/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CAD Service
 * WangLHB 2011-3-24
 */
package dyna.net.service.brs;

import java.rmi.ServerException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dyna.common.Criterion;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.iopconfigparamter.IOPColumnTitle;
import dyna.common.bean.data.iopconfigparamter.IOPColumnValue;
import dyna.common.bean.data.iopconfigparamter.IOPConfigParameter;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.extra.Message;
import dyna.common.bean.extra.ReturnModel;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.net.service.Service;

/**
 * CAD相关服务
 * 
 * @author WangLHB
 * 
 */
public interface CAD extends Service
{

	/**
	 * 通过2D模型取得3D模型 <br>
	 * 
	 * 
	 * @param objectGuid2D
	 *            objectGuid2D
	 * 
	 * @return FoundationObject对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<FoundationObject> list3DModelBy2DModel(ObjectGuid objectGuid2D) throws ServiceRequestException;

	/**
	 * 通过3D模型取得2D模型 <br>
	 * 
	 * @param objectGuid3D
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> list2DModelBy3DModel(ObjectGuid objectGuid3D) throws ServiceRequestException;

	/**
	 * 通过模型取得物料，根据模型类型判断是否为3D，2D，电子 <br>
	 * 
	 * @param modelObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listItemByModel(ObjectGuid modelObjectGuid, boolean isAllField) throws ServiceRequestException;

	/**
	 * 通过物料，模板名称取得模型
	 * 
	 * @param itemobjectGuid
	 * @param templateName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listModelByItem(ObjectGuid itemobjectGuid, String templateName) throws ServiceRequestException;

	/**
	 * 创建关联关系
	 * 
	 * @param end1ObjectGuid
	 * @param templateName
	 * @param end2ObjectGuidList
	 * @param isUpdate
	 *            是否更新
	 * @throws ServiceRequestException
	 */
	public void batchLink(ObjectGuid end1ObjectGuid, String templateName, List<ObjectGuid> end2ObjectGuidList, boolean isUpdate) throws ServiceRequestException;

	/**
	 * 读取配置文件
	 * 
	 * @param fileName
	 * @param typeEnum
	 * @return
	 * @throws ServiceRequestException
	 */
	public String read(String fileName, ApplicationTypeEnum typeEnum) throws ServiceRequestException;

	/**
	 * 保存配置文件
	 * 
	 * @param fileName
	 * @param xmlStream
	 * @param typeEnum
	 * @throws ServiceRequestException
	 */
	public void write(String fileName, String xmlStream, ApplicationTypeEnum typeEnum) throws ServiceRequestException;

	/**
	 * 保存CAD模型对象，同时保存对应的物料对象
	 * 
	 * @param foundationObject
	 * @param configItemList
	 * @return FoundationObject CAD模型对象
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 更新族表实例的物料关联
	 * 
	 * @param guid
	 * @param classname
	 * @param genricItemClassGuid
	 * @param genricItemMasterGuid
	 * @param GenericItemGuid
	 * @param configInstanceName
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveRelationOfConfigItemAndMaterial(String guid, String classname, String genricItemClassGuid, String genricItemMasterGuid, String GenericItemGuid,
			String configInstanceName) throws ServiceRequestException;

	/**
	 * 创建CAD对象,同时创建对应的物料对象
	 * 
	 * @param foundationObject
	 * @param isCheckOut
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject createObject(FoundationObject foundationObject, boolean isCheckOut) throws ServiceRequestException;

	/**
	 * 通过end1取得其下的所有BOMStructure(树形结构，仅一层)
	 * 
	 * @param end1ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOMStructure> listBOM(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 通过end1，cadtemplatename,取得其下的StructureObject(仅一层)
	 * 
	 * @param end1ObjectGuid
	 * @param cadTemplateName
	 * @param end1ConfigInstance
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> listModel(ObjectGuid end1ObjectGuid, String cadTemplateName, String end1ConfigInstance) throws ServiceRequestException;

	/**
	 * 通过end1，cadtemplatename,判断是否存在end2
	 * 
	 * @param end1ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasChildModel(ObjectGuid end1ObjectGuid, String cadTemplateName, String parentConfigInstance) throws ServiceRequestException;

	/**
	 * 
	 * 通过CAD类型取得其下的所有对象
	 * 
	 * @param cadType
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listCADTemplateObjectByType(String cadType, ModelInterfaceEnum interfaceEnum) throws ServiceRequestException;

	/**
	 * 根据条件查询对象
	 * 
	 * @param interfaceEnum
	 * @param boName
	 * @param folderGuid
	 * @param criterionList
	 * @param pageSize
	 * @param pageNum
	 * @param sortField
	 * @param isAsc
	 * @return 符合条件的对象
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> search(String boName, String folderGuid, List<Criterion> criterionList, int pageSize, int pageNum, Boolean[] conditions, String sortField,
			String advancedQueryType, boolean isAsc) throws ServiceRequestException;

	/**
	 * 取得 对象详细信息，包含主文件的MD5码
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 通过ID,ClassName取得对象详细信息，包含主文件的MD5码
	 * 
	 * @param id
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getObjectByID(String id, String className) throws ServiceRequestException;

	/**
	 * 批量上传文件
	 * 
	 * @param objectGuidList
	 * @param isContainNonMaster
	 *            是否是包含非文件
	 * @param fileFix
	 *            后缀名
	 * @return
	 * @throws ServiceRequestException
	 */
	public ReturnModel<DSSFileTrans> batchDownloadFile(List<DSSFileInfo> objectGuidList, boolean isContainNonMaster, String fileFix) throws ServiceRequestException;

