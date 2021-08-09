/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeXMLWriter
 * WangLHB Jul 6, 2011
 */
package dyna.data.service.model.codemodel;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.IconEntry;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.bean.model.ui.ClassificationUIObject;
import dyna.common.bean.model.ui.UIIcon;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.cache.AbstractCacheInfo;
import dyna.common.cache.CacheRefreshListener;
import dyna.common.cache.DynaCacheObserver;
import dyna.common.cache.DynaObserverMediator;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.ClassificationUIField;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.model.DataCacheServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangLHB
 */
@Repository
public class CodeModelServiceStub extends DataCacheServiceStub<CodeModelServiceImpl>
{
	public static final long							serialVersionUID			= 5533284662045268963L;

	private final Map<String, String>					CODEOBJECT_NAME_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());

	private final Map<String, CodeObject>				CODEOBJECT_GUID_MAP			= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, CodeItem>					CODEITEM_GUID_MAP			= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, ClassificationField>		CLF_FIELD_GUID_MAP			= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, ClassificationUIObject>	CLF_UI_GUID_MAP				= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, ClassificationUIField>	CLF_FIELD_UI_FIELD_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());

	private AbstractCacheInfo							cacheInfo					= null;

	protected CodeModelServiceStub()
	{
		super();
		this.cacheInfo = new CodeModelCacheInfo();
		this.cacheInfo.register();
	}

	@Override
	public void loadModel() throws ServiceRequestException
	{
		List<CodeObjectInfo> codeObjectList = DataServer.getSystemDataService().listFromCache(CodeObjectInfo.class, null);
		if (codeObjectList != null)
		{
			for (CodeObjectInfo codeObjectInfo : codeObjectList)
			{
				CODEOBJECT_NAME_GUID_MAP.put(codeObjectInfo.getName(), codeObjectInfo.getGuid());
				CODEOBJECT_GUID_MAP.put(codeObjectInfo.getGuid(), new CodeObject(codeObjectInfo));

				// 构造分类数据
				this.makeClassificationObject(CODEOBJECT_GUID_MAP.get(codeObjectInfo.getGuid()));

				// 把字段的type值设置再UIField上
				this.setUIFieldType(CODEOBJECT_GUID_MAP.get(codeObjectInfo.getGuid()));
			}
		}

		this.sortBySequence();
	}

	protected void reLoadModel() throws ServiceRequestException
	{
		this.relaodModelBaseInfo();

		this.clearAllCache();

		this.loadModel();
	}

	protected void clearClassification()
	{
		for (String codeGuid : CODEOBJECT_GUID_MAP.keySet())
		{
			CodeObject codeObjectInfo = CODEOBJECT_GUID_MAP.get(codeGuid);
			if (codeObjectInfo.isClassification())
			{
				this.clearCodeItem(codeObjectInfo.getCodeDetailList());
			}
		}
	}

	protected CodeObject getCodeObjectByGuid(String codeObjectGuid)
	{
		return CODEOBJECT_GUID_MAP.get(codeObjectGuid);
	}

	protected CodeObject getCodeObject(String codeName)
	{
		String codeGuid = CODEOBJECT_NAME_GUID_MAP.get(codeName);
		return CODEOBJECT_GUID_MAP.get(codeGuid);
	}

	protected List<CodeItemInfo> listDetailCodeItemInfo(String codeGuid, String codeItemGuid)
	{
		List<CodeItemInfo> codeItemInfoList = new ArrayList<>();
		if (StringUtils.isGuid(codeItemGuid))
		{
			CodeItem codeItem = this.getCodeItemByGuid(codeItemGuid);
			List<CodeItem> codeItemList = codeItem.getCodeDetailList();
			if (codeItemList != null)
			{
				codeItemInfoList = codeItemList.stream().map(CodeItem::getInfo).collect(Collectors.toList());
			}
		}
		else if (StringUtils.isGuid(codeGuid))
		{
			CodeObject codeObject = this.getCodeObjectByGuid(codeGuid);
			List<CodeItem> codeItemList = codeObject.getCodeDetailList();
			if (codeItemList != null)
			{
				codeItemInfoList = codeItemList.stream().map(CodeItem::getInfo).collect(Collectors.toList());
			}
		}
		return codeItemInfoList;
	}

	protected CodeItem getCodeItem(String codeName, String itemName)
	{
		CodeObject codeObjectInfo = this.getCodeObject(codeName);
		if (codeObjectInfo != null)
		{
			return codeObjectInfo.getCodeItem(itemName);
		}
		return null;
	}

	protected List<ClassField> listField(String itemGuid)
	{
		CodeItem codeItem = this.getCodeItemByGuid(itemGuid);
		return codeItem == null ? null : codeItem.getFieldList();
	}

	protected List<ClassificationUIObject> listUIObject(String itemGuid)
	{
		CodeItem codeItem = this.getCodeItemByGuid(itemGuid);
		return codeItem == null ? null : codeItem.getClassificationUIObjectList();
	}

	protected List<UIField> listUIField(String uiGuid)
	{
		ClassificationUIObject clfUIObject = CLF_UI_GUID_MAP.get(uiGuid);
		if (clfUIObject != null)
		{
			String itemGuid = clfUIObject.getClassificationGuid();
			CodeItem codeItem = this.getCodeItemByGuid(itemGuid);
			UIObject uiObject = codeItem.getUiObject(uiGuid);
			return uiObject == null ? null : uiObject.getFieldList();
		}

		return null;
	}

	protected CodeItem getCodeItemByGuid(String codeItemGuid)
	{
		return CODEITEM_GUID_MAP.get(codeItemGuid);
	}

	protected List<String> listAllParentClassificationGuid(String classificationItemGuid) throws ServiceRequestException
	{
		List<String> parentGuidList = new ArrayList<>();
		this.listAllParentClassificationGuid(classificationItemGuid, parentGuidList);
		return parentGuidList;
	}

	private void listAllParentClassificationGuid(String classificationItemGuid, List<String> parentGuidList) throws ServiceRequestException
	{
		CodeItem codeItemInfo = this.getCodeItemByGuid(classificationItemGuid);
		if (codeItemInfo != null)
		{
			parentGuidList.add(classificationItemGuid);
			String parentGuid = codeItemInfo.getParentGuid();
			this.listAllParentClassificationGuid(parentGuid, parentGuidList);
		}
	}

	protected List<String> listAllSubClassificationGuid(String classificationItemGuid) throws ServiceRequestException
	{
		List<String> subGuidList = new ArrayList<>();
		CodeItem classificationItem = this.getCodeItemByGuid(classificationItemGuid);
		this.listAllSubClassificationGuid(classificationItem, subGuidList);
		return subGuidList;
	}

	protected List<CodeItem> listAllCodeItem(String codeName)
	{
		List<CodeItem> codeItemList = new ArrayList<>();
		CodeObject codeObjectInfo = this.getCodeObject(codeName);
		List<CodeItem> codeDetailList = codeObjectInfo.getCodeDetailList();
		if (!SetUtils.isNullList(codeDetailList))
		{
			codeItemList.addAll(codeDetailList);
			for (CodeItem codeItemInfo : codeDetailList)
			{
				this.listAllSubCodeItem(codeItemInfo, codeItemList);
			}
		}
		return codeItemList;
	}

	protected List<CodeItem> listAllCodeItem()
	{
		List<CodeItem> codeItemList = new ArrayList<>();
		for (String codeItemGuid : CODEITEM_GUID_MAP.keySet())
		{
			codeItemList.add(CODEITEM_GUID_MAP.get(codeItemGuid));
		}
		return codeItemList;
	}

	private void clearAllCache()
	{
		CODEOBJECT_NAME_GUID_MAP.clear();
		CODEOBJECT_GUID_MAP.clear();
		CLF_FIELD_GUID_MAP.clear();
		CLF_UI_GUID_MAP.clear();
		CLF_FIELD_UI_FIELD_GUID_MAP.clear();
	}

	private void relaodModelBaseInfo()
	{
		DataServer.getSystemDataService().reloadAllCache(CodeObjectInfo.class);
		DataServer.getSystemDataService().reloadAllCache(CodeItemInfo.class);
		DataServer.getSystemDataService().reloadAllCache(ClassificationField.class);
	}

	private void listAllSubCodeItem(CodeItem codeItemInfo, List<CodeItem> codeItemList)
	{
		List<CodeItem> codeDetailList = codeItemInfo.getCodeDetailList();
		if (!SetUtils.isNullList(codeDetailList))
		{
			codeItemList.addAll(codeDetailList);
			for (CodeItem codeItemInfo_ : codeDetailList)
			{
				this.listAllSubCodeItem(codeItemInfo_, codeItemList);
			}
		}
	}

	private void listAllSubClassificationGuid(CodeItem codeItemInfo, List<String> subGuidList) throws ServiceRequestException
	{
		if (codeItemInfo != null)
		{
			subGuidList.add(codeItemInfo.getGuid());
			List<CodeItem> codeDetailList = codeItemInfo.getCodeDetailList();
			if (!SetUtils.isNullList(codeDetailList))
			{
				for (CodeItem detail : codeDetailList)
				{
					this.listAllSubClassificationGuid(detail, subGuidList);
				}
			}
		}
	}

	protected List<CodeObject> listAllCodeObjectList()
	{
		return new ArrayList<>(CODEOBJECT_GUID_MAP.values());
	}

	private void clearCodeItem(List<CodeItem> itemList)
	{
		if (itemList == null || itemList.size() == 0)
		{
			return;
		}

		for (CodeItem codeItem : itemList)
		{
			codeItem.setClassificationFieldList(null);
			codeItem.setIconPath(null);
			codeItem.setShowPreview(null);
			codeItem.setSymbolPath(null);
			codeItem.setShowType(null);
			codeItem.setType(null);

			this.clearCodeItem(codeItem.getCodeDetailList());
		}
	}

	private void setUIFieldType(CodeObject codeObjectInfo)
	{
		if (!codeObjectInfo.isClassification())
		{
			return;
		}

		List<CodeItem> codeDetailList = codeObjectInfo.getCodeDetailList();
		if (!SetUtils.isNullList(codeDetailList))
		{
			codeDetailList.forEach(codeItem -> {
				List<UIObject> uiObjectList = codeItem.getUiObjectList();
				if (!SetUtils.isNullList(uiObjectList))
				{
					uiObjectList.forEach(uiObject -> {
						List<UIField> uiFields = uiObject.getFieldList();
						if (!SetUtils.isNullList(uiFields))
						{
							uiFields.forEach(uiField -> {
								ClassField classField = codeItem.getField(uiField.getName());
								if (classField != null)
								{
									uiField.setType(classField.getType());
								}
							});
						}
					});
				}
			});
		}
	}

	private void sortBySequence()
	{
		for (String codeGuid : CODEOBJECT_GUID_MAP.keySet())
		{
			CodeObject codeObjectInfo = CODEOBJECT_GUID_MAP.get(codeGuid);
			this.sortBySequence(codeObjectInfo.getCodeDetailList());
		}
	}

	public void sortBySequence(List<CodeItem> codeItemList)
	{
		if (!SetUtils.isNullList(codeItemList))
		{
			codeItemList.sort(Comparator.comparingInt(CodeItem::getSequence));
			for (CodeItem item : codeItemList)
			{
				this.sortBySequence(item.getCodeDetailList());
			}
		}
	}

	/**
	 * 构造分类数据
	 *
	 * @param codeMaster
	 * @return
	 */
	private void makeClassificationObject(CodeObject codeMaster) throws ServiceRequestException
	{
		// 做成分类item数据
		List<CodeItemInfo> codeItemInfoList = DataServer.getSystemDataService().listFromCache(CodeItemInfo.class,
				new FieldValueEqualsFilter<>(CodeItemInfo.MASTERGUID, codeMaster.getGuid()));
		Map<String, CodeItem> tempMap = new HashMap<>();
		boolean hasFields = false;
		if (codeItemInfoList != null)
		{
			for (CodeItemInfo detailInfo : codeItemInfoList)
			{
				CodeItem codeItem = new CodeItem(detailInfo);
				codeItem.setMasterName(codeMaster.getName());
				codeItem.setClassification(codeMaster.isClassification());
				tempMap.put(codeItem.getGuid(), codeItem);

				CODEITEM_GUID_MAP.put(detailInfo.getGuid(), codeItem);
				if (codeItem.isClassification())
				{
					List<ClassificationField> fieldList = DataServer.getSystemDataService().listFromCache(ClassificationField.class,
							new FieldValueEqualsFilter<>(ClassificationField.CLASSIFICATIONFK, detailInfo.getGuid()));
					if (fieldList != null)
					{
						fieldList.forEach(field -> CLF_FIELD_GUID_MAP.put(field.getGuid(), field));
					}
					codeItem.setClassificationFieldList(fieldList);
					if (!SetUtils.isNullList(fieldList))
					{
						hasFields = true;
					}
					this.makeUIModel(codeItem);
				}
			}
		}
		codeMaster.setHasFields(hasFields);

		// 构造分类树结构
		Collection<CodeItem> c = tempMap.values();
		for (CodeItem o : c)
		{
			if (StringUtils.isGuid(o.getParentGuid()))
			{
				CodeItem parent = tempMap.get(o.getParentGuid());
				parent.addChild(o);
				o.setParent(parent);
				o.setParentName(parent.getName());
			}
			else
			{
				codeMaster.addChild(o);
			}
		}

		// 构造UI模型继承字段
		for (CodeItem detail : codeMaster.getCodeDetailList())
		{
			this.makeAllUIFields(detail);
		}
	}

	/**
	 * 取得分类图标对象
	 *
	 * @param classification
	 * @return
	 */
	private UIIcon getUIIcon(CodeItem classification)
	{
		UIIcon uiIcon = new UIIcon();
		IconEntry iconEntry = new IconEntry(null, null, null, classification.isShowPreview());
		iconEntry.setSymbolPath(classification.getSymbolPath());
		iconEntry.setThumbnailPath(classification.getIconPath());

		uiIcon.setClassIcon(iconEntry);

		return uiIcon;
	}

	/**
	 * 做成分类UI模型
	 *
	 * @param itemInfo
	 * @return
	 */
	private void makeUIModel(CodeItem itemInfo) throws ServiceRequestException
	{
		List<ClassificationUIObject> uiObjectList = new ArrayList<>();
		List<ClassificationUIInfo> uiInfoList = DataServer.getSystemDataService().listFromCache(ClassificationUIInfo.class,
				new FieldValueEqualsFilter<>(ClassificationUIInfo.CLASSIFICATIONFK, itemInfo.getGuid()));
		if (uiInfoList != null)
		{
			for (ClassificationUIInfo clfUi : uiInfoList)
			{
				ClassificationUIObject uiObject = new ClassificationUIObject(clfUi);
				CLF_UI_GUID_MAP.put(clfUi.getGuid(), uiObject);
				List<ClassificationUIField> uiFieldList = DataServer.getSystemDataService().listFromCache(ClassificationUIField.class,
						new FieldValueEqualsFilter<>(ClassificationUIField.CLASSIFICATIONUIFK, clfUi.getGuid()));
				if (uiFieldList != null)
				{
					uiFieldList.forEach(uiField -> CLF_FIELD_UI_FIELD_GUID_MAP.put(uiField.getGuid(), uiField));
				}
				uiObject.setUiFieldList(uiFieldList);
				uiObjectList.add(uiObject);
			}
		}

		itemInfo.setClassificationUIObjectList(uiObjectList);
		itemInfo.setUiIcon(this.getUIIcon(itemInfo));
	}

	/**
	 * 处理UIModel的继承关系
	 *
	 * @param parentObject
	 */
	private void makeAllUIFields(CodeItem parentObject)
	{
		for (CodeItem child : parentObject.getCodeDetailList())
		{
			if (parentObject.getClassificationUIObjectList() != null)
			{
				for (ClassificationUIObject parentUIObject : parentObject.getClassificationUIObjectList())
				{
					// 如果父节点的UIObject对象在子节点不存在，则直接把父节点UIObject对象继承到子节点
					ClassificationUIObject childUIObject = child.getClassificationUIObject(parentUIObject.getUIName());
					if (childUIObject == null)
					{
						child.addUIObject(parentUIObject);
					}
				}
			}
			this.makeAllUIFields(child);
		}
	}

	class CodeModelCacheInfo extends AbstractCacheInfo
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -5619241309591723818L;

		@Override
		public void register()
		{
			DynaObserverMediator mediator = DynaObserverMediator.getInstance();
			mediator.register(CodeObjectInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<CodeObjectInfo>()));
			mediator.register(CodeItemInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<CodeItemInfo>()));
			mediator.register(ClassificationField.class, new DynaCacheObserver<>(this, new CacheRefreshListener<ClassificationField>()));
			mediator.register(ClassificationUIInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<ClassificationUIInfo>()));
			mediator.register(ClassificationUIField.class, new DynaCacheObserver<>(this, new CacheRefreshListener<ClassificationUIField>()));
		}

		@Override
		public <T extends SystemObject> void addToCache(T data) throws ServiceRequestException
		{
			if (data instanceof CodeObjectInfo)
			{
				CodeObjectInfo codeObjectInfo = (CodeObjectInfo) data;
				CODEOBJECT_GUID_MAP.put(codeObjectInfo.getGuid(), new CodeObject(codeObjectInfo));
				CODEOBJECT_NAME_GUID_MAP.put(codeObjectInfo.getName(), codeObjectInfo.getGuid());
			}
			else if (data instanceof CodeItemInfo)
			{
				CodeItemInfo codeItemInfo = (CodeItemInfo) data;
				CODEITEM_GUID_MAP.put(codeItemInfo.getGuid(), new CodeItem(codeItemInfo));

				CodeItem parent = CODEITEM_GUID_MAP.get(codeItemInfo.getParentGuid());
				if (parent != null)
				{
					parent.addChild(CODEITEM_GUID_MAP.get(codeItemInfo.getGuid()));
				}
				else
				{
					CodeObject codeObject = CODEOBJECT_GUID_MAP.get(codeItemInfo.getCodeGuid());
					codeObject.addChild(CODEITEM_GUID_MAP.get(codeItemInfo.getGuid()));
				}

				CodeItem codeItem = CODEITEM_GUID_MAP.get(codeItemInfo.getGuid());
				codeItem.setUiIcon(getUIIcon(codeItem));
			}
			else if (data instanceof ClassificationField)
			{
				ClassificationField clfField = (ClassificationField) data;
				CODEITEM_GUID_MAP.get(clfField.getClassificationGuid()).addField(clfField);
				CLF_FIELD_GUID_MAP.put(clfField.getGuid(), clfField);
			}
			else if (data instanceof ClassificationUIInfo)
			{
				ClassificationUIInfo clfUI = (ClassificationUIInfo) data;
				CLF_UI_GUID_MAP.put(clfUI.getGuid(), new ClassificationUIObject(clfUI));
				CODEITEM_GUID_MAP.get(clfUI.getClassificationGuid()).addUIObject(CLF_UI_GUID_MAP.get(clfUI.getGuid()));
			}
			else if (data instanceof ClassificationUIField)
			{
				ClassificationUIField clfUIField = (ClassificationUIField) data;
				ClassificationUIObject uiObject = CLF_UI_GUID_MAP.get(clfUIField.getUIGuid());
				uiObject.addUIField(clfUIField);
				CLF_FIELD_UI_FIELD_GUID_MAP.put(clfUIField.getGuid(), clfUIField);
			}
		}

		@Override
		public <T extends SystemObject> void removeFromCache(T data) throws ServiceRequestException
		{
			if (data instanceof CodeObjectInfo)
			{
				CodeObjectInfo codeObjectInfo = (CodeObjectInfo) data;
				CODEOBJECT_GUID_MAP.remove(codeObjectInfo.getGuid());
				CODEOBJECT_NAME_GUID_MAP.remove(codeObjectInfo.getName());
			}
			else if (data instanceof CodeItemInfo)
			{
				CodeItemInfo codeItemInfo = (CodeItemInfo) data;
				if (StringUtils.isGuid(codeItemInfo.getParentGuid()))
				{
					CodeItem codeItem = CODEITEM_GUID_MAP.get(codeItemInfo.getParentGuid());
					// 一级缓存删除子项不按照树的结构删除，有父项，但可能已经删除
					if (codeItem != null)
					{
						codeItem.getCodeDetailList().remove(CODEITEM_GUID_MAP.get(codeItemInfo.getGuid()));
					}
				}
				else
				{
					CodeObject codeObject = CODEOBJECT_GUID_MAP.get(codeItemInfo.getCodeGuid());
					if (codeObject != null)
					{
						codeObject.getCodeDetailList().remove(CODEITEM_GUID_MAP.get(codeItemInfo.getGuid()));
					}
				}
				CODEITEM_GUID_MAP.remove(codeItemInfo.getGuid());
			}
			else if (data instanceof ClassificationField)
			{
				ClassificationField clfField = (ClassificationField) data;
				CodeItem codeItem = CODEITEM_GUID_MAP.get(clfField.getClassificationGuid());
				if (codeItem != null)
				{
					codeItem.removeField(clfField);
				}
				CLF_FIELD_GUID_MAP.remove(clfField.getGuid());
			}
			else if (data instanceof ClassificationUIInfo)
			{
				ClassificationUIInfo clfUI = (ClassificationUIInfo) data;
				CodeItem codeItem = CODEITEM_GUID_MAP.get(clfUI.getClassificationGuid());
				if (codeItem != null)
				{
					codeItem.removeUIObject(CLF_UI_GUID_MAP.get(clfUI.getGuid()));
				}
				CLF_UI_GUID_MAP.remove(clfUI.getGuid());
			}
			else if (data instanceof ClassificationUIField)
			{
				ClassificationUIField clfUIField = (ClassificationUIField) data;
				ClassificationUIObject uiObject = CLF_UI_GUID_MAP.get(clfUIField.getUIGuid());
				if (uiObject != null)
				{
					uiObject.removeUIField(CLF_FIELD_UI_FIELD_GUID_MAP.get(clfUIField.getGuid()));
				}
				CLF_FIELD_UI_FIELD_GUID_MAP.remove(clfUIField.getGuid());
			}
		}

		@Override
		public <T extends SystemObject> void updateToCache(T data) throws ServiceRequestException
		{
			if (data instanceof CodeObjectInfo)
			{
				CodeObjectInfo codeObjectInfo = (CodeObjectInfo) data;
				CODEOBJECT_GUID_MAP.get(codeObjectInfo.getGuid()).getInfo().putAll(codeObjectInfo);
			}
			else if (data instanceof CodeItemInfo)
			{
				CodeItemInfo codeItemInfo = (CodeItemInfo) data;
				CODEITEM_GUID_MAP.get(codeItemInfo.getGuid()).getInfo().putAll(codeItemInfo);
				CodeItem codeItem = CODEITEM_GUID_MAP.get(codeItemInfo.getGuid());
				codeItem.setUiIcon(getUIIcon(codeItem));
			}
			else if (data instanceof ClassificationField)
			{
				ClassificationField clfField = (ClassificationField) data;
				CLF_FIELD_GUID_MAP.get(clfField.getGuid()).putAll(clfField);
			}
			else if (data instanceof ClassificationUIInfo)
			{
				ClassificationUIInfo clfUI = (ClassificationUIInfo) data;
				ClassificationUIObject uiObject = CLF_UI_GUID_MAP.get(clfUI.getGuid());
				uiObject.getInfo().putAll(clfUI);
			}
			else if (data instanceof ClassificationUIField)
			{
				ClassificationUIField clfUIField = (ClassificationUIField) data;
				ClassificationUIField uiField = CLF_FIELD_UI_FIELD_GUID_MAP.get(clfUIField.getGuid());
				uiField.putAll(clfUIField);
			}
		}
	}
}
