package dyna.common.dtomapper.cfm;

import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dtomapper.DynaCacheMapper;

/**
*
* @author   Lizw
* @date     2021/7/11 16:47
**/

public interface ClassificationNumberFieldMapper extends DynaCacheMapper<ClassificationNumberField>
{

	void deleteFields(String clfNumberRegualrGuid);
}
