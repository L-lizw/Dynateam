/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRDSSFileInfoImpl
 * Wanglei 2011-11-21
 */
package dyna.app.service.brs.dss.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.common.dto.DSSFileInfo;

/**
 * @author Wanglei
 *
 */
public class TRDSSFileInfoImpl extends DefaultTrackerRendererImpl
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
		Object[] parameters = tracker.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for (Object param : parameters)
			{
				if (param instanceof DSSFileInfo)
				{
					fileInfo = (DSSFileInfo) param;
					break;
				}
			}
		}
		if (fileInfo == null)
		{
			Object result = tracker.getResult();
			if (result != null && result instanceof DSSFileInfo)
			{
				fileInfo = (DSSFileInfo) result;
			}
		}
		if (fileInfo != null)
		{
			return this.getFileName(fileInfo);
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
