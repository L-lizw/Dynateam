package dyna.app.service.brs.cpb;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.configparamter.TestHistory;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DrivenHistoryStub extends AbstractServiceStub<CPBImpl>
{

	/**
	 * 保存驱动测试参数输入值
	 * 
	 * @param testHistory
	 * @return
	 */
	protected void saveTestHistory(TestHistory testHistory) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (StringUtils.isNullString(testHistory.getConditionName()))
		{
			testHistory.setConditionName(ConfigParameterConstants.CONFIG_PARAMETER_TEST_CONDITION_NAME);
		}

		sds.save(testHistory);
	}

	/**
	 * 删除驱动测试条件历史
	 * 
	 * @param history
	 * @throws ServiceRequestException
	 */
	protected void deleteTestHistory(TestHistory history) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.delete(history);
	}

	/**
	 * 取得驱动测试参数输入历史值
	 * 
	 * @param foundationGuid
	 * @param userGuid
	 * @param historyName
	 * @return
	 */
	protected List<TestHistory> listTestHistory(TestHistory history) throws ServiceRequestException
	{
		String conditionName = history.getConditionName();
		if (StringUtils.isNullString(conditionName))
		{
			conditionName = ConfigParameterConstants.CONFIG_PARAMETER_TEST_CONDITION_NAME;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(TestHistory.class, history);
	}

	/**
	 * 取得驱动测试参数输入历史值
	 * 
	 * @param history
	 * @return
	 * @throws ServiceRequestException
	 */
	protected TestHistory getTestHistory(TestHistory history) throws ServiceRequestException
	{
		List<TestHistory> list = this.listTestHistory(history);
		if (!SetUtils.isNullList(list))
		{
			return list.get(0);
		}

		return null;
	}
}
