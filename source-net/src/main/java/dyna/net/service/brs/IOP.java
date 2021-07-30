package dyna.net.service.brs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.configparamter.DrivenResult;
import dyna.common.bean.data.iopconfigparamter.IOPColumnTitle;
import dyna.common.bean.data.iopconfigparamter.IOPColumnValue;
import dyna.common.bean.data.iopconfigparamter.IOPConfigParameter;
import dyna.common.dto.DataRule;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

public interface IOP extends Service
{
	/**
	 * end1发布时，同时发布配置规则
	 * 所有已发布但是下一个版本数据为Y，且有效的数据失效，所有未发布的数据发布。
	 * 
	 * @param masterGuid
	 *            end1 masterguid
	 * @param foundationId
	 */
	public void release(String masterGuid, String foundationId) throws ServiceRequestException;
	
	/**
	 * get the parameter data
	 * 
	 * @param masterGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<IOPConfigParameter> listIOPConfigParameter(String masterGuid, Date end1ReleaseTime) throws ServiceRequestException;

	/**
	 * 取到此对象的LID号
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listLID(String masterGuid, Date end1ReleaseTime) throws ServiceRequestException;
	
	/**
	 * 差异更新
	 * 
	 * @param end1ObjectGuid
	 * @param end1ReleaseTime
	 * @param listTitles
	 * @param listValues
	 * @throws ServiceRequestException
	 */
	public void syncIOPConfigParameter(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, List<IOPColumnTitle> listTitles, Map<Integer, List<IOPColumnValue>> listValues)
			throws ServiceRequestException;

	/**
	 * 
	 * * 驱动测试
	 * 
	 * @param objectGuid
	 * @param condition
	 * @param end2SearchCondition
	 * @param rule
	 *            数据规则
	 * @param codeValue
	 *            输入代号值
	 * @param isAppend
	 *            是否追加件号值 true:追加;false:不追加
	 * @return
	 * @throws ServiceRequestException
	 */
	public DrivenResult drivenTest(ObjectGuid objectGuid, SearchCondition condition, SearchCondition end2SearchCondition, DataRule rule, String codeValue, boolean isAppend)
			throws ServiceRequestException;
}
