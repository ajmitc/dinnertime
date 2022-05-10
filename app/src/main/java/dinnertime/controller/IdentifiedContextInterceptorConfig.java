package dinnertime.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author aaron.mitchell
 */
@Configuration
public class IdentifiedContextInterceptorConfig implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getInterceptor()).order(Ordered.LOWEST_PRECEDENCE);
    }

    @Bean
    public IdentifiedContextInterceptor getInterceptor() {
        return new IdentifiedContextInterceptor();
    }
}
