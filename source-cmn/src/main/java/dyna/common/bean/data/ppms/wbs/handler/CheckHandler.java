package dyna.common.bean.data.ppms.wbs.handler;

import java.util.Stack;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.PMAuthorityEnum;

public interface CheckHandler extends Handler
{
	public void checkDependLoop(ObjectGuid task, ObjectGuid preTask, Stack<String> taskStack, boolean isParent) throws ServiceRequestException;

	public void checkSummayDepend(FoundationObject task) throws ServiceRequestException;

	public boolean isEditable(FoundationObject task) throws ServiceRequestException;

	public boolean isEditable(FoundationObject task, String name) throws ServiceRequestException;

	public boolean isValid(FoundationObject task, boolean containCOP);

	public boolean isSubProject(FoundationObject task);

	public void hasPMAuthority(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, String operatorGuid) throws ServiceRequestException;

	public boolean hasPMAuthorityNoException(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, String operatorGuid);
}
