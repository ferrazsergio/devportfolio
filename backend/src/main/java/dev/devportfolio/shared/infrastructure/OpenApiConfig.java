package dev.devportfolio.shared.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devPortfolioOpenApi() {
        return new OpenAPI().info(new Info().title("DevPortfolio API")
                .description(
                        "API do DevPortfolio — CMS open source de portfólio para desenvolvedores. "
                                + "Autenticação por sessão (cookie); use /api/v1/auth/login antes de chamar endpoints protegidos.")
                .version("v1")
                .license(new License().name("MIT")
                        .url("https://github.com/ferrazsergio/devportfolio/blob/main/LICENSE")));
    }
}
