package dyna.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;

public class POIUtil
{
	public static final int	INTEGER_TYPE	= 1;
	public static final int	DOUBLE_TYPE		= 2;
	public static final int	STRING_TYPE		= 3;
	public static final int	DATE_TYPE		= 4;

	/**
	 * 单元格写入数据，style为空则不设置
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param style
	 * @param value
	 * @param type
	 * @throws ServiceRequestException
	 */
	public static void writeCell(HSSFSheet sheet, int rowNum, int columnNum, HSSFCellStyle style, Object value, int type) throws ServiceRequestException
	{
		if (value == null)
		{
			return;
		}
		if (sheet == null || rowNum < 0 || columnNum < 0)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", null);
		}
		HSSFRow row = sheet.getRow(rowNum);
		if (row == null)
		{
			row = sheet.createRow(rowNum);
		}
		HSSFCell cell = row.getCell(columnNum);
		if (cell == null)
		{
			cell = row.createCell(columnNum);
		}
		if (style != null)
		{
			cell.setCellStyle(style);
		}
		try
		{
			switch (type)
			{
			case INTEGER_TYPE:
				cell.setCellValue(Integer.parseInt(value.toString()));
				break;
			case DOUBLE_TYPE:
				cell.setCellValue(Double.parseDouble(value.toString()));
				break;
			case DATE_TYPE:
				cell.setCellValue(DateFormat.formatYMDHM((Date) value));
				break;
			case STRING_TYPE:
			default:
				cell.setCellValue(String.valueOf(value));
				break;
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_TYPE_IS_ERROR", null, e);
		}
	}

	/**
	 * 单元格写入数据，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param style
	 * @param value
	 * @param type
	 * @throws ServiceRequestException
	 */
	public static void writeCellUseXlsx(XSSFSheet sheet, int rowNum, int columnNum, XSSFCellStyle style, Object value, int type) throws ServiceRequestException
	{
		if (value == null)
		{
			return;
		}
		if (sheet == null || rowNum < 0 || columnNum < 0)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", null);
		}
		XSSFRow row = sheet.getRow(rowNum);
		if (row == null)
		{
			row = sheet.createRow(rowNum);
		}
		XSSFCell cell = row.getCell(columnNum);
		if (cell == null)
		{
			cell = row.createCell(columnNum);
		}
		if (style != null)
		{
			cell.setCellStyle(style);
		}
		try
		{
			switch (type)
			{
			case INTEGER_TYPE:
				cell.setCellValue(Integer.parseInt(value.toString()));
				break;
			case DOUBLE_TYPE:
				cell.setCellValue(Double.parseDouble(value.toString()));
				break;
			case DATE_TYPE:
				cell.setCellValue(DateFormat.formatYMDHM((Date) value));
				break;
			case STRING_TYPE:
			default:
				cell.setCellValue(String.valueOf(value));
				break;
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_TYPE_IS_ERROR", null, e);
		}
	}

