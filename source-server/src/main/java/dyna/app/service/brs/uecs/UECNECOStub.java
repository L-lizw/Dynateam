/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMReportStub
 * Wanglei 2011-12-21
 */
package dyna.app.service.brs.uecs;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.*;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.systemenum.uecs.ECNLifecyclePhaseEnum;
import dyna.common.systemenum.uecs.ECOLifecyclePhaseEnum;
import dyna.common.systemenum.uecs.UECModifyTypeEnum;
import dyna.common.systemenum.uecs.UECReplacepolicyEnum;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.ec.ECService;
import dyna.data.service.ins.InstanceService;
import dyna.net.service.brs.BOMS;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Wanglei
 * 
 */
@Component
public class UECNECOStub extends AbstractServiceStub<UECSImpl>
{
	// 缓存正在start的eco，startECO过程中再次start会造成数据异常
	private static Set<String> runningECOSet = new HashSet<String>();

	/**
	 * 批量ECO页面时,批量增加变更项 所有的变更项都关联ECO
	 * 
	 * @param ecoFoundationObject
	 *            eco实例
	 * @param structureObjectList
	 *            增加的变更项关系结构
	 * @param deleteList
	 *            需要删除的变更项--已经有Guid的structureObject
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject saveECO_ECI_ChangeItem(FoundationObject ecoFoundation, List<StructureObject> structureObjectList, List<StructureObject> deleteList, String proguid)
			throws ServiceRequestException
	{
		if (ecoFoundation == null || ecoFoundation.getObjectGuid() == null)
		{
			return null;
		}
		FoundationObject resultEco = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			Object element = ecoFoundation.get(UpdatedECSConstants.ChangeItem);
			if (element != null)
			{
				if (element instanceof FoundationObject)
				{
					FoundationObject fo = (FoundationObject) element;
					ecoFoundation.put(UpdatedECSConstants.ChangeItem, fo.getGuid());
					ecoFoundation.put(UpdatedECSConstants.ChangeItem + UpdatedECSConstants.ClassGuid, fo.getObjectGuid().getClassGuid());
					ecoFoundation.put(UpdatedECSConstants.ChangeItem + UpdatedECSConstants.CLASSNAME, fo.getObjectGuid().getClassName());
					ecoFoundation.put(UpdatedECSConstants.ChangeItem + UpdatedECSConstants.MASTER, fo.getObjectGuid().getMasterGuid());
				}
			}
			else
			{
				ecoFoundation.put(UpdatedECSConstants.ChangeItem, null);
				ecoFoundation.put(UpdatedECSConstants.ChangeItem + UpdatedECSConstants.ClassGuid, null);
				ecoFoundation.put(UpdatedECSConstants.ChangeItem + UpdatedECSConstants.CLASSNAME, null);
				ecoFoundation.put(UpdatedECSConstants.ChangeItem + UpdatedECSConstants.MASTER, null);
			}

			ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecoFoundation.getObjectGuid().getClassGuid());
			if (ecoClassInfo.hasInterface(ModelInterfaceEnum.IIndependenceForEc))
			{
				resultEco = this.saveECO(ecoFoundation, null, null, proguid);
			}
			else
			{
				ObjectGuid ecnfoObject = null;
				List<FoundationObject> ecnList = this.stubService.getUECQueryStub().getWhereUsedECNByECO(ecoFoundation.getObjectGuid());
				if (!SetUtils.isNullList(ecnList))
				{
					ecnfoObject = ecnList.get(0).getObjectGuid();
				}
				resultEco = this.saveECO(ecoFoundation, ecnfoObject, null, proguid);
			}
			if (!SetUtils.isNullList(deleteList))
			{
				// 先删除需要删除的ECI
				this.deleteECI(ecoFoundation.getObjectGuid(), deleteList);
			}

			if (SetUtils.isNullList(structureObjectList))
			{
//				DataServer.getTransactionManager().commitTransaction();
				return resultEco;
			}
			// 检查锁定,并且父阶对象必须是发布对象
			this.checkBathChangeItem(ecoFoundation, structureObjectList);
			// 批量新增时,判断顺序号是否重复,判断BOM结构是否循环
			String typeGuid = (String) ecoFoundation.get(UpdatedECSConstants.ModifyType);
			String bomTemplateName = (String) ecoFoundation.get(UpdatedECSConstants.BOMTemplate);
			if (!StringUtils.isNullString(typeGuid) && !StringUtils.isNullString(bomTemplateName))
			{
				// 取得结构信息
				List<StructureObjectImpl> bomStructureList = this.getBOMStructure(structureObjectList);
				CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(typeGuid);
				if (codeItem.getName().equals(UECModifyTypeEnum.BatchAdd.name()))
				{
					// 判断顺序是否重复
					StringBuffer errorMessage = new StringBuffer();
					boolean sequenceOk = this.checkSequenceOfStructureObject(bomTemplateName, bomStructureList, errorMessage);
					if (!sequenceOk)
					{
						throw new ServiceRequestException("ID_APP_UECNECOSTUB_SEQUENCE_ERROR", "sequence is error!", null, errorMessage.toString());
					}

					// 判断BOM结构是否循环
					boolean isCircle = this.checkCircleOfBOM(bomTemplateName, bomStructureList, errorMessage);
					if (!isCircle)
					{
						throw new ServiceRequestException("ID_APP_UECNECOSTUB_CIRCLE_ERROR", "circle is error!", null, errorMessage.toString());
					}
				}
				else if (codeItem.getName().equals(UECModifyTypeEnum.BatchReplace.name()))
				{
					String repalcePolicy = (String) ecoFoundation.get(UpdatedECSConstants.Replacepolicy);
					if (StringUtils.isGuid(repalcePolicy))
					{
						CodeItemInfo policy = this.stubService.getEMM().getCodeItem(repalcePolicy);
						if (policy != null && policy.getName().equals(UECReplacepolicyEnum.MandatoryReplace))
						{
							// 判断BOM结构是否循环
							StringBuffer errorMessage = new StringBuffer();
							boolean isCircle = this.checkCircleOfBOM(bomTemplateName, bomStructureList, errorMessage);
							if (!isCircle)
							{
								throw new ServiceRequestException("ID_APP_UECNECOSTUB_CIRCLE_ERROR", "circle is error!", null, errorMessage.toString());
							}
						}
					}
				}
			}

			// 批量保存变更项,并且关联到ECO上
			for (int i = 0; i < structureObjectList.size(); i++)
			{
				StructureObject stru = structureObjectList.get(i);
				stru.resetEnd1ObjectGuid();
				stru.resetEnd2ObjectGuid();
				if (StringUtils.isGuid(stru.getObjectGuid().getGuid()))
				{
					((BOASImpl) this.stubService.getBOAS()).getStructureStub().saveStructure(stru, false);
				}
				else
				{
					this.stubService.getUECSStub().link(ecoFoundation.getObjectGuid(), stru.getEnd2ObjectGuid(), stru, proguid, UpdatedECSConstants.ECO_CHANGEITEMBEFORE$);
				}
				// 批量ECO时，需要对父阶对象锁定
				FoundationObject changeItem = this.stubService.getBOAS().getObject(stru.getEnd2ObjectGuid());
				if (changeItem != null && StringUtils.isGuid(changeItem.getObjectGuid().getGuid()))
				{
					changeItem.setECFlag(ecoFoundation.getObjectGuid());
					this.stubService.getUECRECPStub().saveOnly(changeItem);
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
		return resultEco;
	}

	/**
	 * @param ecoFoundationObject
	 * @param structureObjectList
	 * @throws ServiceRequestException
	 */
	private void checkBathChangeItem(FoundationObject ecoFoundationObject, List<StructureObject> structureObjectList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(structureObjectList))
		{
			return;
		}
		StringBuffer errorMessageLock = new StringBuffer();
		StringBuffer errorMessageStatus = new StringBuffer();
		for (StructureObject stru : structureObjectList)
		{
			if (stru.getEnd2ObjectGuid() == null || StringUtils.isGuid(stru.getObjectGuid().getGuid()))
			{
				continue;
			}
			FoundationObject end2Fo = this.stubService.getBOAS().getObject(stru.getEnd2ObjectGuid());
			if (end2Fo != null)
			{
				// 父阶对象必须是发布对象
				String laterst = (String) end2Fo.get("LATESTREVISION$");
				if (StringUtils.isNullString(laterst) || !laterst.contains("r"))
				{
					if (errorMessageStatus.length() > 0)
					{
						errorMessageStatus.append(";");
					}
					errorMessageStatus.append(end2Fo.getFullName());
					continue;
				}
			}
			if (end2Fo.getECFlag() != null && StringUtils.isGuid(end2Fo.getECFlag().getGuid()))
			{
				try
				{
					FoundationObject ecFou = this.stubService.getBOAS().getObject(end2Fo.getECFlag());
					if (ecFou != null)
					{
						if (errorMessageLock.length() > 0)
						{
							errorMessageLock.append(";");
						}
						errorMessageLock.append(end2Fo.getFullName());
					}
				}
				catch (ServiceRequestException e)
				{
					if (!DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT.getMsrId().equals(e.getMsrId()))
					{
						throw e;
					}
				}
			}
		}
		if (errorMessageStatus.length() > 0)
		{
			throw new ServiceRequestException("ID_APP_UECNECOSTUB_PARENTOBJECT_NOT_RELASEAD", "parent instance is not released!", null, ecoFoundationObject.getFullName(),
					errorMessageStatus.toString());

		}
		if (errorMessageLock.length() > 0)
		{
			throw new ServiceRequestException("ID_APP_UECNECOSTUB_BATHCHANGEITEM_ERROR", "parent instance is used!", null, ecoFoundationObject.getFullName(),
					errorMessageLock.toString());
		}
	}

	/**
	 * 在关联关系的结构中取得BOMStructure上的信息
	 * 
	 * @param structureObjectList
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<StructureObjectImpl> getBOMStructure(List<StructureObject> structureObjectList) throws ServiceRequestException
	{
		List<StructureObjectImpl> returnValue = null;
		if (!SetUtils.isNullList(structureObjectList))
		{
			returnValue = new ArrayList<StructureObjectImpl>();
			for (StructureObject structure : structureObjectList)
			{
				String value = this.getJsonString(structure, UpdatedECSConstants.ECChangeRecord1, UpdatedECSConstants.ECChangeRecord2, UpdatedECSConstants.ECChangeRecord3);
				if (!StringUtils.isNullString(value))
				{
					StructureObjectImpl bomStructure = JsonUtils.getObjectByJsonStr(value, StructureObjectImpl.class);
					bomStructure.setEnd1ObjectGuid(structure.getEnd2ObjectGuid());
					returnValue.add(bomStructure);
				}
			}
		}
		return returnValue;
	}

	/**
	 * @param templateName
	 * @param structureObjectList
	 * @param errorMessage
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean checkCircleOfBOM(String templateName, List<StructureObjectImpl> eciBomStructureObjectList, StringBuffer errorMessage) throws ServiceRequestException
	{
		if (SetUtils.isNullList(eciBomStructureObjectList))
		{
			return true;
		}
		for (StructureObjectImpl eciBomStructure : eciBomStructureObjectList)
		{
			if (eciBomStructure.getEnd1ObjectGuid().equals(eciBomStructure.getEnd2ObjectGuid()))
			{
				if (errorMessage.length() > 0)
				{
					errorMessage.append(";");
				}
				FoundationObject foun = this.stubService.getBOAS().getObject(eciBomStructure.getEnd1ObjectGuid());
				errorMessage.append(foun.getFullName());
			}
			List<BOMStructure> end2BomStructureList = this.stubService.getBOMS().listBOM(eciBomStructure.getEnd2ObjectGuid(), templateName, null, null, null);
			if (!SetUtils.isNullList(end2BomStructureList))
			{
				List<StructureObjectImpl> tempEnd2BomStructureList = new ArrayList<StructureObjectImpl>();
				for (BOMStructure end2BomStructure : end2BomStructureList)
				{
					tempEnd2BomStructureList.add(end2BomStructure);
				}
				this.checkCircleOfBOM(templateName, tempEnd2BomStructureList, errorMessage);
			}
		}
		if (errorMessage.length() > 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * 先解除ECO与changeItem的关系
	 * 
	 * @param ecoFoundationObject
	 * @param deleteList
	 * @throws ServiceRequestException
	 */
	protected void deleteECI(ObjectGuid ecoObjectGuid, List<StructureObject> deleteList) throws ServiceRequestException
	{
		if (ecoObjectGuid == null)
		{
			return;
		}
		if (!SetUtils.isNullList(deleteList))
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(ecoObjectGuid.getClassGuid());
			for (StructureObject structure : deleteList)
			{
				if (structure.getEnd2ObjectGuid() != null && StringUtils.isGuid(structure.getObjectGuid().getGuid()))
				{
					// 删除
					((BOASImpl) this.stubService.getBOAS()).getRelationUnlinkStub().unlink(structure, false);
					// 解除ECO与实例之间的锁
					if (classInfo.hasInterface(ModelInterfaceEnum.IECOM))
					{
						FoundationObject foundation = this.stubService.getBOAS().getObject(structure.getEnd2ObjectGuid());
						if (foundation != null)
						{
							foundation.setECFlag(null);
							this.stubService.getUECRECPStub().saveOnly(foundation);
						}
					}
				}
			}
		}
	}

	// /**
	// * 将转行成json的String拆分到三个字段中去
	// *
	// * @param foun
	// * @param jsonStr
	// * @param value
	// * @param value1
	// * @param value2
	// * @throws ServiceRequestException
	// */
	// protected void getStringValue(FoundationObject foun, String jsonStr, String value, String value1, String value2)
	// throws ServiceRequestException
	// {
	// if (foun != null && !StringUtils.isNullString(jsonStr))
	// {
	// int byteSize = jsonStr.getBytes().length;
	// if (byteSize <= 4000)
	// {
	// foun.put(value, jsonStr);
	// }
	// else
	// {
	// if (jsonStr.length() > 1330)
	// {
	// foun.put(value, jsonStr.substring(0, 1330));
	// String v1 = jsonStr.substring(1330, jsonStr.length());
	// if (v1.getBytes().length > 4000)
	// {
	// if (v1.length() > 1330)
	// {
	// foun.put(value1, v1.substring(0, 1330));
	// String v2 = v1.substring(1330, v1.length());
	// if (v2.getBytes().length > 4000)
	// {
	// throw new ServiceRequestException("ID_APP_UECNECOSTUB_STRING_SO_LONG",
	// "structure to string so long", null, jsonStr);
	// }
	// else
	// {
	// foun.put(value2, v2);
	// }
	// }
	// else
	// {
	// foun.put(value1, v1);
	// }
	// }
	// else
	// {
	// foun.put(value1, v1);
	// }
	// }
	// else
	// {
	// foun.put(value, jsonStr);
	// }
	// }
	// }
	// }

	/**
	 * 将转行成json的String拆分到三个字段中去
	 * 
	 * @param foun
	 * @param jsonStr
	 * @param value
	 * @param value1
	 * @param value2
	 * @throws ServiceRequestException
	 */
	protected void getStringValue(FoundationObject foun, String jsonStr, String value, String value1, String value2) throws ServiceRequestException
	{
		try
		{
			String str = jsonStr;
			if (jsonStr != null && jsonStr.getBytes("UTF-8").length > 12000 - 500)
			{
				throw new ServiceRequestException("ID_APP_UECNECOSTUB_STRING_SO_LONG", "structure to string so long > 11500", null, jsonStr.getBytes("UTF-8").length + "");
			}

			String vstr = this.substringByBytes(jsonStr);
			foun.put(value, vstr);

			str = str.substring(vstr.length());
			vstr = this.substringByBytes(str);
			foun.put(value1, vstr);

			str = str.substring(vstr.length());
			vstr = this.substringByBytes(str);
			foun.put(value2, vstr);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	private String substringByBytes(String str) throws UnsupportedEncodingException
	{
		if (str == null || str.length() == 0)
		{
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		for (int j = 0; j < str.length(); j++)
		{
			if ((buffer.toString() + str.substring(j, j + 1)).getBytes("UTF-8").length > 4000)
			{
				break;
			}
			buffer.append(str.substring(j, j + 1));
		}

		return buffer.toString();
	}

	/**
	 * 判断顺序是否有重复
	 * 
	 * @param templateId
	 * @param eciFoundationObjectList
	 * @param structureObjectList
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean checkSequenceOfStructureObject(String templateName, List<StructureObjectImpl> eciBomStructureObjectList, StringBuffer errorMessage)
			throws ServiceRequestException
	{
		// 判断end1下所有的StructureObject是否与新增加structureObjectList的存在相同的sequence
		for (StructureObjectImpl eciStructure : eciBomStructureObjectList)
		{
			List<BOMStructure> listExistsStructure = this.stubService.getBOMS().listBOM(eciStructure.getEnd1ObjectGuid(), templateName, null, null, null);
			if (SetUtils.isNullList(listExistsStructure))
			{
				continue;
			}
			for (BOMStructure existsBomStru : listExistsStructure)
			{
				if (Integer.parseInt(eciStructure.getSequence()) == Integer.parseInt(existsBomStru.getSequence()))
				{
					if (errorMessage.length() != 0)
					{
						errorMessage.append(";");
					}
					FoundationObject foun = this.stubService.getBOAS().getObject(eciStructure.getEnd1ObjectGuid());
					errorMessage.append(foun.getFullName());
				}
			}
		}
		if (errorMessage.length() > 0)
		{
			return false;
		}
		return true;
	}

	protected void linkECO_ECI(ObjectGuid ecoObjectGuid, ObjectGuid eciObjectGuid, String proGuid) throws ServiceRequestException
	{
		this.stubService.getUECSStub().link(ecoObjectGuid, eciObjectGuid, null, proGuid, UpdatedECSConstants.ECO_ECI$);
	}

	/**
	 * 批量将ECP转换成ECO 普通类型转换成普通的ECO,批量类型转换成批量的ECO
	 * 将ECP对象某些字段值赋值到ECO中,并且将ECO与ECP关联起来,且ECN与ECO关联起来
	 * 
	 * @param ecnObjectGuid
	 *            ecn的ObjectGuid
	 * @param ecpObjectGuid
	 *            批量ECP
	 * @throws ServiceRequestException
	 */
	protected Map<String, ServiceRequestException> exchangeECP_ECO(ObjectGuid ecnObjectGuid, List<ObjectGuid> ecpObjectGuidList, String proGuid) throws ServiceRequestException
	{
		if (SetUtils.isNullList(ecpObjectGuidList))
		{
			return null;
		}
		Map<String, ServiceRequestException> exception = new HashMap<String, ServiceRequestException>();
		for (ObjectGuid ecpObjectGuid : ecpObjectGuidList)
		{
			try
			{
				FoundationObject ecpFoun = this.stubService.getBOAS().getObject(ecpObjectGuid);
				if (ecpFoun == null)
				{
					continue;
				}
				ClassInfo ecpClassInfo = this.stubService.getEMM().getClassByGuid(ecpObjectGuid.getClassGuid());
				ClassInfo ecoClassInfo = null;
				List<ClassInfo> listecoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
				if (!SetUtils.isNullList(listecoClassInfo))
				{
					for (ClassInfo tmpEcoClassInfo : listecoClassInfo)
					{
						if (ecpClassInfo.hasInterface(ModelInterfaceEnum.INormalForEc) && tmpEcoClassInfo.hasInterface(ModelInterfaceEnum.INormalForEc)
								&& !tmpEcoClassInfo.hasInterface(ModelInterfaceEnum.IIndependenceForEc))
						{
							ecoClassInfo = tmpEcoClassInfo;
							break;
						}
						if (ecpClassInfo.hasInterface(ModelInterfaceEnum.IBatchForEc) && tmpEcoClassInfo.hasInterface(ModelInterfaceEnum.IBatchForEc)
								&& !tmpEcoClassInfo.hasInterface(ModelInterfaceEnum.IIndependenceForEc))
						{
							ecoClassInfo = tmpEcoClassInfo;
							break;
						}
					}
				}
				if (ecoClassInfo == null)
				{
					continue;
				}
				// wangwx修改--假如ECP已经转换成ECO后,就不再转(不考虑ECO的状态)
				List<FoundationObject> existECOlist = this.stubService.getUECQueryStub().getEND2ByECTypeEND1ByTemp(ecpFoun.getObjectGuid(), ecoClassInfo,
						UpdatedECSConstants.ECP_ECO$, false);
				if (!SetUtils.isNullList(existECOlist))
				{
					throw new ServiceRequestException("ID_APP_UECNECOSTUB_ECP_EXCHANGED_ECO_ALREADY", "ecp has already exchanged eco!", null);
				}
				FoundationObject newEcoFoundation = this.stubService.getBOAS().newFoundationObject(ecoClassInfo.getGuid(), ecoClassInfo.getName());
				String ecTypeString = this.stubService.getEMM().getCurrentBizObject(ecoClassInfo.getGuid()).getTitle(this.stubService.getUserSignature().getLanguageEnum());
				newEcoFoundation.put(UpdatedECSConstants.ECType, ecTypeString);

//				DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
				if (ecpFoun.get("CHANGEITEM") == null || StringUtils.isNullString((String) ecpFoun.get("CHANGEITEM")))
				{
					throw new ServiceRequestException("ID_APP_UECNECOSTUB_CHANGEITEM_ISINEXISTENT", "changeIt is inexistent", null);
				}
				else
				{
					ObjectGuid changeItemObject = new ObjectGuid();
					changeItemObject.setGuid((String) ecpFoun.get("CHANGEITEM"));
					changeItemObject.setClassGuid((String) ecpFoun.get("CHANGEITEM$CLASS"));
					changeItemObject.setMasterGuid((String) ecpFoun.get("CHANGEITEM$MASTER"));
					ClassInfo changeItemClass = this.stubService.getEMM().getClassByGuid(changeItemObject.getClassGuid());
					changeItemObject.setClassName(changeItemClass.getName());
					try
					{
						this.stubService.getBOAS().getObject(changeItemObject);
					}
					catch (ServiceRequestException e)
					{
						if (DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT.getMsrId().equals(e.getMsrId()))
						{
							throw new ServiceRequestException("ID_APP_UECNECOSTUB_CHANGEITEM_ISINEXISTENT", "changeIt is inexistent", null);
						}
						else
						{
							throw e;
						}
					}
				}
				this.createECOByECP(ecnObjectGuid, ecpFoun, newEcoFoundation, ecpClassInfo, proGuid);
//				DataServer.getTransactionManager().commitTransaction();
			}
			catch (Exception e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
				if (e instanceof ServiceRequestException)
				{
					exception.put(ecpObjectGuid.getGuid(), (ServiceRequestException) e);
				}
				else
				{
					throw (ServiceRequestException) e;
				}
			}
		}
		return exception;
	}

	/**
	 * @param ecnObjectGuid
	 * @param ecpFoun
	 * @param tempMap
	 * @param boInfo
	 * @param foun
	 * @throws ServiceRequestException
	 */
	private void createECOByECP(ObjectGuid ecnObjectGuid, FoundationObject ecpFoun, FoundationObject ecoFoun, ClassInfo ecpClassInfo, String proGuid) throws ServiceRequestException
	{
		boolean isNeedtoPlusTheSameEco = false;
		if (ecpClassInfo.hasInterface(ModelInterfaceEnum.INormalForEc))
		{
			List<ClassInfo> listEcoClassInfo = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IECOM);
			ClassInfo ecoClassInfo = null;
			if (!SetUtils.isNullList(listEcoClassInfo))
			{
				for (ClassInfo tempEcoClassInfo : listEcoClassInfo)
				{
					if (tempEcoClassInfo.hasInterface(ModelInterfaceEnum.INormalForEc) && !tempEcoClassInfo.hasInterface(ModelInterfaceEnum.IIndependenceForEc))
					{
						ecoClassInfo = tempEcoClassInfo;
					}
				}
			}
			List<FoundationObject> ListExistsCommonEco = this.stubService.getUECQueryStub().getEND2ByECTypeEND1ByTemp(ecnObjectGuid, ecoClassInfo, UpdatedECSConstants.ECN_ECO$,
					true);
			if (!SetUtils.isNullList(ListExistsCommonEco))
			{
				for (FoundationObject existsCommonEco : ListExistsCommonEco)
				{
					if (existsCommonEco.get(UpdatedECSConstants.ChangeItem) == null)
					{
						continue;
					}
					if (ecpFoun.get(UpdatedECSConstants.ChangeItem).equals(existsCommonEco.get(UpdatedECSConstants.ChangeItem)))
					{
						ecoFoun = existsCommonEco;
						isNeedtoPlusTheSameEco = true;
						break;
					}
				}
			}
		}
		if (!isNeedtoPlusTheSameEco)
		{
			this.setECOValueByECP(ecpFoun, ecoFoun);
			ecoFoun = this.saveECO(ecoFoun, ecnObjectGuid, null, proGuid);
		}

		// 取得普通ECP的content,新建,挂在eco下
		if (ecpClassInfo.hasInterface(ModelInterfaceEnum.INormalForEc))
		{
			List<FoundationObject> listContent = this.stubService.getECPCONTENTByECP(ecpFoun.getObjectGuid());
			if (!SetUtils.isNullList(listContent))
			{
				for (FoundationObject contentFoun : listContent)
				{
					// I11436/A.2-从ECP生成ECN，ECP变更建议项的文件未带到ECO的变更建议项中
					ObjectGuid ecpContentObjectGuid = new ObjectGuid(contentFoun.getObjectGuid());
					contentFoun.clear(SystemObject.GUID);
					contentFoun.setGuid(null);
					FoundationObject newContentFoun = this.stubService.getUECSStub().createObject(contentFoun);
					if (newContentFoun != null && newContentFoun.getObjectGuid() != null)
					{
						// ECPContent下面的文件另存一份在转化的ECOContent下
						this.stubService.getDSS().copyFile(newContentFoun.getObjectGuid(), ecpContentObjectGuid);
						this.stubService.getUECSStub().link(ecoFoun.getObjectGuid(), newContentFoun.getObjectGuid(), null, proGuid, UpdatedECSConstants.ECO_ECOCONTENT$);
					}
				}
			}
		}
		// 取得批量ECP的ECI,重新新建ECI,挂在ECO下
		else
		{
			List<StructureObject> listECI_ChangeItem = this.stubService.getUECSStub().listStructureObjects(ecpFoun.getObjectGuid(), UpdatedECSConstants.ECP_CHANGEITEMBEFORE$);
			if (!SetUtils.isNullList(listECI_ChangeItem))
			{
				RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(ecoFoun.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEMBEFORE$);
				if (relationTemplate == null)
				{
					throw new ServiceRequestException("ID_APP_UECRECPSTUB_TEMPLATE_NULL", "the template is null", null, UpdatedECSConstants.ECO_CHANGEITEMBEFORE$);
				}
				List<StructureObject> listStructure = new ArrayList<StructureObject>();
				for (StructureObject eciStructure : listECI_ChangeItem)
				{
					eciStructure.clear(SystemObject.GUID);
					StructureObject newECIFoun = this.stubService.getBOAS().newStructureObject(null, relationTemplate.getStructureClassName());
					newECIFoun.setEnd1ObjectGuid(ecoFoun.getObjectGuid());
					newECIFoun.setEnd2ObjectGuid(eciStructure.getEnd2ObjectGuid());
					newECIFoun.put(UpdatedECSConstants.ECChangeRecord1, eciStructure.get(UpdatedECSConstants.ECChangeRecord1));// BOM结构String
					newECIFoun.put(UpdatedECSConstants.ECChangeRecord2, eciStructure.get(UpdatedECSConstants.ECChangeRecord2));// BOM结构String
					newECIFoun.put(UpdatedECSConstants.ECChangeRecord3, eciStructure.get(UpdatedECSConstants.ECChangeRecord3));// BOM结构String
					newECIFoun.put(UpdatedECSConstants.ActionType, eciStructure.get(UpdatedECSConstants.ActionType));// 变更方式
					listStructure.add(newECIFoun);
				}
				this.saveECO_ECI_ChangeItem(ecoFoun, listStructure, null, proGuid);
			}
		}
		// 不需要叠加时,ecp和eco才关联---
		// I11470/A.2-ECR-变更建议tab-关联变更通知：当ECN中对ECP多次点击启动，会出现多个重复的关联
		if (!isNeedtoPlusTheSameEco)
		{
			this.stubService.getUECSStub().link(ecpFoun.getObjectGuid(), ecoFoun.getObjectGuid(), null, proGuid, UpdatedECSConstants.ECP_ECO$);
		}
	}

	/**
	 * @param ecpFoun
	 * @param ecoFoun
	 * @throws ServiceRequestException
	 */
	private void setECOValueByECP(FoundationObject ecpFoun, FoundationObject ecoFoun) throws ServiceRequestException
	{
		List<UIField> uiField = this.stubService.getEMM().listFormUIField(ecoFoun.getObjectGuid().getClassName());
		if (!SetUtils.isNullList(uiField) && uiField.size() > 0)
		{
			for (UIField f : uiField)
			{
				if (f.getName().equals(UpdatedECSConstants.ECType))
				{
					continue;
				}
				if (f.getType().equals(FieldTypeEnum.OBJECT))
				{
					this.getObjectGuidByMap(ecpFoun, ecoFoun, f.getName());
				}
				else
				{
					if (ecpFoun.get(f.getName()) != null)
					{
						ecoFoun.put(f.getName(), ecpFoun.get(f.getName()));
					}
				}
			}

			// 取得编码规则Id
			// String id = this.stubService.getBOAS().allocateUniqueId(ecoFoun);
			// if (!StringUtils.isNullString(id)) {
			// ecoFoun.put(SystemClassFieldEnum.ID.getName(), id);
			// } else {
			ecoFoun.put(SystemClassFieldEnum.ID.getName(), ecpFoun.get(SystemClassFieldEnum.ID.getName()));
			// }
			// 增加BOInfo
			ecoFoun.put(UpdatedECSConstants.BOMInfo, ecpFoun.get(UpdatedECSConstants.BOMInfo));
			ecoFoun.put(UpdatedECSConstants.BOMInfo1, ecpFoun.get(UpdatedECSConstants.BOMInfo1));
			ecoFoun.put(UpdatedECSConstants.BOMInfo2, ecpFoun.get(UpdatedECSConstants.BOMInfo2));
			ecoFoun.put(UpdatedECSConstants.ModifyType, ecpFoun.get(UpdatedECSConstants.ModifyType));
			ecoFoun.put(UpdatedECSConstants.BOMTemplate, ecpFoun.get(UpdatedECSConstants.BOMTemplate));
			ecoFoun.put(UpdatedECSConstants.Replacepolicy, ecpFoun.get(UpdatedECSConstants.Replacepolicy));
			ecoFoun.put(UpdatedECSConstants.replaceRate, ecpFoun.get(UpdatedECSConstants.replaceRate));
			this.getObjectGuidByMap(ecpFoun, ecoFoun, UpdatedECSConstants.ChangeItem);
			this.getObjectGuidByMap(ecpFoun, ecoFoun, UpdatedECSConstants.TargetItem);

		}

	}

	/**
	 * @param ecpFoun
	 * @param ecoFoun
	 * @param classname
	 */
	private void getObjectGuidByMap(FoundationObject ecpFoun, FoundationObject ecoFoun, String fieldName)
	{
		String guid = (String) ecpFoun.get(fieldName);
		if (StringUtils.isGuid(guid))
		{
			ecoFoun.put(fieldName, guid);
		}
		String classname = (String) ecpFoun.get(fieldName + UpdatedECSConstants.CLASSNAME);
		if (!StringUtils.isNullString(classname))
		{
			ecoFoun.put(fieldName + UpdatedECSConstants.CLASSNAME, classname);
		}
		String classguid = (String) ecpFoun.get(fieldName + UpdatedECSConstants.ClassGuid);
		if (!StringUtils.isNullString(classguid))
		{
			ecoFoun.put(fieldName + UpdatedECSConstants.ClassGuid, classguid);
		}
		String masterGuid = (String) ecpFoun.get(fieldName + UpdatedECSConstants.MASTER);
		if (!StringUtils.isNullString(masterGuid))
		{
			ecoFoun.put(fieldName + UpdatedECSConstants.MASTER, masterGuid);
		}

	}

	/**
	 * 启动ECO
	 * 
	 * @param listECOObjectGuid
	 * @throws ServiceRequestException
	 */
	protected void startECO(ObjectGuid ecnObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		if (ecoObjectGuid == null)
		{
			return;
		}
		if (!runningECOSet.contains(ecoObjectGuid.getGuid()))
		{
			runningECOSet.add(ecoObjectGuid.getGuid());
		}
		else
		{
			throw new ServiceRequestException("ID_CLIENT_EC_HAS_STARTED", "eco is starting!", null);
		}
		try
		{
			if (!this.checkECOCanBeStarted(ecoObjectGuid))
			{
				throw new ServiceRequestException("ID_APP_UECNECOSTUB_NOT_START", "can not start eco!", null);
			}
			FoundationObject ecof = this.stubService.getBOAS().getObject(ecoObjectGuid);
			if (ecof == null)
			{
				throw new ServiceRequestException("ID_APP_CANNT_FIND_INSTANCE", "not found instance: ", null);
			}
			List<FoundationObject> end1List = null;
			try
			{
//				DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
				ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecoObjectGuid.getObjectGuid().getClassGuid());
				if (ecoClassInfo.hasInterface(ModelInterfaceEnum.IECOM) && ecoClassInfo.hasInterface(ModelInterfaceEnum.IBatchForEc))
				{
					end1List = this.startBathECO(ecof);
					// I10949, 批量ECO启动后直接完成
					ecof = this.stubService.getBOAS().getObject(ecof.getObjectGuid());
					ecof.put(UpdatedECSConstants.isCompleted, "Y");
					this.stubService.saveNotCheckout(ecof);
					this.stubService.completeEco(ecof.getObjectGuid());
				}
				else
				{
					this.startGeneralECO(ecof);
				}

//				DataServer.getTransactionManager().commitTransaction();
			}
			catch (DynaDataException e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
			catch (Exception e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
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
				if (end1List != null)
				{
					String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
					this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
							.addTask(new UpdateScheduledTask(serviceContext, end1List, bmGuid));
				}
			}
		}
		finally
		{
			runningECOSet.remove(ecoObjectGuid.getGuid());
		}
	}

	/**
	 * 检查ECO能否启动,若能启动,返回可以启动的ECO 检查依据:是否含有解决对象,没有解决对象的就可以启动
	 * 
	 * @param listECOObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean checkECOCanBeStarted(ObjectGuid ECOobjectGuid) throws ServiceRequestException
	{
		FoundationObject foun = this.stubService.getBOAS().getObject(ECOobjectGuid);
		// 生命周期是取消的过滤掉
		LifecyclePhaseInfo ecnLifecyclePhase = this.stubService.getEMM().getLifecyclePhaseInfo(foun.getLifecyclePhaseGuid());
		// 如果ecn的生命周期为Created或者Reviewing阶段,就置eco的生命周期为Created阶段
		if (!ecnLifecyclePhase.getName().equals(ECOLifecyclePhaseEnum.Performing.name()))
		{
			return false;
		}
		List<StructureObject> listStructure = this.stubService.getUECSStub().listStructureObjects(ECOobjectGuid, UpdatedECSConstants.ECO_CHANGEITEMAFTER$);
		if (SetUtils.isNullList(listStructure))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void setJasonMapsValue(BOMStructure jasonMap, String objClassName) throws ServiceRequestException
	{
		try
		{
			List<ClassField> fieldList = DataServer.getClassModelService().listClassField(objClassName);
			if (fieldList != null)
			{
				for (ClassField field : fieldList)
				{
					if (field.getType().equals(FieldTypeEnum.DATE) && null != jasonMap.get(field.getName()))
					{
						jasonMap.put(field.getName(), DateFormat.getSDFYMD().parse((String) jasonMap.get(field.getName())));
					}
					else if (field.getType().equals(FieldTypeEnum.DATETIME) && null != jasonMap.get(field.getName()))
					{
						jasonMap.put(field.getName(), DateFormat.getSDFYMDHMS().parse((String) jasonMap.get(field.getName())));
					}
				}
			}
		}
		catch (Exception e)
		{
			throw (ServiceRequestException) e;
		}
	}

	/**
	 * @param ecoBathFoudation
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> startBathECO(FoundationObject ecof) throws ServiceRequestException
	{
		List<FoundationObject> end1List = new ArrayList<FoundationObject>();
		List<StructureObject> eciStructurelist = this.stubService.getUECSStub().listStructureObjects(ecof.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEMBEFORE$);
		if (SetUtils.isNullList(eciStructurelist))
		{
			throw new ServiceRequestException("ID_APP_UECNECOSTUB_CHANGEITEMBEFORE_NULL", "eco's changeItembefore is null", null, ecof.getFullName());
		}

		ObjectGuid tarObjectGuid = this.getObjectGuidByKey(ecof, UpdatedECSConstants.TargetItem);
		ObjectGuid changItem = this.getObjectGuidByKey(ecof, UpdatedECSConstants.ChangeItem);
		String bomTemplateName = (String) ecof.get(UpdatedECSConstants.BOMTemplate);
		CodeItemInfo modifyTypeCode = this.stubService.getEMM().getCodeItem((String) ecof.get(UpdatedECSConstants.ModifyType));
		Map<String, FoundationObject> changeItemAfterFORevisionMap = new HashMap<String, FoundationObject>(); // I14390
		for (StructureObject eciStructure : eciStructurelist)
		{
			String json = this.getJsonString(eciStructure, UpdatedECSConstants.ECChangeRecord1, UpdatedECSConstants.ECChangeRecord2, UpdatedECSConstants.ECChangeRecord3);
			BOMStructure eciJasonStructure = null;
			if (!StringUtils.isNullString(json))
			{
				eciJasonStructure = JsonUtils.getObjectByJsonStr(json, BOMStructure.class);
				ClassStub.decorateObjectGuid(eciJasonStructure.getObjectGuid(), this.stubService);

				this.setJasonMapsValue(eciJasonStructure, eciJasonStructure.getObjectGuid().getClassName());
			}
			List<StructureObjectImpl> listStruture = new ArrayList<StructureObjectImpl>();

			// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchAdd.name()))
			{
				eciJasonStructure.setEnd2ObjectGuid(changItem);
				listStruture.add(eciJasonStructure);
				StringBuffer errorMessage = new StringBuffer();
				boolean sequenceOk = this.checkSequenceOfStructureObject(bomTemplateName, listStruture, errorMessage);
				if (!sequenceOk)
				{
					throw new ServiceRequestException("ID_APP_UECNECOSTUB_SEQUENCE_ERROR", "sequence is error!", null, errorMessage.toString());
				}
				// 判断BOM结构是否循环
				boolean isCircle = this.checkCircleOfBOM(bomTemplateName, listStruture, errorMessage);
				if (!isCircle)
				{
					throw new ServiceRequestException("ID_APP_UECNECOSTUB_CIRCLE_ERROR", "circle is error!", null, errorMessage.toString());
				}
			}
			// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchReplace.name()))
			{
				eciJasonStructure.setEnd2ObjectGuid(tarObjectGuid);
				listStruture.add(eciJasonStructure);
				StringBuffer errorMessage = new StringBuffer();
				// 判断BOM结构是否循环
				boolean isCircle = this.checkCircleOfBOM(bomTemplateName, listStruture, errorMessage);
				if (!isCircle)
				{
					throw new ServiceRequestException("ID_APP_UECNECOSTUB_CIRCLE_ERROR", "circle is error!", null, errorMessage.toString());
				}
				// O24819
				boolean duplicateEnd2 = this.isDuplicate(tarObjectGuid, eciJasonStructure, bomTemplateName);
				if (duplicateEnd2)
				{
					FoundationObject end1 = this.stubService.getBOAS().getObject(eciJasonStructure.getEnd1ObjectGuid());
					FoundationObject end2 = this.stubService.getBOAS().getObject(tarObjectGuid);
					String message = this.stubService.getMSRM().getMSRString("ID_CLIENT_EC_BATCHREPLACE_IS_RS_DUPLICATE",
							this.stubService.getUserSignature().getLanguageEnum().toString(), end2 == null ? "" : end2.getFullName(), end1 == null ? "" : end1.getFullName());
					throw new ServiceRequestException(message);
				}
			}
			FoundationObject end1Fo = this.dealWithTheECIData(ecof, eciStructure, eciJasonStructure, changItem, tarObjectGuid, changeItemAfterFORevisionMap);
			if (end1Fo != null)
			{
				end1List.add(end1Fo);
			}
		}
		
		return end1List;
	}

	/**
	 * 判断是否已存在相同子阶
	 * O24819
	 * 
	 * @return
	 */
	private boolean isDuplicate(ObjectGuid tarObjectGuid, BOMStructure end2BomStructure, String bomTemplateName)
	{
		if (end2BomStructure != null)
		{
			try
			{
				FoundationObject end1 = this.stubService.getBOAS().getObject(end2BomStructure.getEnd1ObjectGuid());
				BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateByName(end2BomStructure.getEnd1ObjectGuid(), bomTemplateName);
				if (end1 != null && bomTemplate != null && !bomTemplate.isIncorporatedMaster())
				{
					BOMView bomView = this.stubService.getBOMS().getBOMViewByEND1(end1.getObjectGuid(), bomTemplateName);
					if (bomView != null)
					{
						List<BOMStructure> end2List = this.stubService.getBOMS().listBOM(bomView.getObjectGuid(), null, null, null);
						if (!SetUtils.isNullList(end2List))
						{
							for (BOMStructure bs : end2List)
							{
								if (tarObjectGuid.getMasterGuid() != null && tarObjectGuid.getMasterGuid().equals(bs.getEnd2ObjectGuid().getMasterGuid()))
								{
									return true;
								}
							}
						}
					}
				}
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 处理ECI的数据
	 * 
	 * @param value
	 * @throws ServiceRequestException
	 */
	private FoundationObject dealWithTheECIData(FoundationObject ecof, StructureObject eciStructure, BOMStructure eciJasonStructure, ObjectGuid changItem, ObjectGuid tarObjectGuid,
			Map<String, FoundationObject> changeItemAfterFORevisionMap) throws ServiceRequestException
	{
		
		FoundationObject changeItemAfterFoRevisionF = null;

		String bomTemplateName = (String) ecof.get(UpdatedECSConstants.BOMTemplate);
		CodeItemInfo modifyTypeCode = this.stubService.getEMM().getCodeItem((String) ecof.get(UpdatedECSConstants.ModifyType));
		ObjectGuid changeItemBeforeObjectGuid = eciJasonStructure.getEnd1ObjectGuid();
		FoundationObject changeItemAfterFo = this.stubService.getBOAS().prepareRevision(changeItemBeforeObjectGuid);
		if (changeItemAfterFo == null)
		{
			return null;
		}
		// FoundationObject changeItemAfterFoRevisionF =
		// this.stubService.getUECRECPStub().createRevision(changeItemAfterFo, true);
		// I14390
		if (changeItemAfterFORevisionMap.containsKey(changeItemBeforeObjectGuid.getGuid()))
		{
			changeItemAfterFoRevisionF = changeItemAfterFORevisionMap.get(changeItemBeforeObjectGuid.getGuid());
		}
		else
		{
			changeItemAfterFoRevisionF = this.stubService.getUECRECPStub().createRevision(changeItemAfterFo, true);
			changeItemAfterFORevisionMap.put(changeItemBeforeObjectGuid.getGuid(), changeItemAfterFoRevisionF);
		}
		if (changeItemAfterFoRevisionF == null || changeItemAfterFoRevisionF.getObjectGuid() == null)
		{
			return null;
		}

		FoundationObject changItemFO = this.stubService.getBOAS().getObject(changItem);
		BOMView changeItemAfterBomview = this.stubService.getBOMS().getBOMViewByEND1(changeItemAfterFoRevisionF.getObjectGuid(), bomTemplateName);
		if (!modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchAdd.name()) && changeItemAfterBomview == null)
		{
			return changeItemAfterFoRevisionF;
		}

		if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchAdd.name()))
		{
			boolean isPrecise = false;
			if (changeItemAfterBomview == null)
			{
				BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateByName(changeItemAfterFoRevisionF.getObjectGuid(), bomTemplateName);
				if (bomTemplate == null)
				{
					return changeItemAfterFoRevisionF;
				}
				else
				{
					BomPreciseType preciseEnum = bomTemplate.getPrecise();
					if (preciseEnum.name().equals(BomPreciseType.PRECISE.name()))
					{
						isPrecise = true;
					}
					changeItemAfterBomview = this.stubService.getBOMS().createBOMViewByBOMTemplate(bomTemplate.getGuid(), changeItemAfterFoRevisionF.getObjectGuid(), isPrecise);
				}
				CodeItemInfo info = this.stubService.getEMM().getCodeItemByName("UHasBOM", "Y");
				if (info != null)
				{
					changeItemAfterFoRevisionF.put(Constants.FIELD_UHASBOM, info.getGuid());
				}
				else
				{
					changeItemAfterFoRevisionF.put(Constants.FIELD_UHASBOM, null);
				}
			}
			isPrecise = changeItemAfterBomview.isPrecise();
			this.stubService.getBOMS().linkBOM(changeItemAfterFoRevisionF.getObjectGuid(), changItemFO.getObjectGuid(), eciJasonStructure, bomTemplateName, isPrecise);
			changeItemAfterBomview = this.stubService.getBOMS().getBOMViewByEND1(changeItemAfterFoRevisionF.getObjectGuid(), bomTemplateName);
		}
		if (!changeItemAfterBomview.isCheckOut())
		{
			this.stubService.getBOMS().checkOut(changeItemAfterBomview);
		}

		if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchDel.name()) || modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchMod.name())
				|| modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchReplace.name()))
		{
			List<BOMStructure> structurelist = this.stubService.getUECSStub().listStructureObject(changeItemAfterFoRevisionF.getObjectGuid(), changItemFO.getObjectGuid(),
					bomTemplateName);
			if (SetUtils.isNullList(structurelist))
			{
				return changeItemAfterFoRevisionF;
			}
			BOMStructure oldStructure = null;
			for (BOMStructure structureObject : structurelist)
			{
				if (eciJasonStructure.getSequence().equals(structureObject.getSequence()))
				{
					oldStructure = structureObject;
				}
			}

			if (oldStructure != null)
			{
				if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchDel.name()))
				{
					((BOMSImpl) this.stubService.getBOMS()).getBomEditStub().unlinkBOM(oldStructure, false);
				}
				else if (modifyTypeCode.getName().equals(UECModifyTypeEnum.BatchMod.name()))
				{
					this.getBOMStructure(eciJasonStructure, oldStructure);
					((BOMSImpl) this.stubService.getBOMS()).getBomStub().saveBOM(oldStructure, false, false, true);
				}
				else
				{
					String typeGuid = (String) ecof.get(UpdatedECSConstants.Replacepolicy);
					if (StringUtils.isGuid(typeGuid))
					{
						CodeItemInfo typeCode = this.stubService.getEMM().getCodeItem(typeGuid);
						FoundationObject tarFoun = this.stubService.getBOAS().getObject(tarObjectGuid);
						if (tarFoun == null)
						{
							return changeItemAfterFoRevisionF;
						}
						Double oldQuantity = oldStructure.getQuantity();
						((BOMSImpl) this.stubService.getBOMS()).getBomEditStub().unlinkBOM(oldStructure, false);
						this.getBOMStructure(eciJasonStructure, oldStructure);
						oldStructure.clear(SystemObject.GUID);
						oldStructure.setRsFlag(false);
						oldStructure.setFromCAD(false);
						BOMStructure bomstr = this.stubService.getBOMS().linkBOM(changeItemAfterFoRevisionF.getObjectGuid(), tarFoun.getObjectGuid(), oldStructure, bomTemplateName,
								changeItemAfterBomview.isPrecise());

						if (UECReplacepolicyEnum.NaturalReplace.name().equals(typeCode.getName()))
						{
							// 创建取代关系(局部)
							this.createReplaceData(changeItemAfterFoRevisionF, tarFoun, changItemFO, bomTemplateName, oldQuantity, bomstr);
							if (bomstr != null && !bomstr.isRsFlag())
							{
								bomstr.setRsFlag(true);
								((BOMSImpl) this.stubService.getBOMS()).getBomStub().saveBOM(bomstr, false, true, true);
							}
						}
						else if (UECReplacepolicyEnum.MandatoryReplace.name().equals(typeCode.getName()))// I14407
						{
							if (bomstr != null)
							{
								if (bomstr.isRsFlag())
								{
									bomstr.setRsFlag(false);
								}
								((BOMSImpl) this.stubService.getBOMS()).getBomStub().saveBOM(bomstr, false, true, true);
							}
						}
					}
				}
			}
		}
		this.stubService.getBOMS().checkIn(changeItemAfterBomview);
		// 将解决对象放到结构中去
		ClassStub.decorateObjectGuid(eciStructure.getObjectGuid(), this.stubService);
		eciStructure = this.stubService.getUECSStub().getStructureObject(eciStructure.getObjectGuid().getGuid(), eciStructure.getObjectGuid().getClassName());
		this.stubService.getUECSStub().getKeyFromObjectGuid(changeItemAfterFoRevisionF.getObjectGuid(), UpdatedECSConstants.ECTargetItem, eciStructure);
		((BOASImpl) this.stubService.getBOAS()).getStructureStub().saveStructure(eciStructure, false);
		this.stubService.getUECSStub().link(ecof.getObjectGuid(), changeItemAfterFoRevisionF.getObjectGuid(), null, null, UpdatedECSConstants.ECO_CHANGEITEMAFTER$);

		changeItemAfterFoRevisionF.setECFlag(ecof.getObjectGuid());
		this.stubService.getUECRECPStub().saveOnly(changeItemAfterFoRevisionF);
		List<BOMView> listBOMView = this.stubService.getBOMS().listBOMView(changeItemAfterFoRevisionF.getObjectGuid());
		if (!SetUtils.isNullList(listBOMView))
		{
			for (BOMView bomview : listBOMView)
			{
				bomview.setECFlag(ecof.getObjectGuid());
				this.stubService.getUECRECPStub().saveOnly(bomview);
			}
		}
		
		return changeItemAfterFoRevisionF;
	}

	/**
	 * @param revisionF
	 * @param end2Foun
	 * @param oldQuantity
	 * @param oldStructure
	 * @throws ServiceRequestException
	 */
	private void createReplaceData(FoundationObject revisionF, FoundationObject end2Foun, FoundationObject tarFoun, String bomTemplateName, Double oldQuantity,
			BOMStructure bomStructure) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateByName(revisionF.getObjectGuid(), bomTemplateName);
		if (bomTemplate == null)
		{
			return;
		}
		FoundationObject replaceData = this.stubService.getBRM().newFoundationObject(ReplaceRangeEnum.PART, ReplaceTypeEnum.QUDAI);

		String sequence = this.getReplaceSequence(revisionF, end2Foun, bomTemplateName);
		replaceData.put(ReplaceSubstituteConstants.RSNumber, sequence);
		replaceData.put(ReplaceSubstituteConstants.EffectiveDate.toUpperCase(), new Date());

		replaceData.put(ReplaceSubstituteConstants.BOMViewDisplay.toUpperCase(), bomTemplate.getName());

		replaceData.put(ReplaceSubstituteConstants.MasterItem.toUpperCase(), revisionF.getObjectGuid());
		replaceData.put(ReplaceSubstituteConstants.ComponentItem.toUpperCase(), end2Foun.getObjectGuid());
		replaceData.put(ReplaceSubstituteConstants.BOMKey.toUpperCase(), bomStructure.getBOMKey());
		replaceData.put(ReplaceSubstituteConstants.RSItem.toUpperCase(), tarFoun.getObjectGuid());
		replaceData.put(ReplaceSubstituteConstants.ComponentUsage, oldQuantity);

		this.stubService.getBRM().createReplaceData(replaceData);
	}

	/**
	 * @param revisionF
	 * @param end2Foun
	 * @param id
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getReplaceSequence(FoundationObject revisionF, FoundationObject end2Foun, String bomTemplateName) throws ServiceRequestException
	{
		List<FoundationObject> listReplaceData = this.stubService.getBRM().listReplaceDataByRang(revisionF.getObjectGuid(), end2Foun.getObjectGuid(), ReplaceRangeEnum.PART, null,
				bomTemplateName, null, true);
		if (!SetUtils.isNullList(listReplaceData))
		{
			int i = 0;
			for (FoundationObject fo : listReplaceData)
			{
				String number = (String) fo.get(ReplaceSubstituteConstants.RSNumber);
				if (!StringUtils.isNullString(number))
				{
					int temp = Integer.parseInt(number);
					if (i < temp)
					{
						i = temp;
					}
				}
			}
			return Integer.toString(i + 1);
		}
		return "1";
	}

	/**
	 * @param structure
	 *            --页面的Structure
	 * @param oldStructure
	 *            --数据库中的Structure
	 * @throws ServiceRequestException
	 */
	private void getBOMStructure(BOMStructure structure, BOMStructure oldStructure) throws ServiceRequestException
	{
		List<UIField> listUIfield = this.stubService.getEMM().listListUIField(oldStructure.getObjectGuid().getClassName());
		if (structure.getUOM() != null)
		{
			oldStructure.setUOM(structure.getUOM());
		}
		if (structure.get("QUANTITY") != null)
		{
			oldStructure.setQuantity(structure.getQuantity());
		}
		if (!SetUtils.isNullList(listUIfield))
		{
			for (UIField field : listUIfield)
			{
				if (structure.get(field.getName()) != null && !field.getName().equalsIgnoreCase(SystemObject.GUID))
				{
					oldStructure.put(field.getName(), structure.get(field.getName()));
				}
			}
		}
	}

	/**
	 * 
	 * @param objectValue
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return
	 */

	private String getJsonString(DynaObject mapValue, String key1, String key2, String key3)
	{
		String value = "";
		if (!StringUtils.isNullString((String) mapValue.get(key1)))
		{
			value = (String) mapValue.get(key1);
		}
		if (!StringUtils.isNullString((String) mapValue.get(key2)))
		{
			value = value + (String) mapValue.get(key2);
		}
		if (!StringUtils.isNullString((String) mapValue.get(key3)))
		{
			value = value + (String) mapValue.get(key3);
		}
		return value;
	}

	/**
	 * 
	 * 普通ECO启动
	 * 
	 * @param listECOObjectGuid
	 * @throws ServiceRequestException
	 */
	protected void startGeneralECO(FoundationObject ECOFoundation) throws ServiceRequestException
	{
		if (ECOFoundation.get(UpdatedECSConstants.ChangeItem) != null)
		{
			ObjectGuid changeItem = this.getObjectGuidByKey(ECOFoundation, UpdatedECSConstants.ChangeItem);
			if (changeItem != null && StringUtils.isGuid(changeItem.getGuid()))
			{
				FoundationObject f = this.stubService.getBOAS().prepareRevision(changeItem);
				if (f != null)
				{
					FoundationObject revisionF = this.stubService.getUECRECPStub().createRevision(f, true);
					revisionF.setECFlag(ECOFoundation.getObjectGuid());
					this.stubService.getUECRECPStub().saveOnly(revisionF);
					List<BOMView> listBOMView = this.stubService.getBOMS().listBOMView(revisionF.getObjectGuid());
					if (!SetUtils.isNullList(listBOMView))
					{
						for (BOMView bomview : listBOMView)
						{
							bomview.setECFlag(ECOFoundation.getObjectGuid());
							this.stubService.getUECRECPStub().saveOnly(bomview);
						}
					}

					if (revisionF != null && revisionF.getObjectGuid() != null)
					{
						this.stubService.getUECSStub().getKeyFromObjectGuid(revisionF.getObjectGuid(), UpdatedECSConstants.SolveItem, ECOFoundation);
						this.stubService.getUECRECPStub().saveNotCheckout(ECOFoundation);
						// 创建eco与解决对象之间的关系
						this.stubService.getUECSStub().link(ECOFoundation.getObjectGuid(), revisionF.getObjectGuid(), null, null, UpdatedECSConstants.ECO_CHANGEITEMAFTER$);
					}
				}
			}
		}
	}

	/**
	 * @param ecof
	 * @param changeitem
	 * @return
	 */
	protected ObjectGuid getObjectGuidByKey(DynaObject ecof, String fieldKey)
	{
		ObjectGuid objectGuid = new ObjectGuid();
		if (ecof.get(fieldKey) instanceof ObjectGuid)
		{
			if (null != ecof.get(fieldKey))
			{
				objectGuid.setGuid(((ObjectGuid) ecof.get(fieldKey)).getGuid());
				objectGuid.setMasterGuid(((ObjectGuid) ecof.get(fieldKey)).getMasterGuid());
				objectGuid.setClassName(((ObjectGuid) ecof.get(fieldKey)).getClassName());
				objectGuid.setClassGuid(((ObjectGuid) ecof.get(fieldKey)).getClassGuid());
			}
		}
		else
		{
			if (ecof.get(fieldKey) != null)
			{
				objectGuid.setGuid((String) ecof.get(fieldKey));
			}
			if (ecof.get(fieldKey + UpdatedECSConstants.MASTER) != null)
			{
				objectGuid.setMasterGuid(((String) ecof.get(fieldKey + UpdatedECSConstants.MASTER)));
			}
			if (ecof.get(fieldKey + UpdatedECSConstants.CLASSNAME) != null)
			{
				objectGuid.setClassName((String) ecof.get(fieldKey + UpdatedECSConstants.CLASSNAME));
			}
			if (ecof.get(fieldKey + UpdatedECSConstants.ClassGuid) != null)
			{
				objectGuid.setClassGuid((String) ecof.get(fieldKey + UpdatedECSConstants.ClassGuid));
			}
		}
		return objectGuid;
	}

	/**
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isUsedByOtherNormalECO(ObjectGuid objectGuid) throws ServiceRequestException
	{
		FoundationObject fou = this.stubService.getBOAS().getObject(objectGuid);
		if (fou != null && fou.getECFlag() != null)
		{
			try
			{
				FoundationObject ecFou = this.stubService.getBOAS().getObject(fou.getECFlag());
				if (ecFou != null)
				{
					return true;
				}
			}
			catch (ServiceRequestException e)
			{
				if (DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT.getMsrId().equals(e.getMsrId()))
				{
					return false;
				}
				else
				{
					throw e;
				}
			}
		}
		return false;
	}

	/**
	 * @param ecpfoundation
	 * @param ecrObject
	 * @param parentECP
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject saveECO(FoundationObject ecofoundation, ObjectGuid ecnObject, ObjectGuid parentECO, String proGuid) throws ServiceRequestException
	{
		if (ecofoundation == null)
		{
			return null;
		}
		FoundationObject resultEcoFoundation = null;
		ecofoundation.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
		ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecofoundation.getObjectGuid().getClassGuid());

		// ECO与变更对象之间的关联
		ObjectGuid changeItemObject = this.stubService.getUECNECOStub().getObjectGuidByKey(ecofoundation, UpdatedECSConstants.ChangeItem);
		FoundationObject changeItemFo = null;

		ObjectGuid solveItemObject = this.stubService.getUECNECOStub().getObjectGuidByKey(ecofoundation, UpdatedECSConstants.SolveItem);
		if (solveItemObject == null || StringUtils.isGuid(solveItemObject.getGuid()) == false)
		{
			if (changeItemObject != null && StringUtils.isGuid(changeItemObject.getGuid()))
			{
				changeItemFo = this.stubService.getBOAS().getObject(changeItemObject);
				String laterst = (String) changeItemFo.get("LATESTREVISION$");
				// bug 修改成最新发布版
				if (StringUtils.isNullString(laterst) || !laterst.contains("mr"))
				{
					throw new ServiceRequestException("ID_APP_UECNECOSTUB_CHANGEITEM_NOTRELEASED", "changeIt not released", null, changeItemFo.getFullName());
				}
				if (ecoClassInfo.hasInterface(ModelInterfaceEnum.INormalForEc))
				{
					this.checkChangeItemByEnd1(changeItemObject, ecofoundation, UpdatedECSConstants.ECO_CHANGEITEM$);
				}
			}
		}

		// ECO与取代对象之间的关联
		ObjectGuid targetItemObject = this.stubService.getUECNECOStub().getObjectGuidByKey(ecofoundation, UpdatedECSConstants.TargetItem);

		if (targetItemObject != null && StringUtils.isGuid(targetItemObject.getGuid()))
		{
			// targetItem必须是发布状态
			FoundationObject targetItemFo = this.stubService.getBOAS().getObject(targetItemObject);
			String laterst = (String) targetItemFo.get("LATESTREVISION$");
			if (StringUtils.isNullString(laterst) || !laterst.contains("r"))
			{
				throw new ServiceRequestException("ID_APP_UECNECOSTUB_CHANGEITEM_NOTRELEASED", "targetItem not released", null, targetItemFo.getFullName());
			}
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			FoundationObject ecnfoundation = null;
			String performer = null;
			if (ecnObject != null)
			{
				ecnfoundation = this.stubService.getBOAS().getObject(ecnObject);
				performer = ecnfoundation.getOwnerUserGuid();
			}
			else
			{
				performer = this.stubService.getUserSignature().getUserGuid();
			}
			if (StringUtils.isNullString((String) ecofoundation.get(UpdatedECSConstants.Performer)))
			{
				ecofoundation.put(UpdatedECSConstants.Performer, performer);
			}
			// 已经存在--更新
			if (!StringUtils.isNullString(ecofoundation.getGuid()))
			{
				resultEcoFoundation = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(ecofoundation, false, false, false, null, true, false, true);
				// 判断原来的ECO下变更对象是否一样
				this.updateChangeItem(changeItemObject, resultEcoFoundation, UpdatedECSConstants.ECO_CHANGEITEM$, proGuid);
			}
			// 不存在--新建
			else
			{
				if (ecnObject != null && ecnfoundation != null)
				{
					this.stubService.getUECSStub().getKeyFromObjectGuid(ecnfoundation.getObjectGuid(), "ECN", ecofoundation);
				}
				resultEcoFoundation = this.stubService.getUECSStub().createObject(ecofoundation);
				if (ecoClassInfo.hasInterface(ModelInterfaceEnum.IIndependenceForEc))
				{
					resultEcoFoundation = this.stubService.getBOAS().checkOut(resultEcoFoundation);
				}

				// ///////////////////////////////////////////////////////////////////////////
				// with ecn, without ecn
				if (ecnObject != null && ecnfoundation != null)
				{
					LifecyclePhaseInfo ecnLifecyclePhase = this.stubService.getEMM().getLifecyclePhaseInfo(ecnfoundation.getLifecyclePhaseGuid());
					// 如果ecn的生命周期为Created或者Reviewing阶段,就置eco的生命周期为Created阶段,将生命周期置为执行阶段
					if (ecnLifecyclePhase.getName().equals(ECNLifecyclePhaseEnum.Performing.name()))
					{
						LifecyclePhaseInfo ecoCreateLPInfo = this.stubService.getEMM().getLifecyclePhaseInfo(ecoClassInfo.getLifecycleName(), ECOLifecyclePhaseEnum.Created.name());
						LifecyclePhaseInfo ecoPerformLPInfo = this.stubService.getEMM().getLifecyclePhaseInfo(ecoClassInfo.getLifecycleName(),
								ECOLifecyclePhaseEnum.Performing.name());
						resultEcoFoundation = ((BOASImpl) this.stubService.getBOAS()).getFUpdaterStub().updateLifeCyclePhase(resultEcoFoundation.getObjectGuid(), new Date(),
								ecoCreateLPInfo, ecoPerformLPInfo, false);
					}
					this.stubService.getUECSStub().link(ecnObject, resultEcoFoundation.getObjectGuid(), null, proGuid, UpdatedECSConstants.ECN_ECO$);
				}
				if (changeItemFo != null)
				{
					this.stubService.getUECSStub().link(resultEcoFoundation.getObjectGuid(), changeItemFo.getObjectGuid(), null, proGuid, UpdatedECSConstants.ECO_CHANGEITEM$);
				}
			}

			// 假如是普通ECO，则需要锁定新增的changeItem
			if (ecoClassInfo.hasInterface(ModelInterfaceEnum.IECOM) && changeItemFo != null)
			{
				changeItemFo.setECFlag(resultEcoFoundation.getObjectGuid());
				if (changeItemFo.isChanged())
				{
					this.stubService.getUECRECPStub().saveOnly(changeItemFo);
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
		return resultEcoFoundation;
	}

	/**
	 * @param changeItemObject
	 * @param ecofoundation
	 * @param ecoChangeitem$
	 * @throws ServiceRequestException
	 */
	private void checkChangeItemByEnd1(ObjectGuid changeItemObject, FoundationObject ecofoundation, String tempalteName) throws ServiceRequestException
	{
		List<StructureObject> structureList = this.stubService.getUECSStub().listStructureObjects(ecofoundation.getObjectGuid(), tempalteName);
		if (!SetUtils.isNullList(structureList) && structureList.size() > 1)
		{
			throw new ServiceRequestException("ID_UECNECOSTUB_CHANGEITEM_NOT_EXIST", "changeItem is not exist or exist two changeItem", null, ecofoundation.getFullName());
		}
		if (!SetUtils.isNullList(structureList) && structureList.get(0).getEnd2ObjectGuid().getGuid().equals(changeItemObject.getGuid()))
		{
			return;
		}
		boolean isused = this.isUsedByOtherNormalECO(changeItemObject);
		if (isused)
		{
			FoundationObject fou = this.stubService.getBOAS().getObject(changeItemObject);
			ecofoundation = this.stubService.getBOAS().getObject(ecofoundation.getObjectGuid());
			throw new ServiceRequestException("ID_APP_UECNECOSTUB_CHANGEITEM_USED", "the changeItem is used", null, fou.getFullName());
		}
	}

	/**
	 * @param changeItemObject
	 * @param fObject
	 * @param ecoChangeitem$
	 * @throws ServiceRequestException
	 */
	protected void updateChangeItem(ObjectGuid changeItemObject, FoundationObject fObject, String tempalteName, String proGuid) throws ServiceRequestException
	{
		List<StructureObject> structureList = this.stubService.getUECSStub().listStructureObjects(fObject.getObjectGuid(), tempalteName);
		if (!SetUtils.isNullList(structureList) && structureList.size() > 1)
		{
			throw new ServiceRequestException("ID_UECNECOSTUB_CHANGEITEM_NOT_EXIST", "changeItem is exist two changeItem", null, fObject.getFullName());
		}
		if (!SetUtils.isNullList(structureList) && !structureList.get(0).getEnd2ObjectGuid().getGuid().equals(changeItemObject.getGuid()))
		{
			// 将原来的实例解锁--只有eco对应的changeItem需要
			String classguid = fObject.getObjectGuid().getClassGuid();
			ClassInfo classinfo = null;
			if (StringUtils.isGuid(classguid))
			{
				classinfo = this.stubService.getEMM().getClassByGuid(classguid);
			}
			if (classinfo != null && classinfo.hasInterface(ModelInterfaceEnum.IECOM))
			{
				StructureObject structure = structureList.get(0);
				if (structure.getEnd2ObjectGuid() != null && StringUtils.isGuid(structure.getObjectGuid().getGuid()))
				{
					FoundationObject end2Fou = this.stubService.getBOAS().getObject(structure.getEnd2ObjectGuid());
					if (end2Fou != null && end2Fou.getECFlag() != null && StringUtils.isGuid(end2Fou.getECFlag().getGuid()))
					{
						end2Fou.setECFlag(null);
						this.stubService.getUECRECPStub().saveOnly(end2Fou);
					}
				}
			}
			// 删除原来的end2,重新将新的end2挂上
			((BOASImpl) this.stubService.getBOAS()).getRelationUnlinkStub().unlink(structureList.get(0), false);
			FoundationObject changeItemFo = this.stubService.getBOAS().getObject(changeItemObject);
			if (changeItemFo != null)
			{
				this.stubService.getUECSStub().link(fObject.getObjectGuid(), changeItemFo.getObjectGuid(), null, proGuid, tempalteName);
			}

		}

	}

	/**
	 * 执行阶段取消ECN或者ECO 权限:ECN和ECO的owner可以取消 以及ECN的owner可以取消ECO
	 * 
	 * @param ecofoundation
	 * @param ecnObject
	 * @param parentECO
	 * @throws ServiceRequestException
	 */
	protected void cancelECN_ECO(ObjectGuid cancelValue) throws ServiceRequestException
	{
		if (cancelValue == null)
		{
			return;
		}

		FoundationObject fo = this.stubService.getBOAS().getObject(cancelValue);
		if (fo == null)
		{
			throw new ServiceRequestException("ID_APP_CANNT_FIND_INSTANCE", "not found instance: ", null);
		}
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
		if (classInfo == null)
		{
			return;
		}
		if (classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN))
		{
			List<FoundationObject> listECO = this.stubService.getECOByECNAll(fo.getObjectGuid());
			if (!SetUtils.isNullList(listECO))
			{
				for (FoundationObject ecoFoundation : listECO)
				{
					this.onlyCancelCEO(ecoFoundation);

				}
			}
			// 撤销流程
			this.stubService.getUECRECPStub().cancelWorkFlow(fo);
			fo = this.stubService.getBOAS().getObject(fo.getObjectGuid());
			ClassInfo ecnClassInfo = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
			String lifeCyclePhaseOriginal = fo.getLifecyclePhaseGuid();
			String lifeCyclePhaseDest = this.stubService.getEMM().getLifecyclePhaseInfo(ecnClassInfo.getLifecycleName(), ECNLifecyclePhaseEnum.Canceled.name()).getGuid();
			fo = this.stubService.getUECSStub().updateLifeCyclePhase(fo.getObjectGuid(), lifeCyclePhaseOriginal, lifeCyclePhaseDest);

			// 更改状态
			if (!fo.getStatus().equals(SystemStatusEnum.OBSOLETE))
			{
				InstanceService ds = DataServer.getInstanceService();
				String sessionId = this.stubService.getSignature().getCredential();
				ds.changeStatus(fo.getStatus(), SystemStatusEnum.OBSOLETE, fo.getObjectGuid(), false, sessionId);
			}

		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IECOM))
		{
			FoundationObject ecoFoundation = fo;
			this.onlyCancelCEO(ecoFoundation);
		}

	}

	/**
	 * 仅仅只取消ECo
	 * 
	 * @param ecoFoundation
	 * @throws ServiceRequestException
	 */
	private void onlyCancelCEO(FoundationObject ecoFoundation) throws ServiceRequestException
	{
		try
		{
			String sessionId = this.stubService.getSignature().getCredential();
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 撤销流程
			this.stubService.getUECRECPStub().cancelWorkFlow(ecoFoundation);
			ecoFoundation = this.stubService.getBOAS().getObject(ecoFoundation.getObjectGuid());
			ClassInfo ecoClassInfo = this.stubService.getEMM().getClassByGuid(ecoFoundation.getObjectGuid().getClassGuid());
			String lifeCyclePhaseOriginal = ecoFoundation.getLifecyclePhaseGuid();
			String lifeCyclePhaseDest = this.stubService.getEMM().getLifecyclePhaseInfo(ecoClassInfo.getLifecycleName(), ECOLifecyclePhaseEnum.Canceled.name()).getGuid();
			ecoFoundation = this.stubService.getUECSStub().updateLifeCyclePhase(ecoFoundation.getObjectGuid(), lifeCyclePhaseOriginal, lifeCyclePhaseDest);
			// 解锁--通过ECO
			this.unlockByEco(ecoFoundation);
			// 备份
			this.stubService.getUECIStub().deleteSolveObjectAndBackup(ecoFoundation);

			// 更改状态
			if (!ecoFoundation.getStatus().equals(SystemStatusEnum.OBSOLETE))
			{
				InstanceService ds = DataServer.getInstanceService();
				ds.changeStatus(ecoFoundation.getStatus(), SystemStatusEnum.OBSOLETE, ecoFoundation.getObjectGuid(), false, sessionId);
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

	/**
	 * 通过ECO进行解锁
	 * 
	 * @param objectGuid
	 *            eco的objectGuid
	 * @throws ServiceRequestException
	 */
	protected void unlockByEco(FoundationObject ecoObject) throws ServiceRequestException
	{
		ECService ds = DataServer.getECService();
		List<String> classNameList = new ArrayList<String>();
		List<ClassInfo> classInfoList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IChangeItem);
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!SetUtils.isNullList(classInfoList))
			{
				for (ClassInfo classInfo : classInfoList)
				{
					if (classInfo.isCreateTable() == false)
					{
						processCreateTableClass(classInfo, classNameList);
					}
					else
					{
						classNameList.add(classInfo.getName());
					}

				}
				ds.unlockByECO(ecoObject.getObjectGuid(), classNameList);
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

	private void processCreateTableClass(ClassInfo rclassInfo, List<String> classNameList) throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = this.stubService.getEMM().listSubClass(rclassInfo.getName(), rclassInfo.getGuid(), false, null);
		if (classInfoList != null)
		{
			for (ClassInfo classInfo : classInfoList)
			{
				if (classInfo.isCreateTable() == false)
				{
					processCreateTableClass(classInfo, classNameList);
				}
				else
				{
					classNameList.add(classInfo.getName());
				}

			}

		}
	}

	/**
	 * 删除ECN之前的脚本
	 * 删除ecn之前,需要删除eco以及eco下的ecContent/eci/changeItem/changeItemBefore
	 * /changeItemAfter/ecr
	 * 
	 * @param foundationObject
	 * @throws ServiceRequestException
	 */
	public void deleteECN(FoundationObject foundationObject) throws ServiceRequestException
	{
		if (foundationObject != null && foundationObject.getObjectGuid() != null)
		{
			try
			{
//				DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
				// ECN_ECR$
				ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(foundationObject.getObjectGuid(), UpdatedECSConstants.ECN_ECR$);
				if (viewObject != null && viewObject.getObjectGuid() != null)
				{
					((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject, false, false);
				}
				// 取得ECN_ECO$
				ViewObject viewObject1 = this.stubService.getBOAS().getRelationByEND1(foundationObject.getObjectGuid(), UpdatedECSConstants.ECN_ECO$);
				if (viewObject1 != null && viewObject1.getObjectGuid() != null)
				{
					List<FoundationObject> listECO = this.stubService.getUECSStub().listFoundationObjectOfRelation(viewObject1.getObjectGuid(), null);
					if (!SetUtils.isNullList(listECO))
					{
						for (FoundationObject ecoFo : listECO)
						{
							// ECO_ECOCONTENT$
							ViewObject viewObject2 = this.stubService.getBOAS().getRelationByEND1(ecoFo.getObjectGuid(), UpdatedECSConstants.ECO_ECOCONTENT$);
							if (viewObject2 != null && viewObject2.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject2, true, false);
							}

							// ECO_CHANGEITEMBEFORE$
							ViewObject viewObject4 = this.stubService.getBOAS().getRelationByEND1(ecoFo.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEMBEFORE$);
							if (viewObject4 != null && viewObject4.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject4, false, false);
							}

							// ECO_CHANGEITEM$
							ViewObject viewObject6 = this.stubService.getBOAS().getRelationByEND1(ecoFo.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEM$);
							if (viewObject6 != null && viewObject6.getObjectGuid() != null)
							{
								((BOASImpl) this.stubService.getBOAS()).getRelationStub().deleteRelation(viewObject6, false, false);
							}

							List<ObjectGuid> listEcpOREcoObjectGuid = new ArrayList<ObjectGuid>();
							listEcpOREcoObjectGuid.add(ecoFo.getObjectGuid());
							// 删除eco
							this.stubService.getUECRECPStub().deleteECPorECO(listEcpOREcoObjectGuid, false, ModelInterfaceEnum.IECOM);
						}
					}
				}
//				DataServer.getTransactionManager().commitTransaction();
			}
			catch (DynaDataException e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
			catch (Exception e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
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

class UpdateScheduledTask extends AbstractScheduledTask
{
	private ServiceContext			serviceContext	= null;
	private List<FoundationObject>	end1List		= null;
	private String					bmGuid			= null;
	private BOMS					boms			= null;

	public UpdateScheduledTask(ServiceContext serviceContext, List<FoundationObject> end1List, String bmGuid)
	{
		this.serviceContext = serviceContext;
		this.end1List = end1List;
		this.bmGuid = bmGuid;
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]UpdateScheduledTask , Scheduled Task Start...");
		try
		{
			this.boms = this.serviceContext.allocatService(BOMS.class);
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