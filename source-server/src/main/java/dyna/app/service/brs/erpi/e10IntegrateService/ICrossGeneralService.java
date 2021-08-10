/**
 * ICrossGeneralService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.e10IntegrateService;

public interface ICrossGeneralService extends java.rmi.Remote {
    public java.lang.String invokeSrv(java.lang.String xml) throws java.rmi.RemoteException;
    public java.lang.String callbackSrv(java.lang.String xml) throws java.rmi.RemoteException;
    public java.lang.String syncProd(java.lang.String xml) throws java.rmi.RemoteException;
    public java.lang.String invokeMdm(java.lang.String xml) throws java.rmi.RemoteException;
}
