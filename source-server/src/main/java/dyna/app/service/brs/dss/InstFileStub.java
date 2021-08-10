/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileTransStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.dss;

import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.dss.tracked.TRDSSFileInfoImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.FilterBuilder;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.FileType;
import dyna.common.dto.aas.User;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;

/**
 * @author Wanglei
 * 
 */
public class InstFileStub extends AbstractServiceStub<DSSImpl>
{

	/**
	 * 系统允许的预览文件类型列表
	 */
	private static final List<String>	PREVIEW_FILE_EXT_LIST	= Arrays.asList("gif", "jpg", "jpeg", "bmp", "png");
	private static TrackerBuilder		trackerBuilder			= null;

	/**
	 * @param context
	 * @param service
	 */
	protected InstFileStub(ServiceContext context, DSSImpl service)
	{
		super(context, service);
	}

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);

			trackerBuilder.setTrackerRendererClass(TRDSSFileInfoImpl.class, TrackedDesc.DETTACH_FILE);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

	/**
	 * 给实例添加文件, 不能添加预览类型的文件<br>
	 * 如需要添加预览类型的文件, 请使用dss.uploadPreivewFile
	 * 
	 * @param objectGuid
	 * @param file
	 * @param isAclCheck
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo attachFile(ObjectGuid objectGuid, DSSFileInfo file, boolean isAclCheck, boolean isCheckCheckOut) throws ServiceRequestException
	{
		return this.attachFile(objectGuid, file, false, false, isAclCheck, isCheckCheckOut);
	}

	public DSSFileInfo attachFile(ObjectGuid objectGuid, DSSFileInfo file, boolean isPreviewFile, boolean isIconFile, boolean isAclCheck, boolean isCheckCheckOut)
			throws ServiceRequestException
	{
		// instance info
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		if (!isPreviewFile && !isIconFile && !classInfo.hasInterface(ModelInterfaceEnum.IStorage))
		{
			throw new ServiceRequestException("ID_APP_NOT_STORABLE", "not storable object");
		}

		this.stubService.getFileInfoStub().prepareFileInfo(file, isPreviewFile, isIconFile);
		file.setTabName(null);
		file.setTabFkGuid(null);
		file.setProcessGuid(null);
		file.setActivityGuid(null);

		if (FileType.PREVIEW_FILE_TYPE.equals(file.getFileType()) || FileType.ICON_FILE_TYPE.equals(file.getFileType()))
		{
			if (isPreviewFile || isIconFile)
			{
				String ext = file.getExtentionName().toLowerCase();
				if (!PREVIEW_FILE_EXT_LIST.contains(ext))
				{
					throw new ServiceRequestException("ID_APP_INVALID_PREVIEW_FILE_TYPE", "preview/icon file type is stricted by gif,jpg,jpeg,bmp,png.");
				}
			}
			else
			{
				throw new ServiceRequestException("can not attach preview/icon file, only upload preivew file accepted.");
			}
		}

		file.setClassGuid(objectGuid.getClassGuid());
		file.setRevision(objectGuid.getGuid());
		file.setIterationId(null);

		SystemDataService sds = DataServer.getSystemDataService();

		FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(objectGuid, false);

		ACLItem aclItem = null;
		isAclCheck = isAclCheck ? !(!object.isCommited() && object.getOwnerUserGuid().equals(this.stubService.getOperatorGuid())) : isAclCheck;
		if (isAclCheck)
		{
			// SystemStatusEnum status = object.getStatus();
			// if (status == SystemStatusEnum.PRE || status == SystemStatusEnum.RELEASE
			// || status == SystemStatusEnum.OBSOLETE)
			// {
			// throw new ServiceRequestException("ID_APP_FILE_EDIT_DENY",
			// "file edit deny at status (pre-release, release, obsolete)");
			// }

			// if (!StringUtils.isNullString(this.stubService.getWFE().isInRunningProcess(objectGuid)))
			// {
			// // throw new ServiceRequestException("ID_APP_IN_PROCESS", "object in process running.");
			// }

			aclItem = this.stubService.getACL().getACLItemForObject(objectGuid);
		}

		if (isCheckCheckOut && object.isCheckOut() && !this.stubService.getOperatorGuid().equalsIgnoreCase(object.getCheckedOutUserGuid()))
		{
			throw new ServiceRequestException("ID_APP_FILE_EDIT_OTHER_CHECKED", "file not edit object is checked for other user");
		}

		DSSFileInfo existFileInfo = null;
		boolean updatePrimary = false;
		List<DSSFileInfo> listFile = this.listFile(objectGuid, object.getIterationId(), null, false, isPreviewFile || isIconFile);
		if (SetUtils.isNullList(listFile))
		{
			file.setPrimary(true);
			if (!isPreviewFile && !isIconFile)
			{
				updatePrimary = true;
			}

			if (isAclCheck && !aclItem.isAddFile())
			{
				throw new ServiceRequestException("ID_APP_NO_AUTH_ADD_FILE", "permission denied, no authorized.");
			}
		}
		else
		{
			for (DSSFileInfo tmpFileInfo : listFile)
			{
				if (isPreviewFile || isIconFile)
				{
					if (isPreviewFile && FileType.PREVIEW_FILE_TYPE.equals(tmpFileInfo.getFileType()) || //
							isIconFile && FileType.ICON_FILE_TYPE.equals(tmpFileInfo.getFileType()))
					{
						existFileInfo = tmpFileInfo;
						break;
					}
				}
				else
				{
					if (file.getName().toUpperCase().equals(tmpFileInfo.getName().toUpperCase()))
					{
						existFileInfo = tmpFileInfo;
						break;
					}
				}
			}

			if (isAclCheck)
			{
				if (existFileInfo != null)
				{
					if (!aclItem.isEditFile())
					{
						throw new ServiceRequestException("ID_APP_NO_AUTH_EDIT_FILE", "permission denied, no authorized.");
					}
				}
				else if (!aclItem.isAddFile())
				{
					throw new ServiceRequestException("ID_APP_NO_AUTH_ADD_FILE", "permission denied, no authorized.");
				}
			}

			if (!isPreviewFile && !isIconFile && (file.isPrimary() || existFileInfo != null && existFileInfo.isPrimary()))
			{
				updatePrimary = true;
			}
		}

		if (isPreviewFile || isIconFile)
		{
			file.setPrimary(false);
		}
		String fileGuid = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
			fileGuid = sds.save(file);
			String id = object.getId();
			/*
			 * : / \ ? * “ < > |
			 */
			id = id.replace(':', '_');
			id = id.replace('/', '_');
			id = id.replace('\\', '_');
			id = id.replace('?', '_');
			id = id.replace('*', '_');
			id = id.replace('"', '_');
			id = id.replace('<', '_');
			id = id.replace('>', '_');
			id = id.replace('|', '_');
			id = id.replace('\t', '_');
			file.setIterationId(object.getIterationId());
			file.setId(objectGuid.getClassName() + "_" + id + "_" + fileGuid + "." + file.getExtentionName());
			file.setGuid(fileGuid);

			// setting file id
			sds.save(file);

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
			throw ServiceRequestException.createByException("", e);
		}

		if (updatePrimary)
		{
			this.setAsPrimaryFile(fileGuid);
		}

		// file with same name is exist!
		if (existFileInfo != null)
		{
			this.detachFile(existFileInfo.getGuid(), true, false);
		}

		return this.stubService.getFile(fileGuid);
	}

	public void detachFile(String fileGuid, boolean isForce, boolean resetPrimaryFile) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		InstanceService ds = DataServer.getInstanceService();

		DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
		if (fileInfo == null)
		{
			return;
		}

		// boolean deletePhyFile = true;
		String revisionGuid = fileInfo.getRevisionGuid();
		// DSSFileTrans fileTrans = null;
		try
		{
			if (StringUtils.isGuid(revisionGuid))// fix bug#547
			{
				FoundationObject object = null;
				if (!isForce)
				{
					object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(new ObjectGuid(fileInfo.getClassGuid(), null, revisionGuid, null), false);
					isForce = isForce ? isForce : !object.isCommited() && object.getOwnerUserGuid().equals(this.stubService.getOperatorGuid());
				}

				if (!isForce)
				{
					SystemStatusEnum status = object.getStatus();
					if (status == SystemStatusEnum.PRE || status == SystemStatusEnum.RELEASE || status == SystemStatusEnum.OBSOLETE)
					{
						throw new ServiceRequestException("ID_APP_FILE_EDIT_DENY", "file edit deny at status (pre-release, release, obsolete)");
					}

					if (!StringUtils.isNullString(this.stubService.getWFE().isInRunningProcess(object.getObjectGuid())))
					{
						throw new ServiceRequestException("ID_APP_IN_PROCESS", "object in process running.");
					}

					ACLItem aclItem = this.stubService.getACL().getACLItemForObject(object.getObjectGuid());
					if (!aclItem.isDeleteFile())
					{
						throw new ServiceRequestException("ID_APP_NO_AUTH_DELETE_FILE", "permission denied, no authorized.");
					}
				}

				Map<String, Object> filter = new HashMap<String, Object>();
				filter.put(DSSFileInfo.CLASS_GUID, fileInfo.getClassGuid());
				filter.put(DSSFileInfo.REVISION_GUID, fileInfo.getRevisionGuid());
				filter.put(DSSFileInfo.ITERATION_ID, fileInfo.getIterationId());
				if (!FileType.PREVIEW_FILE_TYPE.equals(fileInfo.getFileType()) && !FileType.ICON_FILE_TYPE.equals(fileInfo.getFileType()))
				{
					filter.put("UNPREVIEWFILE", "Y");
				}

				List<DSSFileInfo> resultList = sds.query(DSSFileInfo.class, filter);
				if (SetUtils.isNullList(resultList))
				{
					return;
				}

				if (!isForce && fileInfo.isPrimary() && resultList.size() > 1)
				{
					filter.clear();
					filter = null;
					throw new ServiceRequestException("ID_DSS_CAN_NOT_DEL_PRIMARY_FILE", "can not delete primary file.");
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{

			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			throw ServiceRequestException.createByException("", e);
		}

		ServiceRequestException returnObj = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());

			this.stubService.getFileInfoStub().deleteFile(fileGuid);

//			DataServer.getTransactionManager().commitTransaction();

		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			throw returnObj;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				returnObj = (ServiceRequestException) e;
				throw returnObj;
			}
			returnObj = ServiceRequestException.createByException("", e);
			throw returnObj;
		}
		finally
		{
			Object[] args = new Object[] { fileInfo };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}

		if (resetPrimaryFile && StringUtils.isGuid(fileInfo.getRevisionGuid()) && fileInfo.isPrimary())
		{
			ds.saveFile(fileInfo.getRevisionGuid(), fileInfo.getClassGuid(), this.stubService.getSignature().getCredential(), null, null, null, null);
		}
	}

	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, SearchCondition searchCondition, boolean isCheckAuth) throws ServiceRequestException
	{
		return this.listFile(objectGuid, searchCondition, isCheckAuth, false);
	}

	protected List<DSSFileInfo> listFile(ObjectGuid objectGuid, SearchCondition searchCondition, boolean isCheckAuth, boolean includePreviewFile) throws ServiceRequestException
	{
		FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);
		if (object == null)
		{
			throw new ServiceRequestException("not found object");
		}

		return this.listFile(objectGuid, object.getIterationId(), searchCondition, isCheckAuth, includePreviewFile);
	}

	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, int iterationId, SearchCondition searchCondition, boolean isCheckAuth, boolean includePreviewFile)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			// 变更20130312 listfile时不判断查看权限
			// if (isCheckAuth)
			// {
			// ACLItem aclItem = this.stubService.getACL().getACLItemForObject(objectGuid);
			// if (!aclItem.isViewFile())
			// {
			// throw new ServiceRequestException("ID_APP_NO_AUTH_VIEW_FILE", "permission denied, no authorized.");
			// }
			// }

			Map<String, Object> filter = null;
			if (searchCondition == null)
			{
				filter = new HashMap<String, Object>();
				filter.put(Constants.ORDER_BY, "ORDER BY " + DSSFileInfo.IS_PRIMARY + " DESC");
			}
			else
			{
				searchCondition.addOrder(DSSFileInfo.IS_PRIMARY, false);
				filter = FilterBuilder.buildFilterBySearchCondition(searchCondition);
			}

			filter.put(DSSFileInfo.CLASS_GUID, objectGuid.getClassGuid());
			filter.put(DSSFileInfo.REVISION_GUID, objectGuid.getGuid());
			filter.put(DSSFileInfo.ITERATION_ID, iterationId);
			if (!includePreviewFile)
			{
				filter.put("UNPREVIEWFILE", "Y");
			}

			List<DSSFileInfo> resultList = sds.query(DSSFileInfo.class, filter);
			this.decodeCreateUser(resultList);
			return resultList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}
	public List<DSSFileInfo> listFileByFileType(ObjectGuid objectGuid, int iterationId, SearchCondition searchCondition)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = null;
			if (searchCondition == null)
			{
				filter = new HashMap<String, Object>();
				filter.put(Constants.ORDER_BY, "ORDER BY " + DSSFileInfo.IS_PRIMARY + " DESC");
			}
			else
			{
				searchCondition.addOrder(DSSFileInfo.IS_PRIMARY, false);
				filter = FilterBuilder.buildFilterBySearchCondition(searchCondition);
			}

			filter.put(DSSFileInfo.CLASS_GUID, objectGuid.getClassGuid());
			filter.put(DSSFileInfo.REVISION_GUID, objectGuid.getGuid());
			filter.put(DSSFileInfo.ITERATION_ID, iterationId);
			//filter.put(DSSFileInfo.FILE_TYPE,FileType.PREVIEW_FILE_TYPE);

			List<DSSFileInfo> resultList = sds.query(DSSFileInfo.class, filter);
			return resultList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}
	protected void decodeCreateUser(List<DSSFileInfo> resultList) throws ServiceRequestException
	{
		if (resultList != null)
		{
			for (DSSFileInfo info : resultList)
			{
				if (!StringUtils.isNullString(info.getCreateUserGuid()))
				{
					User user = this.stubService.getAAS().getUser(info.getCreateUserGuid());
					info.put(DSSFileInfo.CREATE_USER_NAME, user.getName());
				}
			}
		}
	}

	protected List<DSSFileInfo> listFile(ObjectGuid objectGuid, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.listFile(objectGuid, searchCondition, true);
	}

	protected void setAsPrimaryFile(String fileGuid) throws ServiceRequestException
	{
		DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
		if (fileInfo == null)
		{
			throw new ServiceRequestException("Not found file: " + fileGuid);
		}

		SystemDataService sds = DataServer.getSystemDataService();
		InstanceService ds = DataServer.getInstanceService();

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DSSFileInfo.CLASS_GUID, fileInfo.getClassGuid());
			filter.put(DSSFileInfo.REVISION_GUID, fileInfo.getRevisionGuid());
			filter.put(DSSFileInfo.ITERATION_ID, fileInfo.getIterationId());

			filter.put(SystemObject.UPDATE_USER_GUID, this.stubService.getOperatorGuid());

			sds.update(DSSFileInfo.class, filter, "resetPrimary");

			filter.clear();
			filter.put(SystemObject.GUID, fileGuid);
			filter.put(SystemObject.UPDATE_USER_GUID, this.stubService.getOperatorGuid());

			sds.update(DSSFileInfo.class, filter, "setPrimary");

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
			throw ServiceRequestException.createByException("", e);
		}

		ds.saveFile(fileInfo.getRevisionGuid(), fileInfo.getClassGuid(), this.stubService.getSignature().getCredential(), fileGuid, fileInfo.getName(), fileInfo.getFileType(),
				fileInfo.getMD5());
	}

	protected void setAsFileType(String fileGuid, String fileTypeGuidOrIdOrExt) throws ServiceRequestException
	{
		FileType fileType = this.stubService.getFileType(fileTypeGuidOrIdOrExt);
		if (FileType.PREVIEW_FILE_TYPE.equals(fileType.getId()))
		{
			throw new ServiceRequestException("can not set preview type, only upload preivew file accepted.");
		}

		DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
		if (fileInfo == null)
		{
			throw new ServiceRequestException("Not found file: " + fileGuid);
		}

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
			fileInfo.setFileType(fileType.getId());

			sds.save(fileInfo);

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

		if (fileInfo.isPrimary())
		{
			DataServer.getInstanceService().saveFile(fileInfo.getRevisionGuid(), fileInfo.getClassGuid(), this.stubService.getSignature().getCredential(), fileGuid,
					fileInfo.getName(), fileType.getId(), fileInfo.getMD5());
		}
	}

	protected void detachIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		DSSFileInfo previewFileInfo = this.stubService.getTransFileStub().getFileInfoByFileType(objectGuid, iterationId, FileType.ICON_FILE_TYPE);
		if (previewFileInfo != null)
		{
			this.detachFile(previewFileInfo.getGuid(), true, false);
		}
	}

	protected void detachPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		DSSFileInfo previewFileInfo = this.stubService.getTransFileStub().getFileInfoByFileType(objectGuid, iterationId, FileType.PREVIEW_FILE_TYPE);
		if (previewFileInfo != null)
		{
			this.detachFile(previewFileInfo.getGuid(), true, false);
		}
	}

	protected void detachFile(String fileTransGuid, List<String> fileNameList) throws ServiceRequestException
	{
		List<DSSFileTrans> listFileTransDetail = this.stubService.listFileTransDetail(fileTransGuid);
		if (!SetUtils.isNullList(listFileTransDetail))
		{
			for (DSSFileTrans detail : listFileTransDetail)
			{
				if (fileNameList.contains(detail.getFileName()))
				{
					DSSFileInfo file = this.stubService.getFile(detail.getFileGuid());
					if (file != null && !file.isUploaded())
					{
						this.detachFile(file.getGuid(), true, true);
					}
				}
			}
		}
	}

	protected void setLocalFilePath(String fileGuid, String fileClientPath) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
			DSSFileInfo fi = new DSSFileInfo();
			fi.setGuid(fileGuid);
			fi.setLocalFilePath(fileClientPath);

			sds.save(fi);
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
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	protected List<String> checkAndDeleteFiles(String transFileGuid, boolean isDelete) throws ServiceRequestException
	{

		if (!StringUtils.isGuid(transFileGuid))
		{
			DynaLogger.info("transFileGuid is null");
			return null;
		}

		List<String> fileInfoList = new ArrayList<String>();
		try
		{

			List<DSSFileTrans> listFileTransDetail = this.stubService.listFileTransDetail(transFileGuid);
			if (!SetUtils.isNullList(listFileTransDetail))
			{
				for (DSSFileTrans detail : listFileTransDetail)
				{
					DSSFileInfo file = this.stubService.getFile(detail.getFileGuid());
					if (file != null && !file.isUploaded())
					{
						if (isDelete)
						{
							this.detachFile(file.getGuid(), true, true);
						}
						fileInfoList.add(detail.getParamFile());
					}
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return fileInfoList;
	}
}
