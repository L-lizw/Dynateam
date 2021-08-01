/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecyclePhaseObject
 * Jiagang 2010-9-20
 */
package dyna.common.dto.model.lf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.lf.LifecyclePhaseInfoMapper;

import java.math.BigDecimal;

/**
 * 生命周期阶段对象
 * 
 * @author Jiagang
 * 
 */
@Cache
@EntryMapper(LifecyclePhaseInfoMapper.class)
public class LifecyclePhaseInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -6207863329404221489L;

	public static final String	MASTERFK			= "MASTERFK";

	public static final String	NAME				= "ITEMNAME";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	TITLE				= "TITLE";

	public static final String	SEQUENCE			= "DATASEQ";

	/**
	 * @return the lifecycleSequence
	 */
	public int getLifecycleSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	/**
	 * @param lifecycleSequence
	 *            the lifecycleSequence to set
	 */
	public void setLifecycleSequence(int lifecycleSequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(lifecycleSequence)));
	}

	public String getMasterfk()
	{
		return (String) super.get(MASTERFK);
	}

	public void setMasterfk(String masterfk)
	{
		super.put(MASTERFK, masterfk);
	}

	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String desc)
	{
		super.put(DESCRIPTION, desc);
	}

	public String getTitle()
	{
		return (String) super.get(TITLE);
	}

	public void setTitle(String title)
	{
		super.put(TITLE, title);
	}

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
}
