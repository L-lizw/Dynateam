package dyna.common.bean.data.configparamter;

import java.util.Map;

import dyna.common.bean.data.SystemObject;

/**
 * 分组(G表)
 * 
 * @author wwx
 * 
 */
public class TableOfGroup extends ConfigTableBase implements SystemObject
{
	private static final long	serialVersionUID	= 741177795259781144L;
	// G番号
	public static final String	GNUMBER				= "GNUMBER";

	public static final String	SN					= "SN";

	public static final String	CLASSFIELDNAME		= "FIELDNAME";

	public static final String	CLASSFIELDGUID		= "FIELDGUID";
	public static final String	DESCRIPTION			= "DESCRIPTION";

	public String getGNumber()
	{
		return (String) this.get(GNUMBER);
	}

	public void setGNumber(String gNumber)
	{
		this.put(GNUMBER, gNumber);
	}
	
	public String getSN()
	{
		return (String) this.get(SN);
	}

	public void setSN(String sn)
	{
		this.put(SN, sn);
	}

	public void setClassFieldGuid(String classFieldGuid)
	{
		this.put(CLASSFIELDGUID, classFieldGuid);
	}

	public String getClassFieldGuid()
	{
		return (String) this.get(CLASSFIELDGUID);
	}

	public void setClassFieldName(String classFieldName)
	{
		this.put(CLASSFIELDNAME, classFieldName);
	}

	public String getClassFieldName()
	{
		return (String) this.get(CLASSFIELDNAME);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfGroup result = new TableOfGroup();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}
}
