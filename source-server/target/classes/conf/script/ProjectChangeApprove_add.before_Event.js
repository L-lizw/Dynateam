var boas= provider.getServiceInstance(Packages.dyna.net.service.brs.BOAS.class, sid);
var ppms= provider.getServiceInstance(Packages.dyna.net.service.brs.PPMS.class, sid);
var attachs = inputObject.getAttachSettings() ;
if(attachs.length > 0)
{ 
	try{
		
		var objectGuid = new Packages.dyna.common.bean.data.ObjectGuid(attachs[0].getInstanceClassGuid(),null,attachs[0].getInstanceGuid(),null);
		var foundation = boas.getFoundationStub().getObject(objectGuid,false);
		if(!(foundation.getStatus() == Packages.dyna.common.systemenum.SystemStatusEnum.WIP || foundation.getStatus() == Packages.dyna.common.systemenum.SystemStatusEnum.ECP)){
			return new Packages.dyna.common.exception.ServiceRequestException("ID_APP_WF_PROJECT_NOT_WIP", "project not wip or ecp");
		}
		
		var isApproval =  foundation.get(Packages.dyna.common.bean.data.ppms.PPMFoundationObjectUtil.PLANAPPROVAL);
		if(isApproval != "Y")
		{
			return new Packages.dyna.common.exception.ServiceRequestException("ID_APP_WF_PROJECT_NOT_APPROVAL", "project need not approval");	
		}
		var taskList = ppms.getWBSStub().listAllSubTask(objectGuid,false);
		if(taskList != null && taskList.size()>0)
		{
			for(var j = 0 ; j < taskList.size(); j++ )
			{
				var userGuid = taskList.get(j).get("EXECUTOR");
				if ("SUMMARY".equals(taskList.get(j).get("TaskType$name"))==false)
				{
					if (userGuid == null || "".equals(userGuid.trim()))
					{
					    return new Packages.dyna.common.exception.ServiceRequestException("ID_APP_WF_PROJECT_NO_EXECUTOR", "task has not exist executor");	
					}
				}
			}
		}
		
	} catch (oException)
	{
		if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
		{
		return oException.javaException;
		}
	}
}