package dyna.app.service.brs.dcr;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.bean.data.checkrule.ClassConditionData;
import dyna.common.bean.data.checkrule.ClassConditionDetailData;
import dyna.common.bean.data.checkrule.End2CheckRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

public class DataCheckRuleQueryStub extends AbstractServiceStub<DCRImpl>
{
	protected static Map<String, ClassConditionData>	CONDITION_GUID_CACHE		= Collections.synchronizedMap(new HashMap<String, ClassConditionData>());

	protected static Map<String, String>				CHECK_RULE_ID_GUID_CACHE	= Collections.synchronizedMap(new HashMap<String, String>());

	protected static Map<String, CheckRule>				CHECK_RULE_GUID_CACHE		= Collections.synchronizedMap(new HashMap<String, CheckRule>());

	protected static Map<String, End2CheckRule>			END2_RULE_GUID_CACHE		= Collections.synchronizedMap(new HashMap<String, End2CheckRule>());

	protected static Map<String, List<String>>			END2_RULE_GUID_LIST_CACHE	= Collections.synchronizedMap(new HashMap<String, List<String>>());

	protected DataCheckRuleQueryStub(ServiceContext context, DCRImpl service)
	{
		super(context, service);
	}

	protected static void init()
	{
		cacheClassCondition();
		cacheCheckRule();
	}

	protected static void cacheCheckRule()
	{
		CHECK_RULE_ID_GUID_CACHE.clear();
		CHECK_RULE_GUID_CACHE.clear();
		END2_RULE_GUID_CACHE.clear();
		END2_RULE_GUID_LIST_CACHE.clear();

		List<CheckRule> checkRuleList = queryCheckRule();
		if (!SetUtils.isNullList(checkRuleList))
		{
			for (CheckRule rule : checkRuleList)
			{
				List<End2CheckRule> end2CheckRList = queryEnd2CheckRule(rule.getGuid());
				rule.setEnd2ConditionList(end2CheckRList);

				CHECK_RULE_ID_GUID_CACHE.put(rule.getRuleId(), rule.getGuid());
				CHECK_RULE_GUID_CACHE.put(rule.getGuid(), rule);

				if (!SetUtils.isNullList(end2CheckRList))
				{
					for (End2CheckRule end2CheckRule : end2CheckRList)
					{
						END2_RULE_GUID_CACHE.put(end2CheckRule.getGuid(), end2CheckRule);
						if (!END2_RULE_GUID_LIST_CACHE.containsKey(end2CheckRule.getMasterGuid()))
						{
							END2_RULE_GUID_LIST_CACHE.put(end2CheckRule.getMasterGuid(), new ArrayList<String>());
						}
						END2_RULE_GUID_LIST_CACHE.get(end2CheckRule.getMasterGuid()).add(end2CheckRule.getGuid());
					}
				}
			}
		}
	}

	protected void cacheCheckRule(CheckRule checkRule)
	{
		CHECK_RULE_ID_GUID_CACHE.put(checkRule.getRuleId(), checkRule.getGuid());
		CHECK_RULE_GUID_CACHE.put(checkRule.getGuid(), checkRule);
		cacheEnd2CheckRule(checkRule.getEnd2ConditionList());
	}

	protected void reCache()
	{
		init();
	}

	protected void removeCheckRuleFromCache(String checkRuleGuid)
	{
		CheckRule checkRule = CHECK_RULE_GUID_CACHE.get(checkRuleGuid);
		if (checkRule != null)
		{
			CHECK_RULE_ID_GUID_CACHE.remove(checkRule.getRuleId());
			CHECK_RULE_GUID_CACHE.remove(checkRuleGuid);

			List<String> end2CheckRuleGuidList = END2_RULE_GUID_LIST_CACHE.get(checkRuleGuid);
			if (!SetUtils.isNullList(end2CheckRuleGuidList))
			{
				for (String end2CheckRuleGuid : end2CheckRuleGuidList)
				{
					END2_RULE_GUID_CACHE.remove(end2CheckRuleGuid);
				}
				END2_RULE_GUID_LIST_CACHE.remove(checkRuleGuid);
			}
		}
	}

