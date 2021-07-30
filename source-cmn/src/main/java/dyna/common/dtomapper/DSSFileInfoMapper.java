/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileInfo 文件信息bean
 * Wanglei 2010-9-1
 */
package dyna.common.dtomapper;

import dyna.common.dto.DSSFileInfo;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:22
**/

public interface DSSFileInfoMapper extends DynaCommonMapper<DSSFileInfo>
{

	List<DSSFileInfo> selectForCopy(Map<String,Object> param);

	int updatePrimaryFileAfterCopyOnly(DSSFileInfo dssFileInfo);

	int resetPrimary(Map<String,Object> param);

	int setPrimary(Map<String,Object> param);

	List<DSSFileInfo> checkUploadFiles(Map<String,Object> param);

	List<DSSFileInfo> selectAnyTableFile(Map<String,Object> param);

	List<DSSFileInfo>selectWfFile(Map<String,Object> param);

	void selectWfFile(String guid);

	int updateAnyTableFile(DSSFileInfo dssFileInfo);

	void insertAnyTableFile(DSSFileInfo dssFileInfo);

	void deleteWfFile(String guid);

	int updateWfFile(DSSFileInfo dssFileInfo);

	void insertWfFile(DSSFileInfo dssFileInfo);


}
