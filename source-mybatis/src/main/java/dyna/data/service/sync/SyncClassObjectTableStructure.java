package dyna.data.service.sync;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dtomapper.model.cls.ClassInfoMapper;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.TableIndexTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.service.sync.bean.ColumnModel;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.data.service.sync.bean.TableIndexModel;
import dyna.data.service.sync.bean.TableIndexObject;
import dyna.data.service.sync.bean.TableModel;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class SyncClassObjectTableStructure extends SyncService
{
	@Autowired
	private final ModelXMLCache dataModel;

	@Autowired
	private ClassInfoMapper     classInfoMapper;

	public SyncClassObjectTableStructure(SqlSessionFactory sqlClient, String userGuid, ModelXMLCache dataModel)
	{
		super(sqlClient, userGuid);
		this.dataModel = dataModel;
	}

	/**
	 * 根据iscreatetable标记，来重设basetablename，并生成表
	 *
	 * @throws SQLException
	 */
	public void syncTable() throws Exception
	{
		DynaLogger.info("Start to synchronize instance table...");
		Map<String, TableModel> tableModelMap = new HashMap<>();
		this.getFieldOfTable(this.dataModel.getClassObject("DynaObject"), tableModelMap);

		for (TableModel tableModel : tableModelMap.values())
		{
			long start = System.currentTimeMillis();
			if (!tableModel.isStructure())
			{
				syncInsTable(tableModel);
			}
			else
			{
				syncStrucTable(tableModel);
			}
			DynaLogger.info("Sync Tbale:" + tableModel.getTableName() + ", cost:" + String.valueOf(System.currentTimeMillis() - start));
		}
//		this.sqlClient.startTransaction();
//		this.sqlClient.getCurrentConnection().setAutoCommit(false);
		try
		{
			// 更新字段的tableindex
			List<ClassInfo> dataList = this.classInfoMapper.selectAllRealbaseTableName();
			List<String> tableNameList = dataList.stream().map(ClassInfo::getRealBaseTableName).collect(Collectors.toList());
			SyncUtil.updateTableIndexOfColumn(tableNameList);

//			this.sqlClient.commitTransaction();
		}
		catch (Exception e)
		{
//			this.sqlClient.endTransaction();
			e.printStackTrace();
		}
		DynaLogger.info("Sucess to synchronize instance table(" + tableModelMap.size() + ")...");
	}

	private void syncInsTable(TableModel tableModel) throws Exception
	{
		TableModel masterTableModel = initMasterTableModel(tableModel);
		SyncUtil.syncMasterTable(tableModel.getTableName(), masterTableModel.getColumnList(), masterTableModel.getIndexModelList());

		SyncUtil.syncRevisionTable(tableModel.getTableName(), tableModel.getColumnList(), tableModel.getIndexModelList());

		SyncUtil.syncIterationTable(tableModel.getTableName(), tableModel.getColumnList());
	}

	private void syncStrucTable(TableModel tableModel) throws Exception
	{
		SyncUtil.syncStructureTable(tableModel.getTableName(), tableModel.getColumnList(), tableModel.getIndexModelList());
	}

	private static TableModel initMasterTableModel(TableModel tableModel)
	{
		TableModel tableModel_ = new TableModel();
		tableModel_.setTableName(tableModel.getTableName().toUpperCase() + "_MAST");
		tableModel_.setIDClassGuidUnique(tableModel.isIDClassGuidUnique());
		tableModel_.setIDUnique(tableModel.isIDUnique());
		tableModel_.setCheckUnique(true);

		ColumnModel columnModel = new ColumnModel();
		columnModel.setName(SystemClassFieldEnum.CLASSGUID.name());
		columnModel.setLength("32");
		columnModel.setType(FieldTypeEnum.STRING);
		tableModel_.addColumn(columnModel);

		columnModel = new ColumnModel();
		columnModel.setName("MD_ID");
		columnModel.setLength("128");
		columnModel.setType(FieldTypeEnum.STRING);
		tableModel_.addColumn(columnModel);

		columnModel = new ColumnModel();
		columnModel.setName(SystemClassFieldEnum.UNIQUES.name());
		columnModel.setLength("1000");
		columnModel.setType(FieldTypeEnum.STRING);
		tableModel_.addColumn(columnModel);

		if (tableModel_.isCheckUnique())
		{
			List<TableIndexObject> tableIndexObjectList = new ArrayList<>();

			TableIndexObject tableIndexObject = new TableIndexObject();
			tableIndexObject.setName(SystemClassFieldEnum.CLASSGUID.name());
			tableIndexObject.setColumnName(SystemClassFieldEnum.CLASSGUID.name());
			tableIndexObjectList.add(tableIndexObject);

			tableIndexObject = new TableIndexObject();
			tableIndexObject.setName(SystemClassFieldEnum.UNIQUES.name());
			tableIndexObject.setColumnName(SystemClassFieldEnum.UNIQUES.name());
			tableIndexObjectList.add(tableIndexObject);

			TableIndexModel tableIndexModel = new TableIndexModel();
			tableIndexModel.setUnique(true);
			tableIndexModel.setTableIndexObjectList(tableIndexObjectList);
			tableIndexModel.setType(TableIndexTypeEnum.INDEX);
			tableIndexModel.setName("SUX_CU_" + tableModel_.getTableName());
			List<TableIndexModel> tableIndexModelList = new ArrayList<>();
			tableIndexModelList.add(tableIndexModel);

			tableModel_.setIndexModelList(tableIndexModelList);
		}

		if (tableModel_.isIDClassGuidUnique())
		{
			List<TableIndexObject> tableIndexObjectList = new ArrayList<>();

			TableIndexObject tableIndexObject = new TableIndexObject();
			tableIndexObject.setName(SystemClassFieldEnum.CLASSGUID.name());
			tableIndexObject.setColumnName(SystemClassFieldEnum.CLASSGUID.name());
			tableIndexObjectList.add(tableIndexObject);

			tableIndexObject = new TableIndexObject();
			tableIndexObject.setName(SystemClassFieldEnum.ID.name());
			tableIndexObject.setColumnName("MD_ID");
			tableIndexObjectList.add(tableIndexObject);

			TableIndexModel tableIndexModel = new TableIndexModel();
			tableIndexModel.setUnique(true);
			tableIndexModel.setTableIndexObjectList(tableIndexObjectList);
			tableIndexModel.setType(TableIndexTypeEnum.INDEX);
			tableIndexModel.setName("SUX_CN_" + tableModel_.getTableName());
			tableModel_.getIndexModelList().add(tableIndexModel);
		}

		if (tableModel_.isIDUnique())
		{
			List<TableIndexObject> tableIndexObjectList = new ArrayList<>();

			TableIndexObject tableIndexObject = new TableIndexObject();
			tableIndexObject.setName(SystemClassFieldEnum.ID.name());
			tableIndexObject.setColumnName("MD_ID");
			tableIndexObjectList.add(tableIndexObject);

			TableIndexModel tableIndexModel = new TableIndexModel();
			tableIndexModel.setUnique(true);
			tableIndexModel.setTableIndexObjectList(tableIndexObjectList);
			tableIndexModel.setType(TableIndexTypeEnum.INDEX);
			tableIndexModel.setName("SUX_CI_" + tableModel_.getTableName());
			tableModel_.getIndexModelList().add(tableIndexModel);
		}

		return tableModel_;
	}

	// StructureObject和BOMStructure分表
	private void getFieldOfTable(ClassObject classObject, Map<String, TableModel> tableModelMap)
	{
		this.getFieldOfTableWithoutBOMStruc(classObject, tableModelMap);
		this.getFieldOfTableBOMStruc(classObject, tableModelMap);
	}

	private void getFieldOfTableBOMStruc(ClassObject classObject, Map<String, TableModel> tableModelMap)
	{
		if (classObject.isCreateTable() && classObject.hasInterface(ModelInterfaceEnum.IStructureObject))
		{
			TableModel tableModel = tableModelMap.get(classObject.getBasetableName().toUpperCase());
			if (tableModel == null)
			{
				tableModel = new TableModel();
				tableModelMap.put(classObject.getBasetableName().toUpperCase(), tableModel);
			}
			Map<String, ClassField> fieldMap = new HashMap<>();

			this.getAllClassFieldOfClass(classObject, fieldMap);
			tableModel.setTableName(classObject.getBasetableName());
			tableModel.setStructure(true);

			this.buildColumnOfTable(fieldMap, tableModel);

			// 把配置文件中配置的索引添加到模型中
			this.addIndexOfTable(classObject, tableModel);
			tableModel.getClassObjectList().add(classObject);
		}
		else
		{
			List<ClassObject> childList = classObject.getChildList();
			if (!SetUtils.isNullList(childList))
			{
				for (ClassObject child : childList)
				{
					this.getFieldOfTableBOMStruc(child, tableModelMap);
				}
			}
		}
	}

	/**
	 * 根据iscreatetable标记，来重设basetablename，并得到所有字段
	 *
	 * @param classObject
	 * @param tableModelList
	 */
	private void getFieldOfTableWithoutBOMStruc(ClassObject classObject, Map<String, TableModel> tableModelMap)
	{
		// 上层节点选中createTable，则所有子节点的basetablename都和该节点相同，因此不再遍历子节点
		// 表的所有字段即为当前类及其子类的所有字段
		if (classObject.isCreateTable() && (!classObject.hasInterface(ModelInterfaceEnum.IStructureObject)))
		{
			TableModel tableModel = tableModelMap.get(classObject.getBasetableName().toUpperCase());
			if (tableModel == null)
			{
				tableModel = new TableModel();
				tableModelMap.put(classObject.getBasetableName().toUpperCase(), tableModel);
			}
			Map<String, ClassField> fieldMap = new HashMap<>();

			// 取得当前类及其子类的所有字段
			this.getAllClassFieldOfClass(classObject, fieldMap);
			tableModel.setTableName(classObject.getBasetableName());

			this.buildColumnOfTable(fieldMap, tableModel);

			// 把配置文件中配置的索引添加到模型中
			this.addIndexOfTable(classObject, tableModel);

			// 设置唯一值检查属性
			if (this.isCheckUnique(classObject))
			{
				tableModel.setCheckUnique(true);
			}

			// 设置编号唯一检查,如果root节点设置为编号唯一,则表内id唯一,如果子节点设置编号唯一,则类内编号唯一
			if (classObject.isIdUnique())
			{
				tableModel.setIDUnique(true);
			}
			else if (this.isCheckIdUnique(classObject))
			{
				tableModel.setIDClassGuidUnique(true);
			}
			tableModel.getClassObjectList().add(classObject);
		}
		else
		{
			List<ClassObject> childList = classObject.getChildList();
			if (!SetUtils.isNullList(childList))
			{
				for (ClassObject child : childList)
				{
					this.getFieldOfTableWithoutBOMStruc(child, tableModelMap);
				}
			}
		}
	}

	/**
	 * 判断类（包含子类）是否有检查唯一值
	 *
	 * @param classObject
	 * @return
	 */
	private boolean isCheckUnique(ClassObject classObject)
	{
		if (!classObject.isCheckUnique())
		{
			List<ClassObject> childList = classObject.getChildList();
			if (!SetUtils.isNullList(childList))
			{
				for (ClassObject child : childList)
				{
					boolean isCheckUnique = this.isCheckUnique(child);
					if (isCheckUnique)
					{
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * 判断类（包含子类）是否有检查唯一值
	 *
	 * @param classObject
	 * @return
	 */
	private boolean isCheckIdUnique(ClassObject classObject)
	{
		List<ClassObject> childList = classObject.getChildList();
		if (!SetUtils.isNullList(childList))
		{
			for (ClassObject child : childList)
			{
				return this.isCheckIdUnique(child);
			}
		}
		return classObject.isIdUnique();
	}

	/**
	 * 取得类包括子类的所有字段
	 * 在此,BOMStructure和StructureObject分表
	 *
	 * @param classObject
	 * @param fieldList
	 */
	private void getAllClassFieldOfClass(ClassObject classObject, Map<String, ClassField> fieldMap)
	{
		if (!SetUtils.isNullList(classObject.getFieldList()))
		{
			for (ClassField classField : classObject.getFieldList())
			{
				if (!this.isContains(fieldMap, classField))
				{
					fieldMap.put(classField.getName() + classField.getColumnName(), classField);
				}
				else
				{
					ClassField classField_ = fieldMap.get(classField.getName() + classField.getColumnName());
					if (!StringUtils.isNullString(classField_.getFieldSize()) && !StringUtils.isNullString(classField.getFieldSize()))
					{
						if (!classField_.getFieldSize().contains(",") && Integer.parseInt(classField_.getFieldSize()) < Integer.parseInt(classField.getFieldSize()))
						{
							classField_.setFieldSize(classField.getFieldSize());
						}
					}
				}
			}
		}

		List<ClassObject> childList = classObject.getChildList();
		if (!SetUtils.isNullList(childList))
		{
			for (ClassObject child : childList)
			{
				this.getAllClassFieldOfClass(child, fieldMap);
			}
		}
	}

	private boolean isContains(Map<String, ClassField> fieldMap, ClassField field)
	{
		if (SetUtils.isNullMap(fieldMap))
		{
			return false;
		}

		String key = field.getName() + field.getColumnName();
		return fieldMap.containsKey(key);
	}

	/**
	 * 把字段添加到表模型中
	 *
	 * @param fieldMap
	 * @param tableModel
	 */
	private void buildColumnOfTable(Map<String, ClassField> fieldMap, TableModel tableModel)
	{
		if (!SetUtils.isNullMap(fieldMap))
		{
			for (String key : fieldMap.keySet())
			{
				ClassField field = fieldMap.get(key);
				this.addColumnToTable(field, tableModel);
				if (field.getType() == FieldTypeEnum.OBJECT)
				{
					if (!this.isSpecialTypeValue(field.getTypeValue()))
					{
						ClassField classField = field.clone();
						if (classField.getColumnName().endsWith("$"))
						{
							classField.setColumnName(classField.getColumnName() + "CLASS");
							this.addColumnToTable(classField, tableModel);

							ClassField masterField = field.clone();
							masterField.setColumnName(masterField.getColumnName() + "MASTER");
							this.addColumnToTable(masterField, tableModel);

						}
						else
						{
							classField.setColumnName(classField.getColumnName() + "$CLASS");
							this.addColumnToTable(classField, tableModel);

							ClassField masterField = field.clone();
							masterField.setColumnName(masterField.getColumnName() + "$MASTER");
							this.addColumnToTable(masterField, tableModel);
						}
					}
				}
			}
		}
	}

	private boolean isSpecialTypeValue(String typeValue)
	{
		ClassObject classObject = this.dataModel.getClassObject(typeValue);
		if (classObject == null)
		{
			return true;
		}

		return classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup) || classObject.hasInterface(ModelInterfaceEnum.IPMRole)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMCalendar) || classObject.hasInterface(ModelInterfaceEnum.IPMCalendar);
	}

	private void addColumnToTable(ClassField classField, TableModel tableModel)
	{
		ColumnModel columnModel = this.newColumnModel(classField);
		ColumnModel columnModel_ = tableModel.getColumnModel(columnModel);
		if (columnModel_ == null)
		{
			if (!StringUtils.isNullString(columnModel.getLength()) && Integer.parseInt(columnModel.getLength()) > 4000)
			{
				columnModel.setLength("4000");
			}
			tableModel.addColumn(columnModel);
		}
		else
		{
			if (!StringUtils.isNullString(columnModel.getLength()) && Integer.parseInt(columnModel.getLength()) > 4000)
			{
				columnModel.setLength("4000");
			}
			if (!StringUtils.isNullString(columnModel.getLength()) && !StringUtils.isNullString(columnModel_.getLength()))
			{
				if (Integer.parseInt(columnModel.getLength()) > Integer.parseInt(columnModel_.getLength()))
				{
					columnModel_.setLength(columnModel.getLength());
					// 不限制精度比限制精度范围大
					if (StringUtils.isNullString(columnModel.getScale()) && !StringUtils.isNullString(columnModel_.getScale()))
					{
						columnModel_.setScale(null);
					}
					else if (!StringUtils.isNullString(columnModel.getScale()) && !StringUtils.isNullString(columnModel_.getScale())
							&& Integer.parseInt(columnModel.getScale()) > Integer.parseInt(columnModel_.getScale()))
					{
						columnModel_.setScale(columnModel.getScale());
					}
				}
			}
			// 任何一个使用该列的字段rollback为true,就设置该列rollback为true
			if (classField.isRollback())
			{
				columnModel_.setRollback(true);
			}
		}
	}

	/**
	 * 把配置文件中配置的索引添加到模型中
	 *
	 * @param classObject
	 * @param tableModel
	 */
	private void addIndexOfTable(ClassObject classObject, TableModel tableModel)
	{
		List<ModelInterfaceEnum> interfaceList = classObject.getInterfaceList();
		if (interfaceList != null)
		{
			for (ModelInterfaceEnum interfaceEnum : interfaceList)
			{
				List<TableIndexModel> indexModelList = this.dataModel.getInterfaceIndexMap().get(interfaceEnum.name());
				if (!SetUtils.isNullList(indexModelList))
				{
					for (TableIndexModel tabIndexModel : indexModelList)
					{
						List<TableIndexObject> tmpList = new ArrayList<>();
						for (TableIndexObject indexObject : tabIndexModel.getTableIndexObjectList())
						{
							ClassField classField = classObject.getField(indexObject.getName());

							String columnName = classField.getColumnName();
							indexObject.setColumnName(columnName);
							tmpList.add(indexObject);
						}

						TableIndexModel indexModel = new TableIndexModel();
						indexModel.setType(TableIndexTypeEnum.INDEX);
						indexModel.setUnique(tabIndexModel.isUnique());
						if (indexModel.getTableIndexObjectList() == null)
						{
							indexModel.setTableIndexObjectList(new ArrayList<>());
						}
						indexModel.getTableIndexObjectList().addAll(tmpList);
						TableIndexModel existIndex = indexModel.getAlreadyIndexedModel(tableModel.getIndexModelList());
						if (existIndex == null)
						{
							tableModel.getIndexModelList().add(indexModel);
						}
					}
					// 结构表固定增加end2$master索引
					if (tableModel.isStructure())
					{
						TableIndexObject indexObject = new TableIndexObject();
						indexObject.setColumnName("END2$MASTER");
						indexObject.setName("END2$MASTER");

						List<TableIndexObject> indexObjectList = new ArrayList<>();
						indexObjectList.add(indexObject);

						TableIndexModel indexModel_ = new TableIndexModel();
						indexModel_.setType(TableIndexTypeEnum.INDEX);
						indexModel_.setUnique(false);
						indexModel_.setTableIndexObjectList(indexObjectList);
						TableIndexModel existIndex = indexModel_.getAlreadyIndexedModel(tableModel.getIndexModelList());
						if (existIndex == null)
						{
							tableModel.getIndexModelList().add(indexModel_);
						}
					}

				}
			}
		}
	}

	public ColumnModel newColumnModel(ClassField field)
	{
		String length = getColumnLength(field);
		String scale = null;
		if (length != null && length.indexOf(",") != -1)
		{
			scale = length.substring(length.indexOf(",") + 1);
			length = length.substring(0, length.indexOf(","));
		}
		boolean isNullable = !field.isMandatory();

		return newColumnModel(field.getColumnName(), field.getType(), length, scale, isNullable, field.isRollback(), field.isSystem(), field.isBuiltin());
	}

	public ColumnModel newColumnModel(String name, FieldTypeEnum type, String length, String scale, boolean isNullable, boolean isRollback, boolean isSystem, boolean isBuiltin)
	{
		ColumnModel column = new ColumnModel();
		column.setName(name);
		column.setType(type);
		column.setLength(length);
		column.setNullable(isNullable);
		column.setScale(scale);
		column.setRollback(isRollback);
		column.setSystem(isSystem);
		column.setBuiltin(isBuiltin);

		return column;
	}

	/**
	 * 取得数据库字段长度
	 *
	 * @param field
	 * @param fieldTypeEnum
	 * @return
	 */
	public String getColumnLength(ClassField field)
	{
		if (field.getType() == FieldTypeEnum.CODE || field.getType() == FieldTypeEnum.CLASSIFICATION || field.getType() == FieldTypeEnum.FOLDER
				|| field.getType() == FieldTypeEnum.OBJECT || field.getType() == FieldTypeEnum.CODEREF)
		{
			return "32";
		}
		else if (field.getType() == FieldTypeEnum.MULTICODE)
		{
			return "4000";
		}
		else if (!StringUtils.isNullString(field.getFieldSize()))
		{
			return field.getFieldSize();
		}
		else if (field.getType() == FieldTypeEnum.STATUS)
		{
			return "3";
		}
		else if (field.getType() == FieldTypeEnum.BOOLEAN)
		{
			return "1";
		}
		else if (field.getType() == FieldTypeEnum.INTEGER || field.getType() == FieldTypeEnum.FLOAT)
		{
			return null;
		}
		return "128";
	}
}
