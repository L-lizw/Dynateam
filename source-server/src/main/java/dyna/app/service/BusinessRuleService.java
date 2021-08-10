/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaBIService
 * Wanglei 2010-3-26
 */
package dyna.app.service;

import dyna.app.core.Authorizable;
import dyna.common.dto.aas.Group;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.AAS;

import java.lang.reflect.Method;

/**
 * 业务逻辑服务适配器, 提供服务的基础设置与实现
 * 具体业务逻辑服务需扩展此接口
 * 
 * @author Wanglei
 * 
 */
public class BusinessRuleService extends DataAccessService implements Authorizable
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Authorizable#authorize(dyna.common.Signature)
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("invoke [" + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "] by " + this.getSignature());
		}
		if (this.getSignature() == null)
		{
			throw new AuthorizeException(AuthorizeException.ID_PER_DENIED, "unauthorized signature", null);
		}
	}

	/**
	 * 检查调用者是否属于管理组
	 * 
	 * @param signature
	 * @throws AuthorizeException
	 */
	public void administrativeAuthorize(UserSignature signature, AAS aas) throws AuthorizeException
	{
		String groupGuid = signature.getLoginGroupGuid();
		try
		{
			Group group = aas.getGroup(groupGuid);
			if (group == null || !group.isAdminGroup())
			{
				throw new AuthorizeException("accessible for administrative group only");
			}
		}
		catch (ServiceRequestException e)
		{
			throw new AuthorizeException("accessible for administrative group only", e.fillInStackTrace());
		}
	}
}
