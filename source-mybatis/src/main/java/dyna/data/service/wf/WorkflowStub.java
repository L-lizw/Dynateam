/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理工作流
 * zhangHW 2012-2-27
 */
package dyna.data.service.wf;

import dyna.common.dto.wf.*;
import dyna.common.dtomapper.wf.ProcAttachMapper;
import dyna.common.dtomapper.wf.ProcLockObjectMapper;
import dyna.common.dtomapper.wf.ProcessRuntimeMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.SystemDataService;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataExceptionSQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对工作流提供支持
 * 
 * @author zhangHW
 * 
 */
@Repository
public class WorkflowStub extends DSAbstractServiceStub<WorkFlowServiceImpl>
{

	@Autowired
	private ProcLockObjectMapper        procLockObjectMapper;
	@Autowired
	private ProcessRuntimeMapper        processRuntimeMapper;
	@Autowired
	private ProcAttachMapper            procAttachMapper;

	@SuppressWarnings({ "unchecked", "unused" })
	private boolean checkLock(String instanceGuid, String procrtGuid) throws SQLException
	{
		List<String> allProcrtGuidList = getParentProcessRuntime(procrtGuid);
		ProcLockObject param = new ProcLockObject();
		param.setInstatnceGuid(instanceGuid);
		List<ProcLockObject> proLockList = this.procLockObjectMapper.select(param);
		if (!SetUtils.isNullList(proLockList))
		{
			for (ProcLockObject procLockObject : proLockList)
			{
				String processRuntimeGuid = procLockObject.getProcessRuntimeGuid();
				if (!StringUtils.isNullString(processRuntimeGuid) && !allProcrtGuidList.contains(processRuntimeGuid))
				{
					return false;
				}
			}
		}
		return true;
	}

	private List<String> getParentProcessRuntime(String processRuntimeGuid) throws SQLException
	{
		List<String> allProcrtGuidList = new ArrayList<>();
		allProcrtGuidList.add(processRuntimeGuid);
		Map<String, Object> filter = new HashMap<>();
		filter.put(ProcessRuntime.GUID, processRuntimeGuid);
		ProcessRuntime processRuntime = (ProcessRuntime) this.processRuntimeMapper.select(filter);
		if (processRuntime != null && processRuntime.getParentGuid() != null)
		{
			List<String> parentProcrtGuidList = getParentProcessRuntime(processRuntime.getParentGuid());
			if (!SetUtils.isNullList(parentProcrtGuidList))
			{
				allProcrtGuidList.addAll(parentProcrtGuidList);
			}
		}
		return allProcrtGuidList;
	}

	protected void deleteUnExistsAttach(String procRtGuid)
	{
		try
		{
			StringBuilder sqlBuffer = new StringBuilder();

			Map<String, Object> filter = new HashMap<>();
			filter.put(ProcAttach.PROCRT_GUID, procRtGuid);
			List<ProcAttach> list = DataServer.getSystemDataService().query(ProcAttach.class, filter, "selectClassOfInstance");
			if (!SetUtils.isNullList(list))
			{
				for (ProcAttach proc : list)
				{
					if (sqlBuffer.length() > 0)
					{
						sqlBuffer.append(" union ");
					}
					sqlBuffer.append("select a.guid from ");
					sqlBuffer.append(DataServer.getDSCommonService().getTableName(proc.getInstanceClassGuid())).append(" a ");
				}
			}

			filter.clear();
			filter.put(ProcAttach.PROCRT_GUID, procRtGuid);
			filter.put("tablesql", sqlBuffer.toString());
			DataServer.getSystemDataService().delete(ProcAttach.class, filter, "deleteUnexistsAttach");
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteUnExistsAttach exception procrtguid =" + procRtGuid, e, DataExceptionEnum.SDS_DELETE_PARAM);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<ProcAttach> listAllRevisionInWF(String masterGuid, String classGuid)
	{
		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("ITEMTABLE", DataServer.getDSCommonService().getTableName(classGuid));
			param.put("MASTERGUID", masterGuid);
			return (List<ProcAttach>) this.procAttachMapper.selectRevisionGuidInWFByMaster(param);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("query attach failed:" + e.getMessage(), e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}

	protected ActivityRuntime getActivityRuntime(String actRtGuid)
	{
		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put(ActivityRuntime.GUID, actRtGuid);
		ActivityRuntime activity = DataServer.getSystemDataService().queryObject(ActivityRuntime.class, searchConditionMap);
		return activity;
	}

	protected ActivityRuntime getActivityRuntimeByType(String procRtGuid, WorkflowActivityType workflowActivityType)
	{
		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put(ActivityRuntime.PROCRT_GUID, procRtGuid);
		searchConditionMap.put(ActivityRuntime.ACT_TYPE, workflowActivityType);
		ActivityRuntime activity = DataServer.getSystemDataService().queryObject(ActivityRuntime.class, searchConditionMap);
		return activity;
	}

	protected List<ProcTrack> listActivityComment(String actrtGuid, String startNumber)
	{
		// TODO Auto-generated method stub
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(ProcTrack.ACTRT_GUID, actrtGuid);
		if (!StringUtils.isNullString(startNumber))
		{
			filter.put(ProcTrack.START_NUMBER, startNumber);
		}

		List<ProcTrack> trackList = DataServer.getSystemDataService().query(ProcTrack.class, filter);

		return trackList;
	}

	protected List<ProcTrack> listProcessRuntimeComment(String procRtGuid)
	{
		// TODO Auto-generated method stub
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(ProcTrack.PROCRT_GUID, procRtGuid);

		List<ProcTrack> trackList = DataServer.getSystemDataService().query(ProcTrack.class, filter);
		return trackList;
	}

	protected void deleteProcess(ProcessRuntime processRuntime) throws DynaDataException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("PROCRTGUID", processRuntime.getGuid());
		sds.delete(ProcessRuntime.class, paramMap, "deleteProcrt");
		sds.delete(ProcessRuntime.class, paramMap, "deleteActrt");
		sds.delete(ProcessRuntime.class, paramMap, "deleteTransition");
		sds.delete(ProcessRuntime.class, paramMap, "deleteTransrst");
		sds.delete(ProcessRuntime.class, paramMap, "removeLock");
		sds.delete(ProcessRuntime.class, paramMap, "deleteAttach");
		sds.delete(ProcessRuntime.class, paramMap, "deletePerformer");
		sds.delete(ProcessRuntime.class, paramMap, "deletePerformerActual");
		sds.delete(ProcessRuntime.class, paramMap, "deleteTrack");
		sds.delete(ProcessRuntime.class, paramMap, "deleteTrackAttach");

	}
}
