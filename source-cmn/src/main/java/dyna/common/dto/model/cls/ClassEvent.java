/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.dto.model.cls;

import dyna.common.bean.model.AbstractScript;
import dyna.common.systemenum.ScriptTypeEnum;


public class ClassEvent extends AbstractScript
{
	private static final long	serialVersionUID	= -3699501028572128906L;

	public static final String	CLASSFK				= "CLASSFK";

	public ClassEvent()
	{
		this.setScriptType(ScriptTypeEnum.CLASSEVENT);
	}

	public String getClassfk()
	{
		return (String) this.get(CLASSFK);
	}

	public void setClassfk(String classfk)
	{
		this.put(CLASSFK, classfk);
	}

	@Override
	public ClassEvent clone()
	{
		return (ClassEvent) super.clone();
	}
}
