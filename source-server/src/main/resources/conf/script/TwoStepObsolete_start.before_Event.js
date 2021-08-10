var boas = provider.getServiceInstance(Packages.dyna.net.service.brs.BOAS.class, sid);
var wfe = provider.getServiceInstance(Packages.dyna.net.service.brs.WFE.class, sid);
var attachs = wfe.listProcAttach(inputObject.getProcessGuid());
if(attachs != null && attachs.size() > 0)
{ 
	try{
		for (var i = 0 ; i < attachs.size(); i++ )
		{
			var objectGuid = new Packages.dyna.common.bean.data.ObjectGuid(attachs.get(i).getInstanceClassGuid(), null, attachs.get(i).getInstanceGuid(), null);
			var foundation = boas.getFoundationStub().getObject(objectGuid, false);
			boas.canStop(foundation, inputObject.getProcessGuid);
		}
	} catch (oException)
	{
		return oException.javaException;
	}
}