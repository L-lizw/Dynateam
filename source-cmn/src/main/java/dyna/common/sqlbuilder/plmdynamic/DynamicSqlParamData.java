//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dyna.common.sqlbuilder.plmdynamic;

import java.util.List;

public abstract class DynamicSqlParamData {
    private String tableName;
    private List<SqlParamData> whereParamList;

    public DynamicSqlParamData() {
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<SqlParamData> getWhereParamList() {
        return this.whereParamList;
    }

    public void setWhereParamList(List<SqlParamData> whereParamList) {
        this.whereParamList = whereParamList;
    }
}
