print("替代审批结束后，创建取替代关系\n");
try {
	var brm = provider.getServiceInstance(Packages.dyna.net.service.brs.BRM.class, sid);
	var processGuid = inputObject.getProcessGuid();
	if (processGuid != null && processGuid != "") {
		print("start...\n");
		brm.batchDealReplaceApply(processGuid);
		print("end...\n");
	}

} catch (oException) {
	if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) {
		return oException.javaException;
	}
}