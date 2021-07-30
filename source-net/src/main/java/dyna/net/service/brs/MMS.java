/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EMM Enterprise Model Management
 * Wanglei 2010-7-8
 */
package dyna.net.service.brs;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dyna.common.bean.configure.ProjectModel;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.bean.model.lf.Lifecycle;
import dyna.common.bean.model.ui.UIIcon;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.bean.model.wf.WorkflowProcess;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * Model Manage Service 企业建模信息服务
 * 
 * @author Wanglei
 * 
 */
public interface MMS extends Service
{
	/**
	 * 检查业务模型，业务对象，如果有问题，有问题的数据需要执行删除操作，并对数据重新整理
	 * 
	 * @throws ServiceRequestException
	 */
	public void checkAndReBuildBusinessModel() throws ServiceRequestException;

	/**
	 * 复制创建ui
	 * 
	 * @param sourceUIobject
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UIObjectInfo> copy4CreateUIObject(List<UIObjectInfo> sourceUIobject, String toClassguid, boolean iscut) throws ServiceRequestException;

	/**
	 * 复制创建业务模型
	 * 
	 * @param sourceBMInfoList
	 * @return
	 */
	public List<BMInfo> copy4CreateBusinessModle(List<BMInfo> sourceBMInfoList) throws ServiceRequestException;

	/**
	 * 复制创建业务对象
	 * 
	 * @param sourceBOInfoList
	 * @param bmguid
	 * @param parentBOguid
	 *            parentBOguid 为空表示直接粘贴到业务模型下创建
	 * @throws ServiceRequestException
	 */
	public void copy4CreateBusinessObject(List<BOInfo> sourceBOInfoList, String bmguid, String parentBOguid) throws ServiceRequestException;

	/**
	 * 复制创建组码, 剪切操作还将删除原来的组码及其子项
	 * 
	 * @param sourceCodeObjectInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<CodeObjectInfo> copy4CreateCodeObject(List<CodeObjectInfo> sourceCodeObjectInfoList, boolean isCut) throws ServiceRequestException;

	/**
	 * 复制创建
	 * 
	 * @param sourceCodeObjectInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public void copy4CreateCodeItem(List<CodeItemInfo> sourceCodeItemInfoList, String codeObjectguid, String parentCodeItemguid) throws ServiceRequestException;

	/**
	 * 复制创建生命周器
	 * 
	 * @param sourceLifecycleInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<LifecycleInfo> copy4CreateLifeCycle(List<LifecycleInfo> sourceLifecycleInfoList) throws ServiceRequestException;

	/**
	 * 复制创建工作流模型
	 * 
	 * @param sourceWfProcessInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowProcessInfo> copy4CreateWorkflowProcess(List<WorkflowProcessInfo> sourceWfProcessInfoList) throws ServiceRequestException;

	/**
	 * 创建codeItem
	 * 
	 * @param codeItemInfo
	 * @throws ServiceRequestException
	 */
	public void createCodeItem(CodeItemInfo codeItemInfo) throws ServiceRequestException;

	/**
	 * 创建codeObject
	 * 
	 * @param codeObjectInfo
	 * @throws ServiceRequestException
	 */
	public void createCodeObject(CodeObjectInfo codeObjectInfo) throws ServiceRequestException;

	/**
	 * 创建业务模型
	 * 
	 * @param boInfo
	 * @throws ServiceRequestException
	 */
	public void createBusinessModel(BMInfo bmInfo) throws ServiceRequestException;

	/**
	 * 创建业务对象(返回创建成功的业务对象)
	 * 
	 * @param boInfo
	 * @throws ServiceRequestException
	 */
	public BOInfo createBusinessObject(BOInfo boInfo) throws ServiceRequestException;

	public void deleteBMInfo(String bmInfoguid) throws ServiceRequestException;

	public void deleteBOInfo(String boInfoguid) throws ServiceRequestException;

	public void deleteCodeObject(String codeObjectguid) throws ServiceRequestException;

	public void deleteCodeItem(String codeItemguid) throws ServiceRequestException;

	public void deleteLifecycle(String lifecycleGuid) throws ServiceRequestException;

