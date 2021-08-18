/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BMStub
 * Wanglei 2010-8-11
 */
package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.BusinessModelTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 与业务模型相关的操作分支
 *
 * @author Wanglei
 */
@Component
public class BMStub extends AbstractServiceStub<EMMImpl>
{

	protected BMInfo getCurrentBizModel() throws ServiceRequestException
	{
		return getBizModel(this.stubService.getUserSignature().getLoginGroupBMGuid());
	}

	public String getBizModelNameByGuid(String bmGuid) throws ServiceRequestException
	{
		BMInfo bmInfo = this.stubService.getDomainServiceStub().getBusinessModelService().getBMInfoByGuid(bmGuid);
		return bmInfo == null ? null : bmInfo.getName();
	}

	public List<BMInfo> listBizModel() throws ServiceRequestException
	{
		List<BMInfo> datalist = this.stubService.getDomainServiceStub().getBusinessModelService().listBusinessModelInfo();
		if (!SetUtils.isNullList(datalist))
		{
			for (int i = datalist.size() - 1; i > -1; i--)
			{
				if (datalist.get(i).isShareMode())
				{
					datalist.remove(i);
				}
			}
		}
		return datalist;
	}

	public BMInfo getBizModel(String bmGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getBusinessModelService().getBMInfoByGuid(bmGuid);
	}

