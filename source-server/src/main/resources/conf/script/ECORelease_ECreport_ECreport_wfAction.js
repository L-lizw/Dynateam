var boas = provider.getServiceInstance(Packages.dyna.net.service.brs.BOAS.class, sid);// 调用BOAS服务
var emm = provider.getServiceInstance(Packages.dyna.net.service.brs.EMM.class, sid);// 调用EMM服务
var wfe = provider.getServiceInstance(Packages.dyna.net.service.brs.WFE.class, sid);
var srs = provider.getServiceInstance(Packages.dyna.net.service.brs.SRS.class, sid);
var processGuid = inputObject.getProcessGuid();

if (processGuid != null || processGuid != "") {
	print("start...\n");
	var attachlist = wfe.listProcAttach(processGuid);
	if (attachlist != null && attachlist.size() > 0) {
		var length = attachlist.size();
		// 符合条件的ECO,ECN
		var list = new Packages.java.util.ArrayList();

		for ( var i = 0; i < length; i++) {
			var attach = attachlist.get(i);
			if (attach != null) {
				var objectGuid = new Packages.dyna.common.bean.data.ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);
				var classInfo = emm.getClassByGuid(attach.getInstanceClassGuid());// 通过classGuid获取这个类的实例
				var object = boas.getObject(objectGuid);
				if (classInfo != null && (classInfo.hasInterface(Packages.dyna.common.systemenum.ModelInterfaceEnum.IUpdatedECN) || classInfo.hasInterface(Packages.dyna.common.systemenum.ModelInterfaceEnum.IECOM))) {
					list.add(object.getObjectGuid());
				}
			}
		}
		if (list.size() > 0) {
			srs.reportGeneric(null, Packages.dyna.common.systemenum.ReportTypeEnum.EXCEL, list, null, true);
		}
	}
	print("finished...\n");
}