/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SDSCommonUnit
 * ZhangHW 2011-7-8
 */
package dyna.data.service.sdm;

import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;

import java.util.List;

/**
 * 系统数据服务 公用操作
 * 
 * @author ZhangHW
 * 
 */
public class SDSCommonStub extends DSAbstractServiceStub<SystemDataServiceImpl>
{
	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public SDSCommonStub(DataServerContext context, SystemDataServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	@SuppressWarnings("unchecked")
	public List<String> executeQueryBySql(String sql) throws DynaDataException
	{
		try
		{
			return (List<String>) this.dynaObjectMapper.selectAuto(sql);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("executeQueryBySql()", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}
}
