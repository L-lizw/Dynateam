/**
 * IYiFeiGatewayExserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.yfIntegrateService;


@SuppressWarnings("serial")
public class IYiFeiGatewayExserviceLocator extends org.apache.axis.client.Service implements IYiFeiGatewayExservice {

	// Use to get a proxy class for IYiFeiGatewayExPort
	private java.lang.String IYiFeiGatewayExPort_address = "http://192.168.10.113:8082/soap/IYiFeiGatewayEx";


	// The WSDD service name defaults to the port name.
	private java.lang.String IYiFeiGatewayExPortWSDDServiceName = "IYiFeiGatewayExPort";

	@SuppressWarnings("unchecked")
	private java.util.HashSet ports = null;

	public IYiFeiGatewayExserviceLocator()
	{
	}

	public IYiFeiGatewayExserviceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	public IYiFeiGatewayExserviceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public IYiFeiGatewayEx getIYiFeiGatewayExPort() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(this.IYiFeiGatewayExPort_address);
		}
		catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return this.getIYiFeiGatewayExPort(endpoint);
	}

	public IYiFeiGatewayEx getIYiFeiGatewayExPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			IYiFeiGatewayExbindingStub _stub = new IYiFeiGatewayExbindingStub(portAddress, this);
			_stub.setPortName(this.getIYiFeiGatewayExPortWSDDServiceName());
			return _stub;
		}
		catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public java.lang.String getIYiFeiGatewayExPortAddress() {
		return this.IYiFeiGatewayExPort_address;
	}

	public java.lang.String getIYiFeiGatewayExPortWSDDServiceName() {
		return this.IYiFeiGatewayExPortWSDDServiceName;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (IYiFeiGatewayEx.class.isAssignableFrom(serviceEndpointInterface)) {
				IYiFeiGatewayExbindingStub _stub = new IYiFeiGatewayExbindingStub(new java.net.URL(this.IYiFeiGatewayExPort_address), this);
				_stub.setPortName(this.getIYiFeiGatewayExPortWSDDServiceName());
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
	@SuppressWarnings("unchecked")
	@Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return this.getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("IYiFeiGatewayExPort".equals(inputPortName)) {
			return this.getIYiFeiGatewayExPort();
		}
		else  {
			java.rmi.Remote _stub = this.getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public java.util.Iterator getPorts() {
		if (this.ports == null) {
			this.ports = new java.util.HashSet();
			this.ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "IYiFeiGatewayExPort"));
		}
		return this.ports.iterator();
	}

	@Override
	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://tempuri.org/", "IYiFeiGatewayExservice");
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("IYiFeiGatewayExPort".equals(portName)) {
			this.setIYiFeiGatewayExPortEndpointAddress(address);
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
		this.setEndpointAddress(portName.getLocalPart(), address);
	}

	public void setIYiFeiGatewayExPortEndpointAddress(java.lang.String address) {
		this.IYiFeiGatewayExPort_address = address;
	}

	public void setIYiFeiGatewayExPortWSDDServiceName(java.lang.String name) {
		this.IYiFeiGatewayExPortWSDDServiceName = name;
	}

}
