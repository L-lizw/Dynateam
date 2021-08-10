/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: StructureStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import dyna.app.core.sch.Scheduler;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.CheckConnectUtil;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.EOSS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Wanglei
 * 
 */
public class StructureStub extends AbstractServiceStub<BOASImpl>
{
	/**
	 * @param context
	 * @param service
	 */
	public StructureStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	public List<StructureObject> listStructureObject(ObjectGuid viewObjectGuid, ObjectGuid end2ObjectGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		List<StructureObject> structureObjectList;
		try
		{
			FoundationObject foundationObject = this.stubService.getObject(viewObjectGuid);
			if (foundationObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			ViewObject viewObject = new ViewObject(foundationObject);
			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());

			structureObjectList = DataServer.getRelationService().listObjectOfRelation(viewObjectGuid, relationTemplate.getGuid(), null,
					Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, null);

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			if (SetUtils.isNullList(structureObjectList))
			{
				return null;
			}
			if (StringUtils.isNull(end2ObjectGuid.getGuid()))
			{
				for (int i = structureObjectList.size() - 1; i > -1; i--)
				{
					if (!structureObjectList.get(i).getEnd2ObjectGuid().getMasterGuid().equals(end2ObjectGuid.getMasterGuid()))
					{
						structureObjectList.remove(i);
					}
				}

			}
			else
			{
				for (int i = structureObjectList.size() - 1; i > -1; i--)
				{
					if (!structureObjectList.get(i).getEnd2ObjectGuid().getGuid().equals(end2ObjectGuid.getGuid()))
					{
						structureObjectList.remove(i);
					}
				}
			}
			EMM emm = this.stubService.getEMM();

			Set<String> objectFieldNames = null;
			Set<String> codeFieldNames = null;

			for (StructureObject structureObject : structureObjectList)
			{
				DecoratorFactory.decorateStructureObject(structureObject, objectFieldNames, codeFieldNames, emm, bmGuid);
			}
			DecoratorFactory.decorateStructureObject(objectFieldNames, structureObjectList, this.stubService.getEMM(), sessionId);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		return structureObjectList;
	}

	protected StructureObject saveStructure(StructureObject structureObject) throws ServiceRequestException
	{
		return this.saveStructure(structureObject, true);
	}

	public StructureObject saveStructure(StructureObject structureObject, boolean isCheckAcl) throws ServiceRequestException
	{
		String sessionId = this.stubService.getUserSignature().getCredential();
		try
		{
			ViewObject viewObject = this.stubService.getRelationStub().getRelation(structureObject.getViewObjectGuid(), isCheckAcl);

			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());
			boolean isCheckCycle = false;
			boolean ischeckEnd2 = false;
			String originalEnd2MasterGuid = (String) structureObject.getOriginalValue("END2$MASTERFK");
			String newEnd2MasterGuid = structureObject.getEnd2ObjectGuid().getMasterGuid();
			if (!newEnd2MasterGuid.equals(originalEnd2MasterGuid))
			{
				ischeckEnd2 = true;
			}
			if (relationTemplate.getStructureModel() == RelationTemplateTypeEnum.TREE)
			{
				isCheckCycle = ischeckEnd2;
			}
			if (ischeckEnd2)
			{
				// 不允许有相同的end2
				List<String> end2MasterGuidList = new ArrayList<String>();
				if (!relationTemplate.isIncorporatedMaster())
				{
					List<StructureObject> bomList = DataServer.getRelationService().listSimpleStructureOfRelation(viewObject.getObjectGuid(), relationTemplate.getViewClassGuid(),
							relationTemplate.getStructureClassGuid(), sessionId);
					if (bomList != null)
					{
						for (StructureObject stru : bomList)
						{
							end2MasterGuidList.add(stru.getEnd2ObjectGuid().getMasterGuid());
						}
						if (end2MasterGuidList.contains(structureObject.getEnd2ObjectGuid().getMasterGuid()))
						{
							FoundationObject objectByGuid = this.stubService.getFoundationStub().getObjectByGuid(structureObject.getEnd2ObjectGuid(), false);

							throw new ServiceRequestException("ID_APP_BOMEDIT_CANT_CONECT_BOMVIEW", "end2 is not relation", null, objectByGuid.getFullName());
						}
					}
				}

			}
			StructureObject saveStructureInner = this.saveStructureInner(viewObject, structureObject, relationTemplate, isCheckAcl);
			if (isCheckCycle)
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
			return saveStructureInner;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public StructureObject saveStructureInner(ViewObject viewObject, StructureObject structureObject, RelationTemplateInfo relationTemplate, boolean isCheckAcl)
			throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{

			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			if (relationTemplate == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "template not found");
			}

			if (isCheckAcl && SystemStatusEnum.PRE.equals(viewObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			if (SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}
			if (viewObject.getEnd1ObjectGuid().getMasterGuid().equals(structureObject.getEnd2ObjectGuid().getMasterGuid())
					|| viewObject.getEnd1ObjectGuid().getGuid().equals(structureObject.getEnd2ObjectGuid().getGuid()))
			{
				throw new DynaDataExceptionAll("linkBOM() youself.", null, DataExceptionEnum.DS_RELATION_SELF);
			}

			this.checkFoundationFieldRegex(structureObject);

			// DCR规则检查
			List<ObjectGuid> end2ObjectGuidList = new ArrayList<>();
			end2ObjectGuidList.add(structureObject.getEnd2ObjectGuid());
			viewObject.getTemplateID();
			this.stubService.getDCR().check(structureObject.getEnd1ObjectGuid(), end2ObjectGuidList, relationTemplate.getName(), RuleTypeEnum.RELATION);

			ClassStub.decorateObjectGuid(structureObject.getObjectGuid(), this.stubService);
			EOSS eoss = this.stubService.getEOSS();
			// !invoke update.before
			eoss.executeUpdateBeforeEvent(structureObject);

			structureObject = DataServer.getRelationService().save(structureObject, "2".equals(relationTemplate.getEnd2Type()),
					Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId());
			eoss.executeUpdateAfterEvent(structureObject);

			return structureObject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void saveStructureBatch(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList,
			String procRtGuid) throws ServiceRequestException
	{
		StructureObject so = null;

		try
		{
			boolean isCheckAcl = !StringUtils.isGuid(procRtGuid);
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 需要删除
			if (!SetUtils.isNullList(unlinkList) && isCheckAcl)
			{
				for (FoundationObject fo : unlinkList)
				{
					// 删除关联
					so = (StructureObject) fo.get(StructureObject.STRUCTURE_CLASS_NAME);
					this.stubService.unlink(so);
				}
			}

			// 需要更新
			if (!SetUtils.isNullList(updateList))
			{
				for (FoundationObject fo : updateList)
				{
					// 更新关联关系
					so = (StructureObject) fo.get(StructureObject.STRUCTURE_CLASS_NAME);
					this.stubService.getStructureStub().saveStructure(so, isCheckAcl);
				}
			}

			// 需要新增并关联
			if (!SetUtils.isNullList(linkList))
			{
				for (FoundationObject fo : linkList)
				{
					// 创建关联关系
					so = (StructureObject) fo.get(StructureObject.STRUCTURE_CLASS_NAME);
					this.stubService.getRelationLinkStub().link(viewObjectGuid, fo.getObjectGuid(), so, isCheckAcl, procRtGuid);
				}
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
	}

	private void checkFoundationFieldRegex(StructureObject structureObject) throws ServiceRequestException
	{
		String classGuid = structureObject.getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(classInfo.getName());
		if (!SetUtils.isNullList(listFieldOfClass))
		{
			for (ClassField field : listFieldOfClass)
			{
				if (field.getType() == FieldTypeEnum.STRING && !StringUtils.isNullString(field.getValidityRegex()))
				{
					Pattern pattern = Pattern.compile(field.getValidityRegex());
					boolean matches = pattern.matcher(StringUtils.convertNULLtoString(structureObject.get(field.getName()))).matches();
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

	public void saveStructure4Detail(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList,
			String procRtGuid) throws ServiceRequestException
	{
		StructureObject so;

		try
		{
			boolean isCheckAcl = !StringUtils.isGuid(procRtGuid);
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 需要删除
			if (!SetUtils.isNullList(unlinkList) && isCheckAcl)
			{
				for (FoundationObject fo : unlinkList)
				{
					// 删除关联
					so = (StructureObject) fo.get(StructureObject.STRUCTURE_CLASS_NAME);
					this.stubService.unlink(so);
					// 删除实例
					this.stubService.deleteFoundationObject(fo.getObjectGuid().getGuid(), fo.getObjectGuid().getClassGuid());
				}
			}

			// 需要更新
			if (!SetUtils.isNullList(updateList))
			{
				for (FoundationObject fo : updateList)
				{
					// 更新关联关系
					so = (StructureObject) fo.get(StructureObject.STRUCTURE_CLASS_NAME);
					this.stubService.getStructureStub().saveStructure(so, isCheckAcl);
					// 更新实例
					this.stubService.getFSaverStub().saveObject(fo, false, Constants.isSupervisor(isCheckAcl, this.stubService), false, procRtGuid);
				}
			}

			// 需要新增并关联
			if (!SetUtils.isNullList(linkList))
			{
				for (FoundationObject fo : linkList)
				{
					// 创建实例
					FoundationObject foundationObject = this.stubService.getFSaverStub().createObject(fo, false);
					// 创建关联关系
					so = (StructureObject) fo.get(StructureObject.STRUCTURE_CLASS_NAME);
					this.stubService.getRelationLinkStub().link(viewObjectGuid, foundationObject.getObjectGuid(), so, isCheckAcl, procRtGuid);
				}
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

	}

	public StructureObject getStructureObject(ObjectGuid objectGuid, SearchCondition searchCondition, boolean isCheckAcl, DataRule dataRule) throws ServiceRequestException
	{
		StructureObject structure;
		String sessionId = this.stubService.getSignature().getCredential();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		try
		{
			structure = DataServer.getRelationService().getStructureObject(objectGuid, isCheckAcl, sessionId);
			if (structure == null)
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
			DecoratorFactory.decorateStructureObject(structure, objectFieldNames, codeFieldNames, emm, bmGuid);
			DecoratorFactory.ofd.decorateWithField(objectFieldNames, structure, this.stubService.getEMM(), sessionId, false);
			String templeteId = (String) structure.get(ViewObject.TEMPLATE_ID);
			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(templeteId);
			if (relationTemplate != null)
			{
				ObjectGuid end2ObjectGuid = structure.getEnd2ObjectGuid();
				FoundationObject end2;
				if ("2".equals(relationTemplate.getEnd2Type()))
				{
					end2 = DataServer.getInstanceService().getSystemFieldInfo(end2ObjectGuid.getGuid(), end2ObjectGuid.getClassGuid(), isCheckAcl, sessionId);
				}
				else if ("1".equals(relationTemplate.getEnd2Type()))
				{
					end2 = DataServer.getInstanceService().getWipSystemFieldInfoByMaster(end2ObjectGuid.getMasterGuid(), end2ObjectGuid.getClassGuid(), isCheckAcl, sessionId);
				}
				else
				{
					if (dataRule == null || dataRule.getSystemStatus() == SystemStatusEnum.WIP)
					{
						end2 = DataServer.getInstanceService().getWipSystemFieldInfoByMaster(end2ObjectGuid.getMasterGuid(), end2ObjectGuid.getClassGuid(), isCheckAcl, sessionId);
					}
					else
					{
						end2 = DataServer.getInstanceService().getSystemFieldInfoByMasterContext(end2ObjectGuid.getMasterGuid(), end2ObjectGuid.getClassGuid(), dataRule,
								isCheckAcl, sessionId);
					}
				}
				structure.setEnd2UIObject(end2);
				if (end2 != null)
				{
					structure.setEnd2ObjectGuid(end2.getObjectGuid());
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}

		return structure;
	}

	protected StructureObject newStructureObject(String classGuid, String className) throws ServiceRequestException
	{
		return this.stubService.getFoundationStub().newFoundationObject(StructureObject.class, classGuid, className, false, null);
	}

	/**
	 * 变更关系结构中的主对象
	 * 
	 * @param structureObject
	 * @throws ServiceRequestException
	 */
	public void changePrimaryObject(ObjectGuid end1ObjectGuid, String viewName, StructureObject structureObject, boolean isCheckAcl) throws ServiceRequestException
	{
		RelationTemplateInfo relationTemplateByName = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, viewName);

		SearchCondition createSearchConditionForStructure = SearchConditionFactory.createSearchConditionForStructure(relationTemplateByName.getStructureClassName());
		createSearchConditionForStructure.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		createSearchConditionForStructure.addFilter(StructureObject.ISPRIMARY, null, OperateSignEnum.TRUE);

		List<StructureObject> dlist = this.stubService.getRelationStub().listObjectOfRelation(end1ObjectGuid, viewName, createSearchConditionForStructure, null, null, false);

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!SetUtils.isNullList(dlist))
			{

				for (StructureObject structure : dlist)
				{
					if (structure.getObjectGuid().getGuid().equalsIgnoreCase(structureObject.getObjectGuid().getGuid()))
					{
						continue;
					}
					structure.setParmary(null);
					this.saveStructure(structure, isCheckAcl);
				}
			}

			structureObject.setParmary(true);
			this.saveStructure(structureObject, isCheckAcl);
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
}
