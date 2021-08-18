/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECSImpl
 * caogc 2010-11-02
 */
package dyna.app.service.brs.pms;

import dyna.app.service.BusinessRuleService;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.ItemProduct;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.brs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Engineering Change Service Implement工程变更服务的实现类
 * 
 * @author caogc
 * 
 */
@Service
public class PMSImpl extends BusinessRuleService implements PMS
{
	private static boolean	initialized	= false;

	private static synchronized void syncInit()
	{
		// initialize only once.
		if (initialized)
		{
			return;
		}

		initialized = true;

	}

	@Autowired
	private ItemProductStub		itemProductStub		= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(dyna.net.security.signature.Signature)
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		super.authorize(method, args);
	}

	@Override
	public void deleteItemProduct(List<String> itemProductGuidList) throws ServiceRequestException
	{
		this.getItemProductStub().deleteItemProduct(itemProductGuidList);
	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * @return the ecItemProductStub
	 */
	public ItemProductStub getItemProductStub()
	{
		return this.itemProductStub;
	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized POS getPOS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(POS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public void init()
	{
		syncInit();
	}

	@Override
	public List<ItemProduct> listItemProductByItem(ObjectGuid itemObjectGuid) throws ServiceRequestException
	{
		return this.getItemProductStub().listItemProductByItem(itemObjectGuid);
	}

	@Override
	public void deleteItemProductByItem(ObjectGuid itemObjectGuid) throws ServiceRequestException
	{
		this.getItemProductStub().deleteItemProductByItem(itemObjectGuid);
	}

	@Override
	public void addToProduct(ObjectGuid itemObjectGuid, List<ObjectGuid> productObjectGuidList) throws ServiceRequestException
	{
		this.getItemProductStub().addToProduct(itemObjectGuid, productObjectGuidList);
	}


	@Override
	public List<FoundationObject> listProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String viewList)
			throws ServiceRequestException
	{
		return this.getItemProductStub().listProductSummaryObject(productObjectGuid, searchCondition, viewList, true);
	}

	@Override
	public List<RelationTemplateInfo> listProductSummaryRelationTemplate(ObjectGuid productObjectGuid) throws ServiceRequestException
	{
		return this.getItemProductStub().listProductSummaryRelationTemplate(productObjectGuid);
	}
}
