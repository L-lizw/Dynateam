/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplate
 * WangLHB Jan 6, 2012
 */
package dyna.common.bean.model.wf.template;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.util.SetUtils;

/**
 * 工作流模板Bean
 * 
 * @author WangLHB
 * 
 */
public class WorkflowTemplateVo implements Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long					serialVersionUID	= 8374134690425407916L;

	private WorkflowTemplate					WorkflowTemplate	= null;

	/**
	 * 工作流活动节点名与工作流模板活动设置键值对
	 */
	private Map<String, WorkflowTemplateAct>	templateActivityMap	= null;

	/**
	 * @return the templateActivityMap
	 */
	public Map<String, WorkflowTemplateAct> getTemplateActivityMap()
	{
		return this.templateActivityMap;
	}

	public WorkflowTemplateAct getWorkflowTemplateActivity(String activityName)
	{
		return this.templateActivityMap == null ? null : this.templateActivityMap.get(activityName);
	}

	public void addWorkflowTemplateActivity(String activityName, WorkflowTemplateAct templateActivity)
	{
		if (this.templateActivityMap == null)
		{
			this.templateActivityMap = new HashMap<String, WorkflowTemplateAct>();
		}

		this.templateActivityMap.put(activityName, templateActivity);
	}

	/**
	 * @param templateActivityMap
	 *            the templateActivityMap to set
	 */
	public void setTemplateActivityMap(Map<String, WorkflowTemplateAct> templateActivityMap)
	{
		this.templateActivityMap = templateActivityMap;
	}

	/**
	 * @return the templateInfo
	 */
	public WorkflowTemplate getTemplate()
	{
		return this.WorkflowTemplate;
	}

	/**
	 * @param templateInfo
	 *            the templateInfo to set
	 */
	public void setTemplate(WorkflowTemplate template)
	{
		this.WorkflowTemplate = template;
	}

	public List<WorkflowTemplateAct> getTemplateActList()
	{
		if (this.WorkflowTemplate == null)
		{
			return null;
		}
		return this.WorkflowTemplate.getTemplateActList();
	}

	public void clearForCreate()
	{
		if (WorkflowTemplate != null)
		{
			WorkflowTemplate.clearForCreate();
		}
		if (templateActivityMap != null)
		{
			for (WorkflowTemplateAct act : templateActivityMap.values())
			{
				if (act != null)
				{
					act.clearForCreate();
				}
			}
		}
	}

	public String getName()
	{
		if (this.WorkflowTemplate == null || this.WorkflowTemplate.getWorkflowTemplateInfo() == null)
		{
			return null;
		}
		return this.WorkflowTemplate.getWorkflowTemplateInfo().getName();
	}

	public void setName(String name)
	{
		if (this.WorkflowTemplate != null && this.WorkflowTemplate.getWorkflowTemplateInfo() != null)
		{
			this.WorkflowTemplate.getWorkflowTemplateInfo().setName(name);
		}
	}

	public String getId()
	{
		if (this.WorkflowTemplate == null || this.WorkflowTemplate.getWorkflowTemplateInfo() == null)
		{
			return null;
		}
		return this.WorkflowTemplate.getWorkflowTemplateInfo().getId();
	}

	public void setId(String templateId)
	{
		if (this.WorkflowTemplate != null && this.WorkflowTemplate.getWorkflowTemplateInfo() != null)
		{
			this.WorkflowTemplate.getWorkflowTemplateInfo().setId(templateId);
		}
	}

	@Override
	public WorkflowTemplateVo clone()
	{
		// TODO Auto-generated method stub
		WorkflowTemplateVo workflowTemplateVo = new WorkflowTemplateVo();
		workflowTemplateVo.setTemplate(this.WorkflowTemplate.clone());
		Map<String, WorkflowTemplateAct> templateActivityMap_ = new HashMap<String, WorkflowTemplateAct>();
		if (!SetUtils.isNullMap(templateActivityMap))
		{
			for (Map.Entry<String, WorkflowTemplateAct> entry : templateActivityMap.entrySet())
			{
				WorkflowTemplateAct workflowTemplateAct = entry.getValue();
				templateActivityMap_.put(entry.getKey(), workflowTemplateAct.clone());
			}
		}
		workflowTemplateVo.setTemplateActivityMap(templateActivityMap_);
		return workflowTemplateVo;
	}

}
