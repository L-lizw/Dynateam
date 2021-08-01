package dyna.data.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Lizw
 * @date 2021/7/25
 **/
@Configuration
@ComponentScan(basePackages = {"dyna.common","dyna.data","dyna.net"})
@Import({MybatisConfig.class})
public class SpringConfigForData
{


}
