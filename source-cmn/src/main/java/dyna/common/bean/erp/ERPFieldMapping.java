/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYTField
 * wangweixia 2012-5-2
 */
package dyna.common.bean.erp;


/**
 * @author wangweixia
 * 
 */
public class ERPFieldMapping
{
	/**
	 * PLM属性字段 必填
	 */
	private String					PLMField				= null;
	/**
	 * ERP对应字段 必填
	 */
	private String					ERPField				= null;
	/**
	 * 默认值
	 */
	private String					defaultValue			= "";
	/**
	 * 描述
	 */
	private String					description				= "";
	/**
	 * 字段最大长度(以字符计，0表示没有限制)
	 */
	private int						maxLength				= 0;

	/**
	 * 当数据长度超过最大长度后，是否截断
	 */
	private boolean					truncWhenLengthExceeds	= false;
	/**
	 * 数据截断方向，true：从左向右；false：从右向左
	 */
	private boolean					LengthExceedsOrientationisRight	= true;
	/**
	 * 字段是否是必填的
	 */
	private boolean					isMandatory				= false;
	/**
	 * 字段是否为主键(向ERP数据的时候，ERP应该有自己的主键逻辑，不能依赖PLM提供的主键来更新数据；在PLM从ERP读取数据时，根据此主键设置来更新PLM数据)
	 */
	private boolean					isKeyColumn				= false;
	
	/**
	 * 当抛转空bom时，是否不显示任何值
	 */
	private boolean                 isShowDefault           = false;
	
	/**
	 * 小数精度（小数点后最大位数，超出截断）
	 */
	private int						precision				= 0;
	/**
	 * ERPField的数据类型，目前支持：string(默认),number, date, boolean <br/>
	 * 包含time，具体是在每个ERP定义自己的日期时间格式
	 * 
	 */
	private ERPFieldMappingTypeEnum	dataType				= ERPFieldMappingTypeEnum.STRING;

	/**
	 * 日期的格式，
	 */
	private String					dateFormat;

	public String getPLMField()
	{
		return PLMField;
	}

	public void setPLMField(String pLMField)
	{
		PLMField = pLMField;
	}

	public String getERPField()
	{
		return ERPField;
	}

	public void setERPField(String eRPField)
	{
		ERPField = eRPField;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getMaxLength()
	{
		return maxLength;
	}

	public void setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
	}

	public boolean isTruncWhenLengthExceeds()
	{
		return truncWhenLengthExceeds;
	}

	public void setTruncWhenLengthExceeds(boolean truncWhenLengthExceeds)
	{
		this.truncWhenLengthExceeds = truncWhenLengthExceeds;
	}

	public boolean isLengthExceedsOrientationisRight()
	{
		return LengthExceedsOrientationisRight;
	}

	public void setLengthExceedsOrientationisRight(boolean lengthExceedsOrientationisLeft)
	{
		LengthExceedsOrientationisRight = lengthExceedsOrientationisLeft;
	}

	public int getPrecision()
	{
		return precision;
	}

	public void setPrecision(int precision)
	{
		this.precision = precision;
	}

	public boolean isMandatory()
	{
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory)
	{
		this.isMandatory = isMandatory;
	}

	public boolean isKeyColumn()
	{
		return isKeyColumn;
	}

	public void setKeyColumn(boolean isKeyColumn)
	{
		this.isKeyColumn = isKeyColumn;
	}
	
	public boolean isShowDefault() 
	{
		return isShowDefault;
	}

	public void setShowDefault(boolean isShowDefault) 
	{
		this.isShowDefault = isShowDefault;
	}

	public ERPFieldMappingTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(ERPFieldMappingTypeEnum dataType)
	{
		this.dataType = dataType;
	}

	public String getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	/**
	 * 判断字段映射是否没有配置<br/>
	 * 如果PLMField为空且defaultValue也为空则返回true
	 * 
	 * @return
	 */
	public boolean isNull()
	{
		boolean noDefaultValue = "".equals(this.getDefaultValue());
		boolean noPLMField = "".equals(this.getPLMField());
		return noDefaultValue && noPLMField;
	}
}
