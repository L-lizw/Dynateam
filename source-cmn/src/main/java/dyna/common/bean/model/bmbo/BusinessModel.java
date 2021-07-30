/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BMInfo
 * Wanglei 2010-9-2
 */
package dyna.common.bean.model.bmbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;

/**
 * @author Wanglei
 * 
 */
public class BusinessModel extends SystemObjectImpl implements SystemObject
{

	private static final long		serialVersionUID	= 7454704314111283772L;

	private final BMInfo			bmInfo;

	private List<BusinessObject>	businessObjectList	= new ArrayList<BusinessObject>();

	public BusinessModel()
	{
		this.bmInfo = new BMInfo();
	}

	public BusinessModel(BMInfo bmInfo)
	{
		this.bmInfo = bmInfo;
	}

	@Override
	public String getGuid()
	{
		return this.bmInfo.getGuid();
	}

	@Override
	public void setGuid(String guid)
	{
		this.bmInfo.setGuid(guid);
	}

	public BMInfo getBmInfo()
	{
		return bmInfo;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.bmInfo.getName();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.bmInfo.setName(name);
	}

	/**
	 * @return the title by language
	 */
	public String getTitle(LanguageEnum lang)
	{
		return this.bmInfo.getTitle(lang);
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return this.bmInfo.getTitle();
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.bmInfo.setTitle(title);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.bmInfo.setDescription(description);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.bmInfo.getDescription();
	}

	public void setModelType(String modelType)
	{
		this.bmInfo.setModelType(modelType);
	}

	public String getModelType()
	{
		return this.bmInfo.getModelType();
	}

	public void setSequence(int sequence)
	{
		this.bmInfo.setSequence(sequence);
	}

	public int getSequence()
	{
		return this.bmInfo.getSequence();
	}

	public List<BusinessObject> getBusinessObjectList()
	{
		return businessObjectList;
	}

	public void setBusinessObjectList(List<BusinessObject> businessObjectList)
	{
		this.businessObjectList = businessObjectList;
	}

	/**
	 * @param businessObjectName
	 * @return BusinessObject
	 */
	public BusinessObject getBusinessObject(String businessObjectName)
	{
		if (businessObjectName == null)
		{
			return null;
		}

		if (this.businessObjectList == null)
		{
			return null;
		}

		for (BusinessObject businessObject : this.businessObjectList)
		{
			if (businessObject == null)
			{
				continue;
			}
			if (businessObjectName.equalsIgnoreCase(businessObject.getName()))
			{
				return businessObject;
			}
		}

		return null;
	}

	/**
	 * @param businessObjectName
	 * @return BusinessObject
	 */
	public void removeBusinessObject(String businessObjectName)
	{
		if (businessObjectName == null)
		{
			return;
		}

		if (this.businessObjectList == null)
		{
			return;
		}

		for (BusinessObject businessObject : this.businessObjectList)
		{
			if (businessObject == null)
			{
				continue;
			}
			if (businessObjectName.equalsIgnoreCase(businessObject.getName()))
			{
				this.businessObjectList.remove(businessObject);
				break;
			}
		}
		this.sortBusinessObject();
	}

	public void addBusinessObject(BusinessObject businessObject)
	{
		if (this.businessObjectList == null)
		{
			this.businessObjectList = new ArrayList<BusinessObject>();
		}
		this.businessObjectList.add(businessObject);
		this.sortBusinessObject();
	}

	public void sortBusinessObject()
	{
		if (!SetUtils.isNullList(this.businessObjectList))
		{
			Collections.sort(this.businessObjectList, new Comparator<BusinessObject>()
			{
				@Override
				public int compare(BusinessObject o1, BusinessObject o2)
				{
					return o1.getSequence() - o2.getSequence();
				}
			});
		}
	}

	@Override
	public BusinessModel clone()
	{
		return (BusinessModel) super.clone();
	}

	public BusinessObject findBusinessObject(String boName)
	{
		if (boName == null)
		{
			return null;
		}

		if (this.businessObjectList == null)
		{
			return null;
		}

		for (BusinessObject businessObject : this.businessObjectList)
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
