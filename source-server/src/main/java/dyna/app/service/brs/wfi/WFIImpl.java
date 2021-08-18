/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WFMImpl
 * Wanglei 2010-11-4
 */
package dyna.app.service.brs.wfi;

import dyna.app.core.track.annotation.Tracked;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.server.Server;
import dyna.app.service.BusinessRuleService;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.boas.tracked.TROpenObjectImpl;
import dyna.app.service.brs.boas.tracked.TRSaveRelationByTemplateImpl;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.brs.dss.tracked.TRDSSFileInfoImpl;
import dyna.app.service.brs.edap.EDAPImpl;
import dyna.app.service.brs.pms.PMSImpl;
import dyna.app.service.brs.sms.SMSImpl;
import dyna.app.service.brs.wfi.activity.ActivityRuntimeStub;
import dyna.app.service.brs.wfi.attach.AttachStub;
import dyna.app.service.brs.wfi.comment.AttachCommentStub;
import dyna.app.service.brs.wfi.favoritecomment.FavoriteCommentStub;
import dyna.app.service.brs.wfi.performer.PerformerStub;
import dyna.app.service.brs.wfi.processruntime.ProcessRuntimeStub;
import dyna.app.service.brs.wfi.routrestriction.RoutRestrictionStub;
import dyna.app.service.brs.wfi.scheduledtask.TimeTaskServerContextListener;
import dyna.app.service.brs.wfi.track.TRPerfActImpl;
import dyna.app.service.brs.wfi.track.TRProcessImpl;
import dyna.app.service.brs.wfi.track.TrackStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.extra.ProcessSetting;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.bean.model.wf.template.WorkflowTemplateActClass;
import dyna.common.bean.model.wf.template.WorkflowTemplateVo;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.*;
import dyna.common.dto.wf.*;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.common.util.SetUtils;
import dyna.net.service.brs.*;
import dyna.net.service.das.MSRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WFI 实现类
 * 
 * @author Wanglei
 * 
 */
