/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SAPImportType
 * wangweixia 2012-1-4
 */
package dyna.common.systemenum;

/**
 * @author wangweixia
 * 
 */
public enum SAPImportType
{
	ITEM, // 导出item
	BOM, // 导出bom
	ITEM_BOM, // 导出item以及所有下层物料
	ITEM_AND_BOM;// 导出item以及下层所有物料和bom结构

}