	/**
	 * 删除UIField
	 * 
	 * @param uiFieldGuid
	 * @throws ServiceRequestException
	 */
	public void deleteUIField(String uiFieldGuid) throws ServiceRequestException;

	/**
	 * 删除UIObject
	 * 
	 * @param uiGuid
	 * @throws ServiceRequestException
	 */
	public void deleteUIObject(String uiGuid) throws ServiceRequestException;

	/**
	 * 删除UIAction
	 * 
	 * @param uiGuid
	 * @throws ServiceRequestException
	 */
	public void deleteUIAction(String uiActionGuid) throws ServiceRequestException;

	/**
	 * 删除工作流模型
	 * 
	 * @param procGuid
	 * @throws ServiceRequestException
	 */
	public void deleteWorkflowProcess(String procGuid) throws ServiceRequestException;

	/**
	 * 根据模板的guid删除BOM关联关系模板的处理
	 * 
	 * @param bomTemplateGuid
	 *            要删除的BOM关联关系模板对象的Guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */

	@Deprecated
	public void deleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException;

	/**
	 * 根据模板名字删除所有为该名字的BOM关联关系模板
	 * 
	 * @param templateName
	 *            模板名字
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	@Deprecated
	public void deleteBOMTemplateByName(String templateName) throws ServiceRequestException;

	/**
	 * 根据关系模板的guid,删除关联关系模板的处理 <br>
	 * 调用SQL删除关系模板
	 * 
	 * @param relationTemplateGuid
	 *            要删除的关联关系模板对象的Guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	@Deprecated
	public void deleteRelationTemplate(String relationTemplateGuid) throws ServiceRequestException;

	/**
	 * 根据关系模板的名字删除关系模板数据集<br>
	 * 该方法删除的都是结构模板，与普通关系模板无关
	 * 
	 * @param templateName
	 * @throws ServiceRequestException
	 */
	@Deprecated
	public void deleteRelationTemplateByName(String templateName) throws ServiceRequestException;

	/**
	 * 部署分类字段，导入工具使用
	 * 
	 * @param dataSource
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	public void deployClassification(List<Map<String, String>> dataSource, String sessionId) throws ServiceRequestException;

	/**
	 * bm信息的编辑，当前只有上移下移操作，其他的修改保存操作直接sds服务操作的保存
	 * 
	 * @param bmList
	 * @param isUp
	 * @throws ServiceRequestException
	 */
	public List<BMInfo> editBMInfo(List<BMInfo> bmList, boolean isUp) throws ServiceRequestException;

	/**
	 * 业务对象位置调整
	 * 
	 * @param boList
	 * @param isUp
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> editBOInfo(List<BOInfo> boList, boolean isUp) throws ServiceRequestException;

	/**
	 * 编辑组码
	 * 
	 * @param codeItem
	 * @throws ServiceRequestException
	 */
	public void editCodeObject(CodeObject codeObject) throws ServiceRequestException;

	/**
	 * 编辑生命周期
	 * 
	 * @param lifecycle
	 * @throws ServiceRequestException
	 */
	public Lifecycle editLifecycle(Lifecycle lifecycle) throws ServiceRequestException;

	/**
	 * 編輯编码规则
	 * 
	 * @param numberingModel
	 * @throws ServiceRequestException
	 */
	public void editNumberingModel(String classGuid, List<NumberingModel> numberingModelList, boolean isNumbering) throws ServiceRequestException;

	/**
	 * 编辑类的动作或者脚本
	 * 
	 * @param classGuid
	 * @param actionList
	 * @throws ServiceRequestException
	 */
	public void editClassAction(String classGuid, List<ClassAction> actionList, boolean isAction) throws ServiceRequestException;

	/**
	 * 编辑分类ui字段(继承的ui修改字段则ui进行重建，返回新建的ui)
	 * 
	 * @param cfUIfieldList
	 */
	public ClassificationUIInfo editClassificationUIField(ClassificationUIInfo uiInfo, List<UIField> cfUIfieldList) throws ServiceRequestException;

