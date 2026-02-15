package org.ecommerce.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SbECommerceProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbECommerceProjectApplication.class, args);
    }
}
