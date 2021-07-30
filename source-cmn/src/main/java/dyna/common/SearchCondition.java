/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SearchCondition
 * xiasheng May 4, 2010
 */
package dyna.common;

import java.util.List;
import java.util.Map;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.systemenum.AdvancedQueryTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.ppms.SearchCategoryEnum;

/**
 * 查询数据时使用的搜索条件容器, 可以按照单类查询, 多类查询, 接口查询
 * 
 * @author xiasheng
 */
public interface SearchCondition
{

	public static final String	VALUE_NULL					= "NULL";
	public static final String	BMGUID						= "BMGUID";
	public static final String	CLASSIFICATION_MASTER_GUID	= "CLASSIFICATIONMASTERGUID";

	public static final int		MAX_PAGE_SIZE				= 500;

	public static final Folder	ROOT_LIB					= new Folder(Folder.ROOT_LIB_GUID);

	/**
	 * 添加过滤条件（“与”操作）支持以下格式字符串查询：
	 * 1. 使用'*'或'%', '?'或'_'通配符进行查询
	 * 2. 使用双引号包围的字符串进行精确查询
	 * 3. 其它字符串使用部分匹配查询
	 * 
	 * @param field
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilter(String field, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加过滤条件（“与”操作）支持以下格式字符串查询：
	 * 1. 使用'*'或'%', '?'或'_'通配符进行查询
	 * 2. 使用双引号包围的字符串进行精确查询
	 * 3. 其它字符串使用部分匹配查询
	 * 
	 * @param fieldOrignType
	 *            字段来自于分类或者类
	 * @param field
	 *            字段名
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilter(FieldOrignTypeEnum fieldOrignType, String field, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加过滤条件（“与”操作）支持以下格式字符串查询：
	 * 1. 使用'*'或'%', '?'或'_'通配符进行查询
	 * 2. 使用双引号包围的字符串进行精确查询
	 * 3. 其它字符串使用部分匹配查询
	 * 
	 * @param fieldName
	 *            关联对象的字段
	 * @param value
	 * @param operateSignEnum
	 * @param joinFieldName
	 *            为当前对象与关联对象关联的字段
	 * @param joinIndex
	 *            关联表索引, 相同对象相同索引为同一张表.
	 */
	public abstract void addFilterWithJoin(String fieldName, Object value, OperateSignEnum operateSignEnum, String joinFieldName, String joinIndex);

	/**
	 * 添加系统字段的过滤条件（“与”操作）
	 * 
	 * @param systemField
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilter(SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加系统字段的过滤条件（“与”操作）
	 * 
	 * @param fieldOrignType
	 *            字段来自于分类或者类
	 * @param systemField
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilter(FieldOrignTypeEnum fieldOrignType, SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加过滤条件（“或”操作）支持以下格式字符串查询：
	 * 1. 使用'*'或'%', '?'或'_'通配符进行查询
	 * 2. 使用双引号包围的字符串进行精确查询
	 * 3. 其它字符串使用部分匹配查询
	 * 
	 * @param field
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilterWithOR(String field, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加过滤条件（“或”操作）支持以下格式字符串查询：
	 * 1. 使用'*'或'%', '?'或'_'通配符进行查询
	 * 2. 使用双引号包围的字符串进行精确查询
	 * 3. 其它字符串使用部分匹配查询
	 * 
	 * @param fieldName
	 *            关联对象的字段
	 * @param value
	 * @param operateSignEnum
	 * @param joinFieldName
	 *            当前对象与关联对象关联的字段
	 * @param joinIndex
	 *            关联表索引, 相同对象相同索引为同一张表.
	 */
	public abstract void addFilterWithJoinWithOR(String fieldName, Object value, OperateSignEnum operateSignEnum, String joinFieldName, String joinIndex);

	/**
	 * 添加过滤条件（“或”操作）支持以下格式字符串查询：
	 * 1. 使用'*'或'%', '?'或'_'通配符进行查询
	 * 2. 使用双引号包围的字符串进行精确查询
	 * 3. 其它字符串使用部分匹配查询
	 * 
	 * @param fieldOrignType
	 *            字段来自于分类或者类
	 * @param field
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilterWithOR(FieldOrignTypeEnum fieldOrignType, String field, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加系统字段的过滤条件（“或”操作）
	 * 
	 * @param systemField
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilterWithOR(SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加系统字段的过滤条件（“或”操作）
	 * 
	 * @param fieldOrignType
	 *            字段来自于分类或者类
	 * @param systemField
	 * @param value
	 * @param operateSignEnum
	 */
	public abstract void addFilterWithOR(FieldOrignTypeEnum fieldOrignType, SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum);

	/**
	 * 添加排序字段并指明是否正序排序
	 * 
	 * @param orderField
	 * @param isAsc
	 */
	public abstract void addOrder(String field, boolean isAsc);

	/**
	 * 添加排序字段并指明是否正序排序
	 * 
	 * @param isClassField
	 *            是否是类上的字段。true：是类上字段；false：是分类上的字段。
	 * @param orderField
	 * @param isAsc
	 */
	public abstract void addOrder(boolean isClassField, String field, boolean isAsc);

