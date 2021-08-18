/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lizw
 * 
 */
@Component
public class ACLStub extends AbstractServiceStub<ACLImpl>
{

	public void batchDealWithACL(List<ACLItem> addAclItemList, List<ACLSubject> addAclSubjectList, List<ACLItem> updateAclItemList, List<ACLSubject> updateAclSubjectList,
			List<ACLItem> deleteAclItemList, List<ACLSubject> deleteAclSubjectList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 删除ACLItem
			if (!SetUtils.isNullList(deleteAclItemList))
			{
				for (ACLItem aclItem : deleteAclItemList)
				{
					this.stubService.getAclItemStub().deleteACLItem(aclItem.getGuid());
				}
			}

			// 更新ACLItem
			if (!SetUtils.isNullList(updateAclItemList))
			{
				for (ACLItem aclItem : updateAclItemList)
				{
					this.stubService.getAclItemStub().deleteACLItem(aclItem.getGuid());
				}
			}

			// 删除ACLSubject
			if (!SetUtils.isNullList(deleteAclSubjectList))
			{
				for (ACLSubject aclSubject : deleteAclSubjectList)
				{
					this.stubService.getAclSubjectStub().deleteACLSubject(aclSubject.getGuid());
				}
			}

			// 新增ACLSubject
			if (!SetUtils.isNullList(addAclSubjectList))
			{
				for (ACLSubject aclSubject : addAclSubjectList)
				{
					UpperKeyMap filter = new UpperKeyMap();
					filter.put(ACLSubject.PARENT_GUID, aclSubject.getParentGuid());
					filter.put(ACLSubject.POSITION, aclSubject.getPosition());
					List<ACLSubject> aclMasterList = sds.listFromCache(ACLSubject.class, new FieldValueEqualsFilter<ACLSubject>(filter));
					ACLSubject retAclSubject = SetUtils.isNullList(aclMasterList) ? null : aclMasterList.get(0);
					if (retAclSubject != null)
					{
						retAclSubject.setPosition(-retAclSubject.getPosition());
						sds.save(retAclSubject);
					}

					String oldAclSubjectGuid = aclSubject.getGuid();

					aclSubject.remove(ACLSubject.GUID);

					retAclSubject = this.stubService.getAclSubjectStub().saveACLSubject(aclSubject);

					for (ACLSubject aclSubjectAnother : addAclSubjectList)
					{
						if (!StringUtils.isNullString(oldAclSubjectGuid) && oldAclSubjectGuid.equals(aclSubjectAnother.getParentGuid()))
						{
							aclSubjectAnother.setParentGuid(retAclSubject.getGuid());
						}
					}

					// 重置ACLItem对应的subjectGuid
					if (!SetUtils.isNullList(addAclItemList))
					{
						for (ACLItem aclItem : addAclItemList)
						{
							if (aclItem.getMasterGuid().equals(oldAclSubjectGuid))
							{
								aclItem.setMasterGuid(retAclSubject.getGuid());
							}
						}
					}
				}
			}

			// 新增ACLItem
			if (!SetUtils.isNullList(addAclItemList))
			{
				for (ACLItem aclItem : addAclItemList)
				{
					this.stubService.getAclItemStub().saveACLItem(aclItem);
				}
			}

			// 更新ACLItem
			if (!SetUtils.isNullList(updateAclItemList))
			{
				for (ACLItem aclItem : updateAclItemList)
				{
					aclItem.setGuid(null);
					this.stubService.getAclItemStub().saveACLItem(aclItem);
				}
			}

			// 更新ACLSubject
			if (!SetUtils.isNullList(updateAclSubjectList))
			{
				for (ACLSubject aclSubject : updateAclSubjectList)
				{
					UpperKeyMap filter = new UpperKeyMap();
					filter.put(ACLSubject.PARENT_GUID, aclSubject.getParentGuid());
					filter.put(ACLSubject.POSITION, aclSubject.getPosition());

					List<ACLSubject> tempList = sds.listFromCache(ACLSubject.class, new FieldValueEqualsFilter<ACLSubject>(filter));
					ACLSubject retAclSubject = SetUtils.isNullList(tempList) ? null : tempList.get(0);
					if (aclSubject.isChanged(ACLSubject.NAME))
					{
						ACLSubject aclSubjectSameName = this.stubService.getAclSubjectStub().getACLSubjectByName(aclSubject.getLibraryGuid(), aclSubject.getName());
						if (aclSubjectSameName != null)
						{
							boolean isChange = false;
							for (ACLSubject aclSubjectOld : updateAclSubjectList)
							{
								if (aclSubjectSameName.getGuid().equals(aclSubjectOld.getGuid()))
								{
									if (aclSubjectOld.isChanged(ACLSubject.NAME))
									{
										isChange = true;
									}
								}
							}
							if (isChange)
							{
								aclSubjectSameName.setName(aclSubject.getName() + "XXXXXXXX" + updateAclSubjectList.indexOf(aclSubject));

								this.stubService.getAclSubjectStub().saveACLSubject(aclSubjectSameName);
							}
							else
							{
								throw new ServiceRequestException("ID_APP_ACL_SUBJECT_UNIQUEL", "acl subject uniquel");
							}
						}
					}

					if (retAclSubject != null)
					{
						retAclSubject.setPosition(-retAclSubject.getPosition());
						sds.save(retAclSubject);
					}

					this.stubService.getAclSubjectStub().saveACLSubject(aclSubject);
				}
			}

			DataServer.getAclService().loadACLTreeToCache();

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
		finally
		{
		}
	}
}
