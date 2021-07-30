package dyna.common.dtomapper.cfm;

import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:46
**/

public interface ClassficationFeatureMapper extends DynaCacheMapper<ClassficationFeature>
{
	List<ClassficationFeature> selectMaster(String classGuid);
}
