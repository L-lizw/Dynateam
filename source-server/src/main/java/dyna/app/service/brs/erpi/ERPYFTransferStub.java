/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPStub
 * WangLHB Nov 17, 2011
 */
package dyna.app.service.brs.erpi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dataExport.IntegrateYF;
import dyna.app.service.brs.erpi.yfIntegrateService.IYiFeiGatewayEx;
import dyna.app.service.brs.erpi.yfIntegrateService.IYiFeiGatewayExbindingStub;
import dyna.app.service.brs.erpi.yfIntegrateService.IYiFeiGatewayExserviceLocator;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.erp.ERPMoreCompanies;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPYFOperationEnum;
import dyna.common.util.SetUtils;

/**
 * @author WangLHB
 * @author chega
 */
public class ERPYFTransferStub extends ERPTransferStub<ERPYFOperationEnum>
{
	private String	writebackFileName;

	protected ERPYFTransferStub(ServiceContext context, ERPIImpl service, Document document) throws Exception
	{
		super(context, service);
		this.ERPType = ERPServerType.ERPYF;
		this.integrate = new IntegrateYF(this, document);
		this.operationPollingTime = this.integrate.getOperationPollingTime();
		this.operationLiveTime = this.integrate.getOperationLiveTime();
	}

	/**
	 * 如果YF成功接收则返回一个唯一值，待YF内部处理完毕，会生成一个以这个唯一值为名的文件，这个文件里包含最终的处理状态信息
	 * 
	 * @param msg
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	@Override
	protected BooleanResult parseResponseString(String msg) throws JDOMException, IOException
	{
		return this.parseResponseString(XMLUtil.convertString2XML(msg));
	}

	private BooleanResult checkConnectionParseResponseString(String msg) throws JDOMException, IOException
	{
		return this.checkConnectionParseResponseString(XMLUtil.convertString2XML(msg));
	}
	
	private BooleanResult parseResponseString(Document document) throws JDOMException, IOException
	{
		Element root = document.getRootElement();
		Element serviceEle = root.getChild("Service");
		String status = serviceEle.getChildText("Status");
		if ("0".equals(status))
		{
			this.writebackFileName = serviceEle.getChildText("JobID");
			if (this.writebackFileName == null)
			{
				throw new IllegalArgumentException("the write back file name is null in YF response xml");
			}
			return new BooleanResult(true, this.writebackFileName);
		}
		else
		{
			BooleanResult result = new BooleanResult(false);
			StringBuilder sb = new StringBuilder();
			if (SetUtils.isNullList(serviceEle.getChild("Error").getChildren("Row")))
			{
				result.setDetail(serviceEle.getChild("Error").getText());
			}
			else
			{
				Iterator<Element> rowIt = serviceEle.getChild("Error").getChildren("Row").iterator();
				Element rowEle = null;
				while (rowIt.hasNext())
				{
					rowEle = rowIt.next();
					sb.append("Table: " + rowEle.getAttributeValue("Table") + ", Field: " + rowEle.getAttributeValue("Field") + ", Data: " + rowEle.getAttributeValue("Data")
							+ ", Desc: " + rowEle.getAttributeValue("Desc") + "\r\n");
				}
				result.setDetail(sb.toString());
			}
			return result;
		}
	}

	private BooleanResult checkConnectionParseResponseString(Document document) throws JDOMException, IOException
	{
		Element root = document.getRootElement();
		Element serviceEle = root.getChild("Service");
		String status = serviceEle.getChildText("Status");
		if ("0".equals(status))
		{
			return new BooleanResult(true, this.writebackFileName);
		}
		else
		{
			BooleanResult result = new BooleanResult(false);
			StringBuilder sb = new StringBuilder();
			if (SetUtils.isNullList(serviceEle.getChild("Error").getChildren("Row")))
			{
				result.setDetail(serviceEle.getChild("Error").getText());
			}
			else
			{
				Iterator<Element> rowIt = serviceEle.getChild("Error").getChildren("Row").iterator();
				Element rowEle = null;
				while (rowIt.hasNext())
				{
					rowEle = rowIt.next();
					sb.append("Table: " + rowEle.getAttributeValue("Table") + ", Field: " + rowEle.getAttributeValue("Field") + ", Data: " + rowEle.getAttributeValue("Data")
							+ ", Desc: " + rowEle.getAttributeValue("Desc") + "\r\n");
				}
				result.setDetail(sb.toString());
			}
			return result;
		}
	}
	
	@Override
	public BooleanResult checkConnection(ERPServiceConfig serverDef) throws Exception
	{
		this.userId = this.stubService.getUserSignature().getUserId();
		this.decorateServieConfig(serverDef);
		this.ERPConfig = serverDef;
		this.useCross = this.isUseCross(this.ERPConfig);
		Element rootElement = new Element("STD_IN");
		rootElement.setAttribute("Origin", "PDM");
		Element serviceElement = new Element("Service");
		serviceElement.setAttribute("Name", "CheckConnection");
		rootElement.addContent(serviceElement);
		// String returnString = this.getRemoteStub().yiFeiGatewayEx();
		String returnString = this.checkConnectionCallWS(XMLUtil.convertXML2String(rootElement), ERPYFOperationEnum.CheckConnection);
		return this.checkConnectionParseResponseString(returnString);
	}

	/**
	 * YF中删除“数据”XML文件
	 * 
	 * @param fileName
	 */
	private void delTempFile(String fileName)
	{
		if (((IntegrateYF) this.integrate).shouldDeleteTempFile())
		{
			File file = new File(((IntegrateYF) this.integrate).getAbsoluteFilePath(fileName));
			if (file.exists())
			{
				file.delete();
			}
		}
	}

