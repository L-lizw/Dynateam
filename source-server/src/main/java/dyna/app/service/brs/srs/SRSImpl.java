/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SRSImpl 简单报表服务实现
 * Wanglei 2011-3-29
 */
package dyna.app.service.brs.srs;

import dyna.app.report.*;
import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.edap.EDAPImpl;
import dyna.common.Criterion;
import dyna.common.FieldOrignTypeEnum;
import dyna.common.SearchCondition;
import dyna.common.bean.data.*;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.ppms.PMStructureUtil;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.dto.Queue;
import dyna.common.dto.*;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.systemenum.uecs.ECOLifecyclePhaseEnum;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.*;
import dyna.net.service.das.JSS;
import dyna.net.service.das.MSRM;
import jxl.write.DateTime;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.util.JRFontNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//import dyna.dsserver.client.DSSFileTransProgress;
//import dyna.dsserver.client.DSSFileTransProgress.FileOperateEnum;

/**
 * Simple Report Service 简单报表服务实现
 * 
 * @author Wanglei
 * 
 */
public class SRSImpl extends DataAccessService implements SRS
{
	private CommonReportStub	commonReportStub	= null;
	private BOMReportStub		bomReportStub		= null;
	private final String		reportConfigFile	= EnvUtils.getConfRootPath() + "conf/comment/report/report_template.xml";
	private ReportConfigStub	reportConfigStub	= null;

	public BOMReportStub getBOMReportStub()
	{
		if (this.bomReportStub == null)
		{
			this.bomReportStub = new BOMReportStub(this.serviceContext, this);
		}
		return this.bomReportStub;
	}

	public ReportConfigStub getReportConfigStub()
	{
		if (this.reportConfigStub == null)
		{
			this.reportConfigStub = new ReportConfigStub(this.serviceContext, this);
		}
		return this.reportConfigStub;
	}

	public CommonReportStub getCommonReportStub()
	{
		if (this.commonReportStub == null)
		{
			this.commonReportStub = new CommonReportStub(this.serviceContext, this);
		}
		return this.commonReportStub;
	}

