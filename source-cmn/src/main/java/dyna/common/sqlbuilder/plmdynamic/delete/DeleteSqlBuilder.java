package dyna.common.sqlbuilder.plmdynamic.delete;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlBuilder;

public class DeleteSqlBuilder extends SqlBuilder
{
	public String getSql(DynamicSqlParamData paramData)
	{
		DynamicDeleteParamData deleteData = (DynamicDeleteParamData)paramData;
		String where = " WHERE " + deleteData.getWhereSql();
		return "delete from " + paramData.getTableName() + where;
	}
}