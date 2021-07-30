/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProgressRateEnum
 * wangweixia 2013-10-17
 */
package dyna.common.systemenum.ppms;

/**
 * @author wangweixia
 * 
 */
public enum ProgressRateEnum
{
	PROGRESSSUCCESSFUL("PROGRESSSUCCESSFUL", "ID_CLIENT_PPM_PROJECT_TASK_PROGRESSSUCCESSFUL"), // 进展顺利
	PROGRESSBLOCKED("PROGRESSBLOCKED", "ID_CLIENT_PPM_PROJECT_TASK_PROGRESSBLOCKED"), // 进展受阻
	PROGRESSPOSTPONE("PROGRESSPOSTPONE", "ID_CLIENT_PPM_PROJECT_TASK_PROGRESSPOSTPONE"), // 进展延后
	PROGRESSAHEAD("PROGRESSAHEAD", "ID_CLIENT_PPM_PROJECT_TASK_PROGRESSAHEAD"), // 进展超前
	
	APPISOK("APPISOK","ID_CLIENT_DIALOG_TASK_APP_OK"),//审批通过
	APPISCANCEL("APPISCANCEL","ID_CLIENT_DIALOG_TASK_APP_CANCEL"),//审批驳回

	// 启动、暂停、终止、完成 等状态
	START("START", "ID_SYS_PROGRESSRATEENUM_START"), // 启动
	PAUSE("PAUSE", "ID_SYS_PROGRESSRATEENUM_PAUSE"), // 暂停
	CANCEL("CANCEL", "ID_SYS_PROGRESSRATEENUM_CANCEL"), // 终止
	APP("APP", "ID_SYS_PROGRESSRATEENUM_APP"), //审批中
	FINISH("FINISH", "ID_SYS_PROGRESSRATEENUM_FINISH");// 完成

	private String			value;
	private final String	msrId;

	ProgressRateEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getValue()
	{
		return this.value;
	}

}
