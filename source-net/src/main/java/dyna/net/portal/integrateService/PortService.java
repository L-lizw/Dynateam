/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PortService
 * wangweixia 2012-8-3
 */
package dyna.net.portal.integrateService;

import dyna.common.util.StringUtils;

/**
 * 取得portal接口服务
 * 
 * @author wangweixia
 * 
 */
public class PortService
{
	public String	address	= "http://10.20.86.137:8081/cas-web/WebService/IssueandValidateTicketWS";

	public PortService(String address)
	{
		super();
		if (!StringUtils.isNullString(address))
		{
			this.address = address;
		}
	}

	/**
	 * 取得portal接口
	 * 
	 * @return IssueandValidateTicketWS
	 */
	public IssueandValidateTicketWS portGateway()
	{
		IssueandValidateTicketWS binding = null; // 通信协议

		try
		{
			binding = (IssueandValidateTicketWSImplServiceSoapBindingStub) new IssueandValidateTicketWSImplServiceLocator(
					address).getIssueandValidateTicketWSImplPort();

		}
		catch (javax.xml.rpc.ServiceException jre)
		{
			if (jre.getLinkedCause() != null)
			{
				jre.getLinkedCause().printStackTrace();
			}
			throw new AssertionError("JAX-RPC ServiceException caught: " + jre);
		}

		return binding;
	}

}
