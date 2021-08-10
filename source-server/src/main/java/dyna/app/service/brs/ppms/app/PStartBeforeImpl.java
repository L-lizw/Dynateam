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
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.WarningEventEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 *         项目启动前通知:项目计划开始时间的前N天报警
 */
public class PStartBeforeImpl extends AbstractEventApp
{
	/**
	 * @param eventEnum
	 */
	public PStartBeforeImpl()
	{
		super(WarningEventEnum.P_START_BEFORE);
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
		boolean isSend = false;
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(projectFoundationObject);
		if (util.getPlanStartTime() == null)
		{
			return isSend;
		}
		if (util.getProjectStatusEnum() == ProjectStatusEnum.INI)
		{
			// 0=<差距时间=<间隔天数
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			String compareDate = DateFormat.getDifferenceDay(util.getPlanStartTime(), c.getTime()) + "";
			if (!StringUtils.isNullString(rule.getInterval()) && !StringUtils.isNullString(compareDate))
			{
				Double gapTime = Double.valueOf(compareDate);
				Double confTime = Double.valueOf(rule.getInterval());
				if (gapTime >= 0 && gapTime <= confTime)
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
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(projectFoundationObject);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return DateFormat.getDifferenceDay(util.getPlanStartTime(), c.getTime()) + "";
	}
}
