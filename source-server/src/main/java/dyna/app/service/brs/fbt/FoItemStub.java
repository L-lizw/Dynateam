/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoItemStub
 * wangweixia 2012-9-7
 */
package dyna.app.service.brs.fbt;

import java.util.ArrayList;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.FileItemAndConfig;
import dyna.common.dto.FileOpenConfig;
import dyna.common.dto.FileOpenItem;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

/**
 * 对应FileOpenItem中的相关操作
 * 
 * @author wangweixia
 * 
 */
public class FoItemStub extends AbstractServiceStub<FBTSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected FoItemStub(ServiceContext context, FBTSImpl service)
	{
		super(context, service);
	}

	/**
	 * @param fileOpenSubjectGuid
	 * @return
	 */
	protected List<FileOpenItem> listFileOpenItemBySubject(String fileOpenSubjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<FileOpenItem> fileItemList = sds.listFromCache(FileOpenItem.class, new FieldValueEqualsFilter<FileOpenItem>("MASTERFK", fileOpenSubjectGuid));
			if (!SetUtils.isNullList(fileItemList))
			{
				for (FileOpenItem item : fileItemList)
				{
					List<FileItemAndConfig> configList = sds.listFromCache(FileItemAndConfig.class, new FieldValueEqualsFilter<FileItemAndConfig>("RULEGUID", item.getGuid()));
					if (!SetUtils.isNullList(configList))
					{
						List<FileOpenConfig> fileOpenconfig = new ArrayList<FileOpenConfig>();
						for (FileItemAndConfig conf : configList)
						{
							FileOpenConfig fileConfig = this.stubService.getFoConfigStub().getFileOpenConfigByGuid(conf.getFoConfigGuid());
							if (fileConfig != null)
							{
								fileOpenconfig.add(fileConfig);
							}
						}
						item.setFileSelectList(fileOpenconfig);
					}

				}
			}
			return fileItemList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	/**
	 * @param ownerUserGuid
	 * @param suffix
	 * @return
	 * @throws ServiceRequestException
	 */
	// protected List<FileOpenItem> listItemByGuids(String ownerUserGuid, String suffix) throws ServiceRequestException
	// {
	// List<FileOpenItem> fileItemList = null;
	// SystemDataService sds = DataServer.getSystemDataService();
	// String groupGuid = this.stubService.getUserSignature().getLoginGroupGuid();
	// String roleGuid = this.stubService.getUserSignature().getLoginRoleGuid();
	// String userGuid = this.stubService.getUserSignature().getUserGuid();
	//
	// Map<String, Object> filter = new HashMap<String, Object>();
	// filter.put("USERGUID", userGuid);
	// if (!StringUtils.isNullString(ownerUserGuid) && userGuid.equals(ownerUserGuid))
	// {
	// filter.put("ISOWNER", "Y");
	// }
	// else
	// {
	// filter.put("ISOWNER", "N");
	// }
	// filter.put("GROUPGUID", groupGuid);
	// filter.put("ROLEGUID", roleGuid);
	// filter.put("FILETYPE", suffix);
	//
	// try
	// {
	// fileItemList = sds.query(FileOpenItem.class, filter, "selectRuleBySessionAndFile");
	//
	// }
	// catch (DynaDataException e)
	// {
	// throw ServiceRequestException.createByDynaDataException(e);
	// }
	// return fileItemList;
	// }

	/**
	 * @param fileItemConfig
	 * @return
	 */
	protected String saveFileOpenItem(FileOpenItem fileItemConfig) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (StringUtils.isNullString(fileItemConfig.getValueGuid()))
			{
				AccessTypeEnum accessType = fileItemConfig.getAccessType();
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

			String guid = sds.save(fileItemConfig);

			return guid;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, fileItemConfig.getValueName() == null ? "" : fileItemConfig.getValueName());
		}
	}

	public List<FileOpenConfig> listFileOpenItemBySubject(String guid, boolean isOwner, String suffix) throws ServiceRequestException
	{
		List<FileOpenItem> list = this.listFileOpenItemBySubject(guid);
		if (!SetUtils.isNullList(list))
		{
			for (FileOpenItem fo : list)
			{
				List<FileOpenConfig> tempList=new ArrayList<>();
				if (!SetUtils.isNullList(fo.getFileSelectList()))
				{
					for (int i = fo.getFileSelectList().size() - 1; i > -1; i--)
					{
						FileOpenConfig foc = fo.getFileSelectList().get(i);
						if (foc.getOpentype().toLowerCase().indexOf(suffix)>-1)
						{
							tempList.add(foc);
						}
					}
				}
				if (!SetUtils.isNullList(tempList))
				{
					if (fo.getAccessType() == AccessTypeEnum.USER)
					{
						if (this.stubService.getUserSignature().getUserGuid().equalsIgnoreCase(fo.getValueGuid()))
						{
							return tempList;
						}
					}
					else if (fo.getAccessType() == AccessTypeEnum.OWNER && isOwner)
					{
						return tempList;
					}
					else if (fo.getAccessType() == AccessTypeEnum.RIG)
					{
						if (this.stubService.getAAS().isUserInRIG(fo.getValueGuid(), this.stubService.getUserSignature().getUserGuid()))
						{
							return tempList;
						}
					}
					else if (fo.getAccessType() == AccessTypeEnum.GROUP)
					{
						if (this.stubService.getAAS().isUserInGroup(fo.getValueGuid(), this.stubService.getUserSignature().getUserGuid()))
						{
							return tempList;
						}
					}
					else if (fo.getAccessType() == AccessTypeEnum.ROLE)
					{
						if (this.stubService.getAAS().isUserInRole(fo.getValueGuid(), this.stubService.getUserSignature().getUserGuid()))
						{
							return tempList;
						}
					}
					else
					{
						return tempList;
					}
				}
			}
		}
		return null;
	}
}
