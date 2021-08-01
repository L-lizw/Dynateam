/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BIViewHis
 * Caogc 2011-01-11
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.BIViewHisMapper;

/**
 *BIViewHis 记录查询的历史对象的实体bean
 * 
 */
@EntryMapper(BIViewHisMapper.class)
public class BIViewHis extends SystemObjectImpl implements SystemObject
{
	private static final long serialVersionUID = -8041854755543561578L;

	public static final String	INSTANCE_GUID		= "INSTANCEGUID";
	public static final String	INSTANCE_CLASSGUID  = "INSTANCECLASSGUID";
	public static final String	INSTANCE_BOGUID     = "INSTANCEBOGUID";
	public static final String	CREATE_USER			= "CREATEUSER";

	// 系统默认的"最近打开"的最大的条数
	public static final int		MAXROWNUM			= 30;
	
	/**
	 * @return the instanceGuid
	 */
	public String getInstanceGuid()
	{
		return (String) this.get(INSTANCE_GUID);
	}

	public void setInstanceGuid(String instanceGuid)
	{
		this.put(INSTANCE_GUID, instanceGuid);
	}
	
	/**
	 * @return the instanceClassGuid
	 */
	public String getInstanceClassGuid()
	{
		return (String) this.get(INSTANCE_CLASSGUID);
	}

	public void setInstanceClassGuid(String instanceClassGuid)
	{
		this.put(INSTANCE_CLASSGUID, instanceClassGuid);
	}
	
	/**
	 * @return the instanceBOGuid
	 */
	public String getInstanceBOGuid()
	{
		return (String) this.get(INSTANCE_BOGUID);
	}

	public void setInstanceBOGuid(String instanceBOGuid)
	{
		this.put(INSTANCE_BOGUID, instanceBOGuid);
	}
}
