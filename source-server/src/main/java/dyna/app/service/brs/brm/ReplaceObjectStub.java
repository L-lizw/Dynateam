/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceObject
 * wangweixia 2012-7-17
 */
package dyna.app.service.brs.brm;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.boas.RelationStub;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.ReplaceConfig;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author wangweixia
 *         取替代关系新建、删除、修改
 */
public class ReplaceObjectStub extends AbstractServiceStub<BRMImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected ReplaceObjectStub(ServiceContext context, BRMImpl service)
	{
		super(context, service);

	}

	/**
	 * 往foundationObject里面放置classguid和MasterGuid字段
	 * 
	 * @param objectGuid
	 *            从里面取得classguid和MasterGuid字段
	 * @param sid
	 *            foundationObject的字段名
	 * @param foundationObject
	 *            需要放置的Object
	 * @throws ServiceRequestException
	 */
	private void getKeyFromObjectGuid(ObjectGuid objectGuid, String sid, FoundationObject foundationObject) throws ServiceRequestException
	{
		if (objectGuid != null)
		{
			// 去除原先存储的字段值
			foundationObject.clear(sid.toUpperCase());
			// 放置ClassGuid，MasterGuid
			foundationObject.put(sid, objectGuid.getGuid());
			foundationObject.put((sid + ReplaceSubstituteConstants.ClassGuid).toUpperCase(), objectGuid.getClassGuid());
			if (StringUtils.isNullString(objectGuid.getMasterGuid()))
			{
				FoundationObject fMasterItem = this.stubService.getBOAS().getObjectByGuid(objectGuid);
				String masterGuid = fMasterItem.getObjectGuid().getMasterGuid();
				foundationObject.put((sid + ReplaceSubstituteConstants.MASTER).toUpperCase(), masterGuid);
			}
			else
			{
				foundationObject.put((sid + ReplaceSubstituteConstants.MASTER).toUpperCase(), objectGuid.getMasterGuid());
			}
		}
	}

	/**
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject newFoundationObject(ReplaceRangeEnum range, ReplaceTypeEnum type) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IReplaceSubstitute, null);
		FoundationObject fo = this.stubService.getBOAS().newFoundationObject(classInfo.getGuid(), classInfo.getName());

		setRsTypeAndRange(fo, range, type);

		return fo;
	}

	private void setRsTypeAndRange(FoundationObject fo, ReplaceRangeEnum range, ReplaceTypeEnum type) throws ServiceRequestException
	{
		if (range != null && !StringUtils.isNullString(range.getValue()))
		{
			CodeItemInfo scope = this.stubService.getEMM().getCodeItemByName(ReplaceSubstituteConstants.Scope, range.getValue());
			if (scope != null)
			{
				fo.put(ReplaceSubstituteConstants.Scope.toUpperCase(), scope.getGuid());
			}
		}
		if (type != null && !StringUtils.isNullString(type.getValue()))
		{
			CodeItemInfo rSType = this.stubService.getEMM().getCodeItemByName(ReplaceSubstituteConstants.RSType, type.getValue());
			if (rSType != null)
			{
				fo.put(ReplaceSubstituteConstants.RSType.toUpperCase(), rSType.getGuid());
			}
		}
	}

	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		return boas.getFSaverStub().saveObject(foundationObject, true, false, false, null, true, false, false);
	}

	/**
	 * 创建取替代数据
	 * 
	 * @param foundationObject
	 * @return
	 */
	public FoundationObject createReplaceData(FoundationObject foundationObject) throws ServiceRequestException
	{
		FoundationObject saveObject = null;
		ObjectGuid replaceObjectGuid = null;
		ObjectGuid componentObjectGuid = null;
		ObjectGuid masterObjectGuid = null;
		Object componentObject = foundationObject.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase());
		if (componentObject instanceof ObjectGuid)
		{
			componentObjectGuid = (ObjectGuid) componentObject;
		}
		if (componentObjectGuid == null && componentObject instanceof String && !StringUtils.isNullString((String) componentObject))
		{
			componentObjectGuid = new ObjectGuid((String) foundationObject.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.ClassGuid), null,
					(String) componentObject, (String) foundationObject.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER), null);
		}

		Object replaceObject = foundationObject.get(ReplaceSubstituteConstants.RSItem.toUpperCase());
		if (replaceObject instanceof ObjectGuid)
		{
			replaceObjectGuid = (ObjectGuid) replaceObject;
		}
		if (replaceObjectGuid == null && replaceObject instanceof String && !StringUtils.isNullString((String) replaceObject))
		{
			replaceObjectGuid = new ObjectGuid((String) foundationObject.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.ClassGuid), null,
					(String) replaceObject, (String) foundationObject.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER), null);
		}

		Object masterObject = foundationObject.get(ReplaceSubstituteConstants.MasterItem);
		if (masterObject instanceof ObjectGuid)
		{
			masterObjectGuid = (ObjectGuid) masterObject;
		}
		if (masterObjectGuid == null && masterObject instanceof String && !StringUtils.isNullString((String) masterObject))
		{
			masterObjectGuid = new ObjectGuid((String) foundationObject.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.ClassGuid), null,
					(String) masterObject, (String) foundationObject.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER), null);
		}

		String bomViewName = (String) foundationObject.get(ReplaceSubstituteConstants.BOMViewDisplay);
		String rsNumber = (String) foundationObject.get(ReplaceSubstituteConstants.RSNumber);
		String bomKey = (String) foundationObject.get(ReplaceSubstituteConstants.BOMKey);
		Date effectiveDate = (Date) foundationObject.get(ReplaceSubstituteConstants.EffectiveDate);
		Date expireDate = (Date) foundationObject.get(ReplaceSubstituteConstants.InvalidDate);
		if (this.checkCreateReplaceData(masterObjectGuid, componentObjectGuid, replaceObjectGuid, bomViewName, bomKey))
		{
			if (!this.checkDateForCreate(effectiveDate, expireDate, (Date) foundationObject.get(ReplaceSubstituteConstants.COMPARE_DATE)))
			{
				throw new ServiceRequestException("ID_APP_BRM_REPLACE_APPLY_DATE_ILLEGAL", "The date of batch-apply is illegal");
			}
			CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem((String) foundationObject.get(ReplaceSubstituteConstants.Scope.toUpperCase()));
			if (codeItemInfo != null)
			{
				String scopeCode = codeItemInfo.getCode();
				boolean isRepeat = false;
				if (scopeCode.equals(ReplaceRangeEnum.PART.getValue()))
				{
					isRepeat = this.checkSequence(masterObjectGuid, componentObjectGuid, bomViewName, bomKey, ReplaceRangeEnum.PART, rsNumber);
					// 放置Master相关字段
					this.getKeyFromObjectGuid(masterObjectGuid, ReplaceSubstituteConstants.MasterItem, foundationObject);
				}
				else if (scopeCode.equals(ReplaceRangeEnum.GLOBAL.getValue()))
				{
					isRepeat = this.checkSequence(null, componentObjectGuid, null, null, ReplaceRangeEnum.GLOBAL, rsNumber);
					foundationObject.clear(ReplaceSubstituteConstants.MasterItem.toUpperCase());
				}
				if (isRepeat)
				{
					throw new ServiceRequestException("ID_APP_BRM_SEQUENCE_REPEAT", "Duplicated sequence");
				}
				// 放置字段
				String userGuid = this.stubService.getUserSignature().getUserGuid();
				foundationObject.setCreateUserGuid(userGuid);
				foundationObject.setUpdateUserGuid(userGuid);
				this.getKeyFromObjectGuid(componentObjectGuid, ReplaceSubstituteConstants.ComponentItem, foundationObject);
				this.getKeyFromObjectGuid(replaceObjectGuid, ReplaceSubstituteConstants.RSItem, foundationObject);
				saveObject = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(foundationObject, false);
			}
		}
		return saveObject;
	}

	/**
	 * 客户端创建取替代数据
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject createReplace(FoundationObject foundationObject) throws ServiceRequestException
	{
		FoundationObject saveObject = null;
		ObjectGuid componentObjectGuid = null;
		ObjectGuid masterObjectGuid = null;
		Object componentObject = foundationObject.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase());
		if (componentObject instanceof ObjectGuid)
		{
			componentObjectGuid = (ObjectGuid) componentObject;
		}
		Object masterObject = foundationObject.get(ReplaceSubstituteConstants.MasterItem.toUpperCase());
		if (masterObject instanceof ObjectGuid)
		{
			masterObjectGuid = (ObjectGuid) masterObject;
		}
		String bomViewName = (String) foundationObject.get(ReplaceSubstituteConstants.BOMViewDisplay.toUpperCase());
		String bomKey = (String) foundationObject.get(ReplaceSubstituteConstants.BOMKey.toUpperCase());
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			saveObject = this.createReplaceData(foundationObject);
			this.updateBOMStructure(masterObjectGuid, componentObjectGuid, bomViewName, bomKey);
			saveObject.put("ComponentUsage", 1);
			saveObject = this.saveReplaceData(saveObject);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		return saveObject;
	}

	/**
	 * 判断序号是否重复
	 * 
	 * @param masterObjectGuid
	 * @param componentObjectGuid
	 * @param bomViewName
	 * @param range
	 * @param sequence
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean checkSequence(ObjectGuid masterObjectGuid, ObjectGuid componentObjectGuid, String bomViewName, String bomKey, ReplaceRangeEnum range, String sequence)
			throws ServiceRequestException
	{
		List<FoundationObject> listFoundationObject = null;
		if (range.equals(ReplaceRangeEnum.PART))
		{
			if (masterObjectGuid != null && componentObjectGuid != null && bomViewName != null)
			{
				listFoundationObject = this.stubService.listReplaceDataByRang(masterObjectGuid, componentObjectGuid, ReplaceRangeEnum.PART, null, bomViewName, bomKey, true);
			}
		}
		else if (range.equals(ReplaceRangeEnum.GLOBAL))
		{
			if (componentObjectGuid != null)
			{
				listFoundationObject = this.stubService.listReplaceDataByRang(null, componentObjectGuid, ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI, null, null, true);
			}
		}
		if (!SetUtils.isNullList(listFoundationObject))
		{
			for (FoundationObject fo : listFoundationObject)
			{
				String seq = fo.get(ReplaceSubstituteConstants.RSNumber.toUpperCase()) + "";
				if (!StringUtils.isNullString(StringUtils.convertNULL(sequence)))
				{
					if (seq.equals(sequence))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param objectGuid
	 */
	public void deleteReplaceData(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.stubService.getBOAS().deleteFoundationObject(objectGuid.getGuid(), objectGuid.getClassGuid());
	}

	/**
	 * 
	 * @param objectGuid
	 */
	public void deleteReplaceData(ObjectGuid objectGuid, ObjectGuid StructureObjGuid, String bomViewName) throws ServiceRequestException
	{
		if (StructureObjGuid != null && !StringUtils.isNullString(bomViewName))
		{
			SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(StructureObjGuid.getClassName());
			BOMStructure bomStructure = this.stubService.getBOMS().getBOM(StructureObjGuid, searchCondition, null);
			if (bomStructure != null)
			{
				try
				{
//					DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
					this.deleteReplaceData(objectGuid);
					this.updateBOMStructure(bomStructure.getEnd1ObjectGuid(), bomStructure.getEnd2ObjectGuid(), bomViewName, bomStructure.getBOMKey());
//					DataServer.getTransactionManager().commitTransaction();
				}
				catch (ServiceRequestException e)
				{
//					DataServer.getTransactionManager().rollbackTransaction();
					throw e;
				}
			}
		}
	}

	/**
	 * 
	 * @param foundationObject
	 * @return
	 */
	public FoundationObject saveReplaceData(FoundationObject foundationObject) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		return boas.getFSaverStub().saveObject(foundationObject, true, true, false, null, true, false, true);
	}

	/**
	 * 交换序号
	 * 
	 * @param frontObjectGuid
	 * @param laterObjectGuid
	 * @throws ServiceRequestException
	 */
	public void exchangeRSNumber(ObjectGuid frontObjectGuid, ObjectGuid laterObjectGuid) throws ServiceRequestException
	{
		FoundationObject frontObject = this.stubService.getBOAS().getObject(frontObjectGuid);
		FoundationObject laterObject = this.stubService.getBOAS().getObject(laterObjectGuid);
		String laterSequence = (String) laterObject.get(ReplaceSubstituteConstants.RSNumber);
		laterObject.put(ReplaceSubstituteConstants.RSNumber, frontObject.get(ReplaceSubstituteConstants.RSNumber));
		frontObject.put(ReplaceSubstituteConstants.RSNumber, laterSequence);
		this.saveReplaceData(frontObject);
		this.saveReplaceData(laterObject);
	}

	public boolean checkCreateReplaceData(FoundationObject foundationObject) throws ServiceRequestException
	{
		ObjectGuid componentObjectGuid = null;
		ObjectGuid masterObjectGuid = null;
		ObjectGuid rsObjectGuid = null;
		Object masterObject = foundationObject.get(ReplaceSubstituteConstants.MasterItem.toUpperCase());
		if (masterObject instanceof ObjectGuid)
		{
			masterObjectGuid = (ObjectGuid) masterObject;
		}
		Object componentObject = foundationObject.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase());
		if (componentObject instanceof ObjectGuid)
		{
			componentObjectGuid = (ObjectGuid) componentObject;
		}

		Object rsObject = foundationObject.get(ReplaceSubstituteConstants.RSItem.toUpperCase());
		if (rsObject instanceof ObjectGuid)
		{
			rsObjectGuid = (ObjectGuid) rsObject;
		}
		String bomViewName = (String) foundationObject.get(ReplaceSubstituteConstants.BOMViewDisplay.toUpperCase());
		String bomKey = (String) foundationObject.get(ReplaceSubstituteConstants.BOMKey.toUpperCase());
		return checkCreateReplaceData(masterObjectGuid, componentObjectGuid, rsObjectGuid, bomViewName, bomKey);
	}

	/**
	 * 判断新增的取替代数据是否符合规则
	 * 新增的取替代数据如果在
	 * 
	 * @param masterItemObjectGuid
	 * @param componentItemObjectGuid
	 * @param rsItemObjectGuid
	 * @param bomViewName
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean checkCreateReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomViewName, String bomKey)
			throws ServiceRequestException
	{
		boolean hasSame = false;
		if (componentItemObjectGuid != null && rsItemObjectGuid != null)
		{
			FoundationObject componentItem = this.stubService.getBOAS().getObject(componentItemObjectGuid);
			FoundationObject rsItem = this.stubService.getBOAS().getObject(rsItemObjectGuid);

			// 判断替代件与元件是否相同
			if (componentItemObjectGuid.getMasterGuid().equals(rsItemObjectGuid.getMasterGuid()))
			{
				throw new ServiceRequestException("ID_CLIENT_TOOL_REPLACE_PIECE_NO_PARET", "The replace item cannot be identical to the component item.");
			}
			// 判断替代件与主件是否相同
			if (masterItemObjectGuid != null && masterItemObjectGuid.getMasterGuid().equals(rsItemObjectGuid.getMasterGuid()))
			{
				throw new ServiceRequestException("ID_APP_BRM_REPLACE_NOT_EQUAL_MASTER", "The replace item cannot be identical to the master item.");
			}
			if (masterItemObjectGuid == null)
			{
				// 是否已经存在相同的全局替代关系（不包含失效的数据）
				hasSame = this.stubService.getReplaceQueryStub().isHasCompentItemGlobalReplaceData(componentItemObjectGuid, rsItemObjectGuid);
				if (hasSame)
				{
					if (componentItem != null && rsItem != null)
					{
						throw new ServiceRequestException("ID_APP_BRM_GLOBAL_REPLACE_RELATION_ALREADY_EXISTS", "The global replace relation is already exist", null,
								new Object[] { componentItem.getFullName(), rsItem.getFullName() });
					}
				}
			}
			else
			{
				if (StringUtils.isNullString(bomKey))
				{
					throw new ServiceRequestException("ID_APP_BRM_BOMKEY_IS_NULL", "bomkey is null");
				}
				// 是否存在相同的局部取替代关系（不包含失效的数据）
				FoundationObject masterItem = this.stubService.getBOAS().getObject(masterItemObjectGuid);
				hasSame = this.stubService.getReplaceQueryStub().isHasCompentItemPartReplaceDataExceptExpire(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid,
						bomViewName, bomKey);
				if (hasSame)
				{
					if (componentItem != null && rsItem != null)
					{
						Object[] objects = null;
						if (componentItem != null && rsItem != null)
						{
							objects = new Object[] { masterItem != null ? masterItem.getFullName() : "", componentItem.getFullName(), rsItem.getFullName() };
						}
						throw new ServiceRequestException("ID_APP_BRM_PART_REPLACE_RELATION_ALREADY_EXISTS", "The part replace relation is already exist", null, objects);
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 批量取替代表单日期检查
	 */
	public boolean checkDateForBatch(Date effectiveDate, Date expireDate, Date applyDate, String applyType) throws ServiceRequestException
	{
		if (applyType.equalsIgnoreCase(ReplaceSubstituteConstants.TYPE_ADD))
		{
			if (effectiveDate == null)
			{
				throw new ServiceRequestException("ID_CLIENT_REPLACE_RELATION_NULL_INEFFECTIVE", "Effective date is null!");
			}
			return checkDateForCreate(effectiveDate, expireDate, applyDate);
		}

		if (applyType.equalsIgnoreCase(ReplaceSubstituteConstants.TYPE_ALTER))
		{
			if (effectiveDate != null && expireDate != null)
			{
				if (DateFormat.compareDate(effectiveDate, expireDate) > 0)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 取替代日期检查
	 * 创建取替代时用
	 */
	public boolean checkDateForCreate(Date effectiveDate, Date expireDate, Date applyDate)
	{
		return checkDate(effectiveDate, expireDate, applyDate, true, true);
	}

	/**
	 * 取替代数据日期检查
	 * 
	 * @param effectiveDate
	 *            生效日期
	 * @param expireDate
	 *            失效日期
	 * @param applyDate
	 *            申请日期，applyDate可为null，applyDate为null时applyDate为系统时间
	 * @param checkEffectiveDate
	 *            是否将生效日期同ApplyDate作比较，修改取替代数据时，如果生效时间未修改，此参数应传false
	 * @param checkExpireDate
	 *            是否将失效日期同ApplyDate作比较，修改取替代数据时，如果失效时间未修改，次参数应传false
	 * @return
	 */
	public boolean checkDate(Date effectiveDate, Date expireDate, Date applyDate, boolean checkEffectiveDate, boolean checkExpireDate)
	{
		// 允许都为空，直接创建取替代数据
		if (effectiveDate == null && expireDate == null)
		{
			return true;
		}

		if (effectiveDate == null)
		{
			return false;
		}

		if (applyDate == null)
		{
			applyDate = DateFormat.getSysDate();
		}

		if (checkEffectiveDate && DateFormat.compareDate(effectiveDate, applyDate) < 0)
		{
			return false;
		}

		if (expireDate != null)
		{
			if (checkExpireDate && DateFormat.compareDate(applyDate, expireDate) > 0)
			{
				return false;
			}
			if (DateFormat.compareDate(effectiveDate, expireDate) > 0)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 通过流程创建批量取替代关系，并跟批量修改单关联
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	@Deprecated
	public void createBathReplaceRelation(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listAllValidProcAttach = this.stubService.getWFI().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(listAllValidProcAttach))
		{
			return;
		}
		for (ProcAttach procAttach : listAllValidProcAttach)
		{
			FoundationObject procFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub()
					.getObjectByGuid(new ObjectGuid(procAttach.getInstanceClassGuid(), null, procAttach.getInstanceGuid(), null), false);
			if (procFoundation != null)
			{
				procFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(procFoundation.getObjectGuid(), false);
				ClassInfo procAttachClassInfo = this.stubService.getEMM().getClassByGuid(procFoundation.getObjectGuid().getClassGuid());
				if (procAttachClassInfo != null && procAttachClassInfo.hasInterface(ModelInterfaceEnum.IRSApplyForm))
				{
					ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(procFoundation.getObjectGuid(), ReplaceSubstituteConstants.BATCHRS_ITEM);
					if (viewObject != null)
					{
						List<StructureObject> listStructure = this.stubService.getBOAS().listObjectOfRelation(viewObject.getObjectGuid(), null, null, null);
						if (!SetUtils.isNullList(listStructure))
						{
							for (StructureObject stru : listStructure)
							{
								ObjectGuid masterItem = stru.getEnd2ObjectGuid();
								ObjectGuid componentItem = this.stubService.getReplaceQueryStub().getObjectGuid(procFoundation, ReplaceSubstituteConstants.ComponentItem);
								ObjectGuid rsItem = this.stubService.getReplaceQueryStub().getObjectGuid(procFoundation, ReplaceSubstituteConstants.RSItem);
								String bomCodeGuid = (String) procFoundation.get(ReplaceSubstituteConstants.BOMViewDisplay);
								String bomViewName = this.stubService.getEMM().getCodeItem(bomCodeGuid).getName();
								if (masterItem != null && componentItem != null && rsItem != null)
								{
									try
									{
										boolean isCreate = this.checkCreateReplaceData(masterItem, componentItem, rsItem, bomViewName, null);
										if (isCreate)
										{
											FoundationObject fo = this.createObjectForRsApply(masterItem, componentItem, rsItem, null, procFoundation);
											if (fo != null)
											{
												BOMStructure bomstru = this.updateBOMStructure(masterItem, componentItem, bomViewName, null);
												if (procFoundation.get(ReplaceSubstituteConstants.ComponentUsage) != null
														&& ((Number) procFoundation.get(ReplaceSubstituteConstants.ComponentUsage)).doubleValue() > 0)
												{
													fo.put(ReplaceSubstituteConstants.ComponentUsage, procFoundation.get(ReplaceSubstituteConstants.ComponentUsage));
												}
												else if (bomstru != null)
												{
													fo.put(ReplaceSubstituteConstants.ComponentUsage, bomstru.getQuantity());
												}
												fo = this.saveReplaceData(fo);
												this.addRSRelation(procFoundation, fo);
											}
										}
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
								this.stubService.activeSession(true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 批量处理取替代申请
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void batchDealReplaceApply(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listAllValidProcAttach = this.stubService.getWFI().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(listAllValidProcAttach))
		{
			return;
		}

		for (ProcAttach procAttach : listAllValidProcAttach)
		{

			FoundationObject procFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub()
					.getObjectByGuid(new ObjectGuid(procAttach.getInstanceClassGuid(), null, procAttach.getInstanceGuid(), null), false);
			if (procFoundation == null)
			{
				continue;
			}

			procFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(procFoundation.getObjectGuid(), false);

			String formType = this.stubService.getEMM().getCodeItem((String) procFoundation.get(ReplaceSubstituteConstants.FORM_TYPE)).getName();
			if (formType == null)
			{
				continue;
			}

			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(procFoundation.getObjectGuid(), ReplaceSubstituteConstants.BATCHRS_ITEM);
			ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(procFoundation.getObjectGuid(), ReplaceSubstituteConstants.BATCHRS_ITEM);
			List<StructureObject> listStructure = null;
			if (viewObject != null)
			{
				SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(relationTemplate.getStructureClassName());
				UIObjectInfo structureListUIObject = this.stubService.getEMM().getUIObjectInCurrentBizModel(relationTemplate.getStructureClassName(), UITypeEnum.LIST);
				if (structureListUIObject != null)
				{
					List<UIField> uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(structureListUIObject.getGuid());
					if (!SetUtils.isNullList(uiFieldList))
					{
						for (UIField uiField : uiFieldList)
						{
							searchCondition.addResultField(uiField.getName());
						}
					}
				}
				listStructure = this.stubService.getBOAS().listObjectOfRelation(viewObject.getObjectGuid(), searchCondition, null, null);
			}

			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(procAttach.getInstanceClassGuid());
			if (classInfo == null)
			{
				throw new ServiceRequestException("ID_APP_BRM_UNABLE_TO_GET_THE_FORM_TYPE", "Unable to get the form type!");
			}
			if (classInfo.hasInterface(ModelInterfaceEnum.IRSApplyFormGlobal))
			{
				try
				{
					globalBatchProcess(listStructure, procFoundation, formType);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else if (classInfo.hasInterface(ModelInterfaceEnum.IRSApplyFormPartial))
			{
				if (viewObject == null || SetUtils.isNullList(listStructure))
				{
					DynaLogger.error("dyna.app.service.brs.brm.ReplaceObjectStub [batchDealReplaceApply]: Must add at least one instance linked to the partial batch apply!");
					throw new ServiceRequestException("ID_APP_BRM_BATCH_APPLY_INSTANCE", "Must add at least one instance linked to the partial batch apply!!");
				}
				partialBatchProcess(listStructure, procFoundation, formType);
			}
		}
	}

	/**
	 * 全局替代新增
	 */
	private void globalReplaceApplyAdd(FoundationObject procFoundation, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid) throws ServiceRequestException
	{
		FoundationObject globalReplaceData = this.createObjectForRsApply(null, componentItemObjectGuid, rsItemObjectGuid, null, procFoundation);
		if (globalReplaceData != null)
		{
			globalReplaceData = this.saveReplaceData(globalReplaceData);
			updateProcessResult(procFoundation, false, null);
		}
	}

	/**
	 * 全局替代修改
	 */
	private void globalReplaceApplyAlter(FoundationObject procFoundation, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> globalReplaceList = this.stubService.getReplaceQueryStub().getIdenticalGlobalReplaceData(componentItemObjectGuid, rsItemObjectGuid);
		if (SetUtils.isNullList(globalReplaceList))
		{
			throw new ServiceRequestException("IDD_APP_BRM_GLOBAL_APPLY_UNABLE_TO_FIND", "Unable to find the global replace!");
		}
		else
		{
			for (FoundationObject globalReplace : globalReplaceList)
			{
				updateReplaceData(procFoundation, globalReplace);
				this.stubService.saveReplaceData(globalReplace);
			}
			updateProcessResult(procFoundation, false, null);
		}
	}

	/**
	 * 全局替代申请
	 */
	private void globalBatchProcess(List<StructureObject> listStructure, FoundationObject procFoundation, String applyType) throws ServiceRequestException
	{

		ObjectGuid componentItemGuid = this.stubService.getReplaceQueryStub().getObjectGuid(procFoundation, ReplaceSubstituteConstants.ComponentItem);
		ObjectGuid rsItemObjectGuid = this.stubService.getReplaceQueryStub().getObjectGuid(procFoundation, ReplaceSubstituteConstants.RSItem);
		try
		{
			if (applyType.equalsIgnoreCase(ReplaceSubstituteConstants.TYPE_ADD))
			{
				globalReplaceApplyAdd(procFoundation, componentItemGuid, rsItemObjectGuid);
			}
			else if (applyType.equalsIgnoreCase(ReplaceSubstituteConstants.TYPE_ALTER))
			{
				globalReplaceApplyAlter(procFoundation, componentItemGuid, rsItemObjectGuid);
			}
		}
		catch (Exception e)
		{
			handleExceptionForBatch(e, procFoundation);
		}
	}

	/**
	 * 批量局部取替代处理
	 * 
	 * @param listStructure
	 *            MasterItem主件对象列表
	 * @param procFoundation
	 *            局部取替代申请单
	 */
	private void partialBatchProcess(List<StructureObject> listStructure, FoundationObject procFoundation, String applyType) throws ServiceRequestException
	{
		ObjectGuid componentItem = this.stubService.getReplaceQueryStub().getObjectGuid(procFoundation, ReplaceSubstituteConstants.ComponentItem);
		ObjectGuid rsItem = this.stubService.getReplaceQueryStub().getObjectGuid(procFoundation, ReplaceSubstituteConstants.RSItem);
		String bomCodeGuid = (String) procFoundation.get(ReplaceSubstituteConstants.BOMViewDisplay);
		String bomViewName = this.stubService.getEMM().getCodeItem(bomCodeGuid).getName();

		boolean hasException = false; // 该申请单是否含有异常
		for (StructureObject batchMasterStructureObject : listStructure)
		{
			if (componentItem != null && rsItem != null)
			{
				String bomKey = (String) batchMasterStructureObject.get(ReplaceSubstituteConstants.BOMKey);
				try
				{
//					DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
					if (applyType.equalsIgnoreCase(ReplaceSubstituteConstants.TYPE_ADD))
					{
						ObjectGuid masterItem = batchMasterStructureObject.getEnd2ObjectGuid();
						partialBatchAdd(masterItem, componentItem, rsItem, bomKey, bomViewName, procFoundation, batchMasterStructureObject);
					}
					else if (applyType.equalsIgnoreCase(ReplaceSubstituteConstants.TYPE_ALTER))
					{
						partialBatchAlter(null, componentItem, rsItem, bomKey, bomViewName, procFoundation, batchMasterStructureObject);
					}
//					DataServer.getTransactionManager().commitTransaction();
				}
				catch (Exception e)
				{
					hasException = true;
//					DataServer.getTransactionManager().rollbackTransaction();
					handleExceptionForBatch(e, batchMasterStructureObject);
				}
			}
			this.stubService.activeSession(true);
		}
		updateProcessResult(procFoundation, hasException, null);
	}

	private void partialBatchAdd(ObjectGuid masterItem, ObjectGuid componentItem, ObjectGuid rsItem, String bomKey, String bomViewName, FoundationObject procFoundation,
			StructureObject batchMasterStructureObject) throws ServiceRequestException
	{
		FoundationObject fo = this.createObjectForRsApply(masterItem, componentItem, rsItem, bomKey, procFoundation);
		if (fo != null)
		{
			BOMStructure bomStructure = this.updateBOMStructure(masterItem, componentItem, bomViewName, (String) batchMasterStructureObject.get(ReplaceSubstituteConstants.BOMKey));

			if (procFoundation.get(ReplaceSubstituteConstants.ComponentUsage) != null && ((Number) procFoundation.get(ReplaceSubstituteConstants.ComponentUsage)).doubleValue() > 0)
			{
				fo.put(ReplaceSubstituteConstants.ComponentUsage, procFoundation.get(ReplaceSubstituteConstants.ComponentUsage));
				fo.put(ReplaceSubstituteConstants.Sequence, bomStructure.getSequence());
			}
			else if (bomStructure != null)
			{
				fo.put(ReplaceSubstituteConstants.ComponentUsage, bomStructure.getQuantity());
			}
			fo = this.saveReplaceData(fo);
			updateProcessResult(batchMasterStructureObject, false, null);
		}
	}

	private void partialBatchAlter(ObjectGuid masterItem, ObjectGuid componentItem, ObjectGuid rsItem, String bomKey, String bomViewName, FoundationObject procFoundation,
			StructureObject batchMasterStructureObject) throws ServiceRequestException
	{
		ObjectGuid replaceObjectGuid = this.stubService.getReplaceQueryStub().getObjectGuid(batchMasterStructureObject, ReplaceSubstituteConstants.REPLACE_DATA);
		FoundationObject replaceData = this.stubService.getBOAS().getObject(replaceObjectGuid);
		updateReplaceData(procFoundation, replaceData);
		this.stubService.saveReplaceData(replaceData);
		updateProcessResult(batchMasterStructureObject, false, null);
	}

	/**
	 * 取替代批量修改
	 * 
	 * @param listStructure
	 *            Replace取替代数据列表
	 * @param procFoundation
	 *            修改申请单
	 */
	@Deprecated
	public void batchAlter(List<StructureObject> listStructure, FoundationObject procFoundation) throws ServiceRequestException
	{
		for (StructureObject structureObject : listStructure)
		{
			try
			{
//				DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
				FoundationObject replaceData = this.stubService.getBOAS().getObject(structureObject.getEnd2ObjectGuid());
				updateReplaceData(procFoundation, replaceData);
				this.stubService.saveReplaceData(replaceData);
				updateProcessResult(structureObject, true, null);
//				DataServer.getTransactionManager().commitTransaction();
			}
			catch (Exception e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
				handleExceptionForBatch(e, structureObject);
			}
			this.stubService.activeSession(true);
		}
	}

	/**
	 * 更新取替代数据
	 */
	private void updateReplaceData(FoundationObject procFoundation, FoundationObject replaceData) throws ServiceRequestException
	{
		Date effectiveDate = (Date) replaceData.get(ReplaceSubstituteConstants.EffectiveDate);
		Date expireDate = (Date) replaceData.get(ReplaceSubstituteConstants.InvalidDate);
		Date effectiveDateAfter = procFoundation.get(ReplaceSubstituteConstants.EffectiveDate) == null ? effectiveDate
				: (Date) procFoundation.get(ReplaceSubstituteConstants.EffectiveDate);
		Date expireDateAfter = procFoundation.get(ReplaceSubstituteConstants.InvalidDate) == null ? expireDate : (Date) procFoundation.get(ReplaceSubstituteConstants.InvalidDate);

		// 判断日期是否符合规则
		if (!this.checkDate(effectiveDateAfter, expireDateAfter, null, DateFormat.compareDate(effectiveDate, effectiveDateAfter) != 0,
				DateFormat.compareDate(expireDate, expireDateAfter) != 0))
		{
			throw new ServiceRequestException("ID_APP_BRM_REPLACE_APPLY_DATE_ILLEGAL", "The date of batch-apply is illegal");
		}

		// 判断取替代是否失效
		if (ReplaceSubstituteConstants.getReplaceDataStatus(replaceData) == ReplaceStatusEnum.EXPIRE)
		{
			throw new ServiceRequestException("ID_APP_BRM_FORM_RS_EXPIRE", "The replace data cannot be modified since it is expire!");
		}

		replaceData.put(ReplaceSubstituteConstants.EffectiveDate, effectiveDateAfter);
		replaceData.put(ReplaceSubstituteConstants.InvalidDate, expireDateAfter);
		if (procFoundation.get(ReplaceSubstituteConstants.ComponentUsage) != null)
		{
			replaceData.put(ReplaceSubstituteConstants.ComponentUsage, procFoundation.get(ReplaceSubstituteConstants.ComponentUsage));
		}
	}

	/**
	 * 增加批量修改和取替代的关联关系
	 * 
	 * @param procFoundation
	 * @param fo
	 * @throws ServiceRequestException
	 */
	@Deprecated
	private void addRSRelation(FoundationObject procFoundation, FoundationObject fo) throws ServiceRequestException
	{
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(procFoundation.getObjectGuid(),
				ReplaceSubstituteConstants.BatchRS_RSRelationship);
		if (relationTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_UECRECPSTUB_TEMPLATE_NULL", "the template is null", null, ReplaceSubstituteConstants.BatchRS_RSRelationship);
		}
		String structureClassName = relationTemplate.getStructureClassName();
		if (StringUtils.isNullString(structureClassName))
		{
			structureClassName = StructureObject.STRUCTURE_CLASS_NAME;
		}
		StructureObject structureObject = this.stubService.getBOAS().newStructureObject(null, structureClassName);
		structureObject.setObjectGuid(new ObjectGuid(relationTemplate.getStructureClassGuid(), relationTemplate.getStructureClassName(), null, null));
		structureObject.put("StructureClassName", relationTemplate.getStructureClassName());
		structureObject.put("viewName", relationTemplate.getName());

		RelationStub relationStub = ((BOASImpl) this.stubService.getBOAS()).getRelationStub();
		ViewObject viewObject = this.stubService.getWFI().getRelationByEND1(procFoundation.getObjectGuid(), ReplaceSubstituteConstants.BatchRS_RSRelationship);
		// 先创建一个View 然后再关联
		if (viewObject == null)
		{
			viewObject = relationStub.saveRelationByTemplate(relationTemplate.getGuid(), procFoundation.getObjectGuid(), false, null);
		}
		((BOASImpl) this.stubService.getBOAS()).getRelationLinkStub().link(viewObject.getObjectGuid(), fo.getObjectGuid(), structureObject, false, null);
	}

	/**
	 * 创建取替代关系
	 * 
	 * @param masterItemObjectGUid
	 * @param componentItemObjectGuid
	 * @param rsItemObjectGuid
	 * @param procFoundation
	 *            取替代申请对象
	 * @throws ServiceRequestException
	 */
	private FoundationObject createObjectForRsApply(ObjectGuid masterItemObjectGUid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomKey,
			FoundationObject procFoundation) throws ServiceRequestException
	{
		if (componentItemObjectGuid != null && rsItemObjectGuid != null)
		{
			componentItemObjectGuid.setIsMaster(true);
			rsItemObjectGuid.setIsMaster(true);
			FoundationObject componentItem = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(componentItemObjectGuid, false);
			FoundationObject rsItem = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(rsItemObjectGuid, false);
			if (componentItem.getObjectGuid() != null && rsItem.getObjectGuid() != null)
			{
				return this.createReplaceData(masterItemObjectGUid, componentItem.getObjectGuid(), rsItem.getObjectGuid(), bomKey, procFoundation, false);
			}
		}
		return null;
	}

	/**
	 * 创建取替代
	 * 
	 * @param bomKey
	 *            bomKey：在一个BOM中存在多个相同的end2的情况下，对不同顺序号的end2做标识的字段
	 *            当另一个传入参数foundation为取替代批量申请单对象时，bomKey不能为null；当另一个传入参数foundation为取替代对象时，bomKey可以为null
	 * @param foundationObject
	 *            可能为取替代批量申请单或者取替代对象
	 * @param copy
	 *            是否另存
	 */
	public FoundationObject createReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomKey,
			FoundationObject foundationObject, boolean copy) throws ServiceRequestException
	{
		if (foundationObject != null)
		{
			FoundationObject newFo = this.newFoundationObject(null, null);
			// 此处的FoundationObject对象可能是取替代申请或是取替代对象，取替代申请的BOMVIEWDISPLAY是Code类型，取替代对象的BOMVIEDISPLAY是String类型
			String bomViewName = (String) foundationObject.get(ReplaceSubstituteConstants.BOMViewDisplay);
			if (StringUtils.isGuid(bomViewName))
			{
				bomViewName = this.stubService.getEMM().getCodeItem(bomViewName).getName();
			}
			if (masterItemObjectGuid == null)
			{
				newFo.put(ReplaceSubstituteConstants.MasterItem, masterItemObjectGuid);
				this.setRsTypeAndRange(newFo, ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI);
			}
			else
			{
				newFo.put(ReplaceSubstituteConstants.MasterItem, masterItemObjectGuid);
				newFo.put(ReplaceSubstituteConstants.Scope, foundationObject.get(ReplaceSubstituteConstants.Scope));
				newFo.put(ReplaceSubstituteConstants.RSType, foundationObject.get(ReplaceSubstituteConstants.RSType));
				newFo.put(ReplaceSubstituteConstants.BOMViewDisplay, bomViewName);
			}
			newFo.put(ReplaceSubstituteConstants.ComponentItem, componentItemObjectGuid);
			newFo.put(ReplaceSubstituteConstants.RSItem, rsItemObjectGuid);

			if (copy)
			{
				newFo.put(ReplaceSubstituteConstants.RSNumber, foundationObject.get(ReplaceSubstituteConstants.RSNumber));
			}
			else
			{
				newFo.put(ReplaceSubstituteConstants.RSNumber,
						this.getRSNumber(masterItemObjectGuid, componentItemObjectGuid, (String) newFo.get(ReplaceSubstituteConstants.BOMViewDisplay)));
				newFo.put(ReplaceSubstituteConstants.EffectiveDate, foundationObject.get(ReplaceSubstituteConstants.EffectiveDate));
				newFo.put(ReplaceSubstituteConstants.InvalidDate, foundationObject.get(ReplaceSubstituteConstants.InvalidDate));
			}
			newFo.put(ReplaceSubstituteConstants.ComponentUsage, foundationObject.get(ReplaceSubstituteConstants.ComponentUsage));
			String inputBomKey = (null == bomKey ? (String) foundationObject.get(ReplaceSubstituteConstants.BOMKey) : bomKey);
			newFo.put(ReplaceSubstituteConstants.BOMKey, inputBomKey);
			newFo.put(ReplaceSubstituteConstants.Sequence, foundationObject.get(ReplaceSubstituteConstants.Sequence));
			newFo.put(ReplaceSubstituteConstants.COMPARE_DATE, foundationObject.getCreateTime());
			return this.createReplaceData(newFo);
		}
		return null;
	}

	/**
	 * 取得取替代编号
	 * 
	 */
	private String getRSNumber(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, String bomViewDisplay) throws ServiceRequestException
	{
		List<FoundationObject> listRS = this.stubService.getReplaceQueryStub().listReplaceDataByRang(masterItemObjectGuid, componentItemObjectGuid, null, null, bomViewDisplay,
				null, true, false);
		if (!SetUtils.isNullList(listRS))
		{
			int i = 0;
			for (FoundationObject fo : listRS)
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
	 * 保存批量取替代之前的检查
	 * 元件、替代件不相同，且(最新版)不被废弃,失效日期应该晚于生效日期
	 * 
	 * @param foundationObject
	 * @throws ServiceRequestException
	 */
	public void checkBathReplaceBeforeSave(FoundationObject foundationObject) throws ServiceRequestException
	{
		if (foundationObject != null)
		{
			ObjectGuid componentItemObjectGuid = this.stubService.getReplaceQueryStub().getObjectGuid(foundationObject, ReplaceSubstituteConstants.ComponentItem);
			componentItemObjectGuid.setIsMaster(true);
			ObjectGuid rsItemObjectGuid = this.stubService.getReplaceQueryStub().getObjectGuid(foundationObject, ReplaceSubstituteConstants.RSItem);
			rsItemObjectGuid.setIsMaster(true);
			if (componentItemObjectGuid != null && componentItemObjectGuid.getMasterGuid() != null && rsItemObjectGuid != null && rsItemObjectGuid.getMasterGuid() != null)
			{
				if (componentItemObjectGuid.getMasterGuid().equalsIgnoreCase(rsItemObjectGuid.getMasterGuid()))
				{
					throw new ServiceRequestException("ID_APP_REPLACEOBJECTSTUB_BATHSAVE_SAME", "Save failed, componentItem and rsItem are identical!", null);
				}
				FoundationObject comfo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(componentItemObjectGuid, false);
				FoundationObject rsfo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(rsItemObjectGuid, false);
				if (comfo == null || rsfo == null)
				{
					throw new ServiceRequestException("ID_DS_NO_DATA", "Data not exist!", null);
				}
				if (comfo != null && SystemStatusEnum.OBSOLETE == comfo.getStatus())
				{
					throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "Object has been obsoleted!", null, comfo.getFullName());
				}
				if (rsfo != null && SystemStatusEnum.OBSOLETE == rsfo.getStatus())
				{
					throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "Object has been obsoleted!", null, rsfo.getFullName());
				}
			}
			// if (foundationObject.get(ReplaceSubstituteConstants.InvalidDate) != null)
			// {
			Date effectiveDate = (Date) foundationObject.get(ReplaceSubstituteConstants.EffectiveDate);
			Date expireDate = (Date) foundationObject.get(ReplaceSubstituteConstants.InvalidDate);
			String formType = this.stubService.getEMM().getCodeItem((String) foundationObject.get(ReplaceSubstituteConstants.FORM_TYPE)).getName();
			if (!this.checkDateForBatch(effectiveDate, expireDate, null, formType))
			{
				throw new ServiceRequestException("ID_APP_BRM_REPLACE_APPLY_DATE_ILLEGAL", "The date of batch-apply is illegal");
			}
		}
	}

	/**
	 * 根据主件和元件ObjectGuid处理历史数据(当移除子件时，已经生效的取替代数据设置成失效；未生效的取替代直接删除)
	 * 
	 * @param dealWith
	 *            是否处理
	 * @param isdelete
	 *            bomStructure是否被删除
	 */
	public void dealWithHistoryReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, BOMStructure bomStructure, String bomViewName, boolean dealWith,
			boolean isdelete) throws ServiceRequestException
	{
		if (bomStructure != null)
		{
			if (!isdelete)
			{
				bomStructure.setRsFlag(false);
			}
			ObjectGuid masterItem = bomStructure.getEnd1ObjectGuid();
			ObjectGuid componentItem = bomStructure.getEnd2ObjectGuid();
			if (masterItemObjectGuid != null && StringUtils.isGuid(masterItemObjectGuid.getMasterGuid()))
			{
				masterItem = masterItemObjectGuid;
			}
			if (componentItemObjectGuid != null && StringUtils.isGuid(componentItemObjectGuid.getMasterGuid()))
			{
				componentItem = componentItemObjectGuid;
			}
			if (masterItem != null && componentItem != null)
			{
				List<FoundationObject> listFo = this.stubService.getReplaceQueryStub().listReplaceDataByRang(masterItem, componentItem, ReplaceRangeEnum.PART, null, bomViewName,
						bomStructure.getBOMKey(), true, false);
				if (!SetUtils.isNullList(listFo))
				{
					bomStructure.setRsFlag(true);
					if (dealWith)
					{
						for (FoundationObject fo : listFo)
						{
							Date effectiveDate = null;
							if (fo.get(ReplaceSubstituteConstants.EffectiveDate) != null)
							{
								effectiveDate = (Date) fo.get(ReplaceSubstituteConstants.EffectiveDate);
							}
							Date invalidDate = null;
							if (fo.get(ReplaceSubstituteConstants.InvalidDate) != null)
							{
								invalidDate = (Date) fo.get(ReplaceSubstituteConstants.InvalidDate);
							}
							if (effectiveDate == null && invalidDate == null)
							{
								((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteObject(fo, false);
							}
							else if (effectiveDate != null && invalidDate == null)
							{
								fo.put(ReplaceSubstituteConstants.InvalidDate, DateFormat.getSysDate());
								this.stubService.saveObject(fo);
							}
						}
						if (!isdelete)
						{
							List<FoundationObject> leftList = this.stubService.getReplaceQueryStub().listReplaceDataByRang(masterItem, bomStructure.getEnd2ObjectGuid(),
									ReplaceRangeEnum.PART, null, bomViewName, bomStructure.getBOMKey(), true, false);
							if (!SetUtils.isNullList(leftList))
							{
								bomStructure.setRsFlag(true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 根据主件或者元件或者取替代件，删除其取替代关系
	 */
	public void deleteRepaceDataByItem(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid != null && StringUtils.isGuid(objectGuid.getMasterGuid()))
		{
			String classguid = objectGuid.getClassGuid();
			if (StringUtils.isGuid(classguid))
			{
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classguid);
				if (classInfo != null && !classInfo.hasInterface(ModelInterfaceEnum.IReplaceSubstitute))
				{

					List<FoundationObject> listMaster = this.stubService.getReplaceQueryStub().listReplaceDataByRang(objectGuid, null, ReplaceRangeEnum.PART, null, null, null,
							true, false);
					this.delete(listMaster, false);
					// 查询元件或者取替代件是否还有其他版本对象
					objectGuid.setIsMaster(true);
					objectGuid.setGuid(null);
					FoundationObject fo = this.stubService.getBOAS().getObject(objectGuid);
					if (fo == null)
					{
						List<FoundationObject> listComponent = this.stubService.getReplaceQueryStub().listReplaceDataByRang(null, objectGuid, null, null, null, null, true, false);
						this.delete(listComponent, false);
						List<FoundationObject> listRSItem = this.stubService.getReplaceQueryStub().listRepalcedByRsItem(objectGuid, null, null, true);
						this.delete(listRSItem, true);
					}
				}
			}
		}
	}

	private void delete(List<FoundationObject> foundationObjectList, boolean isUpdateBOMStructure) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(foundationObjectList))
		{
			for (FoundationObject foundationObject : foundationObjectList)
			{
				this.deleteReplaceData(foundationObject.getObjectGuid());
				if (isUpdateBOMStructure)
				{
					this.updateBOMStructure(foundationObject);
				}
			}
		}
	}

	/**
	 * 更新BOMStructure的取替代标记
	 * 供服务层接口调用
	 */
	public void updateBomRsFlag(FoundationObject replaceData) throws ServiceRequestException
	{
		updateBOMStructure(replaceData);
	}

	/**
	 * 更新BOMStructure的取替代标记
	 */
	private void updateBOMStructure(FoundationObject replaceData) throws ServiceRequestException
	{
		if (replaceData != null && replaceData.get(ReplaceSubstituteConstants.MasterItem) != null)
		{
			ObjectGuid masterObjectGuid = new ObjectGuid((String) replaceData.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.ClassGuid), null,
					(String) replaceData.get(ReplaceSubstituteConstants.MasterItem), null);
			ObjectGuid componentObjectGuid = new ObjectGuid((String) replaceData.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.ClassGuid), null,
					(String) replaceData.get(ReplaceSubstituteConstants.ComponentItem),
					(String) replaceData.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER), true, null);
			List<BOMTemplateInfo> bomTemplateList = this.stubService.getEMM().listBOMTemplateByEND2(componentObjectGuid);
			String bomKey = (String) replaceData.get(ReplaceSubstituteConstants.BOMKey.toUpperCase());
			if (!SetUtils.isNullList(bomTemplateList))
			{
				for (BOMTemplateInfo bomTemplate : bomTemplateList)
				{
					BOMView bomView = this.stubService.getBOMS().getBOMViewByEND1(masterObjectGuid, bomTemplate.getName());
					if (bomView != null)
					{
						this.updateBOMStructure(masterObjectGuid, componentObjectGuid, bomTemplate.getName(), bomKey);
					}
				}
			}
		}
	}

	/**
	 * 更新BOMStructure的取替代标记
	 */
	private BOMStructure updateBOMStructure(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, String bomName, String bomKey) throws ServiceRequestException
	{
		if (masterItemObjectGuid != null && componentItemObjectGuid != null && !StringUtils.isNullString(bomName))
		{
			BOMView bomView = this.stubService.getBOMS().getBOMViewByEND1(masterItemObjectGuid, bomName);
			if (bomView != null)
			{
				String structureClassName = bomView.getStructureClassName();
				SearchCondition searchCondition = null;
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(structureClassName);
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(structureClassName, uiObjectList);
				List<BOMStructure> bomStructureList = this.stubService.getBOMS().listBOM(bomView.getObjectGuid(), searchCondition, null, null);
				if (!SetUtils.isNullList(bomStructureList))
				{
					for (BOMStructure structure : bomStructureList)
					{
						ObjectGuid end2ObjectGuid = structure.getEnd2ObjectGuid();
						if (end2ObjectGuid != null && StringUtils.isGuid(end2ObjectGuid.getMasterGuid())
								&& end2ObjectGuid.getMasterGuid().equals(componentItemObjectGuid.getMasterGuid()) && structure.getBOMKey().equals(bomKey))
						{
							List<FoundationObject> foList = this.stubService.listReplaceDataByRang(masterItemObjectGuid, componentItemObjectGuid, ReplaceRangeEnum.PART, null,
									bomName, bomKey, true);
							if (SetUtils.isNullList(foList))
							{
								structure.setRsFlag(false);
							}
							else
							{
								structure.setRsFlag(true);
							}
							((BOMSImpl) this.stubService.getBOMS()).getBomStub().saveBOM(structure, false, true, true);
							return structure;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 读取取替代配置
	 */
	public ReplaceConfig getReplaceConfig() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<ReplaceConfig> replaceConfigList = sds.query(ReplaceConfig.class, new HashMap<String, Object>(), "selectReplaceConfig");
		if (!SetUtils.isNullList(replaceConfigList))
		{
			return replaceConfigList.get(0);
		}
		else
		{
			return null;
		}
	}

	public boolean isReplaceControl() throws ServiceRequestException
	{
		ReplaceConfig replaceConfig = getReplaceConfig();
		if (replaceConfig != null)
		{
			return replaceConfig.getQASSOCIATEDTYPE().equals("1") ? true : false;
		}
		return true;
	}

	public void updateReplaceConfig(ReplaceConfig replaceConfig) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.update(ReplaceConfig.class, replaceConfig, "updateReplaceConfig");
	}

	/**
	 * 更新取替代申请表关联对象的状态：
	 * 该状态用以描述取替代数据的处理结果
	 * 全局的流程结果信息存放在申请表单中；局部的流程结果信息存放在关联关系结构字段中
	 * 
	 * @param hasException
	 *            有无异常，true为有异常，false为无异常
	 * @param message
	 *            描述信息，为错误结果提供额外描述，仅当结果为失败的时候传递
	 * @throws ServiceRequestException
	 */
	private void updateProcessResult(DynaObject dynaObject, boolean hasException, String message) throws ServiceRequestException
	{
		if (hasException)
		{
			dynaObject.put(ReplaceSubstituteConstants.EXCEPTION_DETAIL, message);
		}
		dynaObject.put(ReplaceSubstituteConstants.PROCESS_RESULT, BooleanUtils.getBooleanStringYN(hasException));
		if (dynaObject instanceof StructureObject)
		{
			((BOASImpl) this.stubService.getBOAS()).getStructureStub().saveStructure((StructureObject) dynaObject, false); // 忽略权限保存StructureObject
		}
		else if (dynaObject instanceof FoundationObject)
		{
			((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject((FoundationObject) dynaObject, true, false, false, null, false, false, false);
		}
	}

	/**
	 * 批量取替代申请异常处理
	 */
	private void handleExceptionForBatch(Exception e, DynaObject dynaObject)
	{
		try
		{
			DynaLogger.error(e);
			e.printStackTrace();
			String errorMessage = null;
			if (e instanceof ServiceRequestException)
			{
				errorMessage = this.stubService.getMSRM().getMSRString(((ServiceRequestException) e).getMsrId(), this.stubService.getUserSignature().getLanguageEnum().toString(),
						((ServiceRequestException) e).getArgs());
			}
			else
			{
				errorMessage = e.getMessage();
			}
			updateProcessResult(dynaObject, true, errorMessage);
		}
		catch (Exception ex)
		{
			DynaLogger.error(ex);
			ex.printStackTrace();
		}
	}
}
