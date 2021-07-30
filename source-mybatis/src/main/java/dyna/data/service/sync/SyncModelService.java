package dyna.data.service.sync;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dyna.common.bean.configure.ProjectModel;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.net.service.Service;

public interface SyncModelService extends Service
{
	/**
	 * 模型部署
	 *
	 * @param sessionId
	 * @param projectModel
	 * @param hasClassificationLicense
	 *            是否有分类模块license
	 * @return
	 * @throws Exception
	 */
	ProjectModel deploy(String sessionId, ProjectModel projectModel, boolean hasClassificationLicense) throws Exception;

	/**
	 * 部署分类字段，导入工具用
	 * 
	 * @param dataSource
	 * @param sessionId
	 * @throws SQLException
	 */
	public void deployClassificationField(List<Map<String, String>> dataSource, String sessionId) throws ServiceRequestException;

	/**
	 * 判断模型是否是同步的
	 *
	 * @param projectModel
	 * @throws ServiceRequestException
	 */
	boolean isModelSync(ProjectModel projectModel) throws ServiceRequestException;

	/**
	 * 建模器只允许一个用户登录
	 *
	 * @return
	 */
	String getCurrentLoginUser();

	/**
	 * 取得当前服务器配置模型
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	ProjectModel getCurrentSyncModel() throws ServiceRequestException;

	/**
	 * 从数据库中生成模型文件
	 *
	 * @throws SQLException
	 */
	ProjectModel makeModelFile(boolean hasClassificationLicense) throws ServiceRequestException;

	/**
	 * 返回模型能否被同步
	 *
	 * @return
	 * @throws SQLException
	 */
	boolean isDeployLock(String sessionId) throws ServiceRequestException;
	
	
	public String getColumnDBType(FieldTypeEnum fieldTypeEnum, String fieldSize);

}
