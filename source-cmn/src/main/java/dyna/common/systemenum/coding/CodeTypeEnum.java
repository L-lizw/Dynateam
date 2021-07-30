/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeTypeEnum
 * wangweixia 2012-12-7
 */
package dyna.common.systemenum.coding;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 *         用于编码规则中Code类型的显示方式
 */
public enum CodeTypeEnum
{
	CODE("CODE", "ID_SYS_CODETYPEENUM_CODE"), // 显示code的组码
	TITLE("TITLE", "ID_SYS_CODETYPEENUM_TITLE"), // 显示Code的Title
	REFERVALUE("REFERVALUE", "ID_SYS_CODETYPEENUM_REFERVALUE"), // 显示选值类型的选值
	REFERMEANING("REFERMEANING", "ID_SYS_CODETYPEENUM_REFERMEANING"), // 显示选值类型的含义

	TITLE_ZH_CN("TITLE_ZH_CN", "ID_SYS_CODETYPEENUM_TITLE_ZH_CN"), // 显示Code的简体Title
	TITLE_ZH_TW("TITLE_ZH_TW", "ID_SYS_CODETYPEENUM_TITLE_ZH_TW"), // 显示Code的繁体Title
	TITLE_EN("TITLE_EN", "ID_SYS_CODETYPEENUM_TITLE_EN"); // 显示Code的英文Title
	// DESCRIPTION, // 显示Code的描述
	// NAME, // 显示Code的name

	private final String	value;
	private final String	msrId;

	private CodeTypeEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	public static CodeTypeEnum typeValueOf(String value)
	{

		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (CodeTypeEnum valueEnum : CodeTypeEnum.values())
		{
			if (value.equals(valueEnum.getValue()))
			{
				return valueEnum;
			}
		}

		return null;
	}
}
