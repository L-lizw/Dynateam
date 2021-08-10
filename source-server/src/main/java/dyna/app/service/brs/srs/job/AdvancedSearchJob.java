package dyna.app.service.brs.srs.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.das.jss.JSSImpl;
import dyna.app.service.das.jss.JobExecutor;
import dyna.app.service.das.jss.JobResult;
import dyna.common.Criterion;
import dyna.common.FieldOrignTypeEnum;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.Queue;
import dyna.common.dto.Search;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AdvancedQueryTypeEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.SMS;
import dyna.net.service.brs.SRS;
import dyna.net.service.das.MSRM;
import dyna.net.spi.ServiceProvider;

public class AdvancedSearchJob implements JobExecutor
{
	private SRS	srs	= null;
	private EMM	emm	= null;

	@Override
	public JobResult perform(JSSImpl jss, Queue job) throws Exception
	{
		String[] login = job.getFieldj().split(SRS.JOIN_CHAR);
		String session = ((AASImpl) jss.getAAS()).getLoginStub().login(login[0], login[1], login[2], LanguageEnum.getById(login[3]));
		ServiceProvider serviceProvider = ServiceProviderFactory.getServiceProvider();
		this.srs = serviceProvider.getServiceInstance(SRS.class, session);
		this.emm = serviceProvider.getServiceInstance(EMM.class, session);

		ObjectGuid object = new ObjectGuid();
		Folder folder = new Folder();

		String fieldg = job.getFieldg();
		if (job.getFieldg() != null)
		{
			String[] fieldgs = fieldg.split(SRS.JOIN_CHAR);
			String classguid = fieldgs[0].equals("null") ? null : fieldgs[0];
			String className = fieldgs[1].equals("null") ? null : fieldgs[1];
			String folderGuid = fieldgs[2].equals("null") ? null : fieldgs[2];

			folder.setGuid(folderGuid);

			object.setClassGuid(classguid);
			object.setClassName(className);
		}

		boolean hasSubFolders = false;
		boolean isCheckOutOnly = false;
		boolean isIncludeOBS = false;
		String searchType = AdvancedQueryTypeEnum.NORMAL.name();
		String revisiontype = SearchRevisionTypeEnum.ISHISTORYREVISION.getType();
		if (job.getFieldd() != null)
		{
			String[] fieldd = job.getFieldd().split(SRS.JOIN_CHAR);
			hasSubFolders = "true".equals(fieldd[0]) ? true : false;
			isCheckOutOnly = "true".equals(fieldd[1]) ? true : false;
			isIncludeOBS = "true".equals(fieldd[2]) ? true : false;
			revisiontype = "null".equals(fieldd[3]) || fieldd[3] == null ? SearchRevisionTypeEnum.ISHISTORYREVISION.getType() : fieldd[3];
			searchType = fieldd[4];
		}

		boolean caseSensitive = false;
		boolean isOwnerOnly = false;
		boolean isOwnerGroupOnly = false;
		boolean isAdvanced = false;
		if (job.getFielde() != null)
		{
			String[] fieldes = job.getFielde().split(SRS.JOIN_CHAR);
			caseSensitive = fieldes[0].equals("true") ? true : false;
			isOwnerOnly = fieldes[1].equals("true") ? true : false;
			isOwnerGroupOnly = fieldes[2].equals("true") ? true : false;
			isAdvanced = fieldes[3].equals("true") ? true : false;
		}

		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(object, folder, hasSubFolders);
		searchCondition.setHasSubFolders(hasSubFolders);
		searchCondition.setCheckOutOnly(isCheckOutOnly);
		searchCondition.setIncludeOBS(isIncludeOBS);
		searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.typeValueOf(revisiontype));
		searchCondition.setCaseSensitive(caseSensitive);
		searchCondition.setOwnerOnly(isOwnerOnly);
		searchCondition.setOwnerGroupOnly(isOwnerGroupOnly);
		searchCondition.setIsAdvanced(isAdvanced);
		searchCondition.setSearchType(AdvancedQueryTypeEnum.valueOf(searchType));

		String firstCondition = job.getFieldb().equals("null") ? null : job.getFieldb();
		String secondCondition = (null == job.getFieldc() || job.getFieldc().equals("null")) ? null : job.getFieldc();

		String fieldf = job.getFieldf();
		String[] fieldfs = fieldf.split(SRS.JOIN_CHAR);

		String userProductBOGuid = fieldfs[0];
		String parentGuid = fieldfs[1];

		String BOMTemplateId = fieldfs[3].equals("null") ? null : fieldfs[3];
		String relationTemplateId = fieldfs[4].equals("null") ? null : fieldfs[4];

		searchCondition.setBOMTemplateId(BOMTemplateId);
		searchCondition.setRelationTemplateId(relationTemplateId);
		searchCondition.setUserProductBOGuid(userProductBOGuid);
		searchCondition.setParentGuid(parentGuid);

