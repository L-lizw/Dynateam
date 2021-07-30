package dyna.data.service.sync;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.TableIndexTypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.service.sync.bean.ColumnModel;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.data.service.sync.bean.TableIndexModel;
import dyna.data.service.sync.bean.TableIndexObject;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 进行分类表结构的部署
 *
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class SyncClassificationTableStructure
{
	private final ModelXMLCache dynaModel;

	private final SqlSessionFactory sqlSessionFactory;

	public SyncClassificationTableStructure(ModelXMLCache dynaModel, SqlSessionFactory sqlSessionFactory)
	{
		this.dynaModel = dynaModel;
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void syncClassificationTable() throws Exception
	{
		DynaLogger.info("Start to synchronize classification table...");
		for (String codeName : this.dynaModel.getCodeObjectMap().keySet())
		{
			CodeObject codeObject = this.dynaModel.getCodeObjectMap().get(codeName);
			if (codeObject.isClassification())
			{
				String tableName = "CF_" + codeObject.getBaseTableName().toUpperCase();
				SyncUtil.syncClassificatioRevisionTable(codeObject.getBaseTableName(), this.buildColumnModelList(codeName, false), this.buildIndexModel(tableName, false));
				SyncUtil.syncClassificationIterationTable(codeObject.getBaseTableName(), this.buildColumnModelList(codeName, true), this.buildIndexModel(tableName, true));
			}
		}
		DynaLogger.info("Success to synchronize classification table...");
	}

	private List<ColumnModel> buildColumnModelList(String codeName, boolean isIteration)
	{
		List<ColumnModel> columnModelList = new ArrayList<>();
		ColumnModel columnModel = new ColumnModel();
		columnModel.setName(SystemClassFieldEnum.GUID.name());
		columnModel.setType(FieldTypeEnum.STRING);
		columnModel.setLength("32");
		columnModelList.add(columnModel);

		columnModel = new ColumnModel();
		columnModel.setName("FOUNDATIONFK");
		columnModel.setType(FieldTypeEnum.STRING);
		columnModel.setLength("32");
		columnModelList.add(columnModel);

		columnModel = new ColumnModel();
		columnModel.setName("CLASSIFICATIONITEMGUID");
		columnModel.setType(FieldTypeEnum.STRING);
		columnModel.setLength("32");
		columnModelList.add(columnModel);

		if (isIteration)
		{
			columnModel = new ColumnModel();
			columnModel.setName(SystemClassFieldEnum.ITERATIONID.name());
			columnModel.setType(FieldTypeEnum.INTEGER);
			columnModelList.add(columnModel);
		}

		List<String> columnList = new ArrayList<>();
		List<ClassificationField> fieldList = this.listAllFields(codeName);
		for (ClassificationField field : fieldList)
		{
			if (columnList.contains(field.getColumnName()))
			{
				continue;
			}
			columnList.add(field.getColumnName());

			columnModel = new ColumnModel();
			columnModel.setName(field.getColumnName());
			columnModel.setType(field.getType());
			columnModel.setLength(field.getFieldSize());
			columnModelList.add(columnModel);
			if (field.getType() == FieldTypeEnum.OBJECT)
			{
				String typeValue = field.getTypeValue();
				if (typeValue != null)
				{
					ClassObject classObject = this.dynaModel.getClassObject(typeValue);
					if (classObject != null && !classObject.hasInterface(ModelInterfaceEnum.IGroup) && !classObject.hasInterface(ModelInterfaceEnum.IUser)
							&& !classObject.hasInterface(ModelInterfaceEnum.IPMRole) && !classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
					{
						columnModel = new ColumnModel();
						columnModel.setName(field.getColumnName() + "$CLASS");
						columnModel.setType(field.getType());
						columnModel.setLength("32");
						columnModelList.add(columnModel);

						columnModel = new ColumnModel();
						columnModel.setName(field.getColumnName() + "$MASTER");
						columnModel.setType(field.getType());
						columnModel.setLength("32");
						columnModelList.add(columnModel);
					}
				}
			}
		}

		return columnModelList;
	}

	private List<ClassificationField> listAllFields(String codeName)
	{
		List<ClassificationField> allFieldList = new ArrayList<>();
		CodeObject codeObject = this.dynaModel.getCodeObjectMap().get(codeName);
		List<CodeItem> codeItemList = codeObject.getCodeDetailList();
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItem codeItem : codeItemList)
			{
				this.listAllFields(codeItem, allFieldList);
			}
		}
		return allFieldList;
	}

	private void listAllFields(CodeItem codeItem, List<ClassificationField> allFieldList)
	{
		List<ClassificationField> fieldList = codeItem.getClassificationFieldList();
		if (!SetUtils.isNullList(fieldList))
		{
			allFieldList.addAll(fieldList);
		}

		List<CodeItem> codeItemList = codeItem.getCodeDetailList();
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItem codeItem_ : codeItemList)
			{
				this.listAllFields(codeItem_, allFieldList);
			}
		}
	}

	private List<TableIndexModel> buildIndexModel(String tableName, boolean isIteration)
	{
		// FOUNDATIONFK+CLASSIFICATIONITEMGUID唯一索引
		// FOUNDATIONFK+CLASSIFICATIONITEMGUID+ITERATIONID唯一索引
		List<TableIndexModel> indexModelList = new ArrayList<>();
		TableIndexModel indexModel = new TableIndexModel();
		indexModel.setType(TableIndexTypeEnum.INDEX);
		indexModel.setUnique(true);
		indexModelList.add(indexModel);

		List<TableIndexObject> indexObjectList = new ArrayList<>();
		TableIndexObject indexObject = new TableIndexObject();
		indexObject.setColumnName("FOUNDATIONFK");
		indexObject.setSequence("1");
		indexObjectList.add(indexObject);

		indexObject = new TableIndexObject();
		indexObject.setColumnName("CLASSIFICATIONITEMGUID");
		indexObject.setSequence("2");
		indexObjectList.add(indexObject);

		if (isIteration)
		{
			indexObject = new TableIndexObject();
			indexObject.setColumnName("ITERATIONID");
			indexObject.setSequence("3");
			indexObjectList.add(indexObject);
		}
		indexModel.setTableIndexObjectList(indexObjectList);

		// FOUNDATIONFK索引
		indexModel = new TableIndexModel();
		indexModel.setType(TableIndexTypeEnum.INDEX);
		indexModel.setUnique(false);
		indexModelList.add(indexModel);

		indexObjectList = new ArrayList<>();
		indexObject = new TableIndexObject();
		indexObject.setColumnName("FOUNDATIONFK");
		indexObject.setSequence("1");
		indexObjectList.add(indexObject);
		indexModel.setTableIndexObjectList(indexObjectList);

		// CLASSIFICATIONITEMGUID索引
		indexModel = new TableIndexModel();
		indexModel.setType(TableIndexTypeEnum.INDEX);
		indexModel.setUnique(false);
		indexModelList.add(indexModel);

		indexObjectList = new ArrayList<>();
		indexObject = new TableIndexObject();
		indexObject.setColumnName("CLASSIFICATIONITEMGUID");
		indexObject.setSequence("1");
		indexObjectList.add(indexObject);
		indexModel.setTableIndexObjectList(indexObjectList);

		return indexModelList;
	}
}
