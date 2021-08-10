package dyna.app.service.brs.emm;

import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

import java.util.*;

public class ViewTableUIModelBuilder
{
	private EMMImpl									emm						= null;
	private String									relationTemplateGuid	= null;
	private boolean									isBOM					= false;
	private boolean									isTree					= false;

	private String									struClassGuid			= null;
	private String									struClassName			= null;
	private Map<String, UIObjectInfo>				struDefineUIMap			= null;
	private Map<String, Map<String, UIObjectInfo>>	end2DefineUIMap			= new HashMap<>();
	// private Map<String, Map<String, ClassField>> allClassFieldMap = new HashMap<>();
	private List<BOInfo>							end2BoList				= null;
	private List<UIField>							tableColumnList			= new ArrayList<>();
	private String									allLeafEnd2GlassGuid	= null;

	private Map<String, ClassField>					end2ClassFieldMap		= new HashMap<>();
	private Map<String, ClassField>					struClassFieldMap		= new HashMap<>();

	public ViewTableUIModelBuilder(EMMImpl emm, String relationTemplateGuid, boolean isBOM, boolean isTree)
	{
		this.emm = emm;
		this.relationTemplateGuid = relationTemplateGuid;
		this.isBOM = isBOM;
		this.isTree = isTree;
	}

	public void build() throws ServiceRequestException
	{
		end2BoList = emm.getViewModelStub().listRelationEnd2BoInfo(relationTemplateGuid, isBOM, isTree);
		if (SetUtils.isNullList(end2BoList))
		{
			return;
		}
		initUIInfo();
		initEnd2MultiClass();
		initStruClassFieldMap();
		processUIFieldInfo();
		buildFieldList();
	}

	private void processUIFieldInfo()
	{

	}

	private void buildFieldList() throws ServiceRequestException
	{
		if (!SetUtils.isNullMap(struDefineUIMap) && end2DefineUIMap.size() > 0)
		{
			buildFieldListDefineType();
		}
		else
		{
			buildFieldListListType();
		}
	}

	private void buildFieldListDefineType() throws ServiceRequestException
	{
		Set<String> nameSet = new HashSet<>();
		for (int i = 0; i < 9; i++)
		{
			UIObjectInfo uiObjectInfo = getDefineUIObject(String.valueOf(i));
			if (uiObjectInfo != null)
			{
				List<UIField> uiFeildList = emm.listUIFieldByUIGuid(uiObjectInfo.getGuid());
				if (!SetUtils.isNullList(uiFeildList))
				{
					for (UIField uf : uiFeildList)
					{
						if (!uf.isSeparator())
						{
							if (!nameSet.contains(uf.getName()))
							{
								nameSet.add(uf.getName());
								tableColumnList.add(uf);
								uf.setClassGuid(uiObjectInfo.getClassGuid());
							}
						}
					}
				}
			}
		}
	}

	private void initStruClassFieldMap() throws ServiceRequestException
	{
		List<ClassField> list = this.emm.listFieldOfClass(this.struClassName);
		if (list != null)
		{
			for (ClassField cf : list)
			{
				this.struClassFieldMap.put(cf.getName(), cf);
			}
		}
	}

	private UIObjectInfo getDefineUIObject(String i)
	{
		if (this.struDefineUIMap.containsKey(i))
		{
			return this.struDefineUIMap.get(i);
		}
		for (BOInfo binfo : end2BoList)
		{
			Map<String, UIObjectInfo> temp = end2DefineUIMap.get(binfo.getName());
			if (temp != null && temp.containsKey(i))
			{
				return temp.get(i);
			}
		}
		return null;
	}

	private void buildFieldListListType() throws ServiceRequestException
	{
		Set<String> nameSet = new HashSet<>();
		List<UIField> end2List = new ArrayList<>();
		for (BOInfo binfo : end2BoList)
		{
			if (struDefineUIMap.size() > 0)
			{
				this.getDefineUIfeildList(end2DefineUIMap.get(binfo.getName()), end2List, nameSet);
			}
			else
			{
				this.getClassListUIField(binfo.getClassName(), end2List, nameSet);
			}
		}
		List<UIField> struList = new ArrayList<>();
		if (SetUtils.isNullMap(struDefineUIMap))
		{
			this.getClassListUIField(struClassName, struList, nameSet);
		}
		else
		{
			this.getDefineUIfeildList(struDefineUIMap, struList, nameSet);
		}
		fillTableUIModel(end2List, struList);
	}

	private void fillTableUIModel(List<UIField> end2List, List<UIField> struList)
	{
		if (struList.size() == 0)
		{
			this.tableColumnList.addAll(end2List);
		}
		else if (end2List.size() == 0)
		{
			this.tableColumnList.addAll(struList);
		}
		else if (end2List.size() == 1)
		{
			this.tableColumnList.addAll(end2List);
			this.tableColumnList.addAll(struList);
		}
		else
		{
			this.tableColumnList.add(end2List.get(0));
			this.tableColumnList.addAll(struList);
			for (int i = 1; i < end2List.size(); i++)
			{
				this.tableColumnList.add(end2List.get(i));
			}
		}
	}