	protected void removeEnd2CheckRuleFromCache(String end2CheckRuleGuid)
	{
		End2CheckRule end2CheckRule = END2_RULE_GUID_CACHE.get(end2CheckRuleGuid);
		String masterGuid = end2CheckRule.getMasterGuid();

		CheckRule rule = CHECK_RULE_GUID_CACHE.get(masterGuid);
		if (rule != null)
		{
			List<End2CheckRule> end2CheckRuleList = rule.getEnd2ConditionList();
			if (!SetUtils.isNullList(end2CheckRuleList))
			{
				for (End2CheckRule end2Rule : end2CheckRuleList)
				{
					if (end2CheckRuleGuid.equals(end2Rule.getGuid()))
					{
						end2CheckRuleList.remove(end2Rule);
						break;
					}
				}
			}
		}

		END2_RULE_GUID_LIST_CACHE.get(masterGuid).remove(end2CheckRuleGuid);
		END2_RULE_GUID_CACHE.remove(end2CheckRuleGuid);
	}

	protected void removeClassConditionFromCache(String classConditionGuid)
	{
		CONDITION_GUID_CACHE.remove(classConditionGuid);
	}

	protected void cacheEnd2CheckRule(List<End2CheckRule> end2CheckRList)
	{
		if (!SetUtils.isNullList(end2CheckRList))
		{
			for (End2CheckRule rule : end2CheckRList)
			{
				END2_RULE_GUID_CACHE.put(rule.getGuid(), rule);
				if (!END2_RULE_GUID_LIST_CACHE.containsKey(rule.getMasterGuid()))
				{
					END2_RULE_GUID_LIST_CACHE.put(rule.getMasterGuid(), new ArrayList<String>());
				}
				END2_RULE_GUID_LIST_CACHE.get(rule.getMasterGuid()).add(rule.getGuid());
			}
		}
	}

	protected static void cacheClassCondition()
	{
		CONDITION_GUID_CACHE.clear();

		List<ClassConditionData> classConditionList = queryClassCondition();
		if (!SetUtils.isNullList(classConditionList))
		{
			for (ClassConditionData cond : classConditionList)
			{
				cond.setDetailDataList(queryClassConditionDetail(cond));
				CONDITION_GUID_CACHE.put(cond.getGuid(), cond);
			}
		}
	}

	protected void cacheClassCondition(ClassConditionData classConditionData)
	{
		ClassConditionData cond = CONDITION_GUID_CACHE.get(classConditionData.getGuid());
		if (cond != null)
		{
			CONDITION_GUID_CACHE.remove(cond.getGuid());
		}

		CONDITION_GUID_CACHE.put(classConditionData.getGuid(), classConditionData);
	}

	protected List<ClassConditionData> listClassConditionData() throws ServiceRequestException
	{
		List<ClassConditionData> dataList = new ArrayList<ClassConditionData>();
		if (!SetUtils.isNullMap(CONDITION_GUID_CACHE))
		{
			for (String ruleGuid : CONDITION_GUID_CACHE.keySet())
			{
				ClassConditionData data = this.getClassConditionData(ruleGuid);
				if (data != null)
				{
					dataList.add(data);
				}
			}
		}
		return dataList;
	}

	protected ClassConditionData getClassConditionData(String guid) throws ServiceRequestException
	{
		ClassConditionData cond = CONDITION_GUID_CACHE.get(guid);
		if (cond != null)
		{
			if (!SetUtils.isNullList(cond.getDetailDataList()))
			{
				for (ClassConditionDetailData detail : cond.getDetailDataList())
				{
					this.setConditionDesc(detail, cond.getClassName());
				}
			}
			cond.setConditionDesc(this.getConditionDesc(cond));
		}

		return cond;
	}

