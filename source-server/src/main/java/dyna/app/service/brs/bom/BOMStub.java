/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与BOMStructure相关的操作分支
 * Caogc 2010-8-18
 */
package dyna.app.service.brs.bom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import dyna.app.core.sch.Scheduler;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.CheckConnectUtil;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ListRelationTreeUtil;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.BomPreciseType;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.BOMItemDetailSum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.service.acl.AclService;
import dyna.data.service.ins.InstanceService;
import dyna.data.service.relation.RelationService;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.EOSS;
import org.springframework.stereotype.Component;

/**
 * 与BOMStructure相关的操作分支
 * 
 * @author Caogc
 * 
 */
@Component
public class BOMStub extends AbstractServiceStub<BOMSImpl>
{

	public BOMStructure getBOM(ObjectGuid objectGuid, SearchCondition searchCondition, boolean isCheckAcl, DataRule dataRule) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		BOMStructure bomStructure = null;
		String sessionId = this.stubService.getSignature().getCredential();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		try
		{
			bomStructure = DataServer.getRelationService().getBOMStructure(objectGuid, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId);
			if (bomStructure == null)
			{
				return null;
			}

			Set<String> objectFieldNames = null;
			Set<String> codeFieldNames = null;
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			EMM emm = this.stubService.getEMM();
			if (searchCondition != null && searchCondition.getObjectGuid() != null)
			{
				objectFieldNames = emm.getObjectFieldNamesInSC(searchCondition);
				codeFieldNames = emm.getCodeFieldNamesInSC(searchCondition);
			}
			DecoratorFactory.decorateStructureObject(bomStructure, objectFieldNames, codeFieldNames, emm, bmGuid);
			DecoratorFactory.ofd.decorateWithField(objectFieldNames, bomStructure, emm, sessionId, false);
			String templeteId = (String) bomStructure.get(ViewObject.TEMPLATE_ID);
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(templeteId);
			if (bomTemplate != null)
			{
				ObjectGuid end2ObjectGuid = bomStructure.getEnd2ObjectGuid();
				FoundationObject end2;
				if (bomTemplate.getPrecise() == BomPreciseType.PRECISE)
				{
					end2 = ds.getSystemFieldInfo(end2ObjectGuid.getGuid(), end2ObjectGuid.getClassGuid(), isCheckAcl, sessionId);
				}
				else
				{
					if (dataRule == null || dataRule.getSystemStatus() == SystemStatusEnum.WIP)
					{
						end2 = ds.getWipSystemFieldInfoByMaster(end2ObjectGuid.getMasterGuid(), end2ObjectGuid.getClassGuid(), Constants.isSupervisor(isCheckAcl, this.stubService),
								sessionId);
					}
					else
					{
						end2 = ds.getSystemFieldInfoByMasterContext(end2ObjectGuid.getMasterGuid(), end2ObjectGuid.getClassGuid(), dataRule,
								Constants.isSupervisor(isCheckAcl, this.stubService), sessionId);
					}
				}
				bomStructure.setEnd2UIObject(end2);
				if (end2 != null)
				{
					bomStructure.setEnd2ObjectGuid(end2.getObjectGuid());
				}
			}

		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}

