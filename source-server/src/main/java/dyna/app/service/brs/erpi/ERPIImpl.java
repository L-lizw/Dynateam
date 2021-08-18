/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMImpl
 * caogc 2010-10-12
 */
package dyna.app.service.brs.erpi;

import dyna.app.core.track.annotation.Tracked;
import dyna.app.service.BusinessRuleService;
import dyna.app.service.brs.erpi.cross.util.CrossConfigureManager;
import dyna.app.service.brs.erpi.tracker.TRERPImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.erp.ERPQuerySchemaParameterObject;
import dyna.common.bean.erp.ERPSchema;
import dyna.common.bean.erp.ERPYFAllConfig;
import dyna.common.bean.erp.ERPYFPLMClassConfig;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.ErrorRecord;
import dyna.common.dto.Queue;
import dyna.common.dto.erp.*;
import dyna.common.dto.erp.tmptab.ERPTempTableInfo;
import dyna.common.dto.erp.tmptab.ERPtempData;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.service.brs.*;
import dyna.net.service.das.JSS;
import dyna.net.service.das.MSRM;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Bill of material implementation
 * 
 * @author caogc
 * 
 */
public class ERPIImpl extends BusinessRuleService implements ERPI
{

	// 下面的这些变量采用singleton模式，因此必须确保Job是单线程执行的，否则会产生线程安全问题
	// 创建单线程Job方法：在相关的ERP**Stub类中的createQueue()方法中将isSingleThread参数指定为Y
	public static String	ECNITTEM	= "ECNITEM";
	private ERPStub			erpStub		= null;

	private PortalStub		portalStub;
	private DrawingStub		drawingStub	= null;

	/**
	 * CrossStub无状态，可以使用singleton模式
	 */
	private CrossStub		crossStub;

	protected CrossStub getCrossStub()
	{
		return this.crossStub;
	}

	public ERPStub getERPStub()
	{
		return this.erpStub;
	}

	public DrawingStub getDrawingStub()
	{
		return this.drawingStub;
	}

