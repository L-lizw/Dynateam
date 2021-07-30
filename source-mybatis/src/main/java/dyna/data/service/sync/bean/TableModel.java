package dyna.data.service.sync.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.model.cls.ClassObject;

/**
 * 数据库表模型
 * 
 * @author daniel
 * 
 */
public class TableModel implements Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 7682030221417516342L;

	// 表名
	private String					tableName			= null;

	// 字段列表
	private List<ColumnModel>		columnList			= null;

	// 一条记录为一个索引
	private List<TableIndexModel>	indexModelList		= new ArrayList<TableIndexModel>();

	private List<ClassObject>		classObjectList		= new ArrayList<ClassObject>();

	// 是否是结构表
	private boolean					isStructure			= false;

	// 是否检查唯一值
	private boolean					isCheckUnique		= false;

	// 是否检查编号唯一(表内唯一)
	private boolean					isIDUnique			= false;

	// 类内唯一
	private boolean					isIDClassGuidUnique	= false;

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public List<ColumnModel> getColumnList()
	{
		return columnList;
	}

	public void setColumnList(List<ColumnModel> columnList)
	{
		this.columnList = columnList;
	}

	public void addColumn(ColumnModel columnModel)
	{
		if (this.columnList == null)
		{
			this.columnList = new ArrayList<ColumnModel>();
		}
		this.columnList.add(columnModel);
	}

	public ColumnModel getColumnModel(ColumnModel columnModel)
	{
		if (this.columnList == null)
		{
			return null;
		}
		for (ColumnModel columnModel_ : this.columnList)
		{
			if (columnModel_.getName().equals(columnModel.getName()))
			{
				return columnModel_;
			}
		}
		return null;
	}

	public boolean containColumn(ColumnModel columnModel)
	{
		if (this.columnList == null)
		{
			return false;
		}
		for (ColumnModel columnModel_ : this.columnList)
		{
			if (columnModel_.getName().equals(columnModel.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isStructure()
	{
		return isStructure;
	}

	public void setStructure(boolean isStructure)
	{
		this.isStructure = isStructure;
	}

	public List<TableIndexModel> getIndexModelList()
	{
		return indexModelList;
	}

	public void setIndexModelList(List<TableIndexModel> indexModelList)
	{
		this.indexModelList = indexModelList;
	}

	public List<ClassObject> getClassObjectList()
	{
		return classObjectList;
	}

	public void setClassObjectList(List<ClassObject> classObjectList)
	{
		this.classObjectList = classObjectList;
	}

	public boolean isIDUnique()
	{
		return this.isIDUnique;
	}

	public void setIDUnique(boolean isIDUnique)
	{
		this.isIDUnique = isIDUnique;
	}

	public boolean isIDClassGuidUnique()
	{
		return this.isIDClassGuidUnique;
	}

	public void setIDClassGuidUnique(boolean isIDClassGuidUnique)
	{
		this.isIDClassGuidUnique = isIDClassGuidUnique;
	}

	public boolean isCheckUnique()
	{
		return isCheckUnique;
	}

	public void setCheckUnique(boolean isCheckUnique)
	{
		this.isCheckUnique = isCheckUnique;
	}
}
