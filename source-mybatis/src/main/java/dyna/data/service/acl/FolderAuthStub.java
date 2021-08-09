package dyna.data.service.acl;

import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.Folder;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.acl.AbstractACLItem;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.acl.SaAclFolderLibConf;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class FolderAuthStub extends DSAbstractServiceStub<AclServiceImpl>
{
	protected SaAclFolderLibConf getSaAclFolderLibConf(String folderGuid) throws DynaDataException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<>();
		filter.put(SaAclFolderLibConf.FOLDER_GUID, folderGuid);
		List<SaAclFolderLibConf> list = sds.listFromCache(SaAclFolderLibConf.class, new FieldValueEqualsFilter<>(SaAclFolderLibConf.FOLDER_GUID, folderGuid));
		return SetUtils.isNullList(list) ? null : list.get(0);
	}

	public List<FolderACLItem> listChangingLibFolderAuth(String folderGuid, boolean isextend) throws ServiceRequestException
	{
		if (!isextend)
		{
			// 取当前文件夹自己的非继承的权限集合
			UpperKeyMap filter = new UpperKeyMap();
			filter.put(FolderACLItem.FOLDER_GUID, folderGuid);
			filter.put(FolderACLItem.IS_EXTEND, "N");
			List<FolderACLItem> aclItemList = DataServer.getSystemDataService().listFromCache(FolderACLItem.class, new FieldValueEqualsFilter<>(filter));
			if (!SetUtils.isNullList(aclItemList))
			{
				for (FolderACLItem aclItem : aclItemList)
				{
					this.setValueName(aclItem);
				}

				aclItemList.sort(Comparator.comparing(AbstractACLItem::getPrecedence));

				this.clearForTempFolderACLItem(aclItemList);
			}
			return aclItemList;
		}
		else
		{
			// 非继承的当前文件夹自己的权限+继承来的父文件夹权限
			// 取得父文件夹
			Folder folder = DataServer.getFolderService().getFolder(folderGuid, null, false);
			if (folder != null)
			{
				List<FolderACLItem> finalFolderAclItemList = new ArrayList<>();

				// 取得父文件夹继承的权限列表
				List<FolderACLItem> parentFolderAclItemList = this.listFolderAuth(folder.getParentGuid(), false);
				if (!SetUtils.isNullList(parentFolderAclItemList))
				{
					// 构造最终权限条目，先记录父文件夹权限，再修改子文件夹重载的权限
					finalFolderAclItemList.addAll(parentFolderAclItemList);
					for (FolderACLItem aclItem : parentFolderAclItemList)
					{
						aclItem.put(FolderACLItem.PARENT_OPER_READ, aclItem.getOperRead());
						aclItem.put(FolderACLItem.PARENT_OPER_CREATE, aclItem.getOperCreate());
						aclItem.put(FolderACLItem.PARENT_OPER_DELETE, aclItem.getOperDelete());
						aclItem.put(FolderACLItem.PARENT_OPERRE_NAME, aclItem.getOperRename());
						aclItem.put(FolderACLItem.PARENT_OPER_ADDREF, aclItem.getOperAddref());
						aclItem.put(FolderACLItem.PARENT_OPER_DELREF, aclItem.getOperDelref());
					}
				}

				// 取得当前文件夹的权限列表(当前非继承权限+父文件夹权限)
				List<FolderACLItem> currentFolderAclItemList = this.listCurrentFolderACLItemForTemp(folderGuid, parentFolderAclItemList);
				this.distinct(currentFolderAclItemList);

				// 权限合并,对于相同的OBJECTGUID，可能存在继承和非继承数据同时存在的情况，这时需要进行合并，把重载的权限合并到最终权限上
				if (!SetUtils.isNullList(finalFolderAclItemList))
				{
					for (FolderACLItem aclItem : finalFolderAclItemList)
					{
						aclItem.setIsExtend(true);

						// 取得重载的OBJECTGUID权限
						FolderACLItem override = this.getFolderACLItemByObjectGuid(aclItem, currentFolderAclItemList);
						if (override != null)
						{
							// 合并
							this.merge(override, aclItem);
							currentFolderAclItemList.remove(override);
						}
					}
				}

				// 把剩余的非继承的权限添加进来
				if (!SetUtils.isNullList(currentFolderAclItemList))
				{
					finalFolderAclItemList.addAll(currentFolderAclItemList);
				}

				// 按照优先级进行排序
				finalFolderAclItemList.sort(Comparator.comparing(AbstractACLItem::getPrecedence));
				this.clearForTempFolderACLItem(finalFolderAclItemList);
				return finalFolderAclItemList;
			}
			return null;
		}
	}

	/**
	 * 取得文件夹的权限列表，用在保存前，预览用
	 * 
	 * @param folderGuid
	 * @param parentFolderAclItemList
	 * @return
	 */
	private List<FolderACLItem> listCurrentFolderACLItemForTemp(String folderGuid, List<FolderACLItem> parentFolderAclItemList) throws ServiceRequestException
	{
		// 取得当前文件夹非继承的权限列表
		UpperKeyMap filter = new UpperKeyMap();
		filter.put(FolderACLItem.FOLDER_GUID, folderGuid);
		filter.put(FolderACLItem.IS_EXTEND, "N");
		List<FolderACLItem> aclItemList = DataServer.getSystemDataService().listFromCache(FolderACLItem.class, new FieldValueEqualsFilter<>(filter));

		// 把父阶权限条目增加到当前文件夹权限条目里，作为保存前显示使用
		this.addParentACLItemToCurrentForTempShow(aclItemList, parentFolderAclItemList);

		return aclItemList;
	}

	/**
	 * 把父阶权限条目增加到当前文件夹权限条目里，作为保存前显示使用
	 * 
	 * @param currentFolderAclItemList
	 * @param parentFolderAclItemList
	 */
	private void addParentACLItemToCurrentForTempShow(List<FolderACLItem> currentFolderAclItemList, List<FolderACLItem> parentFolderAclItemList)
	{
		if (currentFolderAclItemList == null)
		{
			currentFolderAclItemList = new ArrayList<>();
		}
		if (!SetUtils.isNullList(parentFolderAclItemList))
		{
			for (FolderACLItem parentAcl : parentFolderAclItemList)
			{
				FolderACLItem aclItem = new FolderACLItem();
				aclItem.putAll(parentAcl);
				aclItem.setIsExtend(true);
				currentFolderAclItemList.add(aclItem);
			}
		}
	}

	/**
	 * 判断当前用户组角色对当前文件夹是否有指定的权限
	 * 
	 * @param folderGuid
	 * @param folderAuthorityEnum
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @return
	 * @throws DynaDataException
	 */
	public boolean hasFolderAuthority(String folderGuid, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		FolderACLItem aclItem = this.getFolderAuthority(folderGuid, userGuid, groupGuid, roleGuid);
		String authority = (String) aclItem.get(folderAuthorityEnum.getDbKey());
		if (StringUtils.isNullString(authority))
		{
			return false;
		}
		else
		{
			return !"2".equalsIgnoreCase(authority);
		}
	}

	/**
	 * 取得指定文件夹的所有权限设置
	 * 
	 * @param folderGuid
	 * @return
	 * @throws DynaDataException
	 */
	public List<FolderACLItem> listFolderAuth(String folderGuid) throws ServiceRequestException
	{
		return this.listFolderAuth(folderGuid, true);
	}

	/**
	 * 取得指定文件夹的权限列表
	 * 
	 * @param folderGuid
	 *            指定的文件夹
	 * @param withParentACL
	 *            是否在权限条目里包含父文件夹的权限
	 * @return
	 * @throws DynaDataException
	 */
	public List<FolderACLItem> listFolderAuth(String folderGuid, boolean withParentACL) throws ServiceRequestException
	{
		Folder folder = DataServer.getFolderService().getFolder(folderGuid, null, false);
		if (folder.getType() == FolderTypeEnum.LIBRARY)
		{
			return null;
		}
		String parentFolderGuid = folder.getParentFolder();

		// 取得当前文件夹的权限列表
		List<FolderACLItem> currentFolderAclItemList = this.listFolderACLOnly(folder, false);

		// 取得当前文件夹父文件夹的权限列表
		if (withParentACL && !SetUtils.isNullList(currentFolderAclItemList))
		{
			List<FolderACLItem> parentFolderAclItemList = new ArrayList<>();
			if (StringUtils.isGuid(parentFolderGuid))
			{
				parentFolderAclItemList = this.listFolderAuth(parentFolderGuid, false);
			}

			// 合并
			this.merge(currentFolderAclItemList, parentFolderAclItemList, true);
		}

		if (!SetUtils.isNullList(currentFolderAclItemList))
		{
			currentFolderAclItemList.sort(Comparator.comparing(AbstractACLItem::getPrecedence));
		}

		return currentFolderAclItemList;
	}

	private List<FolderACLItem> listFolderACLOnly(Folder folder, boolean withParentACL) throws ServiceRequestException
	{
		List<FolderACLItem> folderAclItemList = DataServer.getSystemDataService().listFromCache(FolderACLItem.class,
				new FieldValueEqualsFilter<>(FolderACLItem.FOLDER_GUID, folder.getGuid()));
		if (!SetUtils.isNullList(folderAclItemList))
		{
			List<FolderACLItem> folderAclItemInheritList = new ArrayList<>();
			for (Iterator<FolderACLItem> it = folderAclItemList.iterator(); it.hasNext();)
			{
				FolderACLItem folderAclItem = it.next();
				if (folderAclItem.isExtend())
				{
					folderAclItemInheritList.add(folderAclItem);
					it.remove();
				}
			}

			this.merge(folderAclItemList, folderAclItemInheritList, withParentACL);
		}
		return folderAclItemList;
	}

	/**
	 * 取得指定文件夹的权限
	 * 
	 * @param folderGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @return
	 * @throws DynaDataException
	 */
	public FolderACLItem getFolderAuthority(String folderGuid, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		Folder folder = DataServer.getFolderService().getFolder(folderGuid, userGuid, groupGuid, roleGuid, false);
		if (folder == null)
		{
			return new FolderACLItem(PermissibleEnum.NO);
		}
		if (folder.getType() == FolderTypeEnum.PRIVATE && folder.getOwnerUserGuid().equals(userGuid))
		{
			return new FolderACLItem(PermissibleEnum.GRANTED);
		}

		FolderACLItem aclItem = this.getACLItem(folder, userGuid, groupGuid, roleGuid);
		if (aclItem == null)
		{
			return new FolderACLItem(PermissibleEnum.NO);
		}
		return aclItem;
	}

	/**
	 * 取得指定文件夹的权限
	 * 
	 * @param folder
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @return
	 */
	public FolderACLItem getACLItem(Folder folder, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		if (folder == null)
		{
			return new FolderACLItem(PermissibleEnum.NO);
		}
		if (folder.getType() == FolderTypeEnum.PRIVATE && folder.getOwnerUserGuid().equals(userGuid))
		{
			return new FolderACLItem(PermissibleEnum.GRANTED);
		}

		List<FolderACLItem> list = DataServer.getSystemDataService().listFromCache(FolderACLItem.class,
				new FieldValueEqualsFilter<>(FolderACLItem.FOLDER_GUID, folder.getGuid()));
		List<FolderACLItem> matchList = new ArrayList<>();
		if (!SetUtils.isNullList(list))
		{
			for (FolderACLItem aclItem : list)
			{
				boolean isMatch = this.isACLDetailMatch(aclItem, folder, userGuid, groupGuid, roleGuid);
				if (isMatch)
				{
					matchList.add(aclItem);
				}
			}
			this.stubService.getAuthCommonStub().setLevelData(matchList, groupGuid);
		}
		matchList.sort((o1, o2) -> {
			// 在同一个大组下面，则子组优先
			// 非集成的权限最优先
			if (o1.getPrecedence().compareTo(o2.getPrecedence()) == 0)
			{
				if (((BigDecimal) o1.get("LEVELDATA")).compareTo((BigDecimal) o2.get("LEVELDATA")) == 0)
				{
					return BooleanUtils.getBooleanStringYN(o1.isExtend()).compareTo(BooleanUtils.getBooleanStringYN(o2.isExtend()));
				}
				return ((BigDecimal) o1.get("LEVELDATA")).compareTo((BigDecimal) o2.get("LEVELDATA"));
			}
			return o1.getPrecedence().compareTo(o2.getPrecedence());
		});
		return this.merge(matchList);
	}

	/**
	 * 相同的valueGuid，把父权限合并到当前文件夹权限里
	 * 
	 * @param currentFolderAclItemList
	 * @param parentFolderAclItemList
	 */
	private void merge(List<FolderACLItem> currentFolderAclItemList, List<FolderACLItem> parentFolderAclItemList, boolean withParentACL)
	{
		if (!SetUtils.isNullList(currentFolderAclItemList))
		{
			for (FolderACLItem current : currentFolderAclItemList)
			{
				if (!SetUtils.isNullList(parentFolderAclItemList))
				{
					for (Iterator<FolderACLItem> it = parentFolderAclItemList.iterator(); it.hasNext();)
					{
						FolderACLItem parent = it.next();
						if ((current.getAccessType() == AccessTypeEnum.OTHERS && parent.getAccessType() == AccessTypeEnum.OTHERS)
								|| (current.getAccessType() == AccessTypeEnum.OWNER && parent.getAccessType() == AccessTypeEnum.OWNER)
								|| (!StringUtils.isNullString(current.getValueGuid()) && current.getValueGuid().equals(parent.getValueGuid())))
						{
							current.setIsExtend(true);
							current.setOperAddref(StringUtils.isNullString(current.getOperAddref()) ? parent.getOperAddref() : current.getOperAddref());
							current.setOperCreate(StringUtils.isNullString(current.getOperCreate()) ? parent.getOperCreate() : current.getOperCreate());
							current.setOperDelete(StringUtils.isNullString(current.getOperDelete()) ? parent.getOperDelete() : current.getOperDelete());
							current.setOperDelref(StringUtils.isNullString(current.getOperDelref()) ? parent.getOperDelref() : current.getOperDelref());
							current.setOperRead(StringUtils.isNullString(current.getOperRead()) ? parent.getOperRead() : current.getOperRead());
							current.setOperRename(StringUtils.isNullString(current.getOperRename()) ? parent.getOperRename() : current.getOperRename());
							if (withParentACL)
							{
								current.put(FolderACLItem.PARENT_OPER_READ, parent.getOperRead());
								current.put(FolderACLItem.PARENT_OPER_CREATE, parent.getOperCreate());
								current.put(FolderACLItem.PARENT_OPER_DELETE, parent.getOperDelete());
								current.put(FolderACLItem.PARENT_OPERRE_NAME, parent.getOperRename());
								current.put(FolderACLItem.PARENT_OPER_ADDREF, parent.getOperAddref());
								current.put(FolderACLItem.PARENT_OPER_DELREF, parent.getOperDelref());
							}
							it.remove();
						}
					}
				}
			}
		}

		if (!SetUtils.isNullList(parentFolderAclItemList))
		{
			currentFolderAclItemList.addAll(parentFolderAclItemList);
		}
	}

	/**
	 * 先按照AccessType和ValueGuid以及isextend进行排序，由于非继承权限的isextend=N，相同的AccessType和ValueGuid，isextend=N排在第一位
	 * 最终相同的AccessType和ValueGuid，只取第一个
	 * 
	 * @param aclItemList
	 */
	private void distinct(List<FolderACLItem> aclItemList)
	{
		if (!SetUtils.isNullList(aclItemList))
		{
			// 先按照AccessType和ValueGuid以及isextend进行排序
			aclItemList.sort(new FolderAclItemComparator());

			// 相同的AccessType和ValueGuid，只取第一个
			List<String> tempList = new ArrayList<>();
			for (Iterator<FolderACLItem> it = aclItemList.iterator(); it.hasNext();)
			{
				FolderACLItem aclItem = it.next();
				String tempKey = aclItem.getAccessType().name() + ";" + aclItem.getValueGuid();
				if (tempList.contains(tempKey))
				{
					it.remove();
				}
				else
				{
					tempList.add(tempKey);
				}
			}
		}
	}

	private FolderACLItem merge(List<FolderACLItem> itemList)
	{
		FolderACLItem aclItem = new FolderACLItem();
		aclItem.setOperRead(this.stubService.getAuthCommonStub().getAuth(FolderACLItem.OPER_READ, itemList));
		aclItem.setOperCreate(this.stubService.getAuthCommonStub().getAuth(FolderACLItem.OPER_CREATE, itemList));
		aclItem.setOperDelete(this.stubService.getAuthCommonStub().getAuth(FolderACLItem.OPER_DELETE, itemList));
		aclItem.setOperRename(this.stubService.getAuthCommonStub().getAuth(FolderACLItem.OPER_RENAME, itemList));
		aclItem.setOperAddref(this.stubService.getAuthCommonStub().getAuth(FolderACLItem.OPER_ADDREF, itemList));
		aclItem.setOperDelref(this.stubService.getAuthCommonStub().getAuth(FolderACLItem.OPER_DELREF, itemList));

		return aclItem;
	}

	/**
	 * 把from权限的非空权限合并到to权限上
	 * 
	 * @param from
	 * @param to
	 */
	private void merge(FolderACLItem from, FolderACLItem to)
	{
		this.merge(from, to, FolderACLItem.OPER_READ);
		this.merge(from, to, FolderACLItem.OPER_CREATE);
		this.merge(from, to, FolderACLItem.OPER_DELETE);
		this.merge(from, to, FolderACLItem.OPER_RENAME);
		this.merge(from, to, FolderACLItem.OPER_ADDREF);
		this.merge(from, to, FolderACLItem.OPER_DELREF);
	}

	private void merge(FolderACLItem from, FolderACLItem to, String authType)
	{
		if (!StringUtils.isNull(from.get(authType)))
		{
			to.put(authType, from.get(authType));
		}
	}

	private FolderACLItem getFolderACLItemByObjectGuid(FolderACLItem aclItem, List<FolderACLItem> aclItemList)
	{
		for (FolderACLItem aclItem_ : aclItemList)
		{
			if (aclItem.getAccessType() == AccessTypeEnum.OTHERS && aclItem_.getAccessType() == AccessTypeEnum.OTHERS)
			{
				return aclItem_;
			}
			if (aclItem.getAccessType() == AccessTypeEnum.OWNER && aclItem_.getAccessType() == AccessTypeEnum.OWNER)
			{
				return aclItem_;
			}
			if (aclItem.getValueGuid().equals(aclItem_.getValueGuid()))
			{
				return aclItem_;
			}
		}
		return null;
	}

	/**
	 * 权限明细是否匹配
	 * 
	 * @param item
	 * @param foundationObject
	 * @param sessionId
	 * @return
	 */
	private boolean isACLDetailMatch(FolderACLItem item, Folder folder, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		AccessTypeEnum accessType = item.getAccessType();
		switch (accessType)
		{
		case USER:
			return this.stubService.getAuthCommonStub().isUserMatch(userGuid, item.getValueGuid());
		case OWNER:
			return this.stubService.getAuthCommonStub().isUserMatch(userGuid, folder.getOwnerUserGuid());
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

	private void setValueName(FolderACLItem aclItem) throws ServiceRequestException
	{
		switch (aclItem.getAccessType())
		{
		case USER:
			this.setUserName(aclItem);
			break;
		case GROUP:
			this.setGroupName(aclItem);
			break;
		case ROLE:
			this.setRoleName(aclItem);
			break;
		case RIG:
			this.setRIGName(aclItem);
			break;
		default:
			break;
		}
	}

	private void setUserName(FolderACLItem aclItem) throws ServiceRequestException
	{
		User user = DataServer.getSystemDataService().get(User.class, aclItem.getValueGuid());
		if (user != null)
		{
			aclItem.setValueName(user.getUserId() + "-" + user.getUserName());
		}
	}

	private void setGroupName(FolderACLItem aclItem) throws ServiceRequestException
	{
		Group group = DataServer.getSystemDataService().get(Group.class, aclItem.getValueGuid());
		if (group != null)
		{
			aclItem.setValueName(group.getGroupId() + "-" + group.getGroupName());
		}
	}

	private void setRoleName(FolderACLItem aclItem) throws ServiceRequestException
	{
		Role role = DataServer.getSystemDataService().get(Role.class, aclItem.getValueGuid());
		if (role != null)
		{
			aclItem.setValueName(role.getRoleId() + "-" + role.getRoleName());
		}
	}

	private void setRIGName(FolderACLItem aclItem) throws ServiceRequestException
	{
		RIG rig = DataServer.getSystemDataService().get(RIG.class, aclItem.getValueGuid());
		if (rig != null)
		{
			Group group = DataServer.getSystemDataService().get(Group.class, rig.getGroupGuid());
			Role role = DataServer.getSystemDataService().get(Role.class, rig.getRoleGuid());

			String groupName = group == null ? "" : group.getGroupName();
			String roleName = role == null ? "" : role.getRoleName();

			aclItem.setValueName(groupName + "-" + roleName);
		}
	}

	/**
	 * 临时权限无效数据清空
	 * 
	 * @param aclItemList
	 */
	private void clearForTempFolderACLItem(List<FolderACLItem> aclItemList)
	{
		if (!SetUtils.isNullList(aclItemList))
		{
			for (FolderACLItem aclItem : aclItemList)
			{
				aclItem.clear("GUID");
				aclItem.clear("CREATEUSERGUID");
				aclItem.clear("CREATETIME");
				aclItem.clear("UPDATEUSERGUID");
				aclItem.clear("UPDATETIME");
			}
		}
	}

	static class FolderAclItemComparator implements Comparator<FolderACLItem>
	{
		@Override
		public int compare(FolderACLItem o1, FolderACLItem o2)
		{
			if (o1.getAccessType().name().compareTo(o2.getAccessType().name()) == 0)
			{
				if ((o1.getAccessType() == AccessTypeEnum.OTHERS && o2.getAccessType() == AccessTypeEnum.OTHERS)
						|| (o1.getAccessType() == AccessTypeEnum.OWNER && o2.getAccessType() == AccessTypeEnum.OWNER)
						|| (!StringUtils.isNullString(o1.getValueGuid()) && o1.getValueGuid().compareTo(o2.getValueGuid()) == 0))
				{
					return BooleanUtils.getBooleanStringYN(o1.isExtend()).compareTo(BooleanUtils.getBooleanStringYN(o2.isExtend()));
				}
				return o1.getValueGuid().compareTo(o2.getValueGuid());
			}
			return o1.getAccessType().name().compareTo(o2.getAccessType().name());
		}
	}
}
