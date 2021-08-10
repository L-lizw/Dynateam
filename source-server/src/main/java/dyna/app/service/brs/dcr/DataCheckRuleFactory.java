package dyna.app.service.brs.dcr;

import java.util.ArrayList;
import java.util.List;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.dcr.checkcondition.FieldConditionFactory;
import dyna.app.service.brs.dcr.checkrule.AbstractRule;
import dyna.app.service.brs.dcr.checkrule.CheckRuleFactory;
import dyna.app.service.brs.dcr.checkrule.ClassCondition;
import dyna.app.service.brs.dcr.checkrule.ConditionGroup;
import dyna.app.service.brs.dcr.checkrule.ERPRule;
import dyna.app.service.brs.dcr.checkrule.ICondition;
import dyna.app.service.brs.dcr.checkrule.RelationRule;
import dyna.app.service.brs.dcr.checkrule.WFRule;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.bean.data.checkrule.ClassConditionData;
import dyna.common.bean.data.checkrule.ClassConditionDetailData;
import dyna.common.bean.data.checkrule.End2CheckRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EMM;

public class DataCheckRuleFactory
{
	private static DataAccessService service;

	public static RelationRule getRule(CheckRule checkRule, FoundationObject end1, FoundationObject end2) throws ServiceRequestException
	{
		checkRule.setTempObject(end2);
		RelationRule rule = getRelationRule(checkRule);
		rule.setFoundationObject(end1, end2);
		rule.setService(service);
		return rule;
	}

	public static WFRule getRule(CheckRule checkRule, String actrtName, FoundationObject attach) throws ServiceRequestException
	{
		checkRule.setTempObject(attach);
		WFRule rule = getWFRule(checkRule);
		rule.setFoundationObject(actrtName, attach);
		rule.setService(service);
		return rule;
	}

	public static ERPRule getRule(CheckRule checkRule, FoundationObject attach) throws ServiceRequestException
	{
		ERPRule rule = getERPRule(checkRule);
		rule.setFoundationObject(attach);
		rule.setService(service);
		return rule;
	}

	public static RelationRule getRelationRule(CheckRule checkRule) throws ServiceRequestException
	{
		return (RelationRule) buildRuleData(checkRule);
	}

	public static WFRule getWFRule(CheckRule checkRule) throws ServiceRequestException
	{
		return (WFRule) buildRuleData(checkRule);
	}

	public static ERPRule getERPRule(CheckRule checkRule) throws ServiceRequestException
	{
		return (ERPRule) buildRuleData(checkRule);
	}

	private static AbstractRule buildRuleData(CheckRule checkRule) throws ServiceRequestException
	{
		AbstractRule rule = CheckRuleFactory.createRule(checkRule.getRuleType());
		rule.setRuleType(checkRule.getRuleType());
		rule.setRuleName(checkRule.getRuleName());
		rule.setRuleId(checkRule.getRuleId());
		rule.setExceptionMessage(checkRule.getExceptionMessage());
		rule.setEnd1Condition(buildEnd1Condition(checkRule));
		rule.setEnd2ConditionList(buildEnd2Condition(checkRule));
		rule.setService(service);
		return rule;
	}

	private static ClassCondition buildEnd1Condition(CheckRule checkRule) throws ServiceRequestException
	{
		ClassCondition classCondition = new ClassCondition();
		ClassConditionData end1Condition = checkRule.getEnd1Condition();
		checkRule.setEnd1Condition(end1Condition);
		if (end1Condition != null)
		{
			classCondition.setClassName(checkRule.getEnd1Condition().getClassName());
			classCondition.setConditionGroup(buildConditionGroup(checkRule.getEnd1Condition(), checkRule));
		}
		return classCondition;
	}

	private static List<ClassCondition> buildEnd2Condition(CheckRule checkRule) throws ServiceRequestException
	{
		List<ClassCondition> conditionList = new ArrayList<>();
		if (!SetUtils.isNullList(checkRule.getEnd2ConditionList()))
		{
			for (End2CheckRule end2Rule : checkRule.getEnd2ConditionList())
			{
				ClassConditionData end2Condition = end2Rule.getEnd2Condition();
				end2Rule.setEnd2Condition(end2Condition);
				if (end2Condition != null)
				{
					ClassCondition classCondition = new ClassCondition();
					classCondition.setClassName(end2Rule.getEnd2Condition().getClassName());
					classCondition.setConditionGroup(buildConditionGroup(end2Rule.getEnd2Condition(), checkRule));
					conditionList.add(classCondition);
				}
			}
		}
		return conditionList;
	}

