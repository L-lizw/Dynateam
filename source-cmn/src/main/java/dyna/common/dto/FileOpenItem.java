/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileOpenItem
 * wangweixia 2012-9-5
 */
package dyna.common.dto;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.FileOpenItemMapper;
import dyna.common.systemenum.AccessTypeEnum;

import java.util.List;

/**
 * 打开文件配置时的规则块
 * 
 * @author wangweixia
 * 
 */
@Cache
@EntryMapper(FileOpenItemMapper.class)
public class FileOpenItem extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 8075682131167424730L;

	// 关联基本信息的Guid
	public static final String	MASTER_FK			= "MASTERFK";
	// 类型
	public static final String	OBJECT_TYPE			= "OBJECTTYPE";
	// 根据类型所选值的Guid
	public static final String	OBJECT_GUID			= "OBJECTGUID";
	// 根据类型所选的值
	public static final String	OBJECT_NAME			= "OBJECTNAME";
	// 显示顺序
	public static final String	OBJECT_SEQUENCE		= "DATASEQ";
	// 所选的文件打开配置
	public List<FileOpenConfig>	fileSelectList		= null;

	// 遍历权限时，数据层临时存放FileOpenConfig的Guid：字符串类型，以","分隔Guid
	public static final String	fileOpenGuidList	= "FILEOPENGUIDLIST";

	public String getSubjectGuid()
	{
		return (String) this.get(MASTER_FK);
	}

	public void setSubjectGuid(String subjectGuid)
	{
		this.put(MASTER_FK, subjectGuid);
	}

	public AccessTypeEnum getAccessType()
	{
		return AccessTypeEnum.valueOf((String) this.get(OBJECT_TYPE));
	}

	public void setAccessType(AccessTypeEnum type)
	{
		this.put(OBJECT_TYPE, type.name());

	}

	public String getValueGuid()
	{
		return (String) this.get(OBJECT_GUID);
	}

	public void setValueGuid(String guid)
	{
		this.put(OBJECT_GUID, guid);
	}

	public String getValueName()
	{
		return (String) this.get(OBJECT_NAME);
	}

	public void setValueName(String name)
	{
		this.put(OBJECT_NAME, name);
	}

	public int getSequence()
	{
		Number b = (Number) this.get(OBJECT_SEQUENCE);
		int i = b == null ? Integer.parseInt("0") : b.intValue();
		return i;
	}

	public void setSequence(int sequence)
	{
		this.put(OBJECT_SEQUENCE, sequence);
	}

	/**
	 * @return the fileSelectList
	 */
	public List<FileOpenConfig> getFileSelectList()
	{
		return fileSelectList;
	}

	/**
	 * @param fileSelectList
	 *            the fileSelectList to set
	 */
	public void setFileSelectList(List<FileOpenConfig> fileSelectList)
	{
		this.fileSelectList = fileSelectList;
	}

	public String getFileOpenGuidList()
	{
		return (String) this.get(fileOpenGuidList);
	}

	public void setFileOpenGuidList(String guidList)
	{
		this.put(fileOpenGuidList, guidList);
	}

}
