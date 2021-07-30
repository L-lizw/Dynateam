package dyna.data.service.ec;

import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceRequestException;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

public class ECServiceImpl extends DataRuleService implements ECService
{
	private DSECStub ecStub;

	public ECServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	protected DSECStub getECStub()
	{
		if (this.ecStub == null)
		{
			this.ecStub = new DSECStub(this.serviceContext, this);
		}
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
