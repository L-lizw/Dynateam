/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERP集成服务类型
 * wangweixia 2012-3-12
 */
package dyna.common.systemenum;

/**
 * ERP集成服务类型
 * 
 * @author wangweixia
 * 
 */
public enum ERPServerType
{
	ERPYF("ID_SYS_ERPSERVERTYPE_YF", "tmyf.png", "YIFEI"), // 易飞ERP
	ERPE10("ID_SYS_ERPSERVERTYPE_E10", "tme10.png", "E10"),
//	ERPYT("ID_SYS_ERPSERVERTYPE_YT", "tmyt.png", "TOPGP"), // 易拓ERP
	ERPTIPTOP("ID_SYS_ERPSERVERTYPE_TIPTOP", "TIPTOPGP.png", "TIPTOP"), // TipTop ERP
	ERPT100("ID_SYS_ERPSERVERTYPE_T100", "T100.png", "T100"), // T100
	ERPWORKFLOW("ID_SYS_ERPSERVERTYPE_WORKFLOW", "tmwf.png", "WFGP"), // WorkFlow ERP
	ERPSM("ID_SYS_ERPSERVERTYPE_SMART", "tmsmart.png", "SMERP"), // smart ERP
	ERPT100DB("ID_SYS_ERPSERVERTYPE_T100_DB", "T100DB.png", "T100DB"), // T100 DBLINK
	;

	private String	msrId	= null;
	private String	picName	= null;
	private String	proName	= null;

	private ERPServerType(String msrId, String picName, String proName)
	{
		this.msrId = msrId;
		this.picName = picName;
		this.proName = proName;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return msrId;
	}

	/**
	 * @param msrId
	 *            the msrId to set
	 */
	public void setMsrId(String msrId)
	{
		this.msrId = msrId;
	}

	/**
	 * @return the picName
	 */
	public String getPicName()
	{
		return picName;
	}

	/**
	 * @param picName
	 *            the picName to set
	 */
	public void setPicName(String picName)
	{
		this.picName = picName;
	}

	/**
	 * @return the proName
	 */
	public String getProName()
	{
		return proName;
	}

	/**
	 * @return the proName
	 */
	public void setProName(String proName)
	{
		this.proName = proName;
	}

}
