/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRProcessImpl
 * Wanglei 2011-11-21
 */
package dyna.app.service.brs.wfi.track;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.impl.DefaultTrackerRendererImpl;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.bean.model.wf.template.WorkflowTemplateVo;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.WFI;

/**
 * @author Wanglei
 * 
 */
public class TRProcessImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		WFI wfe = null;
		try
		{

			if ("createProcess".equalsIgnoreCase(tracker.getMethod().getName()))
			{
				wfe = tracker.getServiceContext().allocatService(WFI.class);
				((WFIImpl) wfe).setSignature(tracker.getServiceContext().getServerContext().getSystemInternalSignature());

				WorkflowTemplateVo workflowTemplateVo = wfe.getWorkflowTemplateDetail((String) tracker.getParameters()[0]);
				if (workflowTemplateVo != null)
				{
					String title = workflowTemplateVo.getTemplate().getWorkflowTemplateInfo()
							.getTitle(tracker.getServiceContext().getServerContext().getSystemInternalSignature().getLanguageEnum());
					if (tracker.getResult() != null && tracker.getResult() instanceof ProcessRuntime)
					{
						title = StringUtils.convertNULLtoString(title) + "|" + StringUtils.convertNULLtoString(((ProcessRuntime) tracker.getResult()).getDescription());
					}
					return title;
				}
			}
			else if ("recallProcessRuntime".equalsIgnoreCase(tracker.getMethod().getName()))
			{
				wfe = tracker.getServiceContext().allocatService(WFI.class);
				((WFIImpl) wfe).setSignature(tracker.getServiceContext().getServerContext().getSystemInternalSignature());

				ProcessRuntime processRuntime = wfe.getProcessRuntime((String) tracker.getParameters()[0]);
				return StringUtils
						.convertNULLtoString(processRuntime.getWFTemplateTitle(tracker.getServiceContext().getServerContext().getSystemInternalSignature().getLanguageEnum())) + "|"
						+ StringUtils.convertNULLtoString(processRuntime.getDescription());
			}
			else if ("resumeProcess".equalsIgnoreCase(tracker.getMethod().getName()))
			{
				wfe = tracker.getServiceContext().allocatService(WFI.class);
				((WFIImpl) wfe).setSignature(tracker.getServiceContext().getServerContext().getSystemInternalSignature());

				ProcessRuntime processRuntime = wfe.getProcessRuntime((String) tracker.getParameters()[0]);

				return StringUtils
						.convertNULLtoString(processRuntime.getWFTemplateTitle(tracker.getServiceContext().getServerContext().getSystemInternalSignature().getLanguageEnum())) + "|"
						+ StringUtils.convertNULLtoString(processRuntime.getDescription());
			}
			else if ("deleteProcess".equalsIgnoreCase(tracker.getMethod().getName()))
			{
				ProcessRuntime processRuntime = (ProcessRuntime) tracker.getParameters()[0];
				if (processRuntime != null)
				{
					// ProcessRuntime processRuntime = wfe.getProcessRuntime((String) tracker.getParameters()[0]);
					return StringUtils
							.convertNULLtoString(processRuntime.getWFTemplateTitle(tracker.getServiceContext().getServerContext().getSystemInternalSignature().getLanguageEnum()))
							+ "|" + StringUtils.convertNULLtoString(processRuntime.getDescription());
				}
			}

		}
		catch (ServiceNotFoundException e)
		{
			DynaLogger.info("write log error:" + e.getMessage());
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (wfe != null)
			{
				tracker.getServiceContext().releaseService(wfe);
			}
		}

		return super.getHandledObject(tracker);
	}
}
