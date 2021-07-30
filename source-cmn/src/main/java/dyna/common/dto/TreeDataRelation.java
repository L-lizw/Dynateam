package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @Description: 树形结构数据父子关系
 * @author: duanll
 * @date: 2020年3月26日
 */
public class TreeDataRelation extends SystemObjectImpl implements SystemObject
{
	private static final long serialVersionUID = 1L;

	/**
	 * 当前数据
	 */
	public static final String DATAGUID = "DATAGUID";

	/**
	 * 子数据
	 */
	public static final String SUBDATAGUID = "SUBDATAGUID";

	/**
	 * 数据类型:CLASS,GROUP,FOLDER,CODEITEM,INTERFACE
	 */
	public static final String DATATYPE = "DATATYPE";

	public static final String DATATYPE_CLASS = "CLASS";

	public static final String DATATYPE_GROUP = "GROUP";

	public static final String DATATYPE_FOLDER = "FOLDER";

	public static final String DATATYPE_CODEITEM = "CODEITEM";

	public static final String DATATYPE_INTERFACE = "INTERFACE";

	public String getDataGuid()
	{
		return (String) this.get(DATAGUID);
	}

	public void setDataGuid(String dataGuid)
	{
		this.put(DATAGUID, dataGuid);
	}

	public String getSubDataGuid()
	{
		return (String) this.get(SUBDATAGUID);
	}

	public void setSubDataGuid(String subDataGuid)
	{
		this.put(SUBDATAGUID, subDataGuid);
	}

	public String getDataType()
	{
		return (String) this.get(DATATYPE);
	}

	public void setDataType(String dataType)
	{
		this.put(DATATYPE, dataType);
	}
}
