/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理任务分解
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.*;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainUpdateBean;
import dyna.common.bean.data.ppms.wbs.*;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DomainSyncModeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.ppms.*;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author WangLHB
 *         任务分解
 * 
 */
@Component
public class WBSStub extends AbstractServiceStub<PPMSImpl>
{

	@SuppressWarnings("rawtypes")
	public CodeItemInfo getCodeItemByEnum(Enum enumType) throws ServiceRequestException
	{
		if (enumType != null)
		{
			String codeName = null;
			String codeItemName = null;
			if (enumType instanceof ProjectStatusEnum)
			{
				codeName = CodeNameEnum.EXECUTESTATUS.getValue();
				codeItemName = ((ProjectStatusEnum) enumType).getValue();
			}
			else if (enumType instanceof TaskStatusEnum)
			{
				codeName = CodeNameEnum.EXECUTESTATUS.getValue();
				codeItemName = ((TaskStatusEnum) enumType).getValue();
			}
			else if (enumType instanceof TaskTypeEnum)
			{
				codeName = CodeNameEnum.TASKTYPE.getValue();
				codeItemName = ((TaskTypeEnum) enumType).getValue();
			}
			else if (enumType instanceof DurationUnitEnum)
			{
				codeName = CodeNameEnum.DURATIONUNIT.getValue();
				codeItemName = ((DurationUnitEnum) enumType).getValue();
			}
			else if (enumType instanceof TaskDependEnum)
			{
				codeName = CodeNameEnum.TASKDEPENDENUM.getValue();
				codeItemName = ((TaskDependEnum) enumType).name();
			}
			else if (enumType instanceof OperationStateEnum)
			{
				codeName = CodeNameEnum.OPERATIONSTATE.getValue();
				codeItemName = ((OperationStateEnum) enumType).getValue();
			}
			else if (enumType instanceof OnTimeStateEnum)
			{
				codeName = CodeNameEnum.ONTIMESTATE.getValue();
				codeItemName = ((OnTimeStateEnum) enumType).getValue();
			}

			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItemByName(codeName, codeItemName);
			if (codeItem == null)
			{
				new ServiceRequestException("ID_APP_PM_BUILTIN_CODEITEM_NOT_FOUND", "not found code item :" + codeName + "-" + codeItemName, null, codeName, codeItemName);
			}
			return codeItem;
		}
		return null;
	}

	protected FoundationObject getParentTask(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		FoundationObject obj = boas.getFoundationStub().getObject(projectObjectGuid, false);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(obj);
		if (util.getParentTask() != null && StringUtils.isNullString(util.getParentTask().getGuid()) == false)
		{
			return boas.getFoundationStub().getObject(util.getParentTask(), false);
		}
		return null;
	}

