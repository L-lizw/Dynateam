package dyna.app.service.brs.boas.numbering.classification.strategy;

import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;

public class FieldMultiCodeStrategy extends FieldCodeStrategy
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{
		String value = (String) this.getValue(parameter.classificationMap, parameter.field, parameter.item,
				parameter.field.getFieldName());

		String[] codeGuidList = StringUtils.splitStringWithDelimiter(";", value);
		String str = "";

		if (codeGuidList != null)
		{
			for (String codeItemGuid : codeGuidList)
			{
				str = str + StringUtils.convertNULLtoString(this.getCodeValue(codeItemGuid, parameter.field, parameter.dataAccessService, parameter.item));
			}

			return str;
		}

		return str;
	}
}
