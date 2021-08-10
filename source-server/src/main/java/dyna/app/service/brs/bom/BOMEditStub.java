/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMEdit
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.bom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.sch.Scheduler;
import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRViewLinkImpl;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.bom.comparator.ECEffectedBOMRelationComparator;
import dyna.app.service.brs.brm.BRMImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.CheckConnectUtil;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.dto.ECEffectedBOMRelation;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.BomPreciseType;
import dyna.common.systemenum.BuiltinRelationNameEnum;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.ECOperateTypeEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.service.acl.AclService;
import dyna.data.service.relation.RelationService;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.EOSS;

/**
 * @author Wanglei
 * 
 */
public class BOMEditStub extends AbstractServiceStub<BOMSImpl>
{
	private static TrackerBuilder	trackerBuilder			= null;
	private static TrackerBuilder	unLinktrackerBuilder	= null;

	/**
	 * @param context
	 * @param service
	 */
	public BOMEditStub(ServiceContext context, BOMSImpl service)
	{
		super(context, service);
	}

	private void unlinkBOMInner(BOMView viewObject, BOMStructure bomStructure, boolean isCheckACL) throws ServiceRequestException
	{

		ServiceRequestException returnObj = null;
		String sessionId = this.stubService.getSignature().getCredential();
		RelationService ds = DataServer.getRelationService();
		try
		{
			// DCR规则检查
			List<ObjectGuid> end2ObjectGuidList = new ArrayList<ObjectGuid>();
			end2ObjectGuidList.add(bomStructure.getEnd2ObjectGuid());
			viewObject.getTemplateID();
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(viewObject.getTemplateID());
			this.stubService.getDCR().check(viewObject.getEnd1ObjectGuid(), end2ObjectGuidList, bomTemplate.getName(), RuleTypeEnum.BOM);

			EOSS eoss = this.stubService.getEOSS();
			// 处理取替代，并且设置bomStructure上取替代的标识
			((BRMImpl) this.stubService.getBRM()).getReplaceObjectStub().dealWithHistoryReplaceData(null, null, bomStructure, viewObject.getName(), true, true);
			// !invoke unlink.before
			eoss.executeDeleteBeforeEvent(bomStructure);

			ds.delete(bomStructure, Constants.isSupervisor(isCheckACL, this.stubService), sessionId);
			// !invoke unlink.after
			eoss.executeDeleteAfterEvent(bomStructure);
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
			Object[] args = new Object[] { viewObject, bomStructure.getEnd2ObjectGuid() };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getUNLinkTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}

	}

