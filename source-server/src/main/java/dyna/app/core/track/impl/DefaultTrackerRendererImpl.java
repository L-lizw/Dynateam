/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DefaultTrackerRendererImpl
 * Wanglei 2011-11-14
 */
package dyna.app.core.track.impl;

import java.rmi.server.RemoteServer;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.TrackerDescription;
import dyna.app.core.track.TrackerRenderer;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.UserSignature;

/**
 * @author Wanglei
 *
 */
public class DefaultTrackerRendererImpl implements TrackerRenderer
{
	protected TrackerDescription	desc	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.TrackerRenderer#getSessionId(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getSessionId(Tracker tracker)
	{
		if (tracker.getSignature() == null)
		{
			return "<unknown>";
		}
		String sid = tracker.getSignature().getCredential();
		sid = sid.replace("-", "").toUpperCase();
		return sid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.TrackerRenderer#getAddress(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getAddress(Tracker tracker)
	{
		Signature signature = tracker.getSignature();
		if (signature == null)
		{
			try
			{
				return RemoteServer.getClientHost();
			}
			catch (Exception e)
			{
			}
			return "127.0.0.1/localhost";
		}

		return signature.getIPAddress();
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.track.TrackerRenderer#getOperatorInfo(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getOperatorInfo(Tracker tracker)
	{
		Signature signature = tracker.getSignature();
		if (signature == null)
		{
			return "<unknown>";
		}
		else if (signature instanceof ModuleSignature)
		{
			return "<module>" + ((ModuleSignature) signature).getModuleId();
		}

		UserSignature us = (UserSignature) signature;
		return us.getUserId() + "-" + us.getUserName() + "::" + us.getLoginGroupName() + "(" + us.getLoginRoleName() + ")";
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.track.TrackerRenderer#getOperatorGuid(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getOperatorGuid(Tracker tracker)
	{
		Signature signature = tracker.getSignature();
		if (signature == null || signature instanceof ModuleSignature)
		{
			return tracker.getServiceContext().getServerContext().getSystemInternalSignature().getUserGuid();
		}
		return ((UserSignature) signature).getUserGuid();
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.track.TrackerRenderer#getJobDescription(dyna.app.core.track.Tracker)
	 */
	@Override
	public TrackerDescription getTrackerDescription()
	{
		return this.desc;
	}

	@Override
	public void setTrackerDescription(TrackerDescription desc)
	{
		this.desc = desc;
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.track.TrackerRenderer#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		Object[] parameters = tracker.getParameters();
		if (parameters == null || parameters.length == 0)
		{
			return "";
		}
		StringBuffer sb = new StringBuffer();

		for (Object param : parameters)
		{
			sb.append(param + "|");
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.track.TrackerRenderer#getResult(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getResult(Tracker tracker)
	{
		Object result = tracker.getResult();
		if (result == null)
		{
			return "Successful";
		}

		if (result instanceof Throwable)
		{
			String msg = ((Throwable) result).getMessage();

			if (result instanceof ServiceRequestException)
			{
				msg = ((ServiceRequestException) result).getMsrId() + (StringUtils.isNullString(msg) ? "" : "|" + msg);
			}

			return "Failed, Exception: " + msg;
		}

		return "Successful";
	}

}
