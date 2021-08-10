/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CompleteDelayImpl
 * WangLHB Jun 27, 2012
 */
package dyna.app.service.brs.ppms.app;

import dyna.app.service.brs.ppms.PPMSImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.systemenum.ppms.WarningEventEnum;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 *         任务进度风险
 */
public class TprogressRiskImpl extends AbstractEventApp
{
	/**
	 * @param eventEnum
	 */
	public TprogressRiskImpl()
	{
		super(WarningEventEnum.T_PROGRESS_RISK);
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
			Double value = util.getSPI();
			if (!StringUtils.isNullString(rule.getInterval()) && value != null)
			{
				// 当任务SPI小于设定值时，发起预警
				Double threshold = Double.valueOf(rule.getInterval());
				// 项目的spi小于设定的值
				if (value < threshold)
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
		return util.getSPI() + "";
	}
}
