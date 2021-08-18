package dyna.app.service.brs.bom;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.numbering.NumberAllocate;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.ECEffectedBOMRelation;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DrawTransferBOMStub extends AbstractServiceStub<BOMSImpl>
{

	protected void transferBOM(ObjectGuid objectGuid, String bomTemplateName, String procRtGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		if (!classInfo.hasInterface(ModelInterfaceEnum.IDWTransfer))
		{
			// 模型错误
			throw new ServiceRequestException("ID_APP_SWTTOBOM_MODEL_ERROR", "model is wrong.");
		}

		FoundationObject end1 = this.stubService.getBOAS().getObject(objectGuid);
		if (end1 == null)
		{
			// 对象不存在
			throw new ServiceRequestException("ID_DS_NO_DATA", "draw is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (!end1.getStatus().equals(SystemStatusEnum.ECP) && !end1.getStatus().equals(SystemStatusEnum.WIP))
		{
			// 当前状态不允许转换BOM
			throw new ServiceRequestException("ID_APP_SWTTOBOM_STATUS_WRONG", "draw cannot be transfered to bom with status ='" + end1.getStatus() + "'");
		}

		RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateByName(objectGuid, BuiltinRelationNameEnum.MODEL_STRUCTURE.toString());
		if (template == null)
		{
			// 模版{0}不存在
			throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + BuiltinRelationNameEnum.MODEL_STRUCTURE.toString(), null,
					BuiltinRelationNameEnum.MODEL_STRUCTURE.toString());
		}

		ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(objectGuid, BuiltinRelationNameEnum.MODEL_STRUCTURE.toString());
		if (viewObject == null)
		{
			// 模型结构数据不存在
			throw new ServiceRequestException("ID_APP_NO_RELATION_DATA", "model structure data is not exist.");
		}

		SearchCondition end2SearchCondition = this.buildEnd2SearchCondition(template);
		List<StructureObject> structureList = this.stubService.getBOAS().listObjectOfRelation(objectGuid, BuiltinRelationNameEnum.MODEL_STRUCTURE.toString(), null,
				end2SearchCondition, null);
		if (SetUtils.isNullList(structureList))
		{
			// 模型结构数据不存在
			throw new ServiceRequestException("ID_APP_NO_RELATION_DATA", "model structure data is not exist.");
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			List<FoundationObject> checkoutFoundationObjectList = new ArrayList<FoundationObject>();

			// 转换BOM
			this.transferBOM(end1, structureList, bomTemplateName, procRtGuid, checkoutFoundationObjectList);

			// 检入所有检出的对象
			this.checkAllObject(checkoutFoundationObjectList);

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

	private FoundationObject getCheckoutFoundationObject(FoundationObject foundationObject, List<FoundationObject> checkoutFoundationObjectList) throws ServiceRequestException
	{
		if (!foundationObject.isCheckOut())
		{
			foundationObject = this.stubService.getBOAS().checkOut(foundationObject);
			checkoutFoundationObjectList.add(foundationObject);
		}
		return foundationObject;
	}

	/**
	 * 检入end1和BOM结构
	 * 
	 * @param end1
	 * @return
	 * @throws ServiceRequestException
	 */
	private void checkAllObject(List<FoundationObject> foundationObjectList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(foundationObjectList))
		{
			for (FoundationObject foundationObject : foundationObjectList)
			{
				if (foundationObject.isCheckOut())
				{
					this.stubService.getBOAS().checkIn(foundationObject, false);
				}
			}
		}
	}

	/**
	 * 处理模型结构中的数据 转换为Bom结构
	 * 
	 * @param listObjectOfRelation
	 * @throws ServiceRequestException
	 */
	public void transferBOM(FoundationObject end1, List<StructureObject> structureObjectList, String bomTemplateName, String procRtGuid,
			List<FoundationObject> checkoutFoundationObjectList) throws ServiceRequestException
	{
		NumberAllocate na = new NumberAllocate();
		if (!SetUtils.isNullList(structureObjectList))
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(end1.getObjectGuid().getClassGuid());
			if (!classInfo.hasInterface(ModelInterfaceEnum.IDWTransfer))
			{
				return;
			}

			// 如果对象是发布状态，则修订新版本
			if (end1.getStatus() == SystemStatusEnum.RELEASE)
			{
				end1 = this.stubService.getBOAS().prepareRevision(end1.getObjectGuid());
				end1 = this.stubService.getBOAS().createRevision(end1, true);
			}

			if (end1.getAlterId() == null)
			{
				// 检出
				end1 = getCheckoutFoundationObject(end1, checkoutFoundationObjectList);
				String message = na.alterIdAllocate(end1, this.stubService, false);

				if (StringUtils.isNullString(message) && end1.isChanged(SystemClassFieldEnum.ALTERID.getName()))
				{
					end1 = this.stubService.getBOAS().saveObject(end1);
				}
			}

			// 获得BOM视图
			BOMView bomView = this.stubService.getBOMViewByEND1(end1.getObjectGuid(), bomTemplateName);

			// 取得BOM结构
			List<BOMStructure> bomStructureList = null;
			if (bomView != null)
			{
				// 检出
				bomView = (BOMView) this.getCheckoutFoundationObject(bomView, checkoutFoundationObjectList);
				// 取得BOM结构
				bomStructureList = this.stubService.listBOM(end1.getObjectGuid(), bomView.getName(), null, null, null);
			}

			if (bomStructureList == null)
			{
				bomStructureList = new ArrayList<BOMStructure>();
			}

			// 首先根据模型结构删除多余的BOM结构
			this.deleteNotExistInModelFromBOM(structureObjectList, bomStructureList);
			// 然后把模型结构中存在，但是BOM结构中不存在的对象增加到BOM结构中
			this.addNotExistInBOMFromModel(end1, structureObjectList, bomView, bomTemplateName, bomStructureList, procRtGuid, checkoutFoundationObjectList);
			for (StructureObject structureObject : structureObjectList)
			{
				ObjectGuid end2ObjectGuid = structureObject.getEnd2ObjectGuid();
				FoundationObject end2 = this.stubService.getBOAS().getObject(end2ObjectGuid);

				RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateByName(end2.getObjectGuid(), BuiltinRelationNameEnum.MODEL_STRUCTURE.toString());
				SearchCondition end2SearchCondition = this.buildEnd2SearchCondition(template);
				List<StructureObject> structureObjectList_ = this.stubService.getBOAS().listObjectOfRelation(end2.getObjectGuid(),
						BuiltinRelationNameEnum.MODEL_STRUCTURE.toString(), null, end2SearchCondition, null);
				this.transferBOM(end2, structureObjectList_, bomTemplateName, procRtGuid, checkoutFoundationObjectList);
			}
		}
	}

	/**
	 * 对于模型图中不存在，但是BOM中存在的，要删除
	 * 
	 * @throws ServiceRequestException
	 */
	private void deleteNotExistInModelFromBOM(List<StructureObject> structureObjectList, List<BOMStructure> bomStructureList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(bomStructureList))
		{
			for (BOMStructure bomStructure : bomStructureList)
			{
				StructureObject structureObject = this.getStuctureObjectByBOMStructure(bomStructure, structureObjectList);
				if (structureObject == null)
				{
					// bom结构中的对象在模型结构中不存在，并且该对象是图纸转物料对象，则把该对象从BOM结构中移除
					ObjectGuid bomhasModelNone = bomStructure.getEnd2ObjectGuid();
					FoundationObject end2 = this.stubService.getBOAS().getObject(bomhasModelNone);
					if (end2 != null)
					{
						ClassInfo end2ClassInfo = this.stubService.getEMM().getClassByGuid(end2.getObjectGuid().getClassGuid());
						if (end2ClassInfo.hasInterface(ModelInterfaceEnum.IDWTransfer))
						{
							this.stubService.unlinkBOM(bomStructure);
						}
					}
				}
			}
		}
	}

	/**
	 * 把模型结构中存在，但是BOM结构中不存在的对象增加到BOM结构中
	 * 
	 * @param end1
	 * @param structureObjectList
	 * @param bomTemplateName
	 * @param checkoutFoundationObjectList
	 * @return
	 * @throws ServiceRequestException
	 */
	private BOMView addNotExistInBOMFromModel(FoundationObject end1, List<StructureObject> structureObjectList, BOMView bomView, String bomTemplateName,
			List<BOMStructure> bomStructureList, String procRtGuid, List<FoundationObject> checkoutFoundationObjectList) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateByName(end1.getObjectGuid(), bomTemplateName);
		if (bomTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_NO_BOMTEMPLATE_EXIST", "bomTemplate is not exist.", null, bomTemplateName);
		}
		// 获得BOM视图
		if (bomView == null)
		{
			// 不存在，则创建
			bomView = this.stubService.createBOMViewByBOMTemplate(bomTemplate.getGuid(), end1.getObjectGuid(), false);
			bomView = (BOMView) this.getCheckoutFoundationObject(bomView, checkoutFoundationObjectList);
			bomStructureList = new ArrayList<BOMStructure>();
		}

		// 取得BOM结构
		if (!SetUtils.isNullList(structureObjectList))
		{
			List<ECEffectedBOMRelation> needAddBOMList = new ArrayList<ECEffectedBOMRelation>();
			for (StructureObject structureObject : structureObjectList)
			{
				ObjectGuid end2ObjectGuid = structureObject.getEnd2ObjectGuid();
				ClassStub.decorateObjectGuid(end2ObjectGuid, this.stubService);
				ClassInfo end2ClassInfo = this.stubService.getEMM().getClassByGuid(end2ObjectGuid.getClassGuid());
				BOMStructure bomStructure = this.getBOMStructureByStuctureObject(structureObject, bomStructureList);
				boolean bomRelated = true;
				if (!StringUtils.isNullString((String) structureObject.get("BOMRelated")))
				{
					if (BooleanUtils.getBooleanByYN((String) structureObject.get("BOMRelated")))
					{
						bomRelated = true;
					}
				}
				else
				{
					bomRelated = true;
				}
				if (bomRelated && end2ClassInfo.hasInterface(ModelInterfaceEnum.IDWTransfer))
				{
					if (bomStructure == null)
					{
						// 未找到对应的BOM结构，则需要在BOM结构中新增
						this.addToBOM(bomStructureList, structureObject, bomView, bomTemplate, needAddBOMList, null, ECOperateTypeEnum.INSERT);
					}
					else
					{
						this.addToBOM(bomStructureList, structureObject, bomView, bomTemplate, needAddBOMList, bomStructure, ECOperateTypeEnum.MODIFY);
					}
				}
			}

			if (!SetUtils.isNullList(needAddBOMList))
			{
				this.stubService.editBOM(bomView, needAddBOMList, procRtGuid, bomTemplate.getName(), true);
			}
		}

		return bomView;
	}

	/**
	 * 添加到BOM结构中
	 * 
	 * @param bomStructureList
	 * @param structureObject
	 * @param bomView
	 * @param bomTemplate
	 * @return
	 * @throws ServiceRequestException
	 */
	private void addToBOM(List<BOMStructure> bomStructureList, StructureObject structureObject, BOMView bomView, BOMTemplateInfo bomTemplate,
			List<ECEffectedBOMRelation> effectedBOMList, BOMStructure bomStructure, ECOperateTypeEnum operateEnum) throws ServiceRequestException
	{
		if (operateEnum.equals(ECOperateTypeEnum.INSERT))
		{
			bomStructure = this.stubService.newBOMStructure(bomTemplate.getStructureClassGuid(), bomTemplate.getStructureClassName());
			bomStructure.setViewObjectGuid(bomView.getObjectGuid());
			bomStructure.setEnd1ObjectGuid(bomView.getEnd1ObjectGuid());
			bomStructure.setEnd2ObjectGuid(structureObject.getEnd2ObjectGuid());
			bomStructure.setSequence(this.getSequence(bomStructureList, bomTemplate));
		}

		Number quantity = (Number) structureObject.get("QUANTITY");
		bomStructure.setQuantity(quantity == null ? null : quantity.doubleValue());
		bomStructureList.add(bomStructure);
		effectedBOMList.add(this.getECEffectedBOMRelation(bomStructure, bomTemplate, operateEnum.name()));
	}

	private ECEffectedBOMRelation getECEffectedBOMRelation(BOMStructure bomStructure, BOMTemplateInfo bomTemplate, String operate)
	{
		ECEffectedBOMRelation effectedBOM = new ECEffectedBOMRelation();
		effectedBOM.setBOMStructrue(bomStructure);
		effectedBOM.setEcOperate(operate);
		effectedBOM.setIsPrecise(bomTemplate.getPrecise() == BomPreciseType.PRECISE);
		return effectedBOM;
	}

	/**
	 * 取得BOM结构的下一个顺序号
	 * 
	 * @param bomStructureList
	 * @param bomTemplate
	 * @return
	 */
	private String getSequence(List<BOMStructure> bomStructureList, BOMTemplateInfo bomTemplate)
	{
		int increment = 10;
		int length = -1;

		if (bomTemplate.getSequencePad() && bomTemplate.getSequenceLength() != null)
		{
			length = bomTemplate.getSequenceLength().intValue();
		}
		if (bomTemplate.getSequenceIncrement() != null)
		{
			increment = bomTemplate.getSequenceIncrement().intValue();
		}
		int start = bomTemplate.getSequenceStart().intValue();
		int max = this.getMaxSequence(bomStructureList);

		if (SetUtils.isNullList(bomStructureList))
		{
			if (length > 0)
			{
				return StringUtils.lpad(String.valueOf(start), length, '0');
			}
			else
			{
				return String.valueOf(start);
			}
		}
		else
		{
			if (length > 0)
			{
				return StringUtils.lpad(String.valueOf(max + increment), length, '0');
			}
			else
			{
				return String.valueOf(max + increment);
			}
		}
	}

	private int getMaxSequence(List<BOMStructure> bomStructureList)
	{
		int max = 0;
		if (!SetUtils.isNullList(bomStructureList))
		{
			for (BOMStructure bomStructure : bomStructureList)
			{
				if (!StringUtils.isNullString(bomStructure.getSequence()) && Integer.valueOf(bomStructure.getSequence()) > max)
				{
					max = Integer.valueOf(bomStructure.getSequence());
				}
			}
			return max;
		}
		return max;
	}

	/**
	 * 找出模型结构中对应的BOM结构对象
	 * 
	 * @param bomStructure
	 * @param structureObjectList
	 * @return
	 */
	private StructureObject getStuctureObjectByBOMStructure(BOMStructure bomStructure, List<StructureObject> structureObjectList)
	{
		if (!SetUtils.isNullList(structureObjectList))
		{
			for (StructureObject structureObject : structureObjectList)
			{
				if (bomStructure.getEnd2ObjectGuid().getMasterGuid().equals(structureObject.getEnd2ObjectGuid().getMasterGuid()))
				{
					return structureObject;
				}
			}
		}
		return null;
	}

	/**
	 * 找出BOM结构中对应的模型结构对象
	 * 
	 * @param bomStructure
	 * @param structureObjectList
	 * @return
	 */
	private BOMStructure getBOMStructureByStuctureObject(StructureObject structureObject, List<BOMStructure> bomStructureList)
	{
		if (!SetUtils.isNullList(bomStructureList))
		{
			for (BOMStructure bomStructure : bomStructureList)
			{
				if (bomStructure.getEnd2ObjectGuid().getMasterGuid().equals(structureObject.getEnd2ObjectGuid().getMasterGuid()))
				{
					return bomStructure;
				}
			}
		}
		return null;
	}

	/**
	 * 根据关联关系模板构造end2的SearchCondition
	 * 
	 * @param template
	 * @return
	 * @throws ServiceRequestException
	 */
	private SearchCondition buildEnd2SearchCondition(RelationTemplateInfo template) throws ServiceRequestException
	{
		List<RelationTemplateEnd2> templateEnd2List = this.stubService.getEMM().getRelationTemplate(template.getGuid()).getRelationTemplateEnd2List();
		RelationTemplateEnd2 templateEnd2 = templateEnd2List.get(0);
		BOInfo end2BOInfo = this.stubService.getEMM().getCurrentBoInfoByName(templateEnd2.getEnd2BoName(), false);

		List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(end2BOInfo.getClassName());

		SearchCondition condition = SearchConditionFactory.createSearchCondition4Class(end2BOInfo.getClassName(), null, false);
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObject : uiObjectList)
			{
				condition.addResultUIObjectName(uiObject.getName());
			}
		}
		return condition;
	}
}
