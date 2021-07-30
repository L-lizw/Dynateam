package dyna.common.dto.acl;

import dyna.common.annotation.Cache;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.systemenum.PublicSearchAuthorityEnum;
import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;

@Cache
public class PublicSearchACLItem extends AbstractACLItem
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1579570727994413321L;

	public static String		PUBLIC_SEARCH_GUID	= "PUBLICSEARCHGUID";

	// 读取
	public static String		PUBLIC_OPER_READ	= "OPERREAD";

	// 更新
	public static String		PUBLIC_OPER_UPDATE	= "OPERUPDATE";

	// 删除
	public static String		PUBLIC_OPER_DELETE	= "OPERDELETE";

	public PublicSearchACLItem()
	{
		super();
	}

	public PublicSearchACLItem(PermissibleEnum allAuthority)
	{
		super();
		this.setPublicSearchPermision(PublicSearchAuthorityEnum.READ, allAuthority);
		this.setPublicSearchPermision(PublicSearchAuthorityEnum.UPDATE, allAuthority);
		this.setPublicSearchPermision(PublicSearchAuthorityEnum.DELETE, allAuthority);
	}

	public PublicSearchACLItem(String authority)
	{
		super();
		String[] values = StringUtils.splitString(authority);
		if (values == null || values.length != PublicSearchAuthorityEnum.values().length)
		{
			return;
		}
		this.setPublicSearchPermision(PublicSearchAuthorityEnum.READ, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[0])));
		this.setPublicSearchPermision(PublicSearchAuthorityEnum.UPDATE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[1])));
		this.setPublicSearchPermision(PublicSearchAuthorityEnum.DELETE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[2])));
	}

	public String getPublicSearchGuid()
	{
		return (String) this.get(PUBLIC_SEARCH_GUID);
	}

	public void setPublicSearchGuid(String publicSearchGuid)
	{
		this.put(PUBLIC_SEARCH_GUID, publicSearchGuid);
	}

	public String getOperRead()
	{
		return (String) this.get(PUBLIC_OPER_READ);
	}

	public void setOperRead(String operRead)
	{
		this.put(PUBLIC_OPER_READ, operRead);
	}

	public String getOperUpdate()
	{
		return (String) this.get(PUBLIC_OPER_UPDATE);
	}

	public void setOperUpdate(String operUpdate)
	{
		this.put(PUBLIC_OPER_UPDATE, operUpdate);
	}

	public String getOperDelete()
	{
		return (String) this.get(PUBLIC_OPER_DELETE);
	}

	public void setOperDelete(String operDelete)
	{
		this.put(PUBLIC_OPER_DELETE, operDelete);
	}

	public void setPublicSearchPermision(PublicSearchAuthorityEnum preSearchAuthority, PermissibleEnum permission)
	{
		this.put(preSearchAuthority.getDbKey(), permission == null ? null : permission.getPriority());
	}
}
