package dyna.app.service.brs.cpb;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.configparamter.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class VarCalculateStub extends AbstractServiceStub<CPBImpl>
{

	// 取得所有变量的值
	public Map<String, String> listAllVariableValue(ObjectGuid objectGuid, Date ruleTime, ConfigCalculateVar configVariable) throws ServiceRequestException
	{
		// 取得L00的变量值
		this.setL00Variable(objectGuid, ruleTime, configVariable);
		// 输入确定值变量的值
		if (!SetUtils.isNullMap(configVariable.getVariableHasValMap()))
		{
			for (String var : configVariable.getVariableHasValMap().keySet())
			{
				String log = var + "=" + configVariable.getVariableHasValMap().get(var);
				configVariable.addToVarLogList(log);
			}
		}

		if (!SetUtils.isNullMap(configVariable.getVariableNoValueMap()))
		{
			List<String> searchedVariableList = new ArrayList<String>();
			for (String variable : configVariable.getVariableNoValueMap().keySet())
			{
				try
				{
					this.recurseForGetVariableValue(variable, configVariable, searchedVariableList);
				}
				catch (DynaDataException e)
				{
					configVariable.addErrVar(variable);
					e.printStackTrace();
				}
			}

			for (String var : configVariable.getVariableNoValueMap().keySet())
			{
				if (!configVariable.getVariableHasValMap().containsKey(var) && !configVariable.getErrVarList().contains(var))
				{
					configVariable.getErrVarList().add(var);
				}
			}
		}

		return configVariable.getVariableHasValMap();
	}

	/**
	 * 取得指定L番号的所有图面变量
	 * 
	 * @param configVariable
	 * @param lNumbers
	 * @return
	 */
	private String getVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{
		String value = null;
		if (configVariable.getVariableHasValMap().containsKey(variable))
		{
			value = configVariable.getVariableHasValMap().get(variable);
		}
		else if (ConfigUtil.isDrawVariable(variable))
		{
			boolean result_ = this.recurseForGetVariableValue(variable, configVariable, searchedVariableList);
			if (result_)
			{
				value = configVariable.getVariableHasValMap().get(variable);
				configVariable.addToVarLogList(variable + "=" + value);
			}
			else
			{
				configVariable.addErrVar(variable);
			}
		}
		else
		{
			value = variable;
		}
		return value;
	}

	private boolean recurseForGetMVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{
		if (SetUtils.isNullList(configVariable.getVarMultiRegionList(variable)))
		{
			String title = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_M");
			configVariable.addToVarLogList(this.getMessage("ID_APP_CONFIG_PARAM_NO_VARIABLE", variable, title));
			return false;
		}

		boolean result = true;
		List<TableOfMultiCondition> matchList = new ArrayList<TableOfMultiCondition>();
		for (TableOfMultiCondition region : configVariable.getVarMultiRegionList(variable))
		{
			if (region.getConditions() != null)
			{
				boolean isMatch = true;
				if (region.get("ISMATCH") == null)
				{
					for (TableOfDefineCondition condition : region.getConditions())
					{
						String variable1 = condition.getDefinitionName();
						String value1 = this.getVariableValue(variable1, configVariable, searchedVariableList);
						String tempvalue2 = condition.getDefinitionValue();
						String value2 = this.getVariableValue(tempvalue2, configVariable, searchedVariableList);
						if (StringUtils.isNullString(value1) || StringUtils.isNullString(value2))
						{
							isMatch = false;
							break;
						}
						isMatch = condition.getConditionMacth().match(value1, value2);
						if (!isMatch)
						{
							break;
						}
					}
					region.put("ISMATCH", isMatch);
				}
				else
				{
					isMatch = BooleanUtils.getBoolean((Boolean) region.get("ISMATCH"), false);
				}
				if (isMatch)
				{
					matchList.add(region);
				}
			}
		}
		if (matchList.size() == 1)
		{
			String title = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_M");
			String message = this.getMessage("ID_APP_CONFIG_TABLE_REGION_FIND", title, variable);
			configVariable.addToVarLogList(message);
			String valueVar = matchList.get(0).getVarValue(variable);
			String value = this.getVariableValue(valueVar, configVariable, searchedVariableList);
			if (configVariable.getErrVarList().contains(valueVar))
			{
				configVariable.addErrVar(variable);
			}
			else
			{
				configVariable.getVariableHasValMap().put(variable, value);
				configVariable.addToVarLogList(variable + "=" + value);
			}
		}
		else if (matchList.size() > 1)
		{
			configVariable.addToVarLogList(this.getMessage("ID_APP_CONFIG_PARAM_MULTY_DATA_FOUND", variable));
			configVariable.getErrVarList().add(variable);
		}
		else
		{
			configVariable.getVariableHasValMap().put(variable, null);
			configVariable.addToVarLogList(variable + "= null");
		}
		if (configVariable.getErrVarList().contains(variable))
		{
			return false;
		}
		return result;
	}

	/**
	 * 取得L00番号对应的变量值
	 * 
	 * @param objectGuid
	 * @param configVariable
	 * @param ruleTime
	 * @param variableHasValMap
	 * @param variableNoValueMap
	 * @throws ServiceRequestException
	 */
	private void setL00Variable(ObjectGuid objectGuid, Date ruleTime, ConfigCalculateVar configVariable) throws ServiceRequestException
	{
		// 当L号指定的图面变量没有值时，使用默认值L00的图面变量
		Map<String, String> defaultL00ColumnMap = this.getDefaultL00Column(objectGuid, ruleTime);
		if (!SetUtils.isNullMap(defaultL00ColumnMap))
		{
			for (String variable : defaultL00ColumnMap.keySet())
			{
				String value = defaultL00ColumnMap.get(variable);
				if (!StringUtils.isNullString(value) && !configVariable.getVariableNoValueMap().containsKey(variable)
						&& !configVariable.getVariableHasValMap().containsKey(variable))
				{
					if (ConfigUtil.isDrawVariable(value) || configVariable.getAllVariables().contains(value))
					{
						configVariable.getVariableNoValueMap().put(variable, value);
					}
					else
					{
						configVariable.getVariableHasValMap().put(variable, value);
					}
				}
			}
		}
	}

	/**
	 * 递归取得所有图面变量的值
	 * 
	 * @param variable
	 *            被引用的变量
	 * @param origVariable
	 *            需要计算的变量
	 * @param variableHasValMap
	 *            已有确定值得变量map
	 * @param variableNoValueMap
	 *            需要计算的变量map
	 * @param regionList
	 *            条件表数据
	 * @param searchedVariableList
	 *            已计算过的变量
	 * @param varLogList
	 *            变量计算日志
	 * @param errList
	 *            变量计算错误日志
	 * @param errVarList
	 *            计算发生错误的变量
	 * @param multiRegionList
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean recurseForGetVariableValue(String variable, ConfigCalculateVar configVariable, List<String> tempSearchedVariableList) throws ServiceRequestException
	{

		if (configVariable.getVariableHasValMap().containsKey(variable))
		{
			return true;
		}
		else if (configVariable.getErrVarList().contains(variable))
		{
			return false;
		}
		else if (!configVariable.getVariableNoValueMap().containsKey(variable))
		{
			String message = this.getMessage("ID_APP_CONFIG_PARAM_NO_VARIABLE3", variable);

			configVariable.addErrVar(variable);
			configVariable.addToVarLogList(message);
			return false;
		}

		List<String> searchedVariableList = new ArrayList<String>();
		if (!tempSearchedVariableList.contains(variable))
		{
			searchedVariableList.addAll(tempSearchedVariableList);
			searchedVariableList.add(variable);
		}
		else
		{
			String message = this.getMessage("ID_APP_CONFIG_PARAM_VARIABLE_CYCLE", variable, variable);
			configVariable.addErrVar(variable);
			configVariable.addToVarLogList(message);
			return false;
		}

		boolean result = true;

		// 计算L变量
		if (variable.endsWith("G"))
		{
			return this.recurseForGetGVariableValue(variable, configVariable, searchedVariableList);
		}
		else if (variable.endsWith("L"))
		{
			return this.recurseForGetLVariableValue(variable, configVariable, searchedVariableList);
		}
		else if (variable.endsWith("M"))
		{
			return this.recurseForGetMVariableValue(variable, configVariable, searchedVariableList);
		}
		else if (variable.endsWith("F"))
		{
			return this.recurseForGetFVariableValue(variable, configVariable, searchedVariableList);
		}
		else if (variable.endsWith("A") || variable.endsWith("B") || variable.endsWith("C") || variable.endsWith("D") || variable.endsWith("E"))
		{
			return this.recurseForGetAEVariableValue(variable, configVariable, searchedVariableList);
		}
		else if (variable.endsWith("R") || variable.endsWith("Q"))
		{
			return this.recurseForGetRQVariableValue(variable, configVariable, searchedVariableList);
		}
		return result;
	}

	private boolean recurseForGetGVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{
		boolean result = true;
		String quoteVariable = (String) configVariable.getVariableNoValueMap().get(variable);
		result = this.recurseForGetVariableValue(quoteVariable, configVariable, searchedVariableList);
		if (result)
		{
			configVariable.getVariableHasValMap().put(variable, configVariable.getVariableHasValMap().get(quoteVariable));

			configVariable.addToVarLogList(variable + "=" + configVariable.getVariableHasValMap().get(quoteVariable));
		}
		else
		{
			configVariable.addErrVar(variable);
		}
		return result;
	}

	private boolean recurseForGetLVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{
		boolean result = true;
		String quoteVariable = (String) configVariable.getVariableNoValueMap().get(variable);
		result = this.recurseForGetVariableValue(quoteVariable, configVariable, searchedVariableList);
		if (result)
		{
			configVariable.getVariableHasValMap().put(variable, configVariable.getVariableHasValMap().get(quoteVariable));

			configVariable.addToVarLogList(variable + "=" + configVariable.getVariableHasValMap().get(quoteVariable));
		}
		else
		{
			configVariable.addErrVar(variable);
		}
		return result;
	}

	private boolean recurseForGetFVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		boolean result = true;
		TableOfExpression expression = (TableOfExpression) configVariable.getVariableNoValueMap().get(variable);
		String formula = expression.getFormula();
		String variablesOfFormula = expression.getVariableInFormula();

		Map<String, String> variableMap = new HashMap<String, String>();
		String[] tmpArr = new String[] { variablesOfFormula };

		if (variablesOfFormula.contains(","))
		{
			tmpArr = variablesOfFormula.split(",");
		}

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(variable + "=" + formula + ";" + this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_F_INPT_VAR") + ":");

		StringBuffer tmpBuffer = new StringBuffer();
		for (String variable_ : tmpArr)
		{
			if (tmpBuffer.length() > 0)
			{
				tmpBuffer.append(",");
			}
			String value = this.getVariableValue(variable_, configVariable, searchedVariableList);
			variableMap.put(variable_, value);
			tmpBuffer.append(variable_ + "=" + value);
		}
		logBuffer.append(tmpBuffer.toString());
		logBuffer.append(";");

		String function = this.getRealFunction(formula, variableMap, expression.getDrawvariable(), variable, configVariable);

		String sql = "select " + function + " val from dual";
		try
		{
			List<String> tmpList = sds.executeQueryBySql(sql);
			configVariable.getVariableHasValMap().put(variable, tmpList.get(0));
			logBuffer.append(this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_F_RESULT") + ":").append(variable + "=" + tmpList.get(0));
			configVariable.addToVarLogList(variable + "=" + tmpList.get(0) + "(" + logBuffer.toString() + ")");
		}
		catch (Exception e)
		{
			String message = this.getMessage("ID_APP_CONFIG_PARAM_FUNCTION_ERROR2", expression.getDrawvariable());
			configVariable.addToVarLogList(message + "(" + logBuffer.toString() + this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_F_ERR_INFO") + ")");
			configVariable.addErrVar(variable);

			return false;
		}
		return result;
	}

	private boolean recurseForGetAEVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{
		String tableType = variable.substring(variable.length() - 1);
		List<TableOfRegion> regionList = configVariable.getVarRegionList(variable);
		if (SetUtils.isNullList(regionList))
		{
			String title = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_" + tableType);
			configVariable.addToVarLogList(this.getMessage("ID_APP_CONFIG_PARAM_NO_VARIABLE", variable, title));
			configVariable.addErrVar(variable);
			return false;
		}

		List<TableOfRegion> matchList = new ArrayList<TableOfRegion>();
		for (TableOfRegion region : regionList)
		{
			boolean isMatch = true;
			if (region.get("ISMATCH") == null)
			{
				String variable1 = region.getVariable1();

				String value1 = this.getVariableValue(variable1, configVariable, searchedVariableList);

				if (StringUtils.isNullString(value1))
				{
					isMatch = false;
				}
				else
				{
					// 上下限优先当做变量使用，如果变量中没有，则直接比较值
					String lowerLimit1 = region.getLowerLimit1();
					String lower1 = this.getVariableValue(lowerLimit1, configVariable, searchedVariableList);

					if (StringUtils.isNullString(lower1))
					{
						isMatch = false;
					}
					else
					{
						String upperLimit1 = region.getUpperLimit1();
						String upper1 = this.getVariableValue(upperLimit1, configVariable, searchedVariableList);

						if (StringUtils.isNullString(upper1))
						{
							isMatch = false;
						}
						else
						{
							if (NumberUtils.isNumeric(value1) && NumberUtils.isNumeric(lower1) && NumberUtils.isNumeric(upper1))
							{
								if (new BigDecimal(value1).doubleValue() >= new BigDecimal(lower1).doubleValue()
										&& new BigDecimal(value1).doubleValue() < new BigDecimal(upper1).doubleValue())
								{

								}
								else
								{
									isMatch = false;
								}
							}
							else
							{
								if (value1.compareTo(lower1) >= 0 && value1.compareTo(upper1) < 0)
								{
								}
								else
								{
									isMatch = false;
								}
							}
						}
					}
				}

				region.put("ISMATCH", isMatch);
			}
			else
			{
				isMatch = BooleanUtils.getBoolean((Boolean) region.get("ISMATCH"), false);
			}
			if (isMatch)
			{
				matchList.add(region);
			}
		}
		return processMatchRegionList(variable, configVariable, searchedVariableList, tableType, matchList);
	}

	private boolean processMatchRegionList(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList, String tableType, List<TableOfRegion> matchList)
			throws ServiceRequestException
	{
		boolean result = true;
		if (matchList.size() == 1)
		{
			String title = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_" + tableType);
			String message = this.getMessage("ID_APP_CONFIG_TABLE_REGION_FIND", title, matchList.get(0).getSequence());
			configVariable.addToVarLogList(message);
			String valueVar = matchList.get(0).getVarValue(variable);
			String value = this.getVariableValue(valueVar, configVariable, searchedVariableList);
			if (configVariable.getErrVarList().contains(valueVar))
			{
				configVariable.addErrVar(variable);
			}
			else
			{
				configVariable.getVariableHasValMap().put(variable, value);
				configVariable.addToVarLogList(variable + "=" + value);
			}
		}
		else if (matchList.size() > 1)
		{
			configVariable.addToVarLogList(this.getMessage("ID_APP_CONFIG_PARAM_MULTY_DATA_FOUND", variable));
			configVariable.getErrVarList().add(variable);
		}
		else
		{
			configVariable.getVariableHasValMap().put(variable, null);
			configVariable.addToVarLogList(variable + "= null");
		}
		if (configVariable.getErrVarList().contains(variable))
		{
			return false;
		}
		return result;
	}

	private boolean recurseForGetRQVariableValue(String variable, ConfigCalculateVar configVariable, List<String> searchedVariableList) throws ServiceRequestException
	{
		String tableType = variable.endsWith("R") ? "R" : "Q";
		List<TableOfRegion> regionList = configVariable.getVarRegionList(variable);
		if (SetUtils.isNullList(regionList))
		{
			String title = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_" + tableType);
			configVariable.addToVarLogList(this.getMessage("ID_APP_CONFIG_PARAM_NO_VARIABLE", variable, title));
			configVariable.addErrVar(variable);
			return false;
		}

		List<TableOfRegion> matchList = new ArrayList<TableOfRegion>();
		for (TableOfRegion region : regionList)
		{
			boolean isMatch = true;
			if (region.get("ISMATCH") == null)
			{
				String variable1 = region.getVariable1();
				String value1 = this.getVariableValue(variable1, configVariable, searchedVariableList);
				if (StringUtils.isNullString(value1))
				{
					isMatch = false;
				}
				else
				{
					// 上下限优先当做变量使用，如果变量中没有，则直接比较值
					String lowerLimit1 = region.getLowerLimit1();
					String lower1 = this.getVariableValue(lowerLimit1, configVariable, searchedVariableList);

					if (StringUtils.isNullString(lower1))
					{
						isMatch = false;
					}
					else
					{
						String upperLimit1 = region.getUpperLimit1();
						String upper1 = this.getVariableValue(upperLimit1, configVariable, searchedVariableList);

						if (StringUtils.isNullString(upper1))
						{
							isMatch = false;
						}
						else
						{
							String variable2 = region.getVariable2();
							String value2 = this.getVariableValue(variable2, configVariable, searchedVariableList);

							if (StringUtils.isNullString(value2))
							{
								isMatch = false;
							}
							else
							{
								// 上下限优先当做变量使用，如果变量中没有，则直接比较值
								String lowerLimit2 = region.getLowerLimit2();
								String lower2 = this.getVariableValue(lowerLimit2, configVariable, searchedVariableList);

								if (StringUtils.isNullString(lower2))
								{
									isMatch = false;
								}
								else
								{
									String upperLimit2 = region.getUpperLimit2();
									String upper2 = this.getVariableValue(upperLimit2, configVariable, searchedVariableList);

									if (StringUtils.isNullString(upper2))
									{
										isMatch = false;
									}
									else
									{
										if (NumberUtils.isNumeric(value1) && NumberUtils.isNumeric(lower1) && NumberUtils.isNumeric(upper1) && NumberUtils.isNumeric(value2)
												&& NumberUtils.isNumeric(lower2) && NumberUtils.isNumeric(upper2))
										{
											if (new BigDecimal(value1).doubleValue() >= new BigDecimal(lower1).doubleValue()
													&& new BigDecimal(value1).doubleValue() < new BigDecimal(upper1).doubleValue()
													&& new BigDecimal(value2).doubleValue() >= new BigDecimal(lower2).doubleValue()
													&& new BigDecimal(value2).doubleValue() < new BigDecimal(upper2).doubleValue())
											{

											}
											else
											{
												isMatch = false;
											}
										}
										else
										{
											if (value1.compareTo(lower1) >= 0 && value1.compareTo(upper1) < 0 && value2.compareTo(lower2) >= 0 && value2.compareTo(upper2) < 0)
											{

											}
											else
											{
												isMatch = false;
											}
										}
									}
								}
							}
						}
					}
				}
				region.put("ISMATCH", isMatch);
			}
			else
			{
				isMatch = BooleanUtils.getBoolean((Boolean) region.get("ISMATCH"), false);
			}
			if (isMatch)
			{
				matchList.add(region);
			}
		}

		return processMatchRegionList(variable, configVariable, searchedVariableList, tableType, matchList);
	}

	/**
	 * 解析公式，并替换变量
	 * 
	 * @param formula
	 *            公式（公式中支持的运算符号："(", ")", ",", "+", "-", "*", "/", "||"
	 * @param variableMap
	 *            公式中使用的变量，以及变量的值
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getRealFunction(String formula, Map<String, String> variableMap, String varOfF, String origVar, ConfigCalculateVar configVariable) throws ServiceRequestException
	{
		List<String> operatorList = this.getDefaultOperatorOfFormul();

		// 公式不可能出现单引号嵌套，且单引号不可能出现在公式首位，所以，根据单引号分割字符串，奇数下标为字符串内的内容，不处理
		// 组成公式的字符串，单引号之外的部分，不可能包含空格
		// 奇数下标的内容，遇运算符号就两边加空格替换
		// 公式中包含||符号，则中间不能加空格
		StringBuffer formulBuffer = new StringBuffer();
		String[] tmpArr = formula.split("'");

		for (int i = 0; i < tmpArr.length; i++)
		{
			StringBuffer tmpBuffer = new StringBuffer();
			tmpBuffer.append(" ");
			if (i % 2 == 0)
			{
				char[] charArr = tmpArr[i].toCharArray();
				for (int j = 0; j < charArr.length; j++)
				{
					if (operatorList.contains(String.valueOf(charArr[j])))
					{
						tmpBuffer.append(" ").append(String.valueOf(charArr[j])).append(" ");
					}
					else if ('|' == charArr[j] && j < tmpArr.length - 1 && '|' == charArr[j + 1])
					{
						tmpBuffer.append(" ").append("||").append(" ");
						j++;
					}
					else
					{
						tmpBuffer.append(charArr[j]);
					}
				}
				tmpBuffer.append(" ");

				boolean isOnlyNumberVal = true;
				for (String variable : variableMap.keySet())
				{
					String value = variableMap.get(variable);
					if (!StringUtils.isNullString(value))
					{
						if (!NumberUtils.isNumeric(value))
						{
							isOnlyNumberVal = false;
							break;
						}
					}
				}

				String finalFormula = tmpBuffer.toString();
				for (String variable : variableMap.keySet())
				{
					String value = variableMap.get(variable);
					if (StringUtils.isNullString(value))
					{
						if (isOnlyNumberVal)
						{
							value = "0";
						}
						else
						{
							configVariable.addToVarLogList(this.getMessage("ID_APP_CONFIG_PARAM_NO_VARIABLE", origVar, variable));
							break;
						}
					}

					if (isOnlyNumberVal)
					{
						finalFormula = finalFormula.replaceAll(" " + variable + " ", value);
					}
					else
					{
						if (value.indexOf("$") != -1)
						{
							value = value.replace("$", "\\$");
						}
						finalFormula = finalFormula.replaceAll(" " + variable + " ", "'" + value + "'");
					}
				}
				formulBuffer.append(finalFormula);
			}
			else
			{
				formulBuffer.append("'").append(tmpArr[i]).append("'");
			}
		}

		return formulBuffer.toString().length() == 0 ? formula : formulBuffer.toString();
	}

	/**
	 * 定义公式中允许使用的操作符
	 * 
	 * @return
	 */
	private List<String> getDefaultOperatorOfFormul()
	{
		List<String> list = new ArrayList<String>();
		list.add("(");
		list.add(")");
		list.add(",");
		list.add("+");
		list.add("-");
		list.add("*");
		list.add("/");

		return list;
	}

	/**
	 * 取得L00番号的图面变量值
	 * 
	 * @param list
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getDefaultL00Column(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		TableOfList defaultL00Number = this.stubService.getDefaultL00Number(objectGuid, ruleTime);
		Map<String, String> resultMap = new HashMap<String, String>();
		if (defaultL00Number != null)
		{
			List<DynamicOfColumn> columns = defaultL00Number.getColumns();
			if (!SetUtils.isNullList(columns))
			{
				for (DynamicOfColumn column : columns)
				{
					resultMap.put(column.getName(), column.getValue());
				}
			}
		}
		return resultMap;
	}

	private String getMessage(String id, Object... agrs) throws ServiceRequestException
	{
		return this.stubService.getMSRM().getMSRString(id, this.stubService.getUserSignature().getLanguageEnum().getId(), agrs);
	}

}
