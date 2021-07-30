/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPS Enterprise Resource Planning Service  ERP集成服务
 * caogc 2010-10-12
 */
package dyna.net.service.brs;

import java.util.Map;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.erp.ERPQuerySchemaParameterObject;
import dyna.common.bean.erp.ERPSchema;
import dyna.common.bean.erp.ERPYFAllConfig;
import dyna.common.bean.erp.ERPYFPLMClassConfig;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.ErrorRecord;
import dyna.common.dto.Queue;
import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.dto.erp.ERPBOConfig;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.erp.ERPTransferLog;
import dyna.common.dto.erp.tmptab.ERPTempTableInfo;
import dyna.common.dto.erp.tmptab.ERPtempData;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ERPServerType;
import dyna.net.service.Service;

/**
 * ERPS ERP集成服务
 * 
 * @author caogc
 * 
 */
public interface ERPI extends Service
{
	/**
	 * 所选ERP名称：易飞/易拓/WorkFlow/TipTop 通过所选ERP服务名称取得所有的配置信息
	 * 
	 * @param selectServerName
	 *            所选ERP类型
	 * @return
	 */
	public List<ERPServiceConfig> listERPServiceConfigByERPServiceName(String selectServerName) throws ServiceRequestException;

	/**
	 * 取得ERPServiceConfig中的默认值
	 * 
	 * @param selectServerName
	 * @return
	 */
	public ERPServiceConfig getERPServiceConfigDefaultvalues(String selectServerName) throws ServiceRequestException;

	/**
	 * 通过所选服务、版本、和服务配置名称得到ERPServiceConfig的对象
	 * 
	 * @param selectServerName
	 * @param serverConfigName
	 * @return ERPServiceConfig
	 */
	public ERPServiceConfig getERPServiceConBySerNandSerConN(String selectServerName, String serverConfigName) throws ServiceRequestException;

	/**
	 * 取得CrossServiceConfig中的默认值 从xml文件中取
	 * 
	 * @return
	 */
	public CrossServiceConfig getCrossServiceConfigDefaultvalues() throws ServiceRequestException;

	/**
	 * 取得系统的ERP服务种类
	 * 
	 * @return
	 */
	public List<ERPServerType> listERPType() throws ServiceRequestException;

	/**
	 * 所选ERP名称：易飞/易拓/WorkFlow/TipTop 通过所选ERP服务名称和版本号取得所有的配置信息的名称
	 * 
	 * @param selectServerName
	 * @param version
	 * @return
	 */
	public List<String> listERPServiceConfigNameByName(String selectServerName) throws ServiceRequestException;

	/**
	 * 根据BO的className取得可用的ERPServiceConfig
	 * 
	 * @param boClassName
	 *            Bo的className
	 * @return List
	 * @throws ServiceNotFoundException
	 */
	public List<ERPServiceConfig> listERPServiceConfigNameByBoClassName(String boClassName) throws ServiceRequestException;

	/**
	 * 删除ERP服务配置模板以及PLM类权限设置
	 * 
	 * @param ServiceGuid
	 *            服务的Guid
	 * 
	 */
	public void deleteERPConfig(String serviceGuid) throws ServiceRequestException;

	/**
	 * @param allConfig
	 *            更新ERP配置模板
	 */
	public ERPYFAllConfig saveTemplate(ERPYFAllConfig allConfig) throws ServiceRequestException;

	/**
	 * 通过服务的Guid，得到ERPServiceConfig的信息
	 * 
	 * @param serviceGuid
	 *            服务Guid
	 * @return
	 */

	public ERPServiceConfig getERPServiceConfigbyServiceGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 通过服务的Guid，得到模板中配置的结束后需要通知的人
	 * 
	 * @param serviceGuid
	 *            服务Guid
	 * @return
	 */
	public List<WorkflowTemplateActPerformerInfo> listTemplateEndNoticeUsersByServGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 通过服务的Guid，得到模板中配置的可用该集成的人
	 * 
	 * @param serviceGuid
	 * @return
	 */
	public List<WorkflowTemplateActPerformerInfo> listTemplateCanUseUsersByServGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 通过服务的Guid得到所有结束后的需通知人的userGuid
	 * 
	 * @param serviceGuid
	 * @return
	 */
	public List<String> listEndNoticeUsersByServGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 重新保存结束后需要通知的人
	 * 
	 * @return
	 */

	public void saveEndNoticeUsers(String serviceGuid, List<WorkflowTemplateActPerformerInfo> userList) throws ServiceRequestException;

