/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileOpenToolTypeEnum
 * wangweixia 2012-12-4
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 *         文件打开浏览工具：工具类型值的选项
 * 
 */
public enum FileOpenToolTypeEnum
{
	NETVERSION("0", "ID_SYS_FILEOPENTOOLTYPEENUM_NETVERSION"), // AutoVue 网络版
	DESKTOPVERSION("1", "ID_SYS_FILEOPENTOOLTYPEENUM_DESKTOPVERSION"),// AutoVue 桌面版
	INTERVUE("2", "ID_SYS_FILEOPENTOOLTYPEENUM_INTERVUE"),// InterVue
	DIGIWINPDF("3", "ID_SYS_FILEOPENTOOLTYPEENUM_DIGIWINPDF");// DigiWinPDF
	private String			value;
	private final String	msrId;

	private FileOpenToolTypeEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	public static FileOpenToolTypeEnum typeValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (FileOpenToolTypeEnum toolType : FileOpenToolTypeEnum.values())
		{
			if (value.equals(toolType.getValue()))
			{
				return toolType;
			}
		}

		return null;
	}
}
