package dyna.data.service.model;

import dyna.common.exception.ServiceRequestException;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.DataRuleService;

public abstract class DataCacheServiceStub<T extends DataRuleService> extends DSAbstractServiceStub<T>
{
	/**
	 * 重新加载缓存
	 * 
	 * @throws ServiceRequestException
	 */
	public abstract void loadModel() throws ServiceRequestException;
}
