package dyna.common.bean.data.trans;

import java.util.List;

import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.trans.TransParamFromType;

public class TransformSignParam extends SystemObjectImpl
{

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 6564841658649248759L;

	public static String					SIGN_GUID			= "SIGNGUID";

	public static String					NAME				= "PARAMNAME";
	public static String					DATA_NAME			= "DATENAME";
	public static String					SOURCE				= "SOURCE";

	public static String					SEAL_NAME			= "SEALNAME";
	public static String					VALUE				= "ITEMVALUE";
	public static String					VALUE_DATE			= "DATEVALUE";

	public static String					IS_DELETE			= "ISDELETE";

	private List<TransformSignWFMap>		wfMapList			= null;
	private List<TransformSignObjectMap>	objectMapList		= null;

	public Object getValue()
	{
		return this.get(VALUE);
	}

	public void setValue(Object value)
	{
		this.put(VALUE, value);
	}

	public Object getDateValue()
	{
		return this.get(VALUE_DATE);
	}

	public void setDateValue(Object value)
	{
		this.put(VALUE_DATE, value);
	}

	public String getSealName()
	{
		return (String) this.get(SEAL_NAME);
	}

	public void setSealName(String sealName)
	{
		this.put(SEAL_NAME, sealName);
	}

	public String getSignGuid()
	{
		return (String) this.get(SIGN_GUID);
	}

	public void setSignGuid(String signGuid)
	{
		this.put(SIGN_GUID, signGuid);
	}

	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public String getDateName()
	{
		return (String) this.get(DATA_NAME);
	}

	public void setDateName(String name)
	{
		this.put(DATA_NAME, name);
	}

	public String getSource()
	{
		return (String) this.get(SOURCE);
	}

	public void setSource(String from)
	{
		this.put(SOURCE, from);
	}

	public TransParamFromType getSourceEnum()
	{
		return TransParamFromType.typeValueOf((String) this.get(SOURCE));
	}

	public boolean isDelete()
	{
		if (this.get(IS_DELETE) == null)
		{
			return false;
		}
		return (Boolean) this.get(IS_DELETE);
	}

	public void setDelete(boolean isDelete)
	{
		this.put(IS_DELETE, isDelete);
	}

	public List<TransformSignWFMap> getWfMapList()
	{
		return this.wfMapList;
	}

	public void setWfMapList(List<TransformSignWFMap> wfMapList)
	{
		this.wfMapList = wfMapList;
	}

	public List<TransformSignObjectMap> getObjectMapList()
	{
		return this.objectMapList;
	}

	public void setObjectMapList(List<TransformSignObjectMap> objectMapList)
	{
		this.objectMapList = objectMapList;
	}

}
