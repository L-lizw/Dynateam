/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 预警管理
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.ppms.app.EventApp;
import dyna.app.service.brs.ppms.app.EventAppFactory;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.WarningNotifier;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.WarningNotifiterEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangLHB
 *         预警管理
 * 
 */
@Component
public class WarningNoticeStub extends AbstractServiceStub<PPMSImpl>
{

	public EarlyWarning saveEarlyWarning(EarlyWarning earlyWarning) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			earlyWarning.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
			if (!StringUtils.isGuid(earlyWarning.getGuid()))
			{
				earlyWarning.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}
			// 保存EarlyWarning
			String guid = sds.save(earlyWarning);
			if (StringUtils.isGuid(guid))
			{
				earlyWarning.setGuid(guid);
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(WarningNotifier.WARNINGGUID, earlyWarning.getGuid());
			// 新建预警通知人
			if (!SetUtils.isNullList(earlyWarning.getNoticeMemberList()))
			{
				// 判断是否含有通知人
				List<WarningNotifier> listWarningNotify = this.listWarningNotifier(earlyWarning.getGuid());
				if (!SetUtils.isNullList(listWarningNotify))
				{
					// 批量删除原有通知人
					sds.delete(WarningNotifier.class, filter, "delete");
				}
				for (WarningNotifier warning : earlyWarning.getNoticeMemberList())
				{
					warning.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					warning.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
					warning.setGuid(null);
					warning.setWarningGuid(earlyWarning.getGuid());
					sds.save(warning);
				}
			}
			else
			{
				// 批量删除原有通知人
				sds.delete(WarningNotifier.class, filter, "delete");
			}

			// 新建适用对象
			if (!SetUtils.isNullList(earlyWarning.getApplyToTaskList()))
			{
				// 先删除所有的适用对象
				this.deleteApplyToTask(earlyWarning.getGuid());
				// 保存适用对象
				this.saveApplyToTask(earlyWarning.getGuid(), earlyWarning.getApplyToTaskList());
			}
			else
			{
				// 删除所有的适用对象
				this.deleteApplyToTask(earlyWarning.getGuid());
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
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

		return this.getEarlyWarningByGuid(earlyWarning.getGuid());
	}

	/**
	 * @param guid
	 * @param forwardUserList
	 * @throws ServiceRequestException
	 */
	private void saveApplyToTask(String warningGuid, List<ObjectGuid> appltToTaskList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		try
		{
			for (ObjectGuid object : appltToTaskList)
			{
				EarlyWarning applyTask = new EarlyWarning();
				applyTask.put("WARNINGGUID", warningGuid);
				applyTask.put(EarlyWarning.TASKGUID + PMConstans.MASTER, object.getMasterGuid());
				applyTask.put(EarlyWarning.TASKGUID + PMConstans.CLASS, object.getClassGuid());
				applyTask.put(EarlyWarning.TASKGUID, object.getGuid());
				applyTask.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
				applyTask.put(SystemObject.CREATE_USER_GUID, operatorGuid);
				sds.save(applyTask, "insertWarningTask");
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param guid
	 * @throws ServiceRequestException
	 */
	private void deleteApplyToTask(String warningGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(warningGuid))
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("WARNINGGUID", warningGuid);
			try
			{
				List<ObjectGuid> applyTaskList = this.listApplyToTaskByWarningGuid(warningGuid);
				if (!SetUtils.isNullList(applyTaskList))
				{
					sds.delete(EarlyWarning.class, filter, "deleteWarningTask");
				}
			}
			catch (DynaDataException e)
			{
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
		}

	}

	/**
	 * @param warningGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<ObjectGuid> listApplyToTaskByWarningGuid(String warningGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("WARNINGGUID", warningGuid);
		List<ObjectGuid> value = new ArrayList<ObjectGuid>();
		try
		{
			List<EarlyWarning> applyTaskList = sds.query(EarlyWarning.class, filter, "selectWarningTask");
			if (!SetUtils.isNullList(applyTaskList))
			{
				for (EarlyWarning task : applyTaskList)
				{
					String masterGuidKey = EarlyWarning.TASKGUID + PMConstans.MASTER;
					String classGuidKey = EarlyWarning.TASKGUID + PMConstans.CLASS;
					String guidKey = EarlyWarning.TASKGUID;
					if (task != null)
					{
						ObjectGuid objectGuid = new ObjectGuid();
						if (task.get(masterGuidKey) != null && task.get(masterGuidKey) != "")
						{
							objectGuid.setMasterGuid((String) task.get(masterGuidKey));
						}
						if (task.get(classGuidKey) != null && task.get(classGuidKey) != "")
						{
							objectGuid.setClassGuid((String) task.get(classGuidKey));
						}
						if (task.get(guidKey) != null && task.get(guidKey) != "")
						{
							objectGuid.setGuid((String) task.get(guidKey));
						}
						value.add(objectGuid);
					}
				}
			}
			return value;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<WarningNotifier> listWarningNotifier(String earlyWarningGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(WarningNotifier.WARNINGGUID, earlyWarningGuid);
		try
		{
			List<WarningNotifier> warningNotifierList = sds.query(WarningNotifier.class, filter, "selectOrganizationOfWarning");
			if (warningNotifierList != null)
			{
				for (WarningNotifier item : warningNotifierList)
				{
					if (item.getNotifierType() == WarningNotifiterEnum.PROJECTROLE)
					{
						if (!StringUtils.isNullString(item.getNotifierGuid()))
						{
							ProjectRole role = this.stubService.getProjectRole(item.getNotifierGuid());
							if (role != null)
							{
								item.setName(role.getRoleId() + "-" + role.getRoleName());
							}
						}
					}
					else if (item.getNotifierType() == WarningNotifiterEnum.GROUP)
					{
						if (!StringUtils.isNullString(item.getNotifierGuid()))
						{
							Group group = this.stubService.getAAS().getGroup(item.getNotifierGuid());
							if (group != null)
							{
								item.setName(group.getGroupId() + "-" + group.getGroupName());
							}
						}
					}
					else if (item.getNotifierType() == WarningNotifiterEnum.RIG)
					{
						if (!StringUtils.isNullString(item.getNotifierGuid()))
						{
							RIG rig = this.stubService.getAAS().getRIG(item.getNotifierGuid());
							if (rig != null)
							{
								Group group = null;
								Role role = null;
								if (!StringUtils.isNullString(rig.getGroupGuid()))
								{
									group = this.stubService.getAAS().getGroup(rig.getGroupGuid());
								}
								if (!StringUtils.isNullString(rig.getRoleGuid()))
								{
									role = this.stubService.getAAS().getRole(rig.getRoleGuid());
								}
								if (group != null && role != null)
								{
									item.setName(group.getGroupName() + "-" + role.getRoleName());
								}
							}
						}
					}
					else if (item.getNotifierType() == WarningNotifiterEnum.USER)
					{
						if (!StringUtils.isNullString(item.getNotifierGuid()))
						{
							User user = this.stubService.getAAS().getUser(item.getNotifierGuid());
							if (user != null)
							{
								item.setName(user.getUserId() + "-" + user.getUserName());
							}
						}
					}
				}
			}
			return warningNotifierList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public void deleteEarlyWarning(String warningGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, warningGuid);
			sds.delete(EarlyWarning.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public EarlyWarning getEarlyWarningByGuid(String warningGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, warningGuid);
		try
		{
			List<EarlyWarning> earlyWarningList = sds.query(EarlyWarning.class, filter);
			EarlyWarning earlyWarning = null;
			if (!SetUtils.isNullList(earlyWarningList))
			{
				earlyWarning = earlyWarningList.get(0);
				if (earlyWarning != null)
				{
					// 设置通知对象
					earlyWarning.setNoticeMemberList(this.listWarningNotifier(warningGuid));
					// 设置适用对象
					earlyWarning.setApplyToTaskList(this.listApplyToTaskByWarningGuid(warningGuid));
				}
			}
			return earlyWarning;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 通过项目或这项目模板的Guid取得所有的预警对象
	 * 
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<EarlyWarning> listEarlyWarning(String projectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(EarlyWarning.PROJECTGUID, projectGuid);
		try
		{
			List<EarlyWarning> earlyWarningList = sds.query(EarlyWarning.class, filter);
			if (!SetUtils.isNullList(earlyWarningList))
			{
				for (EarlyWarning earlyWarning : earlyWarningList)
				{
					if (earlyWarning != null && !StringUtils.isNullString(earlyWarning.getGuid()))
					{
						// 设置可用的人
						earlyWarning.setNoticeMemberList(this.listWarningNotifier(earlyWarning.getGuid()));
						// 设置可用的任务
						earlyWarning.setApplyToTaskList(this.listApplyToTaskByWarningGuid(earlyWarning.getGuid()));
					}

				}
			}
			return earlyWarningList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 启用预警
	 * 
	 * @param warningGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public EarlyWarning startEarlyWarning(String warningGuid) throws ServiceRequestException
	{
		EarlyWarning orig = this.getEarlyWarningByGuid(warningGuid);
		if (orig != null)
		{
			orig.setEnable(true);
		}
		return this.saveEarlyWarning(orig);
	}

	/**
	 * 停用预警
	 * 
	 * @param warningGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public EarlyWarning stopEarlyWarning(String warningGuid) throws ServiceRequestException
	{
		EarlyWarning orig = this.getEarlyWarningByGuid(warningGuid);
		if (orig != null)
		{
			orig.setEnable(false);
		}
		return this.saveEarlyWarning(orig);
	}

	/**
	 * 通过项目guid取得所有启动的预警
	 * 
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<EarlyWarning> listStartEarlyWarning(String projectGuid) throws ServiceRequestException
	{
		List<EarlyWarning> earlyWarningList = this.listEarlyWarning(projectGuid);
		List<EarlyWarning> returnEarlyWarning = new ArrayList<EarlyWarning>();
		if (!SetUtils.isNullList(earlyWarningList))
		{
			for (EarlyWarning early : earlyWarningList)
			{
				if (early.isEnable())
				{
					returnEarlyWarning.add(early);
				}
			}
		}
		return returnEarlyWarning;
	}

	/**
	 * 分发项目管理的预警
	 * 
	 * @throws ServiceRequestException
	 */
	protected void dispatchProjectWarningRule() throws ServiceRequestException
	{
		BOInfo pmProjectBoInfo = this.stubService.getPMProjectBoInfo();
		if (pmProjectBoInfo == null)
		{
			return;
		}

		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(pmProjectBoInfo.getClassName(), null, true);
		searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		CodeItemInfo codeItem = null;
		try
		{
			searchCondition.startGroup();
			codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(ProjectStatusEnum.INI);
			searchCondition.addFilterWithOR(PPMFoundationObjectUtil.EXECUTESTATUS, codeItem.getGuid(), OperateSignEnum.YES);
			codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(ProjectStatusEnum.RUN);
			searchCondition.addFilterWithOR(PPMFoundationObjectUtil.EXECUTESTATUS, codeItem.getGuid(), OperateSignEnum.YES);
			codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(ProjectStatusEnum.PUS);
			searchCondition.addFilterWithOR(PPMFoundationObjectUtil.EXECUTESTATUS, codeItem.getGuid(), OperateSignEnum.YES);
			searchCondition.endGroup();
		}
		catch (Exception e)
		{
			DynaLogger.error("dispatchProjectWarningRule executor error: please check ProjectStatus CodeItem", e);
		}
		// 为处理Code,object name,fullname
		searchCondition.addResultField(PPMFoundationObjectUtil.EXECUTESTATUS);// 项目状态
		searchCondition.addResultField(PPMFoundationObjectUtil.EXECUTOR);// 任务执行人
		searchCondition.addResultField(PPMFoundationObjectUtil.PROJECTTYPE);// 项目类型

		List<FoundationObject> listObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
		if (!SetUtils.isNullList(listObject))
		{
			for (FoundationObject foundation : listObject)
			{
				try
				{
					if (foundation.getObjectGuid() != null && StringUtils.isGuid(foundation.getObjectGuid().getGuid()))
					{
						List<EarlyWarning> ruleList = this.listStartEarlyWarning(foundation.getObjectGuid().getGuid());
						this.executeProjectWarningRule(foundation, ruleList);
					}
				}
				catch (Exception e)
				{
					DynaLogger.error("execute error", e);
				}
			}
		}

	}

	/**
	 * 执行项目预警
	 * 
	 * @param foundation
	 * @param ruleList
	 * @param b
	 * @throws ServiceRequestException
	 */
	private void executeProjectWarningRule(FoundationObject projectFoundationObject, List<EarlyWarning> ruleList) throws ServiceRequestException
	{

		List<FoundationObject> taskObjectList = null;
		if (!SetUtils.isNullList(ruleList))
		{
			for (EarlyWarning warning : ruleList)
			{
				if (warning == null || warning.getEventType() == null || !warning.isEnable())
				{
					continue;
				}
				if (warning.getEventType().getType().equalsIgnoreCase("PROJECT"))
				{
					EventApp eventApp = EventAppFactory.getEventAppFactory().getEventApp(warning.getEventType().name());
					if (eventApp != null)
					{
						try
						{
							eventApp.execute(this.stubService, projectFoundationObject, null, warning);
						}
						catch (Exception e)
						{
							DynaLogger.error("project execute error", e);
						}
					}
				}
				else
				{
					if (!SetUtils.isNullList(warning.getApplyToTaskList()))
					{
						taskObjectList = this.listAppointTask(warning);
					}
					else
					{
						List<ClassInfo> taskClassList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IPMTask);
						ClassInfo taskClassInfo = taskClassList.get(0);
						SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(taskClassInfo.getName(), null, true);
						searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);

						searchCondition.startGroup();
						CodeItemInfo codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(TaskStatusEnum.RUN);
						searchCondition.addFilterWithOR(PPMFoundationObjectUtil.EXECUTESTATUS, codeItem.getGuid(), OperateSignEnum.YES);
						codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(TaskStatusEnum.PUS);
						searchCondition.addFilterWithOR(PPMFoundationObjectUtil.EXECUTESTATUS, codeItem.getGuid(), OperateSignEnum.YES);
						codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(TaskStatusEnum.INI);
						searchCondition.addFilterWithOR(PPMFoundationObjectUtil.EXECUTESTATUS, codeItem.getGuid(), OperateSignEnum.YES);
						searchCondition.endGroup();

						searchCondition.addFilter(PPMFoundationObjectUtil.OWNERPROJECT, projectFoundationObject.getObjectGuid(), OperateSignEnum.EQUALS);

						// 为处理Code,object name,fullname
						searchCondition.addResultField(PPMFoundationObjectUtil.EXECUTESTATUS);
						searchCondition.addResultField(PPMFoundationObjectUtil.EXECUTOR);
						searchCondition.addResultField(PPMFoundationObjectUtil.TASKTYPE);
						searchCondition.addResultField(PPMFoundationObjectUtil.OWNERPROJECT);
						searchCondition.addResultField(PPMFoundationObjectUtil.FROMTEMPLATE);
						searchCondition.addResultField(PPMFoundationObjectUtil.PARENTTASK);
						searchCondition.addResultField(PPMFoundationObjectUtil.PLANSTARTTIME);
						searchCondition.addResultField(PPMFoundationObjectUtil.PLANFINISHTIME);
						searchCondition.addResultField(PPMFoundationObjectUtil.ACTUALSTARTTIME);
						searchCondition.addResultField(PPMFoundationObjectUtil.ACTUALFINISHTIME);
						searchCondition.addResultField(PPMFoundationObjectUtil.ACTUALWORKLOAD);
						searchCondition.addResultField(PPMFoundationObjectUtil.COMPLETIONRATE);
						searchCondition.addResultField(PPMFoundationObjectUtil.SPI);

						taskObjectList = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
					}

					if (!SetUtils.isNullList(taskObjectList))
					{
						for (FoundationObject taskfoundation : taskObjectList)
						{
							EventApp eventApp = EventAppFactory.getEventAppFactory().getEventApp(warning.getEventType().name());
							if (eventApp != null)
							{
								try
								{
									eventApp.execute(this.stubService, projectFoundationObject, taskfoundation, warning);
								}
								catch (Exception e)
								{
									DynaLogger.error("task execute error", e);
								}
							}
						}
					}
				}
			}
		}

	}

	/**
	 * 在warning中取得指定的任务
	 * 
	 * @param warning
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> listAppointTask(EarlyWarning warning) throws ServiceRequestException
	{
		List<FoundationObject> value = null;
		if (warning != null)
		{
			if (!SetUtils.isNullList(warning.getApplyToTaskList()))
			{
				value = new ArrayList<FoundationObject>();
				for (ObjectGuid taskObject : warning.getApplyToTaskList())
				{
					if (!StringUtils.isNullString(taskObject.getGuid()) && !StringUtils.isNullString(taskObject.getClassGuid()))
					{
						FoundationObject taskFoun = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskObject.getObjectGuid(), false);
						if (taskFoun != null)
						{
							value.add(taskFoun);
						}
					}
				}
			}
		}
		return value;
	}
}