package com.plantler.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<CrosFilter> crosFilter() {
		FilterRegistrationBean<CrosFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CrosFilter());
		registrationBean.addUrlPatterns("/*");		
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<TokenFilter> webFilter() {
		FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new TokenFilter());
		registrationBean.addUrlPatterns("/*");		
		return registrationBean;
	}
	


	  @Bean
	    public CharacterEncodingFilter characterEncodingFilter() {
	        CharacterEncodingFilter filter = new CharacterEncodingFilter();
	        filter.setEncoding("UTF-8");
	        filter.setForceEncoding(true);
	        return filter;
	    }
}
