package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface PPMFoundationObjectUtilMapper
{

	List<PPMFoundationObjectUtil>selectTaskOfUserInMonth(Map<String,Object> param);

	List<PPMFoundationObjectUtil> selectTaskOfUserInDate(Map<String,Object> param);

}
