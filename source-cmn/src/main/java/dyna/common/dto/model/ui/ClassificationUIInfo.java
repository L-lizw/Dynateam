package dyna.common.dto.model.ui;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

@Cache
public class ClassificationUIInfo extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	public static final long	serialVersionUID	= 2051604070981568833L;

	public static final String	CLASSIFICATIONFK	= "CLASSIFICATIONFK";

	public static final String	TITLE				= "TITLE";

	public static final String	INHERITED			= "INHERITED";

	public static final String	NAME				= "UINAME";

	public static final String	VISIBLE				= "VISIBLE";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	TYPE				= "UITYPE";

	public String getClassificationGuid()
	{
		return (String) this.get(CLASSIFICATIONFK);
	}

	public void setClassificationGuid(String classificationGuid)
	{
		this.put(CLASSIFICATIONFK, classificationGuid);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public String getName()
	{
		return (String) this.get(NAME);
	}

	public void setName(String uiName)
	{
		this.put(NAME, uiName);
	}

	public Boolean isInherited()
	{
		if ((String) this.get(INHERITED) == null)
		{
			return null;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(INHERITED));
	}

	public void setInherited(Boolean isInherited)
	{
		this.put(INHERITED, BooleanUtils.getBooleanStringYN(isInherited));
	}

	public boolean isVisible()
	{
		if ((String) this.get(VISIBLE) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(VISIBLE));
	}

	public void setVisible(boolean isVisible)
	{
		this.put(VISIBLE, BooleanUtils.getBooleanStringYN(isVisible));
	}

	public String getType()
	{
		return (String) this.get(TYPE);
	}

	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}
}
