/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplate
 * Caogc 2010-9-27
 */
package dyna.common.dto.template.bom;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.bom.BOMTemplateInfoMapper;
import dyna.common.systemenum.BomPreciseType;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author JiangHL
 * 
 */
@Cache
@EntryMapper(BOMTemplateInfoMapper.class)
public class BOMTemplateInfo extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= 2613382536824720787L;

	public static final String	GUID					= "GUID";
	public static final String	NAME					= "TEMPLATENAME";
	public static final String	ID						= "TEMPLATEID";
	public static final String	TITLE					= "TITLE";
	public static final String	BM_GUID					= "BMGUID";
	public static final String	END1_BO_TITLE			= "END1BOTITLE";
	public static final String	END1_BO_NAME			= "END1BONAME";
	public static final String	VIEW_CLASS_GUID			= "VIEWCLASSGUID";
	public static final String	VIEW_CLASS_NAME			= "VIEWCLASSNAME";
	public static final String	STRUCTURE_CLASS_GUID	= "STRUCTURECLASSGUID";
	public static final String	STRUCTURE_CLASS_NAME	= "STRUCTURECLASSNAME";
	// public static final String HAS_CHECKOUT_AUTH = "HASCHECKOUTAUTH";

	// 是否允许bom下面有相同master的end2. 1：允许。 0：不允许, Default 0
	public static final String	IS_INCORPORATED_MASTER	= "ISINCORPORATEDMASTER";

	public static final String	CREATE_USER_GUID		= "CREATEUSERGUID";
	public static final String	CREATE_TIME				= "CREATETIME";
	public static final String	UPDATE_USER_GUID		= "UPDATEUSERGUID";
	public static final String	UPDATE_TIME				= "UPDATETIME";

	// sequence start data, Default 10
	public static final String	SEQUENCE_START			= "SEQUENCESTART";

	// sequence increment data, Default 10
	public static final String	SEQUENCE_INCREMENT		= "SEQUENCEINCREMENT";

	// sequence length, Default 4
	public static final String	SEQUENCE_LENGTH			= "SEQUENCELENGTH";

	// sequence前缀, 是否用0填充, Y，用0填充；N，不用0填充；Default N
	public static final String	SEQUENCE_PAD			= "SEQUENCEPAD";

	public static final String	GROUPBMGUID				= "GROUPBMGUID";

	public static final String	PRECISE					= "PRECISE";

	public static final String	IS_VALID				= "ISVALID";

	public static final String	ALL						= "ALL";

	public static final String	DESIGNATORS_SEPARATOR	= "DESIGNATORSEPARATOR";

	public static final String	MOVEFORWARD				= "MOVEFORWARD";

	public static final String	CHECKDESIGNATOR			= "CHECKDESIGNATOR";

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
	 * @return the sequenceIncrement
	 */
	public BigDecimal getSequenceIncrement()
	{
		if (this.get(SEQUENCE_INCREMENT) == null)
		{
			return BigDecimal.valueOf(10);
		}
		else
		{
			return this.get(SEQUENCE_INCREMENT) == null ? null : new BigDecimal(this.get(SEQUENCE_INCREMENT).toString());
		}

	}

	/**
	 * @param sequenceIncrement
	 *            the sequenceIncrement to set
	 */
	public void setSequenceIncrement(BigDecimal sequenceIncrement)
	{
		this.put(SEQUENCE_INCREMENT, sequenceIncrement);
	}

	/**
	 * @return the sequenceLength
	 */
	public BigDecimal getSequenceLength()
	{
		if (this.get(SEQUENCE_LENGTH) == null)
		{
			return BigDecimal.valueOf(4);
		}
		else
		{
			return this.get(SEQUENCE_LENGTH) == null ? null : new BigDecimal(this.get(SEQUENCE_LENGTH).toString());
		}

	}

	/**
	 * @param sequenceLength
	 *            the sequenceLength to set
	 */
	public void setSequenceLength(BigDecimal sequenceLength)
	{

		this.put(SEQUENCE_LENGTH, sequenceLength);

	}

	/**
	 * @param sequencePad
	 *            the sequencePad to set
	 */
	public void setSequencePad(boolean sequencePad)
	{
		this.put(SEQUENCE_PAD, BooleanUtils.getBooleanStringYN(sequencePad));
	}

	/**
	 * @return the sequencePad
	 */
	public boolean getSequencePad()
	{
		if (this.get(SEQUENCE_PAD) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(SEQUENCE_PAD));
	}

	/**
	 * @return the sequenceStart
	 */
	public BigDecimal getSequenceStart()
	{
		if (this.get(SEQUENCE_START) == null)
		{
			return BigDecimal.valueOf(10);
		}
		else
		{
			return this.get(SEQUENCE_START) == null ? null : new BigDecimal(this.get(SEQUENCE_START).toString());
		}
	}

	/**
	 * @param sequenceStart
	 *            the sequenceStart to set
	 */
	public void setSequenceStart(BigDecimal sequenceStart)
	{

		this.put(SEQUENCE_START, sequenceStart);

	}

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return (String) this.get(BM_GUID);
	}

	/**
	 * @return the createtime
	 */
	public Date getCreatetime()
	{
		return (Date) this.get(CREATE_TIME);
	}

	/**
	 * @return the createuserGuid
	 */
	public String getCreateuserGuid()
	{
		return (String) this.get(CREATE_USER_GUID);
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
	public String getEnd1BoTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(END1_BO_TITLE), lang.getType());
	}

	/**
	 * @return the guid
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
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(NAME);
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
	 * @return the updateTime
	 */
	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get(UPDATE_TIME);
	}

	/**
	 * @return the updateUserGuid
	 */
	@Override
	public String getUpdateUserGuid()
	{
		return (String) this.get(UPDATE_USER_GUID);
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

	public boolean isIncorporatedMaster()
	{
		if (this.get(IS_INCORPORATED_MASTER) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(IS_INCORPORATED_MASTER));
		// return "0".equals(this.get(IS_INCORPORATED_MASTER));
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
	 * @param end1BoName
	 *            the end1BoName to set
	 */
	public void setEnd1BoName(String end1BoName)
	{
		this.put(END1_BO_NAME, end1BoName);
	}

	public void setEnd1BoTitle(String title)
	{
		this.put(END1_BO_TITLE, title);
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
	 * @param NAME
	 *            the NAME to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public void setIsIncorporatedMaster(Boolean isIncorporatedMaster)
	{
		// this.put(IS_INCORPORATED_MASTER, BooleanUtils.getBooleanString01(isIncorporatedMaster));
		this.put(IS_INCORPORATED_MASTER, BooleanUtils.getBooleanString10(isIncorporatedMaster));
	}

	/**
	 * @param structureClassGuid
	 *            the structureClassGuid to set
	 */
	public void setStructureClassGuid(String structureClassGuid)
	{
		this.put(STRUCTURE_CLASS_GUID, structureClassGuid);
	}

	/**
	 * @param structureClassGuid
	 *            the structureClassGuid to set
	 */
	public void setStructureClassName(String structureClassName)
	{
		this.put(STRUCTURE_CLASS_NAME, structureClassName);
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

	public void setPrecise(BomPreciseType type)
	{
		this.put(PRECISE, type.getValue());
	}

	public BomPreciseType getPrecise()
	{
		if (StringUtils.isNullString((String) this.get(PRECISE)))
		{
			return BomPreciseType.USERSPECIFIED;
		}
		else
		{
			return BomPreciseType.getEnum((String) this.get(PRECISE));
		}
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

	public String getDesignatorsSeparatorChar()
	{
		return (String) this.get(DESIGNATORS_SEPARATOR);
	}

	public void setDesignatorsSeparator(String separatorChar)
	{
		this.put(DESIGNATORS_SEPARATOR, separatorChar);
	}

	public boolean isMoveForWard()
	{
		if (this.get(MOVEFORWARD) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(MOVEFORWARD));
	}

	public boolean needCheckDesignator()
	{
		if (this.get(CHECKDESIGNATOR) == null)
		{
			return true;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(CHECKDESIGNATOR));
	}

	public void setMoveForward(boolean moveForward)
	{
		this.put(MOVEFORWARD, BooleanUtils.getBooleanStringYN(moveForward));
	}

	public void setNeedCheckDesignator(boolean needCheckDesignator)
	{
		this.put(CHECKDESIGNATOR, BooleanUtils.getBooleanStringYN(needCheckDesignator));
	}
}