	/**
	 * 编辑分类ui
	 * 
	 * @param cfUIInfoList
	 * @throws ServiceRequestException
	 */
	public void editClassificationUI(List<ClassificationUIInfo> cfUIInfoList) throws ServiceRequestException;

	/**
	 * 编辑类图标和类脚本图标
	 * 
	 * @throws ServiceRequestException
	 */
	public void editIcon(UIIcon uiIcon) throws ServiceRequestException;

	/**
	 * 編輯保存ui字段(继承的ui修改字段则ui进行重建，返回新建的ui)
	 * 
	 * @param uiFieldList
	 * @param isClassification
	 *            是否是分类ui
	 * @throws ServiceRequestException
	 */
	public UIObjectInfo editUIField(UIObjectInfo uiInfo, List<UIField> uiFieldList) throws ServiceRequestException;

	/**
	 * 编辑保存ui
	 * 
	 * @param uiObjectInfoList
	 * @throws ServiceRequestException
	 */
	public void editUIObject(List<UIObjectInfo> uiObjectInfoList) throws ServiceRequestException;

	/**
	 * 编辑保存工作流模型
	 * 
	 * @param workflowProcess
	 * @throws ServiceRequestException
	 */
	public WorkflowProcess editWorkflowProcess(WorkflowProcess workflowProcess) throws ServiceRequestException;

	/**
	 * 保存codeItem
	 * 
	 * @param codeItemInfo
	 * @throws ServiceRequestException
	 */
	public void saveCodeItem(CodeItemInfo codeItemInfo) throws ServiceRequestException;

	/**
	 * 更新类信息，不包含接口信息，是否创建表父类等会影响到数据结构的信息改变
	 * 
	 * @param classInfo
	 * @throws ServiceRequestException
	 */
	public void saveClassObject(ClassInfo classInfo) throws ServiceRequestException;

	/**
	 * 更新字段信息，不包含字段列名，字段名，字段表索引，字段类型等影响到数据结构的信息改变
	 * 
	 * @param classField
	 * @throws ServiceRequestException
	 */
	public void saveClassField(ClassField classField) throws ServiceRequestException;

	/**
	 * 新增或者更新UIObject
	 * 
	 * @param uiObject
	 * @throws ServiceRequestException
	 */
	public void saveUIObject(UIObject uiObject) throws ServiceRequestException;

	/**
	 * 新增或者更新UIField
	 * 
	 * @param uiField
	 * @throws ServiceRequestException
	 */
	public void saveUIField(UIField uiField) throws ServiceRequestException;

	/**
	 * 新增或者更新UIAction
	 * 
	 * @param uiAction
	 * @throws ServiceRequestException
	 */
	public void saveUIAction(UIAction uiAction) throws ServiceRequestException;

	public String saveSystemObject(SystemObject systemObject) throws ServiceRequestException;

	public void saveCodeObject(CodeObjectInfo codeObjectInfo) throws ServiceRequestException;

	<T extends SystemObject> T saveModel(T model) throws ServiceRequestException;

	/**
	 * 取得当前数据库中的最新模型信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProjectModel getCurrentSyncModel() throws ServiceRequestException;

	/**
	 * 从数据库中生成模型文件
	 * 
	 * @throws SQLException
	 */
	public ProjectModel makeModelFile() throws ServiceRequestException;

	/**
	 * 部署模型
	 * 
	 * @param projectModel
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProjectModel deploy(ProjectModel projectModel) throws ServiceRequestException;

	/**
	 * 返回模型能否被同步
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	boolean isDeployLock() throws ServiceRequestException;

	/**
	 * 判断模型是否是同步的
	 * 
	 * @param projectModel
	 * @return
	 * @throws ServiceRequestException
	 */
	boolean isModelSync(ProjectModel projectModel) throws ServiceRequestException;

	/**
	 * 保存类的分类映射
	 * 
	 * @param list
	 * @throws ServiceRequestException
	 */
	public void saveNormalClassficationFeature(String classguid, List<ClassficationFeature> classficationFeatures) throws ServiceRequestException;

