/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SearchConditionFactory searchcondition 工厂方法
 * Wanglei 2010-7-21
 */
package dyna.common;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.Mail;
import dyna.common.dto.Search;
import dyna.common.dto.StatisticResult;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SearchTypeEnum;
import dyna.common.systemenum.StatisticResultEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * searchcondition 工厂方法
 * 
 * @author Wanglei
 * 
 */
public class SearchConditionFactory
{

	/**
	 * 创建不包含任何条件的search condition
	 * 
	 * @return
	 */
	public static SearchCondition createSearchCondition()
	{
		SearchCondition sc = new SearchConditionImpl();
		return sc;
	}

	/**
	 * 根据提供参数创建search condition
	 * 
	 * @param objectGuid
	 * @param folder
	 * @param hasSubFolders
	 * @return
	 */
	public static SearchCondition createSearchCondition(ObjectGuid objectGuid, Folder folder, boolean hasSubFolders)
	{
		return new SearchConditionImpl(objectGuid, folder, hasSubFolders);
	}

	/**
	 * 创建查询订阅夹内容的search condition
	 * 
	 * @param subscriptionFolderGuid
	 * @param hasSubFolder
	 * @return
	 */
	public static SearchCondition createSearchCondition4AllSubscription()
	{
		SearchCondition sc = createSearchCondition();
		return sc;
	}

	/**
	 * 根据class name创建search condition 不查询已废弃对象
	 * 
	 * @param className
	 * @param folder
	 * @param hasSubFolders
	 * @return
	 */
	public static SearchCondition createSearchCondition4Class(String className, Folder folder, boolean hasSubFolders)
	{
		SearchCondition searchCondition = createSearchCondition(new ObjectGuid(className, null, null), folder, hasSubFolders);

		// searchCondition.addFilter(SystemClassFieldEnum.STATUS, SystemStatusEnum.OBSOLETE, OperateSignEnum.NOTEQUALS);

		return searchCondition;
	}

	/**
	 * 根据class name创建search condition 用于查找已废弃的对象
	 * 
	 * @param className
	 * @param folder
	 * @param hasSubFolders
	 * @return
	 */
	public static SearchCondition createSearchCondition4ClassByObsolete(String className, Folder folder, boolean hasSubFolders)
	{
		SearchCondition searchCondition = createSearchCondition(new ObjectGuid(className, null, null), folder, hasSubFolders);

		searchCondition.addFilter(SystemClassFieldEnum.STATUS, SystemStatusEnum.OBSOLETE, OperateSignEnum.EQUALS);

		return searchCondition;
	}

	public static SearchCondition createSearchCondition4File()
	{
		SearchCondition sc = createSearchCondition();
		return sc;
	}

	/**
	 * 创建全局查找的搜索条件<br>
	 * 不查询已废弃对象
	 * 
	 * @param boInfo
	 * @param folder
	 * @param hasSubFolders
	 * @return
	 */
	public static SearchCondition createSearchCondition4GlobalSearch(BOInfo boInfo, Folder folder, boolean hasSubFolders)
	{
		SearchCondition searchCondition = null;
		if (boInfo == null)
		{
			searchCondition = createSearchCondition(new ObjectGuid(null, null, null), folder, hasSubFolders);

		}
		else
		{
			searchCondition = createSearchCondition(new ObjectGuid(boInfo.getClassGuid(), boInfo.getClassName(), null, null), folder, hasSubFolders);
		}
		searchCondition.setIncludeOBS(false);
		return searchCondition;
	}

	/**
	 * 创建全局查找的搜索条件 只查已废弃对象<br>
	 * boInfo为空, 则folder必须为个人文件夹
	 * 
	 * @param boInfo
	 * @param folder
	 * @param hasSubFolders
	 * @return
	 */
	public static SearchCondition createSearchCondition4GlobalSearchByObsolete(BOInfo boInfo, Folder folder, boolean hasSubFolders)
	{
		SearchCondition searchCondition = null;
		if (boInfo == null)
		{
			searchCondition = createSearchCondition(new ObjectGuid(null, null, null), folder, hasSubFolders);
		}
		else
		{
			searchCondition = createSearchCondition(new ObjectGuid(boInfo.getClassGuid(), boInfo.getClassName(), null, null), folder, hasSubFolders);
		}
		searchCondition.setIncludeOBS(true);

		return searchCondition;
	}

