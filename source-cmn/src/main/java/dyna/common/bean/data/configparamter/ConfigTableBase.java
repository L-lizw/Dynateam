package dyna.common.bean.data.configparamter;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.util.SetUtils;

public class ConfigTableBase extends ConfigBase implements SystemObject
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -1773602785944799263L;

	public List<DynamicOfColumn>	columns				= new ArrayList<DynamicOfColumn>();

	public List<DynamicOfColumn> getColumns()
	{
		return this.columns;
	}

	public DynamicOfColumn getColumn(String columnName)
	{
		if (!SetUtils.isNullList(this.columns))
		{
			for (DynamicOfColumn column : this.columns)
			{
				if (column.getName().equals(columnName))
				{
					return column;
				}
			}
			return null;
		}
		return null;
	}

	public void setColumns(List<DynamicOfColumn> columns)
	{
		this.columns = columns;
	}

	public void addColumn(DynamicOfColumn column)
	{
		if (this.columns == null)
		{
			this.columns = new ArrayList<DynamicOfColumn>();
		}
		this.columns.add(column);
	}
}
