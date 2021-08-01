/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Group
 * Wanglei 2010-7-13
 */
package dyna.common.dto.aas;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.aas.GroupMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
@Cache
@EntryMapper(GroupMapper.class)
public class Group extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= 4806388039916169742L;

	public static final String	GROUP_ID				= "GROUPID";
	public static final String	GROUP_NAME				= "GROUPNAME";
	public static final String	PARENT_GUID				= "PARENTGROUPGUID";
	public static final String	HIERARCHY				= "HIERARCHY";
	public static final String	LEVEL					= "LEVEL";
	public static final String	IS_ADMIN_GROUP			= "ISADMIN";
	public static final String	DESCRIPTION				= "DESCRIPTION";
	public static final String	IS_VALID				= "ISVALID";
	public static final String	SITE_ID					= "SITEID";
	public static final String	LIBRARY_GUID			= "LIBRARYGUID";
	public static final String	CALENDARGUID			= "CALENDARGUID";
	public static final String	BM_GUID					= "BMGUID";
	public static final String	BM_NAME					= "BMNAME";
	public static final String	BM_TITLE				= "BMTITLE";

	public static final String	GROUP_ID_ADMINISTRATOR	= "ADMINISTRATOR";

	/**
	 * @return the libraryGuid
	 */
	public String getLibraryGuid()
	{
		return (String) this.get(LIBRARY_GUID);
	}

	/**
	 * @param libraryGuid
	 *            the libraryGuid to set
	 */
	public void setLibraryGuid(String libraryGuid)
	{
		this.put(LIBRARY_GUID, libraryGuid);
	}

	/**
	 * @return the Description
	 */
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public String getFullname()
	{
		return this.getGroupId() + "-" + this.getGroupName();
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId()
	{
		return (String) this.get(GROUP_ID);
	}

	/**
	 * @return the groupName
	 */
	@Override
	public String getName()
	{
		return (String) this.get(GROUP_NAME);
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName()
	{
		return this.getName();
	}

	/**
	 * @return the hierarchy
	 */
	public String getHierarchy()
	{
		return (String) this.get(HIERARCHY);
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{

		return this.get(LEVEL) == null ? 0 : ((Number) this.get(LEVEL)).intValue();
	}

	public String getOriginalFullname()
	{
		return this.getOriginalValue(GROUP_ID) + "-" + this.getOriginalValue(GROUP_NAME);
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return (String) this.get(PARENT_GUID);
	}

	public String getSiteId()
	{
		return (String) this.get(SITE_ID);
	}

	public boolean isActive()
	{
		return this.get("ISVALID") == null || "Y".equals(this.get("ISVALID")) ? true : false;
	}

	/**
	 * @return the isAdminGroup
	 */
	public boolean isAdminGroup()
	{
		return "Y".equals(this.get(IS_ADMIN_GROUP)) ? true : false;
	}

	public String getCalendarguid()
	{
		return (String) this.get(CALENDARGUID);
	}

	public void setActive(boolean isActive)
	{
		this.put("ISVALID", isActive ? "Y" : "N");
	}

	/**
	 * @param Description
	 *            the Description to set
	 */
	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId)
	{
		this.put(GROUP_ID, groupId);
	}

	/**
	 * @param groupName
	 *            the groupName to set
	 */
	@Override
	public void setName(String groupName)
	{
		this.put(GROUP_NAME, groupName);
	}

	public void setGroupName(String groupName)
	{
		this.setName(groupName);
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.put(PARENT_GUID, parentGuid);
	}

	public void setSiteId(String siteId)
	{
		this.put(SITE_ID, siteId);
	}

	public void setCalendarguid(String calendarguid)
	{
		this.put(CALENDARGUID, calendarguid);
	}

	public String getBizModelGuid()
	{
		return (String) this.get(BM_GUID);
	}

	public String getBizModelName()
	{
		return (String) this.get(BM_NAME);
	}

	public String getBizModelTitle(LanguageEnum lang)
	{
		String title = (String) this.get(BM_TITLE);
		return StringUtils.getMsrTitle(title, lang.getType());
	}

	public String getBizModelTitle()
	{
		return (String) this.get(BM_TITLE);
	}

	public void setBizModelGuid(String guid)
	{
		this.put(BM_GUID, guid);
	}

	public void setBizModelName(String modelName)
	{
		this.put(BM_NAME, modelName);
	}
}
