package dyna.app.service.brs.dcr;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.bean.data.checkrule.ClassConditionData;
import dyna.common.bean.data.checkrule.ClassConditionDetailData;
import dyna.common.bean.data.checkrule.End2CheckRule;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataCheckRuleSaveStub extends AbstractServiceStub<DCRImpl>
{

	protected CheckRule saveRule(CheckRule rule) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (StringUtils.isNullString(rule.getGuid()))
			{
				String id = "0000";
				int seq = 1;
				List<CheckRule> allList = this.stubService.listDataCheckRule();
				if (!SetUtils.isNullList(allList))
				{
					seq = allList.get(allList.size() - 1).getSequence() + 1;
				}
				id = id + String.valueOf(seq);
				if (id.length() > 5)
				{
					id = id.substring(id.length() - 5);
				}
				id = "R" + id;
				rule.setRuleId(id);
				rule.setSequence(seq);
			}
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			rule.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
			rule.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
			String guid = sds.save(rule);
			rule = this.queryCheckRuleByGuid(StringUtils.isGuid(guid) ? guid : rule.getGuid());

			List<End2CheckRule> end2ConditionList = rule.getEnd2ConditionList();
			if (!SetUtils.isNullList(end2ConditionList))
			{
				for (End2CheckRule end2Rule : end2ConditionList)
				{
					end2Rule.setMasterGuid(rule.getGuid());
					end2Rule.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
					end2Rule.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());

					guid = sds.save(end2Rule);
					if (StringUtils.isGuid(guid))
					{
						end2Rule.setGuid(guid);
					}
				}
			}

			this.stubService.getDataCheckRuleQueryStub().cacheCheckRule(rule);

//			DataServer.getTransactionManager().commitTransaction();

			return rule;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteRule(CheckRule rule) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			List<End2CheckRule> end2ConditionList = rule.getEnd2ConditionList();
			if (!SetUtils.isNullList(end2ConditionList))
			{
				for (End2CheckRule end2Rule : end2ConditionList)
				{
					sds.delete(end2Rule);
				}
			}
			sds.delete(rule);

			this.stubService.getDataCheckRuleQueryStub().removeCheckRuleFromCache(rule.getGuid());

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected End2CheckRule saveEnd2CheckRule(End2CheckRule rule) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			SystemDataService sds = DataServer.getSystemDataService();
			rule.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
			rule.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
			String guid = sds.save(rule);
			if (StringUtils.isGuid(guid))
			{
				rule.setGuid(guid);
			}

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

		List<End2CheckRule> ruleList = new ArrayList<End2CheckRule>();
		ruleList.add(rule);

		this.stubService.getDataCheckRuleQueryStub().cacheEnd2CheckRule(ruleList);

		return rule;
	}

	protected void deleteEnd2CheckRule(String guid) throws ServiceRequestException
	{
		End2CheckRule rule = new End2CheckRule();
		rule.setGuid(guid);
		deleteEnd2CheckRule(rule);
	}

	protected void deleteEnd2CheckRule(End2CheckRule rule) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.delete(rule);

		this.stubService.getDataCheckRuleQueryStub().removeEnd2CheckRuleFromCache(rule.getGuid());
	}

	protected ClassConditionData saveClassCondition(ClassConditionData classCondition, CheckRule rule, boolean end1) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			boolean newData = (!StringUtils.isGuid(classCondition.getGuid()));

			List<ClassConditionDetailData> detailDataList = classCondition.getDetailDataList();

			SystemDataService sds = DataServer.getSystemDataService();
			classCondition.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
			classCondition.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
			String guid = sds.save(classCondition);

			if (newData)
			{
				if (end1)
				{
					rule.setEnd1ConditionGuid(guid);
					saveRule(rule);
				}
				else
				{
					End2CheckRule end2Rule = new End2CheckRule();
					end2Rule.setEnd2Condition(classCondition);
					end2Rule.setEnd2ConditionGuid(guid);
					end2Rule.setMasterGuid(rule.getGuid());
					List<End2CheckRule> end2CheckList = rule.getEnd2ConditionList();
					int sequence = SetUtils.isNullList(end2CheckList) ? 1 : end2CheckList.get(end2CheckList.size() - 1).getSequence();
					end2Rule.setSequence(sequence + 1);
					saveEnd2CheckRule(end2Rule);
				}
			}

			if (!StringUtils.isGuid(guid))
			{
				guid = classCondition.getGuid();
			}
			classCondition = this.getClassConditionByGuid(guid);
			classCondition.setDetailDataList(detailDataList);

			// 先删除所有条件，再重新添加
			ClassConditionDetailData param = new ClassConditionDetailData();
			param.setMasterGuid(guid);
			sds.delete(ClassConditionDetailData.class, param, "deleteByMaster");

			Map<String, String> dataMap = new HashMap<String, String>();
			this.saveClassConditionDetail(guid, classCondition.getDetailDataList(), dataMap);

			// this.stubService.getDataCheckRuleQueryStub().cacheClassCondition(classCondition);
			this.stubService.getDataCheckRuleQueryStub().reCache();

			ClassConditionData classConditionData = this.stubService.getDataCheckRuleQueryStub().getClassConditionData(classCondition.getGuid());

