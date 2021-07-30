/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOM Bill of Material 物料清单服务
 * caogc 2010-10-12
 */
package dyna.net.service.brs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.ECEffectedBOMRelation;
import dyna.common.dto.template.bom.BOMCompare;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * Bill of Material Service物料清单服务
 * 
 * @author caogc
 * 
 */
public interface BOMS extends Service
{

	/**
	 * 初始化BOMStructure实例<br>
	 * 该方法供前台获取初始的要创建的BOM结构关系的实例，主要提供模型中设置的字段的初始值<br>
	 * 参数中classGuid及className二者传其一即可，如果都传值，那么以classGuid为准<br>
	 * 返回的实例只供前台使用，数据库中并不存在
	 * 
	 * @param classGuid
	 * @param className
	 * @return BOMStructure
	 * @throws ServiceRequestException
	 */
	public BOMStructure newBOMStructure(String classGuid, String className) throws ServiceRequestException;

	/**
	 * 登录用户是否能够对指定的end1下的指定ID的BOMView进行编辑 <br>
	 * 如果对应的BOMView为非WIP状态，那么不允许编辑 ，返回值是false
	 * 
	 * @param end1Object
	 * @param name
	 *            BOMView的名字或者对应的BOM模板的名字
	 * @return true 能编辑,false 不能编辑
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常
	 */
	public boolean canEditBom(FoundationObject end1Object, String name) throws ServiceRequestException;

	/**
	 * 取消处于检出状态的BOMView <br>
	 * 
	 * @param bomView
	 *            要取消的检出状态的BOMView
	 * @return 取消检出后的BOMView
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public BOMView cancelCheckOut(BOMView bomView) throws ServiceRequestException;

	/**
	 * BOMView检入 <br>
	 * 
	 * @param bomView
	 *            要检入的bomView
	 * @return 检入后的bomView
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public BOMView checkIn(BOMView bomView) throws ServiceRequestException;

	/**
	 * BOMView检出 <br>
	 * 
	 * @param bomView
	 *            要检出的bomView
	 * @return 检出的bomView
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public BOMView checkOut(BOMView bomView) throws ServiceRequestException;

	/**
	 * 检出状态迁移 ——把一个BOMView的检出者由有权限用户迁移给另外一个人 <br>
	 * 
	 * @param bomView
	 *            要迁移的BOMView
	 * @param toUserGuid
	 *            要迁移给的用户
	 * @return 迁移后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public BOMView transferCheckout(BOMView bomView, String toUserGuid) throws ServiceRequestException;

	/**
	 * BOM比较 <br>
	 * 将给定的两个BOMView下的结构及结构上的end2进行比较,规则如下： <br>
	 * 顺序号不同则不同 <br>
	 * 找到顺序号一致的对象进行比较 <br>
	 * 根据ID判断两个对象是否同一对象 <br>
	 * 判断两个对象版本是否一致 <br>
	 * 判断引用数量是否一致 <br>
	 * 如果三者都为真并且 fieldChangedList为空 那么就是相同的BOM <br>
	 * 否则为不同的BOM
	 * 
	 * @param leftBOMViewObjectGuid
	 * @param leftBOMRule
	 * @param rightBOMViewObjectGuid
	 * @param rightBOMRule
	 * @return 比较后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常
	 */
	public List<BOMCompare> compareBOM(ObjectGuid leftBOMViewObjectGuid, DataRule leftDataRule, ObjectGuid rightBOMViewObjectGuid, DataRule rightDataRule)
			throws ServiceRequestException;

