/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LIC license control service
 * Wanglei 2010-10-12
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.dto.Session;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * license控制服务
 * 
 * @author Wanglei
 * 
 */
public interface LIC extends Service
{

	/**
	 * 获取授权版本信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getVersionInfo() throws ServiceRequestException;

	/**
	 * 获取授权ID信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getSystemIdentification() throws ServiceRequestException;

	/**
	 * 查询license有效期
	 * 
	 * @return
	 */
	public long[] getLicensePeriod() throws ServiceRequestException;

	/**
	 * 获取被占用的license数
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int[] getLicenseOccupiedNode() throws ServiceRequestException;

	/**
	 * 获取license的节点数
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int[] getLicenseNode() throws ServiceRequestException;

	/**
	 * 查找license中所有可用模块的列表<br>
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> getLicenseModuleList() throws ServiceRequestException;

	/**
	 * 查找license中所有可用模块的列表<br>
	 * 模块名称以逗号","分隔
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getLicenseModules() throws ServiceRequestException;

	/**
	 * 判断是否有指定产品模块的授权
	 * 
	 * @param moduleName
	 *            产品模块名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasLicence(String moduleName) throws ServiceRequestException;

	/**
	 * 踢出用户
	 * 
	 * @param sessionId
	 * 
	 * @throws ServiceRequestException
	 */
	public void kickUser(String sessionId) throws ServiceRequestException;

	/**
	 * 查询license占用者
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Session> listLicensedOccupant() throws ServiceRequestException;

	/**
	 * 获取session
	 * 
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	public Session getSession(String sessionId) throws ServiceRequestException;

	/**
	 * 查看用户登录的所有系统会话
	 * 
	 * @param userId
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Session> listUserSession(String userId) throws ServiceRequestException;

	/**
	 * 查询所有登陆的session
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Session> listSession() throws ServiceRequestException;

	/**
	 * 清除登陆系统的session<br>
	 * 仅系统会话管理账户能访问
	 * 
	 * @throws ServiceRequestException
	 *             权限异常AuthorizeException(ID_PERMISSION_DENIED)
	 */
	public void clearSession() throws ServiceRequestException;

	/**
	 * 清除指定id的会话<br>
	 * 仅系统会话管理账户能访问
	 * 
	 * @param sessionId
	 * @throws ServiceRequestException
	 *             权限异常AuthorizeException(ID_PERMISSION_DENIED)
	 */
	public void deleteSession(String sessionId) throws ServiceRequestException;
	
	/**
	 * 查询session的自动释放时间
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public int getSessionReleaseTime() throws ServiceRequestException;

	/**
	 * 激活session
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void activeSession() throws ServiceRequestException;

	/**
	 * 查询session的自动释放提醒时间
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public int getSessionPromptTime() throws ServiceRequestException;
}
