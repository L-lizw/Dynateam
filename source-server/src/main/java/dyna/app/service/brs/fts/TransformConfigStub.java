/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与文件夹相关的操作分支
 * Caogc 2010-8-31
 */
package dyna.app.service.brs.fts;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.trans.*;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.trans.TransExecuteType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;

import java.util.*;

/**
 * 与转换相关的操作分支
 * 
 * @author Wanglh
 * 
 */
public class TransformConfigStub extends AbstractServiceStub<FTSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected TransformConfigStub(ServiceContext context, FTSImpl service)
	{
		super(context, service);
	}

	protected TransformConfig saveTransformConfig(TransformConfig config) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(config.getGuid()))
			{
				config.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			config.setUpdateUserGuid(this.stubService.getOperatorGuid());

			String save = sds.save(config);
			if (StringUtils.isGuid(save))
			{
				return this.getTransformConfig(save);
			}
			return config;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected TransformConfig getTransformConfig(String transConfigGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(transConfigGuid))
		{
			return null;
		}
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, transConfigGuid);

		try
		{
			TransformConfig query = sds.queryObject(TransformConfig.class, filter);

			if (!StringUtils.isGuid(query.getTransformType()))
			{
				return query;
			}

			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(query.getTransformType());
			if (codeItem != null)
			{
				query.put(TransformConfig.TRANSFORM_TYPE + "NAME", codeItem.getName());
			}
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected List<TransformConfig> listTransformConfig4Class(String classGuid, TransExecuteType executeType) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(classGuid))
		{
			return null;
		}

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TransformConfig.TRANSFORM_CLASS, classGuid);
		if (executeType != null)
		{
			switch (executeType)
			{
			case MANUAL:
				filter.put(TransformConfig.ISMANUAL, "Y");
				break;
			case OBJECT:
				filter.put(TransformConfig.ISOBJECT, "Y");
				break;
			case WF:
				filter.put(TransformConfig.ISWORKFLOW, "Y");
				break;
			default:
				break;
			}

		}

		return this.listTransformConfig(filter, true);

	}

	protected List<TransformConfig> listTransformConfig(Map<String, Object> filter, boolean isCheckLic) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (filter == null)
		{
			filter = new HashMap<String, Object>();
		}

		List<String> listTLicenseModules = this.listTLicenseModules();

		try
		{
			List<TransformConfig> query = sds.query(TransformConfig.class, filter);
			if (!SetUtils.isNullList(query))
			{
				for (int i = query.size() - 1; i >= 0; i--)
				{
					TransformConfig config = query.get(i);
					if (!StringUtils.isGuid(config.getTransformType()))
					{
						continue;
					}

					CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(config.getTransformType());
					if (codeItem != null)
					{
						if (isCheckLic)
						{
							boolean hasLic = false;
							if (!SetUtils.isNullList(listTLicenseModules))
							{
								for (String module : listTLicenseModules)
								{
									if (codeItem.getCode().toUpperCase().startsWith(module))
									{
										hasLic = true;
										break;
									}
								}

							}

							if (!hasLic)
							{
								query.remove(i);
								continue;
							}
						}

						config.put(TransformConfig.TRANSFORM_TYPE + "NAME", codeItem.getName());
						// config.put(TransformConfig.TRANSFORM_TYPE + "$TITLE",
						// codeItem.getTitle(this.stubService.getUserSignature().getLanguageEnum()) + "[" +
						// codeItem.getCode()
						// + "]");
						if (this.stubService.getSignature() instanceof UserSignature)
						{
							config.put(TransformConfig.TRANSFORM_TYPE + "$TITLE", codeItem.getTitle(this.stubService.getUserSignature().getLanguageEnum()));
						}

					}
				}
			}

			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private boolean checkCreateManualQueue(List<TransformManualPerformer> performerList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(performerList))
		{
			return false;
		}

		String operatorGuid = this.stubService.getOperatorGuid();
		for (TransformManualPerformer performer : performerList)
		{
			if (PerformerTypeEnum.USER == performer.getPerfType())
			{
				if (operatorGuid.equals(performer.getPerfGuid()))
				{
					return true;
				}
			}
			else if (performer.getPerfType() == PerformerTypeEnum.RIG)
			{
				List<User> listUser = this.stubService.getAAS().listUserByRoleInGroup(performer.getPerfGuid());
				if (!SetUtils.isNullList(listUser))
				{
					for (User user : listUser)
					{
						if (operatorGuid.equals(user.getGuid()))
						{
							return true;
						}
					}
				}
			}
			else if (performer.getPerfType() == PerformerTypeEnum.GROUP)
			{
				List<User> listUser = this.stubService.getAAS().listUserInGroupAndSubGroup(performer.getPerfGuid());
				if (!SetUtils.isNullList(listUser))
				{
					for (User user : listUser)
					{
						if (operatorGuid.equals(user.getGuid()))
						{
							return true;
						}
					}
				}
			}
			else if (performer.getPerfType() == PerformerTypeEnum.ROLE)
			{
				List<User> listUser = ((AASImpl) this.stubService.getAAS()).getUserStub().listUserInRole(performer.getPerfGuid(), true);
				if (!SetUtils.isNullList(listUser))
				{
					for (User user : listUser)
					{
						if (operatorGuid.equals(user.getGuid()))
						{
							return true;
						}
					}
				}
			}

		}

		return false;
	}

	protected boolean checkCreate4Manual(TransformConfig config, FoundationObject foundation) throws ServiceRequestException
	{
		TransformManualConfig manualConfig = config.getManualConfig();

		if (manualConfig != null)
		{
			switch (foundation.getStatus())
			{
			case ECP:
				if (manualConfig.isECP())
				{

					if (this.checkCreateManualQueue(manualConfig.getEcpPerformerList()))
					{
						config.setSignGuid(manualConfig.getEcpSolution());
						return true;
					}
				}
				break;
			case PRE:
				if (manualConfig.isPRE())
				{
					if (this.checkCreateManualQueue(manualConfig.getPrePerformerList()))
					{
						config.setSignGuid(manualConfig.getPreSolution());
						return true;
					}
				}
				break;
			case RELEASE:
				if (manualConfig.isRLS())
				{
					if (this.checkCreateManualQueue(manualConfig.getRlsPerformerList()))
					{

						config.setSignGuid(manualConfig.getRlsSolution());
						return true;
					}
				}
				break;
			case WIP:
			default:
				if (manualConfig.isWIP())
				{
					if (this.checkCreateManualQueue(manualConfig.getWipPerformerList()))
					{

						config.setSignGuid(manualConfig.getWipSolution());
						return true;
					}
				}

				break;
			}
		}

		return false;
	}

	protected List<TransformConfig> listTransformConfig4Manual(ObjectGuid objectGuid) throws ServiceRequestException
	{
		List<TransformConfig> configList = this.listTransformConfig4Class(objectGuid.getClassGuid(), TransExecuteType.MANUAL);
		if (SetUtils.isNullList(configList))
		{
			return null;
		}

		// 过滤不满足手动的条件
		List<TransformConfig> resultList = new ArrayList<TransformConfig>();
		FoundationObject foundation = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		for (TransformConfig config : configList)
		{
			TransformManualConfig manualConfig = this.getManualConfig(config.getGuid());
			config.setManualConfig(manualConfig);
			boolean checkCreate4Manual = this.checkCreate4Manual(config, foundation);
			if (checkCreate4Manual)
			{
				resultList.add(config);
			}
		}

		return resultList;

	}

	protected List<TransformConfig> listTransformConfig4WF(ObjectGuid objectGuid, String wfName, String activityName) throws ServiceRequestException
	{
		List<TransformConfig> configList = this.listTransformConfig4Class(objectGuid.getClassGuid(), TransExecuteType.WF);
		if (SetUtils.isNullList(configList))
		{
			return null;
		}

		// 过滤不满足工作流的条件
		List<TransformConfig> resultList = new ArrayList<TransformConfig>();
		for (TransformConfig config : configList)
		{
			List<TransformWFConfig> listWFConfig = this.listWFConfig(config.getGuid());
			if (!SetUtils.isNullList(listWFConfig))
			{
				for (TransformWFConfig wfConfig : listWFConfig)
				{
					if (wfName.equalsIgnoreCase(wfConfig.getWorkflowName()) && activityName.equalsIgnoreCase(wfConfig.getActivityName()))
					{
						config.setSignGuid(wfConfig.getSignatureSolution());
						resultList.add(config);
					}
				}
			}

		}

		return resultList;

	}

	protected List<TransformConfig> listTransformConfig4CheckIn(ObjectGuid objectGuid) throws ServiceRequestException
	{

		List<TransformConfig> configList = this.listTransformConfig4Class(objectGuid.getClassGuid(), TransExecuteType.OBJECT);
		if (SetUtils.isNullList(configList))
		{
			return null;
		}

		// 过滤不满足检入的条件
		List<TransformConfig> resultList = new ArrayList<TransformConfig>();
		for (TransformConfig config : configList)
		{
			TransformObjectConfig objectConfig = this.getObjectConfig(config.getGuid());
			if (objectConfig != null && objectConfig.isCheckIn())
			{
				config.setSignGuid(objectConfig.getCheckinSolution());
				resultList.add(config);
			}

		}

		return resultList;
	}

	protected List<TransformConfig> listTransformConfig4Uploaded(ObjectGuid objectGuid) throws ServiceRequestException
	{

		List<TransformConfig> configList = this.listTransformConfig4Class(objectGuid.getClassGuid(), TransExecuteType.OBJECT);
		if (SetUtils.isNullList(configList))
		{
			return null;
		}

		// 过滤不满足上传的条件
		List<TransformConfig> resultList = new ArrayList<TransformConfig>();
		for (TransformConfig config : configList)
		{
			TransformObjectConfig objectConfig = this.getObjectConfig(config.getGuid());
			if (objectConfig != null && objectConfig.isUploaded())
			{
				config.setSignGuid(objectConfig.getUploadedSolution());
				resultList.add(config);
			}

		}

		return resultList;

	}

	protected List<TransformWFConfig> listWFConfig(String workflowName, String activityName) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TransformWFConfig.WORKFLOW_NAME, workflowName);
		filter.put(TransformWFConfig.ACTIVITY_NAME, activityName);
		try
		{
			List<TransformWFConfig> query = sds.query(TransformWFConfig.class, filter);
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformWFConfig> listWFConfig(String transConfigGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(transConfigGuid))
		{
			return null;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<>();
		filter.put(TransformWFConfig.TRANSFORM_CONFIG_GUID, transConfigGuid);

		try
		{
			List<TransformWFConfig> query = sds.query(TransformWFConfig.class, filter);

			if (!SetUtils.isNullList(query))
			{
				for (TransformWFConfig config : query)
				{
					if (!StringUtils.isNullString(config.getWorkflowName()))
					{
						WorkflowProcessInfo workflowProcess = this.stubService.getWFM().getProcessModelInfo(config.getWorkflowName());
						if (workflowProcess != null)
						{
							String title = workflowProcess.getDescription();
							config.put(TransformWFConfig.WORKFLOW_TITLE, title);
						}
					}

					if (!StringUtils.isNullString(config.getActivityName()))
					{
						WorkflowActivityInfo workflowActivity = this.stubService.getWFM().getWorkflowActivityInfoByName(config.getWorkflowName(), config.getActivityName());

						if (workflowActivity != null)
						{
							String title = workflowActivity.getTitle(this.stubService.getUserSignature().getLanguageEnum());
							config.put(TransformWFConfig.ACTIVITY_TITLE, title);
						}
					}
				}
			}

			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveWFConfig(TransformWFConfig wfConfig) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(wfConfig.getGuid()))
			{
				wfConfig.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			wfConfig.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(wfConfig);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteWFConfig(String wfConfigGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			sds.delete(TransformWFConfig.class, wfConfigGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected TransformObjectConfig getObjectConfig(String transConfigGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(transConfigGuid))
		{
			return null;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TransformObjectConfig.TRANSFORM_CONFIG_GUID, transConfigGuid);

		try
		{
			TransformObjectConfig query = sds.queryObject(TransformObjectConfig.class, filter);

			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveObjectConfig(TransformObjectConfig objectConfig) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(objectConfig.getGuid()))
			{
				objectConfig.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			objectConfig.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(objectConfig);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteObjectConfig(String objectConfig) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			sds.delete(TransformObjectConfig.class, objectConfig);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected TransformManualConfig getManualConfig(String transConfigGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(transConfigGuid))
		{
			return null;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TransformManualConfig.TRANSFORM_CONFIG_GUID, transConfigGuid);

		try
		{
			TransformManualConfig query = sds.queryObject(TransformManualConfig.class, filter);

			if (query != null)
			{
				List<TransformManualPerformer> performerList = sds.query(TransformManualPerformer.class, filter);
				if (!SetUtils.isNullList(performerList))
				{
					decodePerfName(performerList);

					List<TransformManualPerformer> wipList = new ArrayList<TransformManualPerformer>();
					List<TransformManualPerformer> preList = new ArrayList<TransformManualPerformer>();
					List<TransformManualPerformer> ecpList = new ArrayList<TransformManualPerformer>();
					List<TransformManualPerformer> rlsList = new ArrayList<TransformManualPerformer>();
					query.setEcpPerformerList(ecpList);
					query.setWipPerformerList(wipList);
					query.setPrePerformerList(preList);
					query.setRlsPerformerList(rlsList);
					for (TransformManualPerformer p : performerList)
					{
						if (SystemStatusEnum.WIP.name().equalsIgnoreCase(p.getStatusType()))
						{
							wipList.add(p);
						}
						else if (SystemStatusEnum.ECP.name().equalsIgnoreCase(p.getStatusType()))
						{
							ecpList.add(p);
						}
						else if (SystemStatusEnum.PRE.name().equalsIgnoreCase(p.getStatusType()))
						{
							preList.add(p);
						}
						else if (SystemStatusEnum.RELEASE.name().equalsIgnoreCase(p.getStatusType()))
						{
							rlsList.add(p);
						}
					}
				}
			}
			if (query == null)
			{
				return new TransformManualConfig();
			}
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveManualConfig(TransformManualConfig manualConfig) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(manualConfig.getGuid()))
			{
				manualConfig.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			manualConfig.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(manualConfig);

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(TransformManualPerformer.TRANSFORM_CONFIG_GUID, manualConfig.getTransformConfigGuid());
			sds.delete(TransformManualPerformer.class, filter, "deleteAll");

			if (!SetUtils.isNullList(manualConfig.getWipPerformerList()))
			{
				for (TransformManualPerformer p : manualConfig.getWipPerformerList())
				{
					p.setCreateUserGuid(this.stubService.getOperatorGuid());
					p.setUpdateUserGuid(this.stubService.getOperatorGuid());
					p.setGuid(null);
					p.setStatusType(SystemStatusEnum.WIP.name());
					p.setTransformConfigGuid(manualConfig.getTransformConfigGuid());
					sds.save(p);
				}
			}

			if (!SetUtils.isNullList(manualConfig.getPrePerformerList()))
			{
				for (TransformManualPerformer p : manualConfig.getPrePerformerList())
				{
					p.setCreateUserGuid(this.stubService.getOperatorGuid());
					p.setUpdateUserGuid(this.stubService.getOperatorGuid());
					p.setGuid(null);
					p.setStatusType(SystemStatusEnum.PRE.name());
					p.setTransformConfigGuid(manualConfig.getTransformConfigGuid());
					sds.save(p);
				}
			}

			if (!SetUtils.isNullList(manualConfig.getEcpPerformerList()))
			{
				for (TransformManualPerformer p : manualConfig.getEcpPerformerList())
				{
					p.setCreateUserGuid(this.stubService.getOperatorGuid());
					p.setUpdateUserGuid(this.stubService.getOperatorGuid());
					p.setGuid(null);
					p.setStatusType(SystemStatusEnum.ECP.name());
					p.setTransformConfigGuid(manualConfig.getTransformConfigGuid());
					sds.save(p);
				}
			}

			if (!SetUtils.isNullList(manualConfig.getRlsPerformerList()))
			{
				for (TransformManualPerformer p : manualConfig.getRlsPerformerList())
				{
					p.setCreateUserGuid(this.stubService.getOperatorGuid());
					p.setUpdateUserGuid(this.stubService.getOperatorGuid());
					p.setGuid(null);
					p.setStatusType(SystemStatusEnum.RELEASE.name());
					p.setTransformConfigGuid(manualConfig.getTransformConfigGuid());
					sds.save(p);
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteManualConfig(String manualConfig) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			sds.delete(TransformManualConfig.class, manualConfig);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformFieldMapping> listFieldMappingConfig(String transConfigGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(transConfigGuid))
		{
			return null;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TransformFieldMapping.TRANSFORM_CONFIG_GUID, transConfigGuid);

		try
		{
			List<TransformFieldMapping> query = sds.query(TransformFieldMapping.class, filter);
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveFieldMapping(TransformFieldMapping fieldMapping) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(fieldMapping.getGuid()))
			{
				fieldMapping.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			fieldMapping.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(fieldMapping);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteFieldMapping(String fieldMapping) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			sds.delete(TransformFieldMapping.class, fieldMapping);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteTransformConfig(String configGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (configGuid == null)
			{
				return;
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(TransformObjectConfig.TRANSFORM_CONFIG_GUID, configGuid);
			sds.delete(TransformObjectConfig.class, filter, "deleteConfig");
			sds.delete(TransformManualConfig.class, filter, "deleteConfig");
			sds.delete(TransformFieldMapping.class, filter, "deleteConfig");
			sds.delete(TransformWFConfig.class, filter, "deleteConfig");

			sds.delete(TransformManualPerformer.class, filter, "deleteAll");

			sds.delete(TransformConfig.class, configGuid);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformConfig> saveTransformConfig(List<TransformConfig> configList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(configList))
		{
			return null;
		}
		List<TransformConfig> result = new ArrayList<TransformConfig>();
		String configGuid = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			for (TransformConfig config : configList)
			{

				configGuid = config.getGuid();
				TransformConfig resultConfig = config;
				if (config.isDelete() && StringUtils.isGuid(config.getGuid()))
				{
					this.deleteTransformConfig(config.getGuid());
					config.setFieldConfigList(null);
					config.setWfConfigList(null);
					config.setObjectConfig(null);
					config.setManualConfig(null);
					continue;
				}
				else if (!StringUtils.isGuid(config.getGuid()))
				{
					// oriGuid = config.getGuid();
					config.setGuid(null);
					c.add(Calendar.SECOND, 1);
					config.put("CREATETIME", c.getTime());

					resultConfig = this.saveTransformConfig(config);
					configGuid = resultConfig.getGuid();

				}
				else if (config.isChanged())
				{
					resultConfig = this.saveTransformConfig(config);
				}

				if (!SetUtils.isNullList(config.getWfConfigList()))
				{
					boolean hasWorkflow = false;
					for (TransformWFConfig wf : config.getWfConfigList())
					{
						if (StringUtils.isNullString(wf.getWorkflowName()))
						{
							continue;
						}

						if (wf.isDelete() && StringUtils.isGuid(wf.getGuid()))
						{
							this.deleteWFConfig(wf.getGuid());
						}
						else if (!StringUtils.isGuid(wf.getGuid()))
						{
							wf.setGuid(null);
							c.add(Calendar.SECOND, 1);
							wf.put("CREATETIME", c.getTime());

							wf.setTransformConfigGuid(configGuid);
							this.saveWFConfig(wf);
							hasWorkflow = true;
						}
						else if (wf.isChanged())
						{
							wf.setTransformConfigGuid(configGuid);
							this.saveWFConfig(wf);
							hasWorkflow = true;
						}
						else
						{
							hasWorkflow = true;
						}

					}

					if (hasWorkflow)
					{
						resultConfig.setWorkflow(true);
					}
					else
					{
						resultConfig.setWorkflow(false);
					}

				}
				else
				{
					resultConfig.setWorkflow(false);
				}

				if (!SetUtils.isNullList(config.getFieldConfigList()))
				{
					for (TransformFieldMapping field : config.getFieldConfigList())
					{
						if (StringUtils.isNullString(field.getTargetFieldName()) || StringUtils.isNullString(field.getSourceFieldName()))
						{
							continue;
						}

						if (field.isDelete() && StringUtils.isGuid(field.getGuid()))
						{
							this.deleteFieldMapping(field.getGuid());
						}
						else if (!StringUtils.isGuid(field.getGuid()))
						{
							field.setGuid(null);
							c.add(Calendar.SECOND, 1);
							field.put("CREATETIME", c.getTime());
							field.setTransformConfigGuid(configGuid);
							this.saveFieldMapping(field);
						}
						else if (field.isChanged())
						{
							field.setTransformConfigGuid(configGuid);
							this.saveFieldMapping(field);
						}

					}
				}

				TransformObjectConfig objectConfig = config.getObjectConfig();
				if (objectConfig != null)
				{
					// isChange = true;
					if (!StringUtils.isGuid(objectConfig.getGuid()))
					{
						objectConfig.setGuid(null);
						objectConfig.setTransformConfigGuid(configGuid);
						this.saveObjectConfig(objectConfig);
					}
					else if (objectConfig.isChanged())
					{
						objectConfig.setTransformConfigGuid(configGuid);
						this.saveObjectConfig(objectConfig);
					}

					if (objectConfig.isCheckIn() || objectConfig.isUploaded())
					{
						resultConfig.setObject(true);
					}
					else
					{
						resultConfig.setObject(false);
					}

				}

				TransformManualConfig manualConfig = config.getManualConfig();
				if (manualConfig != null)
				{
					// isChange = true;

					manualConfig.setTransformConfigGuid(configGuid);
					this.saveManualConfig(manualConfig);

					if (manualConfig.isPRE() || manualConfig.isECP() || manualConfig.isRLS() || manualConfig.isWIP())
					{
						resultConfig.setManual(true);
					}
					else
					{
						resultConfig.setManual(false);
					}
				}

				if (resultConfig.isChanged())
				{
					this.saveTransformConfig(resultConfig);
				}

			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

		return result;
	}

	public List<String> listTLicenseModules() throws ServiceRequestException
	{
		// String allModules =
		List<String> allModules = this.stubService.getLIC().getLicenseModuleList();
		List<String> result = new ArrayList<String>();
		if (!SetUtils.isNullList(allModules))
		{
			for (String module : allModules)
			{
				if (module.startsWith("DT-T*"))
				{
					result.add(module.substring(3));
				}
				else if (module.equalsIgnoreCase("DT-PDFSIG"))
				{
					result.add(module.substring(3));
				}
			}
		}

		return result;
	}

	protected void decodePerfName(List<TransformManualPerformer> list) throws ServiceRequestException
	{
		if (list != null)
		{
			for (TransformManualPerformer item : list)
			{
				if (item.getPerfType() == PerformerTypeEnum.GROUP)
				{
					if (!StringUtils.isNullString(item.getPerfGuid()))
					{
						Group group = this.stubService.getAAS().getGroup(item.getPerfGuid());
						if (group != null)
						{
							item.setName(group.getGroupId() + "-" + group.getGroupName());
						}
					}
				}
				else if (item.getPerfType() == PerformerTypeEnum.ROLE)
				{
					if (!StringUtils.isNullString(item.getPerfGuid()))
					{
						Role role = this.stubService.getAAS().getRole(item.getPerfGuid());
						if (role != null)
						{
							item.setName(role.getRoleId() + "-" + role.getRoleName());
						}
					}
				}
				else if (item.getPerfType() == PerformerTypeEnum.RIG)
				{
					if (!StringUtils.isNullString(item.getPerfGuid()))
					{
						RIG rig = this.stubService.getAAS().getRIG(item.getPerfGuid());
						if (rig != null)
						{
							Group group = null;
							Role role = null;
							if (!StringUtils.isNullString(rig.getGroupGuid()))
							{
								group = this.stubService.getAAS().getGroup(rig.getGroupGuid());
							}
							if (!StringUtils.isNullString(rig.getRoleGuid()))
							{
								role = this.stubService.getAAS().getRole(rig.getRoleGuid());
							}
							if (group != null && role != null)
							{
								item.setName(group.getGroupName() + "-" + role.getRoleName());
							}
						}
					}

				}
				else if (item.getPerfType() == PerformerTypeEnum.USER)
				{
					if (!StringUtils.isNullString(item.getPerfGuid()))
					{
						User user = this.stubService.getAAS().getUser(item.getPerfGuid());
						if (user != null)
						{
							item.setName(user.getUserId() + "-" + user.getUserName());
						}
					}
				}
			}
		}
	}

}
