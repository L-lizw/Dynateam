/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DetailColumnInfo 定义报表模板中detail区域的属性信息
 * cuilei 2012-2-7
 */
package dyna.app.report;

import java.awt.Color;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

/**
 * 定义报表模板中detail区域的属性信息
 * 
 * @author cuilei
 * 
 */
public class DetailColumnInfo
{
	public static final String			STYLE_INTERVAL_ROW		= "interval_row";
	public static final int				DEFAULT_COLUMN_WIDTH	= 100;

	// 列描述
	private String						columnDescription		= "";
	// 列类型
	private Class<?>					classT					= null;
	// 列属性名
	private String						propertyName			= "";
	// 横向对齐方式
	private HorizontalAlignEnum			horizontalAlignment		= HorizontalAlignEnum.CENTER;
	// 纵向对齐方式
	private VerticalAlignEnum			verticalAlignEnum		= VerticalAlignEnum.MIDDLE;
	// 数据字体颜色
	private Color						fontColor				= Color.BLACK;
	// 列宽
	private int							columnWidth				= DEFAULT_COLUMN_WIDTH;
	// 行样式
	private String						rowStyleName			= STYLE_INTERVAL_ROW;
	// 获取报表字段值
	private ReportFieldValueDecorater	valueDecorater			= new ReportFieldValueDecoraterImpl();
	// 列头背景色
	private Color						columnBackgroudColor	= Color.GRAY;
	// 列头字体颜色
	private Color						columnFontColor			= Color.BLUE;
	// 报表模板textField的key定义
	private String						textFieldKey			= "";
	// 字段属性集
	private JRPropertiesMap				propertiesMap			= null;

	public DetailColumnInfo(String columnDescription, Class<?> classT, String propertyName)
	{
		this(columnDescription, classT, propertyName, DEFAULT_COLUMN_WIDTH);
	}

	public DetailColumnInfo(String columnDescription, Class<?> classT, String propertyName, JRPropertiesMap propertiesMap)
	{
		this(columnDescription, classT, propertyName, DEFAULT_COLUMN_WIDTH, propertiesMap);
	}

	public DetailColumnInfo(String columnDescription, Class<?> classT, String propertyName, int columnWidth)
	{
		this.columnDescription = columnDescription;
		this.classT = classT;
		this.propertyName = propertyName;
		this.columnWidth = columnWidth;
	}

	public DetailColumnInfo(String columnDescription, Class<?> classT, String propertyName, int columnWidth, JRPropertiesMap propertiesMap)
	{
		this.columnDescription = columnDescription;
		this.classT = classT;
		this.propertyName = propertyName;
		this.columnWidth = columnWidth;
		this.propertiesMap = propertiesMap;
	}

	/**
	 * @return the textFieldKey
	 */
	public String getTextFieldKey()
	{
		return textFieldKey;
	}

	/**
	 * @param textFieldKey
	 *            the textFieldKey to set
	 */
	public void setTextFieldKey(String textFieldKey)
	{
		this.textFieldKey = textFieldKey;
	}

	public Class<?> getType()
	{
		return this.classT;
	}

	public void setType(Class<?> clas)
	{
		this.classT = clas;
	}

	public String getColumnDescription()
	{
		return this.columnDescription;
	}

	public void setColumnDescription(String columnDescription)
	{
		this.columnDescription = columnDescription;
	}

	/**
	 * @return the valueDecorater
	 */
	public ReportFieldValueDecorater getValueDecorater()
	{
		return this.valueDecorater;
	}

	/**
	 * @param valueDecorater
	 *            the valueDecorater to set
	 */
	public void setValueDecorater(ReportFieldValueDecorater valueDecorater)
	{
		this.valueDecorater = valueDecorater;
	}

	public String getPropertyName()
	{
		return this.propertyName;
	}

	public void setPropertyName(String propertyName)
	{
		this.propertyName = propertyName;
	}

	/**
	 * @return the hrizontalAlignment
	 */
	public HorizontalAlignEnum getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	/**
	 * @param hrizontalAlignment
	 *            the hrizontalAlignment to set
	 */
	public void setHorizontalAlignment(HorizontalAlignEnum hrizontalAlignment)
	{
		this.horizontalAlignment = hrizontalAlignment;
	}

	public VerticalAlignEnum getVerticalAlignment()
	{
		return this.verticalAlignEnum;
	}

	public void setVerticalAlignment(VerticalAlignEnum verticalAlignment)
	{
		this.verticalAlignEnum = verticalAlignment;
	}

	public Color getFontColor()
	{
		return this.fontColor;
	}

	public void setFontColor(Color fontColor)
	{
		this.fontColor = fontColor;
	}

	/**
	 * @return the columnWidth
	 */
	public int getColumnWidth()
	{
		return this.columnWidth;
	}

	/**
	 * @param columnWidth
	 *            the columnWidth to set
	 */
	public void setColumnWidth(int columnWidth)
	{
		this.columnWidth = columnWidth;
	}

	/**
	 * @return the rowStyleName
	 */
	public String getRowStyleName()
	{
		return this.rowStyleName;
	}

	/**
	 * @param rowStyleName
	 *            the rowStyleName to set
	 */
	public void setRowStyleName(String rowStyleName)
	{
		this.rowStyleName = rowStyleName;
	}

	/**
	 * @return the columnBackgroudColor
	 */
	public Color getColumnBackgroudColor()
	{
		return columnBackgroudColor;
	}

	/**
	 * @param columnBackgroudColor
	 *            the columnBackgroudColor to set
	 */
	public void setColumnBackgroudColor(Color columnBackgroudColor)
	{
		this.columnBackgroudColor = columnBackgroudColor;
	}

	/**
	 * @return the columnFontColor
	 */
	public Color getColumnFontColor()
	{
		return columnFontColor;
	}

	/**
	 * @param columnFontColor
	 *            the columnFontColor to set
	 */
	public void setColumnFontColor(Color columnFontColor)
	{
		this.columnFontColor = columnFontColor;
	}

	public JRPropertiesMap getPropertiesMap()
	{
		return propertiesMap == null ? new JRPropertiesMap() : propertiesMap;
	}

	public void setPropertiesMap(JRPropertiesMap propertiesMap)
	{
		this.propertiesMap = propertiesMap;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof DetailColumnInfo)
		{
			DetailColumnInfo obj_ = (DetailColumnInfo) obj;
			if (this.getPropertyName().equals(obj_.getPropertyName()))
			{
				return true;
			}
		}
		return false;
	}
}
