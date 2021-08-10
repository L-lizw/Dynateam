try{

var uecs= provider.getServiceInstance(Packages.dyna.net.service.brs.UECS.class, sid);

var processGuid = inputObject.getProcessGuid();
   if(processGuid!=null||processGuid!=""){
   print("start...\n");
   uecs.getUECSStub().unlockFoundation(processGuid);
   print("finished...\n");
   }

} catch (oException) {
      if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) 
      {
     return oException.javaException;
      }
     }