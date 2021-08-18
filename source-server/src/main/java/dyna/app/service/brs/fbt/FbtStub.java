/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FbtStub
 * wangweixia 2012-9-7
 */
package dyna.app.service.brs.fbt;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.dss.DSSImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.dto.*;
import dyna.common.dto.aas.Group;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文件浏览工具中其他的方法
 * 
 * @author wangweixia
 * 
 */
@Component
public class FbtStub extends AbstractServiceStub<FBTSImpl>
{

	private static final String	SUFFIXEDELIMITER	= ";";
	private static final String	STRINGDELIMITER		= ",";

	/**
	 * @param fileOpenSubject
	 * @param fileOpenItemList
	 * @param deleteFileOpenItem
	 */
	protected void saveSubjectAndItem(FileOpenSubject fileOpenSubject, List<FileOpenItem> fileOpenItemList) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		String userGuid = this.stubService.getOperatorGuid();
		// String sessionId = this.stubService.getSignature().getCredential();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());// 开始事务

			// 删除操作
			// if (!SetUtils.isNullList(deleteFileOpenItem))
			// {
			// for (FileOpenItem fileItem : deleteFileOpenItem)
			// {
			// sds.delete(FileOpenItem.class, fileItem.getGuid());
			// }
			// }

			if (fileOpenSubject != null)
			{
				// 更新或者新建
				String fileOpenSubjectGuid = "";
				boolean isCreate = false;
				// 更新或者新建fileOpenSubject
				fileOpenSubject.setUpdateUserGuid(userGuid);
				if (!StringUtils.isGuid(fileOpenSubject.getGuid()))
				{
					isCreate = true;
					fileOpenSubject.setCreateUserGuid(userGuid);
				}
				fileOpenSubjectGuid = sds.save(fileOpenSubject);
				if (!isCreate)
				{
					fileOpenSubjectGuid = fileOpenSubject.getGuid();
				}
				// 更新FileOpenItem时，先删掉跟fileOpenSubjectGuid相关的FileOpenItem
				List<FileOpenItem> oldfileOpenItemList = this.stubService.getFoItemStub().listFileOpenItemBySubject(fileOpenSubjectGuid);
				if (!SetUtils.isNullList(oldfileOpenItemList))
				{
					for (FileOpenItem fileItemConfig : oldfileOpenItemList)
					{
						sds.delete(FileOpenItem.class, fileItemConfig.getGuid());
					}
				}
				if (!SetUtils.isNullList(fileOpenItemList))
				{
					for (FileOpenItem fileItemConfig : fileOpenItemList)
					{
						fileItemConfig.setGuid("");
						fileItemConfig.setCreateUserGuid(userGuid);
						fileItemConfig.setUpdateUserGuid(userGuid);
						fileItemConfig.setSubjectGuid(fileOpenSubjectGuid);
						String fileItemGuid = this.stubService.getFoItemStub().saveFileOpenItem(fileItemConfig);
						// String fileItemGuid = sds.save(fileItemConfig);
						List<FileOpenConfig> fileConfigList = fileItemConfig.getFileSelectList();
						if (!SetUtils.isNullList(fileConfigList))
						{
							for (FileOpenConfig fileConfig : fileConfigList)
							{
								if (fileConfig.getFiletypename() == null)
								{
									continue;
								}
								FileItemAndConfig config = new FileItemAndConfig();
								config.setCreateUserGuid(userGuid);
								config.setFoItemGuid(fileItemGuid);
								config.setFoConfigGuid(fileConfig.getGuid());
								sds.save(config);
							}
						}

					}

				}
			}
//			DataServer.getTransactionManager().commitTransaction();// 提交事务

		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByDynaDataException(e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	/**
	 * @param foundationObject
	 * @param suffix
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<FileOpenConfig> listFileOpenConfigBySuffix(FoundationObject foundationObject, String suffix) throws ServiceRequestException
	{
		List<FileOpenConfig> fileConfigList = null;
		if (foundationObject == null)
		{
			// fileConfigList = this.stubService.getFoConfigStub().listFileOpenConfigbySuffix(suffix);
			return null;
		}
		suffix = suffix.toLowerCase();

		// Map<String, List<FileOpenItem>> tempFileSubject = new HashMap<String, List<FileOpenItem>>();
		// List<FileOpenItem> fileOpenItem = this.stubService.getFoItemStub().listItemByGuids(
		// foundationObject.getOwnerUserGuid(), suffix);
		// if (SetUtils.isNullList(fileOpenItem))
		// {
		// return null;
		// }
		// for (FileOpenItem item : fileOpenItem)
		// {
		// if (SetUtils.isNullList(tempFileSubject.get(item.getSubjectGuid())))
		// {
		// List<FileOpenItem> mapValue = new ArrayList<FileOpenItem>();
		// mapValue.add(item);
		// tempFileSubject.put(item.getSubjectGuid(), mapValue);
		// }
		// else
		// {
		// tempFileSubject.get(item.getSubjectGuid()).add(item);
		// }
		// }
		boolean isOwner = foundationObject.getOwnerUserGuid().equalsIgnoreCase(this.stubService.getUserSignature().getUserGuid());
		try
		{
			List<FileOpenSubject> fileStubjectList = this.stubService.getFoSubjectStub().listALLNodeFileOpenSubject();
			if (!SetUtils.isNullList(fileStubjectList))
			{
				// 将返回的所有节点按照根节点和子节点的关系组装成树
				List<StubjectTree> stubjectTreeList = this.assembledIntoTree(fileStubjectList);
				// 从组装好的树中取到符合规则的fileOpenConfig
				if (!SetUtils.isNullList(stubjectTreeList))
				{
					for (StubjectTree tree : stubjectTreeList)
					{
						fileConfigList = this.getProperFileOpenConfig(foundationObject, tree, isOwner, suffix);
						if (!SetUtils.isNullList(fileConfigList))
						{
							return fileConfigList;
						}
					}
				}
			}
			return null;
			// 由于返回的fileConfigGuid是一串以";"相隔的Guid,因此要通过guid取到对应的FileOpenConfig对象
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	/**
	 * @param foundationObject
	 * @param stubjectTreeList
	 * @param suffix
	 * @return
	 * @throws ServiceRequestException
	 */

	private List<FileOpenConfig> getProperFileOpenConfig(FoundationObject foundationObject, StubjectTree tree, boolean isOwner, String suffix) throws ServiceRequestException
	{
		// 拿到当前节点
		FileOpenSubject fileSubject = tree.getrootNode();
		boolean hasSubjectPrivilege = this.hasSubjectPrivilege(fileSubject, foundationObject);
		// 假如根节点不满足就返回
		if (!hasSubjectPrivilege)
		{
			return null;
		}
		// 若根节点满足就查看子节点
		List<StubjectTree> stubjectTreeList = tree.getChildList();
		if (!SetUtils.isNullList(stubjectTreeList))
		{
			for (StubjectTree chile : stubjectTreeList)
			{
				List<FileOpenConfig> list = this.getProperFileOpenConfig(foundationObject, chile, isOwner, suffix);
				if (!SetUtils.isNullList(list))
				{
					return list;
				}
			}
		}
		List<FileOpenConfig> list = this.stubService.getFoItemStub().listFileOpenItemBySubject(fileSubject.getGuid(), isOwner, suffix);
		return list;

	}

	/**
	 * @param fileSubject
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean hasSubjectPrivilege(FileOpenSubject fileSubject, FoundationObject foundationObject) throws ServiceRequestException
	{
		boolean isHasPrivilege = false;
		boolean isHas = false;
		// 类判断：子项值空
		if (AccessConditionEnum.HASCLASS.name().equals(fileSubject.getCondition().name()))
		{
			String foundationString = foundationObject.getObjectGuid().getClassGuid();
			// 判断此foundationBoInfo是否被配置
			isHas = this.isHasFoundationClassGuid(foundationString, fileSubject);
		}
		// 判断状态：子项值空
		else if (AccessConditionEnum.HASSTATUS.name().equals(fileSubject.getCondition().name()))
		{
			isHas = this.isHasStatus(foundationObject.getStatus(), fileSubject);
		}
		// 判断所有组：子项值空
		else if (AccessConditionEnum.OWNINGGROUP.name().equals(fileSubject.getCondition().name()))
		{
			isHas = this.isHasOwningGroup(foundationObject, fileSubject);
		}
		// 判断分类：子项值不空
		else if (AccessConditionEnum.HASCLASSFICATION.name().equals(fileSubject.getCondition().name()))
		{
			isHas = this.isHasClassfication(foundationObject, fileSubject);
		}
		// 判断生命周期：子项值不空
		else if (AccessConditionEnum.INLIFECYCLEPHASE.name().equals(fileSubject.getCondition().name()))
		{
			isHas = this.isHasInLifeCyclePhase(foundationObject, fileSubject);
		}
		if (isHas)
		{
			isHasPrivilege = true;
		}
		return isHasPrivilege;
	}

	/**
	 * @param foundationObject
	 * @param fileSubject
	 * @return
	 */
	private boolean isHasInLifeCyclePhase(FoundationObject foundationObject, FileOpenSubject fileSubject)
	{
		boolean isHas = false;
		if (!StringUtils.isNullString(foundationObject.getLifecyclePhaseGuid()) && !StringUtils.isNullString(fileSubject.getValueGuid()))
		{
			if (foundationObject.getLifecyclePhaseGuid().equals(fileSubject.getValueGuid()))
			{
				isHas = true;
			}
		}

		return isHas;
	}

	/**
	 * @param foundationObject
	 * @param fileSubject
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean isHasClassfication(FoundationObject foundationObject, FileOpenSubject fileSubject) throws ServiceRequestException
	{
		boolean isHas = false;
		String detailClassficationGuid = fileSubject.getValueGuid();
		List<CodeItemInfo> classficationList = this.stubService.getEMM().listLeafCodeItemInfoByDatail(detailClassficationGuid);
		if (!StringUtils.isNullString(foundationObject.getClassificationGuid()) && !SetUtils.isNullList(classficationList))
		{
			for (CodeItemInfo itemInfo : classficationList)
			{
				if (foundationObject.getClassificationGuid().equals(itemInfo.getGuid()))
				{
					isHas = true;
					break;
				}
			}

		}

		return isHas;
	}

	/**
	 * @param foundationObject
	 * @param fileSubject
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean isHasOwningGroup(FoundationObject foundationObject, FileOpenSubject fileSubject) throws ServiceRequestException
	{
		boolean isHas = false;
		Group currentGroup = this.stubService.getAAS().getGroup(fileSubject.getValueGuid());
		List<Group> groupList = new ArrayList<Group>();
		groupList.add(currentGroup);
		List<Group> childGroupList = this.stubService.getAAS().listAllSubGroup(fileSubject.getValueGuid(), null, true);
		if (!SetUtils.isNullList(childGroupList))
		{
			groupList.addAll(childGroupList);
		}
		if (!SetUtils.isNullList(groupList) && !StringUtils.isNullString(foundationObject.getOwnerGroupGuid()))
		{
			for (Group group : groupList)
			{
				if (group.getGuid().equals(foundationObject.getOwnerGroupGuid()))
				{
					isHas = true;
					break;
				}
			}
		}
		return isHas;
	}

	/**
	 * @param status
	 * @param fileSubject
	 * @return
	 */
	private boolean isHasStatus(SystemStatusEnum status, FileOpenSubject fileSubject)
	{
		boolean isHas = false;
		if (status.getId().equals(fileSubject.getValueGuid()))
		{
			isHas = true;
		}
		return isHas;
	}

	/**
	 * @param foundationBoInfo
	 * @param fileSubject
	 * @return
	 */
	private boolean isHasFoundationClassGuid(String foundationClassGuid, FileOpenSubject fileSubject) throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = new ArrayList<>();
		boolean isHas = false;
		classInfoList = this.stubService.getEMM().listSubClass(null, fileSubject.getValueGuid(), true, null);
		ClassInfo classSelf = this.stubService.getEMM().getClassByGuid(fileSubject.getValueGuid());
		classInfoList.add(classSelf);
		if (!SetUtils.isNullList(classInfoList))
		{
			for (ClassInfo classInfo : classInfoList)
			{
				if (!StringUtils.isNullString(foundationClassGuid) && foundationClassGuid.equals(classInfo.getGuid()))
				{
					isHas = true;
					break;
				}
			}
		}
		return isHas;
	}

