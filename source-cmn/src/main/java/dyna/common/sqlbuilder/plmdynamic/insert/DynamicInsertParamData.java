package dyna.common.sqlbuilder.plmdynamic.insert;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;

import java.util.List;

public class DynamicInsertParamData extends DynamicSqlParamData
{
	private List<SqlParamData> insertParamList;

	public List<SqlParamData> getInsertParamList()
	{
		return this.insertParamList;
	}

	public void setInsertParamList(List<SqlParamData> insertParamList)
	{
		this.insertParamList = insertParamList;
	}
}