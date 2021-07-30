/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 系统内置关系模板
 * Jiagang 2011-08-08
 */
package dyna.common.systemenum;

/**
 * 内置关系名
 * 
 * @author WangLHB
 * 
 */
public enum BuiltinRelationNameEnum
{

	// ITEM-CAD2D$、ITEM-CAD3D$、ITEM-ECAD$

	// CAD2DITEM("CAD2D-ITEM$"), CAD3DITEM("CAD3D-ITEM$"), ECADITEM("ECAD-ITEM$"), CAD3DCAD2D("CAD3D-CAD2D$");
	ITEMCAD2D("ITEM-CAD2D$"), ITEMCAD3D("ITEM-CAD3D$"), ITEMECAD("ITEM-ECAD$"), CAD3DCAD2D("CAD3D-CAD2D$"), MODEL_STRUCTURE("MODEL-STRUCTURE"), // 装配关系
	MODEL_REFERENCE("MODEL-REFERENCE"), // 引用关系
	MODEL_MEMBER("MODEL-MEMBER"), // 族表关系
	PM_CHANGE("PROJECT-PMCHANGEREQUEST"), // 项目管理变更请求
	// 2012.10.16变更去掉内置模板MODELREFERENCE
	// MODELREFERENCE("MODEL-REFERENCE$");

	TRANSFROM("CONVERTRELATION");

	private String	name	= null;

	private BuiltinRelationNameEnum(String name)
	{
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.name;
	}

	public static String[] getCADItemRelationName()
	{
		return new String[] { ITEMCAD2D.toString(), ITEMCAD3D.toString(), ITEMECAD.toString() };
	}
}
