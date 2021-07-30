/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SerialNumber
 * wangweixia 2013-11-11
 */
package dyna.common.bean.data.coding;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 流水号表对应的类
 * 
 * @author wangweixia
 * 
 */
public class SerialNumber extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7800390911503403624L;
	//
	public static final String	CLASSGUID			= "CLASSGUID";
	//
	public static final String	FIELDNAME			= "FIELDNAME";
	//
	public static final String	PRESTR				= "PRESTR";
	//
	public static final String	SUFSTR				= "SUFSTR";
	public static final String	SERIALNOLOCATION	= "SERIALNOLOCATION";
	public static final String	SERIALNO			= "SERIALNO";
	public static final String	INSTANCEGUID		= "INSTANCEGUID";
	public static final String	FULLNUMBER			= "FULLNUMBER";
	public static final String	ISVALID				= "ISVALID";

	/**
	 * @return the classguid
	 */
	public String getClassguid()
	{
		return (String) super.get(CLASSGUID);
	}

	public void setClassguid(String classguid)
	{
		super.put(CLASSGUID, classguid);
	}

	/**
	 * @return the fieldname
	 */
	public String getFieldname()
	{
		return (String) super.get(FIELDNAME);
	}

	public void setFieldname(String name)
	{
		super.put(FIELDNAME, name);
	}

	/**
	 * @return the prestr
	 */
	public String getPrestr()
	{
		return (String) super.get(PRESTR);
	}

	public void setPrestr(String preStr)
	{
		super.put(PRESTR, preStr);
	}

	/**
	 * @return the sufstr
	 */
	public String getSufstr()
	{
		return (String) super.get(SUFSTR);
	}

	public void setSufstr(String sufStr)
	{
		super.put(SUFSTR, sufStr);
	}

	/**
	 * @return the serialnolocation
	 */
	public String getSerialnolocation()
	{
		return (String) super.get(SERIALNOLOCATION);
	}

	public void setSerialnolocation(String serialNolation)
	{
		super.put(SERIALNOLOCATION, serialNolation);

	}

	/**
	 * @return the serialno
	 */
	public String getSerialno()
	{
		return (String) super.get(SERIALNO);
	}

	public void setSerialno(String serialNo)
	{
		super.put(SERIALNO, serialNo);
	}

	/**
	 * @return the instanceguid
	 */
	public String getInstanceguid()
	{
		return (String) super.get(INSTANCEGUID);
	}

	public void setInstanceguid(String instanceGuid)
	{
		super.put(INSTANCEGUID, instanceGuid);
	}

	/**
	 * @return the fullnumber
	 */
	public String getFullnumber()
	{
		return (String) super.get(FULLNUMBER);
	}

	public void setFullnumber(String number)
	{
		super.put(FULLNUMBER, number);
	}

	/**
	 * @return the isvalid
	 */
	public String getIsValid()
	{
		return (String) super.get(ISVALID);
	}

	public void setValid(String valid)
	{
		super.put(ISVALID, valid);
	}
}
