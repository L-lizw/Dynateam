package dyna.common.bean.data.ppms.wbs;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.wbs.handler.PJCheckHandler;
import dyna.common.bean.data.ppms.wbs.handler.PJQueryHandler;
import dyna.common.bean.data.ppms.wbs.handler.PJTCalculateHandler;
import dyna.common.bean.data.ppms.wbs.handler.PJTUpdateHandler;

public class ProjectTemplateWBSDriver extends AbstractWBSDriver
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ProjectTemplateWBSDriver(FoundationObject rootObject, String operatorGuid)
	{
		super(rootObject, operatorGuid);
		this.calculateHandler = new PJTCalculateHandler(this);
		this.checkHandler = new PJCheckHandler(this);
		this.queryHandler = new PJQueryHandler(this);
		this.updateHandler = new PJTUpdateHandler(this);
	}
}