	/**
	 * 批量上传文件
	 * 
	 * @param fileInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans batchUploadFile(List<DSSFileInfo> fileInfoList) throws ServiceRequestException;

	/**
	 * 批量checkin
	 * 
	 * @param objectGuidList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Message> batchCheckIn(List<ObjectGuid> objectGuidList) throws ServiceRequestException;

	/**
	 * 批量checkout
	 * 
	 * @param objectGuidList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Message> batchCheckOut(List<ObjectGuid> objectGuidList) throws ServiceRequestException;

	/**
	 * 批量batchcancelCheckOut
	 * 
	 * @param objectGuidList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Message> batchcancelCheckOut(List<ObjectGuid> objectGuidList) throws ServiceRequestException;

	/**
	 * 保存BOM，删除原有的BOM结构，重新创建BOM（仅在电子CAD中使用）
	 * 
	 * @param end1ObjectGuid
	 * @param bomStructureList
	 * @param isPrecise
	 */
	public List<Message> updateBOM(ObjectGuid end1ObjectGuid, List<BOMStructure> bomStructureList, boolean isPrecise) throws ServiceRequestException;

	/**
	 * 取得板材BOM结构，其中包含板材ID，className(电子CAD专用)
	 * 
	 * @param end1ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOMStructure> listBoardMaterialInBOM(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 批量上传预览文件
	 * 
	 * @param fileInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public ReturnModel<DSSFileTrans> batchUploadPreviewFile(List<DSSFileInfo> fileInfoList) throws ServiceRequestException;

	/**
	 * 批量下载传预览文件
	 * 
	 * @param fileInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public ReturnModel<DSSFileTrans> batchDownloadPreviewFile(List<DSSFileInfo> fileInfoList) throws ServiceRequestException;

	/**
	 * 批量上传缩略图文件
	 * 
	 * @param fileInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public ReturnModel<DSSFileTrans> batchUploadIconFile(List<DSSFileInfo> fileInfoList) throws ServiceRequestException;

	/**
	 * 批量下载缩略图文件
	 * 
	 * @param fileInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public ReturnModel<DSSFileTrans> batchDownloadIconFile(List<DSSFileInfo> fileInfoList) throws ServiceRequestException;

	/**
	 * 批量取得文件信息
	 * 
	 * @param objectGuidList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listMasterFileByObjectGuid(List<ObjectGuid> objectGuidList) throws ServiceRequestException;

	/**
	 * 通过ID,className 批量取得foundation
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listObjectByID(List<FoundationObject> idList, boolean isAllField) throws ServiceRequestException;

	/**
	 * 替换item关联的CAD, 如替换前未有关联, 则建立关联关系<br>
	 * 其中CAD是end1,item从cadFrom的end1上解除关系 然后再关联到cadTo对应的CAD上
	 * 
	 * @param cadFrom
	 * @param cadTo
	 * @param itemObjectGuid
	 * @param templateName
	 * @throws ServiceRequestException
	 */
	public void replaceCADByItem(ObjectGuid cadFrom, ObjectGuid cadTo, ObjectGuid itemObjectGuid, String templateName) throws ServiceRequestException;

