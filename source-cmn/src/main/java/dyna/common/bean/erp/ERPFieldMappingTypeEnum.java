/**
 * chega
 * 2013-1-9下午3:54:13
 */
package dyna.common.bean.erp;

import dyna.common.util.DateFormat;

public enum ERPFieldMappingTypeEnum
{
	STRING("string"), NUMBER("number"), DATE("date"), BOOLEAN("boolean");

	private String	type;

	public String getType()
	{
		return type;
	}

	private ERPFieldMappingTypeEnum(String type)
	{
		this.type = type;
	}

	public static ERPFieldMappingTypeEnum getEnumByType(String type)
	{
		for (ERPFieldMappingTypeEnum e : ERPFieldMappingTypeEnum.values())
		{
			if (e.getType().equalsIgnoreCase(type))
			{
				return e;
			}
		}
		throw new IllegalArgumentException("no enum const found in " + ERPFieldMappingTypeEnum.class.getCanonicalName() + ":" + type);
	}

	/**
	 * 验证数据格式 <br/>
	 * string date boolean类型不需要判断值（因为值就是按照相应的类型取出来的，不必要再进行验证格式）
	 * 
	 * @param value
	 * @param field
	 * @return
	 */
	public boolean validate(Object value, ERPFieldMapping field, String booleanConvert)
	{
		boolean result = false;
		switch (this)
		{
		case STRING:
			result = true;
			break;
		case NUMBER:
//			验证number类型时，不区分整数,小数
			try
			{
				Double.parseDouble((String) value);
				result = true;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException(value + " doesn't match type: " + this.type);
			}
			break;
		case DATE:
			// 判断是否符合指定的日期格式
			if ("null".equalsIgnoreCase(field.getDateFormat()))
			{
				result = ((String) value).matches("\\d+");
			}
			else
			{
				result = DateFormat.parse((String) value, field.getDateFormat()) == null ? false : true;

			}
			break;
		case BOOLEAN:
			// 判断是否为boolean类型
			result = ((String) value).matches(booleanConvert);
			break;
		default:
			result = false;
			break;
		}
		return result;
	}
}
