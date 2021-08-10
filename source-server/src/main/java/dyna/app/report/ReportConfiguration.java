/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReportCondition 报表详细信息设置，如：报表标题、每页显示多少条数据等
 * cuilei 2012-2-7
 */
package dyna.app.report;

import java.awt.Color;
import java.io.File;
import java.util.List;

import org.simpleframework.xml.Attribute;

import dyna.common.systemenum.ReportTypeEnum;

/**
 * 报表详细信息设置，如：报表标题、每页显示多少条数据等
 * 
 * @author cuilei
 * 
 */
public class ReportConfiguration
{
	// 报表描述
	private String					reportTitle				= "";
	// 详细
	@SuppressWarnings("rawtypes")
	private List<DetailColumnInfo>	detailColumnInfoList	= null;
	// 导出文件类型
	private ReportTypeEnum			exportFileType			= ReportTypeEnum.PDF;
	// 自定义分页每页的页数
	private Integer					pageCount				= 50;
	// 背景色设置
	private Color					backgroudcolor			= Color.WHITE;
	// 导出文件路径及名称
	private File					exportToFilePath		= null;
	// 单行颜色
	private Color					singleRowColor			= Color.WHITE;
	// 双行颜色
	private Color					doubleRowColor			= Color.LIGHT_GRAY;
	// 报表搜索条件
	private String					reportCondition			= "";
	// 排序字段1
	@Attribute(name = "sortField1", required = false)
	private String					sortField1				= null;
	// 排序：降序或者升序Y:desc, N:asc
	@Attribute(name = "sortValue1", required = false)
	private String					sortValue1				= null;
	// 排序字段2
	@Attribute(name = "sortField2", required = false)
	private String					sortField2				= null;
	// 顺序
	@Attribute(name = "sortValue2", required = false)
	private String					sortValue2				= null;
	// 排序字段3
	@Attribute(name = "sortField3", required = false)
	private String					sortField3				= null;
	// 顺序
	@Attribute(name = "sortValue3", required = false)
	private String					sortValue3				= null;
	// 建模器使用的分组
	@Attribute(name = "groupField", required = false)
	private String					groupField				= null;
	// bom报表使用的多个分组
	private List<String>			groupFields				= null;

	/**
	 * @return the groupFields
	 */
	public List<String> getGroupFields()
	{
		return groupFields;
	}

	/**
	 * @param groupFields
	 *            the groupFields to set
	 */
	public void setGroupFields(List<String> groupFields)
	{
		this.groupFields = groupFields;
	}

	/**
	 * @return the sortField1
	 */
	public String getSortField1()
	{
		return sortField1;
	}

	/**
	 * @return the sortValue1
	 */
	public String getSortValue1()
	{
		return sortValue1;
	}

	/**
	 * @return the sortField2
	 */
	public String getSortField2()
	{
		return sortField2;
	}

	/**
	 * @return the sortValue2
	 */
	public String getSortValue2()
	{
		return sortValue2;
	}

	/**
	 * @return the sortField3
	 */
	public String getSortField3()
	{
		return sortField3;
	}

	/**
	 * @return the sortValue3
	 */
	public String getSortValue3()
	{
		return sortValue3;
	}

	/**
	 * @return the groupField
	 */
	public String getGroupField()
	{
		return groupField;
	}

	/**
	 * @param sortField1
	 *            the sortField1 to set
	 */
	public void setSortField1(String sortField1)
	{
		this.sortField1 = sortField1;
	}

	/**
	 * @param sortValue1
	 *            the sortValue1 to set
	 */
	public void setSortValue1(String sortValue1)
	{
		this.sortValue1 = sortValue1;
	}

	/**
	 * @param sortField2
	 *            the sortField2 to set
	 */
	public void setSortField2(String sortField2)
	{
		this.sortField2 = sortField2;
	}

	/**
	 * @param sortValue2
	 *            the sortValue2 to set
	 */
	public void setSortValue2(String sortValue2)
	{
		this.sortValue2 = sortValue2;
	}

	/**
	 * @param sortField3
	 *            the sortField3 to set
	 */
	public void setSortField3(String sortField3)
	{
		this.sortField3 = sortField3;
	}

	/**
	 * @param sortValue3
	 *            the sortValue3 to set
	 */
	public void setSortValue3(String sortValue3)
	{
		this.sortValue3 = sortValue3;
	}

	/**
	 * @param groupField
	 *            the groupField to set
	 */
	public void setGroupField(String groupField)
	{
		this.groupField = groupField;
	}

	/**
	 * @return the reportCondition
	 */
	public String getReportCondition()
	{
		return reportCondition;
	}

	/**
	 * @param reportCondition
	 *            the reportCondition to set
	 */
	public void setReportCondition(String reportCondition)
	{
		this.reportCondition = reportCondition;
	}

	/**
	 * @return the singleRowColor
	 */
	public Color getSingleRowColor()
	{
		return singleRowColor;
	}

	/**
	 * @param singleRowColor
	 *            the singleRowColor to set
	 */
	public void setSingleRowColor(Color singleRowColor)
	{
		this.singleRowColor = singleRowColor;
	}

	/**
	 * @return the doubleRowColor
	 */
	public Color getDoubleRowColor()
	{
		return doubleRowColor;
	}

	/**
	 * @param doubleRowColor
	 *            the doubleRowColor to set
	 */
	public void setDoubleRowColor(Color doubleRowColor)
	{
		this.doubleRowColor = doubleRowColor;
	}

	/**
	 * @return the exportToFilePath
	 */
	public File getExportToFilePath()
	{
		return this.exportToFilePath;
	}

	/**
	 * @param exportToFilePath
	 *            the exportToFilePath to set
	 */
	public void setExportToFilePath(File exportToFilePath)
	{
		this.exportToFilePath = exportToFilePath;
	}

	public Color getBackgroudcolor()
	{
		return this.backgroudcolor;
	}

	public void setBackgroudcolor(Color backgroudcolor)
	{
		this.backgroudcolor = backgroudcolor;
	}

	public String getReportTitle()
	{
		return this.reportTitle;
	}

	public void setReportTitle(String reportTitle)
	{
		this.reportTitle = reportTitle;
	}

	@SuppressWarnings("rawtypes")
	public List<DetailColumnInfo> getDetailColumnInfoList()
	{
		return this.detailColumnInfoList;
	}

	@SuppressWarnings("rawtypes")
	public void setDetailColumnInfoList(List<DetailColumnInfo> detailColumnInfoList)
	{
		this.detailColumnInfoList = detailColumnInfoList;
	}

	public ReportTypeEnum getExportFileType()
	{
		return this.exportFileType;
	}

	public void setExportFileType(ReportTypeEnum exportFileType)
	{
		this.exportFileType = exportFileType;
	}

	public Integer getPageCount()
	{
		return this.pageCount;
	}

	public void setPageCount(Integer pageCount)
	{
		this.pageCount = pageCount;
	}

}
