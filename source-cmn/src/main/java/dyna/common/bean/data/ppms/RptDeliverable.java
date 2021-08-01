/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptDeliverableMapper;
import dyna.common.util.DateFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptDeliverableMapper.class)
public class RptDeliverable extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -513335952439323894L;
	public static final String	ID					= "MD_ID";
	public static final String	NAME				= "MD_NAME";
	public static final String	STATUS				= "STATUS";
	public static final String	BOTITLE				= "BOTITLE";
	public static final String	CLASSIFICATION		= "CLASSIFICATION";
	public static final String	CREATEUSER			= "CREATEUSER";
	public static final String	SUBMITTEDTIME		= "SUBMITTEDTIME";
	public static final String	CREATIONTIME		= "CREATIONTIME";
	public static final String	PRJNAME				= "PRJNAME";
	public static final String	GUID				= "GUID";
	public static final String	CLASSNAME			= "CLASSNAME";
	public static final String	SUMOFRECORDS		= "SUMOFRECORDS";
	public static final String	PRJID				= "PRJID";

	public static final String	CLASSIFICATIONGUID	= "CLASSIFICATIONGUID";
	public static final String	CREATEUSERGUID		= "CREATEUSERGUID";
	public static final String	CLASSGUID			= "CLASSGUID";

	public String getClassName()
	{
		return (String) super.get(CLASSNAME);
	}

	public void setClassName(String className)
	{
		super.put(CLASSNAME, className);
	}

	@Override
	public String getGuid()
	{
		return (String) super.get(GUID);
	}

	@Override
	public void setGuid(String guid)
	{
		super.put(GUID, guid);
	}

	public String getSumOfRecords()
	{
		if (super.get(SUMOFRECORDS) != null)
		{
			if (super.get(SUMOFRECORDS) instanceof BigDecimal)
			{
				return String.valueOf(super.get(SUMOFRECORDS));
			}
		}
		return (String) super.get(SUMOFRECORDS);
	}

	public void setSumOfRecords(String sumOfRecords)
	{
		super.put(SUMOFRECORDS, sumOfRecords);
	}

	@Override
	public String getCreateUser()
	{
		return (String) super.get(CREATEUSER);
	}

	public void setCreateUser(String createUser)
	{
		super.put(CREATEUSER, createUser);
	}

	public String getClassification()
	{
		return (String) super.get(CLASSIFICATION);
	}

	public void setClassification(String classification)
	{
		super.put(CLASSIFICATION, classification);
	}

	public String getBOTitle()
	{
		return (String) super.get(BOTITLE);
	}

	public void setBOTitle(String type)
	{
		super.put(BOTITLE, type);
	}

	public String getId()
	{
		return (String) super.get(ID);
	}

	public void setId(String id)
	{
		super.put(ID, id);
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}

	public String getStatus()
	{
		return (String) super.get(STATUS);
	}

	public void setStatus(String status)
	{
		super.put(STATUS, status);
	}

	public String getSubmittedTime()
	{
		if (super.get(SUBMITTEDTIME) != null && super.get(SUBMITTEDTIME) instanceof Date)
		{
			Date submittime = (Date) super.get(SUBMITTEDTIME);
			return DateFormat.formatYMD(submittime);
		}
		else if (super.get(SUBMITTEDTIME) != null && super.get(SUBMITTEDTIME) instanceof Timestamp)
		{
			Timestamp submittime = (Timestamp) super.get(SUBMITTEDTIME);
			return DateFormat.formatYMD(submittime);
		}
		return (String) super.get(SUBMITTEDTIME);
	}

	public void setSubmittedTime(String submittedTime)
	{
		super.put(SUBMITTEDTIME, submittedTime);
	}

	public String getCreationTime()
	{
		if (super.get(CREATIONTIME) != null && super.get(CREATIONTIME) instanceof Date)
		{
			Date createTime = (Date) super.get(CREATIONTIME);
			return DateFormat.formatYMD(createTime);
		}
		return (String) super.get(CREATIONTIME);
	}

	public void setCreationTime(String creatTime)
	{
		super.put(CREATIONTIME, creatTime);
	}

	public String getPrjName()
	{
		return (String) super.get(PRJNAME);
	}

	public void setPrjName(String prjName)
	{
		super.put(PRJNAME, prjName);
	}

	public String getPrjID()
	{
		return (String) super.get(PRJID);
	}

	public void setPrjID(String prjName)
	{
		super.put(PRJID, prjName);
	}

	public RptDeliverable()
	{
		super();
	}

}
