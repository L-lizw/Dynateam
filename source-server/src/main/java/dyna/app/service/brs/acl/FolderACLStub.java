/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLSubjectStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.Folder;
import dyna.common.dto.aas.Group;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.acl.SaAclFolderLibConf;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.acl.AclService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lizw
 */
@Component
public class FolderACLStub extends AbstractServiceStub<ACLImpl>
{

	protected FolderACLItem getACLFolderItemByFolder(String folderGuid) throws ServiceRequestException
	{
		FolderACLItem aclStr = null;
		try
		{
			String userGuid = this.stubService.getOperatorGuid();
			String groupGuid = this.stubService.getUserSignature().getLoginGroupGuid();
			String roleGuid = this.stubService.getUserSignature().getLoginRoleGuid();

			Group group = this.stubService.getAAS().getGroup(groupGuid);
			if (group == null)
			{
				throw new ServiceRequestException("not found group: " + groupGuid);
			}

			if (group.isAdminGroup())
			{
				aclStr = new FolderACLItem();
				String acl = "1";
				aclStr.put(FolderACLItem.OPER_READ, acl);
				aclStr.put(FolderACLItem.OPER_CREATE, acl);
				aclStr.put(FolderACLItem.OPER_DELETE, acl);
				aclStr.put(FolderACLItem.OPER_RENAME, acl);
				aclStr.put(FolderACLItem.OPER_ADDREF, acl);
				aclStr.put(FolderACLItem.OPER_DELREF, acl);

				return aclStr;
			}

			aclStr = DataServer.getAclService().getAuthorityFolder(folderGuid, userGuid, groupGuid, roleGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}

		return aclStr;
	}

	protected FolderACLItem getACLFolderItemByFolder(String folderGuid, AccessTypeEnum accessTypeEnum, String objectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			UpperKeyMap filter = new UpperKeyMap();
			filter.put("FOLDERGUID", folderGuid);
			filter.put("OBJECTTYPE", accessTypeEnum.toString());
			filter.put("OBJECTGUID", objectGuid);
			List<FolderACLItem> list = sds.listFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(filter));
			return SetUtils.isNullList(list) ? null : list.get(0);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected List<FolderACLItem> listACLFolderItemByFolder(String folderGuid) throws ServiceRequestException
	{
		try
		{
			List<FolderACLItem> list = DataServer.getAclService().listFolderAuth(folderGuid);
			this.stubService.getAclItemStub().decodeObjectValueName(list);
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected void batchDealFolderACLItem(List<FolderACLItem> saveACLItemList, String folderGuid, boolean isExtend, boolean isMandatoryWriteOver, boolean isMandatoryAll)
			throws ServiceRequestException
	{
		try
		{
			String userGuid = this.stubService.getOperatorGuid();

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 更新是否是否继承的属性
			SaAclFolderLibConf saAclFolderLibConf = this.stubService.getEDAP().getSaAclFolderLibConf(folderGuid);
			if (saAclFolderLibConf != null)
			{
				if (saAclFolderLibConf.isExtend() != isExtend)
				{
					saAclFolderLibConf.setIsExtend(isExtend);
					this.stubService.getEDAP().saveSaAclFolderLibConf(saAclFolderLibConf);
				}
			}

			// saveACLItemList中包含了当前文件夹的所有权限，所以可以先清空数据库中的权限列表，重新创建
			if (!isMandatoryWriteOver && !isMandatoryAll)
			{
				// 权限检查，USER/RIG/ROLE/GROUP类型权限必须有权限值
				this.checkValueGuid(saveACLItemList);

				// 首先删除原有的所有权限
				DataServer.getSystemDataService().deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(FolderACLItem.FOLDER_GUID, folderGuid));

				// 重新创建当前文件夹的权限
				this.reCreateFolderAclItem(folderGuid, userGuid, saveACLItemList, isExtend);
			}
			else
			{
				String isExtendFolder = "N";
				if (isMandatoryWriteOver)
				{
					isExtendFolder = "Y";
				}

				// 当设置为强制覆盖时，删除当前文件夹及子文件夹权限
				this.delFolderAclItemWhenForceOverride(folderGuid, BooleanUtils.getBooleanByYN(isExtendFolder));

				// 权限检查，USER/RIG/ROLE/GROUP类型权限必须有权限值
				this.checkValueGuid(saveACLItemList);

				// 重新创建当前文件夹的权限
				this.reCreateFolderAclItemWhenForceOverride(folderGuid, userGuid, saveACLItemList, BooleanUtils.getBooleanByYN(isExtendFolder));
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

	/**
	 * 当设置为强制覆盖时，删除当前文件夹及子文件夹权限
	 *
	 * @param folderGuid
	 * @param isExtend
	 * @throws ServiceRequestException
	 */
	private void delFolderAclItemWhenForceOverride(String folderGuid, boolean isExtend) throws ServiceRequestException
	{
		String sessionId = this.stubService.getUserSignature().getCredential();
		SystemDataService sds = DataServer.getSystemDataService();
		if (isExtend)
		{
			// 继承时，删除所有设置为继承权限的文件夹权限，直至第一个非继承权限的文件夹为止
			List<SaAclFolderLibConf> confList = sds.listFromCache(SaAclFolderLibConf.class,
					new FieldValueEqualsFilter<SaAclFolderLibConf>(SaAclFolderLibConf.FOLDER_GUID, folderGuid));
			SaAclFolderLibConf conf = SetUtils.isNullList(confList) ? null : confList.get(0);
			if (conf == null || !conf.isExtend())
			{
				return;
			}

			// 删除文件夹权限
			sds.deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(FolderACLItem.FOLDER_GUID, folderGuid));

			// 取得子文件夹
			List<Folder> subFolderList = DataServer.getFolderService().listSubFolder(folderGuid, sessionId, false);
			if (!SetUtils.isNullList(subFolderList))
			{
				for (Folder subFolder : subFolderList)
				{
					// 递归删除文件夹权限
					this.delFolderAclItemWhenForceOverride(subFolder.getGuid(), isExtend);
				}
			}
		}
		else
		{
			// 非继承时，删除当前文件夹及其子文件夹的所有权限

			// 删除当前文件夹权限
			sds.deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<>(FolderACLItem.FOLDER_GUID, folderGuid));
			// 取得子文件夹
			List<Folder> subFolderList = DataServer.getFolderService().listSubFolder(folderGuid, sessionId, false);
			if (!SetUtils.isNullList(subFolderList))
			{
				for (Folder subFolder : subFolderList)
				{
					// 递归删除文件夹权限
					this.delFolderAclItemWhenForceOverride(subFolder.getGuid(), isExtend);
				}
			}
		}
	}

	/**
	 * 当权限设置为强制覆盖时，重新创建文件夹权限列表
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param folderAclItemList
	 * @throws ServiceRequestException
	 */
	private void reCreateFolderAclItemWhenForceOverride(String folderGuid, String userGuid, List<FolderACLItem> folderAclItemList, boolean isExtend) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		// 首先删除原有的所有权限
		sds.deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(FolderACLItem.FOLDER_GUID, folderGuid));

		Folder folder = DataServer.getFolderService().getFolder(folderGuid, null, null, null, false);
		Folder parentFolder = DataServer.getFolderService().getFolder(folder.getParentGuid(), null, null, null, false);

		List<FolderACLItem> extendACLItemList = null;
		if (parentFolder.getType() != FolderTypeEnum.LIB_FOLDER)
		{
			// 取得父文件夹的权限作为当前文件夹的继承权限
			extendACLItemList = this.createFolderACLFromParent(folder, userGuid);
		}

		// 保存当前文件夹的权限列表
		this.createCurrentFolderACLItem(folderGuid, userGuid, folderAclItemList, extendACLItemList);

		// 处理子文件夹的权限条目
		this.reCreateSubFolderAclItemWhenForceOverride(folderGuid, folderGuid, userGuid, isExtend);
	}

	/**
	 * 重新创建文件夹权限列表
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param folderAclItemList
	 * @throws ServiceRequestException
	 */
	private void reCreateFolderAclItem(String folderGuid, String userGuid, List<FolderACLItem> folderAclItemList, boolean isExtend) throws ServiceRequestException
	{
		Folder folder = DataServer.getFolderService().getFolder(folderGuid, null, null, null, false);
		Folder parentFolder = DataServer.getFolderService().getFolder(folder.getParentGuid(), null, null, null, false);

		List<FolderACLItem> extendACLItemList = null;
		if (isExtend && parentFolder.getType() != FolderTypeEnum.LIBRARY)
		{
			// 取得父文件夹的权限作为当前文件夹的继承权限
			extendACLItemList = this.createFolderACLFromParent(folder, userGuid);
		}

		// 保存当前文件夹的权限列表
		this.createCurrentFolderACLItem(folderGuid, userGuid, folderAclItemList, extendACLItemList);

		// 处理子文件夹的权限条目
		this.reCreateSubFolderACLItem(folder, parentFolder, userGuid);
	}

	/**
	 * 创建当前文件夹权限列表
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param folderAclItemList
	 */
	private void createCurrentFolderACLItem(String folderGuid, String userGuid, List<FolderACLItem> folderAclItemList, List<FolderACLItem> extendACLItemList)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		// 保存当前文件夹的权限列表
		if (!SetUtils.isNullList(folderAclItemList))
		{
			for (FolderACLItem aclItem : folderAclItemList)
			{
				// 如果当前权限条目是当前文件夹的非继承权限，且有非空权限，则记录非继承权限
				if (!aclItem.isExtend())
				{
					aclItem.clear("GUID");
					aclItem.setCreateUserGuid(userGuid);
					aclItem.setUpdateUserGuid(userGuid);
					aclItem.setFolderGuid(folderGuid);
					aclItem.setIsExtend(false);
					sds.save(aclItem);
				}
				else
				{
					// 如果当前权限条目是当前文件夹的继承权限，且权限有修改，则新增非继承权限，只记录修改的权限
					FolderACLItem parentACLItem = this.getExtendACLItemFromParent(extendACLItemList, aclItem);
					if (parentACLItem != null)
					{
						FolderACLItem changedACLItem = this.isFolderACLChanged(parentACLItem, aclItem);
						if (changedACLItem != null)
						{
							changedACLItem.setIsExtend(false);
							changedACLItem.setFolderGuid(folderGuid);
							changedACLItem.setPrecedence(aclItem.getPrecedence());
							changedACLItem.setAccessType(aclItem.getAccessType());
							changedACLItem.setValueGuid(aclItem.getValueGuid());
							changedACLItem.setValueName(aclItem.getValueName());
							changedACLItem.setCreateUserGuid(userGuid);
							changedACLItem.setUpdateUserGuid(userGuid);
							sds.save(changedACLItem);
						}
					}
				}
			}
		}
	}

