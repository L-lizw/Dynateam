/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GraphActivity
 * zhanghj 2010-12-24
 */
package dyna.common.dto.wf;

import java.io.Serializable;

import dyna.common.bean.model.wf.WorkflowActivity;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * @author zhanghj
 *
 */
public class GraphActivity implements Serializable
{
	private static final long	serialVersionUID	= -326199591784761773L;
	private WorkflowActivity	data				= null;
	private int					level				= 0;
	private int					sameLevelNum		= 0;
	private GraphActivity		parent				= null;

	/**
	 * @return the sameLevelNum
	 */
	public int getSameLevelNum()
	{
		return this.sameLevelNum;
	}

	/**
	 * @param sameLevelNum
	 *            the sameLevelNum to set
	 */
	public void setSameLevelNum(int sameLevelNum)
	{
		this.sameLevelNum = sameLevelNum;
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return this.level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}

	/**
	 * @return the data
	 */
	public WorkflowActivity getData()
	{
		return this.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(WorkflowActivity data)
	{
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public WorkflowActivityType getType()
	{
		return WorkflowActivityType.getEnum(this.data.getType());
	}

	public String getTitle(LanguageEnum lang)
	{
		return this.data.getWorkflowActivityInfo().getTitle(lang);
	}

	public String getName()
	{
		return this.data.getName();
	}

	/**
	 * @return the parent
	 */
	public GraphActivity getParent()
	{
		return this.parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(GraphActivity parent)
	{
		this.parent = parent;
	}

}
