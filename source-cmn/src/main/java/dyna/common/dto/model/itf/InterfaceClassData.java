package dyna.common.dto.model.itf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.itf.InterfaceClassDataMapper;

@Cache
@EntryMapper(InterfaceClassDataMapper.class)
public class InterfaceClassData extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7232995682392002582L;

	public static final String	INTERFACEGUID		= "INTERFACEGUID";

	public static final String	CLASSGUID			= "CLASSGUID";

	public static final String	CREATE_USER_GUID	= "CREATEUSERGUID";

	public static final String	UPDATE_USER_GUID	= "UPDATEUSERGUID";

	public static final String	CREATE_TIME			= "CREATETIME";

	public static final String	UPDATE_TIME			= "UPDATETIME";

	public String getInterfaceGuid()
	{
		return (String) this.get(INTERFACEGUID);
	}

	public void setInterfaceGuid(String interfaceGuid)
	{
		this.put(INTERFACEGUID, interfaceGuid);
	}

	public String getClassGuid()
	{
		return (String) this.get(CLASSGUID);
	}

	public void setClassGuid(String classGuid)
	{
		this.put(CLASSGUID, classGuid);
	}
}