	protected PortalStub getPortalStub()
	{
		return this.portalStub;
	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized BRM getBRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BRM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized JSS getJSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(JSS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized UECS getUECS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(UECS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized DSS getDSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DSS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized SMS getSMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(SMS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized MMS getMMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MMS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized CPB getCPB() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(CPB.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized DCR getDCR() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DCR.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	protected synchronized ACL getACL() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(ACL.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.app.service.BusinessRuleService#authorize(dyna.net.security.signature
	 * .Signature)
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("invoke [" + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "] by " + getSignature());
		}
		if ("doSyncProcess".equals(method.getName()) || "getSrvRegInfo".equals(method.getName()) || "getProdRegInfo".equals(method.getName())
				|| "syncEncodingState".equals(method.getName()) || "syncInvokeParam".equals(method.getName()) || "checkPortalUserPassword".equals(method.getName())
				|| "getEncodingState".equals(method.getName()) || "isSessionExpire".equals(method.getName()) || "isCrossEncoding".equals(method.getName())
				|| "setERPJobStatus".equals(method.getName()) || "isSingleSignOn".equals(method.getName()))
		{
			if (this.getSignature() instanceof ModuleSignature)
			{
				this.clearSignature();
			}
			return;
		}

		super.authorize(method, args);

	}

	@Override
	public void createERPJob(List<ObjectGuid> objectGuidList, List<String> erpFactory, String templateName, String serverGuid, List<WorkflowTemplateActPerformerInfo> userList,
			boolean isGoFlow, boolean isMerge) throws ServiceRequestException
	{

		this.getERPStub().createERPJob(objectGuidList, erpFactory, templateName, serverGuid, userList, isGoFlow, isMerge, true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#getProdRegInfo()
	 */
	@Override
	public String getProdRegInfo(String ip, String uid) throws ServiceRequestException
	{
		return getCrossStub().getProdRegInfo(ip, uid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#getSrvRegInfo()
	 */
	@Override
	public String getSrvRegInfo(String ip, String uid) throws ServiceRequestException
	{

		return getCrossStub().getSrvRegInfo(ip, uid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#doSyncProcess()
	 */
	@Override
	public String doSyncProcess(String paramXML) throws Exception
	{

		return getCrossStub().doSyncProcess(paramXML);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#syncEncodingState(java.lang.String)
	 */
	@Override
	public String syncEncodingState(String paramXML) throws ServiceRequestException
	{
		return getCrossStub().syncEncodingState(paramXML);

	}

	@Override
	public String getEncodingState(String paramXML) throws ServiceRequestException
	{

		return getCrossStub().getEncodingState(paramXML);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listERPServiceConfigByERPServiceName
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public List<ERPServiceConfig> listERPServiceConfigByERPServiceName(String selectServerName) throws ServiceRequestException
	{

		return this.getERPStub().listERPServiceConfig(selectServerName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#getERPServiceConfigDefaultvalues(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public ERPServiceConfig getERPServiceConfigDefaultvalues(String selectServerName) throws ServiceRequestException
	{
		return this.getERPStub().getERPServiceConfigDefaultvalues(selectServerName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#getCrossServiceConfigDefaultvalues()
	 */
	@Override
	public CrossServiceConfig getCrossServiceConfigDefaultvalues() throws ServiceRequestException
	{
		return CrossConfigureManager.getInstance().getCrossServiceConfigXML();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#listERPType()
	 */
	@Override
	public List<ERPServerType> listERPType() throws ServiceRequestException
	{
		return this.getERPStub().listERPType();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listERPServiceConfigNameByERPServiceName
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public List<ERPServiceConfig> listERPServiceConfigNameByBoClassName(String boClassName) throws ServiceRequestException
	{
		return this.getERPStub().listERPServiceConfigNameByBoClassName(boClassName);

	}

	@Override
	public List<String> listERPServiceConfigName() throws ServiceRequestException
	{
		return this.getERPStub().listERPServiceConfigName();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#update(dyna.common.bean.data.system.ERPYFAllConfig
	 * )
	 */
	@Override
	public ERPYFAllConfig saveTemplate(ERPYFAllConfig allConfig) throws ServiceRequestException
	{
		return this.getERPStub().saveTemplate(allConfig);
	}

	@Override
	public void deleteERPConfig(String serviceConfigGuid) throws ServiceRequestException
	{
		this.getERPStub().deleteERPConfig(serviceConfigGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#listTemplateCanUseUsers(java.lang.String)
	 */
	@Override
	public List<WorkflowTemplateActPerformerInfo> listTemplateCanUseUsersByServGuid(String serviceConfigGuid) throws ServiceRequestException
	{
		return this.getERPStub().listTemplateCanUseUsersByServGuid(serviceConfigGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#saveEndNoticeUsers(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void saveEndNoticeUsers(String serviceGuid, List<WorkflowTemplateActPerformerInfo> userList) throws ServiceRequestException
	{
		this.getERPStub().saveEndNoticeUsers(serviceGuid, userList);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#getEndNoticeUsersByServGuid(java.lang.String)
	 */
	@Override
	public List<String> listEndNoticeUsersByServGuid(String serviceConfigGuid) throws ServiceRequestException
	{
		return this.getERPStub().listEndNoticeUsersByServGuid(serviceConfigGuid);

	}

	@Override
	public List<WorkflowTemplateActPerformerInfo> getTemplateEndNoticeUsersByServGuid(String serviceGuid) throws ServiceRequestException
	{
		return this.getERPStub().getTemplateEndNoticeUsersByServGuid(serviceGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listERPSerConfigNameByUseGuid(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ERPServiceConfig> listERPSerConfigNameByUseGuid(String serName, String userGuid) throws ServiceRequestException
	{
		return this.getERPStub().listERPSerConfigNameByUseGuid(serName, userGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listERPBOConfigByServGuid(java.lang.String)
	 */
	@Override
	public List<ERPBOConfig> listERPBOConfigByServGuid(String serviceGuid) throws ServiceRequestException
	{
		return this.getERPStub().listERPBOConfigByServGuid(serviceGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#getERPServiceConBySerNandSerConN(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ERPServiceConfig getERPServiceConBySerNandSerConN(String selectServerName, String serverConfigName) throws ServiceRequestException
	{
		return this.getERPStub().getERPServiceConBySerNandSerConN(selectServerName, serverConfigName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#deleteERPYFAllConfigBySNandCN(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteERPAllConfigBySNandCN(String selectServerName, String serverConfigName) throws ServiceRequestException
	{
		this.getERPStub().deleteERPAllConfigBySNandCN(selectServerName, serverConfigName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#geERPYFPLMClassConfig(java.lang.String)
	 */
	@Override
	public ERPYFPLMClassConfig getERPYFPLMClassConfig(String serviceGuid) throws ServiceRequestException
	{
		return this.getERPStub().getERPYFPLMClassConfig(serviceGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listERPBoConfigBySerGuidAndUseGuid(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public List<ERPBOConfig> listERPBoConfigBySerGuidAndUseGuid(String serviceGuid, String userGuid, boolean isAdministrator) throws ServiceRequestException
	{
		return this.getERPStub().listERPBoConfigBySerGuidAndUseGuid(serviceGuid, userGuid, isAdministrator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listERPServiceConfigNameByName(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public List<String> listERPServiceConfigNameByName(String selectServerName) throws ServiceRequestException
	{
		return this.getERPStub().listERPServiceConfigNameByName(selectServerName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#getSchemaName(dyna.common.systemenum.ERPServerType
	 * )
	 */
	@Override
	public List<String> getSchemaName(ERPServerType serverType) throws Exception
	{
		return this.getERPStub().getSchemaName(serverType);

	}

	@Override
	public List<String> listNotifyUsersWhenFailed(String serviceGuid) throws ServiceRequestException
	{
		return this.getERPStub().listNotifyUsersWhenFailed(serviceGuid);
	}

	@Override
	public List<ERPMoreCompanies> listFacrotyFromDataBase(String serverType) throws ServiceRequestException
	{

		return this.getERPStub().listFacrotyFromDataBase(serverType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#listFacrotyByServiceGuid(java.lang.String)
	 */
	@Override
	public List<String> listFacrotyByServiceGuid(String serviceGuid) throws ServiceRequestException
	{
		return this.getERPStub().listFacrotyByServiceGuid(serviceGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#listServices()
	 */
	@Override
	public List<ERPServiceConfig> listServices() throws ServiceRequestException
	{
		return this.getERPStub().listServices();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#ListDefaultMoreCompanyByServGuid(java.lang.
	 * String)
	 */
	@Override
	public List<ERPMoreCompanies> listDefaultMoreCompanyByServGuid(String serverGuid) throws ServiceRequestException
	{
		return this.getERPStub().listDefaultMoreCompanyByServGuid(serverGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listFialdEndNoticeByServGuid(java.lang.String)
	 */
	@Override
	public List<WorkflowTemplateActPerformerInfo> listFialdEndNoticeByServGuid(String serviceGuid) throws ServiceRequestException
	{
		return this.getERPStub().listFialdEndNoticeByServGuid(serviceGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ERPI#syncInvokeParam(java.lang.String)
	 */
	@Override
	public String syncInvokeParam(String plmName) throws ServiceRequestException
	{
		return this.getCrossStub().syncInvokeParam(plmName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.ERPI#listMoreCompanyByYTERP(dyna.common.bean.data
	 * .system.ERPServiceConfig)
	 */
	@Override
	public List<ERPMoreCompanies> getMoreCompanyThroughWS(ERPServiceConfig serverDef) throws Exception
	{

		return this.getERPStub().getStub(ERPServerType.valueOf(serverDef.getERPServerSelected())).getMoreCompanyThroughWS(serverDef);
	}

	@Override
	public String checkPortalUserPassword(String userAccount, String tripleDESPassword) throws ServiceRequestException
	{
		return getPortalStub().checkPortalUserPassword(userAccount, tripleDESPassword);
	}

	@Override
	public BooleanResult exportData2ERP(ObjectGuid objectGuid, Queue jobQueue, String userId, ERPServiceConfig serviceConfig) throws Exception
	{
		ERPServerType serverType = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
		return this.getERPStub().getStub(serverType).export(objectGuid, jobQueue, userId, serviceConfig);
	}

	@Override
	public void saveMoreCompany(String serverType, List<ERPMoreCompanies> morecompanyList) throws ServiceRequestException
	{
		this.getERPStub().saveMoreCompany(serverType, morecompanyList);

	}

	@Override
	public ERPServiceConfig getERPServiceConfigbyServiceGuid(String serviceGuid) throws ServiceRequestException
	{

		return this.getERPStub().getERPServiceConfigbyServiceGuid(serviceGuid);

	}

	@Override
	public String getDefaultBOMTemplate(ERPServerType ERPType) throws ServiceRequestException
	{
		return this.getERPStub().getDefaultBOMTemplate(ERPType);
	}

	@Override
	public ERPSchema getSchemaByName(ERPQuerySchemaParameterObject schemaParameterObj) throws ServiceRequestException
	{
		return this.getERPStub().getSchemaByName(schemaParameterObj);
	}

	@Override
	public List<WorkflowTemplateActPerformerInfo> listTemplateEndNoticeUsersByServGuid(String serviceGuid) throws ServiceRequestException
	{
		return this.getTemplateEndNoticeUsersByServGuid(serviceGuid);
	}

	@Override
	public BooleanResult checkConnection(ERPServiceConfig serviceConfig) throws ServiceRequestException
	{
		try
		{
			ERPServerType serverType = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
			return this.getERPStub().getStub(serverType).checkConnection(serviceConfig);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("ID_CLIENT_ADMIN_TEMPLATE_TEST_FAIL", e.getMessage(), e);
		}
	}

	/**
	 * @param id
	 *            OperationID
	 * @return
	 */
	@Override
	public Map<String, String> getOperationAttribute(ERPServerType serverType, String operation) throws ServiceRequestException
	{
		return this.getERPStub().getOperationAttribute(serverType, operation);
	}

	@Override
	public void saveERPTransferLog(ERPTransferLog log) throws ServiceRequestException
	{
		this.getERPStub().saveERPTransferLog(log);
	}

	@Override
	public List<ERPTransferLog> getERPTransferLog(Map<String, Object> param) throws ServiceRequestException
	{
		return this.getERPStub().getERPTransferLog(param);
	}

	@Override
	public boolean showBOMTemplate(ERPSchema schema, ERPServerType serverType) throws ServiceRequestException
	{
		return this.getERPStub().showBOMTemplate(schema, serverType);
	}

	@Override
	public boolean isSessionExpire() throws ServiceRequestException
	{
		return this.signature == null;
	}

	@Tracked(description = TrackedDesc.ERP_SEARCH_DRAW, renderer = TRERPImpl.class)
	@Override
	public String createFileData(String paramXML) throws Exception
	{
		return this.getDrawingStub().getFileData(paramXML);
	}

	@Override
	public boolean isRealTime(String schemaName, ERPServerType serverType) throws Exception
	{
		return this.getERPStub().isRealTime(schemaName, serverType);
	}

	@Tracked(description = TrackedDesc.ERP_SEARCH_PROPERTY_VAL, renderer = TRERPImpl.class)
	@Override
	public List<FoundationObject> getFoundationInfoFromERP(List<FoundationObject> list, List<String> factoryId, ERPServiceConfig serviceConfig) throws Exception
	{
		return this.getERPStub().getInfo(list, factoryId, serviceConfig);
	}

	@Override
	public Map<String, String> getContentAttribute(ERPServerType serverType, String id) throws ServiceRequestException
	{
		return this.getERPStub().getContentAttribute(serverType, id);
	}

	@Override
	public void createCFERPJob(List<String> codeGuidList, String codeTitle, ERPServiceConfig config, boolean isRoot) throws ServiceRequestException
	{
		this.getERPStub().createCFERPJob(codeGuidList, codeTitle, config, isRoot);
	}

	@Override
	public BooleanResult exportCF2ERP(List<String> codeGuidList, Queue queuejob, String userId, ERPServiceConfig serviceConfig) throws Exception
	{
		ERPServerType serverType = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
		return this.getERPStub().getStub(serverType).export(codeGuidList, queuejob, userId, serviceConfig);
	}

	@Override
	public BooleanResult setERPJobStatus(String jobId, String userId, String jobStatusVal, String message, boolean isNotify) throws ServiceRequestException
	{
		if (this.getSignature() == null)
		{
			this.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
		}
		this.newTransactionId();
		return this.getERPStub().setERPJobStatus(jobId, userId, jobStatusVal, message, isNotify);
	}

	@Override
	public boolean isCrossEncoding()
	{
		return CrossConfigureManager.getInstance().getCrossServiceConfig().getHostIsEncode();
	}

	@Override
	public ERPTempTableInfo getBaseTableInfo(Map<String, Object> map) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		return sds.queryObject(ERPTempTableInfo.class, map, "selectBaseTable");
	}

	@Override
	public List<ERPTempTableInfo> listBaseTableInfo(Map<String, Object> map) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(ERPTempTableInfo.class, map, "selectBaseTable");
	}

	@Override
	public void insertBaseTableInfo(ERPTempTableInfo info) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.save(info, "insert");
	}

	@Override
	public void createTable(Map<String, Object> map) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.update(ERPTempTableInfo.class, map, "createTable");
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		sds.update(ERPTempTableInfo.class, map, "createTableIndex");
	}

	@Override
	public List<ERPtempData> listERPTempData(Map<String, Object> map) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(ERPtempData.class, map, "select");
	}

	@Override
	public void insertTempData(ERPtempData data) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			SystemDataService sds = DataServer.getSystemDataService();
			sds.save(data, "insert", null);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	@Override
	public void updateTempData(ERPtempData data) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			SystemDataService sds = DataServer.getSystemDataService();
			sds.update(ERPtempData.class, data, "update");
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	@Override
	public BooleanResult exportMergeData2ERP(List<ObjectGuid> objectGuids, Queue queuejob, String userId, ERPServiceConfig serviceConfig) throws Exception
	{
		ERPServerType serverType = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
		return this.getERPStub().getStub(serverType).exportMergeData(objectGuids, queuejob, userId, serviceConfig);
	}

	@Override
	public BooleanResult getJobStatusBySeqkeyFromERP(String jobGuid) throws Exception
	{
		return this.getERPStub().getJobStatusBySeqkeyFromERP(jobGuid);
	}

	@Override
	public boolean isSingleSignOn(String type) throws Exception
	{
		ERPServerType serverType = ERPServerType.valueOf(type);
		return this.getERPStub().isSingleSignOn(serverType);
	}

	@Override
	public void createCFERPJob(String codeGuid, List<String> erpFactoryList, ERPServiceConfig config, boolean isRoot) throws ServiceRequestException
	{
		this.getERPStub().createCFERPJob(codeGuid, erpFactoryList, config, isRoot);
	}

	@Override
	public Map<String, String> getDbLinkConfig(ERPServerType serverType) throws Exception
	{
		return this.getERPStub().getDbLinkConfig(serverType);
	}

	@Override
	public List<ErrorRecord> listErrorRecordInfo(String jobId) throws Exception
	{
		return ((ERPT100DBTransferStub) this.getERPStub().getStub(ERPServerType.ERPT100DB)).listErrorRecordInfo(jobId);
	}

	@Tracked(description = TrackedDesc.ERP_SEARCH_SAVE_PROT_VAL, renderer = TRERPImpl.class)
	@Override
	public List<Integer> getObjectDataFromERP(ERPSchema schema) throws Exception
	{
		return this.getERPStub().getObjectDataFromERP(schema);
	}

	@Override
	public String getCategoryType(String operation) throws Exception
	{
		return this.getERPStub().getCategoryType(operation);
	}
}