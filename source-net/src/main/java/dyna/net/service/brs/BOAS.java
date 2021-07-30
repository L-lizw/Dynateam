/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOAS Bussiness Object Access Service
 * Wanglei 2010-7-8
 */
package dyna.net.service.brs;

import java.util.Date;
import java.util.List;
import java.util.Set;

import dyna.common.SearchCondition;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.extra.OpenInstanceModel;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DataRule;
import dyna.common.dto.Folder;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.net.service.Service;

/**
 * Bussiness Object Access Service 业务对象存取服务
 * 
 * @author Wanglei
 * 
 */
public interface BOAS extends Service
{
	/**
	 * 根据编码规则生成对象的ID <br>
	 * 直接调用数据层的生成编码规则的方法
	 * 
	 * @param foundationObject
	 *            要处理的对象
	 * @return 对象的ID
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public String allocateUniqueId(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 根据编码规则生成对象的NAME <br>
	 * 直接调用数据层的生成编码规则的方法
	 * 
	 * @param foundationObject
	 *            要处理的对象
	 * @return 对象的名字
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public String allocateUniqueName(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 根据编码规则生成对象的AlterId <br>
	 * 直接调用数据层的生成编码规则的方法
	 * 
	 * @param foundationObject
	 *            要处理的对象
	 * @return 对象的AlterId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public String allocateUniqueAlterId(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 批量将对象列表关联到view对象上 <br>
	 * 没有用事物控制<br>
	 * 创建structureObject对象
	 * 
	 * @param viewObjectGuid
	 *            view对象的ObjectGuid
	 * @param end2FoundationObjectGuidList
	 *            要关联的对象的ObjectGuid列表
	 * @param structureObjectList
	 *            结构关系的对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void batchLink(ObjectGuid viewObjectGuid, List<ObjectGuid> end2FoundationObjectGuidList, List<StructureObject> structureObjectList) throws ServiceRequestException;

	/**
	 * 批量解除关联关系 <br>
	 * 无事务控制<br>
	 * 删除structureObject对象
	 * 
	 * @param structureObjectList
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void batchUnlink(List<StructureObject> structureObjectList) throws ServiceRequestException;

	/**
	 * 取消处于检出状态的对象 <br>
	 * 关系模板中有事件关联的设置，如果"返回检出"项的设置为真， <br>
	 * 那么返回检出对象的时候将使用该模板创建的关系的end2对象也取消检出 <br>
	 * 递归处理
	 * 
	 * @param foundationObject
	 *            要取消的检出状态的对象
	 * @param isDealBom
	 *            是否同时取消该操作者检出的BOMView
	 * @return 取消检出后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject cancelCheckOut(FoundationObject foundationObject, boolean isDealBom) throws ServiceRequestException;

	/**
	 * 对象检入 <br>
	 * 关系模板中有事件关联的设置，如果"检入"项的设置为真， <br>
	 * 那么检入对象的时候将使用该模板创建的关系的end2对象也检入处理 <br>
	 * 递归处理
	 * 
	 * @param foundationObject
	 *            要检入的对象
	 * @param isDealBom
	 *            是否同时检入由操作者检出的BOMView
	 * @return 检入的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject checkIn(FoundationObject foundationObject, boolean isDealBom) throws ServiceRequestException;

	/**
	 * 对象检出 <br>
	 * 关系模板中有事件关联的设置，如果"检出"项的设置为真， <br>
	 * 那么检出对象的时候将使用该模板创建的关系的end2对象也检出处理 <br>
	 * 递归处理 <br>
	 * 如果递归处理的时候任何一层出现检出异常或检出失败，那么继续处理同层别的end2
	 * 
	 * @param foundationObject
	 *            要检出的对象
	 * @return 检出的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject checkOut(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 创建对象 并检出 <br>
	 * 抽象类不能创建对象,对象的ID为空时根据编码规则自动生成ID, <br>
	 * 参数中记录是自动生成的编码规则还是输入的， <br>
	 * 如果是自动生成的编码规则在存储对象时报ID重复异常， <br>
	 * 那么捕获该异常并继续自动生成编码规则直到不重复为止(这么做是为了处理编码的由"不占号"造成的并发)<br>
	 * 如果ID重复，那么重新获取ID后会把通知信息放入MESSAGE字段用以告诉用户ID已变化
	 * 
	 * @param foundationObject
	 *            要创建的对象
	 * @return 创建以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及"抽象类不能创建实例"异常(ID_APP_ABSTRACT_CLASS)
	 */
	public FoundationObject createObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 创建对象 并检出 <br>
	 * 抽象类不能创建对象,对象的ID为空时根据编码规则自动生成ID, <br>
	 * 参数中记录是自动生成的编码规则还是输入的， <br>
	 * 如果是自动生成的编码规则在存储对象时报ID重复异常， <br>
	 * 那么捕获该异常并继续自动生成编码规则直到不重复为止(这么做是为了处理编码的由"不占号"造成的并发)<br>
	 * 如果ID重复，那么重新获取ID后会把通知信息放入MESSAGE字段用以告诉用户ID已变化
	 * 如果入库，则检出，不指定检出目录，则检出到根目录
	 * 
	 * @param foundationObject
	 *            要创建的对象
	 * @return 创建以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及"抽象类不能创建实例"异常(ID_APP_ABSTRACT_CLASS)
	 */
	public FoundationObject createAndCheckOutObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 根据文档模板创建文档实例<br>
	 * 根据指定文档模板tmpObjectGuid，除“编号”、“ALTER_ID”、“版本”、“创建者”、“状态”、系统列，<br>
	 * 系统从该模板中获取其它字段信息，作为新对象的相应字段的默认值(只取类上自定义字段,并且前台并没赋值的字段)<br>
	 * 同时模板所关联的物理文件也被复制
	 * 
	 * @param docFoundationObject
	 *            要创建的文档实例
	 * @param tmpObjectGuid
	 *            文档模板的ObjectGuid
	 * @param isCheckOut
	 *            是否检出
	 * @return 创建后的对象
	 * @throws ServiceRequestException
	 */
	public FoundationObject createDocumentByTemplate(FoundationObject docFoundationObject, ObjectGuid tmpObjectGuid, boolean isCheckOut) throws ServiceRequestException;

