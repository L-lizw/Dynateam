/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PublicSearchACLStub
 * WanglhB 2012-2-8
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.acl.PublicSearchACLItem;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author caogc
 * 
 */
@Component
public class PublicSearchACLStub extends AbstractServiceStub<ACLImpl>
{

	protected void savePublicSearchACLItem(List<PublicSearchACLItem> aclItemList, String publicSearchGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String userGuid = this.stubService.getOperatorGuid();
		if (!StringUtils.isGuid(publicSearchGuid))
		{
			return;
		}

		try
		{

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			this.deletePublicSearchACLItem(publicSearchGuid);

			if (!SetUtils.isNullList(aclItemList))
			{
				for (PublicSearchACLItem aclItem : aclItemList)
				{
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
					aclItem.put(SystemObject.CREATE_USER_GUID, userGuid);
					aclItem.put(SystemObject.UPDATE_USER_GUID, userGuid);
					sds.save(aclItem, "insertPublicSearch");
				}
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

	protected List<PublicSearchACLItem> listPublicSearchACLItemByPreSearchGuid(String publicSearchGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<PublicSearchACLItem> list = sds.listFromCache(PublicSearchACLItem.class,
					new FieldValueEqualsFilter<PublicSearchACLItem>(PublicSearchACLItem.PUBLIC_SEARCH_GUID, publicSearchGuid));
			this.stubService.getAclItemStub().decodeObjectValueName(list);
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected void deletePublicSearchACLItem(String publicSearchGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.deleteFromCache(PublicSearchACLItem.class, new FieldValueEqualsFilter<PublicSearchACLItem>(PublicSearchACLItem.PUBLIC_SEARCH_GUID, publicSearchGuid));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

}
