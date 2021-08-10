/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 活动操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.activity.app.application;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ActivityRuntimeApplication;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DecisionEnum;

/**
 * 结束节点相关操作
 * 
 * @author lizw
 *
 */
public class EndActivityApplication implements ActivityRuntimeApplication
{
	WFIImpl stubService = null;

	public EndActivityApplication(WFIImpl wfeImpl)
	{
		this.stubService = wfeImpl;
	}

	@Override
	public void finishActivity(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		this.stubService.getProcessRuntimeStub().closeProcess(activity.getProcessRuntimeGuid());
	}

	@Override
	public ActivityRuntime fireNextAcceptActivity(ActivityRuntime nextActRt) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		// wfeImpl.getActivityRuntimeStub().updateActrtStartNumber(nextActRt);
		// this.finishActivity(wfeImpl, nextActRt, DecisionEnum.ACCEPT);
		// wfeImpl.getFTS().createTransformQueue4WF(nextActRt);
		this.stubService.getActivityRuntimeStub().finishActivity(nextActRt, DecisionEnum.ACCEPT);

		return null;
	}

	@Override
	public ActivityRuntime fireRejectActivity(ActivityRuntime activity) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		this.stubService.getProcessRuntimeStub().closeProcess(activity.getProcessRuntimeGuid());

		return null;
	}

	@Override
	public boolean performActivityRuntime(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return true;
	}

}
