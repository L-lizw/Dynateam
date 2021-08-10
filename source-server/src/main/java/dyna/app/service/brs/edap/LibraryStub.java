/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LibraryStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.edap;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.aas.Group;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.folder.FolderService;
import dyna.data.service.ins.InstanceService;

/**
 * @author Wanglei
 * 
 */
public class LibraryStub extends AbstractServiceStub<EDAPImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected LibraryStub(ServiceContext context, EDAPImpl service)
	{
		super(context, service);
	}

	protected List<Folder> listLibrary() throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();
		List<Folder> folderList = null;

		try
		{
			folderList = ds.listLibraryByUser(this.stubService.getSignature().getCredential(), false);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return folderList;
	}

	protected List<Folder> listLibraryByUserGroup() throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		try
		{
			return ds.listLibraryByUser(this.stubService.getSignature().getCredential(), Constants.isSupervisor(true, this.stubService));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected Folder setValidFlag(String libraryGuid, boolean isValid) throws ServiceRequestException
	{

		try
		{
			Group group = this.stubService.getAAS().getGroup(this.stubService.getUserSignature().getLoginGroupGuid());
			if (group == null || !group.isAdminGroup())
			{
				throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only");
			}

			Folder folder = this.stubService.getFolder(libraryGuid);
			if (folder == null)
			{
				return null;
			}

			if (folder.getType().equals(FolderTypeEnum.LIBRARY))
			{
				folder.setIsValid(isValid);
				folder = this.stubService.saveFolder(folder);
			}

			return folder;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected Folder getDefaultLibraryByUserGroup() throws ServiceRequestException
	{
		Group group = this.stubService.getAAS().getGroup(this.stubService.getUserSignature().getLoginGroupGuid());

		try
		{
			return this.stubService.getFolder(group.getLibraryGuid());
		}
		catch (ServiceRequestException e)
		{
			if (DataExceptionEnum.DS_READFOLDER_NOAUTH.getMsrId().equalsIgnoreCase(e.getMsrId()))
			{
				e.setMsrId("ID_APP_READ_DEFAULT_FOLDER_NOAUTH");
			}
			throw e;
		}
	}

	/*
	 * protected List<StatisticResult> listStatisticResultByLib(String libraryGuid, StatisticResultEnum
	 * statisticResultEnum) throws ServiceRequestException
	 * {
	 * DataService ds = ServerFactory.getDataService();
	 * List<StatisticResult> statisticResultList = null;
	 * 
	 * try
	 * {
	 * Map<String, Long> resultMap = ds.statisticResultMapByLib(libraryGuid, statisticResultEnum, null,
	 * this.stubService.getUserSignature().getCredential());
	 * 
	 * if (resultMap != null && !resultMap.isEmpty())
	 * {
	 * statisticResultList = new ArrayList<StatisticResult>();
	 * Iterator<String> it = resultMap.keySet().iterator();
	 * while (it.hasNext())
	 * {
	 * StatisticResult statisticResult = new StatisticResult();
	 * statisticResult.setType(statisticResultEnum);
	 * String key = it.next();
	 * statisticResult.setNumber(resultMap.get(key));
	 * if (statisticResultEnum.equals(StatisticResultEnum.BUSINESSOBJECT))
	 * {
	 * BMInfo bmInfo = this.stubService.getEMM().getBizModel(key);
	 * statisticResult.setBMInfo(bmInfo);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.CLASSIFICATION))
	 * {
	 * CodeObjectInfo codeObjectInfo = null;
	 * if (CodeObjectInfo.OTHER_GUID.equals(key))
	 * {
	 * codeObjectInfo = new CodeObjectInfo();
	 * codeObjectInfo.setGuid(key);
	 * }
	 * else
	 * {
	 * codeObjectInfo = this.stubService.getEMM().getCode(key);
	 * }
	 * statisticResult.setCodeObjectInfo(codeObjectInfo);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.GROUP))
	 * {
	 * Group group = this.stubService.getAAS().getGroup(key);
	 * statisticResult.setGroup(group);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.INTERFACE))
	 * {
	 * ModelInterfaceEnum modelInterfaceEnum = ModelInterfaceEnum.valueOf(key);
	 * statisticResult.setInterfaceEnum(modelInterfaceEnum);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.STATUS))
	 * {
	 * SystemStatusEnum systemStatusEnum = SystemStatusEnum.getStatusEnum(key);
	 * statisticResult.setSystemStatusEnum(systemStatusEnum);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.FOLDER))
	 * {
	 * Folder folder = this.stubService.getFolder(key);
	 * statisticResult.setFolder(folder);
	 * }
	 * 
	 * statisticResultList.add(statisticResult);
	 * }
	 * }
	 * }
	 * catch (DynaDataException e)
	 * {
	 * throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
	 * }
	 * 
	 * return statisticResultList;
	 * }
	 */

	/*
	 * protected List<StatisticResult> listStatisticResultByLib(String libraryGuid, StatisticResultEnum
	 * statisticResultEnum, String parent) throws ServiceRequestException
	 * {
	 * DataService ds = ServerFactory.getDataService();
	 * List<StatisticResult> statisticResultList = null;
	 * 
	 * try
	 * {
	 * Map<String, Long> resultMap = ds.statisticResultMapByLib(libraryGuid, statisticResultEnum, parent,
	 * this.stubService.getUserSignature().getCredential());
	 * 
	 * if (resultMap != null && !resultMap.isEmpty())
	 * {
	 * statisticResultList = new ArrayList<StatisticResult>();
	 * Iterator<String> it = resultMap.keySet().iterator();
	 * while (it.hasNext())
	 * {
	 * StatisticResult statisticResult = new StatisticResult();
	 * statisticResult.setType(statisticResultEnum);
	 * String key = it.next();
	 * statisticResult.setNumber(resultMap.get(key));
	 * if (statisticResultEnum.equals(StatisticResultEnum.BUSINESSOBJECT))
	 * {
	 * String bmGuid = "";
	 * if (!StringUtils.isNullString(parent))
	 * {
	 * String[] splitStringWithDelimiter = StringUtils.splitStringWithDelimiter(";", parent);
	 * 
	 * if (splitStringWithDelimiter != null && splitStringWithDelimiter.length > 0)
	 * {
	 * bmGuid = splitStringWithDelimiter[0];
	 * }
	 * 
	 * }
	 * BOInfo boInfo = BMStub.getBizObject(bmGuid, key);
	 * // BOInfo boInfo = this.stubService.getEMM().getCurrentBizObjectByGuid(key);
	 * statisticResult.setBusinessObject(boInfo);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.CLASSIFICATION))
	 * {
	 * CodeItemInfo codeItemInfo = this.stubService.getEMM().getClassification(key);
	 * statisticResult.setClassification(codeItemInfo);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.GROUP))
	 * {
	 * Group group = this.stubService.getAAS().getGroup(key);
	 * statisticResult.setGroup(group);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.INTERFACE))
	 * {
	 * ModelInterfaceEnum modelInterfaceEnum = ModelInterfaceEnum.valueOf(key);
	 * statisticResult.setInterfaceEnum(modelInterfaceEnum);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.STATUS))
	 * {
	 * SystemStatusEnum systemStatusEnum = SystemStatusEnum.getStatusEnum(key);
	 * statisticResult.setSystemStatusEnum(systemStatusEnum);
	 * }
	 * else if (statisticResultEnum.equals(StatisticResultEnum.FOLDER))
	 * {
	 * Folder folder = this.stubService.getFolder(key);
	 * statisticResult.setFolder(folder);
	 * }
	 * 
	 * statisticResultList.add(statisticResult);
	 * }
	 * }
	 * }
	 * catch (DynaDataException e)
	 * {
	 * throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
	 * }
	 * 
	 * return statisticResultList;
	 * }
	 */

	protected Long getMainDataNumberByLib(String libraryGuid) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		try
		{
			return ds.getMainDataNumberByLib(libraryGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected Long getVersionDataNumberByLib(String libraryGuid) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		try
		{
			return ds.getVersionDataNumberByLib(libraryGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void obsoleteFoundationObjectList(List<String> foundationGuidList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(foundationGuidList))
		{
			return;
		}

		for (String guid : foundationGuidList)
		{
			try
			{
				this.obsolete(new ObjectGuid(null, guid, null), true, false);
			}
			catch (ServiceRequestException e)
			{
				DynaLogger.error("effectiveFoundationObjectList:", e);
			}
		}

	}

	/**
	 * 对系统所有满足生效条件的数据进行生效
	 * 
	 * @throws ServiceRequestException
	 */
	// public void runObsolete() throws ServiceRequestException
	// {
	// DataService ds = ServerFactory.getDataService();
	//
	// try
	// {
	//
	// List<String> foundationGuidList = ds.getNeedObsoleteData();
	// if (SetUtils.isNullList(foundationGuidList))
	// {
	// return;
	// }
	//
	// this.obsoleteFoundationObjectList(foundationGuidList);
	// }
	// catch (DynaDataException e)
	// {
	// throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
	// }
	// catch (ServiceRequestException e)
	// {
	// throw e;
	// }
	// }

	/**
	 * 将指定的实例执行废弃操作
	 * 流程中不抛异常
	 * 
	 * @param objectGuid
	 * @param isCheckAcl
	 * @throws ServiceRequestException
	 */
	public void obsolete(ObjectGuid objectGuid, boolean isCheckAcl, boolean isWf) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();
		String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			ds.obsolete(objectGuid, isWf, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}

/*
 * class ObsoleteScheduledTask extends AbstractScheduledTask
 * {
 * private ServiceContext serviceContext = null;
 * 
 * public ObsoleteScheduledTask(ServiceContext serviceContext)
 * {
 * this.serviceContext = serviceContext;
 * }
 * 
 * @Override
 * public void run()
 * {
 * // DynaLogger.info("Obsolete Scheduled [Class]ObsoleteScheduledTask , Scheduled Task Start...");
 * 
 * EDAPImpl edap = null;
 * try
 * {
 * edap = (EDAPImpl) this.serviceContext.allocatService(EDAP.class);
 * edap.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
 * edap.newTransactionId();
 * edap.getLibraryStub().runObsolete();
 * }
 * catch (Throwable e)
 * {
 * DynaLogger.error("run Obsolete:", e);
 * }
 * finally
 * {
 * SecurityContextHolder.clearContext();
 * 
 * if (edap != null)
 * {
 * this.serviceContext.releaseService(edap);
 * }
 * }
 * 
 * // DynaLogger.info("Obsolete Scheduled [Class]ObsoleteScheduledTask , Scheduled Task End...");
 * 
 * }
 * }
 */
