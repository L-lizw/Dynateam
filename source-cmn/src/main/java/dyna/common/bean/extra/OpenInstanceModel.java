/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: OpenInstanceModel
 * WangLHB Apr 28, 2012
 */
package dyna.common.bean.extra;

import java.io.Serializable;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.template.relation.RelationTemplateInfo;

/**
 * @author WangLHB
 * 
 */
public class OpenInstanceModel implements Serializable
{
	/**
	 * 
	 */
	private static final long			serialVersionUID		= 1L;
	private FoundationObject			instance				= null;
	private FoundationObject			master					= null;
	private List<FoundationObject>		historyRevision			= null;

	private List<RelationTemplateInfo>	relationTemplateList	= null;

	private List<BOMView>				bomViewList				= null;
	private List<ViewObject>			relationList			= null;

	/**
	 * @return the instance
	 */
	public FoundationObject getInstance()
	{
		return this.instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public void setInstance(FoundationObject instance)
	{
		this.instance = instance;
	}

	/**
	 * @return the master
	 */
	public FoundationObject getMaster()
	{
		return this.master;
	}

	/**
	 * @param master
	 *            the master to set
	 */
	public void setMaster(FoundationObject master)
	{
		this.master = master;
	}

	/**
	 * @return the historyRevision
	 */
	public List<FoundationObject> getHistoryRevision()
	{
		return this.historyRevision;
	}

	/**
	 * @param historyRevision
	 *            the historyRevision to set
	 */
	public void setHistoryRevision(List<FoundationObject> historyRevision)
	{
		this.historyRevision = historyRevision;
	}

	/**
	 * @return the relationTemplateList
	 */
	public List<RelationTemplateInfo> getRelationTemplateList()
	{
		return this.relationTemplateList;
	}

	/**
	 * @param relationTemplateList
	 *            the relationTemplateList to set
	 */
	public void setRelationTemplateList(List<RelationTemplateInfo> relationTemplateList)
	{
		this.relationTemplateList = relationTemplateList;
	}

	/**
	 * @return the bomViewList
	 */
	public List<BOMView> getBomViewList()
	{
		return this.bomViewList;
	}

	/**
	 * @param bomViewList
	 *            the bomViewList to set
	 */
	public void setBomViewList(List<BOMView> bomViewList)
	{
		this.bomViewList = bomViewList;
	}

	/**
	 * @return the relationList
	 */
	public List<ViewObject> getRelationList()
	{
		return this.relationList;
	}

	/**
	 * @param relationList
	 *            the relationList to set
	 */
	public void setRelationList(List<ViewObject> relationList)
	{
		this.relationList = relationList;
	}

}
