package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class InformationLoggerConfiguration {

    @Bean
    public EnvironmentInformationLogger environmentInformationLogger(ResourceLoader resourceLoader) {
        return new EnvironmentInformationLogger(resourceLoader);
    }
}
