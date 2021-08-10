/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeRuleGroup
 * WangLHB Sep 25, 2012
 */
package dyna.app.service.brs.boas.numbering.classification;

import java.util.List;
import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.numbering.ClassificationAllocate;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassField;

/**
 * @author WangLHB
 * 
 */
public class AllocateParameter extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long				serialVersionUID				= 1L;

	// 已经存在
	// public static final String GUID = "GUID";
	// public static final String NAME = "NAME";
	// public static final String CREATE_USER_GUID = "CREATEUSERGUID";
	// public static final String CREATE_USER_NAME = "CREATEUSERNAME";
	// public static final String UPDATE_USER_GUID = "UPDATEUSERGUID";
	// public static final String UPDATE_USER_NAME = "UPDATEUSERNAME";
	// public static final String CREATE_TIME = "CREATETIME";
	// public static final String UPDATE_TIME = "UPDATETIME";

	public Map<String, FoundationObject>	classificationMap				= null;
	public ClassificationNumberField		field							= null;
	public ClassficationFeatureItem			item							= null;
	public ClassField						numberClassField				= null;
	public DataAccessService				dataAccessService				= null;
	public ClassificationAllocate			control							= null;
	public List<ClassficationFeatureItem>	classficationFeatureItemList	= null;
	public List<ClassificationNumberField>	classificationNumberFieldList	= null;

	// 是否是创建，创建要更新frozen 字段，更新流水吗
	// 保存时不做这些操作
	public boolean							isCreate						= true;
}
