/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: VesionTypeEnum
 * zhanghj 2011-4-29
 */
package dyna.common.systemenum;

/**
 * 版本类型
 * 
 * @author zhanghj
 * 
 */
public enum VesionTypeEnum
{
	ALL("ALL", "ID_SYS_ENUM_ALL"), //
	LATEST("LATEST", "ID_SYS_ENUM_LATEST"); //

	public static VesionTypeEnum getVesionEnum(String id)
	{
		for (VesionTypeEnum statusEnum : VesionTypeEnum.values())
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

	private VesionTypeEnum(String id, String msrId)
	{
		this.id = id;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	@Override
	public String toString()
	{
		return this.id;
	}
}