		return bomStructure;

	}

	public List<BOMStructure> listBOM(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException
	{
		List<BOMStructure> bomList = null;
		List<BOMStructure> reList = null;
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);

			BOMView bomViewObject = this.stubService.getBOMView(viewObjectGuid);
			if (bomViewObject == null)
			{
				return null;
			}
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
			if (bomTemplate == null)
			{
				return null;
			}

			if (searchCondition == null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(bomTemplate.getStructureClassName());
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(bomTemplate.getStructureClassName(), uiObjectList);
			}

			if (end2SearchCondition != null)
			{
				setEnd2UIToStructureSearchCondition(end2SearchCondition, searchCondition);
			}

			RelationService ds = DataServer.getRelationService();
			String sessionId = this.stubService.getUserSignature().getCredential();
			bomList = ds.listBOMStructure(viewObjectGuid, bomTemplate.getGuid(), dataRule, searchCondition, Constants.isSupervisor(true, this.stubService), sessionId);
			if (SetUtils.isNullList(bomList))
			{
				return null;
			}
			reList = new ArrayList<BOMStructure>();
			this.decoratorResult(bomList, end2SearchCondition, searchCondition, sessionId, null, reList, false, bomViewObject.getEnd1ObjectGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		return reList;
	}

	public List<BOMStructure> listBOM(ObjectGuid end1ObjectGuid, String templateName, SearchCondition searchConditionStructure, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException
	{
		List<BOMStructure> bomList = null;
		List<BOMStructure> reList = null;
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(end1ObjectGuid, this.stubService);

			BOMView bomViewObject = this.stubService.getBOMViewByEND1(end1ObjectGuid, templateName);

			if (bomViewObject == null)
			{
				return null;
			}
			ObjectGuid viewObjectGuid = bomViewObject.getObjectGuid();

			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
			if (bomTemplate == null)
			{
				return null;
			}

			if (searchConditionStructure == null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(bomTemplate.getStructureClassName());
				searchConditionStructure = SearchConditionFactory.createSearchConditionForBOMStructure(bomTemplate.getStructureClassName(), uiObjectList);
			}
			if (end2SearchCondition != null)
			{
				setEnd2UIToStructureSearchCondition(end2SearchCondition, searchConditionStructure);
			}

			RelationService ds = DataServer.getRelationService();
			String sessionId = this.stubService.getUserSignature().getCredential();
			bomList = ds.listBOMStructure(viewObjectGuid, bomTemplate.getGuid(), dataRule, searchConditionStructure, Constants.isSupervisor(true, this.stubService), sessionId);
			if (SetUtils.isNullList(bomList))
			{
				return null;
			}
			reList = new ArrayList<BOMStructure>();
			this.decoratorResult(bomList, end2SearchCondition, searchConditionStructure, sessionId, null, reList, false, bomViewObject.getEnd1ObjectGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		return reList;
	}

	protected List<FoundationObject> listWhereUsed(ObjectGuid objectGuid, String viewName, SearchCondition end1SearchCondition, SearchCondition structureSearchCondition,
			DataRule dataRule, boolean viewHistory) throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();
		List<FoundationObject> foundationObjectList = null;
		try
		{
			List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByName(viewName, false);
			if (SetUtils.isNullList(bomTemplateList))
			{
				return null;
			}
			BOMTemplateInfo bomTemplate = bomTemplateList.get(0);
			String sessionId = this.stubService.getSignature().getCredential();
			List<String> end1FieldList = new ArrayList<String>();
			end1FieldList.add("SPECIFICATION");
			if (end1SearchCondition != null)
			{
				List<String> listField = end1SearchCondition.getResultFieldList();
				if (!SetUtils.isNullList(listField))
				{
					for (String name : listField)
					{
						if (!end1FieldList.contains(name.toUpperCase()))
						{
							end1FieldList.add(name.toUpperCase());
						}
					}
				}
				List<String> list = end1SearchCondition.listResultUINameList();
				ObjectGuid end1ObjectGuid = end1SearchCondition.getObjectGuid();
				if (!SetUtils.isNullList(list) && end1ObjectGuid != null && end1ObjectGuid.getClassName() != null)
				{
					for (String name : list)
					{
						List<UIField> flist = this.stubService.getEMM().listUIFieldByUIObject(end1ObjectGuid.getClassName(), name);
						if (!SetUtils.isNullList(flist))
						{
							for (UIField ff : flist)
							{
								if (!end1FieldList.contains(ff.getName().toUpperCase()))
								{
									end1FieldList.add(ff.getName().toUpperCase());
								}
							}
						}
					}
				}
			}
			else
			{
				BMInfo bizModel = null;
				if ("ALL".equalsIgnoreCase(bomTemplate.getBmGuid()))
				{
					bizModel = this.stubService.getEMM().getCurrentBizModel();
				}
				else
				{
					bizModel = this.stubService.getEMM().getBizModel(bomTemplate.getBmGuid());
				}
				if (bizModel == null)
				{
					return null;
				}
				BOInfo end1ClassInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bizModel.getGuid(), bomTemplate.getEnd1BoName());
				if (end1ClassInfo == null)
				{
					return null;
				}
				String end1ClassName = end1ClassInfo.getClassName();
				end1SearchCondition = SearchConditionFactory.createSearchCondition4Class(end1ClassName, null, false);
			}

			List<String> structureFieldList = new ArrayList<String>();
			if (structureSearchCondition != null)
			{
				structureFieldList.addAll(structureSearchCondition.getResultFieldList());
			}
			else
			{
				structureSearchCondition = SearchConditionFactory.createSearchCondition4Class(bomTemplate.getStructureClassName(), null, false);
			}
			foundationObjectList = ds.listWhereUsed(objectGuid.getObjectGuid(), bomTemplate.getName(), bomTemplate.getViewClassName(),
					bomTemplate.getPrecise() == BomPreciseType.PRECISE, end1SearchCondition, structureSearchCondition, viewHistory, Constants.isSupervisor(true, this.stubService),
					sessionId);
			if (SetUtils.isNullList(foundationObjectList))
			{
				return null;
			}
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			EMM emm = this.stubService.getEMM();

			Set<String> objectFieldSet = new HashSet<String>();
			Set<String> codeFieldSet = new HashSet<String>();
			if (end1SearchCondition != null)
			{
				String className = end1SearchCondition.getObjectGuid().getClassName();
				if (className == null)
				{
					className = emm.getClassByGuid(end1SearchCondition.getObjectGuid().getClassGuid()).getName();
				}
				objectFieldSet.addAll(emm.getObjectFieldNames(className, end1FieldList));
				codeFieldSet.addAll(emm.getCodeFieldNames(className, end1FieldList));
			}
			else
			{
				codeFieldSet.add(SystemClassFieldEnum.CLASSIFICATION.getName());
				objectFieldSet.add(SystemClassFieldEnum.UPDATEUSER.getName());
				objectFieldSet.add(SystemClassFieldEnum.CREATEUSER.getName());
				objectFieldSet.add(SystemClassFieldEnum.OBSOLETEUSER.getName());
				objectFieldSet.add(SystemClassFieldEnum.CHECKOUTUSER.getName());
				objectFieldSet.add(SystemClassFieldEnum.OWNERUSER.getName());
				objectFieldSet.add(SystemClassFieldEnum.OWNERGROUP.getName());
				objectFieldSet.add(SystemClassFieldEnum.LOCATIONLIB.getName());
				objectFieldSet.add(SystemClassFieldEnum.COMMITFOLDER.getName());
			}

			for (FoundationObject fObject : foundationObjectList)
			{
				DecoratorFactory.decorateFoundationObject(objectFieldSet, fObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(codeFieldSet, fObject, emm, bmGuid);
			}

			objectFieldSet.clear();
			if (end1FieldList != null)
			{
				objectFieldSet.addAll(end1FieldList);
			}
			DecoratorFactory.decorateFoundationObject(objectFieldSet, foundationObjectList, emm, sessionId);
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}

		return foundationObjectList;
	}

	protected BOMStructure saveBOM(BOMStructure bomStructure) throws ServiceRequestException
	{
		return this.saveBOM(bomStructure, true, true, true);
	}

	public BOMStructure saveBOM(BOMStructure bomStructure, boolean isCheckAuth, boolean isCheckField, boolean isCheckConnect) throws ServiceRequestException
	{
		// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
		ClassStub.decorateObjectGuid(bomStructure.getObjectGuid(), this.stubService);
		String sessionId = this.stubService.getUserSignature().getCredential();
		try
		{
			BOMView viewObject = this.stubService.getBomViewStub().getBOMView(bomStructure.getViewObjectGuid(), true);
			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			if (SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()) || SystemStatusEnum.PRE.equals(viewObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(viewObject.getTemplateID());

			boolean isCheckEnd2Unique = bomStructure.isChanged("END2$MASTER");
			if (isCheckEnd2Unique)
			{
				// 不允许有相同的end2
				List<String> end2MasterGuidList = new ArrayList<String>();
				if (!bomTemplate.isIncorporatedMaster())
				{
					List<StructureObject> bomList = DataServer.getRelationService().listSimpleStructureOfRelation(viewObject.getObjectGuid(), bomTemplate.getViewClassGuid(),
							bomTemplate.getStructureClassGuid(), sessionId);
					if (bomList != null)
					{
						for (StructureObject stru : bomList)
						{
							end2MasterGuidList.add(stru.getEnd2ObjectGuid().getMasterGuid());
						}
						if (end2MasterGuidList.contains(bomStructure.getEnd2ObjectGuid().getMasterGuid()))
						{
							FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(bomStructure.getEnd2ObjectGuid(), false);

							throw new ServiceRequestException("ID_APP_BOMEDIT_CANT_CONECT_BOMVIEW", "end2 is not relation", null, objectByGuid.getFullName());
						}
					}
				}

			}
			this.saveBOMInner(bomStructure, bomTemplate.getPrecise() == BomPreciseType.PRECISE, isCheckAuth, isCheckField);

			if (isCheckConnect)
			{
				Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				if (schedule == null)
				{
					this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
					schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				}
				CheckConnectUtil util = new CheckConnectUtil(this.stubService.getEMM(), schedule, this.stubService.getUserSignature().getCredential(),
						ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, false);
				if (util.checkConntc(viewObject.getEnd1ObjectGuid()))
				{
					throw new DynaDataExceptionAll("connect by error ", null, DataExceptionEnum.DS_CONNECT_BY_ERROR);
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return bomStructure;
	}

	protected BOMStructure saveBOMInner(BOMStructure bomStructure, boolean isPrecise, boolean isCheckAuth, boolean isCheckField) throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();

		// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
		ClassStub.decorateObjectGuid(bomStructure.getObjectGuid(), this.stubService);

		try
		{
			BOMView bomView = this.stubService.getBOMView(bomStructure.getViewObjectGuid());
			if (bomView.getEnd1ObjectGuid().getMasterGuid().equals(bomStructure.getEnd2ObjectGuid().getMasterGuid())
					|| bomView.getEnd1ObjectGuid().getGuid().equals(bomStructure.getEnd2ObjectGuid().getGuid()))
			{
				throw new DynaDataExceptionAll("linkBOM() youself.", null, DataExceptionEnum.DS_RELATION_SELF);
			}

			// DCR规则检查
			List<ObjectGuid> end2ObjectGuidList = new ArrayList<ObjectGuid>();
			end2ObjectGuidList.add(bomStructure.getEnd2ObjectGuid());
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(bomView.getTemplateID());
			this.stubService.getDCR().check(bomView.getEnd1ObjectGuid(), end2ObjectGuidList, bomTemplate.getName(), RuleTypeEnum.BOM);

			String sessionId = this.stubService.getSignature().getCredential();
			EOSS eoss = this.stubService.getEOSS();
			// !invoke update.before
			eoss.executeUpdateBeforeEvent(bomStructure);

			if (isCheckField)
			{
				String message = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().checkDynaObjectField(bomStructure);
				if (!StringUtils.isNullString(message))
				{
					throw new ServiceRequestException(message);
				}
			}
			String originalEnd2MasterGuid = (String) bomStructure.getOriginalValue("END2$MASTERFK");
			String newEnd2MasterGuid = bomStructure.getEnd2ObjectGuid().getMasterGuid();
			if (newEnd2MasterGuid.equals(originalEnd2MasterGuid) == false)
			{
				String key = BOMStructure.BOMKEY;
				bomStructure.put(key, UUID.randomUUID().toString().replace("-", "").toUpperCase());
			}

			if (bomView.getEnd1ObjectGuid().getMasterGuid().equals(newEnd2MasterGuid))
			{
				throw new ServiceRequestException("ID_DS_RELATION_SELF", "cannot link with itself", null);
			}

			this.checkFoundationFieldRegex(bomStructure);

			bomStructure = (BOMStructure) ds.save(bomStructure, isPrecise, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId,
					this.stubService.getFixedTransactionId());

			eoss.executeUpdateAfterEvent(bomStructure);
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return bomStructure;
	}

	public List<BOMStructure> listBOMForList(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAcl) throws ServiceRequestException
	{
		List<BOMStructure> reList = null;

		if (viewObjectGuid == null)
		{
			return null;
		}

		AclService aclService = DataServer.getAclService();

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);
			// I13167/A.2-BOMStructure添加类别字段，设置类别值并且保存后，在BOM界面，类别值没有显示出来
			BOMView bomViewObject = this.stubService.getBOMView(viewObjectGuid);
			if (bomViewObject == null)
			{
				return null;
			}
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
			if (bomTemplate == null)
			{
				return null;
			}

			if (searchCondition == null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(bomTemplate.getStructureClassName());
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(bomTemplate.getStructureClassName(), uiObjectList);
			}

			if (end2SearchCondition != null)
			{
				setEnd2UIToStructureSearchCondition(end2SearchCondition, searchCondition);
			}
			Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
			if (schedule == null)
			{
				this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
				schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
			}
			ListRelationTreeUtil util = new ListRelationTreeUtil(this.stubService, schedule);
			Map<String, List<BOMStructure>> bomMap = util.listBOMForTree(bomViewObject.getEnd1ObjectGuid(), bomTemplate.getName(), searchCondition, end2SearchCondition, dataRule,
					-1);

			if (SetUtils.isNullMap(bomMap))
			{
				return null;
			}
			// 计算权限
			if (Constants.isSupervisor(isCheckAcl, this.stubService))
			{
				Set<String> authList = new HashSet<String>();
				authList.add(bomViewObject.getGuid());
				authList.add(bomViewObject.getEnd1ObjectGuid().getGuid());
				for (List<BOMStructure> list : bomMap.values())
				{
					if (!SetUtils.isNullList(list))
					{
						StructureObject bomstru = list.get(0);
						if (!authList.contains(bomstru.getEnd1ObjectGuid().getGuid()))
						{
							if (!aclService.hasAuthority(bomstru.getEnd1ObjectGuid(), AuthorityEnum.READ, sessionId))
							{
								throw new ServiceRequestException(DataExceptionEnum.DS_NO_READ_AUTH.getMsrId(), "", null, bomViewObject.getFullName());
							}
							if (!aclService.hasAuthority(bomstru.getViewObjectGuid(), AuthorityEnum.READ, sessionId))
							{
								throw new ServiceRequestException(DataExceptionEnum.DS_NO_READ_AUTH.getMsrId(), "", null, bomViewObject.getFullName());
							}
							authList.add(bomstru.getEnd1ObjectGuid().getGuid());
							authList.add(bomstru.getViewObjectGuid().getGuid());
						}

					}
				}
			}
			// 汇总
			BOMItemDetailSum xBOMItemDetailSum = new BOMItemDetailSum(bomViewObject.getEnd1ObjectGuid().getGuid(), bomMap);
			xBOMItemDetailSum.prepareTreeMap();
			xBOMItemDetailSum.calculateTree();
			List<BOMStructure> newBOMList = xBOMItemDetailSum.getSummaryList();
			// 过滤
			this.filterBOMList(newBOMList, end2SearchCondition);
			reList = new ArrayList<BOMStructure>();
			reList.addAll(newBOMList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return reList;
	}

	private void filterBOMList(List<BOMStructure> bomList, SearchCondition end2SearchCondition) throws ServiceRequestException
	{
		if (end2SearchCondition == null)
		{
			return;
		}

		if (!StringUtils.isNullString(end2SearchCondition.getObjectGuid().getClassName()))
		{
			ClassInfo superInfo = this.stubService.getEMM().getClassByName(end2SearchCondition.getObjectGuid().getClassName());
			List<ClassInfo> end2ClassList = this.stubService.getEMM().listSubClass(end2SearchCondition.getObjectGuid().getClassName(),
					end2SearchCondition.getObjectGuid().getClassGuid(), true, null);
			Set<String> end2ClassGuidList = new HashSet<String>();
			end2ClassGuidList.add(superInfo.getGuid());
			if (end2ClassList != null)
			{
				for (ClassInfo info : end2ClassList)
				{
					end2ClassGuidList.add(info.getGuid());
				}
			}
			Set<String> end2ClfGuidList = new HashSet<String>();
			if (!StringUtils.isNullString(end2SearchCondition.getClassification()))
			{
				end2ClfGuidList.add(end2SearchCondition.getClassification());
				List<CodeItemInfo> clfList = this.stubService.getEMM().listLeafCodeItemInfoByDatail(end2SearchCondition.getClassification());
				if (clfList != null)
				{
					for (CodeItemInfo info : clfList)
					{
						end2ClfGuidList.add(info.getGuid());
					}
				}
			}
			for (int i = bomList.size() - 1; i > -1; i--)
			{
				BOMStructure stru = bomList.get(i);
				FoundationObject foundationObject = stru.getEnd2UIObject();
				if (end2ClassGuidList.contains(foundationObject.getObjectGuid().getClassGuid()))
				{
					if (end2ClfGuidList.isEmpty() || end2ClfGuidList.contains(foundationObject.getClassificationGuid()))
					{
						String searchValue = end2SearchCondition.getSearchValue();
						if (!StringUtils.isNullString(searchValue))
						{
							String id = foundationObject.getId();
							String name = foundationObject.getName();
							String alterId = foundationObject.getAlterId();
							if (!StringUtils.isNullString(id))
							{
								if (id.contains(searchValue))
								{
									continue;
								}
							}
							if (!StringUtils.isNullString(name))
							{
								if (name.contains(searchValue))
								{
									continue;
								}
							}
							if (!StringUtils.isNullString(alterId))
							{
								if (alterId.contains(searchValue))
								{
									continue;
								}
							}
							bomList.remove(i);
						}
					}
					else
					{
						bomList.remove(i);
					}
				}
				else
				{
					bomList.remove(i);
				}
			}
		}
	}

	public Map<String, List<BOMStructure>> listBOMForTreeCheckACL(ObjectGuid viewObjectGuid, int level, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, boolean isCheckAcl) throws ServiceRequestException
	{
		Map<String, List<BOMStructure>> resultMap = this.listBOMForTree(viewObjectGuid, level, searchCondition, end2SearchCondition, dataRule, false);
		if (!Constants.isSupervisor(isCheckAcl, this.stubService))
		{
			return resultMap;
		}

		String sessionId = this.stubService.getSignature().getCredential();
		AclService aclService = DataServer.getAclService();
		Map<String, Boolean> aclValueMap = new HashMap<>();
		if (!SetUtils.isNullMap(resultMap))// O23205
		{
			for (List<BOMStructure> list : resultMap.values())
			{
				if (!SetUtils.isNullList(list))
				{
					for (BOMStructure bomStructure : list)
					{
						ObjectGuid strucObjectGuid = bomStructure.getObjectGuid();
						ObjectGuid end2ObjectGuid = bomStructure.getEnd2ObjectGuid();
						ClassStub.decorateObjectGuid(strucObjectGuid, this.stubService);
						ClassStub.decorateObjectGuid(end2ObjectGuid, this.stubService);
						if (aclValueMap.containsKey(bomStructure.getViewObjectGuid().getGuid()) == false)
						{
							aclValueMap.put(bomStructure.getViewObjectGuid().getGuid(), aclService.hasAuthority(bomStructure.getViewObjectGuid(), AuthorityEnum.READ, sessionId));
						}
						if (aclValueMap.get(bomStructure.getViewObjectGuid().getGuid()))
						{
							if (aclValueMap.containsKey(end2ObjectGuid.getGuid()) == false)
							{
								aclValueMap.put(end2ObjectGuid.getGuid(), aclService.hasAuthority(end2ObjectGuid, AuthorityEnum.READ, sessionId));
							}
							bomStructure.getEnd2ObjectGuid().setHasAuth(aclValueMap.get(end2ObjectGuid.getGuid()));
						}
						else
						{
							bomStructure.getEnd2ObjectGuid().setHasAuth(false);
						}
					}
				}
			}
		}

		return resultMap;
	}

	public Map<String, List<BOMStructure>> listBOMForTree(ObjectGuid viewObjectGuid, int level, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, boolean isCheckAcl) throws ServiceRequestException
	{
		if (viewObjectGuid == null)
		{
			return null;
		}

		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);
			BOMView bomViewObject = this.stubService.getBOMView(viewObjectGuid);
			if (bomViewObject == null)
			{
				return null;
			}
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
			if (bomTemplate == null)
			{
				return null;
			}

			if (searchCondition == null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(bomTemplate.getStructureClassName());
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(bomTemplate.getStructureClassName(), uiObjectList);
			}

			if (end2SearchCondition != null)
			{
				setEnd2UIToStructureSearchCondition(end2SearchCondition, searchCondition);
			}
			Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
			if (schedule == null)
			{
				this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
				schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
			}
			ListRelationTreeUtil util = new ListRelationTreeUtil(this.stubService, schedule);
			return util.listBOMForTree(bomViewObject.getEnd1ObjectGuid(), bomTemplate.getName(), searchCondition, end2SearchCondition, dataRule, level);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected void unLinkAllBOMStructure(ObjectGuid end1ObjectGuid, String viewName) throws ServiceRequestException
	{
		// DataService ds = ServerFactory.getDataService();
		// String sessionId = this.stubService.getSignature().getCredential();

		// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
		ClassStub.decorateObjectGuid(end1ObjectGuid, this.stubService);

		try
		{
			ClassStub.decorateObjectGuid(end1ObjectGuid, this.stubService);

			BOMView bomViewObject = this.stubService.getBOMViewByEND1(end1ObjectGuid, viewName);

			if (bomViewObject != null)
			{
				ObjectGuid viewObjectGuid = bomViewObject.getObjectGuid();

				BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
				if (bomTemplate != null)
				{
					RelationService ds = DataServer.getRelationService();
					String sessionId = this.stubService.getUserSignature().getCredential();
					List<BOMStructure> bomStructureList = ds.listBOMStructure(viewObjectGuid, bomTemplate.getGuid(), null, null, Constants.isSupervisor(true, this.stubService),
							sessionId);
//					DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
					if (!SetUtils.isNullList(bomStructureList))
					{
						for (BOMStructure bomStructure : bomStructureList)
						{
							this.stubService.unlinkBOM(bomStructure);
						}
					}
//					DataServer.getTransactionManager().commitTransaction();
				}
			}

		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	protected List<FoundationObject> listFoundationObjectOfBOMView(ObjectGuid viewObjectGuid, SearchCondition end2SearchCondition, DataRule dataRule) throws ServiceRequestException
	{
		List<FoundationObject> foundationObjectList = null;
		List<BOMStructure> bomList = null;
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);

			BOMView bomViewObject = this.stubService.getBOMView(viewObjectGuid);
			if (bomViewObject == null)
			{
				return null;
			}
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
			if (bomTemplate == null)
			{
				return null;
			}

			SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(bomTemplate.getStructureClassName(), null);
			if (end2SearchCondition != null)
			{
				setEnd2UIToStructureSearchCondition(end2SearchCondition, searchCondition);
			}

			RelationService ds = DataServer.getRelationService();
			String sessionId = this.stubService.getUserSignature().getCredential();
			bomList = ds.listBOMStructure(viewObjectGuid, bomTemplate.getGuid(), dataRule, searchCondition, Constants.isSupervisor(true, this.stubService), sessionId);
			if (SetUtils.isNullList(bomList))
			{
				return null;
			}
			foundationObjectList = new ArrayList<FoundationObject>();
			this.decoratorResult(bomList, end2SearchCondition, searchCondition, sessionId, foundationObjectList, null, true, bomViewObject.getEnd1ObjectGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}

		return foundationObjectList;
	}

	protected BOMStructure newBOMStructure(String classGuid, String className) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().newFoundationObject(BOMStructure.class, classGuid, className, false, null);
	}

	/**
	 * 将endUI上的字段加上前缀放入structureSearchCondition
	 * 
	 * @param end2SearchCondition
	 * @param structureSearchCondition
	 * @throws ServiceRequestException
	 */
	private void setEnd2UIToStructureSearchCondition(SearchCondition end2SearchCondition, SearchCondition structureSearchCondition) throws ServiceRequestException
	{
		if (structureSearchCondition != null)
		{
			if (end2SearchCondition != null)
			{
				List<String> listField = end2SearchCondition.getResultFieldList();
				if (!SetUtils.isNullList(listField))
				{
					for (String name : listField)
					{
						structureSearchCondition.addResultField(ViewObject.PREFIX_END2 + name);
					}
				}
				List<String> list = end2SearchCondition.listResultUINameList();
				ObjectGuid end2ObjectGuid = end2SearchCondition.getObjectGuid();
				if (!SetUtils.isNullList(list) && end2ObjectGuid != null && end2ObjectGuid.getClassName() != null)
				{
					for (String name : list)
					{
						List<UIField> flist = this.stubService.getEMM().listUIFieldByUIObject(end2ObjectGuid.getClassName(), name);
						if (!SetUtils.isNullList(flist))
						{
							for (UIField ff : flist)
							{
								structureSearchCondition.addResultField(ViewObject.PREFIX_END2 + ff.getName());
							}
						}
					}
				}

			}
		}

	}

	/**
	 * 装饰结果
	 * 
	 * @param foList
	 * @param end2SearchCondition
	 * @param soList
	 * @param structureSearchCondition
	 * @param retList
	 * @throws ServiceRequestException
	 * @throws DecorateException
	 */
	private void decoratorResult(List<BOMStructure> resultList, SearchCondition end2SearchCondition, SearchCondition structureSearchCondition, String sessionId,
			List<FoundationObject> retList, List<BOMStructure> sretList, boolean isFoundation, ObjectGuid end1ObjectGuid) throws ServiceRequestException, DecorateException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		EMM emm = this.stubService.getEMM();
		Set<String> objectFieldNames = null;
		Set<String> codeFieldNames = null;
		if (structureSearchCondition != null && structureSearchCondition.getObjectGuid() != null)
		{
			objectFieldNames = emm.getObjectFieldNamesInSC(structureSearchCondition);
			codeFieldNames = emm.getCodeFieldNamesInSC(structureSearchCondition);
		}

		Set<String> objectFieldNamesEnd2 = null;
		Set<String> codeFieldNamesEnd2 = null;
		if (end2SearchCondition != null && end2SearchCondition.getObjectGuid() != null)
		{
			objectFieldNamesEnd2 = emm.getObjectFieldNamesInSC(end2SearchCondition);
			codeFieldNamesEnd2 = emm.getCodeFieldNamesInSC(end2SearchCondition);
		}

		if (!SetUtils.isNullList(resultList))
		{
			List<FoundationObject> foList = new LinkedList<FoundationObject>();
			List<BOMStructure> soList = new LinkedList<BOMStructure>();
			for (int i = 0; i < resultList.size(); i++)
			{
				BOMStructure structureObject = resultList.get(i);
				structureObject.setEnd1ObjectGuid(end1ObjectGuid);

				FoundationObject foundationObject = (FoundationObject) structureObject.get(ViewObject.PREFIX_END2);
				DecoratorFactory.decorateFoundationObject(objectFieldNamesEnd2, foundationObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(codeFieldNamesEnd2, foundationObject, emm, bmGuid);

				DecoratorFactory.decorateStructureObject(structureObject, objectFieldNames, codeFieldNames, emm, bmGuid);

				foList.add(foundationObject);
				soList.add(structureObject);
				structureObject.clear(ViewObject.PREFIX_END2);
			}
			DecoratorFactory.decorateBOMStructure(objectFieldNames, soList, emm, sessionId);
			DecoratorFactory.decorateFoundationObject(objectFieldNamesEnd2, foList, emm, sessionId);

			for (int i = 0; i < foList.size(); i++)
			{
				FoundationObject foundationObject = foList.get(i);
				BOMStructure structureObject = soList.get(i);
				if (isFoundation)
				{
					foundationObject.put(StructureObject.STRUCTURE_CLASS_NAME, structureObject);
					retList.add(foundationObject);
				}
				else
				{
					structureObject.put(BOMStructure.END2_UI_OBJECT, foundationObject);
					structureObject.put("TITLE", foundationObject.getFullName());
					sretList.add(structureObject);
				}
			}
		}
	}

	protected List<String> getHasAuthFieldList(String className, List<String> resultUINameList) throws ServiceRequestException
	{
		List<String> hasAuthFieldList = new ArrayList<String>();
		hasAuthFieldList.add(SystemClassFieldEnum.ID.getName());
		hasAuthFieldList.add(SystemClassFieldEnum.NAME.getName());

		if (!SetUtils.isNullList(resultUINameList))
		{
			for (String resultUiName : resultUINameList)
			{
				UIObjectInfo uiObject = this.stubService.getEMM().getUIObjectByName(className, resultUiName);
				List<UIField> uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
				if (uiObject != null && uiFieldList != null)
				{
					if (!SetUtils.isNullList(uiFieldList))
					{
						for (UIField uiField : uiFieldList)
						{
							if (uiField.isShowValWhenNoAuth() && !hasAuthFieldList.contains(uiField.getName()))
							{
								hasAuthFieldList.add(uiField.getName());
							}
						}
					}
				}
			}
		}

		return hasAuthFieldList;
	}

	public void updateUHasBOM(List<FoundationObject> end1List, String bmGuid) throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();
		if (!SetUtils.isNullList(end1List))
		{
			for (FoundationObject foundationObject : end1List)
			{
				List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByEND1(foundationObject.getObjectGuid(), bmGuid);
				if (!SetUtils.isNullList(bomTemplateList))
				{
					for (BOMTemplateInfo bomTemplate : bomTemplateList)
					{
						ds.updateUHasBOM(foundationObject.getObjectGuid(), bomTemplate.getGuid(), foundationObject.getName(), this.stubService.getFixedTransactionId());
					}
				}
			}
		}
	}

	private void checkFoundationFieldRegex(BOMStructure bomStructure) throws ServiceRequestException
	{
		String classGuid = bomStructure.getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(classInfo.getName());
		if (!SetUtils.isNullList(listFieldOfClass))
		{
			for (ClassField field : listFieldOfClass)
			{
				if (field.getType() == FieldTypeEnum.STRING && !StringUtils.isNullString(field.getValidityRegex()))
				{
					Pattern pattern = Pattern.compile(field.getValidityRegex());
					boolean matches = pattern.matcher(StringUtils.convertNULLtoString(bomStructure.get(field.getName()))).matches();
					if (matches)
					{
						UIField uiField = this.stubService.getEMM().getUIFieldByName(classInfo.getName(), field.getName());
						String title = uiField == null ? field.getName() : uiField.getTitle(this.stubService.getUserSignature().getLanguageEnum());
						throw new ServiceRequestException("ID_CLIENT_VALIDATOR_REGEXLEGAL", "field value ilegal.", null, title);
					}
				}
			}
		}
	}
}
