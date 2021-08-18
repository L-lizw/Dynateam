/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UIStub
 * Wanglei 2010-8-11
 */
package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 与 ui model 相关的操作分支
 * 
 * @author Wanglei
 * 
 */
@Component
public class UIStub extends AbstractServiceStub<EMMImpl>
{

	public static List<String>	NormalTableUINameList	= Arrays.asList("LISTUIFORSTRUCTURE1", "LISTUIFORSTRUCTURE2", "LISTUIFORSTRUCTURE3", "LISTUIFORSTRUCTURE4",
			"LISTUIFORSTRUCTURE5", "LISTUIFORSTRUCTURE6", "LISTUIFORSTRUCTURE7", "LISTUIFORSTRUCTURE7", "LISTUIFORSTRUCTURE8", "LISTUIFORSTRUCTURE9");
	public static List<String>	BomTreeUINameList		= Arrays.asList("ListUIForBOM1", "ListUIForBOM2", "ListUIForBOM3", "ListUIForBOM4", "ListUIForBOM5", "ListUIForBOM6",
			"ListUIForBOM7", "ListUIForBOM8", "ListUIForBOM9");


	/**
	 * 获取当前业务模型内, 指定UI类型的UI对象(如UI对象存在多个, 则默认取第一个)<br>
	 * 
	 * <b>
	 * 注意：该方法不支持FORM类型的UI
	 * </b>
	 * 
	 * @param classObjectName
	 *            类名
	 * @param uiType
	 *            UI类型
	 * @return UI对象
	 * @throws ServiceRequestException
	 */
	public UIObjectInfo getUIObjectByBizModel(String classObjectName, UITypeEnum uiType) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getCurrentBizModel().getGuid();
		return this.getUIObjectByBizModel(classObjectName, bmGuid, uiType);
	}

	/**
	 * 获取指定业务模型内, 指定UI类型的UI对象(如UI对象存在多个, 则默认取第一个)
	 * 
	 * @param classObjectName
	 * @param bmGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	public UIObjectInfo getUIObjectByBizModel(String classObjectName, String bmGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		UIObjectInfo uiObject = null;
		List<UIObjectInfo> uiList = this.listUIObjectByBizModel(classObjectName, bmGuid, uiType, true);
		if (!SetUtils.isNullList(uiList))
		{
			uiObject = uiList.get(0);
		}
		return uiObject;
	}

	protected List<UIAction> listUIAction(String uiGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getClassModelService().listUIAction(uiGuid);
	}

	protected List<UIField> listUIField(String uiGuid)
	{
		List<UIField> listUIField = this.stubService.getDomainServiceStub().getClassModelService().listUIField(uiGuid);
		return listUIField;
	}

	protected List<UIField> listUIField(String classGuid, String uiGuid)
	{

		List<UIField> uiFieldList = this.listUIField(uiGuid);
		if (uiFieldList != null)
		{
			uiFieldList.forEach(uiField -> uiField.setClassGuid(classGuid));
		}
		return uiFieldList;
	}

	protected List<UIField> listUIFieldByUIObject(String classObjectName, String uiObjectName) throws ServiceRequestException
	{
		List<UIObjectInfo> uiObjectList = this.stubService.getDomainServiceStub().getClassModelService().listUIObjectInfo(classObjectName);
		if (!SetUtils.isNullList(uiObjectList))
		{
			Optional<UIObjectInfo> optional = uiObjectList.stream().filter(ui -> ui.getName().equals(uiObjectName)).findFirst();
			if (optional.isPresent())
			{
				UIObjectInfo uiObjectInfo = optional.get();
				List<UIField> uiFieldList = this.stubService.getDomainServiceStub().getClassModelService().listUIField(uiObjectInfo.getGuid());
				if (uiFieldList != null)
				{
					uiFieldList.forEach(uiField -> uiField.setClassGuid(uiObjectInfo.getClassGuid()));
				}
				return uiFieldList;
			}
			else
			{
				throw new ServiceRequestException("ID_APP_NO_FOUND_UI_OBJECT", "not found UI object: " + classObjectName + " " + uiObjectName, null, classObjectName, uiObjectName);
			}
		}
		else
		{
			throw new ServiceRequestException("ID_APP_NO_FOUND_UI_OBJECT", "not found UI object: " + classObjectName + " " + uiObjectName, null, classObjectName, uiObjectName);
		}
	}

	protected UIField getUIFieldByName(String classObjectName, String fieldName) throws ServiceRequestException
	{
		List<UIField> uiFieldList = this.stubService.listFormUIField(classObjectName);
		UIField uiField = this.getUIFieldByName(uiFieldList, fieldName);
		if (uiField != null)
		{
			return uiField;
		}
		uiFieldList = this.stubService.listListUIField(classObjectName);
		uiField = this.getUIFieldByName(uiFieldList, fieldName);
		if (uiField != null)
		{
			return uiField;
		}
		uiFieldList = this.stubService.listSectionUIField(classObjectName);
		uiField = this.getUIFieldByName(uiFieldList, fieldName);
		if (uiField != null)
		{
			return uiField;
		}
		uiFieldList = this.stubService.listReportUIField(classObjectName);
		return this.getUIFieldByName(uiFieldList, fieldName);
	}

	private UIField getUIFieldByName(List<UIField> uiFieldList, String fieldName) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(uiFieldList))
		{
			for (UIField uiField : uiFieldList)
			{
				if (uiField.getName().equals(fieldName))
				{
					return uiField;
				}
			}
		}
		return null;
	}

	/**
	 * 获取当前业务模型内, 指定UI类型的UI对象列表
	 * 
	 * @param classObjectName
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<UIObjectInfo> listALLFormListUIObjectInBizModel(String classObjectName) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getCurrentBizModel().getGuid();
		List<UIObjectInfo> listUIObjectByBizModel = this.listUIObjectByBizModel(classObjectName, bmGuid, UITypeEnum.FORM, true);
		List<UIObjectInfo> listUIObjectByBizModel2 = this.listUIObjectByBizModel(classObjectName, bmGuid, UITypeEnum.LIST, true);
		if (listUIObjectByBizModel == null)
		{
			return listUIObjectByBizModel2;
		}
		else
		{
			if (listUIObjectByBizModel2 != null)
			{
				listUIObjectByBizModel.addAll(listUIObjectByBizModel2);
			}
			return listUIObjectByBizModel;
		}
	}

	/**
	 * 获取指定业务模型内, 指定UI类型的UI对象列表
	 * 
	 * @param classObjectName
	 * @param bmGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIObjectInfo> listUIObjectByBizModel(String classObjectName, String bmGuid, UITypeEnum uiType, boolean isOnlyVisible) throws ServiceRequestException
	{
		if (classObjectName == null)
		{
			return null;
		}

		List<UIObjectInfo> uiObjectInfoList = this.stubService.getDomainServiceStub().getClassModelService().listUIObjectInfo(classObjectName);

		String bmName = this.stubService.getBMStub().getBizModelNameByGuid(bmGuid);

		return this.getUIObjectByBusinessModel(uiObjectInfoList, bmName, uiType, isOnlyVisible);
	}

	private List<UIObjectInfo> getUIObjectByBusinessModel(List<UIObjectInfo> uiObjectInfoList, String businessModelName, UITypeEnum uiType, boolean onlyVisible)
	{
		List<UIObjectInfo> uiObjectList = new ArrayList<UIObjectInfo>();
		if (SetUtils.isNullList(uiObjectInfoList))
		{
			return uiObjectInfoList;
		}
		for (UIObjectInfo uiObject : uiObjectInfoList)
		{
			if (uiObject.getBusinessmodels() != null || (onlyVisible == true ? uiObject.isVisible() : true))
			{

				if (uiObject.getType() == uiType)
				{
					String[] ss = StringUtils.splitStringWithDelimiter(";", uiObject.getBusinessmodels());
					if (ss == null)
					{
						return uiObjectList;
					}
					if ("*".equals(businessModelName))
					{
						uiObjectList.add(uiObject);
						continue;
					}
					for (String s : ss)
					{
						if ("*".equals(s))
						{
							uiObjectList.add(uiObject);
						}
						else if (s.equalsIgnoreCase(businessModelName))
						{
							uiObjectList.add(uiObject);
						}
					}
				}
			}
		}
		return uiObjectList;
	}

	protected UIObjectInfo getUIObjectByName(String className, String uiName) throws ServiceRequestException
	{
		List<UIObjectInfo> uiObjectList = this.stubService.getDomainServiceStub().getClassModelService().listUIObjectInfo(className);

		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObjectInfo : uiObjectList)
			{
				if (uiObjectInfo.getName().equals(uiName))
				{
					return uiObjectInfo;
				}
			}
		}
		return null;
	}

	protected List<UIField> listFormUIField(String className) throws ServiceRequestException
	{
		List<UIField> uiFieldList = new ArrayList<>();
		String bmGuid = this.stubService.getCurrentBizModel().getGuid();
		List<UIObjectInfo> uiObjectList = this.listUIObjectByBizModel(className, bmGuid, UITypeEnum.FORM, true);
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObject : uiObjectList)
			{
				List<UIField> uiFields = this.listUIField(uiObject.getClassGuid(), uiObject.getGuid());
				if (!SetUtils.isNullList(uiFields))
				{
					for (UIField uiField : uiFields)
					{
						boolean isContain = false;
						for (UIField allUiField : uiFieldList)
						{
							if (allUiField.getName().equals(uiField.getName()))
							{
								isContain = true;
								break;
							}
						}
						if (!isContain)
						{
							uiFieldList.add(uiField);
						}
					}
				}
			}
			if (uiFieldList.size() == 0)
			{
				uiFieldList = null;
			}
		}
		return uiFieldList;
	}

	protected List<UIField> listListUIField(String className) throws ServiceRequestException
	{
		return this.listUIFieldByUIType(className, UITypeEnum.LIST);
	}

	protected List<UIField> listReportUIField(String className) throws ServiceRequestException
	{
		return this.listUIFieldByUIType(className, UITypeEnum.REPORT);
	}

	protected List<UIField> listSectionUIField(String className) throws ServiceRequestException
	{
		return this.listUIFieldByUIType(className, UITypeEnum.SECTION);
	}

	/**
	 * 该方法不适用与FORM类型的UI
	 * 
	 * @param className
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<UIField> listUIFieldByUIType(String className, UITypeEnum uiType) throws ServiceRequestException
	{
		UIObjectInfo uiObject = this.getUIObjectByBizModel(className, uiType);
		if (uiObject == null)
		{
			return null;
		}
		return this.listUIField(uiObject.getClassGuid(), uiObject.getGuid());
	}

	protected List<UIObjectInfo> listUiObjectInfo(String classguid, String className) throws ServiceRequestException
	{
		if (className == null && classguid != null)
		{
			className = this.stubService.getClassByGuid(classguid).getName();
		}
		return this.stubService.getDomainServiceStub().getClassModelService().listUIObjectInfo(className);
	}

	public SearchCondition createBOSearchCondition(String className) throws ServiceRequestException
	{
		SearchCondition condition = SearchConditionFactory.createSearchCondition4Class(className, null, true);
		List<UIField> uiFeildList = this.listListUIField(className);
		if (!SetUtils.isNullList(uiFeildList))
		{
			for (UIField uf : uiFeildList)
			{
				if (!uf.isSeparator())
				{
					condition.addResultField(uf.getName());
				}
			}
		}
		return condition;
	}

	public SearchCondition createAssoSearchCondition(String strcutureClassName) throws ServiceRequestException
	{
		ClassInfo info = this.stubService.getClassByName(strcutureClassName);
		if (info == null)
		{
			return null;
		}
		Map<String, UIObjectInfo> defineUIMap = null;
		if (info.hasInterface(ModelInterfaceEnum.IBOMStructure))
		{
			defineUIMap = this.getBOMDefineUI(info.getGuid());
		}
		else
		{
			defineUIMap = this.getAssoDefineUI(info.getGuid());
		}
		if (SetUtils.isNullMap(defineUIMap))
		{
			return createBOSearchCondition(strcutureClassName);
		}
		else
		{
			SearchCondition condition = SearchConditionFactory.createSearchCondition4Class(strcutureClassName, null, true);
			for (int i = 0; i < 9; i++)
			{
				UIObjectInfo uiObjectInfo = defineUIMap.get(String.valueOf(i));
				if (uiObjectInfo != null)
				{
					List<UIField> uiFeildList = this.listUIField(uiObjectInfo.getGuid());
					if (!SetUtils.isNullList(uiFeildList))
					{
						for (UIField uf : uiFeildList)
						{
							if (!uf.isSeparator())
							{
								condition.addResultField(uf.getName());
							}
						}
					}
				}
			}
			return condition;
		}
	}

	public Map<String, UIObjectInfo> getBOMDefineUI(String classGuid) throws ServiceRequestException
	{
		List<UIObjectInfo> list = this.listUiObjectInfo(classGuid, null);
		Map<String, UIObjectInfo> returnMap = new HashMap<String, UIObjectInfo>();
		if (!SetUtils.isNullList(list))
		{
			for (UIObjectInfo info : list)
			{
				int i = UIStub.BomTreeUINameList.indexOf(info.getName());
				if (i > -1)
				{
					returnMap.put(String.valueOf(i), info);
				}
			}
		}
		return returnMap;
	}

	public Map<String, UIObjectInfo> getAssoDefineUI(String classGuid) throws ServiceRequestException
	{
		List<UIObjectInfo> list = this.listUiObjectInfo(classGuid, null);
		Map<String, UIObjectInfo> returnMap = new HashMap<String, UIObjectInfo>();
		if (!SetUtils.isNullList(list))
		{
			for (UIObjectInfo info : list)
			{
				int i = UIStub.BomTreeUINameList.indexOf(info.getName());
				if (i > -1)
				{
					returnMap.put(String.valueOf(i), info);
				}
			}
		}
		return returnMap;
	}

}
