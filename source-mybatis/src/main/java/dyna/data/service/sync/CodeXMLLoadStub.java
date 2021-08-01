package dyna.data.service.sync;

import dyna.data.service.sync.bean.ModelXMLCache;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.CodeDisplayEnum;
import dyna.common.systemenum.CodeTypeEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;

import java.io.File;
import java.util.*;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class CodeXMLLoadStub extends DSAbstractServiceStub<SyncModelServiceImpl>
{
	protected CodeXMLLoadStub(DataServerContext context, SyncModelServiceImpl service)
	{
		super(context, service);
	}

	protected void loadXMLCodeModel(ModelXMLCache xmlModel, String modelConfigPath)
	{
		xmlModel.setCodeObjectMap(new HashMap<>());

		Map<String, CodeObject> codeObjectModelMap = this.loadXMLCode(modelConfigPath);
		Map<String, CodeObject> classificationObjectModelMap = this.loadXMLClassification(modelConfigPath);

		xmlModel.getCodeObjectMap().putAll(codeObjectModelMap);
		xmlModel.getCodeObjectMap().putAll(classificationObjectModelMap);
	}

	private Map<String, CodeObject> loadXMLCode(String modelConfigPath)
	{
		Map<String, CodeObject> codeObjectModelMap = new HashMap<>();
		Map<String, CodeItem> codeItemModelMap = new HashMap<>();

		// 加载分类
		File folder = new File(modelConfigPath + "/code-models");
		if (folder.exists())
		{
			File[] xmlFiles = folder.listFiles(file -> file.getName().toLowerCase().endsWith(".xml"));
			if (xmlFiles != null && xmlFiles.length > 0)
			{
				for (File codeModelXML : xmlFiles)
				{
					this.loadCodeModel(codeModelXML.getAbsolutePath(), codeObjectModelMap, codeItemModelMap);
				}
			}
		}

		for (String key : codeItemModelMap.keySet())
		{
			CodeItem codeItem = codeItemModelMap.get(key);
			// parentName为空时，是codeObject
			if (StringUtils.isNullString(codeItem.getParentName()))
			{
				DynaLogger.debug("do nothing.");
			}
			// 第一阶分类子阶
			else if (!StringUtils.isNullString(codeItem.getParentName()) && codeItem.getMasterName().equals(codeItem.getParentName()))
			{
				codeObjectModelMap.get(codeItem.getMasterName()).addChild(codeItem);
			}
			// 其他分类子阶
			else
			{
				codeItemModelMap.get(codeItem.getMasterName() + "_" + codeItem.getParentName()).addChild(codeItem);
			}
		}
		return codeObjectModelMap;
	}

	private Map<String, CodeObject> loadXMLClassification(String modelConfigPath)
	{
		Map<String, CodeObject> codeObjectModelMap = new HashMap<>();
		Map<String, CodeItem> codeItemModelMap = new HashMap<>();

		// 加载分类
		File folder = new File(modelConfigPath + "/classification-models");
		if (folder.exists())
		{
			File[] xmlFiles = folder.listFiles(file -> file.getName().toLowerCase().endsWith(".xml"));
			if (xmlFiles != null && xmlFiles.length > 0)
			{
				for (File codeModelXML : xmlFiles)
				{
					this.loadClassificationModel(codeModelXML.getAbsolutePath(), codeObjectModelMap, codeItemModelMap);
				}
			}
		}

		for (String key : codeItemModelMap.keySet())
		{
			CodeItem codeItem = codeItemModelMap.get(key);
			// parentName为空时，是codeObject
			if (StringUtils.isNullString(codeItem.getParentName()))
			{
				DynaLogger.debug("do nothing.");
			}
			// 第一阶分类子阶
			else if (!StringUtils.isNullString(codeItem.getParentName()) && codeItem.getMasterName().equals(codeItem.getParentName()))
			{
				codeObjectModelMap.get(codeItem.getMasterName()).addChild(codeItem);
			}
			// 其他分类子阶
			else
			{
				codeItemModelMap.get(codeItem.getMasterName() + "_" + codeItem.getParentName()).addChild(codeItem);
			}
		}

		// 把继承的字段添加到子节点上
		for (String codeName : codeObjectModelMap.keySet())
		{
			CodeObject classification = codeObjectModelMap.get(codeName);
			this.makeAllFields(new ArrayList<>(), classification, null);
		}
		return codeObjectModelMap;
	}

	private void loadCodeModel(String objectModelPath, Map<String, CodeObject> codeObjectModelMap, Map<String, CodeItem> codeItemModelMap)
	{
		final ConfigLoaderDefaultImpl confLoader = new ConfigLoaderDefaultImpl();
		confLoader.setConfigFile(new File(objectModelPath));
		confLoader.load();
		ConfigurableKVElementImpl modelElement = confLoader.getConfigurable();

		String groupName = modelElement.getAttributeValue("code-item.name");
		String masterCode = modelElement.getAttributeValue("code-item.mastername");
		String parentCode = modelElement.getAttributeValue("code-item.parentname");

		if (StringUtils.isNullString(parentCode))
		{
			CodeObject codeObject = new CodeObject();
			codeObject.setName(groupName);
			codeObject.setTitle(modelElement.getAttributeValue("code-item.title"));
			codeObject.setDescription(modelElement.getAttributeValue("code-item.description"));

			String type = modelElement.getAttributeValue("code-item.type");
			codeObject.setType(type == null || CodeTypeEnum.getEnum(type) == null ? CodeTypeEnum.LIST : CodeTypeEnum.getEnum(type));
			String showType = modelElement.getAttributeValue("code-item.showtype");
			codeObject.setShowType(showType == null || CodeDisplayEnum.getEnum(showType) == null ? CodeDisplayEnum.DROPDOWN : CodeDisplayEnum.getEnum(showType));
			codeObject.setClassification(false);
			codeObjectModelMap.put(codeObject.getName(), codeObject);
		}
		else
		{
			CodeItem codeItem = new CodeItem();
			codeItem.setName(groupName);
			codeItem.setTitle(modelElement.getAttributeValue("code-item.title"));
			codeItem.setDescription(modelElement.getAttributeValue("code-item.description"));
			codeItem.setMasterName(masterCode);
			codeItem.setParentName(parentCode);
			codeItem.setClassification(false);
			String sequence = modelElement.getAttributeValue("code-item.sequence");
			codeItem.setSequence(Integer.parseInt(sequence));
			codeItem.setCode(modelElement.getAttributeValue("code-item.code"));

			codeItemModelMap.put(codeItem.getMasterName() + "_" + codeItem.getName(), codeItem);
		}
	}

	private void loadClassificationModel(String objectModelPath, Map<String, CodeObject> codeObjectModelMap, Map<String, CodeItem> codeItemModelMap)
	{
		final ConfigLoaderDefaultImpl confLoader = new ConfigLoaderDefaultImpl();
		confLoader.setConfigFile(new File(objectModelPath));
		confLoader.load();
		ConfigurableKVElementImpl modelElement = confLoader.getConfigurable();

		String groupName = modelElement.getAttributeValue("code-item.name");
		String masterClassification = modelElement.getAttributeValue("code-item.mastername");
		String parentClassification = modelElement.getAttributeValue("code-item.parentname");

		if (StringUtils.isNullString(parentClassification))
		{
			CodeObject classification = new CodeObject();
			classification.setName(groupName);
			classification.setTitle(modelElement.getAttributeValue("code-item.title"));
			classification.setDescription(modelElement.getAttributeValue("code-item.description"));

			String type = modelElement.getAttributeValue("code-item.type");
			classification.setType(type == null || CodeTypeEnum.getEnumByType(type) == null ? CodeTypeEnum.LIST : CodeTypeEnum.getEnumByType(type));
			String showType = modelElement.getAttributeValue("code-item.showtype");
			classification.setShowType(showType == null || CodeDisplayEnum.getEnumByType(showType) == null ? CodeDisplayEnum.DROPDOWN : CodeDisplayEnum.getEnumByType(showType));
			classification.setClassification(true);
			codeObjectModelMap.put(classification.getName(), classification);
		}
		else
		{
			CodeItem classificationItem = new CodeItem();
			classificationItem.setName(groupName);
			classificationItem.setTitle(modelElement.getAttributeValue("code-item.title"));
			classificationItem.setDescription(modelElement.getAttributeValue("code-item.description"));
			classificationItem.setMasterName(masterClassification);
			classificationItem.setParentName(parentClassification);
			classificationItem.setClassification(true);
			String sequence = modelElement.getAttributeValue("code-item.sequence");
			classificationItem.setSequence(Integer.parseInt(sequence));
			classificationItem.setClassificationFieldList(this.getFields(modelElement.iterator("code-item.fields.field")));
			classificationItem.setCode(modelElement.getAttributeValue("code-item.code"));

			codeItemModelMap.put(classificationItem.getMasterName() + "_" + classificationItem.getName(), classificationItem);
		}
	}

	private ArrayList<ClassificationField> getFields(Iterator<ConfigurableKVElementImpl> iterator)
	{
		ArrayList<ClassificationField> list = new ArrayList<>();
		while (iterator.hasNext())
		{
			ConfigurableKVElementImpl fieldElement = iterator.next();
			String name = fieldElement.getAttributeValue("name");
			String title = fieldElement.getAttributeValue("title");
			String description = fieldElement.getAttributeValue("description");
			String mandatory = fieldElement.getAttributeValue("mandatory");
			String type = fieldElement.getAttributeValue("type");
			String size = fieldElement.getAttributeValue("size");
			String typevalue = fieldElement.getAttributeValue("typevalue");
			String defaultvalueex = fieldElement.getElementValue("defaultvalueex");
			String defaultvalue = fieldElement.getAttributeValue("defaultvalue");
			String validity = fieldElement.getAttributeValue("validity");
			String publicFielInERP = fieldElement.getAttributeValue("publicfieldinerp");

			ClassificationField field = new ClassificationField();
			field.setName(name);
			field.setTitle(title);
			field.setDescription(description);
			field.setMandatory("true".equalsIgnoreCase(mandatory));
			field.setTypeValue(typevalue);
			field.setValidityRegex(validity);
			field.setInherited(false);
			field.setPublicFieldInERP(publicFielInERP);
			if (StringUtils.isNullString(publicFielInERP))
			{
				field.setPublicFieldInERP(name);
			}

			if (type != null)
			{
				field.setType(FieldTypeEnum.valueOf(type.toUpperCase()));
			}
			if (defaultvalueex != null)
			{
				field.setDefaultValue(defaultvalueex);
			}
			else
			{
				field.setDefaultValue(defaultvalue);
			}

			if (field.getType() == FieldTypeEnum.STRING && StringUtils.isNullString(size))
			{
				field.setFieldSize(String.valueOf(ClassField.defaultCharLength));
			}
			else
			{
				field.setFieldSize(size);
			}

			list.add(field);
		}

		return list;
	}

	/**
	 * 把继承的字段添加到子节点上
	 *
	 * @param classification
	 */
	private void makeAllFields(List<ClassificationField> parentFields, CodeObject classification, CodeItem codeItem)
	{
		List<CodeItem> codeItemList;
		if (codeItem == null)
		{
			codeItemList = classification.getCodeDetailList();
		}
		else
		{
			codeItemList = codeItem.getCodeDetailList();
		}
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItem child : codeItemList)
			{
				if (parentFields.size() != 0)
				{
					for (ClassificationField parentField : parentFields)
					{
						ClassificationField field = (ClassificationField) parentField.clone();
						if (this.isOverride(child.getClassificationFieldList(), field))
						{
							continue;
						}
						field.setInherited(true);
						child.addField(field);
					}
				}

				this.makeAllFields(child.getClassificationFieldList(), classification, child);
			}
		}
	}

	private boolean isOverride(List<ClassificationField> childFieldList, ClassificationField parentField)
	{
		if (childFieldList != null)
		{
			for (ClassificationField field : childFieldList)
			{
				if (field.getName().equals(parentField.getName()))
				{
					return true;
				}
			}
		}

		return false;
	}
}
