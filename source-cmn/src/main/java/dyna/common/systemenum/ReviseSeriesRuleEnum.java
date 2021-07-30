/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReviseSeriesRuleEnum
 * zhanghj 2010-11-26
 */
package dyna.common.systemenum;

/**
 * @author zhanghj
 * 
 */
public enum ReviseSeriesRuleEnum
{
	LETTER("0"), // 字母
	NUMBER("1"), // 数字
	INPUT("2"); // 输入
	// OTHER("3");// 不限制

	public static ReviseSeriesRuleEnum typeValueOf(String type)
	{
		for (ReviseSeriesRuleEnum reviseSeriesRuleEnum : ReviseSeriesRuleEnum.values())
		{
			if (type.equals(reviseSeriesRuleEnum.toString()))
			{
				return reviseSeriesRuleEnum;
			}
		}

		return null;
	}

	private final String	type;

	private ReviseSeriesRuleEnum(String type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.type;
	}

}
