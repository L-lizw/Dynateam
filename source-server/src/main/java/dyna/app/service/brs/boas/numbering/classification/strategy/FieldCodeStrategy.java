package dyna.app.service.brs.boas.numbering.classification.strategy;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.app.service.brs.boas.numbering.classification.FieldStratery;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.coding.CodeTypeEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public class FieldCodeStrategy extends FieldStratery
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{

		String value = (String) this.getValue(parameter.classificationMap, parameter.field, parameter.item,
				parameter.field.getFieldName());
		return this.getCodeValue(value, parameter.field, parameter.dataAccessService, parameter.item);
	}

	protected String getCodeValue(String codeItemGuid, ClassificationNumberField field,
			DataAccessService dataAccessService, ClassficationFeatureItem item) throws ServiceRequestException
	{
		EMM emm = this.getService(dataAccessService, EMM.class);

		if (StringUtils.isNullString(codeItemGuid))
		{
			return null;
		}
		CodeItemInfo codeItem = emm.getCodeItem(codeItemGuid);
		if (codeItem == null)
		{
			return null;
		}

		// REPEAT字段特殊处理
		if (SystemClassFieldEnum.REPEAT.getName().equalsIgnoreCase(item.getFieldName()))
		{
			return codeItem.getGuid();
		}

		if (CodeTypeEnum.TITLE_ZH_CN.toString().equalsIgnoreCase(field.getTypeValue()))
		{
			return codeItem.getTitle(LanguageEnum.ZH_CN);
		}
		else if (CodeTypeEnum.TITLE_ZH_TW.toString().equalsIgnoreCase(field.getTypeValue()))
		{
			return codeItem.getTitle(LanguageEnum.ZH_TW);
		}
		else if (CodeTypeEnum.TITLE_EN.toString().equalsIgnoreCase(field.getTypeValue()))
		{
			return codeItem.getTitle(LanguageEnum.EN);
		}
		else if (CodeTypeEnum.TITLE.toString().equalsIgnoreCase(field.getTypeValue()))
		{
			return codeItem.getTitle(((EMMImpl) emm).getUserSignature().getLanguageEnum());
		}
		else if (CodeTypeEnum.CODE.toString().equalsIgnoreCase(field.getTypeValue()))
		{
			return codeItem.getCode();
		}
		else if ("NAME".equalsIgnoreCase(field.getTypeValue()))
		{
			return codeItem.getName();
		}
		else
		{
			return codeItem.getCode();
		}
	}

}
