/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LaborHourConfig
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import java.math.BigDecimal;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

/**
 * 工时基本参数设置
 * 
 * @author wangweixia
 * 
 */
public class LaborHourConfig extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= 6369831918624479080L;
	// 标准工时
	public static final String	STANDARDHOUR			= "STANDARDHOUR";

	// 超负荷工时
	public static final String	OVERLOADHOUR			= "OVERLOADHOUR";

	// 低负荷工时
	public static final String	UNDERLOADHOUR			= "UNDERLOADHOUR";

	// 下月冻结工时日期:此不为空时，页面的下月是否冻结上月填报为勾上
	public static final String	FREEZETIME				= "FREEZETIME";

	// 是否把工时存为个人日志
	public static final String	ISSAVEASPERSONALLOG		= "ISSAVEASPERSONALLOG";

	// 能否修改审核后的工时
	public static final String	ISMODIFICATIONAUDITHOUR	= "ISMODIFICATIONAUDITHOUR";

	/**
	 * @return the standardhour
	 */
	public Double getStandardhour()
	{
		Number b = (Number) super.get(STANDARDHOUR);
		return b == null ? 0 : b.doubleValue();
	}

	public void setStandardhour(Double standardhour)
	{
		super.put(STANDARDHOUR, (standardhour == null ? null : BigDecimal.valueOf(standardhour)));
	}

	/**
	 * @return the overloadhour
	 */
	public Double getOverloadhour()
	{
		Number b = (Number) super.get(OVERLOADHOUR);
		return b == null ? 0 : b.doubleValue();
	}

	public void setOverloadhour(Double overloadhour)
	{
		super.put(OVERLOADHOUR, (overloadhour == null ? null : BigDecimal.valueOf(overloadhour)));
	}

	/**
	 * @return the underloadhour
	 */
	public Double getUnderloadhour()
	{
		Number b = (Number) super.get(UNDERLOADHOUR);
		return b == null ? 0 : b.doubleValue();
	}

	public void setUnderloadhour(Double underloadhour)
	{
		super.put(UNDERLOADHOUR, (underloadhour == null ? null : BigDecimal.valueOf(underloadhour)));
	}

	/**
	 * @return the freezetime
	 */
	public Double getFreezetime()
	{
		Number b = (Number) super.get(FREEZETIME);
		return b == null ? 0 : b.doubleValue();
	}

	public void setFreezetime(Double freezetime)
	{
		super.put(FREEZETIME, (freezetime == null ? null : BigDecimal.valueOf(freezetime)));
	}

	/**
	 * @return the issaveaspersonallog
	 */
	public boolean isSaveAsPersonalLog()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISSAVEASPERSONALLOG));
		return ret == null ? false : ret.booleanValue();
	}

	public void setSaveAsPersonalLog(boolean saveAsPersonalLog)
	{
		this.put(ISSAVEASPERSONALLOG, BooleanUtils.getBooleanStringYN(saveAsPersonalLog));
	}

	/**
	 * @return the ismodificationaudithour
	 */
	public boolean isModificationAudithour()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISMODIFICATIONAUDITHOUR));
		return ret == null ? false : ret.booleanValue();
	}

	public void setModificationAudithour(boolean modificationAudithour)
	{
		this.put(ISMODIFICATIONAUDITHOUR, BooleanUtils.getBooleanStringYN(modificationAudithour));
	}

	/**
	 * 仅页面用
	 * 下月是否冻结上月填报
	 * 
	 * @return
	 */
	public boolean isFreeze()
	{
		if (this.getFreezetime() != null)
		{
			return true;
		}
		return false;
	}
}
