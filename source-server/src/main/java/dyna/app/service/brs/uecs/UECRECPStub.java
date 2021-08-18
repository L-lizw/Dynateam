/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMReportStub
 * Wanglei 2011-12-21
 */
package dyna.app.service.brs.uecs;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.StructureObjectImpl;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.uecs.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.common.util.UpdatedECSConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class UECRECPStub extends AbstractServiceStub<UECSImpl>
{

	/**
	 * @param ecpfoundation
	 * @param ecrObject
	 * @param parentECP
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject saveECP(FoundationObject ecpfoundation, ObjectGuid ecrObject, ObjectGuid parentECP, String proGuid) throws ServiceRequestException
	{
		if (ecpfoundation == null && ecrObject == null)
		{
			return null;
		}
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(ecrObject, UpdatedECSConstants.ECR_ECP$);
		if (relationTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_UECRECPSTUB_TEMPLATE_NULL", "the template is null", null, UpdatedECSConstants.ECR_ECP$);
		}
		// 新增ECP与变更对象之间的关联
		ObjectGuid changeItemObject = this.stubService.getUECNECOStub().getObjectGuidByKey(ecpfoundation, UpdatedECSConstants.ChangeItem);
		FoundationObject resultEcpFoundation = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 已经存在--更新
			if (!StringUtils.isNullString(ecpfoundation.getObjectGuid().getGuid()))
			{
				ecpfoundation.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
				resultEcpFoundation = this.stubService.getUECRECPStub().saveNotCheckout(ecpfoundation);
				// 判断原来的ECP下变更对象是否一样
				this.stubService.getUECNECOStub().updateChangeItem(changeItemObject, resultEcpFoundation, UpdatedECSConstants.ECP_CHANGEITEM$, proGuid);
			}
			// 不存在--新建
			else
			{
				if (!this.isSingleECP4ECR(changeItemObject, ecrObject))
				{
					throw new ServiceRequestException("ID_APP_UECRECPSTUB_REPEAT_CHANGEITEM", "same changeItem in different ecp is not allowed", null);
				}
				resultEcpFoundation = this.stubService.getUECSStub().createObject(ecpfoundation);
				// 关联ECP和ECR---ECR作为end1,ECP作为end2
				this.stubService.getUECSStub().link(ecrObject, resultEcpFoundation.getObjectGuid(), null, proGuid, UpdatedECSConstants.ECR_ECP$);
				// 新增ECP与变更对象之间的关联
				if (StringUtils.isGuid(changeItemObject.getGuid()))
				{
					this.stubService.getUECSStub().link(resultEcpFoundation.getObjectGuid(), changeItemObject, null, proGuid, UpdatedECSConstants.ECP_CHANGEITEM$);
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
		return resultEcpFoundation;
	}

	/**
	 * 流程中添加变更建议，不允许加进来相同的两条数据
	 * 
	 * @param ecpfoundation
	 * @param ecrObject
	 * @throws ServiceRequestException
	 */
	private boolean isSingleECP4ECR(ObjectGuid changeItemObject, ObjectGuid ecrObject) throws ServiceRequestException
	{
		List<FoundationObject> ecpList = this.stubService.getECPByECRAll(ecrObject);
		if (!SetUtils.isNullList(ecpList))
		{
			for (FoundationObject foundationObject : ecpList)
			{
				ObjectGuid cObject = this.stubService.getUECNECOStub().getObjectGuidByKey(foundationObject, UpdatedECSConstants.ChangeItem);
				if (cObject != null && !StringUtils.isNullString(changeItemObject.getGuid()) && changeItemObject.getGuid().equals(cObject.getGuid()))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 删除ECP:若ECP下没有子节点,则直接删除(包括ECP实例,解除ECP与ECR的关系)deleteAll参数值为true;
	 * 若ECP存在子节点,则由客户端判断是否全部删除还是只删除当前结点
	 * ,以下结点往上移动(删除ECP,将其下子节点的parentObjectGuid清空)
	 * 
	 * @param ecrObjectGuid
	 * @param listEcpObjectGuid
	 * @param deleteAll
	 *            是否全部删除
	 * @throws ServiceRequestException
	 */
	protected void deleteECPorECO(List<ObjectGuid> listEcpOREcoObjectGuid, boolean deleteAll, ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		if (SetUtils.isNullList(listEcpOREcoObjectGuid))
		{
			return;
		}
		String parentString = "";
		if (interfaceEnum == ModelInterfaceEnum.IECPM)
		{
			parentString = "PARENTECP";
		}
		else
		{
			parentString = "PARENTECO";
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 删除传过来的当前结点
			if (!deleteAll)
			{
				for (ObjectGuid ecpORecoObjectGuid : listEcpOREcoObjectGuid)
				{
					FoundationObject ecpORecoFoundation = this.stubService.getBOAS().getObject(ecpORecoObjectGuid);
					ClassInfo foClassInfo = this.stubService.getEMM().getClassByGuid(ecpORecoFoundation.getObjectGuid().getClassGuid());
					if (foClassInfo.hasInterface(ModelInterfaceEnum.IECOM))
					{
						this.deleteECOsInfo(ecpORecoFoundation);
					}
					List<FoundationObject> listSonEcpOrEcoFoundation = this.stubService.getUECQueryStub().listChildbyParentObjectGuid(ecpORecoObjectGuid, parentString);
					this.stubService.getUECSStub().deleteObject(ecpORecoFoundation);
					if (!SetUtils.isNullList(listSonEcpOrEcoFoundation))
					{
						for (FoundationObject sonEcpOrEco : listSonEcpOrEcoFoundation)
						{
							sonEcpOrEco.put(parentString, ecpORecoFoundation.get(parentString));
							sonEcpOrEco.put(parentString + "$MASTER", ecpORecoFoundation.get(parentString + "$MASTER"));
							sonEcpOrEco.put(parentString + "$CLASS", ecpORecoFoundation.get(parentString + "$CLASS"));
							sonEcpOrEco.put(parentString + "$CLASSNAME", ecpORecoFoundation.get(parentString + "$CLASSNAME"));
							this.stubService.saveNotCheckout(sonEcpOrEco);
						}
					}
				}
			}
			// 全部删除
			else
			{
				List<FoundationObject> deleteEcpOrEcoMenuList = new ArrayList<FoundationObject>();
				for (ObjectGuid ecpORecoObjectGuid : listEcpOREcoObjectGuid)
				{
					FoundationObject ecpORecoFo = this.stubService.getBOAS().getObject(ecpORecoObjectGuid);
					deleteEcpOrEcoMenuList.add(ecpORecoFo);
					List<FoundationObject> listALLChildFo = this.stubService.getUECQueryStub().listALLChildbyParentObjectGuid(ecpORecoFo.getObjectGuid(), parentString);
					deleteEcpOrEcoMenuList.addAll(listALLChildFo);
				}
				if (!SetUtils.isNullList(deleteEcpOrEcoMenuList))
				{
					for (FoundationObject fo : deleteEcpOrEcoMenuList)
					{
						ClassInfo foClassInfo = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
						if (foClassInfo.hasInterface(ModelInterfaceEnum.IECOM))
						{
							this.deleteECOsInfo(fo);
						}
						this.stubService.getUECSStub().deleteObject(fo);
					}
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

	public void deleteECOsInfo(FoundationObject ecpORecoFoundation) throws ServiceRequestException
	{
		LifecyclePhaseInfo ecoLife = this.stubService.getEMM().getLifecyclePhaseInfo(ecpORecoFoundation.getLifecyclePhaseGuid());
		if (!ecoLife.getName().equals(ECOLifecyclePhaseEnum.Performing.name()) && !ecoLife.getName().equals(ECOLifecyclePhaseEnum.Canceled.name())
				&& !ecoLife.getName().equals(ECOLifecyclePhaseEnum.Created.name()))
		{
			throw new ServiceRequestException("ID_APP_UECS_ECO_CANNOT_BE_DELETE", "", null, ecpORecoFoundation.getFullName());
		}

		List<StructureObject> ecoChangeItemAfterList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listObjectOfRelation(ecpORecoFoundation.getObjectGuid(),
				UpdatedECSConstants.ECO_CHANGEITEMAFTER$, null, null, null, false);
		if (!SetUtils.isNullList(ecoChangeItemAfterList))
		{
			for (StructureObject structure : ecoChangeItemAfterList)
			{
				ObjectGuid solveObjectGuid = structure.getEnd2ObjectGuid();
				this.stubService.getBOAS().deleteFoundationObject(solveObjectGuid.getGuid(), solveObjectGuid.getClassGuid());
			}
		}

		List<ClassInfo> listEciClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECI);
		ClassInfo eciClassInfo = null;
		if (!SetUtils.isNullList(listEciClassInfo))
		{
			eciClassInfo = listEciClassInfo.get(0);
		}
		List<FoundationObject> eciList = this.stubService.getUECQueryStub().getEND2ByECTypeEND1ByTemp(ecpORecoFoundation.getObjectGuid(), eciClassInfo,
				UpdatedECSConstants.ECO_ECI$, true);
		if (!SetUtils.isNullList(eciList))
		{
			for (FoundationObject eciFo : eciList)
			{
				CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem((String) eciFo.get("CHANGETYPE"));
				if (!codeItemInfo.getName().equals(UECChangeTypeEnum.Others.name()))
				{
					this.stubService.getBOAS().deleteFoundationObject(eciFo.getGuid(), eciFo.getObjectGuid().getClassGuid());
				}
			}
		}

	}

	/**
	 * 增加ECP和ECP类型为普通类型的Content关系
	 * 
	 * @param ecpObjectGuid
	 *            ecp的objectGuid
	 * @param contentObject
	 *            变更内容的objectGuid
	 * @throws ServiceRequestException
	 */
	protected void addECRelation(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, String relationTempalte, String proGuid) throws ServiceRequestException
	{
		this.stubService.getUECSStub().link(end1ObjectGuid, end2ObjectGuid, null, proGuid, relationTempalte);
	}

	/**
	 * 更新ECP的Content实例
	 * 
	 * @param contentFoundation
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject saveNotCheckout(FoundationObject contentFoundation) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(contentFoundation, false, false, false, null, true, false, true);
	}

	protected FoundationObject saveOnly(FoundationObject contentFoundation) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(contentFoundation, false, false, false, false, null, true, false, false, false);
	}

	protected FoundationObject createRevision(FoundationObject contentFoundation, boolean isContainBom) throws ServiceRequestException
	{
		FoundationObject reFoundationObject = this.stubService.getBOAS().createRevision(contentFoundation, isContainBom);
		reFoundationObject = this.stubService.getBOAS().getObject(reFoundationObject.getObjectGuid());
		return reFoundationObject;

	}

	/**
	 * 审核阶段取消ECR
	 * 
	 * @param ecofoundation
	 * @param ecnObject
	 * @param parentECO
	 * @throws ServiceRequestException
	 */
	protected void cancelECR(ObjectGuid ecrObjectGuid) throws ServiceRequestException
	{
		if (ecrObjectGuid == null)
		{
			return;
		}
		FoundationObject ecrFoundation = this.stubService.getBOAS().getObject(ecrObjectGuid);
		if (ecrFoundation == null)
		{
			return;
		}
		try
		{
			List<FoundationObject> listECP = this.stubService.getECPByECRAll(ecrFoundation.getObjectGuid());
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!SetUtils.isNullList(listECP))
			{
				for (FoundationObject ecpFoundation : listECP)
				{
					// 撤销所有的流程
					this.cancelWorkFlow(ecpFoundation);
					ecpFoundation = this.stubService.getBOAS().getObject(ecpFoundation.getObjectGuid());
					ClassInfo ecpClassInfo = this.stubService.getEMM().getClassByGuid(ecpFoundation.getObjectGuid().getClassGuid());
					String lifeCyclePhaseOriginal = ecpFoundation.getLifecyclePhaseGuid();
					String lifeCyclePhaseDest = this.stubService.getEMM().getLifecyclePhaseInfo(ecpClassInfo.getLifecycleName(), ECPLifecyclePhaseEnum.Canceled.name()).getGuid();
					ecpFoundation = this.stubService.getUECSStub().updateLifeCyclePhase(ecpFoundation.getObjectGuid(), lifeCyclePhaseOriginal, lifeCyclePhaseDest);
				}
			}
			// 撤销所有的流程
			this.cancelWorkFlow(ecrFoundation);
			ecrFoundation = this.stubService.getBOAS().getObject(ecrFoundation.getObjectGuid());
			ClassInfo ecrClassInfo = this.stubService.getEMM().getClassByGuid(ecrFoundation.getObjectGuid().getClassGuid());
			String lifeCyclePhaseOriginal = ecrFoundation.getLifecyclePhaseGuid();
			String lifeCyclePhaseDest = this.stubService.getEMM().getLifecyclePhaseInfo(ecrClassInfo.getLifecycleName(), ECRLifecyclePhaseEnum.Canceled.name()).getGuid();
			ecrFoundation = this.stubService.getUECSStub().updateLifeCyclePhase(ecrFoundation.getObjectGuid(), lifeCyclePhaseOriginal, lifeCyclePhaseDest);
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

	/**
	 * 撤销流程
	 * 
	 * @param fo
	 * @throws ServiceRequestException
	 */
	protected void cancelWorkFlow(FoundationObject fo) throws ServiceRequestException
	{
		if (fo.getObjectGuid() != null && StringUtils.isGuid(fo.getObjectGuid().getGuid()))
		{
			List<ProcessRuntime> runList = this.stubService.getWFI().listProcessRuntimeOfObject(fo.getObjectGuid(), null);

			if (!SetUtils.isNullList(runList))
			{
				for (ProcessRuntime processRuntime : runList)
				{
					List<ActivityRuntime> activityList = this.stubService.getWFI().listCurrentActivityRuntime(processRuntime.getGuid());
					if (!SetUtils.isNullList(activityList))
					{
						ActivityRuntime activity = activityList.get(0);
						if (activity != null & StringUtils.isGuid(activity.getGuid()))
						{
							this.stubService.getWFI().recallProcessRuntime(processRuntime.getGuid(), activity.getGuid());
						}
					}

				}
			}
		}

	}

	/**
	 * @param ecpFoundationObject
	 * @param structureObjectList
	 * @param deleteList
	 * @param ecpChangeitembefore$
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveECP_ECI_ChangeItem(FoundationObject ecpFoundationObject, List<StructureObject> structureObjectList, List<StructureObject> deleteList,
			String proGuid) throws ServiceRequestException
	{
		if (ecpFoundationObject == null || ecpFoundationObject.getObjectGuid() == null)
		{
			return null;
		}
		FoundationObject returnvalue = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			returnvalue = this.stubService.getUECRECPStub().saveNotCheckout(ecpFoundationObject);
			if (!SetUtils.isNullList(deleteList))
			{
				// 先删除需要删除的ECI
				this.stubService.getUECNECOStub().deleteECI(ecpFoundationObject.getObjectGuid(), deleteList);
			}

			if (SetUtils.isNullList(structureObjectList))
			{
//				DataServer.getTransactionManager().commitTransaction();
				return returnvalue;
			}

			// 批量新增时,判断顺序号是否重复,判断BOM结构是否循环
			String typeGuid = (String) ecpFoundationObject.get(UpdatedECSConstants.ModifyType);
			String bomTemplateName = (String) ecpFoundationObject.get(UpdatedECSConstants.BOMTemplate);
			if (!StringUtils.isNullString(typeGuid) && !StringUtils.isNullString(bomTemplateName))
			{
				// 取得结构信息
				List<StructureObjectImpl> bomStructureList = this.stubService.getUECNECOStub().getBOMStructure(structureObjectList);
				CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(typeGuid);
				if (codeItem.getName().equals(UECModifyTypeEnum.BatchAdd.name()))
				{
					// 判断顺序是否重复
					StringBuffer errorMessage = new StringBuffer();
					boolean sequenceOk = this.stubService.getUECNECOStub().checkSequenceOfStructureObject(bomTemplateName, bomStructureList, errorMessage);
					if (!sequenceOk)
					{
						throw new ServiceRequestException("ID_APP_UECNECOSTUB_SEQUENCE_ERROR", "sequence is error!", null, errorMessage.toString());
					}

					// 判断BOM结构是否循环
					boolean isCircle = this.stubService.getUECNECOStub().checkCircleOfBOM(bomTemplateName, bomStructureList, errorMessage);
					if (!isCircle)
					{
						throw new ServiceRequestException("ID_APP_UECNECOSTUB_CIRCLE_ERROR", "circle is error!", null, errorMessage.toString());
					}

				}
				else if (codeItem.getName().equals(UECModifyTypeEnum.BatchReplace.name()))
				{
					String repalcePolicy = (String) ecpFoundationObject.get(UpdatedECSConstants.Replacepolicy);
					if (StringUtils.isGuid(repalcePolicy))
					{
						CodeItemInfo policy = this.stubService.getEMM().getCodeItem(repalcePolicy);
						if (policy != null && policy.getName().equals(UECReplacepolicyEnum.MandatoryReplace))
						{
							// 判断BOM结构是否循环
							StringBuffer errorMessage = new StringBuffer();
							boolean isCircle = this.stubService.getUECNECOStub().checkCircleOfBOM(bomTemplateName, bomStructureList, errorMessage);
							if (!isCircle)
							{
								throw new ServiceRequestException("ID_APP_UECNECOSTUB_CIRCLE_ERROR", "circle is error!", null, errorMessage.toString());
							}
						}
					}
				}
			}
			// 批量保存变更项,并且关联到ECP上
			for (int i = 0; i < structureObjectList.size(); i++)
			{
				StructureObject stru = structureObjectList.get(i);
				if (StringUtils.isGuid(stru.getObjectGuid().getGuid()))
				{
					((BOASImpl) this.stubService.getBOAS()).getStructureStub().saveStructure(stru, false);
				}
				else
				{
					this.stubService.getUECSStub().link(ecpFoundationObject.getObjectGuid(), stru.getEnd2ObjectGuid(), stru, proGuid, UpdatedECSConstants.ECP_CHANGEITEMBEFORE$);
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
		return returnvalue;
	}

	/**
	 * 删除ECR之前的脚本
	 * 删除ecr之前,需要删除ecp以及ecp下的ecContent/eci/changeItem/changeItemBefore
	 * 
	 * @param foundationObject
	 * @throws ServiceRequestException
	 */
	public void deleteECR(FoundationObject foundationObject) throws ServiceRequestException
	{
		if (foundationObject != null && foundationObject.getObjectGuid() != null)
		{
			// 取得ECR-ECP
			ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(foundationObject.getObjectGuid(), UpdatedECSConstants.ECR_ECP$);
			if (viewObject != null && viewObject.getObjectGuid() != null)
			{
				try
				{
//					DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
					List<FoundationObject> listECP = this.stubService.getUECSStub().listFoundationObjectOfRelation(viewObject.getObjectGuid(), null);
					if (!SetUtils.isNullList(listECP))
					{
						for (FoundationObject ecpFo : listECP)
						{
							// ECP_ECPCONTENT$
							ViewObject viewObject2 = this.stubService.getBOAS().getRelationByEND1(ecpFo.getObjectGuid(), UpdatedECSConstants.ECP_ECPCONTENT$);
							if (viewObject2 != null && viewObject2.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject2, true, false);
							}
							// ECP_ECO$
							ViewObject viewObject1 = this.stubService.getBOAS().getRelationByEND1(ecpFo.getObjectGuid(), UpdatedECSConstants.ECP_ECO$);
							if (viewObject1 != null && viewObject1.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject1, false, false);
							}
							// ECP_CHANGEITEMBEFORE$
							ViewObject viewObject3 = this.stubService.getBOAS().getRelationByEND1(ecpFo.getObjectGuid(), UpdatedECSConstants.ECP_CHANGEITEMBEFORE$);
							if (viewObject3 != null && viewObject3.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject3, false, false);
							}
							// ECP_CHANGEITEM$
							ViewObject viewObject4 = this.stubService.getBOAS().getRelationByEND1(ecpFo.getObjectGuid(), UpdatedECSConstants.ECP_CHANGEITEM$);
							if (viewObject4 != null && viewObject4.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject4, false, false);
							}
							// 删除ecp
							this.stubService.getUECSStub().deleteObject(ecpFo);
						}
					}
//					DataServer.getTransactionManager().commitTransaction();
				}
				catch (DynaDataException e)
				{
//					DataServer.getTransactionManager().rollbackTransaction();
					throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
				}
				catch (Exception e)
				{
//					DataServer.getTransactionManager().rollbackTransaction();
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
		}
	}
}