/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplate
 * Caogc 2010-8-18
 */
package dyna.common.bean.data.template;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.RelationTemplateActionEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;

/**
 * 
 * @author Caogc
 * 
 */
public class RelationTemplate extends SystemObjectImpl implements SystemObject
{
	private static final long			serialVersionUID			= -5466625404445108685L;

	private final RelationTemplateInfo	info;

	private List<RelationTemplateEnd2>	relationTemplateEnd2List	= null;

	public RelationTemplate()
	{
		this.info = new RelationTemplateInfo();
	}

	public RelationTemplate(RelationTemplateInfo info)
	{
		this.info = info;
	}

	public RelationTemplateInfo getInfo()
	{
		return this.info;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType()
	{
		return this.info.getTemplateType();
	}

	public void setTitle(String title)
	{
		this.info.setTitle(title);
	}

	public String getTitle()
	{
		return this.info.getTitle();
	}
	
	/**
	 * @return the title
	 */
	public String getTitle(LanguageEnum lang)
	{
		return this.info.getTitle(lang);
	}

	/**
	 * @param templateType
	 *            the templateType to set
	 */
	public void setTemplateType(String templateType)
	{
		this.info.setTemplateType(templateType);
	}

	/**
	 * @return the end2Interface
	 */
	public ModelInterfaceEnum getEnd2Interface()
	{
		return this.info.getEnd2Interface();
	}

	/**
	 * @param end2Interface
	 *            the end2Interface to set
	 */
	public void setEnd2Interface(ModelInterfaceEnum end2Interface)
	{
		this.info.setEnd2Interface(end2Interface);
	}

	/**
	 * @return the structureModel
	 */
	public RelationTemplateTypeEnum getStructureModel()
	{
		return this.info.getStructureModel();
	}

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return this.info.getBmGuid();
	}

	/**
	 * @return the cancelChkoutTrigger
	 */
	public RelationTemplateActionEnum getCancelChkoutTrigger()
	{
		return this.info.getCancelChkoutTrigger();
	}

	/**
	 * @return the changeLocationTrigger
	 */
	public RelationTemplateActionEnum getChangeLocationTrigger()
	{
		return this.info.getChangeLocationTrigger();
	}

	/**
	 * @return the checkinTrigger
	 */
	public RelationTemplateActionEnum getCheckinTrigger()
	{
		return this.info.getCheckinTrigger();
	}

	/**
	 * @return the checkoutTrigger
	 */
	public RelationTemplateActionEnum getCheckoutTrigger()
	{
		return this.info.getCheckoutTrigger();
	}

	/**
	 * @return the saveAsTrigger
	 */
	public RelationTemplateActionEnum getSaveAsTrigger()
	{
		return this.info.getSaveAsTrigger();
	}

	/**
	 * @return the deleteTrigger
	 */
	public RelationTemplateActionEnum getDeleteTrigger()
	{
		return this.info.getDeleteTrigger();
	}

	/**
	 * @return the end1boname
	 */
	public String getEnd1BoName()
	{
		return this.info.getEnd1BoName();
	}

	/**
	 * @return the end1botitle
	 */
	public String getEnd1BoTitle()
	{
		return this.info.getEnd1BoTitle();
	}
	
	/**
	 * @return the title
	 */
	public String getEnd1BoTitle(LanguageEnum lang)
	{
		return this.info.getEnd1BoTitle(lang);
	}

	/**
	 * @return the end2Type
	 */
	public String getEnd2Type()
	{
		return this.info.getEnd2Type();
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return this.info.getGuid();
	}

	public String getId()
	{
		return this.info.getId();
	}

	/**
	 * @return the ReceiveTime
	 */
	public Integer getMaxQuantity()
	{
		return this.info.getMaxQuantity();
	}

	/**
	 * @return the NAME
	 */
	@Override
	public String getName()
	{
		return this.info.getName();
	}

	/**
	 * @return the reviseTrigger
	 */
	public RelationTemplateActionEnum getReviseTrigger()
	{
		return this.info.getReviseTrigger();
	}

	/**
	 * @return the structureClassGuid
	 */
	public String getStructureClassGuid()
	{
		return this.info.getStructureClassGuid();
	}

	/**
	 * @return the structureClassName
	 */
	public String getStructureClassName()
	{
		return this.info.getStructureClassName();
	}

	/**
	 * @return the traschkoutTrigger
	 */
	public RelationTemplateActionEnum getTraschkoutTrigger()
	{
		return this.info.getTraschkoutTrigger();
	}

	/**
	 * @return the viewClassGuid
	 */
	public String getViewClassGuid()
	{
		return this.info.getViewClassGuid();
	}

	/**
	 * @return the viewClassName
	 */
	public String getViewClassName()
	{
		return this.info.getViewClassName();
	}

	/**
	 * @param bmGuid
	 *            the bmGuid to set
	 */
	public void setBmGuid(String bmGuid)
	{
		this.info.setBmGuid(bmGuid);
	}

	/**
	 * @param cancelChkoutTrigger
	 *            the cancelChkoutTrigger to set
	 */
	public void setCancelChkoutTrigger(RelationTemplateActionEnum cancelChkoutTrigger)
	{
		this.info.setCancelChkoutTrigger(cancelChkoutTrigger);
	}

	/**
	 * @param changeLocationTrigger
	 *            the changeLocationTrigger to set
	 */
	public void setChangeLocationTrigger(RelationTemplateActionEnum changeLocationTrigger)
	{
		this.info.setChangeLocationTrigger(changeLocationTrigger);
	}

