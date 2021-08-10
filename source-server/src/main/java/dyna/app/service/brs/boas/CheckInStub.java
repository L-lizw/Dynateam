/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与checkIn/checkOut相关的操作分支
 * Caogc 2010-8-18
 */
package dyna.app.service.brs.boas;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
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
 * 与checkIn相关的操作分支
 * 
 * @author Caogc
 * 
 */
public class CheckInStub extends AbstractServiceStub<BOASImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public CheckInStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	public FoundationObject checkIn(FoundationObject foundationObject, boolean isDealBom, boolean isCheckAuth) throws ServiceRequestException
	{
		// DataService ds = ServerFactory.getDataService();

		FoundationObject retFoundationObject = null;

		// String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 执行检入操作
			retFoundationObject = this.checkInNoCascade(foundationObject, isCheckAuth);

			if (isDealBom)
			{
				List<BOMView> bomViewList = this.stubService.getBOMS().listBOMView(retFoundationObject.getObjectGuid());
				if (!SetUtils.isNullList(bomViewList))
				{
					for (BOMView bomView : bomViewList)
					{
						if (this.stubService.getOperatorGuid().equals(bomView.getCheckedOutUserGuid()))
						{
							this.stubService.getBOMS().checkIn(bomView);
						}
					}
				}
			}

			EMM emm = this.stubService.getEMM();

			// 处理relation
			// 查找所有关联的ViewObject
			List<RelationTemplateInfo> relationTemplates = this.stubService.getEMM().listRelationTemplate(retFoundationObject.getObjectGuid());
			if (!SetUtils.isNullList(relationTemplates))
			{
				for (RelationTemplateInfo relationTemplate : relationTemplates)
				{
					if (relationTemplate == null)
					{
						continue;
					}

					if (RelationTemplateActionEnum.NONE.equals(relationTemplate.getCheckinTrigger()))
					{
						// 不处理end2
						continue;
					}
					else if (RelationTemplateActionEnum.CHECKIN.equals(relationTemplate.getCheckinTrigger()))
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

							ViewObject viewObject = this.stubService.getRelationByEND1(retFoundationObject.getObjectGuid(), relationTemplate.getName());
							if (viewObject == null)
							{
								continue;
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
											this.checkIn(end2FoundationObject, isDealBom, isCheckAuth);
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
			if (retFoundationObject.isChanged())
			{
				retFoundationObject = this.stubService.getFSaverStub().saveObject(retFoundationObject, false, false, false, false, null, false, false, false, false);
			}
		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}

		return retFoundationObject;
	}

	protected FoundationObject checkInNoCascade(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.checkInNoCascade(foundationObject, true);
	}

	public FoundationObject checkInNoCascade(FoundationObject foundationObject, boolean isCheckAuth) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		FoundationObject retFoundationObject = null;

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			// invoke checkin.before event script
			this.stubService.getEOSS().executeCheckInBeforeEvent(foundationObject);

			// 执行检入操作
			// ds.checkin(foundationObject, Constants.isSupervisor(isCheckAuth, this.stubService),
			// Constants.IS_OWN_ONLY,
			// sessionId);

			retFoundationObject = ds.checkin(foundationObject, Constants.IS_OWN_ONLY, sessionId, this.stubService.getFixedTransactionId());

			ObjectGuid objectGuid = retFoundationObject.getObjectGuid();

			this.stubService.getFTS().createTransformQueue4Checkin(objectGuid);


			// invoke checkin.after event script
			this.stubService.getEOSS().executeCheckInAfterEvent(retFoundationObject);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return retFoundationObject;
	}
}
