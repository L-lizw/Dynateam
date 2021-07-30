/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BRM
 * wangweixia 2012-7-12
 */
package dyna.net.service.brs;

import java.util.Date;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.ReplaceConfig;
import dyna.common.dto.ReplaceSearchConf;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ReplaceRangeEnum;
import dyna.common.systemenum.ReplaceStatusEnum;
import dyna.common.systemenum.ReplaceTypeEnum;
import dyna.net.service.Service;

/**
 * BRM BOM REPLACE MANAGER :bom 取/替代管理
 * 
 * @author wangweixia
 * 
 */
public interface BRM extends Service
{
	/**
	 * 预定义搜索_取替代查询
	 * 
	 * @param replaceSearchConf
	 * @param rowIndex
	 *            数据开始的行数, 对应数据库中的row number
	 * @param pageSize
	 *            每页数据量
	 * @param isForward
	 *            翻页操作, true为下一页/末页
	 * @return
	 *         List<FoundationObject> 所有的取替代对象
	 * @throws ServiceRequestException
	 **/
	public List<FoundationObject> listReplaceDataBySearch(ReplaceSearchConf replaceSearchConf, int pageNum, int pageSize) throws ServiceRequestException;

	/**
	 * 通过主件和元件的ObjectGuid和范围（全局或局部）取得(取代&替代)数据
	 * 假如范围是全局，则masterItemObjectGuid为空且bomName的值为空；componentItemObjectGuid不为空
	 * 假如范围是局部，masterItemObjectGuid、componentItemObjectGuid、bomViewName的值不为空
	 * 
	 * @param masterItemObjectGuid
	 * @param componentItemObjectGuid
	 * @param rang
	 * @param type
	 * @param bomViewName
	 * @param bomKey
	 * @param isContainInvalidDate
	 *            true:未生效;生效;失效的数据 false:未生效;生效的数据
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listReplaceDataByRang(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ReplaceRangeEnum rang, ReplaceTypeEnum type,
			String bomViewName, String bomKey, boolean isContainInvalidDate) throws ServiceRequestException;

	/**
	 * 通过主件、元件和替代件的ObjectGuid和范围（全局或局部）取得(取代&替代)数据
	 * 
	 * @param masterItemObjectGuid
	 * @param componentItemGuid
	 * @param rsItemGuid
	 * @param rang
	 * @param type
	 * @param bomViewName
	 * @param bomKey
	 * @param isContainInvalidDate
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listReplaceDataByRang(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemGuid, ObjectGuid rsItemGuid, ReplaceRangeEnum rang,
			ReplaceTypeEnum type, String bomViewName, String bomKey, boolean isContainInvalidDate) throws ServiceRequestException;

	/**
	 * 通过主件和元件的ObjectGuid、范围（全局或局部）和取替代状态取得(取代&替代)数据
	 * 假如范围是全局，则masterItemObjectGuid为空且bomName的值为空；componentItemObjectGuid不为空
	 * 假如范围是局部，masterItemObjectGuid、componentItemObjectGuid、bomViewName的值不为空
	 * 
	 * @param masterItemObjectGuid
	 * @param componentItemObjectGuid
	 * @param rang
	 * @param type
	 * @param bomViewName
	 * @param isContainInvalidDate
	 *            true:未生效;生效;失效的数据 false:未生效;生效的数据
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listReplaceDataByRangByStatus(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ReplaceRangeEnum rang, ReplaceTypeEnum type,
			String bomViewName, String bomKey, boolean isContainInvalidDate, ReplaceStatusEnum status) throws ServiceRequestException;

	/**
	 * 增加取替代关系数据,增加之前需判断是否可以添加取替代关系
	 * 
	 * @param foundationObject
	 *            此foundationObject中没有Guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject createReplaceData(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 保存取替代关系(修改除元件、主件、替代件的其他属性用)
	 * 
	 * @param foundationObject
	 *            此foundationObject中有Guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveReplaceData(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 删除取替代关系数据
	 * 
	 * @param ObjectGuid
	 * @throws ServiceRequestException
	 */
	public void deleteReplaceData(ObjectGuid ObjectGuid) throws ServiceRequestException;

