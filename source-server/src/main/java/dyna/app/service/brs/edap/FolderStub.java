/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与文件夹相关的操作分支
 * Caogc 2010-8-31
 */
package dyna.app.service.brs.edap;

import java.util.Comparator;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.Folder;
import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.acl.SaAclFolderLibConf;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.folder.FolderService;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.EDAP;

/**
 * 与Folder相关的操作分支
 *
 * @author Caogc
 */
public class FolderStub extends AbstractServiceStub<EDAPImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected FolderStub(ServiceContext context, EDAPImpl service)
	{
		super(context, service);
	}

	/**
	 * 删除指定文件夹
	 *
	 * @param folderGuid
	 *            要删除的文件夹的guid
	 * @return 删除成功true 失败flase
	 * @throws ServiceRequestException
	 */
	protected void deleteFolder(String folderGuid) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		boolean needSaveFolderTreeRelation = false;
		Folder folder = null;
		try
		{
			folder = this.getFolder(folderGuid, Constants.isSupervisor(true, this.stubService));

			if (folder == null)
			{
				return;
			}

			boolean isNotOwner = true;

			if (this.stubService.getOperatorGuid().equals(folder.getOriginalValue(Folder.OWNER_USER_GUID)) && FolderTypeEnum.LIBRARY.equals(folder.getType()))
			{
				isNotOwner = false;
			}
			else if (FolderTypeEnum.LIB_FOLDER.equals(folder.getType()))
			{
				Folder parent = this.getFolder(folder.getParentGuid(), true);
				if (FolderTypeEnum.LIBRARY.equals(parent.getType()))
				{

					if (this.stubService.getOperatorGuid().equals(parent.getOwnerUserGuid()))
					{
						isNotOwner = false;
					}
				}
				else
				{
					Folder lib = this.getFolder(parent.getLibraryUser(), true);
					if (this.stubService.getOperatorGuid().equals(lib.getOwnerUserGuid()))
					{
						isNotOwner = false;
					}

				}
			}

			if (FolderTypeEnum.LIBRARY.equals(folder.getType()) && "LIB-admin".equals(folder.getName()))
			{
				throw new ServiceRequestException("ID_APP_LIB_CANNT_DELETE", "this lib can't be deleted");
			}
			else if (FolderTypeEnum.LIB_FOLDER.equals(folder.getType()))
			{
				if (folder.getParentGuid() != null)
				{
					Folder parentFolder = this.getFolder(folder.getParentGuid(), Constants.isSupervisor(true, this.stubService));
					if (parentFolder != null && parentFolder.getType().equals(FolderTypeEnum.LIBRARY))
					{
						throw new ServiceRequestException("ID_APP_FOLDER_CANNT_DELETE", "this folder can't be deleted");
					}
				}
			}

			// 0.无权限拿到所有class 该文件夹以及一下文件夹中有无数据 如若有数据 则不允许删除
			// 拿到当前用户登陆的模型
			BMInfo currentBizModel = this.stubService.getEMM().getCurrentBizModel();
			List<BOInfo> boInfoList = null;
			if (currentBizModel != null)
			{
				boInfoList = this.stubService.getEMM().listBizObjectOfModelNonContainOthers(currentBizModel.getName());
			}
			if (!SetUtils.isNullList(boInfoList) && folder != null)
			{
				for (BOInfo boInfo : boInfoList)
				{
					if (!StringUtils.isNullString(boInfo.getClassGuid()) && boInfo.isCreateTable())
					{
						ClassInfo classByBizGuid = this.stubService.getEMM().getClassByGuid(boInfo.getClassGuid());
						SearchCondition createSearchCondition4GlobalSearch = SearchConditionFactory.createSearchCondition4Class(classByBizGuid.getName(), folder, true);
						List<FoundationObject> listObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(createSearchCondition4GlobalSearch, false);
						if (!SetUtils.isNullList(listObject))
						{
							throw new ServiceRequestException("ID_APP_LIB_CANNT_DELETE_THIS_HASDATA", "this lib has data,it can't be deleted");
						}
					}
				}
			}
			ds.deleteFolder(folderGuid, this.stubService.getUserSignature().getCredential(), Constants.isSupervisor(true, this.stubService) && isNotOwner);
			needSaveFolderTreeRelation = true;
		}
		catch (DynaDataException e)
		{
			needSaveFolderTreeRelation = false;
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		finally
		{
			if (needSaveFolderTreeRelation)
			{
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new SaveFolderTreeScheduledTask(serviceContext, folder, true, this.stubService.getUserSignature()));
			}
		}
	}

	public Folder getFolder(String folderGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		return this.getFolder(folderGuid, this.stubService.getUserSignature().getUserGuid(), this.stubService.getUserSignature().getLoginGroupGuid(),
				this.stubService.getUserSignature().getLoginRoleGuid(), isCheckAuth);
	}

	public Folder getFolder(String folderGuid, String userGuid, String groupGuid, String roleGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		Folder folder = null;

		FolderService ds = DataServer.getFolderService();

		if (folderGuid == null)
		{
			return null;
		}

		try
		{
			if (folderGuid.equals(Folder.ROOT_LIB_GUID))
			{
				return SearchCondition.ROOT_LIB;
			}

			folder = ds.getFolder(folderGuid, userGuid, groupGuid, roleGuid, Constants.isSupervisor(isCheckAuth, this.stubService));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return folder;
	}

	/**
	 * 获取个人文件夹的根目录
	 *
	 * @param edap
	 * @return Folder对象
	 * @throws ServiceRequestException
	 */
	protected Folder getRootMyFolder() throws ServiceRequestException
	{
		return DataServer.getFolderService().getRootPrivateFolderByUser(this.stubService.getOperatorGuid());
	}

	protected SaAclFolderLibConf getSaAclFolderLibConf(String folderGuid) throws ServiceRequestException
	{
		try
		{
			return DataServer.getAclService().getSaAclFolderLibConf(folderGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<Folder> listAllRootFolderHasAcl() throws ServiceRequestException
	{

		try
		{
			FolderService ds = DataServer.getFolderService();
			return ds.listRootFolder(this.stubService.getSignature().getCredential(), Constants.isSupervisor(true, this.stubService));

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 查询下一层子文件夹
	 *
	 * @param folderGuid
	 * @return Folder列表
	 * @throws ServiceRequestException
	 */
	public List<Folder> listSubFolder(String folderGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();
		List<Folder> folderList = null;

		if (StringUtils.isNullString(folderGuid))
		{
			return null;
		}

		Folder libFolder = null;
		Folder folder = this.getFolder(folderGuid, false);
		if (folder == null)
		{
			return null;
		}
		if (folder.getType() == FolderTypeEnum.LIBRARY)
		{
			libFolder = folder;
		}
		else if (folder.getType() == FolderTypeEnum.LIB_FOLDER)
		{
			libFolder = this.getFolder(folder.getLibraryUser(), false);
		}

		boolean needChkAuth = true;
		if (libFolder != null && libFolder.getOwnerUserGuid().equals(this.stubService.getOperatorGuid()))
		{
			needChkAuth = false;
		}

		try
		{
			folderList = ds.listSubFolder(folderGuid, //
					this.stubService.getUserSignature().getCredential(), //
					Constants.isSupervisor(isCheckAuth, this.stubService) && needChkAuth);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		if (!SetUtils.isNullList(folderList))
		{
			folderList.sort(Comparator.comparing(Folder::getName));
		}

		return folderList;
	}

	/**
	 * 新建/更新文件夹
	 *
	 * @param folder
	 *            要创建或者更新的文件夹对象
	 * @param edap
	 * @return 新的文件夹
	 * @throws ServiceRequestException
	 */
	protected Folder saveFolder(Folder folder) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		Folder retFolder = null;

		if (folder == null || folder.getName() == null)
		{
			return null;
		}

		boolean needSaveFolderTreeRelation = false;
		try
		{
			// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			if (folder.getName().contains("/"))
			{
				throw new ServiceRequestException("ID_APP_FOLDER_NAME_ILLEGAL", "folder's name illegal");
			}

			// 判断ID和NAME是否包含$,如果包含并抛异常
			Constants.isContain$(folder);

			String folderGuid = folder.getGuid();
			String operatorGuid = this.stubService.getOperatorGuid();
			folder.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			boolean isNotOwner = true;

			if (this.stubService.getOperatorGuid().equals(folder.getOriginalValue(Folder.OWNER_USER_GUID)) && FolderTypeEnum.LIBRARY.equals(folder.getType()))
			{
				isNotOwner = false;
			}
			else if (FolderTypeEnum.LIB_FOLDER.equals(folder.getType()))
			{
				Folder parent = this.getFolder(folder.getParentGuid(), true);
				if (FolderTypeEnum.LIBRARY.equals(parent.getType()))
				{

					if (this.stubService.getOperatorGuid().equals(parent.getOwnerUserGuid()))
					{
						isNotOwner = false;
					}
				}
				else
				{
					Folder lib = this.getFolder(parent.getLibraryUser(), true);
					if (this.stubService.getOperatorGuid().equals(lib.getOwnerUserGuid()))
					{
						isNotOwner = false;
					}

				}
			}

			if (!StringUtils.isGuid(folderGuid))
			{
				folder.put(SystemObject.CREATE_USER_GUID, operatorGuid);

				// 判断是公有的还是私有的文件夹，如果是私有文件夹，那么给拥有者Guid字段赋值为登录者的guid
				if (FolderTypeEnum.PRIVATE.equals(folder.getType()))
				{
					folder.setOwnerUserGuid(operatorGuid);
				}

				retFolder = ds.createFolder(folder, this.stubService.getUserSignature().getCredential(), this.stubService.getFixedTransactionId(),
						Constants.isSupervisor(true, this.stubService) && isNotOwner);
				needSaveFolderTreeRelation = true;

				// 如果是公共库 那么为这个公共库建根文件夹
				if (retFolder.getType().equals(FolderTypeEnum.LIBRARY))
				{
					Folder rootFolder = new Folder();
					rootFolder.setName(retFolder.getName());
					rootFolder.setFolderType(FolderTypeEnum.LIB_FOLDER);
					rootFolder.setIsValid(true);
					rootFolder.setParentGuid(retFolder.getGuid());
					rootFolder.setUpdateUserGuid(operatorGuid);
					rootFolder.setCreateUserGuid(operatorGuid);
					rootFolder.setLibraryUser(retFolder.getGuid());
					rootFolder.setOwnerUserGuid(operatorGuid);
					ds.createFolder(rootFolder, this.stubService.getUserSignature().getCredential(), this.stubService.getFixedTransactionId(),
							Constants.isSupervisor(true, this.stubService) && isNotOwner);
				}
				needSaveFolderTreeRelation = true;
			}
			else
			{
				if (FolderTypeEnum.LIBRARY.equals(folder.getType()) && "LIB-admin".equals(folder.getOriginalValue(Folder.FOLDER_NAME)) && folder.isChanged(Folder.FOLDER_NAME))
				{
					throw new ServiceRequestException("ID_APP_LIB_CANNT_UPDATE_NAME", "this lib name can't be update");
				}
				else if (FolderTypeEnum.LIB_FOLDER.equals(folder.getType()))
				{
					if (folder.getParentGuid() != null)
					{
						Folder parentFolder = this.getFolder(folder.getParentGuid(), Constants.isSupervisor(true, this.stubService));
						if (parentFolder != null && parentFolder.getType().equals(FolderTypeEnum.LIBRARY) && folder.isChanged(Folder.FOLDER_NAME))
						{
							throw new ServiceRequestException("ID_APP_FOLDER_NAME_CANNT_UPDATE", "this folder name can't be updated");
						}
					}
				}
				else if (FolderTypeEnum.LIBRARY.equals(folder.getType()) && folder.isChanged(Folder.FOLDER_NAME))
				{
					List<Folder> childFolderList = this.listSubFolder(folder.getGuid(), true);
					if (!SetUtils.isNullList(childFolderList))
					{
						Folder rootFolder = childFolderList.get(0);
						rootFolder.setName(folder.getName());
						ds.updateFolder(rootFolder, this.stubService.getUserSignature().getCredential(), this.stubService.getFixedTransactionId(),
								Constants.isSupervisor(true, this.stubService) && isNotOwner);
					}
				}
				else if (FolderTypeEnum.LIBRARY.equals(folder.getType()) && "LIB-admin".equals(folder.getOriginalValue(Folder.FOLDER_NAME)) && !folder.isValid())
				{
					throw new ServiceRequestException("ID_APP_LIB_CANNT_UPDATE_VALID", "this lib valid can't be update");
				}

				retFolder = ds.updateFolder(folder, this.stubService.getUserSignature().getCredential(), this.stubService.getFixedTransactionId(),
						Constants.isSupervisor(true, this.stubService) && isNotOwner);
			}

			// DataServer.getTransactionManager().commitTransaction();
			return retFolder;
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			needSaveFolderTreeRelation = false;
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByDynaDataException(e, folder.getName());
		}
		catch (Exception e)
		{
			needSaveFolderTreeRelation = false;
			// DataServer.getTransactionManager().rollbackTransaction();
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
			if (needSaveFolderTreeRelation)
			{
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new SaveFolderTreeScheduledTask(serviceContext, retFolder, false, stubService.getUserSignature()));
			}
		}
	}

	protected SaAclFolderLibConf saveSaAclFolderLibConf(SaAclFolderLibConf saAclFolderLibConf) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			SaAclFolderLibConf retSaAclFolderLibConf = null;
			boolean isCreate = false;
			String saAclFolderLibConfGuid = saAclFolderLibConf.getGuid();
			String operatorGuid = this.stubService.getOperatorGuid();

			saAclFolderLibConf.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (!StringUtils.isGuid(saAclFolderLibConfGuid))
			{
				isCreate = true;
				saAclFolderLibConf.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			Folder folder = this.getFolder(saAclFolderLibConf.getFolderGuid(), true);

			if (folder == null)
			{
				return null;
			}

			String ret = sds.save(saAclFolderLibConf);

			if (isCreate)
			{
				saAclFolderLibConfGuid = ret;
			}

			retSaAclFolderLibConf = this.getSaAclFolderLibConf(saAclFolderLibConf.getFolderGuid());

			return retSaAclFolderLibConf;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected Folder getRootPrivateFolderByUser(String userGuid, String userId) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(userGuid))
		{
			User user = this.stubService.getAAS().getUserById(userId);
			if (user == null)
			{
				return null;
			}
			else
			{
				userGuid = user.getGuid();
			}
		}

		return DataServer.getFolderService().getRootPrivateFolderByUser(userGuid);
	}

	protected Folder getDefaultRootFolderByUserGroup(String groupGuid) throws ServiceRequestException
	{
		Folder retFolder = null;
		try
		{
			Group group = this.stubService.getAAS().getGroup(groupGuid);
			if (group == null || group.getLibraryGuid() == null)
			{
				return null;
			}

			Folder lib = this.stubService.getFolder(group.getLibraryGuid());

			if (lib != null)
			{
				List<Folder> folderList = this.stubService.listSubFolder(lib.getGuid());
				if (!SetUtils.isNullList(folderList))
				{
					retFolder = folderList.get(0);
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}

		return retFolder;
	}
}

class SaveFolderTreeScheduledTask extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;
	private Folder			folder			= null;
	private boolean			isDelete		= false;
	private EDAP			edap			= null;
	private UserSignature	userSignature;

	public SaveFolderTreeScheduledTask(ServiceContext serviceContext, Folder folder, boolean isDelete, UserSignature userSignature)
	{
		this.serviceContext = serviceContext;
		this.folder = folder;
		this.isDelete = isDelete;
		this.userSignature = userSignature;
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]SaveFolderTreeScheduledTask , Scheduled Task Start...");
		try
		{
			this.edap = this.serviceContext.allocatService(EDAP.class);
			if (this.isDelete)
			{
				TreeDataRelation relation = new TreeDataRelation();
				relation.setDataGuid(folder.getGuid());
				relation.setSubDataGuid(folder.getGuid());
				relation.setDataType(TreeDataRelation.DATATYPE_FOLDER);
				DataServer.getSystemDataService().delete(TreeDataRelation.class, relation, "deleteBy");
			}
			else
			{
				TreeDataRelation relation = new TreeDataRelation();
				relation.setDataGuid(folder.getGuid());
				relation.setSubDataGuid(folder.getGuid());
				relation.setDataType(TreeDataRelation.DATATYPE_FOLDER);
				relation.setCreateUserGuid(this.userSignature.getUserGuid());
				DataServer.getSystemDataService().save(relation);

				String parentFolderGuid = folder.getParentGuid();
				while (StringUtils.isGuid(parentFolderGuid))
				{
					Folder parentFolder = ((EDAPImpl) this.edap).getFolderStub().getFolder(parentFolderGuid, this.userSignature.getUserGuid(),
							this.userSignature.getLoginGroupGuid(), this.userSignature.getLoginRoleGuid(), false);
					if (parentFolder != null)
					{
						relation = new TreeDataRelation();
						relation.setDataGuid(parentFolder.getGuid());
						relation.setSubDataGuid(folder.getGuid());
						relation.setDataType(TreeDataRelation.DATATYPE_FOLDER);
						relation.setCreateUserGuid(this.userSignature.getUserGuid());
						DataServer.getSystemDataService().save(relation);
						parentFolderGuid = parentFolder.getParentGuid();
					}
					else
					{
						parentFolderGuid = null;
					}
				}
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error("run send mail:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.edap != null)
			{
				this.serviceContext.releaseService(this.edap);
			}
		}
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]SaveFolderTreeScheduledTask , Scheduled Task End...");
	}

}
