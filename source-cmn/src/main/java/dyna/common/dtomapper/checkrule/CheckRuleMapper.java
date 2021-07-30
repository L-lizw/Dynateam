package dyna.common.dtomapper.checkrule;

import dyna.common.bean.data.checkrule.CheckRule;
import dyna.common.dtomapper.DynaCommonMapper;

public interface CheckRuleMapper extends DynaCommonMapper<CheckRule>
{
	void deleteAll();
}
