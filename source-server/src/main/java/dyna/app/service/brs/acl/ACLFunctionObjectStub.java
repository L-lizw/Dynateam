/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLSubjectStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.acl.ACLFunctionItem;
import dyna.common.dto.acl.ACLFunctionObject;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class ACLFunctionObjectStub extends AbstractServiceStub<ACLImpl>
{

	protected List<ACLFunctionObject> listRootACLFunctionObject() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			return sds.query(ACLFunctionObject.class, null, "getRootACLSubjectByLIB");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected List<ACLFunctionObject> listSubACLFunctionObject(String aclFunctionObjectGuid)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		HashMap<String, Object> filter = new HashMap<String, Object>();
		filter.put(ACLFunctionObject.PARENTGUID, aclFunctionObjectGuid);

		try
		{
			return sds.query(ACLFunctionObject.class, filter, "getSubACLSubject");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected ACLFunctionObject saveACLFunctionObject(ACLFunctionObject aclFunctionObject) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{

			if (aclFunctionObject.getType() == null)
			{
				throw new ServiceRequestException("ID_APP_ACLSUBJECT_CONDITION_EMPTY", "aclsubject condition is empty");
			}


			String userGuid = this.stubService.getOperatorGuid();

			boolean isCreate = false;
			if (!StringUtils.isGuid(aclFunctionObject.getGuid()))
			{
				isCreate = true;
				aclFunctionObject.put(SystemObject.CREATE_USER_GUID, userGuid);
			}

			aclFunctionObject.put(SystemObject.UPDATE_USER_GUID, userGuid);

			String guid = sds.save(aclFunctionObject);
			if (!isCreate)
			{
				guid = aclFunctionObject.getGuid();
			}

			return this.getACLFunctionObject(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, aclFunctionObject.getName());
		}
	}

	protected ACLFunctionObject getACLFunctionObject(String aclFunctionObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put("GUID", aclFunctionObjectGuid);

			return sds.queryObject(ACLFunctionObject.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected ACLFunctionObject getACLFunctionObjectByName(String aclFunctionObjectName) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put("NAME", aclFunctionObjectName);

			return sds.queryObject(ACLFunctionObject.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected boolean deleteACLFunctionObject(String aclFunctionObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		boolean hasDeleted = false;

		try
		{
			hasDeleted = sds.delete(ACLFunctionObject.class, aclFunctionObjectGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}

		return hasDeleted;
	}
	
	public void batchDealWithACL(List<ACLFunctionItem> addAclItemList, List<ACLFunctionObject> addAclSubjectList,
			List<ACLFunctionItem> updateAclItemList, List<ACLFunctionObject> updateAclSubjectList, List<ACLFunctionItem> deleteAclItemList,
			List<ACLFunctionObject> deleteAclSubjectList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 删除ACLItem
			if (!SetUtils.isNullList(deleteAclItemList))
			{
				for (ACLFunctionItem aclItem : deleteAclItemList)
				{
					this.stubService.getAclFunctionItemStub().deleteACLFunctionItem(aclItem.getGuid());
				}
			}

			// 更新ACLItem
			if (!SetUtils.isNullList(updateAclItemList))
			{
				for (ACLFunctionItem aclItem : updateAclItemList)
				{
					this.stubService.getAclFunctionItemStub().deleteACLFunctionItem(aclItem.getGuid());
				}
			}

			// 删除ACLSubject
			if (!SetUtils.isNullList(deleteAclSubjectList))
			{
				for (ACLFunctionObject aclSubject : deleteAclSubjectList)
				{
					this.stubService.getAclFunctionObjectStub().deleteACLFunctionObject(aclSubject.getGuid());
				}
			}

			// 新增ACLSubject
			if (!SetUtils.isNullList(addAclSubjectList))
			{
				for (ACLFunctionObject aclSubject : addAclSubjectList)
				{
					HashMap<String, Object> filter = new HashMap<String, Object>();
					filter.put(ACLSubject.PARENT_GUID, aclSubject.getParentGuid());
					filter.put(ACLSubject.POSITION, aclSubject.getPosition());

					ACLFunctionObject retAclSubject = sds.queryObject(ACLFunctionObject.class, filter);

					if (retAclSubject != null)
					{
						sds.save(retAclSubject);
					}

					String oldAclSubjectGuid = aclSubject.getGuid();

					aclSubject.remove(ACLSubject.GUID);

					retAclSubject = this.stubService.getAclFunctionObjectStub().saveACLFunctionObject(aclSubject);

					for (ACLFunctionObject aclSubjectAnother : addAclSubjectList)
					{
						if (!StringUtils.isNullString(oldAclSubjectGuid)
								&& oldAclSubjectGuid.equals(aclSubjectAnother.getParentGuid()))
						{
							aclSubjectAnother.setParentGuid(retAclSubject.getGuid());
						}
					}

					// 重置ACLItem对应的subjectGuid
					if (!SetUtils.isNullList(addAclItemList))
					{
						for (ACLFunctionItem aclItem : addAclItemList)
						{
							if (aclItem.getFunctionObjectGuid()!=null && aclItem.getFunctionObjectGuid().equals(oldAclSubjectGuid))
							{
								aclItem.setFunctionObjectGuid(retAclSubject.getGuid());
							}
						}
					}
				}
			}

			// 新增ACLItem
			if (!SetUtils.isNullList(addAclItemList))
			{
				for (ACLFunctionItem aclItem : addAclItemList)
				{
					this.stubService.getAclFunctionItemStub().saveACLFunctionItem(aclItem);
				}
			}

			// 更新ACLItem
			if (!SetUtils.isNullList(updateAclItemList))
			{
				for (ACLFunctionItem aclItem : updateAclItemList)
				{
					aclItem.setGuid(null);
					this.stubService.getAclFunctionItemStub().saveACLFunctionItem(aclItem);
				}
			}

			// 更新ACLSubject
			if (!SetUtils.isNullList(updateAclSubjectList))
			{
				for (ACLFunctionObject aclSubject : updateAclSubjectList)
				{

					HashMap<String, Object> filter = new HashMap<String, Object>();
					filter.put(ACLFunctionObject.PARENTGUID, aclSubject.getParentGuid());
					filter.put(ACLFunctionObject.POSITION, aclSubject.getPosition());

					ACLFunctionObject retAclSubject = sds.queryObject(ACLFunctionObject.class, filter);

					if (aclSubject.isChanged(ACLSubject.NAME))
					{
						ACLFunctionObject aclSubjectSameName = this.getACLFunctionObjectByName(
								 aclSubject.getName());
						if (aclSubjectSameName != null)
						{
							boolean isChange = false;
							for (ACLFunctionObject aclSubjectOld : updateAclSubjectList)
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
								aclSubjectSameName.setName(aclSubject.getName() + "XXXXXXXX"
										+ updateAclSubjectList.indexOf(aclSubject));

								this.stubService.getAclFunctionObjectStub().saveACLFunctionObject(aclSubjectSameName);
							}
							else
							{
								throw new ServiceRequestException("ID_APP_ACL_SUBJECT_UNIQUEL", "acl subject uniquel");
							}
						}
					}

					if (retAclSubject != null)
					{
						sds.save(retAclSubject);
					}

					this.saveACLFunctionObject(aclSubject);
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
		finally
		{
		}
	}

}
