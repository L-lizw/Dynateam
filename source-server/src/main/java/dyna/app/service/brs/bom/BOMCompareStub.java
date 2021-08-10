/*
	 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMCompare
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.bom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.bom.comparator.BOMComparator;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMCompare;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ECOperateTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public class BOMCompareStub extends AbstractServiceStub<BOMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public BOMCompareStub(ServiceContext context, BOMSImpl service)
	{
		super(context, service);
	}

	protected List<BOMCompare> compareBOMByObject(ObjectGuid leftBOMViewObjectGuid, DataRule leftDataRule, ObjectGuid rightBOMViewObjectGuid, DataRule rightDataRule)
			throws ServiceRequestException
	{
		try
		{

			String className = null;

			BOMView leftBomView = this.stubService.getBOMView(leftBOMViewObjectGuid);
			BOMView rightBomView = this.stubService.getBOMView(rightBOMViewObjectGuid);
			BOMView bomView = leftBomView;
			if (bomView == null)
			{
				bomView = rightBomView;
			}
			BOMTemplateInfo bomTemplate = null;
			if (bomView != null)
			{
				bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomView.get(ViewObject.TEMPLATE_ID));

				if (bomTemplate != null)
				{
					className = bomTemplate.getStructureClassName();
				}
			}

			SearchCondition searchCondition = null;

			if (className != null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(className);
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(className, uiObjectList);
			}

			SearchCondition end2SearchCondition = null;

			if (bomTemplate != null)
			{
				end2SearchCondition = this.stubService.getEMM().createAssoEnd2SearchCondition(bomTemplate.getGuid(), true, true);
			}

			// 获取左边对应的BOMStructure列表
			List<BOMStructure> leftBomStructureList = this.stubService.listBOM(leftBOMViewObjectGuid, searchCondition, end2SearchCondition, leftDataRule);

			// 获取右边对应的BOMStructure列表
			List<BOMStructure> rightBomStructureList = this.stubService.listBOM(rightBOMViewObjectGuid, searchCondition, end2SearchCondition, rightDataRule);

			List<BOMStructure> mergerLeftStructureList = null;
			if (leftBomView != null)
			{
				mergerLeftStructureList = this.mergerStructureByEnd2(leftBomStructureList, leftBomView.isPrecise());

			}

			List<BOMStructure> mergerRightStructureList = null;
			if (rightBomView != null)
			{
				mergerRightStructureList = this.mergerStructureByEnd2(rightBomStructureList, rightBomView.isPrecise());
			}

			List<BOMCompare> bomCompareList = this.compareByObject(mergerLeftStructureList, mergerRightStructureList);
			if (bomCompareList != null)
			{
				Collections.sort(bomCompareList, new BOMComparator());
			}
			return bomCompareList;

		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
	}

	private List<BOMStructure> mergerStructureByEnd2(List<BOMStructure> bomStructureList, boolean isPrecise)
	{
		if (SetUtils.isNullList(bomStructureList))
		{
			return bomStructureList;
		}

		List<BOMStructure> mergeredBOMStructure = new ArrayList<BOMStructure>();
		Map<String, BOMStructure> mergeredBOMStructureMap = new HashMap<String, BOMStructure>();
		String key = null;
		for (BOMStructure bomStructure : bomStructureList)
		{
			if (isPrecise)
			{
				key = bomStructure.getEnd2ObjectGuid().getGuid();
			}
			else
			{
				key = bomStructure.getEnd2ObjectGuid().getMasterGuid();
			}

			if (!StringUtils.isNullString(key) && mergeredBOMStructureMap.containsKey(key))
			{
				BOMStructure bomStructureTemp = mergeredBOMStructureMap.get(key);
				bomStructureTemp.setQuantity(bomStructureTemp.getQuantity() + bomStructure.getQuantity());
			}
			else
			{
				mergeredBOMStructureMap.put(key, bomStructure);
				mergeredBOMStructure.add(bomStructure);
			}
		}

		return mergeredBOMStructure;
	}

	public List<BOMCompare> compareBOM(ObjectGuid leftBOMViewObjectGuid, DataRule leftDataRule, ObjectGuid rightBOMViewObjectGuid, DataRule rightDataRule)
			throws ServiceRequestException
	{

		try
		{
			String className = null;
			BOMView bomView = this.stubService.getBOMView(leftBOMViewObjectGuid);

			if (bomView == null)
			{
				bomView = this.stubService.getBOMView(rightBOMViewObjectGuid);
			}
			BOMTemplateInfo bomTemplate = null;
			if (bomView != null)
			{
				bomTemplate = this.stubService.getEMM().getBOMTemplateById((String) bomView.get(ViewObject.TEMPLATE_ID));

				if (bomTemplate != null)
				{
					className = bomTemplate.getStructureClassName();
				}
			}

			SearchCondition searchCondition = null;

			if (className != null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(className);
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(className, uiObjectList);
			}

			SearchCondition end2SearchCondition = null;
			if (bomTemplate != null)
			{
				end2SearchCondition = this.stubService.getEMM().createAssoEnd2SearchCondition(bomTemplate.getGuid(), true, true);
			}

			List<BOMStructure> leftBomStructureList = null;
			List<BOMStructure> rightBomStructureList = null;
			// 获取左边对应的BOMStructure列表
			leftBomStructureList = this.stubService.listBOM(leftBOMViewObjectGuid, searchCondition, end2SearchCondition, leftDataRule);

			// 获取右边对应的BOMStructure列表
			rightBomStructureList = this.stubService.listBOM(rightBOMViewObjectGuid, searchCondition, end2SearchCondition, rightDataRule);
			List<BOMCompare> bomCompareList = this.compareBySequence(leftBomStructureList, rightBomStructureList);
			if (bomCompareList != null)
			{
				Collections.sort(bomCompareList, new BOMComparator());
			}
			return bomCompareList;

		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
	}

	private List<BOMCompare> compareBySequence(List<BOMStructure> leftBomStructureList, List<BOMStructure> rightBomStructureList) throws ServiceRequestException
	{
		List<BOMCompare> bomCompareList = null;
		BOMCompare bomCompare = null;

		if (SetUtils.isNullList(leftBomStructureList) && SetUtils.isNullList(rightBomStructureList))
		{// 左右BOM都为空
			return null;
		}
		else if (!SetUtils.isNullList(leftBomStructureList) && SetUtils.isNullList(rightBomStructureList))
		{// 左BOM不为空 右BOM为空
			bomCompareList = new ArrayList<BOMCompare>();

			for (BOMStructure bomStructure : leftBomStructureList)
			{
				bomCompare = new BOMCompare();
				bomCompare.setLeftBomStructure(bomStructure);

				// 这时的操作符为删除
				bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.REMOVE);
				bomCompareList.add(bomCompare);
			}
		}
		else if (SetUtils.isNullList(leftBomStructureList) && !SetUtils.isNullList(rightBomStructureList))
		{// 左BOM为空 右BOM不为空
			bomCompareList = new ArrayList<BOMCompare>();

			for (BOMStructure bomStructure : rightBomStructureList)
			{
				bomCompare = new BOMCompare();
				bomCompare.setRightBomStructure(bomStructure);

				// 这时的操作符为插入
				bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.INSERT);
				bomCompareList.add(bomCompare);
			}
		}
		else
		{// 左右BOM都不为空

			bomCompareList = new ArrayList<BOMCompare>();
			for (BOMStructure leftBOMStructure : leftBomStructureList)
			{
				bomCompare = new BOMCompare();
				bomCompare.setLeftBomStructure(leftBOMStructure);

				// 操作符先赋值为移除
				bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.REMOVE);

				if (StringUtils.isNullString(leftBOMStructure.getEnd2ObjectGuid().getGuid()))
				{

					bomCompare.setHasLeftACL(false);
				}

				if (!SetUtils.isNullList(rightBomStructureList) && !StringUtils.isNullString(leftBOMStructure.getEnd2ObjectGuid().getGuid()))
				{
					for (BOMStructure rightBOMStructure : rightBomStructureList)
					{

						if (StringUtils.isNullString(rightBOMStructure.getEnd2ObjectGuid().getGuid()))
						{

							bomCompare.setHasRightACL(false);
							rightBomStructureList.remove(rightBOMStructure);
							continue;
						}

						if (!StringUtils.isNullString(leftBOMStructure.getSequence()) && !StringUtils.isNullString(rightBOMStructure.getSequence())
								&& rightBOMStructure.getSequence().equals(leftBOMStructure.getSequence()))
						{// 找到顺序号一致的对象进行比较

							bomCompare.setRightBomStructure(rightBOMStructure);
							FoundationObject leftFoundationObject = null;
							leftFoundationObject = this.stubService.getBOAS().getObject(leftBOMStructure.getEnd2ObjectGuid());

							FoundationObject rightFoundationObject = null;
							rightFoundationObject = this.stubService.getBOAS().getObject(rightBOMStructure.getEnd2ObjectGuid());

							// 如果顺序号一致，则把操作符先变更为修改
							bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.MODIFY);

							// 根据ID判断两个对象是否同一对象
							if (leftFoundationObject.getId().equals(rightFoundationObject.getId()))
							{
								bomCompare.setSameId(true);

								// 判断两个对象版本是否一致
								if (leftFoundationObject.getRevisionId().equals(rightFoundationObject.getRevisionId()))
								{
									bomCompare.setSameVersion(true);
								}

								// 判断引用数量是否一致
								if (leftBOMStructure.getQuantity().doubleValue() == rightBOMStructure.getQuantity().doubleValue())
								{
									bomCompare.setSameQuantity(true);
								}
							}

							// 判断是否为修改或替换
							if (leftFoundationObject.getObjectGuid().getGuid().equals(rightFoundationObject.getObjectGuid().getGuid()))
							{
								bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.MODIFY);
							}
							else
							{
								bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.REPLACE);
							}

							Map<String, Object> map = leftBOMStructure;
							Iterator<String> iterator = map.keySet().iterator();

							List<String> fieldChangedList = new ArrayList<String>();
							while (iterator.hasNext())
							{
								String key = iterator.next();
								Object leftValue = map.get(key) == null ? "" : (Object) map.get(key);
								if (!key.equals("UPDATETIME$") && !key.equals("UPDATEUSER$") && !key.equals("CREATETIME$") && !key.equals("CREATEUSER$") && !key.startsWith("VIEW")
										&& !key.equals("GUID$") && !key.equals("ROWCOUNT$") && !key.equals("ROWINDEX$") && !key.equals("END1") && !key.equals("END1$MASTER")
										&& !key.equals("END1$CLASSNAME") && !key.equals("END1$CLASS$ICON") && !key.equals("END1$CLASS") && !key.startsWith("LIFECYCLEPHASE$")
										&& !key.startsWith("CREATEUSER$NAME") && !key.startsWith("UPDATEUSER$NAME") && !key.startsWith("END1$BOGUID"))
								{
									Object rightValue = rightBOMStructure.get(key) == null ? "" : (Object) rightBOMStructure.get(key);

									if (!leftValue.equals(rightValue))
									{
										fieldChangedList.add(key);
									}
								}

							}
							if (fieldChangedList.size() > 0)
							{
								bomCompare.setFieldChangedList(fieldChangedList);
							}

							rightBomStructureList.remove(rightBOMStructure);
							break;
						}
					}
				}

				// 如果三者都为真并且 fieldChangedList为空 那么就是相同的BOM 操作符为NOTHING
				if (bomCompare.isSameId() && bomCompare.isSameQuantity() && bomCompare.isSameVersion() && (SetUtils.isNullList(bomCompare.getFieldChangedList())))
				{
					bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.NOTHING);
				}
				if (ECOperateTypeEnum.NOTHING != bomCompare.getECOperateTypeEnum())
				{
					bomCompareList.add(bomCompare);
				}
			}

			// 如果在左边的数据处理完了之后 右边还有数据，那么对右边剩余数据继续处理
			if (!SetUtils.isNullList(rightBomStructureList))
			{
				for (BOMStructure bomStructure : rightBomStructureList)
				{
					bomCompare = new BOMCompare();
					bomCompare.setRightBomStructure(bomStructure);

					// 这个分支的操作符为插入
					bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.INSERT);

					if (StringUtils.isNullString(bomStructure.getEnd2ObjectGuid().getGuid()))
					{

						bomCompare.setHasRightACL(false);
					}

					bomCompareList.add(bomCompare);
				}
			}

		}

		return bomCompareList;
	}

	private List<BOMCompare> compareByObject(List<BOMStructure> leftBomStructureList, List<BOMStructure> rightBomStructureList) throws ServiceRequestException
	{
		List<BOMCompare> bomCompareList = null;
		BOMCompare bomCompare = null;

		if (SetUtils.isNullList(leftBomStructureList) && SetUtils.isNullList(rightBomStructureList))
		{// 左右BOM都为空
			return null;
		}
		else if (!SetUtils.isNullList(leftBomStructureList) && SetUtils.isNullList(rightBomStructureList))
		{// 左BOM不为空 右BOM为空
			bomCompareList = new ArrayList<BOMCompare>();

			for (BOMStructure bomStructure : leftBomStructureList)
			{
				bomCompare = new BOMCompare();
				bomCompare.setLeftBomStructure(bomStructure);

				// 这时的操作符为删除
				bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.REMOVE);
				bomCompareList.add(bomCompare);
			}
		}
		else if (SetUtils.isNullList(leftBomStructureList) && !SetUtils.isNullList(rightBomStructureList))
		{// 左BOM为空 右BOM不为空
			bomCompareList = new ArrayList<BOMCompare>();

			for (BOMStructure bomStructure : rightBomStructureList)
			{
				bomCompare = new BOMCompare();
				bomCompare.setRightBomStructure(bomStructure);

				// 这时的操作符为插入
				bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.INSERT);
				bomCompareList.add(bomCompare);
			}
		}
		else
		{// 左右BOM都不为空

			bomCompareList = new ArrayList<BOMCompare>();
			for (BOMStructure leftBOMStructure : leftBomStructureList)
			{
				bomCompare = new BOMCompare();
				bomCompare.setLeftBomStructure(leftBOMStructure);

				// 操作符先赋值为移除
				bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.REMOVE);

				if (StringUtils.isNullString(leftBOMStructure.getEnd2ObjectGuid().getGuid()))
				{
					bomCompare.setHasLeftACL(false);
				}

				if (!SetUtils.isNullList(rightBomStructureList) && !StringUtils.isNullString(leftBOMStructure.getEnd2ObjectGuid().getGuid()))
				{
					for (BOMStructure rightBOMStructure : rightBomStructureList)
					{

						if (StringUtils.isNullString(rightBOMStructure.getEnd2ObjectGuid().getGuid()))
						{

							bomCompare.setHasRightACL(false);
							rightBomStructureList.remove(rightBOMStructure);
							continue;
						}

						if (!StringUtils.isNullString(leftBOMStructure.getEnd2ObjectGuid().getGuid()) && !StringUtils.isNullString(rightBOMStructure.getEnd2ObjectGuid().getGuid())
								&& rightBOMStructure.getEnd2ObjectGuid().getGuid().equals(leftBOMStructure.getEnd2ObjectGuid().getGuid()))
						{// 找到一致的对象进行比较

							bomCompare.setRightBomStructure(rightBOMStructure);

							// 如果对象一致，则把操作符先变更为修改
							bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.MODIFY);

							// 根据ID判断两个对象是否同一对象
							bomCompare.setSameId(true);

							// 判断两个对象版本是否一致
							bomCompare.setSameVersion(true);

							// 判断引用数量是否一致
							if (leftBOMStructure.getQuantity().doubleValue() == rightBOMStructure.getQuantity().doubleValue())
							{
								bomCompare.setSameQuantity(true);
							}

							Map<String, Object> map = leftBOMStructure;
							Iterator<String> iterator = map.keySet().iterator();

							List<String> fieldChangedList = new ArrayList<String>();
							while (iterator.hasNext())
							{
								String key = iterator.next();
								Object leftValue = map.get(key) == null ? "" : (Object) map.get(key);
								if (!key.equals("UPDATETIME$") && !key.equals("UPDATEUSER$") && !key.equals("CREATETIME$") && !key.equals("CREATEUSER$") && !key.startsWith("VIEW")
										&& !key.equals("GUID$") && !key.equals("ROWCOUNT$") && !key.equals("ROWINDEX$") && !key.equals("END1") && !key.equals("END1$MASTER")
										&& !key.equals("END1$CLASSNAME") && !key.equals("END1$CLASS$ICON") && !key.equals("END1$CLASS") && !key.startsWith("LIFECYCLEPHASE$"))
								{
									Object rightValue = rightBOMStructure.get(key) == null ? "" : (Object) rightBOMStructure.get(key);

									if (!leftValue.equals(rightValue))
									{
										fieldChangedList.add(key);
									}
								}

							}
							if (fieldChangedList.size() > 0)
							{
								bomCompare.setFieldChangedList(fieldChangedList);
							}

							rightBomStructureList.remove(rightBOMStructure);
							break;
						}
					}
				}

				// 如果三者都为真并且 fieldChangedList为空 那么就是相同的BOM 操作符为NOTHING
				if (bomCompare.isSameId() && bomCompare.isSameQuantity() && bomCompare.isSameVersion() && SetUtils.isNullList(bomCompare.getFieldChangedList()))
				{
					bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.NOTHING);
				}

				bomCompareList.add(bomCompare);
			}

			// 如果在左边的数据处理完了之后 右边还有数据，那么对右边剩余数据继续处理
			if (!SetUtils.isNullList(rightBomStructureList))
			{
				for (BOMStructure bomStructure : rightBomStructureList)
				{
					bomCompare = new BOMCompare();
					bomCompare.setRightBomStructure(bomStructure);

					// 这个分支的操作符为插入
					bomCompare.setECOperateTypeEnum(ECOperateTypeEnum.INSERT);

					if (StringUtils.isNullString(bomStructure.getEnd2ObjectGuid().getGuid()))
					{
						bomCompare.setHasRightACL(false);
					}

					bomCompareList.add(bomCompare);
				}
			}

		}

		return bomCompareList;
	}
}
