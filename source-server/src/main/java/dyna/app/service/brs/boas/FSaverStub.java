/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FSaverStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.numbering.NumberAllocate;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.brm.BRMImpl;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.brs.dss.InstFileStub;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.brs.slc.SLCImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.*;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Wanglei
 * 
 */
@Component
public class FSaverStub extends AbstractServiceStub<BOASImpl>
{
	public static final String CHECKOUT_ERROR_ID = "CHECKOUTERRORID";

	/**
	 * 根据编码规则生成对象的ID
	 * 
	 * @param foundationObject
	 *            要处理的对象
	 * @return 对象的ID
	 * @throws ServiceRequestException
	 * @author caogc
	 */
	@Deprecated
	public String allocateUniqueId(FoundationObject foundationObject) throws ServiceRequestException
	{
		ObjectGuid objectGuid = foundationObject.getObjectGuid();

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		try
		{
			// 使用建模器编码规则编码
			NumberAllocate nc = new NumberAllocate();

			return nc.modelerAllocate(this.stubService.getEMM(), foundationObject, this.stubService, true, false, false);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	@Deprecated
	protected String allocateUniqueAlterId(FoundationObject foundationObject) throws ServiceRequestException
	{
		ObjectGuid objectGuid = foundationObject.getObjectGuid();

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		try
		{
			// 使用建模器编码规则编码
			NumberAllocate nc = new NumberAllocate();
			String id = nc.modelerAllocate(this.stubService.getEMM(), foundationObject, this.stubService, false, true, false);

			return id;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	@Deprecated
	protected String allocateUniqueName(FoundationObject foundationObject) throws ServiceRequestException
	{
		ObjectGuid objectGuid = foundationObject.getObjectGuid();

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		try
		{
			// 使用建模器编码规则编码
			NumberAllocate nc = new NumberAllocate();
			String id = nc.modelerAllocate(this.stubService.getEMM(), foundationObject, this.stubService, false, false, true);

			return id;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		finally
		{
		}
	}

	/**
	 * 创建FoundationObject对象
	 * 
	 * @param foundationObject
	 *            要创建的FoundationObject对象
	 * @param hasReturn
	 *            true 有返回值 ,false 返回null
	 * @return 创建后的FoundationObject对象
	 * @throws ServiceRequestException
	 */
	public FoundationObject createObject(FoundationObject foundationObject, String originalFoundationGuid, boolean isCheckAuth, boolean isCheckOut) throws ServiceRequestException
	{
		String ownerUserGuid = this.stubService.getUserSignature().getUserGuid();
		String ownerGroupGuid = this.stubService.getUserSignature().getLoginGroupGuid();

		return this.createObject(foundationObject, originalFoundationGuid, ownerGroupGuid, ownerUserGuid, isCheckAuth, true, true, isCheckOut, null, false);

	}

	public FoundationObject createObject(FoundationObject foundationObject, boolean isClearStatus) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getUserSignature().getUserGuid();
		String groupGuid = this.stubService.getUserSignature().getLoginGroupGuid();
		// return this.createObject(foundationObject, null, groupGuid, operatorGuid,
		// Constants.isSupervisor(true, this.stubService), true);
		//
		return this.createObject(foundationObject, null, groupGuid, operatorGuid, Constants.isSupervisor(true, this.stubService), true, true, false, null, isClearStatus);

	}

	public FoundationObject createObject(FoundationObject foundationObject, String originalFoundationGuid, String ownerGroupGuid, String ownerUserGuid, boolean isCheckAuth,
			boolean isNeededEffective) throws ServiceRequestException
	{
		return this.createObject(foundationObject, originalFoundationGuid, ownerGroupGuid, ownerUserGuid, isCheckAuth, isNeededEffective, true, false, null, false);
	}

	public FoundationObject createAndCheckOutObject(FoundationObject foundationObject, String originalFoundationGuid, String ownerGroupGuid, String ownerUserGuid,
			boolean isCheckAuth, boolean isNeededEffective) throws ServiceRequestException
	{
		FoundationObject createObject = this.createObject(foundationObject, originalFoundationGuid, ownerGroupGuid, ownerUserGuid, isCheckAuth, isNeededEffective, true, true, null,
				true);

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(createObject.getObjectGuid().getClassGuid());
		// 打开取替代对象时，不记录历史
		if (classInfo != null && !classInfo.hasInterface(ModelInterfaceEnum.IReplaceSubstitute))
		{
			BIViewHis biViewHis = new BIViewHis();
			biViewHis.setInstanceGuid(createObject.getObjectGuid().getGuid());
			biViewHis.setInstanceClassGuid(createObject.getObjectGuid().getClassGuid());
			biViewHis.setInstanceBOGuid(createObject.getObjectGuid().getBizObjectGuid());
			biViewHis.put(BIViewHis.CREATE_USER, this.stubService.getOperatorGuid());

			this.stubService.getPOS().saveBIViewHis(biViewHis);
		}
		// 检出

		try
		{
			if (!createObject.isCheckOut() && createObject.isCommited())
			{
				createObject = this.stubService.checkOut(createObject);
			}
		}
		catch (ServiceRequestException e)
		{
			createObject.put(CHECKOUT_ERROR_ID, "ID_APP_CREATE_CHECKOUT_FAIL");
			// throw new ServiceRequestException("ID_APP_CREATE_CHECKOUT_FAIL",
			// "create successful , checkout error");

		}

		return createObject;

	}

	/**
	 * 用相同信息创建对象/或者新建对象的修订版
	 * 
	 * @param foundationObject
	 *            新建的对象
	 * @param viewObjectList
	 *            关联的viewObjectList
	 * @param isClearId
	 *            true:相同信息创建,false:新建修订版
	 * @param isRevisionIdAddOne
	 *            true 版本号加1 false 版本号不变<br>
	 *            因为有些新建修订版的操作是来自前台画面 所以前台会把这个新的版本ID传过来,这时候不需要加1<br>
	 *            有些新建修订版的操作是在后台直接进行的，这种情况版本ID要加1
	 * @param isDataImport
	 *            是否是数据导入的修订
	 * @return 用相同信息创建以后的对象
	 * @throws ServiceRequestException
	 * @author caogc
	 */
	public FoundationObject saveAsObject(FoundationObject foundationObject, List<ObjectGuid> bomViewObjectGuidList, String ownerGroupGuid, String ownerUserGuid, boolean isClearId,
			boolean isRevisionIdAddOne, boolean isCheckAuth, boolean isCheckOut, boolean isContainPartReplace, boolean isContainGlobalReplce) throws ServiceRequestException
	{

		FoundationObject retFoundationObject = null;
		// ConfigRuleBOPhaseSet boPhaseSet = null;
		BOInfo boInfo = null;
		ConfigRuleBOLM configRuleBOLM = null;
		// List<BOMStructure> bomStructureList = null;

		DataRule bomRule = new DataRule();
		bomRule.setLocateTime(DateFormat.getSysDate());

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			BOMS boms = this.stubService.getBOMS();

			ObjectGuid objectGuid = foundationObject.getObjectGuid();

			String originalFoundationGuid = null;
			String saveAsoriginalFoundationGuid = null;
			// 处理relation
			// 查找所有关联的ViewObject
			// 注：不能移动
			List<ViewObject> viewObjectList = this.stubService.listRelation(foundationObject.getObjectGuid());

			boInfo = this.stubService.getEMM().getCurrentBizObject(objectGuid.getClassGuid());

			if (isRevisionIdAddOne)
			{
				Number startRevisionIdSequence = (Number) foundationObject.get(SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName());
				foundationObject.setRevisionId(this.stubService.getFRevisionStub().getNextRevision(foundationObject.getObjectGuid(), startRevisionIdSequence.intValue()));
			}

			objectGuid = foundationObject.resetObjectGuid();
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);

			List<FoundationObject> globalReplace = null;
			// 新修订
			if (!isClearId)
			{
				FoundationObject existObject = this.stubService.getObject(objectGuid);
				if (existObject == null)
				{
					throw new ServiceRequestException("ID_APP_REVSION_OBJECT_NOT_FOUND", "foundation not found");
				}
				if (boInfo != null)
				{
					configRuleBOLM = ((SLCImpl) this.stubService.getSLC()).getConfigRuleBOLMStub()
							.getConfigRuleBOLMContainParent(this.stubService.getUserSignature().getLoginGroupBMGuid(), boInfo.getGuid(), false);
				}
			}
			// 另存
			if (isClearId)
			{
				if (isContainGlobalReplce)
				{
					globalReplace = this.stubService.getBRM().listReplaceDataByRang(null, objectGuid, ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI, null, null, false);
				}
				saveAsoriginalFoundationGuid = objectGuid.getGuid();
				// 清除masterGuid
				objectGuid.setMasterGuid(null);

				// 10.17变更
				// 料件另存时会将原料件的族表信息一起复制过来 料件另存时不允许将与族表中相关的字段信息带入，
				// 即不复制料件实例上与族表相关的字段（这些字段分别是ma_foundation数据表中的CONFIGINSTANCE、GENERICITEM、GENERICITEM$CLASS、GENERICITEM$MASTER）

				Number startRevisionIdSequence = (Number) foundationObject.get(SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName());
				foundationObject.setRevisionId(this.stubService.getFRevisionStub().getInitRevisionId(startRevisionIdSequence == null ? 0 : startRevisionIdSequence.intValue()));
			}
			else
			{
				// 如果是修改 那么给该字段赋值
				originalFoundationGuid = objectGuid.getGuid();
			}

			// 清除guid
			objectGuid.setGuid(null);
			// 清除classification的guid
			List<FoundationObject> restoreAllClassification = foundationObject.restoreAllClassification(false);
			if (!SetUtils.isNullList(restoreAllClassification))
			{
				for (FoundationObject object : restoreAllClassification)
				{
					ObjectGuid objectGuid2 = object.getObjectGuid();
					objectGuid2.setGuid(null);
					object.setObjectGuid(objectGuid2);
				}
			}

			foundationObject.setObjectGuid(objectGuid);
			// 给拥有者字段赋值
			foundationObject.setOwnerUserGuid(ownerUserGuid);
			foundationObject.setOwnerGroupGuid(ownerGroupGuid);

			EMM emm = this.stubService.getEMM();
			// 新修订
			if (!isClearId)
			{
				boolean hasMutliWorkVersion = DataServer.getInstanceService().hasMutliWorkVersion(foundationObject.getObjectGuid());
				if (hasMutliWorkVersion)
				{
					throw new ServiceRequestException("ID_APP_CREATE_MUTLI_REVISION", "not allowed to create mutli working version");
				}
			}

			foundationObject.clear(SystemClassFieldEnum.STATUS.getName());
			foundationObject.clear(SystemClassFieldEnum.RELEASETIME.getName());
			if (!isClearId)
			{
				if (configRuleBOLM == null || configRuleBOLM.isReviseToFirstPhrase())
				{
					LifecyclePhaseInfo lifecyclePhaseInfo = emm.getFirstLifecyclePhaseInfoByClassName(foundationObject.getObjectGuid().getClassName());

					foundationObject.setLifecyclePhaseGuid(lifecyclePhaseInfo.getGuid());
				}
			}
			else
			{
				LifecyclePhaseInfo lifecyclePhaseInfo = emm.getFirstLifecyclePhaseInfoByClassName(foundationObject.getObjectGuid().getClassName());
				foundationObject.setLifecyclePhaseGuid(lifecyclePhaseInfo.getGuid());
			}
			ObjectGuid retObjectGuid = null;
			if (!isClearId)
			{
				// important! invoke wip.before event.
				this.stubService.getEOSS().executeReviseBeforeEvent(foundationObject);
			}

			if (foundationObject.get(SystemClassFieldEnum.ECFLAG.getName()) != null)
			{
				foundationObject.clear(SystemClassFieldEnum.ECFLAG.getName());
			}

			retFoundationObject = this.createObject(foundationObject, originalFoundationGuid, ownerGroupGuid, ownerUserGuid, Constants.isSupervisor(isCheckAuth, this.stubService),
					true, isClearId, isCheckOut, saveAsoriginalFoundationGuid, false);

			retObjectGuid = retFoundationObject.getObjectGuid();
			this.createReplaceData(null, retObjectGuid, globalReplace, isClearId);

			// key:bomViewNamw,value:(key:end2Masterguid,value:(key:sequence,value:取替代数据))
			Map<String, Map<String, Map<String, List<FoundationObject>>>> originalReplaceMap = new HashMap<String, Map<String, Map<String, List<FoundationObject>>>>();
			// 处理BOM关系
			if (!SetUtils.isNullList(bomViewObjectGuidList) && retObjectGuid != null)
			{
				Map<String, Object> specialField = new HashMap<>();
				for (ObjectGuid viewObjectGuid : bomViewObjectGuidList)
				{
					ClassStub.decorateObjectGuid(viewObjectGuid, this.stubService);
					String className = viewObjectGuid.getClassName();
					String viewGuid = viewObjectGuid.getGuid();
					if (className == null)
					{
						continue;
					}

					if (emm.getClassByName(className).hasInterface(ModelInterfaceEnum.IBOMView))
					{
						BOMView bomView = boms.getBOMView(viewObjectGuid);

						if (bomView == null)
						{
							continue;
						}
						BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(bomView.getTemplateID());

						// 无效模板不创建bomview
						if (bomTemplate != null && !bomTemplate.isValid())
						{
							continue;
						}

						if (isContainPartReplace)
						{
							this.getOriginalReplaceFoundation(bomView, bomRule, boms, originalReplaceMap, isClearId);
						}

						bomView.getObjectGuid().setGuid(null);
						bomView.getObjectGuid().setMasterGuid(null);
						bomView.setObjectGuid(bomView.getObjectGuid());
						bomView.setEnd1ObjectGuid(retObjectGuid);
						bomView.setId(retFoundationObject.getId());
						bomView.clear(SystemClassFieldEnum.ECFLAG.getName());
						bomView.clear(SystemClassFieldEnum.RELEASETIME.getName());
						bomView.setLifecyclePhaseGuid(null);// 把LifecyclePhaseGuid清空，由saveBOMRelation方法自己去获取初始生命周期阶段
						bomView.clear(SystemClassFieldEnum.STATUS.getName());
						bomView = ((BOMSImpl) boms).getBomViewStub().saveBOMView(bomView, false, null);

						bomView = ((BOMSImpl) this.stubService.getBOMS()).getBOMViewCheckOutStub().checkOut(bomView, this.stubService.getOperatorGuid(), false);
						if (isClearId)
						{
							// 另存
							specialField.put(BOMStructure.RSFLAG, "N");
							specialField.put(BOMStructure.BOMKEY, UUID.randomUUID().toString().replace("-", ""));
						}
						else
						{
							// 修订
						}

						DataServer.getRelationService().copyBomOrRelation(bomView.getObjectGuid().getClassGuid(), viewGuid, bomView.getObjectGuid().getGuid(),
								bomTemplate.getStructureClassGuid(), "3", specialField, sessionId, this.stubService.getFixedTransactionId());

						bomView = ((BOMSImpl) this.stubService.getBOMS()).getBOMViewCheckInStub().checkIn(bomView, false);

						// 取替代数据处理
						if (isContainPartReplace && isClearId) // 另存
						{
							this.createPartReplace(bomView, bomRule, boms, originalReplaceMap, isClearId);
						}
						else if (!isClearId) // 修订
						{
							reviseReplaceData(retFoundationObject, originalFoundationGuid, bomView);
						}

					}
				}
			}

			// 处理关系
			if (!SetUtils.isNullList(viewObjectList))
			{
				Map<String, Object> specialField = new HashMap<>();
				for (ViewObject viewObject : viewObjectList)
				{
					String viewGuid = viewObject.getObjectGuid().getGuid();
					RelationTemplateInfo relationTemplate = this.stubService.getEMM()
							.getRelationTemplateById(viewObject.get(ViewObject.TEMPLATE_ID) == null ? "" : (String) viewObject.get(ViewObject.TEMPLATE_ID));
					if (relationTemplate == null)
					{
						continue;
					}

					// 无效模板不创建view
					if (relationTemplate != null && !relationTemplate.isValid())
					{
						continue;
					}

					if (relationTemplate.getStructureModel() != RelationTemplateTypeEnum.FACTORY && relationTemplate.getStructureModel() != RelationTemplateTypeEnum.DETAIL
							&& relationTemplate.getStructureModel() != RelationTemplateTypeEnum.EXTENDED)
					{
						if (!isClearId && RelationTemplateActionEnum.NONE.equals(relationTemplate.getReviseTrigger()))
						{
							// 修订 不关联
							continue;
						}
						else if (isClearId && RelationTemplateActionEnum.NONE.equals(relationTemplate.getSaveAsTrigger()))
						{
							// saveas 不关联
							continue;
						}
						else if (RelationTemplateActionEnum.LINK.equals(relationTemplate.getReviseTrigger())
								|| RelationTemplateActionEnum.LINK.equals(relationTemplate.getSaveAsTrigger()))
						{
							// 直接关联
							viewObject.getObjectGuid().setGuid(null);
							viewObject.getObjectGuid().setMasterGuid(null);
							viewObject.setObjectGuid(viewObject.getObjectGuid());
							viewObject.setEnd1ObjectGuid(retObjectGuid);
							viewObject.setId(retFoundationObject.getId());

							viewObject = this.stubService.getRelationStub().saveRelation(viewObject, true, true, null);

							DataServer.getRelationService().copyBomOrRelation(viewObject.getObjectGuid().getClassGuid(), viewGuid, viewObject.getObjectGuid().getGuid(),
									relationTemplate.getStructureClassGuid(), "5", specialField, sessionId, this.stubService.getFixedTransactionId());

							// 另存时，更新订单明细的所属订单合同字段为新的订单合同
							if (ConfigParameterConstants.CONFIG_PARAMETER_ORDELDETAIL_TEMPLATE_NAME.equals(relationTemplate.getName()))
							{
								DataServer.getConfigManagerService().changeOwnerContractOfContent(foundationObject.getObjectGuid(), relationTemplate.getGuid(), sessionId);
							}
						}
					}
					else if ((relationTemplate.getStructureModel() == RelationTemplateTypeEnum.FACTORY || relationTemplate.getStructureModel() == RelationTemplateTypeEnum.DETAIL
							|| relationTemplate.getStructureModel() == RelationTemplateTypeEnum.EXTENDED)
							&& (!isClearId && relationTemplate.isReviseCopyRelation() || isClearId && relationTemplate.isSaveAsCopyRelation()))
					{
						// 复制并关联
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
						viewObject.getObjectGuid().setGuid(null);
						viewObject.getObjectGuid().setMasterGuid(null);
						viewObject.setObjectGuid(viewObject.getObjectGuid());
						viewObject.setEnd1ObjectGuid(retObjectGuid);

						viewObject = this.stubService.getRelationStub().saveRelation(viewObject, true, true, null);

						if (!SetUtils.isNullList(structureObjectList))
						{
							for (StructureObject structureObject : structureObjectList)
							{

								// 2013年12月11日变更，如果end2停用，不需要复制
								if (structureObject.getStatus() == SystemStatusEnum.OBSOLETE)
								{
									continue;
								}

								FoundationObject end2FoundationObject = null;
								if (relationTemplate.getStructureModel() == RelationTemplateTypeEnum.DETAIL)
								{
									String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
									end2FoundationObject = this.stubService.getFoundationStub().getObject(structureObject.getEnd2ObjectGuid(), bmGuid, UITypeEnum.LIST,
											Constants.isSupervisor(true, this.stubService));

								}
								else
								{
									end2FoundationObject = this.stubService.getObject(structureObject.getEnd2ObjectGuid());
								}

								// 另存时，更新订单明细的所属订单合同字段为新的订单合同
								if (ConfigParameterConstants.CONFIG_PARAMETER_ORDELDETAIL_TEMPLATE_NAME.equals(relationTemplate.getName()))
								{
									end2FoundationObject.put(ConfigParameterConstants.OWNER_CONTRACT, foundationObject.getObjectGuid().getGuid());
									end2FoundationObject.put(ConfigParameterConstants.OWNER_CONTRACT + "$CLASS", foundationObject.getObjectGuid().getClassGuid());
									end2FoundationObject.put(ConfigParameterConstants.OWNER_CONTRACT + "$MASTER", foundationObject.getObjectGuid().getMasterGuid());
								}

								// 复制end2之前把ID字段设置为end1的id+revisionid
								end2FoundationObject.setId(retFoundationObject.getId() + retFoundationObject.getRevisionId());
								end2FoundationObject.setRevisionId(this.stubService.getInitRevisionId(0));
								// 将end2复制 并关联
								end2FoundationObject = this.saveAsObject(end2FoundationObject, null, ownerGroupGuid, ownerUserGuid, true, true, true, false, isContainPartReplace,
										isContainGlobalReplce);

								structureObject.setViewObjectGuid(viewObject.getObjectGuid());
								structureObject.setGuid(null);
								structureObject.setEnd2ObjectGuid(end2FoundationObject.resetObjectGuid());

								this.stubService.link(viewObject.getObjectGuid(), end2FoundationObject.resetObjectGuid(), structureObject);

							}
						}

					}
				}
			}

			if (!isClearId)
			{
				// important! invoke wip.after event.
				this.stubService.getEOSS().executeReviseAfterEvent(retFoundationObject);
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
		}
		return retFoundationObject;
	}

	/**
	 * 新修订处理取替代数据
	 * 
	 * @param retFoundationObject
	 *            升版后新对象
	 * @param originalFoundationGuid
	 *            升版前的原对象GUID
	 * @param bomView
	 *            升版后新对象的bomView
	 */
	private void reviseReplaceData(FoundationObject retFoundationObject, String originalFoundationGuid, BOMView bomView) throws ServiceRequestException
	{

		ObjectGuid origObjectGuid = new ObjectGuid(retFoundationObject.getObjectGuid().getClassGuid(), null, originalFoundationGuid, null);
		List<FoundationObject> copyReplaceDataList = ((BRMImpl) this.stubService.getBRM()).getReplaceQueryStub().listReplaceDataInner(origObjectGuid, null, null,
				ReplaceRangeEnum.PART, null, bomView.getName(), null, true);
		List<String> bomUpdate = new ArrayList<String>(); // 记录取替代在修订的时候是否变更了
		if (!SetUtils.isNullList(copyReplaceDataList))
		{
			for (FoundationObject copyReplaceData : copyReplaceDataList)
			{
				ReplaceStatusEnum status = ReplaceSubstituteConstants.getReplaceDataStatus(copyReplaceData);
				if (status != ReplaceStatusEnum.EXPIRE)
				{
					FoundationObject replaceDataNextRevision = (FoundationObject) copyReplaceData.clone();
					replaceDataNextRevision.put(ReplaceSubstituteConstants.MasterItem, retFoundationObject.getObjectGuid().getGuid());
					this.stubService.saveAsObject(replaceDataNextRevision, false, false, false);
				}
				else
				{
					bomUpdate.add((String) copyReplaceData.get(ReplaceSubstituteConstants.BOMKey));
				}
				copyReplaceData.put(ReplaceSubstituteConstants.MasterItem, originalFoundationGuid);
				copyReplaceData.put(ReplaceSubstituteConstants.IN_USE, BooleanUtils.getBooleanStringYN(false));
				saveObject(copyReplaceData, true, true, false, null, true, true, false);
			}
		}
		if (SetUtils.isNullList(bomUpdate))
		{
			return;
		}

		List<BOMStructure> bomStructureList = this.stubService.getBOMS().listBOM(bomView.getObjectGuid(), null, null, null);
		if (SetUtils.isNullList(bomStructureList))
		{
			return;
		}

		// 取替代标记更新，失效的取替代不会带到修订后的物料上面，因此需要重新判断并且更新BOM上的取替代标记
		for (BOMStructure bomStructure : bomStructureList)
		{
			if (bomUpdate.contains(bomStructure.getBOMKey()))
			{
				List<FoundationObject> hasReplace = this.stubService.getBRM().listReplaceDataByRang(bomStructure.getEnd1ObjectGuid(), bomStructure.getEnd2ObjectGuid(),
						ReplaceRangeEnum.PART, null, bomView.getName(), bomStructure.getBOMKey(), true);
				if (SetUtils.isNullList(hasReplace))
				{
					bomStructure.setRsFlag(false);
					this.stubService.getBOMS().saveBOM(bomStructure);
				}
			}
		}
	}

	/**
	 * 创建局部取替代关系
	 */
	private void createPartReplace(BOMView bomView, DataRule dataRule, BOMS boms, Map<String, Map<String, Map<String, List<FoundationObject>>>> originalReplaceMap, boolean copy)
			throws ServiceRequestException
	{
		if (SetUtils.isNullMap(originalReplaceMap) || SetUtils.isNullMap(originalReplaceMap.get(bomView.getName())))
		{
			return;
		}
		List<BOMStructure> bomStructureList = this.listBOMStructurte(bomView, boms, dataRule);
		if (SetUtils.isNullList(bomStructureList))
		{
			return;
		}

		Map<String, Map<String, List<FoundationObject>>> firstValueMap = originalReplaceMap.get(bomView.getName());
		for (BOMStructure bomStructure : bomStructureList)
		{
			ObjectGuid end1ObjectGuid = bomStructure.getEnd1ObjectGuid();
			ObjectGuid end2ObjectGuid = bomStructure.getEnd2ObjectGuid();
			if (end1ObjectGuid != null && StringUtils.isGuid(end1ObjectGuid.getMasterGuid()) && end2ObjectGuid != null && StringUtils.isGuid(end2ObjectGuid.getMasterGuid())
					&& !SetUtils.isNullMap(firstValueMap.get(end2ObjectGuid.getMasterGuid())))
			{
				Map<String, List<FoundationObject>> secondValueMap = firstValueMap.get(end2ObjectGuid.getMasterGuid());
				if (SetUtils.isNullList(secondValueMap.get(bomStructure.getSequence())))
				{
					continue;
				}
				List<FoundationObject> origReplaceDataList = secondValueMap.get(bomStructure.getSequence());
				for (FoundationObject origReplaceData : origReplaceDataList)
				{
					if (copy)
					{
						origReplaceData.put(ReplaceSubstituteConstants.BOMKey, bomStructure.getBOMKey());
					}
				}
				this.createReplaceData(end1ObjectGuid, end2ObjectGuid, origReplaceDataList, copy);
				bomStructure.setRsFlag(true);
				boms.saveBOM(bomStructure);
			}
		}
	}

	/**
	 * 创建取替代数据
	 */
	private void createReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, List<FoundationObject> listFoundation, boolean copy)
			throws ServiceRequestException
	{
		if (!SetUtils.isNullList(listFoundation))
		{
			for (FoundationObject fo : listFoundation)
			{
				ObjectGuid rsItemObjectGuid = this.getItemObjectGuid(fo, ReplaceSubstituteConstants.RSItem);
				((BRMImpl) this.stubService.getBRM()).getReplaceObjectStub().createReplaceData(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, null, fo, copy);
			}
		}
	}

	/**
	 * 取得替代件的ObjectGuid
	 * 
	 * @param fo
	 * @return
	 */
	private ObjectGuid getItemObjectGuid(FoundationObject foundationObject, String sid)
	{
		if (!StringUtils.isNullString((String) foundationObject.get((sid + ReplaceSubstituteConstants.MASTER).toUpperCase())))
		{
			ObjectGuid objectItem = new ObjectGuid((String) foundationObject.get((sid + ReplaceSubstituteConstants.ClassGuid).toUpperCase()),
					(String) foundationObject.get((sid + ReplaceSubstituteConstants.CLASSNAME).toUpperCase()), (String) foundationObject.get((sid).toUpperCase()),
					(String) foundationObject.get((sid + ReplaceSubstituteConstants.MASTER).toUpperCase()), null);
			objectItem.setIsMaster(true);
			return objectItem;
		}
		return null;
	}

	/**
	 * 
	 * @param bomView
	 * @param bomRule
	 * @param boms
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<BOMStructure> listBOMStructurte(BOMView bomView, BOMS boms, DataRule dataRule) throws ServiceRequestException
	{
		String structureClassName = bomView.getStructureClassName();
		SearchCondition searchCondition = null;
		List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(structureClassName);
		searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(structureClassName, uiObjectList);
		return boms.listBOM(bomView.getObjectGuid(), searchCondition, null, dataRule);
	}

	/**
	 * 取得原始的取替代关系(局部)
	 */
	private void getOriginalReplaceFoundation(BOMView bomView, DataRule dataRule, BOMS boms, Map<String, Map<String, Map<String, List<FoundationObject>>>> originalReplaceMap,
			boolean copy) throws ServiceRequestException
	{
		List<BOMStructure> bomStructureList = this.listBOMStructurte(bomView, boms, dataRule);
		if (SetUtils.isNullList(bomStructureList))
		{
			return;
		}
		for (BOMStructure bomStructure : bomStructureList)
		{
			List<FoundationObject> replaceDataList;
			replaceDataList = this.stubService.getBRM().listReplaceDataByRang(bomStructure.getEnd1ObjectGuid(), bomStructure.getEnd2ObjectGuid(), ReplaceRangeEnum.PART, null,
					bomView.getName(), bomStructure.getBOMKey(), false);
			if (SetUtils.isNullList(replaceDataList))
			{
				continue;
			}
			// originalReplaceMap的value
			Map<String, Map<String, List<FoundationObject>>> firstValueMap = originalReplaceMap.get(bomView.getName());
			// originalReplaceMap的value的value
			Map<String, List<FoundationObject>> secondValueMap;
			if (SetUtils.isNullMap(firstValueMap))
			{
				firstValueMap = new HashMap<String, Map<String, List<FoundationObject>>>();
				secondValueMap = new HashMap<String, List<FoundationObject>>();
				secondValueMap.put(bomStructure.getSequence(), replaceDataList);
				firstValueMap.put(bomStructure.getEnd2ObjectGuid().getMasterGuid(), secondValueMap);
				originalReplaceMap.put(bomView.getName(), firstValueMap);
			}
			else
			{
				secondValueMap = firstValueMap.get(bomStructure.getEnd2ObjectGuid().getMasterGuid());
				if (SetUtils.isNullMap(secondValueMap))
				{
					secondValueMap = new HashMap<String, List<FoundationObject>>();
					secondValueMap.put(bomStructure.getSequence(), replaceDataList);
					firstValueMap.put(bomStructure.getEnd2ObjectGuid().getMasterGuid(), secondValueMap);
				}
				else
				{
					secondValueMap.put(bomStructure.getSequence(), replaceDataList);
				}
			}
		}
	}

	/**
	 * 携带所有关联关系 用相同信息创建/新建修订版
	 * 
	 * @param foundationObject
	 * @param isClearId
	 *            为true是用相同信息创建 false时是创建新版本
	 * @param isRevisionIdAddOne
	 *            true 版本号加1 false 版本号不
	 * @param isContainBom
	 *            是否携带BOMView
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveAsObject(FoundationObject foundationObject, String ownerGroupGuid, String ownerUserGuid, boolean isClearId, boolean isRevisionIdAddOne,
			boolean isCheckAuth, boolean isContainBom, boolean isContainPartReplace, boolean isContainGlobalReplce, boolean isCheckOut) throws ServiceRequestException
	{

		List<ObjectGuid> objectGuidList = null;

		BOMS boms = this.stubService.getBOMS();

		if (isContainBom)
		{
			// 查找所有关联的BOMView
			List<BOMView> bomViewList = boms.listBOMView(foundationObject.getObjectGuid());

			if (!SetUtils.isNullList(bomViewList))
			{
				if (SetUtils.isNullList(objectGuidList))
				{
					objectGuidList = new ArrayList<>();
				}
				for (BOMView bomView : bomViewList)
				{
					objectGuidList.add(bomView.getObjectGuid());
				}
			}
		}
		else
		{
			// 变更0724 4. 当复制另存物料时,如时不选择复制BOM, UHasBOM字段的值置为”false”;
			CodeItemInfo info = this.stubService.getEMM().getCodeItemByName("UHasBOM", "N");
			if (info != null)
			{
				foundationObject.put(Constants.FIELD_UHASBOM, info.getGuid());
			}
			else
			{
				foundationObject.put(Constants.FIELD_UHASBOM, null);
			}

		}

		FoundationObject retFoundationObject = this.saveAsObject(foundationObject, objectGuidList, ownerGroupGuid, ownerUserGuid, isClearId, isRevisionIdAddOne, isCheckAuth,
				isCheckOut, isContainPartReplace, isContainGlobalReplce);

		return retFoundationObject;
	}

	/**
	 * 更新FoundationObject对象
	 * 
	 * @param foundationObject
	 *            要更新的FoundationObject对象
	 * @param boas
	 *            BOASImpl
	 * @param hasReturn
	 *            true 有返回值 ,false 返回null
	 * @return 更新后的FoundationObject对象
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth, String procRtGuid) throws ServiceRequestException
	{
		return this.saveObject(foundationObject, hasReturn, isCheckAuth, true, procRtGuid);
	}

	public FoundationObject saveObject(FoundationObject foundationObject, RelationTemplateTypeEnum structureModel, boolean isCheckAuth, String procRtGuid)
			throws ServiceRequestException
	{
		// ObjectGuid viewObjectGuid = structureObject.getViewObjectGuid();
		// ViewObject viewObject = this.stubService.getRelationStub().getRelation(viewObjectGuid, false);
		// RelationTemplateTypeEnum structureModel = viewObject.getStructureModel();
		boolean needCheckOut = true;
		if (structureModel == RelationTemplateTypeEnum.DETAIL || structureModel == RelationTemplateTypeEnum.EXTENDED || structureModel == RelationTemplateTypeEnum.FACTORY)
		{
			needCheckOut = false;
		}
		return this.saveObject(foundationObject, true, isCheckAuth, needCheckOut, procRtGuid);
	}

	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth, boolean needCheckOut, String procRtGuid)
			throws ServiceRequestException
	{
		return this.saveObject(foundationObject, hasReturn, isCheckAuth, needCheckOut, procRtGuid, true, true);
	}

	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth, boolean needCheckOut, String procRtGuid, boolean isRunScript,
			boolean isCheckStatus) throws ServiceRequestException
	{
		return this.saveObject(foundationObject, hasReturn, isCheckAuth, needCheckOut, procRtGuid, isRunScript, isCheckStatus, true);
	}

	protected void checkClassificationExist(FoundationObject foundationObject, boolean isCreate) throws ServiceRequestException
	{
		ObjectGuid objectGuid = foundationObject.getObjectGuid();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		List<FoundationObject> restoreAllClassification = foundationObject.restoreAllClassification(false);
		// 补上主classification foundation
		if (!StringUtils.isNullString(foundationObject.getClassificationGuid()))
		{
			CodeItemInfo classification = this.stubService.getEMM().getCodeItem(foundationObject.getClassificationGuid());

			FoundationObject masterFou = null;
			boolean hasMasterClassificaiton = false;

			if (classification != null && !SetUtils.isNullList(restoreAllClassification))
			{
				for (FoundationObject classificationFoundationObject : restoreAllClassification)
				{
					if (classification.getCodeGuid().equalsIgnoreCase(classificationFoundationObject.getClassificationGroup()))
					{
						masterFou = classificationFoundationObject;
					}

					if (foundationObject.getClassificationGuid().equalsIgnoreCase(classificationFoundationObject.getClassificationGuid()))
					{
						hasMasterClassificaiton = true;
						break;
					}
				}
			}

			if (!hasMasterClassificaiton && isCreate)
			{
				// String oldGuid = null;
				if (masterFou != null)
				{
					// oldGuid = masterFou.getObjectGuid().getGuid();
					foundationObject.clearClasssification(masterFou.getClassificationGroupName());
				}

				FoundationObject newFoundationObject = this.stubService.newClassificationFoundation(foundationObject.getClassificationGuid(), masterFou);
				if (newFoundationObject != null)
				{
					foundationObject.addClassification(newFoundationObject, false);
				}
			}
		}
		else
		{

			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());

			// 去除不存在的主分类foundation
			FoundationObject removeFoundation = null;
			if (!SetUtils.isNullList(restoreAllClassification))
			{
				for (FoundationObject classificationFoundationObject : restoreAllClassification)
				{
					if (!StringUtils.isNullString(classInfo.getClassification())
							&& classInfo.getClassification().equalsIgnoreCase(classificationFoundationObject.getClassificationGroupName()))
					{
						removeFoundation = classificationFoundationObject;
						break;
					}
				}
			}

			if (removeFoundation != null)
			{
				foundationObject.clearClasssification(removeFoundation.getClassificationGroupName());
			}
		}

		// 1. 检查分类映射与foundation中的分类foundation是否一致，去除分类映射中不存在的分类foundation
		restoreAllClassification = foundationObject.restoreAllClassification(false);
		List<String> mappingExistClassification = new ArrayList<String>();
		if (!SetUtils.isNullList(restoreAllClassification))
		{
			List<ClassficationFeature> listClassficationFeature = this.stubService.getEMM().listClassficationFeature(objectGuid.getClassGuid());
			if (!SetUtils.isNullList(listClassficationFeature))
			{
				for (ClassficationFeature feature : listClassficationFeature)
				{
					mappingExistClassification.add(feature.getClassificationfk());
				}
			}

			List<String> remove = new ArrayList<String>();
			for (FoundationObject foundation : restoreAllClassification)
			{
				if (!mappingExistClassification.contains(foundation.getClassificationGroup()))
				{
					remove.add(foundation.getClassificationGroupName());
					continue;
				}

				if (!StringUtils.isNullString(foundation.getClassificationGuid()))
				{
					CodeItemInfo classification = this.stubService.getEMM().getCodeItem(foundation.getClassificationGuid());
					if (classification == null)
					{
						remove.add(foundation.getClassificationGroupName());
					}
				}
			}

			for (String classification : remove)
			{
				foundationObject.clearClasssification(classification);
			}
		}

		// // 2. 检查主分类字段中值是否与类中设置的分类一致
		// ClassInfo classInfo =
		// this.stubService.getEMM().getClassByGuid(foundationObject.getObjectGuid().getClassGuid());
		// if (!StringUtils.isNullString(foundationObject.getClassificationGuid()))
		// {
		// boolean isNeedRemove = false;
		// CodeItemInfo classification = this.stubService.getEMM().getClassification(
		// foundationObject.getClassificationGuid());
		// if (classification == null)
		// {
		// // 分类不存在时，去除分类值
		// isNeedRemove = true;
		// }
		// else
		// {
		// CodeObjectInfo code = this.stubService.getEMM().getCode(classification.getCodeGuid());
		// if (code != null && code.getName() != null
		// && code.getName().equalsIgnoreCase(classInfo.getClassification()))
		// {
		//
		// }
		// else
		// {
		// // 分类不一致，去除分类值
		// isNeedRemove = true;
		// }
		// }
		//
		// if (isNeedRemove)
		// {
		// // 分类不存在时，去除分类值
		// foundationObject.setClassificationGuid(null);
		// foundationObject.setClassificationName(null);
		// foundationObject.setClassification(null);
		// }
		// }
		//
		// // 3. 实例中分类字段与分类foundation中的主分类是否一致
		// // 存在错误的主分类foundation时，删除且补上，不存在时，直接补上
		// if (!StringUtils.isNullString(foundationObject.getClassificationGuid()))
		// {
		// CodeItemInfo classification = this.stubService.getEMM().getClassification(
		// foundationObject.getClassificationGuid());
		// FoundationObject masterFou = null;
		// boolean hasMasterClassificaiton = false;
		// restoreAllClassification = foundationObject.restoreAllClassification(false);
		// if (!SetUtils.isNullList(restoreAllClassification))
		// {
		// for (FoundationObject classificationFoundationObject : restoreAllClassification)
		// {
		// if (classification.getCodeGuid().equalsIgnoreCase(
		// classificationFoundationObject.getClassificationGroup()))
		// {
		// masterFou = classificationFoundationObject;
		// }
		//
		// if (foundationObject.getClassificationGuid().equalsIgnoreCase(
		// classificationFoundationObject.getClassificationGuid()))
		// {
		// hasMasterClassificaiton = true;
		// break;
		// }
		// }
		// }
		//
		// if (!hasMasterClassificaiton)
		// {
		// String oldGuid = null;
		// if (masterFou != null)
		// {
		// oldGuid = masterFou.getObjectGuid().getGuid();
		// foundationObject.clearClasssification(masterFou.getClassificationGroupName());
		// }
		//
		// FoundationObject newFoundationObject = this.stubService.newClassificationFoundation(
		// foundationObject.getClassificationGuid(), oldGuid);
		// foundationObject.addClassification(newFoundationObject, false);
		// }
		// }
	}

	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth, boolean needCheckOut, String procRtGuid, boolean isRunScript,
			boolean isCheckStatus, boolean isUpdateTime) throws ServiceRequestException
	{
		return this.saveObject(foundationObject, hasReturn, isCheckAuth, needCheckOut, true, procRtGuid, isRunScript, isCheckStatus, isUpdateTime, true);
	}

	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth, boolean needCheckOut, String procRtGuid, boolean isRunScript,
			boolean isCheckStatus, boolean isUpdateTime, boolean isFieldMerge) throws ServiceRequestException
	{
		return this.saveObject(foundationObject, hasReturn, isCheckAuth, needCheckOut, true, procRtGuid, isRunScript, isCheckStatus, isUpdateTime, isFieldMerge);
	}

	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth, boolean needCheckOut, boolean isCheckMandatory, String procRtGuid,
			boolean isRunScript, boolean isCheckStatus, boolean isUpdateTime, boolean isFieldMerge) throws ServiceRequestException
	{
		FoundationObject retObject = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			if (isCheckStatus)
			{
				// 流程中预发布状态对象可修改
				if (!StringUtils.isGuid(procRtGuid))
				{
					if (foundationObject.getStatus() != null
							&& !(SystemStatusEnum.WIP.equals(foundationObject.getStatus()) || SystemStatusEnum.ECP.equals(foundationObject.getStatus())))
					{
						throw new ServiceRequestException("ID_APP_STATUS_CANNT_EDIT", "only wip status can edit");
					}
				}
				else
				{
					if (foundationObject.getStatus() != null && !(SystemStatusEnum.WIP.equals(foundationObject.getStatus())
							|| SystemStatusEnum.ECP.equals(foundationObject.getStatus()) || SystemStatusEnum.PRE.equals(foundationObject.getStatus())))
					{
						throw new ServiceRequestException("ID_APP_STATUS_CANNT_EDIT", "only wip status can edit");
					}
				}
			}

			this.checkClassificationExist(foundationObject, false);
			this.checkFoundationFieldExist(foundationObject);
			String allocate = null;
			String message = "";
			if (isFieldMerge)
			{
				NumberAllocate na = new NumberAllocate();
				allocate = na.allocate(foundationObject, this.stubService, false);
			}

			if (isCheckMandatory)
			{
				String checkMandatoryField = this.checkDynaObjectField(foundationObject);

				if (!StringUtils.isNullString(allocate))
				{
					message = message + allocate;
				}

				if (!StringUtils.isNullString(checkMandatoryField))
				{
					message = message + checkMandatoryField;
				}

				if (!StringUtils.isNullString(message))
				{
					throw new ServiceRequestException(message);
				}
			}

			this.checkFoundationFieldRegex(foundationObject);

			if (isRunScript)
			{
				// invoke update.before event
				this.stubService.getEOSS().executeUpdateBeforeEvent(foundationObject);
			}

			// DCR规则检查
			this.stubService.getDCR().check(foundationObject);

			if (isUpdateTime)
			{
				retObject = DataServer.getInstanceService().save(foundationObject, Constants.isSupervisor(isCheckAuth, this.stubService),
						this.stubService.getSignature().getCredential(), this.stubService.getFixedTransactionId(), StringUtils.isGuid(procRtGuid) ? false : needCheckOut);
			}
			else
			{
				retObject = DataServer.getInstanceService().saveForIgnoreUpdateTime(foundationObject, Constants.isSupervisor(isCheckAuth, this.stubService),
						this.stubService.getSignature().getCredential(), this.stubService.getFixedTransactionId());
			}

			this.decorate(retObject);

			if (isRunScript)
			{
				// invoke update.after event
				this.stubService.getEOSS().executeUpdateAfterEvent(retObject);
			}

//			DataServer.getTransactionManager().commitTransaction();
			return retObject;

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

	private void decorate(FoundationObject foundationObject) throws DecorateException, ServiceRequestException
	{
		String className = foundationObject.getObjectGuid().getClassName();
		if (StringUtils.isNullString(className))
		{
			className = this.stubService.getEMM().getClassByGuid(foundationObject.getObjectGuid().getClassGuid()).getName();
		}
		List<UIObjectInfo> uiList = ((EMMImpl) this.stubService.getEMM()).listUIObjectInCurrentBizModel(className, UITypeEnum.FORM, true);
		if (SetUtils.isNullList(uiList))
		{
			return;
		}

		Set<String> fieldNames = new HashSet<String>();
		Set<String> fieldCodeNames = new HashSet<String>();
		Set<String> fieldFolderNames = new HashSet<String>();

		UIObjectInfo uiObject = uiList.get(0);
		List<UIField> uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
		if (!SetUtils.isNullList(uiFieldList))
		{
			for (UIField uiField : uiFieldList)
			{
				if (uiField.getType() == FieldTypeEnum.OBJECT)
				{
					fieldNames.add(uiField.getName());
				}
				else if (uiField.getType() == FieldTypeEnum.CODE || //
						uiField.getType() == FieldTypeEnum.CLASSIFICATION || //
						uiField.getType() == FieldTypeEnum.MULTICODE || //
						uiField.getType() == FieldTypeEnum.CODEREF)
				{
					fieldCodeNames.add(uiField.getName());
				}
				else if (uiField.getType() == FieldTypeEnum.FOLDER)
				{
					fieldFolderNames.add(uiField.getName());
				}
			}
		}

		EMM emm = this.stubService.getEMM();

		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

		DecoratorFactory.decorateFoundationObject(fieldNames, foundationObject, emm, bmGuid, null);
		DecoratorFactory.decorateFoundationObjectCode(fieldCodeNames, foundationObject, emm, bmGuid);
		String classificationGuid = foundationObject.getClassification();
		if (!StringUtils.isNullString(classificationGuid))
		{
			List<FoundationObject> restoreAllClassification = foundationObject.restoreAllClassification(true);
			if (!SetUtils.isNullList(restoreAllClassification))
			{
				for (FoundationObject classificationFoundation : restoreAllClassification)
				{
					this.stubService.getClassificationStub().decorateClassification(classificationFoundation, bmGuid, classificationFoundation.getClassificationGuid());
				}
			}
		}

		String sessionId = this.stubService.getSignature().getCredential();
		fieldNames.addAll(fieldFolderNames);
		DecoratorFactory.ofd.decorateWithField(fieldNames, foundationObject, this.stubService.getEMM(), sessionId, false);
	}

	protected FoundationObject createDocumentByTemplate(FoundationObject docFoundationObject, ObjectGuid tmpObjectGuid, boolean isCheckOut) throws ServiceRequestException
	{
		FoundationObject retFoundationObject = null;

		try
		{
			FoundationObject tmpFoundationObject = this.stubService.getObject(tmpObjectGuid);

			if (tmpFoundationObject == null)
			{
				return null;
			}

			retFoundationObject = this.createObject(docFoundationObject, null, true, isCheckOut);

			// 打开取替代对象时，不记录历史
			BIViewHis biViewHis = new BIViewHis();
			biViewHis.setInstanceGuid(retFoundationObject.getObjectGuid().getGuid());
			biViewHis.setInstanceClassGuid(retFoundationObject.getObjectGuid().getClassGuid());
			biViewHis.setInstanceBOGuid(retFoundationObject.getObjectGuid().getBizObjectGuid());
			biViewHis.put(BIViewHis.CREATE_USER, this.stubService.getOperatorGuid());

			// bug fixed by wanglei 将复制文件操作提到事务之外, 修复文件未上传异常
			((DSSImpl) this.stubService.getDSS()).getFileInfoStub().copyFile(retFoundationObject.getObjectGuid(), tmpFoundationObject.getObjectGuid(), false);

			// 检出
			if (isCheckOut)
			{
				retFoundationObject = this.stubService.getObject(retFoundationObject.getObjectGuid());
				try
				{
					if (!retFoundationObject.isCheckOut() && retFoundationObject.isCommited())
					{
						retFoundationObject = this.stubService.checkOut(retFoundationObject);
					}
				}
				catch (ServiceRequestException e)
				{
					retFoundationObject.put(CHECKOUT_ERROR_ID, "ID_APP_CREATE_CHECKOUT_FAIL");
				}
			}
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
		return retFoundationObject;
	}

	protected List<DynaObject> createDocumentByFile(FoundationObject docFoundationObject, List<DSSFileInfo> fileInfoList, boolean isCheckOut) throws ServiceRequestException
	{
		if (StringUtils.isNullString(docFoundationObject.getName()))
		{
			if (!SetUtils.isNullList(fileInfoList))
			{
				String name = fileInfoList.get(0).getName();
				int idx = name.lastIndexOf('.');
				if (idx != -1)
				{
					name = name.substring(0, idx);
				}
				docFoundationObject.setName(name);
			}
		}

		List<DynaObject> retList = new ArrayList<DynaObject>();

		try
		{

			FoundationObject newObject = this.createObject(docFoundationObject, null, true, isCheckOut);

			// 打开取替代对象时，不记录历史
			BIViewHis biViewHis = new BIViewHis();
			biViewHis.setInstanceGuid(newObject.getObjectGuid().getGuid());
			biViewHis.setInstanceClassGuid(newObject.getObjectGuid().getClassGuid());
			biViewHis.setInstanceBOGuid(newObject.getObjectGuid().getBizObjectGuid());
			biViewHis.put(BIViewHis.CREATE_USER, this.stubService.getOperatorGuid());

			if (!SetUtils.isNullList(fileInfoList))
			{
				ObjectGuid objectGuid = newObject.getObjectGuid();
				DSSImpl dss = (DSSImpl) this.stubService.getDSS();
				InstFileStub fileStub = dss.getInstFileStub();
				DSSFileInfo newFileInfo = null;
				String filePath = null;
				for (DSSFileInfo fileInfo : fileInfoList)
				{
					filePath = fileInfo.getFilePath();

					try
					{
//						DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
						newFileInfo = fileStub.attachFile(objectGuid, fileInfo, true, false);
						DSSFileTrans fileTrans = dss.uploadFile(newFileInfo.getGuid(), filePath);
//						DataServer.getTransactionManager().commitTransaction();
						retList.add(fileTrans);
					}
					catch (Exception e)
					{
//						DataServer.getTransactionManager().rollbackTransaction();
						throw e;
					}
				}
			}

			retList.add(0, newObject);
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
		return retList;
	}

	protected void setIsExportToERP(ObjectGuid objectGuid) throws ServiceRequestException
	{
		try
		{
			DataServer.getInstanceService().setIsExportToERP(objectGuid.getGuid(), objectGuid.getClassName(), true, Constants.isSupervisor(true, this.stubService),
					this.stubService.getSignature().getCredential());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public FoundationObject createObject(FoundationObject foundationObject, String originalFoundationGuid, String ownerGroupGuid, String ownerUserGuid, boolean isCheckAuth,
			boolean isNeededEffective, boolean execCreateScript, boolean isCheckOut, String saveAsOriginalFoundationGuid, boolean isClearStatus) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();

		FoundationObject retObject = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			foundationObject.setUnique(null);
			this.checkClassificationExist(foundationObject, true);
			this.checkFoundationFieldExist(foundationObject);
			NumberAllocate na = new NumberAllocate();
			String message = "";
			String allocate = null;
			if (!execCreateScript)
			{
				// 修订
				allocate = na.allocate(foundationObject, this.stubService, false);
			}
			else
			{
				allocate = na.allocate(foundationObject, this.stubService, true);
			}

			String masterGuid = foundationObject.getObjectGuid().getMasterGuid();
			boolean isCreateMaster = StringUtils.isNullString(masterGuid) ? true : false;
			if (isCreateMaster)
			{
				String checkMandatoryField = this.checkDynaObjectField(foundationObject);
				if (!StringUtils.isNullString(allocate))
				{
					message = message + allocate;
				}

				if (!StringUtils.isNullString(checkMandatoryField))
				{
					message = message + checkMandatoryField;
				}

				if (!StringUtils.isNullString(message))
				{
					throw new ServiceRequestException(message);
				}

				Number startRevisionIdSequence = (Number) foundationObject.get(SystemClassFieldEnum.CUSTSTARTREVIDSEQUENCE.getName());
				foundationObject.setRevisionId(this.stubService.getFRevisionStub().getInitRevisionId(startRevisionIdSequence == null ? 0 : startRevisionIdSequence.intValue()));
			}
			this.checkFoundationFieldRegex(foundationObject);
			if (isClearStatus)
			{
				foundationObject.setStatus(SystemStatusEnum.WIP);
				foundationObject.setLifecyclePhaseGuid(null);
			}

			// 数据新建时，清除废弃时间和废弃人
			foundationObject.clear(SystemClassFieldEnum.OBSOLETETIME.getName());
			foundationObject.clear(SystemClassFieldEnum.OBSOLETEUSER.getName());
			foundationObject.clear(SystemClassFieldEnum.ISCHECKOUT.getName());
			foundationObject.clear(SystemClassFieldEnum.CHECKOUTTIME.getName());
			foundationObject.clear(SystemClassFieldEnum.CHECKOUTUSER.getName());
			foundationObject.clear(SystemClassFieldEnum.RELEASETIME.getName());
			EMM emm = this.stubService.getEMM();

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(foundationObject.getObjectGuid(), this.stubService);

			ClassInfo classInfo = emm.getClassByGuid(foundationObject.getObjectGuid().getClassGuid());
			boolean isStorage = classInfo.hasInterface(ModelInterfaceEnum.IStorage);
			boolean isShowPreview = classInfo.isShowPreview();
			String copyFileGuid = null;
			boolean copyFile = (isStorage || isShowPreview) && !StringUtils.isNullString(originalFoundationGuid);
			if (copyFile)
			{
				copyFileGuid = originalFoundationGuid;
			}
			else
			{
				copyFile = classInfo.hasInterface(ModelInterfaceEnum.IStorage) && !StringUtils.isNullString(saveAsOriginalFoundationGuid);
				if (copyFile)
				{
					copyFileGuid = saveAsOriginalFoundationGuid;
				}
			}

			if (classInfo != null)
			{
				if (classInfo.isAbstract())
				{
					throw new ServiceRequestException("ID_APP_ABSTRACT_CLASS", "absract class couldn't creat object");
				}
			}

			if (!StringUtils.isGuid(foundationObject.getOwnerUserGuid()))
			{
				foundationObject.setOwnerUserGuid(ownerUserGuid);
			}

			if (!StringUtils.isGuid(foundationObject.getOwnerGroupGuid()))
			{
				foundationObject.setOwnerGroupGuid(ownerGroupGuid);
			}

			LifecyclePhaseInfo lifecyclePhaseInfo = null;
			if (!StringUtils.isGuid(foundationObject.getLifecyclePhaseGuid()))
			{
				lifecyclePhaseInfo = emm.getFirstLifecyclePhaseInfoByClassName(foundationObject.getObjectGuid().getClassName());
			}
			else
			{
				lifecyclePhaseInfo = emm.getLifecyclePhaseInfo(foundationObject.getLifecyclePhaseGuid());
			}

			foundationObject.setLifecyclePhaseGuid(lifecyclePhaseInfo.getGuid());

			if (execCreateScript)
			{
				// important! invoke add.before event.
				this.stubService.getEOSS().executeAddBeforeEvent(foundationObject);
			}

			// DCR规则检查
			this.stubService.getDCR().check(foundationObject);

			retObject = DataServer.getInstanceService().create(foundationObject, originalFoundationGuid, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId,
					this.stubService.getFixedTransactionId());
			retObject.resetObjectGuid();

			// 添加文件复制功能
			if (copyFile)
			{
				FoundationObject originalObject = this.stubService.getFoundationStub().getObjectByGuid(new ObjectGuid(classInfo.getGuid(), null, copyFileGuid, null), false);
				if (originalObject != null)
				{
					try
					{
						((DSSImpl) this.stubService.getDSS()).getFileInfoStub().copyFile(retObject, originalObject, false, true);
					}
					catch (Throwable e)
					{
						DynaLogger.error(e);
					}
				}

			}

			if (execCreateScript)
			{
				// invoke add.after event.
				this.stubService.getEOSS().executeAddAfterEvent(retObject);
			}

			List<ClassField> classFieldList = this.stubService.getEMM().listFieldOfClass(classInfo.getName());
			Set<String> codeFieldSet = new HashSet<String>();
			Set<String> objectFieldSet = new HashSet<String>();
			Set<String> folderFieldSet = new HashSet<String>();
			if (!SetUtils.isNullList(classFieldList))
			{
				for (ClassField field : classFieldList)
				{
					if (field.getType() == FieldTypeEnum.CODE || //
							field.getType() == FieldTypeEnum.CLASSIFICATION || //
							field.getType() == FieldTypeEnum.MULTICODE || //
							field.getType() == FieldTypeEnum.CODEREF)
					{
						codeFieldSet.add(field.getName());
					}
					else if (field.getType() == FieldTypeEnum.OBJECT)
					{
						objectFieldSet.add(field.getName());
					}
					else if (field.getType() == FieldTypeEnum.FOLDER)
					{
						folderFieldSet.add(field.getName());
					}
				}
			}

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			DecoratorFactory.decorateFoundationObject(objectFieldSet, retObject, emm, bmGuid, null);
			DecoratorFactory.decorateFoundationObjectCode(codeFieldSet, retObject, emm, bmGuid);

			objectFieldSet.addAll(folderFieldSet);
			DecoratorFactory.ofd.decorateWithField(objectFieldSet, retObject, this.stubService.getEMM(), sessionId, false);

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

		return retObject;

	}

	public String checkDynaObjectField(DynaObject dynaObject) throws ServiceRequestException
	{
		StringBuffer result = new StringBuffer();
		LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

		Map<String, ClassField> classFieldMap = new HashMap<String, ClassField>();
		List<String> mandatoryFieldList = new ArrayList<String>();
		ClassStub.decorateObjectGuid(dynaObject.getObjectGuid(), this.stubService);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(dynaObject.getObjectGuid().getClassName());
		Map<String, String> fieldTitleMap = this.getFieldTitleMap(dynaObject.getObjectGuid().getClassName(), languageEnum);
		if (!SetUtils.isNullList(listFieldOfClass))
		{
			for (ClassField classField : listFieldOfClass)
			{
				if (classField.isMandatory())
				{
					mandatoryFieldList.add(classField.getName());
				}
				classFieldMap.put(classField.getName(), classField);
			}
		}

		List<SystemClassFieldEnum> noCheckMandatoryList = SystemClassFieldEnum.getNoCheckMandatoryClassFieldList();
		for (SystemClassFieldEnum fieldEnum : noCheckMandatoryList)
		{
			mandatoryFieldList.remove(fieldEnum.getName());
		}

		ObjectGuid objectGuid = dynaObject.getObjectGuid();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		List<String> hasCheckFieldList = new ArrayList<String>();
		if (!SetUtils.isNullList(mandatoryFieldList))
		{
			String messageMandatoryText = this.stubService.getMSRM().getMSRString("ID_APP_FOUNDATIONFIELD_MANDATORY_CHECK", languageEnum.toString());
			if (dynaObject instanceof StructureObject)
			{
				if (StringUtils.isNullString(messageMandatoryText))
				{
					messageMandatoryText = "ID_APP_FOUNDATIONFIELD_MANDATORY_CHECK";
				}
			}
			String messageLengthText = this.stubService.getMSRM().getMSRString("ID_APP_FOUNDATIONFIELD_LENGTH_CHECK", languageEnum.toString());
			if (StringUtils.isNullString(messageLengthText))
			{
				messageLengthText = "ID_APP_FOUNDATIONFIELD_LENGTH_CHECK";
			}
			for (String fieldname : mandatoryFieldList)
			{
				if (hasCheckFieldList.contains(fieldname.toUpperCase()))
				{
					continue;
				}
				hasCheckFieldList.add(fieldname.toUpperCase());
				String title = fieldTitleMap.get(fieldname) == null ? fieldname : fieldTitleMap.get(fieldname);
				String rt = this.checkFieldMandatory(dynaObject, dynaObject, mandatoryFieldList, title, fieldname, languageEnum, messageMandatoryText);
				if (!StringUtils.isNullString(rt))
				{
					result.append(rt);
				}

				rt = this.checkFieldSize(dynaObject, classFieldMap, title, fieldname, languageEnum, messageLengthText);

				if (!StringUtils.isNullString(rt))
				{
					result.append(rt);
				}
			}
		}
		if (this.stubService.getLIC().hasLicence(ApplicationTypeEnum.CLS.name()))
		{
			if (dynaObject instanceof StructureObject)
			{
				return result.toString();
			}

			List<FoundationObject> restoreAllClassification = ((FoundationObject) dynaObject).restoreAllClassification(false);

			if (!SetUtils.isNullList(restoreAllClassification))
			{
				String messageMandatoryText = this.stubService.getMSRM().getMSRString("ID_APP_FOUNDATION_MANDATORY_CHECK", languageEnum.toString());
				if (dynaObject instanceof StructureObject)
				{
					if (StringUtils.isNullString(messageMandatoryText))
					{
						messageMandatoryText = "ID_APP_FOUNDATION_MANDATORY_CHECK";
					}
				}
				String messageLengthText = this.stubService.getMSRM().getMSRString("ID_APP_FOUNDATION_LENGTH_CHECK", languageEnum.toString());
				if (StringUtils.isNullString(messageLengthText))
				{
					messageLengthText = "ID_APP_FOUNDATION_LENGTH_CHECK";
				}
				for (FoundationObject foundationObject : restoreAllClassification)
				{
					if (foundationObject == null)
					{
						continue;
					}

					mandatoryFieldList.clear();
					classFieldMap.clear();

					List<ClassField> listClassificationField = this.stubService.getEMM().listClassificationField(foundationObject.getClassificationGuid());
					if (!SetUtils.isNullList(listClassificationField))
					{
						for (ClassField classField : listClassificationField)
						{
							if (classField.isMandatory())
							{
								mandatoryFieldList.add(classField.getName());
							}
							classFieldMap.put(classField.getName(), classField);
						}
					}
					String classificationGroupTitle = "";
					if (!StringUtils.isNullString(foundationObject.getClassificationGroupTitle()))
					{
						classificationGroupTitle = StringUtils.getMsrTitle(foundationObject.getClassificationGroupTitle(), languageEnum.getType());
					}
					else
					{
						CodeObjectInfo code = this.stubService.getEMM().getCode(foundationObject.getClassificationGroup());
						if (code != null)
						{
							classificationGroupTitle = code.getTitle(languageEnum);
						}
					}
					if (!SetUtils.isNullList(mandatoryFieldList))
					{
						for (String fieldname : mandatoryFieldList)
						{
							String rt = this.checkFieldMandatory(dynaObject, foundationObject, mandatoryFieldList, classificationGroupTitle, fieldname, languageEnum,
									messageMandatoryText);
							if (!StringUtils.isNullString(rt))
							{
								result.append(rt);
							}

							rt = this.checkFieldSize(foundationObject, classFieldMap, classificationGroupTitle, fieldname, languageEnum, messageLengthText);
							if (!StringUtils.isNullString(rt))
							{
								result.append(rt);
							}
						}
					}

				}
			}
		}
		return result.toString();
	}

	private Map<String, String> getFieldTitleMap(String className, LanguageEnum languageEnum) throws ServiceRequestException
	{
		Map<String, String> fieldMap = new HashMap<String, String>();
		UIObjectInfo uiObject = this.stubService.getEMM().getUIObjectInCurrentBizModel(className, UITypeEnum.FORM);
		if (uiObject != null)
		{
			List<UIField> uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField field : uiFieldList)
				{
					fieldMap.put(field.getName(), field.getTitle(languageEnum));
				}
			}
		}
		return fieldMap;
	}

	private String checkFieldMandatory(DynaObject master, DynaObject foundation, List<String> mandatoryFieldList, String classificationTitle, String fieldname,
			LanguageEnum languageEnum, String message) throws ServiceRequestException
	{
		if (mandatoryFieldList.contains(fieldname))
		{
			Object object = foundation.get(fieldname);

			if (object == null || (object instanceof String && "".equals(object)))
			{
				String fullName = "structure";
				try
				{

					if (master instanceof FoundationObject)
					{
						fullName = ((FoundationObject) master).getFullName();
						if (StringUtils.isNullString(fullName))
						{
							DecoratorFactory.fnd.decorate(master, this.stubService.getEMM());
							fullName = ((FoundationObject) master).getFullName();
						}

						if (StringUtils.isNullString(fullName))
						{
							fullName = "";
						}

						DecoratorFactory.ofd.decorateWithField(null, (FoundationObject) master, this.stubService.getEMM(), this.stubService.getSignature().getCredential(), false);
					}
				}
				catch (DecorateException e)
				{
				}
				return MessageFormat.format(message, fullName, classificationTitle, fieldname) + "\n";
			}
		}
		return null;
	}

	/**
	 * 判断字段大小是否符合规范
	 * 
	 * @param className
	 * @param field
	 * @param fieldValue
	 * @return
	 * @throws DynaDataException
	 */
	private String checkFieldSize(DynaObject foundation, Map<String, ClassField> classFieldMap, String classificationTitle, String fieldname, LanguageEnum languageEnum,
			String message) throws ServiceRequestException
	{
		ClassField field = classFieldMap.get(fieldname.toUpperCase());
		if (field == null)
		{
			return null;
		}
		String fieldType = field.getType().toString();
		String limitSize = field.getFieldSize();
		Object fieldValue = foundation.get(field.getName());
		if (fieldValue == null)
		{
			fieldValue = "";
		}
		else if (fieldType.equals(FieldTypeEnum.STRING.toString()))
		{
			String fieldValueSttr = String.valueOf(fieldValue);
			fieldValueSttr = StringUtils.isNullString(fieldValueSttr) ? "" : fieldValueSttr;
			// String类型直接判断长度
			if (StringUtils.isNullString(limitSize))
			{
				limitSize = String.valueOf(ClassField.defaultCharLength);
			}
			int byteSize = 0;
			byteSize = fieldValueSttr.getBytes(StandardCharsets.UTF_8).length;
			if (Integer.parseInt(limitSize) < byteSize)
			{
				return MessageFormat.format(message, classificationTitle, fieldname, byteSize, limitSize) + "\n";

			}
			if (4000 < byteSize)
			{
				return MessageFormat.format(message, classificationTitle, fieldname, byteSize, 4000) + "\n";
			}
		}
		else if (fieldType.equals(FieldTypeEnum.INTEGER.toString()))
		{
			// integer类型直接判断长度
			String fieldValueSttr = String.valueOf(fieldValue);
			fieldValueSttr = fieldValueSttr.replace("-", "").replace("+", "");
			int realSize = (StringUtils.isNullString(fieldValueSttr) ? "" : fieldValueSttr).length();
			if (StringUtils.isNullString(limitSize))
			{
				// 查过126位,数字溢出
				limitSize = "126";
			}
			if (Integer.parseInt(limitSize) < realSize)
			{
				return MessageFormat.format(message, classificationTitle, fieldname, realSize, limitSize) + "\n";
			}
		}
		else if (fieldType.equals(FieldTypeEnum.FLOAT.toString()))
		{
		}

		return null;
	}

	protected void checkFieldValueExist(DynaObject foundation, List<ClassField> listField, String classification) throws ServiceRequestException
	{
		if (listField != null)
		{

			for (ClassField classField : listField)
			{
				if (classField.getName().equalsIgnoreCase(SystemClassFieldEnum.OBSOLETEUSER.getName())//
						|| classField.getName().equalsIgnoreCase(SystemClassFieldEnum.OWNERUSER.getName())//
						|| classField.getName().equalsIgnoreCase(SystemClassFieldEnum.OWNERGROUP.getName())//
						|| classField.getName().equalsIgnoreCase(SystemClassFieldEnum.CHECKOUTUSER.getName())//
						|| classField.getName().equalsIgnoreCase(SystemClassFieldEnum.CREATEUSER.getName())//
						|| classField.getName().equalsIgnoreCase(SystemClassFieldEnum.UPDATEUSER.getName())//
						|| classField.getName().equalsIgnoreCase(SystemClassFieldEnum.ECFLAG.getName()))//
				{
					continue;
				}

				switch (classField.getType())
				{
				case CODE:
					String codeItemGuid = (String) foundation.get(classField.getName());
					if (!StringUtils.isNullString(codeItemGuid))
					{
						CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(codeItemGuid);
						if (codeItem == null)
						{
							foundation.put(classField.getName(), null);
							foundation.put(classField.getName() + "$NAME", null);
							foundation.put(classField.getName() + "$TITLE", null);
							continue;
						}
						String codeGuid = codeItem.getCodeGuid();
						if (!StringUtils.isNullString(codeGuid))
						{
							CodeObjectInfo code = this.stubService.getEMM().getCode(codeGuid);
							if (code == null || !code.getName().equalsIgnoreCase(classField.getTypeValue()))
							{
								foundation.put(classField.getName(), null);
								foundation.put(classField.getName() + "$NAME", null);
								foundation.put(classField.getName() + "$TITLE", null);
							}
						}
					}
					break;
				case CLASSIFICATION:
					codeItemGuid = (String) foundation.get(classField.getName());
					if (!StringUtils.isNullString(codeItemGuid))
					{
						CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(codeItemGuid);
						if (codeItem == null)
						{
							foundation.put(classField.getName(), null);
							foundation.put(classField.getName() + "NAME", null);
							foundation.put(classField.getName() + "TITLE", null);
							continue;
						}
						String codeGuid = codeItem.getCodeGuid();
						if (!StringUtils.isNullString(codeGuid))
						{
							CodeObjectInfo code = this.stubService.getEMM().getCode(codeGuid);
							if (code == null || !(code.getName().equalsIgnoreCase(classField.getTypeValue()) || code.getName().equalsIgnoreCase(classification)))
							{
								foundation.put(classField.getName(), null);
								foundation.put(classField.getName() + "NAME", null);
								foundation.put(classField.getName() + "TITLE", null);
							}
						}
					}
					break;
				case OBJECT:

					ClassInfo typeValueObject = this.stubService.getEMM().getClassByName(classField.getTypeValue());
					if (typeValueObject == null)
					{
						foundation.put(classField.getName(), null);
						foundation.put(classField.getName() + "$CLASS", null);
						foundation.put(classField.getName() + "$MASTER", null);
						foundation.put(classField.getName() + "$NAME", null);
					}

					if (typeValueObject != null && (typeValueObject.hasInterface(ModelInterfaceEnum.IUser) || typeValueObject.hasInterface(ModelInterfaceEnum.IGroup)
							|| typeValueObject.hasInterface(ModelInterfaceEnum.IPMCalendar) || typeValueObject.hasInterface(ModelInterfaceEnum.IPMRole)))
					{
						return;
					}

					String fieldClassGuid = (String) foundation.get(classField.getName() + "$CLASS");
					if (!StringUtils.isNullString(fieldClassGuid))
					{
						ClassInfo classByGuid = this.stubService.getEMM().getClassByGuid(fieldClassGuid);
						if (classByGuid == null)
						{
							foundation.put(classField.getName(), null);
							foundation.put(classField.getName() + "$CLASS", null);
							foundation.put(classField.getName() + "$MASTER", null);
							foundation.put(classField.getName() + "$NAME", null);

							continue;
						}

						boolean isClear = true;
						if (!classByGuid.getName().equalsIgnoreCase(typeValueObject.getName()))
						{
							List<ClassInfo> listAllSuperClass = this.stubService.getEMM().listAllSuperClass(classByGuid.getName(), classByGuid.getGuid());

							if (!SetUtils.isNullList(listAllSuperClass))
							{
								for (ClassInfo all : listAllSuperClass)
								{
									if (all != null && all.getName().equalsIgnoreCase(typeValueObject.getName()))
									{
										isClear = false;
										break;
									}
								}
							}
						}
						else
						{
							isClear = false;
						}

						if (isClear)
						{
							foundation.put(classField.getName(), null);
							foundation.put(classField.getName() + "$CLASS", null);
							foundation.put(classField.getName() + "$MASTER", null);
							foundation.put(classField.getName() + "$NAME", null);
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}

	protected void checkFoundationFieldExist(FoundationObject foundation) throws ServiceRequestException
	{
		String classGuid = foundation.getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(classInfo.getName());

		this.checkFieldValueExist(foundation, listFieldOfClass, classInfo.getClassification());

		List<FoundationObject> restoreAllClassification = foundation.restoreAllClassification(false);
		if (!SetUtils.isNullList(restoreAllClassification))
		{
			for (FoundationObject classificationFoundaiton : restoreAllClassification)
			{
				if (classificationFoundaiton == null)
				{
					return;
				}

				if (StringUtils.isNullString(classificationFoundaiton.getClassificationGuid()))
				{
					return;
				}

				List<ClassField> listClassificationField = this.stubService.getEMM().listClassificationField(classificationFoundaiton.getClassificationGuid());
				this.checkFieldValueExist(classificationFoundaiton, listClassificationField, null);
			}
		}
	}

	protected void checkFoundationFieldRegex(FoundationObject foundation) throws ServiceRequestException
	{
		String classGuid = foundation.getObjectGuid().getClassGuid();
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);

		List<ClassField> listFieldOfClass = this.stubService.getEMM().listFieldOfClass(classInfo.getName());
		if (!SetUtils.isNullList(listFieldOfClass))
		{
			for (ClassField field : listFieldOfClass)
			{
				if (field.getType() == FieldTypeEnum.STRING && !StringUtils.isNullString(field.getValidityRegex()))
				{
					Pattern pattern = Pattern.compile(field.getValidityRegex());
					boolean matches = pattern.matcher(StringUtils.convertNULLtoString(foundation.get(field.getName()))).matches();
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

	public void changeStatus(ObjectGuid objectGuid, SystemStatusEnum fromStatusEnum, SystemStatusEnum toStatusEnum, boolean isCheckStatusChange, boolean isCheckAuth)
			throws ServiceRequestException
	{

		if (fromStatusEnum == null || toStatusEnum == null)
		{
			return;
		}

		if (fromStatusEnum == toStatusEnum)
		{
			return;
		}

		// 判断是否可以跳转
		if (isCheckStatusChange && !SystemStatusEnum.listStatusChange(fromStatusEnum).contains(toStatusEnum))
		{
			return;
		}

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		try
		{
			String sessionId = this.stubService.getSignature().getCredential();
			// objectGuid中没有masterguid，需要重新查询一次
			FoundationObject foundationObject = this.stubService.getFoundationStub().getObjectByGuid(objectGuid, Constants.isSupervisor(isCheckAuth, this.stubService));
			DataServer.getInstanceService().changeStatus(fromStatusEnum, toStatusEnum, foundationObject.getObjectGuid(), Constants.isSupervisor(isCheckAuth, this.stubService),
					sessionId);

			if (toStatusEnum == SystemStatusEnum.RELEASE || toStatusEnum == SystemStatusEnum.PRE || toStatusEnum == SystemStatusEnum.ECP
					|| (fromStatusEnum == SystemStatusEnum.PRE && toStatusEnum == SystemStatusEnum.WIP))
			{
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
				if (classInfo != null && !(classInfo.hasInterface(ModelInterfaceEnum.IViewObject) || classInfo.hasInterface(ModelInterfaceEnum.IBOMView)))
				{
					List<ViewObject> listRelation = this.stubService.getRelationStub().listRelation(objectGuid, false, true);
					if (!SetUtils.isNullList(listRelation))
					{
						for (ViewObject viewObject : listRelation)
						{
							if (viewObject.isCheckOut())
							{
								DataServer.getInstanceService().checkin(viewObject, false, sessionId, this.stubService.getFixedTransactionId());
							}
							DataServer.getInstanceService().changeStatus(fromStatusEnum, toStatusEnum, viewObject.getObjectGuid(),
									Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);
						}
					}

					List<BOMView> bomviewList = ((BOMSImpl) this.stubService.getBOMS()).getBomViewStub().listBOMView(objectGuid, false);
					if (!SetUtils.isNullList(bomviewList))
					{
						for (BOMView obj : bomviewList)
						{
							if (obj.isCheckOut())
							{
								DataServer.getInstanceService().checkin(obj, false, sessionId, this.stubService.getFixedTransactionId());
							}
							DataServer.getInstanceService().changeStatus(fromStatusEnum, toStatusEnum, obj.getObjectGuid(), Constants.isSupervisor(isCheckAuth, this.stubService),
									sessionId);
						}
					}
				}
			}

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
	}

	public void updateHasEnd2Flg(ObjectGuid end1ObjectGuid, String relationTemplateGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(relationTemplateGuid))
		{
			return;
		}

		RelationTemplate relationTemplate = this.stubService.getEMM().getRelationTemplate(relationTemplateGuid);
		if (!relationTemplate.isIsRecordHasEnd2Data() || StringUtils.isNullString(relationTemplate.getFieldForRecordHasEnd2Data()))
		{
			return;
		}

		FoundationObject end1 = this.stubService.getObject(end1ObjectGuid);
		if (end1 == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "end1 is not exist.");
		}

		boolean hasEnd2 = false;
		ViewObject viewObject = this.stubService.getRelationByEND1(end1ObjectGuid, relationTemplate.getName());
		if (viewObject != null)
		{
			List<FoundationObject> end2List = this.stubService.listFoundationObjectOfRelation(viewObject.getObjectGuid(), null, null, null, false);
			if (!SetUtils.isNullList(end2List))
			{
				hasEnd2 = true;
			}
		}

		end1.put(relationTemplate.getFieldForRecordHasEnd2Data(), null);
		end1.put(relationTemplate.getFieldForRecordHasEnd2Data(), BooleanUtils.getBooleanStringYN(hasEnd2));
		this.stubService.getFSaverStub().saveObject(end1, false, false, false, null, false, false, false);
	}
}