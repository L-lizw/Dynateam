package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.PMCalendarSpecialDate;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface PMCalendarSpecialDateMapper extends DynaCommonMapper<PMCalendarSpecialDate>
{

	void deleteSpecialDateByCal(String calendarGuid);
}
