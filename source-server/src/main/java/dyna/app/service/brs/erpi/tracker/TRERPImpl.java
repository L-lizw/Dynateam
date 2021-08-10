package dyna.app.service.brs.erpi.tracker;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;

public class TRERPImpl extends DefaultTrackerRendererImpl
{
	@Override
	public String getHandledObject(Tracker tracker)
	{
		Object[] params = tracker.getParameters();
		if ((params == null) || (params.length == 0))
		{
			return null;
		}
		for (Object object : params)
		{
			if ((object instanceof String))
			{
				return (String) object;
			}
		}
		return null;
	}
}
