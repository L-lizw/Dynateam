/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecyclePhaseObject
 * Jiagang 2010-9-20
 */
package dyna.common.bean.model.lf;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.lf.LifecycleGate;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.util.SetUtils;

/**
 * 生命周期阶段对象
 * 
 * @author Jiagang
 * 
 */
public class LifecyclePhase extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -6207863329404221489L;

	private LifecyclePhaseInfo	info				= null;

	private String				lifecycleGuid		= null;

	private List<LifecycleGate>	gateList			= null;

	public LifecyclePhase()
	{
		this.info = new LifecyclePhaseInfo();
	}

	public LifecyclePhase(LifecyclePhaseInfo info)
	{
		this.info = info;
	}

	public LifecyclePhaseInfo getInfo()
	{
		return info;
	}

	public void setInfo(LifecyclePhaseInfo info)
	{
		this.info = info;
	}

	/**
	 * @return the lifecycleSequence
	 */
	public int getLifecycleSequence()
	{
		return this.info.getLifecycleSequence();
	}

	/**
	 * @param lifecycleSequence
	 *            the lifecycleSequence to set
	 */
	public void setLifecycleSequence(int lifecycleSequence)
	{
		this.info.setLifecycleSequence(lifecycleSequence);
	}

	public String getMasterfk()
	{
		return this.info.getMasterfk();
	}

	public void setMasterfk(String masterfk)
	{
		this.info.setMasterfk(masterfk);
	}

	public String getDescription()
	{
		return this.info.getDescription();
	}

	public void setDescription(String desc)
	{
		this.info.setDescription(desc);
	}

	public String getTitle()
	{
		return this.info.getTitle();
	}

	public void setTitle(String title)
	{
		this.info.setTitle(title);
	}

	public String getName()
	{
		return this.info.getName();
	}

	public void setName(String name)
	{
		this.info.setName(name);
	}

	public String getLifecycleGuid()
	{
		return lifecycleGuid;
	}

	public void setLifecycleGuid(String lifecycleGuid)
	{
		this.lifecycleGuid = lifecycleGuid;
	}

	public List<LifecycleGate> getGateList()
	{
		if (this.gateList == null)
		{
			this.gateList = new ArrayList<LifecycleGate>();
		}
		return gateList;
	}

	public void setGateList(List<LifecycleGate> gateList)
	{
		this.gateList = gateList;
	}

	public void addGate(LifecycleGate gate)
	{
		if (this.gateList == null)
		{
			this.gateList = new ArrayList<LifecycleGate>();
		}
		this.gateList.add(gate);
	}

	public void removeGate(LifecycleGate gate)
	{
		if (this.gateList != null)
		{
			for (LifecycleGate gate_ : this.gateList)
			{
				if (gate_.getGuid().equals(gate.getGuid()))
				{
					this.gateList.remove(gate_);
					break;
				}
			}
		}
	}

	@Override
	public LifecyclePhase clone()
	{
		LifecyclePhase result = new LifecyclePhase();
		result.putAll(this);
		if (this.getInfo() != null)
		{
			result.setInfo((LifecyclePhaseInfo) this.getInfo().clone());
		}

		List<LifecycleGate> gateList = new ArrayList<LifecycleGate>();
		if (!SetUtils.isNullList(getGateList()))
		{
			for (LifecycleGate gate : getGateList())
			{
				gateList.add((LifecycleGate) gate.clone());
			}
		}
		return result;
	}
}
