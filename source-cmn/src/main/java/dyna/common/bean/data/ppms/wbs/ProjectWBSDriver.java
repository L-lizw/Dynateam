package dyna.common.bean.data.ppms.wbs;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.wbs.handler.PJCalculateHandler;
import dyna.common.bean.data.ppms.wbs.handler.PJCheckHandler;
import dyna.common.bean.data.ppms.wbs.handler.PJQueryHandler;
import dyna.common.bean.data.ppms.wbs.handler.PJUpdateHandler;

public class ProjectWBSDriver extends AbstractWBSDriver
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ProjectWBSDriver(FoundationObject rootObject, String operatorGuid)
	{
		super(rootObject, operatorGuid);
		this.calculateHandler = new PJCalculateHandler(this);
		this.checkHandler = new PJCheckHandler(this);
		this.queryHandler = new PJQueryHandler(this);
		this.updateHandler = new PJUpdateHandler(this);
	}

}
