/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceObject
 * wangweixia 2012-7-17
 */
package dyna.app.service.brs.brm;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.ReplaceSearchConf;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangweixia
 *         取替代关系查询
 */
@Component
public class ReplaceQueryStub extends AbstractServiceStub<BRMImpl>
{

	/**
	 * @param replaceSearchConf
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listReplaceDataBySearch(ReplaceSearchConf replaceSearchConf, int pageNum, int pageSize) throws ServiceRequestException
	{
		List<FoundationObject> value = null;
		SearchCondition searchCondition = this.createReplaceSearchCondition();
		if (searchCondition == null)
		{
			return null;
		}
		searchCondition.setPageSize(pageSize);
		searchCondition.setPageNum(pageNum);
		this.setFilter(searchCondition, replaceSearchConf.getMasterItemObjectGuid(), replaceSearchConf.getComponentItemObjectGuid(), replaceSearchConf.getRSItemObjectGuid(),
				replaceSearchConf.getRange(), replaceSearchConf.getType(), replaceSearchConf.getBOMName(), null, true);
		if (SetUtils.isNullList(replaceSearchConf.getSearchResult()) == false)
		{
			for (String field : replaceSearchConf.getSearchResult())
			{
				searchCondition.addResultField(field);
			}
		}
		if ("WIP".equalsIgnoreCase(replaceSearchConf.getSearchType()))
		{
			searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, null, OperateSignEnum.NOTNULL);
			searchCondition.startGroup();

			searchCondition.startGroup();
			searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.ISNULL);
			searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, new Date(), OperateSignEnum.NOTLATER);
			searchCondition.endGroup();

			searchCondition.startGroupWithOR();
			searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.NOTNULL);
			searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, new Date(), OperateSignEnum.NOTLATER);
			searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, new Date(), OperateSignEnum.LATER);
			searchCondition.endGroup();

			searchCondition.startGroupWithOR();
			searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, new Date(), OperateSignEnum.EQUALS);
			searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, new Date(), OperateSignEnum.EQUALS);
			searchCondition.endGroup();

			searchCondition.endGroup();
		}
		else if ("RLS".equalsIgnoreCase(replaceSearchConf.getSearchType()))
		{
			if (replaceSearchConf.getStartEffectivedate() == null)
			{
				throw new ServiceRequestException("ID_APP_BRM_SEARCH_RLS_TIME_ISNULL", "start time is null");
			}
			else
			{
				searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, replaceSearchConf.getStartEffectivedate(), OperateSignEnum.NOTLATER);
				searchCondition.startGroup();

				searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.ISNULL);
				searchCondition.addFilterWithOR(ReplaceSubstituteConstants.InvalidDate, replaceSearchConf.getStartEffectivedate(), OperateSignEnum.LATER);

				searchCondition.startGroupWithOR();
				searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, replaceSearchConf.getStartEffectivedate(), OperateSignEnum.EQUALS);
				searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, replaceSearchConf.getStartEffectivedate(), OperateSignEnum.EQUALS);
				searchCondition.endGroup();

				searchCondition.endGroup();
			}
		}
		else
		{
			if (replaceSearchConf.getStartEffectivedate() != null)
			{
				searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, replaceSearchConf.getStartEffectivedate(), OperateSignEnum.NOTEARLIER);
			}
			if (replaceSearchConf.getEndEffectivedate() != null)
			{
				searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, replaceSearchConf.getEndEffectivedate(), OperateSignEnum.NOTLATER);
			}
			if (replaceSearchConf.getStartExpiryedate() != null)
			{
				searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, replaceSearchConf.getStartExpiryedate(), OperateSignEnum.NOTEARLIER);
			}
			if (replaceSearchConf.getEndExpiryedate() != null)
			{
				searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, replaceSearchConf.getEndExpiryedate(), OperateSignEnum.NOTLATER);
			}
		}
		searchCondition.addOrder(ReplaceSubstituteConstants.MasterItem, true);
		searchCondition.addOrder(ReplaceSubstituteConstants.BOMViewDisplay, true);
		searchCondition.setPageNum(pageNum);
		searchCondition.setPageSize(pageSize);
		value = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
		this.recombinationToNew(value, true, true);
		return value;
	}

	/**
	 * 取替代查询
	 */
	public List<FoundationObject> listReplaceDataByRang(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ReplaceRangeEnum rang, ReplaceTypeEnum type,
			String bomViewName, String bomKey, boolean isContainInvalidDate, boolean isAddOtherAttribute) throws ServiceRequestException
	{
		return listReplaceDataByRang(masterItemObjectGuid, componentItemObjectGuid, null, rang, type, bomViewName, bomKey, isContainInvalidDate, isAddOtherAttribute);
	}

