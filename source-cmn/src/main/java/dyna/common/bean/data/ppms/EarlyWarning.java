/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EarlyWarning
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ppms.WBSOperateEnum;
import dyna.common.systemenum.ppms.WarningEventEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;

/**
 * 预警设置
 * 
 * @author wangweixia
 * 
 */
public class EarlyWarning extends SystemObjectImpl implements SystemObject
{
	private static final long		serialVersionUID	= -4263542688620950523L;

	// 编号
	public static final String		ID					= "WARNINGID";

	// 名称
	// public static final String NAME = "NAME";

	// 事件类型
	public static final String		EVENTTYPE			= "EVENTTYPE";

	// 间隔
	public static final String		INTERVAL			= "INTERVALUE";

	// 通知
	public List<WarningNotifier>	noticeMemberList	= null;

	// 适用对象:若选择所有任务，此字段为空值；若选择指定任务，则添加到list中
	public List<ObjectGuid>			applyToTaskList		= null;

	// 任务guid taskguid
	public static final String		TASKGUID			= "TASKGUID";

	// 启用状态
	public static final String		ISENABLE			= "ISENABLE";

	// 项目guid或者模板Guid
	public static final String		PROJECTGUID			= "PROJECTGUID";

	private WBSOperateEnum			operate				= null;

	/**
	 * @return the noticeMemberList
	 */
	public List<WarningNotifier> getNoticeMemberList()
	{
		return this.noticeMemberList;
	}

	/**
	 * @param noticeMemberList
	 *            the noticeMemberList to set
	 */
	public void setNoticeMemberList(List<WarningNotifier> noticeMemberList)
	{
		this.noticeMemberList = noticeMemberList;
	}

	/**
	 * @return the applyToTaskList
	 */
	public List<ObjectGuid> getApplyToTaskList()
	{
		return this.applyToTaskList;
	}

	/**
	 * @param applyToTaskList
	 *            the applyToTaskList to set
	 */
	public void setApplyToTaskList(List<ObjectGuid> applyToTaskList)
	{
		this.applyToTaskList = applyToTaskList;
	}

	public void setApplayToTaskObjectGuid(ObjectGuid objectGuid)
	{
		if (objectGuid == null)
		{
			this.put(TASKGUID + PMConstans.MASTER, null);
			this.put(TASKGUID + PMConstans.CLASS, null);
			this.put(TASKGUID, null);
		}
		else
		{
			this.put(TASKGUID + PMConstans.MASTER, objectGuid.getMasterGuid());
			this.put(TASKGUID + PMConstans.CLASS, objectGuid.getClassGuid());
			this.put(TASKGUID, objectGuid.getGuid());
		}

	}

	public ObjectGuid getApplayToTaskObjectGuid()
	{
		return new ObjectGuid((String) this.get(TASKGUID + PMConstans.CLASS), null, (String) this.get(TASKGUID), (String) this.get(TASKGUID + PMConstans.MASTER), null);
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return (String) super.get(ID);
	}

	public void setId(String id)
	{
		super.put(ID, id);
	}

	/**
	 * @return the eventtype
	 */
	public WarningEventEnum getEventType()
	{
		return WarningEventEnum.valueOf((String) super.get(EVENTTYPE));
	}

	public void setEventType(WarningEventEnum eventType)
	{
		super.put(EVENTTYPE, eventType.name());
	}

	/**
	 * @return the interval
	 */
	public String getInterval()
	{
		return (String) super.get(INTERVAL);
	}

	public void setInterval(String interval)
	{
		super.put(INTERVAL, interval);
	}

	/**
	 * @return the isenable
	 */
	public boolean isEnable()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISENABLE));
		return ret == null ? true : ret.booleanValue();
	}

	public void setEnable(boolean enable)
	{
		super.put(ISENABLE, BooleanUtils.getBooleanStringYN(enable));
	}

	/**
	 * @return the projectguid
	 */
	public String getProjectGuid()
	{
		return (String) super.get(PROJECTGUID);
	}

	/**
	 * 设置模板Guid或者项目Guid
	 * 
	 * @param projectGuid
	 */
	public void setProjectGuid(String projectGuid)
	{
		super.put(PROJECTGUID, projectGuid);
	}

	public WBSOperateEnum getOperate()
	{
		return this.operate;
	}

	public void setOperate(WBSOperateEnum operate)
	{
		this.operate = operate;
	}

	@Override
	public Object clone()
	{
		EarlyWarning clone = (EarlyWarning) super.clone();

		List<WarningNotifier> noticeList = new ArrayList<WarningNotifier>();
		if (!SetUtils.isNullList(this.noticeMemberList))
		{
			for (WarningNotifier warning : this.noticeMemberList)
			{
				noticeList.add((WarningNotifier) warning.clone());
			}
		}
		clone.setNoticeMemberList(noticeList);

		List<ObjectGuid> applyList = new ArrayList<ObjectGuid>();
		if (!SetUtils.isNullList(this.applyToTaskList))
		{
			for (ObjectGuid warning : this.applyToTaskList)
			{
				applyList.add(new ObjectGuid(warning));
			}
		}

		clone.setApplyToTaskList(applyList);

		return clone;
	}
}
