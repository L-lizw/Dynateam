/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERP服务配置
 * caogc 2010-08-23
 */
package dyna.common.dto.erp;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.erp.ERPServiceConfigMapper;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * 
 * @author caogc
 * 
 */
@EntryMapper(ERPServiceConfigMapper.class)
public class ERPServiceConfig extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID		= -8739956338174888435L;
	// 服务的Guid
	public static final String	ERP_SERVER_GUID			= "GUID";
	// 所选的ERP服务：易飞ERP/易拓ERP/WorkFlow ERP/TipTop ERP
	public static final String	ERP_SERVER_SELECTED		= "ERPTYPE";
	// ERP服务配置名称，唯一标识
	public static final String	CONFIG_NAME				= "TEMPLATENAME";
	//模板是否可用
	public static final String IS_IN_USE				="ISINUSE"; 
	// ERP服务器信息：有ERP服务IP、端口号、ERP服务服务器地址组成
	public static final String	ERP_SERVER_ADDRESS		= "ERPSERVERADDRESS";
	// 易飞ERP服务名称
	public static final String	SERVER_NAME				= "SERVICENAME";
	// SOAP服务
	public static final String	SOAP_SERVER				= "SOAPSERVICE";
	// 命名空间
	public static final String	NAME_PLACE				= "NAMESPACE";
	// ERP服务所在的IP
	public static final String	ERP_SERVER_IP			= "SERVICEIP";
	// ERP服务的端口号
	public static final String	ERP_SERVER_PORT			= "SERVICEPORT";
	// ERP服务器地址
	public static final String	ERP_SERVER_NAME			= "ERPSERVERLOCATION";
	// 是否用cross集成
	public static final String	CROSS_INTEGRATE			= "ISBYCROSS";

	// 导出schemaName
	public static final String	SCHEMANAME				= "SCHEMANAME";
	// 是否制定用户更改公司别
	private static final String	ISMODIFY				= "ISPERMITCUSTOM";
	// 业务模型
	public static final String	BM_GUID					= "BMGUID";

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return (String) this.get(BM_GUID);
	}

	public void setBmGuid(String bmguid)
	{
		put(BM_GUID, bmguid);
	}

	/**
	 * 是否允许弹出框选择营运中心
	 * @return the isModify
	 */
	public boolean isModify()
	{
		if (StringUtils.isNullString((String) get(ISMODIFY)))
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) get(ISMODIFY));

	}

	/**
	 * 设置是否允许弹出框选择营运中心
	 * @param isModify
	 *  
	 */
	public void setModify(boolean isModify)
	{
		put(ISMODIFY, BooleanUtils.getBooleanStringYN(isModify));
	}

	public void setERPServerSelected(String erpSelected)
	{
		put(ERP_SERVER_SELECTED, erpSelected);
	}

	/**
	 * @return the bmGuid
	 */
	public String getSchemaName()
	{
		return (String) this.get(SCHEMANAME);
	}

	public void setSchemaName(String schemaName)
	{
		put(SCHEMANAME, schemaName);
	}

	public String getERPServerSelected()
	{
		return (String) this.get(ERP_SERVER_SELECTED);
	}

	public void setERPServerName(String erpName)
	{
		put(ERP_SERVER_NAME, erpName);
	}

	public String getERPServerName()
	{
		return (String) this.get(ERP_SERVER_NAME);
	}

	public void setERPServerPort(String erpPort)
	{
		put(ERP_SERVER_PORT, erpPort);
	}

	public String getERPServerPort()
	{
		return (String) this.get(ERP_SERVER_PORT);
	}

	public void setERPServerIP(String erpIP)
	{
		put(ERP_SERVER_IP, erpIP);
	}

	public String getERPServerIP()
	{
		return (String) this.get(ERP_SERVER_IP);
	}

	public void setCrossIntegrate(String crossIntegrate)
	{

		put(CROSS_INTEGRATE, crossIntegrate);
	}

	public String isCrossIntegrate()
	{
		return (String) this.get(CROSS_INTEGRATE);
	}

	/**
	 * @return the configName
	 */
	public String getConfigName()
	{
		return (String) this.get(CONFIG_NAME);
	}

	/**
	 * @return the erpServerAddress
	 */
	public String getErpServerAddress()
	{
		return (String) this.get(ERP_SERVER_ADDRESS);
	}

	/**
	 * @return the namePlace
	 */
	public String getNamePlace()
	{
		return (String) this.get(NAME_PLACE);
	}

	/**
	 * @return the serverName
	 */
	public String getServerName()
	{
		return (String) this.get(SERVER_NAME);
	}

	/**
	 * @return the soapServer
	 */
	public String getSoapServer()
	{
		return (String) this.get(SOAP_SERVER);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	public void setConfigName(String configName)
	{
		put(CONFIG_NAME, configName);
	}

	/**
	 * @param erpServerAddress
	 *            the erpServerAddress to set
	 */
	public void setErpServerAddress(String erpServerAddress)
	{
		put(ERP_SERVER_ADDRESS, erpServerAddress);
	}

	/**
	 * @param namePlace
	 *            the namePlace to set
	 */
	public void setNamePlace(String namePlace)
	{
		put(NAME_PLACE, namePlace);
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(String serverName)
	{
		put(SERVER_NAME, serverName);
	}

	/**
	 * @param soapServer
	 *            the soapServer to set
	 */
	public void setSoapServer(String soapServer)
	{
		put(SOAP_SERVER, soapServer);
	}

	public void setIsInUse(String inUse){
		this.put(IS_IN_USE, inUse);
	}
	
	public String getIsInUse(){
		return (String) this.get(IS_IN_USE);
	}
	
	@Override
	public String getName()
	{
		return (String) super.get(CONFIG_NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(CONFIG_NAME,name);
	}
}
