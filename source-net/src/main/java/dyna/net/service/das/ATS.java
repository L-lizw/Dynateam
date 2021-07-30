/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ATS
 * Wanglei 2011-11-14
 */
package dyna.net.service.das;

import java.util.Date;
import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.dto.SysTrack;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * Action Track Service 用户操作跟踪服务(系统日志)
 * 
 * @author Wanglei
 * 
 */
public interface ATS extends Service
{

	/**
	 * 查询日志
	 * 
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<SysTrack> listTrack(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 删除日志
	 * 
	 * @param trackGuids
	 *            必须包含至少一条记录
	 * @throws ServiceRequestException
	 */
	public void deleteTrack(String... trackGuids) throws ServiceRequestException;

	/**
	 * 删除某会话或者某用户或者某时间段内的日志<br>
	 * 参数可随意组合, 得到需要的结果
	 * 
	 * @param sessionId
	 *            会话id 指定会话id的日志
	 * @param userId
	 *            用户id 指定用户id的日志, 如果指定了sessionId, 则此参数无效
	 * @param fromDate
	 *            开始时间(含), 为空表示不指定开始时间, 只判断结束时间
	 * @param toDate
	 *            结束时间(含), 为空表示截止到当前时间
	 * @throws ServiceRequestException
	 */
	public void deleteTrackByDate(String sessiondId, String userId, Date fromDate, Date toDate) throws ServiceRequestException;

	/**
	 * 清空系统的所有日志
	 * 
	 * @throws ServiceRequestException
	 */
	public void clearTrack() throws ServiceRequestException;

}