	/**
	 * 解除物料与CAD的关系
	 * 
	 * @param itemObjectGuid
	 *            end1对象的ObjectGuid
	 * @param templateName
	 *            模板名
	 * @param cadObjectGuid
	 *            要解除的对象的ObjectGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlinkCAD(ObjectGuid itemObjectGuid, String templateName, ObjectGuid cadObjectGuid) throws ServiceRequestException;

	/**
	 * 通过end1和应用类型，取得其下的BOMView
	 * 
	 * @param end1ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOMView getBOMView(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 通过CAD类型取得BOM模板，再取得BOM和END2中的字段信息
	 * 
	 * @param end1ObjectGuid
	 *            上层物料ObjectGuid
	 * @return
	 * @throws ServerException
	 */
	public List<BOMStructure> listBOMWithEnd2(ObjectGuid end1ObjectGuid, List<String> end2FieldList) throws ServiceRequestException;

	/**
	 * 根据END1的ObjectGuid与BOM模板的名字获取对应的BOM模板
	 * 
	 * @param objectGuid
	 * @return BOM模板
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public BOMTemplateInfo getBOMTemplateByName(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 取得 对象详细信息
	 * 
	 * @param objectGuidList
	 * @param isSystem
	 *            是否系统
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listObject(List<ObjectGuid> objectGuidList, boolean isSystem) throws ServiceRequestException;

	public void linkDrawing(ObjectGuid end1ObjectGuid, String templateName, List<ObjectGuid> end2ObjectGuidList, boolean isUpdate) throws ServiceRequestException;

	/**
	 * 检测对象是否存在，返回无效ClassNameID键值对的列表
	 * 
	 * @param objectList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> checkClassNameAndIDValidity(List<FoundationObject> objectList) throws ServiceRequestException;

	/**
	 * 批量更新模型图与工程图的关联关系
	 * 
	 * @param structureList
	 * @param isUpdate
	 * @throws ServiceRequestException
	 */
	public void batchLinkModel(String templateName, List<StructureObject> structureList, boolean isUpdate) throws ServiceRequestException;

	/**
	 * 获取特定关系上的end2对象列表
	 * 
	 * @param end1ObjectGuid
	 * @param templateName
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid end1ObjectGuid, String templateName, boolean isCheckACL) throws ServiceRequestException;

	/**
	 * 获取模型特定关系上的关联对象列表（递归获取）
	 * 
	 * @param end1ObjectGuid
	 * @param templateName
	 * @param isCheckACL
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> recursionGetObjectOfRelation(ObjectGuid end1ObjectGuid, String templateName, boolean isCheckACL) throws ServiceRequestException;

	/**
	 * 批量获取模型特定关系上的关联对象列表（递归获取）
	 * 
	 * @param end1ObjectGuid
	 * @param templateName
	 * @param isCheckACL
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> batchRecursionGetObjectOfRelation(List<ObjectGuid> end1ObjectGuidList, String templateName, boolean isCheckACL) throws ServiceRequestException;

	/**
	 * 关联物料与图纸对象
	 * 先取得genericItem,再关联
	 * 
	 * @param modelObjectGuid
	 * @param itemObjectGuid
	 * @param templateName
	 * @param isCheckAcl
	 * @throws ServiceRequestException
	 */
	public void linkGenericItemCAD(ObjectGuid modelObjectGuid, ObjectGuid itemObjectGuid, String templateName) throws ServiceRequestException;