	/**
	 * 添加系统字段并指明是否正序排序
	 * 
	 * @param orderSystemField
	 * @param isAsc
	 */
	public abstract void addOrder(SystemClassFieldEnum systemField, boolean isAsc);

	/**
	 * 添加系统字段并指明是否正序排序
	 * 
	 * @param orderSystemField
	 * @param fieldOrignType
	 * @param isAsc
	 */
	public abstract void addOrder(String field, FieldOrignTypeEnum fieldOrignType, boolean isAsc);

	/**
	 * 添加系统字段并指明是否正序排序
	 * 
	 * @param systemField
	 * @param fieldOrignType
	 * @param isAsc
	 */
	public abstract void addOrder(SystemClassFieldEnum systemField, FieldOrignTypeEnum fieldOrignType, boolean isAsc);

	/**
	 * 添加返回结果集的字段。
	 * 当查询结构字段时,可以通过添加前缀来返回end1、end2、view的字段，前缀分别为："end1$","end2$","view$"
	 * 
	 * @param resultField
	 */
	public abstract void addResultField(String resultField);

	/**
	 * 添加返回结果集的字段
	 * 
	 * @param resultField
	 *            关联对象的字段,最终返回格式为:当前对象关联字段#对象索引#关联对象字段(可能包含$符号)
	 * @param joinFieldName
	 *            与关联对象关联的字段
	 * @param joinIndex
	 *            关联对象索引
	 */
	public abstract void addResultField(String resultField, String joinFieldName, String joinIndex);

	/**
	 * 添加返回结果集的字段
	 * 
	 * @param resultField
	 * @param fieldOrignType
	 */
	public abstract void addResultField(String resultField, FieldOrignTypeEnum fieldOrignType);

	/**
	 * 是否区分大小写
	 * 
	 * @return true 区分 false 不区分
	 */
	public boolean caseSensitive();

	public boolean isOwnerOnly();

	public void setOwnerOnly(boolean isOwnerOnly);

	public boolean isOwnerGroupOnly();

	public void setOwnerGroupOnly(boolean isOwnerGroupOnly);

	/**
	 * 清除所有过滤条件
	 */
	public abstract void clear();

	/**
	 * 清除查询列
	 */
	public void clearResultField();

	/**
	 * 清除排序
	 */
	public void clearOrder();

	/**
	 * 结束一个过滤条件组
	 */
	public abstract void endGroup();

	/**
	 * 获取 Criterion List
	 * 
	 * @return
	 */
	public List<Criterion> getCriterionList();

	/**
	 * 查询的folder
	 * 
	 * @return
	 */
	public Folder getFolder();

	/**
	 * 获取查询ObjectGuid条件
	 * 
	 * @return
	 */
	public ObjectGuid getObjectGuid();

	/**
	 * 查询排序字段列表
	 * 
	 * @return
	 */
	public List<Map<String, Boolean>> getOrderMapList();

	/**
	 * 每页数据量
	 * 
	 * @return
	 */
	public int getPageSize();

	/**
	 * 每页数据量
	 * 
	 * @return
	 */
	public void setPageSizeMax(int pageSize);

	/**
	 * 分页开始序号
	 * 
	 * @return
	 */
	public int getPageNum();

	/**
	 * 获取查询结果列
	 * 
	 * @return
	 */
	public List<String> getResultFieldList();

	/**
	 * 获取关联对象的查询字段,只有添加的字段才被查询
	 * 
	 * @return
	 */
	public List<String> getRelationResultFieldList();

	/**
	 * 获取结果UIObject的名称列表
	 * 
	 * @return
	 */
	public List<String> listResultUINameList();

	/**
	 * 是否查询子目录
	 * 
	 * @return the hasSubFolders
	 */
	public boolean hasSubFolders();

	/**
	 * 是否只查询废弃数据
	 */
	public abstract boolean isIncludeOBS();

	public abstract void setCaseSensitive(boolean caseSensitive);

	/**
	 * 配置criterionList数据
	 * 
	 * @param criterionList
	 */
	public void setCriterionList(List<Criterion> criterionList);

	/**
	 * 设置instance guid为查找条件，将忽略其它查找条件
	 * 
	 * @param guid
	 */
	public abstract void setGuid(String guid);

	public void setFolder(Folder folder);

	/**
	 * 设置是否查询子目录
	 * 
	 * @param hasSubFolders
	 */
	public void setHasSubFolders(boolean hasSubFolders);

	public abstract void setIncludeOBS(Boolean obsOnly);

	/**
	 * 指定页面大小（数据集行数，默认值为20，最大值为100）
	 * 
	 * @param pageSize
	 */
	public abstract void setPageSize(int pageSize);

	/**
	 * 指定查询页的页码
	 * 
	 * @param pageStart
	 */
	public abstract void setPageNum(int pageNum);

