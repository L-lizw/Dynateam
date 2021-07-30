package dyna.data.service.model;

import dyna.common.exception.ServiceRequestException;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.DataRuleService;

public abstract class DataCacheServiceStub<T extends DataRuleService> extends DSAbstractServiceStub<T>
{
	protected DataCacheServiceStub(DataServerContext context, T service)
	{
		super(context, service);
	}

	/**
	 * 重新加载缓存
	 * 
	 * @throws ServiceRequestException
	 */
	public abstract void loadModel() throws ServiceRequestException;
}
