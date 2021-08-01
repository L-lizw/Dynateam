/**
 * Copyright(C) DCIS 版权所有。
 * 功能描述：data common object definitions
 * 创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.dto.model.ui;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.ui.UIFieldMapper;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;

@Cache
@EntryMapper(UIFieldMapper.class)
public class UIField extends SystemObjectImpl implements SystemObject
{
	public static final long	serialVersionUID	= -3770117130669035651L;

	public static final String	UIGUID				= "UIGUID";

	public static final String	FIELDGUID			= "FIELDGUID";

	public static final String	FIELDNAME			= "FIELDNAME";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	TITLE				= "TITLE";

	public static final String	SHOWTITLE			= "SHOWTITLE";

	public static final String	READONLY			= "READONLY";

	public static final String	FROZE				= "FROZE";

	public static final String	WIDTH				= "WIDTH";

	public static final String	HEIGHT				= "HEIGHT";

	public static final String	ROWAMOUNT			= "ROWAMOUNT";

	public static final String	COLUMNAMOUNT		= "COLUMAMOUNT";

	public static final String	COLUMNSPAN			= "COLUMNSPAN";

	public static final String	COLUMNNAME			= "COLUMNNAME";

	public static final String	NOTINHERIT			= "NOTINHERIT";

	public static final String	ISMANDATORY			= "MANDATORY";

	public static final String	FORMAT				= "FORMAT";

	public static final String	SHOWVALWHENNOAUTH	= "SHOWVALWHENNOAUTH";

	private static final String	SEPARATOR_DOLLAR	= "SEPARATOR$";

	private FieldTypeEnum		type				= null;

	private String				classGuid			= null;

	public String				multiclass			= null;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public UIField clone()
	{
		return (UIField) super.clone();
	}

	/**
	 * @return the type
	 */
	public FieldTypeEnum getType()
	{
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FieldTypeEnum type)
	{
		this.type = type;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(FIELDNAME);
	}

	/**
	 * language
	 *
	 * @return the title
	 */
	public String getTitle(LanguageEnum language)
	{
		if (this.getTitle() == null)
		{
			return null;
		}

		String[] titles = StringUtils.splitString(this.getTitle());
		if (language.getType() < titles.length)
		{
			return titles[language.getType()];
		}
		else
		{
			return null;
		}
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(FIELDNAME, name);
	}

	/**
	 * @param uiTitle
	 *            the uiTitle to set
	 */
	public void setUiTitle(String uiTitle)
	{
		this.setTitle(uiTitle);
	}

	/**
	 * @return the uiTitle
	 */
	public String getUiTitle()
	{
		return this.getTitle();
	}

	public String getCell()
	{
		return this.getColumnAmount() + "," + this.getRowAmount();
	}

	public String getClassGuid()
	{
		return classGuid;
	}

	public void setClassGuid(String classGuid)
	{
		this.classGuid = classGuid;
	}

	public void setMulticlass(String multiclass)
	{
		this.multiclass = multiclass;
	}

	public String getMulticlass()
	{
		return multiclass;
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public String getUIGuid()
	{
		return (String) this.get(UIGUID);
	}

	public void setUIGuid(String uiGuid)
	{
		this.put(UIGUID, uiGuid);
	}

	public String getFieldGuid()
	{
		return (String) this.get(FIELDGUID);
	}

	public void setFieldGuid(String fieldGuid)
	{
		this.put(FIELDGUID, fieldGuid);
	}

	public Integer getSequence()
	{
		return this.get(SEQUENCE) == null ? null : ((Number) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, sequence == null ? 0 : new BigDecimal(sequence.toString()));
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

	public String getWidth()
	{
		return (String) this.get(WIDTH);
	}

	public void setWidth(String sequence)
	{
		this.put(WIDTH, sequence);
	}

	public String getHeight()
	{
		return (String) this.get(HEIGHT);
	}

	public void setHeight(String height)
	{
		this.put(HEIGHT, height);
	}

	public Integer getRowAmount()
	{
		return this.get(ROWAMOUNT) == null ? 1 : Integer.valueOf((String) this.get(ROWAMOUNT));
	}

	public void setRowAmount(Integer rowAmount)
	{
		this.put(ROWAMOUNT, String.valueOf(rowAmount));
	}

	public Integer getColumnAmount()
	{
		return this.get(COLUMNAMOUNT) == null ? 1 : Integer.valueOf((String) this.get(COLUMNAMOUNT));
	}

	public void setColumnAmount(Integer columAmount)
	{
		this.put(COLUMNAMOUNT, String.valueOf(columAmount));
	}

	public Integer getColumnSpan()
	{
		return this.get(COLUMNSPAN) == null ? 1 : Integer.valueOf((String) this.get(COLUMNSPAN));
	}

	public void setColumnSpan(Integer columSpan)
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
		if ((String) this.get(NOTINHERIT) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(NOTINHERIT));
	}

	public void setNotInherit(boolean isNotInherit)
	{
		this.put(NOTINHERIT, BooleanUtils.getBooleanStringYN(isNotInherit));
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

	public boolean isSeparator()
	{
		return getName().contains(SEPARATOR_DOLLAR);
	}
}
