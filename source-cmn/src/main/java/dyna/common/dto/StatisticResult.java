/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: StatisticResult 用于传输库的统计结果
 * caogc 2011-03-29
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.aas.Group;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.StatisticResultEnum;
import dyna.common.systemenum.SystemStatusEnum;

/**
 * StatisticResult 用于传输库的统计结果
 * 
 * @author caogc
 * 
 */
public class StatisticResult extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -4966476859629926639L;

	// 类别
	public StatisticResultEnum	type				= null;

	// 接口
	public ModelInterfaceEnum	interfaceEnum		= null;

	// 数量
	public Long					number				= null;

	// 业务对象
	public BOInfo				businessObject		= null;

	// 业务模型
	public BMInfo				bmInfo				= null;

	// 类别
	public CodeItemInfo			classification		= null;

	// 状态
	public SystemStatusEnum		status				= null;

	// 组
	public Group				group				= null;

	// classificationMaster
	public CodeObjectInfo		codeObjectInfo		= null;

	public Folder				folder				= null;

	public CodeObjectInfo getCodeObjectInfo()
	{
		return this.codeObjectInfo;
	}

	public void setCodeObjectInfo(CodeObjectInfo codeObjectInfo)
	{
		this.codeObjectInfo = codeObjectInfo;
	}

	public Folder getFolder()
	{
		return this.folder;
	}

	public void setFolder(Folder folder)
	{
		this.folder = folder;
	}

	public SystemStatusEnum getSystemStatusEnum()
	{
		return this.status;
	}

	public void setSystemStatusEnum(SystemStatusEnum systemStatusEnum)
	{
		this.status = systemStatusEnum;
	}

	public BMInfo getBMInfo()
	{
		return this.bmInfo;
	}

	public void setBMInfo(BMInfo bmInfo)
	{
		this.bmInfo = bmInfo;
	}

	public Group getGroup()
	{
		return this.group;
	}

	public void setGroup(Group group)
	{
		this.group = group;
	}

	public CodeItemInfo getClassification()
	{
		return this.classification;
	}

	public void setClassification(CodeItemInfo classification)
	{
		this.classification = classification;
	}

	public BOInfo getBusinessObject()
	{
		return this.businessObject;
	}

	public void setBusinessObject(BOInfo businessObject)
	{
		this.businessObject = businessObject;
	}

	public Long getNumber()
	{
		return this.number;
	}

	public void setNumber(Long number)
	{
		this.number = number;
	}

	public ModelInterfaceEnum getInterfaceEnum()
	{
		return this.interfaceEnum;
	}

	public void setInterfaceEnum(ModelInterfaceEnum interfaceEnum)
	{
		this.interfaceEnum = interfaceEnum;
	}

	public StatisticResultEnum getType()
	{
		return this.type;
	}

	public void setType(StatisticResultEnum type)
	{
		this.type = type;
	}
}