	/**
	 * @param fileStubjectList
	 * @return
	 */
	private List<StubjectTree> assembledIntoTree(List<FileOpenSubject> fileStubjectList)
	{
		List<StubjectTree> treeList = new ArrayList<FbtStub.StubjectTree>();
		for (FileOpenSubject fileStub : fileStubjectList)
		{
			if (StringUtils.isNullString(fileStub.getParentGuid()))
			{
				StubjectTree tree = new StubjectTree();
				this.listRelateNode(fileStub, fileStubjectList, tree);
				treeList.add(tree);
			}
		}
		// 将得到的树进行排序：同层position的值1>2,权限高的放在List前面
		if (!SetUtils.isNullList(treeList))
		{
			this.sortTreeListByRules(treeList);
		}
		return treeList;
	}

	/**
	 * 按照规则将Tree排序
	 * 
	 * @param treeList
	 */
	private void sortTreeListByRules(List<StubjectTree> treeList)
	{
		int length = treeList.size();
		if (!SetUtils.isNullList(treeList))
		{
			for (int i = 0; i <= length - 1; i++)
			{
				for (int j = length - 1; j > i; j--)
				{
					if (treeList.get(j).getrootNode().getPosition() < treeList.get(j - 1).getrootNode().getPosition())
					{
						StubjectTree temp = treeList.get(j);
						treeList.set(j, treeList.get(j - 1));
						treeList.set(j - 1, temp);

					}
				}

				this.sortTreeListByRules(treeList.get(i).getChildList());

			}
		}

	}

