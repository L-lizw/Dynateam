package dyna.common.bean.data.ppms.wbs.handler;

import java.util.Date;
import java.util.Map;
import java.util.Stack;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.exception.ServiceRequestException;

public interface CalculateHandler extends Handler
{
	public void updateWBScode(FoundationObject foundation) throws ServiceRequestException;

	public void updateTaskPreInfo(FoundationObject foundation);

	public void updateTaskPreInfo() throws ServiceRequestException;

	public void calculatePlanStartTime(FoundationObject foundation, Stack<String> stack) throws ServiceRequestException;

	public void calculateLasterTime(FoundationObject foundation, Stack<String> stack) throws ServiceRequestException;

	public void calculateProjectInfo(FoundationObject project, FoundationObject task, Date now, Map<String, Integer> map) throws ServiceRequestException;

	public void calculateMilestone() throws ServiceRequestException;

	public void change(Date startDate, PMCalendar targetPMCalendar, String changeType) throws ServiceRequestException;

	public void updatePlanWorkload();

	public void updateOperateStatus() throws ServiceRequestException;
}
