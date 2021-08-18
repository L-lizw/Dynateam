/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与FoundationObject-Iteration相关的基本操作分支
 * Caogc 2010-8-19
 */
package dyna.app.service.brs.boas;

import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 与FoundationObject-Iteration相关的基本操作分支
 * 
 * @author Caogc
 * 
 */
@Component
public class IterationStub extends AbstractServiceStub<BOASImpl>
{
	private static TrackerBuilder	trackerBuilder	= null;
	ServiceRequestException			returnObj		= null;

	public List<FoundationObject> listObjectIteration(ObjectGuid objectGuid, Integer iterationId, boolean isNeedAuthority) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);

			// get ui model object for this object.
			List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.FORM, true);
			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			searchCondition.setPageNum(1);
			searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			List<FoundationObject> results = DataServer.getInstanceService().listIteration(objectGuid, iterationId, searchCondition,
					Constants.isSupervisor(isNeedAuthority, this.stubService), sessionId);

			if (SetUtils.isNullList(results))
			{
				return null;
			}

			for (FoundationObject foundation : results)
			{
				this.stubService.getClassificationStub().makeClassificationFoundation(foundation, searchCondition, bmGuid, String.valueOf(foundation.getIterationId()));
			}

			EMM emm = this.stubService.getEMM();

			Set<String> fieldNames = emm.getObjectFieldNamesInSC(searchCondition);
			Set<String> fieldCodeNames = emm.getCodeFieldNamesInSC(searchCondition);
			Folder folder = searchCondition.getFolder();
			for (FoundationObject fObject : results)
			{
				DecoratorFactory.decorateFoundationObject(fieldNames, fObject, emm, bmGuid, folder);
				DecoratorFactory.decorateFoundationObjectCode(fieldCodeNames, fObject, emm, bmGuid);
				fObject.put("$IterationFoGuid$", objectGuid.getGuid());
			}
			DecoratorFactory.decorateFoundationObject(fieldNames, results, this.stubService.getEMM(), sessionId);

			return results;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	public void rollbackObjectIteration(ObjectGuid objectGuid, Integer iterationId) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		FoundationObject object = null;
		try
		{
			object = this.stubService.getFoundationStub().getObject(objectGuid, false);

			DataServer.getInstanceService().rollbackIteration(objectGuid, iterationId, false, sessionId);

			// delete file
			((DSSImpl) this.stubService.getDSS()).getFileInfoStub().rollbackFile(objectGuid, object.getIterationId(), iterationId);
		}
		catch (ServiceRequestException e)
		{
			this.returnObj = e;
			throw this.returnObj;
		}
		finally
		{
			Object[] args = new Object[] { object };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, this.returnObj));
		}

	}

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			trackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.ROLLBACK_ITR);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

}