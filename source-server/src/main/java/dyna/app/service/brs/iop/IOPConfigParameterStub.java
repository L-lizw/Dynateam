package dyna.app.service.brs.iop;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.iopconfigparamter.IOPColumnTitle;
import dyna.common.bean.data.iopconfigparamter.IOPColumnValue;
import dyna.common.bean.data.iopconfigparamter.IOPConfigConstant;
import dyna.common.bean.data.iopconfigparamter.IOPConfigParameter;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

public class IOPConfigParameterStub extends AbstractServiceStub<IOPImpl>
{
	public IOPConfigParameterStub(ServiceContext context, IOPImpl service)
	{
		super(context, service);
	}

	public List<IOPConfigParameter> listIOPConfigParameter(String masterGuid, Date end1ReleaseTime) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("MASTERGUID", masterGuid);
		if (end1ReleaseTime != null)
		{
			end1ReleaseTime = DateFormat.getDateOfEnd(end1ReleaseTime, DateFormat.PTN_YMDHMS);
		}
		filter.put("RELEASETIME", end1ReleaseTime);

		List<IOPColumnValue> listColumn = sds.query(IOPColumnValue.class, filter, "listAllVirableOfValue");
		List<IOPConfigParameter> value = new ArrayList<IOPConfigParameter>();
		if (!SetUtils.isNullList(listColumn))
		{
			Map<Integer, List<IOPColumnValue>> tempMap = new LinkedHashMap<Integer, List<IOPColumnValue>>();
			for (IOPColumnValue columnvalue : listColumn)
			{
				int sequence = columnvalue.getSequence();
				List<IOPColumnValue> listvalue = tempMap.get(sequence);
				if (SetUtils.isNullList(listvalue))
				{
					listvalue = new ArrayList<IOPColumnValue>();
					listvalue.add(columnvalue);
					tempMap.put(sequence, listvalue);
				}
				else
				{
					listvalue.add(columnvalue);
				}
			}
			for (Integer seq : tempMap.keySet())
			{
				IOPConfigParameter parameter = new IOPConfigParameter();
				parameter.setMasterGuid(masterGuid);
				parameter.setSequence(seq);
				parameter.setValueList(tempMap.get(seq));
				value.add(parameter);
			}
			// 取列头:第一行对象中才有列头
			if (!SetUtils.isNullList(value))
			{
				List<IOPColumnTitle> listTitle = this.listIOPColumnTitle(masterGuid, end1ReleaseTime);
				if (!SetUtils.isNullList(listTitle))
				{
					value.get(0).put(IOPConfigParameter.TITLES, listTitle);
				}
			}
		}
		else
		{
			IOPConfigParameter parameter = new IOPConfigParameter();
			parameter.setMasterGuid(masterGuid);
			value.add(parameter);
			List<IOPColumnTitle> listTitle = this.listIOPColumnTitle(masterGuid, end1ReleaseTime);
			if (!SetUtils.isNullList(listTitle))
			{
				value.get(0).put(IOPConfigParameter.TITLES, listTitle);
			}
		}
		return value;
	}

	public List<IOPColumnTitle> listIOPColumnTitle(String masterGuid, Date end1ReleaseTime) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("MASTERGUID", masterGuid);
		if (end1ReleaseTime != null)
		{
			end1ReleaseTime = DateFormat.getDateOfEnd(end1ReleaseTime, DateFormat.PTN_YMDHMS);
		}
		filter.put("RELEASETIME", end1ReleaseTime);
		return sds.query(IOPColumnTitle.class, filter, "selectCustTtileOfTable");
	}

	/**
	 * 差异同步
	 * 
	 * @param end1ObjectGuid
	 * @param end1ReleaseTime
	 * @param listTitles
	 * @param listValues
	 * @throws ServiceRequestException
	 */
	public void syncIOPConfigParameter(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, List<IOPColumnTitle> listTitles, Map<Integer, List<IOPColumnValue>> listValues)
			throws ServiceRequestException
	{
		saveTitles(end1ObjectGuid, end1ReleaseTime, listTitles);
		saveColumnValue(end1ObjectGuid, end1ReleaseTime, listValues);
	}

	private void saveColumnValue(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, Map<Integer, List<IOPColumnValue>> listValues) throws ServiceRequestException
	{
		List<IOPColumnValue> insertList = new ArrayList<IOPColumnValue>();
		List<IOPColumnValue> updateList = new ArrayList<IOPColumnValue>();
		List<Map<String, Object>> deleteList = new LinkedList<>();
		List<Map<String, Object>> updateHasNextRevision = new LinkedList<>();
		this.getCategorizingColumnData(end1ObjectGuid, end1ReleaseTime, listValues, insertList, updateList, deleteList, updateHasNextRevision);
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(insertList))
		{
			for (Map<String, Object> param : insertList)
			{
				param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
			}
			sds.insertBatch(IOPColumnValue.class, insertList, "inserBatchList");
		}
		if (!SetUtils.isNullList(updateList))
		{
			for (Map<String, Object> param : updateList)
			{
				param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
			}
			sds.updateBatch(IOPColumnTitle.class, updateList, "updateBatchList");
		}
		if (!SetUtils.isNullList(deleteList))
		{
			sds.deleteBatch(IOPColumnValue.class, deleteList, "delete");
		}
		if (!SetUtils.isNullList(updateHasNextRevision))
		{
			sds.updateBatch(IOPColumnValue.class, updateHasNextRevision, "updateNextRevisionBatchList");
		}
	}

	private void getCategorizingColumnData(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, Map<Integer, List<IOPColumnValue>> listValues, List<IOPColumnValue> insertList,
			List<IOPColumnValue> updateList, List<Map<String, Object>> deleteList, List<Map<String, Object>> updateHasNextRevision)
	{
		Map<Integer, Map<String, IOPColumnValue>> oldMap = new LinkedHashMap<Integer, Map<String, IOPColumnValue>>();
		Map<Integer, Map<String, IOPColumnValue>> newMap = new LinkedHashMap<Integer, Map<String, IOPColumnValue>>();

		List<IOPColumnValue> oldValueList = this.listIOPColumnValue(end1ObjectGuid.getMasterGuid(), end1ReleaseTime, null);
		if (!SetUtils.isNullList(oldValueList))
		{
			for (IOPColumnValue columnValue : oldValueList)
			{
				int id = columnValue.getSequence();
				Map<String, IOPColumnValue> value = null;
				if (oldMap.get(id) == null)
				{
					value = new HashMap<String, IOPColumnValue>();
					value.put(columnValue.getColumnName(), columnValue);
					oldMap.put(id, value);
				}
				else
				{
					value = oldMap.get(id);
					value.put(columnValue.getColumnName(), columnValue);
				}
			}
		}
		if (!SetUtils.isNullMap(listValues))
		{
			for (int sequence : listValues.keySet())
			{
				List<IOPColumnValue> listColumn = listValues.get(sequence);
				if (!SetUtils.isNullList(listColumn))
				{
					Map<String, IOPColumnValue> value = new HashMap<String, IOPColumnValue>();
					for (IOPColumnValue column : listColumn)
					{
						String uid = StringUtils.generateRandomUID(32).toUpperCase();
						column.setGuid(uid);
						column.setMasterGuid(end1ObjectGuid.getMasterGuid());
						value.put(column.getColumnName(), column);
					}
					newMap.put(sequence, value);
				}
			}
		}
		if (!SetUtils.isNullMap(newMap) && SetUtils.isNullMap(oldMap))
		{
			for (int sequence : listValues.keySet())
			{
				List<IOPColumnValue> listColumn = listValues.get(sequence);
				if (!SetUtils.isNullList(listColumn))
				{
					insertList.addAll(listColumn);
				}
			}
		}
		else if (SetUtils.isNullMap(newMap) && !SetUtils.isNullMap(oldMap))
		{
			for (IOPColumnValue columnvalue : oldValueList)
			{
				this.setDeleteColumnValue(columnvalue, deleteList, insertList, updateHasNextRevision);
			}
		}
		else
		{
			for (int sequence : oldMap.keySet())
			{
				Map<String, IOPColumnValue> oldColumn = oldMap.get(sequence);
				Map<String, IOPColumnValue> newColumn = newMap.get(sequence);
				if (newColumn == null)
				{
					for (String name : oldColumn.keySet())
					{
						this.setDeleteColumnValue(oldColumn.get(name), deleteList, insertList, updateHasNextRevision);
					}
				}
				else
				{
					for (String name : oldColumn.keySet())
					{
						IOPColumnValue _oldCV = oldColumn.get(name);
						IOPColumnValue _newCV = newColumn.get(name);
						if (_newCV == null)
						{
							this.setDeleteColumnValue(_oldCV, deleteList, insertList, updateHasNextRevision);
						}
						else
						{
							if ((_oldCV.getColumnValue() != null && !_oldCV.getColumnValue().equals(_newCV.getColumnValue()))
									|| (_oldCV.getColumnValue() == null && _newCV.getColumnValue() != null) || (_oldCV.getColumnValue() != null && _newCV.getColumnValue() == null))
							{
								_oldCV.setColumnValue(_newCV.getColumnValue());
								this.setUpdateColumnValue(_oldCV, updateList, insertList, updateHasNextRevision);
							}
						}
					}
					for (String name : newColumn.keySet())
					{
						IOPColumnValue _oldCV = oldColumn.get(name);
						IOPColumnValue _newCV = newColumn.get(name);
						if (_oldCV == null)
						{
							insertList.add(_newCV);
						}
					}
				}
			}
			for (int sequence : newMap.keySet())
			{
				Map<String, IOPColumnValue> oldColumn = oldMap.get(sequence);
				if (oldColumn == null)
				{
					Map<String, IOPColumnValue> newColumn = newMap.get(sequence);
					for (String name : newColumn.keySet())
					{
						insertList.add(newColumn.get(name));
					}
				}
			}
		}

	}

	private void setUpdateColumnValue(IOPColumnValue _oldCV, List<IOPColumnValue> updateList, List<IOPColumnValue> insertList, List<Map<String, Object>> updateHasNextRevision)
	{
		if (_oldCV.getReleaseTime() == null)
		{
			updateList.add(_oldCV);
		}
		else
		{
			this.copyColumnValue(_oldCV, insertList, updateHasNextRevision);
		}
	}

	private void setDeleteColumnValue(IOPColumnValue columnvalue, List<Map<String, Object>> deleteList, List<IOPColumnValue> insertList,
			List<Map<String, Object>> updateHasNextRevision)
	{
		if (columnvalue.getReleaseTime() == null)
		{
			HashMap<String, Object> guidmap = new HashMap<>();
			guidmap.put("GUID", columnvalue.getGuid());
			deleteList.add(guidmap);
		}
		else
		{
			this.copyColumnValue(columnvalue, insertList, updateHasNextRevision);
		}
	}

	private void copyColumnValue(IOPColumnValue columnvalue, List<IOPColumnValue> insertList, List<Map<String, Object>> updateHasNextRevision)
	{
		HashMap<String, Object> guidmap = new HashMap<>();
		guidmap.put("GUID", columnvalue.getGuid());
		updateHasNextRevision.add(guidmap);
		columnvalue.clear(IOPColumnTitle.RELEASETIME);
		columnvalue.clear(IOPColumnTitle.HASNEXTREVISION);
		String uid = StringUtils.generateRandomUID(32).toUpperCase();
		columnvalue.setGuid(uid);
		insertList.add(columnvalue);
	}

	private List<IOPColumnValue> listIOPColumnValue(String masterGuid, Date end1ReleaseTime, String titleName)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("MASTERGUID", masterGuid);
		if (end1ReleaseTime != null)
		{
			end1ReleaseTime = DateFormat.getDateOfEnd(end1ReleaseTime, DateFormat.PTN_YMDHMS);
		}
		filter.put("RELEASETIME", end1ReleaseTime);
		if (!StringUtils.isNullString(titleName))
		{
			filter.put("NAME", titleName);
		}
		return sds.query(IOPColumnValue.class, filter, "listColumnValue");
	}

	private void saveTitles(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, List<IOPColumnTitle> listTitles) throws ServiceRequestException
	{
		List<IOPColumnTitle> insertList = new ArrayList<IOPColumnTitle>();
		List<IOPColumnTitle> updateList = new ArrayList<IOPColumnTitle>();
		List<Map<String, Object>> deleteList = new LinkedList<>();
		List<Map<String, Object>> updateHasNextRevision = new LinkedList<>();
		this.getCategorizingTitleData(end1ObjectGuid, end1ReleaseTime, listTitles, insertList, updateList, deleteList, updateHasNextRevision);
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(insertList))
		{
			for (Map<String, Object> param : insertList)
			{
				param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
			}
			sds.insertBatch(IOPColumnTitle.class, insertList, "inserBatchList");
		}
		if (!SetUtils.isNullList(updateList))
		{
			for (Map<String, Object> param : updateList)
			{
				param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
			}
			sds.updateBatch(IOPColumnTitle.class, updateList, "updateBatchList");
		}
		if (!SetUtils.isNullList(deleteList))
		{
			sds.deleteBatch(IOPColumnTitle.class, deleteList, "delete");
		}
		if (!SetUtils.isNullList(updateHasNextRevision))
		{
			sds.updateBatch(IOPColumnTitle.class, updateHasNextRevision, "updateNextRevisionBatchList");
		}
	}

	/**
	 * 数据分类
	 * 
	 * @param end1ObjectGuid
	 * @param end1ReleaseTime
	 * @param listTitles
	 * @param insertList
	 * @param updateList
	 * @param deleteList
	 * @param updateHasNextRevision
	 * @throws ServiceRequestException
	 */
	private void getCategorizingTitleData(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, List<IOPColumnTitle> listTitles, List<IOPColumnTitle> insertList,
			List<IOPColumnTitle> updateList, List<Map<String, Object>> deleteList, List<Map<String, Object>> updateHasNextRevision) throws ServiceRequestException
	{
		Map<Integer, IOPColumnTitle> oldMap = new LinkedHashMap<Integer, IOPColumnTitle>();
		Map<Integer, IOPColumnTitle> newMap = new LinkedHashMap<Integer, IOPColumnTitle>();
		// 比较列头
		List<IOPColumnTitle> oldTitleList = this.listIOPColumnTitle(end1ObjectGuid.getMasterGuid(), end1ReleaseTime);
		if (!SetUtils.isNullList(oldTitleList))
		{
			for (int i = 0; i < oldTitleList.size(); i++)
			{
				oldMap.put(i, oldTitleList.get(i));
			}
		}
		if (!SetUtils.isNullList(listTitles))
		{
			for (int i = 0; i < listTitles.size(); i++)
			{
				IOPColumnTitle newtitle = listTitles.get(i);
				if (!StringUtils.isGuid(newtitle.getGuid()))
				{
					String uid = StringUtils.generateRandomUID(32).toUpperCase();
					newtitle.setGuid(uid);
				}
				newtitle.setMasterGuid(end1ObjectGuid.getMasterGuid());
				newMap.put(i, listTitles.get(i));
			}
		}
		if (!SetUtils.isNullMap(newMap) && SetUtils.isNullMap(oldMap))
		{
			insertList.addAll(listTitles);
		}
		else if (SetUtils.isNullMap(newMap) && !SetUtils.isNullMap(oldMap))
		{
			for (IOPColumnTitle oldTitle : oldTitleList)
			{
				this.setDeleteTitleValue(oldTitle, deleteList, insertList, updateHasNextRevision);
			}
		}
		else
		{
			for (int sequence : oldMap.keySet())
			{
				IOPColumnTitle oldTitle = oldMap.get(sequence);
				IOPColumnTitle newTitle = newMap.get(sequence);
				if (newTitle == null)
				{
					this.setDeleteTitleValue(oldTitle, deleteList, insertList, updateHasNextRevision);
				}
				else
				{
					if (!newTitle.getColumnName().equals(oldTitle.getColumnName()))
					{
						oldTitle.setColumnName(newTitle.getName());
						oldTitle.setColumnTitle(newTitle.getColumnTitle());
						this.setUpdateTitleValue(oldTitle, updateList, insertList, updateHasNextRevision);
						continue;
					}
					if (!oldTitle.getColumnTitle().equals(newTitle.getColumnTitle()))
					{
						oldTitle.setColumnTitle(newTitle.getColumnTitle());
						this.setUpdateTitleValue(oldTitle, updateList, insertList, updateHasNextRevision);
						continue;
					}
				}
			}
			for (int sequence : newMap.keySet())
			{
				IOPColumnTitle oldTitle = oldMap.get(sequence);
				if (oldTitle == null)
				{
					insertList.add(newMap.get(sequence));
				}
			}
		}
	}

	private void setDeleteTitleValue(IOPColumnTitle oldTitle, List<Map<String, Object>> deleteList, List<IOPColumnTitle> insertList,
			List<Map<String, Object>> updateHasNextRevision)
	{
		if (oldTitle.getReleaseTime() == null)
		{
			HashMap<String, Object> guidmap = new HashMap<>();
			guidmap.put("GUID", oldTitle.getGuid());
			deleteList.add(guidmap);
		}
		else
		{
			this.copyTitleValue(oldTitle, insertList, updateHasNextRevision);
		}
	}

	private void setUpdateTitleValue(IOPColumnTitle oldTitle, List<IOPColumnTitle> updateList, List<IOPColumnTitle> insertList, List<Map<String, Object>> updateHasNextRevision)
	{
		if (oldTitle.getReleaseTime() == null)
		{
			updateList.add(oldTitle);
		}
		else
		{
			this.copyTitleValue(oldTitle, insertList, updateHasNextRevision);
		}
	}

	private void copyTitleValue(IOPColumnTitle oldTitle, List<IOPColumnTitle> insertList, List<Map<String, Object>> updateHasNextRevision)
	{
		HashMap<String, Object> guidmap = new HashMap<>();
		guidmap.put("GUID", oldTitle.getGuid());
		updateHasNextRevision.add(guidmap);
		oldTitle.clear(IOPColumnTitle.RELEASETIME);
		oldTitle.clear(IOPColumnTitle.HASNEXTREVISION);
		String uid = StringUtils.generateRandomUID(32).toUpperCase();
		oldTitle.setGuid(uid);
		insertList.add(oldTitle);
	}

	/**
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @return
	 */
	public List<String> listLID(String masterGuid, Date end1ReleaseTime)
	{
		List<IOPColumnValue> listValue = this.listIOPColumnValue(masterGuid, end1ReleaseTime, IOPConfigConstant.LID);
		if (!SetUtils.isNullList(listValue))
		{
			List<String> value = new ArrayList<String>();
			for (IOPColumnValue column : listValue)
			{
				value.add(column.getColumnValue());
			}
			return value;
		}
		return null;
	}
}
