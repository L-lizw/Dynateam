package dyna.app.service.brs.erpi.wfIntegrateService;

public class WFPLMServiceSoapProxy implements WFPLMServiceSoap
{
	private String				_endpoint			= null;
	private WFPLMServiceSoap	wFPLMServiceSoap	= null;

	public WFPLMServiceSoapProxy()
	{
		_initWFPLMServiceSoapProxy();
	}

	public WFPLMServiceSoapProxy(String endpoint)
	{
		_endpoint = endpoint;
		_initWFPLMServiceSoapProxy();
	}

	private void _initWFPLMServiceSoapProxy()
	{
		try
		{
			wFPLMServiceSoap = new WFPLMServiceLocator().getWFPLMServiceSoap();
			if (wFPLMServiceSoap != null)
			{
				if (_endpoint != null)
				{
					((javax.xml.rpc.Stub) wFPLMServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				}
				else
				{
					_endpoint = (String) ((javax.xml.rpc.Stub) wFPLMServiceSoap)
							._getProperty("javax.xml.rpc.service.endpoint.address");
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
		if (wFPLMServiceSoap != null)
		{
			((javax.xml.rpc.Stub) wFPLMServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
		}

	}

	public WFPLMServiceSoap getWFPLMServiceSoap()
	{
		if (wFPLMServiceSoap == null)
		{
			_initWFPLMServiceSoapProxy();
		}
		return wFPLMServiceSoap;
	}

	@Override
	public java.lang.String XMLAdapter(java.lang.String sXML) throws java.rmi.RemoteException
	{
		if (wFPLMServiceSoap == null)
		{
			_initWFPLMServiceSoapProxy();
		}
		return wFPLMServiceSoap.XMLAdapter(sXML);
	}

}