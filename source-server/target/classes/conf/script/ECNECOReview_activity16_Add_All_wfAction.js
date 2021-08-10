try{

var uesc= provider.getServiceInstance(Packages.dyna.net.service.uecs.UECS.class, sid);

var processGuid = inputObject.getProcessGuid();
	if(processGuid!=null||processGuid!=""){
	print("start...\n");
	uesc.getUECSStub().addAttachMent(processGuid);
	print("finished...\n");
	}

} catch (oException) {
	   if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
	   {
	  	return oException.javaException;
	   }
	  }