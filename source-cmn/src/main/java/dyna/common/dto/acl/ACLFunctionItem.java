/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLItem
 * Wanglei 2010-7-30
 */
package dyna.common.dto.acl;

import java.util.HashMap;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.AccessFunctionConditionEnum;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.StringUtils;

/**
 * @author Lizw
 * 
 */
public class ACLFunctionItem extends SystemObjectImpl implements SystemObject
{

	private static final long			serialVersionUID	= -5256245302334123561L;

	public static final String			MASTER_FK			= "MASTERFK";

	// 类型值（类型为用户则是userguid）
	public static final String			TYPEVALUE			= "TYPEVALUE";

	public static final String			TYPEVALUENAME		= "TYPEVALUENAME";

	// 功能权限(功能id-设置)，每个功能用英文分号分割
	public static final String			ACLVALUE			= "ACLVALUE";				// 查询权限

	public Map<String, PermissibleEnum>	aclMap				= null;

	public AccessFunctionConditionEnum	type				= null;

	public ACLFunctionItem()
	{
		super();
	}

	public ACLFunctionItem(String authority)
	{
		super();
		String[] values = StringUtils.splitString(authority);
		if (values == null || values.length != AuthorityEnum.values().length)
		{
			return;
		}

		if (this.aclMap == null)
		{
			this.aclMap = new HashMap<String, PermissibleEnum>();
		}

		if (values.length > 0)
		{
			for (String aclString : values)
			{
				this.aclMap.put(aclString.split("-")[0], PermissibleEnum.getPermissibleEnum(Integer.parseInt(aclString.split("-")[1])));
			}
		}
	}

	public void initAclMap()
	{
		if (this.aclMap == null)
		{
			this.aclMap = new HashMap<String, PermissibleEnum>();
		}

		this.aclMap.clear();

		String aclValue = this.getACLValue();
		if (aclValue != null)
		{
			String[] values = StringUtils.splitString(aclValue);
			if (values == null)
			{
				return;
			}

			if (values.length > 0)
			{
				for (String aclString : values)
				{
					this.aclMap.put(aclString.split("-")[0], PermissibleEnum.valueOf(aclString.split("-")[1]));
				}
			}
		}
	}

	public PermissibleEnum checkPermission(String functionID)
	{
		if (this.aclMap == null)
		{
			return PermissibleEnum.NONE;
		}

		return this.aclMap.get(functionID) == null ? PermissibleEnum.NONE : this.aclMap.get(functionID);
	}

	public String getFunctionObjectGuid()
	{
		return (String) this.get(MASTER_FK);
	}

	public void setFunctionObjectGuid(String masterGuid)
	{
		this.put(MASTER_FK, masterGuid);
	}

	public String getTypeValue()
	{
		return (String) this.get(TYPEVALUE);
	}

	public void setTypeValue(String typeValue)
	{
		this.put(TYPEVALUE, typeValue);
	}

	public String getACLValue()
	{
		return (String) this.get(ACLVALUE);
	}

	public void setACLValue(String aclValue)
	{
		this.put(ACLVALUE, aclValue);
	}

	public String getValueName()
	{
		return (String) this.get(TYPEVALUENAME);
	}

	public void setValueName(String valueName)
	{
		this.put(TYPEVALUENAME, valueName);
	}

	public AccessFunctionConditionEnum getType()
	{
		return type;
	}

	public void setType(AccessFunctionConditionEnum type)
	{
		this.type = type;
	}

	public void addPermiss(String key, PermissibleEnum value)
	{
		if (this.aclMap == null)
		{
			this.aclMap = new HashMap<String, PermissibleEnum>();
		}
		this.aclMap.put(key, value);
	}

	public void removePermiss(String key)
	{
		if (this.aclMap == null)
		{
			return;
		}
		this.aclMap.remove(key);
	}

	public Map<String, PermissibleEnum> getAclMap()
	{
		return this.aclMap;
	}

	public void setAclMap(Map<String, PermissibleEnum> aclMap)
	{
		this.aclMap = aclMap;
	}

}