	// /**
	// * 根据文档模板创建文档实例<br>
	// * 根据指定文档模板tmpObjectGuid，除“编号”、“ALTER_ID”、“版本”、“创建者”、“状态”、系统列，<br>
	// * 系统从该模板中获取其它字段信息，作为新对象的相应字段的默认值(只取类上自定义字段,并且前台并没赋值的字段)<br>
	// * 同时模板所关联的物理文件也被复制
	// *
	// * @param docFoundationObject
	// * 要创建的文档实例
	// * @param tmpObjectGuid
	// * 文档模板的ObjectGuid
	// * @param isCheckOut
	// * 是否检出
	// * @return 创建后的对象
	// * @throws ServiceRequestException
	// */
	// public FoundationObject copyFoudationByTemplate(FoundationObject docFoundationObject, ObjectGuid tmpObjectGuid)
	// throws ServiceRequestException;

	/**
	 * 从文件创建文档<br>
	 * 创建文档对象(如果对象名称为空, 则设置为文件名(多个文件,则使用第一个文件的文件名)), 并将文件关联到文档对象的版序1
	 * 
	 * @param docFoundationObject
	 *            文档对象
	 * @param fileInfoList
	 *            文件列表
	 * @param isCheckOut
	 *            是否检出
	 * @return 对象列表, 第一个对象为新建的文档对象, 后续对象为文件传输信息(DSSFileTrans,用于传输文件到服务器), 与需要关联的文件对应
	 * @throws ServiceRequestException
	 */
	public List<DynaObject> createDocumentByFile(FoundationObject docFoundationObject, List<DSSFileInfo> fileInfoList, boolean isCheckOut) throws ServiceRequestException;

	/**
	 * 新建对象的修订版 创建后检出<br>
	 * 处理顺序及业务如下：<br>
	 * 清除guid<br>
	 * 给"拥有者"等字段赋值<br>
	 * 如果foundationObject的folderGuid不为空，那么将新建好的数据放置该目录下<br>
	 * 否则将新建对象放置根目录<br>
	 * 如果原对象为libraire 那么将新建对象放置原对象的libraire的根目录，<br>
	 * 如果原对象为private,那么将新建对象放置源对象拥有者的根目录<br>
	 * 将前版本的BOM结构拷贝一份挂到新版本下<br>
	 * 根据关系模板中的配置<br>
	 * 关系模板中有事件关联的设置，如果"新建修订版"项的设置为NONE，则不创建该关联关系 <br>
	 * 如果"新建修订版"项的设置为LINK，则直接将原关系中关联的end2关联到新版本上 <br>
	 * 如果"新建修订版"项的设置为REVISELINK，则直接将原关系中关联的end2新建修订版并将新版end2关联到新版本上 <br>
	 * 递归处理 <br>
	 * 如果递归处理的时候任何一层出现异常或失败，那么继续处理同层别的end2
	 * 
	 * @param foundationObject
	 *            要新建修订版的对象
	 * @param isContainBom
	 *            是否携带BOMView
	 * @return 用相同信息创建以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject createRevision(FoundationObject foundationObject, boolean isContainBom) throws ServiceRequestException;

	/**
	 * 新建对象的修订版 创建后检出<br>
	 * 处理顺序及业务如下：<br>
	 * 清除guid<br>
	 * 给"拥有者"等字段赋值<br>
	 * 如果foundationObject的folderGuid不为空，那么将新建好的数据放置该目录下<br>
	 * 否则将新建对象放置根目录<br>
	 * 如果原对象为libraire 那么将新建对象放置原对象的libraire的根目录，<br>
	 * 如果原对象为private,那么将新建对象放置源对象拥有者的根目录<br>
	 * 将前版本的BOM结构拷贝一份挂到新版本下<br>
	 * 根据关系模板中的配置<br>
	 * 关系模板中有事件关联的设置，如果"新建修订版"项的设置为NONE，则不创建该关联关系 <br>
	 * 如果"新建修订版"项的设置为LINK，则直接将原关系中关联的end2关联到新版本上 <br>
	 * 如果"新建修订版"项的设置为REVISELINK，则直接将原关系中关联的end2新建修订版并将新版end2关联到新版本上 <br>
	 * 递归处理 <br>
	 * 如果递归处理的时候任何一层出现异常或失败，那么继续处理同层别的end2
	 * 创建完后将对象检出
	 * 
	 * @param foundationObject
	 *            要新建修订版的对象
	 * @param isContainBom
	 *            是否携带BOMView
	 * @return 用相同信息创建以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject createRevisionAndCheckOut(FoundationObject foundationObject, boolean isContainBom) throws ServiceRequestException;

	/**
	 * 删除对象，具体删除的是实例还是快捷方式由数据层根据传入的foundationObject判断<br>
	 * 直接调用数据层的删除方法
	 * 
	 * @param foundationObject
	 *            要删除对象
	 * @throws ServiceRequestException
	 */
	public void deleteObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 删除关联关系的处理 <br>
	 * 直接调用数据层提供的删除关系的方法
	 * 
	 * @param relation
	 *            要删除的关联关系对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteRelation(ViewObject relation) throws ServiceRequestException;

