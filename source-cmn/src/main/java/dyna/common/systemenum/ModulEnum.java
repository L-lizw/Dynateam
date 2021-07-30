package dyna.common.systemenum;

import dyna.common.util.StringUtils;

public enum ModulEnum
{
	HOME("dyna.client.home.myhomedynaobjecteditorinput", "home;主页;主頁"), // 主页
	INSTANCE("dyna.client.instance.editorInput", "instance;实例;實例"), // 实例
	ADVANCESEARCH("dyna.client.search.editorInput.advancedSearch", "advancesearch;高级搜索;高級搜索"), // 高级搜索
	CONFIGPARAMETER("dyna.client.instance.configParameterEditorInput", "configParameter;配置产品参数;配置產品參數"), // 配置产品参数
	DRIVERTEST("dyna.client.config.drivertest.editorInput", "driverTest;驱动测试;驅動測試"), // 驱动测试
	LIBRARY("dyna.client.workspace.library.editorInput", "library;公共库;公共庫"), // 公共库
	PREDEFINE("dyna.client.search.editorInput.predefineSearch", "predefine;预定义搜索;預定義搜索");// 预定义搜索

	private String	title		= null;
	private String	editorID	= null;

	private ModulEnum(String editorID, String title)
	{
		this.editorID = editorID;
		this.title = title;
	}

	public String getEditorId()
	{
		return this.editorID;
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.title, lang.getType());
	}

	public String getTitle()
	{
		return this.title;
	}

	public static ModulEnum getPositionByEditorId(String editorId)
	{
		for (ModulEnum modul : ModulEnum.values())
		{
			if (modul.getEditorId().equals(editorId))
			{
				return modul;
			}
		}
		return null;
	}

}
