/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECOperateTypeEnum  工程变更操作类型枚举
 * caogc 2010-11-01
 */
package dyna.common.systemenum;

/**
 * ECOperateTypeEnum 工程变更操作类型枚举
 * 
 * @author caogc
 * 
 */
public enum ECOperateTypeEnum
{
	REMOVE("REMOVE", "ID_CLIENT_INSTANCE_BOM_EDIT_REMOVE", 1), //
	MODIFY("UPDATE", "ID_CLIENT_INSTANCE_BOM_EDIT_MODIFY", 2), //
	INSERT("INSERT", "ID_CLIENT_INSTANCE_BOM_EDIT_INSERT", 3), //
	NOTHING("NOTHING", "", 0), //

	ADD("ADD", "ID_CLIENT_INSTANCE_BOM_EDIT_ADD", 4), // 前台专用
	REPLACE("REPLACE", "ID_CLIENT_INSTANCE_BOM_EDIT_REPLACE", 5); // 前台专用

	private String	type		= null;
	private String	msrId		= null;
	private int		sequence	= 0;

	private ECOperateTypeEnum(String type, String msrId, int sequence)
	{
		this.type = type;
		this.msrId = msrId;
		this.sequence = sequence;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public int getSequence()
	{
		return this.sequence;
	}

	@Override
	public String toString()
	{
		return this.type;
	}

}
