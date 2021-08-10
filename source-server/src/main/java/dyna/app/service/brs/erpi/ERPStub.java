/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPStub
 * wangweixia 2012-10-29
 */
package dyna.app.service.brs.erpi;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.erpi.dataExport.IntegrateERP;
import dyna.app.service.das.jss.JSSImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.erp.ERPQuerySchemaParameterObject;
import dyna.common.bean.erp.ERPSchema;
import dyna.common.bean.erp.ERPYFAllConfig;
import dyna.common.bean.erp.ERPYFPLMClassConfig;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.ServiceDefinition;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.Queue;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.erp.ERPBOConfig;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.erp.ERPTransferLog;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataFieldNullException;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.UECS;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.*;

/**
 * @author wangweixia
 */
public class ERPStub extends AbstractServiceStub<ERPIImpl>
{
	/**
	 * 将xml配置文件缓存到map中，只有当xml文件发生变化，更新这个缓存. key是文件路径
	 */
	public static final Map<String, DocumentMark> XMLMap = new HashMap<String, DocumentMark>();

	/**
	 * 将xml配置文件缓存到map中，只有当xml文件发生变化，更新这个缓存，key是文件路径
	 */
	public static final Map<String, ConfigurableKVElementImpl> ConfigurableMap = new HashMap<String, ConfigurableKVElementImpl>();

	/**
	 * 一些个性化的参数设置或者经常发生变化的参数，不方便配置在xml文件中。如E10的TokenId这个参数，由于经常 <br/>
	 * 发生变化，配置在xml中每次都要更新xml文件，浪费大量I/O资源。因此配置在<b>paramMap</b>里。
	 * key是ERPTypeName+.+参数名
	 */
	public static final Map<String, String> paramMap        = new HashMap<String, String>();
	/**
	 * 流程拋轉合併到同一個隊列的對象數量</br>
	 * 最大值120,默认值50
	 */
	public static       int                 objectBatchSize = 50;

	/**
	 * @param context
	 * @param service
	 */
	protected ERPStub(ServiceContext context, ERPIImpl service)
	{
		super(context, service);

	}

	public Document getDocument(String fileName) throws Exception
	{
		File file = new File(EnvUtils.getConfRootPath() + "conf" + File.separator + fileName);
		if (!file.exists())
		{
			throw new ServiceRequestException("ID_APP_ERPI_CONFIG_FILE_NOT_EXIST", "no erp config file:", null, fileName);
		}
		try
		{
			fileName = fileName.toLowerCase();
			if (XMLMap.get(fileName) == null || XMLMap.get(fileName).isChanged(file.lastModified()))
			{
				XMLMap.remove(fileName);
				ConfigurableMap.remove(fileName);
				Document document = new SAXBuilder().build(file);
				DocumentMark mark = new DocumentMark(document, file.lastModified());
				XMLMap.put(fileName, mark);

				ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
				configLoader.setConfigFile(file);
				configLoader.load();
				ConfigurableKVElementImpl kv = configLoader.getConfigurable();
				ConfigurableMap.put(fileName, kv);
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("ID_APP_ERPI_CONFIG_FILE_ERROR", "error erp config file:", null, fileName);
		}
		return XMLMap.get(fileName).getDocument();
	}

	/**
	 * 这里不能用单例。
	 * 一次传输过程中，会把一些状态信息保存在Stub实例的变量中，如果用单例的话会产生多线程安全问题
	 *
	 * @return
	 * @throws Exception
	 */
	private ERPYFTransferStub getErpYFStub() throws Exception
	{
		return new ERPYFTransferStub(this.serviceContext, this.stubService, this.getDocument("yfconf.xml"));
	}

	/**
	 * @return
	 * @throws Exception
	 * @see #getErpYFStub
	 */
	private ERPWFTransferStub getErpWFStub() throws Exception
	{
		return new ERPWFTransferStub(this.serviceContext, this.stubService, this.getDocument("wfconf.xml"));
	}

	/**
	 * @return
	 * @throws Exception
	 * @see #getErpYFStub
	 */
	private ERPYTTransferStub getErpYTStub() throws Exception
	{
		return new ERPYTTransferStub(this.serviceContext, this.stubService, this.getDocument("ytconf.xml"));
	}

	/**
	 * @return
	 * @throws Exception
	 * @see #getErpYFStub
	 */
	private ERPE10TransferStub getErpE10Stub() throws Exception
	{
		return new ERPE10TransferStub(this.serviceContext, this.stubService, this.getDocument("e10conf.xml"));
	}

	private ERPSMTransferStub getErpSMStub() throws Exception
	{
		return new ERPSMTransferStub(this.serviceContext, this.stubService, this.getDocument("smconf.xml"));
	}

	private ERPT100TransferStub getErpT100Stub() throws Exception
	{
		return new ERPT100TransferStub(this.serviceContext, this.stubService, this.getDocument("t100conf.xml"));
	}

	private ERPT100OldTransferStub getErpT100OldStub() throws Exception
	{
		return new ERPT100OldTransferStub(this.serviceContext, this.stubService, this.getDocument("t100conf.xml"));
	}

	private ERPT100DBTransferStub getErpT100DBStub() throws Exception
	{
		return new ERPT100DBTransferStub(this.serviceContext, this.stubService, this.getDocument("t100_dbconf.xml"));
	}

	public BooleanResult setERPJobStatus(String jobId, String userId, String jobStatusVal, String message) throws ServiceRequestException
	{
		if (StringUtils.isNullString(jobId))
		{
			throw new ServiceRequestException("PLM:    Job data is not exists!");
		}

		JobStatus jobStatus = JobStatus.FAILED;
		if (jobStatusVal != null)
		{
			jobStatus = JobStatus.getStatus(Integer.valueOf(jobStatusVal));
		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(Queue.FIELDH, jobId);
		List<Queue> list = this.stubService.getJSS().listJob(condition);
		if (SetUtils.isNullList(list))
		{
			throw new ServiceRequestException("PLM:    Job data is not exists! jobid=" + jobId);
		}

		Queue qu = list.get(0);
		qu.setJobStatus(jobStatus);
		if (StringUtils.isNullString(message))
		{
			if (JobStatus.FAILED.equals(jobStatus))
			{
				LanguageEnum lang = this.stubService.getUserSignature().getLanguageEnum();
				message = this.stubService.getMSRM().getMSRString("ID_ERP_FAILED_DEFAULT_MESSAGE", lang.toString());
			}
		}
		qu.setResult(message);
		Queue qq = this.stubService.getJSS().saveJob4ERPNotify(qu);
		DynaLogger.debug(qq.getJobStatus());
		return new BooleanResult(jobStatus == JobStatus.SUCCESSFUL ? true : false, message);
	}

	public BooleanResult setERPJobStatus(String jobId, String userId, String jobStatusVal, String message, boolean isNotify) throws ServiceRequestException
	{
		BooleanResult result = this.setERPJobStatus(jobId, userId, jobStatusVal, message);
		if (isNotify)
		{
			this.notifyUser(jobId, result);
		}
		return result;
	}

	private void notifyUser(String jobId, BooleanResult result) throws ServiceRequestException
	{
		Queue queue = this.getJobByID(jobId);
		User user = this.stubService.getAAS().getUser(queue.getCreateUserGuid());
		MailCategoryEnum mailCategory = MailCategoryEnum.INFO;
		List<String> listNoticeUsers = new ArrayList<String>();
		String mail = null;
		if (!result.getFlag())
		{
			mailCategory = MailCategoryEnum.ERROR;
		}
		if (!StringUtils.isNullString(queue.getType()) && "CFERP".equals(queue.getType()))
		{
			mail = this.formatMailContent(queue.getFieldf(), queue.getFieldd(), result.getDetail().replaceAll(";", "\r\n"), result.getFlag(), jobId);
			listNoticeUsers.add(user.getGuid());
		}
		else
		{
			// 抛转时 class is not found 问题。抛转时不一定只抛转一个对象
			String[] splita = queue.getFielda().split(";");
			String[] splitb = queue.getFieldb().split(";");
			String foNames = "";
			try
			{
				FoundationObject fo = this.getObject(splita[0], splitb[0]);
				foNames = foNames + fo.getFullName();
				if (splita.length > 1)
				{
					foNames = foNames + "  (...)";
				}
			}
			catch (Exception e)
			{

			}
			mail = this.formatMailContent(queue.getFieldf(), foNames, queue.getFieldd(), result.getDetail().replaceAll(";", "\r\n"), result.getFlag(), jobId);
			// FoundationObject fo = this.getObject(queue.getFielda(), queue.getFieldb());
			// mail = this.formatMailContent(queue.getFieldf(), fo.getFullName(), queue.getFieldd(),
			// result.getDetail().replaceAll(";", "\r\n"), result.getFlag(), jobId);

			listNoticeUsers = this.stubService.listEndNoticeUsersByServGuid(queue.getFieldc());
			// 如果listNoticeUsers不包含创建者，则把创建者加入到通知列表中
			if (!listNoticeUsers.contains(user.getGuid()))
			{
				listNoticeUsers.add(user.getGuid());
			}
		}

		this.stubService.getSMS().sendMailToUsers(queue.getName(), mail, mailCategory, null, listNoticeUsers, MailMessageType.ERPNOTIFY);
	}

	private String formatMailContent(String factory, String schema, String result, boolean success, String jobId)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(factory).append(": ").append(schema).append(". ").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
				.append("JobID: ").append(jobId).append(System.getProperty("line.separator")).append("CFERP:    ").append(result);
		String mail = null;
		if (success)
		{
			mail = "Succeed: " + strBuilder.toString();
		}
		else
		{
			mail = "Fail: " + strBuilder.toString();
		}

		// 如果mail长度超过一定字符，则截断
		final int maxLength = 2000;
		if (mail.length() > maxLength)
		{
			mail = mail.substring(0, maxLength);
		}
		return mail;
	}

	private String formatMailContent(String factory, String itemNo, String schema, String result, boolean success, String jobId)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(factory).append(": ").append(itemNo).append(": ").append(schema).append(". ").append(System.getProperty("line.separator"))
				.append(System.getProperty("line.separator")).append("JobID: ").append(jobId).append(System.getProperty("line.separator")).append("ERP:    ").append(result);
		String mail = null;
		if (success)
		{
			mail = "Succeed: " + strBuilder.toString();
		}
		else
		{
			mail = "Fail: " + strBuilder.toString();
		}

		// 如果mail长度超过一定字符，则截断
		final int maxLength = 2000;
		if (mail.length() > maxLength)
		{
			mail = mail.substring(0, maxLength);
		}
		return mail;
	}

	private FoundationObject getObject(String guid, String classGuid) throws ServiceRequestException
	{
		ObjectGuid itemObjectGuid = new ObjectGuid(classGuid, null, guid, null);
		FoundationObject foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(itemObjectGuid, false);
		DynaLogger.debug(foundationObject);
		if (foundationObject == null)
		{
			throw new NullPointerException(this.stubService.getMSRM().getMSRString("ID_DS_NO_DATA", this.stubService.getUserSignature().getLanguageEnum().toString()));
		}

		return foundationObject;
	}

