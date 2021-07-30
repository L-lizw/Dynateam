package dyna.common.bean.model.code;

import java.util.Comparator;

public class CodeItemSeqCompare implements Comparator<CodeItem>
{

	@Override
	public int compare(CodeItem o1, CodeItem o2)
	{
		if (o1 == null)
		{
			return o2 == null ? 0 : -1;
		}
		if (o2 == null)
		{
			return 1;
		}
		return o1.getSequence() - o2.getSequence();
	}

}
