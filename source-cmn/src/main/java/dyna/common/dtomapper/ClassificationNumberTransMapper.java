package dyna.common.dtomapper;

import dyna.common.bean.data.coding.ClassificationNumberTrans;

import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/18
 **/
public interface ClassificationNumberTransMapper extends DynaCommonMapper<ClassificationNumberTrans>
{

	int updateModelSerial(Map<String,Object> param);

}
