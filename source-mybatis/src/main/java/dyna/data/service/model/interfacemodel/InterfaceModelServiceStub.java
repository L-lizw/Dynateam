package dyna.data.service.model.interfacemodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dyna.common.bean.model.ReferenceCode;
import dyna.common.bean.model.ReferenceField;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.TableIndexTypeEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sync.bean.TableIndexModel;
import dyna.data.service.sync.bean.TableIndexObject;

public class InterfaceModelServiceStub extends DSAbstractServiceStub<InterfaceModelServiceImpl>
{
	private final Map<String, List<ClassField>>			INTERFACE_NAME_FIELDNAME_MAP	= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, InterfaceObject>			INTERFACE_NAME_MAP				= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<String>>				INTERFACE_CHILD_NAME_MAP		= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<TableIndexModel>>	INTERFACE_NAME_INDEX_MAP		= Collections.synchronizedMap(new HashMap<>());

	private static final String							INTERFACE_MODEL_PATH			= EnvUtils.getConfRootPath() + "conf/interface-model.xml";

	protected InterfaceModelServiceStub(DataServerContext context, InterfaceModelServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 装载接口
	 */
	public void loadModel()
	{
		File interfaceModelFile = new File(INTERFACE_MODEL_PATH);

		ConfigLoaderDefaultImpl confLoader = new ConfigLoaderDefaultImpl();
		confLoader.setConfigFile(interfaceModelFile);
		Iterator<ConfigurableKVElementImpl> interfaceElementIterator = (confLoader.load()).iterator("interface-model.interface");

		while (interfaceElementIterator.hasNext())
		{
			ConfigurableKVElementImpl interfaceElement = interfaceElementIterator.next();
			String interfaceName = interfaceElement.getAttributeValue("name");

			InterfaceObject interfaceObject = new InterfaceObject();
			interfaceObject.setName(interfaceName);
			interfaceObject.setSystem("true".equalsIgnoreCase(interfaceElement.getAttributeValue("system")));
			interfaceObject.setSingle("true".equalsIgnoreCase(interfaceElement.getAttributeValue("single")));
			interfaceObject.setCreateTable("true".equalsIgnoreCase(interfaceElement.getAttributeValue("createtable")));
			interfaceObject.setParentInterfaces(interfaceElement.getAttributeValue("extend"));

			List<ClassField> classFieldList = new ArrayList<>();
			Iterator<ConfigurableKVElementImpl> fieldElementIter = interfaceElement.iterator("fields.field");
			while (fieldElementIter.hasNext())
			{
				ConfigurableKVElementImpl fieldElement = fieldElementIter.next();
				ClassField classField = this.loadClassField(fieldElement);
				if (StringUtils.isNullString(classField.getColumnName()))
				{
					if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(classField.getName()))
					{
						classField.setColumnName("MD_ID");
					}
					else if (SystemClassFieldEnum.NAME.getName().equalsIgnoreCase(classField.getName()))
					{
						classField.setColumnName("MD_NAME");
					}
					else if (SystemClassFieldEnum.REPEAT.getName().equalsIgnoreCase(classField.getName()))
					{
						classField.setColumnName("REPEATVALUE");
					}
					else
					{
						if (classField.isSystem())
						{
							classField.setColumnName(classField.getName().substring(0, classField.getName().length() - 1).toUpperCase());
						}
						else
						{
							classField.setColumnName(classField.getName().toUpperCase());
						}
					}
				}
				classField.setTableIndex("0");
				classFieldList.add(classField);
			}
			INTERFACE_NAME_FIELDNAME_MAP.put(interfaceName, classFieldList);

			List<TableIndexModel> indexModelList = new ArrayList<>();
			Iterator<ConfigurableKVElementImpl> indexElementIter = interfaceElement.iterator("index-model");
			while (indexElementIter.hasNext())
			{
				ConfigurableKVElementImpl indexElement = indexElementIter.next();
				String type = indexElement.getAttributeValue("type");
				String unique = indexElement.getAttributeValue("unique");

				int index = 1;
				List<TableIndexObject> indexObjectList = new ArrayList<TableIndexObject>();
				Iterator<ConfigurableKVElementImpl> indexObjectElementIter = indexElement.iterator("index-object");
				while (indexObjectElementIter.hasNext())
				{
					ConfigurableKVElementImpl indexObjectElement = indexObjectElementIter.next();
					String name = indexObjectElement.getAttributeValue("name");

					TableIndexObject indexObject = new TableIndexObject();
					indexObject.setName(name);
					indexObject.setSequence(String.valueOf(index++));
					indexObjectList.add(indexObject);
				}

				TableIndexModel indexModel = new TableIndexModel();
				indexModel.setType(type == null ? TableIndexTypeEnum.INDEX.name() : type);
				indexModel.setTableIndexObjectList(indexObjectList);
				indexModel.setUnique("true".equalsIgnoreCase(unique));
				indexModelList.add(indexModel);
			}
			INTERFACE_NAME_INDEX_MAP.put(interfaceName, indexModelList);

			INTERFACE_NAME_MAP.put(interfaceName, interfaceObject);
		}

		// 处理接口继承关系
		for (Entry<String, InterfaceObject> entry : INTERFACE_NAME_MAP.entrySet())
		{
			this.handleInterfaceInherited(entry.getKey(), entry.getValue());
		}
	}

