/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MySearchStub
 * Wanglei 2011-3-30
 */
package dyna.app.service.brs.pos;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.edap.EDAPImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.FilterBuilder;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.Criterion;
import dyna.common.FieldOrignTypeEnum;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.Search;
import dyna.common.dto.SearchDetail;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.BooleanUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.service.acl.AclService;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Wanglei
 * 
 */
@Component
public class MySearchStub extends AbstractServiceStub<POSImpl>
{

	protected void deleteSearch(String searchGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Search search = this.getSearch(searchGuid, false);
		if (search != null && search.getType() == SearchTypeEnum.PUBLIC)
		{

			boolean hasAuthorityForPublicSearch = true;
			if (Constants.isSupervisor(true, this.stubService))
			{
				AclService aclService = DataServer.getAclService();
				hasAuthorityForPublicSearch = aclService.hasAuthorityForPublicSearch(searchGuid, PublicSearchAuthorityEnum.DELETE, this.stubService.getSignature().getCredential());

			}
			if (!hasAuthorityForPublicSearch)
			{
				throw new ServiceRequestException("ID_APP_NO_DELETE_PUBLIC_SEARCH_ACL", "has not ACL for delete public search");
			}

		}

		try
		{
			sds.delete(Search.class, searchGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected Search getSearch(String searchGuid, boolean createCondtion) throws ServiceRequestException
	{

		Search search = null;

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();

		filter.put(Search.GUID, searchGuid);

		try
		{
			search = sds.queryObject(Search.class, filter, "selectByGuid");

			if (search != null && createCondtion)
			{
				search = this.organizeSearchObject(search, this.stubService.getEMM());
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return search;

	}

	protected Search getSearchLatest() throws ServiceRequestException
	{
		Search search = null;

		SystemDataService sds = DataServer.getSystemDataService();

		String operatorGuid = this.stubService.getOperatorGuid();

		Map<String, Object> filter = new HashMap<String, Object>();

		filter.put(Search.OWNER_USER, operatorGuid);

		try
		{
			List<Search> searchList = sds.query(Search.class, filter);
			if (!SetUtils.isNullList(searchList))
			{
				search = this.organizeSearchObject(searchList.get(0), this.stubService.getEMM());
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return search;
	}

	protected List<Search> listSearch(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<Search> retList = new ArrayList<Search>();
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = FilterBuilder.buildFilterJustCondition(searchCondition);

		String operatorGuid = this.stubService.getOperatorGuid();

		List<Search> searchList = null;

		filter.put(Search.OWNER_USER, operatorGuid);

		try
		{
			searchList = sds.query(Search.class, filter);

			if (!SetUtils.isNullList(searchList))
			{
				for (Search search : searchList)
				{
					try
					{
						search = this.organizeSearchObject(search, this.stubService.getEMM());
						retList.add(search);
					}
					catch (Exception e)
					{
						this.deleteSearch(search.getGuid());
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		// 排序
		final Map<String, Boolean> orderMap;
		if (!SetUtils.isNullList(searchCondition.getOrderMapList()))
		{
			orderMap = searchCondition.getOrderMapList().get(0);
		}
		else
		{
			orderMap = null;
		}
		if (!SetUtils.isNullMap(orderMap))
		{
			Collections.sort(retList, new Comparator<Search>()
			{

				@Override
				public int compare(Search o1, Search o2)
				{
					for (String fieldName : orderMap.keySet())
					{
						Boolean sortDirection = orderMap.get(fieldName);
						if (fieldName.endsWith("$"))
						{
							fieldName = fieldName.substring(0, fieldName.length() - 1);
						}
						Object o1Content = o1.get(fieldName);
						Object o2Content = o2.get(fieldName);

						if (o1Content == null && o2Content == null)
						{
							continue;
						}

						if (sortDirection == null)
						{
							sortDirection = true;
						}

						if (o1Content == null)
						{
							if (sortDirection)
							{
								return 1;
							}
							else
							{
								return -1;
							}
						}
						else if (o2Content == null)
						{
							if (sortDirection)
							{
								return -1;
							}
							else
							{
								return 1;
							}
						}
						else
						{
							if (o1Content instanceof Date)
							{
								if (sortDirection)
								{
									return DateFormat.compareDate((Date) o1Content, (Date) o2Content, DateFormat.PTN_YMDHMS);
								}
								else
								{
									return DateFormat.compareDate((Date) o2Content, (Date) o1Content, DateFormat.PTN_YMDHMS);
								}
							}
							else if (o1Content instanceof Number)
							{
								if (sortDirection)
								{
									if (((Number) o1Content).doubleValue() > ((Number) o2Content).doubleValue())
									{
										return 1;
									}
									else
									{
										return -1;
									}
								}
								else
								{
									if (((Number) o1Content).doubleValue() > ((Number) o2Content).doubleValue())
									{
										return -1;
									}
									else
									{
										return 1;
									}
								}
							}
							else
							{
								if (sortDirection)
								{
									return o1Content.toString().compareTo(o2Content.toString());
								}
								else
								{
									return o2Content.toString().compareTo(o1Content.toString());
								}
							}
						}

					}
					return 0;
				}

			});
		}

		return retList;
	}

	protected List<Search> listPublicSearch(SearchCondition searchCondition, boolean isCheckACL) throws ServiceRequestException
	{
		List<Search> retList = new ArrayList<Search>();
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = null;
		if (searchCondition != null)
		{
			filter = FilterBuilder.buildFilterJustCondition(searchCondition);
		}
		List<Search> searchList = null;
		if (filter == null)
		{
			filter = new HashMap<String, Object>();
		}

		filter.put(Search.TYPE, SearchTypeEnum.PUBLIC.getValue());

		try
		{
			searchList = sds.query(Search.class, filter);
			AclService aclService = DataServer.getAclService();
			if (!SetUtils.isNullList(searchList))
			{
				for (Search search : searchList)
				{
					try
					{

						boolean hasAuthorityForPublicSearch = true;
						if (isCheckACL && Constants.isSupervisor(true, this.stubService))
						{
							hasAuthorityForPublicSearch = aclService.hasAuthorityForPublicSearch(search.getGuid(), PublicSearchAuthorityEnum.READ,
									this.stubService.getSignature().getCredential());

						}

						if (hasAuthorityForPublicSearch)
						{
							search = this.organizeSearchObject(search, this.stubService.getEMM());
							retList.add(search);
						}
					}
					catch (Exception e)
					{
						this.deleteSearch(search.getGuid());
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return retList;
	}

	/**
	 * 将查询出来的search对象中存放检索条件的两个字符串解析成Criterion对象的List形式，并放回Search对象中
	 * 
	 * @param search
	 *            要处理的search对象
	 * @return 处理后的search对象
	 * @throws ServiceRequestException
	 */
	public Search organizeSearchObject(Search search, EMM emm) throws ServiceRequestException
	{
		if (search == null)
		{
			return null;
		}

		String classGuid = search.getClassGuid();

		String classificationGuid = search.getClassification();
		String folderGuid = search.getFolderGuid();
		String firstCondition = search.getFirstCondition();
		String secondCondition = search.getSecondCondition();
		String className = null;
		BOInfo boInfo = null;
		SearchCondition sc;

		Folder folder = null;

		if (folderGuid != null)
		{
			folder = ((EDAPImpl) this.stubService.getEDAP()).getFolderStub().getFolder(folderGuid, false);
		}

		if (search.getClassGuid() != null)
		{
			ClassInfo classInfo = null;
			try
			{
				classInfo = emm.getClassByGuid(classGuid);
			}
			catch (Exception ignored)
			{
			}
			if (classInfo != null)
			{
				className = classInfo.getName();
			}

			String bmGuid = this.stubService.getEMM().getCurrentBizModel().getGuid();
			boInfo = this.stubService.getEMM().getBizObject(bmGuid, classGuid, classificationGuid);

			sc = SearchConditionFactory.createSearchCondition4GlobalSearch(boInfo, folder, false);
		}
		else
		{
			sc = SearchConditionFactory.createSearchCondition4GlobalSearch(boInfo, folder, false);
		}

		if (boInfo != null)
		{
			search.setBOTitle(boInfo.getTitle());
		}

		sc.setIncludeOBS(search.isIncludeOBS());
		sc.setHasSubFolders(search.hasSubFolders());
		sc.setSearchRevisionTypeEnum(search.getSearchRevisionType());
		sc.setCaseSensitive(search.caseSensitive());
		sc.setOwnerOnly(search.isOwnerOnly());
		sc.setOwnerGroupOnly(search.isOwnerGroupOnly());
		sc.setIsAdvanced(search.isAdvanced());

		sc.setBOMTemplateId(search.getBomTemplateId());
		sc.setRelationTemplateId(search.getRelationTemplateId());
		sc.setUserProductBOGuid(search.getUserProductBOGuid());
		sc.setParentGuid(search.getParentGuid());
		sc.setCheckOutOnly(search.isCheckOutOnly());
		sc.setSearchType(search.getAdvancedQueryType());
		if (firstCondition != null)
		{

			if (secondCondition != null)
			{
				firstCondition = firstCondition + secondCondition;
			}

			String[] conditionArray = StringUtils.splitStringWithDelimiter(";", firstCondition);

			if (conditionArray != null)
			{
				for (String critationStr : conditionArray)
				{

					String[] critationArray = StringUtils.splitStringWithDelimiterHavEnd(",", critationStr);

					assert critationArray != null;
					String value = critationArray[3] == null ? "" : critationArray[3];
					FieldOrignTypeEnum fieldOrignType = FieldOrignTypeEnum.CLASS;
					if (critationArray.length > 4)
					{
						fieldOrignType = critationArray[4] == null ? FieldOrignTypeEnum.CLASS : FieldOrignTypeEnum.valueOf(critationArray[4]);
					}

					Object objectValue = null;

					if (!SearchCondition.VALUE_NULL.equals(value))
					{
						value = value.replaceAll("\\" + Search.semicolon, ";").replaceAll("\\" + Search.comma, ",");

						ClassField classField = null;

						try
						{
							classField = className == null ? null : emm.getFieldByName(className, critationArray[1], true);
						}
						catch (ServiceRequestException ignored)
						{
						}

						if (classField == null)
						{
							objectValue = value;
						}
						else
						{
							if (SystemClassFieldEnum.STATUS.getName().equalsIgnoreCase(classField.getName()))
							{
								objectValue = SystemStatusEnum.getStatusEnum(value);
							}
							else
							{

								FieldTypeEnum fieldType = classField.getType();
								try
								{
									if (FieldTypeEnum.OBJECT.equals(fieldType))
									{
										if (!StringUtils.isNullString(value))
										{
											ObjectFieldTypeEnum objectFieldTypeOfField = this.getObjectFieldTypeOfField(classField);
											if (objectFieldTypeOfField == ObjectFieldTypeEnum.OBJECT)
											{
												objectValue = this.getObjectGuidByString(value);
											}
											else
											{
												objectValue = value;
											}
										}
									}
									if (FieldTypeEnum.CLASSIFICATION.equals(fieldType) || FieldTypeEnum.CODE.equals(fieldType) || FieldTypeEnum.CODEREF.equals(fieldType)
											|| FieldTypeEnum.STRING.equals(fieldType)

											|| FieldTypeEnum.MULTICODE.equals(fieldType))
									{
										objectValue = value;
									}
									else if (FieldTypeEnum.DATE.equals(fieldType) || FieldTypeEnum.DATETIME.equals(fieldType))
									{
										try
										{
											objectValue = DateFormat.getSDFYMDHMS().parse(value);
										}
										catch (Exception e)
										{
											objectValue = DateFormat.parse(value);
										}

									}
									else if (FieldTypeEnum.FLOAT.equals(fieldType) || FieldTypeEnum.INTEGER.equals(fieldType))
									{
										objectValue = new BigDecimal(value);
									}
								}
								catch (Exception ignored)
								{
								}

							}
						}
					}
					if (Criterion.GROUP_START.equals(critationArray[1]))
					{
						sc.startGroup();
					}
					else if (Criterion.GROUP_END.equals(critationArray[1]))
					{
						sc.endGroup();
					}
					else
					{
						if (Criterion.CON_AND.equals(critationArray[0]))
						{
							sc.addFilter(fieldOrignType, critationArray[1], objectValue, OperateSignEnum.valueOf(critationArray[2]));
						}
						else
						{
							sc.addFilterWithOR(fieldOrignType, critationArray[1], objectValue, OperateSignEnum.valueOf(critationArray[2]));
						}
					}

				}
			}

			search.setSearchCondition(sc);

		}

		return search;
	}

	private ObjectFieldTypeEnum getObjectFieldTypeOfField(ClassField field) throws ServiceRequestException
	{
		if (field.getType() == FieldTypeEnum.OBJECT)
		{
			ClassInfo clasInfo = this.stubService.getEMM().getClassByName(field.getTypeValue());
			if (clasInfo == null)
			{
				throw new DynaDataExceptionAll("query error ,the object type filed has not type value. fieldName is " + field.getName(), null,
						DataExceptionEnum.DS_MODEL_OBJECT_TYPEVALUE_ERROR, field.getName());
			}
			if (clasInfo.hasInterface(ModelInterfaceEnum.IUser))
			{
				return ObjectFieldTypeEnum.USER;
			}
			else if (clasInfo.hasInterface(ModelInterfaceEnum.IGroup))
			{
				return ObjectFieldTypeEnum.GROUP;
			}
			else if (clasInfo.hasInterface(ModelInterfaceEnum.IPMRole))
			{
				return ObjectFieldTypeEnum.PMROLE;
			}
			else if (clasInfo.hasInterface(ModelInterfaceEnum.IPMCalendar))
			{
				return ObjectFieldTypeEnum.PMCALENDAR;
			}
			return ObjectFieldTypeEnum.OBJECT;
		}
		return null;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private ObjectGuid getObjectGuidByString(String value)
	{
		ObjectGuid objectGuid = new ObjectGuid();
		String[] objectv = StringUtils.splitStringWithDelimiter("$", value);
		if (objectv != null && objectv.length > 0)
		{
			for (int i = 0; i < objectv.length; i++)
			{
				if (objectv[i] != null)
				{
					switch (i)
					{
					case 0:
						objectGuid.setClassGuid(objectv[i]);
						break;
					case 1:
						objectGuid.setClassName(objectv[i]);
						break;
					case 2:
						objectGuid.setGuid(objectv[i]);
						break;
					case 3:
						objectGuid.setMasterGuid(objectv[i]);
						break;
					case 4:
						objectGuid.setIsMaster(BooleanUtils.getBooleanByValue(objectv[i]));
						break;
					case 5:
						objectGuid.setBizObjectGuid(objectv[i]);
						break;
					case 6:
						objectGuid.setCommitFolderGuid(objectv[i]);
						break;
					default:
						break;
					}
				}
			}
		}
		return objectGuid;
	}

	public Search saveSearch(Search search, String classGuid, String clasfGuid, String folderGuid, SearchCondition sc) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		// String sessionId = this.stubService.getSignature().getCredential();
		String firstCondition = null;
		String secondCondition = null;
		String name = "";
		String searchGuid = search.getGuid();
		String operatorGuid = this.stubService.getOperatorGuid();
		boolean isCreate = false;
		try
		{
			List<Criterion> criterionList = sc.getCriterionList();
			if (SetUtils.isNullList(criterionList))
			{
				throw new ServiceRequestException("ID_SEARCH_CONDITION_ISNULL", "condition is empty");
			}

			StringBuffer condition = new StringBuffer();

			// 拼凑检索条件到firstCondition中
			for (Criterion criterion : criterionList)
			{
				Object value = criterion.getValue();

				if (value instanceof Date)
				{
					value = DateFormat.formatYMDHMS((Date) value);
				}

				if (value != null && !"".equals(value))
				{
					value = value.toString().replace(",", Search.comma).replace(";", Search.semicolon);
				}

				condition.append(criterion.getConjunction());
				condition.append(",");
				condition.append(criterion.getKey());
				condition.append(",");
				condition.append(criterion.getOperateSignEnum());
				condition.append(",");
				condition.append(StringUtils.convertNULLtoString(value));
				condition.append(",");
				condition.append(StringUtils.convertNULLtoString(criterion.getFieldOrignType()));
				condition.append(";");

			}

			firstCondition = condition.toString();

			// 如果大于1024Byte 则把大于1024byte的字符串放到secondCondition中
			if (condition.length() > Search.maxByte)
			{
				secondCondition = condition.substring(Search.maxByte);

				firstCondition = condition.substring(0, Search.maxByte);
			}

			operatorGuid = this.stubService.getOperatorGuid();
			search.setUpdateUserGuid(operatorGuid);
			search.setOwnerUser(operatorGuid);

			search.setFirstCondition(firstCondition);
			search.setSecondCondition(secondCondition);

			search.setFolderGuid(folderGuid);
			if (classGuid == null)
			{
				classGuid = sc.getObjectGuid().getClassGuid();
			}
			search.setClassGuid(classGuid);
			// }
			if (clasfGuid == null)
			{
				clasfGuid = sc.getClassification();
			}
			search.setClassification(clasfGuid);
			search.setHasSubFolders(sc.hasSubFolders());
			// search.setIsLatestOnly(sc.isLatestOnly());
			search.setCheckOutOnly(sc.isCheckOutOnly());
			// search.setIsMaster(sc.isMaster());
			if (sc.getSearchRevisionTypeEnum() == null)
			{
				search.setSearchRevisionType(SearchRevisionTypeEnum.ISHISTORYREVISION.getType());
			}
			else
			{
				search.setSearchRevisionType(sc.getSearchRevisionTypeEnum().getType());
			}

			search.setCaseSensitive(sc.caseSensitive());
			search.setOwnerOnly(sc.isOwnerOnly());
			search.setOwnerGroupOnly(sc.isOwnerGroupOnly());
			search.setIsAdvanced(sc.isAdvanced());
			search.setUserProductBOGuid(sc.getUserProductBOGuid());
			search.setParentGuid(sc.getParentGuid());
			search.setAdvancedQueryType(sc.getSearchType());
			// if (sc.getModelInterfaceEnum() != null)
			// {
			// search.setModelInterfaceEnum(sc.getModelInterfaceEnum().toString());
			// }
			search.setBomTemplateId(sc.getBOMTemplateId());
			search.setRelationTemplateId(sc.getRelationTemplateId());

			if (StringUtils.isNullString(sc.getSearchName()))
			{
				// 组装Search的名字
				if (!search.isAdvanced())
				{
					// 简单搜索
					if (criterionList.get(0).getValue() instanceof String)
					{
						name = (String) criterionList.get(0).getValue();
					}
					else
					{
						name = "simple";
					}
				}
				else
				{
					// 高级搜索
					for (Criterion criterion : criterionList)
					{
						Object value = criterion.getValue();
						if (value != null && !"".equals(value) && !SearchCondition.VALUE_NULL.equals(value))
						{
							if (name.length() == 0)
							{
								if (value instanceof String)
								{
									name = (String) value;
								}
								else
								{
									name = "advance";
								}
							}
							else
							{
								name = name + " " + (String) value;
							}
						}
					}
				}

				if (name.length() > 20)
				{
					name = name.substring(0, 20) + "...";
				}
			}
			else
			{
				name = sc.getSearchName();
			}

			if ("NULL".equals(name))
			{
				name = "<NULL>";
			}

			search.setName(name);

			if (!StringUtils.isGuid(searchGuid))
			{
				isCreate = true;
				search.setCreateUserGuid(operatorGuid);
			}
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

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			String ret = sds.save(search);
//			DataServer.getTransactionManager().commitTransaction();
			HistoryDeleteScheduledTask.addWaitProcessUser(this.stubService.getOperatorGuid());
			if (isCreate)
			{
				return this.stubService.getSearch(ret);
			}
			else
			{
				return this.stubService.getSearch(searchGuid);
			}
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

	}

	protected Search saveSearch(String searchGuid, SearchCondition sc) throws ServiceRequestException
	{
		Search search = this.getSearch(searchGuid, false);
		if (search == null)
		{
			throw new ServiceRequestException("ID_APP_NOT_FOUND_SEARCH_HISTORY", "not found search history");
		}
		return this.saveSearch(search, sc.getObjectGuid() == null ? "" : sc.getObjectGuid().getClassGuid(), search.getClassification(), sc.getFolder().getGuid(), sc);
	}

	protected Search saveSearch(String name, String searchGuid) throws ServiceRequestException
	{
		Search search = this.getSearch(searchGuid, false);
		if (name.length() > 20)
		{
			name = name.substring(0, 20) + "...";
		}
		search.setName(name);
		search.setOwnerUser(this.stubService.getOperatorGuid());
		search.setType(SearchTypeEnum.USER);

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.save(search);

			return this.stubService.getSearch(searchGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, search.getName());
		}
	}

	protected Search savePMSearch(String name, PMSearchTypeEnum pmTypeEnum, String searchGuid) throws ServiceRequestException
	{
		Search search = this.getSearch(searchGuid, false);
		if (name.length() > 20)
		{
			name = name.substring(0, 20) + "...";
		}
		search.setName(name);
		search.setOwnerUser(this.stubService.getOperatorGuid());
		search.setType(null);
		search.setPMType(pmTypeEnum);

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.save(search);

			return this.stubService.getSearch(searchGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, search.getName());
		}
	}

	public List<Search> listPMSearch(PMSearchTypeEnum pmTypeEnum, SearchCondition searchCondition) throws ServiceRequestException
	{
		List<Search> retList = new ArrayList<Search>();
		String operatorGuid = this.stubService.getOperatorGuid();
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = null;
		if (searchCondition != null)
		{
			filter = FilterBuilder.buildFilterJustCondition(searchCondition);
		}
		List<Search> searchList = null;
		if (filter == null)
		{
			filter = new HashMap<String, Object>();
		}
		if (pmTypeEnum != null)
		{
			filter.put(Search.PMTYPE, pmTypeEnum.getValue());
		}
		filter.put(Search.OWNER_USER, operatorGuid);

		try
		{
			searchList = sds.query(Search.class, filter);

			if (!SetUtils.isNullList(searchList))
			{
				for (Search search : searchList)
				{
					try
					{

						search = this.organizeSearchObject(search, this.stubService.getEMM());
						if (search != null)
						{
							retList.add(search);
						}
					}
					catch (Exception e)
					{
						this.deleteSearch(search.getGuid());
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return retList;
	}

	protected Search saveSearchCondition(BOInfo boInfo, String folderGuid, SearchCondition sc) throws ServiceRequestException
	{
		String classGuid = null;
		String clasfGuid = null;

		if (boInfo != null)
		{
			classGuid = boInfo.getClassGuid();
			clasfGuid = boInfo.getClassificationGuid();
		}
		Search search = new Search();
		search.setType(SearchTypeEnum.AUTO);
		return this.saveSearch(search, classGuid, clasfGuid, folderGuid, sc);
	}

	protected boolean hasUpdateAuthorityForPublicSearch(String searchGuid) throws ServiceRequestException
	{
		boolean hasAuthorityForPublicSearch = true;
		if (Constants.isSupervisor(true, this.stubService))
		{
			AclService aclService = DataServer.getAclService();
			hasAuthorityForPublicSearch = aclService.hasAuthorityForPublicSearch(searchGuid, PublicSearchAuthorityEnum.UPDATE, this.stubService.getSignature().getCredential());
		}
		return hasAuthorityForPublicSearch;
	}

	protected Search savePublicSearch(String name, String searchGuid) throws ServiceRequestException
	{
		// if (!Group.GROUP_ID_ADMINISTRATOR.equalsIgnoreCase(this.stubService.getUserSignature().getLoginGroupId()))
		// {
		// throw new ServiceRequestException("ID_APP_NOT_SAVE_PUBLIC_SEARCH",
		// "only administrator group can save public search");
		// }

		boolean hasAuthorityForPublicSearch = true;
		if (Constants.isSupervisor(true, this.stubService))
		{
			AclService aclService = DataServer.getAclService();
			hasAuthorityForPublicSearch = aclService.hasAuthorityForPublicSearch(searchGuid, PublicSearchAuthorityEnum.UPDATE, this.stubService.getSignature().getCredential());

		}

		if (!hasAuthorityForPublicSearch)
		{
			throw new ServiceRequestException("ID_APP_NO_SAVE_PUBLIC_SEARCH_ACL", "has not ACL for save public search");
		}

		Search search = this.getSearch(searchGuid, false);
		if (name.length() > 20)
		{
			name = name.substring(0, 20) + "...";
		}
		search.setName(name);
		search.setOwnerUser(this.stubService.getOperatorGuid());
		search.setType(SearchTypeEnum.PUBLIC);

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.save(search);

			return this.stubService.getSearch(searchGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, search.getName());
		}
	}

	public List<SearchDetail> saveSearchDetailList(List<SearchDetail> searchDetailList, String searchGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		if (SetUtils.isNullList(searchDetailList))
		{
			return null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			this.deleteSearchDetail(searchDetailList.get(0).getSearchConditionFK());

			for (SearchDetail searchDetail : searchDetailList)
			{
				searchDetail.setUpdateUserGuid(this.stubService.getOperatorGuid());
				searchDetail.setCreateUserGuid(this.stubService.getOperatorGuid());
				searchDetail.setSearchConditionFK(searchGuid);
				sds.save(searchDetail);
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SearchDetail.SEARCHCONDITION_FK, searchGuid);
			List<SearchDetail> query = sds.query(SearchDetail.class, filter);
//			DataServer.getTransactionManager().commitTransaction();
			return query;
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

	}

	protected void deleteSearchDetail(String searchGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();

			filter.put(SearchDetail.SEARCHCONDITION_FK, searchGuid);

			sds.delete(SearchDetail.class, filter, "deleteAdvanced");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected boolean isPMSearchNameExist(String name, PMSearchTypeEnum pmTypeEnum) throws ServiceRequestException
	{
		DSCommonService ds = DataServer.getDSCommonService();

		try
		{

			return ds.isSearchNameUnique(name, false, pmTypeEnum, this.stubService.getOperatorGuid());

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected boolean isSearchNameExist(String name, SearchTypeEnum typeEnum) throws ServiceRequestException
	{
		DSCommonService ds = DataServer.getDSCommonService();

		try
		{

			return ds.isSearchNameUnique(name, typeEnum == SearchTypeEnum.PUBLIC, null, this.stubService.getOperatorGuid());

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected List<Search> listFilter4BO(SearchCondition searchCondition, String boGuid) throws ServiceRequestException
	{
		List<Search> list = new ArrayList<Search>();
		if (searchCondition == null)
		{
			searchCondition = SearchConditionFactory.createSearchCondition(null, null, false);

		}

		if (StringUtils.isGuid(boGuid))
		{
			BOInfo boInfo = this.stubService.getEMM().getCurrentBizObjectByGuid(boGuid);
			if (boInfo != null)
			{
				searchCondition.addFilter(Search.CLASS_GUID, boInfo.getClassGuid(), OperateSignEnum.EQUALS);
			}
		}
		else
		{
			searchCondition.addFilter(Search.CLASS_GUID, null, OperateSignEnum.EQUALS);
		}

		searchCondition.addFilter("QUERYTYPE", AdvancedQueryTypeEnum.NORMAL.getType(), OperateSignEnum.EQUALS);

		List<Search> listPublicSearch = this.listPublicSearch(searchCondition, false);
		list.addAll(listPublicSearch);

		List<Search> listSearch = this.listSearch(searchCondition);

		list.addAll(listSearch);

		return list;
	}

	protected List<Search> listFilter4Classificaiton(SearchCondition searchCondition, String classificationItemGuid) throws ServiceRequestException
	{
		List<Search> list = new ArrayList<Search>();
		if (searchCondition == null)
		{
			searchCondition = SearchConditionFactory.createSearchCondition(null, null, false);

		}

		if (StringUtils.isGuid(classificationItemGuid))
		{
			searchCondition.addFilter(Search.CLASSIFICATION, classificationItemGuid, OperateSignEnum.EQUALS);
		}
		else
		{
			searchCondition.addFilter(Search.CLASSIFICATION, null, OperateSignEnum.EQUALS);
		}
		searchCondition.addFilter("QUERYTYPE", AdvancedQueryTypeEnum.CLASSIFICATION.getType(), OperateSignEnum.EQUALS);

		List<Search> listPublicSearch = this.listPublicSearch(searchCondition, false);
		list.addAll(listPublicSearch);

		List<Search> listSearch = this.listSearch(searchCondition);

		list.addAll(listSearch);

		return list;
	}
}
