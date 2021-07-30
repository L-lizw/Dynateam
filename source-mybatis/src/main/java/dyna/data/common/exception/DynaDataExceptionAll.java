/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 捕获异常
 * JiangHL 2011-5-9
 */
package dyna.data.common.exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dyna.common.exception.DynaDataException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;

/**
 * 捕获异常
 * 
 * @author Administrator
 * 
 */
public class DynaDataExceptionAll extends DynaDataException
{
	private static final long	serialVersionUID	= 252786333493412067L;

	/**
	 * 
	 * @param message
	 * @param t
	 * @param dee
	 * @param param
	 */
	public DynaDataExceptionAll(String message, Throwable t, DataExceptionEnum dee, Object... param)
	{
		super(message, t, dee, param);
	}

	public DynaDataExceptionAll(String errNo, SQLException e, DataExceptionEnum dee, Object... param)
	{
		super(errNo, e, dee, param);
		if (e != null)
		{
			Throwable cause = e;
			if (!e.getClass().equals(SQLException.class))
			{
				cause = new SQLException(e.getMessage(), e.getSQLState(), e.getErrorCode(), e.getCause());
			}
			this.initCause(cause);
		}

		if (errNo == null)
		{
			errNo = "DynaDataSQLException[" + dee + "]";
		}

		if (e != null)
		{
			errNo = errNo.concat(":").concat(e.getLocalizedMessage());
		}

		DynaLogger.error(errNo);

		this.setArgs(param);
		this.setDataExceptionEnum(dee);

		if (e == null)
		{
			return;
		}
		if (e.getErrorCode() == 1)
		{
			this.setDataExceptionEnum(DataExceptionEnum.DS_UNIQUE);
		}
		else if (e.getErrorCode() == 12899)
		{
			String eStr = e.getCause().toString();
			String[] eStrList = eStr.split("\"");
			String[] value = eStrList[eStrList.length - 1].replace("(", "").replace(")", "").split(",");
			List<Object> messageList = new ArrayList<Object>();

			messageList.add(eStrList[5].trim()); // 字段名称
			// 实际值 格式为actual:2394，需要解析出“:”后面的数字
			String realValue = value[0].trim();
			realValue = realValue.substring(realValue.indexOf(":") + 1);
			messageList.add(realValue.trim());
			// 限制值 格式为maximum: 128，需要解析出“:”后面的数字
			String limitValue = value[1].trim();
			limitValue = limitValue.substring(limitValue.indexOf(":") + 1);
			messageList.add(limitValue.trim());
			this.setArgs(messageList.toArray());
			this.setDataExceptionEnum(DataExceptionEnum.DS_VALUE_TOO_LARGE);
		}
		else if (e.getErrorCode() == 2291)
		{
			this.setDataExceptionEnum(DataExceptionEnum.DS_CREATE_SYS_UK);
		}
		else if (e.getErrorCode() == 1400)
		{
			this.setDataExceptionEnum(DataExceptionEnum.FIELD_IS_NULL);
		}
		else if (e.getErrorCode() == 1438)
		{
			this.setDataExceptionEnum(DataExceptionEnum.DS_INT_FIELD_TOO_LARGE);
		}
		else if (e.getErrorCode() == 1722)
		{
			this.setDataExceptionEnum(DataExceptionEnum.DS_INVALID_NUMBER);
		}
		// 1461保存的字段值过长，1704查询的字段值过长
		else if (e.getErrorCode() == 1461 || e.getErrorCode() == 1704 || e.getErrorCode() == 1460
				|| e.getErrorCode() == 6502 || e.getErrorCode() == 6550)
		{
			this.setDataExceptionEnum(DataExceptionEnum.DS_DATA_TOO_LONG);
		}
	}
}
