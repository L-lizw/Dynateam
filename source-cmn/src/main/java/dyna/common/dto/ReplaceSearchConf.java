/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceSearchConf
 * wangweixia 2012-7-12
 */
package dyna.common.dto;

import java.util.Date;
import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ReplaceRangeEnum;
import dyna.common.systemenum.ReplaceTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * 取替代查询
 * 
 * @author wangweixia
 * 
 */
public class ReplaceSearchConf extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4674085842623986574L;

	// 搜索方式：1、主件；2、元件；3、主件&元件
	public static final String	SEARCH_TYPE			= "SEARCH_TYPE";

	// 主件编号
	public static final String	MASTERITEM			= "MASTERITEM";
	// 元件编号
	public static final String	SUBITEM				= "SUBITEM";
	// 取替代件编号
	public static final String	RSITEM				= "RSITEM";
	// BOM模板名称
	public static final String	BOMNAME				= "BOMNAME";
	// 范围
	public static final String	RANGE				= "RANGE";
	// 生效日期开始
	public static final String	STARTEFFECTIVEDATE	= "STARTEFFECTIVEDATE";
	// 失效日期开始
	public static final String	STARTEXPIRYEDATE	= "STARTEXPIRYEDATE";
	// 生效日期结束
	public static final String	ENDEFFECTIVEDATE	= "ENDEFFECTIVEDATE";
	// 失效日期结束
	public static final String	ENDEXPIRYEDATE		= "ENDEXPIRYEDATE";
	// 类型
	public static final String	TYPE				= "TYPE";
	// 数据开始的行数, 对应数据库中的row number
	public static final String	ROWINDEX			= "ROWINDEX";
	// 每页显示数量
	public static final String	PAGESIZE			= "PAGESIZE";
	// 是否翻页
	public static final String	ISFORWARD			= "ISFORWARD";
	// 查询结果列
	public static final String	SEARCHRESULT		= "SEARCHRESULT";

	/**
	 * @return the bOMNAME
	 */
	public String getBOMName()
	{
		return (String) this.get(BOMNAME);
	}

	/**
	 * @param bOMNAME
	 *            the bOMNAME to set
	 */
	public void setBOMName(String bOMNAME)
	{
		this.put(BOMNAME, bOMNAME);
	}

	/**
	 * @return the rangeList
	 */
	public ReplaceRangeEnum getRange()
	{
		return StringUtils.isNullString((String) this.get(RANGE)) ? null : ReplaceRangeEnum.typeValueOf((String) this.get(RANGE));
	}

	/**
	 * @param rangeList
	 *            the rangeList to set
	 */
	public void setRange(ReplaceRangeEnum range)
	{
		if (range == null)
		{
			this.put(RANGE, null);
		}
		else
		{
			this.put(RANGE, range.getValue());
		}
	}

	/**
	 * @return the tYPELIST
	 */
	public ReplaceTypeEnum getType()
	{
		return StringUtils.isNullString((String) this.get(TYPE)) ? null : ReplaceTypeEnum.typeValueOf((String) this.get(TYPE));
	}

	/**
	 * @param tYPELIST
	 *            the tYPELIST to set
	 */
	public void setType(ReplaceTypeEnum type)
	{
		if (type == null)
		{
			this.put(TYPE, null);
		}
		else
		{
			this.put(TYPE, type.getValue());
		}
	}

	/**
	 * @return the searchType
	 */
	public String getSearchType()
	{
		return (String) this.get(SEARCH_TYPE);
	}

	public void setSearchType(String searchType)
	{
		this.put(SEARCH_TYPE, searchType);
	}

	/**
	 * @return the end1Id
	 */
	public ObjectGuid getMasterItemObjectGuid()
	{
		if (StringUtils.isNullString((String) this.get(MASTERITEM)))
		{
			return null;
		}
		else
		{
			ObjectGuid master = new ObjectGuid();
			master.setGuid((String) this.get(MASTERITEM));
			master.setClassGuid((String) this.get(MASTERITEM + "$CLASS"));
			master.setMasterGuid((String) this.get(MASTERITEM + "$MASTER"));
			// master.setIsMaster(true);
			return master;
		}
	}

	public void setMasterItemObjectGuid(ObjectGuid objectGuid)
	{
		if (objectGuid == null)
		{
			this.put(MASTERITEM, null);
			this.put(MASTERITEM + "$CLASS", null);
			this.put(MASTERITEM + "$MASTER", null);
		}
		else
		{
			this.put(MASTERITEM, objectGuid.getGuid());
			this.put(MASTERITEM + "$CLASS", objectGuid.getClassGuid());
			this.put(MASTERITEM + "$MASTER", objectGuid.getMasterGuid());
		}
	}

	/**
	 * @return the end2Id
	 */
	public ObjectGuid getComponentItemObjectGuid()
	{
		if (StringUtils.isNullString((String) this.get(SUBITEM)))
		{
			return null;
		}
		else
		{
			ObjectGuid master = new ObjectGuid();
			master.setClassGuid((String) this.get(SUBITEM + "$CLASS"));
			master.setMasterGuid((String) this.get(SUBITEM + "$MASTER"));
			master.setIsMaster(true);
			return master;
		}
	}

	public void setComponentItemObjectGuid(ObjectGuid objectGuid)
	{
		if (objectGuid == null)
		{
			this.put(SUBITEM, null);
			this.put(SUBITEM + "$CLASS", null);
			this.put(SUBITEM + "$MASTER", null);
		}
		else
		{
			this.put(SUBITEM, objectGuid.getGuid());
			this.put(SUBITEM + "$CLASS", objectGuid.getClassGuid());
			this.put(SUBITEM + "$MASTER", objectGuid.getMasterGuid());
		}
	}

	/**
	 * @return the replaceid
	 */
	public ObjectGuid getRSItemObjectGuid()
	{
		if (StringUtils.isNullString((String) this.get(RSITEM)))
		{
			return null;
		}
		else
		{
			ObjectGuid master = new ObjectGuid();
			master.setClassGuid((String) this.get(RSITEM + "$CLASS"));
			master.setMasterGuid((String) this.get(RSITEM + "$MASTER"));
			master.setIsMaster(true);
			return master;
		}
	}

	public void setRSItemObjectGuid(ObjectGuid objectGuid)
	{
		if (objectGuid == null)
		{
			this.put(RSITEM, null);
			this.put(RSITEM + "$CLASS", null);
			this.put(RSITEM + "$MASTER", null);
		}
		else
		{
			this.put(RSITEM, objectGuid.getGuid());
			this.put(RSITEM + "$CLASS", objectGuid.getClassGuid());
			this.put(RSITEM + "$MASTER", objectGuid.getMasterGuid());
		}
	}

	/**
	 * @return the starteffectivedate
	 */
	public Date getStartEffectivedate()
	{
		return (Date) this.get(STARTEFFECTIVEDATE);
	}

	public void setStartEffectivedate(Date effectiveDate)
	{
		this.put(STARTEFFECTIVEDATE, effectiveDate);
	}

	/**
	 * @return the start expiryedate
	 */
	public Date getStartExpiryedate()
	{
		return (Date) this.get(STARTEXPIRYEDATE);
	}

	public void setStartExpiryedate(Date expiryeDate)
	{
		this.put(STARTEXPIRYEDATE, expiryeDate);
	}

	/**
	 * @return the end effectivedate
	 */
	public Date getEndEffectivedate()
	{
		return (Date) this.get(ENDEFFECTIVEDATE);
	}

	public void setEndEffectivedate(Date effectiveDate)
	{
		this.put(ENDEFFECTIVEDATE, effectiveDate);
	}

	/**
	 * @return the end expiryedate
	 */
	public Date getEndExpiryedate()
	{
		return (Date) this.get(ENDEXPIRYEDATE);
	}

	public void setEndExpiryedate(Date expiryeDate)
	{
		this.put(ENDEXPIRYEDATE, expiryeDate);
	}

	/**
	 * @return the rowIndex
	 */
	public long getRowIndex()
	{
		return (Long) this.get(ROWINDEX);
	}

	/**
	 * @param rowIndex
	 *            the rowIndex to set
	 */
	public void setRowIndex(long rowIndex)
	{
		this.put(ROWINDEX, rowIndex);
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize()
	{
		return (Integer) this.get(PAGESIZE);
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize)
	{
		this.put(PAGESIZE, pageSize);
	}

	/**
	 * @return the isForward
	 */
	public boolean isForward()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(ISFORWARD));
	}

	/**
	 * @param isForward
	 *            the isForward to set
	 */
	public void setForward(boolean isForward)
	{
		this.put(ISFORWARD, isForward ? "Y" : "N");
	}

	/**
	 * @return the SEARCHRESULT
	 */
	@SuppressWarnings("unchecked")
	public List<String> getSearchResult()
	{
		return (List<String>) this.get(SEARCHRESULT);
	}

	/**
	 * @param list
	 *            the SEARCHRESULT to set
	 */
	public void setSearchResult(List<String> list)
	{
		this.put(SEARCHRESULT, list);
	}

}
