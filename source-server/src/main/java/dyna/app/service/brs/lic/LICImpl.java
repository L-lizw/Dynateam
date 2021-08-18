/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LICImpl License 控制服务
 * Wanglei 2011-4-20
 */
package dyna.app.service.brs.lic;

import dyna.app.core.SessionActiveTime;
import dyna.app.server.Server;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.BusinessRuleService;
import dyna.common.dto.Session;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.security.signature.SignatureFactory;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.LIC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * License 控制服务
 * 
 * @author Wanglei
 * 
 */
@Service
public class LICImpl extends BusinessRuleService implements LIC
{
	private static boolean	initialized	= false;
	@Autowired
	private SessionStub		sessionStub	= null;
	@Autowired
	private LicenseStub		licenseStub	= null;

	public SessionStub getSessionStub()
	{
		return this.sessionStub;
	}

	public LicenseStub getLicenseStub()
	{
		return this.licenseStub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public void init()
	{
		if (initialized)
		{
			return;
		}
		initialized = true;

		syncInit(this.serviceContext, server,this);
	}

	private static void syncInit(ServiceContext context, Server server, LIC lic)
	{

		server.addServerContextListener(new ServerContextListenerLicenseCheckImpl());
		try
		{
			List<Session> sessionList = lic.listSession();
			if (SetUtils.isNullList(sessionList))
			{
				return;
			}

			String sessionId = null;
			String userId = null;
			String userName = null;
			String userGuid = null;
			String groupId = null;
			String groupName = null;
			String loginGroupGuid = null;
			String roleId = null;
			String roleName = null;
			String loginRoleGuid = null;
			String ip = null;
			LanguageEnum lang = null;
			ApplicationTypeEnum appType = null;
			context.getServerContext().getLicenseDaemon().resetLicense(lic);
			for (Session session : sessionList)
			{
				sessionId = session.getGuid();
				userId = session.getUserId();
				userName = session.getUserName();
				userGuid = session.getUserGuid();
				groupId = session.getLoginGroupId();
				groupName = session.getLoginGroupName();
				loginGroupGuid = session.getLoginGroupGuid();
				roleId = session.getLoginRoleId();
				roleName = session.getLoginRoleName();
				loginRoleGuid = session.getLoginRoleGuid();
				ip = session.getIpAddress();
				lang = session.getLanguageEnum();
				appType = session.getAppType();
				String bizModelGuid = session.getBizModelGuid();
				String bizModelName = session.getBizModelName();
				
				context.getServerContext().getCredentialManager().bind(sessionId,//
						SignatureFactory.createSignature(userId, userName, userGuid, //
								groupId, groupName, loginGroupGuid, //
								roleId, roleName, loginRoleGuid, //
								ip, appType, lang,bizModelGuid,bizModelName,null));
			}
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		if (this.getSignature() == null)
		{
			super.authorize(method, args);
		}
	}

	protected synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#hasLicence(java.lang.String)
	 */
	@Override
	public boolean hasLicence(String moduleName) throws ServiceRequestException
	{
		return this.getSessionStub().hasLicence(moduleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#listLicensedOccupant()
	 */
	@Override
	public List<Session> listLicensedOccupant() throws ServiceRequestException
	{
		return this.getSessionStub().listLicensedOccupant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#kickUser(java.lang.String)
	 */
	@Override
	public void kickUser(String sessionId) throws ServiceRequestException
	{
		this.getSessionStub().kickUser(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getSession(java.lang.String)
	 */
	@Override
	public Session getSession(String sessionId) throws ServiceRequestException
	{
		return this.getSessionStub().getSession(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#listSession()
	 */
	@Override
	public List<Session> listSession() throws ServiceRequestException
	{
		return this.getSessionStub().listSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#clearSession()
	 */
	@Override
	public void clearSession() throws ServiceRequestException
	{
		this.getSessionStub().clearSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#deleteSession(java.lang.String)
	 */
	@Override
	public void deleteSession(String sessionId) throws ServiceRequestException
	{
		this.getSessionStub().deleteSession(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getLicenseOccupiedNode()
	 */
	@Override
	public int[] getLicenseOccupiedNode() throws ServiceRequestException
	{
		return this.getLicenseStub().getLicenseOccupiedNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getLicenseNode()
	 */
	@Override
	public int[] getLicenseNode() throws ServiceRequestException
	{
		return this.getLicenseStub().getLicenseNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getLicenseModules()
	 */
	@Override
	public List<String> getLicenseModuleList() throws ServiceRequestException
	{
		return StringUtils.splitToListByStrs(this.getLicenseStub().getLicenseModules(), ",");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getLicenseModules()
	 */
	@Override
	public String getLicenseModules() throws ServiceRequestException
	{
		return this.getLicenseStub().getLicenseModules();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#checkUserSession(java.lang.String)
	 */
	@Override
	public List<Session> listUserSession(String userId) throws ServiceRequestException
	{
		return this.getSessionStub().listUserSession(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getLicensePeriod()
	 */
	@Override
	public long[] getLicensePeriod() throws ServiceRequestException
	{
		return this.getLicenseStub().getLicensePeriod();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getVersionInfo()
	 */
	@Override
	public String getVersionInfo() throws ServiceRequestException
	{
		return this.getLicenseStub().getVersionInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.LIC#getSystemIdentification()
	 */
	@Override
	public String getSystemIdentification() throws ServiceRequestException
	{
		return this.getLicenseStub().getSystemIdentification();
	}

	@Override
	@SessionActiveTime(isUpdate = false)
	public int getSessionReleaseTime() throws ServiceRequestException
	{
		return this.getSessionStub().getSessionReleaseTime();
	}

	@Override
	public void activeSession() throws ServiceRequestException
	{
		this.activeSession(true);
	}

	@Override
	@SessionActiveTime(isUpdate = false)
	public int getSessionPromptTime() throws ServiceRequestException {
		Integer time = this.serviceContext.getServerContext().getServerConfig().getSessionPromptTime();
		return time==null ? 0 : time.intValue();
	}

}