	@Override
	public boolean containParamTagInCross()
	{
		return false;
	}

	@Override
	protected String doCallWS(String originalXML, ERPYFOperationEnum operation) throws Exception
	{
		this.saveTempFile(this.getXMLPackNo()+"_request.xml", originalXML);
		String returnString = this.getRemoteStub().yiFeiGatewayEx(originalXML);
		this.saveTempFile(this.getXMLPackNo()+"_response.xml", returnString);
		return returnString;
	}

	@Override
	protected String checkConnectionDoCallWS(String originalXML, ERPYFOperationEnum operation) throws Exception
	{
		this.saveTempFile(System.nanoTime()+"_request.xml", originalXML);
		String returnString = this.getRemoteStub().yiFeiGatewayEx(originalXML);
		this.saveTempFile(System.nanoTime()+"_response.xml", returnString);
		return returnString;
	}
	
	@Override
	protected IYiFeiGatewayEx getRemoteStub() throws MalformedURLException, ServiceException
	{
		if (this.stub == null)
		{
			this.stub = (IYiFeiGatewayExbindingStub) new IYiFeiGatewayExserviceLocator().getIYiFeiGatewayExPort(new URL(this.ERPConfig.getErpServerAddress()));
			((IYiFeiGatewayExbindingStub) stub).setTimeout(60 * 1000);
		}
		return (IYiFeiGatewayEx) this.stub;
	}

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

	@Override
	protected BooleanResult prepare4NextOperation(ERPYFOperationEnum operation) throws Exception
	{
		BooleanResult loopResult = this.loopYFProcess(operation);
		// 删除YF产生的文件
		this.delTempFile(writebackFileName);
		// 删除PLM产生的文件
		this.delTempFile(this.getXMLPackNo());
		return loopResult;
	}

	/**
	 * YF接收到抛转请求，处理完毕会在sendFilePath下新建一个<b>writebackFileName</b>文件，
	 * 通过读取这个文件中的内容可以判断抛转是否成功
	 * 
	 * @return
	 */
	private BooleanResult loopYFProcess(ERPYFOperationEnum operation) throws Exception
	{
		while (true)
		{
			this.checkTimeOut();
			this.sleep(this.operationPollingTime);
			File file = new File(((IntegrateYF) this.integrate).getAbsoluteFilePath(writebackFileName));
			if (!file.exists())
			{
				continue;
			}
			// 如果YF处理成功则继续后面的Operation
			BooleanResult result = this.checkConnectionParseResponseString(XMLUtil.openFile(file));
			if (result.getFlag())
			{
				break;
			}
			else
			{
				return result;
			}
		}
		return BooleanResult.getNull();
	}

	@Override
	protected boolean isOmitDeclarationInXML()
	{
		return false;
	}

	@Override
	protected boolean isExpandEmptyTagInXML()
	{
		return true;
	}
}
