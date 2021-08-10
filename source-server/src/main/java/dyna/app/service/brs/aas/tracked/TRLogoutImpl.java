/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRLogoutImpl
 * Wanglei 2011-11-14
 */
package dyna.app.service.brs.aas.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.UserSignature;

/**
 * @author Wanglei
 *
 */
public class TRLogoutImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		Signature signature = tracker.getSignature();
		if (signature != null && signature instanceof UserSignature)
		{
			return ((UserSignature) signature).getApplicationType().name();
		}
		return "";
	}

}
