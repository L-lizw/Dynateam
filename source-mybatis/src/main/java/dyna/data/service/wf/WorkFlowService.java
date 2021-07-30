/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data service interface
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.data.service.wf;

import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

import java.util.List;

/**
 * 数据服务
 * 
 * @author xiasheng
 */
public interface WorkFlowService extends Service
{
	void deleteUnExistsAttach(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询当前master在流程中的所有版本
	 * 
	 * @param masterGuid
	 * @param classGuid
	 * @return
	 * @throws DynaDataException
	 */
	List<ProcAttach> listRevisionInWF(String masterGuid, String classGuid);

	/**
	 * 查询流程实例活动节点
	 * 
	 * @param actRtGuid
	 * @return
	 */
	ActivityRuntime getActivityRuntime(String actRtGuid);

	/**
	 * 查询流程实例开始活动节点
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	ActivityRuntime getBeginActivityRuntime(String procRtGuid);

	/**
	 * 查询流程实例结束活动节点
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	ActivityRuntime getEndActivityRuntime(String procRtGuid);

	/**
	 * 根据guid删除流程实例
	 * 
	 * @param processRuntime
	 * @throws ServiceRequestException
	 */
	void deleteProcess(ProcessRuntime processRuntime) throws DynaDataException;
}