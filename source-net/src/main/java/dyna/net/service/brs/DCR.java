package dyna.net.service.brs;

import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.bean.data.checkrule.ClassConditionData;
import dyna.common.bean.data.checkrule.ClassConditionDetailData;
import dyna.common.bean.data.checkrule.End2CheckRule;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.net.service.Service;

public interface DCR extends Service
{
	/**
	 * 取得检查规则列表
	 * 
	 * @param ruleTypeEnum
	 *            规则类型
	 * @param ruleName
	 *            RuleType:
	 *            <ol>
	 *            Relation:Relation模板名<br>
	 *            BOM:BOM模板名<br>
	 *            WF:流程名<br>
	 *            OBJECTFIELD:字段名
	 *            </ol>
	 * @param end1ClassName
	 *            RuleType:
	 *            <ol>
	 *            Relation/BOM:end1类名<br>
	 *            WF:节点名<br>
	 *            OBJECTFIELD:主对象类名
	 *            </ol>
	 * @param end2ClassName
	 *            RuleType:
	 *            <ol>
	 *            Relation/BOM:end2类名<br>
	 *            WF:流程附件类名<br>
	 *            OBJECTFIELD:主对象上的Object字段选择类名
	 *            </ol>
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<CheckRule> loadDataCheckConditionList(RuleTypeEnum ruleTypeEnum, String ruleName, String end1ClassName, String end2ClassName) throws ServiceRequestException;

	public CheckRule getCheckRuleByGuid(String ruleGuid) throws ServiceRequestException;

	/**
	 * 根据规则编号取得规则
	 * 
	 * @param ruleId
	 * @return
	 * @throws ServiceRequestException
	 */
	public CheckRule getCheckRuleById(String ruleId) throws ServiceRequestException;

	/**
	 * 根据检查规则进行数据检查(BOM和关联关系编辑时)
	 * 
	 * @param end1ObjectGuid
	 *            end1对象
	 * @param end2ObjectGuidList
	 *            end2对象列表
	 * @param ruleName
	 *            BOM模板名或Relation模板名
	 * @param ruleTypeEnum
	 *            规则类型
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean check(ObjectGuid end1ObjectGuid, List<ObjectGuid> end2ObjectGuidList, String ruleName, RuleTypeEnum ruleType) throws ServiceRequestException;

	/**
	 * 根据检查规则进行数据检查
	 * 
	 * @param procrtGuid
	 *            流程guid，在流程未发起时做检查，值为空
	 * @param wfName
	 *            流程名
	 * @param actrtName
	 *            流程检查节点名
	 * @param attachList
	 *            流程附件，在流程未发起时做检查，值为发起流程的对象<br>
	 *            值为空时，表示检查指定已创建流程的所有附件
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean check(String procrtGuid, String wfName, String actrtName, List<ProcAttach> attachList) throws ServiceRequestException;

	/**
	 * ERP抛转检查
	 * 
	 * @param serviceTemplateName
	 *            ERP集成模板名称
	 * @param foundationObject
	 *            抛转对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean check(String serviceTemplateName, FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 检查对象上Object类型的规则
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean check(FoundationObject foundationObject) throws ServiceRequestException;

	public List<ClassConditionData> listClassConditionData() throws ServiceRequestException;

	public ClassConditionData getClassConditionData(String guid) throws ServiceRequestException;

	public List<CheckRule> listDataCheckRule() throws ServiceRequestException;

	public List<CheckRule> listDataDoCheckRule() throws ServiceRequestException;

	public List<CheckRule> listDataCheckRuleByType(RuleTypeEnum ruleType) throws ServiceRequestException;

	public List<CheckRule> listDataCheckRuleByType(RuleTypeEnum ruleType, boolean enabledDirectly) throws ServiceRequestException;

	public CheckRule saveRule(CheckRule rule) throws ServiceRequestException;

	public void deleteRule(CheckRule rule) throws ServiceRequestException;

	public ClassConditionData saveClassCondition(ClassConditionData classCondition, CheckRule rule, boolean end1) throws ServiceRequestException;

	public void deleteClassCondition(ClassConditionData classCondition) throws ServiceRequestException;

	public void deleteClassCondition(ClassConditionData classCondition, CheckRule rule) throws ServiceRequestException;

	public void deleteClassCondition(ClassConditionData classCondition, End2CheckRule rule) throws ServiceRequestException;

	public End2CheckRule saveEnd2CheckRule(End2CheckRule rule) throws ServiceRequestException;

	public void deleteEnd2CheckRule(String guid) throws ServiceRequestException;

	public void deleteEnd2CheckRule(End2CheckRule rule) throws ServiceRequestException;

	public List<End2CheckRule> listEnd2CheckRule(String masterGuid) throws ServiceRequestException;

	public String getFieldValue(ClassConditionDetailData classConditionDetailData) throws ServiceRequestException;
}
