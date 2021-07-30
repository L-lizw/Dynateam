package dyna.common.sqlbuilder.plmdynamic.update;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlBuilder;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.util.SetUtils;

import java.util.List;

public class UpdateSqlBuilder extends SqlBuilder
{
	public String getSql(DynamicSqlParamData paramData)
	{
		DynamicUpdateParamData updateData = (DynamicUpdateParamData)paramData;

		List whereParamList = updateData.getWhereParamList();

		List updateParamList = updateData.getUpdateParamList();

		StringBuffer buffer = new StringBuffer();
		buffer.append("update ");
		buffer.append(updateData.getTableName()).append(" ");
		buffer.append("set ");
		for (int i = 0; i < updateParamList.size(); i++)
		{
			if (i > 0)
			{
				buffer.append(",");
			}

			SqlParamData param = (SqlParamData)updateParamList.get(i);
			buffer.append(param.getParamName()).append("=?");
		}

		if (!SetUtils.isNullList(whereParamList))
		{
			StringBuffer whereBuffer = new StringBuffer(" where ");
			for (int i = 0; i < whereParamList.size(); i++)
			{
				if (i > 0)
				{
					whereBuffer.append(" and ");
				}

				SqlParamData param = (SqlParamData)whereParamList.get(i);
				whereBuffer.append(param.getParamName()).append("=?");
			}
			buffer.append(whereBuffer);
		}

		return buffer.toString();
	}
}