/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FUpdaterStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.tracked.TRUpdateOwnerImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class FUpdaterStub extends AbstractServiceStub<BOASImpl>
{
	private static TrackerBuilder updateUserTrackerBuilder = null;

	public FoundationObject update(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, String revisionId, Date updatetime, LifecyclePhaseInfo fromLifeCyclePhase,
			LifecyclePhaseInfo toLifeCyclePhase, String rlsUserGuid, boolean isCheckAcl, SystemStatusEnum systemStatusEnum) throws ServiceRequestException
	{
		try
		{
			if (StringUtils.isNullString(objectGuid.getClassGuid()))
			{
				ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			}

			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
			boolean isView = false;
			if (classInfo != null && (classInfo.hasInterface(ModelInterfaceEnum.IBOMView) || classInfo.hasInterface(ModelInterfaceEnum.IViewObject)))
			{
				isView = true;
			}
			FoundationObject foundationObject = null;
			if (!isView)
			{
				foundationObject = this.stubService.getFoundationStub().getObject(objectGuid, false);
				if (!StringUtils.isNullString(ownerUserGuid) && !StringUtils.isNullString(ownerGroupGuid))
				{
					foundationObject.setOwnerUserGuid(ownerUserGuid);
					foundationObject.setOwnerGroupGuid(ownerGroupGuid);
				}
				if (!StringUtils.isNullString(revisionId))
				{
					foundationObject.setRevisionId(revisionId);
				}
				if (toLifeCyclePhase != null)
				{
					foundationObject.setLifecyclePhaseGuid(toLifeCyclePhase.getGuid());
				}
				if (systemStatusEnum != null)
				{
					foundationObject.setStatus(systemStatusEnum);
				}

				// invoke update.before event
				this.stubService.getEOSS().executeUpdateBeforeEvent(foundationObject);
			}
			String sessionId = this.stubService.getSignature().getCredential();

			DataServer.getInstanceService().save(objectGuid, ownerUserGuid, ownerGroupGuid, revisionId, updatetime, fromLifeCyclePhase, toLifeCyclePhase, rlsUserGuid,
					systemStatusEnum, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId());
			if (!isView)
			{
				foundationObject = this.stubService.getFoundationStub().getObject(objectGuid, false);
				// invoke add.after event.
				this.stubService.getEOSS().executeUpdateAfterEvent(foundationObject);

				return this.stubService.getFoundationStub().getObject(objectGuid, isCheckAcl);
			}
			else
			{
				return null;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public FoundationObject updateLifeCyclePhase(ObjectGuid objectGuid, Date updateTime, LifecyclePhaseInfo srcPhase, LifecyclePhaseInfo destPhase, boolean isCheckAuth)
			throws ServiceRequestException
	{
		this.update(objectGuid, null, null, null, updateTime, srcPhase, destPhase, null, isCheckAuth, null);

		return this.stubService.getObject(objectGuid);
	}

	public FoundationObject updateStatus(ObjectGuid objectGuid, Date updateTime, String rlsUserGuid, SystemStatusEnum statusEnum, boolean isCheckAuth)
			throws ServiceRequestException
	{
		this.update(objectGuid, null, null, null, updateTime, null, null, rlsUserGuid, isCheckAuth, statusEnum);

		return this.stubService.getObject(objectGuid);
	}

	public FoundationObject updateLifeCyclePhase(ObjectGuid objectGuid, String lifeCyclePhaseOriginal, String lifeCyclePhaseDest, Date updateTime) throws ServiceRequestException
	{

		EMM emm = this.stubService.getEMM();

		LifecyclePhaseInfo srcPhase = emm.getLifecyclePhaseInfo(lifeCyclePhaseOriginal);
		LifecyclePhaseInfo destPhase = emm.getLifecyclePhaseInfo(lifeCyclePhaseDest);

		return this.updateLifeCyclePhase(objectGuid, updateTime, srcPhase, destPhase, true);
	}

	private TrackerBuilder getTrackerBuilder()
	{
		if (updateUserTrackerBuilder == null)
		{
			updateUserTrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			updateUserTrackerBuilder.setTrackerRendererClass(TRUpdateOwnerImpl.class, TrackedDesc.UPDATE_OWNER);
			updateUserTrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return updateUserTrackerBuilder;
	}

	public FoundationObject updateOwnerUser(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime, boolean isCheckAuth, boolean containBomView)
			throws ServiceRequestException
	{
		FoundationObject foundation = this.stubService.getFoundationStub().getObjectByGuid(objectGuid, false);
		ServiceRequestException returnObj = null;
		FoundationObject newFoundation = null;
		try
		{
			this.update(objectGuid, ownerUserGuid, ownerGroupGuid, null, updateTime, null, null, null, isCheckAuth, null);

			List<ViewObject> relaList = this.stubService.listRelation(objectGuid);
			if (!SetUtils.isNullList(relaList))
			{
				for (ViewObject viewObject : relaList)
				{
					if (viewObject != null)
					{
						this.update(viewObject.getObjectGuid(), ownerUserGuid, ownerGroupGuid, null, viewObject.getUpdateTime(), null, null, null, isCheckAuth, null);
					}
				}
			}

			newFoundation = this.stubService.getFoundationStub().getObject(objectGuid, false);

			if (containBomView)
			{
				List<BOMView> listBOMView = this.stubService.getBOMS().listBOMView(objectGuid);
				if (!SetUtils.isNullList(listBOMView))
				{
					for (BOMView bomView : listBOMView)
					{
						this.stubService.getBOMS().updateBomViewOwner(bomView.getObjectGuid(), ownerUserGuid, ownerGroupGuid, bomView.getUpdateTime());
					}

				}
			}
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw returnObj;
		}
		finally
		{
			Object[] args = null;
			if (newFoundation != null)
			{
				args = new Object[] { foundation, foundation.getOwnerUser(), newFoundation.getOwnerUser() };

			}
			else
			{
				args = new Object[] { foundation, null, null };

			}
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}

		Group group = this.stubService.getAAS().getGroup(ownerGroupGuid);
		User user = this.stubService.getAAS().getUser(ownerUserGuid);

		LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

		String subject = this.stubService.getMSRM().getMSRString("ID_APP_UPDATE_OWNERUSER_SUBJECT", languageEnum.toString());

		// from user
		String content = this.stubService.getMSRM().getMSRString("ID_APP_UPDATE_OWNERUSER_FROMUSER_CONTENT", languageEnum.toString());
		if (content != null)
		{
			content = MessageFormat.format(content, newFoundation.getFullName(), foundation.getOwnerGroup(), foundation.getOwnerUser(), group.getGroupName(), user.getUserName());
		}
		this.stubService.getSMS().sendMailToUser(subject, content, MailCategoryEnum.INFO, null, foundation.getOwnerUserGuid(), MailMessageType.OWNERUPDATE);

		// to user
		content = this.stubService.getMSRM().getMSRString("ID_APP_UPDATE_OWNERUSER_TOUSER_CONTENT", languageEnum.toString());
		if (content != null)
		{
			content = MessageFormat.format(content, newFoundation.getFullName(), foundation.getOwnerGroup(), foundation.getOwnerUser(), group.getGroupName(), user.getUserName());
		}
		this.stubService.getSMS().sendMailToUser(subject, content, MailCategoryEnum.INFO, null, user.getGuid(), MailMessageType.OWNERUPDATE);

		return newFoundation;
	}

	public FoundationObject updateOwnerUserInLib(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime) throws ServiceRequestException
	{

		FoundationObject oriFoundationObject = this.stubService.getFoundationStub().getObject(objectGuid, false);

		// invoke update.before event
		this.stubService.getEOSS().executeUpdateBeforeEvent(oriFoundationObject);

		String sessionId = this.stubService.getSignature().getCredential();

		ServiceRequestException returnObj = null;
		FoundationObject foundationObject = null;
		try
		{
			DataServer.getInstanceService().saveOwner(objectGuid.getGuid(), objectGuid.getClassGuid(), ownerUserGuid, ownerGroupGuid, sessionId);

			foundationObject = this.stubService.getFoundationStub().getObject(objectGuid, false);

		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw returnObj;
		}
		finally
		{
			Object[] args;
			if (oriFoundationObject != null)
			{
				args = new Object[] { oriFoundationObject, oriFoundationObject.getOwnerUser(), foundationObject.getOwnerUser() };
			}
			else
			{
				args = new Object[] { oriFoundationObject, null, null };
			}
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}

		Group group = this.stubService.getAAS().getGroup(ownerGroupGuid);
		User user = this.stubService.getAAS().getUser(ownerUserGuid);

		LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

		String subject = this.stubService.getMSRM().getMSRString("ID_APP_UPDATE_OWNERUSER_SUBJECT", languageEnum.toString());

		// from user
		String content = this.stubService.getMSRM().getMSRString("ID_APP_UPDATE_OWNERUSER_FROMUSER_CONTENT", languageEnum.toString());
		if (content != null)
		{
			// 实例fullname，原所有者组，原所有者，现所有者组，现所有者
			content = MessageFormat.format(content, foundationObject.getFullName(), oriFoundationObject.getOwnerGroup(), oriFoundationObject.getOwnerUser(), group.getGroupName(),
					user.getUserName());
		}
		this.stubService.getSMS().sendMailToUser(subject, content, MailCategoryEnum.INFO, null, oriFoundationObject.getOwnerUserGuid(), MailMessageType.OWNERUPDATE);

		// to user
		content = this.stubService.getMSRM().getMSRString("ID_APP_UPDATE_OWNERUSER_TOUSER_CONTENT", languageEnum.toString());
		if (content != null)
		{
			content = MessageFormat.format(content, foundationObject.getFullName(), oriFoundationObject.getOwnerGroup(), oriFoundationObject.getOwnerUser(), group.getGroupName(),
					user.getUserName());
		}
		this.stubService.getSMS().sendMailToUser(subject, content, MailCategoryEnum.INFO, null, user.getGuid(), MailMessageType.OWNERUPDATE);

		// invoke add.after event.
		this.stubService.getEOSS().executeUpdateAfterEvent(foundationObject);

		return this.stubService.getFoundationStub().getObject(objectGuid, false);

	}

	protected FoundationObject updateRevisionId(ObjectGuid objectGuid, String revisionId, Date updateTime) throws ServiceRequestException
	{
		this.update(objectGuid, null, null, revisionId, updateTime, null, null, null, true, null);

		return this.stubService.getObject(objectGuid);
	}

}
