try{

var uesc= provider.getServiceInstance(Packages.dyna.net.service.brs.UECS.class, sid);

	if(inputObject!=null||inputObject!=""){
	 print("start...\n");
	 uesc.getUECRECPStub().deleteECR(inputObject);
	 print("finished...\n");
	}

} catch (oException) {
	   if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
	   {
	  	return oException.javaException;
	   }
	  }