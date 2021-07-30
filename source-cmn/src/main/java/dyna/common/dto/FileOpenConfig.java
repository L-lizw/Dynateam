/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileOpenConfig
 * wangweixia 2012-9-5
 */
package dyna.common.dto;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.FileOpenToolTypeEnum;

/**
 * 打开文件配置时的文件打开类型块
 * 
 * @author wangweixia
 * 
 */
@Cache
public class FileOpenConfig extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5338733195941034556L;

	// Guid
	public static final String	GUID				= "GUID";
	// 文件类型名称
	public static final String	FILETYPENAME		= "TYPENAME";
	// 应用程序运行路径及参数
	public static final String	RUNNINGPATH			= "APPPATH";
	// 可打开的文件类型
	public static final String	OPENTYPE			= "FILETYPE";
	// 关系模板名称
	public static final String	RELATIONNAME		= "RELATIONNAME";
	// 同步下载文件
	public static final String	SYNDOWNLOADTYPE		= "SYNFILETYPE";

	// 工具类型
	public static final String	TOOLTYPE			= "TOOLTYPE";

	/**
	 * @return the guid
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @return the filetypename
	 */
	public String getFiletypename()
	{
		return (String) this.get(FILETYPENAME);
	}

	public void setFiletypename(String filetypeName)
	{
		this.put(FILETYPENAME, filetypeName);
	}

	/**
	 * @return the runningpath
	 */
	public String getRunningpath()
	{
		return (String) this.get(RUNNINGPATH);
	}

	public void setRunningpath(String runningPath)
	{
		this.put(RUNNINGPATH, runningPath);
	}

	/**
	 * @return the opentype
	 */
	public String getOpentype()
	{
		return (String) this.get(OPENTYPE);
	}

	public void setOpentype(String openType)
	{
		this.put(OPENTYPE, openType);
	}

	public FileOpenToolTypeEnum getToolType()
	{
		return FileOpenToolTypeEnum.typeValueOf((String) super.get(TOOLTYPE));
	}

	public void setToolType(FileOpenToolTypeEnum toolType)
	{
		super.put(TOOLTYPE, toolType.getValue());
	}

	/**
	 * @return the relationname
	 */
	public String getRelationname()
	{
		return (String) this.get(RELATIONNAME);
	}

	public void setRelationname(String relationName)
	{
		this.put(RELATIONNAME, relationName);
	}

	/**
	 * @return the syndownloadtype
	 */
	public String getSyndownloadtype()
	{
		return (String) this.get(SYNDOWNLOADTYPE);
	}

	public void setSyndownloadtype(String synDownloadType)
	{
		this.put(SYNDOWNLOADTYPE, synDownloadType);
	}

	@Override
	public String getName()
	{
		return getFiletypename();
	}

	@Override
	public void setName(String name)
	{
		setFiletypename(name);
	}

}
