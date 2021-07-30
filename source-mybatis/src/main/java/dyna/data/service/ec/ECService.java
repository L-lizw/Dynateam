package dyna.data.service.ec;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

import java.util.List;

public interface ECService extends Service
{
	/**
	 * 批量解除ECO锁定
	 * 
	 * @param ecoObjectGuid
	 * @param classNameList
	 * @throws ServiceRequestException
	 */
	void unlockByECO(ObjectGuid ecoObjectGuid, List<String> classNameList) throws ServiceRequestException;
}
