/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: POS Personal Object Service
 * caogc 2010-8-20
 */
package dyna.net.service.brs;

import java.util.Date;
import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.BIViewHis;
import dyna.common.dto.MySchedule;
import dyna.common.dto.Preference;
import dyna.common.dto.SaCustomColumnsPreferenceDetail;
import dyna.common.dto.SaQueryPreferenceDetail;
import dyna.common.dto.Search;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.WorkFlowPerference;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FilePreferenceEnum;
import dyna.common.systemenum.PMSearchTypeEnum;
import dyna.common.systemenum.PreferenceTypeEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.SearchTypeEnum;
import dyna.net.service.Service;

/**
 * Personal Object Service 私有对象服务
 * <String>该服务内的操作都是针对登陆者本人进行的<String>
 * 
 * @author caogc
 * 
 */
public interface POS extends Service
{
	/**
	 * 根据guid删除日程
	 * 
	 * @param guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteMySchedule(String guid) throws ServiceRequestException;

	/**
	 * 删除检索条件对象
	 * 
	 * @param searchGuid
	 *            要删除的对象的guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteSearch(String searchGuid) throws ServiceRequestException;

	/**
	 * 根据guid获取对应的日程对象
	 * 
	 * @param guid
	 * @return MySchedule
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public MySchedule getMySchedule(String guid) throws ServiceRequestException;

	/**
	 * 获取检索条件对象
	 * 
	 * @param searchGuid
	 *            要获取的对象的guid
	 * @return 对应的检索条件对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Search getSearch(String searchGuid) throws ServiceRequestException;

	/**
	 * 获取登录用户对应的最新的检索条件对象，
	 * 
	 * @return 对应的检索条件对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Search getSearchLatest() throws ServiceRequestException;

	/**
	 * 获取登录者对应的“最近打开“的对象的记录 列表 按照创建时间降序<br>
	 * 
	 * 参数searchCondition中只存放用于分页的字段信息
	 * 
	 * @param searchCondition
	 * @return FoundationObject 列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FoundationObject> listBIViewHis(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 根据日期获取我的日程列表（登陆者本人只能获取本人的日程），日期不传查所有
	 * 
	 * @param scheduleDate
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<MySchedule> listMySchedule(Date scheduleDate) throws ServiceRequestException;

	/**
	 * 获取登陆者所有的Preference对象
	 * 
	 * 
	 * @return Preference对象列表
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Preference> listPreference() throws ServiceRequestException;

	/**
	 * 获取所有的检索条件对象
	 * 
	 * @param searchCondition
	 * @return 检索条件对象列表
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Search> listSearch(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 创建"最近打开"对象的记录 创建者为登录用户<br>
	 * 
	 * <code>调用时只需将对象的guid放到参数biViewHis对象中</code> <br>
	 * <code>创建以后会判断总记录数是否大于"最大条数",<br>
	 * 如果大于  那么会按照创建时间将最早超出的记录删除掉,以保证数据库中的数据不超过最大条目</code>
	 * 
	 * @param biViewHis
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void saveBIViewHis(BIViewHis biViewHis) throws ServiceRequestException;

	/**
	 * 保存我的日程（创建或者更新）
	 * 
	 * @param mySchedule
	 * @return 保存后的MySchedule
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public MySchedule saveMySchedule(MySchedule mySchedule) throws ServiceRequestException;

	/**
	 * 获取登录用户的最大查询历史数
	 * 
	 * @return 最大查询历史数
	 * @throws ServiceRequestException
	 */
	public int getMaxHistory() throws ServiceRequestException;

	/**
	 * 获取登录用户的数据表每页显示行数
	 * 
	 * @return 数据表每页显示行数
	 * @throws ServiceRequestException
	 */
	public int getRowCount() throws ServiceRequestException;

	/**
	 * 获取登录用户的最近打开保存记录数
	 * 
	 * @return 最近打开保存记录数
	 * @throws ServiceRequestException
	 */
	public int getBIViewHiscount() throws ServiceRequestException;

	/**
	 * 获取登录用户的常用BO列表
	 * 
	 * @return 常用BO列表
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> listCommonBO() throws ServiceRequestException;

	/**
	 * 获取登录用户的常用文件夹订阅文件夹列表
	 * 
	 * @return 常用文件夹订阅文件夹列表
	 * @throws ServiceRequestException
	 */
	public List<SystemObject> listCommonLIB() throws ServiceRequestException;

	/**
	 * 添加登录用户的最大查询历史数
	 * 
	 * @param maxHistory
	 *            最大查询历史数
	 * @throws ServiceRequestException
	 */
	public void saveMaxHistory(int maxHistory) throws ServiceRequestException;

	/**
	 * 添加登录用户的数据表每页显示行数
	 * 
	 * @param rowCount
	 *            数据表每页显示行数
	 * @throws ServiceRequestException
	 */
	public void saveRowCount(int rowCount) throws ServiceRequestException;

