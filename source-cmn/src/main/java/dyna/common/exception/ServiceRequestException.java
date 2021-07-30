/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceRequestException
 * Wanglei 2010-3-29
 */
package dyna.common.exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.StringUtils;

/**
 * 请求服务方法异常
 * 
 * @author Wanglei
 * 
 */
public class ServiceRequestException extends ParameterizedException
{
	private static final long serialVersionUID = 8152414435288146242L;

	public static ServiceRequestException createByDynaDataException(DynaDataException e, Object... args)
	{
		List<Object> argList = new ArrayList<Object>();
		DataExceptionEnum ee = e.getDataExceptionEnum();
		Object[] eargs = e.getArgs();
		if (eargs != null && eargs.length != 0)
		{
			argList.addAll(Arrays.asList(eargs));
		}
		if (args != null && args.length != 0)
		{
			argList.addAll(Arrays.asList(args));
		}

		Throwable dataEx = e.getCause();
		if (dataEx != null && dataEx.getCause() != null && dataEx.getCause() instanceof SQLException)
		{
			SQLException sqlEx = (SQLException) dataEx.getCause();
			argList.add(sqlEx.getMessage());
		}

		String message = null;
		if (dataEx != null)
		{
			if (StringUtils.isNullString(dataEx.getMessage()))
			{
				message = ee.name();
			}
			else
			{
				message = dataEx.getMessage();
			}
		}
		else
		{
			message = e.getMessage();
		}

		if (dataEx != null && dataEx instanceof ServiceNotAvailableException)
		{
			return new ServiceRequestException("ID_APP_DATA_SERVICE_NOT_AVAILABLE", dataEx.getMessage(), e);
		}
		DynaLogger.error(e.getMessage(), e);
		return new ServiceRequestException(ee.getMsrId(), message, null, argList.toArray());
	}

	public static ServiceRequestException createByDecorateException(DecorateException e)
	{
		return createByException(null, e);
	}

	public static ServiceRequestException createByException(String msrId, Exception e)
	{
		return new ServiceRequestException(msrId, e.getMessage(), e);
	}

	public ServiceRequestException(ScriptEvalResultWrap wrap)
	{
		super(null, null, wrap);
	}

	public ServiceRequestException(String message)
	{
		this(null, message, null, (Object[]) null);
	}

	public ServiceRequestException(String msrId, String message)
	{
		this(msrId, message, null, (Object[]) null);
	}

	public ServiceRequestException(String msrId, String message, Throwable t)
	{
		this(msrId, message, t, (Object[]) null);
	}

	/**
	 * @param msrId
	 * @param message
	 * @param t
	 * @param args
	 */
	public ServiceRequestException(String msrId, String message, Throwable t, Object... args)
	{
		super(msrId, message, t, args);
	}

}
