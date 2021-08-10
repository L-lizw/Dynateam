package dyna.app.service.brs.dcr.checkrule;

import dyna.app.service.DataAccessService;
import dyna.common.bean.data.FoundationObject;
import dyna.common.exception.ServiceRequestException;

import java.util.Map;

public interface ICondition
{
	/**
	 * 设置服务
	 * 
	 * @param service
	 */
	void setService(DataAccessService service);

	/**
	 * 执行检查规则
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	boolean check() throws ServiceRequestException;

	FoundationObject getFoundationObject();

	void setFoundationObject(FoundationObject foundationObject);

	String getSessionId();

	void setSessionId(String sessionId);

	Map<String, FoundationObject> getDataMap();

	void setDataMap(Map<String, FoundationObject> dataMap);
}
