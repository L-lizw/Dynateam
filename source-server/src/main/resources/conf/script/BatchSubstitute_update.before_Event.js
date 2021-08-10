print("对批量取替代关系保存或者新建时，检查生效日期是否合法\n");
try {
	var brm = provider.getServiceInstance(Packages.dyna.net.service.brs.BRM.class, sid);
	if(inputObject!=null){
		print("start....\n");
		brm.getReplaceObjectStub().checkBathReplaceBeforeSave(inputObject);
		print("end....\n");
	}
} catch (oException) {
	if (oException.javaException instanceof Packages.dyna.common.exception.ServiceRequestException) {
		return oException.javaException;
	}
}