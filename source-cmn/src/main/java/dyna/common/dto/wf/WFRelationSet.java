/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WFRelationSet
 * WangLHB 3/1, 2013
 */

package dyna.common.dto.wf;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * 工作流关联审批设置Bean
 * 
 * @author WangLHB
 * 
 */
public class WFRelationSet extends SystemObjectImpl implements SystemObject
{
	// 1:仅首层，2：所有层，3：仅BOMView
	public static final String			FIRST					= "1";
	public static final String			ALL						= "2";
	public static final String			ONLYBOMVIEW				= "3";

	/**
	 * 
	 */
	private static final long			serialVersionUID		= -7800728943797597122L;

	public static final String			RELATION_NAME			= "RELATIONNAME";
	// 1:仅首层，2：所有层，3：仅BOMView
	public static final String			STRATEGY				= "STRATEGY";

	// 默认true
	public static final String			IS_BOM					= "ISBOM";

	public List<RelationTemplateInfo>	relationTemplateList	= null;

	public List<BOMTemplateInfo>		bomTemplateList			= null;

	/**
	 * @return the relationName
	 */
	public String getRelationName()
	{
		return (String) this.get(RELATION_NAME);
	}

	/**
	 * @param relationName
	 *            the relationName to set
	 */
	public void setRelationName(String relationName)
	{
		this.put(RELATION_NAME, relationName);
	}

	/**
	 * @return the strategy
	 */
	public String getStrategy()
	{
		return (String) this.get(STRATEGY);
	}

	/**
	 * @param strategy
	 *            the strategy to set
	 */
	public void setStrategy(String strategy)
	{
		this.put(STRATEGY, strategy);
	}

	/**
	 * @return the strategy
	 */
	public boolean isBOM()
	{
		String a = (String) this.get(IS_BOM);
		if (StringUtils.isNullString(a))
		{
			return true;
		}

		return BooleanUtils.getBooleanByYN(a);
	}

	/**
	 * @param strategy
	 *            the strategy to set
	 */
	public void setBOM(boolean isBOM)
	{
		this.put(IS_BOM, BooleanUtils.getBooleanStringYN(isBOM));
	}

	public List<RelationTemplateInfo> getRelationTemplateList()
	{
		return this.relationTemplateList;
	}

	public void setRelationTemplateList(List<RelationTemplateInfo> relationTemplateList)
	{
		this.relationTemplateList = relationTemplateList;
	}

	public List<BOMTemplateInfo> getBomTemplateList()
	{
		return this.bomTemplateList;
	}

	public void setBomTemplateList(List<BOMTemplateInfo> bomTemplateList)
	{
		this.bomTemplateList = bomTemplateList;
	}
}
