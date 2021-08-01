/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLObject
 * Wanglei 2010-7-30
 */
package dyna.common.dto.acl;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.acl.ACLSubjectMapper;
import dyna.common.systemenum.AccessConditionEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Cache
@EntryMapper(ACLSubjectMapper.class)
public class ACLSubject extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -3051284247295002344L;

	public static final String	NAME				= "ACLNAME";
	public static final String	CONDITION_GUID		= "CONDITIONGUID";
	public static final String	CONDITION_NAME		= "CONDITIONNAME";
	public static final String	VALUE_NAME			= "VALUENAME";
	public static final String	VALUE_GUID			= "VALUEGUID";
	public static final String	MASTER_VALUE_GUID	= "MASTERVALUEGUID";
	public static final String	MASTER_VALUE_NAME	= "MASTERVALUENAME";
	public static final String	PARENT_GUID			= "PARENTGUID";
	public static final String	POSITION			= "POSITION";
	public static final String	HIERARCHY			= "HIERARCHY";
	public static final String	LEVEL				= "LEVEL";
	public static final String	LIBRARYGUID			= "LIBRARYGUID";

	private List<ACLSubject>	children			= null;

	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public AccessConditionEnum getCondition()
	{
		if (this.get(CONDITION_NAME) == null)
		{
			return null;
		}
		return AccessConditionEnum.valueOf((String) this.get(CONDITION_NAME));
	}

	public void setCondition(AccessConditionEnum condition)
	{
		if (condition == null)
		{
			this.put(CONDITION_NAME, null);
		}
		else
		{
			this.put(CONDITION_NAME, condition.name());
		}
	}

	public String getValueName()
	{
		return (String) this.get(VALUE_NAME);
	}

	public String getLibraryGuid()
	{
		return (String) this.get(LIBRARYGUID);
	}

	public void setLibraryGuid(String libraryGuid)
	{
		this.put(LIBRARYGUID, libraryGuid);
	}

	public String getParentGuid()
	{
		return (String) this.get(PARENT_GUID);
	}

	public void setParentGuid(String parentGuid)
	{
		this.put(PARENT_GUID, parentGuid);
	}

	public void setValueName(String valueName)
	{
		this.put(VALUE_NAME, valueName);
	}

	public String getValueGuid()
	{
		return (String) this.get(VALUE_GUID);
	}

	public void setValueGuid(String valueGuid)
	{
		this.put(VALUE_GUID, valueGuid);
	}

	public String getMasterValueGuid()
	{
		return (String) this.get(MASTER_VALUE_GUID);
	}

	public void setMasterValueGuid(String masterValueGuid)
	{
		this.put(MASTER_VALUE_GUID, masterValueGuid);
	}

	public String getMasterValueName()
	{
		return (String) this.get(MASTER_VALUE_NAME);
	}

	public void setMasterValueName(String masterValueName)
	{
		this.put(MASTER_VALUE_NAME, masterValueName);
	}

	public int getPosition()
	{
		return ((Number) this.get(POSITION)).intValue();
	}

	public void setPosition(int position)
	{
		this.put(POSITION, new BigDecimal(String.valueOf(position)));
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
		return ((Number) this.get(LEVEL)).intValue();
	}

	public List<ACLSubject> getChildren()
	{
		return this.children;
	}

	public void setChildren(List<ACLSubject> children)
	{
		this.children = children;
	}
}
