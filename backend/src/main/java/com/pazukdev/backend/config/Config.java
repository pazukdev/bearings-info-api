package com.pazukdev.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Properties;

import static com.pazukdev.backend.util.Util.getMailSenderData;

/**
 * @author Siarhei Sviarkaltsau
 */
@Configuration
public class Config {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addViewControllers(final ViewControllerRegistry registry) {
                registry.addViewController("/login").setViewName("login");
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedMethods(CorsConfiguration.ALL)
                        .allowedOrigins("*")
                        .allowedHeaders("*");
            }

            @Override
            public void addResourceHandlers(final ResourceHandlerRegistry registry) {
                registry
                        .addResourceHandler("swagger-ui.html")
                        .addResourceLocations("classpath:/META-INF/resources/");

                registry
                        .addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");

                registry
                        .addResourceHandler("/**")
                        .addResourceLocations("file:backend/src/img/");
            }
        };

    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
//        mailSender.setHost("smtp.sendgrid.net");
        mailSender.setPort(587);
        mailSender.setUsername(getMailSenderData()[0]);
        mailSender.setPassword(getMailSenderData()[1]);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.channel", "plain");

        return mailSender;
    }

}
