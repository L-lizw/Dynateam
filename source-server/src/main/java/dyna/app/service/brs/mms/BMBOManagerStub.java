package dyna.app.service.brs.mms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeBoInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class BMBOManagerStub extends AbstractServiceStub<MMSImpl>
{

	protected void checkAndReBuildBusinessModel() throws ServiceRequestException
	{
		List<BMInfo> bmInfoList = this.stubService.getEMM().listBizModel();
		if (bmInfoList == null)
		{
			bmInfoList = new ArrayList<BMInfo>();
		}
		BMInfo shareBMInfo = this.stubService.getEMM().getSharedBizModel();
		if (shareBMInfo != null)
		{
			bmInfoList.add(shareBMInfo);
		}
		if (!SetUtils.isNullList(bmInfoList))
		{
			// for()
		}
	}

	/**
	 * 建模器穿过来的guid为原bmguid，name为重命名后的
	 * 
	 * @param sourceBMInfoList
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<BMInfo> copy4CreateBusinessModel(List<BMInfo> sourceBMInfoList) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		List<BMInfo> resultList = new ArrayList<BMInfo>();

		try
		{
			if (!SetUtils.isNullList(sourceBMInfoList))
			{
				for (BMInfo bmInfo : sourceBMInfoList)
				{
					resultList.add(this.createBusinessModel(bmInfo));
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
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

		return resultList;
	}

	private BMInfo createBusinessModel(BMInfo sourcebmInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();

		String oldBMguid = sourcebmInfo.getGuid();
		BMInfo newBMInfo = sourcebmInfo.clone();
		int i = -1;
		List<BMInfo> list = DataServer.getBusinessModelService().listBusinessModelInfo();
		if (list != null)
		{
			for (BMInfo info : list)
			{
				if (info.getSequence() > i)
				{
					i = info.getSequence();
				}
			}
		}
		i++;
		try
		{
			newBMInfo.setGuid(null);
			newBMInfo.setCreateUserGuid(currentUserGuid);
			newBMInfo.setUpdateUserGuid(currentUserGuid);
			newBMInfo.setSequence(i);
			sds.save(newBMInfo);

			List<BOInfo> boInfoList = this.stubService.getEMM().listBizObjectOfModelByBMGuid(oldBMguid);
			if (!SetUtils.isNullList(boInfoList))
			{
				for (BOInfo boInfo : boInfoList)
				{
					if (boInfo.getParent() == null)
					{
						this.createBusinsessObject(boInfo, newBMInfo, null);
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			throw e;
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

		return newBMInfo;

	}

	private void createBusinsessObject(BOInfo sourceBOInfo, BMInfo bmInfo, BOInfo parentBOInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();

		try
		{
			BOInfo newBOInfo = sourceBOInfo.clone();
			newBOInfo.setGuid(null);
			newBOInfo.setCreateUserGuid(currentUserGuid);
			newBOInfo.setUpdateUserGuid(currentUserGuid);
			newBOInfo.setBMGuid(bmInfo.getGuid());
			newBOInfo.setParentBOGuid(parentBOInfo == null ? null : parentBOInfo.getGuid());

			sds.save(newBOInfo);

			List<BOInfo> subBOInfoList = this.stubService.getEMM().listSubBOInfoByBM(sourceBOInfo.getBMGuid(), sourceBOInfo.getGuid());
			if (!SetUtils.isNullList(subBOInfoList))
			{
				for (BOInfo subBOInfo : subBOInfoList)
				{
					this.createBusinsessObject(subBOInfo, bmInfo, newBOInfo);
				}
			}
		}
		catch (ServiceRequestException e)
		{
			throw e;
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

	protected void copy4CreateBusinessObject(List<BOInfo> sourceBOInfoList, String bmguid, String parentBOguid) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			BMInfo bmInfo = this.stubService.getEMM().getBizModel(bmguid);
			BOInfo parentBOInfo = this.stubService.getEMM().getBizObject(bmguid, parentBOguid);
			if (!SetUtils.isNullList(sourceBOInfoList))
			{
				for (BOInfo boInfo : sourceBOInfoList)
				{
					this.createBusinsessObject(boInfo, bmInfo, parentBOInfo);
				}
			}
//			DataServer.getTransactionManager().commitTransaction();

		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
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

	/**
	 * 新建业务模型
	 * 
	 * @param bmInfo
	 * @throws ServiceRequestException
	 */
	protected void createnewBusinessModel(BMInfo bmInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			String currentUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();
			bmInfo.setCreateUserGuid(currentUserGuid);
			bmInfo.setUpdateUserGuid(currentUserGuid);
			sds.save(bmInfo);

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

	protected BOInfo createBusinessObject(BOInfo boInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			BMInfo bmInfo = this.stubService.getEMM().getBizModel(boInfo.getBMGuid());
			if (bmInfo != null)
			{

				boInfo.setBMGuid(boInfo.getBMGuid());
				boInfo.setCreateUserGuid(currentUserGuid);
				boInfo.setUpdateUserGuid(currentUserGuid);

				UpperKeyMap param = new UpperKeyMap();
				param.put(BOInfo.BMGUID, boInfo.getBMGuid());
				param.put(BOInfo.PARENTBOGUID, boInfo.getParentBOGuid());
				List<BOInfo> existsList = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<BOInfo>(param));
				boInfo.setSequence(existsList == null ? 0 : existsList.size());
				sds.save(boInfo);

//				DataServer.getTransactionManager().commitTransaction();

				return this.stubService.getEMM().getBizObject(boInfo.getBMGuid(), boInfo.getGuid());
			}
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
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

		return null;
	}

	protected void deleteBMInfo(String bmInfoguid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.delete(BMInfo.class, bmInfoguid);

		this.deleteBOInfoByBM(bmInfoguid);

	}

	protected void deleteBOInfoByBM(String bmguid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		sds.deleteFromCache(BOInfo.class, new FieldValueEqualsFilter<>(BOInfo.BMGUID, bmguid));
	}

	protected void deleteBOInfo(String boGuid) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			UpperKeyMap param = new UpperKeyMap();
			BOInfo boInfo = sds.getObject(BOInfo.class, boGuid);

			// 同一级后面的顺序号全部减一
			param.clear();
			param.put(BOInfo.BMGUID, boInfo.getBMGuid());
			param.put(BOInfo.PARENTBOGUID, boInfo.getParentBOGuid());

			List<BOInfo> otherList = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(param));
			if (!SetUtils.isNullList(otherList))
			{
				for (BOInfo bmbo : otherList)
				{
					if (bmbo.getSequence() > boInfo.getSequence())
					{
						bmbo.setSequence(bmbo.getSequence() - 1);
						sds.save(bmbo);
					}
				}
			}

			// 删除工作流模板记录的业务范围中的记录
			sds.deleteFromCache(WorkflowTemplateScopeBoInfo.class, new FieldValueEqualsFilter<>(WorkflowTemplateScopeBoInfo.BOGUID, boInfo.getGuid()));
			// 删除工作流节点bo无效记录
			sds.deleteFromCache(WorkflowTemplateActClassInfo.class, new FieldValueEqualsFilter<>(WorkflowTemplateActClassInfo.BOGUID, boInfo.getGuid()));
			sds.delete(boInfo);

			this.deleteChildBOInfo(boInfo.getBMGuid(), boGuid);

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

	private void deleteChildBOInfo(String bmGuid, String parentBOGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			UpperKeyMap param = new UpperKeyMap();
			param.put(BOInfo.BMGUID, bmGuid);
			param.put(BOInfo.PARENTBOGUID, parentBOGuid);

			// 子阶全部删除
			List<BOInfo> childBMBOList = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(param));
			if (!SetUtils.isNullList(childBMBOList))
			{
				for (BOInfo boInfo : childBMBOList)
				{
					sds.deleteFromCache(WorkflowTemplateScopeBoInfo.class, new FieldValueEqualsFilter<>(WorkflowTemplateScopeBoInfo.BOGUID, boInfo.getGuid()));
					sds.deleteFromCache(WorkflowTemplateActClassInfo.class, new FieldValueEqualsFilter<>(WorkflowTemplateActClassInfo.BOGUID, boInfo.getGuid()));
					sds.delete(boInfo);
					this.deleteChildBOInfo(bmGuid, boInfo.getGuid());
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

	protected List<BMInfo> moveBmInfo(List<BMInfo> bmList, boolean isUp) throws ServiceRequestException
	{
		List<BMInfo> result = new ArrayList<BMInfo>();
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			if (!SetUtils.isNullList(bmList))
			{
				Collections.sort(bmList, new Comparator<BMInfo>()
				{

					@Override
					public int compare(BMInfo o1, BMInfo o2)
					{
						return o1.getSequence() - o2.getSequence();
					}
				});

				if (isUp)
				{
					UpperKeyMap param = new UpperKeyMap();
					param.put(BMInfo.SEQUENCE, bmList.get(0).getSequence() - 1);
					BMInfo aboveBMInfo = sds.listFromCache(BMInfo.class, new FieldValueEqualsFilter<>(param)).get(0);
					aboveBMInfo.setSequence(bmList.get(bmList.size() - 1).getSequence());
					aboveBMInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(aboveBMInfo);

					for (BMInfo bmInfo : bmList)
					{
						bmInfo.setSequence(bmInfo.getSequence() - 1);
						bmInfo.setUpdateUserGuid(currentUserGuid);
						sds.save(bmInfo);
						result.add(bmInfo);
					}
				}
				else
				{
					UpperKeyMap param = new UpperKeyMap();
					param.put(BMInfo.SEQUENCE, bmList.get(0).getSequence() + 1);
					BMInfo aboveBMInfo = sds.listFromCache(BMInfo.class, new FieldValueEqualsFilter<>(param)).get(bmList.size() - 1);
					aboveBMInfo.setSequence(bmList.get(0).getSequence());
					aboveBMInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(aboveBMInfo);

					for (BMInfo bmInfo : bmList)
					{
						bmInfo.setSequence(bmInfo.getSequence() + 1);
						bmInfo.setUpdateUserGuid(currentUserGuid);
						sds.save(bmInfo);
						result.add(bmInfo);
					}
				}
			}

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

		return result;
	}

	protected List<BOInfo> moveBOInfo(List<BOInfo> boList, boolean isUp) throws ServiceRequestException
	{
		List<BOInfo> result = new ArrayList<BOInfo>();
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			if (!SetUtils.isNullList(boList))
			{
				Collections.sort(boList, new Comparator<BOInfo>()
				{

					@Override
					public int compare(BOInfo o1, BOInfo o2)
					{
						return o1.getSequence() - o2.getSequence();
					}
				});

				if (isUp)
				{
					UpperKeyMap param = new UpperKeyMap();
					param.put(BOInfo.SEQUENCE, boList.get(0).getSequence() - 1);
					param.put(BOInfo.PARENTBOGUID, boList.get(0).getParentBOGuid());
					param.put(BOInfo.BMGUID, boList.get(0).getBMGuid());

					BOInfo aboveBOInfo = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<BOInfo>(param)).get(0);

					aboveBOInfo.setSequence(boList.get(boList.size() - 1).getSequence());
					aboveBOInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(aboveBOInfo);

					for (BOInfo boInfo : boList)
					{
						boInfo.setSequence(boInfo.getSequence() - 1);
						boInfo.setUpdateUserGuid(currentUserGuid);
						sds.save(boInfo);
						result.add(boInfo);
					}
				}
				else
				{
					UpperKeyMap param = new UpperKeyMap();
					param.put(BOInfo.SEQUENCE, boList.get(boList.size() - 1).getSequence() + 1);
					param.put(BOInfo.PARENTBOGUID, boList.get(0).getParentBOGuid());
					param.put(BOInfo.BMGUID, boList.get(0).getBMGuid());
					BOInfo aboveBMBOInfo = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(param)).get(0);
					aboveBMBOInfo.setSequence(boList.get(0).getSequence());
					aboveBMBOInfo.setUpdateUserGuid(currentUserGuid);
					sds.save(aboveBMBOInfo);

					for (BOInfo boInfo : boList)
					{
						boInfo.setSequence(boInfo.getSequence() + 1);
						boInfo.setUpdateUserGuid(currentUserGuid);
						sds.save(boInfo);
						result.add(boInfo);
					}
				}
			}

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

		return result;
	}

}
