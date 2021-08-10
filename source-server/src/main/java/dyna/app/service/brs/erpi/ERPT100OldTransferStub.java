package dyna.app.service.brs.erpi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.rpc.ServiceException;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.erpi.cross.util.CrossConfigureManager;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dataExport.IntegrateT100Old;
import dyna.app.service.brs.erpi.t100IntegrateService.TIPTOPServiceGateWayLocator;
import dyna.app.service.brs.erpi.t100IntegrateService.TIPTOPServiceGateWayPortType;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.Queue;
import dyna.common.dto.erp.CrossIntegrate;
import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPT100OperationEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.Base64Util;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.das.MSRM;

public class ERPT100OldTransferStub extends ERPTransferStub<ERPT100OperationEnum>
{
	private ERPParameter			param				= null;
	private Map<String, Boolean>	operationDataMap	= null;
	private boolean					isEmptyData			= true;
	private MSRM					msrm				= null;

	protected ERPT100OldTransferStub(ServiceContext context, ERPIImpl service, Document document) throws Exception
	{
		super(context, service);
		this.ERPType = ERPServerType.ERPT100;
		this.integrate = new IntegrateT100Old(this, document);
		operationPollingTime = this.integrate.getOperationPollingTime();
		operationLiveTime = this.integrate.getOperationLiveTime();
		param = this.integrate.getParameter(null);
		msrm = this.stubService.getMSRM();
	}

	/**
	 * 根据ERP返回的结果判断ERP是否成功接收这笔数据，此时ERP的状态是“正在等待”
	 * 
	 * @param returnString
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	@Override
	protected BooleanResult parseResponseString(String responseString) throws JDOMException, IOException
	{
		Element root = XMLUtil.convertString2XML(responseString).getRootElement();
		Element executionElement = root.getChild("Execution");
		Element statusElement = executionElement.getChild("Status");
		String codeValue = statusElement.getAttributeValue("code");
		String sqlCode = statusElement.getAttributeValue("sqlcode");
		String descriptionValue = statusElement.getAttributeValue("description");
		// 失败
		if (!codeValue.equalsIgnoreCase("0"))
		{
			return new BooleanResult(false, descriptionValue + "(code=" + codeValue + ",sqlcode=" + sqlCode + ")");
		}
		// 成功
		else
		{
			return new BooleanResult(true, responseString);
		}
	}

	/**
	 * 取运营中心requestContent
	 * 
	 * @return
	 * @throws Exception
	 */
	private Element getConnEle() throws Exception
	{
		Element requestEle = new Element("Request");
		Element field = new Element("Field");
		field.setAttribute("name", "condition");
		field.setAttribute("value", "AND gzoustus = 'Y'");
		Element requestContentElement = new Element("RequestContent");
		requestContentElement.addContent(new Element("Parameter").addContent(new Element("Record").addContent(field)));
		requestEle.addContent(requestContentElement);
		return requestEle;
	}

	/**
	 * @param returnString
	 * @return
	 * @throws Exception
	 */
	private List<ERPMoreCompanies> saveMoreCompanyListByReturnString(String serverType, String returnString) throws Exception
	{
		Document doc = XMLUtil.convertString2XML(returnString);
		Element root = doc.getRootElement();
		Element responseContentElement = root.getChild("ResponseContent");
		Element documentElement = responseContentElement.getChild("Document");
		List<Element> recordSetElementList = documentElement.getChildren("RecordSet");
		List<ERPMoreCompanies> morecompanyList = new ArrayList<ERPMoreCompanies>();
		if (recordSetElementList != null && recordSetElementList.size() > 0)
		{
			for (Element recordSet : recordSetElementList)
			{
				ERPMoreCompanies moreCompany = new ERPMoreCompanies();
				Element masterElement = recordSet.getChild("Master");
				Element recordElement = masterElement.getChild("Record");
				moreCompany.setERPTypeFlag(serverType);// 设置标识
				List<Element> fieldElementList = recordElement.getChildren("Field");
				if (!SetUtils.isNullList(fieldElementList))
				{
					String jc = "";
					for (Element field : fieldElementList)
					{
						if (field.getAttributeValue("name").equalsIgnoreCase("gzou001"))
						{
							// 将代号存入
							moreCompany.setCompanydh(field.getAttributeValue("value"));
							jc = field.getAttributeValue("value");
						}
						else if (field.getAttributeValue("name").equalsIgnoreCase("gzou002"))
						{
							jc = field.getAttributeValue("value") + "(" + jc + ")";
						}
					}
					moreCompany.setCompanyjc(jc);
				}
				morecompanyList.add(moreCompany);
			}
		}
		// 将多公司资料保存到数据库中
		stubService.saveMoreCompany(serverType, morecompanyList);
		return morecompanyList;
	}

