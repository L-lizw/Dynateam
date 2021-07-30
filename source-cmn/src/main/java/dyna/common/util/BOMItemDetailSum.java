package dyna.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.structure.BOMStructure;

public class BOMItemDetailSum implements Comparator<BOMStructure>
{
	private Map<String, List<BOMStructure>>	bomTreeMap			= new HashMap<String, List<BOMStructure>>();
	private Map<String, List<BOMStructure>>	end2Map				= new HashMap<String, List<BOMStructure>>();
	private String							TEMP_QUANTITY_FIELD	= "TEMP_QUANTITY";
	private String							rootGuid			= "";

	private List<BOMStructure>				bomLists			= new ArrayList<BOMStructure>();

	public BOMItemDetailSum(String rootGuid, Map<String, List<BOMStructure>> bomTreeMap)
	{
		this.rootGuid = rootGuid;
		this.bomTreeMap = bomTreeMap;
	}

	public void prepareTreeMap()
	{
		for (List<BOMStructure> list : bomTreeMap.values())
		{
			if (!SetUtils.isNullList(list))
			{
				for (BOMStructure bomstru : list)
				{
					if (!end2Map.containsKey(bomstru.getEnd2ObjectGuid().getGuid() + "$" + bomstru.getUOM()))
					{
						end2Map.put(bomstru.getEnd2ObjectGuid().getGuid() + "$" + bomstru.getUOM(), new ArrayList<BOMStructure>());
					}
					end2Map.get(bomstru.getEnd2ObjectGuid().getGuid() + "$" + bomstru.getUOM()).add(bomstru);
					bomstru.put(TEMP_QUANTITY_FIELD, new BigDecimal("0"));
				}

			}
		}
	}

	public void calculateTree()
	{
		calculateTree(rootGuid, new BigDecimal("1"));
	}

	private void calculateTree(String productGuid, BigDecimal count)
	{
		List<BOMStructure> bomChildList = bomTreeMap.get(productGuid);
		if (!SetUtils.isNullList(bomChildList))
		{
			Collections.sort(bomChildList, this);
			for (BOMStructure bomstru : bomChildList)
			{
				BigDecimal childcount = (BigDecimal) bomstru.get(TEMP_QUANTITY_FIELD);
				BigDecimal quantity = new BigDecimal(bomstru.getQuantity() == null ? "0" : bomstru.getQuantity().toString());
				childcount = quantity.multiply(count).add(childcount);
				bomstru.put(TEMP_QUANTITY_FIELD, childcount);
				bomLists.add(bomstru);
				calculateTree(bomstru.getEnd2ObjectGuid().getGuid(), quantity.multiply(count));
			}
		}
	}

	public List<BOMStructure> getSummaryList()
	{
		List<BOMStructure> bomRetList = new ArrayList<BOMStructure>();
		for (BOMStructure bomstru : bomLists)
		{
			if (end2Map.containsKey(bomstru.getEnd2ObjectGuid().getGuid() + "$" + bomstru.getUOM()))
			{
				List<BOMStructure> bomChildList = end2Map.get(bomstru.getEnd2ObjectGuid().getGuid() + "$" + bomstru.getUOM());
				BOMStructure retBomstru = bomChildList.get(0);
				if (bomChildList.size() > 1)
				{
					BigDecimal childcount = (BigDecimal) retBomstru.get(TEMP_QUANTITY_FIELD);
					for (int i = 1; i < bomChildList.size(); i++)
					{
						childcount = childcount.add((BigDecimal) bomChildList.get(i).get(TEMP_QUANTITY_FIELD));
					}
					retBomstru.put(TEMP_QUANTITY_FIELD, childcount);
				}

				bomRetList.add(retBomstru);
				retBomstru.setQuantity(((BigDecimal) retBomstru.get(TEMP_QUANTITY_FIELD)).doubleValue());
				retBomstru.getObjectGuid().setGuid(null);
				retBomstru.setGuid(null);
				end2Map.remove(bomstru.getEnd2ObjectGuid().getGuid() + "$" + bomstru.getUOM());
			}
		}
		return bomRetList;
	}

	@Override
	public int compare(BOMStructure o1, BOMStructure o2)
	{
		if (o1 == null || StringUtils.isNullString(o1.getSequence()))
		{
			return -1;
		}
		if (o2 == null || StringUtils.isNullString(o2.getSequence()))
		{
			return 1;
		}
		return o1.getSequence().compareToIgnoreCase(o2.getSequence());
	}
}
