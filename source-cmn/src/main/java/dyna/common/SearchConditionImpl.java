/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SearchCondition is a container for searching data
 * xiasheng Apr 30, 2010
 */
package dyna.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.systemenum.AdvancedQueryTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.ppms.SearchCategoryEnum;
import dyna.common.util.SetUtils;

/**
 * SearchCondition接口的实现
 * 
 * @author xiasheng
 */
public class SearchConditionImpl implements SearchCondition, Serializable
{
	private static final long			serialVersionUID		= -2869778119347995839L;
	private ObjectGuid					objectGuid				= null;
	private Folder						folder					= null;
	private final List<Criterion>		criterionList			= new ArrayList<Criterion>();
	private final List<String>			resultFieldList			= new ArrayList<String>();
	private final List<String>			relationResultFieldList	= new ArrayList<String>();

	private List<String>				resultUIObjectList		= null;
	private List<Map<String, Boolean>>	orderMapList			= null;
	private int							pageNum					= 1;
	private int							pageSize				= 20;
	private boolean						hasSubFolders			= true;
	private boolean						isCheckOutOnly			= false;

	// 是否包含废弃数据
	private boolean						isIncludeOBS			= false;
	// 是否区分大小写
	private boolean						caseSensitive			= true;
	// 是否仅搜索我的数据
	private boolean						isOwnerOnly				= false;
	// 是否仅搜索我组的数据
	private boolean						isOwnerGroupOnly		= false;
	// 接口
	private ModelInterfaceEnum			modelInterfaceEnum		= null;
	// 保存 查询历史的名称
	private String						searchName				= null;
	// 是否高级检索
	private boolean						isAdvanced				= false;
	// BOM模板id
	private String						bomTemplateId			= null;
	// 关系模板id
	private String						relationTemplateId		= null;
	// BO guid 列表
	private List<String>				boGuidList				= null;
	// classification guid的列表
	private List<String>				classificationGuidList	= null;
	// 高级查询中的分类
	// private String classification = null;
	// 高级查询中的分类master
	// private String masterClassification = null;
	// 查询BOM结构时的过滤值
	private String						searchValue				= null;

	private String						userProductBOGuid		= null;

	private boolean						isImport				= false;

	// 给产品模块记录父检索历史用
	private String						parentGuid				= null;

	// 搜索条件是否为空
	private boolean						allowEmpty				= false;

	private SearchCategoryEnum			searchCategory			= null;

	// 关联项目的ObejctGuid ,如存在时则包含私有交付物，不存在时则不包含
	private ObjectGuid					projectReference		= null;

	private SearchRevisionTypeEnum		searchRevisionTypeEnum	= null;

	private AdvancedQueryTypeEnum		searchType				= null;

	// 项目管理专用，指定参与者
	private String						userGuidForPPM			= null;

	private List<String>				limitClassList			= null;

	// 我管理的组
	private String						manageGroup				= null;

