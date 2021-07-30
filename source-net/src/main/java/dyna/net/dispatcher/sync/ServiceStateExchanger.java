/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateExchanger
 * Wanglei 2011-1-14
 */
package dyna.net.dispatcher.sync;

/**
 * 服务状态交换器
 * 
 * @author Wanglei
 * 
 */
public interface ServiceStateExchanger
{

	/**
	 * 与服务器同步状态
	 * 
	 * @param reactor
	 */
	public void exchangeState(ServiceStateChangeReactor reactor);
}
