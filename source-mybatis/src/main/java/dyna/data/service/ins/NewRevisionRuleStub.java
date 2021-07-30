package dyna.data.service.ins;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import org.springframework.beans.factory.annotation.Autowired;

public class NewRevisionRuleStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	@Autowired
	private FoundationObjectMapper          foundationObjectMapper;
	protected NewRevisionRuleStub(DataServerContext context, InstanceServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 取得当前对象的下一个版本号
	 *
	 * @param foundationGuid
	 * @param className
	 * @return
	 * @throws SQLException
	 */
	public String getNextRevisionId(String foundationGuid, String className) throws ServiceRequestException
	{
		int maxRevisionIdSequence = this.getMaxRevisionIdSequence(foundationGuid, className);
		return this.getNextRevisionId(maxRevisionIdSequence);
	}

	@SuppressWarnings("unchecked")
	public String getNextRevisionId(int currentRevisionIdSequence) throws ServiceRequestException
	{
		try
		{
			Map<String, Object> revMap = (Map<String, Object>) this.dynaObjectMapper.getConfigRuleRevise();
			if (!SetUtils.isNullMap(revMap))
			{
				String type = (String) revMap.get("REVISETYPE");
				String value = (String) revMap.get("REVISEVALUE");
				if ("0".equals(type))
				{

					StringBuilder buffer = new StringBuilder();
					int mod = new BigDecimal(String.valueOf(currentRevisionIdSequence + 1)).divide(new BigDecimal("26"), 0, BigDecimal.ROUND_UP).intValue();
					if (mod == 1)
					{
						// 26个以内版本
						if ((currentRevisionIdSequence + 1) % 26 == 0)
						{
							return "Z";
						}
						return String.valueOf((char) (((currentRevisionIdSequence + 1) % 26) + 64));
					}
					else
					{
						// 超过26个版本
						for (int i = 0; i < mod - 1; i++)
						{
							buffer.append("A");
						}
						if ((currentRevisionIdSequence + 1) % 26 != 0)
						{
							buffer.append((char) (((currentRevisionIdSequence + 1) % 26) + 64));
						}
						else
						{
							buffer.append("Z");
						}
						return buffer.toString();
					}
				}
				else if ("1".equals(type))
				{
					return String.valueOf(currentRevisionIdSequence + 1);
				}
				else if ("2".equals(type) && !StringUtils.isNullString(value))
				{
					String[] tmpArr = value.split(",");
					if (currentRevisionIdSequence + 1 < tmpArr.length)
					{
						return tmpArr[currentRevisionIdSequence + 1];
					}
				}
			}
			throw new DynaDataExceptionAll("-20011", null, DataExceptionEnum.DS_PROCEDURE_20011);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	/**
	 * 取得当前对象的最大版本顺序
	 *
	 * @param foundationGuid
	 * @param className
	 * @return
	 * @throws SQLException
	 */
	public int getMaxRevisionIdSequence(String foundationGuid, String className) throws ServiceRequestException
	{
		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("tablename", DataServer.getDSCommonService().getTableName(className));
			param.put("GUID", foundationGuid);
			FoundationObject foundationObject = (FoundationObject) this.foundationObjectMapper.getFoundationByGuid(param);
			String masterGuid = foundationObject.getObjectGuid().getMasterGuid();

			param.put("MASTERGUID", masterGuid);
			return this.foundationObjectMapper.getMaxRevisionIdSequence(param);
//			return ((BigDecimal) tmp.get("REVISIONIDSEQUENCE")).intValue();
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}
}