	protected InterfaceObject getInterface(ModelInterfaceEnum interfaceEnum)
	{
		return INTERFACE_NAME_MAP.get(interfaceEnum.name()) == null ? null : INTERFACE_NAME_MAP.get(interfaceEnum.name()).clone();
	}

	protected List<InterfaceObject> listSubInterface(ModelInterfaceEnum interfaceEnum)
	{
		if (INTERFACE_NAME_MAP.get(interfaceEnum.name()) == null)
		{
			return null;
		}

		List<InterfaceObject> childInterfaceList = new ArrayList<>();
		List<String> childInterfaceNameList = INTERFACE_CHILD_NAME_MAP.get(interfaceEnum.name());
		if (!SetUtils.isNullList(childInterfaceNameList))
		{
			childInterfaceNameList.forEach(childInterfaceName -> {
				ModelInterfaceEnum modelInterfaceEnum = ModelInterfaceEnum.typeof(childInterfaceName);
				if (modelInterfaceEnum != null)
				{
					InterfaceObject childInterface = getInterface(modelInterfaceEnum);
					if (childInterface != null)
					{
						childInterfaceList.add(childInterface);
					}
				}
			});
		}
		return childInterfaceList;
	}

	protected Map<String, InterfaceObject> getInterfaceObjectMap()
	{
		Map<String, InterfaceObject> map = new HashMap<>();
		INTERFACE_NAME_MAP.forEach((itfName, itf) -> map.put(itfName, itf.clone()));
		return Collections.unmodifiableMap(map);
	}

	protected List<ClassField> listClassFieldOfInterface(String interfaceName)
	{
		List<ClassField> list = new ArrayList<>();
		if (!SetUtils.isNullList(INTERFACE_NAME_FIELDNAME_MAP.get(interfaceName)))
		{
			INTERFACE_NAME_FIELDNAME_MAP.get(interfaceName).forEach(field -> list.add(field.clone()));
		}
		return Collections.unmodifiableList(list);
	}

	protected Map<String, List<ClassField>> getInterfaceFieldMap()
	{
		Map<String, List<ClassField>> map = new HashMap<>();
		INTERFACE_NAME_FIELDNAME_MAP.forEach((itfName, fieldList) -> {
			List<ClassField> fieldList_ = new ArrayList<>();
			if (!SetUtils.isNullList(fieldList))
			{
				fieldList.forEach(field -> fieldList_.add(field.clone()));
			}
			map.put(itfName, fieldList_);
		});
		return Collections.unmodifiableMap(map);
	}

	protected Map<String, List<TableIndexModel>> getInterfaceIndexMap()
	{
		Map<String, List<TableIndexModel>> map = new HashMap<>();
		INTERFACE_NAME_INDEX_MAP.forEach((itfName, indexModelList) -> {
			List<TableIndexModel> list = new ArrayList<>();
			if (!SetUtils.isNullList(indexModelList))
			{
				indexModelList.forEach(indexModel -> {
					try
					{
						list.add(indexModel.clone());
					}
					catch (CloneNotSupportedException e)
					{
						e.printStackTrace();
					}
				});
			}
			map.put(itfName, list);
		});
		return Collections.unmodifiableMap(map);
	}

