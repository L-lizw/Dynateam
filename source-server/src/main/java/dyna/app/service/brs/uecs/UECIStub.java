/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMReportStub
 * Wanglei 2011-12-21
 */
package dyna.app.service.brs.uecs;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.brm.BRMImpl;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.*;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.systemenum.uecs.UECActionTypeEnum;
import dyna.common.systemenum.uecs.UECChangeTypeEnum;
import dyna.common.systemenum.uecs.UECModifyTypeEnum;
import dyna.common.systemenum.uecs.UECReplacepolicyEnum;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.tool.DSToolService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Wanglei
 * 
 */
@Component
public class UECIStub extends AbstractServiceStub<UECSImpl>
{

	private List<BOMStructure> listBOMByEnd1ForEc(ObjectGuid end1ObjectGuid, DataRule dataRule, String viewName, SearchCondition searchConditionStructure)
			throws ServiceRequestException
	{
		List<BOMStructure> bomList = null;
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(end1ObjectGuid, this.stubService);
			BOMView bomViewObject = ((BOMSImpl) this.stubService.getBOMS()).getBomViewStub().getBOMViewByEND1(end1ObjectGuid, viewName, false);
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
			searchConditionStructure.addResultField(ViewObject.PREFIX_END2 + BOMStructure.SPEC);
			bomList = ((BOMSImpl) this.stubService.getBOMS()).getBomStub().listBOM(viewObjectGuid, searchConditionStructure, null, dataRule);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		return bomList;
	}

	public List<FoundationObject> getFormECIByECO(ObjectGuid ChangeItemObjectGuid, ObjectGuid SolveObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		List<ClassInfo> classInfoList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
		if (SetUtils.isNullList(classInfoList))
		{
			return null;
		}
		ClassInfo classInfo = classInfoList.get(0);
		// EC功能，不判断权限
		FoundationObject changeItemObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ChangeItemObjectGuid, false);
		FoundationObject solveObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(SolveObjectGuid, false);
		if (changeItemObject == null || solveObject == null)
		{
			return null;
		}