	@Override
	protected Element makeDataKey()
	{
		Element dataKeyEle = new Element("datakey").setAttribute("type", "FOM").addContent(new Element("key").setAttribute("name", "EntId").setText(factory));
		return dataKeyEle;
	}

	@Override
	protected List<String> getERPTypeNameAsList()
	{
		return Arrays.asList(ERPServerType.ERPT100.getProName());
	}

	/**
	 * T100中间文件
	 */
	@Override
	public String callWS(String originalXML, ERPT100OperationEnum operation) throws Exception
	{
		String returnString = null;
		if (useCross)
		{
			CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
			originalXML = this.wrapWithCross(crossServiceConfig, originalXML, operation);
			this.saveTempFile(this.getXMLPackNo() + "_request.xml", originalXML);
			String crossReturnString = (String) stubService.getCrossStub().crossExportDataToErp(originalXML);
			this.saveTempFile(this.getXMLPackNo() + "_response.xml", crossReturnString);
			returnString = this.unwrapWithCross(crossReturnString, crossServiceConfig.getHostIsEncode());
			if (StringUtils.isNullString(returnString))
			{
				throw new IllegalArgumentException(this.parseCrossErrorString(crossReturnString));
			}
		}
		else
		{
			returnString = this.doCallWS(originalXML, operation);
		}
		return returnString;
	}

	@Override
	protected String doCallWS(String originalXML, ERPT100OperationEnum operation) throws Exception
	{
		return this.doCallWS(originalXML, operation, StringUtils.EMPTY_STRING);
	}

