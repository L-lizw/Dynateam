/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMCompare  用于保存BOM比较后的某一顺序号的信息的BEAN
 * caogc 2010-10-12
 */
package dyna.common.dto.template.bom;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.systemenum.ECOperateTypeEnum;

/**
 * 用于保存BOM比较后的某一顺序号的信息的BEAN
 * <String>存放的信息有：同一顺序号的左右两个<String>
 * 
 * @author caogc
 * 
 */
public class BOMCompare extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -2479408193776468137L;

	// 左边的BOMStructure
	public BOMStructure			leftBomStructure	= null;

	// 右边的BOMStructure
	public BOMStructure			rightBomStructure	= null;

	// 版本是否相同
	public boolean				isSameVersion		= false;

	// 数量是否相同
	public boolean				isSameQuantity		= false;

	// 对象Id是否相同
	public boolean				isSameId			= false;

	// BOM编辑的操作符
	public ECOperateTypeEnum	ecOperateTypeEnum	= null;

	public List<String>			fieldChangedList	= null;

	// 左end2有读取权限
	private boolean				hasLeftACL			= true;

	// 右end2有读取权限
	private boolean				hasRightACL			= true;

	public ECOperateTypeEnum getECOperateTypeEnum()
	{
		return this.ecOperateTypeEnum;
	}

	public List<String> getFieldChangedList()
	{
		return this.fieldChangedList;
	}

	public BOMStructure getLeftBomStructure()
	{
		return this.leftBomStructure;
	}

	public BOMStructure getRightBomStructure()
	{
		return this.rightBomStructure;
	}

	public boolean isSameId()
	{
		return this.isSameId;
	}

	public boolean isSameQuantity()
	{
		return this.isSameQuantity;
	}

	public boolean isSameVersion()
	{
		return this.isSameVersion;
	}

	public void setECOperateTypeEnum(ECOperateTypeEnum ecOperateTypeEnum)
	{
		this.ecOperateTypeEnum = ecOperateTypeEnum;
	}

	public void setFieldChangedList(List<String> fieldChangedList)
	{
		this.fieldChangedList = fieldChangedList;
	}

	public void setLeftBomStructure(BOMStructure leftBomStructure)
	{
		this.leftBomStructure = leftBomStructure;
	}

	public void setRightBomStructure(BOMStructure rightBomStructure)
	{
		this.rightBomStructure = rightBomStructure;
	}

	public void setSameId(boolean isSameId)
	{
		this.isSameId = isSameId;
	}

	public void setSameQuantity(boolean isSameQuantity)
	{
		this.isSameQuantity = isSameQuantity;
	}

	public void setSameVersion(boolean isSameVersion)
	{
		this.isSameVersion = isSameVersion;
	}

	/**
	 * @return the hasLeftACL
	 */
	public boolean isHasLeftACL()
	{
		return this.hasLeftACL;
	}

	/**
	 * @param hasLeftACL
	 *            the hasLeftACL to set
	 */
	public void setHasLeftACL(boolean hasLeftACL)
	{
		this.hasLeftACL = hasLeftACL;
	}

	/**
	 * @return the hasRightACL
	 */
	public boolean isHasRightACL()
	{
		return this.hasRightACL;
	}

	/**
	 * @param hasRightACL
	 *            the hasRightACL to set
	 */
	public void setHasRightACL(boolean hasRightACL)
	{
		this.hasRightACL = hasRightACL;
	}

}