	/**
	 * 删除BOMView的处理
	 * 
	 * @param bomView
	 *            要删除的bomView对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteBOMView(BOMView bomView) throws ServiceRequestException;

	/**
	 * 批量保存BOM编辑 <br>
	 * 为了使顺序号不产生冲突，那么采用先更新，再移除，最后关联的顺序进行处理
	 * 
	 * 
	 * @param ecEffectedBOMRelationList
	 * @param procRtGuid
	 *            流程guid
	 * @param templateName
	 *            bom template name
	 * @return List<BOMStructure> 返回修改后或新增的BOMStructure
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMStructure> editBOM(BOMView itemView, List<ECEffectedBOMRelation> ecEffectedBOMRelationList, String procRtGuid, String templateName)
			throws ServiceRequestException;

	/**
	 * 批量保存BOM编辑 <br>
	 * 为了使顺序号不产生冲突，那么采用先更新，再移除，最后关联的顺序进行处理
	 * 
	 * 
	 * @param ecEffectedBOMRelationList
	 * @param procRtGuid
	 *            流程guid
	 * @param templateName
	 *            bom template name
	 * @return List<BOMStructure> 返回修改后或新增的BOMStructure
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMStructure> editBOM(BOMView itemView, List<ECEffectedBOMRelation> ecEffectedBOMRelationList, String procRtGuid, String templateName, boolean isCheckField)
			throws ServiceRequestException;

	/**
	 * 根据BOMStructure的guid取得 BOMStructure对象
	 * 
	 * @param bomGuid
	 * @param bomRule
	 * @param searchCondition
	 *            该参数为必传参数, 用createSearchConditionForStructure生成
	 *            其中Structure对应的className必传
	 * 
	 * @return BOMStructure
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public BOMStructure getBOM(ObjectGuid objectGuid, SearchCondition searchCondition, DataRule dataRule) throws ServiceRequestException;

	/**
	 * 根据关联关系的objectGuid获取BOMView对象
	 * 
	 * @param objectGuid
	 *            关联关系的objectGuid对象
	 * @return BOMView对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public BOMView getBOMView(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据END1及BOM模板的名字 查找对应的BOMView <br>
	 * 因为BOM模板的名字用来表示BOM的类别 所以每个END1下 以某个名字的模板创建的BOMView只能有一个存在
	 * 
	 * @param end1ObjectGuid
	 * @param viewName
	 *            BOM模板的名字 用来表示BOM的类别 如EBOM、MBOM等
	 * @return BOMView对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public BOMView getBOMViewByEND1(ObjectGuid end1ObjectGuid, String viewName) throws ServiceRequestException;

	/**
	 * 将一个对象关联到BOM上 <br>
	 * 直接调用数据层提供的方法
	 * 
	 * @param bomView
	 *            view对象的ObjectGuid
	 * @param end2FoundationObject
	 *            要关联的对象的ObjectGuid
	 * @param bomStructure
	 *            bom结构的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public BOMStructure linkBOM(ObjectGuid bomView, ObjectGuid end2FoundationObject, BOMStructure bomStructure) throws ServiceRequestException;

	/**
	 * 将end2对象直接关联到end1对象上，如果没有View存在，那么先用模板创建一个View然后再将end2对象挂上来
	 * 
	 * @param end1Object
	 * @param end2Object
	 * @param bomStructure
	 * @param viewName
	 *            BOM模板的名字
	 * @param isPrecise
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器的没有对应BOM模板异常(
	 *             ID_APP_NO_BOM_TEMPLATE)
	 */
	public BOMStructure linkBOM(ObjectGuid end1Object, ObjectGuid end2Object, BOMStructure bomStructure, String viewName, boolean isPrecise) throws ServiceRequestException;

