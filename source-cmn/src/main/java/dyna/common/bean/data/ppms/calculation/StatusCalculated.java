package dyna.common.bean.data.ppms.calculation;

import java.util.Date;

import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.systemenum.ppms.OnTimeStateEnum;
import dyna.common.systemenum.ppms.OperationStateEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.PMConstans;

/**
 * 状态计算
 * 准时状态,操作状态
 * 
 * @author wwx
 * 
 */
public class StatusCalculated
{
	private PPMFoundationObjectUtil	ppmUtil	= null;

	public void setPpmUtil(PPMFoundationObjectUtil ppmUtil)
	{
		this.ppmUtil = ppmUtil;
	}

	public OperationStateEnum calculateTaskOperationState(PPMFoundationObjectUtil preUtil, TaskDependEnum depend)
	{
		OperationStateEnum operation = null;
		if (this.ppmUtil != null)
		{
			if (this.ppmUtil.getTaskStatusEnum() == TaskStatusEnum.INI)
			{
				// 后置任务未启动
				if (depend == TaskDependEnum.START_START)
				{
					if (preUtil.getTaskStatusEnum() == TaskStatusEnum.RUN || preUtil.getTaskStatusEnum() == TaskStatusEnum.PUS || preUtil.getTaskStatusEnum() == TaskStatusEnum.SSP
							|| preUtil.getTaskStatusEnum() == TaskStatusEnum.COP)
					{
						operation = OperationStateEnum.START;
					}
					else if (preUtil.getTaskStatusEnum() == TaskStatusEnum.INI)
					{
						operation = OperationStateEnum.CANNOTSTART;
					}
				}
				else if (depend == TaskDependEnum.FINISH_START)
				{
					if (preUtil.getTaskStatusEnum() == TaskStatusEnum.COP || preUtil.getTaskStatusEnum() == TaskStatusEnum.SSP)
					{
						operation = OperationStateEnum.START;
					}
					else if (preUtil.getTaskStatusEnum() == TaskStatusEnum.RUN || preUtil.getTaskStatusEnum() == TaskStatusEnum.PUS)
					{
						operation = OperationStateEnum.CANNOTSTART;
					}
				}
			}
			else if (this.ppmUtil.getTaskStatusEnum() == TaskStatusEnum.RUN)
			{
				// 后置任务运行中
				if (depend == TaskDependEnum.START_FINISH)
				{
					if (preUtil.getTaskStatusEnum() == TaskStatusEnum.RUN || preUtil.getTaskStatusEnum() == TaskStatusEnum.PUS || preUtil.getTaskStatusEnum() == TaskStatusEnum.SSP
							|| preUtil.getTaskStatusEnum() == TaskStatusEnum.COP)
					{
						operation = OperationStateEnum.COMPLETE;
					}
					else if (preUtil.getTaskStatusEnum() == TaskStatusEnum.INI)
					{
						operation = OperationStateEnum.CANNOTCOMPLETE;
					}
				}
				else if (depend == TaskDependEnum.FINISH_FINISH)
				{
					if (preUtil.getTaskStatusEnum() == TaskStatusEnum.INI || preUtil.getTaskStatusEnum() == TaskStatusEnum.RUN || preUtil.getTaskStatusEnum() == TaskStatusEnum.PUS)
					{
						operation = OperationStateEnum.CANNOTCOMPLETE;

					}
					else if (preUtil.getTaskStatusEnum() == TaskStatusEnum.SSP)
					{
						operation = OperationStateEnum.COMPLETE;
					}
				}
				else if (depend == TaskDependEnum.START_START)
				{
					if (preUtil.getTaskStatusEnum() == TaskStatusEnum.RUN || preUtil.getTaskStatusEnum() == TaskStatusEnum.PUS || preUtil.getTaskStatusEnum() == TaskStatusEnum.SSP
							|| preUtil.getTaskStatusEnum() == TaskStatusEnum.COP)
					{
						operation = OperationStateEnum.COMPLETE;
					}
				}
				else if (depend == TaskDependEnum.FINISH_START)
				{
					if (preUtil.getTaskStatusEnum() == TaskStatusEnum.COP)
					{
						operation = OperationStateEnum.COMPLETE;
					}
				}
			}
		}
		return operation;
	}

	/**
	 * 通过SPI设置准时状态的值
	 * 
	 * @param spi
	 */
	public void setOnTimeStateBySPI(Double spi)
	{
		if (this.ppmUtil != null && this.ppmUtil.wbsPrepareContain != null)
		{
			if (spi != null)
			{
				CodeItemInfo onTimeStateFromEnum = null;
				OnTimeStateEnum enumvalue = null;
				if (spi >= 1)
				{
					// 正常状态
					onTimeStateFromEnum = this.ppmUtil.wbsPrepareContain.getOnTimeStateFromEnum(OnTimeStateEnum.PROGRESSONTIME);
					enumvalue = OnTimeStateEnum.PROGRESSONTIME;

				}
				else if (DateFormat.compareDate(new Date(), this.ppmUtil.getPlanFinishTime()) > 0)
				{
					// spi<1 延迟
					onTimeStateFromEnum = this.ppmUtil.wbsPrepareContain.getOnTimeStateFromEnum(OnTimeStateEnum.DELAY);
					enumvalue = OnTimeStateEnum.DELAY;
				}
				else
				{
					// spi<1 潜在进度风险
					if (this.ppmUtil.getTaskStatusEnum() != TaskStatusEnum.COP)
					{
						onTimeStateFromEnum = this.ppmUtil.wbsPrepareContain.getOnTimeStateFromEnum(OnTimeStateEnum.POTENTIALRISK);
						enumvalue = OnTimeStateEnum.POTENTIALRISK;
					}
				}
				if (onTimeStateFromEnum != null && enumvalue != null)
				{
					this.ppmUtil.setOnTimeStateEnum(enumvalue);
					this.ppmUtil.setOnTimeState(onTimeStateFromEnum.getGuid());
					this.ppmUtil.getFoundation().put(PPMFoundationObjectUtil.ONTIMESTATE + PMConstans.TITLE, onTimeStateFromEnum.getTitle());
				}
			}
		}

	}

}
