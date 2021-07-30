/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SRS Simple Report Service 简单报表服务
 * Wanglei 2011-3-29
 */
package dyna.net.service.brs;

import java.util.Map;
import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.dto.DataRule;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.net.service.Service;

/**
 * Simple Report Service 简单报表服务
 * 
 * @author Wanglei
 * 
 */
public interface SRS extends Service
{
	public static final String	JOIN_CHAR	= "\r\n";

	/**
	 * bom物料清单报表
	 * 
	 * @param bomViewObjectGuid
	 *            bom视图guid
	 * @param bomRule
	 *            bom规则
	 * @param level
	 *            展开的层数
	 * @param bomSearchCondition
	 *            bom查询条件
	 * @param reportUIName
	 *            report类型ui名称
	 * @throws ServiceRequestException
	 */
	public void reportGenericBOM(ObjectGuid bomViewObjectGuid, DataRule dataRule, int level, SearchCondition bomSearchCondition, String reportUIName,
			ReportTypeEnum exportFileType, String jobGuid, boolean isContainRepf) throws ServiceRequestException;

	/**
	 * * 报表工具jasper导出报表
	 * 
	 * @param uiName
	 *            UI对象
	 * @param exportFileType
	 *            导出报表的文件类型
	 * @param ObjectGuidList
	 *            导出数据的ObjectGuid
	 * @param reportcondition
	 *            报表查询条件
	 * @param isScript
	 *            是否在脚本中调用
	 * @throws ServiceRequestException
	 */
	public void reportGeneric(String uiName, ReportTypeEnum exportFileType, List<ObjectGuid> objectGuidList, SearchCondition reportCondition, boolean isScript)
			throws ServiceRequestException;

	/**
	 * * 报表工具jasper导出报表
	 * 
	 * @param uiName
	 *            UI对象
	 * @param exportFileType
	 *            导出报表的文件类型
	 * @param ObjectGuidList
	 *            导出数据的ObjectGuid
	 * @param reportcondition
	 *            报表查询条件
	 * @param isScript
	 *            是否在脚本中调用
	 * @param isMail
	 *            是否发送通知
	 * @throws ServiceRequestException
	 */
	public void reportGenericEC(String uiName, ReportTypeEnum exportFileType, List<ObjectGuid> objectGuidList, SearchCondition reportCondition, boolean isScript, boolean isMail)
			throws ServiceRequestException;

	/**
	 * EC报表导出
	 * 
	 * @param exportFileType
	 * @param guidListMap
	 * @param reportcondition
	 * @param isScript
	 * @param jobGuid
	 * @param isMail
	 * @throws ServiceRequestException
	 */
	public void reportGenericECHelp(ReportTypeEnum exportFileType, Map<String, List<String>> guidListMap, SearchCondition reportcondition, boolean isScript, String jobGuid,
			boolean isMail) throws ServiceRequestException;

	/**
	 * WBS报表
	 * 
	 * @param foundation
	 * @param foundationList
	 * @param exportFileType
	 * @throws ServiceRequestException
	 */
	public String reportWBS(FoundationObject foundation, List<FoundationObject> foundationList, ReportTypeEnum exportFileType) throws ServiceRequestException;

	/**
	 * 交付物报表
	 * 
	 * @param foundation
	 * @param foundationList
	 * @param structureList
	 * @param exportFileType
	 * @throws ServiceRequestException
	 */
	public String reportDeliverables(FoundationObject foundation, List<FoundationObject> foundationList, List<StructureObject> structureList, ReportTypeEnum exportFileType)
			throws ServiceRequestException;

	/**
	 * 导出BOM报表
	 * 
	 * @param bomViewObjectGuid
	 * @param bomRule
	 * @param level
	 *            导出层级
	 * @param bomSearchCondition
	 * @param exportFileType
	 *            导出文件类型
	 * @param bomReportName
	 *            bom模板定义的报表名称
	 * @param exportType
	 *            导出报表的方式：bomtree、bomlist、按bo、classification分组
	 * @param levelStyle
	 *            层级方式：bomtree类型的报表需要传递的参数 方式1：10,10.10 方式2：.10,..10
	 * @param groupStyle
	 *            分组方式：按bo、classification分组
	 * @param groupStyle
	 *            报表模板名称
	 * @throws ServiceRequestException
	 */
	public void reportGenericBOM(ObjectGuid bomViewObjectGuid, DataRule dataRule, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType,
			String bomReportName, String exportType, String levelStyle, String groupStyle, String bomReportTemplateName, String isExportAllLevel, List<String> summaryFiledName,
			String pagesize, String reportpath, List<String> classGuids, String jobGuid, boolean isContainRepf) throws ServiceRequestException;

