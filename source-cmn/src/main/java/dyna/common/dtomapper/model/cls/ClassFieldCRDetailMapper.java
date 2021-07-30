package dyna.common.dtomapper.model.cls;

import dyna.common.dto.model.cls.ClassFieldCRDetail;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:53
**/

public interface ClassFieldCRDetailMapper extends DynaCacheMapper<ClassFieldCRDetail>
{
	List<ClassFieldCRDetail> select(ClassFieldCRDetail classFieldCRDetail);
}