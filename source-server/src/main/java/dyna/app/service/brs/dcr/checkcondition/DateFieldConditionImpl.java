package dyna.app.service.brs.dcr.checkcondition;

import java.text.ParseException;
import java.util.Date;

import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;

public class DateFieldConditionImpl extends AbstractFieldCondition
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5874773699153608989L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		if (getDateValue() == null)
		{
			return true;
		}
		else
		{
			String fieldName = this.getFieldName();
			Date date = (Date) getFoundationObject().get(fieldName);
			if (this.getOperatorSign() == OperateSignEnum.EQUALS)
			{
				return DateFormat.compareDate(date, getDateValue()) == 0;
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
			{
				return DateFormat.compareDate(date, getDateValue()) != 0;
			}
			else if (this.getOperatorSign() == OperateSignEnum.EARLIER)
			{
				return DateFormat.compareDate(date, getDateValue()) < 0;
			}
			else if (this.getOperatorSign() == OperateSignEnum.LATER)
			{
				return DateFormat.compareDate(date, getDateValue()) > 0;
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTEARLIER)
			{
				return DateFormat.compareDate(date, getDateValue()) >= 0;
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTLATER)
			{
				return DateFormat.compareDate(date, getDateValue()) <= 0;
			}
		}
		return true;
	}

	private Date getDateValue() throws ServiceRequestException
	{
		try
		{
			return DateFormat.formatStringToDateYMD(StringUtils.convertNULLtoString(getValue()));
		}
		catch (ParseException e)
		{
			throw new ServiceRequestException("[DateFieldConditionImpl.getDateValue()]: illegal date");
		}
	}

}
