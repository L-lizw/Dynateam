/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.bean.model.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.StringUtils;

public class UIObject extends SystemObjectImpl implements SystemObject
{
	private static final long		serialVersionUID	= 5433733140889652588L;

	private final UIObjectInfo		info;

	private boolean					isInherited			= false;

	private List<UIField>			fieldList			= null;

	private List<UIAction>			actionList			= null;

	private List<ReportTypeEnum>	reportTypeList		= null;

	public UIObject()
	{
		this.info = new UIObjectInfo();
	}

	public UIObject(UIObjectInfo info)
	{
		this.info = info;
	}

	/**
	 * @param action
	 *            the action to add
	 */
	public void addAction(UIAction action)
	{
		if (this.actionList == null)
		{
			this.actionList = new ArrayList<UIAction>();
		}
		if (action == null)
		{
			return;
		}

		this.actionList.add(action);
	}

	/**
	 * @param field
	 *            the field to add
	 */
	public void addField(UIField field)
	{
		if (this.fieldList == null)
		{
			this.fieldList = new ArrayList<UIField>();
		}
		if (field == null)
		{
			return;
		}

		this.fieldList.add(field);
		Collections.sort(this.fieldList, new UIFieldSeqCompare());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public UIObject clone()
	{
		return (UIObject) super.clone();
	}

	/**
	 * @return the actionList
	 */
	public List<UIAction> getActionList()
	{
		return this.actionList;
	}

	public void setActionList(List<UIAction> actionList)
	{
		this.actionList = actionList;
	}

	/**
	 * @return the Businessmodels
	 */
	public String getBusinessmodels()
	{
		return this.info.getBusinessmodels();
	}

	public void setBusinessmodels(String businessmodels)
	{
		this.info.setBusinessmodels(businessmodels);
	}

	/**
	 * @return the fieldList
	 */
	public List<UIField> getFieldList()
	{
		return this.fieldList;
	}

	public void setFieldList(List<UIField> fieldList)
	{
		this.fieldList = fieldList;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.info.getName();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return this.info.getTitle();
	}

	public void setTitle(String title)
	{
		this.info.setTitle(title);
	}

	/**
	 * language
	 * 
	 * @return the title
	 */
	public String getTitle(LanguageEnum language)
	{
		return this.info.getTitle(language);
	}

	/**
	 * @return the type
	 */
	public UITypeEnum getType()
	{
		return this.info.getType();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(UITypeEnum type)
	{
		this.info.setType(type);
	}

	/**
	 * @return the isInherited
	 */
	public boolean isInherited()
	{
		return this.isInherited;
	}

	/**
	 * @param isInherited
	 *            the isInherited to set
	 */
	public void setInherited(boolean isInherited)
	{
		this.isInherited = isInherited;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible()
	{
		return this.info.isVisible();
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible)
	{
		this.info.setVisible(visible);
	}

	/**
	 * @return the reportTemplateName
	 */
	public String getReportTemplateName()
	{
		return this.info.getReportTemplateName();
	}

	public void setReportTemplateName(String templateName)
	{
		this.info.setReportTemplateName(templateName);
	}

	/**
	 * @return the reportTypeList
	 */

	public List<ReportTypeEnum> getReportTypeList()
	{
		if (this.reportTypeList == null)
		{
			this.reportTypeList = new ArrayList<ReportTypeEnum>();
		}
		return this.reportTypeList;
	}

	public String getReportTypes()
	{
		StringBuffer sb = new StringBuffer();
		if (this.getReportTypeList() != null)
		{
			for (ReportTypeEnum type : this.getReportTypeList())
			{
				if (type != null)
				{
					sb.append(type.name() + ";");
				}
			}
		}
		return sb.toString();
	}

	public void setReportTypes(String types)
	{

		if (!StringUtils.isNullString(types))
		{
			String[] splitStringWithDelimiterHavEnd = StringUtils.splitStringWithDelimiterHavEnd(";", types);
			for (String type : splitStringWithDelimiterHavEnd)
			{
				try
				{
					this.getReportTypeList().add(ReportTypeEnum.valueOf(type));
				}
				catch (Exception e)
				{
				}
			}

		}
	}

	/**
	 * @return the oriReportTemplateName
	 */
	public String getOriReportTemplateName()
	{
		String reportTemplateName = this.getReportTemplateName();
		if (!StringUtils.isNullString(reportTemplateName))
		{
			String[] split = reportTemplateName.split("\\.");
			if (split.length > 0)
			{
				return split[0];
			}
		}
		return "";
	}

	public String getSortField1()
	{
		return this.info.getSortField1();
	}

	public void setSortField1(String sortField1)
	{
		this.info.setSortField1(sortField1);
	}

	public String getSortValue1()
	{
		return this.info.getSortValue1();
	}

	public void setSortValue1(String sortValue1)
	{
		this.info.setSortValue1(sortValue1);
	}

	public String getSortField2()
	{
		return this.info.getSortField2();
	}

	public void setSortField2(String sortField2)
	{
		this.info.setSortField2(sortField2);
	}

	public String getSortValue2()
	{
		return this.info.getSortValue2();
	}

	public void setSortValue2(String sortValue2)
	{
		this.info.setSortValue2(sortValue2);
	}

	public String getSortField3()
	{
		return this.info.getSortField3();
	}

	public void setSortField3(String sortField3)
	{
		this.info.setSortField3(sortField3);
	}

	public String getSortValue3()
	{
		return this.info.getSortValue3();
	}

	public void setSortValue3(String sortValue3)
	{
		this.info.setSortValue3(sortValue3);
	}

	/**
	 * @return the groupField
	 */
	public String getGroupField()
	{
		return this.info.getGroupField();
	}

	public void setGroupField(String groupField)
	{
		this.info.setGroupField(groupField);
	}

	/**
	 * @return the position
	 */
	public String getPosition()
	{
		return this.info.getPosition();
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(String position)
	{
		this.info.setPosition(position);
	}

	public UIObjectInfo getInfo()
	{
		return this.info;
	}

	public boolean isMaster()
	{
		return this.info.isMaster();
	}

	public void setMaster(boolean isMaster)
	{
		this.info.setMaster(isMaster);
	}

	public boolean isFromClassification()
	{
		return this.info.isFromClassification();
	}

	public void setFromClassification(boolean isFromClassification)
	{
		this.info.setFromClassification(isFromClassification);
	}

	public String getClassGuid()
	{
		return this.info.getClassGuid();
	}

	public void setClassGuid(String classGuid)
	{
		this.info.setClassGuid(classGuid);
	}

	public Boolean isFieldInherited()
	{
		return this.info.isFieldInherited();
	}

	public void setFieldInherited(Boolean isFieldInherited)
	{
		this.info.setFieldInherited(isFieldInherited);
	}
}