	/**
	 * 取替代查询
	 */
	public List<FoundationObject> listReplaceDataByRang(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, ReplaceRangeEnum rang,
			ReplaceTypeEnum type, String bomViewName, String bomKey, boolean isContainInvalidDate, boolean isAddOtherAttribute) throws ServiceRequestException
	{
		return listReplaceData(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, rang, type, bomViewName, bomKey, isContainInvalidDate, isAddOtherAttribute, true,
				true);
	}

	/**
	 * 取替代查询
	 * 服务端调用，不展现数据
	 */
	public List<FoundationObject> listReplaceDataInner(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, ReplaceRangeEnum rang,
			ReplaceTypeEnum type, String bomViewName, String bomKey, boolean isContainInvalidDate) throws ServiceRequestException
	{
		return listReplaceData(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, rang, type, bomViewName, bomKey, isContainInvalidDate, false, false, false);
	}

	/**
	 * 取替代查询
	 * 
	 * @param masterItemObjectGuid
	 * @param componentItemObjectGuid
	 * @param rsItemObjectGuid
	 * @param rang
	 * @param type
	 * @param bomViewName
	 * @param bomKey
	 * @param isContainInvalidDate
	 * @param isAddOtherAttribute
	 * @param rsOrder
	 * @param recombinationToNew
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> listReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, ReplaceRangeEnum rang,
			ReplaceTypeEnum type, String bomViewName, String bomKey, boolean isContainInvalidDate, boolean isAddOtherAttribute, boolean rsOrder, boolean recombinationToNew)
			throws ServiceRequestException
	{
		List<FoundationObject> replaceDataList = null;
		SearchCondition searchCondition = this.createReplaceSearchCondition();
		if (searchCondition != null)
		{
			searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
			this.setFilter(searchCondition, masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, rang, type, bomViewName, bomKey, isContainInvalidDate);
			this.setResultFields(searchCondition);
			if (rsOrder)
			{
				searchCondition.addOrder(ReplaceSubstituteConstants.RSNumber, true);
			}
			replaceDataList = this.stubService.getBOAS().listObject(searchCondition);
			// if (!isContainInvalidDate)
			// {
			// if (!SetUtils.isNullList(value))
			// {
			// List<FoundationObject> tempList = new ArrayList<FoundationObject>();
			// tempList.addAll(value);
			// for (FoundationObject foundation : tempList)
			// {
			// Date effectiveDate = (Date) foundation.get(ReplaceSubstituteConstants.EffectiveDate);
			// Date invalidDate = (Date) foundation.get(ReplaceSubstituteConstants.InvalidDate);
			// if (invalidDate != null && DateFormat.compareDate(invalidDate, DateFormat.getSysDate()) <= 0)
			// {
			// if (!(DateFormat.compareDate(effectiveDate, invalidDate) == 0 && DateFormat.compareDate(invalidDate,
			// DateFormat.getSysDate()) == 0))
			// {
			// value.remove(foundation);
			// }
			// }
			// }
			// }
			// }
			this.getDataBySearchNextPage(replaceDataList, searchCondition);
			if (recombinationToNew)
			{
				this.recombinationToNew(replaceDataList, true, isAddOtherAttribute);
			}
		}
		return replaceDataList;
	}

	/**
	 * 创建取替代对象的高级搜索
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	private SearchCondition createReplaceSearchCondition() throws ServiceRequestException
	{
		ObjectGuid objectGuid = new ObjectGuid();
		try
		{
			ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IReplaceSubstitute, null);
			if (classInfo != null)
			{
				objectGuid.setClassGuid(classInfo.getGuid());
				objectGuid.setClassName(classInfo.getName());
				return SearchConditionFactory.createSearchCondition(objectGuid, null, true);
			}
		}
		catch (Exception e)
		{
			DynaLogger.info(e.getMessage());
		}
		return null;
	}

	/**
	 * 数据大于500条时，取到剩下所有的数据
	 * 
	 * @param value
	 * @param searchCondition
	 * @throws ServiceRequestException
	 */
	private void getDataBySearchNextPage(List<FoundationObject> value, SearchCondition searchCondition) throws ServiceRequestException
	{
		int size = 0;
		if (!SetUtils.isNullList(value))
		{
			size = value.get(0).getRowCount();
		}
		size = size / 500;
		for (int i = 1; i <= size + 1; i++)
		{
			if (i > 1)
			{
				searchCondition.setPageNum(i);
				List<FoundationObject> list = this.stubService.getBOAS().listObject(searchCondition);
				if (!SetUtils.isNullList(list))
				{
					value.addAll(list);
				}
			}
		}
	}

