/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Status
 * sam Jun 28, 2010
 */
package dyna.dbcommon.util;

import dyna.common.systemenum.SystemStatusEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统状态数据
 * 
 * @author xiasheng
 */
public class SystemStatusGuid
{
	private Map<String, String> statusGuidMap = null;

	public SystemStatusGuid(List<Map<String, String>> statusList)
	{
		if (statusList != null)
		{
			this.statusGuidMap = new HashMap<String, String>();

			for (Map<String, String> statusMap : statusList)
			{
				this.statusGuidMap.put(statusMap.get("ID"), statusMap.get("GUID"));
			}
		}
	}

	public String getGuid(SystemStatusEnum systemStatus)
	{
		if (this.statusGuidMap == null || this.statusGuidMap.isEmpty())
		{
			return null;
		}

		return this.statusGuidMap.get(systemStatus.toString());

	}
}
