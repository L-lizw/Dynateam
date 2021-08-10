package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.StringUtils;

public class StringFieldConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -194548853290278502L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String fieldName = this.getFieldName();
		String val = StringUtils.convertNULLtoString(this.getFoundationObject().get(fieldName));
		if (this.getOperatorSign() == OperateSignEnum.EQUALS)
		{
			return val.equals(this.getValue());
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
		{
			return !val.equals(this.getValue());
		}
		else if (this.getOperatorSign() == OperateSignEnum.CONTAIN)
		{
			return val.indexOf(this.getValue()) != -1;
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTCONTAIN)
		{
			return val.indexOf(this.getValue()) == -1;
		}
		else if (this.getOperatorSign() == OperateSignEnum.STARTWITH)
		{
			return val.startsWith(this.getValue());
		}
		else if (this.getOperatorSign() == OperateSignEnum.ENDWITH)
		{
			return val.endsWith(this.getValue());
		}
		else if (this.getOperatorSign() == OperateSignEnum.ISNULL)
		{
			return StringUtils.isNullString(val);
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTNULL)
		{
			return !StringUtils.isNullString(val);
		}

		return true;
	}

	public String getValue() throws ServiceRequestException
	{
		Object o = super.getValue();
		if (o == null)
		{
			return StringUtils.EMPTY_STRING;
		}
		return o.toString();
	}
}
