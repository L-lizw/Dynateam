/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 流程意见操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.track;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.aas.User;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcTrack;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程意见操作分支
 * 
 * @author Wanglei
 * 
 */
@Component
public class TrackStub extends AbstractServiceStub<WFIImpl>
{
	@Autowired
	private TrackDBStub dbStub = null;

	public void doTrack(String procRtGuid, String actRtGuid, DecisionEnum decide, String comments, String performerGuid, int startNumber, String agentGuid)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String operatorGuid = this.stubService.getOperatorGuid();
			ProcTrack track = new ProcTrack();
			track.setProcessRuntimeGuid(procRtGuid);
			track.setActRuntimeGuid(actRtGuid);
			track.setDecide(decide);
			track.setComments(comments);
			track.setStartNumber(startNumber);
			track.setPerformerGuid(performerGuid);
			track.setCreateUserGuid(operatorGuid);
			track.setUpdateUserGuid(operatorGuid);
			track.setAgent(agentGuid);

			sds.save(track);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void doTrack(ProcTrack track) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String operatorGuid = this.stubService.getOperatorGuid();
			track.setCreateUserGuid(operatorGuid);
			track.setUpdateUserGuid(operatorGuid);

			sds.save(track);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<ProcTrack> listComment(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			List<ProcTrack> trackList = this.dbStub.listComment(procRtGuid);

			this.decorateProTrack(trackList);
			return trackList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void decorateProTrack(List<ProcTrack> trackList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(trackList))
		{
			for (ProcTrack track : trackList)
			{
				String perfGuid = track.getPerformerGuid();
				String agentGuid = track.getAgent();
				if (!StringUtils.isNullString(perfGuid))
				{
					User user = this.stubService.getAAS().getUser(perfGuid);

					track.setPerformerName(user == null ? null : user.getName());
				}
				if (!StringUtils.isNullString(agentGuid))
				{
					User user = this.stubService.getAAS().getUser(agentGuid);
					track.setAgentName(user == null ? null : user.getName());
				}

				ActivityRuntime actRuntime = this.stubService.getActivityRuntimeStub().getActivityRuntime(track.getActRuntimeGuid());
				track.setActRuntimeTitle(actRuntime == null ? null : actRuntime.getTitle());
				track.setDeadline(actRuntime == null ? null : actRuntime.getDeadline());
			}
		}
	}

	public List<ProcTrack> listActivityComment(String actrtGuid, String startNumber) throws ServiceRequestException
	{
		try
		{
			return this.dbStub.listActivityComment(actrtGuid, startNumber);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
	
	/**
	 * 根据是否有执行意见判断是否执行过
	 * @param actRtGuid
	 * @param performerGuid
	 * @param startNumber
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isPerformed(String actRtGuid, String performerGuid, int startNumber) throws ServiceRequestException
	{

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ProcTrack.ACTRT_GUID, actRtGuid);
		params.put(ProcTrack.PERF_GUID, performerGuid);
		params.put(ProcTrack.START_NUMBER, startNumber);
		List<ProcTrack> trackList = this.dbStub.listActivityComment(params);
		if (!SetUtils.isNullList(trackList))
		{
			return true;
		}
		return false;
	}

}
