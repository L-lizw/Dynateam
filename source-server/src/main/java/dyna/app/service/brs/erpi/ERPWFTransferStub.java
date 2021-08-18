/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPWFStub
 * wangweixia 2012-4-5
 */
package dyna.app.service.brs.erpi;

import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dataExport.IntegrateERP;
import dyna.app.service.brs.erpi.dataExport.IntegrateWF;
import dyna.app.service.brs.erpi.wfIntegrateService.WFPLMServiceLocator;
import dyna.app.service.brs.erpi.wfIntegrateService.WFPLMServiceSoap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.bean.erp.ERPQuerySchemaParameterObject;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPWFOperationEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author wangweixia
 * @author chega
 */
public class ERPWFTransferStub extends ERPTransferStub<ERPWFOperationEnum>
{
	// 取得多公司资料
	// 公司代号栏
	private final String					companyDHfield		= "MB003";
	// 公司简称栏
	private final String					companyJCfield		= "MB002";
	// 多公司资料对应的表——WF数据库表名
	private final String					companyTableName	= "DSCMB";
	private Map<String, FoundationObject>	foMap			= new HashMap<String, FoundationObject>();

	/**
	 * @param context
	 * @param service
	 * @throws Exception
	 * @throws IOException
	 * @throws JDOMException
	 */
	protected ERPWFTransferStub(Document document) throws Exception
	{
		super();
		this.ERPType = ERPServerType.ERPWORKFLOW;
		this.integrate = new IntegrateWF(this, document);
		this.operationLiveTime = this.integrate.getOperationLiveTime();
		this.operationPollingTime = this.integrate.getOperationPollingTime();
	}

	@Override
	public BooleanResult checkConnection(ERPServiceConfig serverDef) throws Exception
	{
		this.userId = this.integrate.getParameter(null).getParamMap().get("user");
		this.decorateServieConfig(serverDef);
		this.useCross = this.isUseCross(serverDef);
		String inputItem = checkConnectionString();
		String returnString = this.checkConnectionCallWS(inputItem, ERPWFOperationEnum.PLMGETDSCMB);
		return this.parseResponseString(returnString);
	}

	@Override
	protected BooleanResult parseResponseString(String responseString) throws JDOMException, IOException
	{
		Element root = XMLUtil.convertString2XML(responseString).getRootElement();
		Element resultElement = root.getChild("result");
		String result = resultElement.getTextTrim();

		// 取资料失败
		if (result.equalsIgnoreCase("fail"))
		{
			Element exception = root.getChild("exception");
			Element sysmsg = exception.getChild("sysmsg");
			Element mesmsg = exception.getChild("mesmsg");
			Element stack = exception.getChild("stack");
			DynaLogger.debug(resultElement.getTextTrim() + ":" + mesmsg.getTextTrim());
			return new BooleanResult(false, sysmsg.getValue() + "," + mesmsg.getValue() + "," + stack.getValue());
		}
		// 取资料成功
		else
		{
			return new BooleanResult(true, responseString);
		}
	}

	private String checkConnectionString() throws ServiceRequestException, IOException
	{
		ERPParameter param = this.integrate.getParameter(null);
		// 增加STD_IN的根节点
		Element rootElement = new Element("STD_IN");
		// 增加product节点
		Element productElement = new Element("Product");
		productElement.addContent("PLM");
		rootElement.addContent(productElement);
		// 增加资料库(数据库)节点
		Element companyIdElement = new Element("Companyid");
		String companyId = "";
		String useId = "";
		companyId = param.getParamMap().get("company");
		useId = param.getParamMap().get("user");
		companyIdElement.addContent(companyId);
		rootElement.addContent(companyIdElement);
		// 增加使用者代号
		Element userIdElement = new Element("Userid");
		userIdElement.addContent(useId);
		rootElement.addContent(userIdElement);
		// 增加DoAction节点
		Element doActionElement = new Element("DoAction");
		doActionElement.addContent(ERPWFOperationEnum.PLMGETDSCMB.getNo());
		rootElement.addContent(doActionElement);
		// 增加querySQL节点
		Element querySQLElement = new Element("QuerySql");
		querySQLElement.addContent("select " + companyDHfield + "," + companyJCfield + " from " + companyTableName + " where MB012 = '0'");
		rootElement.addContent(querySQLElement);
		return XMLUtil.convertXML2String(new Document(rootElement), true, true);
	}

