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
import dyna.app.service.brs.erpi.dataExport.IntegrateT100;
import dyna.app.service.brs.erpi.dblink.DBLinkStub;
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

public class ERPT100TransferStub extends ERPTransferStub<ERPT100OperationEnum>
{
	private ERPParameter			param				= null;
	private Map<String, Boolean>	operationDataMap	= null;
	private boolean					isEmptyData			= true;
	private MSRM					msrm				= null;
	private Map<String, String>		dblinkInfoMap		= null;
	private DBLinkStub				dbLinkStub			= null;

	protected ERPT100TransferStub(ServiceContext context, ERPIImpl service, Document document) throws Exception
	{
		super(context, service);
		this.ERPType = ERPServerType.ERPT100;
		this.dbLinkStub = new DBLinkStub();
		this.integrate = new IntegrateT100(this, document, this.dbLinkStub);
		operationPollingTime = this.integrate.getOperationPollingTime();
		operationLiveTime = this.integrate.getOperationLiveTime();
		param = this.integrate.getParameter(null);
		msrm = this.stubService.getMSRM();
	}

	/**
	 * 连接中间库
	 * 
	 * @throws Exception
	 */
	public void connectDbLink() throws ServiceRequestException
	{
		try
		{
			dblinkInfoMap = this.stubService.getDbLinkConfig(ERPServerType.ERPT100);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("T100 get DBLinkConfig error!");
		}
		if (!SetUtils.isNullMap(dblinkInfoMap))
		{
			String url = dblinkInfoMap.get("url");
			String username = dblinkInfoMap.get("username");
			String password = dblinkInfoMap.get("password");
			dbLinkStub.connectDataBase(url, username, password);
		}
		else
		{
			throw new ServiceRequestException("T100 DBLinkConfig is null!");
		}
	}

	/**
	 * 关闭连接中间库
	 * 
	 * @throws ServiceRequestException
	 */
	public void closeDblink() throws ServiceRequestException
	{
		dbLinkStub.close();
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
	@SuppressWarnings("unchecked")
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
		Element dataKeyEle = new Element("datakey").setAttribute("type", "FOM");
		dataKeyEle.addContent(new Element("key").setAttribute("name", "EntId").setText(factory));
		return dataKeyEle;
	}

	@Override
	protected List<String> getERPTypeNameAsList()
	{
		return Arrays.asList(ERPServerType.ERPT100.getProName());
	}

	public String callWS() throws Exception
	{
		String returnString = null;
		if (this.useCross)
		{
			CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
			String originalXML = this.wrapWithCross(crossServiceConfig);
			this.saveTempFile(this.getDataKey() + "_request.xml", originalXML);
			String crossReturnString = (String) stubService.getCrossStub().crossExportDataToErp(originalXML);
			this.saveTempFile(this.getDataKey() + "_response.xml", crossReturnString);
			returnString = this.unwrapWithCross(crossReturnString, crossServiceConfig.getHostIsEncode());
			if (StringUtils.isNullString(returnString))
			{
				throw new IllegalArgumentException(this.parseCrossErrorString(crossReturnString));
			}
		}
		else
		{
			returnString = this.doCallWS();
		}
		return returnString;
	}

	private String createRequestXML(List<ERPT100OperationEnum> operationList) throws IOException
	{
		Element recordElement = new Element("Record");
		recordElement.addContent(new Element("Field").setAttribute("name", "Seqkey").setAttribute("value", this.jobId));
		recordElement.addContent(new Element("Field").setAttribute("name", "Seqkey_totcnt").setAttribute("value", String.valueOf(operationList.size())));
		Element element = new Element("Request").addContent(new Element("RequestContent").addContent(new Element("Parameter").addContent(recordElement)));
		return XMLUtil.convertXML2String(element);
	}

