/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：class model definitions
 *    创建标识：Xiasheng , 2010-05-10
 **/

package dyna.data.service.model.classmodel;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.model.cls.*;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassModelService服务的实现
 * 
 * @author xiasheng
 */
public class ClassModelServiceImpl extends DataRuleService implements ClassModelService
{
	private ClassModelServiceStub modelStub;

	public ClassModelServiceImpl(DataServerContext context, ServiceDefinition sd)
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

	public ClassModelServiceStub getModelStub()
	{
		if (this.modelStub == null)
		{
			this.modelStub = new ClassModelServiceStub(this.serviceContext, this);
		}
		return this.modelStub;
	}

	public void checkAndClearClassObject() throws ServiceRequestException
	{
		this.getModelStub().checkAndClearClassObject();
	}

	@Override
	public void reloadModel() throws ServiceRequestException
	{
		this.getModelStub().reLoadModel();
	}

	/**
	 * 得到实现指定接口的类对象列表
	 * 
	 * @param interfaceEnum
	 *            指定的接口枚举
	 * @return 类对象列表，不存在时返回null
	 */
	@Override
	public List<ClassInfo> getClassInfoListImplInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getModelStub().getClassInfoListImplInterface(interfaceEnum);
	}

	@Override
	public ClassObject getClassObjectByGuid(String guid)
	{
		return this.getModelStub().getClassObjectByGuid(guid);
	}

	@Override
	public ClassObject getClassObject(String name)
	{
		return this.getModelStub().getClassObject(name);
	}

	@Override
	public Map<String, ClassObject> getClassObjectMap()
	{
		return this.getModelStub().getClassObjectMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.data.model.ClassModelService#getClassNameListImplInterface(dyna.common.systemenum.ModelInterfaceEnum)
	 */
	@Override
	public List<String> getClassNameListImplInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getModelStub().getClassNameListImplInterface(interfaceEnum);
	}

	@Override
	public List<String> listAllParentClassGuid(String classGuid)
	{
		return this.getModelStub().listAllParentClassGuid(classGuid);
	}

	@Override
	public List<ClassInfo> listAllParentClassInfo(String classGuid)
	{
		return this.getModelStub().listAllParentClassInfo(classGuid);
	}

	@Override
	public List<String> listAllSubClassGuid(String classGuid)
	{
		return this.getModelStub().listAllSubClassGuid(classGuid);
	}

	@Override
	public List<ClassInfo> listAllSubClassInfo(String classGuid)
	{
		return this.getModelStub().listAllSubClassInfo(classGuid);
	}

	@Override
	public List<ClassField> listClassField(String className)
	{
		return this.getModelStub().listClassField(className);
	}

	@Override
	public boolean hasInterface(String className, ModelInterfaceEnum interfaceEnum)
	{
		return this.getModelStub().hasInterface(className, interfaceEnum);
	}

	@Override
	public List<ClassInfo> listAllClass() throws ServiceRequestException
	{
		return this.getModelStub().listAllClass();
	}

	@Override
	public ClassField getField(String className, String fieldName)
	{
		return this.getModelStub().getField(className, fieldName);
	}

	@Override
	public ClassInfo getClassInfoByName(String className) throws ServiceRequestException
	{
		return this.getModelStub().getClassInfoByName(className);
	}

	@Override
	public List<NumberingModelInfo> getNumberingModelInfoList(String className)
	{
		return this.getModelStub().getNumberingModelInfoList(className);
	}

	@Override
	public List<ClassInfo> listChildren(String classGuid)
	{
		ClassObject classObject = this.getClassObjectByGuid(classGuid);
		List<ClassObject> childList = classObject.getChildList();
		return SetUtils.isNullList(childList) ? null : childList.stream().map(ClassObject::getInfo).collect(Collectors.toList());
	}

	private Map<String, UIObject> listAllUIObject(String className)
	{
		Map<String, UIObject> returnMap = new HashMap<>();
		;

		ClassObject classObject = this.getClassObject(className);

		List<UIObject> uiObjectList = classObject.getUiObjectList();
		if (uiObjectList != null)
		{
			for (UIObject ui : uiObjectList)
			{
				ui.getInfo().setFieldInherited(false);
				returnMap.put(ui.getName(), ui);
			}
		}

		if (!StringUtils.isNullString(classObject.getSuperclass()))
		{
			Map<String, UIObject> parentUIMap = this.listAllUIObject(classObject.getSuperclass());
			if (!SetUtils.isNullMap(parentUIMap))
			{
				for (Map.Entry<String, UIObject> entry : parentUIMap.entrySet())
				{
					if (!returnMap.containsKey(entry.getKey()))
					{
						entry.getValue().getInfo().setFieldInherited(true);
						returnMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		return returnMap;
	}

	@Override
	public List<UIObjectInfo> listUIObjectInfo(String className)
	{
		Map<String, UIObject> returnMap = this.listAllUIObject(className);
		return SetUtils.isNullMap(returnMap) ? null
				: returnMap.values().stream().map(UIObject::getInfo).sorted(Comparator.comparing(UIObjectInfo::getSequence)).collect(Collectors.toList());
	}

	@Override
	public List<NumberingObjectInfo> listChildNumberingObjectInfo(String numberModelGuid, String numberObjectGuid)
	{
		return this.getModelStub().listChildNumberingObjectInfo(numberModelGuid, numberObjectGuid);
	}

	@Override
	public ClassInfo getClassInfo(String classGuid)
	{
		ClassObject classObject = this.getModelStub().getClassObjectByGuid(classGuid);
		return classObject == null ? null : classObject.getInfo();
	}

	@Override
	public List<UIField> listUIField(String guid)
	{
		UIObject uiObject = this.getModelStub().getUIObject(guid);
		if (uiObject != null)
		{
			if (uiObject.getFieldList() != null)
			{
				return new ArrayList<>(uiObject.getFieldList());
			}
		}
		return null;
	}

	@Override
	public List<UIAction> listUIAction(String guid)
	{
		UIObject uiObject = this.getModelStub().getUIObject(guid);
		if (uiObject != null)
		{
			if (uiObject.getActionList() != null)
			{
				return new ArrayList<>(uiObject.getActionList());
			}
		}
		return null;
	}

	@Override
	public List<ReportTypeEnum> listReportTypes(String uiObjectGuid)
	{
		// TODO Auto-generated method stub
		UIObject uiObject = this.getModelStub().getUIObject(uiObjectGuid);
		if (uiObject != null)
		{
			return uiObject.getReportTypeList();
		}
		return null;
	}

	@Override
	public List<ClassAction> listClassAction(String classGuid)
	{
		// TODO Auto-generated method stub
		Map<String, ClassAction> actionMap = this.listClassActionContainSuper(classGuid);
		return new ArrayList<>(actionMap.values());
	}

	private Map<String, ClassAction> listClassActionContainSuper(String classGuid)
	{
		Map<String, ClassAction> returnMap = new HashMap<String, ClassAction>();

		ClassObject classObject = this.getClassObjectByGuid(classGuid);

		List<ClassAction> uiObjectList = classObject.getClassActionList();
		if (uiObjectList != null)
		{
			for (ClassAction ui : uiObjectList)
			{
				ui.setInherited(false);
				returnMap.put(ui.getName(), ui);
			}
		}

		if (!StringUtils.isNullString(classObject.getSuperclass()))
		{
			Map<String, ClassAction> parentUIMap = this.listClassActionContainSuper(classObject.getSuperClassGuid());
			if (!SetUtils.isNullMap(parentUIMap))
			{
				for (Map.Entry<String, ClassAction> entry : parentUIMap.entrySet())
				{
					if (!returnMap.containsKey(entry.getKey()))
					{
						entry.getValue().setInherited(true);
						returnMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		return returnMap;

	}

	@Override
	public Map<String, ClassObject> buildClassModelRelation(Map<String, ClassObject> classObjectMap)
	{
		return this.getModelStub().buildClassModelRelation(classObjectMap);
	}
}
