package dyna.app.service.brs.dcr.checkrule;

import dyna.common.exception.ServiceRequestException;

import java.io.Serializable;

public class ClassCondition extends AbstractCondition implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4950206986612444690L;

	private String				className			= null;

	private ConditionGroup		conditionGroup		= null;

	/**
	 * 当条件为end1，conditionGroup为null的时候返回true；当条件为end2，conditionGroup为null的时候返回false
	 */
	@Override
	public boolean check() throws ServiceRequestException
	{
		this.conditionGroup.setFoundationObject(this.getFoundationObject());
		return this.conditionGroup.check();
	}

	public String getClassName()
	{
		return this.className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public ConditionGroup getConditionGroup()
	{
		return this.conditionGroup;
	}

	public void setConditionGroup(ConditionGroup conditionGroup)
	{
		this.conditionGroup = conditionGroup;
	}

}
