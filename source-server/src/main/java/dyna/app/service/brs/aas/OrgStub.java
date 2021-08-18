/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: OrgStub 与组织结构相关的操作分支
 * Wanglei 2010-7-22
 */
package dyna.app.service.brs.aas;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.aas.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 与组织结构相关的操作分支
 * 
 * @author Wanglei
 * 
 */
@Component
public class OrgStub extends AbstractServiceStub<AASImpl>
{
	protected String assignRoleToGroup(String roleGuid, String groupGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(roleGuid) || !StringUtils.isGuid(groupGuid))
			{
				throw new ServiceRequestException("role or group is not exist.");
			}

			String operatorGuid = this.stubService.getOperatorGuid();

			RIG rig = new RIG();
			rig.setRoleGuid(roleGuid);
			rig.setGroupGuid(groupGuid);
			rig.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			rig.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			String ret = sds.save(rig);

			return ret;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void assignUserToRoleInGroup(String userGuid, String roleInGroupGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			User user = this.stubService.getUser(userGuid);
			if (user == null)
			{
				throw new ServiceRequestException("user not exist.");
			}

			RIG rig = this.getRIG(roleInGroupGuid);
			if (rig == null)
			{
				throw new ServiceRequestException("role in group " + roleInGroupGuid + " not exist.");
			}

			boolean updateUser = false;
			if (StringUtils.isNullString(user.getDefaultGroupGuid()))
			{
				user.setDefaultGroupGuid(rig.getGroupGuid());
				updateUser = true;
			}
			if (StringUtils.isNullString(user.getDefaultRoleGuid()))
			{
				user.setDefaultRoleGuid(rig.getRoleGuid());
				updateUser = true;
			}

			if (updateUser)
			{
				sds.save(user);
			}

			String operatorGuid = this.stubService.getOperatorGuid();

			URIG urig = new URIG();
			urig.setUserGuid(user.getGuid());
			urig.setRoleGroupGuid(roleInGroupGuid);
			urig.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			urig.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			sds.save(urig);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("failed to assign user to group", e);
		}

	}

	protected void assignUserToRoleInGroup(String userGuid, String roleGuid, String groupGuid) throws ServiceRequestException
	{
		try
		{
			String rigGuid = null;

			RIG rig = this.getRIGByGroupAndRole(groupGuid, roleGuid, false);
			if (rig == null)
			{
				rigGuid = this.assignRoleToGroup(roleGuid, groupGuid);
			}
			else
			{
				rigGuid = rig.getGuid();
			}

			this.assignUserToRoleInGroup(userGuid, rigGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected RIG getRIG(String rigGuid) throws DynaDataException
	{
		return DataServer.getSystemDataService().get(RIG.class, rigGuid);
	}

	private RIG getRIG(String roleId, String groupId) throws ServiceRequestException
	{
		List<RIG> rigList = DataServer.getSystemDataService().listFromCache(RIG.class, null);
		for (RIG rig : rigList)
		{
			Group group = this.stubService.getGroup(rig.getGroupGuid());
			Role role = this.stubService.getRole(rig.getRoleGuid());
			if (group == null || role == null)
			{
				continue;
			}
			if (group.getGroupId().equals(groupId) && role.getRoleId().equals(roleId))
			{
				return rig;
			}
		}
		return null;
	}

	protected RIG getRIGByGroupAndRole(final String groupGuid, final String roleGuid, boolean isValid) throws ServiceRequestException
	{
		RIG rig = null;
		UpperKeyMap upperKeyMap = new UpperKeyMap();
		upperKeyMap.put(RIG.GROUP_GUID, groupGuid);
		upperKeyMap.put(RIG.ROLE_GUID, roleGuid);
		List<RIG> rigList = DataServer.getSystemDataService().listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(upperKeyMap));
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig_ : rigList)
			{
				rig = rig_;
			}
		}

		if (rig == null)
		{
			return null;
		}
		Role role = this.stubService.getRole(roleGuid);
		Group group = this.stubService.getGroup(groupGuid);
		if (isValid)
		{
			if (group.isActive()==false||role.isActive()==false)
			{
				return null;
			}
		}

		rig.put("ROLENAME", role == null ? null : role.getRoleName());
		rig.put("ROLEID", role == null ? null : role.getRoleId());
		rig.put("GROUPNAME", group == null ? null : group.getGroupName());
		rig.put("GROUPID", group == null ? null : group.getGroupId());

		return rig;
	}

	protected List<RIG> listRoleInGroup() throws ServiceRequestException
	{
		return DataServer.getSystemDataService().listFromCache(RIG.class, null);
	}

	protected List<RIG> listRoleInGroupByRoleGuid(String roleGuid) throws ServiceRequestException
	{
		List<RIG> retList = new ArrayList<RIG>();

		List<RIG> rigList = this.listRoleInGroup();
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				if (rig.getRoleGuid().equals(roleGuid))
				{
					retList.add(rig);
				}
			}
		}

		return retList;
	}

	protected List<RIG> listRIGByGroup(String groupGuid)
	{
		List<RIG> rigList = DataServer.getSystemDataService().listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.GROUP_GUID, groupGuid));
		return rigList;
	}

	protected List<RIG> listRIGByRole(String roleGuid)
	{
		List<RIG> rigList = DataServer.getSystemDataService().listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(RIG.ROLE_GUID, roleGuid));
		return rigList;
	}

	protected List<User> listUserOfRIG(String rigGuid) throws ServiceRequestException
	{
		List<User> userList = new ArrayList<User>();

		List<URIG> urigList = DataServer.getSystemDataService().listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.ROLE_GROUP_GUID, rigGuid));
		for (URIG urig : urigList)
		{
			if (urig != null)
			{
				String userGuid = urig.getUserGuid();
				User user = this.stubService.getUser(userGuid);
				if (user != null && !userList.contains(user))
				{
					userList.add(user);
				}
			}
		}
		return userList;
	}

	protected List<URIG> listUserRoleInGroupByUser(String userId) throws ServiceRequestException
	{
		User user = this.stubService.getUserById(userId);
		if (user == null)
		{
			return null;
		}

		List<URIG> urigList = DataServer.getSystemDataService().listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(URIG.USER_GUID, user.getGuid()));

		return urigList;
	}

	protected List<URIG> listUserRoleInGroupByUserGroup(String userId, String groupId) throws ServiceRequestException
	{
		List<URIG> dataList = new ArrayList<URIG>();

		List<URIG> urigList = DataServer.getSystemDataService().listFromCache(URIG.class, null);
		for (URIG urig : urigList)
		{
			User user = this.stubService.getUser(urig.getUserGuid());
			if (user != null && user.getUserId().equals(userId))
			{
				RIG rig = this.getRIG(urig.getRoleGroupGuid());
				if (rig != null)
				{
					Group group = this.stubService.getGroup(rig.getGroupGuid());
					if (group != null && group.getGroupId().equals(groupId) && !dataList.contains(urig))
					{
						dataList.add(urig);
					}
				}
			}
		}
		return dataList;
	}

	private void resetUserDefaultRoleGroup(User user, RIG rig) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			URIG urig = null;
			String groupGuid = rig.getGroupGuid();
			if (groupGuid.equals(user.getDefaultGroupGuid()))
			{
				user.setDefaultGroupGuid(null);

				List<URIG> urigList = this.listUserRoleInGroupByUser(user.getUserId());
				if (!SetUtils.isNullList(urigList))
				{
					urig = urigList.get(0);
				}
			}
			else
			{
				String roleGuid = rig.getRoleGuid();
				if (roleGuid.equals(user.getDefaultRoleGuid()))
				{
					user.setDefaultRoleGuid(null);
					List<URIG> urigList = this.listUserRoleInGroupByUserGroup(user.getUserId(), user.getDefaultGroupGuid());
					if (SetUtils.isNullList(urigList))
					{
						urigList = this.listUserRoleInGroupByUser(user.getUserId());
					}
					if (!SetUtils.isNullList(urigList))
					{
						urig = urigList.get(0);
					}
				}
			}

			if (urig != null)
			{
				user.setDefaultGroupGuid(urig.getGroupGuid());
				user.setDefaultRoleGuid(urig.getRoleGuid());
				sds.save(user);
			}
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("failed to reset user to group", e);
		}
	}

	protected void revokeRoleFromGroup(String roleId, String groupId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		RIG rig = this.getRIG(roleId, groupId);
		if (rig == null)
		{
			return;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			sds.delete(rig);

			List<User> userList = this.stubService.getUserStub().listAllUserByRoleInGroup(rig.getGuid());
			if (!SetUtils.isNullList(userList))
			{
				for (User user : userList)
				{
					this.resetUserDefaultRoleGroup(user, rig);
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("failed to revokeRoleFromGroup", e);
		}
	}

	protected URIG getURIG(String userId, final String roleInGroupGuid) throws ServiceRequestException
	{
		final User user = this.stubService.getUserById(userId);
		if (user != null)
		{
			UpperKeyMap filter = new UpperKeyMap();
			filter.put(URIG.USER_GUID, user.getGuid());
			filter.put(URIG.ROLE_GROUP_GUID, roleInGroupGuid);
			List<URIG> urigList = DataServer.getSystemDataService().listFromCache(URIG.class, new FieldValueEqualsFilter<URIG>(filter));

			if (!SetUtils.isNullList(urigList))
			{
				return urigList.get(0);
			}
		}
		return null;
	}

	protected void revokeUserFromRoleInGroup(String userId, String roleInGroupGuid) throws ServiceRequestException
	{
		URIG urig = this.getURIG(userId, roleInGroupGuid);
		if (urig == null)
		{
			return;
		}
		RIG rig = this.getRIG(urig.getRoleGroupGuid());
		if (rig == null)
		{
			return;
		}

		User user = this.stubService.getUserStub().getUserById(userId, false);
		if (user == null)
		{
			return;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			SystemDataService sds = DataServer.getSystemDataService();
			sds.delete(urig);

			this.resetUserDefaultRoleGroup(user, rig);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("failed to revokeUserFromRoleInGroup", e);
		}
	}

	protected void revokeUserFromRoleInGroup(String userId, String roleId, String groupId) throws ServiceRequestException
	{
		RIG rig = this.getRIG(roleId, groupId);
		if (rig == null)
		{
			return;
		}

		this.revokeUserFromRoleInGroup(userId, rig.getGuid());
	}

	protected List<RIG> listRIGByGroupGuidAndRoleGuid(String groupGuid, String roleGuid)
	{
		UpperKeyMap upperKeyMap = new UpperKeyMap();
		if (!StringUtils.isNull(groupGuid))
		{
			upperKeyMap.put(RIG.GROUP_GUID, groupGuid);
		}
		if (!StringUtils.isNull(roleGuid))
		{
			upperKeyMap.put(RIG.ROLE_GUID, roleGuid);
		}
		List<RIG> rigList = DataServer.getSystemDataService().listFromCache(RIG.class, new FieldValueEqualsFilter<RIG>(upperKeyMap));

		return rigList;
	}
}