	private void initUIInfo() throws ServiceRequestException
	{
		if (isBOM)
		{
			BOMTemplate bomTemplate = this.emm.getBomTemplateStub().getBOMTemplate(relationTemplateGuid);
			struClassGuid = bomTemplate.getStructureClassGuid();
			struClassName = bomTemplate.getStructureClassName();
			struDefineUIMap = this.emm.getUIStub().getBOMDefineUI(struClassGuid);
			for (BOInfo binfo : end2BoList)
			{
				Map<String, UIObjectInfo> defineUIMap = this.emm.getUIStub().getBOMDefineUI(binfo.getClassGuid());
				if (!SetUtils.isNullMap(defineUIMap))
				{
					end2DefineUIMap.put(binfo.getName(), defineUIMap);
				}
			}
		}
		else
		{
			RelationTemplateInfo bomTemplate = this.emm.getRelationTemplateStub().getRelationTemplateInfo(relationTemplateGuid);
			struClassGuid = bomTemplate.getStructureClassGuid();
			struClassName = bomTemplate.getStructureClassName();
			struDefineUIMap = this.emm.getUIStub().getBOMDefineUI(struClassGuid);
			for (BOInfo binfo : end2BoList)
			{
				Map<String, UIObjectInfo> defineUIMap = this.emm.getUIStub().getAssoDefineUI(binfo.getClassGuid());
				if (!SetUtils.isNullMap(defineUIMap))
				{
					end2DefineUIMap.put(binfo.getName(), defineUIMap);
				}
			}
		}
	}

	private void getDefineUIfeildList(Map<String, UIObjectInfo> end2DefineUIMap, List<UIField> ufList, Set<String> end2NameSet) throws ServiceRequestException
	{
		if (SetUtils.isNullMap(end2DefineUIMap))
		{
			for (UIObjectInfo uiObjectInfo : end2DefineUIMap.values())
			{
				List<UIField> uiFeildList = emm.listUIFieldByUIGuid(uiObjectInfo.getGuid());
				if (!SetUtils.isNullList(uiFeildList))
				{
					for (UIField uf : uiFeildList)
					{
						if (!uf.isSeparator())
						{
							if (!end2NameSet.contains(uf.getName().toUpperCase()))
							{
								end2NameSet.add(uf.getName().toUpperCase());
								ufList.add(uf);
								uf.setClassGuid(uiObjectInfo.getClassGuid());
							}
						}
					}
				}
			}
		}
	}

	private void getClassListUIField(String className, List<UIField> ufList, Set<String> end2NameSet) throws ServiceRequestException
	{
		List<UIField> uiFeildList = emm.listListUIField(className);
		if (!SetUtils.isNullList(uiFeildList))
		{
			for (UIField uf : uiFeildList)
			{
				if (!uf.isSeparator())
				{
					if (!end2NameSet.contains(uf.getName().toUpperCase()))
					{
						end2NameSet.add(uf.getName().toUpperCase());
						ufList.add(uf);
						uf.setClassGuid(emm.getClassByName(className).getGuid());
					}
				}
			}
		}

	}

	public List<UIField> getResult() throws ServiceRequestException
	{
		if (tableColumnList.size() > 0)
		{
			for (int i = tableColumnList.size() - 1; i > -1; i--)
			{
				UIField uf = tableColumnList.get(i);
				if (struClassGuid.equals(uf.getClassGuid()))
				{
					uf.setType(this.struClassFieldMap.get(uf.getName()).getType());
				}
				else
				{
					if (end2ClassFieldMap.containsKey(uf.getName()))
					{
						uf.setType(end2ClassFieldMap.get(uf.getName()).getType());
						uf.setMulticlass(allLeafEnd2GlassGuid);
					}
					else
					{
						tableColumnList.remove(i);
					}
				}
			}
		}
		return this.tableColumnList;
	}

	private void initEnd2MultiClass() throws ServiceRequestException
	{
		Map<String, Integer> shareFieldCountMap = new HashMap<>();
		Map<String, ClassField> shareFieldMap = new HashMap<>();
		if (StringUtils.isNullString(this.allLeafEnd2GlassGuid))
		{
			Set<String> end2GuidSet = new HashSet<>();
			if (this.end2BoList != null)
			{
				for (BOInfo binfo : end2BoList)
				{
					List<ClassInfo> list = this.emm.getClassStub().listAllSubClassInfoOnlyLeaf(null, binfo.getClassName());
					if (list != null)
					{
						for (ClassInfo ci : list)
						{
							end2GuidSet.add(ci.getGuid());
						}
					}
					List<ClassField> fieldlist = this.emm.listFieldOfClass(binfo.getClassName());
					if (fieldlist != null)
					{
						for (ClassField cf : fieldlist)
						{
							if (shareFieldCountMap.containsKey(cf.getName()))
							{
								if (isSameTypeFiled(cf, shareFieldMap.get(cf.getName())))
								{
									shareFieldCountMap.put(cf.getName(), shareFieldCountMap.get(cf.getName()) + 1);
								}
							}
							else
							{
								shareFieldCountMap.put(cf.getName(), 1);
								shareFieldMap.put(cf.getName(), cf);
							}
						}
					}
				}
				for (String fn : shareFieldCountMap.keySet())
				{
					if (shareFieldCountMap.get(fn) == end2BoList.size())
					{
						this.end2ClassFieldMap.put(fn, shareFieldMap.get(fn));
					}
				}
				if (end2GuidSet.size() > 0)
				{
					Object[] guidArray = end2GuidSet.toArray();
					allLeafEnd2GlassGuid = (String) guidArray[0];
					for (int i = 1; i < guidArray.length; i++)
					{
						allLeafEnd2GlassGuid = allLeafEnd2GlassGuid + ";" + (String) guidArray[i];
					}
				}
			}
		}
	}

	private boolean isSameTypeFiled(ClassField cf, ClassField classField)
	{
		return cf.getType() == classField.getType();
	}

}
