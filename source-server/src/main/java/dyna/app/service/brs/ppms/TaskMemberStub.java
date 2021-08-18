/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理项目实例
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangLHB
 *         项目的查询，新建
 * 
 */
@Component
public class TaskMemberStub extends AbstractServiceStub<PPMSImpl>
{

	protected List<TaskMember> listTaskMemberByRole(String roleGuid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TaskMember.PROJECT_ROLE, roleGuid);
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.query(TaskMember.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}


	protected TaskMember saveTaskMember(TaskMember taskMember) throws ServiceRequestException
	{

		String operatorGuid = this.stubService.getOperatorGuid();
		taskMember.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		if (StringUtils.isNullString(taskMember.getGuid()))
		{
			taskMember.put(SystemObject.CREATE_USER_GUID, operatorGuid);

		}

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			String guid = sds.save(taskMember);
			if (StringUtils.isGuid(guid))
			{
				taskMember.setGuid(guid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return taskMember;
	}

	protected void removeTaskMember(String TaskMemberGuid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, TaskMemberGuid);
		SystemDataService sds = DataServer.getSystemDataService();

		TaskMember TaskMember = this.getTaskMember(TaskMemberGuid);
		if (TaskMember != null)
		{
			try
			{
				sds.delete(TaskMember.class, filter, "delete");
			}
			catch (DynaDataException e)
			{
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
		}
	}

	protected TaskMember getTaskMember(String TaskMemberGuid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, TaskMemberGuid);
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.queryObject(TaskMember.class, filter, "select");

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public List<TaskMember> listTaskMember(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(taskObjectGuid.getGuid()))
		{
			return null;
		}
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TaskMember.TASK_GUID, taskObjectGuid.getGuid());

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.query(TaskMember.class, filter);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 批量保存资源
	 * 
	 * @param objectGuid
	 * @param listTaskMember
	 * @throws ServiceRequestException
	 */
	protected void saveBatchTaskMember(ObjectGuid objectGuid, List<TaskMember> listTaskMember) throws ServiceRequestException
	{
		if (objectGuid == null || objectGuid.getGuid() == null)
		{
			return;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			this.dealWithTaskObject(objectGuid, listTaskMember);

			// 删除之前的数据
			List<TaskMember> listmem = this.stubService.listTaskMember(objectGuid);
			if (!SetUtils.isNullList(listmem))
			{
				for (TaskMember mem : listmem)
				{
					this.stubService.getTaskMemberStub().removeTaskMember(mem.getGuid());
				}
			}
			if (!SetUtils.isNullList(listTaskMember))
			{
				int count = 0;
				for (TaskMember mem : listTaskMember)
				{
					mem.clear(SystemObject.GUID);
					mem.setTaskObjectGuid(objectGuid);
					mem.setSequence(count);
					this.stubService.getTaskMemberStub().saveTaskMember(mem);
					count++;
				}
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
	}


	/**
	 * 处理对应的任务
	 * 
	 * @param objectGuid
	 * @param listTaskMember
	 * @throws ServiceRequestException
	 */
	private void dealWithTaskObject(ObjectGuid objectGuid, List<TaskMember> listTaskMember) throws ServiceRequestException
	{
		FoundationObject taskFoun = this.stubService.getBOAS().getObject(objectGuid);
		if (taskFoun == null)
		{
			return;
		}
		PPMFoundationObjectUtil ppmFoundation = new PPMFoundationObjectUtil(taskFoun);
		String executor = ppmFoundation.getExecutor();
		String executorRole = ppmFoundation.getExecuteRole();
		boolean isExchangeExector = false;
		boolean isExecutor = false;
		TaskMember executorMember = null;
		if (!SetUtils.isNullList(listTaskMember))
		{
			for (TaskMember member : listTaskMember)
			{
				if (member.isPersonInCharge())
				{
					isExecutor = true;
					if (member.getUserGuid() == null || member.getProjectRole() == null || !member.getProjectRole().equalsIgnoreCase(executorRole)
							|| !member.getUserGuid().equalsIgnoreCase(executor))
					{
						isExchangeExector = true;
						executorMember = member;
					}
					break;
				}
			}
			if (!isExecutor)
			{
				executorMember = listTaskMember.get(0);
				executorMember.setPersonInCharge(true);
			}
			if (isExchangeExector || !isExecutor)
			{
				taskFoun.put(PPMFoundationObjectUtil.EXECUTOR, executorMember.getUserGuid());
				boolean isWorkItem = this.isHasWorkItem(taskFoun);
				if (!isWorkItem)
				{
					taskFoun.put(PPMFoundationObjectUtil.EXECUTORROLE, executorMember.getProjectRole());
				}
				taskFoun = this.stubService.getWorkItemStub().saveObjectNotCheckAuthority(taskFoun);
			}
		}
		else
		{
			taskFoun.put(PPMFoundationObjectUtil.EXECUTOR, null);
			boolean isWorkItem = this.isHasWorkItem(taskFoun);
			if (!isWorkItem)
			{
				taskFoun.put(PPMFoundationObjectUtil.EXECUTORROLE, null);
			}
			taskFoun = this.stubService.getWorkItemStub().saveObjectNotCheckAuthority(taskFoun);
		}

	}

	/**
	 * 判断是否是工作项
	 * 
	 * @param taskFoun
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean isHasWorkItem(FoundationObject taskFoun) throws ServiceRequestException
	{
		if (taskFoun != null)
		{
			String classGuid = taskFoun.getObjectGuid().getClassGuid();
			if (StringUtils.isGuid(classGuid))
			{
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);
				if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IPMWorkItem))
				{
					return true;
				}
			}
		}
		return false;
	}

}
