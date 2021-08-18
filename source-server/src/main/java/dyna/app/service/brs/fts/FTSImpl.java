package dyna.app.service.brs.fts;

import dyna.app.server.Server;
import dyna.app.service.BusinessRuleService;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.trans.*;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobStatus;
import dyna.common.util.SetUtils;
import dyna.net.service.brs.*;
import dyna.net.service.das.MSRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
public class FTSImpl extends BusinessRuleService implements FTS
{
	@Autowired
	private TransformStub		transformStub		= null;
	@Autowired
	private TransformConfigStub	transformConfigStub	= null;
	@Autowired
	private TransformSignStub	transformSignStub	= null;
	private static boolean		isInit				= false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		try
		{
			super.authorize(method, args);
		}
		catch (AuthorizeException e)
		{
			if (this.getSignature() == null)
			{
				throw e;
			}
		}
	}

	protected synchronized DSS getDSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DSS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

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

	protected synchronized LIC getLIC() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(LIC.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized SMS getSMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(SMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized BOAS getBOAS() throws ServiceRequestException
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

	protected synchronized WFM getWFM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized AAS getAAS() throws ServiceRequestException
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

	protected synchronized WFI getWFI() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public TransformStub getTransformStub()
	{
		return this.transformStub;
	}

	public TransformConfigStub getTransformConfigStub()
	{
		return this.transformConfigStub;
	}

	public TransformSignStub getTransformSignStub()
	{
		return this.transformSignStub;
	}

	private synchronized static void timingNotice(Server server)
	{
		if (isInit)
		{
			return;
		}
		isInit = true;

		server.addServerContextListener(new TimeTaskServerContextListener());
	}

	@Override
	public void init()
	{
		timingNotice(this.server);
	}

	/*
	 * 获取所有子队列
	 */
	@Override
	public List<TransformQueue> listQueue(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getTransformStub().listQueue(condition, true);
	}

	@Override
	public List<TransformQueue> listQueue4Trans(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getTransformStub().listQueue4Trans(condition);
	}

	/**
	 * 获取根据队列获取所有子队列
	 * 
	 * @param queueGuid
	 * @return
	 */
	@Override
	public TransformQueue getQueueInfo(String queueGuid) throws ServiceRequestException
	{
		return this.getTransformStub().getQueue(queueGuid);
	}

	/**
	 * 生更上传任务单号
	 * 如果配置中需要重新生成对象，则新生成
	 * 
	 * @param transfromQueueGuid
	 * @param fileName
	 * @return
	 */
	@Override
	public DSSFileTrans uploadQueueInfo(String queueGuid, DSSFileInfo fileInfo) throws ServiceRequestException
	{
		return this.getTransformStub().uploadQueueInfo(queueGuid, fileInfo);
	}

	/**
	 * 更新任务队列
	 * 
	 * @param childGuid
	 * @param fileMD5
	 */
	@Override
	public TransformQueue updateQueueInfo(String queueGuid, String status, String result) throws ServiceRequestException
	{
		return this.getTransformStub().updateQueueInfo(queueGuid, status, result);
	}

	/**
	 * 重启队列
	 * 
	 * @param queue
	 * @throws ServiceRequestException
	 */
	@Override
	public void reStartQueue(TransformQueue queue) throws ServiceRequestException
	{
		this.getTransformStub().reStartQueue(queue);
	}

	@Override
	public void deleteQueue(TransformQueue queue) throws ServiceRequestException
	{
		this.getTransformStub().deleteQueue(queue);
	}

	@Override
	public TransformConfig saveTransformConfig(TransformConfig config) throws ServiceRequestException
	{
		return this.getTransformConfigStub().saveTransformConfig(config);
	}

	@Override
	public TransformConfig getTransformConfig(String transConfigGuid) throws ServiceRequestException
	{
		return this.getTransformConfigStub().getTransformConfig(transConfigGuid);
	}

	@Override
	public void createTransformQueue4WF(ActivityRuntime activity) throws ServiceRequestException
	{
		this.getTransformStub().createTransformQueue4WF(activity);
	}

	@Override
	public void createTransformQueue4Manual(ObjectGuid objectGuid, TransformConfig config) throws ServiceRequestException
	{
		this.getTransformStub().createTransformQueue4Manual(objectGuid, config);
	}

	@Override
	public void createTransformQueue4Checkin(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getTransformStub().createTransformQueue4Checkin(objectGuid);
	}

	@Override
	public void createTransformQueue4Upload(ObjectGuid objectGuid, String fileGuid) throws ServiceRequestException
	{
		this.getTransformStub().createTransformQueue4Upload(objectGuid, fileGuid);
	}

	@Override
	public List<TransformWFConfig> listWFConfig(String transConfigGuid) throws ServiceRequestException
	{
		return this.getTransformConfigStub().listWFConfig(transConfigGuid);
	}

	@Override
	public void saveWFConfig(TransformWFConfig wfConfig) throws ServiceRequestException
	{
		this.getTransformConfigStub().saveWFConfig(wfConfig);
	}

	@Override
	public void deleteWFConfig(String wfConfigGuid) throws ServiceRequestException
	{
		this.getTransformConfigStub().deleteWFConfig(wfConfigGuid);

	}

	@Override
	public TransformObjectConfig getObjectConfig(String transConfigGuid) throws ServiceRequestException
	{
		return this.getTransformConfigStub().getObjectConfig(transConfigGuid);
	}

	@Override
	public void saveObjectConfig(TransformObjectConfig objectConfig) throws ServiceRequestException
	{
		this.getTransformConfigStub().saveObjectConfig(objectConfig);
	}

	@Override
	public void deleteObjectConfig(String objectConfig) throws ServiceRequestException
	{
		this.getTransformConfigStub().deleteObjectConfig(objectConfig);

	}

	@Override
	public TransformManualConfig getManualConfig(String transConfigGuid) throws ServiceRequestException
	{
		return this.getTransformConfigStub().getManualConfig(transConfigGuid);
	}

	@Override
	public void saveManualConfig(TransformManualConfig manualConfig) throws ServiceRequestException
	{
		this.getTransformConfigStub().saveManualConfig(manualConfig);
	}

	@Override
	public void deleteManualConfig(String manualConfig) throws ServiceRequestException
	{
		this.getTransformConfigStub().deleteManualConfig(manualConfig);

	}

	@Override
	public List<TransformFieldMapping> listFieldMappingConfig(String transConfigGuid) throws ServiceRequestException
	{
		return this.getTransformConfigStub().listFieldMappingConfig(transConfigGuid);
	}

	@Override
	public void saveFieldMapping(TransformFieldMapping fieldMapping) throws ServiceRequestException
	{
		this.getTransformConfigStub().saveFieldMapping(fieldMapping);
	}

	@Override
	public void deleteFieldMapping(String fieldMapping) throws ServiceRequestException
	{
		this.getTransformConfigStub().deleteFieldMapping(fieldMapping);
	}

	@Override
	public void deleteTransformConfig(String configGuid) throws ServiceRequestException
	{
		this.getTransformConfigStub().deleteTransformConfig(configGuid);
	}

	@Override
	public List<TransformConfig> listTransformConfig(Map<String, Object> filter) throws ServiceRequestException
	{

		return this.getTransformConfigStub().listTransformConfig(filter, true);
	}

	@Override
	public List<TransformConfig> listTransformConfigContain(Map<String, Object> filter) throws ServiceRequestException
	{
		List<TransformConfig> listTransformConfig = this.getTransformConfigStub().listTransformConfig(filter, false);

		if (!SetUtils.isNullList(listTransformConfig))
		{
			for (TransformConfig config : listTransformConfig)
			{
				config.setWfConfigList(this.listWFConfig(config.getGuid()));
				config.setFieldConfigList(this.listFieldMappingConfig(config.getGuid()));
				config.setObjectConfig(this.getObjectConfig(config.getGuid()));
				config.setManualConfig(this.getManualConfig(config.getGuid()));
			}
		}
		return listTransformConfig;

	}

	@Override
	public List<TransformConfig> listTransformConfig4Manual(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getTransformConfigStub().listTransformConfig4Manual(objectGuid);
	}

	@Override
	public List<TransformSignParam> listTransformSignParam(String queueGuid) throws ServiceRequestException
	{
		return this.getTransformSignStub().listTransformSignParam(queueGuid);
	}

	@Override
	public void deleteTransformSign(String configGuid) throws ServiceRequestException
	{
		this.getTransformSignStub().deleteTransformSign(configGuid);

	}

	@Override
	public TransformSignParam saveSignParam(TransformSignParam signParam) throws ServiceRequestException
	{
		return this.getTransformSignStub().saveSignParam(signParam);
	}

	@Override
	public List<TransformSignParam> listTransformSignParam(Map<String, Object> filter) throws ServiceRequestException
	{

		return this.getTransformSignStub().listTransformSignParam(filter);
	}

	@Override
	public void deleteTransformSigParam(String configGuid) throws ServiceRequestException
	{
		this.getTransformSignStub().deleteTransformSigParam(configGuid);

	}

	@Override
	public void saveSignObjectMap(TransformSignObjectMap signObjectMap) throws ServiceRequestException
	{
		this.getTransformSignStub().saveSignObjectMap(signObjectMap);

	}

	@Override
	public List<TransformSignObjectMap> listTransformSignObjectMap(Map<String, Object> filter) throws ServiceRequestException
	{

		return this.getTransformSignStub().listTransformSignObjectMap(filter);
	}

	@Override
	public void deleteTransformSigObjectMap(String configGuid) throws ServiceRequestException
	{
		this.getTransformSignStub().deleteTransformSigObjectMap(configGuid);

	}

	@Override
	public void saveSignWFMap(TransformSignWFMap signWFMap) throws ServiceRequestException
	{
		this.getTransformSignStub().saveSignWFMap(signWFMap);

	}

	@Override
	public List<TransformSignWFMap> listTransformSignWFMap(Map<String, Object> filter) throws ServiceRequestException
	{

		return this.getTransformSignStub().listTransformSignWFMap(filter);
	}

	@Override
	public void deleteTransformSignWFMap(String configGuid) throws ServiceRequestException
	{
		this.getTransformSignStub().deleteTransformSignWFMap(configGuid);
	}

	@Override
	public TransformSign saveTransSign(TransformSign sign) throws ServiceRequestException
	{
		return this.getTransformSignStub().saveTransSign(sign);
	}

	@Override
	public DSSFileTrans downloadQueueInfo(String queueGuid) throws ServiceRequestException
	{
		return this.getTransformStub().downloadQueueInfo(queueGuid, false);
	}

	@Override
	public List<TransformSign> listTransformSign(Map<String, Object> filter) throws ServiceRequestException
	{

		return this.getTransformSignStub().listTransformSign(filter, true);
	}

	@Override
	public List<TransformConfig> saveTransformConfig(List<TransformConfig> configList) throws ServiceRequestException
	{
		return this.getTransformConfigStub().saveTransformConfig(configList);
	}

	@Override
	public List<TransformSign> saveTransSign(List<TransformSign> signList) throws ServiceRequestException
	{
		return this.getTransformSignStub().saveTransSign(signList);
	}

	@Override
	public TransformQueue changeQueueStatus(TransformQueue fo, JobStatus jobStatus) throws ServiceRequestException
	{
		return this.getTransformStub().changeQueueStatus(fo, jobStatus);
	}

	@Override
	public void saveTransformServers(String serverID, String[] transformTypes) throws ServiceRequestException
	{
		this.getTransformStub().saveTransformServers(serverID, transformTypes);
	}

	@Override
	public List<String> listTLicenseModules() throws ServiceRequestException
	{
		return this.getTransformConfigStub().listTLicenseModules();
	}

}
