/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttachTemplate
 * zhanghj 2011-4-16
 */
package dyna.common.dto.wf;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 流程模板
 * 
 * @author zhanghj
 * 
 */
public class ApproveTemplate extends SystemObjectImpl implements SystemObject
{

	private static final long			serialVersionUID			= 1L;
	public static final String			PROCRT_NAME					= "PROCRTNAME";
	public List<ApproveTemplateDetail>	listProcessTemplateDetail	= null;
	public static final String 			NAME						= "PERFTEMPLATENAME";

	public String getProcrtName()
	{
		return (String) super.get(PROCRT_NAME);
	}

	public void setProcrtName(String procName)
	{
		super.put(PROCRT_NAME, procName);
	}

	public List<ApproveTemplateDetail> listProcessTemplateDetail()
	{
		return this.listProcessTemplateDetail;
	}

	public void setListProcessTemplateDetail(List<ApproveTemplateDetail> listProcessTemplateDetail)
	{
		this.listProcessTemplateDetail = listProcessTemplateDetail;
	}
	
	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}
}
