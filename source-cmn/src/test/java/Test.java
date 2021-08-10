import dyna.common.dto.aas.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lizw
 * @date 2021/7/29
 **/
@Configuration
public class Test
{
	public static void main(String[] args)
	{
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Test.class);
		User user1 = applicationContext.getBean(User.class,"name='super'");
		System.out.println(user1);
//		User user2 = applicationContext.getBean("super",User.class);
//		System.out.println(user2);
	}

	@Bean(name = "admin")
	public User createUser()
	{
		User user = new User();
		user.setName("admin");
		return user;
	}

//	@Bean(name = "super")
//	public User createUsertest()
//	{
//		User user = new User();
//		user.setName("super");
//		return user;
//	}
}
