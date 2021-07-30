package dyna.common.bean.data.ppms.indicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 维度
 * 
 * @author duanll
 * 
 */
public class Dismension implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6996451541991071166L;

	public static final String		EMPTY_DISMENSION	= "XXXXX@@#@#";

	private String					name				= null;

	private List<DismensionField>	dismensionFieldList	= new ArrayList<DismensionField>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<DismensionField> getDismensionFieldList()
	{
		return dismensionFieldList;
	}

	public void setDismensionFieldList(List<DismensionField> dismensionFieldList)
	{
		this.dismensionFieldList = dismensionFieldList;
	}
}
