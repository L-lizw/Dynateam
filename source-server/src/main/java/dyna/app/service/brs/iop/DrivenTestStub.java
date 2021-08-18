package dyna.app.service.brs.iop;

import dyna.app.service.AbstractServiceStub;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.configparamter.DrivenResult;
import dyna.common.bean.data.iopconfigparamter.IOPColumnValue;
import dyna.common.bean.data.iopconfigparamter.IOPConfigConstant;
import dyna.common.bean.data.iopconfigparamter.IOPConfigParameter;
import dyna.common.dto.DataRule;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;
import parsii.tokenizer.ParseException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Component
public class DrivenTestStub extends AbstractServiceStub<IOPImpl>
{

	public DrivenResult drivenTest(ObjectGuid objectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule, String codeValue,
			boolean isAppend) throws ServiceRequestException
	{
		FoundationObject end1 = this.stubService.getBOAS().getObject(objectGuid);
		if (end1 == null)	
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "data is not exist, guid='" + objectGuid.getGuid() + "'", null, objectGuid.getGuid());
		}
		// 取得材料明细
		List<StructureObject> end2List = this.stubService.getBOAS().listObjectOfRelation(objectGuid, IOPConfigConstant.IOPMaterialDetail, searchCondition, end2SearchCondition,
				dataRule);
		if (SetUtils.isNullList(end2List))
		{
			return null;
		}
		List<StructureObject> returnStructure = new ArrayList<StructureObject>();
		// 取得所有配置表数据
		List<IOPConfigParameter> configVariable = this.stubService.listIOPConfigParameter(objectGuid.getMasterGuid(), dataRule.getLocateTime());
		StringBuffer errMgs = new StringBuffer();
		List<String> logMgs = new ArrayList<String>();
		Map<String, String> paramMap = this.checkPartNumber(end1, configVariable, codeValue, end2List, errMgs);
		if (!SetUtils.isNullMap(paramMap))
		{
			for (StructureObject structureObject : end2List)
			{
				this.calculateQuantityAndParNumber(structureObject, paramMap, errMgs, isAppend, returnStructure);
			}
			//所有被计算的值
			for (String key : paramMap.keySet())
			{
				logMgs.add(key + "=" + paramMap.get(key));
			}
		}
		else
		{
			returnStructure.addAll(end2List);
		}
		
		DrivenResult result = new DrivenResult();
		result.setStructureObjectList(returnStructure);
		logMgs.add(errMgs.toString());
		result.setErrMsg(errMgs.toString());
		result.setVarLogList(logMgs);
		return result;
	}

	/**
	 * 计算数量和件号
	 * 
	 * @param structureObject
	 * @param paramMap
	 * @param errMgs
	 * @param isAppend
	 * @param returnStructure
	 * @param errMgs
	 * @throws ServiceRequestException
	 */
	private void calculateQuantityAndParNumber(StructureObject structureObject, Map<String, String> paramMap, StringBuffer errMgs, boolean isAppend, List<StructureObject> returnStructure)
			throws ServiceRequestException
	{
		Double[] fraction = new Double[2];
		String end2Name = structureObject.getEnd2UIObject() != null ? structureObject.getEnd2UIObject().getId() : "";
		String quantity = structureObject.get(IOPConfigConstant.QUANTITY) == null ? "" : (String) structureObject.get(IOPConfigConstant.QUANTITY);
		Double value = this.getQuantityOfStructure(quantity, paramMap, end2Name, errMgs, fraction);
		// 如果数量为0，则不显示，且子阶不参与计算
		if (value != 0)
		{
			structureObject.put(StructureObject.DRIVERTREEITEMKEY, StringUtils.generateRandomUID(32).toString());
			this.setUniqueNo(structureObject, isAppend);
			returnStructure.add(structureObject);
			if (fraction[0] != null && fraction[1] != null)
			{
				structureObject.put(IOPConfigConstant.QUANTITY, fraction[0]);
				structureObject.put(IOPConfigConstant.BASE, fraction[1]);
			}
			else if (value != -1)
			{
				structureObject.put(IOPConfigConstant.QUANTITY, value);
			}
			boolean isClientProvide = false;
			ObjectGuid end2 = structureObject.getEnd2ObjectGuid();
			FoundationObject end2Fo = this.stubService.getBOAS().getObject(end2);
			if (end2Fo != null)
			{
				Object clientProvide = end2Fo.get(IOPConfigConstant.ISCLIENTPROVIDEITEM);
				if (clientProvide != null && clientProvide.equals("Y"))
				{
					isClientProvide = true;
					structureObject.setEnd2ObjectGuid(end2Fo.getObjectGuid());
					structureObject.put(StructureObject.END2_UI_OBJECT, end2Fo);
				}
			}
			if (!isClientProvide)
			{
				// 计算件号
				String pn = (String) structureObject.get(IOPConfigConstant.PARTNUMBER);
				if (!StringUtils.isNullString(pn))
				{
					Map<String, String> partNumber = new HashMap<String, String>();
					partNumber.put(IOPConfigConstant.PARTNUMBER, pn);
					Map<String, String> varsMap = new HashMap<String, String>();
					Map<String, String> lettersMap = new HashMap<String, String>();
					this.getVariableValue(IOPConfigConstant.PARTNUMBER, partNumber, paramMap, varsMap, errMgs, lettersMap);
					if (paramMap.get(IOPConfigConstant.PARTNUMBER) == null && paramMap.get(pn) == null)
					{
						errMgs.append(this.getMessage("ID_APP_IOPCONFIG_EXPRESSION_CANNOT_EVALUATED", end2Name, pn) + ";");
					}
					else
					{
						structureObject.put(IOPConfigConstant.PARTNUMBER,
								paramMap.get(IOPConfigConstant.PARTNUMBER) == null ? paramMap.get(pn) : paramMap.get(IOPConfigConstant.PARTNUMBER));
					}
				}
			}
		}
	}

	private void setUniqueNo(StructureObject structureObject, boolean isAppend)
	{
		if (structureObject != null)
		{
			FoundationObject end2Foundation = (FoundationObject) structureObject.get(StructureObject.END2_UI_OBJECT);
			if (end2Foundation != null)
			{
				structureObject.put(IOPConfigConstant.UNIQUENO, end2Foundation.getId());
				if (isAppend)
				{
					structureObject.put(IOPConfigConstant.UNIQUENO,
							StringUtils.convertNULLtoString(end2Foundation.getId()) + StringUtils.convertNULLtoString(structureObject.get(IOPConfigConstant.PARTNUMBER)));
				}
			}
		}
	}

	/**
	 * 计算数量
	 * 
	 * @param quantity
	 * @param paramMap
	 * @param errMgs
	 * @param end2Name
	 * @return
	 * @throws ServiceRequestException
	 */
	private Double getQuantityOfStructure(String quantity, Map<String, String> paramMap, String end2Name, StringBuffer errMgs, Double[] fraction)
			throws ServiceRequestException
	{
		if (StringUtils.isNullString(quantity))
		{
			return 0.0;
		}
		double result = -1;
		String exp = quantity.trim();
		Scope scope = new Scope();
		Expression parsiiExpr = null;
		try
		{
			parsiiExpr = Parser.parse(exp, scope);
			Set<String> vars = scope.getLocalNames();
			if (vars != null && vars.size() > 0)
			{
				for (String name : vars)
				{
					Variable var = scope.getVariable(name);
					String value = paramMap.get(name);
					if (StringUtils.isNullString(value))
					{
						errMgs.append(this.getMessage("ID_APP_IOPCONFIG_EXPRESSION_PARAMETER_VALUE_NULL", end2Name, quantity, name) + ";");
						return result;
					}
					try
					{
						BigDecimal bv = new BigDecimal(value);
						var.setValue(bv.doubleValue());
					}
					catch (NumberFormatException e)
					{
						errMgs.append(this.getMessage("ID_APP_IOPCONFIG_EXPRESSION_PARAMETER_VALUE_ERROR", end2Name, name, value) + ";");
						return result;
					}
				}
			}
			result = parsiiExpr.evaluate();
			if (!parsiiExpr.isDivided())
			{
				fraction[0] = parsiiExpr.getNumerator();
				fraction[1]=parsiiExpr.getDenominator();
			}
		}
		catch (ArithmeticException e)
		{
			errMgs.append(this.getMessage("ID_APP_IOPCONFIG_EXPRESSION_EVALUATE_ERROR", end2Name, quantity) + ";");
		}
		catch (ParseException e)
		{
			errMgs.append(this.getMessage("ID_APP_IOPCONFIG_EXPRESSION_ANALYSIS_ERROR", end2Name, quantity) + ";");
		}
		return result;
	}

	private String getMessage(String id, Object... agrs) throws ServiceRequestException
	{
		return this.stubService.getMSRM().getMSRString(id, this.stubService.getUserSignature().getLanguageEnum().getId(), agrs);
	}
	
	/**
	 * 件号为变量时，是否存在选配表中
	 * 若存在，找出其对应的一组参数，若不存在抛出异常
	 * 
	 * @param end1
	 * @param configVariable
	 * @param end2List
	 * @param errMgs
	 * @throws ParseException
	 */
	private Map<String, String> checkPartNumber(FoundationObject end1, List<IOPConfigParameter> configVariable, String codeValue, List<StructureObject> end2List, StringBuffer errMgs)
			throws ServiceRequestException
	{
		Map<String,Map<String,String>> paramMap=new HashMap<String, Map<String,String>>();
		if (!StringUtils.isNullString(codeValue) && SetUtils.isNullList(configVariable))
		{
			throw new ServiceRequestException("ID_APP_IOPCONFIG_LID_NULL", "code value is not exist" + codeValue, null, codeValue);
		}
		else if (!StringUtils.isNullString(codeValue) && !SetUtils.isNullList(configVariable))
		{
			for (IOPConfigParameter param : configVariable)
			{
				List<IOPColumnValue> valueList = param.getValueList();
				Map<String, String> map = new HashMap<String, String>();
				String LID = null;
				for (IOPColumnValue value : valueList)
				{
					if (IOPConfigConstant.LID.equalsIgnoreCase(value.getColumnName()))
					{
						LID = value.getColumnValue();
					}
					else
					{
						map.put(value.getColumnName(), value.getColumnValue());
					}
				}
				if (!StringUtils.isNullString(LID))
				{
					paramMap.put(LID, map);
				}
			}
			Map<String, String> pm = paramMap.get(codeValue.trim());
			if (SetUtils.isNullMap(pm))
			{
				throw new ServiceRequestException("ID_APP_IOPCONFIG_LID_NULL", "code value is not exist" + codeValue, null, codeValue);
			}
			fixFractionValue(pm);
			Map<String, String> calculateVariable = this.calculateVariable(pm,errMgs);
			//将未计算出来的变量直接放入返回值
			this.putOtherVariable(calculateVariable,pm);
			return calculateVariable;
		}
		else if (StringUtils.isNullString(codeValue) && !SetUtils.isNullList(configVariable))
		{
			throw new ServiceRequestException("ID_APP_IOPCONFIG_PARTNUMBER_NULL", "partNumber " + end1 + " value is not exist", null, end1);
		}
		else
		{
			for (StructureObject structure : end2List)
			{
				Object quantity = structure.get(IOPConfigConstant.QUANTITY);
				String name = "";
				if (quantity == null)
				{
					errMgs.append(this.getMessage("ID_APP_IOPCONFIG_CHILD_QUANTITY_NULL", end1, name) + ";");
					// throw new ServiceRequestException("[{0}]子阶[{1}]数量位空", "end1 " + end1 + " the end2 " + name +
					// " quantity is null", null, end1, name);
				}
				boolean isDigital = this.checkDigital(quantity.toString());
				if (!isDigital)
				{
					errMgs.append(this.getMessage("ID_APP_IOPCONFIG_CHILD_QUANTITY_ERROR", end1, name, quantity.toString()) + ";");
					// throw new ServiceRequestException("[{0}]子阶[{1}]数量{[2]}错误", "end1 " + end1 + " the end2 " + name +
					// " quantity " + quantity.toString() + " is error", null, end1,
					// name, quantity.toString());
				}
			}
		}
		return null;
	}


	private void putOtherVariable(Map<String, String> calculateVariable, Map<String, String> pm)
	{
		if (!SetUtils.isNullMap(calculateVariable))
		{
			for (String name : pm.keySet())
			{
				if (calculateVariable.get(name) == null)
				{
					calculateVariable.put(name, pm.get(name));
				}
			}

		}
	}

	private void fixFractionValue(Map<String, String> pm)
	{
		for (String name : pm.keySet())
		{
			String variableValue = pm.get(name);
			if (variableValue.contains("{S"))
			{
				List<CellString> list = new ArrayList<CellString>();
				this.getSepecialTextOfValue(variableValue, list);
				if (!SetUtils.isNullList(list))
				{
					if (list.size() == 1)
					{
						CellString cellsValue = list.get(0);
						if (cellsValue.getSymbol() != null && cellsValue.getSymbol().name().equalsIgnoreCase(CellSymbol.FRACTION.name()))
						{
							pm.put(name, cellsValue.getFirstValue() + "/" + cellsValue.getSecondValue());
						}
					}
				}
			}
		}
	}

	/**
	 * 计算变量
	 * 
	 * @param pm
	 * @param errMgs
	 * @return
	 * @throws ParseException
	 * @throws ServiceRequestException
	 */
	private Map<String, String> calculateVariable(Map<String, String> pm, StringBuffer errMgs) throws ServiceRequestException
	{
		if (!SetUtils.isNullMap(pm))
		{
			Map<String, String> values = new HashMap<String, String>();
			Map<String, String> varsMap = new HashMap<String, String>();
			Map<String, String> lettersMap = new HashMap<String, String>();
			for (String name : pm.keySet())
			{
				if (values.get(name) == null && !varsMap.containsKey(name))
				{
					int loop = this.getVariableValue(name, pm, values, varsMap, errMgs, lettersMap);
					if (loop == -1)
					{
						break;
					}
				}
			}
			return values;
		}
		return null;
	}

	private int getVariableValue(String variable, Map<String, String> pm, Map<String, String> values, Map<String, String> varsMap, StringBuffer errMgs,
			Map<String, String> lettersMap) throws ServiceRequestException
	{
		Scope scope = new Scope();
		String v1 = pm.get(variable);
		if (v1 == null)
		{
			lettersMap.put(variable, variable);
		}
		else
		{
			if (varsMap.get(variable) == null)
			{
				varsMap.put(variable, variable);
			}
			else
			{
				if (values.get(variable) == null)
				{
					errMgs.append(this.getMessage("ID_APP_IOPCONFIG_VARIABLE_EXPRESSION_LOOP_ERROR", variable, v1) + ";");
					return -1;
					// throw new ServiceRequestException("参数[{0}]表达式[{1}]异常，死循环", "paramater " + variable +
					// " expression " + v1 + " is error", null, variable, v1);
				}
			}
			if (v1.contains("+") || v1.contains("-") || v1.contains("*") || v1.contains("/") || v1.contains("%"))
			{
				try
				{
					Expression parsiiExpr1 = Parser.parse(v1, scope);
					Set<String> vars = scope.getLocalNames();
					if (vars != null && vars.size() > 0)
					{
						for (String var_1 : vars)
						{
							if (values.get(var_1) == null)
							{
								int loop = getVariableValue(var_1, pm, values, varsMap, errMgs, lettersMap);
								if (loop == -1)
								{
									return -1;
								}
							}
						}
						Map<String, String> letterMap = new HashMap<String, String>();
						for (String var_1 : vars)
						{
							String vv = values.get(var_1);
							if (vv != null)
							{
								boolean isDigital = checkDigital(vv);
								if (!isDigital)
								{
									letterMap.put(var_1, vv);
								}
								else
								{
									Variable v = scope.getVariable(var_1);
									v.setValue(Double.parseDouble(values.get(var_1)));
								}
							}
							else
							{
								if (lettersMap.get(var_1) != null)
								{
									letterMap.put(var_1, lettersMap.get(var_1));
								}
							}

						}
						String _value = null;
						if (letterMap.size() > 0)
						{
							String ex = pm.get(variable);
							_value = this.getLetterValue(variable, ex, letterMap, values);
							values.put(variable, _value);
						}
						else
						{
							_value = Double.toString(parsiiExpr1.evaluate());
							values.put(variable, _value);
						}
					}
					else
					{
						String _value = Double.toString(parsiiExpr1.evaluate());
						values.put(variable, _value);
					}
				}
				catch (ArithmeticException e)
				{
					errMgs.append(this.getMessage("ID_APP_IOPCONFIG_VARIABLE_EXPRESSION_EVALUATED_ERROR", variable, v1) + ";");
					return 0;
					// throw new ServiceRequestException("参数[{0}]表达式[{1}]算术异常", "paramater " + variable + " expression "
					// + v1 + " is error", null, variable, v1);
				}
				catch (ParseException e)
				{
					errMgs.append(this.getMessage("ID_APP_IOPCONFIG_VARIABLE_EXPRESSION_ERROR", variable, v1) + ";");
					return 0;
					// throw new ServiceRequestException("参数[{0}]表达式[{1}]异常", "paramater " + variable + " expression " +
					// v1 + " is error", null, variable, v1);
				}
			}
			else
			{
				boolean isDigital = checkDigital(v1);
				if (isDigital)
				{
					values.put(variable, v1);
				}
				else
				{
					if (values.get(v1) == null)
					{
						int loop = getVariableValue(v1, pm, values, varsMap, errMgs, lettersMap);
						if (loop == -1)
						{
							return -1;
						}
						if (lettersMap.get(v1) != null)
						{
							String ex = pm.get(variable);
							String _value = this.getLetterValue(variable, ex, lettersMap, values);
							values.put(variable, _value);
						}
					}
				}
			}
		}
		return 0;
	}

	protected void getSepecialTextOfValue(String text, List<CellString> listCent)
	{
		String cellStr = text.trim();
		if (!cellStr.contains("{") && !cellStr.contains("}"))
		{
			listCent.add(new CellString(cellStr));
		}
		else
		{
			int strIndex = 0;
			char[] tempStr = cellStr.toCharArray();
			for (int i = strIndex; i < cellStr.length(); i++)
			{
				char tmpChar = tempStr[i];
				if (tmpChar == '{')
				{
					boolean isStack = false;
					int right = cellStr.indexOf('}', i);
					if (right == -1)
						continue;
					int ctrlMark = cellStr.indexOf('S', i);
					if (ctrlMark < i || ctrlMark > right)
						continue;
					int seperate = -1;
					String strfirst;
					String strsecond;
					CellSymbol stackType = CellSymbol.FRACTION;
					seperate = cellStr.indexOf('^', i);
					if (seperate >= ctrlMark + 1 && seperate < right)
					{
						if (seperate == ctrlMark + 1 && seperate == right - 1)
							isStack = false;
						else
						{
							stackType = CellSymbol.TOLERANCE;
							isStack = true;
						}
					}
					else
					{
						seperate = cellStr.indexOf('#', i);
						if (seperate >= ctrlMark + 1 && seperate < right)
						{
							if (seperate == ctrlMark + 1 && seperate == right - 1)
								isStack = false;
							else
							{
								stackType = CellSymbol.FRACTION;
								isStack = true;
							}
						}
					}
					if (isStack)
					{
						strfirst = cellStr.substring(ctrlMark + 1, seperate);
						strsecond = cellStr.substring(seperate + 1, right);
						if (!StringUtils.isNullString(strfirst) || !StringUtils.isNullString(strsecond))
						{
							if (i > strIndex)
							{
								String strTmp = cellStr.substring(strIndex, i);
								listCent.add(new CellString(strTmp));
							}

							CellString cell = new CellString();
							cell.setSymbol(stackType);
							cell.setFirstValue(strfirst);
							cell.setSecondValue(strsecond);
							listCent.add(cell);
						}
						i = right;
						strIndex = i + 1;
					}
				}
			}
			if (strIndex <= (cellStr.length() - 1))
			{
				String strTmp = cellStr.substring(strIndex, cellStr.length());
				listCent.add(new CellString(strTmp));
			}
		}
	}

	private String getLetterValue(String variable, String ex, Map<String, String> letterMap, Map<String, String> values) throws ServiceRequestException
	{
		if (ex != null)
		{
			String[] _exs = ex.split("\\+");
			if (_exs != null && _exs.length > 0)
			{
				for (int i = 0; i < _exs.length; i++)
				{
					String _var = _exs[i];
					if (_var.contains("(") || _var.contains(")"))
					{
						_var = _var.replace("(", "");
						_var = _var.replace(")", "");
					}
					if (values.get(_var) != null)
					{
						_exs[i] = _exs[i].replace(_var, values.get(_var));
					}
					else if (letterMap.get(_var) != null)
					{
						_exs[i] = _exs[i].replace(_var, letterMap.get(_var));
					}
				}
				StringBuffer append = new StringBuffer();
				for (int i = 0; i < _exs.length; i++)
				{
					append.append(_exs[i]);
				}
				return append.toString().replace("+", "");
			}
		}
		return null;
	}

	private boolean checkDigital(String vv)
	{
		String reg = "^[+-]?\\d+(\\.\\d+)?$";
		if (vv.contains("/"))
		{
			vv = vv.replaceAll("/", "");
		}
		return vv.matches(reg);
	}

}

class CellString extends SystemObjectImpl implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private String				value				= null;
	private CellSymbol			symbol				= null;
	private String				firstValue			= "";
	private String				secondValue			= "";

	public CellString()
	{
		// TODO Auto-generated constructor stub
	}

	public CellString(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setSymbol(CellSymbol symbol)
	{
		this.symbol = symbol;
	}

	public CellSymbol getSymbol()
	{
		return symbol;
	}

	public void setFirstValue(String firstValue)
	{
		this.firstValue = firstValue;
	}

	public String getFirstValue()
	{
		return firstValue;
	}

	public void setSecondValue(String secondValue)
	{
		this.secondValue = secondValue;
	}

	public String getSecondValue()
	{
		return secondValue;
	}

}
enum CellSymbol
{
	FRACTION, // 分数(#)
	TOLERANCE;// 公差(^)
}
