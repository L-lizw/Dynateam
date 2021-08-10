package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.EMM;

public class ObjectGuidFieldConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3176324305211849517L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String val = StringUtils.convertNULLtoString(this.getFoundationObject().get(this.getFieldName()));
		if (this.getOperatorSign() == OperateSignEnum.EQUALS)
		{
			return StringUtils.isGuid(val) ? false : this.isSame(val);
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
		{
			return StringUtils.isGuid(val) ? false : !this.isSame(val);
		}
		return true;
	}

	private boolean isSameUser(String guid) throws ServiceRequestException
	{
		String id = this.getValue();
		if (this.isUser())
		{
			User user = this.getAAS().getUserById(id);
			if (user == null)
			{
				return true;
			}
			return guid.equals(user.getGuid());
		}
		return false;
	}

	private boolean isSameGroup(String guid) throws ServiceRequestException
	{
		String id = this.getValue();
		if (this.isGroup())
		{
			Group group = this.getAAS().getGroupById(id);
			if (group == null)
			{
				return true;
			}
			return guid.equals(group.getGuid());
		}
		return false;
	}

	private boolean isSameRole(String guid) throws ServiceRequestException
	{
		String id = this.getValue();
		if (this.isRole())
		{
			Role role = this.getAAS().getRoleById(id);
			if (role == null)
			{
				return true;
			}
			return guid.equals(role.getGuid());
		}
		return false;
	}

	private boolean isSameObject(String guid)
	{
		return true;
	}

	private ClassInfo getTypeValueClassInfo() throws ServiceRequestException
	{
		String className = this.getFoundationObject().getObjectGuid().getClassName();
		ClassField field = this.getEMM().getFieldByName(className, this.getFieldName(), true);
		String typeValue = field.getTypeValue();
		if (!StringUtils.isNullString(typeValue))
		{
			return this.getEMM().getClassByName(typeValue);
		}
		return null;
	}

	private boolean isUser() throws ServiceRequestException
	{
		ClassInfo classInfo_ = this.getTypeValueClassInfo();
		return classInfo_ == null ? false : classInfo_.hasInterface(ModelInterfaceEnum.IUser);
	}

	private boolean isGroup() throws ServiceRequestException
	{
		ClassInfo classInfo_ = this.getTypeValueClassInfo();
		return classInfo_ == null ? false : classInfo_.hasInterface(ModelInterfaceEnum.IGroup);
	}

	private boolean isRole() throws ServiceRequestException
	{
		ClassInfo classInfo_ = this.getTypeValueClassInfo();
		return classInfo_ == null ? false : classInfo_.hasInterface(ModelInterfaceEnum.IPMRole);
	}

	private boolean isSame(String guid) throws ServiceRequestException
	{
		return this.isSameUser(guid) || this.isSameGroup(guid) || this.isSameRole(guid) || this.isSameObject(guid);
	}

	public String getValue() throws ServiceRequestException
	{
		Object o = super.getValue();
		if (o == null)
		{
			return StringUtils.EMPTY_STRING;
		}
		return o.toString();
	}

	private EMM getEMM() throws ServiceRequestException
	{
		return this.getServiceInstance(EMM.class);
	}

	private AAS getAAS() throws ServiceRequestException
	{
		return this.getServiceInstance(AAS.class);
	}
}
