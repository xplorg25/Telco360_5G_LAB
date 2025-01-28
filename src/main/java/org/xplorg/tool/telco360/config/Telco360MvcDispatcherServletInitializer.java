package org.xplorg.tool.telco360.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableWebMvc
@ComponentScan("org.xplorg.tool.telco360")

public class Telco360MvcDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer implements WebMvcConfigurer{
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getServletMappings() {
	//	return new String[] { "/telco360/*" };
		 return new String[] { "/" }; 
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
	return new Class[] { Telco360MvcDispatcherServletInitializer.class ,WebSocketConfig.class};
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
	Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
	//builder.serializationInclusion(JsonInclude.Include.NON_NULL);
	builder.featuresToEnable(SerializationFeature.WRAP_ROOT_VALUE); // enable wrapping for root elements 
	return builder;
	}

	@Bean(name="multipartResolver")
    public CommonsMultipartResolver getResolver() throws IOException{
     CommonsMultipartResolver resolver = new CommonsMultipartResolver();
     resolver.setMaxUploadSize(209715200);//0.5MB
     return resolver;
    }
}



	



