package dyna.data.service.tool;

import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

public interface DSToolService extends Service
{
	/**
	 * 清除Mast表的垃圾数据（可能存在系统数据已删除但mast表数据未删除的情况,流水号重置时使用）
	 *
	 * @param className
	 * @throws DynaDataException
	 */
	void clearErrDataInMast(String className) throws ServiceRequestException;

	/**
	 * 个案准备的状态修改方法(标准功能禁止使用)
	 * 
	 * @param parameterMap
	 * @throws ServiceRequestException
	 */
	void changeFoundationStatus(Map<String, Object> parameterMap) throws ServiceRequestException;

	/**
	 * 更新指定对象的指定字段的值(主要用来刷新历史数据的合成规则,给顾问使用,不能在系统中调用)
	 *
	 * @param foundationObject
	 * @param fieldName
	 * @throws DynaDataException
	 */
	void refreshMergeFieldValue(FoundationObject foundationObject, String fieldName) throws ServiceRequestException;
}
