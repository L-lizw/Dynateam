package dyna.app.service.brs.ppms.app;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.ppms.PMWarningFieldEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.PMConstans;
import dyna.common.util.StringUtils;

public class MessageDecode
{
	public static String decodeMessage(FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, String oriContent, Map<String, String> cacheMsrm,
			LanguageEnum languageEnum) throws ServiceRequestException
	{

		String newContent = oriContent;
		if (StringUtils.isNullString(oriContent))
		{
			return null;
		}
		int fromIndex = 0;
		int toIndex = 0;
		while (toIndex >= 0 && fromIndex >= 0)
		{
			fromIndex = oriContent.indexOf(PMConstans.CONTENT_FIELD_SYMBO_START, toIndex);
			if (fromIndex >= 0)
			{
				toIndex = oriContent.indexOf(PMConstans.CONTENT_FIELD_SYMBO_END, fromIndex) + 2;

				if (toIndex >= 0)
				{
					String oriString = oriContent.substring(fromIndex, toIndex);
					String replacement = oriString.replace(PMConstans.CONTENT_FIELD_SYMBO_START, "");
					replacement = replacement.replace(PMConstans.CONTENT_FIELD_SYMBO_END, "");
					String[] fieldGroup = StringUtils.splitStringWithDelimiterHavEnd(":", replacement);
					if (fieldGroup.length == 2)
					{
						if ("PROJECT".equalsIgnoreCase(fieldGroup[0]))
						{
							if (projectFoundationObject == null)
							{
								replacement = "";
							}
							else
							{
								replacement = getValue(projectFoundationObject, fieldGroup[1], cacheMsrm, languageEnum);
							}
						}
						else if (fieldGroup[0] == null || "TASK".equalsIgnoreCase(fieldGroup[0]))
						{
							if (taskFoundationObject == null)
							{
								replacement = "";
							}
							else
							{
								replacement = getValue(taskFoundationObject, fieldGroup[1], cacheMsrm, languageEnum);
							}
						}

						newContent = replaceAll(newContent, oriString, replacement);
					}
					else if (fieldGroup.length == 1)
					{
						if (!("PRETASKNAME$".equalsIgnoreCase(fieldGroup[0]) || "PERCENT$".equalsIgnoreCase(fieldGroup[0]) || "RECEIVER$".equalsIgnoreCase(fieldGroup[0]) || "SENDER$"
								.equalsIgnoreCase(fieldGroup[0])))
						{
							replacement = getValue(projectFoundationObject, fieldGroup[0], cacheMsrm, languageEnum);
							newContent = replaceAll(newContent, oriString, replacement);
						}
					}

				}
			}

		}

		if (newContent.contains("\\n"))
		{
			newContent = newContent.replaceAll("\\\\n", "\r\n");
		}
		return newContent;
	}

	private static String replaceAll(String newContent, String oriString, String replacement)
	{
		newContent = newContent.replace(oriString, StringUtils.convertNULLtoString(replacement));
		return newContent;
	}

	private static String getValue(FoundationObject foundationObject, String fieldName, Map<String, String> cacheMsrm, LanguageEnum languageEnum) throws ServiceRequestException
	{
		if (fieldName == null)
		{
			return "";
		}
		if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.PLANSTARTTIME))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
			Date date = util.getPlanStartTime();
			if (date != null)
			{
				return DateFormat.formatYMD(date);
			}
			else
			{
				return null;
			}

		}
		else if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.PLANFINISHTIME))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
			Date date = util.getPlanFinishTime();
			if (date != null)
			{
				return DateFormat.formatYMD(date);
			}
			else
			{
				return null;
			}
		}
		else if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.ACTUALSTARTTIME))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
			Date date = util.getActualStartTime();
			if (date != null)
			{
				return DateFormat.formatYMD(date);
			}
			else
			{
				return null;
			}

		}
		else if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.ACTUALFINISHTIME))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
			Date date = util.getActualFinishTime();
			if (date != null)
			{
				return DateFormat.formatYMD(date);
			}
			else
			{
				return null;
			}
		}
		else if (fieldName.equalsIgnoreCase(PMWarningFieldEnum.SYSTEMTIME.getFieldName()))
		{
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			return DateFormat.formatYMD(c.getTime());
		}
		else if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.EXECUTOR))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
			String executor = util.getExecutor();
			if (StringUtils.isGuid(executor))
			{
				return util.getExecutorName();
			}
			else
			{
				return null;
			}

		}
		else if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.EXECUTESTATUS))
		{
			return StringUtils.getMsrTitle((String) foundationObject.get(PPMFoundationObjectUtil.EXECUTESTATUS + "$TITLE"), languageEnum.getType());
		}
		else if (fieldName.equalsIgnoreCase(SystemClassFieldEnum.STATUS.getName()))
		{
			return cacheMsrm.get(foundationObject.getStatus().getMsrId());
		}
		else if (fieldName.equalsIgnoreCase(PPMFoundationObjectUtil.COMPLETIONRATE))
		{
			return foundationObject.get(PPMFoundationObjectUtil.COMPLETIONRATE) + "";
		}
		return (String) foundationObject.get(fieldName);
	}
}
