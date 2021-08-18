/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileInfoStub
 * Wanglei 2010-11-12
 */
package dyna.app.service.brs.dss;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.DSSFileTrans.FileTransTypeEnum;
import dyna.common.dto.FileType;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.FileUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 文件信息处理类
 * 
 * @author Wanglei
 * 
 */
@Component
public class FileInfoStub extends AbstractServiceStub<DSSImpl>
{

	protected static void init()
	{
	}

	protected List<FileType> getFileTypeList() throws ServiceRequestException
	{
		return DataServer.getSystemDataService().listFromCache(FileType.class, null);
	}

	public FileType getFileType(final String fileTypeGuidOrIdOrExt) throws ServiceRequestException
	{
		FileType unknown = null;
		String tempKey = fileTypeGuidOrIdOrExt;
		if (tempKey != null)
		{
			tempKey = tempKey.toUpperCase();
		}

		FileType fileType_ = null;
		List<FileType> fileTypeList = getFileTypeList();
		if (!SetUtils.isNullList(fileTypeList))
		{
			for (FileType fileType : fileTypeList)
			{
				if (FileType.UNKNOW_FILE_TYPE.equals(fileType.getId()))
				{
					unknown = fileType;
				}
				if (!StringUtils.isNullString(tempKey))
				{
					if (tempKey.equalsIgnoreCase(fileType.getGuid()) || tempKey.equalsIgnoreCase(fileType.getId()) || tempKey.equalsIgnoreCase(fileType.getExtension()))
					{
						return fileType;
					}
				}
				else if (StringUtils.isNullString(fileType.getId()) || StringUtils.isNullString(fileType.getExtension()))
				{
					fileType_ = fileType;
				}
			}
		}

		if (fileType_ == null)
		{
			fileType_ = unknown;
		}

		if (fileType_ != null)
		{
			return fileType_;
		}
		throw new ServiceRequestException("file type not found");
	}

	protected void prepareFileInfo(DSSFileInfo file, boolean isPreviewFile, boolean isIconFile) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		String operatorId = this.stubService.getUserSignature().getUserId();
		String operatorGroupGuid = this.stubService.getUserSignature().getLoginGroupGuid();
		String operatorGroupId = this.stubService.getUserSignature().getLoginGroupId();

		if (!StringUtils.isGuid(file.getGuid()))
		{
			file.setUploaded(false);
		}
		file.setGuid(null);

		// file path
		file.setFilePath("/" + operatorGroupId + "_" + operatorGroupGuid + "/" + operatorId);
		file.setStorageId(this.stubService.getStorageForGroup(operatorGroupGuid, null).getId());
		file.setSiteId(this.serviceContext.getServerContext().getServerConfig().getServerId());

		// file extention
		file.setExtentionName(FileUtils.getFileExtention(file.getName()));

		if (isPreviewFile)
		{
			file.setFileType(FileType.PREVIEW_FILE_TYPE);
		}
		else if (isIconFile)
		{
			file.setFileType(FileType.ICON_FILE_TYPE);
		}
		else
		{
			boolean updateType = true;
			if (!StringUtils.isNullString(file.getFileType()))
			{
				try
				{
					this.stubService.getFileType(file.getFileType());
					updateType = false;
				}
				catch (Exception ignored)
				{
				}
			}

			if (updateType)
			{
				file.setFileType(this.stubService.getFileType(file.getExtentionName()).getId());
			}
		}

