package dyna.app.service.brs.boas.numbering.classification.strategy;

import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.app.service.brs.boas.numbering.classification.FieldStratery;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class FieldStringStrategy extends FieldStratery
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{
		if (parameter.field.isFormClass())
		{
			// 判断规则中的字段是否需要合成,如果需要且还末合成，则先合成
			ClassficationFeatureItem existFeatureItem = null;
			if (!SetUtils.isNullList(parameter.classficationFeatureItemList))
			{
				for (ClassficationFeatureItem featureItem : parameter.classficationFeatureItemList)
				{
					if (featureItem.getFieldName().equalsIgnoreCase(parameter.field.getFieldName()))
					{
						existFeatureItem = featureItem;
						if (!featureItem.isHasAllocate())
						{
							parameter.control.allocateNumberItem(parameter.dataAccessService, featureItem, parameter.classificationMap, parameter.classficationFeatureItemList,
									parameter.isCreate, true);
						}
						break;
					}
				}
			}

			if (existFeatureItem != null)
			{
				if (existFeatureItem.isNeedAllocate())
				{
					parameter.item.setNeedAllocate(existFeatureItem.isNeedAllocate());
				}
				return StringUtils.convertNULLtoString(existFeatureItem.getFieldValue());
			}
			else
			{
				// FoundationObject foundationObject = parameter.classificationMap
				// .get(ClassificationAllocate.TARGET_FOUNDATION);
				// return (String) foundationObject.get(parameter.field.getFieldName());

				return StringUtils.convertNULLtoString(this.getValue(parameter.classificationMap, parameter.field, parameter.item, parameter.field.getFieldName()));
			}
		}
		else
		{
			if (parameter.numberClassField != null)
			{
				// FoundationObject foundationObject = parameter.classificationMap.get(parameter.item
				// .getClassificationItemGuid());
				// return (String) foundationObject.get(parameter.field.getFieldName());
				Object value = this.getValue(parameter.classificationMap, parameter.field, parameter.item, parameter.field.getFieldName());
				return StringUtils.convertNULLtoString(value);

			}
			else
			{
				return StringUtils.EMPTY_STRING;
			}

			// return parameter.classificationMap.get(parameter.item.getClassificationItemGuid());
		}

	}
}