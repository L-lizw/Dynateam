/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplateEnd2
 * Caogc 2010-8-31
 */
package dyna.common.dto.template.relation;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.relation.RelationTemplateEnd2Mapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * 
 * @author Caogc
 *
 */
@Cache
@EntryMapper(RelationTemplateEnd2Mapper.class)
public class RelationTemplateEnd2 extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5466625404445108685L;

	public static final String	GUID				= "GUID";
	public static final String	MASTERFK			= "MASTERFK";
	public static final String	BM_GUID				= "BMGUID";
	public static final String	END2_BO_TITLE		= "END2BOTITLE";
	public static final String	END2_BO_NAME		= "END2BONAME";
	public static final String	CREATE_USER_GUID	= "CREATEUSERGUID";
	public static final String	CREATE_USER_NAME	= "CREATEUSERNAME";
	public static final String	CREATE_TIME			= "CREATETIME";

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return (String) this.get(BM_GUID);
	}

	/**
	 * @return the CreateTime
	 */
	@Override
	public Date getCreateTime()
	{
		return (Date) this.get(CREATE_TIME);
	}

	/**
	 * @return the createUserGuid
	 */
	@Override
	public String getCreateUserGuid()
	{
		return (String) this.get(CREATE_USER_GUID);
	}

	/**
	 * @return the createUserName
	 */
	public String getCreateUserName()
	{
		return (String) this.get(CREATE_USER_NAME);
	}

	/**
	 * @return the end2boname
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
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the masterFK
	 */
	public String getMasterFK()
	{
		return (String) this.get(MASTERFK);
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
	 * @param createUserName
	 *            the createUserName to set
	 */
	public void setCreateUserName(String createUserName)
	{
		this.put(CREATE_USER_NAME, createUserName);
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
		this.put(MASTERFK, masterFK);
	}
}
