/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与relation相关的操作分支
 * Caogc 2010-8-18
 */
package dyna.app.service.brs.boas;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassRelationInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 与relation相关的操作分支
 * 
 * @author Caogc
 * 
 */
@Component
public class RelationStub extends AbstractServiceStub<BOASImpl>
{

	protected void deleteRelation(ViewObject relation) throws ServiceRequestException
	{
		this.deleteRelation(relation, false, true);
	}

	protected void deleteRelationAndEnd2(ViewObject relation) throws ServiceRequestException
	{
		this.deleteRelation(relation, true, true);
	}

	public void deleteRelation(ViewObject relation, boolean isDeleteEnd2, boolean isCheckAuthority) throws ServiceRequestException
	{
		try
		{
			if (isDeleteEnd2)
			{
				RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(relation.getTemplateID());
				String structureClassName = StructureObject.STRUCTURE_CLASS_NAME;

				if (relationTemplate != null)
				{
					structureClassName = relationTemplate.getStructureClassName();
				}

				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(structureClassName, UITypeEnum.FORM, true);
				SearchCondition searchCondition = null;
				if (!SetUtils.isNullList(uiObjectList))
				{
					searchCondition = SearchConditionFactory.createSearchConditionForStructure(structureClassName);
					for (UIObjectInfo uiObject : uiObjectList)
					{
						searchCondition.addResultUIObjectName(uiObject.getName());
					}
				}

				List<StructureObject> listObjectOfRelation = this.stubService.listObjectOfRelation(relation.getObjectGuid(), searchCondition, null, null);

				if (!SetUtils.isNullList(listObjectOfRelation))
				{
					for (StructureObject structure : listObjectOfRelation)
					{
						this.stubService.deleteFoundationObject(structure.getEnd2ObjectGuid().getGuid(), structure.getEnd2ObjectGuid().getClassGuid());
					}
				}

			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		finally
		{
			String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
			ClassStub.decorateObjectGuid(relation.getObjectGuid(), this.stubService);
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new DeleteScheduledTask(serviceContext, relation.getObjectGuid(), null, bmGuid, relation.getName()));
		}
	}

	protected ViewObject getRelation(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getRelation(objectGuid, true);
	}

	public ViewObject getRelation(ObjectGuid objectGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);

			// get ui model object for this object.
			List<UIObjectInfo> uiObjectList = null;
			try
			{
				uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.FORM, true);
			}
			catch (ServiceRequestException ignored)
			{
			}

			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			searchCondition.setPageNum(1);
			searchCondition.setPageSize(1);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			searchCondition.addResultField(ViewObject.END1);
			searchCondition.addResultField(ViewObject.TEMPLATE_ID);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			EMM emm = this.stubService.getEMM();
			Set<String> fieldNames = emm.getObjectFieldNamesInSC(searchCondition);

			FoundationObject foundationObject = DataServer.getInstanceService().getFoundationObject(objectGuid, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);
			if (foundationObject == null)
			{
				return null;
			}

			DecoratorFactory.ofd.decorateWithField(fieldNames, foundationObject, this.stubService.getEMM(), sessionId, false);

			ViewObject retObject = new ViewObject(foundationObject);

			retObject = this.decorateViewObjectByTemplate(Collections.singletonList(retObject)).get(0);

			DecoratorFactory.decorateViewObject(retObject, this.stubService.getEMM(), bmGuid);

			return retObject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	protected ViewObject getRelationByEND1(ObjectGuid end1ObjectGuid, String id) throws ServiceRequestException
	{
		return this.getRelationByEND1(end1ObjectGuid, id, true);
	}