	private String doCallWS(String originalXML, ERPT100OperationEnum operation, String factoryIdIndex) throws Exception
	{
		originalXML = this.wrapWithCrossWS(originalXML, operation);
		// this.job.setFieldl(DateFormat.formatYMDHMS(new Date()));// TODO test
		DynaLogger.info("JobID:" + this.jobId + "*** Request:" + DateFormat.formatYMDHMS(new Date()));
		this.saveTempFile(this.getXMLPackNo() + factoryIdIndex + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().invokeSrv(originalXML);
		// this.job.setFieldm(DateFormat.formatYMDHMS(new Date()));// TODO test
		DynaLogger.info("JobID:" + this.jobId + "*** Response:" + DateFormat.formatYMDHMS(new Date()));
		// this.stubService.getJSS().saveJob(job);// TODO test
		this.saveTempFile(this.getXMLPackNo() + factoryIdIndex + "_response.xml", returnString);
		returnString = this.unwrapWithCross(returnString, false);
		return returnString;
	}

	@Override
	protected String checkConnectionDoCallWS(String originalXML, ERPT100OperationEnum operation) throws Exception
	{
		originalXML = this.wrapWithCrossWS(originalXML, operation);
		this.saveTempFile(System.nanoTime() + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().invokeSrv(originalXML);
		this.saveTempFile(System.nanoTime() + "_response.xml", returnString);
		returnString = this.unwrapWithCross(returnString, false);
		return returnString;
	}

	@Override
	protected TIPTOPServiceGateWayPortType getRemoteStub() throws MalformedURLException, ServiceException
	{
		if (this.stub == null)
		{
			this.stub = new TIPTOPServiceGateWayLocator().getTIPTOPServiceGateWayPortType(new URL(this.ERPConfig.getErpServerAddress()));
		}
		return (TIPTOPServiceGateWayPortType) this.stub;
	}

	@Override
	public List<ERPMoreCompanies> getMoreCompanyThroughWS(ERPServiceConfig ERPConfig) throws Exception
	{
		this.userId = this.stubService.getUserSignature().getUserId();
		if (lang == null)
		{
			lang = this.stubService.getUserSignature().getLanguageEnum();
		}
		this.decorateServieConfig(ERPConfig);
		this.useCross = this.isUseCross(ERPConfig);
		String returnString = this.checkConnectionCallWS(XMLUtil.convertXML2String(new Document(this.getConnEle()), true, true), ERPT100OperationEnum.EntDataGet);
		if (this.parseResponseString(returnString).getFlag())
		{
			return this.saveMoreCompanyListByReturnString(ERPConfig.getERPServerSelected(), returnString);
		}
		return null;
	}

	@Override
	public BooleanResult checkConnection(ERPServiceConfig serviceConfig) throws Exception
	{
		this.decorateServieConfig(serviceConfig);
		// 测试连接是从UI上发起的，userId是当前操作用户
		this.userId = this.stubService.getUserSignature().getUserId();
		this.useCross = this.isUseCross(serviceConfig);
		if (lang == null)
		{
			lang = this.stubService.getUserSignature().getLanguageEnum();
		}
		Element connEle = getConnEle();
		String inputString = XMLUtil.convertXML2String(new Document(connEle), true, true);
		String returnString = this.checkConnectionCallWS(inputString, ERPT100OperationEnum.EntDataGet);
		return this.parseResponseString(returnString);
	}

	@Override
	protected String wrapWithCross(CrossServiceConfig crossConfig, String originalXML, ERPT100OperationEnum operation) throws IOException
	{
		Element host = this.makeHost(crossConfig, this.userId);
		Element service = this.makeService(crossConfig, operation);
		String key = this.encryptKeyMD5(host, service);
		Element payloadEle = null;
		if (this.containParamTagInCross())
		{
			payloadEle = new Element("payload").addContent(new Element("param").setAttribute("key", "Data").setAttribute("type", "xml").addContent(new CDATA(originalXML)));
		}
		else
		{
			payloadEle = new Element("payload").addContent(new CDATA(originalXML));
		}
		Element requestEle = new Element("request").setAttribute("type", "sync").setAttribute("key", key).addContent(host).addContent(service).addContent(this.makeDataKey())
				.addContent(payloadEle);

		String requestStr = XMLUtil.convertXML2String(requestEle);
		if (crossConfig.getHostIsEncode())
		{
			requestStr = Base64Util.encryptBase64(requestStr);
		}
		return requestStr;
	}

	@Override
	protected Element makeHost(CrossServiceConfig crossServiceConfig, String loginUserId)
	{
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();
		int zone = timeZone.getRawOffset() / 3600000;
		String zoneString = "+" + String.valueOf(zone);
		Element hostEle = new Element("host").setAttribute("prod", StringUtils.convertNULLtoString((CrossServiceConfig.PRODUCT_NAME)))
				.setAttribute("ver", StringUtils.convertNULLtoString(crossServiceConfig.getHostVer()))
				.setAttribute("ip", StringUtils.convertNULLtoString(crossServiceConfig.getHostIP()))
				.setAttribute("id", StringUtils.convertNULLtoString(crossServiceConfig.getHostID())).setAttribute("lang", this.lang == null ? "" : getLang())
				.setAttribute("timezone", zoneString).setAttribute("timestamp", DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP))
				.setAttribute("acct", StringUtils.convertNULLtoString(param.getParamMap().get("acct")));
		return hostEle;
	}

	@Override
	protected Element makeService(CrossServiceConfig crossServiceConfig, ERPT100OperationEnum operation)
	{
		Element serviceEle = new Element("service");
		List<CrossIntegrate> servers = new ArrayList<CrossIntegrate>();
		servers = crossServiceConfig.getServices();
		boolean crossConfigured = false;
		if (!SetUtils.isNullList(servers))
		{
			for (CrossIntegrate crossServer : servers)
			{
				if (this.getERPTypeNameAsList().contains(crossServer.getServiceProd()))
				{
					serviceEle.setAttribute("prod", StringUtils.convertNULLtoString(crossServer.getServiceProd()))
							.setAttribute("ip", StringUtils.convertNULLtoString(crossServer.getServiceIP()))
							.setAttribute("name", this.integrate.getOperationCrossServiceName(operation)).setAttribute("srvver", "1.0")
							.setAttribute("id", StringUtils.convertNULLtoString(crossServer.getServiceID()));
					crossConfigured = true;
					break;
				}
			}
		}
		if (!crossConfigured)
		{
			throw new IllegalArgumentException(msrm.getMSRString("ID_APP_ERPI_CROSS_SYNCHRONIZE", lang.toString()));
		}
		return serviceEle;
	}

	@Override
	protected void checkTimeOut()
	{
	}

	@Override
	protected boolean containParamTagInCross()
	{
		return true;
	}

	@Override
	protected boolean isOmitDeclarationInXML()
	{
		return true;
	}

	@Override
	protected boolean isExpandEmptyTagInXML()
	{
		return true;
	}

	public String getLang()
	{
		if (lang.equals(LanguageEnum.EN))
		{
			return "en_US";
		}
		else
		{
			return lang.toString();
		}
	}

	@Override
	protected BooleanResult doExportWork() throws Exception
	{
		String operationName = null;
		String category = null;
		try
		{
			List<ERPT100OperationEnum> operationList = this.integrate.getOperationList(true);
			checkServer();
			checkTempTable();
			operationDataMap = this.integrate.getOperationDataEmptyFlag(false, null);
			long start = System.currentTimeMillis();
			// if (!this.schema.isExportAllData())
			// ---Mark:T100需要acttype字段，故强制抛转也许走compare方法
			this.integrate.compareWithDB(this.schema.isExportAllData());
			// }
			long end = System.currentTimeMillis();
			DynaLogger.info("dyna.app.service.brs.erpi.dataExport.IntegrateERP#compareWithDB time consumed: " + (end - start) + "ms");
			operationDataMap = this.integrate.getOperationDataEmptyFlag(true, operationDataMap);
			((IntegrateT100Old) this.integrate).fillOptionListSize();

			for (int i = 0; i < operationList.size(); i++)
			{
				this.startCount();
				this.currentOpeIdx = i;
				ERPT100OperationEnum operation = operationList.get(i);
				operationName = this.integrate.getOperationName(operation);
				category = operation.getCategory();
				int totalCount = this.integrate.getXMLPackageCount(operation);
				if (totalCount <= 0)
				{
					if (operationDataMap.get(category))
					{
						this.appendResult(this.integrate.getOperationName(operation), NO_DATA_SENT, false);
					}
					else
					{
						this.appendResult(this.integrate.getOperationName(operation), DATA_HAS_SENT, false);
					}
					continue;
				}
				else
				{
					isEmptyData = false;
					this.seq_sub_cnt += 1;
				}
				BooleanResult exportResult = this.exportOpeation(operation, totalCount);
				String detailMsg = !StringUtils.isNullString(exportResult.getDetail()) ? "ERP:    " + exportResult.getDetail() : null;
				exportResult.setDetail(detailMsg);
				// 注意:集成方两方面:一是数据传输,二是数据在ERP中的处理结果。有的ERP是直接把两者合并一次传回（像E10和WF），这样PLM中的prepare4NextOperation就是一个空的实现
				// 如果ERP是分两次返回结果，第一次返回数据传输结果，第二次在PLM轮询时返回数据处理结果(像易拓和易飞），则就要重写prepare4NextOperation.
				// 如果exportOperation()返回true则认为传输成功
				if (exportResult.getFlag())
				{
					BooleanResult prepareResult = this.prepare4NextOperation(operation);
					// 如果prepare4NextOperation返回false则认为刚才传的数据在ERP中处理失败
					if (!prepareResult.getFlag())
					{
						this.appendResult(operationName, prepareResult.getDetail(), false);
						return prepareResult;
					}
					else
					{
						// 如果prepare4NextOperation返回true则：如果prepare4NextOperation的结果不为空则取它的结果，否则取exportOperation的结果
						final String returnResult = StringUtils.isNullString(prepareResult.getDetail()) ? exportResult.getDetail() : prepareResult.getDetail();
						this.appendResult(operationName, returnResult, false);
						// write log into database
						this.saveTransferStatus(this.integrate.getOperationCategory(operation), -1, this.integrate.getOperationId(operation));
						continue;
					}
				}
				else
				{
					this.appendResult(operationName, exportResult.getDetail(), true);

					return exportResult;
				}
			}

			// 如果for循环完毕，则说明所有Operation都执行成功了，这时只需要返回一个简单的true
			return BooleanResult.getNull().setDataEmpty(isEmptyData);
		}
		catch (Exception e)
		{
			String msg = e.getMessage();
			if (msg == null)
			{
				msg = e.getCause() == null ? StringUtils.EMPTY_STRING : e.getCause().getMessage();
			}

			this.appendResult(operationName, msg, false);
			throw e;
		}
	}

	@Override
	protected BooleanResult exportOpeation(ERPT100OperationEnum operation, int count) throws Exception
	{
		String returnString = null;
		String message = null;
		for (int j = 1; j <= count; j++)
		{
			this.checkTimeOut();
			this.currentXMLPackIdx = j;
			Document document = this.integrate.getCreateDataXML(operation, count, j);
			String dataString = XMLUtil.convertXML2String(document, this.isOmitDeclarationInXML(), this.isExpandEmptyTagInXML());
			// set document to null, convenient for GC
			document = null;
			returnString = this.callWS(dataString, operation);
			BooleanResult result = this.parseResponseString(returnString);
			if (result.getFlag())
			{
				continue;
			}
			else
			{
				// 失败返回
				return result;
			}
		}
		Element root = XMLUtil.convertString2XML(returnString).getRootElement();
		Element result = null;
		List<Element> fields = root.getChild("ResponseContent").getChild("Parameter").getChild("Record").getChildren();
		for (Element e : fields)
		{
			if ("result".equals(e.getAttributeValue("name")))
			{
				result = e;
				break;
			}
		}
		message = result == null ? this.exportSuccess : (String) result.getAttributeValue(("value"));
		return new BooleanResult(true, message.replace(';', '\n'));
	}

	private String wrapWithCrossWS(String originalXML, ERPT100OperationEnum operation) throws IOException
	{
		Element host = this.makeHostWS();
		Element service = this.makeServiceWS(operation);
		String key = this.encryptKeyMD5(host, service);
		Element payloadEle = null;
		payloadEle = new Element("payload").addContent(new Element("param").setAttribute("key", "Data").setAttribute("type", "xml").addContent(new CDATA(originalXML)));

		Element requestEle = new Element("request").setAttribute("type", "sync").setAttribute("key", key).addContent(host).addContent(service).addContent(this.makeDataKey())
				.addContent(payloadEle);

		String requestStr = XMLUtil.convertXML2String(requestEle);
		return requestStr;
	}

	private Element makeHostWS() throws UnknownHostException
	{
		InetAddress addr = InetAddress.getLocalHost();
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();
		int zone = timeZone.getRawOffset() / 3600000;
		Element host = new Element("host").setAttribute("prod", "PLM").setAttribute("ver", "1.0").setAttribute("ip", addr.getHostAddress())
				.setAttribute("lang", this.lang == null ? "" : getLang()).setAttribute("timezone", String.valueOf(zone))
				.setAttribute("timestamp", DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP))
				.setAttribute("acct", StringUtils.convertNULLtoString(param.getParamMap().get("acct")));
		return host;
	}

