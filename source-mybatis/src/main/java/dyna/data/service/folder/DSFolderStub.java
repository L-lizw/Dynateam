/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理文件夹
 * JiangHL 2011-5-10
 */
package dyna.data.service.folder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.acl.SaAclFolderLibConf;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

/**
 * 处理文件夹
 *
 * @author JiangHL
 */
public class DSFolderStub extends DSAbstractServiceStub<FolderServiceImpl>
{
	/**
	 * @param context
	 * @param sd
	 * @throws DynaDataException
	 */
	public DSFolderStub(DataServerContext context, FolderServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	/**
	 * 创建文件夹
	 *
	 * @param folder
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @param isCheckAcl
	 * @return
	 * @throws DynaDataException
	 */
	protected Folder createFolder(Folder folder, String sessionId, String fixTranId, boolean isCheckAcl) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String groupGuid = session.getLoginGroupGuid();
		String roleGuid = session.getLoginRoleGuid();

		// 管理员才能创建库，新建库按钮对管理员可见，不用判断权限
		if (folder.getType() != FolderTypeEnum.LIBRARY)
		{
			folder.setGuid(folder.getParentGuid());
			if ((ISCHECKACL && isCheckAcl) && !DataServer.getAclService().hasFolderAuthority(folder, FolderAuthorityEnum.CREATE, userGuid, groupGuid, roleGuid))
			{
				throw new DynaDataExceptionAll("createFolder no auth.", null, DataExceptionEnum.DS_CREATEFOLDER_NOAUTH);
			}
		}

		SystemDataService sds = DataServer.getSystemDataService();
		if (folder.getType() != FolderTypeEnum.LIBRARY)
		{
			String path = "";
			if (!StringUtils.isNullString(folder.getParentGuid()))
			{
				Folder parentfodel = sds.get(Folder.class, folder.getParentGuid());
				if (parentfodel != null)
				{
					if (!StringUtils.isNullString(parentfodel.getHierarchy()))
					{
						path = parentfodel.getHierarchy();
					}
				}
			}
			if (!StringUtils.isNullString(folder.getName()))
			{
				path = path + "/" + folder.getName();
			}
			folder.setHierarchy(path);
		}
		folder.setGuid(null);
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			// 创建folder
			folder.setOwnerUserGuid(userGuid);
			String libuser = this.getLibraryuser(folder);
			folder.setLibraryUser(libuser);
			String folderGuid = sds.save(folder);
			folder.setGuid(folderGuid);

			// 记录folder子父阶层关系
			folder.put("CREATETIME", new Date());

			// 给公共文件夹、库授予父亲的权限
			if (folder.getType() == FolderTypeEnum.LIB_FOLDER)
			{
				this.createDefaultLibFolderAuth(folder, userGuid);
			}

			// 为lib权限树建root 传的库的guid
			if (folder.getType().name().equals(FolderTypeEnum.LIBRARY.name()) && StringUtils.isNullString(folder.getParentGuid()))
			{
				ACLSubject aclSubject = new ACLSubject();
				aclSubject.put(ACLSubject.LIBRARYGUID, folderGuid);
				aclSubject.put(ACLSubject.NAME, "ROOT");
				aclSubject.put(ACLSubject.POSITION, "1");
				aclSubject.put(ACLSubject.CREATE_USER_GUID, userGuid);
				aclSubject.put(ACLSubject.UPDATE_USER_GUID, userGuid);

				sds.save(aclSubject);
			}

			// 库不能同名
			if (folder.getType() == FolderTypeEnum.LIBRARY)
			{
				this.checkLibName(folder.getName());
			}

			this.decorateFolder(folder);

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (SQLException e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			throw new DynaDataExceptionAll("createFolder error." + folder.getName(), e, DataExceptionEnum.DS_CREATE_FOLDER);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("createFolder error." + folder.getName(), e, DataExceptionEnum.DS_CREATE_FOLDER);
		}
		return folder;
	}

