/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CustomJasperReportImpl 自定义报表生成器实现
 * cuilei 2012-2-7
 */
package dyna.app.report;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.DynaObjectImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseParagraph;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * 自定义报表生成器实现
 * 
 * @author cuilei
 * 
 */
public class GenericDynaReportBuilderImpl implements GenericDynaReportBuilder
{

	// 报表设计器
	private JasperDesign			jasperDesign		= new JasperDesign();
	@SuppressWarnings("rawtypes")
	private List<DetailColumnInfo>	detailFields		= new ArrayList<DetailColumnInfo>();
	// 导出报表的格式
	private ReportTypeEnum			exportFileType		= ReportTypeEnum.PDF;
	private ReportConfiguration		reportCondition		= new ReportConfiguration();
	private File					reportTemplateFile	= new File(EnvUtils.getConfRootPath() + "conf/report/generic_report_template.jrxml");
	private Map<String, Object>		headerParameters	= new HashMap<String, Object>();

	public GenericDynaReportBuilderImpl()
	{
	}

	/**
	 * 
	 * @param reportCondition
	 *            生成报表的条件封装
	 * @throws JRException
	 */
	public GenericDynaReportBuilderImpl(ReportConfiguration reportCondition) throws JRException
	{
		this.reportCondition = reportCondition;
	}

	/**
	 * 
	 * @param provider
	 *            数据源提供者
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void generateReport(ReportDataProvider provider) throws Exception
	{
		int reportWidth = 0;
		boolean isSort = true;

		for (int i = 0; i < this.detailFields.size(); i++)
		{
			DetailColumnInfo detailColumnInfo = this.detailFields.get(i);
			reportWidth = reportWidth + detailColumnInfo.getColumnWidth();
		}
		this.jasperDesign = JRXmlLoader.load(this.reportTemplateFile);

		GenericDynaDataSource dataSource = new GenericDynaDataSource(provider, this.detailFields);
		int dataListSize = dataSource.getDataListSize();
		if (dataListSize > 1)
		{
			this.jasperDesign.setPageWidth(reportWidth + 40);
			this.jasperDesign.setColumnWidth(reportWidth);
			jasperDesign.getTitle().getElementByKey("reportTitle").setWidth(reportWidth);
			jasperDesign.getTitle().getElementByKey("reportcondition").setWidth(reportWidth);
		}
		else
		{
			isSort = false;
			if (dataListSize == 1)
			{
				if (provider.getResultClass() == StructureObject.class)
				{
					List<StructureObject> oldList = dataSource.getReportDataCache();
					StructureObject fo = oldList.get(0);
					List<StructureObject> list_new = new ArrayList<StructureObject>();
					this.reCreateData(fo, list_new);
					provider.setDataList(list_new);
				}
				else if (provider.getResultClass() == BOMStructure.class)
				{
					List<BOMStructure> oldList = dataSource.getReportDataCache();
					BOMStructure fo = oldList.get(0);
					List<BOMStructure> list_new = new ArrayList<BOMStructure>();
					this.reCreateData(fo, list_new);
					provider.setDataList(list_new);
				}
				else if (provider.getResultClass() == FoundationObject.class)
				{
					List<FoundationObject> oldList = dataSource.getReportDataCache();
					FoundationObject fo = oldList.get(0);
					List<FoundationObject> list_new = new ArrayList<FoundationObject>();
					this.reCreateData(fo, list_new);
					provider.setDataList(list_new);
				}
				else
				{
					throw new ServiceRequestException("please check data type!");
				}

				DetailColumnInfo titleColumn = new DetailColumnInfo("title", String.class, "FIELDTITLE");
				DetailColumnInfo valueColumn = new DetailColumnInfo("value", String.class, "FIELDVALUE");
				List<DetailColumnInfo> columnList = new ArrayList<DetailColumnInfo>();
				columnList.add(titleColumn);
				columnList.add(valueColumn);
				this.detailFields = columnList;
				provider.getParams().setDetailColumnList(detailFields);
			}
		}

		// jasperDesign.getTitle().getElementByKey("titleLine").setWidth(reportWidth);

		Map<String, Object> params = new HashMap<String, Object>();
		// 设置分页（自定义每页的数据条数 ) 通过分组实现

		params.put("reportTitle", this.reportCondition.getReportTitle());
		params.putAll(provider.getHeaderParameter());
		params.put("pageSize", this.reportCondition.getPageCount() + 1);

		this.setReportOtherComponents(reportWidth, isSort);
		JasperReport report = this.templateCreator(dataListSize);
		// 由于在方法setReportOtherComponents中，添加了新的字段，所以需要重新构造dataSource对象
		JasperPrint jprint = JasperFillManager.fillReport(report, params, new GenericDynaDataSource(provider, this.detailFields));

		this.setJRExporter(this.exportFileType, this.reportCondition.getExportToFilePath().getAbsolutePath(), jprint, this.reportCondition.getReportTitle());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void reCreateData(DynaObject fo, List<T> list_new)
	{
		for (int i = 0; i < this.detailFields.size(); i++)
		{
			DynaObject fo_new = new DynaObjectImpl();
			DetailColumnInfo detailColumnInfo = this.detailFields.get(i);
			fo_new.put("FIELDVALUE", fo.get(detailColumnInfo.getPropertyName()) == null ? "" : fo.get(detailColumnInfo.getPropertyName()).toString());
			fo_new.put("FIELDTITLE", detailColumnInfo.getColumnDescription() == null ? "" : detailColumnInfo.getColumnDescription().toString());
			list_new.add((T) fo_new);
		}
	}

	/**
	 * 设置导出报表的格式
	 * 
	 * @param exportFileType
	 *            导出报表格式类型
	 * @param exportToFilePath
	 *            导出路径
	 * @param jasperPrint
	 *            封装打印数据
	 * @throws JRException
	 * 
	 */
	private void setJRExporter(ReportTypeEnum exportFileType, String exportToFilePath, JasperPrint jasperPrint, String reportTitle) throws JRException
	{
		if (exportFileType.equals(ReportTypeEnum.PDF))
		{
			JasperExportManager.exportReportToPdfFile(jasperPrint, exportToFilePath);
		}
		else if (exportFileType.equals(ReportTypeEnum.EXCEL))
		{
			JRExporter exporter = new JRXlsExporter();
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportToFilePath);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

			// 导出excel时，显示正确的数据类型，如某列是数字类型，若不进行下面的设置，数字类型会显示String类型，不方便做数据统计
			exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);

