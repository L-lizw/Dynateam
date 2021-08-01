/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UpdateTaskStatus
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.aas.User;
import dyna.common.dtomapper.ppm.UpdateTaskStatusMapper;
import dyna.common.systemenum.ppms.ProgressRateEnum;
import dyna.common.util.PMConstans;

import java.util.List;

/**
 * 更新任务状态
 * 
 * @author wangweixia
 * 
 */
@EntryMapper(UpdateTaskStatusMapper.class)
public class UpdateTaskStatus extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -2451461156189765722L;
	// 任务guid
	public static final String	TASKGUID			= "TASKGUID";

	// 更新内容
	public static final String	UPDATECONTENT		= "UPDATECONTENT";

	// 状态
	public static final String	STATUS				= "STATUS";

	// 进度
	public static final String	PROGRESSRATE		= "PROGRESSRATE";

	// 转发人guidlist; User对象中需要UserGuid,UserName,UserId
	public List<User>			forwardUserList		= null;

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

	public ObjectGuid getTaskObjectGuid()
	{
		return new ObjectGuid((String) this.get(TASKGUID + PMConstans.CLASS), null, (String) this.get(TASKGUID), (String) this.get(TASKGUID + PMConstans.MASTER), null);
	}

	public void setTaskObjectGuid(ObjectGuid taskObjectGuid)
	{
		if (taskObjectGuid == null)
		{
			this.put(TASKGUID + PMConstans.MASTER, null);
			this.put(TASKGUID + PMConstans.CLASS, null);
			this.put(TASKGUID, null);
		}
		else
		{
			this.put(TASKGUID + PMConstans.MASTER, taskObjectGuid.getMasterGuid());
			this.put(TASKGUID + PMConstans.CLASS, taskObjectGuid.getClassGuid());
			this.put(TASKGUID, taskObjectGuid.getGuid());
		}

	}

	/**
	 * @return the updatecontent
	 */
	public String getUpdateContent()
	{
		return (String) super.get(UPDATECONTENT);
	}

	public void setUpdateContent(String updateContent)
	{
		super.put(UPDATECONTENT, updateContent);
	}

	/**
	 * @return the status
	 */
	public ProgressRateEnum getStatus()
	{
		return ProgressRateEnum.valueOf((String) super.get(STATUS));
	}

	public void setStatus(ProgressRateEnum status)
	{
		super.put(STATUS, status.name());
	}

	// public String getStatusGuid()
	// {
	// return (String) super.get(STATUS);
	// }
	//
	// public void setStatusGuid(String statusguid)
	// {
	// super.put(STATUS, statusguid);
	// }
	//
	// public String getStatusName()
	// {
	// return (String) super.get(STATUS);
	// }
	//
	// public void setStatusName(String statusName)
	// {
	// super.put(STATUS, statusName);
	// }

	/**
	 * @return the progressrate
	 */
	public String getProgressRate()
	{
		return (String) super.get(PROGRESSRATE);
	}

	public void setProgressRate(String progressRate)
	{
		super.put(PROGRESSRATE, progressRate);
	}

	/**
	 * @return the forwardUserList
	 */
	public List<User> getForwardUserList()
	{
		return forwardUserList;
	}

	/**
	 * @param forwardUserList
	 *            the forwardUserList to set
	 */
	public void setForwardUserList(List<User> forwardUserList)
	{
		this.forwardUserList = forwardUserList;
	}
}
