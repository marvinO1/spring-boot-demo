package hello;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;

import java.lang.invoke.MethodHandles;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.join;
import static java.util.Comparator.comparing;


public class EnvironmentInformationLogger implements ApplicationContextAware {


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final List<String> PROPERTY_SOURCE_CLASSES_TO_DUMP = Arrays.asList(
            "org.springframework.core.env.MapPropertySource",
            "org.springframework.core.env.PropertiesPropertySource",
            "org.springframework.core.env.SystemEnvironmentPropertySource"
    );


    private final ResourceLoader resourceLoader;

    public EnvironmentInformationLogger(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Environment environment = applicationContext.getEnvironment();
        LOGGER.info("-- environment information dump start --------------------------------------------------------------");
        LOGGER.info("application context environment is from type {}", environment.getClass().getName());
        LOGGER.info("active profiles: {}", join(", ", environment.getActiveProfiles()));
        LOGGER.info("default profiles: {}", join(", ", environment.getDefaultProfiles()));

        if (StandardEnvironment.class.isInstance(environment)) {
            StandardEnvironment standardEnvironment = StandardEnvironment.class.cast(environment);
            Map<String, Object> systemEnvironmentMap = standardEnvironment.getSystemEnvironment();

            LOGGER.info("-- system environment ------------------------------------------------------------------------------");
            logMap(systemEnvironmentMap);
            LOGGER.info("----------------------------------------------------------------------------------------------------");

            Map<String, Object> systemPropertiesMap = standardEnvironment.getSystemProperties();
            LOGGER.info("-- system properties ------------------------------------------------------------------------------");
            logMap(systemPropertiesMap);
            LOGGER.info("----------------------------------------------------------------------------------------------------");


            MutablePropertySources mutablePropertySources = standardEnvironment.getPropertySources();
            LOGGER.info("---- mutablePropertySources ----------------------------------------------------------------------");
            LOGGER.info("     found mutablePropertySources: {}", mutablePropertySources.toString());
            mutablePropertySources.forEach(EnvironmentInformationLogger::print);
            LOGGER.info("----------------------------------------------------------------------------------------------------");
        } else {
            LOGGER.info("found environment is not of type StandardEnvironment but of type: {}, skipping dumping environment", environment.getClass().getName());
        }
        LOGGER.info("-- environment information dump end ----------------------------------------------------------------");

        LOGGER.info("-- classpath dump -----------------------------------------------------------------");
        dumpClassPath(EnvironmentInformationLogger.class.getClassLoader());
        LOGGER.info("-----------------------------------------------------------------------------------");

    }

    private static void logMap(Map<String, Object> map) {
        map.entrySet()
                .stream()
                .sorted(comparing(Map.Entry::getKey))
                .forEach((e) -> LOGGER.info(e.toString()));
    }

    private static void print(PropertySource<?> propertySource) {
        LOGGER.info("---- propertySource dump start ----------------------------------------------------------");
        LOGGER.info("     propertySource class: " + propertySource.getClass().getName());
        LOGGER.info("     propertySource.name=" + propertySource.getName());

        if (PROPERTY_SOURCE_CLASSES_TO_DUMP.contains(propertySource.getClass().getName())) {
            LOGGER.info("     propertySource.source=" + propertySource.getSource());
        }
        LOGGER.info("-----------------------------------------------------------------------------------------");
    }

    private static void dumpClassPath(ClassLoader classLoader) {

        if (classLoader instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader)classLoader;
            LOGGER.info("loader={}, {}", ucl.getClass().getName(), Arrays.toString(ucl.getURLs()));
        } else
            LOGGER.info("(cannot display components as not a URLClassLoader)");

        if (classLoader.getParent() != null) {
            dumpClassPath(classLoader.getParent());
        }
    }
}
