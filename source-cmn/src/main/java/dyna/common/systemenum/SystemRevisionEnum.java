/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: System Status
 * xiasheng, 2010-7-13
 */
package dyna.common.systemenum;

/**
 * 系统状态枚举
 */
public enum SystemRevisionEnum
{
	LATEST("LATEST", "ID_SYS_REVISION_LATEST"), //
	ALL("ALL", "ID_SYS_REVISION_ALL"); //

	public static SystemRevisionEnum getStatusEnum(String id)
	{
		for (SystemRevisionEnum statusEnum : SystemRevisionEnum.values())
		{
			if (statusEnum.id.equals(id))
			{
				return statusEnum;
			}
		}
		return null;
	}

	private String	id		= null;
	private String	msrId	= null;

	private SystemRevisionEnum(String id, String msrId)
	{
		this.id = id;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getId()
	{
		return this.id;
	}

	@Override
	public String toString()
	{
		return this.id;
	}
}
