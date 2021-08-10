package dyna.app.service.brs.boas.numbering.classification.strategy;

import java.util.HashMap;
import java.util.Map;

import dyna.app.service.brs.boas.numbering.ClassificationAllocate;
import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.app.service.brs.boas.numbering.classification.FieldStratery;
import dyna.app.service.brs.boas.numbering.classification.Strategy;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EMM;

public class FieldObjectStrategy extends FieldStratery
{
	private static final String defaultTypeValue = "ID$";

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{

		// if (StringUtils.isNullString(parameter.field.getTypeValue()))
		// {
		// return null;
		// }

		String subGuid = (String) this.getValue(parameter.classificationMap, parameter.field, parameter.item, parameter.field.getFieldName());
		String subClassGuid = (String) this.getValue(parameter.classificationMap, parameter.field, parameter.item, parameter.field.getFieldName() + "$CLASS");

		if (StringUtils.isNullString(subGuid))
		{
			return null;
		}

		EMM emm = this.getService(parameter.dataAccessService, EMM.class);
		ClassInfo subClassInfo = null;
		if (parameter.numberClassField.getTypeValue() != null)
		{
			subClassInfo = emm.getClassByName(parameter.numberClassField.getTypeValue());
		}
		else
		{
			subClassInfo = emm.getClassByGuid(subClassGuid);
		}

		if (subClassInfo == null)
		{
			return null;
		}

		String subTypeValue = "";
		if (StringUtils.isNullString(parameter.field.getTypeValue()))
		{
			subTypeValue = defaultTypeValue;
		}
		else
		{
			subTypeValue = parameter.field.getTypeValue();
		}

		if (subClassInfo != null)
		{
			if (subClassInfo.hasInterface(ModelInterfaceEnum.IGroup))
			{
				AAS aas = this.getService(parameter.dataAccessService, AAS.class);
				Group group = aas.getGroup(subGuid);
				if (group != null)
				{
					if (subTypeValue.contains("ID"))
					{
						return group.getGroupId();
					}
					else
					{
						return group.getGroupName();
					}
				}
				return null;
			}
			else if (subClassInfo.hasInterface(ModelInterfaceEnum.IUser))
			{
				AAS aas = this.getService(parameter.dataAccessService, AAS.class);
				User user = aas.getUser(subGuid);
				if (user != null)
				{
					if (subTypeValue.contains("ID"))
					{
						return user.getUserId();
					}
					else
					{
						return user.getUserName();
					}
				}
				return null;
			}
		}

		int indexOf = subTypeValue.indexOf("*");
		String fieldName1 = subTypeValue.trim();
		String fieldName2 = null;
		if (indexOf >= 0)
		{
			fieldName1 = subTypeValue.substring(0, indexOf);
			if (fieldName1 == null)
			{
				fieldName1 = defaultTypeValue;
			}
			else
			{
				fieldName2 = parameter.field.getTypeValue().substring(indexOf + 1);
			}
		}
		// 取值采用默认值时用fileName1，用自定义设置的值时用fieldName2
		ClassField subClassField = emm.getFieldByName(subClassInfo.getName(), fieldName1.trim(), true);
		ObjectGuid objectGuid = new ObjectGuid(subClassInfo.getGuid(), null, subGuid, null);
		BOAS boas = this.getService(parameter.dataAccessService, BOAS.class);
		FoundationObject subFoundation = boas.getObject(objectGuid);

		Strategy strategy = ClassificationAllocate.getStrategy(parameter.field, subClassField);
		if (strategy == null)
		{
			return null;
		}

		Map<String, FoundationObject> subClassificationMap = new HashMap<String, FoundationObject>();
		subClassificationMap.put(ClassificationAllocate.TARGET_FOUNDATION, subFoundation);

		ClassificationNumberField makeSubNumberField = this.makeSubNumberField(parameter.field, subClassField, fieldName2);

		AllocateParameter makeParameter = ClassificationAllocate.makeParameter(subClassificationMap, makeSubNumberField, parameter.item, subClassField, parameter.dataAccessService,
				null, null, null, parameter.isCreate);
		strategy.run(makeParameter);

		return makeParameter.field.getNumberFieldLastValue();
	}

	private ClassificationNumberField makeSubNumberField(ClassificationNumberField numberField, ClassField subClassField, String subTypeValue)
	{
		ClassificationNumberField clone = (ClassificationNumberField) numberField.clone();
		if (subTypeValue != null)
		{
			subTypeValue = subTypeValue.trim();
		}

		clone.setFieldName(subClassField.getName());
		clone.setFormClass(true);
		clone.setTypeValue(subTypeValue);
		clone.setFieldlength(0);
		clone.setPrefix(null);
		clone.setSuffix(null);

		return clone;
	}

}
