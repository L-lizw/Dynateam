package dyna.common.bean.data.trans;

import dyna.common.bean.data.SystemObjectImpl;

public class TransformSignObjectMap extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6564841658649248759L;

	public static String		SIGN_PARAM_GUID		= "SIGNPARAMGUID";

	public static String		CLASSGUID			= "CLASSGUID";

	public static String		FIELDNAME			= "FIELDNAME";

	public static String		IS_DELETE			= "ISDELETE";

	public String getSignParamGuid()
	{
		return (String) this.get(SIGN_PARAM_GUID);
	}

	public void setSignParamGuid(String signParamGuid)
	{
		this.put(SIGN_PARAM_GUID, signParamGuid);
	}

	public String getClassName()
	{
		return (String) this.get("CLASSNAME");
	}

	public void setClassName(String className)
	{
		this.put("CLASSNAME", className);
	}

	public String getClassGuid()
	{
		return (String) this.get(CLASSGUID);
	}

	public void setClassGuid(String classGuid)
	{
		this.put(CLASSGUID, classGuid);
	}

	public String getFieldName()
	{
		return (String) this.get(FIELDNAME);
	}

	public void setFieldName(String fieldName)
	{
		this.put(FIELDNAME, fieldName);
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
}
