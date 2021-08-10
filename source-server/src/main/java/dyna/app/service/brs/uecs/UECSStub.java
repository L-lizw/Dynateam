/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UECSStub
 * wangweixia 2014-1-14
 */
package dyna.app.service.brs.uecs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.boas.RelationStub;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.uecs.ECNLifecyclePhaseEnum;
import dyna.common.systemenum.uecs.ECOLifecyclePhaseEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.common.util.UpdatedECSConstants;
import dyna.net.service.brs.EMM;

/**
 * ECS的公共方法
 * 
 * @author wangweixia
 * 
 */
public class UECSStub extends AbstractServiceStub<UECSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected UECSStub(ServiceContext context, UECSImpl service)
	{
		super(context, service);
	}

	protected void getKeyFromObjectGuid(ObjectGuid object, String sid, DynaObject foundationObject) throws ServiceRequestException
	{
		if (object != null)
		{
			// foundationObject.clear(sid.toUpperCase());
			foundationObject.put((sid + UpdatedECSConstants.ClassGuid).toUpperCase(), object.getClassGuid());
			foundationObject.put((sid).toUpperCase(), object.getGuid());
			if (StringUtils.isNullString(object.getMasterGuid()))
			{
				FoundationObject fMasterItem = this.stubService.getBOAS().getObjectByGuid(object);
				String masterGuid = fMasterItem.getObjectGuid().getMasterGuid();
				foundationObject.put((sid + UpdatedECSConstants.MASTER).toUpperCase(), masterGuid);
			}
			else
			{
				foundationObject.put((sid + UpdatedECSConstants.MASTER).toUpperCase(), object.getMasterGuid());
			}
		}
		else
		{
			foundationObject.clear(sid.toUpperCase());
		}
	}

	/**
	 * ECR与ECP的关联
	 * ECR-ECP/ECN-ECO/ECP_ECO/ECO-EC?/
	 * ECN与ECO的关联
	 * 
	 * @param ecrObject
	 * @param objectGuid
	 * @param relationTemplate
	 * @throws ServiceRequestException
	 */
	protected void link(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, StructureObject structureObject, String proGuid, String... strings) throws ServiceRequestException
	{
		if (strings == null || strings.length < 0)
		{
			throw new ServiceRequestException("ID_APP_UECRECPSTUB_TEMPLATE_NULL", "the template is null", null, "'");
		}
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, strings[0]);
		if (relationTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_UECRECPSTUB_TEMPLATE_NULL", "the template is null", null, strings[0]);
		}
		if (structureObject == null)
		{
			String structureClassName = relationTemplate.getStructureClassName();
			if (StringUtils.isNullString(structureClassName))
			{
				structureClassName = StructureObject.STRUCTURE_CLASS_NAME;
			}
			structureObject = this.stubService.getBOAS().newStructureObject(null, structureClassName);
			structureObject.setObjectGuid(new ObjectGuid(relationTemplate.getStructureClassGuid(), relationTemplate.getStructureClassName(), null, null));
			structureObject.put("StructureClassName", relationTemplate.getStructureClassName());
			structureObject.put("viewName", relationTemplate.getName());
		}

		RelationStub relationStub = ((BOASImpl) this.stubService.getBOAS()).getRelationStub();
		ViewObject viewObject = null;
		viewObject = this.stubService.getBOAS().getRelationByEND1(end1ObjectGuid, relationTemplate.getName());

		// 先创建一个View 然后再关联
		if (viewObject == null)
		{
			viewObject = relationStub.saveRelationByTemplate(relationTemplate.getGuid(), end1ObjectGuid, false, null);
		}
		((BOASImpl) this.stubService.getBOAS()).getRelationLinkStub().link(viewObject.getObjectGuid(), end2ObjectGuid, structureObject, false, proGuid);

	}

	/**
	 * 删除实例,不判断权限
	 * 
	 * @param foundation
	 * @throws ServiceRequestException
	 */
	protected void deleteObject(FoundationObject foundation) throws ServiceRequestException
	{
		if (foundation != null && foundation.getObjectGuid() != null && StringUtils.isGuid(foundation.getObjectGuid().getGuid()))
		{
			try
			{
				this.stubService.getBOAS().getObject(foundation.getObjectGuid());
			}
			catch (ServiceRequestException e)
			{
				if (!DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT.getMsrId().equals(e.getMsrId()))
				{
					throw e;
				}
				else
				{
					return;
				}
			}
			if (foundation.isCheckOut())
			{
				foundation = this.stubService.getBOAS().checkIn(foundation, false);
			}
			// 如果对象是ECO，则先解锁
			String classguid = foundation.getObjectGuid().getClassGuid();
			if (StringUtils.isGuid(classguid))
			{
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classguid);
				if (classInfo != null)
				{
					if (classInfo.hasInterface(ModelInterfaceEnum.IECOM))
					{
						this.stubService.getUECNECOStub().unlockByEco(foundation);
					}
				}
			}
			((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteObject(foundation, false);
		}
	}

	/**
	 * 通过end1ObjectGuid和end2ObjectGuid以及bomTemplateName取得对应的StructureObject
	 * 
	 * @param end1ObjectGuid
	 * @param end2ObjectGuid
	 * @param viewName
	 *            BOMTemplate的Name
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<BOMStructure> listStructureObject(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, String viewName) throws ServiceRequestException
	{
		List<BOMStructure> returnValue = null;
		if (end1ObjectGuid != null && end2ObjectGuid != null && !StringUtils.isNullString(viewName))
		{
			List<BOMStructure> listStructure = this.stubService.getBOMS().listBOM(end1ObjectGuid, viewName, null, null, null);
			if (!SetUtils.isNullList(listStructure))
			{
				returnValue = new ArrayList<BOMStructure>();
				for (BOMStructure stru : listStructure)
				{
					if (stru != null && stru.getEnd2ObjectGuid() != null && stru.getEnd2ObjectGuid().equals(end2ObjectGuid))
					{
						returnValue.add(stru);
					}
				}
			}

		}
		return returnValue;
	}

	protected FoundationObject createObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		String ownerUserGuid = this.stubService.getUserSignature().getUserGuid();
		String ownerGroupGuid = this.stubService.getUserSignature().getLoginGroupGuid();
		return ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(foundationObject, null, ownerGroupGuid, ownerUserGuid, false, true, true, true, null, true);
	}

	public FoundationObject updateLifeCyclePhase(ObjectGuid objectGuid, String lifeCyclePhaseOriginal, String lifeCyclePhaseDest) throws ServiceRequestException
	{
		if (StringUtils.isGuid(lifeCyclePhaseOriginal) && StringUtils.isGuid(lifeCyclePhaseDest))
		{
			EMM emm = this.stubService.getEMM();
			LifecyclePhaseInfo srcPhase = emm.getLifecyclePhaseInfo(lifeCyclePhaseOriginal);
			LifecyclePhaseInfo destPhase = emm.getLifecyclePhaseInfo(lifeCyclePhaseDest);
			return ((BOASImpl) this.stubService.getBOAS()).getFUpdaterStub().updateLifeCyclePhase(objectGuid, new Date(), srcPhase, destPhase, false);
		}
		return this.stubService.getBOAS().getObject(objectGuid);

	}

	/**
	 * @param eco
	 * @param allAttachMent
	 * @throws ServiceRequestException
	 */
	private void putSolveItemInWorkFlow(FoundationObject eco, List<FoundationObject> allAttachMent) throws ServiceRequestException
	{
		// 查看ECO下面是否有解决对象
		ViewObject ecoViewObject = this.stubService.getBOAS().getRelationByEND1(eco.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEMAFTER$);
		if (ecoViewObject != null && ecoViewObject.getObjectGuid() != null)
		{
			List<FoundationObject> listSolveFo = this.listFoundationObjectOfRelation(ecoViewObject.getObjectGuid(), null);
			if (!SetUtils.isNullList(listSolveFo))
			{
				for (FoundationObject solveFo : listSolveFo)
				{
					// 解决对象直接加进去
					allAttachMent.add(solveFo);
				}
			}
		}
		allAttachMent.add(eco);
	}

	public void sendMailtoPerformerByScript(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listAllValidProcAttach = this.stubService.getWFI().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(listAllValidProcAttach))
		{
			return;
		}
		for (ProcAttach procAttach : listAllValidProcAttach)
		{
			FoundationObject procFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(
					new ObjectGuid(procAttach.getInstanceClassGuid(), null, procAttach.getInstanceGuid(), null), false);
			procFoundation = this.stubService.getBOAS().getObject(procFoundation.getObjectGuid());
			ClassInfo procClassInfo = this.stubService.getEMM().getClassByGuid(procFoundation.getObjectGuid().getClassGuid());
			if (procClassInfo == null)
			{
				continue;
			}

			if (procClassInfo.hasInterface(ModelInterfaceEnum.IECOM))
			{
				String receiver = "";
				if (procFoundation.get("PERFORMER") != null && StringUtils.isGuid((String) procFoundation.get("PERFORMER")))
				{
					User user = this.stubService.getAAS().getUser((String) procFoundation.get("PERFORMER"));
					if (user != null)
					{
						receiver = (String) procFoundation.get("PERFORMER");
					}
				}
				if (!StringUtils.isGuid(receiver))
				{
					if (procFoundation.getOwnerUserGuid() != null && StringUtils.isGuid(procFoundation.getOwnerUserGuid()))
					{
						User user = this.stubService.getAAS().getUser(procFoundation.getOwnerUserGuid());
						if (user != null)
						{
							receiver = procFoundation.getOwnerUserGuid();
						}
					}
				}
				if (!StringUtils.isGuid(receiver))
				{
					continue;
				}

				LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
				// subject:ECO执行通知
				String subject = this.stubService.getMSRM().getMSRString("ID_APP_UECS_SENDMAIL_TOECO_PERFORMER_SUBJECT", languageEnum.toString());
				if (StringUtils.isNullString(subject))
				{
					subject = "ID_APP_UECS_SENDMAIL_TOECO_PERFORMER_SUBJECT";
				}

				FoundationObject ecnFoundation = null;
				if (!procClassInfo.hasInterface(ModelInterfaceEnum.IIndependenceForEc))
				{
					List<FoundationObject> listEcn = this.stubService.getUECQueryStub().getWhereUsedECNByECO(procFoundation.getObjectGuid());
					if (!SetUtils.isNullList(listEcn))
					{
						ecnFoundation = listEcn.get(0);
					}
				}

				String contentsMsr = "";
				if (ecnFoundation != null)
				{
					// 请执行隶属于ECN{0}(ECN的编号加名称)的ECO{1}（ECN的编号加变更主题）
					String contents = this.stubService.getMSRM().getMSRString("ID_APP_UECS_SENDMAIL_TOECO_PERFORMER_CONTENTS_1", languageEnum.toString());
					if (StringUtils.isNullString(contents))
					{
						contents = "ID_APP_UECS_SENDMAIL_TOECO_PERFORMER_CONTENTS_1";
					}
					String ecnsName = StringUtils.convertNULLtoString(ecnFoundation.getFullName());
					String ecosName = StringUtils.convertNULLtoString(procFoundation.getFullName());
					contentsMsr = MessageFormat.format(contents, ecnsName, ecosName);
				}
				else
				{
					// 请执行ECO{0}（ECN的编号加变更主题）
					String contents = this.stubService.getMSRM().getMSRString("ID_APP_UECS_SENDMAIL_TOECO_PERFORMER_CONTENTS_2", languageEnum.toString());
					if (StringUtils.isNullString(contents))
					{
						contents = "ID_APP_UECS_SENDMAIL_TOECO_PERFORMER_CONTENTS_2";
					}
					String ecosName = StringUtils.convertNULLtoString(procFoundation.getFullName());
					contentsMsr = MessageFormat.format(contents, ecosName);
				}

				List<ObjectGuid> objectGuidList = new ArrayList<ObjectGuid>();
				objectGuidList.add(procFoundation.getObjectGuid());
				this.stubService.getSMS().sendMailToUser(subject, contentsMsr, MailCategoryEnum.INFO, objectGuidList, receiver, MailMessageType.ECNOTIFY);
			}
		}
	}

	/**
	 * ECR和ECP一起走流程时,只要不是Released的生命周期,都要判断ECP有没有加进来---没有加进来就报错
	 * ECN和ECO一起走流程是，只要不是Released的生命周期,都要判断解决对象有没有加进来--没有加进来就报错
	 * 
	 * @param wfTemplateGuid
	 * @param parentActGuid
	 * @param procAttach
	 * @throws ServiceRequestException
	 */
	public void checkAttachMent(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listAllValidProcAttach = this.stubService.getWFI().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(listAllValidProcAttach))
		{
			return;
		}
		Map<String, FoundationObject> procAttachMap = new HashMap<String, FoundationObject>();
		for (ProcAttach procAttach : listAllValidProcAttach)
		{
			FoundationObject fObj = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(
					new ObjectGuid(procAttach.getInstanceClassGuid(), null, procAttach.getInstanceGuid(), null), false);
			procAttachMap.put(procAttach.getInstanceGuid(), fObj);
		}
		// ////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (String keyString : procAttachMap.keySet())
		{
			FoundationObject procFoundation = procAttachMap.get(keyString);
			ClassInfo procAttachClassInfo = this.stubService.getEMM().getClassByGuid(procFoundation.getObjectGuid().getClassGuid());
			if (procAttachClassInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN))
			{
				List<FoundationObject> ecoList = this.stubService.getUECQueryStub().getECOByECNAll(procFoundation.getObjectGuid());
				if (SetUtils.isNullList(ecoList))
				{
					continue;
				}
				for (FoundationObject ecoObj : ecoList)
				{
					LifecyclePhaseInfo ecoLifephase = this.stubService.getEMM().getLifecyclePhaseInfo(ecoObj.getLifecyclePhaseGuid());
					if (ecoLifephase.getName().equals(ECOLifecyclePhaseEnum.Performing.name()) && !"Y".equals(ecoObj.get(UpdatedECSConstants.isCompleted)))
					{
						throw new ServiceRequestException("ID_APP_UECSSTUB_ECO_NOT_COMPLETE", "eco not complete", null, ecoObj.getFullName());
					}
					List<FoundationObject> attachMent = new ArrayList<FoundationObject>();
					this.putSolveItemInWorkFlow(ecoObj, attachMent);
					if (!SetUtils.isNullList(attachMent))
					{
						for (FoundationObject fo : attachMent)
						{
							ClassInfo foClassInfo = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
							LifecyclePhaseInfo foLifecyclePhase = this.stubService.getEMM().getLifecyclePhaseInfo(fo.getLifecyclePhaseGuid());
							if (foClassInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) && foLifecyclePhase.getName().equals(ECNLifecyclePhaseEnum.Canceled.name()))
							{
								continue;
							}
							else if (foClassInfo.hasInterface(ModelInterfaceEnum.IECOM) && foLifecyclePhase.getName().equals(ECOLifecyclePhaseEnum.Canceled.name()))
							{
								continue;
							}
							// ////////////////////////////////////////////////////////////////////////////////////////////////
							// ////////////////////////////////////////////////////////////////////////////////////////////////
							if (procAttachMap.get(fo.getObjectGuid().getGuid()) == null && fo.getStatus() != SystemStatusEnum.RELEASE)
							{
								throw new ServiceRequestException("ID_APP_UECS_ECSOBJECT_NOT_PUT", "ec's object is lost", null, fo.getFullName());
							}
						}
					}
				}
			}
			else if (procAttachClassInfo.hasInterface(ModelInterfaceEnum.IECOM))
			{
				FoundationObject ecoObj = this.stubService.getBOAS().getObject(procFoundation.getObjectGuid());
				LifecyclePhaseInfo ecoLifephase = this.stubService.getEMM().getLifecyclePhaseInfo(ecoObj.getLifecyclePhaseGuid());
				if (ecoLifephase.getName().equals(ECOLifecyclePhaseEnum.Performing.name()) && !"Y".equals(ecoObj.get(UpdatedECSConstants.isCompleted)))
				{
					throw new ServiceRequestException("ID_APP_UECSSTUB_ECO_NOT_COMPLETE", "eco not complete", null, ecoObj.getFullName());
				}
				List<FoundationObject> attachMent = new ArrayList<FoundationObject>();
				this.putSolveItemInWorkFlow(procFoundation, attachMent);
				if (!SetUtils.isNullList(attachMent))
				{
					for (FoundationObject fo : attachMent)
					{
						ClassInfo foClassInfo = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
						LifecyclePhaseInfo foLifecyclePhase = this.stubService.getEMM().getLifecyclePhaseInfo(fo.getLifecyclePhaseGuid());
						if (foClassInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) && foLifecyclePhase.getName().equals(ECNLifecyclePhaseEnum.Canceled.name()))
						{
							continue;
						}
						else if (foClassInfo.hasInterface(ModelInterfaceEnum.IECOM) && foLifecyclePhase.getName().equals(ECOLifecyclePhaseEnum.Canceled.name()))
						{
							continue;
						}
						// ////////////////////////////////////////////////////////////////////////////////////////////////
						// ////////////////////////////////////////////////////////////////////////////////////////////////
						if (procAttachMap.get(fo.getObjectGuid().getGuid()) == null && fo.getStatus() != SystemStatusEnum.RELEASE)
						{
							throw new ServiceRequestException("ID_APP_UECS_ECSOBJECT_NOT_PUT", "ec's object is lost", null, fo.getFullName());
						}
					}
				}
			}
			else if (procAttachClassInfo.hasInterface(ModelInterfaceEnum.IUpdatedECR))
			{
				List<FoundationObject> ecpList = this.stubService.getUECQueryStub().getECPByECRAll(procFoundation.getObjectGuid());
				if (SetUtils.isNullList(ecpList))
				{
					continue;
				}
				for (FoundationObject fo : ecpList)
				{
					if (procAttachMap.get(fo.getObjectGuid().getGuid()) == null)
					{
						throw new ServiceRequestException("ID_APP_UECS_ECSOBJECT_NOT_PUT", "ec's object is lost", null, fo.getFullName());
					}
				}
			}
		}
	}

	/**
	 * 审核阶段增加eco
	 * 审核阶段增加ecp
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void addAttachMent(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listAllValidProcAttach = this.stubService.getWFI().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(listAllValidProcAttach))
		{
			return;
		}

		Map<String, ProcAttach> beAddedProcList = new HashMap<String, ProcAttach>();
		Map<String, FoundationObject> procAttachMap = new HashMap<String, FoundationObject>();
		for (ProcAttach procAttach : listAllValidProcAttach)
		{
			FoundationObject fObj = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(
					new ObjectGuid(procAttach.getInstanceClassGuid(), null, procAttach.getInstanceGuid(), null), false);
			procAttachMap.put(procAttach.getInstanceGuid(), fObj);
		}
		// ////////////////////////////////////////////////////////////////////////////////////////////////////////
		for (String keyString : procAttachMap.keySet())
		{
			FoundationObject procFoundation = procAttachMap.get(keyString);
			ClassInfo procAttachClassInfo = this.stubService.getEMM().getClassByGuid(procFoundation.getObjectGuid().getClassGuid());
			if (procAttachClassInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN))
			{
				List<FoundationObject> ecoList = this.stubService.getUECQueryStub().getECOByECNAll(procFoundation.getObjectGuid());
				if (SetUtils.isNullList(ecoList))
				{
					continue;
				}
				for (FoundationObject ecoObj : ecoList)
				{
					List<FoundationObject> attachMent = new ArrayList<FoundationObject>();
					this.putSolveItemInWorkFlow(ecoObj, attachMent);
					if (!SetUtils.isNullList(attachMent))
					{
						for (FoundationObject fo : attachMent)
						{
							if (procAttachMap.get(fo.getObjectGuid().getGuid()) == null)
							{
								// fo = this.stubService.getUECSStub().updateStatusByPrOGuid(fo.getObjectGuid(),
								// procRtGuid);
								ProcAttach procAttach = new ProcAttach();
								procAttach.setInstanceClassGuid(fo.getObjectGuid().getClassGuid());
								procAttach.setInstanceGuid(fo.getObjectGuid().getGuid());
								beAddedProcList.put(procAttach.getInstanceGuid(), procAttach);
							}
						}
					}
				}
			}
			else if (procAttachClassInfo.hasInterface(ModelInterfaceEnum.IECOM) && beAddedProcList.get(procFoundation.getObjectGuid().getGuid()) == null)
			{
				List<FoundationObject> attachMent = new ArrayList<FoundationObject>();
				this.putSolveItemInWorkFlow(procFoundation, attachMent);
				if (SetUtils.isNullList(attachMent))
				{
					continue;
				}
				for (FoundationObject fo : attachMent)
				{
					if (procAttachMap.get(fo.getObjectGuid().getGuid()) == null)
					{
						// fo = this.stubService.getUECSStub().updateStatusByPrOGuid(fo.getObjectGuid(), procRtGuid);
						ProcAttach procAttach = new ProcAttach();
						procAttach.setInstanceClassGuid(fo.getObjectGuid().getClassGuid());
						procAttach.setInstanceGuid(fo.getObjectGuid().getGuid());
						beAddedProcList.put(procAttach.getInstanceGuid(), procAttach);
					}
				}
			}
			else if (procAttachClassInfo.hasInterface(ModelInterfaceEnum.IUpdatedECR))
			{
				List<FoundationObject> ecpList = this.stubService.getUECQueryStub().getECPByECRAll(procFoundation.getObjectGuid());
				if (SetUtils.isNullList(ecpList))
				{
					continue;
				}
				for (FoundationObject fo : ecpList)
				{
					if (procAttachMap.get(fo.getObjectGuid().getGuid()) == null)
					{
						// fo = this.stubService.getUECSStub().updateStatusByPrOGuid(fo.getObjectGuid(), procRtGuid);
						ProcAttach procAttach = new ProcAttach();
						procAttach.setInstanceClassGuid(fo.getObjectGuid().getClassGuid());
						procAttach.setInstanceGuid(fo.getObjectGuid().getGuid());
						beAddedProcList.put(procAttach.getInstanceGuid(), procAttach);
					}
				}
			}
		}

		List<ProcAttach> tmpBeAddedProcList = new ArrayList<ProcAttach>();
		if (!SetUtils.isNullMap(beAddedProcList))
		{
			for (String keyString : beAddedProcList.keySet())
			{
				tmpBeAddedProcList.add(beAddedProcList.get(keyString));
			}
		}
		if (!SetUtils.isNullList(tmpBeAddedProcList))
		{
			ProcAttach[] settings = tmpBeAddedProcList.toArray(new ProcAttach[tmpBeAddedProcList.size()]);
			this.stubService.getWFI().addAttachment(procRtGuid, settings);
		}
	}

	protected List<StructureObject> listStructureObjects(ObjectGuid end1ObjectGuid, String relationTemplate) throws ServiceRequestException
	{
		RelationTemplateInfo relationChangeItemBefore = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, relationTemplate);
		if (relationChangeItemBefore == null)
		{
			return null;
		}
		SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(relationChangeItemBefore.getStructureClassName());
		List<ClassField> classFieldList = this.stubService.getEMM().listFieldOfClass(relationChangeItemBefore.getStructureClassName());
		if (!SetUtils.isNullList(classFieldList))
		{
			for (ClassField classField : classFieldList)
			{
				searchCondition.addResultField(classField.getName());
			}
		}
		return ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(end1ObjectGuid, relationTemplate, searchCondition, null, null, false);
	}

	/**
	 * 
	 * @param structureObjectGuid
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	protected StructureObject getStructureObject(String structureObjectGuid, String className) throws ServiceRequestException
	{
		SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(className);
		List<ClassField> classFieldList = this.stubService.getEMM().listFieldOfClass(className);
		if (!SetUtils.isNullList(classFieldList))
		{
			for (ClassField classField : classFieldList)
			{
				searchCondition.addResultField(classField.getName());
			}
		}
		ObjectGuid oldBom=new ObjectGuid();
		oldBom.setGuid(structureObjectGuid);
		oldBom.setClassName(className);
		return ((BOASImpl) this.stubService.getBOAS()).getStructureStub().getStructureObject(oldBom, searchCondition, false, null);
	}

	/**
	 * 
	 * @param viewObject
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(viewObject, searchCondition, null, null, false);
	}

	public void unlockFoundation(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listAllValidProcAttach = this.stubService.getWFI().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(listAllValidProcAttach))
		{
			return;
		}
		for (ProcAttach oraginAttach : listAllValidProcAttach)
		{
			try
			{
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(oraginAttach.getInstanceClassGuid());
				if (classInfo != null)
				{
					if (classInfo.hasInterface(ModelInterfaceEnum.IECOM))
					{
						// 解锁其对应的解决对象
						FoundationObject foun = this.stubService.getBOAS().getObjectByGuid(
								new ObjectGuid(oraginAttach.getInstanceClassGuid(), null, oraginAttach.getInstanceGuid(), null));
						if (foun != null)
						{
							this.stubService.getUECNECOStub().unlockByEco(foun);
						}
					}
				}
			}
			catch (Exception e)
			{
				DynaLogger.error(e.getMessage(), e.getCause());
				DynaLogger.error(oraginAttach);
			}
		}
	}
}