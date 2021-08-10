package dyna.app.service.brs.wfi.activity;

import java.util.Comparator;
import java.util.Map;
import java.util.Map;

import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcTrack;

public class ProcTrackComparator implements Comparator<ProcTrack>
{
	private Map<String, ActivityRuntime> actMap = null;

	public ProcTrackComparator(Map<String, ActivityRuntime> actMap)
	{
		this.actMap = actMap;
	}

	@Override
	public int compare(ProcTrack o1, ProcTrack o2)
	{
		ActivityRuntime act1 = actMap.get(o1.getActRuntimeGuid());
		ActivityRuntime act2 = actMap.get(o2.getActRuntimeGuid());
		int returnValue = -1;
		if (act1.getGate() != null)
		{
			if (act2.getGate() == null)
			{
				returnValue = 1;
			}
			else
			{
				returnValue = act1.getGate().compareTo(act2.getGate());
			}
		}
		if (o1.getCreateTime() == null)
		{
			if (o2.getCreateTime() == null)
			{
				return returnValue;
			}
			else
			{
				return -1;
			}
		}
		else if (o2.getCreateTime() == null)
		{
			return -1;
		}
		else
		{
			int timeCompareValue = o1.getCreateTime().compareTo(o2.getCreateTime());
			if (timeCompareValue == 0)
			{
				return returnValue;
			}
			else
			{
				return timeCompareValue;
			}
		}
	}

}
