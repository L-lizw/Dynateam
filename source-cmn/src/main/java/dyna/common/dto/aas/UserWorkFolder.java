/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLObject
 * Wanglei 2010-7-30
 */
package dyna.common.dto.aas;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.UserWorkFolderTypeEnum;

/**
 * @author Wanglei
 * 
 */
public class UserWorkFolder extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -3051284247295002344L;

	public static final String	USERGUID			= "USERGUID";
	public static final String	FOLDERTYPE			= "FOLDERTYPE";
	public static final String	FOLDERPATH			= "FOLDERPATH";

	/**
	 * @return the userGuid
	 */
	public String getUserGuid()
	{
		return (String) this.get(USERGUID);
	}
	
	/**
	 * @param userGuid
	 *            the userGuid to set
	 */
	public void setUserGuid(String userGuid)
	{
		this.put(USERGUID, userGuid);
	}
	
	/**
	 * @return the folderPath
	 */
	public String getFolderPath()
	{
		return (String) this.get(FOLDERPATH);
	}
	
	/**
	 * @param userGuid
	 *            the userGuid to set
	 */
	public void setFolderPath(String folderPath)
	{
		this.put(FOLDERPATH, folderPath);
	}
	
	/**
	 * @return the folderType
	 */
	public UserWorkFolderTypeEnum getFolderType()
	{
		return UserWorkFolderTypeEnum.valueOf((String) this.get(FOLDERTYPE));
	}
	
	/**
	 * @param folderType
	 *            the folderType to set
	 */
	public void setFolderType(UserWorkFolderTypeEnum workFolderTypeEnum)
	{
		this.put(FOLDERTYPE, workFolderTypeEnum.name());
	}
}
