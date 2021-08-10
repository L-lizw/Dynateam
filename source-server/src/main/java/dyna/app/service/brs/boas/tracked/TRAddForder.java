package dyna.app.service.brs.boas.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.dto.Folder;

public class TRAddForder extends TRFoundationImpl
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		StringBuffer sb = new StringBuffer();

		Object[] params = tracker.getParameters();

		if (params[0] != null)
		{
			sb.append("instance:");
			sb.append(this.renderFoundation((FoundationObject) params[0]));
		}

		if (params[1] != null)
		{
			sb.append("   tofolder:");
			sb.append(((Folder) params[1]).getHierarchy());
		}

		return sb.toString();
	}
}
