package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SystemClassFieldEnum;

public class FieldConditionFactory
{
	public static AbstractFieldCondition getFieldCondition(String fieldName, FieldTypeEnum fieldType, OperateSignEnum operator, String value)
	{
		AbstractFieldCondition fieldCondition = null;
		if (fieldName.equalsIgnoreCase("$PreviewImg"))
		{
			// 预览图
			fieldCondition = buildPreviewImgFieldConditionImpl();
		}
		else if (fieldName.equalsIgnoreCase("$File"))
		{
			// 文件
			fieldCondition = buildFileFieldConditionImpl();
		}
		// 条件为其他规则时${ruleid}
		else if (fieldName.matches("^\\$\\{\\w+\\}$"))
		{
			fieldCondition = buildRuleCheckFieldConditionImpl();
			String ruleId = fieldName.substring(2, fieldName.length() - 2);
			((RuleCheckConditionImpl) fieldCondition).setRuleId(ruleId);
		}
		else if (fieldName.equals(SystemClassFieldEnum.CLASSIFICATION.getName()))
		{
			fieldCondition = buildClassificationConditionImpl();
		}
		else if (fieldName.equals(SystemClassFieldEnum.LCPHASE.getName()))
		{
			fieldCondition = buildLifecyclePhaseConditionImpl();
		}
		else if (fieldType == FieldTypeEnum.BOOLEAN)
		{
			fieldCondition = buildBooleanFieldConditionImpl();
		}
		else if (fieldType == FieldTypeEnum.FLOAT)
		{
			fieldCondition = buildDoubleFieldConditionImpl();
		}
		else if (fieldType == FieldTypeEnum.OBJECT)
		{
			fieldCondition = buildObjectGuidFieldConditionImpl();
		}
		else if (fieldType == FieldTypeEnum.CODE)
		{
			fieldCondition = buildCodeFieldConditionImpl();
		}
		else if (fieldType == FieldTypeEnum.MULTICODE)
		{
			fieldCondition = buildMultiCodeFieldConditionImpl();
		}
		else if (fieldType == FieldTypeEnum.DATE || fieldType == FieldTypeEnum.DATETIME)
		{
			fieldCondition = buildDateFieldConditionImpl();
		}
		else
		{
			fieldCondition = buildStringFieldConditionImpl();
		}

		fieldCondition.setFieldName(fieldName);
		fieldCondition.setOperatorSign(operator);
		fieldCondition.setValue(value);

		return fieldCondition;
	}

	private static CodeFieldConditionImpl buildCodeFieldConditionImpl()
	{
		return new CodeFieldConditionImpl();
	}

	private static MultiCodeFieldConditionImpl buildMultiCodeFieldConditionImpl()
	{
		return new MultiCodeFieldConditionImpl();
	}

	private static StringFieldConditionImpl buildStringFieldConditionImpl()
	{
		return new StringFieldConditionImpl();
	}

	private static BooleanFieldConditionImpl buildBooleanFieldConditionImpl()
	{
		return new BooleanFieldConditionImpl();
	}

	private static DoubleFieldConditionImpl buildDoubleFieldConditionImpl()
	{
		return new DoubleFieldConditionImpl();
	}

	private static ObjectGuidFieldConditionImpl buildObjectGuidFieldConditionImpl()
	{
		return new ObjectGuidFieldConditionImpl();
	}

	private static PreviewImgConditionImpl buildPreviewImgFieldConditionImpl()
	{
		return new PreviewImgConditionImpl();
	}

	private static FileConditionImpl buildFileFieldConditionImpl()
	{
		return new FileConditionImpl();
	}

	private static RuleCheckConditionImpl buildRuleCheckFieldConditionImpl()
	{
		return new RuleCheckConditionImpl();
	}

	private static LifecyclePhaseConditionImpl buildLifecyclePhaseConditionImpl()
	{
		return new LifecyclePhaseConditionImpl();
	}

	private static ClassificationConditionImpl buildClassificationConditionImpl()
	{
		return new ClassificationConditionImpl();
	}

	private static DateFieldConditionImpl buildDateFieldConditionImpl()
	{
		return new DateFieldConditionImpl();
	}
}
