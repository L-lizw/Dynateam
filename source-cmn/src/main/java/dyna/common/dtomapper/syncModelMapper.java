package dyna.common.dtomapper;

import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/18
 **/
public interface syncModelMapper
{

	void createTable(Map<String,Object> param);

	int addColToTable(Map<String,Object> param);

	int addColToTableForMS(Map<String,Object> param);

	int createPrimaryKey(Map<String,Object> param);

	int createIndex(Map<String,Object> param);

	int createUniqueConstraint(Map<String,Object> param);

	void dropColumn(Map<String,Object> param);

	void dropTable(String sql);

	void dropConstraint1(Map<String,Object> param);

	void dropIndex1(Map<String,Object> param);

	void dropConstraint2(Map<String,Object> param);

	void dropIndex2(Map<String,Object> param);

	void executeddl(String sql);

}
