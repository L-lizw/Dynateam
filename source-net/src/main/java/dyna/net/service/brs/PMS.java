/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECS Engineering Change Service 工程变更服务
 * caogc 2010-11-02
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.ItemProduct;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * PMS PRODUCT MANAGE SERVICE 产品管理服务
 * 
 * @author caogc
 * 
 */
public interface PMS extends Service
{
	/**
	 * 批量添加物料的产品的List列表 <br>
	 * 将产品添加到物料的"产品列表"中<br>
	 * 
	 * 特别需要注意的是 ：如果item是非精确的 那么itemObjectGuid中Guid为空
	 * 
	 * @param itemObjectGuid
	 * @param productObjectGuidList
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public void addToProduct(ObjectGuid itemObjectGuid, List<ObjectGuid> productObjectGuidList) throws ServiceRequestException;

	/**
	 * 批量根据itemProduct的guid的list删除itemProduct对象列表
	 * 
	 * @param itemProductGuidList
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteItemProduct(List<String> itemProductGuidList) throws ServiceRequestException;

	/**
	 * 根据item的信息将item对应的ItemProduct数据清除
	 * 
	 * @param itemObjectGuid
	 * @throws ServiceRequestException
	 */
	public void deleteItemProductByItem(ObjectGuid itemObjectGuid) throws ServiceRequestException;

	/**
	 * 获取指定物料的所有的产品List列表
	 * 
	 * @param itemObjectGuid
	 * @return ItemProduct列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ItemProduct> listItemProductByItem(ObjectGuid itemObjectGuid) throws ServiceRequestException;

	/**
	 * 查询产品中Summary数据列表
	 * 
	 * @param productObjectGuid
	 * @param searchCondition
	 * @param viewList
	 *            关联关系视图的模板名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String viewName) throws ServiceRequestException;

	/**
	 * 查询产品中Summary关系模板
	 * 
	 * @param productObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listProductSummaryRelationTemplate(ObjectGuid productObjectGuid) throws ServiceRequestException;

}
