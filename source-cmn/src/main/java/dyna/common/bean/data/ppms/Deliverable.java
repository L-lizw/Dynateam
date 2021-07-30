/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Deliverable
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import java.math.BigDecimal;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;

/**
 * 交付物
 * 
 * @author wangweixia
 * 
 */
public class Deliverable extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 6417447519527643698L;
	// 交付项GUID
	public static final String	DELIVERABLEITEMGUID	= "DELIVERABLEITEMGUID";

	// 项目GUID
	public static final String	PROJECTGUID			= "PROJECTGUID";

	// 序号
	public static final String	SEQUENCE			= "DATASEQ";

	// 交付物实例guid
	public static final String	INSTANCEGUID		= "INSTANCEGUID";

	public static final String	INSTANCECLASSGUID	= "INSTANCECLASSGUID";

	/**
	 * @return the deliverableitemguid
	 */
	public String getDeliverableItemGuid()
	{
		return (String) super.get(DELIVERABLEITEMGUID);
	}

	public void setDeliverableItemGuid(String deliverableItemGuid)
	{
		super.put(DELIVERABLEITEMGUID, deliverableItemGuid);
	}

	/**
	 * @return the projectguid
	 */
	public String getProjectGuid()
	{
		return (String) super.get(PROJECTGUID);
	}

	public void setProjectGuid(String projectGuid)
	{
		super.put(PROJECTGUID, projectGuid);
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence()
	{
		Number b = (Number) this.get(SEQUENCE);
		return b == null ? 0 : b.intValue();
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(int sequence)
	{
		super.put(SEQUENCE, new BigDecimal(sequence));
	}

	/**
	 * @return the instanceguid
	 */
	public String getInstanceGuid()
	{
		return (String) super.get(INSTANCEGUID);
	}

	public void setInstanceGuid(String instanceGuid)
	{
		super.put(INSTANCEGUID, instanceGuid);
	}

	/**
	 * @return the instanceClassGuid
	 */
	public String getInstanceClassGuid()
	{
		return (String) super.get(INSTANCECLASSGUID);
	}

	public void setInstanceClassGuid(String instanceClassGuid)
	{
		super.put(INSTANCECLASSGUID, instanceClassGuid);
	}

	public SystemStatusEnum getInstanceStatus()
	{
		if (this.get(SystemClassFieldEnum.STATUS.getName()) == null)
		{
			return null;
		}
		return SystemStatusEnum.getStatusEnum((String) this.get(SystemClassFieldEnum.STATUS.getName()));

	}

	public ObjectGuid getInstanceObjectGuid()
	{
		ObjectGuid instanceGuid = new ObjectGuid(//
				(String) this.get("CLASSGUID"), //
				null, //
				(String) this.get("INSTANCEGUID"), //
				null, //
				false, //
				null);

		return instanceGuid;
	}

}
