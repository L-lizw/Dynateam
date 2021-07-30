package dyna.common.sqlbuilder.plmdynamic;

public abstract class SqlBuilder
{
	protected static final String FOUNDATION_TABLE_AS = "f$";

	public abstract String getSql(DynamicSqlParamData paramDynamicSqlParamData);
}