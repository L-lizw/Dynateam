package dyna.app.service.brs.srs;

import java.util.Comparator;

public class DataComparetor implements Comparator<String[]>
{

	@Override
	public int compare(String[] o1, String[] o2)
	{
		return o1[0].compareTo(o2[0]);
	}

}
