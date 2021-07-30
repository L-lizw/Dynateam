package dyna.common.bean.data.ppms.indicator.function;

import java.util.List;

import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;

public abstract class AbstractFunction
{
	private List<IndicatorAnalysisVal>	list;

	public AbstractFunction(List<IndicatorAnalysisVal> list)
	{
		this.list = list;
	}

	public abstract Double calculate();

	public List<IndicatorAnalysisVal> getList()
	{
		return list;
	}
}
