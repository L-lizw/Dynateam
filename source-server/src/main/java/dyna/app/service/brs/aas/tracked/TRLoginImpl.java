/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRLoginImpl
 * Wanglei 2011-11-14
 */
package dyna.app.service.brs.aas.tracked;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.common.exception.AuthorizeException;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.UserSignature;

/**
 * @author Wanglei
 * 
 */
public class TRLoginImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getSessionId(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getSessionId(Tracker tracker)
	{
		Object result = tracker.getResult();
		if (result instanceof String)
		{
			return (String) result;
		}
		return super.getSessionId(tracker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getOperatorInfo(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getOperatorInfo(Tracker tracker)
	{
		Object[] params = tracker.getParameters();
		if (params != null && params.length > 2)
		{
			return params[0] + "::" + params[1] + "(" + params[2] + ")";
		}
		return super.getOperatorInfo(tracker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getOperatorGuid(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getOperatorGuid(Tracker tracker)
	{
		Object result = tracker.getResult();
		if (result instanceof String)
		{
			CredentialManager cm = tracker.getServiceContext().getServerContext().getCredentialManager();
			try
			{
				Signature signature = cm.authenticate((String) result);
				if (signature instanceof UserSignature)
				{
					return ((UserSignature) signature).getUserGuid();
				}
			}
			catch (AuthorizeException e)
			{
			}
		}
		return super.getOperatorGuid(tracker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getAddress(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getAddress(Tracker tracker)
	{
		Object result = tracker.getResult();
		if (result instanceof String)
		{
			CredentialManager cm = tracker.getServiceContext().getServerContext().getCredentialManager();
			try
			{
				Signature signature = cm.authenticate((String) result);
				if (signature != null)
				{
					return signature.getIPAddress();
				}
				return null;
			}
			catch (AuthorizeException e)
			{
			}
		}
		String ip = null;
		if (tracker.getParameters().length > 5)
		{
			ip = (String) tracker.getParameters()[4];
			if (ip != null)
			{
				return ip;
			}
		}

		return super.getAddress(tracker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		ApplicationTypeEnum type = ApplicationTypeEnum.STANDARD;
		Object[] params = tracker.getParameters();
		if (params != null)
		{
			for (Object param : params)
			{
				if (param != null && param instanceof ApplicationTypeEnum)
				{
					type = ((ApplicationTypeEnum) param);
					break;
				}
			}
		}
		return type.name();
	}

}
