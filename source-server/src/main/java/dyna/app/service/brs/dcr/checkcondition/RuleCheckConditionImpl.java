package dyna.app.service.brs.dcr.checkcondition;

import java.util.List;

import dyna.app.service.brs.dcr.DataCheckRuleFactory;
import dyna.app.service.brs.dcr.checkrule.RelationRule;
import dyna.app.service.brs.dcr.checkrule.WFRule;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.util.SetUtils;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.DCR;
import dyna.net.service.brs.WFI;
import dyna.net.spi.ServiceProvider;

public class RuleCheckConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3016780283861097780L;

	private String				ruleId				= null;

	ServiceProvider				serviceProvider		= null;

	@Override
	public boolean check() throws ServiceRequestException
	{
		try
		{
			this.serviceProvider = ServiceProviderFactory.getServiceProvider();
			DCR dcr = this.serviceProvider.getServiceInstance(DCR.class, this.getSessionId());
			CheckRule rule = dcr.getCheckRuleById(this.getRuleId());
			if (rule == null || rule.getRuleType() == null)
			{
				return true;
			}

			if (rule.getRuleType() == RuleTypeEnum.BOM)
			{
				return this.checkBOM(rule);
			}
			else if (rule.getRuleType() == RuleTypeEnum.RELATION)
			{
				return this.checkRelation(rule);
			}
			else if (rule.getRuleType() == RuleTypeEnum.WF)
			{
				return this.checkWF(rule);
			}
			else if (rule.getRuleType() == RuleTypeEnum.OBJECTFIELD)
			{
				return this.checkObjectField(rule);
			}
			return true;
		}
		catch (ServiceNotFoundException e)
		{
			e.printStackTrace();
			throw new ServiceRequestException("ID_APP_SERVER_EXCEPTION", "", e);
		}
	}

	private boolean checkBOM(CheckRule rule) throws ServiceRequestException, ServiceNotFoundException
	{
		RelationRule relationRule = DataCheckRuleFactory.getRelationRule(rule);

		// 取得BOM结构
		BOMS boms = this.serviceProvider.getServiceInstance(BOMS.class, this.getSessionId());
		BOAS boas = this.serviceProvider.getServiceInstance(BOAS.class, this.getSessionId());
		List<BOMStructure> bomList = boms.listBOM(this.getFoundationObject().getObjectGuid(), rule.getRuleName(), null, null, null);
		if (!SetUtils.isNullList(bomList))
		{
			for (BOMStructure bomStructure : bomList)
			{
				FoundationObject end2 = boas.getObject(bomStructure.getEnd2ObjectGuid());
				relationRule.setFoundationObject(this.getFoundationObject(), end2);
				boolean b = relationRule.check();
				if (!b)
				{
					throw new ServiceRequestException("", "");
				}
			}
		}
		return true;
	}

	private boolean checkRelation(CheckRule rule) throws ServiceRequestException, ServiceNotFoundException
	{
		RelationRule relationRule = DataCheckRuleFactory.getRelationRule(rule);
		// 取得关联关系结构
		BOAS boas = this.serviceProvider.getServiceInstance(BOAS.class, this.getSessionId());
		List<StructureObject> structureList = boas.listObjectOfRelation(this.getFoundationObject().getObjectGuid(), rule.getRuleName(), null, null, null);
		if (!SetUtils.isNullList(structureList))
		{
			for (StructureObject structure : structureList)
			{
				FoundationObject end2 = boas.getObject(structure.getEnd2ObjectGuid());
				relationRule.setFoundationObject(this.getFoundationObject(), end2);
				boolean b = relationRule.check();
				if (!b)
				{
					throw new ServiceRequestException("", "");
				}
			}
		}
		return true;
	}

	private boolean checkWF(CheckRule rule) throws ServiceNotFoundException, ServiceRequestException
	{
		WFRule wfRule = DataCheckRuleFactory.getWFRule(rule);
		WFI wfi = this.serviceProvider.getServiceInstance(WFI.class, this.getSessionId());

		List<ProcessRuntime> processList = wfi.listProcessRuntimeOfObject(this.getFoundationObject().getObjectGuid(), null);
		if (!SetUtils.isNullList(processList))
		{
			for (ProcessRuntime process : processList)
			{
				List<ActivityRuntime> currentActivityRuntimeList = wfi.listCurrentActivityRuntime(process.getGuid());
				if (!SetUtils.isNullList(currentActivityRuntimeList))
				{
					for (ActivityRuntime actrt : currentActivityRuntimeList)
					{
						wfRule.setFoundationObject(actrt.getName(), this.getFoundationObject());
						boolean b = wfRule.check();
						if (!b)
						{
							throw new ServiceRequestException("", "");
						}
					}
				}
			}
		}

		return true;
	}

	private boolean checkObjectField(CheckRule rule) throws ServiceRequestException, ServiceNotFoundException
	{
		RelationRule relationRule = DataCheckRuleFactory.getRelationRule(rule);
		BOAS boas = this.serviceProvider.getServiceInstance(BOAS.class, this.getSessionId());

		String ruleName = rule.getRuleName();
		String fieldName = ruleName.split("\\.")[1];

		String guid = (String) this.getFoundationObject().get(fieldName);
		String classGuid = (String) this.getFoundationObject().get(fieldName + "$CLASS");
		ObjectGuid objectGuid = new ObjectGuid(classGuid, null, guid, null);
		FoundationObject end2 = boas.getObject(objectGuid);
		if (end2 == null)
		{
			throw new ServiceRequestException("", "");
		}
		relationRule.setFoundationObject(this.getFoundationObject(), end2);

		return relationRule.check();
	}

	public String getRuleId()
	{
		return this.ruleId;
	}

	public void setRuleId(String ruleId)
	{
		this.ruleId = ruleId;
	}
}
