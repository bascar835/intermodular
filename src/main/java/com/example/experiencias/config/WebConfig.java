package com.example.experiencias.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.experiencias.interceptor.AuthInterceptor;
import com.example.experiencias.interceptor.RoleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final RoleInterceptor roleInterceptor;

    public WebConfig(AuthInterceptor authInterceptor,
                     RoleInterceptor roleInterceptor) {
        this.authInterceptor = authInterceptor;
        this.roleInterceptor = roleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                    "/api/reservas/**",
                    "/experiencias/reservas.html"
                )
                .excludePathPatterns(
                    "/api/auth/**",
                    "/api/experiencias/**",
                    "/api/categorias/**",
                    "/api/test",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js",
                    "/**/*.png",
                    "/**/*.jpg",
                    "/**/*.gif",
                    "/",
                    "/index.html"
                );

        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/admin/**", "/api/admin/**");
    }

    // Sirve los archivos subidos desde uploads/ como recursos estáticos en /uploads/**
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
