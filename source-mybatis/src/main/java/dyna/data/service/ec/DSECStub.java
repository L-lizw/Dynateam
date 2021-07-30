/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EC处理
 * JiangHL 2011-5-10
 */
package dyna.data.service.ec;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dtomapper.DynaObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 处理EC
 * 
 * @author JiangHL
 */
public class DSECStub extends DSAbstractServiceStub<ECServiceImpl>
{
	@Autowired
	private DynaObjectMapper            dynaObjectMapper;
	public DSECStub(DataServerContext context, ECServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	protected void unlockByECO(ObjectGuid ecoObjectGuid, List<String> classNameList) throws DynaDataException
	{
		List<String> tableList = new ArrayList<String>();

		Map<String, Object> fileMap = new HashMap<>();
		fileMap.put("ECFLAG", ecoObjectGuid.getGuid());
		fileMap.put("ECFLAGCLASS", ecoObjectGuid.getClassGuid());
		fileMap.put("ECFLAGMASTER", ecoObjectGuid.getMasterGuid());
		try
		{
			if (!SetUtils.isNullList(classNameList))
			{
				for (String className : classNameList)
				{
					String tableName = DataServer.getDSCommonService().getTableName(className);
					if (!tableList.contains(tableName))
					{
						fileMap.put("table", tableName);
						this.dynaObjectMapper.unlockAllByECO(fileMap);

						tableList.add(tableName);
					}
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("unlockByECO error", e, DataExceptionEnum.SAVE_ECFLAG_ERROR);
		}
	}
}
