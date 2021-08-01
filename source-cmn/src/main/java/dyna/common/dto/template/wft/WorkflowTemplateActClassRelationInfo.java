/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActClassRelation
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.wft.WorkflowTemplateActClassRelationInfoMapper;
import dyna.common.systemenum.WFTemplateRelationTypeEnum;
import dyna.common.util.BooleanUtils;

/**
 * 工作流模板活动节点class 关系 Bean
 * 
 * @author WangLHB
 * 
 */
@Cache
@EntryMapper(WorkflowTemplateActClassRelationInfoMapper.class)
public class WorkflowTemplateActClassRelationInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2833440885506515220L;

	// public static final String GUID = "GUID";
	public static final String	TEMACTBOGUID		= "TEMACTBOGUID";
	public static final String	RELATIONNAME		= "RELATIONNAME";
	public static final String	RELATIONTYPE		= "RELATIONTYPE";

	public static final String	EDIT				= "EDITRELATION";
	public static final String	VIEW				= "SELECTRELATION";

	/**
	 * @return the temActBOGuid
	 */
	public String getTemActBOGuid()
	{
		return (String) this.get(TEMACTBOGUID);
	}

	/**
	 * @param temactBOGuid
	 *            the temactBOGuid to set
	 */
	public void setTemActBOGuid(String temactBOGuid)
	{
		this.put(TEMACTBOGUID, temactBOGuid);
	}

	/**
	 * @return the relationName
	 */
	public String getRelationName()
	{
		return (String) this.get(RELATIONNAME);
	}

	/**
	 * @param relationName
	 *            the relationName to set
	 */
	public void setRelationName(String relationName)
	{
		this.put(RELATIONNAME, relationName);
	}

	/**
	 * @return the relationType
	 */
	public WFTemplateRelationTypeEnum getRelationType()
	{
		return WFTemplateRelationTypeEnum.typeValueOf((String) this.get(RELATIONTYPE));
	}

	/**
	 * @param relationTypeEnum
	 *            the relationTypeEnum to set
	 */
	public void setRelationType(WFTemplateRelationTypeEnum relationTypeEnum)
	{
		if (relationTypeEnum == null)
		{
			this.put(RELATIONTYPE, "");
		}
		else
		{
			this.put(RELATIONTYPE, relationTypeEnum.toString());
		}
	}

	/**
	 * @return the isView
	 */
	public boolean isView()
	{
		if (this.get(VIEW) == null)
		{
			return true;
		}
		else
		{
			return BooleanUtils.getBooleanBy10((String) this.get(VIEW));
		}
	}

	/**
	 * @param isView
	 *            the isView to set
	 */
	public void setView(boolean isView)
	{
		this.put(VIEW, BooleanUtils.getBooleanString10(isView));
	}

	/**
	 * @return the isEdit
	 */
	public boolean isEdit()
	{

		if (this.get(EDIT) == null)
		{
			return false;
		}
		else
		{
			return BooleanUtils.getBooleanBy10((String) this.get(EDIT));
		}
	}

	/**
	 * @param isEdit
	 *            the isEdit to set
	 */
	public void setEdit(boolean isEdit)
	{
		this.put(EDIT, BooleanUtils.getBooleanString10(isEdit));
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMACTBOGUID);
	}

	@Override
	public WorkflowTemplateActClassRelationInfo clone()
	{
		return (WorkflowTemplateActClassRelationInfo) super.clone();
	}
}
