//package ca.ws;
package pt.upa.ca.ws;

import pt.upa.ca.exceptions.UnavailableCertificateException;

import javax.jws.WebService;

@WebService(name = "CAPortType", targetNamespace = "http://ws.ca.upa.pt/")
public interface CAPortType {

	String ping(String name);

	byte[] requestCertificate(String name) throws UnavailableCertificateException;
}
