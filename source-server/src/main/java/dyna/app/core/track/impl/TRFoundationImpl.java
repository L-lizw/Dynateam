/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRFoundationImpl
 * Wanglei 2011-11-14
 */
package dyna.app.core.track.impl;

import dyna.app.core.track.Tracker;
import dyna.common.bean.data.FoundationObject;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 *
 */
public class TRFoundationImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		return this.renderFoundation(this.getRenderedFoundationObject(tracker));
	}

	protected FoundationObject getRenderedFoundationObject(Tracker tracker)
	{
		Object[] params = tracker.getParameters();
		if (params == null || params.length == 0)
		{
			return null;
		}

		for (Object object : params)
		{
			if (object instanceof FoundationObject)
			{
				return (FoundationObject) object;
			}
		}

		return null;
	}

	protected String renderFoundation(FoundationObject foundationObject)
	{
		if (foundationObject == null)
		{
			return null;
		}

		String id = StringUtils.convertNULLtoString(foundationObject.getId());
		String rid = StringUtils.convertNULLtoString(foundationObject.getRevisionId());
		String iid = StringUtils.convertNULLtoString(foundationObject.getIterationId());
		String name = StringUtils.convertNULLtoString(foundationObject.getName());
		return id + "/" + rid + "." + iid + "-" + name;
	}
}
