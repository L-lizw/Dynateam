/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TrackerRenderer
 * Wanglei 2011-11-14
 */
package dyna.app.core.track;

/**
 * @author Wanglei
 *
 */
public interface TrackerRenderer
{

	/**
	 * 获取操作者会话id
	 * 
	 * @return
	 */
	public String getSessionId(Tracker tracker);

	/**
	 * 处理客户端地址表示的字符串, 该返回值将记录到系统日志内
	 * 
	 * @param track
	 * @return 地址串
	 */
	public String getAddress(Tracker tracker);

	/**
	 * 处理操作者信息, 返回值将记录到系统日志内
	 * 
	 * @param tracker
	 * @return
	 */
	public String getOperatorInfo(Tracker tracker);

	/**
	 * 处理操作者GUID, 返回值将记录到系统日志内
	 * 
	 * @param tracker
	 * @return
	 */
	public String getOperatorGuid(Tracker tracker);

	/**
	 * 处理操作内容, 返回值将记录到系统日志内
	 * 
	 * @param track
	 * 
	 * @return
	 */
	public TrackerDescription getTrackerDescription();

	/**
	 * 设置操作内容处理器
	 * 
	 * @param desc
	 */
	public void setTrackerDescription(TrackerDescription desc);

	/**
	 * 处理操作对象, 返回值将记录到系统日志内
	 * 
	 * @param tracker
	 * 
	 * @return
	 */
	public String getHandledObject(Tracker tracker);

	/**
	 * 处理操作的结果, 返回值将记录到系统日志内
	 * 
	 * @param tracker
	 * 
	 * @return
	 */
	public String getResult(Tracker tracker);
}
