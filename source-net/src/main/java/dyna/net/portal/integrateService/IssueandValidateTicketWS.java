/**
 * IssueandValidateTicketWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.net.portal.integrateService;

public interface IssueandValidateTicketWS extends java.rmi.Remote
{
	public java.lang.String getModulusForXMLRSA() throws java.rmi.RemoteException;

	public java.lang.String getValidatedUser(byte[] arg0, java.lang.String arg1, byte[] arg2)
			throws java.rmi.RemoteException, InvalidSOKException;

	public java.lang.String getValidatedAD(java.lang.String arg0, java.lang.String arg1)
			throws java.rmi.RemoteException;

	public java.lang.String getValidatedUserInBase64(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2)
			throws java.rmi.RemoteException, InvalidSOKException;

	public java.lang.String getPublicExponentForXMLRSA() throws java.rmi.RemoteException;

	public PubKeyInfo getPubKeyInfo() throws java.rmi.RemoteException;
}
