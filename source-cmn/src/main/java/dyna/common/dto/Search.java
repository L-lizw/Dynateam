/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Search  检索条件BEAN
 * caogc 2010-08-26
 */
package dyna.common.dto;

import dyna.common.SearchCondition;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.SearchMapper;
import dyna.common.systemenum.*;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * 检索条件BEAN,用以存放检索条件历史记录
 * 
 * @author caogc
 * 
 */
@EntryMapper(SearchMapper.class)
public class Search extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= -5168560756324214395L;
	public static final String	GUID					= "GUID";
	public static final String	OWNER_USER				= "OWNERUSER";
	public static final String	OWNER_USER_NAME			= "OWNERUSERNAME";
	public static final String	TYPE					= "ISUSERSAVED";
	public static final String	PMTYPE					= "PMTYPE";
	public static final String	NAME					= "CONDITIONNAME";
	public static final String	BO_TITLE				= "BOTITLE";
	public static final String	CREATE_TIME				= "CREATETIME";
	public static final String	UPDATE_TIME				= "UPDATETIME";
	public static final String	FIRST_CONDITION			= "FIRSTCONDITION";
	public static final String	SECOND_CONDITION		= "SECONDCONDITION";
	public static final String	CLASS_GUID				= "CLASSGUID";
	public static final String	CLASSIFICATION			= "CLASSIFICATION";
	public static final String	MODEL_INTERFACE_ENUM	= "MODELINTERFACEENUM";
	public static final String	OBS_ONLY				= "ISOBSONLY";

	public static final String	HAS_SUB_FOLDERS			= "HASSUBFOLDERS";
	public static final String	HAS_CONTAIN_HIDDEN_DATA	= "ISSHOWHIDEDATA";
	public static final String	SEARCH_REVISION_TYPE	= "SEARCHREVISIONTYPE";

	// 是否高级检索
	public static final String	IS_ADVANCED				= "ISADVANCED";

	// 是否区分大小写
	public static final String	CASE_SENSITIVE			= "CASESENSITIVE";

	// 是否只查看本人的数据
	public static final String	IS_OWNER_ONLY			= "ISOWNERONLY";

	// 是否只查看所有者所属组的数据
	public static final String	IS_OWNER_GROUP_ONLY		= "ISOWNERGROUPONLY";

	public static final String	FOLDER_GUID				= "FOLDERGUID";

	// 分号的替换符
	public static final String	semicolon				= "$semicolon";
	// 逗号的替换符
	public static final String	comma					= "$comma";
	// 字符串的最大长度标准
	public static final int		maxByte					= 512;

	private SearchCondition		searchCondtion			= null;

	public static final int		MAX_HISTORY_NUM			= 50;

	public static final String	BOM_TEMPLATE_ID			= "BOMTEMPLATEID";

	public static final String	RELATION_TEMPLATE_ID	= "RELATIONTEMPLATEID";

	public static final String	USER_PRODUCT_BO_GUID	= "USERPRODUCTBOGUID";

	// 用以记录父结果的guid
	public static final String	PARENT_GUID				= "PARENTGUID";

	public static final String	IS_CHECKOUT_ONLY		= "ISCHECKOUTONLY";
	
	public static final String  ADVANCED_QUERY_TYPE     = "QUERYTYPE";

	public boolean isCheckOutOnly()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(IS_CHECKOUT_ONLY));
		return ret == null ? false : ret.booleanValue();
	}

	public void setCheckOutOnly(boolean isCheckOutOnly)
	{
		this.put(IS_CHECKOUT_ONLY, BooleanUtils.getBooleanStringYN(isCheckOutOnly));
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.put(PARENT_GUID, parentGuid);
	}

	public String getParentGuid()
	{
		return (String) this.get(PARENT_GUID);
	}

	/**
	 * @return the searchRevisionType
	 */
	public SearchRevisionTypeEnum getSearchRevisionType()
	{
		return SearchRevisionTypeEnum.typeValueOf((String) this.get(SEARCH_REVISION_TYPE));
	}

	/**
	 * @param searchRevisionType
	 *            the searchRevisionType to set
	 */
	public void setSearchRevisionType(String searchRevisionType)
	{
		this.put(SEARCH_REVISION_TYPE, searchRevisionType);
	}

	public String getUserProductBOGuid()
	{
		return (String) this.get(USER_PRODUCT_BO_GUID);
	}

	public void setUserProductBOGuid(String userProductBOGuid)
	{
		this.put(USER_PRODUCT_BO_GUID, userProductBOGuid);
	}
	
	public AdvancedQueryTypeEnum getAdvancedQueryType()
	{
		String queryType = (String) this.get(ADVANCED_QUERY_TYPE);
		
		return AdvancedQueryTypeEnum.typeof(queryType);
	}

	public void setAdvancedQueryType(AdvancedQueryTypeEnum queryTypeEnum)
	{
		this.put(ADVANCED_QUERY_TYPE, queryTypeEnum.getType());
	}

	public boolean hasContainHiddenData()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(HAS_CONTAIN_HIDDEN_DATA));
		return ret == null ? false : ret.booleanValue();
	}

	public void setHasContainHiddenData(boolean hasContainHiddenData)
	{
		this.put(HAS_CONTAIN_HIDDEN_DATA, BooleanUtils.getBooleanStringYN(hasContainHiddenData));
	}

	public String getBomTemplateId()
	{
		return (String) this.get(BOM_TEMPLATE_ID);
	}

	public void setBomTemplateId(String bomTemplateId)
	{
		this.put(BOM_TEMPLATE_ID, bomTemplateId);
	}

	public String getRelationTemplateId()
	{
		return (String) this.get(RELATION_TEMPLATE_ID);
	}

	public void setRelationTemplateId(String relationTemplateId)
	{
		this.put(RELATION_TEMPLATE_ID, relationTemplateId);
	}

	public String getModelInterfaceEnum()
	{
		return (String) this.get(MODEL_INTERFACE_ENUM);
	}

	public void setModelInterfaceEnum(String modelInterfaceEnum)
	{
		this.put(MODEL_INTERFACE_ENUM, modelInterfaceEnum);
	}

	public String getBOTitle()
	{
		return (String) this.get(BO_TITLE);
	}

	public void setBOTitle(String boTitle)
	{
		this.put(BO_TITLE, boTitle);
	}

	public boolean isOwnerOnly()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(IS_OWNER_ONLY)) == null ? false : BooleanUtils
				.getBooleanByYN((String) this.get(IS_OWNER_ONLY));
	}

	public void setOwnerOnly(boolean isOwnerOnly)
	{
		this.put(IS_OWNER_ONLY, BooleanUtils.getBooleanStringYN(isOwnerOnly));
	}

	public boolean isOwnerGroupOnly()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(IS_OWNER_GROUP_ONLY)) == null ? false : BooleanUtils
				.getBooleanByYN((String) this.get(IS_OWNER_GROUP_ONLY));
	}

	public void setOwnerGroupOnly(boolean isOwnerGroupOnly)
	{
		this.put(IS_OWNER_GROUP_ONLY, BooleanUtils.getBooleanStringYN(isOwnerGroupOnly));
	}

	public boolean caseSensitive()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(CASE_SENSITIVE)) == null ? false : BooleanUtils
				.getBooleanByYN((String) this.get(CASE_SENSITIVE));
	}

	/**
	 * @return the ClassGuid
	 */
	public String getClassGuid()
	{
		return (String) this.get(CLASS_GUID);
	}

	/**
	 * @return the classification
	 */
	public String getClassification()
	{
		return (String) this.get(CLASSIFICATION);
	}

	/**
	 * @return the CreateTime
	 */
	@Override
	public Date getCreateTime()
	{
		return (Date) this.get(CREATE_TIME);
	}

	/**
	 * @return the FirstCondition
	 */
	public String getFirstCondition()
	{
		return (String) this.get(FIRST_CONDITION);
	}

	/**
	 * @return the FolderGuid
	 */
	public String getFolderGuid()
	{
		return (String) this.get(FOLDER_GUID);
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the IsUserSaved
	 */
	public SearchTypeEnum getType()
	{
		return SearchTypeEnum.getEnum((String) this.get(TYPE));
	}

	/**
	 * @return the Name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	/**
	 * @return the Title
	 */
	public String getTitle(LanguageEnum lang)
	{
		String title = this.getName();

		if (StringUtils.isNullString(title))
		{
			return null;
		}

		if (this.getType() == SearchTypeEnum.PUBLIC || this.getType() == SearchTypeEnum.USER)
		{

			if (title.length() > 20)
			{
				title = title.substring(0, 20) + "···";
			}

			return title;
		}

		if (title.length() > 20)
		{
			return title.substring(0, 20) + "···";
		}

		if (!StringUtils.isNullString(this.getBOTitle()))
		{
			title = title + "[" + StringUtils.getMsrTitle(this.getBOTitle(), lang.getType()) + "]";
		}

		if (this.getSearchCondition() != null && this.getSearchCondition().getFolder() != null)
		{
			if (!this.getSearchCondition().getFolder().getType().equals(FolderTypeEnum.PRIVATE))
			{
				title = title + "-" + this.getSearchCondition().getFolder().getName();
			}
		}

		if (title.length() > 20)
		{
			title = title.substring(0, 20) + "···";
		}

		return title;
	}

	/**
	 * @return the OwnerUser
	 */
	public String getOwnerUser()
	{
		return (String) this.get(OWNER_USER);
	}

	/**
	 * @return the OwnerUserName
	 */
	public String getOwnerUserName()
	{
		return (String) this.get(OWNER_USER_NAME);
	}

	public SearchCondition getSearchCondition()
	{
		return this.searchCondtion;
	}

	/**
	 * @return the SecondCondition
	 */
	public String getSecondCondition()
	{
		return (String) this.get(SECOND_CONDITION);
	}

	/**
	 * @return the UpdateTime
	 */
	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get(UPDATE_TIME);
	}

	/**
	 * @return the ClassGuid
	 */
	public boolean hasSubFolders()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(HAS_SUB_FOLDERS)) == null ? false : BooleanUtils
				.getBooleanByYN((String) this.get(HAS_SUB_FOLDERS));
	}

	public Boolean isAdvanced()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(IS_ADVANCED)) == null ? false : BooleanUtils
				.getBooleanByYN((String) this.get(IS_ADVANCED));
	}

	public boolean isIncludeOBS()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(OBS_ONLY)) == null ? false : BooleanUtils
				.getBooleanByYN((String) this.get(OBS_ONLY));
	}

	public boolean isLatestOnly()
	{
		return SearchRevisionTypeEnum.ISLATESTONLY.getType().equals(this.getSearchRevisionType());
	}

	public boolean isHistoryRevision()
	{
		return SearchRevisionTypeEnum.ISHISTORYREVISION.getType().equals(this.getSearchRevisionType());
	}

	public boolean isLatestRLSOnly()
	{
		return SearchRevisionTypeEnum.ISLATESTRLSONLY.getType().equals(this.getSearchRevisionType());
	}

	public void setCaseSensitive(boolean caseSensitive)
	{
		this.put(CASE_SENSITIVE, BooleanUtils.getBooleanStringYN(caseSensitive));
	}

	/**
	 * @param ClassGuid
	 *            the ClassGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		this.put(CLASS_GUID, classGuid);
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassification(String classification)
	{
		this.put(CLASSIFICATION, classification);
	}

	/**
	 * @param CreateTime
	 *            the CreateTime to set
	 */
	public void setCreateTime(Date createTime)
	{
		this.put(CREATE_TIME, createTime);
	}

	/**
	 * @param FirstCondition
	 *            the FirstCondition to set
	 */
	public void setFirstCondition(String firstCondition)
	{
		this.put(FIRST_CONDITION, firstCondition);
	}

	/**
	 * @param FolderGuid
	 *            the FolderGuid to set
	 */
	public void setFolderGuid(String folderGuid)
	{
		this.put(FOLDER_GUID, folderGuid);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @param hasSubFolders
	 *            the hasSubFolders to set
	 */
	public void setHasSubFolders(boolean hasSubFolders)
	{
		this.put(HAS_SUB_FOLDERS, BooleanUtils.getBooleanStringYN(hasSubFolders));
	}

	public void setIsAdvanced(boolean isAdvanced)
	{
		this.put(IS_ADVANCED, BooleanUtils.getBooleanStringYN(isAdvanced));
	}

	/**
	 * @param IsUserSaved
	 *            the IsUserSaved to set
	 */
	public void setType(SearchTypeEnum typeEnum)
	{
		if (typeEnum == null)
		{
			this.put(TYPE, null);
		}
		else
		{
			this.put(TYPE, typeEnum.getValue());
		}
	}

	/**
	 * @param Name
	 *            the Name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public void setIncludeOBS(boolean includeOBS)
	{
		this.put(OBS_ONLY, BooleanUtils.getBooleanStringYN(includeOBS));
	}

	/**
	 * @param OwnerUser
	 *            the OwnerUser to set
	 */
	public void setOwnerUser(String ownerUser)
	{
		this.put(OWNER_USER, ownerUser);
	}

	/**
	 * @param OwnerUserName
	 *            the OwnerUserName to set
	 */
	public void setOwnerUserName(String ownerUserName)
	{
		this.put(OWNER_USER_NAME, ownerUserName);
	}

	public void setSearchCondition(SearchCondition sc)
	{
		this.searchCondtion = sc;
	}

	/**
	 * @param SecondCondition
	 *            the SecondCondition to set
	 */
	public void setSecondCondition(String secondCondition)
	{
		this.put(SECOND_CONDITION, secondCondition);
	}

	/**
	 * @param UpdateTime
	 *            the UpdateTime to set
	 */
	public void setUpdateTime(Date updateTime)
	{
		this.put(UPDATE_TIME, updateTime);
	}

	/**
	 * @return the typeEnum
	 */
	public PMSearchTypeEnum getPMType()
	{
		return PMSearchTypeEnum.getEnum((String) this.get(PMTYPE));
	}

	/**
	 * @param typeEnum
	 *            the typeEnum to set
	 */
	public void setPMType(PMSearchTypeEnum typeEnum)
	{
		this.put(PMTYPE, typeEnum.getValue());
	}

}
