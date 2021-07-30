package dyna.common.bean.data.coding;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class ClassificationNumberTrans extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID			= 1L;
	public static final String	CLASSIFICATIONFEATUREITEM	= "CLASSIFICATIONFEATUREITEM";
	public static final String	CONSENGVALUES				= "CONSENGVALUES";
	public static final String	TRANSNUMBER					= "TRANSNUMBER";
	public static final String	FIELDNAME					= "FIELDNAME";

	public long getTransNumber()
	{
		return ((Number) this.get(TRANSNUMBER)).longValue();
	}

	public void setTransNumber(long transNumber)
	{
		this.put(TRANSNUMBER, transNumber);
	}

	public String getConsengValues()
	{
		return (String) this.get(CONSENGVALUES);
	}

	public void setConsengValues(String consengValues)
	{
		this.put(CONSENGVALUES, consengValues);
	}

	public String getClassificationFeatureItem()
	{
		return (String) this.get(CLASSIFICATIONFEATUREITEM);
	}

	public void setClassificationFeatureItem(String classificationFeatureItem)
	{
		this.put(CLASSIFICATIONFEATUREITEM, classificationFeatureItem);
	}

	public String getFieldName()
	{
		return (String) this.get(FIELDNAME);
	}

	public void setFieldName(String fieldName)
	{
		this.put(FIELDNAME, fieldName);
	}
}
