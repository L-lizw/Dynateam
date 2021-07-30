/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecycleObject
 * Jiagang 2010-9-20
 */
package dyna.common.bean.model.lf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.lf.LifecycleInfo;

/**
 * 生命周期对象
 * 
 * @author Jiagang
 * 
 */
public class Lifecycle extends SystemObjectImpl implements SystemObject, Comparator<LifecyclePhase>
{
	private static final long		serialVersionUID	= -4575865822004868692L;

	private final LifecycleInfo		info;

	private List<LifecyclePhase>	lifecyclePhaseList	= null;

	public Lifecycle()
	{
		this.info = new LifecycleInfo();
	}

	public Lifecycle(LifecycleInfo lifecycleInfo)
	{
		this.info = lifecycleInfo;
	}

	public LifecycleInfo getInfo()
	{
		return this.info;
	}

	@Override
	public String getName()
	{
		return this.info.getName();
	}

	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	public String getDescription()
	{
		return this.info.getDescription();
	}

	public void setDescription(String desc)
	{
		this.info.setDescription(desc);
	}

	public List<LifecyclePhase> getLifecyclePhaseList()
	{
		if (this.lifecyclePhaseList == null)
		{
			this.lifecyclePhaseList = new ArrayList<LifecyclePhase>();
		}
		return lifecyclePhaseList;
	}

	public void setLifecyclePhaseList(List<LifecyclePhase> lifecyclePhaseList)
	{
		this.lifecyclePhaseList = lifecyclePhaseList;
		if (lifecyclePhaseList != null)
		{
			Collections.sort(lifecyclePhaseList, this);
		}
	}

	public LifecyclePhase getLifecyclePhaseBySequence(int sequence)
	{
		for (LifecyclePhase lifecyclePhase : this.lifecyclePhaseList)
		{
			if (lifecyclePhase.getLifecycleSequence() == sequence)
			{
				return lifecyclePhase;
			}
		}
		return null;
	}

	public void addPhase(LifecyclePhase phase)
	{
		if (this.lifecyclePhaseList == null)
		{
			this.lifecyclePhaseList = new ArrayList<LifecyclePhase>();
		}
		this.lifecyclePhaseList.add(phase);
		Collections.sort(lifecyclePhaseList, this);
	}

	public void removePhase(LifecyclePhase phase)
	{
		if (this.lifecyclePhaseList != null)
		{
			Iterator<LifecyclePhase> it = this.lifecyclePhaseList.iterator();
			while (it.hasNext())
			{
				LifecyclePhase lifecyclePhase = it.next();
				if (lifecyclePhase.getInfo().getGuid().equals(phase.getInfo().getGuid()))
				{
					it.remove();
				}
			}

		}
	}

	@Override
	public int compare(LifecyclePhase o1, LifecyclePhase o2)
	{
		if (o1 == null)
		{
			return o2 == null ? 0 : -1;
		}
		if (o2 == null)
		{
			return 1;
		}
		return o1.getLifecycleSequence() - o2.getLifecycleSequence();
	}
}
