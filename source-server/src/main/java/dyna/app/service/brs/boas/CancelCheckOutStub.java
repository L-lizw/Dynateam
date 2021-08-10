/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CancelCheckOutStub
 * Wanglei 2011-3-30
 */
package dyna.app.service.brs.boas;

import java.util.List;

import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RelationTemplateActionEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;
import dyna.net.service.brs.EMM;

/**
 * @author Wanglei
 * 
 */
public class CancelCheckOutStub extends AbstractServiceStub<BOASImpl>
{
	private static TrackerBuilder	updateUserTrackerBuilder	= null;

	/**
	 * @param context
	 * @param service
	 */
	public CancelCheckOutStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	public FoundationObject cancelCheckOutNoCascade(FoundationObject foundationObject) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		FoundationObject retFoundationObject = null;

		String sessionId = this.stubService.getSignature().getCredential();

		try
		{

			// 执行取消检出操作
			ds.cancelCheckout(foundationObject, Constants.isSupervisor(true, this.stubService), sessionId, this.stubService.getFixedTransactionId());

			// 20170301 去除再次查询
			// // 获取取消检出后的对象
			retFoundationObject = this.stubService.getObject(foundationObject.getObjectGuid());

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return retFoundationObject;
	}

	private TrackerBuilder getTrackerBuilder()
	{
		if (updateUserTrackerBuilder == null)
		{
			updateUserTrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			updateUserTrackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.CANCEL_CHECK_OUT);
			updateUserTrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return updateUserTrackerBuilder;
	}

	public FoundationObject cancelCheckOut(FoundationObject foundationObject, boolean isDealBom) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();
		ServiceRequestException returnObj = null;
		FoundationObject retFoundationObject = null;

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{

			// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 执行取消检出操作
			try
			{
				foundationObject = this.stubService.getFoundationStub().getObjectByGuid(foundationObject.getObjectGuid(), false);

				if (!foundationObject.isCheckOut())
				{
					throw new ServiceRequestException("ID_DS_CANCEL_CHECKOUT_DATA_LOST",
							"Cancel checkout fails, no permission or data is not checked out or has been modified, deleted");
				}

				ds.cancelCheckout(foundationObject, Constants.isSupervisor(true, this.stubService), sessionId, this.stubService.getFixedTransactionId());
			}
			catch (DynaDataException e)
			{
				e.printStackTrace();
				returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
				throw returnObj;
			}
			finally
			{
				Object[] args = new Object[] { foundationObject };
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
			}

			if (isDealBom)
			{
				List<BOMView> bomViewList = this.stubService.getBOMS().listBOMView(foundationObject.getObjectGuid());
				if (!SetUtils.isNullList(bomViewList))
				{
					for (BOMView bomView : bomViewList)
					{
						if (this.stubService.getOperatorGuid().equals(bomView.getCheckedOutUserGuid()))
						{
							this.stubService.getBOMS().cancelCheckOut(bomView);
						}
					}
				}
			}

			// delete file
			((DSSImpl) this.stubService.getDSS()).getFileInfoStub().deleteIterationFile(foundationObject.getObjectGuid(), foundationObject.getIterationId());

			EMM emm = this.stubService.getEMM();

			// 处理relation
			// 查找所有关联的ViewObject
			List<ViewObject> viewObjectList = this.stubService.listRelation(foundationObject.getObjectGuid());

			if (!SetUtils.isNullList(viewObjectList))
			{
				for (ViewObject viewObject : viewObjectList)
				{
					RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.get(ViewObject.TEMPLATE_ID) == null ? "" : (String) viewObject
							.get(ViewObject.TEMPLATE_ID));
					if (relationTemplate == null)
					{
						continue;
					}

					if (RelationTemplateActionEnum.NONE.equals(relationTemplate.getCheckinTrigger()))
					{
						// 不处理end2
						continue;
					}
					else if (RelationTemplateActionEnum.CANCELCHKOUT.equals(relationTemplate.getCancelChkoutTrigger()))
					{
						// 关联检入end2
						try
						{
							String structureClassName = relationTemplate.getStructureClassName();

							List<UIObjectInfo> uiObjectList = emm.listUIObjectInCurrentBizModel(structureClassName, UITypeEnum.FORM, true);
							SearchCondition searchCondition = null;
							if (!SetUtils.isNullList(uiObjectList))
							{
								searchCondition = SearchConditionFactory.createSearchConditionForStructure(structureClassName);
								for (UIObjectInfo uiObject : uiObjectList)
								{
									searchCondition.addResultUIObjectName(uiObject.getName());
								}
							}

							List<StructureObject> structureObjectList = this.stubService.listObjectOfRelation(viewObject.getObjectGuid(), searchCondition, null, null);
							if (!SetUtils.isNullList(structureObjectList))
							{
								for (StructureObject structureObject : structureObjectList)
								{
									try
									{
										FoundationObject end2FoundationObject = this.stubService.getObject(structureObject.getEnd2ObjectGuid());
										if (end2FoundationObject != null)
										{
											this.cancelCheckOut(end2FoundationObject, isDealBom);
										}
									}
									catch (Exception e)
									{
										continue;
									}
								}
							}
						}
						catch (Exception e)
						{
							continue;
						}
					}
				}

			}

			// DataServer.getTransactionManager().commitTransaction();
			// 获取取消检出后的对象
			retFoundationObject = this.stubService.getObject(foundationObject.getObjectGuid());

		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		finally
		{
		}

		return retFoundationObject;
	}
}
