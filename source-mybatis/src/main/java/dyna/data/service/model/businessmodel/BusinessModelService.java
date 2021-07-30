/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BusinessModelService
 * xiasheng May 10, 2010
 */
package dyna.data.service.model.businessmodel;

import java.util.List;

import dyna.common.bean.model.bmbo.BusinessModel;
import dyna.common.bean.model.bmbo.BusinessObject;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.model.ModelService;

/**
 * 提供Business Model服务
 *
 * @author xiasheng
 */
public interface BusinessModelService extends ModelService
{
	/**
	 * 部署后检查业务对象，比如业务对象对应的类已删除，则此业务对象不再有意义，直接删除
	 * @throws ServiceRequestException
	 */
	void checkAndClearBusinessObject() throws ServiceRequestException;
	/**
	 * 重新加载缓存
	 *
	 * @throws ServiceRequestException
	 */
	void reloadModel() throws ServiceRequestException;

	/**
	 * 根据指定的业务模型的名字，取得对应的业务模型
	 *
	 * @param modelName
	 * @return
	 */
	BusinessModel getBusinessModel(String modelName);

	/**
	 * 根据指定的业务模型的名字，取得对应的业务模型基本信息
	 *
	 * @param modelName
	 * @return
	 */
	BMInfo getBMInfo(String modelName);

	/**
	 * 根据指定的业务模型的GUID，取得对应的业务模型
	 *
	 * @param modelName
	 * @return
	 */
	BusinessModel getBusinessModelByGuid(String modelGuid);

	/**
	 * 根据指定的业务模型的GUID，取得对应的业务模型基本信息
	 *
	 * @param modelGuid
	 * @return
	 */
	BMInfo getBMInfoByGuid(String modelGuid);

	/**
	 * 根据BOGUID取得BO的关联类的类名
	 *
	 * @param boGuid
	 * @return
	 */
	String getBOClassName(String boGuid);

	/**
	 * 获取共享业务模型
	 *
	 * @return
	 */
	BusinessModel getSharedBusinessModel();

	/**
	 * 获取共享业务模型
	 *
	 * @return
	 */
	BMInfo getSharedBusinessModelInfo();

	/**
	 * 取得共享模型中的所有业务对象基本信息
	 *
	 * @return
	 */
	List<BOInfo> listAllBOInfoInShareModel();

	/**
	 * 返回所有的业务模型
	 *
	 * @return
	 */
	List<BusinessModel> listBusinessModel();

	/**
	 * 返回所有的业务模型基本信息
	 *
	 * @return
	 */
	List<BMInfo> listBusinessModelInfo();

	/**
	 * 返回指定模型的所有业务对象基本信息
	 *
	 * @param bmGuid
	 * @return
	 */
	List<BOInfo> listAllBOInfoInBusinessModel(String bmGuid);

	/**
	 * 根据业务名称获取业务对象
	 *
	 * @param bomInfoName
	 * @return
	 */
	BusinessObject getBusinessObjectByName(String bmName ,String boInfoName);

	/**
	 * 根据业务名称获取业务对象基本信息
	 *
	 * @param boInfoName
	 * @return
	 */
	BOInfo getBOInfo(String bmName,String boInfoName);

	/**
	 * 根据业务guid获取业务对象
	 *
	 * @param boInfoGuid
	 * @return
	 */
	BusinessObject getBusinessObjectByGuid(String boInfoGuid);

	/**
	 * 根据业务guid获取业务对象基本信息
	 *
	 * @param boInfoGuid
	 * @return
	 */
	BOInfo getBOInfoByGuid(String boInfoGuid);

	/**
	 * 返回子阶业务对象基本信息
	 *
	 * @param boInfoGuid
	 * @return
	 */
	List<BOInfo> listChildBOInfo(String boInfoGuid);



	List<BOInfo> listRootBizObject(String modelName);
}
