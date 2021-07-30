/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.dto.model.cls;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

@Cache
public class NumberingObjectInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID			= -8069925747893489637L;

	public static final String	NUMBERRULEFK				= "NUMBERRULEFK";

	public static final String	DEFAULT_START_VALUE			= "1";

	public static final String	SEQUENCE					= "DATASEQ";

	public static final String	TYPE						= "ITEMTYPE";

	public static final String	VALUE						= "ITEMVALUE";

	public static final String	FIELDTYPE					= "FIELDTYPE";

	public static final String	FIELDCLASSNAME				= "FIELDCLASSNAME";

	public static final String	PARENTGUID					= "PARENTGUID";

	public static final String	STARTVALUE					= "STARTVALUE";

	public static final String	ISDATEBYUSER				= "ISDATEBYUSER";

	public static final String	PREFIX						= "PREFIX";

	public static final String	SUFFIX						= "SUFFIX";

	public static final String	FIELDLENGTH					= "FIELDLENGTH";

	public static final String	CONTROLLEDNUMBERFIELDGUID	= "CONTROLLEDNUMBERFIELDGUID";

	public static final String	FIELDNAME					= "FIELDNAME";

	@Override
	public String getName()
	{
		return (String) this.get(FIELDNAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(FIELDNAME, name);
	}

	public String getNumberRuleFK()
	{
		return (String) this.get(NUMBERRULEFK);
	}

	public void setNumberRuleFK(String numberRuleFK)
	{
		this.put(NUMBERRULEFK, numberRuleFK);
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return (String) this.get(PARENTGUID);
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.put(PARENTGUID, parentGuid);
	}

	/**
	 * @return the typevalue
	 */
	public String getTypevalue()
	{
		return (String) this.get(VALUE);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public String getType()
	{
		return (String) this.get(TYPE);
	}

	/**
	 * @param typevalue
	 *            the typevalue to set
	 */
	public void setTypevalue(String typevalue)
	{
		this.put(VALUE, typevalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public NumberingObjectInfo clone()
	{
		return (NumberingObjectInfo) super.clone();
	}

	/**
	 * @param fieldClassName
	 *            the fieldClassName to set
	 */
	public void setFieldClassName(String fieldClassName)
	{
		this.put(FIELDCLASSNAME, fieldClassName);
	}

	/**
	 * @return the fieldClassName
	 */
	public String getFieldClassName()
	{
		return (String) this.get(FIELDCLASSNAME);
	}

	/**
	 * @return the startValue
	 */
	public String getStartValue()
	{
		return this.get(STARTVALUE) == null ? null : (String) this.get(STARTVALUE);
	}

	/**
	 * @param startValue
	 *            the startValue to set
	 */
	public void setStartValue(String startValue)
	{
		this.put(STARTVALUE, startValue);
	}

	public String getFieldType()
	{
		return (String) this.get(FIELDTYPE);
	}

	public void setFieldType(String fieldType)
	{
		this.put(FIELDTYPE, fieldType);
	}

	public String getFieldLength()
	{
		return this.get(FIELDLENGTH) == null ? null : ((BigDecimal) this.get(FIELDLENGTH)).toString();
	}

	public void setFieldLength(String fieldLength)
	{
		if (!StringUtils.isNullString(fieldLength))
		{
			this.put(FIELDLENGTH, new BigDecimal(fieldLength));
		}
	}

	public String getPrefix()
	{
		return (String) this.get(PREFIX);
	}

	public void setPrefix(String prefix)
	{
		this.put(PREFIX, prefix);
	}

	public String getSuffix()
	{
		return (String) this.get(SUFFIX);
	}

	public void setSuffix(String suffix)
	{
		this.put(SUFFIX, suffix);
	}

	public String getSerialField()
	{
		return (String) this.get(CONTROLLEDNUMBERFIELDGUID);
	}

	public void setSerialField(String serialField)
	{
		this.put(CONTROLLEDNUMBERFIELDGUID, serialField);
	}

	public boolean isDatebyUser()
	{
		return this.get(ISDATEBYUSER) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISDATEBYUSER));
	}

	public void setDatebyUser(boolean isDatebyUser)
	{
		this.put(ISDATEBYUSER, BooleanUtils.getBooleanStringYN(isDatebyUser));
	}

	public CFMCodeRuleEnum getFieldTypeEnum()
	{
		return CFMCodeRuleEnum.typeValueOf(this.getFieldType());
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((BigDecimal) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}
}
