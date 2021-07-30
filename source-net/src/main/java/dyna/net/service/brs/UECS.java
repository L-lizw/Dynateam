/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECS Engineering Change Service 工程变更服务
 * caogc 2010-11-02
 */
package dyna.net.service.brs;

import java.util.List;
import java.util.Map;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * ECS Engineering Change Service 工程变更服务
 * 
 * @author cgc
 * 
 */
public interface UECS extends Service
{
	/**
	 * 保存ECP:新建和更新ECP时使用
	 * 
	 * @param ecpfoundation
	 *            ecp实例
	 * @param ecrObject
	 *            所属的ecr的ObjectGuid
	 * @param parentECP
	 *            ECP父节点的ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveECP(FoundationObject ecpfoundation, ObjectGuid ecrObject, ObjectGuid parentECP, String proGuid) throws ServiceRequestException;

	/**
	 * 保存ECO:新建和更新ECO时使用
	 * 
	 * @param ecofoundation
	 *            eco实例
	 * @param ecnObject
	 *            所属的ecn的ObjectGuid
	 * @param parentECO
	 *            ECOP父节点的ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveECO(FoundationObject ecofoundation, ObjectGuid ecnObject, ObjectGuid parentECO, String proGuid) throws ServiceRequestException;

	/**
	 * 根据ECR查询ECP
	 * 
	 * @param end1ObjectGuid
	 *            ecr的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPByECRAll(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECR查询ECPby tree
	 * 
	 * @param end1ObjectGuid
	 *            ecr的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPByECRTree(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECN查询ECO,树形结构
	 * 
	 * @param end1ObjectGuid
	 *            ecn的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECOByECNTree(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECN查询ECO,列表形式
	 * 
	 * @param end1ObjectGuid
	 *            ecn的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECOByECNAll(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * ecn的界面,ecp的列表信息,树形模式
	 * 
	 * @param ecrObjectGuid
	 * @param ecnObjectGuid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPByECRTreeInECNPage(ObjectGuid ecrObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException;

	/**
	 * ecn的界面,ecp的列表信息，全部展现的模式
	 * 
	 * @param ecrObjectGuid
	 * @param ecnObjectGuid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPByECRAllInECNPage(ObjectGuid ecrObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException;

	/**
	 * ecn的界面,ecp的列表信息，ecp下面子节点的ecp
	 * 
	 * @param ecpObjectGuid
	 * @param ecnObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPChildbyParentECPInECNPage(ObjectGuid ecpObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECN查询ECR
	 * 
	 * @param end1ObjectGuid
	 *            ecn的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECRByECN(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * eci界面,根据bom模版得到,解决对象和问题对象之间的差异
	 * 
	 * @param changeItemObjectGuid
	 * @param solveObjectGuid
	 * @param relationTemplateName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getBomECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName, ObjectGuid ecoObjectGuid)
			throws ServiceRequestException;

	/**
	 * eci界面,根据relation模版得到,解决对象和问题对象之间的差异
	 * 
	 * @param changeItemObjectGuid
	 * @param solveObjectGuid
	 * @param relationTemplateName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getRelationECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName, ObjectGuid ecoObjectGuid)
			throws ServiceRequestException;

	/**
	 * 查询eci属性变更部分的信息
	 * 
	 * @param ChangeItemObjectGuid
	 *            问题对象的objectguid
	 * @param SolveObjectGuid
	 *            解决对象的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getFormECIByECO(ObjectGuid ChangeItemObjectGuid, ObjectGuid SolveObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECO查询ECN
	 * 
	 * @param end1ObjectGuid
	 *            eco的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getWhereUsedECNByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECP查询ECR
	 * 
	 * @param end1ObjectGuid
	 *            ecp的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getWhereUsedECRByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECP查询ECO
	 * 
	 * @param end1ObjectGuid
	 *            ecp的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECOByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECP查询ECPCONTENT
	 * 
	 * @param end1ObjectGuid
	 *            ecp的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPCONTENTByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECO查询ECPCONTENT
	 * 
	 * @param end1ObjectGuid
	 *            eco的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getECPCONTENTByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据ECO查询ECI
	 * 
	 * @param end1ObjectGuid
	 *            eco的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getContentECIByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 查询批量ecp的object
	 * 
	 * @param ObjectGuid
	 *            ecp的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public FoundationObject getBatchECPFoundation(ObjectGuid ObjectGuid) throws ServiceRequestException;

	/**
	 * 查询批量eco的object
	 * 
	 * @param ObjectGuid
	 *            eco的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public FoundationObject getBatchECOFoundation(ObjectGuid ObjectGuid) throws ServiceRequestException;

	/**
	 * 查询批量ecp的ecilist
	 * 
	 * @param ObjectGuid
	 *            ecp的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> getEciBYBatchECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 查询批量eco的ecilist
	 * 
	 * @param ObjectGuid
	 *            eco的objectguid
	 * @return List<FoundationObject>
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> getEciBYBatchECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * ECR和ECP的关联
	 * 
	 * @param ecrObjectGuid
	 *            ecr的ObjectGuid
	 * @param ecpObjectGuid
	 *            ecp的ObjectGuid
	 * @throws ServiceRequestException
	 */
	public void linkECR_ECP(ObjectGuid ecrObjectGuid, ObjectGuid ecpObjectGuid, String proGuid) throws ServiceRequestException;

