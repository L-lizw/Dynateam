/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InputObjectListActionImpl
 * Wanglei 2012-4-1
 */
package dyna.common.bean.data.input;

import dyna.common.bean.data.ObjectGuid;

/**
 * Search Result动作脚本的输入对象
 * 
 * @author Qiuxq
 * 
 */
public class InputObjectWrokflowActionImpl extends AbstractInputObject
{

	private static final long	serialVersionUID	= 5165617970259613697L;

	private String				processGuid			= null;

	private String				processName			= null;

	private String				actrtGuid			= null;

	private String				actrtName			= null;

	/**
	 * @return the processName
	 */
	public String getProcessName()
	{
		return this.processName;
	}

	/**
	 * @return the actrtGuid
	 */
	public String getActrtGuid()
	{
		return this.actrtGuid;
	}

	/**
	 * @return the actrtName
	 */
	public String getActrtName()
	{
		return this.actrtName;
	}

	/**
	 * 构造方法
	 * 
	 * @param className
	 *            Search Result的类的Name
	 * @param selectedBOMStructures
	 *            Search Result选择的对象
	 */
	public InputObjectWrokflowActionImpl(String processGuid, String processName, String actrtGuid, String actrtName)
	{
		this.processName = processName;
		this.actrtGuid = actrtGuid;
		this.actrtName = actrtName;
		this.processGuid = processGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.InputObject#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the processGuid
	 */
	public String getProcessGuid()
	{
		return this.processGuid;
	}

}
