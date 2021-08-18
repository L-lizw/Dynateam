package dyna.app.service.brs.wfi.activity;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.TransCondition;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ActRuntimeModeEnum;
import dyna.common.systemenum.WorkflowTransitionConditionType;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActivityRuntimeDBStub extends AbstractServiceStub<WFIImpl>
{

	protected ActivityRuntime getActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		try
		{
			return DataServer.getWorkFlowService().getActivityRuntime(actRtGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected ActivityRuntime getBeginActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			return DataServer.getWorkFlowService().getBeginActivityRuntime(procRtGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected ActivityRuntime getEndActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			return DataServer.getWorkFlowService().getEndActivityRuntime(procRtGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ActivityRuntime> listSortedPerformableActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ActivityRuntime.PROCRT_GUID, procRtGuid);

			List<ActivityRuntime> actList = sds.query(ActivityRuntime.class, filter, "selectPerformalActivityRuntime");
			return actList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ActivityRuntime> listCurrentActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ActivityRuntime.PROCRT_GUID, procRtGuid);
			filter.put(ActivityRuntime.ACT_MODE, ActRuntimeModeEnum.CURRENT);

			List<ActivityRuntime> actList = sds.query(ActivityRuntime.class, filter, "select");

			return actList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void setAsByPassActivity(ActivityRuntime activity) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			activity.setActMode(ActRuntimeModeEnum.BYPASS);
			activity.setFinished(true);

			sds.save(activity);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void setAsCurrentActivity(ActivityRuntime activity) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			activity.setActMode(ActRuntimeModeEnum.CURRENT);
			activity.setFinished(false);

			sds.save(activity);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void finishAllActivityInProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ActivityRuntime.PROCRT_GUID, procRtGuid);

			sds.update(ActivityRuntime.class, filter, "finishAllActivityRuntime");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ActivityRuntime> listActivityInProcessRuntime(String procRtGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(ActivityRuntime.PROCRT_GUID, procRtGuid);

			List<ActivityRuntime> activityList = sds.query(ActivityRuntime.class, searchConditionMap);

			return activityList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);

		}
	}

	protected List<ActivityRuntime> listAcceptableToActivityRuntime(String actRtGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(SystemObject.GUID, actRtGuid);
			searchConditionMap.put(TransCondition.CONDITION_TYPE, WorkflowTransitionConditionType.ACCEPT.name());

			List<ActivityRuntime> actList = sds.query(ActivityRuntime.class, searchConditionMap, "getPreviousActivityRuntime");

			return actList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 查询通过活动之后可到达的活动列表
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ActivityRuntime> listAcceptableFromActivityRuntime(String actRtGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(SystemObject.GUID, actRtGuid);
			searchConditionMap.put(TransCondition.CONDITION_TYPE, WorkflowTransitionConditionType.ACCEPT.name());

			List<ActivityRuntime> actList = sds.query(ActivityRuntime.class, searchConditionMap, "getNextActivityRuntime");

			return actList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 查询拒绝可到达的活动列表
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ActivityRuntime> listRejectableFromActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(SystemObject.GUID, actRtGuid);
			searchConditionMap.put(TransCondition.CONDITION_TYPE, WorkflowTransitionConditionType.REJECT.name());

			List<ActivityRuntime> actList = sds.query(ActivityRuntime.class, searchConditionMap, "getNextActivityRuntime");

			return actList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 更新流程开始时间
	 * 
	 * @param activityGuid
	 * @throws ServiceRequestException
	 */
	protected void updatenNextActrtStartTime(String activityGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, activityGuid);
		// start activity
		try
		{
			sds.update(ActivityRuntime.class, filter, "updateNextActrtStartTime");
		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			throw ServiceRequestException.createByException("ID_APP_WF_UNKNOW_ERROR", e);
		}
	}
	
	/**
	 * 结束当前活动,并更新startnumber
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	protected void updateActrtStartNumber(ActivityRuntime activity) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		int number = activity.getStartNumber() + 1;
		activity.setStartNumber(number);
		filter.put(SystemObject.GUID, activity.getGuid());
		filter.put(ActivityRuntime.START_NUMBER, activity.getStartNumber());
		sds.update(ActivityRuntime.class, filter, "updateActrtStartNumber");

		filter.clear();
		filter.put(SystemObject.GUID, activity.getGuid());

		int ret = sds.update(ActivityRuntime.class, filter, "finishActivityRuntime");
		if (0 == ret)
		{
			throw new ServiceRequestException("ID_WEB_WFFINISH_ERROR", "Can't finish activity.");
		}
	}
	
	/**
	 * 重置活动为normal状态
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	public void resetActivityRuntime(ActivityRuntime activity) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			activity.setFinished(false);
			sds.save(activity);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
