// src/main/java/com/example/backend/config/DataInitializer.java (o la ruta que prefieras)
package com.example.backend.config;

import com.example.backend.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;

    public DataInitializer(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void run(String... args) throws Exception {
        categoryService.createDefaultCategories();
    }
}