	/**
	 * 添加登录用户的最近打开保存记录数
	 * 
	 * @param biviewHisCount
	 *            最近打开保存记录数
	 * @throws ServiceRequestException
	 */
	public void saveBIViewHiscount(int biviewHisCount) throws ServiceRequestException;

	/**
	 * 添加登录用户的常用BO列表
	 * 
	 * @param boInfoList
	 *            BO列表
	 * @throws ServiceRequestException
	 */
	public void saveCommonBO(List<BOInfo> boInfoList) throws ServiceRequestException;

	/**
	 * 添加登录用户的常用文件夹订阅文件夹列表
	 * 
	 * @param systemObjectList
	 *            常用文件夹订阅文件夹列表
	 * @throws ServiceRequestException
	 */
	public void saveCommonLIB(List<SystemObject> systemObjectList) throws ServiceRequestException;

	/**
	 * 单个添加登录用户的常用文件夹订阅文件夹
	 * 
	 * @param systemObject
	 *            常用文件夹订阅文件夹列表
	 * @throws ServiceRequestException
	 */
	public void addCommonLIB(SystemObject systemObject) throws ServiceRequestException;

	/**
	 * 保存(替换)查询条件
	 * 
	 * @param searchGuid
	 * @param sc
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及查询历史未找到异常
	 */
	public Search saveSearch(String searchGuid, SearchCondition sc) throws ServiceRequestException;

	/**
	 * 保存存储条件为个人常用搜索
	 * 
	 * @param name
	 * @param searchGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Search saveSearch(String name, String searchGuid) throws ServiceRequestException;

	/**
	 * 保存存储条件为public常用搜索,仅administrator组用户可以保存
	 * 
	 * @param name
	 * @param searchGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */

	public Search savePublicSearch(String name, String searchGuid) throws ServiceRequestException;

	/**
	 * 保存检索条件，新建
	 * 
	 * @param boInfo
	 *            BOInfo
	 * @param folderGuid
	 *            文件夹的guid
	 * @param searchCondition
	 *            检索条件
	 * @return 更新后的检索条件对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Search saveSearchCondition(BOInfo boInfo, String folderGuid, SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 获取所有公共的检索条件对象
	 * 
	 * @param searchCondition
	 * @return 检索条件对象列表
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Search> listPublicSearch(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 获取通知刷新时间
	 * 
	 * @return 通知刷新时间
	 * @throws ServiceRequestException
	 */
	public int getNoticeRefreshInterval() throws ServiceRequestException;

	/**
	 * 获取是否接收Email
	 * 
	 * @param userGuid
	 *            接收者guid
	 * @return 是否接收Email
	 * @throws ServiceRequestException
	 */
	public boolean isReceiveEmail(String userGuid) throws ServiceRequestException;

	/**
	 * 获取是否接收Email
	 * 
	 * @return 是否接收Email
	 * @throws ServiceRequestException
	 */
	public boolean isReceiveEmail() throws ServiceRequestException;

	/**
	 * 保存通知刷新时间
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void saveNoticeRefreshInterval(int interval) throws ServiceRequestException;

	/**
	 * 保存是否接收Email
	 * 
	 * @param isReceive
	 * @throws ServiceRequestException
	 */
	public void saveReceiveMail(boolean isReceive) throws ServiceRequestException;

	/**
	 * 判断当前登录用户是否有保存public search权限
	 * 
	 * @param searchGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasUpdateAuthorityForPublicSearch(String searchGuid) throws ServiceRequestException;

	/**
	 * 判断检索名称是否重复
	 * 
	 * @param name
	 * @param typeEnum
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isSearchNameExist(String name, SearchTypeEnum typeEnum) throws ServiceRequestException;

	/**
	 * 保存存储条件为项目管理常用搜索
	 * 
	 * @param name
	 * @param pmTypeEnum
	 * @param searchGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */

	public Search savePMSearch(String name, PMSearchTypeEnum pmTypeEnum, String searchGuid) throws ServiceRequestException;

	/**
	 * 获取项目管理中的检索条件对象
	 * 
	 * @param searchCondition
	 * @return 检索条件对象列表
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Search> listPMSearch(PMSearchTypeEnum pmTypeEnum, SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 检查项目检索名是否唯 一
	 * 
	 * @param name
	 * @param pmTypeEnum
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isPMSearchNameExist(String name, PMSearchTypeEnum pmTypeEnum) throws ServiceRequestException;

	/**
	 * 是否显示登录警语
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isShowLoginWarning() throws ServiceRequestException;

	/**
	 * 保存是否显示登录警语
	 * 
	 * @param isShow
	 * @throws ServiceRequestException
	 */
	public void saveLoginWarning(boolean isShow) throws ServiceRequestException;

