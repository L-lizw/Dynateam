/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DeliverableItem
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.DeliverableItemMapper;
import dyna.common.systemenum.ppms.WBSOperateEnum;
import dyna.common.util.BooleanUtils;

/**
 * 交付项
 * 
 * @author wangweixia
 * 
 */
@EntryMapper(DeliverableItemMapper.class)
public class DeliverableItem extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -2580148025716054936L;

	// 项目guid
	public static final String	PROJECTGUID			= "PROJECTGUID";

	// 任务guid
	public static final String	TASKGUID			= "TASKGUID";

	// 类型：实例类型
	public static final String	CLASSGUID			= "CLASSGUID";

	// 分类
	public static final String	CLASSIFICATION		= "CLASSIFICATION";

	// 必须
	public static final String	ISNEED				= "ISNEED";

	// 发布
	public static final String	ISRELEASE			= "ISRELEASE";

	// 实例类型BO Title
	public static final String	BOTILE				= "BOTILE";

	// 任务实例名称
	public static final String	TASKNAME			= "TASKNAME";

	// 分类Title
	public static final String	CLSFITILE			= "CLSFITILE";

	// 备注
	public static final String	REMARK				= "REMARK";

	private WBSOperateEnum		operate				= null;

	/**
	 * @return the taskguid
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
	 * @return the taskguid
	 */
	public String getTaskGuid()
	{
		return (String) super.get(TASKGUID);
	}

	public void setTaskGuid(String taskGuid)
	{
		super.put(TASKGUID, taskGuid);
	}

	/**
	 * @return the classguid
	 */
	public String getClassGuid()
	{
		return (String) super.get(CLASSGUID);
	}

	public void setClassGuid(String classGuid)
	{
		super.put(CLASSGUID, classGuid);
	}

	/**
	 * @return the classification
	 */
	public String getClassification()
	{
		return (String) super.get(CLASSIFICATION);
	}

	public void setClassification(String classification)
	{
		super.put(CLASSIFICATION, classification);
	}

	/**
	 * @return the isneed
	 */
	public boolean isNeed()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISNEED));
		return ret == null ? false : ret.booleanValue();
	}

	public void setNeed(boolean need)
	{
		this.put(ISNEED, BooleanUtils.getBooleanStringYN(need));
	}

	/**
	 * @return the isrelease
	 */
	public boolean isRelease()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISRELEASE));
		return ret == null ? false : ret.booleanValue();
	}

	public void setRelease(boolean release)
	{
		this.put(ISRELEASE, BooleanUtils.getBooleanStringYN(release));
	}

	public String getTaskName()
	{
		return (String) this.get(TASKNAME);
	}

	public String getBoTitle()
	{
		return (String) this.get(BOTILE);
	}

	public String getClsfiTitle()
	{
		return (String) this.get(CLSFITILE);
	}

	public void setTaskName(String value)
	{
		this.put(TASKNAME, value);
	}

	public void setBoTitle(String value)
	{
		this.put(BOTILE, value);
	}

	public void setClsfiTitle(String value)
	{
		this.put(CLSFITILE, value);
	}
	
	public void setRemark(String remark)
	{
		this.put(REMARK, remark);
	}

	public String getRemark()
	{
		return (String) this.get(REMARK);
	}

	public WBSOperateEnum getOperate()
	{
		return this.operate;
	}

	public void setOperate(WBSOperateEnum operate)
	{
		this.operate = operate;
	}
}
