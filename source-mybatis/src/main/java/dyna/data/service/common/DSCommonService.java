package dyna.data.service.common;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.dto.Queue;
import dyna.common.dto.Session;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ObjectFieldTypeEnum;
import dyna.common.systemenum.PMSearchTypeEnum;
import dyna.net.service.Service;

import java.util.List;

public interface DSCommonService extends Service
{
	String getTableName(String classNameOrGuid) throws ServiceRequestException;

	String getTableName(String classGuidOrName, String fieldName) throws ServiceRequestException;

	String getRealBaseTableName(String classGuidOrName) throws ServiceRequestException;

	String getTableIndex(String classGuidOrName, String fieldName) throws ServiceRequestException;

	/**
	 * 取得类字段所对应的列名，并加上表的自定义别名作为前缀
	 * 
	 * @param mainTableAlias
	 *            表的自定义别名
	 * @param fieldName
	 *            字段名（格式为：类名+"."+字段名）
	 * @return
	 * @throws ServiceRequestException
	 */
	String getFieldColumn(String mainTableAlias, String fieldName) throws ServiceRequestException;

	Session getSession(String guid) throws ServiceRequestException;

	Session getSystemInternal() throws ServiceRequestException;

	String updateSession(String guid) throws ServiceRequestException;

	void updateSessionActiveTime(String sessionId) throws ServiceRequestException;

	ObjectFieldTypeEnum getObjectFieldTypeOfField(ClassField field, String bizModelName) throws ServiceRequestException;

	ObjectFieldTypeEnum getObjectFieldTypeOfField(String className, String fieldName, String bizModelName) throws ServiceRequestException;

	/**
	 * 从SearchCondition中取得所有的字段，包含构造SearchCondition的类的接口字段，查询结果字段，查询结果UI上的字段
	 * 
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	List<String> getAllFieldsFromSC(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 自定义SQL通用查询方法
	 * 
	 * @param paramData
	 * @return
	 */
	List<FoundationObject> executeQuery(DynamicSelectParamData paramData);

	/**
	 * 取得master表名
	 * 
	 * @param classGuidOrName
	 * @return
	 * @throws ServiceRequestException
	 */
	String getMasterTableName(String classGuidOrName) throws ServiceRequestException;

	/**
	 * 递归取得指定类的所有子类
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	List<String> getAllSubClass(String className) throws ServiceRequestException;

	/**
	 * 根据传入的查询参数构造器，查询动态sql执行之后的记录数
	 *
	 * @param paramData
	 * @return
	 */
	int getRecordCount(DynamicSelectParamData paramData);

	/**
	 * 根据SearchCondition的ObjectGuid中的ClassGuid和ClassName其中一个的值设置另外一个的值
	 * 
	 * @param searchCondition
	 * @throws ServiceRequestException
	 */
	void decorateClass(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 当类中的字段类型指定为code时，包含了组码和分类两种，这里，要根据code名字，区分是组码还是分类。
	 * 
	 * @param codeName
	 * @return
	 * @throws ServiceRequestException
	 */
	FieldTypeEnum getFieldTypeByName(String codeName) throws ServiceRequestException;

	/**
	 * 创建队列数据
	 * 
	 * @param queue
	 * @return
	 * @throws ServiceRequestException
	 */
	String saveQueue(Queue queue) throws ServiceRequestException;

	/**
	 * 检查高级搜索（用户保存、public保存）名称是否重复
	 * public保存：在所有的用户范围检查是否重复
	 * 用户保存：在userGuid下检查是否重复
	 * 首先看pmTypeEnum是否有值，
	 * 如果有值则按照项目管理角色来判断是否重复
	 *
	 * @param name
	 *            搜索名称
	 * @param isPublic
	 *            是否public保存
	 * @param pmTypeEnum
	 *            项目管理角色
	 * @param userGuid
	 * @return
	 * @throws DynaDataException
	 */
	boolean isSearchNameUnique(String name, boolean isPublic, PMSearchTypeEnum pmTypeEnum, String userGuid) throws ServiceRequestException;

	/**
	 * 删除邮件数据
	 * 
	 * @param userGuid
	 * @param messageDay
	 * @param isWorkflow
	 * @throws ServiceRequestException
	 */
	void deleteMail(String userGuid, int messageDay, boolean isWorkflow) throws ServiceRequestException;

	/**
	 * 获取数据层配置文件路径
	 * 
	 * @return
	 */
	String getDataServerConfRootPath();
}
