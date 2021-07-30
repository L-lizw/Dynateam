package dyna.common.bean.data.ppms.indicator.function;

import java.math.BigDecimal;
import java.util.List;

import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;
import dyna.common.util.SetUtils;

public class DividedFunction extends AbstractFunction
{
	public DividedFunction(List<IndicatorAnalysisVal> list)
	{
		super(list);
	}

	@Override
	public Double calculate()
	{
		if (SetUtils.isNullList(this.getList()) || this.getList().size() != 2)
		{
			return 0d;
		}

		IndicatorAnalysisVal n = this.getList().get(0);
		IndicatorAnalysisVal d = this.getList().get(1);
		if (n == null || d == null || n.getResult() == 0 || d.getResult() == 0)
		{
			return 0d;
		}
		return new BigDecimal(String.valueOf(n.getResult())).divide(new BigDecimal(String.valueOf(d.getResult())), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
