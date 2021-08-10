/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与FoundationObject相关的基本操作分支
 * Caogc 2010-8-19
 */
package dyna.app.service.brs.boas;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.acl.ACLImpl;
import dyna.app.service.brs.boas.numbering.ClassificationAllocate;
import dyna.app.service.brs.boas.numbering.NumberAllocate;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.brm.BRMImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.brs.pos.POSImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.Criterion;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.*;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.extra.OpenInstanceModel;
import dyna.common.dto.Folder;
import dyna.common.dto.Search;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.acl.AclService;
import dyna.data.service.folder.FolderService;
import dyna.data.service.ins.InstanceService;
import dyna.data.service.model.interfacemodel.InterfaceModelService;
import dyna.data.service.relation.RelationService;
import dyna.data.service.tool.DSToolService;
import dyna.data.service.wf.WorkFlowService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.BRM;
import dyna.net.service.brs.EMM;
import org.acegisecurity.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

/**
 * 与FoundationObject相关的基本操作分支
 * 
 * @author Caogc
 * 
 */
public class FoundationStub extends AbstractServiceStub<BOASImpl>
{
	private static TrackerBuilder	startTrackerBuilder		= null;
	private static TrackerBuilder	stopTrackerBuilder		= null;
	private static TrackerBuilder	obsoleteTrackerBuilder	= null;

