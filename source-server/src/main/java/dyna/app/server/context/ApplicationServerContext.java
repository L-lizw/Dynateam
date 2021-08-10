/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerContext
 * Wanglei 2010-3-30
 */
package dyna.app.server.context;

import dyna.app.core.i18n.NLSManager;
import dyna.app.core.lic.LicenseDaemon;
import dyna.app.core.pool.PoolManager;
import dyna.app.core.sch.SchedulerManager;
import dyna.app.core.track.TrackerManager;
import dyna.common.conf.ConfigurableServerImpl;
import dyna.common.context.SvContext;
import dyna.net.connection.ConnectionManager;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateManager;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.UserSignature;
import org.springframework.remoting.support.RemoteInvocation;

import java.util.List;

/**
 * 服务器上下文接口
 * 
 * @author Wanglei
 * 
 */
public interface ApplicationServerContext extends SvContext
{

	/**
	 * 获取服务端配置参数
	 * 
	 * @return
	 */
	public ConfigurableServerImpl getServerConfig();

	/**
	 * 获取对象池管理器
	 * 
	 * @return
	 */
	public PoolManager getPoolManager();

	/**
	 * 获取凭证管理器
	 * 
	 * @return
	 */
	public CredentialManager getCredentialManager();

	/**
	 * 获取客户连接管理器
	 * 
	 * @return
	 */
	public ConnectionManager getConnectionManager();

	/**
	 * 获取服务状态管理器
	 * 
	 * @return
	 */
	public ServiceStateManager getServiceStateManager();

	/**
	 * 获取license守护
	 * 
	 * @return
	 */
	public LicenseDaemon getLicenseDaemon();

	/**
	 * 获取计划任务调度管理器
	 * 
	 * @return
	 */
	public SchedulerManager getSchedulerManager();

	/**
	 * 获取用户操作日志管理器
	 * 
	 * @return
	 */
	public TrackerManager getTrackerManager();

	/**
	 * 获取多语言管理器
	 * 
	 * @return
	 */
	public NLSManager getNLSManager();

	/**
	 * 设置服务状态变更协调器
	 * 
	 * @param sscReactor
	 *            the sscReactor to set
	 */
	public void setSscReactor(ServiceStateChangeReactor sscReactor);

	/**
	 * 获取服务状态变更协调器
	 * @return
	 */
	public ServiceStateChangeReactor getSscReactor();

	/**
	 * 获取系统用户会话签名
	 * 
	 * @return
	 */
	public UserSignature getSystemInternalSignature();

	/**
	 * 获取会话的最后更新时间,用于缓存,不是从数据库取数据.<br>
	 * 
	 * @param sessionId
	 *            会话id
	 * @param updateTime
	 *            更新时间
	 * @return
	 */
	public boolean shouldUpdateSessionTime(String sessionId, long updateTime);

	/**
	 * 删除缓存内对应会话的最后更新时间, 用户登出系统时调用.
	 * 
	 * @param sessionId
	 */
	public void removeSessionUpdateTime(String sessionId);

	/**
	 * 是否调试模式
	 * 
	 * @return
	 */
	public boolean isDebugMode();

	/**
	 * 设置调试模式
	 * 
	 * @param value
	 */
	public void setDebugMode(boolean value);

	/**
	 * 记录远程调用开始
	 * 
	 * @param value
	 * 
	 */
	public void startRMIRemoteInvocation(RemoteInvocation invocation, String Ip);

	/**
	 * 远程调用结束
	 * 
	 * @param value
	 */
	public void finishRMIRemoteInvocation(RemoteInvocation invocation);

	/**
	 * 获取超时远程调用
	 * 
	 * @return
	 */
	public List<List<Object>> listRMIRemoteInvocation();

}
