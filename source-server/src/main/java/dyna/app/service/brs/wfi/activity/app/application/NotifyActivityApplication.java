/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 活动操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.activity.app.application;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ActivityRuntimeApplication;
import dyna.app.service.brs.wfi.activity.app.ProcApp;
import dyna.app.service.brs.wfi.activity.app.ProcAppFactory;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ActRuntimeModeEnum;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通知节点相关操作
 * 
 * @author lizw
 *
 */
@Component
public class NotifyActivityApplication implements ActivityRuntimeApplication
{
	@Autowired
	private WFIImpl stubService ;

	@Override
	public void finishActivity(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		this.stubService.getAttachStub().excuteERPWorkflow(activity.getProcessRuntimeGuid(), activity.getGuid());
	}

	@Override
	public ActivityRuntime fireNextAcceptActivity(ActivityRuntime nextActRt) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		ActivityRuntime returnActrt = null;
		if (nextActRt.getActMode() == ActRuntimeModeEnum.BYPASS)
		{
			returnActrt = this.stubService.getActivityRuntimeStub().fireNextAcceptActivity(nextActRt);
			return returnActrt;
		}
		ProcApp procApp = ProcAppFactory.getProcAppFactory().getProcApp(nextActRt.getApplicationName());
		boolean isSkip = false;
		if (procApp != null)
		{
			Object execute = procApp.execute(this.stubService, nextActRt);
			if (execute != null && WorkflowApplicationType.ACTION.name().equalsIgnoreCase(nextActRt.getApplicationName()))
			{
				if (execute instanceof Boolean && (Boolean) execute == true)
				{
					return nextActRt;
				}
			}

			if (execute != null)
			{
				if (execute instanceof Boolean && (Boolean) execute == true)
				{
					isSkip = true;
				}
			}
		}

		DecisionEnum decide = isSkip ? DecisionEnum.SKIP : DecisionEnum.ACCEPT;

		this.stubService.getActivityRuntimeStub().finishActivity(nextActRt, decide);

		returnActrt = this.stubService.getActivityRuntimeStub().fireNextAcceptActivity(nextActRt);

		return returnActrt;
	}

	@Override
	public ActivityRuntime fireRejectActivity(ActivityRuntime rejAct) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		ProcApp procApp = ProcAppFactory.getProcAppFactory().getProcApp(rejAct.getApplicationName());
		boolean isSkip = false;
		if (procApp != null)
		{
			Object execute = procApp.execute(this.stubService, rejAct);
			if (execute != null && WorkflowApplicationType.ACTION.name().equalsIgnoreCase(rejAct.getApplicationName()))
			{
				if (execute instanceof Boolean && (Boolean) execute == true)
				{
					return rejAct;
				}
			}

			if (rejAct.getActType() == WorkflowActivityType.NOTIFY && execute != null)
			{
				if (execute instanceof Boolean && (Boolean) execute == true)
				{
					isSkip = true;
				}
			}

		}

		DecisionEnum decide = isSkip ? DecisionEnum.SKIP : DecisionEnum.REJECT;
		this.stubService.getActivityRuntimeStub().finishActivity(rejAct, decide);
		return null;
	}

	@Override
	public boolean performActivityRuntime(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return true;
	}

}
