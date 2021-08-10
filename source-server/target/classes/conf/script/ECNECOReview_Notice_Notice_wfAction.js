var uesc= provider.getServiceInstance(Packages.dyna.net.service.brs.UECS.class, sid);
var processGuid = inputObject.getProcessGuid();
if(processGuid!=null || processGuid!="")
{
         print("start...\n");
         uesc.getUECSStub().sendMailtoPerformerByScript(processGuid);
         print("finished...\n");
}