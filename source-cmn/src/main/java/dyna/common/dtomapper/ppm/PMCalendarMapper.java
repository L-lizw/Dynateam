package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface PMCalendarMapper extends DynaCommonMapper<PMCalendar>
{

	List<PMCalendar> selectTaskOfUserInMonth(Map<String,Object> param);

	List<PMCalendar> selectTaskOfUserInDate(Map<String,Object> param);


}
