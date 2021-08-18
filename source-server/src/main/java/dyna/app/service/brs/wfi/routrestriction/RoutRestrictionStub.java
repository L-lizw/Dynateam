/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 流程变迁处理分支
 * Wanglei 2010-11-8
 */
package dyna.app.service.brs.wfi.routrestriction;


import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.dto.wf.TransRestriction;
import dyna.common.exception.ServiceRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 流程变迁处理分支
 * 
 * @author Wanglei
 * 
 */
@Component
public class RoutRestrictionStub extends AbstractServiceStub<WFIImpl>
{
	@Autowired
	private RoutRestrictionDBStub dbStub = null;

	public TransRestriction getRoutRestriction(String actRtGuid) throws ServiceRequestException
	{
		return this.dbStub.getRoutRestriction(actRtGuid);
	}
}
