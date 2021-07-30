/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttachTemplate
 * zhanghj 2011-4-16
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ProcAttachSetting;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:17
**/

public interface ProcAttachSettingMapper
{
	void insertbom(ProcAttachSetting setting);

	int updatebom(ProcAttachSetting setting);

	void insertrelation(ProcAttachSetting setting);

	int updaterelation(ProcAttachSetting setting);

	List<ProcAttachSetting> selectbomtemplate(Map<String,Object> param);

	List<ProcAttachSetting> selectrelationtemplate(Map<String,Object> param);

	void deletebom(String guid);

	void deleterelation(String guid);

}
