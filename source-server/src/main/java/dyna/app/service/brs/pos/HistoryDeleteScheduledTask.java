package dyna.app.service.brs.pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.dto.BIViewHis;
import dyna.common.dto.Preference;
import dyna.common.dto.PreferenceDetail;
import dyna.common.dto.Search;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.PreferenceTypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.POS;

class HistoryDeleteScheduledTask extends AbstractScheduledTask
{
	private static Set<String>	userGuidSet		= new HashSet<>();

	private ServiceContext		serviceContext	= null;

	public HistoryDeleteScheduledTask(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{

		DynaLogger.debug("BIViewHisDeleteScheduledTask Scheduled [Class]DeletetScheduledTask , Scheduled Task Start...");
		List<String> userGuidList = new ArrayList<>();
		synchronized (userGuidSet)
		{
			if (userGuidSet.size() > 0)
			{
				userGuidList.addAll(userGuidSet);
				userGuidSet.clear();
			}
		}

		if (userGuidList.size() > 0)
		{
			for (String userGuid : userGuidList)
			{
				deleteBIViewHis(userGuid);
				deleteSearchHis(userGuid);
			}
			userGuidList.clear();
		}

		DynaLogger.debug("BIViewHisDeleteScheduledTask Scheduled [Class]DeletetScheduledTask , Scheduled Task End...");
	}

	private void deleteBIViewHis(String userGuid) {
		POS pos = null;
		try
		{
			pos = serviceContext.allocatService(POS.class);
			Preference preference = ((POSImpl) pos).getPreferenceStub().getPreference(PreferenceTypeEnum.BIVIEWHISCOUNT, userGuid);
			int maxRowNum = BIViewHis.MAXROWNUM;
			if (preference != null)
			{
				List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
				if (!SetUtils.isNullList(preferenceDetailList))
				{
					maxRowNum = Integer.parseInt(preferenceDetailList.get(0).getValue());
				}
			}

			SystemDataService sds = DataServer.getSystemDataService();
			BIViewHis his = new BIViewHis();
			his.put(BIViewHis.CREATE_USER, userGuid);
			List<BIViewHis> hisListOfUser = sds.query(BIViewHis.class, his, "selectByUser");
			hisListOfUser.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
			if (hisListOfUser.size() > maxRowNum)
			{
				List<BIViewHis> deleteHisList = hisListOfUser.subList(maxRowNum, hisListOfUser.size());
				if (!SetUtils.isNullList(deleteHisList))
				{
					for (BIViewHis item : deleteHisList)
					{
						BIViewHis deleteHis = new BIViewHis();
						deleteHis.setInstanceGuid(item.getInstanceGuid());
						deleteHis.put(BIViewHis.CREATE_USER, item.get(BIViewHis.CREATE_USER));
						sds.delete(BIViewHis.class, deleteHis, "deleteBy");
					}
				}
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error(e.getMessage(), e);
		}
		finally
		{
			if (pos != null)
			{
				serviceContext.releaseService(pos);
			}
			SecurityContextHolder.clearContext();
		}
	}

	public static void addWaitProcessUser(String userGuid)
	{
		synchronized (userGuidSet)
		{
			userGuidSet.add(userGuid);
		}
	}
	
	public void deleteSearchHis(String userGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		int maxHistoryNum = 0;

		// 如果用户做了"最近打开"的数量的配置，那么以用户配置的为准
		// 如果用户没做配置，那么取系统默认的最大条数
		
		POS pos = null;
		try
		{
			pos = serviceContext.allocatService(POS.class);
			Preference preference = ((POSImpl) pos).getPreferenceStub().getPreference(PreferenceTypeEnum.MAXHISTORY, userGuid);
			maxHistoryNum =Search.MAX_HISTORY_NUM;
			if (preference != null)
			{
				List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
				if (!SetUtils.isNullList(preferenceDetailList))
				{
					maxHistoryNum = Integer.parseInt(preferenceDetailList.get(0).getValue());
				}
			}
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Search.OWNER_USER, userGuid);
			List<Search> searchList = sds.query(Search.class, filter);
			if (!SetUtils.isNullList(searchList))
			{
				for (int i = searchList.size() - 1; i > maxHistoryNum - 1; i--)
				{
					sds.delete(Search.class, searchList.get(i).getGuid());
				}
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error(e.getMessage(), e);
		}
		finally
		{
			if (pos != null)
			{
				serviceContext.releaseService(pos);
			}
			SecurityContextHolder.clearContext();
		}
	}
}