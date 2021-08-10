package dyna.app.service.brs.ppms.timertask;

import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.ppms.PPMSImpl;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.util.SetUtils;
import dyna.net.service.brs.PPMS;

public class ProjectCalculateScheduled extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;

	public void calculateProjectInfo(final PPMS ppms) throws ServiceRequestException
	{
		this.calculateProjectSPI(ppms);
		this.calculateWorkItem(ppms);
	}

	/**
	 * 工作项定时任务
	 * 
	 * @param ppms
	 * @throws ServiceRequestException
	 */
	private void calculateWorkItem(PPMS ppms) throws ServiceRequestException
	{
		BOInfo pmWorkBoInfo = ppms.getWorkItemBoinfo();
		if (pmWorkBoInfo == null)
		{
			return;
		}
		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(pmWorkBoInfo.getClassName(), null, true);
		searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
		searchCondition.setPageNum(1);
		List<FoundationObject> listWork = ppms.listWorkItem(searchCondition);
		if (!SetUtils.isNullList(listWork))
		{
			int size = listWork.get(0).getRowCount();
			size = size / SearchCondition.MAX_PAGE_SIZE;
			for (int i = 1; i <= size + 1; i++)
			{
				if (i > 1)
				{
					searchCondition.setPageNum(i);
					listWork = ppms.listWorkItem(searchCondition);
				}
				if (!SetUtils.isNullList(listWork))
				{
					for (FoundationObject work : listWork)
					{
						PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(work);
						((PPMSImpl) ppms).getWorkItemStub().calculateSPIAndDuration(util);
						((PPMSImpl) ppms).getProjectStub().saveObject(work, false, false);
					}
				}
			}
		}
	}

	/**
	 * 计算项目信息
	 * 项目定时任务
	 * 
	 * @param ppms
	 * @throws ServiceRequestException
	 */
	private void calculateProjectSPI(PPMS ppms) throws ServiceRequestException
	{
		BOInfo pmProjectBoInfo = ppms.getPMProjectBoInfo();
		if (pmProjectBoInfo == null)
		{
			return;
		}
		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(pmProjectBoInfo.getClassName(), null, true);
		searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		searchCondition.setPageNum(1);
		searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
		List<FoundationObject> listProject = ppms.listProject(searchCondition);

		if (!SetUtils.isNullList(listProject))
		{
			int size = listProject.get(0).getRowCount();
			size = size / SearchCondition.MAX_PAGE_SIZE;
			for (int i = 1; i <= size + 1; i++)
			{
				if (i > 1)
				{
					searchCondition.setPageNum(i);
					listProject = ppms.listProject(searchCondition);
				}
				if (!SetUtils.isNullList(listProject))
				{
					for (FoundationObject project : listProject)
					{
						ppms.calculateProjectInfo(project);
					}
				}
			}

		}
	}

	@Override
	public void run()
	{
		DynaLogger.info("PPMS Scheduled [Class]ProjectCalculateScheduled , Scheduled Task Start...");
		PPMS ppms = null;
		try
		{
			ppms = this.serviceContext.allocatService(PPMS.class);
			((PPMSImpl) ppms).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			this.calculateProjectInfo(ppms);
		}
		catch (Throwable e)
		{
			DynaLogger.error("run ProjectCalculateScheduled:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (ppms != null)
			{
				this.serviceContext.releaseService(ppms);
			}
		}
		DynaLogger.info("PPMS Scheduled [Class]ProjectCalculateScheduled , Scheduled Task End...");
	}

	public ProjectCalculateScheduled(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}
}
