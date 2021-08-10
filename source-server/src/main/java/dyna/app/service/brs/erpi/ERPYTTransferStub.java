/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYTStub
 * wangweixia 2012-5-15
 */
package dyna.app.service.brs.erpi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.erpi.cross.util.CrossConfigureManager;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dataExport.IntegrateYT;
import dyna.app.service.brs.erpi.ytIntegrateService.TIPTOPServiceGateWayLocator;
import dyna.app.service.brs.erpi.ytIntegrateService.TIPTOPServiceGateWayPortType;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.Queue;
import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPYTOperationEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 * @author chega
 * 
 */
public class ERPYTTransferStub extends ERPTransferStub<ERPYTOperationEnum>
{
	// 易拓wsdl生成的java文件所在包地址
	private final String	packageName	= "dyna.app.service.brs.erpi.ytIntegrateService";

	protected ERPYTTransferStub(ServiceContext context, ERPIImpl service, Document document) throws Exception
	{
		super(context, service);
		this.ERPType = ERPServerType.ERPTIPTOP;
		this.integrate = new IntegrateYT(this, document);
		operationPollingTime = this.integrate.getOperationPollingTime();
		operationLiveTime = this.integrate.getOperationLiveTime();
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
	 * @return
	 * @throws Exception
	 */
	private Element getConnEle() throws Exception
	{
		Element requestEle = new Element("Request").addContent(((IntegrateYT) this.integrate).getAccessEle()).addContent(
				new Element("RequestContent").addContent(new Element("Parameter").addContent(new Element("Record"))));
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
						if (field.getAttributeValue("name").equalsIgnoreCase("azp01"))
						{
							// 将代号存入
							moreCompany.setCompanydh(field.getAttributeValue("value"));
							jc = field.getAttributeValue("value");
						}
						else if (field.getAttributeValue("name").equalsIgnoreCase("azp02"))
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
		String dataKey = "";
		if (!StringUtils.isNullString(this.jobId))
		{
			dataKey = this.getDataKey();
		}
		Element dataKeyEle = new Element("datakey").setAttribute("type", "FOM").addContent(new Element("key").setAttribute("name", "CompanyId").setText(dataKey))
				.addContent(new Element("key").setAttribute("name", "Product").setText("PLM"));
		return dataKeyEle;
	}

	@Override
	protected List<String> getERPTypeNameAsList()
	{
		return Arrays.asList(ERPServerType.ERPTIPTOP.getProName());
	}

	public String callWS(String originalXML, ERPYTOperationEnum operation, int factoryIdIndex) throws Exception
	{
		String factoryIdIndex_ = (new DecimalFormat("000")).format(factoryIdIndex);
		String returnString = null;
		if (useCross)
		{
			CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
			originalXML = this.wrapWithCross(crossServiceConfig, originalXML, operation);
			this.saveTempFile(this.getXMLPackNo() + factoryIdIndex_ + "_request.xml", originalXML);
			String crossReturnString = (String) stubService.getCrossStub().crossExportDataToErp(originalXML);
			this.saveTempFile(this.getXMLPackNo() + factoryIdIndex_ + "_response.xml", crossReturnString);
			returnString = this.unwrapWithCross(crossReturnString, crossServiceConfig.getHostIsEncode());
			if (StringUtils.isNullString(returnString))
			{
				throw new IllegalArgumentException(this.parseCrossErrorString(crossReturnString));
			}
		}
		else
		{
			returnString = this.doCallWS(originalXML, operation, factoryIdIndex_);
		}
		return returnString;
	}

	public String callWS(String originalXML, ERPYTOperationEnum operation) throws Exception
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
	protected String doCallWS(String originalXML, ERPYTOperationEnum operation) throws Exception
	{
		return this.doCallWS(originalXML, operation, StringUtils.EMPTY_STRING);
	}

	private String doCallWS(String originalXML, ERPYTOperationEnum operation, String factoryIdIndex) throws Exception
	{
		this.saveTempFile(this.getXMLPackNo() + factoryIdIndex + "_request.xml", originalXML);
		Class<?> requestClass = this.getClassByName(operation.getRequest());
		Method method = this.getRemoteStub().getClass().getDeclaredMethod(operation.getMethod(), requestClass);
		Object responseInstance = null;
		responseInstance = method.invoke(this.getRemoteStub(), requestClass.getDeclaredConstructor(String.class).newInstance(originalXML));
		// 易拓中每个ResponseClass都有一个getResponse方法
		String returnString = (String) responseInstance.getClass().getDeclaredMethod("getResponse").invoke(responseInstance);
		this.saveTempFile(this.getXMLPackNo() + factoryIdIndex + "_response.xml", returnString);

		return returnString;
	}

	@Override
	protected String checkConnectionDoCallWS(String originalXML, ERPYTOperationEnum operation) throws Exception
	{
		this.saveTempFile(System.nanoTime() + "_request.xml", originalXML);
		Class<?> requestClass = this.getClassByName(operation.getRequest());
		Method method = this.getRemoteStub().getClass().getDeclaredMethod(operation.getMethod(), requestClass);
		Object responseInstance = null;
		responseInstance = method.invoke(this.getRemoteStub(), requestClass.getDeclaredConstructor(String.class).newInstance(originalXML));
		// 易拓中每个ResponseClass都有一个getResponse方法
		String returnString = (String) responseInstance.getClass().getDeclaredMethod("getResponse").invoke(responseInstance);
		this.saveTempFile(System.nanoTime() + "_response.xml", returnString);
		return returnString;
	}

	private Class<?> getClassByName(String clsName) throws ClassNotFoundException
	{
		if (!StringUtils.isNullString(this.packageName))
		{
			clsName = this.packageName + "." + clsName;
		}
		return Class.forName(clsName);
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
		String returnString = this.checkConnectionCallWS(XMLUtil.convertXML2String(new Document(this.getConnEle()), true, true), ERPYTOperationEnum.GetOrganizationList);
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
		Element connEle = getConnEle();
		String inputString = XMLUtil.convertXML2String(new Document(connEle), true, true);
		String returnString = this.checkConnectionCallWS(inputString, ERPYTOperationEnum.GetOrganizationList);
		return this.parseResponseString(returnString);
	}

	@Override
	protected void checkTimeOut()
	{
	}

	// @Override
	// protected BooleanResult prepare4NextOperation(ERPYTOperationEnum operation) throws Exception
	// {
	// BooleanResult result = new BooleanResult(false, null);
	// while (!result.getFlag())
	// {
	// this.checkTimeOut();
	// sleep(operationPollingTime);
	// String jobStatusString = this.getERPStatus();
	// DynaLogger.debug(jobStatusString);
	// result = canDelERP(jobStatusString);
	// }
	// boolean isDeleteSuccess = false;
	// if (result.getFlag())
	// {
	// isDeleteSuccess = this.deleteDataKey();
	// }
	// BooleanResult delResult = new BooleanResult();
	// delResult.setFlag(isDeleteSuccess);
	// if (!isDeleteSuccess)
	// {
	// delResult.setDetail(result.getDetail());
	// }
	//
	// if (!delResult.getFlag())
	// {
	// // 如果删除不了易拓的临时表数据，退出
	// return delResult;
	// }
	// else
	// {
	// // 如果YT删除从临时表中删除成功了，说明当前抛数据是成功的
	// if (StringUtils.isNullString(result.getDetail()))
	// {
	// return BooleanResult.getNull();
	// }
	// else
	// {
	// result.setFlag(false);
	// if ("F".equalsIgnoreCase(result.getDetail()))
	// {
	// result.setDetail("");
	// }
	// return result;
	// }
	// }
	// }

	/**
	 * 根据datakey查询ERP的处理状态
	 * 
	 * @param serviceConfig
	 * @return
	 * @throws Exception
	 */
	// private String getERPStatus() throws Exception
	// {
	// String checkString = XMLUtil.convertXML2String(new Document(getCheckStatusEle()), true, true);
	// return this.checkConnectionCallWS(checkString, ERPYTOperationEnum.GetPLMTempTableDataStatus);
	// }

	/**
	 * 通知ERP删除datakey
	 * 
	 * @param serviceConfig
	 * @param jobId
	 * @param erpFactory
	 * @return
	 * @throws Exception
	 */
	// private boolean deleteDataKey() throws Exception
	// {
	// String returnString = this.checkConnectionCallWS(XMLUtil.convertXML2String(new Document(getDelEle()), true,
	// true), ERPYTOperationEnum.DeletePLMTempTableData);
	// return this.parseResponseString(returnString).getFlag();
	// }

	/**
	 * @return
	 * @throws Exception
	 */
	// private Element getDelEle() throws Exception
	// {
	// Element requestEle = new Element("Request").addContent(((IntegrateYT) this.integrate).getAccessEle()).addContent(
	// new Element("RequestContent").addContent(new Element("Parameter").addContent(new Element("Record").addContent(new
	// Element("Field").setAttribute("name", "datakey")
	// .setAttribute("value", this.jobId)))));
	// return requestEle;
	// }

	/**
	 * 根据ERP返回状态判断是否可以删除datakey（Y F情况下可以删除，N S 不可删除） <br/>
	 * 只有当ERP状态是Y的情况下，PLM才可以继续向ERP传数据
	 * 
	 * @param jobStatusString
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 * @throws Exception
	 */
	// private BooleanResult canDelERP(String jobStatusString) throws Exception
	// {
	// BooleanResult result = new BooleanResult();
	// result.setFlag(false);
	// if (jobStatusString == null)
	// {
	// result.setDetail("data is null");
	// return result;
	// }
	// Element root = XMLUtil.convertString2XML(jobStatusString).getRootElement();
	// Element executionElement = root.getChild("Execution");
	// Element statusElement = executionElement.getChild("Status");
	// String codeValue = statusElement.getAttributeValue("code");
	// // String descriptionValue = statusElement.getAttributeValue("description");
	// if (!codeValue.equalsIgnoreCase("0"))
	// {
	// return result;
	// }
	// // 临时表中存在此JobId，查看状态
	// else
	// {
	// Iterator<Element> it =
	// root.getChild("ResponseContent").getChild("Document").getChild("RecordSet").getChild("Master").getChild("Record").getChildren("Field")
	// .iterator();
	// Element wcf14Ele = null;
	// Element wcf17Ele = null;
	// Element tempEle = null;
	// while (it.hasNext())
	// {
	// if (wcf14Ele != null && wcf17Ele != null)
	// {
	// break;
	// }
	// tempEle = it.next();
	// if ("wcf14".equals(tempEle.getAttributeValue("name")))
	// {
	// wcf14Ele = tempEle;
	// }
	// if ("wcf17".equals(tempEle.getAttributeValue("name")))
	// {
	// wcf17Ele = tempEle;
	// }
	// }
	// String wcf14Str = null;
	// String wcf17Str = null;
	// if (wcf14Ele != null)
	// {
	// wcf14Str = wcf14Ele.getAttributeValue("value");
	// }
	// if (wcf17Ele != null)
	// {
	// wcf17Str = wcf17Ele.getAttributeValue("value");
	// }
	// result.setDetail(wcf14Str.trim());
	// // 处理失败就需将此JobId删掉(后续不再执行)
	// if (wcf17Str.equalsIgnoreCase("F"))
	// {
	// result.setFlag(true);
	// if (StringUtils.isNullString(result.getDetail()))
	// {
	// result.setDetail("F");
	// }
	// }
	// // 处理成功也将此JobId删掉
	// else if (wcf17Str.equalsIgnoreCase("Y"))
	// {
	// result.setFlag(true);
	// result.setDetail("");
	// }
	// else if (wcf17Str.equalsIgnoreCase("N"))
	// {
	// result.setFlag(false);
	// }
	// else
	// {
	// result.setFlag(false);
	// }
	// return result;
	// }
	// }

	/**
	 * 检测此JobId的封装字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	// private Element getCheckStatusEle() throws Exception
	// {
	// Element requestEle = new Element("Request").addContent(((IntegrateYT) this.integrate).getAccessEle()).addContent(
	// new Element("RequestContent").addContent(new Element("Parameter").addContent(new Element("Record").addContent(new
	// Element("Field")
	// .setAttribute("name", "condition").setAttribute("value", "wcf06 like '" + jobId + "'")))));
	// return requestEle;
	// }

	@Override
	protected boolean containParamTagInCross()
	{
		return false;
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

	@Override
	protected BooleanResult doExportWork() throws Exception
	{
		String operationName = null;
		Map<String, Boolean> operationDataMap = null;
		String category = null;
		boolean isEmptyData = true;
		try
		{
			checkServer();
			checkTempTable();
			operationDataMap = this.integrate.getOperationDataEmptyFlag(false, null);

			long start = System.currentTimeMillis();
			this.integrate.compareWithDB(this.schema.isExportAllData());
			long end = System.currentTimeMillis();
			DynaLogger.info("dyna.app.service.brs.erpi.dataExport.IntegrateERP#compareWithDB time consumed: " + (end - start) + "ms");

			operationDataMap = this.integrate.getOperationDataEmptyFlag(true, operationDataMap);
			((IntegrateYT) this.integrate).fillOptionListSize();

			List<ERPYTOperationEnum> operationList = this.integrate.getOperationList(true);
			for (int i = 0; i < operationList.size(); i++)
			{
				this.startCount();
				this.currentOpeIdx = i;
				ERPYTOperationEnum operation = operationList.get(i);
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
					this.seq_sub_cnt += 1;
					isEmptyData = false;
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
	public BooleanResult getJobStatusBySeqkeyFromERP(ERPServiceConfig conf, Queue job) throws Exception
	{
		this.useCross = "Y".equalsIgnoreCase(conf.isCrossIntegrate());
		this.factory = job.getFieldf();
		this.currentXMLPackIdx = 1;
		this.jobId = job.getFieldh() + "X";
		this.currentOpeIdx = 1;
		this.ERPConfig = conf;
		this.job = job;
		ERPConfig.setErpServerAddress("http://" + conf.getERPServerIP() + ":" + conf.getERPServerPort() + "/" + conf.getERPServerName());
		String inputString = XMLUtil.convertXML2String(new Document(this.getERPStatusEle(jobId)), true, true);
		;
		String returnXML = callWS(inputString, ERPYTOperationEnum.GetPLMTempTableDataStatus);
		BooleanResult result = parseResponseString4JobStatus(returnXML);
		return result;
	}

	private BooleanResult parseResponseString4JobStatus(String returnXML) throws JDOMException, IOException, ServiceRequestException
	{
		BooleanResult result = null;
		Element root = XMLUtil.convertString2XML(returnXML).getRootElement();
		Element executionElement = root.getChild("Execution");
		Element statusElement = executionElement.getChild("Status");
		String codeValue = statusElement.getAttributeValue("code");
		if (codeValue.equalsIgnoreCase("0"))
		{
			String status = null;
			String errMeg = null;
			List<Element> setElementList = root.getChild("ResponseContent").getChild("Document").getChildren("RecordSet");
			if (!SetUtils.isNullList(setElementList))
			{
				for (Element rs : setElementList)
				{
					List<Element> fieldList = rs.getChild("Master").getChild("Record").getChildren();
					for (Element field : fieldList)
					{
						if (field.getAttributeValue("name").equalsIgnoreCase("wcf17"))
						{
							status = field.getAttributeValue("value");
						}
						else if (field.getAttributeValue("name").equalsIgnoreCase("wcf14"))
						{
							errMeg = field.getAttributeValue("value");
						}
					}
					if (status.equalsIgnoreCase("F"))
					{
						break;
					}
					else if (!status.equalsIgnoreCase("Y"))
					{
						status = null;
						break;
					}
				}
			}
			if (status != null)
			{

				if ("Y".equalsIgnoreCase(status))
				{
					result = new BooleanResult(true, this.stubService.getMSRM()
							.getMSRString("ID_APP_INTEGRATEWF_ERP_DATA_SUCESS", LanguageEnum.getById(job.getFieldg()).toString()));
				}
				else if ("F".equalsIgnoreCase(status))
				{
					result = new BooleanResult(false, errMeg);
				}
			}

		}
		return result;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private Element getERPStatusEle(String jobId) throws Exception
	{
		Element fieldEle = new Element("Field");
		fieldEle.setAttribute("name", "seqkey");
		fieldEle.setAttribute("value", jobId);
		Element requestEle = new Element("Request").addContent(((IntegrateYT) this.integrate).getAccessEle()).addContent(
				new Element("RequestContent").addContent(new Element("Parameter").addContent(new Element("Record").addContent(fieldEle))));
		return requestEle;
	}
}
