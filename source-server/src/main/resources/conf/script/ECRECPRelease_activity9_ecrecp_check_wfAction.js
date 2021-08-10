try{
	
var uesc= provider.getServiceInstance(Packages.dyna.net.service.brs.UECS.class, sid);
var processGuid = inputObject.getProcessGuid();

	
var processGuid = inputObject.getProcessGuid();
	if(processGuid!=null||processGuid!=""){
	print("start...\n");
	uesc.getUECSStub().checkAttachMent(processGuid);
	print("finished...\n");
	}

} catch (oException) {

	   if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
	   {
	  	return oException.javaException;
	   }
	  }