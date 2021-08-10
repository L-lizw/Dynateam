package dyna.app.service.brs.wfi.activity.app.application;

import java.util.List;
import java.util.Stack;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ActivityRuntimeApplication;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.TransRestriction;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ActRuntimeModeEnum;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowRouteModeType;
import dyna.common.systemenum.WorkflowRouteType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * 路由节点
 * 
 * @author lizw
 *
 */

public class RouteActivityApplication implements ActivityRuntimeApplication
{
	private WFIImpl stubService = null;

	public RouteActivityApplication(WFIImpl wfeImpl)
	{
		this.stubService = wfeImpl;
	}

	@Override
	public void finishActivity(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public ActivityRuntime fireNextAcceptActivity(ActivityRuntime nextActRt) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		ActivityRuntime returnActrt = null;
		TransRestriction restriction = this.stubService.getRoutRestrictionStub().getRoutRestriction(nextActRt.getGuid());

		WorkflowRouteType rtcType = restriction.getRestrictionType();
		WorkflowRouteModeType cntType = restriction.getConnectionType();
		if (rtcType == WorkflowRouteType.SPLIT)
		{
			this.stubService.getActivityRuntimeStub().finishActivity(nextActRt, DecisionEnum.ACCEPT);
			returnActrt = this.stubService.getActivityRuntimeStub().fireNextAcceptActivity(nextActRt);

		}
		else if (rtcType == WorkflowRouteType.JOIN)
		{
			if (cntType == WorkflowRouteModeType.AND)
			{
				boolean isAllFinished = true;
				List<ActivityRuntime> list = this.stubService.getActivityRuntimeStub().listAcceptableToActivityRuntime(nextActRt.getGuid());
				for (ActivityRuntime rstAct : list)
				{
					if (!rstAct.isFinished())
					{
						isAllFinished = false;
					}
				}
				if (isAllFinished)
				{
					if (!nextActRt.isFinished())
					{
						this.stubService.getActivityRuntimeStub().finishActivity(nextActRt, DecisionEnum.ACCEPT);
						returnActrt = this.stubService.getActivityRuntimeStub().fireNextAcceptActivity(nextActRt);
					}
				}
			}
			else if (cntType == WorkflowRouteModeType.XOR)
			{
				if (nextActRt.isFinished())
				{
					return null;
				}

				this.stubService.getActivityRuntimeStub().finishActivity(nextActRt, DecisionEnum.ACCEPT);

				Stack<ActivityRuntime> routeActStack = new Stack<ActivityRuntime>();
				ActivityRuntime splitRoute = this.normalizeActivity(routeActStack, nextActRt);

				if (splitRoute == null)
				{
					splitRoute = new ActivityRuntime();
				}

				this.normalizeRouteActivity(routeActStack, nextActRt, splitRoute);
				returnActrt = this.stubService.getActivityRuntimeStub().fireNextAcceptActivity(nextActRt);
			}
		}
		return returnActrt;
	}

	/**
	 * 进入 join xor route 之后 将route之间的未完成的current活动置为normal
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	private void normalizeRouteActivity(Stack<ActivityRuntime> routeActStack, ActivityRuntime activity, ActivityRuntime splitRoute) throws ServiceRequestException
	{
		String actGuid = activity.getGuid();

		if (actGuid.equals(splitRoute.getGuid()))
		{
			return;
		}

		List<ActivityRuntime> toActList = this.stubService.getActivityRuntimeStub().listAcceptableToActivityRuntime(actGuid);
		if (!SetUtils.isNullList(toActList))
		{
			ActivityRuntime tempAct = null;
			for (ActivityRuntime toAct : toActList)
			{
				tempAct = this.normalizeActivity(routeActStack, toAct);
				if (tempAct != null && StringUtils.isNullString(splitRoute.getGuid()))
				{
					splitRoute.putAll(tempAct);
				}
				this.normalizeRouteActivity(routeActStack, toAct, splitRoute);
			}

		}

	}

	public ActivityRuntime normalizeActivity(Stack<ActivityRuntime> routeActStack, ActivityRuntime activity) throws ServiceRequestException
	{
		String actGuid = activity.getGuid();
		WorkflowActivityType actType = activity.getActType();
		ActRuntimeModeEnum actMode = activity.getActMode();
		if (actType == WorkflowActivityType.ROUTE)
		{
			ActivityRuntime splitRoute = null;
			TransRestriction restriction = this.stubService.getRoutRestrictionStub().getRoutRestriction(actGuid);
			if (restriction.getRestrictionType() == WorkflowRouteType.JOIN)
			{
				routeActStack.push(activity);
			}
			else if (!routeActStack.isEmpty())
			{
				splitRoute = routeActStack.pop();
			}

			if (routeActStack.isEmpty())
			{
				return splitRoute;
			}

		}
		else if (actMode == ActRuntimeModeEnum.CURRENT)
		{
			this.stubService.getActivityRuntimeStub().resetActivityRuntime(activity);
		}

		return null;
	}

	@Override
	public ActivityRuntime fireRejectActivity(ActivityRuntime activity) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean performActivityRuntime(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return true;
	}

}
