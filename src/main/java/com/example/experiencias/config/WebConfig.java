package com.example.experiencias.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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

        // --- AuthInterceptor ---
        // Protege rutas que requieren sesión activa.
        // Se excluyen: auth (login/register), recursos estáticos públicos y la home.
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                    "/api/reservas/**",      // Reservas siempre requieren login
                   // "/api/users/**",         // Gestión de usuarios
                    "/experiencias/reservas.html"
                )
                .excludePathPatterns(
                    "/api/auth/**",          // Login, register, logout
                    "/api/experiencias/**",  // Catálogo público
                    "/api/categorias/**",    // Categorías públicas
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

        // --- RoleInterceptor ---
        // Solo deja pasar a ROLE_ADMIN.
        // Se aplica únicamente a rutas /admin/** y /api/admin/**
       // registry.addInterceptor(roleInterceptor)
          //      .addPathPatterns(
                  //  "/admin/**",
                   // "/api/admin/**"
              //  );
    }
}
