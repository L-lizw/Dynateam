package dyna.common.dto.cfm;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

@Cache
public class ClassficationFeatureItemInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID		= 4615905827123016818L;
	// 类与分类关系唯一标识符
	public final static String	CLASSIFICATIONFEATTUREGUID	= "CLASSCLASSIFICATIONGUID";
	// 分类item唯一标识符
	public final static String	CLASSIFICATIONITEMGUID	= "CLASSIFICATIONITEMGUID";
	// 字段名
	public final static String	FIELDNAME				= "FIELDNAME";
	// 是否编码
	public final static String	ISNUMBERING				= "ISNUMBERING";
	// 格式化
	public final static String	FORMAT					= "FORMAT";
	// 样式
	public final static String	STYLE					= "STYLE";

	private final String		ISINHERIT				= "ISINHERIT";

	public final static String	CLASSGUID				= "CLASSGUID";

	public String getClassificationItemGuid()
	{
		return (String) super.get(CLASSIFICATIONITEMGUID);
	}

	public void setClassificationItemGuid(String classificationItemGuid)
	{
		super.put(CLASSIFICATIONITEMGUID, classificationItemGuid);
	}

	public String getFieldName()
	{
		return (String) super.get(FIELDNAME);
	}

	public void setFieldName(String fieldName)
	{
		super.put(FIELDNAME, fieldName);
	}

	public String getClassificationFeatureGuid()
	{
		return (String) super.get(CLASSIFICATIONFEATTUREGUID);
	}

	public void setClassificationFeatureGuid(String classclassificationGuid)
	{
		super.put(CLASSIFICATIONFEATTUREGUID, classclassificationGuid);
	}

	public boolean isNumbering()
	{

		if (this.get(ISNUMBERING) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(ISNUMBERING));

	}

	public String getFormat()
	{
		return (String) super.get(FORMAT);
	}

	public String getStyle()
	{
		return (String) super.get(STYLE);
	}

	public void setNumbering(boolean isNumbering)
	{
		super.put(ISNUMBERING, BooleanUtils.getBooleanStringYN(isNumbering));
	}

	public void setFormat(String format)
	{
		super.put(FORMAT, format);
	}

	public void setStyle(String style)
	{
		super.put(STYLE, style);
	}

	public boolean isInherit()
	{
		if (this.get(this.ISINHERIT) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(this.ISINHERIT));
	}

	public void setInherit(boolean isInherit)
	{
		this.put(this.ISINHERIT, BooleanUtils.getBooleanStringYN(isInherit));
	}

	public String getClassGuid()
	{
		return (String) super.get(CLASSGUID);
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		super.put(CLASSGUID, classGuid);
	}

}
