/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TemplateStub
 * WangLHB Jan 6, 2012
 */
package dyna.app.service.brs.wfi;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.bean.model.wf.template.WorkflowTemplateActClass;
import dyna.common.bean.model.wf.template.WorkflowTemplateVo;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.cache.CacheConstants;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.template.wft.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流模板
 * 
 * @author WangLHB
 * 
 */
@Component
public class WorkflowTemplateStub extends AbstractServiceStub<WFIImpl>
{

	protected WorkflowTemplateVo saveWorkflowTemplate(WorkflowTemplateVo workflowTemplateVo) throws ServiceRequestException
	{
		if (workflowTemplateVo == null || workflowTemplateVo.getTemplate() == null)
		{
			return null;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		UpperKeyMap filter = new UpperKeyMap();
		String operationType = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			String templateGuid = null;
			String operatorGuid = this.stubService.getOperatorGuid();

			// 更新模板信息
			if (StringUtils.isNullString(workflowTemplateVo.getTemplate().getWorkflowTemplateInfo().getGuid()))
			{
				workflowTemplateVo.getTemplate().getWorkflowTemplateInfo().setCreateUserGuid(operatorGuid);
			}
			workflowTemplateVo.getTemplate().getWorkflowTemplateInfo().setUpdateUserGuid(operatorGuid);

			String templateInfoResult = sds.save(workflowTemplateVo.getTemplate().getWorkflowTemplateInfo());

			if (StringUtils.isGuid(templateInfoResult))
			{
				operationType = CacheConstants.CHANGE_TYPE.INSERT;
				templateGuid = templateInfoResult;
			}
			else
			{
				operationType = CacheConstants.CHANGE_TYPE.UPDATE;
				templateGuid = workflowTemplateVo.getTemplate().getWorkflowTemplateInfo().getGuid();
			}
			if (!SetUtils.isNullList(workflowTemplateVo.getTemplate().getListScopeBO()))
			{
				for (WorkflowTemplateScopeBoInfo scopeBO : workflowTemplateVo.getTemplate().getListScopeBO())
				{
					if (!StringUtils.isNullString(scopeBO.getBOGuid()))
					{
						BOInfo boinfo = sds.get(BOInfo.class, scopeBO.getBOGuid());
						if (boinfo != null)
						{
							scopeBO.setBOName(boinfo.getBOName());
						}
					}
				}
			}

			// 刷新模板缓存信息
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItem(sds.getObjectDirectly(WorkflowTemplateInfo.class, templateGuid, null),
					operationType);

			this.checkBOScopeAndRelationTempalte(workflowTemplateVo.getTemplate());

			// 观察者
			this.saveActPerformers(workflowTemplateVo.getTemplate().getObserverList(), null, templateGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_OBSERVER, operatorGuid,
					true);

			// 使用者
			this.saveActPerformers(workflowTemplateVo.getTemplate().getUseList(), null, templateGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_ORIGINATOR, operatorGuid, true);

			// 启动节点设置
			WorkflowTemplateActAdvnoticeInfo startUpAdvnotice = workflowTemplateVo.getTemplate().getStartUpAdvnotice();
			if (startUpAdvnotice != null)
			{
				startUpAdvnotice.setTemplateGuid(templateGuid);
				if (StringUtils.isNullString(startUpAdvnotice.getGuid()))
				{
					startUpAdvnotice.setCreateUserGuid(operatorGuid);
				}
				startUpAdvnotice.setUpdateUserGuid(operatorGuid);
				startUpAdvnotice.setNoticeType(WorkflowTemplateActPerformerInfo.NOTICETYPE_OPENNOTICE);

				String returnResult = sds.save(startUpAdvnotice);
				operationType = StringUtils.isGuid(returnResult) ? CacheConstants.CHANGE_TYPE.INSERT : CacheConstants.CHANGE_TYPE.UPDATE;
				this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
						.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateActAdvnoticeInfo.class, startUpAdvnotice.getGuid(), null), operationType);
			}

			// 启动通知人
			this.saveActPerformers(workflowTemplateVo.getTemplate().getStartUpNotifierList(), null, templateGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_OPENNOTICE,
					operatorGuid, true);

