package org.goodstay.configuration;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
        "org.goodstay.service",
        "org.goodstay.repository",
        "org.goodstay.exception",
        "org.goodstay.security"
})
@Import({HibernatePersistenceConfiguration.class, PasswordConfig.class})
public class ApplicationConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
