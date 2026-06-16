package com.boarhat.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(info = @Info(
        title = "Bank Account API",
        version = "1",
        description = "Manage bank and savings accounts: create accounts, deposit and withdraw money, "
                + "update overdraft authorization and consult balances and statements."))
class OpenApiConfiguration {
}