	private void createDefaultLibFolderAuth(Folder folder, String userGuid) throws ServiceRequestException
	{
		// 生成文件夹权限继承配置信息
		this.saveFolderLibConf(folder, userGuid);

		// 从父文件夹继承权限
		this.createFolderACLFromParent(folder, userGuid);
	}

	private void createFolderACLFromParent(Folder folder, String userGuid) throws ServiceRequestException
	{
		List<FolderACLItem> folderAuthList = DataServer.getAclService().listFolderAuth(folder.getParentGuid());
		if (!SetUtils.isNullList(folderAuthList))
		{
			for (FolderACLItem aclItem : folderAuthList)
			{
				FolderACLItem folderACLItem = new FolderACLItem();
				folderACLItem.setIsExtend(true);
				folderACLItem.setFolderGuid(folder.getGuid());
				folderACLItem.setPrecedence(aclItem.getPrecedence());
				folderACLItem.setAccessType(aclItem.getAccessType());
				folderACLItem.setValueGuid(aclItem.getValueGuid());
				folderACLItem.setValueName(aclItem.getValueName());
				folderACLItem.setOperRead(aclItem.getOperRead());
				folderACLItem.setOperCreate(aclItem.getOperCreate());
				folderACLItem.setOperDelete(aclItem.getOperDelete());
				folderACLItem.setOperAddref(aclItem.getOperAddref());
				folderACLItem.setOperDelref(aclItem.getOperDelref());
				folderACLItem.setOperRename(aclItem.getOperRename());
				folderACLItem.setCreateUserGuid(userGuid);
				folderACLItem.setUpdateUserGuid(userGuid);
				DataServer.getSystemDataService().save(folderACLItem);
			}
		}
	}

	private void saveFolderLibConf(Folder folder, String userGuid) throws ServiceRequestException
	{
		Folder parentFolder = this.getFolder(folder.getParentGuid(), null, false);
		List<SaAclFolderLibConf> list = DataServer.getSystemDataService().listFromCache(SaAclFolderLibConf.class,
				new FieldValueEqualsFilter<>(SaAclFolderLibConf.FOLDER_GUID, folder.getGuid()));
		if (SetUtils.isNullList(list))
		{
			SaAclFolderLibConf conf = new SaAclFolderLibConf();
			conf.setFolderGuid(folder.getGuid());
			conf.setIsExtend(folder.getType() != FolderTypeEnum.LIBRARY
					&& (folder.getType() != FolderTypeEnum.LIB_FOLDER || parentFolder == null || parentFolder.getType() != FolderTypeEnum.LIBRARY));
			conf.setCreateUserGuid(userGuid);
			conf.setUpdateUserGuid(userGuid);
			DataServer.getSystemDataService().save(conf);
		}
	}

	private String getLibraryuser(Folder folder) throws ServiceRequestException
	{
		int clafication = folder.get("CLASSIFICATION") == null ? 0 : Integer.parseInt((String) folder.get("CLASSIFICATION"));
		String parentGuid = folder.getParentGuid();
		if (StringUtils.isNull(parentGuid) && clafication == 1)
		{
			return folder.getOwnerUserGuid();
		}

		if (folder.getType() == FolderTypeEnum.LIBRARY)
		{
			return folder.getGuid();
		}

		Folder parentFolder = this.getFolder(folder.getParentGuid(), null, false);
		return parentFolder.getLibraryUser();
	}

	/**
	 * @param folderName
	 * @throws SQLException
	 */
	private void checkLibName(String folderName) throws SQLException
	{
		Map<String, Object> libMap = new HashMap<>();
		libMap.put("NAME", folderName);

		Integer count = (Integer) this.dynaObjectMapper.checkLibName(libMap);
		if (count == 1)
		{
			throw new DynaDataExceptionAll("checkLibName error.", null, DataExceptionEnum.DS_LIB_NAME_DUPLICATE);
		}
	}

