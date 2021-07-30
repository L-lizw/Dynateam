/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Field类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Field类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum FieldTypeEnum
{
	STRING("String"), //
	CLASSIFICATION("Classification"), //
	CODE("Code"), //
	MULTICODE("MultiCode"), //
	CODEREF("CodeRef"), //
	BOOLEAN("Boolean"), //
	INTEGER("Integer"), //
	FLOAT("Float"), //
	DATE("Date"), //
	DATETIME("Datetime"), //
	OBJECT("Object"), //
	STATUS("Status"), //
	@Deprecated
	BINARY("Image"), //
	FOLDER("Folder");

	private final String	type;

	private FieldTypeEnum(String type)
	{
		this.type = type;
	}

	public static FieldTypeEnum typeof(String type)
	{
		if (type == null)
		{
			return null;
		}

		for (FieldTypeEnum typeEnum : FieldTypeEnum.values())
		{
			if (type.equals(typeEnum.toString()))
			{
				return typeEnum;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return this.type;
	}

	public static FieldTypeEnum[] getUIUsedField()
	{
		return new FieldTypeEnum[] { STRING, CODE, MULTICODE, CODEREF, BOOLEAN, INTEGER, FLOAT, DATE, DATETIME, OBJECT, CLASSIFICATION };
	}

	public static FieldTypeEnum[] getClassficationUIUsedField()
	{
		return new FieldTypeEnum[] { STRING, CODE, MULTICODE, BOOLEAN, INTEGER, FLOAT, DATE, DATETIME, OBJECT };
	}
}
