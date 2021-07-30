package dyna.data.service.acl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.common.dto.Session;
import dyna.common.dto.acl.PublicSearchACLItem;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.systemenum.PublicSearchAuthorityEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;

public class PublicSearchAuthStub extends DSAbstractServiceStub<AclServiceImpl>
{
	public PublicSearchAuthStub(DataServerContext serviceContext, AclServiceImpl aclService)
	{
		super(serviceContext, aclService);
	}

	public boolean hasAuthorityForPublicSearch(String publicSearchGuid, PublicSearchAuthorityEnum publicSearchAuthorityEnum, String sessionId) throws ServiceRequestException
	{
		PublicSearchACLItem aclItem = this.getAuthority(publicSearchGuid, sessionId);
		String authority = (String) aclItem.get(publicSearchAuthorityEnum.getDbKey());
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
	 * 获取权限
	 * 
	 * @param publicSearchGuid
	 * @param sessionId
	 * @return
	 */
	public PublicSearchACLItem getAuthority(String publicSearchGuid, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		List<PublicSearchACLItem> aclItemList = DataServer.getSystemDataService().listFromCache(PublicSearchACLItem.class,
				new FieldValueEqualsFilter<PublicSearchACLItem>(PublicSearchACLItem.PUBLIC_SEARCH_GUID, publicSearchGuid));
		if (!SetUtils.isNullList(aclItemList))
		{
			List<PublicSearchACLItem> matchList = new ArrayList<PublicSearchACLItem>();
			if (!SetUtils.isNullList(aclItemList))
			{
				for (PublicSearchACLItem aclItem : aclItemList)
				{
					boolean isMatch = this.isACLDetailMatch(aclItem, session.getUserGuid(), session.getLoginGroupGuid(), session.getLoginRoleGuid());
					if (isMatch)
					{
						matchList.add(aclItem);
					}
				}
				this.stubService.getAuthCommonStub().setLevelData(matchList, session.getLoginGroupGuid());

				Collections.sort(matchList, new Comparator<PublicSearchACLItem>() {

					@Override
					public int compare(PublicSearchACLItem o1, PublicSearchACLItem o2)
					{
						if (o1.getPrecedence().compareTo(o2.getPrecedence()) == 0)
						{
							return ((BigDecimal) o1.get("LEVELDATA")).compareTo((BigDecimal) o2.get("LEVELDATA"));
						}
						return o1.getPrecedence().compareTo(o2.getPrecedence());
					}
				});

				return this.merge(matchList);
			}
		}
		return new PublicSearchACLItem(PermissibleEnum.NO);
	}

	/**
	 * 权限合并
	 * 
	 * @param itemList
	 * @return
	 */
	private PublicSearchACLItem merge(List<PublicSearchACLItem> itemList)
	{
		PublicSearchACLItem aclItem = new PublicSearchACLItem();
		aclItem.setOperRead(this.stubService.getAuthCommonStub().getAuth(PublicSearchACLItem.PUBLIC_OPER_READ, itemList));
		aclItem.setOperUpdate(this.stubService.getAuthCommonStub().getAuth(PublicSearchACLItem.PUBLIC_OPER_UPDATE, itemList));
		aclItem.setOperDelete(this.stubService.getAuthCommonStub().getAuth(PublicSearchACLItem.PUBLIC_OPER_DELETE, itemList));

		return aclItem;
	}

	/**
	 * 权限明细是否匹配
	 * 
	 * @param item
	 * @param foundationObject
	 * @param sessionId
	 * @return
	 */
	private boolean isACLDetailMatch(PublicSearchACLItem item, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		AccessTypeEnum accessType = item.getAccessType();
		switch (accessType)
		{
		case USER:
			return this.stubService.getAuthCommonStub().isUserMatch(userGuid, item.getValueGuid());
		case OWNER:
			return this.stubService.getAuthCommonStub().isUserMatch(userGuid, item.getValueGuid());
		case RIG:
			return this.stubService.getAuthCommonStub().isRIGMatch(roleGuid, groupGuid, item.getValueGuid());
		case ROLE:
			return this.stubService.getAuthCommonStub().isRoleMatch(roleGuid, item.getValueGuid());
		case GROUP:
			return this.stubService.getAuthCommonStub().isGroupMatch(groupGuid, item.getValueGuid());
		case OTHERS:
			return true;
		default:
			return false;
		}
	}
}
