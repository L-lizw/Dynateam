/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EMM Enterprise Model Management
 * Wanglei 2010-7-8
 */
package dyna.net.service.brs;

import java.util.Date;
import java.util.List;
import java.util.Set;

import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecycleGate;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.net.service.Service;

/**
 * Enterprise Model Management 企业建模信息服务
 * 
 * @author Wanglei
 * 
 */
public interface EMM extends Service
{

	/**
	 * 取得数据库当前时间
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public Date getSystemDate();

	/**
	 * 获取数据服务所对应的地址及端口信息
	 * 
	 * @throws ServiceRequestException
	 */
	public ConfigurableConnToDSImpl getConfigLoaderConnToDS() throws ServiceRequestException;

	/** LifeCycle Manager **/

	/**
	 * 取得所有的生命周期的列表
	 * 
	 * @return LifeCycle列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<LifecycleInfo> listLifeCycleInfo() throws ServiceRequestException;

	/**
	 * 根据生命周期的名字 取得该生命周期下的所有的阶段
	 * 
	 * @param lifeCycleName
	 * @return LifeCyclePhase列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<LifecyclePhaseInfo> listLifeCyclePhase(String lifeCycleName) throws ServiceRequestException;

	/**
	 * 
	 * @param lifecyclePhaseGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<LifecycleGate> listLifecyclePhaseGate(String lifecyclePhaseGuid) throws ServiceRequestException;

	/**
	 * 根据生命周期的guid取得生命周期
	 * 
	 * @param lifecycleInfoGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public LifecycleInfo getLifecycleInfoByGuid(String lifecycleInfoGuid) throws ServiceRequestException;

	/**
	 * 根据lifecyclePhaseInfoGuid取得对应的LifecyclePhase
	 * 
	 * @param lifecyclePhaseInfoGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecyclePhaseInfoGuid) throws ServiceRequestException;

	/**
	 * 根据lifecyclePhaseInfoName取得对应的LifecyclePhase
	 * 
	 * @param lifecyclePhaseInfoName
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecycleInfoName, String lifecyclePhaseInfoName) throws ServiceRequestException;

	/**
	 * 根据类的名字 获取对应的第一个生命周期阶段的信息
	 * 
	 * @param className
	 * @return LifecyclePhaseInfo
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到指定名字的类或者该类上没有对应的生命周期或者没有生命周期的阶段异常
	 */
	public LifecyclePhaseInfo getFirstLifecyclePhaseInfoByClassName(String className) throws ServiceRequestException;

	/** CodeManager */

	/**
	 * 获取所有的Code列表
	 * 包含Classification的master
	 * 
	 * @return Code列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeObjectInfo> listCodeInfo() throws ServiceRequestException;

	/**
	 * 根据Code的guid获取对应的Code
	 * 
	 * @param codeGuid
	 * @return Code
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public CodeObjectInfo getCode(String codeGuid) throws ServiceRequestException;

	/**
	 * 根据Code的name获取对应的Code
	 * 
	 * @param codeName
	 * @return Code
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到指定名字的code异常
	 */
	public CodeObjectInfo getCodeByName(String codeName) throws ServiceRequestException;

