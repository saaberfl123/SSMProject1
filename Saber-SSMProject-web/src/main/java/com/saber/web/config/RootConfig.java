package com.saber.web.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@ComponentScan("com.saber.service")
@PropertySource("classpath:db.properties")//配置数据源路径
@EnableTransactionManagement//开启事务管理
@MapperScan("com.saber.dao")//扫描并注册dao的代理类 进入Spring
public class RootConfig
{
    @Autowired
    private Environment env;

    //配置数据源
    @Bean
    public DataSource dataSource()
    {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(env.getProperty("jdbc.driver"));
        ds.setUrl(env.getProperty("jdbc.url"));
        ds.setUsername(env.getProperty("jdbc.username"));
        ds.setPassword(env.getProperty("jdbc.password"));
        return ds;
    }

    //配置数据事务管理器
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource)//这里自动注入
    {
        return new DataSourceTransactionManager(dataSource);
    }

    //配置myBatis sqlSessionFactory
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
    {
        //工厂的工厂
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        //导入分页插件
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties props = new Properties();
        props.setProperty("helperDialect", "mysql");   // 方言
        props.setProperty("reasonable", "true");       // 页码合理化
        pageInterceptor.setProperties(props);
        sqlSessionFactoryBean.setPlugins(new PageInterceptor[]{pageInterceptor});
        return sqlSessionFactoryBean.getObject();
    }
}
