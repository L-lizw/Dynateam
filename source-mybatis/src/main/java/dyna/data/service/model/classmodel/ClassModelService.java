/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassModelService
 * xiasheng 2010-05-10
 */
package dyna.data.service.model.classmodel;

import java.util.Map;
import java.util.List;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.cls.NumberingModelInfo;
import dyna.common.dto.model.cls.NumberingObjectInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.data.service.model.ModelService;

/**
 * 提供类模型服务
 *
 * @author xiasheng
 */
public interface ClassModelService extends ModelService
{
	/**
	 * 部署后检查类对象的相关信息，
	 * 1，当类直接删除时，他的ui，ui的字段，动作，事件，编码规则，合成规则全部删除处理
	 * 2，当类的字段修改进行了删除时，ui字段对应删除
	 * @throws ServiceRequestException
	 */
	void checkAndClearClassObject() throws ServiceRequestException;
	
	/**
	 * 重新加载缓存
	 *
	 * @throws ServiceRequestException
	 */
	void reloadModel() throws ServiceRequestException;

	/**
	 * 得到实现指定接口的类名列表
	 *
	 * @param interfaceEnum 指定的接口枚举
	 * @return 类对象列表，不存在时返回null
	 */
	List<String> getClassNameListImplInterface(ModelInterfaceEnum interfaceEnum);

	/**
	 * 得到实现指定接口的类对象列表
	 *
	 * @param interfaceEnum 指定的接口枚举
	 * @return 类对象列表，不存在时返回null
	 */
	List<ClassInfo> getClassInfoListImplInterface(ModelInterfaceEnum interfaceEnum);

	/**
	 * 取得当前类的单阶子类
	 *
	 * @param classGuid
	 * @return
	 */
	List<ClassInfo> listChildren(String classGuid);

	/**
	 * 根据类的guid返回类信息，包含字段，ui等
	 *
	 * @param guid
	 * @return
	 */
	ClassObject getClassObjectByGuid(String guid);

	/**
	 * 根据类名返回类信息
	 *
	 * @param name
	 * @return
	 */
	ClassObject getClassObject(String name);

	/**
	 * 判断某个类是否是接口的实现类
	 *
	 * @param className
	 * @param interfaceEnum
	 * @return
	 */
	boolean hasInterface(String className, ModelInterfaceEnum interfaceEnum);

	/**
	 * 返回所有的类
	 *
	 * @return
	 */
	Map<String, ClassObject> getClassObjectMap();

	List<ClassInfo> listAllParentClassInfo(String classGuid);

	/**
	 * 取得指定类的所有父类guid
	 *
	 * @param classGuid
	 * @return
	 */
	List<String> listAllParentClassGuid(String classGuid);

	/**
	 * 取得指定类的所有子类guid(不包含当前类)
	 *
	 * @param classGuid
	 * @return
	 */
	List<String> listAllSubClassGuid(String classGuid);

	/**
	 * 取得指定类的所有子类(不包含当前类)
	 *
	 * @param classGuid
	 * @return
	 */
	List<ClassInfo> listAllSubClassInfo(String classGuid);

	/**
	 * 取得指定类的所有字段
	 *
	 * @param className
	 * @return
	 */
	List<ClassField> listClassField(String className);

	/**
	 * 取得指定类的指定字段
	 *
	 * @param className
	 * @param fieldName
	 * @return
	 */
	ClassField getField(String className, String fieldName);

	/**
	 * 取得排除指定接口的所有类列表
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	List<ClassInfo> listAllClass() throws ServiceRequestException;

	/**
	 * 取得指定类信息
	 *
	 * @param className
	 * @return
	 */
	ClassInfo getClassInfoByName(String className) throws ServiceRequestException;

	/**
	 * 取得指定类的编码模型基本信息
	 *
	 * @param className
	 * @return
	 */
	List<NumberingModelInfo> getNumberingModelInfoList(String className);

	/**
	 * 取得指定子编码规则
	 * <br>
	 * 模型guid和规则guid二传一即可，规则guid优先
	 *
	 * @param numberModelGuid
	 * @param numberObjectGuid
	 * @return
	 */
	List<NumberingObjectInfo> listChildNumberingObjectInfo(String numberModelGuid, String numberObjectGuid);

	/**
	 * 取得指定类的UI对象基本信息
	 *
	 * @param className
	 * @return
	 */
	List<UIObjectInfo> listUIObjectInfo(String className);

	ClassInfo getClassInfo(String classGuid);

	List<UIField> listUIField(String guid);

	/**
	 * 获取uiobject的脚本信息
	 *
	 * @param uiObjectguid
	 * @return
	 */
	List<UIAction> listUIAction(String guid);

	/**
	 * 获取uiobject报表模板导出类型
	 *
	 * @param uiObjectGuid
	 * @return
	 */
	List<ReportTypeEnum> listReportTypes(String uiObjectGuid);

	/**
	 * 获取类的脚本信息
	 *
	 * @param classGuid
	 * @return
	 */
	List<ClassAction> listClassAction(String classGuid);

	Map<String, ClassObject> buildClassModelRelation(Map<String, ClassObject> classObjectMap);
}
