var boas= provider.getServiceInstance(Packages.dyna.net.service.brs.BOAS.class, sid);
var wfe= provider.getServiceInstance(Packages.dyna.net.service.brs.WFE.class, sid);
var emm= provider.getServiceInstance(Packages.dyna.net.service.brs.EMM.class, sid);
var ppms= provider.getServiceInstance(Packages.dyna.net.service.brs.PPMS.class, sid);
var attachs =  wfe.listProcAttach(inputObject.getProcessGuid());

if(attachs != null && attachs.size()>0)
{
	  try
	  {
			  var projectObjectGuid = null;
			  var requestObjectGuid = null;
			  for(var i = 0 ; i < attachs.size(); i++ )
			  {
			  	var classInfo = emm.getClassByGuid(attachs.get(i).getInstanceClassGuid());

           			if(classInfo.hasInterface(Packages.dyna.common.systemenum.ModelInterfaceEnum.IPMChangeRequest))
				{
					requestObjectGuid = new Packages.dyna.common.bean.data.ObjectGuid(attachs.get(i).getInstanceClassGuid(),classInfo.getName(),attachs.get(i).getInstanceGuid(),null);
				}else
				{
					projectObjectGuid = new Packages.dyna.common.bean.data.ObjectGuid(attachs.get(i).getInstanceClassGuid(),classInfo.getName(),attachs.get(i).getInstanceGuid(),null);
					var list = ppms.listChangeRequest(projectObjectGuid);
			
					if (list != null)
					{
						for (var x=0;x<list.size();x++)
						{
							var obj = list.get(x);
							if (obj.getStatus() != Packages.dyna.common.systemenum.SystemStatusEnum.RELEASE)
							{
								requestObjectGuid= obj.getObjectGuid();
								print(obj);
								print(requestObjectGuid);

								break;
							}
						}
					}

				}
			  }
		
			  if(requestObjectGuid != null)
			  {
			   	var request =  boas.getFoundationStub().getObject(requestObjectGuid,false);
			  	ppms.getPMChangeStub().completeChangeRequest(request,true);
			  }
			  else if(projectObjectGuid != null)
			  {	
				var taskList = ppms.getWBSStub().listAllSubTask(projectObjectGuid,false);
				if(taskList != null && taskList.size()>0)
				{
					for(var j = 0 ; j < taskList.size(); j++ )
					{
						if (taskList.get(j).getStatus() != Packages.dyna.common.systemenum.SystemStatusEnum.RELEASE)
						{
							boas.getFSaverStub().changeStatus(taskList.get(j).getObjectGuid(),
													Packages.dyna.common.systemenum.SystemStatusEnum.WIP, Packages.dyna.common.systemenum.SystemStatusEnum.RELEASE, false, false);
						}
					}
				}
				
			} 
			  
		 } catch (oException)
		 {
			   //if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
			   {
			  	return oException.javaException;
			   }
		 }	
  
}