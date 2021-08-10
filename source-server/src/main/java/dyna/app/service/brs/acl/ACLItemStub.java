/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLSubjectStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.acl.AbstractACLItem;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.acl.AclService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.AAS;

/**
 * @author Wanglei
 * 
 */
public class ACLItemStub extends AbstractServiceStub<ACLImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected ACLItemStub(ServiceContext context, ACLImpl service)
	{
		super(context, service);
	}

	protected ACLItem getACLItemForObjectByUser(ObjectGuid objectGuid, String userId, String groupId, String roleId) throws ServiceRequestException
	{
		AAS aas = this.stubService.getAAS();

		User user = aas.getUserById(userId);
		if (user == null)
		{
			throw new ServiceRequestException("not found user: " + userId);
		}

		Group group = aas.getGroupById(groupId);
		if (group == null)
		{
			throw new ServiceRequestException("not found group: " + groupId);
		}

		if (group.isAdminGroup())
		{
			return new ACLItem(PermissibleEnum.YES);
		}

		Role role = aas.getRoleById(roleId);
		String roleGuid = null;
		if (role != null)
		{
			roleGuid = role.getGuid();
		}
		try
		{
			ClassInfo classInfo = null;
			if (objectGuid.getClassName() != null)
			{
				classInfo = this.stubService.getEMM().getClassByName(objectGuid.getClassName());
			}
			if (classInfo == null && objectGuid.getClassGuid() != null)
			{
				classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
			}
			AclService aclService = DataServer.getAclService();
			String authority = aclService.getAuthority(objectGuid.getGuid(), classInfo.getName(), user.getGuid(), group.getGuid(), roleGuid);
			return new ACLItem(authority);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected ACLItem saveACLItem(ACLItem aclItem) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		boolean isCreate = false;

		try
		{
			String userGuid = this.stubService.getOperatorGuid();

			if (!StringUtils.isGuid(aclItem.getGuid()))
			{
				isCreate = true;
				aclItem.put(SystemObject.CREATE_USER_GUID, userGuid);
			}

			aclItem.put(SystemObject.UPDATE_USER_GUID, userGuid);

			if (StringUtils.isNullString(aclItem.getValueGuid()))
			{
				AccessTypeEnum accessType = aclItem.getAccessType();
				switch (accessType)
				{
				case USER:
				case RIG:
				case ROLE:
				case GROUP:
					throw new ServiceRequestException("ID_APP_MISS_VALUE_ACLITEM", "missing value if access type is USER/RIG/ROLE/GROUP");
				default:
					break;
				}
			}

			String guid = sds.save(aclItem);
			if (!isCreate)
			{
				guid = aclItem.getGuid();
			}

			return (ACLItem) this.getACLItem(ACLItem.class, guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, aclItem.getValueName() == null ? "" : aclItem.getValueName());
		}
	}

	protected List<ACLItem> listACLItemBySubject(String aclSubjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<ACLItem> list = sds.listFromCache(ACLItem.class, new FieldValueEqualsFilter<ACLItem>(ACLItem.MASTER_FK, aclSubjectGuid));
			if (list != null)
			{
				Collections.sort(list, new Comparator<ACLItem>()
				{

					@Override
					public int compare(ACLItem o1, ACLItem o2)
					{
						return o1.getPrecedence().compareTo(o2.getPrecedence());
					}
				});
				decodeObjectValueName(list);
			}
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected void decodeObjectValueName(List<? extends AbstractACLItem> list) throws ServiceRequestException
	{
		if (list != null)
		{
			for (AbstractACLItem item : list)
			{
				if (item.getAccessType() == AccessTypeEnum.GROUP)
				{
					if (!StringUtils.isNullString(item.getValueGuid()))
					{
						Group group = this.stubService.getAAS().getGroup(item.getValueGuid());
						if (group != null)
						{
							item.setValueName(group.getGroupId() + "-" + group.getGroupName());
						}
					}
				}
				else if (item.getAccessType() == AccessTypeEnum.ROLE)
				{
					if (!StringUtils.isNullString(item.getValueGuid()))
					{
						Role role = this.stubService.getAAS().getRole(item.getValueGuid());
						if (role != null)
						{
							item.setValueName(role.getRoleId() + "-" + role.getRoleName());
						}
					}
				}
				else if (item.getAccessType() == AccessTypeEnum.RIG)
				{
					if (!StringUtils.isNullString(item.getValueGuid()))
					{
						RIG rig = this.stubService.getAAS().getRIG(item.getValueGuid());
						if (rig != null)
						{
							Group group = null;
							Role role = null;
							if (!StringUtils.isNullString(rig.getGroupGuid()))
							{
								group = this.stubService.getAAS().getGroup(rig.getGroupGuid());
							}
							if (!StringUtils.isNullString(rig.getRoleGuid()))
							{
								role = this.stubService.getAAS().getRole(rig.getRoleGuid());
							}
							if (group != null && role != null)
							{
								item.setValueName(group.getGroupName() + "-" + role.getRoleName());
							}
						}
					}

				}
				else if (item.getAccessType() == AccessTypeEnum.USER)
				{
					if (!StringUtils.isNullString(item.getValueGuid()))
					{
						User user = this.stubService.getAAS().getUser(item.getValueGuid());
						if (user != null)
						{
							item.setValueName(user.getUserId() + "-" + user.getUserName());
						}
					}
				}
			}
		}
	}

	protected AbstractACLItem getACLItem(Class<? extends AbstractACLItem> cls, String aclItemGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("GUID", aclItemGuid);

			return sds.get(cls, aclItemGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected boolean deleteACLItem(String aclItemGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		boolean hasDeleted = false;

		try
		{
			hasDeleted = sds.delete(ACLItem.class, aclItemGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}

		return hasDeleted;
	}

}
