package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.configparamter.TestHistoryMapper;

/**
 * 驱动测试时，界面参数bean
 * 
 * @author Administrator
 * 
 */
@EntryMapper(TestHistoryMapper.class)
public class TestHistory extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1644282037042871743L;

	/**
	 * 对象guid
	 */
	private static final String	FOGUID				= "FOGUID";

	/**
	 * 用户guid
	 */
	private static final String	USERGUID			= "USERGUID";

	/**
	 * 条件名，为空则默认为"DEFAULT"
	 */
	private static final String	CONDITIONNAME		= "CONDITIONNAME";

	/**
	 * 条件
	 */
	private static final String	CONDITIONS			= "CONDITIONS";

	public String getFoGuid()
	{
		return (String) this.get(FOGUID);
	}

	public void setFoGuid(String foGuid)
	{
		this.put(FOGUID, foGuid);
	}
	
	public String getUserGuid()
	{
		return (String) this.get(USERGUID);
	}

	public void setUserGuid(String userGuid)
	{
		this.put(USERGUID, userGuid);
	}

	public String getConditionName()
	{
		return (String) this.get(CONDITIONNAME);
	}

	public void setConditionName(String conditionName)
	{
		this.put(CONDITIONNAME, conditionName);
	}

	public String getConditions()
	{
		return (String) this.get(CONDITIONS);
	}

	public void setConditions(String conditions)
	{
		this.put(CONDITIONS, conditions);
	}
}
