/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 活动操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.activity.app.application;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ActivityRuntimeApplication;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.dto.aas.User;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.Performer;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ActRuntimeModeEnum;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

/**
 * 人为活动节点相关操作
 * 
 * @author lizw
 *
 */
@Component
public class ManualActivityApplication implements ActivityRuntimeApplication
{
	@Autowired
	private WFIImpl stubService = null;

	@Override
	public void finishActivity(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		this.stubService.getNoticeStub().activiteCompleteNotice(activity, decide);
		if (decide == DecisionEnum.ACCEPT)
		{
			this.stubService.getAttachStub().excuteERPWorkflow(activity.getProcessRuntimeGuid(), activity.getGuid());
		}
	}

	@Override
	public ActivityRuntime fireNextAcceptActivity(ActivityRuntime nextActRt) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		ActivityRuntime returnActrt = null;
		List<Performer> listPerformer = this.stubService.listPerformer(nextActRt.getGuid());
		if (SetUtils.isNullList(listPerformer) && nextActRt.getActMode() == ActRuntimeModeEnum.BYPASS)
		{
			// wfeImpl.getActivityRuntimeStub().updateActrtStartNumber(nextActRt);
			// this.finishActivity(wfeImpl, nextActRt, DecisionEnum.ACCEPT);
			// wfeImpl.getFTS().createTransformQueue4WF(nextActRt);
			this.stubService.getActivityRuntimeStub().finishActivity(nextActRt, DecisionEnum.ACCEPT);

			returnActrt = this.stubService.getActivityRuntimeStub().fireNextAcceptActivity(nextActRt);
		}
		else
		{
			this.stubService.getActivityRuntimeStub().setAsCurrentActivity(nextActRt);

			// 执行人为空是，跳过活动节点
			if (SetUtils.isNullList(listPerformer))
			{
				this.stubService.performActivityRuntime(nextActRt.getGuid(), null, DecisionEnum.SKIP, null, null);
			}
		}
		return returnActrt;
	}

	@Override
	public ActivityRuntime fireRejectActivity(ActivityRuntime rejAct) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		ActRuntimeModeEnum actMode = rejAct.getActMode();
		if (actMode != ActRuntimeModeEnum.BYPASS)
		{
			this.stubService.getActivityRuntimeStub().setAsCurrentActivity(rejAct);

			this.stubService.getPerformerStub().resetPerformersOfActivityRutime(rejAct.getGuid());

			this.stubService.getActivityRuntimeStub().resetRelatedActivityRuntime(rejAct);

			// 拒绝后继续执行
			List<Performer> listPerformer = this.stubService.listPerformer(rejAct.getGuid());

			// 执行人为空是，跳过活动节点
			if (SetUtils.isNullList(listPerformer))
			{
				this.stubService.performActivityRuntime(rejAct.getGuid(), null, DecisionEnum.SKIP, null, null);
			}

		}
		return null;
	}

	@Override
	public boolean performActivityRuntime(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		boolean isExecute = true;

		WorkflowTemplateAct workflowTemplateAct = this.stubService.getActivityRuntimeStub().getWorkflowTemplateActSingle(activity.getGuid());

		boolean isAllPerformer = WorkflowTemplateActInfo.EXECUTION_TYPE_AND.equals(workflowTemplateAct.getWorkflowTemplateActInfo().getExecutionType());

		if (isAllPerformer)
		{
			List<User> listActrtPerformer = this.stubService.getPerformerStub().listActrtPerformer(activity.getGuid());
			int allPerformer = listActrtPerformer.size();

			String templatePassagePercent = workflowTemplateAct.getWorkflowTemplateActInfo().getPassagePercent();
			double templateAcceptPassage = Math.ceil(Float.valueOf(templatePassagePercent) * allPerformer / 100);
			// double templateRejectPassage = Math.ceil((100 - Float.valueOf(templatePassagePercent))
			// * allPerformer / 100);
			double templateRejectPassage = allPerformer - templateAcceptPassage;
			if (DecisionEnum.ACCEPT == decide || DecisionEnum.SKIP == decide)
			{
				List<User> acceptPerformerList = this.stubService.getPerformerStub().listActrtAcceptPerformer(activity.getGuid());

				isExecute = acceptPerformerList.size() >= templateAcceptPassage;
			}
			else if (DecisionEnum.REJECT == decide)
			{
				List<User> rejectPerformerList = this.stubService.getPerformerStub().listActrtRejectPerformer(activity.getGuid());

				isExecute = rejectPerformerList.size() > templateRejectPassage;
			}
			if (isExecute)
			{
				this.doTrack4Manual(activity, decide);
			}
		}

		return isExecute;
	}

	/**
	 * 活动执行完后保存意见
	 * 
	 * @param activity
	 * @param decide
	 * @throws ServiceRequestException
	 */
	private void doTrack4Manual(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		List<User> users = this.stubService.listNotFinishPerformer(activity.getGuid());
		String operatorGuid = this.stubService.getOperatorGuid();
		LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
		User operatorUser = this.stubService.getAAS().getUser(operatorGuid);
		if (!SetUtils.isNullList(users))
		{
			for (User user : users)
			{
				if (!user.getGuid().equals(operatorGuid))
				{
					String content = this.stubService.getMSRM().getMSRString("ID_WF_ATTACH_ALREAD_DEAL", languageEnum.toString());
					content = MessageFormat.format(content, operatorUser.getUserName());
					this.stubService.getTrackStub().doTrack(activity.getProcessRuntimeGuid(), activity.getGuid(), decide, content, user.getGuid(), activity.getStartNumber(), null);
				}
			}
		}

	}

}
