/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileTransStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.dss;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.serv.DSStorage;
import dyna.common.dto.Folder;
import dyna.common.dto.aas.Group;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wanglei
 *
 */
@Component
public class StorageStub extends AbstractServiceStub<DSSImpl>
{

	public List<DSStorage> listStorage() throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getServerConfig().getStorageList();
	}

	protected DSStorage getStorage(String storageId) throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getServerConfig().getDSStorage(storageId);
	}

	protected DSStorage getStorageForGroup(String groupGuid, String groupId) throws ServiceRequestException
	{
		Group group = null;
		if (!StringUtils.isNullString(groupGuid))
		{
			group = this.stubService.getAAS().getGroup(groupGuid);
		}
		else if (!StringUtils.isNullString(groupId))
		{
			group = this.stubService.getAAS().getGroupById(groupId);
		}
		else
		{
			throw new ServiceRequestException("Illegal params: groupGuid groupId");
		}
		if (group == null)
		{
			throw new ServiceRequestException("Not found group: " + groupGuid + " " + groupId);
		}

		if (StringUtils.isNullString(group.getLibraryGuid()))
		{
			throw new ServiceRequestException("Not found library for group: " + groupGuid + " " + groupId);
		}

		Folder library = this.stubService.getEDAP().getFolder(group.getLibraryGuid());
		String storageId = library.getStorage();

		DSStorage storage = this.getStorage(storageId);
		if (storage == null)
		{
			throw new ServiceRequestException("ID_NOT_FOUND_GROUP_STORAGE", "not found storage for group: " + groupId);
		}
		return storage;
	}

}
