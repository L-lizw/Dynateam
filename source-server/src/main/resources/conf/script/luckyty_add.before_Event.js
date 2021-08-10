//判断对象是否可以被停用
print("判断对象是否可以被停用");
var wfi = provider.getServiceInstance(Packages.dyna.net.service.brs.WFI.class, sid);
var boas = provider.getServiceInstance(Packages.dyna.net.service.brs.BOAS.class, sid);
var templateInfo = inputObject.getWorkflowTemplateInfo();
var listActivity =wfi.listActivity(templateInfo.getWFName());
var hasObsolete = false;
if(!Packages.dyna.common.util.SetUtils.isNullList(listActivity))
{
	for (var i=0  ; i< listActivity.size() ; i++)
	{
		activity = listActivity.get(i);
		 if (activity.getType() == Packages.dyna.common.systemenum.WorkflowActivityType.APPLICATION)
		 {
			 var appType =  activity.getApplicationType();
			 if (appType ==Packages.dyna.common.systemenum.WorkflowApplicationType.CHANGE_STATUS)
			 {
				var statusChangeList = activity.getStatusChangeList();
				for (var j=0  ; j< statusChangeList.size() ; j++ )
				{
					if("OBS" ==statusChangeList.get(j).getToStatus)
					{
						hasObsolete = true;
						break;
					}
				}
			
			 }
		 }
	 }
}

if (hasObsolete == true)
{
	var foundationObject = boas.getFoundationStub().getObjectByGuid(attachSettings[0].getInstanceGuid(), false);

	if (foundationObject != null)
	{
		boas.getFoundationStub().canStop(foundationObject, false, null);
	}
}