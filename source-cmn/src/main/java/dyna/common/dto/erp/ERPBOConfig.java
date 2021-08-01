/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYFPLMCanUseClass
 * wangweixia 2012-3-13
 */
package dyna.common.dto.erp;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.erp.ERPBOConfigMapper;

/**
 * @author wangweixia
 * 
 */
@EntryMapper(ERPBOConfigMapper.class)
public class ERPBOConfig extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 3212021459608038851L;

	public static String		GUID				= "GUID";
	public static String		TEMPLATE_GUID		= "TEMPLATEGUID";
	public static String		BO_NAME				= "BONAME";
	public static String		BM_GUID				= "BMGUID";
	public static String		BO_TITLE			= "BOTITLE";

	/**
	 * @return the guid
	 */
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @return the masterFk
	 */
	public String getTemplateGguid()
	{
		return (String) this.get(TEMPLATE_GUID);
	}

	/**
	 * @param masterFk
	 *            the masterFk to set
	 */
	public void setTemplateGguid(String templateGguid)
	{
		this.put(TEMPLATE_GUID, templateGguid);
	}

	/**
	 * @return the boName
	 */
	public String getBoName()
	{
		return (String) this.get(BO_NAME);
	}

	/**
	 * @param boName
	 *            the boName to set
	 */
	public void setBoName(String boName)
	{
		this.put(BO_NAME, boName);
	}

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return (String) this.get(BM_GUID);
	}

	/**
	 * @param bmGuid
	 *            the bmGuid to set
	 */
	public void setBmGuid(String bmGuid)
	{
		this.put(BM_GUID, bmGuid);
	}

	/**
	 * @return the boTitle
	 */
	public String getBoTitle()
	{
		return (String) this.get(BO_TITLE);
	}

	/**
	 * @param boTitle
	 *            the boTitle to set
	 */
	public void setBoTitle(String boTitle)
	{
		this.put(BO_TITLE, boTitle);
	}



}
