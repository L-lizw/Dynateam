package dyna.data.service.ins;

import java.util.ArrayList;
import java.util.List;

import dyna.common.systemenum.SystemStatusEnum;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class SystemStatusChangeBean
{
	private SystemStatusEnum					from;

	private SystemStatusEnum					to;

	private String[]							clearFields;

	private String[]							setValsFields;

	private static List<SystemStatusChangeBean>	statusChangeOptions	= new ArrayList<>();

	static
	{
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.WIP, SystemStatusEnum.PRE, null, null));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.WIP, SystemStatusEnum.RELEASE, null, new String[] { "releasetime" }));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.WIP, SystemStatusEnum.ECP, null, null));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.WIP, SystemStatusEnum.OBSOLETE, null, new String[] { "obsoletetime", "obsoleteuser" }));

		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.PRE, SystemStatusEnum.WIP, null, null));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.PRE, SystemStatusEnum.RELEASE, null, new String[] { "releasetime" }));

		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.RELEASE, SystemStatusEnum.OBSOLETE, null, new String[] { "obsoletetime", "obsoleteuser" }));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.RELEASE, SystemStatusEnum.WIP, new String[] { "releasetime" }, null));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.RELEASE, SystemStatusEnum.ECP, null, null));

		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.ECP, SystemStatusEnum.RELEASE, null, new String[] { "releasetime" }));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.ECP, SystemStatusEnum.WIP, null, null));
		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.ECP, SystemStatusEnum.OBSOLETE, null, new String[] { "obsoletetime", "obsoleteuser" }));

		statusChangeOptions.add(new SystemStatusChangeBean(SystemStatusEnum.OBSOLETE, SystemStatusEnum.RELEASE, new String[] { "obsoletetime", "obsoleteuser" }, null));
	}

	public SystemStatusChangeBean(SystemStatusEnum from, SystemStatusEnum to)
	{
		this.from = from;
		this.to = to;
	}

	private SystemStatusChangeBean(SystemStatusEnum from, SystemStatusEnum to, String[] clearFields, String[] setValsFields)
	{
		this.from = from;
		this.to = to;
		this.clearFields = clearFields;
		this.setValsFields = setValsFields;
	}

	public SystemStatusEnum getFrom()
	{
		return from;
	}

	public void setFrom(SystemStatusEnum from)
	{
		this.from = from;
	}

	public SystemStatusEnum getTo()
	{
		return to;
	}

	public void setTo(SystemStatusEnum to)
	{
		this.to = to;
	}

	public String[] getClearFields()
	{
		return clearFields;
	}

	public void setClearFields(String[] clearFields)
	{
		this.clearFields = clearFields;
	}

	public String[] getSetValsFields()
	{
		return setValsFields;
	}

	public void setSetValsFields(String[] setValsFields)
	{
		this.setValsFields = setValsFields;
	}

	public SystemStatusChangeBean getStatusChangeRule()
	{
		for (SystemStatusChangeBean rule : statusChangeOptions)
		{
			if (rule.getFrom() == this.getFrom() && rule.getTo() == this.getTo())
			{
				return rule;
			}
		}
		return null;
	}
}
