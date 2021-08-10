package dyna.app.service.brs.boas.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.util.StringUtils;

public class TRUpdateOwnerImpl extends TRFoundationImpl
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
		StringBuffer sb = new StringBuffer();
		if (parameters.length > 0)
		{
			sb.append(this.renderFoundation((FoundationObject) parameters[0]));
		}

		if (parameters.length > 1)
		{
			sb.append(": " + StringUtils.convertNULLtoString(parameters[1]));
		}

		if (parameters.length > 2)
		{
			sb.append("->" + StringUtils.convertNULLtoString(parameters[2]));
		}

		return sb.toString();
	}
}
