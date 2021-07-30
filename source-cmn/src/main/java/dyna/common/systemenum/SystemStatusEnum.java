/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: System Status
 * xiasheng, 2010-7-13
 */
package dyna.common.systemenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统状态枚举
 */
public enum SystemStatusEnum
{
	@Deprecated
	CREATED("CRT", "ID_SYS_STATUS_CRT"), //
	WIP("WIP", "ID_SYS_STATUS_WIP"), //
	ECP("ECP", "ID_SYS_STATUS_ECP"), //
	PRE("PRE", "ID_SYS_STATUS_PRE"), //
	RELEASE("RLS", "ID_SYS_STATUS_RLS"), //
	OBSOLETE("OBS", "ID_SYS_STATUS_OBS"); //

	public static SystemStatusEnum getStatusEnum(String id)
	{
		for (SystemStatusEnum statusEnum : SystemStatusEnum.values())
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

	private SystemStatusEnum(String id, String msrId)
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

	public static List<SystemStatusEnum> listStatusChange(SystemStatusEnum fromStatus)
	{
		List<SystemStatusEnum> statusList = new ArrayList<SystemStatusEnum>();
		switch (fromStatus)
		{
		case CREATED:
			statusList.add(SystemStatusEnum.WIP);
			break;
		case WIP:
			statusList.add(SystemStatusEnum.PRE);
			statusList.add(SystemStatusEnum.CREATED);
			statusList.add(SystemStatusEnum.ECP);
			break;

		case PRE:
			statusList.add(SystemStatusEnum.WIP);
			statusList.add(SystemStatusEnum.RELEASE);
			break;

		case RELEASE:
			statusList.add(SystemStatusEnum.OBSOLETE);
			break;
		case OBSOLETE:
			statusList.add(SystemStatusEnum.RELEASE);
			break;
		case ECP:
			statusList.add(SystemStatusEnum.RELEASE);
			break;
		default:
			break;
		}

		return statusList;
	}

}