	/**
	 * 预定义搜索报表
	 * 
	 * @param preSearchGuid
	 * @param searchParameters
	 *            搜索条件
	 * @param foundationGuid
	 *            导出数据guid
	 * @param exportFileType
	 *            导出报表类型：PDF、EXCEL等
	 * @throws ServiceRequestException
	 */
	public void reportGenericPreSearch(String preSearchGuid, List<Object> searchParameters, List<String> foundationGuid, ReportTypeEnum exportFileType, String jobGuid)
			throws ServiceRequestException;

	/**
	 * 创建导出bom报表队列
	 * 
	 * @param end1ObjectGuid
	 * @param viewName
	 * @param bomRule
	 * @param level
	 *            搜索层级
	 * @param bomSearchCondition
	 * @param exportFileType
	 *            导出报表文件类型
	 * @param bomReportName
	 *            报表名称
	 * @param exportType
	 *            导出报表方式：树形结构、列表结构、分组汇总
	 * @param levelStyle
	 *            树形结构报表的导出方式
	 * @param groupStyle
	 *            分组报表的分组字段
	 * @param bomScriptFileName
	 *            定制化java类名称
	 * @param bomReportTemplateName
	 *            bom报表模板名称
	 * @param isExportAllLevel
	 *            料件数量汇总：多阶还是底阶
	 * @param summaryFiledName
	 *            汇总报表的分组字段
	 * @param classGuids
	 *            树形结构不输出某个类下的end2、列表结构和分组结构不输出该类
	 * @param isContainRepf
	 *            是否包含取替代数据
	 * @throws ServiceRequestException
	 */
	public void createReportBOMJob(ObjectGuid end1ObjectGuid, String viewName, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType, String bomReportName,
			String exportType, String levelStyle, String groupStyle, String bomScriptFileName, String bomReportTemplateName, String isExportAllLevel,
			List<String> summaryFiledName, String pagesize, String reportPath, List<String> classGuids, boolean isContainRepf) throws ServiceRequestException;

	/**
	 * 创建预定义搜索报表队列
	 * 
	 * @param preSearchGuid
	 * @param searchParameters
	 * @param foundationGuid
	 * @param exportFileType
	 * @throws ServiceRequestException
	 */
	public void createReportGenericPreSearchJob(String preSearchGuid, List<String> searchParameters, List<String> foundationGuid, ReportTypeEnum exportFileType)
			throws ServiceRequestException;

	/**
	 * 获得bom定制化报表数据
	 * 
	 * @param bomViewObjectGuid
	 * @param bomRule
	 * @param level
	 * @param bomSearchCondition
	 * @param bomScriptFileName
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<String, Object> personalizedReportData(ObjectGuid bomViewObjectGuid, int level, SearchCondition bomSearchCondition, String bomScriptFileName,
			ReportTypeEnum exportFileType, String bomReportName, String exportType, String levelStyle, String groupStyle) throws ServiceRequestException;

	/**
	 * 生成bom定制化报表
	 * 
	 * @param data
	 * @param exportFileType
	 * @param bomReportTemplateName
	 * @param bomReportName
	 * @throws ServiceRequestException
	 */
	public void personalizedReport(Map<String, Object> data, String bomReportTemplateName, String bomReportName, ObjectGuid bomViewObjectGuid, int level,
			SearchCondition searchCondition, ReportTypeEnum exportFileType, String exportType, String levelStyle, String groupStyle, String jobGuid) throws ServiceRequestException;

	/**
	 * 创建导出产品资料清单报表队列
	 * 
	 * @param productObjectGuid
	 * @param searchCondition
	 * @param exportFileType
	 * @param bomReportTemplateName
	 * @param reportName
	 * @throws ServiceRequestException
	 */
	public void createReportGenericProductSummaryObjectJob(ObjectGuid productObjectGuid, SearchCondition searchCondition, String relationTemplateName,
			ReportTypeEnum exportFileType, String bomReportTemplateName, String reportName, String pagesize, String reportpath) throws ServiceRequestException;

