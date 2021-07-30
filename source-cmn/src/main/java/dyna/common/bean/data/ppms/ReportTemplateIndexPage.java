package dyna.common.bean.data.ppms;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 报表模板自定义显示字段
 * 
 * @author duanll
 * 
 */
public class ReportTemplateIndexPage extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1639424887974548708L;

	public static final String	TEMPLATEID			= "TEMPLATEID";

	public static final String	USERGUID			= "USERGUID";

	public String getTemplateId()
	{
		return (String) this.get(TEMPLATEID);
	}

	public void setTemplateId(String templateId)
	{
		this.put(TEMPLATEID, templateId);
	}

	public String getUserGuid()
	{
		return (String) this.get(USERGUID);
	}

	public void setUserGuid(String userGuid)
	{
		this.put(USERGUID, userGuid);
	}
}
