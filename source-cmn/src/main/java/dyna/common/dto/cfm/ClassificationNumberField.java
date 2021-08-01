package dyna.common.dto.cfm;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dtomapper.cfm.ClassificationNumberFieldMapper;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.BooleanUtils;

import java.math.BigDecimal;

@Cache
@EntryMapper(ClassificationNumberFieldMapper.class)
public class ClassificationNumberField extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long				serialVersionUID			= 1L;
	public final static String				CFEATUREITEMGUID			= "CLFNUMBERREGULARGUID";
	// 字段名
	public final static String				FIELDNAME					= "FIELDNAME";
	// 字段顺序
	public final static String				SEQUENCE					= "DATASEQ";
	// 字段类型
	public final static String				TYPE						= "FIELDTYPE";
	// 开始值
	// public final static String VALUE = "VALUE";
	// 字段长度
	public final static String				FIELDLENGTH					= "FIELDLENGTH";
	// 是否是类上的字段。Y：是类上的字段；N：是分类上的字段
	public final static String				FIELDSOURCE					= "ISCLASSFIELD";
	// 前分割符
	public static final String				PREFIX						= "PREFIX";
	// 后分割符
	public static final String				SUFFIX						= "SUFFIX";
	// 受控码段
	public static final String				CONTROLLED_NUMBERFIELD_GUID	= "CONTROLLEDNUMBERFIELDGUID";
	// 是否用户指定时间
	public static final String				IS_DATE_BYUSER				= "ISDATEBYUSER";
	// 验证表达式
	public static final String				VALIDATE					= "VALIDATE";
	// 值类型 或者 开始值 ，值
	public static final String				TYPEVALUE					= "TYPEVALUE";

	// 生成编码时使用
	private transient ThreadLocal<Boolean>	hasAllocate					= new ThreadLocal<Boolean>();
	private transient ThreadLocal<String>	numberFieldValue			= new ThreadLocal<String>();
	private transient ThreadLocal<String>	numberFieldLastValue		= new ThreadLocal<String>();
	private transient ThreadLocal<Boolean>	isUpdateSerial				= new ThreadLocal<Boolean>();

	private ClassField						classField					= null;

	public void clearAllocate()
	{
		this.hasAllocate.set(false);
		this.numberFieldValue.set(null);
		this.numberFieldLastValue.set(null);
		this.isUpdateSerial.set(true);
	}

	/**
	 * @return the typeValue
	 */
	public String getTypeValue()
	{
		return (String) super.get(TYPEVALUE);
	}

	/**
	 * @param typeValue
	 *            the typeValue to set
	 */
	public void setTypeValue(String typeValue)
	{
		super.put(TYPEVALUE, typeValue);
	}

	public Boolean isFormClass()
	{
		if (this.get(FIELDSOURCE) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(FIELDSOURCE));
	}

	public void setFormClass(boolean isFromClass)
	{
		this.put(FIELDSOURCE, BooleanUtils.getBooleanStringYN(isFromClass));
	}

	/**
	 * @return the prefix
	 */
	public String getValidate()
	{
		return (String) super.get(VALIDATE);
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setValidate(String validate)
	{
		super.put(VALIDATE, validate);
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix()
	{
		return (String) super.get(PREFIX);
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix)
	{
		super.put(PREFIX, prefix);
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix()
	{
		return (String) super.get(SUFFIX);
	}

	/**
	 * @param suffix
	 *            the suffix to set
	 */
	public void setSuffix(String suffix)
	{
		super.put(SUFFIX, suffix);
	}

	/**
	 * @return the controlledSegmentGuid
	 */
	public String getControlledNumberFieldGuid()
	{
		return (String) super.get(CONTROLLED_NUMBERFIELD_GUID);
	}

	/**
	 * @param controlledSegmentGuid
	 *            the controlledSegmentGuid to set
	 */
	public void setControlledNumberFieldGuid(String controlledNumberFieldGuid)
	{
		super.put(CONTROLLED_NUMBERFIELD_GUID, controlledNumberFieldGuid);
	}

	public Boolean isDateByUser()
	{
		if (this.get(IS_DATE_BYUSER) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_DATE_BYUSER));
	}

	public Integer getFieldlength()
	{
		Number b = (Number) this.get(FIELDLENGTH);
		return b == null ? 0 : b.intValue();
	}

	public void setFieldlength(int fieldlength)
	{
		super.put(FIELDLENGTH, new BigDecimal(fieldlength));
	}

	public Integer getSequence()
	{
		Number b = (Number) this.get(SEQUENCE);
		return b == null ? 0 : b.intValue();
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(int sequence)
	{
		super.put(SEQUENCE, new BigDecimal(sequence));
	}

	public String getCFeatureItemGuid()
	{
		return (String) super.get(CFEATUREITEMGUID);
	}

	public String getFieldName()
	{
		return (String) super.get(FIELDNAME);
	}

	// public String getValue()
	// {
	// return (String) super.get(VALUE);
	// }

	public CFMCodeRuleEnum getType()
	{
		Object object = super.get(TYPE);
		if (object == null)
		{
			return null;
		}
		return CFMCodeRuleEnum.typeValueOf((String) object);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(CFMCodeRuleEnum type)
	{
		super.put(TYPE, type.getType());
	}

	public void setCFeatureItemGuid(String featureItemGuid)
	{
		super.put(CFEATUREITEMGUID, featureItemGuid);
	}

	public void setFieldName(String fieldName)
	{
		super.put(FIELDNAME, fieldName);
	}

	// public void setValue(String value)
	// {
	// super.put(VALUE, value);
	// }

	public boolean isHasAllocate()
	{
		Boolean boolean1 = this.hasAllocate.get();
		return boolean1 == null ? false : boolean1;
	}

	public void setHasAllocate(boolean hasAllocate)
	{
		this.hasAllocate.set(hasAllocate);
	}

	public String getNumberFieldValue()
	{
		return this.numberFieldValue.get();
	}

	public void setNumberFieldValue(String numberFieldValue)
	{
		this.numberFieldValue.set(numberFieldValue);
	}

	public String getNumberFieldLastValue()
	{
		return this.numberFieldLastValue.get();
	}

	public void setNumberFieldLastValue(String numberFieldLastValue)
	{
		this.numberFieldLastValue.set(numberFieldLastValue);
	}

	public ClassField getClassField()
	{
		return this.classField;
	}

	public void setClassField(ClassField classField)
	{
		this.classField = classField;
	}

	public boolean isUpdateSerial()
	{
		Boolean boolean1 = this.isUpdateSerial.get();
		return boolean1 == null ? true : boolean1;
	}

	public void setUpdateSerial(boolean isUpdateSerial)
	{
		this.isUpdateSerial.set(isUpdateSerial);
	}

}
