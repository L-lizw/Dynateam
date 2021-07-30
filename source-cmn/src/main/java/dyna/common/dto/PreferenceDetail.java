/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PreferenceDetail
 * Caogc 2010-10-08
 */
package dyna.common.dto;

import java.math.BigDecimal;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 
 * @author Caogc
 * 
 */
public class PreferenceDetail extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5466625404445108685L;

	public static final String	GUID				= "GUID";
	public static final String	MASTERFK			= "MASTERFK";
	public static final String	SEQUENCE			= "DATASEQ";
	public static final String	VALUE				= "ITEMVALUE";
	public static final String	BMGUID				= "BMGUID";

	/**
	 * @return the bmGuid
	 */
	public String getBMGuid()
	{
		return (String) this.get(BMGUID);
	}

	/**
	 * @param bmGuid
	 *            the bmGuid to set
	 */
	public void setBMGuid(String bmGuid)
	{
		this.put(BMGUID, bmGuid);
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
	 * @return the sequence
	 */
	public Integer getSequence()
	{
		Object object = this.get(SEQUENCE);
		if (object instanceof BigDecimal)
		{
			return ((BigDecimal) object).intValue();
		}
		else if (object instanceof Integer)
		{
			return (Integer) this.get(SEQUENCE);
		}
		else if (object instanceof Number)
		{
			return ((Number) this.get(SEQUENCE)).intValue();
		}

		return 0;
	}

	/**
	 * @return the VALUE
	 */
	public String getValue()
	{
		return (String) this.get(VALUE);
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

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, sequence);
	}

	/**
	 * @param VALUE
	 *            the VALUE to set
	 */
	public void setValue(String value)
	{
		this.put(VALUE, value);
	}
}
