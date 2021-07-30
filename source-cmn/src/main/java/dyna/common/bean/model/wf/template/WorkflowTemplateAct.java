/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActivity
 * WangLHB Jan 6, 2012
 */
package dyna.common.bean.model.wf.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.dto.template.wft.WorkflowTemplateActAdvnoticeInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActCompanyInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.util.SetUtils;

/**
 * 工作流模板活动节点Bean
 * 
 * @author WangLHB
 * 
 */
public class WorkflowTemplateAct implements Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long											serialVersionUID		= -863915324012839699L;

	private WorkflowTemplateActInfo										workflowTemplateActInfo	= null;

	// 指定执行人
	// 执行人范围
	// 完成通知人
	// 超时截止日期通知人明细设置
	// 将日期通知人设置
	private Map<String, Map<String, WorkflowTemplateActPerformerInfo>>	actperformerMap			= new HashMap<String, Map<String, WorkflowTemplateActPerformerInfo>>();

	// 完成通知设置
	private WorkflowTemplateActAdvnoticeInfo							finAdvnotice			= null;

	// 超时截止日期通知人明细设置
	private WorkflowTemplateActAdvnoticeInfo							defAdvnotice			= null;

	// 将截止日期通知人设置
	private WorkflowTemplateActAdvnoticeInfo							advAdvnotice			= null;

	// boGuid 与其对应设置的键值对
	private Map<String, WorkflowTemplateActClass>						classActMap				= null;
	// guid 与其对应设置的键值对
	private Map<String, WorkflowTemplateActClass>						actrtClassMap			= null;

	// erp表单中的公司设置
	private List<WorkflowTemplateActCompanyInfo>						actCompanyList			= null;

	public WorkflowTemplateAct()
	{
		this.workflowTemplateActInfo = new WorkflowTemplateActInfo();
	}

	public WorkflowTemplateAct(WorkflowTemplateActInfo workflowTemplateActInfo)
	{
		this.workflowTemplateActInfo = workflowTemplateActInfo;
	}

	public WorkflowTemplateActInfo getWorkflowTemplateActInfo()
	{
		return workflowTemplateActInfo;
	}

	public void setWorkflowTemplateActInfo(WorkflowTemplateActInfo workflowTemplateActInfo)
	{
		this.workflowTemplateActInfo = workflowTemplateActInfo;
	}

	public List<WorkflowTemplateActPerformerInfo> listAllPerformer()
	{
		List<WorkflowTemplateActPerformerInfo> resultList = new ArrayList<WorkflowTemplateActPerformerInfo>();
		if (!SetUtils.isNullMap(actperformerMap))
		{
			for (Map<String, WorkflowTemplateActPerformerInfo> typeMap : actperformerMap.values())
				if (!SetUtils.isNullMap(typeMap))
				{
					for (Map.Entry<String, WorkflowTemplateActPerformerInfo> entry : typeMap.entrySet())
					{
						resultList.add(entry.getValue());
					}

				}

		}
		return resultList;
	}

	public void setAllPerformer(List<WorkflowTemplateActPerformerInfo> performerList)
	{
		Map<String, Map<String, WorkflowTemplateActPerformerInfo>> sourceMap = new HashMap<String, Map<String, WorkflowTemplateActPerformerInfo>>();
		if (!SetUtils.isNullList(performerList))
		{
			for (WorkflowTemplateActPerformerInfo performer : performerList)
			{
				String noticeType = performer.getNoticeType();
				Map<String, WorkflowTemplateActPerformerInfo> typeMap = sourceMap.get(noticeType);
				if (typeMap == null)
				{
					typeMap = new HashMap<String, WorkflowTemplateActPerformerInfo>();
					sourceMap.put(noticeType, typeMap);
				}
				typeMap.put(performer.getGuid(), performer);
			}
		}
		this.actperformerMap = sourceMap;
	}

	public Map<String, Map<String, WorkflowTemplateActPerformerInfo>> getActperformerMap()
	{
		return this.actperformerMap;
	}

	public void setActperformerMap(Map<String, Map<String, WorkflowTemplateActPerformerInfo>> actperformerMap)
	{
		this.actperformerMap = actperformerMap;
	}

	/**
	 * @return the executorList
	 */
	public List<WorkflowTemplateActPerformerInfo> getExecutorList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECUTOR);
	}

	/**
	 * @param executorList
	 *            the executorList to set
	 */
	public void setExecutorList(List<WorkflowTemplateActPerformerInfo> executorList)
	{
		this.setPerformerListByType(executorList, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECUTOR);
	}

	/**
	 * @return the scopeExecutorList
	 */
	public List<WorkflowTemplateActPerformerInfo> getScopeExecutorList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECSCOPE);
	}

	/**
	 * @param scopeExecutorList
	 *            the scopeExecutorList to set
	 */
	public void setScopeExecutorList(List<WorkflowTemplateActPerformerInfo> scopeExecutorList)
	{
		this.setPerformerListByType(scopeExecutorList, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECSCOPE);
	}

	/**
	 * @return the finNoticeperList
	 */
	public List<WorkflowTemplateActPerformerInfo> getFinNoticeperList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTFIN);
	}

	/**
	 * @param finNoticeperList
	 *            the finNoticeperList to set
	 */
	public void setFinNoticeperList(List<WorkflowTemplateActPerformerInfo> finNoticeperList)
	{
		this.setPerformerListByType(finNoticeperList, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTFIN);
	}

	/**
	 * @return the finAdvnotice
	 */
	public WorkflowTemplateActAdvnoticeInfo getFinAdvnotice()
	{
		return this.finAdvnotice;
	}

	/**
	 * @param finAdvnotice
	 *            the finAdvnotice to set
	 */
	public void setFinAdvnotice(WorkflowTemplateActAdvnoticeInfo finAdvnotice)
	{
		this.finAdvnotice = finAdvnotice;
	}

	/**
	 * @return the defNoticeperList
	 */
	public List<WorkflowTemplateActPerformerInfo> getDefNoticeperList()
	{
		return this.getPerformerListByType(WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF);
	}

	/**
	 * @param defNoticeperList
	 *            the defNoticeperList to set
	 */
	public void setDefNoticeperList(List<WorkflowTemplateActPerformerInfo> defNoticeperList)
	{
		this.setPerformerListByType(defNoticeperList, WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF);
	}

	/**
	 * @return the defAdvnotice
	 */
	public WorkflowTemplateActAdvnoticeInfo getDefAdvnotice()
	{
		return this.defAdvnotice;
	}

	/**
	 * @param defAdvnotice
	 *            the defAdvnotice to set
	 */
	public void setDefAdvnotice(WorkflowTemplateActAdvnoticeInfo defAdvnotice)
	{
		this.defAdvnotice = defAdvnotice;
	}

	/**
	 * @return the advNoticeperList
	 */
	public List<WorkflowTemplateActPerformerInfo> getAdvNoticeperList()
	{
		return this.getPerformerListByType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTADV);
	}

	/**
	 * @param advNoticeperList
	 *            the advNoticeperList to set
	 */
	public void setAdvNoticeperList(List<WorkflowTemplateActPerformerInfo> advNoticeperList)
	{
		this.setPerformerListByType(advNoticeperList, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTADV);
	}

	/**
	 * @return the advAdvnotice
	 */
	public WorkflowTemplateActAdvnoticeInfo getAdvAdvnotice()
	{
		return this.advAdvnotice;
	}

	/**
	 * @param advAdvnotice
	 *            the advAdvnotice to set
	 */
	public void setAdvAdvnotice(WorkflowTemplateActAdvnoticeInfo advAdvnotice)
	{
		this.advAdvnotice = advAdvnotice;
	}

	public List<WorkflowTemplateActClass> getActrtClassList()
	{
		List<WorkflowTemplateActClass> resultList = new ArrayList<WorkflowTemplateActClass>();
		if (!SetUtils.isNullMap(actrtClassMap))
		{
			resultList.addAll(actrtClassMap.values());
		}
		return resultList;
	}

	public void setActrtClassList(List<WorkflowTemplateActClass> actrtClassList)
	{
		Map<String, WorkflowTemplateActClass> workflowTemplateActclassMap = new HashMap<String, WorkflowTemplateActClass>();
		if (!SetUtils.isNullList(actrtClassList))
		{
			for (WorkflowTemplateActClass wfTemplateActclass : actrtClassList)
			{
				workflowTemplateActclassMap.put(wfTemplateActclass.getWorkflowTemplateActClassInfo().getGuid(), wfTemplateActclass);
			}
		}
		this.actrtClassMap = workflowTemplateActclassMap;
	}

	public Map<String, WorkflowTemplateActClass> getActrtClassMap()
	{
		return actrtClassMap;
	}

	public void setActrtClassMap(Map<String, WorkflowTemplateActClass> actrtClassMap)
	{
		this.actrtClassMap = actrtClassMap;
	}

	/**
	 * @param templateActivityMap
	 *            the templateActivityMap to set
	 */
	public void setClassActMap(Map<String, WorkflowTemplateActClass> classActMap)
	{
		this.classActMap = classActMap;
	}

	public WorkflowTemplateActClass getWorkflowTemplateActClass(String className)
	{
		return this.classActMap == null ? null : this.classActMap.get(className);
	}

	public void addWorkflowTemplateActClass(String className, WorkflowTemplateActClass activityClass)
	{
		if (this.classActMap == null)
		{
			this.classActMap = new HashMap<String, WorkflowTemplateActClass>();
		}

		this.classActMap.put(className, activityClass);
	}

	/**
	 * @return the classActMap
	 */
	public Map<String, WorkflowTemplateActClass> getClassActMap()
	{
		if (this.classActMap == null)
		{
			this.classActMap = new HashMap<String, WorkflowTemplateActClass>();
		}
		return this.classActMap;
	}

	/**
	 * @return the actCompanyList
	 */
	public List<WorkflowTemplateActCompanyInfo> getActCompanyList()
	{
		return this.actCompanyList;
	}

	/**
	 * @param actCompanyList
	 *            the actCompanyList to set
	 */
	public void setActCompanyList(List<WorkflowTemplateActCompanyInfo> actCompanyList)
	{
		this.actCompanyList = actCompanyList;
	}

	public void clearForCreate()
	{
		if (this.workflowTemplateActInfo != null)
		{
			this.workflowTemplateActInfo.clearForCreate();
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

		if (finAdvnotice != null)
		{
			this.finAdvnotice.clearForCreate();
		}
		if (defAdvnotice != null)
		{
			this.defAdvnotice.clearForCreate();
		}
		if (advAdvnotice != null)
		{
			this.advAdvnotice.clearForCreate();
		}
		if (classActMap != null)
		{
			for (WorkflowTemplateActClass act : classActMap.values())
			{
				if (act != null)
				{
					act.clearForCreate();
				}
			}
		}
		if (actCompanyList != null)
		{
			for (WorkflowTemplateActCompanyInfo act : actCompanyList)
			{
				if (act != null)
				{
					act.clearForCreate();
				}
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
			;
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
		for (WorkflowTemplateActPerformerInfo info : performerList)
		{
			performerDtoMap.put(info.getGuid(), info);
		}
		this.actperformerMap.put(noticeType, performerDtoMap);
	}

	@Override
	public WorkflowTemplateAct clone()
	{
		WorkflowTemplateAct actrt = new WorkflowTemplateAct(this.workflowTemplateActInfo.clone());

		Map<String, Map<String, WorkflowTemplateActPerformerInfo>> actperformerMap_temp = new HashMap<String, Map<String, WorkflowTemplateActPerformerInfo>>();
		if (!SetUtils.isNullMap(actperformerMap))
		{
			for (Map.Entry<String, Map<String, WorkflowTemplateActPerformerInfo>> entry : actperformerMap.entrySet())
			{
				Map<String, WorkflowTemplateActPerformerInfo> typeMap = entry.getValue();
				if (!SetUtils.isNullMap(typeMap))
				{
					Map<String, WorkflowTemplateActPerformerInfo> temp = new HashMap<String, WorkflowTemplateActPerformerInfo>();
					for (Map.Entry<String, WorkflowTemplateActPerformerInfo> entry_ : typeMap.entrySet())
					{
						temp.put(entry_.getKey(), entry_.getValue().clone());
					}
					actperformerMap_temp.put(entry.getKey(), temp);
				}
			}
		}
		actrt.setActperformerMap(actperformerMap_temp);

		if (this.finAdvnotice != null)
		{
			actrt.setFinAdvnotice(this.finAdvnotice.clone());
		}

		if (this.defAdvnotice != null)
		{
			actrt.setDefAdvnotice(this.defAdvnotice.clone());
		}

		if (this.advAdvnotice != null)
		{
			actrt.setAdvAdvnotice(this.advAdvnotice.clone());
		}
		if (!SetUtils.isNullMap(this.actrtClassMap))
		{
			Map<String, WorkflowTemplateActClass> actrtClassMap_ = new HashMap<String, WorkflowTemplateActClass>();
			for (Map.Entry<String, WorkflowTemplateActClass> entry : this.actrtClassMap.entrySet())
			{
				actrtClassMap_.put(entry.getKey(), entry.getValue().clone());
			}
			actrt.setActrtClassMap(actrtClassMap_);
		}
		if (!SetUtils.isNullList(this.actCompanyList))
		{
			List<WorkflowTemplateActCompanyInfo> actCompanyList_ = new ArrayList<WorkflowTemplateActCompanyInfo>();
			for (WorkflowTemplateActCompanyInfo actCompany : this.actCompanyList)
			{
				actCompanyList_.add(actCompany.clone());
			}
			actrt.setActCompanyList(actCompanyList_);
		}

		return actrt;
	}
}
