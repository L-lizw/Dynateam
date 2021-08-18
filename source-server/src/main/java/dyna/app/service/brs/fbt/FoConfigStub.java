/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoConfigStub
 * wangweixia 2012-9-7
 */
package dyna.app.service.brs.fbt;

import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.FileOpenConfig;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对应FileOpenConfig的相关操作
 * 
 * @author wangweixia
 * 
 */
@Component
public class FoConfigStub extends AbstractServiceStub<FBTSImpl>
{

	private static final String SUFFIXEDELIMITER = ";";

	/**
	 * @param addFileOpenConfigList
	 * @param updateFileOpenConfigList
	 * @param deleteFileOpenConfigList
	 */
	protected void batchSaveFileOpenConfig(List<FileOpenConfig> addFileOpenConfigList, List<FileOpenConfig> updateFileOpenConfigList, List<FileOpenConfig> deleteFileOpenConfigList)
			throws ServiceRequestException
	{

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 删除FileOpenConfig
			if (!SetUtils.isNullList(deleteFileOpenConfigList))
			{
				for (FileOpenConfig deleteconfig : deleteFileOpenConfigList)
				{
					this.stubService.getFoConfigStub().deleteConfig(deleteconfig.getGuid());
				}
			}
			// 更新FileOpenConfig
			if (!SetUtils.isNullList(updateFileOpenConfigList))
			{
				for (FileOpenConfig updateConfig : updateFileOpenConfigList)
				{
					this.stubService.getFoConfigStub().saveConfig(updateConfig);
				}

			}
			// 新增FileOpenConfig
			if (!SetUtils.isNullList(addFileOpenConfigList))
			{
				for (FileOpenConfig addConfig : addFileOpenConfigList)
				{
					this.stubService.getFoConfigStub().saveConfig(addConfig);
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
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

	/**
	 * 保存FileOpenConfig
	 * 
	 * @param guid
	 */
	protected FileOpenConfig saveConfig(FileOpenConfig fileOpenConfig) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			String userGuid = this.stubService.getOperatorGuid();

			boolean isCreate = false;
			if (!StringUtils.isGuid(fileOpenConfig.getGuid()))
			{
				isCreate = true;
				fileOpenConfig.setCreateUserGuid(userGuid);
				fileOpenConfig.setUpdateUserGuid(userGuid);
			}

			fileOpenConfig.setUpdateUserGuid(userGuid);

			String guid = sds.save(fileOpenConfig);
			if (!isCreate)
			{
				guid = fileOpenConfig.getGuid();
			}

			return this.getFileOpenConfigByGuid(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, fileOpenConfig.getFiletypename());
		}
	}

	/**
	 * @param guid
	 */
	protected boolean deleteConfig(String fileOpenConfigGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		boolean hasDeleted = false;

		try
		{
			hasDeleted = sds.delete(FileOpenConfig.class, fileOpenConfigGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
		return hasDeleted;

	}

	/**
	 * @return
	 */
	protected List<FileOpenConfig> listFileOpenConfig()
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<FileOpenConfig> fileOpenConfigList = new ArrayList<FileOpenConfig>();
		fileOpenConfigList = sds.listFromCache(FileOpenConfig.class, null);
		return fileOpenConfigList;
	}

	/**
	 * @param guid
	 * @return
	 */
	protected FileOpenConfig getFileOpenConfigByGuid(String fileOpenConfigGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("GUID", fileOpenConfigGuid);

			return sds.getObject(FileOpenConfig.class, fileOpenConfigGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	/**
	 * @param suffix
	 * @return
	 */
	protected List<FileOpenConfig> listFileOpenConfigbySuffix(String suffix) throws ServiceRequestException
	{
		List<FileOpenConfig> returnFileConfigList = new ArrayList<FileOpenConfig>();
		List<FileOpenConfig> fileOpenConfigList = this.listFileOpenConfig();
		if (!SetUtils.isNullList(fileOpenConfigList))
		{
			for (FileOpenConfig fileConfig : fileOpenConfigList)
			{

				if (StringUtils.isNullString(fileConfig.getOpentype()))
				{
					continue;
				}
				String[] synDownloadType = StringUtils.splitStringWithDelimiter(SUFFIXEDELIMITER, fileConfig.getOpentype());
				for (String suf : synDownloadType)
				{
					if (suf.equals(suffix))
					{
						returnFileConfigList.add(fileConfig);
						break;

					}
				}

			}
		}
		return returnFileConfigList;
	}
}
