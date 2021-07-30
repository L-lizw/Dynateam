/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 数据库同步
 * Jiagang 2010-06-18
 */
package dyna.data.service.sync;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dyna.common.dtomapper.ModelSyncMapper;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.common.bean.configure.ProjectModel;
import dyna.common.dto.ModelSync;
import dyna.common.dto.Session;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataSessionException;
import dyna.data.common.exception.DynaDataSqlException;
import dyna.data.context.DataServerContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据库同步
 *
 * @author Jiagang
 */
public class SyncDatabase
{
	private final SqlSessionFactory sqlSessionFactory;
	private final ModelXMLCache     dynaModel;

	@Autowired
	private ModelSyncMapper         modelSyncMapper;

	/**
	 * 用户GUID
	 */
	private String				userGuid;

	/**
	 * 构造方法
	 *
	 * @param context
	 * @param sqlSessionFactory
	 *            数据库连接
	 * @param sessionId
	 *            登录ID
	 * @throws SQLException
	 */
	public SyncDatabase(DataServerContext context, SqlSessionFactory sqlSessionFactory, ModelXMLCache dynaModel, String sessionId) throws SQLException
	{
		this.sqlSessionFactory = sqlSessionFactory;
		SyncUtil.sqlSessionFactory = this.sqlSessionFactory;
		this.dynaModel = dynaModel;
		Session session = DataServer.getSystemDataService().get(Session.class, sessionId);
		if (session != null)
		{
			this.userGuid = session.getUserGuid();
		}
		else
		{
			throw new DynaDataSessionException("session error", null);
		}
	}

	/**
	 * 开始同步
	 *
	 * @throws SQLException
	 */
	public ProjectModel startSync(ProjectModel projectModel, boolean hasClassificationLicense) throws Exception
	{
		ProjectModel projectModelNew;
		try
		{
			DynaLogger.info("Start synchronize database.");

			// 模型同步
			this.syncModel();

			SyncUtil.initDBTableInfo();

			if (hasClassificationLicense)
			{
				SyncClassificationTableStructure syncTableStructure = new SyncClassificationTableStructure(this.dynaModel, this.sqlSessionFactory);
				syncTableStructure.syncClassificationTable();
			}

			SyncClassObjectTableStructure syncTableStructure = new SyncClassObjectTableStructure(this.sqlSessionFactory, this.userGuid, this.dynaModel);
			syncTableStructure.syncTable();

			DynaLogger.info("Update sync info");
			projectModelNew = this.updateSyncInfo(projectModel);

			DynaLogger.info("Reload Code Model......");
			DataServer.getCodeModelService().reloadModel();

			DynaLogger.info("Reload Class Model......");
			DataServer.getClassModelService().reloadModel();

			DynaLogger.info("check And Clear ClassObject......");
			DataServer.getClassModelService().checkAndClearClassObject();

			DynaLogger.info("check And Clear BusinessObject......");
			DataServer.getBusinessModelService().checkAndClearBusinessObject();

			DynaLogger.info("check And Clear ClassificationFeature......");
			DataServer.getClassificationFeatureService().checkAndClearClassificationFeature(this.userGuid);

			DynaLogger.info("Sucess to synchronize database.");
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataException)
			{
				DynaDataSqlException ex = new DynaDataSqlException("Synchronize database error", e);
				ex.setDataExceptionEnum(DataExceptionEnum.DS_MODEL_DEPLOY);
				throw ex;
			}
			throw e;
		}

