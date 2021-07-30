//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dyna.common.sqlbuilder.plmdynamic.insert;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlBuilder;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;

import java.util.List;

public class InsertSqlBuilder extends SqlBuilder
{
    public InsertSqlBuilder() {
    }

    public String getSql(DynamicSqlParamData paramData) {
        DynamicInsertParamData insertData = (DynamicInsertParamData)paramData;
        List<SqlParamData> insertParamList = insertData.getInsertParamList();
        StringBuffer fieldBuffer = new StringBuffer();
        StringBuffer valueBuffer = new StringBuffer();

        for(int i = 0; i < insertParamList.size(); ++i) {
            SqlParamData param = (SqlParamData)insertParamList.get(i);
            if (i > 0) {
                fieldBuffer.append(",");
                valueBuffer.append(",");
            }

            if ("guid".equalsIgnoreCase(param.getParamName())) {
                fieldBuffer.append("GUID");
                valueBuffer.append("'").append(param.getVal()).append("'");
            } else {
                fieldBuffer.append(param.getParamName());
                valueBuffer.append("?");
            }
        }

        return "insert into " + paramData.getTableName() + "(" + fieldBuffer.toString() + ") values (" + valueBuffer.toString() + ")";
    }
}
