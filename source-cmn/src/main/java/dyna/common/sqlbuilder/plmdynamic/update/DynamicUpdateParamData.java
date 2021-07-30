package dyna.common.sqlbuilder.plmdynamic.update;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;

import java.util.List;

public class DynamicUpdateParamData extends DynamicSqlParamData
{
	private List<SqlParamData> updateParamList;

	public List<SqlParamData> getUpdateParamList()
	{
		return this.updateParamList;
	}

	public void setUpdateParamList(List<SqlParamData> updateParamList)
	{
		this.updateParamList = updateParamList;
	}
}