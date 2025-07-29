package gift.config;

import gift.interceptor.AuthenticationInterceptor;
import gift.login.LoggedInMemberArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final LoggedInMemberArgumentResolver loggedInMemberArgumentResolver;

    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor,
        LoggedInMemberArgumentResolver loggedInMemberArgumentResolver) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.loggedInMemberArgumentResolver = loggedInMemberArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns("/api/**", "/admin/**")
            .excludePathPatterns(
                "/css/**", "/error",
                "/api/members/register",
                "/api/members/login",
                "/members/register",
                "/members/login",
                "/oauth/kakao",
                "/oauth/kakao/callback"
            );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loggedInMemberArgumentResolver);
    }
}