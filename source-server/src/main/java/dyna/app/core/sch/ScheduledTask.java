/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduledTask 计划任务
 * Wanglei 2011-4-20
 */
package dyna.app.core.sch;

import java.util.Date;

/**
 * 计划任务
 * 
 * @author Wanglei
 * 
 */
public interface ScheduledTask extends Runnable
{

	public final static int	DEFAULT_PRIORITY	= 0;

	public int getPriority();

	public void setPriority(int priority);

	public Date getCreateTime();

	public void setCreateTime(Date value);

}
