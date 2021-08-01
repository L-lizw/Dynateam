/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.TaskMemberMapper;
import dyna.common.systemenum.ppms.WBSOperateEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.PMConstans;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;

/**
 * @author WangLHB
 * 
 */
@EntryMapper(TaskMemberMapper.class)
public class TaskMember extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4140094780574579385L;

	/**
	 * 用户GUID
	 */
	public static final String	USERGUID			= "USERGUID";

	/**
	 * 名称（用户名）
	 */
	public static final String	USERNAME			= "USERNAME";

	/**
	 * 项目角色(GUID)。
	 */
	public static final String	PROJECT_ROLE		= "PROJECTROLE";

	/**
	 * 项目角色(名称)。
	 */
	public static final String	PROJECT_ROLE_NAME	= "PROJECTROLENAME";

	/**
	 * 占比。
	 */
	public static final String	RESOURCERATE		= "RESOURCERATE";

	/**
	 * 责任人。
	 */
	public static final String	ISEXECUTOR			= "ISEXECUTOR";

	/**
	 * 顺序SEQUENCE
	 */
	public static final String	SEQUENCE			= "DATASEQ";
	/**
	 * 所属任务。
	 */
	public static final String	TASK_GUID			= "TASKGUID";

	private WBSOperateEnum		operate				= null;

	/**
	 * @return the userGuid
	 */
	public String getUserGuid()
	{
		return (String) super.get(USERGUID);
	}

	/**
	 * @param userGuid
	 *            the userGuid to set
	 */
	public void setUserGuid(String userGuid)
	{
		super.put(USERGUID, userGuid);
	}

	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return (String) super.get(USERNAME);
	}

	public void setUserName(String userName)
	{
		super.put(USERNAME, userName);
	}

	/**
	 * @return the projectRole
	 */
	public String getProjectRole()
	{
		return (String) super.get(PROJECT_ROLE);
	}

	/**
	 * @param projectRole
	 *            the projectRole to set
	 */
	public void setProjectRole(String projectRole)
	{
		super.put(PROJECT_ROLE, projectRole);
	}

	/**
	 * @return the projectRoleName
	 */
	public String getProjectRoleName()
	{
		return (String) super.get(PROJECT_ROLE_NAME);
	}

	public void setProjectRoleName(String projectRoleName)
	{
		super.put(PROJECT_ROLE_NAME, projectRoleName);
	}

	/**
	 * @return the rate
	 */
	public Double getRate()
	{
		Number rate = (Number) super.get(RESOURCERATE);
		if (rate == null)
		{
			return null;
		}
		return rate.doubleValue();
	}

	/**
	 * @param rate
	 *            the rate to set
	 */
	public void setRate(Double rate)
	{
		if (rate == null)
		{
			return;
		}
		super.put(RESOURCERATE, BigDecimal.valueOf(rate));
	}

	/**
	 * @return the personInCharge
	 */
	public Boolean isPersonInCharge()
	{
		if (StringUtils.isNullString((String) super.get(ISEXECUTOR)))
		{
			return false;
		}
		return BooleanUtils.getBooleanBy10((String) super.get(ISEXECUTOR));
	}

	/**
	 * @param personInCharge
	 *            the personInCharge to set
	 */
	public void setPersonInCharge(Boolean personInCharge)
	{
		super.put(ISEXECUTOR, BooleanUtils.getBooleanString10(personInCharge));
	}

	/**
	 * @return the ownerProject
	 */
	public ObjectGuid getTaskObjectGuid()
	{
		return new ObjectGuid((String) this.get(TASK_GUID + PMConstans.CLASS), null, (String) this.get(TASK_GUID), (String) this.get(TASK_GUID + PMConstans.MASTER), null);

	}

	/**
	 * @param taskObjectGuid
	 *            the ownerProject to set
	 */
	public void setTaskObjectGuid(ObjectGuid taskObjectGuid)
	{
		if (taskObjectGuid == null)
		{
			this.put(TASK_GUID + PMConstans.MASTER, null);
			this.put(TASK_GUID + PMConstans.CLASS, null);
			this.put(TASK_GUID, null);
		}
		else
		{
			this.put(TASK_GUID + PMConstans.MASTER, taskObjectGuid.getMasterGuid());
			this.put(TASK_GUID + PMConstans.CLASS, taskObjectGuid.getClassGuid());
			this.put(TASK_GUID, taskObjectGuid.getGuid());
		}

	}

	public int getSequence()
	{
		Number b = (Number) super.get(SEQUENCE);
		return b == null ? 0 : b.intValue();
	}

	/**
	 * @param value
	 */
	public void setSequence(int value)
	{
		super.put(SEQUENCE, BigDecimal.valueOf(value));
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
