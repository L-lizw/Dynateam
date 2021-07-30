/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CSA Common Service Access
 * Wanglei 2010-9-6
 */
package dyna.net.service.das;

import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * Common Service Access<br>
 * 提供公用服务访问接入
 * 
 * @author Wanglei
 * 
 */
public interface CSA extends Service
{

	/**
	 * 注册SFTP系统模块
	 * 
	 * @return SFTP模块访问服务所需的凭证credential
	 * @throws ServiceRequestException
	 */
	public String registerDSS() throws ServiceRequestException;

	/**
	 * 注册系统会话管理模块
	 * 
	 * @return 系统会话管理模块访问服务所需的凭证credential
	 * @throws ServiceRequestException
	 */
	public String registerSessionManager() throws ServiceRequestException;

}