	/**
	 * T100中间文件
	 */
	@Override
	public String callWS(String originalXML, ERPT100OperationEnum operation) throws Exception
	{
		String returnString = null;
		if (this.useCross)
		{
			CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
			originalXML = this.wrapWithCross(crossServiceConfig, originalXML, operation);
			this.saveTempFile(this.getDataKey() + "_request.xml", originalXML);
			String crossReturnString = (String) stubService.getCrossStub().crossExportDataToErp(originalXML);
			this.saveTempFile(this.getDataKey() + "_response.xml", crossReturnString);
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
		this.currentXMLPackIdx = 1;
		DynaLogger.info("JobID:" + this.jobId + "*** Request:" + DateFormat.formatYMDHMS(new Date()));
		this.saveTempFile(this.getXMLPackNo() + factoryIdIndex + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().invokeSrv(originalXML);
		DynaLogger.info("JobID:" + this.jobId + "*** Response:" + DateFormat.formatYMDHMS(new Date()));
		this.saveTempFile(this.getXMLPackNo() + factoryIdIndex + "_response.xml", returnString);
		returnString = this.unwrapWithCross(returnString, false);
		return returnString;
	}

	private String doCallWS() throws Exception
	{
		String originalXML = this.wrapWithCrossWS();
		this.currentXMLPackIdx = 1;
		DynaLogger.info("JobID:" + this.jobId + "*** Request:" + DateFormat.formatYMDHMS(new Date()));
		this.saveTempFile(this.jobId + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().invokeSrv(originalXML);
		DynaLogger.info("JobID:" + this.jobId + "*** Response:" + DateFormat.formatYMDHMS(new Date()));
		this.saveTempFile(this.jobId + "_response.xml", returnString);
		returnString = this.unwrapWithCross(returnString, false);
		return returnString;
	}

	@Override
	protected String checkConnectionDoCallWS(String originalXML, ERPT100OperationEnum operation) throws Exception
	{
		originalXML = this.wrapWithCrossWS4Other(originalXML, operation);
		this.saveTempFile(System.nanoTime() + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().invokeSrv(originalXML);
		this.saveTempFile(System.nanoTime() + "_response.xml", returnString);
		returnString = this.unwrapWithCross(returnString, false);
		return returnString;
	}

	private String wrapWithCrossWS4Other(String originalXML, ERPT100OperationEnum operation) throws Exception
	{
		Element host = this.makeHostWS();
		Element service = this.makeServiceWS4Other(operation);
		String key = this.encryptKeyMD5(host, service);
		Element payloadEle = null;
		payloadEle = new Element("payload").addContent(new Element("param").setAttribute("key", "Data").setAttribute("type", "xml").addContent(new CDATA(originalXML)));

		Element requestEle = new Element("request").setAttribute("type", "sync").setAttribute("key", key).addContent(host).addContent(service).addContent(this.makeDataKey())
				.addContent(payloadEle);

		String requestStr = XMLUtil.convertXML2String(requestEle);
		return requestStr;
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
		BooleanResult result = this.parseResponseString(returnString);
		if (result.getFlag())
		{
			// 测试中间表
			this.connectDbLink();
		}

		return result;
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

	private String wrapWithCross(CrossServiceConfig crossConfig) throws IOException
	{
		Element host = this.makeHost(crossConfig, this.userId);
		Element service = this.makeService(crossConfig);
		String key = this.encryptKeyMD5(host, service);
		Element requestEle = new Element("request").setAttribute("type", "sync").setAttribute("key", key);
		requestEle.addContent(host);
		requestEle.addContent(service);
		requestEle.addContent(this.makeDataKey());

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

	private Element makeService(CrossServiceConfig crossServiceConfig)
	{
		Element serviceEle = new Element("service");
		List<CrossIntegrate> servers = crossServiceConfig.getServices();
		boolean crossConfigured = false;
		if (!SetUtils.isNullList(servers))
		{
			for (CrossIntegrate crossServer : servers)
			{
				if (this.getERPTypeNameAsList().contains(crossServer.getServiceProd()))
				{
					serviceEle.setAttribute("prod", StringUtils.convertNULLtoString(crossServer.getServiceProd()))
							.setAttribute("ip", StringUtils.convertNULLtoString(crossServer.getServiceIP())).setAttribute("name", "PlmDBLinkProcess").setAttribute("srvver", "1.0")
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
			((IntegrateT100) this.integrate).fillOptionListSize();

			this.connectDbLink();
			this.dbLinkStub.startTransaction();

			this.currentOpeIdx = 0;
			List<ERPT100OperationEnum> hasDataOperationList = new ArrayList<ERPT100OperationEnum>();
			for (int i = 0; i < operationList.size(); i++)
			{
				this.startCount();
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
					this.isEmptyData = false;
					hasDataOperationList.add(operation);
				}
				// 将要抛转的数据写入中间库
				((IntegrateT100) this.integrate).exportDataIntoTempTable(operation, this.currentOpeIdx);
				this.currentOpeIdx++;
			}

			// 生成日志
			if (!SetUtils.isNullList(hasDataOperationList))
			{
				((IntegrateT100) this.integrate).exportLogDataIntoTable(hasDataOperationList);
			}

			this.dbLinkStub.commitTransaction();
			if (!this.isEmptyData)
			{
				String returnString = this.callWS(this.createRequestXML(hasDataOperationList), null);
				BooleanResult exportResult = this.parseResponseString(returnString);
				// 用于记录错误是否来自ERP
				this.saveJobFromERPFlag();

				if (exportResult.getFlag())
				{
					if (!SetUtils.isNullList(operationList))
					{
						for (int i = 0; i < operationList.size(); i++)
						{
							ERPT100OperationEnum operation = operationList.get(i);
							this.saveTransferStatus(this.integrate.getOperationCategory(operation), -1, this.integrate.getOperationId(operation));
						}
					}
				}
				else
				{
					this.appendResult(null, exportResult.getDetail(), true);
					return exportResult;
				}
			}

			// 如果for循环完毕，则说明所有Operation都执行成功了，这时只需要返回一个简单的true
			return BooleanResult.getNull().setDataEmpty(isEmptyData);
		}
		catch (Exception e)
		{
			this.dbLinkStub.rollBackTransaction();

			String msg = e.getMessage();
			if (msg == null)
			{
				msg = e.getCause() == null ? StringUtils.EMPTY_STRING : e.getCause().getMessage();
			}

			this.appendResult(operationName, msg, false);
			throw e;
		}
		finally
		{
			this.closeDblink();
		}
	}

	private void saveJobFromERPFlag() throws ServiceRequestException
	{
		Queue queue = this.stubService.getJSS().getJob(this.job.getGuid());
		if (queue != null)
		{
			queue.setFieldm("Y"); // 用于区别错误是否来自ERP
			this.stubService.getJSS().saveJob(queue);
		}
	}

	/**
	 * 分两步操作
	 * 第一步写中间库T100DB
	 * 第二步调webservice通知
	 */
	@Override
	protected BooleanResult exportOpeation(ERPT100OperationEnum operation, int count) throws Exception
	{
		String returnString = null;
		String message = null;
		this.checkTimeOut();
		// 因T100不需拆包，为了不改变结构，故赋值1,1没有实际意义
		Document document = this.integrate.getCreateDataXML(operation, 1, 1);
		String dataString = XMLUtil.convertXML2String(document, this.isOmitDeclarationInXML(), this.isExpandEmptyTagInXML());
		document = null;
		returnString = this.callWS(dataString, operation);
		BooleanResult result = this.parseResponseString(returnString);
		if (!result.getFlag())
		{
			return result;
		}
		message = result == null ? this.exportSuccess : "";// (String) resultEle.getAttributeValue(("value"));
		return new BooleanResult(true, message.replace(';', '\n'));
	}

	private String wrapWithCrossWS(String originalXML, ERPT100OperationEnum operation) throws IOException
	{
		Element host = this.makeHostWS();
		Element service = this.makeServiceWS(operation);
		Element dataKey = this.makeDataKey();
		Element payload = this.makePayload(originalXML);
		String key = this.encryptKeyMD5(host, service);

		Element requestEle = new Element("request").setAttribute("type", "sync").setAttribute("key", key).addContent(host).addContent(service).addContent(dataKey)
				.addContent(payload);

		String requestStr = XMLUtil.convertXML2String(requestEle);
		return requestStr;
	}

	private Element makePayload(String originalXML)
	{
		Element payloadEle = new Element("payload");
		payloadEle.addContent(new Element("param").setAttribute("key", "data").setAttribute("type", "XML").addContent(new CDATA(originalXML)));
		return payloadEle;
	}

	private String wrapWithCrossWS() throws IOException
	{
		Element host = this.makeHostWS();
		Element service = this.makeServiceWS(null);
		String key = this.encryptKeyMD5(host, service);

		Element requestEle = new Element("request").setAttribute("type", "sync").setAttribute("key", key).addContent(host).addContent(service).addContent(this.makeDataKey());

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
		String serviceName = "PlmDBLinkProcess";
		if (operation != null)
		{
			serviceName = operation.getWs();
		}
		Element service = new Element("service").setAttribute("prod", "T100").setAttribute("name", serviceName).setAttribute("srvver", "1.0").setAttribute("id",
				StringUtils.convertNULLtoString(param.getParamMap().get("id")));
		return service;
	}

	private Element makeServiceWS4Other(ERPT100OperationEnum operation)
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

	@SuppressWarnings("unchecked")
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

	/**
	 * T100中间表集成，DataKey没意义，用jobid
	 */
	@Override
	public String getDataKey()
	{
		return this.jobId;
	}
}