	/**
	 * ECN和ECO的关联
	 * 
	 * @param ecnObjectGuid
	 *            ecn的ObjectGuid
	 * @param ecoObjectGuid
	 *            eco的ObjectGuid
	 * @throws ServiceRequestException
	 */
	public void linkECN_ECO(ObjectGuid ecnObjectGuid, ObjectGuid ecoObjectGuid, String proGuid) throws ServiceRequestException;

	/**
	 * ECN和ECR的关联
	 * 
	 * @param ecnObjectGuid
	 *            ecn的ObjectGuid
	 * @param ecrObjectGuid
	 *            ecr的ObjectGuid
	 * @throws ServiceRequestException
	 */
	public void linkECN_ECR(ObjectGuid ecnObjectGuid, ObjectGuid ecrObjectGuid, String proGuid) throws ServiceRequestException;

	/**
	 * ECP和ECO的关联
	 * 
	 * @param ecnObjectGuid
	 *            ecp的ObjectGuid
	 * @param ecoObjectGuid
	 *            eco的ObjectGuid
	 * @throws ServiceRequestException
	 */
	public void linkECP_ECO(ObjectGuid ecpObjectGuid, ObjectGuid ecoObjectGuid, String proGuid) throws ServiceRequestException;

	/**
	 * ECO和ECI的关联
	 * 普通变更时,增加其他ECI
	 * 
	 * @param ecoObjectGuid
	 * @param eciObjectGuid
	 * @throws ServiceRequestException
	 */
	public void linkECO_ECI(ObjectGuid ecoObjectGuid, ObjectGuid eciObjectGuid, String proGuid) throws ServiceRequestException;

	/**
	 * 通过父结点ECP的ObjectGuid取得子ECP
	 * 
	 * @param ecpObjectGuid
	 *            父节点ECP的ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listChildECPbyParentObjectGuid(ObjectGuid ecpObjectGuid) throws ServiceRequestException;

	/**
	 * 完成eco时，去持久化eci的数据
	 * 
	 * @param ecoObjectGuid
	 * @throws ServiceRequestException
	 */
	public void completeEco(ObjectGuid ecoObjectGuid) throws ServiceRequestException;

	/**
	 * 通过父结点ECO的ObjectGuid取得子ECO
	 * 
	 * @param ecoObjectGuid
	 *            父节点ECO的ObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listChildECObyParentObjectGuid(ObjectGuid ecoObjectGuid) throws ServiceRequestException;

	/**
	 * 增加ECP和ECP类型为普通类型的Content关系
	 * 增加ECP和ECP类型为普通类型的ECI关系
	 * 增加ECO和ECO类型为普通类型的Content关系
	 * 增加ECO和ECO类型为普通类型的ECI关系
	 * 
	 * @param end1ObjectGuid
	 * @param end2ObjectGuid
	 * @param relationTempalte
	 *            关系模板名称
	 * @throws ServiceRequestException
	 */
	public void addECRelation(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, String relationTempalte, String proGuid) throws ServiceRequestException;

	/**
	 * 删除ECP:若ECP下没有子节点,则直接删除(包括ECP实例,解除ECP与ECR的关系)deleteAll参数值为true;
	 * 若ECP存在子节点,则由客户端判断是否全部删除还是只删除当前结点,以下结点往上移动(删除ECP,将其下子节点的parentObjectGuid清空)
	 * 
	 * @param ecrObjectGuid
	 * @param listEcpObjectGuid
	 * @param deleteAll
	 *            是否全部删除
	 * @throws ServiceRequestException
	 */
	public void deleteECP(ObjectGuid ecrObjectGuid, List<ObjectGuid> listEcpObjectGuid, boolean deleteAll) throws ServiceRequestException;

	/**
	 * 删除ECO:若ECO下没有子节点,则直接删除(包括ECO实例,解除ECO与ECN的关系)deleteAll参数值为true;
	 * 若ECO存在子节点,则由客户端判断是否全部删除还是只删除当前结点,以下结点往上移动(删除ECO,将其下子节点的parentObjectGuid清空)
	 * 
	 * @param ecrObjectGuid
	 * @param listEcpObjectGuid
	 * @param deleteAll
	 *            是否全部删除
	 * @throws ServiceRequestException
	 */
	public void deleteECO(ObjectGuid ecnObjectGuid, List<ObjectGuid> listEcoObjectGuid, boolean deleteAll) throws ServiceRequestException;

