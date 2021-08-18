/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoundationACLStub
 * WangLHB Feb 21, 2012
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.BusinessModelTypeEnum;
import dyna.data.DataServer;
import dyna.data.service.acl.AclService;
import org.springframework.stereotype.Component;

/**
 * @author WangLHB
 * 
 */
@Component
public class FoundationACLStub extends AbstractServiceStub<ACLImpl>
{

	protected boolean hasFoundationCreateACL(String boName) throws ServiceRequestException
	{
		boolean supervisor = Constants.isSupervisor(true, this.stubService);
		if (!supervisor)
		{
			return true;
		}

		BOInfo boInfo = this.stubService.getEMM().getCurrentBoInfoByName(boName, true);
		if (boInfo == null)
		{
			return false;
		}

		if (boInfo.getType() == BusinessModelTypeEnum.PACKAGE)
		{
			return false;
		}

		AclService aclService = DataServer.getAclService();
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			return aclService.hasCreateAuthorityForClass(boInfo.getClassGuid(), sessionId);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected boolean hasFileDownloadACL(ObjectGuid objectGuid) throws ServiceRequestException
	{
		boolean supervisor = Constants.isSupervisor(true, this.stubService);
		if (!supervisor)
		{
			return true;
		}

		try
		{
			AclService aclService = DataServer.getAclService();
			String sessionId = this.stubService.getSignature().getCredential();
			return aclService.hasAuthority(objectGuid, AuthorityEnum.DOWNLOADFILE, sessionId);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
