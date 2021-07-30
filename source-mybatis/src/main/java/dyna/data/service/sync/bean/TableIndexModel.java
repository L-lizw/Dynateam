package dyna.data.service.sync.bean;

import dyna.common.systemenum.TableIndexTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableIndexModel implements Cloneable, Serializable
{

	/**
	 *
	 */
	private static final long		serialVersionUID		= -9207596182181589994L;

	@Attribute(name = "name", required = false)
	private String					name					= null;

	/**
	 * 是否为唯一索引
	 */
	@Attribute(name = "unique", required = false)
	private boolean					isUnique				= false;

	@Attribute(name = "type", required = false)
	private String					type					= null;

	/**
	 * 索引
	 */
	@ElementList(entry = "index-object", required = false, inline = true)
	private List<TableIndexObject>	tableIndexObjectList	= null;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @return the type
	 */
	public TableIndexTypeEnum getTypeEnum()
	{
		return TableIndexTypeEnum.typeValueOf(this.type);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	public void setType(TableIndexTypeEnum type)
	{
		this.type = type.name();
	}

	public boolean isUnique()
	{
		return isUnique;
	}

	public void setUnique(boolean isUnique)
	{
		this.isUnique = isUnique;
	}

	public List<TableIndexObject> getTableIndexObjectList()
	{
		return tableIndexObjectList;
	}

	public void setTableIndexObjectList(List<TableIndexObject> tableIndexObjectList)
	{
		this.tableIndexObjectList = tableIndexObjectList;
	}

	public TableIndexModel getAlreadyIndexedModel(List<TableIndexModel> indexModelList)
	{
		if (SetUtils.isNullList(indexModelList))
		{
			return null;
		}
		for (TableIndexModel indexModel : indexModelList)
		{
			if (indexModel.isUnique() != this.isUnique)
			{
				continue;
			}
			if (this.isAlreadyIndexed(indexModel.getTableIndexObjectList()))
			{
				if (this.isUnique)
				{
					if (!StringUtils.isNullString(indexModel.getName()))
					{
						if (!indexModel.getName().equalsIgnoreCase(this.getName()))
						{
							return null;
						}
					}
				}
				return indexModel;
			}
		}
		return null;
	}

	private boolean isAlreadyIndexed(List<TableIndexObject> indexList)
	{
		if (indexList == null)
		{
			indexList = new ArrayList<TableIndexObject>();
		}

		if (this.tableIndexObjectList == null)
		{
			this.tableIndexObjectList = new ArrayList<TableIndexObject>();
		}

		if (this.tableIndexObjectList.size() != indexList.size())
		{
			return false;
		}

		List<String> tmpList = new ArrayList<String>();
		if (!SetUtils.isNullList(indexList))
		{
			for (TableIndexObject indexObject : indexList)
			{
				tmpList.add(indexObject.getColumnName().toUpperCase().trim());
			}
		}

		for (TableIndexObject indexObject : this.tableIndexObjectList)
		{
			if (tmpList.contains(indexObject.getColumnName().toUpperCase().trim()))
			{
				tmpList.remove(indexObject.getColumnName().toUpperCase().trim());
			}
			else
			{
				return false;
			}
		}
		if (tmpList.size() > 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private String converListToStr(List<TableIndexObject> indexList)
	{
		if (!SetUtils.isNullList(indexList))
		{
			Collections.sort(indexList, new Comparator<TableIndexObject>()
			{

				@Override
				public int compare(TableIndexObject o1, TableIndexObject o2)
				{
					return o1.getColumnName().compareTo(o2.getColumnName());
				}
			});

			StringBuilder builder = new StringBuilder();
			for (TableIndexObject indexObject : indexList)
			{
				if (builder.length() > 0)
				{
					builder.append(",");
				}
				builder.append(indexObject.getColumnName());
			}
			return builder.toString();
		}
		return null;
	}

	public String converListToStr()
	{
		return this.converListToStr(this.tableIndexObjectList);
	}

	@Override
	public TableIndexModel clone() throws CloneNotSupportedException
	{
		TableIndexModel indexModel = (TableIndexModel) super.clone();
		List<TableIndexObject> indexObjectList = this.getTableIndexObjectList();
		if (!SetUtils.isNullList(indexObjectList))
		{
			List<TableIndexObject> indexObjectList_ = new ArrayList<>();
			for (TableIndexObject indexObject : indexObjectList)
			{
				indexObjectList_.add(indexObject.clone());
			}
			indexModel.setTableIndexObjectList(indexObjectList_);
		}
		return indexModel;
	}

	public boolean useColumn(String name2)
	{
		List<TableIndexObject> indexObjectList = this.getTableIndexObjectList();
		if (!SetUtils.isNullList(indexObjectList))
		{
			for (TableIndexObject indexObject : indexObjectList)
			{
				if (name2.equalsIgnoreCase(indexObject.getColumnName()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
