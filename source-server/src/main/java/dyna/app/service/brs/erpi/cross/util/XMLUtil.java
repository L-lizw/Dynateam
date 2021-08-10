/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: XMLUtil
 * WangLHB Oct 11, 2011
 */
package dyna.app.service.brs.erpi.cross.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.log.DynaLogger;

/**
 * @author WangLHB
 * 
 */
public class XMLUtil
{
	public static String getCrossServiceEndPoint()
	{
		String endPoint = null;

		CrossServiceConfig crossServiceConfig = CrossConfigureManager.getInstance().getCrossServiceConfig();

		if (crossServiceConfig != null)
		{
			endPoint = "http://" + crossServiceConfig.getCrossServerIP() + ":" + crossServiceConfig.getCrossServerPort() + "/" + crossServiceConfig.getCrossServerName();
		}

		return endPoint;
	}

	public static Document openFile(String fileName) throws JDOMException, IOException
	{
		return openFile(new File(fileName));
	}

	public static Document openFile(File file) throws JDOMException, IOException
	{
		return new SAXBuilder().build(file);
	}

	/**
	 * convert xml to string
	 * 
	 * @param document
	 * @param omitDeclaration
	 * @param expandEmptyTag
	 * @return
	 * @throws IOException
	 */
	public static String convertXML2String(Document doc, boolean omitDeclaration, boolean expandEmptyTag, String charset) throws IOException
	{
		Format format = Format.getPrettyFormat();
		format.setOmitDeclaration(omitDeclaration);// 忽略?xml指令
		format.setOmitEncoding(omitDeclaration);// 忽略编码
		format.setExpandEmptyElements(expandEmptyTag);// 展开空标签
		format.setEncoding(charset);

		XMLOutputter output = new XMLOutputter();
		output.setFormat(format);
		StringWriter sw = new StringWriter();
		output.output(doc, sw);
		return sw.toString();
	}

	/**
	 * jdom在生成文件的时候，可能会造成文件乱码，这个情况和是否设置Format编码无关，而是JDK的原因，总结如下：
	 * 1.如果XMLOutputter向控制台输出，则一定要通过Writer流，不能通过Stream流<br/>
	 * 2.如果XMLOutputter向文件输出，则一定要用Stream流，而不能用Writer流<br/>
	 * @param doc
	 * @param file
	 * @throws IOException
	 */
	public static void saveXML(Document doc, File file) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(file);
			XMLOutputter output = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			format.setExpandEmptyElements(false);
			format.setOmitDeclaration(false);
			format.setOmitEncoding(false);
			output.setFormat(format);
			output.output(doc, fos);
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
					DynaLogger.error("dyna.app.service.brs.erpi.cross.util.XMLUtil#saveXML error: " + e.getMessage());
				}
			}
		}
	}

	public static String convertXML2String(Document doc) throws IOException
	{
		return XMLUtil.convertXML2String(doc, false, false);
	}

	public static String convertXML2String(Document doc, boolean omitDeclaration, boolean expandEmptyTag) throws IOException
	{
		return XMLUtil.convertXML2String(doc, omitDeclaration, expandEmptyTag, "UTF-8");
	}

	public static String convertXML2String(Element ele) throws IOException
	{
		Format format = Format.getPrettyFormat();
		format.setExpandEmptyElements(false);
		format.setEncoding("UTF-8");

		XMLOutputter output = new XMLOutputter();
		output.setFormat(format);
		StringWriter sw = new StringWriter();
		output.output(ele, sw);
		return sw.toString();
	}

	public static String convertXML2StringExpandEmptyElements(Element ele) throws IOException
	{
		Format format = Format.getPrettyFormat();
		format.setExpandEmptyElements(true);
		format.setEncoding("UTF-8");

		XMLOutputter output = new XMLOutputter();
		output.setFormat(format);
		StringWriter sw = new StringWriter();
		output.output(ele, sw);
		return sw.toString();
	}
	
	public static String convertXML2String(DocType docType) throws IOException
	{
		Format format = Format.getPrettyFormat();
		format.setOmitDeclaration(false);
		format.setOmitEncoding(false);
		format.setExpandEmptyElements(true);
		format.setEncoding("UTF-8");

		XMLOutputter output = new XMLOutputter();
		output.setFormat(format);
		StringWriter sw = new StringWriter();
		output.output(docType, sw);
		return sw.toString();
	}

	/**
	 * build string to document
	 * 
	 * @param str
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document convertString2XML(String str) throws JDOMException, IOException
	{
		Document document = null;
		try{
			document = new SAXBuilder().build(new StringReader(str));
		}catch(JDOMException e){
			DynaLogger.error(str);
			throw e;
		}
		return document;
	}
}
