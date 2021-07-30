/**
 * chega
 * 2013-1-5下午4:29:20
 */
package dyna.common.bean.erp;

import java.util.Arrays;
import java.util.List;


public class ERPRestriction
{
	private String						field;
	private List<String>				valueList;
	private ERPRestrictionOperatorEnum	operator;
	private List<String>				categoryList;
	private boolean						skipOnFail	= false;

	public ERPRestriction(String field, String value, ERPRestrictionOperatorEnum operator, String category, boolean skipOnFail)
	{
		this.field = field;
		this.valueList = Arrays.asList(value.split(","));
		this.operator = operator;
		this.categoryList = Arrays.asList(category.split(","));
		this.skipOnFail = skipOnFail;
	}

	/**
	 * 验证是否符合规则
	 * @param value
	 * @return
	 */
	public boolean validate(String value)
	{
		return this.operator.validate(value, this.valueList);
	}

	public String getField()
	{
		return field;
	}

	public List<String> getValueList()
	{
		return valueList;
	}

	public ERPRestrictionOperatorEnum getOperator()
	{
		return operator;
	}

	public List<String> getCategoryList()
	{
		return categoryList;
	}

	public boolean isSkipOnFail()
	{
		return skipOnFail;
	}

	@Override
	public String toString()
	{
		return "Restriction(field: " + this.field + " " + operator.getSign() + " " + this.valueList.toString().substring(1, this.valueList.toString().length() - 1) + ")";
	}
}
