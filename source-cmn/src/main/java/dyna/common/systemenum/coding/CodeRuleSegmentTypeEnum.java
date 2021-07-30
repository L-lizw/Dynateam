/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeRuleLifeCycle
 * WangLHB Sep 25, 2012
 */
package dyna.common.systemenum.coding;

import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
public enum CodeRuleSegmentTypeEnum
{

	FIXED("fixed", "ID_SYS_CODERULESEGMENTTYPEENUM_FIXED"), // 固定
	SERIAL("Serial", "ID_SYS_CODERULESEGMENTTYPEENUM_SERIAL"), // 流水号
	INPUT("input", "ID_SYS_CODERULESEGMENTTYPEENUM_INPUT"), // 输入
	CODE("code", "ID_SYS_CODERULESEGMENTTYPEENUM_CODE"), // Code
	CODEREF("coderef", "ID_SYS_CODERULESEGMENTTYPEENUM_CODEREF"), // 选值
	DATE("date", "ID_SYS_CODERULESEGMENTTYPEENUM_DATE"), // 日期
	DATETIME("datetime", "ID_SYS_CODERULESEGMENTTYPEENUM_DATETIME"), // 时间
	OBJECT("object", "ID_SYS_CODERULESEGMENTTYPEENUM_OBJECT"); //

	private final String	type;
	private final String	msrId;

	private CodeRuleSegmentTypeEnum(String type, String msrId)
	{
		this.type = type;
		this.msrId = msrId;
	}

	public String getType()
	{
		return this.type;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	public static CodeRuleSegmentTypeEnum typeValueOf(String type)
	{

		if (StringUtils.isNullString(type))
		{
			return null;
		}
		for (CodeRuleSegmentTypeEnum codeRuleSegmentTypeEnum : CodeRuleSegmentTypeEnum.values())
		{
			if (type.equals(codeRuleSegmentTypeEnum.getType()))
			{
				return codeRuleSegmentTypeEnum;
			}
		}

		return null;
	}
}
