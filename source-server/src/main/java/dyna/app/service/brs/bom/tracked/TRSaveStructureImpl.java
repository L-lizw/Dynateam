/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRFoundationImpl
 * Wanglei 2011-11-14
 */
package dyna.app.service.brs.bom.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.BOAS;

/**
 * @author Wanglei
 * 
 */
public class TRSaveStructureImpl extends TRFoundationImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		// return this.renderFoundation(this.getRenderedFoundationObject(tracker));
		StringBuffer sb = new StringBuffer();
		BOAS boas = null;
		try
		{

			sb.append("END1:");

			Object[] params = tracker.getParameters();
			boas = tracker.getServiceContext().allocatService(BOAS.class);

			((BOASImpl) boas).setSignature(tracker.getServiceContext().getServerContext().getSystemInternalSignature());
			String templateName = null;
			FoundationObject end2FoundationObject = null;

			if (params != null && params.length > 0)
			{
				if (params[0] instanceof StructureObject)
				{
					try
					{
						ViewObject relation = boas.getRelation(((StructureObject) params[0]).getViewObjectGuid());
						if (relation != null)
						{
							ObjectGuid end1ObjectGuid = relation.getEnd1ObjectGuid();
							if (end1ObjectGuid != null)
							{
								FoundationObject objectByGuid = boas.getObjectByGuid(end1ObjectGuid);
								String renderFoundation = this.renderFoundation(objectByGuid);
								sb.append(StringUtils.convertNULLtoString(renderFoundation));
							}

							end2FoundationObject = boas.getObjectByGuid(((StructureObject) params[0]).getEnd2ObjectGuid());

							templateName = relation.getName();
						}

					}
					catch (ServiceRequestException e)
					{
						DynaLogger.info("write log error,End1:" + e.getMessage());
					}
				}
			}

			sb.append(", END2:");

			sb.append(StringUtils.convertNULLtoString(this.renderFoundation(end2FoundationObject)));

			sb.append(", TEMPLATENAME:");
			sb.append(StringUtils.convertNULLtoString(templateName));
		}
		catch (ServiceNotFoundException e)
		{
			DynaLogger.info("write log error:" + e.getMessage());
		}
		finally
		{
			if (boas != null)
			{
				tracker.getServiceContext().releaseService(boas);
			}
		}
		return sb.toString();
	}

}
