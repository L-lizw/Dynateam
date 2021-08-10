/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PreferenceStub
 * Wanglei 2011-3-30
 */
package dyna.app.service.brs.pos;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.edap.EDAPImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.Preference;
import dyna.common.dto.PreferenceDetail;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.WorkFlowPerference;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FilePreferenceEnum;
import dyna.common.systemenum.PreferenceTypeEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.NumberUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;

/**
 * @author Wanglei
 */
public class PreferenceStub extends AbstractServiceStub<POSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public PreferenceStub(ServiceContext context, POSImpl service)
	{
		super(context, service);
	}

	protected Preference getPreference(PreferenceTypeEnum preferenceTypeEnum, String operatorGuid) throws ServiceRequestException
	{
		Preference preference = null;

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(Preference.TYPE, preferenceTypeEnum.getValue());
		filter.put(Preference.USER_GUID, operatorGuid);

		List<PreferenceDetail> preferenceDetailList = null;
		try
		{
			preference = sds.queryObject(Preference.class, filter);

			if (preference == null)
			{
				return null;
			}

			filter.clear();

			if (PreferenceTypeEnum.COMMONCLASS.equals(preferenceTypeEnum))
			{
				String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
				filter.put(PreferenceDetail.BMGUID, bmGuid);
			}

			filter.put(PreferenceDetail.MASTERFK, preference.getGuid());

			preferenceDetailList = sds.query(PreferenceDetail.class, filter);

			preference.setPreferenceDetailList(preferenceDetailList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return preference;
	}

	protected Preference getPreference(PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException
	{
		return this.getPreference(preferenceTypeEnum, this.stubService.getUserSignature().getUserGuid());
	}

	protected Preference getPreference(String preferenceGuid) throws ServiceRequestException
	{
		Preference preference = null;

		List<PreferenceDetail> preferenceDetailList = null;

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(Preference.GUID, preferenceGuid);

		try
		{
			preference = sds.queryObject(Preference.class, filter);

			filter.clear();
			filter.put(PreferenceDetail.MASTERFK, preferenceGuid);

			preferenceDetailList = sds.query(PreferenceDetail.class, filter);

			preference.setPreferenceDetailList(preferenceDetailList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return preference;
	}

	protected List<Preference> listPreference() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		String operatorGuid = this.stubService.getOperatorGuid();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(Preference.USER_GUID, operatorGuid);

		List<Preference> preferenceList = null;

		try
		{
			preferenceList = sds.query(Preference.class, filter);

			if (!SetUtils.isNullList(preferenceList))
			{
				List<PreferenceDetail> preferenceDetailList = null;

				for (Preference preference : preferenceList)
				{
					filter.clear();
					filter.put(PreferenceDetail.MASTERFK, preference.getGuid());

					preferenceDetailList = sds.query(PreferenceDetail.class, filter);

					preference.setPreferenceDetailList(preferenceDetailList);
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
		finally
		{
		}

		return preferenceList;
	}

	protected Preference savePreference(Preference preference) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();

		Preference retPreference = null;

		String operatorGuid = this.stubService.getOperatorGuid();
		String preferenceGuid = null;

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			preferenceGuid = preference.getGuid();
			preference.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
			preference.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			preference.setUserGuid(operatorGuid);

			String ret = sds.save(preference);

			if (!StringUtils.isGuid(preferenceGuid))
			{
				preferenceGuid = ret;
			}

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(PreferenceDetail.MASTERFK, preferenceGuid);
			if (preference.getType().equals(PreferenceTypeEnum.COMMONCLASS.getValue()))
			{
				filter.put(PreferenceDetail.BMGUID, bmGuid);
			}
			sds.delete(PreferenceDetail.class, filter, "deleteWithBmguid");

			// 保存preferenceDetailList
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail preferenceDetail : preferenceDetailList)
				{
					preferenceDetail.setGuid(null);
					preferenceDetail.setMasterFK(preferenceGuid);

					sds.save(preferenceDetail);
				}
			}

			retPreference = this.getPreference(preferenceGuid);

//			DataServer.getTransactionManager().commitTransaction();

		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByDynaDataException(e, preference.getType());
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
		finally
		{
		}

		return retPreference;
	}

	protected void deletePreference(String preferenceGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.delete(Preference.class, preferenceGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deletePreferenceByType(PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		String operatorGuid = this.stubService.getOperatorGuid();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(Preference.TYPE, preferenceTypeEnum.getValue());
		filter.put(Preference.USER_GUID, operatorGuid);

		try
		{
			sds.delete(Preference.class, filter, "deleteByType");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void saveBIViewHiscount(int biviewHisCount) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.BIVIEWHISCOUNT);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(String.valueOf(biviewHisCount));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.BIVIEWHISCOUNT.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
		HistoryDeleteScheduledTask.addWaitProcessUser(this.stubService.getOperatorGuid());
	}

	protected void saveCommonBO(List<BOInfo> boInfoList) throws ServiceRequestException
	{
		Preference preference = null;

		List<PreferenceDetail> preferenceDetailList = null;
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

		if (!SetUtils.isNullList(boInfoList))
		{
			preferenceDetailList = new ArrayList<PreferenceDetail>();
			for (int i = 0; i < boInfoList.size(); i++)
			{
				PreferenceDetail preferenceDetail = new PreferenceDetail();
				preferenceDetail.setSequence(i);
				if (StringUtils.isNullString(boInfoList.get(i).getClassificationGuid()))
				{
					preferenceDetail.setValue(boInfoList.get(i).getGuid());
				}
				else
				{
					preferenceDetail.setValue(boInfoList.get(i).getGuid() + "|" + boInfoList.get(i).getClassificationGuid());
				}
				preferenceDetail.setBMGuid(bmGuid);
				preferenceDetailList.add(preferenceDetail);
			}
		}

		preference = this.getPreference(PreferenceTypeEnum.COMMONCLASS);

		if (preference != null)
		{
			preference.setPreferenceDetailList(preferenceDetailList);
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.COMMONCLASS.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(preferenceDetailList);
		}
		this.savePreference(preference);
	}

	public void addCommonLIB(SystemObject systemObject) throws ServiceRequestException
	{
		Preference preference = null;

		List<PreferenceDetail> preferenceDetailList = new ArrayList<PreferenceDetail>();

		preference = this.getPreference(PreferenceTypeEnum.COMMONLIB);
		if (preference != null)
		{
			preferenceDetailList = preference.getPreferenceDetailList();
			boolean isHave = false;
			for (PreferenceDetail preferenceDetail : preferenceDetailList)
			{
				if (preferenceDetail.getValue().equals(systemObject.getGuid()))
				{
					isHave = true;
					break;
				}
			}
			if (!isHave)
			{
				PreferenceDetail preferenceDetail = new PreferenceDetail();
				preferenceDetail.setSequence(preferenceDetailList.size());
				preferenceDetail.setValue(systemObject.getGuid());
				preferenceDetailList.add(preferenceDetail);
			}
			preference.setPreferenceDetailList(preferenceDetailList);
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.COMMONLIB.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());

			PreferenceDetail preferenceDetail = new PreferenceDetail();
			preferenceDetail.setSequence(preferenceDetailList.size());
			preferenceDetail.setValue(systemObject.getGuid());
			preferenceDetailList.add(preferenceDetail);

			preference.setPreferenceDetailList(preferenceDetailList);
		}

		this.savePreference(preference);
	}

	protected void saveCommonLIB(List<SystemObject> systemObjectList) throws ServiceRequestException
	{
		Preference preference = null;

		if (SetUtils.isNullList(systemObjectList))
		{
			this.deletePreferenceByType(PreferenceTypeEnum.COMMONLIB);
			return;
		}

		List<PreferenceDetail> preferenceDetailList = new ArrayList<PreferenceDetail>();

		for (int i = 0; i < systemObjectList.size(); i++)
		{
			PreferenceDetail preferenceDetail = new PreferenceDetail();
			preferenceDetail.setSequence(i);
			preferenceDetail.setValue(systemObjectList.get(i).getGuid());
			preferenceDetailList.add(preferenceDetail);
		}

		preference = this.getPreference(PreferenceTypeEnum.COMMONLIB);
		if (preference != null)
		{
			preference.setPreferenceDetailList(preferenceDetailList);
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.COMMONLIB.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(preferenceDetailList);
		}

		this.savePreference(preference);
	}

	protected void saveMaxHistory(int maxHistory) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.MAXHISTORY);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(String.valueOf(maxHistory));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.MAXHISTORY.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
		HistoryDeleteScheduledTask.addWaitProcessUser(this.stubService.getOperatorGuid());
	}

	protected void saveLoginWarning(boolean isShow) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.SHOWLOGINWARN);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(BooleanUtils.getBooleanString10(isShow));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.SHOWLOGINWARN.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	protected void saveReceiveMail(boolean isReceive) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.RECEIVEEMAIL);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(BooleanUtils.getBooleanString10(isReceive));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.RECEIVEEMAIL.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	protected void saveNoticeRefreshInterval(int interval) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.NOTICEREFRESHINTERVAL);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(String.valueOf(interval));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.NOTICEREFRESHINTERVAL.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	protected void saveRowCount(int rowCount) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.ROWCOUNT);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(String.valueOf(rowCount));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.ROWCOUNT.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	protected int getBIViewHiscount() throws ServiceRequestException
	{
		Preference preference = null;
		int resurt = 10;
		preference = this.getPreference(PreferenceTypeEnum.BIVIEWHISCOUNT);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				resurt = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}

		return resurt;
	}

	protected boolean isShowLoginWarning() throws ServiceRequestException
	{
		Preference preference = null;
		boolean isShow = true;
		preference = this.getPreference(PreferenceTypeEnum.SHOWLOGINWARN);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				isShow = BooleanUtils.getBooleanBy10(preferenceDetailList.get(0).getValue());
			}
		}

		return isShow;
	}

	protected int getMaxHistory() throws ServiceRequestException
	{
		Preference preference = null;
		int resurt = 10;
		preference = this.getPreference(PreferenceTypeEnum.MAXHISTORY);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				resurt = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}

		return resurt;
	}

	protected int getNoticeRefreshInterval() throws ServiceRequestException
	{
		Preference preference = null;
		int resurt = 10;
		preference = this.getPreference(PreferenceTypeEnum.NOTICEREFRESHINTERVAL);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				resurt = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}

		return resurt;
	}

	protected boolean isReceiveEmail(String userGuid) throws ServiceRequestException
	{
		Preference preference = null;
		boolean receive = true;
		preference = this.getPreference(PreferenceTypeEnum.RECEIVEEMAIL, userGuid);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				receive = BooleanUtils.getBooleanBy10(preferenceDetailList.get(0).getValue());
			}
		}

		return receive;
	}

	protected int getRowCount() throws ServiceRequestException
	{
		Preference preference = null;
		int resurt = 10;
		preference = this.getPreference(PreferenceTypeEnum.ROWCOUNT);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				resurt = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}

		return resurt;
	}

	protected List<BOInfo> listCommonBO() throws ServiceRequestException
	{
		Preference preference = null;
		List<BOInfo> boInfoList = null;
		preference = this.getPreference(PreferenceTypeEnum.COMMONCLASS);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				boInfoList = new ArrayList<BOInfo>();
				for (PreferenceDetail preferenceDetail : preferenceDetailList)
				{
					String guidStr[] = preferenceDetail.getValue().split("\\|");

					BOInfo boInfo = this.stubService.getEMM().getCurrentBizObjectByGuid(guidStr[0]);
					if (boInfo != null)
					{
						if (guidStr.length > 1)
						{
							boInfo.setClassificationGuid(guidStr[1]);
						}
						boInfoList.add(boInfo);
					}
				}
			}
		}

		return boInfoList;
	}

	protected List<SystemObject> listCommonLIB() throws ServiceRequestException
	{
		Preference preference = null;
		List<SystemObject> systemObjectList = null;
		preference = this.getPreference(PreferenceTypeEnum.COMMONLIB);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				systemObjectList = new ArrayList<SystemObject>();
				for (PreferenceDetail preferenceDetail : preferenceDetailList)
				{
					SystemObject systemObject = null;
					try
					{
						systemObject = ((EDAPImpl) this.stubService.getEDAP()).getFolderStub().getFolder(preferenceDetail.getValue(), false);
					}
					catch (ServiceRequestException e)
					{
						continue;
					}

					if (systemObject != null)
					{
						systemObjectList.add(systemObject);
					}
				}
			}
		}
		return systemObjectList;
	}

	protected FilePreferenceEnum getFilePreference() throws ServiceRequestException
	{
		Preference preference = null;
		FilePreferenceEnum filePreferenceEnum = FilePreferenceEnum.NOT_OPEN;
		preference = this.getPreference(PreferenceTypeEnum.FILEPREFERENCE);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				filePreferenceEnum = FilePreferenceEnum.getByValue(preferenceDetailList.get(0).getValue());
			}
		}

		return filePreferenceEnum;
	}

	protected void saveFilePreference(FilePreferenceEnum filePreferenceEnum) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.FILEPREFERENCE);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(filePreferenceEnum.getValue());
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.FILEPREFERENCE.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	/**
	 * sequence=0: 意见属性
	 * sequence=1: 附件属性
	 * 默认全部展开
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	protected WorkFlowPerference getWorkPreference() throws ServiceRequestException
	{
		Preference preference = null;
		WorkFlowPerference workFlowPerference = new WorkFlowPerference();
		preference = this.getPreference(PreferenceTypeEnum.WOREFLOWPREFERENCE);
		if (preference != null)
		{

			for (PreferenceDetail detail : preference.getPreferenceDetailList())
			{
				if (detail.getSequence() == 0)
				{
					workFlowPerference.setTrackOpen(BooleanUtils.getBooleanByYN(detail.getValue()));
				}
				if (detail.getSequence() == 1)
				{
					workFlowPerference.setAttachOpen(BooleanUtils.getBooleanByYN(detail.getValue()));
				}
				if (detail.getSequence() == 2)
				{
					workFlowPerference.setActivityOpen(BooleanUtils.getBooleanByYN(detail.getValue()));
				}
			}
		}

		return workFlowPerference;
	}

	protected void saveWorkPreference(WorkFlowPerference workFlowPerference) throws ServiceRequestException
	{
		Preference preference = null;

		preference = this.getPreference(PreferenceTypeEnum.WOREFLOWPREFERENCE);
		List<PreferenceDetail> preferenceDetailList = new ArrayList<PreferenceDetail>();
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(BooleanUtils.getBooleanStringYN(workFlowPerference.isTrackOpen()));
		preferenceDetailList.add(preferenceDetail);

		preferenceDetail = new PreferenceDetail();
		preferenceDetail.setSequence(1);
		preferenceDetail.setValue(BooleanUtils.getBooleanStringYN(workFlowPerference.isAttachOpen()));
		preferenceDetailList.add(preferenceDetail);

		preferenceDetail = new PreferenceDetail();
		preferenceDetail.setSequence(2);
		preferenceDetail.setValue(BooleanUtils.getBooleanStringYN(workFlowPerference.isActivityOpen()));
		preferenceDetailList.add(preferenceDetail);

		if (preference == null)
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.WOREFLOWPREFERENCE.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
		}

		preference.setPreferenceDetailList(preferenceDetailList);
		this.savePreference(preference);
	}

	protected boolean getCodingPreference() throws ServiceRequestException
	{
		Preference preference = null;
		boolean isRecord = true;
		preference = this.getPreference(PreferenceTypeEnum.CODERULE);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				isRecord = BooleanUtils.getBooleanBy10(preferenceDetailList.get(0).getValue());
			}
		}

		return isRecord;
	}

	protected void saveCodingPreference(boolean isRecord) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.CODERULE);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(BooleanUtils.getBooleanString10(isRecord));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.CODERULE.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	/**
	 * 布局方式
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean isLeftRightLayout() throws ServiceRequestException
	{
		Preference preference = null;
		boolean result = true;
		preference = this.getPreference(PreferenceTypeEnum.LEFTRIGHTLAYOUT);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = BooleanUtils.getBooleanBy10(preferenceDetailList.get(0).getValue());
			}
		}

		return result;
	}

	/**
	 * 保存布局方式
	 *
	 * @param isLeftRightLayout
	 * @throws ServiceRequestException
	 */
	protected void saveLeftRightLayout(boolean isLeftRightLayout) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.LEFTRIGHTLAYOUT);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(BooleanUtils.getBooleanString10(isLeftRightLayout));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.LEFTRIGHTLAYOUT.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	/**
	 * 取得左边比例值
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	protected int getLeftProportionValue() throws ServiceRequestException
	{
		Preference preference = null;
		int result = 18;
		preference = this.getPreference(PreferenceTypeEnum.LEFTPROPORTION);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = NumberUtils.getIneger(preferenceDetailList.get(0).getValue());
			}
		}

		return result;
	}
	/**
	 * 取得左边比例值
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	protected String getPreferenceByType(PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException
	{
		Preference preference = null;
		String result = "";
		preference = this.getPreference(preferenceTypeEnum);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = preferenceDetailList.get(0).getValue();
			}
		}
		
		return result;
	}

	/**
	 * @param isLeftRightLayout
	 * @throws ServiceRequestException
	 */
	protected void savePreferenceByType(String value,PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(preferenceTypeEnum);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(value + "");
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(preferenceTypeEnum.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}
	/**
	 * @param isLeftRightLayout
	 * @throws ServiceRequestException
	 */
	protected void saveLeftProportionValue(int leftProportionValue) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.LEFTPROPORTION);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(leftProportionValue + "");
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.LEFTPROPORTION.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	/**
	 * 保存消息清理周期
	 *
	 * @param interval
	 * @throws ServiceRequestException
	 */
	protected void saveMessageClearCycle(int interval) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.MESSAGECLEARCYCLE);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(String.valueOf(interval));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.MESSAGECLEARCYCLE.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	/**
	 * 取得消息清理周期
	 *
	 * @return
	 * @throws ServiceRequestException
	 */
	protected int getMessageClearCycle() throws ServiceRequestException
	{
		Preference preference = null;
		int result = 45;
		preference = this.getPreference(PreferenceTypeEnum.MESSAGECLEARCYCLE);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}

		return result;
	}

	/**
	 * @param quickConfigNameList
	 * @throws ServiceRequestException
	 */
	public void savequickConfig(List<String> quickConfigNameList) throws ServiceRequestException
	{
		Preference preference = null;

		List<PreferenceDetail> preferenceDetailList = null;

		if (!SetUtils.isNullList(quickConfigNameList))
		{
			preferenceDetailList = new ArrayList<PreferenceDetail>();
			for (int i = 0; i < quickConfigNameList.size(); i++)
			{
				PreferenceDetail preferenceDetail = new PreferenceDetail();
				preferenceDetail.setSequence(i);
				preferenceDetail.setValue(quickConfigNameList.get(i));
				preferenceDetailList.add(preferenceDetail);
			}
		}

		preference = this.getPreference(PreferenceTypeEnum.CUSTOMCONFIG);

		if (preference != null)
		{
			preference.setPreferenceDetailList(preferenceDetailList);
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.CUSTOMCONFIG.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(preferenceDetailList);
		}
		this.savePreference(preference);
	}

	/**
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listQuickConfig() throws ServiceRequestException
	{
		Preference preference = null;
		List<String> resultValue = new ArrayList<String>();
		preference = this.getPreference(PreferenceTypeEnum.CUSTOMCONFIG);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					resultValue.add(detail.getValue());
				}
			}
		}
		else
		{
			// “我的主页”自定义项默认选中“通知”，“我的任务”，“流程工作”，“我的快捷”
			resultValue.add("MESSAGE");// 消息
			resultValue.add("QUICKCONFIG");// 我的快捷
			resultValue.add("MYWORKFLOW");// 我的流程
			resultValue.add("MYTASK");// 我的任务
		}

		return resultValue;
	}

	/**
	 * @return
	 * @throws ServiceRequestException
	 */
	protected int getWorkFlowClearCycle() throws ServiceRequestException
	{
		Preference preference = null;
		int result = 45;
		preference = this.getPreference(PreferenceTypeEnum.WORKFLOWCLEARCYCLE);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}

		return result;
	}

	/**
	 * @param interval
	 * @throws ServiceRequestException
	 */
	protected void saveWorkFlowClearCycle(int interval) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.WORKFLOWCLEARCYCLE);
		preferenceDetail.setSequence(0);
		preferenceDetail.setValue(String.valueOf(interval));
		if (preference != null)
		{
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.WORKFLOWCLEARCYCLE.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);
	}

	/**
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public int getMessageClearCycle(String userGuid) throws ServiceRequestException
	{
		Preference preference = null;
		int result = 45;
		preference = this.getPreference(PreferenceTypeEnum.MESSAGECLEARCYCLE, userGuid);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}
		return result;
	}

	/**
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public int getWorkFlowClearCycle(String userGuid) throws ServiceRequestException
	{
		Preference preference = null;
		int result = 45;
		preference = this.getPreference(PreferenceTypeEnum.WORKFLOWCLEARCYCLE, userGuid);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = Integer.parseInt(preferenceDetailList.get(0).getValue());
			}
		}
		return result;
	}

	private void setQuerySetting(int i, String value) throws ServiceRequestException
	{
		Preference preference = null;
		PreferenceDetail preferenceDetail = new PreferenceDetail();
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		preferenceDetail.setSequence(i);
		preferenceDetail.setValue(value);
		if (preference != null)
		{
			if (!SetUtils.isNullList(preference.getPreferenceDetailList()))
			{
				boolean hasDetail = false;
				for (PreferenceDetail detail : preference.getPreferenceDetailList())
				{
					if (detail.getSequence() != null && detail.getSequence() == i)
					{
						detail.setValue(value);
						hasDetail = true;
					}
				}
				if (hasDetail == false)
				{
					preference.getPreferenceDetailList().add(preferenceDetail);
				}
			}
			else
			{
				preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
			}
		}
		else
		{
			preference = new Preference();
			preference.setType(PreferenceTypeEnum.QUERYSETTING.getValue());
			preference.setUserGuid(this.stubService.getOperatorGuid());
			preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
		}
		this.savePreference(preference);

	}

	public void setQueryIDFuzzy(boolean value) throws ServiceRequestException
	{
		this.setQuerySetting(0, BooleanUtils.getBooleanStringYN(value));
	}

	public boolean getQueryIDFuzzy() throws ServiceRequestException
	{
		Preference preference = null;
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					if (detail.getSequence() != null && detail.getSequence() == 0)
					{
						return BooleanUtils.getBoolean(BooleanUtils.getBooleanByYN(detail.getValue()), false);
					}
				}
			}
		}
		return false;
	}

	public void setQueryCaseSensitive(boolean value) throws ServiceRequestException
	{
		this.setQuerySetting(1, BooleanUtils.getBooleanStringYN(value));
	}

	public boolean getQueryCaseSensitive() throws ServiceRequestException
	{
		Preference preference = null;
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					if (detail.getSequence() != null && detail.getSequence() == 1)
					{
						return BooleanUtils.getBoolean(BooleanUtils.getBooleanByYN(detail.getValue()), true);
					}
				}
			}
		}
		return true;
	}

	public void setQuerySubFolder(boolean value) throws ServiceRequestException
	{
		this.setQuerySetting(2, BooleanUtils.getBooleanStringYN(value));
	}

	public boolean getQuerySubFolder() throws ServiceRequestException
	{
		Preference preference = null;
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					if (detail.getSequence() != null && detail.getSequence() == 2)
					{
						return BooleanUtils.getBoolean(BooleanUtils.getBooleanByYN(detail.getValue()), false);
					}
				}
			}
		}
		return false;
	}

	public void setHasSimpleQuery(boolean value) throws ServiceRequestException
	{
		this.setQuerySetting(3, BooleanUtils.getBooleanStringYN(value));
	}

	public boolean getHasSimpleQuery() throws ServiceRequestException
	{
		Preference preference = null;
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					if (detail.getSequence() != null && detail.getSequence() == 3)
					{
						return BooleanUtils.getBoolean(BooleanUtils.getBooleanByYN(detail.getValue()), false);
					}
				}
			}
		}
		return false;
	}

	public void setQueryRevisionType(SearchRevisionTypeEnum value) throws ServiceRequestException
	{
		this.setQuerySetting(4, value.getType());
	}

	public SearchRevisionTypeEnum getQueryRevisionType() throws ServiceRequestException
	{
		Preference preference = null;
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					if (detail.getSequence() != null && detail.getSequence() == 4)
					{

						return SearchRevisionTypeEnum.typeValueOf(detail.getValue());
					}
				}
			}
		}
		return SearchRevisionTypeEnum.ISLATESTONLY;
	}

	public void setQueryOwnerConditionType(int value) throws ServiceRequestException
	{
		this.setQuerySetting(5, String.valueOf(value));
	}

	public int getQueryOwnerConditionType() throws ServiceRequestException
	{
		Preference preference = null;
		preference = this.getPreference(PreferenceTypeEnum.QUERYSETTING);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				for (PreferenceDetail detail : preferenceDetailList)
				{
					if (detail.getSequence() != null && detail.getSequence() == 5)
					{

						return Integer.parseInt(detail.getValue());
					}
				}
			}
		}
		return 0;
	}

	protected String defaultOpenEditorInput() throws ServiceRequestException
	{
		Preference preference = null;
		String result = "";
		preference = this.getPreference(PreferenceTypeEnum.OPENONLYONE);
		if (preference != null)
		{
			List<PreferenceDetail> preferenceDetailList = preference.getPreferenceDetailList();
			if (!SetUtils.isNullList(preferenceDetailList))
			{
				result = preferenceDetailList.get(0).getValue();
			}
		}
		return result;
	}

	public void saveDefaultOpenEditorInput(String editorInputId) throws ServiceRequestException
	{
		if (!StringUtils.isNullString(editorInputId))
		{

			Preference preference = null;
			PreferenceDetail preferenceDetail = new PreferenceDetail();
			preference = this.getPreference(PreferenceTypeEnum.OPENONLYONE);
			preferenceDetail.setSequence(0);
			preferenceDetail.setValue(editorInputId);
			if (preference != null)
			{
				preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
			}
			else
			{
				preference = new Preference();
				preference.setType(PreferenceTypeEnum.OPENONLYONE.getValue());
				preference.setUserGuid(this.stubService.getOperatorGuid());
				preference.setPreferenceDetailList(Arrays.asList(preferenceDetail));
			}
			this.savePreference(preference);
		}
	}

}
