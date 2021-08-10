/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECEffectedBOMRelationComparator
 * caogc 2011-3-31
 */
package dyna.app.service.brs.bom.comparator;

import java.util.Comparator;

import dyna.common.dto.ECEffectedBOMRelation;
import dyna.common.systemenum.ECOperateTypeEnum;

/**
 * BOM编辑前排序,将要移除的对象拍到前面,保证对要移除的对象先做移除操作
 * 
 * @author caogc
 * 
 */
public class ECEffectedBOMRelationComparator implements Comparator<ECEffectedBOMRelation>
{
	public int compare(ECEffectedBOMRelation bomStructureFirst, ECEffectedBOMRelation bomStructureSecond)
	{
		ECEffectedBOMRelation bomCompareFirst = bomStructureFirst;
		ECEffectedBOMRelation bomCompareSecond = bomStructureSecond;

		int resurt = 0;

		if (!ECOperateTypeEnum.REMOVE.equals(bomCompareFirst.getEcOperate())
				&& ECOperateTypeEnum.REMOVE.equals(bomCompareSecond.getEcOperate()))
		{
			resurt = 1;
		}
		return resurt;
	}

}
