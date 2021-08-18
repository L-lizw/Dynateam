/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMReportStub
 * Wanglei 2011-12-21
 */
package dyna.app.service.brs.uecs;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.StructureObjectImpl;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.systemenum.uecs.ECOLifecyclePhaseEnum;
import dyna.common.systemenum.uecs.UECChangeTypeEnum;
import dyna.common.systemenum.uecs.UECModifyTypeEnum;
import dyna.common.util.JsonUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.common.util.UpdatedECSConstants;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Wanglei
 * 
 */
@Component
public class UECQueryStub extends AbstractServiceStub<UECSImpl>
{

	public FoundationObject getBatchECPorECOFoundation(ObjectGuid ObjectGuid) throws ServiceRequestException
	{
		FoundationObject foObject = this.stubService.getBOAS().getObject(ObjectGuid);
		if (foObject == null)
		{
			return null;
		}
		StringBuffer bomInfo = new StringBuffer();
		if (foObject.get(UpdatedECSConstants.BOMInfo) != null)
		{
			bomInfo.append(foObject.get(UpdatedECSConstants.BOMInfo).toString());
		}
		if (foObject.get(UpdatedECSConstants.BOMInfo1) != null)
		{
			bomInfo.append(foObject.get(UpdatedECSConstants.BOMInfo1).toString());
		}
		if (foObject.get(UpdatedECSConstants.BOMInfo2) != null)
		{
			bomInfo.append(foObject.get(UpdatedECSConstants.BOMInfo2).toString());
		}
		if (!StringUtils.isNullString(bomInfo.toString()))
		{
			// Map<String, Object> StructureMap = (Map<String, Object>) this.simpleJsonStrToMap(bomInfo.toString());
			StructureObjectImpl StructureMap = JsonUtils.getObjectByJsonStr(bomInfo.toString(), StructureObjectImpl.class);
			foObject.put(UpdatedECSConstants.BOMInfo, StructureMap);
		}
		return foObject;
	}

