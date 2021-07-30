/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECEffectedBOMRelation 记录影响对象所对应的BOMrelation的变更明细
 * caogc 2010-11-02
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.util.BooleanUtils;

/**
 * @author caogc
 * 
 */
public class ECEffectedBOMRelation extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 3279514362902686911L;

	public static final String	GUID				= "GUID";
	public BOMStructure					BOM_STRUCTRUE		= null;
	public static final String	EC_OPERATE			= "ECOPERATE";
	public static final String	CLASSIFICATION		= "CLASSIFICATION";
	public static final String	IS_PRECISE			= "ISPRECISE";

	public BOMStructure getBOMStructrue()
	{
		return this.BOM_STRUCTRUE;
	}

	/**
	 * @return the classification
	 */
	public String getClassification()
	{
		return (String) this.get(CLASSIFICATION);
	}

	/**
	 * @return the ecOperate
	 */
	public String getEcOperate()
	{
		return (String) this.get(EC_OPERATE);
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the classification
	 */
	public boolean isPrecise()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(IS_PRECISE));
	}

	public void setBOMStructrue(BOMStructure structrue)
	{
		this.BOM_STRUCTRUE = structrue;
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassification(String classification)
	{
		this.put(CLASSIFICATION, classification);
	}

	/**
	 * @param ecOperate
	 *            the ecOperate to set
	 */
	public void setEcOperate(String ecOperate)
	{
		this.put(EC_OPERATE, ecOperate);
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

	/**
	 * @param isPrecise
	 *            the isPrecise to set
	 */
	public void setIsPrecise(boolean isPrecise)
	{
		this.put(IS_PRECISE, BooleanUtils.getBooleanStringYN(isPrecise));
	}

}
