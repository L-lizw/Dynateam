/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileOpenSubject
 * wangweixia 2012-9-5
 */
package dyna.common.dto;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.AccessConditionEnum;

/**
 * 打开文件配置时的基本信息块
 * 
 * @author wangweixia
 * 
 */
@Cache
public class FileOpenSubject extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5992208612018445119L;

	// 基本信息：名称
	public static final String	NAME				= "RULENAME";
	// 权限类型GUID
	public static final String	CONDITION_GUID		= "CONDITIONGUID";
	// 权限类型对应的具体名称
	public static final String	CONDITION_NAME		= "CONDITIONNAME";
	// 具体的CLASS/TYPE/STATUS的名称
	public static final String	VALUE_NAME			= "VALUENAME";
	// 具体的CLASS/TYPE/STATUS
	public static final String	VALUE_GUID			= "VALUEGUID";
	// 当子项不为空时存放此对象的Guid
	public static final String	MASTER_VALUE_GUID	= "MASTERVALUEGUID";
	// 当子项不为空时存放此对象的名称
	public static final String	MASTER_VALUE_NAME	= "MASTERVALUENAME";
	// 根节点的Guid
	public static final String	PARENT_GUID			= "PARENTGUID";
	// 在树的同一层级中的顺序(由上到下)
	public static final String	POSITION			= "POSITION";

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
		this.put(POSITION, BigDecimal.valueOf(position));
	}

}
