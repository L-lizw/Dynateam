/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SDSDeleteDataUnit
 * ZhangHW 2011-7-8
 */
package dyna.data.service.sdm;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dtomapper.DynaObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;

/**
 * 系统数据服务 对数据的相关操作（增、删、改、查等）
 *
 * @author ZhangHW
 */
public class SDSOperateDataStub extends DSAbstractServiceStub<SystemDataServiceImpl>
{
	private static final String	INSERT_STATEMENT			= "insert";
	private static final String	UPDATE_STATEMENT			= "update";
	private static final String	DELETE_STATEMENT			= "delete";
	private static final String	DELETE_STATEMENT_ADVANCED	= "deleteAdvanced";
	private static final String	SELECT_STATEMENT			= "select";

	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public SDSOperateDataStub(DataServerContext context, SystemDataServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	/**
	 * 保存一条记录（根据是否有guid来进行insert或update)
	 *
	 * @param systemObject
	 * @return guid of the saved record or affected row count.
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject) throws DynaDataException
	{
		return this.save(systemObject, null);
	}

	/**
	 * 根据sqlStatementId保存一条记录（根据是否有guid来进行insert或update)
	 *
	 * @param <T>
	 * @param systemObject
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject, String sqlStatementId) throws DynaDataException
	{
		return this.save(systemObject, sqlStatementId, true);
	}

	/**
	 * 根据sqlStatementId保存一条记录（根据是否有guid来进行insert或update)
	 *
	 * @param <T>
	 * @param systemObject
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject, String sqlStatementId, boolean isUpdate) throws DynaDataException
	{
		String className = systemObject.getClass().getName();
		Class mapperClass = this.stubService.getEntryMapperMap().get(systemObject.getClass());
		Object mapperBean = this.serviceContext.getApplicationContext().getBean(mapperClass);
		Method method = null;
		try
		{
			String guid = systemObject.getGuid();
			if (isUpdate)
			{
				isUpdate = StringUtils.isGuid(guid);
			}

			if (isUpdate)
			{
				if (sqlStatementId == null)
				{
					sqlStatementId = UPDATE_STATEMENT;
				}
				method = mapperClass.getMethod(sqlStatementId);
				int ret = (int) method.invoke(systemObject);
				return String.valueOf(ret);
			}
			else
			{// insert
				if (systemObject instanceof DSSFileInfo)
				{
					guid = StringUtils.generateRandomUID(32).toUpperCase();
					if ("insertWfFile".equalsIgnoreCase(sqlStatementId))
					{
						guid = "C" + guid.substring(1);
					}
					else if ("insertAnyTableFile".equalsIgnoreCase(sqlStatementId))
					{
						guid = "A" + guid.substring(1);
					}
					else
					{
						guid = "F" + guid.substring(1);
					}
					systemObject.put("GUID", guid);
					if (sqlStatementId == null)
					{
						sqlStatementId = INSERT_STATEMENT;
					}
					method = mapperClass.getMethod(sqlStatementId);
					method.invoke(systemObject);

					systemObject.clear("GUID");

					return guid;
				}
				else
				{
					if (sqlStatementId == null)
					{
						sqlStatementId = INSERT_STATEMENT;
					}
					method = mapperClass.getMethod(sqlStatementId);
					method.invoke(systemObject);
					return systemObject.getGuid();
				}
			}
		}
		catch (Exception e)
		{
			if (isUpdate)
			{
				throw new DynaDataExceptionAll("save() class = " + className + " record = " + systemObject, e, DataExceptionEnum.SDS_SAVE);
			}
			else
			{
				throw new DynaDataExceptionAll("save() class = " + className + " record = " + systemObject, e, DataExceptionEnum.SDS_INSERT);
			}
		}
	}

	/**
	 * 根据sqlStatementId保存一条记录（根据是否有guid来进行insert或update)
	 *
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public String save(Map<String, Object> parameterMap, String sqlStatementId, boolean isSaveDynamic) throws DynaDataException
	{
		try
		{
			parameterMap.put("CURRENTTIME", new Date());

			Method method = null;
			if (isSaveDynamic)
			{
				if (sqlStatementId == null)
				{
					sqlStatementId = UPDATE_STATEMENT;
				}

				method = DynaObjectMapper.class.getMethod(sqlStatementId);

				int ret = (int) method.invoke(this.dynaObjectMapper, parameterMap);

				parameterMap.remove("CURRENTTIME");

				return String.valueOf(ret);
			}
			else
			{// insert

				if (sqlStatementId == null)
				{
					sqlStatementId = INSERT_STATEMENT;
				}
				method = DynaObjectMapper.class.getMethod(sqlStatementId);
				return (String) method.invoke(this.dynaObjectMapper, parameterMap);
			}
		}
		catch (Exception e)
		{
			if (isSaveDynamic)
			{
				throw new DynaDataExceptionAll("save() dynamic :" + sqlStatementId + "." + parameterMap, e, DataExceptionEnum.SDS_SAVE);
			}
			else
			{
				throw new DynaDataExceptionAll("save() dynamic :" + sqlStatementId + "." + parameterMap, e, DataExceptionEnum.SDS_INSERT);
			}
		}
	}

	/**
	 * 根据insertId新建一条记录 根据updateId更新一条记录
	 *
	 * @param <T>
	 * @param systemObject
	 * @param insertId
	 * @param updateId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject, String insertId, String updateId) throws DynaDataException
	{
		String className = systemObject.getClass().getName();
		String guid = systemObject.getGuid();
		boolean isUpdate = StringUtils.isGuid(guid);

		try
		{
			Class mapperCLass = this.serviceContext.getEntryMapperMap().get(systemObject.getClass());
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperCLass);
			Method method = null;

			systemObject.put("CURRENTTIME", new Date());
			if (isUpdate)
			{
				if (updateId == null)
				{
					updateId =  UPDATE_STATEMENT;
				}

				method = mapperCLass.getMethod(updateId);
				int ret = (int) method.invoke(mapper, systemObject);

				systemObject.clear("CURRENTTIME");

				return String.valueOf(ret);
			}
			else
			{// insert
				if (systemObject instanceof DSSFileInfo)
				{
					guid = StringUtils.generateRandomUID(32).toUpperCase();
					if ("insertWfFile".equalsIgnoreCase(insertId))
					{
						guid = "C" + guid.substring(1);
					}
					else if ("insertAnyTableFile".equalsIgnoreCase(insertId))
					{
						guid = "A" + guid.substring(1);
					}
					else
					{
						guid = "F" + guid.substring(1);
					}
					systemObject.put("GUID", guid);
					if (insertId == null)
					{
						insertId = INSERT_STATEMENT;
					}

					method = mapperCLass.getMethod(insertId);

					method.invoke(mapper, systemObject);

					systemObject.clear("GUID");

					return guid;
				}
				else
				{
					if (insertId == null)
					{
						insertId = INSERT_STATEMENT;
					}

					method = mapperCLass.getMethod(insertId);

					return (String) method.invoke(mapper, systemObject);
				}
			}
		}
		catch (Exception e)
		{
			if (isUpdate)
			{
				throw new DynaDataExceptionAll("save() class = " + className + " record = " + systemObject, e, DataExceptionEnum.SDS_SAVE);
			}
			else
			{
				throw new DynaDataExceptionAll("save() class = " + className + " record = " + systemObject, e, DataExceptionEnum.SDS_INSERT);
			}
		}
	}

	/**
	 * 根据指定的搜索Map查询数据 （使用sql为classId.select sql)
	 *
	 * @param searchConditionMap
	 * @return records list
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap) throws DynaDataException
	{
		return this.query(systemClass, searchConditionMap, null, true);
	}

	/**
	 * 根据指定的搜搜索Map和sql statement语句查询数据使用sql为classId.sqlstatement)
	 *
	 * @param searchConditionMap
	 * @return records list
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap, String sqlStatementId) throws DynaDataException
	{
		String className = systemClass.getName();
		return this.select(className, searchConditionMap, null, true, sqlStatementId);
	}

	/**
	 * 根据指定的搜索Map查询数据并排序 （使用sql为classId.Select sql)
	 *
	 * @param searchConditionMap
	 * @param orderby
	 * @param isAscOrder
	 * @return records list
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap, String orderby, boolean isAscOrder) throws DynaDataException
	{
		String className = systemClass.getName();
		return this.select(className, searchConditionMap, orderby, isAscOrder, null);
	}

	/**
	 * 根据指定的搜索Map查询一条数据 （使用sql为classId.Select sql)
	 *
	 * @param searchConditionMap
	 * @return record object, or null if there is no search result or search result
	 *         length > 1
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> T queryObject(Class<T> systemClass, Map<String, Object> searchConditionMap) throws DynaDataException
	{
		return this.queryObject(systemClass, searchConditionMap, null);
	}

	/**
	 * 根据指定的搜索Map查询一条数据
	 *
	 * @param <T>
	 * @param systemClass
	 * @param searchConditionMap
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> T queryObject(Class<T> systemClass, Map<String, Object> searchConditionMap, String sqlStatementId) throws DynaDataException
	{
		List<T> result = this.query(systemClass, searchConditionMap, sqlStatementId);
		if (result == null || result.isEmpty())
		{
			return null;
		}
		else if (result.size() == 1)
		{
			return result.get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 根据指定的搜索Map查询一条数据
	 *
	 * @param <T>
	 * @param systemClass
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	public <T extends SystemObject> T getObject(Class<T> systemClass, String guid, String sqlStatementId) throws DynaDataException
	{
		try
		{

			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			if (sqlStatementId == null)
			{
				sqlStatementId = "get";
			}
			Method method = mapperClass.getMethod(sqlStatementId);
			List<T> result = (List<T>) method.invoke(mapper, guid);
			if (result == null || result.isEmpty())
			{
				return null;
			}
			else if (result.size() == 1)
			{
				return result.get(0);
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("select() selectStatement = " + sqlStatementId + " guid = " + guid, e, DataExceptionEnum.SDS_SELECT);
		}
	}

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 *
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int update(Class<T> systemClass, Map<String, Object> paramMap, String sqlStatementId) throws DynaDataException
	{

		Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
		Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);

		try
		{
			Method method = mapperClass.getMethod(sqlStatementId);
			paramMap.put("CURRENTTIME", new Date());
			int result = (int) method.invoke(mapper, paramMap);
			paramMap.remove("CURRENTTIME");
			return result;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("update() selectStatement = " + sqlStatementId, e, DataExceptionEnum.SDS_UPDATE);
		}
	}

	/**
	 * 根据传进来的参数删除记录 返回被删掉记录的数量
	 *
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int delete(Class<T> systemClass, Map<String, Object> paramMap) throws DynaDataException
	{
		String className = systemClass.getName();

		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			Method method = mapperClass.getMethod(DELETE_STATEMENT_ADVANCED);
			return (int) method.invoke(mapper, paramMap);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("delete() class = " + className + "; paramMap = " + paramMap, e, DataExceptionEnum.SDS_DELETE_PARAM);
		}
	}

	/**
	 * 根据传进来的参数和sql statement删除记录 返回被删掉记录的数量
	 *
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int delete(Class<T> systemClass, Map<String, Object> paramMap, String sqlStatementId) throws DynaDataException
	{
		String className = systemClass.getName();

		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			Method method = mapperClass.getMethod(sqlStatementId);
			return (int) method.invoke(mapper, paramMap);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("delete() class = " + className + "; paramMap = " + paramMap, e, DataExceptionEnum.SDS_DELETE_SQLSTATEMENTID);
		}
	}

	/**
	 * 根据guid删除记录
	 *
	 * @param systemClass
	 *            class to be deleted
	 * @param guid
	 * @return successful or not
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> boolean delete(Class<T> systemClass, String guid) throws DynaDataException
	{
		String className = systemClass.getName();

		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			Method method = mapperClass.getMethod(DELETE_STATEMENT);
			return (int)method.invoke(mapper, guid) == 1;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("delete() class = " + className + "guid = " + guid, e, DataExceptionEnum.SDS_DELETE_GUID);
		}
	}

	/**
	 * 删除记录
	 *
	 * @param systemObject
	 *            to be deleted
	 * @return successful or not
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> boolean delete(T systemObject) throws DynaDataException
	{
		return this.delete(systemObject.getClass(), systemObject.getGuid());
	}

	/**
	 * Execute a mapperper select sqlStatement with specified search condition, and
	 * record count limits
	 *
	 * @param selectStatement
	 * @param searchConditionMap
	 * @param isAscOrder
	 * @param orderby
	 * @param sqlStatementId
	 * @return ArrayList
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private <T extends SystemObject> List<T> select(String className, Map<String, Object> searchConditionMap, String orderby, boolean isAscOrder, String sqlStatementId)
			throws DynaDataException
	{
		if (searchConditionMap == null)
		{
			searchConditionMap = new HashMap<>();
		}

		String classGuid = (String) searchConditionMap.get("CLASSGUID");

		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(Class.forName(className));
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);

			if (StringUtils.isGuid(classGuid))
			{
				searchConditionMap.put("tablename", DataServer.getDSCommonService().getTableName(classGuid));
			}

			if (sqlStatementId == null)
			{
				sqlStatementId = className + "." + SELECT_STATEMENT;
			}

			searchConditionMap.put("CURRENTTIME", new Date());

			Integer rowsPerPage = (Integer) searchConditionMap.get("ROWSPERPAGE");
			Integer currentPage = (Integer) searchConditionMap.get("CURRENTPAGE");
			Method method = mapperClass.getMethod(sqlStatementId);

			List<T> result;
			if (rowsPerPage != null && currentPage != null)
			{
				PageHelper.startPage(currentPage, rowsPerPage);
				result = (List<T>) method.invoke(mapper, searchConditionMap);
			}
			else
			{
				result = (List<T>) method.invoke(mapper, searchConditionMap);
			}
			searchConditionMap.remove("CURRENTTIME");
			if (StringUtils.isGuid(classGuid))
			{
				searchConditionMap.remove("tablename");
			}
			return result;

		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("select() selectStatement = " + sqlStatementId + " searchConditionMap = " + searchConditionMap, e, DataExceptionEnum.SDS_SELECT);
		}
	}

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 *
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int insertBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException
	{
		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			Method method = mapperClass.getMethod(sqlStatementId);

			int returnValue = 0;
			if (SetUtils.isNullList(paramList))
			{
				for (Map<String, Object> paramMap : paramList)
				{
					paramMap.put("CURRENTTIME", new Date());
					returnValue += (int)method.invoke(mapper, paramMap);
					paramMap.remove("CURRENTTIME");
				}
			}
			return returnValue;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("updateBatch() selectStatement = " + sqlStatementId, e, DataExceptionEnum.SDS_UPDATE);
		}
	}

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 *
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int updateBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException
	{

		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			Method method = mapperClass.getMethod(sqlStatementId);
			int returnValue = 0;
			if (SetUtils.isNullList(paramList))
			{
				for (Map<String, Object> paramMap : paramList)
				{
					paramMap.put("CURRENTTIME", new Date());
					returnValue += (int)method.invoke(mapper, paramMap);
					paramMap.remove("CURRENTTIME");
				}
			}
			return returnValue;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("updateBatch() selectStatement = " + sqlStatementId, e, DataExceptionEnum.SDS_UPDATE);
		}
	}

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 *
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int deleteBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException
	{
		sqlStatementId = systemClass.getName() + "." + sqlStatementId;

		try
		{
			Class mapperClass = this.serviceContext.getEntryMapperMap().get(systemClass);
			Object mapper = this.serviceContext.getApplicationContext().getBean(mapperClass);
			Method method = mapperClass.getMethod(sqlStatementId);
			int returnValue = 0;
			if (SetUtils.isNullList(paramList))
			{
				for (Map<String, Object> paramMap : paramList)
				{
					paramMap.put("CURRENTTIME", new Date());
					returnValue += (int)method.invoke(mapper, paramMap);
					paramMap.remove("CURRENTTIME");
				}
			}
			return returnValue;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("deleteBatch() selectStatement = " + sqlStatementId, e, DataExceptionEnum.SDS_UPDATE);
		}
	}

}
