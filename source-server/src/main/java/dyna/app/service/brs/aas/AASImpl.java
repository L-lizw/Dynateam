/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AASImpl Authorization and Authentication Service implementation
 * Wanglei 2010-4-16
 */
package dyna.app.service.brs.aas;

import dyna.app.core.track.annotation.Tracked;
import dyna.app.service.BusinessRuleService;
import dyna.app.service.brs.aas.tracked.TRLoginImpl;
import dyna.app.service.brs.aas.tracked.TRLogoutImpl;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.Version;
import dyna.common.dto.aas.*;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.UserWorkFolderTypeEnum;
import dyna.common.util.SetUtils;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.EDAP;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.LIC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Authorization and Authentication Service implementation
 *
 * @author Wanglei
 */
@Service public class AASImpl extends BusinessRuleService implements AAS
{
	/**
	 * 需要权限判断, 是否为操作者本人的方法
	 */
	private static final List<String> SINGLE_METHOD_LIST = Arrays.asList("logout",                                                                                    //
			"saveUser",                                                                                                                                                    //
			"resetUserPassword",                                                                                                                                        //
			"updateUserPassword");

	/**
	 * 需要权限判断, 是否为管理组内用户的方法
	 */
	private static final List<String> ADMIN_METHOD_LIST = Arrays
			.asList("logout", "revokeUserFromRoleInGroup", "revokeRoleFromGroup", "assignRoleToGroup", "assignUserToRoleInGroup", "saveUser", "saveGroup", "saveRole",
					"inactiveUser", "inactiveGroup", "inactiveRole", "activeUser", "activeGroup", "activeRole", "resetUserPassword");

	@Autowired private GroupStub groupStub;
	@Autowired private OrgStub   orgStub;
	@Autowired private RoleStub  roleStub;
	@Autowired private LoginStub loginStub;
	@Autowired private UserStub  userStub;

	private static boolean initialized = false;

	@Override public void init()
	{
		if (initialized)
		{
			return;
		}

		initialized = true;
	}

	public UserStub getUserStub()
	{
		return this.userStub;
	}

	public GroupStub getGroupStub()
	{
		return this.groupStub;
	}

	protected OrgStub getOrgStub()
	{
		return this.orgStub;
	}

	protected RoleStub getRoleStub()
	{
		return this.roleStub;
	}