	public WBSDriver driveWbsContain(FoundationObject foundationObject, Date planDate, PMCalendar newPMCalendar, FoundationObject targetObj, String copyType)
			throws ServiceRequestException
	{
		WBSDriver driver = null;
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(targetObj);
		if (copyType.equalsIgnoreCase(PMConstans.COPY_TYPE_P2P))
		{
			driver = this.createWBSDriver(foundationObject, util.getExecutor());
			driver.getCalculateHandler().change(planDate, newPMCalendar, copyType);
		}
		else if (copyType.equalsIgnoreCase(PMConstans.COPY_TYPE_T2T))
		{
			driver = this.createWBSDriver(foundationObject, util.getExecutor());
		}
		else if (copyType.equalsIgnoreCase(PMConstans.COPY_TYPE_P2T))
		{
			driver = this.createWBSDriver(foundationObject, util.getExecutor());
			driver.getCalculateHandler().change(null, newPMCalendar, copyType);
		}
		else if (copyType.equalsIgnoreCase(PMConstans.COPY_TYPE_T2P))
		{
			driver = this.createWBSDriver(foundationObject, util.getExecutor());
			driver.getCalculateHandler().change(planDate, newPMCalendar, copyType);
		}

		// 时间推算不成功也可以复制；
		try
		{
			driver.getCalculateHandler().updateWBScode(foundationObject);
			driver.getCalculateHandler().updatePlanWorkload();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return driver;
	}

	public List<FoundationObject> listAllSubTask(ObjectGuid projectObjectGuid, boolean hasAllField) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		SearchCondition searchCondition = null;
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(projectObjectGuid.getObjectGuid().getClassGuid());
		ClassInfo tclassInfo = null;
		if (classInfo.hasInterface(ModelInterfaceEnum.IPMTask) || classInfo.getInterfaceList().contains(ModelInterfaceEnum.IPMProject))
		{
			tclassInfo = this.getPMTaskClassInfo(ModelInterfaceEnum.IPMTask);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMTaskTemplate) || classInfo.getInterfaceList().contains(ModelInterfaceEnum.IPMProjectTemplate))
		{
			tclassInfo = this.getPMTaskClassInfo(ModelInterfaceEnum.IPMTaskTemplate);
		}
		else
		{
			tclassInfo = this.getPMTaskClassInfo(ModelInterfaceEnum.IPMChangeTask);
		}
		searchCondition = SearchConditionFactory.createSearchCondition4Class(tclassInfo.getName(), null, false);
		if (hasAllField)
		{
			List<ClassField> fieldList = this.stubService.getEMM().listFieldOfClass(tclassInfo.getName());
			for (ClassField filed : fieldList)
			{
				searchCondition.addResultField(filed.getName());
			}
		}
		searchCondition.addFilter(PPMFoundationObjectUtil.OWNERPROJECT, projectObjectGuid, OperateSignEnum.EQUALS);
		searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		searchCondition.addOrder(SystemClassFieldEnum.ID, true);
		List<FoundationObject> list = new ArrayList<FoundationObject>();

