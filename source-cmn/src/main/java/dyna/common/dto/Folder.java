/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Folder
 * sam Jun 8, 2010
 */
package dyna.common.dto;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.util.BooleanUtils;

@Cache
public class Folder extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -4966476859629926639L;

	public static final String	PARENT_GUID			= "PARENTGUID";
	public static final String	HIERARCHY			= "HIERARCHY";
	public static final String	LEVEL				= "LEVEL";

	public static final String	CLASSIFICATION		= "CLASSIFICATION";
	public static final String	OWNER_USER_GUID		= "OWNERUSERGUID";
	public static final String	OWNER_USER_NAME		= "OWNERUSERNAME";
	public static final String	SHARED_NAME			= "SHAREDNAME";

	public static final String	IS_SHARED			= "ISSHARED";
	public static final String	UPDATE_USER_GUID	= "UPDATEUSERGUID";
	public static final String	GUID				= "GUID";

	public static final String	IS_VALID			= "ISVALID";
	public static final String	SEARCH_LIMIT		= "SEARCHLIMIT";
	public static final String	DESCRIPTION			= "DESCRIPTION";
	public static final String	LIBRARY_USER		= "LIBRARYUSER";
	public static final String	STORAGE				= "STORAGEGUID";
	public static final String	ROOT_LIB_GUID		= "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$";
	public static final String	FOLDER_NAME			= "FOLDERNAME";
	public static final String	FOLDER_ID			= "FOLDERID";

	public Folder()
	{
	}

	public Folder(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @return the storage
	 */
	public String getStorage()
	{
		return (String) this.get(STORAGE);
	}

	public void setStorage(String storage)
	{
		this.put(STORAGE, storage);
	}

	/**
	 * @return the libraryUser
	 */
	public String getLibraryUser()
	{
		return (String) this.get(LIBRARY_USER);
	}

	public void setLibraryUser(String libraryUser)
	{
		this.put(LIBRARY_USER, libraryUser);
	}

	/**
	 * @return the isValid
	 */
	@Override
	public boolean isValid()
	{
		if (this.get(IS_VALID) == null)
		{
			return true;
		}

		// return BooleanUtils.getBooleanBy01((String) this.get(IS_VALID));
		return "0".equals(this.get(IS_VALID));
	}

	public void setIsValid(boolean isValid)
	{
		// this.put(IS_VALID, BooleanUtils.getBooleanString01(isValid));
		this.put(IS_VALID, BooleanUtils.getBooleanString(isValid, "0", "1", null));
	}

	/**
	 * @return the searchLimit
	 */
	public BigDecimal getSearchLimit()
	{
		if (this.get(SEARCH_LIMIT) == null || "".equals(this.get(SEARCH_LIMIT)))
		{
			return null;
		}
		return (BigDecimal) this.get(SEARCH_LIMIT);
	}

	public void setSearchLimit(BigDecimal searchLimit)
	{
		this.put(SEARCH_LIMIT, searchLimit);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	/**
	 * @return the hierarchy
	 */
	public String getHierarchy()
	{
		return (String) this.get(HIERARCHY);
	}

	/**
	 * @return the isShared
	 */
	public String getIsShared()
	{
		return (String) this.get(IS_SHARED);
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		Object value = this.get(LEVEL);
		if (value == null || !(value instanceof BigDecimal))
		{
			return -1;
		}
		return ((Number) value).intValue();
	}

	@Override
	public String getName()
	{
		return (String) this.get("FOLDERNAME");
	}

	/**
	 * @return the OwnerUserGuid
	 */
	public String getOwnerUserGuid()
	{
		return (String) this.get(OWNER_USER_GUID);
	}

	/**
	 * @return the OwnerUserName
	 */
	public String getOwnerUserName()
	{
		return (String) this.get(OWNER_USER_NAME);
	}

	public String getParentFolder()
	{
		return (String) this.get("PARENTGUID");
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return (String) this.get(PARENT_GUID);
	}

	/**
	 * @return the sharedName
	 */
	public String getSharedName()
	{
		return (String) this.get(SHARED_NAME);
	}

	/**
	 * @return the folderType
	 */
	public FolderTypeEnum getType()
	{
		return FolderTypeEnum.typeValueOf((String) this.get(CLASSIFICATION));
	}

	/**
	 * @param folderType
	 *            the folderType to set
	 */
	public void setFolderType(FolderTypeEnum folderType)
	{
		this.put(CLASSIFICATION, String.valueOf(folderType.toString()));
	}

	/**
	 * @param isShared
	 *            the isShared to set
	 */
	public void setIsShared(String isShared)
	{

		this.put(IS_SHARED, isShared);
	}

	@Override
	public void setName(String name)
	{
		this.put("FOLDERNAME", name);
	}

	/**
	 * @param ownerUserGuid
	 *            the ownerUserGuid to set
	 */
	public void setOwnerUserGuid(String ownerUserGuid)
	{
		this.put(OWNER_USER_GUID, ownerUserGuid);
	}

	/**
	 * @param ownerUserName
	 *            the ownerUserName to set
	 */
	public void setOwnerUserName(String ownerUserName)
	{
		this.put(OWNER_USER_NAME, ownerUserName);
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.put(PARENT_GUID, parentGuid);
	}

	/**
	 * @param sharedName
	 *            the sharedName to set
	 */
	public void setSharedName(String sharedName)
	{
		this.put(SHARED_NAME, sharedName);
	}

	public void setID(String id)
	{
		this.put(FOLDER_ID, id);
	}

	public String getID()
	{
		return (String) this.get(FOLDER_ID);
	}

	public void setHierarchy(String path)
	{
		this.put(HIERARCHY, path);
	}
}