	/**
	 * 通过服务的Guid找到对应的ERPBoConfig
	 * 
	 * @param serviceGuid
	 * @return
	 */
	public List<ERPBOConfig> listERPBOConfigByServGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 通过UserGuid得到可用的ERP服务
	 * 
	 * @return
	 */

	public List<ERPServiceConfig> listERPSerConfigNameByUseGuid(String serName, String userGuid) throws ServiceRequestException;

	/**
	 * 通过所选服务、版本号、配置名称删掉配置模板
	 * 
	 * @param
	 * @param
	 * @param
	 * @return
	 */

	public void deleteERPAllConfigBySNandCN(String selectServerName, String serverConfigName) throws ServiceRequestException;

	/**
	 * 
	 * @param objectGuidList
	 *            集成schema信息
	 * @param erpFactory
	 *            运营中心
	 * @param templateName
	 *            模板名
	 * @param ServerGuid
	 *            服务器
	 * @param userList
	 * @param isGoFlow
	 * @param isMerge
	 *            是否合并对象到一个队列
	 * @throws ServiceRequestException
	 * @throws ServiceNotFoundException
	 */
	public void createERPJob(List<ObjectGuid> objectGuidList, List<String> erpFactory, String templateName, String ServerGuid, List<WorkflowTemplateActPerformerInfo> userList,
			boolean isGoFlow, boolean isMerge) throws ServiceRequestException;

	/**
	 * 通过服务的GUid的到ERPYFPLMClassConfig对象
	 * 
	 * @param
	 * @return
	 */
	public ERPYFPLMClassConfig getERPYFPLMClassConfig(String serviceGuid) throws ServiceRequestException;

	/**
	 * 根据服务的Guid、执行用户Guid和身份取得其可用的ERPBOConfig
	 * 
	 * @param serviceGuid
	 *            服务的Guid
	 * @param userGuid
	 *            执行用户的Guid
	 * @param isAdministrator
	 *            是否是管理员组
	 * @return List 返回erpBoConfig的list
	 */
	public List<ERPBOConfig> listERPBoConfigBySerGuidAndUseGuid(String serviceGuid, String userGuid, boolean isAdministrator) throws ServiceRequestException;

	/**
	 * 显示方案名称
	 * 
	 * @param serverType
	 *            服务类型
	 * @return String 方案名称
	 * 
	 * */
	public List<String> getSchemaName(ERPServerType serverType) throws Exception;

	public ERPSchema getSchemaByName(ERPQuerySchemaParameterObject schemaParameterObj) throws Exception;

	/**
	 * 从TipTop/TopGP中取得多公司资料
	 * 
	 * @param serverDef
	 * @return List
	 */

	public List<ERPMoreCompanies> getMoreCompanyThroughWS(ERPServiceConfig serverDef) throws Exception;

	/**
	 * 通过模板取相应公司别的默认值
	 * 
	 * @param serverDef
	 * @return List
	 */
	public List<ERPMoreCompanies> listDefaultMoreCompanyByServGuid(String serverGuid) throws Exception;

	/**
	 * <p>
	 * 处理成功表示：ERP成功接收到PLM的数据，并且进行分析然后PLM成功接收到ERP返回的结果，这样就认为这个job是成功的，不论ERP中业务上是否成功或失败(如返回物料数据不正确信息)
	 * <P>
	 * 由于PLM不允许job无限制执行(即job是同步执行的)，因此在一定时间内job还未完成则认为job失败
	 * 
	 * @param objectGuid
	 *            数据Guid
	 * @param schemaName
	 *            方案名
	 * @param templateName
	 *            模板名
	 * @param lang
	 * @param userId
	 * @param factory
	 *            工厂别
	 * @param jobId
	 *            任务队列号
	 * @param serviceConfig
	 *            ERP服务器信息
	 * @return
	 * @throws Exception
	 */
	public BooleanResult exportData2ERP(ObjectGuid objectGuid, Queue jobQueue, String userId, ERPServiceConfig serviceConfig) throws Exception;

	/**
	 * 通过服务的Guid取得失败结束后需通知的人的Guid
	 * 
	 * @param serviceGuid
	 * @return List
	 */
	public List<String> listNotifyUsersWhenFailed(String serviceGuid) throws ServiceRequestException;

	/**
	 * WorkFlow ERP用 从数据库中取得多公司资料
	 * 
	 * @param serverType
	 *            服务类型：易飞服务还是易拓服务或其他
	 * @return
	 */
	public List<ERPMoreCompanies> listFacrotyFromDataBase(String serverType) throws ServiceRequestException;

	/**
	 * WorkFlow ERP 用
	 * 
	 * @param serviceGuid
	 * @return List :公司代号
	 */
	public List<String> listFacrotyByServiceGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * WorkFlow ERP 用
	 * 
	 * @return List
	 * @throws ServiceNotFoundException
	 */
	public List<ERPServiceConfig> listServices() throws ServiceRequestException;