@Service
public class WFIImpl extends BusinessRuleService implements WFI
{
	@Autowired
	private ActivityRuntimeStub			activityRuntimeStub	= null;
	@Autowired
	private AttachStub					attachStub			= null;
	@Autowired
	private FavoriteCommentStub			favoriteCommentStub	= null;
	@Autowired
	private AttachCommentStub			attachCommentStub	= null;
	@Autowired
	private GraphStub					graphStub			= null;
	@Autowired
	private LockStub					lockStub			= null;
	@Autowired
	private PerformerStub				performerStub		= null;
	@Autowired
	private ProcessRuntimeStub			processRuntimeStub	= null;
	@Autowired
	private RoutRestrictionStub			routRestrictionStub	= null;
	@Autowired
	private TrackStub					trackStub			= null;
	@Autowired
	private WorkflowTemplateStub		templateStub		= null;
	@Autowired
	private NoticeStub					noticeStub			= null;
	@Autowired
	private WorkflowTemplateCacheStub	wfTemplateCacheStub	= null;
	private static boolean				isInit				= false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		super.authorize(method, args);
	}

	private synchronized static void timingNotice(Server server)
	{
		if (isInit)
		{
			return;
		}
		isInit = true;

		server.addServerContextListener(new TimeTaskServerContextListener());
	}

	@Override
	public void init()
	{
		timingNotice(this.server);

		this.getWfTemplateCacheStub().loadModel();

	}

	/**
	 * @return the activityRuntimeStub
	 */
	public ActivityRuntimeStub getActivityRuntimeStub()
	{
		return this.activityRuntimeStub;
	}

	/**
	 * @return the attachStub
	 */
	public AttachStub getAttachStub()
	{
		return this.attachStub;
	}

	/**
	 * @return the noticeStub
	 */
	public NoticeStub getNoticeStub()
	{
		return this.noticeStub;
	}

	/**
	 * @return the attachStub
	 */
	public WorkflowTemplateStub getTemplateStub()
	{
		return this.templateStub;
	}

	/**
	 * @return the favoriteCommentStub
	 */
	protected FavoriteCommentStub getFavoriteCommentStub()
	{
		return this.favoriteCommentStub;
	}

	/**
	 * @return the attachCommentStub
	 */
	protected AttachCommentStub getAttachCommentStub()
	{
		return this.attachCommentStub;
	}

	/**
	 * @return the graphStub
	 */
	protected GraphStub getGraphStub()
	{
		return this.graphStub;
	}

	/**
	 * @return the lockStub
	 */
	public LockStub getLockStub()
	{
		return this.lockStub;
	}

	/**
	 * @return the performerStub
	 */
	public PerformerStub getPerformerStub()
	{
		return this.performerStub;
	}

	/**
	 * @return the processRuntimeStubr
	 */
	public ProcessRuntimeStub getProcessRuntimeStub()
	{
		return this.processRuntimeStub;
	}

	/**
	 * @return the routRestrictionStub
	 */
	public RoutRestrictionStub getRoutRestrictionStub()
	{
		return this.routRestrictionStub;
	}

	/**
	 * @return the trackStub
	 */
	public TrackStub getTrackStub()
	{
		return this.trackStub;
	}

	public WorkflowTemplateCacheStub getWfTemplateCacheStub()
	{
		return this.wfTemplateCacheStub;
	}

	public synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized DCR getDCR() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DCR.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized FTS getFTS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(FTS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized UECS getUECS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(UECS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized PMS getPMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(PMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized CAD getCAD() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(CAD.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized ERPI getERP() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(ERPI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized ACL getACL() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(ACL.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized WFM getWFM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized SMSImpl getSMS() throws ServiceRequestException
	{
		try
		{
			return (SMSImpl) this.getRefService(SMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized EDAPImpl getEDAP() throws ServiceRequestException
	{
		try
		{
			return (EDAPImpl) this.getRefService(EDAP.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized DSS getDSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DSS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized EOSS getEOSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EOSS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	@Tracked(description = TrackedDesc.CREATE_PRC, renderer = TRProcessImpl.class)
	@Override
	public ProcessRuntime createProcess(String wfTemplateGuid, String parentProcGuid, String parentActGuid, String description, ProcAttach... procAttach)
			throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().createProcess(wfTemplateGuid, parentProcGuid, parentActGuid, description, true, procAttach);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#saveProcess(java.lang.String, dyna.common.bean.extra.ProcessSetting)
	 */
	@Override
	public ProcessRuntime saveProcess(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().saveProcess(procRtGuid, settings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#addAttachment(java.lang.String, dyna.common.bean.data.system.ProcAttachSetting[])
	 */
	@Override
	public List<ProcAttach> addAttachment(String procRtGuid, ProcAttach... procAttach) throws ServiceRequestException
	{
		return this.getAttachStub().addAttachment(procRtGuid, true, procAttach);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getActivityRuntime(java.lang.String)
	 */
	@Override
	public ActivityRuntime getActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().getActivityRuntime(actRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getProcessRuntime(java.lang.String)
	 */
	@Override
	public ProcessRuntime getProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().getProcessRuntime(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#isLock(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public String isLock(ObjectGuid attachment) throws ServiceRequestException
	{
		return this.getLockStub().isLock(attachment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#isPerformerOfProcessRuntime(java.lang.String)
	 */
	@Override
	public boolean isPerformerOfProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		return this.getPerformerStub().isPerformerOfProcessRuntime(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFM#listAcceptableFromActivityRuntime(java.lang.String)
	 */
	@Override
	public List<ActivityRuntime> listAcceptableFromActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().listAcceptableFromActivityRuntime(actRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFM#listComment(java.lang.String)
	 */
	@Override
	public List<ProcTrack> listComment(String procRtGuid) throws ServiceRequestException
	{
		return this.getTrackStub().listComment(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listCurrentActivityRuntime(java.lang.String)
	 */
	@Override
	public List<ActivityRuntime> listCurrentActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().listCurrentActivityRuntime(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFM#listPerformer(java.lang.String)
	 */
	@Override
	public List<Performer> listPerformer(String actRtGuid) throws ServiceRequestException
	{
		return this.getPerformerStub().listPerformer(actRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFM#listRejectableFromActivityRuntime(java.lang.String)
	 */
	@Override
	public List<ActivityRuntime> listRejectableFromActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().listRejectableFromActivityRuntime(actRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listRejectedDestinedActivityRuntime(java.lang.String)
	 */
	@Override
	public List<ActivityRuntime> listRejectedDestinedActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().listRejectedDestinedActivityRuntime(actRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listSortedPerformableActivityRuntime(java.lang.String)
	 */
	@Override
	public List<ActivityRuntime> listSortedPerformableActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().listSortedPerformableActivityRuntime(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFM#performActivityRuntime(java.lang.String, java.lang.String,
	 * dyna.common.systemenum.DecisionEnum)
	 */
	@Tracked(description = TrackedDesc.PERF_ACT, renderer = TRPerfActImpl.class)
	@Override
	public ActivityRuntime performActivityRuntime(String actRtGuid, String comment, DecisionEnum decide, String rejectToActRtGuid, ProcessSetting processSetting)
			throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().performActivityRuntime(actRtGuid, comment, decide, rejectToActRtGuid, null, processSetting, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFM#resumeProcess(java.lang.String, java.lang.String, java.lang.String,
	 * dyna.common.bean.extra.ProcessPerformer, java.util.List)
	 */
	@Tracked(description = TrackedDesc.RESUME_PRC, renderer = TRProcessImpl.class)
	@Override
	public ActivityRuntime resumeProcess(String procRtGuid, String comments, ProcessSetting settings) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().resumeProcess(procRtGuid, comments, true, settings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listProcessRuntimeOfObject(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<ProcessRuntime> listProcessRuntimeOfObject(ObjectGuid objectGuid, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().listProcessRuntimeOfObject(objectGuid, searchCondition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listProcessRuntimeOfObject(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<ProcessRuntime> listProcessRuntime(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().listProcessRuntime(searchCondition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listProcessRuntime(java.util.Map)
	 */
	@Override
	public List<ProcessRuntime> listProcessRuntime(Map<String, Object> paramMap) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().listProcessRuntime(paramMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listFristPassApprovalProcessRuntime(java.util.Map)
	 */
	@Override
	public List<ProcessRuntime> listFristPassApprovalProcessRuntime(Map<String, Object> paramMap) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().listFristPassApprovalProcessRuntime(paramMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listActivityGraphInfo(java.lang.String)
	 */
	@Override
	public List<Object> listActivityGraphInfo(String procRtGuid) throws ServiceRequestException
	{
		return this.getGraphStub().listAllGraphActivity(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listGraphTransition(java.lang.String)
	 */
	@Override
	public List<GraphRuntimeTransition> listGraphTransition(String procRtGuid) throws ServiceRequestException
	{
		return this.getGraphStub().listGraphTransition(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listProcAttachOpinion(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ProcTrackAttach> listProcAttachComment(String procRtGuid, String actRtGuid, String attachGuid) throws ServiceRequestException
	{
		return this.getAttachCommentStub().listProcAttachComment(procRtGuid, actRtGuid, attachGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#saveTrackComm(dyna.common.bean.data.system.ProcTrackComm)
	 */
	@Override
	public ProcTrackComm saveTrackComm(ProcTrackComm trackComm) throws ServiceRequestException
	{
		return this.getFavoriteCommentStub().saveTrackComm(trackComm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#deleteTrackComm(java.lang.String)
	 */
	@Override
	public void deleteTrackComm(String trackCommGuid) throws ServiceRequestException
	{
		this.getFavoriteCommentStub().deleteTrackComm(trackCommGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listProcTrackComm()
	 */
	@Override
	public List<ProcTrackComm> listProcTrackComm() throws ServiceRequestException
	{
		return this.getFavoriteCommentStub().listProcTrackComm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#repealProcessRuntime()
	 */
	@Tracked(description = TrackedDesc.REPEAL_PRC, renderer = TRProcessImpl.class)
	@Override
	public void recallProcessRuntime(String procRtGuid, String actrtGuid) throws ServiceRequestException
	{
		this.getProcessRuntimeStub().recallProcess(procRtGuid, actrtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#addAttachOpinion(dyna.common.bean.data.system.ProcTrackAttach)
	 */
	@Override
	public void addAttachOpinion(String procRtGuid, String actRtGuid, String attchGuid, ProcTrackAttach attachOpinion) throws ServiceRequestException
	{
		this.getAttachCommentStub().addAttachOpinion(procRtGuid, actRtGuid, attchGuid, attachOpinion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#addAttachOpinion(dyna.common.bean.data.system.ProcTrackAttach)
	 */
	@Override
	public void updateAttachOpinion(ProcTrackAttach attachOpinion) throws ServiceRequestException
	{
		this.getAttachCommentStub().updateAttachOpinion(attachOpinion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listProcAttach(java.lang.String, dyna.common.SearchCondition)
	 */
	@Override
	public List<ProcAttach> listProcAttach(String procRtGuid) throws ServiceRequestException
	{
		return this.getAttachStub().listProcAttach(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getBeginActivityRuntime(java.lang.String)
	 */
	@Override
	public ActivityRuntime getBeginActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().getBeginActivityRuntime(procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getSubFlowSize(java.lang.String)
	 */
	@Override
	public Integer getSubFlowSize(String parentProcGuid, String parentRtGuid) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().getSubFlowSize(parentProcGuid, parentRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listSubProcessRuntime(java.lang.String)
	 */
	@Override
	public List<ProcessRuntime> listSubProcessRuntime(String parentProcGuid) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().listSubProcessRuntime(parentProcGuid);
	}

	@Override
	public FoundationObject getAttachment(ObjectGuid objectGuid, String processGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(objectGuid, false);
	}

	@Override
	public BOMView getBOMView(ObjectGuid objectGuid, String processGuid) throws ServiceRequestException
	{
		return ((BOMSImpl) this.getBOMS()).getBomViewStub().getBOMView(objectGuid, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#releaseProcess(java.util.List, java.lang.String)
	 */
	@Override
	public void releaseProcess(List<ObjectGuid> attachObjectGuidList) throws ServiceRequestException
	{
		this.getProcessRuntimeStub().releaseProcess(attachObjectGuidList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getInstanceLatestProcessRuntime(java.lang.String)
	 */
	@Override
	public ProcessRuntime getInstanceLatestProcessRuntime(String instanceGuid) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().getInstanceLatestProcessRuntime(instanceGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listNotFinishPerformer(java.lang.String)
	 */
	@Override
	public List<User> listNotFinishPerformer(String actRtGuid) throws ServiceRequestException
	{
		return this.getPerformerStub().listNotFinishPerformer(actRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#saveWorkflowTemplate(dyna.common.bean.data.system.WorkflowTemplate)
	 */
	@Override
	public WorkflowTemplateVo saveWorkflowTemplateInDetail(WorkflowTemplateVo workflowTemplate) throws ServiceRequestException
	{
		return this.getTemplateStub().saveWorkflowTemplate(workflowTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplate(java.lang.String)
	 */
	@Override
	public WorkflowTemplateVo getWorkflowTemplateDetail(String workflowTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateDetail(workflowTemplateGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateById(java.lang.String)
	 */
	@Override
	public WorkflowTemplateVo getWorkflowTemplateInDetailById(String workflowTemplateId) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateDetailById(workflowTemplateId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateInfo(java.lang.String)
	 */
	@Override
	public WorkflowTemplateInfo getWorkflowTemplateInfo(String workflowTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateInfo(workflowTemplateGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateInfo(java.lang.String)
	 */
	@Override
	public WorkflowTemplate getWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplate(workflowTemplateGuid, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateActSetInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkflowTemplateActInfo getWorkflowTemplateActSetInfo(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateActSetInfo(workflowTemplateGuid, actrtName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateActSetInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkflowTemplateAct getWorklfowTemplateActSet(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateActSet(workflowTemplateGuid, actrtName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateInfoById(java.lang.String)
	 */
	@Override
	public WorkflowTemplateInfo getWorkflowTemplateInfoById(String workflowTemplateId) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateInfoById(workflowTemplateId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listWorkflowTemplateInfoByWFName()
	 */
	@Override
	public List<WorkflowTemplateInfo> listWorkflowTemplateInfoByWFNameWithOutObserver(String workFlowName) throws ServiceRequestException
	{
		return this.getTemplateStub().listWorkflowTemplateInfoByWFNameWithOutObserver(workFlowName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listWorkflowName()
	 */
	@Override
	public List<String> listWorkflowName() throws ServiceRequestException
	{
		return this.getTemplateStub().listWorkflowName(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateActClassSetInfo(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public WorkflowTemplateActClassInfo getWorkflowTemplateActClassSetInfo(String workflowTemplateGuid, String workflowActrtName, String classGuid, String className,
			boolean isCurrentBM, String templateBMGuid) throws ServiceRequestException
	{
		WorkflowTemplateActClass actClass = this.getTemplateStub().getWorkflowTemplateActClassSetInfo(workflowTemplateGuid, workflowActrtName, classGuid, className, isCurrentBM,
				templateBMGuid);
		return actClass == null ? null : actClass.getWorkflowTemplateActClassInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listWorkflowTemplateActClassSetInfoSingle(java.lang.String)
	 */
	@Override
	public List<WorkflowTemplateActClassInfo> listWorkflowTemplateActClassSetInfoSingle(String workflowTemplateGuid, String actrtName) throws ServiceRequestException
	{
		return this.getTemplateStub().listWorkflowTemplateActClassSetInfoSingle(workflowTemplateGuid, actrtName);
	}

	@Override
	public List<StructureObject> listObjectOfRelation(ObjectGuid end1ObjectGuid, String viewName, SearchCondition searchCondition) throws ServiceRequestException
	{

		return ((BOASImpl) this.getBOAS()).getRelationStub().listObjectOfRelation(end1ObjectGuid, viewName, searchCondition, null, null, false);
	}

	@Override
	public ViewObject getRelation(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().getRelation(objectGuid, false);
	}

	@Override
	public List<FoundationObject> listProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String viewName) throws ServiceRequestException
	{
		return ((PMSImpl) this.getPMS()).getItemProductStub().listProductSummaryObject(productObjectGuid, searchCondition, viewName, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#uploadPreviewFile(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.system.DSSFileInfo)
	 */
	@Tracked(description = TrackedDesc.SET_PREVIEW_FILE, renderer = TRDSSFileInfoImpl.class)
	@Override
	public DSSFileTrans uploadPreviewFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException
	{
		return ((DSSImpl) this.getDSS()).getTransFileStub().uploadPreviewFile(objectGuid, file, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#attachFile(dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Tracked(description = TrackedDesc.ATTACH_FILE, renderer = TRDSSFileInfoImpl.class)
	@Override
	public DSSFileInfo attachFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException
	{
		return ((DSSImpl) this.getDSS()).getInstFileStub().attachFile(objectGuid, file, Constants.isSupervisor(false, this), false);
	}

	@Override
	public List<BOMView> listBOMView(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return ((BOMSImpl) this.getBOMS()).getBomViewStub().listBOMView(end1ObjectGuid, false);
	}

	@Override
	public List<StructureObject> listObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().listObjectOfRelation(viewObject, searchCondition, null, null, false);
	}

	@Override
	public List<ViewObject> listRelation(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().listRelation(end1ObjectGuid, false, true);
	}

	@Override
	public ViewObject getRelationByEND1(ObjectGuid end1ObjectGuid, String name) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().getRelationByEND1(end1ObjectGuid, name, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#link(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid,
	 * java.lang.String, dyna.common.bean.data.StructureObject)
	 */
	@Tracked(description = TrackedDesc.LINK_RELATION)
	@Override
	public StructureObject link(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject, String procRtGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationLinkStub().link(viewObjectGuid, end2FoundationObjectGuid, structureObject, false, procRtGuid);
	}

	@Override
	public void saveStructure4Detail(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList,
			String procRtGuid) throws ServiceRequestException
	{
		((BOASImpl) this.getBOAS()).getStructureStub().saveStructure4Detail(viewObjectGuid, linkList, unlinkList, updateList, procRtGuid);
	}

	@Override
	public FoundationObject saveObject(FoundationObject foundationObject, String procRtGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getFSaverStub().saveObject(foundationObject, true, false, procRtGuid);
	}

	@Override
	public FoundationObject saveObject(FoundationObject foundationObject, RelationTemplateTypeEnum structureModel, String procRtGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getFSaverStub().saveObject(foundationObject, structureModel, false, procRtGuid);
	}

	@Tracked(description = TrackedDesc.SAVE_RELATION, renderer = TRFoundationImpl.class)
	@Override
	public ViewObject saveRelation(ViewObject relation, String procRtGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().saveRelation(relation, false, procRtGuid);
	}

	@Tracked(description = TrackedDesc.SAVE_RELATION, renderer = TRSaveRelationByTemplateImpl.class)
	@Override
	public ViewObject saveRelationByTemplate(String relationTemplateGuid, ObjectGuid end1ObjectGuid, String procRtGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().saveRelationByTemplate(relationTemplateGuid, end1ObjectGuid, false, procRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listWorkflowTemplateInfoByWFNameContainObsolete(java.lang.String)
	 */
	@Override
	public List<WorkflowTemplateInfo> listWorkflowTemplateInfoByWFNameContainObsolete(String workFlowName) throws ServiceRequestException
	{
		return this.getTemplateStub().listWorkflowTemplateInfoByWFNameContainObsolete(workFlowName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#getWorkflowTemplateWithOutGuid(java.lang.String)
	 */
	@Override
	public WorkflowTemplateVo copyWorkflowTemplateWithOutGuid(String workflowTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().copyWorkflowTemplateWithOutGuid(workflowTemplateGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#obsoleteWorkflowTemplate(java.lang.String)
	 */
	@Override
	public void obsoleteWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException
	{
		this.getTemplateStub().obsoleteWorkflowTemplate(workflowTemplateGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#isInRunningProcess(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public String isInRunningProcess(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().isInRunningProcess(objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#removeAttachment(java.lang.String,
	 * dyna.common.bean.data.system.ProcAttachSetting[])
	 */
	@Override
	public void removeAttachment(String procRtGuid, List<String> procAttachGuidList) throws ServiceRequestException
	{
		this.getAttachStub().removeAttachment(procRtGuid, procAttachGuidList, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listRelationWithOutBuiltIn(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<ViewObject> listRelationWithOutBuiltIn(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getRelationStub().listRelation(end1ObjectGuid, false, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#listWorkflowNameContainObsolete()
	 */
	@Override
	public List<String> listWorkflowNameContainObsolete() throws ServiceRequestException
	{
		return this.getTemplateStub().listWorkflowName(true);
	}

	@Override
	public void unlink(StructureObject structureObject) throws ServiceRequestException
	{
		((BOASImpl) this.getBOAS()).getRelationUnlinkStub().unlink(structureObject, false);
	}

	@Override
	public void unlink(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, String relationTemplate) throws ServiceRequestException
	{
		((BOASImpl) this.getBOAS()).getRelationUnlinkStub().unlink(viewObjectGuid, end2FoundationObjectGuid, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#replaceCADByItem(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Override
	public void replaceCADByItem(ObjectGuid cadFrom, ObjectGuid cadTo, ObjectGuid itemObjectGuid, String templateName) throws ServiceRequestException
	{
//		((CADImpl) this.getCAD()).getCadStub().replaceCADByItem(cadFrom, cadTo, itemObjectGuid, templateName, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#unlinkCAD(dyna.common.bean.data.ObjectGuid, java.lang.String,
	 * dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void unlinkCAD(ObjectGuid itemObjectGuid, String templateName, ObjectGuid cadObjectGuid) throws ServiceRequestException
	{
//		((CADImpl) this.getCAD()).getCadStub().unlinkCAD(itemObjectGuid, templateName, cadObjectGuid, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#unlinkAndDeleteEnd2(dyna.common.bean.data.StructureObject)
	 */
	@Override
	public void unlinkAndDeleteEnd2(StructureObject structureObject) throws ServiceRequestException
	{

		((BOASImpl) this.getBOAS()).getRelationUnlinkStub().unlinkAndDeleteEnd2(structureObject, false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#fireNextActivity(dyna.common.bean.data.system.ActivityRuntime,
	 * dyna.common.systemenum.DecisionEnum, java.lang.String)
	 */
	@Override
	public synchronized ActivityRuntime fireNextActivity(ActivityRuntime activity, DecisionEnum decision, String rejectToActRtGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().fireNextActivity(activity, decision, rejectToActRtGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#detachFile(java.lang.String)
	 */
	@Override
	public void detachFile(String fileGuid) throws ServiceRequestException
	{
		((DSSImpl) this.getDSS()).getInstFileStub().detachFile(fileGuid, true, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.WFE#linkGenericItemCAD(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Override
	public void linkGenericItemCAD(ObjectGuid modelObjectGuid, ObjectGuid itemObjectGuid, String templateName, String procRtGuid) throws ServiceRequestException
	{
//		((CADImpl) this.getCAD()).getCadStub().linkGenericItemCAD(modelObjectGuid, itemObjectGuid, templateName, false, procRtGuid);
	}

	@Override
	public boolean isEffectiveApprovalActivity(String procRtGuid, String actRtGuid) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().isEffectiveApprovalProcess(procRtGuid, actRtGuid);
	}

	@Override
	public List<ActivityRuntime> listHistoryActivityRuntimeAndPerformer(String procRtGuid) throws ServiceRequestException
	{

		return this.getActivityRuntimeStub().listHistoryActivityRuntimeAndPerformer(procRtGuid);
	}

	@Override
	public List<ProcTrack> listActivityComment(String actrtGuid, String startNumber) throws ServiceRequestException
	{
		return this.getTrackStub().listActivityComment(actrtGuid, startNumber);
	}

	@Override
	public void calculateAttach(String procRtGuid, ProcAttachSetting settings) throws ServiceRequestException
	{
		this.getAttachStub().calculateAttach(procRtGuid, settings, true);

	}

	@Override
	public List<BOInfo> calculateEnableAttachBO(String procRtGuid, ObjectGuid attachInstanceObjectGuid, List<WFRelationSet> relationSetList) throws ServiceRequestException
	{

		return this.getAttachStub().calculateEnableAttachBO(procRtGuid, attachInstanceObjectGuid, relationSetList);
	}

	@Override
	public List<ProcAttach> verifyValidAttach(String procRtGuid) throws ServiceRequestException
	{
		return this.getAttachStub().verifyValidAttach(procRtGuid, true);
	}

	@Override
	public void changePrimaryObject(ObjectGuid end1ObjectGuid, String viewName, StructureObject structureObject) throws ServiceRequestException
	{
		((BOASImpl) this.getBOAS()).getStructureStub().changePrimaryObject(end1ObjectGuid, viewName, structureObject, false);

	}

	@Override
	public List<ProcAttach> listInValidAttach(String procRtGuid) throws ServiceRequestException
	{
		return this.getAttachStub().listInValidAttach(procRtGuid);
	}

	@Override
	public List<ProcAttach> listProcAttachContainInValid(String procRtGuid) throws ServiceRequestException
	{
		return this.getAttachStub().listProcAttachContainInValid(procRtGuid);
	}

	@Override
	public void savePerformer(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		this.getPerformerStub().savePerformer(procRtGuid, settings);

	}

	@Override
	public void addPerformer(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		this.getPerformerStub().addPerformer(procRtGuid, settings);

	}

	@Override
	public void checkPerform(String actRtGuid, String rejectToActRtGuid, boolean isAllActivity) throws ServiceRequestException
	{
		ActivityRuntime activityRuntime = this.getActivityRuntimeStub().getActivityRuntime(actRtGuid);
		this.getPerformerStub().checkPerform(activityRuntime, rejectToActRtGuid, isAllActivity);

	}

	@Override
	public void deleteAttachOpinion(String attachOpinion) throws ServiceRequestException
	{
		this.getAttachCommentStub().deleteAttachOpinion(attachOpinion);
	}

	@Override
	public List<ProcAttach> listSubProcAttach(String procRtGuid, String workflowTemplateGuid) throws ServiceRequestException
	{
		return this.getAttachStub().listSubProcAttach(procRtGuid, workflowTemplateGuid);
	}

	@Override
	@Tracked(description = TrackedDesc.DELETE_PRC, renderer = TRProcessImpl.class)
	public void deleteProcess(ProcessRuntime processRuntime) throws ServiceRequestException
	{
		this.getProcessRuntimeStub().deleteProcess(processRuntime);
	}

	@Tracked(description = TrackedDesc.VIEW_OBJECT, renderer = TROpenObjectImpl.class)
	@Override
	public FoundationObject openObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.getBOAS()).getFUIStub().openObject(objectGuid, false);
	}

	@Override
	public void removeECPAttachment(String procRtGuid) throws ServiceRequestException
	{
		this.getAttachStub().removeECPAttachment(procRtGuid);
	}

	// O11877/A.3-“无人员角色”和“人员意外禁用”导致的流程无法审批也无法退回
	@Override
	public List<ProcessRuntime> queryActrtProcess(String guid, PerformerTypeEnum perfTypeEnum) throws ServiceRequestException
	{
		return this.getProcessRuntimeStub().queryActrtProcess(guid, perfTypeEnum);
	}

	/**
	 * 查询user是否在流程中
	 * 
	 */
	@Override
	public List<Performer> queryPerformer(String guid) throws ServiceRequestException
	{
		return this.getPerformerStub().queryPerformer(guid);
	}

	/**
	 * 根据用户guid和流程活动节点查询出所有被代理人
	 * 
	 * @param agentGuid
	 * @param actrtGuid
	 * @return
	 */
	@Override
	public List<String> getPrincipalsOfAgent(String agentGuid, String actrtGuid) throws ServiceRequestException
	{
		return this.getPerformerStub().getPrincipalsOfAgent(agentGuid, actrtGuid);
	}

	@Override
	public List<String> getNotFinishedPrincipalsOfAgent(String agentGuid, String actrtGuid) throws ServiceRequestException
	{
		return this.getPerformerStub().getNotFinishedPrincipalsOfAgent(agentGuid, actrtGuid);
	}

	@Override
	public boolean isAgentPerformerOfActrt(String agentGuid, String actrtGuid) throws ServiceRequestException
	{
		return this.getPerformerStub().isAgentPerformerOfActrt(agentGuid, actrtGuid);
	}

	@Override
	public void sendPerformMail(List<Performer> listPerformer, ActivityRuntime activity, MailMessageType messageType) throws ServiceRequestException
	{
		this.getNoticeStub().sendPerformMail(listPerformer, activity, messageType);
	}

	@Override
	public WorkflowActivityRuntimeData getWorkflowRuntimeData(String procRtGuid, String actRuntimeGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().getWorkflowRuntimeData(procRtGuid, actRuntimeGuid);
	}

	@Override
	public WorkflowActivityRuntimeData getWorkflowRuntimeData(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getActivityRuntimeStub().getWorkflowRuntimeData(objectGuid);
	}

	@Override
	public WorkflowTemplate reUseWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().reUseWorkflowTemplate(workflowTemplateGuid);
	}

	@Override
	public List<WorkflowTemplateActPerformerInfo> listAllPerformerOfAct(String wfTemplateActGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().listAllPerformerOfAct(wfTemplateActGuid, null);
	}

	@Override
	public List<WorkflowTemplateActPerformerInfo> listPerFormerOfActByType(String wfTemplateActGuid, String noticeType) throws ServiceRequestException
	{
		return this.getTemplateStub().listAllPerformerOfAct(wfTemplateActGuid, noticeType);
	}

	@Override
	public Map<String, WorkflowTemplateActAdvnoticeInfo> getAllAdvnoticeSetInfoOfAct(String wfTemplateActGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().getAllAdvnoticeSetInfoOfAct(wfTemplateActGuid);
	}

	@Override
	public WorkflowTemplateActAdvnoticeInfo getAdvnoticeSetInfoByType(String wfTemplateActGuid, String noticeType) throws ServiceRequestException
	{
		return this.getTemplateStub().getAdvnoticeSetInfoByType(wfTemplateActGuid, noticeType);
	}

	@Override
	public List<WorkflowTemplateActCompanyInfo> listWorkflowTemplateActCompanyInfoOfAct(String wfTemplateActGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().listWorkflowTemplateActCompanyInfoOfAct(wfTemplateActGuid);
	}

	@Override
	public WorkflowTemplateActClassInfo getWorkflowTemplateActClassInfo(String actClassguid) throws ServiceRequestException
	{
		return this.getTemplateStub().getWorkflowTemplateActClassInfo(actClassguid);
	}

	@Override
	public List<WorkflowTemplateActClassUIInfo> listUIInfoOfWfActClass(String actClassguid) throws ServiceRequestException
	{
		return this.getTemplateStub().listUIInfoOfWfActClass(actClassguid);
	}

	@Override
	public List<WorkflowTemplateActClassRelationInfo> listRelationInfoOfWfActClass(String actClassguid) throws ServiceRequestException
	{
		return this.getTemplateStub().listRelationInfoOfWfActClass(actClassguid);
	}

	@Override
	public List<WorkflowTemplateActPerformerInfo> listAllPerformerOfTemplate(String wfTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().listAllPerformerOfTemplate(wfTemplateGuid);
	}

	@Override
	public List<WorkflowTemplateActPerformerInfo> listPerFormerOfTemplateByType(String wfTemplateGuid, String noticeType) throws ServiceRequestException
	{
		return this.getTemplateStub().listPerFormerOfTemplateByType(wfTemplateGuid, noticeType);
	}

	@Override
	public Map<String, WorkflowTemplateActAdvnoticeInfo> getAllAdvnoticeSetInfoOfTemplate(String wfTemplateGuid) throws ServiceRequestException
	{
		return this.getTemplateStub().getAllAdvnoticeSetInfoOfTemplate(wfTemplateGuid);
	}

	@Override
	public List<WorkflowTemplateScopeBoInfo> listScopeBoCanLaunchOfTemplate(String wfTemplateguid) throws ServiceRequestException
	{
		return this.getTemplateStub().listScopeBoCanLaunchOfTemplate(wfTemplateguid);
	}

	@Override
	public List<WorkflowTemplateScopeBoInfo> listScopeBoOfTemplate(String wfTemplateguid) throws ServiceRequestException
	{
		return this.getTemplateStub().listScopeBoOfTemplate(wfTemplateguid);
	}

	@Override
	public List<WorkflowTemplateScopeRTInfo> listScoperRelationTemplateOfTemplate(String wfTemplateguid) throws ServiceRequestException
	{
		return this.getTemplateStub().listScoperRelationTemplateOfTemplate(wfTemplateguid);
	}

	@Override
	public boolean isCurrentActivityApprover(String procRtGuid, String actrtName) throws ServiceRequestException
	{
		List<ActivityRuntime> activityList = this.listCurrentActivityRuntime(procRtGuid);
		if (SetUtils.isNullList(activityList))
		{
			return false;
		}
		Map<String, ActivityRuntime> activityGuidMap = new HashMap<String, ActivityRuntime>(activityList.size());
		for (ActivityRuntime actrt : activityList)
		{
			if (actrt.getName().equalsIgnoreCase(actrtName))
			{
				activityGuidMap.put(actrt.getGuid(), actrt);
			}
		}
		if (activityGuidMap.isEmpty())
		{
			return false;
		}
		return this.getPerformerStub().isPerformerOfActrtRuntime(procRtGuid, activityGuidMap);
	}
}
