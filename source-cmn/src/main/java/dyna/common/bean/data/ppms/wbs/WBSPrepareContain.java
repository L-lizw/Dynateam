package dyna.common.bean.data.ppms.wbs;

import java.io.Serializable;
import java.util.List;

import dyna.common.bean.data.ppms.LaborHourConfig;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.systemenum.ppms.DurationUnitEnum;
import dyna.common.systemenum.ppms.OnTimeStateEnum;
import dyna.common.systemenum.ppms.OperationStateEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class WBSPrepareContain implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 6460463500904718396L;

	protected List<CodeItemInfo>	dependTypeList		= null;

	protected List<CodeItemInfo>	durationUnitList	= null;

	protected List<CodeItemInfo>	taskTypeList		= null;

	protected List<CodeItemInfo>	taskStatusList		= null;

	protected List<CodeItemInfo>	operationStatusList	= null;

	protected List<CodeItemInfo>	onTimeStateEnumList	= null;

	protected PMCalendar			projectCalendar		= null;

	protected LaborHourConfig		workHourConfig		= null;

	public WBSPrepareContain(List<CodeItemInfo> dependTypeList, List<CodeItemInfo> durationUnitList, List<CodeItemInfo> taskTypeList, List<CodeItemInfo> taskStatusList,
			List<CodeItemInfo> operationStatusList, List<CodeItemInfo> onTimeStateEnumList)
	{
		this.dependTypeList = dependTypeList;
		this.durationUnitList = durationUnitList;
		this.taskTypeList = taskTypeList;
		this.taskStatusList = taskStatusList;
		this.operationStatusList = operationStatusList;
		this.onTimeStateEnumList = onTimeStateEnumList;
	}

	public OnTimeStateEnum getOnTimeStateFromCode(String guid)
	{
		if (StringUtils.isNullString(guid))
		{
			return null;
		}
		if (SetUtils.isNullList(this.onTimeStateEnumList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.onTimeStateEnumList)
		{
			if (xCodeItemInfo.getGuid().equals(guid))
			{
				return OnTimeStateEnum.getValueOf(xCodeItemInfo.getName());
			}
		}
		return null;
	}

	public CodeItemInfo getOnTimeStateFromEnum(OnTimeStateEnum state)
	{
		if (state == null)
		{
			return null;
		}

		if (SetUtils.isNullList(this.onTimeStateEnumList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.onTimeStateEnumList)
		{
			if (xCodeItemInfo.getName().equals(state.getValue()))
			{
				return xCodeItemInfo;
			}
		}
		return null;
	}

	/**
	 * @param day
	 * @return
	 */
	public String getOnTimeStateGuid(OnTimeStateEnum day)
	{
		if (day == null)
		{
			return null;
		}

		if (SetUtils.isNullList(this.onTimeStateEnumList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.onTimeStateEnumList)
		{
			if (xCodeItemInfo.getName().equals(day.getValue()))
			{
				return xCodeItemInfo.getGuid();
			}
		}
		return null;
	}

	public DurationUnitEnum getDurationUnitFromCode(String guid)
	{
		if (StringUtils.isNullString(guid))
		{
			return null;
		}
		if (SetUtils.isNullList(this.durationUnitList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.durationUnitList)
		{
			if (xCodeItemInfo.getGuid().equals(guid))
			{
				return DurationUnitEnum.getStatusEnum(xCodeItemInfo.getName());
			}
		}
		return null;
	}

	/**
	 * @param day
	 * @return
	 */
	public String getDurationUnitGuid(DurationUnitEnum day)
	{
		if (day == null)
		{
			return null;
		}

		if (SetUtils.isNullList(this.dependTypeList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.dependTypeList)
		{
			if (xCodeItemInfo.getName().equals(day.getValue()))
			{
				return xCodeItemInfo.getGuid();
			}
		}
		return null;
	}

	/**
	 * @param prepareDependType
	 * @return
	 */
	public dyna.common.systemenum.ppms.TaskDependEnum getDependTypeFromCode(String prepareDependType)
	{
		return dyna.common.systemenum.ppms.TaskDependEnum.getEnum(prepareDependType);
	}

	/**
	 * @param fs
	 * @return
	 */
	public String getDependTypeGuid(TaskDependEnum fs)
	{
		return fs.getValue();
	}

	/**
	 * @param string
	 * @return
	 */
	public CodeItemInfo getTaskStatusCodeItem(TaskStatusEnum value)
	{
		if (value == null)
		{
			return null;
		}
		if (SetUtils.isNullList(this.taskStatusList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.taskStatusList)
		{
			if (xCodeItemInfo.getName().equals(value.getValue()))
			{
				return xCodeItemInfo;
			}
		}
		return null;
	}

	/**
	 * @param string
	 * @return
	 */
	public TaskStatusEnum getTaskStatusFromCode(String guid)
	{
		if (StringUtils.isNullString(guid))
		{
			return null;
		}
		if (SetUtils.isNullList(this.taskStatusList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.taskStatusList)
		{
			if (xCodeItemInfo.getGuid().equals(guid))
			{
				return TaskStatusEnum.getStatusEnum(xCodeItemInfo.getName());
			}
		}
		return null;
	}

	/**
	 * @param taskType
	 * @return
	 */
	public TaskTypeEnum getTaskTypeFromCode(String guid)
	{
		if (StringUtils.isNullString(guid))
		{
			return null;
		}
		if (SetUtils.isNullList(this.taskTypeList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.taskTypeList)
		{
			if (xCodeItemInfo.getGuid().equals(guid))
			{
				return TaskTypeEnum.getStatusEnum(xCodeItemInfo.getName());
			}
		}
		return null;
	}

	/**
	 * @return the projectCalendar
	 */
	public PMCalendar getProjectCalendar()
	{
		if (this.projectCalendar == null)
		{
			this.projectCalendar = new PMCalendar();
		}
		return this.projectCalendar;
	}

	/**
	 * @param projectCalendar
	 *            the projectCalendar to set
	 */
	public void setProjectCalendar(PMCalendar projectCalendar)
	{
		this.projectCalendar = projectCalendar;
	}

	/**
	 * @param general
	 * @return
	 */
	public String getTaskTypeGuid(TaskTypeEnum type)
	{
		if (type == null)
		{
			return null;
		}

		if (SetUtils.isNullList(this.taskTypeList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.taskTypeList)
		{
			if (xCodeItemInfo.getName().equals(type.getValue()))
			{
				return xCodeItemInfo.getGuid();
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	public CodeItemInfo getTaskTypeCodeItem(TaskTypeEnum type)
	{
		if (type == null)
		{
			return null;
		}

		if (SetUtils.isNullList(this.taskTypeList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.taskTypeList)
		{
			if (xCodeItemInfo.getName().equals(type.getValue()))
			{
				return xCodeItemInfo;
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	public CodeItemInfo getOperationStatusCodeItem(OperationStateEnum status)
	{
		if (status == null)
		{
			return null;
		}

		if (SetUtils.isNullList(this.operationStatusList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.operationStatusList)
		{
			if (xCodeItemInfo.getName().equals(status.getValue()))
			{
				return xCodeItemInfo;
			}
		}
		return null;
	}

	/**
	 * @param string
	 * @return
	 */
	public OperationStateEnum getOperationStatusFromCode(String guid)
	{
		if (StringUtils.isNullString(guid))
		{
			return null;
		}
		if (SetUtils.isNullList(this.operationStatusList))
		{
			return null;
		}

		for (CodeItemInfo xCodeItemInfo : this.operationStatusList)
		{
			if (xCodeItemInfo.getGuid().equals(guid))
			{
				return OperationStateEnum.getValueOf(xCodeItemInfo.getName());
			}
		}
		return null;
	}

	public LaborHourConfig getWorkHourConfig()
	{
		return this.workHourConfig;
	}

	public void setWorkHourConfig(LaborHourConfig workHourConfig)
	{
		this.workHourConfig = workHourConfig;
	}
}
