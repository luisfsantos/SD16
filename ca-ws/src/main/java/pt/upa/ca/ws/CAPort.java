package pt.upa.ca.ws;

import pt.upa.ca.exceptions.UnavailableCertificateException;


import javax.jws.WebService;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Properties;



@WebService(
	    endpointInterface="pt.upa.ca.ws.CAPortType",
	    name="CAWebService",
	    portName="CAPort",
	    targetNamespace="http://ws.ca.upa.pt/",
	    serviceName="CAService"
	)
public class CAPort implements CAPortType {
	private static final String PROPERTIES_FILE = "config.properties";
	private static Properties PROPS;
	private static KeyStore ks;
	private static String JKSPASSWORD;
	private static String JKS_PATH;

	public CAPort() throws Exception {
		PROPS = new Properties();
		try {
			PROPS.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", PROPERTIES_FILE);
			System.out.println(msg);
		}
		JKS_PATH = PROPS.getProperty("JKS.PATH");
		JKSPASSWORD = PROPS.getProperty("JKS.PASSWORD");
		ks = loadKeyStore();
	}


	public String ping(String name) {return name;}

	public byte[] requestCertificate(String name) throws UnavailableCertificateException {
		byte [] encodedCertificate = null;
		try {
			X509Certificate certificate = (X509Certificate) ks.getCertificate(name.toLowerCase());
			encodedCertificate = certificate.getEncoded();
		} catch (KeyStoreException e) {
			throw new UnavailableCertificateException("Certificate not found", e);
		} catch (CertificateEncodingException e) {
			throw new UnavailableCertificateException("Certificate can not be encoded", e);
		}
		return encodedCertificate;
	}

	private static KeyStore loadKeyStore() throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		java.io.FileInputStream fis = null;
		try {
			fis = new java.io.FileInputStream(JKS_PATH);
			ks.load(fis, JKSPASSWORD.toCharArray());
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return ks;
	}
}