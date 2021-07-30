package dyna.common.dto.model.itf;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

@Cache
public class InterfaceData extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1232415051715869976L;

	public static final String	INTERFACENAME		= "INTERFACENAME";

	public static final String	CREATE_USER_GUID	= "CREATEUSERGUID";

	public static final String	UPDATE_USER_GUID	= "UPDATEUSERGUID";

	public static final String	CREATE_TIME			= "CREATETIME";

	public static final String	UPDATE_TIME			= "UPDATETIME";

	public String getName()
	{
		return this.getInterfaceName();
	}

	public void setName(String name)
	{
		this.setInterfaceName(name);
	}

	public String getInterfaceName()
	{
		return (String) this.get(INTERFACENAME);
	}

	public void setInterfaceName(String name)
	{
		this.put(INTERFACENAME, name);
	}
}
