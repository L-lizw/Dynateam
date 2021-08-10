package dyna.app.service.brs.wfi.processruntime;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.FilterBuilder;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

public class ProcessRuntimeDBStub extends AbstractServiceStub<WFIImpl>
{

	protected ProcessRuntimeDBStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 根据条件更新流程实例
	 * 
	 * @param valueMap
	 * @throws ServiceRequestException
	 */
	protected void updateProcess(Map<String, Object> valueMap) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.update(ProcessRuntime.class, valueMap, "updateProcessRuntime");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 重启流程的相关数据处理
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	protected void restartProcrt(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("PROCRTGUID", procRtGuid);
		sds.delete(ProcessRuntime.class, paramMap, "removeLock");

		sds.delete(ProcessRuntime.class, paramMap, "deletePerformer");

		sds.delete(ProcessRuntime.class, paramMap, "deletePerformerActual");
	}

	/**
	 * 根据guid查询流程实例
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected ProcessRuntime getProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		Map<String, Object> searchConditionMap = new HashMap<>();
		searchConditionMap.put(SystemObject.GUID, procRtGuid);

		ProcessRuntime process = this.getProcessRuntime(searchConditionMap, "select");

		return process;
	}

	/**
	 * 根据条件查询流程实例
	 * @param searchConditionMap
	 * @param sqlID
	 * @return
	 * @throws ServiceRequestException
	 */
	protected ProcessRuntime getProcessRuntime(Map<String, Object> searchConditionMap, String sqlID) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			ProcessRuntime process = sds.queryObject(ProcessRuntime.class, searchConditionMap, sqlID);

			return process;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 查询流程,objectguid 不为空则是查找实例执行过的流程
	 * 
	 * @param objectGuid
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ProcessRuntime> listProcessRuntime(ObjectGuid objectGuid, SearchCondition searchCondition) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = null;
			if (searchCondition == null)
			{
				searchConditionMap = new HashMap<>();
				searchConditionMap.put("ORDERBY", "ORDER BY createtime desc");
			}
			else
			{
				// b is sql table alia name
				String tableName = objectGuid != null ? "a" : "b";
				searchConditionMap = FilterBuilder.buildFilterBySearchCondition(searchCondition, tableName);
			}

			if (objectGuid != null)
			{
				searchConditionMap.put(ProcAttach.INSTANCE_GUID, objectGuid.getGuid());
			}

			List<ProcessRuntime> retList = sds.query(ProcessRuntime.class, searchConditionMap, "selectByObject");
			return retList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 查询符合条件的流程
	 * 
	 * @param paramMap
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ProcessRuntime> listProcessRuntime(Map<String, Object> paramMap, String sqlID) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<ProcessRuntime> resultList = sds.query(ProcessRuntime.class, paramMap, sqlID);

			return resultList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 查询子流程
	 * 
	 * @param parentRtGuid
	 *            父流程guid
	 * @param parentActrtGuid
	 *            父流程活动节点guid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected ProcessRuntime getSubProcessRuntime(String parentRtGuid, String parentActrtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put(ProcessRuntime.PARENT_GUID, parentRtGuid);
			searchConditionMap.put(ProcessRuntime.ACTRT_GUID, parentActrtGuid);

			ProcessRuntime procRt = sds.queryObject(ProcessRuntime.class, searchConditionMap, "selectcntsons");
			return procRt;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

}
