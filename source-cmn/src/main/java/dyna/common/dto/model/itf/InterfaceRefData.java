package dyna.common.dto.model.itf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.itf.InterfaceRefDataMapper;

@Cache
@EntryMapper(InterfaceRefDataMapper.class)
public class InterfaceRefData extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6466503937286798878L;

	public static final String	PARENTGUID			= "PARENTGUID";

	public static final String	INTERFACEGUID		= "INTERFACEGUID";

	public static final String	CREATE_USER_GUID	= "CREATEUSERGUID";

	public static final String	UPDATE_USER_GUID	= "UPDATEUSERGUID";

	public static final String	CREATE_TIME			= "CREATETIME";

	public static final String	UPDATE_TIME			= "UPDATETIME";

	public String getParentGuid()
	{
		return (String) this.get(PARENTGUID);
	}

	public void setParentGuid(String parentGuid)
	{
		this.put(PARENTGUID, parentGuid);
	}

	public String getInterfaceGuid()
	{
		return (String) this.get(INTERFACEGUID);
	}

	public void setInterfaceGuid(String interfaceGuid)
	{
		this.put(INTERFACEGUID, interfaceGuid);
	}
}
