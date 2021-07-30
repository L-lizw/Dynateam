/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActClass
 * WangLHB Jan 6, 2012
 */
package dyna.common.bean.model.wf.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.dto.template.wft.WorkflowTemplateActClassInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassRelationInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassUIInfo;
import dyna.common.systemenum.WFTemplateRelationTypeEnum;
import dyna.common.util.SetUtils;

/**
 * 工作流模板活动节点class设置 Bean
 * 
 * @author WangLHB
 * 
 */
public class WorkflowTemplateActClass implements Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long									serialVersionUID				= -7130352152192777523L;

	private WorkflowTemplateActClassInfo						workflowTemplateActClassInfo	= null;

	// class对应的可修改ui
	private Map<String, WorkflowTemplateActClassUIInfo>			actClassUIMap					= new HashMap<String, WorkflowTemplateActClassUIInfo>();

	// class对应的可修改关系
	private Map<String, WorkflowTemplateActClassRelationInfo>	actClassRelationMap				= new HashMap<String, WorkflowTemplateActClassRelationInfo>();

	public WorkflowTemplateActClass()
	{
		this.workflowTemplateActClassInfo = new WorkflowTemplateActClassInfo();
	}

	public WorkflowTemplateActClass(WorkflowTemplateActClassInfo WorkflowTemplateActClassInfo)
	{
		this.workflowTemplateActClassInfo = WorkflowTemplateActClassInfo;
	}

	public WorkflowTemplateActClassInfo getWorkflowTemplateActClassInfo()
	{
		return workflowTemplateActClassInfo;
	}

	public void setWorkflowTemplateActClassInfo(WorkflowTemplateActClassInfo workflowTemplateActClassInfo)
	{
		this.workflowTemplateActClassInfo = workflowTemplateActClassInfo;
	}

	/**
	 * @return the actClassUIList
	 */
	public List<WorkflowTemplateActClassUIInfo> getActClassUIList()
	{
		List<WorkflowTemplateActClassUIInfo> resultList = new ArrayList<WorkflowTemplateActClassUIInfo>();
		if (!SetUtils.isNullMap(actClassUIMap))
		{
			resultList.addAll(actClassUIMap.values());
		}
		return resultList;
	}

	/**
	 * @param actClassUIList
	 *            the actClassUIList to set
	 */
	public void setActClassUIList(List<WorkflowTemplateActClassUIInfo> actClassUIList)
	{
		Map<String, WorkflowTemplateActClassUIInfo> actClassUIMap = new HashMap<String, WorkflowTemplateActClassUIInfo>();
		if (!SetUtils.isNullList(actClassUIList))
		{
			for (WorkflowTemplateActClassUIInfo info : actClassUIList)
			{
				actClassUIMap.put(info.getGuid(), info);
			}
		}
		this.actClassUIMap = actClassUIMap;
	}

	public Map<String, WorkflowTemplateActClassUIInfo> getActClassUIMap()
	{
		return actClassUIMap;
	}

	public void setActClassUIMap(Map<String, WorkflowTemplateActClassUIInfo> actClassUIMap)
	{
		this.actClassUIMap = actClassUIMap;
	}

	/**
	 * @return the actClassRelationList
	 */
	public List<WorkflowTemplateActClassRelationInfo> getActClassRelationList()
	{
		List<WorkflowTemplateActClassRelationInfo> resultList = new ArrayList<WorkflowTemplateActClassRelationInfo>();
		if (!SetUtils.isNullMap(actClassRelationMap))
		{
			resultList.addAll(actClassRelationMap.values());
		}
		return resultList;
	}

	/**
	 * @param actClassRelationList
	 *            the actClassRelationList to set
	 */
	public void setActClassRelationList(List<WorkflowTemplateActClassRelationInfo> actClassRelationList)
	{
		Map<String, WorkflowTemplateActClassRelationInfo> actClassRelationMap = new HashMap<String, WorkflowTemplateActClassRelationInfo>();
		if (!SetUtils.isNullList(actClassRelationList))
		{
			for (WorkflowTemplateActClassRelationInfo info:actClassRelationList)
			{
				actClassRelationMap.put(info.getGuid(), info);
			}
		}
		this.actClassRelationMap = actClassRelationMap;
	}

	public Map<String, WorkflowTemplateActClassRelationInfo> getActClassRelationMap()
	{
		return actClassRelationMap;
	}

	public void setActClassRelationMap(Map<String, WorkflowTemplateActClassRelationInfo> actClassRelationMap)
	{
		this.actClassRelationMap = actClassRelationMap;
	}

	public List<WorkflowTemplateActClassRelationInfo> listActClassRelationByType(WFTemplateRelationTypeEnum relationType)
	{
		List<WorkflowTemplateActClassRelationInfo> actClassRelationList = this.getActClassRelationList();
		List<WorkflowTemplateActClassRelationInfo> relationTypeList = new ArrayList<WorkflowTemplateActClassRelationInfo>();
		if (relationType == null)
		{
			return actClassRelationList;
		}

		if (actClassRelationList != null)
		{
			for (WorkflowTemplateActClassRelationInfo relation : actClassRelationList)
			{
				if (relationType == relation.getRelationType())
				{
					relationTypeList.add(relation);
				}
			}
		}

		return relationTypeList;
	}

	public void clearForCreate()
	{
		if (this.workflowTemplateActClassInfo != null)
		{
			this.workflowTemplateActClassInfo.clearForCreate();
		}
		if (!SetUtils.isNullMap(actClassUIMap))
		{
			for (Map.Entry<String, WorkflowTemplateActClassUIInfo> entry : actClassUIMap.entrySet())
			{
				if (entry.getValue() != null)
				{
					entry.getValue().clearForCreate();
				}
			}
		}
		if (!SetUtils.isNullMap(actClassRelationMap))
		{
			for (Map.Entry<String, WorkflowTemplateActClassRelationInfo> entry : actClassRelationMap.entrySet())
			{
				if (entry.getValue() != null)
				{
					entry.getValue().clearForCreate();
				}
			}
		}

	}

	@Override
	public WorkflowTemplateActClass clone()
	{
		WorkflowTemplateActClass actClass = new WorkflowTemplateActClass(this.workflowTemplateActClassInfo.clone());
		if (!SetUtils.isNullMap(this.actClassUIMap))
		{
			Map<String, WorkflowTemplateActClassUIInfo> actClassUIMap_ = new HashMap<String, WorkflowTemplateActClassUIInfo>();
			for (Map.Entry<String, WorkflowTemplateActClassUIInfo> entry : this.actClassUIMap.entrySet())
			{
				actClassUIMap_.put(entry.getKey(), entry.getValue().clone());
			}
			actClass.setActClassUIMap(actClassUIMap_);
		}
		if (!SetUtils.isNullMap(this.actClassRelationMap))
		{
			Map<String, WorkflowTemplateActClassRelationInfo> actClassRelationMap_ = new HashMap<String, WorkflowTemplateActClassRelationInfo>();

			for (Map.Entry<String, WorkflowTemplateActClassRelationInfo> entry : this.actClassRelationMap.entrySet())
			{
				actClassRelationMap_.put(entry.getKey(), entry.getValue().clone());
			}
			actClass.setActClassRelationMap(actClassRelationMap_);
		}
		return actClass;
	}
}
