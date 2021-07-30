/**
 * chega
 * 2013-1-5下午5:49:05
 */
package dyna.common.bean.erp;

import java.util.List;

public enum ERPRestrictionOperatorEnum
{
	EQUALS("="), NOTEQUALS("!="), IN("in"), NOTIN("!in");

	private String	sign;

	private ERPRestrictionOperatorEnum(String sign)
	{
		this.sign = sign;
	}

	public String getSign()
	{
		return sign;
	}
	
	public boolean validate(String value, List<String> valueList)
	{
		if (valueList.size() < 1)
		{
			throw new IllegalArgumentException("compared value must not be null");
		}
		boolean result = false;
		switch (this)
		{
		case EQUALS:
			result = value.equalsIgnoreCase(valueList.get(0));
			break;
		case NOTEQUALS:
			result = !value.equalsIgnoreCase(valueList.get(0));
			break;
		case IN:
			result = valueList.contains(value);
			break;
		case NOTIN:
			result = !valueList.contains(value);
			break;
		default:
			result = false;
			break;
		}
		return result;
	}

	public static ERPRestrictionOperatorEnum getOperatorBySign(String sign)
	{
		for (ERPRestrictionOperatorEnum e : ERPRestrictionOperatorEnum.values())
		{
			if (e.getSign().equalsIgnoreCase(sign))
			{
				return e;
			}
		}
		throw new IllegalArgumentException("no enum const found in dyna.common.bean.data.system.ERPRestrictionOperatorEnum." + sign);
	}

}
