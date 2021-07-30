/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcTrackAttach
 * zhanghj 2011-3-30
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ProcTrackAttach;
import dyna.common.dtomapper.DynaCommonMapper;
import org.apache.ibatis.annotations.Param;

/**
*
* @author   Lizw
* @date     2021/7/11 17:18
**/

public interface ProcTrackAttachMapper extends DynaCommonMapper<ProcTrackAttach>
{

	void delete(@Param("PROCRTGUID")String procrtGuid,@Param("ATTACHGUID")String attchGuid);

	void deleteGuid(String guid);

}
