package pt.upa.ws.handler;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by lads on 09-05-2016.
 */
public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
    private static final String PROPERTIES_FILE = "auth.properties";
    private static Properties PROPS;
    private static String JKSPASSWORD;
    private static String PRIVKEYPASS;
    private static String COMPANY_NAME;
    private static String JKS_PATH;

    private void init() {
        PROPS = new Properties();
        try {
            PROPS.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", PROPERTIES_FILE);
            System.out.println(msg);
        }
        COMPANY_NAME = PROPS.getProperty("COMPANY.NAME");
        JKS_PATH = PROPS.getProperty("JKS.PATH");
        JKSPASSWORD = PROPS.getProperty("JKS.PASSWORD");
        PRIVKEYPASS = PROPS.getProperty("JKS.PRIVKEY");
    }


    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        if (COMPANY_NAME == null) {
            this.init();
        }

        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                return handleOutBound(smc);
            } else {
                return handleInBound(smc);
            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }


    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }

    private boolean handleOutBound(SOAPMessageContext smc) throws SOAPException {
        System.out.println("Writing header in outbound SOAP message...");

        // get SOAP envelope
        SOAPMessage msg = smc.getMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();

        System.out.println("Generating TimeStamp");
        //Make the created date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String createdDate = df.format(Calendar.getInstance().getTime());


        // initialize the message digest
        byte [] signedDigest = null;

        // add header
        SOAPHeader sh = se.getHeader();
        if (sh == null)
            sh = se.addHeader();

        System.out.println("Digitally signing the necessary information");
        try {
            signedDigest = makeDigitalSignature(getMessageDigest(se, createdDate).toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add header element (name, namespace prefix, namespace)
        Name name = se.createName("Security", "auth", "http://ws.handler.upa.pt");
        SOAPHeaderElement element = sh.addHeaderElement(name);

        SOAPElement messageDigest = element.addChildElement("MessageDigest", "auth");
        messageDigest.addTextNode(DatatypeConverter.printBase64Binary(signedDigest));

        SOAPElement senderName = element.addChildElement("SenderName", "auth");
        senderName.addTextNode(COMPANY_NAME);

        SOAPElement dateCreated = element.addChildElement("CreatedDate", "auth");
        dateCreated.addTextNode(createdDate);

        return true;

    }

    private boolean handleInBound(SOAPMessageContext smc) throws SOAPException {
        System.out.println("Reading header in inbound SOAP message...");

        // get SOAP envelope header
        SOAPMessage msg = smc.getMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPHeader sh = se.getHeader();
        sh.detachNode();
        // check header
        if (sh == null) {
            System.out.println("Header not found.");
            return false;
        }

        // get first header element
        Name name = se.createName("Security", "auth", "http://ws.handler.upa.pt");
        Iterator it = sh.getChildElements(name);
        // check header element
        if (!it.hasNext()) {
            System.out.println("Header element not found.");
            return false;
        }
        SOAPElement Security = (SOAPElement) it.next();
        it = Security.getChildElements();

        SOAPElement messageDigest = (SOAPElement) it.next();
        SOAPElement senderName = (SOAPElement) it.next();
        SOAPElement createdDate = (SOAPElement) it.next();

        String date = createdDate.getValue();

        try {
            return verifyDigitalSignature(DatatypeConverter.parseBase64Binary(messageDigest.getValue()),
                    getMessageDigest(se, date).toByteArray(), senderName.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;

    }

    // make the byte array for the digest
    private static ByteArrayOutputStream getMessageDigest(SOAPEnvelope se, String createdDate) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] createdDateBytes = createdDate.getBytes("UTF-8");
            baos.write(DatatypeConverter.parseBase64Binary(se.toString()));
            baos.write(createdDateBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos;
    }

    /** auxiliary method to get PrivateKey from jks*/
    private static PrivateKey getPrivateKey() throws Exception {
        KeyStore ks = loadKeyStore();

        PrivateKey key = (PrivateKey)ks.getKey(COMPANY_NAME.toLowerCase(), PRIVKEYPASS.toCharArray());

        return key;
    }

    /** auxiliary method to get PublicKey from jks*/
    private static PublicKey getPublicKey(String alias) throws Exception {
        KeyStore ks = loadKeyStore();
        PublicKey key = null;
        try {
            Certificate cert = ks.getCertificate(alias.toLowerCase());
            if (cert != null) {
                key = cert.getPublicKey();
            } else {
                //caPort.requestCertificate(alias);

            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return key;
    }

    private static KeyStore loadKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        java.io.FileInputStream fis = null;
        try {
            System.out.println("\n---------------------------------\n"+JKS_PATH+"\n---------------------------------\n");
            fis = new java.io.FileInputStream(JKS_PATH);
            ks.load(fis, JKSPASSWORD.toCharArray());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return ks;
    }

    /** auxiliary method to calculate digest from text and cipher it */
    private static byte[] makeDigitalSignature(byte[] bytes) throws Exception {

        // get a signature object using the SHA-1 and RSA combo
        // and sign the plaintext with the private key
        PrivateKey key = getPrivateKey();
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(key);
        sig.update(bytes);
        byte[] signature = sig.sign();

        return signature;
    }

    /**
     * auxiliary method to calculate new digest from text and compare it to the
     * to deciphered digest
     */
    private static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, String senderAlias) throws Exception {

        // verify the signature with the public key
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(getPublicKey(senderAlias));
        sig.update(bytes);
        try {
            return sig.verify(cipherDigest);
        } catch (SignatureException se) {
            System.err.println("Caught exception while verifying " + se);
            return false;
        }
    }

}
