package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.MessageRule;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface MessageRuleMapper extends DynaCommonMapper<MessageRule>
{

	List<MessageRule> selectMessageNotifier(Map<String,Object> param);

	void insertMessageNotifier(Map<String,Object> param);

	void deleteMessageNotifier(String messageRuleGuid);
}
