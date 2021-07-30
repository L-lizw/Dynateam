/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateInfo
 * WangLHB Jan 9, 2012
 */
package dyna.common.bean.model.wf.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dyna.common.dto.template.wft.WorkflowTemplateActAdvnoticeInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.dto.template.wft.WorkflowTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeBoInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeRTInfo;
import dyna.common.util.CloneUtils;
import dyna.common.util.SetUtils;

/**
 * @author WangLHB
 * 
 */
public class WorkflowTemplate implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long											serialVersionUID		= 904737217884318579L;

	private WorkflowTemplateInfo										WorkflowTemplateInfo	= null;

	// 观察人
	// 使用人
	// 启动通知人
	// 结束通知人
	private Map<String, Map<String, WorkflowTemplateActPerformerInfo>>	actperformerMap			= new HashMap<String, Map<String, WorkflowTemplateActPerformerInfo>>();

	// 开始流程通知设置
	private WorkflowTemplateActAdvnoticeInfo							startUpAdvnotice		= null;
	// 结束流程通知设置
	private WorkflowTemplateActAdvnoticeInfo							completeAdvnotice		= null;

	private Map<String, WorkflowTemplateAct>							templateActMap			= new HashMap<String, WorkflowTemplateAct>();

	private Map<String, WorkflowTemplateScopeBoInfo>					scopeBOMap				= new HashMap<String, WorkflowTemplateScopeBoInfo>();

	private Map<String, WorkflowTemplateScopeRTInfo>					scopeRTMap				= new HashMap<String, WorkflowTemplateScopeRTInfo>();

	public WorkflowTemplate()
	{
		this.WorkflowTemplateInfo = new WorkflowTemplateInfo();
	}

	public WorkflowTemplate(WorkflowTemplateInfo wfTemplateInfo)
	{
		this.WorkflowTemplateInfo = wfTemplateInfo;
	}

	public WorkflowTemplateInfo getWorkflowTemplateInfo()
	{
		return WorkflowTemplateInfo;
	}

	public void setWorkflowTemplateInfo(WorkflowTemplateInfo workflowTemplateInfo)
	{
		WorkflowTemplateInfo = workflowTemplateInfo;
	}

	public Map<String, Map<String, WorkflowTemplateActPerformerInfo>> getActperformerMap()
	{
		return this.actperformerMap;
	}

	public void setActperformerMap(Map<String, Map<String, WorkflowTemplateActPerformerInfo>> actperformerMap)
	{
		this.actperformerMap = actperformerMap;
	}

	public Map<String, WorkflowTemplateScopeBoInfo> getScopeBOMap()
	{
		return scopeBOMap;
	}

	public void setScopeBOMap(Map<String, WorkflowTemplateScopeBoInfo> scopeBOMap)
	{
		this.scopeBOMap = scopeBOMap;
	}

	public Map<String, WorkflowTemplateScopeRTInfo> getScopeRTMap()
	{
		return scopeRTMap;
	}

	public void setScopeRTMap(Map<String, WorkflowTemplateScopeRTInfo> scopeRTMap)
	{
		this.scopeRTMap = scopeRTMap;
	}

	/**
	 * @return the observerList
	 */
	public List<WorkflowTemplateActPerformerInfo> getObserverList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_OBSERVER);
	}

	/**
	 * @param observerList
	 *            the observerList to set
	 */
	public void setObserverList(List<WorkflowTemplateActPerformerInfo> observerList)
	{
		this.setPerformerListByType(observerList, WorkflowTemplateActPerformerInfo.NOTICETYPE_OBSERVER);
	}

	/**
	 * @return the useList
	 */
	public List<WorkflowTemplateActPerformerInfo> getUseList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ORIGINATOR);
	}

	/**
	 * @param useList
	 *            the useList to set
	 */
	public void setUseList(List<WorkflowTemplateActPerformerInfo> useList)
	{
		this.setPerformerListByType(useList, WorkflowTemplateActPerformerInfo.NOTICETYPE_ORIGINATOR);
	}

	/**
	 * @return the startUpNotifierList
	 */
	public List<WorkflowTemplateActPerformerInfo> getStartUpNotifierList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_OPENNOTICE);
	}

	/**
	 * @param startUpNotifierList
	 *            the startUpNotifierList to set
	 */
	public void setStartUpNotifierList(List<WorkflowTemplateActPerformerInfo> startUpNotifierList)
	{
		this.setPerformerListByType(startUpNotifierList, WorkflowTemplateActPerformerInfo.NOTICETYPE_OPENNOTICE);
	}

	/**
	 * @return the completeNotifierList
	 */
	public List<WorkflowTemplateActPerformerInfo> getCompleteNotifierList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_CLOSENOTICE);
	}

	/**
	 * @param completeNotifierList
	 *            the completeNotifierList to set
	 */
	public void setCompleteNotifierList(List<WorkflowTemplateActPerformerInfo> completeNotifierList)
	{
		this.setPerformerListByType(completeNotifierList, WorkflowTemplateActPerformerInfo.NOTICETYPE_CLOSENOTICE);
	}

	/**
	 * @return the startUpAdvnotice
	 */
	public WorkflowTemplateActAdvnoticeInfo getStartUpAdvnotice()
	{
		return this.startUpAdvnotice;
	}

	/**
	 * @param startUpAdvnotice
	 *            the startUpAdvnotice to set
	 */
	public void setStartUpAdvnotice(WorkflowTemplateActAdvnoticeInfo startUpAdvnotice)
	{
		this.startUpAdvnotice = startUpAdvnotice;
	}

	/**
	 * @return the completeAdvnotice
	 */
	public WorkflowTemplateActAdvnoticeInfo getCompleteAdvnotice()
	{
		return this.completeAdvnotice;
	}

	/**
	 * @param completeAdvnotice
	 *            the completeAdvnotice to set
	 */
	public void setCompleteAdvnotice(WorkflowTemplateActAdvnoticeInfo completeAdvnotice)
	{
		this.completeAdvnotice = completeAdvnotice;
	}

	/**
	 * @return the listScopeBO
	 */
	public List<WorkflowTemplateScopeBoInfo> getListScopeBO()
	{
		List<WorkflowTemplateScopeBoInfo> resultList = new ArrayList<WorkflowTemplateScopeBoInfo>();
		if (!SetUtils.isNullMap(scopeBOMap))
		{
			resultList.addAll(scopeBOMap.values());
		}
		return resultList;
	}

	/**
	 * @param listScopeBO
	 *            the listScopeBO to set
	 */
	public void setListScopeBO(List<WorkflowTemplateScopeBoInfo> listScopeBO)
	{
		Map<String, WorkflowTemplateScopeBoInfo> scopeBOmap = new HashMap<String, WorkflowTemplateScopeBoInfo>();
		if (!SetUtils.isNullList(listScopeBO))
		{
			for (WorkflowTemplateScopeBoInfo dto : listScopeBO)
			{
				scopeBOmap.put(dto.getGuid(), dto);
			}
		}
		this.scopeBOMap = scopeBOmap;
	}

	public List<WorkflowTemplateAct> getTemplateActList()
	{
		List<WorkflowTemplateAct> resultList = new ArrayList<WorkflowTemplateAct>();
		if (!SetUtils.isNullMap(templateActMap))
		{
			resultList.addAll(templateActMap.values());
		}
		return resultList;
	}

	public void setTemplateActList(List<WorkflowTemplateAct> templateActList)
	{
		Map<String, WorkflowTemplateAct> workflowTemplateActMap = new HashMap<String, WorkflowTemplateAct>();
		if (!SetUtils.isNullList(templateActList))
		{
			for (WorkflowTemplateAct wfTemplateAct : templateActList)
			{
				workflowTemplateActMap.put(wfTemplateAct.getWorkflowTemplateActInfo().getGuid(), wfTemplateAct);
			}
		}
		this.templateActMap = workflowTemplateActMap;
	}

	public WorkflowTemplateAct getTemplateAct(String actrtName)
	{
		if (!SetUtils.isNullMap(this.templateActMap))
		{
			for (Map.Entry<String, WorkflowTemplateAct> entry : this.templateActMap.entrySet())
			{
				WorkflowTemplateAct templateActrt = entry.getValue();
				WorkflowTemplateActInfo wfTemplateActDto = templateActrt.getWorkflowTemplateActInfo();
				if (wfTemplateActDto != null && wfTemplateActDto.getActrtName().equals(actrtName))
				{
					return templateActrt;
				}
			}
		}
		return null;
	}

	public Map<String, WorkflowTemplateAct> getTemplateActMap()
	{
		return templateActMap;
	}

	public void setTemplateActMap(Map<String, WorkflowTemplateAct> templateActMap)
	{
		this.templateActMap = templateActMap;
	}

	public List<WorkflowTemplateScopeRTInfo> getListScopeRT()
	{
		List<WorkflowTemplateScopeRTInfo> resultList = new ArrayList<WorkflowTemplateScopeRTInfo>();
		if (!SetUtils.isNullMap(scopeRTMap))
		{
			resultList.addAll(scopeRTMap.values());
		}
		return resultList;
	}

	public void setListScopeRT(List<WorkflowTemplateScopeRTInfo> listScopeRT)
	{
		Map<String, WorkflowTemplateScopeRTInfo> scopeRTmap = new HashMap<String, WorkflowTemplateScopeRTInfo>();
		if (!SetUtils.isNullList(listScopeRT))
		{
			for (WorkflowTemplateScopeRTInfo dto : listScopeRT)
			{
				scopeRTmap.put(dto.getGuid(), dto);
			}
		}
		this.scopeRTMap = scopeRTmap;
	}

	public void clearForCreate()
	{
		if (this.WorkflowTemplateInfo != null)
		{
			this.WorkflowTemplateInfo.clearForCreate();
		}
		if (this.startUpAdvnotice != null)
		{
			startUpAdvnotice.clearForCreate();
		}
		if (this.completeAdvnotice != null)
		{
			completeAdvnotice.clearForCreate();
		}
		if (!SetUtils.isNullMap(actperformerMap))
		{
			for (Map.Entry<String, Map<String, WorkflowTemplateActPerformerInfo>> entry : actperformerMap.entrySet())
			{
				Map<String, WorkflowTemplateActPerformerInfo> typeMap = entry.getValue();
				if (!SetUtils.isNullMap(typeMap))
				{
					for (Map.Entry<String, WorkflowTemplateActPerformerInfo> entry_ : typeMap.entrySet())
					{
						entry_.getValue().clearForCreate();
					}
				}
			}
		}
		if (!SetUtils.isNullMap(scopeBOMap))
		{
			for (Map.Entry<String, WorkflowTemplateScopeBoInfo> entry : this.scopeBOMap.entrySet())
			{
				entry.getValue().clearForCreate();
			}
		}

		if (!SetUtils.isNullMap(scopeRTMap))
		{
			for (Map.Entry<String, WorkflowTemplateScopeRTInfo> entry : this.scopeRTMap.entrySet())
			{
				entry.getValue().clearForCreate();
			}
		}
	}

	private List<WorkflowTemplateActPerformerInfo> getPerformerListByType(String noticeType)
	{
		Map<String, WorkflowTemplateActPerformerInfo> map = this.actperformerMap.get(noticeType);
		List<WorkflowTemplateActPerformerInfo> resultList = new ArrayList<WorkflowTemplateActPerformerInfo>();
		if (!SetUtils.isNullMap(map))
		{
			resultList.addAll(map.values());
		}
		return resultList;
	}

	private void setPerformerListByType(List<WorkflowTemplateActPerformerInfo> performerList, String noticeType)
	{
		Map<String, WorkflowTemplateActPerformerInfo> performerDtoMap = this.actperformerMap.get(noticeType);

		if (SetUtils.isNullList(performerList))
		{
			this.actperformerMap.put(noticeType, new HashMap<String, WorkflowTemplateActPerformerInfo>());
			return;
		}
		if (performerDtoMap == null)
		{
			performerDtoMap = new HashMap<String, WorkflowTemplateActPerformerInfo>();
		}
		performerDtoMap.clear();
		for (WorkflowTemplateActPerformerInfo dto : performerList)
		{
			performerDtoMap.put(dto.getGuid(), dto);
		}
		this.actperformerMap.put(noticeType, performerDtoMap);
	}

	@Override
	public WorkflowTemplate clone()
	{
		return CloneUtils.clone(this);
	}

	public boolean containsScropBO(String boName)
	{
		if (scopeBOMap != null)
		{
			for (Entry<String, WorkflowTemplateScopeBoInfo> dto : scopeBOMap.entrySet())
			{
				if (boName != null && boName.equalsIgnoreCase(dto.getValue().getBOName()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
