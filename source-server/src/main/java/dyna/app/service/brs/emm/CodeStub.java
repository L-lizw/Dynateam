/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeStub
 * Wanglei 2010-8-11
 */
package dyna.app.service.brs.emm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * 与code 管理相关的操作分支
 * 
 * @author Wanglei
 * 
 */
public class CodeStub extends AbstractServiceStub<EMMImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public CodeStub(ServiceContext context, EMMImpl service)
	{
		super(context, service);
	}

	protected void init(boolean hasLicense) throws ServiceRequestException
	{
		if (!hasLicense)
		{
			this.stubService.getDomainServiceStub().getCodeModelService().clearClassification();
		}
	}

	protected CodeObjectInfo getCodeInfo(String codeGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getCodeModelService().getCodeObjectInfoByGuid(codeGuid);
	}

	protected CodeObjectInfo getCodeByName(String codeName) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getCodeModelService().getCodeObjectInfo(codeName);
	}

	protected CodeItemInfo getCodeItemInfo(String codeItemInfoGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getCodeModelService().getCodeItemInfoByGuid(codeItemInfoGuid);
	}

	protected CodeItemInfo getCodeItemInfoByName(String codeName, String codeItemName) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getCodeModelService().getCodeItemInfo(codeName, codeItemName);
	}

	protected List<CodeItemInfo> listAllCodeItem(String codeGuid, String codeName) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(codeGuid))
		{
			CodeObjectInfo codeObject = this.getCodeByName(codeName);
			if (codeObject == null)
			{
				return null;
			}
			codeGuid = codeObject.getGuid();
		}
		if (!StringUtils.isGuid(codeGuid))
		{
			return null;
		}

		List<CodeItemInfo> codeItemInfoList = null;
		List<CodeItemInfo> codeItemList = this.stubService.getDomainServiceStub().getCodeModelService().listDetailCodeItemInfo(codeGuid, null);
		if (!SetUtils.isNullList(codeItemList))
		{
			codeItemInfoList = new ArrayList<>();
			for (CodeItemInfo codeItem : codeItemList)
			{
				codeItemInfoList.add(codeItem);
				List<CodeItemInfo> codeItemInfos = this.listAllSubCodeItemInfoByDatail(codeItem.getGuid(), false);
				if (!SetUtils.isNullList(codeItemInfos))
				{
					codeItemInfoList.addAll(codeItemInfos);
				}
			}
		}

		return codeItemInfoList;
	}

	protected List<CodeItemInfo> listSubCodeItemForMaster(String codeGuid, String codeName) throws ServiceRequestException
	{
		CodeObjectInfo codeObject = null;
		if (!StringUtils.isNullString(codeGuid))
		{
			codeObject = this.getCodeInfo(codeGuid);
		}
		else if (!StringUtils.isNullString(codeName))
		{
			codeObject = this.getCodeByName(codeName);
		}
		if (codeObject != null)
		{
			return this.stubService.getDomainServiceStub().getCodeModelService().listDetailCodeItemInfo(codeObject.getGuid(), null);
		}
		return null;
	}

	protected List<CodeItemInfo> listSubCodeItemForDetail(String codeItemGuid) throws ServiceRequestException
	{
		CodeItemInfo codeItem = null;
		if (!StringUtils.isNullString(codeItemGuid))
		{
			codeItem = this.getCodeItemInfo(codeItemGuid);
		}
		if (codeItem != null)
		{
			return this.stubService.getDomainServiceStub().getCodeModelService().listDetailCodeItemInfo(null, codeItem.getGuid());
		}
		return null;
	}

	public List<CodeItemInfo> listAllSubCodeItemInfoByDatail(String codeItemGuid, boolean containSelf) throws ServiceRequestException
	{
		List<CodeItemInfo> codeItemList = new ArrayList<>();
		if (containSelf)
		{
			codeItemList.add(this.getCodeItemInfo(codeItemGuid));
		}
		this.listAllChildCodeItemByGuid(codeItemGuid, codeItemList);
		return codeItemList;
	}

	public List<CodeItemInfo> listLeafCodeItemInfoByMaster(String codeGuid, String codeName) throws ServiceRequestException
	{
		List<CodeItemInfo> returnList = new ArrayList<>();
		List<CodeItemInfo> childList = this.listSubCodeItemForMaster(codeGuid, codeName);
		if (childList != null)
		{
			for (CodeItemInfo info : childList)
			{
				returnList.addAll(this.listLeafCodeItemInfoByDatail(info.getGuid()));
			}
		}
		return returnList;
	}

	public List<CodeItemInfo> listLeafCodeItemInfoByDatail(String codeItemGuid) throws ServiceRequestException
	{
		List<CodeItemInfo> returnList = new ArrayList<>();
		List<CodeItemInfo> childList = this.listSubCodeItemForDetail(codeItemGuid);
		if (SetUtils.isNullList(childList))
		{
			returnList.add(this.getCodeItemInfo(codeItemGuid));
		}
		else
		{
			for (CodeItemInfo info : childList)
			{
				returnList.addAll(this.listLeafCodeItemInfoByDatail(info.getGuid()));
			}
		}
		return returnList;
	}

	private void listAllChildCodeItemByGuid(String codeItemGuid, List<CodeItemInfo> codeItemList) throws ServiceRequestException
	{
		List<CodeItemInfo> newCodeItemList = this.stubService.getDomainServiceStub().getCodeModelService().listDetailCodeItemInfo(null, codeItemGuid);

		if (!SetUtils.isNullList(newCodeItemList))
		{
			codeItemList.addAll(newCodeItemList);

			for (CodeItemInfo codeItm : newCodeItemList)
			{
				this.listAllChildCodeItemByGuid(codeItm.getGuid(), codeItemList);
			}
		}

	}

	public List<CodeItemInfo> listAllSuperCodeItemInfo(String codeItemGuid) throws ServiceRequestException
	{
		CodeItemInfo iteminfo = this.getCodeItemInfo(codeItemGuid);
		List<CodeItemInfo> returnList = new ArrayList<>();
		while (!StringUtils.isNullString(iteminfo.getParentGuid()))
		{
			iteminfo = this.getCodeItemInfo(iteminfo.getParentGuid());
			returnList.add(iteminfo);
		}
		return returnList;
	}

	protected List<CodeObjectInfo> listCode() throws ServiceRequestException
	{
		List<CodeObjectInfo> codeObjectList = this.stubService.getDomainServiceStub().getCodeModelService().listAllCodeInfoList();
		return codeObjectList;
	}

	protected Set<String> getAllSuperCodeItemGuidSet(String classificationItemGuid) throws ServiceRequestException
	{
		Set<String> returnSet = new HashSet<String>();
		returnSet.add(classificationItemGuid);
		List<CodeItemInfo> list = this.stubService.listAllSuperCodeItemInfo(classificationItemGuid);
		if (list != null)
		{
			for (CodeItemInfo info : list)
			{
				returnSet.add(info.getGuid());
			}
		}
		return returnSet;
	}

}