	/**
	 * 更新ECP的Content实例
	 * 
	 * @param contentFoundation
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveNotCheckout(FoundationObject contentFoundation) throws ServiceRequestException;

	/**
	 * 批量ECO页面时,批量增加变更项
	 * 所有的变更项都关联ECO
	 * 
	 * @param ecoFoundationObject
	 *            eco实例
	 * @param structureObjectList
	 *            增加的变更项关系结构
	 * @param deleteList
	 *            需要删除的变更项--已经有Guid的structureObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveECO_ECI_ChangeItem(FoundationObject ecoFoundationObject, List<StructureObject> structureObjectList, List<StructureObject> deleteList, String proGuid)
			throws ServiceRequestException;

	/**
	 * 批量ECP页面时,批量增加变更项
	 * 所有的变更项都关联ECO
	 * 
	 * @param ecpFoundationObject
	 *            ecp实例
	 * @param structureObjectList
	 *            增加的变更项关系结构
	 * @param deleteList
	 *            需要删除的变更项--已经有Guid的structureObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveECP_ECI_ChangeItem(FoundationObject ecpFoundationObject, List<StructureObject> structureObjectList, List<StructureObject> deleteList, String proGuid)
			throws ServiceRequestException;

	/**
	 * 批量将ECP转换成ECO
	 * 普通类型转换成普通的ECO,批量类型转换成批量的ECO
	 * 将ECP对象某些字段值赋值到ECO中,并且将ECO与ECP关联起来,且ECN与ECO关联起来
	 * 
	 * @param ecnObjectGuid
	 *            ecn的ObjectGuid
	 * @param ecpObjectGuid
	 *            批量ECP
	 * @throws ServiceRequestException
	 */
	public Map<String, ServiceRequestException> exchangeECP_ECO(ObjectGuid ecnObjectGuid, List<ObjectGuid> ecpObjectGuidList, String proGuid) throws ServiceRequestException;

	/**
	 * 通过end1ObjectGuid和end2ObjectGuid以及bomTemplateName取得对应的StructureObject
	 * 
	 * @param end1ObjectGuid
	 * @param end2ObjectGuid
	 * @param viewName
	 *            BOMTemplate的Name
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOMStructure> listStructureObject(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, String viewName) throws ServiceRequestException;

	/**
	 * 批量启动ECO
	 * 
	 * @param listECOObjectGuid
	 *            批量ECO
	 * @throws ServiceRequestException
	 */
	/**
	 * 
	 * @param ecnObjectGuid
	 * @param listECOObjectGuid
	 * @throws ServiceRequestException
	 */
	public void startECO(ObjectGuid ecnObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException;

	/**
	 * 实例是否被其他eco使用
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isUsedByOtherECO(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 删除ECP或者ECO,无需检出,且只有ECP/ECR或者ECO/ECN的owner才能删除
	 * 
	 * @param objectGuid
	 *            删除对象的ObjectGuid
	 * @param tagString
	 *            删除的是ECO/ECP:如果是ECP,UpdatedECSConstants.ECP;如果是ECO,UpdatedECSConstants.ECO
	 * @throws ServiceRequestException
	 */
	public void deleteNotCheckOut(ObjectGuid objectGuid, String tagString) throws ServiceRequestException;

	/**
	 * 取消ECN或者ECO
	 * 
	 * @param ecofoundation
	 * @param ecnObject
	 * @param parentECO
	 * @throws ServiceRequestException
	 */
	public void cancelECN_ECO(ObjectGuid cancelValue) throws ServiceRequestException;

	/**
	 * ECO是否可以完成
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean checkECOCanBeStarted(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 审核阶段取消ECR
	 * 
	 * @param ecofoundation
	 * @param ecnObject
	 * @param parentECO
	 * @throws ServiceRequestException
	 */
	public void cancelECR(ObjectGuid ecrObjectGuid) throws ServiceRequestException;

	/**
	 * script 发送邮件给eco的执行人
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void sendMailtoPerformerByScript(String procRtGuid) throws ServiceRequestException;

	/**
	 * 将eco锁定的附件解锁
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void unlockFoundation(String procRtGuid) throws ServiceRequestException;

	/**
	 * 取消完成的方法
	 * 
	 * @param ecoObjectGuid
	 * @throws ServiceRequestException
	 */
	public void recoveryEco(ObjectGuid ecoObjectGuid) throws ServiceRequestException;

	/**
	 * 我的主页--工程变更任务查询
	 * 
	 * @param searchConditionMap
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listMyExchangeTask(SearchCondition searchCondition) throws ServiceRequestException;

}
