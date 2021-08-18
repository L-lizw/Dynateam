/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessTemplateStub
 * zhanghj 2011-6-9
 */
package dyna.app.service.brs.wfm;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.aas.*;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.dto.wf.ApproveTemplate;
import dyna.common.dto.wf.ApproveTemplateDetail;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghj
 * 
 */
@Component
public class ApproveTemplateStub extends AbstractServiceStub<WFMImpl>
{

	protected void batchSaveApproveTemplate(List<ApproveTemplate> addApproveTemplateList, List<ApproveTemplate> updateApproveTemplateList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// add
			if (!SetUtils.isNullList(addApproveTemplateList))
			{
				for (ApproveTemplate approveTemplate : addApproveTemplateList)
				{
					String guid = this.saveProcessTemplate(approveTemplate);
					if (guid != null)
					{
						List<ApproveTemplateDetail> approveTemplateDetailList = approveTemplate.listProcessTemplateDetail();
						for (ApproveTemplateDetail detail : approveTemplateDetailList)
						{
							detail.setTemplateGuid(guid);
							sds.save(detail);
						}
					}
				}
			}

			// update
			if (!SetUtils.isNullList(updateApproveTemplateList))
			{
				for (ApproveTemplate approveTemplate : updateApproveTemplateList)
				{
					this.updateProcessTemplate(approveTemplate);
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
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
	}

	protected void updateProcessTemplate(ApproveTemplate approveTemplate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UserSignature loginUser = this.stubService.getUserSignature();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, approveTemplate.getGuid());
			filter.put(ApproveTemplate.PROCRT_NAME, approveTemplate.getProcrtName());
			filter.put(SystemObject.NAME, approveTemplate.getName());
			filter.put(SystemObject.UPDATE_USER_GUID, loginUser.getUserGuid());
			sds.update(ApproveTemplate.class, filter, "update");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected String saveProcessTemplate(ApproveTemplate approveTemplate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			return sds.save(approveTemplate);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void batchDeleteApproveTemplate(List<ApproveTemplate> deleteApproveTemlateList) throws ServiceRequestException
	{

		// String sessionId = this.stubService.getSignature().getCredential();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!SetUtils.isNullList(deleteApproveTemlateList))
			{
				for (ApproveTemplate approveTemplate : deleteApproveTemlateList)
				{
					this.deleteApproveTemplate(approveTemplate.getGuid());
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
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

	}

	protected void deleteApproveTemplate(String tempGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, tempGuid);
			sds.delete(ApproveTemplate.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ApproveTemplate> listProcessTemplate(String procName, String perfGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ApproveTemplate.PROCRT_NAME, procName);
			filter.put(SystemObject.CREATE_USER_GUID, perfGuid);
			return sds.query(ApproveTemplate.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ApproveTemplateDetail> listProcessTemplateDetail(String tempGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ApproveTemplateDetail.MASTER_FK, tempGuid);
			List<ApproveTemplateDetail> resultList = sds.query(ApproveTemplateDetail.class, filter);
			return this.addPerfName(resultList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ApproveTemplateDetail> listProcessTemplateDetail(String tempGuid, String actName) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ApproveTemplateDetail.MASTER_FK, tempGuid);
			filter.put(ApproveTemplateDetail.ACTRT_NAME, actName);
			List<ApproveTemplateDetail> resultList = sds.query(ApproveTemplateDetail.class, filter);
			return this.addPerfName(resultList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected boolean canApplyExecutorTemplate(String processGuid, String executorTemplateGuid) throws ServiceRequestException
	{
		try
		{
			ProcessRuntime processRuntime = this.stubService.getWFI().getProcessRuntime(processGuid);

			String processName = processRuntime.getName();

			SystemDataService sds = DataServer.getSystemDataService();

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ApproveTemplate.GUID, executorTemplateGuid);
			filter.put("PROCRTNAME", processName);

			List<ApproveTemplateDetail> templateDetailList = sds.query(ApproveTemplateDetail.class, filter, "selectDetailWithMaster");

			if (!SetUtils.isNullList(templateDetailList))
			{
				for (ApproveTemplateDetail templateDetail : templateDetailList)
				{
					List<WorkflowTemplateActPerformerInfo> saadminList = new ArrayList<WorkflowTemplateActPerformerInfo>();
					String actrtName = templateDetail.getActrtName();

					// wf_template_actrt_execscope
					UpperKeyMap keyMap4execScope = new UpperKeyMap();
					keyMap4execScope.put(WorkflowTemplateActInfo.PERSCOPETYPE, "2");
					keyMap4execScope.put(WorkflowTemplateActInfo.TEMPLATEGUID, processRuntime.getWFTemplateGuid());
					keyMap4execScope.put(WorkflowTemplateActInfo.ACTRTNAME, actrtName);
					List<WorkflowTemplateActInfo> wfTemplateActList = sds.listFromCache(WorkflowTemplateActInfo.class,
							new FieldValueEqualsFilter<WorkflowTemplateActInfo>(keyMap4execScope));
					if (!SetUtils.isNullList(wfTemplateActList))
					{
						for (WorkflowTemplateActInfo wfTemplateAct : wfTemplateActList)
						{
							UpperKeyMap param = new UpperKeyMap();
							param.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECSCOPE);
							param.put(WorkflowTemplateActPerformerInfo.TEMPLATEACTRTGUID, wfTemplateAct.getGuid());
							List<WorkflowTemplateActPerformerInfo> execScopeList = sds.listFromCache(WorkflowTemplateActPerformerInfo.class,
									new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(param));
							if (!SetUtils.isNullList(execScopeList))
							{
								for (WorkflowTemplateActPerformerInfo performer : execScopeList)
								{
									saadminList.add(performer);
								}

							}
						}
					}

					// wf_template_actrt_executor
					keyMap4execScope.put(WorkflowTemplateActInfo.PERSCOPETYPE, "1");
					List<WorkflowTemplateActInfo> wfTemplateActList4Executor = sds.listFromCache(WorkflowTemplateActInfo.class,
							new FieldValueEqualsFilter<WorkflowTemplateActInfo>(keyMap4execScope));
					if (!SetUtils.isNullList(wfTemplateActList4Executor))
					{
						for (WorkflowTemplateActInfo wfTemplateAct : wfTemplateActList4Executor)
						{
							UpperKeyMap param = new UpperKeyMap();
							param.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECUTOR);
							param.put(WorkflowTemplateActPerformerInfo.TEMPLATEACTRTGUID, wfTemplateAct.getGuid());
							List<WorkflowTemplateActPerformerInfo> executorList = sds.listFromCache(WorkflowTemplateActPerformerInfo.class,
									new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(param));
							if (!SetUtils.isNullList(executorList))
							{
								for (WorkflowTemplateActPerformerInfo performer : executorList)
								{
									saadminList.add(performer);
								}
							}
						}
					}

					List<String> performerResult = new ArrayList<String>();
					if (SetUtils.isNullList(saadminList))
					{
						continue;
					}

					for (WorkflowTemplateActPerformerInfo saadmin : saadminList)
					{
						List<String> performerTempList = getUserGuidByType(saadmin.getPerfGuid(), saadmin.getPerfType());
						if (!SetUtils.isNullList(performerTempList))
						{
							performerResult.addAll(performerTempList);
						}
					}

					List<String> normalResult = getUserGuidByType(templateDetail.getPerfGuid(), templateDetail.getPerformerType());

					if (!SetUtils.isNullList(normalResult))
					{
						for (String userGuid : normalResult)
						{
							User user = sds.getObject(User.class, userGuid);
							if (user != null && user.isActive() && !performerResult.contains(userGuid))
							{
								return false;
							}

						}
					}

				}
			}

			return true;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	private List<String> getUserGuidByType(String perfGuid, PerformerTypeEnum perfType)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		List<String> userGuidNormal = new ArrayList<String>();
		// PerformerTypeEnum perfType = templateDetail.getPerformerType();
		switch (perfType)
		{
		case USER:
			String userGuid = perfGuid;
			if (!StringUtils.isNullString(userGuid))
			{
				User user = sds.getObject(User.class, userGuid);
				if (user != null && user.isActive())
				{
					userGuidNormal.add(userGuid);
				}
			}
			break;
		case GROUP:
			String parentGroupGuid = perfGuid;
			List<String> groupGuidList = this.getChildGroup(parentGroupGuid);
			if (!SetUtils.isNullList(groupGuidList))
			{
				for (String groupGuid : groupGuidList)
				{
					UpperKeyMap keyMap = new UpperKeyMap();
					keyMap.put(RIG.IS_VALID, "Y");
					keyMap.put(RIG.GROUP_GUID, groupGuid);
					List<RIG> rigList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(keyMap));
					if (!SetUtils.isNullList(rigList))
					{
						for (RIG rig : rigList)
						{
							UpperKeyMap keyMap4Urig = new UpperKeyMap();
							keyMap4Urig.put(URIG.IS_VALID, "Y");
							keyMap4Urig.put(URIG.ROLE_GROUP_GUID, rig.getGuid());
							List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(keyMap4Urig));
							if (!SetUtils.isNullList(urigList))
							{
								for (URIG urig : urigList)
								{
									userGuidNormal.add(urig.getUserGuid());
								}
							}
						}
					}
				}
			}
			break;
		case RIG:
			String rigGuid = perfGuid;

			UpperKeyMap keyMap = new UpperKeyMap();
			keyMap.put(URIG.IS_VALID, "Y");
			keyMap.put(URIG.ROLE_GROUP_GUID, rigGuid);
			List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(keyMap));
			if (!SetUtils.isNullList(urigList))
			{
				for (URIG urig : urigList)
				{
					if (urig.getUserGuid() != null)
					{
						userGuidNormal.add(urig.getUserGuid());
					}
				}
			}
			break;
		case ROLE:
			String roleGuid = perfGuid;

			UpperKeyMap keyMap4Role = new UpperKeyMap();
			keyMap4Role.put(Role.IS_VALID, "Y");
			keyMap4Role.put(Role.GUID, roleGuid);
			List<Role> roleList = sds.listFromCache(Role.class, new FieldValueEqualsFilter<Role>(keyMap4Role));
			if (!SetUtils.isNullList(roleList))
			{
				UpperKeyMap keyMap4RoleUrig = new UpperKeyMap();
				keyMap4RoleUrig.put(URIG.IS_VALID, "Y");
				keyMap4RoleUrig.put(URIG.ROLE_GROUP_GUID, roleGuid);
				List<URIG> urig4RoleList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(keyMap4RoleUrig));
				if (!SetUtils.isNullList(urig4RoleList))
				{
					for (URIG urig : urig4RoleList)
					{
						if (urig.getUserGuid() != null)
						{
							userGuidNormal.add(urig.getUserGuid());
						}
					}
				}
			}
			break;

		default:
			break;
		}
		return userGuidNormal;
	}

	/**
	 * 结果自己所有的后代节点组（包括自己）
	 * 
	 * @return
	 */
	private List<String> getChildGroup(String parentGroupGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> allGroup = new ArrayList<String>();
		if (!StringUtils.isNullString(parentGroupGuid))
		{
			Group parentGroup = sds.get(Group.class, parentGroupGuid);
			if (parentGroup != null && parentGroup.isValid())
			{
				allGroup.add(parentGroupGuid);

				UpperKeyMap keyMap = new UpperKeyMap();
				keyMap.put(Group.IS_VALID, "Y");
				keyMap.put(Group.PARENT_GUID, parentGroupGuid);
				List<Group> childGroupList = sds.listFromCache(Group.class, new FieldValueEqualsFilter<Group>(keyMap));
				if (!SetUtils.isNullList(childGroupList))
				{
					for (Group childGroup : childGroupList)
					{
						List<String> childGroupGuidList = getChildGroup(childGroup.getGuid());
						if (!SetUtils.isNullList(childGroupGuidList))
						{
							allGroup.addAll(childGroupGuidList);
						}
					}
				}
			}
		}
		return allGroup;
	}

	private List<ApproveTemplateDetail> addPerfName(List<ApproveTemplateDetail> resultList)
	{
		if (!SetUtils.isNullList(resultList))
		{
			for (ApproveTemplateDetail approveTemplateDetail : resultList)
			{
				PerformerTypeEnum perfType = approveTemplateDetail.getPerformerType();
				String perfName = getPerfName(perfType, approveTemplateDetail.getPerfGuid());
				approveTemplateDetail.setPerfName(perfName);
			}
		}
		return resultList;
	}

	private String getPerfName(PerformerTypeEnum perfType, String perfguid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String perfName = "";
		if (perfType != null)
		{
			switch (perfType)
			{
			case USER:
				perfName = sds.getObject(User.class, perfguid).getName();
				break;
			case RIG:
				RIG rig = sds.getObject(RIG.class, perfguid);
				String groupGuid = rig.getGroupGuid();
				String roleGuid = rig.getRoleGuid();
				if (!StringUtils.isNullString(groupGuid) && !StringUtils.isNullString(roleGuid))
				{
					String groupName = sds.getObject(Group.class, groupGuid).getName();
					String roleName = sds.getObject(Role.class, roleGuid).getName();
					perfName = groupName + "-" + roleName;
				}
				break;
			case ROLE:
				perfName = sds.getObject(Role.class, perfguid).getName();
				break;
			case GROUP:
				perfName = sds.getObject(Group.class, perfguid).getName();
				break;
			default:
				break;
			}
		}
		return perfName;
	}

}
