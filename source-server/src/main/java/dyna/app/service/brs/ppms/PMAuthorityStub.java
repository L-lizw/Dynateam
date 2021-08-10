/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PMAuthorityStub
 * WangLHB May 28, 2012
 */
package dyna.app.service.brs.ppms;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.wbs.AuthorityCheck;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.PMAuthorityEnum;

/**
 * @author WangLHB
 * 
 */
public class PMAuthorityStub extends AbstractServiceStub<PPMSImpl>
{
	AuthorityCheck	check	= null;

	protected PMAuthorityStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	protected AuthorityCheck getAuthorityCheck()
	{
		if (this.check == null)
		{

			this.check = new AuthorityCheck() {
				@Override
				protected FoundationObject getProject(ObjectGuid objectGuid) throws ServiceRequestException
				{
					return ((BOASImpl) PMAuthorityStub.this.stubService.getBOAS()).getFoundationStub().getObject(
							objectGuid, false);
				}

				@Override
				protected List<ProjectRole> listProjectRoleByUser(ObjectGuid projectObjectGuid, String operatorGuid)
						throws ServiceRequestException
				{
					return PMAuthorityStub.this.stubService.getRoleStub().listProjectRoleByUser(
							projectObjectGuid.getGuid(), operatorGuid);
				}

				@Override
				protected List<TaskMember> listTaskMember(ObjectGuid taskObjectGuid) throws ServiceRequestException
				{
					return PMAuthorityStub.this.stubService.getTaskMemberStub().listTaskMember(taskObjectGuid);
				}
			};
		}

		return this.check;
	}

	protected boolean hasPMAuthority(FoundationObject foundationObject, PMAuthorityEnum authorityEnum)
			throws ServiceRequestException
	{
		return this.getAuthorityCheck().hasPMAuthority(foundationObject, authorityEnum,
				this.stubService.getOperatorGuid());
	}

}
