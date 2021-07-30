/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMEditBean
 * WangLHB 2010-10-29
 */
package dyna.common.bean.serv;

import java.io.Serializable;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.structure.BOMStructure;

/**
 * @author WangLHB
 *
 */
public class BOMEditBean implements Serializable
{
	private static final long	serialVersionUID			= -6963958424062286551L;

	// end1
	private ObjectGuid		end1ObjectGuid	= null;

	// end2
	private ObjectGuid		end2ObjectGuid	= null;

	// 剪切或替换的BOMStructure ObjecgGuid
	private ObjectGuid		originalStructureObjectGuid	= null;

	private String			classification	= null;

	private boolean			isPrecise		= false;

	// 新建，Add，粘贴后的BOM结构
	private BOMStructure	bomStructure	= null;

	/**
	 * @param end1ObjectGuid
	 *            the end1ObjectGuid to set
	 */
	public void setEnd1ObjectGuid(ObjectGuid end1ObjectGuid)
	{
		this.end1ObjectGuid = end1ObjectGuid;
	}

	/**
	 * @param end2ObjectGuid
	 *            the end2ObjectGuid to set
	 */
	public void setEnd2ObjectGuid(ObjectGuid end2ObjectGuid)
	{
		this.end2ObjectGuid = end2ObjectGuid;
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassification(String classification)
	{
		this.classification = classification;
	}

	/**
	 * @param isPrecise
	 *            the isPrecise to set
	 */
	public void setPrecise(boolean isPrecise)
	{
		this.isPrecise = isPrecise;
	}

	/**
	 * @param bomStructure
	 *            the bomStructure to set
	 */
	public void setBomStructure(BOMStructure bomStructure)
	{
		this.bomStructure = bomStructure;
	}

	/**
	 * @return the end1ObjectGuid
	 */
	public ObjectGuid getEnd1ObjectGuid()
	{
		return this.end1ObjectGuid;
	}

	/**
	 * @return the end2ObjectGuid
	 */
	public ObjectGuid getEnd2ObjectGuid()
	{
		return this.end2ObjectGuid;
	}

	/**
	 * @return the classification
	 */
	public String getClassification()
	{
		return this.classification;
	}

	/**
	 * @return the isPrecise
	 */
	public boolean isPrecise()
	{
		return this.isPrecise;
	}

	/**
	 * @return the bomStructure
	 */
	public BOMStructure getBomStructure()
	{
		return this.bomStructure;
	}

	/**
	 * @param structureCutObjectGuid
	 *            the structureCutObjectGuid to set
	 */
	public void setOriginalStructureObjectGuid(ObjectGuid structureCutObjectGuid)
	{
		this.originalStructureObjectGuid = structureCutObjectGuid;
	}

	/**
	 * @return the structureCutObjectGuid
	 */
	public ObjectGuid getOriginalStructureObjectGuid()
	{
		return this.originalStructureObjectGuid;
	}
}