	private void setConditionDesc(ClassConditionDetailData detail, String className) throws ServiceRequestException
	{
		detail.setClassName(className);
		if (!SetUtils.isNullList(detail.getDetailDataList()))
		{
			for (ClassConditionDetailData detail_ : detail.getDetailDataList())
			{
				this.setConditionDesc(detail_, className);
			}
		}
		detail.setConditionDesc(this.getConditionDesc(detail));
	}

	protected List<CheckRule> listDataCheckRule() throws ServiceRequestException
	{
		if (SetUtils.isNullMap(CHECK_RULE_ID_GUID_CACHE))
		{
			return null;
		}

		List<CheckRule> ruleList = new ArrayList<CheckRule>();
		for (String ruleId : CHECK_RULE_ID_GUID_CACHE.keySet())
		{
			String checkRuleGuid = CHECK_RULE_ID_GUID_CACHE.get(ruleId);
			ruleList.add(this.getDataCheckRule(checkRuleGuid));
		}

		if (!SetUtils.isNullList(ruleList))
		{
			Collections.sort(ruleList, new Comparator<CheckRule>() {

				@Override
				public int compare(CheckRule o1, CheckRule o2)
				{
					return o1.getRuleId().compareTo(o2.getRuleId());
				}
			});
		}

		return ruleList;
	}

	protected List<CheckRule> listDataDoCheckRule() throws ServiceRequestException
	{
		List<CheckRule> ruleList = listDataCheckRule();
		for (Iterator<CheckRule> iterator = ruleList.iterator(); iterator.hasNext();)
		{
			CheckRule rule = iterator.next();
			if (rule.getRuleType() == null || rule.getRuleType() == RuleTypeEnum.ERP || rule.getRuleType() == RuleTypeEnum.WF)
			{
				iterator.remove();
			}
		}
		return ruleList;
	}

	protected List<CheckRule> listDataCheckRuleByType(RuleTypeEnum ruleType) throws ServiceRequestException
	{
		List<CheckRule> ruleList = listDataCheckRule();
		if (!SetUtils.isNullList(ruleList))
		{
			for (Iterator<CheckRule> iterator = ruleList.iterator(); iterator.hasNext();)
			{
				CheckRule rule = iterator.next();
				if (rule.getRuleType() == null || rule.getRuleType() != ruleType)
				{
					iterator.remove();
				}
			}
		}
		return ruleList;
	}

	protected List<CheckRule> listDataCheckRuleByType(RuleTypeEnum ruleType, boolean enabledDirectly) throws ServiceRequestException
	{
		List<CheckRule> ruleList = listDataCheckRule();
		for (Iterator<CheckRule> iterator = ruleList.iterator(); iterator.hasNext();)
		{
			CheckRule rule = iterator.next();
			if (rule.getRuleType() == null || rule.getRuleType() != ruleType || rule.isEnabledDirectly() == enabledDirectly)
			{
				iterator.remove();
			}
		}
		return ruleList;
	}

	protected CheckRule getDataCheckRule(String ruleGuid) throws ServiceRequestException
	{
		CheckRule checkRule = CHECK_RULE_GUID_CACHE.get(ruleGuid);
		if (checkRule.getEnd1Condition() == null)
		{
			checkRule.setEnd1Condition(this.getClassConditionData(checkRule.getEnd1ConditionGuid()));
			List<String> end2RuleGuidList = END2_RULE_GUID_LIST_CACHE.get(checkRule.getGuid());
			if (!SetUtils.isNullList(end2RuleGuidList))
			{
				for (String end2RuleGuid : end2RuleGuidList)
				{
					End2CheckRule end2Rule = END2_RULE_GUID_CACHE.get(end2RuleGuid);
					if (end2Rule != null)
					{
						end2Rule.setEnd2Condition(this.getClassConditionData(end2Rule.getEnd2ConditionGuid()));
						checkRule.addEnd2Condition(end2Rule);
					}
				}
			}
		}
		return checkRule;
	}

	protected List<End2CheckRule> listEnd2CheckRule(String masterGuid) throws ServiceRequestException
	{
		CheckRule checkRule = this.getDataCheckRule(masterGuid);
		if (checkRule != null)
		{
			return checkRule.getEnd2ConditionList();
		}
		return null;
	}