		// create user
		file.setCreateUserGuid(operatorGuid);
		file.setUpdateUserGuid(operatorGuid);

	}

	protected List<FileType> listFileTypeByExtension(final String extension) throws ServiceRequestException
	{
		String tempKey = extension;
		if (tempKey != null)
		{
			tempKey = tempKey.toUpperCase();
		}

		List<FileType> typeList = this.listFileTypeByExtension_(tempKey);
		return new ArrayList<>(typeList);
	}

	private List<FileType> listFileTypeByExtension_(String extension) throws ServiceRequestException
	{
		List<FileType> haveExtensionList = new ArrayList<>();
		List<FileType> tempList = new ArrayList<>();
		List<FileType> fileTypeList = getFileTypeList();
		if (!SetUtils.isNullList(fileTypeList))
		{
			for (FileType fileType : fileTypeList)
			{
				if (!StringUtils.isNullString(extension))
				{
					if (extension.endsWith(fileType.getExtension()))
					{
						haveExtensionList.add(fileType);
					}
				}
				if (StringUtils.isNullString(fileType.getId()) || StringUtils.isNullString(fileType.getExtension()))
				{
					tempList.add(fileType);
				}
			}
		}
		if (!SetUtils.isNullList(haveExtensionList))
		{
			return haveExtensionList;
		}
		return tempList;
	}

	protected List<FileType> listFileType() throws ServiceRequestException
	{
		return Collections.unmodifiableList(getFileTypeList());
	}

	protected DSSFileInfo getFile(String fileGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<>();
			filter.put(DSSFileInfo.GUID, fileGuid);

			return sds.queryObject(DSSFileInfo.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteFile(String fileGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());

			sds.delete(DSSFileInfo.class, fileGuid);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void copyFile(String destFileGuid, String srcFileGuid) throws ServiceRequestException
	{

		DSSFileTrans fileTrans = this.stubService.getTransFileStub().createFileTrans(destFileGuid, srcFileGuid, FileTransTypeEnum.CPY);

		// TODO duanll DSSFileTransProgress.startTransmitSilently(JOptionPane.getRootFrame(), fileTrans, null,
		// FileOperateEnum.COPY, true, null);
	}

	public void copyFile(ObjectGuid destObjectGuid, boolean isCheckAuth, String... srcFileGuids) throws ServiceRequestException
	{
		if (srcFileGuids == null || srcFileGuids.length == 0)
		{
			return;
		}

		List<String> destFileGuidList = new ArrayList<>();
		DSSFileInfo srcFileInfo;
		DSSFileInfo destFileInfo;
		for (String fileGuid : srcFileGuids)
		{
			srcFileInfo = this.getFile(fileGuid);
			// 清除本地路径
			srcFileInfo.clear(DSSFileInfo.LOCAL_FILE_PATH);
			if (FileType.PREVIEW_FILE_TYPE.equals(srcFileInfo.getFileType()))
			{
				destFileInfo = this.stubService.getInstFileStub().attachFile(destObjectGuid, srcFileInfo, true, false, isCheckAuth, false);
			}
			else if (FileType.ICON_FILE_TYPE.equals(srcFileInfo.getFileType()))
			{
				destFileInfo = this.stubService.getInstFileStub().attachFile(destObjectGuid, srcFileInfo, false, true, isCheckAuth, false);
			}
			else
			{
				destFileInfo = this.stubService.getInstFileStub().attachFile(destObjectGuid, srcFileInfo, isCheckAuth, false);
			}
			destFileGuidList.add(destFileInfo.getGuid());
		}

		DSSFileTrans fileTrans = this.stubService.getTransFileStub().createBatchFileTrans(destFileGuidList, Arrays.asList(srcFileGuids), FileTransTypeEnum.CPY);

		// TODO duanll DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), fileTrans, null,
		// FileOperateEnum.COPY, true);
	}

	public void copyFile(FoundationObject destObject, FoundationObject srcObject, boolean isCheckAuth, boolean isOnlyCopyRecord) throws ServiceRequestException
	{
		if (isOnlyCopyRecord)
		{
			DataServer.getInstanceService().copyFileOnlyRecord(destObject, srcObject, this.stubService.getSignature().getCredential(), this.stubService.getFixedTransactionId());
		}
		else
		{
			this.copyFile(destObject.getObjectGuid(), srcObject.getObjectGuid(), isCheckAuth);
		}
	}

	public void copyFile(ObjectGuid destObjectGuid, ObjectGuid srcObjectGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		List<DSSFileInfo> fileInfoList = this.stubService.getInstFileStub().listFile(srcObjectGuid, null, isCheckAuth, true);
		if (SetUtils.isNullList(fileInfoList))
		{
			return;
		}

		int i = 0;
		String[] srcFileGuids = new String[fileInfoList.size()];
		for (DSSFileInfo fileInfo : fileInfoList)
		{
			srcFileGuids[i++] = fileInfo.getGuid();
		}

		this.copyFile(destObjectGuid, isCheckAuth, srcFileGuids);
	}

	public void rollbackFile(ObjectGuid objectGuid, int fromIterationId, int toIterationId) throws ServiceRequestException
	{
		if (toIterationId < 1)
		{
			return;
		}

		for (int i = fromIterationId; i > toIterationId; i--)
		{
			this.deleteIterationFile(objectGuid, i);
		}

	}

	public void deleteIterationFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			Map<String, Object> filter = new HashMap<>();
			filter.put(DSSFileInfo.CLASS_GUID, objectGuid.getClassGuid());
			filter.put(DSSFileInfo.REVISION_GUID, objectGuid.getGuid());
			filter.put(DSSFileInfo.ITERATION_ID, iterationId);

			List<DSSFileInfo> resultList = sds.query(DSSFileInfo.class, filter);
			if (SetUtils.isNullList(resultList))
			{
				return;
			}

			for (DSSFileInfo fileInfo : resultList)
			{
				this.stubService.getInstFileStub().detachFile(fileInfo.getGuid(), true, false);
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected boolean hasIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		DSSFileInfo iconFileInfo = this.stubService.getTransFileStub().getFileInfoByFileType(objectGuid, iterationId, FileType.ICON_FILE_TYPE);
		return iconFileInfo != null;
	}

	protected boolean hasPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		DSSFileInfo previewFileInfo;
		try
		{
			previewFileInfo = this.stubService.getTransFileStub().getFileInfoByFileType(objectGuid, iterationId, FileType.PREVIEW_FILE_TYPE);
		}
		catch (ServiceRequestException e)
		{
			if ("ID_DSS_NO_PREVIEW".equalsIgnoreCase(e.getMsrId()))
			{
				return false;
			}
			else
			{
				throw e;
			}
		}
		return previewFileInfo != null;
	}
}