	/**
	 * @param context
	 * @param service
	 */
	protected FoundationStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	protected void deleteObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		this.deleteObject(foundationObject, true);
	}

	public void deleteObject(FoundationObject foundationObject, boolean isCheckAuth) throws ServiceRequestException
	{
		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setClassGuid(foundationObject.getObjectGuid().getClassGuid());
		objectGuid.setClassName(foundationObject.getObjectGuid().getClassName());
		objectGuid.setGuid(foundationObject.getObjectGuid().getGuid());
		objectGuid.setMasterGuid(foundationObject.getObjectGuid().getMasterGuid());

		String sessionId = this.stubService.getSignature().getCredential();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(foundationObject.getObjectGuid().getClassGuid());

		List<FoundationObject> end1List = new ArrayList<>();

		boolean deleteReplace = false;

		try
		{
			if (!foundationObject.isShortcut() && foundationObject.getCheckedOutTime() != null)
			{
				throw new ServiceRequestException("ID_APP_CHECKEDOUT_CANNOT_DELETE", "checkedout object can't delete", null, foundationObject.getId());
			}
			if (!foundationObject.isShortcut())
			{
				// important! invoke add.before event.
				this.stubService.getEOSS().executeDeleteBeforeEvent(foundationObject);
			}

			List<String> bomTemplateNameList = new ArrayList<>();
			if (classInfo.hasInterface(ModelInterfaceEnum.IItem))
			{
				List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByEND2(foundationObject.getObjectGuid());
				if (!SetUtils.isNullList(bomTemplateList))
				{
					for (BOMTemplateInfo bomTemplate : bomTemplateList)
					{
						if (bomTemplateNameList.contains(bomTemplate.getName()))
						{
							continue;
						}
						bomTemplateNameList.add(bomTemplate.getName());

						// 模板为精确 或者 删除的实例对象是第一版的才需要查找end1
						if (bomTemplate.getPrecise() == BomPreciseType.PRECISE
								|| ((Number) foundationObject.get(SystemClassFieldEnum.REVISIONIDSEQUENCE.getName())).intValue() == 1)
						{
							List<FoundationObject> tmpList = this.stubService.getBOMS().listWhereUsed(objectGuid, bomTemplate.getName(), null, null, null, true);
							if (!SetUtils.isNullList(tmpList))
							{
								end1List.addAll(tmpList);
							}
						}
					}
				}
			}
			else if (classInfo.hasInterface(ModelInterfaceEnum.IBOMView))
			{
				BOMView bomView = new BOMView(foundationObject);
				List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByEND1(bomView.getEnd1ObjectGuid());
				if (!SetUtils.isNullList(bomTemplateList))
				{
					FoundationObject end1 = this.getObject(bomView.getEnd1ObjectGuid());
					if (end1 != null)
					{
						end1List.add(end1);
					}
				}
			}

			if (!foundationObject.isShortcut())
			{
				EMM emm = this.stubService.getEMM();

				// 处理relation
				// 查找所有关联的ViewObject
				List<ViewObject> viewObjectList = this.stubService.listRelation(foundationObject.getObjectGuid());

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

						if (RelationTemplateActionEnum.NONE.equals(relationTemplate.getDeleteTrigger()))
						{
							// 不处理end2
							continue;
						}
						else if (RelationTemplateActionEnum.DELETE.equals(relationTemplate.getDeleteTrigger()))
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
												this.deleteObject(end2FoundationObject, isCheckAuth);
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
			}

			boolean isShortCut = foundationObject.isShortcut();
			if (isShortCut)
			{
				String folderGuid = (String) foundationObject.get("SHORTCUTFOLDERGUID");
				boolean hasUserAuthorize4Lib = ((ACLImpl) this.stubService.getACL()).getFolderACLStub().hasUserAuthorize4Lib(folderGuid, isCheckAuth);
				DataServer.getInstanceService().delete(foundationObject, !hasUserAuthorize4Lib, sessionId, this.stubService.getFixedTransactionId());
			}
			else
			{
				DataServer.getInstanceService().delete(foundationObject, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId,
						this.stubService.getFixedTransactionId());
			}

			if (classInfo.hasInterface(ModelInterfaceEnum.IItem))
			{
				deleteReplace = true;
			}

			String exceptionParameter = foundationObject.getId();
			if (classInfo.hasInterface(ModelInterfaceEnum.IBOMView) || classInfo.hasInterface(ModelInterfaceEnum.IViewObject))
			{
				exceptionParameter = foundationObject.getName();
			}

			String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new DeleteScheduledTask(serviceContext, objectGuid, end1List, bmGuid, exceptionParameter, deleteReplace, stubService.getUserSignature()));

			// invoke delete.after event.
			this.stubService.getEOSS().executeDeleteAfterEvent(foundationObject);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
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
	}

	@SuppressWarnings("unchecked")
	protected String getFoundationObjectViewValue(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid == null || objectGuid.getClassGuid() == null && objectGuid.getClassName() == null)
		{
			return null;
		}

		EMM emm = this.stubService.getEMM();
		ClassInfo classInfo = null;
		if (objectGuid.getClassGuid() != null)
		{
			classInfo = emm.getClassByGuid(objectGuid.getClassGuid());
		}
		else
		{
			classInfo = emm.getClassByName(objectGuid.getClassName());
		}

		if (classInfo == null)
		{
			return null;
		}

		Map<String, Object> objectMap = null;

		if (classInfo.hasInterface(ModelInterfaceEnum.IUser))
		{
			objectMap = this.stubService.getAAS().getUser(objectGuid.getGuid());
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IGroup))
		{
			objectMap = this.stubService.getAAS().getGroup(objectGuid.getGuid());
		}
		// 2012-10-29项目管理变更，增加接口IPMRole，IPMCalendar
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			objectMap = this.stubService.getPPMS().getWorkCalendar(objectGuid.getGuid());
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
		{
			objectMap = this.stubService.getPPMS().getProjectRole(objectGuid.getGuid());
		}
		else
		{
			// no need authorized.
			objectMap = (Map<String, Object>) this.getObject(objectGuid, false);
		}

		if (objectMap == null)
		{
			return null;
		}
		String instanceString = classInfo.getInstanceString();

		if (instanceString == null)
		{
			if (classInfo.hasInterface(ModelInterfaceEnum.IUser) || classInfo.hasInterface(ModelInterfaceEnum.IGroup))
			{
				return objectMap.get("ID") + "-" + objectMap.get("NAME");
			}
			else
			{
				return objectMap.get(SystemClassFieldEnum.ID.getName()) + "-" + objectMap.get(SystemClassFieldEnum.NAME.getName());
			}
		}

		String renString = "";
		String[] instanceArr = instanceString.split("\\+");
		if (classInfo.hasInterface(ModelInterfaceEnum.IUser) || classInfo.hasInterface(ModelInterfaceEnum.IGroup))
		{
			for (String str : instanceArr)
			{
				if (str.contains("\""))
				{
					renString = renString + str.replaceAll("\"", "");
				}
				else
				{
					renString = (renString == "" ? "" : renString) + (objectMap.get(str.toUpperCase()) == null ? "" : (String) objectMap.get(str.toUpperCase()));
				}
			}
		}
		else
		{
			for (String str : instanceArr)
			{
				if (str.contains("\""))
				{
					renString = renString + str.replaceAll("\"", "");
				}
				else
				{
					ClassField classField = this.stubService.getEMM().getFieldByName(classInfo.getName(), str, true);
					if (classField == null)
					{
						continue;
					}

					String classFieldName = classField.getName();
					if (FieldTypeEnum.CLASSIFICATION == classField.getType())
					{
						classFieldName = classFieldName + "TITLE";
					}
					Object title = objectMap.get(classFieldName);
					String titleValue = null;
					if (FieldTypeEnum.DATE == classField.getType())
					{
						titleValue = DateFormat.formatYMD((Date) title);
					}
					else if (FieldTypeEnum.DATETIME == classField.getType())
					{
						titleValue = DateFormat.formatYMDHMS((Date) title);
					}
					else if (FieldTypeEnum.STATUS == classField.getType())
					{
						titleValue = this.stubService.getMSRM().getMSRString(SystemStatusEnum.getStatusEnum((String) title).getMsrId(),
								this.stubService.getUserSignature().getLanguageEnum().toString());
					}
					else if (FieldTypeEnum.CLASSIFICATION == classField.getType())
					{
						titleValue = StringUtils.getMsrTitle((String) title, this.stubService.getUserSignature().getLanguageEnum().getType());
					}
					else
					{
						if (title instanceof BigDecimal)
						{
							titleValue = title.toString();
						}
						else
						{
							titleValue = (String) title;
						}
					}

					renString = renString + (titleValue == null ? "" : titleValue);

				}
			}

		}

		while (renString.endsWith("-"))
		{
			renString = renString.substring(0, renString.length() - 1);
		}

		return renString;
	}

	protected FoundationObject getObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		return this.getObject(objectGuid, bmGuid, UITypeEnum.FORM, Constants.isSupervisor(true, this.stubService));

	}

	public FoundationObject getObjectWithoutDecorate(ObjectGuid objectGuid) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		List<UIObjectInfo> uiObjectList = ((EMMImpl) this.stubService.getEMM()).getUIStub().listUIObjectByBizModel(objectGuid.getClassName(), bmGuid, UITypeEnum.FORM, true);
		UIObjectInfo[] uiObjects = null;
		if (!SetUtils.isNullList(uiObjectList))
		{
			uiObjects = new UIObjectInfo[uiObjectList.size()];
			int i = 0;
			for (UIObjectInfo uiObject : uiObjectList)
			{
				uiObjects[i++] = uiObject;
			}
		}

		return this.getObject(objectGuid, bmGuid, Constants.isSupervisor(true, this.stubService), false, uiObjects);
	}

	protected FoundationObject getObject(ObjectGuid objectGuid, String sharedFolderGuid) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();
		boolean hasRelation = ds.hasRelation(objectGuid.getGuid(), sharedFolderGuid);
		if (!hasRelation)
		{
			throw new ServiceRequestException("ID_APP_NOT_IN_FOLDER", "access denied, cause object not in folder");
		}

		FoundationObject retObject = this.getObject(objectGuid, false);
		return retObject;
	}

	public FoundationObject getObject(ObjectGuid objectGuid, boolean isNeedAuthority) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		return this.getObject(objectGuid, bmGuid, UITypeEnum.FORM, isNeedAuthority);
	}

	public FoundationObject getObject(ObjectGuid objectGuid, String uiName, boolean isNeedAuthority) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		UIObjectInfo uiObject = ((EMMImpl) this.stubService.getEMM()).getUIObjectByName(objectGuid.getClassName(), uiName);
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		return this.getObject(objectGuid, bmGuid, isNeedAuthority, false, uiObject);
	}

	protected FoundationObject getObject(ObjectGuid objectGuid, String bmGuid, UITypeEnum uiType, boolean isNeedAuthority) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		// if (uiType.equals(UITypeEnum.FORM))
		// {
		List<UIObjectInfo> uiObjectList = ((EMMImpl) this.stubService.getEMM()).getUIStub().listUIObjectByBizModel(objectGuid.getClassName(), bmGuid, uiType, true);
		// ((EMMImpl) this.stubService.getEMM()).getUIStub().listFormUIObjectInBizModel(objectGuid.getClassName(),
		// bmGuid);
		UIObjectInfo[] uiObjects = null;
		if (!SetUtils.isNullList(uiObjectList))
		{
			uiObjects = new UIObjectInfo[uiObjectList.size()];
			int i = 0;
			for (UIObjectInfo uiObject : uiObjectList)
			{
				uiObjects[i++] = uiObject;
			}
		}
		// }
		// else
		// {
		// get ui model object for this object.
		// uiObjects = new UIObject[1];
		// uiObjects[0] = UIStub.getUIObjectByBizModel(objectGuid.getClassName(), bmGuid, uiType);

		// }

		return this.getObject(objectGuid, bmGuid, isNeedAuthority, false, uiObjects);
	}

	private FoundationObject getObject(ObjectGuid objectGuid, String bmGuid, boolean isNeedAuthority, boolean isWithoutDecorate, UIObjectInfo... uiObjects)
			throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			InstanceService ds = DataServer.getInstanceService();

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);

			if (uiObjects != null)
			{
				for (UIObjectInfo uiObject : uiObjects)
				{
					if (uiObject == null)
					{
						continue;
					}
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			// searchCondition.setPageStartRowIdx(0);
			// searchCondition.setPageForward(true);
			searchCondition.setPageNum(1);
			searchCondition.setPageSize(1);
			// searchCondition.setLatestOnly(false);
			if (objectGuid != null && !StringUtils.isGuid(objectGuid.getGuid()) && objectGuid.isMaster())
			{
				searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
			}
			else
			{
				searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);
			}

			InterfaceModelService cs = DataServer.getInterfaceModelService();
			ClassInfo classInfo = this.stubService.getEMM().getClassByName(objectGuid.getClassName());
			if (classInfo != null)
			{
				List<ModelInterfaceEnum> interfaceList = classInfo.getInterfaceList();
				if (interfaceList != null)
				{
					for (ModelInterfaceEnum modelInterfaceEnum : interfaceList)
					{
						List<ClassField> listClassFieldInInterface = cs.listClassFieldOfInterface(modelInterfaceEnum);

						if (listClassFieldInInterface != null)
						{
							for (ClassField classField : listClassFieldInInterface)
							{
								searchCondition.addResultField(classField.getName());
							}
						}
					}
				}
			}
			if (StringUtils.isNullString(objectGuid.getGuid()) && !StringUtils.isNullString(objectGuid.getMasterGuid()))
			{
				try
				{
					FoundationObject fo = ds.getWipSystemFieldInfoByMaster(objectGuid.getMasterGuid(), objectGuid.getClassGuid(), false, sessionId);
					if (fo == null)
					{
						return null;
					}
					objectGuid = fo.getObjectGuid();
				}
				catch (Exception e)
				{
					return null;
				}
			}
			FoundationObject retObject = ds.getFoundationObject(objectGuid, Constants.isSupervisor(isNeedAuthority, this.stubService), sessionId);

			if (retObject == null)
			{
				return null;
			}

			this.stubService.getClassificationStub().makeClassificationFoundation(retObject, searchCondition, bmGuid, null);

			EMM emm = this.stubService.getEMM();

			if (!isWithoutDecorate)
			{
				DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), retObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(emm.getCodeFieldNamesInSC(searchCondition), retObject, emm, bmGuid);
				retObject.resetObjectGuid();

				DecoratorFactory.ofd.decorateWithField(emm.getObjectFieldNamesInSC(searchCondition), retObject, emm, sessionId, false);
			}
			retObject.resetObjectGuid();

			return retObject;
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	public FoundationObject getObject4ObjectField(ObjectGuid objectGuid) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		UIObjectInfo uiObject = ((EMMImpl) this.stubService.getEMM()).getUIStub().getUIObjectByBizModel(objectGuid.getClassName(), bmGuid, UITypeEnum.SECTION);

		if (uiObject != null)
		{
			return null;
		}

		return this.getObject(objectGuid, UITypeEnum.SECTION, false);
	}

	public List<FoundationObject> getObject4ObjectField(List<ObjectGuid> objectGuidList) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		ObjectGuid objectGuid = objectGuidList.get(0);
		UIObjectInfo uiObject = ((EMMImpl) this.stubService.getEMM()).getUIStub().getUIObjectByBizModel(objectGuid.getClassName(), bmGuid, UITypeEnum.SECTION);
		if (uiObject != null)
		{
			return null;
		}

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		List<UIObjectInfo> uiObjectList = ((EMMImpl) this.stubService.getEMM()).getUIStub().listUIObjectByBizModel(objectGuid.getClassName(), bmGuid, UITypeEnum.SECTION, true);
		UIObjectInfo[] uiObjects = null;
		if (!SetUtils.isNullList(uiObjectList))
		{
			uiObjects = new UIObjectInfo[uiObjectList.size()];
			int i = 0;
			for (UIObjectInfo uiObject_ : uiObjectList)
			{
				uiObjects[i++] = uiObject_;
			}
		}

		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		try
		{
			int mod = (int) Math.ceil((double) objectGuidList.size() / (double) 50);
			for (int i = 0; i < mod; i++)
			{
				int endIndex = (i + 1) * 50;
				if (objectGuidList.size() < endIndex)
				{
					endIndex = objectGuidList.size();
				}
				List<ObjectGuid> subList = objectGuidList.subList(i * 50, endIndex);

				resultList.addAll(this.batchQueryByGuid(subList, uiObjects));
			}

			return resultList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<FoundationObject> batchQueryByGuid(List<ObjectGuid> objectGuidList, UIObjectInfo[] uiObjects) throws ServiceRequestException
	{
		try
		{
			String sessionId = this.stubService.getSignature().getCredential();
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			ObjectGuid objectGuid = objectGuidList.get(0);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			InstanceService ds = DataServer.getInstanceService();

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(objectGuid.getClassName(), null, false);

			if (uiObjects != null)
			{
				for (UIObjectInfo uiObject_ : uiObjects)
				{
					if (uiObject_ == null)
					{
						continue;
					}
					searchCondition.addResultUIObjectName(uiObject_.getName());
				}
			}

			searchCondition.setPageNum(1);
			searchCondition.setPageSize(objectGuidList.size());
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);
			for (ObjectGuid objectGuid_ : objectGuidList)
			{
				searchCondition.addFilterWithOR(SystemClassFieldEnum.GUID.getName(), objectGuid_.getGuid(), OperateSignEnum.EQUALS);
			}

			InterfaceModelService cs = DataServer.getInterfaceModelService();
			ClassInfo classInfo = this.stubService.getEMM().getClassByName(objectGuid.getClassName());
			if (classInfo != null)
			{
				List<ModelInterfaceEnum> interfaceList = classInfo.getInterfaceList();
				if (interfaceList != null)
				{
					for (ModelInterfaceEnum modelInterfaceEnum : interfaceList)
					{
						List<ClassField> listClassFieldInInterface = cs.listClassFieldOfInterface(modelInterfaceEnum);

						if (listClassFieldInInterface != null)
						{
							for (ClassField classField : listClassFieldInInterface)
							{
								searchCondition.addResultField(classField.getName());
							}
						}
					}
				}
			}

			List<FoundationObject> results = ds.query(searchCondition, Constants.isSupervisor(false, this.stubService), sessionId);
			if (SetUtils.isNullList(results))
			{
				return null;
			}

			for (FoundationObject foundationObject : results)
			{
				this.stubService.getClassificationStub().makeClassificationFoundation(foundationObject, searchCondition, bmGuid, null);

				EMM emm = this.stubService.getEMM();

				DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), foundationObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(emm.getCodeFieldNamesInSC(searchCondition), foundationObject, emm, bmGuid);

				foundationObject.resetObjectGuid();
			}

			EMM emm = this.stubService.getEMM();
			DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), results, emm, sessionId);

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

	public FoundationObject getObject(ObjectGuid objectGuid, UITypeEnum uiType, boolean isNeedAuthority) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		return this.getObject(objectGuid, bmGuid, uiType, isNeedAuthority);
	}

	public FoundationObject getObjectByGuid(ObjectGuid objectGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		try
		{
			InstanceService ds = DataServer.getInstanceService();

			FoundationObject foundationObject = null;
			String classGuidOrClassName = StringUtils.isNullString(objectGuid.getClassGuid()) ? objectGuid.getClassName() : objectGuid.getClassGuid();
			if (StringUtils.isNullString(classGuidOrClassName))
			{
				throw new ServiceRequestException("ID_SDS_GET_MA_CLASS", "classname or classguid cannot be null!");
			}

			foundationObject = ds.getSystemFieldInfo(objectGuid.getGuid(), classGuidOrClassName, Constants.isSupervisor(isCheckAuth, this.stubService),
					this.stubService.getSignature().getCredential());

			DecoratorFactory.decorateFoundationObject(null, foundationObject, this.stubService.getEMM(), this.stubService.getUserSignature().getLoginGroupBMGuid(), null);
			DecoratorFactory.ofd.decorateWithField(null, foundationObject, this.stubService.getEMM(), this.stubService.getSignature().getCredential(), false);

			return foundationObject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			e.printStackTrace();
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	protected List<FoundationObject> quickQuery(String searchKey, int rowCntPerPate, int pageNum, boolean caseSensitive, boolean isEquals, boolean isOnlyId,
			List<String> boNameList) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();
		try
		{
			List<FoundationObject> list = ds.quickQuery(searchKey, rowCntPerPate, pageNum, caseSensitive, isEquals, isOnlyId, boNameList,
					Constants.isSupervisor(true, this.stubService), this.stubService.getSignature().getCredential());
			if (!SetUtils.isNullList(list))
			{
				for (FoundationObject foundationObject : list)
				{
					DecoratorFactory.decorateFoundationObject(null, foundationObject, this.stubService.getEMM(), this.stubService.getEMM().getCurrentBizModel().getGuid(), null);
				}
			}
			DecoratorFactory.decorateFoundationObject(null, list, this.stubService.getEMM(), this.stubService.getSignature().getCredential());
			return list;
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

	protected List<FoundationObject> listObject(SearchCondition condition) throws ServiceRequestException
	{
		return this.listObject(condition, true);
	}

	public List<FoundationObject> listObject(SearchCondition condition, boolean isCheckAuth) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(condition.getObjectGuid(), this.stubService);

			condition = this.dealWithSearchCondition(condition);

			List<FoundationObject> results = ds.query(condition, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);

			if (SetUtils.isNullList(results))
			{
				return results;
			}

			EMM emm = this.stubService.getEMM();

			Set<String> fieldNames = emm.getObjectFieldNamesInSC(condition);
			Set<String> fieldCodeNames = emm.getCodeFieldNamesInSC(condition);
			Folder folder = condition.getFolder();

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			for (FoundationObject fObject : results)
			{
				// fObject.getObjectGuid().setIsMaster(condition.isMaster());
				DecoratorFactory.decorateFoundationObject(fieldNames, fObject, emm, bmGuid, folder);
				DecoratorFactory.decorateFoundationObjectCode(fieldCodeNames, fObject, emm, bmGuid);
				String classificationGuid = condition.getClassification();
				if (!StringUtils.isNullString(classificationGuid))
				{
					List<FoundationObject> restoreAllClassification = fObject.restoreAllClassification(true);
					if (!SetUtils.isNullList(restoreAllClassification))
					{
						for (FoundationObject classificationFoundation : restoreAllClassification)
						{
							this.stubService.getClassificationStub().decorateClassification(classificationFoundation, bmGuid, classificationFoundation.getClassificationGuid());
						}
					}
				}

			}

			DecoratorFactory.decorateFoundationObject(fieldNames, results, emm, sessionId);

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

	/**
	 * 处理检索条件中有没有日期对应的"在"所对应的时间段，如：今天，本周，本月，昨天，上周，上月，明天，下周，下月。
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 */
	public SearchCondition dealWithSearchCondition(SearchCondition condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			return null;
		}

		DateRangeEnum rangeCond = null;
		// 处理检索条件中有没有日期对应的"在"所对应的时间段，如：今天，本周，本月，昨天，上周，上月，明天，下周，下月。
		List<Criterion> criterionList = condition.getCriterionList();
		if (!SetUtils.isNullList(criterionList))
		{
			Date todayDate = null;
			Date fromDate = null;
			Date toDate = null;
			Calendar today = Calendar.getInstance();
			for (int i = 0; i < criterionList.size(); i++)
			{
				Criterion criterion = criterionList.get(i);
				if (criterion.getValue() == null)
				{
					continue;
				}

				rangeCond = null;
				if (criterion.getValue() instanceof String)
				{
					try
					{
						rangeCond = DateRangeEnum.valueOf((String) criterion.getValue());
					}
					catch (Exception e)
					{
					}
				}
				else if (criterion.getValue() instanceof DateRangeEnum)
				{
					rangeCond = (DateRangeEnum) criterion.getValue();
				}

				if (rangeCond == null)
				{
					continue;
				}

				if (todayDate == null)
				{
					todayDate = this.stubService.getEMM().getSystemDate();
				}
				today.setTime(todayDate);
				fromDate = null;
				toDate = null;
				switch (rangeCond)
				{
				case TODAY:
					fromDate = this.getDayBeginTime(today);
					toDate = this.getDayEndTime(today);

					break;
				case THISWEEK:
					today.setTime(DateFormat.parse(DateFormat.getMondayOFWeek(today.getTime())));
					fromDate = this.getDayBeginTime(today);

					today.setTime(DateFormat.parse(DateFormat.getSundayOFWeek(todayDate)));
					toDate = this.getDayEndTime(today);

					break;
				case THISMONTH:
					today.setTime(DateFormat.parse(DateFormat.getFirstDayOfMonth(today.getTime())));
					fromDate = this.getDayBeginTime(today);

					today.setTime(DateFormat.parse(DateFormat.getLastDayOfMonth(todayDate)));
					toDate = this.getDayEndTime(today);

					break;
				case YESTERDAY:
					today.add(Calendar.DATE, -1);
					fromDate = this.getDayBeginTime(today);
					toDate = this.getDayEndTime(today);

					break;
				case LASTWEEK:
					today.setTime(DateFormat.parse(DateFormat.getMondayOFLastWeek(today.getTime())));
					fromDate = this.getDayBeginTime(today);

					today.setTime(DateFormat.parse(DateFormat.getSundayOFLastWeek(todayDate)));
					toDate = this.getDayEndTime(today);

					break;
				case LASTMONTH:
					today.setTime(DateFormat.parse(DateFormat.getFirstDayOfLastMonth(today.getTime())));
					fromDate = this.getDayBeginTime(today);

					today.setTime(DateFormat.parse(DateFormat.getLastDayOfLastMonth(todayDate)));
					toDate = this.getDayEndTime(today);

					break;
				case NEXTWEEK:
					today.setTime(DateFormat.parse(DateFormat.getMondayOFNextWeek(today.getTime())));
					fromDate = this.getDayBeginTime(today);

					today.setTime(DateFormat.parse(DateFormat.getSundayOFNextWeek(todayDate)));
					toDate = this.getDayEndTime(today);

					break;
				case NEXTMONTH:
					today.setTime(DateFormat.parse(DateFormat.getFirstDayOfNextMonth(today.getTime())));
					fromDate = this.getDayBeginTime(today);

					today.setTime(DateFormat.parse(DateFormat.getLastDayOfLastNextMonth(todayDate)));
					toDate = this.getDayEndTime(today);

					break;
				case TOMORROW:
					today.add(Calendar.DATE, 1);
					fromDate = this.getDayBeginTime(today);
					toDate = this.getDayEndTime(today);

					break;

				default:
					break;
				}
				if (fromDate == null || toDate == null)
				{
					continue;
				}

				this.formCriterion(criterionList, criterion.getKey(), criterion.getConjunction(), fromDate, toDate);

				criterionList.remove(i);
				i++;

			}
		}

		return condition;
	}

	private Date getDayBeginTime(Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private Date getDayEndTime(Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	private void formCriterion(List<Criterion> criterionList, String key, String Conjunction, Date fromDate, Date toDate)
	{
		criterionList.add(new Criterion(key, fromDate, Conjunction, OperateSignEnum.NOTEARLIER));
		criterionList.add(new Criterion(key, toDate, Criterion.CON_AND, OperateSignEnum.NOTLATER));
	}

	protected List<FoundationObject> listObjectBySearch(SearchCondition condition, String searchGuid) throws ServiceRequestException
	{
		List<FoundationObject> foundationObjectList = null;
		foundationObjectList = this.listObject(condition);
		UserSignature userSignature = this.stubService.getUserSignature();

		this.addSaveSearchListener(condition, searchGuid, userSignature);
		return foundationObjectList;
	}

	/**
	 * 保存搜索条件
	 * 
	 * @param condition
	 * @param searchGuid
	 * @param userSignature
	 */
	private void addSaveSearchListener(final SearchCondition condition, final String searchGuid, final UserSignature userSignature)
	{
		Thread d = new Thread()
		{
			@Override
			public void run()
			{
				BOAS boas = null;
				try
				{
					if (userSignature != null)
					{
						boas = FoundationStub.this.serviceContext.allocatService(BOAS.class);
						((BOASImpl) boas).setSignature(userSignature);
						((BOASImpl) boas).getFoundationStub().saveSearchByCondition(condition, searchGuid);
					}
				}
				catch (Throwable e)
				{
					DynaLogger.error("saveSearchByCondition:", e);
				}
				finally
				{
					SecurityContextHolder.clearContext();

					if (boas != null)
					{
						FoundationStub.this.serviceContext.releaseService(boas);
					}
				}

			}
		};
		d.start();
	}

	private void saveSearchByCondition(SearchCondition condition, String searchGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(searchGuid))
		{
			this.stubService.getPOS().saveSearch(searchGuid, condition);
		}
		else
		{
			if (!SetUtils.isNullList(condition.getCriterionList()))
			{
				Search search = new Search();
				search.setIsAdvanced(condition.isAdvanced());
				String classGuid = null;
				String clasfGuid = null;
				String folderGuid = null;
				BOInfo boInfo = null;

				if (condition.getObjectGuid() != null && condition.getObjectGuid().getClassName() != null)
				{
					ClassStub.decorateObjectGuid(condition.getObjectGuid(), this.stubService);

					boInfo = ((EMMImpl) this.stubService.getEMM()).getBMStub().getCurrentBoInfoByClassName(condition.getObjectGuid().getClassName());
				}

				if (boInfo != null)
				{
					classGuid = boInfo.getClassGuid();
					clasfGuid = boInfo.getClassificationGuid();
				}

				if (condition.getFolder() != null)
				{
					folderGuid = condition.getFolder().getGuid();
				}

				((POSImpl) this.stubService.getPOS()).getMySearchStub().saveSearch(search, classGuid, clasfGuid, folderGuid, condition);
			}
		}

	}

	protected FoundationObject newFoundationObject(String classGuid, String className, ObjectGuid templateObjectGuid) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(templateObjectGuid, this.stubService);
		// String masterClassification = this.getMasterClassification(classGuid, className);
		if (!StringUtils.isNullString(classGuid))
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);
			if (classInfo != null)
			{
				classGuid = classInfo.getGuid();
			}

		}

		FoundationObject renFoundationObject = null;
		if (templateObjectGuid == null)
		{
			renFoundationObject = this.newFoundationObject(classGuid, className, (String) null);
			return renFoundationObject;
		}

		FoundationObject templateFoundationObject = this.getObject(templateObjectGuid);
		if (templateFoundationObject == null)
		{
			renFoundationObject = this.newFoundationObject(classGuid, className, (String) null);
			return renFoundationObject;
		}

		// 清除起始版本号
		templateFoundationObject.clear(SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName());
		templateFoundationObject.clear(SystemClassFieldEnum.UNIQUES.getName());
		templateFoundationObject.clear(SystemClassFieldEnum.REPEAT.getName());
		if (classGuid.equalsIgnoreCase(templateFoundationObject.getObjectGuid().getClassGuid()))
		{
			ObjectGuid objectGuid = templateFoundationObject.getObjectGuid();
			objectGuid.setGuid(null);
			templateFoundationObject.setId(null);
			templateFoundationObject.setObjectGuid(objectGuid);
			renFoundationObject = templateFoundationObject;

			List<FoundationObject> restoreAllClassification = templateFoundationObject.restoreAllClassification(false);
			if (!SetUtils.isNullList(restoreAllClassification))
			{
				for (FoundationObject foundation : restoreAllClassification)
				{
					foundation.setGuid(null);
				}
			}

		}
		else
		{
			renFoundationObject = this.newFoundationObject(classGuid, className, (String) null);
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			// get ui model object for this object.
			UIObjectInfo uiObject = ((EMMImpl) this.stubService.getEMM()).getUIStub().getUIObjectByBizModel(renFoundationObject.getObjectGuid().getClassName(), bmGuid,
					UITypeEnum.FORM);

			List<UIField> uiFieldList = null;

			if (uiObject != null)
			{
				uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
			}

			String fieldName = null;
			FieldTypeEnum type = null;
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField uiField : uiFieldList)
				{
					fieldName = uiField.getName();
					// 通过模板新建的情况下，不复制id
					if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(fieldName) || SystemClassFieldEnum.CLASSIFICATION.getName().equalsIgnoreCase(fieldName))
					{
						continue;
					}

					type = uiField.getType();
					if (templateFoundationObject.get(fieldName) != null && !"".equals(templateFoundationObject.get(fieldName)))
					{
						renFoundationObject.put(fieldName, templateFoundationObject.get(fieldName));
						switch (type)
						{
						case CODE:
						case CODEREF:
						case MULTICODE:
						case CLASSIFICATION:
							// if (SystemClassFieldEnum.getSystemField(fieldName) == null)
							if (!fieldName.contains("$"))
							{
								renFoundationObject.put(fieldName + "$NAME", templateFoundationObject.get(fieldName + "$NAME"));
								renFoundationObject.put(fieldName + "$TITLE", templateFoundationObject.get(fieldName + "$TITLE"));
							}
							else
							{
								// system class field
								renFoundationObject.put(fieldName + "NAME", templateFoundationObject.get(fieldName + "NAME"));
								renFoundationObject.put(fieldName + "TITLE", templateFoundationObject.get(fieldName + "TITLE"));
							}
							break;

						case OBJECT:
							renFoundationObject.put(fieldName + "$NAME", templateFoundationObject.get(fieldName + "NAME"));
							renFoundationObject.put(fieldName + "$CLASS", templateFoundationObject.get(fieldName + "CLASS"));
							renFoundationObject.put(fieldName + "$MASTER", templateFoundationObject.get(fieldName + "MASTER"));
							renFoundationObject.put(fieldName + "$ISMASTER", templateFoundationObject.get(fieldName + "ISMASTER"));
							renFoundationObject.put(fieldName + "$CLASSIFICATION", templateFoundationObject.get(fieldName + "CLASSIFICATION"));
							break;

						default:
							break;
						}
					}
				}
			}
			ClassField fieldinfo = this.stubService.getEMM().getFieldByName(templateObjectGuid.getClassName(), SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName(), false);
			if (StringUtils.isNullString(fieldinfo.getDefaultValue()))
			{
				renFoundationObject.put(SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName(), new BigDecimal(0));
			}
			else
			{
				renFoundationObject.put(SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName(), new BigDecimal(fieldinfo.getDefaultValue()));
			}
			renFoundationObject.put(SystemClassFieldEnum.REVISIONIDSEQUENCE.getName(), new BigDecimal(0));

			// 分类和名字 为空时要传
			fieldName = SystemClassFieldEnum.NAME.getName();
			if (templateFoundationObject.get(fieldName) != null && !"".equals(templateFoundationObject.get(fieldName)))
			{
				renFoundationObject.put(fieldName, templateFoundationObject.get(fieldName));
			}

			// 分类和名字 为空时要传
			// fieldName = SystemClassFieldEnum.CLASSIFICATION.getName();
			// if (templateFoundationObject.get(fieldName) != null &&
			// !"".equals(templateFoundationObject.get(fieldName)))
			// {
			// renFoundationObject.put(fieldName, templateFoundationObject.get(fieldName));
			// renFoundationObject.put(fieldName + "NAME", templateFoundationObject.get(fieldName + "NAME"));
			// renFoundationObject.put(fieldName + "TITLE", templateFoundationObject.get(fieldName + "TITLE"));
			// }

			// 分类和名字 为空时要传
			fieldName = SystemClassFieldEnum.CLASSIFICATION.getName();
			// 取得模板分类

			// String classificationfk = //(String) templateFoundationObject.get(fieldName);
			// 取得类的主分类
			// String masterClassification = this.getMasterClassification(classGuid, className);
			ClassInfo newClassInfo = this.stubService.getEMM().getClassByGuid(classGuid);
			ClassInfo oldClassInfo = this.stubService.getEMM().getClassByGuid(templateObjectGuid.getClassGuid());
			boolean isCopyMaster = false;
			if (!StringUtils.isNullString(newClassInfo.getClassification()) && newClassInfo.getClassification().equalsIgnoreCase(oldClassInfo.getClassification()))
			{
				renFoundationObject.put(fieldName, templateFoundationObject.get(fieldName));
				renFoundationObject.put(fieldName + "NAME", templateFoundationObject.get(fieldName + "NAME"));
				renFoundationObject.put(fieldName + "TITLE", templateFoundationObject.get(fieldName + "TITLE"));
				isCopyMaster = true;
			}
			else
			{
				renFoundationObject.put(fieldName, null);
				renFoundationObject.put(fieldName + "NAME", null);
				renFoundationObject.put(fieldName + "TITLE", null);
			}

			// 分类复制
			Map<String, FoundationObject> oldClassificaitonMap = new HashMap<String, FoundationObject>();
			List<FoundationObject> restoreAllClassification = templateFoundationObject.restoreAllClassification(false);
			if (!SetUtils.isNullList(restoreAllClassification))
			{
				for (FoundationObject foundation : restoreAllClassification)
				{
					oldClassificaitonMap.put(foundation.getClassificationGroupName(), foundation);
				}
			}

			List<ClassficationFeature> listClassficationFeature = this.stubService.getEMM().listClassficationFeature(classGuid);

			if (!SetUtils.isNullList(listClassficationFeature))
			{
				for (ClassficationFeature feature : listClassficationFeature)
				{
					if (!isCopyMaster && feature.isMaster())
					{
						continue;
					}

					FoundationObject foundationObject = oldClassificaitonMap.get(feature.getClassificationName());
					if (foundationObject != null)
					{
						foundationObject.setGuid(null);
						// oldClassificaitonMap.remove(foundationObject);
						renFoundationObject.addClassification(foundationObject, false);
					}
				}

				// for (FoundationObject foundationObject : oldClassificaitonMap.values())
				// {
				// templateFoundationObject.clearClasssification(foundationObject.getClassificationGroupName());
				// }
			}

		}

		this.stubService.getFSaverStub().checkFoundationFieldExist(renFoundationObject);

		return renFoundationObject;
	}

	protected FoundationObject newFoundationObject(String classGuid, String className, String classificationGuid) throws ServiceRequestException
	{
		FoundationObject newFoundationObject = this.newFoundationObject(FoundationObject.class, classGuid, className, false, null);
		if (StringUtils.isGuid(classificationGuid))
		{
			CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem(classificationGuid);
			if (codeItemInfo != null)
			{
				newFoundationObject.setClassificationGuid(classificationGuid);
				newFoundationObject.setClassificationName(codeItemInfo.getName());
				newFoundationObject.setClassification(codeItemInfo.getTitle());
			}

			FoundationObject classification = this.newFoundationObject(FoundationObject.class, classificationGuid, null, true, null);

			newFoundationObject.addClassification(classification, false);
		}

		return newFoundationObject;
	}

	@SuppressWarnings("unchecked")
	public <T extends DynaObject> T newFoundationObject(Class<T> objectClass, String classGuid, String className, boolean isClassification,
			FoundationObject oriClassificationFoundation) throws ServiceRequestException
	{
		if (StringUtils.isNullString(classGuid) && StringUtils.isNullString(className))
		{
			return null;
		}

		EMM emm = this.stubService.getEMM();
		List<ClassField> classFieldList = null;
		T retObject = null;
		if (!isClassification)
		{
			ObjectGuid objectGuid = new ObjectGuid(classGuid, className, null, null);
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			if (FoundationObject.class.equals(objectClass))
			{
				retObject = (T) new FoundationObjectImpl();
				((FoundationObject) retObject).setRevisionId(this.stubService.getInitRevisionId(0));
			}
			else if (StructureObject.class.equals(objectClass))
			{
				retObject = (T) new StructureObjectImpl();
			}
			else if (BOMStructure.class.equals(objectClass))
			{
				retObject = (T) new BOMStructure();
			}
			else
			{
				return null;
			}

			retObject.setObjectGuid(objectGuid);
			classFieldList = emm.listFieldOfClass(objectGuid.getClassName());
		}
		else
		{
			if (oriClassificationFoundation == null)
			{
				retObject = (T) new FoundationObjectImpl();
			}
			else
			{
				retObject = (T) ((FoundationObjectImpl) oriClassificationFoundation);
			}
			CodeItemInfo classification = this.stubService.getEMM().getCodeItem(classGuid);
			if (classification == null)
			{
				return null;
			}

			if (classification != null)
			{
				CodeObjectInfo code = this.stubService.getEMM().getCode(classification.getCodeGuid());
				((FoundationObjectImpl) retObject).setClassificationGroup(classification.getCodeGuid());
				((FoundationObjectImpl) retObject).setClassificationGroupName(code.getName());
				// ((FoundationObjectImpl) retObject).setClassificationGroupTitle(classification.getCodeTitle());
				((FoundationObjectImpl) retObject).setClassification(classification.getTitle());
				((FoundationObjectImpl) retObject).setClassificationName(classification.getName());
				((FoundationObjectImpl) retObject).setClassificationGuid(classification.getGuid());

			}

			// if (StringUtils.isGuid(oriClassificationFoundationGuid))
			// {
			// ObjectGuid objectGuid = new ObjectGuid(null, oriClassificationFoundationGuid, null);
			// retObject.setObjectGuid(objectGuid);
			// retObject.put(FoundationObjectImpl.ORICLASSIFICATIONITEM, oriClassificationFoundationGuid);
			// }

			classFieldList = emm.listClassificationField(classGuid);
		}

		if (!SetUtils.isNullList(classFieldList))
		{
			for (ClassField classField : classFieldList)
			{
				if (retObject.get(classField.getName()) == null)
				{
					retObject.put(classField.getName(), null);
				}
				if (FieldTypeEnum.STRING.equals(classField.getType()))
				{
					// String类型
					if (!StringUtils.isNullString(classField.getDefaultValue()))
					{
						if (retObject.get(classField.getName()) == null)
						{
							retObject.put(classField.getName(), classField.getDefaultValue());
						}
					}
				}
				else if (FieldTypeEnum.INTEGER.equals(classField.getType()) || FieldTypeEnum.FLOAT.equals(classField.getType()))
				{
					// Integer类型,Float类型
					if (retObject.get(classField.getName()) == null)
					{
						if (!StringUtils.isNullString(classField.getDefaultValue()))
						{
							retObject.put(classField.getName(), new BigDecimal(classField.getDefaultValue()));
						}
					}
				}
				else if (FieldTypeEnum.CODE.equals(classField.getType()) || FieldTypeEnum.CLASSIFICATION.equals(classField.getType()))
				{
					if (retObject.get(classField.getName()) == null)
					{
						// Code类型,Classification类型
						if (!StringUtils.isNullString(classField.getTypeValue()) && !StringUtils.isNullString(classField.getDefaultValue()))
						{

							CodeItemInfo codeItemInfo = emm.getCodeItemByName(classField.getTypeValue(), classField.getDefaultValue());
							if (codeItemInfo != null)
							{
								retObject.put(classField.getName(), codeItemInfo.getGuid());
								if (classField.isSystem())
								{
									retObject.put(classField.getName() + "TITLE", codeItemInfo.getTitle());
									retObject.put(classField.getName() + "NAME", codeItemInfo.getCode());
								}
								else
								{
									retObject.put(classField.getName() + "$TITLE", codeItemInfo.getTitle());
									retObject.put(classField.getName() + "$NAME", codeItemInfo.getCode());
								}
							}
						}
					}
				}
				else if (FieldTypeEnum.MULTICODE.equals(classField.getType()))
				{
					// MultiCode类型
					if (retObject.get(classField.getName()) == null)
					{
						if (!StringUtils.isNullString(classField.getTypeValue()) && !StringUtils.isNullString(classField.getDefaultValue()))
						{
							String defaultValue = classField.getDefaultValue();
							String[] defaultValues = StringUtils.splitStringWithDelimiter(CodeObjectInfo.MULTI_CODE_DELIMITER_GUID, defaultValue);
							if (defaultValues != null)
							{
								StringBuffer guidBuffer = new StringBuffer();
								StringBuffer nameBuffer = new StringBuffer();
								StringBuffer titleBuffer = new StringBuffer();
								int i = 0;
								int length = defaultValues.length;
								for (String value : defaultValues)
								{
									CodeItemInfo codeItemInfo = emm.getCodeItemByName(classField.getTypeValue(), value);

									i++;
									// guidBuffer.append(codeItemInfo.getGuid());
									guidBuffer.append((codeItemInfo == null ? "" : codeItemInfo.getGuid()) + (i == length ? "" : CodeObjectInfo.MULTI_CODE_DELIMITER_GUID));

									titleBuffer.append((codeItemInfo == null ? "" : codeItemInfo.getTitle()) + (i == length ? "" : CodeObjectInfo.MULTI_CODE_DELIMITER));

									nameBuffer.append((codeItemInfo == null ? "" : codeItemInfo.getCode()) + (i == length ? "" : CodeObjectInfo.MULTI_CODE_DELIMITER));

								}

								retObject.put(classField.getName(), guidBuffer.toString());
								retObject.put(classField.getName() + "$TITLE", titleBuffer.toString());
								retObject.put(classField.getName() + "$NAME", nameBuffer.toString());

							}

						}
					}
				}
				else if (FieldTypeEnum.DATE.equals(classField.getType()) || FieldTypeEnum.DATETIME.equals(classField.getType()))
				{
					// DATE,DATETIME类型
					if (retObject.get(classField.getName()) == null)
					{
						if (!StringUtils.isNullString(classField.getDefaultValue()))
						{
							retObject.put(classField.getName(), DateFormat.parse(classField.getDefaultValue()));
						}
					}
				}
				else if (FieldTypeEnum.BOOLEAN.equals(classField.getType()))
				{
					// BOOLEAN类型
					if (retObject.get(classField.getName()) == null)
					{
						if (!StringUtils.isNullString(classField.getDefaultValue()))
						{
							retObject.put(classField.getName(), BooleanUtils.getBooleanStringYN(BooleanUtils.getBooleanByValue(classField.getDefaultValue())));
						}
					}
				}
				else if (FieldTypeEnum.OBJECT.equals(classField.getType()))
				{
					// TODO OBJECT 类型
				}
			}

			String classification = null;
			if (!isClassification)
			{
				ClassField classificationClassField = this.stubService.getEMM().getFieldByName(retObject.getObjectGuid().getClassName(),
						SystemClassFieldEnum.CLASSIFICATION.getName(), false);
				if (classificationClassField != null)
				{
					classification = classificationClassField.getTypeValue();
				}
			}
			this.stubService.getFSaverStub().checkFieldValueExist(retObject, classFieldList, classification);
		}

		return retObject;
	}

	public void deleteFoundationObject(String foundationObjectGuid, String classGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		FoundationObject foundationObject = this.getObjectByGuid(new ObjectGuid(classGuid, null, foundationObjectGuid, null), isCheckAcl);
		if (foundationObject != null)
		{
			this.deleteObject(foundationObject, isCheckAcl);
		}
	}

	public void obsoleteObject(ObjectGuid objectGuid, Date obsoleteTime, boolean isCheckAcl) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();

		String sessionId = this.stubService.getSignature().getCredential();
		ServiceRequestException returnObj = null;
		FoundationObject foundationObject = null;
		try
		{
			isCheckAcl = Constants.isSupervisor(isCheckAcl, this.stubService);

			// 判断权限
			if (isCheckAcl && !aclService.hasAuthority(objectGuid, AuthorityEnum.OBSOLETE, sessionId))
			{
				throw new ServiceRequestException("ID_APP_OBJECT_NO_OBSOLETE_ACL", "no obsolete acl");
			}

			foundationObject = this.getObject(objectGuid, isCheckAcl);

			// 是否已经废弃
			if (foundationObject.getStatus().equals(SystemStatusEnum.OBSOLETE))
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "object has bean obsoleted", null, foundationObject.getFullName());
			}

			// 判断是否已经发布 发布的数据才能废弃
			if (foundationObject.getStatus() != null && !SystemStatusEnum.RELEASE.equals(foundationObject.getStatus()))
			{
				throw new ServiceRequestException("ID_APP_OBJECT_NOT_RELEASE", "object has not bean rls", null, foundationObject.getFullName());
			}
		}
		catch (DynaDataException e)
		{
			returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);

			throw returnObj;
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw e;
		}
		finally
		{
			Object[] args = new Object[] { foundationObject };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getObsoleteTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}
	}

	protected void cancelObsolete(ObjectGuid objectGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		String sessionId = this.stubService.getSignature().getCredential();
		isCheckAcl = Constants.isSupervisor(true, this.stubService);

		if (isCheckAcl)
		{
			throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only");
		}
		try
		{

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			ds.cancelObsolete(objectGuid.getGuid(), objectGuid.getClassGuid(), isCheckAcl, sessionId);

			// 取消废弃视图 此处不判断权限
			List<BOMView> bomViewList = ((BOMSImpl) this.stubService.getBOMS()).getBomViewStub().listBOMView(objectGuid, false);
			if (!SetUtils.isNullList(bomViewList))
			{
				for (BOMView bomView : bomViewList)
				{
					ds.cancelObsolete(bomView.getObjectGuid().getGuid(), objectGuid.getClassGuid(), isCheckAcl, sessionId);
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

	public FoundationObject getMaster(ObjectGuid objectGuid) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		try
		{
			return ds.getMaster(objectGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteMaster(String masterGuid, String classGuid, boolean isCheckAcl) throws ServiceRequestException
	{

		InstanceService ds = DataServer.getInstanceService();

		String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			ds.deleteMaster(masterGuid, classGuid, Constants.isSupervisor(isCheckAcl, this.stubService), sessionId, this.stubService.getFixedTransactionId());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected OpenInstanceModel openInstance(ObjectGuid objectGuid, boolean isView, String processRuntimeGuid) throws ServiceRequestException
	{
		OpenInstanceModel instanceModel = new OpenInstanceModel();
		if (!isView)
		{
			instanceModel.setInstance(this.stubService.openObject(objectGuid));
			if (instanceModel.getInstance() != null)
			{
				// 去除master信息
				// instanceModel.setMaster(this.getMaster(instanceModel.getInstance().getObjectGuid()));
				instanceModel.setHistoryRevision(this.stubService.listObjectRevisionHistory(instanceModel.getInstance().getObjectGuid()));
			}
		}
		else
		{
			List<RelationTemplateInfo> listRelationTemplate = this.stubService.getEMM().listRelationTemplate(objectGuid);
			RelationTemplateInfo relationTemplateByName = this.stubService.getEMM().getRelationTemplateByName(objectGuid, BuiltinRelationNameEnum.CAD3DCAD2D.toString());
			if (relationTemplateByName != null)
			{
				listRelationTemplate.add(relationTemplateByName);
			}
			instanceModel.setRelationTemplateList(listRelationTemplate);

			if (StringUtils.isNullString(processRuntimeGuid))
			{
				// 获取该（end1）对象的上的所有BOM视图
				instanceModel.setBomViewList(this.stubService.getBOMS().listBOMView(objectGuid));
			}
			else
			{
				instanceModel.setBomViewList(this.stubService.getWFI().listBOMView(objectGuid));
			}

			if (StringUtils.isNullString(processRuntimeGuid))
			{
				instanceModel.setRelationList(this.stubService.listRelationWithOutBuiltIn(objectGuid));
			}
			else
			{
				instanceModel.setRelationList(this.stubService.getWFI().listRelationWithOutBuiltIn(objectGuid));
			}
		}

		return instanceModel;
	}

	public void startUsingObject(ObjectGuid objectGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		ServiceRequestException returnObj = null;
		FoundationObject foundationObject = null;

		String sessionId = this.stubService.getSignature().getCredential();
		isCheckAcl = Constants.isSupervisor(isCheckAcl, this.stubService);
		try
		{
			foundationObject = this.getObject(objectGuid, isCheckAcl);

			List<FoundationObject> listObjectRevisionHistoryForMaster = this.stubService.getFRevisionStub().listObjectRevisionHistoryForMaster(foundationObject.getObjectGuid());
			if (!SetUtils.isNullList(listObjectRevisionHistoryForMaster))
			{
				for (FoundationObject foundation : listObjectRevisionHistoryForMaster)
				{
					// 判断是否已经停用 停用的数据才能启用
					if (foundation.getStatus() != null && !SystemStatusEnum.OBSOLETE.equals(foundation.getStatus()))
					{
						throw new ServiceRequestException("ID_APP_OBJECT_NOT_OBSOLETE", "object has not bean obslete", null, foundation.getFullName());
					}
					// 判断权限
					if (isCheckAcl && !aclService.hasAuthority(foundation.getObjectGuid(), AuthorityEnum.UNOBSOLETE, sessionId))
					{
						throw new ServiceRequestException("ID_APP_OBJECT_NO_START_ACL", "no start acl");
					}
				}
			}
			// // 判断权限
			// if (isCheckAcl && !ds.hasAuthority(objectGuid, AuthorityEnum.UNOBSOLETE, sessionId))
			// {
			// throw new ServiceRequestException("ID_APP_OBJECT_NO_UNOBSOLETE_ACL", "no unobsolete acl");
			// }
			//
			// FoundationObject foundationObject = this.getObject(objectGuid, isCheckAcl);
			//
			// // 判断是否已经停用 停用的数据才能启用
			// if (foundationObject.getStatus() != null &&
			// !SystemStatusEnum.OBSOLETE.equals(foundationObject.getStatus()))
			// {
			// throw new ServiceRequestException("ID_APP_OBJECT_NOT_OBSOLETE", "object has not bean obslete", null,
			// foundationObject.getFullName());
			// }

			// 启用
			// SystemDataService systemDataService = DataServer.getSystemDataService();
			// Map<String, String> filter = new HashMap<String, String>();
			// filter.put("MASTERFK", foundationObject.getObjectGuid().getMasterGuid());
			// filter.put("USERGUID", this.stubService.getOperatorGuid());
			//
			// systemDataService.save(filter, "resume", true);
			if (!SetUtils.isNullList(listObjectRevisionHistoryForMaster))
			{
				for (FoundationObject foundation : listObjectRevisionHistoryForMaster)
				{
					this.stubService.getFSaverStub().changeStatus(foundation.getObjectGuid(), SystemStatusEnum.OBSOLETE, SystemStatusEnum.RELEASE, false, false);
				}
			}
		}
		catch (DynaDataException e)
		{
			returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);

			throw returnObj;
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw e;
		}
		finally
		{
			Object[] args = new Object[] { foundationObject };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getStartTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}
	}

	public void stopUsingObject(ObjectGuid objectGuid, boolean isCheckAcl, String procrtGuid) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();

		String sessionId = this.stubService.getSignature().getCredential();
		ServiceRequestException returnObj = null;
		isCheckAcl = Constants.isSupervisor(isCheckAcl, this.stubService);

		// 判断权限
		if (isCheckAcl && !aclService.hasAuthority(objectGuid, AuthorityEnum.OBSOLETE, sessionId))
		{
			throw new ServiceRequestException("ID_APP_OBJECT_NO_OBSOLETE_ACL", "no obsolete acl");
		}

		FoundationObject foundationObject = this.getObjectByGuid(objectGuid, isCheckAcl);
		if (!SystemStatusEnum.RELEASE.equals(foundationObject.getStatus()))
		{
			throw new ServiceRequestException("ID_APP_OBJECT_NOT_RELEASE_OTHERS", "others has not bean rls", null, foundationObject.getFullName());
		}
		try
		{
			List<FoundationObject> listObjectRevisionHistoryForMaster = this.stubService.getFRevisionStub().listObjectRevisionHistoryForMaster(foundationObject.getObjectGuid());
			if (!SetUtils.isNullList(listObjectRevisionHistoryForMaster))
			{
				for (FoundationObject foundation : listObjectRevisionHistoryForMaster)
				{
					this.stubService.getFSaverStub().changeStatus(foundation.getObjectGuid(), SystemStatusEnum.RELEASE, SystemStatusEnum.OBSOLETE, false, false);
				}
			}
		}
		catch (DynaDataException e)
		{
			returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);

			throw returnObj;
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw e;
		}
		finally
		{
			Object[] args = new Object[] { foundationObject };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getStopTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}
	}

	private TrackerBuilder getStartTrackerBuilder()
	{
		if (startTrackerBuilder == null)
		{
			startTrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			startTrackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.START_FOUNDATION);
			startTrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return startTrackerBuilder;
	}

	private TrackerBuilder getObsoleteTrackerBuilder()
	{
		if (obsoleteTrackerBuilder == null)
		{
			obsoleteTrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			obsoleteTrackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.OBS_OBJECT);
			obsoleteTrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return obsoleteTrackerBuilder;
	}

	private TrackerBuilder getStopTrackerBuilder()
	{
		if (stopTrackerBuilder == null)
		{
			stopTrackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);
			stopTrackerBuilder.setTrackerRendererClass(TRFoundationImpl.class, TrackedDesc.STOP_FOUNDATION);
			stopTrackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return stopTrackerBuilder;
	}

	public void canStop(FoundationObject foundationObject, String procrtGuid) throws ServiceRequestException
	{
		// 是否已经停用
		RelationService ds = DataServer.getRelationService();
		WorkFlowService wfService = DataServer.getWorkFlowService();

		if (!foundationObject.isLatestRevision())
		{
			throw new ServiceRequestException("ID_APP_WF_OBS_NOT_RLS", "only latest revision can be obsoleted.");
		}

		AclService aclService = DataServer.getAclService();
		boolean checkAcl = !StringUtils.isGuid(procrtGuid) && Constants.isSupervisor(true, this.stubService);
		// 判断权限
		if (checkAcl && !aclService.hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.OBSOLETE, this.stubService.getSignature().getCredential()))
		{
			throw new ServiceRequestException("ID_APP_OBJECT_NO_OBSOLETE_ACL", "no obsolete acl");
		}

		if (foundationObject.getStatus().equals(SystemStatusEnum.OBSOLETE))
		{
			throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "object has bean obsoleted", null, foundationObject.getFullName());
		}

		// 判断是否已经发布 发布的数据才能停用
		if (foundationObject.getStatus() != null && !SystemStatusEnum.RELEASE.equals(foundationObject.getStatus()))
		{
			throw new ServiceRequestException("ID_APP_OBJECT_NOT_RELEASE", "object has not bean rls", null, foundationObject.getFullName());
		}

		String credential = this.stubService.getSignature().getCredential();
		List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByEND2(foundationObject.getObjectGuid());
		if (!SetUtils.isNullList(bomTemplateList))
		{
			for (BOMTemplateInfo bomTemplate : bomTemplateList)
			{
				List<FoundationObject> listWhereUsed = null;
				try
				{
					BMInfo bizModel = null;
					if ("ALL".equalsIgnoreCase(bomTemplate.getBmGuid()))
					{
						bizModel = this.stubService.getEMM().getCurrentBizModel();
					}
					else
					{
						bizModel = this.stubService.getEMM().getBizModel(bomTemplate.getBmGuid());
					}
					if (bizModel == null)
					{
						continue;
					}
					BOInfo end1ClassInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bizModel.getGuid(), bomTemplate.getEnd1BoName());
					if (end1ClassInfo == null)
					{
						continue;
					}
					String end1ClassName = end1ClassInfo.getClassName();
					SearchCondition end1SearchCondition = SearchConditionFactory.createSearchCondition4Class(end1ClassName, null, false);
					SearchCondition struSearchCondition = SearchConditionFactory.createSearchCondition4Class(bomTemplate.getStructureClassName(), null, false);
					listWhereUsed = ds.getObsoleteWhereUsed(foundationObject.getObjectGuid(), bomTemplate.getName(), bomTemplate.getViewClassName(),
							bomTemplate.getPrecise() == BomPreciseType.PRECISE, end1SearchCondition, struSearchCondition, credential);
				}
				catch (Exception e)
				{
					throw new ServiceRequestException("ID_DS_QUERY_DATA_EXCEPTION", "getObsoleteWhereUsed error.");
				}
				if (!SetUtils.isNullList(listWhereUsed))
				{
					for (FoundationObject foundation : listWhereUsed)
					{
						if (foundation.isLatestRevision() && !foundation.isObsolete())
						{
							throw new ServiceRequestException("ID_APP_STOPOBJ_BOM_LAST", "bom last", null,
									StringUtils.convertNULLtoString(foundation.getId()) + "/" + StringUtils.convertNULLtoString(foundation.getRevisionId()));
						}
					}
				}
			}
		}

		List<RelationTemplateInfo> relationTemplateList = this.stubService.getEMM().listRelationTemplateByEND2(foundationObject.getObjectGuid());
		if (!SetUtils.isNullList(relationTemplateList))
		{
			for (RelationTemplateInfo relationTemplate : relationTemplateList)
			{
				List<FoundationObject> listWhereUsed = null;
				try
				{
					BMInfo bizModel = null;
					if ("ALL".equalsIgnoreCase(relationTemplate.getBmGuid()))
					{
						bizModel = this.stubService.getEMM().getCurrentBizModel();
					}
					else
					{
						bizModel = this.stubService.getEMM().getBizModel(relationTemplate.getBmGuid());
					}

					if (bizModel == null)
					{
						continue;
					}
					BOInfo end1ClassInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bizModel.getGuid(), relationTemplate.getEnd1BoName());
					if (end1ClassInfo == null)
					{
						continue;
					}
					String end1ClassName = end1ClassInfo.getClassName();
					SearchCondition end1SearchCondition = SearchConditionFactory.createSearchCondition4Class(end1ClassName, null, false);
					SearchCondition struSearchCondition = SearchConditionFactory.createSearchCondition4Class(relationTemplate.getStructureClassName(), null, false);
					listWhereUsed = ds.getObsoleteWhereUsed(foundationObject.getObjectGuid(), relationTemplate.getId(), relationTemplate.getViewClassName(),
							"2".equals(relationTemplate.getEnd2Type()), end1SearchCondition, struSearchCondition, credential);
				}
				catch (Exception e)
				{
					throw new ServiceRequestException("ID_DS_QUERY_DATA_EXCEPTION", "getObsoleteWhereUsed error.");
				}
				if (!SetUtils.isNullList(listWhereUsed))
				{
					for (FoundationObject foundation : listWhereUsed)
					{
						if (foundation.isLatestRevision())
						{
							ClassInfo classInfo = null;
							if (foundation.getObjectGuid() != null)
							{
								ClassStub.decorateObjectGuid(foundation.getObjectGuid(), this.stubService);
								classInfo = this.stubService.getEMM().getClassByName(foundation.getObjectGuid().getClassName());
							}
							/**
							 * 关联关系中若end1为以下两种情况end2可停用
							 * 1.无版本且是已发布状态
							 * 2.本身处于停用状态
							 * 否则抛出异常 ID_APP_STOPOBJ_RELATION_LAST
							 */
							if (!(SystemStatusEnum.RELEASE.equals(foundation.getStatus()) && classInfo != null && !classInfo.hasInterface(ModelInterfaceEnum.IVersionable))
									&& !SystemStatusEnum.OBSOLETE.equals(foundation.getStatus()))
							{
								// throw new ServiceRequestException("ID_APP_STOPOBJ_RELATION_LAST", "relation last");
								throw new ServiceRequestException("ID_APP_STOPOBJ_RELATION_LAST", "relation last", null,
										StringUtils.convertNULLtoString(foundation.getId()) + "/" + StringUtils.convertNULLtoString(foundation.getRevisionId()));
							}
						}
					}
				}
			}
		}

		// 是否有版本在流程中
		List<ProcAttach> listWhereUsed = wfService.listRevisionInWF(foundationObject.getObjectGuid().getMasterGuid(), foundationObject.getObjectGuid().getClassGuid());
		if (!SetUtils.isNullList(listWhereUsed))
		{
			for (ProcAttach procAttach : listWhereUsed)
			{
				if (!(StringUtils.isGuid(procrtGuid) && procrtGuid.equals(procAttach.getProcessRuntimeGuid())))
				{
					throw new ServiceRequestException("ID_APP_STOPOBJ_WORKFLOW_LAST", "relation last");
				}
			}
		}
		// throw new ServiceRequestException("ID_APP_IN_PROCESS", "in process");
		// 判断此料是否作为有效的（无失效时间）全局/局部的替代件；如果是，则不允许停用；反之，则可停用
		List<FoundationObject> listRepalcedByRsItem = ((BRMImpl) this.stubService.getBRM()).getReplaceQueryStub().listRepalcedByRsItem(foundationObject.getObjectGuid(), null, null,
				false);
		if (!SetUtils.isNullList(listRepalcedByRsItem))
		{
			FoundationObject foundationObject2 = listRepalcedByRsItem.get(0);
			FoundationObject componentItem = null;
			if (foundationObject2 != null)
			{
				String componentItemClassGuid = (String) foundationObject2.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.ClassGuid);
				String componentItemMasterGuid = (String) foundationObject2.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER);
				String className = (String) foundationObject2.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.CLASSNAME);
				if (StringUtils.isGuid(componentItemMasterGuid))
				{
					// componentItem = this.stubService.getObjectByGuid(new ObjectGuid(componentItemClassGuid, null,
					// componentItemGuid, null));
					ObjectGuid objectGuid = new ObjectGuid(componentItemClassGuid, className, null, componentItemMasterGuid, null);
					objectGuid.setIsMaster(true);
					componentItem = this.stubService.getObject(objectGuid);
				}
			}
			throw new ServiceRequestException("ID_APP_STOPOBJ_REPLACE_ITEM", "in replace item", null, componentItem != null ? componentItem.getFullName() : "");
		}
	}

	public String checkFoundationRepeat(FoundationObject foundationObject, boolean isSaveAs) throws ServiceRequestException
	{
		this.stubService.getFSaverStub().checkClassificationExist(foundationObject, true);

		ClassificationAllocate ac = new ClassificationAllocate();
		String allocateSingleItem = ac.startSingleNumberItem(foundationObject, this.stubService, SystemClassFieldEnum.REPEAT.getName(), true);
		if (!StringUtils.isNullString(allocateSingleItem))
		{
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(foundationObject.getObjectGuid().getClassName(), null, false);
			searchCondition.addFilter(SystemClassFieldEnum.REPEAT.getName(), allocateSingleItem, OperateSignEnum.EQUALS);
			// searchCondition.addFilter(SystemClassFieldEnum.REPEAT.getName(),
			// foundationObject.getObjectGuid(), OperateSignEnum.NO);
			// searchCondition.addFilter(SystemClassFieldEnum.GUID.getName(), unique,
			// OperateSignEnum.NOTEQUALS);

			if (isSaveAs)
			{
				foundationObject.getObjectGuid().setMasterGuid(null);
			}

			searchCondition.setPageSize(10);
			List<FoundationObject> listObject = this.stubService.getFoundationStub().listObject(searchCondition, false);
			if (!SetUtils.isNullList(listObject))
			{

				for (FoundationObject foundation : listObject)
				{

					if (!foundation.getObjectGuid().getMasterGuid().equals(foundationObject.getObjectGuid().getMasterGuid()))
					{
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

						String msrString = this.stubService.getMSRM().getMSRString("ID_APP_OBJECT_REPEAT", languageEnum.toString());
						if (msrString == null)
						{
							msrString = "ID_APP_OBJECT_REPEAT";
						}
						return msrString;

					}
				}
			}
		}
		return "";
	}

	protected void refreshMergeFieldValue(String className, String fieldName) throws ServiceRequestException
	{
		this.refreshMergeFieldValue(className, new HashSet<String>(Arrays.asList(fieldName)), null);
	}

	protected void refreshMergeFieldValue(String className, Set<String> fieldNameSet, String classificationGuid) throws ServiceRequestException
	{
		this.refresh(1, className, fieldNameSet, classificationGuid);
	}

	private void refresh(int pageIndex, String className, Set<String> fieldNameSet, String classificationGuid) throws ServiceRequestException
	{
		SearchCondition condition = SearchConditionFactory.createSearchCondition4Class(className, null, true);
		condition.setPageNum(pageIndex);
		condition.setPageSize(500);
		if (!StringUtils.isNullString(classificationGuid))
		{
			condition.addFilter(SystemClassFieldEnum.CLASSIFICATION.getName(), classificationGuid, OperateSignEnum.YES);
		}
		List<FoundationObject> list = this.listObject(condition);
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject foundationObject : list)
			{
				foundationObject = this.getObject(foundationObject.getObjectGuid());
				this.refresh(foundationObject, fieldNameSet);
			}
			if (list.size() < 500)
			{
				return;
			}
			pageIndex++;
			refresh(pageIndex, className, fieldNameSet, classificationGuid);
		}
	}

	private void refresh(FoundationObject foundationObject, Set<String> fieldNameSet) throws ServiceRequestException
	{
		NumberAllocate na = new NumberAllocate();
		na.allocate(foundationObject, this.stubService, true);
		DSToolService ds = DataServer.getToolService();
		for (String fieldName : fieldNameSet)
		{
			if (foundationObject.isChanged(fieldName))
			{
				ds.refreshMergeFieldValue(foundationObject, fieldName);
			}
		}
	}

	protected void release(ObjectGuid objectGuid) throws ServiceRequestException
	{
		InstanceService dataService = DataServer.getInstanceService();
		if (objectGuid == null)
		{
			throw new ServiceRequestException("NO DATA");
		}
		FoundationObject foundationObject = this.getObject(objectGuid);

		if (foundationObject.isCheckOut())
		{
			throw new ServiceRequestException("DATA is checkout");
		}
		String credential = null;
		try
		{
			credential = this.stubService.getUserSignature().getCredential();
		}
		catch (ServiceRequestException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			dataService.release(objectGuid, credential, this.stubService.getFixedTransactionId());
		}
		catch (Exception e)
		{
			DynaLogger.error(e.getMessage());
		}
	}
}