		int pageIndex = 1;
		while (true)
		{
			searchCondition.setPageNum(pageIndex);
			List<FoundationObject> tempList = boas.getFoundationStub().listObject(searchCondition, false);
			if (SetUtils.isNullList(tempList))
			{
				break;
			}

			list.addAll(tempList);
			if (tempList.size() < SearchCondition.MAX_PAGE_SIZE)
			{
				break;
			}
			pageIndex++;
		}
		return list;
	}

	protected ClassInfo getPMTaskClassInfo(ModelInterfaceEnum interFace) throws ServiceRequestException
	{
		List<ClassInfo> boInfoList = this.stubService.getEMM().listClassByInterface(interFace);
		if (!SetUtils.isNullList(boInfoList))
		{
			return boInfoList.get(0);
		}
		else
		{

			throw new ServiceRequestException("ID_APP_PM_TASK_BOINFO_NOT_FOUND", "not found task boinfo");

		}
	}

	/**
	 * @param projectFoundationObject
	 * @param wbsNumber
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject getTaskFoundationObjectByWBSNumber(ObjectGuid projectObjectGuid, String wbsNumber) throws ServiceRequestException
	{
		if (projectObjectGuid != null && projectObjectGuid.getGuid() != null)
		{
			List<FoundationObject> listStubTask = this.listAllSubTask(projectObjectGuid, true);
			if (!SetUtils.isNullList(listStubTask))
			{
				for (FoundationObject task : listStubTask)
				{
					PPMFoundationObjectUtil ppmTask = new PPMFoundationObjectUtil(task);
					if (ppmTask.getWBSNumber() != null && ppmTask.getWBSNumber().equalsIgnoreCase(wbsNumber))
					{
						return task;
					}
				}
			}
		}
		return null;
	}

	public InstanceDomainUpdateBean getInstanceDomain(ObjectGuid rootObjectGuid) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		FoundationObject rootObject = boas.getFoundationStub().getObject(rootObjectGuid, false);
		InstanceDomainUpdateBean updateBean = new InstanceDomainUpdateBean(rootObject);

		// 任务
		List<FoundationObject> list = this.listAllSubTask(rootObjectGuid, true);
		if (SetUtils.isNullList(list) == false)
		{
			updateBean.setLoadMap(DataTypeEnum.TASK.name(), list);
		}

		List<TaskRelation> dependList = this.stubService.getTaskStub().listAllTaskRelation(rootObjectGuid);
		if (SetUtils.isNullList(dependList) == false)
		{
			updateBean.setLoadMap(DataTypeEnum.DEPEND.name(), dependList);
		}

		List<ProjectRole> listProjectRole = this.stubService.getRoleStub().listProjectRoleByProjectGuid(rootObjectGuid.getGuid());
		if (SetUtils.isNullList(listProjectRole) == false)
		{
			updateBean.setLoadMap(DataTypeEnum.ROLE.name(), listProjectRole);
		}

		List<CheckpointConfig> projectMileStone = this.stubService.getBaseConfigStub().listCheckpointConfigByMileStoneGuid(rootObjectGuid.getGuid());
		if (!SetUtils.isNullList(projectMileStone))
		{
			updateBean.setLoadMap(DataTypeEnum.MILESTONE.name(), projectMileStone);
		}

		// 取得项目交付物
		List<DeliverableItem> listAllDeliveryByProject = this.stubService.getDeliveryStub().listDeliveryItemByProject(rootObjectGuid.getGuid());
		if (!SetUtils.isNullList(listAllDeliveryByProject))
		{
			updateBean.setLoadMap(DataTypeEnum.DEVELIVE.name(), listAllDeliveryByProject);
		}

		// 预警
		List<EarlyWarning> listEarlyWarning = this.stubService.getWarningNoticeStub().listEarlyWarning(rootObjectGuid.getGuid());
		if (!SetUtils.isNullList(listEarlyWarning))
		{
			updateBean.setLoadMap(DataTypeEnum.WARN.name(), listEarlyWarning);
		}

		List<RoleMembers> roleMembers = new ArrayList<RoleMembers>();
		List<TaskMember> taskMembers = new ArrayList<TaskMember>();

		if (!SetUtils.isNullList(listProjectRole))
		{
			for (ProjectRole role : listProjectRole)
			{
				List<RoleMembers> listRoleMembers = this.stubService.getRoleStub().listRoleMembers(role.getGuid());
				if (listRoleMembers != null)
				{
					roleMembers.addAll(listRoleMembers);
				}
				if (!SetUtils.isNullList(listRoleMembers))
				{
					for (RoleMembers member : listRoleMembers)
					{
						User user = this.stubService.getAAS().getUser(member.getUserGuid());
						if (user != null)
						{
							member.setUser(user);
						}
					}
				}

				List<TaskMember> listTaskMember = this.stubService.getTaskMemberStub().listTaskMemberByRole(role.getGuid());
				if (listTaskMember != null)
				{
					taskMembers.addAll(listTaskMember);
				}

			}
		}

		updateBean.setLoadMap(DataTypeEnum.TEAM.name(), roleMembers);
		updateBean.setLoadMap(DataTypeEnum.RESOURCE.name(), taskMembers);

		updateBean.setMode(DomainSyncModeEnum.REPACLEALL);
		return updateBean;
	}

	public AbstractWBSDriver createWBSDriver(FoundationObject rootObj, String userGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = null;
		AbstractWBSDriver domainModel = null;
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(rootObj);
		if (classInfo == null)
		{
			classInfo = this.stubService.getEMM().getClassByGuid(rootObj.getObjectGuid().getClassGuid());
		}
		if (classInfo == null)
		{

			domainModel = new ProjectWBSDriver(rootObj, userGuid);
		}

		if (classInfo.hasInterface(ModelInterfaceEnum.IPMProjectTemplate))
		{
			domainModel = new ProjectTemplateWBSDriver(rootObj, userGuid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMProject))
		{
			domainModel = new ProjectWBSDriver(rootObj, userGuid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMTask))
		{
			FoundationObject project = this.stubService.getBOAS().getObject(util.getOwnerProject());
			domainModel = new ProjectWBSDriver(project, userGuid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMTaskTemplate))
		{
			FoundationObject project = this.stubService.getBOAS().getObject(util.getOwnerProject());
			domainModel = new ProjectTemplateWBSDriver(project, userGuid);
		}
		util.setFoundation(domainModel.getRootObject());
		WBSPrepareContain prepareContain = this.getWBSPrepareContain(util.getProjectCalendar());
		domainModel.setPrepareContain(prepareContain);

		InstanceDomainUpdateBean instanceDomain = this.stubService.getWBSStub().getInstanceDomain(rootObj.getObjectGuid());

		domainModel.syncInstanceDomain(instanceDomain);

		domainModel.clearNotifyObservers();

		return domainModel;
	}

	public WBSPrepareContain getWBSPrepareContain(String calendarGuid) throws ServiceRequestException
	{

		List<CodeItemInfo> taskTypeList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.TASKTYPE.getValue());
		List<CodeItemInfo> dependTypeList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.TASKDEPENDENUM.getValue());
		List<CodeItemInfo> durationUnitList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.DURATIONUNIT.getValue());
		List<CodeItemInfo> taskStatusList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.EXECUTESTATUS.getValue());
		List<CodeItemInfo> operateStatusList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.OPERATIONSTATE.getValue());
		List<CodeItemInfo> onTimeStatusList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.ONTIMESTATE.getValue());

		WBSPrepareContain prepareContain = new WBSPrepareContain(dependTypeList, durationUnitList, taskTypeList, taskStatusList, operateStatusList, onTimeStatusList);
		PMCalendar workCalendar = null;
		if (calendarGuid == null)
		{
			workCalendar = this.stubService.getCalendarStub().getStandardWorkCalendar();
		}
		else
		{
			workCalendar = this.stubService.getCalendarStub().getWorkCalendar(calendarGuid);
		}
		prepareContain.setProjectCalendar(workCalendar);
		LaborHourConfig workTimeConfig = this.stubService.getBaseConfigStub().getWorkTimeConfig();
		prepareContain.setWorkHourConfig(workTimeConfig);

		return prepareContain;

	}

	public InstanceDomainUpdateBean saveInstanceDomain(InstanceDomainUpdateBean bean) throws ServiceRequestException
	{
		Set<String> signSet = bean.getLoadSignSet();
		Map<String, String> newGuidMap = bean.getNewGuidMap();

		InstanceDomainUpdateBean newBean = new InstanceDomainUpdateBean(null);
		newBean.setNewGuidMap(newGuidMap);
		if (signSet == null)
		{
			return null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 项目
			if (bean.getRootObject() != null && bean.getRootObject().isChanged())
			{
				if (bean.getRootObject().isChanged(PPMFoundationObjectUtil.EXECUTOR))
				{
					this.stubService.getNoticeStub().sendChangeProjectManagerMail(bean.getRootObject());
				}
				newBean.setRootObject(this.stubService.getProjectStub().saveObject(bean.getRootObject(), true, true));
			}
			else
			{
				newBean.setRootObject(bean.getRootObject());
			}

			// 角色
			String type = DataTypeEnum.ROLE.name();
			List<DynaObject> newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						ProjectRole t = ((ProjectRole) o);
						this.stubService.getRoleStub().deleteProjectRole(t.getGuid());
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						ProjectRole t = ((ProjectRole) o);
						oldGuid = t.getGuid();
						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.getRoleStub().saveProjectRole(t, false);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.getRoleStub().saveProjectRole(t, false);
							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			// 团队
			type = DataTypeEnum.TEAM.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						RoleMembers t = ((RoleMembers) o);
						this.stubService.getRoleStub().deleteRoleMemberByGuid(t.getGuid());
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						RoleMembers t = ((RoleMembers) o);
						oldGuid = t.getGuid();

						// 替换项目角色guid
						if (!StringUtils.isGuid(t.getProjectRoleGuid()))
						{
							t.setProjectRoleGuid(newGuidMap.get(t.getProjectRoleGuid()));
						}

						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.getRoleStub().saveProjectRoleUser(t);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.getRoleStub().saveProjectRoleUser(t);
							newGuidMap.put(oldGuid, t.getGuid());

							User user = this.stubService.getAAS().getUser(t.getUserGuid());
							if (user != null)
							{
								t.setUser(user);
							}

							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
			// 任务
			type = DataTypeEnum.TASK.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						FoundationObject t = ((FoundationObject) o);
						((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteObject(t, false);
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);

				if (list != null)
				{
					Collections.sort(list, new TaskCompareByWBSCode());
					String oldGuid = null;
					for (DynaObject o : list)
					{
						FoundationObject t = ((FoundationObject) o);
						oldGuid = t.getGuid();

						// 替换项目角色guid
						util.setFoundation(t);
						if (!StringUtils.isNullString(util.getExecuteRole()) && !StringUtils.isGuid(util.getExecuteRole()))
						{
							util.setExecuteRole(newGuidMap.get(util.getExecuteRole()));
						}

						// 替换parent任务guid
						ObjectGuid taskObjectGuid = util.getParentTask();
						if (!StringUtils.isGuid(util.getParentTask().getGuid()))
						{
							taskObjectGuid.setGuid(newGuidMap.get(taskObjectGuid.getGuid()));
						}
						util.setParentTask(taskObjectGuid);

						// 取得id
						NumberingModel idNumberingModel = this.stubService.getEMM().lookupNumberingModel(t.getObjectGuid().getClassGuid(), "", SystemClassFieldEnum.ID.getName());
						if (idNumberingModel != null)
						{
							if ((SetUtils.isNullList(idNumberingModel.getNumberingObjectList()) || idNumberingModel.getNumberingObjectList().size() != 1) == false)
							{
								if (PPMFoundationObjectUtil.WBSNUMBER.equalsIgnoreCase(idNumberingModel.getNumberingObjectList().get(0).getTypevalue()))
								{
									t.setId(util.getWBSNumber());
								}
							}
						}
						// 把对象的owneruser和ownergroup设置为和executor一致
						String executor = util.getExecutor();
						if (StringUtils.isGuid(executor))
						{
							User user = this.stubService.getAAS().getUser(executor);
							if (user != null)
							{
								String ownerGroup = user.getDefaultGroupGuid();
								if (StringUtils.isGuid(ownerGroup))
								{
									t.setOwnerGroupGuid(ownerGroup);
									t.setOwnerUserGuid(executor);
								}
							}
						}

						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.saveObject(t, false, false);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject((FoundationObject) o, null, false, false);

							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			// 依赖
			type = DataTypeEnum.DEPEND.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						TaskRelation t = ((TaskRelation) o);
						this.stubService.getTaskStub().deleteTaskRelation(t.getGuid());
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						TaskRelation t = ((TaskRelation) o);
						oldGuid = t.getGuid();
						// 替换任务guid
						ObjectGuid taskObjectGuid = t.getPreTaskObjectGuid();
						if (!StringUtils.isGuid(t.getPreTaskObjectGuid().getGuid()))
						{
							taskObjectGuid.setGuid(newGuidMap.get(taskObjectGuid.getGuid()));
						}
						t.setpreTaskObjectGuid(taskObjectGuid);
						taskObjectGuid = t.getTaskObjectGuid();
						if (!StringUtils.isGuid(t.getTaskObjectGuid().getGuid()))
						{
							taskObjectGuid.setGuid(newGuidMap.get(taskObjectGuid.getGuid()));
						}
						t.settaskObjectGuid(taskObjectGuid);
						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.saveTaskRelation(t);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.saveTaskRelation(t);
							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			// 资源
			type = DataTypeEnum.RESOURCE.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						TaskMember t = ((TaskMember) o);
						this.stubService.getTaskMemberStub().removeTaskMember(t.getGuid());
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						TaskMember t = ((TaskMember) o);
						oldGuid = t.getGuid();

						// 替换项目角色guid,
						if (!StringUtils.isGuid(t.getProjectRole()))
						{
							t.setProjectRole(newGuidMap.get(t.getProjectRole()));
						}
						// 替换任务guid
						ObjectGuid taskObjectGuid = t.getTaskObjectGuid();
						if (!StringUtils.isGuid(t.getTaskObjectGuid().getGuid()))
						{
							taskObjectGuid.setGuid(newGuidMap.get(taskObjectGuid.getGuid()));
						}
						t.setTaskObjectGuid(taskObjectGuid);

						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.getTaskMemberStub().saveTaskMember(t);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.getTaskMemberStub().saveTaskMember(t);
							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			// 里程碑
			type = DataTypeEnum.MILESTONE.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						CheckpointConfig t = ((CheckpointConfig) o);
						this.stubService.getBaseConfigStub().delCheckpointConfig(t.getGuid());
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						CheckpointConfig t = ((CheckpointConfig) o);
						oldGuid = t.getGuid();

						// 替换任务guid
						if (!StringUtils.isGuid(t.getRelatedTaskObject().getGuid()))
						{
							ObjectGuid taskObjectGuid = t.getRelatedTaskObject();
							taskObjectGuid.setGuid(newGuidMap.get(taskObjectGuid.getGuid()));
							t.setRelatedTaskObject(taskObjectGuid);
						}
						else
						{
							ObjectGuid taskObjectGuid = t.getRelatedTaskObject();
							t.setRelatedTaskObject(taskObjectGuid);

						}

						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.getBaseConfigStub().saveCheckpointConfig(t);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.getBaseConfigStub().saveCheckpointConfig(t);

							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			// 交付项
			type = DataTypeEnum.DEVELIVE.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						DeliverableItem t = ((DeliverableItem) o);
						this.stubService.getDeliveryStub().deleteDeliveryItem(t.getGuid());
					}

				}
				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						DeliverableItem t = ((DeliverableItem) o);
						oldGuid = t.getGuid();
						// 替换任务guid
						if (!StringUtils.isGuid(t.getTaskGuid()))
						{
							t.setTaskGuid(newGuidMap.get(t.getTaskGuid()));
						}

						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.getDeliveryStub().saveDeliveryItem(t);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.getDeliveryStub().saveDeliveryItem(t);
							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			// 预警
			type = DataTypeEnum.WARN.name();
			newList = new ArrayList<DynaObject>();
			if (signSet.contains(type))
			{
				List<DynaObject> l = bean.getDeleteMap(type);
				if (l != null)
				{
					for (DynaObject o : l)
					{
						EarlyWarning t = ((EarlyWarning) o);
						this.stubService.getWarningNoticeStub().deleteEarlyWarning(t.getGuid());
					}

				}

				List<DynaObject> list = bean.getLoadMap(type);
				if (list != null)
				{
					String oldGuid = null;
					for (DynaObject o : list)
					{
						EarlyWarning t = ((EarlyWarning) o);

						// 替换任务guid
						if (!SetUtils.isNullList(t.getApplyToTaskList()))
						{
							for (ObjectGuid g : t.getApplyToTaskList())
							{
								g.setGuid(newGuidMap.get(g.getGuid()));
							}
						}

						oldGuid = t.getGuid();
						if (StringUtils.isGuid(oldGuid))
						{
							t = this.stubService.getWarningNoticeStub().saveEarlyWarning(t);
							newList.add(t);
						}
						else
						{
							t.setGuid(null);
							t = this.stubService.getWarningNoticeStub().saveEarlyWarning(t);
							newGuidMap.put(oldGuid, t.getGuid());
							newList.add(t);
						}
					}
				}
			}
			newBean.setLoadMap(type, newList);

			newBean.setMode(DomainSyncModeEnum.CHANGE);
//			DataServer.getTransactionManager().commitTransaction();

		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		return newBean;
	}
}
