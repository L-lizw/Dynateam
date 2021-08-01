/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SaQueryPreferenceDetail
 * duanll 2013-1-17
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.SaCustomColumnsPreferenceDetailMapper;

import java.math.BigDecimal;

/**
 * @author niumr
 *         表格自定义列明细字段
 */
@EntryMapper(SaCustomColumnsPreferenceDetailMapper.class)
public class SaCustomColumnsPreferenceDetail extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -5823574809902566778L;

	/**
	 * 用户唯一标识
	 */
	public static final String	MASTERFK			= "MASTERFK";

	/**
	 * 分类特殊标识符
	 */
	public static final String	CLASSIFICATIONFK	= "CLASSIFICATIONFK";

	public static final String	CLASSFK				= "CLASSFK";

	public static final String	FIELDORIGN			= "FIELDORIGN";

	/**
	 * 表格列字段
	 */
	public static final String	CUSTOMFIELD			= "CUSTOMFIELD";

	/**
	 * 表格列的宽度
	 */
	public static final String	COLUMNLENGTH		= "COLUMNLENGTH";

	/**
	 * 表格列字段的值类型
	 */
	public static final String	VALUETYPE			= "VALUETYPE";

	/**
	 * 表格中列显示的顺序
	 */
	public static final String	SEQUENCE			= "DATASEQ";

	/**
	 * 表格是否有编辑的权限
	 */
	public static final String	ISNEEDEDIT			= "ISNEEDEDIT";

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
	 * @return the classificationfk
	 */
	public String getClassificationFK()
	{
		return (String) super.get(CLASSIFICATIONFK);
	}

	/**
	 * @param classificationfk
	 *            the classificationfk to set
	 */
	public void setClassificationFK(String classificationfk)
	{
		super.put(CLASSIFICATIONFK, classificationfk);
	}

	/**
	 * @return the classfk
	 */
	public String getClassFK()
	{
		return (String) super.get(CLASSFK);
	}

	/**
	 * @param classfk
	 *            the classfk to set
	 */
	public void setClassFK(String classfk)
	{
		super.put(CLASSFK, classfk);
	}

	/**
	 * @return the fieldorign
	 */
	public String getFieldorign()
	{
		return (String) super.get(FIELDORIGN);
	}

	/**
	 * @param fieldorign
	 *            the fieldorign to set
	 */
	public void setFieldorign(String fieldorign)
	{
		super.put(FIELDORIGN, fieldorign);
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
	 * @return the columnLength
	 */
	public String getColumnLength()
	{
		return (String) super.get(COLUMNLENGTH);
	}

	/**
	 * @param valueType
	 *            the valueType to set
	 */
	public void setValueType(String valueType)
	{
		super.put(VALUETYPE, valueType);
	}

	/**
	 * @return the valueType
	 */
	public String getValueType()
	{
		return (String) super.get(VALUETYPE);
	}

	/**
	 * @param columnLength
	 *            the columnLength to set
	 */
	public void setColumnLength(String columnLength)
	{
		super.put(COLUMNLENGTH, columnLength);
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
	 * @return the isNeedEdit
	 */
	public String getIsNeedEdit()
	{
		return (String) super.get(ISNEEDEDIT);
	}

	/**
	 * @param isNeedEdit
	 *            the isNeedEdit to set
	 */
	public void setIsNeedEdit(String isNeedEdit)
	{
		super.put(ISNEEDEDIT, isNeedEdit);
	}

}
