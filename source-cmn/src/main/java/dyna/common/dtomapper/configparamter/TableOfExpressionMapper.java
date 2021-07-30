package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.TableOfExpression;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/15
 **/
public interface TableOfExpressionMapper
{

	List<TableOfExpression> select(Map<String,Object> param);

	List<TableOfExpression> haveRLSData(String masterGuid);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	void deleteLine(String guid);

	void deleteByMaster(String masterGuid);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);
}
