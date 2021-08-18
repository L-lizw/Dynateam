/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FUIStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.ClassStub;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.BIViewHis;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.POS;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class FUIStub extends AbstractServiceStub<BOASImpl>
{

	private synchronized POS getPOS() throws ServiceRequestException
	{
		try
		{
			return this.stubService.getRefService(POS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public FoundationObject openObject(ObjectGuid objectGuid, boolean icCheckACL) throws ServiceRequestException
	{
		FoundationObject foundationObject = this.stubService.getFoundationStub().getObject(objectGuid, icCheckACL);

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(foundationObject.getObjectGuid().getClassGuid());
		// 打开取替代对象时，不记录历史
		if (classInfo != null && !classInfo.hasInterface(ModelInterfaceEnum.IReplaceSubstitute))
		{
			BIViewHis biViewHis = new BIViewHis();
			biViewHis.setInstanceGuid(objectGuid.getGuid());
			biViewHis.setInstanceClassGuid(objectGuid.getClassGuid());
			biViewHis.setInstanceBOGuid(objectGuid.getBizObjectGuid());
			biViewHis.put(BIViewHis.CREATE_USER, this.stubService.getOperatorGuid());

			this.getPOS().saveBIViewHis(biViewHis);
		}
		return foundationObject;
	}

	private SearchCondition createSearchCondition4Structure(String strucClassName) throws ServiceRequestException
	{
		SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForStructure(strucClassName);
		List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(strucClassName, UITypeEnum.FORM, true);
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObject : uiObjectList)
			{
				searchCondition.addResultUIObjectName(uiObject.getName());
			}
		}
		return searchCondition;
	}

	protected String getFoundationPreviewInfo(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(objectGuid.getGuid()))
		{
			return "";
		}

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		String className = objectGuid.getClassName();
		EMM emm = this.stubService.getEMM();
		ClassInfo classInfo = null;
		try
		{
			classInfo = emm.getClassByName(className);
		}
		catch (Exception e)
		{
			return "";
		}

		DynaObject object = null;
		if (classInfo.hasInterface(ModelInterfaceEnum.IBOMStructure))
		{
			List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listUIObjectInCurrentBizModel(className, UITypeEnum.FORM, true);
			SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(className, uiObjectList);
			object = this.stubService.getBOMS().getBOM(objectGuid, searchCondition, null);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IStructureObject))
		{
			SearchCondition searchCondition = this.createSearchCondition4Structure(className);
			object = this.stubService.getStructureObject(objectGuid, searchCondition);
		}
		else
		{
			object = this.stubService.getObject(objectGuid);
		}

		if (object == null)
		{
			return "???";
		}

		LanguageEnum lang = this.stubService.getUserSignature().getLanguageEnum();

		StringBuffer infoBuffer = new StringBuffer();

		if (object instanceof FoundationObject)
		{
			infoBuffer.append(StringUtils.getMsrTitle((String) object.get("BOTITLE$"), lang.getType()));
			infoBuffer.append("\n");
			infoBuffer.append(this.stubService.getMSRM().getMSRString("ID_SYS_FIELD_FULLNAME", lang.toString()));
			infoBuffer.append(": ");
			infoBuffer.append(((FoundationObject) object).getFullName());
			infoBuffer.append("\n");

			SystemStatusEnum statusEnum = ((FoundationObject) object).getStatus();
			if (statusEnum != null)
			{
				String phase = StringUtils.getMsrTitle(((FoundationObject) object).getLifecyclePhase(), lang.getType());
				phase = StringUtils.isNullString(phase) ? "" : phase;
				phase += "(" + this.stubService.getMSRM().getMSRString(statusEnum.getMsrId(), lang.toString()) + ")";

				infoBuffer.append(this.stubService.getMSRM().getMSRString(SystemClassFieldEnum.STATUS.getDescription(), lang.toString()));
				infoBuffer.append(": ");
				infoBuffer.append(phase);
				infoBuffer.append("\n");
			}
		}

		List<UIField> fieldList = emm.listFormUIField(className);
		if (SetUtils.isNullList(fieldList))
		{
			return infoBuffer.toString();
		}

		String pName = null;
		String pName1 = null;
		FieldTypeEnum type = null;
		Object value = null;
		String fieldTitle = null;
		for (UIField field : fieldList)
		{
			fieldTitle = field.getTitle(lang);

			pName = field.getName();
			type = field.getType();
			if (type == null)
			{
				continue;
			}
			value = null;
			// if (!((DynaObjectImpl) object).containsKey(pName))
			// {
			// continue;
			// }

			// if (PMFoundationObjectUtil.EXECUTOR.equals(pName))
			// {
			// // 项目执行角色特殊处理
			// infoBuffer.append(fieldTitle);
			// infoBuffer.append(": ");
			// infoBuffer.append((String) object.get(pName + "$NAME"));
			// infoBuffer.append("\n");
			// continue;
			// }
			// if (PMFoundationObjectUtil.EXECUTE_ROLE.equals(pName))
			// {
			// // 项目执行角色特殊处理
			// infoBuffer.append(fieldTitle);
			// infoBuffer.append(": ");
			// infoBuffer.append(StringUtils.convertNULLtoString(object.get(pName + "$NAME")));
			// infoBuffer.append("\n");
			// continue;
			// }

			switch (type)
			{
			case CODE:
			case CODEREF:
			case MULTICODE:
			case CLASSIFICATION:
				pName1 = pName;
				if (pName.endsWith("$"))
				{
					pName += "TITLE";
					pName1 += "NAME";
				}
				else
				{
					pName += "$TITLE";
					pName1 += "$NAME";
				}
				if (!StringUtils.isNullString((String) object.get(pName1)))
				{
					value = StringUtils.getMsrTitle(StringUtils.convertNULLtoString(object.get(pName)), lang.getType()) + "[" + StringUtils.convertNULLtoString(object.get(pName1))
							+ "]";
				}
				else
				{
					value = "";
				}
				break;

			case OBJECT:
				if (pName.endsWith("$"))
				{
					pName += "NAME";
				}
				else
				{
					pName += "$NAME";
				}
				value = StringUtils.convertNULLtoString(object.get(pName));

				break;
			case DATE:
				String date = StringUtils.convertNULLtoString(object.get(pName));
				if (!StringUtils.isNullString(date) && date.length() >= 10)
				{
					value = date.substring(0, 10);
				}
				break;
			default:
				if (SystemClassFieldEnum.LCPHASE.getName().equalsIgnoreCase(pName))
				{
					pName += "TITLE";
					String pValue = (String) object.get(pName);
					value = StringUtils.convertNULLtoString(StringUtils.getMsrTitle(pValue, lang.getType()));
				}
				else if (SystemClassFieldEnum.STATUS.getName().equalsIgnoreCase(pName))
				{
					value = StringUtils.convertNULLtoString(object.get(pName));
				}
				else
				{
					value = StringUtils.convertNULLtoString(object.get(pName));
				}
				break;
			}

			if (value == null)
			{
				value = "";
			}

			infoBuffer.append(fieldTitle);
			infoBuffer.append(": ");
			infoBuffer.append(value);
			infoBuffer.append("\n");
		}
		return infoBuffer.toString();
	}
}