	/**
	 * 根据BO取得其对应的过滤器
	 * 
	 * @param searchCondition
	 * @param boGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Search> listFilter4BO(SearchCondition searchCondition, String boGuid) throws ServiceRequestException;

	/**
	 * 根据classification取得其对应的过滤器
	 * 
	 * @param searchCondition
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Search> listFilter4Classificaiton(SearchCondition searchCondition, String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 保存自定义查询栏
	 * 
	 * @param classGuid
	 * @param detailList
	 * @return 返回自定义查询栏
	 * @throws ServiceRequestException
	 */
	public List<SaQueryPreferenceDetail> saveMySearchCondition(String classGuid, List<SaQueryPreferenceDetail> detailList) throws ServiceRequestException;

	/**
	 * 取得自定义查询栏
	 * 
	 * @param classGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<SaQueryPreferenceDetail> listMySearchCondition(String classGuid) throws ServiceRequestException;

	/**
	 * 保存表格自定义列
	 * 
	 * @param classGuid
	 * @param detailList
	 * @return 返回表格自定义列
	 * @throws ServiceRequestException
	 */
	public List<SaCustomColumnsPreferenceDetail> saveMyCustomColumns(String tableType, List<SaCustomColumnsPreferenceDetail> detailList, String classifFK, String classFK)
			throws ServiceRequestException;

	/**
	 * 取得表格自定义列
	 * 
	 * @param classGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<SaCustomColumnsPreferenceDetail> listMyCustomColumns(String tableType, String classifFK, String classFK) throws ServiceRequestException;

	/**
	 * 文件下载时的自动打开设置
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public FilePreferenceEnum getFilePreference() throws ServiceRequestException;

	/**
	 * 保存文件下载时的自动打开设置
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public void saveFilePreference(FilePreferenceEnum filePreferenceEnum) throws ServiceRequestException;

	/**
	 * 取得是否记录编码上次选值
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean getCodingPreference() throws ServiceRequestException;

	/**
	 * 保存是否记录编码上次选值
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public void saveCodingPreference(boolean isRecord) throws ServiceRequestException;

	/**
	 * 工作流偏好设置
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkFlowPerference getWorkPreference() throws ServiceRequestException;

	/**
	 * 工作流偏好设置
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public void saveWorkPreference(WorkFlowPerference workFlowPerference) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 是否是左右分隔布局
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isLeftRightLayout() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存左右分隔布局
	 * 
	 * @param isLeftRightLayout
	 * @throws ServiceRequestException
	 */
	public void saveLeftRightLayout(boolean isLeftRightLayout) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 取得左边比例值
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int getLeftProportionValue() throws ServiceRequestException;
	/**
	 * 根据配置类型枚举取得相应设置结果
	 * 
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getPreferenceByType(PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException;
	
	/**
	 * 根据配置类型枚举保存相应设置结果
	 * 
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public void savePreferenceByType(String value,PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存左边比例值
	 * 
	 * @param leftProportionValue
	 * @throws ServiceRequestException
	 */
	public void saveLeftProportionValue(int leftProportionValue) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 打开方式：是否始终在第一个位置打开
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String defaultOpenEditorInput() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存打开方式
	 * 
	 * @param openOnlyOne
	 * @throws ServiceRequestException
	 */
	public void saveDefaultOpenEditorInput(String editorInputId) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存系统消息清理周期
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void saveMessageClearCycle(int interval) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 取得系统消息清理周期
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int getMessageClearCycle() throws ServiceRequestException;

	/**
	 * 主页--自定义项
	 * 保存自定义项列表
	 * 
	 * @param quickConfigNameList
	 * @throws ServiceRequestException
	 */
	public void savequickConfig(List<String> quickConfigNameList) throws ServiceRequestException;

	/**
	 * 主页--自定义项
	 * 取得自定义项列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listQuickConfig() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 取得流程消息清理周期
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int getWorkFlowClearCycle() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存流程消息清理周期
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void saveWorkFlowClearCycle(int interval) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存ID模糊查询
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void setQueryIDFuzzy(boolean value) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 获取ID模糊查询
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean getQueryIDFuzzy() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存区分查询大小写
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void setQueryCaseSensitive(boolean value) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 获取区分查询大小写
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean getQueryCaseSensitive() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存查询子目录
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void setQuerySubFolder(boolean value) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 获取查询子目录
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean getQuerySubFolder() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存支持简单查询
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void setHasSimpleQuery(boolean value) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 获取支持简单查询
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean getHasSimpleQuery() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存查询版本类型
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void setQueryRevisionType(SearchRevisionTypeEnum value) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 获取查询版本类型
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public SearchRevisionTypeEnum getQueryRevisionType() throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 保存Owner限制模式
	 * 0：不限制
	 * 1：我的数据
	 * 2：所在组的数据
	 * 
	 * @param interval
	 * @throws ServiceRequestException
	 */
	public void setQueryOwnerConditionType(int value) throws ServiceRequestException;

	/**
	 * 个人常用设置--设置项
	 * 获取Owner限制模式
	 * 0：不限制
	 * 1：我的数据
	 * 2：所在组的数据
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int getQueryOwnerConditionType() throws ServiceRequestException;

}
