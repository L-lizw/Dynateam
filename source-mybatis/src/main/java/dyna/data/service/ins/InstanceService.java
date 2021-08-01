package dyna.data.service.ins;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.net.service.Service;

import java.util.Date;
import java.util.List;

public interface InstanceService extends Service
{
	/**
	 * 获取revision的Iteration
	 * 1、在需要判断权限的情况下，判断当前revision的查看权限，如果没有当前revision的肯定查看权限，不能查询
	 * 2、按照iterationId查询iteration，如果iterationId为空，查询所有iteration
	 *
	 * @param objectGuid      实例基本信息，不能为空，必须包含guid、classname
	 * @param iterationId     要查看的小版本号，可以为空，为空则按照iterationId正序排序后查出所有iteration
	 * @param searchCondition 查询条件，可以为空，包含需要查询的iteration属性
	 * @param isCheckAcl      不能为空，是否校验权限
	 * @param sessionId       当前操作者的sessionId，不能为空
	 * @return 实例结果集
	 */
	List<FoundationObject> listIteration(ObjectGuid objectGuid, Integer iterationId, SearchCondition searchCondition, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException;

	FoundationObject getFoundationObject(ObjectGuid objectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 根据指定的Guid查询数据
	 *
	 * @param guid       实例guid
	 * @param isCheckAcl 是否校验权限
	 * @param sessionId  当前操作者的sessionId，不能为空
	 * @return 实例信息
	 * @throws DynaDataSqlException
	 */
	FoundationObject getSystemFieldInfo(String guid, String classGuidOrClassName, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 根据指定的Guid查询数据
	 *
	 * @param guid       实例guid
	 * @param isCheckAcl 是否校验权限
	 * @param sessionId  当前操作者的sessionId，不能为空
	 * @return 实例信息
	 * @throws DynaDataSqlException
	 */
	FoundationObject getWipSystemFieldInfoByMaster(String masterGuid, String classGuidOrClassName, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 根据指定的Guid查询数据
	 *
	 * @param guid       实例guid
	 * @param isCheckAcl 是否校验权限
	 * @param sessionId  当前操作者的sessionId，不能为空
	 * @return 实例信息
	 * @throws DynaDataSqlException
	 */
	FoundationObject getSystemFieldInfoByMasterContext(String masterGuid, String classGuidOrClassName, DataRule dataRule, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException;

	/**
	 * 根据指定的SearchCondition查询数据, 不支持按照接口查询
	 * 目前支持的查询：
	 * 1、导入操作中的查询
	 * 2、查询单条数据
	 * 3、查询bomview、view
	 * 4、查询私有数据
	 * 5、查询共有数据
	 * 6、查询版本序列
	 *
	 * @param searchCondition 查询条件
	 * @param isCheckAcl      是否校验权限，不能为空
	 * @param sessionId       当前操作者的sessionId，不能为空
	 * @return 实例结果集
	 * @throws DynaDataException
	 */
	List<FoundationObject> query(SearchCondition searchCondition, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 数据的简单查询，根据传入的guid列表，批量查询返回对象，只返回对象的系统字段，不判断数据的权限。
	 *
	 * @param searchCondition
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	List<FoundationObject> queryByGuidList(String className, List<String> guidList, String sessionId) throws ServiceRequestException;

	/**
	 * 查询订阅项
	 *
	 * @param searchCondition        查询条件
	 * @param isCheckAcl             是否判断权限，不能为空
	 * @param sessionId              当前操作者的sessionId，不能为空
	 * @param subscriptionFolderGuid 订阅项文件夹的guid
	 * @return 订阅项结果集
	 * @throws DynaDataException
	 */
	List<FoundationObject> querySubscription(SearchCondition searchCondition, boolean isCheckAcl, String sessionId, String subscriptionFolderGuid) throws ServiceRequestException;

	/**
	 * 查询指定实例的所有分类数据
	 *
	 * @param objectGuid  要查询的实例，不能为空
	 * @param iterationId 要查询的实例小版本，为空，则查询最新的数据
	 * @param itemGuid    要查询的分类itemguid
	 * @return
	 * @throws DynaDataException
	 */
	FoundationObject queryForClassification(String foundationObjectGuid, String iterationId, String itemGuid, String sessionId) throws ServiceRequestException;

	/**
	 * 根据objectGuid获取master，包含id、name、alterid信息
	 *
	 * @param objectGuid
	 * @return FoundationObject
	 * @throws DynaDataException
	 */
	FoundationObject getMaster(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 判断对象是否有多个工作版本
	 *
	 * @param objectGuid
	 * @return
	 */
	boolean hasMutliWorkVersion(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 快速查询(只查询编号,或者编号+名称+alterid)
	 * 一般情况请不要使用该查询，目前仅在产品配置材料明细结构中使用（因为模板可以关联多接口的子阶业务对象，所以需要跨表查询）,需要根据模板进行查询，只返回有限的几个系统字段,且只做普通对象查询，不做关联查询
	 *
	 * @param searchKey     快速查询的关键字
	 * @param rowCntPerPate 每页记录数
	 * @param pageNum       当前页码
	 * @param caseSensitive 是否区分大小写
	 * @param isEquals      完全匹配还是部分匹配
	 * @param isOnlyId      仅仅查询id，还是查询id+name+alterid
	 * @param boNameList    限制的BO列表
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	List<FoundationObject> quickQuery(String searchKey, int rowCntPerPate, int pageNum, boolean caseSensitive, boolean isEquals, boolean isOnlyId, List<String> boNameList,
			boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 返回指定时间点的最新发布版本对象(不判断权限)
	 *
	 * @param objectGuid
	 * @param ruleTime
	 * @return
	 */
	FoundationObject queryByTime(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException;

	/**
	 * 取消检出item、bomview、view，将数据还原为检出前的状态
	 * <p>
	 * 1、如果不是检出数据不能取消检出
	 * 2、在需要判断权限的情况下，非本人检出的数据，没有取消检出该数据的肯定权限，不能取消检出
	 * 3、如果取消检出item，通过updatetime判断是否被修改过，如果被修改过，不能取消检出
	 * 4、如果数据在工作流中、已经被废弃、已经发布、预发布状态，不能取消检出
	 * 只有CRT、WIP、ECP状态的数据可以取消检出
	 * 5、如果是取消检出item
	 * 1）删除检出时创建的和文件夹的关联关系
	 * 2）工作项的关联关系
	 * 3）订阅项的关联关系
	 * 4）实例副本
	 *
	 * @param foundationObject 实例数据，不能为空且必须包含的信息：
	 *                         1、是否检出
	 *                         2、实例id（抛异常时用来定位实例）
	 *                         3、检出者
	 *                         4、ObjectGuid
	 *                         5、更新时间（用来判断实例是否被修改）
	 * @param isCheckAcl       是否校验取消检出权限，不能为空
	 * @param sessionId        当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	void cancelCheckout(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 检入item、bomview、view
	 * <p>
	 * 1、如果非检出者不可以检入，且检入者不是检出者，不能检入
	 * 2、如果检入item，通过updatetime判断是否被修改过，如果被修改过，不能检入
	 * 3、数据在工作流中、已经被废弃、已经发布、预发布状态，不能检入
	 * 只有CRT、WIP、ECP状态的数据可以取消检出
	 * 4、如果检入bomview、view，直接将ma_foundation检入，不需要处理内建字段和扩展字段
	 * 5、如果检入item，将ma_foundation检入，保存内建字段、扩展字段，同时备份小版本
	 *
	 * @param foundationObject 实例数据
	 *                         不能为空，必须包含的信息：
	 *                         1、ObjectGuid
	 *                         2、实例id（抛异常时用来定位实例）
	 *                         3、更新时间（用来判断实例是否被修改）
	 * @param isOwnerOnly      是否只允许非检出者检入，不能为空
	 * @param sessionId        当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	FoundationObject checkin(FoundationObject foundationObject, boolean isOwnerOnly, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 检出item、bomview、view
	 * 1、发布状态的数据不能检出
	 * 2、私有数据不能检出
	 * 3、需要判断权限的情况下，没有检出权限，不能检出
	 * 4、数据在工作流中、已经被废弃、已经发布、预发布状态，不能检出
	 * 只有CRT、WIP、ECP状态的数据可以取消检出
	 * 5、已经检出的数据不能检出
	 * 6、如果检出bomview、view，将小版本加1，数据改为已检出状态
	 * 7、如果检出item，将小版本加1，数据改为已检出状态，备份内建字段、扩展字段，备份订阅项、工作项和item关联关系
	 * 8、如果数据已经入库，备份和库下文件夹的关联关系
	 * 9、更新bi_file
	 *
	 * @param foundationObject 实例数据
	 *                         不能为空，必须包含的信息：
	 *                         1、ObjectGuid
	 *                         2、是否入库
	 *                         3、检出文件夹
	 *                         4、实例id（抛异常时用来定位实例）
	 *                         5、更新时间（用来判断实例是否被修改）
	 * @param checkOutUser     检出者
	 *                         如果检出者为空，默认为当前操作用户
	 * @param isCheckAcl       是否校验检出权限，不能为空
	 * @param sessionId        当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	FoundationObject checkout(FoundationObject foundationObject, String checkOutUser, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 创建/修订 item、bomview、view
	 * 1、在需要判断权限的情况下，如果创建item、bomview、view，没有创建的肯定权限，不能创建
	 * 2、在需要判断权限的情况下，如果修订item、bomview、view，没有修订的肯定权限，不能修订
	 * 3、如果创建item、bomview、view，创建主对象（ma_master）
	 * 4、创建revision（ma_foundation）
	 * 4.1、系统字段的处理
	 * 4.1.1、判断ID的长度是否和建模器所配置的长度一致，不一致，不能创建。其中，修订不需要检查id长度。
	 * 4.1.2、判断NAME、ALTERID、REVISIONID的长度是否和建模器所配置的长度一致，不一致，不能创建/修订。
	 * 4.1.3、不判断BOGUID、CLASSGUID、CLASSIFICATION、LOCATIONUSER、LIFECYCLEPHASE、OWNERUSER、OWNERGROUP、ECFLAG、
	 * ECFLAG$CLASS、ECFLAG$MASTER的长度，直接保存进数据库。
	 * 4.1.4、REVISIONIDSEQUENCE，同主对象最大REVISIONIDSEQUENCE+1。
	 * 4.1.5、ITERATIONID固定为1；
	 * ISCHECKOUT固定为'N'；
	 * CHECKOUTUSER、CHECKOUTTIME、SOURCEGUID、OBSOLETETIME、OBSOLETEUSER、FILEGUID、FILENAME、FILETYPE固定为NULL；
	 * CREATEUSER、UPDATEUSER固定为当前操作者；
	 * CREATETIME、UPDATETIME固定为当前时间；
	 * ISEXPORTTOERP固定为'0'。
	 * 4.1.6、FOTYPE，创建/修订item为1，创建/修订bomview为3，创建/修订view为5。
	 * 4.1.7、LOCATIONLIB，创建/修订item为当前操作者所在组对应的库；创建/修订bomview、view为bomview、view的end1的LOCATIONLIB。
	 * 4.1.8、COMMITFOLDER，如果指定入库位置，直接存入数据库；如果没有指定入库位置，为NULL。
	 * 4.1.9、STATUS，如果状态为ECP,PRE,RLS，直接存入数据库；如果状态是CRT、WIP、OBS，为NULL。
	 * 4.1.10、RELEASETIME，如果状态不是RLS，为NULL；如果状态为RLS，如果指定发布时间，直接存入数据库；如果状态为RLS，没有指定发布时间，将当前时间存入数据库。
	 * 4.1.12、COMMITTIME，如果创建/修订item，为NULL；如果创建/修订bomview、view，有对应的end1（ec中的bomview、view没有对应的end1）并且end1有入库时间，将当前时间存入数据库
	 * 4.1.13、RELEASEUSER，如果为RLS状态，指定发布人，将发布人存入数据库；如果为RLS状态，未指定发布人，将当前操作人存入数据库；如果不是RLS状态，为NULL。
	 * 4.1.14、EFFECTIVEUSER，如果是ECP状态、修订或者是bomview、view（起工作流的时候会用到），将当前操作者存入数据库；其他情况，为NULL。
	 * 4.1.15、将数据库连接符单引号（'）转换成双单引号（''）。
	 * <p>
	 * 4.2、如果系统规定只允许有一个修订版，且当前主对象有多个修订版，不能修订。
	 * 4.3、工作流中的数据不能建bomview、view
	 * 4.4、内建字段（如果有）、扩展字段（如果有）的处理
	 * 4.4.1、如果值为空，直接将空存入数据库
	 * 4.4.2、INTEGER、FLOAT类型，判断长度是否和建模器所配置的长度一致，不一致，不能创建/修订。
	 * 4.4.3、DATE、SDFYMDHMS类型，格式化为YYYY-MM-DD hh24:mi:ss后存入数据库。
	 * 4.4.4、OBJECT，取得guid、masterguid、classguid后存入数据库。
	 * 4.4.5、其他类型，判断长度是否和建模器所配置的长度一致，不一致，不能创建/修订；一致，直接存入数据库。
	 * 4.4.6、将数据库连接符单引号（'）转换成双单引号（''）。
	 * <p>
	 * 4.3、备份小版本
	 * 4.3.1、将系统字段、内建字段（如果有）、扩展字段（如果有）插入到_I表中
	 * 4.3.2、根据建模器配置删除多余的小版本
	 * <p>
	 * 4.4、更新流水码
	 * 4.4.1、如果是修订操作，根据master复制流水码。
	 * 4.4.2、如果是创建操作，记录该master用到的流水码，判断长度是否和建模器所配置的长度一致，不一致，不能创建。
	 *
	 * @param foundationObject       实例数据
	 *                               不能为空，必须包含的信息：
	 *                               1、ObjectGuid
	 *                               2、实例id（抛异常时用来定位实例）
	 *                               3、实例name（针对bomview、view操作时，抛异常时用来定位实例）
	 *                               4、如果创建vom、view，需要end1的guid
	 * @param originalFoundationGuid 原始版本guid，修订时不能为空
	 * @param isCheckAcl             是否校验权限，不能为空
	 * @param sessionId              当前操作者的sessionId，不能为空
	 * @return ObjectGuid
	 * @throws ServiceRequestException
	 */
	FoundationObject create(FoundationObject foundationObject, String originalFoundationGuid, boolean isCheckAcl, String sessionId, String fixTranId)
			throws ServiceRequestException;

	/**
	 * 删除item、bomview、view
	 * 1、在判断权限的情况下
	 * 1.1、如果删除关联关系，判断删除关联关系的权限，如果没有删除关联关系的肯定权限，不能删除
	 * 1.2、如果删除实例
	 * 1.2.1、如果可以查看检出后的数据，判断删除检出后的数据的权限，如果没有删除检出后数据的肯定权限，不能删除
	 * 1.2.2、如果不能查看检出后的数据，判断删除检出前的数据的权限，如果没有删除检出前数据的肯定权限，不能删除
	 * 2、如果是删除关联关系，直接删除关联关系，不再继续处理
	 * 3、如果删除实例
	 * 3.1、删除所有的关联关系
	 * 3.2、如果是ecp状态的实例，清空ECA的SI
	 * 3.3、如果删除的实例不是最后一个版本，重新计算 wip、rls 、master的最新版，通过updatetime判断是否被修改过，如果被修改过，不能检入；
	 * 数据在工作流中、已经被废弃、已经发布、预发布状态，不能检入，即只有CRT、WIP、ECP状态的数据可以取消检出
	 * 3.4、如果删除的是主对象
	 * 3.4.1、revision在工作流中、已经被废弃、已经发布、预发布状态、被检出，不能删除，只有CRT、WIP、ECP状态的revision可以删除。
	 * 如果更新者不是当前操作者，且revision已经被修改，不能删除。
	 * 3.4.2、如果revision没有对应的master，数据错误，不能删除。
	 *
	 * @param foundationObject 实例信息
	 *                         不能为空，必须包含的信息：
	 *                         1、ObjectGuid
	 *                         2、是否是快捷方式，如果是快捷方式，实例中要包含SHORTCUTFOLDERGUID信息
	 *                         3、实例id（抛异常时用来定位实例）
	 *                         4、实例name（针对bomview、view操作时，抛异常时用来定位实例）
	 *                         5、状态
	 *                         6、更新者
	 *                         7、更新时间
	 * @param isCheckAcl       是否校验权限，不能为空
	 * @param sessionId        当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	void delete(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 移交检出
	 * 1、没有检出的数据不能移交检出，没有指定移交检出接收者不能移交检出
	 * 2、需要判断检出者才能移交检出的情况下，不是检出者移交检出，不能移交检出
	 * 3、在需要判断权限的情况下，移交检出接收者没有检出的权限，不能移交检出
	 * 4、移交检出
	 * 4.1、如果是移交检出bomview、view，直接移交检出
	 * 4.2、如果是移交检出item，将item及item检出时的备份移交
	 * 4.3、修改实例和工作项的关联关系
	 * 5、工作流中的数据不能移交检出
	 * 6、被他人修改过的数据不能移交检出
	 *
	 * @param foundationObject 不能为空，必须包含的信息：
	 *                         1、如果是移交检出item，必须指定id，用来抛异常时定位数据
	 *                         2、如果是移交检出bomview、view，必须指定name，用来抛异常时定位数据
	 *                         3、locationlib
	 *                         4、ObjectGuid
	 *                         5、如果是检出实例，需要指定检出文件夹
	 *                         6、更新时间（用来判断是否被他人修改过）
	 * @param toUserGuid       不能为空，移交检出接收者
	 * @param isCheckAcl       不能为空，是否判断权限
	 * @param isOwnerOnly      不能为空，是否只能检出者可以移交检出
	 * @param sessionId        当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	FoundationObject transferCheckout(FoundationObject foundationObject, String toUserGuid, boolean isCheckAcl, boolean isOwnerOnly, String sessionId, String fixTranId)
			throws ServiceRequestException;

	/**
	 * 将revision回滚到某一个小版本
	 * 1、只有WIP、CRT、ECP状态的数据可以回滚
	 * 2、用小版本备份表中对应的小版本信息更新revision信息
	 * 3、用isOwnerOnly参数来控制，是否只有检出者才能回滚。如果只有检出者可以执行回滚操作，且当前操作者不是检出者，不能回滚。
	 * 4、删除回滚到的小版本之后的小版本的备份。
	 * 5、如果数据被检出，删除检出数据的备份、检出数据的备份和工作项的关联关系
	 * 6、如果数据被检出且被订阅，删除检出数据的备份订阅
	 *
	 * @param objectGuid  实例基本信息，不能为空，必须包含guid
	 * @param iterationId 小版本号，不能为空，要回滚到的小版本id
	 * @param isOwnerOnly 是否只有检出者才能回滚，不能为空
	 * @param sessionId   当前操作者的sessionid，不能为空
	 * @throws ServiceRequestException
	 */
	void rollbackIteration(ObjectGuid objectGuid, Integer iterationId, boolean isOwnerOnly, String sessionId) throws ServiceRequestException;

	/**
	 * 取消废弃
	 * 将数据的废弃时间清空
	 *
	 * @param guid       要废弃数据的guid，不能为空
	 * @param isCheckAcl 预留，暂未用到
	 * @param sessionId  当前操作者的sessionid，不能为空
	 * @throws ServiceRequestException
	 */
	void cancelObsolete(String guid, String classGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 执行废弃操作，设置废弃时间为当前系统时间
	 * 如果废弃临时表中有当前实例的废弃时间需要删除
	 * 重新计算 wip、rls 、master的最新版
	 *
	 * @param foundationObjectGuid
	 * @param isFromWF             是否从工作流废弃
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	void obsolete(ObjectGuid foundationObjectGuid, boolean isFromWF, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 实例状态变更
	 *
	 * @param from                 变更前状态，不能为空
	 * @param to                   变更后状态，不能为空
	 * @param foundationObjectGuid 要变更状态的实例，不能为空
	 * @param sessionId            当前操作者的sessionid，不能为空
	 * @throws ServiceRequestException
	 */
	void changeStatus(SystemStatusEnum from, SystemStatusEnum to, ObjectGuid foundationObject, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 保存特殊字段数据
	 * 1、如果数据被检出，修改检出数据
	 * 2、修改ownerUser
	 * 2.1、判断是否有修改ownerUser的权限，如果没有权限，不能修改。
	 * 2.2、如果被他人修改过，不能修改。
	 * 2.3、工作流中的数据，不能修改。
	 * 3、修改ownerGroup
	 * 3.1、判断是否有修改ownerGroup的权限，如果没有权限，不能修改。
	 * 3.2、如果被他人修改过，不能修改。
	 * 3.3、工作流中的数据，不能修改。
	 * 4、修改revisionId
	 * 4.1、不判断权限。
	 * 4.2、如果被他人修改过，不能修改。
	 * 4.3、工作流中的数据，不能修改。
	 * 5、修改生命周期，直接将原生命周期修改为目标生命周期
	 * 6、更新status
	 * 6.1、将状态更改为发布状态（RLS），同时修改发布者为rlsUserGuid（如果为空，为当前操作者）、发布时间为当前时间
	 * 6.2、将状态更改为预发布状态（PRE）或变更中状态（ECP），直接将PRE、ECP写入到status字段中
	 * 6.3、将状态更改为其他状态（CRT、WIP、OBS），将status字段清空
	 *
	 * @param objectGuid         要修改的对象信息，不能为空，必须包含guid
	 * @param ownerUserGuid      所有者的guid，需要同时传入updatetime
	 * @param ownerGroupGuid     所在组的guid，需要同时传入updatetime
	 * @param revisionId         版本号，需要同时传入updatetime
	 * @param updatetime         更新时间，用来判断数据是否被他人修改过
	 * @param fromLifeCyclePhase 修改前的生命周期，需要和toLifeCyclePhase同时传入
	 * @param toLifeCyclePhase   修改后的生命周期，需要和fromLifeCyclePhase同时传入
	 * @param rlsUserGuid        发布人的guid（status为发布时用）
	 * @param status             实例状态
	 * @param isCheckAcl         是否校验权限
	 * @param sessionId          当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	void save(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, String revisionId, Date updatetime, LifecyclePhaseInfo fromLifeCyclePhase,
			LifecyclePhaseInfo toLifeCyclePhase, String masterName, SystemStatusEnum status, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 保存FoundationObject（FO）
	 * 1、保存私有的普通FO、非检出状态的bomview、view时会自动做检出检入操作
	 * 2、库中的一般FO保存检出后的数据（需要根据参数isCheckCheckout判断）
	 * 3、EC相关FO（ec eca ect）直接保存
	 * 普通FO是指除了bomview、view和EC相关FO之外的FO
	 *
	 * @param foundationObject
	 * @param isCheckAcl       是否检查权限
	 * @param sessionId
	 * @param isCheckCheckout  是否需要判断数据处于检出状态（只有库中的普通FO才会检查）
	 *                         此处暂时不对isCheckCheckout做判断 2012年1月9日
	 * @throws ServiceRequestException
	 */
	FoundationObject save(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId, boolean isCheckCheckout) throws ServiceRequestException;

	/**
	 * 修改foundation owner
	 * 根据传入的ownerUserGuid、ownerGroupGuid修改owner，
	 * 同时修改更新时间和更新人（根据sessionId取得当前登录人）
	 *
	 * @param foundationGuid 实例的guid
	 * @param ownerUserGuid  所有者的guid
	 * @param ownerGroupGuid 所在组的guid
	 * @param sessionId      当前操作者的sessionid
	 * @throws ServiceRequestException
	 */
	void saveOwner(String foundationGuid, String classGuid, String ownerUserGuid, String ownerGroupGuid, String sessionId) throws ServiceRequestException;

	/**
	 * 删除master
	 * 遍历master下所有的版本，
	 * 1、存在检出状态则不能删除 抛出异常
	 * 2、存在PRE、RLS、OBS状态的不能删除 抛出异常
	 * 3、在需要判断权限的情况下，如果当前用户对其中一个版本没有权限则不能删除 抛出异常
	 * 4、数据处在工作流或者已经删除 抛出异常
	 * 删除master，会关联删除所有版本
	 *
	 * @param masterGuid master的guid
	 * @param isCheckAcl 是否需要判断权限
	 * @param sessionId  当前操作者的sessionid
	 * @throws ServiceRequestException
	 */
	void deleteMaster(String masterGuid, String classGuid, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 根据系统配置获取下一个版本号（修订版本用）
	 *
	 * @param objectGuid 实例基本信息，不能为空，必须包含guid信息
	 * @return
	 * @throws ServiceRequestException
	 */
	String getNewRevisionId(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据系统配置获取第一个版本号
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	String getFirstRevisionId(int startRevisionIdSequence) throws ServiceRequestException;

	/**
	 * 保存FoundationObject（FO） （目前项目管理使用，不判断更新时间）
	 * 1、保存私有的普通FO、非检出状态的bomview、view时会自动做检出检入操作
	 * 2、库中的一般FO根据检出状态保存相应的记录
	 * 3、EC相关FO（ec eca ect）直接保存
	 * 普通FO是指除了bomview、view和EC相关FO之外的FO
	 *
	 * @param foundationObject
	 * @param isCheckAcl       是否检查权限
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	FoundationObject saveForIgnoreUpdateTime(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 不通过工作流，执行发布操作，设置发布时间为当前系统时间
	 * 重新计算 wip、rls 、master的最新版
	 *
	 * @param foundationObjectGuid
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	void release(ObjectGuid foundationObjectGuid, String sessionId, String fixTranId) throws ServiceRequestException;

	void copyFileOnlyRecord(FoundationObject destObject, FoundationObject srcObject, String sessionId, String fixTranId) throws ServiceRequestException;

	/**
	 * 变更生命周期
	 *
	 * @param foundationGuid
	 * @param foundationClassGuid
	 * @param lifecyclePhaseGuid
	 * @param isCheckAuth
	 * @param fixTranId
	 * @throws ServiceRequestException
	 */
	void changePhase(String foundationGuid, String foundationClassGuid, String lifecyclePhaseGuid, boolean isCheckAuth, String fixTranId) throws ServiceRequestException;

	/**
	 * 保存实例的file信息
	 *
	 * @param guid     要更新的revision的guid，不能为空
	 * @param fileGuid 文件的guid
	 * @param fileName 文件的name
	 * @param fileType 文件的type
	 * @throws ServiceRequestException
	 */
	void saveFile(String guid, String classGuid, String sessionId, String fileGuid, String fileName, String fileType, String MD5) throws ServiceRequestException;

	/**
	 * 删除对象相关数据，包括：1、0表以外的其他表
	 * 2、作为end2时的结构数据
	 * 3、作为end1时的关系数据，包括关系master数据，包含0表在内的所有关系数据，以及结构数据
	 *
	 * @param objectGuid
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	void deleteReferenceData(ObjectGuid objectGuid, String sessionId) throws ServiceRequestException;

	/**
	 * 设置是否已导入至ERP
	 *
	 * @param guid          实例的guid
	 * @param isExportToERP 是否已导入至ERP
	 * @param isCheckAcl    预留，暂未用到
	 * @param sessionId     预留，暂未用到
	 * @throws DynaDataException
	 */
	void setIsExportToERP(String guid, String className, boolean isExportToERP, boolean isCheckAcl, String sessionId) throws DynaDataException, ServiceRequestException;
}
