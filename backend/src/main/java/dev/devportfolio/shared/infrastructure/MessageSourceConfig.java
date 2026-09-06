package dev.devportfolio.shared.infrastructure;

import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Infraestrutura de i18n (ver ADR-006, seção "Consequências" — camada de tradução
 * prevista desde a decisão original, agora implementada). O idioma é resolvido pelo
 * header Accept-Language que o frontend envia (ver LocaleService/interceptor Angular),
 * sem cookie/sessão dedicada — cada requisição já carrega o idioma corrente da UI.
 */
@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(new Locale("pt"));
        // Sem isso, a resolução de "pt" pode cair pro locale padrão da JVM/container
        // (ex.: en_US) em vez do arquivo raiz messages.properties — daí termos
        // messages_pt.properties explícito também, sem depender do fallback pra root.
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    /** Faz o Bean Validation (@NotBlank etc.) resolver "{chave}" pelo MessageSource acima. */
    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("pt"));
        resolver.setSupportedLocales(List.of(new Locale("pt"), Locale.ENGLISH));
        return resolver;
    }
}
