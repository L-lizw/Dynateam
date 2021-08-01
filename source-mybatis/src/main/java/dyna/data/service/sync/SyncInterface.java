package dyna.data.service.sync;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.model.itf.InterfaceData;
import dyna.common.dto.model.itf.InterfaceRefData;
import dyna.common.dtomapper.TreeDataRelationMapper;
import dyna.common.dtomapper.model.itf.InterfaceDataMapper;
import dyna.common.dtomapper.model.itf.InterfaceRefDataMapper;
import dyna.common.systemenum.ModelInterfaceEnum;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SyncInterface extends SyncService
{
	@Autowired
	private InterfaceDataMapper             interfaceDataMapper;
	@Autowired
	private InterfaceRefDataMapper          interfaceRefDataMapper;
	@Autowired
	private TreeDataRelationMapper          treeDataRelationMapper;
	/**
	 * 存放更新过的接口
	 */
	private final Map<ModelInterfaceEnum, String> interfaceGuidMap = new HashMap<ModelInterfaceEnum, String>();

	public SyncInterface(SqlSessionFactory sqlClient, String userGuid)
	{
		super(sqlClient, userGuid);
	}

	@SuppressWarnings("unchecked")
	public void startSync() throws SQLException
	{
		for (ModelInterfaceEnum interfaceEnum : ModelInterfaceEnum.values())
		{
			InterfaceData interfaceData = (InterfaceData) this.interfaceDataMapper.get(interfaceEnum.name());
			if (interfaceData == null)
			{
				interfaceData = new InterfaceData();
				interfaceData.setInterfaceName(interfaceEnum.name());
				interfaceData.setGuid(this.genericGuid());
				this.interfaceDataMapper.insert(interfaceData);
			}
			this.interfaceGuidMap.put(interfaceEnum, interfaceData.getGuid());
		}

		List<InterfaceData> interfaceList = this.interfaceDataMapper.selectForLoad();
		for (InterfaceData interfaceData : interfaceList)
		{
			ModelInterfaceEnum interfaceEnum = ModelInterfaceEnum.typeof(interfaceData.getInterfaceName());
			if (interfaceEnum == null)
			{
				this.interfaceDataMapper.delete(interfaceData.getGuid());
			}
		}

		this.interfaceDataMapper.deleteAll();

		this.processInterface();

		// 保存接口树结构
		this.saveInterfaceTreeRelation();
	}

	private void processInterface() throws SQLException
	{
		for (ModelInterfaceEnum interfaceEnum : ModelInterfaceEnum.values())
		{
			if (interfaceEnum.getSuperInterfaces() != null && interfaceEnum.getSuperInterfaces().length > 0)
			{
				for (ModelInterfaceEnum parentInterfaceEnum : interfaceEnum.getSuperInterfaces())
				{
					this.updateInterfaceRef(interfaceEnum, parentInterfaceEnum);
				}
			}
			else
			{
				this.updateInterfaceRef(interfaceEnum, null);
			}
		}
	}

	private void saveInterfaceTreeRelation() throws SQLException
	{
		Map<String, List<String>> interfaceTreeMap = new HashMap<>();
		List<InterfaceRefData> interfaceRefDataList = this.interfaceRefDataMapper.selectForLoad();
		for (InterfaceRefData refData : interfaceRefDataList)
		{
			String interfaceGuid = refData.getInterfaceGuid();
			if (!interfaceTreeMap.containsKey(interfaceGuid))
			{
				interfaceTreeMap.put(interfaceGuid, new ArrayList<String>());
			}
			if (!interfaceTreeMap.get(interfaceGuid).contains(interfaceGuid))
			{
				interfaceTreeMap.get(interfaceGuid).add(interfaceGuid);
			}

			this.listAllSubInterfaceGuidList(interfaceGuid, interfaceRefDataList, interfaceTreeMap.get(interfaceGuid));
		}

		for (String interfaceGuid : interfaceTreeMap.keySet())
		{
			List<String> subInterfaceGuidList = interfaceTreeMap.get(interfaceGuid);
			for (String subInterfaceGuid : subInterfaceGuidList)
			{
				TreeDataRelation relation = new TreeDataRelation();
				relation.setDataType(TreeDataRelation.DATATYPE_INTERFACE);
				relation.setDataGuid(interfaceGuid);
				relation.setSubDataGuid(subInterfaceGuid);
				relation.setCreateUserGuid(this.userGuid);
				relation.setGuid(this.genericGuid());
				this.treeDataRelationMapper.insert(relation);
			}
		}
	}

	private void listAllSubInterfaceGuidList(String interfaceGuid, List<InterfaceRefData> interfaceRefDataList, List<String> interfaceGuidList)
	{
		for (InterfaceRefData refData : interfaceRefDataList)
		{
			if (interfaceGuid.equals(refData.getParentGuid()))
			{
				if (!interfaceGuidList.contains(refData.getInterfaceGuid()))
				{
					interfaceGuidList.add(refData.getInterfaceGuid());
				}
				this.listAllSubInterfaceGuidList(refData.getInterfaceGuid(), interfaceRefDataList, interfaceGuidList);
			}
		}
	}

	private void updateInterfaceRef(ModelInterfaceEnum interfaceEnum, ModelInterfaceEnum parentInterfaceEnum) throws SQLException
	{
		InterfaceRefData interfaceRefData = new InterfaceRefData();
		interfaceRefData.setGuid(this.genericGuid());
		interfaceRefData.setInterfaceGuid(this.interfaceGuidMap.get(interfaceEnum));
		if (parentInterfaceEnum != null)
		{
			interfaceRefData.setParentGuid(this.interfaceGuidMap.get(parentInterfaceEnum));
		}

		this.interfaceRefDataMapper.insert(interfaceRefData);
	}
}
