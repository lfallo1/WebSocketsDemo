package com.lancefallon.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RestAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RestAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    /**
     * used to establish an authentication mechanism by allowing AuthenticationProviders to be added easily:
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * configuration of web based security at a resource level, based on a selection match -
     * e.g. The example below restricts the URLs that start with /admin/ to users that have ADMIN role,
     * and declares that any other URLs need to be successfully authenticated.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = concurrentSessionStrategy();

        //set max of one session
//        http.sessionManagement()
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(true);

        /*
            - The "/shared" path needs to be specified as allowed because it is used when initializing the socket endpoint
            - all the websocket endpoints have their security defined in WebSocketSecurityConfig.java
        */
//		// @formatter:off
        http
                .authorizeRequests().antMatchers("/dist/**", "/bower/**", "/images/**", "/**/favicon.ico").permitAll() //vue
                .and().authorizeRequests().antMatchers("/", "/faq", "/invalidSession", "/api/config/**", "/api/channel/**", "/shared/**").permitAll() //routes
                .and().formLogin()
                .loginPage("/login").defaultSuccessUrl("/").permitAll() //login
                .and().authorizeRequests().anyRequest().authenticated() //require auth for other requests
                .and().logout() //logout handling
                .deleteCookies("remove")
                .invalidateHttpSession(true)
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));

        //auth failure handlling
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().successHandler(authenticationSuccessHandler);
        http.formLogin().failureHandler(authenticationFailureHandler);

//		// @formatter:on
    }

    /**
     * configuration settings that impact global security
     * (ignore resources, set debug mode, reject requests by implementing a custom firewall definition).
     * For example, the following method would cause any request that starts with /resources/ to be ignored
     * for authentication purposes.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {

    }

}