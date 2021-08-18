/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SessionStub
 * Wanglei 2011-4-20
 */
package dyna.app.service.brs.lic;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.conf.ConfigurableServerImpl;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Wanglei
 * 
 */
@Component
public class SessionStub extends AbstractServiceStub<LICImpl>
{

	protected boolean hasLicence(String moduleName) throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getLicenseDaemon().hasLicence(moduleName);
	}

	protected void kickUser(String sessionId) throws ServiceRequestException
	{
		try
		{
			this.stubService.administrativeAuthorize(this.stubService.getUserSignature(), this.stubService.getAAS());
			this.deleteSessionInside(sessionId);
		}
		catch (AuthorizeException e)
		{
			throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, e);
		}
	}

	protected List<Session> listLicensedOccupant() throws ServiceRequestException
	{
		List<Session> list = this.listSession();

		if (list != null)
		{
			Set<String> sessionSet = new HashSet<String>();
			for (int i = list.size() - 1; i > -1; i--)
			{
				Session session = list.get(i);
				if (ApplicationTypeEnum.INTERNAL.equals(session.getAppType()) || ApplicationTypeEnum.MONITOR.equals(session.getAppType()))
				{
					list.remove(i);
				}
				else
				{
					String key = "";
					if (session.getAppType() == ApplicationTypeEnum.WEB || session.getAppType() == ApplicationTypeEnum.WEBWX)
					{
						key = session.getUserId() + "@" + session.getHostName() + ".WEB";
					}
					else
					{
						key = session.getUserId() + "@" + session.getHostName() + ".BASE";
					}
					if (sessionSet.contains(key))
					{
						list.remove(i);
					}
					else
					{
						sessionSet.add(key);
					}
				}
			}
		}

		return list;
	}

	protected Session getSession(String sessionId) throws ServiceRequestException
	{
		try
		{
			Session session = DataServer.getSystemDataService().get(Session.class, sessionId);
			return session;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<Session> listUserSession(String userGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			return sds.listFromCache(Session.class, new FieldValueEqualsFilter<Session>("USERGUID", userGuid));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<Session> listSession() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<Session> list = sds.listFromCache(Session.class, null);
			if (list != null)
			{
				for (Session ss : list)
				{
					decodeSession(ss);
				}
			}
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void decodeSession(Session ss) throws ServiceRequestException
	{
		User user = DataServer.getSystemDataService().get(User.class, ss.getUserGuid());
		ss.put("USERID", user.getUserId());
		ss.put("USERNAME", user.getUserName());
		Group group = DataServer.getSystemDataService().get(Group.class, ss.getLoginGroupGuid());
		ss.put("LOGINGROUPID", group.getGroupId());
		ss.put("LOGINGROUPNAME", group.getGroupName());
		Role role = DataServer.getSystemDataService().get(Role.class, ss.getLoginRoleGuid());
		ss.put("LOGINROLEID", role.getRoleId());
		ss.put("LOGINROLENAME", role.getRoleName());
	}

	protected void clearSession() throws ServiceRequestException
	{
		Signature sgn = this.stubService.getSignature();
		if (sgn instanceof ModuleSignature)
		{
			CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();
			String ftpc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_SESSION);
			if (sgn.getCredential().equals(ftpc))
			{
				this.clearSessionInside();
			}
		}
		else
		{
			try
			{
				this.stubService.administrativeAuthorize(this.stubService.getUserSignature(), this.stubService.getAAS());
				this.clearSessionInside();
			}
			catch (AuthorizeException e)
			{
				throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, e);
			}

			throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, new AuthorizeException("unauthorized signature"));
		}

	}

	protected void clearSessionInside() throws ServiceRequestException
	{
		List<Session> listSession = this.listSession();
		if (SetUtils.isNullList(listSession))
		{
			return;
		}

		for (Session session : listSession)
		{
			this.deleteSessionInside(session.getGuid());
		}
	}

	protected void deleteSession(String sessionId) throws ServiceRequestException
	{
		Signature sgn = this.stubService.getSignature();
		if (sgn instanceof ModuleSignature)
		{
			CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();
			String ftpc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_SESSION);
			if (sgn.getCredential().equals(ftpc))
			{
				this.deleteSessionInside(sessionId);
			}
		}
		else
		{
			try
			{
				this.stubService.administrativeAuthorize(this.stubService.getUserSignature(), this.stubService.getAAS());
				this.deleteSessionInside(sessionId);
			}
			catch (AuthorizeException e)
			{
				throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, e);
			}

			throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, new AuthorizeException("unauthorized signature"));
		}

	}

	public void deleteSessionInside(String sessionId) throws ServiceRequestException
	{
		try
		{
			try
			{
				User user = this.stubService.getAAS().getUser(this.stubService.getSession(sessionId).getUserGuid());
				if (user != null)
				{
					DynaLogger.debug("[deleteSessionInside]" + " userId: " + user.getUserId() + " sessionId: " + sessionId, new Exception());
				}
			}
			catch (Exception e)
			{
				DynaLogger.error("[deleteSessionInside]", e);
			}

			Session session = this.getSession(sessionId);
			DataServer.getSystemDataService().delete(Session.class, sessionId);

			this.serviceContext.getServerContext().removeSessionUpdateTime(sessionId);
			this.serviceContext.getServerContext().getCredentialManager().unbind(sessionId);

			if (session != null && session.getAppType() != ApplicationTypeEnum.MONITOR && session.getAppType() != ApplicationTypeEnum.INTERNAL)
			{
				this.serviceContext.getServerContext().getLicenseDaemon().resetLicense(this.stubService);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public int getSessionReleaseTime() throws ServiceRequestException
	{
		Session session = this.getSession(this.stubService.getSignature().getCredential());
		ConfigurableServerImpl svConfig = this.serviceContext.getServerContext().getServerConfig();
		int timeout = svConfig.getSessionTimeout() == null ? 0 : svConfig.getSessionTimeout();
		int returnValue = timeout * 60 - (int) ((System.currentTimeMillis() - session.getUpdateTime().getTime()) / 1000);
		return returnValue;
	}

}
