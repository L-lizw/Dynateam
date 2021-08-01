package dyna.data.service.sync;

import dyna.common.bean.model.ReferenceCode;
import dyna.common.bean.model.ReferenceField;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.dbcommon.exception.DynaDataSqlException;

import java.io.File;
import java.util.*;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class ClassXMLLoadStub extends DSAbstractServiceStub<SyncModelServiceImpl>
{
	protected ClassXMLLoadStub(DataServerContext context, SyncModelServiceImpl service)
	{
		super(context, service);
	}

	protected void loadXMLClassObject(ModelXMLCache xmlModel, String modelPath)
	{
		DynaLogger.println("\tLoading class");
		String builtPath = EnvUtils.getConfRootPath() + "conf";
		HashMap<String, ClassObject> classObjectMap = new HashMap<>();
		try
		{
			this.loadClassModel(builtPath + "/dynaobjects.xml", true, classObjectMap);

			this.loadClassModel(modelPath + "/class-models", false, classObjectMap);

			this.buildInterface(classObjectMap);

			Map<String, ClassObject> classObjectMap_ = DataServer.getClassModelService().buildClassModelRelation(classObjectMap);
			xmlModel.setClassObjectMap(classObjectMap_);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataSqlException("Class Modle read error", e);
		}
	}

	private void buildInterface(HashMap<String, ClassObject> classObjectMap)
	{
		for (ClassObject classObject : classObjectMap.values())
		{
			if (classObject.getInterfaces() != null)
			{
				String[] interfaceNames = StringUtils.splitString(classObject.getInterfaces());
				if (interfaceNames != null)
				{
					for (String interfaceName : interfaceNames)
					{
						classObject.addInterface(interfaceName);
					}
				}
			}

			if (!StringUtils.isNullString(classObject.getSuperInterface()))
			{
				String[] interfaceNames = StringUtils.splitString(classObject.getSuperInterface());
				if (interfaceNames != null)
				{
					for (String interfaceName : interfaceNames)
					{
						classObject.addInterface(interfaceName);
					}
				}
			}
		}
	}

	/**
	 * load class xml definition
	 *
	 * @param objectModelPath
	 * @param isBuiltin
	 */
	private void loadClassModel(String objectModelPath, boolean isBuiltin, HashMap<String, ClassObject> classObjectMap)
	{
		File classModelFile = new File(objectModelPath);

		if (classModelFile.isDirectory())
		{
			File[] files = classModelFile.listFiles();
			if (files == null)
			{
				return;
			}

			for (File file : files)
			{
				this.loadClassModel(file.getAbsolutePath(), isBuiltin, classObjectMap);
			}
		}
		else if (objectModelPath.endsWith(".xml"))
		{
			final ConfigLoaderDefaultImpl confLoader = new ConfigLoaderDefaultImpl();
			confLoader.setConfigFile(new File(objectModelPath));
			//TODO
//			ConfigurableKVElementImpl modelElement = (confLoader.load()).iterator("object-models").next();
			ConfigurableKVElementImpl modelElement = null;

			for (Iterator<ConfigurableKVElementImpl> objectIterator = modelElement.iterator("class-model.class-object"); objectIterator.hasNext();)
			{
				ConfigurableKVElementImpl objectElement = objectIterator.next();
				String className = objectElement.getAttributeValue("name");
				ClassObject classObject = classObjectMap.get(className);

				if (classObject == null)
				{
					classObject = new ClassObject();

					classObject.setName(className);
					classObject.setSuperclass(objectElement.getAttributeValue("superclass"));
					classObject.setCreateTable("true".equalsIgnoreCase(objectElement.getAttributeValue("createtable")));
					classObject.setIdUnique("true".equalsIgnoreCase(objectElement.getAttributeValue("idunique")));
					if (objectElement.getAttributeValue("interface") != null)
					{
						classObject.setInterfaces(objectElement.getAttributeValue("interface"));
						String[] interfaceNames = StringUtils.splitString(objectElement.getAttributeValue("interface"));
						if (interfaceNames != null)
						{
							for (String interfaceName : interfaceNames)
							{
								classObject.addInterface(interfaceName);

								if (!classObject.isCreateTable() && DataServer.getInterfaceModelService().getInterface(ModelInterfaceEnum.typeof(interfaceName)) != null)
								{
									InterfaceObject interfaceObject = DataServer.getInterfaceModelService().getInterface(ModelInterfaceEnum.typeof(interfaceName));
									if (interfaceObject.isCreateTable())
									{
										classObject.setCreateTable(true);
									}
								}
							}
						}
					}

					classObject.setAbstract("true".equalsIgnoreCase(objectElement.getAttributeValue("abstract")));
					classObject.setFinal("true".equalsIgnoreCase(objectElement.getAttributeValue("final")));
					classObject.setIcon(objectElement.getAttributeValue("icon"));
					classObject.setIcon32(objectElement.getAttributeValue("icon32"));
					String showPreview = objectElement.getAttributeValue("showpreview");
					if (StringUtils.isNullString(showPreview))
					{
						classObject.setShowPreview(true);
					}
					else
					{
						classObject.setShowPreview(Boolean.parseBoolean(showPreview));
					}

					classObject.setInstanceString(objectElement.getAttributeValue("instancestring"));
					classObject.setLifecycle(objectElement.getAttributeValue("lifecycle"));
					classObject.setDescription(objectElement.getAttributeValue("description"));
					classObject.setIterationLimit(objectElement.getAttributeValue("iterationlimit"));
					classObject.setBuiltin(isBuiltin);
				}
				else
				{
					if (objectElement.getAttributeValue("interface") != null)
					{
						String[] interfaceNames = StringUtils.splitString(objectElement.getAttributeValue("interface"));
						if (interfaceNames != null)
						{
							for (String interfaceName : interfaceNames)
							{
								classObject.addInterface(interfaceName);
							}
						}
					}
					if (objectElement.getAttributeValue("instancestring") != null)
					{
						classObject.setInstanceString(objectElement.getAttributeValue("instancestring"));
					}
					String lifecycle = objectElement.getAttributeValue("lifecycle");
					if (lifecycle != null)
					{
						classObject.setLifecycle(lifecycle);
					}
					String classification = objectElement.getAttributeValue("classification");
					if (classification != null)
					{
						classObject.setClassification(objectElement.getAttributeValue("classification"));
					}
				}

				this.loadClassFieldList(classObject, objectElement.iterator("fields.field"), isBuiltin);

				ClassField field = classObject.getField(SystemClassFieldEnum.CLASSIFICATION.getName());
				classObject.setClassification(field == null ? null : field.getTypeValue());

				classObjectMap.put(classObject.getName(), classObject);
			}
		}
	}

	private void loadClassFieldList(ClassObject classObject, Iterator<ConfigurableKVElementImpl> iterator, boolean isBuiltin)
	{
		// 接口字段不能被重载
		Map<String, ClassField> fieldMap = new HashMap<>();
		ModelInterfaceEnum[] interfaceEnums = ModelInterfaceEnum.values();
		for (ModelInterfaceEnum interfaceEnum : interfaceEnums)
		{
			if (classObject.hasInterface(interfaceEnum))
			{
				List<ClassField> interfaceFieldList = DataServer.getInterfaceModelService().listClassFieldOfInterface(interfaceEnum);
				if (!SetUtils.isNullList(interfaceFieldList))
				{
					for (ClassField field : interfaceFieldList)
					{
						fieldMap.putIfAbsent(field.getName(), field);
						// classObject.addField(field);
					}
				}
			}
		}

		while (iterator.hasNext())
		{
			ConfigurableKVElementImpl fieldElement = iterator.next();
			String name = fieldElement.getAttributeValue("name");

			if (classObject.getField(name) != null)
			{
				continue;
			}

			ClassField field = this.readFieldFromXML(fieldElement, isBuiltin, fieldMap);

			this.loadRefCode(field, fieldElement);

			classObject.addField(field);
		}
	}

	private ClassField readFieldFromXML(ConfigurableKVElementImpl fieldElement, boolean isBuiltin, Map<String, ClassField> fieldMap)
	{
		ClassField interClassField = null;
		String name = fieldElement.getAttributeValue("name");
		if (fieldMap.get(name) != null)
		{
			interClassField = fieldMap.get(name);
		}

		ClassField field = new ClassField();
		field.setBuiltin(isBuiltin);
		field.setName(name);
		field.setDescription(fieldElement.getAttributeValue("description"));
		field.setMandatory("true".equalsIgnoreCase(fieldElement.getAttributeValue("mandatory")));
		field.setRollback("true".equalsIgnoreCase(fieldElement.getAttributeValue("rollback")));
		field.setSystem("true".equalsIgnoreCase(fieldElement.getAttributeValue("system")));

		if (fieldElement.getAttributeValue("type") != null)
		{
			field.setType(FieldTypeEnum.valueOf(fieldElement.getAttributeValue("type").toUpperCase()));
		}
		if (interClassField != null)
		{
			// 接口字段的类型不允许修改
			field.setType(interClassField.getType());
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

		field.setValidityRegex(fieldElement.getAttributeValue("validity"));
		field.setTitle(fieldElement.getAttributeValue("title"));

		return field;
	}

	private void loadRefCode(ClassField field, ConfigurableKVElementImpl fieldElement)
	{
		String relationfield = fieldElement.getAttributeValue("relationfield");
		field.setCoderefField(relationfield);
		String[] relationfields = StringUtils.splitString(relationfield);

		if (relationfields != null)
		{
			field.setRelationFieldList(new ArrayList<>((Arrays.asList(relationfields))));

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