	private static ConditionGroup buildConditionGroup(ClassConditionData classConditionData, CheckRule checkRule) throws ServiceRequestException
	{
		List<ClassConditionDetailData> detailDataList = classConditionData.getDetailDataList();
		if (SetUtils.isNullList(detailDataList))
		{
			return new ConditionGroup();
		}
		ConditionGroup group = new ConditionGroup();
		for (ClassConditionDetailData detail : detailDataList)
		{
			group.addCondition(buildConditionGroup(detail, checkRule));
		}
		return group;
	}

	private static ConditionGroup buildConditionGroup(ClassConditionDetailData detail, CheckRule checkRule) throws ServiceRequestException
	{
		ConditionGroup group = new ConditionGroup();
		group.setJoinTypeEnum(detail.getJoinType());

		List<ClassConditionDetailData> detailDataList = detail.getDetailDataList();
		if (!SetUtils.isNullList(detailDataList))
		{
			for (ClassConditionDetailData detailData : detailDataList)
			{
				group.addCondition(buildConditionGroup(detailData, checkRule));
			}
		}
		else
		{
			// 规则调用
			if (detail.isDoCheckRule())
			{
				group.addCondition(buildRuleCondition(detail, checkRule));
			}
			else
			{
				group.addCondition(buildFieldCondition(detail));
			}
		}
		return group;
	}

	private static ICondition buildRuleCondition(ClassConditionDetailData detail, CheckRule checkRule) throws ServiceRequestException
	{
		CheckRule doCheckRule = DataCheckRuleQueryStub.CHECK_RULE_GUID_CACHE.get(detail.getFieldName());
		if (doCheckRule != null)
		{
			if (doCheckRule.getRuleType() == RuleTypeEnum.BOM || doCheckRule.getRuleType() == RuleTypeEnum.RELATION)
			{
				return getRule(doCheckRule, (FoundationObject) checkRule.getTempObject(), null);
			}
			else if (doCheckRule.getRuleType() == RuleTypeEnum.OBJECTFIELD)
			{
				FoundationObject foundationObject = (FoundationObject) checkRule.getTempObject();
				int index = doCheckRule.getRuleName().indexOf(".");
				if (index >= 0)
				{
					String objectField = doCheckRule.getRuleName().substring(index + 1);
					String guid = (String) foundationObject.get(objectField);
					String classGuid = (String) foundationObject.get(objectField + "$CLASS");
					if (StringUtils.isGuid(guid) && StringUtils.isGuid(classGuid))
					{
						ObjectGuid objectGuid = new ObjectGuid(classGuid, null, guid, null);
						try
						{
							BOAS boas = service.getRefService(BOAS.class);
							FoundationObject fieldObject = boas.getObjectNotCheckAuthor(objectGuid);
							return getRule(doCheckRule, (FoundationObject) checkRule.getTempObject(), fieldObject);
						}
						catch (ServiceNotFoundException e)
						{
							throw new ServiceRequestException(null, null, e);
						}
					}
				}
			}
		}
		return null;
	}

	// 条件格式为：x EQUALS 'b';或者 x ISNULL;或者${RULENAME}
	private static ICondition buildFieldCondition(ClassConditionDetailData detail) throws ServiceRequestException
	{
		String fieldName = detail.getFieldName();

		try
		{
			EMM emm = service.getRefService(EMM.class);
			FieldTypeEnum fieldType = null;
			ClassField classField = emm.getFieldByName(detail.getClassName(), fieldName, true);
			if (classField != null)
			{
				fieldType = classField.getType();
			}
			return FieldConditionFactory.getFieldCondition(fieldName, fieldType, detail.getOperator(), detail.getValue());
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, null, e);
		}
	}

	public static void init(DataAccessService service_)
	{
		service = service_;
	}
}
