package dyna.common.bean.model.ui;

import java.util.Comparator;

import dyna.common.dto.model.ui.UIField;

public class UIFieldSeqCompare implements Comparator<UIField>
{

	@Override
	public int compare(UIField o1, UIField o2)
	{
		if (o1 == null)
		{
			return o2 == null ? 0 : -1;
		}
		if (o2 == null)
		{
			return 1;
		}
		if (o1.getRowIndex() == o2.getRowAmount())
		{
			return o1.getColumnAmount() - o2.getColumnAmount();
		}
		else
		{
			return o1.getRowAmount() - o2.getRowAmount();
		}
	}

}
