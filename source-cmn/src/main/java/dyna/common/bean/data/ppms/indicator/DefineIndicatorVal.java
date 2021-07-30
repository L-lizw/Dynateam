package dyna.common.bean.data.ppms.indicator;

import java.io.Serializable;

/**
 * 预定义的指标值
 * 
 * @author duanll
 * 
 */
public class DefineIndicatorVal implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6313693090752005033L;

	private String				id;

	private String				name;

	private Dismension			dismension;

	// 为false时，说明在选择年，季度，半年等非月周期时，不需要把周期内的月相累加
	// 即：当前月的数据，即为当前年的数据，也是当前半年，当前季度的数据
	private boolean				accumulate;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Dismension getDismension()
	{
		return dismension;
	}

	public void setDismension(Dismension dismension)
	{
		this.dismension = dismension;
	}

	public boolean isAccumulate()
	{
		return accumulate;
	}

	public void setAccumulate(boolean accumulate)
	{
		this.accumulate = accumulate;
	}
}
