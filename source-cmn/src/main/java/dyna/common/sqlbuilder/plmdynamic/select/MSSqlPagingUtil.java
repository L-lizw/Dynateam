package dyna.common.sqlbuilder.plmdynamic.select;

public class MSSqlPagingUtil implements PagingUtil
{
	public String paging(String sql, int pageIndex, int rowsPerPage)
	{
		return sql + " offset " + String.valueOf((pageIndex - 1) * rowsPerPage) + " rows fetch next " + String.valueOf(rowsPerPage) + " rows only";
	}
}