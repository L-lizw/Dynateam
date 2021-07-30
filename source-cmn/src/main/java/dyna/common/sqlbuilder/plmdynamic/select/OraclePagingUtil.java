package dyna.common.sqlbuilder.plmdynamic.select;

public class OraclePagingUtil implements PagingUtil
{
	public String paging(String sql, int pageIndex, int rowsPerPage)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT * ");
		buffer.append("  FROM (SELECT ROWNUM AS ROWINDEX, r.* ");
		buffer.append("          FROM (").append(sql).append(") r");
		buffer.append("         WHERE ROWNUM <= ").append(pageIndex * rowsPerPage);
		buffer.append("       ) p ");
		buffer.append(" WHERE p.ROWINDEX > ").append((pageIndex - 1) * rowsPerPage);
		return buffer.toString();
	}
}