package dyna.app.service.brs.erpi.dblink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.bean.erp.ERPFieldMappingTypeEnum;
import dyna.common.dto.ErrorRecord;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class DBLinkStub
{
	// private static DBLinkStub dbLinkStub = null;

	private Connection	connect	= null;

	/**
	 * 获取当前类的实例对象
	 * 
	 * @return
	 */
	// public static DBLinkStub getInstance()
	// {
	// if(dbLinkStub == null)
	// {
	// dbLinkStub = new DBLinkStub();
	// }
	// return dbLinkStub;
	// }

	/**
	 * 通过url,用户名和密码连接中间库
	 * 
	 * @param url
	 *            数据库URL
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @throws ServiceRequestException
	 */
	public void connectDataBase(String url, String username, String password) throws ServiceRequestException
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			connect = DriverManager.getConnection(url, username, password);
		}
		catch (Exception e)
		{
			if (!StringUtils.isNullString(e.getMessage()))
			{
				if (e instanceof SQLException && e.getMessage().contains("ORA-01017"))
				{
					throw new ServiceRequestException("ID_DBLINK_CONNECT_DATABASE_ERROR", e.getMessage(), e);
				}
				else if (e instanceof SQLException && e.getMessage().contains("ORA-12514"))
				{
					throw new ServiceRequestException("ID_DBLINK_CONNECT_DATABASE_ERROR", e.getMessage(), e);
				}
				else
				{
					throw new ServiceRequestException(e.getMessage());
				}
			}
			else
			{
				throw new ServiceRequestException("connect temp database error!");
			}
		}
	}

	/**
	 * 查询错误记录表
	 * 
	 * @param jobId
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ErrorRecord> listErrorRecordInfo(String jobId) throws ServiceRequestException
	{
		List<ErrorRecord> errorRecrdList = null;
		List<String> columnList = new ArrayList<String>();
		columnList.add("plm_pk");// 任务号
		columnList.add("tast001");// 任务类型
		columnList.add("tast002");// 企业编号
		columnList.add("tast003");// 处理中间表
		columnList.add("tast004");// 主件料号
		columnList.add("tast005");// 元件料号
		columnList.add("tast006");// 错误描述
		String sql = "select " + this.concatStringWithComma(columnList) + " from plm_tastuc_t where plm_pk = '" + jobId + "'";
		try
		{
			Statement stmt = connect.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			if (resultSet != null)
			{
				errorRecrdList = new ArrayList<ErrorRecord>();
				while (resultSet.next())
				{
					ErrorRecord errorRecord = new ErrorRecord();
					errorRecord.setJobId(resultSet.getString(1));
					errorRecord.setTaskType(resultSet.getString(2));
					errorRecord.setCompanyId(resultSet.getString(3));
					errorRecord.setTempTable(resultSet.getString(4));
					errorRecord.setMasterItemId(resultSet.getString(5));
					errorRecord.setComponentItemId(resultSet.getString(6));
					errorRecord.setErrorDescription(resultSet.getString(7));
					errorRecrdList.add(errorRecord);
				}
			}
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			DynaLogger.error(e.getMessage(), e);
			throw new ServiceRequestException(e.getMessage());
		}
		return errorRecrdList;
	}

	/**
	 * 从中间库取资料
	 * 
	 * @param tableRecord
	 * @throws Exception
	 */
	public LinkedList<Map<String, String>> searchDataFromTempTable(TableRecord tableRecord) throws Exception
	{
		LinkedList<Map<String, String>> resultList = null;
		if (tableRecord != null && !SetUtils.isNullList(tableRecord.getColumnNameList()))
		{
			List<String> columnNameList = tableRecord.getColumnNameList();
			try
			{
				Statement stmt = connect.createStatement();
				String sql = this.createSql(tableRecord, SQLTypeEnum.SEARCH);
				if (!StringUtils.isNullString(sql))
				{
					ResultSet resultSet = stmt.executeQuery(sql);
					if (resultSet != null)
					{
						resultList = new LinkedList<Map<String, String>>();
						while (resultSet.next())
						{
							Map<String, String> map = new HashMap<String, String>();
							for (int i = 0; i < columnNameList.size(); i++)
							{
								if (!StringUtils.isNullString(columnNameList.get(i)))
								{
									map.put(columnNameList.get(i), resultSet.getString(i + 1));
								}
							}
							resultList.add(map);
						}
					}
					resultSet.close();
				}
				stmt.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw e;
			}
		}
		return resultList;
	}

	/**
	 * 向中间表插入数据
	 * 
	 * @throws SQLException
	 */
	public void insertDataIntoTempTable(TableRecord tableRecord) throws ServiceRequestException
	{
		String sql = this.createSql(tableRecord, SQLTypeEnum.INSERT);
		List<String> errorInfoSqlList = new ArrayList<String>();
		if (!StringUtils.isNullString(sql))
		{
			try
			{
				PreparedStatement prepStmt = connect.prepareStatement(sql);
				List<ERPFieldMapping> fieldList = tableRecord.getErpFieldMappingList();
				LinkedList<Map<String, String>> recordDataList = tableRecord.getRecordDataList();
				if (!SetUtils.isNullList(recordDataList) && !SetUtils.isNullList(fieldList))
				{
					for (Map<String, String> recordData : recordDataList)
					{
						String errorInfoSql = sql;
						for (int i = 0; i < fieldList.size(); i++)
						{
							ERPFieldMapping field = fieldList.get(i);
							String columnName = field.getERPField();
							if (!StringUtils.isNullString(columnName))
							{
								String val = recordData.get(columnName);
								if (!StringUtils.isNullString(val))
								{
									val = java.util.regex.Matcher.quoteReplacement(val);
								}
								errorInfoSql = errorInfoSql.replaceFirst("[?]", "\'" + val + "\'");
								if (ERPFieldMappingTypeEnum.DATE.equals(field.getDataType()))
								{
									java.util.Date utilDate = DateFormat.parse(recordData.get(columnName), field.getDateFormat());
									if (utilDate != null)
									{
										prepStmt.setTimestamp(i + 1, new java.sql.Timestamp(utilDate.getTime()));
									}
									else
									{
										prepStmt.setTimestamp(i + 1, null);
									}
								}
								else
								{
									prepStmt.setString(i + 1, recordData.get(columnName));
								}
							}
						}
						errorInfoSqlList.add(errorInfoSql);
						prepStmt.addBatch();
					}
				}
				prepStmt.executeBatch();
				prepStmt.close();
			}
			catch (SQLException e)
			{
				// DynaLogger.error(errorInfoSqlList);
				String errorInfo = "";
				if (!SetUtils.isNullList(errorInfoSqlList))
				{
					errorInfo = this.concatStringWithComma(errorInfoSqlList);
				}
				throw new ServiceRequestException(e.getMessage() + "[" + errorInfo + "]");
			}
		}
	}

	/**
	 * 批量插入数据
	 * 考虑事物处理
	 * 
	 * @param listTableRecord
	 * @throws ServiceRequestException
	 */
	public void batchInsertData(List<TableRecord> listTableRecord) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(listTableRecord))
		{
			try
			{
				for (TableRecord tableRecord : listTableRecord)
				{
					this.insertDataIntoTempTable(tableRecord);
				}
			}
			catch (Exception e)
			{
				throw new ServiceRequestException(e.getMessage());
			}
		}
	}

	/**
	 * 开始事务
	 * 
	 * @throws ServiceRequestException
	 * @throws SQLException
	 */
	public void startTransaction() throws ServiceRequestException
	{
		if (this.connect != null)
		{
			try
			{
				this.connect.setAutoCommit(false);
			}
			catch (SQLException e)
			{
				throw new ServiceRequestException("ID_START_TRANSACTION_DATABASE_ERROR", e.getMessage(), e);
			}
		}
	}

	/**
	 * 提交数据
	 * 
	 * @throws ServiceRequestException
	 * @throws SQLException
	 */
	public void commitTransaction() throws ServiceRequestException
	{
		if (this.connect != null)
		{
			try
			{
				this.connect.commit();
			}
			catch (SQLException e)
			{
				throw new ServiceRequestException("ID_COMMIT_TRANSACTION_DATABASE_ERROR", e.getMessage(), e);
			}
		}
	}

	/**
	 * 回滚数据
	 * 
	 * @throws ServiceRequestException
	 */
	public void rollBackTransaction() throws ServiceRequestException
	{
		if (this.connect != null)
		{
			try
			{
				this.connect.rollback();
			}
			catch (SQLException e)
			{
				throw new ServiceRequestException("ID_DBLINK_ROLLBACK_DATABASE_ERROR", e.getMessage(), e);
			}
		}
	}

	/**
	 * 生成SQL语句
	 * 
	 * @param tableRecord
	 *            表数据
	 * @param sqlType
	 *            SQL类型
	 * @return
	 */
	private String createSql(TableRecord tableRecord, SQLTypeEnum sqlType)
	{
		String resultSql = "";
		String tableName = tableRecord.getTableName();
		if (!StringUtils.isNullString(tableName))
		{
			if (SQLTypeEnum.INSERT.equals(sqlType))
			{
				List<ERPFieldMapping> fieldList = tableRecord.getErpFieldMappingList();
				if (!SetUtils.isNullList(fieldList))
				{
					List<String> columnList = new ArrayList<String>();
					List<String> valueList = new ArrayList<String>();
					for (ERPFieldMapping field : fieldList)
					{
						String columnName = field.getERPField();
						if (!StringUtils.isNullString(columnName))
						{
							columnList.add(columnName);
							valueList.add("?");
						}
					}
					if (!SetUtils.isNullList(columnList) && !SetUtils.isNullList(valueList))
					{
						StringBuffer resultSqlBuf = new StringBuffer("insert into " + tableName + "(" + this.concatStringWithComma(columnList) + ")" + " values");
						resultSqlBuf.append("(" + this.concatStringWithComma(valueList) + ")");
						resultSql = resultSql + resultSqlBuf.toString();
					}
				}
			}
			else if (SQLTypeEnum.SEARCH.equals(sqlType))
			{
				List<String> columnList = tableRecord.getColumnNameList();
				if (!SetUtils.isNullList(columnList))
				{
					resultSql = "select " + this.concatStringWithComma(columnList) + " from " + tableName;
					Map<String, String> conditionMap = tableRecord.getConditionMap();
					if (!SetUtils.isNullMap(conditionMap))
					{
						resultSql = resultSql + "\nwhere ";
						Iterator<String> conditionIt = conditionMap.keySet().iterator();
						while (conditionIt.hasNext())
						{
							String condition = conditionIt.next();
							resultSql = resultSql + condition + "= '" + conditionMap.get(condition) + "'";
						}
					}
				}
			}
		}
		return resultSql;
	}

	/**
	 * 用逗号连接字符串
	 * 类似于"a,b,c"
	 * 
	 * @param strList
	 * @return
	 */
	private String concatStringWithComma(List<String> strList)
	{
		String resultStr = "";
		if (!SetUtils.isNullList(strList))
		{
			for (int i = 0; i < strList.size(); i++)
			{
				resultStr = resultStr + strList.get(i);
				if (i == strList.size() - 1)
				{
					break;
				}
				resultStr = resultStr + ",";
			}
		}
		return resultStr;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @throws ServiceRequestException
	 */
	public void close() throws ServiceRequestException
	{
		try
		{
			if (connect != null)
			{
				connect.close();
			}
		}
		catch (SQLException e)
		{
			throw new ServiceRequestException("ID_DBLINK_CLOSE_DATABASE_ERROR", e.getMessage(), e);
		}
	}
}
