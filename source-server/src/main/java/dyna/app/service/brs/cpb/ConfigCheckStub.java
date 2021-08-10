package dyna.app.service.brs.cpb;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.regex.Pattern;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.ConfigCalculateVar;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.configparamter.ConfigVariable;
import dyna.common.bean.data.configparamter.DynamicColumnTitle;
import dyna.common.bean.data.configparamter.DynamicOfColumn;
import dyna.common.bean.data.configparamter.DynamicOfMultiVariable;
import dyna.common.bean.data.configparamter.InputVariableValueSelectEnum;
import dyna.common.bean.data.configparamter.TableOfDefineCondition;
import dyna.common.bean.data.configparamter.TableOfExpression;
import dyna.common.bean.data.configparamter.TableOfGroup;
import dyna.common.bean.data.configparamter.TableOfInputVariable;
import dyna.common.bean.data.configparamter.TableOfList;
import dyna.common.bean.data.configparamter.TableOfMark;
import dyna.common.bean.data.configparamter.TableOfMultiCondition;
import dyna.common.bean.data.configparamter.TableOfParameter;
import dyna.common.bean.data.configparamter.TableOfRegion;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ConfigParameterTableType;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.ConfigUtil;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class ConfigCheckStub extends AbstractServiceStub<CPBImpl>
{
	protected ConfigCheckStub(ServiceContext context, CPBImpl service)
	{
		super(context, service);
	}

	protected List<String> checkAllValuesOfTabs(ObjectGuid end1ObjectGuid, DataRule dataRule) throws ServiceRequestException
	{
		FoundationObject end1 = this.stubService.getBOAS().getObject(end1ObjectGuid);
		if (end1 == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "data is not exist, guid='" + end1ObjectGuid.getGuid() + "'", null, end1ObjectGuid.getGuid());
		}

		if (end1.isLatestRevision())
		{
			dataRule.setLocateTime(null);
		}

		// 取得所有配置表数据
		ConfigVariable configVariable = this.stubService.getCPBStub().buildConfigVariable(end1, dataRule.getLocateTime());

		return this.checkAllValuesOfTabs(end1, dataRule, null, configVariable);
	}

	protected List<String> checkIputVar(String id, ConfigCalculateVar configVariable) throws ServiceRequestException
	{
		return this.checkIputVar(id, configVariable, true);
	}

	/**
	 * 驱动前检查输入的G号,L号和输入变量是否正确
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @param gNumber
	 * @param lNumbers
	 * @throws ServiceRequestException
	 */
	protected List<String> checkIputVar(String id, ConfigCalculateVar configVariable, boolean isThrowable) throws ServiceRequestException
	{
		List<String> logList = new ArrayList<String>();

		Pattern p = Pattern.compile("^G[0-9]{2,3}$");
		List<TableOfGroup> gNumberDataList = configVariable.getGroupList();

		List<String> gNumberList = new ArrayList<String>();
		if (!SetUtils.isNullList(gNumberDataList))
		{
			for (TableOfGroup g : gNumberDataList)
			{
				gNumberList.add(g.getGNumber());
			}
		}
		if (!StringUtils.isNullString(configVariable.getGNumber()))
		{
			if (Pattern.compile("^G[0-9]{2,3}$").matcher(configVariable.getGNumber()).find())
			{
				if (SetUtils.isNullList(gNumberList))
				{
					String gTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_G");
					logList.add(this.getMessage("ID_APP_CONFIG_G_TABLE_IS_NULL", id, gTableTitle));
					if (isThrowable)
					{
						throw new ServiceRequestException("ID_APP_CONFIG_G_TABLE_IS_NULL", "gnumber is not null, but gtable is empty.", null, id, gTableTitle);
					}
				}
				else if (gNumberList.contains(configVariable.getGNumber()) == false)
				{
					String gTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_G");
					logList.add(this.getMessage("ID_APP_CONFIG_GNUMBER_IS_WRONG_WHEN_GTAB_HAS_DATA", id, gTableTitle, configVariable.getGNumber()));
					if (isThrowable)
					{
						throw new ServiceRequestException("ID_APP_CONFIG_GNUMBER_IS_WRONG_WHEN_GTAB_HAS_DATA", "gTable has data, gnumber is wrong.", null, id, gTableTitle,
								configVariable.getGNumber());
					}
				}
			}
			else if (!configVariable.getGNumber().startsWith("-"))
			{
				String gTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_G");
				logList.add(this.getMessage("ID_APP_CONFIG_GNUMBER_IS_WRONG", id, gTableTitle, configVariable.getGNumber()));
				if (isThrowable)
				{
					throw new ServiceRequestException("ID_APP_CONFIG_GNUMBER_IS_WRONG", "gnumber is wrong.", null, id, gTableTitle, configVariable.getGNumber());
				}
			}
		}
		else
		{
			if (!SetUtils.isNullList(gNumberList))
			{
				String gTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_G");
				logList.add(this.getMessage("ID_APP_CONFIG_GNUMBER_IS_WRONG_WHEN_GTAB_HAS_DATA", id, gTableTitle, ""));
				if (isThrowable)
				{
					throw new ServiceRequestException("ID_APP_CONFIG_GNUMBER_IS_WRONG_WHEN_GTAB_HAS_DATA", "gTable has data, gnumber is wrong.", null, id, gTableTitle, "");
				}
				// configVariable.setGNumber("G01");
			}

		}

		// 每个分组有且仅有一个L番号(去除L00剩余的L番号数量应该属于不同分组)
		int l00Cnt = 0;
		List<String> lNumberWithoutL00List = new ArrayList<String>();
		String lNumbers = configVariable.getLNumber();
		if (!StringUtils.isNullString(lNumbers))
		{
			String[] tmpArr = lNumbers.toUpperCase().split("L");
			for (int i = 0; i < tmpArr.length; i++)
			{
				if (!StringUtils.isNullString(tmpArr[i]))
				{
					String lNumber = "L" + tmpArr[i];
					if (!"L00".equalsIgnoreCase(lNumber))
					{
						lNumberWithoutL00List.add(lNumber);
					}
					else
					{
						l00Cnt++;
					}
				}
			}

			// L番号不为空，但是L表为空
			List<TableOfList> listOfList = configVariable.getListOfList();
			if (SetUtils.isNullList(listOfList))
			{
				logList.add(this.getMessage("ID_APP_CONFIG_CHECK_VAR_FOUND_ERR", id));
				if (isThrowable)
				{
					throw new ServiceRequestException("ID_APP_CONFIG_CHECK_VAR_FOUND_ERR", "lnumber is not null, but ltable is empty.", null, id);
				}
			}
		}

		List<String> groupList = new ArrayList<String>();
		if (!SetUtils.isNullList(lNumberWithoutL00List))
		{
			for (String lNumber : lNumberWithoutL00List)
			{
				TableOfList lData = configVariable.getListMap().get(lNumber);
				// 输入的L番号不存在
				if (lData == null)
				{
					logList.add(this.getMessage("ID_APP_CONFIG_PARAM_INPT_LNUMBER_NOT_EXIST", id, lNumber));
					if (isThrowable)
					{
						throw new ServiceRequestException("ID_APP_CONFIG_PARAM_INPT_LNUMBER_NOT_EXIST", "lnumber not exist.", null, id, lNumber);
					}
				}
				if (lData != null)
				{
					// 每个分组只能有一个L番号输入
					if (groupList.contains(lData.getGroupName()))
					{
						logList.add(this.getMessage("ID_APP_CONFIG_MORE_THAN_ONE_LNUMBER_PER_GROUP", id));
						if (isThrowable)
						{
							throw new ServiceRequestException("ID_APP_CONFIG_MORE_THAN_ONE_LNUMBER_PER_GROUP", "only one lnumber for one group.id=" + id, null, id);
						}
					}
					else
					{
						groupList.add(lData.getGroupName());
					}
				}
			}
		}

		// 非L00番号+L00番号应该等于分组数量
		List<String> allGroupList = new ArrayList<String>();
		List<TableOfList> listOfList = configVariable.getListOfList();
		if (!SetUtils.isNullList(listOfList))
		{
			for (TableOfList l : listOfList)
			{
				if (!StringUtils.isNullString(l.getGroupName()) && !allGroupList.contains(l.getGroupName()))
				{
					allGroupList.add(l.getGroupName());
				}
			}
		}

		if (groupList.size() + l00Cnt != allGroupList.size())
		{
			logList.add(this.getMessage("ID_APP_CONFIG_ONE_GROUP_ONE_LNUMBER", id));
			if (isThrowable)
			{
				throw new ServiceRequestException("ID_APP_CONFIG_ONE_GROUP_ONE_LNUMBER", "one group should have only one lnumber.id=" + id, null, id);
			}
		}

		Map<String, String> inptMap = configVariable.getInptVarValueMap();
		if (!SetUtils.isNullMap(inptMap))
		{
			for (String inptVar : inptMap.keySet())
			{
				if (!configVariable.getInptVarMap().containsKey(inptVar))
				{
					logList.add(this.getMessage("ID_APP_CONFIG_INPT_VAR_OF_GNUM_NOTEXIST", id, inptVar));
					if (isThrowable)
					{
						throw new ServiceRequestException("ID_APP_CONFIG_INPT_VAR_OF_GNUM_NOTEXIST", "input variable is not exist.inptVar=" + inptVar, null, id, inptVar);
					}
				}
				else if (!StringUtils.isNullString(inptMap.get(inptVar)))
				{
					TableOfInputVariable var = configVariable.getInptVarMap().get(inptVar);
					if (var.getValueType() == InputVariableValueSelectEnum.Select)
					{
						if (StringUtils.isNullString(var.getRange()))
						{
							logList.add(this.getMessage("ID_APP_CONFIG_INPT_VAR_VALUELIST_IS_NULL", id, inptVar));
							if (isThrowable)
							{
								throw new ServiceRequestException("ID_APP_CONFIG_INPT_VAR_VALUELIST_IS_NULL", "input variable is not exist.inptVar=" + inptVar, null, id, inptVar);
							}

						}
						else if (("^" + var.getRange() + "^").contains("^" + inptMap.get(inptVar) + "^") == false)
						{
							logList.add(this.getMessage("ID_APP_CONFIG_INPT_VAR_VALUE_NOT_IN_LIST", id, inptVar));
							if (isThrowable)
							{
								throw new ServiceRequestException("ID_APP_CONFIG_INPT_VAR_VALUE_NOT_IN_LIST", "input variable is not exist.inptVar=" + inptVar, null, id, inptVar);
							}

						}
					}
				}
				if ("MAK".equalsIgnoreCase(inptVar))
				{
					String value = inptMap.get(inptVar);
					List<String> makKeyList = ConfigUtil.resolveMakStr(value);
					if (!SetUtils.isNullList(makKeyList))
					{
						Map<String, TableOfMark> tmpMap = new HashMap<String, TableOfMark>();
						for (TableOfMark makData : configVariable.getMarkList())
						{
							tmpMap.put(makData.getMak(), makData);
						}

						for (String makKey : makKeyList)
						{
							if (tmpMap.get(makKey) == null)
							{
								String tableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_MAK");
								throw new ServiceRequestException("ID_APP_CONFIG_MAK_ERR", "value of mak [" + makKey + "] is null", null, id, inptVar, makKey, tableTitle);
							}
						}
					}
				}
			}
		}

		return logList;
	}

	protected List<String> checkAllValuesOfTabs(FoundationObject end1, DataRule dataRule, List<StructureObject> end2StrucList, ConfigVariable configVariable)
			throws ServiceRequestException
	{
		ObjectGuid end1ObjectGuid = end1.getObjectGuid();
		List<DynamicColumnTitle> titles = configVariable.getTitleList();
		List<TableOfList> tableOfListDataList = configVariable.getListOfList();
		List<TableOfGroup> gDataList = configVariable.getGroupList();
		List<TableOfExpression> fDataList = configVariable.getExpressionList();
		List<TableOfInputVariable> inptDataList = configVariable.getInptVarList();
		List<TableOfParameter> pDataList = configVariable.getParameterList();
		List<TableOfRegion> aerqDataList = configVariable.getRegionList();
		List<TableOfMultiCondition> multiRegionList = configVariable.getMultiRegionList();

		List<String> logList = new ArrayList<String>();

		Map<String, String> titleGuidMap = new HashMap<String, String>();
		List<String> gTitleList = new ArrayList<String>();
		List<String> lTitleList = new ArrayList<String>();
		List<String> aerqTitleList = new ArrayList<String>();
		List<String> pTitleList = new ArrayList<String>();
		if (!SetUtils.isNullList(titles))
		{
			for (DynamicColumnTitle title : titles)
			{
				if (title.getTableType() == ConfigParameterTableType.G)
				{
					if (!gTitleList.contains(title.getTitle()))
					{
						gTitleList.add(title.getTitle());
					}
					else
					{
						// dynamic row title of G Table is already exist
						String rowTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_G");
						logList.add(this.getMessage("ID_APP_CONFIG_TITLE_DUPLICATE_SET", rowTitle, title.getTitle()));
					}
				}
				else if (title.getTableType() == ConfigParameterTableType.La || title.getTableType() == ConfigParameterTableType.Lb)
				{
					titleGuidMap.put(title.getUniqueValue(), title.getTitle());
					if (!lTitleList.contains(title.getTitle()))
					{
						lTitleList.add(title.getTitle());
					}
					else
					{
						// variable of La and Lb can not be same
						String laTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LA");
						String lbTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LB");
						logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_DUPLICATE_SET", laTableTitle, lbTableTitle, title.getTitle()));
					}
				}
				else if (title.getTableType() == ConfigParameterTableType.A || title.getTableType() == ConfigParameterTableType.B
						|| title.getTableType() == ConfigParameterTableType.C || title.getTableType() == ConfigParameterTableType.D
						|| title.getTableType() == ConfigParameterTableType.E || title.getTableType() == ConfigParameterTableType.R
						|| title.getTableType() == ConfigParameterTableType.Q)
				{
					titleGuidMap.put(title.getUniqueValue(), title.getTitle());
					if (!aerqTitleList.contains(title.getTitle()))
					{
						aerqTitleList.add(title.getTitle());
					}
					else
					{
						// variable is already exist
						String tableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_" + title.getTableType().name());
						logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_REG_DUPLICATE_SET", tableTitle, title.getTitle()));
					}
				}
				else if (title.getTableType() == ConfigParameterTableType.P)
				{
					if (!pTitleList.contains(title.getTitle()))
					{
						pTitleList.add(title.getTitle());
					}
					else
					{
						// dynamic row title of P is already exist
						String tableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_P");
						logList.add(this.getMessage("ID_APP_CONFIG_TITLE_DUPLICATE_SET", tableTitle, title.getTitle()));
					}
				}
			}
		}

		List<String> allVariableList = new ArrayList<String>();
		if (!SetUtils.isNullList(titles))
		{
			for (DynamicColumnTitle title : titles)
			{
				if (!allVariableList.contains(title.getTitle()))
				{
					allVariableList.add(title.getTitle());
				}
			}
		}

		// L番号是否有重复
		// 是否存在同一分组有的有值有的没值的情况isSNWithValAndNull
		List<String> lNumberList = new ArrayList<String>();
		if (!SetUtils.isNullList(tableOfListDataList))
		{
			Map<String, List<String>> snOfGroupMap = new HashMap<String, List<String>>();
			for (TableOfList lData : tableOfListDataList)
			{
				if (!"L00".equalsIgnoreCase(lData.getLNumber()))
				{
					if (snOfGroupMap.get(lData.getGroupName()) == null)
					{
						snOfGroupMap.put(lData.getGroupName(), new ArrayList<String>());
					}
					// sn为空时,用@@@@代替
					if (StringUtils.isNullString(lData.getSN()))
					{
						if (!snOfGroupMap.get(lData.getGroupName()).contains("@@@@"))
						{
							snOfGroupMap.get(lData.getGroupName()).add("@@@@");
						}
					}
					else if (snOfGroupMap.get(lData.getGroupName()).size() == 0)
					{
						snOfGroupMap.get(lData.getGroupName()).add(lData.getSN());
					}
					else if (!snOfGroupMap.get(lData.getGroupName()).contains(lData.getSN()))
					{
						logList.add(this.getMessage("ID_APP_CONFIG_SN_DUPLICATE_SET"));
					}
				}

				if (!lNumberList.contains(lData.getLNumber()))
				{
					lNumberList.add(lData.getLNumber());
				}
				else if ("L00".equalsIgnoreCase(lData.getLNumber()) && !StringUtils.isNullString(lData.getGroupName()))
				{
					logList.add(this.getMessage("ID_APP_CONFIG_TABLE_L00_GROUP_IS_NULL"));
				}
				else
				{
					// lNumber is already exist
					String laTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LA");
					String lbTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LB");
					logList.add(this.getMessage("ID_APP_CONFIG_LNUMBER_DUPLICATE_SET", laTableTitle, lbTableTitle, lData.getLNumber()));
				}
			}
		}

		List<String> gNumberList = new ArrayList<String>();
		if (!SetUtils.isNullList(gDataList))
		{
			for (TableOfGroup gData : gDataList)
			{
				if (!gNumberList.contains(gData.getGNumber()))
				{
					gNumberList.add(gData.getGNumber());
				}
				else
				{
					// gNumber is already exist
					logList.add(this.getMessage("ID_APP_CONFIG_GNUMBER_DUPLICATE_SET", gData.getGNumber()));
				}
			}
		}

		if (!SetUtils.isNullList(multiRegionList))
		{
			for (TableOfMultiCondition expression : multiRegionList)
			{
				if (!SetUtils.isNullList(expression.getColumns()))
				{
					for (DynamicOfMultiVariable column : expression.getColumns())
					{
						allVariableList.add(column.getName());
					}
				}
			}
		}

		Map<String, String> inptVarMap = new HashMap<String, String>();
		if (!SetUtils.isNullList(inptDataList))
		{
			for (TableOfInputVariable inptData : inptDataList)
			{
				// 输入变量
				if ((inptData.getName().startsWith("#") || ConfigUtil.isStartWithLetter(inptData.getName())))
				{
					if (ConfigUtil.isStartWithLetter(inptData.getName()) && ConfigUtil.isDrawVariable(inptData.getName()))
					{
						logList.add(this.getMessage("ID_APP_CONFIG_INPT_VARIABLE_IS_RONG", inptData.getName()));
					}
					else if (!inptVarMap.containsKey(inptData.getName()))
					{
						inptVarMap.put(inptData.getName(), null);
						if (!allVariableList.contains(inptData.getName()))
						{
							allVariableList.add(inptData.getName());
						}
						else
						{
							// input variable can not be drawing variable
							logList.add(this.getMessage("ID_APP_CONFIG_INPT_VARIABLE_IS_BE_USED", inptData.getName()));
						}
					}
					else
					{
						// input variable is same
						logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_INPT_DUPLICATE_SET", inptData.getName()));
					}
				}
				else
				{
					// vairable is not right input variable
					logList.add(this.getMessage("ID_APP_CONFIG_INPT_VARIABLE_IS_RONG", inptData.getName()));
				}
			}
		}

		// 先加载所有的F表变量
		if (!SetUtils.isNullList(fDataList))
		{
			for (TableOfExpression fData : fDataList)
			{
				if (!allVariableList.contains(fData.getDrawvariable()))
				{
					allVariableList.add(fData.getDrawvariable());
				}
				else
				{
					String fTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_F");
					logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_REG_DUPLICATE_SET", fTableTitle, fData.getDrawvariable()));
				}
			}
		}

		if (!SetUtils.isNullList(fDataList))
		{
			for (TableOfExpression fData : fDataList)
			{
				List<String> variableInFList = new ArrayList<String>();
				if (!ConfigUtil.isDrawVariable(fData.getDrawvariable()))
				{
					logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IS_WRONG", "F", fData.getDrawvariable()));
				}

				String variableStr = fData.getVariableInFormula();
				String[] variableArr = variableStr.split(",");
				for (String variable : variableArr)
				{
					if (!variableInFList.contains(variable.trim()))
					{
						variableInFList.add(variable.trim());
						if (!allVariableList.contains(variable))
						{
							// 不是图面变量和输入变量，或者变量不存在
							logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IN_FORMULA_IS_WRONG", fData.getDrawvariable(), variable.trim()));
						}
					}
					else
					{
						// variable in formula can not be same
						logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IN_FORMULA_DUPLICATE_SET", fData.getDrawvariable(), variable.trim()));
					}

					if (!fData.getFormula().contains(variable.trim()))
					{
						// variable is not exist in formula
						logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_FORMULA_IS_NOT_USED", fData.getDrawvariable(), variable.trim()));
					}

					if (variable.trim().equals(fData.getDrawvariable()))
					{
						// variable can not be used in it's own table
						logList.add(this.getMessage("ID_APP_CONFIG_PARAM_VARIABLE_CYCLE", fData.getDrawvariable(), variable.trim()));
					}
				}
			}
		}

		if (!SetUtils.isNullList(pDataList))
		{
			for (TableOfParameter pData : pDataList)
			{
				if (!gNumberList.contains(pData.getGNumber()))
				{
					// gNumber in P table is not exist
					String pTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_P");
					String gTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_G");
					logList.add(this.getMessage("ID_APP_CONFIG_GNUMBER_OF_P_IS_NOT_EXIST", pTableTitle, pData.getGNumber(), gTableTitle));
				}
				List<DynamicOfColumn> columns = pData.getColumns();
				if (!SetUtils.isNullList(columns))
				{
					for (DynamicOfColumn column : columns)
					{
						if (!StringUtils.isNullString(column.getValue()) && !inptVarMap.containsKey(column.getValue()))
						{
							// input variable of P table is not exist
							String pTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_P");
							String inptTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_INPT");
							logList.add(this.getMessage("ID_APP_CONFIG_INPTVAR_OF_P_IS_NOT_EXIST", pTableTitle, column.getValue(), inptTableTitle));
						}
					}
				}
			}
		}

		if (!SetUtils.isNullList(tableOfListDataList))
		{
			for (TableOfList lData : tableOfListDataList)
			{
				if (!SetUtils.isNullList(lData.getColumns()))
				{
					for (DynamicOfColumn column : lData.getColumns())
					{
						if (!StringUtils.isNullString(column.getValue()))
						{
							if (ConfigUtil.isVariable(column.getValue(), inptVarMap))
							{
								if (!allVariableList.contains(column.getValue()))
								{
									logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_VALUE_IS_WRONG", lData.getTableType(), column.getValue(), lData.getSequence(),
											column.getName()));
								}
							}
						}
					}
				}

			}
		}

		if (!SetUtils.isNullList(aerqDataList))
		{
			for (TableOfRegion aerqData : aerqDataList)
			{
				this.checkVarOfAERQ(aerqData.getVariable1(), aerqData.getTableType(), allVariableList, inptVarMap, logList);
				if (ConfigUtil.isVariable(aerqData.getLowerLimit1(), inptVarMap))
				{
					this.checkVarOfAERQ(aerqData.getLowerLimit1(), aerqData.getTableType(), allVariableList, inptVarMap, logList);
				}
				if (ConfigUtil.isVariable(aerqData.getUpperLimit1(), inptVarMap))
				{
					this.checkVarOfAERQ(aerqData.getUpperLimit1(), aerqData.getTableType(), allVariableList, inptVarMap, logList);
				}

				if (!StringUtils.isNullString(aerqData.getVariable2()))
				{
					this.checkVarOfAERQ(aerqData.getVariable2(), aerqData.getTableType(), allVariableList, inptVarMap, logList);
					if (ConfigUtil.isVariable(aerqData.getLowerLimit2(), inptVarMap))
					{
						this.checkVarOfAERQ(aerqData.getLowerLimit2(), aerqData.getTableType(), allVariableList, inptVarMap, logList);
					}
					if (ConfigUtil.isVariable(aerqData.getUpperLimit2(), inptVarMap))
					{
						this.checkVarOfAERQ(aerqData.getUpperLimit2(), aerqData.getTableType(), allVariableList, inptVarMap, logList);
					}
				}
				if (!SetUtils.isNullList(aerqData.getColumns()))
				{
					for (DynamicOfColumn column : aerqData.getColumns())
					{
						if (!StringUtils.isNullString(column.getValue()))
						{
							if (ConfigUtil.isVariable(column.getValue(), inptVarMap))
							{
								if (!allVariableList.contains(column.getValue()))
								{
									logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_VALUE_IS_WRONG", aerqData.getTableType(), column.getValue(), aerqData.getSequence(),
											column.getName()));
								}
							}
						}
					}
				}
			}
		}

		if (!SetUtils.isNullList(multiRegionList))
		{
			for (TableOfMultiCondition expression : multiRegionList)
			{
				if (!SetUtils.isNullList(expression.getConditions()))
				{
					for (TableOfDefineCondition condition : expression.getConditions())
					{
						if (!allVariableList.contains(condition.getDefinitionName()))
						{
							// 不是图面变量和输入变量，或者变量不存在
							logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IN_MULTI_IS_WRONG", expression.getIndex(), condition.getDefinitionName().trim()));
						}
						if (!StringUtils.isNullString(condition.getDefinitionValue()))
						{
							if (condition.getDefinitionValue().contains(";"))
							{
								String[] variableArr = condition.getDefinitionValue().split(",");
								for (String variable : variableArr)
								{
									if (ConfigUtil.isVariable(variable, inptVarMap))
									{
										logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_VALUE_IS_WRONG", ConfigParameterTableType.M, condition.getDefinitionValue(),
												expression.getIndex(), condition.getDefinitionName()));
									}

								}
							}
							else
							{
								if (ConfigUtil.isVariable(condition.getDefinitionValue(), inptVarMap))
								{
									logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_VALUE_IS_WRONG", ConfigParameterTableType.M, condition.getDefinitionValue(),
											expression.getIndex(), condition.getDefinitionName()));
								}
							}
						}
					}
				}
				if (!SetUtils.isNullList(expression.getColumns()))
				{
					for (DynamicOfMultiVariable column : expression.getColumns())
					{
						if (ConfigUtil.isVariable(column.getValue(), inptVarMap))
						{
							if (!allVariableList.contains(column.getValue()))
							{
								logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_VALUE_IS_WRONG", ConfigParameterTableType.M, column.getValue(), expression.getIndex(),
										column.getName()));
							}
						}
					}
				}

			}
		}

		List<StructureObject> end2StructureObjectList = end2StrucList;
		if (SetUtils.isNullList(end2StrucList))
		{
			end2StructureObjectList = this.stubService.listStructureObject(end1ObjectGuid, dataRule, gNumberList, null, null);
		}
		if (!SetUtils.isNullList(end2StructureObjectList))
		{
			for (StructureObject structureObject : end2StructureObjectList)
			{
				FoundationObject end2 = (FoundationObject) structureObject.get(StructureObject.END2_UI_OBJECT);
				// 客供料的编号，必须在输入变量中存在，且输入变量的值不能为空
				// 修改为不存在则舍弃
				/*
				 * if (end2.getId().startsWith("#") && !"#W".equals(end2.getId()) &&
				 * !inptVarList.contains(end2.getId()))
				 * {
				 * String inptTableName = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_INPT");
				 * logList.add(this.getMessage("ID_APP_CONFIG_CLIENT_SUPPLIED_ITEM_ID_ERR", end2.getId(),
				 * inptTableName));
				 * }
				 */
				ClassInfo end2ClassInfo = this.stubService.getEMM().getClassByGuid(end2.getObjectGuid().getClassGuid());

				// 客供料不需要转换类，查询所有物料
				boolean isSuppliedItem = false;
				if (inptVarMap.containsKey(end2.getId()) && !ConfigParameterConstants.NO_DRAWING_ITEM_ID.equals(end2.getId())
						&& end2.getId().startsWith(ConfigParameterConstants.SUPPLIED_MATERIALS_ITEM_ID_PREFIX))
				{
					isSuppliedItem = true;
				}

				if (end2ClassInfo.hasInterface(ModelInterfaceEnum.IManufacturingRule) && !isSuppliedItem)
				{
					String matchedClass = (String) end2.get(ConfigParameterConstants.MATCHEDCLASS);
					if ("#W".equals(end2.getId()))
					{
						matchedClass = (String) end1.get(ConfigParameterConstants.MATCHEDCLASS);
					}
					String titleOfMatchedClass = this.getMessage("ID_APP_CONFIGTITLE_MATCHED_TITLE");
					if (!StringUtils.isGuid(matchedClass))
					{
						// matchedClass can not be null
						logList.add(this.getMessage("ID_APP_CONFIG_MATCHED_NOT_EXIST", end2.getId(), titleOfMatchedClass));
					}
					else
					{
						CodeItemInfo codeItemInfo = this.stubService.getEMM().getCodeItem(matchedClass);
						if (codeItemInfo == null)
						{
							// matchedClass can not be null
							logList.add(this.getMessage("ID_APP_CONFIG_MATCHED_NOT_EXIST", end2.getId(), titleOfMatchedClass));
						}
						else
						{
							String matchedClassName = codeItemInfo.getName();
							ClassInfo classInfo = this.stubService.getEMM().getClassByName(matchedClassName);
							if (classInfo == null)
							{
								// matchedClass is not exist
								logList.add(this.getMessage("ID_APP_CONFIG_MATCHED_NOT_EXIST", titleOfMatchedClass));
							}
						}
					}
				}

				String partNumber = (String) structureObject.get(ConfigParameterConstants.PARTNUMBER);
				if (!StringUtils.isNullString(partNumber) && ConfigUtil.isVariable(partNumber, inptVarMap))
				{
					if (!allVariableList.contains(partNumber))
					{
						// 对象中的件号不存在
						logList.add(this.getMessage("ID_APP_CONFIG_OBJ_NUM_VAR_IS_NOT_EXIST", end2.getId(), partNumber));
					}
				}

				String lNumbers = (String) structureObject.get(ConfigParameterConstants.LNUMBER);
				if (!StringUtils.isNullString(lNumbers))
				{
					lNumberList = ConfigUtil.transferLNumberStrToList(lNumbers);
					for (String lNumber＿ : lNumberList)
					{
						// 是否有字母和数字以外的其他字符
						if (!lNumber＿.matches("[0-9A-Za-z]*"))
						{
							// 对象中的选配不正确
							logList.add(this.getMessage("ID_APP_CONFIG_OBJ_LNUMBER_NOT_RIGHT", end2.getId(), lNumber＿));
						}
						// 以数字结尾时,必须以L开头
						// 不能以数字开头
						else if ((ConfigUtil.isEndWithNumber(lNumber＿) && !lNumber＿.startsWith("L")) || lNumber＿.substring(0, 1).matches("[0-9]"))
						{
							// 对象中的选配不正确
							logList.add(this.getMessage("ID_APP_CONFIG_OBJ_LNUMBER_NOT_RIGHT", end2.getId(), lNumber＿));
						}
						else if (!allVariableList.contains(lNumber＿))
						{
							// 对象的L番号不存在
							logList.add(this.getMessage("ID_APP_CONFIG_OBJ_NUM_VAR_IS_NOT_EXIST", end2.getId(), lNumber＿));
						}
					}
				}

				String configParameter = (String) structureObject.get(ConfigParameterConstants.CONFIGPARAMETER);
				if (!StringUtils.isNullString(configParameter))
				{
					if (configParameter.contains(";"))
					{
						String[] tmpArr = configParameter.split(";");
						for (String var : tmpArr)
						{
							if (var.contains("@"))
							{
								String var_ = var.substring(var.indexOf("@") + 1);
								if (!allVariableList.contains(var_))
								{
									// {0}的配置参数中引用的父类图面变量{1}或者输入变量{2}不存在
									logList.add(this.getMessage("ID_APP_CONFIG_VAR_END1_IS_NOT_EXIST", end2.getId(), var_, var_));
								}
							}
						}
					}
					else
					{
						if (configParameter.contains("@"))
						{
							String var = configParameter.substring(configParameter.indexOf("@") + 1);
							if (!allVariableList.contains(var))
							{
								// {0}的配置参数中引用的父类图面变量{1}或者输入变量{2}不存在
								logList.add(this.getMessage("ID_APP_CONFIG_VAR_END1_IS_NOT_EXIST", end2.getId(), var, var));
							}
						}
					}
				}
			}
		}

		return logList;
	}

	private void checkVarOfAERQ(String variable, ConfigParameterTableType tableType, List<String> allVariableList, Map<String, String> inputMap, List<String> logList)
			throws ServiceRequestException
	{
		String messageId = "CONFIGPARAMETERTABLETYPE_TABLE_" + tableType.getName();
		String titleOfTable = this.getMessage(messageId);
		if (!ConfigUtil.isVariable(variable, inputMap) && !allVariableList.contains(variable))
		{
			// 不是图面变量和输入变量，或者变量不存在
			logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IS_WRONG", titleOfTable, variable));
		}
		else if (!allVariableList.contains(variable))
		{
			// variable is not exist or not a variable
			logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IS_WRONG", titleOfTable, variable));
		}

		if (ConfigUtil.isVariableOfTable(variable, tableType.name()))
		{
			logList.add(this.getMessage("ID_APP_CONFIG_VARIABLE_IN_OWN_TAB", titleOfTable, variable));
		}
	}

	private String getMessage(String id, Object... agrs) throws ServiceRequestException
	{
		return this.stubService.getMSRM().getMSRString(id, this.stubService.getUserSignature().getLanguageEnum().getId(), agrs);
	}
}
