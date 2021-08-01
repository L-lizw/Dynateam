package dyna.common.dto.model.ui;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.ui.ClassificationUIFieldMapper;
import dyna.common.util.BooleanUtils;

import java.math.BigDecimal;

@Cache
@EntryMapper(ClassificationUIFieldMapper.class)
public class ClassificationUIField extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	public static final long	serialVersionUID		= 3825087609621335691L;

	public static final String	CLASSIFICATIONUIFK		= "CLASSIFICATIONUIFK";

	public static final String	CLASSIFICATIONFIELDGUID	= "CLASSIFICATIONFIELDGUID";

	public static final String	FIELDNAME				= "FIELDNAME";

	public static final String	TITLE					= "TITLE";

	public static final String	SEQUENCE				= "DATASEQ";

	public static final String	SHOWTITLE				= "SHOWTITLE";

	public static final String	READONLY				= "READONLY";

	public static final String	FROZE					= "FROZE";

	public static final String	WIDTH					= "WIDTH";

	public static final String	HEIGHT					= "HEIGHT";

	public static final String	ROWAMOUNT				= "ROWAMOUNT";

	public static final String	COLUMAMOUNT				= "COLUMAMOUNT";

	public static final String	COLUMNSPAN				= "COLUMNSPAN";

	public static final String	COLUMNNAME				= "COLUMNNAME";

	public static final String	NOTINHERITED			= "NOTINHERITED";

	public static final String	ISMANDATORY				= "MANDATORY";

	public static final String	FORMAT					= "FORMAT";

	public static final String	SHOWVALWHENNOAUTH		= "SHOWVALWHENNOAUTH";

	public String getUIGuid()
	{
		return (String) this.get(CLASSIFICATIONUIFK);
	}

	public void setUIGuid(String uiGuid)
	{
		this.put(CLASSIFICATIONUIFK, uiGuid);
	}

	public String getFieldGuid()
	{
		return (String) this.get(CLASSIFICATIONFIELDGUID);
	}

	public void setFieldGuid(String fieldGuid)
	{
		this.put(CLASSIFICATIONFIELDGUID, fieldGuid);
	}

	public String getFieldName()
	{
		return (String) this.get(FIELDNAME);
	}

	public void setFieldName(String name)
	{
		this.put(FIELDNAME, name);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public Integer getSequence()
	{
		return this.get(SEQUENCE) == null ? null : ((Number) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, sequence == null ? new BigDecimal("0") : new BigDecimal(sequence.toString()));
	}

	public boolean isShowTitle()
	{
		if ((String) this.get(SHOWTITLE) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(SHOWTITLE));
	}

	public void setShowTitle(boolean showTitle)
	{
		this.put(SHOWTITLE, BooleanUtils.getBooleanStringYN(showTitle));
	}

	public boolean isReadOnly()
	{
		if ((String) this.get(READONLY) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(READONLY));
	}

	public void setReadOnly(boolean isReadOnly)
	{
		this.put(READONLY, BooleanUtils.getBooleanStringYN(isReadOnly));
	}

	public boolean isFroze()
	{
		if ((String) this.get(FROZE) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(FROZE));
	}

	public void setFroze(boolean isFroze)
	{
		this.put(FROZE, BooleanUtils.getBooleanStringYN(isFroze));
	}

	public boolean isShowValWhenNoAuth()
	{
		if ((String) this.get(SHOWVALWHENNOAUTH) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(SHOWVALWHENNOAUTH));
	}

	public void setShowValWhenNoAuth(boolean isShowValWhenNoAuth)
	{
		this.put(SHOWVALWHENNOAUTH, BooleanUtils.getBooleanStringYN(isShowValWhenNoAuth));
	}

	public int getWidth()
	{
		return this.get(WIDTH) == null ? 0 : Integer.valueOf((String) this.get(WIDTH));
	}

	public void setWidth(int width)
	{
		this.put(WIDTH, String.valueOf(width));
	}

	public int getHeight()
	{
		return this.get(HEIGHT) == null ? 0 : Integer.valueOf((String) this.get(HEIGHT));
	}

	public void setHeight(int height)
	{
		this.put(HEIGHT, String.valueOf(height));
	}

	public int getRowAmount()
	{
		return this.get(ROWAMOUNT) == null ? 0 : Integer.valueOf((String) this.get(ROWAMOUNT));
	}

	public void setRowAmount(int rowAmount)
	{
		this.put(ROWAMOUNT, String.valueOf(rowAmount));
	}

	public int getColumAmount()
	{
		return this.get(COLUMAMOUNT) == null ? 0 : Integer.valueOf((String) this.get(COLUMAMOUNT));
	}

	public void setColumAmount(int columAmount)
	{
		this.put(COLUMAMOUNT, String.valueOf(columAmount));
	}

	public int getColumSpan()
	{
		return this.get(COLUMNSPAN) == null ? 0 : Integer.valueOf((String) this.get(COLUMNSPAN));
	}

	public void setColumSpan(int columSpan)
	{
		this.put(COLUMNSPAN, String.valueOf(columSpan));
	}

	public String getColumName()
	{
		return (String) this.get(COLUMNNAME);
	}

	public void setColumName(String columName)
	{
		this.put(COLUMNNAME, columName);
	}

	public boolean isNotInherit()
	{
		if ((String) this.get(NOTINHERITED) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(NOTINHERITED));
	}

	public void setNotInherit(boolean isNotInherit)
	{
		this.put(NOTINHERITED, BooleanUtils.getBooleanStringYN(isNotInherit));
	}

	public String getFormat()
	{
		return (String) this.get(FORMAT);
	}

	public void setFormat(String format)
	{
		this.put(FORMAT, format);
	}

	public boolean isMandatory()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(ISMANDATORY));
	}

	public void setMandatory(boolean isMandatory)
	{
		this.put(ISMANDATORY, BooleanUtils.getBooleanStringYN(isMandatory));
	}
}