			// 导出的excel报表进行格式化，去掉background的白色区域，并且去掉行列的间隙
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.SHEET_NAMES, new String[] { reportTitle == null || reportTitle.equals("") ? "report" : reportTitle });

			exporter.exportReport();
		}
		else if (exportFileType.equals(ReportTypeEnum.HTML))
		{
			JasperExportManager.exportReportToHtmlFile(jasperPrint, exportToFilePath);
		}
		else if (exportFileType.equals(ReportTypeEnum.CSV))
		{
			JRCsvExporter csv = new JRCsvExporter();
			// csv导出报表要显示设置字符编码
			csv.setParameter(JRExporterParameter.CHARACTER_ENCODING, "GBK");
			csv.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportToFilePath);
			csv.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

			csv.exportReport();
		}
		else if (exportFileType.equals(ReportTypeEnum.WORD))
		{
			JRDocxExporter docExporter = new JRDocxExporter();
			docExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, exportToFilePath);
			docExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			docExporter.exportReport();
		}
	}

	/**
	 * 返回编译好的报表文件
	 * 
	 * @return
	 * @throws JRException
	 */
	private JasperReport templateCreator(int dataListSize) throws JRException
	{
		// 根据数据的个数判断动态生成哪种报表模板，条件：多条数据时/只有一条数据时
		if (dataListSize > 1)
		{
			this.setColumnHeader();
			this.setReportField();
		}
		else
		{
			this.setSingleDataReport();
		}

		if (this.exportFileType.equals(ReportTypeEnum.EXCEL))
		{
			// 导出excel时，去除左边空白区域
			this.jasperDesign.setLeftMargin(0);
			// 导出excel时，忽略分页;
			this.jasperDesign.setIgnorePagination(true);
		}
		this.jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		return JasperCompileManager.compileReport(this.jasperDesign);
	}

	/**
	 * 设置只有一条数据的报表模板
	 * 
	 * @throws JRException
	 */
	@SuppressWarnings("unchecked")
	private void setSingleDataReport() throws JRException
	{
		this.jasperDesign.setColumnHeader(null);
		JRDesignBand detail = (JRDesignBand) this.jasperDesign.getDetailSection().getBands()[0];
		detail.setHeight(20);

		DetailColumnInfo detailColumnInfo = new DetailColumnInfo("", String.class, "");

		JRDesignField jrfilecTitle = new JRDesignField();
		jrfilecTitle.setValueClassName(String.class.getName());
		jrfilecTitle.setName("FIELDTITLE");
		this.jasperDesign.addField(jrfilecTitle);

		JRDesignField jrfilecValue = new JRDesignField();
		jrfilecValue.setValueClassName(String.class.getName());
		jrfilecValue.setName("FIELDVALUE");
		this.jasperDesign.addField(jrfilecValue);

		{
			JRDesignTextField textFieldTitle = new JRDesignTextField();
			textFieldTitle.setX(0);
			textFieldTitle.setY(0);

			textFieldTitle.setWidth(detailColumnInfo.getColumnWidth());

			// 字体居中对齐
			textFieldTitle.setHorizontalAlignment(detailColumnInfo.getHorizontalAlignment());
			textFieldTitle.setVerticalAlignment(detailColumnInfo.getVerticalAlignment());

			this.formatTextField(detailColumnInfo.getPropertiesMap().getProperty("Pattern"), textFieldTitle);
			// 当取出的数据为null时，设置数据为空
			textFieldTitle.setBlankWhenNull(true);
			textFieldTitle.setHeight(20);

			// 设置语言
			this.setTextEncoding(textFieldTitle);

			JRDesignExpression expression = new JRDesignExpression();
			expression.setText("$F{FIELDTITLE}");
			// expression.setValueClassName(detailColumnInfo.getType().getName());
			textFieldTitle.setExpression(expression);

			// 对文字过多的适当调节
			textFieldTitle.setStretchWithOverflow(true);
			textFieldTitle.setPrintWhenDetailOverflows(true);
			textFieldTitle.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
			textFieldTitle.setForecolor(detailColumnInfo.getColumnFontColor());
			textFieldTitle.setBold(true);

			// 设置报表样式，单双行颜色
			if (this.reportCondition.getExportFileType().equals(ReportTypeEnum.PDF))
			{
				JRStyle style = this.jasperDesign.getStylesMap().get(detailColumnInfo.getRowStyleName());
				style.getConditionalStyles()[0].getStyle().setBackcolor(reportCondition.getSingleRowColor());
				style.getConditionalStyles()[0].setBackcolor(reportCondition.getSingleRowColor());
				textFieldTitle.setStyle(this.jasperDesign.getStylesMap().get(detailColumnInfo.getRowStyleName()));
			}
			detail.addElement(textFieldTitle);
		}

		JRDesignTextField textField = new JRDesignTextField();
		textField.setX(detailColumnInfo.getColumnWidth());
		textField.setY(0);

		textField.setWidth(this.jasperDesign.getColumnWidth() - detailColumnInfo.getColumnWidth());

		// 字体居中对齐
		textField.setHorizontalAlignment(detailColumnInfo.getHorizontalAlignment());
		textField.setVerticalAlignment(detailColumnInfo.getVerticalAlignment());

		this.formatTextField(detailColumnInfo.getPropertiesMap().getProperty("Pattern"), textField);
		// 当取出的数据为null时，设置数据为空
		textField.setBlankWhenNull(true);
		textField.setHeight(20);

		// 设置语言
		this.setTextEncoding(textField);

		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$F{FIELDVALUE}");
		// expression.setValueClassName(detailColumnInfo.getType().getName());
		textField.setExpression(expression);

		// 对文字过多的适当调节
		textField.setStretchWithOverflow(true);
		textField.setPrintWhenDetailOverflows(true);
		textField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		textField.setForecolor(detailColumnInfo.getFontColor());

		// 设置报表样式，单双行颜色
		if (this.reportCondition.getExportFileType().equals(ReportTypeEnum.PDF))
		{
			textField.setStyle(this.jasperDesign.getStylesMap().get(detailColumnInfo.getRowStyleName()));
		}

		detail.addElement(textField);
	}

	/**
	 * 设置显示列的中文描述
	 * 
	 * @throws JRException
	 */
	@SuppressWarnings("rawtypes")
	private void setColumnHeader() throws JRException
	{
		JRDesignBand columnHeader = (JRDesignBand) this.jasperDesign.getColumnHeader();
		int columnHeight = columnHeader.getHeight();
		JRDesignBand band = new JRDesignBand();
		int reportWidth = 0;
		int columLen = 0;
		for (int i = 0; i < this.detailFields.size(); i++)
		{
			DetailColumnInfo detailColumnInfo = this.detailFields.get(i);
			JRDesignStaticText jrstaticText = new JRDesignStaticText();
			jrstaticText.setText(detailColumnInfo.getColumnDescription());

			// 需要设置语言
			this.setTextEncoding(jrstaticText);
			jrstaticText.setPdfEmbedded(true);
			jrstaticText.setHorizontalAlignment(detailColumnInfo.getHorizontalAlignment());
			jrstaticText.setVerticalAlignment(detailColumnInfo.getVerticalAlignment());

			jrstaticText.setForecolor(detailColumnInfo.getColumnFontColor());
			jrstaticText.setPrintWhenDetailOverflows(true);
			jrstaticText.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);

			// 设置列头边框
			JRDesignRectangle rectangleh = new JRDesignRectangle();
			rectangleh.setBackcolor(detailColumnInfo.getColumnBackgroudColor());
			rectangleh.setX(columLen);
			rectangleh.setY(0);
			rectangleh.setWidth(detailColumnInfo.getColumnWidth());
			rectangleh.setHeight(columnHeight);
			columnHeader.addElement(rectangleh);

			jrstaticText.setX(columLen);
			jrstaticText.setY(0);
			jrstaticText.setWidth(detailColumnInfo.getColumnWidth());

			jrstaticText.setHeight(columnHeight);
			jrstaticText.setBold(true);
			band.addElement(jrstaticText);
			columnHeader.addElement(jrstaticText);
			columLen = columLen + detailColumnInfo.getColumnWidth();
			reportWidth = reportWidth + detailColumnInfo.getColumnWidth();
		}

		this.jasperDesign.setColumnWidth(reportWidth);
	}

	/**
	 * 设置报表需要显示字段
	 * 
	 * @throws JRException
	 */
	@SuppressWarnings({ "unchecked" })
	private void setReportField() throws JRException
	{
		JRDesignBand detail = (JRDesignBand) this.jasperDesign.getDetailSection().getBands()[0];

		int columLen = 0;
		for (int i = 0; i < this.detailFields.size(); i++)
		{
			DetailColumnInfo detailColumnInfo = this.detailFields.get(i);

			// 将原来列显示单个属性改为每列可以存放多个属性，程序对其解析
			/*
			 * JRDesignField jrfilec = new JRDesignField();
			 * jrfilec.setValueClassName(detailColumnInfo.getType().getName());
			 * jrfilec.setName(detailColumnInfo.getPropertyName());
			 * this.jasperDesign.addField(jrfilec);
			 */

			String[] columns = detailColumnInfo.getPropertyName().split(",");
			boolean flag = true;
			for (String column : columns)
			{
				JRDesignField jrfilec = new JRDesignField();
				jrfilec.setValueClassName(detailColumnInfo.getType().getName());
				jrfilec.setName(column.replace("$", "#"));
				List<JRField> fields = jasperDesign.getFieldsList();

				for (JRField jr : fields)
				{
					if (jr.getName().equals(column))
					{
						flag = false;
						break;
					}
				}
				if (flag)
				{
					this.jasperDesign.addField(jrfilec);
				}
				else
				{
					break;
				}
			}

			if (!flag)
			{
				break;
			}

			// 生成表内容
			String field = "";// "$F{" + detailColumnInfo.getPropertyName() +
								// "}";

			// String[] columns = detailColumnInfo.getPropertyName().split(",");
			for (String column : columns)
			{
				field = field + "$F{" + column.replace("$", "#") + "}+';'+";
			}
			field = field.substring(0, field.length() - 5);

			JRDesignTextField textField = new JRDesignTextField();
			textField.setX(columLen);
			textField.setY(0);
			textField.setWidth(detailColumnInfo.getColumnWidth());
			// 字体居中对齐
			textField.setHorizontalAlignment(detailColumnInfo.getHorizontalAlignment());
			textField.setVerticalAlignment(detailColumnInfo.getVerticalAlignment());

			String pattern = detailColumnInfo.getPropertiesMap().getProperty("Pattern");
			this.formatTextField(pattern, textField);
			// 当取出的数据为null时，设置数据为空
			textField.setBlankWhenNull(true);
			textField.setHeight(20);

			// 设置语言
			this.setTextEncoding(textField);

			JRDesignExpression expression = new JRDesignExpression();
			// expression.setValueClassName(detailColumnInfo.getType().getName());
			expression.setText(field);
			textField.setExpression(expression);

			// 对文字过多的适当调节
			textField.setStretchWithOverflow(true);
			textField.setPrintWhenDetailOverflows(true);
			textField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
			textField.setForecolor(detailColumnInfo.getFontColor());
			// 设置单双行的颜色
			JRStyle style = this.jasperDesign.getStylesMap().get(detailColumnInfo.getRowStyleName());
			style.getConditionalStyles()[0].getStyle().setBackcolor(reportCondition.getSingleRowColor());
			style.getConditionalStyles()[0].setBackcolor(reportCondition.getDoubleRowColor());
			textField.setStyle(this.jasperDesign.getStylesMap().get(detailColumnInfo.getRowStyleName()));

			detail.addElement(textField);
			columLen = columLen + detailColumnInfo.getColumnWidth();
		}
	}

	/**
	 * 设置报表的其他控件，如分组、数据排序等
	 * 
	 * @throws JRException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setReportOtherComponents(int reportWidth, boolean isSort) throws JRException
	{
		Object object = this.headerParameters.get("exportType");
		if (null != object && object.toString().equals("bomtree"))
		{
			JRElement[] elements = this.jasperDesign.getDetailSection().getBands()[0].getElements();

			for (JRElement element : elements)
			{
				JRDesignTextField filed = (JRDesignTextField) element;
				JRBaseParagraph para = (JRBaseParagraph) filed.getParagraph();
				JRDesignTextField tfiled = (JRDesignTextField) para.getParagraphContainer();
				JRDesignExpression expession = (JRDesignExpression) tfiled.getExpression();
				if (expession.getText().equals("$V{REPORT_COUNT}"))
				{
					expession.setText("$F{NUMBER}");
					JasperCompileManager.compileReport(this.jasperDesign);
					break;
				}

			}
		}

		List<DetailColumnInfo> list = reportCondition.getDetailColumnInfoList();
		boolean flag = false;
		if (!StringUtils.isNullString(reportCondition.getGroupField()))
		{
			for (DetailColumnInfo column : list)
			{
				if (column.getPropertyName().equals(reportCondition.getGroupField().replace("#", "$")))

				{
					flag = true;
					break;
				}
			}
		}
		else
		{
			flag = true;
		}

		if (!flag)
		{
			JRDesignField jrfilec = new JRDesignField();
			jrfilec.setValueClassName(String.class.getName());
			jrfilec.setName(reportCondition.getGroupField().replace("$", "#"));
			jasperDesign.addField(jrfilec);
			JasperCompileManager.compileReport(this.jasperDesign);

			DetailColumnInfo detailColumnInfo = new DetailColumnInfo("", String.class, reportCondition.getGroupField().replace("#", "$"));
			reportCondition.getDetailColumnInfoList().add(detailColumnInfo);
			jasperDesign.setPageWidth(jasperDesign.getPageWidth() + detailColumnInfo.getColumnWidth());
		}

		flag = false;
		List<String> groupFields = reportCondition.getGroupFields();
		if (!SetUtils.isNullList(groupFields))
		{
			int i = 1;
			for (String field : groupFields)
			{
				for (DetailColumnInfo column : list)
				{
					if (column.getPropertyName().equals(field.replace("#", "$")))

					{
						flag = true;
						break;
					}
				}
				if (!flag)
				{
					JRDesignField jrfilec = new JRDesignField();
					jrfilec.setValueClassName(String.class.getName());
					jrfilec.setName(field);
					jasperDesign.addField(jrfilec);

					DetailColumnInfo detailColumnInfo = new DetailColumnInfo("", String.class, field.replace("#", "$"));
					reportCondition.getDetailColumnInfoList().add(detailColumnInfo);
					jasperDesign.setPageWidth(jasperDesign.getPageWidth() + detailColumnInfo.getColumnWidth());
				}
				groupField(reportWidth, field, i);
				++i;
			}
			JasperCompileManager.compileReport(this.jasperDesign);
		}

		// detailColumnInfo.setColumnWidth(field.getPrope)

		// 报表数据分组
		if (null != reportCondition.getGroupField())
		{
			JRDesignGroup group = new JRDesignGroup();
			group.setName("group");

			this.jasperDesign.addGroup(group);

			JRDesignExpression expression = new JRDesignExpression();
			expression.setText("$F{" + reportCondition.getGroupField().replace("$", "#") + "}==null?\"--\":$F{" + reportCondition.getGroupField().replace("$", "#") + "}");
			group.setExpression(expression);

			JRDesignBand band = new JRDesignBand();
			band.setHeight(20);
			band.setSplitType(SplitTypeEnum.STRETCH);
			((JRDesignSection) group.getGroupHeaderSection()).addBand(band);

			JRDesignTextField text = new JRDesignTextField();
			text.setX(0);
			text.setY(0);
			text.setWidth(reportWidth);
			text.setHeight(20);
			setLineBox(text);

			expression = new JRDesignExpression();
			expression.setText("$F{" + reportCondition.getGroupField().replace("$", "#") + "}==null?\"--\":$F{" + reportCondition.getGroupField().replace("$", "#") + "}");
			text.setExpression(expression);
			text.setStretchWithOverflow(true);
			text.setPrintWhenDetailOverflows(true);
			text.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
			setTextEncoding(text);
			band.addElement(text);
			sortfields(reportCondition.getGroupField().replace("$", "#"), SortOrderEnum.DESCENDING);
		}
		// 报表数据排序
		if (null != reportCondition.getSortField1() && isSort)
		{
			sortfields(reportCondition.getSortField1().replace("$", "#"),
					SortOrderEnum.getByName(reportCondition.getSortValue1().equals("ASCENDING") ? "Ascending" : "Descending"));
		}
		// 报表数据排序
		if (null != reportCondition.getSortField2() && isSort)
		{
			sortfields(reportCondition.getSortField2().replace("$", "#"),
					SortOrderEnum.getByName(reportCondition.getSortValue2().equals("ASCENDING") ? "Ascending" : "Descending"));
		}
		// 报表数据排序
		if (null != reportCondition.getSortField3() && isSort)
		{
			sortfields(reportCondition.getSortField3().replace("$", "#"),
					SortOrderEnum.getByName(reportCondition.getSortValue3().equals("ASCENDING") ? "Ascending" : "Descending"));
		}

	}

	private void sortfields(String name, SortOrderEnum order) throws JRException
	{
		boolean flag = true;
		JRSortField[] sortFields = this.jasperDesign.getSortFields();
		for (int m = 0; m < sortFields.length; m++)
		{
			if (sortFields[m].getName().equals(name))
			{
				flag = false;
				break;
			}
		}

		if (flag)
		{
			JRDesignSortField sortField = new JRDesignSortField();
			sortField.setOrder(order);
			sortField.setType(SortFieldTypeEnum.FIELD);
			sortField.setName(name);
			this.jasperDesign.addSortField(sortField);
		}
	}

	private void groupField(int reportWidth, String field, int i) throws JRException
	{
		JRDesignGroup group = new JRDesignGroup();
		group.setName("group" + i);

		this.jasperDesign.addGroup(group);

		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$F{" + field.replace("$", "#") + "}==null?\"--\":$F{" + field.replace("$", "#") + "}");
		group.setExpression(expression);
		group.setStartNewPage(true);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(20);
		band.setSplitType(SplitTypeEnum.STRETCH);
		((JRDesignSection) group.getGroupHeaderSection()).addBand(band);

		JRDesignTextField text = new JRDesignTextField();
		text.setX(0);
		text.setY(0);
		text.setWidth(reportWidth);
		text.setHeight(20);
		expression = new JRDesignExpression();
		expression.setText("$F{" + field.replace("$", "#") + "}==null?\"--\":$F{" + field.replace("$", "#") + "}");
		text.setExpression(expression);
		text.setStretchWithOverflow(true);
		text.setPrintWhenDetailOverflows(true);
		text.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		setLineBox(text);
		setTextEncoding(text);
		band.addElement(text);

		sortfields(field.replace("$", "#"), SortOrderEnum.DESCENDING);
	}

	/**
	 * (公共方法)
	 * 
	 * @param textField
	 *            普通文本:设置文本框边框
	 */
	private void setLineBox(JRDesignTextElement textElement)
	{
		JRLineBox box = textElement.getLineBox();
		box.setTopPadding(1);
		box.setBottomPadding(1);
		box.setLeftPadding(1);
		box.setRightPadding(1);
		box.getTopPen().setLineWidth((float) 0.5);
		box.getTopPen().setLineStyle(LineStyleEnum.SOLID);
		box.getTopPen().setLineColor(Color.BLACK);
		box.getBottomPen().setLineWidth((float) 0.5);
		box.getBottomPen().setLineStyle(LineStyleEnum.SOLID);
		box.getBottomPen().setLineColor(Color.BLACK);
		box.getLeftPen().setLineWidth((float) 0.5);
		box.getLeftPen().setLineStyle(LineStyleEnum.SOLID);
		box.getLeftPen().setLineColor(Color.BLACK);
		box.getRightPen().setLineWidth((float) 0.5);
		box.getRightPen().setLineStyle(LineStyleEnum.SOLID);
		box.getRightPen().setLineColor(Color.BLACK);
	}

	/**
	 * 子报表设计
	 */
	public void setSubReportDesign()
	{
		JRDesignSubreport subReport = new JRDesignSubreport(jasperDesign);
		subReport.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
		subReport.setPrintWhenDetailOverflows(true);
		// subReport.setX(colWidth);
		subReport.setY(0);
		// subReport.setWidth(lieShu * colWidth);
		subReport.setHeight(20);
		// subReport.setKey("");

		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$P{subReportDataSource}");
		subReport.setDataSourceExpression(expression);

		expression = new JRDesignExpression();
		expression.setText("\"XSDShouRuHuiZong_subReport.jasper\"");
		subReport.setExpression(expression);

		// band.addElement(subReport);
	}

	/**
	 * 半定制化模板（列数固定,某列有1到N个字段的情况）
	 * 
	 * @param jasperDesign
	 * @throws JRException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void setReportFieldPersonalized(JasperDesign jasperDesign, ReportConfiguration configuration) throws JRException
	{
		JRDesignBand detail = (JRDesignBand) jasperDesign.getDetailSection().getBands()[0];

		for (int i = 0; i < configuration.getDetailColumnInfoList().size(); i++)
		{
			DetailColumnInfo detailColumnInfo = configuration.getDetailColumnInfoList().get(i);

			// 定义field
			String[] columns = detailColumnInfo.getPropertyName().split(",");
			for (String column : columns)
			{
				JRDesignField jrfilec = new JRDesignField();
				jrfilec.setValueClassName(detailColumnInfo.getType().getName());
				jrfilec.setName(column.replace("$", "#"));
				jasperDesign.addField(jrfilec);
			}

			// 定义变量,对'$'进行转义,变量之间用'+'连接
			String field = "";
			for (String column : columns)
			{
				field = field + "$F{" + column.replace("$", "#") + "}+';'+";
			}
			field = field.substring(0, field.length() - 5);
			// 定制化模板的textfield定义了key属性，通过key对应字段和模板中字段的位置
			JRDesignTextField textField = (JRDesignTextField) detail.getElementByKey(detailColumnInfo.getTextFieldKey());

			// 当取出的数据为null时，设置数据为空
			textField.setBlankWhenNull(true);
			textField.setHorizontalAlignment(detailColumnInfo.getHorizontalAlignment());
			JRDesignExpression expression = new JRDesignExpression();
			// expression.setValueClassName(detailColumnInfo.getType().getName());
			expression.setText(field);
			textField.setExpression(expression);

			String pattern = detailColumnInfo.getPropertiesMap().getProperty("Pattern");
			this.formatTextField(pattern, textField);
			// 对文字过多的适当调节
			textField.setStretchWithOverflow(true);
			textField.setPrintWhenDetailOverflows(true);
			textField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
			textField.setForecolor(detailColumnInfo.getFontColor());

		}
	}

	/**
	 * 格式化数据
	 * 
	 * @param textField
	 */
	private void formatTextField(String pattern, JRDesignTextField textField)
	{
		if (pattern.length() > 0)
		{
			textField.setPattern(pattern);
		}
	}

	/**
	 * (公共方法)
	 * 
	 * @param textField
	 *            普通文本
	 */
	private void setTextEncoding(JRDesignTextElement textElement)
	{
		if (null != this.jasperDesign.getColumnHeader() && this.jasperDesign.getColumnHeader().getElements().length > 0)
		{
			JRElement element = this.jasperDesign.getColumnHeader().getElements()[0];
			if (element instanceof JRDesignTextField)
			{
				JRDesignTextField textField = (JRDesignTextField) element;
				textElement.setFontName(textField.getFontName());
				textElement.setPdfFontName(textField.getPdfFontName());
				textElement.setPdfEncoding(textField.getPdfEncoding());
			}
			else if (element instanceof JRDesignStaticText)
			{
				JRDesignStaticText textField = (JRDesignStaticText) element;
				textElement.setFontName(textField.getFontName());
				textElement.setPdfFontName(textField.getPdfFontName());
				textElement.setPdfEncoding(textField.getPdfEncoding());
			}
			else
			{
				textElement.setFontName("Arial");
				textElement.setPdfFontName("STSong-Light");
				textElement.setPdfEncoding("UniGB-UCS2-H");
			}
		}
		else
		{
			textElement.setFontName("Arial");
			textElement.setPdfFontName("STSong-Light");
			textElement.setPdfEncoding("UniGB-UCS2-H");
			/*
			 * textElement.setFontName("SansSerif");
			 * textElement.setPdfFontName("STSong-Light");
			 * textElement.setPdfEncoding("UniGB-UCS2-H");
			 */
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.app.report.GenericDynaReportBuilder#generateReport(dyna.app.report
	 * .ReportConfiguration, dyna.app.report.ReportDataProvider, java.io.File,
	 * java.io.File)
	 */
	@Override
	public <T extends DynaObject> void generateReport(ReportConfiguration configuration, ReportDataProvider<T> provider, File reportTemplateFile) throws Exception
	{
		this.reportCondition = configuration;
		if (reportTemplateFile != null)
		{
			this.reportTemplateFile = reportTemplateFile;
		}
		this.detailFields = this.reportCondition.getDetailColumnInfoList();
		this.exportFileType = this.reportCondition.getExportFileType();
		this.generateReport(provider);

	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends DynaObject> void personalizedReport(File templateFile, ReportDataProvider<T> provider, ReportConfiguration configuration,
			Map<String, Object> headerParameters) throws Exception
	{
		JasperDesign jasperDesign = JRXmlLoader.load(templateFile.getAbsolutePath());
		this.jasperDesign = jasperDesign;
		this.reportCondition = configuration;
		this.headerParameters = headerParameters;
		// configuration.g
		this.setReportOtherComponents(this.getDetailWidth(jasperDesign), true);
		if (configuration.getExportFileType().equals(ReportTypeEnum.EXCEL))
		{
			// 导出excel时，去除左边空白区域
			jasperDesign.setLeftMargin(0);
			// 导出excel时，忽略分页;
			jasperDesign.setIgnorePagination(true);

			// 导出excel文件去掉页眉页脚：该配置在导出excel或pdf报表时，某些情况下可能存在冲突
			// jasperDesign.setPageFooter(null);
			// jasperDesign.setPageHeader(null);
		}
		jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		// 定制化报表列数固定：封装列属性
		// setReportFieldPersonalized(jasperDesign,configuration);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("reportTitle", configuration.getReportTitle());
		if (null != headerParameters)
		{
			param.putAll(headerParameters);
		}
		if ((!SetUtils.isNullList(configuration.getGroupFields()) || !StringUtils.isNullString(configuration.getGroupField()))
				&& configuration.getExportFileType().equals(ReportTypeEnum.EXCEL))
		{
			param.put("pageSize", 600);
		}
		else
		{
			param.put("pageSize", configuration.getPageCount() + 1);
		}

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new GenericDynaDataSource(provider, configuration.getDetailColumnInfoList()));

		this.setJRExporter(configuration.getExportFileType(), configuration.getExportToFilePath().getAbsolutePath(), jasperPrint, configuration.getReportTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.app.report.GenericDynaReportBuilder#personalizedReportVO(java.io
	 * .File, dyna.app.report.ReportDataProvider,
	 * dyna.app.report.ReportConfiguration, java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void personalizedReportVO(File reportTemplateFile, ReportDataProvider provider, ReportConfiguration configuration, Map<String, Object> headerParameters, List dataList)
			throws Exception
	{
		JasperDesign jasperDesign = JRXmlLoader.load(reportTemplateFile.getAbsolutePath());
		if (configuration.getExportFileType().equals(ReportTypeEnum.EXCEL))
		{
			// 导出excel时，去除左边空白区域
			jasperDesign.setLeftMargin(0);
			// 导出excel时，忽略分页;
			jasperDesign.setIgnorePagination(true);

			// 导出excel文件去掉页眉页脚：该配置在导出excel或pdf报表时，某些情况下可能存在冲突
			jasperDesign.setPageFooter(null);
			// jasperDesign.setPageHeader(null);
		}
		jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		// 定制化报表列数固定：封装列属性
		// setReportFieldPersonalized(jasperDesign,configuration);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		Map<String, Object> param = new HashMap<String, Object>();

		param.putAll(headerParameters);
		param.put("pageSize", configuration.getPageCount() + 1);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JRBeanCollectionDataSource(dataList));

		this.setJRExporter(configuration.getExportFileType(), configuration.getExportToFilePath().getAbsolutePath(), jasperPrint, configuration.getReportTitle());

	}

	@Override
	public <T extends DynaObject> void personalizedReportByEC(File templateFile, List<Map<String, Object>> provider, ReportConfiguration configuration,
			Map<String, Object> headerParameters) throws Exception
	{
		JasperDesign jasperDesign = JRXmlLoader.load(templateFile.getAbsolutePath());
		this.jasperDesign = jasperDesign;
		this.reportCondition = configuration;
		this.headerParameters = headerParameters;
		// configuration.g
		this.setReportOtherComponents(this.getDetailWidth(jasperDesign), true);
		if (configuration.getExportFileType().equals(ReportTypeEnum.EXCEL))
		{
			// 导出excel时，去除左边空白区域
			jasperDesign.setLeftMargin(0);
			// 导出excel时，忽略分页;
			jasperDesign.setIgnorePagination(true);

			// 导出excel文件去掉页眉页脚：该配置在导出excel或pdf报表时，某些情况下可能存在冲突
			// jasperDesign.setPageFooter(null);
			// jasperDesign.setPageHeader(null);
		}

		// 定制化报表列数固定：封装列属性
		// setReportFieldPersonalized(jasperDesign,configuration);
		jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("reportTitle", configuration.getReportTitle());
		if (null != headerParameters)
		{
			param.putAll(headerParameters);
		}
		param.put("pageSize", configuration.getPageCount() + 1);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, new DataSourceProvider(provider));

		this.setJRExporter(configuration.getExportFileType(), configuration.getExportToFilePath().getAbsolutePath(), jasperPrint, configuration.getReportTitle());

	}

	/**
	 * 取得第一个detai的所有column，使其长度相加，得到detail的宽度。
	 * 不支持多个detail。
	 * 
	 * @param jasperDesign
	 * @return
	 */
	private int getDetailWidth(JasperDesign jasperDesign)
	{
		int[] widths = this.getAllDetailWidth(jasperDesign);
		if (widths != null && widths.length > 0)
		{
			return widths[0];
		}

		return jasperDesign.getColumnWidth();
	}

	/**
	 * 取得所有detail的宽度
	 * 
	 * @param jasperDesign
	 * @return
	 */
	private int[] getAllDetailWidth(JasperDesign jasperDesign)
	{
		JRSection detailSection = jasperDesign.getDetailSection();
		if (detailSection == null)
		{
			return new int[] { jasperDesign.getColumnWidth() };
		}

		JRBand[] detailBands = detailSection.getBands();
		if (detailBands == null || detailBands.length == 0)
		{
			return new int[] { jasperDesign.getColumnWidth() };
		}

		int[] allDetailBandWidth = new int[detailBands.length];
		for (int i = 0; i < detailBands.length; i++)
		{
			JRBand jrBand = detailBands[i];
			JRDesignBand detailBand = (JRDesignBand) jrBand;

			JRElement[] elements = detailBand.getElements();
			if (elements == null || elements.length == 0)
			{
				return new int[] { jasperDesign.getColumnWidth() };
			}

			int width = 0;
			for (JRElement element : elements)
			{
				if (element instanceof JRDesignTextField)
				{
					JRDesignTextField textField = (JRDesignTextField) element;
					width = width + textField.getWidth();
				}
			}
			allDetailBandWidth[i] = width;
		}

		return allDetailBandWidth;
	}
}
