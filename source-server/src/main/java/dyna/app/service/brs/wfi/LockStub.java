/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 附件锁操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.wf.ProcLockObject;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * 附件锁操作分支
 * 
 * @author Wanglei
 * 
 */
public class LockStub extends AbstractServiceStub<WFIImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected LockStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
	}

	protected String isLock(ObjectGuid attachment) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(ProcLockObject.INSTANCE_GUID, attachment.getGuid());

			ProcLockObject lockObject = sds.queryObject(ProcLockObject.class, searchConditionMap);
			if (lockObject != null)
			{
				return lockObject.getProcessRuntimeGuid();
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 附件上锁
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void lock(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			// 取得当前流程的所有子流程
			List<ProcessRuntime> subProcrtList = new ArrayList<ProcessRuntime>();
			this.listAllSubProc(procRtGuid, subProcrtList);

			StringBuffer subProcGuidBuff = new StringBuffer();
			if (!SetUtils.isNullList(subProcrtList))
			{
				for (ProcessRuntime subProc : subProcrtList)
				{
					if (subProcGuidBuff.length() > 0)
					{
						subProcGuidBuff.append(",");
					}
					subProcGuidBuff.append("'").append(subProc.getGuid()).append("'");
				}
			}
			else
			{
				subProcGuidBuff.append("'NULL'");
			}

			ProcLockObject param = new ProcLockObject();
			param.put("SUBPROCGUIDLIST", subProcGuidBuff.toString());
			param.put("PROCRTGUID", procRtGuid);

			SystemDataService sds = DataServer.getSystemDataService();
			sds.save(param, "lock");
		}
		catch (DynaDataException e)
		{
			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_UNIQUE)
			{
				throw new ServiceRequestException("ID_APP_WF_ATTACH_HAS_LOCKED", "attach has been locked");
			}

			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void listAllSubProc(String procrtGuid, List<ProcessRuntime> procrtList) throws ServiceRequestException
	{
		List<ProcessRuntime> list = this.stubService.listSubProcessRuntime(procrtGuid);
		if (!SetUtils.isNullList(list))
		{
			procrtList.addAll(list);
			for (ProcessRuntime proc : list)
			{
				this.listAllSubProc(proc.getGuid(), procrtList);
			}
		}
	}

	/**
	 * 附件解锁
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void unlock(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcLockObject.PROCRT_GUID, procRtGuid);
			sds.delete(ProcLockObject.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

}
