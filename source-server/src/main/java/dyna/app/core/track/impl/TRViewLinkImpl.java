/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRFoundationImpl
 * Wanglei 2011-11-14
 */
package dyna.app.core.track.impl;

import dyna.app.core.track.Tracker;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
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
public class TRViewLinkImpl extends TRFoundationImpl
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
			if (params != null && params.length > 0)
			{
				if (params[0] instanceof ObjectGuid)
				{
					try
					{
						ViewObject relation = boas.getRelation((ObjectGuid) params[0]);
						if (relation != null)
						{
							ObjectGuid end1ObjectGuid = relation.getEnd1ObjectGuid();
							if (end1ObjectGuid != null)
							{
								FoundationObject objectByGuid = boas.getObjectByGuid(end1ObjectGuid);
								String renderFoundation = this.renderFoundation(objectByGuid);
								sb.append(StringUtils.convertNULLtoString(renderFoundation));
							}
							templateName = relation.getName();
						}

					}
					catch (ServiceRequestException e)
					{
						DynaLogger.info("write log error,End1:" + e.getMessage());
					}
				}
				else if (params[0] instanceof ViewObject)
				{
					ViewObject relation = (ViewObject) params[0];
					if (relation != null)
					{
						ObjectGuid end1ObjectGuid = relation.getEnd1ObjectGuid();
						if (end1ObjectGuid != null)
						{
							try
							{
								FoundationObject objectByGuid = boas.getObjectByGuid(end1ObjectGuid);
								String renderFoundation = this.renderFoundation(objectByGuid);
								sb.append(StringUtils.convertNULLtoString(renderFoundation));
							}
							catch (ServiceRequestException e)
							{
								DynaLogger.info("write log error,End1:" + e.getMessage());
							}
						}
						templateName = relation.getName();
					}
				}
				else if (params[0] instanceof BOMView)
				{
					BOMView relation = (BOMView) params[0];
					if (relation != null)
					{
						ObjectGuid end1ObjectGuid = relation.getEnd1ObjectGuid();
						if (end1ObjectGuid != null)
						{
							try
							{
								FoundationObject objectByGuid = boas.getObjectByGuid(end1ObjectGuid);
								String renderFoundation = this.renderFoundation(objectByGuid);
								sb.append(StringUtils.convertNULLtoString(renderFoundation));
							}
							catch (ServiceRequestException e)
							{
								DynaLogger.info("write log error,End1:" + e.getMessage());
							}
						}
						templateName = relation.getName();
					}
				}
			}

			sb.append(", END2:");
			FoundationObject end2FoundationObject = this.getEnd2FoundationObject(params, boas);
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

	protected FoundationObject getEnd1FoundationObject(Object[] params, BOAS boas)
	{
		if (params != null && params.length > 0)
		{
			if (params[0] instanceof ObjectGuid)
			{
				try
				{
					return boas.getObjectByGuid((ObjectGuid) params[0]);
				}
				catch (ServiceRequestException e)
				{
					DynaLogger.info("write log error,End1:" + e.getMessage());
				}
			}
		}

		return null;
	}

	protected FoundationObject getEnd2FoundationObject(Object[] params, BOAS boas)
	{
		if (params != null && params.length > 1)
		{
			if (params[1] instanceof ObjectGuid)
			{
				try
				{
					return boas.getObjectByGuid((ObjectGuid) params[1]);
				}
				catch (ServiceRequestException e)
				{
					DynaLogger.info("write log error,End2:" + e.getMessage());
				}
			}
		}

		return null;
	}

}
