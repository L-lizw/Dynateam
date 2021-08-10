/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRFileTransImpl
 * Wanglei 2011-11-21
 */
package dyna.app.service.brs.dss.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.common.dto.DSSFileTrans;

/**
 * @author Wanglei
 *
 */
public class TRFileTransImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		Object result = tracker.getResult();
		if (result != null && result instanceof DSSFileTrans)
		{
			return ((DSSFileTrans) result).getFileName();
		}
		return super.getHandledObject(tracker);
	}

}
