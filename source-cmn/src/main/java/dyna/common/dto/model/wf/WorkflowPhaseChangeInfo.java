/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowPhaseChange
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import java.io.Serializable;

/**
 * 生命周期阶段转变
 * 
 * @author Jiagang
 * 
 */
public class WorkflowPhaseChangeInfo implements Cloneable, Serializable
{
	private static final long	serialVersionUID	= 8717952182962274409L;

	private String				lifecycle			= null;
	private String				fromPhase			= null;
	private String				toPhase				= null;

	/**
	 * @param lifecycle
	 *            the lifecycle to set
	 */
	public void setLifecycle(String lifecycle)
	{
		this.lifecycle = lifecycle;
	}

	/**
	 * @return the lifecycle
	 */
	public String getLifecycle()
	{
		return this.lifecycle;
	}

	/**
	 * @param fromPhase
	 *            the fromPhase to set
	 */
	public void setFromPhase(String fromPhase)
	{
		this.fromPhase = fromPhase;
	}

	/**
	 * @return the fromPhase
	 */
	public String getFromPhase()
	{
		return this.fromPhase;
	}

	/**
	 * @param toPhase
	 *            the toPhase to set
	 */
	public void setToPhase(String toPhase)
	{
		this.toPhase = toPhase;
	}

	/**
	 * @return the toPhase
	 */
	public String getToPhase()
	{
		return this.toPhase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this.getClass() == obj.getClass())
		{
			WorkflowPhaseChangeInfo compareObj = (WorkflowPhaseChangeInfo) obj;

			if ((this.getLifecycle() == null && compareObj.getLifecycle() == null || this.getLifecycle().equalsIgnoreCase(compareObj.getLifecycle()))
					&& (this.getFromPhase() == null && compareObj.getFromPhase() == null || this.getFromPhase().equalsIgnoreCase(compareObj.getFromPhase()))
					&& (this.getToPhase() == null && compareObj.getToPhase() == null || this.getToPhase().equalsIgnoreCase(compareObj.getToPhase())))
			{
				return true;
			}
		}
		return false;
	}
}