	/**
	 * 写入一行数据，style为空则不设置
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param startColumn
	 * @param cellStyle
	 * @param values
	 * @param type
	 * @throws ServiceRequestException
	 */
	public static void writeRow(HSSFSheet sheet, int rowNum, int startColumn, int columnWidth, HSSFCellStyle cellStyle, Object[] values, int type) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		for (Object value : values)
		{
			sheet.setColumnWidth(startColumn, columnWidth * 256);
			writeCell(sheet, rowNum, startColumn, cellStyle, value, type);
			startColumn++;
		}
	}

	/**
	 * 写入一行数据，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param startColumn
	 * @param cellStyle
	 * @param values
	 * @param type
	 * @throws ServiceRequestException
	 */
	public static void writeRowUseXlsx(XSSFSheet sheet, int rowNum, int startColumn, int columnWidth, XSSFCellStyle cellStyle, Object[] values, int type)
			throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		for (Object value : values)
		{
			sheet.setColumnWidth(startColumn, columnWidth);
			writeCellUseXlsx(sheet, rowNum, startColumn, cellStyle, value, type);
			startColumn++;
		}
	}

	/**
	 * 写一列数据，style为空则不设置
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param cellStyle
	 * @param values
	 * @param type
	 * @throws ServiceRequestException
	 */
	public static void writeColumn(HSSFSheet sheet, int startRow, int columnNum, HSSFCellStyle cellStyle, Object[] values, int type) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		for (Object value : values)
		{
			writeCell(sheet, startRow, columnNum, cellStyle, value, type);
			startRow++;
		}
	}

	/**
	 * 写一列数据，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param cellStyle
	 * @param values
	 * @param type
	 * @throws ServiceRequestException
	 */
	public static void writeColumnUseXlsx(XSSFSheet sheet, int startRow, int columnNum, XSSFCellStyle cellStyle, Object[] values, int type) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		for (Object value : values)
		{
			writeCellUseXlsx(sheet, startRow, columnNum, cellStyle, value, type);
			startRow++;
		}
	}

	/**
	 * 写入一行数据 指定每列单元值类型，style为空则不设置
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param cellStyle
	 * @param values
	 * @param types
	 * @throws ServiceRequestException
	 */
	public static void writeRow(HSSFSheet sheet, int rowNum, int startColumn, HSSFCellStyle cellStyle, Object[] values, int[] types) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		if (types == null || types.length != values.length)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", null);
		}
		for (int i = 0; i < values.length; i++)
		{
			Object value = values[i];
			int type = types[i];
			writeCell(sheet, rowNum, startColumn + i, cellStyle, value, type);
		}
	}

	/**
	 * 写入一行数据 指定每列单元值类型，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param cellStyle
	 * @param values
	 * @param types
	 * @throws ServiceRequestException
	 */
	public static void writeRowUseXlsx(XSSFSheet sheet, int rowNum, int startColumn, XSSFCellStyle cellStyle, Object[] values, int[] types) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		if (types == null || types.length != values.length)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", null);
		}
		for (int i = 0; i < values.length; i++)
		{
			Object value = values[i];
			int type = types[i];
			writeCellUseXlsx(sheet, rowNum, startColumn + i, cellStyle, value, type);
		}
	}

	/**
	 * 写入一列数据 指定每行单元值类型，style为空则不设置
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param cellStyle
	 * @param values
	 * @param types
	 * @throws ServiceRequestException
	 */
	public static void writeColumn(HSSFSheet sheet, int startRow, int columnNum, HSSFCellStyle cellStyle, Object[] values, int[] types) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		if (types == null || types.length != values.length)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", null);
		}
		for (int i = 0; i < values.length; i++)
		{
			Object value = values[i];
			int type = types[i];
			writeCell(sheet, startRow + i, columnNum, cellStyle, value, type);
		}
	}

	/**
	 * 写入一列数据 指定每行单元值类型，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @param cellStyle
	 * @param values
	 * @param types
	 * @throws ServiceRequestException
	 */
	public static void writeColumnUseXlsx(XSSFSheet sheet, int startRow, int columnNum, XSSFCellStyle cellStyle, Object[] values, int[] types) throws ServiceRequestException
	{
		if (values == null)
		{
			return;
		}
		if (types == null || types.length != values.length)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", null);
		}
		for (int i = 0; i < values.length; i++)
		{
			Object value = values[i];
			int type = types[i];
			writeCellUseXlsx(sheet, startRow + i, columnNum, cellStyle, value, type);
		}
	}

	/**
	 * 计算某一行 start列到end列（闭合区间）的值 只取整形，style为空则不设置
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws ServiceRequestException
	 */
	public static int countRowIntValues(HSSFSheet sheet, int rowNum, int startColumn, int endColumn) throws ServiceRequestException
	{
		if (sheet == null || rowNum < 0 || startColumn < 0 || startColumn > endColumn)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		int value = 0;
		HSSFRow row = sheet.getRow(rowNum);
		if (row == null)
		{
			return value;
		}
		for (int i = startColumn; i <= endColumn; i++)
		{
			HSSFCell cell = row.getCell(i);
			if (cell == null)
			{
				continue;
			}
			if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			{
				value += cell.getNumericCellValue();
			}
			else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
			{
				String cellValue = cell.getStringCellValue().trim();
				try
				{
					int valueInt = Integer.parseInt(cellValue);
					value += valueInt;
				}
				catch (NumberFormatException e)
				{
					throw new ServiceRequestException("ID_APP_CELLTYPE_NOT_NUMBER", "row:" + rowNum + ",column:" + i + ",value:" + cellValue); // 单元格值不是数字
				}
			}
			else
			{
				throw new ServiceRequestException("ID_APP_CELLTYPE_NOT_NUMBER", ""); // 单元格值不是数字
			}
		}
		return value;
	}

	/**
	 * 计算某一行 start列到end列（闭合区间）的值 只取整形，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws ServiceRequestException
	 */
	public static int countRowIntValuesUseXlsx(XSSFSheet sheet, int rowNum, int startColumn, int endColumn) throws ServiceRequestException
	{
		if (sheet == null || rowNum < 0 || startColumn < 0 || startColumn > endColumn)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		int value = 0;
		XSSFRow row = sheet.getRow(rowNum);
		if (row == null)
		{
			return value;
		}
		for (int i = startColumn; i <= endColumn; i++)
		{
			XSSFCell cell = row.getCell(i);
			if (cell == null)
			{
				continue;
			}
			if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
			{
				value += cell.getNumericCellValue();
			}
			else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
			{
				String cellValue = cell.getStringCellValue().trim();
				try
				{
					int valueInt = Integer.parseInt(cellValue);
					value += valueInt;
				}
				catch (NumberFormatException e)
				{
					throw new ServiceRequestException("ID_APP_CELLTYPE_NOT_NUMBER", "row:" + rowNum + ",column:" + i + ",value:" + cellValue); // 单元格值不是数字
				}
			}
			else
			{
				throw new ServiceRequestException("ID_APP_CELLTYPE_NOT_NUMBER", ""); // 单元格值不是数字
			}
		}
		return value;
	}

	/**
	 * 计算指定列 startRow行到endRow行(闭区间)总值 整型
	 * 
	 * @param sheet
	 * @param startRow
	 * @param endRow
	 * @param columnNum
	 * @return
	 * @throws ServiceRequestException
	 */
	public static int countColumnIntValue(HSSFSheet sheet, int startRow, int endRow, int columnNum) throws ServiceRequestException
	{
		if (sheet == null || startRow < 0 || endRow < 0 || columnNum < 0 || endRow < startRow)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		int result = 0;
		while (startRow <= endRow)
		{
			HSSFRow row = sheet.getRow(startRow);
			if (row == null || row.getCell(columnNum) == null)
			{
				continue;
			}
			String stringCellValue = null;
			double numericCellValue = 0;
			try
			{
				stringCellValue = row.getCell(columnNum).getStringCellValue();
			}
			catch (Exception e1)
			{
				numericCellValue = row.getCell(columnNum).getNumericCellValue();
			}
			if (!StringUtils.isNullString(stringCellValue))
			{
				try
				{
					result += Integer.parseInt(stringCellValue.trim());
				}
				catch (NumberFormatException e)
				{
					throw new ServiceRequestException("ID_APP_EXCEL_VALUETYPE_ERROR", null, null, startRow, columnNum, stringCellValue.trim());
				}
			}
			else
			{
				result += numericCellValue;
			}
			startRow++;
		}
		return result;
	}

	/**
	 * 计算指定列 startRow行到endRow行(闭区间)总值 整型(.xlsx)
	 * 
	 * @param sheet
	 * @param startRow
	 * @param endRow
	 * @param columnNum
	 * @return
	 * @throws ServiceRequestException
	 */
	public static int countColumnIntValueUseXlsx(XSSFSheet sheet, int startRow, int endRow, int columnNum) throws ServiceRequestException
	{
		if (sheet == null || startRow < 0 || endRow < 0 || columnNum < 0 || endRow < startRow)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		int result = 0;
		while (startRow <= endRow)
		{
			XSSFRow row = sheet.getRow(startRow);
			if (row == null || row.getCell(columnNum) == null)
			{
				continue;
			}
			String stringCellValue = null;
			double numericCellValue = 0;
			try
			{
				stringCellValue = row.getCell(columnNum).getStringCellValue();
			}
			catch (Exception e1)
			{
				numericCellValue = row.getCell(columnNum).getNumericCellValue();
			}
			if (!StringUtils.isNullString(stringCellValue))
			{
				try
				{
					result += Integer.parseInt(stringCellValue.trim());
				}
				catch (NumberFormatException e)
				{
					throw new ServiceRequestException("ID_APP_EXCEL_VALUETYPE_ERROR", null, null, startRow, columnNum, stringCellValue.trim());
				}
			}
			else
			{
				result += numericCellValue;
			}
			startRow++;
		}
		return result;
	}

	/**
	 * 合并单元格 并保持样式一致，style为空则不设置
	 * 
	 * @param sheet
	 * @param address
	 * @param cellStyle
	 * @throws ServiceRequestException
	 */
	public static void margeCell(HSSFSheet sheet, CellRangeAddress address, HSSFCellStyle cellStyle) throws ServiceRequestException
	{
		if (sheet == null || address == null)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		for (int i = address.getFirstRow(); i <= address.getLastRow(); i++)
		{
			HSSFRow hssfRow = sheet.getRow(i);
			if (hssfRow == null)
			{
				hssfRow = sheet.createRow(i);
			}
			for (int j = address.getFirstColumn(); j <= address.getLastColumn(); j++)
			{
				HSSFCell hssfCell = hssfRow.getCell(j);
				if (hssfCell == null)
				{
					hssfCell = hssfRow.createCell(j);
				}

				if (cellStyle != null)
				{
					hssfCell.setCellStyle(cellStyle);
				}

			}
		}
		sheet.addMergedRegion(address);
	}

	/**
	 * 合并单元格 并保持样式一致，style为空则不设置(.xlsx)
	 * 
	 * @param sheet
	 * @param address
	 * @param cellStyle
	 * @throws ServiceRequestException
	 */
	public static void margeCell4Xlsx(XSSFSheet sheet, CellRangeAddress address, XSSFCellStyle cellStyle) throws ServiceRequestException
	{
		if (sheet == null || address == null)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		for (int i = address.getFirstRow(); i <= address.getLastRow(); i++)
		{
			XSSFRow hssfRow = sheet.getRow(i);
			if (hssfRow == null)
			{
				hssfRow = sheet.createRow(i);
			}
			for (int j = address.getFirstColumn(); j <= address.getLastColumn(); j++)
			{
				XSSFCell hssfCell = hssfRow.getCell(j);
				if (hssfCell == null)
				{
					hssfCell = hssfRow.createCell(j);
				}
				if (cellStyle != null)
				{
					hssfCell.setCellStyle(cellStyle);
				}

			}
		}
		sheet.addMergedRegion(address);
	}

	/**
	 * 寻找startRow 到 (startRow-endRow)行间某列是否有与value值相同的单元格 跳过空单元格和无值单元格
	 * 
	 * @param sheet
	 * @param startRow
	 * @param endRow
	 * @param columnNum
	 * @param value
	 * @return
	 * @throws ServiceRequestException
	 */
	public static boolean isColumnValueEquals(HSSFSheet sheet, int startRow, int endRow, int columnNum, String value) throws ServiceRequestException
	{
		if (sheet == null || startRow < endRow || columnNum < 0 || value == null || endRow < 0)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		boolean result = false;
		HSSFRow row = sheet.getRow(startRow);
		if (row == null && startRow != endRow)
		{
			return isColumnValueEquals(sheet, startRow - 1, endRow, columnNum, value);
		}
		else if (row == null && value.equals(""))
		{
			return true;
		}
		HSSFCell cell = row.getCell(columnNum);
		if (cell == null && startRow != endRow)
		{
			return isColumnValueEquals(sheet, startRow - 1, endRow, columnNum, value);
		}
		else if (cell == null && value.equals(""))
		{
			return true;
		}
		String tempValue = "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
		{
			tempValue = String.valueOf(cell.getNumericCellValue());
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
		{
			tempValue = String.valueOf(cell.getStringCellValue());
		}
		if ("".equals(tempValue) && startRow != endRow)
		{
			return isColumnValueEquals(sheet, startRow - 1, endRow, columnNum, value);
		}

		if (value.equals(tempValue))
		{
			return true;
		}
		return result;
	}

	/**
	 * 寻找startRow 到 (startRow-endRow)行间某列是否有与value值相同的单元格 跳过空单元格和无值单元格(.xlsx)
	 * 
	 * @param sheet
	 * @param startRow
	 * @param endRow
	 * @param columnNum
	 * @param value
	 * @return
	 * @throws ServiceRequestException
	 */
	public static boolean isColumnValueEquals4Xlsx(XSSFSheet sheet, int startRow, int endRow, int columnNum, String value) throws ServiceRequestException
	{
		if (sheet == null || startRow < endRow || columnNum < 0 || value == null || endRow < 0)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		boolean result = false;
		XSSFRow row = sheet.getRow(startRow);
		if (row == null && startRow != endRow)
		{
			return isColumnValueEquals4Xlsx(sheet, startRow - 1, endRow, columnNum, value);
		}
		else if (row == null && value.equals(""))
		{
			return true;
		}
		XSSFCell cell = row.getCell(columnNum);
		if (cell == null && startRow != endRow)
		{
			return isColumnValueEquals4Xlsx(sheet, startRow - 1, endRow, columnNum, value);
		}
		else if (cell == null && value.equals(""))
		{
			return true;
		}
		String tempValue = "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
		{
			tempValue = String.valueOf(cell.getNumericCellValue());
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
		{
			tempValue = String.valueOf(cell.getStringCellValue());
		}
		if ("".equals(tempValue) && startRow != endRow)
		{
			return isColumnValueEquals4Xlsx(sheet, startRow - 1, endRow, columnNum, value);
		}

		if (value.equals(tempValue))
		{
			return true;
		}
		return result;
	}

	/**
	 * 获取某单元格样式 不存在没有则返回null
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @return
	 * @throws ServiceRequestException
	 */
	public static HSSFCellStyle getCellStyle(HSSFSheet sheet, int rowNum, int columnNum) throws ServiceRequestException
	{
		if (sheet == null || rowNum < 0 || columnNum < 0)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		HSSFCellStyle cellStyle = null;
		HSSFRow row = sheet.getRow(rowNum);
		if (row != null && row.getCell(columnNum) != null)
		{
			cellStyle = row.getCell(columnNum).getCellStyle();
		}
		return cellStyle;
	}

	/**
	 * 获取某单元格样式 不存在没有则返回null(.xlsx)
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param columnNum
	 * @return
	 * @throws ServiceRequestException
	 */
	public static XSSFCellStyle getCellStyle4Xlsx(XSSFSheet sheet, int rowNum, int columnNum) throws ServiceRequestException
	{
		if (sheet == null || rowNum < 0 || columnNum < 0)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		XSSFCellStyle cellStyle = null;
		XSSFRow row = sheet.getRow(rowNum);
		if (row != null && row.getCell(columnNum) != null)
		{
			cellStyle = row.getCell(columnNum).getCellStyle();
		}
		return cellStyle;
	}

	/**
	 * 创建默认单元格样式
	 * 
	 * @param wb
	 * @return
	 * @throws ServiceRequestException
	 */
	public static HSSFCellStyle createDefaultCellStyle(HSSFWorkbook wb) throws ServiceRequestException
	{
		if (wb == null)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
		return cellStyle;
	}

	/**
	 * 创建默认单元格样式(.xlsx)
	 * 
	 * @param wb
	 * @return
	 * @throws ServiceRequestException
	 */
	public static XSSFCellStyle createDefaultCellStyle4Xlsx(XSSFWorkbook wb) throws ServiceRequestException
	{
		if (wb == null)
		{
			throw new ServiceRequestException("ID_APP_REPORT_PARAM_IS_NULL_OR_ERROR", "");
		}
		XSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); // 下边框
		cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
		cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
		cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
		return cellStyle;
	}

	/**
	 * 列宽自适应,指定类的长度能较好展示该列文本值
	 * 
	 * @param sheet
	 * @param size
	 */
	public static void setSizeColumn(HSSFSheet sheet, int startColumn, int size)
	{
		for (int columnNum = startColumn; columnNum < startColumn + size; columnNum++)
		{
			int columnWidth = sheet.getColumnWidth(columnNum) / 256;
			for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++)
			{
				HSSFRow currentRow = sheet.getRow(rowNum);
				// 当前行未被使用过
				if (sheet.getRow(rowNum) == null)
				{
					continue;
				}

				if (currentRow.getCell(columnNum) != null)
				{
					HSSFCell currentCell = currentRow.getCell(columnNum);
					if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING)
					{
						int length = 0;
						try
						{
							length = currentCell.getStringCellValue().getBytes("UTF-8").length;
						}
						catch (UnsupportedEncodingException e)
						{
							DynaLogger.info("", e);
						}
						if (columnWidth < length)
						{
							columnWidth = length;
						}
					}
				}
			}
			sheet.setColumnWidth(columnNum, columnWidth * 256);
		}

	}

	/**
	 * 列宽自适应,指定类的长度能较好展示该列文本值(.xlsx)
	 * 
	 * @param sheet
	 * @param size
	 */
	public static void setSizeColumn4Xlsx(XSSFSheet sheet, int startColumn, int size)
	{
		for (int columnNum = startColumn; columnNum < startColumn + size; columnNum++)
		{
			int columnWidth = sheet.getColumnWidth(columnNum) / 256;
			for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++)
			{
				XSSFRow currentRow = sheet.getRow(rowNum);
				// 当前行未被使用过
				if (sheet.getRow(rowNum) == null)
				{
					continue;
				}

				if (currentRow.getCell(columnNum) != null)
				{
					XSSFCell currentCell = currentRow.getCell(columnNum);
					if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING)
					{
						int length = 0;
						try
						{
							length = currentCell.getStringCellValue().getBytes("UTF-8").length;
						}
						catch (UnsupportedEncodingException e)
						{
							DynaLogger.info("", e);
						}
						if (columnWidth < length)
						{
							columnWidth = length;
						}
					}
				}
			}
			sheet.setColumnWidth(columnNum, columnWidth * 256);
		}
	}

	/**
	 * excel2007在指定行数前插入一行
	 * 
	 * @param hSheet
	 * @param rowNumber
	 * @return
	 */
	public static HSSFRow insertRow(HSSFSheet hSheet, int rowNumber)
	{
		if (hSheet.getRow(rowNumber) != null)
		{
			int lastRowNo = hSheet.getLastRowNum();
			hSheet.shiftRows(rowNumber, lastRowNo, 1);
		}
		HSSFRow createRow = hSheet.createRow(rowNumber);
		return createRow;
	}

	public static void copyCell(HSSFSheet hSheet, int oldRowNumber, int oldColumnNumber, int rowNumber, int columnNumber) throws ServiceRequestException
	{
		HSSFRow oldRow = hSheet.getRow(oldRowNumber);
		if (oldRow != null)
		{
			HSSFCell oldCell = oldRow.getCell(oldColumnNumber);
			if (oldCell != null)
			{
				String stringCellValue = oldCell.getStringCellValue();
				HSSFRow row = hSheet.getRow(rowNumber);
				if (row != null)
				{
					hSheet.createRow(rowNumber);
				}
				HSSFCell cell = row.getCell(columnNumber);
				if (cell != null)
				{
					cell = row.createCell(columnNumber);
				}
				cell.setCellValue(stringCellValue);
			}
		}
	}

	public static void copyRow(HSSFSheet hSheet, int columnNum, int oldRowNumber, int oldColumnNumber, int rowNumber, int columnNumber) throws ServiceRequestException
	{
		for (int i = columnNumber; i <= columnNum; i++)
		{
			copyCell(hSheet, oldRowNumber, oldColumnNumber, rowNumber, i);
		}
	}

	public static void writeLine(HSSFWorkbook wb4H, HSSFSheet hSheet, int startRowNum, int startColumnName, int endRowNum, int endEndColumnName)
	{
		CreationHelper helper = wb4H.getCreationHelper();
		HSSFPatriarch drawing = hSheet.createDrawingPatriarch();
		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(startColumnName);
		anchor.setRow1(startRowNum);
		anchor.setCol2(endEndColumnName);
		anchor.setRow2(endRowNum);
		HSSFSimpleShape shape = drawing.createSimpleShape((HSSFClientAnchor) anchor);
		shape.setShapeType(ShapeTypes.LINE);
		shape.setLineWidth(1);
		shape.setLineStyle(1);
		// 设置线的颜色
		shape.setLineStyleColor(256, 256, 256);
	}
}
