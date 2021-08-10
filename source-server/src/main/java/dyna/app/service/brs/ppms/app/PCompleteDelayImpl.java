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
 * 
 *         项目超期 :到项目计划完成时间时，该项目还未结束(进行中的项目)
 */
public class PCompleteDelayImpl extends AbstractEventApp
{
	/**
	 * @param eventEnum
	 */
	public PCompleteDelayImpl()
	{
		super(WarningEventEnum.P_COMPLETE_DELAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.ppms.app.AbstractEventApp#isSendNotice(dyna.app.service.brs.ppms.PPMSImpl,
	 * dyna.common.bean.data.FoundationObject, dyna.common.bean.data.FoundationObject,
	 * dyna.common.bean.data.ppms.EarlyWarning)
	 */
	@Override
	protected boolean isSendNotice(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
			throws ServiceRequestException
	{
		boolean isSend = false;
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(projectFoundationObject);
		if (util.getPlanFinishTime() == null)
		{
			return isSend;
		}
		if (util.getProjectStatusEnum() == ProjectStatusEnum.RUN)
		{
			// 差距时间>=间隔天数
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			
			String value = DateFormat.getDifferenceDay(c.getTime(), util.getPlanFinishTime()) + "";
			if (!StringUtils.isNullString(rule.getInterval()) && !StringUtils.isNullString(value))
			{
				Double gapTime = Double.valueOf(value);
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
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(projectFoundationObject);
		return DateFormat.getDifferenceDay(c.getTime(), util.getPlanStartTime()) + "";
	}
}
