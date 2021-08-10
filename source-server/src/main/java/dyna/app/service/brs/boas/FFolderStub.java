/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FFolderStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.acl.ACLImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.Folder;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.systemenum.RelationTemplateActionEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.folder.FolderService;
import dyna.net.service.brs.EMM;

/**
 * @author Wanglei
 * 
 */
public class FFolderStub extends AbstractServiceStub<BOASImpl>
{
	/**
	 * @param context
	 * @param service
	 */
	public FFolderStub(ServiceContext context, BOASImpl service)
	{
		super(context, service);
	}

	public void moveToFolder(ObjectGuid objectGuid, String fromFolderGuid, String toFolderGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		String sessionId = this.stubService.getSignature().getCredential();

		try
		{

			FoundationObject foundationObject = this.stubService.getFoundationStub().getObject(objectGuid, isCheckAuth);
			boolean toFolderGuidAuthorize = ((ACLImpl) this.stubService.getACL()).getFolderACLStub().hasUserAuthorize4Lib(toFolderGuid, isCheckAuth);

			boolean fromFolderGuidAuthorize = ((ACLImpl) this.stubService.getACL()).getFolderACLStub().hasUserAuthorize4Lib(fromFolderGuid, isCheckAuth);

			// DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			ds.moveToFolder(objectGuid, fromFolderGuid, toFolderGuid, !fromFolderGuidAuthorize, !toFolderGuidAuthorize, true, true, sessionId, this.stubService.getFixedTransactionId());

			boolean isChangeLocation = false;
			Folder toFolder = this.stubService.getEDAP().getFolder(toFolderGuid);

			// 如果目标文件夹是库 判断对象所在的库与目标文件夹所对应的库不一致的话 属于changeLocation
			if (toFolder.getType().equals(FolderTypeEnum.LIB_FOLDER) && !toFolder.getLibraryUser().equals(foundationObject.getLocationlib()))
			{
				isChangeLocation = true;

			}// 如果目标文件夹是库内文件夹 判断对象所在的库与目标文件夹所对应的库不一致的话 属于changeLocation
			else if (toFolder.getType().equals(FolderTypeEnum.LIBRARY) && !toFolder.getGuid().equals(foundationObject.getLocationlib()))
			{
				isChangeLocation = true;
			}

			if (isChangeLocation)
			{
				EMM emm = this.stubService.getEMM();

				// 处理relation
				// 查找所有关联的ViewObject
				List<ViewObject> viewObjectList = this.stubService.getRelationStub().listRelation(objectGuid, isCheckAuth, true);

				if (!SetUtils.isNullList(viewObjectList))
				{
					for (ViewObject viewObject : viewObjectList)
					{
						RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.get(ViewObject.TEMPLATE_ID) == null ? "" : (String) viewObject
								.get(ViewObject.TEMPLATE_ID));
						if (relationTemplate == null)
						{
							continue;
						}

						if (RelationTemplateActionEnum.NONE.equals(relationTemplate.getChangeLocationTrigger()))
						{
							// 不处理end2
							continue;
						}
						else if (RelationTemplateActionEnum.CHANGELOCATION.equals(relationTemplate.getChangeLocationTrigger()))
						{
							// 关联处理end2
							try
							{
								String structureClassName = relationTemplate.getStructureClassName();

								List<UIObjectInfo> uiObjectList = emm.listUIObjectInCurrentBizModel(structureClassName, UITypeEnum.FORM, true);
								SearchCondition searchCondition = null;
								if (!SetUtils.isNullList(uiObjectList))
								{
									searchCondition = SearchConditionFactory.createSearchConditionForStructure(structureClassName);
									for (UIObjectInfo uiObject : uiObjectList)
									{
										searchCondition.addResultUIObjectName(uiObject.getName());
									}
								}

								List<StructureObject> structureObjectList = this.stubService.getRelationStub().listObjectOfRelation(viewObject.getObjectGuid(), searchCondition,
										null, null, isCheckAuth);
								if (!SetUtils.isNullList(structureObjectList))
								{
									for (StructureObject structureObject : structureObjectList)
									{
										try
										{
											FoundationObject end2FoundationObject = this.stubService.getFoundationStub()
													.getObject(structureObject.getEnd2ObjectGuid(), isCheckAuth);
											if (end2FoundationObject != null)
											{
												this.moveToFolder(end2FoundationObject.getObjectGuid(), fromFolderGuid, toFolderGuid, isCheckAuth);
											}
										}
										catch (Exception e)
										{
											continue;
										}
									}
								}
							}
							catch (Exception e)
							{
								continue;
							}
						}
					}

				}
			}

			// DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}

	}

	protected List<Folder> listFolder(ObjectGuid foundationObjectGuid) throws ServiceRequestException
	{
		FolderService ds = DataServer.getFolderService();

		String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			return ds.listFolder(foundationObjectGuid.getGuid(), Constants.isSupervisor(true, this.stubService), sessionId);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		finally
		{
		}
	}
}
