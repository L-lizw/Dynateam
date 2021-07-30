package dyna.common.dto;

import dyna.common.bean.data.SystemObjectImpl;

/**
 * 返回结果为true/false，同时还要求附带详细描述信息；有两个参数，第一个表示结果成功或失败，用true/false表示，第二个是具体的详细信息。
 * <p>
 * 为true的话，第二个参数一般为""
 * <p>
 * 为false的话，则第二个参数一定不能为"", 开发人员可以取这个值展现给用户或进行其它操作
 * <p>
 * 
 * @author chega
 * 
 */
public class BooleanResult extends SystemObjectImpl
{
	private static final long			serialVersionUID	= 6579403788108202356L;
	private boolean						flag				= false;
	private String						detail				= "";
	private boolean						isDataEmpty			= true;
	private final static BooleanResult	NULL_RESULT			= new BooleanResult(true, "");

	public BooleanResult(boolean flag, String detail)
	{
		this.flag = flag;
		this.detail = detail;
	}

	public BooleanResult()
	{
		this(false);
	}

	public BooleanResult(boolean flag)
	{
		this(flag, "");
	}

	public boolean getFlag()
	{
		return flag;
	}

	public void setFlag(boolean flag)
	{
		if (this == NULL_RESULT)
		{
			throw new IllegalArgumentException("can't modify on NULL_RESULT");
		}
		this.flag = flag;
	}

	public String getDetail()
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		if (this == NULL_RESULT)
		{
			throw new IllegalArgumentException("can't modify on NULL_RESULT");
		}
		this.detail = detail;
	}

	public boolean isDataEmpty()
	{
		return isDataEmpty;
	}

	public BooleanResult setDataEmpty(boolean isDataEmpty)
	{
		this.isDataEmpty = isDataEmpty;
		return this;
	}

	@Override
	public String toString()
	{
		return String.valueOf(this.getFlag()) + ", " + this.getDetail();
	}

	/**
	 * 返回一个空的Result, flag为true
	 */
	public static BooleanResult getNull()
	{
		return NULL_RESULT;
	}
}