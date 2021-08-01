/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileType 文件类型对象
 * Wanglei 2010-11-2
 */
package dyna.common.dto;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.FileTypeMapper;

/**
 * 文件类型对象
 * 
 * @author Wanglei
 * 
 */
@Cache
@EntryMapper(FileTypeMapper.class)
public class FileType extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	UNKNOW_FILE_TYPE	= "UNKNOW_FILE";
	public static final String	PREVIEW_FILE_TYPE	= "PREVIEW_FILE";
	public static final String	ICON_FILE_TYPE		= "ICON_FILE";

	public static final String	ID					= "TYPEID";
	public static final String	EXTENSION			= "EXTENSION";
	public static final String	ICON16				= "ICON16";
	public static final String	ICON32				= "ICON32";

	public FileType()
	{
		super();
	}

	public String getId()
	{
		return (String) super.get(ID);
	}

	public String getExtension()
	{
		return (String) super.get(EXTENSION);
	}

	public String getIcon16()
	{
		return (String) super.get(ICON16);
	}

	public String getIcon32()
	{
		return (String) super.get(ICON32);
	}

}
