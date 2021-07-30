package dyna.common.bean.data.configparamter;

import java.math.BigDecimal;

import dyna.common.bean.data.SystemObject;
import dyna.common.systemenum.ConfigParameterTableType;

/**
 * 
 * 动态列的列头信息
 * 
 * @author wwx
 * 
 */
public class DynamicColumnTitle extends ConfigBase implements SystemObject
{
	private static final long	serialVersionUID	= 7947344135529703180L;
	// tableType 的取值范围：G,La,Lb,A,B,C,D,E,R,Q,F,P,MAK,INPT
	public static final String	TABLETYPE			= "TABLETYPE";
	public static final String	TITLE				= "TITLE";
	public static final String	TITLESEQUENCE		= "TITLESEQUENCE";
	public static final String	UNIQUEVALUE			= "UNIQUEVALUE";
	// 页面使用，不保存
	private boolean				isDelete			= true;
	private boolean				isEditor			= true;

	public ConfigParameterTableType getTableType()
	{
		return ConfigParameterTableType.valueOf((String) this.get(TABLETYPE));
	}

	public void setTableType(ConfigParameterTableType typeEnum)
	{
		this.put(TABLETYPE, typeEnum == null ? null : typeEnum.name());
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public BigDecimal getTitleSequence()
	{
		return (BigDecimal) this.get(TITLESEQUENCE);
	}

	public void setTitleSequence(BigDecimal sequence)
	{
		this.put(TITLESEQUENCE, sequence);
	}

	public void setEditor(boolean isEditor)
	{
		this.isEditor = isEditor;
	}

	public boolean isEditor()
	{
		return isEditor;
	}

	public void setDelete(boolean isDelete)
	{
		this.isDelete = isDelete;
	}

	public boolean isDelete()
	{
		return isDelete;
	}

	public void setUniqueValue(String uniqueValue)
	{
		this.put(UNIQUEVALUE, uniqueValue);
	}

	public String getUniqueValue()
	{
		return (String) this.get(UNIQUEVALUE);
	}
}
