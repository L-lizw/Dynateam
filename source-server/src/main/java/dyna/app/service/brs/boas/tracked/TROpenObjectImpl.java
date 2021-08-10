/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TROpenObjectImpl
 * Wanglei 2011-11-14
 */
package dyna.app.service.brs.boas.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;

/**
 * for method boas.openObject
 * 
 * @author Wanglei
 * 
 */
public class TROpenObjectImpl extends TRFoundationImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.TRFoundationImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		if(tracker.getResult() instanceof Throwable){
			ObjectGuid objectGuid = (ObjectGuid) tracker.getParameters()[0];
			if (objectGuid == null)
			{
				return null;
			}
			return objectGuid.toString();
		}

		return super.getHandledObject(tracker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.TRFoundationImpl#getRenderedFoundationObject(dyna.app.core.track.Tracker)
	 */
	@Override
	protected FoundationObject getRenderedFoundationObject(Tracker tracker)
	{
		return (FoundationObject) tracker.getResult();
	}

}
