package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface IndicatorAnalysisValMapper extends DynaCommonMapper<IndicatorAnalysisVal>
{

	List<IndicatorAnalysisVal> selectForTest();

	void deleteBy(Map<String,Object> param);

}
