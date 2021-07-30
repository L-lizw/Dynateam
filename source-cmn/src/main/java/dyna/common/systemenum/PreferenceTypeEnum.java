/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Preference类型枚举
 * caogc 2010-10-08
 */
package dyna.common.systemenum;

/**
 * Preference类型枚举
 * 
 * @author caogc
 * 
 */
public enum PreferenceTypeEnum
{
	MAXHISTORY("maxhistory", "02"), ROWCOUNT("rowcount", "03"), COMMONLIB("commonlib", "04"), COMMONCLASS(
			"commonclass", "05"), BIVIEWHISCOUNT("biviewhiscount", "00"), RECEIVEEMAIL("receiveemail", "06"), NOTICEREFRESHINTERVAL(
			"noticerefreshinterval", "07"), SHOWLOGINWARN("showloginwarn", "08"), FILEPREFERENCE("filepreference", "09"), WOREFLOWPREFERENCE(
			"workflowpreference", "10"), CODERULE("coderule", "11"), LEFTRIGHTLAYOUT("leftrightlayout", "12"), LEFTPROPORTION(
			"leftproportion", "13"), OPENONLYONE("openonlyone", "14"), MESSAGECLEARCYCLE("messageclearcycle", "15"), CUSTOMCONFIG(
			"CUSTOMCONFIG", "16"), WORKFLOWCLEARCYCLE("WORKFLOWCLEARCYCLE", "17"), QUERYSETTING("QUERYSETTING", "18"),
	ADVANCESEARCH("ADVANCESEARCH", "19"),QUICKSEARCHDIALOG("QUICKSEARCHDIALOG", "20");
	private final String	type;
	private String			value;

	private PreferenceTypeEnum(String type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	@Override
	public String toString()
	{
		return this.type;
	}
}
