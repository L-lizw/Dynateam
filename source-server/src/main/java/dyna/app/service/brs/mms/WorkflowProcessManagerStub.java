package dyna.app.service.brs.mms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.brs.wfm.WFMImpl;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.wf.*;
import dyna.common.cache.CacheConstants;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.wf.*;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WorkflowProcessManagerStub extends AbstractServiceStub<MMSImpl>
{

	protected List<WorkflowProcessInfo> copy4CreateWorkflowProcess(List<WorkflowProcessInfo> sourceWfInfoList) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			List<WorkflowProcessInfo> resultList = new ArrayList<WorkflowProcessInfo>();
			if (!SetUtils.isNullList(sourceWfInfoList))
			{
				for (WorkflowProcessInfo process : sourceWfInfoList)
				{
					resultList.add(this.copyCreateWorkflowProcess(process));
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().executeToDoItem();
			return resultList;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().rollBack();
			;
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private WorkflowProcessInfo copyCreateWorkflowProcess(WorkflowProcessInfo sourceWfProcess) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			WorkflowProcessInfo newProcessInfo = (WorkflowProcessInfo) sourceWfProcess.clone();
			newProcessInfo.setGuid(null);
			newProcessInfo.setCreateUserGuid(currentUserGuid);
			newProcessInfo.setUpdateUserGuid(currentUserGuid);
			sds.save(newProcessInfo);
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
					.registerToDoItem(sds.getObjectDirectly(WorkflowProcessInfo.class, newProcessInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

			Map<String, String> old_new_guidMap = new HashMap<String, String>();
			List<WorkflowActivityInfo> activityInfoList = this.stubService.getWFM().listAllActivityInfo(sourceWfProcess.getGuid(), null);
			if (!SetUtils.isNullList(activityInfoList))
			{
				for (WorkflowActivityInfo activityInfo : activityInfoList)
				{
					old_new_guidMap.putAll(copyCreateWorkflowActivity(activityInfo, newProcessInfo.getGuid()));
				}
			}

			List<WorkflowLifecyclePhaseInfo> phaseInfoList = this.stubService.getWFM().listLifecyclePhaseInfo(sourceWfProcess.getGuid(), sourceWfProcess.getName());
			if (!SetUtils.isNullList(phaseInfoList))
			{
				for (WorkflowLifecyclePhaseInfo phaseInfo : phaseInfoList)
				{
					WorkflowLifecyclePhaseInfo newPhaseInfo = (WorkflowLifecyclePhaseInfo) phaseInfo.clone();
					newPhaseInfo.setGuid(null);
					newPhaseInfo.setMAWFfk(newProcessInfo.getGuid());
					newPhaseInfo.setCreateUserGuid(currentUserGuid);
					newPhaseInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newPhaseInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowLifecyclePhaseInfo.class, newPhaseInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}

			List<WorkflowTransitionInfo> transitionInfoList = this.stubService.getWFM().listTransitionInfo(sourceWfProcess.getGuid(), null);
			if (!SetUtils.isNullList(transitionInfoList))
			{
				for (WorkflowTransitionInfo transitionInfo : transitionInfoList)
				{
					WorkflowTransitionInfo newTransitionInfo = (WorkflowTransitionInfo) transitionInfo.clone();
					newTransitionInfo.setGuid(null);
					newTransitionInfo.setWorkflowGuid(newProcessInfo.getGuid());
					newTransitionInfo.setActFromGuid(old_new_guidMap.get(newTransitionInfo.getActFromGuid()));
					newTransitionInfo.setActToGuid(old_new_guidMap.get(newTransitionInfo.getActToGuid()));
					newTransitionInfo.setCreateUserGuid(currentUserGuid);
					newTransitionInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newTransitionInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowTransitionInfo.class, newTransitionInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}

			List<WorkflowEventInfo> eventList = this.stubService.getWFM().listEventInfo(sourceWfProcess.getGuid(), sourceWfProcess.getName());
			if (!SetUtils.isNullList(eventList))
			{
				for (WorkflowEventInfo eventInfo : eventList)
				{
					WorkflowEventInfo newEventInfo = eventInfo.clone();
					newEventInfo.setGuid(null);
					newEventInfo.setWffk(newProcessInfo.getGuid());
					newEventInfo.setCreateUserGuid(currentUserGuid);
					newEventInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newEventInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowEventInfo.class, newEventInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}
			return sds.getObjectDirectly(WorkflowProcessInfo.class, newProcessInfo.getGuid(), null);
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 返回变化前后活动的guidmap，活动变迁需更新成新的活动节点guid
	 * 
	 * @param activityInfo
	 * @param processModelGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> copyCreateWorkflowActivity(WorkflowActivityInfo activityInfo, String processModelGuid) throws ServiceRequestException
	{
		try
		{
			Map<String, String> old_new_guidMap = new HashMap<String, String>();
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			WorkflowActivityInfo newActivityInfo = new WorkflowActivityInfo();
			newActivityInfo.putAll(activityInfo);
			newActivityInfo.setGuid(null);
			newActivityInfo.setWorkflowGuid(processModelGuid);
			newActivityInfo.setCreateUserGuid(currentUserGuid);
			newActivityInfo.setUpdateUserGuid(currentUserGuid);

			sds.save(newActivityInfo);
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
					.registerToDoItem(sds.getObjectDirectly(WorkflowActivityInfo.class, newActivityInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

			old_new_guidMap.put(activityInfo.getGuid(), newActivityInfo.getGuid());

			WorkflowActivityType activeType = activityInfo.getType() == null ? null : WorkflowActivityType.getEnum(activityInfo.getType());
			WorkflowApplicationType applicationType = null;

			if (activeType == null || activeType == WorkflowActivityType.NOTIFY)
			{
				activeType = WorkflowActivityType.APPLICATION;
				applicationType = activityInfo.getType() == null ? null : WorkflowApplicationType.getEnum(activityInfo.getType());
				if (applicationType == null)
				{
					applicationType = WorkflowApplicationType.CHANGE_PHASE;
				}
			}

			switch (activeType)
			{
			case NOTIFY:
			case APPLICATION:

				switch (applicationType)
				{
				case ACTION:
					this.copyCreateWorkflowActryAction(activityInfo.getGuid(), newActivityInfo.getGuid());
					break;
				case CHANGE_PHASE:
					this.copyCreateWorkflowActrtLifecyclePhaseInfo(activityInfo.getGuid(), newActivityInfo.getGuid());
					break;
				case CHANGE_STATUS:
					this.copyCreateWorkflowActrtStatusInfo(activityInfo.getGuid(), newActivityInfo.getGuid());
					break;
				default:
					break;
				}

				break;
			default:
				break;
			}

			return old_new_guidMap;
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void copyCreateWorkflowActryAction(String sourceActivityGuid, String toActivityGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			List<WorkflowActrtActionInfo> actrtActionList = sds.listFromCache(WorkflowActrtActionInfo.class,
					new FieldValueEqualsFilter<>(WorkflowActrtActionInfo.WFACTIVITYGUID, sourceActivityGuid));
			if (!SetUtils.isNullList(actrtActionList))
			{
				for (WorkflowActrtActionInfo actionInfo : actrtActionList)
				{
					WorkflowActrtActionInfo newActionInfo = actionInfo.clone();
					newActionInfo.setGuid(null);
					newActionInfo.setWfActivityGuid(toActivityGuid);
					newActionInfo.setCreateUserGuid(currentUserGuid);
					newActionInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newActionInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtActionInfo.class, newActionInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void copyCreateWorkflowActrtLifecyclePhaseInfo(String sourceActivityGuid, String toActivityGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			List<WorkflowActrtLifecyclePhaseInfo> phaseInfoList = sds.listFromCache(WorkflowActrtLifecyclePhaseInfo.class,
					new FieldValueEqualsFilter<>(WorkflowActrtLifecyclePhaseInfo.MAWFACTFK, sourceActivityGuid));
			if (!SetUtils.isNullList(phaseInfoList))
			{
				for (WorkflowActrtLifecyclePhaseInfo phaseInfo : phaseInfoList)
				{
					WorkflowActrtLifecyclePhaseInfo newPhaseInfo = (WorkflowActrtLifecyclePhaseInfo) phaseInfo.clone();
					newPhaseInfo.setGuid(null);
					newPhaseInfo.setMAWFActfk(toActivityGuid);
					newPhaseInfo.setCreateUserGuid(currentUserGuid);
					newPhaseInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newPhaseInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtLifecyclePhaseInfo.class, newPhaseInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void copyCreateWorkflowActrtStatusInfo(String sourceActivityGuid, String toActivityGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			List<WorkflowActrtStatusInfo> actrtStatusInfoList = sds.listFromCache(WorkflowActrtStatusInfo.class,
					new FieldValueEqualsFilter<>(WorkflowActrtStatusInfo.MAWFACTFK, sourceActivityGuid));

			if (!SetUtils.isNullList(actrtStatusInfoList))
			{
				for (WorkflowActrtStatusInfo statusInfo : actrtStatusInfoList)
				{
					WorkflowActrtStatusInfo newstatusInfo = (WorkflowActrtStatusInfo) statusInfo.clone();
					newstatusInfo.setGuid(null);
					newstatusInfo.setMAWFActfk(toActivityGuid);
					newstatusInfo.setCreateUserGuid(currentUserGuid);
					newstatusInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newstatusInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtStatusInfo.class, newstatusInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteWorkflowProcess(String wfProcessGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 删除记录的生命周期阶段信息
			List<WorkflowLifecyclePhaseInfo> needDelWfLifecyclePhaseInfoList = sds.listFromCache(WorkflowLifecyclePhaseInfo.class,
					new FieldValueEqualsFilter<WorkflowLifecyclePhaseInfo>(WorkflowLifecyclePhaseInfo.MAWFFK, wfProcessGuid));
			sds.deleteFromCache(WorkflowLifecyclePhaseInfo.class, new FieldValueEqualsFilter<WorkflowLifecyclePhaseInfo>(WorkflowLifecyclePhaseInfo.MAWFFK, wfProcessGuid));
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfLifecyclePhaseInfoList,
					CacheConstants.CHANGE_TYPE.DELETE);

			// 删除工作流事件
			List<WorkflowEventInfo> needDelWorkflowEventInfoList = sds.listFromCache(WorkflowEventInfo.class,
					new FieldValueEqualsFilter<WorkflowEventInfo>(WorkflowEventInfo.WFFK, wfProcessGuid));
			sds.deleteFromCache(WorkflowEventInfo.class, new FieldValueEqualsFilter<WorkflowEventInfo>(WorkflowEventInfo.WFFK, wfProcessGuid));
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWorkflowEventInfoList,
					CacheConstants.CHANGE_TYPE.DELETE);

			List<WorkflowActivityInfo> activityList = sds.listFromCache(WorkflowActivityInfo.class,
					new FieldValueEqualsFilter<WorkflowActivityInfo>(WorkflowActivityInfo.MAWFFK, wfProcessGuid));
			if (!SetUtils.isNullList(activityList))
			{
				for (WorkflowActivityInfo activityInfo : activityList)
				{
					this.deleteWorkflowActivity(activityInfo);
				}
			}

			// 删除活动跃迁信息
			List<WorkflowTransitionInfo> needDelWorkflowTransitionInfoList = sds.listFromCache(WorkflowTransitionInfo.class,
					new FieldValueEqualsFilter<>(WorkflowTransitionInfo.WFFK, wfProcessGuid));
			sds.deleteFromCache(WorkflowTransitionInfo.class, new FieldValueEqualsFilter<>(WorkflowTransitionInfo.WFFK, wfProcessGuid));
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWorkflowTransitionInfoList,
					CacheConstants.CHANGE_TYPE.DELETE);
			// 删除本身
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItem(sds.get(WorkflowProcessInfo.class, wfProcessGuid),
					CacheConstants.CHANGE_TYPE.DELETE);
			sds.delete(WorkflowProcessInfo.class, wfProcessGuid);

//			DataServer.getTransactionManager().commitTransaction();
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().executeToDoItem();
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().rollBack();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	private void deleteWorkflowActivity(WorkflowActivityInfo activity) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		WorkflowActivityType activeType = activity.getType() == null ? null : WorkflowActivityType.getEnum(activity.getType());
		WorkflowApplicationType applicationType = null;

		if (activeType == null || activeType == WorkflowActivityType.NOTIFY)
		{
			activeType = WorkflowActivityType.APPLICATION;
			applicationType = activity.getType() == null ? null : WorkflowApplicationType.getEnum(activity.getType());
			if (applicationType == null)
			{
				applicationType = WorkflowApplicationType.CHANGE_PHASE;
			}
		}

		switch (activeType)
		{
		case NOTIFY:
		case APPLICATION:

			switch (applicationType)
			{
			case ACTION:
				List<WorkflowActrtActionInfo> needDelWfActrtActionList = sds.listFromCache(WorkflowActrtActionInfo.class,
						new FieldValueEqualsFilter<>(WorkflowActrtActionInfo.WFACTIVITYGUID, activity.getGuid()));
				sds.deleteFromCache(WorkflowActrtActionInfo.class, new FieldValueEqualsFilter<>(WorkflowActrtActionInfo.WFACTIVITYGUID, activity.getGuid()));
				((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfActrtActionList,
						CacheConstants.CHANGE_TYPE.DELETE);
				break;
			case CHANGE_PHASE:
				List<WorkflowActrtLifecyclePhaseInfo> needDelWfActrtLifecyclePhaseInfoList = sds.listFromCache(WorkflowActrtLifecyclePhaseInfo.class,
						new FieldValueEqualsFilter<>(WorkflowActrtLifecyclePhaseInfo.MAWFACTFK, activity.getGuid()));
				sds.deleteFromCache(WorkflowActrtLifecyclePhaseInfo.class, new FieldValueEqualsFilter<>(WorkflowActrtLifecyclePhaseInfo.MAWFACTFK, activity.getGuid()));
				((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfActrtLifecyclePhaseInfoList,
						CacheConstants.CHANGE_TYPE.DELETE);
				break;
			case CHANGE_STATUS:
				List<WorkflowActrtStatusInfo> needDelWfActrtStatusInfoList = sds.listFromCache(WorkflowActrtStatusInfo.class,
						new FieldValueEqualsFilter<>(WorkflowActrtStatusInfo.MAWFACTFK, activity.getGuid()));
				sds.deleteFromCache(WorkflowActrtStatusInfo.class, new FieldValueEqualsFilter<>(WorkflowActrtStatusInfo.MAWFACTFK, activity.getGuid()));
				((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfActrtStatusInfoList,
						CacheConstants.CHANGE_TYPE.DELETE);
				break;
			default:
				break;
			}

			break;
		default:
			break;
		}

		((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItem(activity, CacheConstants.CHANGE_TYPE.DELETE);
		sds.delete(activity);

	}

	protected WorkflowProcess editWorkflowProcess(WorkflowProcess editProcess) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{

			WorkflowProcessInfo newProcess = (WorkflowProcessInfo) editProcess.getWorkflowProcessInfo().clone();
			String processGuid = editProcess.getWorkflowProcessInfo().getGuid();
			String operation = null;
			if (StringUtils.isGuid(processGuid))
			{
				operation = CacheConstants.CHANGE_TYPE.UPDATE;
				List<WorkflowActivityInfo> activityInfoList = sds.listFromCache(WorkflowActivityInfo.class, new FieldValueEqualsFilter<>(WorkflowActivityInfo.MAWFFK, processGuid));
				if (!SetUtils.isNullList(activityInfoList))
				{
					for (WorkflowActivityInfo activityInfo : activityInfoList)
					{
						this.deleteWorkflowActivity(activityInfo);
					}
				}

				// 删除生命周期阶段记录
				List<WorkflowLifecyclePhaseInfo> needDelWfLifecyclePhaseInfoList = sds.listFromCache(WorkflowLifecyclePhaseInfo.class,
						new FieldValueEqualsFilter<WorkflowLifecyclePhaseInfo>(WorkflowLifecyclePhaseInfo.MAWFFK, processGuid));
				sds.deleteFromCache(WorkflowLifecyclePhaseInfo.class, new FieldValueEqualsFilter<WorkflowLifecyclePhaseInfo>(WorkflowLifecyclePhaseInfo.MAWFFK, processGuid));
				((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfLifecyclePhaseInfoList,
						CacheConstants.CHANGE_TYPE.DELETE);

				// 删除活动跃迁信息
				List<WorkflowTransitionInfo> needDelWfTransitionInfoList = sds.listFromCache(WorkflowTransitionInfo.class,
						new FieldValueEqualsFilter<WorkflowTransitionInfo>(WorkflowTransitionInfo.WFFK, processGuid));
				sds.deleteFromCache(WorkflowTransitionInfo.class, new FieldValueEqualsFilter<WorkflowTransitionInfo>(WorkflowTransitionInfo.WFFK, processGuid));
				((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfTransitionInfoList,
						CacheConstants.CHANGE_TYPE.DELETE);

				// 删除工作流事件
				List<WorkflowEventInfo> needDelWfEventInfoList = sds.listFromCache(WorkflowEventInfo.class,
						new FieldValueEqualsFilter<WorkflowEventInfo>(WorkflowEventInfo.WFFK, processGuid));
				sds.deleteFromCache(WorkflowEventInfo.class, new FieldValueEqualsFilter<WorkflowEventInfo>(WorkflowEventInfo.WFFK, processGuid));
				((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItemList(needDelWfEventInfoList,
						CacheConstants.CHANGE_TYPE.DELETE);
			}
			else
			{
				operation = CacheConstants.CHANGE_TYPE.INSERT;
				newProcess.setCreateUserGuid(currentUserGuid);
			}
			newProcess.setUpdateUserGuid(currentUserGuid);

			if (StringUtils.isNullString(newProcess.getWFName()))
			{
				throw new ServiceRequestException("WorkflowProcess name is null!");
			}

			sds.save(newProcess);
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
					.registerToDoItem(sds.getObjectDirectly(WorkflowProcessInfo.class, newProcess.getGuid(), null), operation);

			Map<String, String> activityNameGuidMap = new HashMap<String, String>();
			List<WorkflowActivity> activityList = editProcess.getEditList();
			if (!SetUtils.isNullList(activityList))
			{
				for (WorkflowActivity activity : activityList)
				{
					activityNameGuidMap.putAll(this.createWorkflowActivity(activity, newProcess.getGuid()));
				}
			}

			List<WorkflowLifecyclePhaseInfo> lifecyclePhase = editProcess.getLifecyclePhaseList();
			if (!SetUtils.isNullList(lifecyclePhase))
			{
				for (WorkflowLifecyclePhaseInfo phaseInfo : lifecyclePhase)
				{
					WorkflowLifecyclePhaseInfo newPhaseInfo = (WorkflowLifecyclePhaseInfo) phaseInfo.clone();
					newPhaseInfo.setGuid(null);
					newPhaseInfo.setMAWFfk(newProcess.getGuid());

					LifecycleInfo lifecycleInfo = ((EMMImpl) this.stubService.getEMM()).getLCStub().getLifecycleInfoByName(phaseInfo.getLifecycleName());
					if (lifecycleInfo == null)
					{
						throw new ServiceRequestException("not found lifecycle;name:" + phaseInfo.getLifecycleName());
					}
					newPhaseInfo.setLCMasterGuid(lifecycleInfo.getGuid());
					List<LifecyclePhaseInfo> allPhaseList = this.stubService.getEMM().listLifeCyclePhase(phaseInfo.getLifecycleName());
					if (!SetUtils.isNullList(allPhaseList))
					{
						for (LifecyclePhaseInfo lifecyclePhaseInfo : allPhaseList)
						{
							if (lifecyclePhaseInfo.getName().equals(phaseInfo.getPhaseName()))
							{
								newPhaseInfo.setLCPhaseGuid(lifecyclePhaseInfo.getGuid());
								break;
							}
						}
					}

					newPhaseInfo.setCreateUserGuid(currentUserGuid);
					newPhaseInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newPhaseInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowLifecyclePhaseInfo.class, newPhaseInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

				}
			}

			List<WorkflowTransitionInfo> transitionInfoList = editProcess.getEditTransitionList();
			if (!SetUtils.isNullList(transitionInfoList))
			{
				for (WorkflowTransitionInfo transitionInfo : transitionInfoList)
				{
					WorkflowTransitionInfo newTransitionInfo = (WorkflowTransitionInfo) transitionInfo.clone();
					newTransitionInfo.setGuid(null);
					newTransitionInfo.setWorkflowGuid(newProcess.getGuid());
					newTransitionInfo.setActFromGuid(activityNameGuidMap.get(newTransitionInfo.getActrtFromName()));
					newTransitionInfo.setActToGuid(activityNameGuidMap.get(newTransitionInfo.getActrtToName()));
					newTransitionInfo.setCreateUserGuid(currentUserGuid);
					newTransitionInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newTransitionInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowTransitionInfo.class, newTransitionInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}

			List<WorkflowEventInfo> eventList = editProcess.getEventList();
			if (!SetUtils.isNullList(eventList))
			{
				int sequence = 0;
				for (WorkflowEventInfo eventInfo : eventList)
				{
					WorkflowEventInfo newEventInfo = eventInfo.clone();
					newEventInfo.setGuid(null);
					newEventInfo.setWffk(newProcess.getGuid());
					newEventInfo.setSequence(sequence++);
					newEventInfo.setCreateUserGuid(currentUserGuid);
					newEventInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newEventInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowEventInfo.class, newEventInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

					this.createChildWorkflowEvent(newProcess.getGuid(), eventInfo.getChildren(), newEventInfo.getGuid());
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().executeToDoItem();

			return ((WFMImpl) (this.stubService.getWFM())).getProcessStub().getProcess(newProcess.getWFName());
		}
		catch (ServiceRequestException e)
		{
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().rollBack();
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (Exception e)
		{
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().rollBack();
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	private void createChildWorkflowEvent(String processGuid, List<Script> childList, String parentGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
			if (!SetUtils.isNullList(childList))
			{
				int childSequence = 0;
				for (Script script : childList)
				{
					WorkflowEventInfo childAction = (WorkflowEventInfo) script;
					WorkflowEventInfo newChild = childAction.clone();
					newChild.setGuid(null);
					// 脚本内容不放入数据库，保存文件名，通过文件名进行加载详细信息
					newChild.setScript(null);
					newChild.setSequence(childSequence++);
					newChild.setWffk(processGuid);
					newChild.setParentGuid(parentGuid);
					newChild.setCreateUserGuid(currentUserGuid);
					newChild.setUpdateUserGuid(currentUserGuid);

					sds.save(newChild);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowEventInfo.class, newChild.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

					this.createChildWorkflowEvent(processGuid, script.getChildren(), newChild.getGuid());
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 编辑是活动变迁用name牵连，保存后用guid对应创建活动变迁，这里返回name-guid
	 * 
	 * @param activity
	 * @param processModelGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> createWorkflowActivity(WorkflowActivity activity, String processModelGuid) throws ServiceRequestException
	{
		try
		{
			Map<String, String> name_guidMap = new HashMap<String, String>();

			WorkflowActivityInfo newWfActivityInfo = this.createActivity(activity, processModelGuid);
			name_guidMap.put(newWfActivityInfo.getName(), newWfActivityInfo.getGuid());

			WorkflowActivityType activeType = activity.getType() == null ? null : WorkflowActivityType.getEnum(activity.getType());
			WorkflowApplicationType applicationType = null;

			if (activeType == null || activeType == WorkflowActivityType.NOTIFY)
			{
				activeType = WorkflowActivityType.APPLICATION;
				applicationType = activity.getType() == null ? null : WorkflowApplicationType.getEnum(activity.getType());
				if (applicationType == null)
				{
					applicationType = WorkflowApplicationType.CHANGE_PHASE;
				}
			}
			if (activeType == WorkflowActivityType.APPLICATION)
			{
				applicationType = ((WorkflowApplicationActivity) activity).getWorkflowActivityInfo().getApplicationType();
			}

			switch (activeType)
			{
			case NOTIFY:
			case APPLICATION:

				switch (applicationType)
				{
				case ACTION:
					WorkflowActionActivity actionActivity = (WorkflowActionActivity) activity;
					this.createActionActivity(actionActivity, newWfActivityInfo.getGuid());
					break;
				case CHANGE_PHASE:
					WorkflowChangePhaseActivity phaseActivity = (WorkflowChangePhaseActivity) activity;
					this.createWorkflowChangePhaseActivity(phaseActivity, newWfActivityInfo.getGuid());
					break;
				case CHANGE_STATUS:
					WorkflowChangeStatusActivity changeStatusActivity = (WorkflowChangeStatusActivity) activity;
					this.createWorkflowChangeStatusActivity(changeStatusActivity, newWfActivityInfo.getGuid());
					break;
				default:
					break;
				}

				break;
			default:
				break;
			}

			return name_guidMap;
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void createActionActivity(WorkflowActionActivity actionActivity, String newActivityGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			List<WorkflowActrtActionInfo> actionInfoList = actionActivity.getActionList();
			if (!SetUtils.isNullList(actionInfoList))
			{
				for (WorkflowActrtActionInfo actionInfo : actionInfoList)
				{
					actionInfo.setGuid(null);
					actionInfo.setWfActivityGuid(newActivityGuid);
					actionInfo.setCreateUserGuid(currentUserGuid);
					actionInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(actionInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtActionInfo.class, actionInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

					this.createChildActrtAction(newActivityGuid, actionInfo.getChildren(), actionInfo.getGuid());
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	private void createChildActrtAction(String wfActrtGuid, List<Script> childList, String parentGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
			if (!SetUtils.isNullList(childList))
			{
				int childSequence = 0;
				for (Script script : childList)
				{
					WorkflowActrtActionInfo childAction = (WorkflowActrtActionInfo) script;
					WorkflowActrtActionInfo newChild = childAction.clone();
					newChild.setGuid(null);
					// 脚本内容不放入数据库，保存文件名，通过文件名进行加载详细信息
					newChild.setScript(null);
					newChild.setSequence(childSequence++);
					newChild.setWfActivityGuid(wfActrtGuid);
					newChild.setParentGuid(parentGuid);
					newChild.setCreateUserGuid(currentUserGuid);
					newChild.setUpdateUserGuid(currentUserGuid);

					sds.save(newChild);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtActionInfo.class, newChild.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);

					this.createChildWorkflowEvent(wfActrtGuid, script.getChildren(), newChild.getGuid());
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	private void createWorkflowChangePhaseActivity(WorkflowChangePhaseActivity phaseActivity, String newActivityGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			List<WorkflowPhaseChangeInfo> phaseInfoList = phaseActivity.getPhaseChangeList();
			if (!SetUtils.isNullList(phaseInfoList))
			{
				for (WorkflowPhaseChangeInfo phaseInfo : phaseInfoList)
				{

					WorkflowActrtLifecyclePhaseInfo newPhaseInfo = new WorkflowActrtLifecyclePhaseInfo();
					newPhaseInfo.setGuid(null);
					newPhaseInfo.setMAWFActfk(newActivityGuid);

					LifecycleInfo lifecycleInfo = ((EMMImpl) this.stubService.getEMM()).getLCStub().getLifecycleInfoByName(phaseInfo.getLifecycle());
					if (lifecycleInfo == null)
					{
						throw new ServiceRequestException("not found lifecycle;name:" + phaseInfo.getLifecycle());
					}
					newPhaseInfo.setLCMasterGuid(lifecycleInfo.getGuid());
					List<LifecyclePhaseInfo> allPhaseList = this.stubService.getEMM().listLifeCyclePhase(phaseInfo.getLifecycle());
					if (!SetUtils.isNullList(allPhaseList))
					{
						for (LifecyclePhaseInfo lifecyclePhaseInfo : allPhaseList)
						{
							if (lifecyclePhaseInfo.getName().equals(phaseInfo.getFromPhase()))
							{
								newPhaseInfo.setFromLCPhaseGuid(lifecyclePhaseInfo.getGuid());
							}
							if (lifecyclePhaseInfo.getName().equals(phaseInfo.getToPhase()))
							{
								newPhaseInfo.setToLCPhaseGuid(lifecyclePhaseInfo.getGuid());
							}
						}
					}
					newPhaseInfo.setCreateUserGuid(currentUserGuid);
					newPhaseInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(newPhaseInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtLifecyclePhaseInfo.class, newPhaseInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void createWorkflowChangeStatusActivity(WorkflowChangeStatusActivity phaseActivity, String newActivityGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			List<WorkflowActrtStatusInfo> statusInfoList = phaseActivity.getStatusChangeList();
			if (!SetUtils.isNullList(statusInfoList))
			{
				for (WorkflowActrtStatusInfo actionInfo : statusInfoList)
				{
					actionInfo.setGuid(null);
					actionInfo.setMAWFActfk(newActivityGuid);
					actionInfo.setCreateUserGuid(currentUserGuid);
					actionInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(actionInfo);
					((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo()
							.registerToDoItem(sds.getObjectDirectly(WorkflowActrtStatusInfo.class, actionInfo.getGuid(), null), CacheConstants.CHANGE_TYPE.INSERT);
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private WorkflowActivityInfo createActivity(WorkflowActivity activity, String processModelGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			WorkflowActivityInfo newActivityInfo = new WorkflowActivityInfo();
			newActivityInfo.putAll(activity.getWorkflowActivityInfo().clone());

			newActivityInfo.setGuid(null);
			newActivityInfo.setWorkflowGuid(processModelGuid);
			newActivityInfo.setCreateUserGuid(currentUserGuid);
			newActivityInfo.setUpdateUserGuid(currentUserGuid);

			if (activity.getType().equals("30"))
			{
				newActivityInfo.setType(((WorkflowApplicationActivity) activity).getWorkflowActivityInfo().getApplicationType().getValue());
			}

			sds.save(newActivityInfo);
			newActivityInfo = sds.getObjectDirectly(WorkflowActivityInfo.class, newActivityInfo.getGuid(), null);
			((WFMImpl) (this.stubService.getWFM())).getWfModelCacheStub().getWorkflowModelCacheInfo().registerToDoItem(newActivityInfo, CacheConstants.CHANGE_TYPE.INSERT);

			return newActivityInfo;
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}
}
