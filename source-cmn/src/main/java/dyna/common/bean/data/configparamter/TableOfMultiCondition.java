package dyna.common.bean.data.configparamter;

import java.util.List;

import dyna.common.bean.data.SystemObject;

public class TableOfMultiCondition extends ConfigBase implements SystemObject
{
	private static final long		serialVersionUID	= -1773602785944799263L;

	public List<DynamicOfMultiVariable>	columns				= null;

	public List<TableOfDefineCondition>	conditions			= null;

	public boolean						hasGuid				= false;

	public int							index				= 0;

	public List<DynamicOfMultiVariable> getColumns()
	{
		return this.columns;
	}

	public void setColumns(List<DynamicOfMultiVariable> columns)
	{
		this.columns = columns;
	}

	public List<TableOfDefineCondition> getConditions()
	{
		return conditions;
	}

	public void setConditions(List<TableOfDefineCondition> conditions)
	{
		this.conditions = conditions;
	}

	public boolean isHasGuid()
	{
		return hasGuid;
	}

	public void setHasGuid(boolean hasGuid)
	{
		this.hasGuid = hasGuid;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public String getVarValue(String varName) 
	{
		if (this.getColumns()!=null)
		{
			for (DynamicOfMultiVariable column:this.getColumns())
			{
				if (varName.equalsIgnoreCase(column.getName()))
				{
					return column.getValue();
				}
			}
		}
		return null;
	}
}
