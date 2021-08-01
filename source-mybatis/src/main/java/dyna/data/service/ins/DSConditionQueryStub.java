/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 普通实例查询
 * JiangHL 2011-5-10
 */
package dyna.data.service.ins;

import dyna.common.SearchCondition;
import dyna.common.SearchConditionImpl;
import dyna.common.bean.data.DynaObjectImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.data.sqlbuilder.AdvanceQuerySqlBuilder;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 普通实例查询
 * 
 * @author JiangHL
 */
public class DSConditionQueryStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	@Autowired
	private FoundationObjectMapper              foundationObjectMapper;
	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public DSConditionQueryStub(DataServerContext context, InstanceServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	/**
	 * 
	 * @param searchCondition
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public List<FoundationObject> query(SearchCondition searchCondition, boolean isNonQueryable, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{

		SearchConditionImpl searchConditionImpl = (SearchConditionImpl) searchCondition;

		DataServer.getDSCommonService().decorateClass(searchConditionImpl);
		String className = searchConditionImpl.getObjectGuid().getClassName();
		String classGuid = searchConditionImpl.getObjectGuid().getClassGuid();

		// 判断类是否能够被查询
		DataServer.getDSCommonService().getTableName(searchConditionImpl.getObjectGuid().getClassGuid());

		String foType = "1";
		boolean isHierarchy = true;
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject.hasInterface(ModelInterfaceEnum.IViewObject))
		{
			foType = "5";
		}
		else if (classObject.hasInterface(ModelInterfaceEnum.IBOMView))
		{
			foType = "3";
		}
		if ("1".equals(foType) && !classObject.isAbstract())
		{
			isHierarchy = false;
		}

		if (classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup) || classObject.hasInterface(ModelInterfaceEnum.IPMRole)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			return new ArrayList<>();
		}

		Folder folder = searchConditionImpl.getFolder();
		FolderTypeEnum folderTypeEnum = null;
		if (folder != null)
		{
			folderTypeEnum = folder.getType();
		}

		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();

		List<FoundationObject> selectResultList = null;
		Map<String, String> classificationFieldMap = new HashMap<>();
		// 是否要过滤特殊条件 如果为false 将不考虑条件中的是否包含废弃数据（目前单条数据查询时，废弃的也要查出来）
		if (!StringUtils.isNullString(searchConditionImpl.getObjectGuid().getGuid()))
		{
			// 按照guid查询单条数据
			throw new DynaDataExceptionAll("Please Use get method.", null, DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT);
		}
		else if (searchConditionImpl.getObjectGuid().getMasterGuid() != null)
		{
			// 如果master is not null，查询当前master的最新版数据
			throw new DynaDataExceptionAll("Please Use get method.", null, DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT);
		}

		DynamicSelectParamData paramData = AdvanceQuerySqlBuilder.buildInstanceSearchParamData(searchConditionImpl, sessionId, classificationFieldMap);

		// 如果是数据导入的查询需要特别处理，不用判断权限和范围
		if (searchCondition.isImport())
		{
			selectResultList = this.query(classGuid, null, true, false, null, foType, searchCondition.getPageNum(), paramData);
		}
		// view
		else if (foType.equals("3") || foType.equals("5"))
		{
			if (AdvancedQueryTypeEnum.CLASSIFICATION == searchConditionImpl.getSearchType())
			{
				if (!StringUtils.isGuid(searchConditionImpl.getClassification()))
				{
					return null;
				}
			}

			SearchRevisionTypeEnum revisionType = SearchRevisionTypeEnum.typeValueOf(searchConditionImpl.getSearchRevisionType());
			selectResultList = this.query(classGuid, null, isHierarchy, false, revisionType, foType, searchCondition.getPageNum(), paramData);
		}
		// 共有文件夹下数据、库下数据
		else if (folderTypeEnum == FolderTypeEnum.LIB_FOLDER || folderTypeEnum == FolderTypeEnum.LIBRARY)
		{
			if (AdvancedQueryTypeEnum.CLASSIFICATION == searchConditionImpl.getSearchType())
			{
				if (!StringUtils.isGuid(searchConditionImpl.getClassification()))
				{
					return null;
				}
			}
			SearchRevisionTypeEnum revisionType = SearchRevisionTypeEnum.typeValueOf(searchConditionImpl.getSearchRevisionType());
			selectResultList = this.query(classGuid, searchConditionImpl.getFolder().getGuid(), isHierarchy, !searchConditionImpl.hasSubFolders(), revisionType, foType,
					searchCondition.getPageNum(), paramData);
		}
		// 查询版本序列
		else if (folderTypeEnum == null)
		{
			// 查询最新版
			// 查询最新版
			SearchRevisionTypeEnum revisionTypeEnum = SearchRevisionTypeEnum.typeValueOf(searchConditionImpl.getSearchRevisionType());
			selectResultList = this.query(classGuid, null, isHierarchy, false, revisionTypeEnum, foType, searchCondition.getPageNum(), paramData);
		}

		if (SetUtils.isNullList(selectResultList))
		{
			return selectResultList;
		}

		// 把分类数据从column做key，重组为由field做key。
		List<FoundationObject> resultList = this.rebuildClassificationDataList(selectResultList, classificationFieldMap);

		if (isCheckAcl)
		{
			AclService aclService = DataServer.getAclService();
			for (FoundationObject foundationObject : resultList)
			{
				boolean hasAuth = DataServer.getAclService().hasFolderAuthority(foundationObject.getCommitFolderGuid(), FolderAuthorityEnum.READ, userGuid,
						session.getLoginGroupGuid(), session.getLoginRoleGuid());
				if (hasAuth)
				{
					hasAuth = aclService.hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId);
				}
				foundationObject.getObjectGuid().setHasAuth(hasAuth);
			}
		}

		return resultList;
	}

	@SuppressWarnings("unchecked")
	private List<FoundationObject> query(String classGuid, String folderGuid, boolean ishierarchy, boolean isOnlyCurrentFolder, SearchRevisionTypeEnum revisionTypeEnum,
			String foType, int pageNum, DynamicSelectParamData paramData) throws ServiceRequestException
	{
		String whereSql = paramData.getWhereSql();
		// 查询当前类以及其子类的数据
		if (ishierarchy)
		{
			whereSql = whereSql + " and exists (select 1 from ma_treedata_relation cr where f.classguid = cr.subdataguid and cr.datatype = 'CLASS' and cr.dataguid = '" + classGuid
					+ "') ";
		}
		else
		{
			whereSql = whereSql + " and f.classguid = '" + classGuid + "' ";
		}

		// 最新版本
		if (revisionTypeEnum != null)
		{
			if (revisionTypeEnum == SearchRevisionTypeEnum.ISLATESTONLY)
			{
				whereSql = whereSql + " and f.latestrevision like 'm%'";
			}
			// 查询最新发布版本
			else if (revisionTypeEnum == SearchRevisionTypeEnum.ISLATESTRLSONLY)
			{
				whereSql = whereSql + " and f.latestrevision like '%r%'";
			}
			// 最新版本为发布版本
			else if (revisionTypeEnum == SearchRevisionTypeEnum.ISLATESTRLSWIP)
			{
				whereSql = whereSql + " and f.latestrevision = 'mr'";
			}
		}

		if (StringUtils.isGuid(folderGuid))
		{
			if (!isOnlyCurrentFolder)
			{
				whereSql = whereSql + " and exists (select 1 from ma_treedata_relation fr where f.commitfolder = fr.subdataguid and fr.datatype = 'FOLDER' and fr.dataguid = '"
						+ folderGuid + "') ";
			}
			else
			{
				whereSql = whereSql + " and f.commitfolder = '" + folderGuid + "' ";
			}
		}

		paramData.setWhereSql(whereSql);

		String orderBy = paramData.getOrderSql();
		if (StringUtils.isNullString(orderBy))
		{
			orderBy = "f.updatetime desc";
		}

		// 排序字段相同时，增加按id排序
		if (!orderBy.toLowerCase().contains("md_id"))
		{
			orderBy = orderBy + ", f.md_id asc";
		}
		paramData.setOrderSql(orderBy);

		// 取得记录数
		int dataCount = -1;
		if (!"3".equals(foType) && !"5".equals(foType) && pageNum < 2)
		{
			dataCount = 0;
			dataCount = DataServer.getDSCommonService().getRecordCount(paramData);
			if (dataCount == 0)
			{
				return null;
			}
		}

		SqlSession sqlSession = null;
		try
		{
			List<FoundationObject> resultList = null;
			if (dataCount != 0)
			{
				resultList = (List<FoundationObject>) this.foundationObjectMapper.selectDynamic(paramData);
//				resultList = this.sqlSessionFactory.queryForList(FoundationObject.class.getName() + ".selectDynamic", paramData, paramData.getCurrentPage(), paramData.getRowsPerPage());
				if (!SetUtils.isNullList(resultList) && dataCount > 0)
				{
					for (FoundationObject foundationObject : resultList)
					{
						foundationObject.put("ROWCOUNT$", dataCount);
					}
				}
			}
			return resultList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query error.", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
		finally
		{
			if(sqlSession!=null)
			{
				sqlSession.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<FoundationObject> query(String className, List<String> guidList, String sessionId) throws ServiceRequestException
	{
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject == null || classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMRole) || classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			return new ArrayList<>();
		}
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("tablename", DataServer.getDSCommonService().getTableName(className));
		SqlSession sqlSession = null;
		try
		{

			List<FoundationObject> resultList = new ArrayList<>();
			int mod = (int) Math.ceil((double) guidList.size() / (double) 70);
			for (int i = 0; i < mod; i++)
			{
				List<String> inGuidList = new ArrayList<>();
				int endIndex = (i + 1) * 70;
				if (guidList.size() < endIndex)
				{
					endIndex = guidList.size();
				}
				for (int j = i * 70; j < endIndex; j++)
				{
					inGuidList.add(guidList.get(j));
				}
				paraMap.put("GUIDLIST", inGuidList);
				FoundationObjectMapper foundationObjectMapper = sqlSession.getMapper(FoundationObjectMapper.class);
				List<FoundationObject> tempList = foundationObjectMapper.listFoundationByGuid(paraMap);
				if (tempList != null)
				{
					resultList.addAll(tempList);
				}
			}
			return resultList;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("query FoundationObject error.", null, DataExceptionEnum.SDS_SELECT);
		}
		finally
		{
			if(sqlSession!=null)
			{
				sqlSession.close();
			}
		}

	}

	protected List<FoundationObject> rebuildClassificationDataList(List<FoundationObject> selectResultList, Map<String, String> classificationFieldMap)
	{
		if (!SetUtils.isNullMap(classificationFieldMap))
		{
			for (FoundationObject fo : selectResultList)
			{
				for (Entry<String, String> entry : classificationFieldMap.entrySet())
				{
					String columnKey = entry.getKey().substring(0, entry.getKey().lastIndexOf("#"));
					String fieldType = entry.getKey().substring(entry.getKey().lastIndexOf("#") + 1);
					String fieldKey = entry.getValue();
					FieldTypeEnum fieldTypeEnum = FieldTypeEnum.typeof(fieldType);

					if (fieldTypeEnum == FieldTypeEnum.OBJECT)
					{
						String[] arrs = { "$id", "$name", "__id", "__name", "$class", "$master", "$ismaster", "__alid", "__reid", "__itid", "__clfi", "__fina" };
						for (String key : arrs)
						{
							fo.put(fieldKey + key, fo.get(columnKey + key));
							((Map<?, ?>) fo).remove((columnKey + key).toUpperCase());
							((DynaObjectImpl) fo).getOriginalMap().remove((columnKey + key).toUpperCase());
						}
					}

					fo.put(fieldKey, fo.get(columnKey));
					((Map<?, ?>) fo).remove((columnKey).toUpperCase());
					((DynaObjectImpl) fo).getOriginalMap().remove((columnKey).toUpperCase());
				}
			}
			return selectResultList;
		}
		return selectResultList;
	}

}
