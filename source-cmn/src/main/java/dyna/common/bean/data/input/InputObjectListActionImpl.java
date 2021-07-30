/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InputObjectListActionImpl
 * Wanglei 2012-4-1
 */
package dyna.common.bean.data.input;

import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;

/**
 * Search Result动作脚本的输入对象
 * 
 * @author Qiuxq
 *
 */
public class InputObjectListActionImpl extends AbstractInputObject
{

	private static final long	serialVersionUID	= 5165617970259613697L;

	private String className=null;

	private List<FoundationObject> selectedObjectS=null;
	
	
	/**
	 * 构造方法
	 * 
	 * @param className 
	 * 		    Search Result的类的Name
	 * @param selectedBOMStructures
	 * 			Search Result选择的对象
	 */
	public InputObjectListActionImpl(String className,List<FoundationObject> selectedObjectS)
	{
		this.className=className;
		this.selectedObjectS=selectedObjectS;
	}

	/* (non-Javadoc)
	 * @see dyna.common.bean.data.InputObject#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the className
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * @return the selectedObjectS
	 */
	public List<FoundationObject> getSelectedObjectS()
	{
		return selectedObjectS;
	}

}