	/**
	 * 通过服务的Guid取得失败结束后需通知的人对象
	 * 
	 * @param serviceGuid
	 * @return List
	 */
	public List<WorkflowTemplateActPerformerInfo> listFialdEndNoticeByServGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 取得产品注册资讯
	 * 
	 * @param ip
	 * @param uid 产品唯一识别码
	 * @return String
	 * */
	public String getProdRegInfo(String ip, String uid) throws ServiceRequestException;

	/**
	 * 取得服务注册资讯
	 * 
	 * @param ip
	 * @return String
	 */
	public String getSrvRegInfo(String ip, String uid) throws ServiceRequestException;

	/**
	 * 接收整合设定同步资讯
	 * 
	 * @param XMLString
	 * @return String
	 * @throws Exception
	 */
	public String doSyncProcess(String paramXML) throws Exception;

	/**
	 * 接收编码启用状态
	 * 
	 * @param paramXML
	 * @return String
	 */
	public String syncEncodingState(String paramXML) throws ServiceRequestException;

	/**
	 * 获取编码状态
	 * 
	 * @param paramXML
	 * @return String
	 */
	public String getEncodingState(String paramXML) throws ServiceRequestException;

	/**
	 * Cross传送整合规范参数资讯至产品
	 * 
	 * @param paramXML
	 * @return String
	 */
	public String syncInvokeParam(String paramXML) throws ServiceRequestException;

	/**
	 * Portal集成 检查用户ID和密码：密码为密文
	 * 
	 * @param userAccount
	 *            用户账号
	 * @param tripleDESPassword
	 *            Triple DES加密且编码后的密码
	 * @throws ServiceNotFoundException
	 */
	public String checkPortalUserPassword(String userAccount, String tripleDESPassword) throws ServiceRequestException;

	/**
	 * 测试连接，如果ERP的WS提供了测试连接方法则调用测试连接方法，否则一般调用读取营运中心方法
	 * 
	 * @param serviceConfig
	 * @return
	 * @throws Exception
	 */
	public BooleanResult checkConnection(ERPServiceConfig serviceConfig) throws Exception;

	/**
	 * 从xml中取得默认的BOM模板名称
	 * 
	 * @param ERPType
	 * @return
	 */
	public String getDefaultBOMTemplate(ERPServerType ERPType) throws Exception;

	/**
	 * 两个Operation之间的时间间隔（系统的休眠时间，执行完第一个Operation然后休眠，休眠结束后继续执行第二个Operation） <br>
	 * 0表示不限制或没配置
	 * 
	 * @param ERPType
	 * @return
	 */
	// public int getOperationPeriodTime(ERPServerType ERPType) throws Exception;

	/**
	 * 整个传输的时间，超过这个时间限制程序应该抛出超时异常 <br>
	 * 0表示不限制或没配置
	 * 
	 * @param ERPType
	 * @return
	 */
	// public int getTotalOperationTime(ERPServerType ERPType) throws Exception;

	/**
	 * 为了防止WS传输大数据量造成传输失败，这里确定每次传输的笔数（如一次传100个物料或100层BOM） <br>
	 * 0表示不限制或没配置
	 * 
	 * @param ERPType
	 * @return
	 */
	// public int getBatchSize(ERPServerType ERPType) throws Exception;

	/**
	 * 从xml中取AVL的模板名称
	 * 
	 * @param serverType
	 * @return
	 */
	// public String getAVLTemplateName(ERPServerType serverType) throws Exception;

	/**
	 * 取得Operation标签里的值，不是id
	 * 
	 * @param schemaName
	 * @param serverType
	 * @return
	 */
	// public List<String> getOperationListBySchema(String schemaName, ERPServerType serverType) throws Exception;

	/**
	 * 保存营运中心
	 * 
	 * @param serverType
	 * @param morecompanyList
	 * @throws ServiceRequestException
	 */
	public void saveMoreCompany(String serverType, List<ERPMoreCompanies> morecompanyList) throws ServiceRequestException;

	/**
	 * 取得所有的配置模板
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listERPServiceConfigName() throws ServiceRequestException;

	/**
	 * 传输结束后通知人员
	 * 
	 * @param serviceGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowTemplateActPerformerInfo> getTemplateEndNoticeUsersByServGuid(String serviceGuid) throws ServiceRequestException;

	/**
	 * 根据操作方法的id从xml文件中取得category和name属性
	 * 
	 * @param serverType
	 * @param id
	 * @return
	 * @throws ServiceRequestException
	 */

