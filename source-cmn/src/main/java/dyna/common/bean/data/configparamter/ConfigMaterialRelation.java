/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECEffectedBOMRelation 记录影响对象所对应的BOMrelation的变更明细
 * caogc 2010-11-02
 */
package dyna.common.bean.data.configparamter;

import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;


public class ConfigMaterialRelation extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 3279514362902686911L;

	public static final String	GUID				= "GUID";
	public StructureObject		CM_STRUCTRUE		= null;
	public static final String	EC_OPERATE			= "ECOPERATE";
	public static final String	CLASSIFICATION		= "CLASSIFICATION";

	public StructureObject getStructrue()
	{
		return this.CM_STRUCTRUE;
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

	public void setStructrue(StructureObject structrue)
	{
		this.CM_STRUCTRUE = structrue;
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


}
