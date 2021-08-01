package dyna.data.common.sqlbuilder;

import dyna.common.Criterion;
import dyna.common.FieldOrignTypeEnum;
import dyna.common.SearchCondition;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.systemenum.*;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.util.DSCommonUtil;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.data.service.model.codemodel.CodeModelService;

import java.util.List;
import java.util.Map;

public class MutiTableCriterionUtil
{

	private static String buildFieldFilter(String className, String mainTableAlias, String classiFicationAlias, Criterion criterion, SearchCondition searchCondition,
			List<String> relationTableList, Map<String, String> relationTableMap, boolean caseSensitive, String sessionId, List<SqlParamData> paramList)
			throws ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();
		CodeModelService codeModelService = DataServer.getCodeModelService();

		StringBuffer whereClauseSb = new StringBuffer();
		ClassObject classObject = classModelService.getClassObject(className);
		String bmName = commonService.getSession(sessionId).getBizModelName();

		String key = criterion.getKey();
		FieldTypeEnum fieldType = null;
		String column = null;
		ClassField field = null;

		// 分类上的字段
		if (FieldOrignTypeEnum.CLASSIFICATION.equals(criterion.getFieldOrignType()))
		{
			String classificationItem = searchCondition.getClassification();
			CodeItem codeItem = codeModelService.getCodeItemByGuid(classificationItem);
			field = codeItem.getField(key);
			fieldType = field.getType();

			column = classiFicationAlias + "." + field.getColumnName();
		}
		else
		{
			field = (classObject == null) ? null : classObject.getField(key);
			if (field == null)
			{
				throw new DynaDataExceptionAll(key, null, DataExceptionEnum.DS_NO_FIELD);
			}

			if (field.isSystem())
			{
				column = mainTableAlias + "." + field.getColumnName();
				fieldType = field.getType();
			}
			else
			{
				fieldType = field.getType();
				column = commonService.getFieldColumn(mainTableAlias, className + "." + field.getName());
				String tableName = commonService.getTableName(className, field.getName());
				if (!DSCommonUtil.isSystemTable(tableName))
				{
					String alias = DSCommonUtil.getTableAlias(mainTableAlias, tableName);
					if (!relationTableMap.containsKey(tableName + " " + alias))
					{
						relationTableList.add(tableName + " " + alias);
						relationTableMap.put(tableName + " " + alias, mainTableAlias + ".GUID=" + alias + ".FOUNDATIONFK(+)");
					}
				}
			}
		}

		Object criterionValue = criterion.getValue();
		if (criterionValue == null || criterionValue.toString().length() == 0 || fieldType == null)
		{
			if (searchCondition.isAllowEmpty())
			{
				whereClauseSb.delete(0, whereClauseSb.length());
				whereClauseSb.append("1=1");
			}
			else
			{
				whereClauseSb.delete(0, whereClauseSb.length());
				whereClauseSb.append("1=2");
			}
			return whereClauseSb.toString();
		}

		// 如果字段类型为code，需要特殊判断（组码和分类都是code类型，需要根据字段名字重新判断字段类型）
		if (fieldType.equals(FieldTypeEnum.CODE))
		{
			fieldType = commonService.getFieldTypeByName(field.getTypeValue());
			fieldType = fieldType == null ? FieldTypeEnum.CODE : fieldType;
		}