			// 结束节点设置
			WorkflowTemplateActAdvnoticeInfo completeAdvnotice = workflowTemplateVo.getTemplate().getCompleteAdvnotice();
			if (completeAdvnotice != null)
			{
				completeAdvnotice.setTemplateGuid(templateGuid);
				if (StringUtils.isNullString(completeAdvnotice.getGuid()))
				{
					completeAdvnotice.setCreateUserGuid(operatorGuid);
				}
				completeAdvnotice.setUpdateUserGuid(operatorGuid);
				completeAdvnotice.setNoticeType(WorkflowTemplateActPerformerInfo.NOTICETYPE_CLOSENOTICE);

				String returnResult = sds.save(completeAdvnotice);
				operationType = StringUtils.isGuid(returnResult) ? CacheConstants.CHANGE_TYPE.INSERT : CacheConstants.CHANGE_TYPE.UPDATE;
				this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
						.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateActAdvnoticeInfo.class, completeAdvnotice.getGuid(), null), operationType);
			}
			this.saveActPerformers(workflowTemplateVo.getTemplate().getCompleteNotifierList(), null, templateGuid, WorkflowTemplateActPerformerInfo.NOTICETYPE_CLOSENOTICE,
					operatorGuid, true);

			// 业务范围
			filter.clear();
			filter.put(WorkflowTemplateScopeBoInfo.WFTEMPLATEFK, templateGuid);
			List<WorkflowTemplateScopeBoInfo> needDelScopeBOList = sds.listFromCache(WorkflowTemplateScopeBoInfo.class, new FieldValueEqualsFilter<>(filter));
			sds.deleteFromCache(WorkflowTemplateScopeBoInfo.class, new FieldValueEqualsFilter<>(filter));
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItemList(needDelScopeBOList, CacheConstants.CHANGE_TYPE.DELETE);
			if (!SetUtils.isNullList(workflowTemplateVo.getTemplate().getListScopeBO()))
			{
				for (WorkflowTemplateScopeBoInfo scopeBO : workflowTemplateVo.getTemplate().getListScopeBO())
				{
					if (StringUtils.isNullString(scopeBO.getGuid()))
					{
						scopeBO.setCreateUserGuid(operatorGuid);
					}
					scopeBO.setUpdateUserGuid(operatorGuid);

					scopeBO.setWFTemplatefk(templateGuid);
					String returnResult = sds.save(scopeBO);
					operationType = StringUtils.isGuid(returnResult) ? CacheConstants.CHANGE_TYPE.INSERT : CacheConstants.CHANGE_TYPE.UPDATE;
					this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateScopeBoInfo.class, scopeBO.getGuid(), null), operationType);
				}
			}

			// 关系模板
			filter.clear();
			filter.put(WorkflowTemplateScopeRTInfo.WFTEMPLATEFK, templateGuid);
			List<WorkflowTemplateScopeRTInfo> needDelScopeRTList = sds.listFromCache(WorkflowTemplateScopeRTInfo.class, new FieldValueEqualsFilter<>(filter));
			sds.deleteFromCache(WorkflowTemplateScopeRTInfo.class, new FieldValueEqualsFilter<>(filter));
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItemList(needDelScopeRTList, CacheConstants.CHANGE_TYPE.DELETE);

			if (!SetUtils.isNullList(workflowTemplateVo.getTemplate().getListScopeRT()))
			{
				for (WorkflowTemplateScopeRTInfo scopeRT : workflowTemplateVo.getTemplate().getListScopeRT())
				{

					scopeRT.setCreateUserGuid(operatorGuid);
					scopeRT.setUpdateUserGuid(operatorGuid);
					scopeRT.setGuid(null);
					scopeRT.setWFTemplatefk(templateGuid);
					scopeRT.put("WorkflowTemplateScopeRTDto", DateFormat.formatYMDHMS(new Date()));
					sds.save(scopeRT);
					this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateScopeRTInfo.class, scopeRT.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}

			Map<String, WorkflowTemplateAct> templateActivityMap = workflowTemplateVo.getTemplateActivityMap();
			if (templateActivityMap != null)
			{
				Collection<WorkflowTemplateAct> workflowTemplateActs = templateActivityMap.values();
				for (WorkflowTemplateAct act : workflowTemplateActs)
				{
					if (act == null)
					{
						continue;
					}

					// 判断是执行人数是否超过最大执行人数
					String maximumExecutors = act.getWorkflowTemplateActInfo().getMaximumExecutors();
					List<WorkflowTemplateActPerformerInfo> executorList = act.getExecutorList();
					List<User> allUserList = new ArrayList<User>();
					if (executorList != null)
					{
						for (WorkflowTemplateActPerformerInfo performer : executorList)
						{
							List<User> listUserInRoleGroup = this.stubService.getPerformerStub().listUserInRoleGroup(performer.getPerfType(), performer.getPerfGuid());

							if (listUserInRoleGroup != null)
							{
								allUserList.addAll(listUserInRoleGroup);
							}

						}
					}

					if (!"0".equals(maximumExecutors) && allUserList.size() > Integer.valueOf(maximumExecutors))
					{
						throw new ServiceRequestException("ID_APP_WF_PERFORMER_MAXIMUM_OVER", "Ecutors Maximumex Over", null, act.getWorkflowTemplateActInfo().getActrtName());
					}

					act.getWorkflowTemplateActInfo().setTemplateGuid(templateGuid);
					String templateActResult = sds.save(act.getWorkflowTemplateActInfo());
					String templateActrtGuid = null;
					if (StringUtils.isGuid(templateActResult))
					{
						templateActrtGuid = templateActResult;
						operationType = CacheConstants.CHANGE_TYPE.INSERT;
					}
					else
					{
						templateActrtGuid = act.getWorkflowTemplateActInfo().getGuid();
						operationType = CacheConstants.CHANGE_TYPE.UPDATE;
					}
					this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateActInfo.class, templateActrtGuid, null), operationType);

					if (act.getAdvAdvnotice() != null)
					{
						act.getAdvAdvnotice().setTemplateActrtGuid(templateActrtGuid);
						if (StringUtils.isNullString(act.getAdvAdvnotice().getGuid()))
						{
							act.getAdvAdvnotice().setCreateUserGuid(operatorGuid);
						}
						act.getAdvAdvnotice().setUpdateUserGuid(operatorGuid);
						act.getAdvAdvnotice().setNoticeType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTADV);
					}
					this.saveActPerformers(act.getAdvNoticeperList(), templateActrtGuid, null, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTADV, operatorGuid, false);

					if (act.getDefAdvnotice() != null)
					{
						act.getDefAdvnotice().setTemplateActrtGuid(templateActrtGuid);

						if (StringUtils.isNullString(act.getDefAdvnotice().getGuid()))
						{
							act.getDefAdvnotice().setCreateUserGuid(operatorGuid);
						}
						act.getDefAdvnotice().setUpdateUserGuid(operatorGuid);
						act.getDefAdvnotice().setNoticeType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTDEF);
					}
					this.saveActPerformers(act.getDefNoticeperList(), templateActrtGuid, null, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTDEF, operatorGuid, false);

					if (act.getFinAdvnotice() != null)
					{
						act.getFinAdvnotice().setTemplateActrtGuid(templateActrtGuid);
						if (StringUtils.isNullString(act.getFinAdvnotice().getGuid()))
						{
							act.getFinAdvnotice().setCreateUserGuid(operatorGuid);
						}
						act.getFinAdvnotice().setUpdateUserGuid(operatorGuid);
						act.getFinAdvnotice().setNoticeType(WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTFIN);
					}

					filter.clear();
					filter.put(WorkflowTemplateActCompanyInfo.TEMPLATEACTRTGUID, templateActrtGuid);

					List<WorkflowTemplateActCompanyInfo> needDelActCompanyList = sds.listFromCache(WorkflowTemplateActCompanyInfo.class, new FieldValueEqualsFilter<>(filter));
					sds.deleteFromCache(WorkflowTemplateActCompanyInfo.class, new FieldValueEqualsFilter<>(filter));
					this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItemList(needDelActCompanyList, CacheConstants.CHANGE_TYPE.DELETE);

					if (!SetUtils.isNullList(act.getActCompanyList()))
					{
						for (WorkflowTemplateActCompanyInfo company : act.getActCompanyList())
						{
							if (company == null)
							{
								continue;
							}

							company.setCreateUserGuid(operatorGuid);
							company.setUpdateUserGuid(operatorGuid);
							company.setGuid(null);
							company.setTemplateActrtGuid(templateActrtGuid);
							sds.save(company);
							this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
									.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateActCompanyInfo.class, company.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
						}
					}

					this.saveActPerformers(act.getFinNoticeperList(), templateActrtGuid, null, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTFIN, operatorGuid, false);

					this.saveActPerformers(act.getExecutorList(), templateActrtGuid, null, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECUTOR, operatorGuid, false);

					this.saveActPerformers(act.getScopeExecutorList(), templateActrtGuid, null, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECSCOPE, operatorGuid, false);

					Map<String, WorkflowTemplateActClass> classActMap = act.getClassActMap();
					if (classActMap != null)
					{
						Collection<WorkflowTemplateActClass> actClasses = classActMap.values();
						for (WorkflowTemplateActClass actClass : actClasses)
						{
							if (actClass == null)
							{
								continue;
							}

							if (!StringUtils.isNullString(actClass.getWorkflowTemplateActClassInfo().getGuid()))
							{
								filter.clear();
								filter.put(WorkflowTemplateActClassRelationInfo.TEMACTBOGUID, actClass.getWorkflowTemplateActClassInfo().getGuid());

								List<WorkflowTemplateActClassRelationInfo> needDelActClassRelationList = sds.listFromCache(WorkflowTemplateActClassRelationInfo.class,
										new FieldValueEqualsFilter<>(filter));
								sds.deleteFromCache(WorkflowTemplateActClassRelationInfo.class, new FieldValueEqualsFilter<>(filter));
								this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItemList(needDelActClassRelationList,
										CacheConstants.CHANGE_TYPE.DELETE);

								filter.clear();
								filter.put(WorkflowTemplateActClassUIInfo.TEMACTBOGUID, actClass.getWorkflowTemplateActClassInfo().getGuid());
								List<WorkflowTemplateActClassUIInfo> needDelActClassUIList = sds.listFromCache(WorkflowTemplateActClassUIInfo.class,
										new FieldValueEqualsFilter<>(filter));
								sds.deleteFromCache(WorkflowTemplateActClassUIInfo.class, new FieldValueEqualsFilter<>(filter));
								this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItemList(needDelActClassUIList,
										CacheConstants.CHANGE_TYPE.DELETE);
							}

							actClass.getWorkflowTemplateActClassInfo().setTemplateActrtGuid(templateActrtGuid);
							String templateActClsssResult = sds.save(actClass.getWorkflowTemplateActClassInfo());

							String templateActrtClassGuid = null;
							if (StringUtils.isGuid(templateActClsssResult))
							{
								templateActrtClassGuid = templateActClsssResult;
								operationType = CacheConstants.CHANGE_TYPE.INSERT;
							}
							else
							{
								operationType = CacheConstants.CHANGE_TYPE.UPDATE;
								templateActrtClassGuid = actClass.getWorkflowTemplateActClassInfo().getGuid();
							}

							this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
									.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateActClassInfo.class, templateActrtClassGuid, null), operationType);

							List<WorkflowTemplateActClassRelationInfo> listActClassRelation = actClass.listActClassRelationByType(null);

							if (listActClassRelation != null)
							{
								for (WorkflowTemplateActClassRelationInfo actClassRelation : listActClassRelation)
								{
									actClassRelation.setCreateUserGuid(operatorGuid);
									actClassRelation.setUpdateUserGuid(operatorGuid);
									actClassRelation.setGuid(null);
									actClassRelation.setTemActBOGuid(templateActrtClassGuid);
									sds.save(actClassRelation);
									this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItem(
											sds.getObjectDirectly(WorkflowTemplateActClassRelationInfo.class, actClassRelation.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

								}
							}

							if (actClass.getActClassUIList() != null)
							{
								for (WorkflowTemplateActClassUIInfo classUI : actClass.getActClassUIList())
								{
									classUI.setCreateUserGuid(operatorGuid);
									classUI.setUpdateUserGuid(operatorGuid);
									classUI.setGuid(null);
									classUI.setTemActBOGuid(templateActrtClassGuid);
									sds.save(classUI);
									this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItem(
											sds.getObjectDirectly(WorkflowTemplateActClassUIInfo.class, classUI.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
								}
							}

							if (!StringUtils.isNullString(actClass.getWorkflowTemplateActClassInfo().getGuid()) && SetUtils.isNullList(actClass.getActClassUIList())
									&& SetUtils.isNullList(listActClassRelation) && !actClass.getWorkflowTemplateActClassInfo().hasPropertyModify())
							{
								WorkflowTemplateActClassInfo needDelActClassInfo = sds.get(WorkflowTemplateActClassInfo.class,
										actClass.getWorkflowTemplateActClassInfo().getGuid());

								sds.delete(WorkflowTemplateActClassInfo.class, actClass.getWorkflowTemplateActClassInfo().getGuid());
								this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItem(needDelActClassInfo, CacheConstants.CHANGE_TYPE.DELETE);
							}
						}
					}
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().executeToDoItem();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().rollBack();
			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_UNIQUE)
			{
				e.setDataExceptionEnum(DataExceptionEnum.DS_UNIQUE_ID);
			}

			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().rollBack();
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}

		return this.getWorkflowTemplateDetailById(workflowTemplateVo.getTemplate().getWorkflowTemplateInfo().getId());
	}

	protected void obsoleteWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		WorkflowTemplate workflowTemplate = this.getWorkflowTemplate(workflowTemplateGuid, true);
		try
		{
			if (workflowTemplate != null)
			{
//				DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
				workflowTemplate.getWorkflowTemplateInfo().setValid(false);
				sds.save(workflowTemplate.getWorkflowTemplateInfo());
				this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItem(
						sds.getObjectDirectly(WorkflowTemplateInfo.class, workflowTemplate.getWorkflowTemplateInfo().getGuid(), null), CacheConstants.CHANGE_TYPE.UPDATE);
//				DataServer.getTransactionManager().commitTransaction();
				this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().executeToDoItem();
			}
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().rollBack();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected WorkflowTemplate reUseWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		WorkflowTemplate workflowTemplate = this.getWorkflowTemplate(workflowTemplateGuid, true);
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			if (workflowTemplate != null)
			{
				if (workflowTemplate.getWorkflowTemplateInfo().isValid())
				{
					return workflowTemplate;
				}
				workflowTemplate.getWorkflowTemplateInfo().setValid(true);
				sds.save(workflowTemplate.getWorkflowTemplateInfo());
				this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItem(
						sds.getObjectDirectly(WorkflowTemplateInfo.class, workflowTemplate.getWorkflowTemplateInfo().getGuid(), null), CacheConstants.CHANGE_TYPE.UPDATE);
//				DataServer.getTransactionManager().commitTransaction();
				this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().executeToDoItem();
				return this.getWorkflowTemplate(workflowTemplateGuid, true);
			}
			return null;

		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().rollBack();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected WorkflowTemplateVo copyWorkflowTemplateWithOutGuid(String workflowTemplateGuid) throws ServiceRequestException
	{
		WorkflowTemplateVo workflowTemplateVo = this.getWorkflowTemplateDetail(workflowTemplateGuid);

		return workflowTemplateVo;
	}

	protected WorkflowTemplateVo getWorkflowTemplateDetail(String workflowTemplateGuid) throws ServiceRequestException
	{
		WorkflowTemplateVo workflowTemplateVo = new WorkflowTemplateVo();
		workflowTemplateVo.setTemplate(this.getWorkflowTemplate(workflowTemplateGuid, true));
		try
		{
			List<WorkflowTemplateAct> workflowTemplateActList = workflowTemplateVo.getTemplateActList();
			if (SetUtils.isNullList(workflowTemplateActList))
			{
				return workflowTemplateVo;
			}

			Map<String, WorkflowTemplateAct> templateActivityMap = new HashMap<String, WorkflowTemplateAct>();
			workflowTemplateVo.setTemplateActivityMap(templateActivityMap);
			for (WorkflowTemplateAct act : workflowTemplateActList)
			{
				if (!StringUtils.isGuid(act.getWorkflowTemplateActInfo().getGuid()))
				{
					continue;
				}

				this.fillWorkflowTemplateAct(act, act.getWorkflowTemplateActInfo().getGuid());

				templateActivityMap.put(act.getWorkflowTemplateActInfo().getActrtName(), act);

				List<WorkflowTemplateActClass> classSetList = act.getActrtClassList();
				if (SetUtils.isNullList(classSetList))
				{
					continue;

				}

				Map<String, WorkflowTemplateActClass> classActMap = new HashMap<String, WorkflowTemplateActClass>();
				act.setClassActMap(classActMap);
				for (WorkflowTemplateActClass classSet : classSetList)
				{
					String bmGuid = workflowTemplateVo.getTemplate().getWorkflowTemplateInfo().getBMGuid();
					if (BOMTemplateInfo.ALL.equalsIgnoreCase(bmGuid))
					{
						bmGuid = this.stubService.getEMM().getSharedBizModel().getGuid();
					}
					if (StringUtils.isNullString(classSet.getWorkflowTemplateActClassInfo().getBOGuid()))
					{
						continue;
					}
					BOInfo bizObject = this.stubService.getEMM().getBizObject(bmGuid, classSet.getWorkflowTemplateActClassInfo().getBOGuid());
					if (bizObject != null)
					{
						this.fillWorkflowTemplateActClass(classSet, bizObject);
						classActMap.put(bizObject.getGuid(), classSet);
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return workflowTemplateVo;
	}

	protected WorkflowTemplateVo getWorkflowTemplateDetailById(String workflowTemplateId) throws ServiceRequestException
	{
		WorkflowTemplate workflowTemplate = this.getWorkflowTemplateById(workflowTemplateId);
		if (workflowTemplate == null)
		{
			return null;
		}
		return this.getWorkflowTemplateDetail(workflowTemplate.getWorkflowTemplateInfo().getGuid());
	}

	private void buildNotice(WorkflowTemplateActAdvnoticeInfo notice)
	{
		if (notice != null)
		{
			notice.put("CREATEUSERNAME", this.getUserName(notice.getCreateUserGuid()));
			notice.put("UPDATEUSERNAME", this.getUserName(notice.getUpdateUserGuid()));
		}
	}

	private void buildPerformer(List<WorkflowTemplateActPerformerInfo> performerList)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(performerList))
		{
			for (WorkflowTemplateActPerformerInfo performer : performerList)
			{
				if (!StringUtils.isGuid(performer.getPerfGuid()))
				{
					continue;
				}

				switch (performer.getPerfType())
				{
				case USER:
					User user = sds.get(User.class, performer.getPerfGuid());
					if (user != null)
					{
						performer.setName(user.getName());
					}
					break;
				case GROUP:
					Group group = sds.get(Group.class, performer.getPerfGuid());
					if (group != null)
					{
						performer.setName(group.getName());
					}
					break;
				case RIG:
					RIG rig = sds.get(RIG.class, performer.getPerfGuid());
					if (rig != null)
					{
						Group g = sds.get(Group.class, rig.getGroupGuid());
						Role r = sds.get(Role.class, rig.getRoleGuid());
						if (g != null && r != null)
						{
							performer.setName(g.getName() + "-" + r.getName());
						}
					}
					break;
				case ROLE:
					Role role = sds.get(Role.class, performer.getPerfGuid());
					if (role != null)
					{
						performer.setName(role.getRoleName());
					}
					break;
				}
			}
		}
	}

	/**
	 * 数据层初步包装后数据
	 * 
	 * @return
	 */
	protected WorkflowTemplate getSimpleWorkflowTemplate(String workflowTemplateGuid)
	{
		WorkflowTemplate workflowTemplateInfo = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(workflowTemplateGuid);
		return workflowTemplateInfo;
	}

	public WorkflowTemplateInfo getWorkflowTemplateInfo(String workflowTemplateGuid)
	{
		WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(workflowTemplateGuid);
		return workflowTemplate == null ? null : workflowTemplate.getWorkflowTemplateInfo();
	}

	/**
	 * 数据层缓存获取，然后加载其他信息
	 * 
	 * @param workflowTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected WorkflowTemplate getWorkflowTemplate(String workflowTemplateGuid, boolean needDecorate) throws ServiceRequestException
	{
		WorkflowTemplate workflowTemplate = this.getSimpleWorkflowTemplate(workflowTemplateGuid);
		if (workflowTemplate == null)
		{
			return null;
		}
		if (needDecorate)
		{
			this.decorateWorkflowTemplateInfo(workflowTemplate);
		}
		return workflowTemplate;
	}

	protected WorkflowTemplate getWorkflowTemplateById(String workflowTemplateId) throws ServiceRequestException
	{
		WorkflowTemplate workflowTemplateInfo = this.stubService.getWfTemplateCacheStub().getWorkflowTemplateById(workflowTemplateId);

		if (workflowTemplateInfo == null)
		{
			return null;
		}
		this.decorateWorkflowTemplateInfo(workflowTemplateInfo);
		return workflowTemplateInfo;

	}

	protected WorkflowTemplateInfo getWorkflowTemplateInfoById(String workflowTemplateId) throws ServiceRequestException
	{
		WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplateById(workflowTemplateId);

		return workflowTemplate == null ? null : workflowTemplate.getWorkflowTemplateInfo();
	}

	private void decorateWorkflowTemplateInfo(WorkflowTemplate workflowTemplateInfo) throws ServiceRequestException
	{
		this.buildScopeBOList(workflowTemplateInfo.getWorkflowTemplateInfo().getBMGuid(), workflowTemplateInfo.getListScopeBO());
		this.buildScopeRTList(workflowTemplateInfo.getListScopeRT());

		this.buildPerformer(workflowTemplateInfo.getObserverList());
		this.buildPerformer(workflowTemplateInfo.getUseList());
		this.buildPerformer(workflowTemplateInfo.getStartUpNotifierList());
		this.buildPerformer(workflowTemplateInfo.getCompleteNotifierList());

		// 开始流程通知设置
		this.buildNotice(workflowTemplateInfo.getStartUpAdvnotice());

		// 结束流程通知设置
		this.buildNotice(workflowTemplateInfo.getCompleteAdvnotice());
	}

	private WorkflowTemplateAct fillWorkflowTemplateAct(WorkflowTemplateAct workflowTemplateAct, String workflowTemplateActrtGuid) throws ServiceRequestException
	{
		if (workflowTemplateAct == null)
		{
			return null;
		}

		if (!StringUtils.isGuid(workflowTemplateAct.getWorkflowTemplateActInfo().getGuid()))
		{
			return workflowTemplateAct;
		}

		// 将截止日期通知人设置
		this.buildNotice(workflowTemplateAct.getAdvAdvnotice());

		// 将截止日期通知人明细设置
		this.buildPerformer(workflowTemplateAct.getAdvNoticeperList());

		// 超时截止日期通知人设置
		this.buildNotice(workflowTemplateAct.getDefAdvnotice());

		// 超时截止日期通知人明细设置
		this.buildPerformer(workflowTemplateAct.getDefNoticeperList());

		// 完成通知设置
		this.buildNotice(workflowTemplateAct.getFinAdvnotice());

		// 完成通知人明细设置
		this.buildPerformer(workflowTemplateAct.getFinNoticeperList());

		// 指定执行人
		this.buildPerformer(workflowTemplateAct.getExecutorList());

		// 执行人范围
		this.buildPerformer(workflowTemplateAct.getScopeExecutorList());

		return workflowTemplateAct;
	}

	public WorkflowTemplateAct getWorkflowTemplateActSetInfoSingle(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		WorkflowTemplate template = this.getSimpleWorkflowTemplate(workflowTemplateGuid);
		if (template != null)
		{
			return template.getTemplateAct(actrtName);
		}

		return null;
	}

	public WorkflowTemplateAct getWorkflowTemplateActSet(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		WorkflowTemplateAct workflowTemplateAct = this.getWorkflowTemplateActSetInfoSingle(workflowTemplateGuid, actrtName);

		if (workflowTemplateAct == null)
		{
			return null;
		}

		this.fillWorkflowTemplateAct(workflowTemplateAct, workflowTemplateAct.getWorkflowTemplateActInfo().getGuid());
		return workflowTemplateAct;

	}

	protected WorkflowTemplateActInfo getWorkflowTemplateActSetInfo(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		WorkflowTemplateAct workflowTemplateAct = this.getWorkflowTemplateActSetInfoSingle(workflowTemplateGuid, actrtName);

		if (workflowTemplateAct == null)
		{
			return null;
		}

		return workflowTemplateAct.getWorkflowTemplateActInfo();

	}

	protected List<WorkflowTemplateActClassInfo> listWorkflowTemplateActClassSetInfoSingle(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		try
		{
			WorkflowTemplateAct workflowTemplateActSetInfoSingle = this.getWorkflowTemplateActSetInfoSingle(workflowTemplateGuid, actrtName);
			if (workflowTemplateActSetInfoSingle != null)
			{
				if (!StringUtils.isGuid(workflowTemplateActSetInfoSingle.getWorkflowTemplateActInfo().getGuid()))
				{
					return null;
				}

				List<WorkflowTemplateActClass> classSetList = workflowTemplateActSetInfoSingle.getActrtClassList();
				List<WorkflowTemplateActClassInfo> resultList = new ArrayList<>();
				if (!SetUtils.isNullList(classSetList))
				{
					classSetList.forEach(classSet -> {
						resultList.add(classSet.getWorkflowTemplateActClassInfo());
					});
				}

				return resultList;
			}
			else
			{
				return null;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected WorkflowTemplateActClass getWorkflowTemplateActClassSetInfo(WorkflowTemplateAct workflowTemplateAct, BOInfo boInfo) throws ServiceRequestException
	{
		try
		{
			List<WorkflowTemplateActClass> classSetList = workflowTemplateAct.getActrtClassList();
			if (classSetList != null)
			{
				classSetList = classSetList.stream().filter(classSet -> boInfo.getGuid().equals(classSet.getWorkflowTemplateActClassInfo().getBOGuid()))
						.collect(Collectors.toList());
			}
			if (SetUtils.isNullList(classSetList))
			{
				return null;
			}

			WorkflowTemplateActClass classSet = classSetList.get(0);
			this.fillWorkflowTemplateActClass(classSet, boInfo);
			return classSet;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void fillWorkflowTemplateActClass(WorkflowTemplateActClass classSet, BOInfo bizObject) throws ServiceRequestException
	{
		if (classSet == null)
		{
			return;
		}

		List<WorkflowTemplateActClassUIInfo> classUIList = classSet.getActClassUIList();
		if (!SetUtils.isNullList(classUIList))
		{
			if (bizObject != null)
			{
				ClassInfo classInfo = DataServer.getSystemDataService().findInCache(ClassInfo.class,
						new FieldValueEqualsFilter<ClassInfo>(ClassInfo.NAME, bizObject.getClassName()));
				if (classInfo != null)
				{
					UpperKeyMap filterMap = new UpperKeyMap();
					for (WorkflowTemplateActClassUIInfo classUI : classUIList)
					{
						filterMap.put(UIObjectInfo.CLASSFK, classInfo.getGuid());
						filterMap.put(UIObjectInfo.UINAME, classUI.getBOUIName());
						UIObjectInfo uiObject = DataServer.getSystemDataService().findInCache(UIObjectInfo.class, new FieldValueEqualsFilter<UIObjectInfo>(filterMap));
						if (uiObject != null)
						{
							classUI.setUITitle(uiObject.getTitle());
						}
					}
				}
			}
		}
	}

	protected List<WorkflowTemplateInfo> listWorkflowTemplateInfoByWFNameWithOutObserver(String workFlowName) throws ServiceRequestException
	{
		List<WorkflowTemplate> workflowTemplateList = this.stubService.getWfTemplateCacheStub().listWorkflowTemplateInfoByWFName(workFlowName);
		List<WorkflowTemplateInfo> resultList = new ArrayList<WorkflowTemplateInfo>();

		workflowTemplateList.stream().filter((wfTemplate) -> wfTemplate.getWorkflowTemplateInfo().isValid()).forEach((wfTemplate) -> {
			resultList.add(wfTemplate.getWorkflowTemplateInfo());
		});

		return resultList;
	}

	protected List<WorkflowTemplateInfo> listWorkflowTemplateInfoByWFNameContainObsolete(String workFlowName) throws ServiceRequestException
	{
		List<WorkflowTemplate> wfTemplateList = this.stubService.getWfTemplateCacheStub().listWorkflowTemplateInfoByWFName(workFlowName);
		List<WorkflowTemplateInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(wfTemplateList))
		{
			wfTemplateList.forEach(wfTemplate -> {
				resultList.add(wfTemplate.getWorkflowTemplateInfo());
			});
		}
		return resultList;
	}

	protected List<String> listWorkflowName(boolean isContainObsolete) throws ServiceRequestException
	{
		List<WorkflowTemplate> workflowTemplateList = this.stubService.getWfTemplateCacheStub().listAllWorkflowTemplateInfo();
		List<String> nameList = new ArrayList<String>();
		if (!SetUtils.isNullList(workflowTemplateList))
		{
			for (WorkflowTemplate workflowTemplate : workflowTemplateList)
			{
				if ((isContainObsolete || workflowTemplate.getWorkflowTemplateInfo().isValid())
						&& !StringUtils.isNullString(workflowTemplate.getWorkflowTemplateInfo().getWFName()))
				{
					nameList.add(workflowTemplate.getWorkflowTemplateInfo().getWFName());
				}
			}
		}

		Set<String> hashSet = new HashSet<String>(nameList);
		nameList.clear();
		nameList.addAll(hashSet);

		return nameList;

	}

	private void saveActPerformers(List<WorkflowTemplateActPerformerInfo> actPerformerList, String templateActrtGuid, String templateGuid, String dataType, String operatorGuid,
			boolean isObserver) throws DynaDataException, ServiceRequestException

	{
		SystemDataService sds = DataServer.getSystemDataService();

		UpperKeyMap filter = new UpperKeyMap();
		if (isObserver)
		{
			filter.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, templateGuid);
		}
		else
		{
			filter.put(WorkflowTemplateActPerformerInfo.TEMPLATEACTRTGUID, templateActrtGuid);
		}
		filter.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, dataType);

		List<WorkflowTemplateActPerformerInfo> needDelList = sds.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<>(filter));
		sds.deleteFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<>(filter));
		this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo().registerToDoItemList(needDelList, CacheConstants.CHANGE_TYPE.DELETE);

		if (SetUtils.isNullList(actPerformerList))
		{
			return;
		}

		for (WorkflowTemplateActPerformerInfo performer : actPerformerList)
		{
			if (performer == null)
			{
				continue;
			}

			performer.setTemplateActrtGuid(templateActrtGuid);
			performer.setTemplateGuid(templateGuid);
			performer.setGuid(null);
			if (StringUtils.isNullString(performer.getGuid()))
			{
				performer.setCreateUserGuid(operatorGuid);
			}
			performer.setUpdateUserGuid(operatorGuid);
			performer.setNoticeType(dataType);

			String performerGuid = sds.save(performer);
			this.stubService.getWfTemplateCacheStub().getWorkflowTemplateCacheInfo()
					.registerToDoItem(sds.getObjectDirectly(WorkflowTemplateActPerformerInfo.class, performerGuid, null), CacheConstants.CHANGE_TYPE.INSERT);
		}
	}

	protected WorkflowTemplateActClass getWorkflowTemplateActClassSetInfo(String workflowTemplateGuid, String workflowActrtName, String classGuid, String className,
			boolean isCurrentBM, String templateBMGuid) throws ServiceRequestException
	{

		ClassInfo classInfo = ((EMMImpl) this.stubService.getEMM()).getClassStub().getClassInfo(className, classGuid);
		if (classInfo == null)
		{
			return null;
		}

		WorkflowTemplateAct workflowTemplateAct = null;
		WorkflowTemplate template = this.getSimpleWorkflowTemplate(workflowTemplateGuid);
		if (template != null)
		{
			workflowTemplateAct = template.getTemplateAct(workflowActrtName);
		}

		if (workflowTemplateAct == null)
		{
			return null;
		}

		BOInfo bizObject = null;
		if (isCurrentBM)
		{
			bizObject = this.stubService.getEMM().getCurrentBizObject(classInfo.getGuid());
			if (template.getWorkflowTemplateInfo().getBMGuid().equals(BOMTemplateInfo.ALL))
			{
				templateBMGuid = this.stubService.getEMM().getSharedBizModel().getGuid();
				bizObject = this.stubService.getEMM().getBizObject(templateBMGuid, classInfo.getGuid(), null);
			}
		}
		else
		{
			if (BOMTemplateInfo.ALL.equalsIgnoreCase(templateBMGuid))
			{
				templateBMGuid = this.stubService.getEMM().getSharedBizModel().getGuid();
			}
			bizObject = this.stubService.getEMM().getBizObject(templateBMGuid, classInfo.getGuid(), null);
		}
		return this.getWorkflowTemplateActClassSetInfo(workflowTemplateAct, bizObject);

	}

	/**
	 * 属于应用，取用当前用户登录模型
	 * 
	 * @param workflowTemplateGuid
	 * @param workflowActrtName
	 * @param classGuid
	 * @param className
	 * @param relationTemplateName
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowTemplateActClassRelationInfo getWorkflowTemplateActClassSetRelationInfo(String workflowTemplateGuid, String workflowActrtName, String classGuid,
			String className, String relationTemplateName) throws ServiceRequestException
	{
		ClassInfo classInfo = ((EMMImpl) this.stubService.getEMM()).getClassStub().getClassInfo(className, classGuid);
		if (classInfo == null)
		{
			return null;
		}

		WorkflowTemplateAct workflowTemplateAct = this.getWorkflowTemplateActSetInfoSingle(workflowTemplateGuid, workflowActrtName);

		if (workflowTemplateAct == null)
		{
			return null;
		}

		BOInfo bizObject = this.stubService.getEMM().getCurrentBizObject(classInfo.getGuid());

		WorkflowTemplateActClass actClass = this.getWorkflowTemplateActClassSetInfo(workflowTemplateAct, bizObject);

		if (actClass != null)
		{
			if (!SetUtils.isNullList(actClass.getActClassRelationList()))
			{
				for (WorkflowTemplateActClassRelationInfo classRelation : actClass.getActClassRelationList())
				{
					if (classRelation.getRelationName().equals(relationTemplateName))
					{
						return classRelation;
					}
				}
			}
		}

		return null;
	}

	private void checkBOScopeAndRelationTempalte(WorkflowTemplate info) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(info.getListScopeRT()))
		{
			WorkflowTemplateInfo WorkflowTemplateDto = info.getWorkflowTemplateInfo();
			String dtobmGuid = WorkflowTemplateDto == null ? "" : WorkflowTemplateDto.getBMGuid();
			for (WorkflowTemplateScopeRTInfo rt : info.getListScopeRT())
			{
				RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(rt.getTemplateID());
				String bmGuid = null;
				if ("ALL".equalsIgnoreCase(dtobmGuid))
				{
					bmGuid = this.stubService.getEMM().getSharedBizModel().getGuid();
				}
				else
				{
					bmGuid = dtobmGuid;
				}
				List<BOInfo> listAllSubBOInfoContain = this.stubService.getEMM().listAllSubBOInfoContain(relationTemplate.getEnd1BoName(), bmGuid);
				boolean isExist = false;
				if (!SetUtils.isNullList(listAllSubBOInfoContain))
				{
					for (BOInfo boInfo : listAllSubBOInfoContain)
					{
						if (info.containsScropBO(boInfo.getBOName()))
						{
							isExist = true;
							break;
						}

					}
				}

				if (!isExist)
				{
					throw new ServiceRequestException("ID_APP_WORKFLOW_TEMPLATE_CHECK_RELATION", "relation temaplte'end1 is not in scopebo:" + rt.getTemplateID(), null,
							rt.getTemplateID());
				}
			}
		}
	}

	private void buildScopeBOList(String bmGuid, List<WorkflowTemplateScopeBoInfo> scopeBOList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(scopeBOList))
		{
			if (bmGuid.equals("ALL"))
			{
				bmGuid = this.stubService.getEMM().getSharedBizModel().getGuid();
			}
			for (WorkflowTemplateScopeBoInfo bo : scopeBOList)
			{
				BOInfo boInfo = this.stubService.getEMM().getBizObject(bmGuid, bo.getBOGuid());
				if (boInfo != null)
				{
					bo.setBOName(boInfo.getName());
					bo.setBOTitle(boInfo.getTitle());
				}
				bo.put("CREATEUSERNAME", this.getUserName(bo.getCreateUserGuid()));
				bo.put("UPDATEUSERNAME", this.getUserName(bo.getUpdateUserGuid()));
			}
		}
	}

	private void buildScopeRTList(List<WorkflowTemplateScopeRTInfo> scopeRTList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(scopeRTList))
		{
			for (WorkflowTemplateScopeRTInfo scopeRT : scopeRTList)
			{
				RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(scopeRT.getTemplateID());
				if (template != null)
				{
					scopeRT.setTemplateTitle(template.getTitle());
				}
				scopeRT.put("CREATEUSERNAME", this.getUserName(scopeRT.getCreateUserGuid()));
				scopeRT.put("UPDATEUSERNAME", this.getUserName(scopeRT.getUpdateUserGuid()));
			}
		}
	}

	private String getUserName(String userGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		User user = sds.get(User.class, userGuid);
		return user == null ? null : user.getUserName();
	}

	protected List<WorkflowTemplateActPerformerInfo> listAllPerformerOfAct(String actGuid, String noticeType)
	{
		WorkflowTemplateAct wfAct = this.stubService.getWfTemplateCacheStub().getWorkflowTemplateAct(actGuid);

		return wfAct == null ? null : wfAct.listAllPerformer();
	}

	protected List<WorkflowTemplateActCompanyInfo> listWorkflowTemplateActCompanyInfoOfAct(String wfTemplateActGuid)
	{
		WorkflowTemplateAct wfAct = this.stubService.getWfTemplateCacheStub().getWorkflowTemplateAct(wfTemplateActGuid);
		return wfAct == null ? null : wfAct.getActCompanyList();
	}

	protected Map<String, WorkflowTemplateActAdvnoticeInfo> getAllAdvnoticeSetInfoOfAct(String wfTemplateActGuid)
	{
		WorkflowTemplateAct wfAct = this.stubService.getWfTemplateCacheStub().getWorkflowTemplateAct(wfTemplateActGuid);
		Map<String, WorkflowTemplateActAdvnoticeInfo> resultMap = new HashMap<>();
		if (wfAct != null)
		{
			resultMap.put(WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF, wfAct.getDefAdvnotice());
			resultMap.put(WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTADV, wfAct.getAdvAdvnotice());
			resultMap.put(WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTFIN, wfAct.getFinAdvnotice());
		}
		return resultMap;
	}

	protected WorkflowTemplateActAdvnoticeInfo getAdvnoticeSetInfoByType(String wfTemplateActGuid, String noticeType)
	{
		return this.getAllAdvnoticeSetInfoOfAct(wfTemplateActGuid).get(noticeType);
	}

	public WorkflowTemplateActClass getWorkflowTemplateActClass(String actClassguid)
	{
		WorkflowTemplateActClass actClass = this.stubService.getWfTemplateCacheStub().getWorkflowTemplateActClass(actClassguid);
		return actClass;
	}

	protected WorkflowTemplateActClassInfo getWorkflowTemplateActClassInfo(String actClassguid)
	{
		WorkflowTemplateActClass actClass = this.getWorkflowTemplateActClass(actClassguid);
		return actClass == null ? null : actClass.getWorkflowTemplateActClassInfo();
	}

	protected List<WorkflowTemplateActClassUIInfo> listUIInfoOfWfActClass(String actClassguid)
	{
		WorkflowTemplateActClass actClass = this.getWorkflowTemplateActClass(actClassguid);
		return actClass == null ? null : actClass.getActClassUIList();
	}

	protected List<WorkflowTemplateActClassRelationInfo> listRelationInfoOfWfActClass(String actClassguid)
	{
		WorkflowTemplateActClass actClass = this.getWorkflowTemplateActClass(actClassguid);
		return actClass == null ? null : actClass.getActClassRelationList();
	}

	protected Map<String, List<WorkflowTemplateActPerformerInfo>> getAllPerformerListByTypeOfTemplate(String wfTemplateGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		Map<String, List<WorkflowTemplateActPerformerInfo>> resultMap = new HashMap<>();
		WorkflowTemplate wfTemplate = this.getWorkflowTemplate(wfTemplateGuid, true);
		if (wfTemplate != null)
		{
			Map<String, Map<String, WorkflowTemplateActPerformerInfo>> allPerformerMap = wfTemplate.getActperformerMap();
			if (!SetUtils.isNullMap(allPerformerMap))
			{
				allPerformerMap.forEach((noticeType, sourceMap) -> {
					if (resultMap.get(noticeType) == null)
					{
						resultMap.put(noticeType, new ArrayList<>());
					}
					sourceMap.forEach((guid, performer) -> {
						resultMap.get(noticeType).add(performer);
					});

				});
			}
		}
		return resultMap;
	}

	protected List<WorkflowTemplateActPerformerInfo> listAllPerformerOfTemplate(String wfTemplateGuid) throws ServiceRequestException
	{
		Map<String, List<WorkflowTemplateActPerformerInfo>> typeToListOfPerformer = this.getAllPerformerListByTypeOfTemplate(wfTemplateGuid);
		List<WorkflowTemplateActPerformerInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullMap(typeToListOfPerformer))
		{
			typeToListOfPerformer.forEach((noticeType, performerList) -> {
				if (!SetUtils.isNullList(performerList))
				{
					resultList.addAll(performerList);
				}
			});
		}
		return resultList;
	}

	protected List<WorkflowTemplateActPerformerInfo> listPerFormerOfTemplateByType(String wfTemplateGuid, String noticeType) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		Map<String, List<WorkflowTemplateActPerformerInfo>> typeToListOfPerformer = this.getAllPerformerListByTypeOfTemplate(wfTemplateGuid);

		return typeToListOfPerformer.get(noticeType);
	}

	protected Map<String, WorkflowTemplateActAdvnoticeInfo> getAllAdvnoticeSetInfoOfTemplate(String wfTemplateGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		WorkflowTemplate wfTemplate = this.getWorkflowTemplate(wfTemplateGuid, true);
		Map<String, WorkflowTemplateActAdvnoticeInfo> resultMap = new HashMap<>();
		if (wfTemplate != null)
		{
			resultMap.put(WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_OPEN, wfTemplate.getStartUpAdvnotice());
			resultMap.put(WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_CLOSE, wfTemplate.getCompleteAdvnotice());
		}
		return resultMap;
	}

	protected List<WorkflowTemplateScopeBoInfo> listScopeBoCanLaunchOfTemplate(String wfTemplateguid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		WorkflowTemplate wfTemplate = this.getWorkflowTemplate(wfTemplateguid, true);
		List<WorkflowTemplateScopeBoInfo> resultList = new ArrayList<>();
		if (wfTemplate != null)
		{
			List<WorkflowTemplateScopeBoInfo> boList = wfTemplate.getListScopeBO();
			if (!SetUtils.isNullList(boList))
			{
				boList.stream().filter(wfBo -> wfBo.canLaunch()).forEach(wfBo -> {
					resultList.add(wfBo);
				});
			}
		}
		return resultList;

	}

	protected List<WorkflowTemplateScopeBoInfo> listScopeBoOfTemplate(String wfTemplateguid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		WorkflowTemplate wfTemplate = this.getWorkflowTemplate(wfTemplateguid, true);

		return wfTemplate == null ? new ArrayList<>() : wfTemplate.getListScopeBO();
	}

	protected List<WorkflowTemplateScopeRTInfo> listScoperRelationTemplateOfTemplate(String wfTemplateguid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		WorkflowTemplate wfTemplate = this.getWorkflowTemplate(wfTemplateguid, true);

		return wfTemplate == null ? new ArrayList<>() : wfTemplate.getListScopeRT();
	}

}
