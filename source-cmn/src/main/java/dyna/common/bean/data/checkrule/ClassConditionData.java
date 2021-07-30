package dyna.common.bean.data.checkrule;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class ClassConditionData extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long				serialVersionUID	= -199088636887601262L;

	public static final String				CLASSNAME			= "CLASSNAME";

	public static final String				CONDITIONNAME		= "CONDITIONNAME";

	public static final String				RULEGUID			= "RULEGUID";

	private List<ClassConditionDetailData>	detailDataList		= null;

	private String							conditionDesc		= null;

	public String getClassName()
	{
		return (String) this.get(CLASSNAME);
	}

	public void setClassName(String className)
	{
		this.put(CLASSNAME, className);
	}
	
	@Override
	public String getName()
	{
		return (String) this.get(CONDITIONNAME);
	}

	@Override
	public void setName(String conditionName)
	{
		this.put(CONDITIONNAME, conditionName);
	}

	public List<ClassConditionDetailData> getDetailDataList()
	{
		return detailDataList;
	}

	public void setDetailDataList(List<ClassConditionDetailData> detailDataList)
	{
		this.detailDataList = detailDataList;
	}

	public String getConditionDesc()
	{
		return conditionDesc;
	}

	public void setConditionDesc(String conditionDesc)
	{
		this.conditionDesc = conditionDesc;
	}

}
