/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LoginStub 与登陆相关的操作分支
 * Wanglei 2010-7-22
 */
package dyna.app.service.brs.aas;

import dyna.app.core.lic.LicenseException;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.lic.LICImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.EncryptUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.SignatureFactory;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.Date;
import java.util.List;

/**
 * 与登陆相关的操作分支
 *
 * @author Wanglei
 */
@Component
public class LoginStub extends AbstractServiceStub<AASImpl>
{

	public String login(String userId, String groupId, String roleId, LanguageEnum lang) throws ServiceRequestException
	{
		return this.login(userId, groupId, roleId, null, null, null, ApplicationTypeEnum.INTERNAL, lang, false);
	}

	protected String login(String userId, String groupId, String roleId, String password, String ip, String hostName, ApplicationTypeEnum appType, LanguageEnum lang)
			throws ServiceRequestException
	{
		return this.login(userId, groupId, roleId, password, ip, hostName, appType, lang, true);
	}

	private String login(String userId, String groupId, String roleId, String password, String ip, String hostName, ApplicationTypeEnum appType, LanguageEnum lang,
			boolean checkPassword) throws ServiceRequestException
	{
		if (appType == null || StringUtils.isNullString(userId) || StringUtils.isNullString(groupId) || StringUtils.isNullString(roleId))
		{
			throw new ServiceRequestException("missing userId or groupId or roleId or application type");
		}

		if (Signature.SYSTEM_INTERNAL_USERID.equals(userId))
		{
			throw new ServiceRequestException("ID_APP_PRESERVED_ID", "system preserved user");
		}

		String moduleName = Signature.MODULE_CLIENT;
		CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && checkPassword)
		{
			String connCredential = (String) authentication.getDetails();

			try
			{
				Signature signature = cm.authenticate(connCredential);
				if (!(signature instanceof ModuleSignature))
				{
					throw new ServiceRequestException("unable to use unsigned conncetion for login action");
				}
				// boolean versionValidate = ((ModuleSignature) signature).isVersionValidate();
				// if (!versionValidate)
				// {
				// throw new ServiceRequestException("ID_APP_CHECK_CLIENT_VERSION",
				// "client module version has not been validated");
				// }

				moduleName = ((ModuleSignature) signature).getModuleId();
				if (!Signature.MODULE_CLIENT.equals(moduleName))
				{
					throw new ServiceRequestException("invalid module, available value: " + Signature.MODULE_CLIENT);
				}

				ip = RemoteServer.getClientHost() + (StringUtils.isNullString(ip) ? "" : "/" + ip);

			}
			catch (AuthorizeException e)
			{
				throw ServiceRequestException.createByException("", e);
			}
			catch (ServerNotActiveException e)
			{
				DynaLogger.error(e.getMessage());
			}
		}
		if (ApplicationTypeEnum.NRM.equals(appType))
		{
			Group group = this.stubService.getGroupById(groupId);
			if (group == null || !group.isAdminGroup())
			{
				throw new ServiceRequestException("ID_APP_LOGIN_ADMIN_ONLY", "Only System Manager Can Use this Application");
			}
		}
		// String sessionId = this.stubService.getSignature().getCredential();
		String credential = null;
		SystemDataService sds = DataServer.getSystemDataService();
		boolean sucRequestLicense = false;
		User user = null;
		try
		{
			String portalString = "portal";
			String portalPassword = portalString + userId + DateFormat.format(new Date(), "yyyyMMdd");
			portalPassword = EncryptUtils.encryptMD5(portalPassword);
			if (portalPassword.equals(password))
			{
				checkPassword = false;
			}
			if (checkPassword)
			{
				user = this.stubService.getUserStub().checkUserPassword(userId, password);
			}
			else
			{
				user = this.stubService.getUserStub().getUserById(userId, false);
			}

			if (user == null)
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_VALIDATE_ERROR", "id or password error");
			}

			// 保存Session
			String userGuid = user.getGuid();
			String loginGroupGuid = "";
			String loginRoleGuid = null;

