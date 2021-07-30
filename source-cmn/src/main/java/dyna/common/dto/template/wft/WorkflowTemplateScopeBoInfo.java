/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateScopeBo
 * WangLHB Jun 25, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
@Cache
public class WorkflowTemplateScopeBoInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7890082245736879998L;

	public static final String	WFTEMPLATEFK		= "WFTEMPLATEFK";

	public static final String	BOGUID				= "BOGUID";

	public static final String	BONAME				= "BONAME";

	public static final String	BOTITLE				= "BOTITLE";

	public static final String	CANLAUNCH			= "CANLAUNCH";

	/**
	 * @param CANLAUNCH
	 *            the CANLAUNCH to get
	 */
	public boolean canLaunch()
	{
		Boolean value = BooleanUtils.getBooleanByYN((String) this.get(CANLAUNCH));
		return value == null ? false : value.booleanValue();
	}

	/**
	 * @param CANLAUNCH
	 *            the CANLAUNCH to set
	 */
	public void setCanLaunch(boolean canLaunch)
	{
		this.put(CANLAUNCH, BooleanUtils.getBooleanStringYN(canLaunch));
	}

	/**
	 * @return the wftemplatefk
	 */
	public String getWFTemplatefk()
	{
		return (String) this.get(WFTEMPLATEFK);
	}

	/**
	 * @param wftemplatefk
	 *            the wftemplatefk to set
	 */
	public void setWFTemplatefk(String wfTemplatefk)
	{
		this.put(WFTEMPLATEFK, wfTemplatefk);
	}

	/**
	 * @return the boTitle
	 */
	public String getBOTitle()
	{
		return (String) this.get(BOTITLE);
	}

	/**
	 * @return the title by language
	 */
	public String getBOTitle(LanguageEnum lang)
	{
		String boTitle2 = this.getBOTitle();
		return StringUtils.getMsrTitle(boTitle2, lang.getType());
	}

	/**
	 * @param boTitle
	 *            the boTitle to set
	 */
	public void setBOTitle(String boTitle)
	{
		this.put(BOTITLE, boTitle);
	}

	/**
	 * @return the boName
	 */
	public String getBOName()
	{
		return (String) this.get(BONAME);
	}

	/**
	 * @param boGuid
	 *            the boGuid to set
	 */
	public void setBOName(String boName)
	{
		this.put(BONAME, boName);
	}

	/**
	 * @return the boGuid
	 */
	public String getBOGuid()
	{
		return (String) this.get(BOGUID);
	}

	/**
	 * @param boGuid
	 *            the boGuid to set
	 */
	public void setBOGuid(String boGuid)
	{
		this.put(BOGUID, boGuid);
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(WFTEMPLATEFK);
	}
}
