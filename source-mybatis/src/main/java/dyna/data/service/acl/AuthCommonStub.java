package dyna.data.service.acl;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.acl.AbstractACLItem;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthCommonStub extends DSAbstractServiceStub<AclServiceImpl>
{
	/**
	 * 在同一个大组下面，则子组优先
	 * 
	 * @param matchList
	 * @param groupGuid
	 */
	public void setLevelData(List<? extends AbstractACLItem> matchList, String groupGuid) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(matchList))
		{
			List<Group> groupList = new ArrayList<>();
			this.setGroupLevel(groupList, groupGuid, 1);

			for (AbstractACLItem aclItem : matchList)
			{
				if (aclItem.getAccessType() != AccessTypeEnum.GROUP)
				{
					aclItem.put("LEVELDATA", new BigDecimal("1"));
				}
				else
				{
					for (Group group : groupList)
					{
						if (aclItem.getValueGuid().equals(group.getGuid()))
						{
							aclItem.put("LEVELDATA", group.get("LEVELDATA"));
						}
					}
				}
			}
		}
	}

	public void decodeObjectGuid(ObjectGuid objectGuid) throws ServiceRequestException
	{
		String className = objectGuid.getClassName();
		String classGuid = objectGuid.getClassGuid();
		if (StringUtils.isNullString(className) && StringUtils.isGuid(classGuid))
		{
			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(objectGuid.getClassGuid());
			className = classObject == null ? null : classObject.getName();
		}
		else if (!StringUtils.isNullString(className) && !StringUtils.isGuid(classGuid))
		{
			ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
			classGuid = classObject == null ? null : classObject.getGuid();
		}
		objectGuid.setClassGuid(classGuid);
		objectGuid.setClassName(className);
	}

	/**
	 * 从下往上设置group的LEVELDATA
	 * 
	 * @param groupList
	 * @param groupGuid
	 * @param level
	 */
	private void setGroupLevel(List<Group> groupList, String groupGuid, Integer level) throws ServiceRequestException
	{
		Group group = DataServer.getSystemDataService().get(Group.class, groupGuid);
		if (group != null)
		{
			group.put("LEVELDATA", new BigDecimal(level.toString()));
			groupList.add(group);
			if (StringUtils.isGuid(group.getParentGuid()))
			{
				this.setGroupLevel(groupList, group.getParentGuid(), level++);
			}
		}
	}

	/**
	 * 从list中获取指定权限，权限为空，则遍历list，直到找到非空权限，若最终权限为空，则返回"2"
	 * 
	 * @param authType
	 * @param itemList
	 * @return
	 */
	public String getAuth(String authType, List<? extends AbstractACLItem> itemList)
	{
		if (!SetUtils.isNullList(itemList))
		{
			for (AbstractACLItem aclItem : itemList)
			{
				if (!StringUtils.isNullString((String) aclItem.get(authType)))
				{
					return (String) aclItem.get(authType);
				}
			}
		}
		return "2";
	}

	/**
	 * 状态匹配
	 * 
	 * @param status
	 * @param value
	 * @return
	 */
	public boolean isStatusMatch(String status, String value)
	{
		return status.equals(value);
	}

	/**
	 * 所有者组匹配
	 * 
	 * @param groupGuid
	 * @param value
	 * @return
	 */
	public boolean isGroupMatch(String groupGuid, String value) throws ServiceRequestException
	{
		while (StringUtils.isGuid(groupGuid))
		{
			if (groupGuid.equals(value))
			{
				return true;
			}

			Group group = DataServer.getSystemDataService().get(Group.class, groupGuid);
			if (group == null)
			{
				break;
			}
			groupGuid = group.getParentGuid();
		}
		return false;
	}

	/**
	 * 分类匹配
	 * 
	 * @param classificationGuid
	 * @param value
	 * @return
	 */
	public boolean isClassificationMatch(String classificationGuid, String value) throws ServiceRequestException
	{
		while (StringUtils.isGuid(classificationGuid))
		{
			if (classificationGuid.equals(value))
			{
				return true;
			}

			CodeItemInfo classificationItemInfo = DataServer.getSystemDataService().get(CodeItemInfo.class, classificationGuid);
			if (classificationItemInfo == null)
			{
				break;
			}
			classificationGuid = classificationItemInfo.getParentGuid();
		}
		return false;
	}

	/**
	 * 类匹配
	 * 
	 * @param classGuid
	 * @param value
	 * @return
	 */
	public boolean isClassMatch(String classGuid, String value) throws ServiceRequestException
	{
		while (StringUtils.isGuid(classGuid))
		{
			if (classGuid.equals(value))
			{
				return true;
			}

			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);
			if (classObject == null)
			{
				break;
			}
			String superclass = classObject.getSuperclass();
			if (!StringUtils.isNullString(superclass))
			{
				ClassObject superClassObject = DataServer.getClassModelService().getClassObject(superclass);
				if (superClassObject != null)
				{
					classGuid = superClassObject.getGuid();
				}
			}
			else
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * 生命周期匹配
	 * 
	 * @param lifecyclePhaseGuid
	 * @param value
	 * @return
	 */
	public boolean isLifecyclePhaseMatch(String lifecyclePhaseGuid, String value)
	{
		return lifecyclePhaseGuid.equals(value);
	}

	/**
	 * 版本匹配
	 * 
	 * @param revisionId
	 * @param value
	 * @return
	 */
	public boolean isRevisionMatch(String revisionId, String value)
	{
		return revisionId.equals(value);
	}

	/**
	 * 用户匹配
	 * 
	 * @param userGuid
	 * @param value
	 * @return
	 */
	public boolean isUserMatch(String userGuid, String value)
	{
		return StringUtils.isGuid(userGuid) && userGuid.equals(value);
	}

	/**
	 * 组角色匹配
	 * 
	 * @param userGuid
	 * @param value
	 * @return
	 */
	public boolean isRIGMatch(String roleGuid, String groupGuid, String value) throws ServiceRequestException
	{
		UpperKeyMap param = new UpperKeyMap();
		param.put(RIG.GROUP_GUID, groupGuid);
		param.put(RIG.ROLE_GUID, roleGuid);
		List<RIG> rigList = DataServer.getSystemDataService().listFromCache(RIG.class, new FieldValueEqualsFilter<>(param));
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				Role role = DataServer.getSystemDataService().get(Role.class, rig.getRoleGuid());
				Group group =  DataServer.getSystemDataService().get(Group.class, rig.getGroupGuid());
				if (role.isActive() && group.isActive() && rig.getGuid().equals(value))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 角色匹配
	 * 
	 * @param roleGuid
	 * @param groupGuid
	 * @param value
	 * @return
	 */
	public boolean isRoleMatch(String roleGuid, String value)
	{
		return StringUtils.isGuid(roleGuid) && roleGuid.equals(value);
	}
}
