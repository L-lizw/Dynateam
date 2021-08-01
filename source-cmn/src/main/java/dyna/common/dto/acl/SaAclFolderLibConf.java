/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SaAclFolderLibConf
 * Caogc 2011-01-11
 */
package dyna.common.dto.acl;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.acl.SaAclFolderLibConfMapper;
import dyna.common.util.BooleanUtils;

/**
 * 
 * SaAclFolderLibConf 创建配置是否只保存发布版信息的表
 * 
 */
@Cache
@EntryMapper(SaAclFolderLibConfMapper.class)
public class SaAclFolderLibConf extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -891787883916093978L;
	public static final String	FOLDER_GUID			= "FOLDERGUID";
	public static final String	IS_EXTEND			= "ISEXTEND";

	/**
	 * @return the isExtend
	 */
	public boolean isExtend()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(IS_EXTEND));
	}

	/**
	 * @param isExtend
	 *            the isExtend to set
	 */
	public void setIsExtend(boolean isExtend)
	{
		this.put(IS_EXTEND, BooleanUtils.getBooleanStringYN(isExtend));
	}

	/**
	 * @return the folderGuid
	 */
	public String getFolderGuid()
	{
		return (String) this.get(FOLDER_GUID);
	}

	@Override
	public String getGuid()
	{
		return (String) super.get("GUID");
	}

	/**
	 * @param folderGuid
	 *            the folderGuid to set
	 */
	public void setFolderGuid(String folderGuid)
	{
		this.put(FOLDER_GUID, folderGuid);
	}

	/**
	 * @param Guid
	 *            the Guid to set
	 */
	@Override
	public void setGuid(String guid)
	{
		super.put(GUID, guid);
	}

}
