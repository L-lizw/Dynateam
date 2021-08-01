/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CheckpointConfig
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.CheckpointConfigMapper;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 里程碑的关卡列表设置
 * 
 * @author wangweixia
 * 
 */
@EntryMapper(CheckpointConfigMapper.class)
public class CheckpointConfig extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 3290009885922229857L;
	// 名称
	public static final String	NAME				= "CHECKPOINTNAME";

	// 编号
	// public static final String CHECKPOINTID = "CHECKPOINTID";

	// 说明
	public static final String	DESCRIPTION			= "DESCRIPTION";

	// 颜色
	public static final String	COLOR				= "COLOR";

	// 项目类型的guid或者项目guid,项目模板guid,
	public static final String	TYPEGUID			= "TYPEGUID";

	// type=1:项目类型guid; type=2:除项目类型以外的
	public static final String	TYPE				= "OWNERTYPE";

	// 顺序
	public static final String	SEQUENCE			= "DATASEQ";

	// 关联wbs任务
	public static final String	RELATEDTASKOBJECT	= "RELATEDTASKOBJECT";

	// ---新增字段(用于记录完成率、里程碑阶段的计划开时间和计划结束时间)
	// 计划开始时间
	public static final String	ACTUALFINISHTIME	= "ACTUALFINISHTIME";

	// 计划结束时间
	public static final String	PLANFINISHTIME		= "PLANFINISHTIME";

	// 完成率
	public static final String	COMPLETIONRATE		= "COMPLETIONRATE";

	// /**
	// * @return the description
	// */
	// public String getCheckpointId()
	// {
	// return (String) super.get(CHECKPOINTID);
	// }
	//
	// public void setCheckpointId(String checkpointId)
	// {
	// super.put(CHECKPOINTID, checkpointId);
	// }

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		super.put(DESCRIPTION, description);
	}

	/**
	 * @return the color
	 */
	public String getColor()
	{
		return (String) super.get(COLOR);
	}

	public void setColor(String color)
	{
		super.put(COLOR, color);
	}

	/**
	 * @return the typeguid
	 */
	public String getTypeGuid()
	{
		return (String) super.get(TYPEGUID);
	}

	public void setTypeGuid(String typeGuid)
	{
		super.put(TYPEGUID, typeGuid);
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return (String) super.get(TYPE);
	}

	public void setType(String type)
	{
		super.put(TYPE, type);
	}

	/**
	 * @return the type
	 */
	public Integer getSequence()
	{
		return super.get(SEQUENCE) == null ? 0 : ((Number) super.get(SEQUENCE)).intValue();
	}

	public void setSequence(String equence)
	{
		super.put(SEQUENCE, new BigDecimal(equence));
	}

	/**
	 * 
	 * @return
	 */
	public ObjectGuid getRelatedTaskObject()
	{
		return new ObjectGuid((String) this.get(RELATEDTASKOBJECT + PPMConstans.CLASS), null, (String) this.get(RELATEDTASKOBJECT),
				(String) this.get(RELATEDTASKOBJECT + PPMConstans.MASTER), null);

	}

	/**
	 * 
	 * @param relatedTask
	 */
	public void setRelatedTaskObject(ObjectGuid relatedTask)
	{
		if (relatedTask == null)
		{
			this.put(RELATEDTASKOBJECT + PPMConstans.MASTER, null);
			this.put(RELATEDTASKOBJECT + PPMConstans.CLASS, null);
			this.put(RELATEDTASKOBJECT, null);
		}
		else
		{
			this.put(RELATEDTASKOBJECT + PPMConstans.MASTER, relatedTask.getMasterGuid());
			this.put(RELATEDTASKOBJECT + PPMConstans.CLASS, relatedTask.getClassGuid());
			this.put(RELATEDTASKOBJECT, relatedTask.getGuid());
		}
	}

	/**
	 * @return the planStartTime
	 */
	public Date getActualFinishTime()
	{
		return (Date) super.get(ACTUALFINISHTIME);
	}

	/**
	 * @param planStartTime
	 *            the planStartTime to set
	 */
	public void setActualFinishTime(Date planStartTime)
	{
		super.put(ACTUALFINISHTIME, planStartTime);
	}

	/**
	 * @return the planFinishTime
	 */
	public Date getPlanFinishTime()
	{
		return (Date) super.get(PLANFINISHTIME);
	}

	/**
	 * @param planFinishTime
	 *            the planFinishTime to set
	 */
	public void setPlanFinishTime(Date planFinishTime)
	{
		super.put(PLANFINISHTIME, planFinishTime);
	}

	/**
	 * @return the completionRate
	 */
	public Double getCompletionRate()
	{
		Number b = (Number) super.get(COMPLETIONRATE);
		return b == null ? 0 : b.doubleValue();

	}

	/**
	 * @param completionRate
	 *            the completionRate to set
	 */
	public void setCompletionRate(Double completionRate)
	{
		super.put(COMPLETIONRATE, completionRate == null ? null : BigDecimal.valueOf(completionRate));
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}

}
