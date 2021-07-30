/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplateEnd2
 * Caogc 2010-9-27
 */
package dyna.common.dto.template.bom;

import java.util.Date;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * 
 * @author JiangHL
 * 
 */
@Cache
public class BOMTemplateEnd2 extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 6875210343280126693L;

	public static final String	GUID				= "GUID";
	public static final String	MASTER_FK			= "MASTERFK";
	public static final String	BM_GUID				= "BMGUID";
	public static final String	END2_BO_TITLE		= "END2BOTITLE";
	public static final String	END2_BO_NAME		= "END2BONAME";
	public static final String	CREATE_USER_GUID	= "CREATEUSERGUID";
	public static final String	CREATE_TIME			= "CREATETIME";

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return (String) this.get(BM_GUID);
	}

	/**
	 * @return the createtime
	 */
	public Date getCreatetime()
	{
		return (Date) this.get(CREATE_TIME);
	}

	/**
	 * @return the createuserGuid
	 */
	@Override
	public String getCreateUserGuid()
	{
		return (String) this.get(CREATE_USER_GUID);
	}

	/**
	 * @return the end2botitle
	 */
	public String getEnd2BoName()
	{
		return (String) this.get(END2_BO_NAME);
	}

	/**
	 * @return the end2botitle
	 */
	public String getEnd2BoTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(END2_BO_TITLE), lang.getType());
	}

	/**
	 * @return the masterfk
	 */
	public String getMasterFK()
	{
		return (String) this.get(MASTER_FK);
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime)
	{
		this.put(CREATE_TIME, createTime);
	}

	/**
	 * @param createUserGuid
	 *            the createUserGuid to set
	 */
	@Override
	public void setCreateUserGuid(String createUserGuid)
	{
		this.put(CREATE_USER_GUID, createUserGuid);
	}

	/**
	 * @param bmGuid
	 *            the bmGuid to set
	 */
	public void setEnd2BoGuid(String bmGuid)
	{
		this.put(BM_GUID, bmGuid);
	}

	/**
	 * @param end2BoName
	 *            the end2BoName to set
	 */
	public void setEnd2BoName(String end2BoName)
	{
		this.put(END2_BO_NAME, end2BoName);
	}

	public void setEnd2BoTitle(String title)
	{
		this.put(END2_BO_TITLE, title);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @param masterFK
	 *            the masterFK to set
	 */
	public void setMasterFK(String masterFK)
	{
		this.put(MASTER_FK, masterFK);
	}
}
