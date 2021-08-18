/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationUnlinkStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRViewLinkImpl;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EOSS;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class RelationUnlinkStub extends AbstractServiceStub<BOASImpl>
{
	private static TrackerBuilder trackerBuilder = null;

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);

			trackerBuilder.setTrackerRendererClass(TRViewLinkImpl.class, TrackedDesc.UNLINK_RELATION);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

	/**
	 * 解除关联
	 * 
	 * @param end1ObjectGuid
	 * @param templateName
	 * @param end2FoundationObjectGuid
	 * @author caogc
	 */
	public void unlink(ObjectGuid end1ObjectGuid, String templateName, ObjectGuid end2FoundationObjectGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		ViewObject viewObject = this.stubService.getRelationStub().getRelationByEND1(end1ObjectGuid, templateName, isCheckAcl);
		if (viewObject == null)
		{
			throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
		}

		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());
		if (relationTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
		}

		try
		{

			String end2Guid = end2FoundationObjectGuid.getGuid();
			end2FoundationObjectGuid.setGuid(null);

			List<StructureObject> structureList = this.stubService.getStructureStub().listStructureObject(viewObject.getObjectGuid(), end2FoundationObjectGuid, isCheckAcl);

			if (SetUtils.isNullList(structureList))
			{
				return;
			}

			boolean isdelete = false;
			for (StructureObject structure : structureList)
			{
				if (StringUtils.isGuid(end2Guid) && end2Guid.equalsIgnoreCase(structure.getEnd2ObjectGuid().getGuid()))
				{
					structure.resetObjectGuid();

					this.unlink(structure, Constants.isSupervisor(isCheckAcl, this.stubService));
					isdelete = true;
					break;
				}
			}

			if (!isdelete)
			{
				for (StructureObject structure : structureList)
				{
					structure.resetObjectGuid();

					this.unlink(structure, Constants.isSupervisor(isCheckAcl, this.stubService));
					break;
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 解除关联
	 * 
	 * @param viewObjectGuid
	 * @param end2FoundationObjectGuid
	 * @author caogc
	 */
	public void unlink(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, boolean isCheckACL) throws ServiceRequestException
	{
		try
		{
			String end2Guid = end2FoundationObjectGuid.getGuid();
			end2FoundationObjectGuid.setGuid(null);
			List<StructureObject> structureList = this.stubService.getStructureStub().listStructureObject(viewObjectGuid, end2FoundationObjectGuid, false);

			if (SetUtils.isNullList(structureList))
			{
				return;
			}
			boolean isdelete = false;
			for (StructureObject structure : structureList)
			{
				if (end2Guid.equalsIgnoreCase(structure.getEnd2ObjectGuid().getGuid()))
				{
					structure.resetObjectGuid();

					this.unlink(structure, Constants.isSupervisor(isCheckACL, this.stubService));
					isdelete = true;
					break;
				}
			}

			if (!isdelete)
			{
				for (StructureObject structure : structureList)
				{
					structure.resetObjectGuid();

					this.unlink(structure, Constants.isSupervisor(isCheckACL, this.stubService));
					break;
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 根据structure解除关联关系
	 * 
	 * @param structureObject
	 * @param boas
	 * @throws ServiceRequestException
	 */
	protected void unlink(StructureObject structureObject) throws ServiceRequestException
	{
		this.unlink(structureObject, true);
	}

	/**
	 * 根据structure解除关联关系,并且删除end2
	 * 
	 * @param structureObject
	 * @param boas
	 * @throws ServiceRequestException
	 */
	public void unlinkAndDeleteEnd2(StructureObject structureObject, boolean isCheckACL) throws ServiceRequestException
	{

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			this.unlink(structureObject, Constants.isSupervisor(isCheckACL, this.stubService));

			this.stubService.getFoundationStub().deleteFoundationObject(structureObject.getEnd2ObjectGuid().getGuid(), structureObject.getEnd2ObjectGuid().getClassGuid(),
					Constants.isSupervisor(isCheckACL, this.stubService));

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 根据structure解除关联关系
	 * 
	 * @param structureObject
	 * @param boas
	 * @throws ServiceRequestException
	 */
	public void unlink(StructureObject structureObject, boolean isCheckAcl) throws ServiceRequestException
	{
		ViewObject viewObject = this.stubService.getRelationStub().getRelation(structureObject.getViewObjectGuid(), isCheckAcl);
		this.unlinkInner(viewObject, structureObject, isCheckAcl);
	}

	/**
	 * 根据structure解除关联关系
	 * 
	 * @param structureObject
	 * @param boas
	 * @throws ServiceRequestException
	 */
	public void unlinkInner(ViewObject viewObject, StructureObject structureObject, boolean isCheckAcl) throws ServiceRequestException
	{
		ServiceRequestException returnObj = null;
		String sessionId = this.stubService.getSignature().getCredential();
		String relationTemplateGuid = null;
		ObjectGuid end1ObjectGuid = null;
		try
		{
			EOSS eoss = this.stubService.getEOSS();

			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}
			end1ObjectGuid = viewObject.getEnd1ObjectGuid();

			if (SystemStatusEnum.PRE.equals(viewObject.getStatus()) && isCheckAcl)
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			if (SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			// DCR规则检查
			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());
			relationTemplateGuid = relationTemplate.getGuid();
			List<ObjectGuid> end2ObjectGuidList = new ArrayList<>();
			end2ObjectGuidList.add(structureObject.getEnd2ObjectGuid());
			viewObject.getTemplateID();
			this.stubService.getDCR().check(viewObject.getEnd1ObjectGuid(), end2ObjectGuidList, relationTemplate.getName(), RuleTypeEnum.RELATION);

			// !invoke unlink.before
			eoss.executeDeleteBeforeEvent(structureObject);

			DataServer.getRelationService().delete(structureObject, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId);

			// !invoke unlink.after
			eoss.executeDeleteAfterEvent(structureObject);
		}
		catch (DynaDataException e)
		{
			returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);

			throw returnObj;
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw returnObj;
		}
		finally
		{
			Object[] args = new Object[] { viewObject, structureObject.getEnd2ObjectGuid() };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
			if (end1ObjectGuid != null && StringUtils.isGuid(relationTemplateGuid))
			{
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new UpdateScheduledTask(this.serviceContext, end1ObjectGuid, relationTemplateGuid));
			}
		}
	}

	protected void batchUnlink(List<StructureObject> structureObjectList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(structureObjectList))
		{
			return;
		}

		for (StructureObject structureObject : structureObjectList)
		{
			this.unlink(structureObject);
		}
	}

}