	/**
	 * 供public里面的简单查询
	 * 
	 * @param boInfo
	 * @param folder
	 * @param hasSubFolders
	 * @param searchKey
	 * @return
	 */
	public static SearchCondition createSearchCondition4GlobalSearchSimple(BOInfo boInfo, Folder folder, boolean hasSubFolders, String searchKey)
	{
		SearchCondition searchCondition = null;

		if (boInfo != null)
		{
			searchCondition = createSearchCondition(new ObjectGuid(boInfo.getClassGuid(), boInfo.getClassName(), null, null), folder, hasSubFolders);
			searchCondition.setIncludeOBS(false);
			if (!StringUtils.isNullString(searchKey))
			{
				OperateSignEnum operateSignEnum = OperateSignEnum.CONTAIN;
				if (searchKey.contains("?") || searchKey.contains("*"))
				{
					operateSignEnum = OperateSignEnum.LIKE_REGEX;
				}

				searchCondition.addFilter(SystemClassFieldEnum.ID.getName(), searchKey, operateSignEnum);
				searchCondition.addFilterWithOR(SystemClassFieldEnum.NAME.getName(), searchKey, operateSignEnum);
				searchCondition.addFilterWithOR(SystemClassFieldEnum.ALTERID.getName(), searchKey, operateSignEnum);
			}
		}
		return searchCondition;
	}

	/**
	 * 根据mail分类, 创建查询mail的search condtion
	 * 
	 * @param cate
	 * @return
	 */
	public static SearchCondition createSearchCondition4MailCategory(MailCategoryEnum cate)
	{
		SearchCondition sc = new SearchConditionImpl();
		sc.addFilter(Mail.CATEGORY, cate.ordinal() + 1, OperateSignEnum.EQUALS);
		return sc;
	}

	/**
	 * 创建查询mail的search condition
	 * 
	 * @param hasRead
	 *            true是查找已读信件，false是未读信件, null是所有
	 * @param hasCategory
	 *            true是有分类的, false是没有类别的, null是类别任意
	 * @return
	 */
	public static SearchCondition createSearchCondition4MailInbox(Boolean hasRead, Boolean hasCategory)
	{
		SearchCondition sc = new SearchConditionImpl();
		String read = BooleanUtils.getBooleanStringYN(hasRead);
		if (!StringUtils.isNullString(read))
		{
			sc.addFilter(Mail.IS_READ, read, OperateSignEnum.EQUALS);
		}
		sc.addFilter(Mail.HAS_CATEGORY, hasCategory, OperateSignEnum.EQUALS);
		return sc;
	}

	/**
	 * 创建查询发出邮件的condition
	 * 
	 * @return
	 */
	public static SearchCondition createSearchCondition4MailSent()
	{
		return createSearchCondition();
	}

	/**
	 * 创建查询Search(查询的历史记录)的search condition
	 * 
	 * @param isUserSaved
	 *            true是查找是用户保存的‘检索历史记录’,false是查找非用户保存的‘检索历史记录’, null是所有
	 * @param searchTime
	 *            检索时间<一个Date类型数据就行,不用格式化,可以传null>
	 * @return
	 */
	public static SearchCondition createSearchCondition4SearchHistory(Boolean isUserSaved, Date searchTime)
	{
		SearchCondition sc = new SearchConditionImpl();

		if (isUserSaved != null)
		{
			if (isUserSaved)
			{
				// sc.startGroup();
				sc.addFilter(Search.TYPE, SearchTypeEnum.USER.getValue(), OperateSignEnum.EQUALS);
				// sc.addFilterWithOR(Search.TYPE, SearchTypeEnum.PUBLIC.getValue(), OperateSignEnum.EQUALS);
				// sc.endGroup();
			}
			else
			{
				sc.addFilter(Search.TYPE, SearchTypeEnum.AUTO.getValue(), OperateSignEnum.EQUALS);
			}
		}

		sc.addFilter(Search.UPDATE_TIME, searchTime, OperateSignEnum.EQUALS);

		return sc;
	}

	/**
	 * 创建查询Search(查询的历史记录)的search condition
	 * 
	 * @param isUserSaved
	 *            true是查找是用户保存的‘检索历史记录’,false是查找非用户保存的‘检索历史记录’, null是所有
	 * @param searchTime
	 *            检索时间<一个Date类型数据就行,不用格式化,可以传null>
	 * @return
	 */
	public static SearchCondition createSearchCondition4SearchHistoryType(SearchTypeEnum type, Date searchTime)
	{
		SearchCondition sc = new SearchConditionImpl();
		if (type != null)
		{
			sc.addFilter(Search.TYPE, type.getValue(), OperateSignEnum.EQUALS);
		}

		sc.addFilter(Search.UPDATE_TIME, searchTime, OperateSignEnum.EQUALS);

		return sc;
	}

