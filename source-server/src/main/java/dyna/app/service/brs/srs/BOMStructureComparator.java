package dyna.app.service.brs.srs;

import java.util.Comparator;

import dyna.common.bean.data.structure.BOMStructure;

public class BOMStructureComparator implements Comparator<BOMStructure>
{

	@Override
	public int compare(BOMStructure o1, BOMStructure o2)
	{
		if (o1.get("END1.GUID$").toString().compareTo(o2.get("END1.GUID$").toString()) > 0)
		{
			return 1;
		}
		else if (o1.get("END1.GUID$").toString().compareTo(o2.get("END1.GUID$").toString()) == 0)
		{
			if (o1.get("END2.GUID$").toString().compareTo(o2.get("END2.GUID$").toString()) > 0)
			{
				return 1;
			}
			else if (o1.get("END2.GUID$").toString().compareTo(o2.get("END2.GUID$").toString()) < 0)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return -1;
		}
	}

}
