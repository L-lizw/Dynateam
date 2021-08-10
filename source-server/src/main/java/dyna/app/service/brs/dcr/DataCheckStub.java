package dyna.app.service.brs.dcr;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.dcr.checkrule.ERPRule;
import dyna.app.service.brs.dcr.checkrule.RelationRule;
import dyna.app.service.brs.dcr.checkrule.WFRule;
import dyna.app.service.brs.emm.ClassStub;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class DataCheckStub extends AbstractServiceStub<DCRImpl>
{

	protected DataCheckStub(ServiceContext context, DCRImpl service)
	{
		super(context, service);
		DataCheckRuleFactory.init(this.stubService);
	}

	/**
	 * BOM/关联关系检查
	 * 
	 * @param end1ObjectGuid
	 *            父阶对象
	 * @param end2ObjectGuidList
	 *            子阶对象列表
	 * @param ruleName
	 *            BOM/关联关系模板名称
	 * @param ruleType
	 *            BOM/RELATION
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean check(ObjectGuid end1ObjectGuid, List<ObjectGuid> end2ObjectGuidList, String ruleName, RuleTypeEnum ruleType) throws ServiceRequestException
	{
		if (ruleType == RuleTypeEnum.WF)
		{
			return false;
		}

		FoundationObject end1 = null;
		try
		{
			end1 = this.stubService.getBOAS().getObject(end1ObjectGuid);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(
					this.stubService.getMSRM().getMSRString("ID_RM_CHECK_ILLEGAL", this.stubService.getUserSignature().getLanguageEnum().name(), e.getMessage()),
					"Unable to get end1");
		}
		if (end1 == null)
		{
			throw new ServiceRequestException(
					this.stubService.getMSRM().getMSRString("ID_RM_CHECK_ILLEGAL", this.stubService.getUserSignature().getLanguageEnum().name(), "Unable to get end1"),
					"Unable to get end1");
		}

		if (SetUtils.isNullList(end2ObjectGuidList))
		{
			return true;
		}

		for (ObjectGuid end2ObjectGuid : end2ObjectGuidList)
		{
			FoundationObject end2 = null;
			try
			{
				end2 = this.stubService.getBOAS().getObjectNotCheckAuthor(end2ObjectGuid);
			}
			catch (Exception e)
			{
				throw new ServiceRequestException(
						this.stubService.getMSRM().getMSRString("ID_RM_CHECK_ILLEGAL", this.stubService.getUserSignature().getLanguageEnum().name(), e.getMessage()),
						"Unable to get end2");
			}
			if (end2 == null)
			{
				throw new ServiceRequestException(
						this.stubService.getMSRM().getMSRString("ID_RM_CHECK_ILLEGAL", this.stubService.getUserSignature().getLanguageEnum().name(), "Unable to get end1"),
						"Unable to get end2");
			}

			List<CheckRule> list = this.stubService.loadDataCheckConditionList(ruleType, ruleName, end1.getObjectGuid().getClassName(), end2.getObjectGuid().getClassName());
			if (!SetUtils.isNullList(list))
			{
				for (CheckRule rule : list)
				{
					RelationRule relationRule = DataCheckRuleFactory.getRule(rule, end1, end2);
					boolean result = relationRule.check();
					if (!result)
					{
						throw new ServiceRequestException(relationRule.getExceptionMessage());
					}
				}
			}
		}

		return true;
	}

	/**
	 * 工作流检查
	 * 
	 * @param procrtGuid
	 * @param wfName
	 * @param actrtName
	 * @param attachList
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean check(String procrtGuid, String wfName, String actrtName, List<ProcAttach> attachList) throws ServiceRequestException
	{
		boolean result = true;
		if (attachList == null)
		{
			attachList = this.stubService.getWFI().listProcAttach(procrtGuid);
		}
		if (SetUtils.isNullList(attachList))
		{
			throw new ServiceRequestException("", "");
		}
		for (ProcAttach attach : attachList)
		{
			this.check(wfName, actrtName, attach);
		}

		return result;
	}

	/**
	 * Object字段检查
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean check(FoundationObject foundationObject) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(foundationObject.getObjectGuid(), this.stubService);
		List<ClassField> fieldList = this.stubService.getEMM().listFieldOfClass(foundationObject.getObjectGuid().getClassName());
		if (!SetUtils.isNullList(fieldList))
		{
			for (ClassField field : fieldList)
			{
				if (field.getType() != FieldTypeEnum.OBJECT)
				{
					continue;
				}
				String typeValue = field.getTypeValue();
				if (!StringUtils.isNullString(typeValue))
				{
					ClassInfo classInfo = this.stubService.getEMM().getClassByName(typeValue);
					if (classInfo.hasInterface(ModelInterfaceEnum.IUser) || classInfo.hasInterface(ModelInterfaceEnum.IGroup)
							|| classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar) || classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
					{
						continue;
					}
					this.checkObject(field.getName(), foundationObject);
				}
			}
		}
		return true;
	}

	protected boolean check(String serviceTemplateName, FoundationObject foundationObject) throws ServiceRequestException
	{
		List<CheckRule> list = this.stubService.loadDataCheckConditionList(RuleTypeEnum.ERP, serviceTemplateName, null, foundationObject.getObjectGuid().getClassName());
		if (!SetUtils.isNullList(list))
		{
			for (CheckRule rule : list)
			{
				ERPRule erpRule = DataCheckRuleFactory.getRule(rule, foundationObject);
				erpRule.setFoundationObject(foundationObject);

				boolean result = erpRule.check();
				if (!result)
				{
					throw new ServiceRequestException(erpRule.getExceptionMessage());
				}
			}
		}
		return true;
	}

	/**
	 * Object字段检查
	 * 
	 * @param fieldName
	 *            Object字段名
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean checkObject(String fieldName, FoundationObject foundationObject) throws ServiceRequestException
	{
		String guid = (String) foundationObject.get(fieldName);
		String classGuid = (String) foundationObject.get(fieldName + "$CLASS");
		if (!StringUtils.isGuid(guid))
		{
			return true;
		}

		ObjectGuid objectGuid = new ObjectGuid(classGuid, null, guid, null);
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		boolean result = true;
		String ruleName = foundationObject.getObjectGuid().getClassName() + "." + fieldName;
		List<CheckRule> list = this.stubService.loadDataCheckConditionList(RuleTypeEnum.OBJECTFIELD, ruleName, foundationObject.getObjectGuid().getClassName(),
				objectGuid.getClassName());
		if (!SetUtils.isNullList(list))
		{
			FoundationObject end2;
			try
			{
				end2 = this.stubService.getBOAS().getObjectNotCheckAuthor(objectGuid);
			}
			catch (Exception e)
			{
				throw new ServiceRequestException(
						this.stubService.getMSRM().getMSRString("ID_RM_CHECK_ILLEGAL", this.stubService.getUserSignature().getLanguageEnum().name(), e.getMessage()),
						"Unable to get ObjectField object");
			}
			if (end2 == null)
			{
				throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_RM_CHECK_ILLEGAL", this.stubService.getUserSignature().getLanguageEnum().name(),
						"Unable to get ObjectField object"), "Unable to get ObjectField object");
			}

			for (CheckRule rule : list)
			{
				RelationRule relationRule = DataCheckRuleFactory.getRule(rule, foundationObject, end2);
				result = relationRule.check();
				if (!result)
				{
					throw new ServiceRequestException(relationRule.getExceptionMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 工作流检查
	 * 
	 * @param wfName
	 * @param actrtName
	 * @param attach
	 *            流程附件
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean check(String wfName, String actrtName, ProcAttach attach) throws ServiceRequestException
	{
		String classGuid = attach.getInstanceClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		boolean result = true;
		List<CheckRule> list = this.stubService.loadDataCheckConditionList(RuleTypeEnum.WF, wfName, actrtName, classInfo.getName());
		if (!SetUtils.isNullList(list))
		{
			for (CheckRule rule : list)
			{
				ObjectGuid objectGuid = new ObjectGuid(classGuid, null, attach.getInstanceGuid(), null);
				FoundationObject attach_ = this.stubService.getBOAS().getObject(objectGuid);
				WFRule wfRule = DataCheckRuleFactory.getRule(rule, wfName + "." + actrtName, attach_);
				result = wfRule.check();
				if (!result)
				{
					throw new ServiceRequestException(wfRule.getExceptionMessage());
				}
			}
		}

		return true;
	}
}
