package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public class CodeFieldConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -194548853290278502L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String val = StringUtils.convertNULLtoString(this.getFoundationObject().get(this.getFieldName()));
		if (this.getOperatorSign() == OperateSignEnum.ISNULL)
		{
			return StringUtils.isNullString(val);
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTNULL)
		{
			return !StringUtils.isNullString(val);
		}
		else
		{
			CodeItemInfo codeItemInfo = this.getEMM().getCodeItem(val);
			if (codeItemInfo == null)
			{
				return false;
			}

			if (this.getOperatorSign() == OperateSignEnum.EQUALS)
			{
				return codeItemInfo.getName().equals(this.getValue());
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
			{
				return !codeItemInfo.getName().equals(this.getValue());
			}
			else if (this.getOperatorSign() == OperateSignEnum.CONTAIN)
			{
				return codeItemInfo.getName().indexOf(this.getValue()) != -1;
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTCONTAIN)
			{
				return codeItemInfo.getName().indexOf(this.getValue()) == -1;
			}
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

	private EMM getEMM() throws ServiceRequestException
	{
		return this.getServiceInstance(EMM.class);
	}
}
