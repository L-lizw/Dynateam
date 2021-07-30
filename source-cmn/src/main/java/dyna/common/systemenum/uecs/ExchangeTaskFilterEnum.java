/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ExchangeTaskFilterEnum
 * wangweixia 2014-12-31
 */
package dyna.common.systemenum.uecs;

/**
 * @author wangweixia
 *         变更任务过滤
 */
public enum ExchangeTaskFilterEnum
{
	RESPONSIBLEECO("1", "ID_APP_EXCHANGETASKFILTERENUM_RESPONSIBLEECO"), // 我负责的ECO
	CREATEECO("2", "ID_APP_EXCHANGETASKFILTERENUM_CREATEECO"), // 我创建的ECO
	COMPLETEECO("3", "ID_APP_EXCHANGETASKFILTERENUM_COMPLETEECO"), // 已完成的ECO
	NOTCOMPLETEECO("3", "ID_APP_EXCHANGETASKFILTERENUM_NOTCOMPLETEECO");// 未完成的ECO

	private String	value	= null;
	private String	msrId	= null;

	/**
	 * 
	 */
	private ExchangeTaskFilterEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public static ExchangeTaskFilterEnum getExchangeEnumByName(String name)
	{
		ExchangeTaskFilterEnum[] enums = ExchangeTaskFilterEnum.values();
		if (enums != null && enums.length > 0)
		{
			for (ExchangeTaskFilterEnum exchangeEnum : enums)
			{
				if (exchangeEnum.name().equalsIgnoreCase(name))
				{
					return exchangeEnum;
				}
			}
		}

		return null;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return msrId;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}
}
