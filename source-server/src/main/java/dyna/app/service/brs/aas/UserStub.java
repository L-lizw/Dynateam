/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UserStub
 * Wanglei 2010-7-27
 */
package dyna.app.service.brs.aas;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.lic.LICImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dto.aas.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.systemenum.UserWorkFolderTypeEnum;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;

/**
 * 与用户相关的操作分支
 * 
 * @author Wanglei
 * 
 */
public class UserStub extends AbstractServiceStub<AASImpl>
{
	/**
	 * @param context
	 * @param service
	 */
	public UserStub(ServiceContext context, AASImpl service)
	{
		super(context, service);
	}

	protected void activeUser(String userId) throws ServiceRequestException
	{
		User user = this.getUserById(userId, false);
		if (user == null)
		{
			throw new ServiceRequestException(null, "user " + userId + " not found.");
		}
		String operatorGuid = this.stubService.getOperatorGuid();

		user.setActive(true);
		user.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			sds.save(user);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected User checkUserPassword(String userId, String pwd) throws ServiceRequestException
	{
		if (StringUtils.isNullString(userId))
		{
			return null;
		}
		User user = this.getUserById(userId, false);
		if (user == null)
		{
			return null;
		}
		else
		{
			pwd = EncryptUtils.encryptMD5(pwd);
			if (pwd.equals(user.getPassword()))
			{
				return user;
			}
			else
			{
				return null;
			}
		}
	}

	protected User getUser(String userGuid, boolean isMask) throws ServiceRequestException
	{
		if (StringUtils.isNullString(userGuid))
		{
			return null;
		}

		User user = null;
		try
		{
			user = DataServer.getSystemDataService().get(User.class, userGuid);
			if (isMask)
			{
				user = this.maskUser(user);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return user;
	}

	protected User getUserById(String userId, boolean isMask) throws ServiceRequestException
	{
		if (StringUtils.isNullString(userId))
		{
			return null;
		}

		try
		{
			List<User> userList = DataServer.getSystemDataService().listFromCache(User.class, new FieldValueEqualsFilter<User>("USERID", userId));
			if (!SetUtils.isNullList(userList))
			{
				User user = userList.get(0);
				if (isMask)
				{
					user = this.maskUser(user);
				}

				return user;
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<User> listAllUser(String sortField, final boolean isASC) throws ServiceRequestException
	{
		List<User> userList = DataServer.getSystemDataService().listFromCache(User.class, null);

		Collections.sort(userList, new UserComparator(sortField, isASC));

		return userList;
	}

	protected List<User> listAllUserByRoleInGroup(String roleInGroupGuid) throws ServiceRequestException
	{
		return this.listUserByRoleInGroup(roleInGroupGuid, false);
	}

	protected List<User> listAllUserByRoleInGroup(String groupId, String roleId) throws ServiceRequestException
	{
		if (StringUtils.isNullString(groupId) || StringUtils.isNullString(roleId))
		{
			return null;
		}

		Group group = this.stubService.getGroupById(groupId);
		Role role = this.stubService.getRoleById(roleId);
		if (group == null || role == null)
		{
			return null;
		}

		RIG rig = this.stubService.getOrgStub().getRIGByGroupAndRole(group.getGuid(), role.getGuid(), false);
		if (rig != null)
		{
			List<User> userList = this.stubService.getOrgStub().listUserOfRIG(rig.getGuid());
			if (!SetUtils.isNullList(userList))
			{
				Iterator<User> it = userList.iterator();
				while (it.hasNext())
				{
					User user = it.next();
					if ("SYSTEM.INTERNAL".equals(user.getUserId()))
					{
						it.remove();
					}
				}
			}
			return userList;
		}

		return null;
	}

	/**
	 * 获取用户的组与角色
	 * 
	 * @param userId
	 *            用户的Id
	 * @return 用户的RIG
	 * @throws ServiceRequestException
	 */
	protected List<RIG> listRIGOfUser(String userId, boolean containObsolete) throws ServiceRequestException
	{
		List<RIG> rigList = new ArrayList<RIG>();
		List<URIG> urigList = this.stubService.getOrgStub().listUserRoleInGroupByUser(userId);
		if (!SetUtils.isNullList(urigList))
		{
			for (URIG urig : urigList)
			{
				String rigGuid = urig.getRoleGroupGuid();
				RIG rig = this.stubService.getOrgStub().getRIG(rigGuid);
				if (rig != null)
				{
					if (!containObsolete)
					{
						Group group = this.stubService.getGroup(rig.getGroupGuid());
						Role role = this.stubService.getRole(rig.getRoleGuid());
						if (group != null && role != null && group.isActive() && role.isActive())
						{
							rigList.add(rig);
						}
					}
					else
					{
						rigList.add(rig);
					}
				}
			}
		}
		return rigList;
	}

	protected List<User> listUser(String sortField, boolean isASC) throws ServiceRequestException
	{
		List<User> userList = this.listAllUser(sortField, isASC);
		if (!SetUtils.isNullList(userList))
		{
			Iterator<User> it = userList.iterator();
			while (it.hasNext())
			{
				User user = it.next();
				if (!user.isActive())
				{
					it.remove();
				}
			}
		}
		return userList;
	}

	protected List<User> listUserByRoleInGroup(String roleInGroupGuid, boolean activeOnly) throws ServiceRequestException
	{
		RIG rig = this.stubService.getRIG(roleInGroupGuid);
		if (rig == null)
		{
			return null;
		}

		Group group = this.stubService.getGroup(rig.getGroupGuid());
		Role role = this.stubService.getRole(rig.getRoleGuid());
		if (activeOnly && (!group.isActive() || !role.isActive()))
		{
			return null;
		}

		List<User> userList = this.stubService.getOrgStub().listUserOfRIG(rig.getGuid());
		if (!SetUtils.isNullList(userList))
		{
			Iterator<User> it = userList.iterator();
			while (it.hasNext())
			{
				User user = it.next();
				if ("SYSTEM.INTERNAL".equals(user.getUserId()))
				{
					it.remove();
				}
				if (activeOnly && !user.isActive())
				{
					it.remove();
				}
			}
		}
		return userList;
	}

	protected List<User> listUserByRoleInGroup(String groupId, String roleId, boolean activeOnly) throws ServiceRequestException
	{
		List<User> userList = this.listAllUserByRoleInGroup(groupId, roleId);
		if (!SetUtils.isNullList(userList))
		{
			Iterator<User> it = userList.iterator();
			while (it.hasNext())
			{
				User user = it.next();
				if (activeOnly && !user.isActive())
				{
					it.remove();
				}
			}
		}
		return userList;
	}

	public List<User> listUserByRoleInGroupGuid(String groupGuid, String roleGuid, boolean activeOnly) throws ServiceRequestException
	{
		RIG rig = this.stubService.getRIGByGroupAndRole(groupGuid, roleGuid);
		if (rig != null)
		{
			List<User> userList = this.listAllUserByRoleInGroup(rig.getGuid());
			if (!SetUtils.isNullList(userList))
			{
				Iterator<User> it = userList.iterator();
				while (it.hasNext())
				{
					User user = it.next();
					if (activeOnly && !user.isActive())
					{
						it.remove();
					}
				}
			}
			return userList;
		}

		return null;
	}

	public List<User> listUserInRole(String roleGuid) throws ServiceRequestException
	{
		List<User> userList = new ArrayList<User>();

		List<RIG> rigList = this.stubService.listRIGByRoleGuid(roleGuid);
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				List<User> tempList = this.stubService.listUserByRoleInGroup(rig.getGuid());
				if (!SetUtils.isNullList(tempList))
				{
					for (User user : tempList)
					{
						if (!userList.contains(user))
						{
							userList.add(user);
						}
					}
				}
			}
		}

		if (!SetUtils.isNullList(userList))
		{
			Collections.sort(userList, new UserComparator(null, true));
		}
		return userList;
	}

	public List<User> listUserInGroup(String groupGuid, boolean cascade) throws ServiceRequestException
	{
		List<User> userList = null;
		if (cascade)
		{
			userList = this.listAllUserOfGroupWithSubGroup(groupGuid, null);
		}
		else
		{
			userList = this.listUserInGroup(groupGuid);
		}

		if (!SetUtils.isNullList(userList))
		{
			Collections.sort(userList, new UserComparator(null, true));
		}

		return userList;
	}

	/**
	 * 取得当前选定组的所有用户，不包含子组
	 * 
	 * @param groupGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> listUserInGroup(String groupGuid) throws ServiceRequestException
	{
		List<User> userList = new ArrayList<User>();
		List<RIG> rigList = this.stubService.getOrgStub().listRIGByGroup(groupGuid);
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				List<User> userList_ = this.listUserByRoleInGroup(rig.getGuid(), true);
				if (!SetUtils.isNullList(userList_))
				{
					for (User user : userList_)
					{
						if (!userList.contains(user))
						{
							userList.add(user);
						}
					}
				}
			}
		}

		return userList;
	}

	/**
	 * 取得指定组及其子组的所有用户
	 * 
	 * @param groupGuid
	 * @param groupId
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> listAllUserOfGroupWithSubGroup(String groupGuid, String groupId) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(groupGuid))
		{
			Group group = this.stubService.getGroupById(groupId);
			if (group == null)
			{
				return null;
			}
			groupGuid = group.getGuid();
		}

		List<User> userList = new ArrayList<User>();
		List<Group> groupList = this.stubService.listAllSubGroup(groupGuid, null, true);
		if (!SetUtils.isNullList(groupList))
		{
			for (Group group : groupList)
			{
				List<User> userList_ = this.listUserInGroup(group.getGuid());
				if (!SetUtils.isNullList(userList_))
				{
					for (User user : userList_)
					{
						if (!userList.contains(user))
						{
							userList.add(user);
						}
					}
				}
			}
		}
		List<User> userList_ = this.listUserInGroup(groupGuid);
		if (!SetUtils.isNullList(userList_))
		{
			for (User user : userList_)
			{
				if (!userList.contains(user))
				{
					userList.add(user);
				}
			}
		}

		return userList;
	}

	protected List<User> listUserInGroupAndSubGroup(String groupGuid) throws ServiceRequestException
	{
		return this.listUserInGroup(groupGuid, true);
	}

	protected User obsoleteUser(String userId) throws ServiceRequestException
	{
		User user = this.getUserById(userId, false);
		if (user == null)
		{
			throw new ServiceRequestException(null, "user " + userId + " not found.");
		}
		if ("admin".equalsIgnoreCase(userId) || userId.startsWith("SYSTEM"))
		{
			return user;
		}
		String operatorGuid = this.stubService.getOperatorGuid();

		user.setActive(false);
		user.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			sds.save(user);
			UpperKeyMap filter = new UpperKeyMap();
			filter.put("USERGUID", user.getGuid());
			List<Session> query = sds.listFromCache(Session.class, new FieldValueEqualsFilter<Session>(filter));
			if (!SetUtils.isNullList(query))
			{
				for (Session session : query)
				{
					try
					{
						((LICImpl) this.stubService.getLIC()).getSessionStub().deleteSessionInside(session.getGuid());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}

			user = this.getUserById(userId, false);

			return user;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void resetUserPassword(String userId) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			User user = this.getUserById(userId, false);
			if (user == null)
			{
				throw new ServiceRequestException(null, "not found user: " + userId);
			}

			user.put("PASSWORD", EncryptUtils.encryptMD5(user.getUserId()));

			sds.save(user);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected User saveUser(User user) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		boolean isCreate = false;

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(user);

		try
		{
			User retUser = null;

			// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			String userGuid = user.getGuid();

			String operatorGuid = this.stubService.getOperatorGuid();

			user.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (StringUtils.isGuid(userGuid))
			{
				// NOTE never update password by method saveUser,
				// use method updateUserPassword instead.
				user.remove("PASSWORD");

				// NOTE never update user status by method saveUser,
				// use method in/activeUser instead.
				user.remove("ISACTIVE");
			}
			else
			{
				isCreate = true;
				user.put(SystemObject.CREATE_USER_GUID, operatorGuid);
				String pwd = user.getPassword();
				if (StringUtils.isNullString(pwd))
				{
					pwd = user.getUserId();
				}
				user.put("PASSWORD", EncryptUtils.encryptMD5(pwd));

				// NOTE default new user status to active.
				user.setActive(true);
			}

			User oldUser = this.getUserById(user.getUserId(), false);
			if (oldUser != null && !oldUser.getGuid().equals(userGuid))
			{
				throw new ServiceRequestException("ID_DS_UNIQUE_ID", "id is uniqu");
			}

			String ret = sds.save(user);

			if (isCreate)
			{
				userGuid = ret;

				// create user myFolder
				Folder myFolder = new Folder();
				myFolder.setFolderType(FolderTypeEnum.PRIVATE);
				myFolder.setName("\\");
				// myFolder.setParentGuid(retGuid);
				myFolder.put(Folder.OWNER_USER_GUID, userGuid);
				myFolder.put(SystemObject.CREATE_USER_GUID, userGuid);
				myFolder.put(Folder.UPDATE_USER_GUID, userGuid);

				sds.save(myFolder);
			}

			retUser = this.getUser(userGuid, false);

			//DataServer.getTransactionManager().commitTransaction();

			return retUser;
		}
		catch (DynaDataException e)
		{
			//DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e, isCreate ? user.getFullname() : user.getOriginalFullname());
		}
		catch (Exception e)
		{
			//DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}
	}

	protected void updateUserPassword(String userId, String oldPwd, String newPwd) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			User user = this.checkUserPassword(userId, oldPwd);
			if (user == null)
			{
				throw new ServiceRequestException("ID_APP_INVALID_OLD_PWD", "invalid old password.");
			}

			user.put("PASSWORD", EncryptUtils.encryptMD5(newPwd));

			sds.save(user);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected boolean isUserInGroup(String groupGuid, String userGuid) throws ServiceRequestException
	{
		List<User> userList = this.listUserInGroupAndSubGroup(groupGuid);
		boolean isUserInGroup = false;
		if (!SetUtils.isNullList(userList))
		{
			for (User user : userList)
			{
				if (user.getGuid().equals(userGuid))
				{
					isUserInGroup = true;
					break;
				}
			}
		}

		return isUserInGroup;

	}

	protected boolean isUserInRIG(String roleInGroupGuid, String userGuid) throws ServiceRequestException
	{
		List<User> userList = this.listUserByRoleInGroup(roleInGroupGuid, false);
		boolean isUserInGIG = false;
		if (!SetUtils.isNullList(userList))
		{
			for (User user : userList)
			{
				if (user.getGuid().equals(userGuid))
				{
					isUserInGIG = true;
					break;
				}
			}
		}

		return isUserInGIG;

	}

	protected boolean isUserInRole(String roleGuid, String userGuid) throws ServiceRequestException
	{
		List<User> userList = this.listUserInRole(roleGuid, false);
		boolean isUserInRole = false;
		if (!SetUtils.isNullList(userList))
		{
			for (User user : userList)
			{
				if (user.getGuid().equals(userGuid))
				{
					isUserInRole = true;
					break;
				}
			}
		}

		return isUserInRole;

	}

	public List<User> listUserInRole(String roleGuid, boolean activeOnly) throws ServiceRequestException
	{
		List<User> userList = new ArrayList<User>();
		List<RIG> rigList = this.stubService.getOrgStub().listRIGByRole(roleGuid);
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				List<User> userList_ = this.listUserByRoleInGroup(rig.getGuid(), activeOnly);
				if (!SetUtils.isNullList(userList_))
				{
					for (User user : userList_)
					{
						if (!userList.contains(user))
						{
							userList.add(user);
						}
					}
				}
			}
		}

		return userList;
	}

	private User maskUser(User user) throws ServiceRequestException
	{
		if (this.stubService.getUserSignature().getUserGuid().equalsIgnoreCase(user.getGuid()))
		{
			return user;
		}

		boolean supervisor = Constants.isSupervisor(true, this.stubService);
		if (!supervisor)
		{
			return user;
		}

		if (user != null && user.isShield())
		{
			user.setCity(User.MASK_CHAR);
			user.setCountry(User.MASK_CHAR);
			user.setEmail(User.MASK_CHAR);
			user.setFax(User.MASK_CHAR);
			user.setMobile(User.MASK_CHAR);
			user.setTel(User.MASK_CHAR);
			user.setZipCode(User.MASK_CHAR);
			user.setCity(User.MASK_CHAR);
			user.setAddress(User.MASK_CHAR);
		}

		return user;
	}

	public UserAgent saveUserAgent(UserAgent agent) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();

		if (agent.getStartDate() == null)
		{
			agent.setStartDate(new Date());
		}
		if (!StringUtils.isGuid(agent.getGuid()))
		{
			agent.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			agent.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		}
		else
		{
			agent.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		}

		try
		{
			String re = sds.save(agent);
			if (re != null)
			{
				return this.getUserAgentByPrincipal(agent.getPrincipalGuid(), null);
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public User getUserWitType(String guid) throws ServiceRequestException
	{
		String type = null;
		String name = null;
		String guid_ = null;

		User user = this.getUserWithoutThrowable(guid);
		Group group = this.getGroupWithoutThrowable(guid);
		RIG rig = this.getRIGWithoutThrowable(guid);
		if (user != null)
		{
			type = "USER";
			name = user.getUserName();
			guid_ = user.getGuid();
		}
		else if (group != null)
		{
			type = "GROUP";
			name = group.getGroupName();
			guid_ = group.getGuid();
		}
		else if (rig != null)
		{
			type = "RIG";
			name = rig.getGroupName() + "/" + rig.getRoleName();
			guid_ = rig.getGroupGuid();
		}

		if (!StringUtils.isNullString(type))
		{
			user = new User();
			user.put("TYPE", type);
			user.setUserName(name);
			user.setGuid(guid_);

			return user;
		}
		return null;
	}

	private User getUserWithoutThrowable(String userGuid)
	{
		try
		{
			return this.getUser(userGuid, false);
		}
		catch (Exception e)
		{
		}
		return null;
	}

	private Group getGroupWithoutThrowable(String groupGuid)
	{
		try
		{
			return this.stubService.getGroup(groupGuid);
		}
		catch (Exception e)
		{
		}
		return null;
	}

	private RIG getRIGWithoutThrowable(String rigGuid)
	{
		try
		{
			return this.stubService.getRIG(rigGuid);
		}
		catch (Exception e)
		{
		}
		return null;
	}

	public List<UserAgent> listUserAgent(String principalGuid, String agentGuid, Boolean valid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> param = new HashMap<String, Object>();
		if (StringUtils.isGuid(principalGuid))
		{
			param.put(UserAgent.PRINCIPALGUID, principalGuid);
		}
		if (StringUtils.isGuid(agentGuid))
		{
			param.put(UserAgent.AGENTGUID, agentGuid);
		}
		// if (valid != null)
		// {
		// param.put(UserAgent.VALID, BooleanUtils.getBooleanStringYN(valid));
		// }

		List<UserAgent> userAgentList = sds.query(UserAgent.class, param);

		List<UserAgent> resultList = new ArrayList<UserAgent>();

		if (valid != null && "Y".equals(BooleanUtils.getBooleanStringYN(valid)))
		{
			if (!SetUtils.isNullList(userAgentList))
			{
				for (UserAgent agent : userAgentList)
				{
					String startDate = DateFormat.formatYMD(agent.getStartDate());
					String finishDate = agent.getFinishDate() == null ? null : DateFormat.formatYMD(agent.getFinishDate());
					if (startDate.compareTo(DateFormat.formatYMD(DateFormat.getSysDate())) <= 0
							&& (finishDate == null || finishDate.compareTo(DateFormat.formatYMD(DateFormat.getSysDate())) >= 0))
					{
						resultList.add(agent);
					}
				}
			}
		}
		else
		{
			resultList = userAgentList;
		}

		this.decorateUserAgent4List(resultList);

		return resultList;

	}

	public UserAgent getUserAgentByPrincipal(String principalGuid, Boolean valid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(UserAgent.PRINCIPALGUID, principalGuid);
		// if (vaild != null)
		// {
		// param.put(UserAgent.VALID, BooleanUtils.getBooleanStringYN(vaild));
		// }

		List<UserAgent> userAgentList = sds.query(UserAgent.class, param);

		List<UserAgent> resultList = new ArrayList<UserAgent>();

		if (valid != null && "Y".equals(BooleanUtils.getBooleanStringYN(valid)))
		{
			if (!SetUtils.isNullList(userAgentList))
			{
				for (UserAgent agent : userAgentList)
				{
					String startDate = DateFormat.formatYMD(agent.getStartDate());
					String finishDate = agent.getFinishDate() == null ? null : DateFormat.formatYMD(agent.getFinishDate());
					if (startDate.compareTo(DateFormat.formatYMD(DateFormat.getSysDate())) <= 0
							&& (finishDate == null || finishDate.compareTo(DateFormat.formatYMD(DateFormat.getSysDate())) >= 0))
					{
						resultList.add(agent);
					}
				}
			}
		}
		else
		{
			resultList = userAgentList;
		}

		UserAgent userAgent = null;

		if (!SetUtils.isNullList(resultList))
		{
			userAgent = resultList.get(0);
		}

		this.decorateUserAgent(userAgent);

		return userAgent;

	}

	public UserWorkFolder getUserWorkFolder(String userGuid, UserWorkFolderTypeEnum folderTypeEnum)
	{
		UserWorkFolder workFolder = new UserWorkFolder();
		workFolder.setUserGuid(userGuid);
		workFolder.setFolderType(folderTypeEnum);

		SystemDataService sds = DataServer.getSystemDataService();
		return sds.queryObject(UserWorkFolder.class, workFolder);
	}

	public void saveUserWorkFolder(String userGuid, UserWorkFolderTypeEnum folderType, String workFolderPath) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UserWorkFolder workFolder = this.getUserWorkFolder(userGuid, folderType);
		if (workFolder == null)
		{
			workFolder = new UserWorkFolder();
			workFolder.setUserGuid(userGuid);
			workFolder.setFolderType(folderType);
		}
		workFolder.setFolderPath(workFolderPath);

		try
		{
			sds.save(workFolder);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public String getPLMUserByMobile(String mobileUserID) throws ServiceRequestException
	{
		if (StringUtils.isNullString(mobileUserID))
		{
			return null;
		}
		List<User> userList = this.listUser(null, true);
		for (User user : userList)
		{
			if (mobileUserID.equalsIgnoreCase(user.getUserId()) || mobileUserID.equalsIgnoreCase(user.getUseridwx()) || mobileUserID.equalsIgnoreCase(user.getMobile()))
			{
				return user.getUserId();
			}
		}
		return null;
	}

	/**
	 * 判断当前用户的sessionid是否有效
	 * 
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean reportUserAuthentication(String sessionId, String userId) throws ServiceRequestException
	{
		if (StringUtils.isNullString(sessionId))
		{
			return false;
		}

		List<Session> sessionList = DataServer.getSystemDataService().listFromCache(Session.class, null);
		if (!SetUtils.isNullList(sessionList))
		{
			for (Session session : sessionList)
			{
				if (sessionId.equals(session.getGuid()))
				{
					boolean b = this.checkSession(session);
					if (b)
					{
						User user = this.stubService.getUserById(userId);
						if (user != null)
						{
							if (user.getGuid().equals(session.getUserGuid()))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 检查session是否过期
	 * 
	 * @param session
	 * @return
	 */
	private boolean checkSession(Session session)
	{
		if (session == null)
		{
			return false;
		}

		Date now = new Date();
		int timeout = this.serviceContext.getServerContext().getServerConfig().getSessionTimeout();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(session.getUpdateTime());
		calendar.add(Calendar.MINUTE, timeout);
		if (now.before(calendar.getTime()))
		{
			if (session.getLastAccesseTime() != null)
			{
				calendar.setTime(session.getLastAccesseTime());
				calendar.add(Calendar.MINUTE, 15);

				if (now.after(calendar.getTime()))
				{
					return false;
				}
			}
		}
		return true;
	}

	class UserComparator implements Comparator<User>
	{
		private String	sortField	= null;
		private boolean	isASC		= true;

		public UserComparator(String sortField, boolean isASC)
		{
			this.sortField = StringUtils.isNullString(sortField) ? "ID" : sortField;
			this.isASC = isASC;
		}

		@Override
		public int compare(User o1, User o2)
		{
			if (o1.get(sortField) == null && o2.get(sortField) == null)
			{
				return 0;
			}

			if (isASC)
			{
				if (o1.get(sortField) instanceof Date || o2.get(sortField) instanceof Date)
				{
					if (o1.get(sortField) == null)
					{
						return 1;
					}
					else if (o2.get(sortField) == null)
					{
						return -1;
					}
					else
					{
						return ((Date) o1.get(sortField)).compareTo((Date) o2.get(sortField));
					}
				}
				else
				{
					String v1 = StringUtils.convertNULLtoString(o1.get(sortField));
					String v2 = StringUtils.convertNULLtoString(o2.get(sortField));
					return v1.compareTo(v2);
				}
			}
			else
			{
				if (o1.get(sortField) instanceof Date || o2.get(sortField) instanceof Date)
				{
					if (o1.get(sortField) == null)
					{
						return -1;
					}
					else if (o2.get(sortField) == null)
					{
						return 1;
					}
					else
					{
						return ((Date) o2.get(sortField)).compareTo((Date) o1.get(sortField));
					}
				}
				else
				{
					String v1 = StringUtils.convertNULLtoString(o1.get(sortField));
					String v2 = StringUtils.convertNULLtoString(o2.get(sortField));
					return v2.compareTo(v1);
				}
			}
		}
	}

	private void decorateUserAgent4List(List<UserAgent> userAgentList)
	{
		if (!SetUtils.isNullList(userAgentList))
		{
			for (UserAgent userAgent : userAgentList)
			{
				decorateUserAgent(userAgent);
			}
		}
	}

	private void decorateUserAgent(UserAgent userAgent)
	{
		if (userAgent != null)
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String principalGuid = userAgent.getPrincipalGuid();
			String agentGuid = userAgent.getAgentGuid();
			String createUserGuid = userAgent.getCreateUserGuid();
			String updateUserGuid = userAgent.getUpdateUserGuid();

			if (!StringUtils.isNullString(principalGuid))
			{
				User principal = sds.get(User.class, principalGuid);
				userAgent.setPrincipalName(principal == null ? null : principal.getName());
			}

			if (!StringUtils.isNullString(agentGuid))
			{
				User agent = sds.get(User.class, agentGuid);
				userAgent.setAgentName(agent == null ? null : agent.getName());
			}

			if (!StringUtils.isNullString(createUserGuid))
			{
				User createUser = sds.get(User.class, createUserGuid);
				userAgent.put("CREATEUSERNAME", createUser == null ? null : createUser.getName());
			}

			if (!StringUtils.isNullString(updateUserGuid))
			{
				User updateUser = sds.get(User.class, updateUserGuid);
				userAgent.put("UPDATEUSERNAME", updateUser == null ? null : updateUser.getName());
			}
		}
	}
}
