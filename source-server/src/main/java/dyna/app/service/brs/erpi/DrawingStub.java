package dyna.app.service.brs.erpi;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.*;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EMM;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * @author lufeia
 * @date 2015-7-20 下午2:36:21
 * @return
 */
@Component
public class DrawingStub extends AbstractServiceStub<ERPIImpl>
{
	private static final String				MODELCLASSCODE_ASM	= "asm";					// 装配图
	private static final String				MODELCLASSCODE_PRT	= "prt";					// 零件图
	private static final String				MODELCLASSCODE_PSM	= "psm";					// 钣金图
	private static final String				MODELTYPE			= "ModelType";
	private static final String				MODELCLASS			= "ModelClass";
	private ERPServerType					type				= null;
	private List<FileResultInfo>			resultFileList		= null;
	private List<String>					realtionList		= new ArrayList<String>();
	private String							itemVersion			= null;
	private String							itemCode			= null;
	private String							fileAccessMode		= null;
	private String							language			= null;
	private Map<String, List<String>>		fileGuids;
	private Map<String, List<DSSFileInfo>>	fileInfos1;
	private String							local;

	/**
	 * 获得文件数据
	 * 
	 * @param paramXML
	 * @return
	 * @throws Exception
	 */
	public String getFileData(String paramXML) throws Exception
	{
		try
		{
			List<Filter> filterList = new ArrayList<Filter>();
			// List<String> classnameList = new ArrayList<String>();
			resultFileList = new ArrayList<FileResultInfo>();
			fileGuids = new HashMap<String, List<String>>();
			fileInfos1 = new HashMap<String, List<DSSFileInfo>>();
			Document doc = XMLUtil.convertString2XML(paramXML);
			Element root = doc.getRootElement();
			Element Parameter = root.getChild("RequestContent").getChild("Parameter");
			Element erptype = Parameter.getChild("ERPtype");
			Element Record = Parameter.getChild("Record");
			Element Field = Record.getChild("Field");
			language = root.getChild("RequestContent").getChild("Parameter").getChild("Access").getAttributeValue("language");

			String ip = Parameter.getChild("Access").getAttributeValue("IP");
			fileAccessMode = Field.getAttributeValue("fileAccessMode");
			itemCode = Field.getAttributeValue("itemCode");
			Attribute versionAttr = Field.getAttribute("itemVersion");

			itemVersion = null;
			if (versionAttr != null)
			{
				itemVersion = versionAttr.getValue();
			}
			this.type = ERPServerType.valueOf(erptype.getAttributeValue("type"));
			local = this.stubService.getUserSignature().getLanguageEnum().toString();
			getRealtionList();
			filterList = getLocalFilter(fileAccessMode);

			if ("ftp".equalsIgnoreCase(fileAccessMode))
			{
				filterList = getLocalFilter("ftp");
				return getFileByFTP(itemCode, filterList);
			}
			else if ("url".equalsIgnoreCase(fileAccessMode))
			{
				filterList = getLocalFilter("url");
				return getFileByURL(itemCode, filterList, ip);
			}
			else if ("URLFileOnly".equalsIgnoreCase(fileAccessMode))
			{
				filterList = getLocalFilter("url");
				return getFileByURLFO(itemCode, filterList, ip);
			}
			else
			{
				throw new ServiceRequestException("Error fileAccessMode");
			}
		}
		finally
		{
			type = null;
			itemVersion = null;
			itemCode = null;
			fileAccessMode = null;
			language = null;
		}
	}

	/**
	 * 以URL方式查看图纸(仅显示文件视图)
	 * 
	 * @param itemCode
	 * @param filterList
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	private String getFileByURLFO(String itemCode, List<Filter> filterList, String ip) throws Exception
	{
		List<FoundationObject> itemList = this.getItem(itemCode);
		List<FoundationObject> end2List = new ArrayList<FoundationObject>();
		if (SetUtils.isNullList(itemList))
		{
			throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOITEM_THISID", local));
		}
		else
		{
			for (FoundationObject item : itemList)
			{
				if (StringUtils.isNullString(itemVersion))
				{
					itemVersion = item.getRevisionId();
				}
				end2List.addAll(getEnd2List(item));
			}
			if (!SetUtils.isNullList(end2List))
			{
				filterEnd2(end2List, filterList, true);
				if (SetUtils.isNullMap(fileGuids))
				{
					throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOCADITEM", local));
				}
				return getURLReturnXML(ip, null, true);
			}
			else
			{
				throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOCADITEM", local));
			}
		}
	}

	/**
	 * 以FTP方式查看图纸
	 * 
	 * @param foid
	 * @param filterList
	 * @param classnameList
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getFileByFTP(String foid, List<Filter> filterList) throws ServiceRequestException
	{
		List<FoundationObject> itemList = this.getItem(foid);
		List<FoundationObject> end2List = new ArrayList<FoundationObject>();
		if (SetUtils.isNullList(itemList))
		{
			throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOITEM_THISID", local));
		}
		else
		{
			for (FoundationObject item : itemList)
			{
				if (StringUtils.isNullString(itemVersion))
				{
					itemVersion = item.getRevisionId();
				}
				end2List.addAll(getEnd2List(item));
			}
			if (!SetUtils.isNullList(end2List))
			{
				filterEnd2(end2List, filterList, false);
				if (SetUtils.isNullList(resultFileList))
				{
					throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOCADITEM", local));
				}
				return getReturnXML();
			}
			else
			{
				throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOCADITEM", local));
			}
		}
	}

	/**
	 * 以URL方式查看图纸（预留方法）
	 * 目前不支持URL方式取图，故直接返回异常
	 * 
	 * @param foid
	 * @param filterList
	 * @param ip
	 * @param classnameList
	 * @return 空
	 * @throws Exception
	 */
	private String getFileByURL(String foid, List<Filter> filterList, String ip) throws Exception
	{
		if (StringUtils.isNullString(ip) && this.stubService.isSingleSignOn(type.name()))
		{
			throw new ServiceRequestException(
					this.stubService.getMSRM().getMSRString("ID_APP_ERPI_URLMODE_IPNOTNULL", this.stubService.getUserSignature().getLanguageEnum().toString()));
		}
		List<FoundationObject> itemList = this.getItem(foid);
		if (SetUtils.isNullList(itemList))
		{
			throw new ServiceRequestException(this.stubService.getMSRM().getMSRString("ID_APP_ERPI_NOITEM_THISID", local));
		}
		else
		{
			return getURLReturnXML(ip, itemList.get(0), false);
		}
	}

