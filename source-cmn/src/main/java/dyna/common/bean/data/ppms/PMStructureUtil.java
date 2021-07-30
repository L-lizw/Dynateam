/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PMDeliveryStructure
 * WangLHB May 16, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.util.BooleanUtils;
import dyna.common.util.PMConstans;

/**
 * @author WangLHB
 * 
 */
public class PMStructureUtil
{
	private StructureObject		structureObject			= null;

	public static final String	GUID					= "GUID";

	// 任务对象
	public static final String	TASK_OBJECT				= "TASKOBJECT";

	// 对象类型
	public static final String	BO_NAME					= "BUSINESSOBJECTNAME";

	// 对象分类
	public static final String	BO_CLASSIFICATION		= "BOCLASSIFICATION";

	// 对象类型title
	public static final String	BO_NAME_TITLE			= "BUSINESSOBJECT$TITLE";

	// 对象分类title
	public static final String	BO_CLASSIFICATION_TITLE	= "BOCLASSIFICATION$TITLE";

	// 是否必须 1：必须；0：非必须
	public static final String	IS_MANDATORY			= "ISMANDATORY";

	// 是否发布 1：已发布；0：未发布
	public static final String	IS_RELEASED				= "ISRELEASED";

	// 是否为交付项
	public static final String	IS_DELIVERY_ITEM		= "IS_DELIVERY_ITEM";

	// 实际交付物结构GUID
	public static final String	STRUCTURE_OBJECT_GUID	= "STRUCTUREOBJECTGUID";

	// 创建者标示符
	public static final String	CREATE_USER_GUID		= "CREATEUSERGUID";

	// 更新者标示符
	public static final String	UPDATE_USER_GUID		= "UPDATEUSERGUID";

	// end2 Foundation
	public static final String	END2_FOUNDATION			= "END2FOUNDATION";

	public PMStructureUtil(StructureObject structureObject)
	{
		this.structureObject = structureObject;
	}

	/**
	 * @return the guid
	 */
	public String getGuid()
	{
		return (String) this.structureObject.get(GUID);
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid)
	{
		this.structureObject.put(GUID, guid);
	}

	/**
	 * @return the boName
	 */
	public String getBOName()
	{
		return (String) this.structureObject.get(BO_NAME);
	}

	/**
	 * @param boName
	 *            the boName to set
	 */
	public void setBOName(String boName)
	{
		this.structureObject.put(BO_NAME, boName);
	}

	/**
	 * @return the boClassification
	 */
	public String getBOClassification()
	{
		return (String) this.structureObject.get(BO_CLASSIFICATION);
	}

	/**
	 * @param boClassification
	 *            the boClassification to set
	 */
	public void setBOClassification(String boClassification)
	{
		this.structureObject.put(BO_CLASSIFICATION, boClassification);
	}

	/**
	 * @return the boName
	 */
	public String getBONameTitle()
	{
		return (String) this.structureObject.get(BO_NAME_TITLE);
	}

	/**
	 * @return the boClassification
	 */
	public String getBOClassificationTitle()
	{
		return (String) this.structureObject.get(BO_CLASSIFICATION_TITLE);
	}

	/**
	 * @return the boName
	 */
	public void setBONameTitle(String title)
	{
		this.structureObject.put(BO_NAME_TITLE, title);
	}

	/**
	 * @return the boClassification
	 */
	public void setBOClassificationTitle(String title)
	{
		this.structureObject.put(BO_CLASSIFICATION_TITLE, title);
	}

	/**
	 * @return the isMandatory
	 */
	public Boolean isMandatory()
	{
		return BooleanUtils.getBooleanBy10((String) this.structureObject.get(IS_MANDATORY));
	}

	/**
	 * @param isMandatory
	 *            the isMandatory to set
	 */
	public void setMandatory(Boolean isMandatory)
	{
		this.structureObject.put(IS_MANDATORY, BooleanUtils.getBooleanString10(isMandatory));
	}

	/**
	 * @return the isReleased
	 */
	public Boolean isReleased()
	{
		return BooleanUtils.getBooleanBy10((String) this.structureObject.get(IS_RELEASED));
	}

