/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GraphActivity
 * zhanghj 2010-12-24
 */
package dyna.common.dto.wf;

import java.io.Serializable;

import dyna.common.systemenum.ActRuntimeModeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * @author zhanghj
 *
 */
public class GraphRuntimeActivity implements Serializable
{
	private static final long		serialVersionUID	= 928281769776721710L;
	private ActivityRuntime			data			= null;
	private int					level	= 0;
	private int					sameLevelNum	= 0;
	private GraphRuntimeActivity		parent			= null;
	private boolean					isRouteJoin		= false;
	private boolean					isOrAnd			= false;	// false表示and


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
	public ActivityRuntime getData()
	{
		return this.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(ActivityRuntime data)
	{
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public WorkflowActivityType getActType()
	{
		return this.data.getActType();
	}

	public String getTitle(LanguageEnum lang)
	{
		return this.data.getTitle(lang);
	}

	public String getName()
	{
		return this.data.getName();
	}

	/**
	 * @return the parent
	 */
	public GraphRuntimeActivity getParent()
	{
		return this.parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(GraphRuntimeActivity parent)
	{
		this.parent = parent;
	}

	/**
	 * @return the isRouteJoin
	 */
	public boolean isRouteJoin()
	{
		return this.isRouteJoin;
	}

	/**
	 * @param isRouteJoin
	 *            the isRouteJoin to set
	 */
	public void setRouteJoin(boolean isRouteJoin)
	{
		this.isRouteJoin = isRouteJoin;
	}

	public Boolean isFinished()
	{
		return this.data.isFinished();
	}

	public String getGuid()
	{
		return this.data.getGuid();
	}

	public ActRuntimeModeEnum getActMode()
	{
		return this.data.getActMode();
	}

	/**
	 * @return the isOrAnd
	 */
	public boolean isOrAnd()
	{
		return this.isOrAnd;
	}

	/**
	 * @param isOrAnd
	 *            the isOrAnd to set
	 */
	public void setOrAnd(boolean isOrAnd)
	{
		this.isOrAnd = isOrAnd;
	}

}
