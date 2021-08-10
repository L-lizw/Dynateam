/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationLinkStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.CheckConnectUtil;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObjectImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.BuiltinRelationNameEnum;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.RelationTemplateActionEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EOSS;

/**
 * @author Wanglei
 * 
 */
public class RelationLinkStub extends AbstractServiceStub<BOASImpl>
{
	private static TrackerBuilder trackerBuilder = null;

	/**
	 * @param context
	 * @param service
	 */
	public RelationLinkStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	public StructureObject linkInner(ViewObject viewObject, FoundationObject end1FoundationObject, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject,
			boolean isCheckAcl, String procRtGuid) throws ServiceRequestException
	{
		ServiceRequestException returnObj = null;
		String relationTemplateGuid = null;
		ObjectGuid end1ObjectGuid = null;
		try
		{
			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}
			end1ObjectGuid = viewObject.getEnd1ObjectGuid();
			
			if ((SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()) || SystemStatusEnum.PRE.equals(viewObject.getStatus())) && procRtGuid == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			if (end1FoundationObject != null && end1FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end1FoundationObject.getFullName());
			}

			FoundationObject end2FoundationObject = this.stubService.getObjectOfProcrtByGuid(end2FoundationObjectGuid.getGuid(),
					end2FoundationObjectGuid.getClassGuid() == null ? end2FoundationObjectGuid.getClassName() : end2FoundationObjectGuid.getClassGuid(), procRtGuid);
			if (end2FoundationObject != null && end2FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end2FoundationObject.getFullName());
			}
			end2FoundationObjectGuid = end2FoundationObject.getObjectGuid();
			// 不能关联自己
			if (end1FoundationObject.getObjectGuid().getMasterGuid().equals(end2FoundationObjectGuid.getMasterGuid()))
			{
				throw new ServiceRequestException("ID_DS_RELATION_SELF", "cannot link with itself", null, end2FoundationObject.getFullName());
			}

			structureObject.setViewObjectGuid(viewObject.getObjectGuid());
			structureObject.setEnd2ObjectGuid(end2FoundationObjectGuid);
			RelationTemplateInfo relationTemplate = this.stubService.getEMM()
					.getRelationTemplateById(viewObject.get(ViewObject.TEMPLATE_ID) == null ? "" : (String) viewObject.get(ViewObject.TEMPLATE_ID));
			if (relationTemplate != null)
			{
				relationTemplateGuid = relationTemplate.getGuid();
				// DCR规则检查
				List<ObjectGuid> end2ObjectGuidList = new ArrayList<>();
				end2ObjectGuidList.add(end2FoundationObjectGuid);
				viewObject.getTemplateID();
				this.stubService.getDCR().check(viewObject.getEnd1ObjectGuid(), end2ObjectGuidList, relationTemplate.getName(), RuleTypeEnum.RELATION);

				structureObject.getObjectGuid().setClassName(relationTemplate.getStructureClassName());
				List<RelationTemplateEnd2> boInfoList = this.stubService.getEMM().getRelationTemplate(relationTemplate.getGuid()).getRelationTemplateEnd2List();

				boolean end2Auth = false;
				if (!SetUtils.isNullList(boInfoList))
				{
					ClassStub.decorateObjectGuid(end2FoundationObjectGuid, this.stubService);
					for (RelationTemplateEnd2 boInfo : boInfoList)
					{
						BOInfo tempboinfo = this.stubService.getEMM().getCurrentBoInfoByName(boInfo.getEnd2BoName(), false);
						if (tempboinfo.getClassGuid().equals(end2FoundationObjectGuid.getClassGuid()))
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
									if (info.getGuid().equals(end2FoundationObjectGuid.getClassGuid()))
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

				if ("1".equals(relationTemplate.getEnd2Type()))
				{
					end2FoundationObjectGuid.setIsMaster(true);
				}
				else if ("2".equals(relationTemplate.getEnd2Type()))
				{
					end2FoundationObjectGuid.setIsMaster(false);
				}
				else if (relationTemplate.getEnd2Type().equals(RelationTemplateActionEnum.MASTER_REVISION.toString()))
				{
					// saveas,修订 关联最新版本 ,变更2012-11-29
					if (viewObject.getStatus() == SystemStatusEnum.RELEASE)
					{
						end2FoundationObjectGuid.setIsMaster(false);
					}
					else
					{
						end2FoundationObjectGuid.setIsMaster(true);
					}
				}
			}

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(structureObject.getObjectGuid(), this.stubService);

			this.checkFoundationFieldRegex(structureObject);

			String sequence = DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP);
			if (StringUtils.isNullString(structureObject.getSequence()))
			{
				structureObject.setSequence(sequence);
			}

			EOSS eoss = this.stubService.getEOSS();

			// !invoke link.before
			eoss.executeAddBeforeEvent(structureObject);

			StructureObject structureObject_ = DataServer.getRelationService().create(structureObject, "2".equals(relationTemplate.getEnd2Type()),
					Constants.isSupervisor(isCheckAcl, this.stubService), this.stubService.getSignature().getCredential(), this.stubService.getFixedTransactionId());

			eoss.executeAddAfterEvent(structureObject_);

			return structureObject_;

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
			Object[] args = new Object[] { viewObject.getObjectGuid(), end2FoundationObjectGuid };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
			if (end1ObjectGuid != null && StringUtils.isGuid(relationTemplateGuid))
			{
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new UpdateScheduledTask(this.serviceContext, end1ObjectGuid, relationTemplateGuid));
			}
		}
	}

	public StructureObject link(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject, boolean isCheckAcl, String procRtGuid)
			throws ServiceRequestException
	{
		ServiceRequestException returnObj = null;
		structureObject.setViewObjectGuid(viewObjectGuid);
		structureObject.setEnd2ObjectGuid(end2FoundationObjectGuid);
		String sessionId = this.stubService.getUserSignature().getCredential();

		try
		{
			ViewObject viewObject = this.stubService.getRelationStub().getRelation(viewObjectGuid, isCheckAcl);
			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			if ((SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()) || SystemStatusEnum.PRE.equals(viewObject.getStatus())) && procRtGuid == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			FoundationObject end1FoundationObject = this.stubService.getObjectOfProcrtByGuid(viewObject.getEnd1ObjectGuid().getGuid(),
					viewObject.getEnd1ObjectGuid().getClassGuid(), procRtGuid);
			if (end1FoundationObject != null && end1FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end1FoundationObject.getFullName());
			}
			RelationTemplateInfo relationTemplate = this.stubService.getEMM()
					.getRelationTemplateById(viewObject.get(ViewObject.TEMPLATE_ID) == null ? "" : (String) viewObject.get(ViewObject.TEMPLATE_ID));
			if (relationTemplate != null)
			{
				if (relationTemplate.getMaxQuantity() != 0)
				{
					int maxQuantity = DataServer.getRelationService().getEnd2CountOfView(viewObjectGuid.getGuid(), relationTemplate.getGuid());
					if (maxQuantity >= relationTemplate.getMaxQuantity())
					{
						throw new ServiceRequestException("ID_APP_RELATION_TEMPLATE_MAXQUANTITY", "more than max quantity");
					}
				}
				// bug号：I10921---wangwx修改---2014-6-26
				if (BuiltinRelationNameEnum.ITEMCAD3D.toString().equals(relationTemplate.getName())
						|| BuiltinRelationNameEnum.ITEMCAD2D.toString().equals(relationTemplate.getName())
						|| BuiltinRelationNameEnum.ITEMECAD.toString().equals(relationTemplate.getName()))
				{
					List<FoundationObject> listFoundation = this.stubService.listFoundationObjectOfRelation(viewObject.getObjectGuid(), null, null, null, true);
					if (SetUtils.isNullList(listFoundation))
					{
						structureObject.setParmary(true);
					}
				}

			}

			boolean isCheckCycle = false;
			if (relationTemplate.getStructureModel() == RelationTemplateTypeEnum.TREE)
			{
				isCheckCycle = true;
			}
			if (!relationTemplate.isIncorporatedMaster())
			{
				// 不允许有相同的end2
				List<String> end2MasterGuidList = new ArrayList<String>();
				if (!relationTemplate.isIncorporatedMaster())
				{
					List<StructureObject> bomList = DataServer.getRelationService().listSimpleStructureOfRelation(viewObject.getObjectGuid(), relationTemplate.getViewClassGuid(),
							relationTemplate.getStructureClassGuid(), sessionId);
					if (bomList != null)
					{
						for (StructureObject stru : bomList)
						{
							end2MasterGuidList.add(stru.getEnd2ObjectGuid().getMasterGuid());
						}
						if (end2MasterGuidList.contains(structureObject.getEnd2ObjectGuid().getMasterGuid()))
						{
							FoundationObject objectByGuid = this.stubService.getFoundationStub().getObjectByGuid(structureObject.getEnd2ObjectGuid(), false);

							throw new ServiceRequestException("ID_DS_END2_UNIQUE", "end2 is not relation", null, objectByGuid.getFullName());
						}
					}
				}

			}

			StructureObject linkInner = this.linkInner(viewObject, end1FoundationObject, end2FoundationObjectGuid, structureObject, isCheckAcl, procRtGuid);
			if (isCheckCycle)
			{
				Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				if (schedule == null)
				{
					this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
					schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				}
				CheckConnectUtil util = new CheckConnectUtil(this.stubService.getEMM(), schedule, this.stubService.getUserSignature().getCredential(),
						ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, false);
				if (util.checkConntc(viewObject.getEnd1ObjectGuid()))
				{
					throw new DynaDataExceptionAll("connect by error ", null, DataExceptionEnum.DS_CONNECT_BY_ERROR);
				}
			}

			String templateName = relationTemplate.getName();
			boolean isItemCad3D = BuiltinRelationNameEnum.ITEMCAD3D.toString().equals(templateName);

			if (isItemCad3D)
			{
				List<StructureObject> structureList = this.stubService.listObjectOfRelation(end2FoundationObjectGuid, BuiltinRelationNameEnum.CAD3DCAD2D.toString(), null, null,
						null);
				if (!SetUtils.isNullList(structureList))
				{
					ViewObject itemCad2dView = this.stubService.getRelationByEND1(end1FoundationObject.getObjectGuid(), BuiltinRelationNameEnum.ITEMCAD2D.toString());
					if (itemCad2dView == null)
					{
						RelationTemplateInfo itemCad2dTemplate = this.stubService.getEMM().getRelationTemplateByName(end1FoundationObject.getObjectGuid(),
								BuiltinRelationNameEnum.ITEMCAD2D.toString());
						if (itemCad2dTemplate != null)
						{
							itemCad2dView = this.stubService.getRelationStub().saveRelationByTemplate(itemCad2dTemplate.getGuid(), end1FoundationObject.getObjectGuid(), isCheckAcl,
									null);
						}
					}
					if (itemCad2dView != null)
					{
						for (StructureObject str : structureList)
						{
							StructureObject structure2D = this.stubService.newStructureObject(null, StructureObject.CAD_STRUCTURE_CLASS_NAME);
							this.link(itemCad2dView.getObjectGuid(), str.getEnd2ObjectGuid(), structure2D, isCheckAcl, procRtGuid);
						}
					}
				}
			}

			return linkInner;
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
			Object[] args = new Object[] { viewObjectGuid, end2FoundationObjectGuid };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}

	}

	private void checkFoundationFieldRegex(StructureObject structureObject) throws ServiceRequestException
	{
		String classGuid = structureObject.getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(classInfo.getName());
		if (!SetUtils.isNullList(listFieldOfClass))
		{
			for (ClassField field : listFieldOfClass)
			{
				if (field.getType() == FieldTypeEnum.STRING && !StringUtils.isNullString(field.getValidityRegex()))
				{
					Pattern pattern = Pattern.compile(field.getValidityRegex());
					boolean matches = pattern.matcher(StringUtils.convertNULLtoString(structureObject.get(field.getName()))).matches();
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

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);

			trackerBuilder.setTrackerRendererClass(TRViewLinkImpl.class, TrackedDesc.LINK_RELATION);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

	public StructureObject link(ObjectGuid end1Object, ObjectGuid end2Object, StructureObject structureObject, String viewTemplateId) throws ServiceRequestException
	{
		return this.link(end1Object, end2Object, structureObject, viewTemplateId, true);
	}

	public StructureObject link(ObjectGuid end1Object, ObjectGuid end2Object, StructureObject structureObject, String viewName, boolean isCheckAcl) throws ServiceRequestException
	{

		ViewObject viewObject = null;
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1Object, viewName);

		if (relationTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + viewName, null, viewName);
		}
		else
		{
			viewObject = this.stubService.getRelationStub().saveRelationByTemplate(relationTemplate.getGuid(), end1Object, isCheckAcl, null);
		}
		return this.link(viewObject.getObjectGuid(), end2Object, structureObject, isCheckAcl, null);

	}

	public StructureObject link(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, String procRtGuid, StructureObject structureObject) throws ServiceRequestException
	{
		return this.link(viewObjectGuid, end2FoundationObjectGuid, structureObject, true, procRtGuid);
	}

	protected void batchLink(ObjectGuid viewObjectGuid, List<ObjectGuid> end2FoundationObjectGuidList, List<StructureObject> structureObjectList, String procRtGuid)
			throws ServiceRequestException
	{
		if (SetUtils.isNullList(end2FoundationObjectGuidList))
		{
			return;
		}

		if (SetUtils.isNullList(structureObjectList))
		{
			return;
		}

		if (viewObjectGuid == null)
		{
			return;
		}

		if (end2FoundationObjectGuidList.size() != structureObjectList.size())
		{
			return;
		}

		for (int i = 0; i < end2FoundationObjectGuidList.size(); i++)
		{
			this.link(viewObjectGuid, end2FoundationObjectGuidList.get(i), procRtGuid, structureObjectList.get(i));
		}

	}

	protected void editRelation(ObjectGuid end1ObjectGuid, String templateName, List<ObjectGuid> end2FoundationObjectGuidList, List<StructureObject> structureObjectList,
			String procRtGuid, boolean isReplace) throws ServiceRequestException
	{
		try
		{
			if (SetUtils.isNullList(end2FoundationObjectGuidList))
			{
				return;
			}

			if (SetUtils.isNullList(structureObjectList))
			{
				return;
			}

			if (end2FoundationObjectGuidList.size() != structureObjectList.size())
			{
				return;
			}
			ViewObject viewObject = null;
			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, templateName);
			if (relationTemplate == null)
			{
				throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + templateName, null, templateName);
			}
			boolean isEnd2Unique = !relationTemplate.isIncorporatedMaster();
			Set<String> end2MasterSet = new HashSet<String>();
			String sequence = DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP);
			long seqLong = Long.parseLong(sequence);
			for (int i = 0; i < end2FoundationObjectGuidList.size(); i++)
			{
				StructureObject newStrus = structureObjectList.get(i);
				if (StringUtils.isNullString(newStrus.getSequence()))
				{
					newStrus.setSequence(String.valueOf(seqLong + i));
				}
				if (isEnd2Unique)
				{
					if (end2MasterSet.contains(end2FoundationObjectGuidList.get(i).getGuid()))
					{
						throw new DynaDataExceptionAll("End2 unique.", null, DataExceptionEnum.DS_END2_UNIQUE);
					}
					end2MasterSet.add(end2FoundationObjectGuidList.get(i).getGuid());
				}
			}
			viewObject = this.stubService.getRelationStub().saveRelationByTemplate(relationTemplate.getGuid(), end1ObjectGuid, true, null);

			FoundationObject end1FoundationObject = this.stubService.getObjectOfProcrtByGuid(viewObject.getEnd1ObjectGuid().getGuid(),
					viewObject.getEnd1ObjectGuid().getClassGuid(), procRtGuid);

			if (end1FoundationObject != null && end1FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end1FoundationObject.getFullName());
			}
			SearchCondition tempSC = SearchConditionFactory.createSearchConditionForStructure(relationTemplate.getStructureClassName());
			List<ClassField> feildList = this.stubService.getEMM().listFieldOfClass(relationTemplate.getStructureClassName());
			for (ClassField field : feildList)
			{
				tempSC.addResultField(field.getName());
			}
			List<StructureObject> oldStructureList = this.stubService.getRelationStub().listObjectOfRelation(viewObject.getObjectGuid(), null, null, null, false);
			int end2Count = end2FoundationObjectGuidList.size();
			if (relationTemplate.getMaxQuantity() != 0)
			{
				if (end2Count > relationTemplate.getMaxQuantity())
				{
					throw new ServiceRequestException("ID_APP_RELATION_TEMPLATE_MAXQUANTITY", "more than max quantity");
				}
			}
			if (!SetUtils.isNullList(oldStructureList))
			{
				List<StructureObject> changedStructureObjects = new ArrayList<StructureObject>();
				for (int i = 0; i < end2FoundationObjectGuidList.size(); i++)
				{
					StructureObject newStrus = structureObjectList.get(i);
					StructureObject oldStrus = this.getStructureObject(oldStructureList, newStrus, end2FoundationObjectGuidList.get(i));
					if (oldStrus != null)
					{
						if (oldStrus.isChanged())
						{
							changedStructureObjects.add(oldStrus);
						}
					}
					else
					{
						this.linkInner(viewObject, end1FoundationObject, end2FoundationObjectGuidList.get(i), structureObjectList.get(i), true, procRtGuid);
					}
				}
				// 交换顺序号
				for (StructureObject changedStructureObject : changedStructureObjects)
				{
					changedStructureObject.setSequence(changedStructureObject.getSequence() + "t");
					this.stubService.getStructureStub().saveStructureInner(viewObject, changedStructureObject, relationTemplate, StringUtils.isNullString(procRtGuid));
				}
				for (StructureObject changedStructureObject : changedStructureObjects)
				{
					changedStructureObject.setSequence(changedStructureObject.getSequence().substring(0, changedStructureObject.getSequence().length() - 1));
					this.stubService.getStructureStub().saveStructureInner(viewObject, changedStructureObject, relationTemplate, StringUtils.isNullString(procRtGuid));
				}
				if (isReplace)
				{
					for (int i = 0; i < oldStructureList.size(); i++)
					{
						StructureObject oldStrus = oldStructureList.get(i);
						this.stubService.getRelationUnlinkStub().unlinkInner(viewObject, oldStrus, StringUtils.isNullString(procRtGuid));
					}
				}
				else
				{
					end2Count = end2Count + oldStructureList.size();
					if (relationTemplate.getMaxQuantity() != 0)
					{
						if (end2Count > relationTemplate.getMaxQuantity())
						{
							throw new ServiceRequestException("ID_APP_RELATION_TEMPLATE_MAXQUANTITY", "more than max quantity");
						}
					}
				}
			}
			else
			{
				for (int i = 0; i < end2FoundationObjectGuidList.size(); i++)
				{
					this.linkInner(viewObject, end1FoundationObject, end2FoundationObjectGuidList.get(i), structureObjectList.get(i), true, procRtGuid);
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw ServiceRequestException.createByException("ID_APP_CAD_UNKNOW_ERROR", e);
		}
	}

	private StructureObject getStructureObject(List<StructureObject> oldStructureList, StructureObject newStru, ObjectGuid objectGuid)
	{
		if (oldStructureList != null)
		{
			for (int i = 0; i < oldStructureList.size(); i++)
			{
				StructureObject oldStru = oldStructureList.get(i);
				if (newStru.getObjectGuid() == null || StringUtils.isNullString(newStru.getObjectGuid().getGuid()))
				{
					if (oldStru.getEnd2ObjectGuid().getMasterGuid().equalsIgnoreCase(objectGuid.getMasterGuid())
							|| oldStru.getEnd2ObjectGuid().getGuid().equalsIgnoreCase(objectGuid.getGuid()))
					{
						oldStructureList.remove(i);
						for (String key : ((DynaObjectImpl) newStru).keySet())
						{
							if (!key.contains("$"))
							{
								oldStru.put(key, newStru.get(key));
							}
						}
						return oldStru;
					}
				}
				else if (newStru.getObjectGuid().getGuid().equalsIgnoreCase(oldStru.getObjectGuid().getGuid()))
				{
					oldStructureList.remove(i);
					return newStru;
				}
			}
		}
		return null;
	}
}

class UpdateScheduledTask extends AbstractScheduledTask
{
	private ServiceContext	serviceContext			= null;
	private ObjectGuid		end1Object				= null;
	private String			relationTemplateGuid	= null;
	private BOAS			boas					= null;

	public UpdateScheduledTask(ServiceContext serviceContext, ObjectGuid end1Object, String relationTemplateGuid)
	{
		this.serviceContext = serviceContext;
		this.end1Object = end1Object;
		this.relationTemplateGuid = relationTemplateGuid;
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]UpdateScheduledTask , Scheduled Task Start...");
		try
		{
			this.boas = this.serviceContext.allocatService(BOAS.class);
			((BOASImpl) this.boas).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			if (this.end1Object != null)
			{
				this.boas.updateHasEnd2Flg(this.end1Object, this.relationTemplateGuid);
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error("run update item:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.boas != null)
			{
				this.serviceContext.releaseService(this.boas);
			}
		}
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]UpdateScheduledTask , Scheduled Task End...");
	}
}