	/**
	 * @param isReleased
	 *            the isReleased to set
	 */
	public void setReleased(Boolean isReleased)
	{
		this.structureObject.put(IS_RELEASED, BooleanUtils.getBooleanString10(isReleased));
	}

	/**
	 * @return the structureObject
	 */
	public StructureObject getStructureObject()
	{
		return this.structureObject;
	}

	/**
	 * @param structureObject
	 *            the structureObject to set
	 */
	public void setStructureObject(StructureObject structureObject)
	{
		this.structureObject = structureObject;
	}

	/**
	 * @return the taskObject
	 */
	public ObjectGuid getTaskObject()
	{
		return new ObjectGuid((String) this.structureObject.get(TASK_OBJECT + PMConstans.CLASS), null,
				(String) this.structureObject.get(TASK_OBJECT), (String) this.structureObject.get(TASK_OBJECT
						+ PMConstans.MASTER), null);

	}

	/**
	 * @param taskObject
	 *            the taskObject to set
	 */
	public void setTaskObject(ObjectGuid taskObject)
	{
		if (taskObject == null)
		{
			this.structureObject.put(TASK_OBJECT + PMConstans.MASTER, null);
			this.structureObject.put(TASK_OBJECT + PMConstans.CLASS, null);
			this.structureObject.put(TASK_OBJECT, null);
		}
		else
		{
			this.structureObject.put(TASK_OBJECT + PMConstans.MASTER, taskObject.getMasterGuid());
			this.structureObject.put(TASK_OBJECT + PMConstans.CLASS, taskObject.getClassGuid());
			this.structureObject.put(TASK_OBJECT, taskObject.getGuid());
		}

	}

	/**
	 * @return the isDeliveryItem
	 */
	public Boolean isDeliveryItem()
	{
		Boolean b = BooleanUtils.getBooleanBy10((String) this.structureObject.get(IS_DELIVERY_ITEM));
		return b == null ? false : b.booleanValue();
	}

	/**
	 * @param isDeliveryItem
	 *            the isDeliveryItem to set
	 */
	public void setDeliveryItem(Boolean isDeliveryItem)
	{
		this.structureObject.put(IS_DELIVERY_ITEM, BooleanUtils.getBooleanString10(isDeliveryItem));
	}

	/**
	 * @return the structureObjectGuid
	 */
	public String getStructureObjectGuid()
	{
		return (String) this.structureObject.get(STRUCTURE_OBJECT_GUID);
	}

	/**
	 * @param structureObjectGuid
	 *            the structureObjectGuid to set
	 */
	public void setStructureObjectGuid(String structureObjectGuid)
	{
		this.structureObject.put(STRUCTURE_OBJECT_GUID, structureObjectGuid);
	}

	/**
	 * @return the createUserGuid
	 */
	public String getCreateUserGuid()
	{
		return (String) this.structureObject.get(CREATE_USER_GUID);
	}

	/**
	 * @param createUserGuid
	 *            the createUserGuid to set
	 */
	public void setCreateUserGuid(String createUserGuid)
	{
		this.structureObject.put(CREATE_USER_GUID, createUserGuid);
	}

	/**
	 * @return the updateUserGuid
	 */
	public String getUpdateUserGuid()
	{
		return (String) this.structureObject.get(UPDATE_USER_GUID);
	}

	/**
	 * @param updateUserGuid
	 *            the updateUserGuid to set
	 */
	public void setUpdateUserGuid(String updateUserGuid)
	{
		this.structureObject.put(UPDATE_USER_GUID, updateUserGuid);
	}

	/**
	 * @return the end2Foundation
	 */
	public FoundationObject getEnd2Foundation()
	{
		return (FoundationObject) this.structureObject.get(END2_FOUNDATION);
	}

	/**
	 * @param end2Foundation
	 *            the end2Foundation to set
	 */
	public void setEnd2Foundation(FoundationObject end2Foundation)
	{
		this.structureObject.put(END2_FOUNDATION, end2Foundation);
	}

	/**
	 * @return the end2Foundation
	 */
	public StructureObject getEnd2Structure()
	{
		return (StructureObject) this.getEnd2Foundation().get(StructureObject.STRUCTURE_CLASS_NAME);
	}

}
