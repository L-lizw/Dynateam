/**
 * Copyright(C) DCIS 版权所有。
 * 功能描述：business model definitions
 * 创建标识：Xiasheng , 2010-3-30
 **/

package dyna.data.service.model.businessmodel;

import java.util.List;
import java.util.stream.Collectors;

import dyna.common.bean.model.bmbo.BusinessModel;
import dyna.common.bean.model.bmbo.BusinessObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

/**
 * BusinessModelService服务的实现
 *
 * @author xiasheng
 */
public class BusinessModelServiceImpl extends DataRuleService implements BusinessModelService
{
	private BusinessModelServiceStub modelStub;

	public BusinessModelServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public BusinessModelServiceStub getModelStub()
	{
		if (this.modelStub == null)
		{
			this.modelStub = new BusinessModelServiceStub(this.serviceContext, this);
		}
		return this.modelStub;
	}

	@Override
	protected void init()
	{
		try
		{
			this.getModelStub().loadModel();
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void checkAndClearBusinessObject() throws ServiceRequestException
	{
		this.getModelStub().checkAndClearBusinessObject();
	}

	@Override
	public void reloadModel() throws ServiceRequestException
	{
		this.getModelStub().loadModel();
	}

	@Override
	public BusinessModel getBusinessModel(String modelName)
	{
		return this.getModelStub().getBusinessModelByName(modelName);
	}

	@Override
	public BMInfo getBMInfo(String modelName)
	{
		BusinessModel bm = this.getBusinessModel(modelName);
		return bm.getBmInfo();
	}

	@Override
	public BusinessModel getBusinessModelByGuid(String modelGuid)
	{
		return this.getModelStub().getBusinessModelByGuid(modelGuid);
	}

	@Override
	public BMInfo getBMInfoByGuid(String modelGuid)
	{
		BusinessModel bm = this.getBusinessModelByGuid(modelGuid);
		return bm == null ? null : bm.getBmInfo();
	}

	/**
	 * 获取共享的BusinessModel
	 *
	 * @return business model with shared modelName
	 */
	@Override
	public BusinessModel getSharedBusinessModel()
	{
		return this.getModelStub().getSharedBusinessModel();
	}

	@Override
	public BMInfo getSharedBusinessModelInfo()
	{
		BusinessModel bm = this.getSharedBusinessModel();
		return bm.getBmInfo();
	}

	@Override
	public List<BOInfo> listAllBOInfoInShareModel()
	{
		return this.getModelStub().listAllBOInfoInShareModel();
	}

	@Override
	public String getBOClassName(String boGuid)
	{
		return this.getModelStub().getBOClassName(boGuid);
	}

	@Override
	public List<BusinessModel> listBusinessModel()
	{
		return this.getModelStub().listBusinessModel();
	}

	@Override
	public List<BMInfo> listBusinessModelInfo()
	{
		List<BusinessModel> bmList = this.listBusinessModel();
		return bmList.stream().map(BusinessModel::getBmInfo).collect(Collectors.toList());
	}

	@Override
	public List<BOInfo> listAllBOInfoInBusinessModel(String bmGuid)
	{
		return this.getModelStub().listAllBOInfoInBusinessModel(bmGuid);
	}

	@Override
	public BusinessObject getBusinessObjectByName(String bmName, String boInfoName)
	{
		return this.getModelStub().getBusinessObjectByName(bmName, boInfoName);
	}

	@Override
	public BOInfo getBOInfo(String bmName, String boInfoName)
	{
		BusinessObject bo = this.getBusinessObjectByName(bmName, boInfoName);
		if (bo == null)
		{
			return null;
		}
		return bo.getBoInfo();
	}

	@Override
	public BusinessObject getBusinessObjectByGuid(String boInfoGuid)
	{
		return this.getModelStub().getBusinessObjectByGuid(boInfoGuid);
	}

	@Override
	public BOInfo getBOInfoByGuid(String boInfoGuid)
	{
		BusinessObject bo = this.getBusinessObjectByGuid(boInfoGuid);
		return bo.getBoInfo();
	}

	@Override
	public List<BOInfo> listChildBOInfo(String boInfoGuid)
	{
		BusinessObject bo = this.getBusinessObjectByGuid(boInfoGuid);
		List<BusinessObject> childList = bo.getChildList();
		if (childList != null)
		{
			return childList.stream().map(BusinessObject::getBoInfo).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public List<BOInfo> listRootBizObject(String modelName)
	{
		BusinessModel bm = this.getBusinessModel(modelName);
		List<BusinessObject> childList = bm.getBusinessObjectList();
		if (childList != null)
		{
			return childList.stream().filter(bo -> bo.getParent() != null).map(BusinessObject::getBoInfo).collect(Collectors.toList());
		}
		return null;
	}
}
