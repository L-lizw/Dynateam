package dyna.common.bean.data.checkrule;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.checkrule.CheckRuleMapper;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;

import java.util.ArrayList;
import java.util.List;

@EntryMapper(CheckRuleMapper.class)
public class CheckRule extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5852106256255350453L;

	public static final String	RULEID				= "RULEID";

	public static final String	RULENAME			= "RULENAME";

	public static final String	FIRSTCONDITION		= "FIRSTCONDITION";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	RULETYPE			= "RULETYPE";

	public static final String	END1CONDITIONGUID	= "END1CONDITION";

	public static final String	EXCEPTIONMESSAGE	= "EXCEPTIONMSG";

	public static final String	ENABLED_DIRECTLY	= "ENABLEDDIRECTLY";

	/**
	 * 临时存放对象，用以递归构建DoCheckRule
	 */
	public static final String	TEMP_OBJECT			= "TEMP_OBJECT";

	/**
	 * 树结构根节点标记，根节点存放RULETYPE
	 */
	public static final String	ROOT				= "ROOT";

	/**
	 * 是否新建标记
	 */
	public static final String	NEW					= "NEW";

	private ClassConditionData	end1Condition		= null;

	private List<End2CheckRule>	end2ConditionList	= null;

	public String getRuleId()
	{
		return (String) this.get(RULEID);
	}

	public void setRuleId(String ruleId)
	{
		this.put(RULEID, ruleId);
	}

	public String getRuleName()
	{
		return (String) this.get(RULENAME);
	}

	public void setRuleName(String ruleName)
	{
		this.put(RULENAME, ruleName);
	}

	public String getFirstCondition()
	{
		return (String) this.get(FIRSTCONDITION);
	}

	public void setFirstCondition(String firstCondition)
	{
		this.put(FIRSTCONDITION, firstCondition);
	}

	public RuleTypeEnum getRuleType()
	{
		return this.get(RULETYPE) == null ? null : RuleTypeEnum.valueOf((String) this.get(RULETYPE));
	}

	public void setRuleType(RuleTypeEnum ruleTypeEnum)
	{
		this.put(RULETYPE, ruleTypeEnum.name());
	}

	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, sequence);
	}

	public String getEnd1ConditionGuid()
	{
		return (String) this.get(END1CONDITIONGUID);
	}

	public void setEnd1ConditionGuid(String end1ConditionGuid)
	{
		this.put(END1CONDITIONGUID, end1ConditionGuid);
	}

	public Boolean isEnabledDirectly()
	{
		return this.get(ENABLED_DIRECTLY) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ENABLED_DIRECTLY));
	}

	public void setEnabledDirectly(boolean enabledDirectly)
	{
		this.put(ENABLED_DIRECTLY, BooleanUtils.getBooleanStringYN(enabledDirectly));
	}

	public Boolean isRoot()
	{
		return this.get(ROOT) != null;
	}

	public void setRoot(Object root)
	{
		this.put(ROOT, root);
	}

	public Object getRoot()
	{
		return this.get(ROOT);
	}

	public Boolean isNew()
	{
		return this.get(NEW) != null;
	}

	public void setNew()
	{
		this.put(NEW, NEW);
	}

	public ClassConditionData getEnd1Condition()
	{
		return end1Condition;
	}

	public void setEnd1Condition(ClassConditionData end1Condition)
	{
		this.end1Condition = end1Condition;
	}

	public List<End2CheckRule> getEnd2ConditionList()
	{
		return end2ConditionList;
	}

	public void setEnd2ConditionList(List<End2CheckRule> end2ConditionList)
	{
		this.end2ConditionList = end2ConditionList;
	}

	public void addEnd2Condition(End2CheckRule end2CheckRule)
	{
		if (this.end2ConditionList == null)
		{
			this.end2ConditionList = new ArrayList<End2CheckRule>();
		}

		List<String> ruleGuidList = new ArrayList<String>();
		for (End2CheckRule rule : this.end2ConditionList)
		{
			ruleGuidList.add(rule.getGuid());
		}
		if (end2CheckRule != null && !ruleGuidList.contains(end2CheckRule.getGuid()))
		{
			this.end2ConditionList.add(end2CheckRule);
		}
	}

	public void removeEnd2Condition(End2CheckRule end2CheckRule)
	{
		if (!SetUtils.isNullList(this.end2ConditionList))
		{
			this.end2ConditionList.remove(end2CheckRule);
		}
	}

	public String getExceptionMessage()
	{
		return (String) this.get(EXCEPTIONMESSAGE);
	}

	public void setExceptionMessage(String exceptionMessage)
	{
		this.put(EXCEPTIONMESSAGE, exceptionMessage);
	}

	public Object getTempObject()
	{
		return this.get(TEMP_OBJECT);
	}

	public void setTempObject(Object object)
	{
		this.put(TEMP_OBJECT, object);
	}

	@Override
	public String getName()
	{
		return (String) super.get(RULENAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(RULENAME, name);
	}

}