	/**
	 * 通知WF，该Operation已经结束
	 * 
	 * @param erpFactory
	 * @param userId
	 * @param operation
	 * @param jobId
	 * @return
	 */
	private Element getNotifyEle(String erpFactory, String userId, ERPWFOperationEnum operation, String jobId)
	{
		ERPParameter param = this.integrate.getParameter(null);
		userId = StringUtils.isNullString(param.getParamMap().get("user")) ? ((IntegrateWF) this.integrate).WFUserId : param.getParamMap().get("user");
		Element ele = new Element("STD_IN").addContent(new Element("Companyid").setText(erpFactory)).addContent(new Element("Userid").setText(userId))
				.addContent(new Element("DoAction").setText(operation.getNo())).addContent(new Element("DoActive").setText(param.getParamMap().get("doActive")))
				.addContent(new Element("DoCase").setText("Y;" + jobId));
		return ele;
	}

	@Override
	protected BooleanResult doExportWork() throws Exception
	{
		String operationName = null;
		Map<String, Boolean> operationDataMap = null;
		String category = null;
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
			List<ERPWFOperationEnum> operationList = this.integrate.getOperationList(true);
			for (int i = 0; i < operationList.size(); i++)
			{
				this.startCount();
				this.currentOpeIdx = i;
				ERPWFOperationEnum operation = operationList.get(i);
				operationName = this.integrate.getOperationName(operation);
				category = operation.getCategory();
				int totalCount = this.integrate.getXMLPackageCount(operation);
				if (totalCount <= 0)
				{
					// this.appendResult(this.integrate.getOperationName(operation), NO_DATA_SENT);
					// continue;
					if (operation == ERPWFOperationEnum.PLMTODGBOM2)
					{
						String BOMOperationName = this.integrate.getOperationName(operation);
						operation = ERPWFOperationEnum.PLMToDGBOM;
						operation.setCategory(IntegrateERP.ERP_ITEM);
						operation.setName(this.stubService.getOperationAttribute(ERPServerType.ERPWORKFLOW, this.integrate.getCreateItemOperationId()).get("name"));
						operationName = this.integrate.getOperationName(operation);
						this.integrate.getItemData(this.foundationObject, null, operation);
						totalCount = this.integrate.getXMLPackageCount(operation);
						if (totalCount <= 0)
						{
							if (operationDataMap.get(category))
							{
								this.appendResult(BOMOperationName, NO_DATA_SENT, false);
							}
							else
							{
								this.appendResult(BOMOperationName, DATA_HAS_SENT, false);
							}
							continue;
						}
						if (operationDataMap.get(category))
						{
							this.appendResult(BOMOperationName, NO_DATA_SENT, false);
						}
						else
						{
							this.appendResult(BOMOperationName, DATA_HAS_SENT, false);
						}
					}
					else
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
			return BooleanResult.getNull();
		}
		catch (Exception e)
		{
			this.appendResult(operationName, e.getMessage(), false);
			throw e;
		}
	}

	@Override
	protected String doCallWS(String originalXML, ERPWFOperationEnum operation) throws Exception
	{
		this.saveTempFile(this.getXMLPackNo() + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().XMLAdapter(originalXML);
		this.saveTempFile(this.getXMLPackNo() + "_response.xml", returnString);
		return returnString;
	}

	@Override
	protected String checkConnectionDoCallWS(String originalXML, ERPWFOperationEnum operation) throws Exception
	{
		this.saveTempFile(System.nanoTime() + "_request.xml", originalXML);
		String returnString = this.getRemoteStub().XMLAdapter(originalXML);
		this.saveTempFile(System.nanoTime() + "_response.xml", returnString);
		return returnString;
	}

	@Override
	protected WFPLMServiceSoap getRemoteStub() throws MalformedURLException, ServiceException
	{
		if (this.stub == null)
		{
			this.stub = new WFPLMServiceLocator().getWFPLMServiceSoap(new URL(this.ERPConfig.getErpServerAddress()));
		}
		return (WFPLMServiceSoap) this.stub;
	}

	/**
	 * @param serverDef
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ERPMoreCompanies> getMoreCompanyThroughWS(ERPServiceConfig serverDef) throws Exception
	{
		this.userId = this.stubService.getUserSignature().getUserId();
		if (lang == null)
		{
			lang = this.stubService.getUserSignature().getLanguageEnum();
		}
		this.decorateServieConfig(serverDef);
		this.useCross = this.isUseCross(serverDef);
		String serverType = serverDef.getERPServerSelected();
		String inputItem = checkConnectionString();
		String returnString = this.checkConnectionCallWS(inputItem, ERPWFOperationEnum.PLMGETDSCMB);
		List<ERPMoreCompanies> morecompanyList = null;
		if (this.parseResponseString(returnString).getFlag())
		{
			morecompanyList = saveMoreCompanyListByReturnString(serverType, returnString);
		}
		return morecompanyList;
	}

	/**
	 * @param returnString
	 * @return
	 * @throws Exception
	 */
	private List<ERPMoreCompanies> saveMoreCompanyListByReturnString(String serverType, String returnString) throws Exception
	{
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		doc = sb.build(new StringReader(returnString));
		if (doc == null)
		{
			return null;
		}
		else
		{
			Element root = doc.getRootElement();
			Element data = root.getChild("Data");
			List<Element> recordList = data.getChildren("RecordList");
			List<ERPMoreCompanies> morecompanyList = new ArrayList<ERPMoreCompanies>();
			if (recordList != null && recordList.size() > 0)
			{
				for (Element record : recordList)
				{
					ERPMoreCompanies moreCompany = new ERPMoreCompanies();
					moreCompany.setCompanydh(record.getChildTextTrim(companyDHfield));
					moreCompany.setCompanyjc(record.getChildTextTrim(companyJCfield) + "(" + record.getChildTextTrim(companyDHfield) + ")");
					moreCompany.setERPTypeFlag(serverType);
					morecompanyList.add(moreCompany);
				}
			}
			// 将多公司资料保存到数据库中
			this.stubService.saveMoreCompany(serverType, morecompanyList);
			return morecompanyList;
		}
	}

	@Override
	protected BooleanResult exportOpeation(ERPWFOperationEnum operation, int count) throws Exception
	{
		String returnString = null;
		for (int j = 1; j <= count; j++)
		{
			this.checkTimeOut();
			this.currentXMLPackIdx = j;
			Document document = this.integrate.getCreateDataXML(operation, count, j);
			String dataString = XMLUtil.convertXML2String(document, this.isOmitDeclarationInXML(), this.isExpandEmptyTagInXML());
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
		BooleanResult operationResult = new BooleanResult(true, this.exportSuccess);
		if (operationResult.getFlag())
		{
			// 如果是传BOM，则在BOM传输最后通知WF BOM已经传输结束
			if (operation.getCategory().equals(IntegrateERP.ERP_BOM))
			{
				returnString = this.checkConnectionCallWS(XMLUtil.convertXML2StringExpandEmptyElements(this.getNotifyEle(this.factory, userId, operation, jobId)), operation);
				operationResult = this.parseResponseString(returnString);
				if (operationResult.getFlag())
				{
					operationResult = new BooleanResult(true, this.exportSuccess);
				}
			}
		}
		return operationResult;
	}

	@Override
	protected boolean containParamTagInCross()
	{
		return false;
	}

	@Override
	protected boolean isExpandEmptyTagInXML()
	{
		return true;
	}

	@Override
	public List<FoundationObject> getInfoFromERP(List<FoundationObject> list, ERPServiceConfig serviceConfig, List<String> factoryId) throws Exception
	{
		List<String> contentList = null;
		this.lang = this.stubService.getUserSignature().getLanguageEnum();
		boolean isbind = false;
		List<FoundationObject> returnList = new ArrayList<FoundationObject>();
		List<ERPWFOperationEnum> operations = null;
		this.decorateServieConfig(serviceConfig);
		ERPServerType type = ERPServerType.valueOf(serviceConfig.getERPServerSelected());
		ERPQuerySchemaParameterObject parameterObject = new ERPQuerySchemaParameterObject();
		parameterObject.setSchemaName(serviceConfig.getSchemaName());
		parameterObject.setERPType(type);
		parameterObject.setECChangeType(null);
		parameterObject.setEC(false);
		this.schema = this.stubService.getSchemaByName(parameterObject);
		this.currentXMLPackIdx = 1;
		this.jobId = String.valueOf(System.nanoTime());
		this.currentOpeIdx = 1;
		this.useCross = this.isUseCross(serviceConfig);
		if (this.checkConfigFile(type))
		{
			String message = this.stubService.getERPStub().getFileName(type) + ".xml";
			message += this.stubService.getMSRM().getMSRString("ID_APP_ERPI_CONFIG_SCHEMA_ERROR", this.lang.toString()) + ":";
			message += this.schema.getName();
			throw new ServiceRequestException(message);
		}
		contentList = schema.getContentList();
		operations = this.integrate.getOperationList(false);
		if (!SetUtils.isNullList(operations) && !SetUtils.isNullList(list) && !SetUtils.isNullList(factoryId))
		{
			if (operations.contains(ERPWFOperationEnum.GETCOST) && operations.contains(ERPWFOperationEnum.GETPRICE))
			{
				operations.remove(ERPWFOperationEnum.GETCOST);
			}
			if (operations.size() > 1)
			{
				isbind = true;
				for (int i = 0; i < operations.size(); i++)
				{
					ERPWFOperationEnum operation = operations.get(i);
					this.currentOpeIdx += i;
					doExportOperation(isbind, operation, factoryId, list, contentList);
				}
			}
			else
			{
				doExportOperation(isbind, operations.get(0), factoryId, list, contentList);
			}
		}
		Iterator<String> it = foMap.keySet().iterator();
		while (it.hasNext())
		{
			String key = it.next().toString();
			returnList.add(foMap.get(key));
		}
		this.afterExport();
		this.foMap.clear();
		return returnList;
	}

	private List<String> getcontents(List<String> contentList, ERPWFOperationEnum operation)
	{
		List<String> result = new ArrayList<String>();
		Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
		if (!SetUtils.isNullList(contentList))
		{
			for (String content : contentList)
			{
				Map<String, String> map;
				map = this.getStubService().getERPStub().getContentAttribute(ERPServerType.ERPWORKFLOW, content);

				if (paraMap.containsKey(map.get("operation")))
				{
					paraMap.get(map.get("operation")).add(content);
				}
				else
				{
					paraMap.put(map.get("operation"), new ArrayList<String>());
					paraMap.get(map.get("operation")).add(content);
				}
			}
			if (operation.equals(ERPWFOperationEnum.GETCOST) || operation.equals(ERPWFOperationEnum.GETPRICE))
			{
				if (!SetUtils.isNullList(paraMap.get("getCost")))
				{
					result.addAll(paraMap.get("getCost"));
				}
				if (!SetUtils.isNullList(paraMap.get("getPrice")))
				{
					result.addAll(paraMap.get("getPrice"));
				}
			}
			else if (operation.equals(ERPWFOperationEnum.GETQUANTITY))
			{
				result.addAll(paraMap.get("getQuantity"));
			}
		}
		return result;
	}

	private void doExportOperation(boolean isbind, ERPWFOperationEnum operation, List<String> factoryId, List<FoundationObject> list, List<String> contentList) throws Exception
	{
		List<String> operationContent = null;
		if (operation.getId().equalsIgnoreCase(ERPWFOperationEnum.GETPRICE.getId()) || operation.getId().equalsIgnoreCase(ERPWFOperationEnum.GETCOST.getId()))
		{
			operationContent = this.getcontents(contentList, operation);
			for (String factory : factoryId)
			{
				if (isbind)
				{
					for (FoundationObject fo : list)
					{
						List<FoundationObject> paramList = new ArrayList<FoundationObject>();
						paramList.add(fo);
						String returnStringList = this.integrate.getReturnList(paramList, factory, operation);
						getPriceRreturn(isbind, returnStringList, paramList, factory, operation, operationContent);
					}
				}
				else
				{
					String returnStringList = this.integrate.getReturnList(list, factory, operation);
					getPriceRreturn(isbind, returnStringList, list, factory, operation, contentList);
				}
			}
		}
		else if (operation.getId().equalsIgnoreCase(ERPWFOperationEnum.GETQUANTITY.getId()))
		{
			if (isbind)
			{
				operationContent = this.getcontents(contentList, operation);
			}
			else
			{
				operationContent = contentList;
			}
			for (String factory : factoryId)
			{
				for (FoundationObject fo : list)
				{
					List<FoundationObject> paramList = new ArrayList<FoundationObject>();
					paramList.add(fo);
					String returnStringList = this.integrate.getReturnList(paramList, factory, operation);
					getQuantityReturn(returnStringList, fo, factory, operation, operationContent);
				}
			}
		}
	}

	/**
	 * 获取数量返回并缓存到foMap
	 * 
	 * @param returnStringList
	 * 
	 * @param fo
	 * @param factory
	 * @param operation
	 * @param contentList
	 * @throws Exception
	 * @throws IOException
	 * @throws JDOMException
	 */
	private void getQuantityReturn(String returnStringList, FoundationObject fo, String factory, ERPWFOperationEnum operation, List<String> contentList) throws JDOMException,
			IOException, Exception
	{
		BooleanResult result = null;
		List<Element> resultElementList = null;
		String key = factory + fo.getId();
		if (foMap.get(key) == null)
		{
			foMap.put(key, (FoundationObject) fo.clone());
		}
		else
		{
			fo = foMap.get(key);
		}
		result = this.parseResponseString(returnStringList);
		fo.put("PlantCode", factory);
		if (!result.getFlag())
		{
			if (result.getDetail().contains(this.getStubService().getMSRM().getMSRString("ID_APP_ERPI_NOITEM_PINHAO", LanguageEnum.ZH_TW.toString())))
			{
				fo.put("Flag", "N");
				foMap.put(key, fo);
				return;
			}
			else
			{
				throw new ServiceRequestException(result.getDetail());
			}
		}
		else
		{
			fo.put("Flag", "Y");
		}
		resultElementList = this.getDataList(result.getDetail());

		if (!SetUtils.isNullList(resultElementList))
		{
			for (Element e : resultElementList)
			{
				if (fo != null)
				{
					for (String content : contentList)
					{
						fo.put(content, e.getChildText(content));
					}
				}
			}
			foMap.put(key, fo);
		}
	}

	/**
	 * 获取价格返回并缓存到foMap(与取数量混用时仅传单物料)
	 * 
	 * @param isbind
	 * 
	 * @param returnStringList
	 * 
	 * @param list
	 * @param factoryId
	 * @param operation
	 * @param contentList
	 * @throws Exception
	 */
	private void getPriceRreturn(boolean isbind, String returnStringList, List<FoundationObject> list, String factory, ERPWFOperationEnum operation, List<String> contentList)
			throws Exception
	{
		BooleanResult result = null;
		List<Element> resultElementList = null;
		result = this.parseResponseString(returnStringList);
		if (!isbind)
		{
			if (!result.getFlag())
			{
				if (list.size() == 1 && result.getDetail().contains(this.getStubService().getMSRM().getMSRString("ID_APP_ERPI_NOITEM_PINHAO", LanguageEnum.ZH_TW.toString())))
				{
					FoundationObject fo = list.get(0);
					String key = factory + fo.getId();
					if (foMap.get(key) == null)
					{
						foMap.put(key, (FoundationObject) fo.clone());
					}
					fo = foMap.get(key);
					fo.put("PlantCode", factory);
					fo.put("Flag", "N");
					foMap.put(key, fo);
					return;
				}
				throw new ServiceRequestException(result.getDetail());
			}
			resultElementList = this.getDataList(result.getDetail());
			for (FoundationObject object : list)
			{
				if (foMap.get(factory + object.getId()) != null)
				{
					continue;
				}
				foMap.put(factory + object.getId(), (FoundationObject) object.clone());
			}
			if (!SetUtils.isNullList(resultElementList))
			{
				for (Element e : resultElementList)
				{
					String key = factory + e.getChildText("MB001");
					FoundationObject object = foMap.get(key);
					if (object != null)
					{
						object.put("Flag", "Y");
						object.put("PlantCode", factory);
						for (String content : contentList)
						{
							object.put(content, e.getChildText(content));
						}
					}
					foMap.put(key, object);
				}
			}
		}
		else
		{
			FoundationObject fo = list.get(0);
			String key = factory + fo.getId();
			if (foMap.get(key) == null)
			{
				foMap.put(key, (FoundationObject) fo.clone());
			}
			fo = foMap.get(key);
			fo.put("PlantCode", factory);
			if (!result.getFlag())
			{
				if (result.getDetail().contains(this.getStubService().getMSRM().getMSRString("ID_APP_ERPI_NOITEM_PINHAO", LanguageEnum.ZH_TW.toString())))
				{
					fo.put("Flag", "N");
					foMap.put(key, fo);
					return;
				}
				else
				{
					throw new ServiceRequestException(result.getDetail());
				}
			}
			if (foMap.get(key) == null)
			{
				foMap.put(key, (FoundationObject) fo.clone());
			}
			resultElementList = this.getDataList(result.getDetail());
			if (!SetUtils.isNullList(resultElementList))
			{
				for (Element e : resultElementList)
				{
					FoundationObject object = foMap.get(key);
					if (object != null)
					{
						object.put("Flag", "Y");
						object.put("PlantCode", factory);
						for (String content : contentList)
						{
							object.put(content, e.getChildText(content));
						}
					}
					foMap.put(key, object);
				}
			}

		}
	}

	private List<Element> getDataList(String responseXML) throws JDOMException, IOException
	{
		List<Element> returnList = null;
		Element root = XMLUtil.convertString2XML(responseXML).getRootElement();
		returnList = root.getChild("Data").getChildren("RecordList");
		return returnList;
	}
}