	public ViewObject getRelationByEND1(ObjectGuid end1ObjectGuid, String name, boolean isCheckAcl) throws ServiceRequestException
	{

		String sessionId = this.stubService.getSignature().getCredential();
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

		try
		{
			// bom view 存在于revision上
			end1ObjectGuid.setIsMaster(false);
			RelationTemplateInfo asoTemp = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, name);
			if (asoTemp == null)
			{
				return null;
			}
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(asoTemp.getViewClassName(), null, false);
			searchCondition.addFilter(ViewObject.END1, end1ObjectGuid, OperateSignEnum.EQUALS);
			searchCondition.addFilter(SystemClassFieldEnum.NAME, name, OperateSignEnum.EQUALS);

			// get ui model object for this object.
			List<UIObjectInfo> uiObjectList = null;
			try
			{
				uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(searchCondition.getObjectGuid().getClassName(), UITypeEnum.FORM, true);
			}
			catch (ServiceRequestException ignored)
			{

			}

			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			searchCondition.setPageNum(1);
			searchCondition.setPageSize(1);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);
			searchCondition.addResultField(ViewObject.END1);
			searchCondition.addResultField(ViewObject.TEMPLATE_ID);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);

			List<FoundationObject> results = DataServer.getInstanceService().query(searchCondition, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId);
			if (SetUtils.isNullList(results))
			{
				return null;
			}

			EMM emm = this.stubService.getEMM();
			Set<String> fieldNames = emm.getObjectFieldNamesInSC(searchCondition);

			FoundationObject foundationObject = results.get(0);

			DecoratorFactory.ofd.decorateWithField(fieldNames, foundationObject, this.stubService.getEMM(), sessionId, false);

			ViewObject retObject = new ViewObject(results.get(0));
			retObject = this.decorateViewObjectByTemplate(Collections.singletonList(retObject)).get(0);
			DecoratorFactory.decorateViewObject(retObject, this.stubService.getEMM(), bmGuid);

			return retObject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	protected List<StructureObject> listObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException
	{
		return this.listObjectOfRelation(viewObject, searchCondition, end2SearchCondition, dataRule, true);
	}

	public List<StructureObject> listObjectOfRelation(ObjectGuid end1ObjectGuid, String viewName, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, boolean isCheckAuth) throws ServiceRequestException
	{
		List<StructureObject> structureObjectList;
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(end1ObjectGuid, this.stubService);

			ViewObject viewObject = this.stubService.getRelationStub().getRelationByEND1(end1ObjectGuid, viewName, isCheckAuth);

			if (viewObject == null)
			{
				return null;
			}
			structureObjectList = this.listStructureObject(viewObject.getObjectGuid(), searchCondition, end2SearchCondition, dataRule, isCheckAuth);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return structureObjectList;
	}

	public List<StructureObject> listObjectOfRelation(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuth) throws ServiceRequestException
	{
		return this.listStructureObject(viewObjectGuid, searchCondition, end2SearchCondition, dataRule, isCheckAuth);
	}

	private List<StructureObject> listStructureObject(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuth) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		List<StructureObject> reList;
		try
		{
			SearchCondition tempSC = searchCondition;
			ViewObject viewObject = this.getRelation(viewObjectGuid, isCheckAuth);
			String templateID = viewObject.getTemplateID();
			RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(templateID);
			if (template == null)
			{
				return null;
			}
			String strucClass = template.getStructureClassName();
			if (searchCondition == null)
			{
				tempSC = SearchConditionFactory.createSearchConditionForStructure(strucClass);
			}
			else
			{
				tempSC.getObjectGuid().setClassName(strucClass);
			}

			ClassInfo strucClassInfo = this.stubService.getEMM().getClassByName(strucClass);
			if (strucClassInfo.hasInterface(ModelInterfaceEnum.ICADStructureObject))
			{
				tempSC.addResultField(StructureObject.FIELD_NAME_QUANTITY);
				tempSC.addResultField(StructureObject.FIELD_NAME_BOMRELATED);
			}
			this.setEnd2UIToStructureSearchCondition(end2SearchCondition, tempSC);
			dataRule = this.checkTemplateEnd2Type(viewObject, template, dataRule);
			List<StructureObject> list = DataServer.getRelationService().listObjectOfRelation(viewObjectGuid, template.getGuid(), dataRule,
					Constants.isSupervisor(isCheckAuth, this.stubService), sessionId, tempSC);
			if (SetUtils.isNullList(list))
			{
				return null;
			}
			reList = new ArrayList<>();
			this.decoratorResult(list, end2SearchCondition, searchCondition, sessionId, null, reList, false, false, viewObject.getEnd1ObjectGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		return reList;
	}

	/**
	 * 检查模板上end2Type
	 * 
	 * @param viewObject
	 * @param template
	 * @return
	 */
	private DataRule checkTemplateEnd2Type(FoundationObject objectData, RelationTemplateInfo template, DataRule dataRule)
	{
		if ("3".equalsIgnoreCase(template.getEnd2Type()) && objectData != null)
		{
			dataRule = new DataRule();
			if (SystemStatusEnum.RELEASE.equals(objectData.getStatus()) && objectData.getReleaseTime() != null)
			{
				dataRule.setSystemStatus(SystemStatusEnum.RELEASE);
				Date date = new Date(objectData.getReleaseTime().getTime() + (24 * 60 * 60 * 1000));
				dataRule.setLocateTime(date);
			}
		}
		else if (objectData != null && objectData.isLatestRevision())
		{
			dataRule = new DataRule();
		}
		return dataRule;
	}

	protected List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException
	{
		return this.listFoundationObjectOfRelation(viewObjectGuid, searchCondition, end2SearchCondition, dataRule, Constants.isSupervisor(true, this.stubService));
	}

	public List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuth) throws ServiceRequestException
	{
		return this.listFoundationObject(viewObjectGuid, searchCondition, end2SearchCondition, dataRule, isCheckAuth);
	}

	public List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuth, boolean isWithoutDecorator) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			SearchCondition tempSSC = searchCondition;
			String strucClass;
			ViewObject viewObject = this.getRelation(viewObjectGuid, isCheckAuth);
			String templateID = viewObject.getTemplateID();
			RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(templateID);

			if (template == null)
			{
				return null;
			}
			strucClass = template.getStructureClassName();
			if (searchCondition == null)
			{
				tempSSC = SearchConditionFactory.createSearchConditionForStructure(strucClass);
			}
			else
			{
				tempSSC.getObjectGuid().setClassName(strucClass);
			}

			if (StructureObject.CAD_STRUCTURE_CLASS_NAME.equals(strucClass))
			{
				tempSSC.addResultField(StructureObject.FIELD_NAME_QUANTITY);
				tempSSC.addResultField(StructureObject.FIELD_NAME_BOMRELATED);
			}
			this.setEnd2UIToStructureSearchCondition(end2SearchCondition, tempSSC);
			dataRule = this.checkTemplateEnd2Type(viewObject, template, dataRule);
			List<StructureObject> resultList = DataServer.getRelationService().listObjectOfRelation(viewObjectGuid, template.getGuid(), dataRule,
					Constants.isSupervisor(isCheckAuth, this.stubService), sessionId, tempSSC);
			if (SetUtils.isNullList(resultList))
			{
				return null;
			}

			List<FoundationObject> retList = new ArrayList<>();
			this.decoratorResult(resultList, end2SearchCondition, tempSSC, sessionId, retList, null, true, isWithoutDecorator, viewObject.getEnd1ObjectGuid());
			return retList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	private List<FoundationObject> listFoundationObject(ObjectGuid viewObjectGuid, SearchCondition structureSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuth) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			SearchCondition tempSSC = structureSearchCondition;
			ViewObject viewObject = this.getRelation(viewObjectGuid, isCheckAuth);
			String templateID = viewObject.getTemplateID();
			RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(templateID);

			if (template == null)
			{
				return null;
			}
			String strucClass = template.getStructureClassName();
			if (structureSearchCondition == null)
			{
				tempSSC = SearchConditionFactory.createSearchConditionForStructure(strucClass);
			}
			else
			{
				tempSSC.getObjectGuid().setClassName(strucClass);
			}

			if (StructureObject.CAD_STRUCTURE_CLASS_NAME.equals(strucClass))
			{
				tempSSC.addResultField(StructureObject.FIELD_NAME_QUANTITY);
				tempSSC.addResultField(StructureObject.FIELD_NAME_BOMRELATED);
			}
			this.setEnd2UIToStructureSearchCondition(end2SearchCondition, tempSSC);
			dataRule = this.checkTemplateEnd2Type(viewObject, template, dataRule);
			List<StructureObject> resultList = DataServer.getRelationService().listObjectOfRelation(viewObjectGuid, template.getGuid(), dataRule,
					Constants.isSupervisor(isCheckAuth, this.stubService), sessionId, tempSSC);
			if (SetUtils.isNullList(resultList))
			{
				return null;
			}

			List<FoundationObject> retList = new ArrayList<>();
			this.decoratorResult(resultList, end2SearchCondition, tempSSC, sessionId, retList, null, true, false, viewObject.getEnd1ObjectGuid());
			return retList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
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
		List<String> end2FieldList = new ArrayList<>();
		if (structureSearchCondition != null)
		{
			if (end2SearchCondition != null)
			{
				List<String> listField = end2SearchCondition.getResultFieldList();
				if (!SetUtils.isNullList(listField))
				{
					end2FieldList.addAll(listField);
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
								if (!end2FieldList.contains(ff.getName()))
								{
									end2FieldList.add(ff.getName());
								}
							}
						}
					}
				}

				if (end2SearchCondition.getObjectGuid() != null && !StringUtils.isNullString(end2SearchCondition.getObjectGuid().getClassName()))
				{
					ClassInfo classInfo = this.stubService.getEMM().getClassByName(end2SearchCondition.getObjectGuid().getClassName());
					List<ModelInterfaceEnum> interfaceList = classInfo.getInterfaceList();
					if (!SetUtils.isNullList(interfaceList))
					{
						for (ModelInterfaceEnum interfaceEnum : interfaceList)
						{
							if (interfaceEnum == ModelInterfaceEnum.IFoundation)
							{
								continue;
							}
							List<ClassField> fieldList = this.stubService.getEMM().listClassFieldByInterface(interfaceEnum);
							if (!SetUtils.isNullList(fieldList))
							{
								for (ClassField field : fieldList)
								{
									if (!end2FieldList.contains(field.getName()))
									{
										end2FieldList.add(field.getName());
									}
								}
							}
						}
					}
				}
			}

			if (!SetUtils.isNullList(end2FieldList))
			{
				for (String fieldName : end2FieldList)
				{
					structureSearchCondition.addResultField(ViewObject.PREFIX_END2 + fieldName);
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
	private void decoratorResult(List<StructureObject> resultList, SearchCondition end2SearchCondition, SearchCondition structureSearchCondition, String sessionId,
			List<FoundationObject> retList, List<StructureObject> sretList, boolean isFoundation, boolean isWithoutDecorator, ObjectGuid end1ObjectGuid)
			throws ServiceRequestException, DecorateException
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
			List<FoundationObject> foList = new LinkedList<>();
			List<StructureObject> soList = new LinkedList<>();
			for (StructureObject structureObject : resultList)
			{
				structureObject.setEnd1ObjectGuid(end1ObjectGuid);

				ClassStub.decorateObjectGuid(structureObject.getEnd2ObjectGuid(), this.stubService);
				FoundationObject foundationObject = (FoundationObject) structureObject.get(ViewObject.PREFIX_END2);
				if (!isWithoutDecorator)
				{
					DecoratorFactory.decorateFoundationObject(objectFieldNamesEnd2, foundationObject, emm, bmGuid, null);
					DecoratorFactory.decorateFoundationObjectCode(codeFieldNamesEnd2, foundationObject, emm, bmGuid);

					DecoratorFactory.decorateStructureObject(structureObject, objectFieldNames, codeFieldNames, emm, bmGuid);
				}

				structureObject.put("FULLNAME$", foundationObject.getFullName());

				foList.add(foundationObject);
				soList.add(structureObject);
				structureObject.clear(ViewObject.PREFIX_END2);
			}
			if (!isWithoutDecorator)
			{
				DecoratorFactory.decorateStructureObject(objectFieldNames, soList, this.stubService.getEMM(), sessionId);
				DecoratorFactory.decorateFoundationObject(objectFieldNamesEnd2, foList, this.stubService.getEMM(), sessionId);
			}

			for (int i = 0; i < foList.size(); i++)
			{
				FoundationObject foundationObject = foList.get(i);
				StructureObject structureObject = soList.get(i);
				if (isFoundation)
				{
					foundationObject.put(StructureObject.STRUCTURE_CLASS_NAME, structureObject);
					retList.add(foundationObject);
				}
				else
				{
					structureObject.put(StructureObject.END2_UI_OBJECT, foundationObject);
					sretList.add(structureObject);
				}
			}
		}
	}

	protected List<ViewObject> listRelation(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.listRelation(end1ObjectGuid, true, true);
	}

	public List<ViewObject> listRelation(ObjectGuid end1ObjectGuid, boolean isCheckAuth, boolean isContainBuiltin) throws ServiceRequestException
	{
		List<ViewObject> viewList = new ArrayList<>();
		try
		{
			Set<String> containSet = new HashSet<>();
			Set<String> containGuidSet = new HashSet<>();
			List<RelationTemplateInfo> relationTemplateList = this.stubService.getEMM().listRelationTemplate(end1ObjectGuid, isContainBuiltin);
			if (!SetUtils.isNullList(relationTemplateList))
			{
				for (RelationTemplateInfo relationTemplate : relationTemplateList)
				{
					if (!containSet.contains(relationTemplate.getName()))
					{
						containSet.add(relationTemplate.getName());
						List<ViewObject> tmpList = this.listRelation(end1ObjectGuid, relationTemplate, isCheckAuth, isContainBuiltin);
						if (!SetUtils.isNullList(tmpList))
						{
							for (ViewObject obj : tmpList)
							{
								if (!containGuidSet.contains(obj.getObjectGuid().getGuid()))
								{
									containGuidSet.add(obj.getObjectGuid().getGuid());
									viewList.add(obj);
								}
							}

						}
					}
				}
			}
			return viewList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<ViewObject> listRelation(ObjectGuid end1ObjectGuid, RelationTemplateInfo relationTemplate, boolean isCheckAuth, boolean isContainBuiltin)
			throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		List<FoundationObject> foundationObjectList;
		try
		{
			// bom view 存在于revision上
			end1ObjectGuid.setIsMaster(false);

			String viewClassName = relationTemplate.getViewClassName();
			if (StringUtils.isNullString(viewClassName))
			{
				return null;
			}

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(viewClassName, null, false);
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);

			searchCondition.addFilter(ViewObject.END1, end1ObjectGuid, OperateSignEnum.EQUALS);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			searchCondition.setPageSize(1000);
			searchCondition.addResultField(ViewObject.END1);
			searchCondition.addResultField(ViewObject.TEMPLATE_ID);
			searchCondition.addOrder("TEMPLATEID", true);

			foundationObjectList = DataServer.getInstanceService().query(searchCondition, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);
			if (SetUtils.isNullList(foundationObjectList))
			{
				return null;
			}

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			Set<String> fieldNames = this.stubService.getEMM().getObjectFieldNamesInSC(searchCondition);
			DecoratorFactory.decorateFoundationObject(fieldNames, foundationObjectList, this.stubService.getEMM(), sessionId);

			List<ViewObject> viewList = new ArrayList<>();
			for (FoundationObject foundationObject : foundationObjectList)
			{
				if (isContainBuiltin || (foundationObject.getName() == null || !(foundationObject.getName().equalsIgnoreCase(BuiltinRelationNameEnum.ITEMCAD3D.toString())
						|| foundationObject.getName().equalsIgnoreCase(BuiltinRelationNameEnum.ITEMCAD2D.toString())
						|| foundationObject.getName().equalsIgnoreCase(BuiltinRelationNameEnum.ITEMECAD.toString()))))
				{
					ViewObject viewObject = new ViewObject(foundationObject);
					DecoratorFactory.decorateViewObject(viewObject, this.stubService.getEMM(), bmGuid);

					viewList.add(viewObject);
				}
			}

			viewList = this.decorateViewObjectByTemplate(viewList);
			return viewList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	public List<FoundationObject> listWhereReferenced(ObjectGuid end2ObjectGuid, String viewName, SearchCondition end1SearchCondition, DataRule dataRule, boolean isCheckAuth)
			throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			List<RelationTemplateInfo> telationTemplateList = this.stubService.getEMM().listRelationTemplateByEND2(end2ObjectGuid);
			if (SetUtils.isNullList(telationTemplateList))
			{
				return null;
			}

			RelationTemplateInfo relationTemplate = null;
			for (RelationTemplateInfo relationTemplate_ : telationTemplateList)
			{
				if (viewName.equals(relationTemplate_.getName()))
				{
					relationTemplate = relationTemplate_;
					break;
				}
			}

			if (relationTemplate == null)
			{
				return null;
			}

			List<String> end1FieldList = null;
			if (end1SearchCondition != null)
			{
				end1FieldList = new ArrayList<>();
				List<String> listField = end1SearchCondition.getResultFieldList();
				if (!SetUtils.isNullList(listField))
				{
					end1FieldList.addAll(listField);
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
								end1FieldList.add(ff.getName());
							}
						}
					}
				}
			}
			else
			{
				BMInfo bizModel;
				if (StringUtils.isNullString(relationTemplate.getBmGuid()) || "ALL".equalsIgnoreCase(relationTemplate.getBmGuid()))
				{
					bizModel = this.stubService.getEMM().getCurrentBizModel();
				}
				else
				{
					bizModel = this.stubService.getEMM().getBizModel(relationTemplate.getBmGuid());
				}
				if (bizModel == null)
				{
					return null;
				}
				BOInfo end1ClassInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bizModel.getGuid(), relationTemplate.getEnd1BoName());
				if (end1ClassInfo == null)
				{
					return null;
				}
				String end1ClassName = end1ClassInfo.getClassName();
				end1SearchCondition = SearchConditionFactory.createSearchCondition4Class(end1ClassName, null, false);
			}
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			EMM emm = this.stubService.getEMM();

			FoundationObject foun = this.stubService.getObject(end2ObjectGuid);
			this.checkTemplateEnd2Type(foun, relationTemplate, dataRule);
			SearchCondition struSearchCondition = SearchConditionFactory.createSearchCondition4Class(relationTemplate.getStructureClassName(), null, false);
			List<FoundationObject> foundationObjectList = DataServer.getRelationService().listWhereUsed(end2ObjectGuid.getObjectGuid(), relationTemplate.getName(),
					relationTemplate.getViewClassName(), "2".equals(relationTemplate.getEnd2Type()), end1SearchCondition, struSearchCondition, false,
					Constants.isSupervisor(true, this.stubService), sessionId);
			if (SetUtils.isNullList(foundationObjectList))
			{
				return null;
			}

			String className = end1SearchCondition.getObjectGuid().getClassName();
			if (className == null)
			{
				className = emm.getClassByGuid(end1SearchCondition.getObjectGuid().getClassGuid()).getName();
			}
			Set<String> objectFieldSet = new HashSet<>(emm.getObjectFieldNames(className, end1FieldList));
			Set<String> codeFieldSet = new HashSet<>(emm.getCodeFieldNames(className, end1FieldList));
			codeFieldSet.add(SystemClassFieldEnum.CLASSIFICATION.getName());

			for (FoundationObject fObject : foundationObjectList)
			{
				DecoratorFactory.decorateFoundationObjectCode(codeFieldSet, fObject, emm, bmGuid);
				DecoratorFactory.decorateFoundationObject(objectFieldSet, fObject, emm, bmGuid, null);
			}

			objectFieldSet.clear();
			if (end1FieldList != null)
			{
				objectFieldSet.addAll(end1FieldList);
			}
			DecoratorFactory.decorateFoundationObject(objectFieldSet, foundationObjectList, this.stubService.getEMM(), sessionId);

			return foundationObjectList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	public ViewObject saveRelation(ViewObject relation, boolean isCheckAuth, String procRtGuid) throws ServiceRequestException
	{
		return this.saveRelation(relation, isCheckAuth, false, procRtGuid);
	}

	public ViewObject saveRelation(ViewObject relation, boolean isCheckAcl, boolean byTemplate, String procRtGuid) throws ServiceRequestException
	{

		String sessionId = this.stubService.getSignature().getCredential();
		ViewObject retViewObject = null;

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(relation);

		try
		{
			ObjectGuid objectGuid = relation.getObjectGuid();

			// 判断此BO在流程中是否能建关系 relation.getEnd1ObjectGuid().getclassname()
			if (StringUtils.isGuid(procRtGuid) && byTemplate)
			{
				ProcessRuntime processRuntime = this.stubService.getWFI().getProcessRuntime(procRtGuid);
				List<ActivityRuntime> listCurrentActivityRuntime = this.stubService.getWFI().listCurrentActivityRuntime(procRtGuid);
				if (processRuntime != null && !SetUtils.isNullList(listCurrentActivityRuntime))
				{
					RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(relation.getTemplateID());

					WorkflowTemplateActClassRelationInfo classSetRelationInfo = ((WFIImpl) this.stubService.getWFI()).getTemplateStub().getWorkflowTemplateActClassSetRelationInfo(
							processRuntime.getWFTemplateGuid(), listCurrentActivityRuntime.get(0).getName(), relation.getEnd1ObjectGuid().getClassGuid(),
							relation.getEnd1ObjectGuid().getClassName(), relationTemplate.getName());
					if (classSetRelationInfo != null)
					{
						if (!classSetRelationInfo.isEdit())
						{
							throw new ServiceRequestException("ID_APP_RELATION_TEMPLATE_LIMIT_EDIT", "Workflow template to restrict this relation can not be edited");
						}
					}
				}
			}

			if (StringUtils.isGuid(objectGuid.getGuid()))
			{

				if (relation.isChanged(SystemClassFieldEnum.ID.getName()))
				{
					return null;
				}

				DataServer.getInstanceService().save(relation, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId(), true);
				retViewObject = this.getRelation(objectGuid, isCheckAcl);
			}
			else
			{

				relation.setOwnerUserGuid(this.stubService.getUserSignature().getUserGuid());
				relation.setOwnerGroupGuid(this.stubService.getUserSignature().getLoginGroupGuid());
				relation.setUnique(null);

				if (relation.getObjectGuid().getClassName() == null && relation.getObjectGuid().getClassGuid() == null)
				{
					objectGuid.setClassName("ViewObject");
					relation.setObjectGuid(objectGuid);
				}
				// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
				ClassStub.decorateObjectGuid(relation.getObjectGuid(), this.stubService);

				EMM emm = this.stubService.getEMM();
				LifecyclePhaseInfo lifecyclePhaseInfo = emm.getFirstLifecyclePhaseInfoByClassName(relation.getObjectGuid().getClassName());
				relation.setLifecyclePhaseGuid(lifecyclePhaseInfo.getGuid());
				// 清除状态
				relation.clear(SystemClassFieldEnum.STATUS.getName());

				FoundationObject end1FoundationObject = this.stubService.getFoundationStub().getObject(relation.getEnd1ObjectGuid(), isCheckAcl);
				if (end1FoundationObject != null)
				{
					if (!byTemplate)
					{
						relation.setId(end1FoundationObject.getId());
						relation.setRevisionId(end1FoundationObject.getRevisionId());
						if (relation.get(ViewObject.TEMPLATE_ID) == null || "".equals(relation.get(ViewObject.TEMPLATE_ID)))
						{
							relation.put(ViewObject.TEMPLATE_ID, ViewObject.VIEW_APPOINTED_ID);
						}
					}

					FoundationObject retObject = DataServer.getInstanceService().create(relation, null, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId,
							this.stubService.getFixedTransactionId());
					if (end1FoundationObject.getStatus() != null && SystemStatusEnum.RELEASE.equals(end1FoundationObject.getStatus()))
					{
						DataServer.getInstanceService().release(retObject.getObjectGuid(), sessionId, this.stubService.getFixedTransactionId());
					}
					// 20170301 去除再次查询
					retViewObject = this.getRelation(retObject.getObjectGuid(), isCheckAcl);
				}
			}

			return retViewObject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public ViewObject saveRelationByTemplate(String relationTemplateGuid, ObjectGuid end1ObjectGuid, boolean isCheckAcl, String procRtGuid) throws ServiceRequestException
	{

		RelationTemplate relationTemplate = this.stubService.getEMM().getRelationTemplate(relationTemplateGuid);
		RelationTemplateInfo relationTemplateInfo = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, relationTemplate.getName());
		if (relationTemplateInfo == null)
		{
			throw new ServiceRequestException("ID_APP_VIEW_TEMPLATE_NOT_EXIST", "View template is not exist");
		}
		else
		{
			if (!relationTemplate.isValid())
			{
				throw new ServiceRequestException("ID_APP_VIEW_TEMPLATE_NOT_EXIST", "View template is not exist");
			}
		}

		ViewObject result = this.getRelationByEND1(end1ObjectGuid, relationTemplate.getName(), isCheckAcl);
		if (result != null)
		{
			return result;
		}
		ViewObject viewObject = new ViewObject();
		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setClassGuid(relationTemplate.getViewClassGuid());

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		viewObject.setObjectGuid(objectGuid);
		viewObject.setEnd1ObjectGuid(end1ObjectGuid);
		viewObject.setName(relationTemplate.getName());
		FoundationObject end1FoundationObject = this.stubService.getFoundationStub().getObjectByGuid(end1ObjectGuid, isCheckAcl);

		if (end1FoundationObject != null)
		{
			viewObject.setId(end1FoundationObject.getId());
			viewObject.setRevisionId(end1FoundationObject.getRevisionId());
			viewObject.put(ViewObject.TEMPLATE_ID, relationTemplate.getId());
			viewObject.setEnd1ObjectGuid(end1FoundationObject.getObjectGuid());
		}

		viewObject = this.saveRelation(viewObject, isCheckAcl, true, procRtGuid);

		return viewObject;
	}

	/**
	 * 根据将viewObject对应的关系模板内的一些信息放入viewObject内
	 * 
	 * @param viewObjectList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ViewObject> decorateViewObjectByTemplate(List<ViewObject> viewObjectList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(viewObjectList))
		{
			return null;
		}

		for (ViewObject viewObject : viewObjectList)
		{
			RelationTemplateInfo relationTemplate = null;
			if (!StringUtils.isNullString(viewObject.getTemplateID()))
			{
				relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());
			}
			if (relationTemplate != null)
			{
				viewObject.setStructureModel(relationTemplate.getStructureModel());
				viewObject.setTemplateTitle(relationTemplate.getTitle());
			}
			else
			{
				viewObject.setStructureModel(RelationTemplateTypeEnum.NORMAL);
			}
		}
		return viewObjectList;
	}

	protected List<FoundationObject> listFoundationObjectOfRelation4Detail(ObjectGuid viewObjectGuid, SearchCondition structureSearchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, boolean isCheckAuth) throws ServiceRequestException
	{
		ViewObject viewObject = this.stubService.getRelationStub().getRelation(viewObjectGuid, isCheckAuth);
		if (viewObject.getStructureModel() != RelationTemplateTypeEnum.DETAIL)
		{
			throw new ServiceRequestException("ID_APP_INVALID_DETAIL_RELATION", "Invalid detail relation");
		}
		if (end2SearchCondition == null)
		{
			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());
			end2SearchCondition = this.stubService.getEMM().createAssoEnd2SearchCondition(relationTemplate.getGuid(), false, false);
		}
		return this.listFoundationObjectOfRelation(viewObjectGuid, structureSearchCondition, end2SearchCondition, dataRule, isCheckAuth);
	}
}