	/**
	 * 根据codeItem的guid获取对应的CodeItem
	 * 
	 * @param codeItemGuid
	 * @return CodeItem
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到指定guid对应的codeItem异常
	 */
	public CodeItemInfo getCodeItem(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 根据codeName与codeItemName获取对应的codeItem对象
	 * 
	 * @param codeName
	 * @param codeItemName
	 * @return CodeItem对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到指定名字的code或者指定名字的codeItem异常
	 */
	public CodeItemInfo getCodeItemByName(String codeName, String codeItemName) throws ServiceRequestException;

	/**
	 * 取得组码子项，当codeItemGuid为空时，返回首阶组码子项，否则返回指定codeItemGuid的子项
	 * 
	 * @param codeGuid
	 * @param codeItemGuid
	 * @return
	 */
	public List<CodeItemInfo> listSubCodeItemForMaster(String codeGuid, String codeName) throws ServiceRequestException;

	/**
	 * 取得组码子项，当codeItemGuid为空时，返回首阶组码子项，否则返回指定codeItemGuid的子项
	 * 
	 * @param codeGuid
	 * @param codeItemGuid
	 * @return
	 */
	public List<CodeItemInfo> listSubCodeItemForDetail(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 根据Code的guid或者Code的name获取Code下所有的codeItem对象列表，二者传其一即可
	 * 
	 * @param codeGuid
	 * @param codeName
	 * @return ClassField列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeItemInfo> listAllCodeItemInfoByMaster(String codeGuid, String codeName) throws ServiceRequestException;

	/**
	 * 查找分类的所有下级分类
	 * 
	 * @param clsfGuid
	 *            所查分类的guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeItemInfo> listAllSubCodeItemInfoByDatail(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 查找分类的所有下级分类,包含本身
	 * 
	 * @param clsfGuid
	 *            所查分类的guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeItemInfo> listAllSubCodeItemInfoByDatailContain(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 根据Code的guid或者Code的name获取Code下所有的codeItem对象列表，二者传其一即可
	 * 
	 * @param codeGuid
	 * @param codeName
	 * @return ClassField列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeItemInfo> listLeafCodeItemInfoByMaster(String codeGuid, String codeName) throws ServiceRequestException;

	/**
	 * 查找分类的所有下级分类
	 * 
	 * @param clsfGuid
	 *            所查分类的guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeItemInfo> listLeafCodeItemInfoByDatail(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 查找分类的所有父级分类
	 * 
	 * @param clsfGuid
	 *            所查分类的guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<CodeItemInfo> listAllSuperCodeItemInfo(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 取得分类中的字段
	 * 
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassField> listClassificationField(String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 取得分类uiobject
	 * 
	 * @param classificationGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	public ClassificationUIInfo getCFUIObject(String classificationGuid, UITypeEnum uiType) throws ServiceRequestException;

	/**
	 * 取得分类uiobject
	 * 
	 * @param classificationGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassificationUIInfo> listCFUIObject(String classificationGuid) throws ServiceRequestException;

	/**
	 * 取得分类uifield list
	 * 
	 * @param classificationGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listCFUIField(String classificationGuid, UITypeEnum uiType) throws ServiceRequestException;

	public List<UIField> listCFUIField(String uiGuid) throws ServiceRequestException;

	/**
	 * 取得分类中的字段
	 * 
	 * @param classificationItemGuid
	 * @param fieldName
	 * @return
	 * @throws ServiceRequestException
	 */
	public ClassField getClassificationField(String classificationItemGuid, String fieldName) throws ServiceRequestException;

	/**
	 * 根据BOInfo的guid获取对应的class
	 * 
	 * @param boGuid
	 * @return ClassInfo
	 * @throws ServiceRequestException
	 */
	public ClassInfo getClassInfoByBOGuid(String boGuid) throws ServiceRequestException;

	/**
	 * 根据BOInfo的guid获取对应的class上的LifeCycle
	 * 
	 * @param boGuid
	 * @return LifecycleInfo
	 * @throws ServiceRequestException
	 */
	public LifecycleInfo getLifecycleInfoByBOGuid(String boGuid) throws ServiceRequestException;

	/**
	 * 能过objectguid取得定义的分类的uiObject list
	 * 当参数objectGuid 中GUID分空时，返回未实例化时的空uiobject
	 * 
	 * @param objectGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIObjectInfo> listCFUIObjectByObjecGuid(ObjectGuid objectGuid, UITypeEnum uiType) throws ServiceRequestException;

	/** Class Manager **/

	/**
	 * 
	 * 
	 * @param interfaceName
	 * @return
	 */
	public InterfaceObject getInterfaceObjectByName(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException;

	/**
	 * 查询系统中的所有类信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ClassInfo> listClassInfo() throws ServiceRequestException;

	/**
	 * 得到实现指定接口的类对象列表
	 * 
	 * @param interfaceEnum
	 *            指定的接口枚举
	 * @return 类对象列表，不存在时返回null
	 */
	public List<ClassInfo> listClassByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException;

	/**
	 * 查询系统中的所有类信息
	 * 
	 * @param removeInterface
	 * @return removeInterface
	 *         需要移除继承此接口的类
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ClassInfo> listClassOutInterface(ModelInterfaceEnum removeInterface) throws ServiceRequestException;

	/**
	 * 根据 classGuid 获取 classinfo
	 * 
	 * @param classGuid
	 * @return ClassObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             if not found the specified ClassObject
	 */
	public ClassInfo getClassByGuid(String classGuid) throws ServiceRequestException;

	/**
	 * 根据模型类名获取模型类对象bean
	 * 
	 * @param classObjectName
	 * @return ClassInfo
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             and if not found the specified classinfo
	 */
	public ClassInfo getClassByName(String classObjectName) throws ServiceRequestException;

	/**
	 * 取得物料类的第一级父类
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public ClassInfo getFirstLevelClassByItem() throws ServiceRequestException;

	/**
	 * 根据接口返回指定接口的第一个类，有多个的话，只返回一个
	 * 
	 * @param interfaceName
	 *            指定定接口
	 * @param excludeList
	 *            排除的子类
	 * @return
	 * @throws ServiceRequestException
	 */
	public ClassInfo getFirstLevelClassByInterface(ModelInterfaceEnum interfaceName, List<String> excludeList) throws ServiceRequestException;

	/**
	 * 判断以classNameSuper为名字的类是否是以classNameSub为名字的类的父类中的一个
	 * 
	 * @param classNameSub
	 * @param classNameSuper
	 * @return true or false
	 * @throws ServiceRequestException
	 */
	public boolean isSuperClass(String classNameSub, String classNameSuper) throws ServiceRequestException;

	/**
	 * 查询指定类的所有父类信息
	 * 
	 * @param className
	 *            className与参数classGuid二者传其一即可
	 * @param classGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ClassInfo> listAllSuperClass(String className, String classGuid) throws ServiceRequestException;

	/**
	 * 查找指定类的子类
	 * 
	 * @param className
	 *            className与classGuid二者传其一即可
	 * @param classGuid
	 * @param cascade
	 * @param removeInterface
	 *            需要移除继承此接口的类
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ClassInfo> listSubClass(String className, String classGuid, boolean cascade, ModelInterfaceEnum removeInterface) throws ServiceRequestException;

	/**
	 * 取得类或接口下的所有叶子class结点
	 * 接口或类名两者传一，如果都存在，则以类名为准
	 * 
	 * @param interfaceEnum
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassInfo> listAllSubClassInfoOnlyLeaf(ModelInterfaceEnum interfaceEnum, String className) throws ServiceRequestException;

	/**
	 * 根据接口获取该接口中对应的内置字段列表
	 * 
	 * @param interfaceEnum
	 * @return ClassField列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ClassField> listClassFieldByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException;

	/**
	 * 查询指定类的所有字段信息<br>
	 * <只包括扩展字段和内置字段，包括除了GUID$以外的系统字段>
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ClassField> listFieldOfClass(String className) throws ServiceRequestException;

	/**
	 * 建模器编辑ui，无关bm信息，根据classguid或者classname获取类所有的ui信息
	 * 
	 * @param classguid
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIObjectInfo> listUIObjectInfo(String classguid, String className) throws ServiceRequestException;

	/**
	 * 获取类的脚本信息
	 * 
	 * @param classguid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassAction> listClassAction(String classguid) throws ServiceRequestException;

	/**
	 * 获取指定类的所有合成规则或编码规则
	 * 
	 * @param classguid
	 * @param className
	 * @param isNumbering
	 *            是否是编码规则
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<NumberingModel> listNumberingModel(String classguid, String className, boolean isNumbering) throws ServiceRequestException;

	/**
	 * 通过className和fieldName, 获取classField对象
	 * 
	 * @param classObjectName
	 * @param fieldName
	 * @return ClassField
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到指定类下对应的指定名字的字段异常
	 */
	public ClassField getFieldByName(String classObjectName, String fieldName, boolean isThrowException) throws ServiceRequestException;

	/**
	 * 通过类名和ui对象名获取ui对象, 无论此ui对象是否可见,是否只适合当前业务模型, 只匹配ui name
	 * 
	 * @param className
	 *            类名
	 * @param uiName
	 *            ui对象名
	 * @return
	 * @throws ServiceRequestException
	 */
	public UIObjectInfo getUIObjectByName(String className, String uiName) throws ServiceRequestException;

	/**
	 * 通过 class name 取得的所有的UIObject的列表，跟模型无关，包含不可见UI
	 * 
	 * @param classObjectName
	 * @return UIObject的集合
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<UIObjectInfo> listAllUIObject(String classObjectName) throws ServiceRequestException;

	/**
	 * 通过className和fieldName, 获取UIField对象
	 * 
	 * @param classObjectName
	 * @param fieldName
	 * @return UIField
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到指定类下对应的指定名字的字段异常
	 */
	public UIField getUIFieldByName(String classObjectName, String fieldName) throws ServiceRequestException;

	/**
	 * 通过className和uiName, 获取uiField列表
	 * 
	 * @param classObjectName
	 * @param uiObjectName
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到类对应的UI模型或者没找到指定名字的ui对象异常
	 */
	public List<UIField> listUIFieldByUIObject(String classObjectName, String uiObjectName) throws ServiceRequestException;

	/**
	 * 根据UI对象唯一标识符取得其UI字段列表
	 * 
	 * @param uiGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listUIFieldByUIGuid(String uiGuid) throws ServiceRequestException;

	/**
	 * 查询form类型ui的字段列表
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listFormUIField(String className) throws ServiceRequestException;

	/**
	 * 查询List类型ui的字段列表
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listListUIField(String className) throws ServiceRequestException;

	/**
	 * 查询Report类型ui的字段列表
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listReportUIField(String className) throws ServiceRequestException;

	/**
	 * 查询Section类型ui的字段列表
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listSectionUIField(String className) throws ServiceRequestException;

	/**
	 * 取得UI对象的Action列表
	 * 
	 * @param uiGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIAction> listUIAction(String uiGuid) throws ServiceRequestException;

	/**
	 * 通过当前用户的business model 和 class name 取得的 UIObject的列表
	 * (取得所有指定类型的所有的UI,不考虑建模器中的“显示”是否被选中)
	 * 
	 * @param classObjectName
	 * @param uiType
	 * @return UIObject的集合
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public UIObjectInfo getUIObjectInCurrentBizModel(String classObjectName, UITypeEnum uiType) throws ServiceRequestException;

	/**
	 * 通过当前用户的business model 和 class name 取得的 UIObject的列表
	 * (取得所有指定类型的所有的UI,不考虑建模器中的“显示”是否被选中)
	 * 
	 * @param classObjectName
	 * @param uiType
	 * @return UIObject的集合
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<UIObjectInfo> listUIObjectInCurrentBizModel(String classObjectName, UITypeEnum uiType, boolean isOnlyVisible) throws ServiceRequestException;

	/**
	 * 通过business model 和 class name 取得的FORM UIObject的列表
	 * 
	 * @param classObjectName
	 * @return UIObject的集合
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<UIObjectInfo> listUIObjectInBizModel(String bmGuid, String classObjectName, UITypeEnum uiType, boolean isOnlyVisible) throws ServiceRequestException;

	/**
	 * 通过当前用户的business model 和 class name 取得的FORM UIObject 和 List UIObject的列表
	 * 
	 * @param classObjectName
	 * @return UIObject的集合
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<UIObjectInfo> listALLFormListUIObjectInBizModel(String classObjectName) throws ServiceRequestException;

	/**
	 * BM Manager**
	 * 
	 * 
	 * 根据bmGuid查找业务模型信息<br>
	 * 
	 * @param bmGuid
	 * @return 业务模型
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及没有找到指定类对应的业务对象
	 */
	public BMInfo getBizModel(String bmGuid) throws ServiceRequestException;

	/**
	 * 查找共享业务模型信息<br>
	 * 
	 * @return 共享业务模型
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及没有找到指定类对应的业务对象
	 */
	public BMInfo getSharedBizModel() throws ServiceRequestException;

	/**
	 * 取得当前用户的business model
	 * 
	 * @return BusinessModel
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 *             及没有找到登录用户对应的业务模型异常
	 */
	public BMInfo getCurrentBizModel() throws ServiceRequestException;

	/**
	 * 查询所有业务模型
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BMInfo> listBizModel() throws ServiceRequestException;

	public BOInfo getBizObject(String bmGuid, String classGuid, String classificationGuid) throws ServiceRequestException;

	public BOInfo getBizObject(String bmGuid, String boGuid) throws ServiceRequestException;

	public BOInfo getBoInfoByNameAndBM(String bmGuid, String boInfoName) throws ServiceRequestException;

	/**
	 * 根据boGuid查找业务对象信息
	 * 
	 * @param boGuid
	 *            业务对象guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOInfo getCurrentBizObjectByGuid(String boGuid) throws ServiceRequestException;

	/**
	 * 根据classGuid查找业务对象信息<br>
	 * 
	 * 业务模型为登录用户对应的业务模型
	 * 
	 * @param classGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及没有找到指定类对应的业务对象
	 */
	public BOInfo getCurrentBizObject(String classGuid) throws ServiceRequestException;

	/**
	 * 通过BOInfo的名字取BOInfo对象<br>
	 * 业务模型是登录用户的业务模型
	 * 
	 * @param boInfoName
	 * @param isThrowException
	 *            是否抛出异常
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOInfo getCurrentBoInfoByName(String boInfoName, boolean isThrowException) throws ServiceRequestException;

	/**
	 * 通过classname取得当前模型的BOInfo
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOInfo getCurrentBoInfoByClassName(String className) throws ServiceRequestException;

	/**
	 * 查找登录用户对应的业务模型内的业务对象下的所有下层业务对象 不包含参数本身对应的业务对象
	 * 
	 * @param boName
	 *            业务对象名称
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BOInfo> listAllSubBOInfo(String boName) throws ServiceRequestException;

	/**
	 * 查找登录用户对应的业务模型内的业务对象下层业务对象（只取一层 直接下层） 不包含参数本身对应的业务对象
	 * 
	 * @param boName
	 *            业务对象名称
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BOInfo> listSubBOInfo(String boName) throws ServiceRequestException;

	/**
	 * 查找登录用户对应的业务模型内的业务对象下的所有下层业务对象 包含参数对应的业务对象
	 * 
	 * @param boName
	 *            业务对象名称
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BOInfo> listAllSubBOInfoContain(String boName) throws ServiceRequestException;

	/**
	 * 查找指定用户对应的业务模型内的业务对象下的所有下层业务对象 包含参数对应的业务对象
	 * 
	 * @param boName
	 *            业务对象名称
	 * @param bmGuid
	 *            业务模型
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BOInfo> listAllSubBOInfoContain(String boName, String bmGuid) throws ServiceRequestException;

	/**
	 * 获取系统中所有的共享业务对象
	 * 
	 * @return BOInfo列表
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> listSharedBizObject() throws ServiceRequestException;

	/**
	 * 得到实现指定接口的业务对象列表 对应的模型为登录用户对应的BM
	 * 
	 * @param interfaceEnum
	 *            指定的接口枚举
	 * @return 类对象列表，不存在时返回null
	 */
	public List<BOInfo> listBizObjectByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException;

	/**
	 * 得到实现指定接口的业务对象列表,modelGuid为指定的模型，如果不传 那默认为所有模型
	 * 
	 * @param interfaceEnum
	 *            指定的接口枚举
	 * @param modelGuid
	 * @return 类对象列表，不存在时返回null
	 */
	public List<BOInfo> listBizObjectByInterface(ModelInterfaceEnum interfaceEnum, String modelGuid) throws ServiceRequestException;

	/**
	 * 查询业务模型下的业务对象
	 * 
	 * @param modelName
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及没有找到指定名字的模型异常
	 */
	public List<BOInfo> listBizObjectOfModel(String modelName) throws ServiceRequestException;

	/**
	 * 查询业务模型下的业务对象,不包含view,结构，项目等不能新建的bo
	 * 
	 * @param modelName
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及没有找到指定名字的模型异常
	 */
	public List<BOInfo> listBizObjectOfModelNonContainOthers(String modelName) throws ServiceRequestException;

	/**
	 * 查询业务模型下的业务对象
	 * 
	 * @param modelGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BOInfo> listBizObjectOfModelByBMGuid(String modelGuid) throws ServiceRequestException;

	/**
	 * 查询业务模型下的业务对象,不包含view,结构，项目等不能新建的bo
	 * 
	 * @param modelGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及没有找到指定名字的模型异常
	 */
	public List<BOInfo> listBizObjectOfModelNonContainOthersByBMGuid(String modelGuid) throws ServiceRequestException;

	/**
	 * 查询业务模型下业务对象（只取一层 直接下层）
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、没有管理员权限异常(ID_APP_ADMIN_GROUP_TEAM)
	 *             及系统中没有模型异常
	 */
	public List<BOInfo> listRootBizObject(String modelGuid) throws ServiceRequestException;

	/**
	 * 查找业务模型下的业务对象下层业务对象（只取一层 直接下层） 不包含参数本身对应的业务对象
	 * 
	 * @param modelGuid
	 * 
	 * @param boName
	 *            业务对象名称
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<BOInfo> listSubBOInfoByBM(String modelGuid, String boGuid) throws ServiceRequestException;

	/**
	 * 获取哪些bo引用了该分类
	 * 
	 * @param classficationguid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> listBOInfoByClassficationGuid(String classficationguid) throws ServiceRequestException;

	/** Classification Feature Manager **/

	/**
	 * 获取分类映射列表
	 * 
	 * @param classguid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassficationFeature> listClassficationFeature(String classguid) throws ServiceRequestException;

	/**
	 * 获取映射字段列表
	 * 
	 * @param classificationregularguid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassficationFeatureItemInfo> listClassficationFeatureItem(String classclassificationguid, String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 获取分类字段列表
	 * 
	 * @param classificationnumberregular
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassificationNumberField> listClassificationNumberField(String classificationnumberregular) throws ServiceRequestException;

	/**
	 * 根据classGuid或者className取得指定字段classFieldName(可以是id,name,alterid 以系统字段枚举中的名字为准)对应的编码规则模型<br>
	 * ( 递归父类)
	 * 
	 * <String>guid与name传其中任意一个值就行,如果都传了 那么以guid为准
	 * 
	 * @param classGuid
	 * @param calssName
	 * @param classFieldName
	 *            可以是id,name,alterid 以系统字段枚举中的名字为准
	 * @return NumberingModel
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public NumberingModel lookupNumberingModel(String classGuid, String className, String classFieldName) throws ServiceRequestException;

	/**
	 * 根据classGuid或者className取得指定字段classFieldName(可以是id,name,alterid 以系统字段枚举中的名字为准)对应的编码规则模型<br>
	 * ( 递归父类)
	 * 
	 * <String>guid与name传其中任意一个值就行,如果都传了 那么以guid为准
	 * 
	 * @param classGuid
	 * @param calssName
	 * @param classFieldName
	 *            可以是id,name,alterid 以系统字段枚举中的名字为准
	 * @param isContainSynthesis
	 *            是否包括合成
	 * @return NumberingModel
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public NumberingModel lookupNumberingModel(String classGuid, String className, String classFieldName, boolean isContainSynthesis) throws ServiceRequestException;

	/**
	 * 根据classGuid或者className判断是否有指定字段classFieldName(可以是id,name,alterid)对应的编码规则<br>
	 * 
	 * <String>guid与name传其中任意一个值就行,如果都传了 那么以guid为准
	 * 
	 * @param classGuid
	 * @param calssName
	 * @param classFieldName
	 *            id,name,alterid等 以系统字段对应的名字为准
	 * @return false：沒有 true:有
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public boolean hasNumberingModelByField(String classGuid, String className, String classFieldName) throws ServiceRequestException;

	/**
	 * 获取所有支持反向查询的模板
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplate4Opposite(ObjectGuid objectGuid) throws ServiceRequestException;
	
	/** Relation And Bom Define Manager **/

	/**
	 * 获取系统中所有的树状结构及普通关系模板的Name <br>
	 * 
	 * @return 模板名字列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<String> listRelationTemplateName(boolean containObs) throws ServiceRequestException;

	/**
	 * 根据模板的名字，获取所有为该名字的关系模板<br>
	 * 获取的都是结构关系模板 不包含普通关系模板
	 * 
	 * @param templateName
	 * @return RelationTemplate列表
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplateByName(String templateName, boolean containObs) throws ServiceRequestException;

	/**
	 * 根据relationTemplateGuid获取RelationTemplate对象<br>
	 * 其中包含end2的明细列表
	 * 
	 * @param relationTemplateGuid
	 *            关联关系模板的guid
	 * @return RelationTemplate
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public RelationTemplate getRelationTemplate(String relationTemplateGuid) throws ServiceRequestException;

	/**
	 * 根据关系模板的id获取RelationTemplate对象 <br>
	 * 关系模板的ID是唯一性的，所以可以根据ID获取对应的关系模板<br>
	 * 用模板创建View的时候会把模板的ID赋值给View的TEMPLATEID字段所以根据View的TEMPLATEID也可以查询对应的关系模板
	 * 
	 * 
	 * @param id
	 *            关联关系模板的或者ViewObject内的TEMPLATEID字段的值
	 * @return RelationTemplate
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public RelationTemplateInfo getRelationTemplateById(String id) throws ServiceRequestException;

	/**
	 * 根据END1的ObjectGuid与关系模板的名字获取对应的关系模板<br>
	 * 一个end1对应BO只能设置一个模板，当该end1对应的BO上没设置关系模板的时候会往end1对应BO的上层找上层BO对应模板，找到一个即返回
	 * 
	 * @param objectGuid
	 * @param templateName
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public RelationTemplateInfo getRelationTemplateByName(ObjectGuid objectGuid, String templateName) throws ServiceRequestException;

	/**
	 * 获取指定的ObjectGuid作为end1对应的所有的关联关系模板<br>
	 * 以登录用户所对应的业务模型为默认条件
	 * 
	 * @param objectGuid
	 * @return 关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<RelationTemplateInfo> listRelationTemplate(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 获取指定的ObjectGuid作为end1对应的所有的关联关系模板<br>
	 * 以登录用户所对应的业务模型为默认条件
	 * 
	 * @param objectGuid
	 * @param isContainBuiltin
	 *            是否包含内置模板
	 * @return 关联关系模板列表
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplate(ObjectGuid objectGuid, boolean isContainBuiltin) throws ServiceRequestException;

	/**
	 * 取得内置模板,以3D,2D,ecad的顺序
	 * 以字段名：HASEND2 标识是否有end2 值为： Y/N
	 * 
	 * @param end1ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplate4Builtin(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 通过end2取得关系模板
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplateByEND2(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 通过end1 bo list 找出可被用的所有关系模板
	 * 
	 * @param end1BOGuidList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listEnableRelationTemplate4End1BO(String bmGuid, List<String> end1BOGuidList, boolean isContainObsolete) throws ServiceRequestException;

	/**
	 * 获取系统中所有的BOM模板的Name <br>
	 * 系统中用模板的名字来表示BOM的分类 如MBOM、EBOM等 <br>
	 * 所有这个方法返回的是BOM的所有分类
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<String> listBOMTemplateName(boolean containObs) throws ServiceRequestException;

	/**
	 * 根据模板名字获取所有的BOM关联关系模板
	 * 
	 * @param templateName
	 * @return BOM关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMTemplateInfo> listBOMTemplateByName(String templateName, boolean containObs) throws ServiceRequestException;

	/**
	 * 根据bomTemplateGuid获取BOMTemplate对象
	 * 
	 * @param bomTemplateGuid
	 *            BOM关联关系模板的guid
	 * @return BOMTemplate
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public BOMTemplate getBOMTemplate(String bomTemplateGuid) throws ServiceRequestException;

	/**
	 * 获取指定的BOMView的TEMPLATEID或者BOM模板的ID对应的BOM模板 <br>
	 * 
	 * 因为创建BOMView的时候会把BOM模板的ID赋值给BOMView的TEMPLATEID字段
	 * 
	 * @param id
	 *            BOMView的TEMPLATEID或者BOM模板的ID
	 * @return BOM关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public BOMTemplateInfo getBOMTemplateById(String id) throws ServiceRequestException;

	/**
	 * 根据END1的ObjectGuid与BOM模板的名字获取对应的BOM模板
	 * 
	 * @param objectGuid
	 * @param templateName
	 * @return BOM模板
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public BOMTemplateInfo getBOMTemplateByName(ObjectGuid objectGuid, String templateName) throws ServiceRequestException;

	/**
	 * 获取指定的END1ObjectGuid对应的所有的BOM关联关系模板
	 * 
	 * @param objectGuid
	 * @return BOM关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMTemplateInfo> listBOMTemplateByEND1(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 获取指定的END1ObjectGuid对应的所有的BOM关联关系模板
	 * 
	 * @param objectGuid
	 * @return BOM关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMTemplateInfo> listBOMTemplateByEND1(ObjectGuid objectGuid, String bmGuid) throws ServiceRequestException;

	/**
	 * 获取指定的END2ObjectGuid对应的所有的BOM关联关系模板
	 * 
	 * @param objectGuid
	 * @return BOM关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMTemplateInfo> listBOMTemplateByEND2(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据END1的业务模型，取得符合的bom模板
	 * 
	 * @param boName
	 * @param isContainInValid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOMTemplateInfo> listAllBOMTemplateByEnd1BO(String boName, boolean isContainInValid) throws ServiceRequestException;

	/**
	 * 获取所有的BOM关联关系模板,不包含废弃
	 * 
	 * @return BOM关联关系模板列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public List<BOMTemplateInfo> listBOMTemplate() throws ServiceRequestException;

	/**
	 * 获取应用服务器的conf/comment.report/bomReport目录下的所有BOM报表模板的名字(即：所有的后缀名为.jrxml的文件)
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listBOMReportTemplateName() throws ServiceRequestException;

	/**
	 * 查询SearchCondition内搜索结果列表中的所有Code类型字段名称
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Set<String> getCodeFieldNamesInSC(SearchCondition condition) throws ServiceRequestException;

	/**
	 * 查询SearchCondition内搜索结果列表中的所有Object类型字段名称
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Set<String> getObjectFieldNamesInSC(SearchCondition condition) throws ServiceRequestException;

	/**
	 * 查询resultFields列表中的object类型字段
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Set<String> getObjectFieldNames(String className, List<String> resultFields) throws ServiceRequestException;

	/**
	 * 查询resultFields列表中的code类型字段
	 * 
	 * @param className
	 * @param resultFields
	 * @return
	 * @throws ServiceRequestException
	 */
	public Set<String> getCodeFieldNames(String className, List<String> resultFields) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> listRelationEnd2BoInfo(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> listEnd2LeafBoInfoByTemplateGuid(String guid, boolean isBOM) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param isTree
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIField> listAssoUITableColumn(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */
	public SearchCondition createAssoSearchCondition(String strcutureClassName) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */

	public SearchCondition createBOSearchCondition(String className) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */
	public SearchCondition createClassificationSearchCondition(String className, String itemName) throws ServiceRequestException;

	/**
	 * 根据模板中的BO业务对象列表，取得所有BO的最直接父类，如果只有一个业务对象，则直接返回改对象的classinfo
	 * 
	 * @param end2BONameList
	 * @return
	 * @throws ServiceRequestException
	 */
	public SearchCondition createAssoEnd2SearchCondition(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException;

}
