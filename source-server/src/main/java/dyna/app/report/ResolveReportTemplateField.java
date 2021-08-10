/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ResolveTemplateReportField
 * cuilei 2012-6-5
 */
package dyna.app.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * @author cuilei
 *
 */
public class ResolveReportTemplateField
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<DetailColumnInfo> getReportTemplateField(File reportTemplateFilePath)
	{
		List<DetailColumnInfo> detailColumnInfos = new ArrayList<DetailColumnInfo>();

		try
		{
			JasperDesign jasperDesign = JRXmlLoader.load(reportTemplateFilePath);
			List<JRField> fields = jasperDesign.getFieldsList();

			for (JRField field : fields)
			{
				DetailColumnInfo detailColumnInfo = new DetailColumnInfo("", field.getValueClass(), field.getName().replace("#", "$"));
				detailColumnInfos.add(detailColumnInfo);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return detailColumnInfos;
	}

	public List<ParameterColumnInfo> getReportTemplateParameters(File reportTemplateFilePath)
	{
		List<ParameterColumnInfo> parameterList = new ArrayList<ParameterColumnInfo>();
		try
		{
			JasperDesign jasperDesign = JRXmlLoader.load(reportTemplateFilePath);
			List<JRParameter> parameters = jasperDesign.getParametersList();
			for (JRParameter parameter : parameters)
			{
				if (!parameter.isSystemDefined())
				{
					parameterList.add(new ParameterColumnInfo(parameter.getName(), parameter.getValueClass(), parameter.getDescription(), parameter.getPropertiesMap()));
				}
			}
		}
		catch (JRException e)
		{
			e.printStackTrace();
		}
		return parameterList;
	}
}
