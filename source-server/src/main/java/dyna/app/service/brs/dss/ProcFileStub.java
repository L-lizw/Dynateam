/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileTransStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.dss;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wanglei
 * 
 */
public class ProcFileStub extends AbstractServiceStub<DSSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected ProcFileStub(ServiceContext context, DSSImpl service)
	{
		super(context, service);
	}

	protected List<DSSFileInfo> listProcessFile(String procRtGuid, String actrtGuid, int startNumber, String createUserGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			// filter.put(DSSFileInfo.PROCESS_GUID, procRtGuid);
			filter.put(DSSFileInfo.ACTIVITY_GUID, actrtGuid);
			filter.put(DSSFileInfo.START_NUMBER, startNumber);
			filter.put(SystemObject.CREATE_USER_GUID, createUserGuid);

			List<DSSFileInfo> fileList = sds.query(DSSFileInfo.class, filter, "selectWfFile");
			this.stubService.getInstFileStub().decodeCreateUser(fileList);
			return fileList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected DSSFileInfo attachFile(String procGuid, String actGuid, int startNumber, DSSFileInfo file) throws ServiceRequestException
	{
		this.stubService.getFileInfoStub().prepareFileInfo(file, false, false);

		String operatorGuid = this.stubService.getOperatorGuid();
		ActivityRuntime actRt = null;
		if (!StringUtils.isNullString(actGuid))
		{
			actRt = this.stubService.getWFE().getActivityRuntime(actGuid);
			procGuid = actRt.getProcessRuntimeGuid();
		}

		ProcessRuntime procRt = this.stubService.getWFE().getProcessRuntime(procGuid);

		file.setProcessGuid(procGuid);
		file.setActivityGuid(actGuid);
		file.setStartNumber(startNumber);

		SystemDataService sds = DataServer.getSystemDataService();

		DSSFileInfo existFileInfo = null;
		String fileName = file.getName();
		List<DSSFileInfo> fileList = this.listProcessFile(procGuid, actGuid, startNumber, operatorGuid);
		if (!SetUtils.isNullList(fileList))
		{
			for (DSSFileInfo fi : fileList)
			{
				if (fileName.equals(fi.getName()) && //
						(actGuid == null || actGuid != null && actGuid.equals(fi.getActivityGuid())))
				{
					existFileInfo = fi;
					break;
				}
			}
		}
		String fileGuid = "";

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());

			fileGuid = sds.save(file, "insertWfFile", "updateWfFile");

			file.setId(procRt.getName() + "_" + (actRt == null ? "" : actRt.getName() + "_") + startNumber + "_" + fileGuid + "." + file.getExtentionName());
			file.setGuid(fileGuid);

			// setting file id
			sds.save(file, "insertWfFile", "updateWfFile");

			if (existFileInfo != null)
			{
				this.detachFile(existFileInfo.getGuid());
			}
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
		if (StringUtils.isNull(fileGuid))
		{
			return null;
		}
		else
		{
			return this.getFile(fileGuid);
		}
	}

	protected void detachFile(String fileGuid) throws ServiceRequestException
	{
		DSSFileInfo fileInfo = this.getFile(fileGuid);
		if (fileInfo == null)
		{
			return;
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
			sds.delete(DSSFileInfo.class, filter, "deleteWfFile");
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

			DSSFileInfo fileInfo = sds.queryObject(DSSFileInfo.class, filter, "selectWfFile");
			return fileInfo;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveFile(DSSFileInfo fileInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getSecondTranId());
			sds.save(fileInfo, "updateWfFile");
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