	public void unlinkBOM(BOMStructure bomStructure, boolean isCheckACL) throws ServiceRequestException
	{
		ServiceRequestException returnObj = null;
		BOMView viewObject = null;
		FoundationObject end1 = null;
		try
		{
			viewObject = this.stubService.getBomViewStub().getBOMView(bomStructure.getViewObjectGuid(), true);
			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			end1 = this.stubService.getBOAS().getObjectByGuid(viewObject.getEnd1ObjectGuid());
			if (SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()) || SystemStatusEnum.PRE.equals(viewObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			this.unlinkBOMInner(viewObject, bomStructure, isCheckACL);
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
			Object[] args = new Object[] { viewObject, bomStructure.getEnd2ObjectGuid() };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getUNLinkTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));

			if (end1 != null)
			{
				String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler().addTask(new UpdateScheduledTask(serviceContext, end1, bmGuid));
			}
		}
	}

	private TrackerBuilder getUNLinkTrackerBuilder()
	{
		if (unLinktrackerBuilder == null)
		{
			unLinktrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);

			unLinktrackerBuilder.setTrackerRendererClass(TRViewLinkImpl.class, TrackedDesc.UNLINK_BOM);
			unLinktrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return unLinktrackerBuilder;
	}

	public BOMStructure linkBOMNoCheckConnectBy(ObjectGuid viewObjectGuid, ObjectGuid end2ObjectGuid, BOMStructure bomStructure, boolean isCheckAcl, String procRtGuid)
			throws ServiceRequestException
	{
		return this.linkBOM(viewObjectGuid, end2ObjectGuid, bomStructure, isCheckAcl, procRtGuid, false, true);
	}

	private BOMStructure linkBOMInner(BOMView bomViewObject, ObjectGuid end2ObjectGuid, BOMStructure bomStructure, boolean isCheckAcl, boolean isCheckField)
			throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();
		ServiceRequestException returnObj = null;
		ObjectGuid viewObjectGuid = bomViewObject.getObjectGuid();
		// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
		ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);

		try
		{
			ObjectGuid objectGuid = bomStructure.getObjectGuid();
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			bomStructure.setObjectGuid(objectGuid);
			bomStructure.setViewObjectGuid(viewObjectGuid);
			bomStructure.setEnd2ObjectGuid(end2ObjectGuid);

			EOSS eoss = this.stubService.getEOSS();

			// !invoke link.before
			eoss.executeAddBeforeEvent(bomStructure);

			viewObjectGuid = bomStructure.getViewObjectGuid();
			end2ObjectGuid = bomStructure.getEnd2ObjectGuid();
			if (viewObjectGuid == null || end2ObjectGuid == null)
			{
				throw new ServiceRequestException("ID_APP_END2_ILLEGAL", "end2 is not illegal");
			}
			if (bomViewObject.getEnd1ObjectGuid().getMasterGuid().equals(end2ObjectGuid.getMasterGuid())
					|| bomViewObject.getEnd1ObjectGuid().getGuid().equals(end2ObjectGuid.getGuid()))
			{
				throw new DynaDataExceptionAll("linkBOM() youself.", null, DataExceptionEnum.DS_RELATION_SELF);
			}
			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));

			if (bomTemplate != null)
			{
				List<BOMTemplateEnd2> boInfoList = this.stubService.getEMM().getBOMTemplate(bomTemplate.getGuid()).getBOMTemplateEnd2List();
				boolean end2Auth = false;
				if (!SetUtils.isNullList(boInfoList))
				{
					ClassStub.decorateObjectGuid(end2ObjectGuid, this.stubService);

					for (BOMTemplateEnd2 boInfo : boInfoList)
					{
						BOInfo tempboinfo = this.stubService.getEMM().getCurrentBoInfoByName(boInfo.getEnd2BoName(), false);
						if (tempboinfo.getClassGuid().equals(end2ObjectGuid.getClassGuid()))
						{
							end2Auth = true;
							break;
						}
						else
						{
							List<ClassInfo> list = this.stubService.getEMM().listAllSubClassInfoOnlyLeaf(null, tempboinfo.getClassName());
							if (list != null)
							{
								for (ClassInfo info : list)
								{
									if (info.getGuid().equals(end2ObjectGuid.getClassGuid()))
									{
										end2Auth = true;
										break;
									}
								}
							}
						}
					}

				}

				if (!end2Auth)
				{
					throw new ServiceRequestException("ID_APP_END2_ILLEGAL", "end2 is not illegal");
				}

				// DCR规则检查
				List<ObjectGuid> end2ObjectGuidList = new ArrayList<ObjectGuid>();
				end2ObjectGuidList.add(end2ObjectGuid);
				this.stubService.getDCR().check(bomViewObject.getEnd1ObjectGuid(), end2ObjectGuidList, bomTemplate.getName(), RuleTypeEnum.BOM);
			}

			String key = BOMStructure.BOMKEY;
			bomStructure.put(key, UUID.randomUUID().toString().replace("-", "").toUpperCase());
			FoundationObject end2FoundationObject = this.stubService.getBOAS().getObjectByGuid(end2ObjectGuid);
			if (end2FoundationObject != null && end2FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end2FoundationObject.getFullName());
			}

			if (isCheckField)
			{
				String message = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().checkDynaObjectField(bomStructure);

				if (!StringUtils.isNullString(message))
				{
					throw new ServiceRequestException(message);
				}
			}

			this.checkFoundationFieldRegex(bomStructure);

			BOMStructure retbomStructure = ds.linkBOM(viewObjectGuid, end2ObjectGuid, bomStructure, bomTemplate.getPrecise() == BomPreciseType.PRECISE,
					Constants.isSupervisor(isCheckAcl, this.stubService), this.stubService.getSignature().getCredential(), this.stubService.getFixedTransactionId());
			if (retbomStructure == null)
			{
				return null;
			}
			retbomStructure.setViewObjectGuid(viewObjectGuid);
			bomStructure = retbomStructure;
			// 处理取替代，并且设置bomStructure上取替代的标识
			((BRMImpl) this.stubService.getBRM()).getReplaceObjectStub().dealWithHistoryReplaceData(null, null, bomStructure, bomViewObject.getName(), false, false);
			if (bomStructure.isRsFlag())
			{
				BOMStructure bomStructure_ = this.stubService.getProxyBOMS().saveBOM(bomStructure, false, false);
				bomStructure = bomStructure_;
			}

			ClassStub.decorateObjectGuid(bomStructure.getObjectGuid(), this.stubService);
			eoss.executeAddAfterEvent(bomStructure);

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
			Object[] args = new Object[] { viewObjectGuid, end2ObjectGuid };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}

		return bomStructure;

	}

	protected BOMStructure linkBOM(ObjectGuid viewObjectGuid, ObjectGuid end2ObjectGuid, BOMStructure bomStructure, boolean isCheckAcl, String procRtGuid, boolean isCheckConnectBy,
			boolean isCheckField) throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();
		ServiceRequestException returnObj = null;
		// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
		ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);

		BOMView bomViewObject = null;
		FoundationObject end1FoundationObject = null;
		String sessionId = this.stubService.getUserSignature().getCredential();
		try
		{
			if (viewObjectGuid == null)
			{
				throw new ServiceRequestException("ID_APP_END2_ILLEGAL", "end2 is not illegal");
			}

			bomViewObject = this.stubService.getBOMView(viewObjectGuid);
			if (bomViewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			if (SystemStatusEnum.OBSOLETE.equals(bomViewObject.getStatus()) || SystemStatusEnum.PRE.equals(bomViewObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			if (bomViewObject != null)
			{
				end1FoundationObject = this.stubService.getBOAS().getObjectByGuid(bomViewObject.getEnd1ObjectGuid());
				if (end1FoundationObject != null && end1FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
				{
					throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end1FoundationObject.getFullName());
				}
				BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
				if (bomTemplate != null)
				{
					if (bomTemplate != null)
					{
						// 不允许有相同的end2
						List<String> end2MasterGuidList = new ArrayList<String>();
						if (!bomTemplate.isIncorporatedMaster())
						{
							List<StructureObject> bomList = ds.listSimpleStructureOfRelation(viewObjectGuid, bomTemplate.getViewClassGuid(), bomTemplate.getStructureClassGuid(),
									sessionId);
							if (bomList != null)
							{
								for (StructureObject stru : bomList)
								{
									end2MasterGuidList.add(stru.getEnd2ObjectGuid().getMasterGuid());
								}
								if (end2MasterGuidList.contains(bomStructure.getEnd2ObjectGuid().getMasterGuid()))
								{
									FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(bomStructure.getEnd2ObjectGuid(),
											false);

									throw new ServiceRequestException("ID_APP_BOMEDIT_CANT_CONECT_BOMVIEW", "end2 is not relation", null, objectByGuid.getFullName());
								}
							}
						}
					}
				}
				// 检查能否挂相同的end2---ec要求2014-4-10 wangwx修改

			}

			if (!bomViewObject.isCheckOut())
			{
				this.stubService.getBOMViewCheckOutStub().checkOut(bomViewObject, this.stubService.getOperatorGuid(), true);
			}

			if (isCheckField)
			{
				String message = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().checkDynaObjectField(bomStructure);

				if (!StringUtils.isNullString(message))
				{
					throw new ServiceRequestException(message);
				}
			}

			BOMStructure retBOMStructure = this.linkBOMInner(bomViewObject, end2ObjectGuid, bomStructure, isCheckAcl, isCheckField);
			if (isCheckConnectBy)
			{
				Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				if (schedule == null)
				{
					this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
					schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				}
				CheckConnectUtil util = new CheckConnectUtil(this.stubService.getEMM(), schedule, this.stubService.getUserSignature().getCredential(),
						ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, false);
				if (util.checkConntc(bomViewObject.getEnd1ObjectGuid()))
				{
					throw new DynaDataExceptionAll("connect by error ", null, DataExceptionEnum.DS_CONNECT_BY_ERROR);
				}
			}

			bomStructure = retBOMStructure;
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
			Object[] args = new Object[] { viewObjectGuid, end2ObjectGuid };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));

			if (end1FoundationObject != null)
			{
				String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new UpdateScheduledTask(serviceContext, end1FoundationObject, bmGuid));
			}
		}

		return bomStructure;

	}

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);

			trackerBuilder.setTrackerRendererClass(TRViewLinkImpl.class, TrackedDesc.LINK_BOM);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

	protected BOMStructure linkBOM(ObjectGuid end1Object, ObjectGuid end2Object, BOMStructure bomStructure, String viewName, boolean isPrecise, boolean isCheckAcl,
			String procRtGuid, boolean isCheckField) throws ServiceRequestException
	{

		BOMView bomView = null;
		try
		{
			bomView = this.stubService.getBOMViewByEND1(end1Object, viewName);

			// 如果没有View或者查询到的view不是工作状态 那么创建的标识符置为true
			boolean isNeedCreate = false;
			if (bomView != null)
			{
				// nothing
			}
			else
			{
				isNeedCreate = true;
			}

			// 先创建一个View 然后再关联
			if (isNeedCreate)
			{
				BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateByName(end1Object, viewName);

				if (bomTemplate == null)
				{
					throw new ServiceRequestException("ID_APP_NO_BOM_TEMPLATE", "no bom template:" + viewName, null, viewName);
				}
				else
				{
					bomView = this.stubService.getBomViewStub().createBOMViewByBOMTemplate(bomTemplate.getGuid(), end1Object, isPrecise, isCheckAcl);
				}
			}

			return this.linkBOM(bomView.getObjectGuid(), end2Object, bomStructure, isCheckAcl, procRtGuid, true, isCheckField);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected boolean canEditBom(FoundationObject end1Object, String name) throws ServiceRequestException
	{
		BOMView bomView = this.stubService.getBOMViewByEND1(end1Object.getObjectGuid(), name);

		if (bomView == null)
		{
			return false;
		}
		else
		{
			if (SystemStatusEnum.WIP.equals(bomView.getStatus()) || SystemStatusEnum.ECP.equals(bomView.getStatus())// ||
																													// SystemStatusEnum.CREATED.equals(bomView.getStatus())
			)
			{
				String sessionId = this.stubService.getUserSignature().getCredential();
				AclService aclService = DataServer.getAclService();
				return aclService.hasAuthority(bomView.getObjectGuid(), AuthorityEnum.LINK, sessionId);
			}
			else
			{
				return false;
			}
		}
	}

	protected List<BOMStructure> editBOM(BOMView itemView, List<ECEffectedBOMRelation> ecEffectedBOMRelationList, boolean isCheckAcl, String procRtGuid, String templateName,
			boolean isCheckField) throws ServiceRequestException
	{
		List<BOMStructure> updateList = new ArrayList<BOMStructure>();
		List<BOMStructure> rtnList = new ArrayList<BOMStructure>();
		boolean isCheckBomConnect = false;

		if (SetUtils.isNullList(ecEffectedBOMRelationList))
		{
			return null;
		}

		FoundationObject end1 = null;
		try
		{
			String sessionId = this.stubService.getUserSignature().getCredential();
			RelationService ds = DataServer.getRelationService();
			AclService aclService = DataServer.getAclService();
			if (Constants.isSupervisor(isCheckAcl, this.stubService))
			{
				if (!aclService.hasAuthority(itemView.getObjectGuid(), AuthorityEnum.LINK, sessionId))
				{
					throw new ServiceRequestException(DataExceptionEnum.DS_NO_LINK_AUTH.getMsrId(), "", null, itemView.getFullName());
				}
			}
			FoundationObject foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(itemView.getEnd1ObjectGuid(), false);
			if (foundationObject != null && foundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "object has bean obsoleted", null, foundationObject.getFullName());
			}

			BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(itemView.getTemplateID());
			List<String> end2MasterGuidList = new ArrayList<String>();
			if (!bomTemplate.isIncorporatedMaster())
			{
				List<StructureObject> bomList = ds.listSimpleStructureOfRelation(itemView.getObjectGuid(), bomTemplate.getViewClassGuid(), bomTemplate.getStructureClassGuid(),
						sessionId);
				if (bomList != null)
				{
					for (StructureObject stru : bomList)
					{
						end2MasterGuidList.add(stru.getEnd2ObjectGuid().getMasterGuid());
					}
				}
			}

			end1 = this.stubService.getBOAS().getObjectByGuid(itemView.getEnd1ObjectGuid());

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			Collections.sort(ecEffectedBOMRelationList, new ECEffectedBOMRelationComparator());

			for (int i = 0; i < ecEffectedBOMRelationList.size(); i++)
			{
				ECEffectedBOMRelation ecEffectedBOMRelation = ecEffectedBOMRelationList.get(i);
				BOMStructure bomStructure = ecEffectedBOMRelation.getBOMStructrue();
				if (!itemView.getObjectGuid().getGuid().equalsIgnoreCase(bomStructure.getViewObjectGuid().getGuid()))
				{

				}
				if (ECOperateTypeEnum.MODIFY.toString().equals(ecEffectedBOMRelation.getEcOperate()))
				{
					FoundationObject end2 = this.stubService.getBOAS().getObject(bomStructure.getEnd2ObjectGuid());
					if (end2 != null && end2.getStatus() == SystemStatusEnum.OBSOLETE)
					{
						throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end2.getFullName());
					}

					bomStructure.setSequence(bomStructure.getSequence() + "#");
					String originalEnd2MasterGuid = (String) bomStructure.getOriginalValue("END2$MASTERFK");
					String newEnd2MasterGuid = bomStructure.getEnd2ObjectGuid().getMasterGuid();
					if (newEnd2MasterGuid.equals(originalEnd2MasterGuid) == false)
					{
						isCheckBomConnect = true;
						// 检查能否挂相同的end2---ec要求2014-4-10 wangwx修改
						if (bomTemplate != null)
						{
							// 不允许有相同的end2
							if (!bomTemplate.isIncorporatedMaster())
							{
								if (end2MasterGuidList.contains(newEnd2MasterGuid))
								{
									FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(bomStructure.getEnd2ObjectGuid(),
											false);

									throw new ServiceRequestException("ID_APP_BOMEDIT_CANT_CONECT_BOMVIEW", "end2 is not relation", null, objectByGuid.getFullName());
								}

							}
						}
						end2MasterGuidList.remove(originalEnd2MasterGuid);
						end2MasterGuidList.add(newEnd2MasterGuid);
						// 处理取替代，并且设置bomStructure上取替代的标识
						ObjectGuid componentItemObjectGuid = new ObjectGuid((String) bomStructure.getOriginalValue("END2$CLASSGUID"), null,
								(String) bomStructure.getOriginalValue("END2$"), (String) bomStructure.getOriginalValue("END2$MASTERFK"), null);
						((BRMImpl) this.stubService.getBRM()).getReplaceObjectStub().dealWithHistoryReplaceData(null, componentItemObjectGuid, bomStructure, bomTemplate.getName(),
								true, false);
						ObjectGuid replaceComponentItemObjectGuid = bomStructure.getEnd2ObjectGuid();
						if ((String) bomStructure.getOriginalValue("END2$MASTERFK") != null
								&& !((String) bomStructure.getOriginalValue("END2$MASTERFK")).equals(replaceComponentItemObjectGuid.getMasterGuid()))
						{
							((BRMImpl) this.stubService.getBRM()).getReplaceObjectStub().dealWithHistoryReplaceData(null, null, bomStructure, bomTemplate.getName(), false, false);
						}
					}
					bomStructure = this.stubService.getBomStub().saveBOMInner(bomStructure, bomTemplate.getPrecise() == BomPreciseType.PRECISE, isCheckField,
							!bomTemplate.isIncorporatedMaster());
					updateList.add(bomStructure);
					ecEffectedBOMRelationList.remove(i);
					i--;
				}
				else if (ECOperateTypeEnum.REMOVE.toString().equals(ecEffectedBOMRelation.getEcOperate()))
				{
					this.unlinkBOMInner(itemView, bomStructure, false);
					end2MasterGuidList.remove(bomStructure.getEnd2ObjectGuid().getMasterGuid());
					ecEffectedBOMRelationList.remove(i);
					i--;
				}
			}

			if (!SetUtils.isNullList(ecEffectedBOMRelationList))
			{
				for (int i = 0; i < ecEffectedBOMRelationList.size(); i++)
				{
					ECEffectedBOMRelation ecEffectedBOMRelation = ecEffectedBOMRelationList.get(i);
					BOMStructure bomStructure = ecEffectedBOMRelation.getBOMStructrue();
					if (ECOperateTypeEnum.INSERT.toString().equals(ecEffectedBOMRelation.getEcOperate()))
					{
						if (bomTemplate != null)
						{
							// 不允许有相同的end2
							if (!bomTemplate.isIncorporatedMaster())
							{
								if (end2MasterGuidList.contains(bomStructure.getEnd2ObjectGuid().getMasterGuid()))
								{
									FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(bomStructure.getEnd2ObjectGuid(),
											false);

									throw new ServiceRequestException("ID_APP_BOMEDIT_CANT_CONECT_BOMVIEW", "end2 is not relation", null, objectByGuid.getFullName());
								}

							}
						}
						end2MasterGuidList.add(bomStructure.getEnd2ObjectGuid().getGuid());
						ObjectGuid end2Object = bomStructure.getEnd2ObjectGuid();
						BOMStructure linkBOM = this.linkBOMInner(itemView, end2Object, bomStructure, false, true);
						BOMView bomView = this.stubService.getBOMViewByEND1(linkBOM.getEnd2ObjectGuid(), templateName);
						if (bomView != null)
						{
							linkBOM.setEnd2ViewCheckOut(bomView.isCheckOut());
						}
						rtnList.add(linkBOM);
						isCheckBomConnect = true;
					}
				}
			}
			if (isCheckBomConnect)
			{
				Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				if (schedule == null)
				{
					this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
					schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				}
				CheckConnectUtil util = new CheckConnectUtil(this.stubService.getEMM(), schedule, this.stubService.getUserSignature().getCredential(),
						ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, false);
				if (util.checkConntc(itemView.getEnd1ObjectGuid()))
				{
					throw new DynaDataExceptionAll("connect by error ", null, DataExceptionEnum.DS_CONNECT_BY_ERROR);
				}
			}

			if (!SetUtils.isNullList(updateList))
			{
				for (BOMStructure bom : updateList)
				{
					bom.setSequence(bom.getSequence().substring(0, bom.getSequence().length() - 1));
					bom = this.stubService.getBomStub().saveBOMInner(bom, bomTemplate.getPrecise() == BomPreciseType.PRECISE, false, isCheckField);
					rtnList.add(bom);

				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
			if (end1 != null)
			{
				String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler().addTask(new UpdateScheduledTask(serviceContext, end1, bmGuid));
			}
		}

		return rtnList;
	}

	public boolean cadToBom(FoundationObject cad, String bomTemplateGuid, boolean isRoot, FoundationObject end12, boolean isYS) throws ServiceRequestException
	{
		BOMTemplate bomTemplate = this.stubService.getEMM().getBOMTemplate(bomTemplateGuid);
		String sessionId = this.stubService.getUserSignature().getCredential();
		try
		{

			if (cad != null)
			{
				FoundationObject end1 = null;
				BOMView bomView = null;
				List<FoundationObject> end2List = new ArrayList<FoundationObject>();

				if (isRoot)
				{
					end1 = end12;
				}
				else
				{
					// 查找end1
					List<FoundationObject> end1List = this.stubService.getBOAS().listWhereReferenced(cad.getObjectGuid(), BuiltinRelationNameEnum.ITEMCAD3D.toString(), null, null);
					if (!SetUtils.isNullList(end1List))
					{
						end1 = end1List.get(0);
					}
				}

				if (end1 == null)
				{
					return false;
				}
				else
				{
					bomView = this.stubService.getBOMViewByEND1(end1.getObjectGuid(), bomTemplate.getName());
					if (bomView != null)
					{
						SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(bomTemplate.getStructureClassName());
						List<BOMStructure> bomLlist = this.stubService.listBOM(bomView.getObjectGuid(), searchCondition, null, null);
						if (!SetUtils.isNullList(bomLlist) && bomLlist.isEmpty() == false)
						{
							return false;
						}
					}
				}

				// 查找end2List
				List<FoundationObject> end2CADList = new ArrayList<FoundationObject>();
				List<StructureObject> end2CADStructureList = null;

				RelationTemplateInfo tmp = this.stubService.getEMM().getRelationTemplateByName(cad.getObjectGuid(), "MODEL-STRUCTURE");
				if (tmp != null)
				{
					ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(cad.getObjectGuid(), tmp.getName());
					if (viewObject != null)
					{
						SearchCondition condition = null;
						String structureClassName = tmp.getStructureClassName();

						condition = SearchConditionFactory.createSearchConditionForStructure(structureClassName);
						List<String> uiNameList = new ArrayList<String>();
						List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(structureClassName, UITypeEnum.FORM, true);
						if (!SetUtils.isNullList(uiObjectList))
						{
							for (UIObjectInfo uiObject : uiObjectList)
							{
								uiNameList.add(uiObject.getName());
							}
						}
						condition.setResultUINameList(uiNameList);
						condition.addResultField("BOMRelated");

						List<FoundationObject> end2CADListtem = this.stubService.getBOAS().listFoundationObjectOfRelation(viewObject.getObjectGuid(), condition, null, null, true);
						end2CADStructureList = this.listObjectOfRelation(viewObject.getObjectGuid(), condition);

						if (!isYS)
						{
							end2CADList.addAll(end2CADListtem);
						}
						else
						{
							if (!SetUtils.isNullList(end2CADStructureList))
							{
								Map<String, StructureObject> map = new HashMap<String, StructureObject>();
								for (StructureObject structureObject : end2CADStructureList)
								{
									map.put(structureObject.getEnd2ObjectGuid().getGuid(), structureObject);
								}

								for (FoundationObject fo : end2CADListtem)
								{
									StructureObject structureObject = map.get(fo.getObjectGuid().getGuid());
									if (structureObject != null)
									{
										if (!StringUtils.isNullString((String) structureObject.get("BOMRelated")))
										{
											if (!BooleanUtils.getBooleanByYN((String) structureObject.get("BOMRelated")))
											{
												end2CADList.add(fo);
											}
										}
									}
								}
							}
						}
					}
				}
				// end2数量缓存
				Map<String, Double> quetityMap = new HashMap<String, Double>();
				Map<String, Double> quetitySMap = new HashMap<String, Double>();

				if (!SetUtils.isNullList(end2CADList))
				{
					for (StructureObject structureObject : end2CADStructureList)
					{
						Double newQuantity = 1.0;
						if (structureObject.get("Quantity") != null)
						{
							newQuantity = ((Number) structureObject.get("Quantity")).doubleValue();
						}
						quetitySMap.put(structureObject.getEnd2ObjectGuid().getGuid(), newQuantity);
					}

					for (FoundationObject foundationObject : end2CADList)
					{
						List<FoundationObject> foList = this.stubService.getBOAS().listWhereReferenced(foundationObject.getObjectGuid(),
								BuiltinRelationNameEnum.ITEMCAD3D.toString(), null, null);

						if (!SetUtils.isNullList(foList))
						{
							if (!end2List.contains(foList.get(0)))
							{
								end2List.add(foList.get(0));
								quetityMap.put(foList.get(0).getGuid(), quetitySMap.get(foundationObject.getGuid()));
							}
						}
					}
				}
				if (end1 != null && !SetUtils.isNullList(end2List))
				{
					boolean isNew = false;
					int increment = 10;
					int length = -1;
					List<BOMTemplateInfo> templsit = this.stubService.getEMM().listBOMTemplateByEND1(end1.getObjectGuid());
					if (SetUtils.isNullList(templsit))
					{
						throw new ServiceRequestException("ID_APP_CADTOBOM_NOT_SAME_BOM_TEMPLATE", "End1: " + end1.getFullName() + "  Has No BOM Template!");
					}
					boolean isSameTemplate = false;
					for (BOMTemplateInfo template : templsit)
					{
						if (template.getName().equals(bomTemplate.getName()))
						{
							isSameTemplate = true;
						}
					}
					if (!isSameTemplate)
					{
						throw new ServiceRequestException("ID_APP_CADTOBOM_NOT_SAME_BOM_TEMPLATE", end1.getFullName() + " BOM Template Error!");
					}

					if (bomTemplate.getSequencePad() && bomTemplate.getSequenceLength() != null)
					{
						length = bomTemplate.getSequenceLength().intValue();
					}
					if (bomTemplate.getSequenceIncrement() != null)
					{
						increment = bomTemplate.getSequenceIncrement().intValue();
					}
					int start = bomTemplate.getSequenceStart().intValue();
					int max = 0;

					List<String> end2Guid = new ArrayList<String>();

					end2List = this.sort(end2List);

					if (bomView == null)
					{
						if (Constants.isSupervisor(true, this.stubService) && !this.stubService.getACL().hasFoundationCreateACL(bomTemplate.getViewClassName()))
						{
							throw new ServiceRequestException("ID_APP_CADTOBOM_ACL", "has no authority");
						}
						bomView = this.stubService.createBOMViewByBOMTemplate(bomTemplate.getGuid(), end1.getObjectGuid(), false);
						isNew = true;
					}
					else
					{
						// 取得顺序号
						SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(bomTemplate.getStructureClassName());
						List<BOMStructure> bomLlist = this.stubService.listBOM(bomView.getObjectGuid(), searchCondition, null, null);
						if (!SetUtils.isNullList(bomLlist))
						{
							for (BOMStructure bomStructure : bomLlist)
							{
								end2Guid.add(bomStructure.getEnd2ObjectGuid().getGuid());
								String number = bomStructure.getSequence();
								if (!StringUtils.isNullString(number))
								{
									try
									{
										int sequence = Integer.parseInt(number);
										if (sequence > max)
										{
											max = sequence;
										}
									}
									catch (NumberFormatException e)
									{
									}
								}
							}
						}
					}
					boolean flag = false;
					// 创建BOM结构
					if (bomView != null)
					{
						if (bomView.getStatus().equals(SystemStatusEnum.PRE))
						{
							throw new ServiceRequestException("ID_APP_CADTOBOM_PRE", "bomview is pre");
						}

						if (bomView.getStatus().equals(SystemStatusEnum.RELEASE))
						{
							throw new ServiceRequestException("ID_APP_CADTOBOM_RLS", "bomview is rls");
						}

						if (bomView.isCheckOut() && !bomView.getCheckedOutUserGuid().equals(this.stubService.getUserSignature().getUserGuid()))
						{
							throw new ServiceRequestException("ID_APP_CADTOBOM_CHECKEDOUT_BYOTHERS", "checked out by others");
						}
						// boolean ischeckOut = false;
						if (!bomView.isCheckOut())
						{
							this.stubService.checkOut(bomView);
							// ischeckOut = true;
						}
						if (!SetUtils.isNullList(end2List))
						{
//							DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
							for (int i = 0; i < end2List.size(); i++)
							{
								FoundationObject foundationObject = end2List.get(i);
								if (!isNew && end2Guid.contains(foundationObject.getObjectGuid().getGuid()))
								{
									continue;
								}
								// 添加BOMstructure

								BOMStructure bomStructure = null;
								bomStructure = this.stubService.newBOMStructure(bomTemplate.getStructureClassGuid(), bomTemplate.getStructureClassName());

								bomStructure.setQuantity(Double.valueOf(quetityMap.get(foundationObject.getObjectGuid().getGuid())));
								ObjectGuid end2ObjectGuid = foundationObject.getObjectGuid();
								if (bomView.isPrecise())
								{
									end2ObjectGuid.setIsMaster(false);
								}

								if (foundationObject.getStatus() != null)
								{
									bomStructure.setStatus(foundationObject.getStatus());
								}
								bomStructure.put(SystemClassFieldEnum.ECFLAG.getName() + "NAME", foundationObject.get(SystemClassFieldEnum.ECFLAG.getName() + "NAME"));
								bomStructure.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), foundationObject.getCheckedOutTime());
								bomStructure.put(SystemClassFieldEnum.RELEASETIME.getName(), foundationObject.getReleaseTime());
								bomStructure.put(SystemClassFieldEnum.OBSOLETETIME.getName(), foundationObject.getObsoleteTime());
								bomStructure.put(SystemClassFieldEnum.CHECKOUTUSER.getName() + "NAME", foundationObject.getCheckedOutUser());
								bomStructure.put(SystemClassFieldEnum.OBSOLETEUSER.getName() + "NAME", foundationObject.getObsoleteUser());

								bomStructure.setEnd2ObjectGuid(end2ObjectGuid);
								bomStructure.setTitle(foundationObject.getFullName());

								if (isNew)
								{
									bomStructure.setSequence(addLengthen(length, (i + 1) * increment + ""));
								}
								else
								{
									int sequence = 0;
									if (start > max)
									{
										sequence = start;
									}
									else
									{
										sequence = max + increment;
									}
									max = sequence;
									bomStructure.setSequence(addLengthen(length, sequence + ""));
								}
								bomStructure.setEnd1ObjectGuid(cad.getObjectGuid());
								bomStructure.setViewObjectGuid(bomView.getObjectGuid());
								bomStructure.put("END1_OF_BOMTEMPLATE", bomTemplate);
								ObjectGuid objectGuid = new ObjectGuid();
								objectGuid.setClassName(bomTemplate.getStructureClassName());
								objectGuid.setClassGuid(bomTemplate.getStructureClassGuid());
								bomStructure.setObjectGuid(objectGuid);
								bomStructure.put("IsFromCAD", "Y");
								AclService aclService = DataServer.getAclService();
								if (Constants.isSupervisor(true, this.stubService) && !aclService.hasAuthority(bomView.getObjectGuid(), AuthorityEnum.LINK, sessionId))
								{
									throw new ServiceRequestException("ID_APP_CADTOBOM_ACL", "has no authority");
								}
								this.linkBOM(bomView.getObjectGuid(), foundationObject.getObjectGuid(), bomStructure, true, null, true, false);
								flag = true;
							}
							Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
							if (schedule == null)
							{
								this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
								schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
							}
							CheckConnectUtil util = new CheckConnectUtil(this.stubService.getEMM(), schedule, this.stubService.getUserSignature().getCredential(),
									ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, false);
							if (util.checkConntc(bomView.getEnd1ObjectGuid()))
							{
								throw new DynaDataExceptionAll("connect by error ", null, DataExceptionEnum.DS_CONNECT_BY_ERROR);
							}
							BOMView temBOMView = this.stubService.getBOMView(bomView.getObjectGuid());
							if (temBOMView.isCheckOut())
							{
								this.stubService.checkIn(temBOMView);
							}
//							DataServer.getTransactionManager().commitTransaction();
							return flag;
						}
					}
				}
			}
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

		return false;
	}

	private void checkFoundationFieldRegex(BOMStructure bomStructure) throws ServiceRequestException
	{
		String classGuid = bomStructure.getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(classInfo.getName());
		if (!SetUtils.isNullList(listFieldOfClass))
		{
			for (ClassField field : listFieldOfClass)
			{
				if (field.getType() == FieldTypeEnum.STRING && !StringUtils.isNullString(field.getValidityRegex()))
				{
					Pattern pattern = Pattern.compile(field.getValidityRegex());
					boolean matches = pattern.matcher(StringUtils.convertNULLtoString(bomStructure.get(field.getName()))).matches();
					if (matches)
					{
						UIField uiField = this.stubService.getEMM().getUIFieldByName(classInfo.getName(), field.getName());
						String title = uiField == null ? field.getName() : uiField.getTitle(this.stubService.getUserSignature().getLanguageEnum());
						throw new ServiceRequestException("ID_CLIENT_VALIDATOR_REGEXLEGAL", "field value ilegal.", null, title);
					}
				}
			}
		}
	}

	/**
	 * 加长，补0
	 * 
	 * @param length
	 * @param num
	 * @return
	 */
	public static String addLengthen(int length, String num)
	{
		if (num != null)
		{
			int l = num.length();
			if (l < length)
			{
				for (int i = 0; i < length - l; i++)
				{
					num = "0" + num;
				}
			}
		}

		return num;
	}

	/**
	 * 过滤end2相同的结构，cadtobom用
	 * 
	 */
	private List<StructureObject> listObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition)
	{
		try
		{
			List<StructureObject> temList = this.stubService.getBOAS().listObjectOfRelation(viewObject, searchCondition, null, null);
			if (!SetUtils.isNullList(temList))
			{
				List<String> end2GuidList = new ArrayList<String>();
				Map<String, StructureObject> end2StructureObjectmap = new HashMap<String, StructureObject>();
				List<StructureObject> validList = new ArrayList<StructureObject>();
				for (StructureObject structureObject : temList)
				{
					if (structureObject.getEnd2ObjectGuid().getGuid() != null && !end2GuidList.contains(structureObject.getEnd2ObjectGuid().getGuid()))
					{
						end2GuidList.add(structureObject.getEnd2ObjectGuid().getGuid());
						end2StructureObjectmap.put(structureObject.getEnd2ObjectGuid().getGuid(), structureObject);
					}
					else
					{
						StructureObject oldStructureObject = end2StructureObjectmap.get(structureObject.getEnd2ObjectGuid().getGuid());
						// 处理数量
						// oldStructureObject.get("Quantity");

						Double q = 0.0;
						Double oldQuantity = 1.0;
						Double newQuantity = 1.0;

						if (oldStructureObject.get("Quantity") != null)
						{
							oldQuantity = ((Number) oldStructureObject.get("Quantity")).doubleValue();
						}

						if (structureObject.get("Quantity") != null)
						{
							newQuantity = ((Number) structureObject.get("Quantity")).doubleValue();
						}

						q += oldQuantity;
						q += newQuantity;
						oldStructureObject.put("Quantity", new BigDecimal(q));
						end2StructureObjectmap.put(structureObject.getEnd2ObjectGuid().getGuid(), oldStructureObject);
					}
				}

				Set<String> set = end2StructureObjectmap.keySet();
				for (String s : set)
				{
					validList.add(end2StructureObjectmap.get(s));
				}
				return validList;
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * O11170/：从模型结构到BOM的过程中，物料需要按图号进行排序
	 * 
	 * @param end2cadList
	 * @return
	 */
	private List<FoundationObject> sort(List<FoundationObject> end2cadList)
	{
		Collections.sort(end2cadList, new Comparator<FoundationObject>()
		{

			@Override
			public int compare(FoundationObject o1, FoundationObject o2)
			{
				String id1 = o1.getId();
				String id2 = o2.getId();
				return id1.toLowerCase().compareTo(id2.toLowerCase()) > 0 ? 1 : -1;
			}
		});
		return end2cadList;
	}
}

class UpdateScheduledTask extends AbstractScheduledTask
{
	private ServiceContext		serviceContext	= null;
	private FoundationObject	end1			= null;
	private String				bmGuid			= null;
	private BOMS				boms			= null;

	public UpdateScheduledTask(ServiceContext serviceContext, FoundationObject end1, String bmGuid)
	{
		this.serviceContext = serviceContext;
		this.end1 = end1;
		this.bmGuid = bmGuid;
		this.setPriority(950);
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]UpdateScheduledTask , Scheduled Task Start...");
		try
		{
			this.boms = this.serviceContext.allocatService(BOMS.class);
			List<FoundationObject> end1List = new ArrayList<FoundationObject>();
			end1List.add(end1);
			((BOMSImpl) this.boms).getBomStub().updateUHasBOM(end1List, this.bmGuid);
		}
		catch (Throwable e)
		{
			DynaLogger.error("run update item:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.boms != null)
			{
				this.serviceContext.releaseService(this.boms);
			}
		}
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]UpdateScheduledTask , Scheduled Task End...");
	}
}