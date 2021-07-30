/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InterfaceObject
 * Jiagang 2010-8-3
 */
package dyna.common.bean.model.itf;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 接口对象
 * 
 * @author Wanglhb
 * 
 */
public class InterfaceObject extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 4627494234724633698L;

	private String				parentInterfaces	= null;

	private String				name				= null;
	/**
	 * 是否单一，该属性可以被子接口继承
	 * true 表示只能继承这一个接口，继承该接口后就不能再继承其它接口，但是可以继承子接口
	 * false 表示可以多继承
	 */

	private boolean				isSingle			= false;

	/**
	 * 是否系统接口
	 * 继承该接口的类为系统类，系统类只能查看不能修改
	 */

	private boolean				isSystem			= false;

	/**
	 * 如果在接口上设置创建表，则该接口的所有类都创建表，且不能在建模器修改
	 */
	private boolean				isCreateTable		= false;

	/**
	 * 数据处理使用的标记，非模型树形
	 */
	private boolean				isHandled			= false;

	/**
	 * @param parentInterfaces
	 *            the parentInterfaces to set
	 */
	public void setParentInterfaces(String parentInterfaces)
	{
		this.parentInterfaces = parentInterfaces;
	}

	/**
	 * @return the parentInterfaces
	 */
	public String getParentInterfaces()
	{
		return this.parentInterfaces;
	}

	/**
	 * @param isSingle
	 *            the isSingle to set
	 */
	public void setSingle(boolean isSingle)
	{
		this.isSingle = isSingle;
	}

	/**
	 * @return the isSingle
	 */
	public boolean isSingle()
	{
		return this.isSingle;
	}

	/**
	 * @param isSystem
	 *            the isSystem to set
	 */
	public void setSystem(boolean isSystem)
	{
		this.isSystem = isSystem;
	}

	/**
	 * @return the isSystem
	 */
	public boolean isSystem()
	{
		return this.isSystem;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isCreateTable()
	{
		return isCreateTable;
	}

	public void setCreateTable(boolean createTable)
	{
		isCreateTable = createTable;
	}

	public boolean isHandled()
	{
		return isHandled;
	}

	public void setHandled(boolean handled)
	{
		isHandled = handled;
	}

	@Override
	public InterfaceObject clone()
	{
		return (InterfaceObject) super.clone();
	}
}
