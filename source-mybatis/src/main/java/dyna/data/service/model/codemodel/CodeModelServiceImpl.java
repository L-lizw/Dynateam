/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeModelServiceImpl
 * Jiagang 2010-9-7
 */
package dyna.data.service.model.codemodel;

import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.bean.model.ui.ClassificationUIObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.UITypeEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CodeModelService服务的实现
 * 
 * @author Jiagang
 * 
 */
public class CodeModelServiceImpl extends DataRuleService implements CodeModelService
{
	private CodeModelServiceStub modelStub;

	public CodeModelServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
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

	public CodeModelServiceStub getModelStub()
	{
		return this.modelStub;
	}

	@Override
	public void reloadModel() throws ServiceRequestException
	{
		this.getModelStub().reLoadModel();
	}

	@Override
	public void clearClassification()
	{
		this.getModelStub().clearClassification();
	}

	@Override
	public CodeObject getCodeObjectByGuid(String codeObjectGuid)
	{
		return this.getModelStub().getCodeObjectByGuid(codeObjectGuid);
	}

	@Override
	public CodeItem getCodeItemByGuid(String guid) throws ServiceRequestException
	{
		return this.getModelStub().getCodeItemByGuid(guid);
	}

	@Override
	public CodeObject getCodeObject(String name)
	{
		return this.getModelStub().getCodeObject(name);
	}

	@Override
	public List<CodeObject> listAllCodeObjectList()
	{
		return this.getModelStub().listAllCodeObjectList();
	}

	@Override
	public CodeItem getCodeItem(String codeName, String itemName)
	{
		return this.getModelStub().getCodeItem(codeName, itemName);
	}

	@Override
	public List<String> listAllParentClassificationGuid(String classificationItemGuid) throws ServiceRequestException
	{
		return this.getModelStub().listAllParentClassificationGuid(classificationItemGuid);
	}

	@Override
	public List<String> listAllSubClassificationGuid(String classificationItemGuid) throws ServiceRequestException
	{
		return this.getModelStub().listAllSubClassificationGuid(classificationItemGuid);
	}

	@Override
	public List<CodeItem> listAllCodeItem(String codeName)
	{
		return this.getModelStub().listAllCodeItem(codeName);
	}

	@Override
	public List<CodeItemInfo> listDetailCodeItemInfo(String codeGuid, String codeItemGuid)
	{
		return this.getModelStub().listDetailCodeItemInfo(codeGuid, codeItemGuid);
	}

	@Override
	public CodeObjectInfo getCodeObjectInfoByGuid(String guid)
	{
		CodeObject codeObject = this.getCodeObjectByGuid(guid);
		return codeObject == null ? null : codeObject.getInfo();
	}

	@Override
	public CodeObjectInfo getCodeObjectInfo(String name)
	{
		CodeObject codeObject = this.getCodeObject(name);
		return codeObject == null ? null : codeObject.getInfo();
	}

	@Override
	public CodeItemInfo getCodeItemInfoByGuid(String guid) throws ServiceRequestException
	{
		CodeItem codeItem = this.getCodeItemByGuid(guid);
		return codeItem == null ? null : codeItem.getInfo();
	}

	@Override
	public CodeItemInfo getCodeItemInfo(String codeName, String itemName)
	{
		CodeItem codeItem = this.getCodeItem(codeName, itemName);
		return codeItem == null ? null : codeItem.getInfo();
	}

	@Override
	public List<CodeObjectInfo> listAllCodeInfoList()
	{
		List<CodeObject> codeObjectList = this.listAllCodeObjectList();
		return codeObjectList.stream().map(CodeObject::getInfo).collect(Collectors.toList());
	}

	@Override
	public List<CodeItemInfo> listAllCodeItemInfoList()
	{
		List<CodeItem> codeItemList = this.getModelStub().listAllCodeItem();
		return codeItemList.stream().map(CodeItem::getInfo).collect(Collectors.toList());
	}

	@Override
	public List<ClassField> listField(String codeName, String codeItemName) throws ServiceRequestException
	{
		CodeItem codeItem = this.getCodeItem(codeName, codeItemName);
		return this.listField(codeItem.getGuid());
	}

	@Override
	public ClassField getField(String codeName, String codeItemName, String fieldName) throws ServiceRequestException
	{
		CodeItem codeItem = this.getCodeItem(codeName, codeItemName);
		return codeItem.getField(fieldName);
	}

	@Override
	public List<ClassField> listField(String codeItemGuid) throws ServiceRequestException
	{
		return this.getModelStub().listField(codeItemGuid);
	}

	@Override
	public List<ClassificationUIInfo> listAllUIObject(String codeName, String codeItemName) throws ServiceRequestException
	{
		CodeItem codeItem = this.getCodeItem(codeName, codeItemName);
		List<ClassificationUIObject> uiObjectList = this.getModelStub().listUIObject(codeItem.getGuid());
		if (uiObjectList != null)
		{
			return uiObjectList.stream().map(ClassificationUIObject::getInfo).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public List<ClassificationUIInfo> listAllUIObject(String codeName, String codeItemName, UITypeEnum uiType) throws ServiceRequestException
	{
		List<ClassificationUIInfo> uiObjectInfoList = this.listAllUIObject(codeName, codeItemName);
		if (uiObjectInfoList != null)
		{
			return uiObjectInfoList.stream().filter(ui -> ui.getType() == uiType.name()).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public List<ClassificationUIInfo> listAllUIObject(String codeItemGuid) throws ServiceRequestException
	{
		CodeItem codeItem = this.getCodeItemByGuid(codeItemGuid);
		return this.listAllUIObject(codeItem.getMasterName(), codeItem.getName());
	}

	@Override
	public List<UIField> listUIField(String uiGuid) throws ServiceRequestException
	{
		return this.getModelStub().listUIField(uiGuid);
	}

	@Override
	public ClassField getFieldByItemGuid(String codeItemgGuid, String fieldName) throws ServiceRequestException
	{
		CodeItem codeItem = this.getCodeItemByGuid(codeItemgGuid);
		return codeItem.getField(fieldName);
	}
}
