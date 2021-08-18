/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AnyTableFileStub
 * Wanglei 2011-11-23
 */
package dyna.app.service.brs.dss;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.DSSFileTrans.FileTransTypeEnum;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Wanglei
 * 
 */
@Component
public class AnyTableFileStub extends AbstractServiceStub<DSSImpl>
{

	public void copyFile4Tab(String srcTabName, String srcFkGuid, String destTabName, String destFkGuid) throws ServiceRequestException
	{
		List<DSSFileInfo> fileInfoList = this.listFile(srcTabName, srcFkGuid);
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

		this.copyFile4Tab(destTabName, destFkGuid, srcFileGuids);
	}

	protected void copyFile4Tab(String destTabName, String destFkGuid, String... srcFileGuids) throws ServiceRequestException
	{
		if (srcFileGuids == null || srcFileGuids.length == 0)
		{
			return;
		}

		List<String> destFileGuidList = new ArrayList<String>();
		DSSFileInfo srcFileInfo = null;
		DSSFileInfo destFileInfo = null;
		for (String fileGuid : srcFileGuids)
		{
			srcFileInfo = this.getFile(fileGuid);
			// 清除本地路径
			srcFileInfo.clear(DSSFileInfo.LOCAL_FILE_PATH);
			destFileInfo = this.attachFile4Tab(destTabName, destFkGuid, srcFileInfo);
			destFileGuidList.add(destFileInfo.getGuid());
		}

		DSSFileTrans fileTrans = this.stubService.getTransFileStub().createBatchFileTrans(destFileGuidList, Arrays.asList(srcFileGuids), FileTransTypeEnum.CPY);

		// TODO duanll DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), fileTrans, null,
		// FileOperateEnum.COPY, true);

	}

	public List<DSSFileInfo> listFile(String tabName, String fkGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DSSFileInfo.TAB_NAME, tabName);
			filter.put(DSSFileInfo.TABFK_GUID, fkGuid);

			List<DSSFileInfo> fileList = sds.query(DSSFileInfo.class, filter, "selectAnyTableFile");
			this.stubService.getInstFileStub().decodeCreateUser(fileList);
			return fileList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected DSSFileInfo attachFile4Tab(String tabName, String fkGuid, DSSFileInfo file) throws ServiceRequestException
	{
		this.stubService.getFileInfoStub().prepareFileInfo(file, false, false);

		file.setTabName(tabName);
		file.setTabFkGuid(fkGuid);

		SystemDataService sds = DataServer.getSystemDataService();

		DSSFileInfo existFileInfo = null;
		String fileName = file.getName();
		List<DSSFileInfo> fileList = this.listFile(tabName, fkGuid);
		if (!SetUtils.isNullList(fileList))
		{
			for (DSSFileInfo fi : fileList)
			{
				if (fileName.equals(fi.getName()))
				{
					existFileInfo = fi;
					break;
				}
			}
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());

			String fileGuid = sds.save(file, "insertAnyTableFile", "updateAnyTableFile");

			file.setId(tabName + "_" + fkGuid + "_" + fileGuid + "." + file.getExtentionName());
			file.setGuid(fileGuid);

			// setting file id
			sds.save(file, "insertAnyTableFile", "updateAnyTableFile");

			if (existFileInfo != null)
			{
				this.detachFile4Tab(existFileInfo.getGuid());
			}

			existFileInfo = this.getFile(fileGuid);
//			DataServer.getTransactionManager().commitTransaction();
			return existFileInfo;
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

	protected void detachFile4Tab(String tabName, String fkGuid) throws ServiceRequestException
	{
		List<DSSFileInfo> fileList = this.listFile(tabName, fkGuid);
		if (SetUtils.isNullList(fileList))
		{
			return;
		}

		for (DSSFileInfo fileInfo : fileList)
		{
			this.detachFile4Tab(fileInfo.getGuid());
		}
	}

	protected void detachFile4Tab(String fileGuid) throws ServiceRequestException
	{
		DSSFileInfo fileInfo = this.getFile(fileGuid);
		if (fileInfo == null)
		{
			return;
		}

		if (StringUtils.isNullString(fileInfo.getTabName()) || StringUtils.isNullString(fileInfo.getTabFkGuid()))
		{
			throw new ServiceRequestException("Delete file illegally, not valid table file");
		}

		// DSSFileTrans fileTrans = null;
		ServiceRequestException returnObj = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());

			this.deleteFile(fileGuid);

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
		}
	}

	protected void deleteFile(String fileGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, fileGuid);
			sds.delete(DSSFileInfo.class, filter, "deleteAnyTableFile");
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveFile(DSSFileInfo fileInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());

			sds.save(fileInfo, "updateAnyTableFile");
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected DSSFileInfo getFile(String fileGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, fileGuid);

			DSSFileInfo fileInfo = sds.queryObject(DSSFileInfo.class, filter, "selectAnyTableFile");
			return fileInfo;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