	/**
	 * 指定查询结果集相关的UI（仅当未指定任何结果集字段时有效）
	 * 
	 * 此方法设置会清空原有列表，用新列表替代，如果参数为null,会将列表置空
	 * 
	 * @param uiObject
	 */
	public abstract void setResultUINameList(List<String> uiObjectNameList);

	/**
	 * 指定查询结果集相关的UI（仅当未指定任何结果集字段时有效）
	 * 
	 * @param uiObject
	 */
	public abstract void addResultUIObjectName(String uiObjectName);

	/**
	 * 开始一个过滤条件组（“与”操作）
	 */
	public abstract void startGroup();

	/**
	 * 开始一个过滤条件组（“或”操作）
	 */
	public abstract void startGroupWithOR();

	public abstract String getSearchName();

	public abstract void setSearchName(String searchName);

	public SearchCondition clone();

	/**
	 * 是否高级搜索
	 * 
	 * @return
	 */
	public boolean isAdvanced();

	public void setIsAdvanced(boolean isAdvanced);

	/**
	 * 按BOM模板定义的end2列表查询
	 * 
	 * @return
	 */
	public String getBOMTemplateId();

	/**
	 * 设置BOM模板的GUID
	 * 
	 * @param templateGuid
	 */
	public void setBOMTemplateId(String templateGuid);

	/**
	 * 按关系模板定义的end2列表查询
	 * 
	 * @return
	 */
	public String getRelationTemplateId();

	public void setRelationTemplateId(String templateId);

	/**
	 * 取得BO guid的列表
	 * 
	 * @return
	 */
	public List<String> getBOGuidList();

	public void setBOGuidList(List<String> boGuidList);

	/**
	 * 取得classification guid的列表
	 * 
	 * @return
	 */
	public List<String> getClassificationGuidList();

	public void setClassificationList(List<String> classificationGuidList);

	/**
	 * 从过滤条件中取得分类guid
	 * 
	 * @return
	 */
	public String getClassification();

	/**
	 * 取得查询BOM结构时的过滤值
	 * 
	 * @return
	 */
	public String getSearchValue();

	public void setSearchValue(String searchValue);

	/**
	 * 用以表示产品模块的检索历史
	 * 
	 * @return
	 */
	public String getUserProductBOGuid();

	/**
	 * 用以表示产品模块的检索历史
	 * 
	 * @param userProductBOGuid
	 */
	public void setUserProductBOGuid(String userProductBOGuid);

	/**
	 * 数据导入
	 * 
	 * @param isImport
	 */
	public void setIsImport(boolean isImport);

	public boolean isImport();

	public boolean isCheckOutOnly();

	/**
	 * 设置是否仅返回检出的数据（默认为所有数据）
	 * 
	 * @param isCheckOutOnly
	 */
	void setCheckOutOnly(boolean isCheckOutOnly);

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid);

	public String getParentGuid();

	/**
	 * @return the allowEmpty
	 */
	public boolean isAllowEmpty();

	/**
	 * @param allowEmpty
	 *            the allowEmpty to set
	 */
	public void setAllowEmpty(boolean allowEmpty);

	/**
	 * @return the searchConditionType
	 */
	public SearchCategoryEnum getSearchCategory();

	/**
	 * @param searchConditionType
	 *            the searchConditionType to set
	 */
	public void setSearchCategory(SearchCategoryEnum searchCategory);

	/**
	 * @return the projectReference
	 */
	public ObjectGuid getProjectReference();

	/**
	 * @param projectReference
	 *            the projectReference to set
	 */
	public void setProjectReference(ObjectGuid projectReference);

	public String getSearchRevisionType();

	/**
	 * 取得高级检索要查询的版本类型。
	 * 
	 * @return
	 */
	public SearchRevisionTypeEnum getSearchRevisionTypeEnum();

	/**
	 * 查询最新版
	 * 
	 * @return true/false
	 */
	public boolean isLatestOnly();

	/**
	 * 查询历史版本
	 * 
	 * @return true/false
	 */
	public boolean isHistoryRevision();

	/**
	 * 查询已发布最新版
	 * 
	 * @return true/false
	 */
	public boolean isLatestRLSOnly();

	public void setSearchRevisionTypeEnum(SearchRevisionTypeEnum searchRevisionTypeEnum);

	/**
	 * 高级查询类型
	 * 
	 * @param searchType
	 */
	public void setSearchType(AdvancedQueryTypeEnum searchType);

	public AdvancedQueryTypeEnum getSearchType();

	public String getUserGuidForPPM();

	public void setUserGuidForPPM(String userGuidForPPM);

	/**
	 * 进行多类查询时，设置查询类的范围。目前仅限使用在产品配置，材料明细中使用
	 * 
	 * @param limitClassList
	 */
	public void setLimitClassList(List<String> limitClassList);

	public List<String> getLimitClassList();

	/**
	 * 我管理的组,用于项目管理中，查看我管理的组成员的所有项目
	 * 
	 * @param groupGuid
	 */
	public void setManageGroup(String groupGuid);

	public String getManageGroup();
}