	/**
	 * 根据View的guid查找BOM(单层)信息
	 * 
	 * @param viewObjectGuid
	 *            View的 objectguid
	 * @param bomRule
	 *            bom查询条件
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return BOMStructure列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<BOMStructure> listBOM(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException;

	/**
	 * 获取end1对象的上的所有BOM视图
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @return BOMView列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<BOMView> listBOMView(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 查找BOM(单层)信息 <br>
	 * 返回的是指定end1下的 指定类别的BOMView下的所有BOMStructure
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @param bomRule
	 *            bom查询条件
	 * @param viewName
	 *            bom分类
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return BOMStructure列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<BOMStructure> listBOM(ObjectGuid end1ObjectGuid, String templateName, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException;

	/**
	 * 获取已知对象被引用的对象列表,列表版本由DataRule条件控制<br>
	 * <string>被引用是在BOM结构中被关联<string>
	 * 
	 * @param objectGuid
	 *            已知对象objectGuid
	 * @param viewName
	 *            view的名字
	 * @param end1FieldList
	 *            需要返回的end1的字段，若为空，则返回默认字段
	 * @param dataRule
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listWhereUsed(ObjectGuid objectGuid, String viewName, SearchCondition end1SearchCondition, SearchCondition structureSearchCondition,
			DataRule dataRule) throws ServiceRequestException;

	/**
	 * 获取已知对象被引用的对象列表,列表版本由DataRule条件控制<br>
	 * <string>被引用是在BOM结构中被关联<string>
	 * 
	 * @param objectGuid
	 *            已知对象objectGuid
	 * @param viewName
	 *            view的名字
	 * @param end1FieldList
	 *            需要返回的end1的字段，若为空，则返回默认字段
	 * @param dataRule
	 * @param viewHistory
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listWhereUsed(ObjectGuid objectGuid, String viewName, SearchCondition end1SearchCondition, SearchCondition structureSearchCondition,
			DataRule dataRule, boolean viewHistory) throws ServiceRequestException;

	/**
	 * 更新BOMStructure对象
	 * 
	 * @param bomStructure
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 * 
	 * @return 更新后的BOMStructure
	 */
	public BOMStructure saveBOM(BOMStructure bomStructure) throws ServiceRequestException;

	/**
	 * 更新BOMStructure对象
	 * 
	 * @param bomStructure
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 * 
	 * @return 更新后的BOMStructure
	 */
	public BOMStructure saveBOM(BOMStructure bomStructure, boolean isCheckField) throws ServiceRequestException;

	/**
	 * 更新BOMStructure对象
	 * 
	 * @param bomStructure
	 * @param isCheckField
	 * @param isCheckConnect
	 *            检查循环
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 * 
	 * @return 更新后的BOMStructure
	 */
	public BOMStructure saveBOM(BOMStructure bomStructure, boolean isCheckField, boolean isCheckConnect) throws ServiceRequestException;

	/**
	 * 更新BOMStructure对象
	 * 
	 * @param bomView
	 * @param procRtGuid
	 *            流程guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public BOMView saveBOMView(BOMView bomView, String procRtGuid) throws ServiceRequestException;

	/**
	 * 更新一个BOM关联关系对象 <br>
	 * 不支持更新精确非精确属性，因为精确非精确属性修改时需要做一些特殊处理<br>
	 * 非WIP状态的BOM视图不能更新
	 * 
	 * @param bomView
	 *            BOM关联关系的对象
	 * @return 保存后的bomView对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public BOMView saveBOMView(BOMView bomView) throws ServiceRequestException;

	/**
	 * 根据BOM模板创建 一个BOM关联关系对象<br>
	 * 一个BOM模板在一个对象上只能创建一个BOMView,如果重复创建报异常<br>
	 * 创建BOMView的时候会把模板的ID与NAME赋值给BOMView做ID和NAME
	 * 
	 * @param bomTemplateGuid
	 *            BOM关联关系模板的guid
	 * @param isPrecise
	 *            是否精确
	 * @param end1ObjectGuid
	 *            要关联的对象的ObjectGuid
	 * @return 刚创建的BOMView对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器用同一模板重复创建的异常(
	 *             ID_APP_MULTI_CREATE_RELATION)
	 */
	public BOMView createBOMViewByBOMTemplate(String bomTemplateGuid, ObjectGuid end1ObjectGuid, boolean isPrecise) throws ServiceRequestException;

