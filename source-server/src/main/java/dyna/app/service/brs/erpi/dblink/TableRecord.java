package dyna.app.service.brs.erpi.dblink;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * 表及数据
 * 主要用于生成T100中间库集成生成SQL语句
 * 含表名和记录数据
 * 
 * @author Administrator
 */
public class TableRecord
{
	// 表名
	private String										tableName			= null;

	// 表数据（批量插入更新）
	private LinkedList<Map<String, String>>	recordDataList		= null;

	// 列名
	private List<String>								columnNameList		= null;

	// ERPFieldMapping
	private List<ERPFieldMapping>						erpFieldMappingList	= null;

	// 查询条件
	private Map<String, String>							conditionMap		= null;

	public TableRecord(String tableName)
	{
		this.tableName = tableName;
	}

	public TableRecord(String tableName, LinkedList<Map<String, String>> recordDataList)
	{
		this.tableName = tableName;
		this.setRecordDataList(recordDataList);
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public LinkedList<Map<String, String>> getRecordDataList()
	{
		return recordDataList;
	}

	public void setRecordDataList(LinkedList<Map<String, String>> recordDataList)
	{
		this.recordDataList = recordDataList;
		if (!SetUtils.isNullList(recordDataList))
		{
			columnNameList = new ArrayList<String>();
			Map<String, String> recordData = recordDataList.getFirst();
			if (!SetUtils.isNullMap(recordData))
			{
				Iterator<String> columnIt = recordData.keySet().iterator();
				while (columnIt.hasNext())
				{
					columnNameList.add(columnIt.next());
				}
			}
		}
	}

	public List<String> getColumnNameList()
	{
		return columnNameList;
	}

	public void setColumnNameList(List<String> columnNameList)
	{
		this.columnNameList = columnNameList;
	}

	public List<ERPFieldMapping> getErpFieldMappingList()
	{
		return erpFieldMappingList;
	}

	public void setErpFieldMappingList(List<ERPFieldMapping> erpFieldMappingList)
	{
		this.erpFieldMappingList = erpFieldMappingList;
		if (!SetUtils.isNullList(erpFieldMappingList))
		{
			columnNameList = new ArrayList<String>();
			for (int i = 0; i < erpFieldMappingList.size(); i++)
			{
				if (!StringUtils.isNullString(erpFieldMappingList.get(i).getERPField()))
				{
					columnNameList.add(erpFieldMappingList.get(i).getERPField());
				}
			}
		}
	}

	public Map<String, String> getConditionMap()
	{
		return conditionMap;
	}

	public void setConditionMap(Map<String, String> conditionMap)
	{
		this.conditionMap = conditionMap;
	}

}
