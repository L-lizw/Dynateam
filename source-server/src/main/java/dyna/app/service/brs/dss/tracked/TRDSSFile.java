package dyna.app.service.brs.dss.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.common.dto.DSSFileInfo;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.net.service.brs.DSS;

public class TRDSSFile extends DefaultTrackerRendererImpl
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		DSSFileInfo fileInfo = null;
		DSS dss = null;
		try
		{
			dss = tracker.getServiceContext().allocatService(DSS.class);
			Object[] params = tracker.getParameters();
			fileInfo = dss.getFile((String) params[0]);
			if (fileInfo != null)
			{
				return this.getFileName(fileInfo);
			}
		}
		catch (ServiceNotFoundException e)
		{
			DynaLogger.info("write log error:" + e.getMessage());
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.info("write log error:" + e.getMessage());
		}
		finally
		{
			if (dss != null)
			{
				tracker.getServiceContext().releaseService(dss);
			}
		}
		return super.getHandledObject(tracker);
	}

	protected String getFileName(DSSFileInfo fileInfo)
	{
		if (fileInfo == null)
		{
			return null;
		}
		return fileInfo.getName();
	}
}
