package dyna.common.dto.acl;

import java.math.BigDecimal;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.AccessTypeEnum;

public abstract class AbstractACLItem extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7893952561697189036L;

	public static final String	MASTER_FK			= "MASTERFK";

	public static final String	OBJECT_TYPE			= "OBJECTTYPE";

	public static final String	OBJECT_GUID			= "OBJECTGUID";

	public static final String	OBJECT_NAME			= "OBJECTNAME";

	public static final String	PRECEDENCE			= "PRECEDENCE";

	public String getMasterGuid()
	{
		return (String) this.get(MASTER_FK);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTER_FK, masterGuid);
	}

	public String getValueGuid()
	{
		return (String) this.get(OBJECT_GUID);
	}

	public void setValueGuid(String guid)
	{
		this.put(OBJECT_GUID, guid);
	}

	public String getValueName()
	{
		return (String) this.get(OBJECT_NAME);
	}

	public void setValueName(String name)
	{
		this.put(OBJECT_NAME, name);
	}

	public String getPrecedence()
	{
		return ((Number) this.get(PRECEDENCE)).toString();
	}

	public void setPrecedence(String precedence)
	{
		this.put(PRECEDENCE, new BigDecimal(precedence));
	}

	public AccessTypeEnum getAccessType()
	{
		return AccessTypeEnum.valueOf((String) this.get(OBJECT_TYPE));
	}

	public void setAccessType(AccessTypeEnum type)
	{
		this.put(OBJECT_TYPE, type.name());
		this.put(PRECEDENCE, BigDecimal.valueOf(type.getPrecedence()));
	}
}
