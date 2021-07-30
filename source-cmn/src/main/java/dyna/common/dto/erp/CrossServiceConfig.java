/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERP服务配置
 * caogc 2010-08-23
 */
package dyna.common.dto.erp;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * cross服务配置
 * 
 * @author caogc
 * 
 */
public class CrossServiceConfig extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -8739956338174888435L;
	public static final String	PRODUCT_NAME		= "PLM";

	public static final String	CROSS_INTEGRATE		= "CROSS_INTEGRATE";
	// cross服务配置的IP地址
	public static final String	CROSS_SERVER_IP		= "CROSS_SERVER_IP";
	// cross服务配置的端口号
	public static final String	CROSS_SERVER_PORT	= "CROSS_SERVER_PORT";
	// cross服务配置的服务名称
	public static final String	CROSS_SEREVER_NAME	= "CROSS_SEREVER_NAME";

	// WebService信息：
	public static final String	HOST_PROD			= "HOST_PROD";
	// WebService信息： 版本号
	public static final String	HOST_VER			= "HOST_VER";
	// WebService信息：本地主机IP
	public static final String	HOST_IP				= "HOST_IP";

	public static final String	HOST_ACCT			= "HOST_ACCT";
	// WebService信息：使用产品名称
	public static final String	HOST_ID				= "HOST_ID";
	// WebService信息：本地主机端口号
	public static final String	HOST_PORT			= "HOST_PORT";
	// WebService信息：本地主机服务名称
	public static final String	HOST_SERVERNAME		= "HOST_SERVERNAME";
	// 本地主机是否编码
	public static final String	HOST_ISENCODING		= "HOST_ISENCODING";
	// 集成的产品信息
	public List<CrossIntegrate>	SERVICES			= null;
	// 产品EAI平台的RESTful接口地址
	public static final String	CROSS_RERSTURL		= "CROSS_ERSTURL";
	// 产品唯一识别码
	public static final String	UID					= "UID";
	// 在EAI平台注册的产品的RESTful接口地址
	public static final String	RESTURL				= "RESTURL";
	// 参数列表
	public List<CrossParamList>	paramList			= null;

	// public void setHostProd(String hostProd)
	// {
	// put(HOST_PROD, hostProd);
	// }

	// public String getHostProd()
	// {
	// return (String) this.get(HOST_PROD);
	// }

	public void setHostVer(String hostVer)
	{
		put(HOST_VER, hostVer);
	}

	/**
	 * @return the sERVICES
	 */
	public List<CrossIntegrate> getServices()
	{
		return SERVICES;
	}

	/**
	 * @param sERVICES
	 *            the sERVICES to set
	 */
	public void setServices(List<CrossIntegrate> services)
	{
		SERVICES = services;
	}

	public String getHostVer()
	{
		return (String) this.get(HOST_VER);
	}

	public void setHostIP(String hostIP)
	{
		put(HOST_IP, hostIP);
	}

	public String getHostIP()
	{
		return (String) this.get(HOST_IP);
	}

	public void setHostID(String hostID)
	{
		put(HOST_ID, hostID);
	}

	public String getHostID()
	{
		return (String) this.get(HOST_ID);
	}

	public void setHostIsEncode(String isEncode)
	{
		put(HOST_ISENCODING, isEncode);
	}

	public boolean getHostIsEncode()
	{
		return Boolean.valueOf((String) this.get(HOST_ISENCODING));
	}

	public void setHostPort(String hostPort)
	{
		put(HOST_PORT, hostPort);
	}

	public String getHostPort()
	{
		return (String) this.get(HOST_PORT);
	}

	public void setHostServerName(String hostServerName)
	{
		put(HOST_SERVERNAME, hostServerName);
	}

	public String getHostServerName()
	{
		return (String) this.get(HOST_SERVERNAME);
	}

	public void setHostAcct(String hostAcct)
	{
		put(HOST_ACCT, hostAcct);
	}

	public String getHostAcct()
	{
		return (String) this.get(HOST_ACCT);
	}

	public void setCrossServerName(String crossName)
	{
		put(CROSS_SEREVER_NAME, crossName);
	}

	public String getCrossServerName()
	{
		return (String) this.get(CROSS_SEREVER_NAME);
	}

	public void setCrossServerPort(String crossPort)
	{
		put(CROSS_SERVER_PORT, crossPort);
	}

	public String getCrossServerPort()
	{
		return (String) this.get(CROSS_SERVER_PORT);
	}

	public void setCrossServerIP(String crossIP)
	{
		put(CROSS_SERVER_IP, crossIP);
	}

	public String getCrossServerIP()
	{
		return (String) this.get(CROSS_SERVER_IP);
	}

	public void setCrossRestUrl(String restUrl)
	{
		put(CROSS_RERSTURL, restUrl);
	}

	public String getCrossRestUrl()
	{
		return (String) this.get(CROSS_RERSTURL);
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

	public List<CrossParamList> getParamList()
	{
		return paramList;
	}

	public void setParamList(List<CrossParamList> paramList)
	{
		this.paramList = paramList;
	}
}
