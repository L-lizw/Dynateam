package dyna.app.service.brs.dcr.checkrule;

import dyna.common.systemenum.RuleTypeEnum;

public class CheckRuleFactory
{
	public static AbstractRule createRule(RuleTypeEnum ruleType)
	{
		if (ruleType == RuleTypeEnum.BOM || ruleType == RuleTypeEnum.RELATION || ruleType == RuleTypeEnum.OBJECTFIELD)
		{
			return new RelationRule();
		}
		else if (ruleType == RuleTypeEnum.WF)
		{
			return new WFRule();
		}
		else if (ruleType == RuleTypeEnum.ERP)
		{
			return new ERPRule();
		}
		return null;
	}
}
