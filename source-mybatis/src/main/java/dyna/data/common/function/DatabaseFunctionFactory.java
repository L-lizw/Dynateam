package dyna.data.common.function;

import dyna.data.common.function.columntype.*;
import dyna.data.common.function.sqlconvert.*;
import dyna.data.common.function.typename.*;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFunctionFactory
{
	/**
	 * ddl命令转换
	 */
	private static final Map<String, DDLConvertFunction>      ddlCommandConvertFunctionBean = new HashMap<>();
	/**
	 * 列定义转换
	 */
	private static final Map<String, ColumnTypeFunction>      columnTypeFunctionBean        = new HashMap<>();
	/**
	 * 列名转换
	 */
	private static final Map<String, TypeNameConvertFunction> typeNameConvertFunctionBean   = new HashMap<>();

	public static String databaseType;

	static
	{
		columnTypeFunctionBean.put("MySQL", new MySQLColumnTypeFunction());
		columnTypeFunctionBean.put("Oracle", new OracleColumnTypeFunction());
		columnTypeFunctionBean.put("Postgresql", new PostgresqlColumnTypeFunction());
		columnTypeFunctionBean.put("SqlServer", new SQLServerColumnTypeFunction());

		typeNameConvertFunctionBean.put("MySQL", new MySQLTypeNameConvert());
		typeNameConvertFunctionBean.put("Oracle", new OracleTypeNameConvert());
		typeNameConvertFunctionBean.put("Postgresql", new PostgresqlTypeNameConvert());
		typeNameConvertFunctionBean.put("SqlServer", new SQLServerTypeNameConvert());

		ddlCommandConvertFunctionBean.put("MySQL", new MySQLDDLConvert());
		ddlCommandConvertFunctionBean.put("Oracle", new OracleDDLConvert());
		ddlCommandConvertFunctionBean.put("Postgresql", new PostgresqlDDLConvert());
		ddlCommandConvertFunctionBean.put("SqlServer", new SqlServerDDLConvert());
	}

	public static TypeNameConvertFunction getTypeNameConvertFunction()
	{
		return typeNameConvertFunctionBean.get(databaseType);
	}

	public static ColumnTypeFunction getColumnTypeFunction()
	{
		return columnTypeFunctionBean.get(databaseType);
	}

	public static DDLConvertFunction getDDLConvertFunction()
	{
		return ddlCommandConvertFunctionBean.get(databaseType);
	}
}
