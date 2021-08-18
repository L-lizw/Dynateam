/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMViewStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.bom;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.brm.BRMImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.ECConstants;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.service.acl.AclService;
import dyna.data.service.ins.InstanceService;
import dyna.data.service.relation.RelationService;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.EMM;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Wanglei
 * 
 */
@Component
public class BOMViewStub extends AbstractServiceStub<BOMSImpl>
{
	private static TrackerBuilder	trackerBuilder		= null;
	private static TrackerBuilder	delTrackerBuilder	= null;

	private BOMView decorateViewByTemplate(BOMView viewObject) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) viewObject.get(ViewObject.TEMPLATE_ID));
		if (bomTemplate != null)
		{
			viewObject.setStructureClassName(bomTemplate.getStructureClassName());
			viewObject.setStructureClass(bomTemplate.getStructureClassGuid());
			viewObject.setTemplateTitle(bomTemplate.getTitle());
		}

		return viewObject;
	}

	public void deleteBOMView(BOMView bomView, boolean isCheckACL) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();
		ServiceRequestException returnObj = null;
		String sessionId = this.stubService.getSignature().getCredential();
		FoundationObject end1 = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			List<BOMStructure> listBOM = this.stubService.listBOM(bomView.getObjectGuid(), null, null, null);
			if (!SetUtils.isNullList(listBOM))
			{
				for (BOMStructure bomStructure : listBOM)
				{
					// 处理取替代，并且设置bomStructure上取替代的标识
					((BRMImpl) this.stubService.getBRM()).getReplaceObjectStub().dealWithHistoryReplaceData(null, null, bomStructure, bomView.getName(), true, true);
				}
			}
			ds.delete(bomView, Constants.isSupervisor(isCheckACL, this.stubService), sessionId, this.stubService.getFixedTransactionId());
