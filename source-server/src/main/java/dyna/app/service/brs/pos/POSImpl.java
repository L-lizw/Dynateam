/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: POSImpl Personal Object Service的实现类
 * caogc 2010-8-20
 */
package dyna.app.service.brs.pos;

import dyna.app.core.SessionActiveTime;
import dyna.app.service.BusinessRuleService;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.*;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.WorkFlowPerference;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FilePreferenceEnum;
import dyna.common.systemenum.PMSearchTypeEnum;
import dyna.common.systemenum.PreferenceTypeEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.SearchTypeEnum;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.EDAP;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.POS;

import java.util.Date;
import java.util.List;

/**
 * Personal Object Service implementation 私有对象服务的实现类
 * <String>该服务内的操作都是针对登陆者本人进行的<String>
 * 
 * @author caogc
 * 
 */
public class POSImpl extends BusinessRuleService implements POS
{
	private BIViewHisStub			biViewHisStub		= null;
	private MyScheduleStub			myScheduleStub		= null;
	private MySearchStub			mySearchStub		= null;
	private PreferenceStub			preferenceStub		= null;
	private WorkingItemStub			workingItemStub		= null;

	private MySearchConditionStub	searchConditionStub	= null;

	private MyCustomColumnsStub		customColumnsStub	= null;
	private static boolean		isInit				= false;

	@Override
	protected void init()
	{
		if (isInit)
		{
			return;
		}
		isInit = true;

		this.server.addServerContextListener(new TimeTaskServerContextListener());

	}

	@Override
	public void deleteMySchedule(String guid) throws ServiceRequestException
	{
		this.getMyScheduleStub().deleteMySchedule(guid);
	}

	@Override
	public void deleteSearch(String searchGuid) throws ServiceRequestException
	{
		this.getMySearchStub().deleteSearch(searchGuid);
	}

	protected MySearchConditionStub getMySearchConditionStub()
	{
		if (this.searchConditionStub == null)
		{
			this.searchConditionStub = new MySearchConditionStub(this.serviceContext, this);
		}
		return this.searchConditionStub;
	}

	protected MyCustomColumnsStub getMyCustomColumnsStub()
	{
		if (this.customColumnsStub == null)
		{
			this.customColumnsStub = new MyCustomColumnsStub(this.serviceContext, this);
		}
		return this.customColumnsStub;
	}

	protected BIViewHisStub getBIViewHisStub()
	{
		if (this.biViewHisStub == null)
		{
			this.biViewHisStub = new BIViewHisStub(this.serviceContext, this);
		}
		return this.biViewHisStub;
	}

	protected synchronized EDAP getEDAP() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EDAP.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	@Override
	public MySchedule getMySchedule(String guid) throws ServiceRequestException
	{
		return this.getMyScheduleStub().getMySchedule(guid);
	}

	protected MyScheduleStub getMyScheduleStub()
	{
		if (this.myScheduleStub == null)
		{
			this.myScheduleStub = new MyScheduleStub(this.serviceContext, this);
		}
		return this.myScheduleStub;
	}

	public MySearchStub getMySearchStub()
	{
		if (this.mySearchStub == null)
		{
			this.mySearchStub = new MySearchStub(this.serviceContext, this);
		}
		return this.mySearchStub;
	}

	protected PreferenceStub getPreferenceStub()
	{
		if (this.preferenceStub == null)
		{
			this.preferenceStub = new PreferenceStub(this.serviceContext, this);
		}
		return this.preferenceStub;
	}

	@Override
	public Search getSearch(String searchGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().getSearch(searchGuid, true);
	}

	@Override
	public Search getSearchLatest() throws ServiceRequestException
	{
		return this.getMySearchStub().getSearchLatest();
	}

	protected WorkingItemStub getWorkingItemStub()
	{
		if (this.workingItemStub == null)
		{
			this.workingItemStub = new WorkingItemStub(this.serviceContext, this);
		}
		return this.workingItemStub;
	}

	@Override
	public List<FoundationObject> listBIViewHis(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getBIViewHisStub().listBIViewHis(searchCondition);
	}

	@Override
	public List<MySchedule> listMySchedule(Date scheduleDate) throws ServiceRequestException
	{
		return this.getMyScheduleStub().listMySchedule(scheduleDate);
	}

	@Override
	public List<Preference> listPreference() throws ServiceRequestException
	{
		return this.getPreferenceStub().listPreference();
	}

	@Override
	public List<Search> listSearch(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getMySearchStub().listSearch(searchCondition);
	}

	@Override
	public void saveBIViewHis(BIViewHis biViewHis) throws ServiceRequestException
	{
		this.getBIViewHisStub().saveBIViewHis(biViewHis);
	}

