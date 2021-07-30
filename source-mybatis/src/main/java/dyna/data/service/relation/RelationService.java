package dyna.data.service.relation;

import java.util.List;
import java.util.Map;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.template.bom.BOMReportTemplate;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

public interface RelationService extends Service
{
	/**
	 * 将bomView由精确转为非精确，或将bomView由非精确转为精确
	 * 1、已发布、废弃状态的数据不能转换
	 * 2、不是自己检出的bomview，不能转换
	 * 3、在工作流中，不能转换
	 * 3、将bomview的isprecise置为精确/非精确
	 * 4、精确到非精确的转换，将bomview对应的bomstructure的end2清空
	 * 5、非精确到精确的转换，将bomview对应的bomstructure的end2设置为end2所有版本中非废弃版本的最新版
	 *
	 * @param bomView
	 *            bomView信息，不能为空，必须包含
	 *            1、objectGuid
	 *            2、精确非精确信息
	 * @param isCheckAcl
	 *            预留，暂未用到
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	void convertPrecise(BOMView bomView, String bomTemplateGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 在修订或者另存时，拷贝BOM或者关系结构。
	 *
	 * @param origViewGuid
	 *            原始end1的BOM或者关系
	 * @param destViewGuid
	 *            目标end1的BOM或者关系
	 * @param fotype
	 *            BOMView:3; ViewObject:5
	 * @param specialField
	 *            要变更的结构字段和值
	 * @param sessionId
	 *            当前用户sessionid
	 * @throws ServiceRequestException
	 */
	void copyBomOrRelation(String viewClassGuid, String origViewGuid, String destViewGuid, String structureClassGuid, String fotype, Map<String, Object> specialField,
			String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 创建StructureObject
	 * 在需要判断权限的时候，如果没有link权限，不能添加end2
	 * 1、系统字段的处理
	 * VIEWFK,CLASSGUID,END2CLASSGUID,END2MASTERGUID直接存入数据库
	 * END2，如果是精确的，直接存入数据库；如果是非精确的，为NULL
	 * CREATETIME、UPDATETIME固定为当前时间,CREATEUSER、UPDATEUSER固定为当前操作者
	 *
	 * 2、内建字段（如果有）、扩展字段（如果有）的处理
	 * 2.1、如果值为空，直接将空存入数据库
	 * 2.2、DATE、SDFYMDHMS类型，格式化为YYYY-MM-DD hh24:mi:ss后存入数据库。
	 * 2.3、OBJECT，取得guid、masterguid、classguid后存入数据库。
	 * 2.4、其他类型，直接存入数据库。
	 * 2.5、将数据库连接符单引号（'）转换成双单引号（''）。
	 *
	 * 3、不能关联自己
	 * 4、整个structureObject结构不能出现循环
	 * 5、工作流中的数据不能创建structureObject结构
	 * 6、end1下不能有相同的end2的master，不能有相同的end2的revision
	 *
	 * @param structureObject
	 *            结构信息
	 *            不能为空，必须包含的信息：
	 *            1、ObjectGuid
	 *            2、View的ObjectGuid
	 *            3、End2的ObjectGuid
	 * @param isCheckAcl
	 *            是否校验权限，不能为空
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @return
	 * 		创建后的structureObject的guid
	 * @throws ServiceRequestException
	 */
	StructureObject create(StructureObject structureObject, boolean isPrecise, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 删除StructureObject/BOMStructure
	 * 在需要判断权限的时候，如果没有unlink和link权限，不能删除
	 * 1、如果structure对应的view在工作流中，不能删除structure
	 * 2、如果是删除BOMStructure，不是对应view的检出者不能删除。
	 *
	 * @param structureObject
	 *            结构信息
	 *            必须包含的信息
	 *            1、ObjectGuid
	 *            2、View的ObjectGuid
	 *            3、name（抛异常时用来定位structure）
	 * @param isCheckAcl
	 *            是否校验权限（预留，暂未用到）
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	void delete(StructureObject structureObject, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 根据view删除所有的结构数据
	 *
	 * @param className
	 *            结构类
	 * @param viewObjectGuid
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	void deleteAllStruc(String className, ObjectGuid end1ObjectGuid, ObjectGuid viewObjectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 建立BOM关联
	 * 在需要判断权限的时候，如果没有link权限，不能添加end2
	 * 1、系统字段的处理
	 * VIEWFK,CLASSGUID,END2CLASSGUID,END2MASTERGUID直接存入数据库
	 * END2，如果是精确的，直接存入数据库；如果是非精确的，为NULL
	 * CREATETIME、UPDATETIME固定为当前时间,CREATEUSER、UPDATEUSER固定为当前操作者
	 *
	 * 2、内建字段（如果有）、扩展字段（如果有）的处理
	 * 2.1、如果值为空，直接将空存入数据库
	 * 2.2、DATE、SDFYMDHMS类型，格式化为YYYY-MM-DD hh24:mi:ss后存入数据库。
	 * 2.3、OBJECT，取得guid、masterguid、classguid后存入数据库。
	 * 2.4、其他类型，直接存入数据库。
	 * 2.5、将数据库连接符单引号（'）转换成双单引号（''）。
	 *
	 * 3、不能关联自己
	 * 4、整个结构不能出现循环
	 *
	 * @param viewObjectGuid
	 *            view的基本信息，不能为空，必须包含guid
	 * @param childObjectGuid
	 *            end2的基本信息，不能为空，必须包含guid、masterguid、classguid
	 * @param bomStructure
	 *            bom结构信息，不能为空，必须包含ObjectGuid
	 * @param isCheckAcl
	 *            不能为空，是否判断权限
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @param isCheckConnectBy
	 *            是否判断循环关联，另存为时不做判断
	 * @return
	 * 		结构改变后的BOMStructure
	 * @throws ServiceRequestException
	 */
	BOMStructure linkBOM(ObjectGuid viewObjectGuid, ObjectGuid childObjectGuid, BOMStructure bomStructure, boolean isPrecise, boolean isCheckAcl, String sessionId,
			String fixTranId) throws ServiceRequestException;

	/**
	 * 获取用到view的数据结构
	 *
	 * @param viewObjectGuid
	 *            end1基本信息，不能为空，必须包含guid
	 * @param isCheckAcl
	 *            不能为空，是否校验权限
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @return
	 * 		StructureObject结果集
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> listSimpleStructureOfRelation(ObjectGuid viewObjectGuid, String viewClassNameOrGuid, String struClassGuid, String sessionId)
			throws ServiceRequestException;

	/**
	 * 获取用到Structure的数据结构
	 *
	 * @param end1ObjectGuid
	 *            end1基本信息，不能为空，必须包含guid
	 * @param isCheckAcl
	 *            不能为空，是否校验权限
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @param sessionId2
	 * @param searchCondition
	 *            可以为空，包含需要查询的属性、过滤条件
	 * @return
	 * 		StructureObject结果集
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> listSimpleStructureOfEnd1(ObjectGuid end1ObjectGuid, String templateId, String viewClassNameOrGuid, String struClassGuid, String sessionId)
			throws ServiceRequestException;

	/**
	 * 获取用到view的数据结构，包含view和end2的信息
	 *
	 * @param viewObjectGuid
	 *            view基本信息，不能为空，必须包含guid
	 * @param isCheckAcl
	 *            不能为空，是否校验权限
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @param searchCondition
	 *            可以为空，包含需要查询的属性、过滤条件
	 * @return
	 * 		StructureObject结果集
	 * @throws ServiceRequestException
	 */
	List<StructureObject> listObjectOfRelation(ObjectGuid viewObjectGuid, String relationTemplateGuid, DataRule dataRule, boolean isCheckAcl, String sessionId,
			SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 获取关于relation的被关联的StructureObject数据集（由end2获取end1）
	 *
	 * @param objectGuid
	 *            实例基本信息，不能为空，必须包含guid、masterguid
	 * @param isCheckAcl
	 *            不能为空，是否校验权限
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @param viewName
	 *            view的名字，不能为空
	 * @return
	 * 		实例结果集
	 * @throws ServiceRequestException
	 */
	List<FoundationObject> listWhereUsed(ObjectGuid objectGuid, String templateName, String viewClassName, boolean isPrice, SearchCondition end1SearchCondition,
			SearchCondition struSearchCondition, boolean isViewHistory, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 根据BOMStructure的GUID取得BOMStructure对象
	 *
	 * @param bomStructureGuid
	 *            bomStructure的guid
	 * @param bomRule
	 *            查询bom的规则
	 * @param searchCondition
	 *            查询条件
	 * @param isCheckAcl
	 *            是否校验权限，不能为空
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @return
	 * 		BOMStructure信息
	 * @throws ServiceRequestException
	 */
	BOMStructure getBOMStructure(ObjectGuid objectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	StructureObject getStructureObject(ObjectGuid objectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 修改StructureObject/BOMStructure
	 * 在需要判断权限的时候，如果没有unlink和link权限，不能修改
	 * 1、系统字段的处理
	 * UPDATETIME固定为当前时间,UPDATEUSER固定为当前操作者
	 * 2、内建字段（如果有）、扩展字段（如果有）的处理
	 * 2.1、如果值为空，直接将空存入数据库
	 * 2.2、DATE、SDFYMDHMS类型，格式化为YYYY-MM-DD hh24:mi:ss后存入数据库。
	 * 2.3、OBJECT，取得guid、masterguid、classguid后存入数据库。
	 * 2.4、其他类型，直接存入数据库。
	 * 2.5、将数据库连接符单引号（'）转换成双单引号（''）。
	 * 3、StructureObject/BOMStructure已经被他人修改，不能修改。
	 * 4、如果是修改BOMStructure，不是BOMStructure对应view的检出者，不能修改。
	 *
	 * @param structureObject
	 *            不能为空，必须包含的信息：
	 *            1、name（抛异常时，用来定位structure）
	 *            2、更新时间
	 *            3、ObjectGuid
	 *            4、structureObject.getViewObjectGuid() 不能为空
	 * @param isCheckAcl
	 *            是否校验权限
	 * @param isCheckConnect
	 *            是否校验循环
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	StructureObject save(StructureObject structureObject, boolean isPrecise, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 查询产品中Summary数据
	 *
	 * @param itemGuid
	 *            产品guid
	 * @param searchCondition
	 *            查询条件
	 * @param sessionId
	 *            当前操作者的sessionid，不能为空
	 * @param isCheckAcl
	 *            是否判断权限，不能为空
	 * @return
	 * 		实例结果集
	 * @throws ServiceRequestException
	 */
	List<FoundationObject> getSummaryData(ObjectGuid itemObjectGuid, SearchCondition searchCondition, DataRule dataRule, List<String> relationTemplateList, String sessionId,
			boolean isCheckAcl, String fixTranId) throws ServiceRequestException;

	/**
	 * 根据BOM取得其结构对象,只取单层
	 *
	 * @param viewObjectGuid
	 * @param searchCondition
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	List<BOMStructure> listBOMStructure(ObjectGuid viewObjectGuid, String bomTemplateGuid, DataRule dataRule, SearchCondition searchCondition, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException;

	List<FoundationObject> listEnd2OfView(ObjectGuid viewObjectGuid, ObjectGuid end2ObjectGuid, String bomTemplateGuid) throws ServiceRequestException;

	/**
	 * 查询指定view下有多少个end2
	 *
	 * @param viewGuid
	 * @param relationTemplate
	 * @return
	 */
	int getEnd2CountOfView(String viewGuid, String relationTemplateGuid) throws ServiceRequestException;

	/**
	 * 获取废弃时的备用对象
	 *
	 * @param objectGuid
	 * @param viewClass
	 * @param strucClass
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	List<FoundationObject> getObsoleteWhereUsed(ObjectGuid objectGuid, String templateName, String viewClassName, boolean isPrice, SearchCondition end1SearchCondition,
			SearchCondition struSearchCondition, String sessionId) throws ServiceRequestException;

	/**
	 * 异步单线程更新end1对象的uhasbom
	 *
	 * @param end1Object
	 * @param bomTemplate
	 * @throws ServiceRequestException
	 */
	void updateUHasBOM(ObjectGuid end1Object, String bomTemplateGuid, String exceptionParameter, String fixTranId) throws ServiceRequestException;

	BOMTemplateInfo getBOMTemplateInfo(String templateGuid);

	BOMTemplateInfo getBOMTemplateInfoById(String templateId);

	List<BOMTemplateInfo> listBOMTemplateByName(String templateName);

	List<BOMTemplateInfo> listAllBOMTemplateInfo(boolean isContainObsolete);

	List<BOMTemplateEnd2> listBOMTemplateEnd2(String templateGuid);

	List<BOMReportTemplate> listBOMReportTemplate(String templateGuid);

	RelationTemplateInfo getRelationTemplateInfo(String templateGuid);

	RelationTemplateInfo getRelationTemplateInfoById(String templateId);

	List<RelationTemplateInfo> listRelationTemplateByName(String templateName);

	List<RelationTemplateInfo> listAllRelationTemplateInfo(boolean isContainObsolete);

	List<RelationTemplateEnd2> listRelationTemplateEnd2(String templateGuid);

	void deleteBOMTemplate(String templateName);

	void deleteBOMTemplateEnd2(String templateGuid);

	void deleteBOMReportTemplate(String templateGuid);

	void deleteRelationTemplate(String templateName);

	void deleteRelationTemplateEnd2(String templateGuid);
}
