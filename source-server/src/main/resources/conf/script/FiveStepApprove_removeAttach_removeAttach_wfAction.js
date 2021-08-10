try{
    var wfe= provider.getServiceInstance(Packages.dyna.net.service.brs.WFE,class, sid);
    var procRtGuid = inputObject.getProcessGuid();//流程GUID
    print("procRtGuid:"+procRtGuid+"\n");
    if (procRtGuid != null || procRtGuid != ""){
          wfe.removeECPAttachment(procRtGuid);
    }
} catch (oException) {
	   if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
	   {
	  	return oException.javaException;
	   }
}