	private Queue getJobByID(String jobId) throws ServiceRequestException
	{
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(Queue.FIELDH, jobId);
		List<Queue> list = this.stubService.getJSS().listJob(condition);
		if (SetUtils.isNullList(list))
		{
			throw new NullPointerException(this.stubService.getMSRM().getMSRString("ID_DS_NO_DATA", this.stubService.getUserSignature().getLanguageEnum().toString()));
		}

		return list.get(0);
	}

	/**
	 * @param objectGuidList
	 * @param erpFactory
	 * @param templateName
	 * @param serverGuid
	 * @param userList
	 * @param isGoFlow
	 * @param isMerge
	 * @throws ServiceRequestException
	 */
	public void createERPJob(List<ObjectGuid> objectGuidList, List<String> erpFactory, String templateName, String serverGuid, List<WorkflowTemplateActPerformerInfo> userList,
			boolean isGoFlow, boolean isMerge, boolean checkAuthor) throws ServiceRequestException
	{
		ERPServerType serverType = null;
		DynaLogger.info("*****Start createERPJob*****");
		// 取得失败后需要通知的人
		// List<String> userGuidList = this.listNotifyUsersWhenFailed(serverGuid);
		String locale = this.stubService.getUserSignature().getLanguageEnum().toString();
		// 取得当前执行人的Guid
		String userGuid = this.stubService.getUserSignature().getUserGuid();
		boolean isAdministrator = true;
		if (checkAuthor)
		{
			isAdministrator = this.isAdministrator();
		}
		// 通过serviceGuid和userGuid以及身份取得BOConfig信息
		List<ERPBOConfig> erpListCanUse = this.listERPBoConfigBySerGuidAndUseGuid(serverGuid, userGuid, isAdministrator);
		if (SetUtils.isNullList(erpFactory))
		{
			erpFactory = this.listFacrotyByServiceGuid(serverGuid);
		}
		if (SetUtils.isNullList(erpFactory))
		{
			throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_INTEGRATEYF_ERP_HAS_NO_COMPANY_USEING", locale));
		}
		ERPServiceConfig serviceConfig = this.getERPServiceConfigbyServiceGuid(serverGuid);
		if (serviceConfig == null)
		{
			return;
		}

		if (StringUtils.isNullString(templateName))
		{
			templateName = this.getDefaultBOMTemplate(ERPServerType.valueOf(serviceConfig.getERPServerSelected()));
		}
		serverType = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
		if (isMerge)
		{
			objectBatchSize = getObjectBatchSize(serverType);
		}
		// 从服务中 取得导出方案
		String actionSchema = serviceConfig.getSchemaName();

		List<ObjectGuid> tempObjectGuidList = new ArrayList<ObjectGuid>();
		for (String factoryName : erpFactory)
		{
			LanguageEnum lang = this.stubService.getUserSignature().getLanguageEnum();
			if (!SetUtils.isNullList(objectGuidList))
			{
				Map<String, String> parameterMap = new LinkedHashMap<String, String>();
				parameterMap.put("serverGuid", serverGuid);
				parameterMap.put("factoryName", factoryName);
				parameterMap.put("templateName", templateName);
				parameterMap.put("lang", lang.toString());
				parameterMap.put("actionSchema", actionSchema);
				parameterMap.put("serviceConfigName", serviceConfig.getName());
				parameterMap.put("userGuid", userGuid);
				if (this.isBOMExport(serverType, serviceConfig.getSchemaName()))
				{
					parameterMap.put("templateName", ConfigParameterConstants.CONFIG_PARAMETER_RESULTRELATION_TEMPLATE_NAME);
				}
				for (ObjectGuid objectGuid : objectGuidList)
				{
					if (!SetUtils.isNullList(erpListCanUse))
					{
						this.createJob(objectGuid, erpListCanUse, parameterMap, isGoFlow, isMerge, tempObjectGuidList);
					}
				}
				if (isMerge && !SetUtils.isNullList(tempObjectGuidList))
				{
					List<ObjectGuid> tempList = new ArrayList<ObjectGuid>();
					for (int i = 0; i < tempObjectGuidList.size(); i++)
					{
						tempList.add(tempObjectGuidList.get(i));
						if (tempList.size() % objectBatchSize == 0 || i == (tempObjectGuidList.size() - 1))
						{
							this.createMergeQueue(parameterMap, tempList, isGoFlow);
							tempList.clear();
						}
					}
					tempObjectGuidList.clear();
				}
			}
		}
		DynaLogger.info("*****End createERPJob*****");

	}

	public boolean isAdministrator() throws ServiceRequestException
	{
		UserSignature userSign = this.stubService.getUserSignature();
		String groupGuid = userSign.getLoginGroupGuid();
		return this.stubService.getAAS().getGroup(groupGuid).isAdminGroup();
	}

	/**
	 * @param boClassName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPServiceConfig> listERPServiceConfigNameByBoClassName(String boClassName) throws ServiceRequestException
	{
		UserSignature userSignature = this.stubService.getUserSignature();
		String groupGuid = userSignature.getLoginGroupGuid();
		String userGuid = userSignature.getUserGuid();
		return this.listERPServiceConfigNameByBoClassName(boClassName, groupGuid, userGuid, this.isAdministrator());
	}

	/**
	 * @param boClassName
	 * @param groupGuid
	 * @param userGuid
	 * @param isAdministrator
	 * @return
	 */
	protected List<ERPServiceConfig> listERPServiceConfigNameByBoClassName(String boClassName, String groupGuid, String userGuid, boolean isAdministrator)
			throws ServiceRequestException
	{
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		// 如果boClassName不为空，就将其放入；为空，则查所有的服务
		if (!StringUtils.isNullString(boClassName))
		{
			conditionMap.put("CLASSNAME", boClassName);
		}
		conditionMap.put("GROUPGUID", groupGuid);
		// 在传输菜单中只显示isinuse='Y'的模板
		conditionMap.put("ISINUSE", "Y");

		List<ERPServiceConfig> erpServiceConfig;
		try
		{
			erpServiceConfig = this.listERPConfig(boClassName, true);
			// 当前执行者为管理员身份,Map中只需两个参数
			if (!isAdministrator)
			{
				erpServiceConfig = this.listERPServiceConfigByNoticeper(erpServiceConfig, userGuid);
			}
			return erpServiceConfig;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<ERPServiceConfig> listERPServiceConfigByNoticeper(List<ERPServiceConfig> erpServiceConfigList, String userGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(erpServiceConfigList))
		{
			for (Iterator<ERPServiceConfig> it = erpServiceConfigList.iterator(); it.hasNext(); )
			{
				ERPServiceConfig config = it.next();
				List<WorkflowTemplateActPerformerInfo> useInfoList = sds
						.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<>(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, config.getGuid()));
				if (SetUtils.isNullList(useInfoList))
				{
					it.remove();
				}
				else
				{
					List<String> userGuidList = this.listErpUser(useInfoList);
					if (SetUtils.isNullList(userGuidList) || !userGuidList.contains(userGuid))
					{
						it.remove();
					}
				}
			}
		}
		return erpServiceConfigList;
	}

	private List<ERPServiceConfig> listERPConfig(String className, boolean isInUse) throws ServiceRequestException
	{
		// 取得类对应的业务对象在当前业务模型中的所有父阶业务对象
		List<BOInfo> parentBoInfoList = this.listAllParentBOByClassName(className);
		if (SetUtils.isNullList(parentBoInfoList))
		{
			return null;
		}
		Map<String, Object> param = new HashMap<>();
		List<ERPServiceConfig> allERPConfigList = this.listERPConfigForNoClass(this.stubService.getUserSignature().getLoginGroupGuid(), isInUse);
		if (!SetUtils.isNullList(allERPConfigList))
		{
			for (Iterator<ERPServiceConfig> cIt = allERPConfigList.iterator(); cIt.hasNext(); )
			{
				ERPServiceConfig config = cIt.next();

				param.clear();
				param.put("TEMPLATEGUID", config.getGuid());
				param.put("BMGUID", this.stubService.getUserSignature().getLoginGroupBMGuid());

				// 取得模板配置的所有BO
				List<ERPServiceConfig> boConfigList = DataServer.getSystemDataService().query(ERPServiceConfig.class, param, "selectBOConfig");
				if (!SetUtils.isNullList(boConfigList))
				{
					List<String> boNameConfigList = new ArrayList<>();
					for (ERPServiceConfig boConfig : boConfigList)
					{
						String boName = (String) boConfig.get("BONAME");
						if (!StringUtils.isNullString(boName))
						{
							boNameConfigList.add(boName);
						}
					}
					if (SetUtils.isNullList(boNameConfigList))
					{
						return null;
					}

					// 把模板配置的业务对象在父阶业务对象中不存在的去除
					for (Iterator<String> it = boNameConfigList.iterator(); it.hasNext(); )
					{
						String boName = it.next();

						boolean isFind = false;
						for (BOInfo boInfo : parentBoInfoList)
						{
							if (boName.equals(boInfo.getName()))
							{
								isFind = true;
								break;
							}
						}

						if (!isFind)
						{
							it.remove();
						}
					}

					// 如果模板配置的所有业务对象都不在类的所有父阶业务对象列表中，则该模板移除
					if (SetUtils.isNullList(boNameConfigList))
					{
						cIt.remove();
					}
				}
			}
		}
		return allERPConfigList;
	}

	private List<BOInfo> listAllParentBOByClassName(String className) throws ServiceRequestException
	{
		BOInfo boInfo = this.stubService.getEMM().getCurrentBoInfoByClassName(className);
		List<BOInfo> parentBOInfoList = new ArrayList<>();
		parentBOInfoList.add(boInfo);

		String parentBOName = boInfo.getParent();
		if (!StringUtils.isNullString(parentBOName))
		{
			this.listAllParentBO(parentBOName, parentBOInfoList);
		}
		return parentBOInfoList;
	}

	private void listAllParentBO(String boName, List<BOInfo> parentBOInfoList) throws ServiceRequestException
	{
		BOInfo boInfo = this.stubService.getEMM().getCurrentBoInfoByName(boName, false);
		if (boInfo != null)
		{
			parentBOInfoList.add(boInfo);
			String parentBOName = boInfo.getParent();
			if (!StringUtils.isNullString(parentBOName))
			{
				this.listAllParentBO(parentBOName, parentBOInfoList);
			}
		}
	}

	/**
	 * @param allConfig
	 * @return
	 * @throws ServiceRequestException
	 */
	public ERPYFAllConfig saveTemplate(ERPYFAllConfig allConfig) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		Session senInfo = DataServer.getSystemDataService().get(Session.class, sessionId);