	/**
	 * 设置过滤条件
	 * 
	 * @param searchCondition
	 * @param masterItemObjectGuid
	 * @param componentItemObjectGuid
	 * @param rsItemObjectGuid
	 * @param rang
	 * @param type
	 * @param bomViewName
	 * @param bomKey
	 *            导入工具此处传入的是Sequence
	 * @throws ServiceRequestException
	 */
	private void setFilter(SearchCondition searchCondition, ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, ReplaceRangeEnum rang,
			ReplaceTypeEnum type, String bomViewName, String bomKey, boolean isContainInvalidDate) throws ServiceRequestException
	{
		searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION); // 查询所有版本
		if (masterItemObjectGuid != null)
		{
			masterItemObjectGuid.setIsMaster(false);
			searchCondition.addFilter(ReplaceSubstituteConstants.MasterItem, masterItemObjectGuid, OperateSignEnum.EQUALS);
			if (bomKey != null)
			{
				if (StringUtils.isGuid(bomKey.toUpperCase()))
				{
					searchCondition.addFilter(ReplaceSubstituteConstants.BOMKey, bomKey, OperateSignEnum.EQUALS);
				}
				else
				{
					// 导入工具
					searchCondition.addFilter(ReplaceSubstituteConstants.Sequence, bomKey, OperateSignEnum.EQUALS);
				}
			}
		}
		else
		{
			searchCondition.addFilter(ReplaceSubstituteConstants.IN_USE, BooleanUtils.getBooleanStringYN(false), OperateSignEnum.NOTEQUALS);
		}
		if (componentItemObjectGuid != null)
		{
			componentItemObjectGuid.setIsMaster(true);
			searchCondition.addFilter(ReplaceSubstituteConstants.ComponentItem, componentItemObjectGuid, OperateSignEnum.EQUALS);
		}
		if (rsItemObjectGuid != null)
		{
			rsItemObjectGuid.setIsMaster(true);
			searchCondition.addFilter(ReplaceSubstituteConstants.RSItem, rsItemObjectGuid, OperateSignEnum.EQUALS);
		}
		if (rang != null)
		{
			if (!StringUtils.isNullString(rang.getValue()))
			{
				CodeItemInfo scopeCode = this.stubService.getEMM().getCodeItemByName(ReplaceSubstituteConstants.Scope, rang.getValue());
				searchCondition.addFilter(ReplaceSubstituteConstants.Scope, scopeCode.getGuid(), OperateSignEnum.YES);
			}
		}
		if (type != null)
		{
			if (!StringUtils.isNullString(type.getValue()))
			{
				CodeItemInfo typeCode = this.stubService.getEMM().getCodeItemByName(ReplaceSubstituteConstants.RSType, type.getValue());
				searchCondition.addFilter(ReplaceSubstituteConstants.RSType, typeCode.getGuid(), OperateSignEnum.YES);
			}
		}
		if (!StringUtils.isNullString(bomViewName))
		{
			searchCondition.addFilter(ReplaceSubstituteConstants.BOMViewDisplay, bomViewName, OperateSignEnum.EQUALS);
		}
		if (!isContainInvalidDate)
		{
			searchCondition.startGroup();
			searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, DateFormat.getSysDate(), OperateSignEnum.NOTEARLIER);
			searchCondition.addFilterWithOR(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.ISNULL);
			searchCondition.endGroup();
		}
	}

	/**
	 * 重新组合主件，元件，替代件(元件替代件放最新版)
	 * 
	 * @param foList
	 * @param recombination
	 * @param isNeedId
	 * @throws ServiceRequestException
	 */
	private void recombinationToNew(List<FoundationObject> foList, boolean recombination, boolean isNeedId) throws ServiceRequestException
	{
		if (recombination && !SetUtils.isNullList(foList))
		{
			for (FoundationObject foun : foList)
			{
				// this.recombinationObjectField(foun, ReplaceSubstituteConstants.MasterItem.toUpperCase(), isNeedId);
				this.recombinationObjectField(foun, ReplaceSubstituteConstants.ComponentItem.toUpperCase(), isNeedId);
				this.recombinationObjectField(foun, ReplaceSubstituteConstants.RSItem.toUpperCase(), isNeedId);
			}
		}
	}

	private void recombinationObjectField(FoundationObject foun, String keyItem, boolean isNeedId) throws ServiceRequestException
	{
		ObjectGuid rsItem = this.getObjectGuid(foun, keyItem);
		if (rsItem != null)
		{
			FoundationObject fo = stubService.getBOAS().getObject(rsItem);
			if (fo != null)
			{
				foun.put(keyItem, fo.getObjectGuid().getGuid());
				foun.put(keyItem + "$NAME", fo.getFullName());
				if (isNeedId)
				{
					foun.put(keyItem + ReplaceSubstituteConstants.RSID, fo.getId());
					foun.put(keyItem + ReplaceSubstituteConstants.NAME, fo.getName());
					foun.put(keyItem + ReplaceSubstituteConstants.SPECIFICATION, fo.get("Specification"));
					foun.put(keyItem + ReplaceSubstituteConstants.REVISION, fo.getRevisionId());
				}
			}
		}
	}

	/**
	 * 是否存在相同全局的替代数据（不包含失效的数据）
	 */
	public boolean isHasCompentItemGlobalReplaceData(ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> globalReplaceList = this.getIdenticalGlobalReplaceData(componentItemObjectGuid, rsItemObjectGuid);
		if (SetUtils.isNullList(globalReplaceList))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * 获取元件和替代件相同的全局替代数据（不包含失效的数据）
	 */
	public List<FoundationObject> getIdenticalGlobalReplaceData(ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid) throws ServiceRequestException
	{
		return listReplaceDataInner(null, componentItemObjectGuid, rsItemObjectGuid, ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI, null, null, false);
	}

	/**
	 * 是否存在相同的局部的取替代数据（不包含失效的数据）
	 */
	public boolean isHasCompentItemPartReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomViewName,
			String bomKey) throws ServiceRequestException
	{
		List<FoundationObject> partReplaceList = this.getIdenticalPartReplaceData(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, bomViewName, bomKey);
		if (SetUtils.isNullList(partReplaceList))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * 是否存在相同的非失效的局部取替代数据
	 */
	public boolean isHasCompentItemPartReplaceDataExceptExpire(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomViewName,
			String bomKey) throws ServiceRequestException
	{
		List<FoundationObject> partReplaceList = this.getIdenticalPartReplaceData(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, bomViewName, bomKey);
		if (SetUtils.isNullList(partReplaceList))
		{
			return false;
		}
		else
		{
			for (FoundationObject replace : partReplaceList)
			{
				if (ReplaceSubstituteConstants.getReplaceDataStatus(replace) != ReplaceStatusEnum.EXPIRE)
				{
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 获取主件、元件和替代件相同的局部取替代数据（不包含失效的数据）
	 */
	public List<FoundationObject> getIdenticalPartReplaceData(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomViewName,
			String bomKey) throws ServiceRequestException
	{
		return listReplaceDataInner(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, ReplaceRangeEnum.PART, null, bomViewName, bomKey, false);
	}

	/**
	 * 
	 * @param rsItemObjectGuid
	 * @param range
	 * @param type
	 * @param isContainInvalidDate
	 *            是否包含失效
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listRepalcedByRsItem(ObjectGuid rsItemObjectGuid, ReplaceRangeEnum range, ReplaceTypeEnum type, boolean isContainInvalidDate)
			throws ServiceRequestException
	{
		return listReplaceDataInner(null, null, rsItemObjectGuid, range, type, null, null, isContainInvalidDate);
	}

	public List<FoundationObject> listReferenceItem(ObjectGuid componentItemObjectGuid) throws ServiceRequestException
	{
		List<FoundationObject> replaceDataList = listReplaceDataInner(null, componentItemObjectGuid, null, ReplaceRangeEnum.PART, null, null, null, true);
		if (SetUtils.isNullList(replaceDataList))
		{
			return null;
		}
		List<FoundationObject> rsItemList = new ArrayList<FoundationObject>();
		List<String> temp = new ArrayList<String>();
		for (FoundationObject replaceData : replaceDataList)
		{
			ObjectGuid rsItem = this.getObjectGuid(replaceData, ReplaceSubstituteConstants.RSItem);
			if (rsItem != null && !temp.contains(rsItem.getMasterGuid()))
			{
				FoundationObject fo = this.stubService.getBOAS().getObject(rsItem);
				if (fo != null)
				{
					temp.add(rsItem.getMasterGuid());
					rsItemList.add(fo);
				}
			}
		}
		return rsItemList;
	}

	/**
	 * 
	 * @param searchCondition
	 */
	protected void setResultFields(SearchCondition searchCondition)
	{
		searchCondition.addResultField(ReplaceSubstituteConstants.MasterItem);
		searchCondition.addResultField(ReplaceSubstituteConstants.ComponentItem);
		searchCondition.addResultField(ReplaceSubstituteConstants.RSItem);
		searchCondition.addResultField(ReplaceSubstituteConstants.Scope);
		searchCondition.addResultField(ReplaceSubstituteConstants.RSType);
	}

	/**
	 * 取得ObjectGuid对象
	 * 
	 * @param newfo
	 * @param componentitem
	 */
	public ObjectGuid getObjectGuid(DynaObject dynaObject, String sid)
	{
		if (!StringUtils.isNullString((String) dynaObject.get(sid + ReplaceSubstituteConstants.MASTER)))
		{
			ObjectGuid objectItem = new ObjectGuid((String) dynaObject.get(sid + ReplaceSubstituteConstants.ClassGuid),
					(String) dynaObject.get(sid + ReplaceSubstituteConstants.CLASSNAME), (String) dynaObject.get(sid),
					(String) dynaObject.get(sid + ReplaceSubstituteConstants.MASTER), null);
			objectItem.setIsMaster(true);
			return objectItem;
		}
		return null;
	}

	@Deprecated
	public List<FoundationObject> listReplaceDataByeffectInvalid(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ReplaceRangeEnum rang, ReplaceTypeEnum type,
			String bomViewName, String bomKey, String effectInvalid, boolean isAddOtherAttribute) throws ServiceRequestException
	{
		// List<FoundationObject> value = null;
		// SearchCondition searchCondition = this.createReplaceSearchCondition();
		// if (searchCondition == null)
		// {
		// return null;
		// }
		// searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		// this.setFilter(searchCondition, masterItemObjectGuid, componentItemObjectGuid, null, rang, type, bomViewName,
		// bomKey);
		// if (StringUtils.isNullString(effectInvalid) || "0".equalsIgnoreCase(effectInvalid))
		// {
		// searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, null, OperateSignEnum.ISNULL);
		// searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.ISNULL);
		// }
		// else if ("1".equalsIgnoreCase(effectInvalid))
		// {
		// searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, null, OperateSignEnum.NOTNULL);
		// searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.ISNULL);
		// }
		// else
		// {
		// searchCondition.addFilter(ReplaceSubstituteConstants.EffectiveDate, null, OperateSignEnum.NOTNULL);
		// searchCondition.addFilter(ReplaceSubstituteConstants.InvalidDate, null, OperateSignEnum.NOTNULL);
		// }
		// this.setResultFields(searchCondition);
		// searchCondition.addOrder(ReplaceSubstituteConstants.RSNumber, true);
		// value = this.stubService.getBOAS().listObject(searchCondition);
		// this.getDataBySearchNextPage(value, searchCondition);
		// this.recombinationToNew(value, true, isAddOtherAttribute);
		// return value;
		return null;
	}

}
