/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileItemAndConfig
 * wangweixia 2012-9-7
 */
package dyna.common.dto;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 文件浏览工具使用
 * 记录FileOpenItem 和FileOpenConfig的一对多的关系
 * 
 * @author wangweixia
 * 
 */
@Cache
public class FileItemAndConfig extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -3153987120358034194L;

	// FileOpenItem的Guid
	public static final String	FOITEMGUID			= "RULEGUID";

	// FileOpenConfig的Guid
	public static final String	FOCONFIGGUID		= "TYPEGUID";

	public String getFoItemGuid()
	{
		return (String) this.get(FOITEMGUID);
	}

	public void setFoItemGuid(String foItemGuid)
	{
		this.put(FOITEMGUID, foItemGuid);
	}

	public String getFoConfigGuid()
	{
		return (String) this.get(FOCONFIGGUID);
	}

	public void setFoConfigGuid(String foConfigGuid)
	{
		this.put(FOCONFIGGUID, foConfigGuid);
	}
}
