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
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.WarningEventEnum;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 *         项目进度风险预警:当项目SPI小于设定值时，发起预警
 */
public class PprogressRiskImpl extends AbstractEventApp
{
	/**
	 * @param eventEnum
	 */
	public PprogressRiskImpl()
	{
		super(WarningEventEnum.P_PROGRESS_RISK);
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
			Double value = util.getSPI();
			if (!StringUtils.isNullString(rule.getInterval()) && value != null)
			{
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
	 * @see dyna.app.service.brs.ppms.app.AbstractEventApp#getNoticeSubject(dyna.app.service.brs.ppms.PPMSImpl,
	 * dyna.common.bean.data.FoundationObject, dyna.common.bean.data.FoundationObject,
	 * dyna.common.bean.data.ppms.EarlyWarning)
	 */
	@Override
	public Object getDelayPercent(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(projectFoundationObject);
		return util.getSPI() + "";
	}
}