	/**
	 * @param fileStubjectList
	 * @return
	 */
	private void listRelateNode(FileOpenSubject fileSubject, List<FileOpenSubject> fileStubjectList, StubjectTree treeNode)
	{

		List<StubjectTree> listTree = new ArrayList<StubjectTree>();
		treeNode.setrootNode(fileSubject);
		for (FileOpenSubject stub : fileStubjectList)
		{
			if (!StringUtils.isNullString(stub.getParentGuid()) && stub.getParentGuid().equals(fileSubject.getGuid()))
			{
				StubjectTree childTree = new StubjectTree();
				childTree.setrootNode(fileSubject);
				listTree.add(childTree);
				this.listRelateNode(stub, fileStubjectList, childTree);
			}

		}
		treeNode.setChildList(listTree);

	}

	protected List<DSSFileTrans> listDSSFileTransByConfigGuid(FoundationObject foundationObject, String guid, DSSFileInfo file) throws ServiceRequestException
	{
		return this.listDSSFileTransByConfigGuid(foundationObject.getObjectGuid(), guid, file);
	}

	/**
	 * @param foundationObject
	 * @param guid
	 * @param file
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<DSSFileTrans> listDSSFileTransByConfigGuid(ObjectGuid objectGuid, String guid, DSSFileInfo file) throws ServiceRequestException
	{
		if (file == null)
		{
			return null;
		}
		ClassInfo classinfo = this.stubService.getEMM().getClassByName(objectGuid.getClassName());
		// 通过guid取得相关的FileOpenConfig
		List<DSSFileTrans> dssFileTransList = new ArrayList<DSSFileTrans>();
		if (StringUtils.isNullString(guid))
		{
			DSSFileTrans dssFileTrans = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().downloadFile(file.getGuid(), AuthorityEnum.VIEWFILE);
			dssFileTransList.add(dssFileTrans);
			return dssFileTransList;
		}
		FileOpenConfig fileconfig = this.stubService.getFoConfigStub().getFileOpenConfigByGuid(guid);
		String relationTemID = fileconfig.getRelationname();
		if (StringUtils.isNullString(relationTemID) || StringUtils.isNullString(fileconfig.getSyndownloadtype()))
		{
			DSSFileTrans dssFileTrans = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().downloadFile(file.getGuid(), AuthorityEnum.VIEWFILE);
			dssFileTransList.add(dssFileTrans);
			return dssFileTransList;
		}
		else if (classinfo.hasInterface(ModelInterfaceEnum.ICAD))
		{
			//TODO
			List<String> fileGuidList = null;
//			((CADImpl) this.stubService.getCAD()).getCadFileStub().listModelFiles(objectGuid, true, AuthorityEnum.VIEWFILE);
			dssFileTransList = this.downLoadFileListWitGuid(fileGuidList);
		}
		else
		{
			String[] synDownloadType = StringUtils.splitStringWithDelimiter(SUFFIXEDELIMITER, fileconfig.getSyndownloadtype());
			List<RelationTemplateInfo> templates = getTemplates(relationTemID);
			List<StructureObject> structureList = this.getAllStrutureList(objectGuid, templates);
			List<DSSFileInfo> end1FileList = getEnd1FileList(templates, objectGuid, synDownloadType);
			if (SetUtils.isNullList(structureList))
			{
				DSSFileTrans dssFileTrans = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().downloadFile(file.getGuid(), AuthorityEnum.VIEWFILE);
				dssFileTransList.add(dssFileTrans);
				if (!SetUtils.isNullList(end1FileList))
				{
					List<DSSFileTrans> list = this.downLoadFileList(end1FileList);
					dssFileTransList.addAll(list);
				}
				return dssFileTransList;
			}

			// 从end2中取到需同步下载的文件
			List<DSSFileInfo> fileList = this.listDssFileInfoByEnd2AndSuffix(structureList, synDownloadType);
			if (!SetUtils.isNullList(end1FileList))
			{
				fileList.addAll(end1FileList);
			}
			fileList.add(file);

			// 把这些文件下载下来
			dssFileTransList = this.downLoadFileList(fileList);

		}
		return dssFileTransList;
	}

	/**
	 * 获取批注文件的DssFileTrans
	 * 
	 * @param guid
	 * @param file
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<DSSFileTrans> listBIFileByFileGuid(String guid) throws ServiceRequestException
	{
		List<DSSFileTrans> dssFileTransList = new ArrayList<DSSFileTrans>();
		List<DSSFileInfo> list = this.stubService.getDSS().listFile("BI_FILE", guid);
		if (list != null)
		{
			dssFileTransList.addAll(this.downLoadFileList(list));
		}
		return dssFileTransList;
	}

	/**
	 * @param fileList
	 * @param file
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<DSSFileTrans> downLoadFileListWitGuid(List<String> fileList) throws ServiceRequestException
	{
		List<DSSFileTrans> dssFileTransList = new ArrayList<DSSFileTrans>();
		for (String guid : fileList)
		{
			DSSFileTrans dssFile = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().downloadFile(guid, AuthorityEnum.VIEWFILE);
			dssFileTransList.add(dssFile);
		}
		return dssFileTransList;
	}

	/**
	 * @param fileList
	 * @param file
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<DSSFileTrans> downLoadFileList(List<DSSFileInfo> fileList) throws ServiceRequestException
	{
		List<DSSFileTrans> dssFileTransList = new ArrayList<DSSFileTrans>();
		for (DSSFileInfo fileInfo : fileList)
		{
			DSSFileTrans dssFile = ((DSSImpl) this.stubService.getDSS()).getTransFileStub().downloadFile(fileInfo.getGuid(), AuthorityEnum.VIEWFILE);
			dssFileTransList.add(dssFile);
		}
		return dssFileTransList;
	}

	/**
	 * @param structureList
	 * @param synDownloadType
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<DSSFileInfo> listDssFileInfoByEnd2AndSuffix(List<StructureObject> structureList, String[] synDownloadType) throws ServiceRequestException
	{
		List<DSSFileInfo> returndssFileList = new ArrayList<DSSFileInfo>();

		for (StructureObject structure : structureList)
		{
			List<DSSFileInfo> dssFileList = this.stubService.getDSS().listFile(structure.getEnd2ObjectGuid(), null);
			if (!SetUtils.isNullList(dssFileList))
			{
				for (String suffix : synDownloadType)
				{
					String updateSuffix = suffix.substring(2);
					for (DSSFileInfo fileInfo : dssFileList)
					{
						if (fileInfo.getExtentionName().equalsIgnoreCase(updateSuffix))
						{
							returndssFileList.add(fileInfo);
						}
					}
				}
			}
		}

		return returndssFileList;
	}

	private List<StructureObject> getAllStrutureList(ObjectGuid objectGuid, List<RelationTemplateInfo> templates) throws ServiceRequestException
	{
		List<StructureObject> result = new ArrayList<StructureObject>();
		for (RelationTemplateInfo template : templates)
		{
			List<StructureObject> structureObjects = this.stubService.getBOAS().listObjectOfRelation(objectGuid, template.getName(), null, null, null);
			if (!SetUtils.isNullList(structureObjects))
			{
				result.addAll(structureObjects);
			}
		}
		return result;
	}

	private List<RelationTemplateInfo> getTemplates(String relationTemID) throws ServiceRequestException
	{
		List<RelationTemplateInfo> relationTemplates = new ArrayList<RelationTemplateInfo>();
		Set<String> nameSet = new HashSet<String>();
		String[] idArray = relationTemID.split(",");
		for (String id : idArray)
		{
			RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(id);
			if (template != null && !nameSet.contains(template.getName()))
			{
				relationTemplates.add(template);
			}
		}
		nameSet = null;
		return relationTemplates;
	}

	private List<DSSFileInfo> getEnd1FileList(List<RelationTemplateInfo> templates, ObjectGuid objectGuid, String[] synDownloadType) throws ServiceRequestException
	{
		Set<String> nameSet = new HashSet<String>();
		ClassInfo info = null;
		List<DSSFileInfo> result = new ArrayList<DSSFileInfo>();
		List<DSSFileInfo> temp = new ArrayList<DSSFileInfo>();
		for (RelationTemplateInfo template : templates)
		{
			nameSet.add(template.getName());
		}
		info = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		if (info != null)
		{
			if (info.hasInterface(ModelInterfaceEnum.ICAD2D) && nameSet.contains(BuiltinRelationNameEnum.CAD3DCAD2D.toString()))
			{
				temp.addAll(this.getEnd1FileListByInterface(objectGuid, BuiltinRelationNameEnum.CAD3DCAD2D.toString()));
			}
			else if (info.hasInterface(ModelInterfaceEnum.ICAD3D) && nameSet.contains(BuiltinRelationNameEnum.MODEL_MEMBER.toString()))
			{
				temp.addAll(this.getEnd1FileListByInterface(objectGuid, BuiltinRelationNameEnum.MODEL_MEMBER.toString()));
			}
		}
		if (!SetUtils.isNullList(temp))
		{
			for (String suffix : synDownloadType)
			{
				String updateSuffix = suffix.substring(2);
				for (DSSFileInfo fileInfo : temp)
				{
					if (fileInfo.getExtentionName().equalsIgnoreCase(updateSuffix))
					{
						result.add(fileInfo);
					}
				}
			}
		}
		return result;
	}

	private List<DSSFileInfo> getEnd1FileListByInterface(ObjectGuid objectGuid, String relationName) throws ServiceRequestException
	{
		List<DSSFileInfo> result = new ArrayList<DSSFileInfo>();
		List<FoundationObject> foundationObjects = this.stubService.getBOAS().listWhereReferenced(objectGuid, relationName, null, null);
		if (!SetUtils.isNullList(foundationObjects))
		{
			for (FoundationObject object : foundationObjects)
			{
				List<DSSFileInfo> fileList = this.stubService.getDSS().listFile(object.getObjectGuid(), object.getIterationId(), null);
				if (!SetUtils.isNullList(fileList))
				{
					result.addAll(fileList);
				}
			}
		}
		return result;
	}

	class StubjectTree
	{
		public FileOpenSubject		rootNode		= null;
		public List<StubjectTree>	childNodeList	= null;

		/**
		 * @return the parent
		 */
		public FileOpenSubject getrootNode()
		{
			return this.rootNode;
		}

		/**
		 * @param parent
		 *            the parent to set
		 */
		public void setrootNode(FileOpenSubject parent)
		{
			this.rootNode = parent;
		}

		/**
		 * @return the child
		 */
		public List<StubjectTree> getChildList()
		{
			return this.childNodeList;
		}

		/**
		 * @param child
		 *            the child to set
		 */
		public void setChildList(List<StubjectTree> child)
		{
			this.childNodeList = child;
		}

	}
}