	/**
	 * 取得检查规则列表
	 * 
	 * @param ruleTypeEnum
	 * @param ruleName
	 * @param end1ClassName
	 * @param end2ClassName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<CheckRule> loadDataCheckConditionList(RuleTypeEnum ruleTypeEnum, String ruleName, String end1ClassName, String end2ClassName) throws ServiceRequestException
	{
		List<CheckRule> conditionByTypeRuleList = this.loadDataCheckConditionList(ruleTypeEnum, ruleName, end1ClassName);
		if (SetUtils.isNullList(conditionByTypeRuleList))
		{
			return null;
		}

		List<CheckRule> finalList = new ArrayList<CheckRule>();
		this.filterByEnd1Class(finalList, ruleTypeEnum, conditionByTypeRuleList, end1ClassName);

		conditionByTypeRuleList.clear();
		conditionByTypeRuleList.addAll(finalList);
		finalList.clear();
		this.filterByEnd2Class(finalList, ruleTypeEnum, conditionByTypeRuleList, end2ClassName);

		return finalList;
	}

	protected CheckRule getCheckRuleByGuid(String ruleGuid) throws ServiceRequestException
	{
		return this.getDataCheckRule(ruleGuid);
	}

	protected CheckRule getCheckRuleById(String ruleId) throws ServiceRequestException
	{
		String ruleGuid = CHECK_RULE_ID_GUID_CACHE.get(ruleId);
		return this.getDataCheckRule(ruleGuid);
	}

	private static List<ClassConditionDetailData> setTreeOfClassCondDetail(ClassConditionData cond, List<ClassConditionDetailData> classConditionDetailList)
	{
		List<ClassConditionDetailData> firstLevelList = new ArrayList<ClassConditionDetailData>();
		Map<String, ClassConditionDetailData> detailMap = new HashMap<String, ClassConditionDetailData>();
		if (!SetUtils.isNullList(classConditionDetailList))
		{
			for (ClassConditionDetailData detail : classConditionDetailList)
			{
				detailMap.put(detail.getGuid(), detail);
			}

			for (ClassConditionDetailData detail : classConditionDetailList)
			{
				if (StringUtils.isGuid(detail.getParentGuid()))
				{
					detailMap.get(detail.getParentGuid()).addDetail(detail);
				}
			}

			for (String guid : detailMap.keySet())
			{
				ClassConditionDetailData detail = detailMap.get(guid);
				if (!StringUtils.isGuid(detail.getParentGuid()))
				{
					firstLevelList.add(detail);
				}
			}
		}
		Collections.sort(firstLevelList);
		return firstLevelList;
	}

	private List<CheckRule> loadDataCheckConditionList(RuleTypeEnum ruleTypeEnum, String ruleName, String end1ClassName) throws ServiceRequestException
	{
		List<CheckRule> conditionByTypeRuleList = new ArrayList<CheckRule>();
		if (!SetUtils.isNullMap(CHECK_RULE_ID_GUID_CACHE))
		{
			for (String ruleId : CHECK_RULE_ID_GUID_CACHE.keySet())
			{
				String ruleGuid = CHECK_RULE_ID_GUID_CACHE.get(ruleId);
				CheckRule checkRule = this.getDataCheckRule(ruleGuid);
				if (checkRule == null)
				{
					continue;
				}

				if (checkRule.getRuleType() != null && ruleTypeEnum == checkRule.getRuleType() && ruleName.equals(checkRule.getRuleName()) && checkRule.isEnabledDirectly())
				{
					conditionByTypeRuleList.add(checkRule);
				}
			}
		}

		// ERP抛转规则，不需要检查END1条件（只根据第一约束，即ERP集成模板来过滤即可）
		if (ruleTypeEnum == RuleTypeEnum.ERP)
		{
			return conditionByTypeRuleList;
		}

		// 工作流规则，end1ClassName的值为流程名字+"."+流程节点
		if (ruleTypeEnum == RuleTypeEnum.WF)
		{
			List<CheckRule> list = new ArrayList<CheckRule>();
			for (CheckRule rule : conditionByTypeRuleList)
			{
				if (rule.getEnd1Condition() != null && (ruleName + "." + end1ClassName).equals(rule.getEnd1Condition().getClassName()))
				{
					list.add(rule);
				}
			}
			return list;
		}

		ClassInfo classInfo = this.stubService.getEMM().getClassByName(end1ClassName);
		if (classInfo == null)
		{
			return null;
		}

		// OBJECTFIELD检查规则，如果没有配置当前类，需要检查父类(RuleName:类名+"."+Object字段名)
		if (ruleTypeEnum == RuleTypeEnum.OBJECTFIELD && SetUtils.isNullList(conditionByTypeRuleList))
		{
			// 递归取父类
			ClassInfo klassInfo = this.stubService.getEMM().getClassByName(end1ClassName);
			if (StringUtils.isNullString(klassInfo.getSuperclass()))
			{
				return conditionByTypeRuleList;
			}
			return this.loadDataCheckConditionList(ruleTypeEnum, ruleName, klassInfo.getSuperclass());
		}

		return conditionByTypeRuleList;
	}

	/**
	 * 根据类名取得当前类及其父类设置的规则列表
	 * 
	 * @param finalList
	 * @param ruleTypeEnum
	 * @param ruleList
	 * @param className
	 * @throws ServiceRequestException
	 */
	private void filterByEnd1Class(List<CheckRule> finalList, RuleTypeEnum ruleTypeEnum, List<CheckRule> ruleList, String className) throws ServiceRequestException
	{
		if (ruleTypeEnum == RuleTypeEnum.ERP)
		{
			finalList.addAll(ruleList);
			return;
		}

		if (SetUtils.isNullList(ruleList) || StringUtils.isNullString(className))
		{
			return;
		}

		if (ruleTypeEnum == RuleTypeEnum.WF)
		{
			this.filterWFRule(finalList, ruleList, className);
			return;
		}
		if (ruleTypeEnum == RuleTypeEnum.ERP)
		{
			finalList.addAll(ruleList);
			return;
		}

		ClassInfo classInfo = this.stubService.getEMM().getClassByName(className);
		String superClassName = classInfo.getSuperclass();
		for (CheckRule rule : ruleList)
		{
			ClassConditionData end1ClassCondition = rule.getEnd1Condition();
			if (end1ClassCondition != null && className.equals(end1ClassCondition.getClassName()))
			{
				// 取得当前类设置的规则列表以及父类设置的规则列表的合集
				finalList.add(rule);

				if (ruleTypeEnum != RuleTypeEnum.WF)
				{
					this.filterByEnd1Class(finalList, ruleTypeEnum, ruleList, superClassName);
				}
			}
			else
			{
				// 取得父类设置的规则列表
				this.filterByEnd1Class(finalList, ruleTypeEnum, ruleList, superClassName);
			}
		}
	}

