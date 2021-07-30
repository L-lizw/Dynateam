/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 取消检出
 * JiangHL 2011-5-10
 */
package dyna.data.service.ins;

import dyna.common.bean.data.FoundationObject;
import dyna.common.dto.Session;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;

/**
 * 取消检出
 * 
 * @author JiangHL
 */
public class DSCancelCheckoutStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	/**
	 * @param context
	 * @param sd
	 * @throws DynaDataException
	 */
	public DSCancelCheckoutStub(DataServerContext context, InstanceServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 取消检出
	 * 
	 * @param foundationObject
	 * @param isCheckAcl
	 * @param isOwnerOnly
	 * @param sessionId
	 * @throws DynaDataException
	 */
	protected void cancelCheckout(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();

		if (!foundationObject.isCheckOut())
		{
			throw new DynaDataExceptionAll("cancelCheckout error. not in checkout status. id=" + foundationObject.getId(), null, DataExceptionEnum.DS_NON_CHECKOUT,
					foundationObject.getId());
		}

		// authority check 非本人取消检出才判断权限
		if (ISCHECKACL && isCheckAcl && !userGuid.equals(foundationObject.getCheckedOutUserGuid())
				&& !DataServer.getAclService().hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.CANCELCHECKOUT, sessionId))
		{
			throw new DynaDataExceptionAll("cancelCheckout error.no Authority id=" + foundationObject.getId(), null, DataExceptionEnum.DS_NO_CANCELCHECKOUT_AUTH,
					foundationObject.getId());
		}

		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			this.stubService.rollbackIteration(foundationObject.getObjectGuid(), foundationObject.getIterationId() - 1, false, sessionId);
			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("cancelCheckout() guid=" + foundationObject.getObjectGuid().getGuid(), e, DataExceptionEnum.DS_CANCEL_CHECKOUT,
					foundationObject.getId());
		}

	}

}
