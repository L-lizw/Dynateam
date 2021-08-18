/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLSubjectStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.acl.ACLFunctionItem;
import dyna.common.dto.acl.ACLFunctionObject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessFunctionConditionEnum;
import dyna.common.systemenum.ModulEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * 
 */
@Component
public class ACLFunctionItemStub extends AbstractServiceStub<ACLImpl>
{

	protected ACLFunctionItem saveACLFunctionItem(ACLFunctionItem aclFunctionItem) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		boolean isCreate = false;

		try
		{
			String userGuid = this.stubService.getOperatorGuid();

			if (!StringUtils.isGuid(aclFunctionItem.getGuid()))
			{
				isCreate = true;
				aclFunctionItem.put(SystemObject.CREATE_USER_GUID, userGuid);
			}

			aclFunctionItem.put(SystemObject.UPDATE_USER_GUID, userGuid);

			if (StringUtils.isNullString(aclFunctionItem.getTypeValue()))
			{
				AccessFunctionConditionEnum accessType = aclFunctionItem.getType();
				switch (accessType)
				{
				case USER:
				case ROLE:
				case GROUP:
					throw new ServiceRequestException("ID_APP_MISS_VALUE_ACLITEM", "missing value if access type is USER/RIG/ROLE/GROUP");
				default:
					break;
				}
			}

			String aclValue = "";
			if (!SetUtils.isNullMap(aclFunctionItem.getAclMap()))
			{
				for (Map.Entry<String, PermissibleEnum> entry : aclFunctionItem.getAclMap().entrySet())
				{
					aclValue += entry.getKey() + "-" + entry.getValue().name() + ";";
				}
			}

			aclFunctionItem.setACLValue(aclValue);

			String guid = sds.save(aclFunctionItem);
			if (!isCreate)
			{
				guid = aclFunctionItem.getGuid();
			}

			return this.getACLFunctionItem(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, aclFunctionItem.getValueName() == null ? "" : aclFunctionItem.getValueName());
		}
	}

	protected List<ACLFunctionItem> listACLFunctionItemByFunctionObject(String aclFunctionObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put(ACLFunctionItem.MASTER_FK, aclFunctionObjectGuid);

			List<ACLFunctionItem> itemList = sds.query(ACLFunctionItem.class, filter);
			if (!SetUtils.isNullList(itemList))
			{
				for (ACLFunctionItem item : itemList)
				{
					item.initAclMap();
				}
			}
			return itemList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected ACLFunctionItem getACLFunctionItem(String ACLFunctionItemGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put("GUID", ACLFunctionItemGuid);

			ACLFunctionItem aclItem = sds.queryObject(ACLFunctionItem.class, filter);
			if (aclItem != null)
			{
				aclItem.initAclMap();
			}
			return aclItem;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected PermissibleEnum getFunctionPermissionByUser(ModulEnum position, String functionName, String userGuid, String roleGuid, String groupGuid)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (!Constants.isSupervisor(true, this.stubService))
		{
			return PermissibleEnum.YES;
		}

		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put(ACLFunctionObject.POSITION, position.name());

			// 用户
			filter.put(ACLFunctionObject.TYPE, AccessFunctionConditionEnum.USER.name());
			filter.put(ACLFunctionItem.TYPEVALUE, userGuid);

			ACLFunctionItem aclItem = sds.queryObject(ACLFunctionItem.class, filter, "selectItemByUser");
			if (aclItem != null)
			{
				aclItem.initAclMap();
				return aclItem.checkPermission(functionName);
			}
			// 角色
			filter.put(ACLFunctionObject.TYPE, AccessFunctionConditionEnum.ROLE.name());
			filter.put(ACLFunctionItem.TYPEVALUE, roleGuid);

			aclItem = sds.queryObject(ACLFunctionItem.class, filter, "selectItemByUser");
			if (aclItem != null)
			{
				aclItem.initAclMap();
				return aclItem.checkPermission(functionName);
			}
			// 组
			filter.put(ACLFunctionObject.TYPE, AccessFunctionConditionEnum.GROUP.name());
			filter.put(ACLFunctionItem.TYPEVALUE, groupGuid);

			aclItem = sds.queryObject(ACLFunctionItem.class, filter, "selectItemByUser");
			if (aclItem != null)
			{
				aclItem.initAclMap();
				return aclItem.checkPermission(functionName);
			}
			return PermissibleEnum.NONE;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected boolean deleteACLFunctionItem(String ACLFunctionItemGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		boolean hasDeleted = false;

		try
		{
			hasDeleted = sds.delete(ACLFunctionItem.class, ACLFunctionItemGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}

		return hasDeleted;
	}

}