	private Element makeServiceWS(ERPT100OperationEnum operation)
	{
		Element service = new Element("service").setAttribute("prod", "T100").setAttribute("name", operation.getWs()).setAttribute("srvver", "1.0").setAttribute("id",
				StringUtils.convertNULLtoString(param.getParamMap().get("id")));
		return service;
	}

	@Override
	public BooleanResult getJobStatusBySeqkeyFromERP(ERPServiceConfig conf, Queue job) throws Exception
	{
		this.useCross = "Y".equalsIgnoreCase(conf.isCrossIntegrate());
		this.factory = job.getFieldf();
		this.currentXMLPackIdx = 1;
		this.jobId = job.getFieldh();
		this.currentOpeIdx = 1;
		this.ERPConfig = conf;
		this.job = job;
		ERPConfig.setErpServerAddress("http://" + conf.getERPServerIP() + ":" + conf.getERPServerPort() + "/" + conf.getERPServerName());
		String originalXML = getOriginalXML(jobId);
		this.jobId = String.valueOf(System.currentTimeMillis());
		String returnXML = callWS(originalXML, ERPT100OperationEnum.GetJobStatus);
		BooleanResult result = parseResponseString4JobStatus(returnXML);
		return result;
	}

	private BooleanResult parseResponseString4JobStatus(String responseString) throws JDOMException, IOException, ServiceRequestException
	{
		BooleanResult result = null;
		String status = null;
		String errMeg = null;
		Element root = XMLUtil.convertString2XML(responseString).getRootElement();
		Element executionElement = root.getChild("Execution");
		Element statusElement = executionElement.getChild("Status");
		String codeValue = statusElement.getAttributeValue("code");
		String sqlCode = statusElement.getAttributeValue("sqlcode");
		String descriptionValue = statusElement.getAttributeValue("description");
		// 失败
		if (!codeValue.equalsIgnoreCase("0"))
		{
			result = new BooleanResult(false, descriptionValue + "(code=" + codeValue + ",sqlcode=" + sqlCode + ")");
			// DynaLogger.info("parseResponseString4JobStatus*" + descriptionValue + "(code=" + codeValue + ",sqlcode="
			// + sqlCode + ")");
			result.setDataEmpty(false);
			return result;
		}
		// 成功
		else
		{
			Element record = root.getChild("ResponseContent").getChild("Parameter").getChild("Record");
			List<Element> fieldList = record.getChildren("Field");
			for (Element field : fieldList)
			{
				if (field.getAttributeValue("name").equalsIgnoreCase("datastatus"))
				{
					status = field.getAttributeValue("value");
				}
				else if (field.getAttributeValue("name").equalsIgnoreCase("msgtxt"))
				{
					errMeg = field.getAttributeValue("value");
				}
			}
		}
		if (status != null)
		{
			if ("Y".equalsIgnoreCase(status))
			{
				result = new BooleanResult(true, this.stubService.getMSRM().getMSRString("ID_APP_INTEGRATEWF_ERP_DATA_SUCESS", LanguageEnum.getById(job.getFieldg()).toString()));
			}
			else if ("F".equalsIgnoreCase(status))
			{
				result = new BooleanResult(false, errMeg);
			}
		}
		return result;
	}

	private String getOriginalXML(String param)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<Request>\n");
		buffer.append("<RequestContent>\n");
		buffer.append("<Parameter>\n");
		buffer.append("<Record>\n");
		buffer.append("<Field name=\"seqkey\" value=\"");
		buffer.append(param);
		buffer.append("\"/>\n");
		buffer.append("</Record>\n");
		buffer.append(" </Parameter>\n");
		buffer.append("<Document/>\n");
		buffer.append("</RequestContent>\n");
		buffer.append("</Request>\n");
		return buffer.toString();
	}
}
