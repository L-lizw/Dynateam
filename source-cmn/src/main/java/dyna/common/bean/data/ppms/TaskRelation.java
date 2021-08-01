/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskRelation
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.TaskRelationMapper;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;

/**
 * 任务与任务的前后置关系
 * 
 * @author wangweixia
 * 
 */
@EntryMapper(TaskRelationMapper.class)
public class TaskRelation extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -4099410338639609379L;
	/**
	 * 前置任务和任务字段的赋值：
	 * 当前任务添加前置任务时，PRETASKGUID为前置任务guid,TASKGUID为当前任务guid
	 * 当前任务添加后置任务时，PRETASKGUID为当前任务guid,TASKGUID为后置任务guid
	 */
	// 前置任务guid
	public static final String	PRETASKGUID			= "PRETASKGUID";
	// 任务guid
	public static final String	TASKGUID			= "TASKGUID";

	// 前置任务类型
	public static final String	PRETASKTYPE			= "PRETASKTYPE";

	// 延隔时间
	public static final String	DELAYTIME			= "DELAYTIME";

	// 项目GUID
	public static final String	PROJECT				= "PROJECTGUID";

	private ObjectGuid			taskObjectGuid		= null;

	private ObjectGuid			preTaskObjectGuid	= null;

	private ObjectGuid			projectObjectGuid	= null;

	// 序号
	public static final String	SEQUENCE			= "DATASEQ";

	/**
	 * @return
	 */
	public ObjectGuid getTaskObjectGuid()
	{
		if (this.taskObjectGuid == null)
		{
			String fieldName = TASKGUID;
			this.taskObjectGuid = new ObjectGuid((String) this.get(fieldName + "$CLASS"), // classguid
					(String) this.get(fieldName + "$CLASSNAME"), // classname
					(String) this.get(fieldName), // guid
					(String) this.get(fieldName + "$MASTER"), // master
					null);

		}
		return this.taskObjectGuid;
	}

	/**
	 * @return
	 */
	public ObjectGuid getPreTaskObjectGuid()
	{
		if (this.preTaskObjectGuid == null)
		{
			String fieldName = PRETASKGUID;
			this.preTaskObjectGuid = new ObjectGuid((String) this.get(fieldName + "$CLASS"), // classguid
					(String) this.get(fieldName + "$CLASSNAME"), // classname
					(String) this.get(fieldName), // guid
					(String) this.get(fieldName + "$MASTER"), // masterguid
					null);
		}

		return this.preTaskObjectGuid;
	}

	/**
	 * @return
	 */
	public ObjectGuid getProjectObjectGuid()
	{
		if (this.projectObjectGuid == null)
		{
			String fieldName = PROJECT;
			this.projectObjectGuid = new ObjectGuid((String) this.get(fieldName + "$CLASS"), // classguid
					(String) this.get(fieldName + "$CLASSNAME"), // classname
					(String) this.get(fieldName), // guid
					(String) this.get(fieldName + "$MASTERFK"), // masterguid
					null);
		}

		return this.projectObjectGuid;
	}

	public void setProjectObjectGuid(ObjectGuid objectGuid)
	{
		this.projectObjectGuid = objectGuid;
		String fieldName = PROJECT;
		if (this.projectObjectGuid == null)
		{
			this.remove(fieldName);
			this.remove(fieldName + "$CLASSNAME");
			this.remove(fieldName + "$CLASS");
			this.remove(fieldName + "$MASTER");
		}
		else
		{
			this.put(fieldName, this.projectObjectGuid.getGuid());
			this.put(fieldName + "$CLASS", this.projectObjectGuid.getClassGuid());
			this.put(fieldName + "$CLASSNAME", this.projectObjectGuid.getClassName());
			this.put(fieldName + "$MASTER", this.projectObjectGuid.getMasterGuid());
		}
	}

	public void settaskObjectGuid(ObjectGuid objectGuid)
	{
		this.taskObjectGuid = objectGuid;
		String fieldName = TASKGUID;
		if (this.taskObjectGuid == null)
		{
			this.remove(fieldName);
			this.remove(fieldName + "$CLASSNAME");
			this.remove(fieldName + "$CLASS");
			this.remove(fieldName + "$MASTER");
		}
		else
		{
			this.put(fieldName, this.taskObjectGuid.getGuid());
			this.put(fieldName + "$CLASS", this.taskObjectGuid.getClassGuid());
			this.put(fieldName + "$CLASSNAME", this.taskObjectGuid.getClassName());
			this.put(fieldName + "$MASTER", this.taskObjectGuid.getMasterGuid());
		}
	}

	public void setpreTaskObjectGuid(ObjectGuid objectGuid)
	{
		this.preTaskObjectGuid = objectGuid;
		String fieldName = PRETASKGUID;
		if (this.preTaskObjectGuid == null)
		{
			this.remove(fieldName);
			this.remove(fieldName + "$CLASSNAME");
			this.remove(fieldName + "$CLASS");
			this.remove(fieldName + "$MASTER");
		}
		else
		{
			this.put(fieldName, this.preTaskObjectGuid.getGuid());
			this.put(fieldName + "$CLASS", this.preTaskObjectGuid.getClassGuid());
			this.put(fieldName + "$CLASSNAME", this.preTaskObjectGuid.getClassName());
			this.put(fieldName + "$MASTER", this.preTaskObjectGuid.getMasterGuid());
		}
	}

	/**
	 * @return the pretasktypeEnum
	 */
	public TaskDependEnum getDependTypeEnum()
	{
		if (!StringUtils.isNullString((String) this.get(PRETASKTYPE)))
		{
			return TaskDependEnum.getEnum((String) this.get(PRETASKTYPE));
		}
		else
		{
			return null;
		}
	}

	/**
	 * 前置任务类型guid
	 * 
	 * @return
	 */
	public String getDependType()
	{
		return (String) super.get(PRETASKTYPE);
	}

	/**
	 * 设置前置任务类型
	 * 
	 * @param pretaskType
	 */
	public void setDependType(String pretaskType)
	{
		super.put(PRETASKTYPE, pretaskType);
	}

	/**
	 * @return the delaytime
	 */
	public int getDelayTime()
	{
		Object object = super.get(DELAYTIME);
		if (object == null)
		{
			return 0;
		}
		else
		{
			return ((Number) object).intValue();
		}
	}

	public void setDelayTime(int delayTime)
	{

		super.put(DELAYTIME, BigDecimal.valueOf(delayTime));

	}

	/**
	 * @return
	 */
	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	/**
	 * @param value
	 */
	public void setSequence(int value)
	{
		this.put(SEQUENCE, value);
	}

	@Override
	public void sync(DynaObject object)
	{
		projectObjectGuid = null;
		taskObjectGuid = null;
		preTaskObjectGuid = null;
		super.sync(object);
	}

	@Override
	public void syncValue(DynaObject object)
	{
		projectObjectGuid = null;
		taskObjectGuid = null;
		preTaskObjectGuid = null;
		super.syncValue(object);
	}

}
