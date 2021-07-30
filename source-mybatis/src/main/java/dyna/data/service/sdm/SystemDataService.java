/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SystemDataService
 * xiasheng Apr 23, 2010
 */
package dyna.data.service.sdm;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dyna.common.bean.configure.ProjectModel;
import dyna.common.bean.data.SystemObject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.OrmService;

/**
 * 系统数据服务
 * 
 * @author xiasheng
 */
public interface SystemDataService extends OrmService
{
	/**
	 * 根据guid删除记录
	 * 
	 * @param systemClass
	 *            被删除数据的class
	 * @param guid
	 * @return 是否成功
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> boolean delete(Class<T> systemClass, String guid) throws DynaDataException;

	/**
	 * 根据条件删除记录
	 * 
	 * @param systemClass
	 *            被删除数据的class
	 * @param paramMap
	 *            参数
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int deleteBy(Class<T> systemClass, Map<String, Object> paramMap) throws DynaDataException;

	/**
	 * 根据传进来的参数删除记录
	 * 返回被删掉记录的数量
	 * 
	 * @param <T>
	 * @param systemClass
	 *            被删除数据的class
	 * @param paramMap
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int delete(Class<T> systemClass, Map<String, Object> paramMap) throws DynaDataException;

	/**
	 * 根据传进来的参数和sql statement删除记录
	 * 返回被删掉记录的数量
	 * 
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int delete(Class<T> systemClass, Map<String, Object> paramMap, String sqlStatementId) throws DynaDataException;

	/**
	 * 删除记录
	 * 根据此systemObject对象中的guid信息删除记录
	 * 
	 * @param systemObject
	 *            被删除记录的class
	 * @return 是否成功
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> boolean delete(T systemObject) throws DynaDataException;

	/**
	 * 部署模型
	 * 
	 * @param sessionId
	 * @throws SQLException
	 */
	public abstract ProjectModel deploy(String sessionId, ProjectModel projectModel) throws SQLException;

	/**
	 * 根据指定的搜索Map查询数据
	 * 使用sql为systemClass.select
	 * 例如：查询用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.select（需要相应的xml文件中有此sql）
	 * 
	 * @param searchConditionMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @return 此systemClass的列表
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap) throws DynaDataException;

	/**
	 * 根据指定的搜搜索Map和sqlStatementId语句查询数据
	 * 使用sql为systemClass.sqlStatementId
	 * 例如：查询用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.sqlStatementId（需要相应的xml文件中有此sql）
	 * 如果sqlStatementId为null则使用select
	 * 
	 * @param searchConditionMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @return records list
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap, String sqlStatementId) throws DynaDataException;

	/**
	 * 根据指定的搜索Map查询数据并排序
	 * 使用sql为systemClass.select
	 * 例如：查询用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.select（需要相应的xml文件中有此sql）
	 * 注意：orderby、isAscOrder为预留参数，在实现时并未使用，故目前不支持排序
	 * 
	 * @param searchConditionMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @param orderby
	 * @param isAscOrder
	 * @return records list
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap, String orderby, boolean isAscOrder) throws DynaDataException;

	/**
	 * 根据指定的搜索Map查询一条数据
	 * 使用sql为systemClass.select
	 * 例如：查询用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.select（需要相应的xml文件中有此sql）
	 * 
	 * @param searchConditionMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @return T 如果符合条件的有多条记录取第一条返回
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> T getObject(Class<T> systemClass, String guid) throws DynaDataException;

	/**
	 * 根据指定的搜索Map查询一条数据
	 * 使用sql为systemClass.select
	 * 例如：查询用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.select（需要相应的xml文件中有此sql）
	 * 
	 * @param searchConditionMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @return T 如果符合条件的有多条记录取第一条返回
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> T queryObject(Class<T> systemClass, Map<String, Object> searchConditionMap) throws DynaDataException;

	/**
	 * 根据指定的搜索Map和sqlStatementId查询一条数据
	 * 使用sql为systemClass.sqlStatementId
	 * 例如：查询用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.sqlStatementId（需要相应的xml文件中有此sql）
	 * 如果sqlStatementId为null则使用select
	 * 
	 * @param <T>
	 * @param systemClass
	 * @param searchConditionMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @param sqlStatementId
	 * @return
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> T queryObject(Class<T> systemClass, Map<String, Object> searochConditionMap, String sqlStatementId) throws DynaDataException;

	/**
	 * 保存一条记录
	 * 根据systemObject是否有guid来进行insert或update
	 * insert时使用sql为systemObject.insert
	 * update时使用sql为systemObject.update
	 * 例如：插入用户（systemObject为User）信息
	 * 则sql为： dyna.common.bean.data.system.User.insert（需要相应的xml文件中有此sql）
	 * 
	 * @param systemObject
	 * @return String insert为生成的guid，update为影响记录的行数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject) throws DynaDataException;

	/**
	 * 保存一条记录
	 * 根据systemObject是否有guid来进行insert或update
	 * insert时使用sql为systemObject.insert
	 * update时使用sql为systemObject.update
	 * 例如：插入用户（systemObject为User）信息
	 * 则sql为： dyna.common.bean.data.system.User.insert（需要相应的xml文件中有此sql）
	 * 
	 * @param systemObject
	 * @return String insert为生成的guid，update为影响记录的行数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject, boolean isUpdate) throws DynaDataException;

	/**
	 * 根据sqlStatementId保存一条记录
	 * 根据systemObject是否有guid来进行insert或update
	 * 使用sql为：systemObject.sqlStatementId
	 * 如果sqlStatementId为null
	 * 则：
	 * insert时使用sql为systemObject.insert
	 * update时使用sql为systemObject.update
	 * 例如：插入用户（systemObject为User）信息
	 * 则sql为： dyna.common.bean.data.system.User.insert（需要相应的xml文件中有此sql）
	 * 
	 * @param <T>
	 * @param systemObject
	 * @param sqlStatementId
	 * @return String insert为生成的guid，update为影响记录的行数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject, String sqlStatementId) throws DynaDataException;

	/**
	 * 根据sqlStatementId保存一条记录
	 * 根据isSaveDynamic来进行insert或update
	 * 使用sql为：dynaobject.sqlStatementId
	 * 如果sqlStatementId为null
	 * 则：
	 * insert时使用sql为dynaobject.insert
	 * update时使用sql为dynaobject.update
	 * 
	 * @param parameterMap
	 * @param sqlStatementId
	 * @param isSaveDynamic
	 * @return String insert为生成的guid，update为影响记录的行数
	 * @throws DynaDataException
	 */
	public String save(Map<String, Object> parameterMap, String sqlStatementId, boolean isSaveDynamic) throws DynaDataException;