	private void filterWFRule(List<CheckRule> finalList, List<CheckRule> conditionList, String actrtName)
	{
		for (CheckRule rule : conditionList)
		{
			ClassConditionData end1ClassCondition = rule.getEnd1Condition();
			if ((rule.getRuleName() + "." + actrtName).equals(end1ClassCondition.getClassName()))
			{
				// 取得当前类设置的规则列表以及父类设置的规则列表的合集
				finalList.add(rule);
			}
		}
	}

	private void filterByEnd2Class(List<CheckRule> finalList, RuleTypeEnum ruleTypeEnum, List<CheckRule> ruleList, String className) throws ServiceRequestException
	{
		if (SetUtils.isNullList(ruleList) || StringUtils.isNullString(className))
		{
			return;
		}

		ClassInfo classInfo = this.stubService.getEMM().getClassByName(className);
		String superClassName = classInfo.getSuperclass();
		for (CheckRule rule : ruleList)
		{
			List<End2CheckRule> end2ClassConditionList = rule.getEnd2ConditionList();
			if (!SetUtils.isNullList(end2ClassConditionList))
			{
				for (End2CheckRule end2Condition : end2ClassConditionList)
				{
					if (className.equals(end2Condition.getEnd2Condition().getClassName()))
					{
						// 取得当前类设置的规则列表以及父类设置的规则列表的合集
						finalList.add(rule);
						break;
					}
					else
					{
						this.filterByEnd2Class(finalList, ruleTypeEnum, ruleList, superClassName);
					}
				}
			}
		}
	}

