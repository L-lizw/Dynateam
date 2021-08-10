package dyna.app.service.brs.pos;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.SaCustomColumnsPreference;
import dyna.common.dto.SaCustomColumnsPreferenceDetail;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;

public class MyCustomColumnsStub extends AbstractServiceStub<POSImpl>
{

	protected MyCustomColumnsStub(ServiceContext context, POSImpl service)
	{
		super(context, service);
	}

	protected List<SaCustomColumnsPreferenceDetail> saveMyCustomColumns(String tableType, List<SaCustomColumnsPreferenceDetail> detailList, String classifFK, String classFK)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			String operatorGuid = this.stubService.getOperatorGuid();
			SaCustomColumnsPreference saCusColPreference = this.getSaCustomColumnsPreference(tableType, classFK, classifFK);
			String masterGuid = null;

			if (saCusColPreference == null)
			{
				saCusColPreference = new SaCustomColumnsPreference();
				saCusColPreference.put(SystemObject.CREATE_USER_GUID, operatorGuid);
				saCusColPreference.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
				saCusColPreference.setUserGuid(operatorGuid);
				saCusColPreference.setTableType(tableType);
				saCusColPreference.setClassFK(classFK);
				saCusColPreference.setClassificationFK(classifFK);

				masterGuid = sds.save(saCusColPreference);
			}
			else
			{
				masterGuid = saCusColPreference.getGuid();
			}

			List<SaCustomColumnsPreferenceDetail> lists = this.listMyCustomColumnsByMasterGuid(masterGuid);
			if (!SetUtils.isNullList(lists) && !SetUtils.isNullList(detailList))
			{
				for (SaCustomColumnsPreferenceDetail detail1 : lists)
				{
					for (SaCustomColumnsPreferenceDetail detail2 : detailList)
					{
						if (detail1.getCustomField().equals(detail2.getCustomField()) && detail2.getColumnLength() == null)
						{
							detail2.setColumnLength(detail1.getColumnLength());
						}
					}

				}
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SaCustomColumnsPreferenceDetail.MASTERFK, masterGuid);
			sds.delete(SaCustomColumnsPreferenceDetail.class, filter, "deleteDetailByMaster");

			if (!SetUtils.isNullList(detailList))
			{
				int i = 0;
				for (SaCustomColumnsPreferenceDetail detail : detailList)
				{
					detail.setMasterfk(masterGuid);
					detail.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					detail.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
					detail.setSequence(i++);
					sds.save(detail);
				}
			}
			List<SaCustomColumnsPreferenceDetail> listMyCustomColumnsByMasterGuid = this.listMyCustomColumnsByMasterGuid(masterGuid);
//			DataServer.getTransactionManager().commitTransaction();
			return listMyCustomColumnsByMasterGuid;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			throw ServiceRequestException.createByException("ID_APP_WF_UNKNOW_ERROR", e);
		}
	}

	private SaCustomColumnsPreference getSaCustomColumnsPreference(String tableValue, String classFK, String classifFK) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();

		String operatorGuid = this.stubService.getOperatorGuid();
		filter.put(SaCustomColumnsPreference.USERGUID, operatorGuid);
		filter.put(SaCustomColumnsPreference.TABLETYPE, tableValue);
		filter.put(SaCustomColumnsPreference.CLASSFK, classFK);
		filter.put(SaCustomColumnsPreference.CLASSIFICATIONFK, classifFK);
		List<SaCustomColumnsPreference> masterList = null;
		try
		{
			masterList = sds.query(SaCustomColumnsPreference.class, filter);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		if (!SetUtils.isNullList(masterList))
		{
			return masterList.get(0);
		}
		else
		{
			return null;
		}
	}

	protected List<SaCustomColumnsPreferenceDetail> listMyCustomColumns(String tableType, String classifFK, String classFK) throws ServiceRequestException
	{
		SaCustomColumnsPreference saCusColPreference = this.getSaCustomColumnsPreference(tableType, classFK, classifFK);
		if (saCusColPreference != null)
		{
			return this.listMyCustomColumnsByMasterGuid(saCusColPreference.getGuid());
		}
		else
		{
			return new ArrayList<SaCustomColumnsPreferenceDetail>();
		}
	}

	private List<SaCustomColumnsPreferenceDetail> listMyCustomColumnsByMasterGuid(String masterGuid) throws ServiceRequestException
	{
		ArrayList<SaCustomColumnsPreferenceDetail> detailLists = new ArrayList<SaCustomColumnsPreferenceDetail>();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			SystemDataService sds = DataServer.getSystemDataService();
			filter.put(SaCustomColumnsPreferenceDetail.MASTERFK, masterGuid);
			List<SaCustomColumnsPreferenceDetail> detailList = sds.query(SaCustomColumnsPreferenceDetail.class, filter);
			detailLists.addAll(detailList);
			Collections.sort(detailLists, new Comparator<SaCustomColumnsPreferenceDetail>()
			{

				@Override
				public int compare(SaCustomColumnsPreferenceDetail sa1, SaCustomColumnsPreferenceDetail sa2)
				{
					int i = sa1.getSequence() - sa2.getSequence();
					return i;
				}
			});
			return detailLists;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	class ComparatorDetail implements Comparator<SaCustomColumnsPreferenceDetail>
	{
		@Override
		public int compare(SaCustomColumnsPreferenceDetail sa1, SaCustomColumnsPreferenceDetail sa2)
		{
			int i = sa1.getSequence() - sa2.getSequence();
			return i;
		}
	}

}