	/**
	 * 删除分类映射
	 * 
	 * @param classficationFeatureGuid
	 * @throws ServiceRequestException
	 */
	public void deleteNormalClassficationFeature(String classguid, List<String> classficationFeatureGuids) throws ServiceRequestException;

	/**
	 * 删除映射字段
	 * 
	 * @param classificationregularguid
	 * @throws ServiceRequestException
	 */
	public void deleteClassificationFeatureItem(String classificationregularguid) throws ServiceRequestException;

	/**
	 * 保存映射字段
	 * 
	 * @param classficationFeatureItem
	 * @throws ServiceRequestException
	 */
	public String saveClassificationFeatureItem(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException;

	/**
	 * 保存映射字段的字段列表
	 */
	public void saveClassificationNumberField(String classificationFeatureItemGuid, List<ClassificationNumberField> classificationNumberFieldList) throws ServiceRequestException;

	/**
	 * 根据模板名字废弃所有为该名字的BOM关联关系模板
	 * 
	 * @param templateName
	 *            模板名字
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public void obsoleteBOMTemplateByName(String templateName) throws ServiceRequestException;

	/**
	 * 根据模板名字启用所有为该名字的BOM关联关系模板
	 * 
	 * @param templateName
	 *            模板名字
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(
	 *             ID_APP_SERVER_EXCEPTION)
	 */
	public void reUseBOMTemplateByName(String templateName) throws ServiceRequestException;

	/**
	 * 根据模板的guid废弃BOM关联关系模板的处理
	 * 
	 * @param bomTemplateGuid
	 *            要废弃的BOM关联关系模板对象的Guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void obsoleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException;

	/**
	 * 根据模板的guid启用废弃的BOM关联关系模板的处理
	 * 
	 * @param bomTemplateGuid
	 *            要启用的BOM关联关系模板对象的Guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void reUseBOMTemplate(String bomTemplateGuid) throws ServiceRequestException;

	/**
	 * 添加/更新一个BOM关联关系模板对象<br>
	 * 此方法只能管理员组用户调用<br>
	 * 并判断：END1在同名模板中是否已经存在，不能重复创建
	 * 
	 * @param bomTemplate
	 *            BOM关联关系模板的对象
	 * @return 刚添加/更新的BOMTemplate对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器非管理员权限异常(
	 *             ID_APP_ADMIN_GROUP_TEAM)
	 */
	public BOMTemplate saveBOMTemplate(BOMTemplate bomTemplate) throws ServiceRequestException;

	/**
	 * 废弃指定的关系模板
	 * 
	 * @param relationTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public void obsoleteRelationTemplate(String relationTemplateGuid) throws ServiceRequestException;

	/**
	 * 根据关系模板的名字废弃关系模板数据集<br>
	 * 该方法删除的都是结构模板，与普通关系模板无关
	 * 
	 * @param templateName
	 * @throws ServiceRequestException
	 */
	public void obsoleteRelationTemplateByName(String templateName) throws ServiceRequestException;

	/**
	 * 启用指定的关系模板
	 * 
	 * @param relationTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public void reUseRelationTemplate(String relationTemplateGuid) throws ServiceRequestException;

	/**
	 * 根据关系模板的名字重新启用废弃的关系模板数据集<br>
	 * 
	 * @param templateName
	 * @throws ServiceRequestException
	 */
	public void reUseRelationTemplateByName(String templateName) throws ServiceRequestException;

	/**
	 * 添加/更新一个关联关系模板对象<br>
	 * 要求操作者必须为管理员组的用户
	 * 
	 * @param relationTemplate
	 *            关联关系模板的对象
	 * @return 刚添加/更新的RelationTemplate对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)<br>
	 *             应用服务器异常(ID_APP_SERVER_EXCEPTION)<br>
	 *             操作者必须为管理员的权限异常(ID_APP_ADMIN_GROUP_TEAM)
	 */
	public RelationTemplate saveRelationTemplate(RelationTemplate relationTemplate) throws ServiceRequestException;

	/**
	 * 数据导入时用于更新流水号
	 * 
	 * @param foundationObject
	 * @throws ServiceRequestException
	 */
	public void updateSerialNumber(FoundationObject foundationObject) throws ServiceRequestException;

}
