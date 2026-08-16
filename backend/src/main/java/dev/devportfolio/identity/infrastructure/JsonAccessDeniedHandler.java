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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Responde 403 em JSON (não o Whitelabel padrão do Spring Security) — cobre,
 * entre outros casos, falha de validação do token CSRF em rotas mutáveis.
 */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JsonAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String traceId = MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY);
        ErrorResponse body = new ErrorResponse(traceId, "Acesso negado.", List.of());
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
