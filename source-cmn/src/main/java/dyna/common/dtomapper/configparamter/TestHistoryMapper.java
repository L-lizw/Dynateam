package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.TestHistory;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/16
 **/
public interface TestHistoryMapper
{

	List<TestHistory> select(Map<String,Object> param);

	void insert(Map<String,Object> param);

	void delete(String guid);

}
