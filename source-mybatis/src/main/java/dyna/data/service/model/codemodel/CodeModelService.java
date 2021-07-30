/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeModelService
 * Jiagang 2010-9-7
 */
package dyna.data.service.model.codemodel;

import java.util.List;

import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.UITypeEnum;
import dyna.data.service.model.ModelService;

/**
 * 编码模型服务
 * 
 * @author Jiagang
 * 
 */
public interface CodeModelService extends ModelService
{
	/**
	 * 重新加载缓存
	 * 
	 * @throws ServiceRequestException
	 */
	void reloadModel() throws ServiceRequestException;

	/**
	 * 根据组码GUID取得组码信息
	 * 
	 * @param guid
	 * @return
	 */
	CodeObject getCodeObjectByGuid(String guid);

	/**
	 * 根据组码名字取得组码信息
	 * 
	 * @param name
	 * @return
	 */
	CodeObject getCodeObject(String name);

	/**
	 * 根据组码GUID取得组码信息
	 * 
	 * @param guid
	 * @return
	 */
	CodeObjectInfo getCodeObjectInfoByGuid(String guid);

	/**
	 * 根据组码名字取得组码信息
	 * 
	 * @param name
	 * @return
	 */
	CodeObjectInfo getCodeObjectInfo(String name);

	/**
	 * 根据组码子项GUID取得组码子项
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	CodeItem getCodeItemByGuid(String guid) throws ServiceRequestException;

	/**
	 * 根据组码名字和组码子项的名字，取得组码子项信息
	 * 
	 * @param codeName
	 * @param itemName
	 * @return
	 */
	CodeItem getCodeItem(String codeName, String itemName);

	/**
	 * 根据组码子项GUID取得组码子项
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	CodeItemInfo getCodeItemInfoByGuid(String guid) throws ServiceRequestException;

	/**
	 * 根据组码名字和组码子项的名字，取得组码子项信息
	 * 
	 * @param codeName
	 * @param itemName
	 * @return
	 */
	CodeItemInfo getCodeItemInfo(String codeName, String itemName);

	/**
	 * 返回所有的组码
	 * 
	 * @return
	 */
	List<CodeObject> listAllCodeObjectList();

	/**
	 * 返回所有的组码
	 * 
	 * @return
	 */
	List<CodeObjectInfo> listAllCodeInfoList();

	/**
	 * 返回所有的组码
	 * 
	 * @return
	 */
	List<CodeItemInfo> listAllCodeItemInfoList();

	/**
	 * 当不存在分类模块License时，分类做普通组码使用，需要清除字段及UI信息
	 */
	void clearClassification();

	/**
	 * 取得指定分类子项的所有父阶分类guid
	 * 
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	List<String> listAllParentClassificationGuid(String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 取得指定分类子项的所有子阶分类guid
	 * 
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	List<String> listAllSubClassificationGuid(String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 取得指定组码的所有组码子项，不包含树结构
	 * 
	 * @param codeName
	 * @return
	 */
	List<CodeItem> listAllCodeItem(String codeName) throws ServiceRequestException;

	/**
	 * 取得单阶组码子项列表，如果组码子项不为空，则返回该组码子项的子项列表，否则返回组码的首阶子项列表
	 * <br>
	 * 组码表示符和组码子项标识符二者传一即可
	 * 
	 * @param codeGuid
	 *            组码标识符
	 * @param codeItemGuid
	 *            组码子项标识符
	 * @return
	 */
	List<CodeItemInfo> listDetailCodeItemInfo(String codeGuid, String codeItemGuid) throws ServiceRequestException;

	/**
	 * 取得组码子项的所有字段信息
	 * 
	 * @param codeName
	 *            组码名称
	 * @param codeItemName
	 *            组码子项名称
	 * @return
	 */
	List<ClassField> listField(String codeName, String codeItemName) throws ServiceRequestException;

	/**
	 * 取得组码子项的指定字段
	 * 
	 * @param codeName
	 * @param codeItemName
	 * @param fieldName
	 * @return
	 * @throws ServiceRequestException
	 */
	ClassField getField(String codeName, String codeItemName, String fieldName) throws ServiceRequestException;

	/**
	 * 取得组码子项的指定字段
	 * 
	 * @param codeItemgGuid
	 * @param fieldName
	 * @return
	 * @throws ServiceRequestException
	 */
	ClassField getFieldByItemGuid(String codeItemgGuid, String fieldName) throws ServiceRequestException;

	/**
	 * 取得组码子项的所有字段
	 * 
	 * @param codeItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	List<ClassField> listField(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 取得组码子项配置的所有UI对象
	 * 
	 * @param codeName
	 *            组码名称
	 * @param codeItemName
	 *            组码子项名称
	 * @return
	 */
	List<ClassificationUIInfo> listAllUIObject(String codeName, String codeItemName) throws ServiceRequestException;

	/**
	 * 取得组码子项配置的所有指定类型UI对象
	 * 
	 * @param codeName
	 * @param codeItemName
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	List<ClassificationUIInfo> listAllUIObject(String codeName, String codeItemName, UITypeEnum uiType) throws ServiceRequestException;

	/**
	 * 取得组码子项配置的所有UI对象
	 * 
	 * @param codeItemGuid
	 * @return
	 */
	List<ClassificationUIInfo> listAllUIObject(String codeItemGuid) throws ServiceRequestException;

	/**
	 * 取得UI的分类字段列表
	 * 
	 * @param uiGuid
	 * @return
	 */
	List<UIField> listUIField(String uiGuid) throws ServiceRequestException;

}
