/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EventScript 事件脚本
 * Wanglei 2011-3-25
 */
package dyna.common.bean.model;

import dyna.common.bean.model.Script;
import dyna.common.systemenum.EventTypeEnum;

/**
 * 事件脚本
 * 
 * @author Wanglei
 * 
 */
public interface EventScript extends Script
{

	/**
	 * 获取事件类型
	 * 
	 * @return
	 */
	public EventTypeEnum getEventType();
}
