package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.indicator.IndicatorDataMonthlyList;
import dyna.common.dtomapper.DynaCommonMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface IndicatorDataMonthlyListMapper extends DynaCommonMapper<IndicatorDataMonthlyList>
{

	void deleteBy(@Param("INDICATORYEAR")String indicatoryyear,@Param("INDICATORYEAR")String indicatoryear);
}
