/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CrossIntegrate
 * wangweixia 2012-3-25
 */
package dyna.common.dto.erp;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 用于cross集成产品信息
 * 
 * @author wangweixia
 * 
 */
public class CrossIntegrate extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1158001430594964641L;
	// 需集成产品配置信息：产品名
	public static final String	SERVICE_PROD		= "SERVICE_PROD";
	// 需集成产品配置信息：服务IP
	public static final String	SERVICE_IP			= "SERVICE_IP";
	// public static final String SERVICE_NAME = "SERVICE_NAME";
	// 需集成产品配置信息：ID
	public static final String	SERVICE_ID			= "SERVICE_ID";
	// 需集成产品配置信息：服务版本
	public static final String	SERVICE_VER			= "SERVICE_VER";
	// 产品唯一识别码
	public static final String	UID					= "UID";
	// 在EAI平台注册的产品的RESTful接口地址
	public static final String	RESTURL				= "RESTURL";

	public void setServiceProd(String serviceProd)
	{
		put(SERVICE_PROD, serviceProd);
	}

	public String getServiceProd()
	{
		return (String) this.get(SERVICE_PROD);
	}

	public void setServiceID(String serviceID)
	{
		put(SERVICE_ID, serviceID);
	}

	public String getServiceID()
	{
		return (String) this.get(SERVICE_ID);
	}

	public void setServiceVer(String serviceVer)
	{
		put(SERVICE_VER, serviceVer);
	}

	public String getServiceVer()
	{
		return (String) this.get(SERVICE_VER);
	}

	public void setServiceIP(String ServiceIP)
	{
		put(SERVICE_IP, ServiceIP);
	}

	public String getServiceIP()
	{
		return (String) this.get(SERVICE_IP);
	}

	public void setUID(String uid)
	{
		put(UID, uid);
	}

	public String getUid()
	{
		return (String) this.get(UID);
	}

	public void setRestUrl(String restUrl)
	{
		put(RESTURL, restUrl);
	}

	public String getRestUrl()
	{
		return (String) this.get(RESTURL);
	}
}
