/*
	 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericReportParams
 * Daniel 2014-10-29
 */
package dyna.app.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.systemenum.LanguageEnum;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.BRM;
import dyna.net.service.brs.CPB;
import dyna.net.service.brs.EDAP;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.PMS;
import dyna.net.service.brs.PPMS;
import dyna.net.service.brs.UECS;
import dyna.net.service.das.MSRM;

/**
 * @author Daniel
 * 
 */
public class GenericReportParams
{
	private MSRM						msrm				= null;

	private EMM							emm					= null;

	private BOAS						boas				= null;

	private EDAP						edap				= null;

	private AAS							aas					= null;

	private PPMS						ppms				= null;

	private BOMS						boms				= null;

	private UECS						uecs				= null;

	private PMS							pms					= null;

	private BRM							brm					= null;

	private CPB							cpb					= null;

	private List<DetailColumnInfo>		detailColumnList	= new ArrayList<DetailColumnInfo>();

	private List<ParameterColumnInfo>	headerColumnList	= new ArrayList<ParameterColumnInfo>();

	private Map<String, Object>			otherParams			= new HashMap<String, Object>();

	private UIObjectInfo				uiObject			= null;

	private UIObjectInfo				cfUIObject			= null;

	private LanguageEnum				lang				= LanguageEnum.ZH_CN;

	public void setMSRM(MSRM msrm)
	{
		this.msrm = msrm;
	}

	public MSRM getMSRM()
	{
		return this.msrm;
	}

	public void setEMM(EMM emm)
	{
		this.emm = emm;
	}

	public EMM getEMM()
	{
		return this.emm;
	}

	public void setBOAS(BOAS boas)
	{
		this.boas = boas;
	}

	public BOAS getBOAS()
	{
		return this.boas;
	}

	public void setEDAP(EDAP edap)
	{
		this.edap = edap;
	}

	public EDAP getEDAP()
	{
		return this.edap;
	}

	public void setAAS(AAS aas)
	{
		this.aas = aas;
	}

	public AAS getAAS()
	{
		return this.aas;
	}

	public void setPPMS(PPMS ppms)
	{
		this.ppms = ppms;
	}

	public PPMS getPPMS()
	{
		return this.ppms;
	}

	public void setBOMS(BOMS boms)
	{
		this.boms = boms;
	}

	public BOMS getBOMS()
	{
		return this.boms;
	}

	public void setUECS(UECS uecs)
	{
		this.uecs = uecs;
	}

	public UECS getUECS()
	{
		return this.uecs;
	}

	public void setPMS(PMS pms)
	{
		this.pms = pms;
	}

	public PMS getPMS()
	{
		return this.pms;
	}

	public void setBRM(BRM brm)
	{
		this.brm = brm;
	}

	public BRM getBRM()
	{
		return this.brm;
	}

	public void setCPB(CPB cpb)
	{
		this.cpb = cpb;
	}

	public CPB getCPB()
	{
		return this.cpb;
	}

	/**
	 * @return the detailColumnList
	 */
	public List<DetailColumnInfo> getDetailColumnList()
	{
		return detailColumnList == null ? new ArrayList<DetailColumnInfo>() : detailColumnList;
	}

	/**
	 * @param detailColumnList
	 *            the detailColumnList to set
	 */
	public void setDetailColumnList(List<DetailColumnInfo> detailColumnList)
	{
		this.detailColumnList = detailColumnList;
	}

	/**
	 * @return the headerColumnList
	 */
	public List<ParameterColumnInfo> getHeaderColumnList()
	{
		return headerColumnList == null ? new ArrayList<ParameterColumnInfo>() : headerColumnList;
	}

	/**
	 * @param headerColumnList
	 *            the headerColumnList to set
	 */
	public void setHeaderColumnList(List<ParameterColumnInfo> headerColumnList)
	{
		this.headerColumnList = headerColumnList;
	}

	/**
	 * @return the otherParams
	 */
	public Map<String, Object> getOtherParams()
	{
		return otherParams == null ? new HashMap<String, Object>() : otherParams;
	}

	/**
	 * @param otherParams
	 *            the otherParams to set
	 */
	public void setOtherParams(Map<String, Object> otherParams)
	{
		this.otherParams = otherParams;
	}

	/**
	 * @return the uiObject
	 */
	public UIObjectInfo getUiObject()
	{
		return uiObject;
	}

	/**
	 * @param uiObject
	 *            the uiObject to set
	 */
	public void setUiObject(UIObjectInfo uiObject)
	{
		this.uiObject = uiObject;
	}

	/**
	 * @return the cfUIObject
	 */
	public UIObjectInfo getCFUIObject()
	{
		return cfUIObject;
	}

	/**
	 * @param cfUIObject
	 *            the cfUIObject to set
	 */
	public void setCFUIObject(UIObjectInfo cfUIObject)
	{
		this.cfUIObject = cfUIObject;
	}

	/**
	 * @return the lang
	 */
	public LanguageEnum getLang()
	{
		return lang;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(LanguageEnum lang)
	{
		this.lang = lang;
	}
}
