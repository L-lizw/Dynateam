package dyna.app.service.brs.dcr.checkrule;

import java.io.Serializable;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public abstract class AbstractRule extends AbstractCondition implements Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 6448124854949061993L;

	private ClassCondition			end1Condition;

	private List<ClassCondition>	end2ConditionList;

	private String					ruleName;

	private String					ruleId;

	private String					exceptionMessage;

	private RuleTypeEnum			ruleType;

	public ClassCondition getEnd1Condition()
	{
		return end1Condition;
	}

	public void setEnd1Condition(ClassCondition end1Condition)
	{
		this.end1Condition = end1Condition;
	}

	public List<ClassCondition> getEnd2ConditionList()
	{
		return this.end2ConditionList;
	}

	public void setEnd2ConditionList(List<ClassCondition> end2ConditionList)
	{
		this.end2ConditionList = end2ConditionList;
	}

	public String getRuleName()
	{
		return ruleName;
	}

	public void setRuleName(String ruleName)
	{
		this.ruleName = ruleName;
	}

	public RuleTypeEnum getRuleType()
	{
		return ruleType;
	}

	public void setRuleType(RuleTypeEnum ruleType)
	{
		this.ruleType = ruleType;
	}

	public String getExceptionMessage()
	{
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage)
	{
		this.exceptionMessage = exceptionMessage;
	}

	public String getRuleId()
	{
		return ruleId;
	}

	public void setRuleId(String ruleId)
	{
		this.ruleId = ruleId;
	}

	/**
	 * 判断foundationObject是不是classCondition指定的类或者其子类
	 * 
	 * @param condition
	 * @param foundationObject
	 * @return
	 */
	protected boolean isCheck(ClassCondition classCondition, FoundationObject foundationObject) throws ServiceRequestException
	{
		String foClassName = foundationObject.getObjectGuid().getClassName();
		if (foClassName.equals(classCondition.getClassName()))
		{
			return true;
		}
		ClassInfo foClassInfo = super.getServiceInstance(EMM.class).getClassByName(foClassName);
		while (!StringUtils.isNullString(foClassInfo.getSuperclass()))
		{
			foClassInfo = super.getServiceInstance(EMM.class).getClassByName(foClassInfo.getSuperclass());
			if (foClassInfo.getName().equals(classCondition.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
}
