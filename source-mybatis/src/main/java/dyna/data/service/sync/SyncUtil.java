package dyna.data.service.sync;

import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dtomapper.model.cls.ClassFieldMapper;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.service.sync.bean.ColumnModel;
import dyna.data.service.sync.bean.TableIndexModel;
import dyna.data.service.sync.bean.TableIndexObject;
import dyna.dbcommon.function.DatabaseFunctionFactory;
import dyna.dbcommon.function.columntype.ColumnTypeFunction;
import dyna.dbcommon.function.sqlconvert.DDLConvertFunction;
import dyna.dbcommon.function.typename.TypeNameConvertFunction;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SyncUtil
{
	/**
	 * Object类型的Field对应ClassGuid列增加的后缀
	 */
	public static final String									classGuidSuffix			= "$CLASS";

	/**
	 * Object类型的Field对应MASTER列增加的后缀
	 */
	public static final String									masterSuffix			= "$MASTER";

	public static final Integer									COLUMN_NUM_OF_PER_TAB	= 70;

	public static       SqlSessionFactory                       sqlSessionFactory;

	private static final ColumnTypeFunction columnTypeFunction = DatabaseFunctionFactory.getColumnTypeFunction();

	private static final TypeNameConvertFunction typeNameConvert = DatabaseFunctionFactory.getTypeNameConvertFunction();

	private static final DDLConvertFunction ddlConvert = DatabaseFunctionFactory.getDDLConvertFunction();

	private static Map<String, List<ColumnModel>> dbColumnMap = new UpperKeyMap<List<ColumnModel>>();

	private static Map<String, Map<String, TableIndexModel>> dbIndexMap = new UpperKeyMap<Map<String, TableIndexModel>>();

	/**
	 * 新增字段处理
	 *
	 * @param columnModel
	 * @return
	 */
	private static List<String> buildNewColumnsSqlList(ColumnModel columnModel)
	{
		List<String> newColumnList = new ArrayList<>();
		String columnDesc = columnModel.getName() + " ";

		if (columnModel.isVarcharColumn())
		{
			String type = columnTypeFunction.getColumnType(columnModel.getType(), columnModel.getLength());
			newColumnList.add(columnDesc + type);
		}
		else if (columnModel.isBooleanColumn())
		{
			String type = columnTypeFunction.getColumnType(columnModel.getType(), "1");
			newColumnList.add(columnDesc + type);
		}
		else if (columnModel.isDateColumn() || columnModel.isDatetimeColumn())
		{
			String type = columnTypeFunction.getColumnType(columnModel.getType(), null);
			newColumnList.add(columnDesc + type);
		}
		else if (columnModel.isIntegerColumn() || columnModel.isFloatColumn())
		{
			String type = columnTypeFunction.getColumnType(columnModel.getType(), null);
			newColumnList.add(columnDesc + type);
		}
		return newColumnList;
	}

	/**
	 * 已存在字段更新
	 *
	 * @param columnModel
	 * @return
	 */
	private static void modifyExistColumn(ColumnModel columnModel, ColumnModel columnMapInDB) throws SQLException
	{
		if (columnModel.isVarcharColumn())
		{
			if (Integer.parseInt(columnModel.getLength()) > Integer.parseInt(columnMapInDB.getLength()))
			{
				String columnType = columnTypeFunction.getColumnType(FieldTypeEnum.STRING, columnModel.getLength());
				dropCasCadeIndex(columnMapInDB.getTableName(), columnModel.getName());
				modifyTable(columnMapInDB.getTableName(), columnModel.getName(), columnType);
			}
		}
	}

	private static void dropCasCadeIndex(String tableName, String name) throws SQLException
	{
		Map<String, TableIndexModel> dbIndexModelMap = getIndexModelMap(tableName);
		if (dbIndexModelMap != null)
		{
			Map<String, TableIndexModel> temp = new HashMap<>(dbIndexModelMap);
			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", tableName);
			for (String key : temp.keySet())
			{
				TableIndexModel model = dbIndexModelMap.get(key);
				param.put("INDEXNAME", key);
				if (model.useColumn(name))
				{
					if (model.isUnique())
					{
						boolean result = dropConstraint(param);
						if (!result)
						{
							dropIndex(param);
						}
					}
					else
					{
						boolean result = dropIndex(param);
						if (!result)
						{
							dropConstraint(param);
						}
					}
					dbIndexModelMap.remove(key);
				}
			}
		}

	}

	public static void syncMasterTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList) throws Exception
	{
		List<ColumnModel> dbColumnModelList = getMasterTableColumnList(baseTableName);
		syncMasterTable(baseTableName.toUpperCase() + "_MAST", modelColumnList, indexModelList, dbColumnModelList);
	}

	public static void syncRevisionTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList) throws Exception
	{
		List<ColumnModel> modelColumnList_ = new ArrayList<>(modelColumnList);

		List<ColumnModel> dbColumnModelList = getInsRevisionTableColumnList(baseTableName);
		syncRevisionTable(baseTableName, modelColumnList_, indexModelList, dbColumnModelList);
	}

	public static void syncIterationTable(String baseTableName, List<ColumnModel> modelColumnList) throws Exception
	{
		List<ColumnModel> modelColumnList_ = new ArrayList<>(modelColumnList);

		List<ColumnModel> dbColumnModelList = getInsIterationTableColumnList(baseTableName);
		syncIterationTable(baseTableName, modelColumnList_, dbColumnModelList);
	}

	public static void syncStructureTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList) throws Exception
	{
		List<ColumnModel> dbColumnModelList = getStructureTableColumnList(baseTableName);
		syncStructureTable(baseTableName, modelColumnList, indexModelList, dbColumnModelList);
	}

	public static void syncClassificatioRevisionTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList) throws Exception
	{
		List<ColumnModel> dbColumnModelList = getCFRevisionTableColumnList(baseTableName);

		syncClassificationTable("CF_" + baseTableName.toUpperCase(), modelColumnList, indexModelList, dbColumnModelList);
	}

	public static void syncClassificationIterationTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList) throws Exception
	{
		List<ColumnModel> dbColumnModelList = getCFIterationTableColumnList(baseTableName);
		syncClassificationTable("CF_" + baseTableName.toUpperCase() + "_I", modelColumnList, indexModelList, dbColumnModelList);
	}

	private static void syncMasterTable(String tableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList, List<ColumnModel> dbColumnModelList)
			throws Exception
	{
		// 取出所有删除的字段
		dropColumn(dbColumnModelList, modelColumnList);

		// 取出模型中所有的新增字段
		List<ColumnModel> newColumnList = listNewColumn(dbColumnModelList, modelColumnList);
		addAllColumnToOneTable(tableName, newColumnList);

		// 更新剩余字段
		modifyColumnOfTable(tableName, modelColumnList, dbColumnModelList);

		// 更新索引
		syncIndex(tableName, indexModelList);
	}

	private static void syncIterationTable(String baseTableName, List<ColumnModel> modelColumnList, List<ColumnModel> dbColumnModelList) throws Exception
	{
		// 取出所有删除的字段
		dropColumn(dbColumnModelList, modelColumnList);

		// 取出模型中所有的新增字段
		List<ColumnModel> newColumnList = listNewColumn(dbColumnModelList, modelColumnList);
		addAllColumnToOneTable(baseTableName + "_I", newColumnList);

		// 更新剩余字段
		modifyColumnOfTable(baseTableName + "_I", modelColumnList, dbColumnModelList);
	}

	private static void syncStructureTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList, List<ColumnModel> dbColumnModelList)
			throws Exception
	{
		// 取出所有删除的字段
		dropColumn(dbColumnModelList, modelColumnList);

		// 取出模型中所有的新增字段
		List<ColumnModel> newColumnList = listNewColumn(dbColumnModelList, modelColumnList);
		addAllColumnToOneTable(baseTableName + "_0", newColumnList);

		// 更新剩余字段
		modifyColumnOfTable(baseTableName + "_0", modelColumnList, dbColumnModelList);

		// 更新索引
		syncIndex(baseTableName + "_0", indexModelList);
	}

	private static void syncClassificationTable(String tableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList, List<ColumnModel> dbColumnModelList)
			throws Exception
	{
		// 取出所有删除的字段
		dropColumn(dbColumnModelList, modelColumnList);
		// 取出模型中所有的新增字段
		List<ColumnModel> newColumnList = listNewColumn(dbColumnModelList, modelColumnList);
		addAllColumnToOneTable(tableName, newColumnList);

		// 更新剩余字段
		modifyColumnOfTable(tableName, modelColumnList, dbColumnModelList);

		// 更新索引
		syncIndex(tableName, indexModelList);
	}

	private static void syncRevisionTable(String baseTableName, List<ColumnModel> modelColumnList, List<TableIndexModel> indexModelList, List<ColumnModel> dbColumnModelList)
			throws Exception
	{
		// 取出所有删除的字段
		dropColumn(dbColumnModelList, modelColumnList);

		// 取出模型中所有的新增字段
		List<ColumnModel> newColumnList = listNewColumn(dbColumnModelList, modelColumnList);
		addColumnToRevisionTable(baseTableName, newColumnList, dbColumnModelList);

		// 更新剩余字段
		modifyColumnOfRevisionTable(baseTableName, modelColumnList, dbColumnModelList);

		// 更新索引
		syncIndex(baseTableName + "_0", indexModelList);
	}

	private static void modifyColumnOfRevisionTable(String baseTableName, List<ColumnModel> columnList, List<ColumnModel> dbColumnModelList) throws SQLException
	{
		int tableIndex = 0;
		while (!SetUtils.isNullList(columnList))
		{
			String tableName = baseTableName + "_" + tableIndex;
			modifyColumnOfTable(tableName, columnList, dbColumnModelList);

			tableIndex++;
		}
	}

	private static void modifyColumnOfTable(String tableName, List<ColumnModel> columnList, List<ColumnModel> dbColumnModelList) throws SQLException
	{
		Iterator<ColumnModel> iterator = columnList.iterator();
		while (iterator.hasNext())
		{
			ColumnModel columnModel = iterator.next();
			if ("GUID".equals(columnModel.getName()))
			{
				iterator.remove();
				continue;
			}
			ColumnModel column = getColumnFromDB(tableName, columnModel.getName(), dbColumnModelList);
			if (column != null)
			{
				modifyExistColumn(columnModel, column);
				iterator.remove();
			}
		}
	}

	private static ColumnModel getColumnFromDB(String tableName, String columnName, List<ColumnModel> dbColumnModelList)
	{
		if (!SetUtils.isNullList(dbColumnModelList))
		{
			Iterator<ColumnModel> iterator = dbColumnModelList.iterator();
			while (iterator.hasNext())
			{
				ColumnModel column = iterator.next();
				String columnName_ = column.getName();
				if (columnName.equalsIgnoreCase(columnName_) && tableName.equalsIgnoreCase(column.getTableName()))
				{
					iterator.remove();
					return column;
				}
			}
		}
		return null;
	}

	private static void addColumnToRevisionTable(String baseTableName, List<ColumnModel> newColumnList, List<ColumnModel> dbColumnModelList) throws Exception
	{
		int tableIndex = 0;
		while (!SetUtils.isNullList(newColumnList))
		{
			addColumnToRevisionTable(baseTableName, tableIndex, newColumnList, dbColumnModelList);
			tableIndex++;
		}
	}

	private static void addColumnToRevisionTable(String baseTableName, int tableIndex, List<ColumnModel> newColumnList, List<ColumnModel> dbColumnModelList) throws Exception
	{
		String tableName = baseTableName + "_" + tableIndex;
		List<ColumnModel> columnsOfTableList = listDBColumnOfTable(tableName, dbColumnModelList);

		int currentTableColumnCnt = columnsOfTableList.size();
		List<ColumnModel> addColumnList = new ArrayList<>();

		// 添加内置字段
		if (tableIndex == 0 && !SetUtils.isNullList(newColumnList))
		{
			Iterator<ColumnModel> iterator = newColumnList.iterator();
			while (iterator.hasNext())
			{
				ColumnModel column = iterator.next();
				if (column.isBuiltin() || column.isSystem())
				{
					addColumnList.add(column);
					currentTableColumnCnt++;
					iterator.remove();
				}
			}
		}

		// 表未满，可以添加
		if (!SetUtils.isNullList(newColumnList))
		{
			if (currentTableColumnCnt < COLUMN_NUM_OF_PER_TAB)
			{
				Iterator<ColumnModel> iterator = newColumnList.iterator();
				while (iterator.hasNext())
				{
					ColumnModel column = iterator.next();
					// 暂时不处理CLASS和MASTER列
					if (column.getName().endsWith("$CLASS") || column.getName().endsWith("$MASTER"))
					{
						continue;
					}

					addColumnList.add(column);
					currentTableColumnCnt++;
					iterator.remove();

					if (currentTableColumnCnt >= COLUMN_NUM_OF_PER_TAB)
					{
						break;
					}
				}
			}
		}

		// 当前表没有新增任何列
		if (SetUtils.isNullList(addColumnList))
		{
			return;
		}

		// 添加CLASS和MASTER列
		if (!SetUtils.isNullList(newColumnList))
		{
			Iterator<ColumnModel> iterator = newColumnList.iterator();
			while (iterator.hasNext())
			{
				ColumnModel column = iterator.next();
				if (column.getName().endsWith("$CLASS"))
				{
					ColumnModel classColumn = getClassColumn(column, addColumnList);
					if (classColumn != null)
					{
						addColumnList.add(classColumn);
						iterator.remove();
					}
				}
				if (column.getName().endsWith("$MASTER"))
				{
					ColumnModel masterColumn = getMasterColumn(column, addColumnList);
					if (masterColumn != null)
					{
						addColumnList.add(masterColumn);
						iterator.remove();
					}
				}
			}
		}

		// 在表中新增字段
		if (!SetUtils.isNullList(addColumnList))
		{
			addColumnToTable(tableName, addColumnList);
		}
	}

	private static void addAllColumnToOneTable(String tableName, List<ColumnModel> newColumnList) throws Exception
	{
		if (!SetUtils.isNullList(newColumnList))
		{
			addColumnToTable(tableName, newColumnList);
		}
	}

	private static List<ColumnModel> listDBColumnOfTable(String tableName, List<ColumnModel> dbColumnModelList)
	{
		List<ColumnModel> columnsOfTableList = new ArrayList<>();
		if (!SetUtils.isNullList(dbColumnModelList))
		{
			for (ColumnModel dbColumn : dbColumnModelList)
			{
				String tableName_ = dbColumn.getTableName();
				if (tableName.equalsIgnoreCase(tableName_))
				{
					columnsOfTableList.add(dbColumn);
				}
			}
		}

		return columnsOfTableList;
	}

	/**
	 * 在指定表中新增字段
	 *
	 * @param tableName
	 * @param addColumnList
	 * @throws Exception
	 */
	private static void addColumnToTable(String tableName, List<ColumnModel> addColumnList) throws Exception
	{
		// 表是否存在
		if (hasTable(tableName))
		{
			// 表已存在，需要更新
			addColumnToExistTable(tableName, addColumnList);
		}
		else
		{
			// 表不存在，需要新建
			createTable(addColumnList, tableName);
		}
	}

	private static ColumnModel getClassColumn(ColumnModel column, List<ColumnModel> addColumnList)
	{
		for (ColumnModel addColumn : addColumnList)
		{
			if (column.getName().equals(addColumn.getName() + "$CLASS"))
			{
				return column;
			}
		}
		return null;
	}

	private static ColumnModel getMasterColumn(ColumnModel column, List<ColumnModel> addColumnList)
	{
		for (ColumnModel addColumn : addColumnList)
		{
			if (column.getName().startsWith(addColumn.getName() + "$MASTER"))
			{
				return column;
			}
		}
		return null;
	}

	private static void addColumnToExistTable(String tableName, List<ColumnModel> columnModelList) throws SQLException
	{
		List<String> columnDescList = new ArrayList<>();
		if (!SetUtils.isNullList(columnModelList))
		{
			Iterator<ColumnModel> it = columnModelList.iterator();
			while (it.hasNext())
			{
				ColumnModel columnModel = it.next();
				columnDescList.addAll(buildNewColumnsSqlList(columnModel));

				it.remove();
			}
		}

		// 更新表
		addColToTable(tableName, columnDescList);
	}

	/**
	 * 取得数据库中有，但是在模型中没有，需要删除的字段列表
	 *
	 * @param dbColumnModelList
	 * @param columnModelList
	 * @return
	 */
	private static void dropColumn(List<ColumnModel> dbColumnModelList, List<ColumnModel> columnModelList) throws SQLException
	{
		if (dbColumnModelList == null)
		{
			return;
		}

		Map<String, String> param = new HashMap<>();
		if (!SetUtils.isNullList(dbColumnModelList))
		{
			Iterator<ColumnModel> it = dbColumnModelList.iterator();
			while (it.hasNext())
			{
				ColumnModel column = it.next();
				if ("GUID".equalsIgnoreCase(column.getName()) || "FOUNDATIONFK".equalsIgnoreCase(column.getName()) || "CLASSIFICATIONITEMGUID".equalsIgnoreCase(column.getName()))
				{
					continue;
				}

				boolean isFind = false;
				if (!SetUtils.isNullList(columnModelList))
				{
					for (ColumnModel columnModel : columnModelList)
					{
						if (column.getName().equals(columnModel.getName()))
						{
							isFind = true;
							break;
						}
					}
				}

				if (!isFind)
				{
					param.put("TABLENAME", column.getTableName());
					param.put("COLUMNNAME", column.getName());
					dropColumn(param);
					it.remove();
				}
			}
		}
	}

	/**
	 * 取出模型中有，但是数据库中没有，需要新增的字段列表
	 *
	 * @param dbColumnModelList
	 * @param columnModelList
	 * @return
	 */
	private static List<ColumnModel> listNewColumn(List<ColumnModel> dbColumnModelList, List<ColumnModel> columnModelList)
	{
		List<ColumnModel> columns = new ArrayList<>();
		if (!SetUtils.isNullList(columnModelList))
		{
			Iterator<ColumnModel> iterator = columnModelList.iterator();
			while (iterator.hasNext())
			{
				ColumnModel columnModel = iterator.next();
				boolean isFind = false;
				if (!SetUtils.isNullList(dbColumnModelList))
				{
					for (ColumnModel tableColumn : dbColumnModelList)
					{
						String columnName_ = tableColumn.getName();
						if (columnModel.getName().equals(columnName_))
						{
							isFind = true;
							break;
						}
					}
				}
				if (!isFind)
				{
					columns.add(columnModel);
					iterator.remove();
				}
			}
		}
		return columns;
	}

	public static FieldTypeEnum getValFromArray(String[] columnTypes, int index)
	{
		if (columnTypes == null || columnTypes.length <= index)
		{
			return FieldTypeEnum.STRING;
		}
		FieldTypeEnum fieldType = FieldTypeEnum.typeof(columnTypes[index]);
		if (fieldType == null)
		{
			return FieldTypeEnum.STRING;
		}
		return FieldTypeEnum.typeof(columnTypes[index]);
	}

	public static FieldTypeEnum getValFromArray(String columnType)
	{
		if (columnType == null)
		{
			return FieldTypeEnum.STRING;
		}
		FieldTypeEnum fieldType = FieldTypeEnum.typeof(columnType);
		if (fieldType == null)
		{
			return FieldTypeEnum.STRING;
		}
		return FieldTypeEnum.typeof(columnType);
	}

	/**
	 * 同步索引
	 *
	 * @param tableName
	 *            数据库中的索引模型
	 * @param modelIndexList
	 *            模型中的索引模型
	 * @return key：A 添加新索引，value为索引字段
	 *         key：D 删除旧索引，value为索引名字
	 * @throws SQLException
	 */
	private static void syncIndex(String tableName, List<TableIndexModel> modelIndexList) throws SQLException
	{
		// 取得数据库表模型中的索引
		Map<String, TableIndexModel> dbIndexModelMap = getIndexModelMap(tableName);

		// 模型中没有索引，数据库中有索引，删除数据库中的所有索引
		if (SetUtils.isNullList(modelIndexList))
		{
			if (SetUtils.isNullMap(dbIndexModelMap))
			{
				return;
			}

			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", tableName);
			for (String indexName : dbIndexModelMap.keySet())
			{
				param.put("INDEXNAME", indexName);
				if (dbIndexModelMap.get(indexName).isUnique())
				{
					boolean result = dropConstraint(param);
					if (!result)
					{
						dropIndex(param);
					}
				}
				else
				{
					boolean result = dropIndex(param);
					if (!result)
					{
						dropConstraint(param);
					}
				}
			}
			return;
		}

		// 模型中有索引，数据库中没有索引，全部添加
		if (!SetUtils.isNullList(modelIndexList) && SetUtils.isNullMap(dbIndexModelMap))
		{
			int index = 1;
			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", tableName);
			for (TableIndexModel indexModel : modelIndexList)
			{
				String indexName = indexModel.getName();
				if (StringUtils.isNullString(indexName))
				{
					if (indexModel.isUnique())
					{
						indexName = "UX" + index + "_" + tableName;
					}
					else
					{
						indexName = "IX" + index + "_" + tableName;
					}
				}
				indexModel.setName(indexName);
				param.put("INDEXNAME", indexName);
				param.put("INDEXCOLUMNS", indexModel.converListToStr());
				if (indexModel.isUnique())
				{
					createUniqueConstraint(param);
				}
				else
				{
					createIndex(param);
				}
				index++;
			}
		}

		// 数据库中模型中都有索引：
		// 1、索引类型发生变化则先删除后添加；
		// 2、模型中的索引在数据库中不存在，则添加新索引；
		// 3、数据库中的索引在模型中不存在，则删除原索引
		else
		{
			List<TableIndexModel> dbTableIndexModelList = new ArrayList<>();
			// 取得索引中已经使用的顺序号
			if (!SetUtils.isNullMap(dbIndexModelMap))
			{
				// 遍历数据库索引
				Map<String, Object> param = new HashMap<>();
				param.put("TABLENAME", tableName);
				for (String tableIndexName : dbIndexModelMap.keySet())
				{
					TableIndexModel dbIndexModel = dbIndexModelMap.get(tableIndexName);
					boolean isPrimaryIndex = isPrimaryIndex(dbIndexModel);
					if (isPrimaryIndex)
					{
						continue;
					}

					// 数据库中的索引和模型中有匹配的
					TableIndexModel indexModel = dbIndexModel.getAlreadyIndexedModel(modelIndexList);
					if (indexModel != null)
					{
						dbTableIndexModelList.add(dbIndexModel);
						modelIndexList.remove(indexModel);
					}
					else
					{
						// 数据库中的索引在模型中没用匹配的，需要删除
						param.put("INDEXNAME", dbIndexModel.getName());
						if (dbIndexModel.isUnique())
						{
							boolean result = dropConstraint(param);
							if (!result)
							{
								dropIndex(param);
							}
						}
						else
						{
							boolean result = dropIndex(param);
							if (!result)
							{
								dropConstraint(param);
							}
						}
					}
				}
			}

			List<String> tmpList = new ArrayList<>();
			for (TableIndexModel indexModel : dbTableIndexModelList)
			{
				String tableIndexName = indexModel.getName().toUpperCase();
				if ((tableIndexName.toUpperCase().startsWith("IX") || tableIndexName.toUpperCase().startsWith("UX")) && tableIndexName.indexOf("_") > 2)
				{
					String index = tableIndexName.substring(2, tableIndexName.indexOf("_"));
					tmpList.add(index);
				}
			}

			// 遍历模型索引
			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", tableName);
			for (TableIndexModel indexModel : modelIndexList)
			{
				String indexName = indexModel.getName();
				if (StringUtils.isNullString(indexName))
				{
					int index = getNewIndex(tmpList);
					tmpList.add(String.valueOf(index));
					if (indexModel.isUnique())
					{
						indexName = "UX" + index + "_" + tableName;
					}
					else
					{
						indexName = "IX" + index + "_" + tableName;
					}
				}
				param.put("INDEXCOLUMNS", indexModel.converListToStr());
				param.put("INDEXNAME", indexName);
				if (indexModel.isUnique())
				{
					createUniqueConstraint(param);
				}
				else
				{
					createIndex(param);
				}
			}
		}
	}

	private static boolean isPrimaryIndex(TableIndexModel indexModel)
	{
		if (indexModel.getTableIndexObjectList() != null && indexModel.getTableIndexObjectList().size() == 1)
		{
			TableIndexObject tableIndexObject = indexModel.getTableIndexObjectList().get(0);
			if ("guid".equalsIgnoreCase(tableIndexObject.getColumnName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 更新字段的tableindex
	 *
	 * @param realBaseTableNameList
	 * @throws SQLException
	 */
	public static void updateTableIndexOfColumn(List<String> realBaseTableNameList) throws SQLException
	{
		for (String realBaseTableName : realBaseTableNameList)
		{
			List<ColumnModel> tableColumnList = getInsRevisionTableColumnList(realBaseTableName);
			if (!SetUtils.isNullList(tableColumnList))
			{
				for (ColumnModel columnModel : tableColumnList)
				{
					String tableIndex = columnModel.getTableName().substring(columnModel.getTableName().length() - 1);
					String columnName = columnModel.getName();

					Map<String, Object> param = new HashMap<>();
					param.put("TABLEINDEX", tableIndex);
					param.put("REABBASETABLENAME", realBaseTableName.toUpperCase());
					param.put("COLUMNNAME", columnName);
					SqlSession sqlSession = sqlSessionFactory.openSession(true);
					sqlSession.getMapper(ClassFieldMapper.class).updateTableIndex(tableIndex,realBaseTableName.toUpperCase(),columnName);
					sqlSession.close();
				}
			}
		}
	}

	private static int getNewIndex(List<String> list)
	{
		if (!SetUtils.isNullList(list))
		{
			int index = 1;
			while (true)
			{
				if (list.contains(String.valueOf(index)))
				{
					index++;
					continue;
				}
				return index;
			}
		}
		return 1;
	}

	/**
	 * 生成创建表的sql
	 * 需要按照SyncService.COLUMN_NUM_OF_PER_TAB的值进行分表:
	 * 首先，系统字段在第一张表中,且只在一张表中。
	 * 其次，取得与该表关联的所有的类,字段最少的类尽量在一张表中。
	 * 第三，除了guid和foundationfk之外没有其它字段的表，需要删除。
	 * 第四，新创建的表需要尽量使用被跳过的索引。比如原有表1、2、3，后来2表因为每可用字段被删除，当再添加的字段需要创建新表时，使用索引2，而不是索引4。
	 * 第五，备份表不分表。
	 *
	 * @param columnModelList
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private static void createTable(List<ColumnModel> columnModelList, String tableName) throws SQLException
	{
		if (SetUtils.isNullList(columnModelList))
		{
			return;
		}

		List<String> columnDescList = new ArrayList<>();
		columnDescList.add("GUID " + columnTypeFunction.getColumnType(FieldTypeEnum.STRING, "32"));

		if (tableName.toUpperCase().startsWith("CF_"))
		{
			columnDescList.add("FOUNDATIONFK " + columnTypeFunction.getColumnType(FieldTypeEnum.STRING, "32"));
			columnDescList.add("CLASSIFICATIONITEMGUID " + columnTypeFunction.getColumnType(FieldTypeEnum.STRING, "32"));
		}
		else if (!tableName.endsWith("_0"))
		{
			columnDescList.add("FOUNDATIONFK " + columnTypeFunction.getColumnType(FieldTypeEnum.STRING, "32"));
		}

		if (!SetUtils.isNullList(columnDescList))
		{
			Iterator<ColumnModel> it = columnModelList.iterator();
			while (it.hasNext())
			{
				ColumnModel columnModel = it.next();
				if ("GUID".equalsIgnoreCase(columnModel.getName()) || "FOUNDATIONFK".equalsIgnoreCase(columnModel.getName())
						|| "CLASSIFICATIONITEMGUID".equalsIgnoreCase(columnModel.getName()))
				{
					continue;
				}
				columnDescList.addAll(buildNewColumnsSqlList(columnModel));

				it.remove();
			}
		}

		// 创建表
		Map<String, Object> param = new HashMap<>();
		param.put("TABLENAME", tableName);
		param.put("COLUMNDESCLIST", columnDescList);
		createTable(param);

		param.clear();
		param.put("TABLENAME", tableName);
		createPrimaryKey(param);

		// 第0张表为主表，没有foundationfk字段
		if (!tableName.endsWith("_0"))
		{
			param.clear();
			param.put("INDEXNAME", "IDX_" + tableName);
			param.put("TABLENAME", tableName);
			param.put("INDEXCOLUMNS", "FOUNDATIONFK");
			createIndex(param);
		}
	}

	private static void createTable(Map<String, Object> param) throws SQLException
	{
		//TODO
//		try
//		{
//			sqlClient.startTransaction();
//			sqlClient.update("syncModel.createTable", param);
//			sqlClient.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlClient.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			throw new DynaDataSqlException("create table " + param.get("TABLENAME") + " error", e);
//		}
//		finally
//		{
//			try
//			{
//				if (sqlClient.getCurrentConnection() != null)
//				{
//					if (!sqlClient.getCurrentConnection().isClosed())
//					{
//						sqlClient.getCurrentConnection().close();
//					}
//				}
//				if (sqlClient.getCurrentSession() != null)
//				{
//					sqlClient.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
	}

	private static void addColToTable(String tableName, List<String> columnDescList) throws SQLException
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			HashMap<String, String> param = new HashMap<>();
//			for (String columnDesc : columnDescList)
//			{
//				param.put("TABLENAME", tableName);
//				param.put("COLUMNDESC", columnDesc);
//				if ("SqlServer".equalsIgnoreCase(DatabaseFunctionFactory.databaseType))
//				{
//					sqlSessionFactory.update("syncModel.addColToTableForMS", param);
//				}
//				else
//				{
//					sqlSessionFactory.update("syncModel.addColToTable", param);
//				}
//			}
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			throw new DynaDataSqlException("add column to table " + tableName + " error", e);
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
	}

	private static void createPrimaryKey(Map<String, Object> param)
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.createPrimaryKey", param);
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
	}

	private static void dropColumn(Map<String, String> param) throws SQLException
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.dropColumn", param);
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			throw new DynaDataSqlException("drop column from table " + param.get("TABLENAME") + " error", e);
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
	}

	private static void createUniqueConstraint(Map<String, Object> param)
	{
		//TODO
//		DynaLogger.info("create unique index:" + param);
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.createUniqueConstraint", param);
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			// e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
	}

	private static boolean dropConstraint(Map<String, Object> param)
	{
		DynaLogger.info("drop unique index:" + param);
		boolean b = dropConstraint1(param);
		if (!b)
		{
			b = dropConstraint2(param);
		}
		return b;
	}

	private static boolean dropConstraint1(Map<String, Object> param)
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.dropConstraint1", param);
//			sqlSessionFactory.commitTransaction();
//			return true;
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			// e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
		return false;
	}

	private static boolean dropConstraint2(Map<String, Object> param)
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.dropConstraint2", param);
//			sqlSessionFactory.commitTransaction();
//			return true;
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			// e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
		return false;
	}

	private static boolean dropIndex(Map<String, Object> param)
	{
		DynaLogger.info("drop index:" + param);
		boolean b = dropIndex1(param);
		if (!b)
		{
			b = dropIndex2(param);
		}
		return b;
	}

	private static boolean dropIndex1(Map<String, Object> param)
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.dropIndex1", param);
//			sqlSessionFactory.commitTransaction();
//			return true;
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			// e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
		return false;
	}

	private static boolean dropIndex2(Map<String, Object> param)
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.dropIndex2", param);
//			sqlSessionFactory.commitTransaction();
//			return true;
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			// e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
		return false;
	}

	private static void createIndex(Map<String, Object> param)
	{
		//TODO
//		DynaLogger.info("create index:" + param);
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.createIndex", param);
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (sqlSessionFactory.getCurrentConnection() != null)
//				{
//					if (!sqlSessionFactory.getCurrentConnection().isClosed())
//					{
//						sqlSessionFactory.getCurrentConnection().close();
//					}
//				}
//				if (sqlSessionFactory.getCurrentSession() != null)
//				{
//					sqlSessionFactory.getCurrentSession().close();
//				}
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
	}

	public static void dropTable(String tableName) throws SQLException
	{
		//TODO
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.dropTable", tableName);
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			throw new DynaDataSqlException("drop table " + tableName + " error", e);
//		}
//		finally
//		{
//			if (sqlSessionFactory.getCurrentConnection() != null)
//			{
//				if (!sqlSessionFactory.getCurrentConnection().isClosed())
//				{
//					sqlSessionFactory.getCurrentConnection().close();
//				}
//			}
//			if (sqlSessionFactory.getCurrentSession() != null)
//			{
//				sqlSessionFactory.getCurrentSession().close();
//			}
//		}
	}

	public static void modifyTable(String tableName, String columnName, String columnType) throws SQLException
	{
		//TODO
//		try
//		{
//			String sql = ddlConvert.getSql(tableName, columnName, columnType, DDLTypeEnum.ALTER_COLUMN);
//			DynaLogger.info(sql);
//			sqlSessionFactory.startTransaction();
//			sqlSessionFactory.update("syncModel.executeddl", sql);
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			throw new DynaDataSqlException("modify table " + tableName + " error", e);
//		}
//		finally
//		{
//			if (sqlSessionFactory.getCurrentConnection() != null)
//			{
//				if (!sqlSessionFactory.getCurrentConnection().isClosed())
//				{
//					sqlSessionFactory.getCurrentConnection().close();
//				}
//			}
//			if (sqlSessionFactory.getCurrentSession() != null)
//			{
//				sqlSessionFactory.getCurrentSession().close();
//			}
//		}
	}

	/**
	 * 判断某个表是否存在
	 *
	 * @param tableName
	 *            表名
	 * @return
	 * @throws SQLException
	 */
	public static boolean hasTable(String tableName) throws SQLException
	{
		return dbColumnMap.containsKey(tableName.toUpperCase());
	}

	public static Map<String, TableIndexModel> getIndexModelMap(String tableName) throws SQLException
	{
		return dbIndexMap.get(tableName.toUpperCase());
	}

	public static List<ColumnModel> getMasterTableColumnList(String baseTableName) throws SQLException
	{
		List<ColumnModel> columnModelList = getTableColumnList(baseTableName + "_MAST");
		return columnModelList;
	}

	public static List<ColumnModel> getStructureTableColumnList(String baseTableName) throws SQLException
	{
		List<ColumnModel> columnModelList = getTableColumnList(baseTableName + "_0");
		return columnModelList;
	}

	public static List<ColumnModel> getInsRevisionTableColumnList(String baseTableName) throws SQLException
	{
		List<ColumnModel> columnModelList = new LinkedList<>();
		for (int i = 0; i < 10; i++)
		{
			List<ColumnModel> tempList = getTableColumnList(baseTableName + "_" + i);
			if (SetUtils.isNullList(tempList))
			{
				break;
			}
			else
			{
				columnModelList.addAll(tempList);
			}
		}
		return columnModelList;
	}

	public static List<ColumnModel> getInsIterationTableColumnList(String baseTableName) throws SQLException
	{
		List<ColumnModel> columnModelList = getTableColumnList(baseTableName + "_I");
		return columnModelList;
	}

	public static List<ColumnModel> getCFRevisionTableColumnList(String baseTableName) throws SQLException
	{
		List<ColumnModel> columnModelList = getTableColumnList("CF_" + baseTableName);
		return columnModelList;
	}

	public static List<ColumnModel> getCFIterationTableColumnList(String baseTableName) throws SQLException
	{
		List<ColumnModel> columnModelList = getTableColumnList("CF_" + baseTableName + "_I");
		return columnModelList;
	}

	/**
	 * 得到对表结构column的属性
	 *
	 * @param baseTableName
	 * @return
	 * @throws SQLException
	 */
	public static List<ColumnModel> getTableColumnList(String baseTableName)
	{
		return dbColumnMap.get(baseTableName.toUpperCase());
	}

	public static void initDBTableInfo() throws SQLException
	{
//		ResultSet columnRS = null;
//		dbColumnMap.clear();
//		dbIndexMap.clear();
//		try
//		{
//			sqlSessionFactory.startTransaction();
//			DatabaseMetaData dm = sqlSessionFactory.getCurrentConnection().getMetaData();
//			columnRS = dm.getColumns(null, typeNameConvert.getSchemaName(dm.getUserName()), "%", "%");
//			while (columnRS.next())
//			{
//				String columnName = columnRS.getString("COLUMN_NAME");
//				String columnSize = columnRS.getString("COLUMN_SIZE");
//				String columnType = columnRS.getString("TYPE_NAME");
//				String tableName = columnRS.getString("TABLE_NAME");
//				ColumnModel columnModel = new ColumnModel();
//				columnModel.setName(columnName.toUpperCase());
//				columnModel.setColumnType(columnType.toUpperCase());
//				columnModel.setLength(columnSize);
//				columnModel.setTableName(tableName.toUpperCase());
//				List<ColumnModel> columnModelList = dbColumnMap.get(tableName.toUpperCase());
//				if (columnModelList == null)
//				{
//					columnModelList = new LinkedList<>();
//					dbColumnMap.put(tableName.toUpperCase(), columnModelList);
//				}
//				columnModelList.add(columnModel);
//			}
//			columnRS.close();
//			columnRS = null;
//
//			for (String tableName : dbColumnMap.keySet())
//			{
//				columnRS = getIndex(dm, tableName, false);
//			}
//			sqlSessionFactory.commitTransaction();
//		}
//		catch (Exception e)
//		{
//			if (columnRS != null)
//			{
//				columnRS.close();
//			}
//			try
//			{
//				sqlSessionFactory.endTransaction();
//			}
//			catch (SQLException e1)
//			{
//				e1.printStackTrace();
//			}
//			throw new DynaDataSqlException("init Table column/Index information in DB , error", e);
//		}
//		finally
//		{
//			if (sqlSessionFactory.getCurrentConnection() != null)
//			{
//				if (!sqlSessionFactory.getCurrentConnection().isClosed())
//				{
//					sqlSessionFactory.getCurrentConnection().close();
//				}
//			}
//			if (sqlSessionFactory.getCurrentSession() != null)
//			{
//				sqlSessionFactory.getCurrentSession().close();
//			}
//		}
	}

	private static ResultSet getIndex(DatabaseMetaData dm, String tableName, boolean b) throws SQLException
	{
		ResultSet columnRS;
		columnRS = dm.getIndexInfo(null, typeNameConvert.getSchemaName(dm.getUserName()), typeNameConvert.getTypeName(tableName), b, false);
		Map<String, TableIndexModel> resultMap = dbIndexMap.get(tableName.toUpperCase());
		if (resultMap == null)
		{
			resultMap = new HashMap<>();
			dbIndexMap.put(tableName.toUpperCase(), resultMap);
		}
		while (columnRS.next())
		{
			Boolean nounique = columnRS.getBoolean("NON_UNIQUE");
			String indexName = columnRS.getString("INDEX_NAME");
			String columnName = columnRS.getString("COLUMN_NAME");
			if (StringUtils.isNullString(indexName))
			{
				continue;
			}
			if (indexName.toUpperCase().startsWith("PK_"))
			{
				continue;
			}

			if (!resultMap.containsKey(indexName))
			{
				resultMap.put(indexName, new TableIndexModel());
			}
			TableIndexModel indexModel = resultMap.get(indexName);
			indexModel.setName(indexName);
			indexModel.setUnique(nounique == null ? false : !nounique);

			if (indexModel.getTableIndexObjectList() == null)
			{
				indexModel.setTableIndexObjectList(new ArrayList<>());
			}

			TableIndexObject indexObject = new TableIndexObject();
			indexObject.setColumnName(columnName);
			indexModel.getTableIndexObjectList().add(indexObject);
		}
		columnRS.close();
		return columnRS;
	}
}
