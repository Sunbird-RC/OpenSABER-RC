package dev.sunbirdrc.registry.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.sunbirdrc.registry", "dev.sunbirdrc.pojos", "dev.sunbirdrc.keycloak", "dev.sunbirdrc.workflow", "dev.sunbirdrc.plugin"})
public class SunbirdRCApplication {
    private static ApplicationContext context;
    private static SpringApplication application = new SpringApplication(SunbirdRCApplication.class);

    public static void main(String[] args) {
        context = application.run(args);
    }

    /**
     * This method return non-web application context
     *
     * @return context
     */
    public static ApplicationContext getAppContext() {
        application.setWebApplicationType(WebApplicationType.NONE);
        context = application.run();
        return context;
    }

    @Value("${cors.allowedOrigin}")
    public String corsAllowedOrigin;

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(corsAllowedOrigin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PUT");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
