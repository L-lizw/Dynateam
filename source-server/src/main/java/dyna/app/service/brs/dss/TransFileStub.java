/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileTransStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.dss;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.serv.DSServerBean;
import dyna.common.bean.serv.DSStorage;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.DSSFileTrans.FileTransTypeEnum;
import dyna.common.dto.FileType;
import dyna.common.dto.acl.ACLItem;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wanglei
 * 
 */
public class TransFileStub extends AbstractServiceStub<DSSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected TransFileStub(ServiceContext context, DSSImpl service)
	{
		super(context, service);
	}

	public DSSFileInfo getFileInfoByFileType(ObjectGuid objectGuid, int iterationId, String fileType) throws ServiceRequestException
	{
		ACLItem aclItem = this.stubService.getACL().getACLItemForObject(objectGuid);
		if (!aclItem.isPreviewFile())
		{
			throw new ServiceRequestException("ID_APP_NO_AUTH_PREVIEW_FILE", "preview permission denied, no authorized.");
		}

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(DSSFileInfo.CLASS_GUID, objectGuid.getClassGuid());
		filter.put(DSSFileInfo.REVISION_GUID, objectGuid.getGuid());
		filter.put(DSSFileInfo.ITERATION_ID, iterationId);
		filter.put(DSSFileInfo.FILE_TYPE, fileType);
		filter.put(Constants.ORDER_BY, "ORDER BY CREATETIME DESC");
		try
		{
			List<DSSFileInfo> fileList = DataServer.getSystemDataService().query(DSSFileInfo.class, filter);
			if (SetUtils.isNullList(fileList))
			{
				if (fileType == FileType.PREVIEW_FILE_TYPE)
				{
					throw new ServiceRequestException("ID_DSS_NO_PREVIEW", "not found preivew file");
				}
				else if (fileType == FileType.ICON_FILE_TYPE)
				{
					throw new ServiceRequestException("ID_DSS_NO_PREVIEW", "not found icon file");
				}
			}

			return fileList.get(0);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		finally
		{
			filter.clear();
			filter = null;
		}
	}

	protected DSSFileTrans downloadPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		DSSFileInfo previewFileInfo = this.getFileInfoByFileType(objectGuid, iterationId, FileType.PREVIEW_FILE_TYPE);

		return this.downloadFile(previewFileInfo.getGuid(), null);
	}

	private boolean isDeleteByLogic()
	{
		boolean isDeleteByLogic = false;
		String deleteByLogic = this.stubService.getServiceDefinition().getInitParameter("delete-by-logic");
		if (StringUtils.isNullString(deleteByLogic) || "true".equalsIgnoreCase(deleteByLogic))
		{
			isDeleteByLogic = true;
		}
		return isDeleteByLogic;
	}

	protected void fileDeleted(String fileTransGuid) throws ServiceRequestException
	{
		Signature sgn = this.stubService.getSignature();
		if (!(sgn instanceof ModuleSignature))
		{
			throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, new AuthorizeException("unauthorized signature"));
		}

		CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();
		String ftpc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_DSS);
		if (sgn.getCredential().equals(ftpc) && this.isDeleteByLogic())
		{
			this.deleteFileTrans(fileTransGuid);
		}
	}

	protected void fileUploaded(String fileTransGuid, long size, String md5) throws ServiceRequestException
	{
		Signature sgn = this.stubService.getSignature();
		if (!(sgn instanceof ModuleSignature))
		{
			throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, new AuthorizeException("unauthorized signature"));
		}

		CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();
		String ftpc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_DSS);
		if (!sgn.getCredential().equals(ftpc))
		{
			return;
		}

		DSSFileTrans fileTrans = this.getFileTrans(fileTransGuid);
		if (fileTrans == null)
		{
			return;
		}
		if (this.isDeleteByLogic())
		{
			this.deleteFileTrans(fileTransGuid);
		}

		String fileGuid = fileTrans.getFileGuid();
		DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
		if (fileInfo != null)
		{
			SystemDataService sds = DataServer.getSystemDataService();

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("DSSFILENAME", fileInfo.getId());
			List<DSSFileInfo> fileList = null;
			if (fileInfo.getGuid().startsWith(DSSImpl.FILE_TYPE_ANYTABLE))
			{
				fileList = sds.query(DSSFileInfo.class, filter, "selectAnyTableFile");
			}
			else if (fileInfo.getGuid().startsWith(DSSImpl.FILE_TYPE_WF))
			{
				fileList = sds.query(DSSFileInfo.class, filter, "selectWfFile");
			}
			else
			{
				fileList = sds.query(DSSFileInfo.class, filter);
			}

			if (md5 != null)
			{
				if (fileInfo.isPrimary())
				{
					try
					{
						DataServer.getInstanceService().saveFile(fileInfo.getRevisionGuid(), fileInfo.getClassGuid(), this.stubService.getSignature().getCredential(), fileGuid,
								fileInfo.getName(), fileInfo.getFileType(), md5);
					}
					catch (DynaDataException e)
					{
						DynaLogger.error(e);
					}
				}
			}

			for (DSSFileInfo tempFileInfo : fileList)
			{
				tempFileInfo.setUploaded(true);
				if (size != -1 && tempFileInfo.getFileSize() == 0)
				{
					tempFileInfo.setFileSize(size);
				}
				if (!StringUtils.isNullString(md5))
				{
					tempFileInfo.setMD5(md5);
				}

				try
				{
//					DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
					if (fileInfo.getGuid().startsWith(DSSImpl.FILE_TYPE_ANYTABLE))
					{
						this.stubService.getAnyTableFileStub().saveFile(tempFileInfo);
					}
					else if (fileInfo.getGuid().startsWith(DSSImpl.FILE_TYPE_WF))
					{
						this.stubService.getProcFileStub().saveFile(tempFileInfo);
					}
					else
					{
						sds.save(tempFileInfo);
					}

//					DataServer.getTransactionManager().commitTransaction();
				}
				catch (DynaDataException e)
				{
//					DataServer.getTransactionManager().rollbackTransaction();
					DynaLogger.error(e);
				}

				if (StringUtils.isGuid(tempFileInfo.getClassGuid()))
				{
					ObjectGuid objectGuid = new ObjectGuid(tempFileInfo.getClassGuid(), null, tempFileInfo.getRevisionGuid(), null);
					this.stubService.getFTS().createTransformQueue4Upload(objectGuid, fileGuid);
				}
			}
		}

	}

	protected DSSFileTrans getDSServerUser(String userId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DSSFileTrans.USER_ID, userId);

			return sds.queryObject(DSSFileTrans.class, filter, "selectUser");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<DSSFileTrans> listFileTrans(SearchCondition searchCondition) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();

			List<DSSFileTrans> fileTransList = sds.query(DSSFileTrans.class, filter);
			if (SetUtils.isNullList(fileTransList))
			{
				return null;
			}
			for (DSSFileTrans fileTrans : fileTransList)
			{
				this.fillupFileTrans(fileTrans);
			}
			return fileTransList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<DSSFileTrans> listFileTransDetail(String fileTransMasterGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DSSFileTrans.MASTER_GUID, fileTransMasterGuid);

			List<DSSFileTrans> fileTransList = sds.query(DSSFileTrans.class, filter);
			if (SetUtils.isNullList(fileTransList))
			{
				return null;
			}
			for (DSSFileTrans fileTrans : fileTransList)
			{
				this.fillupFileTrans(fileTrans);
			}
			return fileTransList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 创建文件传输信息
	 * 
	 * @param fileGuid
	 *            下载/上传/删除: 文件guid; 复制: 目标文件guid
	 * @param filePath
	 *            下载/删除: null; 上传: 需要上传的文件的全路径; 复制: 源文件guid
	 * @param type
	 *            下载DNL/上传UPL/删除DEL/复制CPY
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans createFileTrans(String fileGuid, String filePath, FileTransTypeEnum type) throws ServiceRequestException
	{
		return this.createFileTrans(fileGuid, filePath, type, true);
	}

	/**
	 * 创建文件传输信息
	 * 
	 * @param fileGuid
	 *            下载/上传/删除: 文件guid; 复制: 目标文件guid
	 * @param filePath
	 *            下载/删除: null; 上传: 需要上传的文件的全路径; 复制: 源文件guid
	 * @param type
	 *            下载DNL/上传UPL/删除DEL/复制CPY
	 * @param updateAccesser
	 *            是否更新最后访问者
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans createFileTrans(String fileGuid, String filePath, FileTransTypeEnum type, boolean updateAccesser) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String transactionId = this.stubService.getSecondTranId();
		DSSFileInfo fileInfo = null;
		try
		{

			fileInfo = this.stubService.getFile(fileGuid);
			if (fileInfo == null)
			{
				throw new ServiceRequestException("ID_APP_FILE_NOT_EXIST", "file not exist");
			}

			if (type == FileTransTypeEnum.DNL && !fileInfo.isUploaded())
			{
				throw new ServiceRequestException("ID_APP_FILE_NOT_UPLOADED", "file not uploaded", null, fileInfo.getName());
			}

			if (updateAccesser && type == FileTransTypeEnum.DNL)
			{
				fileInfo.setLastAccessUser(this.stubService.getOperatorGuid());
				sds.save(fileInfo);
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

		try
		{
//			DataServer.getTransactionManager().startTransaction(transactionId);
			DSSFileTrans fileTrans = new DSSFileTrans();

			fileTrans.setTransType(type);
			fileTrans.setUserId(StringUtils.generateRandomUID(0));
			fileTrans.setUserPassword(StringUtils.generateRandomUID(10));
			fileTrans.setCreateUserGuid(this.stubService.getOperatorGuid());

			String fileTransMasterGuid = sds.save(fileTrans, "insertMaster");

			fileTrans.setGuid(null);
			fileTrans.setMasterFK(fileTransMasterGuid);
			fileTrans.setFileGuid(fileGuid);
			fileTrans.setFileName(fileInfo.getName());
			fileTrans.setRealFileName(fileInfo.getId());
			fileTrans.setRealPath(fileInfo.getFilePath());

			if (type == FileTransTypeEnum.UPL)
			{
				fileTrans.setParamFile(this.convertPath(filePath));
			}
			else if (type == FileTransTypeEnum.CPY)
			{
				// if copy file, then filePath is source FileGuid
				String srcFileGuid = filePath;
				String paramFile = this.createFileTrans(srcFileGuid, null, FileTransTypeEnum.DNL, false).getGuid();
				fileTrans.setParamFile(paramFile);
			}

			fileTrans.setStorageId(fileInfo.getStorageId());
			fileTrans.setSiteId(fileInfo.getSiteId());

			String fileTransDetailGuid = sds.save(fileTrans, "insertDetail");
//			DataServer.getTransactionManager().commitTransaction();

			return this.getFileTrans(fileTransDetailGuid);
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
	}

	/**
	 * 批量创建文件传输信息
	 * 
	 * @param fileGuidList
	 *            下载/上传/删除: 文件guid列表; 复制: 目标文件guid列表
	 * @param filePathList
	 *            下载/删除: null; 上传: 需要上传的文件的全路径列表; 复制: 源文件guid列表
	 * @param type
	 *            下载DNL/上传UPL/删除DEL/复制CPY
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans createBatchFileTrans(List<String> fileGuidList, List<String> filePathList, FileTransTypeEnum type) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String transactionId = this.stubService.getSecondTranId();

		boolean isUploadOrCopy = !SetUtils.isNullList(filePathList);
		int size = isUploadOrCopy ? filePathList.size() : 0;

		String fileGuid = null;
		List<DSSFileInfo> fileInfos = new ArrayList<DSSFileInfo>();
		StringBuffer sb = new StringBuffer();
		try
		{
			for (int i = 0; i < fileGuidList.size(); i++)
			{
				fileGuid = fileGuidList.get(i);

				DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
				if (fileInfo == null)
				{
					throw new ServiceRequestException("ID_APP_FILE_NOT_EXIST", "file not exist");
				}

				if (type == FileTransTypeEnum.DNL && !fileInfo.isUploaded())
				{
					throw new ServiceRequestException("ID_APP_FILE_NOT_UPLOADED", "file not uploaded", null, fileInfo.getName());
				}
				fileInfos.add(fileInfo);

				if (isUploadOrCopy && size > i && type == FileTransTypeEnum.DNL)
				{
					fileInfo.setLastAccessUser(this.stubService.getOperatorGuid());
					sds.save(fileInfo);
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
		try
		{
//			DataServer.getTransactionManager().startTransaction(transactionId);
			DSSFileTrans fileTrans = new DSSFileTrans();

			fileTrans.setTransType(type);
			fileTrans.setUserId(StringUtils.generateRandomUID(0));
			fileTrans.setUserPassword(StringUtils.generateRandomUID(10));
			fileTrans.setCreateUserGuid(this.stubService.getOperatorGuid());

			String fileTransMasterGuid = sds.save(fileTrans, "insertMaster");

			fileTrans.setMasterFK(fileTransMasterGuid);

			String retFileTransGuid = null;
			for (int i = 0; i < fileInfos.size(); i++)
			{
				DSSFileInfo fileInfo = fileInfos.get(i);
				fileGuid = fileInfo.getGuid();

				fileTrans.setGuid(null);
				fileTrans.setFileGuid(fileGuid);

				if (isUploadOrCopy)
				{
					if (size > i)
					{
						if (type == FileTransTypeEnum.UPL)
						{
							fileTrans.setParamFile(this.convertPath(filePathList.get(i)));
						}
						else if (type == FileTransTypeEnum.CPY)
						{
							// if copy file, then filePath is source FileGuid
							String srcFileGuid = filePathList.get(i);
							try
							{
								String paramFile = this.createFileTrans(srcFileGuid, null, FileTransTypeEnum.DNL, false).getGuid();
								fileTrans.setParamFile(paramFile);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				if (i == 0)
				{
					sb.append(fileInfo.getName());
				}
				else
				{
					sb.append("，" + fileInfo.getName());
				}
				fileTrans.setFileName(fileInfo.getName());
				fileTrans.setRealFileName(fileInfo.getId());
				fileTrans.setRealPath(fileInfo.getFilePath());

				fileTrans.setStorageId(fileInfo.getStorageId());
				fileTrans.setSiteId(fileInfo.getSiteId());

				retFileTransGuid = sds.save(fileTrans, "insertDetail");
			}
//			DataServer.getTransactionManager().commitTransaction();
			DSSFileTrans fileTrans2 = this.getFileTrans(retFileTransGuid);
			if (fileTrans2 != null)
			{
				fileTrans2.setFileName(sb.toString());
			}
			return fileTrans2;
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
	}

	/**
	 * 替换反斜杠为双反斜杠
	 * 
	 * @param path
	 * @return
	 */
	private String convertPath(String path)
	{
		if (StringUtils.isNullString(path))
		{
			return path;
		}
		return path.replaceAll("\\\\", "\\\\\\\\");
	}

	protected DSSFileTrans getFileTrans(String fileTransGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, fileTransGuid);

			DSSFileTrans fileTrans = sds.queryObject(DSSFileTrans.class, filter);

			if (fileTrans == null)
			{
				return null;
			}

			this.fillupFileTrans(fileTrans);

			return fileTrans;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void deleteFileTrans(String fileTransGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String transactionId = this.stubService.getSecondTranId();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, fileTransGuid);

		DSSFileTrans fileTrans = sds.queryObject(DSSFileTrans.class, filter);
		if (fileTrans == null)
		{
			return;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(transactionId);
			sds.update(DSSFileTrans.class, filter, "updateDetail");
			filter.clear();
			filter.put(SystemObject.GUID, fileTrans.getMasterFK());
			sds.update(DSSFileTrans.class, filter, "updateMaster");
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
	}

	private void fillupFileTrans(DSSFileTrans fileTrans) throws ServiceRequestException
	{
		DSStorage storage = this.stubService.getStorage(fileTrans.getStorageId());
		if (storage == null)
		{
			throw new ServiceRequestException("Not found storage: " + fileTrans.getStorageId());
		}
		fileTrans.setRealPath(this.trimTrailingSlash(storage.getPath()) + fileTrans.getRealPath());

		DSServerBean dsServerBean = this.serviceContext.getServerContext().getServerConfig().getDSServerBean(storage.getDsserverId());
		if (dsServerBean == null)
		{
			throw new ServiceRequestException("Not found dsserver: " + fileTrans.getSiteId());
		}
		String ip = dsServerBean.getIp();
		if ("127.0.0.1".equals(ip) || "localhost".equalsIgnoreCase(ip))
		{
			try
			{
				ip = InetAddress.getLocalHost().getHostAddress();
			}
			catch (UnknownHostException e)
			{
				DynaLogger.warn(e.getMessage());
			}
		}
		fileTrans.setServerIP(ip);
		fileTrans.setServerPort(dsServerBean.getPort());
	}

	private String trimTrailingSlash(String path)
	{
		if (path.charAt(path.length() - 1) == '/')
		{
			return path.substring(0, path.length() - 1);
		}
		else
		{
			return path;
		}
	}

	public DSSFileTrans downloadFile(String fileGuid, AuthorityEnum checkAuth) throws ServiceRequestException
	{
		if (!(checkAuth == null || checkAuth == AuthorityEnum.DOWNLOADFILE || checkAuth == AuthorityEnum.VIEWFILE))
		{
			throw new ServiceRequestException("ID_APP_AUTH_DOWNLOAD_FILE_PARAMER", "permission denied, please check code");
		}

		if (checkAuth == AuthorityEnum.DOWNLOADFILE || checkAuth == AuthorityEnum.VIEWFILE)
		{

			DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
			if (fileInfo == null)
			{
				throw new ServiceRequestException("ID_APP_DOWNLOAD_FIEL_NOT_EXIST", "file is not exist");
			}

			String revisionGuid = fileInfo.getRevisionGuid();
			if (StringUtils.isGuid(revisionGuid))
			{
				FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub()
						.getObjectByGuid(new ObjectGuid(fileInfo.getClassGuid(), null, revisionGuid, null), false);

				ACLItem aclItem = this.stubService.getACL().getACLItemForObject(object.getObjectGuid());
				if (checkAuth == AuthorityEnum.DOWNLOADFILE && !aclItem.isDownloadFile())
				{
					throw new ServiceRequestException("ID_APP_NO_AUTH_DOWNLOAD_FILE", "permission denied, no authorized.");
				}
				else if (checkAuth == AuthorityEnum.VIEWFILE && !aclItem.isViewFile())
				{
					throw new ServiceRequestException("ID_APP_NO_AUTH_VIEW_FILE", "permission denied, no authorized.");
				}
			}
		}

		// if (isCheckAuth)
		// {
		// DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
		// String revisionGuid = fileInfo.getRevisionGuid();
		// if (StringUtils.isGuid(revisionGuid))
		// {
		// FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(
		// revisionGuid, false);
		//
		// ACLItem aclItem = this.stubService.getACL().getACLItemForObject(object.getObjectGuid());
		// if (!aclItem.isDownloadFile())
		// {
		// throw new ServiceRequestException("ID_APP_NO_AUTH_DOWNLOAD_FILE",
		// "permission denied, no authorized.");
		// }
		// }
		// }
		return this.createFileTrans(fileGuid, null, FileTransTypeEnum.DNL);
	}

	/**
	 * 
	 * @param fileGuidList
	 * @return
	 * @throws ServiceRequestException
	 */
	protected DSSFileTrans batchDownloadFile(List<String> fileGuidList) throws ServiceRequestException
	{
		return this.createBatchFileTrans(fileGuidList, null, FileTransTypeEnum.DNL);
	}

	protected DSSFileTrans batchUploadFile(List<String> fileGuidList, List<String> filePathList) throws ServiceRequestException
	{
		return this.createBatchFileTrans(fileGuidList, filePathList, FileTransTypeEnum.UPL);
	}

	protected DSSFileTrans uploadFile(String fileGuid, String filePath) throws ServiceRequestException
	{
		return this.createFileTrans(fileGuid, filePath, FileTransTypeEnum.UPL);
	}

	public DSSFileTrans uploadPreviewFile(ObjectGuid objectGuid, DSSFileInfo file, boolean isCheckAuth) throws ServiceRequestException
	{
		String filePath = file.getFilePath();
		DSSFileInfo fileInfo = this.stubService.getInstFileStub().attachFile(objectGuid, file, true, false, isCheckAuth, true);

		return this.createFileTrans(fileInfo.getGuid(), filePath, FileTransTypeEnum.UPL);
	}

	protected DSSFileTrans uploadIconFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException
	{
		String filePath = file.getFilePath();
		DSSFileInfo fileInfo = this.stubService.getInstFileStub().attachFile(objectGuid, file, false, true, true, true);

		return this.createFileTrans(fileInfo.getGuid(), filePath, FileTransTypeEnum.UPL);
	}

	protected DSSFileTrans downloadIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		DSSFileInfo previewFileInfo = this.getFileInfoByFileType(objectGuid, iterationId, FileType.ICON_FILE_TYPE);

		return this.downloadFile(previewFileInfo.getGuid(), null);

	}

	protected boolean checkUploadFiles(String transFileGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (!StringUtils.isGuid(transFileGuid))
		{
			DynaLogger.info("transFileGuid is null");
			return false;
		}

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DSSFileTrans.MASTER_GUID, transFileGuid);

			List<DSSFileInfo> fileInfoList = sds.query(DSSFileInfo.class, filter, "checkUploadFiles");
			if (!SetUtils.isNullList(fileInfoList))
			{
				return false;
			}
			return true;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void hasDownLoadFileAuthority(String fileGuid) throws ServiceRequestException
	{
		DSSFileInfo fileInfo = this.stubService.getFile(fileGuid);
		if (fileInfo == null)
		{
			throw new ServiceRequestException("ID_APP_DOWNLOAD_FIEL_NOT_EXIST", "file is not exist");
		}

		String revisionGuid = fileInfo.getRevisionGuid();
		if (StringUtils.isGuid(revisionGuid))
		{
			FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(new ObjectGuid(fileInfo.getClassGuid(), null, revisionGuid, null),
					false);

			ACLItem aclItem = this.stubService.getACL().getACLItemForObject(object.getObjectGuid());
			if (!aclItem.isDownloadFile())
			{
				throw new ServiceRequestException("ID_APP_NO_AUTH_DOWNLOAD_FILE", "permission denied, no authorized.");
			}
		}
	}
}
