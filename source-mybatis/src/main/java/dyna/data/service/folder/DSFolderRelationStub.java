/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理实例和文件夹的关联关系
 * JiangHL 2011-5-10
 */
package dyna.data.service.folder;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.data.service.sdm.SystemDataService;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataExceptionSQL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理实例和文件夹的关联关系
 *
 * @author JiangHL
 */
@Repository
public class DSFolderRelationStub extends DSAbstractServiceStub<FolderServiceImpl>
{
	/**
	 * 剪切
	 * 1、个人文件夹之间移动
	 * 2、库中文件夹之间移动
	 *
	 * @param objectGuid
	 * @param toFolderGuid
	 * @param isCheckAcl
	 * @param sessionId
	 * @param isImport
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	protected String moveToFolder(ObjectGuid objectGuid, String fromFolderGuid, String toFolderGuid, boolean isCheckAclFrom, boolean isCheckAclTo, boolean isUpdate,
			boolean isMaster, String sessionId, String fixTranId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String groupGuid = session.getLoginGroupGuid();
		String roleGuid = session.getLoginRoleGuid();
		String fromLibGuid = "";
		String toLibGuid = "";

		Folder fromFolder = null;
		if (StringUtils.isGuid(fromFolderGuid))
		{
			fromFolder = this.stubService.getFolder(fromFolderGuid, sessionId, false);
			if (fromFolder == null)
			{
				throw new DynaDataExceptionSQL("moveToFolder", null, DataExceptionEnum.DS_FOLDER_IS_NULL, fromFolderGuid);
			}
			fromFolder.getLibraryUser();
		}
		else
		{
			isCheckAclFrom = false;
		}

		Folder toFolder = this.stubService.getFolder(toFolderGuid, sessionId, false);
		if (toFolder == null)
		{
			throw new DynaDataExceptionSQL("moveToFolder", null, DataExceptionEnum.DS_FOLDER_IS_NULL, toFolderGuid);
		}
		toLibGuid = toFolder.getLibraryUser();
		try
		{
			AclService aclService = DataServer.getAclService();
			if (ISCHECKACL && (isCheckAclFrom || isCheckAclTo))
			{
				// auth check
				if (!StringUtils.isNullString(fromFolderGuid) && isCheckAclFrom)
				{
					// 有没有删除ref权限
					String delAuth = aclService.getDelAuthority(objectGuid, fromFolderGuid, sessionId, true);
					if (delAuth.equalsIgnoreCase("2"))
					{
						throw new DynaDataExceptionAll("moveToFolder NO DEL REF AUTH.", null, DataExceptionEnum.DS_NO_DEL_PREFERENCE_AUTHORITY);
					}
				}

				if (isCheckAclTo)
				{
					// 有没有添加ref权限
					FolderACLItem toFolderAclItem = aclService.getAuthorityFolder(toFolderGuid, userGuid, groupGuid, roleGuid);
					String toFolderRefAuth = toFolderAclItem.getOperAddref();

					if (toFolderRefAuth.equalsIgnoreCase("2"))
					{
						throw new DynaDataExceptionAll("moveToFolder NO ADD REF AUTH.", null, DataExceptionEnum.DS_NO_ADD_PREFERENCE_AUTHORITY);
					}
				}
				// 跨库移动时，需要判断源库的移除数据的权限，目标库的添加数据的权限。

				if (!fromLibGuid.equals(toLibGuid))
				{
					if (isCheckAclFrom)
					{
						String fromLibDelAuth = aclService.getDelAuthority(objectGuid, fromLibGuid, sessionId, true);
						if (fromLibDelAuth.equalsIgnoreCase("2"))
						{
							throw new DynaDataExceptionAll("moveToFolder NO DEL LIB REF AUTH.", null, DataExceptionEnum.DS_NO_LIB_DEL_PREFERENCE_AUTH);
						}
					}

					if (isCheckAclTo)
					{
						FolderACLItem toLibAclItem = aclService.getAuthorityFolder(toLibGuid, userGuid, groupGuid, roleGuid);
						String toLibRefAuth = toLibAclItem.getOperAddref();
						if (toLibRefAuth.equalsIgnoreCase("2"))
						{
							throw new DynaDataExceptionAll("moveToFolder NO ADD LIB REF AUTH.", null, DataExceptionEnum.DS_NO_LIB_ADD_PREFERENCE_AUTH);
						}
					}

				}
			}

			if (!isUpdate)
			{
				return null;
			}

//			DataServer.getTransactionManager().startTransaction(fixTranId);

			// 2、从库移动到库
			String locationFolder = null;
			if (FolderTypeEnum.LIB_FOLDER == toFolder.getType())
			{
				boolean isChangeLib = false;
				Map<String, Object> selectLibMap = new HashMap<>();
				selectLibMap.put("GUID", objectGuid.getGuid());
				selectLibMap.put("fieldlist", "LOCATIONLIB");
				selectLibMap.put("table", DataServer.getDSCommonService().getTableName(objectGuid.getClassName()));
				Map<String, String> returnMap = (Map<String, String>) this.dynaObjectMapper.select(selectLibMap);
				String locationLib = returnMap.get("LOCATIONLIB");

				if (!StringUtils.isNullString(fromFolderGuid))
				{
					if (FolderTypeEnum.LIB_FOLDER == fromFolder.getType())
					{
						// 如果不是快捷方式
						if (locationLib.equals(fromFolder.getLibraryUser()))
						{
							if (!locationLib.equals(toFolder.getLibraryUser()))
							{
								isChangeLib = true;
							}
						}
					}
				}
				else
				{
					if (!locationLib.equals(toFolder.getLibraryUser()))
					{
						isChangeLib = true;
					}
				}

				// 跨库
				Map<String, Object> map = new HashMap<>();
				if (isChangeLib)
				{
					// 修改 locationLib
					map.put("table", DataServer.getDSCommonService().getTableName(objectGuid.getClassName()));
					map.put("updatestatement", "locationlib = '" + toFolder.getLibraryUser() + "', commitfolder='" + toFolderGuid + "'");
					if (isMaster)
					{
						map.put("MASTERGUID", objectGuid.getMasterGuid());
					}
					else
					{
						map.put("GUID", objectGuid.getGuid());
					}

					if (StringUtils.isGuid(fromFolderGuid))
					{
						map.put("COMMITFOLDER", fromFolderGuid);
					}
					map.put("COMMITFOLDER", fromFolderGuid);
					if (this.dynaObjectMapper.save(map) == 0)
					{
						throw new DynaDataExceptionSQL("move to folder ", null, DataExceptionEnum.DS_MOVE_TO_FOLDER, objectGuid.getGuid());
					}
					// 修改bomview和view的locationLib
					Map<String, Object> parameterObject = new HashMap<>();
					parameterObject.put("END1GUID", objectGuid.getGuid());
					parameterObject.put("end1table", DataServer.getDSCommonService().getTableName(objectGuid.getClassName()));
					List<String> viewTableList = this.listTableNameByInterface(ModelInterfaceEnum.IBOMView);
					for (String tableName : viewTableList)
					{
						parameterObject.put("viewtable", tableName);
						this.dynaObjectMapper.changeViewBomLocation(parameterObject);
					}

					viewTableList = this.listTableNameByInterface(ModelInterfaceEnum.IViewObject);
					for (String tableName : viewTableList)
					{
						parameterObject.put("viewtable", tableName);
						this.dynaObjectMapper.changeViewBomLocation(parameterObject);
					}

					locationFolder = toFolder.getLibraryUser();
				}
				else
				{
					map.put("table", DataServer.getDSCommonService().getTableName(objectGuid.getClassName()));
					map.put("updatestatement", "commitfolder='" + toFolderGuid + "'");
					if (isMaster)
					{
						map.put("MASTERGUID", objectGuid.getMasterGuid());
					}
					else
					{
						map.put("GUID", objectGuid.getGuid());
					}
					if (StringUtils.isGuid(fromFolderGuid))
					{
						map.put("COMMITFOLDER", fromFolderGuid);
					}
					if (this.dynaObjectMapper.save(map) == 0)
					{
						throw new DynaDataExceptionSQL("move to folder ", null, DataExceptionEnum.DS_MOVE_TO_FOLDER, objectGuid.getGuid());
					}
				}
			}

//			DataServer.getTransactionManager().commitTransaction();

			return locationFolder;
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("moveToFolder error.", e, DataExceptionEnum.DS_MOVE_TO_FOLDER, objectGuid.getGuid());
		}
	}

	/**
	 * 根据实例guid查找出关联的所有文件夹
	 *
	 * @param foundationGuid
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	public List<Folder> listFolder(String foundationGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<Folder> folderList = null;
		try
		{
			Map<String, Object> selectFolderGuidMap = new HashMap<>();
			selectFolderGuidMap.put("SELECT", "FOLDERGUID");
			selectFolderGuidMap.put("FROM", "ma_foundation_folder");
			selectFolderGuidMap.put("WHERE", "FOUNDATIONGUID = '" + foundationGuid + "'");
			List<Map<String, String>> returnList = this.dynaObjectMapper.selectAutoHalf(selectFolderGuidMap);
			if (!SetUtils.isNullList(returnList))
			{
				folderList = new ArrayList<>();
				for (Map<String, String> guidMap : returnList)
				{
					Folder folder = sds.get(Folder.class, guidMap.get("FOLDERGUID"));
					if (folder != null)
					{
						folderList.add(folder);
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("listFolder error.", e, DataExceptionEnum.SDS_SELECT, foundationGuid);
		}

		return folderList;
	}

	/**
	 * 创建view和文件夹的关联关系
	 *
	 * @param viewGuid
	 * @param sessionId
	 * @param isCheckAcl
	 * @return
	 */
	protected String createViewReference(String viewGuid, String sessionId)
	{
		return null;
		// DataService ds = DataServer.getDataService();
		// Session session = ds.getSession(sessionId);
		// String userGuid = session.getUserGuid();
		//
		// String addRefResult = this.executeFunction("fun_reference_add_view", //
		// viewGuid, //
		// userGuid//
		// );
		//
		// return addRefResult;
	}

	private List<String> listTableNameByInterface(ModelInterfaceEnum modelInterfaceEnum) throws ServiceRequestException
	{
		List<String> tableNameList = new ArrayList<>();
		if (modelInterfaceEnum != null)
		{
			List<ClassInfo> classObjectList = DataServer.getClassModelService().getClassInfoListImplInterface(modelInterfaceEnum);
			if (!SetUtils.isNullList(classObjectList))
			{
				for (ClassInfo classObject : classObjectList)
				{
					if (classObject.isAbstract())
					{
						continue;
					}
					String tableName = DataServer.getDSCommonService().getTableName(classObject.getName());
					if (tableName != null && !tableNameList.contains(tableName))
					{
						tableNameList.add(tableName);
					}
				}
			}
		}
		if (SetUtils.isNullList(tableNameList))
		{
			throw new DynaDataExceptionAll("className can not be null .", null, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
		return tableNameList;
	}
}
