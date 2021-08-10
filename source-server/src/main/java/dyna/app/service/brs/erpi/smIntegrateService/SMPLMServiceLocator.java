/**
 * SMPLMServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.smIntegrateService;

import dyna.app.service.brs.erpi.wfIntegrateService.WFPLMServiceSoap;
import dyna.common.dto.erp.ERPServiceConfig;

public class SMPLMServiceLocator extends org.apache.axis.client.Service implements SMPLMService
{

	// Use to get a proxy class for WFPLMServiceSoap
	private java.lang.String	SMPLMServiceSoap_address		= "http://192.168.101.83/WFPLM/WFPLMService.asmx";

	// The WSDD service name defaults to the port name.
	private java.lang.String	SMPLMServiceSoapWSDDServiceName	= "SMPLMServiceSoap";

	public SMPLMServiceLocator()
	{
	}

	public SMPLMServiceLocator(org.apache.axis.EngineConfiguration config)
	{
		super(config);
	}

	public SMPLMServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException
	{
		super(wsdlLoc, sName);
	}

	public SMPLMServiceLocator(ERPServiceConfig serverDef)
	{
		if (serverDef != null)
		{
			SMPLMServiceSoap_address = serverDef.getErpServerAddress();
		}
	}

	@Override
	public java.lang.String getSMPLMServiceSoapAddress()
	{
		return SMPLMServiceSoap_address;
	}

	public java.lang.String getSMPLMServiceSoapWSDDServiceName()
	{
		return SMPLMServiceSoapWSDDServiceName;
	}

	public void setSMPLMServiceSoapWSDDServiceName(java.lang.String name)
	{
		SMPLMServiceSoapWSDDServiceName = name;
	}

	@Override
	public SMPLMServiceSoap getSMPLMServiceSoap() throws javax.xml.rpc.ServiceException
	{
		java.net.URL endpoint;
		try
		{
			endpoint = new java.net.URL(SMPLMServiceSoap_address);
		}
		catch (java.net.MalformedURLException e)
		{
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSMPLMServiceSoap(endpoint);
	}

	@Override
	public SMPLMServiceSoap getSMPLMServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException
	{
		try
		{
			SMPLMServiceSoapStub _stub = new SMPLMServiceSoapStub(portAddress, this);

			_stub.setPortName(getSMPLMServiceSoapWSDDServiceName());
			return _stub;
		}
		catch (org.apache.axis.AxisFault e)
		{
			return null;
		}
	}

	public void setWFPLMServiceSoapEndpointAddress(java.lang.String address)
	{
		SMPLMServiceSoap_address = address;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	@Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
	{
		try
		{
			if (WFPLMServiceSoap.class.isAssignableFrom(serviceEndpointInterface))
			{
				SMPLMServiceSoapStub _stub = new SMPLMServiceSoapStub(new java.net.URL(SMPLMServiceSoap_address), this);
				_stub.setPortName(getSMPLMServiceSoapWSDDServiceName());
				return _stub;
			}
		}
		catch (java.lang.Throwable t)
		{
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
				+ (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	@Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException
	{
		if (portName == null)
		{
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("WFPLMServiceSoap".equals(inputPortName))
		{
			return getSMPLMServiceSoap();
		}
		else
		{
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	@Override
	public javax.xml.namespace.QName getServiceName()
	{
		return new javax.xml.namespace.QName("http://tempuri.org/", "WFPLMService");
	}

	private java.util.HashSet	ports	= null;

	@Override
	public java.util.Iterator getPorts()
	{
		if (ports == null)
		{
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "WFPLMServiceSoap"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException
	{

		if ("WFPLMServiceSoap".equals(portName))
		{
			setWFPLMServiceSoapEndpointAddress(address);
		}
		else
		{ // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException
	{
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
