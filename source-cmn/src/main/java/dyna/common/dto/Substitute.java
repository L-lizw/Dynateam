/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Substitute 取替代料
 * caogc 2010-10-13
 */
package dyna.common.dto;

import java.math.BigDecimal;
import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author caogc
 */
public class Substitute extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= 1625776797449329016L;
	public static final String	GUID					= "GUID";
	public static final String	TYPE					= "TYPE";
	public static final String	PARENT_ITEM				= "PARENTITEM";
	public static final String	PARENT_ITEM_NAME		= "PARENTITEMNAME";
	public static final String	PARENT_ITEM_CLASS_GUID	= "PARENTITEMCLASSGUID";
	public static final String	BOM_VIEW_TYPE			= "BOMVIEWTYPE";
	public static final String	GROUP_ID				= "GROUPID";
	public static final String	ITEM					= "ITEM";
	public static final String	ITEM_CLASS_GUID			= "ITEMCLASSGUID";
	public static final String	REPLACE_ITEM			= "REPLACEITEM";
	public static final String	REPLACE_ITEM_CLASS_GUID	= "REPLACEITEMCLASSGUID";
	public static final String	QUANITY					= "QUANITY";
	public static final String	PRECEDENCE				= "PRECEDENCE";
	public static final String	START_VALIDATE_DATE		= "STARTVALIDATEDATE";
	public static final String	END_VALIDATE_DATE		= "ENDVALIDATEDATE";
	public static final String	CREATE_USER_GUID		= "CREATEUSERGUID";
	public static final String	CREATE_USER_NAME		= "CREATEUSERNAME";
	public static final String	UPDATE_USER_GUID		= "UPDATEUSERGUID";
	public static final String	UPDATE_USER_NAME		= "UPDATEUSERNAME";

	/**
	 * @return the bomViewType
	 */
	public String getBomViewType()
	{
		return (String) this.get(BOM_VIEW_TYPE);
	}

	/**
	 * @return the createUserGuid
	 */
	@Override
	public String getCreateUserGuid()
	{
		return (String) this.get(CREATE_USER_GUID);
	}

	/**
	 * @return the createUserName
	 */
	public String getCreateUserName()
	{
		return (String) this.get(CREATE_USER_NAME);
	}

	/**
	 * @return the endValidateDate
	 */
	public Date getEndValidateDate()
	{
		return (Date) this.get(END_VALIDATE_DATE);
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId()
	{
		return (String) this.get(GROUP_ID);
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the item
	 */
	public String getItem()
	{
		return (String) this.get(ITEM);
	}

	/**
	 * @return the itemClassGuid
	 */
	public String getItemClassGuid()
	{
		return (String) this.get(ITEM_CLASS_GUID);
	}

	/**
	 * @return the parentItem
	 */
	public String getParentItem()
	{
		return (String) this.get(PARENT_ITEM);
	}

	/**
	 * @return the parentItemClassGuid
	 */
	public String getParentItemClassGuid()
	{
		return (String) this.get(PARENT_ITEM_CLASS_GUID);
	}

	/**
	 * @return the parentItem
	 */
	public String getParentItemName()
	{
		return (String) this.get(PARENT_ITEM_NAME);
	}

	/**
	 * @return the precedence
	 */
	public Integer getPrecedence()
	{
		return this.get(PRECEDENCE) == null ? 0 : ((Number) this.get(PRECEDENCE)).intValue();
	}

	/**
	 * @return the quanity
	 */
	public Integer getQuanity()
	{
		return this.get(QUANITY) == null ? 0 : ((Number) this.get(QUANITY)).intValue();
	}

	/**
	 * @return the replaceItem
	 */
	public String getReplaceItem()
	{
		return (String) this.get(REPLACE_ITEM);
	}

	/**
	 * @return the replaceItemClassGuid
	 */
	public String getReplaceItemClassGuid()
	{
		return (String) this.get(REPLACE_ITEM_CLASS_GUID);
	}

	/**
	 * @return the startValidateDate
	 */
	public Date getStartValidateDate()
	{
		return (Date) this.get(START_VALIDATE_DATE);
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return (String) this.get(TYPE);
	}

	/**
	 * @return the updateUserGuid
	 */
	@Override
	public String getUpdateUserGuid()
	{
		return (String) this.get(UPDATE_USER_GUID);
	}

	/**
	 * @return the updateUserName
	 */
	public String getUpdateUserName()
	{
		return (String) this.get(UPDATE_USER_NAME);
	}

	/**
	 * @param bomViewType
	 *            the bomViewType to set
	 */
	public void setBomViewType(String bomViewType)
	{
		this.put(BOM_VIEW_TYPE, bomViewType);
	}

	/**
	 * @param createUserGuid
	 *            the createUserGuid to set
	 */
	@Override
	public void setCreateUserGuid(String createUserGuid)
	{
		this.put(CREATE_USER_GUID, createUserGuid);
	}

	/**
	 * @param createUserName
	 *            the createUserName to set
	 */
	public void setCreateUserName(String createUserName)
	{
		this.put(CREATE_USER_NAME, createUserName);
	}

	/**
	 * @param endValidateDate
	 *            the endValidateDate to set
	 */
	public void setEndValidateDate(Date endValidateDate)
	{
		this.put(END_VALIDATE_DATE, endValidateDate);
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
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(String item)
	{
		this.put(ITEM, item);
	}

	/**
	 * @param itemClassGuid
	 *            the itemClassGuid to set
	 */
	public void setItemClassGuid(String itemClassGuid)
	{
		this.put(ITEM_CLASS_GUID, itemClassGuid);
	}

	/**
	 * @param parentItem
	 *            the parentItem to set
	 */
	public void setParentItem(String parentItem)
	{
		this.put(PARENT_ITEM, parentItem);
	}

	/**
	 * @param parentItemClassGuid
	 *            the parentItemClassGuid to set
	 */
	public void setParentItemClassGuid(String parentItemClassGuid)
	{
		this.put(PARENT_ITEM_CLASS_GUID, parentItemClassGuid);
	}

	public void setParentItemName(String parentItemName)
	{
		this.put(PARENT_ITEM_NAME, parentItemName);
	}

	/**
	 * @param precedence
	 *            the precedence to set
	 */
	public void setPrecedence(Integer precedence)
	{
		this.put(PRECEDENCE, precedence == null ? new BigDecimal("0") : new BigDecimal(precedence.toString()));
	}

	/**
	 * @param quanity
	 *            the quanity to set
	 */
	public void setQuanity(Integer quanity)
	{
		this.put(QUANITY, quanity == null ? new BigDecimal("0") : new BigDecimal(quanity.toString()));
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	public void setReplaceItem(String replaceItem)
	{
		this.put(REPLACE_ITEM, replaceItem);
	}

	/**
	 * @param replaceItemClassGuid
	 *            the replaceItemClassGuid to set
	 */
	public void setReplaceItemClassGuid(String replaceItemClassGuid)
	{
		this.put(REPLACE_ITEM_CLASS_GUID, replaceItemClassGuid);
	}

	/**
	 * @param startValidateDate
	 *            the startValidateDate to set
	 */
	public void setStartValidateDate(Date startValidateDate)
	{
		this.put(START_VALIDATE_DATE, startValidateDate);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	/**
	 * @param updateUserGuid
	 *            the updateUserGuid to set
	 */
	@Override
	public void setUpdateUserGuid(String updateUserGuid)
	{
		this.put(UPDATE_USER_GUID, updateUserGuid);
	}

	/**
	 * @param updateUserName
	 *            the updateUserName to set
	 */
	public void setUpdateUserName(String updateUserName)
	{
		this.put(UPDATE_USER_NAME, updateUserName);
	}
}
