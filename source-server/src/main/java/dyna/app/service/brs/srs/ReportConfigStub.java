/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReportConfigStub
 * cuilei 2012-9-7
 */
package dyna.app.service.brs.srs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.exception.ServiceRequestException;

/**
 * @author cuilei
 *
 */
public class ReportConfigStub extends AbstractServiceStub<SRSImpl>
{
	/**
	 * @param context
	 * @param service
	 * @throws ServiceRequestException
	 */
	protected ReportConfigStub(ServiceContext context, SRSImpl service)
	{
		super(context, service);
	}

	List<Map<String, String>> getConfigList(String reportConfigFile, String type)
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		File f = new File(reportConfigFile);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			NodeList nl = doc.getElementsByTagName("report");
			for (int i = 0; i < nl.getLength(); i++)
			{
				Map<String, String> map = new HashMap<String, String>();
				if (doc.getElementsByTagName("reporttype").item(i).getFirstChild().getNodeValue().equals(type))
				{
					map.put("reportpath", getNodeVal(doc, i, "reportpath"));
					map.put("reporttemplatename", getNodeVal(doc, i, "reporttemplatename"));
					map.put("reportmastername", getNodeVal(doc, i, "reportmastername"));
					map.put("reportdetailname", getNodeVal(doc, i, "reportdetailname"));
					map.put("reportlevel", getNodeVal(doc, i, "reportlevel"));
					map.put("reportfiletype", getNodeVal(doc, i, "reportfiletype"));
					map.put("reportmorelevel", getNodeVal(doc, i, "reportmorelevel"));
					map.put("summaryfiledname", getNodeVal(doc, i, "summaryfiledname"));
					map.put("personaljavaname", getNodeVal(doc, i, "personaljavaname"));
					list.add(map);
				}

			}
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	private String getNodeVal(Document doc, int entryIndex, String propertyName)
	{
		return null == doc.getElementsByTagName(propertyName).item(entryIndex).getFirstChild() ? "" : doc
				.getElementsByTagName(propertyName).item(entryIndex).getFirstChild().getNodeValue();
	}
}
