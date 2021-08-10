/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLSubjectStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.Folder;
import dyna.common.dto.acl.ShareFolderACLItem;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.List;

/**
 * @author Wanglei
 * 
 */
public class SharedFolderACLStub extends AbstractServiceStub<ACLImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected SharedFolderACLStub(ServiceContext context, ACLImpl service)
	{
		super(context, service);
	}

	protected ShareFolderACLItem saveShareFolderACLItem(ShareFolderACLItem aclItem) throws ServiceRequestException
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

			return (ShareFolderACLItem) this.stubService.getAclItemStub().getACLItem(ShareFolderACLItem.class, guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, aclItem.getValueName() == null ? "" : aclItem.getValueName());
		}
	}

	public void batchDealSharedFolderACLItem(List<ShareFolderACLItem> saveACLItemList, String folderGuid) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			this.deleteSharedFolderACLItem(folderGuid);

			if (SetUtils.isNullList(saveACLItemList))
			{
//				DataServer.getTransactionManager().commitTransaction();
				return;
			}

			for (ShareFolderACLItem aclItem : saveACLItemList)
			{
				aclItem.setGuid(null);
				aclItem.setFolderGuid(folderGuid);
				this.saveSharedFolderACLItem(aclItem);
			}

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByDynaDataException(e);
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
	}

	public void saveSharedFolderACLItem(ShareFolderACLItem aclItem) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			String userGuid = this.stubService.getOperatorGuid();

			if (aclItem == null)
			{
				return;
			}

			if (aclItem.getFolderGuid() == null)
			{
				return;
			}
			Folder folder = this.stubService.getEDAP().getFolder(aclItem.getFolderGuid());

			if (!folder.getOwnerUserGuid().equals(userGuid))
			{
				throw new ServiceRequestException("ID_APP_IS_NOT_FOLDER_OWNERUSER");
			}

			if ("N".equals(folder.getIsShared()))
			{
				throw new ServiceRequestException("ID_APP_IS_NOT_SHAREDFOLDER");
			}

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

			aclItem.put(SystemObject.UPDATE_USER_GUID, userGuid);
			if (!StringUtils.isGuid(aclItem.getGuid()))
			{
				aclItem.put(SystemObject.CREATE_USER_GUID, userGuid);
			}
			sds.save(aclItem);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, aclItem.getFolderName() == null ? "" : aclItem.getFolderName());
		}
	}

	protected List<ShareFolderACLItem> listACLSharedFolderItemByFolder(String folderGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<ShareFolderACLItem> list = sds.listFromCache(ShareFolderACLItem.class, new FieldValueEqualsFilter<ShareFolderACLItem>(ShareFolderACLItem.FOLDER_GUID, folderGuid));
			this.stubService.getAclItemStub().decodeObjectValueName(list);
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected void deleteSharedFolderACLItem(String folderGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		String userGuid = this.stubService.getOperatorGuid();

		try
		{
			Folder folder = this.stubService.getEDAP().getFolder(folderGuid);

			if (!folder.getOwnerUserGuid().equals(userGuid))
			{
				throw new ServiceRequestException("ID_APP_IS_NOT_FOLDER_OWNERUSER");
			}

			if ("N".equals(folder.getIsShared()))
			{
				throw new ServiceRequestException("ID_APP_IS_NOT_SHAREDFOLDER");
			}

			sds.deleteFromCache(ShareFolderACLItem.class, new FieldValueEqualsFilter<ShareFolderACLItem>(ShareFolderACLItem.FOLDER_GUID, folderGuid));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

}
