package org.goodstay.configuration;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
        "org.goodstay.service",
        "org.goodstay.repository",
        "org.goodstay.exception"
})
@Import({HibernatePersistenceConfiguration.class, PasswordConfig.class})
public class ApplicationConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
