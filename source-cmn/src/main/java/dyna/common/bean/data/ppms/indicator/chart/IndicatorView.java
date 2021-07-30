package dyna.common.bean.data.ppms.indicator.chart;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.SetUtils;

public class IndicatorView extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -5500695953136877557L;

	public static final String		ID					= "ID";

	private List<IndicatorView>		children			= new ArrayList<IndicatorView>();

	private List<IndicatorViewRow>	rows				= new ArrayList<IndicatorViewRow>();

	public void setId(String id)
	{
		this.put(ID, id);
	}

	public String getId()
	{
		return (String) this.get(ID);
	}

	public List<IndicatorViewRow> getRows()
	{
		// 非叶子节点不显示
		if (!SetUtils.isNullList(this.getChildren()))
		{
			return null;
		}
		return rows;
	}

	public void setRows(List<IndicatorViewRow> rows)
	{
		this.rows = rows;
	}

	public void addRow(IndicatorViewRow row)
	{
		this.rows.add(row);
	}

	public List<IndicatorView> getChildren()
	{
		return this.children;
	}

	public void setChildren(List<IndicatorView> children)
	{
		this.children = children;
	}

	public void addChild(IndicatorView child)
	{
		if (this.children == null)
		{
			this.children = new ArrayList<IndicatorView>();
		}

		if (child == null)
		{
			return;
		}

		for (IndicatorView c : this.children)
		{
			if (c.getId().equals(child.getId()))
			{
				return;
			}
		}
		this.children.add(child);
	}
}