	public LoginStub getLoginStub()
	{
		return this.loginStub;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.service.BusinessRuleService#authorize(dyna.net.security.signature.Signature)
	 */
	@Override public void authorize(Method method, Object... args) throws AuthorizeException
	{
		try
		{
			super.authorize(method, args);
		}
		catch (AuthorizeException e)
		{
			if (this.getSignature() == null)
			{
				throw e;
			}
		}

		String methodName = method.getName();

		boolean isSingleAcl = SINGLE_METHOD_LIST.contains(methodName);
		boolean isAdminAcl = ADMIN_METHOD_LIST.contains(methodName);
		if (this.getSignature() instanceof ModuleSignature)
		{
			isAdminAcl = true;
		}

		if (!isSingleAcl && !isAdminAcl)
		{
			return;
		}

		if (this.getSignature() instanceof ModuleSignature)
		{
			return;
		}

		if (!(this.getSignature() instanceof UserSignature))
		{
			throw new AuthorizeException("permission denied");
		}

		UserSignature signature = (UserSignature) this.getSignature();

		try
		{
			if (isSingleAcl)
			{
				String userId = null;
				if ("saveUser".equals(methodName))
				{
					if (args[0] != null)
					{
						userId = ((User) args[0]).getUserId();
					}
				}
				else if ("updateUserPassword".equals(methodName) || //
						"resetUserPassword".equals(methodName) || //
						"logout".equals(methodName))
				{
					// logout with no argument, no need to check personal Authority
					if (args == null || args.length == 0)
					{
						return;
					}
					userId = (String) args[0];
				}

				this.singleAuthorize(signature, userId);
				return;
			}
		}
		catch (AuthorizeException e)
		{
			if (!isAdminAcl)
			{
				throw e;
			}
		}

		if (isAdminAcl)
		{
			// logout with no argument, no need to check administrative Authority
			if ("logout".equals(methodName) && (args == null || args.length == 0))
			{
				return;
			}
			this.administrativeAuthorize(signature, this);
		}

	}

	/**
	 * 检查调用者是否用户本人
	 *
	 * @param signature
	 * @param userId
	 * @throws AuthorizeException
	 */
	private void singleAuthorize(UserSignature signature, String userId) throws AuthorizeException
	{
		if (!signature.getUserId().equals(userId))
		{
			throw new AuthorizeException("accessible for administrative group or user itself");
		}
	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized EDAP getEDAP() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EDAP.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized LIC getLIC() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(LIC.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#activeGroup(java.lang.String)
	 */
	@Override public void activeGroup(String groupId) throws ServiceRequestException
	{
		this.getGroupStub().activeGroup(groupId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#activeRole(java.lang.String)
	 */
	@Override public void activeRole(String roleId) throws ServiceRequestException
	{
		this.getRoleStub().activeRole(roleId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#activeUser(java.lang.String)
	 */
	@Override public void activeUser(String userId) throws ServiceRequestException
	{
		this.getUserStub().activeUser(userId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#assignRoleToGroup(java.lang.String, java.lang.String)
	 */
	@Override public void assignRoleToGroup(String roleGuid, String groupGuid) throws ServiceRequestException
	{
		this.getOrgStub().assignRoleToGroup(roleGuid, groupGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#assignUserToRoleInGroup(java.lang.String, java.lang.String)
	 */
	@Override public void assignUserToRoleInGroup(String userGuid, String roleInGroupGuid) throws ServiceRequestException
	{
		this.getOrgStub().assignUserToRoleInGroup(userGuid, roleInGroupGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#assignUserToRoleInGroup(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override public void assignUserToRoleInGroup(String userGuid, String roleGuid, String groupGuid) throws ServiceRequestException
	{
		this.getOrgStub().assignUserToRoleInGroup(userGuid, roleGuid, groupGuid);
	}

	@Override public Group getGroup(String groupGuid) throws ServiceRequestException
	{
		return this.getGroupStub().getGroup(groupGuid);
	}

	@Override public Group getGroupById(String groupId) throws ServiceRequestException
	{
		return this.getGroupStub().getGroupById(groupId);
	}

	@Override public RIG getRIGByGroupAndRole(String groupGuid, String roleGuid) throws ServiceRequestException
	{
		return this.getOrgStub().getRIGByGroupAndRole(groupGuid, roleGuid, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#getRole(java.lang.String)
	 */
	@Override public Role getRole(String roleGuid) throws ServiceRequestException
	{
		return this.getRoleStub().getRole(roleGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#getRoleById(java.lang.String)
	 */
	@Override public Role getRoleById(String roleId) throws ServiceRequestException
	{
		return this.getRoleStub().getRoleById(roleId);
	}

	@Override public Group getRootGroup() throws ServiceRequestException
	{
		return this.getGroupStub().getRootGroup();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#getSuperGroup(java.lang.String)
	 */
	@Override public Group getSuperGroup(String groupGuid) throws ServiceRequestException
	{
		return this.getGroupStub().getSuperGroup(groupGuid);
	}

	@Override public User getUser(String userGuid) throws ServiceRequestException
	{
		return this.getUserStub().getUser(userGuid, false);
	}

	@Override public User getUserById(String userId) throws ServiceRequestException
	{
		return this.getUserStub().getUserById(userId, false);
	}

	@Override public List<Group> listAllGroup() throws ServiceRequestException
	{
		return this.getGroupStub().listAllGroup();
	}

	@Override public List<Role> listAllRole(String sortField, boolean isASC) throws ServiceRequestException
	{
		return this.getRoleStub().listAllRole(sortField, isASC);
	}

	@Override public List<Role> listAllRoleByGroupId(String groupId) throws ServiceRequestException
	{
		return this.getRoleStub().listAllRoleByGroupId(groupId);
	}

	@Override public List<Group> listAllSubGroup(String groupGuid, String groupId, boolean cascade) throws ServiceRequestException
	{
		return this.getGroupStub().listSubGroup(groupGuid, groupId, cascade, true);
	}

	@Override public List<User> listAllUser(String sortField, boolean isASC) throws ServiceRequestException
	{
		return this.getUserStub().listAllUser(sortField, isASC);
	}

	@Override public List<User> listAllUserByRoleInGroup(String groupId, String roleId) throws ServiceRequestException
	{
		return this.getUserStub().listAllUserByRoleInGroup(groupId, roleId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listGroup()
	 */
	@Override public List<Group> listGroup(boolean hasRoot) throws ServiceRequestException
	{
		return this.getGroupStub().listGroup(hasRoot);
	}

	@Override public List<Group> listGroupByUser(String userId) throws ServiceRequestException
	{
		return this.getGroupStub().listGroupByUser(userId);
	}

	@Override public List<Group> listGroupByUserForLogin(String userId) throws ServiceRequestException
	{
		List<Group> listGroupByUser = this.getGroupStub().listGroupByUser(userId);
		if (listGroupByUser != null)
		{
			for (Group group : listGroupByUser)
			{
				if ("ROOT".equalsIgnoreCase(group.getName()))
				{
					listGroupByUser.remove(group);
					break;
				}
			}
		}
		return listGroupByUser;
	}

	@Override public List<RIG> listRIGOfUser(String userId) throws ServiceRequestException
	{
		return this.getUserStub().listRIGOfUser(userId, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listRole()
	 */
	@Override public List<Role> listRole(String sortField, boolean isASC) throws ServiceRequestException
	{
		return this.getRoleStub().listRole(sortField, isASC);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listRoleInGroup(java.lang.String)
	 */
	@Override public List<Role> listRoleByGroupId(String groupId) throws ServiceRequestException
	{
		return this.getRoleStub().listRoleByGroupId(groupId);
	}

	@Override public List<Role> listRoleByUserInGroup(String userId, String groupId) throws ServiceRequestException
	{
		return this.getRoleStub().listRoleByUserInGroup(userId, groupId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listRoleInGroup()
	 */
	@Override public List<RIG> listRoleInGroup() throws ServiceRequestException
	{
		return this.getOrgStub().listRoleInGroup();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listSubGroup(java.lang.String, java.lang.String, boolean)
	 */
	@Override public List<Group> listSubGroup(String groupGuid, String groupId, boolean cascade) throws ServiceRequestException
	{
		return this.getGroupStub().listSubGroup(groupGuid, groupId, cascade, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listSuperGroup(java.lang.String, java.lang.String)
	 */
	@Override public List<Group> listSuperGroup(String groupGuid, String groupId) throws ServiceRequestException
	{
		return this.getGroupStub().listSuperGroup(groupGuid, groupId);
	}

	@Override public List<User> listUser(String sortField, boolean isASC) throws ServiceRequestException
	{
		return this.getUserStub().listUser(sortField, isASC);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listUserByRoleInGroup(java.lang.String)
	 */
	@Override public List<User> listUserByRoleInGroup(String roleInGroupGuid) throws ServiceRequestException
	{
		return this.getUserStub().listUserByRoleInGroup(roleInGroupGuid, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listUserInRole(java.lang.String)
	 */
	@Override public List<User> listUserByRoleInGroup(String groupId, String roleId) throws ServiceRequestException
	{
		return this.getUserStub().listUserByRoleInGroup(groupId, roleId, true);
	}

	@Override public List<User> listUserInGroup(String groupGuid) throws ServiceRequestException
	{
		return this.getUserStub().listUserInGroup(groupGuid, false);
	}

	@Tracked(description = TrackedDesc.LOGIN, renderer = TRLoginImpl.class) @Override public String login(String userID, String groupID, String roleID, String password,
			String hostName, LanguageEnum lang) throws ServiceRequestException
	{
		return this.getLoginStub().login(userID, groupID, roleID, password, null, hostName, ApplicationTypeEnum.STANDARD, lang);
	}

	@Tracked(description = TrackedDesc.LOGIN, renderer = TRLoginImpl.class) @Override public String login(String userID, String groupID, String roleID, String password,
			String hostName, ApplicationTypeEnum appType, LanguageEnum lang) throws ServiceRequestException
	{
		return this.getLoginStub().login(userID, groupID, roleID, password, null, hostName, appType, lang);
	}

	@Tracked(description = TrackedDesc.LOGIN, renderer = TRLoginImpl.class) @Override public String login(String userID, String groupID, String roleID, String password, String ip,
			String hostName, ApplicationTypeEnum appType, LanguageEnum lang) throws ServiceRequestException
	{
		return this.getLoginStub().login(userID, groupID, roleID, password, ip, hostName, appType, lang);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.service.brs.AAS#logout()
	 */
	@Tracked(description = TrackedDesc.LOGOUT, renderer = TRLogoutImpl.class) @Override public void logout() throws ServiceRequestException
	{
		this.getLoginStub().logout();
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see dyna.app.service.brs.AAS#logout(java.lang.String, java.lang.String,
	// * java.lang.String)
	// */
	// @Tracked(description = TrackedDesc.LOGOUT, renderer = TRLogoutImpl.class)
	// @Override
	// public void logout(String userID, String groupID, String roleID) throws ServiceRequestException
	// {
	// this.getLoginStub().logout(userID, groupID, roleID, ApplicationTypeEnum.STANDARD);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see dyna.net.service.brs.AAS#logout(java.lang.String, java.lang.String, java.lang.String,
	// * dyna.common.systemenum.ApplicationTypeEnum)
	// */
	// @Tracked(description = TrackedDesc.LOGOUT, renderer = TRLogoutImpl.class)
	// @Override
	// public void logout(String userID, String groupID, String roleID, ApplicationTypeEnum appType)
	// throws ServiceRequestException
	// {
	// this.getLoginStub().logout(userID, groupID, roleID, appType);
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#inactiveGroup(java.lang.String)
	 */
	@Override public void obsoleteGroup(String groupId) throws ServiceRequestException
	{
		this.getGroupStub().obsoleteGroup(groupId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#inactiveRole(java.lang.String)
	 */
	@Override public void obsoleteRole(String roleId) throws ServiceRequestException
	{
		this.getRoleStub().obsoleteRole(roleId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#inactiveUser(java.lang.String)
	 */
	@Override public User obsoleteUser(String userId) throws ServiceRequestException
	{
		return this.getUserStub().obsoleteUser(userId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#resetUserPassword(java.lang.String)
	 */
	@Override public void resetUserPassword(String userId) throws ServiceRequestException
	{
		this.getUserStub().resetUserPassword(userId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#revokeRoleFromGroup(java.lang.String, java.lang.String)
	 */
	@Override public void revokeRoleFromGroup(String roleId, String groupId) throws ServiceRequestException
	{
		this.getOrgStub().revokeRoleFromGroup(roleId, groupId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#revokeUserFromRoleInGroup(java.lang.String, java.lang.String)
	 */
	@Override public void revokeUserFromRoleInGroup(String userId, String roleInGroupGuid) throws ServiceRequestException
	{
		this.getOrgStub().revokeUserFromRoleInGroup(userId, roleInGroupGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#revokeUserFromRoleInGroup(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override public void revokeUserFromRoleInGroup(String userId, String roleId, String groupId) throws ServiceRequestException
	{
		this.getOrgStub().revokeUserFromRoleInGroup(userId, roleId, groupId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#saveGroup(dyna.common.bean.data.system.Group)
	 */
	@Override public Group saveGroup(Group group) throws ServiceRequestException
	{
		return this.getGroupStub().saveGroup(group);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#saveRole(dyna.common.bean.data.system.Role)
	 */
	@Override public Role saveRole(Role role) throws ServiceRequestException
	{
		return this.getRoleStub().saveRole(role);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#saveUser(dyna.common.bean.data.system.User)
	 */
	@Override public User saveUser(User user) throws ServiceRequestException
	{
		return this.getUserStub().saveUser(user);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#updateUserPassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override public void updateUserPassword(String userId, String oldPwd, String newPwd) throws ServiceRequestException
	{
		this.getUserStub().updateUserPassword(userId, oldPwd, newPwd);
	}

	@Override public List<User> listUserInGroupAndSubGroup(String groupGuid) throws ServiceRequestException
	{
		return this.getUserStub().listUserInGroupAndSubGroup(groupGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listRIGByRoleGuid(java.lang.String)
	 */
	@Override public List<RIG> listRIGByRoleGuid(String roleGuid) throws ServiceRequestException
	{
		return this.getOrgStub().listRoleInGroupByRoleGuid(roleGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#listRoleByGroup(java.lang.String)
	 */
	@Override public List<Role> listRoleByGroup(String groupGuid) throws ServiceRequestException
	{
		return this.getRoleStub().listRoleByGroup(groupGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#getRIG(java.lang.String)
	 */
	@Override public RIG getRIG(String rigGuid) throws ServiceRequestException
	{
		return this.getOrgStub().getRIG(rigGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#isUserInGroup(java.lang.String, java.lang.String)
	 */
	@Override public boolean isUserInGroup(String groupGuid, String userGuid) throws ServiceRequestException
	{
		return this.getUserStub().isUserInGroup(groupGuid, userGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#isUserInRIG(java.lang.String, java.lang.String)
	 */
	@Override public boolean isUserInRIG(String roleInGroupGuid, String userGuid) throws ServiceRequestException
	{
		return this.getUserStub().isUserInRIG(roleInGroupGuid, userGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#isUserInRole(java.lang.String, java.lang.String)
	 */
	@Override public boolean isUserInRole(String roleGuid, String userGuid) throws ServiceRequestException
	{
		return this.getUserStub().isUserInRole(roleGuid, userGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#getMaskUser(java.lang.String)
	 */
	@Override public User getMaskUser(String userGuid) throws ServiceRequestException
	{
		return this.getUserStub().getUser(userGuid, true);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see dyna.net.service.brs.AAS#getServiceVersion()
	// */
	// @Override
	// public void getServiceVersion(String versionInfo) throws ServiceRequestException
	// {
	//
	// if (!Version.getVersionInfo().equalsIgnoreCase(versionInfo))
	// {
	// new ServiceRequestException("ID_APP_INVALID_CLIENT_VERSION", "Client Version inconsistencies", null,
	// Version.getVersionInfo(), versionInfo);
	// }
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.service.brs.AAS#checkClientVersion(java.lang.String)
	 */
	@Override public boolean checkClientVersion(String clientSum, String clientType, boolean isDebug) throws ServiceRequestException
	{
		if (this.serviceContext.getServerContext().isDebugMode() || isDebug)
		{
			if (this.getSignature() instanceof ModuleSignature)
			{
				((ModuleSignature) this.getSignature()).setVersionValidate(true);
			}
			return true;
		}
		boolean checkClientVersionSum = Version.getVersionInfo().equalsIgnoreCase(clientSum);
		if (!checkClientVersionSum)
		{
			throw new ServiceRequestException("ID_APP_INVALID_CLIENT_VERSION", "Client Version inconsistencies", null, Version.getVersionInfo(), "");
		}

		if (this.getSignature() instanceof ModuleSignature)
		{
			((ModuleSignature) this.getSignature()).setVersionValidate(true);
		}
		else
		{
			throw new ServiceRequestException("ID_APP_INVALID_CLIENT_VERSION", "Client Version inconsistencies", null, Version.getVersionInfo(), "");
		}

		return checkClientVersionSum;
	}

	@Override public List<RIG> listRIGOfUserForLogin(String userId) throws ServiceRequestException
	{
		List<RIG> listRIGOfUser = this.getUserStub().listRIGOfUser(userId, false);

		for (RIG rig : listRIGOfUser)
		{
			if ("ROOT".equalsIgnoreCase(rig.getGroupName()))
			{
				listRIGOfUser.remove(rig);
				break;
			}
		}
		return listRIGOfUser;
	}

	@Override public UserAgent obsoleteUserAgent(UserAgent agent) throws ServiceRequestException
	{
		agent.setValid(false);
		return this.getUserStub().saveUserAgent(agent);
	}

	@Override public UserAgent activeUserAgent(UserAgent agent) throws ServiceRequestException
	{
		agent.setValid(true);
		return this.getUserStub().saveUserAgent(agent);
	}

	@Override public UserAgent getAgentByPrincipal(String principalGuid) throws ServiceRequestException
	{
		return this.getUserStub().getUserAgentByPrincipal(principalGuid, null);
	}

	@Override public UserAgent getValidAgentByPrincipal(String principalGuid) throws ServiceRequestException
	{
		return this.getUserStub().getUserAgentByPrincipal(principalGuid, true);
	}

	@Override public List<UserAgent> listUserAgent(UserAgent agent) throws ServiceRequestException
	{
		return this.getUserStub().listUserAgent(agent.getPrincipalGuid(), agent.getAgentGuid(), agent.getValid());
	}

	@Override public boolean isAgent(String agentGuid, String principalGuid) throws ServiceRequestException
	{
		UserAgent agent = new UserAgent();
		agent.setAgentGuid(agentGuid);
		agent.setPrincipalGuid(principalGuid);
		agent.setValid(true);

		List<UserAgent> list = this.listUserAgent(agent);
		if (!SetUtils.isNullList(list))
		{
			return true;
		}
		return false;
	}

	@Override public User getUserWitType(String guid) throws ServiceRequestException
	{
		return this.getUserStub().getUserWitType(guid);
	}

	@Override public String getUserWorkFolder(String userGuid, UserWorkFolderTypeEnum workFolderTypeEnum) throws ServiceRequestException
	{
		UserWorkFolder userWorkFolder = this.getUserStub().getUserWorkFolder(userGuid, workFolderTypeEnum);
		return userWorkFolder == null ? null : userWorkFolder.getFolderPath();
	}

	@Override public void saveUserWorkFolder(String userGuid, UserWorkFolderTypeEnum workFolderTypeEnum, String folderPath) throws ServiceRequestException
	{
		this.getUserStub().saveUserWorkFolder(userGuid, workFolderTypeEnum, folderPath);
	}

	@Override public String lookupSession(String userID, String password, String ip, String hostName, ApplicationTypeEnum appType) throws ServiceRequestException
	{
		return this.getLoginStub().lookupSession(userID, password, ip, hostName, appType);
	}

	@Override public String lookupSession(String userID, String password, boolean isEncryptPwd, String ip, String hostName, ApplicationTypeEnum appType)
			throws ServiceRequestException
	{
		return this.getLoginStub().lookupSession(userID, password, isEncryptPwd, ip, hostName, appType);
	}

	@Override public String getPLMUserByMobile(String mobileUserID) throws ServiceRequestException
	{
		return this.getUserStub().getPLMUserByMobile(mobileUserID);
	}

	@Override public boolean reportUserAuthentication(String sessionId, String userId) throws ServiceRequestException
	{
		return this.getUserStub().reportUserAuthentication(sessionId, userId);
	}

	@Override public List<Group> lisgGroupByBM(String bmguid) throws ServiceRequestException
	{
		return this.groupStub.listGroupByBM(bmguid);
	}

	@Override public boolean isChildGroup(String groupGuid, String parentGroupGuid) throws ServiceRequestException
	{
		return getGroupStub().isChildGroup(groupGuid, parentGroupGuid);
	}
}
