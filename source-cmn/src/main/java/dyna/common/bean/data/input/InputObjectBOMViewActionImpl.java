/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InputObjectBOMViewActionImpl
 * Wanglei 2011-12-21
 */
package dyna.common.bean.data.input;

import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.structure.BOMStructure;

/**
 * BOMView动作脚本的输入对象
 * 
 * @author Wanglei
 * 
 */
public class InputObjectBOMViewActionImpl extends AbstractInputObject
{

	private static final long	serialVersionUID	= 8373591417304400650L;

	private ObjectGuid			bomViewObjectGuid		= null;
	private List<BOMStructure>	selectedBOMStructures	= null;

	/**
	 * 构造方法
	 * 
	 * @param bomViewObjectGuid
	 *            脚本所在bomview的guid
	 * @param selectedBOMStructures
	 *            选择的bom结构信息列表
	 */
	public InputObjectBOMViewActionImpl(ObjectGuid bomViewObjectGuid, List<BOMStructure> selectedBOMStructures)
	{
		this.bomViewObjectGuid = bomViewObjectGuid;
		this.selectedBOMStructures = selectedBOMStructures;
	}

	/**
	 * 返回bomview的objectguid
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		return this.bomViewObjectGuid;
	}

	/**
	 * 返回选中的结构信息列表
	 * 
	 * @return
	 */
	public List<BOMStructure> getSelectedBOMStructures()
	{
		return this.selectedBOMStructures;
	}
}
