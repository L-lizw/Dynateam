package dyna.common.sqlbuilder.plmdynamic.select;

public class PostgresqlPagingUtil implements PagingUtil
{
	public String paging(String sql, int pageIndex, int rowsPerPage)
	{
		return "select * from(" + sql + ") p limit " + rowsPerPage + " offset " + (pageIndex - 1) * rowsPerPage;
	}
}