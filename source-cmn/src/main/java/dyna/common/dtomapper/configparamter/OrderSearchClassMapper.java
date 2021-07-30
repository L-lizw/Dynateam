package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.OrderSearchClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/15 21:38
**/

public interface OrderSearchClassMapper
{

	void delete(String bmGuid);

	void insert(OrderSearchClass orderSearchClass);

	List<OrderSearchClass> select(@Param("BMGUID")String bmGuid,@Param("BOINFONAME")String boinfoName);


}
