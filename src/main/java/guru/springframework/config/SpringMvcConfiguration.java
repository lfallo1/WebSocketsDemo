package guru.springframework.config;

import guru.springframework.config.interceptors.LoginPageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import java.util.Locale;

@Configuration
public class SpringMvcConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public LoginPageInterceptor loginPageInterceptor() {
        return new LoginPageInterceptor();
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.US);
        return sessionLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(localeChangeInterceptor());
        interceptorRegistry.addInterceptor(loginPageInterceptor());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig
                = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }

    /**
     * <bean id="templateEngine" class="org.thymeleaf.spring4.SpringTemplateEngine">
     * ...
     * <property name="additionalDialects">
     * <set>
     * <!-- Note the package would change to 'springsecurity3' if you are using that version -->
     * <bean class="org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect"/>
     * </set>
     * </property>
     * ...
     * </bean>
     *
     * @return
     */

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

//    @Bean(name = "dataSource")
//    public DriverManagerDataSource dataSource() {
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//        driverManagerDataSource.setDriverClassName("driver");
//        driverManagerDataSource.setUrl("database_url");
//        driverManagerDataSource.setUsername("username");
//        driverManagerDataSource.setPassword("password");
//        return driverManagerDataSource;
//    }

}
