/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JSS Job Serialized Service 工作序列服务
 * Wanglei 2011-11-7
 */
package dyna.net.service.das;

import java.util.List;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.conf.JobDefinition;
import dyna.common.dto.Queue;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobStatus;
import dyna.net.service.Service;

/**
 * Job Serialized Service 工作序列服务 提供执行顺序计划工作任务的服务
 * 
 * @author Wanglei
 * 
 */
public interface JSS extends Service
{

	/**
	 * 根据guid查询工作任务
	 * 
	 * @param jobGuid
	 *            工作任务guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public Queue getJob(String jobObjectGuid) throws ServiceRequestException;

	/**
	 * 查询工作任务列表
	 * 
	 * @param condition
	 *            条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listJob(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 查询等待执行的工作任务列表
	 * 
	 * @param condition
	 *            条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listWaitingJob(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 查询正在执行的工作任务列表
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listRunningJob(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 查询已经取消的工作任务列表
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listCancelJob(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 查询执行成功的工作任务列表
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listSuccessfulJob(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 查询执行失败的工作任务列表
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listFailedJob(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 创建工作任务
	 * 
	 * @param fo
	 *            工作任务对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public Queue createJob(Queue fo) throws ServiceRequestException;

	/**
	 * 保存(更新)工作任务<br>
	 * 更新工作任务状态,请参考{@link #setJobStatus(FoundationObject, JobStatus)}
	 * 
	 * @param fo
	 * @return
	 * @throws ServiceRequestException
	 */
	public Queue saveJob(Queue fo) throws ServiceRequestException;

	public Queue saveJob4ERPNotify(Queue fo) throws ServiceRequestException;

	/**
	 * 更新工作任务状态, 转换规则如下:<br>
	 * WAITING -> RUNNING, CANCEL<br>
	 * RUNNING -> SUCCESSFUL, FAILED<br>
	 * CANCEL -> WAITING<br>
	 * SUCCESSFUL -> WAITING<br>
	 * FAILED -> WAITING
	 * 
	 * @param fo
	 * @param jobStatus
	 * @return
	 * @throws ServiceRequestException
	 */
	public Queue setJobStatus(Queue fo, JobStatus jobStatus) throws ServiceRequestException;

	/**
	 * 删除任务
	 * 
	 * @param jobGuid
	 *            任务guid
	 * @throws ServiceRequestException
	 */
	public void deleteJobs(String... jobGuids) throws ServiceRequestException;

	/**
	 * 按照类型删除超期对列
	 * 
	 * @param jobType
	 *            对列类型
	 * @param timeOut
	 *            超期时间
	 * @throws ServiceRequestException
	 */
	public void deleteTimeoutJobs(String jobType, int timeOut) throws ServiceRequestException;

	/**
	 * 获取任务类型
	 * 
	 * @throws ServiceRequestException
	 */
	public List<JobDefinition> getJobQueueTypeList() throws ServiceRequestException;

	/**
	 * 获取未执行完成的任务
	 * 
	 * @param typeId
	 *            任务类型ID
	 * @param isSearchOwner
	 * 
	 * @param starindex
	 * @param pageSize
	 * @param searchnext
	 * @param statuslist
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listJob(String typeId, boolean isSearchOwner, List<JobStatus> statuslist) throws ServiceRequestException;

	/**
	 * 通过条件查询Queue
	 * 
	 * @param searchCondition
	 *            条件Map
	 * @param starindex
	 *            开始行数
	 * @param pageSize
	 *            每页条数
	 * @param searchnext
	 *            是否向前翻页
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listJobQueue(Map<String, Object> searchCondition, int pageNum, int pageSize) throws ServiceRequestException;

	/**
	 * 重启队列
	 * 
	 * @param queue
	 * @throws ServiceRequestException
	 */
	public void reStartQueue(Queue queue) throws ServiceRequestException;
}
