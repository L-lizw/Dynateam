/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common exception
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.exception;

import java.sql.SQLException;

import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.StringUtils;

/**
 * 数据服务通用异常
 * 
 * @author xiasheng
 */
public abstract class DynaDataException extends RuntimeException
{

	private static final long	serialVersionUID	= 7416176841400421375L;
	protected Object[]			args				= null;
	DataExceptionEnum			dataExceptionEnum	= null;

	/**
	 * @return the dataExceptionEnum
	 */
	public DataExceptionEnum getDataExceptionEnum()
	{
		return this.dataExceptionEnum;
	}

	/**
	 * @param dataExceptionEnum
	 *            the dataExceptionEnum to set
	 */
	public void setDataExceptionEnum(DataExceptionEnum dataExceptionEnum)
	{
		this.dataExceptionEnum = dataExceptionEnum;
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs()
	{
		return this.args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(Object[] args)
	{
		this.args = args;
	}

	public DynaDataException(String message, Throwable cause)
	{
		super(message, cause);

		if (message == null)
		{
			message = "DynaDataException";
		}
		if (cause != null)
		{
			message = message.concat(":").concat(StringUtils.convertNULLtoString(cause.getLocalizedMessage()));
		}

		DynaLogger.error(message);
	}

	public DynaDataException(String errNo, Throwable cause, DataExceptionEnum dee, Object... args)
	{
		super(errNo, null);

		if (errNo == null)
		{
			errNo = "DynaDataException[" + dee + "]";
		}
		if (cause != null)
		{
			errNo = errNo.concat(":").concat(StringUtils.convertNULLtoString(cause.getLocalizedMessage()));
		}

		DynaLogger.error(errNo);

		this.setArgs(args);
		this.setDataExceptionEnum(dee);
	}

	public DynaDataException(String errNo, SQLException e, DataExceptionEnum dee, Object... args)
	{
		super(errNo);
	}

}
