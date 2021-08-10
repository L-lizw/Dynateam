/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 执行人操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.performer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.extra.ProcessSetting;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.bean.model.wf.template.WorkflowTemplateVo;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.URIG;
import dyna.common.dto.aas.User;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.Performer;
import dyna.common.dto.wf.ProcTrack;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.AAS;

/**
 * 执行人操作分支
 * 
 * @author Wanglei
 * 
 */
public class PerformerStub extends AbstractServiceStub<WFIImpl>
{

	private PerformerDBStub dbStub = null;

	/**
	 * @param context
	 * @param service
	 */
	public PerformerStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.dbStub = new PerformerDBStub(context, service);
	}

	public boolean performed(String procRtGuid, String actRtGuid, String performerGuid) throws ServiceRequestException
	{
		try
		{
			List<String> list = this.getNotFinishedPrincipalsOfAgent(performerGuid, actRtGuid);
			if (!SetUtils.isNullList(list))
			{
				for (String userGuid : list)
				{
					String result = this.setPerformer(userGuid, procRtGuid, actRtGuid);
					if ("Y".equalsIgnoreCase(result))
					{
						continue;
					}
					return false;
				}
			}

			String result = this.setPerformer(performerGuid, procRtGuid, actRtGuid);
			if (!"Y".equalsIgnoreCase(result))
			{
				return false;
			}
			return true;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private String setPerformer(String userGuid, String procrtGuid, String actrtGuid) throws ServiceRequestException
	{

		this.dbStub.saveActualPerformer(procrtGuid, actrtGuid, userGuid);

		ActivityRuntime actrtRuntime = this.stubService.getActivityRuntime(actrtGuid);
		if (actrtRuntime != null)
		{
			String actrtName = actrtRuntime.getActrtName();

			List<Performer> performerList = this.stubService.listPerformer(actrtGuid);
			if (!SetUtils.isNullList(performerList))
			{
				ProcessRuntime procrtRuntime = this.stubService.getProcessRuntime(procrtGuid);
				String wfTemplateGuid = procrtRuntime.getWFTemplateGuid();

				boolean isAllPerformer = this.isTemplateAllPerformer(actrtName, wfTemplateGuid);
				if (isAllPerformer)
				{
					for (Performer performer_ : performerList)
					{
						boolean isPerformed = false;
						switch (performer_.getPerformerType())
						{
						case USER:
							// 当用执行角色为用户时，该用户完成则完成
							isPerformed = this.dbStub.isUserPerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						case GROUP:
							// 当用执行角色为组时，组下的所有用户均完成，才算完成
							isPerformed = this.isAllUserInGroupPerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						case RIG:
							// 当用执行角色为组角色时，组角色下的所有用户均完成，才算完成
							isPerformed = this.isAllUserInRIGPerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						case ROLE:
							// 当用执行角色为角色时，角色下的所有用户均完成，才算完成
							isPerformed = this.isAllUserInRolePerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						}

						if (isPerformed)
						{
							this.dbStub.setPerformerFinished(performer_.getGuid());
						}
					}
				}
				else
				{
					for (Performer performer_ : performerList)
					{
						boolean isPerformed = false;
						switch (performer_.getPerformerType())
						{
						case USER:
							// 当用执行角色为用户时，该用户完成则完成
							isPerformed = this.dbStub.isUserPerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						case GROUP:
							// 组下的用户只要有一个完成即完成
							isPerformed = this.isExistUserInGroupPerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						case RIG:
							// 组角色下的用户有一个完成即完成
							isPerformed = this.isExistUserInRIGPerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						case ROLE:
							// 角色下的用户有一个完成即完成
							isPerformed = this.isExistUserInRolePerformed(performer_.getPerformerGuid(), actrtGuid);
							break;
						}

						if (isPerformed)
						{
							this.dbStub.setPerformerFinished(performer_.getGuid());
						}
					}
				}
			}
		}
		return "Y";
	}

	private boolean isExistUserInRolePerformed(String roleGuid, String actrtGuid) throws ServiceRequestException
	{

		List<Performer> actualPerformerList = this.dbStub.selectActPerformerActual(actrtGuid);

		// 取得角色所属的所有组角色
		List<RIG> rigList = this.stubService.getAAS().listRIGByRoleGuid(roleGuid);
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				// 取得当前设置的用户组角色下的所有用户
				List<User> userInGroupList = this.stubService.getAAS().listUserByRoleInGroup(rig.getGuid());

				// 判断是否所有用户均已完成
				if (!SetUtils.isNullList(userInGroupList))
				{
					for (User user : userInGroupList)
					{
						boolean isUserPerformed = this.isUserPerformed(user, actualPerformerList);
						if (isUserPerformed)
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean isAllUserInRolePerformed(String roleGuid, String actrtGuid) throws ServiceRequestException
	{

		List<Performer> actualPerformerList = this.dbStub.selectActPerformerActual(actrtGuid);

		// 取得角色所属的所有组角色
		List<RIG> rigList = this.stubService.getAAS().listRIGByRoleGuid(roleGuid);
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				// 取得当前设置的用户组角色下的所有用户
				List<User> userInGroupList = this.stubService.getAAS().listUserByRoleInGroup(rig.getGuid());

				// 判断是否所有用户均已完成
				if (!SetUtils.isNullList(userInGroupList))
				{
					for (User user : userInGroupList)
					{
						boolean isUserPerformed = this.isUserPerformed(user, actualPerformerList);
						if (!isUserPerformed)
						{
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private boolean isExistUserInRIGPerformed(String rigGuid, String actrtGuid) throws ServiceRequestException
	{

		List<Performer> actualPerformerList = this.dbStub.selectActPerformerActual(actrtGuid);

		// 取得当前设置的用户组角色下的所有用户
		List<User> userInGroupList = this.stubService.getAAS().listUserByRoleInGroup(rigGuid);

		// 判断是否所有用户均已完成
		if (!SetUtils.isNullList(userInGroupList))
		{
			for (User user : userInGroupList)
			{
				boolean isUserPerformed = this.isUserPerformed(user, actualPerformerList);
				if (isUserPerformed)
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAllUserInRIGPerformed(String rigGuid, String actrtGuid) throws ServiceRequestException
	{

		List<Performer> actualPerformerList = this.dbStub.selectActPerformerActual(actrtGuid);

		// 取得当前设置的用户组角色下的所有用户
		List<User> userInGroupList = this.stubService.getAAS().listUserByRoleInGroup(rigGuid);

		// 判断是否所有用户均已完成
		if (!SetUtils.isNullList(userInGroupList))
		{
			for (User user : userInGroupList)
			{
				boolean isUserPerformed = this.isUserPerformed(user, actualPerformerList);
				if (!isUserPerformed)
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean isAllUserInGroupPerformed(String groupGuid, String actrtGuid) throws ServiceRequestException
	{

		List<Performer> actualPerformerList = this.dbStub.selectActPerformerActual(actrtGuid);

		// 取得当前设置的用户组下的所有用户
		List<User> userInGroupList = this.stubService.getAAS().listUserInGroup(groupGuid);

		// 判断是否所有用户均已完成
		if (!SetUtils.isNullList(userInGroupList))
		{
			for (User user : userInGroupList)
			{
				boolean isUserPerformed = this.isUserPerformed(user, actualPerformerList);
				if (!isUserPerformed)
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean isExistUserInGroupPerformed(String groupGuid, String actrtGuid) throws ServiceRequestException
	{

		List<Performer> actualPerformerList = this.dbStub.selectActPerformerActual(actrtGuid);

		// 取得当前设置的用户组下的所有用户
		List<User> userInGroupList = this.stubService.getAAS().listUserInGroup(groupGuid);

		// 判断是否有用户完成,只要有一个完成即完成
		if (!SetUtils.isNullList(userInGroupList))
		{
			for (User user : userInGroupList)
			{
				boolean isUserPerformed = this.isUserPerformed(user, actualPerformerList);
				if (isUserPerformed)
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean isUserPerformed(User user, List<Performer> actualPerformerList)
	{
		if (!SetUtils.isNullList(actualPerformerList))
		{
			for (Performer performer : actualPerformerList)
			{
				if (user.getGuid().equals(performer.getPerformerGuid()))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean isTemplateAllPerformer(String actrtName, String wfTemplateGuid) throws ServiceRequestException
	{
		boolean isAllPerformer = true;

		WorkflowTemplateAct actrt = this.stubService.getTemplateStub().getWorkflowTemplateActSet(wfTemplateGuid, actrtName);
		if (actrt != null)
		{
			isAllPerformer = "1".equals(actrt.getWorkflowTemplateActInfo().getExecutionType());
		}
		else
		{
			isAllPerformer = true;
		}

		return isAllPerformer;
	}

	@Deprecated
	protected void viewed(String procRtGuid, String actRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			ActivityRuntime activityRuntime = this.stubService.getActivityRuntime(actRtGuid);
			if (activityRuntime == null)
			{
				return;
			}
			String operatorGuid = this.stubService.getOperatorGuid();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.PROCRT_GUID, procRtGuid);
			filter.put(Performer.ACTRT_GUID, actRtGuid);
			filter.put(Performer.PERF_GUID, operatorGuid);

			if (activityRuntime.isFinished())
			{
				// do finish
				filter.put(Performer.ACTUAL_PERF_GUID, operatorGuid);
			}

			sds.update(Performer.class, filter, "viewed");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void resetPerformersOfProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.PROCRT_GUID, procRtGuid);
			filter.put(Performer.IS_FINISH, "Y");

			List<Performer> perfFinishList = sds.query(Performer.class, filter);
			this.resetPerformers(perfFinishList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void resetPerformersOfActivityRutime(String actRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.ACTRT_GUID, actRtGuid);
			filter.put(Performer.IS_FINISH, "Y");

			List<Performer> perfFinishList = sds.query(Performer.class, filter);

			this.resetPerformers(perfFinishList);
			// 删除实际执行人
			Map<String, Object> deleteActualPerf = new HashMap<String, Object>();
			deleteActualPerf.put(Performer.ACTRT_GUID, actRtGuid);
			sds.delete(Performer.class, deleteActualPerf, "deleteperformeractualActrt");

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void resetPerformers(List<Performer> perfFinishList)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (!SetUtils.isNullList(perfFinishList))
		{
			for (Performer perf : perfFinishList)
			{
				// 删除执行人后重新添加执行人
				Map<String, Object> deleteFilter = new HashMap<String, Object>();
				deleteFilter.put(SystemObject.GUID, perf.getGuid());
				sds.delete(Performer.class, deleteFilter, "delete");

				perf.setGuid(null);
				perf.setActualPerformerGuid(null);
				perf.setFinished(false);
				perf.setDeleted(false);
				perf.clearViewTime();
				sds.save(perf);
			}
		}
	}

	public List<Performer> listPerformer(String actRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.ACTRT_GUID, actRtGuid);
			List<Performer> perfList = sds.query(Performer.class, filter, "select");
			this.decoratePerformer4List(perfList);
			return perfList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 
	 * 查询未完成此活动的执行人(前提：此活动未完成，如果完成，返回为空)
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> listNotFinishPerformer(String actRtGuid) throws ServiceRequestException
	{
		ActivityRuntime activity = this.stubService.getActivityRuntime(actRtGuid);

		if (activity == null)
		{
			DynaLogger.info("activity is null");
			return null;
		}
		if (activity.isFinished())
		{
			DynaLogger.info("actvity is finish");
			return null;
		}

		// if (isAllPerformer)
		// {
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<User> userList = new ArrayList<User>();

			Performer performer = new Performer();
			performer.setActRuntimeGuid(actRtGuid);
			performer.setFinished(false);
			List<Performer> unfinishPerforList = sds.query(Performer.class, performer);

			if (!SetUtils.isNullList(unfinishPerforList))
			{
				for (Performer p : unfinishPerforList)
				{
					this.addUser(userList, p);
				}
			}

			if (!SetUtils.isNullList(userList))
			{
				List<Performer> actualPerforList = this.dbStub.selectActPerformerActual(actRtGuid);
				if (SetUtils.isNullList(actualPerforList))
				{
					return userList;
				}

				Iterator<User> iterator = userList.iterator();
				while (iterator.hasNext())
				{
					User u = iterator.next();
					if (!this.isFinshPerformer(u, actualPerforList))
					{
						iterator.remove();
					}
				}

				return userList;
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private boolean isFinshPerformer(User user, List<Performer> actualPerforList)
	{
		for (Performer p : actualPerforList)
		{
			if (user.getGuid().equals(p.getPerformerGuid()))
			{
				return true;
			}
		}
		return true;
	}

	private void addUser(List<User> userList, Performer p) throws ServiceRequestException
	{
		if (p.getPerformerType() == PerformerTypeEnum.USER)
		{
			User user = this.stubService.getAAS().getUser(p.getPerformerGuid());
			if (user != null)
			{
				userList.add(user);
			}
		}
		else if (p.getPerformerType() == PerformerTypeEnum.GROUP)
		{
			List<User> userList_ = this.stubService.getAAS().listUserInGroupAndSubGroup(p.getPerformerGuid());
			if (!SetUtils.isNullList(userList_))
			{
				userList.addAll(userList_);
			}
		}
		else if (p.getPerformerType() == PerformerTypeEnum.RIG)
		{
			List<User> userList_ = this.stubService.getAAS().listUserByRoleInGroup(p.getPerformerGuid());
			if (!SetUtils.isNullList(userList_))
			{
				userList.addAll(userList_);
			}
		}
		else if (p.getPerformerType() == PerformerTypeEnum.ROLE)
		{
			List<RIG> rigList = this.stubService.getAAS().listRIGByRoleGuid(p.getPerformerGuid());
			if (!SetUtils.isNullList(rigList))
			{
				for (RIG rig : rigList)
				{
					List<User> userList_ = this.stubService.getAAS().listUserByRoleInGroup(rig.getGuid());
					if (!SetUtils.isNullList(userList_))
					{
						for (User user : userList_)
						{
							if (!userList.contains(user))
							{
								userList.add(user);
							}
						}
					}
				}
			}
		}
	}

	public boolean isPerformerOfProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		AAS aas = this.stubService.getAAS();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.PROCRT_GUID, procRtGuid);

			List<Performer> perfList = sds.query(Performer.class, filter, "select");

			if (!SetUtils.isNullList(perfList))
			{
				for (Performer performer : perfList)
				{
					if (PerformerTypeEnum.USER == performer.getPerformerType())
					{
						boolean isAgent = this.stubService.getAAS().isAgent(operatorGuid, performer.getPerformerGuid());
						if (operatorGuid.equals(performer.getPerformerGuid()) || isAgent)
						{
							return true;
						}
					}
					else
					{
						List<User> listUser = null;
						if (performer.getPerformerType() == PerformerTypeEnum.RIG)
						{
							listUser = aas.listUserByRoleInGroup(performer.getPerformerGuid());

						}
						else if (performer.getPerformerType() == PerformerTypeEnum.GROUP)
						{
							listUser = aas.listUserInGroupAndSubGroup(performer.getPerformerGuid());
						}
						if (!SetUtils.isNullList(listUser))
						{
							for (User user : listUser)
							{
								if (operatorGuid.equals(user.getGuid()))
								{
									return true;
								}
							}
						}
					}
				}
			}
			return false;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public boolean isPerformerOfActrtRuntime(String procRtGuid, Map<String, ActivityRuntime> activityMap) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		AAS aas = this.stubService.getAAS();
		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.PROCRT_GUID, procRtGuid);
			List<Performer> perfList = sds.query(Performer.class, filter, "select");

			if (!SetUtils.isNullList(perfList))
			{
				for (Performer performer : perfList)
				{
					if (activityMap.containsKey(performer.getActRuntimeGuid()))
					{
						if (PerformerTypeEnum.USER == performer.getPerformerType())
						{
							boolean isAgent = this.stubService.getAAS().isAgent(operatorGuid, performer.getPerformerGuid());
							if (operatorGuid.equals(performer.getPerformerGuid()) || isAgent)
							{
								if (this.stubService.getTrackStub().isPerformed(performer.getActRuntimeGuid(), operatorGuid,
										activityMap.get(performer.getActRuntimeGuid()).getStartNumber()))
								{
									return false;
								}
								else
								{
									return true;
								}
							}
						}
						else
						{
							List<User> listUser = null;
							if (performer.getPerformerType() == PerformerTypeEnum.RIG)
							{
								listUser = aas.listUserByRoleInGroup(performer.getPerformerGuid());

							}
							else if (performer.getPerformerType() == PerformerTypeEnum.GROUP)
							{
								listUser = aas.listUserInGroupAndSubGroup(performer.getPerformerGuid());
							}
							if (!SetUtils.isNullList(listUser))
							{
								for (User user : listUser)
								{
									if (operatorGuid.equals(user.getGuid()))
									{
										if (this.stubService.getTrackStub().isPerformed(performer.getActRuntimeGuid(), operatorGuid,
												activityMap.get(performer.getActRuntimeGuid()).getStartNumber()))
										{
											return false;
										}
										else
										{
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
			return false;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 
	 * @param procRtGuid
	 * @param noticetype
	 *            预警1
	 *            超期2
	 *            完成3
	 * @throws ServiceRequestException
	 */
	public List<String> listNoticeAllper(String procRtGuid, String noticetype, String isreject) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			List<String> userGuidList = new ArrayList<String>();
			List<String> finalUserGuidList = new ArrayList<String>();

			String noticeType = "";
			if ("1".equals(noticetype))
			{
				noticeType = "ACTRTADV";
			}
			else if ("2".equals(noticetype))
			{
				noticeType = "ACTRTDEF";
			}
			else if ("3".equals(noticetype))
			{
				noticeType = "ACTRTFIN";
			}
			else
			{
				throw new ServiceRequestException("noticetype:" + noticetype + " is errror! please recheck");
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("NOTICETYPE", noticeType);
			filter.put("ACTRTGUID", procRtGuid);
			Performer performer = sds.queryObject(Performer.class, filter, "selectNoticePer");
			if (performer != null)
			{
				String procrtguid = (String) performer.get("PROCRTGUID");
				String actrtgate = (String) performer.get("ACTRTGATE");
				String wftemplateactrtguid = (String) performer.get("WFTEMPLATEACTRTGUID");
				String wfcreateuserguid = (String) performer.get("WFCREATEUSERGUID");

				String hasorganiger = (String) performer.get("HASORGANIGER");
				String haslastexecutor = (String) performer.get("HASLASTEXECUTOR");
				String hasnextexecutor = (String) performer.get("HASNEXTEXECUTOR");
				String hasallexecutor = (String) performer.get("HASALLEXECUTOR");
				String hasleader = (String) performer.get("HASLEADER");

				// 发起人
				if (!StringUtils.isNullString(hasorganiger) && "1".equals(hasorganiger) && !StringUtils.isNullString(wfcreateuserguid))
				{
					userGuidList.add(wfcreateuserguid);
				}
				// 上一个活动的执行人
				if (!StringUtils.isNullString(haslastexecutor) && !StringUtils.isNullString(hasallexecutor) && "1".equals(haslastexecutor) && "0".equals(hasallexecutor))
				{
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("ACTRTGATE", actrtgate);
					param.put(Performer.PROCRT_GUID, procrtguid);
					Performer lastPerformer = sds.queryObject(Performer.class, param, "selectLastNoticePer");
					List<String> resultList = this.dealPerformer4LastOrNext(lastPerformer);
					if (!SetUtils.isNullList(resultList))
					{
						userGuidList.addAll(resultList);
					}
				}
				// 下一个活动的执行人
				if (!StringUtils.isNullString(hasnextexecutor) && !StringUtils.isNullString(hasallexecutor) && !StringUtils.isNullString(isreject) && "1".equals(hasnextexecutor)
						&& "0".equals(hasallexecutor) && "N".equals(isreject))
				{
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("ACTRTGATE", actrtgate);
					param.put(Performer.PROCRT_GUID, procrtguid);
					Performer nextPerformer = sds.queryObject(Performer.class, param, "selectNextNoticePer");
					List<String> resultList = this.dealPerformer4LastOrNext(nextPerformer);
					if (!SetUtils.isNullList(resultList))
					{
						userGuidList.addAll(resultList);
					}
				}
				// 所有活动的执行人
				if (!StringUtils.isNullString(hasallexecutor) && "1".equals(hasallexecutor))
				{
					List<String> resultList = this.dealPerformerByType4All(procrtguid);
					if (!SetUtils.isNullList(resultList))
					{
						userGuidList.addAll(resultList);
					}
				}
				// leader
				if (!StringUtils.isNullString(hasleader) && "1".equals(hasleader))
				{
					List<String> resultList = this.dealLeader(procRtGuid);
					if (!SetUtils.isNullList(resultList))
					{
						userGuidList.addAll(resultList);
					}
				}

				// noticeDetail
				List<String> noticeList = this.dealPerformerByType4Notice(wftemplateactrtguid, WorkflowTemplateActPerformerInfo.TEMPLATEACTRTGUID);
				if (!SetUtils.isNullList(noticeList))
				{
					userGuidList.addAll(noticeList);
				}

				// 去除废弃的用户。同时去除重复的userguid
				if (!SetUtils.isNullList(userGuidList))
				{
					for (String userGuid : userGuidList)
					{
						User user = sds.get(User.class, userGuid);
						if (user != null && user.isActive() && !finalUserGuidList.contains(userGuid))
						{
							finalUserGuidList.add(userGuid);
						}
					}
				}
			}

			return finalUserGuidList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	private List<String> dealLeader(String procRtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Performer.ACTRT_GUID, procRtGuid);
		List<Performer> performerList = sds.query(Performer.class, param);
		if (!SetUtils.isNullList(performerList))
		{
			for (Performer perf : performerList)
			{
				PerformerTypeEnum perfType = perf.getPerformerType();

				switch (perfType)
				{
				case USER:
					String roleGuid4User = perf.getPerRoleGuid();
					Role role4User = sds.getObject(Role.class, roleGuid4User);
					if (role4User != null && !"manager".equals(role4User.getRoleId()))
					{
						String groupGuid = perf.getPerGroupGuid();

						List<String> leader4UserList = this.dealLeader4UserType(groupGuid);
						if (!SetUtils.isNullList(leader4UserList))
						{
							userGuidList.addAll(leader4UserList);
						}

						Group group = sds.get(Group.class, groupGuid);
						if (group != null && group.getParentGuid() != null)
						{
							List<String> leader4UserParentList = this.dealLeader4UserType(group.getParentGuid());
							if (!SetUtils.isNullList(leader4UserParentList))
							{
								userGuidList.addAll(leader4UserParentList);
							}
						}
					}
					break;
				case RIG:
					RIG rig = sds.getObject(RIG.class, perf.getPerformerGuid());
					Role role = sds.getObject(Role.class, rig.getRoleGuid());
					if (role != null && "manager".equals(role.getRoleId()))
					{
						String groupGuid = rig.getGroupGuid();

						List<String> leader4RIGList = this.dealLeader4RIGType(groupGuid);
						if (!SetUtils.isNullList(leader4RIGList))
						{
							userGuidList.addAll(leader4RIGList);
						}

						Group group = sds.get(Group.class, groupGuid);
						if (group != null && group.getParentGuid() != null)
						{
							List<String> leader4RIGParentList = this.dealLeader4UserType(group.getParentGuid());
							if (!SetUtils.isNullList(leader4RIGParentList))
							{
								userGuidList.addAll(leader4RIGParentList);
							}
						}
					}
					break;
				case GROUP:
					Group group = sds.get(Group.class, perf.getPerformerGuid());
					if (group != null && group.getParentGuid() != null)
					{
						List<RIG> rigList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.GROUP_GUID, group.getParentGuid()));
						if (!SetUtils.isNullList(rigList))
						{
							for (RIG midRig : rigList)
							{
								Role midRole = sds.get(Role.class, midRig.getRoleGuid());
								if (midRole != null && "manager".equalsIgnoreCase(midRole.getRoleId()))
								{
									List<URIG> midUrigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, midRig.getGuid()));
									if (!SetUtils.isNullList(midUrigList))
									{
										for (URIG urig : midUrigList)
										{
											userGuidList.add(urig.getUserGuid());
										}
									}
								}
							}
						}
					}
					break;
				default:
					break;
				}
			}
		}
		return userGuidList;
	}

	/**
	 * leader type = user
	 * 
	 * @param groupGuid
	 * @return
	 */
	private List<String> dealLeader4UserType(String groupGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();
		List<RIG> rig4UserList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.GROUP_GUID, groupGuid));
		if (!SetUtils.isNullList(rig4UserList))
		{
			for (RIG rig : rig4UserList)
			{
				Role role = sds.get(Role.class, rig.getRoleGuid());
				if (role != null && "manager".equals(role.getRoleId()))
				{
					List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, rig.getGuid()));
					if (!SetUtils.isNullList(urigList))
					{
						for (URIG urig : urigList)
						{
							userGuidList.add(urig.getUserGuid());
						}
					}
				}
			}
		}
		return userGuidList;
	}

	/**
	 * leader type = RIG
	 * 
	 * @param groupGuid
	 * @return
	 */
	private List<String> dealLeader4RIGType(String groupGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();
		List<RIG> rigList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.GROUP_GUID, groupGuid));
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG midRig : rigList)
			{
				Role midRole = sds.get(Role.class, midRig.getRoleGuid());
				if (midRole != null && "manager".equals(midRole.getRoleId()))
				{
					List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, midRig.getGuid()));
					if (!SetUtils.isNullList(urigList))
					{
						for (URIG urig : urigList)
						{
							userGuidList.add(urig.getUserGuid());
						}
					}
				}
			}
		}
		return userGuidList;
	}

	/**
	 * 上一活动和下一活动
	 * 
	 * @param performer
	 * @return
	 */
	private List<String> dealPerformer4LastOrNext(Performer performer)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();
		if (performer != null)
		{
			String actrtGuid = performer.getGuid();
			if (!StringUtils.isNullString(actrtGuid))
			{
				Map<String, Object> mid_oneFilter = new HashMap<String, Object>();
				mid_oneFilter.put(Performer.ACTRT_GUID, actrtGuid);
				List<Performer> perfList = sds.query(Performer.class, mid_oneFilter);
				userGuidList = dealPerformerByType(perfList);
			}
		}
		return userGuidList;
	}

	/**
	 * 所有活动执行人
	 * 
	 * @param performer
	 * @return
	 */
	private List<String> dealPerformerByType4All(String procrtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();
		if (!StringUtils.isNullString(procrtGuid))
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ActivityRuntime.PROCRT_GUID, procrtGuid);
			List<Performer> performerList = sds.query(Performer.class, filter, "selectAllNoticePer");
			userGuidList = dealPerformerByType(performerList);
		}
		return userGuidList;
	}

	/**
	 * notice
	 * 
	 * @param performer
	 * @return
	 */
	private List<String> dealPerformerByType4Notice(String wftemplateactrtguid, String fieldName)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();

		if (StringUtils.isNullString(wftemplateactrtguid))
		{
			return new ArrayList<String>();
		}
		List<WorkflowTemplateActPerformerInfo> noticeList = sds.listFromCache(WorkflowTemplateActPerformerInfo.class,
				new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(fieldName, wftemplateactrtguid));
		if (!SetUtils.isNullList(noticeList))
		{
			for (WorkflowTemplateActPerformerInfo notice : noticeList)
			{
				PerformerTypeEnum perfType = notice.getPerfType();
				switch (perfType)
				{
				case USER:
					userGuidList.add(notice.getPerfGuid());
					break;
				case GROUP:
					String childGroupGuid = notice.getPerfGuid();
					List<String> allgroupList = this.getAllParentGroup(childGroupGuid);
					if (!SetUtils.isNullList(allgroupList))
					{
						for (String groupGuid : allgroupList)
						{
							List<RIG> rigList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.GROUP_GUID, groupGuid));
							if (!SetUtils.isNullList(rigList))
							{
								for (RIG rig : rigList)
								{
									List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, rig.getGuid()));
									if (!SetUtils.isNullList(urigList))
									{
										for (URIG urig : urigList)
										{
											userGuidList.add(urig.getUserGuid());
										}
									}
								}
							}
						}
					}
					break;
				case RIG:
					List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, notice.getPerfGuid()));
					if (!SetUtils.isNullList(urigList))
					{
						for (URIG urig : urigList)
						{
							userGuidList.add(urig.getUserGuid());
						}
					}
					break;
				case ROLE:
					List<RIG> rigList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.ROLE_GUID, notice.getPerfGuid()));
					if (!SetUtils.isNullList(rigList))
					{
						for (RIG rig : rigList)
						{
							List<URIG> midUrigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, rig.getGuid()));
							if (!SetUtils.isNullList(midUrigList))
							{
								for (URIG urig : midUrigList)
								{
									userGuidList.add(urig.getUserGuid());
								}
							}
						}
					}
					break;

				default:
					break;
				}
			}
		}
		return userGuidList;
	}

	/**
	 * 获取自己所有的前代节点组（包括自己）
	 * 
	 * @param childGroupGuid
	 * @return
	 */

	private List<String> getAllParentGroup(String childGroupGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> allGroup = new ArrayList<String>();
		if (!StringUtils.isNullString(childGroupGuid))
		{
			Group childGroup = sds.get(Group.class, childGroupGuid);
			if (childGroup != null)
			{
				allGroup.add(childGroupGuid);
				String parentGroupGuid = childGroup.getParentGuid();
				if (!StringUtils.isNullString(parentGroupGuid))
				{
					Group parentGroup = sds.get(Group.class, parentGroupGuid);
					if (parentGroup != null)
					{
						allGroup.add(parentGroupGuid);
						List<String> parentGroupList = getAllParentGroup(parentGroupGuid);
						if (!SetUtils.isNullList(parentGroupList))
						{
							allGroup.addAll(parentGroupList);
						}
					}
				}
			}
		}
		return allGroup;
	}

	private List<String> dealPerformerByType(List<Performer> performerList)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<String> userGuidList = new ArrayList<String>();
		if (!SetUtils.isNullList(performerList))
		{
			for (Performer perf : performerList)
			{

				PerformerTypeEnum performerType = perf.getPerformerType();
				switch (performerType)
				{
				case USER:
					userGuidList.add(perf.getPerformerGuid());
					break;
				case GROUP:
					List<RIG> rigList = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.GROUP_GUID, perf.getPerformerGuid()));
					if (!SetUtils.isNullList(rigList))
					{
						for (RIG rig : rigList)
						{
							List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, rig.getGuid()));
							if (!SetUtils.isNullList(urigList))
							{
								for (URIG urig : urigList)
								{
									userGuidList.add(urig.getUserGuid());
								}
							}
						}
					}
					break;
				case RIG:
					List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, perf.getPerformerGuid()));
					if (!SetUtils.isNullList(urigList))
					{
						for (URIG urig : urigList)
						{
							userGuidList.add(urig.getUserGuid());
						}
					}
					break;
				case ROLE:
					List<RIG> rigList4Role = sds.listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.ROLE_GUID, perf.getPerformerGuid()));
					if (!SetUtils.isNullList(rigList4Role))
					{
						for (RIG rig : rigList4Role)
						{
							List<URIG> urigList4Role = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, rig.getGuid()));
							if (!SetUtils.isNullList(urigList4Role))
							{
								for (URIG urig : urigList4Role)
								{
									userGuidList.add(urig.getUserGuid());
								}
							}
						}
					}
					break;
				default:
					break;
				}
			}
		}
		return userGuidList;
	}

	/**
	 * 
	 * @param procRtGuid
	 * @param noticetype
	 *            1是创建流程的通知人
	 *            2是流程结束的通知人。
	 * @throws ServiceRequestException
	 */
	public List<String> listProcessNoticeAllper(String procRtGuid, String noticetype) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			List<String> userGuidList = new ArrayList<String>();
			List<String> finalUserGuidList = new ArrayList<String>();

			String noticeType = "";
			if ("1".equals(noticetype))
			{
				noticeType = "OPEN";
			}
			else if ("2".equals(noticetype))
			{
				noticeType = "CLOSE";
			}
			else
			{
				throw new ServiceRequestException("noticetype:" + noticetype + " is errror! please recheck");
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("NOTICETYPE", noticeType);
			filter.put(ActivityRuntime.PROCRT_GUID, procRtGuid);
			Performer performer = sds.queryObject(Performer.class, filter, "selectProcessNoticePer");

			if (performer != null)
			{
				String wftemplateguid = (String) performer.get("WFTEMPLATEGUID");
				String createuserguid = (String) performer.get("CREATEUSERGUID");
				String hasorganiger = (String) performer.get("HASORGANIGER");
				String hasallexecutor = (String) performer.get("HASALLEXECUTOR");
				String hasleader = (String) performer.get("HASLEADER");

				// 发起人
				if (!StringUtils.isNullString(hasorganiger) && !StringUtils.isNullString(createuserguid) && "1".equals(hasorganiger))
				{
					userGuidList.add(createuserguid);
				}
				// 所有活动的执行人
				if (!StringUtils.isNullString(hasallexecutor) && "1".equals(hasallexecutor))
				{
					List<String> resultList = this.dealPerformerByType4All(procRtGuid);
					if (!SetUtils.isNullList(resultList))
					{
						userGuidList.addAll(resultList);
					}
				}
				// leader
				if (!StringUtils.isNullString(hasleader) && "1".equals(hasleader))
				{
					List<String> resultList = this.dealLeader(procRtGuid);
					if (!SetUtils.isNullList(resultList))
					{
						userGuidList.addAll(resultList);
					}

				}

				// noticeDetail
				List<String> noticeList = this.dealPerformerByType4Notice(wftemplateguid, WorkflowTemplateActPerformerInfo.TEMPLATEGUID);
				if (!SetUtils.isNullList(noticeList))
				{
					userGuidList.addAll(noticeList);
				}

				// 去除废弃的用户。同时去除重复的userguid
				if (!SetUtils.isNullList(userGuidList))
				{
					for (String userGuid : userGuidList)
					{
						User user = sds.get(User.class, userGuid);
						if (user != null && user.isActive() && !finalUserGuidList.contains(userGuid))
						{
							finalUserGuidList.add(userGuid);
						}
					}
				}

			}

			return finalUserGuidList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 返回流程活动的执行人
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> listActrtPerformer(String actRtGuid) throws ServiceRequestException
	{
		List<User> userList = new ArrayList<User>();
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("ACTRTGUID", actRtGuid);
			List<Performer> performerList = sds.query(Performer.class, param);
			if (!SetUtils.isNullList(performerList))
			{
				for (Performer performer_ : performerList)
				{
					switch (performer_.getPerformerType())
					{
					case USER:
						User user = this.stubService.getAAS().getUser(performer_.getPerformerGuid());
						if (user != null && !userList.contains(user))
						{
							userList.add(user);
						}
						break;
					case RIG:
						RIG rig = this.stubService.getAAS().getRIG(performer_.getPerformerGuid());
						if (rig != null)
						{
							List<User> userInRIGList = this.stubService.getAAS().listUserByRoleInGroup(rig.getGuid());
							if (!SetUtils.isNullList(userInRIGList))
							{
								userList.addAll(userInRIGList);
							}
						}
						break;
					case ROLE:
						List<RIG> rigList = this.stubService.getAAS().listRIGByRoleGuid(performer_.getPerformerGuid());
						if (!SetUtils.isNullList(rigList))
						{
							for (RIG rig_ : rigList)
							{
								List<User> userInRIGList = this.stubService.getAAS().listUserByRoleInGroup(rig_.getGuid());
								if (!SetUtils.isNullList(userInRIGList))
								{
									userList.addAll(userInRIGList);
								}
							}
						}
						break;
					case GROUP:
						List<User> userInGroupList = this.stubService.getAAS().listUserInGroupAndSubGroup(performer_.getPerformerGuid());
						if (!SetUtils.isNullList(userInGroupList))
						{
							userList.addAll(userInGroupList);
						}
						break;
					}
				}
			}
			return userList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 流程活动同意,取其執行人列表
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> listActrtAcceptPerformer(String actRtGuid) throws ServiceRequestException
	{
		ActivityRuntime activity = this.stubService.getActivityRuntime(actRtGuid);

		if (activity == null)
		{
			DynaLogger.info("activity is null");
			return null;
		}
		if (activity.isFinished())
		{
			DynaLogger.info("actvity is finish");
			return null;
		}

		return this.listActrtPerformedUser(actRtGuid, true, false);
	}

	private List<User> listActrtPerformedUser(String actRtGuid, boolean isAccept, boolean isReject) throws ServiceRequestException
	{
		List<User> acceptUserList = new ArrayList<User>();

		// 1、取得活动节点最新的审批结果（同意/跳过）
		List<ProcTrack> trackList = this.listLatestTrack(actRtGuid, isAccept, isReject);
		// 2、取得活动节点设置的审批人/组/角色/组角色的所有用户列表
		List<User> userList = this.listActrtPerformer(actRtGuid);
		// 3、取得审批节点设置的审批人/组/角色/组角色中已完成的审批人
		if (!SetUtils.isNullList(userList))
		{
			for (User user : userList)
			{
				if (this.isPerformed(user, trackList))
				{
					acceptUserList.add(user);
				}
			}
		}
		return acceptUserList;
	}

	private boolean isPerformed(User user, List<ProcTrack> trackList)
	{
		if (!SetUtils.isNullList(trackList))
		{
			for (ProcTrack track : trackList)
			{
				if (user.getGuid().equals(track.getPerformerGuid()))
				{
					return true;
				}
			}
		}
		return false;
	}

	private List<ProcTrack> listLatestTrack(String actRtGuid, boolean isAccept, boolean isReject) throws ServiceRequestException
	{
		List<ProcTrack> trackList = this.stubService.listActivityComment(actRtGuid, null);
		if (!SetUtils.isNullList(trackList))
		{
			Collections.sort(trackList, new Comparator<ProcTrack>() {

				@Override
				public int compare(ProcTrack o1, ProcTrack o2)
				{
					return o2.getCreateTime().compareTo(o1.getCreateTime());
				}
			});

			List<String> perfUserGuidList = new ArrayList<String>();
			for (Iterator<ProcTrack> it = trackList.iterator(); it.hasNext();)
			{
				ProcTrack track = it.next();

				if (isAccept)
				{
					if (track.getDecide() != DecisionEnum.ACCEPT && track.getDecide() != DecisionEnum.SKIP)
					{
						it.remove();
						continue;
					}
				}

				if (isReject)
				{
					if (track.getDecide() != DecisionEnum.REJECT)
					{
						it.remove();
						continue;
					}
				}

				if (!perfUserGuidList.contains(track.getPerformerGuid()))
				{
					perfUserGuidList.add(track.getPerformerGuid());
				}
				else
				{
					it.remove();
				}
			}
		}
		return trackList;
	}

	/**
	 * 流程活动拒绝，取其执行人列表
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> listActrtRejectPerformer(String actRtGuid) throws ServiceRequestException
	{
		ActivityRuntime activity = this.stubService.getActivityRuntime(actRtGuid);

		if (activity == null)
		{
			DynaLogger.info("activity is null");
			return null;
		}
		if (activity.isFinished())
		{
			DynaLogger.info("actvity is finish");
			return null;
		}

		return this.listActrtPerformedUser(actRtGuid, false, true);
	}

	public void savePerformer(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		if (settings == null)
		{
			return;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();

		String actRtGuid = null;
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);

		Entry<String, Performer> perfEntry = null;
		int perfSize = 0;
		ActivityRuntime actRt = null;
		Set<Entry<String, Map<String, Performer>>> entries = settings.getAddPerformerEntries();
		for (Entry<String, Map<String, Performer>> entry : entries)
		{
			perfSize = 0;
			actRtGuid = entry.getKey();
			int maxPerf = 0;
			actRt = this.stubService.getActivityRuntimeStub().getActivityRuntime(actRtGuid);
			if (actRt != null && (actRt.getActType() == WorkflowActivityType.MANUAL || actRt.getActType() == WorkflowActivityType.NOTIFY))
			{
				WorkflowTemplateAct workflowTemplateAct = this.stubService.getTemplateStub().getWorkflowTemplateActSetInfoSingle(processRuntime.getWFTemplateGuid(),
						actRt.getName());
				String maximumExecutors = workflowTemplateAct == null ? null : workflowTemplateAct.getWorkflowTemplateActInfo().getMaximumExecutors();
				maxPerf = maximumExecutors == null ? 0 : Integer.valueOf(maximumExecutors);
			}

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put(Performer.ACTRT_GUID, actRtGuid);
			sds.delete(Performer.class, paramMap, "deleteperformerActrt");

			for (Iterator<Entry<String, Performer>> iterator = entry.getValue().entrySet().iterator(); iterator.hasNext();)
			{
				perfEntry = iterator.next();

				Performer perf = perfEntry.getValue();// new Performer();
				if (perf == null)
				{
					continue;
				}
				perf.setProcessRuntimeGuid(procRtGuid);
				perf.setActRuntimeGuid(actRtGuid);
				perf.setCreateUserGuid(operatorGuid);
				perf.setUpdateUserGuid(operatorGuid);

				sds.save(perf);

				if (maxPerf != 0)
				{
					if (perf.getPerformerType() == PerformerTypeEnum.USER)
					{
						perfSize++;
					}
					else
					{
						List<User> listUser = this.listUserInRoleGroup(perf.getPerformerType(), perfEntry.getKey());
						if (listUser != null)
						{
							perfSize += listUser.size();
						}
					}

					if (perfSize > maxPerf)
					{
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
						throw new ServiceRequestException("ID_APP_WF_PERFORMER_MAXIMUM_OVER", "Ecutors Maximumex Over", null, actRt.getTitle(languageEnum));

					}
				}
			}
		}
	}

	private void saveDeadLine(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		if (settings == null)
		{
			return;
		}
		SystemDataService sds = DataServer.getSystemDataService();
		ActivityRuntime actRt = null;
		Iterator<Entry<String, Date>> iterator = settings.getDeadlineMap().entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, Date> deadline = iterator.next();
			actRt = this.stubService.getActivityRuntimeStub().getActivityRuntime(deadline.getKey());
			actRt.setDeadline(deadline.getValue());
			sds.save(actRt);
		}
	}

	public void savePerformerAndDeadLine(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		if (settings == null)
		{
			return;
		}
		savePerformer(procRtGuid, settings);
		saveDeadLine(procRtGuid, settings);
	}

	public void addPerformer(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		if (settings == null)
		{
			return;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();

		String actRtGuid = null;
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);

		Entry<String, Performer> perfEntry = null;
		int perfSize = 0;
		ActivityRuntime actRt = null;
		Set<Entry<String, Map<String, Performer>>> entries = settings.getAddPerformerEntries();
		for (Entry<String, Map<String, Performer>> entry : entries)
		{
			actRtGuid = entry.getKey();
			int maxPerf = 0;
			actRt = this.stubService.getActivityRuntimeStub().getActivityRuntime(actRtGuid);
			if (actRt != null && (actRt.getActType() == WorkflowActivityType.MANUAL || actRt.getActType() == WorkflowActivityType.NOTIFY))
			{
				WorkflowTemplateAct workflowTemplateAct = this.stubService.getTemplateStub().getWorkflowTemplateActSetInfoSingle(processRuntime.getWFTemplateGuid(),
						actRt.getName());
				String maximumExecutors = workflowTemplateAct == null ? null : workflowTemplateAct.getWorkflowTemplateActInfo().getMaximumExecutors();
				maxPerf = maximumExecutors == null ? 0 : Integer.valueOf(maximumExecutors);
			}

			List<Performer> listPerformer = this.stubService.listPerformer(actRtGuid);
			perfSize = SetUtils.isNullList(listPerformer) ? 0 : listPerformer.size();
			for (Iterator<Entry<String, Performer>> iterator = entry.getValue().entrySet().iterator(); iterator.hasNext();)
			{
				perfEntry = iterator.next();

				Performer perf = perfEntry.getValue();// new Performer();
				if (perf == null)
				{
					continue;
				}
				perf.setProcessRuntimeGuid(procRtGuid);
				perf.setActRuntimeGuid(actRtGuid);
				perf.setCreateUserGuid(operatorGuid);
				perf.setUpdateUserGuid(operatorGuid);
				// 是加签人
				perf.setRechecker(true);

				sds.save(perf);

				if (maxPerf != 0)
				{
					if (perf.getPerformerType() == PerformerTypeEnum.USER)
					{
						perfSize++;
					}
					else
					{
						List<User> listUser = this.listUserInRoleGroup(perf.getPerformerType(), perfEntry.getKey());
						if (listUser != null)
						{
							perfSize += listUser.size();
						}
					}

					if (perfSize > maxPerf)
					{
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
						throw new ServiceRequestException("ID_APP_WF_PERFORMER_MAXIMUM_OVER", "Ecutors Maximumex Over", null, actRt.getTitle(languageEnum));

					}
				}
			}
		}
	}

	/**
	 * 取得所选组或角色下的用户
	 * 
	 * @param perfEntry
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> listUserInRoleGroup(PerformerTypeEnum performerType, String performerGuid) throws ServiceRequestException
	{
		List<User> listUser = null;
		if (performerType == PerformerTypeEnum.USER)
		{
			listUser = new ArrayList<User>();
			listUser.add(this.stubService.getAAS().getUser(performerGuid));
		}
		else if (performerType == PerformerTypeEnum.RIG)
		{
			listUser = this.stubService.getAAS().listUserByRoleInGroup(performerGuid);

		}
		else if (performerType == PerformerTypeEnum.GROUP)
		{

			listUser = this.stubService.getAAS().listUserInGroupAndSubGroup(performerGuid);
		}

		return listUser;

	}

	public void checkPerform(ActivityRuntime actrt, String rejectToActRtGuid, boolean isAllActivity) throws ServiceRequestException
	{
		WorkflowActivityType actType = null;
		List<ActivityRuntime> actList = null;

		if (StringUtils.isGuid(rejectToActRtGuid))
		{
			actList = this.stubService.getActivityRuntimeStub().listRejectedPath(actrt, rejectToActRtGuid);
		}
		else
		{
			if (actrt != null)
			{
				actList = this.stubService.getActivityRuntimeStub().listAcceptableFromActivityRuntime(actrt.getGuid());
			}
		}

		if (SetUtils.isNullList(actList))
		{
			return;
		}

		for (ActivityRuntime nextActRt : actList)
		{
			actType = nextActRt.getActType();
			switch (actType)
			{
			case MANUAL:
			case SUB_PROCESS:
			case NOTIFY:
				List<Performer> listPerformer = this.stubService.listPerformer(nextActRt.getGuid());

				// 判断执行人是否可以为空
				boolean isCheckNext = this.stubService.getPerformerStub().checkPerform(nextActRt.getProcessRuntimeGuid(), nextActRt, listPerformer, isAllActivity);

				if (isAllActivity)
				{
					this.checkPerform(nextActRt, null, isAllActivity);
				}
				else
				{
					if (isCheckNext)
					{
						this.checkPerform(nextActRt, null, isAllActivity);
					}
				}

				break;
			default: // SUB_PROCESS
				this.checkPerform(nextActRt, null, isAllActivity);
				break;
			}
		}
	}

	private boolean checkPerform(String procRtGuid, ActivityRuntime actrt, List<Performer> listPerformer, boolean isAllActivity) throws ServiceRequestException
	{
		if (listPerformer != null && listPerformer.size() <= 0)
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
			if (StringUtils.isGuid(processRuntime.getWFTemplateGuid()))
			{
				WorkflowTemplateVo workflowTemplateVo = this.stubService.getWorkflowTemplateDetail(processRuntime.getWFTemplateGuid());
				LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

				WorkflowTemplateAct workflowTemplateActivity = workflowTemplateVo.getWorkflowTemplateActivity(actrt.getName());
				// 活动节点是否可以跳过
				if (workflowTemplateActivity != null && workflowTemplateActivity.getWorkflowTemplateActInfo().isPassed())
				{
					return true;
				}

				if (isAllActivity)
				{
					throw new ServiceRequestException("ID_APP_WF_REQUIRED_ACTIVITY_PERFORM_ISNULL", "all perform is required ,activity perform is null", null, actrt.getName(),
							actrt.getTitle(languageEnum));
				}
				else
				{
					throw new ServiceRequestException("ID_APP_WF_NEXT_ACTIVITY_PERFORM_ISNULL", "next activity perform is null", null, actrt.getName(),
							actrt.getTitle(languageEnum));
				}
			}
		}

		return false;
	}

	// O11877/A.3-“无人员角色”和“人员意外禁用”导致的流程无法审批也无法退回
	/**
	 * 查询USER是否在流程中
	 */
	public List<Performer> queryPerformer(String userGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Performer.PERF_GUID, userGuid);
			List<Performer> perfList = sds.query(Performer.class, filter, "selectActmode");
			return perfList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<String> getPrincipalsOfAgent(String agentGuid, String actrtGuid) throws ServiceRequestException
	{
		List<String> principalList = new ArrayList<String>();
		List<User> performers = this.listActrtPerformer(actrtGuid);
		if (!SetUtils.isNullList(performers))
		{
			for (User user : performers)
			{
				boolean isAgent = this.stubService.getAAS().isAgent(agentGuid, user.getGuid());
				if (isAgent)
				{
					principalList.add(user.getGuid());
				}
			}
		}
		return principalList;
	}

	public List<String> getNotFinishedPrincipalsOfAgent(String agentGuid, String actrtGuid) throws ServiceRequestException
	{
		List<String> principalList = new ArrayList<String>();
		List<User> performers = this.listNotFinishPerformer(actrtGuid);
		if (!SetUtils.isNullList(performers))
		{
			for (User user : performers)
			{
				boolean isAgent = this.stubService.getAAS().isAgent(agentGuid, user.getGuid());
				if (isAgent)
				{
					principalList.add(user.getGuid());
				}
			}
		}
		return principalList;
	}

	public boolean isAgentPerformerOfActrt(String agentGuid, String actrtGuid) throws ServiceRequestException
	{
		List<User> performers = this.listActrtPerformer(actrtGuid);
		if (!SetUtils.isNullList(performers))
		{
			for (User user : performers)
			{
				boolean isAgent = this.stubService.getAAS().isAgent(agentGuid, user.getGuid());
				if (isAgent)
				{
					return true;
				}
			}
		}
		return false;
	}

	private void decoratePerformer4List(List<Performer> performerList)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(performerList))
		{
			for (Performer performer : performerList)
			{
				PerformerTypeEnum perfType = performer.getPerformerType();
				String perfGuid = performer.getPerformerGuid();
				if (perfType != null)
				{
					switch (perfType)
					{
					case USER:
						User user = sds.get(User.class, perfGuid);
						performer.setPerformerName(user == null ? null : user.getName());
						break;

					case RIG:
						RIG rig = sds.get(RIG.class, perfGuid);
						if (rig != null)
						{
							Group group = sds.get(Group.class, rig.getGroupGuid());
							Role role = sds.get(Role.class, rig.getRoleGuid());
							performer.setPerformerName(group == null ? "" : group.getName() + "-" + role == null ? "" : role.getName());
						}
						break;

					case GROUP:
						Group group = sds.get(Group.class, perfGuid);
						performer.setPerformerName(group == null ? null : group.getName());
						break;

					case ROLE:
						Role role = sds.get(Role.class, perfGuid);
						performer.setPerformerName(role == null ? null : role.getGuid());
						break;

					default:
						break;
					}

				}
			}
		}
	}

	/**
	 * 删除流程实例中所有执行人
	 * 
	 * @param procRtGuid
	 */
	public void deleteAllPerformer(String procRtGuid)
	{
		this.dbStub.deleteAllPerformer(procRtGuid);
	}

	/**
	 * 删除流程实例中实际执行人
	 * 
	 * @param procRtGuid
	 */
	public void deleteActualPerformer(String procRtGuid)
	{
		this.dbStub.deleteActualPerformer(procRtGuid);
	}
}
