/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.dto.model.ui;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.ui.UIActionMapper;
import dyna.common.util.BooleanUtils;

@Cache
@EntryMapper(UIActionMapper.class)
public class UIAction extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 883480344725899251L;

	public static final String	UIGUID				= "UIGUID";

	public static final String	ACTIONGUID			= "ACTIONGUID";

	public static final String	ACTIONNAME			= "ACTIONNAME";

	public static final String	TITLE				= "TITLE";

	public static final String	ISNOTINHERIT		= "NOTINHERIT";

	/**
	 * @return the name
	 */
	public String getName()
	{
		return (String) this.get(ACTIONNAME);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.put(ACTIONNAME, name);
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	/**
	 * @param title
	 *            the title to set
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public UIAction clone()
	{
		return (UIAction) super.clone();
	}

	/**
	 * @param isNotInherit
	 *            the isNotInherit to set
	 */
	public void setNotInherit(boolean isNotInherit)
	{
		this.put(ISNOTINHERIT, BooleanUtils.getBooleanStringYN(isNotInherit));
	}

	/**
	 * @return the isNotInherit
	 */
	public boolean isNotInherit()
	{
		return this.get(ISNOTINHERIT) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISNOTINHERIT));
	}
}
