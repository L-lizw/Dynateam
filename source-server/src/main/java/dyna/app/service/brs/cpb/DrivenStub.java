package dyna.app.service.brs.cpb;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.*;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class DrivenStub extends AbstractServiceStub<CPBImpl>
{

	protected DrivenResult drivenTestByConfigRulesAll(FoundationObject end1, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException
	{
		DataRule rule = new DataRule();
		rule.setLocateTime(end1.getReleaseTime());
		rule.setSystemStatus(end1.getStatus());

		List<StructureObject> finalList = new ArrayList<StructureObject>();

		DrivenResult result = this.drivenTestByConfigRules(end1, strucSearchCondition, end2SearchCondition, rule, gNumber, lNumbers, inptVarriables);
		List<String> varLogList = null;
		String exceptionMeString = null;
		if (result != null)
		{
			varLogList = result.getVarLogList();
			exceptionMeString = result.getErrMsg();
			end1.put(ConfigParameterConstants.LOGS, result.getVarLogList());
			end1.put(ConfigParameterConstants.UNIQUENO, this.getUniqueNo(end1.getId(), gNumber, lNumbers, inptVarriables));
			// 取得所有配置表数据
			ConfigCalculateVar configVariable = this.stubService.getCPBStub().buildConfigCalculateVar(end1, rule.getLocateTime());
			configVariable.setAllInput(gNumber, lNumbers, inptVarriables);
			end1.put(ConfigParameterConstants.PARAMETERDESC, this.stubService.getDrivenStub().getParameterDesc(gNumber, lNumbers, inptVarriables, configVariable));
			this.resetSpecialProperty(end1, result.getVariableValMap());
		}

		if (StringUtils.isNullString(exceptionMeString))
		{
			int level = 1;
			if (!SetUtils.isNullList(result.getStructureObjectList()))
			{
				for (StructureObject struc : result.getStructureObjectList())
				{
					struc.put("END1_UI_OBJECT", end1);
					struc.put("LEVEL$", "1." + String.valueOf(level++));
					finalList.add(struc);
					exceptionMeString = recursionGetStrucObjList(struc, strucSearchCondition, end2SearchCondition, finalList);
					if (!StringUtils.isNullString(exceptionMeString))
					{
						break;
					}
				}
			}
		}
		result = new DrivenResult();
		result.setVarLogList(varLogList);
		result.setErrMsg(exceptionMeString);
		result.setStructureObjectList(finalList);

		return result;
	}

	protected DrivenResult drivenTestByConfigRules(FoundationObject end1, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException
	{
		return this.drivenByConfigRules(end1, strucSearchCondition, end2SearchCondition, dataRule, gNumber, lNumbers, inptVarriables);
	}

	/**
	 * 根据配置规则驱动
	 * 
	 * @param end1ObjectGuid
	 * @param strucSearchCondition
	 * @param end2SearchCondition
	 * @param dataRule
	 * @param gNumber
	 * @param lNumbers
	 * @param inptVarriables
	 * @param isSave
	 * @return
	 * @throws ServiceRequestException
	 */
	protected DrivenResult drivenByConfigRules(FoundationObject end1, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule, String gNumber,
			String lNumbers, String inptVarriables) throws ServiceRequestException
	{
		try
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(end1.getObjectGuid().getClassGuid());
			if (!classInfo.hasInterface(ModelInterfaceEnum.IManufacturingRule))
			{
				return null;
			}

			if (dataRule == null)
			{
				dataRule = new DataRule();
				dataRule.setLocateTime(end1.getReleaseTime());
			}

			if (end1.isLatestRevision())
			{
				dataRule.setLocateTime(null);
			}

			// 取得所有配置表数据
			ConfigCalculateVar configVariable = this.stubService.getCPBStub().buildConfigCalculateVar(end1, dataRule.getLocateTime());

			configVariable.setAllInput(gNumber, lNumbers, inptVarriables);
			// 解析输入变量
			inptVarriables = configVariable.getRegularInputVarString();

			// 验证输入参数
			this.stubService.getConfigCheckStub().checkIputVar(end1.getId(), configVariable);

			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1.getObjectGuid(),
					ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);

			// 计算变量
			Map<String, String> variableMap = this.stubService.getVarCalculateStub().listAllVariableValue(end1.getObjectGuid(), dataRule.getLocateTime(), configVariable);
			StringBuffer exceptionMsgBuffer = new StringBuffer();
			String end1UniqueNo = this.getUniqueNo(end1.getId(), gNumber, lNumbers, inptVarriables);
			// 取得材料明细
			List<String> gNumberList = this.getGNumberList(configVariable);
			List<StructureObject> end2List = this.stubService.listStructureObject(end1.getObjectGuid(), dataRule, gNumberList, strucSearchCondition, end2SearchCondition);
			if (SetUtils.isNullList(end2List) && (inptVarriables == null || inptVarriables.indexOf("MAK=") == -1))
			{
				return this.buildDrivenResult(variableMap, null, configVariable.getLogList(), exceptionMsgBuffer.toString(), end1UniqueNo);
			}

			// 检查配置表
			List<String> resultLogList = this.stubService.checkAllValuesOfTabs(end1, dataRule, end2List, configVariable);
			if (!SetUtils.isNullList(resultLogList))
			{
				String errors = "";
				for (String a : resultLogList)
				{
					if (errors.length() > 1)
					{
						errors = errors + ";";
					}
					errors = errors + a;
				}
				throw new ServiceRequestException("ID_APP_CONFIG_CHECK_VAR_FOUND_ERR", "config data is wrong.", null, end1.getId(), errors);
			}

			List<TableOfGroup> gNumberDataList = configVariable.getGroupList();
			boolean haveGNumberTableData = !SetUtils.isNullList(gNumberDataList);

			List<StructureObject> structureObjectList = null;
			try
			{
				structureObjectList = this.calculateEnd2Var(end1, end2List, dataRule, relationTemplate, configVariable, gNumber, configVariable.getInptVarValueMap(), variableMap,
						configVariable.getErrVarList(), haveGNumberTableData, exceptionMsgBuffer);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				String message = e.getMessage();
				if (StringUtils.isNullString(message))
				{
					message = e.getCause().getMessage();
				}
				exceptionMsgBuffer.append(this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_FAILED2", message));
			}

			if (exceptionMsgBuffer.length() > 0)
			{
				configVariable.addToVarLogList(exceptionMsgBuffer.toString());
			}
			return this.buildDrivenResult(variableMap, structureObjectList, configVariable.getLogList(), exceptionMsgBuffer.toString(), end1UniqueNo);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}
	}

	private List<String> getGNumberList(ConfigVariable configVariable)
	{
		List<String> gNumberList = new ArrayList<String>();
		List<TableOfGroup> groupList = configVariable.getGroupList();
		if (!SetUtils.isNullList(groupList))
		{
			for (TableOfGroup g : groupList)
			{
				gNumberList.add(g.getGNumber());
			}
		}
		return gNumberList;
	}

	private DrivenResult buildDrivenResult(Map<String, String> variableMap, List<StructureObject> structureObjectList, List<String> logList, String errMsg, String end1UniqueNo)
	{
		DrivenResult result = new DrivenResult();
		result.setStructureObjectList(structureObjectList);
		result.setVarLogList(logList);
		result.setErrMsg(errMsg);
		result.setVariableValMap(variableMap);
		result.put("@@" + ConfigParameterConstants.PARAMETERDESC, end1UniqueNo);
		return result;
	}

	protected FoundationObject saveDrivenTestResult(ObjectGuid end1ObjectGuid, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException
	{
		FoundationObject draw = this.stubService.getBOAS().getObject(end1ObjectGuid);
		if (draw == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "draw is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}
		if (draw.getStatus() != SystemStatusEnum.RELEASE)
		{
			throw new ServiceRequestException("ID_APP_DRAW_NOT_RLS", "draw is not released, id='" + draw.getId() + "'", null, draw.getId());
		}

		FoundationObject root = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			root = this.saveObjectByDriven(root, draw, strucSearchCondition, end2SearchCondition, dataRule, gNumber, lNumbers, inptVarriables);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		return root;
	}

	private FoundationObject saveObjectByDriven(FoundationObject rootItem, FoundationObject end1, SearchCondition strucSearchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, String gNumber, String lNumbers, String inptVarriables) throws Exception
	{
		DrivenResult result = this.drivenByConfigRules(end1, strucSearchCondition, end2SearchCondition, dataRule, gNumber, lNumbers, inptVarriables);
		if (result == null)
		{
			return rootItem;
		}

		// 生成END1的物料
		if (rootItem == null)
		{
			rootItem = this.createItemFromDraw(end1, gNumber, lNumbers, inptVarriables);
		}

		if (!StringUtils.isNullString(result.getErrMsg()))
		{
			throw new ServiceRequestException("ID_APP_CONFIG_SAVE_OBJ_ERR", "instance cannot be created.", null, result.getErrMsg());
		}
		List<StructureObject> structureObjectList = result.getStructureObjectList();

		if (!SetUtils.isNullList(structureObjectList))
		{
			for (StructureObject struc : structureObjectList)
			{
				String partNumber = (String) struc.get(ConfigParameterConstants.PARTNUMBER);
				String lNumber = (String) struc.get(ConfigParameterConstants.LNUMBER);
				String inptVariables = (String) struc.get(ConfigParameterConstants.CONFIGPARAMETER);

				FoundationObject end2 = struc.getEnd2UIObject();
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(end2.getObjectGuid().getClassGuid());
				if (classInfo.hasInterface(ModelInterfaceEnum.IManufacturingRule) && end2.getStatus() != SystemStatusEnum.RELEASE)
				{
					throw new ServiceRequestException("ID_APP_DRAW_NOT_RLS", "draw is not released, id='" + end2.getId() + "'", null, end2.getId());
				}

				this.createItemFromDraw(end2, partNumber, lNumber, inptVariables);

				this.saveObjectByDriven(rootItem, end2, strucSearchCondition, end2SearchCondition, dataRule, partNumber, lNumber, inptVariables);
			}
		}

		return rootItem;
	}

	// 根据唯一号查询物料对象，当对象不存在时，创建
	protected FoundationObject saveFoundationObject(FoundationObject foundationObject, SearchCondition itemSearchCondition, String partNumber, String lNumbers, String inputVars,
			ConfigVariable configVariable) throws ServiceRequestException
	{
		Date ruleTime = foundationObject.getReleaseTime();
		if (foundationObject.isLatestRevision())
		{
			ruleTime = null;
		}

		if (configVariable == null)
		{
			return this.saveFoundationObject(foundationObject, itemSearchCondition, partNumber, lNumbers, inputVars);
		}

		// 重置L番号顺序
		String lNumber_ = ConfigUtil.resetLNumbers(lNumbers);
		// 重置输入变量顺序
		String inputVars_ = ConfigUtil.resetInputVariables(configVariable, partNumber, inputVars);

		FoundationObject foundationObject_ = new FoundationObjectImpl();
		foundationObject_.sync(foundationObject);
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(foundationObject_.getObjectGuid().getClassGuid());
		if (classInfo.hasInterface(ModelInterfaceEnum.IManufacturingRule))
		{
			String matchedClassGuid = (String) foundationObject_.get(ConfigParameterConstants.MATCHEDCLASS);
			String itemClassificationName = (String) foundationObject_.get(ConfigParameterConstants.ITEM_CLASSIFICATION + "$NAME");
			String itemClassificationGuid = (String) foundationObject_.get(ConfigParameterConstants.ITEM_CLASSIFICATION);

			StringBuffer exceptionMsgBuffer = new StringBuffer();
			ClassInfo targetClassInfo = this.getTargetClass(null, foundationObject_.getId(), matchedClassGuid, exceptionMsgBuffer);
			if (exceptionMsgBuffer.length() > 0)
			{
				throw new ServiceRequestException("ID_APP_CONFIG_SAVE_OBJ_ERR", "instance cannot be created.", null, exceptionMsgBuffer.toString());
			}

			String uniqueNo = this.getUniqueNo(foundationObject.getId(), partNumber, lNumber_, inputVars_);
			FoundationObject newIns = searchInstanceByUniqueNo(itemSearchCondition, uniqueNo, targetClassInfo.getName());
			if (newIns == null)
			{
				configVariable = this.stubService.getCPBStub().buildOtherConfigVariable(foundationObject, ruleTime, configVariable);

				this.clearForCreateObj(foundationObject_);

				FoundationObject newEnd2 = this.stubService.getBOAS().newFoundationObject(targetClassInfo.getGuid(), targetClassInfo.getName());
				((FoundationObjectImpl) newEnd2).putAll((FoundationObjectImpl) foundationObject_);
				FoundationObject tmpFoundationObject = this.stubService.getBOAS().getObject(foundationObject.getObjectGuid());
				this.copy(tmpFoundationObject, newEnd2);

				newEnd2.put(ConfigParameterConstants.ORIGOBJ, foundationObject_.getObjectGuid().getGuid());
				newEnd2.put(ConfigParameterConstants.ORIGOBJ + "$CLASS", foundationObject_.getObjectGuid().getClassGuid());
				newEnd2.put(ConfigParameterConstants.ORIGOBJ + "$MASTER", foundationObject_.getObjectGuid().getMasterGuid());
				newEnd2.put(ConfigParameterConstants.IS_NO_DRAWING_ITEM, foundationObject_.get(ConfigParameterConstants.IS_NO_DRAWING_ITEM));
				newEnd2.put(ConfigParameterConstants.PARTNUMBER, partNumber);
				newEnd2.put(ConfigParameterConstants.LNUMBER, lNumber_);
				newEnd2.put(ConfigParameterConstants.UNIQUENO, uniqueNo);
				newEnd2.put(SystemClassFieldEnum.CLASSIFICATION.getName() + "$NAME", itemClassificationName);
				newEnd2.put(SystemClassFieldEnum.CLASSIFICATION.getName(), itemClassificationGuid);
				if (!StringUtils.isNullString(lNumber_))
				{
					newEnd2.put(ConfigParameterConstants.LNUMBERWITHOUTL00, lNumber_.replace("L00", ""));
				}
				newEnd2.put(ConfigParameterConstants.CONFIGPARAMETER, inputVars_);

				FoundationObject retObject = this.stubService.getBOAS().createObject(newEnd2);
				retObject.put(ConfigParameterConstants.IS_NEW_ITEM, BooleanUtils.getBooleanStringYN(true));
				return retObject;
			}
			return newIns;
		}
		return null;
	}

	// 根据唯一号查询物料对象，当对象不存在时，创建
	protected FoundationObject saveFoundationObject(FoundationObject foundationObject, SearchCondition itemSearchCondition, String partNumber, String lNumbers, String inputVars)
			throws ServiceRequestException
	{
		Date ruleTime = foundationObject.getReleaseTime();
		if (foundationObject.isLatestRevision())
		{
			ruleTime = null;
		}

		// 输入变量重置需要取得配置数据
		ConfigVariable configVariable = this.stubService.getCPBStub().getInputVars(foundationObject, ruleTime);

		return this.saveFoundationObject(foundationObject, itemSearchCondition, partNumber, lNumbers, inputVars, configVariable);
	}

	/**
	 * 把源对象中存在,但是目标对象中不存在的属性拷贝到目标对象中
	 * 
	 * @param orig
	 * @param dest
	 */
	private void copy(FoundationObject orig, FoundationObject dest)
	{
		List<String> fieldNeedClearList = ConfigParameterConstants.getFieldNeedClearList();
		if (orig != null && dest != null)
		{
			for (String key : ((FoundationObjectImpl) orig).keySet())
			{
				if (!SetUtils.isNullList(fieldNeedClearList) && fieldNeedClearList.contains(key))
				{
					continue;
				}
				if (dest.get(key) == null)
				{
					dest.put(key, orig.get(key));
				}
			}
		}
	}

	/**
	 * 驱动结果为图纸类,需要判断图纸对象对应物料是否存在,不存在则创建
	 * 
	 * @param foundationObject
	 * @param partNumber
	 * @param lNumber
	 * @param inptVariables
	 * @param rawMCode
	 * @throws ServiceRequestException
	 */
	private FoundationObject createItemFromDraw(FoundationObject foundationObject, String partNumber, String lNumber, String inptVariables) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(foundationObject.getObjectGuid().getClassGuid());
		if (classInfo.hasInterface(ModelInterfaceEnum.IManufacturingRule))
		{
			FoundationObject item = this.saveFoundationObject(foundationObject, null, partNumber, lNumber, inptVariables);
			if (item.get(ConfigParameterConstants.IS_NEW_ITEM) != null && BooleanUtils.getBooleanByYN((String) item.get(ConfigParameterConstants.IS_NEW_ITEM)))
			{
				String templateName = this.getCADTemplate(classInfo.getName());
				RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(item.getObjectGuid(), templateName);
				if (relationTemplate == null)
				{
					throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + templateName, null, templateName);
				}

				StructureObject structureObject = this.stubService.getBOAS().newStructureObject(relationTemplate.getStructureClassGuid(), relationTemplate.getStructureClassName());
				this.stubService.getBOAS().link(item.getObjectGuid(), foundationObject.getObjectGuid(), structureObject, templateName);
			}
			return item;
		}
		return null;
	}

	private List<StructureObject> calculateEnd2Var(FoundationObject end1, List<StructureObject> end2List, DataRule dataRule, RelationTemplateInfo relationTemplate,
			ConfigVariable configVariable, String gNumber, Map<String, String> inptMap, Map<String, String> variableMap, List<String> errVarList, boolean haveGNumberTableData,
			StringBuffer exceptionMsgBuffer) throws Exception
	{
		List<StructureObject> resultList = new ArrayList<StructureObject>();

		// 如果输入变量包含MAK，则需要对MAK进行解析
		if (inptMap.containsKey(ConfigParameterConstants.MAK))
		{
			String makVal = inptMap.get(ConfigParameterConstants.MAK);

			Map<String, TableOfMark> makValMap = new HashMap<String, TableOfMark>();
			if (!StringUtils.isNullString(makVal))
			{
				List<TableOfMark> makList = configVariable.getMarkList();
				if (!SetUtils.isNullList(makList))
				{
					// 根据MAK值解析出MAK序列
					makValMap = this.getMakValMap(makVal, makList);
				}
			}

			if (!SetUtils.isNullMap(makValMap))
			{
				for (String key : makValMap.keySet())
				{
					TableOfMark mak = makValMap.get(key);

					FoundationObjectImpl newEnd2 = (FoundationObjectImpl) end1.getClass().getConstructor().newInstance();
					newEnd2.sync(end1);
					newEnd2.put(ConfigParameterConstants.PARTNUMBER, gNumber);
					newEnd2.put(ConfigParameterConstants.LNUMBER, mak.getValue());
					newEnd2.put(ConfigParameterConstants.UNIQUENO, this.getUniqueNo(end1.getId(), gNumber, mak.getValue(), null));
					newEnd2.clear(ConfigParameterConstants.CONFIGPARAMETER);

					String gNumberDesc = this.getGDescription(gNumber, configVariable.getGroupMap().get(gNumber));
					String lNumberDesc = this.getLDescription(mak.getValue(), configVariable);
					newEnd2.put(ConfigParameterConstants.PARAMETERDESC,
							ConfigParameterConstants.UNIQUE_SPLIT_CHAR + gNumberDesc + ConfigParameterConstants.UNIQUE_SPLIT_CHAR + lNumberDesc);

					StructureObject structureObject = this.stubService.getBOAS().newStructureObject(relationTemplate.getStructureClassGuid(),
							relationTemplate.getStructureClassName());
					structureObject.setEnd2ObjectGuid(newEnd2.getObjectGuid());
					structureObject.put(BOMStructure.END2_UI_OBJECT, newEnd2);
					structureObject.put(ConfigParameterConstants.LNUMBER, mak.getValue()); // MAK值对应参数值
					structureObject.put(ConfigParameterConstants.PARTNUMBER, gNumber); // MAK值对应参数值
					structureObject.put(ConfigParameterConstants.QUANTITY, new BigDecimal(1));
					structureObject.put(StructureObject.DRIVERTREEITEMKEY, StringUtils.generateRandomUID(32));
					structureObject.setSequence(DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP));

					resultList.add(structureObject);
				}
				return resultList;
			}
		}

		if (!SetUtils.isNullList(end2List))
		{
			for (StructureObject strucObject : end2List)
			{
				FoundationObject end2 = (FoundationObject) strucObject.get(BOMStructure.END2_UI_OBJECT);
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(end2.getObjectGuid().getClassGuid());

				// 客供料检查
				boolean isSuppliedItem = false;
				if (end2.getId().startsWith(ConfigParameterConstants.SUPPLIED_MATERIALS_ITEM_ID_PREFIX) && !end2.getId().equals(ConfigParameterConstants.NO_DRAWING_ITEM_ID))
				{
					isSuppliedItem = true;
					if (StringUtils.isNullString(inptMap.get(end2.getId())))
					{
						continue;
					}
				}

				String matchedClassGuid = (String) end2.get(ConfigParameterConstants.MATCHEDCLASS);
				// 取得转换类
				FoundationObject customerSupplyDraw = null;
				if (classInfo.hasInterface(ModelInterfaceEnum.IManufacturingRule))
				{
					String replaceId = inptMap.get(end2.getId());
					// 客供料(#A)都是图纸,需要继续向下驱动
					if (isSuppliedItem)
					{
						if (StringUtils.isNullString(replaceId))
						{
							continue;
						}

						customerSupplyDraw = this.getCustomerSupplyDraw(replaceId);
						if (customerSupplyDraw == null)
						{
							continue;
						}
					}
					// 客供料不需要配置转换类
					else
					{
						if (ConfigParameterConstants.NO_DRAWING_ITEM_ID.equals(end2.getId()))
						{
							// 无图件使用父阶的转换类
							matchedClassGuid = (String) end1.get(ConfigParameterConstants.MATCHEDCLASS);
						}

						StringBuffer errLogBuffer = new StringBuffer();
						this.getTargetClass(end1.getId(), end2.getId(), matchedClassGuid, errLogBuffer);
						if (errLogBuffer.length() > 0)
						{
							exceptionMsgBuffer.append(errLogBuffer);
							return resultList;
						}
					}
				}

				String origId = end2.getId();
				String partNumberOfEnd2 = (String) strucObject.get(ConfigParameterConstants.PARTNUMBER);// 件号
				String lNumberOfEnd2 = (String) strucObject.get(ConfigParameterConstants.LNUMBER);// L号
				String configRule = (String) strucObject.get(ConfigParameterConstants.CONFIGPARAMETER); // 配置参数

				// 计算用量
				String quantityVariable = this.getQuantityVariable(strucObject, gNumber, haveGNumberTableData);
				if (!StringUtils.isNullString(quantityVariable) && errVarList.contains(quantityVariable))
				{
					String end2Id = strucObject.getEnd2UIObject().getId();
					String quantityTitle = this.getMessage("ID_APP_CONFIG_PARAM_QUANTITY");
					exceptionMsgBuffer.append(this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_FAILED4", end1.getId(), end2Id, quantityTitle, quantityVariable));
					return resultList;
				}

				String quantity = this.getQuantity(strucObject, quantityVariable, variableMap);
				// 数量为0不显示
				if (quantity == null || "0".equals(quantity))
				{
					continue;
				}

				// 件号
				StringBuffer errMsgBuffer = new StringBuffer();
				partNumberOfEnd2 = this.getPartNumberOfEnd2(strucObject, end1.getId(), end2.getId(), partNumberOfEnd2, variableMap, errVarList, errMsgBuffer);
				if (errMsgBuffer.length() > 0)
				{
					exceptionMsgBuffer.append(errMsgBuffer);
					return resultList;
				}

				// 计算L号的值
				errMsgBuffer.setLength(0);
				lNumberOfEnd2 = this.getLNumber(end1.getId(), end2.getId(), lNumberOfEnd2, inptMap, variableMap, errVarList, errMsgBuffer);
				if (errMsgBuffer.length() > 0)
				{
					exceptionMsgBuffer.append(errMsgBuffer);
					return resultList;
				}

				// 计算配置参数的值
				String configRule_ = this.getConfigRule(configRule, inptMap, variableMap, end1.getId(), exceptionMsgBuffer, errVarList);
				if (exceptionMsgBuffer.length() != 0)
				{
					return resultList;
				}

				if (NumberUtils.isNumeric(quantity))
				{
					strucObject.put(ConfigParameterConstants.QUANTITY, new BigDecimal(quantity));
				}
				else
				{
					String message = this.getMessage("ID_APP_CONFIG_QUANTITY_IS_NOT_NUMBER", end2.getId(), quantity);
					exceptionMsgBuffer.append(message);
					return resultList;
				}

				// 合成唯一号
				String name = end2.getName();
				if (ConfigParameterConstants.NO_DRAWING_ITEM_ID.equals(end2.getId()))
				{
					origId = end1.getId();
					name = (String) strucObject.get(ConfigParameterConstants.CPNAME);
					end2.setObjectGuid(end1.getObjectGuid());
					end2.put(ConfigParameterConstants.IS_NO_DRAWING_ITEM, "Y");
				}
				if (isSuppliedItem)
				{
					origId = customerSupplyDraw.getId();
					name = (String) strucObject.get(ConfigParameterConstants.CPNAME);
				}

				String uniqueNo = this.getUniqueNo(origId, partNumberOfEnd2, lNumberOfEnd2, configRule_);
				end2.put(ConfigParameterConstants.MATCHEDCLASS, matchedClassGuid);
				end2.put(ConfigParameterConstants.UNIQUENO, uniqueNo);
				end2.put(ConfigParameterConstants.PARTNUMBER, partNumberOfEnd2);
				end2.put(ConfigParameterConstants.LNUMBER, lNumberOfEnd2);
				end2.put(ConfigParameterConstants.CONFIGPARAMETER, configRule_);
				DataRule rule2 = new DataRule();
				rule2.setLocateTime(end2.getReleaseTime());
				rule2.setSystemStatus(end2.getStatus());
				ConfigCalculateVar end2ConfigVar = this.stubService.getCPBStub().buildConfigCalculateVar(end2, rule2.getLocateTime());
				end2ConfigVar.setAllInput(partNumberOfEnd2, lNumberOfEnd2, configRule_);
				end2.put(ConfigParameterConstants.PARAMETERDESC, this.getParameterDesc(partNumberOfEnd2, lNumberOfEnd2, configRule_, end2ConfigVar));
				end2.put(SystemClassFieldEnum.ID.getName(), origId);
				end2.put(SystemClassFieldEnum.NAME.getName(), name);
				if (customerSupplyDraw != null)
				{
					strucObject.put(BOMStructure.END2_UI_OBJECT, customerSupplyDraw);
				}
				strucObject.put(ConfigParameterConstants.PARTNUMBER, partNumberOfEnd2);
				strucObject.put(ConfigParameterConstants.LNUMBER, lNumberOfEnd2);
				strucObject.put(ConfigParameterConstants.CONFIGPARAMETER, configRule_);
				strucObject.put(StructureObject.DRIVERTREEITEMKEY, StringUtils.generateRandomUID(32));

				resultList.add(strucObject);
			}
		}

		return resultList;
	}

	private ClassInfo getTargetClass(String end1Id, String end2Id, String matchedClassGuid, StringBuffer exceptionMsgBuffer) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(matchedClassGuid))
		{
			String title = this.getMessage("ID_APP_CONFIGTITLE_MATCHED_TITLE");
			String message = this.getMessage("ID_APP_CONFIG_MATCHED_NOT_EXIST", end2Id, title);
			exceptionMsgBuffer.append(message);
			return null;
		}

		CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(matchedClassGuid);
		if (codeItem == null)
		{
			String title = this.getMessage("ID_APP_CONFIGTITLE_MATCHED_TITLE");
			String message = this.getMessage("ID_APP_CONFIG_MATCHED_NOT_EXIST", end2Id, title);
			exceptionMsgBuffer.append(message);
			return null;
		}

		String targetClassName = codeItem.getName();
		ClassInfo targetClassInfo = this.stubService.getEMM().getClassByName(targetClassName);
		if (targetClassInfo == null)
		{
			String title = this.getMessage("ID_APP_CONFIGTITLE_MATCHED_TITLE");
			exceptionMsgBuffer.append(this.getMessage("ID_APP_CONFIG_MATCHED_NOT_EXIST", end2Id, title));
			return null;
		}
		if (!targetClassInfo.hasInterface(ModelInterfaceEnum.INewProductOfConfigure))
		{
			exceptionMsgBuffer.append(this.getMessage("ID_APP_CONFIG_MATCHED_NOT_RIGHT", end1Id, end2Id));
			return null;
		}
		return targetClassInfo;
	}

	private String getParameterDesc(String partNumber, String lNumber, String configRule, ConfigVariable configVariable)
	{
		String gDesc = this.getGDescription(partNumber, configVariable.getGroupMap().get(partNumber));
		String lDesc = this.getLDescription(lNumber, configVariable);
		String configRuleDesc = this.getConfigRuleDescription(configRule, configVariable);
		return gDesc + ConfigParameterConstants.UNIQUE_SPLIT_CHAR + lDesc + ConfigParameterConstants.UNIQUE_SPLIT_CHAR + configRuleDesc;
	}

	private FoundationObject getCustomerSupplyDraw(String id) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IManufacturingRule, null);
		ObjectGuid objectGuid_ = new ObjectGuid();
		objectGuid_.setClassGuid(classInfo.getGuid());
		objectGuid_.setClassName(classInfo.getName());
		SearchCondition sc = SearchConditionFactory.createSearchCondition(objectGuid_, null, true);
		sc.setCaseSensitive(true);
		sc.addFilter(SystemClassFieldEnum.ID, id, OperateSignEnum.EQUALS);
		sc.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
		List<FoundationObject> objectList = this.stubService.getBOAS().listObject(sc);
		if (SetUtils.isNullList(objectList))
		{
			return null;
		}
		return objectList.get(0);
	}

	private FoundationObject searchInstanceByUniqueNo(SearchCondition sc, String uniqueNo, String className) throws ServiceRequestException
	{
		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(className, null, true);
		if (sc != null)
		{
			List<String> resultFieldList = sc.getResultFieldList();
			if (!SetUtils.isNullList(resultFieldList))
			{
				for (String field : resultFieldList)
				{
					searchCondition.addResultField(field);
				}
			}
			List<String> uiNameList = sc.listResultUINameList();
			if (!SetUtils.isNullList(uiNameList))
			{
				for (String uiName : uiNameList)
				{
					searchCondition.addResultUIObjectName(uiName);
				}
			}
		}
		else
		{
			List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(className);
			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}
		}
		searchCondition.addFilter(ConfigParameterConstants.UNIQUENO, uniqueNo, OperateSignEnum.EQUALS);
		searchCondition.addResultField(ConfigParameterConstants.IS_NO_DRAWING_ITEM);
		searchCondition.setCaseSensitive(true);
		searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
		List<FoundationObject> list = this.stubService.getBOAS().listObject(searchCondition);
		if (!SetUtils.isNullList(list))
		{
			return list.get(0);
		}
		return null;
	}

	public String getUniqueNo(String id, String partNumber, String lNumbers, String inputVars)
	{
		StringBuffer uniqueBuffer = new StringBuffer();
		uniqueBuffer.append(id);
		uniqueBuffer.append(ConfigParameterConstants.UNIQUE_SPLIT_CHAR);
		uniqueBuffer.append(StringUtils.convertNULLtoString(partNumber));
		uniqueBuffer.append(ConfigParameterConstants.UNIQUE_SPLIT_CHAR);
		uniqueBuffer.append(StringUtils.convertNULLtoString(lNumbers));
		uniqueBuffer.append(ConfigParameterConstants.UNIQUE_SPLIT_CHAR);
		uniqueBuffer.append(StringUtils.convertNULLtoString(inputVars));

		return uniqueBuffer.toString();
	}

	/**
	 * 取得选配的值
	 * 
	 * @param end1Id
	 * @param end2Id
	 * @param lNumberOfEnd2
	 * @param inptMap
	 * @param variableMap
	 * @param exceptionMsgBuffer
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getLNumber(String end1Id, String end2Id, String lNumberOfEnd2, Map<String, String> inptMap, Map<String, String> variableMap, List<String> errVariableList,
			StringBuffer exceptionMsgBuffer) throws ServiceRequestException
	{
		if (!StringUtils.isNullString(lNumberOfEnd2))
		{
			List<String> lNumberList = ConfigUtil.transferLNumberStrToList(lNumberOfEnd2);
			StringBuffer buffer = new StringBuffer();
			for (String lNumber : lNumberList)
			{
				String lNumberStr_ = inptMap.get(lNumber);
				if (StringUtils.isNullString(lNumberStr_))
				{
					lNumberStr_ = variableMap.get(lNumber);
				}
				if (lNumberStr_ != null)
				{
					if (!lNumberStr_.startsWith("L") || !NumberUtils.isNumeric(lNumberStr_.substring(1)))
					{
						String message = this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_FAILED_L_FORMAT", end2Id, lNumber + "=" + lNumberStr_);
						exceptionMsgBuffer.append(message);
						return null;
					}
					else if (!StringUtils.isNullString(lNumberStr_))
					{
						buffer.append(lNumberStr_);
					}
				}
				else if (errVariableList.contains(lNumber))
				{
					String message = this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_FAILED", end1Id, end2Id, lNumberOfEnd2);
					exceptionMsgBuffer.append(message);
					return null;
				}
			}
			if (buffer.length() > 0)
			{
				lNumberOfEnd2 = buffer.toString();
			}
		}
		return lNumberOfEnd2;
	}

	/**
	 * 取得件号的值
	 * 
	 * @param strucObject
	 * @param end1Id
	 * @param end2Id
	 * @param gNumber
	 * @param partNumberOfEnd2
	 * @param variableMap
	 * @param exceptionMsgBuffer
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getPartNumberOfEnd2(StructureObject strucObject, String end1Id, String end2Id, String partNumberOfEnd2, Map<String, String> variableMap, List<String> errVarList,
			StringBuffer exceptionMsgBuffer) throws ServiceRequestException
	{
		if (errVarList.contains(partNumberOfEnd2))
		{
			String message = this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_FAILED", end1Id, end2Id, partNumberOfEnd2);
			exceptionMsgBuffer.append(message);
			return partNumberOfEnd2;
		}

		if (variableMap.containsKey(partNumberOfEnd2))
		{
			partNumberOfEnd2 = variableMap.get(partNumberOfEnd2);
		}

		if (partNumberOfEnd2 != null)
		{
			if (partNumberOfEnd2.startsWith("G") && !NumberUtils.isNumeric(partNumberOfEnd2.substring(1)))
			{
				String message = this.getMessage("ID_APP_CONFIG_PARAM_DRIVE_FAILED_G_FORMAT", end2Id, partNumberOfEnd2);
				exceptionMsgBuffer.append(message);
			}
		}
		return partNumberOfEnd2;
	}

	private String getQuantityVariable(StructureObject strucObject, String gNumber, boolean haveGNumberTableData)
	{
		String quantityVariable = null;
		if (!StringUtils.isNullString(gNumber))
		{
			quantityVariable = (String) strucObject.get(gNumber);
		}
		// 父阶没有G表数据，取固定列
		else if (!haveGNumberTableData)
		{
			quantityVariable = strucObject.get(ConfigParameterConstants.QUANTITY) == null ? "0" : ((Number) strucObject.get(ConfigParameterConstants.QUANTITY)).toString();
		}

		return quantityVariable;
	}

	/**
	 * 取得数量
	 * 
	 * @param strucObject
	 * @param origGNumber
	 * @param gNumber
	 * @param haveGNumberTableData
	 * @param variableMap
	 * @param errVarList
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getQuantity(StructureObject strucObject, String quantityVariable, Map<String, String> variableMap) throws ServiceRequestException
	{
		// 计算用量
		String quantity = null;
		if (!StringUtils.isNullString(quantityVariable))
		{
			if (variableMap.containsKey(quantityVariable))
			{
				quantity = variableMap.get(quantityVariable);
				if (StringUtils.isNullString(quantity))
				{
					quantity = "0";
				}
			}
			else
			{
				quantity = quantityVariable;
			}
		}

		return quantity;
	}

	/**
	 * 取得G号描述
	 * 
	 * @param gNumber
	 * @param group
	 * @return
	 */
	private String getGDescription(String gNumber, TableOfGroup group)
	{
		if (group == null)
		{
			return StringUtils.convertNULLtoString(gNumber);
		}

		List<DynamicOfColumn> columns = group.getColumns();

		StringBuffer buffer = new StringBuffer();
		if (!SetUtils.isNullList(columns))
		{
			for (DynamicOfColumn column : columns)
			{
				buffer.append("[").append(column.getValue()).append("]");
			}
		}

		if (buffer.length() > 0)
		{
			return buffer.toString();
		}

		return StringUtils.convertNULLtoString(gNumber);
	}

	/**
	 * 取得L号的描述
	 * 
	 * @param lNumbers
	 * @param configVariable
	 * @return
	 */
	private String getLDescription(String lNumbers, ConfigVariable configVariable)
	{
		List<String> lNumberList = ConfigUtil.transferLNumberStrToList(lNumbers);
		StringBuffer buffer = new StringBuffer();
		if (!SetUtils.isNullList(lNumberList))
		{
			for (String lNumber : lNumberList)
			{
				TableOfList l = configVariable.getListMap().get(lNumber);
				if (l == null || StringUtils.isNullString(l.getDescription()))
				{
					buffer.append(lNumber);
				}
				else
				{
					buffer.append(l.getDescription());
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 取得输入变量的描述
	 * 
	 * @param configRule
	 * @param configVariable
	 * @return
	 */
	private String getConfigRuleDescription(String configRule, ConfigVariable configVariable)
	{
		StringBuffer buffer = new StringBuffer();
		if (StringUtils.isNullString(configRule))
		{
			return configRule;
		}
		if (configRule.contains(";"))
		{
			String[] tmpInptArr = configRule.split(";");
			for (String tmpInpt : tmpInptArr)
			{
				if (buffer.length() > 0)
				{
					buffer.append(";");
				}
				if (tmpInpt.contains("="))
				{
					String[] tmpArr = StringUtils.splitStringWithDelimiterHavEnd("=", tmpInpt);
					TableOfInputVariable inpt = configVariable.getInptVarMap().get(tmpArr[0]);
					if (inpt == null || StringUtils.isNullString(inpt.getDescription()))
					{
						buffer.append(tmpInpt);
					}
					else
					{
						buffer.append(inpt.getDescription()).append("=").append(StringUtils.convertNULLtoString(tmpArr[1]));
					}
				}
				else
				{
					buffer.append(tmpInpt);
				}
			}
		}
		else
		{
			if (configRule.contains("="))
			{
				String[] tmpArr = configRule.split("=");
				TableOfInputVariable inpt = configVariable.getInptVarMap().get(tmpArr[0]);
				if (inpt == null || StringUtils.isNullString(inpt.getDescription()))
				{
					buffer.append(configRule);
				}
				else if (tmpArr.length > 1)
				{
					buffer.append(inpt.getDescription()).append("=").append(tmpArr[1]);
				}
			}
			else
			{
				buffer.append(configRule);
			}
		}

		if (buffer.length() > 0)
		{
			return buffer.toString();
		}
		return configRule;
	}

	/**
	 * 配置规则中L参数和W参数是特殊的，可以引用图面变量，其他的引用P变量
	 * 
	 * @param configRule
	 * @param end1InptMap
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getConfigRule(String configRule, Map<String, String> end1InptMap, Map<String, String> end1VariableMap, String end1Id, StringBuffer exceptionMsgBuffer,
			List<String> errVariableList) throws ServiceRequestException
	{
		StringBuffer configRuleBuffer = new StringBuffer();
		if (!StringUtils.isNullString(configRule))
		{
			String[] configRuleArr = null;
			if (configRule.contains(";"))
			{
				configRuleArr = configRule.split(";");
			}
			else
			{
				configRuleArr = new String[] { configRule };
			}

			for (int i = 0; i < configRuleArr.length; i++)
			{
				if (configRuleBuffer.length() > 0)
				{
					configRuleBuffer.append(";");
				}

				String configRule_ = configRuleArr[i];
				// 配置规则包含“@”符号(@J或者MAK=@MAK或者@MAK)，要从父对象的P变量取值
				if (configRule_.contains("@"))
				{
					// 配置规则包含“=”(MAK=@MAK)，则把“=”后的部分替换为父对象的P变量值（去掉"@"符号从父对象的P变量取值）
					if (configRule_.contains("="))
					{
						// 取得“=”符号前面的内容(如MAK或J)
						String paramName = (configRule_.split("="))[0];
						// 去掉"@"符号(如等号后面的MAK)
						String paramVar = (configRule_.split("="))[1].substring(1);

						if (errVariableList.contains(paramVar))
						{
							exceptionMsgBuffer.append(this.getMessage("ID_APP_CONFIG_PARENT_VAR_IS_NULL", end1Id, paramVar));
							return null;
						}

						String paramVal = null;
						// 引用的是图面变量
						if (end1VariableMap.containsKey(paramVar))
						{
							paramVal = StringUtils.convertNULLtoString(end1VariableMap.get(paramVar));
						}
						else if (end1InptMap.containsKey(paramVar))
						{
							paramVal = StringUtils.convertNULLtoString(end1InptMap.get(paramVar));
						}
						else
						{
							paramVal = StringUtils.convertNULLtoString(paramVar);
						}
						configRuleBuffer.append(paramName);
						configRuleBuffer.append("=");
						configRuleBuffer.append(paramVal);
					}
					// 配置规则不包含“=”(@MAK)，则直接把当前值替换为父对象的P变量的值
					else
					{
						// 去掉"@"符号
						String paramVal = configRule_.substring(1);
						if (errVariableList.contains(paramVal))
						{
							exceptionMsgBuffer.append(this.getMessage("ID_APP_CONFIG_PARENT_VAR_IS_NULL", end1Id, paramVal));
							return null;
						}

						String val = StringUtils.convertNULLtoString(end1InptMap.get(paramVal));
						configRuleBuffer.append(paramVal);
						configRuleBuffer.append("=");
						configRuleBuffer.append(val);
					}
				}
				// 配置规则不包含“@”符号(MAK=....)
				else
				{
					configRuleBuffer.append(configRule_);
				}
			}
		}
		return configRuleBuffer.toString();
	}

	/**
	 * 解析MAK字符串取得对应的MAK值
	 * 
	 * @param makVal
	 * @param makList
	 * @return
	 */
	private Map<String, TableOfMark> getMakValMap(String makVal, List<TableOfMark> makList)
	{
		Map<String, TableOfMark> tmpMap = new HashMap<String, TableOfMark>();
		for (TableOfMark makData : makList)
		{
			tmpMap.put(makData.getMak(), makData);
		}

		List<String> makKeyList = ConfigUtil.resolveMakStr(makVal);

		Map<String, TableOfMark> resultMap = new HashMap<String, TableOfMark>();
		if (!SetUtils.isNullList(makKeyList))
		{
			for (String makKey : makKeyList)
			{
				resultMap.put(makKey, tmpMap.get(makKey));
			}
		}

		return resultMap;
	}

	private void clearForCreateObj(FoundationObject foundationObject)
	{
		List<String> fieldNeedClearList = ConfigParameterConstants.getFieldNeedClearList();
		if (!SetUtils.isNullList(fieldNeedClearList))
		{
			for (String fieldName : fieldNeedClearList)
			{
				foundationObject.clear(fieldName);
			}
		}
		foundationObject.put(SystemClassFieldEnum.STATUS.getName(), SystemStatusEnum.WIP);
	}

	private String getMessage(String id, Object... agrs) throws ServiceRequestException
	{
		return this.stubService.getMSRM().getMSRString(id, this.stubService.getUserSignature().getLanguageEnum().getId(), agrs);
	}

	protected void saveDriveHistory(ObjectGuid objectGuid, String gNumber, String lNumbers, String inptVarriables) throws ServiceRequestException
	{
		String conditions = "";
		if (!StringUtils.isNullString(gNumber))
		{
			conditions = conditions + gNumber + ConfigParameterConstants.TEST_LOG_CONDITION_SPLIT_CHAR;
		}
		if (!StringUtils.isNullString(lNumbers))
		{
			conditions = conditions + lNumbers + ConfigParameterConstants.TEST_LOG_CONDITION_SPLIT_CHAR;
		}
		if (!StringUtils.isNullString(inptVarriables))
		{
			conditions = conditions + inptVarriables + ConfigParameterConstants.TEST_LOG_CONDITION_SPLIT_CHAR;
		}
		conditions = conditions.substring(0, conditions.length() - 1);

		TestHistory history = new TestHistory();
		history.setFoGuid(objectGuid.getGuid());
		history.setUserGuid(this.stubService.getUserSignature().getUserGuid());
		history.setConditionName(ConfigParameterConstants.CONFIG_PARAMETER_TEST_CONDITION_NAME);
		history.setConditions(conditions);
		this.stubService.saveTestHistory(history);
	}

	protected String getCADTemplate(String className) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByName(className);
		if (classInfo.hasInterface(ModelInterfaceEnum.ICAD2D))
		{
			return ConfigParameterConstants.CONFIG_PARAMETER_ITEM_CAD2D_TEMPLATE_NAME;
		}
		else
		{
			return ConfigParameterConstants.CONFIG_PARAMETER_ITEM_CAD3D_TEMPLATE_NAME;
		}
	}

	private String recursionGetStrucObjList(StructureObject structureObj, SearchCondition condition, SearchCondition end2SearchCondition, List<StructureObject> finalList)
			throws ServiceRequestException
	{
		String partNumber = structureObj.get(ConfigParameterConstants.PARTNUMBER) == null ? null : structureObj.get(ConfigParameterConstants.PARTNUMBER).toString();
		String lNumber = structureObj.get(ConfigParameterConstants.LNUMBER) == null ? null : structureObj.get(ConfigParameterConstants.LNUMBER).toString();
		String paramTemp = structureObj.get(ConfigParameterConstants.CONFIGPARAMETER) == null ? null : structureObj.get(ConfigParameterConstants.CONFIGPARAMETER).toString();

		FoundationObject fo = (FoundationObject) structureObj.get(StructureObject.END2_UI_OBJECT);

		DataRule rule = new DataRule();
		rule.setLocateTime(fo.getReleaseTime());
		rule.setSystemStatus(fo.getStatus());
		DrivenResult result = this.drivenTestByConfigRules(fo, condition, end2SearchCondition, rule, partNumber, lNumber, paramTemp);
		if (result != null)
		{
			if (!StringUtils.isNullString(result.getErrMsg()))
			{
				return result.getErrMsg();
			}
			this.resetSpecialProperty(fo, result.getVariableValMap());
		}

		int level = 1;
		if (result != null && !SetUtils.isNullList(result.getStructureObjectList()))
		{
			for (StructureObject struc : result.getStructureObjectList())
			{
				struc.put("END1_UI_OBJECT", fo);
				struc.put("LEVEL$", (String) structureObj.get("LEVEL$") + "." + level++);
				finalList.add(struc);
				recursionGetStrucObjList(struc, condition, end2SearchCondition, finalList);
			}
		}
		return null;
	}

	private void resetSpecialProperty(FoundationObject foundationObject, Map<String, String> variableMap) throws ServiceRequestException
	{
		this.setOtherFieldByVariableMap(foundationObject, variableMap);
	}

	private void setOtherFieldByVariableMap(FoundationObject foundationObject, Map<String, String> variableMap) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(foundationObject.getObjectGuid(), this.stubService);
		UIObjectInfo uiObject = this.stubService.getEMM().getUIObjectByName(foundationObject.getObjectGuid().getClassName(), "ListUIFormConfig");
		if (uiObject != null)
		{
			List<UIField> uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField uiField : uiFieldList)
				{
					this.setOthers(foundationObject, uiField.getName(), variableMap);
				}
			}
		}
	}

	private void setOthers(FoundationObject foundationObject, String propertyName, Map<String, String> variableMap) throws ServiceRequestException
	{
		ClassField field = this.stubService.getEMM().getFieldByName(foundationObject.getObjectGuid().getClassName(), propertyName, true);
		String origVal = (String) foundationObject.get(propertyName);
		if (!StringUtils.isNullString(origVal))
		{
			if (variableMap.containsKey(origVal))
			{
				if (field.getType() == FieldTypeEnum.STRING)
				{
					foundationObject.put(propertyName, variableMap.get(origVal));
				}
				else if ((field.getType() == FieldTypeEnum.INTEGER || field.getType() == FieldTypeEnum.FLOAT) && NumberUtils.isNumeric(variableMap.get(origVal)))
				{
					foundationObject.put(propertyName, variableMap.get(origVal) == null ? null : new BigDecimal(variableMap.get(origVal)));
				}
			}
			else if (origVal.contains(";"))
			{
				if (field.getType() == FieldTypeEnum.STRING)
				{
					setSpecialFieldContainVariable(foundationObject, variableMap, propertyName);
				}
			}
			else
			{
				if (field.getType() == FieldTypeEnum.INTEGER || field.getType() == FieldTypeEnum.FLOAT)
				{
					foundationObject.put(propertyName, new BigDecimal(origVal));
				}
				else
				{
					foundationObject.put(propertyName, origVal);
				}
			}
		}
	}

	private void setSpecialFieldContainVariable(FoundationObject foundationObject, Map<String, String> variableMap, String propertyName)
	{
		String origVal = (String) foundationObject.get(propertyName);
		if (!StringUtils.isNullString(origVal))
		{
			if (origVal.contains(";"))
			{
				StringBuffer buffer = new StringBuffer();
				String[] tmpArr = origVal.split(";");
				for (String s : tmpArr)
				{
					if (s.contains("@"))
					{
						String[] tmpArr_ = s.split("=");
						String variable = tmpArr_[1].substring(1);

						buffer.append(tmpArr_[0]);
						buffer.append("=");
						buffer.append(variableMap.get(variable));
						buffer.append(";");
					}
					else
					{
						buffer.append(s);
						buffer.append(";");
					}
				}
				foundationObject.put(propertyName, buffer.subSequence(0, buffer.length() - 1).toString());
			}
			else if (origVal.contains("@"))
			{
				StringBuffer buffer = new StringBuffer();
				String[] tmpArr_ = origVal.split("=");
				String variable = tmpArr_[1].substring(1);

				buffer.append(tmpArr_[0]);
				buffer.append("=");
				buffer.append(variableMap.get(variable));
				foundationObject.put(propertyName, buffer.toString());
			}
			else if (variableMap.containsKey(origVal))
			{
				foundationObject.put(propertyName, variableMap.get(origVal));
			}
			else
			{
				foundationObject.put(propertyName, origVal);
			}
		}
	}

}