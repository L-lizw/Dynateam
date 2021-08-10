package dyna.data.service.ec;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.DataRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ECServiceImpl extends DataRuleService implements ECService
{
	@Autowired
	private DSECStub ecStub;

	protected DSECStub getECStub()
	{
		return this.ecStub;
	}

	/**
	 * 批量解除ECO锁定
	 *
	 * @param ecoObjectGuid
	 * @param classNameList
	 * @throws ServiceRequestException
	 */
	@Override
	public void unlockByECO(ObjectGuid ecoObjectGuid, List<String> classNameList) throws ServiceRequestException
	{
		this.getECStub().unlockByECO(ecoObjectGuid, classNameList);
	}
}