	/**
	 * @param checkinTrigger
	 *            the checkinTrigger to set
	 */
	public void setCheckinTrigger(RelationTemplateActionEnum checkinTrigger)
	{
		this.info.setCheckinTrigger(checkinTrigger);
	}

	/**
	 * @param checkoutTrigger
	 *            the checkoutTrigger to set
	 */
	public void setCheckoutTrigger(RelationTemplateActionEnum checkoutTrigger)
	{
		this.info.setCheckoutTrigger(checkoutTrigger);
	}

	/**
	 * @param deleteTrigger
	 *            the deleteTrigger to set
	 */
	public void setDeleteTrigger(RelationTemplateActionEnum deleteTrigger)
	{
		this.info.setDeleteTrigger(deleteTrigger);
	}

	/**
	 * @param saveAsTrigger
	 *            the saveAsTrigger to set
	 */
	public void setSaveAsTrigger(RelationTemplateActionEnum saveAsTrigger)
	{
		this.info.setSaveAsTrigger(saveAsTrigger);
	}

	/**
	 * @param end1BoName
	 *            the end1BoName to set
	 */
	public void setEnd1BoName(String end1BoName)
	{
		this.info.setEnd1BoName(end1BoName);
	}

	/**
	 * @param end2Type
	 *            the end2Type to set
	 */
	public void setEnd2Type(String end2Type)
	{
		this.info.setEnd2Type(end2Type);
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.info.setGuid(guid);
	}

	public void setId(String id)
	{
		this.info.setId(id);
	}

	/**
	 * @param structureModel
	 *            the structureModel to set
	 */
	public void setStructureModel(RelationTemplateTypeEnum structureModel)
	{
		this.info.setStructureModel(structureModel);
	}

	/**
	 * @param maxQuantity
	 *            the maxQuantity to set
	 */
	public void setMaxQuantity(Integer maxQuantity)
	{
		this.info.setMaxQuantity(maxQuantity);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	/**
	 * @param reviseTrigger
	 *            the reviseTrigger to set
	 */
	public void setReviseTrigger(RelationTemplateActionEnum reviseTrigger)
	{
		this.info.setReviseTrigger(reviseTrigger);
	}

	/**
	 * @param structureClassGuid
	 *            the structureClassGuid to set
	 */
	public void setStructureClassGuid(String structureClassGuid)
	{
		this.info.setStructureClassGuid(structureClassGuid);
	}

	public void setStructureClassName(String structureClassName)
	{
		this.info.setStructureClassName(structureClassName);
	}

	/**
	 * @param traschkoutTrigger
	 *            the traschkoutTrigger to set
	 */
	public void setTraschkoutTrigger(RelationTemplateActionEnum traschkoutTrigger)
	{
		this.info.setTraschkoutTrigger(traschkoutTrigger);
	}

	/**
	 * @param viewClassGuid
	 *            the viewClassGuid to set
	 */
	public void setViewClassGuid(String viewClassGuid)
	{
		this.info.setViewClassGuid(viewClassGuid);
	}

	public void setViewClassName(String viewClassName)
	{
		this.info.setViewClassName(viewClassName);
	}

	public void setIsIncorporatdMaster(boolean isIncorporatdMaster)
	{
		this.info.setIsIncorporatdMaster(isIncorporatdMaster);
	}

	public boolean isIncorporatedMaster()
	{
		return this.info.isIncorporatedMaster();
	}

	public void setReviseCopyRelation(boolean RevisionCopyRelation)
	{
		this.info.setReviseCopyRelation(RevisionCopyRelation);
	}

	public boolean isReviseCopyRelation()
	{
		return this.info.isReviseCopyRelation();
	}

	public void setSaveAsCopyRelation(boolean saveAsCopyRelation)
	{
		this.info.setSaveAsCopyRelation(saveAsCopyRelation);
	}

	public boolean isSaveAsCopyRelation()
	{
		return this.info.isSaveAsCopyRelation();
	}

	public void setValid(boolean isValid)
	{
		this.info.setValid(isValid);
	}

	@Override
	public boolean isValid()
	{
		return this.info.isValid();
	}

	public void setEnd1BoTitle(String title)
	{
		this.info.setEnd1BoTitle(title);
	}

	public List<RelationTemplateEnd2> getRelationTemplateEnd2List()
	{
		return relationTemplateEnd2List;
	}

	public void setRelationTemplateEnd2List(List<RelationTemplateEnd2> relationTemplateEnd2List)
	{
		this.relationTemplateEnd2List = relationTemplateEnd2List;
	}
	
	public boolean canOpposite()
	{
		return this.info.canOpposite();
	}

	public void setCanOpposite(boolean canOpposite)
	{
		this.info.setCanOpposite(canOpposite);
	}
	
	public void setIsRecordHasEnd2Data(boolean isRecordHasEnd2Data)
	{
		this.info.setIsRecordHasEnd2Data(isRecordHasEnd2Data);
	}

	public boolean isIsRecordHasEnd2Data()
	{
		return this.info.isIsRecordHasEnd2Data();
	}

	public String getFieldForRecordHasEnd2Data()
	{
		return this.info.getFieldForRecordHasEnd2Data();
	}

	public void setFieldForRecordHasEnd2Data(String fieldForRecordHasEnd2Data)
	{
		this.info.setFieldForRecordHasEnd2Data(fieldForRecordHasEnd2Data);
	}
}
