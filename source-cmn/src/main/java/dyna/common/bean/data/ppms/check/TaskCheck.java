package dyna.common.bean.data.ppms.check;

import java.util.Date;

import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.DateFormat;

/**
 * 任务检查
 * 检查任务是否循环
 * 任务是否超期
 * 
 * @author wwx
 * 
 */
public class TaskCheck
{
	private PPMFoundationObjectUtil ppmUtil = null;

	public void setPpmUtil(PPMFoundationObjectUtil ppmUtil)
	{
		this.ppmUtil = ppmUtil;
	}

	public boolean isExceedTask()
	{
		if (this.ppmUtil.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
		{
			return false;
		}
		if (this.ppmUtil.getActualStartTime() == null)
		{
			Date date = DateFormat.parse(DateFormat.format(new Date(), DateFormat.PTN_YMD));
			if (this.ppmUtil.getPlanStartTime() != null && this.ppmUtil.getPlanStartTime().getTime() - date.getTime() < 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (this.ppmUtil.getActualFinishTime() == null)
		{
			Date date = DateFormat.parse(DateFormat.format(new Date(), DateFormat.PTN_YMD));
			if (this.ppmUtil.getPlanFinishTime().getTime() - date.getTime() < 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			Date date = DateFormat.parse(DateFormat.formatYMD(this.ppmUtil.getActualFinishTime()));
			if (this.ppmUtil.getPlanFinishTime().getTime() - date.getTime() < 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	public boolean isCriticalTask()
	{
		System.out.println("number:" + this.ppmUtil.getWBSNumber() + "  ls:" + this.ppmUtil.getLatestStartTime() + "  lf:" + this.ppmUtil.getLatestFinishTime());
		System.out.println("number:" + this.ppmUtil.getWBSNumber() + "  es:" + this.ppmUtil.getPlanStartTime() + "  ef:" + this.ppmUtil.getPlanFinishTime());

		if (this.ppmUtil.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
		{
			return false;
		}

		if (this.ppmUtil.getLatestStartTime() == null || this.ppmUtil.getLatestFinishTime() == null)
		{
			return false;
		}
		else if (this.ppmUtil.getActualFinishTime() != null)
		{
			return false;
		}
		else
		{
			if (this.ppmUtil.getPlanStartTime().getTime() >= this.ppmUtil.getLatestStartTime().getTime())
			{
				System.out.println(this.ppmUtil.getWBSNumber());
				return true;
			}
			else if (this.ppmUtil.getPlanFinishTime().getTime() >= this.ppmUtil.getLatestFinishTime().getTime())
			{
				System.out.println(this.ppmUtil.getWBSNumber());
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
