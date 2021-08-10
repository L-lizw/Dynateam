package dyna.app.service.brs.erpi.smIntegrateService;

public class SMPLMServiceSoapProxy implements SMPLMServiceSoap
{
	private String				_endpoint			= null;
	private SMPLMServiceSoap	sMPLMServiceSoap	= null;

	public SMPLMServiceSoapProxy()
	{
		_initsMPLMServiceSoapProxy();
	}

	public SMPLMServiceSoapProxy(String endpoint)
	{
		_endpoint = endpoint;
		_initsMPLMServiceSoapProxy();
	}

	private void _initsMPLMServiceSoapProxy()
	{
		try
		{
			sMPLMServiceSoap = new SMPLMServiceLocator().getSMPLMServiceSoap();
			if (sMPLMServiceSoap != null)
			{
				if (_endpoint != null)
				{
					((javax.xml.rpc.Stub) sMPLMServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				}
				else
				{
					_endpoint = (String) ((javax.xml.rpc.Stub) sMPLMServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
				}
			}

		}
		catch (javax.xml.rpc.ServiceException serviceException)
		{
		}
	}

	public String getEndpoint()
	{
		return _endpoint;
	}

	public void setEndpoint(String endpoint)
	{
		_endpoint = endpoint;
		if (sMPLMServiceSoap != null)
		{
			((javax.xml.rpc.Stub) sMPLMServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
		}

	}

	public SMPLMServiceSoap getsMPLMServiceSoap()
	{
		if (sMPLMServiceSoap == null)
		{
			_initsMPLMServiceSoapProxy();
		}
		return sMPLMServiceSoap;
	}

	@Override
	public java.lang.String XMLAdapter(java.lang.String sXML) throws java.rmi.RemoteException
	{
		if (sMPLMServiceSoap == null)
		{
			_initsMPLMServiceSoapProxy();
		}
		return sMPLMServiceSoap.XMLAdapter(sXML);
	}

}