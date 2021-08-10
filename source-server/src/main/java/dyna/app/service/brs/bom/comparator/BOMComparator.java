/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMComparator
 * caogc 2011-3-31
 */
package dyna.app.service.brs.bom.comparator;

import java.util.Comparator;

import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.template.bom.BOMCompare;
import dyna.common.util.StringUtils;

/**
 * 将BOM比较出来的结果按照顺序号递增的顺序排序
 * 
 * @author caogc
 * 
 */
public class BOMComparator implements Comparator<BOMCompare>
{
	public int compare(BOMCompare bomStructureFirst, BOMCompare bomStructureSecond)
	{
		BOMCompare bomCompareFirst = bomStructureFirst;
		BOMCompare bomCompareSecond = bomStructureSecond;

		BOMStructure firstBOMBomStructure = bomCompareFirst.getLeftBomStructure() == null ? bomCompareFirst
				.getRightBomStructure() : bomCompareFirst.getLeftBomStructure();
		BOMStructure secondBOMBomStructure = bomCompareSecond.getLeftBomStructure() == null ? bomCompareSecond
				.getRightBomStructure() : bomCompareFirst.getLeftBomStructure();
		int resurt = 0;
		if (firstBOMBomStructure==null &&secondBOMBomStructure==null)
		{
			return 0;
		}
		if (firstBOMBomStructure==null)
		{
			return -1;
		}
		if (secondBOMBomStructure==null)
		{
			return 1;
		}
		if (StringUtils.isNullString(firstBOMBomStructure.getSequence())
				|| StringUtils.isNullString(secondBOMBomStructure.getSequence()))
		{
			return -1;
		}
		if (Integer.parseInt(firstBOMBomStructure.getSequence()) < Integer
				.parseInt(secondBOMBomStructure.getSequence()))
		{
			resurt = -1;
		}
		else if (Integer.parseInt(firstBOMBomStructure.getSequence()) == Integer.parseInt(secondBOMBomStructure
				.getSequence()))
		{
			resurt = 0;
		}
		else
		{
			resurt = 1;
		}

		return resurt;
	}

}
