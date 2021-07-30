/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Event类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Event类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum EventTypeEnum
{
	ADD_BEFORE("add.before"), //
	ADD_AFTER("add.after"), //
	UPDATE_BEFORE("update.before"), //
	UPDATE_AFTER("update.after"), //
	DELETE_BEFORE("delete.before"), //
	DELETE_AFTER("delete.after"), //
	EFFECT_BEFORE("effect.before"), //
	EFFECT_AFTER("effect.after"), //
	OBS_BEFORE("obs.before"), //
	OBS_AFTER("obs.after"), //
	REVISE_BEFORE("revise.before"), //
	REVISE_AFTER("revise.after"), //
	CHECKIN_BEFORE("checkin.before"), //
	CHECKIN_AFTER("checkin.after"), //
	CHECKOUT_BEFORE("checkout.before"), //
	CHECKOUT_AFTER("checkout.after"), //
	SUBMITTOLIB_BEFORE("submittolib.before"), //
	SUBMITTOLIB_AFTER("submittolib.after"), //

	START_BEFORE("start.before"), //
	START_AFTER("start.after");//

	private final String	type;

	@Override
	public String toString()
	{
		return this.type;
	}

	private EventTypeEnum(String type)
	{
		this.type = type;
	}

	public static EventTypeEnum typeValueOf(String type)
	{
		type = type.replace('.', '_');
		return valueOf(type.toUpperCase());
	}

	public static EventTypeEnum[] getPhaseChangeEvent()
	{
		return new EventTypeEnum[] { ADD_AFTER, EFFECT_AFTER, OBS_AFTER, REVISE_AFTER, CHECKIN_AFTER, CHECKOUT_AFTER,
				SUBMITTOLIB_AFTER };
	}

	public static EventTypeEnum[] getWorkflowEvent()
	{
		return new EventTypeEnum[] { ADD_BEFORE, ADD_AFTER, START_BEFORE, START_AFTER };
	}

}
