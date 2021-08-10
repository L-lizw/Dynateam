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
 *         项目逾期启动
 *         (当前时间-启动时间)>间隔天数时,发送通知
 */
public class PStartDelayImpl extends AbstractEventApp
{
	/**
	 * @param eventEnum
	 */
	public PStartDelayImpl()
	{
		super(WarningEventEnum.P_START_DELAY);
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
			// 差距时间>=间隔时间
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
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(projectFoundationObject);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return DateFormat.getDifferenceDay(c.getTime(), util.getPlanStartTime()) + "";
	}
}
