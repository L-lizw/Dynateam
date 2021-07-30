package dyna.common.bean.data.trans;

import dyna.common.bean.data.SystemObjectImpl;

public class TransformFieldMapping extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID		= -276122475458760406L;

	public static String		TRANSFORM_CONFIG_GUID	= "TRANSFORMCONFIGGUID";
	public static String		SOURCE_FIELDNAME		= "SOURCEFIELDNAME";
	public static String		TARGET_FIELDNAME		= "TARGETFIELDNAME";

	public static String		DEFULT_VALUE			= "DEFULTVALUE";

	public static String		IS_DELETE				= "ISDELETE";

	public String getTransformConfigGuid()
	{
		return (String) this.get(TRANSFORM_CONFIG_GUID);
	}

	public void setTransformConfigGuid(String transformConfigGuid)
	{
		this.put(TRANSFORM_CONFIG_GUID, transformConfigGuid);
	}

	public String getSourceFieldName()
	{
		return (String) this.get(SOURCE_FIELDNAME);
	}

	public void setSourceFieldName(String sourceFieldName)
	{
		this.put(SOURCE_FIELDNAME, sourceFieldName);
	}

	public String getTargetFieldName()
	{
		return (String) this.get(TARGET_FIELDNAME);
	}

	public void setTargetFieldName(String targetFieldName)
	{
		this.put(TARGET_FIELDNAME, targetFieldName);
	}

	public String getDefultValue()
	{
		return (String) this.get(DEFULT_VALUE);
	}

	public void setDefultValue(String defultValue)
	{
		this.put(DEFULT_VALUE, defultValue);
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