class DeleteScheduledTask extends AbstractScheduledTask
{
	private ServiceContext			serviceContext		= null;
	private ObjectGuid				objectGuid			= null;
	private List<FoundationObject>	end1List			= null;
	private String					bmGuid				= null;
	private String					exceptionParameter	= null;
	private boolean					deleteReplace		= false;
	private BOAS					boas				= null;
	private BOMS					boms				= null;
	private BRM						brm;
	private UserSignature			userSignature;

	public DeleteScheduledTask(ServiceContext serviceContext, ObjectGuid objectGuid, List<FoundationObject> end1List, String bmGuid, String exceptionParameter)
	{
		this.serviceContext = serviceContext;
		this.objectGuid = objectGuid;
		this.end1List = end1List;
		this.bmGuid = bmGuid;
		this.exceptionParameter = exceptionParameter;
		this.setPriority(990);
	}

	public DeleteScheduledTask(ServiceContext serviceContext, ObjectGuid objectGuid, List<FoundationObject> end1List, String bmGuid, String exceptionParameter,
			boolean deleteReplace, UserSignature userSignature)
	{
		this(serviceContext, objectGuid, end1List, bmGuid, exceptionParameter);
		this.deleteReplace = deleteReplace;
		this.userSignature = userSignature;
		this.setPriority(990);
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]DeletetScheduledTask , Scheduled Task Start...");
		try
		{
			this.boas = this.serviceContext.allocatService(BOAS.class);
			((BOASImpl) boas).setSignature(userSignature);
			this.boms = this.serviceContext.allocatService(BOMS.class);
			((BOMSImpl) boms).setSignature(userSignature);
			this.boas.deleteReference(objectGuid, exceptionParameter);
			if (!SetUtils.isNullList(this.end1List))
			{
				((BOMSImpl) this.boms).getBomStub().updateUHasBOM(this.end1List, this.bmGuid);
			}
			if (deleteReplace)
			{
				brm = serviceContext.allocatService(BRM.class);
				((BRMImpl) brm).setSignature(userSignature);
				((BRMImpl) brm).newTransactionId();
				((BRMImpl) brm).getReplaceObjectStub().deleteRepaceDataByItem(objectGuid);
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
			if (this.brm != null)
			{
				this.serviceContext.releaseService(this.brm);
			}
		}
		DynaLogger.info("QueuedTaskScheduler Scheduled [Class]DeletetScheduledTask , Scheduled Task End...");
	}

}