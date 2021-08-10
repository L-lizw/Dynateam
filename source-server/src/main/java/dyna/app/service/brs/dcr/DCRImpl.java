package dyna.app.service.brs.dcr;

import java.util.List;

import dyna.app.service.BusinessRuleService;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.bean.data.checkrule.ClassConditionData;
import dyna.common.bean.data.checkrule.ClassConditionDetailData;
import dyna.common.bean.data.checkrule.End2CheckRule;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.DCR;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.WFI;
import dyna.net.service.das.MSRM;

public class DCRImpl extends BusinessRuleService implements DCR
{
	private static boolean			initialized			= false;

	private DataCheckRuleQueryStub	checkRuleQueryStub	= null;

	private DataCheckStub			checkStub			= null;

	private DataCheckRuleSaveStub	checkRuleSaveStub	= null;

	@Override
	protected void init()
	{
		if (initialized)
		{
			return;
		}

		DataCheckRuleQueryStub.init();

		initialized = true;
	}

	/**
	 * @return the DataCheckRuleQueryStub
	 */
	public DataCheckRuleQueryStub getDataCheckRuleQueryStub()
	{
		if (this.checkRuleQueryStub == null)
		{
			this.checkRuleQueryStub = new DataCheckRuleQueryStub(this.serviceContext, this);
		}
		return this.checkRuleQueryStub;
	}

	/**
	 * @return the DataCheckStub
	 */
	public DataCheckStub getDataCheckStub()
	{
		if (this.checkStub == null)
		{
			this.checkStub = new DataCheckStub(this.serviceContext, this);
		}
		return this.checkStub;
	}

	/**
	 * @return the DataCheckRuleSaveStub
	 */
	public DataCheckRuleSaveStub getDataCheckRuleSaveStub()
	{
		if (this.checkRuleSaveStub == null)
		{
			this.checkRuleSaveStub = new DataCheckRuleSaveStub(this.serviceContext, this);
		}
		return this.checkRuleSaveStub;
	}

	public synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized WFI getWFI() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

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
	public List<CheckRule> loadDataCheckConditionList(RuleTypeEnum ruleTypeEnum, String ruleName, String end1ClassName, String end2ClassName) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().loadDataCheckConditionList(ruleTypeEnum, ruleName, end1ClassName, end2ClassName);
	}

	public CheckRule getCheckRuleByGuid(String ruleGuid) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().getCheckRuleByGuid(ruleGuid);
	}

	public CheckRule getCheckRuleById(String ruleId) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().getCheckRuleById(ruleId);
	}

	@Override
	public boolean check(ObjectGuid end1ObjectGuid, List<ObjectGuid> end2ObjectGuidList, String ruleName, RuleTypeEnum ruleType) throws ServiceRequestException
	{
		return this.getDataCheckStub().check(end1ObjectGuid, end2ObjectGuidList, ruleName, ruleType);
	}

	@Override
	public boolean check(String procrtGuid, String wfName, String actrtName, List<ProcAttach> attachList) throws ServiceRequestException
	{
		return this.getDataCheckStub().check(procrtGuid, wfName, actrtName, attachList);
	}

	@Override
	public boolean check(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getDataCheckStub().check(foundationObject);
	}

	@Override
	public boolean check(String serviceTemplateName, FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getDataCheckStub().check(serviceTemplateName, foundationObject);
	}

	@Override
	public CheckRule saveRule(CheckRule rule) throws ServiceRequestException
	{
		return this.getDataCheckRuleSaveStub().saveRule(rule);
	}

	@Override
	public void deleteRule(CheckRule rule) throws ServiceRequestException
	{
		this.getDataCheckRuleSaveStub().deleteRule(rule);
	}

	@Override
	public ClassConditionData saveClassCondition(ClassConditionData classCondition, CheckRule rule, boolean end1) throws ServiceRequestException
	{
		return this.getDataCheckRuleSaveStub().saveClassCondition(classCondition, rule, end1);
	}

	@Override
	public void deleteClassCondition(ClassConditionData classCondition) throws ServiceRequestException
	{
		this.getDataCheckRuleSaveStub().deleteClassCondition(classCondition);
	}

	@Override
	public void deleteClassCondition(ClassConditionData classCondition, CheckRule rule) throws ServiceRequestException
	{
		this.getDataCheckRuleSaveStub().deleteClassCondition(classCondition, rule);
	}

	@Override
	public void deleteClassCondition(ClassConditionData classCondition, End2CheckRule rule) throws ServiceRequestException
	{
		this.getDataCheckRuleSaveStub().deleteClassCondition(classCondition, rule);
	}

	@Override
	public List<ClassConditionData> listClassConditionData() throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().listClassConditionData();
	}

	@Override
	public ClassConditionData getClassConditionData(String guid) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().getClassConditionData(guid);
	}

	@Override
	public List<CheckRule> listDataCheckRule() throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().listDataCheckRule();
	}

	public List<CheckRule> listDataDoCheckRule() throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().listDataDoCheckRule();
	}

	@Override
	public List<CheckRule> listDataCheckRuleByType(RuleTypeEnum ruleType) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().listDataCheckRuleByType(ruleType);
	}

	public List<CheckRule> listDataCheckRuleByType(RuleTypeEnum ruleType, boolean enabledDirectly) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().listDataCheckRuleByType(ruleType, enabledDirectly);
	}

	@Override
	public End2CheckRule saveEnd2CheckRule(End2CheckRule rule) throws ServiceRequestException
	{
		return this.getDataCheckRuleSaveStub().saveEnd2CheckRule(rule);
	}

	@Override
	public void deleteEnd2CheckRule(String guid) throws ServiceRequestException
	{
		this.getDataCheckRuleSaveStub().deleteEnd2CheckRule(guid);
	}

	@Override
	public void deleteEnd2CheckRule(End2CheckRule rule) throws ServiceRequestException
	{
		this.getDataCheckRuleSaveStub().deleteEnd2CheckRule(rule);
	}

	@Override
	public List<End2CheckRule> listEnd2CheckRule(String masterGuid) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().listEnd2CheckRule(masterGuid);
	}

	@Override
	public String getFieldValue(ClassConditionDetailData classConditionDetailData) throws ServiceRequestException
	{
		return this.getDataCheckRuleQueryStub().getConditionVal(classConditionDetailData.getClassName(), classConditionDetailData.getFieldName(),
				classConditionDetailData.getValue());
	}
}
