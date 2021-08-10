package dyna.app.service.brs.wfi;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfm.WFMImpl;
import dyna.common.bean.model.wf.WorkflowActivity;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.bean.model.wf.template.WorkflowTemplateActClass;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.cache.AppServerCacheInfo;
import dyna.common.cache.CacheConstants;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.*;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;

/**
 * 工作流模板
 * 
 * @author lizw
 *
 */

public class WorkflowTemplateCacheStub extends AbstractServiceStub<WFIImpl>
{

	private static Map<String, WorkflowTemplate>			WFT_TEMPLATE_GUID_CACHE		= Collections.synchronizedMap(new HashMap<>());
	private static Map<String, String>						WFT_TEMPLATE_ID_GUID_CACHE	= Collections.synchronizedMap(new HashMap<>());

	private static Map<String, WorkflowTemplateAct>			TEMPLATE_ACT_GUID_CACHE		= Collections.synchronizedMap(new HashMap<>());

	private static Map<String, WorkflowTemplateActClass>	ACT_CLASS_GUID_CACHE		= Collections.synchronizedMap(new HashMap<>());

	private AppServerCacheInfo								cacheInfo					= null;

	/**
	 * @param context
	 * @param service
	 */
	protected WorkflowTemplateCacheStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.cacheInfo = new WorkflowTemplateCacheInfo();
	}

	// @Override
	public void loadModel()
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateInfo> templateList = sds.listFromCache(WorkflowTemplateInfo.class, null);
		if (!SetUtils.isNullList(templateList))
		{
			templateList.forEach(templateDto -> {
				this.loadWorkflowTemplate(templateDto);
			});
		}
	}

	public List<WorkflowTemplate> listAllWorkflowTemplateInfo()
	{
		List<WorkflowTemplate> templateInfoList = new ArrayList<>();
		WFT_TEMPLATE_GUID_CACHE.forEach((templateGuid, templateInfo) -> {
			templateInfoList.add(templateInfo.clone());
		});
		return templateInfoList;
	}

	protected List<WorkflowTemplate> listWorkflowTemplateInfoByWFName(String wfName)
	{
		List<WorkflowTemplate> templateInfoList = new ArrayList<>();
		WFT_TEMPLATE_GUID_CACHE.forEach((templateGuid, template) -> {
			if (template.getWorkflowTemplateInfo().getWFName().equals(wfName))
			{
				templateInfoList.add(template.clone());
			}
		});
		return templateInfoList;
	}

	public WorkflowTemplate getWorkflowTemplate(String templateGuid)
	{
		WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(templateGuid);
		return template == null ? null : template.clone();
	}

	protected WorkflowTemplate getWorkflowTemplateById(String templateId)
	{
		String templateGuid = WFT_TEMPLATE_ID_GUID_CACHE.get(templateId);
		return this.getWorkflowTemplate(templateGuid);
	}

	protected WorkflowTemplateAct getWorkflowTemplateAct(String actGuid)
	{
		return TEMPLATE_ACT_GUID_CACHE.get(actGuid).clone();
	}

	protected WorkflowTemplateActClass getWorkflowTemplateActClass(String actClassGuid)
	{
		return ACT_CLASS_GUID_CACHE.get(actClassGuid).clone();
	}

	private void loadWorkflowTemplate(WorkflowTemplateInfo templateInfo)
	{
		WorkflowTemplate template = new WorkflowTemplate(templateInfo);
		WFT_TEMPLATE_GUID_CACHE.put(templateInfo.getGuid(), template);
		WFT_TEMPLATE_ID_GUID_CACHE.put(templateInfo.getId(), templateInfo.getGuid());

		// 设置流程节点
		template.setTemplateActList(this.getWorkflowTemplateActList(templateInfo.getGuid(), templateInfo.getWFName()));

		List<WorkflowTemplateScopeBoInfo> scopeBoList = this.getScopeBOList(templateInfo.getGuid());
		template.setListScopeBO(scopeBoList);
		template.setListScopeRT(this.getScopeRTList(templateInfo.getGuid()));

		// 观察人
		List<WorkflowTemplateActPerformerInfo> observerList = this.listPerformer(templateInfo.getGuid(), null, WorkflowTemplateActPerformerInfo.NOTICETYPE_OBSERVER);
		template.setObserverList(observerList);

		List<WorkflowTemplateActPerformerInfo> useList = this.listPerformer(templateInfo.getGuid(), null, WorkflowTemplateActPerformerInfo.NOTICETYPE_ORIGINATOR);
		template.setUseList(useList);

		List<WorkflowTemplateActPerformerInfo> startUpList = this.listPerformer(templateInfo.getGuid(), null, WorkflowTemplateActPerformerInfo.NOTICETYPE_OPENNOTICE);
		template.setStartUpNotifierList(startUpList);

		List<WorkflowTemplateActPerformerInfo> completeList = this.listPerformer(templateInfo.getGuid(), null, WorkflowTemplateActPerformerInfo.NOTICETYPE_CLOSENOTICE);
		template.setCompleteNotifierList(completeList);

		// 开始流程通知设置
		WorkflowTemplateActAdvnoticeInfo startUpAdvnotice = this.getNotice(templateInfo.getGuid(), null, WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_OPEN);
		template.setStartUpAdvnotice(startUpAdvnotice);

		// 结束流程通知设置
		WorkflowTemplateActAdvnoticeInfo completeUpAdvnotice = this.getNotice(templateInfo.getGuid(), null, WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_CLOSE);
		template.setCompleteAdvnotice(completeUpAdvnotice);

	}

	private List<WorkflowTemplateAct> getWorkflowTemplateActList(String templateGuid, String wfName)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateActInfo> actrtList = sds.listFromCache(WorkflowTemplateActInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActInfo>(WorkflowTemplateActInfo.TEMPLATEGUID, templateGuid));
		List<WorkflowActivity> actrtWFList = new ArrayList<WorkflowActivity>();
		if (!SetUtils.isNullList(actrtList))
		{
			List<WorkflowActivity> activityList = null;
			try
			{
				activityList = ((WFMImpl) this.stubService.getWFM()).getActivitiyStub().listActivity(wfName);
			}
			catch (ServiceRequestException e)
			{
				// TODO Auto-generated catch block
				DynaLogger.error("WorkflowTemplateCacheStub load failed...");
				e.printStackTrace();
			}
			if (!SetUtils.isNullList(activityList))
			{
				// actrtWFList = activityList.stream().filter(act -> act.getType() != null &&
				// (WorkflowActivityType.getEnum(act.getType()) == WorkflowActivityType.MANUAL
				// || WorkflowActivityType.getEnum(act.getType()) ==
				// WorkflowActivityType.NOTIFY)).collect(Collectors.toList());
				actrtWFList = new ArrayList<>(activityList);
			}
		}

		List<WorkflowTemplateAct> actrtFinalList = new ArrayList<WorkflowTemplateAct>();
		if (!SetUtils.isNullList(actrtWFList))
		{
			actrtWFList.forEach(actrtWF -> {
				WorkflowTemplateAct actrt = this.getActrtByName(actrtList, actrtWF.getName());
				if (actrt != null)
				{
					actrtFinalList.add(actrt);
				}
				else
				{
					WorkflowTemplateAct workflowTemplateAct = new WorkflowTemplateAct();
					WorkflowTemplateActInfo workflowTemplateActDto = new WorkflowTemplateActInfo();
					workflowTemplateActDto.setTemplateGuid(templateGuid);
					workflowTemplateActDto.setActrtName(actrtWF.getName());
					workflowTemplateAct.setWorkflowTemplateActInfo(workflowTemplateActDto);
					actrtFinalList.add(workflowTemplateAct);
				}
			});
		}

		if (!SetUtils.isNullList(actrtFinalList))
		{
			actrtFinalList.forEach(workflowTemplateAct -> {

				this.loadWorkflowTemplateAct(workflowTemplateAct);

			});
		}

		return actrtFinalList;
	}

	/**
	 * 加载节点设置
	 * 
	 * @param workflowTemplateAct
	 */
	private void loadWorkflowTemplateAct(WorkflowTemplateAct workflowTemplateAct)
	{
		String workflowTemplateActGuid = workflowTemplateAct.getWorkflowTemplateActInfo().getGuid();
		// 工作流模板活动节点class设置
		workflowTemplateAct.setActrtClassList(this.loadActrtClassList(workflowTemplateActGuid));

		// 将截止日期通知人设置
		WorkflowTemplateActAdvnoticeInfo advAdvnotice = this.getNotice(null, workflowTemplateActGuid, WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTADV);
		workflowTemplateAct.setAdvAdvnotice(advAdvnotice);

		// 将截止日期通知人明细设置
		List<WorkflowTemplateActPerformerInfo> advNoticeperList = this.listPerformer(null, workflowTemplateActGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTADV);
		workflowTemplateAct.setAdvNoticeperList(advNoticeperList);

		// 超时截止日期通知人设置
		WorkflowTemplateActAdvnoticeInfo defAdvnotice = this.getNotice(null, workflowTemplateActGuid, WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF);
		workflowTemplateAct.setDefAdvnotice(defAdvnotice);

		// 超时截止日期通知人明细设置
		List<WorkflowTemplateActPerformerInfo> defNoticeperList = this.listPerformer(null, workflowTemplateActGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTDEF);
		workflowTemplateAct.setDefNoticeperList(defNoticeperList);

		// 完成通知设置
		WorkflowTemplateActAdvnoticeInfo finAdvnotice = this.getNotice(null, workflowTemplateActGuid, WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTFIN);
		workflowTemplateAct.setFinAdvnotice(finAdvnotice);

		// 完成通知人明细设置
		List<WorkflowTemplateActPerformerInfo> finNoticeperList = this.listPerformer(null, workflowTemplateActGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTFIN);
		workflowTemplateAct.setFinNoticeperList(finNoticeperList);

		// 指定执行人
		List<WorkflowTemplateActPerformerInfo> executorList = this.listPerformer(null, workflowTemplateActGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECUTOR);
		workflowTemplateAct.setExecutorList(executorList);

		// 执行人范围
		List<WorkflowTemplateActPerformerInfo> scopeExecutorList = this.listPerformer(null, workflowTemplateActGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECSCOPE);
		workflowTemplateAct.setScopeExecutorList(scopeExecutorList);

		// erp
		workflowTemplateAct.setActCompanyList(this.listWorkflowTemplateActCompanyDto(workflowTemplateActGuid));
		TEMPLATE_ACT_GUID_CACHE.put(workflowTemplateAct.getWorkflowTemplateActInfo().getGuid(), workflowTemplateAct);
	}

	protected List<WorkflowTemplateActClass> loadActrtClassList(String workflowTemplateActrtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateActClassInfo> classSetDtoList = sds.listFromCache(WorkflowTemplateActClassInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActClassInfo>(WorkflowTemplateActClassInfo.TEMPLATEACTRTGUID, workflowTemplateActrtGuid));
		List<WorkflowTemplateActClass> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(classSetDtoList))
		{
			classSetDtoList.forEach(classSetDto -> {
				WorkflowTemplateActClass classSet = new WorkflowTemplateActClass(classSetDto);
				this.loadActrtClass(classSet);
				resultList.add(classSet);

			});
		}

		return resultList;
	}

	private void loadActrtClass(WorkflowTemplateActClass classSet)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		List<WorkflowTemplateActClassUIInfo> classSetUIDtoList = sds.listFromCache(WorkflowTemplateActClassUIInfo.class, new FieldValueEqualsFilter<WorkflowTemplateActClassUIInfo>(
				WorkflowTemplateActClassRelationInfo.TEMACTBOGUID, classSet.getWorkflowTemplateActClassInfo().getGuid()));

		classSet.setActClassUIList(classSetUIDtoList);

		List<WorkflowTemplateActClassRelationInfo> classRelationDtoList = sds.listFromCache(WorkflowTemplateActClassRelationInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActClassRelationInfo>(WorkflowTemplateActClassRelationInfo.TEMACTBOGUID,
						classSet.getWorkflowTemplateActClassInfo().getGuid()));

		classSet.setActClassRelationList(classRelationDtoList);
		ACT_CLASS_GUID_CACHE.put(classSet.getWorkflowTemplateActClassInfo().getGuid(), classSet);
		// ACTCLASS_BO_GUID_CACHE.put(classSet.getWorkflowTemplateActClassInfo().getBOGuid(),
		// classSet.getWorkflowTemplateActClassInfo().getGuid());
	}

	private List<WorkflowTemplateActCompanyInfo> listWorkflowTemplateActCompanyDto(String workflowTemplateActrtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateActCompanyInfo> actCompanyDtoList = sds.listFromCache(WorkflowTemplateActCompanyInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActCompanyInfo>(WorkflowTemplateActCompanyInfo.TEMPLATEACTRTGUID, workflowTemplateActrtGuid));

		return actCompanyDtoList;
	}

	private WorkflowTemplateAct getActrtByName(List<WorkflowTemplateActInfo> actrtList, String actrtName)
	{
		if (!SetUtils.isNullList(actrtList))
		{
			Optional<WorkflowTemplateActInfo> optional = actrtList.stream().filter(actrt -> actrt.getActrtName().equals(actrtName)).findFirst();
			if (optional.isPresent())
			{
				WorkflowTemplateAct workflowTemplateAct = new WorkflowTemplateAct(optional.get());
				return workflowTemplateAct;
			}
		}
		return null;
	}

	private List<WorkflowTemplateScopeBoInfo> getScopeBOList(String workflowTemplateGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateScopeBoInfo> scopeBODtoList = sds.listFromCache(WorkflowTemplateScopeBoInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateScopeBoInfo>(WorkflowTemplateScopeBoInfo.WFTEMPLATEFK, workflowTemplateGuid));
		if (scopeBODtoList != null)
		{
			for (WorkflowTemplateScopeBoInfo info : scopeBODtoList)
			{
				BOInfo boinfo = sds.get(BOInfo.class, info.getBOGuid());
				if (boinfo != null)
				{
					info.setBOName(boinfo.getBOName());
				}
			}
		}
		return scopeBODtoList;
	}

	private WorkflowTemplateActAdvnoticeInfo getNotice(String templateGuid, String templateActrtGuid, String type)
	{
		List<WorkflowTemplateActAdvnoticeInfo> list = this.listNotice(templateGuid, templateActrtGuid, type);
		return SetUtils.isNullList(list) ? null : list.get(0);
	}

	private List<WorkflowTemplateActAdvnoticeInfo> listNotice(String templateGuid, String templateActrtGuid, String type)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		UpperKeyMap filterMap = new UpperKeyMap();
		if (StringUtils.isGuid(templateGuid))
		{
			filterMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, templateGuid);
		}
		if (StringUtils.isGuid(templateActrtGuid))
		{
			filterMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEACTRTGUID, templateActrtGuid);
		}
		filterMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, type);
		List<WorkflowTemplateActAdvnoticeInfo> dataDtoList = sds.listFromCache(WorkflowTemplateActAdvnoticeInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActAdvnoticeInfo>(filterMap));
		return dataDtoList;
	}

	private List<WorkflowTemplateScopeRTInfo> getScopeRTList(String workflowTemplateGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateScopeRTInfo> scopeRTDtoList = sds.listFromCache(WorkflowTemplateScopeRTInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateScopeRTInfo>(WorkflowTemplateScopeRTInfo.WFTEMPLATEFK, workflowTemplateGuid));

		return scopeRTDtoList;
	}

	private List<WorkflowTemplateActPerformerInfo> listPerformer(String templateGuid, String templateActrtGuid, String type)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		UpperKeyMap filterMap = new UpperKeyMap();
		if (StringUtils.isGuid(templateGuid))
		{
			filterMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, templateGuid);
		}
		if (StringUtils.isGuid(templateActrtGuid))
		{
			filterMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEACTRTGUID, templateActrtGuid);
		}
		filterMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, type);
		List<WorkflowTemplateActPerformerInfo> dataDtoList = sds.listFromCache(WorkflowTemplateActPerformerInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(filterMap));

		return dataDtoList;
	}

	class WorkflowTemplateCacheInfo extends AppServerCacheInfo
	{
		private static final long serialVersionUID = -4650755908711482731L;

		@Override
		protected void addToCache(Object data) throws ServiceRequestException
		{
			if (data instanceof WorkflowTemplateActPerformerInfo)
			{
				WorkflowTemplateActPerformerInfo dto = (WorkflowTemplateActPerformerInfo) data;
				this.refreshPerformer(dto, CacheConstants.CHANGE_TYPE.INSERT);

			}
			else if (data instanceof WorkflowTemplateActAdvnoticeInfo)
			{
				WorkflowTemplateActAdvnoticeInfo dto = (WorkflowTemplateActAdvnoticeInfo) data;
				this.refreshAdvnotice(dto, CacheConstants.CHANGE_TYPE.INSERT);
			}
			else if (data instanceof WorkflowTemplateScopeBoInfo)
			{
				WorkflowTemplateScopeBoInfo dto = (WorkflowTemplateScopeBoInfo) data;
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(dto.getWFTemplatefk());
				template.getScopeBOMap().put(dto.getGuid(), dto);
				SystemDataService sds = DataServer.getSystemDataService();
				if (!StringUtils.isNullString(dto.getBOGuid()))
				{
					BOInfo boinfo = sds.get(BOInfo.class, dto.getBOGuid());
					if (boinfo != null)
					{
						dto.setBOName(boinfo.getBOName());
					}
				}
			}
			else if (data instanceof WorkflowTemplateScopeRTInfo)
			{
				WorkflowTemplateScopeRTInfo dto = (WorkflowTemplateScopeRTInfo) data;
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(dto.getWFTemplatefk());
				template.getScopeRTMap().put(dto.getGuid(), dto);
			}
			else if (data instanceof WorkflowTemplateInfo)
			{
				WorkflowTemplateInfo workflowTemplateDto = (WorkflowTemplateInfo) data;
				WorkflowTemplateCacheStub.this.loadWorkflowTemplate(workflowTemplateDto);
			}
			else if (data instanceof WorkflowTemplateActInfo)
			{
				WorkflowTemplateActInfo workflowTemplateActDto = (WorkflowTemplateActInfo) data;
				WorkflowTemplateAct workflowTemplateAct = new WorkflowTemplateAct(workflowTemplateActDto);
				WorkflowTemplateCacheStub.this.loadWorkflowTemplateAct(workflowTemplateAct);
				WorkflowTemplate wfTemplate = WFT_TEMPLATE_GUID_CACHE.get(workflowTemplateActDto.getTemplateGuid());
				wfTemplate.getTemplateActMap().put(workflowTemplateActDto.getGuid(), workflowTemplateAct);
			}
			else if (data instanceof WorkflowTemplateActClassInfo)
			{
				WorkflowTemplateActClassInfo workflowTemplateActClassDto = (WorkflowTemplateActClassInfo) data;
				WorkflowTemplateActClass workflowTemplateActClass = new WorkflowTemplateActClass(workflowTemplateActClassDto);
				WorkflowTemplateCacheStub.this.loadActrtClass(workflowTemplateActClass);
				WorkflowTemplateAct templateAct = TEMPLATE_ACT_GUID_CACHE.get(workflowTemplateActClassDto.getTemplateActrtGuid());
				templateAct.getActrtClassMap().put(workflowTemplateActClassDto.getGuid(), workflowTemplateActClass);
				templateAct.getClassActMap().put(workflowTemplateActClassDto.getBOGuid(), workflowTemplateActClass);
			}
			else if (data instanceof WorkflowTemplateActClassUIInfo)
			{
				WorkflowTemplateActClassUIInfo workflowTemplateActClassUIDto = (WorkflowTemplateActClassUIInfo) data;
				WorkflowTemplateActClass actClass = ACT_CLASS_GUID_CACHE.get(workflowTemplateActClassUIDto.getTemActBOGuid());
				actClass.getActClassUIMap().put(workflowTemplateActClassUIDto.getGuid(), workflowTemplateActClassUIDto);
			}
			else if (data instanceof WorkflowTemplateActClassRelationInfo)
			{
				WorkflowTemplateActClassRelationInfo workflowTemplateActClassRelationDto = (WorkflowTemplateActClassRelationInfo) data;
				WorkflowTemplateActClass actClass = ACT_CLASS_GUID_CACHE.get(workflowTemplateActClassRelationDto.getTemActBOGuid());
				actClass.getActClassRelationMap().put(workflowTemplateActClassRelationDto.getGuid(), workflowTemplateActClassRelationDto);
			}
		}

		@Override
		protected void removeFromCache(Object data) throws ServiceRequestException
		{
			if (data instanceof WorkflowTemplateActPerformerInfo)
			{
				WorkflowTemplateActPerformerInfo dto = (WorkflowTemplateActPerformerInfo) data;
				this.refreshPerformer(dto, CacheConstants.CHANGE_TYPE.DELETE);
			}
			else if (data instanceof WorkflowTemplateActAdvnoticeInfo)
			{
				WorkflowTemplateActAdvnoticeInfo dto = (WorkflowTemplateActAdvnoticeInfo) data;
				this.refreshAdvnotice(dto, CacheConstants.CHANGE_TYPE.DELETE);
			}
			else if (data instanceof WorkflowTemplateScopeBoInfo)
			{
				WorkflowTemplateScopeBoInfo dto = (WorkflowTemplateScopeBoInfo) data;
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(dto.getWFTemplatefk());
				template.getScopeBOMap().remove(dto.getGuid());
			}
			else if (data instanceof WorkflowTemplateScopeRTInfo)
			{
				WorkflowTemplateScopeRTInfo dto = (WorkflowTemplateScopeRTInfo) data;
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(dto.getWFTemplatefk());
				template.getScopeRTMap().remove(dto.getGuid());
			}
			else if (data instanceof WorkflowTemplateInfo)
			{
				WorkflowTemplateInfo workflowTemplateDto = (WorkflowTemplateInfo) data;
				WFT_TEMPLATE_GUID_CACHE.remove(workflowTemplateDto.getGuid());
				WFT_TEMPLATE_ID_GUID_CACHE.remove(workflowTemplateDto.getName());
			}
			else if (data instanceof WorkflowTemplateActInfo)
			{
				WorkflowTemplateActInfo workflowTemplateActDto = (WorkflowTemplateActInfo) data;
				TEMPLATE_ACT_GUID_CACHE.remove(workflowTemplateActDto.getGuid());
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(workflowTemplateActDto.getTemplateGuid());
				template.getTemplateActMap().remove(workflowTemplateActDto.getGuid());
			}
			else if (data instanceof WorkflowTemplateActClassInfo)
			{
				WorkflowTemplateActClassInfo workflowTemplateActClassDto = (WorkflowTemplateActClassInfo) data;
				ACT_CLASS_GUID_CACHE.remove(workflowTemplateActClassDto.getGuid());
				WorkflowTemplateAct templateAct = TEMPLATE_ACT_GUID_CACHE.get(workflowTemplateActClassDto.getTemplateActrtGuid());
				templateAct.getActrtClassMap().remove(workflowTemplateActClassDto.getGuid());
				templateAct.getClassActMap().remove(workflowTemplateActClassDto.getBOGuid());
			}
			else if (data instanceof WorkflowTemplateActClassUIInfo)
			{
				WorkflowTemplateActClassUIInfo workflowTemplateActClassUIDto = (WorkflowTemplateActClassUIInfo) data;
				WorkflowTemplateActClass actClass = ACT_CLASS_GUID_CACHE.get(workflowTemplateActClassUIDto.getTemActBOGuid());
				actClass.getActClassUIMap().remove(workflowTemplateActClassUIDto.getGuid());
			}
			else if (data instanceof WorkflowTemplateActClassRelationInfo)
			{
				WorkflowTemplateActClassRelationInfo workflowTemplateActClassRelationDto = (WorkflowTemplateActClassRelationInfo) data;
				WorkflowTemplateActClass actClass = ACT_CLASS_GUID_CACHE.get(workflowTemplateActClassRelationDto.getTemActBOGuid());
				actClass.getActClassRelationMap().remove(workflowTemplateActClassRelationDto.getGuid());
			}
		}

		@Override
		protected void updateToCache(Object data) throws ServiceRequestException
		{
			if (data instanceof WorkflowTemplateActPerformerInfo)
			{
				WorkflowTemplateActPerformerInfo dto = (WorkflowTemplateActPerformerInfo) data;
				this.refreshPerformer(dto, CacheConstants.CHANGE_TYPE.UPDATE);
			}
			else if (data instanceof WorkflowTemplateActAdvnoticeInfo)
			{
				WorkflowTemplateActAdvnoticeInfo dto = (WorkflowTemplateActAdvnoticeInfo) data;
				this.refreshAdvnotice(dto, CacheConstants.CHANGE_TYPE.UPDATE);
			}
			else if (data instanceof WorkflowTemplateScopeBoInfo)
			{
				WorkflowTemplateScopeBoInfo dto = (WorkflowTemplateScopeBoInfo) data;
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(dto.getWFTemplatefk());
				template.getScopeBOMap().get(dto.getGuid()).putAll(dto);
				SystemDataService sds = DataServer.getSystemDataService();
				if (!StringUtils.isNullString(dto.getBOGuid()))
				{
					BOInfo boinfo = sds.get(BOInfo.class, dto.getBOGuid());
					if (boinfo != null)
					{
						dto.setBOName(boinfo.getBOName());
					}
				}
			}
			else if (data instanceof WorkflowTemplateScopeRTInfo)
			{
				WorkflowTemplateScopeRTInfo dto = (WorkflowTemplateScopeRTInfo) data;
				WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(dto.getWFTemplatefk());
				template.getScopeRTMap().get(dto.getGuid()).putAll(dto);
			}
			else if (data instanceof WorkflowTemplateInfo)
			{
				WorkflowTemplateInfo workflowTemplateDto = (WorkflowTemplateInfo) data;
				WFT_TEMPLATE_GUID_CACHE.remove(workflowTemplateDto.getGuid());
				WFT_TEMPLATE_ID_GUID_CACHE.remove(workflowTemplateDto.getName());
				WorkflowTemplateCacheStub.this.loadWorkflowTemplate(workflowTemplateDto);
			}
			else if (data instanceof WorkflowTemplateActInfo)
			{
				WorkflowTemplateActInfo workflowTemplateActDto = (WorkflowTemplateActInfo) data;
				WorkflowTemplateAct workflowTemplateAct = new WorkflowTemplateAct(workflowTemplateActDto);
				WorkflowTemplateCacheStub.this.loadWorkflowTemplateAct(workflowTemplateAct);
				WorkflowTemplate wfTemplate = WFT_TEMPLATE_GUID_CACHE.get(workflowTemplateActDto.getTemplateGuid());
				wfTemplate.getTemplateActMap().put(workflowTemplateActDto.getGuid(), workflowTemplateAct);
			}
			else if (data instanceof WorkflowTemplateActClassInfo)
			{
				WorkflowTemplateActClassInfo workflowTemplateActClassDto = (WorkflowTemplateActClassInfo) data;
				WorkflowTemplateActClass workflowTemplateActClass = new WorkflowTemplateActClass(workflowTemplateActClassDto);
				WorkflowTemplateCacheStub.this.loadActrtClass(workflowTemplateActClass);
				WorkflowTemplateAct templateAct = TEMPLATE_ACT_GUID_CACHE.get(workflowTemplateActClassDto.getTemplateActrtGuid());
				templateAct.getActrtClassMap().put(workflowTemplateActClassDto.getGuid(), workflowTemplateActClass);
				templateAct.getClassActMap().put(workflowTemplateActClassDto.getBOGuid(), workflowTemplateActClass);
			}
			else if (data instanceof WorkflowTemplateActClassUIInfo)
			{
				WorkflowTemplateActClassUIInfo workflowTemplateActClassUIDto = (WorkflowTemplateActClassUIInfo) data;
				WorkflowTemplateActClass actClass = ACT_CLASS_GUID_CACHE.get(workflowTemplateActClassUIDto.getTemActBOGuid());
				actClass.getActClassUIMap().get(workflowTemplateActClassUIDto.getGuid()).putAll(workflowTemplateActClassUIDto);
			}
			else if (data instanceof WorkflowTemplateActClassRelationInfo)
			{
				WorkflowTemplateActClassRelationInfo workflowTemplateActClassRelationDto = (WorkflowTemplateActClassRelationInfo) data;
				WorkflowTemplateActClass actClass = ACT_CLASS_GUID_CACHE.get(workflowTemplateActClassRelationDto.getTemActBOGuid());
				actClass.getActClassRelationMap().get(workflowTemplateActClassRelationDto.getGuid()).putAll(workflowTemplateActClassRelationDto);
			}
		}

		private void refreshPerformer(WorkflowTemplateActPerformerInfo workflowTemplateActPerformerDto, String operation)
		{
			WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(workflowTemplateActPerformerDto.getTemplateGuid());
			String type = workflowTemplateActPerformerDto.getNoticeType();

			if (template != null)
			{
				Map<String, WorkflowTemplateActPerformerInfo> templateTypeMap = template.getActperformerMap().get(type);
				if (templateTypeMap == null)
				{
					templateTypeMap = new HashMap<>();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT))
				{
					templateTypeMap.put(workflowTemplateActPerformerDto.getGuid(), workflowTemplateActPerformerDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					templateTypeMap.remove(workflowTemplateActPerformerDto.getGuid());
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					WorkflowTemplateActPerformerInfo exists = templateTypeMap.get(workflowTemplateActPerformerDto.getGuid());
					exists.putAll(workflowTemplateActPerformerDto);
				}

				template.getActperformerMap().put(type, templateTypeMap);
			}
			WorkflowTemplateAct act = TEMPLATE_ACT_GUID_CACHE.get(workflowTemplateActPerformerDto.getTemplateActrtGuid());

			if (act != null)
			{
				Map<String, WorkflowTemplateActPerformerInfo> actTypeMap = act.getActperformerMap().get(type);
				if (actTypeMap == null)
				{
					actTypeMap = new HashMap<>();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT))
				{
					actTypeMap.put(workflowTemplateActPerformerDto.getGuid(), workflowTemplateActPerformerDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					actTypeMap.remove(workflowTemplateActPerformerDto.getGuid());
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					WorkflowTemplateActPerformerInfo exists = actTypeMap.get(workflowTemplateActPerformerDto.getGuid());
					exists.putAll(workflowTemplateActPerformerDto);
				}
				act.getActperformerMap().put(type, actTypeMap);
			}

		}

		private void refreshAdvnotice(WorkflowTemplateActAdvnoticeInfo workflowTemplateActAdvnoticeDto, String operation)
		{
			WorkflowTemplate template = WFT_TEMPLATE_GUID_CACHE.get(workflowTemplateActAdvnoticeDto.getTemplateGuid());
			WorkflowTemplateAct act = TEMPLATE_ACT_GUID_CACHE.get(workflowTemplateActAdvnoticeDto.getTemplateActrtGuid());

			String type = workflowTemplateActAdvnoticeDto.getNoticeType();

			switch (type)
			{
			case WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_OPEN:
				WorkflowTemplateActAdvnoticeInfo existsStart = template.getStartUpAdvnotice();
				if (existsStart == null)
				{
					existsStart = new WorkflowTemplateActAdvnoticeInfo();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT) || operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					existsStart.putAll(workflowTemplateActAdvnoticeDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					existsStart = null;
				}
				template.setStartUpAdvnotice(existsStart);
				break;
			case WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_CLOSE:
				WorkflowTemplateActAdvnoticeInfo existsComplate = template.getCompleteAdvnotice();
				if (existsComplate == null)
				{
					existsComplate = new WorkflowTemplateActAdvnoticeInfo();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT) || operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					existsComplate.putAll(workflowTemplateActAdvnoticeDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					existsComplate = null;
				}
				template.setCompleteAdvnotice(existsComplate);
				break;
			case WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTADV:
				WorkflowTemplateActAdvnoticeInfo existsAdv = template.getCompleteAdvnotice();
				if (existsAdv == null)
				{
					existsAdv = new WorkflowTemplateActAdvnoticeInfo();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT) || operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					existsAdv.putAll(workflowTemplateActAdvnoticeDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					existsAdv = null;
				}
				act.setAdvAdvnotice(existsAdv);
				break;
			case WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF:
				WorkflowTemplateActAdvnoticeInfo existsDef = template.getCompleteAdvnotice();
				if (existsDef == null)
				{
					existsDef = new WorkflowTemplateActAdvnoticeInfo();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT) || operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					existsDef.putAll(workflowTemplateActAdvnoticeDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					existsDef = null;
				}
				act.setDefAdvnotice(existsDef);
				break;
			case WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTFIN:
				WorkflowTemplateActAdvnoticeInfo existsFin = template.getCompleteAdvnotice();
				if (existsFin == null)
				{
					existsFin = new WorkflowTemplateActAdvnoticeInfo();
				}
				if (operation.equals(CacheConstants.CHANGE_TYPE.INSERT) || operation.equals(CacheConstants.CHANGE_TYPE.UPDATE))
				{
					existsFin.putAll(workflowTemplateActAdvnoticeDto);
				}
				else if (operation.equals(CacheConstants.CHANGE_TYPE.DELETE))
				{
					existsFin = null;
				}
				act.setFinAdvnotice(existsFin);
				break;
			default:
				break;
			}
		}

	}

	public AppServerCacheInfo getWorkflowTemplateCacheInfo()
	{
		return this.cacheInfo;
	}
}
