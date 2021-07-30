package dyna.common.systemenum;

/**
 * JOB队列分组。
 * 
 * @author Daniel
 * 
 */
public enum JobGroupEnum
{
	REPORT("1"),

	ERP("2"),

	OTHER("3"),

	// 驱动
	DRIVE("4");

	private String	jobGroup;

	private JobGroupEnum(String jobGroup)
	{
		this.jobGroup = jobGroup;
	}

	public String getGroup()
	{
		return this.jobGroup;
	}

	public static JobGroupEnum typeValueOf(String jobGroup)
	{
		if (jobGroup == null)
		{
			return OTHER;
		}

		for (JobGroupEnum group : JobGroupEnum.values())
		{
			if (group.getGroup().equals(jobGroup))
			{
				return group;
			}
		}

		return OTHER;
	}
}
