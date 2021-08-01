/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SDSAuthorityUnit
 * ZhangHW 2011-7-8
 */
package dyna.data.service.sync;

import dyna.common.bean.configure.ProjectModel;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.ModelSync;
import dyna.common.dto.Session;
import dyna.common.dto.SysTrack;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dtomapper.ModelSyncMapper;
import dyna.common.dtomapper.SessionMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.model.interfacemodel.InterfaceModelService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sync.bean.CodeModelInfo;
import dyna.data.service.sync.bean.DefaultStrategy;
import dyna.data.service.sync.bean.DynaModel;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.dbcommon.exception.DynaDataModelException;
import dyna.net.security.signature.Signature;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统数据服务 模型相关操作
 *
 * @author ZhangHW
 */
public class SDSModelStub extends DSAbstractServiceStub<SyncModelServiceImpl>
{
	private ModelSyncMapper             modelSyncMapper;

	private SessionMapper               sessionMapper;
	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public SDSModelStub(DataServerContext context, SyncModelServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	/**
	 * 判断模型是否是同步的
	 *
	 * @param projectModel
	 * @throws ServiceRequestException
	 */
	protected boolean isModelSync(ProjectModel projectModel) throws DynaDataException
	{
		ProjectModel projectModelLocal = this.getSyncInfo();
		if (projectModel != null)
		{
			if (projectModelLocal != null)
			{
				return StringUtils.isNullString(projectModelLocal.getGuid()) || projectModelLocal.getGuid().equals(projectModel.getGuid());
			}
		}
		return false;
	}

	/**
	 * 部署模型
	 *
	 * @param sessionId
	 * @throws SQLException
	 */
	protected ProjectModel deploy(String sessionId, ProjectModel projectModel, boolean hasClassificationLicense) throws Exception
	{
		ProjectModel projectModelNew;
		String syncErrorMessage = null;
		try
		{
			this.saveTrack(sessionId, "Start Sync", "");

			this.serviceContext.setServiceState(ServiceStateEnum.WAITING);

			SyncDatabase syncDatabase = new SyncDatabase(this.serviceContext, this.sqlSessionFactory, this.loadXMLModel(), sessionId);
			projectModelNew = syncDatabase.startSync(projectModel, hasClassificationLicense);

			this.serviceContext.setServiceState(ServiceStateEnum.NORMAL);

			return projectModelNew;

		}
		catch (Exception e)
		{
			DynaLogger.error(e.getMessage());
			syncErrorMessage = e.getMessage();
			throw e;
		}
		finally
		{
			if (StringUtils.isNullString(syncErrorMessage))
			{
				this.saveTrack(sessionId, "End Sync", "Successful");
			}
			else
			{
				this.saveTrack(sessionId, "End Sync", "sync error:" + syncErrorMessage);
				this.serviceContext.setServiceState(ServiceStateEnum.SYNCHRONIZE);
			}
		}
	}
	
	/**
	 * 部署分类字段
	 *
	 * @param sessionId
	 * @throws SQLException
	 */
	protected void deployClassificationField(List<Map<String, String>> dataSource,String sessionId) throws ServiceRequestException
	{
		String syncErrorMessage = null;
		try
		{
			this.saveTrack(sessionId, "Start Sync", "");

			this.serviceContext.setServiceState(ServiceStateEnum.WAITING);

			SyncDatabase syncDatabase = new SyncDatabase(this.serviceContext, this.sqlSessionFactory, this.loadXMLModel(), sessionId);
			syncDatabase.syncClassificationField(dataSource);
			this.serviceContext.setServiceState(ServiceStateEnum.NORMAL);
		}
		catch (Exception e)
		{
			DynaLogger.error(e.getMessage());
			syncErrorMessage = e.getMessage();
			throw new ServiceRequestException(null, e.getMessage(), e);
		}
		finally
		{
			if (StringUtils.isNullString(syncErrorMessage))
			{
				this.saveTrack(sessionId, "End Sync", "Successful");
			}
			else
			{
				this.saveTrack(sessionId, "End Sync", "sync error:" + syncErrorMessage);
				this.serviceContext.setServiceState(ServiceStateEnum.SYNCHRONIZE);
			}
		}
	}

	private ModelXMLCache loadXMLModel()
	{
		ModelXMLCache xmlModel = new ModelXMLCache();
		String modelConfigPath = this.serviceContext.getModelConfigPath();
		this.stubService.getClassificationModelLoadStub().loadXMLCodeModel(xmlModel, modelConfigPath);
		this.stubService.getClassModelLoadStub().loadXMLClassObject(xmlModel, modelConfigPath);
		this.loadXMLInterface(xmlModel);
		return xmlModel;
	}

	private void loadXMLInterface(ModelXMLCache xmlModel)
	{
		InterfaceModelService ims = DataServer.getInterfaceModelService();
		xmlModel.setInterfaceFieldMap(ims.getInterfaceFieldMap());
		xmlModel.setInterfaceIndexMap(ims.getInterfaceIndexMap());
		xmlModel.setInterfaceMap(ims.getInterfaceMap());
	}

	protected String getCurrentLoginUser()
	{
		Session session = DataServer.getSystemDataService().findInCache(Session.class, new FieldValueEqualsFilter<>("CLIENTTYPE", Signature.MODULE_MODELER));
		return session != null ? session.getUserId() : null;
	}

	@SuppressWarnings("unchecked")
	protected ProjectModel getSyncInfo() throws DynaDataException
	{
		ModelSync modelSync = new ModelSync();
		modelSync.put("ISLASTER", "Y");
		List<ModelSync> modelSyncList;
		try
		{
			modelSyncList = modelSyncMapper.getSync(modelSync);
			if (modelSyncList != null)
			{
				for (ModelSync sync : modelSyncList)
				{
					ProjectModel projectModel = new ProjectModel();
					projectModel.setDescription(sync.getDescription());
					projectModel.setGuid(sync.getGuid());
					projectModel.setName(sync.getSyncName());
					projectModel.setOmGuid(sync.getOmGuid());
					projectModel.setOmName(sync.getOmName());
					projectModel.setRemarks(sync.getRemarks());
					projectModel.setSyncIP(sync.getSyncIP());
					return projectModel;
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataModelException("get ProjectModel failure", e);
		}

		return null;
	}

	/**
	 * 从数据库中生成模型文件
	 *
	 * @throws SQLException
	 */
	protected void makeModelFile(boolean hasClassificationLicense) throws DynaDataException
	{
		String confRootPath = EnvUtils.getConfRootPath() + "/tmp";

		File omFile = new File(confRootPath + "/om");
		File iconFile = new File(confRootPath + "/icon");

		String timeformat = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

		Boolean isRename = FileUtils.rename(omFile.getAbsolutePath(), "om" + timeformat);
		if (isRename != null)
		{
			if (isRename)
			{
				DynaLogger.info("Backup model file success");
			}
			else
			{
				DynaLogger.error("Backup model file failure");
				throw new DynaDataModelException("Backup model file failure", null);
			}
		}

		isRename = FileUtils.rename(iconFile.getAbsolutePath(), "icon" + timeformat);
		if (isRename != null)
		{
			if (isRename)
			{
				DynaLogger.info("Backup model file success");
			}
			else
			{
				DynaLogger.error("Backup model file failure");
				throw new DynaDataModelException("Backup model file failure", null);
			}
		}

		File srcIconFile = new File(EnvUtils.getConfRootPath() + "/conf/icon");
		try
		{
			FileUtils.copyFile(srcIconFile, new File(confRootPath));
		}
		catch (Exception e)
		{
			throw new DynaDataModelException("copy model file failure", null);
		}

		this.write(hasClassificationLicense);
	}

	protected boolean isDeployLock(String sessionId) throws DynaDataException
	{
//		DataServer.getTransactionManager().startTransaction(sessionId);
		try (Connection conn = this.sqlSession.getConnection())
		{
			String sql = "SELECT PERMITSYNC FROM ma_sync_lock";
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			ResultSet executeQuery = prepareStatement.executeQuery();

			boolean isLock = false;
			if (executeQuery.next())
			{
				String lockString = executeQuery.getString(1);
				isLock = BooleanUtils.getBooleanByYN(lockString);
			}

//			DataServer.getTransactionManager().commitTransaction();

			return isLock;
		}
		catch (SQLException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			return false;
		}
		finally
		{
		}
	}

	private void write(boolean hasClassificationLicense) throws DynaDataException
	{
		DynaModel dynaModel = this.loadModel(hasClassificationLicense);
		Serializer serializer = new Persister(new DefaultStrategy());

		this.writeClassModel(dynaModel, serializer);

		this.writeCodeModel(dynaModel.getCodeItemMap(), serializer, "/code-models");
		this.writeCodeModel(dynaModel.getClassificationItemMap(), serializer, "/classification-models");
	}

	private void loadClassModel(DynaModel dynaModel)
	{
		Map<String, ClassObject> classObjectMap = DataServer.getClassModelService().getClassObjectMap();
		dynaModel.setClassObjectMap(classObjectMap);
	}

	private void writeClassModel(DynaModel dynaModel, Serializer serializer)
	{
		File file = new File(this.getModelConfigPath() + "/class-models");
		if (!file.exists())
		{
			file.mkdirs();
		}
		FileOutputStream outputStream = null;
		Writer writer = null;
		for (ClassObject classObject : dynaModel.getClassObjectMap().values())
		{
			try
			{
				if (!classObject.isBuiltin())
				{
					ClassObject classObject_ = new ClassObject(classObject.getInfo().clone());
					classObject_.setInterfaces(classObject.getSuperInterface());
					classObject_.setFieldList(classObject.getFieldList());
					outputStream = new FileOutputStream(file.getAbsolutePath() + "/" + classObject.getName() + ".xml");
					writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
					serializer.write(classObject_, writer);
				}
			}
			catch (Exception e)
			{
				throw new DynaDataModelException("class-model write error", e);
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (outputStream != null)
				{
					try
					{
						outputStream.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void writeCodeModel(Map<String, CodeModelInfo> codeItemMap, Serializer serializer, String modelFolder)
	{
		File file = new File(this.getModelConfigPath() + modelFolder);
		if (!file.exists())
		{
			file.mkdirs();
		}
		FileOutputStream outputStream = null;
		Writer writer = null;
		for (String key : codeItemMap.keySet())
		{
			if ("CodeModel".equalsIgnoreCase(key))
			{
				continue;
			}
			try
			{
				outputStream = new FileOutputStream(file.getAbsolutePath() + "/" + key + ".xml");
				writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				serializer.write(codeItemMap.get(key), writer);
			}
			catch (Exception e)
			{
				throw new DynaDataModelException("code-model write error", e);
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (outputStream != null)
				{
					try
					{
						outputStream.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String getModelConfigPath()
	{
		File file = new File(this.serviceContext.getModelConfigPath() + "/../../tmp/om");
		if (!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	private DynaModel loadModel(boolean hasClassificationLicense)
	{
		DynaModel dynaModel = new DynaModel();
		this.loadClassModel(dynaModel);
		this.loadCodeModel(dynaModel, hasClassificationLicense);

		return dynaModel;
	}

	protected void loadCodeModel(DynaModel dynaModel, boolean hasClassificationLicense)
	{
		List<CodeObject> codeObjectList = DataServer.getCodeModelService().listAllCodeObjectList();
		if (!SetUtils.isNullList(codeObjectList))
		{
			this.loadCodeModel(dynaModel, codeObjectList);
			this.loadClassificationModel(dynaModel, codeObjectList, hasClassificationLicense);
		}
	}

	private void loadCodeModel(DynaModel dynaModel, List<CodeObject> codeObjectList)
	{
		Map<String, CodeModelInfo> codeItemMap = new HashMap<>();
		CodeModelInfo root = new CodeModelInfo();
		root.setName("CodeModel");
		codeItemMap.put(root.getName(), root);

		codeObjectList.stream().filter(code -> !code.isClassification()).forEach(code -> {

			// 把CodeMaster转为CodeItem存储
			CodeModelInfo master = new CodeModelInfo(code);
			root.addChild(master);
			codeItemMap.put(code.getName(), master);

			List<CodeItem> codeDetailList = code.getCodeDetailList();
			if (!SetUtils.isNullList(codeDetailList))
			{
				codeDetailList.forEach(codeItem -> {
					codeItem.setMasterName(code.getName());
					addCodeItem(code.getName(), codeItem, codeItemMap, false);
				});
				dynaModel.setCodeItemMap(codeItemMap);
			}
		});
	}

	private void loadClassificationModel(DynaModel dynaModel, List<CodeObject> codeObjectList, boolean hasClassificationLicense)
	{
		Map<String, CodeModelInfo> codeItemMap = new HashMap<>();
		CodeModelInfo root = new CodeModelInfo();
		root.setName("CodeModel");
		codeItemMap.put(root.getName(), root);

		codeObjectList.stream().filter(CodeObject::isClassification).forEach(code -> {

			// 把CodeMaster转为CodeItem存储
			CodeModelInfo master = new CodeModelInfo(code);
			root.addChild(master);
			codeItemMap.put(code.getName(), master);

			List<CodeItem> codeDetailList = code.getCodeDetailList();
			if (!SetUtils.isNullList(codeDetailList))
			{
				codeDetailList.forEach(codeItem -> {
					codeItem.setMasterName(code.getName());
					addCodeItem(code.getName(), codeItem, codeItemMap, hasClassificationLicense);
				});
				dynaModel.setClassificationItemMap(codeItemMap);
			}
		});
	}

	private void addCodeItem(String masterName, CodeItem codeItem_, Map<String, CodeModelInfo> codeItemMap, boolean hasClassificationLicense)
	{
		codeItemMap.put(masterName + "_" + codeItem_.getName(), new CodeModelInfo(codeItem_));
		if (hasClassificationLicense)
		{
			codeItemMap.get(masterName + "_" + codeItem_.getName()).setFieldList(codeItem_.getClassificationFieldList());
		}

		List<CodeItem> codeDetailList = codeItem_.getCodeDetailList();
		if (!SetUtils.isNullList(codeDetailList))
		{
			codeDetailList.forEach(codeItem -> {
				codeItem.setMasterName(masterName);
				addCodeItem(masterName, codeItem, codeItemMap, hasClassificationLicense);
			});
		}
	}

	protected void saveTrack(String sessionId, String what, String resultStr) throws ServiceRequestException
	{
		String sid = "";
		String whom = "";
		String crtUserGuid = "";
		String where = "";
		String objectStr = "";

		Session session = DataServer.getDSCommonService().getSession(sessionId);
		User user = DataServer.getSystemDataService().get(User.class, session.getUserGuid());
		Group group = DataServer.getSystemDataService().get(Group.class, session.getLoginGroupGuid());
		Role role = DataServer.getSystemDataService().get(Role.class, session.getLoginRoleGuid());

		sid = StringUtils.convertNULLtoString(sessionId);

		whom = user.getUserId() + "-" + user.getUserName() + "::" + group.getName() + "(" + role.getName() + ")";

		crtUserGuid = session.getUserGuid();
		where = session.getIpAddress();

		objectStr = StringUtils.convertNULLtoString("OM");

		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug(whom + "/" + crtUserGuid + "!" + where + "!" + what + "!" + objectStr + "!" + resultStr);
		}

		if (StringUtils.isNullString(crtUserGuid))
		{
			return;
		}

		SysTrack track = new SysTrack();
		track.put(SysTrack.SID, sid);
		track.put(SysTrack.BY_WHOM, whom);
		track.put(SysTrack.AT_WHERE, where);
		track.put(SysTrack.DO_WHAT, what);
		if (objectStr.length() > 512)
		{
			objectStr = objectStr.substring(0, 512);
		}
		track.put(SysTrack.TG_OBJECT, objectStr);
		track.put(SysTrack.RESULT, resultStr);
		track.put(SystemObject.CREATE_USER_GUID, crtUserGuid);

		DataServer.getSystemDataService().save(track);
	}

	public void deleteModelerSession()
	{
		// 删除建模器session
		try
		{
			this.sessionMapper.deleteModelerSession();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
