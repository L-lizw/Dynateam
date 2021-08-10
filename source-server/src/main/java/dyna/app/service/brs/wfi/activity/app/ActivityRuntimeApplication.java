package dyna.app.service.brs.wfi.activity.app;

import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DecisionEnum;

/**
 * 流程活动的执行
 * 
 * @author lizw
 *
 */

public interface ActivityRuntimeApplication
{
	/**
	 * 结束当前活动
	 * 
	 * @param wfeImpl
	 * @param activity
	 * @param decide
	 * @throws ServiceRequestException
	 */
	public void finishActivity(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException;

	/**
	 * 当前活动同意后执行后续活动
	 * 
	 * @param activity
	 * @return
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime fireNextAcceptActivity(ActivityRuntime activity) throws ServiceRequestException;

	/**
	 * 当前活动执行拒绝后的后续操作
	 * 
	 * @param activity
	 * @param rejectToActRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime fireRejectActivity(ActivityRuntime activity) throws ServiceRequestException;

	/**
	 * 人为活动节点特有的接口，用于执行流程
	 * 
	 * @param activity
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean performActivityRuntime(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException;

}
