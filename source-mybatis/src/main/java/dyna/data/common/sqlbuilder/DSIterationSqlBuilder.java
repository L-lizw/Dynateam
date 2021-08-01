package dyna.data.common.sqlbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dyna.data.service.common.DSCommonService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.Session;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ObjectFieldTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.util.DSCommonUtil;

public class DSIterationSqlBuilder
{
	public static List<ClassField> listRollBackField(ClassObject classObject)
	{
		List<ClassField> list = new ArrayList<>(classObject.getFieldList());
		for (int i = list.size() - 1; i > -1; i--)
		{
			if (!list.get(i).isRollback())
			{
				list.remove(i);
			}
		}
		return list;
	}

	public static List<ClassField> listRollBackFieldForUpdate(ClassObject classObject)
	{
		List<ClassField> list = new ArrayList<>(classObject.getFieldList());
		for (int i = list.size() - 1; i > -1; i--)
		{
			if (SystemClassFieldEnum.GUID.getName().equals(list.get(i).getName()) || SystemClassFieldEnum.MASTERFK.getName().equals(list.get(i).getName())
					|| SystemClassFieldEnum.CLASSGUID.getName().equals(list.get(i).getName()) || SystemClassFieldEnum.BOGUID.getName().equals(list.get(i).getName())
					|| SystemClassFieldEnum.ECFLAG.getName().equals(list.get(i).getName()))
			{
				list.remove(i);
			}
			else if (!list.get(i).isRollback())
			{
				list.remove(i);
			}
		}
		return list;
	}

	public static String getSelectSqlForInteration(String alias, String className, Session session) throws ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		if (StringUtils.isNull(alias))
		{
			alias = "";
		}
		else
		{
			alias = alias + ".";
		}
		ClassObject classObject = classModelService.getClassObject(className);
		List<ClassField> fieldList = listRollBackField(classObject);
		StringBuilder selectColumnSb = new StringBuilder();
		for (ClassField field : fieldList)
		{
			if (field == null)
			{
				continue;
			}
			String fieldName = field.getName();
			String columnName = field.getColumnName();
			if (field.getType().equals(FieldTypeEnum.OBJECT))
			{
				ObjectFieldTypeEnum fieldType = commonService.getObjectFieldTypeOfField(className, fieldName, session.getBizModelName());
				if (fieldType != null)
				{
					String tmpFieldName = fieldName;
					if (fieldName.endsWith("$"))
					{
						tmpFieldName = fieldName.substring(0, fieldName.length() - 1);
					}
					if (fieldType == ObjectFieldTypeEnum.OBJECT)
					{
						selectColumnSb.append(alias).append(columnName).append(" as ").append(fieldName).append(",");
						selectColumnSb.append(alias).append(columnName).append("$class as ").append(tmpFieldName).append("$class,");
						selectColumnSb.append(alias).append(columnName).append("$master as ").append(tmpFieldName).append("$master,");
					}
					else
					{
						selectColumnSb.append(alias).append(columnName).append(" as ").append(fieldName).append(",");
					}
				}
			}
			else if (field.isSystem())
			{
				selectColumnSb.append(alias).append(columnName).append(" AS ").append(fieldName).append(",");
			}
			else if (field.isBuiltin())
			{
				selectColumnSb.append(alias).append(columnName).append(",");
			}
			else
			{
				selectColumnSb.append(alias).append(columnName).append(" AS ").append(fieldName).append(",");
			}
		}

