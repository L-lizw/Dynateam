package dyna.data.service.model.featuremodel;

import java.util.List;

import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

public interface ClassificationFeatureService extends Service
{
	/**
	 * 部署后检查分类映射，
	 * 1，当类直接删除时，对应的分类映射规则全部删除
	 * 2，当分类删除时，有分类映射规则，规则无效，删除
	 * 3，当分类子项删除时，对应规则子项删除
	 * 
	 * @param userGuid
	 * @throws ServiceRequestException
	 */
	void checkAndClearClassificationFeature(String userGuid) throws ServiceRequestException;

	/**
	 * 根据分类映射关系
	 * 
	 * @param featureGuid
	 * @return
	 */
	ClassficationFeature getClassficationFeature(String featureGuid);

	/**
	 * 取得所有的分类映射关系
	 * 
	 * @return
	 */
	List<ClassficationFeature> listAllClassficationFeature();

	/**
	 * 根据子类取得所有父类的分类映射关系
	 * 
	 * @param classguid
	 * @return
	 */
	List<ClassficationFeature> listClassficationFeatureBySubClass(String classguid);

	/**
	 * 根据父类取得所有子类的分类映射关系
	 * 
	 * @param classGuid
	 * @return
	 */
	List<ClassficationFeature> listAllSubClassficationFeature(String classGuid);

	/**
	 * 判断当前分类子项是否和指定类之间存在着主分类映射关系
	 * 
	 * @param classGuid
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	boolean isMasterClassification(String classGuid, String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 取得指定分类映射关系的所有映射规则(根据分类子项往上查找)
	 * 
	 * @param featureGuid
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	List<ClassficationFeatureItemInfo> listAllSuperClassificationFeatureItem(String featureGuid, String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 取得指定分类映射关系的所有映射规则(根据分类子项往下查找)
	 * 
	 * @param featureGuid
	 * @param classificationItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	List<ClassficationFeatureItemInfo> listAllSubClassificationFeatureItem(String featureGuid, String classificationItemGuid) throws ServiceRequestException;

	/**
	 * 取得指定分类映射规则
	 * 
	 * @param featureItemGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	ClassficationFeatureItem getClassficationFeatureItem(String featureItemGuid) throws ServiceRequestException;

	/**
	 * 保存指定分类映射规则
	 * 
	 * @param classficationFeatureItem
	 * @return
	 * @throws DynaDataException
	 * @throws ServiceRequestException
	 */
	String saveClassificationFeatureItem(ClassficationFeatureItem classificationFeatureItem) throws DynaDataException, ServiceRequestException;

	/**
	 * 保存指定分类映射规则的字段
	 * 
	 * @param classficationFeatureItem
	 * @return
	 * @throws DynaDataException
	 * @throws ServiceRequestException
	 */
	void saveClassificationNumberField(String classificationFeatureItemGuid, List<ClassificationNumberField> classificationNumberFieldList)
			throws DynaDataException, ServiceRequestException;

	/**
	 * 删除指定分类映射规则
	 * 
	 * @param featureItemGuid
	 * @return
	 * @throws DynaDataException
	 * @throws ServiceRequestException
	 */
	void deleteClassificationFeatureItem(String featureItemGuid) throws DynaDataException, ServiceRequestException;

	/**
	 * 取得所有的分类映射规则
	 * 
	 * @return
	 */
	List<ClassficationFeatureItemInfo> listAllClassficationFeatureItemInfo();
}
