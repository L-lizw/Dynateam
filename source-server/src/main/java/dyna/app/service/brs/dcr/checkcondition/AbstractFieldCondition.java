package dyna.app.service.brs.dcr.checkcondition;

import java.io.Serializable;

import dyna.app.service.brs.dcr.checkrule.AbstractCondition;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;

public abstract class AbstractFieldCondition extends AbstractCondition implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7334319168861161067L;

	private String				fieldName;

	private OperateSignEnum		operatorSign;

	private Object				value;

	private boolean				isClassification;

	private String				exceptionMessage;

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public OperateSignEnum getOperatorSign()
	{
		return operatorSign;
	}

	public void setOperatorSign(OperateSignEnum operatorSign)
	{
		this.operatorSign = operatorSign;
	}

	public Object getValue() throws ServiceRequestException
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public boolean isClassification()
	{
		return isClassification;
	}

	public void setClassification(boolean isClassification)
	{
		this.isClassification = isClassification;
	}

	public String getExceptionMessage()
	{
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage)
	{
		this.exceptionMessage = exceptionMessage;
	}
}