	public void saveERPTransferLog(ERPTransferLog log) throws ServiceRequestException;

	/**
	 * 根据品号参数查询该品号所有的集成历史
	 * 
	 * @param param
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ERPTransferLog> getERPTransferLog(Map<String, Object> param) throws ServiceRequestException;

	public Map<String, String> getOperationAttribute(ERPServerType serverType, String operation) throws ServiceRequestException;

	public boolean showBOMTemplate(ERPSchema schema, ERPServerType serverType) throws ServiceRequestException;

	/**
	 * 判断Session是否过期
	 * 
	 * @return
	 */
	boolean isSessionExpire() throws ServiceRequestException;

	/**
	 * ERP读PLM图纸信息
	 * 
	 * @param paramXML
	 * @return
	 * @throws ServiceRequestException
	 */
	public String createFileData(String paramXML) throws Exception;

	public boolean isRealTime(String schemaName, ERPServerType serverType) throws Exception;

	public Map<String, String> getContentAttribute(ERPServerType serverType, String id) throws ServiceRequestException;

	/**
	 * 从ERP获取Foundation信息
	 * 
	 * @param list
	 * @param serverType
	 * @return
	 * @throws Exception
	 */
	public List<FoundationObject> getFoundationInfoFromERP(List<FoundationObject> list, List<String> factoryId, ERPServiceConfig serviceConfig) throws Exception;

	/**
	 * ERP抛分类到ERP
	 * 
	 * @param codeItemList
	 * @param config
	 * @throws ServiceRequestException
	 */
	public void createCFERPJob(List<String> codeGuidList, String codeTitle, ERPServiceConfig config, boolean isRoot) throws ServiceRequestException;

	public BooleanResult exportCF2ERP(List<String> codeGuidList, Queue queuejob, String userId, ERPServiceConfig serviceConfig) throws Exception;

	/**
	 * ERP端调用该方法来更新job状态
	 * 
	 * @param jobGuid
	 * @param userId
	 * @param jobStatusVal
	 * @param message
	 * @throws ServiceRequestException
	 */
	public BooleanResult setERPJobStatus(String jobGuid, String userId, String jobStatusVal, String message, boolean isNotify) throws ServiceRequestException;

	/**
	 * 从crossconf.xml中取得cross是否需要编码
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isCrossEncoding();

	public ERPTempTableInfo getBaseTableInfo(Map<String, Object> map) throws ServiceRequestException;

	public List<ERPTempTableInfo> listBaseTableInfo(Map<String, Object> map) throws ServiceRequestException;

	public void insertBaseTableInfo(ERPTempTableInfo info) throws ServiceRequestException;

	public void createTable(Map<String, Object> map) throws ServiceRequestException;

	public List<ERPtempData> listERPTempData(Map<String, Object> map) throws ServiceRequestException;

	public void insertTempData(ERPtempData data) throws ServiceRequestException;

	public void updateTempData(ERPtempData data) throws ServiceRequestException;

	/**
	 * 流程中合并多对象到一个队列中
	 * 
	 * @param objectGuids
	 * @param queuejob
	 * @param userId
	 * @param serviceConfig
	 * @return
	 * @throws Exception
	 */
	public BooleanResult exportMergeData2ERP(List<ObjectGuid> objectGuids, Queue queuejob, String userId, ERPServiceConfig serviceConfig) throws Exception;

	public BooleanResult getJobStatusBySeqkeyFromERP(String jobGuid) throws Exception;

	public boolean isSingleSignOn(String type) throws Exception;

	/**
	 * T100DB抛分类到ERP
	 * 
	 * @param codeGuid
	 * @param erpFactoryList
	 * @param config
	 * @param isRoot
	 * @throws ServiceRequestException
	 */
	public void createCFERPJob(String codeGuid, List<String> erpFactoryList, ERPServiceConfig config, boolean isRoot) throws ServiceRequestException;

	/**
	 * 获取DBLink的配置信息
	 * 含URL,userName,password
	 * 目前只用于ERPT100中间库集成类型
	 * 其余类型ERP请慎用
	 * 
	 * @param serverType
	 * @return
	 *         目前只支持T100DB，其他ERP类型返回Null
	 * @throws Exception
	 */
	public Map<String, String> getDbLinkConfig(ERPServerType serverType) throws Exception;

	/**
	 * T100中间库集成方式
	 * 根据jobId查询错误记录信息
	 * 
	 * @param jobId
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ErrorRecord> listErrorRecordInfo(String jobId) throws Exception;

	public List<Integer> getObjectDataFromERP(ERPSchema schema) throws Exception;

	public String getCategoryType(String operation) throws Exception;
}
