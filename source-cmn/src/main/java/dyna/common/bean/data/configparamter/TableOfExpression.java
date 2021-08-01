package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.configparamter.TableOfExpressionMapper;

import java.util.Map;

/**
 * 公式表(F表)
 * 
 * @author wwx
 * 
 */
@EntryMapper(TableOfExpressionMapper.class)
public class TableOfExpression extends ConfigTableBase implements SystemObject
{
	private static final long	serialVersionUID	= 198901519599475733L;
	public static final String	FNUMBER				= "FNUMBER";
	public static final String	DRAWVARIABLE		= "DRAWVARIABLE";
	public static final String	FORMULA				= "FORMULA";
	public static final String	VARIABLEINFORMULA	= "VARIABLEINFORMULA";

	public String getFnumber()
	{
		return (String) this.get(FNUMBER);
	}

	public void setFnumber(String fnumber)
	{
		this.put(FNUMBER, fnumber);
	}

	public String getFormula()
	{
		return (String) this.get(FORMULA);
	}

	public void setFormula(String formula)
	{
		this.put(FORMULA, formula);
	}

	public String getDrawvariable()
	{
		return (String) this.get(DRAWVARIABLE);
	}

	public void setDrawvariable(String drawariable)
	{
		this.put(DRAWVARIABLE, drawariable);
	}

	public String getVariableInFormula()
	{
		return (String) this.get(VARIABLEINFORMULA);
	}

	public void setVariableInFormula(String variableinformula)
	{
		this.put(VARIABLEINFORMULA, variableinformula);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfExpression result = new TableOfExpression();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}
}
