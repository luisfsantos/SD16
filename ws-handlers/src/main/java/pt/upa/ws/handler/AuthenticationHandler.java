package pt.upa.ws.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.*;
import java.util.Iterator;
import java.util.Set;

import static javafx.scene.input.KeyCode.K;


/**
 * Created by lads on 09-05-2016.
 */
public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
    public static final String COMPANY_NAME_PROPERTY = "company.name";
    private static final String JKSPASSWORD = "ins3cur3";
    private static final String PRIVKEYPASS = "1nsecure";
    private static String COMPANY_NAME;

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        COMPANY_NAME = (String) smc.get(COMPANY_NAME_PROPERTY);
        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        try {
            getPrivateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (outboundElement.booleanValue()) {
                handleOutBound(smc);
            } else {
                handleInBound(smc);
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

    private void handleOutBound(SOAPMessageContext smc) throws SOAPException {
        System.out.println("Writing header in outbound SOAP message...");

        // get SOAP envelope
        SOAPMessage msg = smc.getMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();

        // add header
        SOAPHeader sh = se.getHeader();
        if (sh == null)
            sh = se.addHeader();

        // add header element (name, namespace prefix, namespace)
        Name name = se.createName("auth", "a", "http://ws.handler.upa.pt");
        SOAPHeaderElement element = sh.addHeaderElement(name);

    }

    private void handleInBound(SOAPMessageContext smc) throws SOAPException {
        System.out.println("Reading header in inbound SOAP message...");

        // get SOAP envelope header
        SOAPMessage msg = smc.getMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPHeader sh = se.getHeader();

        // check header
        if (sh == null) {
            System.out.println("Header not found.");
            return;
        }

        // get first header element
        Name name = se.createName("myHeader", "d", "http://demo");
        Iterator it = sh.getChildElements(name);
        // check header element
        if (!it.hasNext()) {
            System.out.println("Header element not found.");
            return;
        }
        SOAPElement element = (SOAPElement) it.next();

        // get header element value
        String valueString = element.getValue();
        int value = Integer.parseInt(valueString);

        // print received header
        System.out.println("Header value is " + value);

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
        PublicKey key;
        try {
             key = (PublicKey) ks.getKey(alias.toLowerCase(), PRIVKEYPASS.toCharArray());
        } catch (KeyStoreException e) {
            //Fixme
            key = null;
        }

        return key;
    }

    private static KeyStore loadKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream("../keys/" + COMPANY_NAME + "/" + COMPANY_NAME + ".jks");
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

    private static byte[] getDigest(byte[] plainBytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(plainBytes);
        byte[] digest = messageDigest.digest();
        return digest;
    }
}