			List<Group> groupList = this.stubService.getGroupStub().listGroupByUser(userId);
			if (SetUtils.isNullList(groupList))
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_GROUP_VALIDATE_ERROR", "missing group");
			}

			Group group = null;
			boolean isValidGroup = false;
			for (Group grp : groupList)
			{
				if (grp.getGroupId().equals(groupId))
				{
					group = grp;
					isValidGroup = true;
					loginGroupGuid = grp.getGuid();
					break;
				}
			}

			if (!isValidGroup)
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_GROUP_VALIDATE_ERROR", "group isn't exist");
			}

			List<Role> roleList = this.stubService.getRoleStub().listRoleByUserInGroup(userId, groupId);
			if (SetUtils.isNullList(roleList))
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_ROLE_VALIDATE_ERROR", "missing role");
			}

			Role role = null;
			boolean isValidRole = false;
			for (Role rl : roleList)
			{
				if (rl.getRoleId().equals(roleId))
				{
					role = rl;
					isValidRole = true;
					loginRoleGuid = rl.getGuid();
					break;
				}
			}

			if (!isValidRole)
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_ROLE_VALIDATE_ERROR", "role isn't exist");
			}

			String bmGuid = (String) group.get("BMGUID");
			BMInfo bm = DataServer.getSystemDataService().get(BMInfo.class, bmGuid);
			if (bm == null)
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_BM_VALIDATE_ERROR", "not specified business model");

			}

			if (StringUtils.isNullString(ip))
			{
				ip = Signature.LOCAL_IP;
			}

			if (appType == ApplicationTypeEnum.MONITOR)
			{

			}
			else if (appType == ApplicationTypeEnum.INTERNAL || appType == ApplicationTypeEnum.STANDARD)
			{
				if (this.serviceContext.getServerContext().getLicenseDaemon().hasLicence("BASE") == false)
				{
					throw new ServiceRequestException("ID_APP_LICENSE_NEED", "No license grant for BASE");
				}
			}
			else
			{
				if (this.serviceContext.getServerContext().getLicenseDaemon().hasLicence(appType.name()) == false)
				{
					throw new ServiceRequestException("ID_APP_LICENSE_NEED", "No license grant for " + appType.name());
				}

			}

			if (appType != ApplicationTypeEnum.MONITOR && appType != ApplicationTypeEnum.INTERNAL)
			{

				List<Session> userSessions = this.stubService.getLIC().listUserSession(user.getGuid());
				boolean requestLicense = true;
				if (SetUtils.isNullList(userSessions) == false)
				{
					for (Session s : userSessions)
					{
						if (hostName.equalsIgnoreCase(s.getHostName()))
						{
							if (appType == ApplicationTypeEnum.WEB || appType == ApplicationTypeEnum.WEBWX)
							{
								if (s.getAppType() == ApplicationTypeEnum.WEB || s.getAppType() == ApplicationTypeEnum.WEBWX)
								{
									requestLicense = false;
									break;
								}
							}
							else
							{
								if (s.getAppType() != ApplicationTypeEnum.WEB && s.getAppType() != ApplicationTypeEnum.WEBWX)
								{
									requestLicense = false;
									break;
								}

							}
						}
					}
				}
				if (requestLicense)
				{
					sucRequestLicense = this.serviceContext.getServerContext().getLicenseDaemon().requestLicense(this.stubService.getLIC(), appType.name());
				}
			}

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// login action
			Session session = new Session();
			session.setUserGuid(userGuid);
			session.setLoginGroupGuid(loginGroupGuid);
			session.setLoginRoleGuid(loginRoleGuid);
			session.setIpAddress(ip);
			session.setHostName(hostName);
			session.setBizModelGuid(bmGuid);
			session.setClientType(moduleName);
			session.setAppType(appType);
			session.setLanguageEnum(lang);
			session.setSiteId(this.serviceContext.getServerContext().getServerConfig().getServerId());

			credential = sds.save(session);

			// session = this.stubService.getLIC().getSession(credential);
			// if (session.getUserSessionCount() == 1)
			// {
			// sucRequestLicense = this.serviceContext.getServerContext().getLicenseDaemon()
			// .requestLicense(this.stubService.getLIC());
			// }

			if (session.getAppType() == ApplicationTypeEnum.OM)
			{
				cm.bind(credential, //
						SignatureFactory.createSignature(Signature.MODULE_MODELER));
			}
			else
			{
				cm.bind(credential, //
						SignatureFactory.createSignature(userId, user.getName(), userGuid, //
								groupId, group.getName(), loginGroupGuid, //
								roleId, role.getName(), loginRoleGuid, //
								ip, appType, lang, group.getBizModelGuid(), group.getBizModelName(), group.getBizModelTitle()));
			}
			// update user latest login time.
			user.setLatestLoginTime(this.stubService.getEMM().getSystemDate());
			user.remove("PASSWORD");
			sds.save(user);
