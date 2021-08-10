var boas= provider.getServiceInstance(Packages.dyna.net.service.brs.BOAS.class, sid);
var wfe= provider.getServiceInstance(Packages.dyna.net.service.brs.WFE.class, sid);
var emm= provider.getServiceInstance(Packages.dyna.net.service.brs.EMM.class, sid);

  try{
	  var attachs =  wfe.listProcAttach(inputObject.getProcessGuid);
	  if(!Packages.dyna.common.util.SetUtil.isNullList(attachs))
	  {
		  for(var i = 0 ; i < attachs.length(); i ++ )
		  {
			  var classInfo =   emm.getClassByGuid(attachs[i].getInstanceClassGuid());
			  if(classInfo.hasInterface(Packages.dyna.common.systemenum.ModelInterfaceEnum.IPMProject))
			  {
				  var objectGuid = new Packages.dyna.common.bean.data.ObjectGuid(classInfo.getName(),attachs[i].getInstanceGuid(),null);
				var viewObject =  boas.getRelationByEND1(objectGuid,Packages.dyna.common.systemenum.BuiltinRelationNameEnum.PM_CHANGE.toString());  
				if(viewObject != null)
				{
					var foundationList = boas.listFoundationObjectOfRelation(viewObject.getObjectGuid(),null);
					if(!Packages.dyna.common.util.SetUtil.isNullList(foundationList))
					{
						 for(var i = 0 ; i < attachs.length(); i ++ )
						 {
							if(foundationList.get(i).getStatus()==Packages.dyna.common.systemenum.SystemStatusEnum.WIP ) 
							{
								var newAttach = new Packages.dyna.common.bean.data.system.ProcAttach();
								newAttach.setInstanceGuid(foundationList.get(i).getGuid());
								newAttach.setInstanceClassGuid(foundationList.get(i).getObjectGuid().getGuid());
								
								var newAttachs = new Array(newAttach); 						
								wfe.addAttachment(inputObject.getProcessGuid,newAttachs);
							}
						 }
					}			
				}
				break;
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