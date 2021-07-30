//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dyna.common.sqlbuilder.plmdynamic.select;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;

import java.util.List;
import java.util.Map;

public class DynamicSelectParamData extends DynamicSqlParamData
{
    private String fieldSql;
    private String whereSql;
    private String orderSql;
    private List<String> joinTableList;
    private Map<String, String> joinTableMap;
    private int currentPage;
    private int rowsPerPage;

    public DynamicSelectParamData() {
    }

    public String getFieldSql() {
        return this.fieldSql;
    }

    public void setFieldSql(String fieldSql) {
        this.fieldSql = fieldSql;
    }

    public String getWhereSql() {
        return this.whereSql;
    }

    public void setWhereSql(String whereSql) {
        this.whereSql = whereSql;
    }

    public String getOrderSql() {
        return this.orderSql;
    }

    public void setOrderSql(String orderSql) {
        this.orderSql = orderSql;
    }

    public Map<String, String> getJoinTableMap() {
        return this.joinTableMap;
    }

    public void setJoinTableMap(Map<String, String> joinTableMap) {
        this.joinTableMap = joinTableMap;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getRowsPerPage() {
        return this.rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public List<String> getJoinTableList() {
        return this.joinTableList;
    }

    public void setJoinTableList(List<String> joinTableList) {
        this.joinTableList = joinTableList;
    }
}