	private String getURLReturnXML(String ip, FoundationObject object, boolean isFileOnly) throws Exception
	{
		StringBuffer result = new StringBuffer();
		result.append("<Response>\n").append("<ResponseContent>\n").append("<Document fileAccessMode =\"").append(fileAccessMode).append("\">\n");
		if (isFileOnly)
		{
			for (String type : fileInfos1.keySet())
			{
				List<DSSFileInfo> list = fileInfos1.get(type);
				if (!SetUtils.isNullList(list))
				{
					for (DSSFileInfo info : list)
					{
						String fileName = StringUtils.formatEscapeString(info.getName());
						type = StringUtils.formatEscapeString(type);
						result.append("<Path fileName=\"").append(fileName).append("\" filetype=\"").append(type).append("\" URL=\"")
								.append(getWebAppPath(object, ip, isFileOnly, info.getGuid())).append("\"/>");
					}
				}

			}
		}
		else
		{
			result.append("<Path  URL=\"");
			result.append(getWebAppPath(object, ip, isFileOnly, null));
			result.append("\"/>");
		}
		result.append("</Document>\n").append("  </ResponseContent>\n").append("</Response>");
		return result.toString();
	}

	private String getWebAppPath(FoundationObject fo, String ip, boolean isFileOnly, String guid) throws Exception
	{
		StringBuffer result = new StringBuffer();
		Element pathElement = null;
		String filename = getFileName(type);
		Document document = new SAXBuilder().build(new File(EnvUtils.getConfRootPath() + "conf" + File.separator + filename + ".xml"));
		if (document.getRootElement().getChild("parameters") != null)
		{
			@SuppressWarnings("unchecked")
			List<Element> list = document.getRootElement().getChild("parameters").getChildren("param");
			if (!SetUtils.isNullList(list))
			{
				for (Element e : list)
				{
					if ("webAppPath".equalsIgnoreCase(e.getAttributeValue("name")))
					{
						pathElement = e;
						break;
					}
				}
			}
		}
		if (pathElement == null)
		{
			throw new ServiceRequestException("ID_APP_ERPI_URLMODE_WEBAPPPATH");
		}
		else
		{
			result.append(pathElement.getAttributeValue("value"));
			if (isFileOnly)
			{
				result.append("FileTypeGetAction?guid=").append(guid);
			}
			else
			{
				result.append("DetailInitAction?searchlistindex=1&amp;boType=ITEM&amp;guid=").append(fo.getObjectGuid().getGuid()).append("&amp;classguid=")
						.append(fo.getObjectGuid().getClassGuid());
			}
			if (this.stubService.isSingleSignOn(type.name()))
			{
				String encodeIP = Base64Util.encryptBase64(ip);
				String encodeSessionID = Base64Util.encryptBase64(this.stubService.getUserSignature().getCredential());
				result.append("&amp;lang=").append(language);
				result.append("&amp;ip=").append(encodeIP == null ? "" : encodeIP.substring(0, encodeIP.length() - 2));
				result.append("&amp;sessionID=").append(encodeSessionID == null ? "" : encodeSessionID.substring(0, encodeSessionID.length() - 2));
			}
		}
		return result.toString();
	}

