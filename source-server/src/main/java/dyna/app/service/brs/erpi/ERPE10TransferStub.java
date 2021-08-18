/**
 * chega
 * 2013-1-6下午3:44:20
 */
package dyna.app.service.brs.erpi;

import dyna.app.service.brs.erpi.cross.util.CrossConfigureManager;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dataExport.IntegrateE10;
import dyna.app.service.brs.erpi.e10IntegrateService.CrossGeneralServiceLocator;
import dyna.app.service.brs.erpi.e10IntegrateService.ICrossGeneralService;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPE10OperationEnum;
import dyna.common.systemenum.ERPServerType;
import dyna.common.util.Base64Util;
import dyna.common.util.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.springframework.stereotype.Component;

import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ERPE10TransferStub extends ERPTransferStub<ERPE10OperationEnum>
{
	/**
	 * @param context
	 * @param service
	 * @throws Exception
	 */
	public ERPE10TransferStub(Document document) throws Exception
	{
		this.ERPType = ERPServerType.ERPE10;
		this.integrate = new IntegrateE10(this, document);
		operationPollingTime = this.integrate.getOperationPollingTime();
		operationLiveTime = this.integrate.getOperationLiveTime();
	}

	/**
	 * <b>returnString</b>是经过Base64编码的数据，因此这里要解码
	 * 
	 * @param returnString
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Override
	public BooleanResult parseResponseString(String returnString) throws JDOMException, IOException
	{
		Document document = XMLUtil.convertString2XML(returnString);
		Element root = document.getRootElement();
		String code = root.getChildTextTrim("Code");
		// Message标签下是业务逻辑错误
		String msg = root.getChildText("Message");
		// StackInfo是代码调用产生的错误
		String stackInfo = root.getChildText("StackInfo");
		String tokenId = root.getChildText("TokenId");
		// 将TokenId保存到conf/e10conf.xml中
		if (!StringUtils.isNullString(tokenId))
		{
			DynaLogger.debug(tokenId);
			if (this.integrate != null)
			{
				((IntegrateE10) this.integrate).updateTokenId(tokenId);
			}
		}
		if ("400".equals(code))
		{// E10用400表示抛转成功，其它的表示抛转失败
			String result = Base64Util.decodeBase64(root.getChildText("Result"));
			DynaLogger.debug(result);
			return new BooleanResult(true, result);
		}
		else
		{
			return new BooleanResult(false, StringUtils.isNullString(stackInfo) ? msg
					: (msg + ERPTransferStub.LINE_SEPERATOR + "[E10 StackInfo]" + ERPTransferStub.LINE_SEPERATOR + stackInfo));
		}
	}

	@Override
	protected String doCallWS(String originXML, ERPE10OperationEnum operation) throws Exception
	{
		CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
		String wrappedXML = this.wrapWithCross(crossServiceConfig, originXML, operation);
		this.saveTempFile(this.getXMLPackNo() + "_request.xml", wrappedXML);
		String returnString = this.unwrapWithCross(((ICrossGeneralService) this.getRemoteStub()).invokeSrv(wrappedXML), false);
		this.saveTempFile(this.getXMLPackNo() + "_response.xml", returnString);
		return returnString;
	}

	@Override
	protected String checkConnectionDoCallWS(String originXML, ERPE10OperationEnum operation) throws Exception
	{
		CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
		String wrappedXML = this.wrapWithCross(crossServiceConfig, originXML, operation);
		this.saveTempFile(System.nanoTime() + "_request.xml", wrappedXML);
		String returnString = this.unwrapWithCross(((ICrossGeneralService) this.getRemoteStub()).invokeSrv(wrappedXML), false);
		this.saveTempFile(System.nanoTime() + "_response.xml", returnString);
		return returnString;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public BooleanResult checkConnection(ERPServiceConfig serviceConfig) throws Exception
	{
		return super.checkConnection(serviceConfig);
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public List<ERPMoreCompanies> getMoreCompanyThroughWS(ERPServiceConfig serverDef) throws Exception
	{
		this.userId = this.stubService.getUserSignature().getUserId();
		if (lang == null)
		{
			lang = this.stubService.getUserSignature().getLanguageEnum();
		}
		this.useCross = this.isUseCross(serverDef);
		List<String> factoryList = Arrays.asList(this.integrate.getParameter(null).getParamMap().get("factory").split(","));
		List<ERPMoreCompanies> companyList = new ArrayList<ERPMoreCompanies>();
		ERPMoreCompanies company = null;
		for (int i = 0; i < factoryList.size(); i++)
		{
			company = new ERPMoreCompanies();
			company.setCompanydh(factoryList.get(i));
			company.setCompanyjc(factoryList.get(i));
			company.setERPTypeFlag(serverDef.getERPServerSelected());
			companyList.add(company);
		}
		this.stubService.saveMoreCompany(serverDef.getERPServerSelected(), companyList);
		return companyList;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	protected Remote getRemoteStub() throws MalformedURLException, ServiceException
	{
		if (this.stub == null)
		{
			this.stub = new CrossGeneralServiceLocator().getBasicHttpBinding_ICrossGeneralService(new URL(this.ERPConfig.getErpServerAddress()));
		}
		return this.stub;
	}

	@Override
	protected BooleanResult exportOpeation(ERPE10OperationEnum operation, int count) throws Exception
	{
		String returnString = null;
		Document document = null;
		for (int j = 1; j <= count; j++)
		{
			this.checkTimeOut();
			this.currentXMLPackIdx = j;
			if (operation.getId().equalsIgnoreCase("createCodeitem") || operation.getId().equalsIgnoreCase("createFieldG"))
			{
				document = this.integrate.getCreateCFDataXML(operation, count, j);
			}
			else
			{
				document = this.integrate.getCreateDataXML(operation, count, j);
			}
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
		return new BooleanResult(true, this.exportSuccess);

	}
}
