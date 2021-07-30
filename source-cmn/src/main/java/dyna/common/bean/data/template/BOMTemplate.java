/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplate
 * Caogc 2010-9-27
 */
package dyna.common.bean.data.template;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.template.bom.BOMReportTemplate;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.systemenum.BomPreciseType;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * 
 * @author JiangHL
 * 
 */
public class BOMTemplate extends SystemObjectImpl implements SystemObject
{
	private static final long		serialVersionUID		= 2613382536824720787L;

	private final BOMTemplateInfo	info;

	private List<BOMTemplateEnd2>	bomTemplateEnd2List		= null;

	private List<BOMReportTemplate>	bomReportTemplateList	= null;

	public BOMTemplate()
	{
		this.info = new BOMTemplateInfo();
	}

	public BOMTemplate(BOMTemplateInfo info)
	{
		this.info = info;
	}

	public BOMTemplateInfo getInfo()
	{
		return this.info;
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
		String title = this.getTitle();
		return StringUtils.getMsrTitle(title, lang.getType());
	}

	/**
	 * @return the sequenceIncrement
	 */
	public BigDecimal getSequenceIncrement()
	{
		return this.info.getSequenceIncrement();
	}

	/**
	 * @param sequenceIncrement
	 *            the sequenceIncrement to set
	 */
	public void setSequenceIncrement(BigDecimal sequenceIncrement)
	{
		this.info.setSequenceIncrement(sequenceIncrement);
	}

	/**
	 * @return the sequenceLength
	 */
	public BigDecimal getSequenceLength()
	{
		return this.info.getSequenceLength();
	}

	/**
	 * @param sequenceLength
	 *            the sequenceLength to set
	 */
	public void setSequenceLength(BigDecimal sequenceLength)
	{
		this.info.setSequenceLength(sequenceLength);
	}

	/**
	 * @param sequencePad
	 *            the sequencePad to set
	 */
	public void setSequencePad(boolean sequencePad)
	{
		this.info.setSequencePad(sequencePad);
	}

	/**
	 * @return the sequencePad
	 */
	public boolean getSequencePad()
	{
		return this.info.getSequencePad();
	}

	/**
	 * @return the sequenceStart
	 */
	public BigDecimal getSequenceStart()
	{
		return this.info.getSequenceStart();
	}

	/**
	 * @param sequenceStart
	 *            the sequenceStart to set
	 */
	public void setSequenceStart(BigDecimal sequenceStart)
	{
		this.info.setSequenceStart(sequenceStart);
	}

	/**
	 * @return the bmGuid
	 */
	public String getBmGuid()
	{
		return this.info.getBmGuid();
	}

	/**
	 * @return the createtime
	 */
	public Date getCreatetime()
	{
		return this.info.getCreateTime();
	}

	/**
	 * @return the createuserGuid
	 */
	public String getCreateuserGuid()
	{
		return this.info.getCreateuserGuid();
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
	public String getEnd1BoTitle(LanguageEnum lang)
	{
		return this.info.getEnd1BoTitle(lang);
	}

	/**
	 * @return the guid
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
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.info.getName();
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
	 * @return the updateTime
	 */
	@Override
	public Date getUpdateTime()
	{
		return this.info.getUpdateTime();
	}

	/**
	 * @return the updateUserGuid
	 */
	@Override
	public String getUpdateUserGuid()
	{
		return this.info.getUpdateUserGuid();
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

	public boolean isIncorporatedMaster()
	{
		return this.info.isIncorporatedMaster();
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
	 * @param end1BoName
	 *            the end1BoName to set
	 */
	public void setEnd1BoName(String end1BoName)
	{
		this.info.setEnd1BoName(end1BoName);
	}

	public void setEnd1BoTitle(String title)
	{
		this.info.setEnd1BoTitle(title);
	}

	/**
	 * @param GUID
	 *            the GUID to set
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
	 * @param NAME
	 *            the NAME to set
	 */
	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	public void setIsIncorporatedMaster(Boolean isIncorporatedMaster)
	{
		this.info.setIsIncorporatedMaster(isIncorporatedMaster);
	}

	/**
	 * @param structureClassGuid
	 *            the structureClassGuid to set
	 */
	public void setStructureClassGuid(String structureClassGuid)
	{
		this.info.setStructureClassGuid(structureClassGuid);
	}

	/**
	 * @param structureClassGuid
	 *            the structureClassGuid to set
	 */
	public void setStructureClassName(String structureClassName)
	{
		this.info.setStructureClassName(structureClassName);
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

	public void setPrecise(BomPreciseType type)
	{
		this.info.setPrecise(type);
	}

	public BomPreciseType getPrecise()
	{
		return this.info.getPrecise();
	}

	public void setValid(boolean isValid)
	{
		this.info.setValid(isValid);
	}
	
	public String getDesignatorsSeparatorChar()
	{
		return this.info.getDesignatorsSeparatorChar();
	}
	
	public void setDesignatorsSeparator(String separatorChar)
	{
		this.info.setDesignatorsSeparator(separatorChar);
	}
	
	public boolean isMoveForWard()
	{
		return this.info.isMoveForWard();
	}

	public boolean needCheckDesignator()
	{
		return this.info.needCheckDesignator();
	}

	public void setMoveForward(boolean moveForward)
	{
		this.info.setMoveForward(moveForward);
	}

	public void setNeedCheckDesignator(boolean needCheckDesignator)
	{
		this.info.setNeedCheckDesignator(needCheckDesignator);
	}

	@Override
	public boolean isValid()
	{
		return this.info.isValid();
	}

	public List<BOMTemplateEnd2> getBOMTemplateEnd2List()
	{
		return bomTemplateEnd2List;
	}

	public void setBOMTemplateEnd2List(List<BOMTemplateEnd2> bomTemplateEnd2List)
	{
		this.bomTemplateEnd2List = bomTemplateEnd2List;
	}

	public List<BOMReportTemplate> getBOMReportTemplateList()
	{
		return bomReportTemplateList;
	}

	public void setBOMReportTemplateList(List<BOMReportTemplate> bomReportTemplateList)
	{
		this.bomReportTemplateList = bomReportTemplateList;
	}
}