	public List<FoundationObject> getECPByECRTree(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcpClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECPM);
		ClassInfo ecpAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcpClassInfo))
		{
			for (ClassInfo ecpClassInfo : listEcpClassInfo)
			{
				if (ecpClassInfo.isAbstract())
				{
					ecpAbstractClassInfo = ecpClassInfo;
				}
			}
		}
		List<FoundationObject> tempList = this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, ecpAbstractClassInfo, UpdatedECSConstants.ECR_ECP$, true);
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(tempList))
		{
			for (FoundationObject foundationObject : tempList)
			{
				Object parentStr = foundationObject.get("PARENTECP");
				if (parentStr == null || (parentStr != null && StringUtils.isNullString(parentStr.toString())))
				{
					resultList.add(foundationObject);
				}
			}
		}
		return resultList;
	}

	public List<FoundationObject> getECPByECRTreeInECNPage(ObjectGuid ecrObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcpClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECPM);
		ClassInfo ecpAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcpClassInfo))
		{
			for (ClassInfo ecpClassInfo : listEcpClassInfo)
			{
				if (ecpClassInfo.isAbstract())
				{
					ecpAbstractClassInfo = ecpClassInfo;
				}
			}
		}
		List<FoundationObject> ecpList = this.getEND2ByECTypeEND1ByTemp(ecrObjectGuid, ecpAbstractClassInfo, UpdatedECSConstants.ECR_ECP$, true);

		List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
		ClassInfo ecoAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcoClassInfo))
		{
			for (ClassInfo ecoClassInfo : listEcoClassInfo)
			{
				if (ecoClassInfo.isAbstract())
				{
					ecoAbstractClassInfo = ecoClassInfo;
				}
			}
		}
		List<FoundationObject> ecoList = this.getEND2ByECTypeEND1ByTemp(ecnObjectGuid, ecoAbstractClassInfo, UpdatedECSConstants.ECN_ECO$, true);
		Map<String, FoundationObject> ecoMap = new HashMap<String, FoundationObject>();
		if (!SetUtils.isNullList(ecoList))
		{
			for (FoundationObject foObject : ecoList)
			{
				ecoMap.put(foObject.getObjectGuid().getGuid(), foObject);
			}
		}

		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(ecpList))
		{
			for (FoundationObject ecpFoundationObject : ecpList)
			{
				Object parentStr = ecpFoundationObject.get("PARENTECP");
				if (parentStr == null || (parentStr != null && StringUtils.isNullString(parentStr.toString())))
				{
					List<FoundationObject> ecpsecoList = this.getEND2ByECTypeEND1ByTemp(ecpFoundationObject.getObjectGuid(), ecoAbstractClassInfo, UpdatedECSConstants.ECP_ECO$,
							false);
					if (!SetUtils.isNullList(ecpsecoList))
					{
						for (FoundationObject ecoObj : ecpsecoList)
						{
							if (null != ecoMap.get(ecoObj.getObjectGuid().getGuid()))
							{
								ecpFoundationObject.put("HASCHANGEDINTOECO", "Y");
							}
						}
					}
					resultList.add(ecpFoundationObject);
				}
			}
		}
		return resultList;
	}

	public List<FoundationObject> getECPChildbyParentECPInECNPage(ObjectGuid ecpObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> ecpList = this.listChildbyParentObjectGuid(ecpObjectGuid, UpdatedECSConstants.ParentECP);

		List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
		ClassInfo ecoAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcoClassInfo))
		{
			for (ClassInfo ecoClassInfo : listEcoClassInfo)
			{
				if (ecoClassInfo.isAbstract())
				{
					ecoAbstractClassInfo = ecoClassInfo;
				}
			}
		}
		List<FoundationObject> ecoList = this.getEND2ByECTypeEND1ByTemp(ecnObjectGuid, ecoAbstractClassInfo, UpdatedECSConstants.ECN_ECO$, true);
		Map<String, FoundationObject> ecoMap = new HashMap<String, FoundationObject>();
		if (!SetUtils.isNullList(ecoList))
		{
			for (FoundationObject foObject : ecoList)
			{
				ecoMap.put(foObject.getObjectGuid().getGuid(), foObject);
			}
		}

		if (!SetUtils.isNullList(ecpList))
		{
			for (FoundationObject ecpFoundationObject : ecpList)
			{
				List<FoundationObject> ecpsecoList = this.getEND2ByECTypeEND1ByTemp(ecpFoundationObject.getObjectGuid(), ecoAbstractClassInfo, UpdatedECSConstants.ECP_ECO$, false);
				if (!SetUtils.isNullList(ecpsecoList))
				{
					for (FoundationObject ecoObj : ecpsecoList)
					{
						if (null != ecoMap.get(ecoObj.getObjectGuid().getGuid()))
						{
							ecpFoundationObject.put("HASCHANGEDINTOECO", "Y");
						}
					}
				}
			}
		}
		return ecpList;
	}

	public List<FoundationObject> getECPByECRAllInECNPage(ObjectGuid ecrObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcpClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECPM);
		ClassInfo ecpAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcpClassInfo))
		{
			for (ClassInfo ecpClassInfo : listEcpClassInfo)
			{
				if (ecpClassInfo.isAbstract())
				{
					ecpAbstractClassInfo = ecpClassInfo;
				}
			}
		}
		List<FoundationObject> ecpList = this.getEND2ByECTypeEND1ByTemp(ecrObjectGuid, ecpAbstractClassInfo, UpdatedECSConstants.ECR_ECP$, true);

		List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
		ClassInfo ecoAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcoClassInfo))
		{
			for (ClassInfo ecoClassInfo : listEcoClassInfo)
			{
				if (ecoClassInfo.isAbstract())
				{
					ecoAbstractClassInfo = ecoClassInfo;
				}
			}
		}
		List<FoundationObject> ecoList = this.getEND2ByECTypeEND1ByTemp(ecnObjectGuid, ecoAbstractClassInfo, UpdatedECSConstants.ECN_ECO$, true);
		Map<String, FoundationObject> ecoMap = new HashMap<String, FoundationObject>();
		if (!SetUtils.isNullList(ecoList))
		{
			for (FoundationObject foObject : ecoList)
			{
				ecoMap.put(foObject.getObjectGuid().getGuid(), foObject);
			}
		}

		if (!SetUtils.isNullList(ecpList))
		{
			for (FoundationObject ecpFoundationObject : ecpList)
			{
				List<FoundationObject> ecpsecoList = this.getEND2ByECTypeEND1ByTemp(ecpFoundationObject.getObjectGuid(), ecoAbstractClassInfo, UpdatedECSConstants.ECP_ECO$, false);
				if (!SetUtils.isNullList(ecpsecoList))
				{
					for (FoundationObject ecoObj : ecpsecoList)
					{
						if (null != ecoMap.get(ecoObj.getObjectGuid().getGuid()))
						{
							ecpFoundationObject.put("HASCHANGEDINTOECO", "Y");
						}
					}
				}
			}
		}
		return ecpList;
	}

	public List<FoundationObject> getECOByECNAll(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
		ClassInfo ecoAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcoClassInfo))
		{
			for (ClassInfo ecoClassInfo : listEcoClassInfo)
			{
				if (ecoClassInfo.isAbstract())
				{
					ecoAbstractClassInfo = ecoClassInfo;
				}
			}
		}
		return this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, ecoAbstractClassInfo, UpdatedECSConstants.ECN_ECO$, true);
	}

	public List<FoundationObject> getECRByECN(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcrClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IUpdatedECR);
		ClassInfo ecrClassInfo = null;
		if (!SetUtils.isNullList(listEcrClassInfo))
		{
			ecrClassInfo = listEcrClassInfo.get(0);
		}
		return this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, ecrClassInfo, UpdatedECSConstants.ECN_ECR$, true);
	}

	public List<FoundationObject> getECOByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
		ClassInfo ecoAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcoClassInfo))
		{
			for (ClassInfo ecoClassInfo : listEcoClassInfo)
			{
				if (ecoClassInfo.isAbstract())
				{
					ecoAbstractClassInfo = ecoClassInfo;
				}
			}
		}
		return this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, ecoAbstractClassInfo, UpdatedECSConstants.ECP_ECO$, true);
	}

	public List<FoundationObject> getECPCONTENTByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEccontentClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.INormalECPContent);
		ClassInfo eccontentClassInfo = null;
		if (!SetUtils.isNullList(listEccontentClassInfo))
		{
			eccontentClassInfo = listEccontentClassInfo.get(0);
		}
		return this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, eccontentClassInfo, UpdatedECSConstants.ECP_ECPCONTENT$, true);
	}

	public List<FoundationObject> getECPCONTENTByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEccontentClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.INormalECPContent);
		ClassInfo eccontentClassInfo = null;
		if (!SetUtils.isNullList(listEccontentClassInfo))
		{
			eccontentClassInfo = listEccontentClassInfo.get(0);
		}
		return this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, eccontentClassInfo, UpdatedECSConstants.ECO_ECOCONTENT$, true);
	}

	public List<FoundationObject> getWhereUsedECNByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		List<FoundationObject> tempList = this.getWhereUsedEND1ByECTypeEND2ByTemp(end1ObjectGuid, ModelInterfaceEnum.IUpdatedECN, UpdatedECSConstants.ECN_ECO$);
		if (!SetUtils.isNullList(tempList))
		{
			for (FoundationObject foundationObject : tempList)
			{
				foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(foundationObject.getObjectGuid(), false);
				resultList.add(foundationObject);
			}
		}
		return resultList;
	}

	public List<FoundationObject> getWhereUsedECRByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		List<FoundationObject> tempList = this.getWhereUsedEND1ByECTypeEND2ByTemp(end1ObjectGuid, ModelInterfaceEnum.IUpdatedECR, UpdatedECSConstants.ECR_ECP$);
		if (!SetUtils.isNullList(tempList))
		{
			for (FoundationObject foundationObject : tempList)
			{
				foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(foundationObject.getObjectGuid(), false);
				resultList.add(foundationObject);
			}
		}
		return resultList;
	}

	public List<FoundationObject> getECPByECRAll(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcpClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECPM);
		ClassInfo ecpAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcpClassInfo))
		{
			for (ClassInfo ecpClassInfo : listEcpClassInfo)
			{
				if (ecpClassInfo.isAbstract())
				{
					ecpAbstractClassInfo = ecpClassInfo;
				}
			}
		}
		return this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, ecpAbstractClassInfo, UpdatedECSConstants.ECR_ECP$, true);
	}

	public List<FoundationObject> getECOByECNTree(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
		ClassInfo ecoAbstractClassInfo = null;
		if (!SetUtils.isNullList(listEcoClassInfo))
		{
			for (ClassInfo ecoClassInfo : listEcoClassInfo)
			{
				if (ecoClassInfo.isAbstract())
				{
					ecoAbstractClassInfo = ecoClassInfo;
				}
			}
		}
		List<FoundationObject> tempList = this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, ecoAbstractClassInfo, UpdatedECSConstants.ECN_ECO$, true);
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(tempList))
		{
			for (FoundationObject foundationObject : tempList)
			{
				Object parentStr = foundationObject.get("PARENTECO");
				if (parentStr == null || (parentStr != null && StringUtils.isNullString(parentStr.toString())))
				{
					resultList.add(foundationObject);
				}
			}
		}
		return resultList;
	}

	public List<StructureObject> getChangeItemBeforeBYJustBatchECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getChangeItemBeforeBYJustBatchECPorECO(end1ObjectGuid, UpdatedECSConstants.ECP_CHANGEITEMBEFORE$);
	}

	public List<StructureObject> getChangeItemBeforeBYJustBatchECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getChangeItemBeforeBYJustBatchECPorECO(end1ObjectGuid, UpdatedECSConstants.ECO_CHANGEITEMBEFORE$);
	}

	public List<StructureObject> getChangeItemBeforeBYJustBatchECPorECO(ObjectGuid end1ObjectGuid, String templateId) throws ServiceRequestException
	{
		try
		{
			FoundationObject foObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(end1ObjectGuid, false);
			if (foObject == null)
			{
				return null;
			}
			String bomTemplateName = "";
			if (foObject.get(UpdatedECSConstants.BOMTemplate) != null)
			{
				bomTemplateName = foObject.get(UpdatedECSConstants.BOMTemplate).toString();
			}
			if (StringUtils.isNullString(bomTemplateName))
			{
				return null;
			}
			List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByName(bomTemplateName, false);
			if (SetUtils.isNullList(bomTemplateList))
			{
				return null;
			}
			BOMTemplateInfo bomTemplate = bomTemplateList.get(0);

			RelationTemplateInfo relationChangeItemBefore = this.stubService.getEMM().getRelationTemplateById(templateId);
			SearchCondition createSearchConditionForStructure = SearchConditionFactory.createSearchConditionForStructure(relationChangeItemBefore.getStructureClassName());
			List<ClassField> classFieldList = this.stubService.getEMM().listFieldOfClass(relationChangeItemBefore.getStructureClassName());
			for (ClassField classField : classFieldList)
			{
				createSearchConditionForStructure.addResultField(classField.getName());
			}
			List<StructureObject> structureObjectList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(end1ObjectGuid, templateId,
					createSearchConditionForStructure, null, null, false);
			if (SetUtils.isNullList(structureObjectList))
			{
				return null;
			}
			for (StructureObject tmpStrObj : structureObjectList)
			{
				StringBuffer value = new StringBuffer();
				if (tmpStrObj.get(UpdatedECSConstants.ECChangeRecord1) != null)
				{
					value.append(tmpStrObj.get(UpdatedECSConstants.ECChangeRecord1).toString());
				}
				if (tmpStrObj.get(UpdatedECSConstants.ECChangeRecord2) != null)
				{
					value.append(tmpStrObj.get(UpdatedECSConstants.ECChangeRecord2).toString());
				}
				if (tmpStrObj.get(UpdatedECSConstants.ECChangeRecord3) != null)
				{
					value.append(tmpStrObj.get(UpdatedECSConstants.ECChangeRecord3).toString());
				}
				// ////////////////////////////////////////////////////////////////////////////////////////
				StructureObjectImpl structureMap = new StructureObjectImpl();
				if (!StringUtils.isNullString(value.toString()))
				{
					structureMap = JsonUtils.getObjectByJsonStr(value.toString(), StructureObjectImpl.class);
				}
				Set<String> codeFieldName = new HashSet<String>();
				UIObjectInfo uiObjectOldStructure = this.stubService.getEMM().getUIObjectInCurrentBizModel(bomTemplate.getStructureClassName(),UITypeEnum.LIST);
				List<UIField> uiFields = new ArrayList<UIField>();
				if (uiObjectOldStructure != null)
				{
					uiFields = this.stubService.getEMM().listUIFieldByUIObject(bomTemplate.getStructureClassName(), uiObjectOldStructure.getName());
				}
				if (!SetUtils.isNullList(uiFields))
				{
					for (UIField uiField : uiFields)
					{
						if (null != structureMap.get(uiField.getName()))
						{
							if (uiField.getType().equals(FieldTypeEnum.CODE) || uiField.getType().equals(FieldTypeEnum.CLASSIFICATION)
									|| uiField.getType().equals(FieldTypeEnum.MULTICODE) || uiField.getType().equals(FieldTypeEnum.CODEREF))
							{
								codeFieldName.add(uiField.getName());
							}
						}
					}
				}
				DecoratorFactory.decorateCodeRule(null, codeFieldName, structureMap, this.stubService.getEMM());
				// ////////////////////////////////////////////////////////////////////////////////////////

				CodeItemInfo modifyTypeCode = this.stubService.getEMM().getCodeItem(foObject.get("MODIFYTYPE").toString());
				if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchDel.toString()) || modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchMod.toString())
						|| modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchReplace.toString()))
				{
					String oldStructureGuid = "";
					if (null != structureMap.get(UpdatedECSConstants.guid$))
					{
						oldStructureGuid = structureMap.get(UpdatedECSConstants.guid$).toString();

						SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(bomTemplate.getStructureClassName());
						UIObjectInfo uiObject = this.stubService.getEMM().getUIObjectInCurrentBizModel(bomTemplate.getStructureClassName(), UITypeEnum.LIST);
						if (uiObject != null)
						{
							searchCondition.addResultUIObjectName(uiObject.getName());
						}
						ObjectGuid oldBom = new ObjectGuid();
						oldBom.setGuid(oldStructureGuid);
						oldBom.setClassName(bomTemplate.getStructureClassName());
						BOMStructure oldBomStructure = ((BOMSImpl) this.stubService.getBOMS()).getBomStub().getBOM(oldBom, searchCondition, false, null);
						if (oldBomStructure != null && !SetUtils.isNullSet(oldBomStructure.entrySet()))
						{
							for (Entry<String, Object> entry : oldBomStructure.entrySet())
							{
								structureMap.put(entry.getKey() + "_old", entry.getValue());
							}
						}
					}
				}
				tmpStrObj.put(UpdatedECSConstants.ECChangeRecord1, structureMap);
			}
			return structureObjectList;
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

	protected List<FoundationObject> getWhereUsedEND1ByECTypeEND2ByTemp(ObjectGuid end2ObjectGuid, ModelInterfaceEnum end1ModelInterfaceEnum, String templateId)
			throws ServiceRequestException
	{
		List<FoundationObject> result = new ArrayList<FoundationObject>();
		List<FoundationObject> end1FoundationList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listWhereReferenced(end2ObjectGuid, templateId, null, null, false);
		if (SetUtils.isNullList(end1FoundationList))
		{
			return null;
		}

		for (FoundationObject fo : end1FoundationList)
		{
			List<ModelInterfaceEnum> modelInterfaceEnumList = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid()).getInterfaceList();
			for (ModelInterfaceEnum modelInterfaceEnum : modelInterfaceEnumList)
			{
				if (end1ModelInterfaceEnum == modelInterfaceEnum)
				{
					result.add(fo);
				}
			}
		}
		return result;
	}

	protected List<FoundationObject> getEND2ByECTypeEND1ByTemp(ObjectGuid end1ObjectGuid, ClassInfo end2ClassInfo, String templateId, boolean isCheckAuth)
			throws ServiceRequestException
	{
		if (end2ClassInfo == null)
		{
			return null;
		}
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		RelationTemplateInfo relationTemplateByName = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, templateId);
		if (null == relationTemplateByName)
		{
			return resultList;
		}
		ViewObject viewObject = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().getRelationByEND1(end1ObjectGuid, relationTemplateByName.getName(), false);
		if (null == viewObject)
		{
			return resultList;
		}
		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////////////////////////
		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setClassName(end2ClassInfo.getName());
		SearchCondition end2SearchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);
		UIObjectInfo uiObject = this.stubService.getEMM().getUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.LIST);
		if (uiObject != null)
		{
			end2SearchCondition.addResultUIObjectName(uiObject.getName());
		}
		ClassInfo classInfo = this.stubService.getEMM().getClassByName(objectGuid.getClassName());
		if (classInfo != null && classInfo.getInterfaceList() != null)
		{
			for (ModelInterfaceEnum ienum : classInfo.getInterfaceList())
			{
				List<ClassField> fieldlist = this.stubService.getEMM().listClassFieldByInterface(ienum);
				if (fieldlist != null)
				{
					for (ClassField field : fieldlist)
					{
						end2SearchCondition.addResultField(field.getName());
					}
				}
			}
		}
		SearchCondition createSearchConditionForStructure = SearchConditionFactory.createSearchConditionForStructure(relationTemplateByName.getStructureClassName());
		createSearchConditionForStructure.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		UIObjectInfo uiObjectForStructure = this.stubService.getEMM().getUIObjectInCurrentBizModel(relationTemplateByName.getStructureClassName(), UITypeEnum.LIST);
		if (uiObjectForStructure != null)
		{
			createSearchConditionForStructure.addResultUIObjectName(uiObjectForStructure.getName());
		}
		List<FoundationObject> tmpFoList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(viewObject.getObjectGuid(),
				createSearchConditionForStructure, end2SearchCondition, null, false);
		if (!SetUtils.isNullList(tmpFoList))
		{
			resultList.addAll(tmpFoList);
		}
		return resultList;
	}

	public List<FoundationObject> getFormECIByECO(ObjectGuid ChangeItemObjectGuid, ObjectGuid SolveObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		FoundationObject ecoFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ecoObjectGuid, false);
		if (null == ecoFoundation)
		{
			return null;
		}
		if (null == ecoFoundation.getLifecyclePhaseGuid())
		{
			return null;
		}
		LifecyclePhaseInfo ecoLifecyclePhaseInfo = this.stubService.getEMM().getLifecyclePhaseInfo(ecoFoundation.getLifecyclePhaseGuid());
		if (ECOLifecyclePhaseEnum.Canceled.toString().equals(ecoLifecyclePhaseInfo.getName()) || "Y".equals((String) ecoFoundation.get("ISCOMPLETED")))
		{
			List<FoundationObject> tempList = new ArrayList<FoundationObject>();
			List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
			ClassInfo eciClassInfo = null;
			if (!SetUtils.isNullList(listEciClassInfo))
			{
				eciClassInfo = listEciClassInfo.get(0);
			}
			List<FoundationObject> eciList = this.getEND2ByECTypeEND1ByTemp(ecoObjectGuid, eciClassInfo, UpdatedECSConstants.ECO_ECI$, true);
			if (SetUtils.isNullList(eciList))
			{
				return null;
			}
			CodeItemInfo changeTypeCode_attribute = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.Attribute.toString());
			CodeItemInfo changeTypeCode_classificationAttribute = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType,
					UECChangeTypeEnum.ClassificationAttribute.toString());
			CodeItemInfo changeTypeCode_classification = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.Classification.toString());
			for (FoundationObject foundationObject : eciList)
			{
				if (null != foundationObject.get(UpdatedECSConstants.ChangeType) && (changeTypeCode_attribute.getGuid().equals(foundationObject.get(UpdatedECSConstants.ChangeType))
						|| changeTypeCode_classificationAttribute.getGuid().equals(foundationObject.get(UpdatedECSConstants.ChangeType))
						|| changeTypeCode_classification.getGuid().equals(foundationObject.get(UpdatedECSConstants.ChangeType))))
				{
					tempList.add(foundationObject);
				}
			}
			return tempList;
		}
		else
		{
			if (null != SolveObjectGuid)
			{
				return this.stubService.getUECIStub().getFormECIByECO(ChangeItemObjectGuid, SolveObjectGuid);
			}
			else
			{
				return null;
			}
		}
	}

	public List<FoundationObject> getRelationECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName, ObjectGuid ecoObjectGuid)
			throws ServiceRequestException
	{
		FoundationObject ecoFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ecoObjectGuid, false);
		if (null == ecoFoundation)
		{
			return null;
		}
		if (null == ecoFoundation.getLifecyclePhaseGuid())
		{
			return null;
		}
		LifecyclePhaseInfo ecoLifecyclePhaseInfo = this.stubService.getEMM().getLifecyclePhaseInfo(ecoFoundation.getLifecyclePhaseGuid());
		if (ECOLifecyclePhaseEnum.Canceled.toString().equals(ecoLifecyclePhaseInfo.getName()) || "Y".equals((String) ecoFoundation.get("ISCOMPLETED")))
		{
			List<FoundationObject> tempList = new ArrayList<FoundationObject>();
			List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
			ClassInfo eciClassInfo = null;
			if (!SetUtils.isNullList(listEciClassInfo))
			{
				eciClassInfo = listEciClassInfo.get(0);
			}
			List<FoundationObject> eciList = this.getEND2ByECTypeEND1ByTemp(ecoObjectGuid, eciClassInfo, UpdatedECSConstants.ECO_ECI$, true);
			if (SetUtils.isNullList(eciList))
			{
				return null;
			}
			for (FoundationObject foundationObject : eciList)
			{
				CodeItemInfo changeTypeCode = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.Relation.toString());
				if (null != foundationObject.get(UpdatedECSConstants.ChangeType) && changeTypeCode.getGuid().equals(foundationObject.get(UpdatedECSConstants.ChangeType))
						&& relationTemplateName != null && relationTemplateName.equals(foundationObject.get(UpdatedECSConstants.Template)))
				{
					StringBuffer value = new StringBuffer();
					if (foundationObject.get(UpdatedECSConstants.Value1) != null)
					{
						value.append(foundationObject.get(UpdatedECSConstants.Value1).toString());
					}
					if (foundationObject.get(UpdatedECSConstants.Value2) != null)
					{
						value.append(foundationObject.get(UpdatedECSConstants.Value2).toString());
					}
					if (foundationObject.get(UpdatedECSConstants.Value3) != null)
					{
						value.append(foundationObject.get(UpdatedECSConstants.Value3).toString());
					}
					// ////////////////////////////////////////////////////////////////////////////////////////
					Map<String, Object> valueMap = new HashMap<String, Object>();
					if (!StringUtils.isNullString(value.toString()))
					{
						valueMap = (Map<String, Object>) JsonUtils.getObjectByJsonStr(value.toString(), Map.class);
					}
					foundationObject.put(UpdatedECSConstants.Value1, valueMap);
					tempList.add(foundationObject);
				}
			}
			return tempList;
		}
		else
		{
			if (null != solveObjectGuid)
			{
				return this.stubService.getUECIStub().getRelationECIByECO(changeItemObjectGuid, solveObjectGuid, relationTemplateName);
			}
			else
			{
				return null;
			}
		}
	}

	public List<FoundationObject> getBomECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName, ObjectGuid ecoObjectGuid)
			throws ServiceRequestException
	{
		FoundationObject ecoFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ecoObjectGuid, false);
		if (null == ecoFoundation)
		{
			return null;
		}
		LifecyclePhaseInfo ecoLifecyclePhaseInfo = this.stubService.getEMM().getLifecyclePhaseInfo(ecoFoundation.getLifecyclePhaseGuid());
		if (ECOLifecyclePhaseEnum.Canceled.toString().equals(ecoLifecyclePhaseInfo.getName()) || "Y".equals((String) ecoFoundation.get("ISCOMPLETED")))
		{
			List<FoundationObject> tempList = new ArrayList<FoundationObject>();
			List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
			ClassInfo eciClassInfo = null;
			if (!SetUtils.isNullList(listEciClassInfo))
			{
				eciClassInfo = listEciClassInfo.get(0);
			}
			List<FoundationObject> eciList = this.getEND2ByECTypeEND1ByTemp(ecoObjectGuid, eciClassInfo, UpdatedECSConstants.ECO_ECI$, true);
			if (SetUtils.isNullList(eciList))
			{
				return null;
			}
			for (FoundationObject foundationObject : eciList)
			{
				CodeItemInfo changeTypeCode = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.BOM.toString());
				if (foundationObject.get(UpdatedECSConstants.ChangeType) != null && changeTypeCode.getGuid().equals(foundationObject.get(UpdatedECSConstants.ChangeType))
						&& relationTemplateName != null && relationTemplateName.equals(foundationObject.get(UpdatedECSConstants.Template)))
				{
					StringBuffer value = new StringBuffer();
					if (foundationObject.get(UpdatedECSConstants.Value1) != null)
					{
						value.append(foundationObject.get(UpdatedECSConstants.Value1).toString());
					}
					if (foundationObject.get(UpdatedECSConstants.Value1) != null)
					{
						value.append(foundationObject.get(UpdatedECSConstants.Value1).toString());
					}
					if (foundationObject.get(UpdatedECSConstants.Value2) != null)
					{
						value.append(foundationObject.get(UpdatedECSConstants.Value2).toString());
					}
					// ////////////////////////////////////////////////////////////////////////////////////////
					Map<String, Object> valueMap = new HashMap<String, Object>();
					if (!StringUtils.isNullString(value.toString()))
					{
						valueMap = (Map<String, Object>) JsonUtils.getObjectByJsonStr(value.toString(), Map.class);
					}
					foundationObject.put(UpdatedECSConstants.Value1, valueMap);
					tempList.add(foundationObject);
				}
			}
			return tempList;
		}
		else
		{
			if (null != solveObjectGuid)
			{
				return this.stubService.getUECIStub().getBomECIByECO(changeItemObjectGuid, solveObjectGuid, relationTemplateName);
			}
			else
			{
				return null;
			}
		}
	}

	public List<FoundationObject> getOtherECIByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> tempList = new ArrayList<FoundationObject>();
		List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
		ClassInfo eciClassInfo = null;
		if (!SetUtils.isNullList(listEciClassInfo))
		{
			eciClassInfo = listEciClassInfo.get(0);
		}
		List<FoundationObject> eciList = this.getEND2ByECTypeEND1ByTemp(end1ObjectGuid, eciClassInfo, UpdatedECSConstants.ECO_ECI$, true);
		if (SetUtils.isNullList(eciList))
		{
			return null;
		}
		for (FoundationObject foundationObject : eciList)
		{
			CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem((String) foundationObject.get("CHANGETYPE"));
			if (codeItemInfo != null && UECChangeTypeEnum.Others.name().equals(codeItemInfo.getName()))
			{
				tempList.add(foundationObject);
			}
		}
		return tempList;
	}

	public List<FoundationObject> listChildbyParentObjectGuid(ObjectGuid parentObjectGuid, String parentKey) throws ServiceRequestException
	{
		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setClassGuid(parentObjectGuid.getClassGuid());
		objectGuid.setClassName(parentObjectGuid.getClassName());
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, true);
		searchCondition.setPageNum(1);
		searchCondition.setPageSize(50000);
		searchCondition.addFilter(parentKey, parentObjectGuid, OperateSignEnum.EQUALS);
		List<UIObjectInfo> uiList = this.stubService.getEMM().listUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.FORM, true);
		if (!SetUtils.isNullList(uiList) && uiList.size() > 0)
		{
			searchCondition.addResultUIObjectName(uiList.get(0).getName());
		}
		return this.stubService.getBOAS().listObject(searchCondition);
	}

	public List<FoundationObject> listALLChildbyParentObjectGuid(ObjectGuid parentObjectGuid, String parentKey) throws ServiceRequestException
	{

		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		List<FoundationObject> sonsList = this.listChildbyParentObjectGuid(parentObjectGuid, parentKey);
		if (!SetUtils.isNullList(sonsList))
		{
			resultList.addAll(sonsList);
			for (FoundationObject foundationObject : sonsList)
			{
				resultList.addAll(this.listALLChildbyParentObjectGuid(foundationObject.getObjectGuid(), parentKey));
			}
		}
		return resultList;
	}
}