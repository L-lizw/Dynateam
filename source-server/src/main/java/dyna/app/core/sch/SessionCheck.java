/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SessionCheck 会话检查管理
 * Wanglei 2011-4-20
 */
package dyna.app.core.sch;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.lic.LICImpl;
import dyna.common.dto.SaLicenseOccupied;
import dyna.common.dto.Session;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.LIC;

import java.util.*;

/**
 * 会话检查管理
 * 
 * @author Wanglei
 * 
 */
public class SessionCheck extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;
	private int				timeout			= 0;
	private final Calendar	calendar		= Calendar.getInstance();

	/**
	 * Constructor
	 * 
	 * @param timeout
	 *            超时时间(分钟)
	 */
	public SessionCheck(ServiceContext serviceContext, int timeout)
	{
		this.serviceContext = serviceContext;
		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		LICImpl lic = null;
		try
		{
			lic = (LICImpl) this.serviceContext.allocatService(LIC.class);
			long[] ltime = lic.getLicensePeriod();
			long curtime = System.currentTimeMillis();
			boolean invalid = true;
			if (ltime != null)
			{
				invalid = (curtime < ltime[0] || curtime > ltime[1]);
			}
			int standardCount = 0;
			int webCount = 0;
			List<Session> listSession = lic.listSession();
			Map<String, Session> sessionGuidMap = new HashMap<String, Session>();
			if (!SetUtils.isNullList(listSession))
			{
				Date now = new Date();
				Set<String> sessionSet = new HashSet<String>();
				for (Session session : listSession)
				{
					sessionGuidMap.put(session.getGuid(), session);
					if (ApplicationTypeEnum.INTERNAL.equals(session.getAppType())
							|| ApplicationTypeEnum.MONITOR.equals(session.getAppType()))
					{
						String key = "";
						if (ApplicationTypeEnum.WEB.equals(session.getAppType()) || ApplicationTypeEnum.WEBWX.equals(session.getAppType()))
						{
							key = session.getUserId() + "@" + session.getHostName() + ".WEB";
							if (!sessionSet.contains(key))
							{
								sessionSet.add(key);
								webCount++;
							}
						}
						else
						{
							key = session.getUserId() + "@" + session.getHostName() + ".BASE";
							if (!sessionSet.contains(key))
							{
								sessionSet.add(key);
								standardCount++;
							}
						}
					}
					if (ApplicationTypeEnum.OM.equals(session.getAppType()) || ApplicationTypeEnum.CLS.equals(session.getAppType())
							|| ApplicationTypeEnum.INTERNAL.equals(session.getAppType())
							|| ApplicationTypeEnum.MONITOR.equals(session.getAppType()))
					{
						this.calendar.setTime(session.getUpdateTime());
						this.calendar.add(Calendar.MINUTE, 720);

						if (now.after(this.calendar.getTime()))
						{
							lic.getSessionStub().deleteSessionInside(session.getGuid());
						}
						continue;
					}
					if (StringUtils.isNullString(session.getClientType()))
					{
						continue;
					}

					if (invalid == false)
					{
						this.calendar.setTime(session.getUpdateTime());
						this.calendar.add(Calendar.MINUTE, this.timeout);
						if (now.before(this.calendar.getTime()))
						{
							if (session.getLastAccesseTime() == null)
							{
								continue;
							}
							else
							{
								this.calendar.setTime(session.getLastAccesseTime());
								this.calendar.add(Calendar.MINUTE, 15);

								if (ApplicationTypeEnum.STANDARD == session.getAppType() || ApplicationTypeEnum.CS == session.getAppType())
								{
									if (now.before(this.calendar.getTime()))
									{
										continue;
									}
								}
								else
								{
									continue;
								}
							}
						}
					}

					// expired
					DynaLogger.info("[Checker] session expired: " + session.getGuid());
					try
					{
						lic.getSessionStub().deleteSessionInside(session.getGuid());
					}
					catch (Exception e)
					{
						DynaLogger.error(this.getClass().getSimpleName(), e);
					}
				}
			}
			updateSessionRecord(standardCount, webCount);
			// 删除系统中缓存的，数据库中已经不存在的session
			List<Session> allSession = DataServer.getSystemDataService().listFromCache(Session.class, null);
			if (!SetUtils.isNullList(allSession))
			{
				for (Session session : allSession)
				{
					if (StringUtils.isNullString(session.getClientType()))
					{
						continue;
					}

					if (!sessionGuidMap.containsKey(session.getGuid()))
					{
						try
						{
							lic.getSessionStub().deleteSessionInside(session.getGuid());
						}
						catch (Exception e)
						{
							DynaLogger.error(this.getClass().getSimpleName(), e);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			DynaLogger.error(this.getClass().getSimpleName(), e);
		}
		finally
		{
			if (lic != null)
			{
				this.serviceContext.releaseService(lic);
			}
		}
	}

	public void updateSessionRecord(int standardCount, int webCount)
	{
		try
		{
			String transactionId = StringUtils.generateRandomUID(32);
//			DataServer.getTransactionManager().startTransaction(transactionId);
			Date date = new Date();
			SystemDataService systemDataService = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SaLicenseOccupied.TIME, DateFormat.formatYMD(date));
			int hours = DateFormat.getHours(date);
			List<SaLicenseOccupied> list = systemDataService.query(SaLicenseOccupied.class, filter);
			SaLicenseOccupied webSessionRecord = null;
			SaLicenseOccupied standardSessionRecord = null;
			if (list != null)
			{
				for (SaLicenseOccupied temp : list)
				{
					if (ApplicationTypeEnum.STANDARD.name().equalsIgnoreCase(temp.getType()))
					{
						standardSessionRecord = temp;
					}
					if (ApplicationTypeEnum.WEB.name().equalsIgnoreCase(temp.getType()))
					{
						webSessionRecord = temp;
					}
				}
			}
			if (standardSessionRecord == null)
			{
				standardSessionRecord = new SaLicenseOccupied();
				standardSessionRecord.setTime((String) filter.get(SaLicenseOccupied.TIME));
				standardSessionRecord.setType(ApplicationTypeEnum.STANDARD.name());
				standardSessionRecord.setValue(hours, standardCount);
				systemDataService.save(standardSessionRecord);
			}
			else
			{
				standardSessionRecord.setValue(hours, standardCount);
				if (standardSessionRecord.isChanged())
				{
					systemDataService.save(standardSessionRecord);
				}
			}
			if (webSessionRecord == null)
			{
				webSessionRecord = new SaLicenseOccupied();
				webSessionRecord.setTime((String) filter.get(SaLicenseOccupied.TIME));
				webSessionRecord.setType(ApplicationTypeEnum.WEB.name());
				webSessionRecord.setValue(hours, standardCount);
				systemDataService.save(webSessionRecord);
			}
			else
			{
				webSessionRecord.setValue(hours, standardCount);
				if (webSessionRecord.isChanged())
				{
					systemDataService.save(webSessionRecord);
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			DynaLogger.error(e.getMessage(), e);
		}

	}

}
