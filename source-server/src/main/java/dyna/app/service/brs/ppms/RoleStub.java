/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 角色管理
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.PMAuthorityEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangLHB
 *         角色管理
 * 
 */
@Component
public class RoleStub extends AbstractServiceStub<PPMSImpl>
{

	public List<RoleMembers> listUserInProjectRole(String projectRoleGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(RoleMembers.PROJECTROLEGUID, projectRoleGuid);
			List<RoleMembers> roleMembersList = sds.query(RoleMembers.class, filter);
			// 将User对象放到RoleMembers中
			if (!SetUtils.isNullList(roleMembersList))
			{
				for (RoleMembers role : roleMembersList)
				{
					String userGuid = role.getUserGuid();
					if (StringUtils.isGuid(userGuid))
					{
						User user = this.stubService.getAAS().getUser(userGuid);
						if (user != null)
						{
							role.setUser(user);
						}
					}
				}
			}
			return roleMembersList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RoleMembers> listUserInProject(String projectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<RoleMembers> roleMembersList = new ArrayList<RoleMembers>();
		List<RoleMembers> value = null;
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProjectRole.TYPEGUID, projectGuid);
			List<ProjectRole> roleList = sds.query(ProjectRole.class, filter);

			for (ProjectRole role : roleList)
			{
				roleMembersList.addAll(this.listUserInProjectRole(role.getGuid()));
			}
			// 去除重复成员
			if (!SetUtils.isNullList(roleMembersList))
			{
				Map<String, RoleMembers> tempMap = new HashMap<String, RoleMembers>();// 过滤重复成员
				value = new ArrayList<RoleMembers>();
				for (RoleMembers role : roleMembersList)
				{
					if (tempMap.get(role.getUserGuid()) == null)
					{
						tempMap.put(role.getUserGuid(), role);
						value.add(role);
					}
				}
			}
			return value;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void deleteProjectRole(String projectRoleGuid) throws ServiceRequestException
	{
		this.stubService.getBaseConfigStub().delProjectRole(projectRoleGuid);
	}


	public List<RoleMembers> saveUserInProjectRole(String pmRoleGuid, List<String> userGuidList)
			throws ServiceRequestException
	{
		ProjectRole projectRole = this.getProjectRole(pmRoleGuid);
		if (projectRole == null)
		{
			return null;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!SetUtils.isNullList(userGuidList))
			{
				// 先删除原来的
				this.deleteProjectRoleUserByRoleGuid(projectRole.getGuid());
				// 保存
				this.saveProjectRoleUser(pmRoleGuid, userGuidList);
			}
			else
			{
				// 删除所有的user
				this.deleteProjectRoleUserByRoleGuid(projectRole.getGuid());
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
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		return this.listUserInProjectRole(pmRoleGuid);

	}

	/**
	 * @param pmRoleGuid
	 * @param userGuidList
	 * @throws ServiceRequestException
	 */
	private void saveProjectRoleUser(String pmRoleGuid, List<String> userGuidList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		try
		{
			if (!SetUtils.isNullList(userGuidList))
			{
				int i = 0;
				for (String userGuid : userGuidList)
				{
					RoleMembers roleUser = new RoleMembers();
					roleUser.setProjectRoleGuid(pmRoleGuid);
					roleUser.setUserGuid(userGuid);
					roleUser.setCreateUserGuid(operatorGuid);
					roleUser.setUpdateUserGuid(operatorGuid);
					roleUser.setSequence(i);
					sds.save(roleUser);
					i++;
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * @param pmRoleGuid
	 * @param userGuidList
	 * @throws ServiceRequestException
	 */
	public RoleMembers saveProjectRoleUser(RoleMembers roleMember) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		roleMember.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		if (StringUtils.isNullString(roleMember.getGuid()))
		{
			roleMember.put(SystemObject.CREATE_USER_GUID, operatorGuid);

		}

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			String guid = sds.save(roleMember);
			if (StringUtils.isGuid(guid))
			{
				roleMember.setGuid(guid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return roleMember;

	}

	/**
	 * @param guid
	 * @throws ServiceRequestException
	 */
	private void deleteProjectRoleUserByRoleGuid(String projectRoleGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(projectRoleGuid))
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(RoleMembers.PROJECTROLEGUID, projectRoleGuid);
			try
			{
				List<RoleMembers> notifierList = this.listUserInProjectRole(projectRoleGuid);
				if (!SetUtils.isNullList(notifierList))
				{
					sds.delete(RoleMembers.class, filter, "delete");
				}
			}
			catch (DynaDataException e)
			{
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
		}

	}

	protected ProjectRole saveProjectRole(ProjectRole pmRole, boolean checkPMAuthority) throws ServiceRequestException
	{
		if (checkPMAuthority)
		{
			FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(
					new ObjectGuid(pmRole.getTypeClassGuid(), null, pmRole.getTypeGuid(), null), false);
			FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(
					objectByGuid.getObjectGuid(), false);
			this.stubService.getPMAuthorityStub().hasPMAuthority(object, PMAuthorityEnum.PROJECT_TEAMRESOURCES);
		}
		String operatorGuid = this.stubService.getOperatorGuid();
		pmRole.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		if (StringUtils.isNullString(pmRole.getGuid()))
		{
			pmRole.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String result = sds.save(pmRole);
			if (StringUtils.isGuid(result))
			{
				return this.getProjectRole(result);
			}
			else
			{
				return this.getProjectRole(pmRole.getGuid());
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected ProjectRole getProjectRole(String pmRoleGuid) throws ServiceRequestException
	{

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, pmRoleGuid);
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.queryObject(ProjectRole.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected List<ProjectRole> listProjectRole(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(ProjectRole.TYPEGUID, projectObjectGuid.getGuid());
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.query(ProjectRole.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<RoleMembers> listRoleMembers(String projectRoleGuid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(RoleMembers.PROJECTROLEGUID, projectRoleGuid);
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.query(RoleMembers.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 复制团队
	 * 
	 * @param fromObjectGuid
	 * @param toObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected Map<String, String> copyProjectRole(ObjectGuid fromObjectGuid, ObjectGuid toObjectGuid)
			throws ServiceRequestException
	{
		List<ProjectRole> list = this.listProjectRole(toObjectGuid);
		Map<String, String> returnMap = new HashMap<String, String>();
		if (SetUtils.isNullList(list) == false)
		{
			SystemDataService sds = DataServer.getSystemDataService();

			for (ProjectRole pmRole : list)
			{
				pmRole.setTypeGuid(fromObjectGuid.getGuid());
				pmRole.setTypeClassGuid(fromObjectGuid.getClassGuid());
				String oldguid = pmRole.getGuid();
				pmRole.setGuid(null);
				pmRole = this.saveProjectRole(pmRole, false);
				returnMap.put(oldguid, pmRole.getGuid());

				List<RoleMembers> roleMembersList = this.listUserInProjectRole(oldguid);
				if (SetUtils.isNullList(roleMembersList) == false)
				{
					for (RoleMembers roleMembers : roleMembersList)
					{
						roleMembers.setGuid(null);
						sds.save(roleMembers);
					}
				}
			}
		}
		return returnMap;
	}

	/**
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProjectRole> listProjectRoleByProjectGuid(String projectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(ProjectRole.TYPEGUID, projectGuid);
		try
		{
			return sds.query(ProjectRole.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProjectRole> listProjectRoleByUser(String projectGuid, String operator) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(ProjectRole.TYPEGUID, projectGuid);
		filter.put(RoleMembers.USERGUID, operator);
		try
		{
			return sds.query(ProjectRole.class, filter, "selectRoleByUser");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public RoleMembers getRoleMemberByGuid(String roleMemberGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, roleMemberGuid);
			List<RoleMembers> roleMembersList = sds.query(RoleMembers.class, filter);
			if (!SetUtils.isNullList(roleMembersList))
			{
				return roleMembersList.get(0);
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteRoleMemberByGuid(String roleMemberGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		RoleMembers roleMember = this.stubService.getRoleStub().getRoleMemberByGuid(roleMemberGuid);
		if (roleMember != null)
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, roleMember.getGuid());
			sds.delete(RoleMembers.class, filter, "delete");
		}
	}
}
