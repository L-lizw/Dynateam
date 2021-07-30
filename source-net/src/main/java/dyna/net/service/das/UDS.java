/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UDS 客户端更新服务
 * Wanglei 2011-9-13
 */
package dyna.net.service.das;

import dyna.common.util.EnvUtils;
import dyna.net.service.Service;
import dyna.net.syncfile.transfer.TransferEnum;

/**
 * 客户端更新服务
 * 
 * @author Wanglei
 * 
 */
public interface UDS extends Service
{
	public static final String	UDPKG_FOLDER	= EnvUtils.getRootPath() + "udpkg/";

	public static final String	UDPKG_FILE		= EnvUtils.getRootPath() + "udpkg/update.xml";

	/**
	 * 判断是否需要更新客户端程序<br>
	 * 读取更新包的序号 > lastUpdateTime, 则返回软件包更新时间; 否则返回0<br>
	 * 更新包不存在, 则返回0;
	 * 
	 * @param lastUpdateIndex
	 *            客户端最后一次更新程序的序号
	 * @return
	 */
	public long shouldUpdate(long lastUpdateIndex);

	/**
	 * 判断是否需要更新客户端程序<br>
	 * 读取更新包的序号 > lastUpdateTime, 则返回软件包更新时间; 否则返回0<br>
	 * 更新包不存在, 则返回0;
	 * 
	 * @param transferEnum
	 *            客户端更新类型
	 * @param lastUpdateTime
	 *            客户端最后一次更新程序的序号
	 * @return
	 */
	public long shouldUpdate(TransferEnum transferEnum, long lastUpdateIndex);
}
