/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailWorkFlowStub
 * wangweixia 2014-7-21
 */
package dyna.app.service.brs.sms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.FilterBuilder;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.Criterion;
import dyna.common.SearchCondition;
import dyna.common.dto.MailWorkFlow;
import dyna.common.dto.aas.User;
import dyna.common.dto.aas.UserAgent;
import dyna.common.dto.template.wft.WorkflowTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.ppms.SearchCategoryEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author wangweixia
 * 
 */
@Component
public class MailWorkFlowStub extends AbstractServiceStub<SMSImpl>
{

	/**
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<MailWorkFlow> listMailOfWorkFlow(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<MailWorkFlow> mailList = null;
		try
		{
			searchCondition.addFilter(MailWorkFlow.MODULE_TYPE, MailMessageType.WORKFLOWAPPROVED.getValue(), OperateSignEnum.EQUALS);
			mailList = this.selectMailOfActrt(searchCondition);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return mailList;
	}

	private String getReceiver(List<Criterion> criterionList)
	{
		String value = null;
		if (!SetUtils.isNullList(criterionList))
		{
			for (Criterion criterion : criterionList)
			{
				Object searchValue = criterion.getValue();
				if (criterion.getKey().equals(MailWorkFlow.RECEIVE_USER))
				{
					value = (String) searchValue;
				}
			}
		}
		return value;
	}

	/**
	 * @param searchCondition
	 * @param object
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, Object> buildFilterBySearchCondition(SearchCondition searchCondition, String orderByTable) throws ServiceRequestException
	{
		Map<String, Object> filter = this.buildFilterJustCondition(searchCondition);

		int pageSize = searchCondition.getPageSize();
		int pageNum = searchCondition.getPageNum();

		filter.put("CURRENTPAGE", pageNum);
		filter.put("ROWSPERPAGE", pageSize);
		Calendar c = Calendar.getInstance();
		filter.put("CURRENTDATE", DateFormat.format(c.getTime(), DateFormat.PTN_YMDHMS));

		// 拼装排序
		// if (!SetUtils.isNullList(searchCondition.getOrderMapList()))
		// {
		// String orderKey = null;
		// Boolean isAsc = false;
		// String orderby = "order by ";
		//
		// String orderByPrefix = "";
		// if (!StringUtils.isNullString(orderByTable))
		// {
		// orderByPrefix = orderByTable + ".";
		// }
		//
		// for (Map<String, Boolean> order : searchCondition.getOrderMapList())
		// {
		// for (Iterator<String> iterator = order.keySet().iterator(); iterator.hasNext();)
		// {
		// orderKey = iterator.next();
		// isAsc = order.get(orderKey);
		// orderby += " " + orderByPrefix + FilterBuilder.trimDollar(orderKey) + " " + (isAsc ? " asc " : " desc ");
		// }
		// }
		//
		// filter.put(Constants.ORDER_BY, orderby);
		// }

		return filter;
	}

	/**
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, Object> buildFilterJustCondition(SearchCondition searchCondition) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();

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
				key = FilterBuilder.trimDollar(criterion.getKey());

				if (key.equals(MailWorkFlow.RECEIVE_TIME))
				{
					if (criterion.getOperateSignEnum().equals(OperateSignEnum.NOTEARLIER))
					{
						filter.put("RECEIVEFROMTIME", value);
					}
					else if (criterion.getOperateSignEnum().equals(OperateSignEnum.NOTLATER))
					{
						filter.put("RECEIVETOTIME", value);
					}
					continue;
				}
				if (key.equals(MailWorkFlow.PLANFINISH_TIME))
				{
					if (criterion.getOperateSignEnum().equals(OperateSignEnum.NOTEARLIER))
					{
						filter.put("PLANFROMTIME", value);
					}
					else if (criterion.getOperateSignEnum().equals(OperateSignEnum.NOTLATER))
					{
						filter.put("PLANTOTIME", value);
					}
					continue;
				}
				if (key.equals("PROCRT_VALID"))
				{
					filter.put("PROCRT_VALID", 'Y');
					continue;
				}
				filter.put(key, value);
			}
		}

		// 我收到的流程;我代理需要审批的流程(如果被代理人自己审批,则代理人无法察看,如果代理人审批,则被代理人和代理人都可以察看);代理过期，代理人审批的流程，代理人可见
		String receiver = this.getReceiver(searchCondition.getCriterionList());
		if (searchCondition.getSearchCategory() == SearchCategoryEnum.WORKFLOW_NOT_AGENT_PROCESS)
		{
			// 我的所有流程，不包含我代理的
			filter.put("RECEIVEUSERLIST", "'" + receiver + "'");
			filter.put("DATATYPE", "1");
		}
		// 我代理的所有流程，只包含代理的
		else if (searchCondition.getSearchCategory() == SearchCategoryEnum.WORKFLOW_AGENT_PROCESS)
		{
			filter.put("DATATYPE", "2");

			UserAgent userAgent = new UserAgent();
			userAgent.setValid(true);
			userAgent.setAgentGuid(receiver);
			List<UserAgent> userAgentList = this.stubService.getAAS().listUserAgent(userAgent);
			StringBuffer userGuidBuffer = new StringBuffer();
			if (!SetUtils.isNullList(userAgentList))
			{
				for (UserAgent userAgent_ : userAgentList)
				{
					if (userGuidBuffer.length() > 0)
					{
						userGuidBuffer.append(",");
					}
					userGuidBuffer.append("'").append(userAgent_.getPrincipalGuid()).append("'");
				}
				filter.put("RECEIVEUSERLIST", userGuidBuffer);
			}
			else
			{
				filter.put("RECEIVEUSERLIST", "'x'");
			}
		}
		else
		{
			filter.put("DATATYPE", "3");
			if (receiver != null)
			{
				// 流程，包含代理流程
				UserAgent userAgent = new UserAgent();
				userAgent.setValid(true);
				userAgent.setAgentGuid(receiver);
				List<UserAgent> userAgentList = this.stubService.getAAS().listUserAgent(userAgent);
				StringBuffer userGuidBuffer = new StringBuffer();
				userGuidBuffer.append("'" + receiver + "'");
				if (!SetUtils.isNullList(userAgentList))
				{
					for (UserAgent userAgent_ : userAgentList)
					{
						userGuidBuffer.append(",");
						userGuidBuffer.append("'").append(userAgent_.getPrincipalGuid()).append("'");
					}
				}
				filter.put("RECEIVEUSERLIST", userGuidBuffer);
			}
		}

		filter.put("ISLATESTONLY", BooleanUtils.getBooleanStringYN(searchCondition.isLatestOnly()));

		return filter;
	}

	private List<MailWorkFlow> selectMailOfActrt(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<MailWorkFlow> resultList = null;
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = this.buildFilterBySearchCondition(searchCondition, null);
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
				if (searchCondition.getSearchCategory() == SearchCategoryEnum.WORKFLOW_NOT_AGENT_PROCESS)
				{
					resultList = sds.query(MailWorkFlow.class, countCondition, "selectMailCountOfActrtDataType1");
				}
				else if (searchCondition.getSearchCategory() == SearchCategoryEnum.WORKFLOW_AGENT_PROCESS)
				{
					resultList = sds.query(MailWorkFlow.class, countCondition, "selectMailCountOfActrtDataType2");
				}
				else
				{
					resultList = sds.query(MailWorkFlow.class, countCondition, "selectMailCountOfActrtDataType3");
				}
				count = SetUtils.isNullList(resultList) ? 0 : resultList.get(0).getRowCount();
			}
		}
		resultList = null;
		if (page > 1 || count > 0)
		{
			if (searchCondition.getSearchCategory() == SearchCategoryEnum.WORKFLOW_NOT_AGENT_PROCESS)
			{
				resultList = sds.query(MailWorkFlow.class, filter, "selectMailOfActrtDataType1");
			}
			else if (searchCondition.getSearchCategory() == SearchCategoryEnum.WORKFLOW_AGENT_PROCESS)
			{
				resultList = sds.query(MailWorkFlow.class, filter, "selectMailOfActrtDataType2");
			}
			else
			{
				resultList = sds.query(MailWorkFlow.class, filter, "selectMailOfActrtDataType3");
			}
		}
		// 日期类型条件

		resultList = dealFilt4DateType(resultList, filter);

		List<MailWorkFlow> mailList = decorateMailWorkFlow(resultList, filter);

		if (!SetUtils.isNullList(mailList))
		{
			for (MailWorkFlow mailWorkFlow : mailList)
			{
				mailWorkFlow.put("AGENTUSERNAME", this.getUserName((String) mailWorkFlow.get("AGENTUSERGUID")));
				mailWorkFlow.put("SENDERUSERNAME", this.getUserName((String) mailWorkFlow.get("SENDERUSER")));
				mailWorkFlow.put("RECEIVEUSERNAME", this.getUserName((String) mailWorkFlow.get("RECEIVEUSER")));
				mailWorkFlow.put("ROWCOUNT$", count);
				String wfTemplateGuid = (String) mailWorkFlow.get("WFTEMPLATEGUID");
				if (StringUtils.isGuid(wfTemplateGuid))
				{
					WorkflowTemplateInfo workflowTemplateInfo = this.stubService.getWFI().getWorkflowTemplateInfo(wfTemplateGuid);
					if (workflowTemplateInfo != null)
					{
						mailWorkFlow.put("PROCRTTITLE", workflowTemplateInfo.getTitle());
					}
				}
			}
			if (!SetUtils.isNullList(searchCondition.getOrderMapList()))
			{

				Map<String, Boolean> order = searchCondition.getOrderMapList().get(0);

				for (Iterator<String> iterator = order.keySet().iterator(); iterator.hasNext();)
				{
					final String orderKey = iterator.next();
					final boolean isAsc = order.get(orderKey) == null ? false : order.get(orderKey);
					Collections.sort(mailList, new Comparator<MailWorkFlow>()
					{

						@Override
						public int compare(MailWorkFlow o1, MailWorkFlow o2)
						{
							// TODO Auto-generated method stub
							if (isAsc)
							{
								return o1.get(orderKey).toString().compareTo(o2.get(orderKey).toString());
							}
							return o2.get(orderKey).toString().compareTo(o1.get(orderKey).toString());
						}
					});
				}
			}
		}
		return mailList;
	}

	private List<MailWorkFlow> dealFilt4DateType(List<MailWorkFlow> tempMailList, Map<String, Object> filter)
	{
		List<MailWorkFlow> resultList = new ArrayList<MailWorkFlow>();
		if (!SetUtils.isNullList(tempMailList))
		{
			Date receiveFromTime = (Date) filter.get("RECEIVEFROMTIME");
			Date receiveToTime = (Date) filter.get("RECEIVETOTIME");
			String processTime = (String) filter.get("PROCESSTIME");
			String planFromTime = (String) filter.get("PLANFROMTIME");
			String planToTime = (String) filter.get("PLANTOTIME");
			if (receiveFromTime == null && receiveToTime == null && processTime == null && planFromTime == null && planToTime == null)
			{
				return tempMailList;
			}
			for (MailWorkFlow mailWorkFlow : tempMailList)
			{
				Date receiveTime = mailWorkFlow.getReceiveTime();
				Date planFinishTime = mailWorkFlow.getPlanFinishTime();
				Date processedTime = mailWorkFlow.getProcessedTime();

				if (receiveFromTime != null)
				{
					if (receiveTime == null)
					{
						continue;
					}
					Date filtReceiveFromTime = receiveFromTime;
					Date tempReceiveFromTime = DateFormat.parse(DateFormat.formatYMD(mailWorkFlow.getReceiveTime()));
					if (tempReceiveFromTime.compareTo(filtReceiveFromTime) < 0)
					{
						continue;
					}
				}
				if (receiveToTime != null)
				{
					if (receiveTime == null)
					{
						continue;
					}
					Date filtReceiveToTime = receiveToTime;
					Date tempReceiveFromTime = DateFormat.parse(DateFormat.formatYMD(mailWorkFlow.getReceiveTime()));
					if (tempReceiveFromTime.compareTo(filtReceiveToTime) > 0)
					{
						continue;
					}
				}
				if (processTime != null)
				{
					if (processedTime == null)
					{
						continue;
					}
					Date filtProcessTime = DateFormat.parse(processTime);
					Date tempProcessTime = DateFormat.parse(DateFormat.formatYMD(mailWorkFlow.getProcessedTime()));
					if (filtProcessTime.compareTo(tempProcessTime) != 0)
					{
						continue;
					}
				}
				if (planFromTime != null)
				{
					if (planFinishTime == null)
					{
						continue;
					}
					Date filtPlanFromTime = DateFormat.parse(planFromTime);
					Date tempPlanFinishTime = DateFormat.parse(DateFormat.formatYMD(mailWorkFlow.getPlanFinishTime()));
					if (tempPlanFinishTime.compareTo(filtPlanFromTime) < 0)
					{
						continue;
					}
				}
				if (planToTime != null)
				{
					if (planFinishTime == null)
					{
						continue;
					}
					Date filtPlanToTime = DateFormat.parse(planToTime);
					Date tempPlanFinishTime = DateFormat.parse(DateFormat.formatYMD(mailWorkFlow.getPlanFinishTime()));
					if (tempPlanFinishTime.compareTo(filtPlanToTime) > 0)
					{
						continue;
					}
				}
				resultList.add(mailWorkFlow);
			}

		}

		return resultList;
	}

	private List<MailWorkFlow> decorateMailWorkFlow(List<MailWorkFlow> tempmailList, Map<String, Object> filter)
	{
		List<MailWorkFlow> mailList = null;
		Map<String, List<MailWorkFlow>> sameactrtindex = new HashMap<String, List<MailWorkFlow>>();
		Map<String, List<MailWorkFlow>> sameprocrtindex = new HashMap<String, List<MailWorkFlow>>();
		if (!SetUtils.isNullList(tempmailList))
		{
			for (MailWorkFlow mailWorkFlow : tempmailList)
			{
				String actrtGuid = mailWorkFlow.getActRuntimeGuid();
				int startNumber = mailWorkFlow.getStartNumber();
				if (sameactrtindex.get(actrtGuid + startNumber) == null)
				{
					List<MailWorkFlow> mailListWorkFlowList = new ArrayList<MailWorkFlow>();
					mailListWorkFlowList.add(mailWorkFlow);
					sameactrtindex.put(actrtGuid + startNumber, mailListWorkFlowList);
				}
				else
				{
					List<MailWorkFlow> mailListWorkFlowList = sameactrtindex.get(actrtGuid + startNumber);
					mailListWorkFlowList.add(mailWorkFlow);
					sameactrtindex.put(actrtGuid + startNumber, mailListWorkFlowList);
				}

				if (filter.get("ONLYSEND") != null && filter.get("ONLYSEND").equals("Y"))
				{
					String procrtGuid = mailWorkFlow.getProcessRuntimeGuid();
					if (sameprocrtindex.get(procrtGuid) == null)
					{
						List<MailWorkFlow> mailListWorkFlowList = new ArrayList<MailWorkFlow>();
						mailListWorkFlowList.add(mailWorkFlow);
						sameprocrtindex.put(actrtGuid + startNumber, mailListWorkFlowList);
					}
					else
					{
						List<MailWorkFlow> mailListWorkFlowList = sameprocrtindex.get(actrtGuid + startNumber);
						mailListWorkFlowList.add(mailWorkFlow);
						sameprocrtindex.put(actrtGuid + startNumber, mailListWorkFlowList);
					}
				}

			}

			List<MailWorkFlow> temp_sameActrtList = new ArrayList<MailWorkFlow>();
			if (!SetUtils.isNullMap(sameactrtindex))
			{
				for (Map.Entry<String, List<MailWorkFlow>> entry : sameactrtindex.entrySet())
				{
					List<MailWorkFlow> mailWorkFlowList = entry.getValue();
					Collections.sort(mailWorkFlowList, new Comparator<MailWorkFlow>()
					{

						@Override
						public int compare(MailWorkFlow o1, MailWorkFlow o2)
						{
							// TODO Auto-generated method stub
							String o1ActrtGuid = o1.getActRuntimeGuid();
							String o2ActrtGuid = o2.getActRuntimeGuid();
							return o1ActrtGuid.compareTo(o2ActrtGuid);
						}
					});

					temp_sameActrtList.add(mailWorkFlowList.get(0));
				}
			}
			List<MailWorkFlow> temp_ActrtList = new ArrayList<MailWorkFlow>();
			if (!SetUtils.isNullMap(sameprocrtindex))
			{
				for (Map.Entry<String, List<MailWorkFlow>> entry : sameprocrtindex.entrySet())
				{
					List<MailWorkFlow> mailWorkFlowList = entry.getValue();
					Collections.sort(mailWorkFlowList, new Comparator<MailWorkFlow>()
					{

						@Override
						public int compare(MailWorkFlow o1, MailWorkFlow o2)
						{
							// TODO Auto-generated method stub
							Date o1ReceiveTime = o1.getReceiveTime();
							Date o2ReceiveTime = o2.getReceiveTime();
							return o2ReceiveTime.compareTo(o1ReceiveTime);
						}
					});
					if (temp_sameActrtList.contains(mailWorkFlowList.get(0)))
					{
						temp_ActrtList.add(mailWorkFlowList.get(0));
					}
				}
			}
			else
			{
				temp_ActrtList = temp_sameActrtList;
			}

			mailList = new ArrayList<MailWorkFlow>();
			if (!SetUtils.isNullList(temp_ActrtList))
			{
				Integer rowIndex = new Integer("0");
				for (MailWorkFlow mailWorkFlow : temp_ActrtList)
				{
					mailWorkFlow.put("ROWCOUNT$", new Integer(temp_ActrtList.size()));
					mailWorkFlow.put("ROWINDEX$", rowIndex++);
				}
			}
			if (filter.get("ROWNUM") != null)
			{
				int rownum = Integer.parseInt(filter.get("CURRENTPAGE").toString());
				int pageSize = Integer.parseInt(filter.get("ROWSPERPAGE").toString());
				int fromIndex = (rownum - 1) * pageSize;
				int toIndex = rownum * pageSize;
				if (toIndex > temp_ActrtList.size())
				{
					toIndex = temp_ActrtList.size();
				}
				mailList.addAll(temp_ActrtList.subList(fromIndex, toIndex));
			}
			else
			{
				mailList.addAll(temp_ActrtList);
			}
		}
		return mailList;
	}

	private String getUserName(String userGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(userGuid))
		{
			User user = this.stubService.getAAS().getUser(userGuid);
			return user.getName();
		}
		return null;
	}
}
