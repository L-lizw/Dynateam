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
 * @author WangLHB
 *         任务超期x%
 */
public class TCompleteDelayPercentImpl extends AbstractEventApp
{

	/**
	 * @param eventEnum
	 */
	public TCompleteDelayPercentImpl()
	{
		super(WarningEventEnum.T_COMPLETE_DELAY_PERCENT);
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
		if (util.getPlanFinishTime() == null)
		{
			return isSend;
		}

		if (taskFoundationObject.getStatus() != SystemStatusEnum.RELEASE)
		{
			return isSend;
		}
		if (util.getTaskStatusEnum() == TaskStatusEnum.RUN)
		{
			// 超期率>设置率都发
			Double threshold = 0.0;
			if (!StringUtils.isNullString(rule.getInterval()))
			{
				threshold = Double.valueOf(rule.getInterval());
			}
			Double b = new Double(this.getDelayPercent(ppm, projectFoundationObject, taskFoundationObject, rule) + "");
			if (b >= threshold)
			{
				isSend = true;
			}
			else
			{
				isSend = false;
			}
		}
		return isSend;
	}

	@Override
	public Object getDelayPercent(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
	{

		if (projectFoundationObject == null)
		{
			return 0D;
		}
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(taskFoundationObject);
		if (util.getPlanFinishTime() == null || util.getPlanStartTime() == null)
		{
			return 0D;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		long compareDate = DateFormat.getDifferenceDay(c.getTime(), util.getPlanFinishTime());

		long compareAllDate = DateFormat.getDifferenceDay(util.getPlanFinishTime(), util.getPlanStartTime()) + 1;
		Double b = Double.valueOf(compareDate) / Double.valueOf(compareAllDate) * 100;
		return b.intValue();
	}
}
