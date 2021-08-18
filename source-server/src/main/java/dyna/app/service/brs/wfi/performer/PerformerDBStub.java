package dyna.app.service.brs.wfi.performer;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.dto.wf.Performer;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PerformerDBStub extends AbstractServiceStub<WFIImpl>
{

	/**
	 * 保存实际执行人
	 * 
	 * @param procrtGuid
	 * @param actrtGuid
	 * @param userGuid
	 */
	protected void saveActualPerformer(String procrtGuid, String actrtGuid, String userGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Performer performer = new Performer();
		performer.setProcessRuntimeGuid(procrtGuid);
		performer.setActRuntimeGuid(actrtGuid);
		performer.setPerformerGuid(userGuid);
		performer.setCreateUserGuid(userGuid);
		performer.setUpdateUserGuid(userGuid);

		sds.save(performer, "insertActualPerformer");
	}

	/**
	 * 取得当前活动节点的所有已完成用户
	 * 
	 * @param actrtGuid
	 * @return
	 */
	protected List<Performer> selectActPerformerActual(String actrtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Performer.ACTRT_GUID, actrtGuid);
		List<Performer> actualPerformerList = sds.query(Performer.class, param, "selectActPerformerActual");
		return actualPerformerList;
	}

	protected void setPerformerFinished(String performerGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Performer.GUID, performerGuid);
		sds.update(Performer.class, param, "setPerformerFinished");
	}

	protected boolean isUserPerformed(String userGuid, String actrtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(Performer.ACTRT_GUID, actrtGuid);
		param.put(Performer.PERF_GUID, userGuid);
		Performer performer = sds.queryObject(Performer.class, param, "selectActPerformerActual");

		return performer != null;
	}
	
	/**
	 * 删除流程实例中实际执行人
	 * 
	 * @param procRtGuid
	 */
	protected void deleteActualPerformer(String procRtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<>();
		filter.put(Performer.PROCRT_GUID, procRtGuid);
		sds.delete(Performer.class, filter, "deleteperformeractual");
	}
	
	/**
	 * 删除流程实例中所有执行人
	 * 
	 * @param procRtGuid
	 */
	protected void deleteAllPerformer(String procRtGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<>();
		filter.put(Performer.PROCRT_GUID, procRtGuid);
		sds.delete(Performer.class, filter, "deleteallperformer");
	}

}