		if (selectColumnSb.toString().equals("") || selectColumnSb.lastIndexOf(",") == -1)
		{
			return "";
		}
		return selectColumnSb.toString().substring(0, selectColumnSb.lastIndexOf(","));
	}

	public static String getInsertSqlForInteration(String className, Session session) throws ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		ClassObject classObject = classModelService.getClassObject(className);
		List<ClassField> fieldList = listRollBackField(classObject);
		StringBuilder selectColumnSb = new StringBuilder();
		for (ClassField field : fieldList)
		{
			if (field == null)
			{
				continue;
			}
			String fieldName = field.getName();
			if (SystemClassFieldEnum.GUID.getName().equalsIgnoreCase(fieldName))
			{
				continue;
			}
			String columnName = field.getColumnName();
			if (field.getType().equals(FieldTypeEnum.OBJECT))
			{
				ObjectFieldTypeEnum fieldType = commonService.getObjectFieldTypeOfField(className, fieldName, session.getBizModelName());
				if (fieldType != null)
				{
					if (fieldType == ObjectFieldTypeEnum.OBJECT)
					{
						selectColumnSb.append(columnName).append(",");
						selectColumnSb.append(columnName).append("$class,");
						selectColumnSb.append(columnName).append("$master,");
					}
					else
					{
						selectColumnSb.append(columnName).append(",");
					}
				}
			}
			else
			{
				selectColumnSb.append(columnName).append(",");
			}
		}

		if (selectColumnSb.toString().equals("") || selectColumnSb.lastIndexOf(",") == -1)
		{
			return "";
		}
		return selectColumnSb.toString().substring(0, selectColumnSb.lastIndexOf(","));
	}

	/**
	 * 取得用于插入备份表的column列表
	 * 
	 * @param relationTableMap
	 * @param className
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public static String getSelectSqlForFoundation(String alias, Map<String, String> relationTableMap, String className, Session session) throws ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		ClassObject classObject = classModelService.getClassObject(className);
		List<ClassField> fieldList = listRollBackField(classObject);
		StringBuilder selectColumnSb = new StringBuilder();
		for (ClassField field : fieldList)
		{
			if (field == null)
			{
				continue;
			}
			String fieldName = field.getName();
			if (SystemClassFieldEnum.GUID.getName().equalsIgnoreCase(fieldName))
			{
				continue;
			}

			String columnName = field.getColumnName();
			String tableName = commonService.getTableName(className, field.getName());
			String tableAlias;
			if (!tableName.endsWith("_0"))
			{
				tableAlias = DSCommonUtil.getTableAlias(alias, tableName);
				relationTableMap.put(tableName + " " + tableAlias, alias + ".guid=" + tableAlias + ".foundationfk(+)");
			}
			else
			{
				tableAlias = alias;
			}
			if (field.getType().equals(FieldTypeEnum.OBJECT))
			{
				ObjectFieldTypeEnum fieldType = commonService.getObjectFieldTypeOfField(className, fieldName, session.getBizModelName());
				if (fieldType != null)
				{
					String tmpFieldName = fieldName;
					if (fieldName.endsWith("$"))
					{
						tmpFieldName = fieldName.substring(0, fieldName.length() - 1);
					}
					if (fieldType == ObjectFieldTypeEnum.OBJECT)
					{
						selectColumnSb.append(tableAlias).append(".").append(columnName).append(" as ").append(fieldName).append(",");
						selectColumnSb.append(tableAlias).append(".").append(columnName).append("$class as ").append(tmpFieldName).append("$class,");
						selectColumnSb.append(tableAlias).append(".").append(columnName).append("$master as ").append(tmpFieldName).append("$master,");
					}
					else
					{
						selectColumnSb.append(tableAlias).append(".").append(columnName).append(" as ").append(fieldName).append(",");
					}
				}
			}
			else if (field.isSystem())
			{
				selectColumnSb.append(tableAlias).append(".").append(columnName).append(" AS ").append(fieldName).append(",");
			}
			else if (field.isBuiltin())
			{
				selectColumnSb.append(tableAlias).append(".").append(columnName).append(",");
			}
			else
			{
				selectColumnSb.append(tableAlias).append(".").append(columnName).append(" AS ").append(fieldName).append(",");
			}
		}

		if (selectColumnSb.toString().equals("") || selectColumnSb.lastIndexOf(",") == -1)
		{
			return "";
		}
		return selectColumnSb.toString().substring(0, selectColumnSb.lastIndexOf(","));
	}

}
