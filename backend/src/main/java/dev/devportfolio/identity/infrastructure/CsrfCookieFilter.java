package dev.devportfolio.identity.infrastructure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * O token CSRF do Spring Security é resolvido de forma "preguiçosa": o cookie
 * XSRF-TOKEN só é escrito na resposta se algo efetivamente ler o token. Este
 * filtro força essa leitura em toda requisição, garantindo que o cookie sempre
 * chegue à SPA (padrão recomendado pelo Spring Security para clientes Angular).
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}
