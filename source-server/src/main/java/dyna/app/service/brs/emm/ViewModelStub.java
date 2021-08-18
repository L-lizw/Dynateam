package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ViewModelStub extends AbstractServiceStub<EMMImpl>
{

	public ClassInfo getClassInfoByBOGuid(String boGuid) throws ServiceRequestException
	{
		BOInfo boInfo = this.stubService.getCurrentBizObjectByGuid(boGuid);
		if (boInfo == null)
		{
			return null;
		}
		else
		{
			return this.stubService.getClassByGuid(boInfo.getClassGuid());
		}
	}

	public LifecycleInfo getLifecycleInfoByBOGuid(String boGuid) throws ServiceRequestException
	{
		BOInfo boInfo = this.stubService.getCurrentBizObjectByGuid(boGuid);
		if (boInfo == null)
		{
			return null;
		}
		else
		{
			ClassInfo classInfo = this.stubService.getClassByGuid(boInfo.getClassGuid());
			if (classInfo == null || !StringUtils.isGuid(classInfo.getLifecycle()))
			{
				return null;
			}
			else
			{
				return this.stubService.getLifecycleInfoByGuid(classInfo.getLifecycle());
			}
		}
	}

	protected List<BOInfo> listBOInfoByClassficationGuid(String classficationGuid) throws ServiceRequestException
	{

		List<ClassficationFeature> classficationFeatureList = this.stubService.getClassificationStub().listClassficationFeatureByClassification(classficationGuid);
		List<BOInfo> boInfoList = new ArrayList<>();
		if (!SetUtils.isNullList(classficationFeatureList))
		{
			for (ClassficationFeature feature : classficationFeatureList)
			{
				BOInfo currentBizObject = this.stubService.getCurrentBizObject(feature.getClassGuid());
				if (currentBizObject != null)
				{
					boInfoList.add(currentBizObject);
				}
			}
		}
		return boInfoList;
	}

	public List<BOInfo> listRelationEnd2BoInfo(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException
	{
		Set<String> end2BoNameList = new HashSet<String>();
		if (isBOM)
		{
			end2BoNameList = this.stubService.getBomTemplateStub().getEnd2BONameSet(relationTemplateGuid, isTree);
		}
		else
		{
			end2BoNameList = this.stubService.getRelationTemplateStub().getEnd2BONameSet(relationTemplateGuid, isTree);
		}
		List<BOInfo> returnList = new ArrayList<>();
		if (end2BoNameList.size() > 0)
		{
			List<BOInfo> tempBoList = this.stubService.getBMStub().listBizObjectOfCurrntModel();
			for (BOInfo info : tempBoList)
			{
				if (end2BoNameList.contains(info.getName()))
				{
					returnList.add(info);
				}
			}
		}
		return returnList;
	}

	public List<BOInfo> listEnd2LeafBoInfoByTemplateGuid(String guid, boolean isBOM) throws ServiceRequestException
	{
		List<BOInfo> tempBoList = this.listRelationEnd2BoInfo(guid, isBOM, false);
		List<BOInfo> returnList = new ArrayList<>();
		if (!SetUtils.isNullList(tempBoList))
		{
			Set<String> end2ClassGuidList = new HashSet<String>();
			for (BOInfo binfo : tempBoList)
			{
				List<ClassInfo> classList = this.stubService.getClassStub().listAllSubClassInfoOnlyLeaf(null, binfo.getClassName());
				if (!SetUtils.isNullList(classList))
				{
					for (ClassInfo cinfo : classList)
					{
						end2ClassGuidList.add(cinfo.getGuid());
					}
				}
			}
			if (end2ClassGuidList.size() > 0)
			{
				tempBoList = this.stubService.getBMStub().listBizObjectOfCurrntModel();
				for (BOInfo binfo : tempBoList)
				{
					if (end2ClassGuidList.contains(binfo.getClassGuid()))
					{
						returnList.add(binfo);
					}
				}
			}

		}
		return returnList;
	}

	public List<UIField> listAssoUITableColumn(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException
	{
		ViewTableUIModelBuilder builder = new ViewTableUIModelBuilder(stubService, relationTemplateGuid, isBOM, isTree);
		builder.build();
		return builder.getResult();
	}

	public SearchCondition createClassificationSearchCondition(String className, String itemName)
	{
		return null;
	}

	public SearchCondition createAssoEnd2SearchCondition(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException
	{
		List<BOInfo> tempBoList = this.listRelationEnd2BoInfo(relationTemplateGuid, isBOM, isTree);
		if (!SetUtils.isNullList(tempBoList))
		{
			boolean hasDenfineUI = false;
			List<String> limitClassNameList = new ArrayList<>();
			SearchCondition condition = SearchConditionFactory.createSearchCondition4MulitClassSearch(limitClassNameList);
			for (BOInfo binfo : tempBoList)
			{
				limitClassNameList.add(binfo.getClassName());
				Map<String, UIObjectInfo> defineUIMap = null;
				if (isBOM)
				{
					defineUIMap = this.stubService.getUIStub().getBOMDefineUI(binfo.getClassGuid());
				}
				else
				{
					defineUIMap = this.stubService.getUIStub().getAssoDefineUI(binfo.getClassGuid());
				}
				if (!SetUtils.isNullMap(defineUIMap))
				{
					hasDenfineUI = true;
					for (int i = 0; i < 9; i++)
					{
						UIObjectInfo uiObjectInfo = defineUIMap.get(String.valueOf(i));
						if (uiObjectInfo != null)
						{
							List<UIField> uiFeildList = this.stubService.listUIFieldByUIGuid(uiObjectInfo.getGuid());
							setSeachCondition(condition, uiFeildList);
						}
					}
				}
			}
			if (hasDenfineUI == false)
			{
				for (BOInfo binfo : tempBoList)
				{
					List<UIField> uiFeildList = this.stubService.listListUIField(binfo.getClassName());
					setSeachCondition(condition, uiFeildList);
				}
			}
			return condition;
		}
		return null;
	}

	private void setSeachCondition(SearchCondition condition, List<UIField> uiFeildList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(uiFeildList))
		{
			for (UIField uf : uiFeildList)
			{
				if (!uf.isSeparator())
				{
					condition.addResultField(uf.getName());
				}
			}
		}
	}

}
