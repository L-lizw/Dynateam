/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SDSCommonUnit
 * ZhangHW 2011-7-8
 */
package dyna.data.service.sdm;

import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.data.service.DSAbstractServiceStub;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统数据服务 公用操作
 * 
 * @author ZhangHW
 * 
 */
@Repository
public class SDSCommonStub extends DSAbstractServiceStub<SystemDataServiceImpl>
{

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
