/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CrossRequestModel
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Element;

/**
 * @author WangLHB
 * 
 */
public class CrossRequestModel extends AbstractCrossRequestModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1697744876956673790L;

	@Element(name = "payload", required = false)
	private String	paramStr			= null;

	public String getParamStr() 
	{
		return paramStr;
	}

	public void setParamStr(String paramStr) 
	{
		this.paramStr = paramStr;
	}

	@Override
	public String getXMLWithoutCrossInfo()
	{
		return getParamStr();
	}
}
