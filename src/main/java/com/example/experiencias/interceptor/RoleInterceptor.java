package com.example.experiencias.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler)
			throws Exception {
/*
		HttpSession session = request.getSession(false);

		if (session == null) {
			if (request.getRequestURI().startsWith("/api/")) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				response.sendRedirect("/");
			}
			return false;
		}

		String role = (String) session.getAttribute("role");

		if (!"ROLE_ADMIN".equals(role)) {

			String path = request.getRequestURI();

			if (path.startsWith("/api/")) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			} else {
				response.sendRedirect("/");
			}

			return false;
		}
*/
		return true;
	}
}
