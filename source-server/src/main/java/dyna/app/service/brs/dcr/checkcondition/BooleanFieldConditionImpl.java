package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

public class BooleanFieldConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3699664310254311222L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		Object o = this.getFoundationObject().get(this.getFieldName());
		Boolean b = BooleanUtils.getBooleanByYN(StringUtils.convertNULLtoString(o));

		if (super.getOperatorSign() == OperateSignEnum.YES || super.getOperatorSign() == OperateSignEnum.TRUE)
		{
			return b == true;
		}
		else
		{
			return b != true;
		}
	}
}