	/**
	 * 筛选符合条件的end2信息
	 * 
	 * @param end2List
	 * @param filterList
	 * @return
	 * @throws ServiceRequestException
	 */
	private void filterEnd2(List<FoundationObject> end2List, List<Filter> filterList, boolean isFileOnly) throws ServiceRequestException
	{
		ACLItem acl = null;
		List<FoundationObject> tempList = new ArrayList<FoundationObject>();
		List<FoundationObject> modelFoundationList = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(filterList)) // 若有输入条件
		{
			tempList = getFilterItem(filterList, end2List, isFileOnly);// 根据筛选条件以及是否下载三维结构获得三维图纸对象
			modelFoundationList = getModelStructureList(tempList);
			if (!SetUtils.isNullList(modelFoundationList))
			{
				getFilterItem(filterList, modelFoundationList, isFileOnly);
			}
		}
		else
		{// 无输入条件则返回所有end2对应信息
			tempList.addAll(end2List);
			if (isDownloadModelStructure())
			{
				for (FoundationObject object : end2List)
				{
					acl = this.getObjectACL(object);
					if (acl != null && acl.isRead())// 若图纸对象无查看权限，不进行以下操作
					{
						ClassInfo info = this.stubService.getEMM().getClassByGuid(object.getObjectGuid().getClassGuid());
						if (info != null && info.hasInterface(ModelInterfaceEnum.ICAD3D))
						{
							List<FoundationObject> list = new ArrayList<FoundationObject>();
							list.add(object);
							modelFoundationList = getModelStructureList(list);
							if (!SetUtils.isNullList(modelFoundationList))
							{
								tempList.addAll(modelFoundationList);
							}
							modelFoundationList.clear();
						}
					}
				}
			}
			for (FoundationObject fo : tempList)
			{
				acl = this.getObjectACL(fo);
				if (acl != null && acl.isRead())
				{
					fo = this.stubService.getBOAS().getObject(fo.getObjectGuid());
					String classname = fo.getObjectGuid().getClassName();
					String modelClass = (String) fo.get(MODELCLASS);
					ClassInfo info = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
					String classification = "";
					if (info.hasInterface(ModelInterfaceEnum.ICAD3D))
					{
						if (this.stubService.getEMM().getCodeItem(modelClass) != null)
						{
							classification = this.stubService.getEMM().getCodeItem(modelClass).getName();
						}
					}
					List<DSSFileInfo> dfi = this.stubService.getDSS().listFile(fo.getObjectGuid(), fo.getIterationId(), null);
					if (!SetUtils.isNullList(dfi))
					{
						if (acl.isViewFile())
						{
							for (DSSFileInfo ds : dfi)// 遍历end2的附件
							{
								if (isFileOnly)
								{
									List<String> list = fileGuids.get(ds.getExtentionName().toLowerCase());
									List<DSSFileInfo> list1 = fileInfos1.get(ds.getExtentionName().toLowerCase());
									if (!SetUtils.isNullList(list))
									{
										if (!list.contains(ds.getGuid()))
										{
											list.add(ds.getGuid());
											list1.add(ds);
										}
									}
									else
									{
										list = new ArrayList<String>();
										list.add(ds.getGuid());
										list1 = new ArrayList<DSSFileInfo>();
										list1.add(ds);
										fileGuids.put(ds.getExtentionName().toLowerCase(), list);
										fileInfos1.put(ds.getExtentionName().toLowerCase(), list1);
									}
								}
								else
								{
									String path = getFilepath(ds);
									FileResultInfo fileInfo = new FileResultInfo(classname, classification, fo.getId(), fo.getRevisionId(), fo.getName(), ds.getName(), path,
											fo.getStatus().getId());
									resultFileList.add(fileInfo);
								}
							}
						}
						else
						{
							for (DSSFileInfo ds : dfi)// 遍历end2的附件
							{
								if (!isFileOnly)
								{
									FileResultInfo fileInfo = new FileResultInfo(classname, classification, fo.getId(), fo.getRevisionId(), fo.getName(), ds.getName(), "",
											fo.getStatus().getId());
									resultFileList.add(fileInfo);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 取得符合条件的图纸对象信息</br>
	 * 
	 * @param filterList
	 * @param end2List
	 * @param isFileOnly
	 * @return
	 * 		若下载3D结构，返回符合筛选条件的3D图纸对象
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> getFilterItem(List<Filter> filterList, List<FoundationObject> end2List, boolean isFileOnly) throws ServiceRequestException
	{
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		ACLItem acl = null;
		for (FoundationObject fo : end2List) // 遍历end2
		{
			acl = getObjectACL(fo);
			if (acl != null && acl.isRead())
			{
				fo = this.stubService.getBOAS().getObject(fo.getObjectGuid());
				String modelClass = (String) fo.get(MODELCLASS);
				String classname = fo.getObjectGuid().getClassName();
				ClassInfo info = this.stubService.getEMM().getClassByGuid(fo.getObjectGuid().getClassGuid());
				String classification = "";
				if (info.hasInterface(ModelInterfaceEnum.ICAD3D) && StringUtils.isGuid(modelClass))
				{
					if (this.stubService.getEMM().getCodeItem(modelClass) != null)
					{
						classification = this.stubService.getEMM().getCodeItem(modelClass).getCode();
					}
				}
				else if (info.hasInterface(ModelInterfaceEnum.IDocument))
				{
					if (this.stubService.getEMM().getCodeItem((String) fo.get(SystemClassFieldEnum.CLASSIFICATION.getName())) != null)
					{
						classification = this.stubService.getEMM().getCodeItem((String) fo.get(SystemClassFieldEnum.CLASSIFICATION.getName())).getCode();
					}
				}
				List<DSSFileInfo> dfi = this.stubService.getDSS().listFile(fo.getObjectGuid(), fo.getIterationId(), null);
				for (Filter filter : filterList)// 遍历输入条件，判断end2是否符合
				{
					// List<String> classnameList = new ArrayList<String>();
					List<String> cfList = new ArrayList<String>();
					List<String> ftList = new ArrayList<String>();
					if (!StringUtils.isNullString(filter.getEnd2classname()))// 类名
					{
						if (!classname.equalsIgnoreCase(filter.getEnd2classname()))
						{
							continue;
						}
					}
					if (info != null)
					{
						if (!StringUtils.isNullString(filter.getClassification()))
						{
							cfList.addAll(Arrays.asList(filter.getClassification().split(",")));
							if (info.hasInterface(ModelInterfaceEnum.ICAD3D))
							{
								if (!cfList.contains(classification))// 若有分类条件，不符合跳出当前循环
								{
									continue;
								}
								else
								{
									if (filter.isDownloadModelStructure())
									{
										resultList.add(fo);
									}
								}
							}
							else if (info.hasInterface(ModelInterfaceEnum.IDocument))
							{
								if (!cfList.contains(classification))// 若有分类条件，不符合跳出当前循环
								{
									continue;
								}
							}
						}
					}
					if (!StringUtils.isNullString(filter.getFiletype()))
					{
						String filetype = filter.getFiletype().toLowerCase();
						ftList.addAll(Arrays.asList(filetype.split(",")));
					}

					for (DSSFileInfo ds : dfi)// 遍历end2的附件
					{
						if (!SetUtils.isNullList(ftList))
						{
							if (ftList.contains(ds.getExtentionName().toLowerCase()))
							{
								if (isFileOnly)
								{
									List<String> list = fileGuids.get(ds.getExtentionName().toLowerCase());
									List<DSSFileInfo> list1 = fileInfos1.get(ds.getExtentionName().toLowerCase());
									if (!SetUtils.isNullList(list))
									{
										if (!list.contains(ds.getGuid()))
										{
											list.add(ds.getGuid());
											list1.add(ds);
										}
									}
									else
									{
										list = new ArrayList<String>();
										list.add(ds.getGuid());
										list1 = new ArrayList<DSSFileInfo>();
										list1.add(ds);
										fileGuids.put(ds.getExtentionName().toLowerCase(), list);
										fileInfos1.put(ds.getExtentionName().toLowerCase(), list1);
									}
								}
								else
								{
									String path = "";
									if (acl.isViewFile())
									{
										path = getFilepath(ds);
									}
									FileResultInfo fileInfo = new FileResultInfo(classname, classification, fo.getId(), fo.getRevisionId(), fo.getName(), ds.getName(), path,
											fo.getStatus().getId());
									resultFileList.add(fileInfo);
								}
							}
						}
						else
						{
							String path = "";
							if (acl.isViewFile())
							{
								path = getFilepath(ds);
							}
							FileResultInfo fileInfo = new FileResultInfo(classname, classification, fo.getId(), fo.getRevisionId(), fo.getName(), ds.getName(), path,
									fo.getStatus().getId());
							resultFileList.add(fileInfo);
						}
					}
				}
			}
		}
		return resultList;

	}

	/**
	 * 获得三维图纸的模型结构
	 * 
	 * @param list
	 *            三维图纸对象
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> getModelStructureList(List<FoundationObject> list) throws ServiceRequestException
	{
		List<FoundationObject> returnList = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject object : list)
			{
				List<String> modelFoGuidList = new ArrayList<String>();
				List<ObjectGuid> modelObjectGuidList = new ArrayList<ObjectGuid>();
				this.listModelFoundationGuid(object, true, modelFoGuidList, modelObjectGuidList, true);
				if (!SetUtils.isNullList(modelObjectGuidList))
				{
					for (ObjectGuid objectGuid : modelObjectGuidList)
					{
						if (!object.getGuid().equalsIgnoreCase(objectGuid.getGuid()))
						{
							FoundationObject fo = this.stubService.getBOAS().getObjectByGuid(objectGuid);
							returnList.add(fo);
						}
					}
				}
			}
			return returnList;
		}
		return null;
	}

	/**
	 * 获得文件的ftp路径
	 * 
	 * @param ds
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getFilepath(DSSFileInfo ds) throws ServiceRequestException
	{
		if (ds != null)
		{
			DSSFileTrans dt = this.stubService.getDSS().downloadFile(ds.getGuid(), AuthorityEnum.VIEWFILE);
			String realPath = dt.getFileFullURI();
			realPath = realPath.substring(0, realPath.length() - 1);
			return "ftp://" + realPath;
		}
		return null;
	}

	/**
	 * 获取物料关联的所有End2对象
	 * 
	 * @param classnameList
	 * 
	 * @param foid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> getEnd2List(FoundationObject item) throws ServiceRequestException
	{
		List<FoundationObject> result = new ArrayList<FoundationObject>();
		BOAS boas = this.stubService.getBOAS();
		if (!SetUtils.isNullList(realtionList))
		{
			for (String temp : realtionList)
			{
				ViewObject view = boas.getRelationByEND1(item.getObjectGuid(), temp);

				if (view != null)
				{
					List<FoundationObject> end2 = boas.listFoundationObjectOfRelation(view.getObjectGuid(), null, null, null, true);
					if (!SetUtils.isNullList(end2))
					{
						result.addAll(end2);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 读取配置文件是否需要下载结构（ERP读PLM图纸信息时使用）
	 * 
	 * @return
	 */
	private boolean isDownloadModelStructure()
	{
		String fileName = this.stubService.getERPStub().getFileName(type);
		ConfigurableKVElementImpl kv = getXMLConfig(fileName);
		Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.parameters.param");
		ConfigurableKVElementImpl tempKV = null;
		while (firstIt.hasNext())
		{
			tempKV = firstIt.next();
			if ("downloadModelStructure".equalsIgnoreCase(tempKV.getAttributeValue("name")) && "true".equalsIgnoreCase(tempKV.getAttributeValue("value")))
			{
				return true;
			}
		}
		return false;
	}

	private ConfigurableKVElementImpl getXMLConfig(String fileName)
	{
		if (StringUtils.isNullString(fileName))
		{
			return null;
		}
		ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
		configLoader.setConfigFile(new File(EnvUtils.getConfRootPath() + "conf" + File.separator + fileName + ".xml"));
		configLoader.load();
		ConfigurableKVElementImpl kv = configLoader.getConfigurable();
		return kv;
	}

	/**
	 * 结构关系
	 * 
	 * 装配关系：CADStructure
	 * 引用关系：MODEL-REFERENCE
	 * 成员关系：MODEL-MEMBER
	 * CAD3D-CAD2D$关系(由开关控制)
	 * ModelType:这个字段判断是组对象还是成员实例
	 * 
	 */
	private void listModelFoundationGuid(FoundationObject foun, boolean useCAD2D, List<String> fileFoundationList, List<ObjectGuid> modelObjectGuidList,
			boolean isDownloadNotPrimaryFile) throws ServiceRequestException
	{
		this.listModelFoundationGuid(foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile, new ArrayList<String>());
	}

	/**
	 * 结构关系
	 * 
	 * 装配关系：CADStructure
	 * 引用关系：MODEL-REFERENCE
	 * 成员关系：MODEL-MEMBER
	 * CAD3D-CAD2D$关系(由开关控制)
	 * ModelType:这个字段判断是组对象还是成员实例
	 * 
	 * @param foun
	 * @param useCAD2D
	 * @param fileMap
	 * @throws ServiceRequestException
	 */
	private void listModelFoundationGuid(FoundationObject foun, boolean useCAD2D, List<String> fileFoundationList, List<ObjectGuid> modelObjectGuidList,
			boolean isDownloadNotPrimaryFile, List<String> loopGuidList) throws ServiceRequestException
	{
		if (loopGuidList.contains(foun.getObjectGuid().getGuid()))
		{
			return;
		}
		else
		{
			loopGuidList.add(foun.getObjectGuid().getGuid());
		}

		String modelClass = (String) foun.get(MODELCLASS);
		// 检查成员还是主对象
		if ((String) foun.get(MODELTYPE) == null)
		{
			return;
		}
		boolean isMaster = BooleanUtils.getBooleanByYN((String) foun.get(MODELTYPE));
		if (StringUtils.isGuid(modelClass))
		{
			CodeItemInfo modeClassItem = this.stubService.getEMM().getCodeItem(modelClass);
			// 零件
			if (modeClassItem.getName().equals(MODELCLASSCODE_PRT) || modeClassItem.getName().equals(MODELCLASSCODE_PSM))
			{
				// 零件实例
				if (!isMaster)
				{
					getFileOfMember(foun, useCAD2D, fileFoundationList, modelObjectGuidList, true, isDownloadNotPrimaryFile, loopGuidList);
				}
				// 零件主
				else
				{
					getFileOfMaster(foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
				}

				// 检查引用关系
				ViewObject referenceView = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().getRelationByEND1(foun.getObjectGuid(),
						BuiltinRelationNameEnum.MODEL_REFERENCE.toString(), false);
				if (referenceView != null && referenceView.getObjectGuid() != null)
				{
					List<FoundationObject> listReference = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(referenceView.getObjectGuid(),
							null, null, null, false);
					if (!SetUtils.isNullList(listReference))
					{
						for (FoundationObject referenceFou : listReference)
						{
							referenceFou = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(referenceFou.getObjectGuid(), false);
							this.listModelFoundationGuid(referenceFou, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile, loopGuidList);
						}
					}
				}
			}
			// 装配
			else if (modeClassItem.getName().equals(MODELCLASSCODE_ASM))
			{
				// 实例
				if (!isMaster)
				{
					this.getFileOfMember(foun, useCAD2D, fileFoundationList, modelObjectGuidList, false, isDownloadNotPrimaryFile, loopGuidList);
				}
				// 主装配
				else
				{
					this.getFileOfMaster(foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
				}
				// 检查装配关系
				if (isMaster)
				{
					ViewObject CADView = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().getRelationByEND1(foun.getObjectGuid(),
							BuiltinRelationNameEnum.MODEL_STRUCTURE.toString(), false);
					if (CADView != null && CADView.getObjectGuid() != null)
					{
						List<FoundationObject> listCADFoun = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(CADView.getObjectGuid(), null,
								null, null, false);
						if (!SetUtils.isNullList(listCADFoun))
						{
							for (FoundationObject cadFou : listCADFoun)
							{
								cadFou = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(cadFou.getObjectGuid(), false);
								this.listModelFoundationGuid(cadFou, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile, loopGuidList);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 主对象文件
	 * 
	 * @param foun
	 * @param useCAD2D
	 * @param fileMap
	 * @throws ServiceRequestException
	 */
	private void getFileOfMaster(FoundationObject foun, boolean useCAD2D, List<String> fileFoundationList, List<ObjectGuid> modelObjectGuidList, boolean isDownloadNotPrimaryFile)
			throws ServiceRequestException
	{
		// 下载自己的文件
		this.getFileOfItSelf(foun, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
		// 下载2D图纸
		this.getFileOfCAD2D(foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
		// 下载成员对象
		ViewObject memberView = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().getRelationByEND1(foun.getObjectGuid(), BuiltinRelationNameEnum.MODEL_MEMBER.toString(),
				false);
		if (memberView != null && memberView.getObjectGuid() != null)
		{
			List<FoundationObject> listMember = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(memberView.getObjectGuid(), null, null,
					null, false);
			if (!SetUtils.isNullList(listMember))
			{
				for (FoundationObject memfoun : listMember)
				{
					// 下载自己的文件
					this.getFileOfItSelf(memfoun, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
					// 下载2D图纸
					this.getFileOfCAD2D(memfoun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
				}
			}
		}

	}

	/**
	 * 成员(实例)文件
	 * 
	 * @param foun
	 * @param useCAD2D
	 * @param fileMap
	 * @throws ServiceRequestException
	 */
	private void getFileOfMember(FoundationObject foun, boolean useCAD2D, List<String> fileFoundationList, List<ObjectGuid> modelObjectGuidList, boolean isPart,
			boolean isDownloadNotPrimaryFile, List<String> loopGuidList) throws ServiceRequestException
	{
		if (isPart)
		{
			// 下载自己的文件
			this.getFileOfItSelf(foun, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
			// 下载2D图纸
			this.getFileOfCAD2D(foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
		}
		// long l = new Date().getTime();
		List<FoundationObject> listEnd1 = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listWhereReferenced(foun.getObjectGuid(),
				BuiltinRelationNameEnum.MODEL_MEMBER.toString(), null, null, false);
		// DynaLogger.info("listWhereReferenced_MODEL_MEMBER:" + (System.currentTimeMillis() - l));
		if (!SetUtils.isNullList(listEnd1))
		{
			for (FoundationObject end1Foun : listEnd1)
			{
				end1Foun = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(end1Foun.getObjectGuid(), false);
				this.getFileOfItSelf(end1Foun, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
				this.getFileOfCAD2D(end1Foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
				// 装配
				if (!isPart)
				{
					this.listModelFoundationGuid(end1Foun, useCAD2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile, loopGuidList);
				}
			}
		}

	}

	/**
	 * CAD3D-对应的CAD2D文件
	 * 
	 * @param foun
	 * @param useCAD2D
	 * @param fileMap
	 * @throws ServiceRequestException
	 */
	private void getFileOfCAD2D(FoundationObject foun, boolean useCAD2D, List<String> fileFoundationList, List<ObjectGuid> modelObjectGuidList, boolean isDownloadNotPrimaryFile)
			throws ServiceRequestException
	{
		if (useCAD2D)
		{
			ViewObject cad2dView = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().getRelationByEND1(foun.getObjectGuid(), BuiltinRelationNameEnum.CAD3DCAD2D.toString(),
					false);
			if (cad2dView != null)
			{
				List<FoundationObject> listCAD2D = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(cad2dView.getObjectGuid(), null, null,
						null, false);
				if (!SetUtils.isNullList(listCAD2D))
				{
					for (FoundationObject cad2D : listCAD2D)
					{
						getFileOfItSelf(cad2D, fileFoundationList, modelObjectGuidList, isDownloadNotPrimaryFile);
					}
				}
			}
		}
	}

	/**
	 * 实例自己的主文件
	 * 
	 * @param foun
	 * @param fileMap
	 * @throws ServiceRequestException
	 */
	private void getFileOfItSelf(FoundationObject foun, List<String> fileFoundationList, List<ObjectGuid> modelObjectGuidList, boolean isDownloadNotPrimaryFile)
			throws ServiceRequestException
	{
		if (fileFoundationList.contains(foun.getObjectGuid().getGuid()))
		{
			return;
		}
		else
		{
			fileFoundationList.add(foun.getObjectGuid().getGuid());
			modelObjectGuidList.add(foun.getObjectGuid());
		}
	}

	/**
	 * 获得对象的权限
	 * 
	 * @throws ServiceRequestException
	 */
	private ACLItem getObjectACL(FoundationObject object) throws ServiceRequestException
	{
		ACLItem item = null;
		if (object != null)
		{
			item = this.stubService.getACL().getACLItemForObject(object.getObjectGuid());
		}
		return item;
	}

	/**
	 * 获得配置文件中的筛选条件
	 * 
	 * @param fileAccessMode
	 * 
	 * @param classnameList
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<Filter> getLocalFilter(String fileAccessMode) throws ServiceRequestException
	{
		List<Filter> result = new ArrayList<Filter>();
		ClassInfo info = null;
		EMM emm = this.stubService.getEMM();
		String fileName = this.stubService.getERPStub().getFileName(type);
		ConfigurableKVElementImpl kv = getXMLConfig(fileName);
		Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.draws.templates.filter." + fileAccessMode);
		if (firstIt.hasNext() == false)
		{
			firstIt = kv.iterator("root.draws.templates.filter");
		}
		ConfigurableKVElementImpl tempKV = null;
		while (firstIt.hasNext())
		{
			Filter filter = new Filter();
			tempKV = firstIt.next();
			filter.setEnd2classname(StringUtils.convertNULLtoString(tempKV.getAttributeValue("end2classname")));
			filter.setFiletype(StringUtils.convertNULLtoString(tempKV.getAttributeValue("filetype")));
			info = emm.getClassByName(filter.getEnd2classname());
			if (info != null)
			{
				if (!info.hasInterface(ModelInterfaceEnum.ICAD3D))
				{
					filter.setDownloadModelStructure("false");
					if (!info.hasInterface(ModelInterfaceEnum.IDocument))
					{
						filter.setClassification(null);
					}
					else
					{
						filter.setClassification(StringUtils.convertNULLtoString(tempKV.getAttributeValue("classification")));
					}
				}
				else
				{
					filter.setClassification(StringUtils.convertNULLtoString(tempKV.getAttributeValue("classification")));
					filter.setDownloadModelStructure(StringUtils.convertNULLtoString(tempKV.getAttributeValue("downloadModelStructure")));
				}
			}
			if (!filter.isEmpty())
			{
				result.add(filter);
			}
		}
		return result;
	}

	/**
	 * 返回内容格式
	 * 
	 * @param string
	 * @return
	 */
	private String getReturnXML()
	{
		StringBuffer resultXML = new StringBuffer();
		resultXML.append("<Response>\n").append("<ResponseContent>\n").append("<Document fileAccessMode =\"").append(fileAccessMode).append("\">\n");
		if (!SetUtils.isNullList(resultFileList))
		{
			for (int i = 1; i <= resultFileList.size(); i++)
			{
				FileResultInfo info = resultFileList.get(i - 1);
				if (!info.isEmpty())
				{
					String itemCode_ = StringUtils.formatEscapeString(itemCode);
					String end2ClassName = StringUtils.formatEscapeString(StringUtils.convertNULLtoString(info.getEnd2classname()));
					String classification = StringUtils.formatEscapeString(StringUtils.convertNULLtoString(info.getClassification()));
					String id = StringUtils.formatEscapeString(StringUtils.convertNULLtoString(info.getId()));
					String name = StringUtils.formatEscapeString(StringUtils.convertNULLtoString(info.getName()));
					String fileName = StringUtils.formatEscapeString(StringUtils.convertNULLtoString(info.getFilename()));
					String filePath = StringUtils.formatEscapeString(StringUtils.convertNULLtoString(info.getFilepath()));
					resultXML.append("<Record id=\"").append(i).append("\">\n").append("<Field itemCode=\"").append(itemCode_).append("\" itemVersion =\"").append(itemVersion)
							.append("\" end2classname=\"").append(end2ClassName).append("\" classification =\"").append(classification).append("\" id=\"").append(id)
							.append("\" version=\"").append(info.getVersion()).append("\" status=\"").append(info.getStatus()).append("\" name=\"").append(name)
							.append("\" filename=\"").append(fileName).append("\"/>\n").append("<Path  filepath=\"").append(filePath).append("\"/>\n").append("</Record>\n");
				}
			}
		}
		resultXML.append("</Document>\n").append("  </ResponseContent>\n").append("</Response>");
		return resultXML.toString();
	}

	/**
	 * 获得关联关系名
	 */
	public void getRealtionList()
	{
		realtionList.clear();
		String fileName = this.stubService.getERPStub().getFileName(type);
		ConfigurableKVElementImpl kv = getXMLConfig(fileName);
		Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.draws.templates");
		ConfigurableKVElementImpl tempKV = null;
		while (firstIt.hasNext())
		{
			tempKV = firstIt.next();
			String relation = tempKV.getAttributeValue("realtion");
			if (!StringUtils.isNullString(relation))
			{
				realtionList.addAll(Arrays.asList(relation.split(",")));
			}
		}
		if (SetUtils.isNullList(realtionList))
		{
			throw new IllegalArgumentException(fileName + " error:can not find root.draws.templates or realtion is null ");
		}
	}

	/***
	 * 获取物料
	 * 
	 * @param id
	 * @return 若无此物料返回null
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> getItem(String id) throws ServiceRequestException
	{
		String rootItemClass = this.getRootClassByInterface(ModelInterfaceEnum.IItem);
		SearchCondition condition = SearchConditionFactory.createSearchCondition4Class(rootItemClass, null, false);
		condition.setPageNum(1);
		condition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		condition.setSearchType(AdvancedQueryTypeEnum.NORMAL);
		condition.addFilter("ID$", id, OperateSignEnum.EQUALS);
		if (!StringUtils.isNullString(itemVersion))
		{
			condition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);
			condition.addFilter(SystemClassFieldEnum.REVISIONID.getName(), itemVersion, OperateSignEnum.EQUALS);
		}
		else
		{
			condition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
		}
		List<FoundationObject> list = this.stubService.getBOAS().listObject(condition);
		return list;
	}

	private String getRootClassByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IItem);
		if (!SetUtils.isNullList(classInfoList))
		{
			for (ClassInfo classInfo : classInfoList)
			{
				ClassInfo superClassInfo = this.stubService.getEMM().getClassByGuid(classInfo.getSuperClassGuid());
				if (!superClassInfo.hasInterface(ModelInterfaceEnum.IItem) || !superClassInfo.isCreateTable())
				{
					return classInfo.getName();
				}
			}
		}
		return null;
	}

	private String getFileName(ERPServerType type)
	{
		if (type == ERPServerType.ERPTIPTOP)
		{
			return "ytconf";
		}
		else if (type == ERPServerType.ERPWORKFLOW)
		{
			return "wfconf";
		}
		else if (type == ERPServerType.ERPYF)
		{
			return "yfconf";
		}
		else if (type == ERPServerType.ERPE10)
		{
			return "e10conf";
		}
		else if (type == ERPServerType.ERPSM)
		{
			return "smconf";
		}
		else if (type == ERPServerType.ERPT100)
		{
			return "T100conf";
		}

		throw new IllegalArgumentException(type + " is not supported(dyna.app.service.brs.erpi.ERPStub#getFileName)");
	}

}

/**
 * 过滤条件
 * 
 * @author lufeia-SH
 * 
 */
class Filter
{
	String	end2classname;				// 图纸/文档的类名
	String	classification;
	String	filetype;					// 文件后缀名
	String	isDownloadModelStructure;	// 是否同步下载3D模型结构

	public Filter()
	{
	}

	public boolean isEmpty()
	{
		return StringUtils.isNullString(end2classname) && StringUtils.isNullString(classification) && StringUtils.isNullString(filetype)
				&& StringUtils.isNullString(isDownloadModelStructure);
	}

	public Filter(String end2classname1, String classification1, String type, String isDM)
	{
		this.end2classname = end2classname1;
		this.classification = classification1;
		this.filetype = type;
		this.isDownloadModelStructure = isDM;
	}

	public String getEnd2classname()
	{
		return end2classname;
	}

	public void setEnd2classname(String end2classname)
	{
		this.end2classname = end2classname;
	}

	public String getClassification()
	{
		return classification;
	}

	public void setClassification(String classification)
	{
		this.classification = classification;
	}

	public String getFiletype()
	{
		return filetype;
	}

	public void setFiletype(String filetype)
	{
		this.filetype = filetype;
	}

	public boolean isDownloadModelStructure()
	{
		return "true".equalsIgnoreCase(isDownloadModelStructure);
	}

	public void setDownloadModelStructure(String isDownloadModelStructure)
	{
		this.isDownloadModelStructure = isDownloadModelStructure;
	}

}

/**
 * 图纸/文档返回信息对象
 * 
 * @author lufeia-SH
 * 
 */
class FileResultInfo
{
	// String itemCode;
	// String itemVersion;
	String	end2classname;	// 图纸/文档的类名
	String	classification;
	String	id;
	String	version;
	String	name;
	String	filename;
	String	filepath;
	String	status;

	public FileResultInfo(String end2classname1, String classification1, String id1, String version, String name1, String filename1, String filepath1, String status1)
	{
		this.end2classname = end2classname1;
		this.classification = classification1;
		this.id = id1;
		this.version = version;
		this.name = name1;
		this.filename = filename1;
		this.filepath = filepath1;
		this.status = status1;
	}

	public boolean isEmpty()
	{
		return StringUtils.isNullString(end2classname) && StringUtils.isNullString(classification) && StringUtils.isNullString(id) && StringUtils.isNullString(name)
				&& StringUtils.isNullString(filename) && StringUtils.isNullString(status);
	}

	public String getEnd2classname()
	{
		return end2classname;
	}

	public void setEnd2classname(String end2classname)
	{
		this.end2classname = end2classname;
	}

	public String getClassification()
	{
		return classification;
	}

	public void setClassification(String classification)
	{
		this.classification = classification;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String getFilepath()
	{
		return filepath;
	}

	public void setFilepath(String filepath)
	{
		this.filepath = filepath;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}