		this.getAttributeECI(resultList, changeItemObject, solveObject, classInfo);
		this.getClassificationECI(resultList, changeItemObject, solveObject, classInfo);
		return resultList;
	}

	/**
	 * 获取变更对象、解决对象之间的属性差异
	 * 
	 * @param resultList
	 * @param changeItemObject
	 * @param solveObject
	 * @param classInfo
	 * @throws ServiceRequestException
	 */
	private void getAttributeECI(List<FoundationObject> resultList, FoundationObject changeItemObject, FoundationObject solveObject, ClassInfo classInfo)
			throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(changeItemObject.getObjectGuid(), this.stubService);
		ClassStub.decorateObjectGuid(solveObject.getObjectGuid(), this.stubService);

		CodeItemInfo actionTypeMod = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Mod.toString());
		CodeItemInfo changeTypeAttribute = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.Attribute.toString());
		List<UIField> uiFieldList = this.stubService.getEMM().listFormUIField(changeItemObject.getObjectGuid().getClassName());
		if (SetUtils.isNullList(uiFieldList))
		{
			return;
		}
		List<ClassField> classFieldList = this.stubService.getEMM().listFieldOfClass(changeItemObject.getObjectGuid().getClassName());
		Map<String, ClassField> classfieldMap = new HashMap<String, ClassField>();

		if (!SetUtils.isNullList(classFieldList))
		{
			for (ClassField classField : classFieldList)
			{
				classfieldMap.put(classField.getName(), classField);
			}
		}

		for (UIField itemUIField : uiFieldList)
		{
			ClassField classfield = classfieldMap.get(itemUIField.getName());
			boolean isEciItem = false;
			if (changeItemObject.get(itemUIField.getName()) == null && solveObject.get(itemUIField.getName()) != null)
			{
				isEciItem = true;
			}
			else if (changeItemObject.get(itemUIField.getName()) != null && solveObject.get(itemUIField.getName()) == null)
			{
				isEciItem = true;
			}
			else if (changeItemObject.get(itemUIField.getName()) != null && solveObject.get(itemUIField.getName()) != null
					&& !changeItemObject.get(itemUIField.getName()).equals(solveObject.get(itemUIField.getName())))
			{
				isEciItem = true;
			}
			if (isEciItem)
			{
				FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
				foObj.put(UpdatedECSConstants.eciContent, itemUIField.getTitle(this.stubService.getUserSignature().getLanguageEnum()));
				foObj.put(UpdatedECSConstants.ChangeType, changeTypeAttribute.getGuid());
				foObj.put(UpdatedECSConstants.ActionType, actionTypeMod.getGuid());
				this.setFormECIByECO(foObj, changeItemObject, solveObject, itemUIField, classfield);
				resultList.add(foObj);
			}
		}

	}

	/**
	 * 获取变更对象、解决对象之间的分类差异
	 * 
	 * @param resultList
	 *            缓存分类差异
	 * @param changeItemObject
	 *            变更对象
	 * @param solveObject
	 *            解决对象
	 * @param classInfo
	 * @throws ServiceRequestException
	 */
	private void getClassificationECI(List<FoundationObject> resultList, FoundationObject changeItemObject, FoundationObject solveObject, ClassInfo classInfo)
			throws ServiceRequestException
	{
		CodeItemInfo actionTypeMod = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Mod.toString());
		Map<String, FoundationObject> changeItemClassificationFoMap = new HashMap<String, FoundationObject>();
		Map<String, FoundationObject> solveClassificationFoMap = new HashMap<String, FoundationObject>();

		List<FoundationObject> changeItemClassificationFoList = changeItemObject.restoreAllClassification(false);
		if (!SetUtils.isNullList(changeItemClassificationFoList))
		{
			for (FoundationObject fo : changeItemClassificationFoList)
			{
				changeItemClassificationFoMap.put(fo.getClassificationGroup(), fo);
			}
		}

		List<FoundationObject> solveClassificationFoList = solveObject.restoreAllClassification(false);
		if (!SetUtils.isNullList(solveClassificationFoList))
		{
			for (FoundationObject fo : solveClassificationFoList)
			{
				solveClassificationFoMap.put(fo.getClassificationGroup(), fo);
			}
		}

		if (!SetUtils.isNullMap(changeItemClassificationFoMap) || !SetUtils.isNullMap(solveClassificationFoMap))
		{
			List<ClassficationFeature> listClassficationFeature = this.stubService.getEMM().listClassficationFeature(changeItemObject.getObjectGuid().getClassGuid());
			if (!SetUtils.isNullList(listClassficationFeature))
			{

				for (ClassficationFeature classficationFeature : listClassficationFeature)
				{
					FoundationObject solveFo = solveClassificationFoMap.get(classficationFeature.getClassificationfk());
					FoundationObject changeItemFo = changeItemClassificationFoMap.get(classficationFeature.getClassificationfk());
					String solveClassificationDetail = "";
					String changeItemClassificationDetail = "";
					if (solveFo != null && solveFo.getClassificationGuid() != null)
					{
						solveClassificationDetail = solveFo.getClassificationGuid();
					}
					if (changeItemFo != null && changeItemFo.getClassificationGuid() != null)
					{
						changeItemClassificationDetail = changeItemFo.getClassificationGuid();
					}
					if (!solveClassificationDetail.equals(changeItemClassificationDetail))
					{
						FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
						CodeItemInfo changeTypeAttribute = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.Classification.toString());
						foObj.put(UpdatedECSConstants.eciContent,
								StringUtils.getMsrTitle(classficationFeature.getTitle(), this.stubService.getUserSignature().getLanguageEnum().getType()));
						foObj.put(UpdatedECSConstants.ChangeType, changeTypeAttribute.getGuid());
						foObj.put(UpdatedECSConstants.ActionType, actionTypeMod.getGuid());
						if (changeItemFo != null)
						{
							foObj.put(UpdatedECSConstants.eciEditBefore,
									StringUtils.getMsrTitle((String) changeItemFo.get("CLASSIFICATION$TITLE"), this.stubService.getUserSignature().getLanguageEnum().getType()));
						}
						else
						{
							foObj.put(UpdatedECSConstants.eciEditBefore, "");
						}
						if (solveFo != null)
						{
							foObj.put(UpdatedECSConstants.eciEditAfter,
									StringUtils.getMsrTitle((String) solveFo.get("CLASSIFICATION$TITLE"), this.stubService.getUserSignature().getLanguageEnum().getType()));
						}
						else
						{
							foObj.put(UpdatedECSConstants.eciEditAfter, "");
						}
						if (!classficationFeature.isMaster())
						{
							resultList.add(foObj);
						}
					}
					if (StringUtils.isNullString(solveClassificationDetail))
					{
						continue;
					}
					if ((classficationFeature.isMaster()
							&& (StringUtils.isGuid(changeItemObject.getClassificationGuid()) || StringUtils.isGuid(solveObject.getClassificationGuid())))
							|| !classficationFeature.isMaster())
					{
						List<ClassField> classificationClassFieldList = this.stubService.getEMM().listClassificationField(solveClassificationDetail);
						Map<String, ClassField> classificationClassfieldMap = new HashMap<String, ClassField>();
						if (!SetUtils.isNullList(classificationClassFieldList))
						{
							for (ClassField classField : classificationClassFieldList)
							{
								classificationClassfieldMap.put(classField.getName(), classField);
							}
						}
						List<UIField> classificationUiFieldList = this.stubService.getEMM().listCFUIField(solveClassificationDetail, UITypeEnum.FORM);

						if (!SetUtils.isNullList(classificationUiFieldList))
						{
							for (UIField itemUIField : classificationUiFieldList)
							{
								FoundationObject originalMap = new FoundationObjectImpl();
								if (solveClassificationDetail.equals(changeItemClassificationDetail))
								{
									originalMap = changeItemFo;
								}

								ClassField classificationClassfield = classificationClassfieldMap.get(itemUIField.getName());
								boolean isEciItem = false;
								if (originalMap.get(itemUIField.getName()) == null && solveFo.get(itemUIField.getName()) != null)
								{
									isEciItem = true;
								}
								else if (originalMap.get(itemUIField.getName()) != null && solveFo.get(itemUIField.getName()) == null)
								{
									isEciItem = true;
								}
								else if (originalMap.get(itemUIField.getName()) != null && solveFo.get(itemUIField.getName()) != null
										&& !originalMap.get(itemUIField.getName()).equals(solveFo.get(itemUIField.getName())))
								{
									isEciItem = true;
								}
								if (isEciItem)
								{
									CodeItemInfo changeTypeAttribute = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType,
											UECChangeTypeEnum.ClassificationAttribute.toString());
									FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
									foObj.put(UpdatedECSConstants.eciContent,
											StringUtils.getMsrTitle((String) solveFo.get("CLASSIFICATION$TITLE"), this.stubService.getUserSignature().getLanguageEnum().getType())
													+ ":" + itemUIField.getTitle(this.stubService.getUserSignature().getLanguageEnum()));
									foObj.put(UpdatedECSConstants.ChangeType, changeTypeAttribute.getGuid());
									foObj.put(UpdatedECSConstants.ActionType, actionTypeMod.getGuid());
									this.setFormECIByECO(foObj, originalMap, solveFo, itemUIField, classificationClassfield);
									resultList.add(foObj);
								}
							}
						}
					}
				}
			}
		}

	}

	private void setFormECIByECO(FoundationObject foObj, FoundationObject changeItemObject, FoundationObject solveObject, UIField itemUIField, ClassField classField)
			throws ServiceRequestException
	{
		String name = itemUIField.getName();
		if ("LIFECYCLEPHASE$".equalsIgnoreCase(itemUIField.getName()))
		{
			if (null != changeItemObject.getLifecyclePhase())
			{
				foObj.put(UpdatedECSConstants.eciEditBefore,
						StringUtils.getMsrTitle(changeItemObject.getLifecyclePhase(), this.stubService.getUserSignature().getLanguageEnum().getType()));
			}
			if (null != solveObject.getLifecyclePhase())
			{
				foObj.put(UpdatedECSConstants.eciEditAfter,
						StringUtils.getMsrTitle(solveObject.getLifecyclePhase(), this.stubService.getUserSignature().getLanguageEnum().getType()));
			}
		}
		else if (FieldTypeEnum.INTEGER.equals(itemUIField.getType()))
		{
			if (null != changeItemObject.get(itemUIField.getName()))
			{
				String changeItemColumnValue = null;
				Object changeItemColumnValue01 = changeItemObject.get(itemUIField.getName());
				if (changeItemColumnValue01 == null)
				{
					changeItemColumnValue = "";
				}
				else if (changeItemColumnValue01 instanceof String)
				{
					changeItemColumnValue = (String) changeItemColumnValue01;
				}
				else if (changeItemColumnValue01 instanceof Integer)
				{
					changeItemColumnValue = changeItemColumnValue01.toString();
				}
				else
				{
					changeItemColumnValue = ((Number) changeItemColumnValue01).toString();
				}
				foObj.put(UpdatedECSConstants.eciEditBefore, changeItemColumnValue);
			}

			if (null != solveObject.get(itemUIField.getName()))
			{
				String solveItemColumnValue = null;
				Object solveItemColumnValue01 = solveObject.get(itemUIField.getName());
				if (solveItemColumnValue01 == null)
				{
					solveItemColumnValue = "";
				}
				else if (solveItemColumnValue01 instanceof String)
				{
					solveItemColumnValue = (String) solveItemColumnValue01;
				}
				else if (solveItemColumnValue01 instanceof Integer)
				{
					solveItemColumnValue = solveItemColumnValue01.toString();
				}
				else
				{
					solveItemColumnValue = ((Number) solveItemColumnValue01).toString();
				}
				foObj.put(UpdatedECSConstants.eciEditAfter, solveItemColumnValue);
			}
		}
		else if (FieldTypeEnum.FLOAT.equals(itemUIField.getType()))
		{
			foObj.put(UpdatedECSConstants.eciEditBefore, CommonUtil.formatString(classField, changeItemObject));
			foObj.put(UpdatedECSConstants.eciEditAfter, CommonUtil.formatString(classField, solveObject));
		}
		else if (FieldTypeEnum.OBJECT.equals(itemUIField.getType()))
		{
			foObj.put(UpdatedECSConstants.eciEditBefore, changeItemObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME")));
			foObj.put(UpdatedECSConstants.eciEditAfter, solveObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME")));
		}
		else if (FieldTypeEnum.DATE.equals(itemUIField.getType()))
		{
			SimpleDateFormat format = new SimpleDateFormat(DateFormat.PTN_YMD);
			if (null != changeItemObject.get(itemUIField.getName()))
			{
				foObj.put(UpdatedECSConstants.eciEditBefore, format.format(changeItemObject.get(name)));
			}
			if (null != solveObject.get(itemUIField.getName()))
			{
				foObj.put(UpdatedECSConstants.eciEditAfter, format.format(solveObject.get(name)));
			}
		}
		else if (FieldTypeEnum.DATETIME.equals(itemUIField.getType()))
		{
			SimpleDateFormat format = new SimpleDateFormat(DateFormat.PTN_YMDHMS);
			if (null != changeItemObject.get(itemUIField.getName()))
			{
				foObj.put(UpdatedECSConstants.eciEditBefore, format.format(changeItemObject.get(name)));
			}
			if (null != solveObject.get(itemUIField.getName()))
			{
				foObj.put(UpdatedECSConstants.eciEditAfter, format.format(solveObject.get(name)));
			}
		}
		else if (FieldTypeEnum.STATUS.equals(itemUIField.getType()))
		{
			foObj.put(UpdatedECSConstants.eciEditBefore, changeItemObject.get(name + "NAME"));
			foObj.put(UpdatedECSConstants.eciEditAfter, solveObject.get(name + "NAME"));
			// ///////////////////////////////////////////////////////////////////////////////////////
			// ///////////////////////////////////////////////////////////////////////////////////////
		}
		else if (FieldTypeEnum.CODE.equals(itemUIField.getType()) || FieldTypeEnum.CODEREF.equals(itemUIField.getType()))
		{
			String nameTitle = name + (name.endsWith("$") ? "TITLE" : "$TITLE");
			name = name + (name.endsWith("$") ? "NAME" : "$NAME");
			String newCode = "";
			if (changeItemObject.get(nameTitle) != null && !StringUtils.isNullString((String) changeItemObject.get(nameTitle)))
			{
				newCode = StringUtils.getMsrTitle((String) changeItemObject.get(nameTitle), this.stubService.getUserSignature().getLanguageEnum().getType());
			}
			if (changeItemObject.get(name) != null)
			{
				newCode = newCode + "[" + ((String) changeItemObject.get(name)) + "]";
			}

			String oldCode = "";
			if (solveObject.get(nameTitle) != null && !StringUtils.isNullString((String) solveObject.get(nameTitle)))
			{
				oldCode = StringUtils.getMsrTitle((String) solveObject.get(nameTitle), this.stubService.getUserSignature().getLanguageEnum().getType());
			}
			if (solveObject.get(name) != null)
			{
				oldCode = oldCode + "[" + ((String) solveObject.get(name)) + "]";
			}

			foObj.put(UpdatedECSConstants.eciEditBefore, newCode);
			foObj.put(UpdatedECSConstants.eciEditAfter, oldCode);
			// ///////////////////////////////////////////////////////////////////////////////////////
			// ///////////////////////////////////////////////////////////////////////////////////////
		}
		else if (FieldTypeEnum.MULTICODE.equals(itemUIField.getType()))
		{
			String[] changeCodeTitleList = null;
			String[] changeCodeNameList = null;
			String changeMultiString = "";
			if (null != changeItemObject.get(name + (name.endsWith("$") ? "TITLE" : "$TITLE"))
					&& !StringUtils.isNullString((String) changeItemObject.get(name + (name.endsWith("$") ? "TITLE" : "$TITLE"))))
			{
				changeCodeTitleList = ((String) changeItemObject.get(name + (name.endsWith("$") ? "TITLE" : "$TITLE"))).split("/");
			}
			if (null != changeItemObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME"))
					&& !StringUtils.isNullString((String) changeItemObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME"))))
			{
				changeCodeNameList = ((String) changeItemObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME"))).split("/");
			}
			if (null != changeCodeTitleList)
			{
				for (int i = 0; i < changeCodeTitleList.length; i++)
				{
					if (StringUtils.isNullString(changeMultiString))
					{
						changeMultiString = StringUtils.getMsrTitle(changeCodeTitleList[i], this.stubService.getUserSignature().getLanguageEnum().getType()) + "["
								+ changeCodeNameList[i] + "]";
					}
					else
					{
						changeMultiString = changeMultiString + ";"
								+ StringUtils.getMsrTitle(changeCodeTitleList[i], this.stubService.getUserSignature().getLanguageEnum().getType()) + "[" + changeCodeNameList[i]
								+ "]";
					}
				}
			}
			foObj.put(UpdatedECSConstants.eciEditBefore, changeMultiString);
			// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
			String[] solveCodeTitleList = null;
			String[] solveCodeNameList = null;
			String solveMultiString = "";
			if (null != solveObject.get(name + (name.endsWith("$") ? "TITLE" : "$TITLE"))
					&& !StringUtils.isNullString((String) solveObject.get(name + (name.endsWith("$") ? "TITLE" : "$TITLE"))))
			{
				solveCodeTitleList = ((String) solveObject.get(name + (name.endsWith("$") ? "TITLE" : "$TITLE"))).split("/");
			}
			if (null != solveObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME"))
					&& !StringUtils.isNullString((String) solveObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME"))))
			{
				solveCodeNameList = ((String) solveObject.get(name + (name.endsWith("$") ? "NAME" : "$NAME"))).split("/");
			}
			if (null != solveCodeTitleList)
			{
				for (int i = 0; i < solveCodeTitleList.length; i++)
				{
					if (StringUtils.isNullString(solveMultiString))
					{
						solveMultiString = StringUtils.getMsrTitle(solveCodeTitleList[i], this.stubService.getUserSignature().getLanguageEnum().getType()) + "["
								+ solveCodeNameList[i] + "]";
					}
					else
					{
						solveMultiString = solveMultiString + ";" + StringUtils.getMsrTitle(solveCodeTitleList[i], this.stubService.getUserSignature().getLanguageEnum().getType())
								+ "[" + solveCodeNameList[i] + "]";
					}
				}
			}
			foObj.put(UpdatedECSConstants.eciEditAfter, solveMultiString);
		}
		else if (FieldTypeEnum.CLASSIFICATION.equals(itemUIField.getType()))
		{
			if (null != changeItemObject.get(name + "TITLE"))
			{
				foObj.put(UpdatedECSConstants.eciEditBefore,
						StringUtils.getMsrTitle((String) changeItemObject.get(name + "TITLE"), this.stubService.getUserSignature().getLanguageEnum().getType()));
			}
			if (null != solveObject.get(name + "TITLE"))
			{
				foObj.put(UpdatedECSConstants.eciEditAfter,
						StringUtils.getMsrTitle((String) solveObject.get(name + "TITLE"), this.stubService.getUserSignature().getLanguageEnum().getType()));
			}
		}
		else
		{
			foObj.put(UpdatedECSConstants.eciEditBefore, changeItemObject.get(itemUIField.getName()));
			foObj.put(UpdatedECSConstants.eciEditAfter, solveObject.get(itemUIField.getName()));
		}
	}

	public List<FoundationObject> getBomECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName) throws ServiceRequestException
	{
		CodeItemInfo actionTypeAdd = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Add.toString());
		CodeItemInfo actionTypeMod = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Mod.toString());
		CodeItemInfo actionTypeDel = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Del.toString());
		CodeItemInfo actionTypeReplace = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Replace.toString());
		CodeItemInfo changeTypeBom = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.BOM.toString());

		List<FoundationObject> result = new ArrayList<FoundationObject>();

		// EC功能，判断权限，不判断权限
		List<BOMStructure> changeItemBomStructureList = this.listBOMByEnd1ForEc(changeItemObjectGuid, null, relationTemplateName, null);
		List<BOMStructure> solveItemBomStructureList = this.listBOMByEnd1ForEc(solveObjectGuid, null, relationTemplateName, null);
		Map<String, BOMStructure> changeItemMap = new HashMap<String, BOMStructure>();
		List<ClassInfo> classInfoList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
		if (SetUtils.isNullList(classInfoList))
		{
			return null;
		}
		ClassInfo classInfo = classInfoList.get(0);

		if (!SetUtils.isNullList(changeItemBomStructureList))
		{
			for (BOMStructure changeBomStructure : changeItemBomStructureList)
			{
				changeItemMap.put(changeBomStructure.get("DATASEQ").toString(), changeBomStructure);
			}
		}
		if (!SetUtils.isNullList(solveItemBomStructureList))
		{
			for (BOMStructure solveBomStructure : solveItemBomStructureList)
			{
				FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
				BOMStructure changeItemBomStructureMatched = null;
				if (changeItemMap.containsKey(solveBomStructure.get("DATASEQ").toString()))
				{
					changeItemBomStructureMatched = changeItemMap.get(solveBomStructure.get("DATASEQ").toString());
					changeItemMap.remove(solveBomStructure.get("DATASEQ").toString());
					if (solveBomStructure.get("END2$").toString().equals(changeItemBomStructureMatched.get("END2$").toString()))
					{
						foObj.put(UpdatedECSConstants.ActionType, actionTypeMod.getGuid());
						foObj.put(UpdatedECSConstants.ChangeType, changeTypeBom.getGuid());
						foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					}
					else
					{
						foObj.put(UpdatedECSConstants.ActionType, actionTypeReplace.getGuid());
						foObj.put(UpdatedECSConstants.ChangeType, changeTypeBom.getGuid());
						foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					}
					if (changeItemBomStructureMatched != null && !SetUtils.isNullSet(changeItemBomStructureMatched.entrySet()))
					{
						for (Entry<String, Object> entry : changeItemBomStructureMatched.entrySet())
						{
							solveBomStructure.put(entry.getKey() + "_OLD", entry.getValue());
						}
					}

					List<UIField> uiFieldList = this.stubService.getEMM().listListUIField(solveBomStructure.getObjectGuid().getClassName());
					if (SetUtils.isNullList(uiFieldList))
					{
						uiFieldList = new ArrayList<UIField>();
						UIField bomSeq = new UIField();
						bomSeq.setName(BOMStructure.SEQUENCE);
						uiFieldList.add(bomSeq);

						UIField bomQuality = new UIField();
						bomQuality.setName(BOMStructure.QUANTITY);
						uiFieldList.add(bomQuality);
					}

					boolean isValidModify = false;
					for (UIField uiField : uiFieldList)
					{
						if ((solveBomStructure.get(uiField.getName()) == null && solveBomStructure.get(uiField.getName() + "_old") != null)
								|| (solveBomStructure.get(uiField.getName()) != null && solveBomStructure.get(uiField.getName() + "_old") == null)
								|| (solveBomStructure.get(uiField.getName()) != null && solveBomStructure.get(uiField.getName() + "_old") != null
										&& !solveBomStructure.get(uiField.getName()).equals(solveBomStructure.get(uiField.getName() + "_old"))))
						{
							isValidModify = true;
						}
					}
					if (isValidModify || actionTypeReplace.getGuid().equals(foObj.get(UpdatedECSConstants.ActionType)))
					{
						foObj.put(UpdatedECSConstants.Value1, solveBomStructure);
						result.add(foObj);
					}
				}
				else
				{
					foObj.put(UpdatedECSConstants.ActionType, actionTypeAdd.getGuid());
					foObj.put(UpdatedECSConstants.ChangeType, changeTypeBom.getGuid());
					foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					foObj.put(UpdatedECSConstants.Value1, solveBomStructure);
					result.add(foObj);
				}
			}
		}

		if (changeItemMap != null && !SetUtils.isNullSet(changeItemMap.entrySet()))
		{
			for (Entry<String, BOMStructure> changeItemEntry : changeItemMap.entrySet())
			{
				if (changeItemEntry.getValue() != null && !SetUtils.isNullSet(changeItemEntry.getValue().entrySet()))
				{
					Map<String, Object> changeItemMapForECI = new HashMap<String, Object>();
					for (Entry<String, Object> fieldEntry : changeItemEntry.getValue().entrySet())
					{
						changeItemMapForECI.put(fieldEntry.getKey() + "_OLD", fieldEntry.getValue());
					}
					FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
					foObj.put(UpdatedECSConstants.Value1, changeItemMapForECI);
					foObj.put(UpdatedECSConstants.ActionType, actionTypeDel.getGuid());
					foObj.put(UpdatedECSConstants.ChangeType, changeTypeBom.getGuid());
					foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					result.add(foObj);
				}
			}
		}
		return result;
	}

	public List<FoundationObject> getRelationECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName) throws ServiceRequestException
	{
		CodeItemInfo actionTypeAdd = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Add.toString());
		CodeItemInfo actionTypeMod = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Mod.toString());
		CodeItemInfo actionTypeDel = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Del.toString());
		CodeItemInfo actionTypeReplace = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ActionType, UECActionTypeEnum.Replace.toString());
		CodeItemInfo changeTypeRelation = this.stubService.getEMM().getCodeItemByName(UpdatedECSConstants.ChangeType, UECChangeTypeEnum.Relation.toString());

		List<FoundationObject> result = new ArrayList<FoundationObject>();

		// EC功能，判断权限，不判断权限
		List<StructureObject> changeItemBomStructureList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(changeItemObjectGuid,
				relationTemplateName, null, null, null, false);
		List<StructureObject> solveItemBomStructureList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(solveObjectGuid, relationTemplateName,
				null, null, null, false);
		Map<String, StructureObject> changeItemMap = new HashMap<String, StructureObject>();
		List<ClassInfo> classInfoList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
		if (SetUtils.isNullList(classInfoList))
		{
			return null;
		}
		ClassInfo classInfo = classInfoList.get(0);

		if (!SetUtils.isNullList(changeItemBomStructureList))
		{
			for (StructureObject changeBomStructure : changeItemBomStructureList)
			{
				changeItemMap.put(changeBomStructure.get("END2$").toString(), changeBomStructure);
			}
		}
		if (!SetUtils.isNullList(solveItemBomStructureList))
		{
			for (StructureObject solveBomStructure : solveItemBomStructureList)
			{
				FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
				StructureObjectImpl changeItemBomStructureMatched = null;
				if (changeItemMap.containsKey(solveBomStructure.get("END2$").toString()))
				{
					changeItemBomStructureMatched = (StructureObjectImpl) changeItemMap.get(solveBomStructure.get("END2$").toString());
					changeItemMap.remove(solveBomStructure.get("END2$").toString());
					if (solveBomStructure.get("END2$").toString().equals(changeItemBomStructureMatched.get("END2$").toString()))
					{
						foObj.put(UpdatedECSConstants.ActionType, actionTypeMod.getGuid());
						foObj.put(UpdatedECSConstants.ChangeType, changeTypeRelation.getGuid());
						foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					}
					else
					{
						foObj.put(UpdatedECSConstants.ActionType, actionTypeReplace.getGuid());
						foObj.put(UpdatedECSConstants.ChangeType, changeTypeRelation.getGuid());
						foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					}
					if (changeItemBomStructureMatched != null && !SetUtils.isNullSet(changeItemBomStructureMatched.entrySet()))
					{
						for (Entry<String, Object> entry : changeItemBomStructureMatched.entrySet())
						{
							solveBomStructure.put(entry.getKey() + "_OLD", entry.getValue());
						}
					}

					List<UIField> uiFieldList = this.stubService.getEMM().listListUIField(solveBomStructure.getObjectGuid().getClassName());
					boolean isValidModify = false;
					if (!SetUtils.isNullList(uiFieldList))
					{
						for (UIField uiField : uiFieldList)
						{
							if ((solveBomStructure.get(uiField.getName()) == null && solveBomStructure.get(uiField.getName() + "_old") != null)
									|| (solveBomStructure.get(uiField.getName()) != null && solveBomStructure.get(uiField.getName() + "_old") == null)
									|| (solveBomStructure.get(uiField.getName()) != null && solveBomStructure.get(uiField.getName() + "_old") != null
											&& !solveBomStructure.get(uiField.getName()).equals(solveBomStructure.get(uiField.getName() + "_old"))))
							{
								isValidModify = true;
							}
						}
					}
					if (isValidModify || actionTypeReplace.getGuid().equals(foObj.get(UpdatedECSConstants.ActionType)))
					{
						foObj.put(UpdatedECSConstants.Value1, solveBomStructure);
						result.add(foObj);
					}
				}
				else
				{
					foObj.put(UpdatedECSConstants.ActionType, actionTypeAdd.getGuid());
					foObj.put(UpdatedECSConstants.ChangeType, changeTypeRelation.getGuid());
					foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					foObj.put(UpdatedECSConstants.Value1, solveBomStructure);
					result.add(foObj);
				}
			}
		}

		if (changeItemMap != null && !SetUtils.isNullSet(changeItemMap.entrySet()))
		{
			for (Entry<String, StructureObject> changeItemEntry : changeItemMap.entrySet())
			{
				if (changeItemEntry.getValue() != null && !SetUtils.isNullSet(((StructureObjectImpl) changeItemEntry.getValue()).entrySet()))
				{
					Map<String, Object> changeItemMapForECI = new HashMap<String, Object>();
					for (Entry<String, Object> fieldEntry : ((StructureObjectImpl) changeItemEntry.getValue()).entrySet())
					{
						changeItemMapForECI.put(fieldEntry.getKey() + "_OLD", fieldEntry.getValue());
					}
					FoundationObject foObj = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());
					foObj.put(UpdatedECSConstants.Value1, changeItemMapForECI);
					foObj.put(UpdatedECSConstants.ActionType, actionTypeDel.getGuid());
					foObj.put(UpdatedECSConstants.ChangeType, changeTypeRelation.getGuid());
					foObj.put(UpdatedECSConstants.Template, relationTemplateName);
					result.add(foObj);
				}
			}
		}
		return result;
	}

	private void persistenceECI(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		if (changeItemObjectGuid == null || solveObjectGuid == null)
		{
			return;
		}
		List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
		ClassInfo eciClassInfo = null;
		if (!SetUtils.isNullList(listEciClassInfo))
		{
			eciClassInfo = listEciClassInfo.get(0);
		}
		List<FoundationObject> eciList = this.stubService.getUECQueryStub().getEND2ByECTypeEND1ByTemp(ecoObjectGuid, eciClassInfo, UpdatedECSConstants.ECO_ECI$, true);
		if (!SetUtils.isNullList(eciList))
		{
			for (FoundationObject eciFo : eciList)
			{
				CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem((String) eciFo.get("CHANGETYPE"));
				if (!codeItemInfo.getName().equals(UECChangeTypeEnum.Others.name()))
				{
					((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(eciFo.getGuid(), eciFo.getObjectGuid().getClassGuid(), false);
				}
			}
		}

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<FoundationObject> eciFormList = this.getFormECIByECO(changeItemObjectGuid, solveObjectGuid);
		if (!SetUtils.isNullList(eciFormList))
		{
			for (FoundationObject fo : eciFormList)
			{
				this.createECIwhenCancel(fo, ecoObjectGuid);
			}
		}

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByEND1(changeItemObjectGuid);
		if (!SetUtils.isNullList(bomTemplateList))
		{
			for (BOMTemplateInfo bomTemplate : bomTemplateList)
			{
				List<FoundationObject> eciBomList = this.getBomECIByECO(changeItemObjectGuid, solveObjectGuid, bomTemplate.getName());
				if (!SetUtils.isNullList(eciBomList))
				{
					for (FoundationObject fo : eciBomList)
					{
						if (null != fo.get(UpdatedECSConstants.Value1) && !SetUtils.isNullMap((Map<?, ?>) fo.get(UpdatedECSConstants.Value1)))
						{
							Map<String, Object> changeItemMapForECI = (Map<String, Object>) fo.get(UpdatedECSConstants.Value1);
							fo.put(UpdatedECSConstants.Value1, null);
							String jsonStr = JsonUtils.writeJsonStr(changeItemMapForECI);
							if (!StringUtils.isNullString(jsonStr))
							{
								this.stubService.getUECNECOStub().getStringValue(fo, jsonStr, UpdatedECSConstants.Value1, UpdatedECSConstants.Value1, UpdatedECSConstants.Value2);
							}
						}
						this.createECIwhenCancel(fo, ecoObjectGuid);
					}
				}
			}
		}

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<RelationTemplateInfo> relationTemplateList = this.stubService.getEMM().listRelationTemplate(changeItemObjectGuid);
		relationTemplateList.addAll(this.stubService.getEMM().listRelationTemplate4Builtin(changeItemObjectGuid));
		if (!SetUtils.isNullList(relationTemplateList))
		{
			for (RelationTemplateInfo relationTemplate : relationTemplateList)
			{
				List<FoundationObject> eciRelationList = this.getRelationECIByECO(changeItemObjectGuid, solveObjectGuid, relationTemplate.getName());
				if (!SetUtils.isNullList(eciRelationList))
				{
					for (FoundationObject fo : eciRelationList)
					{
						if (null != fo.get(UpdatedECSConstants.Value1) && !SetUtils.isNullMap((Map<?, ?>) fo.get(UpdatedECSConstants.Value1)))
						{
							Map<String, Object> changeItemMapForECI = (Map<String, Object>) fo.get(UpdatedECSConstants.Value1);
							fo.put(UpdatedECSConstants.Value1, null);
							String jsonStr = JsonUtils.writeJsonStr(changeItemMapForECI);
							if (!StringUtils.isNullString(jsonStr))
							{
								this.stubService.getUECNECOStub().getStringValue(fo, jsonStr, UpdatedECSConstants.Value1, UpdatedECSConstants.Value1, UpdatedECSConstants.Value2);
							}
						}
						this.createECIwhenCancel(fo, ecoObjectGuid);
					}
				}
			}
		}
	}

	private void createECIwhenCancel(FoundationObject ecifo, ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		FoundationObject ecoFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ecoObjectGuid, false);
		// 新建设置id
		if (StringUtils.isNullString(ecifo.getId()))
		{
			boolean hasNumberingModelOfId = this.stubService.getEMM().hasNumberingModelByField(null, ecifo.getObjectGuid().getClassName(), SystemClassFieldEnum.ID.getName());
			if (hasNumberingModelOfId)
			{
				ecifo.setId(this.stubService.getBOAS().allocateUniqueId(ecifo));
			}
			else
			{
				ecifo.setId(ecoFoundation.getId());
			}
			if (StringUtils.isNullString(ecifo.getId()))
			{
				ecifo.setId(ecoFoundation.getId());
			}
		}
		FoundationObject foundationObject = this.stubService.getBOAS().createObject(ecifo);
		if (StringUtils.isNullString(foundationObject.getId()))
		{
			ecifo.setId(ecoFoundation.getId());
			this.stubService.getBOAS().saveObject(ecifo);
		}
		this.stubService.getUECNECOStub().linkECO_ECI(ecoFoundation.getObjectGuid(), foundationObject.getObjectGuid(), null);
	}

	public void completeEco(ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecoObjectGuid.getClassGuid());
		boolean isBatchEco = false;
		if (null != ecoClassInfo)
		{
			isBatchEco = ecoClassInfo.hasInterface(ModelInterfaceEnum.IBatchForEc);
		}
		else
		{
			this.checkAndSendMailMessage(ecoObjectGuid);
			return;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!isBatchEco)
			{
				ObjectGuid changeItemObjectGuid = new ObjectGuid();
				ObjectGuid solveObjectGuid = new ObjectGuid();
				List<StructureObject> ecoChangeItemList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
						UpdatedECSConstants.ECO_CHANGEITEM$, null, null, null, false);
				if (!SetUtils.isNullList(ecoChangeItemList))
				{
					changeItemObjectGuid = ecoChangeItemList.get(0).getEnd2ObjectGuid();
				}
				else
				{
					this.checkAndSendMailMessage(ecoObjectGuid);
//					DataServer.getTransactionManager().commitTransaction();
					return;
				}
				List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
						UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
				if (!SetUtils.isNullList(ecoChangeItemAfterList))
				{
					solveObjectGuid = ecoChangeItemAfterList.get(0).getEnd2ObjectGuid();
				}
				else
				{
					this.checkAndSendMailMessage(ecoObjectGuid);
//					DataServer.getTransactionManager().commitTransaction();
					return;
				}
				this.persistenceECI(changeItemObjectGuid, solveObjectGuid, ecoObjectGuid);
				((DSSImpl) this.stubService.getDSS()).getFileInfoStub().copyFile(ecoObjectGuid, solveObjectGuid, false);
			}

			// 修改解决对象，bom和关系的状态////////////////////////////////////////////////////
			if (!isBatchEco)
			{
				ObjectGuid solveObjectGuid = new ObjectGuid();
				List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
						UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
				if (!SetUtils.isNullList(ecoChangeItemAfterList))
				{
					solveObjectGuid = ecoChangeItemAfterList.get(0).getEnd2ObjectGuid();
				}
				else
				{
//					DataServer.getTransactionManager().commitTransaction();
					return;
				}

				((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(solveObjectGuid, SystemStatusEnum.WIP, SystemStatusEnum.ECP, true, false);
			}
			else
			{
				List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
						UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
				if (!SetUtils.isNullList(ecoChangeItemAfterList))
				{
					for (StructureObject structure : ecoChangeItemAfterList)
					{
						ObjectGuid solveObjectGuid = structure.getEnd2ObjectGuid();
						((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(solveObjectGuid, SystemStatusEnum.WIP, SystemStatusEnum.ECP, true, false);
					}
				}
			}
			this.checkAndSendMailMessage(ecoObjectGuid);
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

	// 判断ECN下的ECO是否全部完成，全部完成则要发送邮件
	protected void checkAndSendMailMessage(ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> listECN = this.stubService.getWhereUsedECNByECO(ecoObjectGuid);
		if (!SetUtils.isNullList(listECN))
		{
			FoundationObject ecn = listECN.get(0);
			List<FoundationObject> listeco = this.stubService.getECOByECNAll(ecn.getObjectGuid());
			if (!SetUtils.isNullList(listeco))
			{
				boolean iscom = true;
				for (FoundationObject eco : listeco)
				{
					if (!BooleanUtils.getBooleanByYN((String) eco.get(UpdatedECSConstants.isCompleted)))
					{
						iscom = false;
					}
				}
				if (iscom)
				{
					List<ObjectGuid> objectGuidList = new ArrayList<ObjectGuid>();
					objectGuidList.add(ecn.getObjectGuid());
					String subject1 = "";
					if (ecn.get(UpdatedECSConstants.Subject) != null)
					{
						subject1 = (String) ecn.get(UpdatedECSConstants.Subject);
					}
					// subject:ECO执行通知
					String subject2 = this.stubService.getMSRM().getMSRString("ID_APP_UECS_SENDMAIL_TOECN_CREATER_SUBJECT",
							this.stubService.getUserSignature().getLanguageEnum().toString());
					String msr = ecn.getFullName() + subject1 + subject2;
					this.stubService.getSMS().sendMailToUser(msr, msr, MailCategoryEnum.INFO, objectGuidList, ecn.getCreateUserGuid(), MailMessageType.ECNOTIFY);
				}
			}
		}
	}

	/**
	 * 删除解决对象并备份
	 * 
	 * @param ecoObjectGuid
	 * @throws ServiceRequestException
	 */
	public void deleteSolveObjectAndBackup(FoundationObject ecoFoundation) throws ServiceRequestException
	{
		ObjectGuid ecoObjectGuid = ecoFoundation.getObjectGuid();
		ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecoObjectGuid.getClassGuid());
		boolean isBatchEco = false;
		if (null != ecoClassInfo)
		{
			isBatchEco = ecoClassInfo.hasInterface(ModelInterfaceEnum.IBatchForEc);
		}
		else
		{
			return;
		}
		String userGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
		DSToolService ds = DataServer.getToolService();
		if (!isBatchEco)
		{
			ObjectGuid changeItemObjectGuid = new ObjectGuid();
			ObjectGuid solveObjectGuid = new ObjectGuid();

			List<StructureObject> ecoChangeItemList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
					UpdatedECSConstants.ECO_CHANGEITEM$, null, null, null, false);
			if (!SetUtils.isNullList(ecoChangeItemList))
			{
				changeItemObjectGuid = ecoChangeItemList.get(0).getEnd2ObjectGuid();
			}
			else
			{
				return;
			}
			List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
					UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
			if (!SetUtils.isNullList(ecoChangeItemAfterList))
			{
				solveObjectGuid = ecoChangeItemAfterList.get(0).getEnd2ObjectGuid();
			}
			else
			{
				return;
			}
			this.persistenceECI(changeItemObjectGuid, solveObjectGuid, ecoObjectGuid);

			((DSSImpl) this.stubService.getDSS()).getFileInfoStub().copyFile(ecoObjectGuid, solveObjectGuid, false);

			String sessionId = this.stubService.getSignature().getCredential();
			DataServer.getInstanceService().changeStatus(SystemStatusEnum.ECP, SystemStatusEnum.WIP, solveObjectGuid, false, sessionId);
			((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(solveObjectGuid.getGuid(), solveObjectGuid.getClassGuid(), false);

			ecoFoundation.put("SolveItem", null);
			((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(ecoFoundation, false, false, false, null);
		}

		else
		{
			// 批量变更时,需要删除解决对象和取替代对象
			List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
					UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
			if (!SetUtils.isNullList(ecoChangeItemAfterList))
			{
				// 取得替代对象
				FoundationObject ecof = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ecoObjectGuid, false);
				ObjectGuid rsObjectGuid = this.getTargetItemObjectGuid(ecof);
				String bomTemplateName = (String) ecof.get(UpdatedECSConstants.BOMTemplate);
				for (StructureObject structure : ecoChangeItemAfterList)
				{
					ObjectGuid solveObjectGuid = structure.getEnd2ObjectGuid();
					if (solveObjectGuid != null && StringUtils.isGuid(solveObjectGuid.getGuid()))
					{
						// 删除取替代对象
						this.deleteRepalceObject(solveObjectGuid, rsObjectGuid, bomTemplateName, (String) structure.get(BOMStructure.BOMKEY));
						// 删除解决对象
						String sessionId = this.stubService.getSignature().getCredential();
						DataServer.getInstanceService().changeStatus(SystemStatusEnum.ECP, SystemStatusEnum.WIP, solveObjectGuid, false, sessionId);

						((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(solveObjectGuid.getGuid(), solveObjectGuid.getClassGuid(), false);

						ecoFoundation.put("SolveItem", null);
						((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(ecoFoundation, false, false, false, null);
					}
				}
			}
			// 修改结构
			List<StructureObject> ecoChangeITemList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
					UpdatedECSConstants.ECO_CHANGEITEMBEFORE$, null, null, null, false);
			if (!SetUtils.isNullList(ecoChangeITemList))
			{
				for (StructureObject stucture : ecoChangeITemList)
				{
					stucture.put(UpdatedECSConstants.ECTargetItem, null);
					stucture.put(UpdatedECSConstants.ECTargetItem + UpdatedECSConstants.CLASSNAME, null);
					stucture.put(UpdatedECSConstants.ECTargetItem + UpdatedECSConstants.ClassGuid, null);
					stucture.put(UpdatedECSConstants.ECTargetItem + UpdatedECSConstants.MASTER, null);
					((BOASImpl) this.stubService.getBOAS()).getStructureStub().saveStructure(stucture, false);
				}
			}
		}
	}

	/**
	 * 由于创建自然取代关系时，是局部的替代关系，所以删除也只取局部的替代关系
	 * 
	 * @param solveObjectGuid
	 * @param rsObjectGuid
	 * @param BOMname
	 * @throws ServiceRequestException
	 */
	private void deleteRepalceObject(ObjectGuid solveObjectGuid, ObjectGuid rsObjectGuid, String BOMname, String bomKey) throws ServiceRequestException
	{
		FoundationObject solveObject = this.stubService.getBOAS().getObject(solveObjectGuid);
		if (solveObject != null && rsObjectGuid != null && !StringUtils.isNullString(BOMname))
		{
			ObjectGuid masterObjectGuid = solveObject.getObjectGuid();
			List<FoundationObject> replaceObjectList = ((BRMImpl) this.stubService.getBRM()).getReplaceQueryStub().listReplaceDataByRang(masterObjectGuid, rsObjectGuid,
					ReplaceRangeEnum.PART, ReplaceTypeEnum.QUDAI, BOMname, bomKey, true, false);
			if (!SetUtils.isNullList(replaceObjectList))
			{
				for (FoundationObject replaceObject : replaceObjectList)
				{
					((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(replaceObject.getGuid(), replaceObject.getObjectGuid().getClassGuid(),
							false);
				}
			}
		}

	}

	/**
	 * 如果是自然取代，则获取目标实例的ObjectGuid
	 * 
	 * @param ecoObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private ObjectGuid getTargetItemObjectGuid(FoundationObject ecof) throws ServiceRequestException
	{
		if (ecof != null)
		{
			String modify = (String) ecof.get(UpdatedECSConstants.ModifyType);
			CodeItemInfo modifyTypeCode = this.stubService.getEMM().getCodeItem(modify);
			if (modifyTypeCode != null && modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchReplace.name()))
			{
				String typeGuid = (String) ecof.get(UpdatedECSConstants.Replacepolicy);
				CodeItemInfo typeCode = this.stubService.getEMM().getCodeItem(typeGuid);
				if (typeCode != null && UECReplacepolicyEnum.NaturalReplace.name().equals(typeCode.getName()))
				{
					ObjectGuid targetItem = new ObjectGuid((String) ecof.get((UpdatedECSConstants.TargetItem + ReplaceSubstituteConstants.ClassGuid).toUpperCase()),
							(String) ecof.get((UpdatedECSConstants.TargetItem + ReplaceSubstituteConstants.CLASSNAME).toUpperCase()),
							(String) ecof.get((UpdatedECSConstants.TargetItem).toUpperCase()),
							(String) ecof.get((UpdatedECSConstants.TargetItem + ReplaceSubstituteConstants.MASTER).toUpperCase()), null);
					return targetItem;
				}
			}
		}
		return null;
	}

	public void recoveryEco(ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecoObjectGuid.getClassGuid());
		boolean isBatchEco = false;
		if (null != ecoClassInfo)
		{
			isBatchEco = ecoClassInfo.hasInterface(ModelInterfaceEnum.IBatchForEc);
		}
		else
		{
			return;
		}
		if (!isBatchEco)
		{
			List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
			ClassInfo eciClassInfo = null;
			if (!SetUtils.isNullList(listEciClassInfo))
			{
				eciClassInfo = listEciClassInfo.get(0);
			}
			List<FoundationObject> eciList = this.stubService.getUECQueryStub().getEND2ByECTypeEND1ByTemp(ecoObjectGuid, eciClassInfo, UpdatedECSConstants.ECO_ECI$, true);
			if (!SetUtils.isNullList(eciList))
			{
				for (FoundationObject eciFo : eciList)
				{
					CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem((String) eciFo.get("CHANGETYPE"));
					if (!codeItemInfo.getName().equals(UECChangeTypeEnum.Others.name()))
					{
						((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(eciFo.getGuid(), eciFo.getObjectGuid().getClassGuid(), false);
					}
				}
			}
		}

		String sessionId = this.stubService.getSignature().getCredential();
		// 修改解决对象，bom和关系的状态////////////////////////////////////////////////////
		if (!isBatchEco)
		{
			ObjectGuid solveObjectGuid = new ObjectGuid();
			List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
					UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
			if (!SetUtils.isNullList(ecoChangeItemAfterList))
			{
				solveObjectGuid = ecoChangeItemAfterList.get(0).getEnd2ObjectGuid();
			}
			else
			{
				return;
			}

			DataServer.getInstanceService().changeStatus(SystemStatusEnum.ECP, SystemStatusEnum.WIP, solveObjectGuid, false, sessionId);

			List<BOMView> listBomView = ((BOMSImpl) this.stubService.getBOMS()).getBomViewStub().listBOMView(solveObjectGuid.getObjectGuid(), false);
			if (!SetUtils.isNullList(listBomView))
			{
				for (BOMView bomView : listBomView)
				{
					DataServer.getInstanceService().changeStatus(SystemStatusEnum.ECP, SystemStatusEnum.WIP, bomView.getObjectGuid(), false, sessionId);
				}
			}

			List<ViewObject> listRelView = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listRelation(solveObjectGuid.getObjectGuid(), false, true);
			if (!SetUtils.isNullList(listRelView))
			{
				for (ViewObject viewObject : listRelView)
				{
					DataServer.getInstanceService().changeStatus(SystemStatusEnum.ECP, SystemStatusEnum.WIP, viewObject.getObjectGuid(), false, sessionId);
				}
			}
		}
		else
		{
			List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecoObjectGuid,
					UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
			if (SetUtils.isNullList(ecoChangeItemAfterList))
			{
				return;
			}
			// 取得替代对象
			FoundationObject ecof = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(ecoObjectGuid, false);
			ObjectGuid targetObjectGuid = this.getTargetItemObjectGuid(ecof);
			String bomTemplateName = (String) ecof.get(UpdatedECSConstants.BOMTemplate);
			for (StructureObject structure : ecoChangeItemAfterList)
			{
				ObjectGuid solveObjectGuid = structure.getEnd2ObjectGuid();
				DataServer.getInstanceService().changeStatus(SystemStatusEnum.ECP, SystemStatusEnum.WIP, solveObjectGuid.getObjectGuid(), false, sessionId);

				this.deleteRepalceObject(solveObjectGuid, targetObjectGuid, bomTemplateName, (String) structure.get(BOMStructure.BOMKEY));
				((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(solveObjectGuid.getGuid(), solveObjectGuid.getClassGuid(), false);
			}
		}
	}
}