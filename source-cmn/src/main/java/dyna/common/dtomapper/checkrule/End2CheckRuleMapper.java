package dyna.common.dtomapper.checkrule;

import dyna.common.bean.data.checkrule.End2CheckRule;
import dyna.common.dtomapper.DynaCommonMapper;

public interface End2CheckRuleMapper extends DynaCommonMapper<End2CheckRule>
{
	void deleteAll();

	void deleteByMaster(String guid);
}
