package dyna.app.service.brs.fts;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.trans.*;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcTrack;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ProcessStatusEnum;
import dyna.common.systemenum.trans.TransParamFromType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TransformSignStub extends AbstractServiceStub<FTSImpl>
{

	protected TransformSign saveTransSign(TransformSign sign) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(sign.getGuid()))
			{
				sign.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			sign.setUpdateUserGuid(this.stubService.getOperatorGuid());

			String save = sds.save(sign);
			if (StringUtils.isGuid(save))
			{
				return this.getTransformSign(save);
			}
			else
			{
				return sign;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected TransformSign getTransformSign(String signGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(signGuid))
		{
			return null;
		}
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, signGuid);

		try
		{
			TransformSign query = sds.queryObject(TransformSign.class, filter);
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected TransformSignParam getTransformSignParam(String signPargmGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(signPargmGuid))
		{
			return null;
		}
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, signPargmGuid);

		try
		{
			TransformSignParam query = sds.queryObject(TransformSignParam.class, filter);
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected List<TransformSign> listTransformSign(Map<String, Object> filter, boolean isContainDetail) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (filter == null)
		{
			filter = new HashMap<String, Object>();
		}

		try
		{
			List<TransformSign> query = sds.query(TransformSign.class, filter);

			if (!SetUtils.isNullList(query) && isContainDetail)
			{
				for (TransformSign sign : query)
				{

					filter.clear();
					filter.put(TransformSignParam.SIGN_GUID, sign.getGuid());
					List<TransformSignParam> paramList = this.listTransformSignParam(filter);
					sign.setParamList(paramList);

					if (!SetUtils.isNullList(paramList))
					{
						for (TransformSignParam signParam : paramList)
						{
							filter.clear();
							filter.put(TransformSignWFMap.SIGN_PARAM_GUID, signParam.getGuid());
							List<TransformSignObjectMap> objectMapList = this.listTransformSignObjectMap(filter);
							signParam.setObjectMapList(objectMapList);

							List<TransformSignWFMap> wfMapList = this.listTransformSignWFMap(filter);
							signParam.setWfMapList(wfMapList);

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

	protected void deleteTransformSign(String configGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (configGuid == null)
			{
				return;
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, configGuid);
			sds.delete(TransformSign.class, configGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected TransformSignParam saveSignParam(TransformSignParam signParam) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(signParam.getGuid()))
			{
				signParam.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			signParam.setUpdateUserGuid(this.stubService.getOperatorGuid());
			String save = sds.save(signParam);
			if (StringUtils.isGuid(save))
			{
				return this.getTransformSignParam(save);
			}
			return signParam;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformSignParam> listTransformSignParam(Map<String, Object> filter) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (filter == null)
		{
			filter = new HashMap<String, Object>();
		}

		try
		{
			List<TransformSignParam> query = sds.query(TransformSignParam.class, filter);
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteTransformSigParam(String configGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (configGuid == null)
			{
				return;
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, configGuid);
			sds.delete(TransformSignParam.class, configGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveSignObjectMap(TransformSignObjectMap signObjectMap) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(signObjectMap.getGuid()))
			{
				signObjectMap.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			signObjectMap.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(signObjectMap);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformSignObjectMap> listTransformSignObjectMap(Map<String, Object> filter) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (filter == null)
		{
			filter = new HashMap<String, Object>();
		}

		try
		{
			List<TransformSignObjectMap> query = sds.query(TransformSignObjectMap.class, filter);
			return query;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteTransformSigObjectMap(String configGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (configGuid == null)
			{
				return;
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, configGuid);
			sds.delete(TransformSignObjectMap.class, configGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveSignWFMap(TransformSignWFMap signWFMap) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (!StringUtils.isGuid(signWFMap.getGuid()))
			{
				signWFMap.setCreateUserGuid(this.stubService.getOperatorGuid());
			}
			signWFMap.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(signWFMap);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformSignWFMap> listTransformSignWFMap(Map<String, Object> filter) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (filter == null)
		{
			filter = new HashMap<>();
		}

		try
		{
			List<TransformSignWFMap> query = sds.query(TransformSignWFMap.class, filter);

			if (!SetUtils.isNullList(query))
			{
				for (TransformSignWFMap config : query)
				{
					if (!StringUtils.isNullString(config.getWorkflowName()))
					{
						WorkflowProcessInfo workflowProcess = this.stubService.getWFM().getProcessModelInfo(config.getWorkflowName());
						if (workflowProcess != null)
						{
							String title = workflowProcess.getDescription();
							config.put(TransformSignWFMap.WORKFLOW_TITLE, title);
						}

					}

					if (!StringUtils.isNullString(config.getActivityName()))
					{
						WorkflowActivityInfo workflowActivity = this.stubService.getWFM().getWorkflowActivityInfoByName(config.getWorkflowName(), config.getActivityName());

						if (workflowActivity != null)
						{
							String title = workflowActivity.getTitle(this.stubService.getUserSignature().getLanguageEnum());
							config.put(TransformSignWFMap.ACTIVITY_TITLE, title);
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

	protected void deleteTransformSignWFMap(String configGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (configGuid == null)
			{
				return;
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, configGuid);
			sds.delete(TransformSignWFMap.class, configGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<TransformSignParam> listTransformSignParam(String queueGuid) throws ServiceRequestException
	{
		TransformQueue queue = this.stubService.getTransformStub().getQueue(queueGuid);
		if (queue != null)
		{
			ObjectGuid o = new ObjectGuid(queue.getTransformInstanceClassGuid(), null, queue.getTransformInstanceGuid(), null);
			String proguid = (String) queue.get("PROCRTGUID");
			return this.listTransformSignParam(queue.getSignGuid(), o, proguid);
		}

		return null;
	}

	private List<TransformSignParam> listTransformSignParam(String signGuid, ObjectGuid objectGuid, String proguid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(TransformSignParam.SIGN_GUID, signGuid);
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		List<TransformSignParam> transformSignParamList = this.listTransformSignParam(filter);
		if (!SetUtils.isNullList(transformSignParamList))
		{
			for (TransformSignParam param : transformSignParamList)
			{
				param.setValue(null);
				param.setDateValue(null);
				param.put("FIELDTYPE", "");
				if (TransParamFromType.OBJECT.name().equalsIgnoreCase(param.getSource()))
				{
					filter.clear();
					filter.put(TransformSignWFMap.SIGN_PARAM_GUID, param.getGuid());
					List<TransformSignObjectMap> listTransformSignObjectMap = this.listTransformSignObjectMap(filter);
					if (!SetUtils.isNullList(listTransformSignObjectMap))
					{

						for (TransformSignObjectMap objectMap : listTransformSignObjectMap)
						{

							if (objectGuid.getClassName().equalsIgnoreCase(objectMap.getClassName()))
							{
								ClassField classField = this.stubService.getEMM().getFieldByName(objectGuid.getClassName(), objectMap.getFieldName(), true);
								if (classField != null && classField.getType() != null)
								{
									param.put("FIELDTYPE", classField.getType().toString());
								}

								FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);
								if (object != null)
								{
									if ("OWNERUSER$".equals(objectMap.getFieldName()) //
											|| "UPDATEUSER$".equals(objectMap.getFieldName()))
									{
										if (object.get(objectMap.getFieldName()) != null)
										{
											User user = this.stubService.getAAS().getUser((String) object.get(objectMap.getFieldName()));
											if (user != null)
											{
												param.setValue(user.getUserName());
											}
										}
									}
									else if (classField != null && classField.getType() == FieldTypeEnum.OBJECT)
									{
										ClassInfo typeValueObject = this.stubService.getEMM().getClassByName(classField.getTypeValue());
										if (typeValueObject != null && typeValueObject.hasInterface(ModelInterfaceEnum.IUser))
										{
											User user = this.stubService.getAAS().getUser((String) object.get(objectMap.getFieldName()));
											if (user != null)
											{
												param.setValue(user.getUserName());
											}
										}
									}
									else
									{
										param.setValue(object.get(objectMap.getFieldName()));
									}
									break;
								}
							}
						}
					}

				}
				else if (TransParamFromType.WF.name().equalsIgnoreCase(param.getSource()))
				{
					filter.clear();
					filter.put(TransformSignWFMap.SIGN_PARAM_GUID, param.getGuid());
					List<TransformSignWFMap> listTransformSignWFMap = this.listTransformSignWFMap(filter);
					if (!SetUtils.isNullList(listTransformSignWFMap))
					{
						ProcessRuntime pcrt = null;
						/*
						 * List<ProcessRuntime> wfList =
						 * this.stubService.getWFE().listProcessRuntimeOfObject(objectGuid, null);
						 * if (!SetUtils.isNullList(wfList))
						 * {
						 * pcrt = wfList.get(0);
						 * }
						 */
						if (StringUtils.isGuid(proguid))
						{
							ProcessRuntime processRuntime = this.stubService.getWFI().getProcessRuntime(proguid);
							pcrt = processRuntime;
						}
						else
						{
							List<ProcessRuntime> wfList = this.stubService.getWFI().listProcessRuntimeOfObject(objectGuid, null);
							if (!SetUtils.isNullList(wfList))
							{
								for (ProcessRuntime pcrt_ : wfList)
								{
									if (pcrt_.getStatus() == ProcessStatusEnum.ONHOLD || pcrt_.getStatus() == ProcessStatusEnum.OBSOLETE
											|| pcrt_.getStatus() == ProcessStatusEnum.CANCEL)
									{
										continue;
									}
									pcrt = pcrt_;
									break;
								}
							}
						}

						if (pcrt == null)
						{
							continue;
						}

						if (pcrt.getStatus() == ProcessStatusEnum.ONHOLD || pcrt.getStatus() == ProcessStatusEnum.OBSOLETE || pcrt.getStatus() == ProcessStatusEnum.CANCEL)
						{
							continue;
						}

						List<ActivityRuntime> activityRuntimeList = this.stubService.getWFI().listHistoryActivityRuntimeAndPerformer(pcrt.getGuid());
						Map<String, ActivityRuntime> activityNameMap = new HashMap<String, ActivityRuntime>();
						if (!SetUtils.isNullList(activityRuntimeList))
						{
							for (ActivityRuntime acitvity : activityRuntimeList)
							{
								activityNameMap.put(acitvity.getName(), acitvity);
							}

						}

						for (TransformSignWFMap wfMap : listTransformSignWFMap)
						{
							if (wfMap.getWorkflowName().equalsIgnoreCase(pcrt.getName()) && activityNameMap.containsKey(wfMap.getActivityName()))
							{

								ActivityRuntime activityRuntime = activityNameMap.get(wfMap.getActivityName());
								if (activityRuntime == null)
								{
									continue;
								}
								// List<Performer> listPerformer =
								// this.stubService.getWFE().listPerformer(activityRuntime.getGuid());

								String startNumber = "0";
								if (activityRuntime.getStartNumber() != null)
								{
									startNumber = (activityRuntime.getStartNumber().intValue()) + "";
								}
								List<ProcTrack> listActivityComment = this.stubService.getWFI().listActivityComment(activityRuntime.getGuid(), startNumber);
								StringBuffer sb = new StringBuffer();
								if (!SetUtils.isNullList(listActivityComment))
								{
									int i = 0;
									for (ProcTrack p : listActivityComment)
									{
										if (p.getDecide() != DecisionEnum.SKIP)
										{
											if (i != 0)
											{
												sb.append(",");
											}
											sb.append(p.getPerformerName());
											i++;
										}
									}
								}
								param.setValue(sb);
								if (activityRuntime.getDecide() != DecisionEnum.SKIP && !StringUtils.isNullString(param.getDateName()))
								{
									param.setDateValue(activityRuntime.getFinishTime());
								}
								break;
							}
						}
					}

				}

			}

		}
		return transformSignParamList;
	}

	protected List<TransformSign> saveTransSign(List<TransformSign> signList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(signList))
		{
			return null;
		}
		List<TransformSign> result = new ArrayList<TransformSign>();
		String configGuid = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			for (TransformSign config : signList)
			{

				configGuid = config.getGuid();
				if (config.isDelete() && StringUtils.isGuid(config.getGuid()))
				{
					this.deleteTransformSign(config.getGuid());
				}
				else if (!StringUtils.isGuid(config.getGuid()))
				{
					// oriGuid = config.getGuid();
					config.setGuid(null);
					c.add(Calendar.SECOND, 1);
					config.put("CREATETIME", c.getTime());
					TransformSign saveTransformConfig = this.saveTransSign(config);
					configGuid = saveTransformConfig.getGuid();
					// guids.put(oriGuid, newConfig.getGuid());
					// result.add(newConfig);

				}
				else if (config.isChanged())
				{
					this.saveTransSign(config);

					// result.add(newConfig);
				}

				if (!SetUtils.isNullList(config.getParamList()))
				{
					for (TransformSignParam param : config.getParamList())
					{

						String paramGuid = param.getGuid();
						if (param.isDelete() && StringUtils.isGuid(param.getGuid()))
						{
							this.deleteTransformSigParam(param.getGuid());
						}
						else if (!StringUtils.isGuid(param.getGuid()))
						{
							param.setSignGuid(configGuid);
							param.setGuid(null);
							c.add(Calendar.SECOND, 1);
							param.put("CREATETIME", c.getTime());
							// param.put("CREATETIME", DateFormat.SDFYMDHMS.format(c.getTime()));
							TransformSignParam saveSignParam = this.saveSignParam(param);
							paramGuid = saveSignParam.getGuid();
						}
						else if (param.isChanged())
						{
							param.setSignGuid(configGuid);

							TransformSignParam saveSignParam = this.saveSignParam(param);
							paramGuid = saveSignParam.getGuid();
						}

						if (!SetUtils.isNullList(param.getObjectMapList()))
						{
							for (TransformSignObjectMap map : param.getObjectMapList())
							{
								if (StringUtils.isNullString(map.getClassGuid()))
								{
									continue;
								}

								if (map.isDelete() && StringUtils.isGuid(map.getGuid()))
								{
									this.deleteTransformSigObjectMap(map.getGuid());
								}
								else if (!StringUtils.isGuid(map.getGuid()))
								{
									map.setGuid(null);
									c.add(Calendar.SECOND, 1);
									map.put("CREATETIME", c.getTime());

									map.setSignParamGuid(paramGuid);
									this.saveSignObjectMap(map);
								}
								else if (map.isChanged())
								{
									map.setSignParamGuid(paramGuid);
									this.saveSignObjectMap(map);
								}
							}
						}

						if (!SetUtils.isNullList(param.getWfMapList()))
						{
							for (TransformSignWFMap map : param.getWfMapList())
							{
								if (StringUtils.isNullString(map.getWorkflowName()))
								{
									continue;
								}

								if (map.isDelete() && StringUtils.isGuid(map.getGuid()))
								{
									this.deleteTransformSignWFMap(map.getGuid());
								}
								else if (!StringUtils.isGuid(map.getGuid()))
								{
									map.setGuid(null);
									c.add(Calendar.SECOND, 1);
									map.put("CREATETIME", c.getTime());
									map.setSignParamGuid(paramGuid);
									this.saveSignWFMap(map);
								}
								else if (map.isChanged())
								{
									map.setSignParamGuid(paramGuid);
									this.saveSignWFMap(map);
								}
							}
						}

					}
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
}
