package com.challenge.java.tomi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI transactionsOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Transactions API")
                        .description("Mendel challenge application for transactions")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .contact(new Contact()
                                .name("Tomas")
                                .url("https://www.linkedin.com/in/ttomasespinosa/")));
    }
}
