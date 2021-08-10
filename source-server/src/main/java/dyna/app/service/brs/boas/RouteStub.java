package dyna.app.service.brs.boas;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.*;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.route.ProcessFlowConstants;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.CloneUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteStub extends AbstractServiceStub<BOASImpl>
{

	protected RouteStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	/**
	 * 通过模板复制创建工艺流程或者工序
	 * 
	 * @param end1ObjectGuid
	 *            复制创建生成的对象需要关联的父阶对象
	 * @param templateObjectGuid
	 *            模板对象
	 * @param templateName
	 *            父阶对象和生成的新对象的关联关系模板名
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject createByTemplate(ObjectGuid end1ObjectGuid, ObjectGuid templateObjectGuid, String templateName) throws ServiceRequestException
	{
		FoundationObject retObject = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			FoundationObject foundationObject = this.stubService.getObjectNotCheckAuthor(end1ObjectGuid);
			FoundationObject templateObject = this.stubService.getObjectNotCheckAuthor(templateObjectGuid);

			if (ProcessFlowConstants.ITEM_PROCESS_FLOW_RELATION_TEMPLATE_NAME.equals(templateName))
			{
				// 从标准工艺流程复制生成工艺流程
				retObject = this.createProcessFlow(foundationObject, templateObject);
			}
			else if (ProcessFlowConstants.PROCESS_FLOW_PROCESS_RELATION_TEMPLATE_NAME.equals(templateName))
			{
				// 从工艺流程上取得物料编码
				String itemObj = (String) foundationObject.get(ProcessFlowConstants.PROCESS_ITEM);
				if (StringUtils.isGuid(itemObj))
				{
					String itemObjClass = (String) foundationObject.get(ProcessFlowConstants.PROCESS_ITEM + "$CLASS");
					ObjectGuid itemObjectGuid = new ObjectGuid(itemObjClass, null, itemObj, null);

					// 从标准工序库复制生成工序
					retObject = this.createProcess(itemObjectGuid, foundationObject, templateObject);
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
		return retObject;
	}

	/**
	 * 从标准工艺流程复制生成工艺流程
	 * 
	 * @param end1
	 * @param processFlowTemplate
	 * @throws ServiceRequestException
	 */
	private FoundationObject createProcessFlow(FoundationObject end1, FoundationObject processFlowTemplate) throws ServiceRequestException
	{
		String templateName = ProcessFlowConstants.ITEM_PROCESS_FLOW_RELATION_TEMPLATE_NAME;
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1.getObjectGuid(), templateName);

		// 复制生成工艺流程
		String processClassName = this.getClassName(ModelInterfaceEnum.IProcessFlow);
		FoundationObject processFlow = this.copyFoundationObject(processFlowTemplate, processClassName);
		this.setItemObject(end1.getObjectGuid(), processFlow);
		processFlow = this.stubService.createObject(processFlow);

		// 物料关联工艺流程
		StructureObject structureObject = this.stubService.newStructureObject(null, relationTemplate.getStructureClassName());
		this.stubService.link(end1.getObjectGuid(), processFlow.getObjectGuid(), structureObject, templateName);

		// 复制生成工艺流程的所有工序
		templateName = ProcessFlowConstants.PROCESS_FLOW_PROCESS_RELATION_TEMPLATE_NAME;
		ViewObject viewObject_ = this.stubService.getRelationByEND1(processFlowTemplate.getObjectGuid(), templateName);
		if (viewObject_ != null)
		{
			SearchCondition searchCondition = this.getStructureCondition(relationTemplate);
			SearchCondition end2SearchCondition = this.getEnd2Condition(relationTemplate);
			List<FoundationObject> end2List = this.stubService.listFoundationObjectOfRelation(viewObject_.getObjectGuid(), searchCondition, end2SearchCondition, null, false);
			if (!SetUtils.isNullList(end2List))
			{
				for (FoundationObject end2 : end2List)
				{
					this.createProcess(end1.getObjectGuid(), processFlow, end2);
				}
			}
		}

		// 复制文件
		this.copyFile(processFlow.getObjectGuid(), processFlowTemplate.getObjectGuid());

		return processFlow;
	}

	/**
	 * 从标准工序库复制生成工序
	 * 
	 * @param processFlow
	 * @param processTemplate
	 * @throws ServiceRequestException
	 */
	private FoundationObject createProcess(ObjectGuid itemObjectGuid, FoundationObject processFlow, FoundationObject processTemplate) throws ServiceRequestException
	{
		String templateName = ProcessFlowConstants.PROCESS_FLOW_PROCESS_RELATION_TEMPLATE_NAME;

		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(processFlow.getObjectGuid(), templateName);

		// 创建工序
		String processClassName = this.getClassName(ModelInterfaceEnum.IProcess);
		FoundationObject process = this.copyFoundationObject(processTemplate, processClassName);
		process.setClassificationGuid(processTemplate.getClassificationGuid());
		this.setItemObject(itemObjectGuid, process);
		process = this.stubService.createObject(process);

		StructureObject structureObject = (StructureObject) processTemplate.get(StructureObject.STRUCTURE_CLASS_NAME);
		StructureObject newStructureObject = this.copyStructure(structureObject, relationTemplate);

		// 工艺流程关联工序
		this.stubService.link(processFlow.getObjectGuid(), process.getObjectGuid(), newStructureObject, templateName);

		// 复制所有子阶对象
		List<ViewObject> viewList = this.stubService.listRelation(processTemplate.getObjectGuid());
		if (!SetUtils.isNullList(viewList))
		{
			for (ViewObject viewObject : viewList)
			{
				RelationTemplateInfo relationTemplate_ = this.stubService.getEMM().getRelationTemplateByName(processTemplate.getObjectGuid(), viewObject.getName());
				SearchCondition searchCondition = this.getStructureCondition(relationTemplate_);
				SearchCondition end2SearchCondition = this.getEnd2Condition(relationTemplate_);
				List<FoundationObject> end2List = this.stubService.listFoundationObjectOfRelation(viewObject.getObjectGuid(), searchCondition, end2SearchCondition, null, false);
				if (!SetUtils.isNullList(end2List))
				{
					for (FoundationObject end2 : end2List)
					{
						StructureObject structureObject_ = (StructureObject) end2.get(StructureObject.STRUCTURE_CLASS_NAME);
						StructureObject newStructureObject_ = this.copyStructure(structureObject_, relationTemplate_);

						// 关联工序
						this.stubService.link(process.getObjectGuid(), end2.getObjectGuid(), newStructureObject_, viewObject.getName());
					}
				}
			}
		}

		// 复制文件
		this.copyFile(process.getObjectGuid(), processTemplate.getObjectGuid());

		return process;
	}

	/**
	 * 设置对象的物料编码
	 * 
	 * @param itemObjectGuid
	 * @param foundationObject
	 */
	private void setItemObject(ObjectGuid itemObjectGuid, FoundationObject foundationObject)
	{
		foundationObject.put(ProcessFlowConstants.PROCESS_ITEM, itemObjectGuid.getGuid());
		foundationObject.put(ProcessFlowConstants.PROCESS_ITEM + "$MASTER", itemObjectGuid.getMasterGuid());
		foundationObject.put(ProcessFlowConstants.PROCESS_ITEM + "$CLASS", itemObjectGuid.getClassGuid());
	}

	/**
	 * 工艺流程复制新建
	 * 
	 * @param foundationObject
	 * @param relationTemplate
	 * @param end1
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject copyCreate(FoundationObject foundationObject, RelationTemplate relationTemplate, FoundationObject end1) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(stubService.getFixedTransactionId());

			FoundationObject newObject = CloneUtils.clone(foundationObject);

			newObject.setId(null);
			newObject.getObjectGuid().setGuid(null);
			newObject.getObjectGuid().setMasterGuid(null);
			newObject.setObjectGuid(newObject.getObjectGuid());
			setItemObject(end1.getObjectGuid(), newObject);
			newObject = stubService.createObject(newObject);

			StructureObject structureObject = stubService.newStructureObject(relationTemplate.getStructureClassGuid(), relationTemplate.getStructureClassName());
			stubService.link(end1.getObjectGuid(), newObject.getObjectGuid(), structureObject, relationTemplate.getName());

			RelationTemplateInfo template = stubService.getEMM().getRelationTemplateByName(foundationObject.getObjectGuid(),
					ProcessFlowConstants.PROCESS_FLOW_PROCESS_RELATION_TEMPLATE_NAME);

			List<FoundationObject> newProcessList = new ArrayList<FoundationObject>();
			List<StructureObject> processStructureList = listProcessWithStructure(foundationObject, template);
			if (!SetUtils.isNullList(processStructureList))
			{
				for (StructureObject processStructure : processStructureList)
				{
					FoundationObject process = processStructure.getEnd2UIObject();
					process.setId(null);
					setItemObject(end1.getObjectGuid(), process);
					FoundationObject saveAsProcess = stubService.saveAsObject(process, false, false, false);
					newProcessList.add(saveAsProcess);
				}
			}

			for (FoundationObject newProcess : newProcessList)
			{
				StructureObject newStructure = stubService.newStructureObject(template.getStructureClassGuid(), template.getStructureClassName());
				newStructure.setSequence(processStructureList.get(newProcessList.indexOf(newProcess)).getSequence());
				stubService.link(newObject.getObjectGuid(), newProcess.getObjectGuid(), newStructure, template.getName());
			}

//			DataServer.getTransactionManager().commitTransaction();
			return newObject;
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

	private List<StructureObject> listProcessWithStructure(FoundationObject foundationObject, RelationTemplateInfo template) throws ServiceRequestException
	{
		List<StructureObject> processList = Collections.emptyList();
		ViewObject viewObject = stubService.getRelationByEND1(foundationObject.getObjectGuid(), template.getName());
		if (viewObject != null)
		{
			processList = stubService.listObjectOfRelation(viewObject.getObjectGuid(), null, null, null);
		}
		return processList;
	}

	/**
	 * 复制文件
	 * 
	 * @param objectGuid
	 *            目标对象
	 * @param origObjectGuid
	 *            来源对象
	 * @throws ServiceRequestException
	 */
	private void copyFile(ObjectGuid objectGuid, ObjectGuid origObjectGuid) throws ServiceRequestException
	{
		((DSSImpl) this.stubService.getDSS()).getFileInfoStub().copyFile(objectGuid, origObjectGuid, false);
	}

	/**
	 * 拷贝对象数据
	 * 
	 * @param fo
	 * @return
	 * @throws ServiceRequestException
	 */
	private FoundationObject copyFoundationObject(FoundationObject fo, String className) throws ServiceRequestException
	{
		FoundationObject foundationObject_ = new FoundationObjectImpl();
		foundationObject_.sync(fo);

		foundationObject_ = ProcessFlowConstants.clearForCreateObj(foundationObject_);

		FoundationObject newEnd2 = this.stubService.newFoundationObject(null, className);
		((FoundationObjectImpl) newEnd2).putAll((FoundationObjectImpl) foundationObject_);

		return newEnd2;
	}

	/**
	 * 拷贝结构数据
	 * 
	 * @param structureObject
	 * @return
	 * @throws ServiceRequestException
	 */
	private StructureObject copyStructure(StructureObject structureObject, RelationTemplateInfo relationTemplate) throws ServiceRequestException
	{
		StructureObject newStructure = this.stubService.newStructureObject(null, relationTemplate.getStructureClassName());

		if (structureObject == null)
		{
			return newStructure;
		}

		StructureObject structureObject_ = new StructureObjectImpl();
		structureObject_.sync(structureObject);

		structureObject_.clear("END2$");
		structureObject_.clear("END2$MASTER");
		structureObject_.clear("END2$CLASS");
		structureObject_.clear("VIEWFK");
		structureObject_.clear("VIEWCLASSGUID$");
		structureObject_.clear("ISPRIMARY");

		((StructureObjectImpl) newStructure).putAll((StructureObjectImpl) structureObject_);
		newStructure.put("VIEWCLASSGUID$", relationTemplate.getViewClassGuid());

		return newStructure;
	}

	/**
	 * 构造结构SearchCondition
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	private SearchCondition getStructureCondition(RelationTemplateInfo relationTemplate) throws ServiceRequestException
	{
		SearchCondition sc = SearchConditionFactory.createSearchConditionForStructure(relationTemplate.getStructureClassName());
		List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(relationTemplate.getStructureClassName());
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObject : uiObjectList)
			{
				sc.addResultUIObjectName(uiObject.getName());
			}
		}
		return sc;
	}

	private SearchCondition getEnd2Condition(RelationTemplateInfo info) throws ServiceRequestException
	{
		RelationTemplate relationTemplate = this.stubService.getEMM().getRelationTemplate(info.getGuid());
		String end2BoName = SetUtils.isNullList(relationTemplate.getRelationTemplateEnd2List()) ? null : relationTemplate.getRelationTemplateEnd2List().get(0).getEnd2BoName();
		if (StringUtils.isNullString(end2BoName))
		{
			return null;
		}

		BOInfo end2BOInfo = this.stubService.getEMM().getCurrentBoInfoByName(end2BoName, false);
		if (end2BOInfo == null || StringUtils.isNullString(end2BOInfo.getClassName()))
		{
			return null;
		}

		SearchCondition sc = SearchConditionFactory.createSearchCondition4Class(end2BOInfo.getClassName(), null, false);
		List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(end2BOInfo.getClassName());
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObject : uiObjectList)
			{
				sc.addResultUIObjectName(uiObject.getName());
			}
		}
		return sc;
	}

	private String getClassName(ModelInterfaceEnum modelInterface) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(modelInterface, null);
		return classInfo.getName();
	}

}
