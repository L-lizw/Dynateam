package dyna.data.service.acl;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.URIG;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class InstanceAuthStub extends DSAbstractServiceStub<AclServiceImpl>
{
	protected static Map<String, ACLSubject> ACL_TREE_LIBRARY_MAP = Collections.synchronizedMap(new HashMap<String, ACLSubject>());

	/**
	 * 按照树形结构返回权限列表
	 * 
	 * @return
	 */
	public Map<String, ACLSubject> listAllSubjectWithTree()
	{
		return new HashMap<>(ACL_TREE_LIBRARY_MAP);
	}

	public boolean hasAuthority(ObjectGuid objectGuid, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException
	{
		this.stubService.getAuthCommonStub().decodeObjectGuid(objectGuid);

		FoundationObject foundationObject = DataServer.getInstanceService().getSystemFieldInfo(objectGuid.getGuid(), objectGuid.getClassName(), false, sessionId);
		Session session = DataServer.getDSCommonService().getSession(sessionId);

		return this.hasAuthority(foundationObject, authorityEnum, session.getUserGuid(), session.getLoginGroupGuid(), session.getLoginRoleGuid());
	}

	public boolean hasAuthority(String foundationGuid, String className, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException
	{
		FoundationObject foundationObject = DataServer.getInstanceService().getSystemFieldInfo(foundationGuid, className, false, sessionId);
		return this.hasAuthority(foundationObject, authorityEnum, sessionId);
	}

	public boolean hasAuthority(FoundationObject foundationObject, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		return this.hasAuthority(foundationObject, authorityEnum, session.getUserGuid(), session.getLoginGroupGuid(), session.getLoginRoleGuid());
	}

	public String getAuthority(String foundationGuid, String className, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		FoundationObject foundationObject = DataServer.getInstanceService().getSystemFieldInfo(foundationGuid, className, false, null);

		return this.getAuthority(foundationObject, userGuid, groupGuid, roleGuid);
	}

	private String getAuthority(FoundationObject foundationObject, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		ACLItem aclItem = this.getACLItem(foundationObject, userGuid, groupGuid, roleGuid);

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < AuthorityEnum.authorityEnumMap.size(); i++)
		{
			AuthorityEnum authorityEnum = AuthorityEnum.authorityEnumMap.get(i);
			if (buffer.length() > 0)
			{
				buffer.append(";");
			}

			String auth = (String) aclItem.get(authorityEnum.getDbKey());
			buffer.append(StringUtils.isNull(auth) ? "2" : auth);
		}

		return buffer.toString();
	}

	/**
	 * 删除数据或者数据和文件夹关系的权限
	 * 
	 * @param objectGuid
	 * @param folderGuid
	 * @param sessionId
	 * @param isDelRef
	 * @return
	 */
	public String getDelAuthority(ObjectGuid objectGuid, String folderGuid, String sessionId, boolean isDelRef) throws ServiceRequestException
	{
		this.stubService.getAuthCommonStub().decodeObjectGuid(objectGuid);

		Session session = DataServer.getSystemDataService().get(Session.class, sessionId);
		Folder folder = DataServer.getFolderService().getFolder(folderGuid, session.getUserGuid(), session.getLoginGroupGuid(), session.getLoginRoleGuid(), false);
		if (isDelRef && folder == null)
		{
			return "0";
		}

		if (isDelRef)
		{
			FolderTypeEnum type = folder.getType();
			String owner = folder.getOwnerUserGuid();
			if (type == FolderTypeEnum.PRIVATE && session.getUserGuid().equals(owner))
			{
				return "0";
			}

			FolderACLItem aclItem = this.stubService.getFolderAuthStub().getFolderAuthority(folderGuid, session.getUserGuid(), session.getLoginGroupGuid(),
					session.getLoginRoleGuid());
			return StringUtils.isNull(aclItem.getOperDelref()) ? "2" : aclItem.getOperDelref();
		}
		else
		{
			String auth = this.getAuthority(objectGuid.getGuid(), objectGuid.getClassName(), session.getUserGuid(), session.getLoginGroupGuid(), session.getLoginRoleGuid());
			ACLItem aclItem = new ACLItem(auth);
			return StringUtils.isNull(aclItem.getOperDelete()) ? "2" : aclItem.getOperDelete();
		}
	}

	/**
	 * 创建实例数据或者创建实例和文件夹关联关系权限
	 * 
	 * @param foundationObject
	 * @param folderGuid
	 * @param sessionId
	 * @param isCreateAuth
	 * @param isRefAuth
	 * @return
	 */
	public String getCreateAuthority(FoundationObject foundationObject, String folderGuid, String sessionId, boolean isCreateAuth, boolean isRefAuth) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		if (isRefAuth)
		{
			FolderACLItem aclItem = this.stubService.getFolderAuthStub().getFolderAuthority(folderGuid, session.getUserGuid(), session.getLoginGroupGuid(),
					session.getLoginRoleGuid());
			return StringUtils.isNull(aclItem.getOperDelref()) ? "2" : aclItem.getOperAddref();
		}

		if (isCreateAuth)
		{
			String auth = this.getAuthority(foundationObject, session.getUserGuid(), session.getLoginGroupGuid(), session.getLoginRoleGuid());
			ACLItem aclItem = new ACLItem(auth);
			return StringUtils.isNull(aclItem.getOperInsert()) ? "2" : aclItem.getOperInsert();
		}
		return null;
	}

	/**
	 * 移交检出权限
	 * 
	 * @param toUserGuid
	 * @param instanceGuid
	 * @param className
	 * @return
	 */
	public String getTransferCheckoutAuthority(String toUserGuid, String instanceGuid, String className) throws ServiceRequestException
	{
		List<URIG> urigList = DataServer.getSystemDataService().listFromCache(URIG.class, new FieldValueEqualsFilter<>(URIG.USER_GUID, toUserGuid));
		if (!SetUtils.isNullList(urigList))
		{
			for (URIG urig : urigList)
			{
				RIG rig = DataServer.getSystemDataService().get(RIG.class, urig.getRoleGroupGuid());
				if (rig != null)
				{
					Group group = DataServer.getSystemDataService().get(Group.class, rig.getGroupGuid());
					if (group != null && group.isAdminGroup())
					{
						return "0";
					}
				}
			}

			for (URIG urig : urigList)
			{
				RIG rig = DataServer.getSystemDataService().get(RIG.class, urig.getRoleGroupGuid());
				if (rig != null)
				{
					Group group = DataServer.getSystemDataService().get(Group.class, rig.getGroupGuid());
					Role role = DataServer.getSystemDataService().get(Role.class, rig.getRoleGuid());
					if (group != null && role != null)
					{
						String auth = this.getAuthority(instanceGuid, className, toUserGuid, group.getGuid(), role.getGuid());
						ACLItem aclItem = new ACLItem(auth);
						if ("0".equals(aclItem.getOperCheckout()) || "1".equals(aclItem.getOperCheckout()))
						{
							return aclItem.getOperCheckout();
						}
					}
				}
			}
		}

		return "2";
	}

	public boolean hasCreateAuthorityForClass(String classGuid, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String groupGuid = session.getLoginGroupGuid();
		String roleGuid = session.getLoginRoleGuid();

		Group group = DataServer.getSystemDataService().get(Group.class, groupGuid);
		if (group != null)
		{
			String libraryGuid = group.getLibraryGuid();

			FoundationObject foundationObject = new FoundationObjectImpl();
			ObjectGuid objectGuid = new ObjectGuid(classGuid, null, null, null);
			foundationObject.setObjectGuid(objectGuid);
			foundationObject.setLocationlib(libraryGuid);
			foundationObject.setOwnerUserGuid(userGuid);
			foundationObject.setOwnerGroupGuid(groupGuid);
			foundationObject.setLifecyclePhaseGuid(this.getFirstLifecyclePhaseInfoOfClass(classGuid).getGuid());
			foundationObject.setStatus(SystemStatusEnum.WIP);

			return this.hasAuthority(foundationObject, AuthorityEnum.CREATE, userGuid, groupGuid, roleGuid);
		}

		return true;
	}

	private LifecyclePhaseInfo getFirstLifecyclePhaseInfoOfClass(String classGuid) throws ServiceRequestException
	{
		ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);
		List<LifecyclePhaseInfo> lifecyclePhaseList = DataServer.getSystemDataService().listFromCache(LifecyclePhaseInfo.class,
				new FieldValueEqualsFilter<>(LifecyclePhaseInfo.MASTERFK, classObject.getLifecycle()));
		for (LifecyclePhaseInfo lifecyclePhase : lifecyclePhaseList)
		{
			if (lifecyclePhase.getLifecycleSequence() == 0)
			{
				return lifecyclePhase;
			}
		}
		return new LifecyclePhaseInfo();
	}

	/**
	 * 按照从上到下，从内到外的顺序取得符合条件的最优权限
	 * 
	 * @param foundationObject
	 * @return
	 */
	public ACLItem getACLItem(FoundationObject foundationObject, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		Map<String, ACLSubject> subjectWithTreeMap = this.stubService.listAllSubjectWithTree();
		if (SetUtils.isNullMap(subjectWithTreeMap))
		{
			return new ACLItem(PermissibleEnum.NO);
		}

		// 构建权限树
		ACLSubject root = subjectWithTreeMap.get(foundationObject.getLocationlib());

		// 取得符合条件的权限集合
		List<ACLSubject> matchMasterList = new ArrayList<>();
		this.findMatchACLMaster(matchMasterList, root, foundationObject);
		if (SetUtils.isNullList(matchMasterList))
		{
			return new ACLItem(PermissibleEnum.NO);
		}

		// 取得权限master的符合条件的权限详细设置
		List<ACLItem> itemList = this.listACLDetail(matchMasterList, foundationObject, userGuid, groupGuid, roleGuid);
		if (SetUtils.isNullList(itemList))
		{
			return new ACLItem(PermissibleEnum.NO);
		}

		// 合并权限，权限值为空时，忽略
		return this.merge(itemList);
	}

	private ACLItem merge(List<ACLItem> itemList)
	{
		ACLItem aclItem = new ACLItem();
		aclItem.setOperSelect(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_SELECT, itemList));
		aclItem.setOperInsert(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_INSERT, itemList));
		aclItem.setOperRevise(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_REVISE, itemList));
		aclItem.setOperDelete(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_DELETE, itemList));
		aclItem.setOperEffective(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_EFFECTIVE, itemList));
		aclItem.setOperObsolete(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_OBSOLETE, itemList));
		aclItem.setOperRelease(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_RLS, itemList));
		aclItem.setOperUnbsolete(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_UNOBSOLETE, itemList));
		aclItem.setOperCheckout(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_CHECKOUT, itemList));
		aclItem.setOperCancelCheckout(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_CANCELCHKOUT, itemList));
		aclItem.setOperChangeOwner(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_CHGOWNER, itemList));
		aclItem.setOperExport(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_EXPORT, itemList));
		aclItem.setOperImport(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_IMPORT, itemList));
		aclItem.setOperPreviewFile(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_PREVIEW_FILE, itemList));
		aclItem.setOperViewfile(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_VIEWFILE, itemList));
		aclItem.setOperDownloadFile(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_DOWNLOADFILE, itemList));
		aclItem.setOperAddFile(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_ADDFILE, itemList));
		aclItem.setOperEditFile(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_EDITFILE, itemList));
		aclItem.setOperDeleteFile(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_DELETEFILE, itemList));
		aclItem.setOperLink(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_LINK, itemList));
		aclItem.setOperEditLink(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_EDITLINK, itemList));
		aclItem.setOperUnLink(this.stubService.getAuthCommonStub().getAuth(ACLItem.OPER_UNLINK, itemList));

		return aclItem;
	}

	public void loadACLTreeOfLibraryMap() throws ServiceRequestException
	{
		ACL_TREE_LIBRARY_MAP.clear();
		List<ACLSubject> list = DataServer.getSystemDataService().listFromCache(ACLSubject.class, null);
		if (!SetUtils.isNullList(list))
		{
			Map<String, List<ACLSubject>> subjectLibraryMap = new HashMap<>();
			for (ACLSubject subject : list)
			{
				String library = subject.getLibraryGuid();
				if (!subjectLibraryMap.containsKey(library))
				{
					subjectLibraryMap.put(library, new ArrayList<>());
				}
				subjectLibraryMap.get(library).add(subject);
			}

			for (String library : subjectLibraryMap.keySet())
			{
				List<ACLSubject> subjectList = subjectLibraryMap.get(library);
				if (!SetUtils.isNullList(subjectList))
				{
					ACLSubject root = buildSubjectTree(subjectList);
					ACL_TREE_LIBRARY_MAP.put(library, root);
				}
			}
		}
	}

	/**
	 * 取得权限master的符合条件的权限详细设置
	 * 
	 * @param master
	 * @param foundationObject
	 * @param sessionId
	 * @return
	 */
	private List<ACLItem> listACLDetail(List<ACLSubject> matchMasterList, FoundationObject foundationObject, String userGuid, String groupGuid, String roleGuid)
			throws ServiceRequestException
	{
		List<ACLItem> finalACLItemList = new ArrayList<>();

		if (!SetUtils.isNullList(matchMasterList))
		{
			for (ACLSubject master : matchMasterList)
			{
				List<ACLItem> tmpAclItemList = this.listACLDetail(master, foundationObject, userGuid, groupGuid, roleGuid);
				if (!SetUtils.isNullList(tmpAclItemList))
				{
					finalACLItemList.addAll(tmpAclItemList);
				}
			}
		}
		return finalACLItemList;
	}

	/**
	 * 取得权限master的符合条件的权限详细设置
	 * 
	 * @param master
	 * @param foundationObject
	 * @param sessionId
	 * @return
	 */
	private List<ACLItem> listACLDetail(ACLSubject master, FoundationObject foundationObject, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		List<ACLItem> finalACLItemList = new ArrayList<>();

		List<ACLItem> aclItemList = DataServer.getSystemDataService().listFromCache(ACLItem.class, new FieldValueEqualsFilter<>(ACLItem.MASTER_FK, master.getGuid()));
		if (!SetUtils.isNullList(aclItemList))
		{
			for (ACLItem item : aclItemList)
			{
				boolean isMatch = this.isACLDetailMatch(item, foundationObject, userGuid, groupGuid, roleGuid);
				if (isMatch)
				{
					finalACLItemList.add(item);
				}
			}

			if (!SetUtils.isNullList(finalACLItemList))
			{
				this.stubService.getAuthCommonStub().setLevelData(finalACLItemList, groupGuid);

				finalACLItemList.sort((o1, o2) -> {
					// 在同一个大组下面，则子组优先
					if (o1.getPrecedence().compareTo(o2.getPrecedence()) == 0)
					{
						return ((BigDecimal) o1.get("LEVELDATA")).compareTo((BigDecimal) o2.get("LEVELDATA"));
					}
					return o1.getPrecedence().compareTo(o2.getPrecedence());
				});
			}
		}
		return finalACLItemList;
	}

	/**
	 * 权限明细是否匹配
	 * 
	 * @param item
	 * @param foundationObject
	 * @param sessionId
	 * @return
	 */
	private boolean isACLDetailMatch(ACLItem item, FoundationObject foundationObject, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		AccessTypeEnum accessType = item.getAccessType();
		switch (accessType)
		{
		case USER:
			return this.stubService.getAuthCommonStub().isUserMatch(userGuid, item.getValueGuid());
		case OWNER:
			return this.stubService.getAuthCommonStub().isUserMatch(userGuid, foundationObject.getOwnerUserGuid());
		case RIG:
			return this.stubService.getAuthCommonStub().isRIGMatch(roleGuid, groupGuid, item.getValueGuid());
		case ROLE:
			return this.stubService.getAuthCommonStub().isRoleMatch(roleGuid, item.getValueGuid());
		case GROUP:
			return this.stubService.getAuthCommonStub().isGroupMatch(groupGuid, item.getValueGuid());
		case OTHERS:
			return true;
		}
		return false;
	}

	/**
	 * 递归取得权限树中符合条件的权限集合
	 * 
	 * @param matchMasterList
	 * @param master
	 * @param foundationObject
	 */
	private void findMatchACLMaster(List<ACLSubject> matchMasterList, ACLSubject master, FoundationObject foundationObject) throws ServiceRequestException
	{
		boolean isMatch = this.isMatchACLMaster(master, foundationObject.getStatus().getId(), foundationObject.getOwnerGroupGuid(), foundationObject.getClassificationGuid(),
				foundationObject.getObjectGuid().getClassGuid(), foundationObject.getLifecyclePhaseGuid(), foundationObject.getRevisionId());
		if (isMatch)
		{
			matchMasterList.add(master);
		}

		List<ACLSubject> children = master.getChildren();
		if (!SetUtils.isNullList(children))
		{
			for (ACLSubject child : children)
			{
				this.findMatchACLMaster(matchMasterList, child, foundationObject);
			}
		}
	}

	private boolean isMatchACLMaster(ACLSubject master, String status, String ownerGroupGuid, String classificationGuid, String classGuid, String lifecyclePhaseGuid,
			String revisionId) throws ServiceRequestException
	{
		AccessConditionEnum condition = master.getCondition();
		if (condition != null)
		{
			switch (condition)
			{
			case HASSTATUS:
				return this.stubService.getAuthCommonStub().isStatusMatch(master.getValueGuid(), status);
			case OWNINGGROUP:
				return this.stubService.getAuthCommonStub().isGroupMatch(ownerGroupGuid, master.getValueGuid());
			case HASCLASSFICATION:
				return this.stubService.getAuthCommonStub().isClassificationMatch(classificationGuid, master.getValueGuid());
			case HASCLASS:
				return this.stubService.getAuthCommonStub().isClassMatch(classGuid, master.getValueGuid());
			case INLIFECYCLEPHASE:
				return this.stubService.getAuthCommonStub().isLifecyclePhaseMatch(master.getValueGuid(), lifecyclePhaseGuid);
			case HASREVISION:
				return this.stubService.getAuthCommonStub().isRevisionMatch(master.getValueGuid(), revisionId);
			}
		}
		return false;
	}

	private boolean hasAuthority(FoundationObject foundationObject, AuthorityEnum authorityEnum, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		ACLItem aclItem = this.getACLItem(foundationObject, userGuid, groupGuid, roleGuid);
		return aclItem.checkPermission(authorityEnum).isPermitted();
	}

	private static ACLSubject buildSubjectTree(List<ACLSubject> subjectList)
	{
		Map<String, List<ACLSubject>> masterTreeMap = new HashMap<>();
		for (ACLSubject master : subjectList)
		{
			String parentGuid = master.getParentGuid();
			if (!StringUtils.isGuid(parentGuid))
			{
				continue;
			}
			if (!masterTreeMap.containsKey(parentGuid))
			{
				masterTreeMap.put(parentGuid, new ArrayList<>());
			}
			masterTreeMap.get(parentGuid).add(master);
		}

		ACLSubject root = null;
		for (ACLSubject master : subjectList)
		{
			if ("ROOT".equals(master.getName()))
			{
				master.setChildren(masterTreeMap.get(master.getGuid()));
				root = master;
			}
			else
			{
				master.setChildren(masterTreeMap.get(master.getGuid()));
			}
			if (!SetUtils.isNullList(master.getChildren()))
			{
				master.getChildren().sort(Comparator.comparingInt(ACLSubject::getPosition));
			}
		}

		if (root != null)
		{
			root.put("HIERARCHY", StringUtils.lpad(String.valueOf(root.getPosition()), 4, '0'));
			setACLPath(root);
		}

		return root;
	}

	private static void setACLPath(ACLSubject subject)
	{
		List<ACLSubject> children = subject.getChildren();
		if (!SetUtils.isNullList(children))
		{
			for (ACLSubject child : children)
			{
				child.put("HIERARCHY", subject.get("HIERARCHY") + ";" + StringUtils.lpad(String.valueOf(child.getPosition()), 4, '0'));
				setACLPath(child);
			}
		}
	}
}