	public synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
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
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
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
		catch (Exception e)
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
		catch (Exception e)
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
		catch (Exception e)
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
		catch (Exception e)
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
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized PMS getPMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(PMS.class);
		}
		catch (Exception e)
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
		catch (Exception e)
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
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized PPMS getPPMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(PPMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	@Override
	public ObjectGuid getObjectGuidByStr(String str)
	{
		ObjectGuid objectGuid = new ObjectGuid();
		if (StringUtils.isNullString(str))
		{
			return objectGuid;
		}

		if (str.split("\\$").length != 7)
		{
			return objectGuid;
		}

		String[] objectGuidArray = str.split("\\$");
		String classGuid = this.nullToStrObject(objectGuidArray[0]);
		String className = this.nullToStrObject(objectGuidArray[1]);
		String guid = this.nullToStrObject(objectGuidArray[2]);
		String masterGuid = this.nullToStrObject(objectGuidArray[3]);
		boolean isMaster = this.nullToBooleanObject(objectGuidArray[4]);
		String bizObjectGuid = this.nullToStrObject(objectGuidArray[5]);
		String commitFolderGuid = this.nullToStrObject(objectGuidArray[6]);

		if (classGuid != null && !StringUtils.isGuid(classGuid))
		{
			return objectGuid;
		}
		if (guid != null && !StringUtils.isGuid(guid))
		{
			return objectGuid;
		}
		if (masterGuid != null && !StringUtils.isGuid(masterGuid))
		{
			return objectGuid;
		}
		if (bizObjectGuid != null && !StringUtils.isGuid(bizObjectGuid))
		{
			return objectGuid;
		}
		if (commitFolderGuid != null && !StringUtils.isGuid(commitFolderGuid))
		{
			return objectGuid;
		}

		return new ObjectGuid(classGuid, className, guid, masterGuid, isMaster, bizObjectGuid, commitFolderGuid);
	}

	@Override
	public String buildListToString(List<String> list)
	{
		StringBuilder builder = new StringBuilder();
		if (list != null && list.size() > 0)
		{
			for (String str : list)
			{
				if (builder.length() != 0)
				{
					builder.append(JOIN_CHAR);
				}
				builder.append(str);
			}
		}

		return builder.length() == 0 ? null : builder.toString();
	}

	@Override
	public List<String> rebuildStrToList(String s)
	{
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNullString(s))
		{
			return list;
		}

		if (s.indexOf(JOIN_CHAR) == -1)
		{
			list.add(s);
			return list;
		}

		String[] array = s.split(JOIN_CHAR);
		for (String str : array)
		{
			list.add(str);
		}

		return list;
	}

	private String nullToStrObject(String str)
	{
		return str == null || str.equals("null") ? null : str;
	}

	private boolean nullToBooleanObject(String str)
	{
		return str == null || str.equals("false") ? false : true;
	}

	@Override
	public void reportConfigBOMJob(ObjectGuid objectGuid, ObjectGuid drawObjectGuid, DataRule dataRule, ReportTypeEnum exportFileType, SearchCondition sc, int level)
			throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		queueJob.setName("ConfigBOM");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.ConfigBOMRepeortJob");
		queueJob.setIsSinglethRead("Y");

		StringBuffer resultFieldBuffer = new StringBuffer();
		StringBuffer uiNameBuffer = new StringBuffer();
		if (sc != null)
		{
			if (!SetUtils.isNullList(sc.getResultFieldList()))
			{
				for (String fieldName : sc.getResultFieldList())
				{
					if (resultFieldBuffer.length() > 0)
					{
						resultFieldBuffer.append(",");
					}
					resultFieldBuffer.append(fieldName);
				}
			}
			if (!SetUtils.isNullList(sc.listResultUINameList()))
			{
				for (String uiName : sc.listResultUINameList())
				{
					if (uiNameBuffer.length() > 0)
					{
						uiNameBuffer.append(",");
					}
					uiNameBuffer.append(uiName);
				}
			}
		}

		queueJob.setFielda(objectGuid.toString());

		if (level >= 1)
		{
			queueJob.setFieldb(String.valueOf(level));
		}

		queueJob.setFieldc(exportFileType.toString());
		queueJob.setFieldd(this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId());
		queueJob.setFielde(resultFieldBuffer.toString());
		queueJob.setFieldf(uiNameBuffer.toString());
		if (drawObjectGuid != null)
		{
			queueJob.setFieldg(drawObjectGuid.toString());
		}
		queueJob.setFieldh(dataRule.getSystemStatus().getId());
		if (dataRule.getLocateTime() != null)
		{
			queueJob.setFieldi(DateFormat.formatYMD(dataRule.getLocateTime()));
		}

		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);
	}

	@Override
	public void reportConfigBOM(FoundationObject instance, FoundationObject draw, ReportTypeEnum exportFileType, SearchCondition sc, String jobGuid, int level)
			throws ServiceRequestException
	{
		Map<String, Object> otherParams = new HashMap<String, Object>();
		otherParams.put("level", level);
		otherParams.put("exportType", exportFileType.toString());

		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "object is not exist.");
		}

		String bomReportTemplateName = "ConfigBOM.jrxml";
		ClassInfo classInfo = this.getEMM().getClassByGuid(instance.getObjectGuid().getClassGuid());
		if (classInfo.hasInterface(ModelInterfaceEnum.IOrderContract))
		{
			bomReportTemplateName = "ContractConfigBOM.jrxml";
		}

		LanguageEnum lang = this.getUserSignature().getLanguageEnum();

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);

		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();

		String newReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, bomReportTemplateName.replace(".jrxml", "") + "_template");

		File exportFile = null;
		UIObjectInfo uiObject = null;

		exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName);
		List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(exportFile);
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(exportFile);

		params.setUiObject(uiObject);
		params.setDetailColumnList(columnList);
		params.setHeaderColumnList(parameters);

		ReportDataProvider<StructureObject> reportDataProvider = new ConfigBOMReportDataProviderImpl(instance, draw, sc, params);
		File exportToFilePath = GenericReportUtil.getFile(instance.getId() + "_BOM", exportFileType);
		try
		{
			GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();
			ReportConfiguration configuration = this.getReportConfiguration(exportFileType, exportToFilePath, columnList, null);

			reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), reportDataProvider, configuration,
					reportDataProvider.getHeaderParameter());

			DSSFileInfo reportFileInfo = this.transferFileToDSFile(exportToFilePath);
			String content = " config bom report successful ";

			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()), content,
					MailCategoryEnum.INFO, this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, exportToFilePath.getAbsolutePath(), FileOperateEnum.UPLOAD, true);
					this.addFileToJob(jobGuid, ftpObject.getFileGuid());
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()), " advance search ", MailCategoryEnum.ERROR,
					this.getUserSignature().getUserId(), null);
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + "("
						+ e.getMessage().substring(message.indexOf("{") + 1, message.length() - 1) + ")";
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}
		finally
		{
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGenericBOM(dyna.common.bean.data.ObjectGuid
	 * , dyna.common.bean.data.system.BOMRule, int, dyna.common.SearchCondition,
	 * java.lang.String)
	 */
	@Override
	public void reportGenericBOM(ObjectGuid bomViewObjectGuid, DataRule dataRule, int level, SearchCondition bomSearchCondition, String reportUIName, ReportTypeEnum exportFileType,
			String jobGuid, boolean isContainRepf) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		BOMView bomView = this.getBOMS().getBOMView(bomViewObjectGuid);

		String content = bomView.getFullName();
		try
		{
			File reportFile = this.getBOMReportStub().reportGenericBOM(bomViewObjectGuid, dataRule, level, bomSearchCondition, exportFileType, lang, null, "bomtree", "1", "2",
					null, null, null, null, null, null, isContainRepf);
			if (reportFile == null)
			{
				return;
			}

			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(reportFile.getName());
			reportFileInfo.setFilePath(reportFile.getPath());
			reportFileInfo.setFileSize(reportFile.length());

			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()), content,
					MailCategoryEnum.INFO, this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, reportFile.getAbsolutePath(), FileOperateEnum.UPLOAD, true);
					this.addFileToJob(jobGuid, ftpObject.getFileGuid());
				}
			}
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "export report error:" + e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGeneric(dyna.common.bean.model.UIObject,
	 * java.lang.String, dyna.common.SearchCondition, java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public void reportGeneric(String uiName, ReportTypeEnum exportFileType, List<ObjectGuid> objectGuidList, SearchCondition reportcondition, boolean isScript)
			throws ServiceRequestException
	{
		if (objectGuidList != null && objectGuidList.size() > 0)
		{
			ObjectGuid objectGuid = objectGuidList.get(0);
			ClassInfo classInfo = this.getEMM().getClassByGuid(objectGuid.getClassGuid());
			// 假如该实例为工程变更
			if (classInfo != null && (classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) || classInfo.hasInterface(ModelInterfaceEnum.IECOM)))
			{
				UIObjectInfo uiObject = this.getEMM().getUIObjectByName(classInfo.getName(), uiName);
				if (uiObject != null && uiObject.getType() == UITypeEnum.REPORT && !isScript)
				{
					this.createReportGenericInstacnceObjectJob(uiName, exportFileType, objectGuidList, reportcondition);
				}
				else
				{
					this.reportGenericECScript(uiName, objectGuidList, exportFileType, reportcondition, isScript, false);
				}
			}
			else
			{
				this.createReportGenericInstacnceObjectJob(uiName, exportFileType, objectGuidList, reportcondition);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGeneric(dyna.common.bean.model.UIObject,
	 * java.lang.String, dyna.common.SearchCondition, java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public void reportGenericEC(String uiName, ReportTypeEnum exportFileType, List<ObjectGuid> objectGuidList, SearchCondition reportcondition, boolean isScript, boolean isMail)
			throws ServiceRequestException
	{
		if (objectGuidList != null && objectGuidList.size() > 0)
		{
			ObjectGuid objectGuid = objectGuidList.get(0);
			ClassInfo classInfo = this.getEMM().getClassByGuid(objectGuid.getClassGuid());
			// 假如该实例为工程变更
			if (classInfo != null && (classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) || classInfo.hasInterface(ModelInterfaceEnum.IECOM)))
			{
				this.reportGenericECScript(uiName, objectGuidList, exportFileType, reportcondition, isScript, isMail);
			}
			else
			{
				this.createReportGenericInstacnceObjectJob(uiName, exportFileType, objectGuidList, reportcondition);
			}
		}

	}

	private DSSFileInfo transferFileToDSFile(File file)
	{
		DSSFileInfo dsFile = new DSSFileInfo();
		dsFile.setName(file.getName());
		dsFile.setFilePath(file.getPath());
		dsFile.setFileSize(file.length());

		return dsFile;
	}

	private File reportAllEC(String ecnId, List<FoundationObjectImpl> ecoList, ObjectGuid ecnObjectGuid, ReportTypeEnum exportFileType, Map<String, String> config,
			String jobCreator) throws ServiceRequestException
	{
		String pageSize = config.get("pageSize");
		String templateName = config.get("reporttemplatename");
		String reportpath = config.get("reportpath");
		if (!StringUtils.isNullString(reportpath))
		{
			reportpath = reportpath + "/";
		}

		if (StringUtils.isNullString(templateName))
		{
			throw new ServiceRequestException("batcheco template file name is null in config, please check!");
		}
		// 除去扩展名（.jrxml）
		String templateNameWithoutExtension = templateName.substring(0, templateName.length() - 6);

		LanguageEnum lang = this.getUserSignature().getLanguageEnum();

		File exportToFilePath = GenericReportUtil.getFile(ecnId, exportFileType);
		String templateFileName = GenericReportUtil.getReportTemplateNameBylang(lang, templateNameWithoutExtension + "_template");
		File file = GenericReportUtil.getTemplateFile("conf/comment/report/" + reportpath, templateFileName);
		if (!file.exists())
		{
			throw new ServiceRequestException("batcheco template file is not exist, please check! template file path:" + file.getPath());
		}

		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(file);
		List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(file);

		Map<String, List<String>> templateColumns = new HashMap<String, List<String>>();
		templateColumns.put("ECOLIST", this.getColumnOfSubTemplate(file, "EC_REPORT(eco)_template"));
		templateColumns.put("ECILIST", this.getColumnOfSubTemplate(file, "EC_REPORT(eci)_template"));

		Map<String, Object> othersParamMap = new HashMap<String, Object>();
		othersParamMap.put("REPORTTEMPLATEPATH", GenericReportUtil.getFolderOfFile(file));
		othersParamMap.put("templateColumns", templateColumns);

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setHeaderColumnList(parameters);
		params.setDetailColumnList(columnList);
		params.setOtherParams(othersParamMap);

		ECReportProviderImpl provider = new ECReportProviderImpl(ecnObjectGuid, ecoList, params);

		ReportConfiguration configuration = this.getReportConfiguration(exportFileType, exportToFilePath, columnList, pageSize);

		this.makeECReport(file, provider, configuration, jobCreator);

		return exportToFilePath;
	}

	private List<String> getColumnOfSubTemplate(File templateFile, String subTemplateName) throws ServiceRequestException
	{
		String folder = GenericReportUtil.getFolderOfFile(templateFile);

		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		String subReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, subTemplateName);
		File file = new File(folder + subReportTemplateName);

		List<String> columns = new ArrayList<String>();

		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(file);
		for (DetailColumnInfo detailInfo : columnList)
		{
			columns.add(detailInfo.getPropertyName());
		}
		return columns;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void makeECReport(File file, ReportDataProvider provider, ReportConfiguration configuration, String jobCreator) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();

		try
		{
			reportBuilder.personalizedReport(file, provider, configuration, provider.getHeaderParameter());
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String causeMessage = e.getCause() == null ? "" : e.getCause().getMessage();
			this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()),
					" EC Report Failed." + "\r\n\r\n" + e.getMessage() + "\r\n" + causeMessage, MailCategoryEnum.ERROR, jobCreator, null);
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getExceptionMessage("ID_APP_EXPORT_REPORT_ERROR_FONT", lang, null, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + message);
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String eMsg = e.getMessage();
				String detailMsg = "(" + eMsg.substring(eMsg.indexOf("{") + 1, eMsg.length() - 1) + ")";
				String message = this.getExceptionMessage("ID_APP_EXPORT_REPORT_ERROR", lang, detailMsg, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + message);
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getExceptionMessage("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang, null, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + message);
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getExceptionMessage("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang, null, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + message);
			}
			else
			{
				String message = this.getExceptionMessage("ID_APP_EXPORT_REPORT_ERROR", lang, null, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + message);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			String message = e.getMessage();
			if (e instanceof java.lang.NoClassDefFoundError)
			{
				message = "class not found:" + message;
			}
			else if (e instanceof java.lang.NoSuchMethodError)
			{
				message = "No such method:" + message;
			}
			throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + message);
		}
	}

	private ReportConfiguration getReportConfiguration(ReportTypeEnum reportFileType, File exportToFilePath, List<DetailColumnInfo> columnList, String pageCount)
	{
		ReportConfiguration configuration = new ReportConfiguration();

		configuration.setExportFileType(reportFileType);
		configuration.setExportToFilePath(exportToFilePath);
		configuration.setDetailColumnInfoList(columnList);
		configuration.setPageCount(Integer.valueOf(20));
		if (!StringUtils.isNullString(pageCount))
		{
			configuration.setPageCount(Integer.valueOf(pageCount));
		}

		return configuration;
	}

	private String getExceptionMessage(String messageId, LanguageEnum lang, String detail, Exception exception) throws ServiceRequestException
	{
		String message = this.getMSRM().getMSRString(messageId, lang.toString());
		if (!StringUtils.isNullString(detail))
		{
			message = message + detail;
		}

		return new Exception(exception).getMessage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGeneric(dyna.common.bean.model.UIObject,
	 * java.lang.String, dyna.common.SearchCondition, java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public void reportGenericECHelp(ReportTypeEnum exportFileType, Map<String, List<String>> guidListMap, SearchCondition reportcondition, boolean isScript, String jobGuid,
			boolean isMail) throws ServiceRequestException
	{
		Map<String, List<String>> guidListMapWithACL = new HashMap<String, List<String>>();
		if (guidListMap != null && !guidListMap.isEmpty())
		{
			for (String className : guidListMap.keySet())
			{
				ObjectGuid objectGuid = new ObjectGuid();
				objectGuid.setClassName(className);
				objectGuid.setClassGuid(this.getEMM().getClassByName(className).getGuid());

				ClassInfo classInfo = this.getEMM().getClassByGuid(objectGuid.getClassGuid());
				if (classInfo == null || (!classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) && !classInfo.hasInterface(ModelInterfaceEnum.IECOM)))
				{
					continue;
				}

				for (String guid : guidListMap.get(className))
				{
					try
					{
						objectGuid.setGuid(guid);
						this.getBOAS().getObjectByGuid(objectGuid);
					}
					catch (Exception e)
					{
						continue;
					}

					if (guidListMapWithACL.get(className) == null)
					{
						guidListMapWithACL.put(className, new ArrayList<String>());
					}
					guidListMapWithACL.get(className).add(guid);
				}
			}
		}

		if (SetUtils.isNullMap(guidListMapWithACL))
		{
			return;
		}

		this.reportECScript(exportFileType, guidListMapWithACL, reportcondition, jobGuid, isMail, isScript);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGeneric(dyna.common.bean.model.UIObject,
	 * java.lang.String, dyna.common.SearchCondition, java.lang.String,
	 * java.util.Map)
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reportGenericHelp(String className, String uiName, ReportTypeEnum exportFileType, List<String> guidList, SearchCondition reportcondition, boolean isScript,
			String jobGuid, boolean isMail) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		List<FoundationObject> foundationList = new ArrayList<FoundationObject>();

		String content = "";

		if (className == null)
		{
			return;
		}

		List<FoundationObject> authList = new ArrayList<FoundationObject>();
		List<String> authGuidList = new ArrayList<String>();
		if (guidList != null && guidList.size() > 0)
		{
			for (String guid : guidList)
			{
				ObjectGuid objectGuid = new ObjectGuid();
				objectGuid.setGuid(guid);
				objectGuid.setClassName(className);
				objectGuid.setClassGuid(this.getEMM().getClassByName(className).getGuid());

				FoundationObject obj = null;
				try
				{
					obj = this.getBOAS().getObjectByGuid(objectGuid);
				}
				catch (Exception e)
				{
					continue;
				}

				authList.add(obj);
				authGuidList.add(guid);
			}
		}

		if (SetUtils.isNullList(authList))
		{
			return;
		}

		ObjectGuid objectGuid = authList.get(0).getObjectGuid();
		ClassInfo classInfo = this.getEMM().getClassByGuid(objectGuid.getClassGuid());
		if (classInfo != null && (classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN) || classInfo.hasInterface(ModelInterfaceEnum.IECOM)))
		{
			UIObjectInfo uiObject = this.getEMM().getUIObjectByName(className, uiName);
			if (uiObject.getType() != UITypeEnum.REPORT)
			{
				Map<String, List<String>> guidListMap = new HashMap<String, List<String>>();
				guidListMap.put(className, authGuidList);
				this.reportECScript(exportFileType, guidListMap, reportcondition, jobGuid, isMail, isScript);
				return;
			}
		}

		for (FoundationObject fo : authList)
		{
			FoundationObject object = ((BOASImpl) this.getBOAS()).getObject(fo.getObjectGuid());

			if (null != object.getFullName())
			{
				content = object.getFullName();
				content = GenericReportUtil.reportTitle(content, lang, object);
			}

			foundationList.add(object);
		}
		// 获取UI对象
		UIObjectInfo uiObject = this.getEMM().getUIObjectByName(className, uiName);
		List<UIField> uiFieldList = this.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
		// 封装报表需要显示的字段
		List<DetailColumnInfo> columnList = this.getDetailColumnInfo(uiObject, lang, className);
		List<ParameterColumnInfo> parameters = new ArrayList<ParameterColumnInfo>();
		ReportConfiguration configuration = new ReportConfiguration();
		configuration.setExportFileType(exportFileType);

		// UI对象有报表模板的情况 2012.6.5：
		// 若UI对象有报表模板且UI没有配置导出字段，使用报表的字段；若配置了导出字段默认使用配置字段
		File templateFile = null;
		if (null != uiObject.getReportTemplateName() && null == uiFieldList)
		{
			templateFile = new File(EnvUtils.getConfRootPath() + "conf/report/" + uiObject.getReportTemplateName());

			ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
			columnList = resolveReportTemplateField.getReportTemplateField(templateFile);
			parameters = resolveReportTemplateField.getReportTemplateParameters(templateFile);
		}
		else
		{
			if (null != uiObject.getReportTemplateName() && null != uiFieldList)
			{
				templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml");
			}
			// 报表有多条数据并且UI对象没有报表模板
			else if (guidList.size() > 1 && null == uiObject.getReportTemplateName())
			{
				templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml");
			}
			// 只有一条数据并且UI对象没有模板
			else if (guidList.size() == 1 && null == uiObject.getReportTemplateName())
			{
				if (lang.equals(LanguageEnum.ZH_TW))
				{
					templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/single_data_report_template_zh_tw.jrxml");
				}
				else
				{
					templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/single_data_report_template_zh_cn.jrxml");
				}
			}

			if (uiFieldList != null)
			{
				for (UIField field : uiFieldList)
				{
					JRPropertiesMap propertiesMap = new JRPropertiesMap();
					propertiesMap.setProperty("Pattern", field.getFormat());

					columnList.add(new DetailColumnInfo(field.getTitle(lang), String.class, field.getName(), propertiesMap));
					parameters.add(new ParameterColumnInfo(field.getName(), String.class, field.getTitle(lang), propertiesMap));
				}
			}

			for (DetailColumnInfo column : columnList)
			{
				if (!columnList.contains(column))
				{
					columnList.add(column);
				}
			}

			ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
			parameters = resolveReportTemplateField.getReportTemplateParameters(templateFile);
			for (ParameterColumnInfo parameter : parameters)
			{
				if (!parameters.contains(parameter))
				{
					parameters.add(parameter);
				}
			}
		}

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setUiObject(uiObject);
		params.setHeaderColumnList(parameters);
		params.setDetailColumnList(columnList);

		// 封装报表数据源
		ReportDataProvider reportDataProvider = new FoundationObjectReportDataProviderImpl(foundationList, reportcondition, params);

		if (null != uiFieldList)
		{
			for (UIField field : uiFieldList)
			{
				ClassField classField = DataServer.getClassModelService().getField(className, field.getName());
				if (classField == null)
				{
					continue;
				}

				if (field.getType().equals(FieldTypeEnum.CODE) || field.getType().equals(FieldTypeEnum.OBJECT))
				{
					for (FoundationObject foundation : foundationList)
					{
						ObjectGuid og = new ObjectGuid();
						if (null == foundation.get(field.getName() + "$CLASS"))
						{
							continue;
						}
						og.setClassGuid(foundation.get(field.getName() + "$CLASS").toString());
						og.setGuid(foundation.get(field.getName()).toString());
						og.setClassName(foundation.get(field.getName() + "$CLASSNAME").toString());
						FoundationObjectImpl f = (FoundationObjectImpl) this.getBOAS().getObject(og);
						foundation.put(field.getName() + "$ID$", f.getId());
						foundation.put(field.getName() + "$NAME$", f.getName());
						String title = (null == f.get("CLASSIFICATION$TITLE")) ? "" : StringUtils.getMsrTitle((String) f.get("CLASSIFICATION$TITLE"), lang.getType());
						String name = (null == f.get("CLASSIFICATION$NAME") ? "" : "[" + f.get("CLASSIFICATION$NAME") + "]");
						foundation.put(field.getName() + "$CLASSIFICATION$", title + name);
					}
				}
			}
		}

		this.setFieldAndGroup(uiObject, configuration);
		if (SetUtils.isNullList(foundationList) || foundationList.size() == 1)
		{
			configuration.setGroupField(null);
		}
		File reportFile = null;
		if (guidList.size() > 1)
		{
			String name = uiObject.getName();
			name = GenericReportUtil.reportTitle(name, lang, foundationList.get(0));

			reportFile = GenericReportUtil.getFile(name, exportFileType);
			configuration.setExportToFilePath(reportFile);
			configuration.setReportTitle(uiObject.getTitle(lang));
		}
		else
		{
			String name = foundationList.get(0).getId();
			name = GenericReportUtil.reportTitle(name, lang, foundationList.get(0));
			reportFile = GenericReportUtil.getFile(name, exportFileType);
			configuration.setExportToFilePath(reportFile);

			name = foundationList.get(0).getFullName();
			name = GenericReportUtil.reportTitle(name, lang, foundationList.get(0));
			configuration.setReportTitle(name);
		}

		configuration.setDetailColumnInfoList(columnList);

		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();
		try
		{
			// UI对象有报表模板的情况 2012.6.5：
			// 若UI对象有报表模板且UI没有配置导出字段，使用报表的字段；若配置了导出字段默认使用配置字段
			if (null != uiObject.getReportTemplateName() && null == uiFieldList)
			{
				reportBuilder.personalizedReport(templateFile, reportDataProvider, configuration, reportDataProvider.getHeaderParameter());
			}
			else
			{
				reportBuilder.generateReport(configuration, reportDataProvider, templateFile);
			}

			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(reportFile.getName());
			reportFileInfo.setFilePath(reportFile.getPath());
			reportFileInfo.setFileSize(reportFile.length());

			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()), content,
					MailCategoryEnum.INFO, this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, reportFile.getAbsolutePath(), FileOperateEnum.UPLOAD, true);
					this.addFileToJob(jobGuid, ftpObject.getFileGuid());
				}
			}

		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "export report error:" + e.getMessage(), null,
						"(" + message.substring(message.indexOf("{") + 1, message.length() - 1) + ")");
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "export report error:" + e.getMessage(), null, "");
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", e.getMessage());
			}
			else
			{
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "export report error:" + e.getMessage(), null, "");
			}
		}
	}

	/**
	 * @param uiObject
	 * @param configuration
	 */
	private void setFieldAndGroup(UIObjectInfo uiObject, ReportConfiguration configuration)
	{
		if (null != uiObject.getSortField1() && !uiObject.getSortField1().equals(uiObject.getGroupField()))
		{
			configuration.setSortField1(uiObject.getSortField1());
			configuration.setSortValue1(uiObject.getSortValue1());
		}
		if (null != uiObject.getSortField2() && !uiObject.getSortField2().equals(uiObject.getGroupField()))
		{
			configuration.setSortField2(uiObject.getSortField2());
			configuration.setSortValue2(uiObject.getSortValue2());
		}
		if (null != uiObject.getSortField3() && !uiObject.getSortField3().equals(uiObject.getGroupField()))
		{
			configuration.setSortField3(uiObject.getSortField3());
			configuration.setSortValue3(uiObject.getSortValue3());
		}
		if (null != uiObject.getGroupField())
		{
			configuration.setGroupField(uiObject.getGroupField());
		}

	}

	/**
	 * 设置导出报表字段类型
	 * 
	 * @param <T>
	 * @param title
	 * @param propertyName
	 * @param typeClass
	 * @param isTransform
	 * @return
	 */
	private DetailColumnInfo createDetailColumnInfo(String title, String propertyName, Class<?> typeClass, String transformType, final LanguageEnum lang,
			JRPropertiesMap propertiesMap)
	{
		DetailColumnInfo ret = null;
		if (typeClass.equals(Date.class) || typeClass.equals(DateTime.class))
		{
			DetailColumnInfo detailColumnInfo = new DetailColumnInfo(title, Date.class, propertyName);
			if (null != propertiesMap)
			{
				detailColumnInfo.setPropertiesMap(propertiesMap);
			}
			else
			{
				propertiesMap = new JRPropertiesMap();
				propertiesMap.setProperty("Pattern", "yyyy-MM-dd HH:mm:ss");
				detailColumnInfo.setPropertiesMap(propertiesMap);
			}
			ret = detailColumnInfo;
		}
		else if (transformType.equals("notransform"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			if (null != propertiesMap)
			{
				ret.setPropertiesMap(propertiesMap);
			}
		}
		else if (transformType.equals("spectransform"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{
					String fieldName = field.getName().replace("#", "$");
					return object.get(fieldName + "NAME");
				}
			});
		}
		else if (transformType.equals("paratransform"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{
					String fieldName = field.getName().replace("#", "$");

					if (fieldName.endsWith("$"))
					{
						return object.get(fieldName + "NAME");
					}
					else
					{
						return object.get(fieldName + "$NAME");
					}

				}
			});
		}
		else if (transformType.equals("statustransform"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{
					try
					{
						return SRSImpl.this.getMSRM().getMSRString(((FoundationObject) object).getStatus().getMsrId(), lang.toString());
					}
					catch (ServiceRequestException e)
					{
						e.printStackTrace();
					}
					return null;
				}
			});
		}
		else if (transformType.equals("boolean"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{
					try
					{
						String yes = SRSImpl.this.getMSRM().getMSRString("ID_CLIENT_DIALOG_YES", lang.toString());
						String no = SRSImpl.this.getMSRM().getMSRString("ID_CLIENT_DIALOG_NO", lang.toString());

						Object name = object.get(field.getName());
						if (null == name)
						{
							name = "";
							return name;
						}
						return (name.equals("N") ? no : yes);
					}
					catch (ServiceRequestException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			});
		}
		else if (transformType.equals("multicode"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{
					String[] titles = null == object.get(field.getName().toUpperCase() + "$TITLE") ? null
							: ((String) object.get(field.getName().toUpperCase() + "$TITLE")).split("/");
					String[] names = null == object.get(field.getName().toUpperCase() + "$NAME") ? null : ((String) object.get(field.getName().toUpperCase() + "$NAME")).split("/");

					String title = "";
					if (null != titles && titles.length > 0 && null != names)
					{
						for (int i = 0; i < titles.length; i++)
						{
							title = title + StringUtils.getMsrTitle(titles[i], lang.getType()) + (null == names[i] ? "" : "[" + names[i] + "]") + ";";
						}
					}
					return title;
				}
			});
		}
		else if (transformType.equals("coderef"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{

					String title = null == object.get(field.getName().toUpperCase() + "$TITLE") ? ""
							: StringUtils.getMsrTitle((String) object.get(field.getName().toUpperCase() + "$TITLE"), lang.getType());
					String name = (null == object.get(field.getName().toUpperCase() + "$NAME") ? "" : "[" + object.get(field.getName().toUpperCase() + "$NAME") + "]");

					return title + name;
				}
			});
		}
		else if (transformType.equals("classification"))
		{
			ret = new DetailColumnInfo(title, typeClass, propertyName);
			ret.setValueDecorater(new ReportFieldValueDecorater()
			{

				@Override
				public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
				{
					String title = StringUtils.getMsrTitle((String) object.get("CLASSIFICATION$TITLE"), lang.getType());
					String name = (null == object.get("CLASSIFICATION$NAME") ? "" : "[" + object.get("CLASSIFICATION$NAME") + "]");

					return title + name;
				}
			});
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportWBS(dyna.common.bean.data.FoundationObject
	 * , java.util.List, dyna.common.systemenum.ReportTypeEnum)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String reportWBS(FoundationObject foundation, List<FoundationObject> foundationList, ReportTypeEnum exportFileType) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		String newReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, "wbs_report_template");
		File reportTemplate = new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName);

		// 封装报表需要显示的字段
		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(reportTemplate);
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(reportTemplate);

		if (!SetUtils.isNullList(foundationList))
		{
			for (FoundationObject foun : foundationList)
			{
				ObjectGuid object = new ObjectGuid();
				object.setGuid(foun.getGuid());
				List<TaskMember> taskmemberlist = this.getPPMS().listTaskMember(object);
				String resources = "";
				for (TaskMember member : taskmemberlist)
				{
					resources = resources + member.getUserName() + "-" + member.getProjectRoleName() + ";";
				}
				foun.put("RESOURCE", resources);
			}
		}

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setHeaderColumnList(parameters);
		params.setDetailColumnList(columnList);

		// 封装报表数据源
		ReportDataProvider reportDataProvider = new FoundationObjectReportDataProviderImpl(foundation, foundationList, params);

		ReportConfiguration configuration = new ReportConfiguration();
		configuration.setExportFileType(exportFileType);

		File reportFile = GenericReportUtil.getFile(foundation.getId(), exportFileType);
		configuration.setExportToFilePath(reportFile);
		configuration.setDetailColumnInfoList(columnList);

		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();

		try
		{
			reportBuilder.personalizedReport(reportTemplate, reportDataProvider, configuration, reportDataProvider.getWBSAndDeliverablesReportHeaderParameter());
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "export report error:" + e.getMessage(), null,
						"(" + message.substring(message.indexOf("{") + 1, message.length() - 1) + ")");
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}

		return reportFile.getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.SRS#reportDeliverables(dyna.common.bean.data.
	 * FoundationObject, java.util.List, java.util.List,
	 * dyna.common.systemenum.ReportTypeEnum)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String reportDeliverables(FoundationObject foundation, List<FoundationObject> foundationList, List<StructureObject> structureList, ReportTypeEnum exportFileType)
			throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();

		String newReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, "deliverables_report_template");
		File reportTemplate = new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName);

		// 封装报表需要显示的字段
		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(reportTemplate);
		List<ParameterColumnInfo> paramsList = resolveReportTemplateField.getReportTemplateParameters(reportTemplate);

		String yes = this.getMSRM().getMSRString("ID_CLIENT_DIALOG_YES", lang.toString());
		String no = this.getMSRM().getMSRString("ID_CLIENT_DIALOG_NO", lang.toString());
		List<FoundationObject> datalist = new ArrayList<FoundationObject>();
		for (int i = 0; i < foundationList.size(); i++)
		{
			FoundationObject foundationO = foundationList.get(i);
			PMStructureUtil structure = new PMStructureUtil(structureList.get(i));

			FoundationObject data = new FoundationObjectImpl();
			data.setId(foundationO.getId());
			data.setName(foundationO.getFullName());
			data.put("IsMandatory", structure.isMandatory() == true ? yes : no);
			data.put("IsReleased", structure.isReleased() == true ? yes : no);
			data.put("BussinessObjectName", StringUtils.getMsrTitle(structure.getBONameTitle(), lang.getType()));
			if (null != structure.getEnd2Foundation())
			{
				data.put("FULLNAME", structure.getEnd2Foundation().getFullName());
				data.put("BoClassification", null == structure.getEnd2Foundation().get("CLASSIFICATION$TITLE") ? ""
						: StringUtils.getMsrTitle(structure.getEnd2Foundation().get("CLASSIFICATION$TITLE").toString(), lang.getType()));
				data.put("Status", structure.getEnd2Foundation().getStatus() == null ? ""
						: this.getMSRM().getMSRString(structure.getEnd2Foundation().getStatus().getMsrId(), lang.toString()));
				data.put("Creator", structure.getEnd2Foundation().get("CREATEUSER$NAME"));
			}

			data.put("WBSNUMBER", null == foundationO.get("WBSNUMBER") ? "" : foundationO.get("WBSNUMBER").toString());

			datalist.add(data);
		}

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setUiObject(null);
		params.setDetailColumnList(columnList);
		params.setHeaderColumnList(paramsList);

		// 封装报表数据源
		ReportDataProvider reportDataProvider = new FoundationObjectReportDataProviderImpl(foundation, datalist, params);

		ReportConfiguration configuration = new ReportConfiguration();
		configuration.setExportFileType(exportFileType);
		configuration.setDetailColumnInfoList(columnList);
		File reportFile = GenericReportUtil.getFile(foundation.getId(), exportFileType);
		configuration.setExportToFilePath(reportFile);

		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();

		try
		{
			reportBuilder.personalizedReport(reportTemplate, reportDataProvider, configuration, reportDataProvider.getWBSAndDeliverablesReportHeaderParameter());
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "export report error:" + e.getMessage(), null,
						"(" + message.substring(message.indexOf("{") + 1, message.length() - 1) + ")");
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "export report error:" + e.getMessage(), null, "");
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "export report error:" + e.getMessage(), null, "");
			}
		}

		return reportFile.getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGenericBOM(dyna.common.bean.data.ObjectGuid
	 * , dyna.common.bean.data.system.BOMRule, int, dyna.common.SearchCondition,
	 * dyna.common.systemenum.ReportTypeEnum, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void reportGenericBOM(ObjectGuid bomViewObjectGuid, DataRule dataRule, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType,
			String bomReportName, String exportType, String levelStyle, String groupStyle, String bomReportTemplateName, String isExportAllLevel, List<String> summaryFiledName,
			String pagesize, String reportpath, List<String> classGuids, String jobGuid, boolean isContainRepf) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		BOMView bomView = this.getBOMS().getBOMView(bomViewObjectGuid);
		FoundationObject end1 = this.getBOAS().getObject(bomView.getEnd1ObjectGuid());

		String content = end1.getFullName() + "_" + bomReportName + "|" + this.getMSRM().getMSRString("ID_CLIENT_INFO_OPERATE_SUCCESS", lang.toString());
		File reportFile = null;
		try
		{
			reportFile = this.getBOMReportStub().reportGenericBOM(bomViewObjectGuid, dataRule, level, bomSearchCondition, exportFileType, lang, bomReportName, exportType,
					levelStyle, groupStyle, bomReportTemplateName, isExportAllLevel, summaryFiledName, pagesize, reportpath, classGuids, isContainRepf);
			if (reportFile == null)
			{
				return;
			}

			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(reportFile.getName());
			reportFileInfo.setFilePath(reportFile.getPath());
			reportFileInfo.setFileSize(reportFile.length());

			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()), content,
					MailCategoryEnum.INFO, this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, reportFile.getAbsolutePath(), FileOperateEnum.UPLOAD, true);
					this.addFileToJob(jobGuid, ftpObject.getFileGuid());
				}
			}
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DSSFileInfo reportFileInfo = new DSSFileInfo();
			if (reportFile != null)
			{
				reportFileInfo.setName(reportFile.getName());
				reportFileInfo.setFilePath(reportFile.getPath());
				reportFileInfo.setFileSize(reportFile.length());
			}
			this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + e.getMessage(), content, MailCategoryEnum.ERROR,
					this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));

			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + "("
						+ e.getMessage().substring(message.indexOf("{") + 1, message.length() - 1) + ")";
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}
		finally
		{
			if (null != reportFile && reportFile.exists())
			{
				reportFile.delete();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.SRS#reportGenericPreSearch(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void reportGenericPreSearch(String preSearchGuid, List<Object> searchParameters, List<String> foundationGuid, ReportTypeEnum exportFileType, String jobGuid)
			throws ServiceRequestException
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#createReportBOMJob(dyna.common.bean.data.ObjectGuid
	 * , dyna.common.bean.data.system.BOMRule, int, dyna.common.SearchCondition,
	 * dyna.common.systemenum.ReportTypeEnum, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createReportBOMJob(ObjectGuid end1ObjectGuid, String viewName, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType, String bomReportName,
			String exportType, String levelStyle, String groupStyle, String bomScriptFileName, String bomReportTemplateName, String isExportAllLevel, List<String> summaryFiledName,
			String pagesize, String reportpath, List<String> classGuids, boolean isContainRepf) throws ServiceRequestException
	{
		if (StringUtils.isNullString(pagesize))
		{
			pagesize = "50";
		}
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();

		queueJob.setName("BOM");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.BOMReportJob");
		queueJob.setIsSinglethRead("Y");

		queueJob.setFielda(end1ObjectGuid.toString() + JOIN_CHAR + viewName);

		queueJob.setFieldd(Integer.toString(level) + JOIN_CHAR + exportFileType.toString() + JOIN_CHAR + bomReportName + JOIN_CHAR + exportType + JOIN_CHAR + levelStyle + JOIN_CHAR
				+ groupStyle + JOIN_CHAR + BooleanUtils.getBooleanStringYN(isContainRepf));
		queueJob.setFielde(this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId());

		if (bomSearchCondition != null)
		{
			List<Map<String, Boolean>> orderMapList = bomSearchCondition.getOrderMapList();
			if (null != orderMapList && orderMapList.size() > 0)
			{
				StringBuilder builder = new StringBuilder();
				for (Map<String, Boolean> orderMap : orderMapList)
				{
					if (builder.length() != 0)
					{
						builder.append(JOIN_CHAR);
					}
					builder.append(orderMap.toString());
				}
				queueJob.setFieldg(builder.toString().length() == 0 ? null : builder.toString());
			}

			queueJob.setFieldh(this.buildListToString(summaryFiledName));
			queueJob.setFieldf(this.buildListToString(classGuids));

			String modelInterface = null;
			queueJob.setFieldj(bomSearchCondition.getSearchValue() + JOIN_CHAR + modelInterface + JOIN_CHAR + bomScriptFileName);

			queueJob.setFieldi(
					bomReportTemplateName + JOIN_CHAR + (StringUtils.isNullString(isExportAllLevel) ? "0" : isExportAllLevel) + JOIN_CHAR + pagesize + JOIN_CHAR + reportpath);

			queueJob.setFieldk(this.buildListToString(bomSearchCondition.getBOGuidList()));
			queueJob.setFieldl(this.buildListToString(bomSearchCondition.getClassificationGuidList()));
			queueJob.setFieldm(this.buildListToString(bomSearchCondition.getResultFieldList()));
			queueJob.setFieldn(this.buildListToString(bomSearchCondition.listResultUINameList()));

			ObjectGuid objectGuid = bomSearchCondition.getObjectGuid();
			queueJob.setFieldo(objectGuid == null ? null : objectGuid.toString());
		}

		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);

	}

	public synchronized JSS getJSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(JSS.class);
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
	 * dyna.net.service.brs.SRS#createReportGenericPreSearchJob(java.lang.String
	 * , java.util.List, java.util.List, dyna.common.systemenum.ReportTypeEnum)
	 */
	@Override
	public void createReportGenericPreSearchJob(String preSearchGuid, List<String> searchParameters, List<String> foundationGuid, ReportTypeEnum exportFileType)
			throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		queueJob.setName("PreSearch");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.PreSearchRepeortJob");
		queueJob.setIsSinglethRead("Y");

		queueJob.setFielda(preSearchGuid);
		queueJob.setFieldb(this.buildListToString(searchParameters));

		queueJob.setFieldc(exportFileType.toString());
		queueJob.setFieldd(this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId());

		if (null != foundationGuid)
		{
			int size = foundationGuid.size();
			int tempSize = 0;

			if (size <= 90)
			{
				queueJob.setFielde(foundationGuid.toString().replace(" ", "").substring(1, foundationGuid.toString().replace(" ", "").length() - 1));
			}
			else
			{
				queueJob.setFielde(this.returnValue(0, 90, foundationGuid));
				tempSize = size - 90;
				if (tempSize <= 90)
				{
					queueJob.setFieldf(this.returnValue(90, size, foundationGuid));
				}
				else
				{
					queueJob.setFieldf(this.returnValue(90, 180, foundationGuid));
					tempSize = tempSize - 90;
					if (tempSize <= 90)
					{
						queueJob.setFieldg(this.returnValue(180, size, foundationGuid));
					}
					else
					{
						queueJob.setFieldg(this.returnValue(180, 270, foundationGuid));
						tempSize = tempSize - 90;
						if (tempSize <= 90)
						{
							queueJob.setFieldh(this.returnValue(270, size, foundationGuid));
						}
						else
						{
							queueJob.setFieldh(this.returnValue(270, 360, foundationGuid));
							tempSize = tempSize - 90;
							if (tempSize <= 90)
							{
								queueJob.setFieldi(this.returnValue(360, size, foundationGuid));
							}
							else
							{
								queueJob.setFieldi(this.returnValue(360, 450, foundationGuid));
								tempSize = tempSize - 90;
								if (tempSize <= 90)
								{
									queueJob.setFieldj(this.returnValue(450, size, foundationGuid));
								}
								else
								{
									queueJob.setFieldj(this.returnValue(450, 540, foundationGuid));
									tempSize = tempSize - 90;
									if (tempSize <= 90)
									{
										queueJob.setFieldk(this.returnValue(540, size, foundationGuid));
									}
									else
									{
										queueJob.setFieldk(this.returnValue(540, 630, foundationGuid));
										tempSize = tempSize - 90;
										if (tempSize <= 90)
										{
											queueJob.setFieldl(this.returnValue(630, size, foundationGuid));
										}
										else
										{
											queueJob.setFieldl(this.returnValue(630, 720, foundationGuid));
											tempSize = tempSize - 90;
											if (tempSize <= 90)
											{
												queueJob.setFieldm(this.returnValue(720, size, foundationGuid));
											}
											else
											{
												queueJob.setFieldm(this.returnValue(720, 810, foundationGuid));
												tempSize = tempSize - 90;
												if (tempSize <= 90)
												{
													queueJob.setFieldn(this.returnValue(810, size, foundationGuid));
												}
												else
												{
													queueJob.setFieldn(this.returnValue(810, 900, foundationGuid));
													tempSize = tempSize - 90;
													if (tempSize <= 90)
													{
														queueJob.setFieldo(this.returnValue(900, size, foundationGuid));
													}
													else
													{
														queueJob.setFieldo(this.returnValue(900, 990, foundationGuid));
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);
	}

	private String returnValue(int beginSize, int endSize, List<String> foundationGuid)
	{
		List<String> tempFoundationGuid = foundationGuid.subList(beginSize, endSize);
		return tempFoundationGuid.toString().replace(" ", "").substring(1, tempFoundationGuid.toString().replace(" ", "").length() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.SRS#personalizedReport()
	 */
	@Override
	public Map<String, Object> personalizedReportData(ObjectGuid bomViewObjectGuid, int level, SearchCondition bomSearchCondition, String bomScriptFileName,
			ReportTypeEnum exportFileType, String bomReportName, String exportType, String levelStyle, String groupStyle) throws ServiceRequestException
	{
		Map<String, Object> data = this.getBOMReportStub().reportBOM(bomViewObjectGuid, level, bomSearchCondition, bomScriptFileName, exportFileType, bomReportName, exportType,
				levelStyle, groupStyle);
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.SRS#personalizedReport(java.util.Map,
	 * dyna.common.systemenum.ReportTypeEnum, java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	@Override
	public void personalizedReport(Map<String, Object> data, String bomReportName, String bomReportTemplateName, ObjectGuid bomViewObjectGuid, int level,
			SearchCondition searchCondition, ReportTypeEnum exportFileType, String exportType, String levelStyle, String groupStyle, String jobGuid) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		File file = GenericReportUtil.getFile(bomReportName, exportFileType);

		File reportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/bomReport/" + bomReportTemplateName);
		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(reportFile);
		List<ParameterColumnInfo> paramsList = resolveReportTemplateField.getReportTemplateParameters(reportFile);

		ReportConfiguration configuration = new ReportConfiguration();
		configuration.setExportFileType(exportFileType);
		configuration.setExportToFilePath(file);
		configuration.setDetailColumnInfoList(columnList);

		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();
		String newReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, bomReportTemplateName.replace(".jrxml", ""));

		List<FoundationObject> foundationList = (List<FoundationObject>) data.get("datalist");

		if (paramsList != null)
		{
			Map<String, Object> parameters = (Map<String, Object>) data.get("parameters");
			if (parameters != null)
			{
				Set<String> keySet = parameters.keySet();
				Iterator<String> it = keySet.iterator();
				while (it.hasNext())
				{
					String param = (String) parameters.get(it.next());
					if (!paramsList.contains(param))
					{
						paramsList.add(new ParameterColumnInfo(param, String.class, param, null));
					}
				}
			}
		}

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setUiObject(null);
		params.setDetailColumnList(columnList);
		params.setHeaderColumnList(paramsList);

		ReportDataProvider<FoundationObject> provider = new FoundationObjectReportDataProviderImpl(foundationList, null, params);

		try
		{
			reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
					(Map<String, Object>) data.get("parameters"));

			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(file.getName());
			reportFileInfo.setFilePath(file.getPath());
			reportFileInfo.setFileSize(file.length());

			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()), bomReportName,
					MailCategoryEnum.INFO, this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, file.getAbsolutePath(), FileOperateEnum.UPLOAD, true);
					this.addFileToJob(jobGuid, ftpObject.getFileGuid());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(file.getName());
			reportFileInfo.setFilePath(file.getPath());
			reportFileInfo.setFileSize(file.length());

			this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()), bomReportName, MailCategoryEnum.ERROR,
					this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + "("
						+ e.getMessage().substring(message.indexOf("{") + 1, message.length() - 1) + ")";
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}
		finally
		{
			file.delete();
		}

	}

	@Override
	public void createReportGenericProductSummaryObjectJob(ObjectGuid productObjectGuid, SearchCondition searchCondition, String relationTemplateName,
			ReportTypeEnum exportFileType, String bomReportTemplateName, String reportName, String pagesize, String reportpath) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		if (StringUtils.isNullString(pagesize))
		{
			pagesize = "20";
		}
		queueJob.setName("Summary");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.SummaryReportJob");
		queueJob.setIsSinglethRead("Y");

		queueJob.setFielda(productObjectGuid.toString());

		queueJob.setFieldb(this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId());
		queueJob.setFieldc(this.buildListToString(searchCondition.getBOGuidList()));
		queueJob.setFieldd(this.buildListToString(searchCondition.getClassificationGuidList()));
		queueJob.setFielde(searchCondition.getSearchValue());

		List<Map<String, Boolean>> orderMapList = searchCondition.getOrderMapList();
		if (null != orderMapList && orderMapList.size() > 0)
		{
			StringBuilder builder = new StringBuilder();
			for (Map<String, Boolean> orderMap : orderMapList)
			{
				if (builder.length() != 0)
				{
					builder.append(JOIN_CHAR);
				}
				builder.append(orderMap.toString());
			}
			queueJob.setFieldf(builder.toString().length() == 0 ? null : builder.toString());
		}

		ModelInterfaceEnum inter = null;
		queueJob.setFieldg(inter == null ? null : inter.toString());
		queueJob.setFieldh(0 + JOIN_CHAR + 5000 + JOIN_CHAR + "1");
		queueJob.setFieldi(exportFileType.toString() + JOIN_CHAR + bomReportTemplateName + JOIN_CHAR + reportName + JOIN_CHAR + pagesize + JOIN_CHAR + reportpath);
		queueJob.setFieldj(relationTemplateName);
		queueJob.setFieldk(searchCondition.getObjectGuid().getClassName());
		if (!SetUtils.isNullList(searchCondition.getCriterionList()))
		{
			for (Criterion criter : searchCondition.getCriterionList())
			{
				if (criter.getKey().equalsIgnoreCase(SystemClassFieldEnum.CLASSGUID.getName()))
				{
					queueJob.setFieldl(criter.getKey());
					queueJob.setFieldm(criter.getValue() == null ? null : criter.getValue().toString());
				}
				else if (criter.getKey().equalsIgnoreCase(SystemClassFieldEnum.CLASSIFICATION.getName()))
				{
					queueJob.setFieldn(criter.getKey());
					queueJob.setFieldo(criter.getValue() == null ? null : criter.getValue().toString());
				}
			}
		}
		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);

		this.getJSS().createJob(queueJob);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#reportGenericProductSummaryObject(dyna.common
	 * .bean.data.ObjectGuid, dyna.common.SearchCondition,
	 * dyna.common.systemenum.ReportTypeEnum, java.lang.String)
	 */
	@Override
	public void reportGenericProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String relationTemplateName, ReportTypeEnum exportFileType,
			String bomReportTemplateName, String reportName, String pagesize, String reportpath, String jobGuid) throws ServiceRequestException
	{
		FoundationObject foundation = this.getBOAS().getObject(productObjectGuid);
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		File file = new File(EnvUtils.getConfRootPath() + "conf/comment/report/techFileSummaryReport/" + bomReportTemplateName);

		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(file);
		List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(file);

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setUiObject(null);
		params.setDetailColumnList(columnList);
		params.setHeaderColumnList(parameters);

		ReportDataProvider<FoundationObject> provider = new ProductSummaryDataProviderImpl(searchCondition, relationTemplateName, productObjectGuid, params);

		ReportConfiguration configuration = new ReportConfiguration();
		configuration.setExportFileType(exportFileType);
		File exportToFilePath = GenericReportUtil.getFile(foundation.getId() + "_" + reportName, exportFileType);
		configuration.setExportToFilePath(exportToFilePath);
		configuration.setDetailColumnInfoList(columnList);
		if (!StringUtils.isNullString(pagesize))
		{
			configuration.setPageCount(Integer.valueOf(pagesize));
		}
		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();
		try
		{
			String newReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, bomReportTemplateName.replace(".jrxml", "") + "_template");
			reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
					provider.getHeaderParameter());

			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(exportToFilePath.getName());
			reportFileInfo.setFilePath(exportToFilePath.getPath());
			reportFileInfo.setFileSize(exportToFilePath.length());

			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()),
					foundation.getFullName() + "_" + reportName + " | " + this.getMSRM().getMSRString("ID_CLIENT_INFO_OPERATE_SUCCESS", lang.toString()), MailCategoryEnum.INFO,
					this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, exportToFilePath.getAbsolutePath(), FileOperateEnum.UPLOAD, true);
					this.addFileToJob(jobGuid, ftpObject.getFileGuid());
				}
			}
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DSSFileInfo reportFileInfo = new DSSFileInfo();
			reportFileInfo.setName(exportToFilePath.getName());
			reportFileInfo.setFilePath(exportToFilePath.getPath());
			reportFileInfo.setFileSize(exportToFilePath.length());
			this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()), reportName, MailCategoryEnum.ERROR,
					this.getUserSignature().getUserId(), Arrays.asList(reportFileInfo));
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + "("
						+ e.getMessage().substring(message.indexOf("{") + 1, message.length() - 1) + ")";
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}
		finally
		{
			if (exportToFilePath.exists())
			{
				exportToFilePath.delete();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.net.service.brs.SRS#createReportBOMJob(dyna.common.bean.data.ObjectGuid
	 * , dyna.common.bean.data.system.BOMRule, int, dyna.common.SearchCondition,
	 * dyna.common.systemenum.ReportTypeEnum, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void createReportBOMJob(ObjectGuid bomViewObjectGuid, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType, String bomReportName,
			String exportType, String levelStyle, String groupStyle, String bomScriptFileName, String bomReportTemplateName) throws ServiceRequestException
	{
		// bomReportTemplateName = "BOM_Summary_Report.jrxml";
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();

		queueJob.setName("BOM");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.BOMReportJob");
		queueJob.setIsSinglethRead("Y");

		queueJob.setFielda(bomViewObjectGuid.toString());

		queueJob.setFieldd(Integer.toString(level) + JOIN_CHAR + exportFileType.toString() + JOIN_CHAR + bomReportName + JOIN_CHAR + exportType + JOIN_CHAR + levelStyle + JOIN_CHAR
				+ groupStyle);

		queueJob.setFielde(this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId());

		if (null != bomSearchCondition)
		{
			queueJob.setFieldk(this.buildListToString(bomSearchCondition.getBOGuidList()));
			queueJob.setFieldl(this.buildListToString(bomSearchCondition.getClassificationGuidList()));
			queueJob.setFieldm(this.buildListToString(bomSearchCondition.getResultFieldList()));
			queueJob.setFieldn(this.buildListToString(bomSearchCondition.listResultUINameList()));

			List<Map<String, Boolean>> orderMapList = bomSearchCondition.getOrderMapList();
			if (null != orderMapList && orderMapList.size() > 0)
			{
				StringBuilder builder = new StringBuilder();
				for (Map<String, Boolean> orderMap : orderMapList)
				{
					if (builder.length() != 0)
					{
						builder.append(JOIN_CHAR);
					}
					builder.append(orderMap.toString());
				}
				queueJob.setFieldg(builder.length() == 0 ? null : builder.toString());
			}

			ObjectGuid objectGuid = bomSearchCondition.getObjectGuid();
			queueJob.setFieldo(objectGuid == null ? null : objectGuid.toString());

			ModelInterfaceEnum inter = null;
			String interfaceStr = inter == null ? null : inter.toString();
			queueJob.setFieldj(bomSearchCondition.getSearchValue() + JOIN_CHAR + interfaceStr + JOIN_CHAR + bomScriptFileName);
			queueJob.setFieldi(bomReportTemplateName + JOIN_CHAR + 0 + JOIN_CHAR + null);
		}
		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);

	}

	@Override
	public List<Map<String, String>> getConfigList(String type)
	{
		return this.getReportConfigStub().getConfigList(this.reportConfigFile, type);
	}

	@Override
	public void createReportGenericInstacnceObjectJob(String uiName, ReportTypeEnum exportFileType, List<ObjectGuid> objectGuidList, SearchCondition reportCondition)
			throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		queueJob.setName("FoundationReport");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.GenericReportJob");
		queueJob.setIsSinglethRead("Y");

		if (!SetUtils.isNullList(objectGuidList))
		{
			// 当批量导出多个对象时，客户端队列通知中需要显示第一个对象+“,...”，因此，需要区分是多个对象还是一个对象
			if (objectGuidList.size() == 1)
			{
				queueJob.setFielda(objectGuidList.get(0).toString());
			}
			else
			{
				queueJob.setFielda(objectGuidList.get(0).toString() + ";" + objectGuidList.get(1).toString());
			}
		}

		String loginInfo = this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId();

		queueJob.setFieldb(loginInfo + JOIN_CHAR + uiName + JOIN_CHAR + exportFileType.toString());

		this.addAllListToQueue(queueJob, objectGuidList);

		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);
	}

	/**
	 * 脚本里导出EC报表
	 * 
	 * @param uiName
	 * @param ecnObjectGuid
	 * @param exportFileType
	 * @param reportCondition
	 * @throws ServiceRequestException
	 */
	public void reportGenericECScript(String uiName, List<ObjectGuid> objectGuidList, ReportTypeEnum exportFileType, SearchCondition reportCondition, boolean isScript,
			boolean isMail) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		queueJob.setName("ECReport");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.ECReportJob");
		queueJob.setIsSinglethRead("Y");
		queueJob.setType("ECReport");

		if (!SetUtils.isNullList(objectGuidList))
		{
			// 当批量导出多个对象时，客户端队列通知中需要显示第一个对象+“,...”，因此，需要区分是多个对象还是一个对象
			if (objectGuidList.size() == 1)
			{
				queueJob.setFielda(objectGuidList.get(0).toString());
			}
			else
			{
				queueJob.setFielda(objectGuidList.get(0).toString() + ";" + objectGuidList.get(1).toString());
			}
		}

		String loginInfo = "admin" + JOIN_CHAR + "ADMINISTRATOR" + JOIN_CHAR + "ADMINISTRATOR" + JOIN_CHAR + lang.getId();
		queueJob.setFieldb(loginInfo + JOIN_CHAR + uiName + JOIN_CHAR + exportFileType.name() + JOIN_CHAR + (isScript ? "Y" : "N") + JOIN_CHAR + (isMail ? "Y" : "N"));

		this.addAllECListToQueue(queueJob, objectGuidList);

		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);
	}

	private void addAllECListToQueue(Queue queueJob, List<ObjectGuid> objectGuidList)
	{
		Map<String, List<ObjectGuid>> Map = new HashMap<String, List<ObjectGuid>>();
		if (objectGuidList != null)
		{
			for (ObjectGuid objectGuid : objectGuidList)
			{
				String className = objectGuid.getClassName();
				if (className == null)
				{
					try
					{
						className = this.getEMM().getClassByGuid(objectGuid.getGuid()).getName();
					}
					catch (ServiceRequestException e)
					{
						e.printStackTrace();
					}
				}
				if (className == null)
				{
					continue;
				}
				if (Map.get(className) == null)
				{
					Map.put(className, new ArrayList<ObjectGuid>());
				}
				Map.get(className).add(objectGuid);
			}
		}

		List<String> tmpGuidList = new ArrayList<String>();
		for (String className : Map.keySet())
		{
			List<ObjectGuid> objectGuidList_ = Map.get(className);

			// 字段长度为4000，guid长度为32，分割字符“\r\n”长度为4,再减去类名的长度加上分隔符“$”，即为存储总长度
			int MAX_SIZE = (4000 - className.length() - 1);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < objectGuidList_.size(); i++)
			{
				// 字符串的长度+分割字符“\r\n”长度+32位guid长度大于可容纳的最大长度MAX_SIZE，则分割
				if (builder.length() + 36 > MAX_SIZE)
				{
					tmpGuidList.add(builder.toString());
					builder.setLength(0);
				}

				if (builder.length() > 0)
				{
					builder.append(JOIN_CHAR);
				}
				if (builder.length() == 0)
				{
					builder.append(className + "$");
				}
				builder.append(objectGuidList_.get(i).getGuid());

				if (i == objectGuidList_.size() - 1)
				{
					tmpGuidList.add(builder.toString());
				}
			}
		}

		for (int i = 0; i < tmpGuidList.size(); i++)
		{
			if (i == 0)
			{
				queueJob.setFieldc(tmpGuidList.get(i));
			}
			else if (i == 1)
			{
				queueJob.setFieldd(tmpGuidList.get(i));
			}
			else if (i == 2)
			{
				queueJob.setFielde(tmpGuidList.get(i));
			}
			else if (i == 3)
			{
				queueJob.setFieldf(tmpGuidList.get(i));
			}
			else if (i == 4)
			{
				queueJob.setFieldg(tmpGuidList.get(i));
			}
			else if (i == 5)
			{
				queueJob.setFieldh(tmpGuidList.get(i));
			}
			else if (i == 6)
			{
				queueJob.setFieldi(tmpGuidList.get(i));
			}
			else if (i == 7)
			{
				queueJob.setFieldj(tmpGuidList.get(i));
			}
			else if (i == 8)
			{
				queueJob.setFieldk(tmpGuidList.get(i));
			}
			else if (i == 9)
			{
				queueJob.setFieldl(tmpGuidList.get(i));
			}
			else if (i == 10)
			{
				queueJob.setFieldm(tmpGuidList.get(i));
			}
			else if (i == 11)
			{
				queueJob.setFieldn(tmpGuidList.get(i));
			}
			else if (i == 12)
			{
				queueJob.setFieldo(tmpGuidList.get(i));
			}
			else
			{
				break;
			}
		}
	}

	private void addAllListToQueue(Queue queueJob, List<ObjectGuid> objectGuidList)
	{
		if (objectGuidList != null)
		{
			// 字段长度为4000，guid长度为32，分割字符“\r\n”长度为4，最多保存(32+4)*111=3996个长度的字符串
			int MAX_SIZE = 111;
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < objectGuidList.size(); i++)
			{
				if (builder.length() != 0)
				{
					builder.append(JOIN_CHAR);
				}
				builder.append(objectGuidList.get(i).getGuid());

				if (i <= 1 * MAX_SIZE)
				{
					queueJob.setFieldc(builder.toString());
					if (i == 1 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				if (i > 1 * MAX_SIZE && i <= 2 * MAX_SIZE)
				{
					queueJob.setFieldd(builder.toString());
					if (i == 2 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 2 * MAX_SIZE && i <= 3 * MAX_SIZE)
				{
					queueJob.setFielde(builder.toString());
					if (i == 3 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 3 * MAX_SIZE && i <= 4 * MAX_SIZE)
				{
					queueJob.setFieldf(builder.toString());
					if (i == 4 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 4 * MAX_SIZE && i <= 5 * MAX_SIZE)
				{
					queueJob.setFieldg(builder.toString());
					if (i == 5 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 5 * MAX_SIZE && i <= 6 * MAX_SIZE)
				{
					queueJob.setFieldh(builder.toString());
					if (i == 6 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 6 * MAX_SIZE && i <= 7 * MAX_SIZE)
				{
					queueJob.setFieldi(builder.toString());
					if (i == 7 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 7 * MAX_SIZE && i <= 8 * MAX_SIZE)
				{
					queueJob.setFieldj(builder.toString());
					if (i == 8 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 8 * MAX_SIZE && i <= 9 * MAX_SIZE)
				{
					queueJob.setFieldk(builder.toString());
					if (i == 9 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 9 * MAX_SIZE && i <= 10 * MAX_SIZE)
				{
					queueJob.setFieldl(builder.toString());
					if (i == 10 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 10 * MAX_SIZE && i <= 11 * MAX_SIZE)
				{
					queueJob.setFieldm(builder.toString());
					if (i == 11 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 11 * MAX_SIZE && i <= 12 * MAX_SIZE)
				{
					queueJob.setFieldn(builder.toString());
					if (i == 12 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 12 * MAX_SIZE && i <= 13 * MAX_SIZE)
				{
					queueJob.setFieldo(builder.toString());
					if (i == 13 * MAX_SIZE)
					{
						builder.setLength(0);
					}
				}
				else if (i > 14 * MAX_SIZE)
				{
					break;
				}
			}
		}
	}

	@Override
	public void reportAdvancedSearchObject(SearchCondition searchCondition, String jobGuid) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		List<String> resultUIObject = searchCondition.listResultUINameList();
		Folder folder = new Folder(searchCondition.getFolder().getGuid());
		folder = ((EDAPImpl) this.getEDAP()).getFolderStub().getFolder(folder.getGuid(), false);
		searchCondition.setFolder(folder);

		if (searchCondition.getObjectGuid().getClassName() == null)
		{
			searchCondition.getObjectGuid().setClassName(this.getEMM().getClassByGuid(searchCondition.getObjectGuid().getClassGuid()).getName());
		}

		AdvancedQueryTypeEnum searchType = searchCondition.getSearchType();

		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();

		String bomReportTemplateName = "AdvancedSearch.jrxml";
		// File file = new File(EnvUtils.getConfRootPath() + "conf/comment/report/presearchreport/" +
		// bomReportTemplateName);
		String newReportTemplateName = GenericReportUtil.getReportTemplateNameBylang(lang, bomReportTemplateName.replace(".jrxml", "") + "_template");

		File exportFile = null;
		boolean flag = false;
		UIObjectInfo uiObject = null;
		ClassificationUIInfo cfUIObject = null;

		String classificationGuid = searchCondition.getClassification();

		List<DetailColumnInfo> classificationColumns = new ArrayList<DetailColumnInfo>();
		if (searchType == AdvancedQueryTypeEnum.CLASSIFICATION)
		{
			if (StringUtils.isGuid(classificationGuid))
			{
				CodeItemInfo codeItemInfo = this.getEMM().getCodeItem(classificationGuid);
				if (codeItemInfo != null)
				{
					String masterGuid = codeItemInfo.getCodeGuid();
					CodeObjectInfo master = this.getEMM().getCode(masterGuid);
					if (master != null && !StringUtils.isNullString(master.getName()))
					{
						cfUIObject = this.getEMM().getCFUIObject(classificationGuid, UITypeEnum.LIST);
						List<UIField> uiFieldList = this.getEMM().listCFUIField(cfUIObject.getGuid());
						if (cfUIObject != null && !SetUtils.isNullList(uiFieldList))
						{
							for (UIField uiField : uiFieldList)
							{
								String fieldName = "CF$" + master.getName().toUpperCase() + "$" + uiField.getName().toUpperCase();
								JRPropertiesMap propertiesMap = new JRPropertiesMap();
								propertiesMap.setProperty("Pattern", uiField.getFormat());
								classificationColumns
										.add(new DetailColumnInfo(uiField.getTitle(lang), String.class, fieldName, Integer.valueOf(uiField.getWidth()), propertiesMap));
							}
						}
					}
				}
			}
		}

		// 普通查询：①类上有UI，使用类上的UI
		// ②类上没有UI，使用模板
		// 分类查询
		// ③有分类UI，没有类UI，固定系统字段+分类UI
		// ④有分类UI，有类UI，使用分类UI+类UI
		// ⑤没有分类UI，没有类UI，使用模板
		// ⑥没有分类UI，有类UI，使用类UI
		// 当为第②和⑤两种情况时
		boolean isOnlyUseTmpFile = false;
		List<DetailColumnInfo> allColumnList = new ArrayList<DetailColumnInfo>();
		List<ParameterColumnInfo> allParameterList = new ArrayList<ParameterColumnInfo>();

		if (searchType == AdvancedQueryTypeEnum.NORMAL)
		{
			if (SetUtils.isNullList(resultUIObject))
			{
				exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName);
				List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(exportFile);
				List<DetailColumnInfo> columnList_ = resolveReportTemplateField.getReportTemplateField(exportFile);

				allColumnList = this.addAllColumnToList(allColumnList, columnList_);
				allParameterList = this.addAllParameterToList(allParameterList, parameters);

				isOnlyUseTmpFile = true;
			}
			else
			{
				String className = searchCondition.getObjectGuid().getClassName();
				if (className == null)
				{
					searchCondition.getObjectGuid().setClassName(this.getEMM().getClassByGuid(searchCondition.getObjectGuid().getClassGuid()).getName());
				}
				uiObject = this.getEMM().getUIObjectByName(searchCondition.getObjectGuid().getClassName(), resultUIObject.get(0).toString());
				List<DetailColumnInfo> columnList_ = this.getDetailColumnInfo(uiObject, lang, searchCondition.getObjectGuid().getClassName());
				allColumnList = this.addAllColumnToList(allColumnList, columnList_);

				exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml");

				flag = true;
			}
		}
		else
		{
			if (SetUtils.isNullList(classificationColumns))
			{
				if (SetUtils.isNullList(resultUIObject))
				{
					exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName);
					List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(exportFile);
					List<DetailColumnInfo> templateColumnInfoList = resolveReportTemplateField.getReportTemplateField(exportFile);

					allColumnList = this.addAllColumnToList(allColumnList, templateColumnInfoList);
					allParameterList = this.addAllParameterToList(allParameterList, parameters);

					isOnlyUseTmpFile = true;
				}
				else
				{
					String className = searchCondition.getObjectGuid().getClassName();
					if (className == null)
					{
						searchCondition.getObjectGuid().setClassName(this.getEMM().getClassByGuid(searchCondition.getObjectGuid().getClassGuid()).getName());
					}
					uiObject = this.getEMM().getUIObjectByName(className, resultUIObject.get(0).toString());
					List<DetailColumnInfo> columnList_ = this.getDetailColumnInfo(uiObject, lang, searchCondition.getObjectGuid().getClassName());
					allColumnList = this.addAllColumnToList(allColumnList, columnList_);

					exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml");
				}
			}
			else
			{
				allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_FULLNAME", lang.toString()), String.class, "FULLNAME$"));
				if (SetUtils.isNullList(resultUIObject))
				{
					// 所有分类字段
					allColumnList.addAll(classificationColumns);

					// 使用固定字段
					// 类
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_BIZOBJECT", lang.toString()), String.class, "BOTITLE$"));
					// 分类
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_CLASSIFICATION", lang.toString()), String.class, "CLASSIFICATION$TITLE"));
					// 状态
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_STATUS", lang.toString()), String.class, "STATUS$"));
					// 主文件
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_FILE", lang.toString()), String.class, "FILENAME$"));
					// 更新于
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_UPDATETIME", lang.toString()), String.class, "UPDATETIME$"));
					// 创建于
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_CREATETIME", lang.toString()), String.class, "CREATETIME$"));
					// 所有者
					allColumnList.add(new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_OWNERUSER", lang.toString()), String.class, "OWNERUSER$NAME"));

					exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml");
				}
				else
				{
					allColumnList.addAll(classificationColumns);

					uiObject = this.getEMM().getUIObjectByName(searchCondition.getObjectGuid().getClassName(), resultUIObject.get(0).toString());
					List<DetailColumnInfo> columnList_ = this.getDetailColumnInfo(uiObject, lang, searchCondition.getObjectGuid().getClassName());
					allColumnList = this.addAllColumnToList(allColumnList, columnList_);

					exportFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml");
				}
			}
		}

		List<DetailColumnInfo> detailColumnsInfo = new ArrayList<DetailColumnInfo>();
		if (!SetUtils.isNullList(allColumnList))
		{
			for (DetailColumnInfo column : allColumnList)
			{
				detailColumnsInfo.add(column);

				String fieldName = column.getPropertyName();
				// 在对象中通过decode实现特殊字段取值，特殊字段不存放在resultfield上
				if (Arrays.asList(GenericReportUtil.SPECIAL_FIELD_NAME).contains(fieldName))
				{
					continue;
				}

				if (column.getPropertyName().startsWith("CF$"))
				{
					searchCondition.addResultField(column.getPropertyName(), FieldOrignTypeEnum.CLASSIFICATION);
				}
				else
				{
					if (fieldName.endsWith("$NAME"))
					{
						fieldName = fieldName.substring(0, fieldName.length() - 5);
					}
					if (fieldName.endsWith("$TITLE"))
					{
						fieldName = fieldName.substring(0, fieldName.length() - 6);
					}

					ClassField field = null;
					try
					{
						field = this.getEMM().getFieldByName(searchCondition.getObjectGuid().getClassName(), fieldName, true);
					}
					catch (Exception e)
					{
					}

					if (field == null)
					{
						continue;
					}

					fieldName = field.getName();

					searchCondition.addResultField(fieldName);
				}
			}
		}

		GenericReportParams params = this.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setUiObject(uiObject);
		// TODO lizw
		// params.setCFUIObject(cfUIObject);
		params.setDetailColumnList(allColumnList);
		params.setHeaderColumnList(allParameterList);

		List<File> listFile = new ArrayList<File>();
		try
		{
			searchCondition.setPageNum(1);
			while (true)
			{
				List<FoundationObject> allFoundationList = ((BOASImpl) this.getBOAS()).listObject(searchCondition);
				if (SetUtils.isNullList(allFoundationList))
				{
					break;
				}
				ReportDataProvider<FoundationObject> provider = new AdvanceSearchProviderImpl(allFoundationList, params, flag);

				ReportConfiguration configuration = new ReportConfiguration();
				configuration.setExportFileType(ReportTypeEnum.EXCEL);
				File exportToFilePath = GenericReportUtil.getFile(null, ReportTypeEnum.EXCEL);
				configuration.setExportToFilePath(exportToFilePath);
				configuration.setDetailColumnInfoList(detailColumnsInfo);
				configuration.setPageCount(Integer.valueOf(20));

				GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();

				// if (!SetUtils.isNullList(resultUIObject))
				if (!isOnlyUseTmpFile)
				{
					reportBuilder.generateReport(configuration, provider, new File(EnvUtils.getConfRootPath() + "conf/comment/report/generic_report_template.jrxml"));
				}
				else
				{
					reportBuilder.personalizedReport(exportFile, provider, configuration, provider.getHeaderParameter());
				}
				listFile.add(exportToFilePath);
				searchCondition.setPageNum(searchCondition.getPageNum() + 1);
			}
			if (!SetUtils.isNullList(listFile))
			{
				List<DSSFileInfo> dsslist = new ArrayList<DSSFileInfo>();
				for (int i = 0; i < listFile.size(); i++)
				{
					DSSFileInfo reportFileInfo = new DSSFileInfo();
					reportFileInfo.setName(listFile.get(i).getName());
					reportFileInfo.setFilePath(listFile.get(i).getPath());
					reportFileInfo.setFileSize(listFile.get(i).length());
					dsslist.add(reportFileInfo);
				}

				List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()),
						" advance search " + this.getMSRM().getMSRString("ID_CLIENT_INFO_OPERATE_SUCCESS", lang.toString()), MailCategoryEnum.INFO,
						this.getUserSignature().getUserId(), dsslist);

				fileTransList = this.getDSS().listFileTransDetail(fileTransList.get(0).getMasterFK());
				if (fileTransList != null)
				{
					for (DSSFileTrans ftpObject : fileTransList)
					{
						//TODO
//						DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, ftpObject.getParamFile(), FileOperateEnum.UPLOAD, true);
						if (fileTransList.size() == 1)
						{
							this.addFileToJob(jobGuid, ftpObject.getFileGuid());
						}
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()), " advance search ", MailCategoryEnum.ERROR,
					this.getUserSignature().getUserId(), null);
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + "("
						+ e.getMessage().substring(message.indexOf("{") + 1, message.length() - 1) + ")";
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}
		finally
		{
			for (int i = 0; i < listFile.size(); i++)
			{
				if (listFile.get(i).exists())
				{
					listFile.get(i).delete();
				}
			}
		}
	}

	private List<DetailColumnInfo> addAllColumnToList(List<DetailColumnInfo> destList, List<DetailColumnInfo> origList)
	{
		if (destList == null)
		{
			destList = new ArrayList<DetailColumnInfo>();
		}

		if (!SetUtils.isNullList(origList))
		{
			for (DetailColumnInfo column : origList)
			{
				boolean isExists = false;
				for (DetailColumnInfo destColumn : destList)
				{
					if (column.getPropertyName().equals(destColumn.getPropertyName()))
					{
						isExists = true;
						break;
					}
				}

				if (!isExists)
				{
					destList.add(column);
				}
			}
		}
		return destList;
	}

	private List<ParameterColumnInfo> addAllParameterToList(List<ParameterColumnInfo> destList, List<ParameterColumnInfo> origList)
	{
		if (destList == null)
		{
			destList = new ArrayList<ParameterColumnInfo>();
		}

		if (!SetUtils.isNullList(origList))
		{
			for (ParameterColumnInfo parameter : origList)
			{
				boolean isExists = false;
				for (ParameterColumnInfo destParameter : destList)
				{
					if (parameter.getParameterName().equals(destParameter.getParameterName()))
					{
						isExists = true;
						break;
					}
				}

				if (!isExists)
				{
					destList.add(parameter);
				}
			}
		}
		return destList;
	}

	@Override
	public void reportAdvancedSearchObjectJob(SearchCondition searchCondition) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		Queue queueJob = new Queue();
		queueJob.setName("AdvancedSearch");
		queueJob.setExecutorClass("dyna.app.service.brs.srs.job.AdvancedSearchJob");
		queueJob.setIsSinglethRead("Y");
		queueJob.setFieldj(this.getUserSignature().getUserId() + JOIN_CHAR + this.getUserSignature().getLoginGroupId() + JOIN_CHAR + this.getUserSignature().getLoginRoleId()
				+ JOIN_CHAR + lang.getId());

		try
		{
			List<Criterion> criterionList = searchCondition.getCriterionList();
			StringBuffer condition = new StringBuffer();

			// 拼凑检索条件到firstCondition中
			for (Criterion criterion : criterionList)
			{
				Object value = criterion.getValue();

				if (value instanceof Date)
				{
					value = DateFormat.formatYMDHMS((Date) value);
				}

				if (value != null && !"".equals(value))
				{
					value = value.toString().replace(",", Search.comma).replace(";", Search.semicolon);
				}

				condition.append(criterion.getConjunction());
				condition.append(",");
				condition.append(criterion.getKey());
				condition.append(",");
				condition.append(criterion.getOperateSignEnum());
				condition.append(",");
				condition.append(StringUtils.convertNULLtoString(value));
				condition.append(",");
				condition.append(criterion.getFieldOrignType().name());
				condition.append(";");

			}
			String firstCondition = null;
			String secondCondition = null;
			firstCondition = condition.toString();

			// 如果大于1024Byte 则把大于1024byte的字符串放到secondCondition中
			if (condition.length() > Search.maxByte)
			{
				secondCondition = condition.substring(Search.maxByte);
				firstCondition = condition.substring(0, Search.maxByte);
			}
			queueJob.setFieldb(firstCondition);
			if (null != secondCondition)
			{
				queueJob.setFieldc(secondCondition);
			}

			boolean hasSubFolders = searchCondition.hasSubFolders();
			boolean isCheckOutOnly = searchCondition.isCheckOutOnly();
			boolean isIncludeOBS = searchCondition.isIncludeOBS();
			AdvancedQueryTypeEnum searchTypeEnum = searchCondition.getSearchType();
			String searchType = searchTypeEnum == null ? AdvancedQueryTypeEnum.NORMAL.name() : searchTypeEnum.name();

			String revisiontype = "";
			if (searchCondition.getSearchRevisionTypeEnum() == null)
			{
				revisiontype = SearchRevisionTypeEnum.ISHISTORYREVISION.getType();
			}
			else
			{
				revisiontype = searchCondition.getSearchRevisionTypeEnum().getType();
			}

			queueJob.setFieldd(hasSubFolders + JOIN_CHAR + isCheckOutOnly + JOIN_CHAR + isIncludeOBS + JOIN_CHAR + revisiontype + JOIN_CHAR + searchType);

			boolean caseSensitive = searchCondition.caseSensitive();
			boolean isOwnerOnly = searchCondition.isOwnerOnly();
			boolean isOwnerGroupOnly = searchCondition.isOwnerGroupOnly();
			boolean isAdvanced = searchCondition.isAdvanced();

			queueJob.setFielde(caseSensitive + JOIN_CHAR + isOwnerOnly + JOIN_CHAR + isOwnerGroupOnly + JOIN_CHAR + isAdvanced);

			String userProductBOGuid = searchCondition.getUserProductBOGuid();
			String parentGuid = searchCondition.getParentGuid();

			String mpdelInterface = null;
			String BOMTemplateId = searchCondition.getBOMTemplateId();
			String relationTemplateId = searchCondition.getRelationTemplateId();
			queueJob.setFieldf(userProductBOGuid + JOIN_CHAR + parentGuid + JOIN_CHAR + mpdelInterface + JOIN_CHAR + BOMTemplateId + JOIN_CHAR + relationTemplateId);

			ObjectGuid object = searchCondition.getObjectGuid();
			String classguid = object.getClassGuid();
			String classname = object.getClassName();
			String foldguid = searchCondition.getFolder().getGuid();
			queueJob.setFieldg(classguid + JOIN_CHAR + classname + JOIN_CHAR + foldguid);
			if (null != searchCondition.listResultUINameList())
			{
				String uiName = searchCondition.listResultUINameList().toString();
				queueJob.setFieldh(uiName.substring(1, uiName.length() - 1));
			}
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		queueJob.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		queueJob.setJobGroup(JobGroupEnum.REPORT);
		this.getJSS().createJob(queueJob);
	}

	private List<DetailColumnInfo> getDetailColumnInfo(UIObjectInfo uiObject, LanguageEnum lang, String className) throws ServiceRequestException
	{
		List<DetailColumnInfo> columnList = new ArrayList<DetailColumnInfo>();
		DetailColumnInfo ret = new DetailColumnInfo(this.getMSRM().getMSRString("ID_SYS_FIELD_FULLNAME", lang.toString()), String.class, "FULLNAME$");
		columnList.add(ret);
		List<UIField> uiFieldList = this.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
		if (null != uiFieldList)
		{
			for (UIField field : uiFieldList)
			{
				ClassField classField = DataServer.getClassModelService().getField(className, field.getName());

				// if (classField == null)
				// {
				// classField = SystemClassFieldEnum.getSystemField(field.getName());
				// }

				if (classField == null)
				{
					continue;
				}
				DetailColumnInfo objectColumn = null;

				// ClassField classField =
				// SystemClassFieldEnum.getSystemField(field.getName());

				if (null != classField)
				{
					JRPropertiesMap propertiesMap = new JRPropertiesMap();
					propertiesMap.setProperty("Pattern", field.getFormat());

					String msrString = field.getTitle(lang);
					if (field.getName().equals("ECFLAG$") || field.getName().equals("LIFECYCLEPHASE$") || field.getName().equals("OWNERGROUP$")
							|| field.getName().equals("OWNERUSER$") || field.getName().equals("CREATEUSER$") || field.getName().equals("UPDATEUSER$")
							|| field.getName().equals("CHECKOUTUSER$"))// $name
					{
						objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);

					}
					else if (field.getName().equals("CLASSIFICATION$"))
					{
						objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
					}
					else if (field.getType().equals(FieldTypeEnum.MULTICODE))
					{
						objectColumn = this.createDetailColumnInfo(field.getTitle(lang), field.getName(), String.class, "notransform", lang, propertiesMap);
					}
					else if (field.getType().equals(FieldTypeEnum.CODEREF))
					{
						objectColumn = this.createDetailColumnInfo(field.getTitle(lang), field.getName(), String.class, "notransform", lang, propertiesMap);
					}
					else
					{
						if (field.getType().equals(FieldTypeEnum.FLOAT) || field.getType().equals(FieldTypeEnum.INTEGER))
						{
							objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
						}
						else if (field.getType().equals(FieldTypeEnum.DATE) || field.getType().equals(FieldTypeEnum.DATETIME))
						{
							objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
						}
						else
						{
							if (field.getType().equals(FieldTypeEnum.CODE) || field.getType().equals(FieldTypeEnum.OBJECT))
							{
								if (null == field.getFormat())
								{
									objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
								}
								else
								{
									if (field.getFormat().split("-").length == 1)
									{
										objectColumn = this.createDetailColumnInfo(field.getTitle(lang), field.getName() + "$" + field.getFormat(), String.class, "notransform",
												lang, null);
									}
									else
									{
										String[] fields = field.getFormat().split("-");
										for (int i = 0; i < fields.length; i++)
										{
											objectColumn = this.createDetailColumnInfo(field.getTitle(lang), field.getName() + "$" + fields[i], String.class, "notransform", lang,
													null);
											columnList.add(objectColumn);
										}
										continue;
									}
								}
							}
							else if (field.getType().equals(FieldTypeEnum.CLASSIFICATION))
							{
								objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
							}
							else if (field.getType().equals(FieldTypeEnum.CODEREF))
							{
								objectColumn = this.createDetailColumnInfo(field.getTitle(lang), field.getName(), String.class, "notransform", lang, propertiesMap);
							}
							else if (field.getType().equals(FieldTypeEnum.MULTICODE))
							{
								objectColumn = this.createDetailColumnInfo(field.getTitle(lang), field.getName(), String.class, "notransform", lang, propertiesMap);
							}
							else if (field.getType().equals(FieldTypeEnum.STATUS))
							{
								objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
							}
							else
							{
								objectColumn = this.createDetailColumnInfo(msrString, field.getName(), String.class, "notransform", lang, propertiesMap);
							}
						}

					}
				}
				if (!columnList.contains(objectColumn))
				{
					columnList.add(objectColumn);
				}
			}
		}
		return columnList;
	}

	protected synchronized EDAP getEDAP() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EDAP.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public void reportECScript(ReportTypeEnum exportFileType, Map<String, List<String>> guidListMap, SearchCondition reportcondition, String jobGuid, boolean isMail,
			boolean isScript) throws ServiceRequestException
	{

		Queue queue = this.getQueueByGuid(jobGuid);
		User jobCreator = this.getUserByGuid(queue.getCreateUserGuid());

		List<DSSFileInfo> listDSFile = new ArrayList<DSSFileInfo>();
		if (!SetUtils.isNullMap(guidListMap))
		{
			List<Map<String, String>> allECConfigList = this.getConfigList("4");

			for (String className : guidListMap.keySet())
			{
				ObjectGuid objectGuid = new ObjectGuid();
				objectGuid.setClassName(className);
				objectGuid.setClassGuid(this.getEMM().getClassByName(className).getGuid());
				for (String guid : guidListMap.get(className))
				{
					objectGuid.setGuid(guid);
					FoundationObject obj = this.getBOAS().getObjectByGuid(objectGuid);
					if (obj == null)
					{
						return;
					}

					objectGuid = obj.getObjectGuid();
					List<File> listFile = new ArrayList<File>();
					try
					{
						ClassInfo classInfo = this.getEMM().getClassByGuid(objectGuid.getClassGuid());
						// 假如该实例为工程变更
						if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IUpdatedECN))
						{
							{
								List<FoundationObject> ecoList = this.getUECS().getECOByECNAll(objectGuid);
								List<FoundationObjectImpl> ecoUseList = new ArrayList<FoundationObjectImpl>();
								File file = null;
								for (FoundationObject eco : ecoList)
								{
									if (this.isCanceledECO(eco))
									{
										continue;
									}

									ClassInfo ecoClassInfo = this.getEMM().getClassByGuid(eco.getObjectGuid().getClassGuid());
									if (ecoClassInfo != null)
									{
										eco = this.getBOAS().getObject(eco.getObjectGuid());
										ecoUseList.add((FoundationObjectImpl) eco);
									}
								}
								if (!SetUtils.isNullList(ecoUseList))
								{
									if (allECConfigList == null || allECConfigList.size() == 0)
									{
										throw new ServiceRequestException("can not find alleco template file in config, please check!");
									}
									file = this.reportAllEC(obj.getId(), ecoUseList, objectGuid, exportFileType, allECConfigList.get(0), jobCreator.getUserId());
									listFile.add(file);
								}

							}

							// 添加所有ECO文件为ECN的附件
							if (listFile.size() > 0)
							{
								for (File ecoFile : listFile)
								{
									listDSFile.add(this.attachFileToObject(ecoFile, objectGuid, isScript));
								}
								if (isMail)
								{
									this.sendMailToUser(listDSFile, jobCreator.getUserId());
								}
							}

						}
						else if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IECOM))
						{
							FoundationObject eco = this.getBOAS().getObject(objectGuid);
							if (this.isCanceledECO(eco))
							{
								continue;
							}

							ObjectGuid ecnObjectGuid = null;
							List<FoundationObject> ecnList = this.getUECS().getWhereUsedECNByECO(objectGuid);
							if (ecnList != null && ecnList.size() != 0)
							{
								ecnObjectGuid = ecnList.get(0).getObjectGuid();
							}

							File file = null;
							List<FoundationObjectImpl> ecoList = new ArrayList<FoundationObjectImpl>();
							ecoList.add((FoundationObjectImpl) eco);
							if (allECConfigList == null || allECConfigList.size() == 0)
							{
								throw new ServiceRequestException("can not find alleco template file in config, please check!");
							}
							file = this.reportAllEC(eco.getId(), ecoList, ecnObjectGuid, exportFileType, allECConfigList.get(0), jobCreator.getUserId());
							if (file != null)
							{
								listFile.add(file);
								listDSFile.add(this.attachFileToObject(file, eco.getObjectGuid(), isScript));

								if (isMail)
								{
									List<DSSFileInfo> dsFileList = new ArrayList<DSSFileInfo>();
									dsFileList.addAll(listDSFile);
									this.sendMailToUser(dsFileList, jobCreator.getUserId());
								}
							}
						}
					}
					finally
					{
						for (int i = 0; i < listFile.size(); i++)
						{
							File file = listFile.get(i);
							if (file.exists())
							{
								file.delete();
							}
						}
					}
				}
			}
		}

		if (listDSFile.size() == 1)
		{
			this.addFileToJob(jobGuid, listDSFile.get(0).getGuid());
		}
	}

	private Queue getQueueByGuid(String jobGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> paramClass = new HashMap<String, Object>();
		paramClass.put("GUID", jobGuid);

		return sds.queryObject(Queue.class, paramClass);
	}

	private User getUserByGuid(String userGuid) throws ServiceRequestException
	{

		return this.getAAS().getUser(userGuid);
	}

	private boolean isCanceledECO(FoundationObject eco) throws ServiceRequestException
	{
		if (eco == null || !StringUtils.isGuid(eco.getLifecyclePhaseGuid()))
		{
			return false;
		}

		LifecyclePhaseInfo lifectclePhaseInfo = this.getEMM().getLifecyclePhaseInfo(eco.getLifecyclePhaseGuid());
		if (lifectclePhaseInfo != null)
		{
			if (ECOLifecyclePhaseEnum.Canceled.name().equals(lifectclePhaseInfo.getName()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * 添加文件附件到对象
	 * 
	 * @param file
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private DSSFileInfo attachFileToObject(File file, ObjectGuid objectGuid, boolean isScript) throws ServiceRequestException
	{
		if (file == null)
		{
			return null;
		}

		DSSFileInfo dsFile = this.transferFileToDSFile(file);
		if (isScript)
		{
			dsFile = this.getDSS().attachFile(objectGuid, dsFile);
			dsFile.setFilePath(file.getPath());
			List<DSSFileInfo> listFile = new ArrayList<DSSFileInfo>();
			listFile.add(dsFile);

			DSSFileTrans fileTrans = this.getDSS().uploadFile(dsFile.getGuid(), dsFile.getFilePath());
			//TODO
//			DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), fileTrans, fileTrans.getParamFile(), FileOperateEnum.UPLOAD, true);
		}

		return dsFile;
	}

	private void addFileToJob(String jobGuid, String fileGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Queue paramMap = new Queue();
		paramMap.setFileGuid(fileGuid);
		paramMap.setUpdateUserGuid(this.getOperatorGuid());
		paramMap.setGuid(jobGuid);
		sds.update(Queue.class, paramMap, "update");
	}

	/**
	 * 给用户发送邮件通知.
	 * 
	 * @param listFile
	 * @throws ServiceRequestException
	 */
	private void sendMailToUser(List<DSSFileInfo> listFile, String jobCreator) throws ServiceRequestException
	{
		LanguageEnum lang = this.getUserSignature().getLanguageEnum();
		if (listFile.size() > 0)
		{
			List<DSSFileTrans> fileTransList = this.getSMS().sendMail4Report(this.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_NAME", lang.toString()),
					" ec report " + this.getMSRM().getMSRString("ID_CLIENT_INFO_OPERATE_SUCCESS", lang.toString()), MailCategoryEnum.INFO, jobCreator, listFile);
			fileTransList = this.getDSS().listFileTransDetail(fileTransList.get(0).getMasterFK());
			if (fileTransList != null)
			{
				for (DSSFileTrans ftpObject : fileTransList)
				{
					//TODO
//					DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), ftpObject, ftpObject.getParamFile(), FileOperateEnum.UPLOAD, true);
				}
			}
		}
	}

	public GenericReportParams createGenericReportParamsWithService() throws ServiceRequestException
	{
		GenericReportParams params = new GenericReportParams();

		params.setBOAS(this.getBOAS());
		params.setEMM(this.getEMM());
		params.setMSRM(this.getMSRM());
		params.setEDAP(this.getEDAP());
		params.setAAS(this.getAAS());
		params.setPPMS(this.getPPMS());
		params.setBOMS(this.getBOMS());
		params.setUECS(this.getUECS());
		params.setPMS(this.getPMS());
		params.setBRM(this.getBRM());
		params.setCPB(this.getCPB());

		return params;
	}
}