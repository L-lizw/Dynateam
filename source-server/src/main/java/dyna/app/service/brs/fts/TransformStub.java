/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与文件夹相关的操作分支
 * Caogc 2010-8-31
 */
package dyna.app.service.brs.fts;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.trans.*;
import dyna.common.bean.serv.DSStorage;
import dyna.common.conf.ConfigurableServerImpl;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.DSSFileTrans.FileTransTypeEnum;
import dyna.common.dto.FileType;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.Performer;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.systemenum.ppms.CodeNameEnum;
import dyna.common.systemenum.trans.TransStorageType;
import dyna.common.util.DateFormat;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.DSS;
import dyna.net.service.brs.FTS;
import org.acegisecurity.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * 与转换相关的操作分支
 * 
 * @author Wanglh
 * 
 */
public class TransformStub extends AbstractServiceStub<FTSImpl>
{
	private static long checkTime = 30 * 60 * 1000;

	/**
	 * @param context
	 * @param service
	 */
	protected TransformStub(ServiceContext context, FTSImpl service)
	{
		super(context, service);
	}

	protected void createTransformQueue4WF(ActivityRuntime activity) throws ServiceRequestException
	{
		ProcessRuntime processRuntime = this.stubService.getWFI().getProcessRuntime(activity.getProcessRuntimeGuid());
		if (processRuntime == null)
		{
			return;
		}

		List<TransformWFConfig> listWFConfig = this.stubService.getTransformConfigStub().listWFConfig(processRuntime.getName(), activity.getName());
		if (SetUtils.isNullList(listWFConfig))
		{
			return;
		}

		List<ProcAttach> listProcAttach = this.stubService.getWFI().listProcAttach(activity.getProcessRuntimeGuid());
		if (!SetUtils.isNullList(listProcAttach))
		{
			for (ProcAttach attach : listProcAttach)
			{
				ObjectGuid objectGuid = new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);

				if (objectGuid.getClassGuid() == null)
				{
					ClassStub.decorateObjectGuid(objectGuid, this.stubService);
				}

				List<TransformConfig> transformConfigList = this.stubService.getTransformConfigStub().listTransformConfig4WF(objectGuid, processRuntime.getName(),
						activity.getName());
				if (SetUtils.isNullList(transformConfigList))
				{
					continue;
				}

				this.createTransformQueue(objectGuid, transformConfigList, null, processRuntime.getCreateUserGuid(), false, processRuntime.getGuid(), activity.getGuid());

			}
		}
	}

	protected void createTransformQueue4Manual(ObjectGuid objectGuid, TransformConfig config) throws ServiceRequestException
	{
		if (objectGuid.getClassGuid() == null)
		{
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		}

		TransformConfig transformConfig = this.stubService.getTransformConfigStub().getTransformConfig(config.getGuid());
		if (transformConfig == null)
		{
			return;
		}

		FoundationObject foundation = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		TransformManualConfig manualConfig = this.stubService.getManualConfig(transformConfig.getGuid());
		transformConfig.setManualConfig(manualConfig);

		boolean checkCreate4Manual = this.stubService.getTransformConfigStub().checkCreate4Manual(transformConfig, foundation);
		if (!checkCreate4Manual)
		{
			return;
		}
		List<TransformConfig> transformConfigList = new ArrayList<TransformConfig>();
		// transformConfig.setSignGuid(config.getSignGuid());
		transformConfigList.add(transformConfig);

		this.createTransformQueue(objectGuid, transformConfigList, null, null, true, null, null);
	}

	protected void createTransformQueue4Checkin(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid.getClassGuid() == null)
		{
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		}

		List<TransformConfig> transformConfigList = this.stubService.getTransformConfigStub().listTransformConfig4CheckIn(objectGuid);
		if (SetUtils.isNullList(transformConfigList))
		{
			return;
		}

		this.createTransformQueue(objectGuid, transformConfigList, null, null, false, null, null);
	}

	protected void createTransformQueue4Upload(ObjectGuid objectGuid, String fileGuid) throws ServiceRequestException
	{
		if (objectGuid.getClassGuid() == null)
		{
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		}

		List<TransformConfig> transformConfigList = this.stubService.getTransformConfigStub().listTransformConfig4Uploaded(objectGuid);
		if (SetUtils.isNullList(transformConfigList))
		{
			return;
		}

		DSSFileInfo file = this.stubService.getDSS().getFile(fileGuid);
		if (file == null)
		{
			return;
		}

		List<DSSFileInfo> fileList = new ArrayList<DSSFileInfo>();
		fileList.add(file);
		this.createTransformQueue(objectGuid, transformConfigList, fileList, null, false, null, null);
	}

	private boolean needTransfer(DSSFileInfo info, TransformConfig config) throws ServiceRequestException
	{

		if (info != null && config != null)
		{
			String name = info.getName();

			if (StringUtils.isNullString(name))
			{
				return false;
			}
			name = name.toUpperCase();
			String transformType = config.getTransformType();
			if (StringUtils.isNullString(transformType))
			{
				return false;
			}

			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(transformType);
			if (codeItem == null)
			{
				return false;
			}

			String description = codeItem.getDescription();

			String[] splitStringWithDelimiter = StringUtils.splitStringWithDelimiter(";", description);
			if (splitStringWithDelimiter != null)
			{
				for (String str : splitStringWithDelimiter)
				{
					if (str != null && name.toUpperCase().endsWith("." + str.toUpperCase()))
					{
						return true;
					}
				}
			}

		}

		return false;
	}

	private boolean resultHasExsit(ObjectGuid objectGuid, DSSFileInfo info, TransformConfig config, List<DSSFileInfo> listFile, boolean isUpload) throws ServiceRequestException
	{
		String name = info.getName();
		int lastIndexOf = name.lastIndexOf(".");
		name = name.substring(0, lastIndexOf);
		List<DSSFileInfo> allFile = listFile;
		BOAS boas = null;
		DSS dss = null;
		try
		{
			if (isUpload)
			{
				boas = this.serviceContext.allocatService(BOAS.class);
				((BOASImpl) boas).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());

				dss = this.serviceContext.allocatService(DSS.class);
				((DSSImpl) dss).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());

			}
			else
			{
				boas = this.stubService.getBOAS();
				dss = this.stubService.getDSS();
			}
			// if ("PDFSIG".equalsIgnoreCase((String) config.get(TransformConfig.TRANSFORM_TYPE + "NAME")))
			// { // 签注
			//
			// }
			// else
			if (config.getResultTypeEnum() == TransStorageType.OBJECT)
			{
				// AAS boas = this.stubService.getServiceInstance(AAS.class,
				// this.serviceContext.getServerContext().getSystemInternalSignature());

				// ((BOASImpl) boas).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
				// ((DSSImpl) dss).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());

				List<StructureObject> listObjectOfRelation = boas.listObjectOfRelation(objectGuid, BuiltinRelationNameEnum.TRANSFROM.toString(), null, null, null);

				if (!SetUtils.isNullList(listObjectOfRelation))
				{
					allFile = dss.listFile(listObjectOfRelation.get(0).getEnd2ObjectGuid(), null);
				}
				else
				{
					allFile = null;
				}
			}
			else if (config.getResultTypeEnum() == TransStorageType.PREVIEW)
			{
				allFile = null;
				FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(objectGuid, false);
				DSSFileInfo fileInfoByFileType = ((DSSImpl) dss).getTransFileStub().getFileInfoByFileType(objectGuid, object.getIterationId(), FileType.PREVIEW_FILE_TYPE);
				// dss.downloadPreviewFile(objectGuid, iterationId)
				if (fileInfoByFileType != null)
				{
					// listFile.add(fileInfoByFileType);
					return true;
				}
			}

			if (!SetUtils.isNullList(allFile))
			{
				for (DSSFileInfo temInfo : allFile)
				{
					if (temInfo.getGuid().equalsIgnoreCase(info.getGuid()))
					{
						continue;
					}
					if (temInfo.getName() == null)
					{
						continue;
					}

					lastIndexOf = temInfo.getName().lastIndexOf(".");
					if (lastIndexOf != -1 && name != null && name.equalsIgnoreCase(temInfo.getName().substring(0, lastIndexOf)))
					{
						return true;
					}
				}
			}
		}
		catch (ServiceNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (isUpload)
			{
				if (dss != null)
				{
					this.serviceContext.releaseService(dss);
				}

				if (boas != null)
				{
					this.serviceContext.releaseService(boas);
				}
			}
		}

		return false;

	}

	private void createTransformQueue(ObjectGuid objectGuid, List<TransformConfig> transformConfigList, List<DSSFileInfo> listFile, String createUserGuid, boolean isThrowException,
			String procGuid, String actrtGuid) throws ServiceRequestException
	{
		// String msg = "";
		int i = 0;
		if (objectGuid.getClassGuid() == null)
		{
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		}
		String dssID = null;
		List<DSSFileInfo> allFile = null;
		List<DSSFileInfo> signAllFile = null;
		boolean isUpload = (listFile != null);
		if (listFile == null)
		{
			allFile = this.stubService.getDSS().listFile(objectGuid, null);
		}
		else
		{
			allFile = ((DSSImpl) (this.stubService.getDSS())).getInstFileStub().listFile(objectGuid, listFile.get(0).getIterationId(), null, false, false);
		}

		// for (DSSFileInfo info : allFile)
		// {
		// if (info.isPrimary())
		// {
		// ConfigurableServerImpl serverConfig = this.serviceContext.getServerContext().getServerConfig();
		// DSStorage dsStorage = serverConfig.getDSStorage(info.getStorageId());
		// dssID = dsStorage.getDsserverId();
		// break;
		// }
		// }

		if (listFile == null)
		{ // upload
			listFile = allFile;

			// if (SetUtils.isNullList(listFile))
			// {
			// return;
			// }
		}

		boolean fileExisted = false;
		// if (!SetUtils.isNullList(listFile))
		// {

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> s = new ArrayList<String>();
		s.add(JobStatus.RUNNING.getValue() + "");
		s.add(JobStatus.WAITING.getValue() + "");
		s.add(JobStatus.SUCCESSFUL.getValue() + "");

		Map<String, Date> masterDateMap = new HashMap<String, Date>();
		Map<String, String> masterFkMap = new HashMap<String, String>();
		List<DSSFileInfo> transformFiles = new ArrayList<DSSFileInfo>();
		for (TransformConfig config : transformConfigList)
		{
			transformFiles.clear();
			transformFiles.addAll(listFile);

			if (!isUpload && "PDFSIG".equalsIgnoreCase((String) config.get(TransformConfig.TRANSFORM_TYPE + "NAME")))
			{ // 签注
				if (signAllFile == null)
				{
					List<StructureObject> listObjectOfRelation = this.stubService.getBOAS().listObjectOfRelation(objectGuid, BuiltinRelationNameEnum.TRANSFROM.toString(), null,
							null, null);

					if (!SetUtils.isNullList(listObjectOfRelation))
					{
						signAllFile = ((DSSImpl) (this.stubService.getDSS())).getInstFileStub().listFile(listObjectOfRelation.get(0).getEnd2ObjectGuid(),
								listObjectOfRelation.get(0).getIterationId(), null, false, false);
					}

					if (signAllFile == null)
					{
						signAllFile = new ArrayList<DSSFileInfo>();
					}

				}

				transformFiles.addAll(signAllFile);

			}

			// 得到主文件dssid
			if (dssID == null)
			{
				for (DSSFileInfo info : allFile)
				{
					if (info.isPrimary())
					{
						ConfigurableServerImpl serverConfig = this.serviceContext.getServerContext().getServerConfig();
						DSStorage dsStorage = serverConfig.getDSStorage(info.getStorageId());
						dssID = dsStorage.getDsserverId();
						break;
					}
				}
			}

			if (dssID == null)
			{
				for (DSSFileInfo info : transformFiles)
				{
					if (info.isPrimary())
					{
						ConfigurableServerImpl serverConfig = this.serviceContext.getServerContext().getServerConfig();
						DSStorage dsStorage = serverConfig.getDSStorage(info.getStorageId());
						dssID = dsStorage.getDsserverId();
						break;
					}
				}
			}

			for (DSSFileInfo info : transformFiles)
			{

				if (!masterDateMap.containsKey(info.getGuid()))
				{
					masterDateMap.put(info.getGuid(), new Date());
				}
				Date date = masterDateMap.get(info.getGuid());

				if (!masterFkMap.containsKey(info.getGuid()))
				{
					masterFkMap.put(info.getGuid(), UUID.randomUUID().toString().replace("-", "").toUpperCase());
				}
				String masterFK = masterFkMap.get(info.getGuid());

				if (!this.needTransfer(info, config))
				{
					continue;
				}

				fileExisted = true;

				// 是否有签名
				boolean signHasSign = false;
				if (StringUtils.isGuid(config.getSignGuid()))
				{
					TransformSign transformSign = this.stubService.getTransformSignStub().getTransformSign(config.getSignGuid());
					if (transformSign != null)
					{
						signHasSign = true;
					}
				}

				// 是否存在队列
				condition.clear();
				condition.put(TransformQueue.FILE_NAME, info.getName());
				if (!StringUtils.isNullString(info.getMD5()))
				{
					condition.put(TransformQueue.FILE_MD5, info.getMD5());
				}
				condition.put(TransformQueue.TRANSFORM_CONFIG_GUID, config.getGuid());
				condition.put(TransformQueue.TRANSFORM_INSTANCE_GUID, objectGuid.getGuid());

				condition.put("LISTJOBSTATUS", s);

				List<TransformQueue> listQueue = this.listQueue(condition, false);

				boolean queueHasExist = false;
				if (!SetUtils.isNullList(listQueue))
				{
					queueHasExist = true;
				}

				// 是否存在结果
				boolean resultHasExsit = false;
				if (!("PDFSIG".equalsIgnoreCase((String) config.get(TransformConfig.TRANSFORM_TYPE + "NAME")) || config.getResultTypeEnum() == TransStorageType.PREVIEW))
				{
					resultHasExsit = this.resultHasExsit(objectGuid, info, config, allFile, isUpload);
				}

				// 是否生成队列
				if (!(signHasSign || !queueHasExist || !resultHasExsit))
				{

					continue;
				}

				// 是否需要转换
				boolean needTransform = !(queueHasExist && resultHasExsit);

				if ("PDFSIG".equalsIgnoreCase((String) config.get(TransformConfig.TRANSFORM_TYPE + "NAME")))
				{
					needTransform = true;
				}

				TransformQueue queue = new TransformQueue();
				queue.setTransformInstanceGuid(objectGuid.getGuid());
				queue.setTransformInstanceClassGuid(objectGuid.getClassGuid());
				if (!(this.stubService.getSignature() instanceof UserSignature))
				{
					queue.setCreateUserGuid(info.getUpdateUserGuid());
					queue.setUpdateUserGuid(info.getUpdateUserGuid());
					queue.setOwneruserGuid(info.getUpdateUserGuid());
				}
				else
				{
					if (!StringUtils.isGuid(createUserGuid))
					{
						queue.setCreateUserGuid(this.stubService.getOperatorGuid());
						queue.setUpdateUserGuid(this.stubService.getOperatorGuid());
						queue.setOwneruserGuid(this.stubService.getOperatorGuid());
					}
					else
					{
						queue.setCreateUserGuid(createUserGuid);
						queue.setUpdateUserGuid(createUserGuid);
						queue.setOwneruserGuid(createUserGuid);
					}
				}

				queue.setDssID(dssID);
				queue.setTransformConfigGuid(config.getGuid());
				queue.setTransformType(config.getTransformType());
				queue.setMasterCreateTime(date);
				queue.setFileGuid(info.getGuid());
				queue.setFileType(info.getFileType());
				queue.setFileName(info.getName());
				queue.setFileMD5(info.getMD5());
				queue.setMasterFK(masterFK);
				queue.setSignGuid(config.getSignGuid());
				queue.setNeedTransform(needTransform);

				queue.put("ACTRTGUID", actrtGuid);
				queue.put("PROCRTGUID", procGuid);

				try
				{
					sds.save(queue);
					i++;
				}
				catch (DynaDataException e)
				{
					throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
				}
			}

			// }
		}

		if (!fileExisted && isThrowException)
		{
			throw new ServiceRequestException("ID_APP_TRANSFORM_QUEUE_FILE_NOTEXIST", "file not exist");
		}
		else if (isThrowException && i == 0)
		{
			throw new ServiceRequestException("ID_APP_TRANSFORM_QUEUE_NOT_CREATE", "queue not create");
		}

	}

	/**
	 * 更新任务队列
	 * 
	 * @param childGuid
	 * @param fileMD5
	 */
	protected TransformQueue updateQueueInfo(String queueGuid, String status, String result) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		TransformQueue queue = new TransformQueue();
		queue.setGuid(queueGuid);
		queue.setJobStatus(status);
		queue.setResult(result);
		queue.setUpdateUserGuid(this.stubService.getOperatorGuid());
		try
		{
			sds.save(queue, "update");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		TransformQueue queue2 = this.getQueue(queueGuid);

		if (!((JobStatus.SUCCESSFUL.getValue() + "").equalsIgnoreCase(status) || (JobStatus.FAILED.getValue() + "").equalsIgnoreCase(status)))
		{
			return queue2;
		}

		if (queue2 != null && queue2.get("PROCRTGUID") != null)
		{
			SystemDataService systemDataService = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			String procrtGuid = (String) queue2.get("PROCRTGUID");
			filter.put("PROCRTGUID", procrtGuid);
			TransformQueue queryObject = systemDataService.queryObject(TransformQueue.class, filter, "selectNotComplete");
			Number b = (Number) queryObject.get("NUM");
			if (queryObject != null && b != null && b.intValue() == 0)
			{

				if ((JobStatus.SUCCESSFUL.getValue() + "").equalsIgnoreCase(status) || (JobStatus.CANCEL.getValue() + "").equalsIgnoreCase(status))
				{

					List<ActivityRuntime> listCurrentActivityRuntime = this.stubService.getWFI().listCurrentActivityRuntime(procrtGuid);
					if (!SetUtils.isNullList(listCurrentActivityRuntime))
					{
						for (ActivityRuntime activity : listCurrentActivityRuntime)
						{
							List<User> listPerFormer = this.stubService.getWFI().listNotFinishPerformer(activity.getGuid());
							if (!SetUtils.isNullList(listPerFormer))
							{
								for (User user : listPerFormer)
								{
									if (this.stubService.getUserSignature().getUserGuid().equalsIgnoreCase(user.getGuid()))
									{
										this.stubService.getWFI().performActivityRuntime(activity.getGuid(), null, DecisionEnum.ACCEPT, null, null);
										break;
									}
								}
							}
						}
					}

				}
			}

			queryObject = systemDataService.queryObject(TransformQueue.class, filter, "selectHasTrans");
			b = (Number) queryObject.get("NUM");
			if (queryObject != null && b != null && b.intValue() == 0)
			{
				// 通知
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("PROCRTGUID", queue2.get("PROCRTGUID"));
				List<TransformQueue> listQueue = this.listQueue(condition, true);

				List<TransformQueue> errorList = new ArrayList<TransformQueue>();
				if (!SetUtils.isNullList(listQueue))
				{
					for (TransformQueue q : listQueue)
					{
						if (q.getJobStatus() == JobStatus.FAILED)
						{
							errorList.add(q);
						}
					}
				}

				String actrtGuid = (String) queue2.get("ACTRTGUID");
				List<String> toUserIdList = new ArrayList<String>();
				toUserIdList.add("admin");
				toUserIdList.add(queue2.getCreateUserGuid());
				String actUser = null;
				if (procrtGuid != null)
				{
					List<Performer> listPerformer = this.stubService.getWFI().listPerformer(actrtGuid);
					if (!SetUtils.isNullList(listPerformer))
					{
						for (Performer per : listPerformer)
						{
							actUser = per.getPerformerGuid();
						}
					}
				}

				if (actUser != null)
				{
					toUserIdList.add(actUser);
				}

				String subject = null;
				String content = null;
				List<ObjectGuid> objectGuidList = new ArrayList<ObjectGuid>();
				if (errorList.size() > 0)
				{

					subject = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_TITLE_ERROR", this.stubService.getUserSignature().getLanguageEnum().getId());
					content = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_ERROR_CONTENT", this.stubService.getUserSignature().getLanguageEnum().getId());
					content = MessageFormat.format(content, listQueue.size());

					for (TransformQueue q : errorList)
					{
						String content1 = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_ERROR_CONTENT_LIST",
								this.stubService.getUserSignature().getLanguageEnum().getId());
						ObjectGuid objectGuid = new ObjectGuid(q.getTransformInstanceClassGuid(), null, q.getTransformInstanceGuid(), null);
						content1 = MessageFormat.format(content1, q.get("FULLNAME"), queue2.getFileName(), q.get("TRANSFORMTYPE$TITLE"));
						objectGuidList.add(objectGuid);
						content = content + "\r\n" + content1;
					}

					this.stubService.getSMS().sendMailToUsers(subject, content, MailCategoryEnum.ERROR, objectGuidList, toUserIdList, MailMessageType.JOBNOTIFY);

				}
				else
				{
					subject = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_TITLE_SUCCESSFUL", this.stubService.getUserSignature().getLanguageEnum().getId());
					content = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_SUCCESSFUL_CONTENT", this.stubService.getUserSignature().getLanguageEnum().getId());
					content = MessageFormat.format(content, listQueue.size());
					this.stubService.getSMS().sendMailToUsers(subject, content, MailCategoryEnum.INFO, objectGuidList, toUserIdList, MailMessageType.JOBNOTIFY);

				}
			}
		}

		return queue2;
	}

	protected void reStartQueue(TransformQueue queue) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		TransformQueue queue1 = new TransformQueue();
		queue1.setGuid(queue.getGuid());
		queue1.setJobStatus(JobStatus.WAITING.getValue() + "");
		queue1.setResult(null);
		queue1.setUpdateUserGuid(this.stubService.getOperatorGuid());
		// 重启需清空SERVERID
		queue1.put("SERVERID", null);

		// 复制
		// queue.setGuid(null);
		// queue.setJobStatus(JobStatus.WAITING.getValue() + "");
		// queue.setResult(null);
		// queue.setUpdateUserGuid(this.stubService.getOperatorGuid());
		// queue.setCreateUserGuid(this.stubService.getOperatorGuid());
		// queue.setOwneruserGuid(this.stubService.getOperatorGuid());
		try
		{
			sds.save(queue1);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		// return this.getQueue(queue.getGuid());
	}

	protected void deleteQueue(TransformQueue queue) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.delete(TransformQueue.class, queue.getGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		// return this.getQueue(queue.getGuid());
	}

	protected TransformQueue getQueue(String queueGuid) throws ServiceRequestException
	{
		TransformQueue queue = null;

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put("GUID", queueGuid);
			// searchConditionMap.put(TransformQueue.CURRENTUSERGUID,
			// this.stubService.getUserSignature().getUserGuid());
			queue = sds.queryObject(TransformQueue.class, searchConditionMap);

			if (queue != null && StringUtils.isGuid(queue.getTransformType()))
			{

				CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(queue.getTransformType());
				if (codeItem != null)
				{
					queue.put(TransformConfig.TRANSFORM_TYPE + "NAME", codeItem.getCode());
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return queue;
	}

	protected List<TransformQueue> listQueue4Trans(Map<String, Object> condition) throws ServiceRequestException
	{
		List<TransformQueue> results = null;
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{

			// filter.put("ROWNUM", 1);
			// filter.put("SERVERID", sessionModel.getRemoveHost());
			// filter.put("DSSID", sessionModel.getDssID());

			sds.update(TransformQueue.class, condition, "updateList");

			List<String> s = new ArrayList<String>();
			// s.add(JobStatus.RUNNING.getValue() + "");
			s.add(JobStatus.WAITING.getValue() + "");

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("LISTJOBSTATUS", s);
			filter.put("SERVERID", condition.get("SERVERID"));
			if (!StringUtils.isNullString((String) condition.get("DSSID")))
			{
				filter.put("DSSID", condition.get("DSSID"));
			}

			results = sds.query(TransformQueue.class, filter);
			if (!SetUtils.isNullList(results))
			{
				for (TransformQueue queue : results)
				{
					if (!StringUtils.isGuid(queue.getTransformType()))
					{
						continue;
					}

					CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(queue.getTransformType());
					if (codeItem != null)
					{
						queue.put(TransformConfig.TRANSFORM_TYPE + "NAME", codeItem.getCode());
					}
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return results;
	}

	protected int listQueueCount(Map<String, Object> condition) throws ServiceRequestException
	{
		List<TransformQueue> results = null;
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (condition.containsKey("CREATEFROMTIME"))
			{
				condition.put("CREATEFROMTIME", DateFormat.getDateOfBegin(condition.get("CREATEFROMTIME"), DateFormat.PTN_YMDHMS));
			}
			if (condition.containsKey("CREATETOTIME"))
			{
				condition.put("CREATETOTIME", DateFormat.getDateOfEnd(condition.get("CREATETOTIME"), DateFormat.PTN_YMDHMS));
			}
			if (condition.containsKey("UPDATEFROMTIME"))
			{
				condition.put("UPDATEFROMTIME", DateFormat.getDateOfBegin(condition.get("UPDATEFROMTIME"), DateFormat.PTN_YMDHMS));
			}
			if (condition.containsKey("UPDATETOTIME"))
			{
				condition.put("UPDATETOTIME", DateFormat.getDateOfEnd(condition.get("UPDATETOTIME"), DateFormat.PTN_YMDHMS));
			}
			// condition.put(TransformQueue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return SetUtils.isNullList(results) ? 0 : results.get(0).getRowCount();
	}

	protected List<TransformQueue> listQueue(Map<String, Object> condition, boolean isFullName) throws ServiceRequestException
	{
		List<TransformQueue> results = null;
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			if (condition.containsKey("CREATEFROMTIME"))
			{
				condition.put("CREATEFROMTIME", DateFormat.getDateOfBegin(condition.get("CREATEFROMTIME"), DateFormat.PTN_YMDHMS));
			}
			if (condition.containsKey("CREATETOTIME"))
			{
				condition.put("CREATETOTIME", DateFormat.getDateOfEnd(condition.get("CREATETOTIME"), DateFormat.PTN_YMDHMS));
			}
			if (condition.containsKey("UPDATEFROMTIME"))
			{
				condition.put("UPDATEFROMTIME", DateFormat.getDateOfBegin(condition.get("UPDATEFROMTIME"), DateFormat.PTN_YMDHMS));
			}
			if (condition.containsKey("UPDATETOTIME"))
			{
				condition.put("UPDATETOTIME", DateFormat.getDateOfEnd(condition.get("UPDATETOTIME"), DateFormat.PTN_YMDHMS));
			}
			int page = 999;
			int count = 0;
			if (condition.get("CURRENTPAGE") != null)
			{
				page = ((Number) condition.get("CURRENTPAGE")).intValue();
				if (page < 2)
				{
					Map<String, Object> countCondition = new HashMap<>(condition);
					countCondition.remove("ROWSPERPAGE");
					countCondition.remove("CURRENTPAGE");
					results = sds.query(TransformQueue.class, countCondition, "selectFuzzyCount");
					count = SetUtils.isNullList(results) ? 0 : results.get(0).getRowCount();
				}
			}
			if (page > 1 || count > 0)
			{
				results = sds.query(TransformQueue.class, condition, "selectFuzzy");
				if (!SetUtils.isNullList(results))
				{
					for (TransformQueue queue : results)
					{
						queue.put("ROWCOUNT$", count);
						if (isFullName)
						{
							try
							{
								FoundationObject fo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub()
										.getObjectByGuid(new ObjectGuid(queue.getTransformInstanceClassGuid(), null, queue.getTransformInstanceGuid(), null), false);
								if (fo != null)
								{
									queue.put("FULLNAME", fo.getFullName());
								}

								if (StringUtils.isGuid((String) queue.get(TransformQueue.TARGETREVISIONGUID)))
								{
									fo = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid((String) queue.get(TransformQueue.TARGETREVISIONCLASSGUID), null,
											(String) queue.get(TransformQueue.TARGETREVISIONGUID), null));
									if (fo != null)
									{
										queue.put("TARGETFULLNAME", fo.getFullName());
									}
								}
							}
							catch (Exception e)
							{

							}
						}

						if (!StringUtils.isGuid(queue.getTransformType()))
						{
							continue;
						}

						CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(queue.getTransformType());
						if (codeItem != null)
						{
							queue.put(TransformConfig.TRANSFORM_TYPE + "NAME", codeItem.getCode());
							if (this.stubService.getSignature() instanceof UserSignature)
							{
								queue.put(TransformConfig.TRANSFORM_TYPE + "$TITLE", codeItem.getTitle(this.stubService.getUserSignature().getLanguageEnum()));
							}
						}
					}
				}
			}
			else
			{
				results = null;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return results;
	}

	protected List<TransformQueue> listWaitingJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		// condition.put(TransformQueue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		condition.put(TransformQueue.JOB_STATUS, JobStatus.WAITING.getValue());
		return this.listQueue(condition, true);
	}

	protected List<TransformQueue> listRunningJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(TransformQueue.JOB_STATUS, JobStatus.RUNNING.getValue());
		// condition.put(TransformQueue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		return this.listQueue(condition, true);
	}

	protected List<TransformQueue> listCancelJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(TransformQueue.JOB_STATUS, JobStatus.CANCEL.getValue());
		// condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		return this.listQueue(condition, true);
	}

	protected DSSFileTrans downloadQueueInfo(String queueGuid, boolean needTransform) throws ServiceRequestException
	{
		TransformQueue queue = this.getQueue(queueGuid);
		int indexOf = queue.getFileName().lastIndexOf(".");
		String preName = queue.getFileName().substring(0, indexOf);
		DSSFileTrans downloadFile = null;
		if (needTransform)
		{
			downloadFile = this.stubService.getDSS().downloadFile(queue.getFileGuid());
		}
		else
		{
			TransformConfig transformConfig = this.stubService.getTransformConfigStub().getTransformConfig(queue.getTransformConfigGuid());
			if (transformConfig != null && transformConfig.getResultTypeEnum() == TransStorageType.ATTACHMENT)
			{
				FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS())
						.getObjectByGuid(new ObjectGuid(queue.getTransformInstanceClassGuid(), null, queue.getTransformInstanceGuid(), null));
				List<DSSFileInfo> listFile = this.stubService.getDSS().listFile(objectByGuid.getObjectGuid(), objectByGuid.getIterationId(), null);
				if (!SetUtils.isNullList(listFile))
				{
					for (DSSFileInfo info : listFile)
					{
						if (!"DOC_PDF".equalsIgnoreCase(info.getFileType()))
						{
							continue;
						}

						indexOf = info.getName().lastIndexOf(".");
						String preIName = info.getName().substring(0, indexOf);

						if (preIName.equalsIgnoreCase(preName))
						{
							downloadFile = this.stubService.getDSS().downloadFile(info.getGuid());
							break;
						}
					}
				}

			}
			else if (transformConfig != null && transformConfig.getResultTypeEnum() == TransStorageType.OBJECT)
			{
				List<StructureObject> listObjectOfRelation = this.stubService.getBOAS().listObjectOfRelation(
						new ObjectGuid(queue.getTransformInstanceClassGuid(), null, queue.getTransformInstanceGuid(), null), BuiltinRelationNameEnum.TRANSFROM.toString(), null,
						null, null);
				if (!SetUtils.isNullList(listObjectOfRelation))
				{

					FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getObjectByGuid(listObjectOfRelation.get(0).getEnd2ObjectGuid());
					List<DSSFileInfo> listFile = this.stubService.getDSS().listFile(objectByGuid.getObjectGuid(), objectByGuid.getIterationId(), null);
					if (!SetUtils.isNullList(listFile))
					{
						for (DSSFileInfo info : listFile)
						{
							indexOf = info.getName().lastIndexOf(".");
							String preIName = info.getName().substring(0, indexOf);

							if (preIName.equalsIgnoreCase(preName))
							{
								downloadFile = this.stubService.getDSS().downloadFile(info.getGuid());
								break;
							}
						}
					}
				}

			}

		}
		return downloadFile;
	}

	protected DSSFileTrans uploadQueueInfo(String queueGuid, DSSFileInfo file) throws ServiceRequestException
	{
		TransformQueue queue = this.getQueue(queueGuid);
		TransformConfig transformConfig = this.stubService.getTransformConfig(queue.getTransformConfigGuid());
		ObjectGuid objectGuid = new ObjectGuid(queue.getTransformInstanceClassGuid(), null, queue.getTransformInstanceGuid(), null);
		DSSFileTrans uploadFile = null;
		String clientFilePath = file.getFilePath();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			if (TransStorageType.ATTACHMENT.name().equalsIgnoreCase(transformConfig.getResultType()))
			{

				DSSFileInfo attachFile = ((DSSImpl) this.stubService.getDSS()).getInstFileStub().attachFile(objectGuid, file, false, false);
				uploadFile = this.stubService.getDSS().uploadFile(attachFile.getGuid(), clientFilePath);

			}
			else if (TransStorageType.PREVIEW.name().equalsIgnoreCase(transformConfig.getResultType()))
			{
				if ("Y".equalsIgnoreCase((String) file.get("ISPREVIEW")))
				{
					DSSFileInfo fileInfo = ((DSSImpl) this.stubService.getDSS()).getInstFileStub().attachFile(objectGuid, file, true, false, false, false);
					uploadFile = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().createFileTrans(fileInfo.getGuid(), clientFilePath, FileTransTypeEnum.UPL);
				}
				else
				{
					DSSFileInfo fileInfo = ((DSSImpl) this.stubService.getDSS()).getInstFileStub().attachFile(objectGuid, file, false, true, false, false);
					uploadFile = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().createFileTrans(fileInfo.getGuid(), clientFilePath, FileTransTypeEnum.UPL);
				}
			}
			else if (TransStorageType.OBJECT.name().equalsIgnoreCase(transformConfig.getResultType()) && !StringUtils.isNullString(transformConfig.getMappingClass()))
			{
				ObjectGuid targetObjectGuid = null;
				// true:已经存在的关联， false:其他版本中已经存在的关联，null:对象未创建
				Boolean isFormOwner = null;
				List<StructureObject> listObjectOfRelation = this.stubService.getBOAS().listObjectOfRelation(objectGuid, BuiltinRelationNameEnum.TRANSFROM.toString(), null, null,
						null);
				if (!SetUtils.isNullList(listObjectOfRelation))
				{
					for (StructureObject so : listObjectOfRelation)
					{

						if (StringUtils.isNullString(so.getEnd2ObjectGuid().getClassGuid()))
						{
							continue;
						}

						if (transformConfig.getMappingClass().equalsIgnoreCase(so.getEnd2ObjectGuid().getClassGuid()))
						{
							targetObjectGuid = so.getEnd2ObjectGuid();
							isFormOwner = true;
							break;
						}
					}
				}
				if (targetObjectGuid == null)
				{
					FoundationObject oriFoundation = this.stubService.getBOAS().getObjectByGuid(objectGuid);
					List<FoundationObject> listObjectRevisionHistory = this.stubService.getBOAS().listObjectRevisionHistory(oriFoundation.getObjectGuid());
					if (!SetUtils.isNullList(listObjectRevisionHistory))
					{
						for (FoundationObject object : listObjectRevisionHistory)
						{
							listObjectOfRelation = this.stubService.getBOAS().listObjectOfRelation(object.getObjectGuid(), BuiltinRelationNameEnum.TRANSFROM.toString(), null, null,
									null);
							if (!SetUtils.isNullList(listObjectOfRelation))
							{
								for (StructureObject so : listObjectOfRelation)
								{
									if (StringUtils.isNullString(so.getEnd2ObjectGuid().getClassGuid()))
									{
										continue;
									}
									if (transformConfig.getMappingClass().equalsIgnoreCase(so.getEnd2ObjectGuid().getClassGuid()))
									{
										targetObjectGuid = so.getEnd2ObjectGuid();
										isFormOwner = false;
										break;
									}
								}
							}
							if (targetObjectGuid != null)
							{
								break;
							}
						}
					}
				}

				if (isFormOwner == null || isFormOwner == false)
				{
					RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(objectGuid, BuiltinRelationNameEnum.TRANSFROM.toString());
					if (relationTemplate == null)
					{
						DynaLogger.info("relation template isnot exist, transform file upload error");
						return null;
					}

					FoundationObject newFoundationObject = null;
					if (isFormOwner == null)
					{
						newFoundationObject = this.stubService.getBOAS().newFoundationObject(transformConfig.getMappingClass(), null);
					}
					else
					{
						newFoundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(targetObjectGuid, false);
					}

					// 添加映射字段
					List<TransformFieldMapping> listFieldMappingConfig = this.stubService.getTransformConfigStub().listFieldMappingConfig(transformConfig.getGuid());
					if (!SetUtils.isNullList(listFieldMappingConfig))
					{
						FoundationObject oriFoundation = this.stubService.getBOAS().getObject(objectGuid);

						for (TransformFieldMapping fieldMapping : listFieldMappingConfig)
						{

							boolean setTargetValue = this.setTargetValue(newFoundationObject, fieldMapping.getTargetFieldName(), oriFoundation, fieldMapping.getSourceFieldName());
							if (!setTargetValue && !StringUtils.isNullString(fieldMapping.getDefultValue()))
							{
								this.setDefaultValue(newFoundationObject, fieldMapping.getTargetFieldName(), fieldMapping.getDefultValue());
							}

						}
					}
					if (isFormOwner == null)
					{
						FoundationObject createObject = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(newFoundationObject, null, false, false);
						targetObjectGuid = createObject.getObjectGuid();
					}
					else if (isFormOwner == false)
					{
						((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(newFoundationObject, false, false, false, false, null, true, false, true, true);
					}

					StructureObject structureObject = this.stubService.getBOAS().newStructureObject(relationTemplate.getStructureClassGuid(), null);
					this.stubService.getBOAS().link(objectGuid, targetObjectGuid, structureObject, BuiltinRelationNameEnum.TRANSFROM.toString());

				}

				DSSFileInfo attachFile = ((DSSImpl) this.stubService.getDSS()).getInstFileStub().attachFile(targetObjectGuid, file, false, false);
				uploadFile = this.stubService.getDSS().uploadFile(attachFile.getGuid(), clientFilePath);

				queue.put(TransformQueue.TARGETREVISIONCLASSGUID, targetObjectGuid.getClassGuid());
				queue.put(TransformQueue.TARGETREVISIONGUID, targetObjectGuid.getGuid());

				SystemDataService sds = DataServer.getSystemDataService();
				sds.save(queue);

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
		return uploadFile;
	}

	public boolean setTargetValue(FoundationObject target, String fieldName, FoundationObject sourceFoundation, String sourceFieldName) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(target.getObjectGuid(), this.stubService);
		ClassStub.decorateObjectGuid(sourceFoundation.getObjectGuid(), this.stubService);

		ClassField field = this.stubService.getEMM().getFieldByName(target.getObjectGuid().getClassName(), fieldName, true);
		ClassField sfield = this.stubService.getEMM().getFieldByName(sourceFoundation.getObjectGuid().getClassName(), sourceFieldName, true);
		Object object = sourceFoundation.get(sourceFieldName);
		if (field != null && sfield != null && field.getType() == sfield.getType() && object != null && field.getType().equals(FieldTypeEnum.OBJECT))
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByName(field.getTypeValue());
			if (classInfo.hasInterface(ModelInterfaceEnum.IUser) || classInfo.hasInterface(ModelInterfaceEnum.IGroup) || classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar)
					|| classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
			{
				return false;
			}
			ObjectGuid objectGuid = new ObjectGuid(this.stubService.getEMM().getClassByName(field.getTypeValue()).getGuid(), null, object.toString(), null);
			target.put(fieldName, object);
			target.put(fieldName + "$master", objectGuid.getMasterGuid());
			target.put(fieldName + "$class", this.stubService.getEMM().getClassByName(field.getTypeValue()).getGuid());
			return true;
		}
		else if (field != null && sfield != null && field.getType() == sfield.getType() && object != null)
		{
			target.put(fieldName, object);
			return true;
		}

		return false;

	}

	public void setDefaultValue(FoundationObject target, String fieldName, String value) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(target.getObjectGuid(), this.stubService);
		ClassField field = this.stubService.getEMM().getFieldByName(target.getObjectGuid().getClassName(), fieldName, true);
		if (field != null)
		{
			switch (field.getType())
			{
			case BOOLEAN:
				if ("TRUE".equalsIgnoreCase(value))
				{
					target.put(fieldName, "Y");
				}
				else
				{
					target.put(fieldName, "N");
				}
				break;
			case CODE:
				if (!StringUtils.isNullString(field.getTypeValue()))
				{
					List<CodeItemInfo> listCodeItem = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, field.getTypeValue());
					CodeItemInfo codeItemByName = null;
					if (!SetUtils.isNullList(listCodeItem))
					{
						for (CodeItemInfo info : listCodeItem)
						{
							String title = info.getTitle(this.stubService.getUserSignature().getLanguageEnum());
							if (value.equalsIgnoreCase(title))
							{
								codeItemByName = info;
								break;
							}
						}
					}
					// CodeItemInfo codeItemByName = this.stubService.getEMM().getCodeItemByName(field.getTypeValue(),
					// value);
					if (codeItemByName != null)
					{
						target.put(fieldName, codeItemByName.getGuid());
					}

				}
			case CLASSIFICATION:
				ClassStub.decorateObjectGuid(target.getObjectGuid(), this.stubService);
				ClassInfo classByName = this.stubService.getEMM().getClassByName(target.getObjectGuid().getClassName());
				if (classByName != null && !StringUtils.isNullString(classByName.getClassification()))
				{

					List<CodeItemInfo> listCodeItem = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, classByName.getClassification());
					CodeItemInfo codeItemByName = null;
					if (!SetUtils.isNullList(listCodeItem))
					{
						for (CodeItemInfo info : listCodeItem)
						{
							String title = info.getTitle(this.stubService.getUserSignature().getLanguageEnum());
							if (value.equalsIgnoreCase(title))
							{
								codeItemByName = info;
								break;
							}
						}
					}

					// CodeItemInfo codeItemByName =
					// this.stubService.getEMM().getCodeItemByName(classByName.getClassification(), value);
					if (codeItemByName != null)
					{
						target.put(fieldName, codeItemByName.getGuid());
					}
				}
				break;
			case FLOAT:
			case INTEGER:
				try
				{
					Double.valueOf(value);
					target.put(fieldName, value);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			case DATE:
				try
				{
					Date parse = DateFormat.parse(value, DateFormat.PTN_YMD);
					target.put(fieldName, parse);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			case DATETIME:
				try
				{
					Date parse = DateFormat.parse(value, DateFormat.PTN_YMDHMS);
					target.put(fieldName, parse);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			case OBJECT:

				break;
			case STRING:
				target.put(fieldName, value);
				break;
			default:
				break;
			}
		}
		else
		{

		}

	}

	public TransformQueue changeQueueStatus(TransformQueue fo, JobStatus jobStatus) throws ServiceRequestException
	{
		// if (!fo.isChanged(Queue.JOB_STATUS))
		// {
		// throw new ServiceRequestException("status no changed");
		// }

		fo.setJobStatus(jobStatus.getValue() + "");
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			String guid = fo.getGuid();
			fo.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.save(fo);
			fo = this.stubService.getQueueInfo(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return fo;
	}

	public void queueCheck() throws ServiceRequestException
	{
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(TransformQueue.JOB_STATUS, JobStatus.RUNNING.getValue() + "");
		List<TransformQueue> listQueue = this.listQueue(condition, true);

		if (checkTime == 30 * 60 * 1000)
		{
			File pFile = new File(EnvUtils.getConfRootPath() + "conf/tfconf.properties"); // properties文件
			if (pFile.exists())
			{
				FileInputStream fileInputStream = null;
				try
				{
					fileInputStream = new FileInputStream(pFile);
					Properties propertys = new Properties();
					propertys.load(fileInputStream);
					checkTime = Long.valueOf((String) propertys.get("timeout")) * 1000;

				}
				catch (FileNotFoundException e)
				{
				}
				catch (IOException e)
				{
				}
				finally
				{
					if (fileInputStream != null)
					{
						try
						{
							fileInputStream.close();
						}
						catch (IOException e)
						{
						}
					}
				}
			}
		}

		Date current = new Date(System.currentTimeMillis() - checkTime);
		for (TransformQueue queue : listQueue)
		{
			if (queue.getJobStatus() == JobStatus.RUNNING && queue.getUpdateTime().compareTo(current) < 0)
			{

				String msrString = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_TIMEOUT", this.stubService.getUserSignature().getLanguageEnum().toString());
				if (msrString == null)
				{
					msrString = "ID_APP_TRANSFORM_TIMEOUT";
				}
				// msrString = MessageFormat.format(msrString, "30");
				queue.setResult(msrString);
				// this.changeQueueStatus(queue, JobStatus.FAILED);

				this.updateQueueInfo(queue.getGuid(), JobStatus.FAILED.getValue() + "", msrString);

				String procrtGuid = (String) queue.get("PROCRTGUID");
				String actrtGuid = (String) queue.get("ACTRTGUID");
				List<String> toUserIdList = new ArrayList<String>();
				toUserIdList.add("admin");
				toUserIdList.add(queue.getCreateUserGuid());
				String actUser = null;
				if (procrtGuid != null)
				{
					List<Performer> listPerformer = this.stubService.getWFI().listPerformer(actrtGuid);
					if (!SetUtils.isNullList(listPerformer))
					{
						for (Performer per : listPerformer)
						{
							actUser = per.getActualPerformerGuid();
						}
					}
				}

				if (actUser != null)
				{
					toUserIdList.add(actUser);
				}

				String subject = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_OUTTIME_TITLE", this.stubService.getUserSignature().getLanguageEnum().getId());
				String content = this.stubService.getMSRM().getMSRString("ID_APP_TRANSFORM_OUTTIME_CONTENT", this.stubService.getUserSignature().getLanguageEnum().getId());

				ObjectGuid objectGuid = new ObjectGuid(queue.getTransformInstanceClassGuid(), null, queue.getTransformInstanceGuid(), null);

				// FoundationObject object = ((BOASImpl)this.stubService.getBOAS()
				// ).getFoundationStub().getObject(objectGuid, false);

				content = MessageFormat.format(content, queue.get("FULLNAME"), queue.getFileName(), queue.get("TRANSFORMTYPE$TITLE"));

				List<ObjectGuid> objectGuidList = new ArrayList<ObjectGuid>();
				objectGuidList.add(objectGuid);
				this.stubService.getSMS().sendMailToUsers(subject, content, MailCategoryEnum.ERROR, objectGuidList, toUserIdList, MailMessageType.JOBNOTIFY);

			}
		}
	}

	protected void saveTransformServers(String serverID, String[] transformTypes) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(TransformServer.SERVER_ID, serverID);
			sds.delete(TransformServer.class, filter, "deleteConfig");
			Map<String, String> typeList = new HashMap<String, String>();
			List<CodeItemInfo> listCodeItem = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.TRANSFORMTYPE.getValue());
			if (!SetUtils.isNullList(listCodeItem))
			{
				for (CodeItemInfo info : listCodeItem)
				{
					if (!StringUtils.isNullString(info.getCode()))
					{
						typeList.put(info.getCode().toUpperCase(), info.getGuid());
					}
				}
			}

			if (transformTypes != null)
			{
				for (String itemName : transformTypes)
				{
					if (StringUtils.isNullString(itemName))
					{
						continue;
					}
					if (!typeList.containsKey(itemName.toUpperCase()))
					{
						continue;
					}
					TransformServer server = new TransformServer();
					server.setServerID(serverID);
					server.setTransformType(typeList.get(itemName.toUpperCase()));
					server.setCreateUserGuid(this.stubService.getOperatorGuid());
					server.setUpdateUserGuid(this.stubService.getOperatorGuid());

					sds.save(server);
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
	}
}

class TransformQueueScheduledTask extends AbstractScheduledTask
{
	private ServiceContext serviceContext = null;

	public TransformQueueScheduledTask(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{
		DynaLogger.info("Transform Queue Scheduled [Class]TransformQueueScheduledTask , Scheduled Task Start...");

		FTS fts = null;
		try
		{
			fts = this.serviceContext.allocatService(FTS.class);
			((FTSImpl) fts).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			((FTSImpl) fts).getTransformStub().queueCheck();
		}
		catch (Throwable e)
		{
			DynaLogger.error("run QueueCheck:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (fts != null)
			{
				this.serviceContext.releaseService(fts);
			}
		}
		DynaLogger.info("QueueCheck Scheduled [Class]TransformQueueScheduledTask , Scheduled Task End...");
	}

}
