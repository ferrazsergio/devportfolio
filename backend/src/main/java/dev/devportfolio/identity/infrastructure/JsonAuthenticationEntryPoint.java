package dev.devportfolio.identity.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.devportfolio.shared.infrastructure.TraceIdFilter;
import dev.devportfolio.shared.presentation.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Responde 401 em JSON (não o redirecionamento/HTML padrão do Spring Security)
 * quando uma rota autenticada é acessada sem sessão válida.
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String traceId = MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY);
        ErrorResponse body = new ErrorResponse(traceId, "Não autenticado.", List.of());
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