//			DataServer.getTransactionManager().commitTransaction();
			return classConditionData;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteClassCondition(ClassConditionData classCondition) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			SystemDataService sds = DataServer.getSystemDataService();
			sds.delete(ClassConditionData.class, classCondition.getGuid());

			ClassConditionDetailData param = new ClassConditionDetailData();
			param.setMasterGuid(classCondition.getGuid());
			sds.delete(ClassConditionDetailData.class, param, "deleteByMaster");

			// 遍历所有规则，删除规则和类规则的映射
			List<CheckRule> checkRuleList = this.stubService.listDataCheckRule();
			if (!SetUtils.isNullList(checkRuleList))
			{
				for (CheckRule checkRule : checkRuleList)
				{
					if (StringUtils.isGuid(checkRule.getEnd1ConditionGuid()) && checkRule.getEnd1ConditionGuid().equals(classCondition.getGuid()))
					{
						checkRule.setEnd1Condition(null);
						checkRule.setEnd1ConditionGuid(null);
						this.saveRule(checkRule);
					}

					List<End2CheckRule> end2ConditionList = checkRule.getEnd2ConditionList();
					if (!SetUtils.isNullList(end2ConditionList))
					{
						for (End2CheckRule end2CheckRule : end2ConditionList)
						{
							if (StringUtils.isGuid(end2CheckRule.getEnd2ConditionGuid()) && end2CheckRule.getEnd2ConditionGuid().equals(classCondition.getGuid()))
							{
								sds.delete(end2CheckRule);
							}
						}
					}
				}
			}

			// 重新缓存所有规则
			this.stubService.getDataCheckRuleQueryStub().reCache();

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteClassCondition(ClassConditionData classCondition, CheckRule rule) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			SystemDataService sds = DataServer.getSystemDataService();
			sds.delete(ClassConditionData.class, classCondition.getGuid());

			ClassConditionDetailData param = new ClassConditionDetailData();
			param.setMasterGuid(classCondition.getGuid());
			sds.delete(ClassConditionDetailData.class, param, "deleteByMaster");

			rule.setEnd1Condition(null);
			rule.setEnd1ConditionGuid(null);
			this.saveRule(rule);

			// 重新缓存所有规则
			this.stubService.getDataCheckRuleQueryStub().reCache();

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteClassCondition(ClassConditionData classCondition, End2CheckRule rule) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			SystemDataService sds = DataServer.getSystemDataService();
			sds.delete(ClassConditionData.class, classCondition.getGuid());

			ClassConditionDetailData param = new ClassConditionDetailData();
			param.setMasterGuid(classCondition.getGuid());
			sds.delete(ClassConditionDetailData.class, param, "deleteByMaster");

			sds.delete(rule);

			// 重新缓存所有规则
			this.stubService.getDataCheckRuleQueryStub().reCache();

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void saveClassConditionDetail(String masterGuid, List<ClassConditionDetailData> detailList, Map<String, String> dataMap) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (!SetUtils.isNullList(detailList))
		{
			for (ClassConditionDetailData detail : detailList)
			{
				detail.clear(ClassConditionDetailData.GUID);
				detail.setMasterGuid(masterGuid);
				detail.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
				detail.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
				detail.setParentGuid(dataMap.get(detail.getParentName()));
				detail.setGuid(sds.save(detail));

				dataMap.put(detail.getName(), detail.getGuid());
				this.saveClassConditionDetail(masterGuid, detail.getDetailDataList(), dataMap);
			}
		}
	}

	private CheckRule queryCheckRuleByGuid(String guid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		CheckRule rule = new CheckRule();
		rule.setGuid(guid);

		return sds.queryObject(CheckRule.class, rule);
	}

	private ClassConditionData getClassConditionByGuid(String guid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		ClassConditionData cond = new ClassConditionData();
		cond.setGuid(guid);

		return sds.queryObject(ClassConditionData.class, cond);
	}
}