	private static List<CheckRule> queryCheckRule()
	{
		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(CheckRule.class, null);
	}

	private static List<End2CheckRule> queryEnd2CheckRule(String masterGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(End2CheckRule.MASTERGUID, masterGuid);
		return sds.query(End2CheckRule.class, paramMap);
	}

	private static List<ClassConditionData> queryClassCondition()
	{
		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(ClassConditionData.class, null);
	}

	private static List<ClassConditionDetailData> queryClassConditionDetail(ClassConditionData cond)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(ClassConditionDetailData.MASTERGUID, cond.getGuid());
		List<ClassConditionDetailData> classConditionDetailList = sds.query(ClassConditionDetailData.class, paramMap);

		return setTreeOfClassCondDetail(cond, classConditionDetailList);
	}

	private String getConditionDesc(ClassConditionData data) throws ServiceRequestException
	{
		StringBuffer buffer = new StringBuffer();
		if (!SetUtils.isNullList(data.getDetailDataList()))
		{
			for (ClassConditionDetailData detail : data.getDetailDataList())
			{
				if (buffer.length() > 0)
				{
					buffer.append(" ");
				}
				buffer.append(this.getConditionDesc(detail));
			}
		}
		return buffer.toString().trim();
	}

	private String getConditionDesc(ClassConditionDetailData detail) throws ServiceRequestException
	{
		Map<String, String> messageMap = this.stubService.getMSRM().getMSRMap(this.stubService.getUserSignature().getLanguageEnum().toString());
		String conditionstr = null;
		if (!SetUtils.isNullList(detail.getDetailDataList()))
		{
			StringBuffer buffer = new StringBuffer();
			for (ClassConditionDetailData detail_ : detail.getDetailDataList())
			{
				if (buffer.length() > 0)
				{
					buffer.append(" ");
				}
				buffer.append(this.getConditionstr(detail_, messageMap));
			}

			// 把第一个条件的连接符（AND或者OR）提出来，剩下的部分两边加括号
			String joinTypeTitle = messageMap.get(detail.getJoinType().getMessageId());
			conditionstr = joinTypeTitle + " (" + buffer.substring(3) + ")";
		}
		else
		{
			conditionstr = this.getConditionstr(detail, messageMap);
		}

		// 第一个条件连接符去掉
		if (!StringUtils.isNullString(conditionstr) && "0".equals(detail.getSequence()))
		{
			return conditionstr.substring(3);
		}
		return conditionstr;
	}

	private String getConditionstr(ClassConditionDetailData detail, Map<String, String> messageMap) throws ServiceRequestException
	{
		if (ClassConditionDetailData.ROOT_NAME.equals(detail.getGuid()))
		{
			return null;
		}

		if (StringUtils.isNullString(detail.getFieldName()))
		{
			return null;
		}

		String joinTypeTitle = messageMap.get(detail.getJoinType().getMessageId());

		// DoCheck
		if (StringUtils.isGuid(detail.getFieldName()))
		{
			CheckRule doCheckRule = CHECK_RULE_GUID_CACHE.get(detail.getFieldName());
			if (doCheckRule == null)
			{
				return null;
			}
			return joinTypeTitle + " " + MessageFormat.format(messageMap.get("ID_RM_DOCHECK"), doCheckRule.getName());
		}

		ClassField field = this.stubService.getEMM().getFieldByName(detail.getClassName(), detail.getFieldName(), false);
		ClassInfo classInfo = null;
		try
		{
			classInfo = this.stubService.getEMM().getClassByName(detail.getClassName());
		}
		catch (ServiceRequestException e)
		{
		}
		if (classInfo == null)
		{
			return null;
		}
		String fieldName = StringUtils.convertNULLtoString(field.getTitle(this.stubService.getUserSignature().getLanguageEnum())) + "[" + field.getName() + "]";

		String operatorTitle = (detail.getOperator() == OperateSignEnum.TRUE || detail.getOperator() == OperateSignEnum.FALSE) ? messageMap.get("ID_RM_IS") + " "
				+ messageMap.get(detail.getOperator().getMsrId()) : messageMap.get(detail.getOperator().getMsrId());

		// 条件为RuleName
		// if (detail.getFieldName().startsWith("$"))
		// {
		// return joinTypeTitle + " " + detail.getFieldName();
		// }

		String val = this.getConditionVal(detail.getClassName(), detail.getFieldName(), detail.getValue());
		return joinTypeTitle + " " + fieldName + " " + operatorTitle + " " + StringUtils.convertNULLtoString(val);
	}

	public String getConditionVal(String className, String fieldName, String value) throws ServiceRequestException
	{
		String locale = this.stubService.getUserSignature().getLanguageEnum().toString();
		ClassInfo classInfo = null;
		try
		{
			classInfo = this.stubService.getEMM().getClassByName(className);
		}
		catch (ServiceRequestException e)
		{
		}
		if (classInfo == null)
		{
			return value;
		}
		ClassField classField = this.stubService.getEMM().getFieldByName(className, fieldName, false);
		if (SystemClassFieldEnum.STATUS.getName().equals(fieldName))
		{
			value = this.stubService.getMSRM().getMSRString(SystemStatusEnum.getStatusEnum(value).getMsrId(), locale);
		}
		else if (SystemClassFieldEnum.LCPHASE.getName().equals(fieldName))
		{
			LifecyclePhaseInfo phase = this.stubService.getEMM().getLifecyclePhaseInfo(classInfo.getLifecycleName(), value);
			if (phase != null)
			{
				value = StringUtils.getMsrTitle(phase.getTitle(), this.stubService.getUserSignature().getLanguageEnum().getType());
			}
		}
		else if (classField != null)
		{
			if (classField.getType() == FieldTypeEnum.BOOLEAN)
			{
				value = null;
				// Boolean b = BooleanUtils.getBooleanByYN(value);
				// return b ? this.stubService.getMSRM().getMSRString(OperateSignEnum.YES.getMsrId(), locale) :
				// this.stubService.getMSRM().getMSRString(OperateSignEnum.NO.getMsrId(),
				// locale);
			}
			else if (classField.getType() == FieldTypeEnum.CLASSIFICATION || classField.getType() == FieldTypeEnum.CODE)
			{
				String typeValue = classField.getTypeValue();
				CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItemByName(typeValue, value);
				if (codeItemInfo != null)
				{
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append(StringUtils.getMsrTitle(codeItemInfo.getTitle(), this.stubService.getUserSignature().getLanguageEnum().getType()));
					stringBuffer.append("[");
					stringBuffer.append(codeItemInfo.getCode());
					stringBuffer.append("]");
					value = stringBuffer.toString();
				}
			}
			else if (classField.getType() == FieldTypeEnum.MULTICODE)
			{
				StringBuffer stringBuffer = new StringBuffer();
				String typeValue = classField.getTypeValue();
				if (!StringUtils.isNullString(value))
				{
					for (String s : value.split(";"))
					{
						if (stringBuffer.length() > 0)
						{
							stringBuffer.append(";");
						}
						CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItemByName(typeValue, s);
						if (codeItemInfo != null)
						{
							stringBuffer.append(StringUtils.getMsrTitle(codeItemInfo.getTitle(), this.stubService.getUserSignature().getLanguageEnum().getType()));
							stringBuffer.append("[");
							stringBuffer.append(codeItemInfo.getCode());
							stringBuffer.append("]");
						}
					}
				}
				value = stringBuffer.toString();
			}
		}
		return StringUtils.convertNULLtoString(value);
	}
}
