package dyna.data.service.tool;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.ins.InstanceServiceImpl;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class DSToolStub extends DSAbstractServiceStub<DSToolServiceImpl>
{
	@Autowired
	private FoundationObjectMapper              foundationObjectMapper;

	protected DSToolStub(DataServerContext context, DSToolServiceImpl service)
	{
		super(context, service);
	}

	protected void clearErrDataInMast(String className) throws ServiceRequestException
	{
		String baseTableName = DataServer.getDSCommonService().getRealBaseTableName(className);

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> param = new HashMap<>();
		param.put("BASETABLENAME", baseTableName);
		sds.update(FoundationObject.class, param, "clearErrDataInMast");
	}

	/**
	 * 个案准备的状态修改方法
	 *
	 * @param parameterMap
	 * @throws DynaDataException
	 */
	protected void changeFoundationStatus(Map<String, Object> parameterMap)
	{
		try
		{
			parameterMap.put("CURRENTTIME", new Date());
			parameterMap.put("TABLENAME", DataServer.getDSCommonService().getTableName((String) parameterMap.get("CLASSGUID")));
			this.foundationObjectMapper.updateStatusByCustomize(parameterMap);

			((InstanceServiceImpl) DataServer.getInstanceService()).getFoundationStub().calculateInstanceLatesttestVal((String) parameterMap.get("CLASSGUID"),
					(String) parameterMap.get("MASTERFK"));
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("change status error:" + e.getMessage(), null, DataExceptionEnum.DS_CHANGE_FO_STATUS);
		}
	}

	protected void refreshMergeFieldValue(FoundationObject foundationObject, String fieldName) throws ServiceRequestException
	{
		String whereSql;
		ClassObject classObject = DataServer.getClassModelService().getClassObject(foundationObject.getObjectGuid().getClassName());
		ClassField classField = classObject.getField(fieldName);
		String columnname = classField.getColumnName();
		String tableName = DataServer.getDSCommonService().getTableName(classObject.getName(), fieldName);
		if (tableName.endsWith("_0"))
		{
			whereSql = "guid = '" + foundationObject.getObjectGuid().getGuid() + "'";
		}
		else
		{
			whereSql = "foundationfk = '" + foundationObject.getObjectGuid().getGuid() + "'";
		}

		Map<String, Object> param = new HashMap<>();
		param.put("table", tableName);
		param.put("updatesql", columnname + "='" + this.escapeStr((String) foundationObject.get(fieldName)) + "'");
		param.put("wheresql", whereSql);

		SystemDataService sds = DataServer.getSystemDataService();
		sds.update(FoundationObject.class, param, "updateShort");

		boolean isView = (foundationObject instanceof ViewObject) || (foundationObject instanceof BOMView);
		String exceptionParameter = foundationObject.getId();
		if (isView)
		{
			exceptionParameter = StringUtils.convertNULLtoString(foundationObject.getName());
		}
		((InstanceServiceImpl) DataServer.getInstanceService()).getFoundationStub().updateValueOfMaster(foundationObject.getObjectGuid(), exceptionParameter,
				foundationObject.getUnique());
	}

	/**
	 * 转义Oracle特殊字符
	 *
	 * @param str
	 * @return
	 */
	private String escapeStr(String str)
	{
		if (!StringUtils.isNullString(str))
		{
			str = str.replace("'", "''");
			str = str.replace("&", "' || chr(38) || '");
		}

		return str == null ? "" : str;
	}
}
