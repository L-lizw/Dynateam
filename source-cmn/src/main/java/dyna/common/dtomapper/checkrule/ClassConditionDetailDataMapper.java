package dyna.common.dtomapper.checkrule;

import dyna.common.bean.data.checkrule.ClassConditionDetailData;
import dyna.common.dtomapper.DynaCommonMapper;

public interface ClassConditionDetailDataMapper extends DynaCommonMapper<ClassConditionDetailData>
{

	void deleteAll();

	void deleteByMaster(String guid);
}
