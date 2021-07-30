package dyna.net.service.brs;

import java.util.Date;
import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigVariable;
import dyna.common.bean.data.configparamter.DetailPositionEnum;
import dyna.common.bean.data.configparamter.DocumentMark;
import dyna.common.bean.data.configparamter.DrivenResult;
import dyna.common.bean.data.configparamter.DynamicColumnTitle;
import dyna.common.bean.data.configparamter.DynamicOfColumn;
import dyna.common.bean.data.configparamter.TableOfExpression;
import dyna.common.bean.data.configparamter.TableOfGroup;
import dyna.common.bean.data.configparamter.TableOfInputVariable;
import dyna.common.bean.data.configparamter.TableOfList;
import dyna.common.bean.data.configparamter.TableOfMark;
import dyna.common.bean.data.configparamter.TableOfMultiCondition;
import dyna.common.bean.data.configparamter.TableOfParameter;
import dyna.common.bean.data.configparamter.TableOfRegion;
import dyna.common.bean.data.configparamter.TestHistory;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ConfigParameterTableType;
import dyna.net.service.Service;

/**
 * Configuration Parameter BOM
 * 
 * 配置参数生成BOM
 * 
 * @author wwx
 * 
 */
public interface CPB extends Service
{
	/**
	 * 通过表类型取得动态列头
	 * 
	 * @param masterGuid
	 *            end1 masterguid
	 * @param tableTypeEnum
	 *            表类型
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DynamicColumnTitle> listColumnTitles(ObjectGuid objectGuid, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException;

	/**
	 * 保存G表数据
	 * 
	 * @param end1ObjectGuid
	 *            配置对象
	 * @param ruleTime
	 *            对象发布时间
	 * @param columnTitleList
	 *            修改的和新加的列头
	 * @param groupList
	 *            修改的G表数据
	 * @param deleteListList
	 *            删除的行
	 * @param deleteColumnTitleList
	 *            删除的列
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfGroup> saveTableOfGroup(ObjectGuid end1ObjectGuid, Date ruleTime, List<DynamicColumnTitle> columnTitleList, List<TableOfGroup> groupList,
			List<TableOfGroup> deleteListList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException;

	/**
	 * 保存L表数据
	 * 
	 * @param end1ObjectGuid
	 *            配置对象
	 * @param ruleTime
	 *            对象发布时间
	 * @param tableType
	 *            表La或者Lb
	 * @param columnTitleList
	 *            修改的和新加的列头
	 * @param tableOfListList
	 *            修改的La或者Lb表数据
	 * @param deleteLineList
	 *            删除的行
	 * @param deleteColumnTitleList
	 *            删除的列
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfList> saveTableOfList(ObjectGuid end1ObjectGuid, Date ruleTime, ConfigParameterTableType tableType, List<DynamicColumnTitle> columnTitleList,
			List<TableOfList> tableOfListList, List<TableOfList> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException;

	/**
	 * 保存公式表数据
	 * 
	 * @param tableOfExpressionList
	 *            table中的所有有效数据
	 * @param deleteColumnList
	 *            要删除的行或者列数据
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfExpression> saveTableOfExpression(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfExpression> tableOfExpressionList,
			List<TableOfExpression> deleteColumnList) throws ServiceRequestException;

	/**
	 * 保存输入变量表
	 * 
	 * @param tableOfInputVariableList
	 *            table中的所有有效数据
	 * @param deleteColumnList
	 *            要删除的行或者列数据
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfInputVariable> saveTableOfInputVariable(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfInputVariable> tableOfInputVariableList,
			List<TableOfInputVariable> deleteColumnList) throws ServiceRequestException;

	/**
	 * 保存Mark表数据
	 * 
	 * @param tableOfMarkList
	 *            table中的所有有效数据
	 * @param deleteColumnList
	 *            要删除的行或者列数据
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfMark> saveTableOfMark(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfMark> tableOfMarkList, List<TableOfMark> deleteColumnList)
			throws ServiceRequestException;

	/**
	 * 保存P表数据
	 * 
	 * @param end1ObjectGuid
	 * @param ruleTime
	 * @param columnTitleList
	 *            新增的或修改的列头
	 * @param tableOfParameterList
	 *            新增的或修改的P表数据
	 * @param deleteLineList
	 *            删除的行
	 * @param deleteColumnTitleList
	 *            删除的列
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfParameter> saveTableOfParameter(ObjectGuid end1ObjectGuid, Date ruleTime, List<DynamicColumnTitle> columnTitleList,
			List<TableOfParameter> tableOfParameterList, List<TableOfParameter> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException;

	/**
	 * 保存A-E,RQ表数据
	 * 
	 * @param end1ObjectGuid
	 * @param ruleTime
	 * @param tableType
	 *            表类型
	 * @param columnTitleList
	 *            新增或修改的列头
	 * @param tableOfRegionList
	 *            新增或修改的条件表数据
	 * @param deleteLineList
	 *            要删除的行
	 * @param deleteColumnTitleList
	 *            删除列
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfRegion> saveTableOfRegion(ObjectGuid end1ObjectGuid, Date ruleTime, ConfigParameterTableType tableType, List<DynamicColumnTitle> columnTitleList,
			List<TableOfRegion> tableOfRegionList, List<TableOfRegion> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException;

	/**
	 * 取得La或者Lb表的数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @param tableTypeEnum
	 *            La或者Lb类型
	 * @param ruleTime
	 *            end1发布时间
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfList> listTableOfListData(ObjectGuid objectGuid, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得范围条件表数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @param tableTypeEnum
	 *            A-E,R,Q
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfRegion> listTableOfRegion(ObjectGuid objectGuid, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得公式表数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfExpression> listTableOfExpression(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得参数表数据
	 * 
	 * @param masterGuid
	 *            end1 masterguid
	 * @param end1ReleaseTime
	 *            end1发布时间
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfParameter> listTableOfParameterData(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得G番号对应的参数表数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @param gNumber
	 *            G番号，可以为空
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfParameter> listTableOfParameter(ObjectGuid objectGuid, String gNumber, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得MAK表数据
	 * 
	 * @param masterGuid
	 *            end1 masterguid
	 * @param end1ReleaseTime
	 *            end1发布时间
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfMark> listTableOfMarkData(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得输入变量表数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @param ruleTime
	 *            end1发布时间
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfInputVariable> listTableOfInputVariable(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得所有的L号，仅仅取得L号，不包含L表配置数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfList> listAllList(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取得所有G表数据
	 * 
	 * @param objectGuid
	 *            end1 objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfGroup> listTableOfGroup(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 配置规则驱动生成当前对象的单层结构（驱动测试专用）
	 * 
	 * @param end1
	 * @param strucSearchCondition
	 *            材料明细的结构查询条件对象
	 * @param end2SearchCondition
	 *            材料明细的end2查询条件对象
	 * @param dataRule
	 *            材料明细的end2查询规则
	 * @param gNumber
	 *            G番号
	 * @param lNumbers
	 *            L号分组序列 所有的L番号连接为字符串,如L01L02L03
	 * @param inptVarriables
	 *            输入变量键值对，以";"号分割，如：A=G01;B=G02，输入变量的值不能包含";"和"="号
	 * @return
	 * @throws ServiceRequestException
	 */
	public DrivenResult drivenTestByConfigRules(FoundationObject end1, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException;

	/**
	 * 取得默认的L00番号
	 * 
	 * @param objectGuid
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public TableOfList getDefaultL00Number(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 保存驱动测试条件历史
	 * 
	 * @param testHistory
	 * @return
	 * @throws ServiceRequestException
	 */
	public void saveTestHistory(TestHistory testHistory) throws ServiceRequestException;

	/**
	 * 取得驱动测试参数输入历史值
	 * 
	 * @param history
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TestHistory> listTestHistory(TestHistory history) throws ServiceRequestException;

	/**
	 * 取得驱动测试参数输入历史值
	 * 
	 * @param history
	 * @return
	 * @throws ServiceRequestException
	 */
	public TestHistory getTestHistory(TestHistory history) throws ServiceRequestException;

	/**
	 * 删除驱动测试条件历史
	 * 
	 * @param history
	 * @throws ServiceRequestException
	 */
	public void deleteTestHistory(TestHistory history) throws ServiceRequestException;

	/**
	 * 检查所有配置表数据以及材料明细的配置相关数据的正确性(只判断变量输入是否正确，以及变量是否存在，不判断变量计算是否正确)
	 * 
	 * @param end1ObjectGuid
	 * @param dataRule
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> checkAllValuesOfTabs(ObjectGuid end1ObjectGuid, DataRule dataRule) throws ServiceRequestException;

	/**
	 * 取得需要查询的BOName
	 * 
	 * @return
	 */
	public List<BOInfo> listOrderBoinfo() throws ServiceRequestException;

	/**
	 * 保存查询的BO
	 * 
	 * @param value
	 */
	public void saveOrderBoinfo(List<BOInfo> value) throws ServiceRequestException;

	/**
	 * 复制配置表数据
	 * 
	 * @param destObjectGuid
	 * @param origObjectGuid
	 * @param ruleTime
	 * @param tableTypeList
	 * @throws ServiceRequestException
	 */
	public void copyConfigData(ObjectGuid destObjectGuid, ObjectGuid origObjectGuid, Date ruleTime, List<ConfigParameterTableType> tableTypeList) throws ServiceRequestException;

	/**
	 * 对象是否能复制配置表
	 * 
	 * @param masterGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isConfigTableCanBeCopy(String masterGuid) throws ServiceRequestException;

	/**
	 * 根据输入的L番号序列，取得所有L番号对应的图面变量的值
	 * 
	 * @param objectGuid
	 * @param ruleTime
	 * @param lNumbers
	 * @return
	 */
	public List<DynamicOfColumn> listAllVariableOfLTable(ObjectGuid objectGuid, Date ruleTime, String lNumbers) throws ServiceRequestException;

	/**
	 * 检查所有变量配置,包含子阶配置(结构上的配置,不包含子阶配置表)
	 * 
	 * @param end1
	 * @param dataRule
	 * @param end2StrucList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> checkAllValuesOfTabs(FoundationObject end1, DataRule dataRule, List<StructureObject> end2StrucList, ConfigVariable configVariable)
			throws ServiceRequestException;
	
	/**
	 * 取得关联关系结构，不进行decorate操作
	 * 
	 * @param end1ObjectGuid
	 * @param dataRule
	 * @param gNumberList
	 * @param strucSearchCondition
	 * @param end2SearchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> listStructureObject(ObjectGuid end1ObjectGuid, DataRule dataRule, String viewName, SearchCondition strucSearchCondition,
			SearchCondition end2SearchCondition) throws ServiceRequestException;

	/**
	 * 取得单阶材料明晰
	 * 
	 * @param end1ObjectGuid
	 * @param dataRule
	 * @param gNumberList
	 * @param strucSearchCondition
	 * @param end2SearchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> listStructureObject(ObjectGuid end1ObjectGuid, DataRule dataRule, List<String> gNumberList, SearchCondition strucSearchCondition,
			SearchCondition end2SearchCondition) throws ServiceRequestException;
	
	/**
	 * 批量删除结构
	 * 
	 * @param viewObjectGuid
	 * @throws ServiceRequestException
	 */
	public void deleteRelation(ObjectGuid viewObjectGuid) throws ServiceRequestException;

	/**
	 * 订单明细--图号检查，参数检查,订单参数映射为内部参数，物料检查
	 * 
	 * @param objectGuid
	 *            订单合同
	 * @param allCheck
	 *            true:检查所有明细; false:检查未通过检查的明细
	 * @return
	 *         返回值为空，则检查通过;返回值不空,则订单内容所对应的配置有错
	 * @throws ServiceRequestException
	 */
	public List<List<String>> check4Order(ObjectGuid objectGuid, DataRule dataRule, boolean allCheck) throws ServiceRequestException;

	/**
	 * 订单BOM--驱动结果显示
	 * 
	 * @param gNumber
	 *            父阶G番号
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> driveResult4Order(FoundationObject item, FoundationObject draw, DataRule dataRule, SearchCondition itemSearchCondition)
			throws ServiceRequestException;

	/**
	 * 保存驱动测试结果(创建任务job),必须在驱动测试成功之后才能调用.
	 * 
	 * @param end1ObjectGuid
	 * @param strucSearchCondition
	 * @param end2SearchCondition
	 * @param dataRule
	 * @param origGNumber
	 * @param gNumber
	 * @param lNumbers
	 * @param inptVarriables
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveDrivenTestResult(ObjectGuid end1ObjectGuid, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException;

	/**
	 * 根据订单内容取得对应最新版本图纸对象
	 * 
	 * @param drawNo
	 *            图号
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getDrawInstanceByOrderDetail(String drawNo, Date ruleTime) throws ServiceRequestException;

	/**
	 * 根据物料取得图纸
	 * 
	 * @param item
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getDrawInstanceByItem(FoundationObject item, Date ruleTime) throws ServiceRequestException;

	/**
	 * 不检出忽略更新时间保存
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 根据structure解除关联关系 <br>
	 * 即：删除structureObject对象
	 * 
	 * @param structureObject
	 * @throws ServiceRequestException
	 */
	public void unlink(StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 取得单阶BOM结构（ERP用）
	 * 
	 * @param item
	 * @param searchCondition
	 * @param dataRule
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOMStructure> listBOM(FoundationObject item, SearchCondition searchCondition, DataRule dataRule, String origGNumber) throws ServiceRequestException;

	/**
	 * 根据时间取得图纸对象
	 * 
	 * @param objectGuid
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getDrawing(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;
	
	/**
	 * 删除对象的所有配置数据
	 * 
	 * @param masterGuid
	 * @throws ServiceRequestException
	 */
	public void deleteAllConfig(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 取得对象的所有配置数据
	 * 
	 * @param instance
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public ConfigVariable buildConfigVariable(FoundationObject instance, Date ruleTime) throws ServiceRequestException;

	/**
	 * 建立关联关系,不检查是否循环
	 * 
	 * @param viewObject
	 * @param end1FoundationObject
	 * @param end2FoundationObjectGuid
	 * @param structureObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public StructureObject linkNoCheckCycle(ViewObject viewObject, FoundationObject end1FoundationObject, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject)
			throws ServiceRequestException;

	/**
	 * 第归驱动
	 * 
	 * @param end1
	 * @param strucSearchCondition
	 * @param end2SearchCondition
	 * @param dataRule
	 * @param origGNumber
	 * @param gNumber
	 * @param lNumbers
	 * @param inptVarriables
	 * @return
	 * @throws ServiceRequestException
	 */
	public DrivenResult drivenTestByConfigRulesAll(FoundationObject end1, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException;

	/**
	 * 保存M表数据
	 * 
	 * @param end1ObjectGuid
	 * @param ruleTime
	 * @param tableOfMultiConditionList
	 * @param deleteLineList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfMultiCondition> saveTableOfMultiVariable(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfMultiCondition> tableOfMultiConditionList,
			List<TableOfMultiCondition> deleteLineList) throws ServiceRequestException;

	/**
	 * 取得多变量表数据
	 * 
	 * @param objectGuid
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TableOfMultiCondition> listTableOfMultiVariable(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;


	public int getResultVariableNumber() throws ServiceRequestException;

	/**
	 * 取得材料明细位置
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public DetailPositionEnum getDetailPosition() throws ServiceRequestException;

	/**
	 * 取得导出配置图纸的配置file
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public DocumentMark getExportConfigFile() throws ServiceRequestException;

	/**
	 * 删除对象的指定配置数据
	 * 
	 * @param masterGuid
	 * @throws ServiceRequestException
	 */
	public void deleteConfigByParam(ObjectGuid objectGuid, List<ConfigParameterTableType> tableTypes) throws ServiceRequestException;

	/**
	 * 取得G番号对应的材料明细结构字段
	 * 
	 * @param instance
	 * @param fieldNameList
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getClassFieldOfG(FoundationObject instance, List<String> fieldNameList, Date ruleTime) throws ServiceRequestException;

	
	/**
	 * 保存订单参数（手工输入订单参数用）
	 * 
	 * @param contract
	 * @param orderObjectGuid
	 * @param gNumber
	 * @param lNumbers
	 * @param inputVariables
	 */
	public void saveOrderPara(FoundationObject contract, ObjectGuid orderObjectGuid, String gNumber, String lNumbers, String inputVariables) throws ServiceRequestException;
}