	/**
	 * 导出产品资料清单报表
	 * 
	 * @param productObjectGuid
	 * @param searchCondition
	 * @param exportFileType
	 * @param bomReportTemplateName
	 * @param reportName
	 * @throws ServiceRequestException
	 */
	public void reportGenericProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String relationTemplateName, ReportTypeEnum exportFileType,
			String bomReportTemplateName, String reportName, String pagesize, String reportpath, String jobGuid) throws ServiceRequestException;

	@Deprecated
	public void createReportBOMJob(ObjectGuid bomViewObjectGuid, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType, String bomReportName,
			String exportType, String levelStyle, String groupStyle, String bomScriptFileName, String bomReportTemplateName) throws ServiceRequestException;

	/**
	 * 获取报表模板配置信息
	 * 
	 * @param type
	 *            哪种类型的配置报表 1:bom结构报表 2：bom汇总报表 3：资料清单报表 4：EC报表
	 * @return
	 */
	public List<Map<String, String>> getConfigList(String type);

	/**
	 * 导出EC报表
	 * 
	 * @param uiName
	 * @param ecnObjectGuid
	 * @param exportFileType
	 * @param reportCondition
	 * @throws ServiceRequestException
	 */
	// public void reportGenericEC(String uiName, ObjectGuid ecnObjectGuid,
	// ReportTypeEnum exportFileType,
	// SearchCondition reportCondition) throws ServiceRequestException;

	/**
	 * EC报表
	 * 
	 * @param ecrObjectGuid
	 * @param ecnObjectGuid
	 * @param exportFileType
	 * @param bomReportTemplateName
	 * @param reportName
	 * @param pagesize
	 * @param reportpath
	 * @throws ServiceRequestException
	 */
	// public void createReportGenericECObjectJob(String uiName, ObjectGuid
	// ecnObjectGuid, ReportTypeEnum
	// exportFileType, SearchCondition reportCondition)
	// throws ServiceRequestException;

	/**
	 * 一般实例报表
	 * 
	 * @param uiName
	 * @param exportFileType
	 * @param objectGuidList
	 * @param reportCondition
	 * @throws ServiceRequestException
	 */
	public void createReportGenericInstacnceObjectJob(String uiName, ReportTypeEnum exportFileType, List<ObjectGuid> objectGuidList, SearchCondition reportCondition)
			throws ServiceRequestException;

	public void reportGenericHelp(String className, String uiName, ReportTypeEnum exportFileType, List<String> objectGuidList, SearchCondition reportcondition, boolean isScript,
			String jobGuid, boolean isMail) throws ServiceRequestException;

	public void reportAdvancedSearchObjectJob(SearchCondition searchCondition) throws ServiceRequestException;

	public void reportAdvancedSearchObject(SearchCondition searchCondition, String jobGuid) throws ServiceRequestException;

	/**
	 * 把保存在数据库中的objectguid对象字符串转为objectguid对象。
	 * 
	 * @param str
	 * @return
	 */
	public ObjectGuid getObjectGuidByStr(String str);

	/**
	 * 把string类型的list对象转为string字符串
	 * 
	 * @param list
	 * @return
	 */
	public String buildListToString(List<String> list);

	/**
	 * 把string类型的字符串转回string类型的list
	 * 
	 * @param s
	 * @return
	 */
	public List<String> rebuildStrToList(String s);

	/**
	 * 生成订单物料报表导出队列任务
	 * 
	 * @param objectGuid
	 * @param exportFileType
	 * @param sc
	 * @throws ServiceRequestException
	 */
	public void reportConfigBOMJob(ObjectGuid objectGuid, ObjectGuid drawObjectGuid, DataRule dataRule, ReportTypeEnum exportFileType, SearchCondition sc, int level)
			throws ServiceRequestException;

	/**
	 * 订单物料报表导出
	 * 
	 * @param objectGuid
	 * @param exportFileType
	 * @param sc
	 * @throws ServiceRequestException
	 */
	public void reportConfigBOM(FoundationObject item, FoundationObject draw, ReportTypeEnum exportFileType, SearchCondition sc, String jobGuid, int level)
			throws ServiceRequestException;
}