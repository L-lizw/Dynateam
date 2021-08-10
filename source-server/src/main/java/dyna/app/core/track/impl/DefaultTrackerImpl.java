/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DefaultTracker
 * Wanglei 2011-11-11
 */
package dyna.app.core.track.impl;

import java.lang.reflect.Method;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.TrackerDescription;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.TrackerRenderer;
import dyna.app.server.context.ServiceContext;
import dyna.common.Version;
import dyna.common.dto.SysTrack;
import dyna.common.log.DynaLogger;
import dyna.common.syslog.DynaSysLogger;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.UserSignature;

/**
 * @author Wanglei
 * 
 */
public class DefaultTrackerImpl implements Tracker
{

	private ServiceContext		context		= null;
	private Signature			signature	= null;
	private Method				method		= null;
	private Object[]			parameters	= null;
	private Object				result		= null;
	private TrackerRenderer		renderer	= null;
	private TrackerPersistence	persistence	= null;

	public DefaultTrackerImpl(ServiceContext context, Signature signature, Method method, Object[] parameters, Object result, TrackerRenderer renderer,
			TrackerPersistence persistence)
	{
		this.context = context;
		this.signature = signature;
		this.method = method;
		this.parameters = parameters;
		this.result = result;
		this.renderer = renderer;
		this.persistence = persistence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getServiceContext()
	 */
	@Override
	public ServiceContext getServiceContext()
	{
		return this.context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getSession()
	 */
	@Override
	public Signature getSignature()
	{
		return this.signature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getParameters()
	 */
	@Override
	public Object[] getParameters()
	{
		return this.parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getMethod()
	 */
	@Override
	public Method getMethod()
	{
		return this.method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getResult()
	 */
	@Override
	public Object getResult()
	{
		return this.result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getTrackerRenderer()
	 */
	@Override
	public TrackerRenderer getTrackerRenderer()
	{
		return this.renderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getPersistence()
	 */
	@Override
	public TrackerPersistence getPersistence()
	{
		return this.persistence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#persist()
	 */
	@Override
	public void persist() throws Exception
	{
		if (this.getPersistence() != null)
		{
			this.persistence.persist(this);
			return;
		}

		TrackerRenderer trackerRenderer = this.getTrackerRenderer();

		// TODO persist track
		String sid = "";
		String whom = "";
		String crtUserGuid = "";
		String where = "";
		String objectStr = "";
		String what = "";
		String resultStr = "";
		if (trackerRenderer != null)
		{
			sid = StringUtils.convertNULLtoString(trackerRenderer.getSessionId(this));
			whom = StringUtils.convertNULLtoString(trackerRenderer.getOperatorInfo(this));
			crtUserGuid = StringUtils.convertNULLtoString(trackerRenderer.getOperatorGuid(this));
			where = StringUtils.convertNULLtoString(trackerRenderer.getAddress(this));
			objectStr = StringUtils.convertNULLtoString(trackerRenderer.getHandledObject(this));
			TrackerDescription trackerDescription = trackerRenderer.getTrackerDescription();
			if (trackerDescription != null)
			{
				what = StringUtils.convertNULLtoString(trackerDescription.getDescription(this));
			}
			resultStr = StringUtils.convertNULLtoString(trackerRenderer.getResult(this));
		}

		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug(whom + "/" + crtUserGuid + "!" + where + "!" + what + "!" + objectStr + "!" + resultStr);
		}

		if (StringUtils.isNullString(crtUserGuid))
		{
			return;
		}

		SysTrack track = new SysTrack();
		track.put(SysTrack.SID, sid);
		track.put(SysTrack.BY_WHOM, whom);
		track.put(SysTrack.AT_WHERE, where);
		track.put(SysTrack.DO_WHAT, what);
		if (objectStr.length() > 512)
		{
			objectStr = objectStr.substring(0, 512);
		}
		track.put(SysTrack.TG_OBJECT, objectStr);
		track.put(SysTrack.RESULT, resultStr);
		track.put(SysTrack.CREATE_USER_GUID, crtUserGuid);

		DataServer.getSystemDataService().save(track);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.Tracker#getSyslogString()
	 */
	@Override
	public void getSyslogString() throws Exception
	{
		String productName = "ProductName:" + StringUtils.converNULLtoSign(Version.getProductName(), "^");
		String version = "ProductVersion:" + StringUtils.converNULLtoSign(Version.getVersionInfo(), "^");
		String date = "Date:" + DateFormat.format(new java.util.Date(), DateFormat.PTN_YMD);
		String time = "Time:" + DateFormat.format(new java.util.Date(), "HH:mm:ss");
		String userId = "UserId:";
		String userGroup = "UserGroup:";
		String where = "IP:";
		String operation = "Operation:";
		String content = "Content:";
		String resultStr = "Result:";
		String returnString = "";
		if (this.getPersistence() != null)
		{
			this.persistence.persist(this);
			return;
		}
		TrackerRenderer trackerRenderer = this.getTrackerRenderer();
		String whom = "";
		String crtUserGuid = "";

		String what = "";

		if (trackerRenderer != null)
		{
			whom = StringUtils.convertNULLtoString(trackerRenderer.getOperatorInfo(this));
			crtUserGuid = StringUtils.converNULLtoSign(StringUtils.convertNULLtoString(trackerRenderer.getOperatorGuid(this)), "^");
			where = where + StringUtils.converNULLtoSign(StringUtils.convertNULLtoString(trackerRenderer.getAddress(this)), "^");
			content = content + StringUtils.converNULLtoSign(StringUtils.convertNULLtoString(trackerRenderer.getHandledObject(this)), "^");// 应用类型
			TrackerDescription trackerDescription = trackerRenderer.getTrackerDescription();
			if (trackerDescription != null)
			{
				what = StringUtils.convertNULLtoString(trackerDescription.getDescription(this));
			}
			resultStr = resultStr + StringUtils.converNULLtoSign(StringUtils.convertNULLtoString(trackerRenderer.getResult(this)), "^");
		}

		if (StringUtils.isNullString(crtUserGuid))
		{
			return;
		}
		// 取得系统语言
		LanguageEnum lang = context.getServerContext().getServerConfig().getLanguage();

		if (this.signature instanceof UserSignature)
		{
			lang = ((UserSignature) this.signature).getLanguageEnum();
		}
		operation = operation + StringUtils.converNULLtoSign(StringUtils.getMsrTitle(what, lang.getType()), "^");
		String[] user = StringUtils.splitStringWithDelimiter("::", whom);

		if (user != null && user.length == 2)
		{
			userId = userId + user[0];
			userGroup = userGroup + user[1];
		}
		returnString = productName + "\t" + version + "\t" + userId + "\t" + userGroup + "\t" + where + "\t" + operation + "\t" + content + "\t" + resultStr + "\t" + date + "\t"
				+ time;
		// 保存后将日志写入远程服务器
		DynaSysLogger.info(returnString);

	}
}