	/**
	 * 获取文件夹
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @param isCheckAcl
	 * @return
	 * @throws DynaDataException
	 */
	protected Folder getFolder(String folderGuid, String userGuid, String groupGuid, String roleGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Folder folder = sds.get(Folder.class, folderGuid);
		if (folder == null)
		{
			return null;
		}
		this.decorateFolder(folder);
		if ((ISCHECKACL && isCheckAcl))
		{
			// 如果是库，在判断权限的情况下，如果当前用户是库的管理员则可以查看
			if (folder.getType() == FolderTypeEnum.LIBRARY)
			{
				// 如果库被卸载，那么除了系统管理员外都不能查看该库
				if (!folder.isValid())
				{
					throw new DynaDataExceptionAll("the lib  is not valid ,the folder name is " + folder.getName(), null, DataExceptionEnum.DS_ISNOTVALID);
				}
				String libManager = folder.getOwnerUserGuid();
				if (userGuid.equals(libManager))
				{
					return folder;
				}
			}
			// 如果是库文件夹，在判断权限的情况下，如果当前用户是库的管理员则可以查看
			else if (folder.getType() == FolderTypeEnum.LIB_FOLDER)
			{
				Folder libFolder = sds.get(Folder.class, folder.getLibraryUser());
				// 如果库被卸载，那么除了系统管理员外都不能查看该库
				if (!libFolder.isValid())
				{
					throw new DynaDataExceptionAll("the lib  is not valid ,the folder name is " + folder.getName(), null, DataExceptionEnum.DS_ISNOTVALID);
				}
				String libManager = libFolder.getOwnerUserGuid();
				if (userGuid.equals(libManager))
				{
					return folder;
				}
			}

			if (!DataServer.getAclService().hasFolderAuthority(folder, FolderAuthorityEnum.READ, userGuid, groupGuid, roleGuid))
			{
				throw new DynaDataExceptionAll("getFolder no auth.", null, DataExceptionEnum.DS_READFOLDER_NOAUTH);
			}
		}
		return folder;
	}

	/**
	 * 获取文件夹
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @param isCheckAcl
	 * @return
	 * @throws DynaDataException
	 */
	protected Folder getFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		if ((ISCHECKACL && isCheckAcl))
		{
			Session session = DataServer.getDSCommonService().getSession(sessionId);
			String userGuid = session.getUserGuid();
			String groupGuid = session.getLoginGroupGuid();
			String roleGuid = session.getLoginRoleGuid();
			return getFolder(folderGuid, userGuid, groupGuid, roleGuid, true);
		}