	/**
	 * 递归创建子文件夹权限
	 *
	 * @param folder
	 * @param userGuid
	 * @throws ServiceRequestException
	 */
	private void reCreateSubFolderACLItem(Folder folder, Folder parentFolder, String userGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String sessionId = this.stubService.getUserSignature().getCredential();

		// 当文件夹不是公共库ROOT文件夹，且权限设置为非继承时，递归终止
		if (parentFolder == null || parentFolder.getType() != FolderTypeEnum.LIBRARY)
		{
			List<SaAclFolderLibConf> confList = sds.listFromCache(SaAclFolderLibConf.class,
					new FieldValueEqualsFilter<SaAclFolderLibConf>(SaAclFolderLibConf.FOLDER_GUID, folder.getGuid()));
			if (!SetUtils.isNullList(confList))
			{
				SaAclFolderLibConf conf = confList.get(0);
				if (!conf.isExtend())
				{
					return;
				}
			}
		}

		// 取得所有单阶子文件夹
		List<Folder> subFolderList = DataServer.getFolderService().listSubFolder(folder.getGuid(), sessionId, false);
		if (!SetUtils.isNullList(subFolderList))
		{
			for (Folder subFolder : subFolderList)
			{
				// 清空子文件夹的继承权限
				UpperKeyMap filterMap = new UpperKeyMap();
				filterMap.put(FolderACLItem.FOLDER_GUID, subFolder.getGuid());
				filterMap.put(FolderACLItem.IS_EXTEND, "Y");
				sds.deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(filterMap));

				// 取得父文件夹的权限作为继承权限
				this.createFolderACLFromParent(subFolder, userGuid);

				// 递归
				this.reCreateSubFolderACLItem(subFolder, null, userGuid);
			}
		}
	}

	/**
	 * 根据指定的权限条目取得继承下来的权限条目
	 *
	 * @param extendACLItemList
	 *            继承的父文件夹的所有权限条目
	 * @param aclItem
	 *            当前文件夹的继承权限条目
	 * @return
	 */
	private FolderACLItem getExtendACLItemFromParent(List<FolderACLItem> extendACLItemList, FolderACLItem aclItem)
	{
		if (!SetUtils.isNullList(extendACLItemList))
		{
			for (FolderACLItem extendACLItem : extendACLItemList)
			{
				if (extendACLItem.getAccessType() == AccessTypeEnum.OTHERS && aclItem.getAccessType() == AccessTypeEnum.OTHERS)
				{
					return extendACLItem;
				}
				if (extendACLItem.getAccessType() == AccessTypeEnum.OWNER && aclItem.getAccessType() == AccessTypeEnum.OWNER)
				{
					return extendACLItem;
				}
				if (extendACLItem.getValueGuid().equals(aclItem.getValueGuid()))
				{
					return extendACLItem;
				}
			}
		}
		return null;
	}

	/**
	 * 判断当前文件夹的继承权限条目和父文件夹相同权限条目相比，是否有修改
	 *
	 * @param parentACLItem
	 * @param aclItem
	 * @return 修改的权限
	 */
	private FolderACLItem isFolderACLChanged(FolderACLItem parentACLItem, FolderACLItem aclItem)
	{
		FolderACLItem aclItem_ = new FolderACLItem();

		String parentReadAcl = StringUtils.convertNULLtoString(parentACLItem.getOperRead());
		String parentAddrefAcl = StringUtils.convertNULLtoString(parentACLItem.getOperAddref());
		String parentCreateAcl = StringUtils.convertNULLtoString(parentACLItem.getOperCreate());
		String parentDeleteAcl = StringUtils.convertNULLtoString(parentACLItem.getOperDelete());
		String parentDelrefAcl = StringUtils.convertNULLtoString(parentACLItem.getOperDelref());
		String parentRenameAcl = StringUtils.convertNULLtoString(parentACLItem.getOperRename());

		String readAcl = StringUtils.convertNULLtoString(aclItem.getOperRead());
		String addrefAcl = StringUtils.convertNULLtoString(aclItem.getOperAddref());
		String createAcl = StringUtils.convertNULLtoString(aclItem.getOperCreate());
		String deleteAcl = StringUtils.convertNULLtoString(aclItem.getOperDelete());
		String delrefAcl = StringUtils.convertNULLtoString(aclItem.getOperDelref());
		String renameAcl = StringUtils.convertNULLtoString(aclItem.getOperRename());

		if (!parentReadAcl.equals(readAcl) //
				|| !parentAddrefAcl.equals(addrefAcl)//
				|| !parentCreateAcl.equals(createAcl)//
				|| !parentDeleteAcl.equals(deleteAcl)//
				|| !parentDelrefAcl.equals(delrefAcl)//
				|| !parentRenameAcl.equals(renameAcl))
		{
			aclItem_.setOperRead(parentReadAcl.equals(readAcl) ? null : readAcl);
			aclItem_.setOperAddref(parentAddrefAcl.equals(addrefAcl) ? null : addrefAcl);
			aclItem_.setOperCreate(parentCreateAcl.equals(createAcl) ? null : createAcl);
			aclItem_.setOperDelete(parentDeleteAcl.equals(deleteAcl) ? null : deleteAcl);
			aclItem_.setOperDelref(parentDelrefAcl.equals(delrefAcl) ? null : delrefAcl);
			aclItem_.setOperRename(parentRenameAcl.equals(renameAcl) ? null : renameAcl);
			return aclItem_;
		}
		return null;
	}

	/**
	 * 检查权限是否正确，USER/RIG/ROLE/GROUP类型权限必须有权限值
	 *
	 * @param aclItemList
	 * @throws ServiceRequestException
	 */
	private void checkValueGuid(List<FolderACLItem> aclItemList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(aclItemList))
		{
			for (FolderACLItem aclItem : aclItemList)
			{
				this.checkValueGuid(aclItem);
			}
		}
	}

	private void checkValueGuid(FolderACLItem aclItem) throws ServiceRequestException
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
	}

	/**
	 * 取得父文件夹的权限作为当前文件夹的继承权限
	 *
	 * @param folder
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FolderACLItem> createFolderACLFromParent(Folder folder, String userGuid) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		List<FolderACLItem> folderAuthList = aclService.listFolderAuth(folder.getParentGuid());
		if (!SetUtils.isNullList(folderAuthList))
		{
			for (FolderACLItem aclItem : folderAuthList)
			{
				FolderACLItem folderACLItem = new FolderACLItem();
				folderACLItem.putAll(aclItem);
				folderACLItem.clear("GUID");

				folderACLItem.setIsExtend(true);
				folderACLItem.setFolderGuid(folder.getGuid());
				folderACLItem.setCreateUserGuid(userGuid);
				folderACLItem.setUpdateUserGuid(userGuid);
				DataServer.getSystemDataService().save(folderACLItem);
			}
		}
		return folderAuthList;
	}

	/**
	 * 根据指定的文件夹folderGuidFrom取得权限列表，作为文件夹folderGuidTo的继承权限
	 *
	 * @param folderGuidFrom
	 * @param folderGuidTo
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FolderACLItem> createFolderACLFrom(String folderGuidFrom, String folderGuidTo, String userGuid) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		List<FolderACLItem> folderAuthList = aclService.listFolderAuth(folderGuidFrom);
		if (!SetUtils.isNullList(folderAuthList))
		{
			for (FolderACLItem aclItem : folderAuthList)
			{
				FolderACLItem folderACLItem = new FolderACLItem();
				folderACLItem.putAll(aclItem);

				folderACLItem.setIsExtend(true);
				folderACLItem.setFolderGuid(folderGuidTo);
				folderACLItem.setCreateUserGuid(userGuid);
				folderACLItem.setUpdateUserGuid(userGuid);
				DataServer.getSystemDataService().save(folderACLItem);
			}
		}
		return folderAuthList;
	}

	/**
	 * 当文件夹权限设置为强制覆盖时，递归创建子文件夹权限
	 *
	 * @param fromFolderGuid
	 * @param currentFolderGuid
	 * @param userGuid
	 * @param isExtend
	 * @throws ServiceRequestException
	 */
	private void reCreateSubFolderAclItemWhenForceOverride(String fromFolderGuid, String currentFolderGuid, String userGuid, boolean isExtend) throws ServiceRequestException
	{
		String sessionId = this.stubService.getUserSignature().getCredential();
		Folder folder = DataServer.getFolderService().getFolder(currentFolderGuid, null, null, null, false);
		if (folder != null)
		{
			// 取得子文件夹
			List<Folder> subFolderList = DataServer.getFolderService().listSubFolder(currentFolderGuid, sessionId, false);
			if (SetUtils.isNullList(subFolderList))
			{
				return;
			}

			if (isExtend)
			{
				for (Folder subFolder : subFolderList)
				{
					boolean isExtend_ = false;
					List<SaAclFolderLibConf> confList = DataServer.getSystemDataService().listFromCache(SaAclFolderLibConf.class,
							new FieldValueEqualsFilter<SaAclFolderLibConf>(SaAclFolderLibConf.FOLDER_GUID, subFolder.getGuid()));
					if (!SetUtils.isNullList(confList))
					{
						isExtend_ = confList.get(0).isExtend();
					}

					if (isExtend_)
					{
						// 根据当前文件夹父文件夹权限创建当前文件夹权限
						this.createFolderACLFrom(fromFolderGuid, subFolder.getGuid(), userGuid);

						// 递归创建子文件夹权限
						this.reCreateSubFolderAclItemWhenForceOverride(fromFolderGuid, subFolder.getGuid(), userGuid, isExtend);
					}
				}
			}
			else
			{
				for (Folder subFolder : subFolderList)
				{
					// 根据当前文件夹父文件夹权限创建当前文件夹权限
					this.createFolderACLFrom(fromFolderGuid, subFolder.getGuid(), userGuid);

					// 递归创建子文件夹权限
					this.reCreateSubFolderAclItemWhenForceOverride(fromFolderGuid, subFolder.getGuid(), userGuid, isExtend);
				}
			}
		}
	}

	public List<FolderACLItem> listACLFolderItemByFolderTemporary(String folderGuid, boolean isExdtend) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		try
		{
			List<FolderACLItem> list = aclService.listChangingLibFolderAuth(folderGuid, isExdtend);
			this.stubService.getAclItemStub().decodeObjectValueName(list);
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	public void batchDealLibACLItem(List<FolderACLItem> saveACLItemList, String folderGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		// 判断用户是否有管理员的权限
		String groupGuid = ((UserSignature) this.stubService.getSignature()).getLoginGroupGuid();

		try
		{
			Group group = this.stubService.getAAS().getGroup(groupGuid);

			if (group == null || !group.isAdminGroup())
			{
				Folder libFolder = this.stubService.getEDAP().getFolder(folderGuid);

				if (!this.stubService.getOperatorGuid().equals(libFolder.getOwnerUserGuid()))
				{
					throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only");
				}
			}

		}
		catch (ServiceRequestException e)
		{
			throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only", e);
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			sds.deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(FolderACLItem.FOLDER_GUID, folderGuid));
			if (!SetUtils.isNullList(saveACLItemList))
			{
				String userGuid = this.stubService.getOperatorGuid();

				for (FolderACLItem aclItem : saveACLItemList)
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

					aclItem.setGuid(null);
					aclItem.setUpdateUserGuid(userGuid);
					aclItem.setCreateUserGuid(userGuid);
					sds.save(aclItem);
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

	public List<FolderACLItem> listACLLibItemByLib(String folderGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		// 判断用户是否有管理员的权限
		String groupGuid = ((UserSignature) this.stubService.getSignature()).getLoginGroupGuid();
		try
		{
			Group group = this.stubService.getAAS().getGroup(groupGuid);

			if (group == null || !group.isAdminGroup())
			{
				Folder libFolder = this.stubService.getEDAP().getFolder(folderGuid);

				if (!this.stubService.getOperatorGuid().equals(libFolder.getOwnerUserGuid()))
				{
					throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only");
				}
			}

		}
		catch (ServiceRequestException e)
		{
			throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only", e);
		}

		try
		{
			List<FolderACLItem> list = sds.listFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(FolderACLItem.FOLDER_GUID, folderGuid));
			this.stubService.getAclItemStub().decodeObjectValueName(list);
			return list;
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected boolean hasFolderAuthority(String folderGuid, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid)
			throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		try
		{

			Group group = this.stubService.getAAS().getGroup(groupGuid);
			if (group == null)
			{
				throw new ServiceRequestException("not found group: " + groupGuid);
			}

			if (group.isAdminGroup())
			{
				return true;
			}

			return aclService.hasFolderAuthority(folderGuid, folderAuthorityEnum, userGuid, groupGuid, roleGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	protected void hasUserAuthorize4Lib(String folderGuid) throws ServiceRequestException
	{
		try
		{
			if (!Constants.isSupervisor(false, this.stubService))
			{
				return;
			}

			Folder folder = this.stubService.getEDAP().getFolder(folderGuid);
			if (folder != null)
			{
				if (folder.getOwnerUserGuid().equals(this.stubService.getOperatorGuid()))
				{
					return;
				}
				if (folder.getType().equals(FolderTypeEnum.LIB_FOLDER))
				{
					folderGuid = folder.getLibraryUser();
					folder = this.stubService.getEDAP().getFolder(folderGuid);
					if (folder.getOwnerUserGuid().equals(this.stubService.getOperatorGuid()))
					{
						return;
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
	}

	public boolean hasUserAuthorize4Lib(String folderGuid, boolean isSystemCheckAcl) throws ServiceRequestException
	{
		try
		{
			if (!Constants.isSupervisor(isSystemCheckAcl, this.stubService))
			{
				return true;
			}

			Folder folder = this.stubService.getEDAP().getFolder(folderGuid);
			if (folder != null)
			{
				if (folder.getType().equals(FolderTypeEnum.LIBRARY) && folder.getOwnerUserGuid().equals(this.stubService.getOperatorGuid()))
				{
					return true;
				}
				if (folder.getType().equals(FolderTypeEnum.LIB_FOLDER))
				{
					folderGuid = folder.getLibraryUser();
					folder = this.stubService.getEDAP().getFolder(folderGuid);
					if (folder.getOwnerUserGuid().equals(this.stubService.getOperatorGuid()))
					{
						return true;
					}
				}
			}

			return false;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
	}

	protected List<FolderACLItem> listACLItemBySubject(String aclSubjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<FolderACLItem> list = sds.listFromCache(FolderACLItem.class, new FieldValueEqualsFilter<FolderACLItem>(FolderACLItem.MASTER_FK, aclSubjectGuid));
			this.stubService.getAclItemStub().decodeObjectValueName(list);
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}
}
