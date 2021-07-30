package dyna.net.service.brs;

import java.util.List;
import java.util.Map;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.trans.TransformConfig;
import dyna.common.bean.data.trans.TransformFieldMapping;
import dyna.common.bean.data.trans.TransformManualConfig;
import dyna.common.bean.data.trans.TransformObjectConfig;
import dyna.common.bean.data.trans.TransformQueue;
import dyna.common.bean.data.trans.TransformSign;
import dyna.common.bean.data.trans.TransformSignObjectMap;
import dyna.common.bean.data.trans.TransformSignParam;
import dyna.common.bean.data.trans.TransformSignWFMap;
import dyna.common.bean.data.trans.TransformWFConfig;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobStatus;
import dyna.net.service.Service;

public interface FTS extends Service
{

	/**
	 * 通过工作流创建队列
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	public void createTransformQueue4WF(ActivityRuntime activity) throws ServiceRequestException;

	/**
	 * 手动创建队列
	 * 
	 * @param objectGuid
	 * @param config
	 * @throws ServiceRequestException
	 */
	public void createTransformQueue4Manual(ObjectGuid objectGuid, TransformConfig config) throws ServiceRequestException;

	/**
	 * 检入时创建队列
	 * 
	 * @param objectGuid
	 * @throws ServiceRequestException
	 */
	public void createTransformQueue4Checkin(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 文件上传创建队列
	 * 
	 * @param objectGuid
	 * @param fileGuid
	 * @throws ServiceRequestException
	 */
	public void createTransformQueue4Upload(ObjectGuid objectGuid, String fileGuid) throws ServiceRequestException;

	/**
	 * 取得队列列表，查看时所用
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformQueue> listQueue(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 取得队列列表，转换时所用
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformQueue> listQueue4Trans(Map<String, Object> condition) throws ServiceRequestException;

	/**
	 * 获取根据队列获取所有子队列
	 * 
	 * @param queueGuid
	 * @return
	 */
	public TransformQueue getQueueInfo(String queueGuid) throws ServiceRequestException;

	/**
	 * 生更上传任务单号
	 * 如果配置中需要重新生成对象，则新生成
	 * 
	 * @param transfromQueueGuid
	 * @param fileName
	 * @return
	 */
	public DSSFileTrans uploadQueueInfo(String transfromQueueGuid, DSSFileInfo fileInfo) throws ServiceRequestException;

	/**
	 * 更新任务队列
	 * 
	 * @param childGuid
	 * @param fileMD5
	 */
	public TransformQueue updateQueueInfo(String queueGuid, String status, String result) throws ServiceRequestException;

	/**
	 * 重启队列
	 * 
	 * @param queue
	 * @throws ServiceRequestException
	 */
	public void reStartQueue(TransformQueue queue) throws ServiceRequestException;

	/**
	 * 删除队列
	 * 
	 * @param queue
	 * @throws ServiceRequestException
	 */
	public void deleteQueue(TransformQueue queue) throws ServiceRequestException;

	/**
	 * 取得转换配置
	 * 
	 * @param transConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformConfig getTransformConfig(String transConfigGuid) throws ServiceRequestException;

	/**
	 * 保存转换配置
	 * 
	 * @param config
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformConfig saveTransformConfig(TransformConfig config) throws ServiceRequestException;

	/**
	 * 取得工作流配置
	 * 
	 * @param transConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformWFConfig> listWFConfig(String transConfigGuid) throws ServiceRequestException;

	/**
	 * 保存工作流配置
	 * 
	 * @param wfConfig
	 * @throws ServiceRequestException
	 */
	public void saveWFConfig(TransformWFConfig wfConfig) throws ServiceRequestException;

	/**
	 * 删除工作流配置
	 * 
	 * @param wfConfigGuid
	 * @throws ServiceRequestException
	 */
	public void deleteWFConfig(String wfConfigGuid) throws ServiceRequestException;

	/**
	 * 取得对象配置
	 * 
	 * @param transConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformObjectConfig getObjectConfig(String transConfigGuid) throws ServiceRequestException;

	/**
	 * 保存对象配置
	 * 
	 * @param objectConfig
	 * @throws ServiceRequestException
	 */
	public void saveObjectConfig(TransformObjectConfig objectConfig) throws ServiceRequestException;

	/**
	 * 删除对象配置
	 * 
	 * @param objectConfig
	 * @throws ServiceRequestException
	 */
	public void deleteObjectConfig(String objectConfig) throws ServiceRequestException;

	/**
	 * 取得手动配置
	 * 
	 * @param transConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformManualConfig getManualConfig(String transConfigGuid) throws ServiceRequestException;

	/**
	 * 保存手动配置
	 * 
	 * @param manualConfig
	 * @throws ServiceRequestException
	 */
	public void saveManualConfig(TransformManualConfig manualConfig) throws ServiceRequestException;

	/**
	 * 删除手动配置
	 * 
	 * @param manualConfig
	 * @throws ServiceRequestException
	 */
	public void deleteManualConfig(String manualConfig) throws ServiceRequestException;

	/**
	 * 取得字段映射列表
	 * 
	 * @param transConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformFieldMapping> listFieldMappingConfig(String transConfigGuid) throws ServiceRequestException;

	/**
	 * 保存字段映射
	 * 
	 * @param fieldMapping
	 * @throws ServiceRequestException
	 */
	public void saveFieldMapping(TransformFieldMapping fieldMapping) throws ServiceRequestException;

	/**
	 * 删除字段映射
	 * 
	 * @param fieldMapping
	 * @throws ServiceRequestException
	 */
	public void deleteFieldMapping(String fieldMapping) throws ServiceRequestException;

	/**
	 * 删除转换配置
	 * 
	 * @param configGuid
	 * @throws ServiceRequestException
	 */
	public void deleteTransformConfig(String configGuid) throws ServiceRequestException;

	/**
	 * 取得转换配置
	 * 
	 * @param filter
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformConfig> listTransformConfig(Map<String, Object> filter) throws ServiceRequestException;

	/**
	 * 取得转换配置，包含配置触发条件及字段映射关系
	 * 
	 * @param filter
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformConfig> listTransformConfigContain(Map<String, Object> filter) throws ServiceRequestException;

	/**
	 * 取得手动转换配置
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformConfig> listTransformConfig4Manual(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 保存签名
	 * 
	 * @param sign
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformSign saveTransSign(TransformSign sign) throws ServiceRequestException;

	/**
	 * 取得签名
	 * 
	 * @param filter
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformSign> listTransformSign(Map<String, Object> filter) throws ServiceRequestException;

	/**
	 * 查询签名参数
	 * 
	 * @param queueGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformSignParam> listTransformSignParam(String queueGuid) throws ServiceRequestException;

	/**
	 * 删除签名
	 * 
	 * @param configGuid
	 * @throws ServiceRequestException
	 */
	public void deleteTransformSign(String configGuid) throws ServiceRequestException;

	/**
	 * 保存签名参数
	 * 
	 * @param signParam
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformSignParam saveSignParam(TransformSignParam signParam) throws ServiceRequestException;

	/**
	 * 查询转换参数
	 * 
	 * @param filter
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformSignParam> listTransformSignParam(Map<String, Object> filter) throws ServiceRequestException;

	/**
	 * 删除转换签名参数
	 * 
	 * @param configGuid
	 * @throws ServiceRequestException
	 */
	public void deleteTransformSigParam(String configGuid) throws ServiceRequestException;

	/**
	 * 保存签名对象配置
	 * 
	 * @param signObjectMap
	 * @throws ServiceRequestException
	 */
	public void saveSignObjectMap(TransformSignObjectMap signObjectMap) throws ServiceRequestException;

	/**
	 * 取得签名对象配置
	 * 
	 * @param filter
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformSignObjectMap> listTransformSignObjectMap(Map<String, Object> filter) throws ServiceRequestException;

	/**
	 * 删除签名对象配置
	 * 
	 * @param configGuid
	 * @throws ServiceRequestException
	 */
	public void deleteTransformSigObjectMap(String configGuid) throws ServiceRequestException;

	/**
	 * 保存签名工作流配置
	 * 
	 * @param signWFMap
	 * @throws ServiceRequestException
	 */
	public void saveSignWFMap(TransformSignWFMap signWFMap) throws ServiceRequestException;

	/**
	 * 查询签名工作流配置
	 * 
	 * @param filter
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformSignWFMap> listTransformSignWFMap(Map<String, Object> filter) throws ServiceRequestException;

	/**
	 * 删除 签名工作流配置
	 * 
	 * @param configGuid
	 * @throws ServiceRequestException
	 */
	public void deleteTransformSignWFMap(String configGuid) throws ServiceRequestException;

	/**
	 * 下载队列文件
	 * 
	 * @param queueGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadQueueInfo(String queueGuid) throws ServiceRequestException;

	/**
	 * 保存转换配置
	 * 
	 * @param configList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformConfig> saveTransformConfig(List<TransformConfig> configList) throws ServiceRequestException;

	/**
	 * 保存签名配置
	 * 
	 * @param signList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TransformSign> saveTransSign(List<TransformSign> signList) throws ServiceRequestException;

	/**
	 * 修改队列状态
	 * 
	 * @param fo
	 * @param jobStatus
	 * @return
	 * @throws ServiceRequestException
	 */
	public TransformQueue changeQueueStatus(TransformQueue fo, JobStatus jobStatus) throws ServiceRequestException;

	/**
	 * 保存转换服务与转换类型的关系，以确定当前转换服务可以转换哪些类型的文件
	 * 
	 * @param serverID
	 * @param transformTypes
	 * @throws ServiceRequestException
	 */
	public void saveTransformServers(String serverID, String[] transformTypes) throws ServiceRequestException;

	/**
	 * 取得存在license的转换 列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listTLicenseModules() throws ServiceRequestException;

}
