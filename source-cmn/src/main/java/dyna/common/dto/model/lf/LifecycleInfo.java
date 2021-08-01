/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecycleObject
 * Jiagang 2010-9-20
 */
package dyna.common.dto.model.lf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.lf.LifecycleInfoMapper;

/**
 * 生命周期对象
 * 
 * @author Jiagang
 * 
 */
@Cache
@EntryMapper(LifecycleInfoMapper.class)
public class LifecycleInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -4575865822004868692L;

	public static final String	NAME				= "LCMNAME";

	public static final String	DESCRIPTION			= "DESCRIPTION";

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

	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String desc)
	{
		super.put(DESCRIPTION, desc);
	}
}
