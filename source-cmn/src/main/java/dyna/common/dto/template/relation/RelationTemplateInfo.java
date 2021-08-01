/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplate
 * Caogc 2010-8-18
 */
package dyna.common.dto.template.relation;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.relation.RelationTemplateInfoMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.RelationTemplateActionEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;

/**
 * 
 * @author Caogc
 * 
 */
@Cache
@EntryMapper(RelationTemplateInfoMapper.class)
public class RelationTemplateInfo extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID			= -5466625404445108685L;

	public static final String	GUID						= "GUID";
	public static final String	ID							= "TEMPLATEID";
	public static final String	NAME						= "TEMPLATENAME";
	public static final String	TITLE						= "TITLE";
	// 1、MASTER；2、REVISION
	public static final String	END2_TYPE					= "END2TYPE";
	public static final String	BM_GUID						= "BMGUID";
	public static final String	END1_BO_TITLE				= "END1BOTITLE";
	public static final String	END1_BO_NAME				= "END1BONAME";
	public static final String	VIEW_CLASS_GUID				= "VIEWCLASSGUID";
	public static final String	VIEW_CLASS_NAME				= "VIEWCLASSNAME";
	public static final String	STRUCTURE_CLASS_GUID		= "STRUCTURECLASSGUID";
	public static final String	STRUCTURE_CLASS_NAME		= "STRUCTURECLASSNAME";
	public static final String	MAX_QUANTITY				= "MAXQUANTITY";
	public static final String	END2_INTERFACE				= "END2INTERFACE";

	// 是否允许relation下面有相同master的end2. 1：允许。 0：不允许, Default 0
	public static final String	IS_INCORPORATED_MASTER		= "ISINCORPORATEDMASTER";

	// 0时是内置模板 1时是非内置模板
	public static final String	TEMPLATE_TYPE				= "TEMPLATETYPE";
	// 是否在父阶上记录子阶数据存在
	public static final String	RECORD_HAS_END2				= "ISRECORDHASEND2";
	// 用以记录是否存在子阶数据的父阶字段
	public static final String	FIELD_FOR_RECORD_HAS_END2	= "FIELDFORRECORDHASEND2";

	public static final String	STRUCTURE_MODEL				= "STRUCTUREMODEL";
	public static final String	REVISE_TRIGGER				= "REVISETRIGGER";
	public static final String	CHECKIN_TRIGGER				= "CHECKINTRIGGER";
	public static final String	CHECKOUT_TRIGGER			= "CHECKOUTTRIGGER";
	public static final String	TRASCHKOUT_TRIGGER			= "TRASCHKOUTTRIGGER";
	public static final String	CANCEL_CHKOUT_TRIGGER		= "CANCELCHKOUTTRIGGER";
	public static final String	CHANGE_LOCATION_TRIGGER		= "CHANGELOCATIONTRIGGER";
	public static final String	DELETE_TRIGGER				= "DELETETRIGGER";
	public static final String	SAVEAS_TRIGGER				= "SAVEASTRIGGER";

	// 修订时，复制关系
	public static final String	REVISIE_COPY_RELATION		= "REVISECOPYRELATION";
	// 另存时，复制关系
	public static final String	SAVEAS_COPY_RELATION		= "SAVEASCOPYRELATION";

	public static final String	IS_VALID					= "ISVALID";

	private boolean				canOpposite					= false;

	/**
	 * @return the templateType
	 */
	public String getTemplateType()
	{
		return (String) this.get(TEMPLATE_TYPE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	/**
	 * @return the title
	 */
	public String getTitle(LanguageEnum lang)
	{
		String title = this.getTitle();
		return StringUtils.getMsrTitle(title, lang.getType());
	}

	/**
	 * @param templateType
	 *            the templateType to set
	 */
	public void setTemplateType(String templateType)
	{
		this.put(TEMPLATE_TYPE, templateType);
	}

	/**
	 * @return the end2Interface
	 */
	public ModelInterfaceEnum getEnd2Interface()
	{
		if (this.get(END2_INTERFACE) == null || "".equals(this.get(END2_INTERFACE)))
		{
			return null;
		}
		return ModelInterfaceEnum.valueOf((String) this.get(END2_INTERFACE));
	}

	/**
	 * @param end2Interface
	 *            the end2Interface to set
	 */
	public void setEnd2Interface(ModelInterfaceEnum end2Interface)
	{
		this.put(END2_INTERFACE, end2Interface.toString());
	}

	/**
	 * @return the structureModel
	 */
	public RelationTemplateTypeEnum getStructureModel()
	{
		if (this.get(STRUCTURE_MODEL) == null)
		{
			return RelationTemplateTypeEnum.NORMAL;
		}

		for (RelationTemplateTypeEnum typeEnum : RelationTemplateTypeEnum.values())
		{
			if (typeEnum.toString().equals(this.get(STRUCTURE_MODEL)))
			{
				return typeEnum;
			}
		}

		return RelationTemplateTypeEnum.NORMAL;
	}

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return (String) this.get(BM_GUID);
	}

	/**
	 * @return the cancelChkoutTrigger
	 */
	public RelationTemplateActionEnum getCancelChkoutTrigger()
	{
		return this.getActionTrigger(CANCEL_CHKOUT_TRIGGER);
	}

	/**
	 * @return the changeLocationTrigger
	 */
	public RelationTemplateActionEnum getChangeLocationTrigger()
	{
		return this.getActionTrigger(CHANGE_LOCATION_TRIGGER);
	}

	/**
	 * @return the checkinTrigger
	 */
	public RelationTemplateActionEnum getCheckinTrigger()
	{
		return this.getActionTrigger(CHECKIN_TRIGGER);
	}

	/**
	 * @return the checkoutTrigger
	 */
	public RelationTemplateActionEnum getCheckoutTrigger()
	{
		return this.getActionTrigger(CHECKOUT_TRIGGER);
	}

	/**
	 * @return the saveAsTrigger
	 */
	public RelationTemplateActionEnum getSaveAsTrigger()
	{
		return this.getActionTrigger(SAVEAS_TRIGGER);
	}

	/**
	 * @return the deleteTrigger
	 */
	public RelationTemplateActionEnum getDeleteTrigger()
	{
		return this.getActionTrigger(DELETE_TRIGGER);
	}

	public RelationTemplateActionEnum getActionTrigger(String trigger)
	{
		if (this.get(trigger) == null)
		{
			return RelationTemplateActionEnum.NONE;
		}
		for (RelationTemplateActionEnum triggerEnum : RelationTemplateActionEnum.values())
		{
			if (triggerEnum.toString().equals(this.get(trigger)))
			{
				return triggerEnum;
			}
		}
		return RelationTemplateActionEnum.NONE;
	}

	/**
	 * @return the end1boname
	 */
	public String getEnd1BoName()
	{
		return (String) this.get(END1_BO_NAME);
	}

	/**
	 * @return the end1botitle
	 */
	public String getEnd1BoTitle()
	{
		return (String) this.get(END1_BO_TITLE);
	}

	/**
	 * @return the title
	 */
	public String getEnd1BoTitle(LanguageEnum lang)
	{
		String title = this.getEnd1BoTitle();
		return StringUtils.getMsrTitle(title, lang.getType());
	}

	/**
	 * @return the end2Type
	 */
	public String getEnd2Type()
	{
		return (String) this.get(END2_TYPE);
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	public String getId()
	{
		return (String) this.get(ID);
	}

	/**
	 * @return the ReceiveTime
	 */
	public Integer getMaxQuantity()
	{
		if (this.get(MAX_QUANTITY) == null)
		{
			return 0;
		}
		else
		{
			return ((Number) this.get(MAX_QUANTITY)).intValue();
		}
	}

	/**
	 * @return the NAME
	 */
	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	/**
	 * @return the reviseTrigger
	 */
	public RelationTemplateActionEnum getReviseTrigger()
	{
		return this.getActionTrigger(REVISE_TRIGGER);
	}

	/**
	 * @return the structureClassGuid
	 */
	public String getStructureClassGuid()
	{
		return (String) this.get(STRUCTURE_CLASS_GUID);
	}

	/**
	 * @return the structureClassName
	 */
	public String getStructureClassName()
	{
		return (String) this.get(STRUCTURE_CLASS_NAME);
	}

	/**
	 * @return the traschkoutTrigger
	 */
	public RelationTemplateActionEnum getTraschkoutTrigger()
	{
		return this.getActionTrigger(TRASCHKOUT_TRIGGER);
	}

	/**
	 * @return the viewClassGuid
	 */
	public String getViewClassGuid()
	{
		return (String) this.get(VIEW_CLASS_GUID);
	}

	/**
	 * @return the viewClassName
	 */
	public String getViewClassName()
	{
		return (String) this.get(VIEW_CLASS_NAME);
	}

	/**
	 * @param bmGuid
	 *            the bmGuid to set
	 */
	public void setBmGuid(String bmGuid)
	{
		this.put(BM_GUID, bmGuid);
	}

	/**
	 * @param cancelChkoutTrigger
	 *            the cancelChkoutTrigger to set
	 */
	public void setCancelChkoutTrigger(RelationTemplateActionEnum cancelChkoutTrigger)
	{
		this.put(CANCEL_CHKOUT_TRIGGER, cancelChkoutTrigger.toString());
	}

	/**
	 * @param changeLocationTrigger
	 *            the changeLocationTrigger to set
	 */
	public void setChangeLocationTrigger(RelationTemplateActionEnum changeLocationTrigger)
	{
		this.put(CHANGE_LOCATION_TRIGGER, changeLocationTrigger.toString());
	}

	/**
	 * @param checkinTrigger
	 *            the checkinTrigger to set
	 */
	public void setCheckinTrigger(RelationTemplateActionEnum checkinTrigger)
	{
		this.put(CHECKIN_TRIGGER, checkinTrigger.toString());
	}

	/**
	 * @param checkoutTrigger
	 *            the checkoutTrigger to set
	 */
	public void setCheckoutTrigger(RelationTemplateActionEnum checkoutTrigger)
	{
		this.put(CHECKOUT_TRIGGER, checkoutTrigger.toString());
	}

	/**
	 * @param deleteTrigger
	 *            the deleteTrigger to set
	 */
	public void setDeleteTrigger(RelationTemplateActionEnum deleteTrigger)
	{
		this.put(DELETE_TRIGGER, deleteTrigger.toString());
	}

	/**
	 * @param saveAsTrigger
	 *            the saveAsTrigger to set
	 */
	public void setSaveAsTrigger(RelationTemplateActionEnum saveAsTrigger)
	{
		this.put(SAVEAS_TRIGGER, saveAsTrigger.toString());
	}

	/**
	 * @param end1BoName
	 *            the end1BoName to set
	 */
	public void setEnd1BoName(String end1BoName)
	{
		this.put(END1_BO_NAME, end1BoName);
	}

	/**
	 * @param end2Type
	 *            the end2Type to set
	 */
	public void setEnd2Type(String end2Type)
	{
		this.put(END2_TYPE, end2Type);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	public void setId(String id)
	{
		this.put(ID, id);
	}

	/**
	 * @param structureModel
	 *            the structureModel to set
	 */
	public void setStructureModel(RelationTemplateTypeEnum structureModel)
	{
		this.put(STRUCTURE_MODEL, structureModel.toString());
	}

	/**
	 * @param maxQuantity
	 *            the maxQuantity to set
	 */
	public void setMaxQuantity(Integer maxQuantity)
	{
		this.put(MAX_QUANTITY, new BigDecimal(maxQuantity));
	}

	/**
	 * @param NAME
	 *            the NAME to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	/**
	 * @param reviseTrigger
	 *            the reviseTrigger to set
	 */
	public void setReviseTrigger(RelationTemplateActionEnum reviseTrigger)
	{
		this.put(REVISE_TRIGGER, reviseTrigger.toString());
	}

	/**
	 * @param structureClassGuid
	 *            the structureClassGuid to set
	 */
	public void setStructureClassGuid(String structureClassGuid)
	{
		this.put(STRUCTURE_CLASS_GUID, structureClassGuid);
	}

	public void setStructureClassName(String structureClassName)
	{
		this.put(STRUCTURE_CLASS_NAME, structureClassName);
	}

	/**
	 * @param traschkoutTrigger
	 *            the traschkoutTrigger to set
	 */
	public void setTraschkoutTrigger(RelationTemplateActionEnum traschkoutTrigger)
	{
		this.put(TRASCHKOUT_TRIGGER, traschkoutTrigger.toString());
	}

	/**
	 * @param viewClassGuid
	 *            the viewClassGuid to set
	 */
	public void setViewClassGuid(String viewClassGuid)
	{
		this.put(VIEW_CLASS_GUID, viewClassGuid);
	}

	public void setViewClassName(String viewClassName)
	{
		this.put(VIEW_CLASS_NAME, viewClassName);
	}

	public void setIsIncorporatdMaster(boolean isIncorporatdMaster)
	{
		// this.put(IS_INCORPORATED_MASTER, BooleanUtils.getBooleanString01(isIncorporatdMaster));
		this.put(IS_INCORPORATED_MASTER, BooleanUtils.getBooleanString10(isIncorporatdMaster));
	}

	public boolean isIncorporatedMaster()
	{
		if (this.get(IS_INCORPORATED_MASTER) == null)
		{
			return false;
		}

		// return "0".equals(this.get(IS_INCORPORATED_MASTER));
		return BooleanUtils.getBooleanBy10((String) this.get(IS_INCORPORATED_MASTER));
	}

	public void setReviseCopyRelation(boolean RevisionCopyRelation)
	{
		this.put(REVISIE_COPY_RELATION, BooleanUtils.getBooleanString10(RevisionCopyRelation));
	}

	public boolean isReviseCopyRelation()
	{
		if (this.get(REVISIE_COPY_RELATION) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(REVISIE_COPY_RELATION));
	}

	public void setSaveAsCopyRelation(boolean saveAsCopyRelation)
	{
		this.put(SAVEAS_COPY_RELATION, BooleanUtils.getBooleanString10(saveAsCopyRelation));
	}

	public boolean isSaveAsCopyRelation()
	{
		if (this.get(SAVEAS_COPY_RELATION) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(SAVEAS_COPY_RELATION));
	}

	public void setValid(boolean isValid)
	{
		this.put(IS_VALID, BooleanUtils.getBooleanString10(isValid));
	}

	@Override
	public boolean isValid()
	{
		if (this.get(IS_VALID) == null)
		{
			return true;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(IS_VALID));
	}

	public void setEnd1BoTitle(String title)
	{
		this.put(END1_BO_TITLE, title);
	}

	public boolean canOpposite()
	{
		return this.canOpposite;
	}

	public void setCanOpposite(boolean canOpposite)
	{
		this.canOpposite = canOpposite;
	}

	public void setIsRecordHasEnd2Data(boolean isRecordHasEnd2Data)
	{
		this.put(RECORD_HAS_END2, BooleanUtils.getBooleanString10(isRecordHasEnd2Data));
	}

	public boolean isIsRecordHasEnd2Data()
	{
		if (this.get(RECORD_HAS_END2) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(RECORD_HAS_END2));
	}

	public String getFieldForRecordHasEnd2Data()
	{
		return (String) this.get(FIELD_FOR_RECORD_HAS_END2);
	}

	public void setFieldForRecordHasEnd2Data(String fieldForRecordHasEnd2Data)
	{
		this.put(FIELD_FOR_RECORD_HAS_END2, fieldForRecordHasEnd2Data);
	}
}
