/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AttachCommentStub
 * Wanglei 2011-4-2
 */
package dyna.app.service.brs.wfi.comment;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.aas.User;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcTrackAttach;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 附件意见 操作分支
 * 
 * @author Wanglei
 * 
 */
@Component
public class AttachCommentStub extends AbstractServiceStub<WFIImpl>
{
	@Autowired
	private AttachCommentDBStub dbStub = null;

	public List<ProcTrackAttach> listProcAttachComment(String procRtGuid, String actRtGuid, String attachGuid) throws ServiceRequestException
	{
		List<ProcTrackAttach> opinionList = this.dbStub.listProcAttachComment(procRtGuid, actRtGuid, attachGuid);

		return this.decorateActrtTitle(opinionList);
	}

	protected ProcTrackAttach getProcAttachComment(String attachOpinion) throws ServiceRequestException
	{
		ProcTrackAttach opinion = this.dbStub.getProcAttachComment(attachOpinion);

		return this.decorateActrtTitle(opinion);
	}

	/**
	 * 添加附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void addAttachOpinion(String procRtGuid, String actRtGuid, String attchGuid, ProcTrackAttach attachOpinion) throws ServiceRequestException
	{
		try
		{
			String operatorGuid = this.stubService.getOperatorGuid();

			attachOpinion.setActRuntimeGuid(actRtGuid);
			attachOpinion.setAttachGuid(attchGuid);
			attachOpinion.setProcessRuntimeGuid(procRtGuid);
			attachOpinion.setPerformerGuid(operatorGuid);
			attachOpinion.setCreateUserGuid(operatorGuid);
			attachOpinion.setUpdateUserGuid(operatorGuid);

			this.dbStub.addAttachOpinion(attachOpinion);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 修改附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void updateAttachOpinion(ProcTrackAttach attachOpinion) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		if (attachOpinion != null && !operatorGuid.equalsIgnoreCase(attachOpinion.getCreateUserGuid()))
		{
			throw new ServiceRequestException("ID_APP_ATTACH_OPINION_UPDATE", "no ACL update opinion");
		}

		this.dbStub.updateAttachOpinion(attachOpinion);
	}

	/**
	 * 删除附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void deleteAttachOpinion(String attachOpinion) throws ServiceRequestException
	{

		String operatorGuid = this.stubService.getOperatorGuid();
		ProcTrackAttach procAttachComment = this.getProcAttachComment(attachOpinion);
		if (procAttachComment != null && !operatorGuid.equalsIgnoreCase(procAttachComment.getCreateUserGuid()))
		{
			throw new ServiceRequestException("ID_APP_ATTACH_OPINION_DELETE", "no ACL delete opinion");
		}

		this.dbStub.deleteAttachOpinion(attachOpinion);
	}

	/**
	 * 给返回值actrttitle字段赋值（批量处理）
	 * 
	 * @param opinionList
	 * @return
	 * @throws ServiceRequestException
	 */

	private List<ProcTrackAttach> decorateActrtTitle(List<ProcTrackAttach> opinionList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(opinionList))
		{
			for (ProcTrackAttach procTrackAttach : opinionList)
			{
				this.decorateActrtTitle(procTrackAttach);
			}
		}
		return opinionList;
	}

	/**
	 * 给返回值actrttitle字段赋值(单个处理)
	 * 
	 * @param opinionList
	 * @return
	 * @throws ServiceRequestException
	 */

	private ProcTrackAttach decorateActrtTitle(ProcTrackAttach procTrackAttach) throws ServiceRequestException
	{
		if (procTrackAttach != null)
		{
			String actrtGuid = procTrackAttach.getActRuntimeGuid();
			if (!StringUtils.isNullString(actrtGuid))
			{
				ActivityRuntime actrtRuntime = this.stubService.getActivityRuntimeStub().getActivityRuntime(actrtGuid);
				procTrackAttach.setActRuntimeTitle(actrtRuntime == null ? null : actrtRuntime.getTitle());
			}
			String createUserGuid = procTrackAttach.getCreateUserGuid();
			if (!StringUtils.isNullString(createUserGuid))
			{
				User createUser = this.stubService.getAAS().getUser(createUserGuid);
				procTrackAttach.setCreateUserName(createUser == null ? null : createUser.getName());
			}

		}
		return procTrackAttach;
	}
}
