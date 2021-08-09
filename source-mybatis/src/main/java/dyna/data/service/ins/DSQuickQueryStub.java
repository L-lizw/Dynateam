package dyna.data.service.ins;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.bmbo.BusinessObject;
import dyna.common.dto.Session;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 普通实例查询
 * 
 * @author JiangHL
 */
@Repository
public class DSQuickQueryStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	@Autowired
	private FoundationObjectMapper              foundationObjectMapper;

	/**
	 * 一般情况请不要使用该查询，目前仅在产品配置材料明细结构中使用（因为模板可以关联多接口的子阶业务对象，所以需要跨表查询）,需要根据模板进行查询，只返回有限的几个系统字段,且只做普通对象查询，不做关联查询
	 * 
	 * @param searchKey
	 * @param rowCntPerPage
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public List<FoundationObject> quickQuery(String searchKey, int rowCntPerPage, int pageNum, boolean caseSensitive, boolean isEquals, boolean isOnlyId, List<String> boNameList,
			boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		if (SetUtils.isNullList(boNameList))
		{
			return null;
		}

		Map<String, List<String>> tableOfClassMap = new HashMap<>();
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		for (String end2BOName : boNameList)
		{
			BusinessObject businessModel = DataServer.getBusinessModelService().getBusinessObjectByName(session.getBizModelName(), end2BOName);
			String end2ClassName = businessModel.getClassName();
			String tableName = DataServer.getDSCommonService().getTableName(end2ClassName);
			if (!tableOfClassMap.containsKey(tableName))
			{
				tableOfClassMap.put(tableName, new ArrayList<>());
			}
			if (!tableOfClassMap.get(tableName).contains(end2ClassName))
			{
				tableOfClassMap.get(tableName).add(end2ClassName);
			}

			List<String> allSubClassList = DataServer.getDSCommonService().getAllSubClass(end2ClassName);
			if (!SetUtils.isNullList(allSubClassList))
			{
				for (String subClass : allSubClassList)
				{
					tableName = DataServer.getDSCommonService().getTableName(subClass);
					if (!tableOfClassMap.containsKey(tableName))
					{
						tableOfClassMap.put(tableName, new ArrayList<>());
					}
					if (!tableOfClassMap.get(tableName).contains(subClass))
					{
						tableOfClassMap.get(tableName).add(subClass);
					}
				}
			}
		}

		List<String> tableNameList = new ArrayList<>();
		List<String> classGuidList = new ArrayList<>();
		for (String tableName : tableOfClassMap.keySet())
		{
			if (!tableNameList.contains(tableName))
			{
				tableNameList.add(tableName);
			}

			List<String> end2ClassNameList = tableOfClassMap.get(tableName);
			for (String end2ClassName : end2ClassNameList)
			{
				String end2ClassGuid = DataServer.getClassModelService().getClassObject(end2ClassName).getGuid();
				if (!classGuidList.contains(end2ClassGuid))
				{
					classGuidList.add(end2ClassGuid);
				}
			}
		}

		// 共有文件夹下数据、库下数据
		String where = getSqlWhereQuickSearch(searchKey, isOnlyId, caseSensitive, isEquals);

		List<FoundationObject> selectResultList = this.quickList(null, tableNameList, classGuidList, where, pageNum, rowCntPerPage);

		if (SetUtils.isNullList(selectResultList))
		{
			return selectResultList;
		}

		if (!isCheckAcl)
		{
			return selectResultList;
		}

		for (FoundationObject foundationObject : selectResultList)
		{
			boolean hasAuth = DataServer.getAclService().hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId);
			foundationObject.getObjectGuid().setHasAuth(hasAuth);
		}

		return selectResultList;
	}

	/**
	 * 数据层用，只查询对象的接口字段,只支持简单sql查询
	 * 
	 * @param searchKey
	 * @param isOnlyId
	 *            过滤条件集合
	 * @return
	 */
	private String getSqlWhereQuickSearch(String searchKey, boolean isOnlyId, boolean caseSensitive, boolean isEquals)
	{
		String sqlWhere;
		if (isOnlyId)
		{
			if (isEquals)
			{
				if (caseSensitive)
				{
					sqlWhere = " f$.id = ?";
				}
				else
				{
					sqlWhere = " upper(f$.id) = upper(?)";
				}
			}
			else
			{
				if (caseSensitive)
				{
					sqlWhere = " f$.id like '%' || ? || '%'";
				}
				else
				{
					sqlWhere = " upper(f$.id) like '%' || upper(?) || '%'";
				}
			}
		}
		else
		{
			if (isEquals)
			{
				if (caseSensitive)
				{
					sqlWhere = " (f$.id = ? or f$.name = ? or f$.alterid = ?)";
				}
				else
				{
					sqlWhere = " (upper(f$.id) = upper(?) or upper(f$.name) = upper(?) or upper(f$.alterid) = upper(?))";
				}
			}
			else
			{
				if (caseSensitive)
				{
					sqlWhere = " (f$.id like '%' || ? || '%' or f$.name like '%' || ? || '%' or f$.alterid like '%' || ? || '%')";
				}
				else
				{
					sqlWhere = " (upper(f$.id) like '%' || upper(?) || '%' or upper(f$.name) like '%' || upper(?) || '%' or upper(f$.alterid) like '%' || upper(?) || '%')";
				}
			}
		}
		return sqlWhere;
	}

	/**
	 * 快速搜索，用在客户端进行快速搜索时，可以搜索多个类的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<FoundationObject> quickList(String fields, List<String> tableList, List<String> classGuidList, String where, int pageNum, int pageSize)
	{
		if (!StringUtils.isNullString(where))
		{
			where = " (" + where + ") ";
		}
		else
		{
			where = " (1=1) ";
		}

		if (SetUtils.isNullList(tableList) || SetUtils.isNullList(classGuidList))
		{
			return null;
		}

		StringBuilder classGuidBuffer = new StringBuilder();
		for (String classGuid : classGuidList)
		{
			if (classGuidBuffer.length() > 0)
			{
				classGuidBuffer.append(",");
			}
			classGuidBuffer.append("'").append(classGuid).append("'");
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("TABLES", tableList);
		paramMap.put("FIELDS", StringUtils.isNullString(fields) ? "" : ("," + fields));
		paramMap.put("CLASSGUIDS", classGuidBuffer.toString());
		paramMap.put("WHERESQL", where);
		paramMap.put("PAGE", pageNum);
		paramMap.put("ROWCOUNT$", pageSize);
		try
		{
			int dataCount = this.foundationObjectMapper.selectCount(paramMap);

			if (dataCount == 0)
			{
				return null;
			}

			List<FoundationObject> list = this.foundationObjectMapper.selectMultiClassQuickSearch(paramMap);
//			List<FoundationObject> list = this.foundationObjectMapper.queryForList(FoundationObject.class.getName() + ".selectMultiClassQuickSearch", paramMap, pageNum, pageSize);
			for (FoundationObject foundationObject_ : list)
			{
				foundationObject_.put("ROWCOUNT$", dataCount);
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query search error.", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}

	/**
	 * 返回指定时间点的最新发布版本对象
	 * 
	 * @param objectGuid
	 * @param ruleTime
	 * @param isRLS
	 * @return
	 */
	public FoundationObject queryByTime(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		Map<String, String> param = new HashMap<>();
		param.put("tablename", DataServer.getDSCommonService().getTableName(objectGuid.getClassGuid()));
		param.put("RULETIME", DateFormat.formatYMD(ruleTime));
		param.put("MASTERFK", objectGuid.getMasterGuid());
		try
		{
//			return (FoundationObject) this.foundationObjectMapper.selectByTime(param);
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query FoundationObject error.", null, DataExceptionEnum.SDS_SELECT, "");
		}
	}
}