	public BMInfo getBizModelByName(String bmName) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getBusinessModelService().getBMInfo(bmName);
	}

	public BMInfo getSharedBizModel() throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getBusinessModelService().getSharedBusinessModelInfo();
	}

	public List<BOInfo> listRootBizObjectByGuid(String modelName) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getBusinessModelService().listRootBizObject(modelName);
	}

	protected List<BOInfo> listSharedBizObject() throws ServiceRequestException
	{
		BMInfo model = this.stubService.getDomainServiceStub().getBusinessModelService().getSharedBusinessModelInfo();
		if (model == null)
		{
			return null;
		}
		return this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(model.getGuid());
	}

	public List<BOInfo> listBizObjectOfModelNonContainOthers(String modelName) throws ServiceRequestException
	{
		List<BOInfo> retList = new ArrayList<>();
		BMInfo modelInfo = this.stubService.getDomainServiceStub().getBusinessModelService().getBMInfo(modelName);
		if (modelInfo == null)
		{
			throw new ServiceRequestException("ID_APP_EMM_BM_NOT_EXIST", "not found business model: " + modelName, null, modelName);
		}
		List<BOInfo> businessObjectList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(modelInfo.getGuid());
		if (SetUtils.isNullList(businessObjectList))
		{
			return retList;
		}
		for (BOInfo bo : businessObjectList)
		{
			if (bo.getType() != BusinessModelTypeEnum.PACKAGE)
			{
				if (!StringUtils.isNullString(bo.getClassName()))
				{
					ClassInfo classInfo = this.stubService.getClassByName(bo.getClassName());

					if (!(classInfo.hasInterface(ModelInterfaceEnum.IBOMStructure) //
							|| classInfo.hasInterface(ModelInterfaceEnum.IStructureObject) //
							|| classInfo.hasInterface(ModelInterfaceEnum.IPM)//
							|| classInfo.hasInterface(ModelInterfaceEnum.IViewObject)//
							|| classInfo.hasInterface(ModelInterfaceEnum.IBOMView)//
							|| classInfo.hasInterface(ModelInterfaceEnum.INonQueryable) //
							|| classInfo.hasInterface(ModelInterfaceEnum.IReplaceSubstitute)//
					))
					{
						retList.add(bo);
					}
				}
			}
			else
			{
				retList.add(bo);
			}
		}
		return retList;
	}

	public List<BOInfo> listBizObjectOfCurrntModel() throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(this.stubService.getUserSignature().getLoginGroupBMGuid());
	}

	public List<BOInfo> listBizObjectOfModel(String modelName) throws ServiceRequestException
	{
		BMInfo modelInfo = this.stubService.getDomainServiceStub().getBusinessModelService().getBMInfo(modelName);
		if (modelInfo == null)
		{
			throw new ServiceRequestException("ID_APP_EMM_BM_NOT_EXIST", "not found business model: " + modelName, null, modelName);
		}
		return this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(modelInfo.getGuid());
	}

	public BOInfo getBizObject(String bmGuid, String classGuid, String classificationGuid) throws ServiceRequestException
	{
		if (classGuid == null)
		{
			return null;
		}

		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo businessObject : boInfoList)
			{
				if (classGuid.equals(businessObject.getClassGuid()) && (StringUtils.isNullString(classificationGuid) || classificationGuid
						.equals(businessObject.getClassificationGuid())))
				{
					return businessObject;
				}
			}
		}
		return null;
	}

	public BOInfo getBizObject(String bmGuid, String boGuid) throws ServiceRequestException
	{
		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo businessObject : boInfoList)
			{
				if (boGuid.equals(businessObject.getGuid()))
				{
					return businessObject;
				}
			}
		}
		return null;
	}

	public BOInfo getBoInfoByNameAndBM(String bmGuid, String boInfoName, boolean isThrowException) throws ServiceRequestException
	{
		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo businessObject : boInfoList)
			{
				if (boInfoName.equals(businessObject.getBOName()))
				{
					return businessObject;
				}
			}
		}
		if (isThrowException)
		{
			throw new ServiceRequestException("ID_APP_BUSINESS_OBJECT_NOT_EXIST", "not found business object for " + boInfoName, null, boInfoName);
		}
		else
		{
			return null;
		}
	}

	public BOInfo getBoInfoByNameAndBMName(String bmName, String boInfoName) throws ServiceRequestException
	{
		BMInfo bminfo = this.stubService.getDomainServiceStub().getBusinessModelService().getBMInfo(bmName);
		if (bminfo == null)
		{
			throw new ServiceRequestException("ID_APP_EMM_BM_NOT_EXIST", "not found business model: " + bmName, null, bmName);
		}
		return this.getBoInfoByNameAndBM(bminfo.getGuid(), boInfoName, true);
	}

	protected BOInfo getCurrentBoInfoByName(String boInfoName, boolean isThrowException) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getCurrentBizModel().getGuid();
		return getBoInfoByNameAndBM(bmGuid, boInfoName, isThrowException);
	}

	public BOInfo getCurrentBoInfoByClassName(String className) throws ServiceRequestException
	{
		if (className == null)
		{
			return null;
		}
		ClassInfo classInfo = this.stubService.getClassByName(className);
		if (classInfo == null)
		{
			return null;
		}

		String bmGuid = this.stubService.getCurrentBizModel().getGuid();
		return this.getBizObject(bmGuid, classInfo.getGuid(), null);
	}

	protected List<BOInfo> listAllSubBOInfoOnlyLeaf(String boName) throws ServiceRequestException
	{
		String bmGuid = this.getCurrentBizModel().getGuid();
		return listAllSubBOInfoOnlyLeafByBM(boName, bmGuid);
	}

	protected List<BOInfo> listAllSubBOInfoOnlyLeafByBM(String boName, String bmGuid) throws ServiceRequestException
	{
		BOInfo businessObject = null;
		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo boinfo : boInfoList)
			{
				if (boinfo.getName().equals(boName))
				{
					businessObject = boinfo;
					break;
				}
			}
		}
		if (businessObject == null)
		{
			return null;
		}

		List<BOInfo> retList = new ArrayList<>();
		List<ClassInfo> classInfoList = this.stubService.getClassStub().listAllSubClassInfoOnlyLeaf(null, businessObject.getClassName());
		if (SetUtils.isNullList(classInfoList))
		{
			retList.add(businessObject);
		}
		else
		{
			Set<String> classGuidSet = new HashSet<>();
			for (ClassInfo classinfo : classInfoList)
			{
				classGuidSet.add(classinfo.getGuid());
			}
			for (BOInfo boinfo : boInfoList)
			{
				if (classGuidSet.contains(boinfo.getClassGuid()))
				{
					retList.add(boinfo);
				}
			}
		}
		return retList;
	}

	protected List<BOInfo> listSubBOInfoByBM(String bmGuid, String boGuid)
	{
		List<BOInfo> resultList = new ArrayList<BOInfo>();
		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo boinfo : boInfoList)
			{
				if (boGuid.equals(boinfo.getParentBOGuid()))
				{
					resultList.add(boinfo);
				}
			}
		}
		return resultList;
	}

	protected List<BOInfo> listSubBOInfo(String boName) throws ServiceRequestException
	{
		BOInfo businessObject = null;
		String bmGuid = this.getCurrentBizModel().getGuid();
		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo boinfo : boInfoList)
			{
				if (boinfo.getName().equals(boName))
				{
					businessObject = boinfo;
					break;
				}
			}
		}
		if (businessObject == null)
		{
			return null;
		}

		List<BOInfo> retList = new ArrayList<>();
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo boinfo : boInfoList)
			{
				if (businessObject.getGuid().equals(boinfo.getParentBOGuid()))
				{
					retList.add(boinfo);
				}
			}
		}
		return retList;
	}

	protected List<BOInfo> listAllSubBOInfo(String boName, boolean containSelf) throws ServiceRequestException
	{
		String bmGuid = this.getCurrentBizModel().getGuid();
		return this.listAllSubBOInfo(boName, bmGuid, containSelf);
	}

	// 待优化
	protected List<BOInfo> listAllSubBOInfo(String boName, String bmGuid, boolean containSelf) throws ServiceRequestException
	{
		BOInfo businessObject = null;
		List<BOInfo> boInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);
		if (!SetUtils.isNullList(boInfoList))
		{
			for (BOInfo boinfo : boInfoList)
			{
				if (boinfo.getName().equals(boName))
				{
					businessObject = boinfo;
					break;
				}
			}
		}
		if (businessObject == null)
		{
			return null;
		}

		List<BOInfo> retList = new ArrayList<>();
		if (containSelf)
		{
			retList.add(businessObject);
		}
		if (businessObject.getClassGuid() != null)
		{
			List<String> classGuidList = this.stubService.getDomainServiceStub().getClassModelService().listAllSubClassGuid(businessObject.getClassGuid());
			Set<String> classGuidSet = new HashSet<>(classGuidList);
			for (BOInfo boinfo : boInfoList)
			{
				if (classGuidSet.contains(boinfo.getClassGuid()))
				{
					retList.add(boinfo);
				}
			}
		}
		return retList;
	}

	public List<BOInfo> listBOInfoByInterface(ModelInterfaceEnum interfaceEnum, String bmGuid) throws ServiceRequestException
	{
		List<ClassInfo> classObjectList = this.stubService.getDomainServiceStub().getClassModelService().getClassInfoListImplInterface(interfaceEnum);
		List<BOInfo> retBoInfoList;
		if (SetUtils.isNullList(classObjectList))
		{
			return null;
		}

		List<String> classGuidList = new ArrayList<>();
		for (ClassInfo classObject : classObjectList)
		{
			classGuidList.add(classObject.getGuid());
		}

		if (SetUtils.isNullList(classGuidList))
		{
			return null;
		}

		List<BOInfo> allBoInfoList = this.stubService.getDomainServiceStub().getBusinessModelService().listAllBOInfoInBusinessModel(bmGuid);

		retBoInfoList = new ArrayList<>();
		for (BOInfo boInfo : allBoInfoList)
		{
			if (classGuidList.contains(boInfo.getClassGuid()))
			{
				retBoInfoList.add(boInfo);
			}
		}

		return retBoInfoList;
	}

	protected List<BOInfo> listBOInfoByInterfaceAndBM(List<String> classNameList, ModelInterfaceEnum interfaceEnum, String bmGuid) throws ServiceRequestException
	{
		List<ClassInfo> classObjectList = this.stubService.getDomainServiceStub().getClassModelService().getClassInfoListImplInterface(interfaceEnum);
		List<BOInfo> retBoInfoList;
		if (SetUtils.isNullList(classObjectList))
		{
			return null;
		}

		List<String> boInfoList = new ArrayList<>();
		for (ClassInfo classObject : classObjectList)
		{
			if (classNameList != null && classNameList.contains(classObject.getName()))
			{
				continue;
			}
			BOInfo boInfo = this.getBizObject(bmGuid, classObject.getGuid(), null);

			if (boInfo != null)
			{
				boInfoList.add(boInfo.getGuid());

				if (classNameList != null)
				{
					classNameList.add(classObject.getName());
				}
			}
		}
		String bmName = this.getBizModelNameByGuid(bmGuid);
		List<BOInfo> allBoInfoList = listBizObjectOfModel(bmName);

		retBoInfoList = new ArrayList<>();
		for (BOInfo boInfo : allBoInfoList)
		{
			if (boInfoList.contains(boInfo.getGuid()))
			{
				retBoInfoList.add(boInfo);
			}
		}

		return retBoInfoList;
	}

	protected List<String> getSuperSelfBONameList(String classguid) throws ServiceRequestException
	{
		if (classguid == null)
		{
			return null;
		}
		List<String> boNameList = new ArrayList<String>();

		BOInfo boInfo = this.getBizObject(this.getCurrentBizModel().getGuid(), classguid, null);
		if (boInfo != null)
		{
			boNameList.add(boInfo.getName());
			if (boInfo.getParent() != null)
			{
				this.resucureBOInfo(boNameList, boInfo.getParent());
			}
		}
		return boNameList;
	}

	private void resucureBOInfo(List<String> boNameList, String boInfoName) throws ServiceRequestException
	{
		if (boInfoName == null)
		{
			return;
		}

		BOInfo boInfo = this.stubService.getCurrentBoInfoByName(boInfoName, true);
		if (boInfo != null)
		{
			boNameList.add(boInfo.getName());

			if (boInfo.getParent() != null)
			{
				this.resucureBOInfo(boNameList, boInfo.getParent());
			}
		}
	}

}