//			DataServer.getTransactionManager().commitTransaction();

		}
		catch (LicenseException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw new ServiceRequestException("ID_APP_LICENSE_LIMITED", e.getMessage());
		}
		catch (DynaDataException e)
		{
			if (sucRequestLicense)
			{
				this.serviceContext.getServerContext().getLicenseDaemon().releaseLicense(appType.name());
			}
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			if (sucRequestLicense)
			{
				this.serviceContext.getServerContext().getLicenseDaemon().releaseLicense(appType.name());
			}
			// DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			throw new ServiceRequestException("ID_APP_LOGIN_ERROR", e.getMessage(), e.fillInStackTrace());
		}
		return credential;
	}

	protected void logout() throws ServiceRequestException
	{
		((LICImpl) this.stubService.getLIC()).getSessionStub().deleteSessionInside(this.stubService.getSignature().getCredential());
	}

	protected void logout(String userID, String groupID, String roleID, ApplicationTypeEnum appType) throws ServiceRequestException
	{
		Signature sgn = this.stubService.getSignature();

		if (sgn instanceof ModuleSignature)
		{
			CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();
			String ftpc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_SESSION);
			if (!sgn.getCredential().equals(ftpc))
			{
				throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, new AuthorizeException("unauthorized signature"));
			}
		}

		this.logoutWithId(userID, groupID, roleID, appType);

	}

	private void logoutWithId(String userID, String groupID, String roleID, ApplicationTypeEnum appType) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			UpperKeyMap filter = new UpperKeyMap();
			filter.put("USERID", userID);
			filter.put("LOGINGROUPID", groupID);
			filter.put("LOGINROLEID", roleID);
			filter.put("CLIENTTYPE", Signature.MODULE_CLIENT);
			filter.put("APPTYPE", appType.name());
			filter.put("SITEID", this.serviceContext.getServerContext().getServerConfig().getServerId());

			List<Session> sessionList = sds.listFromCache(Session.class, new FieldValueEqualsFilter<Session>(filter));

			if (SetUtils.isNullList(sessionList))
			{
				return;
			}

			Session session = sessionList.get(0);
			((LICImpl) this.stubService.getLIC()).getSessionStub().deleteSessionInside(session.getGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void logoutWithGuid(String userGuid, String groupGuid, String roleGuid, CredentialManager cm) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			UpperKeyMap filter = new UpperKeyMap();
			filter.put("USERGUID", userGuid);
			filter.put("LOGINGROUPGUID", groupGuid);
			filter.put("LOGINROLEGUID", roleGuid);
			filter.put("CLIENTTYPE", Signature.MODULE_CLIENT);

			List<Session> sessionList = sds.listFromCache(Session.class, new FieldValueEqualsFilter<Session>(filter));
			if (SetUtils.isNullList(sessionList))
			{
				return;
			}

			this.stubService.getLIC().kickUser(sessionList.get(0).getGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public String lookupSession(String userId, String password, String ip, String hostName, ApplicationTypeEnum appType) throws ServiceRequestException
	{
		return this.lookupSession(userId, password, false, ip, hostName, appType);
	}

	public String lookupSession(String userId, String password, boolean isEncryptPwd, String ip, String hostName, ApplicationTypeEnum appType) throws ServiceRequestException
	{
		if (appType == null || StringUtils.isNullString(userId))
		{
			throw new ServiceRequestException("missing userId or application type");
		}

		if (Signature.SYSTEM_INTERNAL_USERID.equals(userId))
		{
			throw new ServiceRequestException("ID_APP_PRESERVED_ID", "system preserved user");
		}

		String moduleName = Signature.MODULE_CLIENT;
		CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null)
		{
			String connCredential = (String) authentication.getDetails();

			try
			{
				Signature signature = cm.authenticate(connCredential);
				if (!(signature instanceof ModuleSignature))
				{
					throw new ServiceRequestException("unable to use unsigned conncetion for login action");
				}
				boolean versionValidate = ((ModuleSignature) signature).isVersionValidate();
				if (!versionValidate)
				{
					throw new ServiceRequestException("ID_APP_CHECK_CLIENT_VERSION", "client module version has not been validated");
				}

				moduleName = ((ModuleSignature) signature).getModuleId();
				if (!Signature.MODULE_CLIENT.equals(moduleName))
				{
					throw new ServiceRequestException("invalid module, available value: " + Signature.MODULE_CLIENT);
				}

				ip = RemoteServer.getClientHost() + (StringUtils.isNullString(ip) ? "" : "/" + ip);

			}
			catch (AuthorizeException e)
			{
				throw ServiceRequestException.createByException("", e);
			}
			catch (ServerNotActiveException e)
			{
				DynaLogger.error(e.getMessage());
			}
		}
		String credential = null;
		try
		{
			User user = null;
			String portalString = "portal";
			String portalPassword = portalString + userId + DateFormat.format(new Date(), "yyyyMMdd");
			portalPassword = EncryptUtils.encryptMD5(portalPassword);
			boolean checkPassword = true;
			if (portalPassword.equals(password))
			{
				checkPassword = false;
			}
			if (checkPassword)
			{
				user = this.stubService.getUserStub().checkUserPassword(userId, password);
			}
			else
			{
				user = this.stubService.getUserStub().getUserById(userId, false);
			}

			if (user == null)
			{
				throw new ServiceRequestException("ID_WEB_LOGIN_VALIDATE_ERROR", "id or password error");
			}
			List<Session> userSessions = this.stubService.getLIC().listUserSession(user.getGuid());
			if (SetUtils.isNullList(userSessions) == false)
			{
				for (Session s : userSessions)
				{
					if (hostName.equalsIgnoreCase(s.getHostName()))
					{
						if (appType == s.getAppType())
						{
							return s.getGuid();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			throw new ServiceRequestException("ID_APP_LOGIN_ERROR", e.getMessage(), e.fillInStackTrace());
		}

		return credential;
	}
}