		if (firstCondition != null)
		{

			if (secondCondition != null)
			{
				firstCondition = firstCondition + secondCondition;
			}

			String[] conditionArray = StringUtils.splitStringWithDelimiter(";", firstCondition);

			for (String critationStr : conditionArray)
			{
				String[] critationArray = StringUtils.splitStringWithDelimiterHavEnd(",", critationStr);

				FieldOrignTypeEnum fieldOrignTypeEnum = FieldOrignTypeEnum.CLASS;
				if (critationArray.length >= 5 && FieldOrignTypeEnum.CLASSIFICATION.name().equals(critationArray[4]))
				{
					fieldOrignTypeEnum = FieldOrignTypeEnum.CLASSIFICATION;
					searchCondition.setSearchType(AdvancedQueryTypeEnum.CLASSIFICATION);
				}

				String value = critationArray[3] == null ? "" : critationArray[3];

				Object objectValue = null;

				if (value != null && !SearchCondition.VALUE_NULL.equals(value))
				{
					value = value.replaceAll("\\" + Search.semicolon, ";").replaceAll("\\" + Search.comma, ",");

					ClassField classField = null;

					if (object.getClassName() != null)
					{
						try
						{
							classField = emm.getFieldByName(object.getClassName(), critationArray[1], true);
						}
						catch (ServiceRequestException e)
						{

						}
					}

					if (classField == null)
					{
						objectValue = value;
					}
					else
					{
						if (SystemClassFieldEnum.STATUS.getName().equals(classField.getName()))
						{
							objectValue = SystemStatusEnum.getStatusEnum(value);
						}
						else
						{

							FieldTypeEnum fieldType = classField.getType();
							try
							{
								if (FieldTypeEnum.CLASSIFICATION.equals(fieldType) || FieldTypeEnum.CODE.equals(fieldType) || FieldTypeEnum.CODEREF.equals(fieldType)
										|| FieldTypeEnum.STRING.equals(fieldType) || FieldTypeEnum.MULTICODE.equals(fieldType))
								{
									objectValue = value;
								}
								else if (FieldTypeEnum.OBJECT.equals(fieldType))
								{
									ClassInfo classInfo_ = null;
									if (classField.getTypeValue() != null)
									{
										classInfo_ = this.emm.getClassByName(classField.getTypeValue());
									}
									if (classInfo_ == null)
									{
										continue;
									}
									if (classInfo_.hasInterface(ModelInterfaceEnum.IUser) || classInfo_.hasInterface(ModelInterfaceEnum.IGroup)
											|| classInfo_.hasInterface(ModelInterfaceEnum.IPMCalendar) || classInfo_.hasInterface(ModelInterfaceEnum.IPMRole))
									{
										objectValue = value;
									}
									else
									{
										objectValue = srs.getObjectGuidByStr(value);
									}
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
							catch (Exception e)
							{

							}

						}
					}
				}
				if (Criterion.GROUP_START.equals(critationArray[1]))
				{
					searchCondition.startGroup();
				}
				else if (Criterion.GROUP_END.equals(critationArray[1]))
				{
					searchCondition.endGroup();
				}
				else
				{
					if (Criterion.CON_AND.equals(critationArray[0]))
					{
						searchCondition.addFilter(fieldOrignTypeEnum, critationArray[1], objectValue, OperateSignEnum.valueOf(critationArray[2]));
					}
					else
					{
						searchCondition.addFilterWithOR(fieldOrignTypeEnum, critationArray[1], objectValue, OperateSignEnum.valueOf(critationArray[2]));
					}
				}

			}
		}
		String uiName = job.getFieldh();

		if (null != uiName)
		{
			List<String> list = new ArrayList<String>();
			list.add(uiName);
			searchCondition.setResultUINameList(list);
		}

		searchCondition.setPageSizeMax(10000);
		try
		{
			this.srs.reportAdvancedSearchObject(searchCondition, job.getGuid());
		}
		finally
		{
			AAS aas = serviceProvider.getServiceInstance(AAS.class, session);
			aas.logout();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.das.jss.JobExecutor#serverPerformFail(dyna.app.service.das.jss.JSSImpl,
	 * dyna.common.bean.data.system.Queue)
	 */
	@Override
	public JobResult serverPerformFail(JSSImpl jss, Queue job) throws Exception
	{
		String[] login = job.getFieldj().split("-");
		String session = ((AASImpl) jss.getAAS()).getLoginStub().login(login[0], login[1], login[2], LanguageEnum.getById(login[3]));

		ServiceProvider serviceProvider = ServiceProviderFactory.getServiceProvider();
		SMS sms = serviceProvider.getServiceInstance(SMS.class, session);
		MSRM msrm = serviceProvider.getServiceInstance(MSRM.class, session);
		LanguageEnum lang = LanguageEnum.getById(login[3]);

		String message = msrm.getMSRString("ID_APP_JSS_JOB_FAIL_SERVER_RESTART", lang.toString());
		String errMessage = msrm.getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());

		try
		{
			sms.sendMail4Report(message, " advance search " + errMessage, MailCategoryEnum.ERROR, login[0], null);
		}
		finally
		{
			AAS aas = serviceProvider.getServiceInstance(AAS.class, session);
			aas.logout();
		}

		return JobResult.failed(message);
	}
}
