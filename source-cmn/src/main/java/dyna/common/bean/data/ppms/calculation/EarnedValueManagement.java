package dyna.common.bean.data.ppms.calculation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.StringUtils;

/**
 * 项目管理中的挣值管理
 * SPI AC CPI
 * 
 * @author wwx
 * 
 */
public class EarnedValueManagement
{
	private PPMFoundationObjectUtil	ppmUtil	= null;

	public void setPpmUtil(PPMFoundationObjectUtil ppmUtil)
	{
		this.ppmUtil = ppmUtil;
	}

	public void calculateSPIAndCPI(PMCalendar workCalendar, Date now, Double workHours, boolean isLeaf)
	{
		if (this.ppmUtil != null)
		{
			this.calculatePV(workCalendar, now, workHours, isLeaf);
			this.calculateAC(workCalendar, now, workHours, isLeaf);
			this.calculateCPI();
			this.calculateSPI(workCalendar, now, workHours, isLeaf);
		}
	}

	public void calculateAC(PMCalendar workCalendar, Date now, Double workHours, boolean isLeaf)
	{
		if (this.ppmUtil != null)
		{
			Double planWorkload = this.ppmUtil.getPlanWorkload();
			if (planWorkload == null)
			{
				planWorkload = this.ppmUtil.getPlannedDuration() * workHours;
			}
			Date planStartTime = this.ppmUtil.getActualStartTime();
			double planWorkloadT = 0;
			double durationDay = 0;
			if (planStartTime != null)
			{
				if (isLeaf)
				{
					if (this.ppmUtil.getPlannedDuration() != 0)
					{
						if (this.ppmUtil.getActualFinishTime() == null)
						{
							durationDay = workCalendar.getDurationDay(this.ppmUtil.getActualStartTime(), now);
						}
						else
						{
							durationDay = workCalendar.getDurationDay(this.ppmUtil.getActualStartTime(), this.ppmUtil.getActualFinishTime()) + 1;
						}
						planWorkloadT = ((planWorkload * (durationDay / this.ppmUtil.getPlannedDuration())));
					}
					if (planWorkloadT < 0)
					{
						planWorkloadT = 0d;
					}
					this.ppmUtil.setAC(planWorkloadT);
				}
			}
		}
	}

	public void calculatePV(PMCalendar workCalendar, Date now, Double workHours, boolean isLeaf)
	{
		if (this.ppmUtil != null)
		{
			Double planWorkload = this.ppmUtil.getPlanWorkload();
			if (planWorkload == null)
			{
				planWorkload = this.ppmUtil.getPlannedDuration() * workHours;
			}
			Date planStartTime = this.ppmUtil.getPlanStartTime();
			double planWorkloadT = 0;
			double durationDay = 0;
			if (planStartTime == null)
			{
				return;
			}
			if (this.ppmUtil.getTaskStatusEnum() == TaskStatusEnum.SSP)
			{
				planWorkloadT = this.ppmUtil.getActualWorkload();
			}
			else
			{
				if (isLeaf)
				{
					if (this.ppmUtil.getPlannedDuration() != 0)
					{
						if (this.ppmUtil.getActualFinishTime() == null)
						{
							durationDay = workCalendar.getDurationDay(planStartTime, now);
						}
						else
						{
							durationDay = workCalendar.getDurationDay(planStartTime, this.ppmUtil.getActualFinishTime()) + 1;
						}
						planWorkloadT = ((planWorkload * (durationDay / this.ppmUtil.getPlannedDuration())));
					}
				}
				else
				{
					planWorkloadT = this.ppmUtil.getPlanCompleteWorkload();
					if (planWorkloadT>planWorkload)
					{
						if (this.ppmUtil.getPlanFinishTime()!=null)
						{
							int i=workCalendar.getDurationDay(this.ppmUtil.getPlanFinishTime(), now);
							if (i>1)
							{
								double summaryPV = getSummaryTaskPV(planWorkload, planStartTime, workCalendar, now, workHours);
								planWorkloadT = Math.min(planWorkloadT, summaryPV);
							}
							else
							{
								planWorkloadT=planWorkload;
							}
						}
					}
				}
			}
			if (planWorkloadT < 0)
			{
				planWorkloadT = 0;
			}
			this.ppmUtil.setPlanCompleteWorkload(planWorkloadT);
		
		}
	}

	private double getSummaryTaskPV(Double planWorkload, Date planStartTime, PMCalendar workCalendar, Date now, Double workHours)
	{
		double durationDay = 0;
		double planWorkloadT = 0;
		if (this.ppmUtil.getPlannedDuration() != 0)
		{
			if (this.ppmUtil.getActualFinishTime() == null)
			{
				durationDay = workCalendar.getDurationDay(planStartTime, now);
			}
			else
			{
				durationDay = workCalendar.getDurationDay(planStartTime, this.ppmUtil.getActualFinishTime()) + 1;
			}
			planWorkloadT = ((planWorkload * (durationDay / this.ppmUtil.getPlannedDuration())));
		}
		if (planWorkloadT < 0)
		{
			planWorkloadT = 0;
		}
		return planWorkloadT;
	}

	public void calculateCPI()
	{
		if (this.ppmUtil.getAC() > 0)
		{
			DecimalFormat df = new DecimalFormat("#.00");
			double value = this.ppmUtil.getActualWorkload() / this.ppmUtil.getAC();
			this.ppmUtil.setCPI(BigDecimal.valueOf(new Double(df.format(value))));
		}
	}

	public void calculateSPI(PMCalendar workCalendar, Date now, Double workHours, boolean isLeaf)
	{
		// spi = 实际完成工作量/计划应该完成工作量 = 实际工作量/(计划工作量 * （计划已过周期/计划总周期）)
		// spi>1 进度超前， spi=1 进度正好 ,spi<1进度延迟
		DecimalFormat df = new DecimalFormat("#.00");
		double planWorkloadT = this.ppmUtil.getPlanCompleteWorkload();
		if (this.ppmUtil.getTaskStatusEnum() == TaskStatusEnum.SSP)
		{
			this.ppmUtil.setSPI(1D);
		}
		else if (planWorkloadT == 0)
		{
			if (this.ppmUtil.getActualStartTime() != null)
			{
				this.ppmUtil.setSPI(PMConstans.DEFALUT_SPI);
			}
			else if (this.ppmUtil.getTaskStatusEnum() == TaskStatusEnum.INI)
			{
				this.ppmUtil.setSPI(PMConstans.DEFALUT_SPI);
			}
		}
		else
		{
			if (this.ppmUtil.getActualStartTime() == null)
			{
				if (StringUtils.isNullString(this.ppmUtil.getParentTask().getGuid()) == false || isLeaf)
				{
					this.ppmUtil.setSPI(0d);
				}
			}
			else
			{
				this.ppmUtil.setSPI(new Double(df.format(this.ppmUtil.getActualWorkload() / planWorkloadT)));
			}
		}
		if (this.ppmUtil.getSPI() != null && this.ppmUtil.getSPI() > PMConstans.DEFALUT_SPI)
		{
			this.ppmUtil.setSPI(PMConstans.DEFALUT_SPI);
		}
	}

}
