package dyna.data.service.config;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

public class ConfigManagerServiceImpl extends DataRuleService implements ConfigManagerService
{
	private ConfigManagerStub configManagerStub;

	public ConfigManagerServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public ConfigManagerStub getConfigManagerStub()
	{
		if (this.configManagerStub == null)
		{
			this.configManagerStub = new ConfigManagerStub(this.serviceContext, this);
		}
		return this.configManagerStub;
	}

	/**
	 * 当end1发布时，同时发布其配置数据
	 * 所有已发布但是下一个版本数据为Y，且有效的数据失效，所有未发布的数据发布。
	 *
	 * @param masterGuid
	 * @param foundationId
	 * @param interfaceEnum
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	@Override
	public void releaseConfigTable(String masterGuid, String foundationId, ModelInterfaceEnum interfaceEnum, String sessionId) throws ServiceRequestException
	{
		this.getConfigManagerStub().releaseConfigTable(masterGuid, foundationId, interfaceEnum, sessionId);
	}

	/**
	 * 当end1删除时，同时删除其配置数据
	 * 所有已发布但是下一个版本数据为Y，且有效的数据失效，所有未发布的数据发布。
	 *
	 * @param masterGuid
	 * @param isMaster
	 * @param interfaceEnum
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	@Override
	public void deleteConfigTable(String masterGuid, boolean isMaster, ModelInterfaceEnum interfaceEnum, String sessionId) throws ServiceRequestException
	{
		this.getConfigManagerStub().deleteConfigTableData(masterGuid, isMaster, interfaceEnum, sessionId);
	}

	@Override
	public void changeOwnerContractOfContent(ObjectGuid contractObjectGuid, String templateGuid, String sessionId) throws ServiceRequestException
	{
		this.getConfigManagerStub().changeOwnerContractOfContent(contractObjectGuid, templateGuid, sessionId);
	}

	@Override
	public void clearItemOfContent(ObjectGuid contractObjectGuid, ObjectGuid itemObjectGuid, String templateGuid, String sessionId) throws ServiceRequestException
	{
		this.getConfigManagerStub().clearItemOfContent(contractObjectGuid, itemObjectGuid, templateGuid, sessionId);
	}
}
