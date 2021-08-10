/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CrossStub
 * WangLHB Nov 17, 2011
 */
package dyna.app.service.brs.erpi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.erpi.cross.util.CrossConfigureManager;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.common.dto.erp.CrossIntegrate;
import dyna.common.dto.erp.CrossParamList;
import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.EnvUtils;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
public class CrossStub extends AbstractServiceStub<ERPIImpl>
{

	/**
	 * PLM产品注册到cross时用的名字
	 */
	public static final String	PLM_NAME		= "PLM";
	/**
	 * 传xml时取md5时加的盐值
	 */
	public static final String	CROSS_SALT		= "28682266";
	public static final String	WSMethodName	= "exportDataToErp";
	public static final String	CROSS_FILE_PATH	= "conf" + File.separator + "crossconf.xml";

	/**
	 * @param context
	 * @param service
	 */
	protected CrossStub(ServiceContext context, ERPIImpl service)
	{
		super(context, service);
	}

	protected String getProdRegInfo(String ip, String uid) throws ServiceRequestException
	{
		CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
		String uid_ = uid;
		if (!StringUtils.isNullString(crossServiceConfig.getUid()))
		{
			uid_ = crossServiceConfig.getUid();
		}

		StringBuffer xml = new StringBuffer();

		xml.append("<request action=\"reg\"> ");

		xml.append("<host prod=\"" + CrossServiceConfig.PRODUCT_NAME + "\" ver=\"" + crossServiceConfig.getHostVer() + "\" ip=\"" + ip + "\" id=\""
				+ crossServiceConfig.getHostID() + "\" /> ");
		xml.append("<payload> ");
		xml.append("<param key=\"wsdl\" type=\"string\">");
		xml.append(" http://" + ip + ":" + crossServiceConfig.getHostPort() + "/" + crossServiceConfig.getHostServerName() + "?wsdl");

		xml.append("</param>");
		xml.append("<param key=\"retrytimes\" type=\"integer\">3</param> ");
		xml.append("<param key=\"retryinterval\" type=\"integer\">3000</param> ");
		xml.append("<param key=\"concurrence\" type=\"integer\">50</param> ");
		xml.append("<param key=\"uid\" type=\"string\">" + uid_ + "</param> ");
		xml.append("</payload> ");
		xml.append("</request> ");
		DynaLogger.debug(xml.toString());
		return xml.toString();
	}

	protected String getSrvRegInfo(String ip, String uid) throws ServiceRequestException
	{
		CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();
		String uid_ = uid;
		if (!StringUtils.isNullString(crossServiceConfig.getUid()))
		{
			uid_ = crossServiceConfig.getUid();
		}

		StringBuffer xml = new StringBuffer();

		xml.append("<request action=\"reg\">  ");

		xml.append("<host prod=\"" + CrossServiceConfig.PRODUCT_NAME + "\" ver=\"" + crossServiceConfig.getHostVer() + "\" ip=\"" + ip + "\" id=\""
				+ crossServiceConfig.getHostID() + "\" /> ");

		xml.append("<payload> ");
		// xml.append("<param key=\"srvname\" type=\"string\">checkConnection</param> ");
		// xml.append("<param key=\"srvname\" type=\"string\">exportDataToErp</param> ");
		xml.append("<param key=\"uid\" type=\"string\">" + uid_ + "</param> ");
		xml.append("</payload>  ");
		xml.append("</request>  ");
		DynaLogger.debug(xml.toString());
		return xml.toString();
	}

	// 在cross页面点执行并同步的时候会执行这个方法
	protected String doSyncProcess(String paramXML) throws ServiceRequestException, JDOMException, IOException
	{
		DynaLogger.debug("ERPIService---doSyncProcess");
		// 更新crossconf.xml文件
		boolean updateCrossConf = updateCrossConf(paramXML);

		StringBuffer sb = new StringBuffer();
		if (updateCrossConf)
		{
			sb.append("<response>");
			sb.append("  <srvcode>000</srvcode>");
			sb.append("  <payload/>");
			sb.append("</response>");
		}
		else
		{
			sb.append("<response>");
			sb.append("  <payload></payload>");
			sb.append("  <srvcode>100</srvcode>");
			sb.append("</response>");
		}
		DynaLogger.debug(sb.toString());
		return sb.toString();
	}

