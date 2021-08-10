/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TrackedDesc
 * Wanglei 2011-11-21
 */
package dyna.app.service.helper;

/**
 * @author Wanglei
 * 
 */
public class TrackedDesc
{
	public static final String	DEFAULT						= "";

	// AAS
	public static final String	LOGIN						= "login;登录系统;登錄系統";
	public static final String	LOGOUT						= "logout;退出系统;退出系統";

	// BOAS
	public static final String	ADD_TO_FOLDER				= "add object to folder;添加实例对象到文件夹;添加實例對象到文件夾";
	public static final String	CHECK_OUT					= "check out;检出实例对象;檢出實例對象";
	public static final String	CREATE_OBJECT				= "create object;创建实例对象;創建實例對象";
	public static final String	DEL_OBJECT					= "delete object;刪除实例对象;刪除實例對象";
	public static final String	DEL_RELATION				= "delete relation;刪除关联关系;刪除關聯關係";
	public static final String	QUERY_OBJECT				= "query object;检索数据;檢索數據";
	public static final String	VIEW_OBJECT					= "view object;查看实例对象;查看實例對象";
	public static final String	SAVE_OBJECT					= "save object;修改实例对象;修改實例對象";
	public static final String	SAVE_RELATION				= "save relation;修改关联关系;修改關聯關係";
	public static final String	UPDATE_ID					= "update object id;修改实例对象id;修改實例對象id";
	public static final String	UPDATE_NAME					= "update object name;修改实例对象名称;修改實例對象名稱";
	public static final String	UPDATE_OWNER				= "update object owner;修改实例对象所有者;修改實例對象所有者";
	public static final String	UPDATE_REV					= "update object revision id;修改实例对象版本号;修改實例對象版本號";
	public static final String	LINK_RELATION				= "link relation;关联两个实例对象;關聯兩個實例對象";
	public static final String	ROLLBACK_ITR				= "rollback iteration;回滚实例对象版序;回滾實例對象版序";
	public static final String	CREATE_OBJ_BY_TPL			= "create object by template;从模板创建实例对象;從模板創建實例對象";
	public static final String	CREATE_OBJ_BY_FILE			= "create object by file;从文件创建实例对象;從文件創建實例對象";
	public static final String	CANCEL_CHECK_OUT			= "cancel check out;取消检出实例对象;取消檢出實例對象";
	public static final String	CHECK_IN					= "check in;检入实例对象;檢入實例對象";
	public static final String	TRANS_CHECK_OUT				= "transfer check out object;移交检出实例对象;移交檢出實例對象";
	public static final String	REVISE_OBJECT				= "revise object;修订实例对象;修訂實例對象";
	public static final String	SET_EFF_DATE				= "set object effective date;设置实例对象生效时间;設置實例對象生效時間";
	public static final String	OBS_OBJECT					= "obsolete object;废弃实例对象;廢棄實例對象";
	public static final String	SAVE_AS_NEW					= "save as new object;另存为新实例对象;另存為新實例對象";
	public static final String	SET_CMT_DATE				= "set object commit date;设置实例对象入库时间;設置實例對象入庫時間";
	public static final String	LINK_BOM					= "link BOM;BOM中关联两个实例对象;BOM中關聯兩個實例對象";
	public static final String	UNLINK_RELATION				= "unlink relation;解除关联两个实例对象;解除關聯兩個實例對象";
	public static final String	UNLINK_BOM					= "unlink BOM;BOM中解除关联两个实例对象;BOM中解除關聯兩個實例對象";
	public static final String	SAVE_BOM					= "save BOM;保存BOM结构;保存BOM结构";
	public static final String	SAVE_BOMVIEW				= "save BOMView;保存BOM;保存BOM";
	public static final String	DEL_BOMVIEW					= "delete relation;刪除BOM;刪除BOM";
	public static final String	START_FOUNDATION			= "start foundation;启用实例;启用实例";
	public static final String	STOP_FOUNDATION				= "stop foundation;停用实例;停用实例";

	// DSS
	public static final String	ATTACH_FILE					= "attach file;添加文件;添加文件";
	public static final String	ATTACH_PRC_FILE				= "attach process file;添加流程文件;添加流程文件";
	public static final String	DETTACH_FILE				= "dettach file;删除文件;刪除文件";
	public static final String	DOWNLOAD_FILE				= "download file;下载文件;下載文件";
	public static final String	SET_FILE_TYPE				= "set file type;设置文件类型;設置文件類型";
	public static final String	SET_PRIMARY_FILE			= "set primary file;设置主文件;設置主文件";
	public static final String	SET_PREVIEW_FILE			= "set preview file;设置预览文件;設置預覽文件";
	public static final String	COPY_FILE					= "copy file;复制文件;複製文件";

	// WFE
	public static final String	CREATE_PRC					= "create process;创建流程;創建流程";
	public static final String	RESUME_PRC					= "resume process;启动流程;啟動流程";
	public static final String	PERF_ACT					= "perform activity;执行流程活动;執行流程活動";
	public static final String	REPEAL_PRC					= "repeal process;撤销流程;撤銷流程";
	public static final String	DELETE_PRC					= "delete process;删除流程;刪除流程";

	// CAD
	public static final String	CAD_UPLOAD_CONFIG			= "upload configFiles;上传CAD集成配置;上傳CAD集成配置";
	public static final String	CAD_DOWNLOAD_CONFIG			= "download configFiles;下载CAD集成配置;下載CAD集成配置";
	public static final String	CAD_UPDATE_CONFIG_ITEM		= "update configItem;更新族表;更新族表";
	public static final String	CAD_UPDATE_BOM				= "update BOM;更新BOM;更新BOM";
	public static final String	CAD_UPDATE_MODEL			= "update model;更新模型结构;更新模型結構";

	public static final String	ERP_SEARCH_DRAW				= "ERP view drawing;ERP看图;ERP看圖";
	public static final String	ERP_SEARCH_PROPERTY_VAL		= "PLM view ERP property value;PLM获取ERP信息;PLM獲取ERP資訊";
	public static final String	ERP_SEARCH_SAVE_PROT_VAL	= "PLM view ERP property value and save in PLM;PLM获取ERP信息并保存;PLM獲取ERP資訊並保存";
}