		ERPServiceConfig erpServiceInfo = allConfig.getErpServiceConfig();
		String serverType = erpServiceInfo.getERPServerSelected();
		if (serverType.equalsIgnoreCase(ERPServerType.ERPTIPTOP.name()))
		{
			serverType = ERPServerType.ERPTIPTOP.name();
		}
		ERPYFPLMClassConfig erpYfPlmInfo = allConfig.getErpYFPLMClassConfig();
		List<ERPBOConfig> erpBoconfig = erpYfPlmInfo.getBoinfoList();
		if (SetUtils.isNullList(erpBoconfig))
		{
			throw new DynaDataFieldNullException("ERPBOConfig");
		}
		SystemDataService sds = DataServer.getSystemDataService();
		String serviceGuid = erpServiceInfo.getGuid();
		try
		{
			// 新建模板
			if (StringUtils.isNullString(serviceGuid))
			{
				// 设置模板的创建人
				erpServiceInfo.setCreateUserGuid(senInfo.getUserGuid());
				erpServiceInfo.setUpdateUserGuid(senInfo.getUserGuid());
				serviceGuid = sds.save(erpServiceInfo, "insert");// 保存服务

				for (ERPBOConfig erpBoInfo : erpBoconfig)
				{
					erpBoInfo.setCreateUserGuid(senInfo.getUserGuid());
					erpBoInfo.setUpdateUserGuid(senInfo.getUserGuid());
					erpBoInfo.setTemplateGguid(serviceGuid);
					sds.save(erpBoInfo, "insert");// 保存ERPBOConfig
				}

				List<String> moreCompanyList = erpYfPlmInfo.getMoreCompanyList();
				if (!SetUtils.isNullList(moreCompanyList))
				{

					for (String company : moreCompanyList)
					{
						ERPMoreCompanies morecompany = new ERPMoreCompanies();
						morecompany.setTemplateGuid(serviceGuid);
						morecompany.setCompanydh(company);
						morecompany.setCreateUserGuid(senInfo.getUserGuid());
						morecompany.setUpdateUserGuid(senInfo.getUserGuid());
						morecompany.setERPTypeFlag(serverType);
						sds.save(morecompany, "insertForAcl");// 保存设置的公司别值
					}
				}

				List<WorkflowTemplateActPerformerInfo> endNoticeList = erpYfPlmInfo.getEndNoticePersonList();
				if (!SetUtils.isNullList(endNoticeList))
				{
					for (WorkflowTemplateActPerformerInfo noticeUserInfo : endNoticeList)
					{
						noticeUserInfo.setCreateUserGuid(senInfo.getUserGuid());
						noticeUserInfo.setUpdateUserGuid(senInfo.getUserGuid());
						noticeUserInfo.setTemplateGuid(serviceGuid);
						sds.save(noticeUserInfo, "insertErpNoticer");// 保存结束后需要通知的人---成功通知人
					}
				}

				List<WorkflowTemplateActPerformerInfo> faildEndNoticeList = erpYfPlmInfo.getFaildEndNoticePersonList();
				if (!SetUtils.isNullList(faildEndNoticeList))
				{
					for (WorkflowTemplateActPerformerInfo faildnoticeUserInfo : faildEndNoticeList)
					{
						faildnoticeUserInfo.setCreateUserGuid(senInfo.getUserGuid());
						faildnoticeUserInfo.setUpdateUserGuid(senInfo.getUserGuid());
						faildnoticeUserInfo.setTemplateGuid(serviceGuid);
						sds.save(faildnoticeUserInfo, "insertErpFailNoticeUser");// 保存结束后需要通知的人---失败通知人
					}
				}

				List<WorkflowTemplateActPerformerInfo> canUserList = erpYfPlmInfo.getCanUseTemplatePersonList();
				if (!SetUtils.isNullList(canUserList))
				{
					for (WorkflowTemplateActPerformerInfo canUserInfo : canUserList)
					{
						canUserInfo.setCreateUserGuid(senInfo.getUserGuid());
						canUserInfo.setUpdateUserGuid(senInfo.getUserGuid());
						canUserInfo.setTemplateGuid(serviceGuid);
						sds.save(canUserInfo, "insertErpUser");// 保存可用的人
					}
				}
			}
			// 更新模板
			else
			{
				try
				{

					// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());//
					// 开始事务
					// 设置模板的创建人
					erpServiceInfo.setUpdateUserGuid(senInfo.getUserGuid());
					Map<String, Object> conditionMap = new HashMap<String, Object>();
					conditionMap.put("GUID", serviceGuid);// 服务的Guid
					// sds.update(ERPServiceConfig.class, conditionMap,
					// "update");// 更新服务
					sds.save(erpServiceInfo);
					Map<String, Object> atherconditionMap = new HashMap<String, Object>();
					atherconditionMap.put("TEMPLATEGUID", serviceGuid);
					sds.delete(ERPBOConfig.class, atherconditionMap, "deleteWithMasterfk");
					sds.delete(ERPMoreCompanies.class, atherconditionMap, "deleteForAcl");

					atherconditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPNOTICER);
					sds.deleteBy(WorkflowTemplateActPerformerInfo.class, atherconditionMap);

					atherconditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPNOTICERFAIL);
					sds.deleteBy(WorkflowTemplateActPerformerInfo.class, atherconditionMap);

					atherconditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPUSER);
					sds.deleteBy(WorkflowTemplateActPerformerInfo.class, atherconditionMap);

					for (ERPBOConfig erpBoInfo : erpBoconfig)
					{
						erpBoInfo.setTemplateGguid(serviceGuid);
						sds.save(erpBoInfo, "insert");// 保存ERPBOConfig
					}

					List<String> moreCompanyList = erpYfPlmInfo.getMoreCompanyList();
					if (!SetUtils.isNullList(moreCompanyList))
					{
						for (String company : moreCompanyList)
						{
							ERPMoreCompanies morecompany = new ERPMoreCompanies();
							morecompany.setTemplateGuid(serviceGuid);
							morecompany.setCompanydh(company);
							morecompany.setERPTypeFlag(serverType);
							morecompany.setUpdateUserGuid(senInfo.getUserGuid());
							sds.save(morecompany, "insertForAcl");// 保存多公司的值
						}
					}

					List<WorkflowTemplateActPerformerInfo> endNoticeList = erpYfPlmInfo.getEndNoticePersonList();
					if (!SetUtils.isNullList(endNoticeList))
					{
						for (WorkflowTemplateActPerformerInfo noticeUserInfo : endNoticeList)
						{
							noticeUserInfo.setUpdateUserGuid(senInfo.getUserGuid());
							noticeUserInfo.setTemplateGuid(serviceGuid);
							sds.save(noticeUserInfo, "insertErpNoticer");// 保存结束后需要通知的人---成功通知人
						}
					}

					List<WorkflowTemplateActPerformerInfo> faildEndNoticeList = erpYfPlmInfo.getFaildEndNoticePersonList();
					if (!SetUtils.isNullList(faildEndNoticeList))
					{
						for (WorkflowTemplateActPerformerInfo faildnoticeUserInfo : faildEndNoticeList)
						{
							faildnoticeUserInfo.setUpdateUserGuid(senInfo.getUserGuid());
							faildnoticeUserInfo.setTemplateGuid(serviceGuid);
							sds.save(faildnoticeUserInfo, "insertErpFailNoticeUser");// 保存结束后需要通知的人---失败通知人
						}
					}

