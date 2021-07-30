/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SLC System Logical Configuration(系统配置服务)
 * caogc 2010-11-08
 */
package dyna.net.service.brs;

import dyna.common.dto.ConfigRuleBOLM;
import dyna.common.dto.ConfigRuleRevise;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * SLC System Logical Configuration(系统配置服务)
 * 
 * @author caogc
 * 
 */
public interface SLC extends Service
{

	/**
	 * 删除版序规则
	 * 
	 * @param guid
	 *            要删除的对象的guid
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deleteConfigRuleRevise(String guid) throws ServiceRequestException;

	/**
	 * 获取权限关联
	 * 
	 * @return ConfigRuleAuth
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	// @Deprecated
	// public ConfigRuleAuth getConfigRuleAuth() throws ServiceRequestException;

	/**
	 * 获取版序规则
	 * 
	 * @return ConfigRuleRevise
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ConfigRuleRevise getConfigRuleRevise() throws ServiceRequestException;

	/**
	 * 获取修订版规则
	 * 
	 * @return ConfigRuleRevision
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	// public ConfigRuleRevision getConfigRuleRevision() throws ServiceRequestException;

	/**
	 * 保存权限关联，新建或者更新
	 * 
	 * @param configRuleAuth
	 * @return ConfigRuleAuth
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	// @Deprecated
	// public ConfigRuleAuth saveConfigRuleAuth(ConfigRuleAuth configRuleAuth) throws ServiceRequestException;

	/**
	 * 保存版序规则，新建或者更新
	 * 
	 * @param configRuleRevise
	 * @return ConfigRuleRevise
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ConfigRuleRevise saveConfigRuleRevise(ConfigRuleRevise configRuleRevise) throws ServiceRequestException;

	/**
	 * 保存修订版规则，新建或者更新
	 * 
	 * @param configRuleRevision
	 * @return ConfigRuleRevision
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	// public ConfigRuleRevision saveConfigRuleRevision(ConfigRuleRevision configRuleRevision) throws
	// ServiceRequestException;

	/**
	 * 保存规则(包含生效规则、失效规则、入库规则)<br>
	 * 新建和更新
	 * 
	 * @param configRuleBOLM
	 * @param bmGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public ConfigRuleBOLM saveConfigRuleBOLM(ConfigRuleBOLM configRuleBOLM, String bmGuid) throws ServiceRequestException;

	/**
	 * 根据业务对象的GUID获取对应的 规则配置(包含生效规则、失效规则、入库规则)<br>
	 * 
	 * 如果当前BO上没有设置 那么直接返回空值 不再去父BO上找配置
	 * 
	 * @param bmGuid
	 * @param boGuid
	 * @return ConfigRuleBOLM
	 * @throws ServiceRequestException
	 */
	public ConfigRuleBOLM getConfigRuleBOLMOnlyCurrent(String bmGuid, String boGuid) throws ServiceRequestException;

	/**
	 * 根据业务对象的GUID获取对应的 规则配置(包含生效规则、失效规则、入库规则)<br>
	 * 
	 * 如果当前BO上没有设置 那么会去父BO上找配置 找到一个为止
	 * 
	 * @param boGuid
	 * @return ConfigRuleBOLM
	 * @throws ServiceRequestException
	 */
	public ConfigRuleBOLM getConfigRuleBOLMContainParent(String boGuid) throws ServiceRequestException;

	/**
	 * 根据ClassName获得对应的数据是否要强制使用入库规则
	 * 
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public boolean isCommitForceByClassName(String className) throws ServiceRequestException;

	/**
	 * 根据规则配置(包含生效规则、失效规则、入库规则)的GUID删除该规则
	 * 
	 * @param configRuleBOLMGuid
	 * @throws ServiceRequestException
	 */
	public void deleteConfigRuleBOLM(String configRuleBOLMGuid) throws ServiceRequestException;

	/**
	 * 更新入库规则及条件信息
	 * 
	 * @param configRuleCommitGuid
	 * @param sc
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及查询历史未找到异常
	 */
	// @Deprecated
	// public ConfigRuleCommit saveConfigRuleCommit(String configRuleCommitGuid, CommitSearchCondition sc)
	// throws ServiceRequestException;

	/**
	 * 创建入库规则及条件信息
	 * 
	 * @param sc
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及查询历史未找到异常
	 */
	// @Deprecated
	// public ConfigRuleCommit createConfigRuleCommit(CommitSearchCondition sc) throws ServiceRequestException;

	/**
	 * 删除入库规则明细信息
	 * 
	 * @param configRuleCommitGuid
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public void deleteConfigRuleCommit(String configRuleCommitGuid) throws ServiceRequestException;

	/**
	 * 获取指定库对应的所有的入库规则信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public List<ConfigRuleCommit> listConfigRuleCommitByLib(String libGuid) throws ServiceRequestException;

	/**
	 * 交换两个入库规则实例所对应的顺序号
	 * 
	 * @param configRuleCommitGuidFrist
	 * @param configRuleCommitGuidSecondGuid
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public void swapSequence(String configRuleCommitGuidFrist, String configRuleCommitGuidSecondGuid)
	// throws ServiceRequestException;
	//
	/**
	 * 运行指定规则
	 * 
	 * @param configRuleCommitGuid
	 * @throws ServiceRequestException
	 */
	// @Deprecated
	// public void runConfigRuleCommit(String configRuleCommitGuid) throws ServiceRequestException;

	/**
	 * 指定的foundation是否能作为问题项添加到EC中
	 * 
	 * @param boGuid
	 * @param phaseGuid
	 * @param ecTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	// public boolean isPIAddToEC(String boGuid, String phaseGuid, String ecTemplateGuid) throws
	// ServiceRequestException;

	/**
	 * 
	 * @param phaseGuid
	 *            生命周期guid
	 * @param classGuid
	 *            实例className
	 * @return
	 * @throws ServiceRequestException
	 */
	// public ConfigRuleBOPhaseSet getConfigRuleBOPhaseSet(String phaseGuid, String className) throws
	// ServiceRequestException;

}
