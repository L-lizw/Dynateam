/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailInboxStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.sms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.FilterBuilder;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.Criterion;
import dyna.common.SearchCondition;
import dyna.common.dto.Mail;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author Wanglei
 * 
 */
public class MailInboxStub extends AbstractServiceStub<SMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected MailInboxStub(ServiceContext context, SMSImpl service)
	{
		super(context, service);
	}

	/**
	 * 
	 * @param searchCondition
	 * @param recieveUserGuid
	 *            接收人
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<Mail> listInbox(SearchCondition searchCondition, String recieveUserGuid) throws ServiceRequestException
	{
		List<Mail> mailList = null;
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = this.buildFilterBySearchCondition(searchCondition, null);
			if (StringUtils.isNullString(recieveUserGuid))
			{
				String operatorGuid = this.stubService.getOperatorGuid();
				filter.put(Mail.USER_GUID, operatorGuid);
			}
			else
			{
				filter.put(Mail.USER_GUID, recieveUserGuid);
			}
			if (filter.get(Mail.MODULE_TYPE) == null)
			{
				filter.put(Mail.MODULE_TYPE, this.setMailTypeCondition());
			}
			int page = 999;
			int count = 0;
			if (filter.get("CURRENTPAGE") != null)
			{
				page = ((Number) filter.get("CURRENTPAGE")).intValue();
				if (page < 2)
				{
					Map<String, Object> countCondition = new HashMap<>(filter);
					countCondition.remove("ROWSPERPAGE");
					countCondition.remove("CURRENTPAGE");
					mailList = sds.query(Mail.class, countCondition, "getInboxCount");
					count = SetUtils.isNullList(mailList) ? 0 : mailList.get(0).getRowCount();
				}
			}
			if (page > 1 || count > 0)
			{
				mailList = sds.query(Mail.class, filter, "getInbox");
			}
			else
			{
				mailList = null;
			}

			if (!SetUtils.isNullList(mailList))
			{
				Map<String, Object> filter_ = new HashMap<String, Object>();
				for (Mail mail : mailList)
				{
					filter_.clear();
					filter_.put("MASTERGUID", mail.getRUMasterGuid());
					mail.put("ROWCOUNT$", count);
					List<Mail> tmpList = sds.query(Mail.class, filter_, "selectReceiverByMasterGuid");
					if (!SetUtils.isNullList(tmpList))
					{
						StringBuffer receiverUserNameBuffer = new StringBuffer();
						for (Mail mail_ : tmpList)
						{
							if (receiverUserNameBuffer.length() > 0)
							{
								receiverUserNameBuffer.append(";");
							}
							User user = sds.get(User.class, mail_.getReceiveUser());
							if (user != null)
							{
								receiverUserNameBuffer.append(user.getUserName());
							}
						}
						mail.setReceiveUserName(receiverUserNameBuffer.toString());
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return mailList;
	}

	/**
	 * 设置邮件类型的查询条件
	 * 
	 * @return
	 */
	public String setMailTypeCondition()
	{
		StringBuffer value = new StringBuffer();
		List<MailMessageType> list = MailMessageType.listMailMessageType();
		for (int i = 0; i < list.size(); i++)
		{
			MailMessageType type = list.get(i);
			value.append("'");
			value.append(type.getValue());
			value.append("'");
			if (i != list.size() - 1)
			{
				value.append(",");
			}

		}
		return value.toString();
	}

	/**
	 * 拼装searchCondition的检索条件,分页及排序 到Map中
	 * 
	 * @param searchCondition
	 * @param orderByTable
	 *            排序字段所属的数据表
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<String, Object> buildFilterBySearchCondition(SearchCondition searchCondition, String orderByTable) throws ServiceRequestException
	{
		Map<String, Object> filter = this.buildFilterJustCondition(searchCondition);

		// pagination
		// boolean pageForward = searchCondition.getPageForward();
		// int rowIdx = searchCondition.getPageStartRowIdx();
		int pageSize = searchCondition.getPageSize();
		int pageNum = searchCondition.getPageNum();
		// if (!pageForward)
		// {
		// rowIdx -= pageSize + 1;
		// }

		filter.put("CURRENTPAGE", pageNum);
		filter.put("ROWSPERPAGE", pageSize);

		// 拼装排序
		if (!SetUtils.isNullList(searchCondition.getOrderMapList()))
		{
			String orderKey = null;
			Boolean isAsc = false;
			String orderby = "order by ";

			String orderByPrefix = "";
			if (!StringUtils.isNullString(orderByTable))
			{
				orderByPrefix = orderByTable + ".";
			}

			for (Map<String, Boolean> order : searchCondition.getOrderMapList())
			{
				for (Iterator<String> iterator = order.keySet().iterator(); iterator.hasNext();)
				{
					orderKey = iterator.next();
					isAsc = order.get(orderKey);
					orderby += " " + orderByPrefix + FilterBuilder.trimDollar(orderKey) + " " + (isAsc ? " asc " : " desc ");
				}
			}

			filter.put(Constants.ORDER_BY, orderby);
		}

		return filter;
	}

	/**
	 * 拼装searchCondition的检索条件到Map中
	 * 
	 * @param searchCondition
	 * @return filter
	 * @throws ServiceRequestException
	 */
	public Map<String, Object> buildFilterJustCondition(SearchCondition searchCondition) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();

		searchCondition = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().dealWithSearchCondition(searchCondition);
		// 拼装检索条件
		if (!SetUtils.isNullList(searchCondition.getCriterionList()))
		{
			String key = null;
			Object value = null;
			for (Criterion criterion : searchCondition.getCriterionList())
			{
				value = criterion.getValue();
				if (value instanceof String)
				{
					value = StringUtils.convertNULL((String) value);
				}
				else if (value instanceof Integer)
				{
					value = String.valueOf(((Integer) value).intValue());
				}
				key = FilterBuilder.trimDollar(criterion.getKey());

				if (key.equals(Mail.CREATE_TIME))
				{
					if (criterion.getOperateSignEnum().equals(OperateSignEnum.NOTEARLIER))
					{
						filter.put("FROMTIME", value);
					}
					else if (criterion.getOperateSignEnum().equals(OperateSignEnum.NOTLATER))
					{
						filter.put("TOTIME", value);
					}
					else if (criterion.getOperateSignEnum().equals(OperateSignEnum.EQUALS))
					{
						Calendar today = Calendar.getInstance();
						today.setTime((Date) value);
						filter.put("FROMTIME", this.getDayBeginTime(today));
						filter.put("TOTIME", this.getDayEndTime(today));
					}

					continue;
				}
				filter.put(key, value);
			}
		}

		// if (searchCondition.isIncludeOBS()/* .isOBSOnly() */)
		// {
		// filter.put("STATUS", "OBS");
		// }

		filter.put("ISLATESTONLY", BooleanUtils.getBooleanStringYN(searchCondition.isLatestOnly()));

		return filter;
	}

	private Date getDayBeginTime(Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private Date getDayEndTime(Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	protected int getCountOfUnreadNotice() throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		int count = 0;
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			String operatorGuid = this.stubService.getOperatorGuid();
			filter.put(Mail.RECEIVE_USER, operatorGuid);

			List<Mail> mailList = sds.query(Mail.class, filter, "getCountForNotRead");
			Mail mail = mailList.get(0);
			count = ((Number) mail.get(Mail.NOT_READ_COUNT)).intValue();
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return count;
	}

}
