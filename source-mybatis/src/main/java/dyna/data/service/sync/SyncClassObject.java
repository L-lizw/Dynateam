package dyna.data.service.sync;

import dyna.common.dtomapper.TreeDataRelationMapper;
import dyna.common.dtomapper.model.cls.ClassFieldCRDetailMapper;
import dyna.common.dtomapper.model.cls.ClassFieldMapper;
import dyna.common.dtomapper.model.cls.ClassInfoMapper;
import dyna.common.dtomapper.model.code.CodeObjectInfoMapper;
import dyna.common.dtomapper.model.lf.LifecycleInfoMapper;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.common.bean.model.ReferenceCode;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassFieldCRDetail;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.NumberUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SyncClassObject extends SyncService
{
	// 分类缓存
	private final Map<String, String> masterClassificationMap   = new HashMap<>();
	// 一般组码缓存
	private final Map<String, String> masterCodeMap             = new HashMap<>();
	// 生命周期缓存
	private final Map<String, String> lifecycleNameGuidMap      = new HashMap<>();
	// 一般对象系统字段缓存
	private final List<String>        systemInsClassFieldList   = new ArrayList<>();
	// 结构类系统字段缓存
	private final List<String>        systemStrucClassFieldList = new ArrayList<>();

	// 数据库原始模型
	private final Map<String, Map<String, ClassField>> origClassFieldModel = new HashMap<>();
	// 数据库原始类缓存
	private final Map<String, ClassInfo>               origClassDataMap    = new HashMap<>();

	// 更新或者新增的类
	private final Map<String, String>       classGuidMap = new HashMap<>();
	// 新增或者更新的字段信息
	private       Map<String, List<String>> fieldGuidMap = new HashMap<>();

	private int maxTableIndex = 1;

	private int maxColumnIndex = 1;

	public final ModelXMLCache                  dataModel;
	private LifecycleInfoMapper                 lifecycleInfoMapper;
	private ClassInfoMapper                     classInfoMapper;
	private ClassFieldMapper                    classFieldMapper;
	private CodeObjectInfoMapper                codeObjectInfoMapper;
	private ClassFieldCRDetailMapper            classFieldCRDetailMapper;
	private TreeDataRelationMapper              treeDataRelationMapper;

	public SyncClassObject(SqlSessionFactory sqlSessionFactory, String userGuid, ModelXMLCache dataModel)
	{
		super(sqlSessionFactory, userGuid);
		this.dataModel = dataModel;
	}

	/**
	 * 同步类对象
	 *
	 * @throws SQLException
	 */
	public void startSync() throws Exception
	{
		// 生命周期缓存
		this.cacheLifecycle();

		// 加载主分类
		this.loadMasterClassification();

		// 把系统字段读取到缓存中
		this.loadSystemClassFieldFromInterface();

		// 缓存class的父类
		this.cacheClass();

		// 缓存ma_field的数据
		this.cacheClassFieldModel();

		// 把接口字段添加到类模型上
		this.loadFieldOfInterface(this.dataModel.getClassObject("DynaObject"));

		// 根据数据对象同步数据库
		// 新添加的类的表名以及字段的column名未处理
		this.syncModel(this.dataModel.getClassObject("DynaObject"));

		// 模型中没有的数据删除
		this.deleteFromModel();

		// 保存类所使用的实际表
		this.syncUseTableNameOfClass();

		// 记录class的父子结构
		this.saveClassTreeRelation(this.dataModel.getClassObject("DynaObject"));
	}

	private void cacheLifecycle() throws SQLException
	{
		this.lifecycleNameGuidMap.clear();
		List<LifecycleInfo> classObjectList = this.lifecycleInfoMapper.selectForLoad();
		classObjectList.forEach(lifecycle -> {
			this.lifecycleNameGuidMap.put(lifecycle.getName(), lifecycle.getGuid());
		});
		;
	}

	/**
	 * 把系统字段读取到缓存中
	 *
	 * @throws SQLException
	 */
	private void loadSystemClassFieldFromInterface() throws SQLException
	{
		List<ClassField> classFieldSet = this.dataModel.getInterfaceFieldMap().get(ModelInterfaceEnum.IFoundation.name());
		if (!SetUtils.isNullList(classFieldSet))
		{
			for (ClassField classField : classFieldSet)
			{
				if (classField.isSystem() && !this.systemInsClassFieldList.contains(classField.getName()))
				{
					this.systemInsClassFieldList.add(classField.getName());
				}
			}
		}

		classFieldSet = this.dataModel.getInterfaceFieldMap().get(ModelInterfaceEnum.IStructureObject.name());
		if (classFieldSet != null && !classFieldSet.isEmpty())
		{
			for (ClassField classField : classFieldSet)
			{
				if (classField.isSystem() && !this.systemStrucClassFieldList.contains(classField.getName()))
				{
					this.systemStrucClassFieldList.add(classField.getName());
				}
			}
		}
	}

	private void cacheClass() throws Exception
	{
		this.origClassDataMap.clear();

		List<ClassInfo> list = this.classInfoMapper.selectForLoad();
		if (!SetUtils.isNullList(list))
		{
			for (ClassInfo classInfo : list)
			{
				this.origClassDataMap.put(classInfo.getName(), classInfo);

				String baseTableName = classInfo.getBasetableName();
				if (!StringUtils.isNullString(baseTableName) && baseTableName.startsWith("NT_"))
				{
					String indexStr = baseTableName.substring(3);
					if (NumberUtils.isNumeric(indexStr) && Integer.parseInt(indexStr) > this.maxTableIndex)
					{
						this.maxTableIndex = Integer.parseInt(indexStr);
					}
				}
			}
		}
	}

	private void cacheClassFieldModel() throws Exception
	{
		// 缓存所有字段
		this.origClassFieldModel.clear();
		Map<String, List<ClassField>> fieldMap = this.loadClassFieldModel();
		if (!SetUtils.isNullMap(fieldMap))
		{
			for (String className : fieldMap.keySet())
			{
				this.origClassFieldModel.computeIfAbsent(className, k -> new HashMap<>());
				for (ClassField classField : fieldMap.get(className))
				{
					this.origClassFieldModel.get(className).put(classField.getFieldName(), classField);

					String columnName = classField.getColumnName();
					if (!StringUtils.isNullString(columnName) && columnName.startsWith("F_"))
					{
						String indexStr = columnName.substring(2);
						if (NumberUtils.isNumeric(indexStr) && Integer.parseInt(indexStr) > this.maxColumnIndex)
						{
							this.maxColumnIndex = Integer.parseInt(indexStr);
						}
					}
				}
			}
		}
	}

	/**
	 * 根据数据对象同步数据库
	 *
	 * @param classObject 需要同步的对象
	 */
	private void syncModel(ClassObject classObject) throws Exception
	{
		// 没有基类，就没有基表，就只能更新MA_CLASS中数据，而不能创建FIELD数据
		this.updateMaClass(classObject, classObject.getBaseClass());
		if (classObject.getChildList() != null && classObject.getChildList().size() > 0)
		{
			for (ClassObject childObject : classObject.getChildList())
			{
				this.syncModel(childObject);
			}
		}
	}

	/**
	 * 模型中没有的数据删除
	 */
	private void deleteFromModel() throws Exception
	{
		for (String className : this.origClassDataMap.keySet())
		{
			ClassInfo dbClassObject = this.origClassDataMap.get(className);
			// 数据库中的类在模型中不存在
			if (!this.classGuidMap.containsValue(dbClassObject.getGuid()))
			{
				this.classInfoMapper.delete(dbClassObject.getGuid());
				this.classFieldMapper.deleteBy(dbClassObject.getGuid());
			}
		}

		for (String className : this.origClassFieldModel.keySet())
		{
			Map<String, ClassField> origFieldMap = this.origClassFieldModel.get(className);
			List<String> fieldGuidList = this.fieldGuidMap.get(className);
			if (fieldGuidList != null)
			{
				for (String origFieldName : origFieldMap.keySet())
				{
					ClassField origClassField = origFieldMap.get(origFieldName);
					// 数据库中的字段在模型中不存在
					if (!fieldGuidList.contains(origClassField.getGuid()))
					{
						this.classFieldMapper.delete(origClassField.getGuid());
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

	/**
	 * 加载主分类
	 *
	 * @throws SQLException
	 */
	private void loadMasterClassification() throws Exception
	{
		List<CodeObjectInfo> codeObjectInfo = this.codeObjectInfoMapper.selectForLoad();
		if (!SetUtils.isNullList(codeObjectInfo))
		{
			for (CodeObjectInfo codeObject_ : codeObjectInfo)
			{
				if (codeObject_.isClassification())
				{
					this.masterClassificationMap.put(codeObject_.getName(), codeObject_.getGuid());
				}
				else
				{
					this.masterCodeMap.put(codeObject_.getName(), codeObject_.getGuid());
				}
			}
		}
	}

	private void loadFieldOfInterface(ClassObject classObject)
	{
		List<String> interfaceList = new ArrayList<>();
		this.getInterfaces(classObject, interfaceList);

		if (!SetUtils.isNullList(interfaceList))
		{
			for (String interfaceName : interfaceList)
			{
				List<ClassField> list = this.dataModel.getInterfaceFieldMap().get(interfaceName);
				if (!SetUtils.isNullList(list))
				{
					for (ClassField classField : list)
					{
						// classField.setFromInterface(true);
						ClassField field_ = this.getClassFieldFromList(classObject.getFieldList(), classField);
						// 该接口字段在类模型上不存在，添加到类模型上
						if (field_ == null)
						{
							classObject.addField(classField);
						}
						else
						{
							// 从接口继承字段,接口字段的类型不允许被覆盖
							field_.setType(classField.getType());
							field_.setSystem(classField.isSystem());
							field_.setBuiltin(classField.isBuiltin());
							field_.setRollback(classField.isRollback());
						}
					}
				}
			}
		}

		// 接口字段删除
		List<ClassField> classFieldList = new ArrayList<>();
		if (!SetUtils.isNullList(classObject.getFieldList()))
		{
			for (ClassField field : classObject.getFieldList())
			{
				if (field.isSystem() && !this.systemInsClassFieldList.contains(field.getName()) && !this.systemStrucClassFieldList.contains(field.getName()))
				{
					classFieldList.add(field);
				}
			}
		}

		if (!SetUtils.isNullList(classFieldList))
		{
			for (ClassField field : classFieldList)
			{
				classObject.removeField(field);
			}
		}

		List<ClassObject> childList = classObject.getChildList();
		if (!SetUtils.isNullList(childList))
		{
			for (ClassObject child : childList)
			{
				this.loadFieldOfInterface(child);
			}
		}
	}

	/**
	 * 取得指定类的所有接口名
	 *
	 * @param classObject
	 * @param interfaceList
	 */
	private void getInterfaces(ClassObject classObject, List<String> interfaceList)
	{
		String interfaces = classObject.getInterfaces();
		if (!StringUtils.isNullString(interfaces))
		{
			String[] interfaceNames = interfaces.split(";");
			for (String interfaceName : interfaceNames)
			{
				if (!interfaceList.contains(interfaceName))
				{
					interfaceList.add(interfaceName);
				}
			}
		}

		String superClass = classObject.getSuperclass();
		if (!StringUtils.isNullString(superClass))
		{
			ClassObject superClassObject = this.dataModel.getClassObject(superClass);
			this.getInterfaces(superClassObject, interfaceList);
		}
	}

	private boolean canReusedColumnFieldType(FieldTypeEnum fieldType1, String typeValue1, FieldTypeEnum fieldType2, String typeValue2)
	{
		boolean isSpecialObject1 = fieldType1 == FieldTypeEnum.OBJECT && this.isSpecialTypeValue(typeValue1);
		boolean isSpecialObject2 = fieldType2 == FieldTypeEnum.OBJECT && this.isSpecialTypeValue(typeValue2);

		if (fieldType1 == fieldType2)
		{
			return (!isSpecialObject1 || isSpecialObject2) && (isSpecialObject1 || !isSpecialObject2);
		}
		else if (!isSpecialObject1 && !isSpecialObject2)
		{
			if (fieldType1 == FieldTypeEnum.OBJECT || fieldType2 == FieldTypeEnum.OBJECT)
			{
				return false;
			}
		}

		String columnType1 = this.transferFieldType2ColumnType(fieldType1);
		String columnType2 = this.transferFieldType2ColumnType(fieldType2);
		return columnType1.equals(columnType2);
	}

	private String transferFieldType2ColumnType(FieldTypeEnum fieldType)
	{
		if (fieldType == FieldTypeEnum.DATE || fieldType == FieldTypeEnum.DATETIME)
		{
			return "Date";
		}
		if (fieldType == FieldTypeEnum.FLOAT || fieldType == FieldTypeEnum.INTEGER)
		{
			return "Number";
		}
		return "Varchar2";
	}

	/**
	 * 取得同一个createtable节点之下的所有同级类
	 *
	 * @param classObject
	 * @param classObjectMap
	 * @param sameLevelClassObjectList
	 */
	private List<ClassObject> getSameLevelClassObject(ClassObject classObject)
	{
		List<ClassObject> list = new ArrayList<>();
		String superClass = classObject.getSuperclass();
		if (!StringUtils.isNullString(superClass))
		{
			ClassObject superClassObject = this.dataModel.getClassObject(superClass);
			List<ClassObject> childList = superClassObject.getChildList();
			for (ClassObject child : childList)
			{
				if (!child.getName().equals(classObject.getName()))
				{
					list.add(child);
				}
			}
		}
		return list;
	}

	/**
	 * 取得当前类及其子类的所有字段
	 *
	 * @param classObject
	 * @return
	 */
	private void listAllFieldWithChild(ClassObject classObject, List<ClassField> fieldList)
	{
		if (!SetUtils.isNullList(classObject.getFieldList()))
		{
			fieldList.addAll(classObject.getFieldList());
		}

		if (!SetUtils.isNullList(classObject.getChildList()))
		{
			for (ClassObject child : classObject.getChildList())
			{
				this.listAllFieldWithChild(child, fieldList);
			}
		}
	}

	/**
	 * 当前类及其子类已使用的所有columnname，不能被复用
	 *
	 * @param classObject
	 * @return
	 */
	private List<String> getAllColumnWithChildList(ClassObject classObject)
	{
		List<String> allColumnWithChildList = new ArrayList<>();
		List<ClassField> allFieldWithChildList = new ArrayList<>();
		this.listAllFieldWithChild(classObject, allFieldWithChildList);
		if (!SetUtils.isNullList(allFieldWithChildList))
		{
			for (ClassField classField : allFieldWithChildList)
			{
				if (!StringUtils.isNullString(classField.getColumnName()) && !allColumnWithChildList.contains(classField.getColumnName()))
				{
					allColumnWithChildList.add(classField.getColumnName());
				}
			}
		}
		return allColumnWithChildList;
	}

	public String getReUsedColumn(ClassObject classObject, ClassField field)
	{
		// 取得父类上字段的列名
		if (field.isInherited())
		{
			String columnName = this.getColumnNameOfSuperClass(classObject.getSuperclass(), field);
			if (!StringUtils.isNullString(columnName))
			{
				return columnName;
			}
		}

		// 只有和当前类同级的其他类上的字段，才能够被复用
		List<ClassObject> sameLevelClassObjectList = this.getSameLevelClassObject(classObject);
		if (!SetUtils.isNullList(sameLevelClassObjectList))
		{
			// 从其他类中找出和当前字段名字相同，且类型可以复用的字段
			ClassField field_ = this.getFieldFromLevelOfOtherClass(field, sameLevelClassObjectList);
			if (field_ != null)
			{
				// 该列是否已经被使用
				List<String> allColumnWithChildList = this.getAllColumnWithChildList(classObject);
				if (!StringUtils.isNullString(field_.getColumnName()) && !allColumnWithChildList.contains(field_.getColumnName()))
				{
					return field_.getColumnName();
				}
			}

			for (ClassObject child : sameLevelClassObjectList)
			{
				List<ClassField> fieldListOfChildClass = child.getFieldList();
				if (!SetUtils.isNullList(fieldListOfChildClass))
				{
					for (ClassField classField : fieldListOfChildClass)
					{
						// 其他类上的字段，在当前类上有相同名字存在的，不复用
						if (this.isFieldOfClass(classObject, classField))
						{
							continue;
						}

						if (!StringUtils.isNullString(classField.getColumnName()) && this
								.canReusedColumnFieldType(field.getType(), field.getTypeValue(), classField.getType(), classField.getTypeValue()))
						{
							// 该列是否已经被使用
							List<String> allColumnWithChildList = this.getAllColumnWithChildList(classObject);
							if (!allColumnWithChildList.contains(classField.getColumnName()))
							{
								return classField.getColumnName();
							}
						}
					}
				}
			}
		}

		return null;
	}

	private String getColumnNameOfSuperClass(String superClass, ClassField subField)
	{
		if (!StringUtils.isNullString(superClass))
		{
			ClassObject classObject = this.dataModel.getClassObject(superClass);
			if (classObject != null)
			{
				ClassField field = classObject.getField(subField.getName());
				if (field != null && this.canReusedColumnFieldType(field.getType(), field.getTypeValue(), subField.getType(), subField.getTypeValue()))
				{
					return field.getColumnName();
				}
			}
		}
		return null;
	}

	/**
	 * 判断字段是否是类的字段或者是子类的字段
	 *
	 * @param classObject
	 * @param classField
	 * @return
	 */
	private boolean isFieldOfClass(ClassObject classObject, ClassField classField)
	{
		if (classObject.getField(classField.getName()) == null)
		{
			List<ClassObject> children = classObject.getChildList();
			if (!SetUtils.isNullList(children))
			{
				for (ClassObject child : children)
				{
					return this.isFieldOfClass(child, classField);
				}
			}
		}
		return true;
	}

	/**
	 * 从相同层级的其他类上，取相同名字类型可以复用的字段，优先使用已生成列名的字段
	 *
	 * @param field
	 * @param classList
	 * @return
	 */
	private ClassField getFieldFromLevelOfOtherClass(ClassField field, List<ClassObject> classList)
	{
		ClassField classField = null;
		if (!SetUtils.isNullList(classList))
		{
			for (ClassObject classObject : classList)
			{
				List<ClassField> fieldList = classObject.getFieldList();
				if (!SetUtils.isNullList(fieldList))
				{
					for (ClassField field_ : fieldList)
					{
						if (field.getName().equals(field_.getName()) && this
								.canReusedColumnFieldType(field.getType(), field.getTypeValue(), field_.getType(), field_.getTypeValue()))
						{
							classField = field_;
							if (!StringUtils.isNullString(field_.getColumnName()))
							{
								return field_;
							}
						}
					}
				}
			}
		}
		return classField;
	}

	// 格式化表名
	private String getNewTableName(int index)
	{
		return new DecimalFormat("NT_00000").format(index);
	}

	// 格式化列名
	private String getNewColumnName(int index)
	{
		return new DecimalFormat("F_000000").format(index);
	}

	/**
	 * 取得所有类的字段信息(包含接口字段)
	 *
	 * @return
	 * @throws SQLException
	 */
	private Map<String, List<ClassField>> loadClassFieldModel() throws Exception
	{
		Map<String, List<ClassField>> resultMap = new HashMap<>();
		List<ClassField> fieldList = this.classFieldMapper.selectForLoad();
		for (ClassField field : fieldList)
		{
			String className = (String) field.get("CLASSNAME");
			resultMap.computeIfAbsent(className, k -> new ArrayList<>());
			resultMap.get(className).add(field);
		}

		return resultMap;
	}

	/**
	 * 根据提供的字段名，从字段列表中取出对应的字段
	 *
	 * @param classFieldList
	 * @param field
	 * @return
	 */
	private ClassField getClassFieldFromList(List<ClassField> classFieldList, ClassField field)
	{
		if (SetUtils.isNullList(classFieldList))
		{
			return null;
		}

		for (ClassField classField : classFieldList)
		{
			if (classField.getName().equals(field.getName()))
			{
				return classField;
			}
		}
		return null;
	}

	private void syncUseTableNameOfClass() throws Exception
	{
		List<ClassInfo> classObjectList = this.classInfoMapper.selectFirstClassWithCreateTable();
		for (ClassInfo classObject : classObjectList)
		{
			this.updateRealbaseTableName(classObject.getGuid(), classObject.getBasetableName());
		}
	}

	private void updateRealbaseTableName(String classGuid, String realbaseTableName) throws SQLException
	{
		ClassInfo classInfo_ = new ClassInfo();
		classInfo_.setRealBaseTableName(realbaseTableName);
		classInfo_.setGuid(classGuid);
		this.classInfoMapper.update(classInfo_);

		classInfo_ = new ClassInfo();
		classInfo_.setSuperClassGuid(classGuid);
		List<ClassInfo> childList = this.classInfoMapper.select(classInfo_);
		if (!SetUtils.isNullList(childList))
		{
			for (ClassInfo child : childList)
			{
				this.updateRealbaseTableName(child.getGuid(), realbaseTableName);
			}
		}
	}

	private String updateMaClass(ClassObject classObject, String interfaceNames) throws Exception
	{
		String classificationGuid = null;
		if (!StringUtils.isNullString(classObject.getClassification()))
		{
			classificationGuid = this.masterClassificationMap.get(classObject.getClassification());
		}

		String lifecycleGuid = this.lifecycleNameGuidMap.get(classObject.getLifecycle());

		if (classObject.getSuperclass() != null)
		{
			classObject.setSuperClassGuid(this.classGuidMap.get(classObject.getSuperclass()));
		}

		System.out.println("Sync Class:" + classObject.getName());

		ClassInfo classObject_ = new ClassInfo();
		classObject_.setName(classObject.getName());
		ClassInfo classInfo = (ClassInfo) this.classInfoMapper.select(classObject_);
		if (classInfo != null)
		{
			// 当父类发生改变时,需要清空当前类的所有字段column
			if (classObject.getSuperclass() != null)
			{
				ClassInfo superClassInfoData = this.origClassDataMap.get(classObject.getSuperclass());
				if (superClassInfoData != null && StringUtils.isGuid(superClassInfoData.getGuid()) && !superClassInfoData.getGuid().equals(classObject.getSuperClassGuid()))
				{
					this.clearColumn(classObject);
				}
			}
			classObject.setGuid(classInfo.getGuid());
			classObject.setLifecycle(lifecycleGuid);
			classObject.setClassification(classificationGuid);
			classObject.setSuperInterface(classObject.getInterfaces());
			classObject.setInterfaces(interfaceNames);
			classObject.setNoQueryable(classObject.hasInterface(ModelInterfaceEnum.INonQueryable));
			classObject.setGuid(classInfo.getGuid());
			classObject.setBasetableName(classInfo.getBasetableName());
			ClassInfo classInfo_ = classObject.getInfo();
			classInfo_.setUpdateUserGuid(this.userGuid);
			classInfo_.put("CURRENTTIME", DateFormat.getSysDate());
			this.classInfoMapper.update(classInfo_);
			this.classGuidMap.put(classObject.getName(), classInfo.getGuid());
		}
		else
		{
			classObject.setGuid(this.genericGuid());
			classObject.setLifecycle(lifecycleGuid);
			classObject.setClassification(classificationGuid);
			classObject.setSuperInterface(classObject.getInterfaces());
			classObject.setInterfaces(interfaceNames);
			classObject.setNoQueryable(classObject.hasInterface(ModelInterfaceEnum.INonQueryable));
			classObject.setBasetableName(this.getBaseTableName(classObject.getName()));

			ClassInfo classInfo_ = classObject.getInfo();
			classInfo_.setUpdateUserGuid(this.userGuid);
			classInfo_.setCreateUserGuid(this.userGuid);
			classInfo_.put("CURRENTTIME", DateFormat.getSysDate());
			this.classInfoMapper.insert(classInfo_);
			this.classGuidMap.put(classObject.getName(), classObject.getGuid());
		}
		this.origClassFieldModel.computeIfAbsent(classObject.getName(), k -> new HashMap<>());

		if (!SetUtils.isNullList(classObject.getFieldList()))
		{
			for (ClassField field : classObject.getFieldList())
			{
				this.updateMaField(field, classObject);
			}
		}

		return classObject.getGuid();
	}

	private void clearColumn(ClassObject classObject)
	{
		Map<String, ClassField> map = this.origClassFieldModel.get(classObject.getName());
		if (!SetUtils.isNullMap(map))
		{
			for (ClassField classField : map.values())
			{
				classField.setColumnName(null);
			}
		}

		List<ClassObject> childList = classObject.getChildList();
		if (!SetUtils.isNullList(childList))
		{
			for (ClassObject child : childList)
			{
				this.clearColumn(child);
			}
		}
	}

	private String getBaseTableName(String className)
	{
		String tableName = null;
		if (!SetUtils.isNullMap(this.origClassDataMap))
		{
			ClassInfo classInfo = this.origClassDataMap.get(className);
			if (classInfo != null)
			{
				tableName = classInfo.getBasetableName();
			}
		}
		if (StringUtils.isNullString(tableName))
		{
			return this.getNewTableName();
		}
		return tableName;
	}

	private String getNewTableName()
	{
		this.maxTableIndex = this.maxTableIndex + 1;
		return this.getNewTableName(this.maxTableIndex);
	}

	private void updateMaField(ClassField classField, ClassObject classObject) throws SQLException
	{
		if (classField.getType() == FieldTypeEnum.MULTICODE)
		{
			classField.setFieldSize("4000");
		}
		else if (classField.getType() == FieldTypeEnum.STRING && StringUtils.isNullString(classField.getFieldSize()))
		{
			classField.setFieldSize("128");
		}
		if (classField.getType() == FieldTypeEnum.FLOAT || classField.getType() == FieldTypeEnum.INTEGER || classField.getType() == FieldTypeEnum.BOOLEAN
				|| classField.getType() == FieldTypeEnum.DATE || classField.getType() == FieldTypeEnum.DATETIME)
		{
			classField.setTypeValue(null);
		}

		ClassField classField_ = new ClassField();
		classField_.setClassGuid(classObject.getGuid());
		classField_.setFieldName(classField.getFieldName());
		classField_ = (ClassField) this.classFieldMapper.select(classField_);
		if (classField_ != null)
		{
			classField.setGuid(classField_.getGuid());
			classField.setClassGuid(classObject.getGuid());
			classField.setUpdateUserGuid(this.userGuid);
			classField.put("CURRENTTIME", DateFormat.getSysDate());
			classField.setColumnName(this.getColumnName(classObject.getName(), classField));
			this.classFieldMapper.update(classField);
		}
		else
		{
			classField.setGuid(this.genericGuid());
			classField.setClassGuid(classObject.getGuid());
			classField.setCreateUserGuid(this.userGuid);
			classField.setUpdateUserGuid(this.userGuid);
			classField.put("CURRENTTIME", DateFormat.getSysDate());
			classField.setColumnName(this.getColumnName(classObject.getName(), classField));
			this.classFieldMapper.insert(classField);
		}
		this.origClassFieldModel.get(classObject.getName()).put(classField.getFieldName(), classField);
		if (!fieldGuidMap.containsKey(classObject.getName()))
		{
			fieldGuidMap.put(classObject.getName(), new ArrayList<>());
		}
		this.fieldGuidMap.get(classObject.getName()).add(classField.getGuid());
		if (classField.getReferenceCodeList() != null)
		{
			this.classFieldCRDetailMapper.delete(classField.getGuid());

			for (ReferenceCode refCode : classField.getReferenceCodeList())
			{
				if (refCode.getCodeName() == null)
				{
					continue;
				}

				StringBuilder refItems = new StringBuilder();
				for (int i = 0; i < classField.getRelationFieldList().size(); i++)
				{
					refItems.append(refCode.getRefFieldList().get(i).getItemName());
					refItems.append(";");
				}
				refItems.deleteCharAt(refItems.length() - 1);
				String codeGuid = this.masterCodeMap.get(refCode.getCodeName());
				if (codeGuid == null)
				{
					codeGuid = this.masterClassificationMap.get(refCode.getCodeName());
				}

				this.updateMaClassFieldCrDetail(refItems.toString(), classField.getGuid(), codeGuid);
			}
		}
	}

	private String getColumnName(String className, ClassField classFieldData)
	{
		if (classFieldData.isSystem())
		{
			if (classFieldData.getName().equals(SystemClassFieldEnum.ID.getName()))
			{
				return "MD_ID";
			}
			if (classFieldData.getName().equals(SystemClassFieldEnum.NAME.getName()))
			{
				return "MD_NAME";
			}
			if (classFieldData.getName().equals(SystemClassFieldEnum.REPEAT.getName()))
			{
				return "REPEATVALUE";
			}
			return classFieldData.getFieldName().toUpperCase().substring(0, classFieldData.getFieldName().length() - 1);
		}
		if (classFieldData.isBuiltin())
		{
			return classFieldData.getFieldName().toUpperCase();
		}

		String columnName = null;
		if (!SetUtils.isNullMap(this.origClassFieldModel))
		{
			Map<String, ClassField> classFieldMap = this.origClassFieldModel.get(className);
			if (!SetUtils.isNullMap(classFieldMap))
			{
				ClassField classField = classFieldMap.get(classFieldData.getFieldName());
				if (classField != null)
				{
					FieldTypeEnum fieldTypeModel = classFieldData.getType();
					FieldTypeEnum fieldTypeOrig = classField.getType();
					// 类型没有发生变化，或者类型发生了变化，但是数据列可以共用
					if (this.canReusedColumnFieldType(fieldTypeModel, classFieldData.getTypeValue(), fieldTypeOrig, classField.getTypeValue()))
					{
						columnName = classField.getColumnName();
					}
				}
			}
		}
		if (StringUtils.isNullString(columnName))
		{
			// 取得可以复用的列
			ClassObject classObject = this.dataModel.getClassObject(className);
			ClassField field = classObject.getField(classFieldData.getFieldName());
			columnName = this.getReUsedColumn(classObject, field);
			if (StringUtils.isNullString(columnName))
			{
				columnName = this.getNewColumnName();
				field.setColumnName(columnName);
			}
		}
		return columnName;
	}

	private String getNewColumnName()
	{
		this.maxColumnIndex = this.maxColumnIndex + 1;
		return this.getNewColumnName(this.maxColumnIndex);
	}

	private void updateMaClassFieldCrDetail(String refItems, String fieldGuid, String codeGuid) throws SQLException
	{
		DynaLogger.println("create FieldCRDetail: " + fieldGuid);

		ClassFieldCRDetail crDetailData = new ClassFieldCRDetail();
		crDetailData.setGuid(this.genericGuid());
		crDetailData.setFieldFK(fieldGuid);
		crDetailData.setCode(codeGuid);
		crDetailData.setValue(refItems);

		this.classFieldCRDetailMapper.insert(crDetailData);
	}

	private void saveClassTreeRelation(ClassObject classObject) throws SQLException
	{

		this.treeDataRelationMapper.deleteByType(TreeDataRelation.DATATYPE_CLASS);

		this.saveClassTreeRelationRecurse(classObject);
	}

	private void saveClassTreeRelationRecurse(ClassObject classObject) throws SQLException
	{
		TreeDataRelation relation = new TreeDataRelation();
		relation.setDataType(TreeDataRelation.DATATYPE_CLASS);
		relation.setDataGuid(this.classGuidMap.get(classObject.getName()));
		relation.setSubDataGuid(this.classGuidMap.get(classObject.getName()));
		relation.setCreateUserGuid(this.userGuid);
		this.treeDataRelationMapper.insert(relation);

		List<ClassObject> childList = classObject.getChildList();
		if (!SetUtils.isNullList(childList))
		{
			for (ClassObject child : childList)
			{
				relation = new TreeDataRelation();
				relation.setDataType(TreeDataRelation.DATATYPE_CLASS);
				relation.setDataGuid(this.classGuidMap.get(classObject.getName()));
				relation.setSubDataGuid(this.classGuidMap.get(child.getName()));
				relation.setCreateUserGuid(this.userGuid);
				this.treeDataRelationMapper.insert(relation);

				this.saveClassTreeRelationRecurse(child);
			}
		}
	}
}
