/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SaQueryPreferenceDetail
 * duanll 2013-1-17
 */
package dyna.common.dto;

import java.math.BigDecimal;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

/**
 * @author duanll
 *         高级搜索自定义栏位明细字段
 */
public class SaQueryPreferenceDetail extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -5823574809902566778L;

	/**
	 * 用户唯一标识
	 */
	public static final String	MASTERFK			= "MASTERFK";

	/**
	 * 用户唯一标识
	 */
	public static final String	CUSTOMFIELD			= "CUSTOMFIELD";

	/**
	 * 用户唯一标识
	 */
	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	FROMCLASS			= "FROMCLASS";

	/**
	 * @return the masterfk
	 */
	public String getMasterfk()
	{
		return (String) super.get(MASTERFK);
	}

	/**
	 * @param masterfk
	 *            the masterfk to set
	 */
	public void setMasterfk(String masterfk)
	{
		super.put(MASTERFK, masterfk);
	}

	/**
	 * @return the customField
	 */
	public String getCustomField()
	{
		return (String) super.get(CUSTOMFIELD);
	}

	/**
	 * @param customField
	 *            the customField to set
	 */
	public void setCustomField(String customField)
	{
		super.put(CUSTOMFIELD, customField);
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence()
	{
		Number sequence = (Number) super.get(SEQUENCE);
		if (sequence == null)
		{
			return 0;
		}
		return sequence.intValue();
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(Integer sequence)
	{
		if (sequence == null)
		{
			return;
		}
		super.put(SEQUENCE, BigDecimal.valueOf(sequence));
	}

	/**
	 * @return the sequence
	 */
	public boolean getFromClass()
	{
		Object object = super.get(FROMCLASS);
		if (object == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByValue((String) object);
	}

	/**
	 * @param FROMCLASS
	 *            the FROMCLASS to set
	 */
	public void setFromClass(boolean fromClass)
	{
		super.put(FROMCLASS, BooleanUtils.getBooleanStringYN(fromClass));
	}
}
