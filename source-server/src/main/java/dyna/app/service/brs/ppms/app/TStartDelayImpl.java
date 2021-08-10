/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CompleteDelayImpl
 * WangLHB Jun 27, 2012
 */
package dyna.app.service.brs.ppms.app;

import java.util.Calendar;
import java.util.Date;

import dyna.app.service.brs.ppms.PPMSImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.systemenum.ppms.WarningEventEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB(里程碑任务和普通任务)
 *         任务逾期启动
 */
public class TStartDelayImpl extends AbstractEventApp
{
	/**
	 * @param eventEnum
	 * 
	 */
	public TStartDelayImpl()
	{
		super(WarningEventEnum.T_START_DELAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.ppm.app.EventApp#isSendNotice(dyna.app.service.brs.ppm.PPMImpl,
	 * dyna.common.bean.data.FoundationObject, dyna.common.bean.data.FoundationObject,
	 * dyna.common.bean.data.ppm.PMWarningRule)
	 */
	@Override
	public boolean isSendNotice(PPMSImpl ppm, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule) throws ServiceRequestException
	{
		if (taskFoundationObject == null)
		{
			return false;
		}
		boolean isSend = false;
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(taskFoundationObject);

		if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
		{
			return isSend;
		}

		if (util.getPlanStartTime() == null)
		{
			return isSend;
		}
		if (taskFoundationObject.getStatus() != SystemStatusEnum.RELEASE)
		{
			return isSend;
		}

		if (util.getTaskStatusEnum() == TaskStatusEnum.INI)
		{
			// 时间差距>=间隔时间，一直发邮件
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			String compareDate = DateFormat.getDifferenceDay(c.getTime(), util.getPlanStartTime()) + "";
			if (!StringUtils.isNullString(rule.getInterval()) && !StringUtils.isNullString(compareDate))
			{
				Double gapTime = Double.valueOf(compareDate);
				Double confTime = Double.valueOf(rule.getInterval());
				if (gapTime >= confTime)
				{
					isSend = true;
				}
				else
				{
					isSend = false;
				}
			}
		}

		return isSend;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.ppms.app.AbstractEventApp#getDelayPercent(dyna.app.service.brs.ppms.PPMSImpl,
	 * dyna.common.bean.data.FoundationObject, dyna.common.bean.data.FoundationObject,
	 * dyna.common.bean.data.ppms.EarlyWarning)
	 */
	@Override
	public Object getDelayPercent(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(taskFoundationObject);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return DateFormat.getDifferenceDay(c.getTime(), util.getPlanStartTime()) + "";
	}
}
