package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.NumberUtils;

public class DoubleFieldConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5916470308468820022L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		Object o = this.getFoundationObject().get(this.getFieldName());
		Double d = Double.valueOf(o.toString());
		if (this.getOperatorSign() == OperateSignEnum.EQUALS)
		{
			return d == this.getValue();
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
		{
			return d != this.getValue();
		}
		else if (this.getOperatorSign() == OperateSignEnum.BIGGER)
		{
			return d > this.getValue();
		}
		else if (this.getOperatorSign() == OperateSignEnum.BIGGEROREQUAL)
		{
			return d >= this.getValue();
		}
		else if (this.getOperatorSign() == OperateSignEnum.SMALLER)
		{
			return d < this.getValue();
		}
		else if (this.getOperatorSign() == OperateSignEnum.SMALLEROREQUAL)
		{
			return d <= this.getValue();
		}

		return true;
	}

	public Double getValue() throws ServiceRequestException
	{
		Object o = super.getValue();
		if (o == null)
		{
			return 0d;
		}

		if (NumberUtils.isNumeric(o.toString()))
		{
			return NumberUtils.getFloat(o, false);
		}
		throw new ServiceRequestException("ID_APP_RULECHECK_RULE_DOUBLE_WRONG", "rule is wrong", null, this.getFoundationObject().getId(), o.toString());
	}
}
