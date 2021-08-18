package dyna.app.service.brs.mms;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.lf.Lifecycle;
import dyna.common.bean.model.lf.LifecyclePhase;
import dyna.common.dto.model.lf.LifecycleGate;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowActrtLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowLifecyclePhaseInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
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
public class LifecycleManagerStub extends AbstractServiceStub<MMSImpl>
{

	protected List<LifecycleInfo> copy4CreateLifecycle(List<LifecycleInfo> lifecycleInfoList) throws ServiceRequestException
	{
		List<LifecycleInfo> resultList = new ArrayList<LifecycleInfo>();
		if (!SetUtils.isNullList(lifecycleInfoList))
		{
			for (LifecycleInfo lifecycleInfo : lifecycleInfoList)
			{
				resultList.add(this.copyCreateLifecycle(lifecycleInfo));
			}
		}
		return resultList;
	}

	private LifecycleInfo copyCreateLifecycle(LifecycleInfo sourceLifecycleInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserguid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			LifecycleInfo newLifecycleInfo = (LifecycleInfo) sourceLifecycleInfo.clone();
			newLifecycleInfo.setGuid(null);
			newLifecycleInfo.setCreateUserGuid(currentUserguid);
			newLifecycleInfo.setUpdateUserGuid(currentUserguid);
			sds.save(newLifecycleInfo);

			List<LifecyclePhaseInfo> lifecyclePhaseInfoList = sds.listFromCache(LifecyclePhaseInfo.class,
					new FieldValueEqualsFilter<LifecyclePhaseInfo>(LifecyclePhaseInfo.MASTERFK, sourceLifecycleInfo.getGuid()));
			if (!SetUtils.isNullList(lifecyclePhaseInfoList))
			{
				for (LifecyclePhaseInfo lifecyclePhaseInfo : lifecyclePhaseInfoList)
				{
					this.copyCreateLifecyclePhase(lifecyclePhaseInfo, newLifecycleInfo.getGuid());
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
			return newLifecycleInfo;
		}
		catch (Exception e)
		{
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

	private void copyCreateLifecyclePhase(LifecyclePhaseInfo sourceLifecyclePhaseInfo, String tolifecycleMasterguid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserguid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
			LifecyclePhaseInfo newLifeCyclePhaseInfo = (LifecyclePhaseInfo) sourceLifecyclePhaseInfo.clone();
			newLifeCyclePhaseInfo.setGuid(null);
			newLifeCyclePhaseInfo.setMasterfk(tolifecycleMasterguid);
			newLifeCyclePhaseInfo.setCreateUserGuid(currentUserguid);
			newLifeCyclePhaseInfo.setUpdateUserGuid(currentUserguid);
			sds.save(newLifeCyclePhaseInfo);
		}
		catch (Exception e)
		{
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

	protected void deleteLifecycle(String guid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			List<LifecyclePhaseInfo> lifecyclePhaseList = sds.listFromCache(LifecyclePhaseInfo.class,
					new FieldValueEqualsFilter<LifecyclePhaseInfo>(LifecyclePhaseInfo.MASTERFK, guid));
			if (!SetUtils.isNullList(lifecyclePhaseList))
			{
				for (LifecyclePhaseInfo lifecyclePhaseInfo : lifecyclePhaseList)
				{
					// 工作流模板适用生命周期阶段删除
					sds.deleteFromCache(WorkflowLifecyclePhaseInfo.class, new FieldValueEqualsFilter<>(WorkflowLifecyclePhaseInfo.LCPHASEGUID, lifecyclePhaseInfo.getGuid()));
					// 工作流模板转换生命周期节点，记录的阶段转换记录删除
					sds.deleteFromCache(WorkflowActrtLifecyclePhaseInfo.class,
							new FieldValueEqualsFilter<>(WorkflowActrtLifecyclePhaseInfo.FROMLCPHASEGUID, lifecyclePhaseInfo.getGuid()));
					sds.deleteFromCache(WorkflowActrtLifecyclePhaseInfo.class,
							new FieldValueEqualsFilter<>(WorkflowActrtLifecyclePhaseInfo.TOLCPHASEGUID, lifecyclePhaseInfo.getGuid()));
					sds.deleteFromCache(LifecycleGate.class, new FieldValueEqualsFilter<LifecycleGate>(LifecycleGate.DETAILFK, lifecyclePhaseInfo.getGuid()));
					sds.delete(lifecyclePhaseInfo);
				}
			}

			sds.delete(LifecycleInfo.class, guid);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
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

	protected Lifecycle editLifecycle(Lifecycle lifecycle) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

		try
		{

			Map<String, String> changeMap = new HashMap<String, String>();

			LifecycleInfo lifecycleInfo = (LifecycleInfo) lifecycle.getInfo().clone();
			String lifecycleGuid = lifecycleInfo.getGuid();
			if (lifecycleGuid == null)
			{
				lifecycleInfo.setCreateUserGuid(currentUserGuid);
			}
			else
			{
				List<LifecyclePhaseInfo> nowList = sds.listFromCache(LifecyclePhaseInfo.class,
						new FieldValueEqualsFilter<LifecyclePhaseInfo>(LifecyclePhaseInfo.MASTERFK, lifecycleGuid));
				if (!SetUtils.isNullList(nowList))
				{
					for (LifecyclePhaseInfo phaseInfo : nowList)
					{
						changeMap.put(phaseInfo.getName(), phaseInfo.getGuid());
					}
				}

			}
			lifecycleInfo.setUpdateUserGuid(currentUserGuid);

			sds.save(lifecycleInfo);

			List<LifecyclePhase> phaseList = lifecycle.getLifecyclePhaseList();

			int sequence = 0;
			if (!SetUtils.isNullList(phaseList))
			{
				for (LifecyclePhase phase : phaseList)
				{
					phase.getInfo().setGuid(changeMap.get(phase.getInfo().getName()));
					changeMap.remove(phase.getInfo().getName());
					this.createLifePhase(phase, lifecycleInfo.getGuid(), sequence++);
				}
			}

			if (!SetUtils.isNullMap(changeMap))
			{
				for (Map.Entry<String, String> entry : changeMap.entrySet())
				{
					sds.delete(LifecyclePhaseInfo.class, entry.getValue());
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
			return DataServer.getLifecycleModelService().getLifecycleByGuid(lifecycleInfo.getGuid());
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw new ServiceRequestException(null, e.getMessage(), e);
		}
		catch (Exception e)
		{
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

	private String createLifePhase(LifecyclePhase phase, String lifecycleGuid, int sequence)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

		LifecyclePhaseInfo phaseInfo = (LifecyclePhaseInfo) phase.getInfo().clone();
		phaseInfo.setLifecycleSequence(sequence);
		phaseInfo.setMasterfk(lifecycleGuid);
		phaseInfo.setCreateUserGuid(currentUserGuid);
		phaseInfo.setUpdateUserGuid(currentUserGuid);

		return sds.save(phaseInfo);

	}

}
