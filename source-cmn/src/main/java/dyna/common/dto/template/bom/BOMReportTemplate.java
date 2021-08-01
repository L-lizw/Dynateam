/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMReportTemplate
 * cuilei 2012-6-12
 */
package dyna.common.dto.template.bom;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.bom.BOMReportTemplateMapper;
import dyna.common.util.BooleanUtils;

/**
 * @author cuilei
 * 
 */
@Cache
@EntryMapper(BOMReportTemplateMapper.class)
public class BOMReportTemplate extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID					= -495296005153817935L;

	public static final String	BOM_REPORT_TEMPLATE_NAME			= "BOMREPORTTEMPLATENAME";

	public static final String	BOM_REPORT_TEMPLATE					= "BOMREPORTTEMPLATE";

	public static final String	BOM_REPORT_EXPORT_BOMTREE			= "BOMREPORTEXPORTBOMTREE";

	public static final String	BOM_REPORT_EXPORT_BOMLIST			= "BOMREPORTEXPORTBOMLIST";

	public static final String	BOM_REPORT_EXPORT_BOORCL			= "BOMREPORTEXPORTBOORCL";

	public static final String	BOM_REPORT_EXPORT_BO				= "BOMREPORTEXPORTBO";

	public static final String	BOM_REPORT_EXPORT_CLASSIFICATION	= "BOMREPORTEXPORTCLASSIFICATION";

	public static final String	BOM_TEMPLATE_GUID					= "BOMTEMPLATEGUID";

	public static final String	BOM_SCRIPT_FILE_NAME				= "BOMSCRIPTFILENAME";

	/**
	 * @return the bomReportTemplateName
	 */
	public String getBomScriptFileName()
	{
		return (String) this.get(BOM_SCRIPT_FILE_NAME);
	}

	/**
	 * @param bomScriptFileName
	 *            the bomScriptFileName to set
	 */
	public void setBomScriptFileName(String bomScriptFileName)
	{
		this.put(BOM_SCRIPT_FILE_NAME, bomScriptFileName);
	}

	/**
	 * @return the bomReportTemplateName
	 */
	public String getBomReportTemplateName()
	{
		return (String) this.get(BOM_REPORT_TEMPLATE_NAME);
	}

	/**
	 * @return the bomReportTemplate
	 */
	public String getBomReportTemplate()
	{
		return (String) this.get(BOM_REPORT_TEMPLATE);
	}

	/**
	 * @return the BomReportExportBomTree
	 */
	public boolean getBomReportExportBomTree()
	{
		if (this.get(BOM_REPORT_EXPORT_BOMTREE) == null)
		{
			return true;
		}

		return "1".equals(this.get(BOM_REPORT_EXPORT_BOMTREE));
	}

	/**
	 * @return the BomReportExportBomList
	 */
	public boolean getBomReportExportBomList()
	{
		if (this.get(BOM_REPORT_EXPORT_BOMLIST) == null)
		{
			return true;
		}

		return "1".equals(this.get(BOM_REPORT_EXPORT_BOMLIST));
	}

	/**
	 * @return the BomReportExportBOORCL
	 */
	public boolean getBomReportExportBOORCL()
	{
		if (this.get(BOM_REPORT_EXPORT_BOORCL) == null)
		{
			return true;
		}

		return "1".equals(this.get(BOM_REPORT_EXPORT_BOORCL));
	}

	/**
	 * @return the BOM_REPORT_EXPORT_BO
	 */
	public boolean getBomReportExportBO()
	{
		if (this.get(BOM_REPORT_EXPORT_BO) == null)
		{
			return true;
		}

		return "1".equals(this.get(BOM_REPORT_EXPORT_BO));
	}

	/**
	 * @return the BOM_REPORT_EXPORT_CLASSIFICATION
	 */
	public boolean getBomReportExportCLASSIFICATION()
	{
		if (this.get(BOM_REPORT_EXPORT_CLASSIFICATION) == null)
		{
			return true;
		}

		return "1".equals(this.get(BOM_REPORT_EXPORT_CLASSIFICATION));
	}

	/**
	 * @return the BOMTemplateGUID
	 */
	public String getBOMTemplateGUID()
	{
		return (String) this.get(BOM_TEMPLATE_GUID);
	}

	/**
	 * @param bomReportTemplateName
	 *            the bomReportTemplateName to set
	 */
	public void setBomReportTemplateName(String bomReportTemplateName)
	{
		this.put(BOM_REPORT_TEMPLATE_NAME, bomReportTemplateName);
	}

	/**
	 * @param bomReportTemplate
	 *            the bomReportTemplate to set
	 */
	public void setBomReportTemplate(String bomReportTemplate)
	{
		this.put(BOM_REPORT_TEMPLATE, bomReportTemplate);
	}

	/**
	 * @param bomReportExportBomTree
	 *            the bomReportExportBomTree to set
	 */
	public void setBomReportExportBomTree(boolean bomReportExportBomTree)
	{
		this.put(BOM_REPORT_EXPORT_BOMTREE, BooleanUtils.getBooleanString10(bomReportExportBomTree));
	}

	/**
	 * @param bomReportExportBomList
	 *            the bomReportExportBomList to set
	 */
	public void setBomReportExportBomList(boolean bomReportExportBomList)
	{
		this.put(BOM_REPORT_EXPORT_BOMLIST, BooleanUtils.getBooleanString10(bomReportExportBomList));
	}

	/**
	 * @param bomReportExportBO
	 *            the bomReportExportBO to set
	 */
	public void setBomReportExportBO(boolean bomReportExportBO)
	{
		this.put(BOM_REPORT_EXPORT_BO, BooleanUtils.getBooleanString10(bomReportExportBO));
	}

	/**
	 * @param bomReportExportCLASSIFICATION
	 *            the bomReportExportCLASSIFICATION to set
	 */
	public void setBomReportExportCLASSIFICATION(boolean bomReportExportCLASSIFICATION)
	{
		this.put(BOM_REPORT_EXPORT_CLASSIFICATION, BooleanUtils.getBooleanString10(bomReportExportCLASSIFICATION));
	}

	/**
	 * @param bomReportExportBOORCL
	 *            the bomReportExportBOORCL to set
	 */
	public void setBomReportExportBOORCL(boolean bomReportExportBOORCL)
	{
		this.put(BOM_REPORT_EXPORT_BOORCL, BooleanUtils.getBooleanString10(bomReportExportBOORCL));
	}

	/**
	 * @param bomTemplateGUID
	 *            the bomTemplateGUID to set
	 */
	public void setBOMTemplateGUID(String bomTemplateGUID)
	{
		this.put(BOM_TEMPLATE_GUID, bomTemplateGUID);
	}
}