	/**
	 * 根据insertId新建一条记录
	 * 根据updateId更新一条记录
	 * 根据systemObject是否有guid来进行insert或update
	 * 使用sql为：systemObject.insertId或者systemObject.updateId
	 * 如果insertId为null，使用sql为systemObject.insert
	 * 如果updateId为null，使用sql为systemObject.update
	 * 例如：插入用户（systemObject为User）信息
	 * 则sql为： dyna.common.bean.data.system.User.insertId（需要相应的xml文件中有此sql）
	 * 
	 * @param <T>
	 * @param systemObject
	 * @param insertId
	 * @param updateId
	 * @return String insert为生成的guid，update为影响记录的行数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> String save(T systemObject, String insertId, String updateId) throws DynaDataException;

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 * 例如：更新用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.sqlStatementId（需要相应的xml文件中有此sql）
	 * 
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int update(Class<T> systemClass, Map<String, Object> paramMap, String sqlStatementId) throws DynaDataException;

	/**
	 * 不使用缓存，直接从数据库查询,一般用在刷新缓存使用
	 * 
	 * @param systemClass
	 * @param guid
	 * @param sqlId
	 * @param <T>
	 * @return
	 * @throws ServiceRequestException
	 */
	<T extends SystemObject> T getObjectDirectly(Class<T> systemClass, String guid, String sqlId) throws ServiceRequestException;

	/**
	 * 不使用缓存，直接从数据库查询,一般用在刷新缓存使用
	 * 
	 * @param <T>
	 * @param systemClass
	 * @return
	 * @throws DynaDataException
	 */
	<T extends SystemObject> List<T> listObjectDirectly(Class<T> systemClass) throws DynaDataException;

	/**
	 * 重新加载指定类的所有缓存信息
	 * 
	 * @param <T>
	 * @param class1
	 * @throws DynaDataException
	 */
	<T extends SystemObject> void reloadAllCache(Class<T> class1) throws DynaDataException;

	/**
	 * 执行指定sql，返回sql查询的值
	 * 
	 * @param sql
	 * @return
	 */
	public List<String> executeQueryBySql(String sql) throws DynaDataException;

	/**
	 * 从缓存中读取数据
	 * 
	 * @param <T>
	 * @param class1
	 * @param guid
	 * @return
	 */
	<T extends SystemObject> T get(Class<T> class1, String guid);

	/**
	 * 从缓存中读取数据
	 * 
	 * @param <T>
	 * @param class1
	 * @param filter
	 * @return
	 */
	<T extends SystemObject> T findInCache(Class<T> class1, BeanFilter<T> filter);

	/**
	 * 从缓存中读取数据
	 * 
	 * @param <T>
	 * @param class1
	 * @param filter
	 * @return
	 */
	<T extends SystemObject> List<T> listFromCache(Class<T> class1, BeanFilter<T> filter);

	<T extends SystemObject> void deleteFromCache(Class<T> class1, BeanFilter<T> filter) throws DynaDataException;

	/**
	 * 触发刷新缓存的动作
	 */
	void notifyRefreshCacheListeners();

	/**
	 * 清空缓存刷新监听器
	 */
	void clearRefreshCacheListeners();

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 * 例如：更新用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.sqlStatementId（需要相应的xml文件中有此sql）
	 * 
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int updateBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException;

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 * 例如：更新用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.sqlStatementId（需要相应的xml文件中有此sql）
	 * 
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int insertBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException;

	/**
	 * 使用systemClass对应mapperper中的sqlStatementId语句进行update，使用paramMap作为参数
	 * 例如：更新用户（systemClass为User.class）信息
	 * 则sql为： dyna.common.bean.data.system.User.sqlStatementId（需要相应的xml文件中有此sql）
	 * 
	 * @param <T>
	 * @param systemClass
	 * @param paramMap
	 *            key为此systemClass中的字段，value为字段对应的值
	 * @param sqlStatementId
	 * @return 更新的记录数
	 * @throws DynaDataException
	 */
	public <T extends SystemObject> int deleteBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException;

}
