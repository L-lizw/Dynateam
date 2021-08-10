package dyna.app.service.brs.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.report.DetailColumnInfo;
import dyna.app.report.GenericReportParams;
import dyna.app.report.GenericReportUtil;
import dyna.app.report.ReportDataProvider;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class AdvanceSearchProviderImpl implements ReportDataProvider<FoundationObject>
{

	private Class<FoundationObject>	resultClass			= FoundationObject.class;
	private List<FoundationObject>	allFoundationList	= new ArrayList<FoundationObject>();
	private boolean					flag				= false;
	private GenericReportParams		genericParams		= null;
	private boolean					refreshData			= true;
	private List<FoundationObject>	dataList			= new ArrayList<FoundationObject>();

	public AdvanceSearchProviderImpl(List<FoundationObject> allFoundationList, GenericReportParams params, boolean flag)
	{
		this.allFoundationList = allFoundationList;
		this.flag = flag;
		this.genericParams = params;
	}

	@Override
	public List<FoundationObject> getDataList() throws ServiceRequestException
	{
		if (genericParams == null)
		{
			return new ArrayList<FoundationObject>();
		}
		if (refreshData)
		{
			dataList.clear();

			GenericReportUtil reportUtil = new GenericReportUtil(genericParams);
			for (int i = 0; i < allFoundationList.size(); i++)
			{
				FoundationObjectImpl fo = (FoundationObjectImpl) allFoundationList.get(i);
				ObjectGuid objectGuid = fo.getObjectGuid();
				if (!objectGuid.hasAuth())
				{
					continue;
				}

				List<FoundationObject> clfList = fo.restoreAllClassification(false);
				if (clfList == null)
				{
					clfList = new ArrayList<FoundationObject>();
				}

				List<DetailColumnInfo> detailColumnList = this.genericParams.getDetailColumnList();
				if (SetUtils.isNullList(detailColumnList))
				{
					return new ArrayList<FoundationObject>();
				}

				FoundationObjectImpl result = new FoundationObjectImpl();
				for (DetailColumnInfo fieldInfo : detailColumnList)
				{
					String fieldName = fieldInfo.getPropertyName().replace("#", "$");
					if (fieldName.startsWith("CF$"))
					{
						String classificationMasterName = fieldName.substring(3, fieldName.indexOf("$", 3));
						for (FoundationObject clf : clfList)
						{
							String masterName = this.getClassificationMasterName(clf);
							if (classificationMasterName.equalsIgnoreCase(masterName))
							{
								String classificationItemGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATION.getName());
								String realFieldName = fieldName.substring(fieldName.indexOf("$", 3) + 1);
								result.put(fieldName,
										reportUtil.getFieldValue((FoundationObjectImpl) clf, classificationItemGuid, realFieldName, true, fieldInfo.getPropertiesMap()));

								break;
							}
						}
					}
					else
					{
						if (fieldName.equals("BOTITLE$") && !fo.get("BOTITLE$").toString().contains("["))
						{
							result.put(fieldName, "[" + StringUtils.getMsrTitle((String) fo.get("BOTITLE$"), this.genericParams.getLang().getType()) + "]");
						}
						else if (fieldName.equals("STATUS$") && !flag)
						{
							String status = this.genericParams.getMSRM().getMSRString(fo.getStatus().getMsrId(), this.genericParams.getLang().toString());
							String lifecyclephaseTitle = reportUtil.getFieldValue(fo, "LIFECYCLEPHASE$TITLE", fieldInfo.getPropertiesMap());
							result.put(fieldName, lifecyclephaseTitle + "(" + status + ")");
						}
						else if (fieldName.equals("CLASSGUID$"))
						{
							result.put(fieldName, "[" + StringUtils.getMsrTitle((String) fo.get("BOTITLE$"), this.genericParams.getLang().getType()) + "]");
						}
						else
						{
							result.put(fieldName, reportUtil.getFieldValue(fo, fieldName, fieldInfo.getPropertiesMap()));
						}
					}
				}
				dataList.add(result);
			}
		}
		return dataList;
	}

	@Override
	public Map<String, Object> getHeaderParameter()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reportcondition", "");
		return map;
	}

	@Override
	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter()
	{
		return null;
	}

	private String getClassificationMasterName(FoundationObject clf) throws ServiceRequestException
	{
		String classificationGroupName = (String) clf.get("CLASSIFICATIONGROUP$NAME");
		String classificationItemGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATION.getName());

		if (classificationGroupName == null)
		{
			String classificationGroupGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName());
			if (StringUtils.isGuid(classificationGroupGuid))
			{
				CodeObjectInfo codeObjectInfo = this.genericParams.getEMM().getCode(classificationGroupGuid);
				if (codeObjectInfo != null)
				{
					classificationGroupName = codeObjectInfo.getName();
				}
			}
			else
			{
				if (StringUtils.isGuid(classificationItemGuid))
				{
					CodeItemInfo codeItemInfo = this.genericParams.getEMM().getCodeItem(classificationItemGuid);
					if (codeItemInfo != null)
					{
						CodeObjectInfo code = this.genericParams.getEMM().getCode(codeItemInfo.getCodeGuid());
						if (code != null)
						{
							classificationGroupName = code.getName();
						}
					}
				}
			}
		}

		return classificationGroupName;
	}

	@Override
	public GenericReportParams getParams()
	{
		return genericParams;
	}

	public List<FoundationObject> getList()
	{
		return dataList;
	}

	@Override
	public void setDataList(List<FoundationObject> dataList)
	{
		this.dataList = dataList;
		this.refreshData = false;
	}

	@Override
	public Class<FoundationObject> getResultClass()
	{
		return this.resultClass;
	}

}