					List<WorkflowTemplateActPerformerInfo> canUserList = erpYfPlmInfo.getCanUseTemplatePersonList();
					if (!SetUtils.isNullList(canUserList))
					{
						for (WorkflowTemplateActPerformerInfo canUserInfo : canUserList)
						{
							canUserInfo.setUpdateUserGuid(senInfo.getUserGuid());
							canUserInfo.setTemplateGuid(serviceGuid);
							sds.save(canUserInfo, "insertErpUser");// 保存可用的人
						}
					}
					// DataServer.getTransactionManager().commitTransaction();// 提交事务
				}
				catch (DynaDataException e)
				{
					// DataServer.getTransactionManager().rollbackTransaction();
					throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
				}

			}
			// 新建或者保存成功后，返回ERPYFAllConfig整个对象
			ERPServiceConfig erpServ = this.getERPServiceConfigbyServiceGuid(serviceGuid);
			ERPYFPLMClassConfig erpPLmclass = this.getERPYFPLMClassConfig(serviceGuid);

			ERPYFAllConfig erpallconfig = new ERPYFAllConfig();

			if (erpServ != null)
			{
				erpallconfig.setErpServiceConfig(erpServ);
				erpallconfig.setErpYFPLMClassConfig(erpPLmclass);
				return erpallconfig;
			}
			else
			{
				return null;

			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return allConfig;

	}

	/**
	 * @param serviceGuid
	 * @param userGuid
	 * @param isAdministrator
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPBOConfig> listERPBoConfigBySerGuidAndUseGuid(String serviceGuid, String userGuid, boolean isAdministrator) throws ServiceRequestException
	{
		List<ERPBOConfig> useBoConfig = new ArrayList<ERPBOConfig>();
		List<ERPBOConfig> erpBoConfigList = this.listERPBOConfigByServGuid(serviceGuid);
		// 如果是管理员身份，就可以取得当前服务下的所有Bo信息
		if (isAdministrator)
		{
			return erpBoConfigList;
		}
		else
		{
			SystemDataService sds = DataServer.getSystemDataService();
			UpperKeyMap conditionMap = new UpperKeyMap();
			conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceGuid);
			conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPUSER);
			try
			{
				List<WorkflowTemplateActPerformerInfo> useERPServerUseInfoList = sds
						.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(conditionMap));

				List<String> userGuidList = this.listErpUser(useERPServerUseInfoList);
				if (!SetUtils.isNullList(userGuidList))
				{
					for (String perfGuid : userGuidList)
					{
						// 若果非管理员身份，就从模板中找相应的人员，若包括执行者，就返回此服务下所有的BO信息
						if (userGuid.equals(perfGuid))
						{
							useBoConfig = erpBoConfigList;
							return useBoConfig;
						}
					}
				}
			}
			catch (DynaDataException e)
			{
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
			return null;
		}
	}

	/**
	 * @param serviceGuid
	 * @return
	 */
	protected List<String> listFacrotyByServiceGuid(String serviceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("TEMPLATEGUID", serviceGuid);
		try
		{
			List<ERPMoreCompanies> moreCompanyList = sds.query(ERPMoreCompanies.class, conditionMap, "selectForAcl");
			List<String> moreCompanyDHList = new ArrayList<String>();

			if (!SetUtils.isNullList(moreCompanyList))
			{
				for (ERPMoreCompanies company : moreCompanyList)
				{
					moreCompanyDHList.add(company.getCompanydh());
				}
				return moreCompanyDHList;
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return null;
	}

	/**
	 * @param serviceGuid
	 * @return
	 */
	public ERPServiceConfig getERPServiceConfigbyServiceGuid(String serviceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("GUID", serviceGuid);
		try
		{
			ERPServiceConfig erpServiceInfo = sds.queryObject(ERPServiceConfig.class, conditionMap);
			return erpServiceInfo;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param eRPType
	 * @return
	 */
	protected String getDefaultBOMTemplate(ERPServerType eRPType)
	{
		String fileName = this.getFileName(eRPType);
		Iterator<ConfigurableKVElementImpl> it = this.getXMLConfig(fileName + ".xml").iterator("root.parameters.param");
		ConfigurableKVElementImpl kv = null;
		while (it.hasNext())
		{
			kv = it.next();
			if (kv.getAttributeValue("name").equals("defaultBOMTemplate"))
			{
				return kv.getAttributeValue("value");
			}
		}
		return null;
	}

	private void createJob(ObjectGuid objectGuid, List<ERPBOConfig> erpListCanUse, Map<String, String> parameterMap, boolean isGoFlow, boolean isMerge,
			List<ObjectGuid> tempObjectGuidList) throws ServiceRequestException
	{
		FoundationObject fo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);

		this.stubService.getDCR().check(parameterMap.get("serviceConfigName"), fo);

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
		if ((classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) || classInfo.hasInterface(ModelInterfaceEnum.IECOM)) && !fo.getStatus().equals(SystemStatusEnum.RELEASE))
		{
			throw new ServiceRequestException("ID_APP_EXPORT_ERP_ECN_NOT_RLS", fo.getId());
		}
		if (isHasBathRS(objectGuid) && !fo.getStatus().equals(SystemStatusEnum.RELEASE))
		{
			throw new ServiceRequestException("ID_APP_ERPI_ERPIJOB_BRS_NOTREALSE", fo.getId());
		}
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		// 如果是BOMView,则直接返回
		if (this.isBomView(objectGuid))
		{
			return;
		}
		for (ERPBOConfig erpBo : erpListCanUse)
		{
			String boName = erpBo.getBoName();
			String bmGuid = erpBo.getBmGuid();
			if ("ALL".equalsIgnoreCase(bmGuid))
			{
				bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			}
			// 获取所有的BOInfo
			List<BOInfo> listBoInfo = this.stubService.getEMM().listAllSubBOInfoContain(boName, bmGuid);
			if (!SetUtils.isNullList(listBoInfo))
			{
				for (BOInfo bo : listBoInfo)
				{
					if (bo.getClassName().equalsIgnoreCase(objectGuid.getClassName()))
					{
						// 如果不是ECN对象
						if (this.isObjectECN(objectGuid)) // 如果是ECN对象
						{
							List<FoundationObject> ecoList = this.stubService.getUECS().getECOByECNAll(objectGuid);
							if (SetUtils.isNullList(ecoList))
							{
								throw new ServiceRequestException("ID_APP_EXPORT_ERP_ECN_NO_ECO", "no ECO In ECN ");
							}
							parameterMap.put("EC", UpdatedECSConstants.ECN);
							for (FoundationObject eco : ecoList)
							{
								this.createECOQueue(parameterMap, eco, isGoFlow, isMerge, erpListCanUse);
							}
						}
						else if (this.isObjectECO(objectGuid))// 如果是ECO对象
						{
							parameterMap.put("EC", UpdatedECSConstants.ECO);
							this.createECOQueue(parameterMap, fo, isGoFlow, isMerge, erpListCanUse);
						}
						else if (isHasBathRS(objectGuid))// 批量取替代申请单不参与合并队列，一个批量申请单就是一个队列
						{
							this.createQueue(parameterMap, objectGuid, isGoFlow);
						}
						else
						{
							if (isMerge)
							{
								tempObjectGuidList.add(objectGuid);
							}
							else
							{
								this.createQueue(parameterMap, objectGuid, isGoFlow);
							}
						}

						return;
					}
				}
			}
		}
	}

	private void createECOQueue(Map<String, String> parameterMap, FoundationObject eco, boolean isGoFlow, boolean isMerge, List<ERPBOConfig> erpListCanUse)
			throws ServiceRequestException
	{
		if (eco.get("LIFECYCLEPHASE$TITLE").toString().contains("Canceled"))
		{
			return;
		}
		boolean flag = false;
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(eco.getObjectGuid().getClassGuid());
		if (classInfo != null)
		{
			// 批量ECO
			if (classInfo.hasInterface(ModelInterfaceEnum.IECOM) && classInfo.hasInterface(ModelInterfaceEnum.IBatchForEc))
			{
				flag = true;
			}
		}
		ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(eco.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEMAFTER$);

		if (!flag)
		{
			parameterMap.put("ChangeType", "Normal");
			parameterMap.put("BOMChange", "Y");
			if (viewObject != null && viewObject.getObjectGuid() != null)
			{
				List<FoundationObject> listECP = ((BOASImpl) this.stubService.getBOAS()).getRelationStub()
						.listFoundationObjectOfRelation(viewObject.getObjectGuid(), null, null, null, false);
				if (!SetUtils.isNullList(listECP))
				{
					for (FoundationObject ecpfo : listECP)
					{
						try
						{
							ClassInfo info = this.stubService.getEMM().getClassByName(ecpfo.getObjectGuid().getClassName());
							if (info.hasInterface(ModelInterfaceEnum.IItem))
							{
								List<FoundationObject> eciList = this.stubService.getServiceInstance(UECS.class)
										.getBomECIByECO(null, null, parameterMap.get("templateName"), eco.getObjectGuid());
								if (SetUtils.isNullList(eciList))
								{
									parameterMap.put("BOMChange", "N");
								}
								// this.createJob(ecpfo.getObjectGuid(), erpListCanUse, parameterMap, isGoFlow,
								// isMerge);
								this.createQueue(parameterMap, ecpfo.getObjectGuid(), isGoFlow);
							}
						}
						catch (ServiceNotFoundException e)
						{
							throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
						}
					}
				}
			}
		}
		else
		{
			parameterMap.put("ChangeType", "Batch");
			parameterMap.put("BOMChange", "Y");
			if (viewObject != null && viewObject.getObjectGuid() != null)
			{
				List<FoundationObject> listECP = ((BOASImpl) this.stubService.getBOAS()).getRelationStub()
						.listFoundationObjectOfRelation(viewObject.getObjectGuid(), null, null, null, false);
				if (!SetUtils.isNullList(listECP))
				{
					List<ObjectGuid> tempList = new ArrayList<ObjectGuid>();
					for (int i = 0; i < listECP.size(); i++)
					{
						FoundationObject ecpfo = listECP.get(i);
						if (!isMerge)
						{
							this.createQueue(parameterMap, ecpfo.getObjectGuid(), isGoFlow);
						}
						else
						{
							tempList.add(ecpfo.getObjectGuid());
							if (tempList.size() % objectBatchSize == 0 || i == (listECP.size() - 1))
							{
								this.createMergeQueue(parameterMap, tempList, isGoFlow);
								tempList.clear();
							}
						}
					}
				}
			}
		}

	}

	private void createQueue(Map<String, String> parameterMap, ObjectGuid objectGuid, boolean isGoFlow) throws ServiceRequestException
	{
		Queue queueJob = new Queue();
		ERPServiceConfig conf = this.stubService.getERPServiceConfigbyServiceGuid(parameterMap.get("serverGuid"));
		String templateName = conf.getName();
		if (templateName != null && templateName.startsWith("ERPpriority"))
		{
			queueJob.setName("ERPpriority");
			queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.ERPpriorityJob");
		}
		else
		{
			queueJob.setName("ERP");
			queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.ERPIJob");
		}
		// 设置字段
		queueJob.setFielda(objectGuid.getGuid());// fieldA:Item
		queueJob.setFieldb(objectGuid.getClassGuid());// fieldB:Item$class
		queueJob.setFieldc(parameterMap.get("serverGuid"));// fieldC:ERPSerGuid
		queueJob.setOwneruserGuid(parameterMap.get("userGuid"));

		queueJob.setFieldd(parameterMap.get("actionSchema"));// fieldD:集成方案
		queueJob.setFielde(parameterMap.get("templateName"));// fieldE:BOMTemplateName
		queueJob.setFieldf(parameterMap.get("factoryName"));// fieldF:ERPFactory
		queueJob.setFieldg(parameterMap.get("lang"));// 获取执行用户当前登录用的语言 fieldG:LANG
		queueJob.setFieldh(String.valueOf(System.nanoTime()));// 记录每笔资料的ID:使用系统的时间
		queueJob.setFieldi(parameterMap.get("EC"));
		queueJob.setFieldj(parameterMap.get(UpdatedECSConstants.ChangeType));
		queueJob.setFieldk(parameterMap.get("BOMChange"));
		queueJob.setIsSinglethRead("Y");// ERP传输必须是单线程，否则会产生线程安全问题
		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.ERP);
		((JSSImpl) this.stubService.getJSS()).createJob(queueJob);
	}

	/**
	 * 判断是否是工程变更的类型
	 *
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean isObjectECN(ObjectGuid objectGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		// 假如该实例为工程变更
		if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN))
		{
			return true;
		}

		return false;
	}

	/**
	 * 判断是否是工程变更的类型
	 *
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean isObjectECO(ObjectGuid objectGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		// 假如该实例为工程变更
		if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IECOM))
		{
			return true;
		}

		return false;
	}

	private boolean isBomView(ObjectGuid objectGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		// 判断是否是BOMView
		if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IBOMView))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param serviceGuid
	 * @return
	 */
	public ERPYFPLMClassConfig getERPYFPLMClassConfig(String serviceGuid) throws ServiceRequestException
	{
		if (StringUtils.isNullString(serviceGuid))
		{
			return null;
		}
		try
		{
			List<ERPBOConfig> erpBoList = this.listERPBOConfigByServGuid(serviceGuid);
			List<WorkflowTemplateActPerformerInfo> noticeUserList = this.getTemplateEndNoticeUsersByServGuid(serviceGuid);
			List<WorkflowTemplateActPerformerInfo> canuseList = this.listTemplateCanUseUsersByServGuid(serviceGuid);
			List<WorkflowTemplateActPerformerInfo> faileEndUserList = this.listFialdEndNoticeByServGuid(serviceGuid);
			List<ERPMoreCompanies> moreCompanyList = this.listDefaultMoreCompanyByServGuid(serviceGuid);
			List<String> factoryList = new ArrayList<String>();
			if (!SetUtils.isNullList(moreCompanyList))
			{
				for (ERPMoreCompanies company : moreCompanyList)
				{
					factoryList.add(company.getCompanydh());
				}
			}
			ERPYFPLMClassConfig erpYfPlmClass = new ERPYFPLMClassConfig();
			erpYfPlmClass.setBoinfoList(erpBoList);
			erpYfPlmClass.setCanUseTemplatePersonList(canuseList);
			erpYfPlmClass.setEndNoticePersonList(noticeUserList);
			erpYfPlmClass.setFaildEndNoticePersonList(faileEndUserList);
			erpYfPlmClass.setMoreCompanyList(factoryList);
			return erpYfPlmClass;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param serviceGuid
	 * @return
	 */
	public List<ERPBOConfig> listERPBOConfigByServGuid(String serviceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("TEMPLATEGUID", serviceGuid);
		conditionMap.put(BOMTemplateInfo.GROUPBMGUID, this.stubService.getUserSignature().getLoginGroupBMGuid());
		try
		{
			List<ERPBOConfig> erpBOConfigList = sds.query(ERPBOConfig.class, conditionMap);

			if (!SetUtils.isNullList(erpBOConfigList))
			{
				return erpBOConfigList;
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return null;
	}

	/**
	 * @param selectServerName
	 * @param serverConfigName
	 * @return
	 * @throws ServiceRequestException
	 */
	public ERPServiceConfig getERPServiceConBySerNandSerConN(String selectServerName, String serverConfigName) throws ServiceRequestException
	{
		List<ERPServiceConfig> erpSerconfigList = this.listERPServiceConfig(selectServerName);
		if (!SetUtils.isNullList(erpSerconfigList))
		{
			for (ERPServiceConfig erpServConfig : erpSerconfigList)
			{
				if (erpServConfig.getConfigName().equals(serverConfigName))
				{
					return erpServConfig;
				}
			}
		}
		return null;
	}

	protected List<ERPServiceConfig> listERPServiceConfig(String selectServerName) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("ERPTYPE", selectServerName);
		try
		{
			List<ERPServiceConfig> erpServiceInfoList = sds.query(ERPServiceConfig.class, conditionMap);
			if (SetUtils.isNullList(erpServiceInfoList))
			{
				return null;
			}
			return erpServiceInfoList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param selectServerName
	 * @return
	 */
	public ERPServiceConfig getERPServiceConfigDefaultvalues(String selectServerName) throws ServiceRequestException

	{
		ERPServiceConfig erpService = new ERPServiceConfig();
		if (selectServerName.equals(ERPServerType.ERPYF.name()))
		{
			erpService.setCrossIntegrate("N");
			erpService.setERPServerSelected(selectServerName);

			erpService.setERPServerIP("192.168.101.75");
			erpService.setERPServerName("soap/IYiFeiGatewayEx");
			erpService.setERPServerPort("8082");
			erpService.setNamePlace("http://tempuri.org/");
			erpService.setSoapServer("IYiFeiGatewayPort");
			erpService.setServerName("IYiFeiGatewayservice");
			erpService.setErpServerAddress("http://" + erpService.getERPServerIP() + ":" + erpService.getERPServerPort() + "/" + erpService.getERPServerName());
		}
		else if (selectServerName.equals(ERPServerType.ERPWORKFLOW.name()))
		{
			erpService.setCrossIntegrate("Y");
			erpService.setERPServerSelected(selectServerName);
			erpService.setERPServerIP("192.168.101.83");
			erpService.setERPServerName("WFPLM/WFPLMService.asmx");
			erpService.setERPServerPort("80");
			erpService.setErpServerAddress("http://" + erpService.getERPServerIP() + ":" + erpService.getERPServerPort() + "/" + erpService.getERPServerName());
		}
		else if (selectServerName.equals(ERPServerType.ERPTIPTOP.name()) || selectServerName.equals(ERPServerType.ERPT100.name()) || selectServerName
				.equals(ERPServerType.ERPT100DB.name()))
		{

			erpService.setCrossIntegrate("Y");
			erpService.setERPServerSelected(selectServerName);
			erpService.setERPServerIP("10.40.40.30");
			erpService.setERPServerName("gas2/ws/r/aws_ttsrv2_top3");
			erpService.setERPServerPort("80");
			erpService.setErpServerAddress("http://" + erpService.getERPServerIP() + ":" + erpService.getERPServerPort() + "/" + erpService.getERPServerName());
		}
		else if (selectServerName.equals(ERPServerType.ERPE10.name()) || selectServerName.equals(ERPServerType.ERPSM.name()))
		{
			erpService.setCrossIntegrate("Y");
			erpService.setERPServerSelected(selectServerName);
			erpService.setERPServerIP("");
			erpService.setERPServerName("");
			erpService.setERPServerPort("80");
			erpService.setErpServerAddress("");
		}
		return erpService;
	}

	/**
	 * @return
	 */
	public List<ERPServerType> listERPType() throws ServiceRequestException
	{
		return Arrays.asList(ERPServerType.values());
	}

	/**
	 * @return
	 */
	public List<String> listERPServiceConfigName() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<ERPServiceConfig> erpServiceTemplateInfoList = sds.query(ERPServiceConfig.class, null);
			if (SetUtils.isNullList(erpServiceTemplateInfoList))
			{
				return null;
			}
			Set<String> hashSet = new HashSet<String>();// 去掉重复的值
			for (ERPServiceConfig erpTemplate : erpServiceTemplateInfoList)
			{
				hashSet.add(erpTemplate.getConfigName());
			}
			return Arrays.asList(hashSet.toArray(new String[hashSet.size()]));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param serviceConfigGuid
	 * @return
	 */
	protected void deleteERPConfig(String serviceConfigGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			sds.delete(ERPServiceConfig.class, serviceConfigGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param serviceConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<WorkflowTemplateActPerformerInfo> listTemplateCanUseUsersByServGuid(String serviceConfigGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UpperKeyMap conditionMap = new UpperKeyMap();
		conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceConfigGuid);
		conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPUSER);
		try
		{
			List<WorkflowTemplateActPerformerInfo> useERPServerUserInfo = sds.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<>(conditionMap));
			if (!SetUtils.isNullList(useERPServerUserInfo))
			{
				for (WorkflowTemplateActPerformerInfo perf : useERPServerUserInfo)
				{
					perf.put("CREATEUSERNAME", this.getUserName(sds, perf.getCreateUserGuid()));
					perf.put("UPDATEUSERNAME", this.getUserName(sds, perf.getUpdateUserGuid()));

					String perfName = null;
					switch (perf.getPerfType())
					{
					case USER:
						perfName = this.getUserName(sds, perf.getPerfGuid());
						break;
					case GROUP:
						Group group = sds.get(Group.class, perf.getPerfGuid());
						perfName = group == null ? null : group.getGroupName();
						break;
					case RIG:
						break;
					case ROLE:
						Role role = sds.get(Role.class, perf.getPerfGuid());
						perfName = role == null ? null : role.getRoleName();
						break;
					}
					perf.setName(perfName);
				}
			}
			return useERPServerUserInfo;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private String getUserName(SystemDataService sds, String userGuid)
	{
		User user = sds.get(User.class, userGuid);
		return user == null ? null : user.getUserName();
	}

	/**
	 * @param serviceGuid
	 * @param userList
	 * @throws ServiceRequestException
	 */
	public void saveEndNoticeUsers(String serviceGuid, List<WorkflowTemplateActPerformerInfo> userList) throws ServiceRequestException
	{
		// String sessionId = this.stubService.getSignature().getCredential();
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("TEMPLATEGUID", serviceGuid);
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			sds.delete(WorkflowTemplateActPerformerInfo.class, conditionMap, "deleteNoticerByTemplateguid");
			if (!SetUtils.isNullList(userList))
			{
				for (WorkflowTemplateActPerformerInfo userInfo : userList)
				{
					userInfo.setTemplateGuid(serviceGuid);
					sds.save(userInfo, "insertErpNoticer");

				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
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

	/**
	 * @param serviceConfigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listEndNoticeUsersByServGuid(String serviceConfigGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UpperKeyMap conditionMap = new UpperKeyMap();
		conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceConfigGuid);
		conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPNOTICER);

		try
		{
			List<WorkflowTemplateActPerformerInfo> useERPServerUseInfoList = sds.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<>(conditionMap));

			return this.listErpUser(useERPServerUseInfoList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<String> listErpUser(List<WorkflowTemplateActPerformerInfo> useERPServerUseInfoList) throws ServiceRequestException
	{
		List<User> userList = new ArrayList<User>();
		List<String> userGuidList = new ArrayList<String>();
		if (!SetUtils.isNullList(useERPServerUseInfoList))
		{
			for (WorkflowTemplateActPerformerInfo performer : useERPServerUseInfoList)
			{
				switch (performer.getPerfType())
				{
				case USER:
					User user = this.stubService.getAAS().getUser(performer.getPerfGuid());
					if (user != null)
					{
						userList.add(user);
					}
					break;
				case GROUP:
					userList = this.stubService.getAAS().listUserInGroupAndSubGroup(performer.getPerfGuid());
					break;
				case RIG:
					userList = this.stubService.getAAS().listUserByRoleInGroup(performer.getPerfGuid());
					break;
				case ROLE:
					List<RIG> rigList = this.stubService.getAAS().listRIGByRoleGuid(performer.getPerfGuid());
					if (!SetUtils.isNullList(rigList))
					{
						for (RIG rig : rigList)
						{
							List<User> tempList = this.stubService.getAAS().listUserByRoleInGroup(rig.getGuid());
							if (!SetUtils.isNullList(tempList))
							{
								userList.addAll(tempList);
							}
						}
					}
					break;
				}
			}
		}

		if (!SetUtils.isNullList(userList))
		{
			for (User user : userList)
			{
				if (user.isActive() && !userGuidList.contains(user.getGuid()))
				{
					userGuidList.add(user.getGuid());
				}
			}
		}
		return userGuidList;
	}

	/**
	 * @param serviceGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<WorkflowTemplateActPerformerInfo> getTemplateEndNoticeUsersByServGuid(String serviceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UpperKeyMap conditionMap = new UpperKeyMap();
		conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceGuid);
		conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPNOTICER);
		try
		{
			List<WorkflowTemplateActPerformerInfo> noticeList = sds
					.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(conditionMap));
			return noticeList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param serName
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ERPServiceConfig> listERPSerConfigNameByUseGuid(String serName, String userGuid) throws ServiceRequestException
	{
		List<ERPServiceConfig> erpSerconfigList = this.listERPServiceConfig(serName);
		List<ERPServiceConfig> erpSerList = new ArrayList<ERPServiceConfig>();
		try
		{
			if (erpSerconfigList != null && erpSerconfigList.size() != 0)
			{
				for (ERPServiceConfig serviceConfig : erpSerconfigList)
				{
					SystemDataService sds = DataServer.getSystemDataService();
					UpperKeyMap conditionMap = new UpperKeyMap();
					conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceConfig.getGuid());
					conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPUSER);

					List<WorkflowTemplateActPerformerInfo> useERPServerUseInfoList = sds
							.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(conditionMap));
					List<String> perfGuidList = this.listErpUser(useERPServerUseInfoList);
					if (!SetUtils.isNullList(perfGuidList))
					{
						for (String perfGuid : perfGuidList)
						{
							if (!perfGuid.equalsIgnoreCase(userGuid))
							{
								continue;
							}
							else
							{
								erpSerList.add(serviceConfig);
							}
						}
					}
				}
				return erpSerList;
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param selectServerName
	 * @param serverConfigName
	 * @throws ServiceRequestException
	 */
	protected void deleteERPAllConfigBySNandCN(String selectServerName, String serverConfigName) throws ServiceRequestException
	{
		ERPServiceConfig erpser = this.getERPServiceConBySerNandSerConN(selectServerName, serverConfigName);
		if (erpser == null)
		{
			throw new ServiceRequestException("ERPServiceConfig is null!");
		}
		String serGuid = erpser.getGuid();
		this.deleteERPConfig(serGuid);
	}

	/**
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPServiceConfig> listServices() throws ServiceRequestException
	{
		UserSignature userSignature = this.stubService.getUserSignature();
		String groupGuid = userSignature.getLoginGroupGuid();
		String userGuid = userSignature.getUserGuid();
		boolean isAdministrator = this.isAdministrator();
		return this.listServices(groupGuid, userGuid, isAdministrator);
	}

	protected List<ERPServiceConfig> listServices(String groupGuid, String userGuid, boolean isAdministrator) throws ServiceRequestException
	{
		List<ERPServiceConfig> erpServiceConfig;
		try
		{
			erpServiceConfig = this.listERPConfigForNoClass(groupGuid, null);
			// 当前执行者为管理员身份,Map中只需两个参数
			if (!isAdministrator)
			{
				this.listERPServiceConfigByNoticeper(erpServiceConfig, userGuid);
			}
			return erpServiceConfig;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<ERPServiceConfig> listERPConfigForNoClass(String groupGuid, Boolean isInUse) throws ServiceRequestException
	{
		Map<String, Object> param = new HashMap<>();
		param.put("GROUPGUID", groupGuid);
		if (isInUse != null)
		{
			param.put("ISINUSE", BooleanUtils.getBooleanStringYN(isInUse));
		}
		List<ERPServiceConfig> allERPConfigList = DataServer.getSystemDataService().query(ERPServiceConfig.class, param, "listERPConfigForNoClass");
		if (!SetUtils.isNullList(allERPConfigList))
		{
			for (Iterator<ERPServiceConfig> cIt = allERPConfigList.iterator(); cIt.hasNext(); )
			{
				ERPServiceConfig config = cIt.next();
				String bmGuid = config.getBmGuid();
				if (!"ALL".equals(bmGuid) && !this.stubService.getUserSignature().getLoginGroupBMGuid().equals(bmGuid))
				{
					cIt.remove();
				}
			}
		}
		return allERPConfigList;
	}

	/**
	 * @param serverGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPMoreCompanies> listDefaultMoreCompanyByServGuid(String serverGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("TEMPLATEGUID", serverGuid);

		try
		{
			List<ERPMoreCompanies> companyList = sds.query(ERPMoreCompanies.class, conditionMap, "selectForAcl");

			if (!SetUtils.isNullList(companyList))
			{
				return companyList;
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return null;
	}

	/**
	 * @param serviceGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<WorkflowTemplateActPerformerInfo> listFialdEndNoticeByServGuid(String serviceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UpperKeyMap conditionMap = new UpperKeyMap();
		conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceGuid);
		conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPNOTICERFAIL);

		try
		{
			List<WorkflowTemplateActPerformerInfo> faildUserERPServerUseInfoList = sds
					.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<>(conditionMap));
			if (!SetUtils.isNullList(faildUserERPServerUseInfoList))
			{
				for (WorkflowTemplateActPerformerInfo perf : faildUserERPServerUseInfoList)
				{
					perf.put("CREATEUSERNAME", this.getUserName(sds, perf.getCreateUserGuid()));
					perf.put("UPDATEUSERNAME", this.getUserName(sds, perf.getUpdateUserGuid()));

					String perfName = null;
					switch (perf.getPerfType())
					{
					case USER:
						perfName = this.getUserName(sds, perf.getPerfGuid());
						break;
					case GROUP:
						Group group = sds.get(Group.class, perf.getPerfGuid());
						perfName = group == null ? null : group.getGroupName();
						break;
					case RIG:
						break;
					case ROLE:
						Role role = sds.get(Role.class, perf.getPerfGuid());
						perfName = role == null ? null : role.getRoleName();
						break;
					}
					perf.setName(perfName);
				}
			}

			return faildUserERPServerUseInfoList;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected ERPTransferStub<?> getStub(ERPServerType serverType) throws Exception
	{
		if (serverType == ERPServerType.ERPTIPTOP)
		{
			return this.getErpYTStub();
		}
		if (serverType == ERPServerType.ERPWORKFLOW)
		{
			return this.getErpWFStub();
		}
		if (serverType == ERPServerType.ERPYF)
		{
			return this.getErpYFStub();
		}
		if (serverType == ERPServerType.ERPE10)
		{
			return this.getErpE10Stub();
		}
		if (serverType == ERPServerType.ERPSM)
		{
			return this.getErpSMStub();
		}
		if (serverType == ERPServerType.ERPT100)
		{
			if (this.isOldT100())
			{
				return this.getErpT100OldStub();
			}
			else
			{
				return this.getErpT100Stub();
			}
		}
		if (serverType == ERPServerType.ERPT100DB)
		{
			return this.getErpT100DBStub();
		}
		throw new IllegalArgumentException("no ERP Configuration for " + serverType.name());
	}

	private boolean isOldT100()
	{
		ServiceDefinition definition = this.serviceContext.getServiceConfig().getServiceDefinition("ERPI");
		String t100IsOldStr = definition.getInitParameter("t100_is_old");
		return "true".equalsIgnoreCase(t100IsOldStr);
	}

	/**
	 * @param serverType
	 * @param morecompanyList
	 * @throws ServiceRequestException
	 */
	protected void saveMoreCompany(String serverType, List<ERPMoreCompanies> morecompanyList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("ERPTYPEFLAG", serverType);
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			sds.delete(ERPMoreCompanies.class, conditionMap, "deleteForAll");
			if (!SetUtils.isNullList(morecompanyList))
			{
				for (ERPMoreCompanies company : morecompanyList)
				{
					sds.save(company, "insertForAll");// 保存多公司
				}
//				DataServer.getTransactionManager().commitTransaction();
			}
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
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

	private ConfigurableKVElementImpl getXMLConfig(String fileName)
	{
		if (StringUtils.isNullString(fileName))
		{
			return null;
		}

		File file = new File(EnvUtils.getConfRootPath() + "conf" + File.separator + fileName);

		if (!file.exists())
		{
			return null;
		}

		fileName = fileName.toLowerCase();
		if (ConfigurableMap.get(fileName) == null || XMLMap.get(fileName) == null || XMLMap.get(fileName).isChanged(file.lastModified()))
		{
			ConfigurableMap.remove(fileName);
			ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
			configLoader.setConfigFile(file);
			configLoader.load();
			ConfigurableKVElementImpl kv = configLoader.getConfigurable();
			ConfigurableMap.put(fileName, kv);
		}

		return ConfigurableMap.get(fileName);
	}

	/**
	 * @param selectServerName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<String> listERPServiceConfigNameByName(String selectServerName) throws ServiceRequestException
	{
		List<ERPServiceConfig> erpServiceInfoList = this.listERPServiceConfig(selectServerName);
		List<String> nameList = new ArrayList<String>();
		if (!SetUtils.isNullList(erpServiceInfoList))
		{
			for (ERPServiceConfig erpTemplate : erpServiceInfoList)
			{
				nameList.add(erpTemplate.getConfigName());
			}
			Set<String> hashSet = new HashSet<String>(nameList);// 去掉重复的值
			nameList.clear();
			nameList.addAll(hashSet);
			return nameList;
		}
		return null;
	}

	/**
	 * @param serverType
	 * @return
	 */
	protected List<String> getSchemaName(ERPServerType serverType) throws Exception
	{
		String fileName = this.getFileName(serverType);
		List<String> schemaList = new ArrayList<String>();
		Iterator<ConfigurableKVElementImpl> it = this.getXMLConfig(fileName + ".xml").iterator("root.schemas.schema");
		ConfigurableKVElementImpl kv = null;
		while (it.hasNext())
		{
			kv = it.next();
			schemaList.add(kv.getAttributeValue("name"));
		}
		return schemaList;
	}

	/**
	 * @param serviceGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<String> listNotifyUsersWhenFailed(String serviceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		UpperKeyMap conditionMap = new UpperKeyMap();
		conditionMap.put(WorkflowTemplateActPerformerInfo.TEMPLATEGUID, serviceGuid);
		conditionMap.put(WorkflowTemplateActPerformerInfo.NOTICETYPE, WorkflowTemplateActPerformerInfo.NOTICETYPE_ERPNOTICERFAIL);

		try
		{
			List<WorkflowTemplateActPerformerInfo> faildUserERPServerUseInfoList = sds
					.listFromCache(WorkflowTemplateActPerformerInfo.class, new FieldValueEqualsFilter<WorkflowTemplateActPerformerInfo>(conditionMap));
			List<String> userGuidList = this.listErpUser(faildUserERPServerUseInfoList);

			return userGuidList;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * @param serverType
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPMoreCompanies> listFacrotyFromDataBase(String serverType) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("ERPTYPEFLAG", serverType);
			List<ERPMoreCompanies> moreCompanyList = sds.query(ERPMoreCompanies.class, conditionMap, "selectForAll");
			return moreCompanyList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 根据操作方法的id从xml文件中取得realtime,category和name属性
	 *
	 * @param serverType
	 * @param id
	 * @return
	 */
	protected Map<String, String> getOperationAttribute(ERPServerType serverType, String id)
	{
		Iterator<ConfigurableKVElementImpl> it = this.getXMLConfig(this.getFileName(serverType) + ".xml").iterator("root.operations.operation");
		ConfigurableKVElementImpl tempKV = null;
		while (it.hasNext())
		{
			tempKV = it.next();
			if (tempKV.getAttributeValue("id").equals(id))
			{
				break;
			}
			tempKV = null;
		}
		if (tempKV == null)
		{
			return Collections.emptyMap();
		}
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("category", tempKV.getAttributeValue("category"));
		attrMap.put("name", tempKV.getAttributeValue("name"));
		attrMap.put("realtime", tempKV.getAttributeValue("realtime"));
		attrMap.put("id", tempKV.getAttributeValue("id"));
		return attrMap;
	}

	/**
	 * @param serverType
	 * @return
	 */
	protected String getFileName(ERPServerType type)
	{
		if (type == ERPServerType.ERPTIPTOP)
		{
			return "ytconf";
		}
		else if (type == ERPServerType.ERPWORKFLOW)
		{
			return "wfconf";
		}
		else if (type == ERPServerType.ERPYF)
		{
			return "yfconf";
		}
		else if (type == ERPServerType.ERPE10)
		{
			return "e10conf";
		}
		else if (type == ERPServerType.ERPSM)
		{
			return "smconf";
		}
		else if (type == ERPServerType.ERPT100)
		{
			return "T100conf";
		}
		else if (type == ERPServerType.ERPT100DB)
		{
			return "T100_DBconf";
		}
		else if (type == ERPServerType.ERPT100DB)
		{
			return "T100_DBconf";
		}
		throw new IllegalArgumentException(type + " is not supported(dyna.app.service.brs.erpi.ERPStub#getFileName)");
	}

	/**
	 * @param serverType
	 * @param name
	 * @return
	 */
	public ERPSchema getSchemaByName(ERPQuerySchemaParameterObject schemaParameterObj) throws ServiceRequestException
	{
		String fileName = this.getFileName(schemaParameterObj.getERPType());
		ConfigurableKVElementImpl kv = this.getXMLConfig(fileName + ".xml");
		Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.schemas.schema");
		ConfigurableKVElementImpl tempKV = null;
		ERPSchema schema = null;
		while (firstIt.hasNext())
		{
			tempKV = firstIt.next();
			if (schemaParameterObj.getSchemaName().equals(tempKV.getAttributeValue("name")))
			{
				schema = new ERPSchema();
				schema.setName(schemaParameterObj.getSchemaName());
				schema.setExpandBOM("true".equalsIgnoreCase(tempKV.getAttributeValue("expandBOM")));
				schema.setExportRSItem("true".equalsIgnoreCase(tempKV.getAttributeValue("exportRSItem")));
				schema.setExpandClassification("true".equalsIgnoreCase(tempKV.getAttributeValue("expandClassification")));
				schema.setExportAllData("true".equalsIgnoreCase(tempKV.getAttributeValue("exportAllData")));
				if (tempKV.getAttributeValue("include") == null || Arrays.asList(tempKV.getAttributeValue("include").split(",")).size() == 0)
				{
					throw new IllegalArgumentException("no operation for schema: " + schemaParameterObj.getSchemaName() + " in " + fileName + ".xml");
				}
				schema.setOperationList(Arrays.asList(tempKV.getAttributeValue("include").split("\\s*,\\s*")));
				if (tempKV.getAttributeValue("expandGet") != null && Arrays.asList(tempKV.getAttributeValue("expandGet").split(",")).size() > 0)
				{
					schema.setContentList(Arrays.asList(tempKV.getAttributeValue("expandGet").split("\\s*,\\s*")));
				}
				break;
			}
		}

		if (schemaParameterObj.isEC())
		{
			Iterator<ConfigurableKVElementImpl> secondIt = kv.iterator("root.patch.EC");
			while (secondIt.hasNext())
			{
				tempKV = secondIt.next();
				List<String> schemaList = Arrays.asList(tempKV.getAttributeValue("sourceSchema").split("\\s*,\\s*"));
				if (schemaList.contains(schema.getName()))
				{
					Iterator<ConfigurableKVElementImpl> filterIt = tempKV.iterator("filter");
					while (filterIt.hasNext())
					{
						ConfigurableKVElementImpl filterKV = filterIt.next();
						if (schemaParameterObj.getECChangeType().equals(filterKV.getAttributeValue("changeType")) && schemaParameterObj.isBomChange() == "true"
								.equalsIgnoreCase(filterKV.getAttributeValue("BOMchange")))
						{
							schema.setOperationList(Arrays.asList(filterKV.getAttributeValue("operation").split("\\s*,\\s*")));
							if (!StringUtils.isNullString(filterKV.getAttributeValue("expandBOM")))
							{
								schema.setExpandBOM("true".equalsIgnoreCase(filterKV.getAttributeValue("expandBOM")));
							}
							if (!StringUtils.isNullString(filterKV.getAttributeValue("exportRSItem")))
							{
								schema.setExportRSItem("true".equalsIgnoreCase(filterKV.getAttributeValue("exportRSItem")));
							}
							// guard clause
							return schema;
						}
					}
				}
			}
		}
		return schema;
	}

	public void saveERPTransferLog(ERPTransferLog log) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		// String txId = this.stubService.getFixedTransactionId();
		try
		{
			// DataServer.getTransactionManager().startTransaction(txId);
			// sds.startTransaction(txId);
			sds.save(log, "insert");
			// DataServer.getTransactionManager().commitTransaction();
			// sds.commitTransaction(txId);
		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			// sds.rollbackTransaction(txId);
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{

		}
	}

	public List<ERPTransferLog> getERPTransferLog(Map<String, Object> param) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		DSCommonService ds = DataServer.getDSCommonService();
		List<ERPTransferLog> logList = new ArrayList<ERPTransferLog>();
		try
		{
			if (IntegrateERP.ERP_BOM.equals(param.get(ERPTransferLog.category)))
			{
				List<ERPTransferLog> classList = sds.query(ERPTransferLog.class, param, "selectClassByBom");
				if (!SetUtils.isNullList(classList))
				{
					for (ERPTransferLog c : classList)
					{
						param.put("END1TABLENAME", ds.getTableName((String) c.get("END1$CLASS")));
						param.put("tablename", ds.getTableName(c.getTargetClassGuid()));
						List<ERPTransferLog> tmpList = sds.query(ERPTransferLog.class, param, "selectByBom");
						if (!SetUtils.isNullList(tmpList))
						{
							logList.addAll(tmpList);
						}
					}
				}
			}
			else
			{
				List<ERPTransferLog> classList = sds.query(ERPTransferLog.class, param, "selectClassByItem");
				if (!SetUtils.isNullList(classList))
				{
					for (ERPTransferLog c : classList)
					{
						param.put("CLASSGUID", c.getTargetClassGuid());
						param.put("tablename", ds.getTableName(c.getTargetClassGuid()));
						List<ERPTransferLog> tmpList = sds.query(ERPTransferLog.class, param, "selectByItem");
						if (!SetUtils.isNullList(tmpList))
						{
							logList.addAll(tmpList);
						}
					}
				}
			}
			return logList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		finally
		{

		}
	}

	public List<ERPTransferLog> getERPTransferLog4UI(Map<String, String> param)
	{
		return null;
	}

	/**
	 * 是否显示BOM模板 <br/>
	 * 用到多个if，重构时可以考虑用子类实现，但前提是Operation不能为Enum形式
	 *
	 * @param schema
	 * @param serverType
	 * @return
	 */
	public boolean showBOMTemplate(ERPSchema schema, ERPServerType serverType)
	{
		// 配置BOM无需选择BOM模板
		if (schema.isExpandBOM() && !schema.getOperationList().contains("createConfigBOM"))
		{
			return true;
		}
		if (serverType == ERPServerType.ERPTIPTOP)
		{
			if (schema.getOperationList().contains(ERPYTOperationEnum.CreatePLMBOM.getId()))
			{
				return true;
			}
		}
		if (serverType == ERPServerType.ERPWORKFLOW)
		{
			if (schema.getOperationList().contains(ERPWFOperationEnum.PLMTODGBOM2.getId()))
			{
				return true;
			}
		}
		if (serverType == ERPServerType.ERPYF)
		{
			if (schema.getOperationList().contains(ERPYFOperationEnum.CreateBOM.getId()))
			{
				return true;
			}
		}
		if (serverType == ERPServerType.ERPE10)
		{
			if (schema.getOperationList().contains(ERPE10OperationEnum.CreateBOM.getId()))
			{
				return true;
			}
		}
		if (serverType == ERPServerType.ERPSM)
		{
			if (schema.getOperationList().contains(ERPSMOperationEnum.PLMTODGBOM2.getId()))
			{
				return true;
			}
		}
		if (serverType == ERPServerType.ERPT100)
		{
			if (schema.getOperationList().contains(ERPT100OperationEnum.PLMBOMDataCreate.getId()))
			{
				return true;
			}
		}
		if (serverType == ERPServerType.ERPT100DB)
		{
			if (schema.getOperationList().contains(ERPT100DBOperationEnum.PLMBOMDataCreate.getId()))
			{
				return true;
			}
		}
		return false;
	}

	/***
	 * 获取物料
	 *
	 * @param id
	 * @return 若无此物料返回null
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getItem(String id) throws ServiceRequestException
	{
		String rootItemClass = this.getRootClassByInterface(ModelInterfaceEnum.IItem);
		SearchCondition condition = SearchConditionFactory.createSearchCondition4Class(rootItemClass, null, false);
		condition.setPageNum(1);
		condition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		condition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
		condition.setSearchType(AdvancedQueryTypeEnum.NORMAL);
		condition.addFilter("ID$", id, OperateSignEnum.EQUALS);
		BOAS boas = this.stubService.getBOAS();
		List<FoundationObject> list = boas.listObject(condition);
		return list;
	}

	private String getRootClassByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IItem);
		if (!SetUtils.isNullList(classInfoList))
		{
			for (ClassInfo classInfo : classInfoList)
			{
				ClassInfo superClassInfo = this.stubService.getEMM().getClassByGuid(classInfo.getSuperClassGuid());
				if (!superClassInfo.hasInterface(ModelInterfaceEnum.IItem) || !superClassInfo.isCreateTable())
				{
					return classInfo.getName();
				}
			}
		}
		return null;
	}

	class DocumentMark
	{
		private Document document;
		private long     lastModified;

		public DocumentMark(Document document, long lastModified)
		{
			this.document = document;
			this.lastModified = lastModified;
		}

		/**
		 * 文件是否被改动过
		 *
		 * @param modified
		 * @return
		 */
		public boolean isChanged(long modified)
		{
			return this.lastModified != modified;
		}

		public Document getDocument()
		{
			return this.document;
		}

		public void setDocument(Document document)
		{
			this.document = document;
		}

		public long getLastModified()
		{
			return this.lastModified;
		}

		public void setLastModified(long lastModified)
		{
			this.lastModified = lastModified;
		}

	}

	private boolean isBOMExport(ERPServerType serverType, String schemaName)
	{
		ERPQuerySchemaParameterObject parameterObject = new ERPQuerySchemaParameterObject();
		parameterObject.setSchemaName(schemaName);
		parameterObject.setERPType(serverType);
		parameterObject.setECChangeType(null);
		parameterObject.setEC(false);
		ERPSchema schema = null;
		try
		{
			schema = this.getSchemaByName(parameterObject);
			List<String> operationList = schema.getOperationList();
			if (!SetUtils.isNullList(operationList) && operationList.contains("createConfigBOM"))
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断Operation是否需要实时返回结果
	 *
	 * @param schemaName
	 * @param serverType
	 * @return
	 * @throws Exception
	 */
	public boolean isRealTime(String schemaName, ERPServerType serverType) throws Exception
	{
		List<String> operations = new ArrayList<String>();
		Map<String, String> map = null;
		ERPQuerySchemaParameterObject parameterObject = new ERPQuerySchemaParameterObject();
		parameterObject.setSchemaName(schemaName);
		parameterObject.setERPType(serverType);
		parameterObject.setECChangeType(null);
		parameterObject.setEC(false);
		ERPSchema schema = this.getSchemaByName(parameterObject);
		operations = schema.getOperationList();

		if (!SetUtils.isNullList(operations))
		{
			for (String operation : operations)
			{
				map = this.getOperationAttribute(serverType, operation);
				if ("true".equalsIgnoreCase(map.get("realtime")))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据content的id从xml文件中取得operation和name属性
	 *
	 * @param serverType
	 * @param id
	 * @return
	 */
	public Map<String, String> getContentAttribute(ERPServerType serverType, String id)
	{
		Iterator<ConfigurableKVElementImpl> it = this.getXMLConfig(this.getFileName(serverType) + ".xml").iterator("root.contents.content");
		ConfigurableKVElementImpl tempKV = null;
		while (it.hasNext())
		{
			tempKV = it.next();
			if (tempKV.getAttributeValue("id").equals(id))
			{
				break;
			}
			tempKV = null;
		}
		if (tempKV == null)
		{
			return Collections.emptyMap();
		}
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("operation", tempKV.getAttributeValue("operation"));
		attrMap.put("name", tempKV.getAttributeValue("name"));
		attrMap.put("tablename", tempKV.getAttributeValue("tablename"));
		return attrMap;
	}

	/**
	 * 从ERP读取价格数量成本
	 *
	 * @param list          物料
	 * @param factoryId     工厂名称
	 * @param serviceConfig 配置
	 * @return
	 * @throws ServiceRequestException
	 * @throws Exception
	 */
	public List<FoundationObject> getInfo(List<FoundationObject> list, List<String> factoryId, ERPServiceConfig serviceConfig) throws ServiceRequestException, Exception
	{
		ERPServerType serverType = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
		List<FoundationObject> foundationList = new ArrayList<FoundationObject>();
		foundationList = this.getStub(serverType).getInfoFromERP(list, serviceConfig, factoryId);
		return foundationList;
	}

	/**
	 * 抛分类到ERP
	 *
	 * @param codeItemList
	 * @param config
	 * @param isRoot
	 * @throws ServiceRequestException
	 */
	public void createCFERPJob(List<String> codeGuidList, String codeTitle, ERPServiceConfig config, boolean isRoot) throws ServiceRequestException
	{
		DynaLogger.info("*****Start createCFERPJob*****");
		LanguageEnum lang = this.stubService.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		queueJob.setName("CFERP");
		// 设置字段
		queueJob.setFielda(codeGuidList.get(0));// fieldA: classification root guid
		queueJob.setFieldb(config.getGuid());// fieldB:ERPSerGuid

		queueJob.setFieldd(config.getSchemaName());// fieldd:集成方案
		queueJob.setFieldc(lang.toString());// 获取执行用户当前登录用的语言 fieldc:LANG
		queueJob.setFieldh(String.valueOf(System.nanoTime()));// 记录每笔资料的ID:使用系统的时间
		queueJob.setFieldf(StringUtils.getMsrTitle(codeTitle, this.stubService.getUserSignature().getLanguageEnum().getType()));// fieldF:选中节点的title
		queueJob.setFieldg(isRoot ? "1" : "0");
		queueJob.setIsSinglethRead("Y");// ERP传输必须是单线程，否则会产生线程安全问题
		queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.CFERPIJob");
		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.ERP);
		((JSSImpl) this.stubService.getJSS()).createJob(queueJob);
		DynaLogger.info("*****End createCFERPJob*****\r\njob id : " + queueJob.getFielde());
	}

	/**
	 * 抛分类到ERP
	 * ERPT100DB
	 *
	 * @param codeItemList
	 * @param config
	 * @param isRoot
	 * @throws ServiceRequestException
	 */
	public void createCFERPJob(String codeGuid, List<String> erpFactoryList, ERPServiceConfig config, boolean isRoot) throws ServiceRequestException
	{
		DynaLogger.info("*****Start createCFERPJob*****");
		LanguageEnum lang = this.stubService.getUserSignature().getLanguageEnum();
		if (!SetUtils.isNullList(erpFactoryList))
		{
			for (String erpFactory : erpFactoryList)
			{
				Queue queueJob = new Queue();
				queueJob.setName("CFERP");
				// 设置字段
				queueJob.setFielda(codeGuid);// fieldA: classification root guid
				queueJob.setFieldb(config.getGuid());// fieldB:ERPSerGuid
				queueJob.setFieldc(lang.toString());// 获取执行用户当前登录用的语言 fieldc:LANG
				queueJob.setFieldd(config.getSchemaName());// fieldd:集成方案
				queueJob.setFieldf(erpFactory);// 集团代码
				queueJob.setFieldg(isRoot ? "1" : "0");
				queueJob.setFieldh(String.valueOf(System.nanoTime()));// 记录每笔资料的ID:使用系统的时间
				queueJob.setIsSinglethRead("Y");// ERP传输必须是单线程，否则会产生线程安全问题
				queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.CFERPIJob");
				queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
				queueJob.setJobGroup(JobGroupEnum.ERP);
				((JSSImpl) this.stubService.getJSS()).createJob(queueJob);
			}
		}
		DynaLogger.info("*****End createCFERPJob*****");
	}

	/**
	 * 获得流程拋轉合併到同一個隊列的對象數量
	 *
	 * @param serverType ERP类型
	 * @return
	 */
	private int getObjectBatchSize(ERPServerType serverType)
	{
		int resultSize = -1;
		Iterator<ConfigurableKVElementImpl> it = this.getXMLConfig(this.getFileName(serverType) + ".xml").iterator("root.parameters.param");
		ConfigurableKVElementImpl tempKV = null;
		while (it.hasNext())
		{
			tempKV = it.next();
			if ("objectBatchSize".equalsIgnoreCase(tempKV.getAttributeValue("name")))
			{
				resultSize = Integer.parseInt(tempKV.getAttributeValue("value"));
			}
		}
		if (resultSize == -1 || resultSize < 0)
		{
			return objectBatchSize;
		}
		else if (resultSize > 120)
		{
			throw new IllegalArgumentException("objectBatchSize can not larger than 120!");
		}
		return resultSize;
	}

	/**
	 * 多个对象合并到一个队列中
	 *
	 * @param parameterMap
	 * @param tempList
	 * @param isGoFlow
	 * @throws ServiceRequestException
	 */
	private void createMergeQueue(Map<String, String> parameterMap, List<ObjectGuid> tempList, boolean isGoFlow) throws ServiceRequestException
	{
		StringBuffer guidBuffer = new StringBuffer();
		StringBuffer classGuidBuffer = new StringBuffer();
		Queue queueJob = new Queue();
		ERPServiceConfig conf = this.stubService.getERPServiceConfigbyServiceGuid(parameterMap.get("serverGuid"));
		String templateName = conf.getName();
		if (templateName != null && templateName.startsWith("ERPpriority"))
		{
			queueJob.setName("ERPpriority");
			queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.ERPpriorityJob");
		}
		else
		{
			queueJob.setName("ERP");
			queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.ERPIJob");
		}
		if (!SetUtils.isNullList(tempList))
		{
			for (ObjectGuid objectguid : tempList)
			{
				FoundationObject fo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectguid, false);
				this.stubService.getDCR().check(parameterMap.get("serviceConfigName"), fo);

				if (!StringUtils.isNullString(guidBuffer.toString()))
				{
					guidBuffer.append(";");
				}
				guidBuffer.append(objectguid.getGuid());
				if (!StringUtils.isNullString(classGuidBuffer.toString()))
				{
					classGuidBuffer.append(";");
				}
				classGuidBuffer.append(objectguid.getClassGuid());
			}
		}
		// 设置字段
		queueJob.setFielda(guidBuffer.toString());// fieldA:Item
		queueJob.setFieldb(classGuidBuffer.toString());// fieldB:Item$class
		queueJob.setFieldc(parameterMap.get("serverGuid"));// fieldC:ERPSerGuid
		queueJob.setOwneruserGuid(parameterMap.get("userGuid"));

		queueJob.setFieldd(parameterMap.get("actionSchema"));// fieldD:集成方案
		queueJob.setFielde(parameterMap.get("templateName"));// fieldE:BOMTemplateName
		queueJob.setFieldf(parameterMap.get("factoryName"));// fieldF:ERPFactory
		queueJob.setFieldg(parameterMap.get("lang"));// 获取执行用户当前登录用的语言 fieldG:LANG
		queueJob.setFieldh(String.valueOf(System.nanoTime()));// 记录每笔资料的ID:使用系统的时间
		queueJob.setFieldi(parameterMap.get("EC"));
		queueJob.setFieldj(parameterMap.get(UpdatedECSConstants.ChangeType));
		queueJob.setFieldk(parameterMap.get("BOMChange"));
		queueJob.setFieldo("true");// Fieldo：是合并队列
		queueJob.setIsSinglethRead("Y");// ERP传输必须是单线程，否则会产生线程安全问题
		queueJob.setExecutorClass("dyna.app.service.brs.erpi.ERPIJob.ERPIJob");
		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.ERP);
		((JSSImpl) this.stubService.getJSS()).createJob(queueJob);
	}

	/**
	 * 是否为批量取替代申请单
	 *
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean isHasBathRS(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid != null)
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
			if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IRSApplyForm))
			{
				return true;
			}
		}
		return false;
	}

	public BooleanResult getJobStatusBySeqkeyFromERP(String jobGuid) throws Exception
	{
		Queue job = this.stubService.getJSS().getJob(jobGuid);
		ERPServiceConfig conf = this.stubService.getERPServiceConfigbyServiceGuid(job.getFieldc());
		ERPServerType serverType = ERPServerType.valueOf(conf.getERPServerSelected());
		return this.getStub(serverType).getJobStatusBySeqkeyFromERP(conf, job);
	}

	public boolean isSingleSignOn(ERPServerType serverType) throws Exception
	{
		Element ssoElement = null;
		String filename = this.getFileName(serverType);
		Document document = this.getDocument(filename + ".xml");
		if (document.getRootElement().getChild("parameters") != null)
		{
			@SuppressWarnings("unchecked") List<Element> list = document.getRootElement().getChild("parameters").getChildren("param");
			if (!SetUtils.isNullList(list))
			{
				for (Element e : list)
				{
					if ("SingleSign-On".equalsIgnoreCase(e.getAttributeValue("name")))
					{
						ssoElement = e;
						break;
					}
				}
			}
		}
		return ssoElement == null ? false : "true".equalsIgnoreCase(ssoElement.getAttributeValue("value"));
	}

	/**
	 * 获取T100配置文件
	 *
	 * @param serverType
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getDbLinkConfig(ERPServerType serverType) throws Exception
	{
		Map<String, String> dblinkInfoMap = null;
		if (ERPServerType.ERPT100DB == serverType || ERPServerType.ERPT100 == serverType)
		{
			String filename = this.getFileName(serverType);
			Document document = this.getDocument(filename + ".xml");
			if (document != null)
			{
				dblinkInfoMap = new HashMap<String, String>();
				@SuppressWarnings("unchecked") List<Element> Elements = document.getRootElement().getChild("parameters").getChildren();
				if (!SetUtils.isNullList(Elements))
				{
					for (Element element : Elements)
					{
						if ("ERPDBlink".equalsIgnoreCase(element.getAttributeValue("name")))
						{
							dblinkInfoMap.put("url", element.getAttributeValue("value"));
						}
						else if ("JDBC.Username".equalsIgnoreCase(element.getAttributeValue("name")))
						{
							dblinkInfoMap.put("username", element.getAttributeValue("value"));
						}
						else if ("JDBC.Password".equalsIgnoreCase(element.getAttributeValue("name")))
						{
							dblinkInfoMap.put("password", element.getAttributeValue("value"));
						}
					}
				}
			}
		}
		return dblinkInfoMap;
	}

	public List<Integer> getObjectDataFromERP(ERPSchema schema) throws Exception
	{
		return this.getErpT100DBStub().getObjectDataFromERP(schema);
	}

	public String getCategoryType(String operation) throws Exception
	{
		return this.getErpT100DBStub().getCategoryType(operation);
	}
}
