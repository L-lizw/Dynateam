package dyna.common.bean.data.configparamter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ConfigParameterTableType;
import dyna.common.util.ConfigUtil;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class ConfigVariable extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long							serialVersionUID	= 3558073710207376384L;

	private List<TableOfGroup>							tableOfGroupList	= null;

	private List<TableOfList>							tableOfList			= null;

	private List<TableOfExpression>						expressionList		= null;

	private List<TableOfInputVariable>					inptVarList			= null;

	private List<TableOfMark>							markList			= null;

	private List<TableOfRegion>							regionList			= null;

	private List<TableOfMultiCondition>					multiRegionList			= null;

	private List<TableOfParameter>						parameterList		= null;

	private List<DynamicColumnTitle>					titleList			= null;

	private Map<String, TableOfGroup>					groupMap			= new HashMap<String, TableOfGroup>();

	private Map<String, TableOfList>					listMap				= new HashMap<String, TableOfList>();

	private Map<String, TableOfInputVariable>	inptMap				= new LinkedHashMap<String, TableOfInputVariable>();
	
	// 所有的变量
	private List<String>								allVariables		= new ArrayList<String>();

	public void prepare()
	{
		if (!SetUtils.isNullList(this.tableOfGroupList))
		{
			for (TableOfGroup g : this.tableOfGroupList)
			{
				this.groupMap.put(g.getGNumber(), g);
			}
		}
		if (!SetUtils.isNullList(this.tableOfList))
		{
			for (TableOfList l : this.tableOfList)
			{
				this.listMap.put(l.getLNumber(), l);
			}
		}
		if (!SetUtils.isNullList(this.inptVarList))
		{
			Collections.sort(this.inptVarList, new Comparator<TableOfInputVariable>() {

				@Override
				public int compare(TableOfInputVariable o1, TableOfInputVariable o2)
				{
					return Integer.valueOf(o1.getSequence()).compareTo(Integer.valueOf(o2.getSequence()));
				}
			});
			for (TableOfInputVariable inpt : this.inptVarList)
			{
				this.inptMap.put(inpt.getName(), inpt);
				this.allVariables.add(inpt.getName());
			}
		}
		if (!SetUtils.isNullList(this.expressionList))
		{
			for (TableOfExpression expression : this.expressionList)
			{
				this.allVariables.add(expression.getDrawvariable());
			}
		}
		if (!SetUtils.isNullList(this.multiRegionList))
		{
			for (TableOfMultiCondition expression : this.multiRegionList)
			{
				if (!SetUtils.isNullList(expression.getColumns()))
				{
					for (DynamicOfMultiVariable column : expression.getColumns())
					{
						this.allVariables.add(column.getName());
					}
				}
			}
		}
		if (!SetUtils.isNullList(this.titleList))
		{
			for (DynamicColumnTitle title : this.titleList)
			{
				if (title.getTableType() == ConfigParameterTableType.P)
				{
					continue;
				}
				if (ConfigUtil.isDrawVariable(title.getTitle()) )
				{
					this.allVariables.add(title.getTitle());
				}
			}
		}
	}

	public void setGroupList(List<TableOfGroup> tableOfGroupList)
	{
		this.tableOfGroupList = tableOfGroupList;
	}

	public List<TableOfGroup> getGroupList()
	{
		return this.tableOfGroupList;
	}

	public void setListOfList(List<TableOfList> tableOfList)
	{
		this.tableOfList = tableOfList;
	}

	public List<TableOfList> getListOfList()
	{
		return this.tableOfList;
	}

	public void setExpressionList(List<TableOfExpression> expressionList)
	{
		this.expressionList = expressionList;
	}

	public List<TableOfExpression> getExpressionList()
	{
		return this.expressionList;
	}

	public void setInptVarList(List<TableOfInputVariable> inptVarList)
	{
		this.inptVarList = inptVarList;
	}

	public List<TableOfInputVariable> getInptVarList()
	{
		return this.inptVarList;
	}

	public void setMarkList(List<TableOfMark> markList)
	{
		this.markList = markList;
	}

	public List<TableOfMark> getMarkList()
	{
		return this.markList;
	}

	public void setRegionList(List<TableOfRegion> regionList)
	{
		this.regionList = regionList;
	}

	public List<TableOfRegion> getRegionList()
	{
		return this.regionList;
	}

	public void setParameterList(List<TableOfParameter> parameterList)
	{
		this.parameterList = parameterList;
	}

	public List<TableOfParameter> getParameterList()
	{
		return this.parameterList;
	}

	public TableOfParameter getParameter(String gNumber)
	{
		if (!StringUtils.isNullString(gNumber))
		{
			if (!SetUtils.isNullList(this.parameterList))
			{
				for (TableOfParameter p : this.parameterList)
				{
					if (gNumber.equals(p.getGNumber()))
					{
						return p;
					}
				}
			}
		}
		return null;
	}

	public void setTitleList(List<DynamicColumnTitle> titleList)
	{
		this.titleList = titleList;
	}

	public List<DynamicColumnTitle> getTitleList()
	{
		return this.titleList;
	}

	public Map<String, TableOfGroup> getGroupMap()
	{
		return this.groupMap;
	}

	public Map<String, TableOfList> getListMap()
	{
		return this.listMap;
	}

	public Map<String, TableOfInputVariable> getInptVarMap()
	{
		return this.inptMap;
	}

	public List<TableOfMultiCondition> getMultiRegionList() {
		return multiRegionList;
	}

	public void setMultiRegionList(List<TableOfMultiCondition> multiRegionList) {
		this.multiRegionList = multiRegionList;
	}

	/**
	 * 取得件号,选配和输入变量的描述
	 * 
	 * @return
	 */
	public String getDescription(String str)
	{
		if (StringUtils.isNullString(str))
		{
			return null;
		}
		if (str.startsWith("G"))
		{
			TableOfGroup g = this.groupMap.get(str);
			String description = null;
			if (g != null)
			{
			}
			if (!StringUtils.isNullString(description))
			{
				return description;
			}
			return str;
		}
		else if (str.startsWith("L"))
		{
			TableOfList l = this.listMap.get(str);
			String description = null;
			if (l != null)
			{
				description = l.getDescription();
			}
			if (!StringUtils.isNullString(description))
			{
				return description;
			}
			return str;
		}
		else
		{
			TableOfInputVariable inpt = this.inptMap.get(str);
			String description = null;
			if (inpt != null)
			{
				description = inpt.getDescription();
			}
			if (!StringUtils.isNullString(description))
			{
				return description;
			}
			return str;
		}
	}
	
	
	public List<String> getAllVariables()
	{
		return this.allVariables;
	}
}