		// 如果是状态字段需要特殊处理，需要拼sql
		if (SystemClassFieldEnum.STATUS.getName().equals(field.getName()))
		{
			DataServicUtil.transOperateStatus(whereClauseSb, fieldType, criterion, SystemStatusEnum.getStatusEnum(String.valueOf(criterionValue)).toString(), caseSensitive,
					paramList);

		}
		// 如果是GUID需要特殊处理
		// checkout时的时候，数据方面所做的操作为：增加一条新纪录（信息和老信息相同小版本升1）同时把新纪录的sourceGuid设置为老信息的guid
		// 设计中新纪录对前台不可见，如果需要新的信息是把新信息封装到老的guid上，传回老的guid，
		// 所以在查询中需要把新、老两条信息的guid都传给后台数据查询处理，根据规则是否可以看到checkout之后数据来查询数据
		else if (SystemClassFieldEnum.GUID.getName().equals(field.getName()))
		{
			whereClauseSb.append(column);
			DataServicUtil.transOperateGuid(whereClauseSb, fieldType, criterion, column, String.valueOf(criterionValue), caseSensitive, paramList);
		}
		else if (SystemClassFieldEnum.MASTERFK.getName().equals(field.getName()))
		{
			whereClauseSb.append(column);
			DataServicUtil.transOperateMasterfk(whereClauseSb, criterion, String.valueOf(criterionValue), caseSensitive, paramList);
		}
		else if (fieldType.equals(FieldTypeEnum.CODE))
		{
			DataServicUtil.transOperateCode(whereClauseSb, criterion, criterionValue, column, codeModelService, paramList);
		}
		else if (fieldType.equals(FieldTypeEnum.CODEREF))
		{
			DataServicUtil.transOperateCode(whereClauseSb, criterion, criterionValue, column, codeModelService, paramList);
		}
		else if (fieldType.equals(FieldTypeEnum.MULTICODE))
		{
			DataServicUtil.transOperateMultCode(whereClauseSb, criterion, criterionValue, column, paramList);
		}
		else if (fieldType.equals(FieldTypeEnum.CLASSIFICATION))
		{
			if (searchCondition.getSearchType().equals(AdvancedQueryTypeEnum.CLASSIFICATION))
			{
				String classificationItemGuid = criterionValue.toString();
				boolean isMaster = false;
				if (classObject != null && classObject != null && classObject.getGuid() != null)
				{
					isMaster = DataServer.getClassificationFeatureService().isMasterClassification(classObject.getGuid(), classificationItemGuid);
				}

				DataServicUtil.transOperateClassification(whereClauseSb, criterion.getOperateSignEnum(), classificationItemGuid, column, isMaster, paramList);
			}
			else
			{
				DataServicUtil.transOperateClassification(whereClauseSb, criterion, criterionValue, column, paramList);
			}
		}
		else if (fieldType.equals(FieldTypeEnum.OBJECT))
		{
			if (StringUtils.isNullString(field.getTypeValue()))
			{
				String valueScope = field.getValueScope();
				if (!StringUtils.isNullString(valueScope))
				{
					String[] interfaces = valueScope.split(";");
					for (String interface_ : interfaces)
					{
						if (interface_.equals(ModelInterfaceEnum.IUser.name()) || interface_.equals(ModelInterfaceEnum.IPMRole.name())
								|| interface_.equals(ModelInterfaceEnum.IPMCalendar.name()))
						{
							DataServicUtil.transOperateUser(whereClauseSb, criterion, criterionValue, column, paramList);
						}
						else if (interface_.equals(ModelInterfaceEnum.IGroup.name()))
						{
							DataServicUtil.transOperateGroup(whereClauseSb, criterion, criterionValue, column, paramList);
						}
						else
						{
							DataServicUtil.transOperateObject(whereClauseSb, criterion, criterionValue, column, paramList);
						}
					}
				}
			}
			else
			{
				ObjectFieldTypeEnum objectFieldTypeOfField = commonService.getObjectFieldTypeOfField(field, bmName);
				if (objectFieldTypeOfField == ObjectFieldTypeEnum.GROUP)
				{
					DataServicUtil.transOperateGroup(whereClauseSb, criterion, criterionValue, column, paramList);
				}
				else if (objectFieldTypeOfField == ObjectFieldTypeEnum.USER)
				{
					DataServicUtil.transOperateUser(whereClauseSb, criterion, criterionValue, column, paramList);
				}
				else
				{
					DataServicUtil.transOperateObject(whereClauseSb, criterion, criterionValue, column, paramList);
				}
			}
		}
		else if (fieldType.equals(FieldTypeEnum.DATETIME) || fieldType.equals(FieldTypeEnum.BOOLEAN) || fieldType.equals(FieldTypeEnum.DATE)
				|| fieldType.equals(FieldTypeEnum.STRING))
		{
			DataServicUtil.transOperate(whereClauseSb, fieldType, criterion, criterionValue, caseSensitive, column, paramList);
		}
		else
		{
			DataServicUtil.transOperate(whereClauseSb, fieldType, criterion, criterionValue, caseSensitive, column, paramList);
		}

		return whereClauseSb.toString();
	}

	/**
	 * 
	 * @param relationTableList
	 * @param relationTableMap
	 * @param searchCondition
	 * @param queryTag
	 * @param caseSensitive
	 * @param sessionId
	 * @param paramList
	 * @param paramList
	 * @return
	 * @throws DynaDataException
	 */
	public static String getSqlWhere(String className, String mainTableAlias, String classiFicationAlias, List<String> relationTableList, Map<String, String> relationTableMap,
			SearchCondition searchCondition, String sessionId, List<SqlParamData> paramList) throws ServiceRequestException
	{
		StringBuilder whereClauseSb = new StringBuilder();
		boolean isFirstCriterion = true;
		String preKey = null;

		whereClauseSb.append("(");
		for (int i = 0, j = (searchCondition.getCriterionList() == null) ? 0 : (searchCondition.getCriterionList().size()); i < j; i++)
		{
			Criterion criterion = searchCondition.getCriterionList().get(i);
			String key = criterion.getKey();

			if (key.equals(Criterion.GROUP_START))
			{
				Criterion criterionNext = searchCondition.getCriterionList().get(i + 1);
				if (criterionNext.getKey().equalsIgnoreCase(Criterion.GROUP_END))
				{
					continue;
				}

				if (!isFirstCriterion && (!preKey.equals(Criterion.GROUP_START) && !preKey.equals(Criterion.CON_AND) && !preKey.equals(Criterion.CON_OR)))
				{
					whereClauseSb.append(" ").append(criterion.getConjunction()).append(" ");
				}

				whereClauseSb.append("(");
				isFirstCriterion = false;
				preKey = key;
				continue;
			}
			else if (key.equals(Criterion.GROUP_END))
			{
				if (i == 0)
				{
					continue;
				}
				Criterion criterionPre = searchCondition.getCriterionList().get(i - 1);
				if (criterionPre.getKey().equalsIgnoreCase(Criterion.GROUP_START))
				{
					continue;
				}
				whereClauseSb.append(")");
				isFirstCriterion = false;
				preKey = key;
				continue;
			}

			if (whereClauseSb.length() > 1 && whereClauseSb.charAt(whereClauseSb.length() - 1) != '(')
			{
				whereClauseSb.append(" " + criterion.getConjunction() + " ");
			}

			boolean caseSensitive = searchCondition.caseSensitive();
			whereClauseSb.append(buildFieldFilter(className, mainTableAlias, classiFicationAlias, criterion, searchCondition, relationTableList, relationTableMap, caseSensitive,
					sessionId, paramList));

			isFirstCriterion = false;
			preKey = key;
		}
		preKey = null;

		if (whereClauseSb.length() == 1 && whereClauseSb.charAt(0) == '(')
		{
			// 查询私有文件夹下的数据会一个条件也没有
			// 高级搜索肯定会有一个条件传过来
			whereClauseSb.append("1=1 ");
		}
		whereClauseSb.append(")");

		return whereClauseSb.toString();
	}

}
