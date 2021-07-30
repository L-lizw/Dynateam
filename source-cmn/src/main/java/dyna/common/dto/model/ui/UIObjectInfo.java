/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.dto.model.ui;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

@Cache
public class UIObjectInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID		= 5433733140889652588L;

	public static final String	CLASSFK					= "CLASSFK";

	public static final String	UINAME					= "UINAME";

	public static final String	TITLE					= "TITLE";

	public static final String	INHERITED				= "INHERITED";

	public static final String	BUSINESSMODELS			= "BUSINESSMODELS";

	public static final String	SEQUENCE				= "DATASEQ";

	public static final String	VISIBLE					= "VISIBLE";

	public static final String	TEMPLATENAME			= "TEMPLATENAME";

	public static final String	TEMPLATETYPES			= "TEMPLATETYPES";

	public static final String	SORTFIELD1				= "SORTFIELD1";

	public static final String	SORTVALUE1				= "SORTVALUE1";

	public static final String	SORTFIELD2				= "SORTFIELD2";

	public static final String	SORTVALUE2				= "SORTVALUE2";

	public static final String	SORTFIELD3				= "SORTFIELD3";

	public static final String	SORTVALUE3				= "SORTVALUE3";

	public static final String	GROUPFIELD				= "GROUPFIELD";

	public static final String	POSITION				= "POSITION";

	public static final String	TYPE					= "UITYPE";

	private boolean				isMaster				= false;

	private boolean				isFromClassification	= false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public UIObjectInfo clone()
	{
		return (UIObjectInfo) super.clone();
	}

	/**
	 * @return the Businessmodels
	 */
	public String getBusinessmodels()
	{
		return (String) this.get(BUSINESSMODELS);
	}

	public void setBusinessmodels(String businessmodels)
	{
		this.put(BUSINESSMODELS, businessmodels);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(UINAME);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(UINAME, name);
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	/**
	 * language
	 * 
	 * @return the title
	 */
	public String getTitle(LanguageEnum language)
	{
		if (this.getTitle() == null)
		{
			return null;
		}
		String[] titles = StringUtils.splitString(this.getTitle());
		if (language.getType() < titles.length)
		{
			return titles[language.getType()];
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return the type
	 */
	public UITypeEnum getType()
	{
		return this.get(TYPE) == null ? null : UITypeEnum.valueOf((String) this.get(TYPE));
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(UITypeEnum type)
	{
		this.put(TYPE, type.name());
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible()
	{
		if ((String) this.get(VISIBLE) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(VISIBLE));
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible)
	{
		this.put(VISIBLE, BooleanUtils.getBooleanStringYN(visible));
	}

	/**
	 * @return the reportTemplateName
	 */
	public String getReportTemplateName()
	{
		return (String) this.get(TEMPLATENAME);
	}

	public void setReportTemplateName(String templateName)
	{
		this.put(TEMPLATENAME, templateName);
	}

	public String getTemplateTypes()
	{
		return (String) this.get(TEMPLATETYPES);
	}

	public void setTemplateTypes(String templateTypes)
	{
		this.put(TEMPLATETYPES, templateTypes);
	}

	public String getSortField1()
	{
		return (String) this.get(SORTFIELD1);
	}

	public void setSortField1(String sortField1)
	{
		this.put(SORTFIELD1, sortField1);
	}

	public String getSortValue1()
	{
		return (String) this.get(SORTVALUE1);
	}

	public void setSortValue1(String sortValue1)
	{
		this.put(SORTVALUE1, sortValue1);
	}

	public String getSortField2()
	{
		return (String) this.get(SORTFIELD2);
	}

	public void setSortField2(String sortField2)
	{
		this.put(SORTFIELD2, sortField2);
	}

	public String getSortValue2()
	{
		return (String) this.get(SORTVALUE2);
	}

	public void setSortValue2(String sortValue2)
	{
		this.put(SORTVALUE2, sortValue2);
	}

	public String getSortField3()
	{
		return (String) this.get(SORTFIELD3);
	}

	public void setSortField3(String sortField3)
	{
		this.put(SORTFIELD3, sortField3);
	}

	public String getSortValue3()
	{
		return (String) this.get(SORTVALUE3);
	}

	public void setSortValue3(String sortValue3)
	{
		this.put(SORTVALUE3, sortValue3);
	}

	/**
	 * @return the groupField
	 */
	public String getGroupField()
	{
		return (String) this.get(GROUPFIELD);
	}

	public void setGroupField(String groupField)
	{
		this.put(GROUPFIELD, groupField);
	}

	/**
	 * @return the position
	 */
	public String getPosition()
	{
		return (String) this.get(POSITION);
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(String position)
	{
		this.put(POSITION, position);
	}

	public String getClassGuid()
	{
		return (String) this.get(CLASSFK);
	}

	public void setClassGuid(String classGuid)
	{
		this.put(CLASSFK, classGuid);
	}

	public Boolean isFieldInherited()
	{
		if ((String) this.get(INHERITED) == null)
		{
			return null;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(INHERITED));
	}

	public void setFieldInherited(Boolean isFieldInherited)
	{
		this.put(INHERITED, BooleanUtils.getBooleanStringYN(isFieldInherited));
	}

	public boolean isMaster()
	{
		return this.isMaster;
	}

	public void setMaster(boolean isMaster)
	{
		this.isMaster = isMaster;
	}

	public boolean isFromClassification()
	{
		return this.isFromClassification;
	}

	public void setFromClassification(boolean isFromClassification)
	{
		this.isFromClassification = isFromClassification;
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}
}
