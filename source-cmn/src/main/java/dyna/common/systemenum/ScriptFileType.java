/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptType 脚本类型枚举
 * Wanglei 2011-3-28
 */
package dyna.common.systemenum;

/**
 * 脚本类型枚举
 * 
 * @author Wanglei
 * 
 */
public enum ScriptFileType
{
	JAVASCRIPT("js");// , JAVA;

	String ext = null;

	ScriptFileType(String ext)
	{
		this.ext = ext;
	}

	public String getExtension()
	{
		return this.ext;
	}

	public static ScriptFileType typeOf(String ext)
	{
		ScriptFileType[] values = ScriptFileType.values();
		for (ScriptFileType type : values)
		{
			if (type.getExtension().contentEquals(ext))
			{
				return type;
			}
		}
		return JAVASCRIPT;
	}
}