//			DataServer.getTransactionManager().commitTransaction();
			end1 = this.stubService.getBOAS().getObject(bomView.getEnd1ObjectGuid());
			// 更新end1的BOM状态
			Object[] args = new Object[] { bomView };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getDelBOMTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));

			String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
			ClassStub.decorateObjectGuid(bomView.getObjectGuid(), this.stubService);
			List<FoundationObject> end1List = new ArrayList<FoundationObject>();
			end1List.add(end1);

			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new DeleteScheduledTask(serviceContext, bomView.getObjectGuid(), end1List, bmGuid, bomView.getName()));
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
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

	public List<BOMView> listBOMViewByGuid(List<ObjectGuid> objectGuidList, boolean isCheckAuth) throws ServiceRequestException
	{
		if (SetUtils.isNullList(objectGuidList))
		{
			return null;
		}

		ObjectGuid tmpObjectGuid = objectGuidList.get(0);
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		ClassStub.decorateObjectGuid(tmpObjectGuid, this.stubService);

		List<BOMView> resultList = new ArrayList<BOMView>();

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			InstanceService ds = DataServer.getInstanceService();

			int mod = (int) Math.ceil((double) objectGuidList.size() / (double) 50);
			for (int i = 0; i < mod; i++)
			{
				SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(tmpObjectGuid.getClassName(), null, false);

				// get ui model object for this object.
				List<UIObjectInfo> uiObjectList = null;
				try
				{
					uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(tmpObjectGuid.getClassName(), UITypeEnum.FORM, true);
				}
				catch (ServiceRequestException e)
				{
				}

				if (!SetUtils.isNullList(uiObjectList))
				{
					for (UIObjectInfo uiObject : uiObjectList)
					{
						searchCondition.addResultUIObjectName(uiObject.getName());
					}
				}

				searchCondition.setPageNum(1);
				searchCondition.setPageSize(50);
				searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

				searchCondition.addResultField(ViewObject.ISPRECISE);
				searchCondition.addResultField(ViewObject.END1);
				searchCondition.addResultField(ViewObject.TEMPLATE_ID);

				int endIndex = (i + 1) * 50;
				if (objectGuidList.size() < endIndex)
				{
					endIndex = objectGuidList.size();
				}
				for (int j = i * 50; j < endIndex; j++)
				{
					String guid = objectGuidList.get(j).getGuid();
					searchCondition.addFilterWithOR(SystemClassFieldEnum.GUID.getName(), guid, OperateSignEnum.EQUALS);
				}
				List<FoundationObject> results = ds.query(searchCondition, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);

				Set<String> fieldNames = this.stubService.getEMM().getObjectFieldNamesInSC(searchCondition);
				DecoratorFactory.decorateFoundationObject(fieldNames, results, this.stubService.getEMM(), sessionId);

				if (!SetUtils.isNullList(results))
				{
					for (FoundationObject result : results)
					{
						BOMView retObject = new BOMView(result);

						retObject = this.decorateViewByTemplate(retObject);

						EMM emm = this.stubService.getEMM();
						DecoratorFactory.decorateViewObject(retObject, emm, bmGuid);

						resultList.add(retObject);
					}
				}
			}
			return resultList;
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

	public BOMView getBOMView(ObjectGuid objectGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

		String tempGuid = objectGuid.getGuid();
		try
		{
			InstanceService ds = DataServer.getInstanceService();

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);

			// get ui model object for this object.
			List<UIObjectInfo> uiObjectList = null;
			try
			{
				uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.FORM, true);
			}
			catch (ServiceRequestException e)
			{
			}

			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			// searchCondition.setPageStartRowIdx(0);
			// searchCondition.setPageForward(true);
			searchCondition.setPageNum(1);
			searchCondition.setPageSize(1);
			// searchCondition.setLatestOnly(false);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			searchCondition.addResultField(ViewObject.ISPRECISE);
			searchCondition.addResultField(ViewObject.END1);
			searchCondition.addResultField(ViewObject.TEMPLATE_ID);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			String sessionId = this.stubService.getSignature().getCredential();

			FoundationObject foundationObject = ds.getFoundationObject(objectGuid, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);
			if (foundationObject == null)
			{
				return null;
			}

			Set<String> fieldNames = this.stubService.getEMM().getObjectFieldNamesInSC(searchCondition);
			DecoratorFactory.ofd.decorateWithField(fieldNames, foundationObject, this.stubService.getEMM(), sessionId, false);

			BOMView retObject = new BOMView(foundationObject);

			retObject = this.decorateViewByTemplate(retObject);

			EMM emm = this.stubService.getEMM();
			DecoratorFactory.decorateViewObject(retObject, emm, bmGuid);

			return retObject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		finally
		{
			objectGuid.setGuid(tempGuid);
		}
	}

	public BOMView getBOMViewByEND1(ObjectGuid end1ObjectGuid, String viewName, boolean isCheckAuth) throws ServiceRequestException
	{
		try
		{
			String sessionId = this.stubService.getSignature().getCredential();
			EMM emm = this.stubService.getEMM();
			InstanceService ds = DataServer.getInstanceService();

			BOMTemplateInfo bomTemp = this.stubService.getEMM().getBOMTemplateByName(end1ObjectGuid, viewName);
			if (bomTemp == null)
			{
				return null;
			}
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(bomTemp.getViewClassName(), null, false);
			// bom view 存在于revision上
			end1ObjectGuid.setIsMaster(false);
			end1ObjectGuid.setGuid(end1ObjectGuid.getGuid().toUpperCase());
			searchCondition.addFilter(ViewObject.END1, end1ObjectGuid, OperateSignEnum.EQUALS);
			searchCondition.addFilter(SystemClassFieldEnum.NAME, viewName, OperateSignEnum.EQUALS);
			searchCondition.setPageSize(1);
			// searchCondition.setLatestOnly(false);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			searchCondition.addResultField(ViewObject.ISPRECISE);
			searchCondition.addResultField(ViewObject.END1);
			searchCondition.addResultField(ViewObject.TEMPLATE_ID);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);

			List<FoundationObject> foundationObjectList = ds.query(searchCondition, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);
			if (SetUtils.isNullList(foundationObjectList))
			{
				return null;
			}

			FoundationObject foundationObject = foundationObjectList.get(0);

			Set<String> fieldNames = this.stubService.getEMM().getObjectFieldNamesInSC(searchCondition);
			DecoratorFactory.ofd.decorateWithField(fieldNames, foundationObject, this.stubService.getEMM(), sessionId, false);

			BOMView bomViewObject = new BOMView(foundationObject);

			bomViewObject = this.decorateViewByTemplate(bomViewObject);

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			DecoratorFactory.decorateViewObject(bomViewObject, emm, bmGuid);

			return bomViewObject;
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

	public List<BOMView> listBOMView(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.listBOMView(end1ObjectGuid, true);
	}

	public List<BOMView> listBOMView(ObjectGuid end1ObjectGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		List<BOMView> bomViewList = null;
		List<FoundationObject> foundationObjectList = null;
		try
		{
			InstanceService ds = DataServer.getInstanceService();

			EMM emm = this.stubService.getEMM();

			// bom view 存在于revision上
			end1ObjectGuid.setIsMaster(false);

			List<ClassInfo> bomViewClassList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IBOMView);
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(bomViewClassList.get(0).getName(), null, false);
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);
			searchCondition.addFilter(ViewObject.END1, end1ObjectGuid, OperateSignEnum.EQUALS);
			searchCondition.addOrder(SystemClassFieldEnum.NAME, true);
			searchCondition.setPageSize(1000);
			searchCondition.addResultField(ViewObject.ISPRECISE);
			searchCondition.addResultField(ViewObject.TEMPLATE_ID);
			searchCondition.addResultField(ViewObject.END1);

			String sessionId = this.stubService.getSignature().getCredential();
			foundationObjectList = ds.query(searchCondition, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);

			Set<String> fieldNames = emm.getObjectFieldNamesInSC(searchCondition);
			if (SetUtils.isNullList(foundationObjectList))
			{
				return null;
			}

			DecoratorFactory.decorateFoundationObject(fieldNames, foundationObjectList, emm, sessionId);

			bomViewList = new ArrayList<BOMView>();
			for (FoundationObject foundationObject : foundationObjectList)
			{
				BOMView bomView = new BOMView(foundationObject);
				bomView = this.decorateViewByTemplate(bomView);
				DecoratorFactory.decorateViewObject(bomView, emm, bmGuid);
				bomViewList.add(bomView);
			}

			return bomViewList;
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

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			trackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.SAVE_BOMVIEW);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

	private TrackerBuilder getDelBOMTrackerBuilder()
	{
		if (delTrackerBuilder == null)
		{
			delTrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			delTrackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.DEL_BOMVIEW);
			delTrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return delTrackerBuilder;
	}

	public BOMView saveBOMView(BOMView bomView, boolean isCheckAcl, String procRtGuid) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();
		ServiceRequestException returnObj = null;
		BOMView retBOMView = null;
		String sessionId = this.stubService.getSignature().getCredential();

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(bomView);

		try
		{
			ObjectGuid objectGuid = bomView.getObjectGuid();

			if (objectGuid.getGuid() != null)
			{
				if (bomView.getStatus() != null && !(SystemStatusEnum.WIP.equals(bomView.getStatus()) || SystemStatusEnum.ECP.equals(bomView.getStatus())))
				{
					throw new ServiceRequestException("ID_APP_STATUS_CANNT_EDIT", "only wip/ecp status can edit");
				}

				// 不支持更新精确非精确属性，因为精确非精确属性修改时需要做一些特殊处理
				ds.save(bomView, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId(), true);
				retBOMView = this.getBOMView(objectGuid, isCheckAcl);
			}
			else
			{

				bomView.setOwnerUserGuid(this.stubService.getOperatorGuid());
				bomView.setOwnerGroupGuid(this.stubService.getUserSignature().getLoginGroupGuid());
				bomView.setUnique(null);
				// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
				ClassStub.decorateObjectGuid(bomView.getObjectGuid(), this.stubService);

				EMM emm = this.stubService.getEMM();

				if (bomView.getLifecyclePhaseGuid() == null || "".equals(bomView.getLifecyclePhaseGuid()))
				{
					LifecyclePhaseInfo lifecyclePhaseInfo = emm.getFirstLifecyclePhaseInfoByClassName(bomView.getObjectGuid().getClassName());
					bomView.setLifecyclePhaseGuid(lifecyclePhaseInfo.getGuid());
				}

				FoundationObject retObject = ds.create(bomView, null, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId());

				// 流程中修改关系，添加end2时，不加入附件列表
				// if (!StringUtils.isNullString(procRtGuid))
				// {
				// ((WFEImpl) this.stubService.getWFE()).getAttachStub().addAttachmentInRunningProcess(procRtGuid,
				// retObjectGuid.getGuid());
				// }

				retBOMView = this.getBOMView(retObject.getObjectGuid(), isCheckAcl);
			}

			return retBOMView;
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
			Object[] args = new Object[] { bomView };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}
	}

	public BOMView createBOMViewAsBOMView(ObjectGuid bomViewObjectGuid, boolean isCheckAcl, String ownerGroupGuid, String ownerUserGuid) throws ServiceRequestException
	{

		BOMView bomView = this.getBOMView(bomViewObjectGuid, isCheckAcl);

		if (bomView == null)
		{
			return null;
		}

		// String sessionId = this.stubService.getSignature().getCredential();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			String structureClassName = bomView.getStructureClassName();
			List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(structureClassName);
			SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(structureClassName, uiObjectList);

			DataRule dataRule = new DataRule();
			dataRule.setLocateTime(this.stubService.getEMM().getSystemDate());

			List<BOMStructure> bomStructureList = this.stubService.listBOM(bomView.getObjectGuid(), searchCondition, null, dataRule);
			bomView.getObjectGuid().setGuid(null);
			bomView.getObjectGuid().setMasterGuid(null);
			bomView.setObjectGuid(bomView.getObjectGuid());
			bomView.setEnd1ObjectGuid(null);
			bomView.setRevisionId(null);

			bomView.put(ECConstants.EC_FLAG, null);
			bomView.put(ECConstants.EC_FLAG + "$CLASS", null);
			bomView.put(ECConstants.EC_FLAG + "$MASTER", null);

			ClassStub.decorateObjectGuid(bomView.getObjectGuid(), this.stubService);
			LifecyclePhaseInfo lifecyclePhaseInfoBOMView = this.stubService.getEMM().getFirstLifecyclePhaseInfoByClassName(bomView.getObjectGuid().getClassName());
			bomView.setLifecyclePhaseGuid(lifecyclePhaseInfoBOMView.getGuid());
			SystemStatusEnum statusBOMView = SystemStatusEnum.ECP;
			bomView.setStatus(statusBOMView);

			bomView.setOwnerUserGuid(ownerUserGuid);
			bomView.setOwnerGroupGuid(ownerGroupGuid);
			bomView.setUnique(null);
			bomView = this.saveBOMView(bomView, isCheckAcl, null);
			bomView = this.stubService.getBOMViewCheckOutStub().checkOut(bomView, this.stubService.getOperatorGuid(), isCheckAcl);
			if (!SetUtils.isNullList(bomStructureList))
			{
				for (BOMStructure bomStructure : bomStructureList)
				{
					bomStructure.setViewObjectGuid(bomView.getObjectGuid());
					bomStructure.setGuid(null);
					bomStructure.setRsFlag(false);
					this.stubService.linkBOM(bomView.getObjectGuid(), bomStructure.getEnd2ObjectGuid(), bomStructure);
				}
			}
			bomView = this.stubService.getBOMViewCheckInStub().checkIn(bomView, isCheckAcl);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
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
		finally
		{
		}
		return bomView;
	}

	public BOMView createBOMViewByBOMTemplate(String bomTemplateGuid, ObjectGuid end1ObjectGuid, boolean isPrecise, boolean isCheckAcl) throws ServiceRequestException
	{
		BOMTemplate bomTemplate = this.stubService.getEMM().getBOMTemplate(bomTemplateGuid);
		InstanceService ds = DataServer.getInstanceService();
		ServiceRequestException returnObj = null;
		String sessionId = this.stubService.getSignature().getCredential();

		BOMView bomView = null;

		if (bomTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_BOM_TEMPLATE_NOT_EXIST", "BOM template is not exist");
		}
		else
		{
			if (bomTemplate.isValid() == false)
			{
				throw new ServiceRequestException("ID_APP_BOM_TEMPLATE_NOT_EXIST", "BOM template is not exist");
			}
		}

		// 判断pricise是否能被修改
		if (bomTemplate.getPrecise() != null && bomTemplate.getPrecise() != BomPreciseType.USERSPECIFIED)
		{
			if (!(bomTemplate.getPrecise() == BomPreciseType.PRECISE && isPrecise || bomTemplate.getPrecise() == BomPreciseType.NONPRECISE && !isPrecise))
			{
				throw new ServiceRequestException("ID_APP_PRECISE_CANNT_MODIFY", " BOM template'pricise is not specified by the user");
			}

		}

		try
		{
			BOMView result = this.getBOMViewByEND1(end1ObjectGuid, bomTemplate.getName(), false);
			AclService aclService = DataServer.getAclService();
			if (result != null)
			{
				if (Constants.isSupervisor(isCheckAcl, this.stubService) && !aclService.hasAuthority(result.getObjectGuid(), AuthorityEnum.CREATE, sessionId))
				{
					throw new DynaDataExceptionSQL("no create authority.", null, DataExceptionEnum.DS_NO_CREATE_AUTH);
				}
				throw new ServiceRequestException("ID_APP_MULTI_CREATE_RELATION", "multi create relation by one template");
			}

			bomView = new BOMView();
			ObjectGuid objectGuid = new ObjectGuid();
			objectGuid.setClassGuid(bomTemplate.getViewClassGuid());

			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			bomView.setObjectGuid(objectGuid);
			bomView.setEnd1ObjectGuid(end1ObjectGuid);
			bomView.setName(bomTemplate.getName());
			bomView.setPrecise(isPrecise);

			FoundationObject end1FoundationObject = this.stubService.getBOAS().getObject(end1ObjectGuid);

			if (end1FoundationObject != null && end1FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end1FoundationObject.getFullName());
			}

			if (end1FoundationObject != null)
			{
				bomView.setId(end1FoundationObject.getId());
				bomView.setRevisionId(end1FoundationObject.getRevisionId());
				bomView.put(ViewObject.TEMPLATE_ID, bomTemplate.getId());
			}

			bomView.setOwnerUserGuid(this.stubService.getOperatorGuid());
			bomView.setOwnerGroupGuid(this.stubService.getUserSignature().getLoginGroupGuid());

			EMM emm = this.stubService.getEMM();

			if (bomView.getLifecyclePhaseGuid() == null || "".equals(bomView.getLifecyclePhaseGuid()))
			{
				LifecyclePhaseInfo lifecyclePhaseInfo = emm.getFirstLifecyclePhaseInfoByClassName(bomView.getObjectGuid().getClassName());
				bomView.setLifecyclePhaseGuid(lifecyclePhaseInfo.getGuid());
			}

			this.stubService.getEOSS().executeAddBeforeEvent(bomView);

			FoundationObject retObject = ds.create(bomView, null, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId());
			bomView = this.getBOMView(retObject.getObjectGuid(), isCheckAcl);

			// 20170301 去除再次查询
			// bomView = this.getBOMView(retObjectGuid, isCheckAcl);

			this.stubService.getEOSS().executeAddAfterEvent(bomView);
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
			Object[] args = new Object[] { bomView };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}
		return bomView;

	}

	public BOMView updateBOMViewPrecise(BOMView bomView) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		RelationService ds = DataServer.getRelationService();
		try
		{
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateByName(bomView.getEnd1ObjectGuid(), bomView.getName());
			ds.convertPrecise(bomView, bomTemplate.getGuid(), Constants.isSupervisor(true, this.stubService), sessionId);

			return this.getBOMView(bomView.getObjectGuid(), true);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public BOMView updateBomViewOwner(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime, boolean isCheckAuth) throws ServiceRequestException
	{

		ServiceRequestException returnObj = null;

		try
		{
			((BOASImpl) this.stubService.getBOAS()).getFUpdaterStub().update(objectGuid, ownerUserGuid, ownerGroupGuid, null, updateTime, null, null, null, isCheckAuth, null);
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw returnObj;
		}

		return null;

	}

	public List<FoundationObject> batchListEnd1OfBOMView(List<BOMView> bomViewList) throws ServiceRequestException
	{
		// 把end1按照class分组
		Map<String, List<ObjectGuid>> map = new HashMap<String, List<ObjectGuid>>();
		if (!SetUtils.isNullList(bomViewList))
		{
			for (BOMView bomView : bomViewList)
			{
				ObjectGuid end1ObjectGuid = bomView.getEnd1ObjectGuid();
				if (map.get(end1ObjectGuid.getClassGuid()) == null)
				{
					map.put(end1ObjectGuid.getClassGuid(), new ArrayList<ObjectGuid>());
				}
				map.get(end1ObjectGuid.getClassGuid()).add(end1ObjectGuid);
			}
		}

		List<FoundationObject> end1List = new ArrayList<FoundationObject>();
		// 按照不同类的class，分批查询end1
		if (!SetUtils.isNullMap(map))
		{
			for (String end1ClassGuid : map.keySet())
			{
				List<ObjectGuid> tmpList = map.get(end1ClassGuid);
				List<FoundationObject> tmpEnd1List = this.stubService.getBOAS().getObject4ObjectField(tmpList);
				if (!SetUtils.isNullList(tmpEnd1List))
				{
					for (FoundationObject end1 : tmpEnd1List)
					{
						end1List.add(end1);
					}
				}
			}
		}
		return end1List;
	}
}

