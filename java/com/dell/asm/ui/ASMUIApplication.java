package com.dell.asm.ui;

import javax.servlet.DispatcherType;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.dell.asm.ui.spring.DataSessionTimeoutFilter;
import com.dell.asm.ui.spring.NoEtagFilter;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ASMUIApplication extends SpringBootServletInitializer {

    @Bean
    public FilterRegistrationBean noEtagRegistrationBean() {

        FilterRegistrationBean bean = new FilterRegistrationBean(new NoEtagFilter());
        bean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        return bean;
    }

    @Bean
    public FilterRegistrationBean dataSessionTimeoutRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new DataSessionTimeoutFilter());
        return bean;
    }

    @Bean
    public FilterRegistrationBean characterEncodingRegistrationBean() {

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        FilterRegistrationBean bean = new FilterRegistrationBean(characterEncodingFilter);
        return bean;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ASMUIApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ASMUIApplication.class, args);
    }
}

