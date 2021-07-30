package dyna.common.bean.data.ppms.wbs;

import dyna.common.bean.data.ppms.wbs.handler.CalculateHandler;
import dyna.common.bean.data.ppms.wbs.handler.CheckHandler;
import dyna.common.bean.data.ppms.wbs.handler.QueryHandler;
import dyna.common.bean.data.ppms.wbs.handler.UpdateHandler;


public interface WBSDriver
{
	public QueryHandler getQueryHandler();

	public CalculateHandler getCalculateHandler();

	public UpdateHandler getUpdateHandler();

	public CheckHandler getCheckHandler();

	public String getOperatorGuid();
}
