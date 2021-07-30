/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLObject
 * Wanglei 2010-7-30
 */
package dyna.common.dto.acl;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.AccessFunctionConditionEnum;
import dyna.common.systemenum.ModulEnum;

/**
 * @author Lizw
 * 
 */
public class ACLFunctionObject extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -3051284247295002344L;

	public static final String	NAME				= "FUNCTIONNAME";
	// 所属根节点guid
	public static final String	PARENTGUID			= "PARENTGUID";
	// 功能所属模块（高级搜索，实例，BOM编辑...）
	public static final String	POSITION			= "POSITION";
	// 类型(用户，角色，组)
	public static final String	TYPE				= "FUNCTIONTYPE";
	// 是否需要编辑
	private boolean				needEdit			= true;

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

	public String getParentGuid()
	{
		return (String) this.get(PARENTGUID);
	}

	public void setParentGuid(String parentGuid)
	{
		this.put(PARENTGUID, parentGuid);
	}

	public ModulEnum getPosition()
	{
		return this.get(POSITION) == null ? null : ModulEnum.valueOf((String) this.get(POSITION));
	}

	public void setPosition(ModulEnum position)
	{
		this.put(POSITION, position.name());
	}

	public AccessFunctionConditionEnum getType()
	{
		if (this.get(TYPE) == null)
		{
			return null;
		}

		return AccessFunctionConditionEnum.valueOf((String) this.get(TYPE));
	}

	public void setType(AccessFunctionConditionEnum type)
	{
		this.put(TYPE, type.name());
	}

	public boolean needEdit()
	{
		return this.needEdit;
	}

	public void setEdit(boolean edit)
	{
		this.needEdit = edit;
	}

}
