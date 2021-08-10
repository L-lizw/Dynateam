/**
 * CrossGeneralServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.e10IntegrateService;

public class CrossGeneralServiceLocator extends org.apache.axis.client.Service implements dyna.app.service.brs.erpi.e10IntegrateService.CrossGeneralService {

    public CrossGeneralServiceLocator() {
    }


    public CrossGeneralServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CrossGeneralServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_ICrossGeneralService
    private java.lang.String BasicHttpBinding_ICrossGeneralService_address = "http://192.168.100.93:8012/Cross";

    public java.lang.String getBasicHttpBinding_ICrossGeneralServiceAddress() {
        return BasicHttpBinding_ICrossGeneralService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_ICrossGeneralServiceWSDDServiceName = "BasicHttpBinding_ICrossGeneralService";

    public java.lang.String getBasicHttpBinding_ICrossGeneralServiceWSDDServiceName() {
        return BasicHttpBinding_ICrossGeneralServiceWSDDServiceName;
    }

    public void setBasicHttpBinding_ICrossGeneralServiceWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_ICrossGeneralServiceWSDDServiceName = name;
    }

    public dyna.app.service.brs.erpi.e10IntegrateService.ICrossGeneralService getBasicHttpBinding_ICrossGeneralService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_ICrossGeneralService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_ICrossGeneralService(endpoint);
    }

    public dyna.app.service.brs.erpi.e10IntegrateService.ICrossGeneralService getBasicHttpBinding_ICrossGeneralService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            dyna.app.service.brs.erpi.e10IntegrateService.BasicHttpBinding_ICrossGeneralServiceStub _stub = new dyna.app.service.brs.erpi.e10IntegrateService.BasicHttpBinding_ICrossGeneralServiceStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_ICrossGeneralServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_ICrossGeneralServiceEndpointAddress(java.lang.String address) {
        BasicHttpBinding_ICrossGeneralService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (dyna.app.service.brs.erpi.e10IntegrateService.ICrossGeneralService.class.isAssignableFrom(serviceEndpointInterface)) {
                dyna.app.service.brs.erpi.e10IntegrateService.BasicHttpBinding_ICrossGeneralServiceStub _stub = new dyna.app.service.brs.erpi.e10IntegrateService.BasicHttpBinding_ICrossGeneralServiceStub(new java.net.URL(BasicHttpBinding_ICrossGeneralService_address), this);
                _stub.setPortName(getBasicHttpBinding_ICrossGeneralServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BasicHttpBinding_ICrossGeneralService".equals(inputPortName)) {
            return getBasicHttpBinding_ICrossGeneralService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "CrossGeneralService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_ICrossGeneralService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_ICrossGeneralService".equals(portName)) {
            setBasicHttpBinding_ICrossGeneralServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