		return getFolder(folderGuid, null, null, null, false);
	}

	/**
	 * 删除文件夹
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @param isCheckAcl
	 * @throws DynaDataException
	 */
	protected void deleteFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		AclService aclService = DataServer.getAclService();

		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String groupGuid = session.getLoginGroupGuid();
		String roleGuid = session.getLoginRoleGuid();

		Folder folder = this.stubService.getFolder(folderGuid, sessionId, false);
		if (folder == null)
		{
			throw new DynaDataExceptionSQL("deleteFolder", null, DataExceptionEnum.DS_FOLDER_IS_NULL, folderGuid);
		}
		try
		{
			// 删除库的时候判断，若库中存在数据（根据对象的location判断），那么不允许删除该库。 变更707
			// 如果没有数据，需要判断权限时，则不能删除
			// 删除库，1、判断是否有权限（系统管理员才可以删除库）--变更864
			// 2、判断是否作为某组的默认库 --bug 1266
			// 3、查看库中是否有数据 --变更707
			if (FolderTypeEnum.LIBRARY == folder.getType())
			{
				if (ISCHECKACL && isCheckAcl)
				{
					throw new DynaDataExceptionAll("delete lib error , the current user is not manager .", null, DataExceptionEnum.DS_DELETE_LIB_NOT_ADMIN);
				}

				List<Group> groupList = sds.listFromCache(Group.class, new FieldValueEqualsFilter<>(Group.LIBRARY_GUID, folderGuid));
				if (!SetUtils.isNullList(groupList))
				{
					List<String> groupNameList = new ArrayList<>();
					for (Group group : groupList)
					{
						String groupName = group.getName();
						groupNameList.add(groupName);
					}
					throw new DynaDataExceptionAll("deleteLib error,the lib is default library for some group .", null, DataExceptionEnum.DS_DELETE_LIB_ERROR_DEFAULTLIB,
							groupNameList.toArray());
				}

			}
			else
			{
				if ((ISCHECKACL && isCheckAcl) && !aclService.hasFolderAuthority(folder, FolderAuthorityEnum.DELETE, userGuid, groupGuid, roleGuid))
				{
					throw new DynaDataExceptionAll("deleteFolder no auth.", null, DataExceptionEnum.DS_DELETEFOLDER_NOAUTH);
				}
			}

			if (!sds.delete(folder))
			{
				throw new DynaDataExceptionAll("deleteFolder error.", null, DataExceptionEnum.DS_DELETEFOLDER_ERROR);
			}

			// 删除权限继承配置信息
			sds.deleteFromCache(SaAclFolderLibConf.class, new FieldValueEqualsFilter<>(SaAclFolderLibConf.FOLDER_GUID, folderGuid));

			// 删除权限
			sds.deleteFromCache(FolderACLItem.class, new FieldValueEqualsFilter<>(FolderACLItem.FOLDER_GUID, folderGuid));
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteFolder error. folderGuid = " + folderGuid, e, DataExceptionEnum.DS_DELETEFOLDER_ERROR, folderGuid);
		}

	}

	/**
	 * 更新文件夹信息
	 *
	 * @param folder
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @param isCheckAcl
	 * @return
	 * @throws DynaDataException
	 */
	protected Folder updateFolder(Folder folder, String sessionId, String fixTranId, boolean isCheckAcl) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		AclService aclService = DataServer.getAclService();

		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String groupGuid = session.getLoginGroupGuid();
		String roleGuid = session.getLoginRoleGuid();

		// 判断此文件夹是否存在，在剪切后，粘贴删除此文件夹时 会不存在
		Folder tempFolder = this.stubService.getFolder(folder.getGuid(), sessionId, false);
		if (tempFolder == null)
		{
			throw new DynaDataExceptionAll("updateFolder no folder .", null, DataExceptionEnum.FOLDER_NOT_EXITSTS);
		}

		if ((ISCHECKACL && isCheckAcl) && !aclService.hasFolderAuthority(folder, FolderAuthorityEnum.RENAME, userGuid, groupGuid, roleGuid))
		{
			throw new DynaDataExceptionAll("updateFolder no auth.", null, DataExceptionEnum.DS_UPDATEFOLDER_NOAUTH);
		}
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			if (sds.save(folder).equals("0"))
			{
				throw new DynaDataExceptionAll("updateFolder error.", null, DataExceptionEnum.DS_UPDATEFOLDER_ERROR);
			}

			// 更新文件夹路径
			this.updateFolderAndSubPath(folder.getGuid());

			this.decorateFolder(folder);

			if (folder.getType() == FolderTypeEnum.LIBRARY)
			{
				this.checkLibName(folder.getName());
			}

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (SQLException e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			throw new DynaDataExceptionAll("updateFolder error." + folder.getName(), e, DataExceptionEnum.DS_UPDATEFOLDER_ERROR);
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("updateFolder error." + folder.getName(), e, DataExceptionEnum.DS_UPDATEFOLDER_ERROR);
		}

		return folder;
	}

	/**
	 * 更新当前文件夹路径以及其子节点的路径
	 *
	 * @param folder
	 * @throws DynaDataException
	 */
	private void updateFolderAndSubPath(String folderGuid) throws DynaDataException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Folder folder = sds.get(Folder.class, folderGuid);
		String path = "";
		if (!StringUtils.isNullString(folder.getParentGuid()))
		{
			Folder parentfodel = sds.get(Folder.class, folder.getParentGuid());
			if (parentfodel != null)
			{
				if (!StringUtils.isNullString(parentfodel.getHierarchy()))
				{
					path = parentfodel.getHierarchy();
				}
			}
		}
		if (!StringUtils.isNullString(folder.getName()))
		{
			path = path + "/" + folder.getName();
		}
		folder.setHierarchy(path);
		try
		{
			sds.save(folder);
			List<Folder> list = this.listSubFolder(folderGuid);
			if (!SetUtils.isNullList(list))
			{
				for (Folder sub : list)
				{
					this.updateFolderAndSubPath(sub.getGuid());
				}
			}

		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("updateFolder error." + folderGuid, e, DataExceptionEnum.DS_UPDATEFOLDER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<Folder> listLibraryByUser(String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String groupGuid = session.getLoginGroupGuid();
		String roleGuid = session.getLoginRoleGuid();
		AclService aclService = DataServer.getAclService();
		try
		{
			if (ISCHECKACL && isCheckAcl)
			{
				List<Folder> libraryList = DataServer.getSystemDataService().listFromCache(Folder.class,
						new FieldValueEqualsFilter<>(Folder.CLASSIFICATION, FolderTypeEnum.LIBRARY.toString()));
				if (!SetUtils.isNullList(libraryList))
				{
					for (Iterator<Folder> it = libraryList.iterator(); it.hasNext();)
					{
						Folder library = it.next();
						if (!aclService.hasFolderAuthority(library, FolderAuthorityEnum.READ, userGuid, groupGuid, roleGuid))
						{
							it.remove();
						}
						this.decorateFolder(library);
					}
				}
				return libraryList;
			}
			else
			{
				List<Folder> libraryList = DataServer.getSystemDataService().listFromCache(Folder.class,
						new FieldValueEqualsFilter<>(Folder.CLASSIFICATION, FolderTypeEnum.LIBRARY.toString()));
				if (!SetUtils.isNullList(libraryList))
				{
					for (Folder library : libraryList)
					{
						this.decorateFolder(library);
					}
				}
				return libraryList;
			}
		}
		catch (Exception ex)
		{
			throw new DynaDataExceptionAll("listLibraire error. ", null, DataExceptionEnum.DS_LISTLIBRAIRE_ERROR);
		}
	}

	/**
	 * 获取子文件夹
	 *
	 * @param folderGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @param isCheckAcl
	 * @return
	 * @throws DynaDataException
	 */
	protected List<Folder> listSubFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws DynaDataException
	{
		if (StringUtils.isNullString(folderGuid))
		{
			return null;
		}
		SystemDataService sds = DataServer.getSystemDataService();

		Session session = null;
		String userGuid = null;
		String groupGuid = null;
		String roleGuid = null;
		if (StringUtils.isGuid(sessionId))
		{
			try
			{
				session = DataServer.getDSCommonService().getSession(sessionId);
				userGuid = session.getUserGuid();
				groupGuid = session.getLoginGroupGuid();
				roleGuid = session.getLoginRoleGuid();
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
		}

		Folder folder = sds.get(Folder.class, folderGuid);

		// 如果当前文件夹为库，或者为库下的文件夹，此时当前用户为库的管理员，则拥有权限
		if ((ISCHECKACL && isCheckAcl))
		{
			// 如果是库，在判断权限的情况下，如果当前用户是库的管理员则可以查看
			if (folder.getType() == FolderTypeEnum.LIBRARY)
			{
				String libManager = folder.getOwnerUserGuid();
				if (userGuid.equals(libManager))
				{
					isCheckAcl = false;
				}
			}
			// 如果是库文件夹，在判断权限的情况下，如果当前用户是库的管理员则可以查看
			else if (folder.getType() == FolderTypeEnum.LIB_FOLDER)
			{
				Folder libFolder = sds.get(Folder.class, folder.getLibraryUser());
				String libManager = libFolder.getOwnerUserGuid();
				if (userGuid.equals(libManager))
				{
					isCheckAcl = false;
				}
			}
		}

		if (folder == null)
		{
			return null;
		}

		try
		{
			List<Folder> folderList = this.listSubFolderWithAuth(folder, userGuid, groupGuid, roleGuid, isCheckAcl);
			if (!SetUtils.isNullList(folderList))
			{
				for (Folder subFolder : folderList)
				{
					this.decorateFolder(subFolder);
				}
			}
			return folderList;
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("listSubFolder error. ", e, DataExceptionEnum.DS_LISTLIBRAIRE_ERROR);
		}
	}

	/**
	 * 获取个人文件夹的根目录
	 *
	 * @param userGuid
	 *            要获取的用户的根目录
	 * @return Folder对象
	 * @throws ServiceRequestException
	 */
	protected Folder getRootPrivateFolderByUser(String userGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			UpperKeyMap filterMap = new UpperKeyMap();
			filterMap.put(Folder.OWNER_USER_GUID, userGuid);
			filterMap.put(Folder.CLASSIFICATION, FolderTypeEnum.PRIVATE.toString());
			List<Folder> folderList = sds.listFromCache(Folder.class, new FieldValueEqualsFilter<>(filterMap));
			if (SetUtils.isNullList(folderList))
			{
				return null;
			}
			Folder privateFolder = folderList.get(0);
			privateFolder.put("LEVEL", 1);
			privateFolder.put("hierarchy", '/' + privateFolder.getName());
			return privateFolder;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<Folder> listAllSubFolder(String folderGuid) throws DynaDataException
	{
		if (StringUtils.isNullString(folderGuid))
		{
			return null;
		}

		List<Folder> subFolderList = new ArrayList<>();
		this.listSubFolderRecursion(folderGuid, subFolderList);
		return subFolderList;
	}

	private void listSubFolderRecursion(String parentFolderGuid, List<Folder> folderList) throws DynaDataException
	{
		List<Folder> subFolderList = this.listSubFolder(parentFolderGuid, null, false);
		if (!SetUtils.isNullList(subFolderList))
		{
			for (Folder subFolder : subFolderList)
			{
				folderList.add(subFolder);
				this.listSubFolderRecursion(subFolder.getGuid(), folderList);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Folder> listSubFolderWithAuth(Folder folder, String userGuid, String groupGuid, String roleGuid, boolean isCheckAuth) throws DynaDataException
	{
		try
		{
			if (isCheckAuth)
			{
				AclService aclService = DataServer.getAclService();

				List<Folder> folderList = this.listSubFolder(folder.getGuid());
				if (!SetUtils.isNullList(folderList))
				{
					for (Iterator<Folder> it = folderList.iterator(); it.hasNext();)
					{
						Folder subFolder = it.next();
						if (!aclService.hasFolderAuthority(subFolder, FolderAuthorityEnum.READ, userGuid, groupGuid, roleGuid))
						{
							it.remove();
						}
					}
				}
				return folderList;
			}
			else
			{
				return this.listSubFolder(folder.getGuid());
			}
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("listSubFolder error. folderGuid = " + folder.getGuid(), e, DataExceptionEnum.DS_READFOLDER_NOAUTH, folder.getGuid());
		}
	}

	protected List<Folder> listSubFolder(String folderGuid)
	{
		List<Folder> folderList = DataServer.getSystemDataService().listFromCache(Folder.class, new FieldValueEqualsFilter<>(Folder.PARENT_GUID, folderGuid));
		return folderList;
	}

	protected long getDataNumberByLib(String libraryGuid, boolean isMaster) throws ServiceRequestException
	{
		DSCommonService commonService = DataServer.getDSCommonService();
		List<String> tableNameList = new ArrayList<>();
		Map<String, ClassObject> classObjectMap = DataServer.getClassModelService().getClassObjectMap();
		for (String className : classObjectMap.keySet())
		{
			ClassObject classObject = classObjectMap.get(className);
			if (classObject.hasInterface(ModelInterfaceEnum.IViewObject) || classObject.hasInterface(ModelInterfaceEnum.IBOMView)
					|| classObject.hasInterface(ModelInterfaceEnum.IStructureObject))
			{
				continue;
			}

			String tableName = commonService.getTableName(className);
			if (!tableNameList.contains(tableName))
			{
				tableNameList.add(tableName);
			}
		}

		int totalCount = 0;
		try
		{
			for (String tableName : tableNameList)
			{
				Integer dataCount = (Integer) this.dynaObjectMapper.selectDataCountOfLibrary(tableName,libraryGuid,BooleanUtils.getBooleanStringYN(isMaster));
				totalCount = totalCount + dataCount;
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("getDataNumberByLib error.", null, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}

		return totalCount;
	}

	/**
	 * 判断文件夹和实例是否有关联关系
	 *
	 * @param foundationGuid
	 * @param folderGuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasRelation(String foundationGuid, String folderGuid) throws DynaDataException
	{
		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("SELECT", "count(1) COUNT");
		selectMap.put("FROM", "ma_foundation_folder");
		selectMap.put("WHERE", "FOUNDATIONGUID = '" + foundationGuid + "' and FOLDERGUID = '" + folderGuid + "'");
		try
		{

			Map<String, BigDecimal> countMap = (Map<String, BigDecimal>) this.dynaObjectMapper.selectAutoHalf(selectMap);
			BigDecimal count = countMap.get("COUNT");
			return count.intValue() > 0;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionSQL("hasRelation", e, DataExceptionEnum.SDS_SELECT, foundationGuid);
		}

	}

	/**
	 * 查出指定用户的所有查询权限的库的根文件夹。
	 *
	 * @param sessionId
	 * @param isCheckAcl
	 * @return
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	protected List<Folder> listRootFolder(String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		List<Folder> folderList = new ArrayList<>();
		List<Folder> libraryList = DataServer.getSystemDataService().listFromCache(Folder.class,
				new FieldValueEqualsFilter<>(Folder.CLASSIFICATION, FolderTypeEnum.LIBRARY.toString()));
		for (Folder library : libraryList)
		{
			Folder rootFolder = DataServer.getSystemDataService().findInCache(Folder.class, new FieldValueEqualsFilter<>(Folder.PARENT_GUID, library.getGuid()));
			folderList.add(rootFolder);
		}
		if (!SetUtils.isNullList(folderList))
		{
			for (Iterator<Folder> it = folderList.iterator(); it.hasNext();)
			{
				if (ISCHECKACL && isCheckAcl)
				{
					try
					{
						this.getFolder(it.next().getGuid(), sessionId, isCheckAcl);
					}
					catch (Exception e)
					{
						it.remove();
					}
				}
			}
		}

		return folderList;
	}

	private void decorateFolder(Folder folder) throws ServiceRequestException
	{
		String ownerUserGuid = folder.getOwnerUserGuid();
		User user = DataServer.getSystemDataService().get(User.class, ownerUserGuid);
		folder.setOwnerUserName(user != null ? user.getUserName() : "");
		if (folder.getType() == FolderTypeEnum.LIB_FOLDER)
		{
			Folder library = this.getFolder(folder.getLibraryUser(), null, false);
			folder.put("HIERARCHY", "/" + library.getName() + folder.get("PATH"));
		}
		else if (folder.getType() == FolderTypeEnum.LIBRARY)
		{
			folder.put("HIERARCHY", folder.getName());
		}
	}
}
