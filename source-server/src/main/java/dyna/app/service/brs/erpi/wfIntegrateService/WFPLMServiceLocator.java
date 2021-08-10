/**
 * WFPLMServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.wfIntegrateService;

import dyna.common.dto.erp.ERPServiceConfig;

public class WFPLMServiceLocator extends org.apache.axis.client.Service implements WFPLMService
{

	// Use to get a proxy class for WFPLMServiceSoap
	private java.lang.String	WFPLMServiceSoap_address		= "http://192.168.101.83/WFPLM/WFPLMService.asmx";

	// The WSDD service name defaults to the port name.
	private java.lang.String	WFPLMServiceSoapWSDDServiceName	= "WFPLMServiceSoap";

	public WFPLMServiceLocator()
	{
	}

	public WFPLMServiceLocator(org.apache.axis.EngineConfiguration config)
	{
		super(config);
	}

	public WFPLMServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException
	{
		super(wsdlLoc, sName);
	}

	public WFPLMServiceLocator(ERPServiceConfig serverDef)
	{
		if (serverDef != null)
		{
			WFPLMServiceSoap_address = serverDef.getErpServerAddress();
		}
	}

	@Override
	public java.lang.String getWFPLMServiceSoapAddress()
	{
		return WFPLMServiceSoap_address;
	}

	public java.lang.String getWFPLMServiceSoapWSDDServiceName()
	{
		return WFPLMServiceSoapWSDDServiceName;
	}

	public void setWFPLMServiceSoapWSDDServiceName(java.lang.String name)
	{
		WFPLMServiceSoapWSDDServiceName = name;
	}

	@Override
	public WFPLMServiceSoap getWFPLMServiceSoap() throws javax.xml.rpc.ServiceException
	{
		java.net.URL endpoint;
		try
		{
			endpoint = new java.net.URL(WFPLMServiceSoap_address);
		}
		catch (java.net.MalformedURLException e)
		{
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getWFPLMServiceSoap(endpoint);
	}

	@Override
	public WFPLMServiceSoap getWFPLMServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException
	{
		try
		{
			WFPLMServiceSoapStub _stub = new WFPLMServiceSoapStub(portAddress, this);

			_stub.setPortName(getWFPLMServiceSoapWSDDServiceName());
			return _stub;
		}
		catch (org.apache.axis.AxisFault e)
		{
			return null;
		}
	}

	public void setWFPLMServiceSoapEndpointAddress(java.lang.String address)
	{
		WFPLMServiceSoap_address = address;
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
				WFPLMServiceSoapStub _stub = new WFPLMServiceSoapStub(new java.net.URL(WFPLMServiceSoap_address), this);
				_stub.setPortName(getWFPLMServiceSoapWSDDServiceName());
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
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException
	{
		if (portName == null)
		{
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("WFPLMServiceSoap".equals(inputPortName))
		{
			return getWFPLMServiceSoap();
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
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException
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
	public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException
	{
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
