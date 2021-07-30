/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.bean.model.cls;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.cls.NumberingObjectInfo;
import dyna.common.systemenum.NumberingTypeEnum;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.CloneUtils;

public class NumberingObject extends SystemObjectImpl implements SystemObject
{
	private static final long			serialVersionUID	= -8069925747893489637L;

	private final NumberingObjectInfo	info;

	private List<NumberingObject>		childObject			= null;

	public NumberingObject()
	{
		this.info = new NumberingObjectInfo();
	}

	public NumberingObject(NumberingObjectInfo info)
	{
		this.info = info;
	}

	public NumberingObjectInfo getInfo()
	{
		return this.info;
	}

	@Override
	public String getName()
	{
		return this.info.getName();
	}

	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	public String getNumberRuleFK()
	{
		return this.info.getNumberRuleFK();
	}

	public void setNumberRuleFK(String numberRuleFK)
	{
		this.info.setNumberRuleFK(numberRuleFK);
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return this.info.getParentGuid();
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.info.setParentGuid(parentGuid);
	}

	/**
	 * @return the type
	 */
	public NumberingTypeEnum getTypeOEnum()
	{
		return NumberingTypeEnum.typeValueOf(this.getType());
	}

	/**
	 * @return the typevalue
	 */
	public String getTypevalue()
	{
		return this.info.getTypevalue();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.info.setType(type);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public String getType()
	{
		return this.info.getType();
	}

	/**
	 * @param typevalue
	 *            the typevalue to set
	 */
	public void setTypevalue(String typevalue)
	{
		this.info.setTypevalue(typevalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public NumberingObject clone()
	{
		return CloneUtils.clone(this);
	}

	/**
	 * @param childObject
	 *            the childObject to set
	 */
	public void setChildObject(List<NumberingObject> childObject)
	{
		this.childObject = childObject;
	}

	/**
	 * @return the childObject
	 */
	public List<NumberingObject> getChildObject()
	{
		return this.childObject;
	}

	public void addChildObject(NumberingObject numberingObject)
	{
		if (this.childObject == null)
		{
			this.childObject = new ArrayList<NumberingObject>();
		}
		this.childObject.add(numberingObject);

	}

	/**
	 * @param fieldClassName
	 *            the fieldClassName to set
	 */
	public void setFieldClassName(String fieldClassName)
	{
		this.info.setFieldClassName(fieldClassName);
	}

	/**
	 * @return the fieldClassName
	 */
	public String getFieldClassName()
	{
		return this.info.getFieldClassName();
	}

	/**
	 * @return the startValue
	 */
	public String getStartValue()
	{
		return this.info.getStartValue();
	}

	/**
	 * @param startValue
	 *            the startValue to set
	 */
	public void setStartValue(String startValue)
	{
		this.info.setStartValue(startValue);
	}

	public String getFieldType()
	{
		return this.info.getFieldType();
	}

	public void setFieldType(String fieldType)
	{
		this.info.setFieldType(fieldType);
	}

	public String getFieldLength()
	{
		return this.info.getFieldLength();
	}

	public void setFieldLength(String fieldLength)
	{
		this.info.setFieldLength(fieldLength);
	}

	public String getPrefix()
	{
		return this.info.getPrefix();
	}

	public void setPrefix(String prefix)
	{
		this.info.setPrefix(prefix);
	}

	public String getSuffix()
	{
		return this.info.getSuffix();
	}

	public void setSuffix(String suffix)
	{
		this.info.setSuffix(suffix);
	}

	public String getSerialField()
	{
		return this.info.getSerialField();
	}

	public void setSerialField(String serialField)
	{
		this.info.setSerialField(serialField);
	}

	public boolean isDatebyUser()
	{
		return this.info.isDatebyUser();
	}

	public void setDatebyUser(boolean isDatebyUser)
	{
		this.info.setDatebyUser(isDatebyUser);
	}

	public CFMCodeRuleEnum getFieldTypeEnum()
	{
		return CFMCodeRuleEnum.typeValueOf(this.getFieldType());
	}

	public int getSequence()
	{
		return this.info.getSequence();
	}

	public void setSequence(Integer sequence)
	{
		this.info.setSequence(sequence);
	}
}
