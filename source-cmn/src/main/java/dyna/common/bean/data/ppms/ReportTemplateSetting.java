package dyna.common.bean.data.ppms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.SetUtils;

/**
 * 报表模板自定义
 * 
 * @author duanll
 * 
 */
public class ReportTemplateSetting extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long							serialVersionUID	= -7162528267282073358L;

	// 报表名称
	public static final String							TEMPLATE_NAME		= "NAME";

	// 报表编号
	public static final String							TEMPLATE_ID			= "ID";

	// 定制类型：默认/自定义
	public static final String							CUSTOM_TYPE			= "CUSTOMTYPE";

	// 报表类型
	public static final String							REPORT_TYPE			= "RTTYPE";

	// 模板类型
	public static final String							TEMPLATE_TYPE		= "TEMPLATETYPE";

	// 时间周期
	public static final String							TIME_PERIOD			= "PERIOD";

	// 开始时间
	public static final String							START_TIME			= "STARTTIME";

	// 结束时间
	public static final String							END_TIME			= "ENDTIME";

	private List<ReportTemplateSettingFilter>			filtertestList	= null;

	private List<ReportTemplateSettingField>			fieldList			= null;

	private Map<String, ReportTemplateSettingFilter>	filtertestMap	= null;

	public ReportTemplateSetting()
	{
		this.reset();
	}

	public String getTemplateName()
	{
		return (String) this.get(TEMPLATE_NAME);
	}

	public void setTemplateName(String templateName)
	{
		this.put(TEMPLATE_NAME, templateName);
	}

	public String getTemplateID()
	{
		return (String) this.get(TEMPLATE_ID);
	}

	public void setTemplateID(String templateID)
	{
		this.put(TEMPLATE_ID, templateID);
	}

	public String getCustomType()
	{
		return (String) this.get(CUSTOM_TYPE);
	}

	public void setCustomType(String customType)
	{
		this.put(CUSTOM_TYPE, customType);
	}

	public String getReportType()
	{
		return (String) this.get(REPORT_TYPE);
	}

	public void setReportType(String reportType)
	{
		this.put(REPORT_TYPE, reportType);
	}

	public String getTemplateType()
	{
		return (String) this.get(TEMPLATE_TYPE);
	}

	public void setTemplateType(String templateType)
	{
		this.put(TEMPLATE_TYPE, templateType);
	}

	public String getTimePeriod()
	{
		return (String) this.get(TIME_PERIOD);
	}

	public void setTimePeriod(String period)
	{
		this.put(TIME_PERIOD, period);
	}

	public String getStartTime()
	{
		return (String) this.get(START_TIME);
	}

	public void setStartTime(String startTime)
	{
		this.put(START_TIME, startTime);
	}

	public String getEndTime()
	{
		return (String) this.get(END_TIME);
	}

	public void setEndTime(String endTime)
	{
		this.put(END_TIME, endTime);
	}

	public List<ReportTemplateSettingFilter> getFiltertestList()
	{
		return this.filtertestList;
	}

	public void setFiltertestList(List<ReportTemplateSettingFilter> filtertestList)
	{
		this.filtertestList = filtertestList;
	}

	public List<ReportTemplateSettingField> getFieldList()
	{
		return this.fieldList;
	}

	public void setFieldList(List<ReportTemplateSettingField> fieldList)
	{
		this.fieldList = fieldList;
	}

	public void reset()
	{
		this.filtertestMap = new HashMap<String, ReportTemplateSettingFilter>();
		if (!SetUtils.isNullList(filtertestList))
		{
			for (ReportTemplateSettingFilter filterField : filtertestList)
			{
				if (!this.filtertestMap.containsKey(filterField.gettestKey()))
				{
					this.filtertestMap.put(filterField.gettestKey(), filterField);
				}
			}
		}
	}

	public ReportTemplateSettingFilter getFilterByName(String filtertestName)
	{
		return this.filtertestMap.get(filtertestName);
	}

	public void clearFiltertest()
	{
		if (this.filtertestMap != null)
		{
			this.filtertestMap.clear();
		}
		if (this.filtertestList != null)
		{
			this.filtertestList.clear();
		}
	}

	public void addFiltertest(ReportTemplateSettingFilter filterPropery)
	{
		if (this.filtertestList == null)
		{
			this.filtertestList = new ArrayList<ReportTemplateSettingFilter>();
		}
		this.filtertestList.add(filterPropery);
	}
}