	@Override
	public boolean isCheckOutOnly()
	{
		return this.isCheckOutOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setCheckedOutOnly(boolean)
	 */
	@Override
	public void setCheckOutOnly(boolean isCheckOutOnly)
	{
		this.isCheckOutOnly = isCheckOutOnly;
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	@Override
	public void setParentGuid(String parentGuid)
	{
		this.parentGuid = parentGuid;
	}

	@Override
	public String getParentGuid()
	{
		return this.parentGuid;
	}

	@Override
	public boolean isAdvanced()
	{
		return this.isAdvanced;
	}

	@Override
	public void setIsAdvanced(boolean isAdvanced)
	{
		this.isAdvanced = isAdvanced;
	}

	@Override
	public boolean isOwnerOnly()
	{
		return this.isOwnerOnly;
	}

	@Override
	public void setOwnerOnly(boolean isOwnerOnly)
	{
		this.isOwnerOnly = isOwnerOnly;
	}

	@Override
	public boolean isOwnerGroupOnly()
	{
		return this.isOwnerGroupOnly;
	}

	@Override
	public void setOwnerGroupOnly(boolean isOwnerGroupOnly)
	{
		this.isOwnerGroupOnly = isOwnerGroupOnly;
	}

	protected SearchConditionImpl()
	{
		super();
	}

	protected SearchConditionImpl(ObjectGuid objectGuid, Folder folder, boolean hasSubFolders)
	{
		this();
		this.objectGuid = objectGuid;
		this.setFolder(folder);
		this.hasSubFolders = hasSubFolders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#add(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addFilter(String field, Object value, OperateSignEnum operateSignEnum)
	{
		this.criterionList.add(new Criterion(field, value, Criterion.CON_AND, operateSignEnum));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addFilter(dyna.common.systemenum.SystemClassFieldEnum, java.lang.Object)
	 */
	@Override
	public void addFilter(SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum)
	{
		this.addFilter(systemField.getName(), value, operateSignEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addFilter(java.lang.String, java.lang.String, java.lang.Object,
	 * dyna.common.systemenum.OperateSignEnum)
	 */
	@Override
	public void addFilter(FieldOrignTypeEnum fieldOrignType, String field, Object value, OperateSignEnum operateSignEnum)
	{
		this.criterionList.add(new Criterion(field, value, fieldOrignType, Criterion.CON_AND, operateSignEnum));
	}

	@Override
	public void addFilterWithJoin(String fieldName, Object value, OperateSignEnum operateSignEnum, String joinFieldName, String joinIndex)
	{
		this.criterionList.add(new Criterion(fieldName, value, Criterion.CON_AND, operateSignEnum, joinFieldName, joinIndex));
	}

	@Override
	public void addFilterWithJoinWithOR(String fieldName, Object value, OperateSignEnum operateSignEnum, String joinFieldName, String joinIndex)
	{
		this.criterionList.add(new Criterion(fieldName, value, Criterion.CON_OR, operateSignEnum, joinFieldName, joinIndex));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addFilter(java.lang.String, dyna.common.systemenum.SystemClassFieldEnum,
	 * java.lang.Object, dyna.common.systemenum.OperateSignEnum)
	 */
	@Override
	public void addFilter(FieldOrignTypeEnum fieldOrignType, SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum)
	{
		this.addFilter(fieldOrignType, systemField.getName(), value, operateSignEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addWithOR(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addFilterWithOR(String field, Object value, OperateSignEnum operateSignEnum)
	{
		this.criterionList.add(new Criterion(field, value, Criterion.CON_OR, operateSignEnum));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addFilterWithOR(dyna.common.systemenum.SystemClassFieldEnum, java.lang.Object)
	 */
	@Override
	public void addFilterWithOR(SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum)
	{
		this.addFilterWithOR(systemField.getName(), value, operateSignEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addFilterWithOR(java.lang.String, java.lang.String, java.lang.Object,
	 * dyna.common.systemenum.OperateSignEnum)
	 */
	@Override
	public void addFilterWithOR(FieldOrignTypeEnum fieldOrignType, String field, Object value, OperateSignEnum operateSignEnum)
	{
		this.criterionList.add(new Criterion(field, value, fieldOrignType, Criterion.CON_OR, operateSignEnum));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addFilterWithOR(java.lang.String, dyna.common.systemenum.SystemClassFieldEnum,
	 * java.lang.Object, dyna.common.systemenum.OperateSignEnum)
	 */
	@Override
	public void addFilterWithOR(FieldOrignTypeEnum fieldOrignType, SystemClassFieldEnum systemField, Object value, OperateSignEnum operateSignEnum)
	{
		this.addFilterWithOR(fieldOrignType, systemField.getName(), value, operateSignEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addOrder(java.lang.String, boolean)
	 */
	@Override
	public void addOrder(String field, boolean isAsc)
	{
		if (this.orderMapList == null)
		{
			this.orderMapList = new ArrayList<Map<String, Boolean>>();
		}

		Map<String, Boolean> orderMap = new HashMap<String, Boolean>();
		orderMap.put(field, Boolean.valueOf(isAsc));
		this.orderMapList.add(orderMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addOrder(boolean, java.lang.String, boolean)
	 */
	@Override
	public void addOrder(boolean isClassField, String field, boolean isAsc)
	{
		if (isClassField)
		{
			this.addOrder(field, isAsc);
		}
		else
		{
			field = FieldOrignTypeEnum.CLASSIFICATION.name() + FoundationObjectImpl.separator + field;
			this.addOrder(field, isAsc);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addOrder(java.lang.String, dyna.common.FieldOrignTypeEnum, boolean)
	 */
	@Override
	public void addOrder(String field, FieldOrignTypeEnum fieldOrignType, boolean isAsc)
	{
		field = fieldOrignType.name() + FoundationObjectImpl.separator + field;

		this.addOrder(field, isAsc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addOrder(dyna.common.systemenum.SystemClassFieldEnum, boolean)
	 */
	@Override
	public void addOrder(SystemClassFieldEnum systemField, boolean isAsc)
	{
		this.addOrder(systemField.getName(), isAsc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addOrder(dyna.common.systemenum.SystemClassFieldEnum,
	 * dyna.common.FieldOrignTypeEnum, boolean)
	 */
	@Override
	public void addOrder(SystemClassFieldEnum systemField, FieldOrignTypeEnum fieldOrignType, boolean isAsc)
	{
		this.addOrder(systemField.getName(), fieldOrignType, isAsc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addResultField(java.lang.String)
	 */
	@Override
	public void addResultField(String resultField)
	{
		if (!this.resultFieldList.contains(resultField.toUpperCase()))
		{
			this.resultFieldList.add(resultField.toUpperCase());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addResultField(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addResultField(String resultField, String joinFieldName, String joinIndex)
	{
		String val = resultField.toUpperCase() + "#" + joinFieldName.toUpperCase() + "#" + joinIndex.toUpperCase();
		if (!this.relationResultFieldList.contains(val))
		{
			this.relationResultFieldList.add(val);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#addResultField(java.lang.String, dyna.common.FieldOrignTypeEnum)
	 */
	@Override
	public void addResultField(String resultField, FieldOrignTypeEnum fieldOrignType)
	{
		String field = fieldOrignType.name() + FoundationObjectImpl.separator + resultField;
		this.addResultField(field);
	}

	@Override
	public boolean caseSensitive()
	{
		return this.caseSensitive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#clear()
	 */
	@Override
	public void clear()
	{
		this.criterionList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#clearResultField()
	 */
	@Override
	public void clearResultField()
	{
		if (this.getResultFieldList() == null)
		{
			return;
		}
		this.getResultFieldList().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#clearOrder()
	 */
	@Override
	public void clearOrder()
	{
		if (this.getOrderMapList() == null)
		{
			return;
		}
		this.getOrderMapList().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#endGroup()
	 */
	@Override
	public void endGroup()
	{
		this.criterionList.add(new Criterion(Criterion.GROUP_END, null, Criterion.CON_AND, null));
	}

	/**
	 * @return the criterionList
	 */
	@Override
	public List<Criterion> getCriterionList()
	{
		return this.criterionList;
	}

	@Override
	public Folder getFolder()
	{
		return this.folder;
	}

	@Override
	public ObjectGuid getObjectGuid()
	{
		if (this.objectGuid == null)
		{
			this.objectGuid = new ObjectGuid();
		}
		return this.objectGuid;
	}

	/**
	 * @return the orderMap
	 */
	@Override
	public List<Map<String, Boolean>> getOrderMapList()
	{
		return this.orderMapList;
	}

	@Override
	public int getPageSize()
	{
		return this.pageSize;
	}

	@Override
	public int getPageNum()
	{
		return this.pageNum == 0 ? 1 : this.pageNum;
	}

	@Override
	public List<String> getResultFieldList()
	{
		return this.resultFieldList;
	}

	@Override
	public List<String> getRelationResultFieldList()
	{
		return this.relationResultFieldList;
	}

	/**
	 * @return the hasSubFolders
	 */
	@Override
	public boolean hasSubFolders()
	{
		return this.hasSubFolders;
	}

	public boolean isEmpty()
	{
		return this.criterionList == null || this.criterionList.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#isOBSOnly()
	 */
	@Override
	public boolean isIncludeOBS()
	{
		return this.isIncludeOBS;
	}

	@Override
	public void setCaseSensitive(boolean caseSensitive)
	{
		this.caseSensitive = caseSensitive;
	}

	@Override
	public void setCriterionList(List<Criterion> criterionList)
	{
		if (SetUtils.isNullList(criterionList))
		{
			return;
		}
		this.criterionList.clear();
		this.criterionList.addAll(criterionList);
	}

	/**
	 * @param folder
	 *            the folder to set
	 */
	@Override
	public void setFolder(Folder folder)
	{
		this.folder = folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition# setGuid(java.lang.String);
	 */
	@Override
	public void setGuid(String guid)
	{
		this.objectGuid.setGuid(guid);
	}

	@Override
	public void setHasSubFolders(boolean hasSubFolders)
	{
		this.hasSubFolders = hasSubFolders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setOBSOnly(boolean)
	 */
	@Override
	public void setIncludeOBS(Boolean isIncludeOBS)
	{
		this.isIncludeOBS = isIncludeOBS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setPageSize(int)
	 */
	@Override
	public void setPageSize(int pageSize)
	{
		if (pageSize > SearchCondition.MAX_PAGE_SIZE)
		{
			pageSize = SearchCondition.MAX_PAGE_SIZE;
		}
		this.pageSize = pageSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setPageStartRowIdx(int)
	 */
	@Override
	public void setPageNum(int pageNum)
	{
		this.pageNum = pageNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#startGroup()
	 */
	@Override
	public void startGroup()
	{
		this.criterionList.add(new Criterion(Criterion.GROUP_START, null, Criterion.CON_AND, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#startGroupWithOR(java.lang.String)
	 */
	@Override
	public void startGroupWithOR()
	{
		this.criterionList.add(new Criterion(Criterion.GROUP_START, null, Criterion.CON_OR, null));
	}

	@Override
	public String getSearchName()
	{
		return this.searchName;
	}

	@Override
	public void setSearchName(String searchName)
	{
		this.searchName = searchName;
	}

	@Override
	public SearchCondition clone()
	{
		SearchConditionImpl sc = new SearchConditionImpl(this.objectGuid, this.folder, this.hasSubFolders);
		sc.criterionList.addAll(this.criterionList);
		sc.resultFieldList.addAll(this.resultFieldList);
		sc.relationResultFieldList.addAll(this.relationResultFieldList);
		sc.resultUIObjectList = this.resultUIObjectList;
		sc.orderMapList = this.orderMapList;
		sc.pageNum = this.pageNum;
		sc.pageSize = this.pageSize;
		sc.isIncludeOBS = this.isIncludeOBS;
		sc.caseSensitive = this.caseSensitive;
		sc.isOwnerOnly = this.isOwnerOnly;
		sc.isOwnerGroupOnly = this.isOwnerGroupOnly;
		sc.modelInterfaceEnum = this.modelInterfaceEnum;
		sc.searchName = this.searchName;
		sc.isAdvanced = this.isAdvanced;
		sc.boGuidList = this.boGuidList;
		sc.classificationGuidList = this.classificationGuidList;
		sc.searchType = this.searchType;
		sc.searchRevisionTypeEnum = this.searchRevisionTypeEnum;

		return sc;

	}

	@Override
	public String getBOMTemplateId()
	{
		return this.bomTemplateId;
	}

	@Override
	public void setBOMTemplateId(String templateId)
	{
		this.bomTemplateId = templateId;
	}

	@Override
	public String getRelationTemplateId()
	{
		return this.relationTemplateId;
	}

	@Override
	public void setRelationTemplateId(String templateId)
	{
		this.relationTemplateId = templateId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#getBOGuidList()
	 */
	@Override
	public List<String> getBOGuidList()
	{
		return this.boGuidList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setBOGuidList(java.util.List)
	 */
	@Override
	public void setBOGuidList(List<String> boGuidList)
	{
		this.boGuidList = boGuidList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#getClassificationList()
	 */
	@Override
	public List<String> getClassificationGuidList()
	{
		return this.classificationGuidList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setClassificationList(java.util.List)
	 */
	@Override
	public void setClassificationList(List<String> classificationGuidList)
	{
		this.classificationGuidList = classificationGuidList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#getClassification()
	 */
	@Override
	public String getClassification()
	{
		return this.getValFromCriterion(SystemClassFieldEnum.CLASSIFICATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#getSearchValue()
	 */
	@Override
	public String getSearchValue()
	{
		return this.searchValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setSearchValue(java.lang.String)
	 */
	@Override
	public void setSearchValue(String searchValue)
	{
		this.searchValue = searchValue;
	}

	@Override
	public String getUserProductBOGuid()
	{
		return this.userProductBOGuid;
	}

	@Override
	public void setUserProductBOGuid(String userProductBOGuid)
	{
		this.userProductBOGuid = userProductBOGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setIsImport(boolean)
	 */
	@Override
	public void setIsImport(boolean isImport)
	{
		this.isImport = isImport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#isImport()
	 */
	@Override
	public boolean isImport()
	{
		return this.isImport;
	}

	@Override
	public void addResultUIObjectName(String uiObject)
	{
		if (this.resultUIObjectList == null)
		{
			this.resultUIObjectList = new ArrayList<String>();
			this.resultUIObjectList.add(uiObject);
		}
		else
		{
			if (!this.resultUIObjectList.contains(uiObject))
			{
				this.resultUIObjectList.add(uiObject);
			}
		}

	}

	@Override
	public void setResultUINameList(List<String> uiObject)
	{
		this.resultUIObjectList = uiObject;
	}

	@Override
	public List<String> listResultUINameList()
	{
		return this.resultUIObjectList;
	}

	/**
	 * @return the allowEmpty
	 */
	@Override
	public boolean isAllowEmpty()
	{
		return this.allowEmpty;
	}

	/**
	 * @param allowEmpty
	 *            the allowEmpty to set
	 */
	@Override
	public void setAllowEmpty(boolean allowEmpty)
	{
		this.allowEmpty = allowEmpty;
	}

	/**
	 * @return the searchConditionType
	 */
	@Override
	public SearchCategoryEnum getSearchCategory()
	{
		return this.searchCategory;
	}

	/**
	 * @param searchConditionType
	 *            the searchConditionType to set
	 */
	@Override
	public void setSearchCategory(SearchCategoryEnum searchCategory)
	{
		this.searchCategory = searchCategory;
	}

	/**
	 * @return the projectReference
	 */
	@Override
	public ObjectGuid getProjectReference()
	{
		return this.projectReference;
	}

	/**
	 * @param projectReference
	 *            the projectReference to set
	 */
	@Override
	public void setProjectReference(ObjectGuid projectReference)
	{
		this.projectReference = projectReference;
	}

	@Override
	public boolean isLatestOnly()
	{
		return SearchRevisionTypeEnum.ISLATESTONLY.getType().equals(this.getSearchRevisionType());
	}

	@Override
	public boolean isHistoryRevision()
	{
		return SearchRevisionTypeEnum.ISHISTORYREVISION.getType().equals(this.getSearchRevisionType());
	}

	@Override
	public boolean isLatestRLSOnly()
	{
		return SearchRevisionTypeEnum.ISLATESTRLSONLY.getType().equals(this.getSearchRevisionType());
	}

	@Override
	public String getSearchRevisionType()
	{
		if (this.searchRevisionTypeEnum == null)
		{
			return SearchRevisionTypeEnum.ISLATESTONLY.getType();
		}
		return this.searchRevisionTypeEnum.getType();
	}

	@Override
	public SearchRevisionTypeEnum getSearchRevisionTypeEnum()
	{
		if (this.searchRevisionTypeEnum == null)
		{
			return SearchRevisionTypeEnum.ISLATESTONLY;
		}
		return this.searchRevisionTypeEnum;
	}

	@Override
	public void setSearchRevisionTypeEnum(SearchRevisionTypeEnum searchRevisionTypeEnum)
	{
		this.searchRevisionTypeEnum = searchRevisionTypeEnum;
	}

	@Override
	public void setPageSizeMax(int pageSize)
	{
		this.pageSize = pageSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#setSearchType(dyna.common.systemenum.AdvancedQueryTypeEnum)
	 */
	@Override
	public void setSearchType(AdvancedQueryTypeEnum searchType)
	{
		this.searchType = searchType;
	}

	@Override
	public String getUserGuidForPPM()
	{
		return this.userGuidForPPM;
	}

	@Override
	public void setUserGuidForPPM(String userGuidForPPM)
	{
		this.userGuidForPPM = userGuidForPPM;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.SearchCondition#getSearchType()
	 */
	@Override
	public AdvancedQueryTypeEnum getSearchType()
	{
		return this.searchType == null ? AdvancedQueryTypeEnum.NORMAL : this.searchType;
	}

	private String getValFromCriterion(SystemClassFieldEnum classFieldEnum)
	{
		for (int i = 0, j = (this.getCriterionList() == null) ? 0 : (this.getCriterionList().size()); i < j; i++)
		{
			Criterion criterion = this.getCriterionList().get(i);
			String key = criterion.getKey();

			String fieldName = classFieldEnum.getName();
			if (key.equalsIgnoreCase(fieldName))
			{
				return String.valueOf(criterion.getValue());
			}
		}

		return null;
	}

	@Override
	public void setLimitClassList(List<String> limitClassList)
	{
		this.limitClassList = limitClassList;
	}

	@Override
	public List<String> getLimitClassList()
	{
		return this.limitClassList;
	}

	@Override
	public void setManageGroup(String groupGuid)
	{
		this.manageGroup = groupGuid;
	}

	@Override
	public String getManageGroup()
	{
		return this.manageGroup;
	}
}
