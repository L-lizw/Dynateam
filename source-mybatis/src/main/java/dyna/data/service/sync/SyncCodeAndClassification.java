package dyna.data.service.sync;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dyna.common.dtomapper.TreeDataRelationMapper;
import dyna.common.dtomapper.model.cls.ClassificationFieldMapper;
import dyna.common.dtomapper.model.code.CodeItemInfoMapper;
import dyna.common.dtomapper.model.code.CodeObjectInfoMapper;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.common.exception.DynaDataSqlException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SyncCodeAndClassification extends SyncService
{
	private final ModelXMLCache dynaModel;

	private final List<String>							codeGuidList		= new ArrayList<>();
	private final List<String>							codeItemGuidList	= new ArrayList<>();
	private final List<String>							fieldGuidList		= new ArrayList<>();

	private final Map<String, List<String>>				itemFieldNameMap	= new HashMap<>();
	private final HashMap<String, CodeObjectInfo>		codeObjectInfoMap	= new HashMap<>();
	private final HashMap<String, CodeItemInfo>			codeItemInfoMap		= new HashMap<>();
	private final HashMap<String, ClassificationField>	itemFieldMap		= new HashMap<>();
	private final Map<String, Integer>					maxColumnIndexMap	= new HashMap<>();
	private int											maxTableIndex		= 0;

	@Autowired
	private CodeObjectInfoMapper                        codeObjectInfoMapper;
	@Autowired
	private CodeItemInfoMapper                          codeItemInfoMapper;
	@Autowired
	private ClassificationFieldMapper                   classificationFieldMapper;
	@Autowired
	private TreeDataRelationMapper                      treeDataRelationMapper;

	public SyncCodeAndClassification(SqlSessionFactory sqlSessionFactory, String userGuid, ModelXMLCache dynaModel)
	{
		super(sqlSessionFactory, userGuid);
		this.dynaModel = dynaModel;
	}

	/**
	 * @throws SQLException
	 */
	public void startSync() throws Exception
	{
		// 初始化
		this.init();

		if (this.dynaModel != null && this.dynaModel.getCodeObjectMap() != null)
		{
			for (String codeObjectName : this.dynaModel.getCodeObjectMap().keySet())
			{
				CodeObject codeObject = this.dynaModel.getCodeObjectMap().get(codeObjectName);
				this.updateCodeMaster(codeObject);
			}
		}

		// 删除模型中没用的数据
		this.deleteSurplusCode();

		// 保存分类的树结构
		if (this.dynaModel != null && this.dynaModel.getCodeObjectMap() != null)
		{
			this.saveCodeTreeRelation();
		}
	}

	private void init() throws SQLException
	{
		List<CodeObjectInfo> codeObjectInfoList = this.codeObjectInfoMapper.select(null);
		if (!SetUtils.isNullList(codeObjectInfoList))
		{
			for (CodeObjectInfo codeObjectInfo : codeObjectInfoList)
			{
				this.codeObjectInfoMap.put(codeObjectInfo.getName(), codeObjectInfo);

				String baseTableName = codeObjectInfo.getBaseTableName();
				if (!StringUtils.isNullString(baseTableName) && Integer.parseInt(baseTableName) > this.maxTableIndex)
				{
					this.maxTableIndex = Integer.parseInt(baseTableName);
				}

				CodeItemInfo itemParam = new CodeItemInfo();
				itemParam.setMasterGuid(codeObjectInfo.getGuid());
				List<CodeItemInfo> codeItemInfoList = this.codeItemInfoMapper.select(itemParam);
				if (!SetUtils.isNullList(codeItemInfoList))
				{
					for (CodeItemInfo codeItemInfo : codeItemInfoList)
					{
						this.codeItemInfoMap.put(codeObjectInfo.getName() + "." + codeItemInfo.getName(), codeItemInfo);
						if (codeItemInfo.isClassification())
						{
							ClassificationField fieldInfo = new ClassificationField();
							fieldInfo.setClassificationGuid(codeItemInfo.getGuid());
							List<ClassificationField> fieldList = this.classificationFieldMapper.select(fieldInfo);
							if (!SetUtils.isNullList(fieldList))
							{
								for (ClassificationField field : fieldList)
								{
									this.itemFieldMap.put(codeObjectInfo.getName() + "." + codeItemInfo.getName() + "." + field.getName(), field);
								}
							}
						}
					}
				}
			}
		}

		this.buildMaxColumnIndex();
	}

	/**
	 * @param codeObject
	 * @throws SQLException
	 */
	private void updateCodeMaster(CodeObject codeObject) throws SQLException
	{
		CodeObjectInfo codeObjectInfo = this.codeObjectInfoMap.get(codeObject.getName());
		if (codeObjectInfo != null)
		{
			codeObjectInfo.setTitle(codeObject.getTitle());
			codeObjectInfo.setType(codeObject.getType());
			codeObjectInfo.setShowType(codeObject.getShowType());
			codeObjectInfo.setDescription(codeObject.getDescription());
			codeObjectInfo.setClassification(codeObject.isClassification());
			codeObjectInfo.setUpdateUserGuid(this.userGuid);
			String basetablename = codeObjectInfo.getBaseTableName();
			if (StringUtils.isNullString(basetablename))
			{
				basetablename = this.getNewBaseTableName();
				codeObjectInfo.setBaseTableName(basetablename);
			}
			codeObjectInfo.put("CURRENTTIME", DateFormat.getSysDate());
			this.codeObjectInfoMapper.update(codeObjectInfo);
		}
		else
		{
			codeObjectInfo = new CodeObjectInfo();
			codeObjectInfo.setGuid(this.genericGuid());
			codeObjectInfo.setCreateUserGuid(this.userGuid);
			codeObjectInfo.setUpdateUserGuid(this.userGuid);
			codeObjectInfo.setClassification(codeObject.isClassification());
			codeObjectInfo.setBaseTableName(this.getNewBaseTableName());
			codeObjectInfo.put("CURRENTTIME", DateFormat.getSysDate());
			codeObjectInfo.setName(codeObject.getName());
			codeObjectInfo.setShowType(codeObject.getShowType());
			codeObjectInfo.setTitle(codeObject.getTitle());
			codeObjectInfo.setType(codeObject.getType());
			codeObjectInfo.setDescription(codeObject.getDescription());
			this.codeObjectInfoMapper.insert(codeObjectInfo);
		}
		codeObject.getInfo().putAll(codeObjectInfo);
		this.codeObjectInfoMap.put(codeObject.getName(), codeObjectInfo);
		this.codeGuidList.add(codeObjectInfo.getGuid());

		if (codeObject.getCodeDetailList() != null)
		{
			codeObject.getCodeDetailList().sort(Comparator.comparingInt(CodeItem::getSequence));
			int sequence = 1;
			for (CodeItem classification : codeObject.getCodeDetailList())
			{
				this.updateCodeDetail(classification, codeObject.getName(), null, codeObject.isClassification(), sequence++);
			}
		}
	}

	private void updateCodeDetail(CodeItem classification, String classificationGroupName, String parentGuid, boolean isClassification, int sequence) throws SQLException
	{
		CodeItemInfo detail_ = this.codeItemInfoMap.get(classificationGroupName + "." + classification.getName());
		if (detail_ != null)
		{
			detail_.setParentGuid(parentGuid);
			detail_.setTitle(classification.getTitle());
			detail_.setDescription(classification.getDescription());
			detail_.setCode(classification.getCode());
			detail_.setSequence(sequence);
			detail_.setUpdateUserGuid(this.userGuid);
			detail_.setClassification(isClassification);
			detail_.put("CURRENTTIME", DateFormat.getSysDate());
			this.codeItemInfoMapper.update(detail_);
		}
		else
		{
			detail_ = new CodeItemInfo();
			detail_.setName(classification.getName());
			detail_.setMasterGuid(this.codeObjectInfoMap.get(classificationGroupName).getGuid());
			detail_.setGuid(this.genericGuid());
			detail_.setParentGuid(parentGuid);
			detail_.setTitle(classification.getTitle());
			detail_.setDescription(classification.getDescription());
			detail_.setCode(classification.getCode());
			detail_.setSequence(sequence);
			detail_.setCreateUserGuid(this.userGuid);
			detail_.setUpdateUserGuid(this.userGuid);
			detail_.setClassification(isClassification);
			detail_.put("CURRENTTIME", DateFormat.getSysDate());
			this.codeItemInfoMapper.insert(detail_);
		}

		classification.getInfo().putAll(detail_);
		this.codeItemInfoMap.put(classificationGroupName + "." + classification.getName(), detail_);
		this.codeItemGuidList.add(detail_.getGuid());

		if (isClassification)
		{
			this.itemFieldNameMap.computeIfAbsent(detail_.getGuid(), k -> new ArrayList<>());

			// 取得当前分类子项已使用的列名
			List<String> alreadyUsedColumns = this.getAlreadyUsedColumns(classificationGroupName, classification.getName());
			// 取得同级的其他分类的字段列表
			List<ClassificationField> fieldList_ = this.listAllFieldInSameLevelCode(classificationGroupName, classification.getName());

			this.updateField(classificationGroupName, classification, alreadyUsedColumns, fieldList_);
		}

		if (classification.getCodeDetailList() != null)
		{
			classification.getCodeDetailList().sort(Comparator.comparingInt(CodeItem::getSequence));
			int sequenceItem = 1;
			for (CodeItem chlidClassification : classification.getCodeDetailList())
			{
				this.updateCodeDetail(chlidClassification, classificationGroupName, detail_.getGuid(), isClassification, sequenceItem++);
			}
		}
	}

	private void updateField(String codeName, CodeItem codeItem, List<String> alreadyUsedColumns, List<ClassificationField> fieldList_) throws SQLException
	{
		List<ClassificationField> fieldList = codeItem.getClassificationFieldList();
		if (!SetUtils.isNullList(fieldList))
		{
			for (ClassificationField field : fieldList)
			{
				field.setInherited(false);
				if (StringUtils.isGuid(codeItem.getParentGuid()) && this.itemFieldNameMap.get(codeItem.getParentGuid()).contains(field.getFieldName()))
				{
					field.setInherited(true);
				}

				if (this.itemFieldNameMap.get(codeItem.getGuid()).contains(field.getFieldName()))
				{
					continue;
				}

				if (field.getType() == FieldTypeEnum.FLOAT || field.getType() == FieldTypeEnum.INTEGER || field.getType() == FieldTypeEnum.BOOLEAN
						|| field.getType() == FieldTypeEnum.DATE || field.getType() == FieldTypeEnum.DATETIME)
				{
					field.setTypeValue(null);
				}

				CodeObject codeObject = this.dynaModel.getCodeObjectMap().get(codeName);
				ClassificationField field_ = this.itemFieldMap.get(codeName + "." + codeItem.getName() + "." + field.getFieldName());
				if (field_ != null)
				{
					field_.setTitle(field.getTitle());
					field_.setDescription(field.getDescription());
					field_.setType(field.getType());
					field_.setTypeValue(field.getTypeValue());
					field_.setDefaultValue(field.getDefaultValue());
					field_.setValidityRegex(field.getValidityRegex());
					field_.setFieldSize(this.getFieldSize(field));
					field_.setMandatory(field.isMandatory());
					field_.setInherited(field.isInherited());
					field_.setUpdateUserGuid(this.userGuid);
					field_.setFieldSize(this.getFieldSize(field));
					if (StringUtils.isNullString(field_.getColumnName()) && field_.getType() != field.getType() && !this.canColumnReuse(field_.getType(), field.getType()))
					{
						field_.setColumnName(null);
						this.clearChildColumn(codeName, codeItem, field_.getFieldName());
					}
					field_.setColumnName(this.makeColumnName(codeObject, codeItem, field_, alreadyUsedColumns, fieldList_));
					field_.put("CURRENTTIME", DateFormat.getSysDate());
					this.classificationFieldMapper.update(field_);
				}
				else
				{
					field_ = new ClassificationField();
					field_.setGuid(this.genericGuid());
					field_.setClassificationGuid(codeItem.getGuid());
					field_.setName(field.getName());
					field_.setTitle(field.getTitle());
					field_.setDescription(field.getDescription());
					field_.setType(field.getType());
					field_.setTypeValue(field.getTypeValue());
					field_.setDefaultValue(field.getDefaultValue());
					field_.setValidityRegex(field.getValidityRegex());
					field_.setMandatory(field.isMandatory());
					field_.setInherited(field.isInherited());
					field_.setUpdateUserGuid(this.userGuid);
					field_.setCreateUserGuid(this.userGuid);
					field_.setFieldSize(this.getFieldSize(field));
					field_.setColumnName(this.makeColumnName(codeObject, codeItem, field_, alreadyUsedColumns, fieldList_));
					field_.put("CURRENTTIME", DateFormat.getSysDate());
					this.classificationFieldMapper.insert(field_);
				}

				codeItem.getClassificationField(field.getName()).putAll(field_);
				this.itemFieldMap.put(codeName + "." + codeItem.getName() + "." + field.getFieldName(), field_);
				this.itemFieldNameMap.get(codeItem.getGuid()).add(field_.getFieldName());
				this.fieldGuidList.add(field_.getGuid());
			}
		}
	}

	/**
	 * 清除子阶分类的列名
	 *
	 * @param codeName
	 * @param codeItemName
	 * @param fieldName
	 */
	private void clearChildColumn(String codeName, CodeItem codeItem, String fieldName)
	{
		List<CodeItem> codeItemList = codeItem.getCodeDetailList();
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItem child : codeItemList)
			{
				ClassificationField field = this.itemFieldMap.get(codeName + "." + codeItem.getName() + "." + fieldName);
				if (field != null)
				{
					field.setColumnName(null);
				}
				this.clearChildColumn(codeName, child, fieldName);
			}
		}
	}

	private boolean canColumnReuse(FieldTypeEnum origType, FieldTypeEnum destType)
	{
		if (this.isFixed32Column(origType) && this.isFixed32Column(destType))
		{
			return true;
		}
		if (this.isDateColumn(origType) && this.isDateColumn(destType))
		{
			return true;
		}
		if (this.isNumericColumn(origType) && this.isNumericColumn(destType))
		{
			return true;
		}
		return this.isVarcharColumn(origType) && this.isVarcharColumn(destType);
	}

	private boolean isVarcharColumn(FieldTypeEnum fieldTypeEnum)
	{
		return fieldTypeEnum == FieldTypeEnum.STRING || fieldTypeEnum == FieldTypeEnum.BOOLEAN || fieldTypeEnum == FieldTypeEnum.STATUS;
	}

	private boolean isNumericColumn(FieldTypeEnum fieldTypeEnum)
	{
		return fieldTypeEnum == FieldTypeEnum.INTEGER || fieldTypeEnum == FieldTypeEnum.FLOAT;
	}

	private boolean isDateColumn(FieldTypeEnum fieldTypeEnum)
	{
		return fieldTypeEnum == FieldTypeEnum.DATE || fieldTypeEnum == FieldTypeEnum.DATETIME;
	}

	private boolean isFixed32Column(FieldTypeEnum fieldTypeEnum)
	{
		List<FieldTypeEnum> codeTypeList = new ArrayList<>();
		codeTypeList.add(FieldTypeEnum.CLASSIFICATION);
		codeTypeList.add(FieldTypeEnum.CODE);
		codeTypeList.add(FieldTypeEnum.CODEREF);
		codeTypeList.add(FieldTypeEnum.FOLDER);
		codeTypeList.add(FieldTypeEnum.OBJECT);

		return codeTypeList.contains(fieldTypeEnum);
	}

	private String getFieldSize(ClassificationField field)
	{
		if (field.getType() == FieldTypeEnum.BOOLEAN)
		{
			return "1";
		}
		else if (field.getType() == FieldTypeEnum.CLASSIFICATION || field.getType() == FieldTypeEnum.CODE || field.getType() == FieldTypeEnum.CODEREF
				|| field.getType() == FieldTypeEnum.FOLDER || field.getType() == FieldTypeEnum.OBJECT)
		{
			return "32";
		}
		else if (field.getType() == FieldTypeEnum.MULTICODE)
		{
			return "4000";
		}
		else if (field.getType() == FieldTypeEnum.STATUS)
		{
			return "3";
		}
		else if (field.getType() == FieldTypeEnum.STRING && StringUtils.isNullString(field.getFieldSize()))
		{
			return "128";
		}
		return field.getFieldSize();
	}

	/**
	 * @throws SQLException
	 */
	private void deleteSurplusCode() throws SQLException
	{
		for (String codeName : this.codeObjectInfoMap.keySet())
		{
			CodeObjectInfo codeObjectInfo = this.codeObjectInfoMap.get(codeName);
			if (this.codeGuidList.contains(codeObjectInfo.getGuid()))
			{
				if (!codeObjectInfo.isClassification())
				{
					this.classificationFieldMapper.deleteByMaster(codeObjectInfo.getGuid());
				}
			}
			else
			{
				this.codeObjectInfoMapper.delete(codeObjectInfo.getGuid());
				this.codeItemInfoMapper.deleteBy(codeObjectInfo.getGuid());
				this.classificationFieldMapper.deleteByMaster(codeObjectInfo.getGuid());
			}
		}

		for (String key : this.codeItemInfoMap.keySet())
		{
			CodeItemInfo codeItemInfo = this.codeItemInfoMap.get(key);
			if (!this.codeItemGuidList.contains(codeItemInfo.getGuid()))
			{
				this.codeItemInfoMapper.delete(codeItemInfo.getGuid());
				this.classificationFieldMapper.deleteByItem(codeItemInfo.getGuid());
			}
		}

		for (String key : this.itemFieldMap.keySet())
		{
			ClassificationField field = this.itemFieldMap.get(key);
			if (!this.fieldGuidList.contains(field.getGuid()))
			{
				this.classificationFieldMapper.delete(field.getGuid());
			}
		}
	}

	private void buildMaxColumnIndex()
	{
		this.maxColumnIndexMap.clear();
		for (String key : this.itemFieldMap.keySet())
		{
			String[] keyArr = key.split("\\.");
			ClassificationField field = this.itemFieldMap.get(key);
			if (!StringUtils.isNullString(field.getColumnName()))
			{
				int index = Integer.parseInt(field.getColumnName().substring(2));
				if (maxColumnIndexMap.get(keyArr[0]) == null || maxColumnIndexMap.get(keyArr[0]) < index)
				{
					this.maxColumnIndexMap.put(keyArr[0], index);
				}
			}
		}
	}

	private String makeColumnName(CodeObject codeObject, CodeItem codeItem, ClassificationField field, List<String> alreadyUsedColumns, List<ClassificationField> fieldList)
	{
		// 取得可复用的列名（从同级的其他分类子项中查找，并排除已使用的列名）
		String columnName = this.getColumnName(codeObject.getName(), codeItem.getName(), field, alreadyUsedColumns, fieldList);
		alreadyUsedColumns.add(columnName);
		// 把列名推送到所有子阶分类
		if (!SetUtils.isNullList(codeItem.getCodeDetailList()))
		{
			for (CodeItem child : codeItem.getCodeDetailList())
			{
				child.getClassificationField(field.getName()).setColumnName(columnName);
				this.setColumnName(child, field.getName(), columnName);
			}
		}
		return columnName;
	}

	private void setColumnName(CodeItem codeItem, String fieldName, String columnName)
	{
		for (CodeItem child : codeItem.getCodeDetailList())
		{
			child.getClassificationField(fieldName).setInherited(true);
			child.getClassificationField(fieldName).setColumnName(columnName);
		}
	}

	/**
	 * 取得当前分类子项已使用的列名
	 *
	 * @param codeName
	 * @param codeItemName
	 * @return
	 */
	private List<String> getAlreadyUsedColumns(String codeName, String codeItemName)
	{
		List<ClassField> fieldList = this.dynaModel.getCodeObjectMap().get(codeName).getCodeItem(codeItemName).getFieldList();
		List<String> alreadyUsedColumns = new ArrayList<>();
		if (!SetUtils.isNullList(fieldList))
		{
			for (ClassField field : fieldList)
			{
				if (!StringUtils.isNullString(field.getColumnName()))
				{
					alreadyUsedColumns.add(field.getColumnName());
				}
				else if (field.isInherited())
				{
					CodeItem parent = this.dynaModel.getCodeObjectMap().get(codeName).getCodeItem(codeItemName).getParent();
					alreadyUsedColumns.add(parent.getField(field.getName()).getColumnName());
				}
			}
		}
		return alreadyUsedColumns;
	}

	private String getColumnName(String codeName, String codeItemName, ClassificationField field, List<String> alreadyUsedColumns, List<ClassificationField> fieldList)
	{
		if (!StringUtils.isNullString(field.getColumnName()))
		{
			return field.getColumnName();
		}
		// 取得父阶分类子项
		CodeObject codeObject = this.dynaModel.getCodeObjectMap().get(codeName);
		CodeItem codeItem = codeObject.getCodeItem(codeItemName);
		CodeItem parent = codeObject.getCodeItem(codeItem.getParentName());

		// 继承字段，取得父阶列名
		if (field.isInherited())
		{
			return parent.getClassificationField(field.getName()).getColumnName();
		}

		String columnName;
		ClassificationField findField = this.findCanReuseColumnField(field, alreadyUsedColumns, fieldList);
		if (findField != null)
		{
			columnName = findField.getColumnName();
			if (StringUtils.isNullString(columnName))
			{
				columnName = this.getNewColumnName(codeName);
				findField.setColumnName(columnName);
			}
		}
		else
		{
			columnName = this.getNewColumnName(codeName);
		}
		return columnName;
	}

	/**
	 * 取得可以被复用的列名
	 * <br/>
	 * 从相同层级的其他分类子项上找可以复用的列
	 * <br/>
	 * 有相同名字的优先使用
	 *
	 * @param codeName
	 * @param codeItemName
	 * @return
	 */
	private ClassificationField findCanReuseColumnField(ClassificationField field, List<String> alreadyUsedColumns, List<ClassificationField> fieldList)
	{
		if (!SetUtils.isNullList(fieldList))
		{
			ClassificationField findField = null;
			for (Iterator<ClassificationField> it = fieldList.iterator(); it.hasNext();)
			{
				ClassificationField field_ = it.next();
				// 已经被使用过的不再使用
				if (alreadyUsedColumns.contains(field_.getColumnName()))
				{
					it.remove();
				}
				// 类型不能复用的不用
				else if (!this.canColumnReuse(field.getType(), field_.getType()))
				{
					continue;
				}
				if (field.getFieldName().equals(field_.getFieldName()))
				{
					findField = field_;
				}
			}
			if (findField == null && !SetUtils.isNullList(fieldList))
			{
				// 优先取类型相同的
				Optional<ClassificationField> find = fieldList.stream().filter(field_ -> field_.getType() == field.getType()).findFirst();
				if (find.isPresent())
				{
					return find.get();
				}
				else
				{
					// 再取类型不同但是可以复用的
					find = fieldList.stream().filter(field_ -> this.canColumnReuse(field.getType(), field_.getType())).findFirst();
					if (find.isPresent())
					{
						return find.get();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 取得同级的其他分类的字段列表
	 *
	 * @return
	 */
	private List<ClassificationField> listAllFieldInSameLevelCode(String codeName, String codeItemName)
	{
		List<ClassificationField> fieldList = new ArrayList<>();
		// 取得同级的其他分类子项
		List<CodeItem> codeItemList = this.listSameLevelCode(codeName, codeItemName);
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItem codeItem : codeItemList)
			{
				List<ClassificationField> fieldList_ = codeItem.getClassificationFieldList();
				if (!SetUtils.isNullList(fieldList_))
				{
					fieldList_ = fieldList_.stream().filter(field -> !StringUtils.isNullString(field.getColumnName())).collect(Collectors.toList());
				}
				if (!SetUtils.isNullList(fieldList_))
				{
					fieldList.addAll(fieldList_);
				}
			}
		}
		return fieldList;
	}

	/**
	 * 取得当前分类同级的其他分类
	 *
	 * @return
	 */
	private List<CodeItem> listSameLevelCode(String codeName, String codeItemName)
	{
		Map<Integer, List<CodeItem>> codeItemOfLevelMap = new HashMap<>();
		List<CodeItem> childList = this.dynaModel.getCodeObjectMap().get(codeName).getCodeDetailList();
		for (CodeItem codeItem : childList)
		{
			codeItemOfLevelMap.computeIfAbsent(0, k -> new ArrayList<>()).add(codeItem);
			this.buildCodeItemLevel(codeItem, 0, codeItemOfLevelMap);
		}

		for (int level : codeItemOfLevelMap.keySet())
		{
			List<CodeItem> itemList = codeItemOfLevelMap.get(level);
			Optional<CodeItem> find = itemList.stream().filter(item -> codeItemName.equals(item.getName())).findAny();
			if (find.isPresent())
			{
				return itemList.stream().filter(item -> !codeItemName.equals(item.getName())).collect(Collectors.toList());
			}
		}

		return null;
	}

	private void buildCodeItemLevel(CodeItem codeItem, int level, Map<Integer, List<CodeItem>> codeItemOfLevelMap)
	{
		if (!SetUtils.isNullList(codeItem.getCodeDetailList()))
		{
			for (CodeItem codeItem_ : codeItem.getCodeDetailList())
			{
				int level_ = level + 1;
				codeItemOfLevelMap.computeIfAbsent(level_, k -> new ArrayList<>()).add(codeItem_);
				this.buildCodeItemLevel(codeItem_, level_, codeItemOfLevelMap);
			}
		}
	}

	private String getNewBaseTableName()
	{
		// 表名由三位流水，左补0
		this.maxTableIndex++;
		return new DecimalFormat("00000").format(this.maxTableIndex);
	}

	private String getNewColumnName(String codeName)
	{
		int nextColumnIndex = 1;
		if (this.maxColumnIndexMap.containsKey(codeName))
		{
			nextColumnIndex = this.maxColumnIndexMap.get(codeName) + 1;
		}
		this.maxColumnIndexMap.put(codeName, nextColumnIndex);
		return "F_" + new DecimalFormat("000000").format(nextColumnIndex);
	}

	private void saveCodeTreeRelation() throws SQLException
	{

		this.treeDataRelationMapper.deleteByType(TreeDataRelation.DATATYPE_CODEITEM);

		for (CodeObject codeObject : this.dynaModel.getCodeObjectMap().values())
		{
			List<CodeItem> codeItemList = codeObject.getCodeDetailList();
			if (!SetUtils.isNullList(codeItemList))
			{
				for (CodeItem codeItem : codeItemList)
				{
					this.saveClassificationTreeRelationRecurse(codeItem);
				}
			}
		}
	}

	private void saveClassificationTreeRelationRecurse(CodeItem codeItem) throws SQLException
	{
		TreeDataRelation relation = new TreeDataRelation();
		relation.setDataType(TreeDataRelation.DATATYPE_CODEITEM);
		relation.setDataGuid(codeItem.getGuid());
		relation.setSubDataGuid(codeItem.getGuid());
		relation.setCreateUserGuid(this.userGuid);
		relation.setGuid(this.genericGuid());
		this.treeDataRelationMapper.insert(relation);

		List<CodeItem> codeItemList = codeItem.getCodeDetailList();
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItem codeItem_ : codeItemList)
			{
				relation = new TreeDataRelation();
				relation.setDataType(TreeDataRelation.DATATYPE_CODEITEM);
				relation.setDataGuid(codeItem.getGuid());
				relation.setSubDataGuid(codeItem_.getGuid());
				relation.setCreateUserGuid(this.userGuid);
				relation.setGuid(this.genericGuid());
				this.treeDataRelationMapper.insert(relation);
				this.saveClassificationTreeRelationRecurse(codeItem_);
			}
		}
	}

	public void startSyncForClassificationField(List<Map<String, String>> dataSource) throws SQLException
	{
		try
		{
			// 初始化
			this.init();

			Map<String, Map<String, CodeItem>> sourceMap = this.generateUpdateClassificationObject(dataSource);
			if (!SetUtils.isNullMap(sourceMap))
			{
				for (Map.Entry<String, Map<String, CodeItem>> entry : sourceMap.entrySet())
				{
					String classificationGroupName = entry.getKey();
					if (!SetUtils.isNullMap(entry.getValue()))
					{
						for (Map.Entry<String, CodeItem> childEntry : entry.getValue().entrySet())
						{
							// 取得当前分类子项已使用的列名
							List<String> alreadyUsedColumns = this.getAlreadyUsedColumns(classificationGroupName, childEntry.getKey());
							// 取得同级的其他分类的字段列表
							List<ClassificationField> fieldList_ = this.listAllFieldInSameLevelCode(classificationGroupName, childEntry.getKey());

							this.updateField(classificationGroupName, childEntry.getValue(), alreadyUsedColumns, fieldList_);
						}
					}
				}
			}

			// 删除模型中没用的数据
			this.deleteSurplusCode();

			// 保存分类的树结构
			if (this.dynaModel != null && this.dynaModel.getCodeObjectMap() != null)
			{
				this.saveCodeTreeRelation();
			}

			DynaLogger.info("Sucess to synchronize classification model.");
		}
		catch (DynaDataSqlException e)
		{
			throw new DynaDataSqlException("Synchronize classification model error", e);
		}
	}

	private Map<String, Map<String, CodeItem>> generateUpdateClassificationObject(List<Map<String, String>> dataSource)
	{
		Map<String, Map<String, CodeItem>> updateClassificationObjectMap = new HashMap<String, Map<String, CodeItem>>();
		for (Map<String, String> dataMap : dataSource)
		{
			String codeName = dataMap.get("groupname");
			String itemName = dataMap.get("classificationname");
			ClassificationField classField = makeClassField(dataMap);
			if (updateClassificationObjectMap.get(codeName) == null)
			{
				updateClassificationObjectMap.put(codeName, new HashMap<String, CodeItem>());

			}
			if (updateClassificationObjectMap.get(codeName).get(itemName) == null)
			{
				CodeItem item = this.dynaModel.getCodeObjectMap().get(codeName).getCodeItem(itemName);
				updateClassificationObjectMap.get(codeName).put(itemName, item);
			}
			CodeItem codeItem = updateClassificationObjectMap.get(codeName).get(itemName);

			List<ClassificationField> fieldList = new ArrayList<ClassificationField>();
			fieldList.add(classField);
			codeItem.setClassificationFieldList(fieldList);
		}

		return updateClassificationObjectMap;
	}

	private ClassificationField makeClassField(Map<String, String> dataMap)
	{
		ClassificationField classField = new ClassificationField();
		classField.setDefaultValue(dataMap.get("default"));
		classField.setDescription(dataMap.get("description"));
		classField.setInherited(false);
		classField.setMandatory(StringUtils.isNullString(dataMap.get("mandatory")) ? false : BooleanUtils.getBooleanByYN(dataMap.get("mandatory")));
		classField.setName(dataMap.get("fieldname"));
		classField.setTitle(dataMap.get("title"));
		classField.setFieldSize(dataMap.get("fieldsize"));
		classField.setType(FieldTypeEnum.typeof(dataMap.get("type")));
		classField.setTypeValue(dataMap.get("typevalue"));
		classField.setValidityRegex(dataMap.get("validityregex"));
		classField.setPublicFieldInERP(dataMap.get("publicfieldinerp"));
		return classField;
	}
}