	/**
	 * 委托PLM webservice将数据发给Cross
	 * 
	 * @param e1
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public Object crossExportDataToErp(String e1) throws ServiceException, RemoteException
	{
		DynaLogger.debug(e1);
		String endpoint = null;
		endpoint = "http://";
		endpoint = endpoint + CrossConfigureManager.getInstance().getCrossServiceConfig().getHostIP();
		endpoint = endpoint + ":";

		endpoint = endpoint + CrossConfigureManager.getInstance().getCrossServiceConfig().getHostPort();
		endpoint = endpoint + "/";
		endpoint = endpoint + CrossConfigureManager.getInstance().getCrossServiceConfig().getHostServerName();
		Object returnString;
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(endpoint);
		call.setOperationName(new QName("http://service.dcis", WSMethodName));

		call.addParameter("guid", org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
		call.addParameter("className", org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);

		call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
		call.setUseSOAPAction(true);
		returnString = call.invoke(new Object[] { XMLUtil.getCrossServiceEndPoint(), e1 });
		DynaLogger.debug(returnString);
		return returnString;
	}

	private Element setElementValue(Element parent, String childElementName, String childElementText)
	{
		Element child = parent.getChild(childElementName);
		if (child == null)
		{
			Element childElement = new Element(childElementName);
			parent.addContent(childElement);
			child = childElement;
		}
		if (childElementText != null)
		{
			child.setText(childElementText);
		}
		return child;
	}

	private boolean updateCrossConf(String xmlParam) throws JDOMException, IOException
	{
		CrossServiceConfig crossServiceConfig = new CrossServiceConfig();
		Document document = XMLUtil.convertString2XML(xmlParam);
		Element rootElement = document.getRootElement();

		Element payload = rootElement.getChild("payload");
		Element syncdata = payload.getChild("syncdata");
		Element cross = syncdata.getChild("cross");

		String crossWsdl = cross.getAttributeValue("wsdl");
		if (StringUtils.isNullString(crossWsdl))
		{
			DynaLogger.debug(xmlParam);
			return true;
		}
		int indexOf = crossWsdl.indexOf(":", 7);// 从第7个数开始，返回第一次出现:的位置

		if (indexOf != -1)
		{
			crossServiceConfig.setCrossServerIP(crossWsdl.substring(7, indexOf));// 返回一个新串
			crossServiceConfig.setCrossServerPort(crossWsdl.substring(indexOf + 1, indexOf + 5));
			int nameIndexOf = crossWsdl.indexOf("?");
			crossServiceConfig.setCrossServerName(crossWsdl.substring(indexOf + 6, nameIndexOf));
		}

		String resturl = cross.getAttributeValue("resturl");
		crossServiceConfig.setCrossRestUrl(resturl);

		Element prodlistElement = syncdata.getChild("prodlist");
		@SuppressWarnings("unchecked")
		List<Element> prodList = prodlistElement.getChildren("prod");
		if (prodList != null)
		{
			List<CrossIntegrate> serversList = new ArrayList<CrossIntegrate>();
			for (Element element : prodList)
			{
				CrossIntegrate crossServer = new CrossIntegrate();
				// DynaTeam产品,这个名字需要改成“DigiWin PLM”
				if (PLM_NAME.equals(element.getAttributeValue("name")))
				{
					crossServiceConfig.setHostIP(element.getAttributeValue("ip"));
					crossServiceConfig.setHostID(element.getAttributeValue("id"));
					crossServiceConfig.setHostVer(element.getAttributeValue("ver"));
					crossServiceConfig.setUID(element.getAttributeValue("uid"));
					crossServiceConfig.setRestUrl(element.getAttributeValue("resturl"));
					// 解析wsdl地址
					String hostWsdl = element.getAttributeValue("wsdl");
					int hostindexOf = hostWsdl.indexOf(":", 7);// 从第7个数开始，返回第一次出现:的位置

					if (hostindexOf != -1)
					{
						crossServiceConfig.setHostPort(hostWsdl.substring(hostindexOf + 1, hostindexOf + 6));
						int nameIndexOf = hostWsdl.indexOf("?");
						crossServiceConfig.setHostServerName(hostWsdl.substring(hostindexOf + 7, nameIndexOf));
					}
				}
				// 其他集成产品
				else
				{
					crossServer.setServiceIP(element.getAttributeValue("ip"));
					crossServer.setServiceProd(element.getAttributeValue("name"));
					crossServer.setServiceID(element.getAttributeValue("id"));
					crossServer.setServiceVer(element.getAttributeValue("ver"));
					crossServer.setUID(element.getAttributeValue("uid"));
					crossServer.setRestUrl(element.getAttributeValue("resturl"));
				}
				if (crossServer != null && crossServer.size() != 0)
				{
					serversList.add(crossServer);
				}
			}
			crossServiceConfig.setServices(serversList);
		}

		Element paramlistElement = syncdata.getChild("paramlist");
		@SuppressWarnings("unchecked")
		List<Element> paramList = paramlistElement.getChildren("param");
		if (paramList != null)
		{
			List<CrossParamList> crossParamList = new ArrayList<CrossParamList>();
			for (Element element : paramList)
			{
				CrossParamList params = new CrossParamList();
				params.setParamName(element.getAttributeValue("name"));
				params.setParamValue(element.getAttributeValue("value"));
				crossParamList.add(params);
			}
			crossServiceConfig.setParamList(crossParamList);
		}

		document = null;
		rootElement = null;
		File file = new File(EnvUtils.getConfRootPath() + CROSS_FILE_PATH);
		document = XMLUtil.openFile(file);
		rootElement = document.getRootElement();
		Element oldServers = rootElement.getChild("servers");
		oldServers.removeContent();

		Element crossserver = setElementValue(rootElement, "crossserver", null);
		setElementValue(crossserver, "ip", crossServiceConfig.getCrossServerIP());
		setElementValue(crossserver, "port", crossServiceConfig.getCrossServerPort());
		setElementValue(crossserver, "servername", crossServiceConfig.getCrossServerName());
		setElementValue(crossserver, "resturl", crossServiceConfig.getCrossRestUrl());

		// 需求产品
		Element host = setElementValue(rootElement, "host", null);
		setElementValue(host, "ip", crossServiceConfig.getHostIP());
		setElementValue(host, "id", crossServiceConfig.getHostID());
		setElementValue(host, "ver", crossServiceConfig.getHostVer());
		setElementValue(host, "port", crossServiceConfig.getHostPort());
		setElementValue(host, "acct", "");
		setElementValue(host, "servername", crossServiceConfig.getHostServerName());
		setElementValue(host, "uid", crossServiceConfig.getUid());
		setElementValue(host, "resturl", crossServiceConfig.getRestUrl());

		// cross中的集成产品列表
		List<CrossIntegrate> crossServersList = crossServiceConfig.getServices();
		// Element servers = setElementValue(rootElement, "servers", null);
		Element serversEle = rootElement.getChild("servers");
		serversEle.removeContent();
		if (crossServersList != null && crossServersList.size() != 0)
		{
			for (CrossIntegrate crossServer : crossServersList)
			{
				Element server = new Element("server");
				Element ip = new Element("ip");
				ip.setText(crossServer.getServiceIP());
				server.addContent(ip);

				Element id = new Element("id");
				id.setText(crossServer.getServiceID());
				server.addContent(id);

				Element ver = new Element("ver");
				ver.setText(crossServer.getServiceVer());
				server.addContent(ver);

				Element prod = new Element("prod");
				prod.setText(crossServer.getServiceProd());
				server.addContent(prod);

				Element uid = new Element("uid");
				uid.setText(crossServer.getUid());
				server.addContent(uid);

				Element resturl_ = new Element("resturl");
				resturl_.setText(crossServer.getRestUrl());
				server.addContent(resturl_);
				serversEle.addContent(server);
			}
		}
		XMLUtil.saveXML(document, file);
		return true;
	}

	protected String syncEncodingState(String xmlParam)
	{
		StringBuffer sb = new StringBuffer();
		SAXBuilder dyna = new SAXBuilder();
		File file = new File(EnvUtils.getConfRootPath() + CROSS_FILE_PATH);
		try
		{
			SAXBuilder dynaParam = new SAXBuilder();

			StringReader sr = new StringReader(xmlParam);
			Document buildRead = dynaParam.build(sr);
			Element rootElement = buildRead.getRootElement();
			Element payload = rootElement.getChild("payload");
			Element child = payload.getChild("param");
			String value = child.getValue();
			Document build = dyna.build(file);

			Element writeRootElement = build.getRootElement();
			Element host = setElementValue(writeRootElement, "host", null);
			setElementValue(host, "isencoding", Boolean.valueOf(value).toString());
			CrossConfigureManager.getInstance().getCrossServiceConfig().setHostIsEncode(Boolean.valueOf(value).toString());
			XMLOutputter output = new XMLOutputter();
			output.output(build, new FileOutputStream(file));
		}
		catch (Exception e)
		{
			sb.append("<response>");
			sb.append("  <payload></payload>");
			sb.append("  <srvcode>100</srvcode>");
			sb.append("</response>");
		}

		sb.append("<response>");
		sb.append("  <payload></payload>");
		sb.append("  <srvcode>001</srvcode>");
		sb.append("</response>");

		return sb.toString();
	}

	/**
	 * @param paramXML
	 * @return
	 */
	protected String syncInvokeParam(String plmName)
	{

		StringBuffer returnString = new StringBuffer();
		if (!StringUtils.isNullString(plmName) && plmName.equals(PLM_NAME))
		{
			returnString.append("<response>");
			returnString.append("  <payload></payload>");
			returnString.append("  <srvcode>000</srvcode>");
			returnString.append("</response>");
		}
		else
		{
			returnString.append("<response>");
			returnString.append("  <payload></payload>");
			returnString.append("  <srvcode>100</srvcode>");
			returnString.append("</response>");
		}
		return returnString.toString();
	}

	/**
	 * @param paramXML
	 * @return
	 */
	protected String getEncodingState(String paramXML)
	{

		StringBuffer returnString = new StringBuffer();
		returnString.append("<response>");
		returnString.append("  <srvcode>001</srvcode>");
		returnString.append("  <payload>");
		returnString.append(" <param key=\"isEncoding\" type=\"boolean\">");
		returnString.append(CrossConfigureManager.getInstance().getCrossServiceConfig().getHostIsEncode());
		returnString.append(" </param>");
		returnString.append(" </payload>");
		returnString.append("</response>");
		return returnString.toString();
	}
}
