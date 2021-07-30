/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum;

/**
 * @author WangLHB
 * 
 */
public enum OverTimeActionEnum
{

	COMPLETE("1", "ID_SYS_OVERTIME_COMPLETE"), //
	WAIT("2", "ID_SYS_OVERTIME_WAIT"), //
	SKIP("3", "ID_SYS_OVERTIME_SKIP");

	private String	value	= null;
	private String	msrId	= null;

	OverTimeActionEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.value;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public static OverTimeActionEnum getEnum(String value)
	{
		if ("1".equals(value))
		{
			return COMPLETE;
		}
		else if ("2".equals(value))
		{
			return WAIT;
		}
		else if ("3".equals(value))
		{
			return SKIP;
		}
		else
		{
			return null;
		}
	}

}
