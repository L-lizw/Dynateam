/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeTypeEnum
 * Jiagang 2010-8-13
 */
package dyna.common.systemenum;

/**
 * @author WangLHB
 * 
 */
public enum ReportTypeEnum
{
	PDF("ID_REPORT_TYPE_PDF"), EXCEL("ID_REPORT_TYPE_EXCEL"), CSV("ID_REPORT_TYPE_CSV"), HTML("ID_REPORT_TYPE_HTML"), WORD(
			"ID_REPORT_TYPE_WORD");

	private String	msrId;

	public String getMsrId()
	{
		return this.msrId;
	}

	private ReportTypeEnum(String msrId)
	{
		this.msrId = msrId;
	}
}