	/**
	 * 删除局部取替代关系数据
	 * 
	 * @param objectGuid
	 * @param StructureObjGuid
	 * @param bomViewName
	 * @throws ServiceRequestException
	 */
	public void deleteReplaceData(ObjectGuid objectGuid, ObjectGuid StructureObjGuid, String bomViewName) throws ServiceRequestException;

	/**
	 * 取替代对象的初始FoundationObject
	 * 
	 * @param range
	 * @param type
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject newFoundationObject(ReplaceRangeEnum range, ReplaceTypeEnum type) throws ServiceRequestException;

	/**
	 * 不检查保存
	 * 
	 * @param foundationObject
	 * @param hasReturn
	 * @param isCheckAuth
	 * @param needCheckOut
	 * @param procRtGuid
	 * @param isRunScript
	 * @param isCheckStatus
	 * @param isUpdateTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 通过元件取得对应的替代件(参考件)
	 * 
	 * @param componentItemObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listReferenceItem(ObjectGuid componentItemObjectGuid) throws ServiceRequestException;

	/**
	 * 交换顺序
	 * 
	 * @param frontObjectGuid
	 * @param laterObjectGuid
	 * @throws ServiceRequestException
	 */
	public void exchangeRSNumber(ObjectGuid frontObjectGuid, ObjectGuid laterObjectGuid) throws ServiceRequestException;

	/**
	 * 检查能否创建取替代关系
	 * 
	 * @param foundationObject
	 *            要创建的取替代对象
	 * @return
	 *         若可以新建，返回true;若不能能新建，抛出不能新建的异常信息
	 * @throws ServiceRequestException
	 */
	public boolean checkCreateReplaceRelation(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 检查能否创建取替代关系
	 * 
	 * @param masterItemObjectGuid
	 *            主件：当判断是否有全局取替代关系时，可为空；判断局部时，不能为空
	 * @param componentItemObjectGuid
	 *            元件：不能为空
	 * @param rsItemObjectGuid
	 *            替代件：不能为空
	 * @param bomViewName
	 *            bom模板名称：当判断是否有全局取替代关系时，可为空；判断局部时，不能为空
	 * @param bomKey
	 *            bom位置判断字段
	 * @return
	 *         若可以新建，返回true;若不能能新建，抛出不能新建的异常信息
	 * @throws ServiceRequestException
	 */
	public boolean checkCreateReplaceRelation(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomViewName, String bomKey)
			throws ServiceRequestException;

	/**
	 * 批量处理取替代申请
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void batchDealReplaceApply(String procRtGuid) throws ServiceRequestException;

	/**
	 * 通过替代件，取得是否有取替代关系
	 * 
	 * @param rsItemObjectGuid
	 * @param range
	 * @param type
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listRepalcedByRsItem(ObjectGuid rsItemObjectGuid, ReplaceRangeEnum range, ReplaceTypeEnum type) throws ServiceRequestException;

	/**
	 * 客户端创建取替代数据
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject createReplace(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 读取取替代配置
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public ReplaceConfig getReplaceConfig() throws ServiceRequestException;

	/**
	 * 保存取替代配置
	 * 
	 * @param enable
	 * @throws ServiceRequestException
	 */
	public void updateReplaceConfig(ReplaceConfig replaceConfig) throws ServiceRequestException;

	/**
	 * 是否进行取替代管控
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isReplaceControl() throws ServiceRequestException;

	/**
	 * 更新BOM的取替代标记
	 * 
	 * @param replaceData
	 * @throws ServiceRequestException
	 */
	public void updateBomRsFlag(FoundationObject replaceData) throws ServiceRequestException;

	/**
	 * 取替代日期判断，以服务器时间作为判断基准
	 * 
	 * @param effectiveDate
	 * @param invalidDate
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean checkReplaceDate(Date effectiveDate, Date invalidDate, boolean checkEffectiveDate, boolean checkExpireDate) throws ServiceRequestException;
}
