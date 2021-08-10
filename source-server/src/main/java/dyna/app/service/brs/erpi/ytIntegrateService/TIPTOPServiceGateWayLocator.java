/**
 * TIPTOPServiceGateWayLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.ytIntegrateService;

import dyna.common.dto.erp.ERPServiceConfig;

public class TIPTOPServiceGateWayLocator extends org.apache.axis.client.Service implements TIPTOPServiceGateWay
{
	// http://10.40.40.30/gas2/ws/r/aws_ttsrv2_top3
	// http://10.40.40.30/gas2/ws/r/aws_ttsrv2_tiptop3
	// Use to get a proxy class for TIPTOPServiceGateWayPortType
	private java.lang.String	TIPTOPServiceGateWayPortType_address		= "http://10.40.40.30/gas2/ws/r/aws_ttsrv2_top3";

	// The WSDD service name defaults to the port name.
	private java.lang.String	TIPTOPServiceGateWayPortTypeWSDDServiceName	= "TIPTOPServiceGateWayPortType";

	public TIPTOPServiceGateWayLocator()
	{
	}

	public TIPTOPServiceGateWayLocator(org.apache.axis.EngineConfiguration config)
	{
		super(config);
	}

	public TIPTOPServiceGateWayLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException
	{
		super(wsdlLoc, sName);
	}

	/**
	 * @param serverDef
	 */
	public TIPTOPServiceGateWayLocator(ERPServiceConfig serverDef)
	{

		if (serverDef != null)
		{
			TIPTOPServiceGateWayPortType_address = serverDef.getErpServerAddress();
		}
	}

	@Override
	public java.lang.String getTIPTOPServiceGateWayPortTypeAddress()
	{
		return TIPTOPServiceGateWayPortType_address;
	}

	public java.lang.String getTIPTOPServiceGateWayPortTypeWSDDServiceName()
	{
		return TIPTOPServiceGateWayPortTypeWSDDServiceName;
	}

	public void setTIPTOPServiceGateWayPortTypeWSDDServiceName(java.lang.String name)
	{
		TIPTOPServiceGateWayPortTypeWSDDServiceName = name;
	}

	@Override
	public TIPTOPServiceGateWayPortType getTIPTOPServiceGateWayPortType() throws javax.xml.rpc.ServiceException
	{
		java.net.URL endpoint;
		try
		{
			endpoint = new java.net.URL(TIPTOPServiceGateWayPortType_address);
		}
		catch (java.net.MalformedURLException e)
		{
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getTIPTOPServiceGateWayPortType(endpoint);
	}

	@Override
	public TIPTOPServiceGateWayPortType getTIPTOPServiceGateWayPortType(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException
	{
		try
		{
			TIPTOPServiceGateWayBindingStub _stub = new TIPTOPServiceGateWayBindingStub(portAddress, this);
			_stub.setPortName(getTIPTOPServiceGateWayPortTypeWSDDServiceName());
			return _stub;
		}
		catch (org.apache.axis.AxisFault e)
		{
			return null;
		}
	}

	public void setTIPTOPServiceGateWayPortTypeEndpointAddress(java.lang.String address)
	{
		TIPTOPServiceGateWayPortType_address = address;
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
			if (TIPTOPServiceGateWayPortType.class.isAssignableFrom(serviceEndpointInterface))
			{
				TIPTOPServiceGateWayBindingStub _stub = new TIPTOPServiceGateWayBindingStub(new java.net.URL(
						TIPTOPServiceGateWayPortType_address), this);
				_stub.setPortName(getTIPTOPServiceGateWayPortTypeWSDDServiceName());
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
		if ("TIPTOPServiceGateWayPortType".equals(inputPortName))
		{
			return getTIPTOPServiceGateWayPortType();
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
		return new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"TIPTOPServiceGateWay");
	}

	private java.util.HashSet	ports	= null;

	@Override
	public java.util.Iterator getPorts()
	{
		if (ports == null)
		{
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
					"TIPTOPServiceGateWayPortType"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException
	{

		if ("TIPTOPServiceGateWayPortType".equals(portName))
		{
			setTIPTOPServiceGateWayPortTypeEndpointAddress(address);
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
