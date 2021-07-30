/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOInfo
 * Wanglei 2010-9-2
 */
package dyna.common.bean.model.bmbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.systemenum.BusinessModelTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public class BusinessObject implements Cloneable, Serializable
{
	private static final long		serialVersionUID	= 4181949505516193453L;

	private final BOInfo			boInfo;

	private ClassInfo				classInfo			= null;

	// 该字段仅供设置用户常用类时用 别的地方不会用到
	private CodeItemInfo			classificationItem	= null;

	private List<BusinessObject>	childList			= null;

	public BusinessObject()
	{
		this.boInfo = new BOInfo();
	}

	public BusinessObject(BOInfo boInfo)
	{
		this.boInfo = boInfo;
	}

	public String getGuid()
	{
		return this.boInfo.getGuid();
	}

	public void setGuid(String guid)
	{
		this.boInfo.setGuid(guid);
	}

	/**
	 * @return the classification
	 */
	public CodeItemInfo getClassificationItem()
	{
		return this.classificationItem;
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassificationItem(CodeItemInfo classificationItem)
	{
		this.classificationItem = classificationItem;
	}

	/**
	 * @return the icon
	 */
	public String getIcon()
	{
		if (this.getType() == BusinessModelTypeEnum.PACKAGE)
		{
			return "package.png";
		}
		else if (this.getClassInfo() != null && StringUtils.isNullString(this.getClassInfo().getIcon()))
		{
			return "object.png";
		}
		else
		{
			return this.getClassInfo() == null ? null : this.getClassInfo().getIcon();
		}
	}

	/**
	 * @return the icon32
	 */
	public String getIcon32()
	{
		if (this.getType() == BusinessModelTypeEnum.PACKAGE)
		{
			return "package32.png";
		}
		else if (this.getClassInfo() != null && StringUtils.isNullString(this.getClassInfo().getIcon32()))
		{
			return "object32.png";
		}
		else
		{
			return this.getClassInfo() == null ? null : this.getClassInfo().getIcon32();
		}
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.getBOName();
	}

	public String getBOName()
	{
		return this.boInfo.getBOName();
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return this.boInfo.getTitle();
	}

	/**
	 * @return the title by language
	 */
	public String getTitle(LanguageEnum lang)
	{
		return this.boInfo.getTitle();
	}

	/**
	 * @return BusinessModelTypeEnum类型枚举
	 */
	public BusinessModelTypeEnum getType()
	{
		return this.boInfo.getType();
	}

	public String getModelType()
	{
		return this.boInfo.getModelType();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.setBOName(name);
	}

	public void setBOName(String name)
	{
		this.boInfo.setBOName(name);
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.boInfo.setTitle(title);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(BusinessModelTypeEnum type)
	{
		this.boInfo.setType(type);
	}

	public void setModelType(String modelType)
	{
		this.boInfo.setModelType(modelType);
	}

	/**
	 * @return the classGuid
	 */
	public String getClassGuid()
	{
		return this.boInfo.getClassGuid();
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		this.boInfo.setClassGuid(classGuid);
	}

	/**
	 * @return the classificationGuid
	 */
	public String getClassificationGuid()
	{
		return this.boInfo.getClassificationGuid();
	}

	/**
	 * @param classificationGuid
	 *            the classificationGuid to set
	 */
	public void setClassificationGuid(String classificationGuid)
	{
		this.boInfo.setClassificationGuid(classificationGuid);
	}

	/**
	 * @param showClassification
	 *            the showClassification to set
	 */
	public void setShowClassification(boolean showClassification)
	{
		this.boInfo.setShowClassification(showClassification);
	}

	/**
	 * @return the showClassification
	 */
	public boolean isShowClassification()
	{
		return this.boInfo.isShowClassification();
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.boInfo.setDescription(description);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.boInfo.getDescription();
	}

	public ClassInfo getClassInfo()
	{
		return classInfo;
	}

	public void setClassInfo(ClassInfo classInfo)
	{
		this.classInfo = classInfo;
	}

	public boolean isAbstract()
	{
		return this.boInfo.isAbstract();
	}

	public boolean isCreateTable()
	{
		return this.boInfo.isCreateTable();
	}

	public String getClassName()
	{
		return this.boInfo.getClassName();
	}

	public List<BusinessObject> getChildList()
	{
		return childList;
	}

	public void setChildList(List<BusinessObject> childList)
	{
		this.childList = childList;
	}

	public void setShared(boolean isShared)
	{
		this.boInfo.setShared(isShared);
	}

	public boolean isShared()
	{
		return this.boInfo.isShared();
	}

	public BOInfo getBoInfo()
	{
		return boInfo;
	}

	public String getBMGuid()
	{
		return this.boInfo.getBMGuid();
	}

	public void setBMGuid(String bmGuid)
	{
		this.boInfo.getBMGuid();
	}

	public void setSequence(int sequence)
	{
		this.boInfo.setSequence(sequence);
	}

	public int getSequence()
	{
		return this.boInfo.getSequence();
	}

	public String getParentBOGuid()
	{
		return this.boInfo.getParentBOGuid();
	}

	public void setParentBOGuid(String parentBOGuid)
	{
		this.boInfo.setParentBOGuid(parentBOGuid);
	}

	/**
	 * @param child
	 *            the child to add
	 */
	public void addChild(BusinessObject child)
	{
		if (this.childList == null)
		{
			this.childList = new ArrayList<BusinessObject>();
		}

		this.childList.add(child);
		this.sortChild();
	}

	/**
	 * @param child
	 *            the child to add
	 */
	public void removeChild(BusinessObject child)
	{
		if (this.childList == null)
		{
			this.childList = new ArrayList<BusinessObject>();
			return;
		}

		for (BusinessObject bo : this.childList)
		{
			if (bo.getGuid().equals(child.getGuid()))
			{
				this.childList.remove(bo);
				break;
			}
		}
		this.sortChild();

	}

	public void sortChild()
	{
		if (!SetUtils.isNullList(childList))
		{
			Collections.sort(this.childList, new Comparator<BusinessObject>()
			{

				@Override
				public int compare(BusinessObject o1, BusinessObject o2)
				{
					return o1.getSequence() - o2.getSequence();
				}
			});
		}
	}

	public String getParent()
	{
		return this.boInfo.getParent();
	}

	public void setParent(String parent)
	{
		this.boInfo.setParent(parent);
	}

	@Override
	public BusinessObject clone()
	{
		return this.clone();
	}

	public BusinessObject findBusinessObject(String boName)
	{
		if (boName == null)
		{
			return null;
		}

		if (this.childList == null)
		{
			return null;
		}

		for (BusinessObject businessObject : this.childList)
		{
			if (businessObject == null)
			{
				continue;
			}
			if (boName.equalsIgnoreCase(businessObject.getName()))
			{
				return businessObject;
			}
			BusinessObject bo = businessObject.findBusinessObject(boName);
			if (bo != null)
			{
				return bo;
			}
		}

		return null;
	}
}
