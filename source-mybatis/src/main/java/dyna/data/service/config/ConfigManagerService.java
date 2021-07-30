package dyna.data.service.config;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.net.service.Service;

public interface ConfigManagerService extends Service
{
	/**
	 * 当end1发布时，同时发布其配置数据
	 * 所有已发布但是下一个版本数据为Y，且有效的数据失效，所有未发布的数据发布。
	 *
	 * @param masterGuid
	 * @param masterGuid
	 * @throws ServiceRequestException
	 */
	void releaseConfigTable(String masterGuid, String foundationId, ModelInterfaceEnum interfaceEnum, String sessionId) throws ServiceRequestException;

	/**
	 * 当end1删除时，同时删除其配置数据
	 * 所有已发布但是下一个版本数据为Y，且有效的数据失效，所有未发布的数据发布。
	 *
	 * @param masterGuid
	 * @param masterGuid
	 * @throws ServiceRequestException
	 */
	void deleteConfigTable(String masterGuid, boolean isMaster, ModelInterfaceEnum interfaceEnum, String sessionId) throws ServiceRequestException;

	/**
	 * 修改指定订单明细的所属合同
	 * 
	 * @param contractObjectGuid
	 * @param template
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	void changeOwnerContractOfContent(ObjectGuid contractObjectGuid, String relationTemplateGuid, String sessionId) throws ServiceRequestException;

	/**
	 * 根据订单合同清空订单明细上的生成物料标记
	 *
	 * @param contractObjectGuid
	 *            订单合同
	 * @param itemObjectGuid
	 *            生成物料
	 * @param template
	 *            订单合同和订单明细关联模板
	 * @param sessionId
	 * @throws DynaDataException
	 */
	void clearItemOfContent(ObjectGuid contractObjectGuid, ObjectGuid itemObjectGuid, String relationTemplateGuid, String sessionId)
			throws DynaDataException, ServiceRequestException;
}
