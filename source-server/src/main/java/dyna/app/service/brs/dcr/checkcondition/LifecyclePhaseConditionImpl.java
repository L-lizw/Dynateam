package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public class LifecyclePhaseConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -684241088924502122L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String val = StringUtils.convertNULLtoString(this.getFoundationObject().get(SystemClassFieldEnum.LCPHASE.getName()));

		LifecyclePhaseInfo lifecyclePhaseInfo = this.getEMM().getLifecyclePhaseInfo(val);
		if (lifecyclePhaseInfo == null)
		{
			throw new ServiceRequestException("ID_APP_RULECHECK_LCPHASE_NOT_EXIST", "lifecyclephase is not exist.", null, this.getFoundationObject().getId());
		}
		int sequence = lifecyclePhaseInfo.getLifecycleSequence();

		if (this.getOperatorSign() == OperateSignEnum.EQUALS)
		{
			return sequence == this.getSequence();
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
		{
			return sequence != this.getSequence();
		}
		else if (this.getOperatorSign() == OperateSignEnum.BIGGER)
		{
			return sequence > this.getSequence();
		}
		else if (this.getOperatorSign() == OperateSignEnum.BIGGEROREQUAL)
		{
			return sequence >= this.getSequence();
		}
		else if (this.getOperatorSign() == OperateSignEnum.SMALLER)
		{
			return sequence < this.getSequence();
		}
		else if (this.getOperatorSign() == OperateSignEnum.SMALLEROREQUAL)
		{
			return sequence <= this.getSequence();
		}
		return false;
	}

	private int getSequence() throws ServiceRequestException
	{
		String val = this.getValue();
		if (StringUtils.isNullString(val))
		{
			throw new ServiceRequestException("ID_APP_RULECHECK_RULE_LCPHASE_WRONG", "rule is wrong.", null, this.getFoundationObject().getId());
		}

		ClassInfo classInfo = this.getEMM().getClassByGuid(this.getFoundationObject().getObjectGuid().getClassGuid());
		LifecyclePhaseInfo lifecyclePhaseInfo = this.getEMM().getLifecyclePhaseInfo(classInfo.getLifecycleName(), val);
		if (lifecyclePhaseInfo == null)
		{
			throw new ServiceRequestException("ID_APP_RULECHECK_LCPHASE_NOT_EXIST", "lifecyclephase is not exist.", null, this.getFoundationObject().getId());
		}

		return lifecyclePhaseInfo.getLifecycleSequence();
	}

	private EMM getEMM() throws ServiceRequestException
	{
		return this.getServiceInstance(EMM.class);
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
}
