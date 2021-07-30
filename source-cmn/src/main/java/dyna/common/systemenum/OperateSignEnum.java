/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 操作符的枚举
 * caogc, 2010-08-26
 */
package dyna.common.systemenum;

/**
 * 操作符的枚举
 * 
 * @author caogc
 * 
 */
public enum OperateSignEnum
{
	// 等于
	EQUALS("ID_SYS_OPERATOR_EQUALS", "1", "="),

	// 不等于
	NOTEQUALS("ID_SYS_OPERATOR_NOTEQUALS", "1", "≠"),

	// 包含
	CONTAIN("ID_SYS_OPERATOR_CONTAIN", "1"),

	// 模糊查询（如果查询的值包含？和*，则前后不再添加通配符*）
	LIKE_REGEX("ID_SYS_OPERATOR_LIKE", "1"),

	// 不包含
	NOTCONTAIN("ID_SYS_OPERATOR_NOTCONTAIN", "1", "⊄"),

	// 开头是
	STARTWITH("ID_SYS_OPERATOR_STARTWITH", "1"),

	// 结尾是
	ENDWITH("ID_SYS_OPERATOR_ENDWITH", "1"),

	// 开头是（结尾不添加*）
	STARTWITH_REGEX("ID_SYS_OPERATOR_STARTWITH_REGEX", "1"),

	// 结尾是（开头不添加*）
	ENDWITH_REGEX("ID_SYS_OPERATOR_ENDWITH_REGEX", "1"),

	// 为空
	ISNULL("ID_SYS_OPERATOR_ISNULL", "0"),

	// 不为空
	NOTNULL("ID_SYS_OPERATOR_NOTNULL", "0"),

	// 是
	YES("ID_SYS_OPERATOR_YES", "1"),

	// 不是
	NO("ID_SYS_OPERATOR_NO", "1"),

	// 是
	TRUE("ID_SYS_OPERATOR_TRUE", "0"),

	// 否
	FALSE("ID_SYS_OPERATOR_FALSE", "0"),

	// 大于
	BIGGER("ID_SYS_OPERATOR_BIGGER", "1"),

	// 小于
	SMALLER("ID_SYS_OPERATOR_SMALLER", "1"),

	// 大于等于
	BIGGEROREQUAL("ID_SYS_OPERATOR_BIGGEROREQUAL", "1"),

	// 小于等于
	SMALLEROREQUAL("ID_SYS_OPERATOR_SMALLEROREQUAL", "1"),

	// 早于
	EARLIER("ID_SYS_OPERATOR_EARLIER", "1"),

	// 晚于
	LATER("ID_SYS_OPERATOR_LATER", "1"),

	// 不早于
	NOTEARLIER("ID_SYS_OPERATOR_NOTEARLIER", "1"),

	// 不晚于
	NOTLATER("ID_SYS_OPERATOR_NOTLATER", "1"),

	// 有
	HAVE("ID_SYS_OPERATOR_HAVE", "0"),

	// 无
	NOTHAVE("ID_SYS_OPERATOR_NOTHAVE", "0"),

	// 在
	IN("ID_SYS_OPERATOR_IN", "1");

	private String	msrId		= null;
	private String	fieldCount	= null;
	private String	symbol		= null;

	private OperateSignEnum(String msrId, String fieldCount)
	{
		this.msrId = msrId;
		this.fieldCount = fieldCount;
	}

	private OperateSignEnum(String msrId, String fieldCount, String symbol)
	{
		this.msrId = msrId;
		this.fieldCount = fieldCount;
		this.symbol = symbol;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String fieldCount()
	{
		return this.fieldCount;
	}

	public String getSymbol()
	{
		return this.symbol;
	}

}
