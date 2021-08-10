package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public class ClassificationConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 867809407189220576L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String classificationGuid = this.getFoundationObject().getClassificationGuid();
		CodeItemInfo ruleClassification = this.getRuleClassification();

		if (this.getOperatorSign() == OperateSignEnum.EQUALS)
		{
			if (!StringUtils.isGuid(classificationGuid) || ruleClassification == null)
			{
				return false;
			}
			return this.isSubClassification(classificationGuid, ruleClassification.getGuid());
		}
		else if (this.getOperatorSign() == OperateSignEnum.ISNULL)
		{
			return !StringUtils.isGuid(classificationGuid);
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTNULL)
		{
			return StringUtils.isGuid(classificationGuid);
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
		{
			if (!StringUtils.isGuid(classificationGuid) || ruleClassification == null)
			{
				return false;
			}
			return !this.isSubClassification(classificationGuid, ruleClassification.getGuid());
		}

		return true;
	}

	private boolean isSubClassification(String classificationGuid, String ruleClassificationGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(ruleClassificationGuid) && StringUtils.isGuid(classificationGuid))
		{
			if (classificationGuid.equals(ruleClassificationGuid))
			{
				return true;
			}
			CodeItemInfo item = this.getEMM().getCodeItem(classificationGuid);
			if (item != null)
			{
				return this.isSubClassification(item.getParentGuid(), ruleClassificationGuid);
			}
		}
		return false;
	}

	private CodeItemInfo getRuleClassification() throws ServiceRequestException
	{
		String classGuid = this.getFoundationObject().getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.getEMM().getClassByGuid(classGuid);
		String classificationGroup = classInfo.getClassification();
		if (StringUtils.isNullString(classificationGroup))
		{
			return null;
		}

		String classification = this.getValue();
		if (!StringUtils.isNullString(classification))
		{
			return this.getEMM().getCodeItemByName(classificationGroup, classification);
		}
		return null;
	}

	@Override
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
