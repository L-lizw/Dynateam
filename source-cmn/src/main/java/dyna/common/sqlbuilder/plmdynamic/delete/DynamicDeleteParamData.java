package dyna.common.sqlbuilder.plmdynamic.delete;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;

public class DynamicDeleteParamData extends DynamicSqlParamData
{
	private String whereSql;

	public String getWhereSql()
	{
		return this.whereSql;
	}

	public void setWhereSql(String whereSql)
	{
		this.whereSql = whereSql;
	}
}