	@Override
	public MySchedule saveMySchedule(MySchedule mySchedule) throws ServiceRequestException
	{
		return this.getMyScheduleStub().saveMySchedule(mySchedule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveSearch(java.lang.String, dyna.common.SearchCondition)
	 */
	@Override
	public Search saveSearch(String searchGuid, SearchCondition sc) throws ServiceRequestException
	{
		return this.getMySearchStub().saveSearch(searchGuid, sc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveSearch(java.lang.String, java.lang.String)
	 */
	@Override
	public Search saveSearch(String name, String searchGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().saveSearch(name, searchGuid);
	}

	@Override
	public Search saveSearchCondition(BOInfo boInfo, String folderGuid, SearchCondition sc) throws ServiceRequestException
	{
		return this.getMySearchStub().saveSearchCondition(boInfo, folderGuid, sc);
	}

	@Override
	public void saveBIViewHiscount(int biviewHisCount) throws ServiceRequestException
	{
		this.getPreferenceStub().saveBIViewHiscount(biviewHisCount);
	}

	@Override
	public void saveCommonBO(List<BOInfo> boInfoList) throws ServiceRequestException
	{
		this.getPreferenceStub().saveCommonBO(boInfoList);
	}

	@Override
	public void saveCommonLIB(List<SystemObject> systemObjectList) throws ServiceRequestException
	{
		this.getPreferenceStub().saveCommonLIB(systemObjectList);
	}

	@Override
	public void saveMaxHistory(int maxHistory) throws ServiceRequestException
	{
		this.getPreferenceStub().saveMaxHistory(maxHistory);
	}

	@Override
	public void saveRowCount(int rowCount) throws ServiceRequestException
	{
		this.getPreferenceStub().saveRowCount(rowCount);
	}

	@Override
	public int getBIViewHiscount() throws ServiceRequestException
	{
		return this.getPreferenceStub().getBIViewHiscount();
	}

	@Override
	public int getMaxHistory() throws ServiceRequestException
	{
		return this.getPreferenceStub().getMaxHistory();
	}

	@Override
	public int getRowCount() throws ServiceRequestException
	{
		return this.getPreferenceStub().getRowCount();
	}

	@Override
	public List<BOInfo> listCommonBO() throws ServiceRequestException
	{
		return this.getPreferenceStub().listCommonBO();
	}

	@Override
	public List<SystemObject> listCommonLIB() throws ServiceRequestException
	{
		return this.getPreferenceStub().listCommonLIB();
	}

	@Override
	public void addCommonLIB(SystemObject systemObject) throws ServiceRequestException
	{
		this.getPreferenceStub().addCommonLIB(systemObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#savePublicSearch(java.lang.String, java.lang.String)
	 */
	@Override
	public Search savePublicSearch(String name, String searchGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().savePublicSearch(name, searchGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#listPublicSearch(dyna.common.SearchCondition)
	 */
	@Override
	public List<Search> listPublicSearch(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getMySearchStub().listPublicSearch(searchCondition, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#getNoticeRefreshInterval()
	 */
	@Override
	@SessionActiveTime(isUpdate = false)
	public int getNoticeRefreshInterval() throws ServiceRequestException
	{
		return this.getPreferenceStub().getNoticeRefreshInterval();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#isReceiveEmail()
	 */
	@Override
	public boolean isReceiveEmail(String userGuid) throws ServiceRequestException
	{
		return this.getPreferenceStub().isReceiveEmail(userGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveNoticeRefreshInterval(int)
	 */
	@Override
	public void saveNoticeRefreshInterval(int interval) throws ServiceRequestException
	{
		this.getPreferenceStub().saveNoticeRefreshInterval(interval);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveReceiveMail(boolean)
	 */
	@Override
	public void saveReceiveMail(boolean isReceive) throws ServiceRequestException
	{
		this.getPreferenceStub().saveReceiveMail(isReceive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#isReceiveEmail()
	 */
	@Override
	public boolean isReceiveEmail() throws ServiceRequestException
	{
		return this.isReceiveEmail(this.getUserSignature().getUserGuid());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#hasUpdateAuthorityForPublicSearch(java.lang.String)
	 */
	@Override
	public boolean hasUpdateAuthorityForPublicSearch(String searchGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().hasUpdateAuthorityForPublicSearch(searchGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#isSearchNameExist(java.lang.String, dyna.common.systemenum.SearchTypeEnum)
	 */
	@Override
	public boolean isSearchNameExist(String name, SearchTypeEnum typeEnum) throws ServiceRequestException
	{

		return this.getMySearchStub().isSearchNameExist(name, typeEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#savePMSearch(java.lang.String, dyna.common.systemenum.PMSearchTypeEnum,
	 * java.lang.String)
	 */
	@Override
	public Search savePMSearch(String name, PMSearchTypeEnum pmTypeEnum, String searchGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().savePMSearch(name, pmTypeEnum, searchGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#listPMSearch(dyna.common.systemenum.PMSearchTypeEnum, dyna.common.SearchCondition)
	 */
	@Override
	public List<Search> listPMSearch(PMSearchTypeEnum pmTypeEnum, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getMySearchStub().listPMSearch(pmTypeEnum, searchCondition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#isPMSearchNameExist(java.lang.String, dyna.common.systemenum.PMSearchTypeEnum)
	 */
	@Override
	public boolean isPMSearchNameExist(String name, PMSearchTypeEnum pmTypeEnum) throws ServiceRequestException
	{
		return this.getMySearchStub().isPMSearchNameExist(name, pmTypeEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#isShowLoginWarning()
	 */
	@Override
	public boolean isShowLoginWarning() throws ServiceRequestException
	{
		return this.getPreferenceStub().isShowLoginWarning();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveLoginWarning(boolean)
	 */
	@Override
	public void saveLoginWarning(boolean isShow) throws ServiceRequestException
	{
		this.getPreferenceStub().saveLoginWarning(isShow);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#listFilter4BO(dyna.common.SearchCondition, java.lang.String)
	 */
	@Override
	public List<Search> listFilter4BO(SearchCondition searchCondition, String boGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().listFilter4BO(searchCondition, boGuid);
	}

	@Override
	public List<SaQueryPreferenceDetail> listMySearchCondition(String classGuid) throws ServiceRequestException
	{
		return this.getMySearchConditionStub().listMySearchCondition(classGuid);
	}

	@Override
	public List<SaQueryPreferenceDetail> saveMySearchCondition(String classGuid, List<SaQueryPreferenceDetail> detailList) throws ServiceRequestException
	{
		return this.getMySearchConditionStub().saveMySearchCondition(classGuid, detailList);
	}

	@Override
	public List<SaCustomColumnsPreferenceDetail> saveMyCustomColumns(String tableType, List<SaCustomColumnsPreferenceDetail> detailList, String classifFK, String classFK)
			throws ServiceRequestException
	{
		return this.getMyCustomColumnsStub().saveMyCustomColumns(tableType, detailList, classifFK, classFK);
	}

	// TODO
	@Override
	public List<SaCustomColumnsPreferenceDetail> listMyCustomColumns(String tableType, String classifFK, String classFK) throws ServiceRequestException
	{
		return this.getMyCustomColumnsStub().listMyCustomColumns(tableType, classifFK, classFK);
	}

	@Override
	public FilePreferenceEnum getFilePreference() throws ServiceRequestException
	{
		return this.getPreferenceStub().getFilePreference();
	}

	@Override
	public void saveFilePreference(FilePreferenceEnum filePreferenceEnum) throws ServiceRequestException
	{
		this.getPreferenceStub().saveFilePreference(filePreferenceEnum);

	}

	@Override
	public WorkFlowPerference getWorkPreference() throws ServiceRequestException
	{
		return this.getPreferenceStub().getWorkPreference();
	}

	@Override
	public void saveWorkPreference(WorkFlowPerference workFlowPerference) throws ServiceRequestException
	{
		this.getPreferenceStub().saveWorkPreference(workFlowPerference);
	}

	@Override
	public boolean getCodingPreference() throws ServiceRequestException
	{
		return this.getPreferenceStub().getCodingPreference();
	}

	@Override
	public void saveCodingPreference(boolean isRecord) throws ServiceRequestException
	{
		this.getPreferenceStub().saveCodingPreference(isRecord);
	}

	@Override
	public List<Search> listFilter4Classificaiton(SearchCondition searchCondition, String classificationItemGuid) throws ServiceRequestException
	{
		return this.getMySearchStub().listFilter4Classificaiton(searchCondition, classificationItemGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#isLeftRightLayout()
	 */
	@Override
	public boolean isLeftRightLayout() throws ServiceRequestException
	{
		return this.getPreferenceStub().isLeftRightLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveLeftRightLayout(boolean)
	 */
	@Override
	public void saveLeftRightLayout(boolean isLeftRightLayout) throws ServiceRequestException
	{
		this.getPreferenceStub().saveLeftRightLayout(isLeftRightLayout);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#getLeftProportionValue()
	 */
	@Override
	public int getLeftProportionValue() throws ServiceRequestException
	{
		return this.getPreferenceStub().getLeftProportionValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveLeftProportionValue(int)
	 */
	@Override
	public void saveLeftProportionValue(int leftProportionValue) throws ServiceRequestException
	{
		this.getPreferenceStub().saveLeftProportionValue(leftProportionValue);
	}
	
	@Override
	public void savePreferenceByType(String value,PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException
	{
		this.getPreferenceStub().savePreferenceByType(value,preferenceTypeEnum);
	}
	
	@Override
	public String getPreferenceByType(PreferenceTypeEnum preferenceTypeEnum) throws ServiceRequestException
	{
		return this.getPreferenceStub().getPreferenceByType(preferenceTypeEnum);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveMessageClearCycle(int)
	 */
	@Override
	public void saveMessageClearCycle(int interval) throws ServiceRequestException
	{
		this.getPreferenceStub().saveMessageClearCycle(interval);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#getMessageClearCycle()
	 */
	@Override
	public int getMessageClearCycle() throws ServiceRequestException
	{
		return this.getPreferenceStub().getMessageClearCycle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#getWorkFlowClearCycle()
	 */
	@Override
	public int getWorkFlowClearCycle() throws ServiceRequestException
	{
		return this.getPreferenceStub().getWorkFlowClearCycle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#saveWorkFlowClearCycle(int)
	 */
	@Override
	public void saveWorkFlowClearCycle(int interval) throws ServiceRequestException
	{
		this.getPreferenceStub().saveWorkFlowClearCycle(interval);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#save(java.util.List)
	 */
	@Override
	public void savequickConfig(List<String> quickConfigNameList) throws ServiceRequestException
	{
		this.getPreferenceStub().savequickConfig(quickConfigNameList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.POS#listQuickConfig()
	 */
	@Override
	@SessionActiveTime(isUpdate = false)
	public List<String> listQuickConfig() throws ServiceRequestException
	{
		return this.getPreferenceStub().listQuickConfig();
	}

	public int getMessageClearCycle(String userGuid) throws ServiceRequestException
	{
		return this.getPreferenceStub().getMessageClearCycle(userGuid);
	}

	public int getWorkFlowClearCycle(String userGuid) throws ServiceRequestException
	{
		return this.getPreferenceStub().getWorkFlowClearCycle(userGuid);
	}

	@Override
	public void setQueryIDFuzzy(boolean value) throws ServiceRequestException
	{
		this.getPreferenceStub().setQueryIDFuzzy(value);

	}

	@Override
	public boolean getQueryIDFuzzy() throws ServiceRequestException
	{
		return this.getPreferenceStub().getQueryIDFuzzy();
	}

	@Override
	public void setQueryCaseSensitive(boolean value) throws ServiceRequestException
	{
		this.getPreferenceStub().setQueryCaseSensitive(value);
	}

	@Override
	public boolean getQueryCaseSensitive() throws ServiceRequestException
	{
		return this.getPreferenceStub().getQueryCaseSensitive();
	}

	@Override
	public void setQuerySubFolder(boolean value) throws ServiceRequestException
	{
		this.getPreferenceStub().setQuerySubFolder(value);
	}

	@Override
	public boolean getQuerySubFolder() throws ServiceRequestException
	{
		return this.getPreferenceStub().getQuerySubFolder();
	}

	@Override
	public void setHasSimpleQuery(boolean value) throws ServiceRequestException
	{
		this.getPreferenceStub().setHasSimpleQuery(value);
	}

	@Override
	public boolean getHasSimpleQuery() throws ServiceRequestException
	{
		return this.getPreferenceStub().getHasSimpleQuery();
	}

	@Override
	public void setQueryRevisionType(SearchRevisionTypeEnum value) throws ServiceRequestException
	{
		this.getPreferenceStub().setQueryRevisionType(value);
	}

	@Override
	public SearchRevisionTypeEnum getQueryRevisionType() throws ServiceRequestException
	{
		return this.getPreferenceStub().getQueryRevisionType();
	}

	@Override
	public void setQueryOwnerConditionType(int value) throws ServiceRequestException
	{
		this.getPreferenceStub().setQueryOwnerConditionType(value);
	}

	@Override
	public int getQueryOwnerConditionType() throws ServiceRequestException
	{
		return this.getPreferenceStub().getQueryOwnerConditionType();
	}

	@Override
	public String defaultOpenEditorInput() throws ServiceRequestException
	{
		return this.getPreferenceStub().defaultOpenEditorInput();
	}

	@Override
	public void saveDefaultOpenEditorInput(String editorInputId) throws ServiceRequestException
	{
		this.getPreferenceStub().saveDefaultOpenEditorInput(editorInputId);
	}

}