class DeleteScheduledTask extends AbstractScheduledTask
{
	private ServiceContext			serviceContext		= null;
	private ObjectGuid				objectGuid			= null;
	private List<FoundationObject>	end1List			= null;
	private String					bmGuid				= null;
	private String					exceptionParameter	= null;
	private BOAS					boas				= null;
	private BOMS					boms				= null;

	public DeleteScheduledTask(ServiceContext serviceContext, ObjectGuid objectGuid, List<FoundationObject> end1List, String bmGuid, String exceptionParameter)
	{
		this.serviceContext = serviceContext;
		this.objectGuid = objectGuid;
		this.end1List = end1List;
		this.bmGuid = bmGuid;
		this.exceptionParameter = exceptionParameter;
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]DeletetScheduledTask , Scheduled Task Start...");
		try
		{
			this.boas = this.serviceContext.allocatService(BOAS.class);
			this.boms = this.serviceContext.allocatService(BOMS.class);
			this.boas.deleteReference(objectGuid, exceptionParameter);
			if (this.end1List != null)
			{
				((BOMSImpl) this.boms).getBomStub().updateUHasBOM(this.end1List, this.bmGuid);
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error("run send mail:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.boas != null)
			{
				this.serviceContext.releaseService(this.boas);
			}
			if (this.boms != null)
			{
				this.serviceContext.releaseService(this.boms);
			}
		}
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]DeletetScheduledTask , Scheduled Task End...");
	}
}