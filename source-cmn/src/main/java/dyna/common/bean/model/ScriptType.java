/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptType 脚本类型枚举
 * Wanglei 2011-3-28
 */
package dyna.common.bean.model;

/**
 * 脚本类型枚举
 * 
 * @author Wanglei
 * 
 */
public enum ScriptType
{
	JAVASCRIPT("js");// , JAVA;

	String	ext	= null;

	ScriptType(String ext)
	{
		this.ext = ext;
	}

	public String getExtension()
	{
		return this.ext;
	}
}