		return projectModelNew;
	}

	private void syncModel() throws Exception
	{
		//TODO
//		this.sqlSessionFactory.startTransaction();
//		this.sqlSessionFactory.getCurrentConnection().setAutoCommit(false);
		try
		{
			DynaLogger.info("Processing interface");
			SyncInterface syncInterfaceService = new SyncInterface(this.sqlSessionFactory, this.userGuid);
			syncInterfaceService.startSync();

			DynaLogger.info("Processing code");
			SyncCodeAndClassification cacService = new SyncCodeAndClassification(this.sqlSessionFactory, this.userGuid, this.dynaModel);
			cacService.startSync();

			DynaLogger.info("Processing class");
			SyncClassObject syncClassObjectService = new SyncClassObject(this.sqlSessionFactory, this.userGuid, this.dynaModel);
			syncClassObjectService.startSync();

//			this.sqlSessionFactory.commitTransaction();
		}
		catch (Exception e)
		{
//			this.sqlSessionFactory.endTransaction();
			if (e instanceof DynaDataException)
			{
				DynaDataSqlException ex = new DynaDataSqlException("Synchronize database error", e);
				ex.setDataExceptionEnum(DataExceptionEnum.DS_MODEL_DEPLOY);
				throw ex;
			}
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private ProjectModel updateSyncInfo(ProjectModel projectModel) throws SQLException
	{
		String version = null;

		if (projectModel == null)
		{
			return null;
		}

		if (StringUtils.isNullString(projectModel.getOmGuid()))
		{
			version = "1.0";
		}
		else
		{
			ModelSync modelSync = new ModelSync();
			modelSync.setOmGuid(projectModel.getOmGuid());
			modelSync.put("ISLASTER", "Y");
			List<ModelSync> modelSyncList = this.modelSyncMapper.getSync(modelSync);
			if (modelSyncList != null)
			{
				for (ModelSync modelSync_ : modelSyncList)
				{
					version = modelSync_.getVersion();
					break;
				}
			}

			if (StringUtils.isNullString(version))
			{
				version = "1.0";
			}
			else
			{
				try
				{
					version = (Double.parseDouble(version) * 10 + 1) / 10 + "";
				}
				catch (Exception e)
				{
					version = "1.0";
				}
			}
		}

		ModelSync sync = new ModelSync();
		sync.setGuid(StringUtils.generateRandomUID(32).replace("-", "").toUpperCase());
		sync.setName(projectModel.getName());
		sync.setDescription(projectModel.getDescription());
		sync.setVersion(version);
		sync.setOmGuid(projectModel.getOmGuid());
		sync.setSyncIP(projectModel.getSyncIP());
		sync.setOmName(projectModel.getOmName());
		sync.setCreateUserGuid(this.userGuid);
		sync.setUpdateUserGuid(this.userGuid);
		sync.put("CURRENTTIME", DateFormat.getSysDate());

		try
		{
			//TODO
//			this.sqlSessionFactory.startTransaction();
//			this.sqlSessionFactory.getCurrentConnection().setAutoCommit(false);

			User user = DataServer.getSystemDataService().get(User.class, this.userGuid);
			sync.put("CREATEUSERNAME", user.getName());
			sync.put("UPDATEUSERNAME", user.getName());
			this.modelSyncMapper.insert(sync);

//			this.sqlSessionFactory.commitTransaction();
		}
		catch (Exception e)
		{
//			this.sqlSessionFactory.endTransaction();
			throw e;
		}

		return new ProjectModel(sync);
	}

	protected void syncClassificationField(List<Map<String, String>> dataSource) throws Exception
	{
//		this.sqlSessionFactory.startTransaction();
//		this.sqlSessionFactory.getCurrentConnection().setAutoCommit(false);
		try
		{
			DynaLogger.info("Processing code");
			SyncCodeAndClassification cacService = new SyncCodeAndClassification(this.sqlSessionFactory, this.userGuid, this.dynaModel);
			cacService.startSyncForClassificationField(dataSource);

//			this.sqlSessionFactory.commitTransaction();
		}
		catch (Exception e)
		{
//			this.sqlSessionFactory.endTransaction();
			if (e instanceof DynaDataException)
			{
				DynaDataSqlException ex = new DynaDataSqlException("Synchronize database error", e);
				ex.setDataExceptionEnum(DataExceptionEnum.DS_MODEL_DEPLOY);
				throw ex;
			}
			throw e;
		}
	}
}
