package com.saber.web.config;

import com.saber.web.interceptor.AdminInterceptor;
import com.saber.web.interceptor.LoginInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan({"com.saber.web.controller","com.saber.web.advice","com.saber.web.interceptor"})
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String[] loginPath={"/order/**","/role/**","/product/**","/user/findAll","/user/simplyFindAll","/user/findPage"};
        String[] adminPath={"/order/**","/role/**","/product/**","/user/findAll","/user/simplyFindAll","/user/findPage"};

        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(loginPath);
        registry.addInterceptor(new AdminInterceptor()).addPathPatterns(adminPath);
    }
}
