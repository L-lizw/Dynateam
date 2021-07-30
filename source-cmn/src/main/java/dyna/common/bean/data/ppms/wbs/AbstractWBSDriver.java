package dyna.common.bean.data.ppms.wbs;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainBean;
import dyna.common.bean.data.ppms.wbs.handler.CalculateHandler;
import dyna.common.bean.data.ppms.wbs.handler.CheckHandler;
import dyna.common.bean.data.ppms.wbs.handler.QueryHandler;
import dyna.common.bean.data.ppms.wbs.handler.UpdateHandler;

public abstract class AbstractWBSDriver extends InstanceDomainBean implements WBSDriver
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public WBSPrepareContain	prepareContain		= null;

	private String				operatorGuid		= null;

	public QueryHandler			queryHandler		= null;
	public CalculateHandler		calculateHandler	= null;
	public UpdateHandler		updateHandler		= null;
	public CheckHandler			checkHandler		= null;

	public AbstractWBSDriver(FoundationObject foundation, String operatorGuid)
	{
		super(foundation);
		this.operatorGuid = operatorGuid;
	}

	@Override
	public String getOperatorGuid()
	{
		return this.operatorGuid;
	}

	public void setPrepareContain(WBSPrepareContain prepareContain)
	{
		this.prepareContain = prepareContain;
	}

	@Override
	public QueryHandler getQueryHandler()
	{
		return this.queryHandler;
	}

	@Override
	public CalculateHandler getCalculateHandler()
	{
		return this.calculateHandler;
	}

	@Override
	public UpdateHandler getUpdateHandler()
	{
		return this.updateHandler;
	}

	@Override
	public CheckHandler getCheckHandler()
	{
		return this.checkHandler;
	}

}