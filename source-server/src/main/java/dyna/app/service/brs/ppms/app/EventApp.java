/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EventApp
 * WangLHB Jun 27, 2012
 */
package dyna.app.service.brs.ppms.app;

import dyna.app.service.brs.ppms.PPMSImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.exception.ServiceRequestException;

/**
 * @author WangLHB
 * 
 */
public interface EventApp
{
	public void execute(PPMSImpl ppm, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule) throws ServiceRequestException;
}
