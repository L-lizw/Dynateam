/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRPerfActImpl
 * Wanglei 2011-11-21
 */
package dyna.app.service.brs.wfi.track;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DecisionEnum;
import dyna.net.service.brs.WFI;

/**
 * @author Wanglei
 * 
 */
public class TRPerfActImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		Object[] parameters = tracker.getParameters();
		if (parameters != null && parameters.length > 3)
		{

			WFI wfe = null;
			try
			{
				wfe = tracker.getServiceContext().allocatService(WFI.class);

				((WFIImpl) wfe).setSignature(tracker.getServiceContext().getServerContext()
						.getSystemInternalSignature());
				ActivityRuntime activityRuntime = wfe.getActivityRuntime((String) parameters[0]);
				if (activityRuntime != null)
				{
					return activityRuntime.getTitle(tracker.getServiceContext().getServerContext()
							.getSystemInternalSignature().getLanguageEnum())
							+ "|" + ((DecisionEnum) parameters[2]).name() + ":" + parameters[1];
				}
				else
				{
					return parameters[0] + "|" + ((DecisionEnum) parameters[2]).name() + ":" + parameters[1];

				}

			}
			catch (ServiceNotFoundException e)
			{
				DynaLogger.info("write log error:" + e.getMessage());
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (wfe != null)
				{
					tracker.getServiceContext().releaseService(wfe);
				}
			}
		}
		return super.getHandledObject(tracker);

	}

}
