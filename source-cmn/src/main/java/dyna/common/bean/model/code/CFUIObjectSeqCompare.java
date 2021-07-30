package dyna.common.bean.model.code;

import java.util.Comparator;

import dyna.common.bean.model.ui.ClassificationUIObject;

public class CFUIObjectSeqCompare implements Comparator<ClassificationUIObject>
{

	@Override
	public int compare(ClassificationUIObject o1, ClassificationUIObject o2)
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
