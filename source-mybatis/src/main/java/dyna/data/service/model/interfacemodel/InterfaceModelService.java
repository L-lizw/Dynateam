package dyna.data.service.model.interfacemodel;

import java.util.List;
import java.util.Map;

import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.data.service.model.ModelService;
import dyna.data.service.sync.bean.TableIndexModel;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public interface InterfaceModelService extends ModelService
{
	/**
	 * 重新加载缓存
	 *
	 * @throws ServiceRequestException
	 */
	void reloadModel() throws ServiceRequestException;

	/**
	 * 取得所有的接口
	 *
	 * @return
	 */
	Map<String, InterfaceObject> getInterfaceMap();

	/**
	 * 根据接口名字取得接口对象
	 *
	 * @param interfaceName
	 * @return
	 */
	InterfaceObject getInterface(ModelInterfaceEnum interfaceEnum);

	/**
	 * 根据指定的接口名字取得该接口的所有子接口
	 *
	 * @param interfaceEnum
	 * @return
	 */
	List<InterfaceObject> listSubInterface(ModelInterfaceEnum interfaceEnum);

	/**
	 * 根据接口名字取得该接口的所有字段列表
	 *
	 * @param interfaceName
	 * @return
	 */
	List<ClassField> listClassFieldOfInterface(ModelInterfaceEnum interfaceEnum);

	/**
	 * 取得所有接口的字段信息
	 *
	 * @return
	 */
	Map<String, List<ClassField>> getInterfaceFieldMap();

	/**
	 * 取得接口的索引信息
	 *
	 * @return
	 */
	Map<String, List<TableIndexModel>> getInterfaceIndexMap();
}