	/**
	 * 创建查询订阅夹内容的search condition
	 * 
	 * @return
	 */
	public static SearchCondition createSearchCondition4Subscription()
	{
		SearchCondition sc = createSearchCondition();
		sc.setHasSubFolders(false);
		return sc;
	}

	public static SearchCondition createSearchCondition4WorkingItem()
	{
		SearchCondition sc = createSearchCondition();

		return sc;
	}

	/**
	 * 为处理Structure查询字段 而创建不包含任何条件的search condition
	 * 
	 * @return
	 */
	public static SearchCondition createSearchConditionForStructure(String struClassName)
	{
		SearchCondition sc = new SearchConditionImpl(new ObjectGuid(struClassName, null, null), null, false);
		return sc;
	}

	/**
	 * 为查询树状或者列表状BOMStructure 构造 search condition
	 * 
	 * @param struClassName
	 * @param uiObjectList
	 *            需要返回的UI上的字段
	 * @return
	 */
	public static SearchCondition createSearchConditionForBOMStructure(String struClassName, List<UIObjectInfo> uiObjectList)
	{
		SearchCondition sc = new SearchConditionImpl(new ObjectGuid(struClassName, null, null), null, false);
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObject : uiObjectList)
			{
				sc.addResultUIObjectName(uiObject.getName());
			}
		}
		return sc;
	}

	/**
	 * 根据提供参数创建search condition 供库统计时的数据检索
	 * 
	 * @param folder
	 *            要统计的库
	 * @param statisticResult
	 *            简单统计后的结果
	 * @return
	 */
	public static SearchCondition createSearchCondition4LibStatisticSearch(Folder library, StatisticResult statisticResult)
	{
		SearchCondition sc = new SearchConditionImpl(new ObjectGuid(), library, true);
		StatisticResultEnum statisticResultEnum = statisticResult.getType();
		if (statisticResultEnum.equals(StatisticResultEnum.BUSINESSOBJECT))
		{
			if (statisticResult.getBMInfo() != null)
			{
				sc.addFilter(SearchCondition.BMGUID, statisticResult.getBMInfo().getGuid(), OperateSignEnum.EQUALS);
			}
			else if (statisticResult.getBusinessObject() != null)
			{
				ObjectGuid objectGuid = new ObjectGuid();
				objectGuid.setBizObjectGuid(statisticResult.getBusinessObject().getGuid());
				sc = new SearchConditionImpl(objectGuid, library, true);
			}
		}
		else if (statisticResultEnum.equals(StatisticResultEnum.CLASSIFICATION))
		{
			if (statisticResult.getCodeObjectInfo() != null && statisticResult.getClassification() == null)
			{
				sc.addFilter(SearchCondition.CLASSIFICATION_MASTER_GUID, statisticResult.getCodeObjectInfo().getGuid(), OperateSignEnum.EQUALS);
			}
			else
			{
				sc.setClassificationList(Arrays.asList(statisticResult.getClassification().getGuid()));
			}
		}
		else if (statisticResultEnum.equals(StatisticResultEnum.GROUP))
		{
			sc.addFilter(SystemClassFieldEnum.OWNERGROUP.getName(), statisticResult.getGroup().getGuid(), OperateSignEnum.EQUALS);
		}
		else if (statisticResultEnum.equals(StatisticResultEnum.STATUS))
		{
			sc.addFilter(SystemClassFieldEnum.STATUS.getName(), statisticResult.getSystemStatusEnum().toString(), OperateSignEnum.EQUALS);
		}
		else if (statisticResultEnum.equals(StatisticResultEnum.FOLDER))
		{
			sc.setFolder(statisticResult.getFolder());
		}
		return sc;
	}

	public static SearchCondition createSearchCondition4MulitClassSearch(List<String> limitClassNameList)
	{
		SearchCondition sc = createSearchCondition();
		sc.setLimitClassList(limitClassNameList);
		return sc;
	}

	public static SearchCondition createSearchCondition4ViewHistory()
	{
		SearchCondition sc = createSearchCondition();
		return sc;
	}
}