	/**
	 * @param interfaceName
	 */
	private void handleInterfaceInherited(String interfaceName, InterfaceObject interfaceObject)
	{
		if (!interfaceObject.isHandled())
		{
			return;
		}

		interfaceObject.setHandled(true);
		List<ClassField> classFieldList = INTERFACE_NAME_FIELDNAME_MAP.get(interfaceName);

		String parentInterface = interfaceObject.getParentInterfaces();
		if (!StringUtils.isNullString(parentInterface))
		{
			String[] parentInterfaceNames = StringUtils.splitString(parentInterface);
			for (String parentInterfaceName : parentInterfaceNames)
			{
				InterfaceObject parentInterfaceInformation = INTERFACE_NAME_MAP.get(parentInterfaceName);
				if (parentInterfaceInformation == null)
				{
					return;
				}

				INTERFACE_CHILD_NAME_MAP.computeIfAbsent(parentInterfaceName, k -> new ArrayList<>());
				if (!INTERFACE_CHILD_NAME_MAP.get(parentInterfaceName).contains(interfaceObject.getName()))
				{
					INTERFACE_CHILD_NAME_MAP.get(parentInterfaceName).add(interfaceObject.getName());
				}

				this.handleInterfaceInherited(parentInterfaceName, parentInterfaceInformation);

				if (parentInterfaceInformation.isSingle())
				{
					interfaceObject.setSingle(true);
				}

				classFieldList.addAll(INTERFACE_NAME_FIELDNAME_MAP.get(parentInterfaceName));
			}
		}
	}

	/**
	 * 装载接口中的ClassField
	 *
	 * @param fieldElement
	 * @return
	 */
	private ClassField loadClassField(ConfigurableKVElementImpl fieldElement)
	{
		boolean isSystem = "true".equalsIgnoreCase(fieldElement.getAttributeValue("system"));

		String name = fieldElement.getAttributeValue("name");
		ClassField field = new ClassField();
		if (isSystem)
		{
			field.setSystem(true);
		}
		field.setBuiltin(true);
		field.setName(name);
		field.setDescription(fieldElement.getAttributeValue("description"));
		field.setMandatory("true".equalsIgnoreCase(fieldElement.getAttributeValue("mandatory")));

		boolean isRollBack = true;
		if (fieldElement.getAttributeValue("rollback") != null)
		{
			isRollBack = "true".equalsIgnoreCase(fieldElement.getAttributeValue("rollback"));
		}
		field.setRollback(isRollBack);
		if (fieldElement.getAttributeValue("type") != null)
		{
			field.setType(FieldTypeEnum.valueOf(fieldElement.getAttributeValue("type").toUpperCase()));
		}
		field.setTypeValue(fieldElement.getAttributeValue("typevalue"));

		if (!StringUtils.isNullString(fieldElement.getElementValue("defaultvalueex")))
		{
			field.setDefaultValue(fieldElement.getElementValue("defaultvalueex"));
		}
		else
		{
			field.setDefaultValue(fieldElement.getAttributeValue("defaultvalue"));
		}

		if (field.getType() == FieldTypeEnum.STRING && StringUtils.isNullString(fieldElement.getAttributeValue("size")))
		{
			field.setFieldSize(String.valueOf(ClassField.defaultCharLength));
		}
		else
		{
			field.setFieldSize(fieldElement.getAttributeValue("size"));
		}

		field.setValueScope(fieldElement.getAttributeValue("valuescope"));

		field.setValidityRegex(fieldElement.getAttributeValue("validity"));
		field.setTitle(fieldElement.getAttributeValue("title"));

		this.loadRefCode(field, fieldElement);
		return field;
	}

	private void loadRefCode(ClassField field, ConfigurableKVElementImpl fieldElement)
	{
		String relationfield = fieldElement.getAttributeValue("relationfield");
		field.setCoderefField(relationfield);
		String[] relationfields = StringUtils.splitString(relationfield);

		if (relationfields != null)
		{
			Iterator<ConfigurableKVElementImpl> refcodeElementIter = fieldElement.iterator("refcode");
			List<ReferenceCode> refCodeList = new ArrayList<>();
			while (refcodeElementIter.hasNext())
			{
				ConfigurableKVElementImpl refCodeElement = refcodeElementIter.next();
				ReferenceCode refCode = new ReferenceCode();
				refCode.setCodeName(refCodeElement.getAttributeValue("name"));

				Iterator<ConfigurableKVElementImpl> refFieldElementIter = refCodeElement.iterator("reffield");
				List<ReferenceField> refFieldList = new ArrayList<>();
				while (refFieldElementIter.hasNext())
				{
					ConfigurableKVElementImpl refFieldElement = refFieldElementIter.next();
					ReferenceField refField = new ReferenceField();
					refField.setFieldName(refFieldElement.getAttributeValue("fieldname"));
					refField.setItemName(refFieldElement.getAttributeValue("itemname"));
					refFieldList.add(refField);
				}

				refCode.setRefFieldList(refFieldList);
				refCodeList.add(refCode);
			}

			field.setReferenceCodeList(refCodeList);
		}
	}
}
