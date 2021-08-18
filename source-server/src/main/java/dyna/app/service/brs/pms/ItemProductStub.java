/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECTrackStub
 * Caogc 2011-4-6
 */
package dyna.app.service.brs.pms;

import dyna.app.core.sch.Scheduler;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.helper.ListProductSummaryUtil;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.DataRule;
import dyna.common.dto.Folder;
import dyna.common.dto.ItemProduct;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Caogc
 * 
 */
@Component
public class ItemProductStub extends AbstractServiceStub<PMSImpl>
{

	/**
	 * 根据item的信息将item对应的ItemProduct数据清除
	 * 
	 * @param itemObjectGuid
	 * @throws ServiceRequestException
	 */
	protected void deleteItemProductByItem(ObjectGuid itemObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (itemObjectGuid.getGuid() == null && itemObjectGuid.getMasterGuid() == null)
		{
			return;
		}

		if (itemObjectGuid.getGuid() == null)
		{
			itemObjectGuid.setGuid("1");
		}

		Map<String, Object> filter = new HashMap<String, Object>();

		filter.put(ItemProduct.ITEM_GUID, itemObjectGuid.getGuid());

		filter.put(ItemProduct.ITEM_CLASS_GUID, itemObjectGuid.getClassGuid());

		filter.put(ItemProduct.ITEM_MASTER_GUID, itemObjectGuid.getMasterGuid());

		try
		{
			sds.delete(ItemProduct.class, filter, "deleteItemProductByItem");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteItemProduct(List<String> itemProductGuidList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (SetUtils.isNullList(itemProductGuidList))
		{
			return;
		}
		try
		{
			for (String itemProductGuid : itemProductGuidList)
			{
				sds.delete(ItemProduct.class, itemProductGuid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ItemProduct> listItemProductByItem(ObjectGuid itemObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<ItemProduct> itemProductList = null;

		if (itemObjectGuid.getGuid() == null && itemObjectGuid.getMasterGuid() == null)
		{
			return null;
		}

		if (itemObjectGuid.getGuid() == null)
		{
			itemObjectGuid.setGuid("1");
		}

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ItemProduct.ITEM_GUID, itemObjectGuid.getGuid());
			filter.put(ItemProduct.ITEM_CLASS_GUID, itemObjectGuid.getClassGuid());
			filter.put(ItemProduct.ITEM_MASTER_GUID, itemObjectGuid.getMasterGuid());
			itemProductList = sds.query(ItemProduct.class, filter, "getProductByItem");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return itemProductList;
	}

	public void addToProduct(ObjectGuid itemObjectGuid, List<ObjectGuid> productObjectGuidList) throws ServiceRequestException
	{

		if (SetUtils.isNullList(productObjectGuidList))
		{
			return;
		}

		if (itemObjectGuid.getGuid() == null && itemObjectGuid.getMasterGuid() == null)
		{
			return;
		}

		if (itemObjectGuid.getGuid() == null)
		{
			itemObjectGuid.setGuid("1");
		}

		// String sessionId = this.stubService.getSignature().getCredential();
		String operatorGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
		SystemDataService sds = DataServer.getSystemDataService();
		ItemProduct itemProduct = new ItemProduct();
		itemProduct.setItemGuid(itemObjectGuid.getGuid());
		itemProduct.setItemClassGuid(itemObjectGuid.getClassGuid());
		itemProduct.setItemMasterGuid(itemObjectGuid.getMasterGuid());
		itemProduct.put("UPDATEUSER", operatorGuid);
		itemProduct.put("CREATEUSER", operatorGuid);

		try
		{
			// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			for (ObjectGuid productObjectGuid : productObjectGuidList)
			{
				itemProduct.setGuid(null);
				itemProduct.setProductGuid(productObjectGuid.getGuid());
				itemProduct.setProductClassGuid(productObjectGuid.getClassGuid());
				itemProduct.setProductMasterGuid(productObjectGuid.getMasterGuid());

				sds.save(itemProduct);
			}

			// DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
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
		finally
		{
		}
	}

	public List<FoundationObject> listProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String viewname, boolean isCheckAuth)
			throws ServiceRequestException
	{
		List<FoundationObject> results = null;
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
			if (schedule == null)
			{
				this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
				schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
			}
			ListProductSummaryUtil util = new ListProductSummaryUtil(this.stubService.getBOMS(), this.stubService.getBOAS(), this.stubService.getEMM(), schedule);
			results = util.listProductSummaryObject(productObjectGuid, viewname, searchCondition, new DataRule());

			if (SetUtils.isNullList(results))
			{
				return results;
			}

			EMM emm = this.stubService.getEMM();

			Set<String> fieldNames = searchCondition == null ? null : emm.getObjectFieldNamesInSC(searchCondition);
			Set<String> fieldCodeNames = searchCondition == null ? null : emm.getCodeFieldNamesInSC(searchCondition);
			Folder folder = searchCondition == null ? null : searchCondition.getFolder();

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			for (FoundationObject fObject : results)
			{
				DecoratorFactory.decorateFoundationObject(fieldNames, fObject, emm, bmGuid, folder);
				DecoratorFactory.decorateFoundationObjectCode(fieldCodeNames, fObject, emm, bmGuid);
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
		finally
		{
		}
	}

	public List<RelationTemplateInfo> listProductSummaryRelationTemplate(ObjectGuid productObjectGuid) throws ServiceRequestException
	{
		List<BOInfo> bomEnd2InfoList = this.stubService.getEMM().listBizObjectByInterface(ModelInterfaceEnum.IItem);
		List<RelationTemplate> returnList = new ArrayList<RelationTemplate>();
		Map<String, RelationTemplate> tempNameMap = new HashMap<String, RelationTemplate>();
		for (BOInfo boinfo : bomEnd2InfoList)
		{
			List<RelationTemplateInfo> list = ((EMMImpl) this.stubService.getEMM()).getRelationTemplateStub().listRelationTemplate(null, null, null, boinfo.getGuid(), false);
			if (list != null)
			{
				for (RelationTemplateInfo temp : list)
				{
					List<RelationTemplateEnd2> relationTemplateEnd2List = ((EMMImpl) this.stubService.getEMM()).getRelationTemplateStub().listRelationTemplateEnd2(temp.getGuid());
					if (!SetUtils.isNullList(relationTemplateEnd2List))
					{
						RelationTemplate retTemp = null;
						if (!tempNameMap.containsKey(temp.getName()))
						{
							retTemp = new RelationTemplate((RelationTemplateInfo) temp.clone());
							retTemp.setRelationTemplateEnd2List(new LinkedList<RelationTemplateEnd2>());
							returnList.add(retTemp);
							tempNameMap.put(temp.getName(), retTemp);
						}
						retTemp = tempNameMap.get(temp.getName());
						for (RelationTemplateEnd2 end2 : relationTemplateEnd2List)
						{
							boolean isAdd = true;
							BOInfo end2Info = this.getBOInfo(end2.getEnd2BoName());
							if (end2Info != null)
							{
								String end2ClassName = end2Info.getClassName();
								for (RelationTemplateEnd2 cend2 : retTemp.getRelationTemplateEnd2List())
								{
									String cend2ClassName = (String) cend2.get("END2CLASSNAME");
									if (end2ClassName.equalsIgnoreCase(cend2ClassName))
									{
										isAdd = false;
										break;
									}
									else if (this.stubService.getEMM().isSuperClass(end2ClassName, cend2ClassName))
									{
										isAdd = false;
										break;
									}
									else if (this.stubService.getEMM().isSuperClass(cend2ClassName, end2ClassName))
									{
										cend2.putAll(end2);
										cend2.put("END2CLASSNAME", end2ClassName);
										isAdd = false;
										break;
									}
								}
								if (isAdd)
								{
									RelationTemplateEnd2 nend2 = (RelationTemplateEnd2) end2.clone();
									nend2.put("END2CLASSNAME", end2ClassName);
									retTemp.getRelationTemplateEnd2List().add(nend2);
								}
							}
						}
					}
				}
			}
		}
		for (int i = returnList.size() - 1; i > -1; i--)
		{
			RelationTemplate retTemp = returnList.get(i);
			if (SetUtils.isNullList(retTemp.getRelationTemplateEnd2List()))
			{
				returnList.remove(i);
				continue;
			}
			String rootClassName = null;
			if (retTemp.getRelationTemplateEnd2List().size() == 1)
			{
				rootClassName = (String) retTemp.getRelationTemplateEnd2List().get(0).get("END2CLASSNAME");
			}
			else
			{
				rootClassName = (String) retTemp.getRelationTemplateEnd2List().get(0).get("END2CLASSNAME");
				boolean isLockRoot = true;
				while (isLockRoot)
				{
					isLockRoot = false;
					for (RelationTemplateEnd2 end2 : retTemp.getRelationTemplateEnd2List())
					{
						String end2ClassName = (String) end2.get("END2CLASSNAME");
						if (!this.stubService.getEMM().isSuperClass(end2ClassName, rootClassName))
						{
							rootClassName = this.stubService.getEMM().getClassByName(rootClassName).getSuperclass();
							isLockRoot = true;
							break;
						}
					}
				}
			}
			if (this.stubService.getEMM().getClassByName(rootClassName).isCreateTable() == false)
			{
				returnList.remove(i);
				continue;
			}
			retTemp.put("END2CLASSNAME", rootClassName);
		}

		for (RelationTemplate retTemp : returnList)
		{
			for (int i = retTemp.getRelationTemplateEnd2List().size() - 1; i > -1; i--)
			{
				RelationTemplateEnd2 end2 = retTemp.getRelationTemplateEnd2List().get(i);
				List<BOInfo> list = this.stubService.getEMM().listAllSubBOInfo(end2.getEnd2BoName());
				if (list != null && list.size() > 1)
				{
					int y = i + 1;
					for (BOInfo info : list)
					{
						RelationTemplateEnd2 nend2 = (RelationTemplateEnd2) end2.clone();
						nend2.setEnd2BoName(info.getName());
						nend2.put(RelationTemplateEnd2.END2_BO_TITLE, info.getTitle());
						nend2.put("END2CLASSNAME", info.getClassName());
						if (y < retTemp.getRelationTemplateEnd2List().size())
						{
							retTemp.getRelationTemplateEnd2List().add(y, nend2);
						}
						else
						{
							retTemp.getRelationTemplateEnd2List().add(nend2);
						}
						y++;
					}
				}
			}

		}

		if (returnList != null)
		{
			return returnList.stream().map(RelationTemplate::getInfo).collect(Collectors.toList());
		}

		return null;
	}

	private BOInfo getBOInfo(String boName)
	{
		BOInfo boInfo = null;
		try
		{
			boInfo = this.stubService.getEMM().getCurrentBoInfoByName(boName, true);
		}
		catch (ServiceRequestException e)
		{
		}
		return boInfo;
	}
}