	/**
	 * 根据BOMStructure解除关联<br>
	 * 
	 * 即:删除该结构
	 * 
	 * @param bomStructure
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlinkBOM(BOMStructure bomStructure) throws ServiceRequestException;

	/**
	 * 修改BOMView的精确非精确字段 <br>
	 * 直接调用数据层提供的方法
	 * 
	 * @param bomView
	 * @return 更新后的bomView
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public BOMView updateBOMViewPrecise(BOMView bomView) throws ServiceRequestException;

	/**
	 * 根据指定的bom rule和过滤条件查询BOM结构(仅供树状显示的时候使用)
	 * 
	 * @param viewObjectGuid
	 * @param bomRule
	 * @param level
	 *            显示层数1、2、3……，如果查询所有层则为-1
	 * @param searchCondition
	 *            这个是放要返回的字段以及装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以
	 *            用SearchConditionFactory中的createSearchConditionForBOMStructure构造检索条件
	 * @return
	 * @throws DynaDataException
	 */
	public Map<String, List<BOMStructure>> listBOMForTree(ObjectGuid viewObjectGuid, int level, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException;

	/**
	 * 根据指定的bom rule、过滤条件以及排序规则查询BOM结构(仅供List方式展示的时候使用)
	 * 
	 * @param viewObjectGuid
	 * @param bomRule
	 * @param level
	 *            显示层数1、2、3……，如果查询所有层则为-1
	 * @param searchCondition
	 *            这个是放要返回的字段以及装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以
	 *            用SearchConditionFactory中的createSearchConditionForBOMStructure构造检索条件
	 * @return
	 * @throws DynaDataException
	 */
	public List<BOMStructure> listBOMForList(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException;

	/**
	 * 根据end1的ObjectGuid与BOMView的名字删除该View下的所有BOMStructure（仅当前层）
	 * 
	 * @param end1ObjectGuid
	 * @param viewName
	 * @throws ServiceRequestException
	 */
	public void unLinkAllBOMStructure(ObjectGuid end1ObjectGuid, String viewName) throws ServiceRequestException;

	/**
	 * 获取已知bomView对象上关联的所有结构（仅供前台显示表格样式时使用）<br>
	 * 把结构信息放入对应的end2实例内并返回,key值为StructureObject.STRUCTURE_CLASS_NAME
	 * 
	 * @param bomViewObjectGuid
	 *            bomView对象的ObjectGuid
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的StructureObject对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> listFoundationObjectOfBOMView(ObjectGuid bomViewObjectGuid, SearchCondition searchCondition, DataRule dataRule) throws ServiceRequestException;

	/**
	 * 根据end2进行BOM比较 <br>
	 * 将给定的两个BOMView下的结构及结构上的end2进行比较,规则如下： <br>
	 * 先将比较双方按对象进行汇总（非精确，就按Master汇总，精确就按ItemRevision汇总 <br>
	 * 按Revision找到一致的对象进行比较 <br>
	 * 判断引用数量是否一致 <br>
	 * 如果数量为真并且 fieldChangedList为空 那么就是相同的BOM <br>
	 * 否则为不同的BOM
	 * 
	 * @param leftBOMViewObjectGuid
	 * @param leftBOMRule
	 * @param rightBOMViewObjectGuid
	 * @param rightBOMRule
	 * @return 比较后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常
	 */
	public List<BOMCompare> compareBOMByObject(ObjectGuid leftBOMViewObjectGuid, DataRule leftDataRule, ObjectGuid rightBOMViewObjectGuid, DataRule rightDataRule)
			throws ServiceRequestException;

	/**
	 * 从CAD创建BOM
	 * 
	 * @param cad图纸
	 * @param bomTemplate
	 * @param 是否忽略压缩抑制
	 * @throws ServiceRequestException
	 */
	public boolean cadToBom(FoundationObject cad, String bomTemplateGuid, boolean isRoot, FoundationObject end1, boolean isYS) throws ServiceRequestException;

	/**
	 * 更新bomview 的所有者
	 * 
	 * @param objectGuid
	 * @param ownerUserGuid
	 * @param ownerGroupGuid
	 * @param updateTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOMView updateBomViewOwner(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime) throws ServiceRequestException;

	/**
	 * 根据bomview批量查询对应的end1，不判断权限
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> batchListEnd1OfBOMView(List<BOMView> bomViewList) throws ServiceRequestException;

	/**
	 * 把模型结构转换为BOM结构
	 * 
	 * @param end1ObjectGuid
	 * @param bomTemplateName
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void drawTransferToBOM(ObjectGuid end1ObjectGuid, String bomTemplateName, String procRtGuid) throws ServiceRequestException;

}