	/**
	 * 获取整个组-角色-用户树
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Map<String, String>> getWholeUserTree() throws ServiceRequestException;

	/**
	 * 根据模型对象和族表实例名称，获取实例对应物料和通用件物料
	 * 
	 * @param end1ObjectGuid
	 * @param configItemName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listItemByModelAndConfigItem(ObjectGuid end1ObjectGuid, String configItemName) throws ServiceRequestException;

	/**
	 * 保存对象
	 * 
	 * @param foundationObject
	 * @param hasReturn
	 * @param isCheckAuth
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isCheckAuth) throws ServiceRequestException;

	/**
	 * 调用创建对象，不清除状态
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject createObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 上传预览
	 * 
	 * @param objectGuid
	 * @param file
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo uploadPreviewFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 上传图标
	 * 
	 * @param objectGuid
	 * @param file
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo uploadIconFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 批量判断给定对象是否存在图标文件
	 * 
	 * @param List
	 *            <DSSFileInfo>
	 * @return 判断结果List<guid,Y/N>
	 * @throws ServiceRequestException
	 */
	public List<Map<String, String>> hasIconFile(List<DSSFileInfo> dssFileList) throws ServiceRequestException;

	/**
	 * 批量判断给定对象是否存在预览图文件
	 * 
	 * @param List
	 *            <DSSFileInfo>
	 * @return 判断结果List<guid,Y/N>
	 * @throws ServiceRequestException
	 */
	public List<Map<String, String>> hasPreviewFile(List<DSSFileInfo> dssFileList) throws ServiceRequestException;

	/**
	 * * 下载文件--生成下载文件的信息
	 * a） 装配
	 * 1） 装配主文件下载
	 * 装配主文件
	 * 结构需下载装配用到的实例文件与实例对应主文件
	 * 2） 装配实例文件下载
	 * 装配本身实例文件+装配主文件
	 * 结构需下载装配用到的实例文件与实例对应主文件
	 * 
	 * b） 零件
	 * 1） 主文件下载
	 * 主文件
	 * 2） 实例文件下载
	 * 实例文件+对应主文件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadModelFiles(ObjectGuid objectGuid, boolean isDownloadNotPrimaryFile) throws ServiceRequestException;

	/**
	 * * 下载文件--生成下载文件的信息
	 * a） 装配
	 * 1） 装配主文件下载
	 * 装配主文件
	 * 结构需下载装配用到的实例文件与实例对应主文件
	 * 2） 装配实例文件下载
	 * 装配本身实例文件+装配主文件
	 * 结构需下载装配用到的实例文件与实例对应主文件
	 * 
	 * b） 零件
	 * 1） 主文件下载
	 * 主文件
	 * 2） 实例文件下载
	 * 实例文件+对应主文件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileTrans> downloadlisModelFiles(ObjectGuid objectGuid, boolean isDownloadNotPrimaryFile) throws ServiceRequestException;

	/**
	 * 普通关联关系
	 * 
	 * @param end1Guid
	 * @param listStructure
	 * @param templateName
	 * @param update
	 * @throws ServiceRequestException
	 */
	public void link(String end1Guid, String end1ClassGuid, List<StructureObject> listStructure, String templateName, boolean update) throws ServiceRequestException;

	/**
	 * 通过nd2查找物料
	 * 
	 * @param end1ObjectGuid
	 * @param configItemName
	 * @param templateName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listItemByEnd2(ObjectGuid end1ObjectGuid, String templateName, boolean isAllField) throws ServiceRequestException;

	/**
	 * 通过关系查找end2
	 * 
	 * @param objectGuid
	 * @param templateName
	 * @param end2FieldList
	 * @param structureFieldList
	 * @return
	 */
	public List<FoundationObject> listRelationByTemplate(ObjectGuid objectGuid, String templateName, List<String> structureFieldList) throws ServiceRequestException;

	/**
	 * get parameter data
	 * 
	 * @param end1ObjectGuid
	 * @return
	 */
	public List<IOPConfigParameter> listSerialParaTable(ObjectGuid end1ObjectGuid, Date releaseDate) throws ServiceRequestException;

	/**
	 * 更新
	 * 
	 * @param end1ObjectGuid
	 * @param listColumn
	 * @param listValues
	 */
	public void updateSerialParaTable(ObjectGuid end1ObjectGuid, List<IOPColumnTitle> listTitles, Map<Integer, List<IOPColumnValue>> listValues) throws ServiceRequestException;

	/**
	 * 获取图纸结构文件guid
	 * 
	 * @param end1ObjectGuid
	 * @param listColumn
	 * @param listValues
	 */
	public List<String> listModelFiles(ObjectGuid objectGuid, boolean isDownloadNotPrimaryFile, AuthorityEnum checkAuth) throws ServiceRequestException;
	
}
