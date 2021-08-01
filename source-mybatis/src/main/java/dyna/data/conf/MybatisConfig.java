package dyna.data.conf;

import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lizw
 * @date 2021/7/30
 **/
@Configuration @PropertySource("classpath:dm/mybatis.properties") @MapperScan("dyna.common.dtomapper") public class MybatisConfig
{
	//mybatis总配置文件
	public static final String BASE_LOCATION         = "classpath:dm/mybatis-config.xml";
	//mybatis配置文件位置
	public static final String MAPPER_LOCATION       = "classpath:dm/sql/**.xml";
	//其他的
	public static final String MAPPER_LOCATION_OTHER = "classpath:dm/sql/**/**.xml";

	@Value("${jdbc.driver}") private String driverClass;

	@Value("${jdbc.url}") private String url;

	@Value("${jdbc.username}") private String username;

	@Value("${jdbc.password}") private String password;

	/**
	 * 数据源配置
	 *
	 * @return
	 */
	@Bean public DataSource getDataSource()
	{
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	/**
	 * 事务管理
	 *
	 * @return
	 */
	@Bean public DataSourceTransactionManager getTransactionManager()
	{
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(getDataSource());
		return dataSourceTransactionManager;
	}

	/**
	 * 获取sessionFactory
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean public SqlSessionFactory getSqlSessionFactory() throws Exception
	{
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		factoryBean.setDataSource(getDataSource());
		factoryBean.setConfigLocation(resolver.getResource(BASE_LOCATION));
		factoryBean.setMapperLocations(resolveMapperLocations());
		return factoryBean.getObject();
	}


	public Resource[] resolveMapperLocations()
	{
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		List<String> mapperLocations = new ArrayList<>();
		mapperLocations.add(MAPPER_LOCATION);
		mapperLocations.add(MAPPER_LOCATION_OTHER);
		List<Resource> resources = new ArrayList();
		if (!SetUtils.isNullList(mapperLocations))
		{
			for (String mapperLocation : mapperLocations)
			{
				try
				{
					Resource[] mappers = resourceResolver.getResources(mapperLocation);
					resources.addAll(Arrays.asList(mappers));
				}
				catch (IOException e)
				{
					DynaLogger.error("Get myBatis resources happened exception", e);
				}
			}
		}

		return resources.toArray(new Resource[resources.size()]);
	}

}
