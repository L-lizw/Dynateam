/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DSSFileTrans
 * Wanglei 2010-10-18
 */
package dyna.common.dtomapper;

import dyna.common.dto.DSSFileTrans;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:22
**/

public interface DSSFileTransMapper
{

	List<DSSFileTrans> select(Map<String,Object> param);

	List<DSSFileTrans> selectUser(String userName);

	List<DSSFileTrans> selectDNLUPL(Map<String,Object> param);

	void insertMaster(Map<String,Object> param);

	void insertDetail(Map<String,Object> param);

	void deleteTranFiles(Map<String,Object> param);

	void deleteMaster(String guid);

	void deleteDetail(String guid);

	int updateMaster(Map<String,Object> param);

	int updateDetail(String guid);

}