	/**
	 * 删除关联关系的处理,并删除end2 <br>
	 * 直接调用数据层提供的删除关系的方法
	 * 
	 * @param relation
	 *            要删除的关联关系对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteRelationAndEnd2(ViewObject relation) throws ServiceRequestException;

	/**
	 * 查询Object类型的字段显示内容:在模型中配置的字段的值(id\name\id+name等) <br>
	 * 如果参数objectGuid为空或者objectGuid中的classGuid和className都为空，那么返回空; <br>
	 * 如果根据objectGuid查到的对象为空，那么返回空; <br>
	 * 根据classGuid或者className取得对应的类,然后从类中取得instanceString,如果该值为空，那么返回id-name <br>
	 * 如果该值不为空,那么解析该值（用“+”号分割）：如果是字段 那么取得字段对应的值 如果是常量 那么取得常量的内容 将这些值拼接起来返回
	 * 
	 * @param objectGuid
	 * @return Object类型的字段显示的信息
	 * @throws ServiceRequestException
	 *             应用服务器异常
	 */
	public String getFoundationObjectViewValue(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 新建对象前获取一个版本ID <br>
	 * 如果版本配置选字母：A、B、C、...... 那么初始版本号为A <br>
	 * 如果版本配置选数字：1、2、3、...... 那么初始版本号为1 <br>
	 * 如果版本配置选枚举 那么初始版本号为枚举的第一个元素 <br>
	 * 如果什么都没选，那么默认为A 并允许用户修改
	 * 只有继承IVersionable接口的类才有版本号<br>
	 * 如果没有继承该接口，那么直接返回"--",否则按上面描述的处理
	 * 
	 * @param className
	 *            类名
	 * @return 初始版本号
	 * @throws ServiceRequestException
	 *             应用服务器异常
	 */
	public String getInitRevisionId(int startRevisionIdSequence) throws ServiceRequestException;

	/**
	 * 查询实例对象详细信息. <br>
	 * 该方法只用于查询非打开对象，即：不是在前台画面打开的对象可以调用该方法， <br>
	 * 如果是前台画面要打开对象，那么请不要使用该方法<br>
	 * 该方法除了获取系统字段外，还根据UITypeEnum.FORM获取字段列表对应的值
	 * 
	 * @param objectGuid
	 *            实例对象objectguid
	 * @return 实例对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public FoundationObject getObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 该方法为了获取Object类型字段对应的实例的界面展示数据，该方法只返回优先级最高的section类型的UI对应的字段的数据<br>
	 * 
	 * 该方法不做权限判断
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getObject4ObjectField(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 该方法为了获取Object类型字段对应的实例的界面展示数据，该方法只返回优先级最高的section类型的UI对应的字段的数据<br>
	 * 该方法限定同一个类使用.
	 * 
	 * 该方法不做权限判断
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getObject4ObjectField(List<ObjectGuid> objectGuidList) throws ServiceRequestException;

	/**
	 * 查询实例对象基本信息. <br>
	 * 该方法只用于查询非打开对象，即：不是在前台画面打开的对象可以调用该方法， <br>
	 * 如果是前台画面要打开对象，那么请不要使用该方法<br>
	 * 该方法只获取系统字段
	 * 
	 * @param guid
	 *            实例对象的guid
	 * @return 实例对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject getObjectByGuid(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 查询实例对象基本信息. <br>
	 * 如果对象在流程中打开，则不判断权限
	 * 该方法只用于查询非打开对象，即：不是在前台画面打开的对象可以调用该方法， <br>
	 * 如果是前台画面要打开对象，那么请不要使用该方法<br>
	 * 该方法只获取系统字段
	 * 
	 * @param guid
	 *            实例对象的guid
	 * @return 实例对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject getObjectOfProcrtByGuid(String guid, String classGuid, String procrtGuid) throws ServiceRequestException;

	/**
	 * 根据关联关系的objectGuid获取Relation对象
	 * 
	 * @param objectGuid
	 *            关联关系的objectGuid对象
	 * @return ViewObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public ViewObject getRelation(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据关联关系的objectGuid及关联关系的名字或者对应关系模板的名字获取ViewObject对象
	 * 
	 * @param end1ObjectGuid
	 *            关联关系的ObjectGuid对象
	 * @param name
	 *            ViewObject的名字或者关系模板的名字
	 * 
	 * @return ViewObject
	 * @throws ServiceRequestException
	 */
	public ViewObject getRelationByEND1(ObjectGuid end1ObjectGuid, String name) throws ServiceRequestException;

	/**
	 * 将一个对象end2关联到指定view对象上 <br>
	 * 实际是创建structureObject对象，该对象上有end2和view的关系的结构信息
	 * 
	 * @param viewObjectGuid
	 *            view对象的ObjectGuid
	 * @param end2FoundationObjectGuid
	 *            要关联的对象的ObjectGuid
	 * @param structureObject
	 *            结构关系的对象
	 * @return 创建出来的关联结构关系
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public StructureObject link(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 将end2对象直接关联到end1对象上，如果没有View存在，那么先用模板创建一个View然后再将end2对象挂上来
	 * 
	 * @param end1Object
	 * @param end2Object
	 * @param structureObject
	 * @param viewName
	 *            模板的名字
	 * @return 创建出来的关联结构关系
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器的没有对应BOM模板异常(ID_APP_NO_BOM_TEMPLATE)
	 */
	public StructureObject link(ObjectGuid end1Object, ObjectGuid end2Object, StructureObject structureObject, String viewName) throws ServiceRequestException;

	/**
	 * 查询实例对象列表<br>
	 * 
	 * 非界面简单查询并且非界面高级查询的时候用该方法
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> listObject(SearchCondition condition) throws ServiceRequestException;

	/**
	 * 查询实例对象列表<br>
	 * 
	 * 供前台检索的时候使用<br>
	 * 
	 * 该方法会保存"查询历史"
	 * 
	 * @param condition
	 * @param searchGuid
	 *            检索历史实例的Guid,新的检索条件时该值为空
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> listObjectBySearch(SearchCondition condition, String searchGuid) throws ServiceRequestException;

	/**
	 * 根据指定ObjectGuid(Guid一定要有值)，获取指定的该实例对应的revision的Iteration或者IterationList
	 * 
	 * @param objectGuid
	 * @param iterationId
	 *            可以传空，如果传空那么返回的是所有的Iteration集合，<br>
	 *            如果传值，那么返回对应的IterationId所对应的实例,结果集中只有一条记录
	 * @return
	 */
	public abstract List<FoundationObject> listObjectIteration(ObjectGuid objectGuid, Integer iterationId) throws ServiceRequestException;

	/**
	 * 根据指定ObjectGuid(Guid一定要有值)，回滚到指定实例指定的某个小版本
	 * 
	 * @param objectGuid
	 *            revision.objectguid
	 * @param iterationId
	 *            小版本id,不可为空
	 * @throws ServiceRequestException
	 */
	public abstract void rollbackObjectIteration(ObjectGuid objectGuid, Integer iterationId) throws ServiceRequestException;

	/**
	 * 获取已知view对象上关联的所有结构
	 * 
	 * @param viewObject
	 *            view对象的ObjectGuid
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的StructureObject对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<StructureObject> listObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException;

	/**
	 * 查询明细类型的view对象上关联的end2信息<br>
	 * 查询时，不判断权限
	 * 把结构信息放入对应的end2实例内并返回,key值为StructureObject.STRUCTURE_CLASS_NAME
	 * 
	 * @param viewObject
	 *            view对象的ObjectGuid
	 * @param structureSearchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的end2列表, 其中每条fo记录的StructureObject字段中存储的是Structure对象信息
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listFoundationObjectOfRelation4Detail(ObjectGuid viewObject, SearchCondition structureSearchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException;

	/**
	 * 获取已知view对象上关联的所有结构（仅供前台显示表格样式时使用）<br>
	 * 把结构信息放入对应的end2实例内并返回,key值为StructureObject.STRUCTURE_CLASS_NAME<br>
	 * 返回的end2中仅包含系统字段
	 * 
	 * @param viewObject
	 *            view对象的ObjectGuid
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的end2列表, 其中每条fo记录的StructureObject字段中存储的是Structure对象信息
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuthority) throws ServiceRequestException;

	/**
	 * 根据类型获取已知end1ObjectGuid对象上关联的所有结构
	 * 
	 * @param end1ObjectGuid
	 *            end1对象的ObjectGuid
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的StructureObject对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<StructureObject> listObjectOfRelation(ObjectGuid end1ObjectGuid, String viewName, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException;

	/**
	 * 根据已知对象获取该对象对应master下的所有版本对象,返回对象中所有字段<br>
	 * 
	 * 
	 * @param objectGuid
	 *            FoundationObject对象的objectGuid
	 * @return 对象的历史版本列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> listObjectRevisionHistoryForMaster(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据已知对象获取该对象对应master下的所有版本对象<br>
	 * 按照创建时间排序
	 * 
	 * @param objectGuid
	 *            FoundationObject对象的objectGuid
	 * @return 对象的历史版本列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> listObjectRevisionHistory(ObjectGuid objectGuid) throws ServiceRequestException;

	// /**
	// * 根据已知对象获取该对象对应master下的所有版本中状态为WIP的对象<br>
	// * 按照创建时间排序
	// *
	// * @param objectGuid
	// * FoundationObject对象的objectGuid
	// * @return 对象的历史WIP版本列表
	// * @throws ServiceRequestException
	// * 数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	// */
	// public List<FoundationObject> listObjectRevisionHistoryOnlyWIP(ObjectGuid objectGuid)
	// throws ServiceRequestException;

	/**
	 * 获取指定end1对象的所有的Relation
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @return Relation列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<ViewObject> listRelation(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 获取指定end1对象的所有的Relation,不包含内置关系
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @return Relation列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<ViewObject> listRelationWithOutBuiltIn(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据StructureObject的guid取得 StructureObject对象
	 * 
	 * @param structureObjectGuid
	 * @param searchCondition
	 *            该参数为必传参数, 用createSearchConditionForStructure生成
	 *            其中Structure对应的className必传
	 * 
	 * @return StructureObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public StructureObject getStructureObject(ObjectGuid structureObjectGuid, SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 根据StructureObject的guid取得 StructureObject对象
	 * 
	 * @param structureObjectGuid
	 * @param searchCondition
	 *            该参数为必传参数, 用createSearchConditionForStructure生成
	 *            其中Structure对应的className必传
	 * 
	 * @return StructureObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public StructureObject getStructureObject(ObjectGuid structureObjectGuid, SearchCondition searchCondition, DataRule dataRule) throws ServiceRequestException;

	/**
	 * 根据END2与指定的VIEW的NAME 去查找该名字的View上对应的END1的列表
	 * 
	 * @param end1ObjectGuid
	 * @param viewName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listWhereReferenced(ObjectGuid end2ObjectGuid, String viewName, SearchCondition end1SearchCondition, DataRule dataRule)
			throws ServiceRequestException;

	/**
	 * 从一个文件夹转移对象至指定文件夹（剪切粘贴） <br>
	 * 将实例与原文件夹的关系解除，同时创建新的实力与转移至的文件夹的关系<br>
	 * 关系模板中有事件关联的设置，如果"改变位置"项的设置为真， <br>
	 * 那么转移对象位置的时候将使用该模板创建的关系的end2对象也转移位置处理 <br>
	 * 递归处理
	 * 
	 * @param objectGuid
	 *            要转移的对象的ObjectGuid
	 * @param fromFolderGuid
	 * @param toFolderGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void moveToFolder(ObjectGuid objectGuid, String fromFolderGuid, String toFolderGuid) throws ServiceRequestException;

	/**
	 * 初始化Foundation实例<br>
	 * 该方法供前台获取初始的要创建的类的实例，主要提供模型中设置的字段的初始值<br>
	 * 参数中classGuid及className二者传其一即可，如果都传值，那么以classGuid为准<br>
	 * 返回的实例只供前台使用，数据库中并不存在
	 * 
	 * @param classGuid
	 * @param className
	 * @return FoundationObject
	 * @throws ServiceRequestException
	 */
	public FoundationObject newFoundationObject(String classGuid, String className) throws ServiceRequestException;

	/**
	 * 初始化Foundation实例<br>
	 * 该方法供前台获取初始的要创建的类的实例，主要提供模型中设置的字段的初始值<br>
	 * 参数中classGuid及className二者传其一即可，如果都传值，那么以classGuid为准<br>
	 * 返回的实例只供前台使用，数据库中并不存在
	 * 根据classificationGuid 得到分类初始对象，放入实例对象中
	 * 
	 * @param classGuid
	 * @param className
	 * @param classificationGuid
	 *            主分类guid
	 * @return FoundationObject
	 * @throws ServiceRequestException
	 */
	public FoundationObject newFoundationObject(String classGuid, String className, String classificationGuid) throws ServiceRequestException;

	/**
	 * 初始化Foundation实例<br>
	 * 该方法供前台获取初始的要创建的类的实例，主要提供模型中设置的字段的初始值<br>
	 * 参数中classificationItemGuid<br>
	 * 返回的实例只供前台使用，数据库中并不存在
	 * 
	 * @param classGuid
	 * @param className
	 * @return FoundationObject
	 * @throws ServiceRequestException
	 */
	public FoundationObject newClassificationFoundation(String classificationItemGuid, FoundationObject oriClassificationFoundation) throws ServiceRequestException;

	/**
	 * 初始化StructureObject实例<br>
	 * 该方法供前台获取初始的要创建的结构关系的实例，主要提供模型中设置的字段的初始值<br>
	 * 参数中classGuid及className二者传其一即可，如果都传值，那么以classGuid为准<br>
	 * 返回的实例只供前台使用，数据库中并不存在
	 * 
	 * @param classGuid
	 * @param className
	 * @return StructureObject
	 * @throws ServiceRequestException
	 */
	public StructureObject newStructureObject(String classGuid, String className) throws ServiceRequestException;

	/**
	 * 根据模板的templateObjectGuid初始化Foundation实例<br>
	 * 该方法供前台获取初始的要创建的类的实例，主要提供模型中设置的字段的初始值<br>
	 * 从模板实例中获取该类中字段列表所对应的值传入新初始化的实例对象中<br>
	 * 参数中classGuid及className二者传其一即可，如果都传值，那么以classGuid为准<br>
	 * 返回的实例只供前台使用，数据库中并不存在
	 * 
	 * @param classGuid
	 * @param className
	 * @param templateObjectGuid
	 *            可以为空 如果为空跟newFoundationObject(String classGuid,String className)返回值一致
	 * @return FoundationObject
	 * @throws ServiceRequestException
	 */
	public FoundationObject newFoundationObject(String classGuid, String className, ObjectGuid templateObjectGuid) throws ServiceRequestException;

	/**
	 * 前台画面的专用打开实例对象详细信息的方法. <br>
	 * 如果不是打开实例对象不能使用该方法
	 * 
	 * @param objectGuid
	 *            实例对象objectguid
	 * @return 实例对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public FoundationObject openObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 前台画面的专用打开实例对象详细信息的方法. <br>
	 * 返回所有前台使用到的数据
	 * 
	 * @param objectGuid
	 *            实例对象objectguid
	 * @param isView
	 *            true 返回view相关信息
	 *            false 返回实例相关信息
	 * @param processRuntimeGuid
	 * @return 实例对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public OpenInstanceModel openInstance(ObjectGuid objectGuid, boolean isView, String processRuntimeGuid) throws ServiceRequestException;

	/**
	 * 打开共享文件夹内实例对象, 需要传输共享文件夹的guid<br>
	 * 该实例对象不存在于指定文件夹内, 或者该文件夹的查看权限未开放时, 将返回受限异常
	 * 
	 * @param objectGuid
	 *            实例对象的guid
	 * @param sharedFolderGuid
	 *            共享文件夹的guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getObject(ObjectGuid objectGuid, String sharedFolderGuid) throws ServiceRequestException;

	/**
	 * 获取新建修订版的"建议版本"对象 <br>
	 * 该方法主要是获取下一个版本的版本号 <br>
	 * 先获取数据库中已有的版本数，再根据版本配置规则，获取数量加1的位置的版本号 <br>
	 * 如果手动输入版本号，那么将原最大版本号加1，字母的话就增1
	 * 
	 * @param objectGuid
	 * @return 建议版本FoundationObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject prepareRevision(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 用相同信息创建对象 选择关联原BOM关系 <br>
	 * 选择关联原关联关系<br>
	 * 是否复制局部替代
	 * 是否复制全局替代关系
	 * 
	 * @param foundationObject
	 *            新建的对象
	 * @param isContainBom
	 *            是否携带BOMView
	 * @param isContainPartReplace
	 * @param isContainGlobalReplce
	 * @return 用相同信息创建以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject saveAsObject(FoundationObject foundationObject, boolean isContainBom, boolean isContainPartReplace, boolean isContainGlobalReplce)
			throws ServiceRequestException;

	/**
	 * 用相同信息创建对象 选择关联原BOM关系 <br>
	 * 选择关联原关联关系<br>
	 * 创建好后将对象检出
	 * 
	 * @param foundationObject
	 *            新建的对象
	 * @param isContainBom
	 *            是否携带BOMView
	 * @return 用相同信息创建以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject saveAsObjectAndCheckOut(FoundationObject foundationObject, boolean isContainBom, boolean isContainPartReplace, boolean isContainGlobalReplce)
			throws ServiceRequestException;

	/**
	 * 更新对象 <br>
	 * 判断有没有必填字段没填值 如果存在 直接抛异常信息<br>
	 * 其中ID，生命周期阶段，masterName，拥有者等信息不可用此方法修改
	 * 
	 * @param foundationObject
	 *            要更新的对象
	 * @return 更新以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 更新对象, 修改的fo为明细, 部分/工厂关系的end2则不需要检出检入操作
	 * 否则等同于方法<code>saveObject(FoundationObject)</code><br>
	 * 
	 * @param foundationObject
	 *            要更新的对象
	 * @param structureModel
	 *            结构信息, 用于判断是否为明细, 部分/工厂关系结构
	 * @return 更新以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject saveObject(FoundationObject foundationObject, RelationTemplateTypeEnum structureModel) throws ServiceRequestException;

	/**
	 * 创建/更新对象 没有返回值 <br>
	 * 与saveObject(FoundationObject foundationObject)相同 但是没有返回值
	 * 
	 * @param foundationObject
	 *            要创建/更新的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void saveObjectOnly(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 添加/更新一个关联关系对象<br>
	 * 更新时：只有WIP状态的关系才能被更新<br>
	 * 创建时如果不是用模板创建那么ID为VIEW_APPOINTED_ID = "32AA50EA06FA4D4096E454A063BAA2AF";<br>
	 * 如果是用模板创建 那么ID及NAME取自模板的ID及NAME
	 * 
	 * @param relation
	 *            关联关系的对象
	 * @return 刚添加/更新的Relation对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ViewObject saveRelation(ViewObject relation) throws ServiceRequestException;

	/**
	 * 根据模板创建 一个关联关系对象<br>
	 * 一个模板在一个对象上只能创建一个relation,如果重复创建报异常<br>
	 * 创建relation的时候会把模板的ID与NAME赋值给relation做ID和NAME
	 * 
	 * @param relationTemplateGuid
	 *            关联关系模板的guid
	 * @param end1ObjectGuid
	 *            要关联的对象的ObjectGuid
	 * @return 刚创建的Relation对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及重复创建异常(ID_APP_MULTI_CREATE_RELATION)
	 */
	public ViewObject saveRelationByTemplate(String relationTemplateGuid, ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 更新StructureObject对象 并返回更新后的StructureObject对象
	 * 
	 * @param structureObject
	 * @return StructureObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public StructureObject saveStructure(StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 更新明细列表<br>
	 * 
	 * @param viewObjectGuid
	 *            关系视图 objectguid
	 * @param linkList
	 *            需要新增的end2列表, 其中StructureObject字段存储的是structure信息
	 * @param unlinkList
	 *            需要删除的end2列表
	 * @param updateList
	 *            需要更新的end2列表
	 * @throws ServiceRequestException
	 */
	public void saveStructure4Detail(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList)
			throws ServiceRequestException;

	/**
	 * 批量保存结构<br>
	 * 
	 * @param end1ObjectGuid
	 *            end1 objectguid
	 * @param name
	 *            ViewObject的名字或者关系模板的名字
	 * @param linkList
	 *            需要新增的end2列表, 其中StructureObject字段存储的是structure信息
	 * @param unlinkList
	 *            需要删除的end2列表
	 * @param updateList
	 *            需要更新的end2列表
	 * @throws ServiceRequestException
	 */
	public void saveStructureBatch(ObjectGuid end1ObjectGuid, String name, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList)
			throws ServiceRequestException;;

	/**
	 * 批量保存结构<br>
	 * 
	 * @param viewObjectGuid
	 *            关系视图 objectguid
	 * @param linkList
	 *            需要新增的end2列表, 其中StructureObject字段存储的是structure信息
	 * @param unlinkList
	 *            需要删除的end2列表
	 * @param updateList
	 *            需要更新的end2列表
	 * @throws ServiceRequestException
	 */
	public void saveStructureBatch(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList)
			throws ServiceRequestException;

	/**
	 * 检出状态迁移 ——把一个对象的检出者由有权限用户迁移给另外一个人 <br>
	 * 关系模板中有事件关联的设置，如果"移交检出"项的设置为真， <br>
	 * 那么移交检出对象的时候将使用该模板创建的关系的end2对象也移交检出处理 <br>
	 * 根据参数isDealBom决定是否同时移交检出可移交的BOMView<br>
	 * 可移交检出的BOMView包括操作者检出的BOMView及处于检入状态的BOMView
	 * 
	 * @param foundationObject
	 *            要迁移的对象
	 * @param toUserGuid
	 *            要迁移给的用户
	 * @param locale
	 *            所用语言
	 * @param isDealBom
	 *            是否同时移交由操作者检出的BOMView及检出检入状态的BOMView
	 * @return 迁移后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public FoundationObject transferCheckout(FoundationObject foundationObject, String toUserGuid, String locale, boolean isDealBom) throws ServiceRequestException;

	/**
	 * 从view对象上解除与end2对象的所有关联关系
	 * 
	 * @param viewObjectGuid
	 *            view对象的ObjectGuid
	 * @param end2FoundationObjectGuid
	 *            要解除的对象的ObjectGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlink(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid) throws ServiceRequestException;

	/**
	 * 通过end1,template与end2对象的所有关联关系
	 * 
	 * @param end1ObjectGuid
	 *            end1对象的ObjectGuid
	 * @param templateName
	 *            模板名
	 * @param end2FoundationObjectGuid
	 *            要解除的对象的ObjectGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlink(ObjectGuid end1ObjectGuid, String templateName, ObjectGuid end2FoundationObjectGuid) throws ServiceRequestException;

	/**
	 * 根据structure解除关联关系<br>
	 * 即：删除structureObject对象
	 * 
	 * @param structureObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlink(StructureObject structureObject) throws ServiceRequestException;

	// /**
	// * 更新Master的id,name,alterid三个字段的信息
	// *
	// * @param foundationObject
	// * @return
	// * @throws ServiceRequestException
	// */
	// public FoundationObject updateMasterData(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 更新对象的拥有者信息 <br>
	 * 直接调用数据层提供的更新拥有者信息的方法
	 * 
	 * @param objectGuid
	 * @param ownerUserGuid
	 * @param ownerGroupGuid
	 * @param updateTime
	 *            该参数是画面上显示的更新时间，为了处理并发
	 * @param containBomView
	 *            是否同时修改bomview的owner user
	 * @return 修改后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject updateOwnerUser(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime, boolean containBomView)
			throws ServiceRequestException;

	/**
	 * 更新对象的拥有者信息 <br>
	 * 直接调用数据层提供的更新拥有者信息的方法
	 * 不判断任何权限
	 * 
	 * @param objectGuid
	 * @param ownerUserGuid
	 * @param ownerGroupGuid
	 * @param updateTime
	 *            该参数是画面上显示的更新时间，为了处理并发
	 * @return 修改后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject updateOwnerUserInLib(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime) throws ServiceRequestException;

	// /**
	// * 更新对象的版本Id <br>
	// * 直接调用数据层提供的方法
	// *
	// * @param objectGuid
	// * @param revisionId
	// * @param updateTime
	// * @return 修改后的对象
	// * @throws ServiceRequestException
	// * 数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	// */
	// public FoundationObject updateRevisionId(ObjectGuid objectGuid, String revisionId, Date updateTime)
	// throws ServiceRequestException;

	/**
	 * 获取实例对象的预览信息<br>
	 * 将对象的相关属性转化为字符串流
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getFoundationPreviewInfo(ObjectGuid objectGuid, LanguageEnum lang) throws ServiceRequestException;

	/**
	 * 根据GUID删除FoundationObject实例<br>
	 * 先根据guid获取对应的实例对象<br>
	 * 然后根据该实例对象的ObjectGuid删除该实例对象
	 * 
	 * @param foundationObjectGuid
	 *            foundationObjectGuid
	 * @throws ServiceRequestException
	 */
	public void deleteFoundationObject(String foundationObjectGuid, String classGuid) throws ServiceRequestException;

	/**
	 * 废弃实例<br>
	 * 给实例设置废弃时间
	 * 
	 * @param objectGuid
	 * @param obsoleteTime
	 * @throws ServiceRequestException
	 */
	@Deprecated
	public void obsoleteObject(ObjectGuid objectGuid, Date obsoleteTime) throws ServiceRequestException;

	/**
	 * 取消废弃<br>
	 * 同时将相关的BOMView也一起取消废弃<br>
	 * 仅管理员组有权限
	 * 
	 * @param objectGuid
	 * 
	 * @throws ServiceRequestException
	 */
	public void cancelObsolete(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 设置导入至ERP<br>
	 * 
	 * 该方法供ERP导入处使用<br>
	 * 
	 * 将FoundationObject的isExportToERP值为true
	 * 
	 * @param objectGuid
	 * 
	 * @throws ServiceRequestException
	 */
	public void setIsExportToERP(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据实例guid查找出关联的所有文件夹<br>
	 * 
	 * 前台主要使用该Folder结果内的guid和hierarchy(路径)
	 * 
	 * @param foundationObjectGuid
	 * @return Folde列表
	 * @throws DynaDataException
	 */
	public List<Folder> listFolder(ObjectGuid foundationObjectGuid) throws ServiceRequestException;

	/**
	 * 判断bo对应的配置，是否创建即入库
	 * 
	 * @param boGuid
	 * @return 是：true;否：false
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public boolean isCreateCommit(String boGuid) throws ServiceRequestException;

	/**
	 * 判断class对应的配置，是否创建即入库
	 * 
	 * @param className
	 * @return 是：true;否：false
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public boolean isCreateCommitByClassName(String className) throws ServiceRequestException;

	/**
	 * 获取指定的master实例,该实例返回值只有ID,NAME,ALTERID,CLASSGUID,CLASSNAME等几个字段信息
	 * 
	 * @param objectGuid
	 * @return FoundationObject
	 * @throws ServiceRequestException
	 */
	public FoundationObject getMaster(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 删除master
	 * 遍历master下所有的版本，
	 * 1、存在检出状态则不能删除 抛出异常
	 * 2、存在PRE、RLS、OBS状态的不能删除 抛出异常
	 * 3、在需要判断权限的情况下，如果当前用户对其中一个版本没有权限则不能删除 抛出异常
	 * 4、数据处在工作流或者已经删除 抛出异常
	 * 删除master，会关联删除所有版本
	 * 
	 * @param masterGuid
	 *            master的guid
	 * @throws DynaDataException
	 */
	public void deleteMaster(String masterGuid, String classGuid) throws ServiceRequestException;

	/**
	 * 根据structure解除关联关系,并且删除end2
	 * 
	 * @param structureObject
	 * 
	 * @throws ServiceRequestException
	 */
	public void unlinkAndDeleteEnd2(StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 变更关系结构中的主对象
	 * 
	 * @param end1ObjectGuid
	 * @param viewName
	 * @param structureObject
	 * @throws ServiceRequestException
	 */
	public void changePrimaryObject(ObjectGuid end1ObjectGuid, String viewName, StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 停用实例<br>
	 * 
	 * 
	 * @param objectGuid
	 * @param obsoleteTime
	 * @throws ServiceRequestException
	 */
	public void stopUsingObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 启用实例<br>
	 * 
	 * 
	 * @param objectGuid
	 * @param obsoleteTime
	 * @throws ServiceRequestException
	 */
	public void startUsingObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 检验对象是否重复
	 * 
	 * @param foundation
	 * @param isSaveAs
	 * @return 返回空值时，无重复。返回值不为空时，则为重复提示信息
	 * @throws ServiceRequestException
	 */
	public String checkFoundationRepeat(FoundationObject foundation, boolean isSaveAs) throws ServiceRequestException;

	public void editRelation(ObjectGuid end1ObjectGuid, String templateName, List<ObjectGuid> end2FoundationObjectGuidList, List<StructureObject> structureObjectList,
			String procRtGuid, boolean isReplace) throws ServiceRequestException;

	/**
	 * 删除关联数据
	 * 1、删除end2时，删除结构
	 * 2、删除end1时，删除BOM，View和结构
	 * 3、删除BOM和View时，删除结构
	 * 
	 * @param objectGuid
	 * @param exceptionParameter
	 * @throws ServiceRequestException
	 */
	public void deleteReference(ObjectGuid objectGuid, String exceptionParameter) throws ServiceRequestException;

	/**
	 * 一般情况请不要使用该查询，目前仅在产品配置材料明细结构中使用（因为模板可以关联多接口的子阶业务对象，所以需要跨表查询）,需要根据模板进行查询，只返回有限的几个系统字段,且只做普通对象查询，不做关联查询
	 * 
	 * @param searchKey
	 * @param rowCntPerPate
	 * @param pageNum
	 * @param caseSensitive
	 *            是否区分大小写
	 * @param isEquals
	 *            是否完全匹配（可以和caseSensitive一起使用）
	 * @param isOnlyId
	 *            是否仅仅查询编号。TRUE：只查询编号；false：查询编号/名称/alterid
	 * @param boNameList
	 *            搜索时限制的BO列表，不能为空
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> quickQuery(String searchKey, int rowCntPerPate, int pageNum, boolean caseSensitive, boolean isEquals, boolean isOnlyId, List<String> boNameList)
			throws ServiceRequestException;

	/**
	 * 刷新指定类指定合成字段的所有数据(给顾问使用,不能在系统中调用)
	 * 
	 * @param className
	 * @param fieldName
	 * @throws ServiceRequestException
	 */
	public void refreshMergeFieldValue(String className, String fieldName) throws ServiceRequestException;

	/**
	 * 刷新指定类指定合成字段的所有数据(给顾问使用,不能在系统中调用)
	 * 
	 * @param className
	 * @param fieldNameSet
	 * @param classificationGuid
	 * @throws ServiceRequestException
	 */
	public void refreshMergeFieldValue(String className, Set<String> fieldNameSet, String classificationGuid) throws ServiceRequestException;

	/**
	 * 流程附件是否能够停用
	 * 
	 * @param foundationObject
	 * @param procrtGuid
	 * @throws ServiceRequestException
	 */
	public void canStop(FoundationObject foundationObject, String procrtGuid) throws ServiceRequestException;

	/**
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getObjectNotCheckAuthor(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 不通过工作流，把实例直接发布。
	 * 
	 * @param FoundaionObject
	 * @param objectGuid
	 */
	public void release(ObjectGuid objectGuid) throws ServiceRequestException;

	public void createByTemplate(ObjectGuid objectGuid, ObjectGuid objectGuid2, String name) throws ServiceRequestException;
	
	/**
	 * 在END1上记录是否存在END2数据，一般用在异步任务中
	 * 
	 * @param end1ObjectGuid
	 * @param relationTemplateGuid
	 * @throws ServiceRequestException
	 */
	public void updateHasEnd2Flg(ObjectGuid end1ObjectGuid, String relationTemplateGuid) throws ServiceRequestException;
}
