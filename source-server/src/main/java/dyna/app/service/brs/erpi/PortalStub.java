/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PortStub
 * wangweixia 2012-8-6
 */
package dyna.app.service.brs.erpi;

import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.aas.User;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.DESEndecryptUtils;
import dyna.common.util.EncryptUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author wangweixia
 * 
 */
@Component
public class PortalStub extends AbstractServiceStub<ERPIImpl>
{

	public static final String	SPKEY	= "9E73DC0E80C3D366B72C46P8";

	/**
	 * @param userAccount
	 * @param tripleDESPassword
	 * @return
	 * @throws ServiceRequestException
	 * @throws ServiceNotFoundException 
	 */
	protected String checkPortalUserPassword(String userAccount, String tripleDESPassword)
			throws ServiceRequestException
	{
		User user = this.stubService.getAAS().getUserById(userAccount);
		if (user != null && !StringUtils.isNullString(user.getGuid()))
		{

			if (!StringUtils.isNullString(tripleDESPassword))
			{
				// 解密
				try
				{
					tripleDESPassword = DESEndecryptUtils.get3DESDecrypt(tripleDESPassword, SPKEY);
				}
				catch (Exception e)
				{

					e.printStackTrace();
				}
				// 用MD5方式加密
				tripleDESPassword = EncryptUtils.encryptMD5(tripleDESPassword);
				if (tripleDESPassword.equals(user.getPassword()))
				{
					return "Y";
				}
				else
				{
					return "N";
				}
			}
			else
			{
				return "N";
			}
		}
		else
		{
			return "N";
		}

	}
}
