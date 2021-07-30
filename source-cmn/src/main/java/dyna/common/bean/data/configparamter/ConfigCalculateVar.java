package dyna.common.bean.data.configparamter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ConfigParameterTableType;
import dyna.common.util.ConfigUtil;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class ConfigCalculateVar extends ConfigVariable
{

	/**
	 * 
	 */
	private static final long							serialVersionUID		= 6783519533874442746L;

	private String										gNumberStr				= "";

	private String										lNumberStr				= "";

	private String										originalInputVarString	= "";

	private String										regularInputVarString	= "";

	private Map<String, String>							inptVarValueMap			= null;

	// 有确定值得变量
	private Map<String, String>							variableHasValMap		= new HashMap<String, String>();

	// 需要计算值的变量
	private Map<String, Object>							variableNoValueMap		= new HashMap<String, Object>();

	// 取得所有配置参数值
	private List<String>								logList					= new ArrayList<String>();

	private List<String>								errVarList				= new ArrayList<String>();

	private Map<String, List<TableOfMultiCondition>>	varMultiRegionMap		= null;

	private Map<String, List<TableOfRegion>>			varRegionMap			= null;

	public void setAllInput(String gnumber, String lNumbers, String inptVarriables) throws ServiceRequestException
	{
		this.gNumberStr = gnumber;
		this.setOriginalInputVarString(inptVarriables);
		this.setGTableVariable();
		this.setLTableVariable(lNumbers);
		this.setFTableVariable();
		this.setOtherVariable();
		this.buildRegionVarOfTab();
		this.buildMultiRegionVarOfTab();
	}

	private void buildMultiRegionVarOfTab()
	{
		varMultiRegionMap = new HashMap<String, List<TableOfMultiCondition>>();
		if (!SetUtils.isNullList(this.getMultiRegionList()))
		{
			for (TableOfMultiCondition region : this.getMultiRegionList())
			{
				if (region.getColumns() != null)
				{
					for (DynamicOfMultiVariable column : region.getColumns())
					{
						if (!StringUtils.isNullString(column.getValue()))
						{
							List<TableOfMultiCondition> tempList = varMultiRegionMap.get(column.getName());
							if (tempList == null)
							{
								tempList = new ArrayList<TableOfMultiCondition>();
								varMultiRegionMap.put(column.getName(), tempList);
							}
							tempList.add(region);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 取得G番号的变量值
	 * 
	 * @param lNumbers
	 * @param configVariable
	 * @param variableHasValMap
	 * @param variableNoValueMap
	 * @throws ServiceRequestException
	 */
	private void setGTableVariable() throws ServiceRequestException
	{
		List<DynamicColumnTitle> titleList = this.getTitleList();
		if (!SetUtils.isNullList(titleList))
		{
			if ( this.getGroupMap().get(this.gNumberStr)!=null)
			{
				List<DynamicOfColumn> virableOfGTableList = this.getGroupMap().get(this.gNumberStr).columns;
				if (!SetUtils.isNullList(virableOfGTableList))
				{
					for (DynamicOfColumn column : virableOfGTableList)
					{
						if (ConfigUtil.isDrawVariable(column.getName()))
						{
							if (ConfigUtil.isDrawVariable(column.getValue()) || this.getAllVariables().contains(column.getValue()))
							{
								variableNoValueMap.put(column.getName(), column.getValue());
							}
							else
							{
								variableHasValMap.put(column.getName(), column.getValue());
							}
						}
					}
				}
			}
			for (DynamicColumnTitle title : titleList)
			{
				if (ConfigUtil.isDrawVariable(title.getTitle()))
				{
					if (title.getTableType() == ConfigParameterTableType.G )
					{
						if (!variableNoValueMap.containsKey(title.getTitle()) && !variableHasValMap.containsKey(title.getTitle()))
						{
							variableHasValMap.put(title.getTitle(), null);
						}
					}
				}
			}
		}
	}

	/**
	 * 取得L番号的变量值
	 * 
	 * @param lNumbers
	 * @param configVariable
	 * @param variableHasValMap
	 * @param variableNoValueMap
	 * @throws ServiceRequestException
	 */
	private void setLTableVariable(String lNumbers) throws ServiceRequestException
	{
		// 每一个图面变量只能有一个L番号配置数据
		// L表图面变量
		this.lNumberStr = lNumbers;

		List<DynamicOfColumn> virableOfLTableList = this.getAllVariableOfL(lNumberStr);
		if (!SetUtils.isNullList(virableOfLTableList))
		{
			for (DynamicOfColumn column : virableOfLTableList)
			{
				if (variableNoValueMap.containsKey(column.getName()) || variableHasValMap.containsKey(column.getName()))
				{
					throw new ServiceRequestException("ID_APP_CONFIG_VAR_OF_LNUMBER_DUPLICATE_SET", "variable " + column.getName() + " is duplicate set.", null, column.getName());
				}
				if (ConfigUtil.isDrawVariable(column.getValue()) || this.getAllVariables().contains(column.getValue()))
				{
					variableNoValueMap.put(column.getName(), column.getValue());
				}
				else
				{
					variableHasValMap.put(column.getName(), column.getValue());
				}
			}
		}
		List<DynamicColumnTitle> titleList = this.getTitleList();
		if (!SetUtils.isNullList(titleList))
		{
			for (DynamicColumnTitle title : titleList)
			{
				if (title.getTableType() == ConfigParameterTableType.La || title.getTableType() == ConfigParameterTableType.Lb)
				{
					if (!variableNoValueMap.containsKey(title.getTitle()) && !variableHasValMap.containsKey(title.getTitle()))
					{
						variableHasValMap.put(title.getTitle(), null);
					}
				}
			}
		}
	}

	/**
	 * 取得条件表的变量
	 * 
	 * @param configVariable
	 * @param variableHasValMap
	 * @param variableNoValueMap
	 */
	private void setOtherVariable()
	{
		List<String> varList = this.getAllVariables();
		if (!SetUtils.isNullList(varList))
		{
			for (String varName : varList)
			{
				if (!variableNoValueMap.containsKey(varName) && !variableHasValMap.containsKey(varName))
				{
					variableNoValueMap.put(varName, null);
				}
			}
		}
	}

	/**
	 * 取得F表图面变量的值
	 * 
	 * @param configVariable
	 * @param variableNoValueMap
	 */
	private void setFTableVariable()
	{
		List<TableOfExpression> listTableOfF = this.getExpressionList();
		if (!SetUtils.isNullList(listTableOfF))
		{
			for (TableOfExpression expression : listTableOfF)
			{
				// 把F表变量加入未计算变量表
				variableNoValueMap.put(expression.getDrawvariable(), expression);
			}
		}
	}

	private void setOriginalInputVarString(String inptVarriables)
	{
		this.originalInputVarString = inptVarriables;
		this.inptVarValueMap = ConfigUtil.transferInputVarStrToMap(originalInputVarString);

		StringBuffer buffer = new StringBuffer();
		TableOfParameter parameter = this.getParameter(this.gNumberStr);
		if (parameter != null)
		{
			List<DynamicOfColumn> columns = parameter.getColumns();
			if (!SetUtils.isNullList(columns))
			{
				for (DynamicOfColumn column : columns)
				{
					String varName = column.getValue();
					if (buffer.length() > 0)
					{
						buffer.append(";");
					}
					buffer.append(varName).append("=").append(StringUtils.convertNULLtoString(inptVarValueMap.get(varName)));
					variableHasValMap.put(varName, inptVarValueMap.get(varName));
				}
			}
		}
		else if (!SetUtils.isNullList(this.getInptVarList()))
		{
			for (TableOfInputVariable inputVar : this.getInptVarList())
			{
				String varName = inputVar.getName();
				if (buffer.length() > 0)
				{
					buffer.append(";");
				}
				buffer.append(varName).append("=").append(StringUtils.convertNULLtoString(inptVarValueMap.get(varName)));
				variableHasValMap.put(varName, inptVarValueMap.get(varName));
			}
		}
		this.regularInputVarString = buffer.toString();

	}

	private List<DynamicOfColumn> getAllVariableOfL(String lNumbers)
	{
		List<String> lNumberList = ConfigUtil.transferLNumberStrToList(lNumbers);
		if (SetUtils.isNullList(lNumberList))
		{
			return null;
		}

		// L00为默认值，不在此处理
		List<String> tmpList = new ArrayList<String>();
		tmpList.add("L00");
		lNumberList.removeAll(tmpList);

		List<DynamicOfColumn> virableOfLTableList = new ArrayList<DynamicOfColumn>();
		List<TableOfList> listOfList = this.getListOfList();
		if (!SetUtils.isNullList(listOfList))
		{
			for (TableOfList l : listOfList)
			{
				if (!lNumberList.contains(l.getLNumber()))
				{
					continue;
				}
				List<DynamicOfColumn> columns = l.getColumns();
				if (!SetUtils.isNullList(columns))
				{
					for (DynamicOfColumn column : columns)
					{
						if (!StringUtils.isNullString(column.getValue()))
						{
							virableOfLTableList.add(column);
						}
					}
				}
			}
		}
		return virableOfLTableList;
	}

	public void buildRegionVarOfTab()
	{

		// A-E,R,Q表图面变量
		List<TableOfRegion> regionList = this.getRegionList();

		varRegionMap = new HashMap<String, List<TableOfRegion>>();
		if (!SetUtils.isNullList(regionList))
		{
			for (TableOfRegion region : regionList)
			{

				List<DynamicOfColumn> columns = region.getColumns();
				if (!SetUtils.isNullList(columns))
				{
					for (DynamicOfColumn column : columns)
					{
						if (!StringUtils.isNullString(column.getValue()))
						{
							List<TableOfRegion> tempList = varRegionMap.get(column.getName());
							if (tempList == null)
							{
								tempList = new ArrayList<TableOfRegion>();
								varRegionMap.put(column.getName(), tempList);
							}
							tempList.add(region);
						}
					}
				}
			}
		}
	}

	public String getRegularInputVarString()
	{
		return this.regularInputVarString;

	}

	public List<String> getLogList()
	{
		return this.logList;
	}

	public List<String> getErrVarList()
	{
		return this.errVarList;
	}

	public void addToVarLogList(String log)
	{
		if (!logList.contains(log))
		{
			logList.add(log);
		}
	}

	public void addErrVar(String errVar)
	{
		if (!StringUtils.isNullString(errVar) && !errVarList.contains(errVar))
		{
			errVarList.add(errVar);
		}
	}

	public Map<String, String> getInptVarValueMap()
	{
		return this.inptVarValueMap;
	}

	public Map<String, String> getVariableHasValMap()
	{
		return this.variableHasValMap;
	}

	public Map<String, Object> getVariableNoValueMap()
	{
		return this.variableNoValueMap;
	}

	public List<TableOfMultiCondition> getVarMultiRegionList(String varName)
	{
		return varMultiRegionMap.get(varName);
	}

	public List<TableOfRegion> getVarRegionList(String varName)
	{
		return varRegionMap.get(varName);
	}

	public String getGNumber()
	{
		return this.gNumberStr;
	}

	public String getLNumber()
	{
		return this.lNumberStr;
	}

	public void setGNumber(String string)
	{
		this.gNumberStr = string;
	}

}
