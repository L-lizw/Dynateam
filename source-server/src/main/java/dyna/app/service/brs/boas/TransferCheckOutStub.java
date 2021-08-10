/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TransferCheckOutStub
 * Wanglei 2011-3-30
 */
package dyna.app.service.brs.boas;

import java.util.Arrays;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.bom.BOMSImpl;
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
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.RelationTemplateActionEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.MSRM;

/**
 * @author Wanglei
 * 
 */
public class TransferCheckOutStub extends AbstractServiceStub<BOASImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public TransferCheckOutStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	private synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.stubService.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	private synchronized SMS getSMS() throws ServiceRequestException
	{
		try
		{
			return this.stubService.getRefService(SMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/**
	 * 检出状态迁移 _把一个对象的检出者由检出者本人迁移给另外一个人 递归处理end2
	 * 
	 * @param foundationObject
	 *            要迁移的对象
	 * @param toUserGuid
	 *            要迁移给的用户
	 * @param boas
	 *            BOASImpl服务
	 * @return 返回迁移后的对象
	 * @throws ServiceRequestException
	 */
	public FoundationObject transferCheckout(FoundationObject foundationObject, String toUserGuid, String locale, boolean isCheckAuth, boolean isDealBom)
			throws ServiceRequestException
	{
		FoundationObject retFoundationObject = null;

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{

			// 执行迁移检出操作
			retFoundationObject = DataServer.getInstanceService().transferCheckout(foundationObject, toUserGuid, Constants.isSupervisor(isCheckAuth, this.stubService), true,
					sessionId, this.stubService.getFixedTransactionId());

			if (isDealBom)
			{
				List<BOMView> bomViewList = this.stubService.getBOMS().listBOMView(retFoundationObject.getObjectGuid());
				if (!SetUtils.isNullList(bomViewList))
				{
					for (BOMView bomView : bomViewList)
					{
						if (bomView.getCheckedOutUserGuid() == null || "".equals(bomView.getCheckedOutUserGuid()))
						{
							((BOMSImpl) this.stubService.getBOMS()).getBOMViewCheckOutStub().checkOut(bomView, toUserGuid, true);
						}
						else if (this.stubService.getOperatorGuid().equals(bomView.getCheckedOutUserGuid()))
						{
							this.stubService.getBOMS().transferCheckout(bomView, toUserGuid);
						}
					}
				}
			}

			SMS sms = this.getSMS();
			MSRM msrm = this.getMSRM();
			EMM emm = this.stubService.getEMM();

			List<ObjectGuid> objectGuidList = Arrays.asList(retFoundationObject.getObjectGuid());
			sms.sendMailToUser(msrm.getMSRString("ID_WEB_TRANSFERCHECKED_MAIL_SUBJECT", locale), msrm.getMSRString("ID_WEB_TRANSFERCHECKED_MAIL_CONTENT", locale),
					MailCategoryEnum.INFO, objectGuidList, toUserGuid, MailMessageType.TRANSFERCHECKOUT);
			// 处理relation
			// 查找所有关联的ViewObject
			List<ViewObject> viewObjectList = this.stubService.listRelation(retFoundationObject.getObjectGuid());

			if (!SetUtils.isNullList(viewObjectList))
			{
				for (ViewObject viewObject : viewObjectList)
				{
					RelationTemplateInfo relationTemplate = this.stubService.getEMM()
							.getRelationTemplateById(viewObject.get(ViewObject.TEMPLATE_ID) == null ? "" : (String) viewObject.get(ViewObject.TEMPLATE_ID));
					if (relationTemplate == null)
					{
						continue;
					}

					if (RelationTemplateActionEnum.TRASCHKOUT.equals(relationTemplate.getTraschkoutTrigger()))
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
											this.transferCheckout(end2FoundationObject, toUserGuid, locale, isCheckAuth, isDealBom);
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

			// this.stubService.getTransactionService().commitTransaction();

			// 20170301 去除再次查询

			// 获取迁移检出后的对象
			// retFoundationObject = this.stubService.getObject(foundationObject.getObjectGuid());

		}
		catch (DynaDataException e)
		{
			// this.stubService.getTransactionService().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
			// this.stubService.getTransactionService().rollbackTransaction();
			throw e;
		}
		finally
		{
		}

		return retFoundationObject;
	}

}
